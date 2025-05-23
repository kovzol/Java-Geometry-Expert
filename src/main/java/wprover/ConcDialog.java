package wprover;

import gprover.*;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

/**
 * ConcDialog is a dialog for selecting geometric conclusions.
 * It allows the user to select points and check geometric properties.
 */
public class ConcDialog extends JBaseDialog implements ActionListener, ItemListener {
    int type = 0; // 0. Conclusion   1. NDGS.
    final static String[] ts = {
            "Collinear",
            "Parallel",
            "Perpendicular",
            "Midpoint",
            "Cyclic",
            "Equal Distance",
            "Equal Angle",

            "Similiar Triangle",
            "Congruent Triangle",
            "Equilateral Triangle",

            "Bisect",
            "Tangent",

            "Equal Product",
            "Ratio",

            "Special Angle",
            "Angles Equation",
            "Segment Equation"
    };


    final public static int CONCLUSION_OK = 0;
    final static Font font = new Font("Dialog", Font.BOLD, 14);
    final public static int CONCLUSION_CANCEL = 1;

    private Vector vlist = new Vector();
    private Vector vlist1 = new Vector();
    private boolean model = true;
    private int returnValue;
    private GExpert gxInstance;

    private JComboBox bt;
    private JButton bok;
    private JComboBox bx1, bx2;
    private ImageIcon ic1, ic2, ic3;
    private JLabel ltext, ltext1;
    private JPanel cardPane;
    private condPane Pane2;


    /**
     * Sets the type of the dialog.
     *
     * @param t the type to set
     */
    public void setType(int t) {
        this.type = t;
    }

    /**
     * Changes the action listener for the OK button.
     *
     * @param ls the new ActionListener to set
     */
    public void changeBOKListener(ActionListener ls) {
        bok.removeActionListener(this);
        bok.addActionListener(ls);
    }

    /**
     * Sets the conclusion in the dialog.
     *
     * @param c the conclusion to set
     */
    public void setCns(Cons c) {
        if (c == null)
            return;
        if (c.type == Gib.CO_COLL) {
            bt.setSelectedIndex(0);
            for (int i = 0; i < 3; i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                CPoint px = gxInstance.dp.findPoint(c.pss[i].toString());
                b.setSelectedItem(px);
            }
        }
    }

    /**
     * Constructs a ConcDialog with the specified GExpert instance and title.
     *
     * @param gx the GExpert instance
     * @param title the title of the dialog
     */
    public ConcDialog(GExpert gx, String title) {
        super(gx.getFrame(), title);
        this.setTitle(title);
        gxInstance = gx;
        model = false;
        init();
        this.setPoints(gx.dp.getPointList());
        this.setModal(false);
    }

    /**
     * Sets the value of ltext1 based on the given type.
     *
     * @param t the type to set
     */
    private void setLtext1Value(int t) {
        if (model) return;

        if (t == 1) {
            ltext1.setText(GExpert.getLanguage("True"));
            ltext1.setIcon(ic1);
            ltext1.setForeground(Color.green.darker());
        } else if (t == -1) {
            ltext1.setText(GExpert.getLanguage("False"));
            ltext1.setIcon(ic2);
            ltext1.setForeground(Color.red.brighter().brighter());
        } else if (t == 0) {
            ltext1.setText("");
            ltext1.setIcon(null);
            ltext1.setForeground(Color.black);
        }
    }

    /**
     * Initializes the dialog components.
     */
    private void init() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel Pane1 = new JPanel();
        Pane1.setLayout(new BoxLayout(Pane1, BoxLayout.Y_AXIS));

        ic1 = GExpert.createImageIcon("images/ptree/hook.gif");
        ic2 = GExpert.createImageIcon("images/ptree/cross.gif");
        ic3 = GExpert.createImageIcon("images/ptree/question.gif");

        int len = ts.length;
        String[] ss = new String[len];
        for (int i = 0; i < len; i++)
            ss[i] = GExpert.getLanguage(ts[i]);

        bt = new JComboBox(ss) {
            public Dimension getMaximumSize() {
                return bt.getPreferredSize();
            }
        };
        bt.setMaximumRowCount(20);
        bt.addItemListener(this);
        contentPane.add(bt);

        cardPane = new JPanel(new CardLayout(2, 2));
        JPanel topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel topPane1 = new JPanel();
        topPane1.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPane.add(new JLabel(GExpert.getLanguage("First Set")));
        topPane1.add(new JLabel(GExpert.getLanguage("Second Set")));

        for (int i = 0; i < 4; i++) {
            JComboBox b = new JComboBox();
            b.addItemListener(this);
            topPane.add(b);
            vlist.add(b);
        }
        for (int i = 0; i < 4; i++) {
            JComboBox b = new JComboBox();
            b.addItemListener(this);
            topPane1.add(b);
            vlist1.add(b);
        }
        String[] ls = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        bx1 = new JComboBox(ls);
        bx2 = new JComboBox(ls);
        bx1.addItemListener(this);
        bx2.addItemListener(this);
        topPane.add(bx1);
        topPane1.add(bx2);

        Pane1.add(topPane);
        Pane1.add(topPane1);

        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));
        ltext = new JLabel(GExpert.getLanguage("Please select"));

        ltext1 = new JLabel(GExpert.getLanguage("true"));
        ltext1.setIcon(ic1);
        ltext1.setHorizontalTextPosition(JLabel.LEFT);
        ltext1.setVisible(!model);

        setLtext1Value(0);
        ltext.setAlignmentX(Component.RIGHT_ALIGNMENT);
        textPane.add(Box.createHorizontalStrut(3));
        textPane.add(ltext);
        textPane.add(Box.createHorizontalGlue());
        textPane.add(ltext1);
        Pane1.add(textPane);
        cardPane.add(Pane1, "1");

        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));

        bottomPane.add(Box.createHorizontalGlue());
        bok = new JButton(GExpert.getLanguage("OK"));
        bok.addActionListener(this);
        bottomPane.add(bok);
        JButton bclear, bcancel;
        bottomPane.add(bclear = new JButton(GExpert.getLanguage("Clear")));
        bclear.addActionListener(this);
        bottomPane.add(bcancel = new JButton(GExpert.getLanguage("Cancel")));
        bcancel.addActionListener(this);
        bok.setActionCommand("OK");
        bclear.setActionCommand("Clear");
        bcancel.setActionCommand("Cancel");

        contentPane.add(cardPane);
        contentPane.add(bottomPane);
        this.add(contentPane);
        Pane2 = new condPane();
        cardPane.add(Pane2, "2");

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                returnValue = CONCLUSION_CANCEL;
            }
        });

        this.resetAllItem();
    }

    /**
     * Sets the points in the combo boxes.
     *
     * @param v the vector of points to set
     */
    public void setPoints(Vector v) {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            b.removeAllItems();
            for (int j = 0; j < v.size(); j++) {
                Object obj = v.get(j);
                b.addItem(obj);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            b.removeAllItems();
            for (int j = 0; j < v.size(); j++) {
                Object obj = v.get(j);
                b.addItem(obj);
            }
        }

        Pane2.setPoints(v);
    }

    /**
     * Returns the number of points left to be selected.
     *
     * @return the number of points left to be selected
     */
    private int ptLeftTobeSelect() {
        int n = 0;

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() == -1) {
                n++;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() == -1) {
                n++;
            }
        }
        return n;
    }

    /**
     * Handles item state changes for the combo boxes.
     *
     * @param e the ItemEvent triggered by the user
     */
    public void itemStateChanged(ItemEvent e) {
        if (!this.isVisible()) return;
        Object source = e.getSource();
        if (source == bt) {
            int id = bt.getSelectedIndex();
            CardLayout layout = ((CardLayout) cardPane.getLayout());
            if (id == 18 || id == 19) {
                {
                    int t = 0;
                    if (id == 18)
                        t = 1;
                    else
                        t = 0;
                    layout.show(cardPane, "2");
                    Pane2.setStatus(t);
                    bok.setEnabled(true);
                }
            } else {
                layout.show(cardPane, "1");
                resetAllItem();
                setItemChanged(id);
                if (id != -1) // FIXME, improve translation pattern
                    ltext.setText(GExpert.getLanguage("The number of points to be selected:") + " " + this.getStatePointsCount());
            }
        } else {

            if (model == false && inputFinished()) {
                Cons c = this.getProve();

                bok.setEnabled(true);
                if (c != null)
                    ltext.setText(c.toSString());
                boolean v = checkValid();
                if (v)
                    setLtext1Value(1);
                else
                    setLtext1Value(-1);
                if (v)
                    bt.setEnabled(true);
            } else { // FIXME, improve translation pattern
                ltext.setText(GExpert.getLanguage("The number of points to be selected:") + " " + this.getStatePointsCount());
                setLtext1Value(0);
            }
        }
    }

    /**
     * Checks if the input is finished.
     *
     * @return true if the input is finished, false otherwise
     */
    private boolean inputFinished() {
        return 0 == ptLeftTobeSelect();
    }

    /**
     * Selects a point in the combo boxes.
     *
     * @param p the point to select
     */
    public void selectAPoint(CPoint p) {
        Pane2.selectAPoint(p);

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                b.setSelectedItem(p);
                return;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                b.setSelectedItem(p);
                return;
            }
        }
    }

    /**
     * Resets all combo boxes and labels.
     */
    private void resetAllItem() {
        bok.setEnabled(false);
        ltext1.setIcon(ic3);
        ltext1.setText("");

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            b.setSelectedIndex(-1);
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            b.setSelectedIndex(-1);
        }
        bx1.setSelectedIndex(0);
        bx2.setSelectedIndex(0);
    }

    /**
     * Returns the number of points required for the selected item.
     *
     * @return the number of points required
     */
    private int getStatePointsCount() {
        switch (bt.getSelectedIndex()) {
            case 0:
            case 3:
            case 9:
            case 10:
                return 3;

            case 1:
            case 2:
            case 4:
            case 5:
            case 11:
            case 13:
                return 4;
            case 7:
            case 8:
                return 6;

            case 6:
            case 12:
                return 8;
        }
        return -1;
    }

    /**
     * Updates the visibility of combo boxes based on the selected item ID.
     *
     * @param id the ID of the selected item
     */
    private void setItemChanged(int id) {
        switch (id) {
            case 0:
            case 3:
            case 9:
            case 10:
                this.setVisibleBox(3);
                break;
            case 4:
                this.setVisibleBox(4);
                break;
            case 1:
            case 2:
            case 5:
            case 11:
            case 13:
                this.setVisibleBox1(4);
                break;
            case 7:
            case 8:
                this.setVisibleBox1(6);
                break;
            case 6:
            case 12:
                this.setVisibleBox1(8);
                break;
        }
        if (id == 13) {
            setRatioVisible();
        } else if (id == 14) {
            setSAngle();
        } else if (id == 18) {
            //midPane.setVisible(true);
        } else {
            bx1.setVisible(false);
            bx2.setVisible(false);
        }
    }

    /**
     * Sets the angles in the combo box for special angles.
     */
    public void setSAngle() {
        bx1.removeAllItems();
        int[] angles = {0, 15, 30, 36, 45, 72, 75, 90, 120, 135, 150, 180};
        for (int i : angles) {
            bx1.addItem(i);
        }
        bx1.setVisible(true);
        bx2.setVisible(false);
    }

    /**
     * Sets the ratio values in the combo boxes.
     */
    public void setRatioVisible() {
        bx1.removeAllItems();
        bx2.removeAllItems();
        for (int i = 1; i <= 9; i++) {
            Object obj = i;
            bx1.addItem(obj);
            bx2.addItem(obj);
        }
        bx1.setVisible(true);
        bx2.setVisible(true);
    }

    /**
     * Handles action events for the buttons.
     *
     * @param e the ActionEvent triggered by the user
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase("OK")) {
            returnValue = CONCLUSION_OK;
            this.setVisible(false);
            GExpert.conclusion = null; // reset
            if (type == 0)
                gxInstance.getpprove().set_conclusion(getProve(), this.checkValid());
            else
                gxInstance.getpprove().add_ndgs(getProve());
        } else if (command.equalsIgnoreCase("Cancel")) {
            returnValue = CONCLUSION_CANCEL;
            this.setVisible(false);
        } else if (command.equalsIgnoreCase("Clear")) {
            this.resetAllItem();
            Pane2.clear();
        }
    }

    /**
     * Displays the dialog and returns the result.
     *
     * @param s the string to select in the combo box
     * @return the return value indicating the result of the dialog
     */
    public int showDialog(String s) {
        if (model == false)
            this.setPoints(gxInstance.dp.getPointList());

        bt.setSelectedIndex(-1);
        resetAllItem();
        this.setLocation((int) (this.getOwner().getX()), (int) (this.getOwner().getY() + this.getOwner().getHeight() / 3));
        this.pack();
        this.setVisible(true);
        this.setSize(320, 200);
        bt.setSelectedItem(s);
        ltext.setText("");
        return returnValue;
    }

    /**
     * Sets the visibility of the first set of combo boxes based on the number of items.
     *
     * @param num the number of items to be visible
     */
    private void setVisibleBox1(int num) {
        int k = num / 2;
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox obj = (JComboBox) vlist.get(i);
            if (i < k)
                obj.setEnabled(true);
            else
                obj.setEnabled(false);
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox obj = (JComboBox) vlist1.get(i);
            if (i < k)
                obj.setEnabled(true);
            else
                obj.setEnabled(false);
        }
    }

    /**
     * Sets the visibility of the second set of combo boxes based on the number of items.
     *
     * @param num the number of items to be visible
     */
    private void setVisibleBox(int num) {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox obj = (JComboBox) vlist.get(i);
            if (i < num)
                obj.setEnabled(true);
            else
                obj.setEnabled(false);
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox obj = (JComboBox) vlist1.get(i);
            obj.setEnabled(false);
        }
    }

    /**
     * Returns the selected point from the combo boxes.
     *
     * @param id1 the ID of the combo box set (0 or 1)
     * @param id2 the index of the combo box within the set
     * @return the selected point
     */
    private CPoint vspt(int id1, int id2) {
        if (id1 == 0) {
            return (CPoint) ((JComboBox) vlist.get(id2)).getSelectedItem();
        } else {
            return (CPoint) ((JComboBox) vlist1.get(id2)).getSelectedItem();
        }
    }

    /**
     * Checks if the selected item is valid.
     *
     * @return true if the selected item is valid, false otherwise
     */
    public boolean checkValid() {
        int id = bt.getSelectedIndex();
        return checkValid(id);
    }

    /**
     * Checks if the selected item with the given ID is valid.
     *
     * @param id the ID of the selected item
     * @return true if the selected item is valid, false otherwise
     */
    public boolean checkValid(int id) {
        switch (id) {
            case 0:  // COLLINEAR
                return DrawBase.check_Collinear(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 1:  // PARALLEL
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 2:  // PERPENDICULAR
                return DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 3:  // MIDPOINT
                return DrawBase.check_mid(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 4:  // CYCLIC
                return DrawBase.check_cyclic(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3));
            case 5:  // EQDISTANCE
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 6:
                return DrawBase.check_eqangle(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3), vspt(1, 0), vspt(1, 1), vspt(1, 2), vspt(1, 3));
            case 7:  //Similiar Triangle
                return DrawBase.check_simtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 8:  //Congruent Triangle
                return DrawBase.check_congtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 9:  //Equilateral Triangle
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2)) &&
                        DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case 10:
                return DrawBase.check_bisect(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 11: //Tangent
                return DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 1));
            case 12: { //Eq_product
                double d1 = DrawBase.sdistance(vspt(0, 0), vspt(0, 1));
                double d2 = DrawBase.sdistance(vspt(0, 2), vspt(0, 3));
                double d3 = DrawBase.sdistance(vspt(1, 0), vspt(1, 1));
                double d4 = DrawBase.sdistance(vspt(1, 2), vspt(1, 3));
                return Math.abs(d1 * d2 - d3 * d4) < CMisc.ZERO;
            }
            case 13: { //Ratio
                int t1 = getRatioValue(1);
                int t2 = getRatioValue(2);
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1), t1, t2);
            }
        }
        return false;
    }

    /**
     * Returns the ratio value from the combo box.
     *
     * @param id the ID of the combo box (1 or 2)
     * @return the ratio value
     */
    private int getRatioValue(int id) {
        if (id == 1) {
            String s1 = bx1.getSelectedItem().toString();
            int t1 = Integer.parseInt(s1);
            return t1;
        } else {
            String s2 = bx2.getSelectedItem().toString();
            int t2 = Integer.parseInt(s2);
            return t2;
        }
    }

    /**
     * Returns the conclusion based on the selected points and type.
     *
     * @return the conclusion object
     */
    public Cons getProve() {
        JComboBox box1 = (JComboBox) vlist.get(0);
        if (box1.getItemCount() == 0) return null;
        int nd = bt.getSelectedIndex();
        if (nd < 0 || nd >= ts.length)
            return null;

        Object obj = ts[nd];

        if (obj == null) return null;
        int p = CST.getClu_D(obj.toString());

        if (p == 0) return null;

        Cons c = new Cons(p);
        int index = 0;
        String sn = "";
        if (p == Gib.CO_NANG || p == Gib.CO_NSEG) {
            sn = Pane2.getValue();
            c.setText(sn);
            return c;
        }

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                Object o = b.getSelectedItem();
                c.add_pt(o, index++);
                sn += " " + o;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                Object o = b.getSelectedItem();
                c.add_pt(o, index++);
                sn += " " + o;
            }
        }

        if (p == Gib.CO_RATIO) {
            Object o1 = bx1.getSelectedItem();
            Object o2 = bx2.getSelectedItem();
            c.add_pt(o1, 4);
            c.add_pt(o2, 5);
            sn += " " + o1 + " " + o2;
        } else if (p == Gib.CO_TANG) {
            Object o1 = bx1.getSelectedItem();
            c.add_pt(o1, 4);
            sn += " " + o1;
        }
        c.set_conc(true);
        return c;
    }

    class condPane extends JPanel implements ActionListener {

        JComboBox[] bx = new JComboBox[3];
        JTextField field = new JTextField(1);
        JButton badd = new JButton(GExpert.getLanguage("Add"));
        int status = 0; //0: segment 1:angle.
        JButton b1, b2, b3, b4, b5;
        JLabel label;
        JPopupMenu mint;


        /**
         * Constructs a condPane object.
         * Initializes the layout, buttons, combo boxes, and other components.
         */
        public condPane() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            field.setFont(font);
            this.add(field);

            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            b1 = new JButton("+");
            b1.setFont(font);
            b2 = new JButton("-");
            b2.setFont(font);
            b3 = new JButton("*");
            b3.setFont(font);
            b4 = new JButton("=");
            b4.setFont(font);
            b5 = new JButton(GExpert.getLanguage("Number"));
            b5.setFont(font);
            p1.add(b1);
            p1.add(b2);
            p1.add(b3);
            p1.add(b4);
            p1.add(b5);
            b1.addActionListener(this);
            b2.addActionListener(this);
            b3.addActionListener(this);
            b4.addActionListener(this);
            b5.addActionListener(this);
            this.add(p1);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            label = new JLabel();
            p.add(label);
            bx[0] = new JComboBox();
            p.add(bx[0]);
            bx[1] = new JComboBox();
            p.add(bx[1]);
            bx[2] = new JComboBox();
            p.add(bx[2]);
            p.add(badd);
            this.add(p);
            badd.addActionListener(this);
            mint = new JPopupMenu();
            for (int i = 0; i <= 10; i++) {
                JMenuItem item = new JMenuItem(i + "");
                mint.add(item);
                item.addActionListener(this);
            }
            field.setFocusable(true);
        }

        /**
         * Clears the text field and resets the combo boxes.
         */
        public void clear() {
            field.setText("");
            bx[0].setSelectedIndex(-1);
            bx[1].setSelectedIndex(-1);
            bx[2].setSelectedIndex(-1);

            ltext1.setIcon(ic3);
            ltext1.setText("");
        }

        /**
         * Sets the status of the condPane.
         * Updates the visibility and enabled state of the combo boxes based on the status.
         *
         * @param s the status to set (0 for segment, 1 for angle)
         */
        public void setStatus(int s) {
            if (s == 0) {
                bx[2].setVisible(false);
                bx[2].setEnabled(false);
                label.setText("(" + GExpert.getLanguage("Segment") + " ");
            } else {
                bx[2].setVisible(true);
                bx[2].setEnabled(true);
                label.setText("(" + GExpert.getLanguage("Angle") + " ");
            }
            field.setText("");
            bx[0].setSelectedIndex(-1);
            bx[1].setSelectedIndex(-1);
            bx[2].setSelectedIndex(-1);
        }

        /**
         * Handles action events for the buttons and menu items.
         *
         * @param e the ActionEvent triggered by the user
         */
        public void actionPerformed(ActionEvent e) {
            Object o = e.getSource();
            if (o == b3) {
                addPtsText1();
                addText(" * ");
            } else if (o == b1) {
                addPtsText1();
                addText(" + ");
            } else if (o == b2) {
                addPtsText1();
                addText(" - ");
            } else if (o == b4) {
                addPtsText1();
                addText(" = ");
            } else if (o == b5) {
                mint.show(b5, 0, b5.getHeight());
            } else if (o == badd) {
                addText(getPts());
            } else {
                String s = e.getActionCommand();
                addText(s);
            }
        }

        /**
         * Adds the selected points text to the field if the add button is enabled.
         */
        public void addPtsText1() {
            if (!badd.isEnabled())
                return;
            addText(getPts());
        }

        /**
         * Appends the specified text to the field and unselects the combo boxes.
         *
         * @param s the text to add
         */
        public void addText(String s) {
            field.setText(field.getText() + s);
            unselect();
        }

        /**
         * Sets the points in the combo boxes.
         *
         * @param v the vector of points to set
         */
        public void setPoints(Vector v) {
            for (int i = 0; i < 3; i++) {
                bx[i].removeAllItems();
                for (int j = 0; j < v.size(); j++) {
                    Object obj = v.get(j);
                    bx[i].addItem(obj);
                }
                bx[i].setSelectedIndex(-1);
            }
        }

        /**
         * Selects a point in the combo boxes.
         *
         * @param o the point to select
         */
        public void selectAPoint(Object o) {
            boolean set = false;
            for (int i = 0; i < 3; i++) {
                Object obj = bx[i].getSelectedItem();
                if (obj == null && bx[i].isEnabled()) {
                    if (set == false) {
                        bx[i].setSelectedItem(o);
                        set = true;
                    } else {
                        return;
                    }
                }
            }
            badd.setEnabled(true);
        }

        /**
         * Unselects all combo boxes and disables the add button.
         */
        public void unselect() {
            for (int i = 0; i < 3; i++) {
                bx[i].setSelectedIndex(-1);
            }
            badd.setEnabled(false);
        }

        /**
         * Returns the selected points as a string.
         *
         * @return the selected points
         */
        public String getPts() {
            String s = "";
            for (int i = 0; i < 3 && bx[i].isEnabled(); i++) {
                Object obj = bx[i].getSelectedItem();
                s += obj;
            }
            return s;
        }

        /**
         * Returns the value of the text field.
         *
         * @return the value of the text field
         */
        public String getValue() {
            return field.getText();
        }
    }

}
