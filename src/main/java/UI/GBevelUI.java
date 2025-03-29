package UI;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.*;
import java.awt.*;

/**
 * GBevelUI.java
 * This class extends BasicButtonUI to create a custom button UI with bevel borders.
 * It changes the button's border based on its rollover and selected states.
 */
public class GBevelUI extends BasicButtonUI {

    GBevelBorder border1 = new GBevelBorder(GBevelBorder.RAISED);
    GBevelBorder border2 = new GBevelBorder(GBevelBorder.LOWERED);

    public GBevelUI() {
        super();
    }

    /**
     * Installs the UI for a specified component.
     * Sets the rollover enabled and applies an empty border.
     *
     * @param c the component where this UI will be installed
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setRolloverEnabled(true);
        button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    /**
     * Paints the specified component.
     * Changes the border based on the button's rollover and selected states.
     *
     * @param g the Graphics context in which to paint
     * @param c the component being painted
     */
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        boolean b1 = model.isRollover();
        boolean b2 = model.isArmed();
        boolean b3 = model.isSelected();

        if (b3) {
            border2.paintBorder(button, g, 0, 0, button.getWidth(), button.getHeight());
        } else if (b1) {
            border1.paintBorder(button, g, 0, 0, button.getWidth(), button.getHeight());
        }
        super.paint(g, button);
    }
}
