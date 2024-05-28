package wprover;

import UI.EntityButtonUI;
import gprover.Cons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2008-8-3
 * Time: 18:30:46
 * To change this template use File | Settings | File Templates.
 */

/**
 * The `AllSolutionDialog` class provides a dialog for displaying and interacting with
 * all possible solutions of a given problem within the GExpert application.
 */
public class AllSolutionDialog extends JBaseDialog implements ActionListener, WindowListener {

    GExpert gxInstance;
    JTextField field1, field2;
    JLabel labelx;
    JButton button, button1, button2, button3;
    double[] backup = new double[1024];
    ImageIcon cross, hook;


    ConcDialog dlg;
    Cons cns;

    Vector vblist, vlist;
    public int index;

    /**
     * Constructor for `AllSolutionDialog`.
     * Initializes the dialog with the necessary UI components and event listeners.
     *
     * @param gx The instance of GExpert.
     */
    public AllSolutionDialog(GExpert gx) {
        super(gx.getFrame());
        gxInstance = gx;
        this.setTitle(GExpert.getLanguage("All solutions"));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel();
        p1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p1.setLayout(new GridLayout(1, 3));
        JLabel l1 = new JLabel(GExpert.getLanguage("Total solutions:"));
        p1.add(l1);
        p1.add(Box.createHorizontalBox());
        field1 = new JTextField();
        field1.setEditable(false);
//        p1.add(Box.createHorizontalGlue());
        p1.add(field1);
        panel.add(p1);

        JPanel p2 = new JPanel();
        p2.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p2.setLayout(new GridLayout(1, 3));
        JLabel l2 = new JLabel(GExpert.getLanguage("Current Solution:"));
//        p2.add(Box.createHorizontalGlue());
        p2.add(l2);
        p2.add(Box.createHorizontalBox());
        field2 = new JTextField();
        field2.setEditable(false);
        p2.add(field2);
        panel.add(p2);
        EntityButtonUI ui = new EntityButtonUI();
        JButton b = new JButton(GExpert.createImageIcon("images/ptree/show_pred.gif"));
        JButton b1 = new JButton(GExpert.createImageIcon("images/ptree/addconc.gif"));
        JButton b2 = new JButton(GExpert.createImageIcon("images/other/arrow_down.gif"));
        JButton b3 = new JButton(GExpert.createImageIcon("images/other/step_forward.gif"));
        button = b;
        button1 = b1;
        button2 = b2;
        button3 = b3;
        button.addActionListener(this);
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        b.setUI(ui);
        b1.setUI(ui);
        b2.setUI(ui);
        b3.setUI(ui);

        JPanel p3 = new JPanel();
        p3.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        labelx = new JLabel();
        labelx.setForeground(Color.gray);
        labelx.setHorizontalTextPosition(JLabel.LEFT);

        p3.add(labelx);
        p3.add(Box.createHorizontalGlue());

        p3.add(b);
        p3.add(Box.createHorizontalStrut(4));
        p3.add(b1);
        p3.add(Box.createHorizontalStrut(4));
        p3.add(b2);
        p3.add(Box.createHorizontalStrut(4));
        p3.add(b3);

        panel.add(p3);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.getContentPane().add(panel);

        vblist = new Vector();
        vlist = new Vector();
        cross = GExpert.createImageIcon("images/ptree/cross.gif");
        hook = GExpert.createImageIcon("images/ptree/hook.gif");

        this.addWindowListener(this);
        this.pack();
    }

    /**
     * Sets the list of solutions to be displayed.
     *
     * @param v A vector containing the solutions.
     */
    public void setVlist(Vector v) {
        vblist.clear();
        vlist.clear();

        vblist.addAll(v);
        vlist.addAll(v);
        index = 0;
        update();
        gxInstance.dp.BackupParameter(backup, true);
    }

    /**
     * Automatically filters solutions based on certain criteria.
     */
    public void autoFiltered() {
        cns = gxInstance.getpprove().getConstructionTerm().getConclusion();
        if (cns != null) {
            dlg = new ConcDialog(gxInstance, GExpert.getLanguage("Filter"));
            dlg.setPoints(gxInstance.dp.getPointList());
            dlg.setCns(cns);
            button.setEnabled(true);

            button.setSelected(true);
            this.setFilted(true);
        }
    }

    /**
     * Sets the filter state and updates the list of solutions accordingly.
     *
     * @param r A boolean indicating whether to apply the filter.
     */
    public void setFilted(boolean r) {
        if (r == false) {
            vlist.clear();
            vlist.addAll(vblist);
            index = 0;
            update();
            gxInstance.dp.BackupParameter(backup, false);
        } else {
            vlist.clear();
            for (int i = 0; i < vblist.size(); i++) {
                double[] kk = (double[]) vblist.get(i);
                gxInstance.dp.setParameterValue(kk);
                if (dlg.checkValid())
                    vlist.add(kk);
            }
            index = 0;
            update();
            gxInstance.dp.BackupParameter(backup, false);
        }
        gxInstance.d.repaint();
    }

    /**
     * Updates the displayed information in the dialog.
     */
    public void update() {
        field1.setText(Integer.toString(vlist.size()));
        field2.setText(Integer.toString(index));
        if (cns != null)
            labelx.setText(cns.toSString());
        if (dlg != null && cns != null) {
            boolean r = dlg.checkValid();
            if (r)
                labelx.setIcon(hook);
            else labelx.setIcon(cross);
            button.setEnabled(true);
//            if (!button.isSelected()) {
//                labelx.setText("");
//                labelx.setIcon(null);
//            }
        } else
            button.setEnabled(false);

    }


    /**
     * Handles action events from the buttons.
     *
     * @param e The action event.
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == button3) {
                index++;
                int num = vlist.size();
                if (num == 0)
                    return;

                if (index < 0)
                    return;
                else if (index > num)
                    index = index % num;

                if (index <= 0)
                    return;
                double[] kk = (double[]) vlist.get(index - 1);
                gxInstance.dp.setParameterValue(kk);
                update();
                gxInstance.d.repaint();
            } else if (src == button2) {
                if (index <= 0)
                    return;
                index--;

                if (index <= 0)
                    return;
                if (index <= 0)
                    return;

                double[] kk = (double[]) vlist.get(index - 1);
                gxInstance.dp.setParameterValue(kk);
                update();
                gxInstance.d.repaint();
            } else if (src == button1) {
                dlg = new ConcDialog(gxInstance, GExpert.getLanguage("Filter"));
                dlg.setPoints(gxInstance.dp.getPointList());
                dlg.changeBOKListener(this);
                dlg.showDialog("");
            } else if (src == button) {
                boolean r = button.isSelected();
                button.setSelected(!r);
                setFilted(!r);

            } else {
                String s = e.getActionCommand();
                if (s.equalsIgnoreCase("OK")) {
                    cns = dlg.getProve();
                    dlg.setVisible(false);
                    update();
                }
            }
        } catch (NullPointerException ee) {
            CMisc.print("Solution Null Pointer!");
        }

    }

    /**
     * @param b if {@code true}, makes the {@code Dialog} visible,
     *          otherwise hides the {@code Dialog}.
     *          If the dialog and/or its owner
     *          are not yet displayable, both are made displayable.  The
     *          dialog will be validated prior to being made visible.
     *          If {@code false}, hides the {@code Dialog} and then causes {@code setVisible(true)}
     *          to return if it is currently blocked.
     *          <p>
     *          <b>Notes for modal dialogs</b>.
     *          <ul>
     *          <li>{@code setVisible(true)}:  If the dialog is not already
     *          visible, this call will not return until the dialog is
     *          hidden by calling {@code setVisible(false)} or
     *          {@code dispose}.
     *          <li>{@code setVisible(false)}:  Hides the dialog and then
     *          returns on {@code setVisible(true)} if it is currently blocked.
     *          <li>It is OK to call this method from the event dispatching
     *          thread because the toolkit ensures that other events are
     *          not blocked while this method is blocked.
     *          </ul>
     */
    public void setVisible(boolean b) {
        super.setVisible(b);
        gxInstance.dp.BackupParameter(backup, false);
        gxInstance.d.repaint();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
}
