package wprover;

import gprover.Cons;
import gprover.Gib;
import gprover.GTerm;
import maths.TMono;
import maths.TPoly;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * PanelAlgebraic is an abstract class that extends JScrollPane and implements
 * Runnable and ActionListener interfaces. It provides methods for handling
 * algebraic operations and displaying results in a text pane.
 */
public abstract class PanelAlgebraic extends JScrollPane implements Runnable, ActionListener {
    protected DrawProcess dp;
    protected GeoPoly poly = GeoPoly.getPoly();
    protected boolean running = false;
    protected GTerm gt = null;
    protected WuTextPane tpane;
    protected Thread main;
    protected GExpert gxInstance;
    protected Language lan;
    protected TMono _mremainder = null;
    protected RunningDialog rund;

    /**
     * Retrieves the language string for the specified key.
     *
     * @param n the key index (not used)
     * @param s the key for the language string
     * @return the language string associated with the key
     * @deprecated This method is deprecated. Use GExpert.getLanguage(s) directly.
     */
    @Deprecated
    public String getLanguage(int n, String s) {
        return GExpert.getLanguage(s);
    }

    /**
     * Checks if the panel is currently running.
     *
     * @return true if the panel is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stops the panel from running.
     */
    public void stop() {
        running = false;
    }

    /**
     * Sets the GExpert instance and updates the language settings.
     *
     * @param gx the GExpert instance to set
     */
    public void setXInstance(GExpert gx) {
        if (gx != null) {
            this.gxInstance = gx;
            lan = gx.getLan();
        }
    }

    /**
     * Constructs a new PanelAlgebraic with the specified DrawProcess and WuTextPane.
     *
     * @param dp    the DrawProcess instance to associate with this panel
     * @param tpane the WuTextPane instance to associate with this panel
     */
    public PanelAlgebraic(DrawProcess dp, WuTextPane tpane) {
        super(tpane);
        this.dp = dp;
        this.tpane = tpane;
        tpane.addListnerToButton(this);
    }

    /**
     * Abstract method to stop the panel from running.
     * Must be implemented by subclasses.
     */
    public abstract void stopRunning();

    /**
     * Clears all content from the text pane and stops the panel from running.
     */
    public void clearAll() {
        running = false;
        tpane.clearAll();
    }

    /**
     * Adds a string to the text pane with a newline.
     *
     * @param s the string to add
     */
    protected void addString(String s) {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), s + "\n", doc.getStyle("regular"));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds a string to the text pane with the specified style.
     *
     * @param s    the string to add
     * @param type the style type to apply
     */
    protected void addString(String s, String type) {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), " ", doc.getStyle(type));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds a button to the text pane.
     */
    protected void addButton() {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), "button", doc.getStyle("button"));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds a bold string to the text pane.
     *
     * @param s the string to add
     */
    protected void addString1(String s) {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), s, doc.getStyle("bold"));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds a header string to the text pane with newlines before and after.
     *
     * @param s the string to add
     */
    protected void addString2(String s) {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), "\n" + s + "\n", doc.getStyle("head"));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds a header string to the text pane with a newline after.
     *
     * @param s the string to add
     */
    protected void addString2s(String s) {
        try {
            StyledDocument doc = tpane.getStyledDocument();
            doc.insertString(doc.getLength(), s + "\n", doc.getStyle("head"));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
    }

    /**
     * Adds the algebraic form of the points to the text pane.
     */
    protected void addAlgebraicForm() {
        addString2s(getLanguage(1101, "The Algebraic Form:"));
        Vector vp = dp.getPointList();
        int n = vp.size();
        if (n == 0)
            return;

        for (int i = 0; i < vp.size(); i++) {
            CPoint pt = (CPoint) vp.get(i);
            String s1 = pt.x1.getString();
            String s2 = pt.y1.getString();
            if (GeoPoly.vzero(pt.x1.xindex))
                s1 = "0";
            if (GeoPoly.vzero(pt.y1.xindex))
                s2 = "0";
            addString1(pt + ": (" + s1 + "," + s2 + ")  ");
            if (i != 0 && i != vp.size() && i % 5 == 0)
                addString1("\n");
        }
        addString1("\n");
    }

    /**
     * Handles action events for the panel.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        JTextArea a = new JTextArea();
        a.setLineWrap(true);
        String s = GeoPoly.getInstance().getExpandedPrint(_mremainder);
        a.setText(s);
        JDialog dlg = new JDialog(gxInstance.getFrame());
        dlg.setTitle("Remainder");
        dlg.setSize(400, 300);
        dlg.getContentPane().add(new JScrollPane(a, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        gxInstance.centerDialog(dlg);
        dlg.setVisible(true);
    }

    /**
     * Scrolls the text pane to the end.
     */
    protected void scrollToEnd() {
        JScrollBar bar = this.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
        repaint();
    }

    /**
     * Retrieves the TMono representation of the specified construction.
     *
     * @param c the construction
     * @return the TMono representation of the construction
     */
    protected TMono getTMono(Cons c) {
        return dp.getTMono(c);
    }

    /**
     * Runs the panel's main process.
     */
    public void run() {
    }
}