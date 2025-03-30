package wprover;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * RuleListDialog is a dialog that displays a list of rules and allows the user to interact with them.
 * It extends JBaseDialog and provides functionality for loading and displaying rules.
 */
public class RuleListDialog extends JBaseDialog {

    private GExpert gxInstance;
    private JScrollPane scroll;
    private RuleViewPane rpane;
    private ruleDpanel dpane;
    private JPanel split;

    /**
     * Constructs a new RuleListDialog with the specified GExpert instance.
     *
     * @param gx the GExpert instance to associate with this dialog
     */
    public RuleListDialog(GExpert gx) {
        super(gx.getFrame());
        gxInstance = gx;
        init();
    }

    /**
     * Constructs a new RuleListDialog with no associated GExpert instance.
     */
    public RuleListDialog() {
        gxInstance = null;
        init();
    }

    /**
     * Initializes the RuleListDialog by setting up the panels, listeners, and window properties.
     */
    public void init() {
        if (gxInstance != null && CMisc.isApplication())
            this.setAlwaysOnTop(true);

        this.setTitle(GExpert.getLanguage("Rule"));
        this.setModal(false);

        scroll = new JScrollPane((rpane = new RuleViewPane(gxInstance)));

        dpane = new ruleDpanel();

        split = new JPanel();
        split.setLayout(new BoxLayout(split, BoxLayout.Y_AXIS));
        split.add(scroll);
        split.add(dpane);
        this.getContentPane().add(split);
        this.setSize(500, 500);
        if (gxInstance != null)
            this.setLocation(gxInstance.getX(), gxInstance.getY() + gxInstance.getHeight() - 500);
    }

    /**
     * Loads a rule based on the type and index.
     *
     * @param t the type of the rule
     * @param n the index of the rule
     * @return true if the rule is loaded successfully, false otherwise
     */
    public boolean loadRule(int t, int n) {
        GRule r;
        if (t == 0)
            r = RuleList.getGrule(n);
        else
            r = RuleList.getFrule(n);
        if (r == null)
            return false;

        if (t == 0)
            this.setTitle(GExpert.getTranslationViaGettext("Rule {0} for the GDD Method", n + ""));
        else
            this.setTitle(GExpert.getTranslationViaGettext("Rule {0} for the Full Angle Method", n + ""));

        dpane.setRule(t, r);
        boolean rf = rpane.loadRule(t, n);
        rpane.centerAllObject();
        rpane.scrollToCenter();
        return rf;
    }

    /**
     * A custom JPanel that displays rule details and handles mouse events.
     */
    class ruleDpanel extends JPanel implements MouseListener {
        private JLabel label1, label2;
        private JEditorPane epane;
        private int rt1, rt2;

        /**
         * Returns the maximum size of this component.
         *
         * @return the maximum size of this component
         */
        public Dimension getMaximumSize() {
            Dimension dm = super.getMaximumSize();
            Dimension dm2 = super.getPreferredSize();
            dm2.setSize(dm.getWidth(), dm2.getHeight());
            return dm2;
        }

        /**
         * Constructs a new ruleDpanel.
         */
        public ruleDpanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
            label1 = new JLabel();
            label1.setForeground(new Color(0, 128, 0));
            label1.addMouseListener(this);
            label2 = new JLabel();
            epane = new JEditorPane();
            epane.setEditable(false);
            p.add(label1);
            p.add(Box.createHorizontalStrut(5));
            p.add(label2);
            this.add(p);
            this.add(epane);
        }

        /**
         * Sets the rule to be displayed in this panel.
         *
         * @param t the type of the rule
         * @param r the rule to display
         */
        public void setRule(int t, GRule r) {
            rt1 = t;
            rt2 = r.type;

            String sh;
            sh = GExpert.getTranslationViaGettext("Rule {0}", r.type + "");

            label1.setText(sh);
            if (r.name != null)
                label2.setText(GExpert.getLanguage(r.name));
            label1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            String s = GExpert.getLanguage(r.description);
            if (r.exstring != null)
                s += '\n' + GExpert.getLanguage(r.exstring);
            epane.setText(s);
        }

        /**
         * Handles mouse click events to open the rule's help page.
         *
         * @param e the mouse event
         */
        public void mouseClicked(MouseEvent e) {
            String dr = GExpert.getUserDir();
            String sp = GExpert.getFileSeparator();
            if (rt1 == 0)
                GExpert.openURL("file:///" + dr + sp + "help" + sp + "GDD" + sp + "r" + rt2 + ".html");
            else
                GExpert.openURL("file:///" + dr + sp + "help" + sp + "FULL" + sp + "r" + rt2 + ".html");
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * A custom JPanel that displays and interacts with rules in a graphical format.
     * Implements MouseListener, ComponentListener, and MouseMotionListener interfaces.
     */
    class RuleViewPane extends JPanel implements MouseListener, ComponentListener, MouseMotionListener {
        DrawTextProcess dx;
        int xx, yy;
        double scale = 1.0;
        protected Rectangle rc = new Rectangle(0, 0, 0, 0);

        /**
         * Returns the preferred size of this component.
         *
         * @return the preferred size of this component
         */
        public Dimension getPreferredSize() {
            return new Dimension((int) rc.getWidth() + 20, (int) rc.getHeight() + 20);
        }

        /**
         * Resets the size of the component based on the points bounds.
         */
        public void resetSize() {
            if (dx == null) return;
            Vector v1 = dx.pointlist;
            Rectangle rc = this.getPointsBounds(v1);
            double rx = RuleViewPane.this.getWidth();
            double ry = RuleViewPane.this.getHeight();
            if (rc.getWidth() > rx || rc.getHeight() > ry) {
                double r1 = rx / (rc.getWidth() * 1.1);
                double r2 = ry / (rc.getHeight() * 1.1);
                scale = r1 < r2 ? r1 : r2;
            } else
                scale = 1.0;
            centerAllObject();
            scrollToCenter();
        }

        /**
         * Invoked when the component's size changes.
         *
         * @param e the component event
         */
        public void componentResized(ComponentEvent e) {
            resetSize();
        }

        /**
         * Scrolls the view to the center of the component.
         */
        public void scrollToCenter() {
            Rectangle rc1 = scroll.getViewport().getBounds();
            JScrollBar b1 = scroll.getHorizontalScrollBar();
            b1.setValue((int) ((b1.getMaximum() - rc1.getWidth()) / 2));
            b1 = scroll.getVerticalScrollBar();
            b1.setValue(((int) (b1.getMaximum() - rc1.getHeight()) / 2));
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        /**
         * Centers all objects within the view.
         */
        public void centerAllObject() {
            Vector v1 = dx.pointlist;
            this.getPointsBounds(v1);

            xx = (int) ((this.getWidth() - rc.getWidth() * scale) / 2 - rc.getX() * scale);
            yy = (int) ((this.getHeight() - rc.getHeight() * scale) / 2 - rc.getY() * scale);
        }

        /**
         * Loads a rule based on the type and index.
         *
         * @param t the type of the rule
         * @param n the index of the rule
         * @return true if the rule is loaded successfully, false otherwise
         */
        public boolean loadRule(int t, int n) {
            String s = n + "";

            try {
                GeoPoly.clearZeroN();

                if (n < 10)
                    s = "0" + s;

                String sh;
                if (t == 0)
                    sh = "examples/Rules/GDD/" + s + ".gex";
                else sh = "examples/Rules/FULL/" + s + ".gex";
                dx.Load(sh);

                return true;
            } catch (IOException ee) {
                JOptionPane.showMessageDialog(gxInstance,
                        GExpert.getTranslationViaGettext("Can not find file {0}", s + ".gex"),
                        GExpert.getLanguage("Not Found"), JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }

        /**
         * Constructs a new RuleViewPane with the specified GExpert instance.
         *
         * @param gx the GExpert instance
         */
        public RuleViewPane(GExpert gx) {
            gxInstance = gx;
            xx = yy = 0;
            dx = new DrawTextProcess();
            dx.setCurrentDrawPanel(this);
            dx.setRecal(false);
            dx.SetCurrentAction(DrawProcess.MOVE);
            this.addMouseListener(this);
            this.setBackground(Color.white);
            this.addMouseMotionListener(this);
            this.addComponentListener(this);
            this.addMouseWheelListener(new MouseWheelListener() {
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int n = e.getWheelRotation();
                    if (scale < 0.4 && n < 0 || scale > 3.0 && n > 0) return;
                    scale = scale + n * 0.04;
                    if (dx == null) return;
                    centerAllObject();
                    RuleViewPane.this.repaint();
                }
            });
            //this.setBackground(Color.lightGray);
        }

        /**
         * Returns the bounds of the points in the given vector.
         *
         * @param v the vector of points
         * @return the bounds of the points
         */
        public Rectangle getPointsBounds(Vector v) {
            if (v.size() == 0) return rc;
            CPoint p1 = (CPoint) v.get(0);
            double x, y, x1, y1;
            x = x1 = p1.getx();
            y = y1 = p1.gety();
            for (int i = 1; i < v.size(); i++) {
                CPoint p = (CPoint) v.get(i);
                double x0 = p.getx();
                double y0 = p.gety();
                if (x0 > x)
                    x = x0;
                else if (x0 < x1)
                    x1 = x0;
                if (y0 > y)
                    y = y0;
                else if (y0 < y1)
                    y1 = y0;
            }
            rc.setBounds((int) x1, (int) y1, (int) (x - x1), (int) (y - y1));
            return rc;
        }

        /**
         * Invoked when a mouse button is pressed on a component.
         *
         * @param e the mouse event
         */
        public void mouseDragged(MouseEvent e) {
            dx.DWMouseDrag((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }

        /**
         * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
         *
         * @param e the mouse event
         */
        public void mouseMoved(MouseEvent e) {
            dx.DWMouseMove((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            this.repaint();
        }

        public void mouseClicked(MouseEvent e) {
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         *
         * @param e the mouse event
         */
        public void mousePressed(MouseEvent e) {
            dx.DWButtonDown((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }

        /**
         * Invoked when a mouse button has been released on a component.
         *
         * @param e the mouse event
         */
        public void mouseReleased(MouseEvent e) {
            dx.DWButtonUp((e.getX() - xx) / scale, (e.getY() - yy) / scale);
            dx.reCalculate();
            dx.recal_allFlash();
            this.repaint();
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        /**
         * Paints this component.
         *
         * @param g the graphics context to use for painting
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            resetSize();

            g2.translate(xx, yy);
            g2.scale(scale, scale);
            dx.paintPoint(g);
        }
    }
}
