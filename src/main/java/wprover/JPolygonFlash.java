package wprover;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.Vector;

/**
 * JPolygonFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a flashing polygon effect on a JPanel.
 */
public class JPolygonFlash extends JFlash implements ActionListener {
    private int c1, c2;
    private Vector vlist, vlist1;
    private int FTYPE = 0;
    private int nd = 0;
    private int n = 0;
    private double dx, dy, da, dx1, dy1, dx2, dy2;
    private int[] mx, my;
    private CPolygon poly1, poly2;
    private boolean R_Center = false;
    private double xc, yc;

    /**
     * Constructs a new JPolygonFlash with the specified parameters.
     *
     * @param p     the JPanel to associate with this JPolygonFlash
     * @param poly1 the first CPolygon to be used in the flashing effect
     * @param poly2 the second CPolygon to be used in the flashing effect
     * @param oc    a boolean indicating whether to use the specified center coordinates
     * @param xc    the x-coordinate of the center
     * @param yc    the y-coordinate of the center
     * @param c1    the first color index for the flashing effect
     * @param c2    the second color index for the flashing effect
     * @param tt    the type of flashing effect
     */
    public JPolygonFlash(JPanel p, CPolygon poly1, CPolygon poly2, boolean oc, double xc, double yc, int c1, int c2, int tt) {
        super(p);

        vlist = new Vector();
        vlist.addAll(poly1.pointlist);
        vlist1 = new Vector();
        vlist1.addAll(poly2.pointlist);
        this.c1 = c1;
        this.c2 = c2;
        R_Center = oc;
        this.xc = xc;
        this.yc = yc;

        vType = true;
        if (tt == 0)
            init();
        else
            init1();
        FTYPE = tt;

        timer = new Timer(TIME_INTERVAL, this);

        this.poly1 = poly1;
        this.poly2 = poly2;
        poly2.setVisible(false);
    }

    /**
     * Initializes the flashing effect by calculating the number of steps required
     * based on the distance between corresponding points in the two polygons.
     */
    private void init1() {
        int len = vlist.size();
        if (len == 0) return;
        int n = 0;

        for (int i = 0; i < len; i++) {
            CPoint p = (CPoint) vlist.get(i);
            CPoint p1 = (CPoint) vlist1.get(i);
            double d = Math.sqrt(Math.pow(p.getx() - p1.getx(), 2) + Math.pow(p.gety() - p1.gety(), 2));
            int t = (int) (d / CMisc.getMoveStep());
            if (t > n)
                n = t;
        }
        this.n = n;

        mx = new int[len];
        my = new int[len];
        this.nd = 0;
    }

    /**
     * Initializes the flashing effect by calculating the centroid and the number of steps required
     * based on the distance and angle between the centroids of the two polygons.
     */
    private void init() {
        int len = vlist.size();
        if (len == 0) return;

        CPoint p1, p2, p3, p4;
        p1 = p2 = null;
        for (int i = 0; i < vlist.size(); i++) {
            if (vlist.get(i) != vlist1.get(i)) {
                p1 = (CPoint) vlist.get(i);
                p2 = (CPoint) vlist1.get(i);
                break;
            }
        }

        if (R_Center) {
            dx1 = xc;
            dy1 = yc;
            dx2 = xc;
            dy2 = yc;
        } else {
            dx1 = getCentroidX(vlist);
            dy1 = getCentroidY(vlist);
            dx2 = getCentroidX(vlist1);
            dy2 = getCentroidY(vlist1);
        }

        double x = dx2 - dx1;
        double y = dy2 - dy1;

        int s = (int) (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) / CMisc.getMoveStep());
        p1 = (CPoint) vlist.get(0);
        p2 = (CPoint) vlist1.get(0);
        p3 = (CPoint) vlist.get(1);
        p4 = (CPoint) vlist1.get(1);

        double r = CAngle.get4pAngle(p1.getx(), p1.gety(), p3.getx(), p3.gety(), p2.getx(), p2.gety(), p4.getx(), p4.gety());
        int s1 = (int) Math.abs(r / ASTEP);
        n = Math.max(s, s1);
        if (n >= 100 || n < 10)
            n = 10;

        dx = (dx2 - dx1) / n;
        dy = (dy2 - dy1) / n;
        da = r / n;

        mx = new int[len];
        my = new int[len];
        this.nd = 0;
    }

    /**
     * Calculates the x-coordinate of the centroid of the given vector of points.
     *
     * @param v the vector of points
     * @return the x-coordinate of the centroid
     */
    public double getCentroidX(Vector v) {
        double dx1 = 0;
        int n = v.size();
        for (int i = 0; i < n; i++) {
            CPoint pt = (CPoint) v.get(i);
            dx1 += pt.getx();
        }
        dx1 /= n;
        return dx1;
    }

    /**
     * Calculates the y-coordinate of the centroid of the given vector of points.
     *
     * @param v the vector of points
     * @return the y-coordinate of the centroid
     */
    public double getCentroidY(Vector v) {
        double dy1 = 0;
        int n = v.size();
        for (int i = 0; i < n; i++) {
            CPoint pt = (CPoint) v.get(i);
            dy1 += pt.gety();
        }
        dy1 /= n;
        return dy1;
    }

    /**
     * Handles the action event for the timer, updating the animation state and repainting the panel.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (nd <= n)
            nd++;

        if (nd > n)
            stop();

        panel.repaint();
    }

    /**
     * Stops the flashing effect and makes the second polygon visible.
     */
    public void stop() {
        poly2.setVisible(true);
        super.stop();
    }

    /**
     * Starts the flashing effect and makes the second polygon invisible.
     */
    public void start() {
        poly2.setVisible(false);
        super.start();
    }

    /**
     * Recalculates the flashing effect by reinitializing the animation state.
     */
    public void recalculate() {
        int t = nd;
        nd = t;
        this.init();
    }

    /**
     * Draws the flashing polygon effect based on the current animation state.
     *
     * <p>
     * This method computes the polygon's vertex positions by either applying a rotation
     * and translation transformation or by linearly interpolating between two sets of points,
     * depending on the current flashing type. It then fills the polygon with an interpolated
     * color and draws its outline.
     * </p>
     *
     * <p>
     * The method returns false if the animation is not running or has finished.
     * </p>
     *
     * @param g2 the Graphics2D context used for drawing
     * @return true if the polygon is successfully drawn; false otherwise
     */
    public boolean draw(Graphics2D g2) {

        int ln = vlist.size();
        int index = nd;
        if (!isrRunning() && !isfinished())
            return false;

        if (FTYPE == 0) {
            if (index <= this.n) {
                double a = index * da;
                double sin = Math.sin(a);
                double cos = Math.cos(a);
                for (int i = 0; i < ln; i++) {
                    CPoint p = (CPoint) vlist.get(i);
                    double tx = p.getx() - dx1;
                    double ty = p.gety() - dy1;
                    double x = tx * cos - ty * sin;
                    double y = tx * sin + ty * cos;
                    tx = x + dx1 + index * dx;
                    ty = y + dy1 + index * dy;
                    mx[i] = (int) tx;
                    my[i] = (int) ty;
                }
            } else
                return false;
        } else {
            if (index <= this.n) {
                for (int i = 0; i < ln; i++) {
                    CPoint p = (CPoint) vlist.get(i);
                    CPoint p1 = (CPoint) vlist1.get(i);
                    mx[i] = (int) (p.getx() + (p1.getx() - p.getx()) * index / n);
                    my[i] = (int) (p.gety() + (p1.gety() - p.gety()) * index / n);
                }
            } else
                return false;
        }

        Composite ac = g2.getComposite();
        g2.setComposite(CMisc.getFillComposite());

        Color o1 = DrawData.getColor(c1);
        Color o2 = DrawData.getColor(c2);
        double r1 = ((double) index) / n;
        double r2 = 1 - r1;

        int r = (int) (o1.getRed() * r2 + o2.getRed() * r1);
        int g = (int) (o1.getGreen() * r2 + o2.getGreen() * r1);
        int b = (int) (o1.getBlue() * r2 + o2.getBlue() * r1);
        Color c = new Color(r, g, b);

        g2.setColor(c);
        g2.fillPolygon(mx, my, ln);
        g2.setComposite(ac);

        g2.setColor(Color.black);
        g2.setStroke(CMisc.NormalLineStroke);
        g2.drawPolygon(mx, my, ln);
        return true;
    }
}