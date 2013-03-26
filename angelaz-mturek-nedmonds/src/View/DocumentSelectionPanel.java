package View;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 
 * DocumentSelectionPanel is a class to represent the document selection menu.
 * It allows the user to either load a document from the server or create a new
 * document.
 * 
 */

public class DocumentSelectionPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static JList docsList;
    private JButton createButton;
    private JButton openButton;
    private JLabel openLabel;
    private JLabel createLabel;
    private JLabel orLabel;
    private JTextField docNameTextField;
    private JScrollPane scrollPane;

    /**
     * creates a new DocumentSelectionPanel. Will display the list of document
     * from the server that can be selected on one side (or a message that there
     * are no documents on the server). On the other side there will be a text
     * field to create a new document with the given name (or a variation of the
     * name if its already taken, ie. Niki-3 is Niki is already taken).
     * 
     * @param list
     *            list of documents on the server
     * @param editor
     *            the editor this selectionPanel is in.
     */
    public DocumentSelectionPanel(String[] list, final Editor editor) {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        openLabel = new JLabel("Open Existing Document");
        openLabel.setFont(new Font(openLabel.getFont().getFamily(), Font.BOLD,
                openLabel.getFont().getSize()));

        createLabel = new JLabel("Create New Document");
        createLabel.setFont(new Font(createLabel.getFont().getFamily(),
                Font.BOLD, createLabel.getFont().getSize()));

        ImageIcon icon = new ImageIcon("res/orange-or.png", "or-icon");
        orLabel = new JLabel(icon, SwingConstants.CENTER);

        if (list != null) {
            ArrayList<DocumentIDsAndNames> newList = new ArrayList<DocumentIDsAndNames>();
            for (String item : list) {
                newList.add(new DocumentIDsAndNames(item));
            }
            docsList = new JList(newList.toArray());
        } else {
            String[] blank = { "There are no documents on the server." };
            docsList = new JList(blank);
        }

        docsList.setVisible(true);
        docsList.setMinimumSize(new Dimension(0, 200));

        scrollPane = new JScrollPane(docsList);
        docNameTextField = new HintTextField("Enter Document Name");
        docNameTextField.addActionListener(new ActionListener() {

            /**
             * When enter is hit from the new document text field send a
             * requestNew message to the server
             */
            public void actionPerformed(ActionEvent ae) {
                String newName = docNameTextField.getText();
                docNameTextField.setText("");
                if(newName.contains("|") || newName.contains("~")){
                    JOptionPane.showMessageDialog(editor.getTabbedPane(), "pipes and tildas cannot be used in document names");
                    return;
                }
                editor.sendMessage(editor.createControlMessage("requestNew",
                        -1, newName));
            }
        });

        createButton = new JButton();
        createButton.setText("Create");
        createButton.addActionListener(new ActionListener() {
            /**
             * sends a message to the server indicating that a new document has
             * been requested.
             */
            public void actionPerformed(ActionEvent ae) {
                String newName = docNameTextField.getText();
                docNameTextField.setText("");
                if (newName.equals("Enter Document Name") || newName.equals("")) {
                    newName = "New Document";
                }
                editor.sendMessage(editor.createControlMessage("requestNew",
                        -1, newName));

            }
        });

        openButton = new JButton();
        openButton.setText("Open");
        openButton.addActionListener(new ActionListener() {
            /**
             * When a document from the server is selected and the open button
             * is pressed, opens that document in the current tab and opens a
             * new "NewDocPanel" tab as well. Sends message to the server about
             * the opened document. Refuses to open the document if it is
             * already open in the current editor.
             * 
             */
            public void actionPerformed(ActionEvent ae) {
                openDocument(editor);

            }
        });

        docsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openDocument(editor);
                }
            }
        });

        JLabel sep1 = new JLabel(new ImageIcon("res/divider.png"));
        JLabel sep2 = new JLabel(new ImageIcon("res/divider.png"));

        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING)
                                .addComponent(openLabel)
                                .addComponent(scrollPane, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(openButton))

                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING)
                                .addComponent(sep1).addComponent(orLabel)
                                .addComponent(sep2))

                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.LEADING)
                                .addComponent(createLabel)
                                .addComponent(docNameTextField, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(createButton)));

        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(openLabel)
                                .addComponent(scrollPane, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(openButton))

                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(sep1, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(orLabel, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addComponent(sep2, 0,
                                        GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE))

                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(createLabel)
                                .addComponent(docNameTextField,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(createButton)));

    }

    /**
     * paints the panel as it would a JPanle
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * repaints the panel as it would a JPanel
     */
    @Override
    public void repaint() {
        super.repaint();
    }

    
    /**
     * Opens a new panel with the selected value from the list if it is not 
     * already open on the client. Otherwise tells the user that the document
     * is already open. Sets focus on the new Pane. sends Load request to the 
     * server for the newly opened document.
     * @param editor editor to which the components belong.
     */
    private void openDocument(Editor editor) {

        if (docsList.getSelectedValue().equals(
                "There are no documents on the server.")) {
            return;
        }
        int num = ((DocumentIDsAndNames) docsList.getSelectedValue()).getNum();
        String name = docsList.getSelectedValue().toString();

        if (editor.getDocIDtoDocPanel().containsKey(num)) {
            JOptionPane.showMessageDialog(editor.getTabbedPane(),
                    "That document is already open!");
            return;
        }

        editor.getUser().addDocument(num);
        DocPanel newDoc = new DocPanel(num, name, editor);
        editor.getTabbedPane().add(name, newDoc);
        editor.getDocIDtoDocPanel().put(num, newDoc);
        editor.sendMessage(editor.createControlMessage("load", num, name));
        editor.getTabbedPane().remove(editor.getTabbedPane().getTabCount() - 2);
        editor.getTabbedPane().add("+", new NewDocPanel(editor));
        editor.initTabComponent(editor.getTabbedPane().getTabCount() - 2);
        editor.getTabbedPane().setSelectedIndex(editor.getTabbedPane().getTabCount() - 2);
    }

    /**
     * Wrapper class for displaying elements in the JList.
     * 
     */
    class DocumentIDsAndNames extends Object {
        private final int num;
        private final String name;

        public DocumentIDsAndNames(String ob) {
            String[] broken = ob.split("~");
            this.num = Integer.parseInt(broken[0]);
            this.name = broken[1];
        }

        /**
         * Getter for the docID
         * @return num
         */
        public int getNum() {
            return this.num;
        }

        /**
         * toString() method is overriden to diplay JList correctly
         */
        @Override
        public String toString() {
            return name;
        }
    }
}
