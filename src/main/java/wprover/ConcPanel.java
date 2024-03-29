package wprover;

import gprover.Cm;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-7-5
 * Time: 13:25:43
 * To change this template use File | Settings | File Templates.
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

    public ConcPanel(GExpert gx) {
        gxInstance = gx;
        init();
        bt.setSelectedIndex(-1);
    }

    public ConcPanel(GExpert gx, MProveInputPanel ipanel) {
        this(gx);
        this.ipanel = ipanel;
    }


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

    public void setTypeSelection(int k) {
        bt.setSelectedIndex(k);
        this.revalidateValidState();
    }

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

    public void update() {
        this.setPoints(gxInstance.dp.getPointList());
    }

    private void setLtext1Value(boolean t) {
        if (t) {
            ltext1.setText("");
            ltext1.setIcon(icon_Right);
        } else {
            ltext1.setText("");
            ltext1.setIcon(icon_Wrong);
        }
    }

    public int getPointsCount() {
        JComboBox b = (JComboBox) vlist.get(0);
        return b.getItemCount();
    }

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

    public void updateBState() {
        if (ipanel != null) {
            if (inputFinished() || bt.getSelectedIndex() == MAssertion.CONVEX)
                ipanel.setBState(true);
            else
                ipanel.setBState(false);
        }
    }

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

    private boolean inputFinished() {
//        return 0 == ptLeftTobeSelect();
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
//        this.repaint();
    }

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
//                return drawbase.check_concurrent();
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


    public MAssertion getProveM() {
        int id = bt.getSelectedIndex();
        if (id < 0) return null;
        MAssertion ass = new MAssertion(id);
        for (int i = 0; i < vlist.size(); i++) {
            JComboBox b = (JComboBox) vlist.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                ass.addItem(b.getSelectedItem());
            }
        }
        for (int i = 0; i < vlist1.size(); i++) {
            JComboBox b = (JComboBox) vlist1.get(i);
            if (b.isEnabled() && b.getSelectedIndex() < 0) {
                ass.addItem(b.getSelectedItem());
            }
        }
        return ass;
    }

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

    public String getProveDescription() {
        JComboBox box1 = (JComboBox) vlist.get(0);
        if (box1.getItemCount() == 0) return "";
        if (!this.inputFinished()) return "";

        int id = bt.getSelectedIndex();

        switch (id) {
            case MAssertion.COLL:
                return vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " are collinear;";
            case MAssertion.PARA:
                return vs(0, 0) + " " + vs(0, 1) + " is Parallel to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.PERP:
                return vs(0, 0) + " " + vs(0, 1) + " is Perpendicular to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.MID:
                return vs(0, 0) + " is the midpoint of " + vs(0, 1) + " " + vs(0, 2) + ";";
            case MAssertion.CYCLIC:
                return vs(0, 0) + " " + vs(0, 1) + " " + vs(0, 2) + " " + vs(0, 3) + " are cyclic;";
            case MAssertion.EQDIS:
                return vs(0, 0) + " " + vs(0, 1) + " equals to " + vs(1, 0) + " " + vs(1, 1) + ";";
            case MAssertion.EQANGLE:
                return Cm.ANGLE_SIGN + vs(0, 0) + vs(0, 1) + vs(0, 2) + " = " + Cm.ANGLE_SIGN + vs(1, 0) + vs(1, 1) + vs(1, 2);

            case MAssertion.SIM:
                return "tri " + vs(0, 0) + vs(0, 1) + vs(0, 2) + " is similiar to " + "tri " + vs(1, 0) + vs(1, 1) + vs(1, 2);
            case MAssertion.CONG:
                return "tri " + vs(0, 0) + vs(0, 1) + vs(0, 2) + " is equal to " + "tri " + vs(1, 0) + vs(1, 1) + vs(1, 2);
        }
        return "Not Yet Supported Conclusion";
    }

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

    public void cancel() {
        ass = null;
        this.resetAllItem();
    }

}
