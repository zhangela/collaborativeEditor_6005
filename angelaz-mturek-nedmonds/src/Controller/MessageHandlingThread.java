package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import View.Editor;

/**
 * The Message Handling Thread's purpose is to listen for messages from the
 * server and pass them into the editor's message handler. It uses invoke and
 * wait to prevent concurrency issues. There is just one MessageHandlingThread
 * per editor, and it shares a BufferedReader with the editor.
 * 
 * @author Niki
 * 
 */
public class MessageHandlingThread extends Thread {

    private Editor editor;
    private BufferedReader in;

    /**
     * Constructor for the MessageHandlingThread.
     * 
     * @param editor
     *            The editor the thread belongs to and will pass server messages
     *            to.
     * @param in
     *            The buffered reader opened by its editor.
     */
    public MessageHandlingThread(Editor editor, BufferedReader in) {
        this.editor = editor;
        this.in = in;
    }

    /**
     * When run, thread attempts to handle the connection.
     */
    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
        }
    }

    /**
     * Handles the incoming messages from the server. Reads in lines, closes the
     * reader on a "BYE" message, and passes all other messages to another
     * method to be sent to the editor.
     * 
     * @throws IOException
     *             When BufferedReader causes an error in the input.
     */
    public void handleConnection() throws IOException {

        try {
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                if (line != null && line.equals("BYE")) {
                    break;
                }
                System.out.println(line);
                this.sendToEditor(line);
            }
        } finally {
            in.close();
        }

    }

    /**
     * Passes the passed in message to the editor's handleLine method. Uses
     * invoke and wait and a runnable to ensure that only one message is sent at
     * a time.
     * 
     * @param line
     *            message to be passed to the editor's parser.
     */
    public void sendToEditor(final String line) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    editor.handleLine(line);
                }
            });
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
        }

    }

}
