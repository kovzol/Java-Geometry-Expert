package UI;

import javax.swing.border.SoftBevelBorder;
import java.awt.*;

/**
 * GBevelBorder is a custom border class that extends SoftBevelBorder.
 * It provides a beveled border with customizable colors and styles.
 */
public class GBevelBorder extends SoftBevelBorder {

    final private static BasicStroke bstroke = new BasicStroke(0.5f);

    int type = 0;


    /**
     * Constructs a GBevelBorder with a specified bevel type.
     *
     * @param t the bevel type (RAISED or LOWERED)
     */
    public GBevelBorder(int t) {
        super(t);
    }

    /**
     * Constructs a GBevelBorder with a specified bevel type and custom type.
     *
     * @param t    the bevel type (RAISED or LOWERED)
     * @param type the custom type for additional styling
     */
    public GBevelBorder(int t, int type) {
        super(t);
        this.type = type;
    }

    /**
     * Paints the border for the specified component.
     * Draws the custom border and additional lines based on the component's dimensions.
     *
     * @param c      the component for which this border is being painted
     * @param g      the Graphics context in which to paint
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        paintBorder1(c, g, x, y, width, height);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(bstroke);
        g2.setColor(Color.LIGHT_GRAY);
        int w = width;
        int h = height;

        g2.drawLine(0, 0, 0, h);
        g2.drawLine(0, 0, w, 0);
    }

    /**
     * Paints the primary border for the specified component.
     * Draws the beveled border based on the component's dimensions and bevel type.
     *
     * @param c      the component for which this border is being painted
     * @param g      the Graphics context in which to paint
     * @param x      the x position of the painted border
     * @param y      the y position of the painted border
     * @param width  the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder1(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        g.translate(x, y);

        if (bevelType == RAISED) {
            g.setColor(getHighlightOuterColor(c));
            g.drawLine(0, 0, width - 2, 0);
            g.drawLine(0, 0, 0, height - 2);
            g.drawLine(1, 1, 1, 1);

            g.setColor(getHighlightInnerColor(c));
            g.drawLine(2, 1, width - 2, 1);
            g.drawLine(1, 2, 1, height - 2);
            g.drawLine(2, 2, 2, 2);
            g.drawLine(0, height - 1, 0, height - 2);
            g.drawLine(width - 1, 0, width - 1, 0);

            if (type == 0) {
                g.setColor(getShadowOuterColor(c));
                g.drawLine(2, height - 1, width - 1, height - 1);
                g.drawLine(width - 1, 2, width - 1, height - 1);

                g.setColor(getShadowInnerColor(c));
                g.drawLine(width - 2, height - 2, width - 2, height - 2);
            }

        } else if (bevelType == LOWERED) {
            g.setColor(getShadowOuterColor(c));
            g.drawLine(0, 0, width - 2, 0);
            g.drawLine(0, 0, 0, height - 2);
            g.drawLine(1, 1, 1, 1);

            g.setColor(getShadowInnerColor(c));
            g.drawLine(2, 1, width - 2, 1);
            g.drawLine(1, 2, 1, height - 2);
            g.drawLine(2, 2, 2, 2);
            g.drawLine(0, height - 1, 0, height - 2);
            g.drawLine(width - 1, 0, width - 1, 0);

            g.setColor(getHighlightOuterColor(c));
            g.drawLine(2, height - 1, width - 1, height - 1);
            g.drawLine(width - 1, 2, width - 1, height - 1);

            g.setColor(getHighlightInnerColor(c));
            g.drawLine(width - 2, height - 2, width - 2, height - 2);
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    /**
     * Returns the insets of the border.
     * Adjusts the insets based on the custom type.
     *
     * @param c      the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return the insets of the border
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        super.getBorderInsets(c, insets);
        if (type != 0)
            insets.bottom = 0;
        return insets;
    }

}
