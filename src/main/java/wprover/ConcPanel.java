package wprover;

import gprover.Cm;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

/**
 * ConcPanel is a JPanel that provides a user interface for selecting geometric assertions.
 * It allows users to select points and check the validity of assertions based on the selected points.
 */
public class ConcPanel extends JPanel implements ActionListener, ItemListener {

    public static ImageIcon icon_Right = GExpert.createImageIcon("images/dtree/right.gif");
    public static ImageIcon icon_Wrong = GExpert.createImageIcon("images/dtree/wrong.gif");
    public static ImageIcon icon_Question = GExpert.createImageIcon("images/dtree/question.gif");

    private JComboBox bt;
    private Vector vlist = new Vector();
    private Vector vlist1 = new Vector();
    private JLabel ltext1;
    private JButton bbok, bbcancel;
    private JPanel bpanel;
    private TreeCellAssertPanel asspane, asspane_temp;

    private GExpert gxInstance;
    private MAssertion ass, ass_show, ass_temp;
    private JPanel contentPane;

    private MProveInputPanel ipanel = null;

    /**
     * Constructs a ConcPanel with the specified GExpert instance.
     * Initializes the panel and sets the selected index of the combo box to -1.
     *
     * @param gx the GExpert instance
     */
    public ConcPanel(GExpert gx) {
        gxInstance = gx;
        init();
        bt.setSelectedIndex(-1);
    }

    /**
     * Constructs a ConcPanel with the specified GExpert instance and MProveInputPanel.
     * Calls the other constructor and sets the input panel.
     *
     * @param gx the GExpert instance
     * @param ipanel the MProveInputPanel instance
     */
    public ConcPanel(GExpert gx, MProveInputPanel ipanel) {
        this(gx);
        this.ipanel = ipanel;
    }

    /**
     * Initializes the ConcPanel.
     * Sets up the layout, combo boxes, panels, and other components.
     */
    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        String[] cStringsT = new String[MAssertion.cStrings.length];
        // Create the translations first:
        for (int i = 0; i < MAssertion.cStrings.length; i++) {
            cStringsT[i] = GExpert.getLanguage(MAssertion.cStrings[i]);
        }
        // Use the translations:
        bt = new JComboBox(cStringsT) {
            public Dimension getMaximumSize() {
                return bt.getPreferredSize();
            }
        };

        bt.setMaximumRowCount(40);
        this.add(bt);
        bt.addItemListener(this);
        JPanel topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel topPane1 = new JPanel();
        topPane1.setLayout(new FlowLayout(FlowLayout.CENTER));

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
        contentPane.add(topPane);
        contentPane.add(topPane1);

        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.X_AXIS));

        ltext1 = new JLabel();
        setLtext1Value(true);
        asspane = new TreeCellAssertPanel();

        textPane.add(Box.createHorizontalStrut(3));
        textPane.add(asspane);
        textPane.add(Box.createHorizontalGlue());
        textPane.add(ltext1);
        contentPane.add(textPane);

        bpanel = new JPanel();
        bpanel.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Do you mean...")));
        bpanel.setLayout(new BoxLayout(bpanel, BoxLayout.Y_AXIS));

        asspane_temp = new TreeCellAssertPanel();
        bpanel.add(asspane_temp);
        bbok = new JButton(GExpert.getLanguage("Yes"));
        bbok.addActionListener(this);
        bbcancel = new JButton(GExpert.getLanguage("Cancel"));
        bbcancel.addActionListener(this);
        JPanel pt = new JPanel();
        pt.setLayout(new BoxLayout(pt, BoxLayout.X_AXIS));
        pt.add(Box.createHorizontalGlue());
        bbok.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bbcancel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pt.add(bbok);
        pt.add(bbcancel);
        bpanel.add(pt);
        bpanel.setVisible(false);
        contentPane.add(bpanel);

        this.add(contentPane);
        this.resetAllItem();
        ass_show = new MAssertion(0);
    }

    /**
     * Sets the selected index of the combo box and revalidates the state.
     *
     * @param k the index to set
     */
    public void setTypeSelection(int k) {
        bt.setSelectedIndex(k);
        this.revalidateValidState();
    }

    /**
     * Sets the user object for the panel.
     * Resets all items and updates the combo box and points based on the provided assertion.
     *
     * @param as the MAssertion object to set
     */
    public void setUserObject(MAssertion as) {
        this.resetAllItem();
        ass = as;

        if (as != null) {
            bt.setSelectedIndex(as.getAssertionType());
            for (int i = 0; i < as.getobjNum(); i++)
                this.selectAPoint((CPoint) as.getObject(i));
        } else {
            bt.setSelectedIndex(-1);
        }
    }

    /**
     * Returns the user object for the panel.
     * Creates or updates the assertion object based on the selected points.
     *
     * @return the MObject representing the assertion
     */
    public MObject getUserObject() {
        if (ass == null)
            ass = new MAssertion(bt.getSelectedIndex());
        else
            ass.setAssertionType(bt.getSelectedIndex());
        ass.clearObjects();
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.isVisible() && b.getSelectedIndex() >= 0) {
                ass.addObject((CPoint) b.getSelectedItem());
            } else
                break;
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.isVisible() && b.getSelectedIndex() >= 0) {
                ass.addObject((CPoint) b.getSelectedItem());
            } else
                break;
        }
        return ass;
    }

    /**
     * Updates the points in the panel based on the current point list from the GExpert instance.
     */
    public void update() {
        this.setPoints(gxInstance.dp.getPointList());
    }

    /**
     * Sets the value of the ltext1 label based on the given boolean.
     *
     * @param t the boolean value to set
     */
    private void setLtext1Value(boolean t) {
        if (t) {
            ltext1.setText("");
            ltext1.setIcon(icon_Right);
        } else {
            ltext1.setText("");
            ltext1.setIcon(icon_Wrong);
        }
    }

    /**
     * Sets the points in the combo boxes based on the provided vector of points.
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
    }

    /**
     * Returns the number of points left to be selected.
     *
     * @return the number of points left to be selected
     */
    private int ptLeftTobeSelect() {
        int n = this.getStatePointsCount();

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                n--;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() >= 0) {
                n--;
            }
        }
        return n;
    }

    /**
     * Revalidates the state of the panel based on the current input.
     * Checks if the input is finished and updates the validity state and combo box accordingly.
     */
    public void revalidateValidState() {
        if (inputFinished()) {
            boolean v = checkValid();
            setLtext1Value(v);
            if (v)
                bt.setEnabled(true);
            if (v == false) {
                showCorrentOrder();
            }
        } else
            ltext1.setIcon(icon_Question);
    }

    /**
     * Handles item state changes for the combo boxes.
     * Resets all items, updates the combo box and points, and revalidates the state.
     *
     * @param e the ItemEvent triggered by the user
     */
    public void itemStateChanged(ItemEvent e) {
        if (!this.isVisible()) return;

        Object source = e.getSource();
        if (source == bt) {
            this.resetAllItem();
            int id = bt.getSelectedIndex();
            setItemChanged(id);
            if (id == bt.getItemCount() - 1) {
            }
        }
        showTipText();
        if (inputFinished()) {
            createAssertion();
            boolean v = checkValid();
            setLtext1Value(v);
            asspane.setAssertion(ass_show);
            if (v)
                bt.setEnabled(true);
            if (v == false) {
                showCorrentOrder();
            }
        } else
            ltext1.setIcon(icon_Question);

        updateBState();
    }

    /**
     * Updates the state of the button in the input panel based on the current selection.
     * If the input panel is not null and the input is finished or the selected index is CONVEX,
     * it sets the button state to true, otherwise sets it to false.
     */
    public void updateBState() {
        if (ipanel != null) {
            if (inputFinished() || bt.getSelectedIndex() == MAssertion.CONVEX)
                ipanel.setBState(true);
            else
                ipanel.setBState(false);
        }
    }

    /**
     * Creates an assertion based on the selected points.
     * Initializes or updates the assertion object and adds selected points to it.
     *
     * @return true if the assertion is valid, false otherwise
     */
    private boolean createAssertion() {
        if (ass_show == null)
            ass_show = new MAssertion(bt.getSelectedIndex());
        else
            ass_show.setAssertionType(bt.getSelectedIndex());

        ass_show.clearObjects();

        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.isVisible()) ass_show.addObject((CPoint) b.getSelectedItem());
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.isVisible()) ass_show.addObject((CPoint) b.getSelectedItem());
        }
        return ass_show.checkValid();
    }

    /**
     * Checks if the input is finished by verifying that all enabled combo boxes have a selected item.
     *
     * @return true if the input is finished, false otherwise
     */
    private boolean inputFinished() {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                return false;
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Displays a tip text indicating the number of points left to be selected.
     * Updates the assertion panel with the appropriate message.
     */
    private void showTipText() {
        int n = ptLeftTobeSelect();

        if (n == 0) {
        } else {
            if (n > 0)
                asspane.setText("(" + GExpert.getTranslationViaGettext("{0} points left", n + "") + ")");
            else
                asspane.setText(GExpert.getLanguage("Please select"));
            asspane.repaint();
        }
    }

    /**
     * Selects a point in the combo boxes.
     * Sets the selected item in the first available enabled combo box.
     *
     * @param p the point to select
     */
    public void selectAPoint(CPoint p) {
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
        asspane.repaint();
    }

    /**
     * Resets all combo boxes by setting their selected index to -1.
     */
    private void resetAllItem() {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            b.setSelectedIndex(-1);
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            b.setSelectedIndex(-1);
        }
    }

    /**
     * Returns the number of points required for the selected assertion type.
     *
     * @return the number of points required
     */
    private int getStatePointsCount() {
        switch (bt.getSelectedIndex()) {
            case MAssertion.COLL:
            case MAssertion.MID:
            case MAssertion.R_TRIANGLE:
            case MAssertion.ISO_TRIANGLE:
            case MAssertion.R_ISO_TRIANGLE:
            case MAssertion.BETWEEN:
            case MAssertion.EQ_TRIANGLE:
                return 3;
            case MAssertion.PARA:
            case MAssertion.PERP:
            case MAssertion.EQDIS:
            case MAssertion.CYCLIC:
            case MAssertion.DISLESS:
            case MAssertion.PERPBISECT:
            case MAssertion.PARALLELOGRAM:
                return 4;
            case MAssertion.EQANGLE:
            case MAssertion.SIM:
            case MAssertion.CONG:
            case MAssertion.ANGLESS:
            case MAssertion.CONCURRENT:
                return 6;
        }
        return -1;
    }

    /**
     * Sets the visibility and enabled state of the combo boxes based on the selected assertion type.
     *
     * @param id the selected assertion type
     */
    private void setItemChanged(int id) {
        switch (id) {
            case MAssertion.COLL:
            case MAssertion.MID:
            case MAssertion.R_TRIANGLE:
            case MAssertion.ISO_TRIANGLE:
            case MAssertion.R_ISO_TRIANGLE:
            case MAssertion.EQ_TRIANGLE:
                this.setVisibleBox(3);
                break;

            case MAssertion.PARA:
            case MAssertion.PERP:
            case MAssertion.EQDIS:
            case MAssertion.DISLESS:
            case MAssertion.PERPBISECT:
            case MAssertion.OPPOSITE_SIDE:
            case MAssertion.SAME_SIDE:
                this.setVisibleBox1(4);
                break;
            case MAssertion.CYCLIC:
            case MAssertion.PARALLELOGRAM:
            case MAssertion.TRAPEZOID:
            case MAssertion.RECTANGLE:
            case MAssertion.SQUARE:
                this.setVisibleBox(4);
                break;
            case MAssertion.EQANGLE:
            case MAssertion.SIM:
            case MAssertion.CONG:
            case MAssertion.ANGLESS:
            case MAssertion.CONCURRENT:
                this.setVisibleBox1(6);
                break;
            case MAssertion.ANGLE_INSIDE:
            case MAssertion.ANGLE_OUTSIDE:
            case MAssertion.TRIANGLE_INSIDE:
                this.setVisibleBox2(3);
                break;
            case MAssertion.BETWEEN:
                this.setVisibleBox2(2);
                break;
            case MAssertion.PARA_INSIDE:
                this.setVisibleBox2(4);
                break;
            case MAssertion.CONVEX:
                this.setVisibleBox1(8);
                break;
            default:
                CMisc.print("massertion " + id + " not found");
                break;
        }
    }

    /**
     * Handles action events for the buttons.
     * Resets all items and updates the assertion based on the temporary assertion.
     *
     * @param e the ActionEvent triggered by the user
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Object obj = e.getSource();
        if (obj == bbok) {
            this.resetAllItem();
            ass.clearObjects();
            ass.addAll(ass_temp);
            int n = ass_temp.getobjNum();
            for (int i = 0; i < n; i++) {
                CPoint pt = (CPoint) ass_temp.getObject(i);
                ass.addObject(pt);
                this.selectAPoint(pt);
            }
        } else {
        }
        bpanel.setVisible(false);
    }

    /**
     * Sets the visibility and enabled state of the combo boxes based on the specified number.
     *
     * @param num the number of combo boxes to be visible and enabled
     */
    private void setVisibleBox2(int num) {
        int k = 1;
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox obj = (JComboBox) vlist.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }

        k = num;

        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox obj = (JComboBox) vlist1.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
    }

/**
     * Sets the visibility and enabled state of the first set of combo boxes based on the specified number.
     *
     * @param num the number of combo boxes to be visible and enabled
     */
    private void setVisibleBox1(int num) {
        int k = num / 2;
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox obj = (JComboBox) vlist.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox obj = (JComboBox) vlist1.get(i);
            if (i < k) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
    }

    /**
     * Sets the visibility and enabled state of the combo boxes based on the specified number.
     *
     * @param num the number of combo boxes to be visible and enabled
     */
    private void setVisibleBox(int num) {
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox obj = (JComboBox) vlist.get(i);
            if (i < num) {
                obj.setEnabled(true);
                obj.setVisible(true);
            } else {
                obj.setEnabled(false);
                obj.setVisible(false);
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox obj = (JComboBox) vlist1.get(i);
            obj.setEnabled(false);
            obj.setVisible(false);
        }
    }

    /**
     * Returns the selected item from the specified combo box as a string.
     *
     * @param id1 the index of the combo box list (0 for vlist, 1 for vlist1)
     * @param id2 the index of the combo box within the list
     * @return the selected item as a string, or an empty string if the index is out of bounds
     */
    private String vs(int id1, int id2) {
        if (id1 == 0) {
            JComboBox box = ((JComboBox) vlist.get(id2));
            if (box.getItemCount() <= id2)
                return "";
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return obj.toString();
            }

        } else {
            JComboBox box = ((JComboBox) vlist1.get(id2));
            if (box.getItemCount() <= id2)
                return "";
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return obj.toString();
            }
        }
    }

    /**
     * Returns the selected item from the specified combo box as a CPoint.
     *
     * @param id1 the index of the combo box list (0 for vlist, 1 for vlist1)
     * @param id2 the index of the combo box within the list
     * @return the selected item as a CPoint, or null if the index is out of bounds
     */
    private CPoint vspt(int id1, int id2) {
        if (id1 == 0) {
            JComboBox box = ((JComboBox) vlist.get(id2));
            if (box.getItemCount() <= id2)
                return null;
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return (CPoint) obj;
            }
        } else {
            JComboBox box = ((JComboBox) vlist1.get(id2));
            if (box.getItemCount() <= id2)
                return null;
            else {
                Object obj = box.getSelectedItem();
                if (obj == null)
                    return null;
                else
                    return (CPoint) obj;
            }
        }
    }

    /**
     * Checks the validity of the selected geometric assertion based on the selected points.
     *
     * @return true if the assertion is valid, false otherwise
     */
    private boolean checkValid() {
        int id = bt.getSelectedIndex();
        if (!inputFinished()) return false;
        switch (id) {
            case 0:
                return DrawBase.check_Collinear(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 1:
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 2:
                return DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 3:
                return DrawBase.check_mid(vspt(0, 0), vspt(0, 1), vspt(0, 2));
            case 4:
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 5:
                return DrawBase.check_cyclic(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3));
            case 6:
                return DrawBase.check_eqangle(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 7:
                return DrawBase.check_congtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 8:
                return DrawBase.check_simtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 9:
                return DrawBase.check_distance_less(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case 10:
                return DrawBase.check_angle_less(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case 11:
                // return drawbase.check_concurrent();
            case 12:
                return DrawBase.check_perp(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(0, 1));
            case 13:
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawBase.check_para(vspt(0, 0), vspt(0, 3), vspt(0, 1), vspt(0, 2));
            case MAssertion.R_TRIANGLE:
                return DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case MAssertion.ISO_TRIANGLE:
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case MAssertion.R_ISO_TRIANGLE:
                return DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2))
                        && DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case MAssertion.EQ_TRIANGLE:
                return DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2))
                        && DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2));
            case MAssertion.TRAPEZOID:
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3));
            case MAssertion.RECTANGLE:
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2));
            case MAssertion.SQUARE:
                return DrawBase.check_para(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(0, 3))
                        && DrawBase.check_perp(vspt(0, 0), vspt(0, 1), vspt(0, 1), vspt(0, 2))
                        && DrawBase.check_eqdistance(vspt(0, 0), vspt(0, 1), vspt(0, 0), vspt(0, 2));
            case MAssertion.BETWEEN:
                return DrawBase.check_between(vspt(0, 0), vspt(1, 0), vspt(1, 1));
            case MAssertion.ANGLE_INSIDE:
                return DrawBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        && DrawBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case MAssertion.ANGLE_OUTSIDE:
                return !(DrawBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 2), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        && DrawBase.check_angle_less(vspt(0, 0), vspt(1, 1), vspt(1, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2)));
            case MAssertion.TRIANGLE_INSIDE:
                return DrawBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2));
            case MAssertion.PARA_INSIDE:
                return DrawBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 1), vspt(1, 2))
                        || DrawBase.check_triangle_inside(vspt(0, 0), vspt(1, 0), vspt(1, 2), vspt(1, 3));
            case MAssertion.OPPOSITE_SIDE:
                return !DrawBase.check_same_side(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case MAssertion.SAME_SIDE:
                return DrawBase.check_same_side(vspt(0, 0), vspt(0, 1), vspt(1, 0), vspt(1, 1));
            case MAssertion.CONVEX:
                return true;
        }
        return true;
    }

    /**
     * Returns the proof statement for the selected geometric assertion.
     *
     * @return the proof statement as a string, or "Not Yet Supported Conclusion" if the assertion type is not supported
     */
    public String getProve() {
        JComboBox box1 = (JComboBox) vlist.get(0);
        if (box1.getItemCount() == 0) return "";
        if (!this.inputFinished()) return "";

        int id = bt.getSelectedIndex();

        switch (id) {
            case MAssertion.COLL:
                return Cm.PC_COLL + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + ";";
            case MAssertion.PARA:
                return Cm.PC_PARA + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.PERP:
                return Cm.PC_PERP + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.MID:
                return Cm.PC_MIDP + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + ";";
            case MAssertion.CYCLIC:
                return Cm.PC_CYCLIC + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + ";";
            case MAssertion.EQDIS:
                return Cm.PC_CONG + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.EQANGLE:
                return Cm.PC_ACONG + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + " " + vs(1, 3) + ";";
            case MAssertion.SIM:
                return Cm.PC_STRI + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + ";";
            case MAssertion.CONG:
                return Cm.PC_CTRI + " " + vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(1, 0) + " " + vs(1, 1) + " " + vs(1, 2) + ";";
        }
        return "Not Yet Supported Conclusion";
    }

    /**
     * Shows the correct order of points for the selected geometric assertion.
     */
    private void showCorrentOrder() {
        JComboBox box1 = (JComboBox) vlist.get(0);
        if (box1.getItemCount() == 0) return;
        if (!this.inputFinished()) return;

        int id = bt.getSelectedIndex();

        boolean t = false;

        if (id == MAssertion.SIM || id == MAssertion.CONG) {
            int i, j, k;
            i = j = k = 0;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (i != j)
                        for (k = 0; k < 3; k++) {
                            if (i != k && j != k) {
                                if (id == MAssertion.SIM)
                                    t = DrawBase.check_simtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, i), vspt(1, j), vspt(1, k));
                                else if (id == MAssertion.CONG)
                                    t = DrawBase.check_congtri(vspt(0, 0), vspt(0, 1), vspt(0, 2), vspt(1, i), vspt(1, j), vspt(1, k));
                                if (t)
                                    break;
                            }
                        }
                    if (t) break;
                }
                if (t) break;
            }
            if (t) {
                if (ass_temp == null)
                    ass_temp = new MAssertion(bt.getSelectedIndex());
                ass_temp.clearObjects();
                ass_temp.addObject(vspt(0, 0));
                ass_temp.addObject(vspt(0, 1));
                ass_temp.addObject(vspt(0, 2));
                ass_temp.addObject(vspt(1, i));
                ass_temp.addObject(vspt(1, j));
                ass_temp.addObject(vspt(1, k));
                asspane_temp.setAssertion(ass_temp);
                bpanel.setVisible(true);
            }
        }
    }

    /**
     * Cancels the current assertion and resets all combo boxes.
     */
    public void cancel() {
        ass = null;
        this.resetAllItem();
    }

}
