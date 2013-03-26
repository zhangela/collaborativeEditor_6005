package View;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

/**
 * The custom subclass of JTextField, which handles the "hint" part of the
 * "Create New Document" section in DocumentSelectionPanel.
 * 
 */
public class HintTextField extends JTextField {

    private static final long serialVersionUID = 1L;
    Font gainFont = new Font("Lucida Grande", Font.PLAIN, 12);
    Font lostFont = new Font("Lucida Grande", Font.ITALIC, 12);

    /**
     * Constructs a hint text field, which is used for creating a new document and adding the title.
     * @param hint
     */
    public HintTextField(final String hint) {

        setText(hint);
        setFont(lostFont);
        setForeground(Color.GRAY);

        this.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(hint)) {
                    setText("");
                    setFont(gainFont);
                    setForeground(Color.BLACK);

                } else {
                    setText(getText());
                    setFont(gainFont);
                    setForeground(Color.BLACK);

                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().equals(hint) || getText().length() == 0) {
                    setText(hint);
                    setFont(lostFont);
                    setForeground(Color.GRAY);
                } else {
                    setText(getText());
                    setFont(gainFont);
                    setForeground(Color.BLACK);
                }
            }
        });

    }
}
