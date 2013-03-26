package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The SEditThread class represents a connection to an individual client. It
 * listens to messages from users and redirects them to the server queue. The
 * server also uses its sendMessage() method to inform users about updates made
 * by other users.
 * 
 */
public class SEditThread extends Thread {

    private final Socket socket;
    private final SEditServer server;
    private final String userID;

    private final BufferedReader in;
    private final PrintWriter out;

    /**
     * Creates a new SEditThread object to communicate with a client on the
     * specified socket
     * 
     * @param socket
     *            - socket on which the client is communicating
     * @param server
     *            - the server that spawned this thread
     * @param userID
     *            - the ID of the user whose communication this thread is
     *            managing
     * @throws IOException
     *             if IO streams can't be opened on the socket
     */
    public SEditThread(Socket socket, SEditServer server, String userID)
            throws IOException {
        super("SEditThread");
        this.socket = socket;
        this.server = server;

        this.userID = userID;

        this.in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Returns the ID of this user
     * 
     * @return String - userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Method to be run when this thread is created
     */
    public void run() {
        try {
            handleConnection(socket);
        } catch (IOException e) {
            System.out.println("Problem closing the IO streams");
        }
    }

    /**
     * Sends the specified message to the user
     * 
     * @param message
     *            to be sent
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Listens to incoming messages from the client and redirects them to their
     * destination
     * 
     * @param socket
     *            on which the thread is listening
     * @throws IOException
     *             if there is a communication error
     */
    private void handleConnection(Socket socket) throws IOException {

        try {
            // Send hello message:
            /* HELLO|USER_ID */
            out.println("HELLO|" + userID);

            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                if (line != null) {
                    if (line.equals("BYE")) {
                        System.out.println("Disconnecting user");
                        server.removeUser(this);
                        break;
                    }
                }

                System.out.println("Incoming message from " + userID + ": "
                        + line);

                // only one thread can send a request at a time, others
                // have to wait
                synchronized (server) {
                    server.handleRequest(line);
                }
            }
        } catch (IOException e) {
            // Ignore IOExceptions but close streams
        } finally {
            in.close();
            out.close();
        }

    }
}
