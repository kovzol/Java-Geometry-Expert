package wprover;

import java.awt.*;

import javax.swing.*;
import javax.swing.Timer;

/**
 * JFlash is an abstract class that provides methods for creating and managing
 * graphical flash effects in a JPanel. It includes methods for drawing angles,
 * setting colors, and managing timers.
 */
public abstract class JFlash {

    protected Timer timer;
    protected JPanel panel;
    protected int n = 8;
    protected static double ZERO = 1.0;
    final static float[] DF = {8.0f};
    final static float[] DF1 = {2.0f};
    final static BasicStroke BStroke = new BasicStroke(1.0f);
    final static BasicStroke BStroke2 = new BasicStroke(3.0f);
    final public static BasicStroke Dash = new BasicStroke(2.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, DF, 0.0f);

    final public static BasicStroke DASH1 = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, DF, 0.0f);
    final static int RAD = 40;
    final static int MAX = 100;
    final static int MIN_STEP = 20;

    final static double ASTEP = 0.120; // angle step


    //protected static Vector linelist = new Vector();
    protected Color color = Color.red;
    protected boolean finished = false;
    protected boolean vType = false;
    protected boolean fb = false;
    protected int TIME_INTERVAL = CMisc.getFlashInterval();


    /**
     * Updates the timer delay to the flash interval defined in CMisc.
     */
    public void updateTimer() {
        if (timer != null)
            timer.setDelay(CMisc.getFlashInterval());
    }

    /**
     * Sets the delay for the timer.
     *
     * @param n the delay in milliseconds
     */
    public void setDealy(int n) {
        if (timer != null)
            timer.setDelay(n);
    }

    /**
     * Constructs a new JFlash with the specified JPanel.
     *
     * @param p the JPanel to associate with this JFlash
     */
    public JFlash(JPanel p) {
        panel = p;
    }

    /**
     * Gets the visibility type of the flash.
     *
     * @return true if the flash is visible, false otherwise
     */
    public boolean getvisibleType() {
        return vType;
    }

    /**
     * Sets the color of the flash.
     *
     * @param c the color to set
     */
    public void setColor(Color c) {
        color = c;
    }

    /**
     * Gets the color of the flash.
     *
     * @return the color of the flash
     */
    public Color getColor() {
        return color;
    }

    /**
     * Draws the flash effect on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public abstract boolean draw(Graphics2D g2);

    /**
     * Recalculates the flash effect. This method is intended to be overridden by subclasses.
     */
    public void recalculate() {
    }

    /**
     * Starts the flash effect.
     */
    public void start() {
        if (timer != null) {
            finished = false;
            timer.start();
            n = 8;
        }
    }

    /**
     * Stops the flash effect.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
            finished = true;
        }
    }

    /**
     * Checks if the flash effect is finished.
     *
     * @return true if the flash effect is finished, false otherwise
     */
    public boolean isfinished() {
        return this.finished;
    }

    /**
     * Checks if the timer is running.
     *
     * @return true if the timer is running, false otherwise
     */
    public boolean isrRunning() {
        return timer.isRunning();
    }

    /**
     * Sets the drawing stroke to a dashed line with a red color.
     *
     * @param g2 the Graphics2D context to set the stroke on
     */
    final public void setDrawDash(Graphics2D g2) {
        g2.setColor(Color.red);
        g2.setStroke(DASH1);
    }

    /**
     * Sets the drawing stroke to either a bold or dashed line with a red color based on the fb flag.
     *
     * @param g2 the Graphics2D context to set the stroke on
     */
    final public void setDrawDashFb2(Graphics2D g2) {
        if (fb) {
            g2.setStroke(BStroke2);
        } else {
            g2.setStroke(Dash);
        }
        g2.setColor(Color.red);
    }

    /**
     * Checks if the given value is approximately zero.
     *
     * @param d the value to check
     * @return true if the value is approximately zero, false otherwise
     */
    final public boolean isZero(double d) {
        return Math.abs(d) < ZERO;
    }

    /**
     * Calculates the intersection point of two lines defined by their endpoints.
     *
     * @param x1 the x-coordinate of the first point of the first line
     * @param y1 the y-coordinate of the first point of the first line
     * @param x2 the x-coordinate of the second point of the first line
     * @param y2 the y-coordinate of the second point of the first line
     * @param x3 the x-coordinate of the first point of the second line
     * @param y3 the y-coordinate of the first point of the second line
     * @param x4 the x-coordinate of the second point of the second line
     * @param y4 the y-coordinate of the second point of the second line
     * @return an array containing the x and y coordinates of the intersection point, or null if the lines are parallel
     */
    final static double[] interect_LL(double x1, double y1, double x2,
                                      double y2, double x3, double y3,
                                      double x4, double y4) {

        double result[] = new double[2];
        if (Math.abs(x1 - x2) < ZERO) {
            if (Math.abs(x3 - x4) < ZERO) {
                return null;
            }
            double k = (y3 - y4) / (x3 - x4);
            result[0] = x1;
            result[1] = k * (x1 - x3) + y3;
            return result;
        }
        if (Math.abs(x3 - x4) < ZERO) {
            double k0 = (y1 - y2) / (x1 - x2);
            result[0] = x3;
            result[1] = k0 * (x3 - x1) + y1;
            return result;
        }
        double k0 = (y1 - y2) / (x1 - x2);
        double k1 = (y3 - y4) / (x3 - x4);

        double x = (y3 - y1 + k0 * x1 - k1 * x3) / (k0 - k1);
        double y = k0 * (x - x1) + y1;

        result[0] = x;
        result[1] = y;
        return result;
    }

    /**
     * Checks if the distance between two points is less than a small threshold.
     *
     * @param x0 the x-coordinate of the first point
     * @param y0 the y-coordinate of the first point
     * @param x  the x-coordinate of the second point
     * @param y  the y-coordinate of the second point
     * @return true if the distance between the points is less than the threshold, false otherwise
     */
    static boolean spt(double x0, double y0, double x, double y) {
        return Math.pow(x0 - x, 2) + Math.pow(y0 - y, 2) <
                CMisc.ZERO * CMisc.ZERO;
    }

    /**
     * Draws a line segment between a point and the closer of two other points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param x  the x-coordinate of the third point
     * @param y  the y-coordinate of the third point
     * @param g2 the Graphics2D context to draw on
     */
    static public void drawALine3Short(CPoint p1, CPoint p2, double x, double y,
                                       Graphics2D g2) {

        if ((x - p1.getx()) * (x - p2.getx()) < 0 ||
                (y - p1.gety()) * (y - p2.gety()) < 0) {
            return;
        }

        if (Math.pow(p1.getx() - x, 2) + Math.pow(p1.gety() - y, 2) <
                Math.pow(p2.getx() - x, 2) + Math.pow(p2.gety() - y, 2)) {
            g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) x, (int) y);
        } else {
            g2.drawLine((int) p2.getx(), (int) p2.gety(), (int) x, (int) y);
        }

    }

    /**
     * Draws a line segment between a point and the closer of two other points, with optional line drawing.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param x  the x-coordinate of the third point
     * @param y  the y-coordinate of the third point
     * @param ln whether to draw the line segment between p1 and p2
     * @param g2 the Graphics2D context to draw on
     */
    static public void drawALine3(CPoint p1, CPoint p2, double x, double y,
                                  boolean ln, Graphics2D g2) {
        if (Math.abs(x) > 10000 || Math.abs(y) > 10000) {
            return;
        }

        if (p1.getx() > p2.getx()) {
            CPoint p = p1;
            p1 = p2;
            p2 = p;
        }
        double x1, y1, x2, y2;
        x1 = p1.getx();
        y1 = p1.gety();
        x2 = p2.getx();
        y2 = p2.gety();
        if (x < x1) {
            g2.drawLine((int) x, (int) y, (int) x2, (int) y2);
        } else if (x > x1 && x < x2) {
            if (ln) {
                g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            }
        } else if (x > x2) {
            g2.drawLine((int) x, (int) y, (int) x1, (int) y1);
        } else { // ==
            if (p1.gety() > p2.gety()) {
                CPoint p = p1;
                p1 = p2;
                p2 = p;
            }
            x1 = p1.getx();
            y1 = p1.gety();
            x2 = p2.getx();
            y2 = p2.gety();
            if (y < y1) {
                g2.drawLine((int) x, (int) y, (int) x2, (int) y2);
            } else if (y > y1 && y < y2) {
                g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            } else if (y > y2) {
                g2.drawLine((int) x, (int) y, (int) x1, (int) y1);
            } else {
                if (ln) {
                    g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
            }
        }
    }

    /**
     * Gets a color that is a blend of two colors based on a ratio.
     *
     * @param c1 the first color index
     * @param c2 the second color index
     * @param ra the ratio for blending the colors
     * @return the blended color
     */
    protected static Color getRatioColor(int c1, int c2, double ra) {
        Color o1 = DrawData.getColor(c1);
        Color o2 = DrawData.getColor(c2);
        double r1 = ra;
        double r2 = 1 - ra;

        int r = (int) (o1.getRed() * r2 + o2.getRed() * r1);
        int g = (int) (o1.getGreen() * r2 + o2.getGreen() * r1);
        int b = (int) (o1.getBlue() * r2 + o2.getBlue() * r1);
        Color c = new Color(r, g, b);
        return c;
    }

}
