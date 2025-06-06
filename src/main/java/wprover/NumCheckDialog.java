package wprover;

import gprover.Cm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * NumCheckDialog is a dialog for performing numerical checks on geometric points.
 * It allows the user to select points and check various geometric properties.
 */
public class NumCheckDialog extends JBaseDialog implements DiagramUpdater, ItemListener, WindowListener {

    private JComboBox bx;
    private JComboBox[] bxs = new JComboBox[6]; //bx, bx1, bx2, bx3, bx4, bx5, bx6;
    private GExpert gxInstance;

    private ImageIcon icon1, icon2;

    private JLabel[] labels = new JLabel[6]; //label1, label2, label3, label4, label5, label6;

    private JPanel cards;
    int TYPE;


    public static String[] ST = {"Collinear", "Parallel", "Perpendicular", "Cyclic", "Equal Distance", "Equal Angle"};
    public static int[] SN = {3, 4, 4, 4, 4, 6};

    /**
     * Constructs a new NumCheckDialog with the specified GExpert instance.
     *
     * @param gx the GExpert instance to associate with this NumCheckDialog
     */
    public NumCheckDialog(GExpert gx) {
        super(gx.getFrame());
        gxInstance = gx;
        this.setTitle(GExpert.getLanguage("Numerical Check"));
        this.addWindowListener(this);

        icon1 = GExpert.createImageIcon("images/ptree/hook.gif");
        icon2 = GExpert.createImageIcon("images/ptree/cross.gif");

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] sss = new String[ST.length];
        for (int i = 0; i < ST.length; i++)
            sss[i] = GExpert.getLanguage(ST[i]);

        bx = new JComboBox(sss);
        panel.add(bx);
        bx.addItemListener(this);

        Vector v = gxInstance.dp.getPointList();
        for (int i = 0; i < bxs.length; i++) {
            bxs[i] = new JComboBox(v);
            panel.add(bxs[i]);
            panel.add(Box.createHorizontalStrut(3));
            bxs[i].addItemListener(this);
        }

        top.add(panel);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(6, 1));
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            panel2.add(labels[i]);
        }

        top.add(panel2);
        cards = new JPanel(new CardLayout(1, 1));
        cards.add(ST[0], new Panel_Coll());
        cards.add(ST[1], new Panel_Para());
        cards.add(ST[2], new Panel_Perp());
        cards.add(ST[3], new Panel_Cyclic());
        cards.add(ST[4], new Panel_EQDis());
        cards.add(ST[5], new Panel_EQAng());
        top.add(cards);

        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        ActionListener ls = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if (s.equals("Clear")) {
                    NumCheckDialog.this.unSelectAllPoints();
                } else if (s.equals("Close")) {
                    NumCheckDialog.this.setVisible(false);
                }
            }
        };
        JButton b1 = new JButton(GExpert.getLanguage("Clear"));
        JButton b2 = new JButton(GExpert.getLanguage("Close"));
        b1.setActionCommand("Clear");
        b2.setActionCommand("Close");

        p3.add(b1);
        p3.add(b2);
        b1.addActionListener(ls);
        b2.addActionListener(ls);
        top.add(Box.createVerticalGlue());
        top.add(p3);
        setVisibleStatus(1);
        bx.setSelectedIndex(-1);
        this.unSelectAllPoints();
        this.getContentPane().add(top);
        this.setSize(400, 250);
        this.UpdateDiagram();
        gxInstance.dp.addDiagramUpdaterListener(this);
    }

    /**
     * Updates the diagram and the labels in the dialog.
     */
    public void UpdateDiagram() {
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText(getStringFromPoint((CPoint) bxs[i].getSelectedItem()));
        }
        int n = bx.getSelectedIndex();
        if (n >= 0) {
            Panel_Basic b = (Panel_Basic) cards.getComponent(n);
            b.DiagramUpdate();
        }
    }

    /**
     * Adds the selected point to the first available JComboBox.
     *
     * @param p the point to add
     */
    public void addSelectPoint(CPoint p) {
        for (int i = 0; i < bxs.length; i++) {
            if (bxs[i].getSelectedIndex() < 0) {
                bxs[i].setSelectedItem(p);
                return;
            }
        }
    }

    /**
     * Handles item state change events.
     *
     * @param e the item event
     */
    public void itemStateChanged(ItemEvent e) {
        Object o = e.getSource();
        if (o == bx) {
            int n = bx.getSelectedIndex();
            setVisibleStatus(n);
            unSelectAllPoints();

            if (n >= 0) {
                CardLayout cl = (CardLayout) (cards.getLayout());
                cl.show(cards, ST[n]);
            }
        } else
            UpdateDiagram();
    }

    /**
     * Sets the visibility status of the JComboBoxes and labels based on the selected index.
     *
     * @param n the selected index
     */
    public void setVisibleStatus(int n) {
        int num = 0;
        if (n == -1)
            num = 4;
        else num = SN[n];
        for (int i = 0; i < bxs.length; i++) {
            if (i < num) {
                bxs[i].setVisible(true);
                labels[i].setVisible(true);
            } else {
                bxs[i].setVisible(false);
                labels[i].setVisible(false);
            }
        }
    }

    /**
     * Unselects all points in the JComboBoxes.
     */
    public void unSelectAllPoints() {
        Vector v = gxInstance.dp.getPointList();
        for (int i = 0; i < bxs.length; i++) {
        }

        for (int i = 0; i < bxs.length; i++) {
            bxs[i].setSelectedIndex(-1);
        }
    }

    /**
     * Retrieves the selected point from the specified JComboBox.
     *
     * @param n the index of the JComboBox
     * @return the selected point
     */
    public CPoint getPt(int n) {
        return (CPoint) bxs[n].getSelectedItem();
    }

    /**
     * Checks if all visible JComboBoxes have selected points.
     *
     * @return true if all visible JComboBoxes have selected points, false otherwise
     */
    public boolean check_filled() {
        for (int i = 0; i < bxs.length; i++) {
            if ((bxs[i].isVisible() && bxs[i].getSelectedIndex() == -1)) return false;
        }
        return true;
    }

    /**
     * Retrieves the string representation of the specified point.
     *
     * @param p the point
     * @return the string representation of the point
     */
    public String getStringFromPoint(CPoint p) {
        if (p == null) return "      ";

        float x = (float) p.getx();
        float y = (float) p.gety();

        return " " + p.getname() + ":    x = " + x + ",  y = " + y;
    }


    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    /**
     * This method is called when the window is closed.
     * It removes the current instance from the diagram updater listeners.
     *
     * @param e the window event
     */
    public void windowClosed(WindowEvent e) {
        gxInstance.dp.RemoveDiagramUpdaterListener(this);
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }


    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * This method is called when the diagram is updated.
     * It updates the diagram and the labels in the dialog.
     */
    class Panel_Button extends JPanel {

        public Panel_Button() {
            this.setLayout(new FlowLayout(FlowLayout.LEADING));
        }
    }

    /**
     * Abstract class representing a basic panel for numerical checks.
     * It contains methods to set values, reset the panel, and update the diagram.
     */
    abstract class Panel_Basic extends JPanel {
        JLabel lex, ltex;
        String pstring;

        public Panel_Basic(String s) {
            pstring = s;
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            lex = new JLabel();
            this.add(lex);

            ltex = new JLabel();
            this.add(ltex);

        }

        private void setValue(boolean t) {
            if (t) {
                ltex.setIcon(icon1);
                ltex.setText(GExpert.getLanguage("TRUE"));
                ltex.setForeground(Color.green.darker());
            } else {
                ltex.setIcon(icon2);
                ltex.setText(GExpert.getLanguage("FALSE"));
                ltex.setForeground(Color.red.darker());
            }
        }

        private void reset() {
            ltex.setText("");
            ltex.setIcon(null);
        }

        public void DiagramUpdate() {
            boolean t = check_filled();
            if (!t)
                reset();
            else
                setValue(Cal_Value());
            String s = getLex();
            if (t)
                s += "        ";

            lex.setText(s);
        }

        abstract public boolean Cal_Value();

        abstract public String getLex();

        public String toString() {
            return pstring;
        }
    }

    /**
     * Panel_Coll is a subclass of Panel_Basic that checks for collinearity of three points.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_Coll extends Panel_Basic {

        public Panel_Coll() {
            super(GExpert.getLanguage(ST[0]));
        }

        public String getLex() {
            if (check_filled())
                return (pstring + ": " + getPt(0) + " " + getPt(1) + " " + getPt(2));
            else
                return ("");
        }

        public boolean Cal_Value() {
            return DrawBase.check_Collinear(getPt(0), getPt(1), getPt(2));
        }

    }

    /**
     * Panel_Para is a subclass of Panel_Basic that checks for parallelism of two lines.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_Para extends Panel_Basic {
        public Panel_Para() {
            super(ST[1]);
        }

        public boolean Cal_Value() {
            return DrawBase.check_para(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + Cm.PARALLEL_SIGN + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    /**
     * Panel_Perp is a subclass of Panel_Basic that checks for perpendicularity of two lines.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_Perp extends Panel_Basic {
        public Panel_Perp() {
            super(ST[2]);
        }

        public boolean Cal_Value() {
            return DrawBase.check_perp(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + Cm.PERPENDICULAR_SIGN + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    /**
     * Panel_Cyclic is a subclass of Panel_Basic that checks for cyclicity of four points.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_Cyclic extends Panel_Basic {
        public Panel_Cyclic() {
            super(gxInstance.getLanguage(ST[3]));
        }

        public boolean Cal_Value() {
            return DrawBase.check_cyclic(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (pstring + ": " + getPt(0) + ", " + getPt(1) + ", " + getPt(2) + ", " + getPt(3));
            else
                return ("");
        }
    }

    /**
     * Panel_EQDis is a subclass of Panel_Basic that checks for equal distances between two pairs of points.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_EQDis extends Panel_Basic {
        public Panel_EQDis() {
            super(ST[4]);
        }

        public boolean Cal_Value() {
            return DrawBase.check_eqdistance(getPt(0), getPt(1), getPt(2), getPt(3));
        }

        public String getLex() {
            if (check_filled())
                return (getPt(0) + "" + getPt(1) + " = " + getPt(2) + "" + getPt(3));
            else
                return ("");
        }
    }

    /**
     * Panel_EQAng is a subclass of Panel_Basic that checks for equal angles between two sets of points.
     * It overrides the Cal_Value and getLex methods to provide specific functionality.
     */
    class Panel_EQAng extends Panel_Basic {
        public Panel_EQAng() {
            super(ST[5]);
        }

        public boolean Cal_Value() {
            return DrawBase.check_eqangle(getPt(0), getPt(1), getPt(2), getPt(3), getPt(4), getPt(5));
        }

        public String getLex() {
            if (check_filled())
                return (Cm.ANGLE_SIGN + getPt(0) + "" + getPt(1) + "" + getPt(2) + " = " + Cm.ANGLE_SIGN + getPt(3) + "" + getPt(4) + "" + getPt(5));
            else
                return ("");
        }
    }
}
