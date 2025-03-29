package wprover;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * WuTextPane is a class that extends JTextPane and implements ActionListener.
 * It provides functionality for displaying styled text and handling button actions.
 */
public class WuTextPane extends JTextPane implements ActionListener {
    private JButton button;

    /**
     * Clears all text from the text pane.
     */
    public void clearAll() {
        this.setText("");
    }

    /**
     * Constructs a WuTextPane, sets it to be non-editable, and adds styles to the document.
     */
    public WuTextPane() {
        this.setEditable(false);
        StyledDocument doc = getStyledDocument();
        addStylesToDocument(doc);
    }

    /**
     * Sets the size of the text pane, ensuring the width is at least as wide as the parent component.
     *
     * @param d the new size of the text pane
     */
    public void setSize(Dimension d) {
        if (d.width < getParent().getSize().width)
            d.width = getParent().getSize().width;
        super.setSize(d);
    }

    /**
     * Indicates whether the viewport should track the width of the text pane.
     *
     * @return false, indicating the viewport should not track the width
     */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Adds various styles to the specified StyledDocument.
     *
     * @param doc the StyledDocument to which styles will be added
     */
    protected void addStylesToDocument(StyledDocument doc) {

        Font defont = CMisc.algebraFont;    //SansSerif
        String ffamily = defont.getFamily();

        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, ffamily);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("head", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(0, 128, 0));

        int sm = defont.getSize();

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, sm);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, sm + 6);

        s = doc.addStyle("large1", regular);
        StyleConstants.setFontSize(s, sm + 3);
        StyleConstants.setBold(s, false);

        s = doc.addStyle("icon1", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon pigIcon = GExpert.createImageIcon("images/dtree/right.gif");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }
        s = doc.addStyle("icon2", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        pigIcon = GExpert.createImageIcon("images/dtree/wrong.gif");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }
        s = doc.addStyle("icon3", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        pigIcon = GExpert.createImageIcon("images/dtree/question.gif");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }
        s = doc.addStyle("icon4", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        pigIcon = GExpert.createImageIcon("images/dtree/warn.gif");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }

        s = doc.addStyle("button", regular);
        button = new JButton(GExpert.getLanguage("View Remainder"));
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setActionCommand("remainder");
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);
    }

    /**
     * Adds an ActionListener to the button.
     *
     * @param ls the ActionListener to be added
     */
    public void addListnerToButton(ActionListener ls) {
        if (ls == null)
            return;

        button.addActionListener(ls);
    }

    /**
     * Handles action events. Currently, this method does nothing.
     *
     * @param e the ActionEvent to be handled
     */
    public void actionPerformed(ActionEvent e) {}
}
