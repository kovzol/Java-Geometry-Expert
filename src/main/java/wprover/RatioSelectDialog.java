package wprover;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RatioSelectDialog is a dialog that allows the user to input two integers
 * representing a ratio. It extends JBaseDialog and implements ActionListener.
 */
public class RatioSelectDialog extends JBaseDialog implements ActionListener {
    IntTextField field1, field2;
    JButton button1, button2;
    boolean returnValue;


    /**
     * Constructs a new RatioSelectDialog with the specified GExpert instance.
     *
     * @param f the GExpert instance to associate with this dialog
     */
    public RatioSelectDialog(GExpert f) {
        super(f.getFrame(), true);
        this.setTitle(f.getLanguage("Set the ratio"));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JLabel labelx = new JLabel(f.getLanguage("Please input two integers"));
        JPanel px = new JPanel(new FlowLayout());
        px.add(labelx);
        px.add(Box.createHorizontalGlue());
        contentPane.add(px);

        JPanel p = new JPanel(new FlowLayout());
        field1 = new IntTextField(1, 3);
        p.add(field1);
        JLabel label = new JLabel(" / ");
        p.add(label);
        field2 = new IntTextField(1, 3);
        p.add(field2);
        contentPane.add(p);

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(Box.createHorizontalGlue());
        button1 = new JButton(f.getLanguage("OK"));
        button2 = new JButton(f.getLanguage("Cancel"));
        p1.add(button1);
        p1.add(button2);

        button1.addActionListener(this);
        button2.addActionListener(this);
        contentPane.add(p1);
        this.setSize(250, 130);
        field1.setSelectionStart(0);
        field1.setSelectionEnd(1);
        centerWindow();

        if (CMisc.isApplication())
            this.setAlwaysOnTop(true);
    }

    /**
     * Handles action events for the dialog buttons.
     *
     * @param e the ActionEvent triggered by the buttons
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1)
            returnValue = true;
        else returnValue = false;
        this.setVisible(false);
    }

    /**
     * Gets the value from the first input field.
     *
     * @return the integer value from the first input field
     */
    public int getValue1() {
        String s = field1.getText();
        return Integer.parseInt(s);
    }

    /**
     * Gets the value from the second input field.
     *
     * @return the integer value from the second input field
     */
    public int getValue2() {
        String s = field2.getText();
        return Integer.parseInt(s);
    }

    /**
     * Centers the dialog window relative to its owner.
     */
    public void centerWindow() {
        Window wo = this.getOwner();
        int x = wo.getX();
        int y = wo.getY();
        int w = wo.getWidth();
        int h = wo.getHeight();
        this.setLocation(x + w / 2 - 200 / 2, y + h / 2 - 120 / 2);
    }

    /**
     * IntTextField is a custom JTextField that only accepts integer input.
     */
    class IntTextField extends JTextField {
        /**
         * Constructs a new IntTextField with the specified default value and size.
         *
         * @param defval the default integer value
         * @param size   the size of the text field
         */
        public IntTextField(int defval, int size) {
            super("" + defval, size);
        }

        /**
         * Creates the default model for the text field, which is an IntTextDocument.
         *
         * @return the default document model
         */
        protected Document createDefaultModel() {
            return new IntTextDocument();
        }

        /**
         * Checks if the current text in the field is a valid integer.
         *
         * @return true if the text is a valid integer, false otherwise
         */
        public boolean isValid() {
            try {
                Integer.parseInt(getText());
                return true;
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        /**
         * Gets the integer value from the text field.
         *
         * @return the integer value, or 0 if the text is not a valid integer
         */
        public int getValue() {
            try {
                return Integer.parseInt(getText());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /**
         * IntTextDocument is a custom PlainDocument that only allows integer input.
         */
        class IntTextDocument extends PlainDocument {
            /**
             * Inserts a string into the document, ensuring that it remains a valid integer.
             *
             * @param offs the offset at which to insert the string
             * @param str  the string to insert
             * @param a    the attribute set
             * @throws BadLocationException if the insert position is invalid
             */
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null)
                    return;
                String oldString = getText(0, getLength());
                String newString = oldString.substring(0, offs) + str
                        + oldString.substring(offs);
                try {
                    Integer.parseInt(newString + "0");
                    super.insertString(offs, str, a);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

}