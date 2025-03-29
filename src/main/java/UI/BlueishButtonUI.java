package UI;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * BlueishButtonUI.java
 * This class extends BasicButtonUI to create a custom button UI with a blueish theme.
 * It provides a unique look and feel for buttons in a Swing application.
 */
public class BlueishButtonUI extends BasicButtonUI {

    private static Color blueishBackgroundOver = new Color(214, 214, 214);
    private static Color blueishBorderOver = new Color(152, 180, 226);

    private static Color blueishBackgroundSelected = new Color(192, 192, 192);
    private static Color blueishBorderSelected = new Color(49, 106, 197);

    /**
     * Constructs a BlueishButtonUI.
     * Calls the superclass constructor.
     */
    public BlueishButtonUI() {
        super();
    }

    /**
     * Installs the UI for a specified component.
     * Sets the rollover enabled, makes the button non-opaque, and adjusts text position.
     *
     * @param c the component where this UI will be installed
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton b = (AbstractButton) c;
        b.setRolloverEnabled(true);
        b.setOpaque(false);
        b.setHorizontalTextPosition(JButton.CENTER);
        b.setVerticalTextPosition(JButton.BOTTOM);
    }

    /**
     * Paints the specified component.
     * Changes the background and border colors based on the button's state.
     *
     * @param g the Graphics context in which to paint
     * @param c the component being painted
     */
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        if (button.getModel().isRollover() || button.getModel().isArmed() || button.getModel().isSelected()) {
            Color oldColor = g.getColor();
            if (button.getModel().isSelected()) {
                g.setColor(blueishBackgroundSelected);
            } else {
                g.setColor(blueishBackgroundOver);
            }
            g.fillRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

            if (button.getModel().isSelected()) {
                g.setColor(blueishBorderSelected);
            } else {
                g.setColor(blueishBorderOver);
            }
            g.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

            g.setColor(oldColor);
        }

        super.paint(g, c);
    }

}