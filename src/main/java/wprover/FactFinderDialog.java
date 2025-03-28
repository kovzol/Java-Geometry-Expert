package wprover;

import gprover.CClass;
import gprover.Prover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * FactFinderDialog is a dialog that allows users to search for geometric facts based on selected points and types.
 * It extends JBaseDialog and implements ActionListener and ItemListener interfaces.
 */
public class FactFinderDialog extends JBaseDialog implements ActionListener, ItemListener {

    final private static String[] S =
            {"segment", "midpoint", "circle", "parallel line", "perpendicular line", "angle", "triangle"};
    // TODO. This part could be improved by adding other sets in case.

    private GExpert gxInstance;
    private JLabel label;
    private JComboBox bs, b1, b2, b3;
    private JButton bsearch, breset, bcancel;
    private int find_type;
    private DefaultListModel model;
    private JList list;


    /**
     * Constructs a FactFinderDialog with the specified owner, type, and title.
     *
     * @param owner the GExpert instance that owns this dialog
     * @param type  the type of fact to find
     * @param title the title of the dialog
     */
    public FactFinderDialog(GExpert owner, int type, String title) {
        super(owner.getFrame(), title);
        gxInstance = owner;

        find_type = type;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String[] S_T = new String[S.length];
        for (int i = 0; i < S.length; i++) {
            S_T[i] = GExpert.getLanguage(S[i]);
        }

        bs = new JComboBox(S_T);
        bs.addItemListener(this);

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p1.add(new JLabel(GExpert.getLanguage("Type") + " "));
        p1.add(bs);
        panel.add(p1);
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        b1 = new JComboBox();
        b1.addItemListener(this);
        b2 = new JComboBox();
        b2.addItemListener(this);
        b3 = new JComboBox();
        b3.addItemListener(this);
        p2.add(b1);
        p2.add(b2);
        p2.add(b3);
        p2.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Please choose")));
        panel.add(p2);
        label = new JLabel();
        panel.add(label);
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bsearch = new JButton(GExpert.getLanguage("Search"));
        bsearch.addActionListener(this);
        bcancel = new JButton(GExpert.getLanguage("Close"));
        bcancel.addActionListener(this);
        breset = new JButton(GExpert.getLanguage("Reset"));
        breset.addActionListener(this);

        p3.add(bsearch);
        // p3.add(breset); // This seems unimplemented yet. TODO.
        p3.add(bcancel);
        panel.add(p3);
        model = new DefaultListModel();
        list = new JList(model);
        list.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Object o = list.getSelectedValue();
                if (o != null)
                    gxInstance.getpprove().high_light_a_fact((CClass) o);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        JScrollPane p = new JScrollPane(list);
        panel.add(p);
        this.add(panel);
        this.setSize(300, 400);
    }

    /**
     * Sets the points in the combo boxes.
     *
     * @param v the vector of points to set
     */
    public void setPoints(Vector v) {
        b1.removeAllItems();
        b2.removeAllItems();
        b3.removeAllItems();
        for (int i = 0; i < v.size(); i++) {
            Object o = v.get(i);
            b1.addItem(o);
            b2.addItem(o);
            b3.addItem(o);
        }
    }

    /**
     * Shows the dialog.
     */
    public void showDialog() {
        reselect();
        bs.setSelectedIndex(-1);
        this.setVisible(true);
    }

    /**
     * Reselects the combo boxes based on the find type.
     */
    private void reselect() {
        if (find_type == 0 || find_type == 3 || find_type == 4 || find_type == 6)
            b3.setEnabled(false);
        else
            b3.setEnabled(true);

        bsearch.setEnabled(false);

        b1.setSelectedIndex(-1);
        b2.setSelectedIndex(-1);
        b3.setSelectedIndex(-1);
    }

    /**
     * Handles action events for the buttons.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == bcancel) {
            this.setVisible(false);
            return;
        }

        String s1, s2, s3;
        s1 = s2 = s3 = null;
        Object o1 = b1.getSelectedItem();
        Object o2 = b2.getSelectedItem();
        Object o3 = b3.getSelectedItem();
        s1 = o1.toString();
        s2 = o2.toString();
        if (o3 != null)
            s3 = o3.toString();

        Vector v = null;
        if (src == bsearch) {
            v = Prover.search_a_fact(find_type, s1, s2, s3);
            if (v.size() == 0) {
                JOptionPane.showMessageDialog(gxInstance, GExpert.getLanguage("We could not find anything!"),
                        GExpert.getLanguage("No result"), JOptionPane.WARNING_MESSAGE);
            } else {

                model.clear();
                for (int i = 0; i < v.size(); i++)
                    model.addElement(v.get(i));
                if (v.size() == 1) {
                    gxInstance.getpprove().high_light_a_fact((CClass) v.get(0));
                    list.setSelectedIndex(0);
                }
            }

        }
    }

    /**
     * Selects a point in the combo boxes.
     *
     * @param b the point to select
     */
    public void selectAPoint(Object b) {
        if (b1.getSelectedIndex() == -1)
            b1.setSelectedItem(b);
        else if (b2.getSelectedIndex() == -1)
            b2.setSelectedItem(b);
        else if (b3.getSelectedIndex() == -1)
            b3.setSelectedItem(b);
    }

    /**
     * Handles item state changes for the combo boxes.
     *
     * @param e the item event
     */
    public void itemStateChanged(ItemEvent e) {
        JComboBox bx = (JComboBox) e.getSource();
        if (bx == bs) {
            find_type = bs.getSelectedIndex();
            reselect();
        } else {
            if ((b1.getSelectedIndex() != -1 || !b1.isEnabled())
                    && (b2.getSelectedIndex() != -1 || !b2.isEnabled())
                    && (b3.getSelectedIndex() != -1 || !b3.isEnabled()))
                bsearch.setEnabled(true);
            else
                bsearch.setEnabled(false);
        }
    }
}
