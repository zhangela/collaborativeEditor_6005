package View;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Essentially a placeholder JPanel while the documents load from the server.
 * Tells the user that the document list is loading, and the server that the
 * document list should be sent. Usually does not display for long enough to be
 * noticed.
 * 
 */

public class NewDocPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a NewDocPanel to tell the user documents are loading. Sets the
     * minimum size similar the window size. tells the editor to ask for the
     * document list.
     * 
     * @param editor
     */
    public NewDocPanel(final Editor editor) {
        final JLabel loadingMessage = new JLabel(
                "Loading documents from server", SwingConstants.CENTER);
        loadingMessage.setMinimumSize(new Dimension(700, 400));
        editor.sendMessage(editor.createControlMessage("getdoclist", -1, ""));
    }

    /**
     * paints the NewDocPanel as a basic panel
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * repaints the NewDocPanel as a basic panel
     */
    @Override
    public void repaint() {
        super.repaint();
    }
}
