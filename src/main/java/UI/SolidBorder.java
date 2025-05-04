package UI;

import javax.swing.border.Border;
import java.awt.*;

/**
 * SolidBorder is a class that implements the Border interface.
 * It provides a solid border with customizable top and bottom colors.
 */
public class SolidBorder implements Border {
    protected Color topColor = Color.white;
    protected Color bottomColor = Color.gray;

    /**
     * Constructs a SolidBorder with default colors.
     * Initializes the top color to white and the bottom color to gray.
     */
    public SolidBorder() {
    }

    /**
     * Returns the insets of the border.
     * The insets are set to 2 pixels on all sides.
     *
     * @param c the component for which this border insets value applies
     * @return the insets of the border
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    /**
     * Indicates whether the border is opaque.
     *
     * @return true if the border is opaque, false otherwise
     */
    public boolean isBorderOpaque() {
        return true;
    }

    /**
     * Paints the border for the specified component.
     * Draws the border with the top color on the top and left sides,
     * and the bottom color on the bottom and right sides.
     *
     * @param c      the component for which this border is being painted
     * @param g      the Graphics context in which to paint
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        width--;
        height--;
        g.setColor(topColor);
        g.drawLine(x, y + height, x, y);
        g.drawLine(x, y, x + width, y);
        g.setColor(bottomColor);
        g.drawLine(x + width, y, x + width, y + height);
        g.drawLine(x, y + height, x + width, y + height);
    }

}
