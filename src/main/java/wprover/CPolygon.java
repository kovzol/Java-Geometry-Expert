package wprover;

import java.util.Vector;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * CPolygon class represents a polygon or circle shape.
 * It extends the CClass class and provides methods for drawing, manipulating, and checking properties of the shape.
 */
public class CPolygon extends CClass {
    int ftype = 0; // 0: polygon, 1:circle.

    int type = 0; // 0,fill; 1,grid;
    int grid = 12;
    int slope = 0;
    private boolean showArea = false;
    Vector pointlist = new Vector();

    private CPoint pt1, pt2;
    private Vector vtrlist = new Vector();
    private double pdx, pdy;
    private double area;


/**
     * Draws the polygon on the given graphics context with various options.
     *
     * @param g2 the graphics context
     * @param selected true if the polygon is selected, false otherwise
     * @param overlap true if the polygon should be drawn with overlap, false otherwise
     * @param dx the x-offset for drawing
     * @param dy the y-offset for drawing
     * @param rotate true if the polygon should be rotated, false otherwise
     * @param x the x-coordinate for rotation
     * @param y the y-coordinate for rotation
     * @param ang the angle of rotation in radians
     */
    public void draw(Graphics2D g2, boolean selected, boolean overlap, double dx, double dy,
                     boolean rotate, double x, double y, double ang) {
        if (!isdraw()) return;
        int n = pointlist.size();
        if (n == 0) return;
        if (pointlist.get(0) != pointlist.get(n - 1))
            return;

        int[] xpoints = new int[n];
        int[] ypoints = new int[n];

        for (int i = 0; i < n; i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            double tx = (pt.getx() + dx);
            double ty = (pt.gety() + dy);
            if (pt == pt1 || pt == pt2) {
                tx += pdx;
                ty += pdy;
            } else {
                for (int j = 0; j < vtrlist.size() / 2; j++) {
                    CPoint t1 = (CPoint) vtrlist.get(j * 2);
                    CPoint t2 = (CPoint) vtrlist.get(j * 2 + 1);
                    if (t1 == pt) {
                        tx = t2.getx() + dx;
                        ty = t2.gety() + dx;
                        break;
                    }
                }
            }

            if (rotate) {
                double sin = Math.sin(ang);
                double cos = Math.cos(ang);
                tx -= x;
                ty -= y;
                double mx = (tx) * cos - (ty) * sin;
                double my = (tx) * sin + (ty) * cos;
                tx = mx + x;
                ty = my + y;
            }
            xpoints[i] = (int) tx;
            ypoints[i] = (int) ty;
        }

        this.setDraw(g2);

        Composite ac = null;
        if (overlap) {
            ac = g2.getComposite();
            g2.setComposite(CMisc.getFillComposite());
        }

        this.setDraw(g2);

        if (ftype == 0) {
            if (type == 0)
                g2.fillPolygon(xpoints, ypoints, n);
            else
                drawGrid(g2, xpoints, ypoints, n, 0, type);

            if (overlap)
                g2.setComposite(ac);

            if (rotate) {
                g2.setColor(Color.black);
                g2.setStroke(CMisc.DashedStroke);
            } else {
                if (selected)
                    g2.setStroke(CMisc.SelectPolygonStroke);

                g2.setColor(super.getColor().darker());
            }
            area = area(xpoints, ypoints, n);
            g2.drawPolygon(xpoints, ypoints, n);

        } else if (ftype == 1) {
            if (pointlist.size() >= 3) {
                double r = Math.sqrt(Math.pow(xpoints[1] - xpoints[2], 2) + Math.pow(ypoints[1] - ypoints[2], 2));
                int r1 = (int) r;

                if (type == 0)
                    g2.fillOval(xpoints[0] - r1, ypoints[0] - r1, 2 * r1, 2 * r1);
                else {
                }
                if (selected) {
                    g2.setStroke(CMisc.SelectPolygonStroke);
                    g2.setColor(super.getColor().darker());
                    g2.drawOval(xpoints[0] - r1, ypoints[0] - r1, 2 * r1, 2 * r1);
                }
            }
        }
        if (ac != null)
            g2.setComposite(ac);
    }

    /**
     * Retrieves the element at the specified index in the point list.
     *
     * @param n the index of the element
     * @return the element at the specified index, or null if the index is out of bounds
     */
    public CClass getElement(int n) {
        if (n < 0 || n >= pointlist.size())
            return null;
        return (CClass) pointlist.get(n);
    }

    /**
     * Retrieves the area of the polygon.
     *
     * @return the area of the polygon
     */
    public double getArea() {
        return area;
    }

    /**
     * Checks if all points in the polygon are free points.
     *
     * @return true if all points are free points, false otherwise
     */
    public boolean isAllPointsFree() {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            if (!pt.isAFreePoint())
                return false;
        }
        return true;
    }

    /**
     * Calculates the signed area of a triangle given its vertex coordinates.
     *
     * @param x1 the x-coordinate of the first vertex
     * @param y1 the y-coordinate of the first vertex
     * @param x2 the x-coordinate of the second vertex
     * @param y2 the y-coordinate of the second vertex
     * @param x3 the x-coordinate of the third vertex
     * @return the signed area of the triangle
     */
    public static double signArea(int x1, int y1, int x2, int y2, int x3, int y3) {
        return ((x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2)) / 2;
    }

    /**
     * Calculates the area of a polygon given its vertex coordinates.
     *
     * @param xPoints the x-coordinates of the vertices
     * @param yPoints the y-coordinates of the vertices
     * @param nPoints the number of vertices
     * @return the area of the polygon
     */
    public static double area(int xPoints[], int yPoints[], int nPoints) {
        if (nPoints < 4) return 0.0;

        double r = 0.0;
        for (int i = 1; i <= nPoints - 3; i++) {
            r += signArea(xPoints[0], yPoints[0], xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        return r;
    }

    /**
     * Checks if this polygon is equal to the given circle.
     *
     * @param c the circle to compare with
     * @return true if the polygon is equal to the circle, false otherwise
     */
    public boolean isEqual(Circle c) {
        if (c == null) return false;
        if (ftype != 1) return false;
        if (pointlist.size() < 3) return false;
        CPoint pp[] = c.getRadiusPoint();
        if (pp.length != 2) return false;

        return c.o == pointlist.get(0) &&
                ((pp[0] == pointlist.get(1) && pp[1] == pointlist.get(2))
                        || (pp[0] == pointlist.get(2) && pp[1] == pointlist.get(1)));
    }

    /**
     * Draws the polygon on the given graphics context with the selected option.
     *
     * @param g2 the graphics context
     * @param selected true if the polygon is selected, false otherwise
     */
    public void draw(Graphics2D g2, boolean selected) {
        draw(g2, selected, true, 0, 0, false, 0, 0, 0);
    }

    /**
     * Draws the polygon on the given graphics context.
     *
     * @param g2 the graphics context
     */
    public void draw(Graphics2D g2) {
        this.draw(g2, false);
    }

    /**
     * Retrieves the number of points in the polygon.
     *
     * @return the number of points in the polygon
     */
    public int getPtn() {
        return pointlist.size();
    }

    /**
     * Retrieves the point at the specified index in the point list.
     *
     * @param n the index of the point
     * @return the point at the specified index
     */
    public CPoint getPoint(int n) {
        return (CPoint) pointlist.get(n);
    }

    /**
     * Default constructor for the CPolygon class.
     * Initializes the polygon with default values.
     */
    public CPolygon() {
        super(CClass.POLYGON);
        pt1 = pt2 = null;
        pdx = pdy = 0;
    }

    /**
     * Constructor for the CPolygon class with a specified circle.
     * Initializes the polygon as a circle with the given circle's properties.
     *
     * @param c the circle to initialize the polygon with
     */
    public CPolygon(Circle c) {
        this();
        this.ftype = 1;
        pointlist.add(c.o);
        CPoint[] pp = c.getRadiusPoint();
        pointlist.add(pp[0]);
        pointlist.add(pp[1]);
        pointlist.add(c.o);
    }

/**
     * Sets the dragged points and their offsets.
     *
     * @param p1 the first dragged point
     * @param p2 the second dragged point
     * @param x the x-offset
     * @param y the y-offset
     */
    public void setDraggedPoints(CPoint p1, CPoint p2, double x, double y) {
        pt1 = p1;
        pt2 = p2;
        pdx = x;
        pdy = y;
    }

    /**
     * Resets the dragged points and their offsets to null and clears the vector list.
     */
    public void setDraggedPointsNull() {
        pt1 = null;
        pt2 = null;
        pdx = 0;
        pdy = 0;
        vtrlist.clear();
    }

    /**
     * Copies the properties of the given polygon to this polygon.
     *
     * @param c the polygon to copy from
     */
    public void copy(CPolygon c) {
        super.copy(c);
        grid = c.grid;
        slope = c.slope;
        this.type = c.type;
    }

    /**
     * Adds a pair of dragged points to the vector list.
     *
     * @param p1 the first point
     * @param p2 the second point
     */
    public void addDraggedPoints(CPoint p1, CPoint p2) {
        vtrlist.add(p1);
        vtrlist.add(p2);
    }

    /**
     * Checks if all points have been dragged.
     *
     * @return true if all points have been dragged, false otherwise
     */
    public boolean allDragged() {
        return vtrlist.size() == (pointlist.size() - 1) * 2;
    }

    /**
     * Retrieves the vector list of dragged points.
     *
     * @return the vector list of dragged points
     */
    public Vector getDraggedPoints() {
        return vtrlist;
    }

    /**
     * Retrieves the transformed points based on the dragged points.
     *
     * @return the vector list of transformed points
     */
    public Vector getTransformedPoints() {
        Vector vlist = new Vector();

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            boolean fd = false;
            for (int j = 0; j < vtrlist.size() / 2; j++) {
                CPoint t1 = (CPoint) vtrlist.get(j * 2);
                CPoint t2 = (CPoint) vtrlist.get(j * 2 + 1);
                if (pt == t1) {
                    vlist.add(t2);
                    fd = true;
                    break;
                }
            }
            if (!fd)
                vlist.add(pt);
        }
        return vlist;
    }

    /**
     * Retrieves the polygon type as a string.
     *
     * @return the polygon type string
     * @deprecated Use {@link #getPolygonTypeString(String)} instead.
     */
    @Deprecated
    public String getPolygonTypeString() {
        String ds;

        int size = pointlist.size() - 1;
        if (ftype == 0) {
            if (size == 3)
                ds = Language.getLs("triangle ");
            else if (size == 4)
                ds = Language.getLs("quadrangle ");
            else if (size == 5)
                ds = Language.getLs("pentagon ");
            else if (size == 6)
                ds = "hexagon ";
            else
                ds = "polygon";
            return ds;
        } else {
            return Language.getLs("Circle");
        }
    }

    /**
     * Retrieves the polygon type as a string with a specified format.
     *
     * @param s the format string
     * @return the formatted polygon type string
     */
    public String getPolygonTypeString(String s) {
        String ds;

        int size = pointlist.size() - 1;
        if (ftype == 0) {
            if (size == 3)
                ds = GExpert.getTranslationViaGettext("Triangle {0}", s);
            else if (size == 4)
                ds = GExpert.getTranslationViaGettext("Quadrangle {0}", s);
            else if (size == 5)
                ds = GExpert.getTranslationViaGettext("Pentagon {0}", s);
            else if (size == 6)
                ds = GExpert.getTranslationViaGettext("Hexagon {0}", s);
            else
                ds = GExpert.getTranslationViaGettext("Polygon {0}", s);
            return ds;
        } else {
            return GExpert.getTranslationViaGettext("Circle {0}", s);
        }
    }

    /**
     * Retrieves the polygon type as a string.
     *
     * @return the polygon type string
     * @deprecated Use {@link #getPolygonTypeString(String)} instead.
     */
    @Deprecated
    public String TypeString() {
        return getPolygonTypeString();
    }

    /**
     * Retrieves the polygon type as a string with a specified format.
     *
     * @param s the format string
     * @return the formatted polygon type string
     */
    public String TypeString(String s) {
        return getPolygonTypeString(s);
    }

    /**
     * Checks if the polygon is equal to another polygon by comparing their points in a rotated manner.
     *
     * @param v the vector of points to compare with
     * @return true if the polygons are equal, false otherwise
     */
    public boolean check_rdeq(Vector v) {
        if (!visible) return false;

        int n = pointlist.size();
        if (n != v.size() + 1) return false;
        n--;
        int i, j;

        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (pointlist.get(j) != v.get((j + i) % n))
                    break;
            }
            if (j == n)
                return true;
            else {
                for (j = 0; j < n; j++) {
                    if (pointlist.get(j) != v.get((i - j + n) % n))
                        break;
                }
                if (j == n)
                    return true;
            }
        }

        return false;
    }

    /**
     * Checks if the polygon is equal to another polygon by comparing their points.
     *
     * @param v the vector of points to compare with
     * @return true if the polygons are equal, false otherwise
     */
    public boolean check_eq(Vector v) {
        int n = pointlist.size();
        if (n != v.size()) return false;

        for (int i = 0; i < n; i++) {
            if (pointlist.get(i) != v.get(i))
                return false;
        }
        return true;
    }

/**
 * Sets the type of the polygon.
 *
 * @param type the type of the polygon
 */
void setType(int type) {
    this.type = type;
}

/**
 * Gets the type of the polygon.
 *
 * @return the type of the polygon
 */
int getType() {
    return type;
}

/**
 * Sets the grid size for the polygon.
 *
 * @param grid the grid size
 */
void setGrid(int grid) {
    this.grid = grid;
}

/**
 * Sets the slope for the polygon.
 *
 * @param s the slope value
 */
void setSlope(int s) {
    this.slope = s;
}

/**
 * Sets the points of the polygon.
 *
 * @param v the vector containing the points
 */
public void setPoints(Vector v) {
    pointlist.clear();
    pointlist.addAll(v);
    return;
}

/**
 * Adds a point to the polygon.
 *
 * @param p the point to add
 * @return true if the point was added, false if the point was already in the list or null
 */
public boolean addAPoint(CPoint p) {
    if (p == null) return false;
    if (pointlist.size() > 1 && p == pointlist.get(0)) {
        pointlist.add(p);
        return true;
    } else if (pointlist.contains(p))
        return false;
    else
        pointlist.add(p);
    return false;
}

/**
 * Selects the polygon if the given coordinates are within its bounds.
 *
 * @param x the x-coordinate to check
 * @param y the y-coordinate to check
 * @return true if the polygon is selected, false otherwise
 */
public boolean select(double x, double y) {
    if (visible == false) return false;

    if (ftype == 0) {
        Polygon poly = new Polygon();

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            poly.addPoint((int) pt.getx(), (int) pt.gety());
        }

        return poly.contains(x, y);
    } else {
        CPoint p1 = (CPoint) pointlist.get(0);
        CPoint p2 = (CPoint) pointlist.get(1);
        CPoint p3 = (CPoint) pointlist.get(2);
        double r = (Math.pow(p2.getx() - p3.getx(), 2) + Math.pow(p2.gety() - p3.gety(), 2));

        return Math.pow(x - p1.getx(), 2) + Math.pow(y - p1.gety(), 2) < r;
    }
}

/**
 * Gets the previous point in the polygon.
 *
 * @param p the current point
 * @return the previous point, or null if not found
 */
public CPoint getPreviousePoint(CPoint p) {
    for (int i = 0; i < pointlist.size() - 1; i++) {
        if (p == pointlist.get(i + 1))
            return (CPoint) pointlist.get(i);
    }
    return null;
}

/**
 * Gets the next point in the polygon.
 *
 * @param p the current point
 * @return the next point, or null if not found
 */
public CPoint getNextPoint(CPoint p) {
    for (int i = 1; i < pointlist.size(); i++) {
        if (p == pointlist.get(i - 1))
            return (CPoint) pointlist.get(i);
    }
    return null;
}

/**
 * Draws the polygon with an additional point on the given graphics context.
 *
 * @param g2 the graphics context
 * @param p the additional point to draw
 */
public void draw(Graphics2D g2, CPoint p) {
    if (visible == false) return;

    if (pointlist.size() == 0) return;

    int n = pointlist.size() + 2;
    int[] xpoints = new int[n];
    int[] ypoints = new int[n];

    for (int i = 0; i < pointlist.size(); i++) {
        CPoint pt = (CPoint) pointlist.get(i);
        xpoints[i] = (int) pt.getx();
        ypoints[i] = (int) pt.gety();
    }
    xpoints[n - 2] = (int) p.getx();
    ypoints[n - 2] = (int) p.gety();

    CPoint pt = (CPoint) pointlist.get(0);
    xpoints[n - 1] = (int) pt.getx();
    ypoints[n - 1] = (int) pt.gety();

    this.setDraw(g2);

    if (type == 0)
        g2.fillPolygon(xpoints, ypoints, n);
    else
        drawGrid(g2, xpoints, ypoints, n, 0, type);

    g2.setColor(super.getColor().darker().darker());

    g2.drawPolygon(xpoints, ypoints, n);

    g2.drawLine(xpoints[0], ypoints[0], (int) p.getx(), (int) p.gety());
    g2.drawLine(xpoints[n - 1], ypoints[n - 1], (int) p.getx(), (int) p.gety());
}

/**
 * Gets the description of the polygon.
 *
 * @return the description of the polygon
 */
public String getDescription() {
    String s = new String();
    if (pointlist.size() < 4) return "";

    int size = pointlist.size() - 1;

    for (int i = 0; i < size; i++) {
        CClass cc = (CClass) pointlist.get(i);
        s += cc.m_name;
    }
    if (ftype == 0)
        return TypeString(s);
    else return TypeString("(" + s + ")");
}


/**
     * Draws a grid pattern on the given graphics context based on the specified points and grid type.
     *
     * @param g2 the graphics context
     * @param xpoints the x-coordinates of the points
     * @param ypoints the y-coordinates of the points
     * @param n the number of points
     * @param dtype the draw type (0: draw, 1: print ps)
     * @param gtype the grid type (1: vertical, 2: horizontal, 3: both)
     * @return a vector of points if dtype is 1, otherwise null
     */
    public Vector drawGrid(Graphics2D g2, int[] xpoints, int[] ypoints, int n, int dtype, int gtype) {
        Vector vpl = null;
        if (dtype == 1)
            vpl = new Vector();

        if (n <= 3) return vpl;

        double k = Math.tan(slope * Math.PI / 180);
        int step = this.grid;

        int xmax, ymax;
        xmax = ymax = Integer.MIN_VALUE;
        int xmin, ymin;
        xmin = ymin = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (xpoints[i] > xmax)
                xmax = xpoints[i];
            if (ypoints[i] > ymax)
                ymax = ypoints[i];
            if (xpoints[i] < xmin)
                xmin = xpoints[i];
            if (ypoints[i] < ymin)
                ymin = ypoints[i];
        }

        double[] ov = new double[n];

        if (Math.abs(k) > 1 / CMisc.ZERO || Math.abs(k) < CMisc.ZERO) {

            if (gtype == 1 || gtype == 2) {
                double x = ((int) (xmin / step)) * step - step + 0.1;
                double y;
                while (x <= xmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i]) continue;
                        y = ypoints[i + 1] -
                                (xpoints[i + 1] - x) * (ypoints[i + 1] - ypoints[i]) / (xpoints[i + 1] - xpoints[i]);
                        if ((y - ypoints[i + 1]) * (y - ypoints[i]) < 0 || (x - xpoints[i + 1]) * (x - xpoints[i]) < 0)
                            np = add_sort(y, ov, np);
                    }
                    int dx = (int) x;
                    for (int j = 0; j < np - 1; j += 2) {
                        if (dtype == 0)
                            g2.drawLine(dx, (int) ov[j], dx, (int) ov[j + 1]);
                        else if (dtype == 1) {
                            vpl.add(new Point(dx, (int) ov[j]));
                            vpl.add(new Point(dx, (int) ov[j + 1]));
                        }
                    }
                    x += step;
                }
            }
            if (gtype == 1 || gtype == 3) {
                double y = ((int) (ymin / step)) * step - step + 0.1;
                double x;
                while (y <= ymax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (ypoints[i + 1] == ypoints[i])
                            continue;
                        x = xpoints[i + 1] -
                                (ypoints[i + 1] - y) * (xpoints[i + 1] - xpoints[i]) / (ypoints[i + 1] - ypoints[i]);
                        if ((y - ypoints[i + 1]) * (y - ypoints[i]) < 0 || (x - xpoints[i + 1]) * (x - xpoints[i]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    int dy = (int) y;
                    for (int j = 0; j < np - 1; j += 2) {
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], dy, (int) ov[j + 1], dy);
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], dy));
                            vpl.add(new Point((int) ov[j + 1], dy));
                        }
                    }
                    y += step;
                }
            }

        } else {

            if (gtype == 1 || gtype == 2) {
                double stepc = step * Math.sqrt(1 + k * k);
                double c1 = ymax - k * xmax;
                double c2 = ymax - k * xmin;
                double c3 = ymin - k * xmax;
                double c4 = ymin - k * xmin;
                double cmax = Math.max(c1, Math.max(c2, Math.max(c3, c4)));
                double cmin = Math.min(c1, Math.min(c2, Math.min(c3, c4)));
                double c = ((int) (cmin / stepc)) * stepc - stepc + stepc / 2;
                double x;

                while (c <= cmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i])
                            x = xpoints[i];
                        else {
                            double kl = ((double) (ypoints[i + 1] - ypoints[i])) / (xpoints[i + 1] - xpoints[i]);
                            x = (ypoints[i + 1] - kl * xpoints[i + 1] - c) / (k - kl);
                        }

                        if ((x - xpoints[i]) * (x - xpoints[i + 1]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    for (int j = 0; j < np - 1; j += 2)
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], (int) (k * ov[j] + c), (int) ov[j + 1], (int) (k * ov[j + 1] + c));
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], (int) (k * ov[j] + c)));
                            vpl.add(new Point((int) ov[j + 1], (int) (k * ov[j + 1] + c)));
                        }
                    c += stepc;
                }
            }

            if (gtype == 1 || gtype == 3) {
                k = -1.0 / k;

                double stepc = step * Math.sqrt(1 + k * k);
                double c1 = ymax - k * xmax;
                double c2 = ymax - k * xmin;
                double c3 = ymin - k * xmax;
                double c4 = ymin - k * xmin;
                double cmax = Math.max(c1, Math.max(c2, Math.max(c3, c4)));
                double cmin = Math.min(c1, Math.min(c2, Math.min(c3, c4)));
                double c = ((int) (cmin / stepc)) * stepc - stepc + stepc / 2;
                double x;
                while (c <= cmax) {
                    int np = 0;
                    for (int i = 0; i < n - 1; i++) {
                        if (xpoints[i + 1] == xpoints[i])
                            x = xpoints[i];
                        else {
                            double kl = ((double) (ypoints[i + 1] - ypoints[i])) / (xpoints[i + 1] - xpoints[i]);
                            x = (ypoints[i + 1] - kl * xpoints[i + 1] - c) / (k - kl);
                        }

                        if ((x - xpoints[i]) * (x - xpoints[i + 1]) < 0)
                            np = add_sort(x, ov, np);
                    }
                    for (int j = 0; j < np - 1; j += 2)
                        if (dtype == 0)
                            g2.drawLine((int) ov[j], (int) (k * ov[j] + c), (int) ov[j + 1], (int) (k * ov[j + 1] + c));
                        else if (dtype == 1) {
                            vpl.add(new Point((int) ov[j], (int) (k * ov[j] + c)));
                            vpl.add(new Point((int) ov[j + 1], (int) (k * ov[j + 1] + c)));
                        }
                    c += stepc;
                }
            }
        }
        return vpl;
    }

    /**
     * Adds a value to a sorted array and returns the new size of the array.
     *
     * @param a the value to add
     * @param b the array to add the value to
     * @param n the current size of the array
     * @return the new size of the array
     */
    private int add_sort(double a, double[] b, int n) {
        for (int i = 0; i < n; i++) {
            if (a < b[i]) {
                for (int j = n - 1; j >= i; j--)
                    b[j + 1] = b[j];
                b[i] = a;
                return n + 1;
            }
        }
        b[n] = a;
        return n + 1;
    }

    /**
     * Saves the polygon as a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the save type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (visible == false) return;

        int n = pointlist.size();

        if (n == 0) return;
        if (pointlist.get(0) != pointlist.get(n - 1))
            return;

        int[] xpoints = new int[n];
        int[] ypoints = new int[n];

        for (int i = 0; i < n; i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            xpoints[i] = (int) pt.getx();
            ypoints[i] = (int) pt.gety();
        }

        String s = "";

        if (type == 0) {
            for (int i = 0; i < pointlist.size() - 1; i++) {
                CPoint pt = (CPoint) pointlist.get(i);
                if (i == 0)
                    s += pt.getname() + " moveto ";
                else
                    s += pt.getname() + " lineto ";
            }
            s += "closepath";
            if (stype != 2)
                s += " Color" + m_color + " ";
            else {
                Color c = super.getColor();
                double gray = (int) ((0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 2.55) / 100.0;
                s += " " + gray + " " + gray + " " + gray + " setrgbcolor ";
            }
            s += "fill stroke\n";
            fp.write(s.getBytes());
        } else {
            for (int i = 0; i < pointlist.size(); i++) {
                CPoint pt = (CPoint) pointlist.get(i);
                if (i == 0)
                    s += pt.getname() + " moveto ";
                else
                    s += pt.getname() + " lineto ";
            }
            fp.write(s.getBytes());
            Vector vp = drawGrid(null, xpoints, ypoints, n, 1, type);
            String st = "";
            for (int i = 0; i < vp.size() / 2; i++) {
                Point p1 = (Point) vp.get(2 * i);
                Point p2 = (Point) vp.get(2 * i + 1);
                st += this.getPSLineString((int) p1.getX(), -(int) p1.getY(), (int) p2.getX(), -(int) p2.getY());
                if (i % 3 == 0)
                    st += "\n";
            }
            fp.write(st.getBytes());
            this.saveSuper(fp);
        }
    }

    /**
     * Saves the polygon data to a data output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeBoolean(showArea);
        out.writeInt(ftype);
        out.writeInt(type);
        out.writeInt(grid);
        out.writeInt(slope);

        out.writeInt(pointlist.size());
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            out.writeInt(p.m_id);
        }
    }

    /**
     * Loads the polygon data from a data input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        if (CMisc.version_load_now < 0.010) {
            m_id = in.readInt();
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int d = in.readInt();
                pointlist.add(dp.getPointById(d));
            }
            DrawType drawt = new DrawType();
            drawt.Load(in);
            m_color = drawt.color_index;
            {
                if (m_color == 1)
                    m_color = 3;
                else if (m_color == 2)
                    m_color = 5;
                else if (m_color == 3)
                    m_color = 11;
                else if (m_color == 7)
                    m_color = 8;
            }
            m_dash = drawt.dash;
            m_width = drawt.width;
        } else {
            super.Load(in, dp);
            if (CMisc.version_load_now >= 0.051) {
                showArea = in.readBoolean();
                ftype = in.readInt();
            }
            type = in.readInt();
            grid = in.readInt();
            slope = in.readInt();
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int d = in.readInt();
                pointlist.add(dp.getPointById(d));
            }
        }
    }
}
