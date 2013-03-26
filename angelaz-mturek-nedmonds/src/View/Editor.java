package View;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Controller.MessageHandlingThread;
import Model.ServerRequestDQ;
import Model.UserDQ;


/**
 * Thread Safety for the GUI:
 * 
 * The construction of the GUI allows for a relatively simple thread safety argument.
 * 
 * All interaction with the GUI happens within the EventDispatchThread. Every time the EventDispatchThread
 * is used, it is with an InvokeandWait which prevents any concurrency errors.
 * 
 * This simplicity is caused by the existence of the MessageHandlingThread which essentially
 * acts as a buffer between server and client. That is the entire functionality of the MHT.
 */


/**
 * Editor is the main GUI class. It contains a tabbedPane which contains the
 * documents and document loading panes.
 * 
 */
public class Editor extends JFrame {

    private static final long serialVersionUID = 1L;
    private UserDQ user;
    private Socket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageHandlingThread MHT;
    private int ignoreNext = 0;
    private JTabbedPane tabbedPane;
    private HashMap<Integer, DocPanel> docIDtoDocPanel;


    /**
     * Creates new Editor object with a tabbed pane, opens a message handling
     * thread. Opens an open document pane
     * 
     * @throws UnknownHostException
     * @throws IOException
     */
    public Editor() throws UnknownHostException, IOException {

        docIDtoDocPanel = new HashMap<Integer, DocPanel>();

        showGreetingDialog();

        out = new PrintWriter(getServerSocket().getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(getServerSocket()
                .getInputStream()));

        handleServerGreeting();

        tabbedPane = new JTabbedPane();

        MHT = new MessageHandlingThread(this, in);
        MHT.start();

        this.setTitle("Collaborative Editor");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            /**
             * sends a bye message to the server when the GUI is closed
             */
            public void windowClosing(WindowEvent evt) {

                out.println("BYE");
                System.exit(0);
                // close sockets
                // shut down background thread
            }
        });

        tabbedPane.add("Open/Create", new NewDocPanel(this));

        tabbedPane.addChangeListener(new ChangeListener() {
            /**
             * If the new selected tab is the last tab (ie the new doc pane)
             * then refresh the doc list.
             */
            public void stateChanged(ChangeEvent e) {

                if (tabbedPane.getSelectedIndex() == tabbedPane.getTabCount() - 1) {
                    sendMessage(createControlMessage("getdoclist", 0, ""));
                }
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);

        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addComponent(tabbedPane));

        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(
                tabbedPane));

    }

    /**
     * makes a close button on the tab.
     * 
     * @param i
     *            the index of the tab to add the button to.
     */
    public void initTabComponent(int i) {
        tabbedPane.setTabComponentAt(i,
                new ButtonTabComponent(tabbedPane, this));
    }

    /**
     * reads the first message from the server, which assigns a userID. shows an
     * error if the server doesn't respond properly
     * 
     * @throws IOException
     */
    private void handleServerGreeting() throws IOException {
        String helloMessage = in.readLine();
        String[] elements = helloMessage.split("\\|");
        if (!elements[0].equals("HELLO")) {
            JOptionPane
                    .showMessageDialog(null,
                            "Sorry, the server has encountered unexpected error. Please try again.");
            System.exit(0);
        } else {
            setUser(new UserDQ(elements[1]));
        }
    }

    /**
     * sends a message to the server.
     * 
     * @param message
     *            message to be sent to the server.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * parses server messages. If NOT a control message, passes the message to
     * the user and updates the TextPane. If a control message, applies the
     * update to the Editor.
     * 
     * @param line
     *            message to be parsed
     */
    public void handleLine(String line) {
        if (line.startsWith("CONTROL")) {
            String[] splitString = line.split("\\|");
            String reqType = splitString[1];

            if (reqType.equals("ERROR")) {
                JOptionPane.showMessageDialog(tabbedPane, splitString[2]);
            }

            else if (reqType.equals("DOCRENAMED")) {
                String[] broken = splitString[2].split("~");
                DocPanel docToRename = docIDtoDocPanel.get(Integer
                        .parseInt(broken[0]));
                int index = tabbedPane.indexOfComponent(docToRename);
                tabbedPane.setTitleAt(index, broken[1]);
                initTabComponent(index);
            }

            else if (reqType.equals("DOCLIST")) {

                // Send new info to new doc panel, send as list loading
                // label into list
                if (splitString.length < 3) {
                    tabbedPane.setComponentAt(tabbedPane.getTabCount() - 1,
                            new DocumentSelectionPanel(null, this));
                    tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1,
                            "Open/Create");
                } else {
                    String[] docNames = new String[splitString.length - 2];
                    for (int i = 2; i < splitString.length; i++) {
                        docNames[i - 2] = splitString[i];
                    }
                    DocumentSelectionPanel selectionPanel = new DocumentSelectionPanel(
                            docNames, this);
                    tabbedPane.setComponentAt(tabbedPane.getTabCount() - 1,
                            selectionPanel);
                    tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1,
                            "Open/Create");
                    tabbedPane.getComponent(tabbedPane.getTabCount() - 1)
                            .repaint();
                }

            }

            else if (reqType.equals("REQNEWPROCESSED")) {
                // create a new doc with the new name pass to method close +,
                // reopen
                // update userDQ with new doc, send a load message
                String[] splitDoc = splitString[2].split("~");
                getUser().addDocument(Integer.parseInt(splitDoc[0]));
                DocPanel newDocWindow = new DocPanel(
                        Integer.parseInt(splitDoc[0]), splitDoc[1], this);
                this.docIDtoDocPanel.put(newDocWindow.getNum(), newDocWindow);
                tabbedPane.setComponentAt(tabbedPane.getTabCount() - 1,
                        newDocWindow);
                tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1,
                        newDocWindow.getName());
                tabbedPane.add("Open/Create", new NewDocPanel(this));
                initTabComponent(tabbedPane.getTabCount() - 2);
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 2);
                this.sendMessage(createControlMessage("load",
                        Integer.parseInt(splitDoc[0]), splitDoc[1]));

            }

        } else {
            this.getUser().pushRequest(line);
            String[] splitString = line.split("\\|");

            this.updateView(splitString[2]);
        }
    }

    /**
     * updates the textPane of the panel corresponding to the give docID to add
     * the new updates from the server.
     * 
     * @param docID
     *            docID of the document to be updated.
     */
    public void updateView(String docID) {
        synchronized (this) {
            this.ignoreNext = 2;

            int docId = Integer.parseInt(docID);
            DocPanel panelToUpdate = docIDtoDocPanel.get(docId);
            JTextPane tempPane = panelToUpdate.getTextPane();

            ServerRequestDQ newSelection = null;

            // insert message
            if (tempPane.getSelectionStart() == tempPane.getSelectionEnd()) {
                String req = ("dummyUser" + "|" + 24 + "|" + docId + "|"
                        + "INSERT" + "|" + tempPane.getCaretPosition() + "|"
                        + 0 + "|" + "filleer" + "|" + "0");
                newSelection = new ServerRequestDQ(user.updateSelection(req));
            }

            // delete message
            else {
                String req = ("dummyUser" + "|" + 24 + "|" + docId + "|"
                        + "DELETE" + "|" + tempPane.getSelectionStart() + "|"
                        + tempPane.getSelectionEnd() + "|" + "filler" + "|" + "0");
                newSelection = new ServerRequestDQ(user.updateSelection(req));
            }
            String content = user.getView(docId).replace("~", "\n");
            if (content.equals("")) {
                ignoreNext = 0;
            }
            panelToUpdate.getTextPane().setText(content);

            if (newSelection.getAction().equals("INSERT")) {
                tempPane.setCaretPosition(newSelection.getBeginning());
            } else {
                tempPane.setSelectionStart(newSelection.getBeginning());
                tempPane.setSelectionEnd(newSelection.getEnd());
            }
        }
    }

    /**
     * Create a new message from the given parameters that can be understood by
     * the server.
     * 
     * @param messageType
     *            The type of request to be sent
     * @param docID
     *            docID of the doc to be edited.
     * @param docName
     *            document name of the doc to be changed or added
     * @return String with correctly formatted server message
     */
    public String createControlMessage(String messageType, int docID,
            String docName) {
        String prefix = "CONTROL|" + getUser().getUserID();
        if (docName.equals("")) {
            docName = "document";
        }
        if (messageType.equals("requestNew")) {
            return prefix + "|REQUESTNEW|" + docName;
        } else if (messageType.equals("getdoclist")) {
            return prefix + "|GETDOCLIST";
        } else if (messageType.equals("close")) {
            return prefix + "|CLOSE|" + docID;
        } else if (messageType.equals("rename")) {
            return prefix + "|RENAME|" + docID + "~" + docName;
        } else if (messageType.equals("load")) {
            return prefix + "|LOAD|" + docID;
        }
        return null;
    }

    /**
     * Runs the editor. Sets the dimensions etc. If server cannot be reached or
     * sends a bad message, creates a pop up to tell the user there was an
     * error.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Editor main;
                    main = new Editor();
                    main.setMinimumSize(new Dimension(800, 500));
                    main.setMaximumSize(new Dimension(800, 500));
                    main.pack();
                    main.setVisible(true);
                } catch (UnknownHostException e) {
                    JOptionPane
                            .showMessageDialog(null,
                                    "Sorry, we couldn't connect to the server. Please try again.");

                } catch (IOException e) {
                    JOptionPane
                            .showMessageDialog(null,
                                    "Sorry, we were unable to communicate with the server. Please try again.");
                }
            }
        });

    }

    /**
     * returns the user object associated with the GUI.
     * 
     * @return user object associated with GUI
     */
    public UserDQ getUser() {
        return user;
    }

    /**
     * sets user object associated with GUI
     * 
     * @param user
     *            user object to associate with GUI
     */
    public void setUser(UserDQ user) {
        this.user = user;
    }

    /**
     * gets the socket associated with the GUI
     * 
     * @return socket associated with the GUI
     */
    public Socket getServerSocket() {
        return serverSocket;
    }

    /**
     * sets the serverSocket associated with the GUI
     * 
     * @param serverSocket
     *            associated with GUI
     */
    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Shows the greeting dialog and prompts the user for the server address and
     * port number. Handles user input, and connects to server if input is
     * correct.
     */
    public void showGreetingDialog() {
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
        myPanel.add(new JLabel("Server Address:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Port Number:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Collaborative Editor", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                setServerSocket(new Socket(xField.getText(),
                        Integer.parseInt(yField.getText())));
            } catch (NumberFormatException e) {
                JOptionPane
                        .showMessageDialog(null,
                                "Connection failed. Please double check your server address and port number.");
                System.exit(0);
            } catch (UnknownHostException e) {
                JOptionPane
                        .showMessageDialog(null,
                                "Connection failed. Please double check your server address and port number.");
                System.exit(0);
            } catch (IOException e) {
                JOptionPane
                        .showMessageDialog(null,
                                "Connection failed. Please double check your server address and port number.");
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Getter for the ignoreNext variable.
     * @return ignoreNext
     */
    public int getIgnoreNext() {
        return ignoreNext;
    }

    /**
     * Setter for the ignoreNext variable
     * @param ignoreNext
     */
    public void setIgnoreNext(int ignoreNext) {
        this.ignoreNext = ignoreNext;
    }

    /**
     * Getter for the tabbedPane
     * @return tabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Setter for the tabbedPane
     * @param tabbedPane
     */
    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    /**
     * Getter for the hash map from docID to the panel that holds the document
     * @return docIDtoDocPanel
     */
    public HashMap<Integer, DocPanel> getDocIDtoDocPanel() {
        return docIDtoDocPanel;
    }

    /**
     * Setter for the above mentioned hashmap
     * @param docIDtoDocPanel
     */
    public void setDocIDtoDocPanel(HashMap<Integer, DocPanel> docIDtoDocPanel) {
        this.docIDtoDocPanel = docIDtoDocPanel;
    }
}
