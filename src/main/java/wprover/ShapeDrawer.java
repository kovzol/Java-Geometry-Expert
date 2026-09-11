package wprover;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class ShapeDrawer {
    private ShapeDrawer(){}

    private static final Ellipse2D.Double REUSABLE_ELLIPSE = new Ellipse2D.Double();
    private static final Line2D.Double REUSABLE_LINE = new Line2D.Double();
    private static final Arc2D.Double REUSABLE_ARC = new Arc2D.Double();

    /**
     * Draws an ellipse to the argument graphics object
     */
    public static void drawEllipse(Graphics2D g2, double x, double y, double w, double h) {
        REUSABLE_ELLIPSE.setFrame(x, y, w, h);
        g2.draw(REUSABLE_ELLIPSE);
    }

    /**
     * Fills an ellipse to the argument graphics object
     */
    public static void fillEllipse(Graphics2D g2, double x, double y, double w, double h) {
        REUSABLE_ELLIPSE.setFrame(x, y, w, h);
        g2.fill(REUSABLE_ELLIPSE);
    }

    /**
     * Draws a line to the argument graphics object
     *
     * @param g2 the Graphics2D instance to draw to
     * @param x1 the x coorditante of the start point
     * @param y1 the y coorditante of the start point
     * @param x2 the x coorditante of the end point
     * @param y2 the y coorditante of the end point
     */
    public static void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2) {
        REUSABLE_LINE.setLine(x1, y1, x2, y2);
        g2.draw(REUSABLE_LINE);
    }

    /**
     * Draws an arch to the argument graphics object
     */
    public static void drawArc(Graphics2D g2, double x, double y, double w, double h, double sa, double aa) {
        REUSABLE_ARC.setArc(x, y, w, h, sa, aa, Arc2D.OPEN);
        g2.draw(REUSABLE_ARC);
    }

    /**
     * Fills an arch to the argument graphics object
     */
    public static void fillArc(Graphics2D g2, double x, double y, double w, double h, double sa, double aa) {
        REUSABLE_ARC.setArc(x, y, w, h, sa, aa, Arc2D.OPEN);
        g2.fill(REUSABLE_ARC);
    }

}
