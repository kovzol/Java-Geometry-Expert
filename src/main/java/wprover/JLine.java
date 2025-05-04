package wprover;

import java.awt.*;
import java.util.Vector;

/**
 * JLine class represents a line in a graphical context.
 * It allows adding points, drawing the line, and setting properties like infinite drawing.
 */
public class JLine {
    private boolean ext = false;

    Vector vlist = new Vector();

    /**
     * Constructs a new JLine.
     */
    public JLine() {
    }

    /**
     * Sets whether the line should be drawn infinitely.
     *
     * @param inf true to draw the line infinitely, false otherwise
     */
    public void setDrawInfinite(boolean inf) {
        ext = inf;
    }

    /**
     * Adds a point to the list of points defining the line.
     *
     * @param p the point to add
     */
    public void addAPoint(CPoint p) {
        if (p != null && !vlist.contains(p)) {
            vlist.add(p);
        }
    }

    /**
     * Draws the line on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        return true;
    }

    /**
     * Draws the line between the maximum and minimum points on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void drawLine(Graphics2D g2) {
        CPoint[] pl = getMaxMinPoint();
        if (pl != null) {
            if (!ext) {
                g2.drawLine((int) pl[0].getx(), (int) pl[0].gety(),
                        (int) pl[1].getx(), (int) pl[1].gety());
            } else {
                if (Math.abs(pl[0].getx() - pl[1].getx()) < CMisc.ZERO) {
                    double x = pl[0].getx();
                    double y1 = 0;
                    double y2 = 2000;
                    g2.drawLine((int) x, (int) y1, (int) x, (int) y2);
                } else {
                    double k = (pl[1].gety() - pl[0].gety()) /
                            (pl[1].getx() - pl[0].getx());
                    double x1 = 0;
                    double x2 = 2000;
                    double y1 = k * (0 - pl[0].getx()) + pl[0].gety();
                    double y2 = k * (x2 - pl[0].getx()) + pl[0].gety();
                    g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                }
            }
        }
        // drawPt(g2);
    }

    /**
     * Draws the points defining the line on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void drawPt(Graphics2D g2) {
        for (int i = 0; i < vlist.size(); i++) {
            CPoint pt = (CPoint) vlist.get(i);
            int x = (int) pt.getx();
            int y = (int) pt.gety();
            int r = pt.getRadius();
            g2.drawOval(x - r - 1, y - r - 1, 2 * r + 1, 2 * r + 1);
        }
    }

    /**
     * Fills the points defining the line with white color on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void fillPt(Graphics2D g2) {
        g2.setColor(Color.white);
        for (int i = 0; i < vlist.size(); i++) {
            CPoint pt = (CPoint) vlist.get(i);
            int x = (int) pt.getx();
            int y = (int) pt.gety();
            int r = pt.getRadius();
            g2.fillOval(x - r - 1, y - r - 1, 2 * r + 1, 2 * r + 1);
        }
    }

    /**
     * Sets the flashing mode for all points defining the line.
     *
     * @param t true to enable flashing mode, false to disable
     */
    public void setInFlashMode(boolean t) {
        for (int i = 0; i < vlist.size(); i++) {
            CPoint pt = (CPoint) vlist.get(i);
            pt.setInFlashing(t);
        }
    }

    /**
     * Checks if the specified point is contained in the list of points defining the line.
     *
     * @param pt the point to check
     * @return true if the point is contained in the list, false otherwise
     */
    public boolean containPt(CPoint pt) {
        return vlist.contains(pt);
    }

    /**
     * Gets the maximum and minimum points defining the line.
     *
     * @return an array containing the maximum and minimum points, or null if there are less than two points
     */
    public CPoint[] getMaxMinPoint() {
        if (vlist.size() < 2) {
            return null;
        }

        CPoint p1, p2;
        p1 = (CPoint) vlist.get(0);
        if (p1 == null) {
            return null;
        }

        p2 = null;
        for (int i = 1; i < vlist.size(); i++) {
            CPoint p = (CPoint) vlist.get(i);
            if (p.getx() < p1.getx()) {
                if (p2 == null) {
                    p2 = p1;
                    p1 = p;
                } else {
                    p1 = p;
                }
            } else if (p2 == null || p.getx() > p2.getx()) {
                p2 = p;
            }
        }

        if (Math.abs(p1.getx() - p2.getx()) < 0.00001) {
            p1 = (CPoint) vlist.get(0);
            p2 = null;
            for (int i = 1; i < vlist.size(); i++) {
                CPoint p = (CPoint) vlist.get(i);
                if (p.gety() < p1.gety()) {
                    if (p2 == null) {
                        p2 = p1;
                        p1 = p;
                    } else {
                        p1 = p;
                    }
                } else if (p2 == null || p.gety() > p2.gety()) {
                    p2 = p;
                }
            }
        }

        CPoint[] pl = new CPoint[2];
        pl[0] = p1;
        pl[1] = p2;
        return pl;
    }
}
