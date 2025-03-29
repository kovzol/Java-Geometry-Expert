package UI;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * BLeveledButtonUI.java
 * This class extends BasicButtonUI to create a custom button UI with leveled borders.
 * It changes the button's border based on its rollover state.
 */
public class BLeveledButtonUI extends BasicButtonUI {

    private static Border border1 = BorderFactory.createRaisedBevelBorder();
    private static Border border2 = BorderFactory.createEtchedBorder();//createEtchedBorder(Color.white,Color.gray.brighter());//.createMatteBorder(2,2,2,2,Color.LIGHT_GRAY);//.createEmptyBorder(2, 2, 2, 2);

    /**
     * Constructs a BLeveledButtonUI.
     * Calls the superclass constructor.
     */
    public BLeveledButtonUI() {
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
     * Changes the border based on the rollover state of the button.
     *
     * @param g the Graphics context in which to paint
     * @param c the component being painted
     */
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        boolean b1 = button.getModel().isRollover();
        if (b1) {
            button.setBorder(border1);
        } else {
            button.setBorder(border2);
        }

        super.paint(g, c);
    }

}
