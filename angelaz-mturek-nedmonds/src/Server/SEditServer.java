package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import Model.ServerRequestDQ;

/**
 * The SEditServer class is the main entry point of the server side of the
 * application.
 * 
 * It sets up the server, listens for incoming client connections, and manages
 * the client-document communication
 * 
 * Thread safety argument for the server: To prevent race conditions and other
 * concurrency-related bugs, we decided to implement an overall lock on the
 * SEditServer object which might limit concurrency to an extent but this should
 * not produce visible delays considering the number of users we're expecting to
 * have connected to the server at the same time for this project.
 * 
 * All methods in SEditServer are synchronized with the exception of serve(),
 * which only locks on the server when it's performing an action, not when it's
 * waiting for a new user.
 * 
 * Since we're intentionally exposing the rep to SEditDocument, we also need to
 * make sure that all access from there is thread-safe. This is satisfied by
 * design since none of the client threads access any of the SEditDocuments
 * directly but they need to pass their messages through synchronized methods on
 * the SEditServer -> all SEditDocument methods must therefore run on the main 
 * server thread.
 * 
 * Methods in the SEditThread class can get either get called from a SEditThread thread 
 * or from the server thread. The data in SEditThread is definitely thread safe 
 * since all the access (mainly to socket/the out object, to which the server is printing) 
 * is read only and all the fields are declared final.
 * 
 * @author Michael Turek
 */
public class SEditServer {
    private final ServerSocket serverSocket;
    protected final Map<String, SEditThread> users;
    private int userIDCounter;
    private final Map<Integer, SEditDocument> documents;
    private int documentIDCounter;
    // a flag signifying whether shutdown has been requested
    private boolean die;

    /**
     * Creates a new SEditServer object listening for connections on the
     * specified port
     * 
     * @param port
     *            to listen on
     * @throws IOException
     *             if socket cannot be opened properly
     */
    
    public SEditServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.users = new HashMap<String, SEditThread>();
        this.userIDCounter = 0;

        this.documents = new HashMap<Integer, SEditDocument>();
        this.documentIDCounter = 0;

        this.die = false;

        System.out.println("Server created on port " + port);
    }

    /**
     * Reacts appropriately to an incoming request: 
     * Incoming CONTROL requests: 
     * - CONTROL|<userID>|<requestType>|<message dependent tail> 
     * --- GETDOCLIST, tail: empty 
     * --- REQUESTNEW, tail: suggested document name 
     * --- RENAME, tail: documentID~suggested name 
     * --- LOAD, tail: documentID 
     * --- CLOSE, tail: documentID 
     * 
     * Outgoing CONTROL messages: 
     * - CONTROL|<messageType>|<message dependent tail> 
     * --- DOCLIST, tail: (|documentID~documentName)* 
     * --- REQNEWPROCESSED, tail: |documentID~documentName (behavior specified in the design document)
     * 
     * RequestServerDQ requests are passed on to their respective target documents
     * 
     * @param input
     *            - request to be processed
     */
    public synchronized void handleRequest(String input) {
        if (input.startsWith("CONTROL")) {
            String[] elements = input.split("\\|");

            String userID = elements[1];
            String request = elements[2];

            if (request.equals("GETDOCLIST")) {

                String message = getDocListMessage();
                users.get(userID).sendMessage(message);

            } else if (request.equals("REQUESTNEW")) {
                String suggestedName = elements[3];
                int newDocumentID = documentIDCounter;
                documentIDCounter++;

                // Make sure there are no duplicate names
                for (SEditDocument document : documents.values()) {
                    if (document.getDocumentName().equals(suggestedName)) {
                        suggestedName += ("-" + newDocumentID);
                    }
                }

                SEditDocument newDocument = new SEditDocument(newDocumentID,
                        suggestedName);
                documents.put(newDocumentID, newDocument);

                String message = "CONTROL|REQNEWPROCESSED";
                message += ("|" + newDocument.getDocumentID() + "~" + newDocument
                        .getDocumentName());

                // Send REQNEWPROCESSES to the user that requested it
                users.get(userID).sendMessage(message);
                // and distribute DOCLIST to everyone
                distributeMessage(getDocListMessage());

                // The user still needs to load the document and is NOT
                // subscribed automatically

            } else if (request.equals("LOAD")) {
                int documentID = Integer.valueOf(elements[3]);

                documents.get(documentID).subscribeUser(users.get(userID));

            } else if (request.equals("CLOSE")) {
                int documentID = Integer.valueOf(elements[3]);

                documents.get(documentID).unsubscribeUser(users.get(userID));
            } else if (request.equals("RENAME")) {
                String[] content = elements[3].split("~");
                int documentID = Integer.valueOf(content[0]);
                String suggestedName = content[1];

                // Make sure there are no duplicate names
                for (SEditDocument document : documents.values()) {
                    if (document.getDocumentName().equals(suggestedName)) {
                        users.get(userID).sendMessage(
                                "CONTROL|ERROR|Name already in use");
                        return;
                    }
                }

                documents.get(documentID).rename(suggestedName);
                // also distribute DOCLIST to everyone
                distributeMessage(getDocListMessage());
            } else {
                // Ignore invalid CONTROL messages
            }

        } else {
            // Forward ServerRequestDQ messages to the document
            // they're related to

            ServerRequestDQ request = new ServerRequestDQ(input);
            int targetDocumentID = request.getDocumentID();

            documents.get(targetDocumentID).processRequest(request.toString());
        }
    }

    /**
     * Sends a message to all the users connected to the server
     * 
     * @param message
     *            to be sent to the users
     */
    private synchronized void distributeMessage(String message) {
        System.out.println("Server distributing message to connected clients: "
                + message);

        for (SEditThread user : users.values()) {
            user.sendMessage(message);
        }
    }

    /**
     * Generates a DOCLIST message to be sent to the users
     * 
     * @return the DOCLIST message
     */
    private synchronized String getDocListMessage() {
        String message = "CONTROL|DOCLIST";

        for (SEditDocument document : documents.values()) {
            message += ("|" + document.getDocumentID() + "~" + document
                    .getDocumentName());
        }

        return message;
    }

    /**
     * Unsubscribes the user from all the documents he is registered for and
     * removes him from the server
     * 
     * @param user
     *            to be removed
     */
    public synchronized void removeUser(SEditThread user) {
        for (SEditDocument document : documents.values()) {
            document.unsubscribeUser(user);
        }

        users.remove(user.getUserID());
    }

    /**
     * Listens at the serverSocket for incoming connections and creates a
     * SEditThread for them when it hears one
     */
    public void serve() {
        while (true) {
            try {
                String newUserID;
                synchronized (this) {
                    newUserID = "User" + userIDCounter;
                    userIDCounter++;
                }

                // Wait until a new user attempts to connect
                SEditThread user = new SEditThread(serverSocket.accept(), this,
                        newUserID);
                System.out.println("New client connection with userID: "
                        + newUserID);

                synchronized (this) {
                    users.put(newUserID, user);
                    user.start();
                }
            } catch (IOException e) {
                synchronized (this) {
                    if (die == true) {
                        // System.out.println("Server in process of shutting down");
                    } else {
                        System.out.println("Problem connecting to new user!");
                    }
                }
            }
        }
    }

    /**
     * Closes the serverSocket and shuts down the server
     * 
     * @throws a
     *             RuntimeException in order to kill the blocking
     *             serverSocket.accept() method call
     */
    public synchronized void kill() {
        System.out.println("Killing the server!");

        this.die = true;

        try {
            // Closing the socket while listening on it
            // produces an IOException for the accept() call
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket!");
        }

        System.exit(0);
    }

    /**
     * The main method that creates and launches the server
     * 
     * @param args
     *            - not used
     */
    public static void main(String[] args) {
        try {
            String portNum = JOptionPane
                    .showInputDialog("To start the server, please input a port number.");
            
            if (portNum.equals("")) {
                JOptionPane
                .showMessageDialog(null,
                        "Server is not started. Please enter a port number.");
                System.out.println("Server initialization failure!");
                System.exit(0);
            } else {
                int portNumber = Integer.parseInt(portNum);
                SEditServer server = new SEditServer(portNumber);
                server.serve();
            }
        } catch (IOException e) {
            JOptionPane
            .showMessageDialog(null,
                    "Server is not started. Please try entering a different port number.");
            System.out.println("Server initialization failure!");
            System.exit(0);
        }

    }

}
