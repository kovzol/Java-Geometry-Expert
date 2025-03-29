package wprover;

import javax.swing.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * RightTransformPopupMenu is a class that extends JPopupMenu and implements ItemListener and ActionListener.
 * It provides a context menu for transforming graphical objects in a drawing application.
 */
public class RightTransformPopupMenu extends JPopupMenu implements ItemListener, ActionListener {

    private DrawProcess dp;
    private JRadioButtonMenuItem m1, m2, m3;
    private JMenuItem m;
    private int xx, yy;

    /**
     * Constructs a new RightTransformPopupMenu with the specified DrawProcess instance.
     *
     * @param d the DrawProcess instance to associate with this menu
     */
    public RightTransformPopupMenu(DrawProcess d) {
        dp = d;

        ButtonGroup g = new ButtonGroup();
        m1 = new JRadioButtonMenuItem(GExpert.getLanguage("Translate"));
        g.add(m1);
        m2 = new JRadioButtonMenuItem(GExpert.getLanguage("Rotate"));
        g.add(m2);
        m3 = new JRadioButtonMenuItem(GExpert.getLanguage("Flap"));
        g.add(m3);
        m = new JMenuItem(GExpert.getLanguage("Cancel"));

        int t = d.getStatus();
        if (t == 1)
            m1.setSelected(true);
        else
            m2.setSelected(true);

        m1.addItemListener(this);
        m2.addItemListener(this);
        m.addActionListener(this);
        this.add(m1);
        this.add(m2);
        this.add(m3);
        addSeparator();
        this.add(m);
    }

    /**
     * Handles action events for the menu items.
     *
     * @param e the ActionEvent triggered by the menu items
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == m) {
            dp.setTransformStatus(0);
            return;
        }
    }

    /**
     * Displays the popup menu at the specified location.
     *
     * @param invoker the component in which the popup menu is displayed
     * @param x       the x-coordinate of the popup menu's location
     * @param y       the y-coordinate of the popup menu's location
     */
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        xx = x;
        yy = y;
    }

    /**
     * Handles item state change events for the radio buttons.
     *
     * @param e the ItemEvent triggered by the radio buttons
     */
    public void itemStateChanged(ItemEvent e) {

        if (m1.isSelected()) {
            dp.setTransformStatus(1);
            dp.setFirstPnt(xx, yy);
        } else if (m2.isSelected())
            dp.setTransformStatus(2);
        else
            dp.setTransformStatus(3);
    }

}
