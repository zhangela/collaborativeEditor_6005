package View;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentListener;

import Controller.DocumentContentListener;

/**
 * Subtype of JPanel for viewing and editing a document. Displays a text
 * fieldand rename button.
 */
public class DocPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final int docNum;
    private JTextPane text;
    private String docName;
    private DocumentListener listener;

    /**
     * Creates a new DocPanel with a documentId, document name, a document pane,
     * a listener for the TextPane.
     * 
     * @param docNum
     *            documentID of the document represented in the Panel.
     * @param docName
     *            document name of the document represented in the panel
     * @param editor
     *            editor the DocPanel is in.
     */
    public DocPanel(final int docNum, String docName, final Editor editor) {
        this.docNum = docNum;
        this.docName = docName;
        this.text = new JTextPane();
        listener = new DocumentContentListener(docNum, editor);
        text.getDocument().addDocumentListener(listener);
        JScrollPane scrollPane = new JScrollPane(text);

        JButton changeName = new JButton();
        changeName.addActionListener(new ActionListener() {

            /**
             * sends a message to the server with the a rename request
             */
            @Override
            public void actionPerformed(ActionEvent ae) {
                String newName = JOptionPane.showInputDialog(text,
                        "Enter new name");
                while (newName == null || newName.contains("|") || newName.contains("~") || newName.equals("")){
                    if (newName == null) { //user hits CANCEL
                        return;
                    }else if (newName.equals("")) { //user did not enter a name
                        JOptionPane
                        .showMessageDialog(text,
                                "Empty name is not allowed.");
                    } else if (newName.contains("~") || newName.equals("|")) { //user enters ~/|
                        JOptionPane
                                .showMessageDialog(text,
                                        "Pipes \"|\" and tildas \"~\" cannot be used in document names.");
                    }
                    
                    newName = JOptionPane.showInputDialog(text,
                            "Enter new name");
                }
                editor.sendMessage(editor.createControlMessage("rename",
                        docNum, newName));
            }

        });
        changeName.setText("Rename");

        this.setPreferredSize(new Dimension(300, 400));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(scrollPane).addComponent(changeName));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(scrollPane).addComponent(changeName));

    }

    /**
     * gets docID of panel
     * 
     * @return docID of panel
     */
    public int getNum() {
        return docNum;
    }

    /**
     * gets TextPane of panel
     * 
     * @return testPane
     */
    public JTextPane getTextPane() {
        return this.text;
    }

    /**
     * gets name of doc in pane
     * 
     * @return name of the doc in the pane
     */
    public String getName() {
        return docName;
    }

    /**
     * sets name of the doc in the pane.
     * 
     * @param newName
     */
    public void setName(String newName) {
        this.docName = newName;
    }

    /**
     * paints the panel like a regular Jpanel
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * repaints the panel like a regular Jpanel
     */
    @Override
    public void repaint() {
        super.repaint();
    }

}
