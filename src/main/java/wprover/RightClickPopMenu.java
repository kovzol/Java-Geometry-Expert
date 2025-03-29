package wprover;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * RightClickPopMenu is a class that extends JPopupMenu and implements ActionListener.
 * It is used to create a context menu for right-click actions on graphical elements.
 */
public class RightClickPopMenu extends JPopupMenu implements ActionListener {

    private GExpert gxInstance;
    private CClass cc;
    JRadioButtonMenuItem t1, t2;

    /**
     * Constructs a new RightClickPopMenu with the specified CClass and GExpert instances.
     *
     * @param c  the CClass instance to associate with this menu
     * @param gx the GExpert instance to associate with this menu
     */
    public RightClickPopMenu(CClass c, GExpert gx) {
        this.gxInstance = gx;
        this.cc = c;
        this.setForeground(Color.white);

        JMenuItem item = addAMenuItem(GExpert.getLanguage("Cancel Action"), true);
        item.setActionCommand("Cancel Action");
        item.setMnemonic(KeyEvent.VK_ESCAPE);
        KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        item.setAccelerator(ctrlP);

        addFreezeMenu();
        this.addSeparator();

        int d = 0;
        if (c != null)
            d = c.getColorIndex();
        addAColorMenuItem(GExpert.getLanguage("Color"), c != null, d);

        addSpecificMenu(c);
        this.addSeparator();
        item = addAMenuItem(GExpert.getLanguage("Properties"), c != null);
        item.setActionCommand("Properties");
    }

    /**
     * Adds specific menu items based on the type of the given CClass instance.
     *
     * @param c the CClass instance to determine the specific menu items
     */
    public void addSpecificMenu(CClass c) {
        if (c == null) return;

        JMenuItem item;

        int t = c.get_type();
        switch (t) {
            case CClass.TEXT:
                item = addAMenuItem(GExpert.getLanguage("Edit Text"), true);
                item.setActionCommand("Edit Text");
                addFontSizeMenuItem((CText) c);
                break;
            case CClass.POINT: {
                CPoint p = (CPoint) cc;
                if (!p.isFreezed()) {
                    item = addAMenuItem(GExpert.getLanguage("Freeze"), true);
                    item.setActionCommand("Freeze");
                } else {
                    item = addAMenuItem(GExpert.getLanguage("Unfreeze"), true);
                    item.setActionCommand("Unfreeze");
                }
                if (gxInstance.dp.getTraceByPt(p) != null) {
                    item = addAMenuItem(GExpert.getLanguage("Stop Trace"), true);
                    item.setActionCommand("Stop Trace");
                } else {
                    item = addAMenuItem(GExpert.getLanguage("Trace"), true);
                    item.setActionCommand("Trace");
                }
                item = addAMenuItem(GExpert.getLanguage("X Coordinate"), true);
                item.setActionCommand("X Coordinate");
                item = addAMenuItem(GExpert.getLanguage("Y Coordinate"), true);
                item.setActionCommand("Y Coordinate");
            }
            break;
            case CClass.LINE:
                item = addAMenuItem(GExpert.getLanguage("Slope"), true);
                item.setActionCommand("Slope");
                break;
            case CClass.CIRCLE:
                item = addAMenuItem(GExpert.getLanguage("Area"), true);
                item.setActionCommand("Area");

                item = addAMenuItem(GExpert.getLanguage("Girth"), true);
                item.setActionCommand("Girth");

                item = addAMenuItem(GExpert.getLanguage("Radius"), true);
                item.setActionCommand("Radius");

                break;
            case CClass.POLYGON:
                item = addAMenuItem(GExpert.getLanguage("Area"), true);
                item.setActionCommand("Area");
                break;
        }
    }

    /**
     * Handles action events for the menu items.
     *
     * @param e the ActionEvent triggered by the menu items
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Properties"))
            gxInstance.dp.viewElement(cc);
        else if (command.equals("Edit Text")) {
            CText t = (CText) cc;
            Point p = t.getLocation();
            gxInstance.dp.dialog_addText(t, (int) p.getX(), (int) p.getY());
        } else if (command.equals("Color")) {
            JMenuItem it = (JMenuItem) e.getSource();
            Color c = it.getForeground();
            int ci = DrawData.getColorIndex(c);
            cc.setColor(ci);
            gxInstance.d.repaint();
        } else if (command.equals("Move")) {
            gxInstance.setActionMove();
        } else if (command.equals("Cancel Action")) {
            gxInstance.onKeyCancel();
        } else if (command.equals("Freeze"))
            ((CPoint) cc).setFreezed(true);
        else if (command.equals("Unfreeze"))
            ((CPoint) cc).setFreezed(false);
        else if (command.equals("Stop Trace"))
            gxInstance.dp.stopTrack();
        else if (command.equals("Trace")) {
            gxInstance.dp.startTrackPt((CPoint) cc);
        } else if (command.equals("X Coordinate")) {
            gxInstance.dp.addCalculationPX((CPoint) cc);
        } else if (command.equals("Y Coordinate")) {
            gxInstance.dp.addCalculationPY((CPoint) cc);
        } else if (command.equals("Area")) {
            if (cc instanceof Circle)
                gxInstance.dp.addCalculationCircle((Circle) cc, 0);
            else {
                CPolygon cp = (CPolygon) cc;
                if (cp.ftype == 1) {
                    Circle c = gxInstance.dp.fd_circleOR((CPoint) cp.getElement(0), (CPoint) cp.getElement(1), (CPoint) cp.getElement(2));
                    if (c != null)
                        gxInstance.dp.addCalculationCircle(c, 0);
                } else {
                    gxInstance.dp.addCalculationPolygon((CPolygon) cc);
                }
            }
        } else if (command.equals("Girth")) {
            gxInstance.dp.addCalculationCircle((Circle) cc, 1);
        } else if (command.equals("Radius")) {
            gxInstance.dp.addCalculationCircle((Circle) cc, 2);
        } else if (command.equals("Slope")) {
            gxInstance.dp.addLineSlope((CLine) cc);
        } else if (command.equals("Unfreeze All Points"))
            gxInstance.dp.unfreezeAllPoints();
    }

    /**
     * Adds a menu item with the specified label and enabled state.
     *
     * @param s the label of the menu item
     * @param t the enabled state of the menu item
     * @return the created JMenuItem
     */
    private JMenuItem addAMenuItem(String s, boolean t) {
        JMenuItem item = new JMenuItem(s);
        item.setEnabled(t);
        item.addActionListener(this);
        add(item);
        return item;
    }

    /**
     * Adds font size menu items for the specified CText instance.
     *
     * @param t the CText instance to add font size menu items for
     */
    private void addFontSizeMenuItem(CText t) {
        int f = t.getFontSize();
        int[] fz = CMisc.getFontSizePool();
        int n = fz.length;
        Font fx = t.getFont();

        ActionListener ls = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                CText t = (CText) cc;
                Object obj = e.getSource();

                if (obj == t1) {
                    t.setPlain();
                } else if (obj == t2) {
                    t.setBold();
                } else {
                    int n = Integer.parseInt(s.trim());
                    t.setFontSize(n);
                }
            }
        };

        JMenu m = new JMenu(GExpert.getLanguage("Font size"));
        for (int i = 0; i < n; i++) {
            JMenuItem item = new JMenuItem(" " + fz[i] + " ");
            item.addActionListener(ls);
            item.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            if (fz[i] == f)
                item.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.gray, 1), new LineBorder(Color.white, 1)));
            m.add(item);
        }
        t1 = new JRadioButtonMenuItem(GExpert.getLanguage("Plain"));
        t2 = new JRadioButtonMenuItem(GExpert.getLanguage("Bold"));

        ButtonGroup g = new ButtonGroup();
        g.add(t1);
        g.add(t2);
        if (fx.isPlain())
            t1.setSelected(true);
        t1.addActionListener(ls);
        if (fx.isBold())
            t2.setSelected(true);
        t2.addActionListener(ls);
        this.add(t1);
        this.add(t2);
        this.add(m);
    }

    /**
     * Adds a menu item to unfreeze all points if there are any frozen points.
     */
    private void addFreezeMenu() {
        if (gxInstance.dp.containFreezedPoint()) {
            JMenuItem item = new JMenuItem(GExpert.getLanguage("Unfreeze All Points"));
            item.setActionCommand("Unfreeze All Points");
            item.setEnabled(true);
            item.addActionListener(this);
            this.add(item);
        }
    }

    /**
     * Adds a color menu item with the specified label, enabled state, and default color index.
     *
     * @param s the label of the color menu item
     * @param t the enabled state of the color menu item
     * @param d the default color index
     */
    private void addAColorMenuItem(String s, boolean t, int d) {
        JMenu item = new JMenu(s);
        item.setEnabled(t);
        add(item);
        if (!t)
            return;

        int n = DrawData.getColorCounter();
        Dimension dm = new Dimension(90, 15);
        for (int i = 0; i < n; i++) {
            JMenuItem it = new JMenuItem();
            Color c = DrawData.getColor(i);
            it.add(new colorPanel(c));
            it.setForeground(c);
            it.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
            it.setPreferredSize(dm);
            it.setActionCommand("Color");
            it.addActionListener(this);
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            it.setToolTipText("r = " + r + ", g = " + g + ", b = " + b);
            if (d == i && t) {
                it.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.red, 1), new LineBorder(Color.white, 1)));
            }
            item.add(it);
        }
    }

    /**
     * A custom JPanel class that represents a color panel.
     * It sets the foreground and background color to the specified color.
     */
    class colorPanel extends JPanel {
        public colorPanel(Color c) {
            this.setForeground(c);
            this.setBackground(c);
        }
    }

}
