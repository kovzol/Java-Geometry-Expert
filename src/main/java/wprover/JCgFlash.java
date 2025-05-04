package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * JCgFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a flashing effect on a JPanel with specified points.
 */
public class JCgFlash extends JFlash implements ActionListener {
    Vector vlist = new Vector();
    int length = 8;
    int dnum = 2;
    private boolean dTT = true;

    /**
     * Constructs a new JCgFlash with the specified JPanel.
     *
     * @param p the JPanel to associate with this JCgFlash
     */
    public JCgFlash(JPanel p) {
        super(p);
        timer = new Timer(TIME_INTERVAL, this);
    }

    /**
     * Sets whether the drawTT flag is enabled.
     *
     * @param t true to enable drawTT, false to disable
     */
    public void setDrawdTT(boolean t) {
        dTT = t;
    }

    /**
     * Sets the number of divisions.
     *
     * @param n the number of divisions
     */
    public void setDNum(int n) {
        dnum = n;
    }

    /**
     * Gets the number of divisions.
     *
     * @return the number of divisions
     */
    public int getDNum() {
        return dnum;
    }

    /**
     * Adds a pair of CPoints to the vector list.
     *
     * @param a the first CPoint
     * @param b the second CPoint
     */
    public void addACg(CPoint a, CPoint b) {
        vlist.add(a);
        vlist.add(b);
    }

    /**
     * Handles action events for the timer.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        n--;
        if (n <= 0) super.stop();
        panel.repaint();
    }

    /**
     * Draws the flashing effect on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        int num = vlist.size() / 2;
        for (int i = 0; i < num; i++) {
            CPoint p1 = (CPoint) vlist.get(i * 2);
            CPoint p2 = (CPoint) vlist.get(i * 2 + 1);
            g2.setStroke(BStroke2);
            g2.setColor(Color.white);
            g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
        }

        double r = -1;
        boolean all_eq = true;

        for (int i = 0; i < num; i++) {
            CPoint p1 = (CPoint) vlist.get(i * 2);
            CPoint p2 = (CPoint) vlist.get(i * 2 + 1);
            double r1 = DrawBase.sdistance(p1, p2);

            if (r < 0)
                r = r1;
            else if (Math.abs(r - r1) < CMisc.ZERO) {
            } else {
                all_eq = false;

                if (r < r1) {
                    double rx = r;
                    r = r1;
                    r1 = rx;
                }
                int n = (int) (r / r1 + 0.5);
                if (Math.abs(n * r1 - r) < CMisc.ZERO)
                    r = r1;
                else {
                    r = -1;
                    break;
                }
            }
        }

        for (int i = 0; i < num; i++) {
            CPoint p1 = (CPoint) vlist.get(i * 2);
            CPoint p2 = (CPoint) vlist.get(i * 2 + 1);
            if (n % 2 == 0) {
                g2.setStroke(BStroke);
                g2.setColor(DrawData.getColor(i + 3));
                if (r < 0 || all_eq) { /*dnum = 2;*/
                } else
                    dnum = (int) (DrawBase.sdistance(p1, p2) / r + 0.5);

                draw(g2, p1, p2);
                g2.setStroke(Dash);
                g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
            } else {
                g2.setColor(Color.white);
                g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
            }
        }
        return true;
    }

    /**
     * Draws a line between two CPoints with a flashing effect.
     *
     * @param g2 the Graphics2D context to draw on
     * @param p1 the starting CPoint
     * @param p2 the ending CPoint
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2, CPoint p1, CPoint p2) {
        if (!dTT) return false;

        int DEFAULT_GAP = 6;
        int DEFAULT_LEN = 8;

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double dy = y2 - y1;
        double dx = x2 - x1;

        if (dy * dy + dx * dx < DEFAULT_LEN * DEFAULT_LEN)
            return true;

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        if (dnum % 2 == 0) {
            int dgap = DEFAULT_GAP / 2;

            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                dgap += DEFAULT_GAP;
            }
        } else {
            int dgap = DEFAULT_GAP;
            this.drawALine(x, y, dx, dy, g2);
            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.drawALine(xx, yy, dx, dy, g2);
                dgap += DEFAULT_GAP;
            }
        }

        return true;
    }

    /**
     * Draws a line segment with a specified direction and length.
     *
     * @param x  the x-coordinate of the center point
     * @param y  the y-coordinate of the center point
     * @param dx the x-direction unit vector
     * @param dy the y-direction unit vector
     * @param g2 the Graphics2D context to draw on
     */
    public void drawALine(double x, double y, double dx, double dy, Graphics2D g2) {
        double xx1 = x - dy * length;
        double yy1 = y + dx * length;

        double xx2 = x + dy * length;
        double yy2 = y - dx * length;
        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);
    }
}
