package wprover;


import UI.DropShadowBorder;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
/**
 * The `AboutDialog` class represents a custom popup dialog that displays information about the application.
 * It extends `JPopupMenu` and implements `MouseListener` to handle mouse events.
 * The dialog includes labels, panels, and a text pane with information about the application and its authors.
 */
public class AboutDialog extends JPopupMenu implements MouseListener {
    JLabel b2;
    Color color = new Color(206, 223, 242);
    GExpert gx;
    WuTextPane pane;


    /**
     * Initializes the AboutDialog with the specified GExpert frame.
     * Sets up the dialog layout, adds labels and panels with information, and configures mouse listeners.
     *
     * @param f the GExpert frame to associate with this dialog
     */
    public AboutDialog(GExpert f) {
        gx = f;

        this.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(), BorderFactory.createLineBorder(color, 4)));
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.addMouseListener(this);

        JLabel label = new JLabel(GExpert.createImageIcon("images/about/headline.jpg"));
        label.addMouseListener(this);
        label.setBackground(color);
        label.setForeground(color);
        label.setOpaque(false);
        panel.add(label);


        JPanel panel2 = new JPanel();
        panel2.addMouseListener(this);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

        JLabel lv = new JLabel(GExpert.getLanguage("Java Geometry Expert") + " " + Version.getVersion1());
        lv.setHorizontalTextPosition(JLabel.LEFT);
        panel2.add(lv);

        lv = new JLabel(GExpert.getLanguage("Last modified on") + " " + Version.getData());
        lv.setHorizontalTextPosition(JLabel.RIGHT);
        panel2.add(lv);

        lv = new JLabel(GExpert.getLanguage("Java version") + " " + System.getProperty("java.version"));
        lv.setHorizontalTextPosition(JLabel.LEFT);
        panel2.add(lv);
        pane = new WuTextPane();
        pane.addMouseListener(this);
//        String s = "\n" +
        addString("\n", "regular");
        addString(GExpert.getLanguage("Java Geometry Expert is free under GNU General Public License (GPL).") + "\n", "regular");
        addString(GExpert.getLanguage("The user may download and distribute it freely.") + "\n\n", "regular");
//        addString("The software Geometry Expert (GEX) was originally developed around 1994.\n", "regualr");
//        addString("The Java Version of Geometry Expert ", "regular");
//        addString("(JGEX)", "bold");
//
//
//        String s1 = " initially began in early 2004 in\n Wichita State Univerisity. " +
//                "JGEX is a system which combines dynamic geometry software (DGS),\n" +
//                " automated geometry theorem prover (AGTP) and our approach for visually dynamic presentation of proofs (VDPP).\n\n ";

        //  addString(s1, "regular");

        addString("\n", "bold");
        addString(GExpert.getLanguage("Authors") + ":" + "\n", "bold");

        addString("Shang Ching Chou\t\t", "bold");
        addString("chou@cs.wichita.edu\n", "head");

        addString("Xiao Shan Gao   \t\t", "bold");
        addString("xgao@mmrc.iss.ac.cn" + "\n", "head");

        addString("Zheng Ye        \t\t", "bold");
        addString("yezheng@gmail.com", "head");


        pane.setEditable(false);
        Font fx = gx.getDefaultFont();

        if (fx != null) {
            Font fy = new Font(fx.getName(), Font.PLAIN, 12);
            pane.setFont(fy);
        }

        panel2.add(pane);

        JPanel panel3 = new JPanel(new FlowLayout());
        panel3.addMouseListener(this);

        JLabel b1 = new JLabel(GExpert.getLanguage("For more information, please visit:"));
        b1.addMouseListener(this);
        b2 = new JLabel("https://github.com/kovzol/Java-Geometry-Expert"); // TODO: Put this in a top-level file.
        b2.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        b2.addMouseListener(this);
        panel3.add(b1);
        panel3.add(b2);
        panel2.add(panel3);
        panel3.setBackground(Color.white);
        b2.setForeground(Color.blue);

        b2.addMouseListener(this);
        b2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(panel2);
        this.add(panel);
        initLocation();
    }

    /**
     * Centers the dialog within the parent frame.
     */
    public void initLocation() {
        Frame f = gx.getFrame();
        Dimension dm = this.getPreferredSize();
        this.setLocation(f.getX() + f.getWidth() / 2 - (int) dm.getWidth() / 2, f.getY() + f.getHeight() / 2 - (int) dm.getHeight() / 2);
    }

    /**
     * Called when the mouse is clicked.
     *
     * @param e the event to be processed
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Adds a string to the text pane with the specified style.
     *
     * @param s the string to add
     * @param type the style type for the string
     */
    protected void addString(String s, String type) {
        try {
            StyledDocument doc = pane.getStyledDocument();
            doc.insertString(doc.getLength(), s, doc.getStyle(type));
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }

    }

    /**
     * Called when the mouse is pressed.
     *
     * @param e the event to be processed
     */
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == b2)
            GExpert.openURL("https://github.com/kovzol/Java-Geometry-Expert"); // FIXME: it opens twice
        this.setVisible(false);
    }

    /**
     * Called when the mouse is released.
     *
     * @param e the event to be processed
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Called when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    public void mouseEntered(MouseEvent e) {
        //Component c = (Component) e.getSource();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        if (e.getSource() == b2)
            b2.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
    }

    /**
     * Called when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    public void mouseExited(MouseEvent e) {
        Component c = (Component) e.getSource();
        setCursor(Cursor.getDefaultCursor());

        if (e.getSource() == b2) {
            b2.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
    }

}


