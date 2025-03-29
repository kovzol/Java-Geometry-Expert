package UI;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * OvalBorder is a class that implements the Border interface.
 * It creates an oval-shaped border with specified width, height, and colors.
 */
public class OvalBorder implements Border {
    protected int ovalWidth = 6;

    protected int ovalHeight = 6;

    protected Color lightColor = Color.white;

    protected Color darkColor = Color.gray;

    /**
     * Returns the insets of the border.
     * The insets are determined based on the oval width and height.
     *
     * @param c the component for which this border insets value applies
     * @return the insets of the border
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(ovalHeight, ovalWidth, ovalHeight, ovalWidth);
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
     * Draws the oval-shaped border with the specified light and dark colors.
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

        g.setColor(lightColor);
        g.drawLine(x, y + height - ovalHeight, x, y + ovalHeight);
        g.drawArc(x, y, 2 * ovalWidth, 2 * ovalHeight, 180, -90);
        g.drawLine(x + ovalWidth, y, x + width - ovalWidth, y);
        g.drawArc(x + width - 2 * ovalWidth, y, 2 * ovalWidth, 2 * ovalHeight, 90, -90);

        g.setColor(darkColor);
        g.drawLine(x + width, y + ovalHeight, x + width, y + height - ovalHeight);
        g.drawArc(x + width - 2 * ovalWidth, y + height - 2 * ovalHeight, 2 * ovalWidth, 2 * ovalHeight, 0, -90);
        g.drawLine(x + ovalWidth, y + height, x + width - ovalWidth, y + height);
        g.drawArc(x, y + height - 2 * ovalHeight, 2 * ovalWidth, 2 * ovalHeight, -90, -90);
    }
}