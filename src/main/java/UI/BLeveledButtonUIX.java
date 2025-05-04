package UI;

import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * BLeveledButtonUIX is a custom button UI that extends BlueishButtonUI.
 * It provides a leveled button appearance with different borders for different states.
 */
public class BLeveledButtonUIX extends BlueishButtonUI {
    private static Border border1 = BorderFactory.createRaisedBevelBorder();
    private static Border border2 = BorderFactory.createEtchedBorder();//createEtchedBorder(Color.white,Color.gray.brighter());//.createMatteBorder(2,2,2,2,Color.LIGHT_GRAY);//.createEmptyBorder(2, 2, 2, 2);

    /**
     * Constructs a BLeveledButtonUIX.
     * Calls the superclass constructor.
     */
    public BLeveledButtonUIX() {
        super();
    }

    /**
     * Installs the UI for a specified component.
     * Sets the rollover enabled and applies the default border.
     *
     * @param c the component where this UI will be installed
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setRolloverEnabled(true);
        button.setBorder(border2);
    }

    /**
     * Paints the specified component.
     * Changes the border based on the rollover, armed, and selected states of the button.
     *
     * @param g the Graphics context in which to paint
     * @param c the component being painted
     */
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        boolean b1 = button.getModel().isRollover();
        boolean b2 = button.getModel().isArmed();
        boolean b3 = button.getModel().isSelected();
        if (b2 || b3) {
            button.setBorder(border1);
        } else {
            button.setBorder(border2);
        }

        super.paint(g, c);
    }
}
