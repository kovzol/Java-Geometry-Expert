package wprover;

import gprover.GTerm;
import gprover.Cons;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;

/**
 * PPDialog is a class that extends JBaseDialog and implements ActionListener,
 * MouseMotionListener, MouseListener, and ChangeListener interfaces. It is used
 * to display a dialog for nondegenerate conditions in a geometric context.
 */
public class
PPDialog extends JBaseDialog implements ActionListener, MouseMotionListener, MouseListener, ChangeListener {
    private DrawProcess dp;
    private static int WD = 600;
    private static int HD = 400;
    private GTerm gt;
    private JList list1, list2, list22;
    private JTabbedPane tt;
    private DefaultListModel model1, model2, model22;
    private static Color bcolor = new Color(249, 249, 255);


    /**
     * Constructs a new PPDialog with the specified GExpert, GTerm, and DrawProcess.
     *
     * @param gx the GExpert instance to associate with this dialog
     * @param gt the GTerm instance to associate with this dialog
     * @param dp the DrawProcess instance to associate with this dialog
     */
    public PPDialog(GExpert gx, GTerm gt, DrawProcess dp) {
        super(gx.getFrame(), "Nondegenerate Conditions"); // TODO. Internationalize.

        this.gt = gt;
        this.dp = dp;

        tt = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);

        model1 = new DefaultListModel();
        model2 = new DefaultListModel();
        model22 = new DefaultListModel();

        list1 = new JList(model1);
        list2 = new JList(model2);
        list22 = new JList(model22);
        list1.setBackground(bcolor);
        list2.setBackground(bcolor);
        list22.setBackground(bcolor);

        list22.setSelectionModel(list2.getSelectionModel());

        TitledBorder t = BorderFactory.createTitledBorder("Nondegenerate Conditions");
        t.setTitleColor(Color.gray);
        list2.setBorder(t);

        TitledBorder tb = BorderFactory.createTitledBorder("Polynomials of Nondegenerate Conditions");
        tb.setTitleColor(Color.gray);
        list22.setBorder(tb);

        JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, list2, list22);

        tt.addChangeListener(this);
        this.getConstructions();
        this.getNDGs();

        JSplitPane panelx = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list1, panel);
        panelx.setDividerLocation(200);
        this.getContentPane().add(new JScrollPane(panelx));
        this.setSize(WD, HD);
        int w = gx.getWidth();
        int h = gx.getHeight();
        int x = gx.getX();
        int y = gx.getY();
        this.setLocation(x + w / 2 - WD / 2, y + h / 2 - HD / 2);

        panel.resetToPreferredSizes();
        panel.revalidate();
    }

    /**
     * Handles state change events for the tabbed pane.
     *
     * @param e the ChangeEvent triggered by the tabbed pane
     */
    public void stateChanged(ChangeEvent e) {
        PPDialog.this.setTitle(tt.getTitleAt(tt.getSelectedIndex()));
    }

    /**
     * Handles mouse dragged events.
     *
     * @param e the MouseEvent triggered by dragging the mouse
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Handles mouse moved events.
     *
     * @param e the MouseEvent triggered by moving the mouse
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Handles mouse clicked events.
     *
     * @param e the MouseEvent triggered by clicking the mouse
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Handles mouse pressed events.
     *
     * @param e the MouseEvent triggered by pressing the mouse
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Handles mouse released events.
     *
     * @param e the MouseEvent triggered by releasing the mouse
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Handles mouse entered events.
     *
     * @param e the MouseEvent triggered by entering a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles mouse exited events.
     *
     * @param e the MouseEvent triggered by exiting a component
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Handles action events.
     *
     * @param e the ActionEvent triggered by an action
     */
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    /**
     * Retrieves and processes the constructions for the dialog.
     */
    public void getConstructions() {
    }

    /**
     * Retrieves and processes the nondegenerate conditions (NDGs) for the dialog.
     */
    public void getNDGs() {
        if (gt == null)
            return;

        Vector v1 = gt.pc();

        Vector v = gt.getAllNdgs(v1);

        model2.removeAllElements();
        model22.removeAllElements();
        model1.removeAllElements();

        GeoPoly poly = GeoPoly.getPoly();

        for (int i = 0; i < v1.size(); i++) {
            Cons c = (Cons) v1.get(i);
            model1.addElement(c.toDDString());
        }

        for (int i = 0; i < v.size(); i++) {
            Cons c = (Cons) v.get(i);
            model2.addElement((i + 1) + ": " + c.toDString());
        }

        for (int i = 0; i < v.size(); i++) {
            Cons c = (Cons) v.get(i);
            //        TMono m = poly.mm_poly(c, dp);
            //        String s = (i + 1) + ": ";
            //        if (m != null)
            //            s += poly.printNPoly(m);
            //        model22.addElement(s);
        }
    }
}