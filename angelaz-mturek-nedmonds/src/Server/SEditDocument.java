package Server;

import java.util.ArrayList;
import java.util.List;

import Model.ServerRequestDQ;

/**
 * SEditDocument represents the server version of a document. It is recognized
 * by an immutable documentID and a changeable documentName.
 * 
 * The document itself is stored as a sequence of changes to the initial empty
 * document.
 * 
 */

public class SEditDocument {
    private final int documentID;
    private String documentName;
    private final List<ServerRequestDQ> serverHistory;

    private final List<SEditThread> subscribedUsers;

    /**
     * Creates a new SEditDocument with the given ID and Name
     * @param documentID
     * @param documentName
     */
    public SEditDocument(int documentID, String documentName) {
        this.documentID = documentID;
        this.documentName = documentName;

        this.serverHistory = new ArrayList<ServerRequestDQ>();
        serverHistory.add(new ServerRequestDQ("|0|0|INSERT|0|0||0")); // empty
                                                                      // event
                                                                      // leading
                                                                      // to
                                                                      // version
                                                                      // 0

        this.subscribedUsers = new ArrayList<SEditThread>();
    }

    /**
     * Processes a request sent in by a user, makes it match with current server
     * indices and distributes it to the subscribed users
     * 
     * @param input
     *            - a ServerRequestDQ in its text form as received from a client
     */
    public void processRequest(String input) {
        ServerRequestDQ request = new ServerRequestDQ(input);
        int userVersion = request.getVersionID();

        for (int i = userVersion + 1; i < serverHistory.size(); i++) {
            ServerRequestDQ serverEvent = serverHistory.get(i);

            // Don't apply one's own updates, they're applied implicitly
            if (!request.getUserID().equals(serverEvent.getUserID())) {
                request.applyUpdate(serverEvent);
            }
        }

        // The versionID of an event on the server is its index in the server
        // history
        ServerRequestDQ inclusionCandidate = new ServerRequestDQ(request,
                serverHistory.size());
        serverHistory.add(inclusionCandidate);

        System.out.println("Current serverHistory for Document " + documentID
                + ": " + serverHistory.toString());

        distributeMessage(inclusionCandidate.toString());
    }

    /**
     * Registers a user with this document so that he received all future
     * updates related to it. A user cannot be added multiple times.
     * 
     * @param a
     *            user to be subscribed
     */
    public void subscribeUser(SEditThread user) {
        if (!subscribedUsers.contains(user)) {
            subscribedUsers.add(user);
        }

        // Update the user to the current version
        System.out.println("Document " + documentID + " Sending "
                + (serverHistory.size() - 1) + " requests to a new user");

        for (int i = 1; i < serverHistory.size(); i++) {

            user.sendMessage(anonymizeMessage(serverHistory.get(i).toString()));
        }
    }

    /**
     * Removes the user name from a message to make sure loading is processed
     * properly
     * 
     * @param message
     *            to be anonymized
     * @return String - anonymized message
     */
    private String anonymizeMessage(String message) {
        ServerRequestDQ request = new ServerRequestDQ(message);
        request.setUserID("LoadUser");

        return request.toString();
    }

    /**
     * Renames this document and lets all subscribed user know
     * 
     * @param newName
     *            - the new name to be assigned to this document
     */
    public void rename(String newName) {
        this.documentName = newName;
        distributeMessage("CONTROL|DOCRENAMED|" + documentID + "~" + newName);
    }

    /**
     * Removes a user from the list of subscribed users or does nothing if he's
     * not in it
     * 
     * @param user
     *            to be removed
     */
    public void unsubscribeUser(SEditThread user) {
        if (subscribedUsers.contains(user)) {
            subscribedUsers.remove(user);
        }
    }

    /**
     * Sends a message to all the users registered with this document
     * 
     * @param message
     *            to be sent to the users
     */
    public void distributeMessage(String message) {
        System.out.println("Document " + documentID
                + " distributing message to connected clients: " + message);

        for (SEditThread userThread : subscribedUsers) {
            userThread.sendMessage(message);
        }
    }

    /**
     * Getter methods for private fields:
     */
    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }
}
