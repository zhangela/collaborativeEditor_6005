package Controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import View.DocPanel;
import View.Editor;

/**
 * DocumentContentListener is a custom Document listener that waits for updates
 * in the editor and creates the messages to be sent to the server.
 * 
 */

public class DocumentContentListener implements DocumentListener {

    final int docNum;
    final Editor editor;

    /**
     * Creates new DocumentContentListener on passed in document 
     * 
     * @param docNum document number of document listener listens to
     * @param editor to which the listener belongs
     */
    public DocumentContentListener(int docNum, Editor editor) {
        this.docNum = docNum;
        this.editor = editor;
    }

    /**
     * wrapper method for the update method for inserts
     */
    public void insertUpdate(DocumentEvent e) {
        try {
            update(e, EditType.INSERT);
        } catch (BadLocationException e1) {
           // e1.printStackTrace();
        }
    }

    /**
     * wrapper method for the update method for deletes
     */
    public void removeUpdate(DocumentEvent e) {
        try {
            update(e, EditType.DELETE);
        } catch (BadLocationException e1) {
           // e1.printStackTrace();
        }
    }

    /**
     * superclass method that we have to add.
     */
    public void changedUpdate(DocumentEvent e) {
        // Plain text components don't fire these events.
    }

    /**
     * update method for handling user inserts and deletes in the Editor to the
     * document. Gets the correct locations and sends the appropriate messages
     * to the server.
     * 
     * @param e
     * @param action
     * @throws BadLocationException
     */
    public void update(DocumentEvent e, EditType action)
            throws BadLocationException {
        synchronized (editor) {
            if (editor.getIgnoreNext() > 0) {
                editor.setIgnoreNext(editor.getIgnoreNext() - 1);
                return;
            }

            Document doc = (Document) e.getDocument();
            int offset = e.getOffset();
            int length = e.getLength();

            if (action == EditType.INSERT) {

                String content = doc.getText(offset, length).replace("\n", "~");
                if (content.contains("|")) {
                    JOptionPane
                            .showMessageDialog(
                                    editor,
                                    "Hi Katrina! We were hoping you won't see this... but pipe usage is HIGHLY discouraged.");

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {

                            DocPanel pan = editor.getDocIDtoDocPanel().get(
                                    docNum);
                            pan.getTextPane().setCaretPosition(0);
                            editor.updateView(docNum + "");
                        }
                    });
                    return;
                }
                editor.getUser().createRequest(
                        docNum,
                        "" + action.toString() + "|" + (offset) + "|"
                                + (offset + length) + "|" + content);
            } else {
                editor.getUser().createRequest(
                        docNum,
                        "" + action.toString() + "|" + offset + "|"
                                + (offset + length) + "|" + "wooo");
            }
            editor.sendMessage(editor.getUser().pullRequest());
        }
    }

    /**
     * enum for the 2 edit types.
     *
     */
    public static enum EditType {
        INSERT, DELETE,
    }

}
