package wprover;

import gprover.Cons;
import gprover.Gib;
import maths.CharSet;
import maths.TMono;
import maths.TPoly;
import maths.Param;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Vector;

/**
 * DrawBase is a class that provides methods for drawing geometric objects and handling user interactions.
 * It includes methods for drawing points, lines, circles, polygons, and other geometric shapes.
 * It also provides methods for handling mouse events and managing the drawing environment.
 */
public class DrawBase {
    final public static int D_POINT = 1;
    final public static int D_LINE = 2;
    final public static int D_PARELINE = 3;
    final public static int D_PERPLINE = 4;
    final public static int PERPWITHFOOT = 5;
    final public static int D_POLYGON = 6;
    final public static int D_CIRCLE = 9;
    final public static int D_3PCIRCLE = 10;
    final public static int D_MIDPOINT = 15;
    final public static int D_PSQUARE = 16;
    final public static int D_TEXT = 23;
    final public static int D_PFOOT = 25;
    final public static int D_CIRCLEBYRADIUS = 28;
    final public static int D_PTDISTANCE = 29;
    final public static int D_CCLINE = 21;
    final public static int D_SQUARE = 19;
    final public static int LRATIO = 30;
    final public static int D_PRATIO = 31;
    final public static int CIRCUMCENTER = 32;
    final public static int BARYCENTER = 33;
    final public static int ORTHOCENTER = 46;
    final public static int INCENTER = 47;
    final public static int D_TRATIO = 36;
    final public static int D_ANGLE = 17;
    final public static int SETEQANGLE = 18;
    final public static int MEET = 20;
    final public static int D_IOSTRI = 22;
    final public static int MIRROR = 24;
    final public static int DISTANCE = 26;

    final public static int H_LINE = 44;
    final public static int V_LINE = 45;
    final public static int D_ALINE = 50;
    final public static int D_ABLINE = 51;
    final public static int D_BLINE = 52;

    final public static int D_CIR_BY_DIM = 53;
    final public static int D_TCLINE = 54;
    final public static int CCTANGENT = 55;


    protected Vector pointlist = new Vector();
    protected Vector linelist = new Vector();
    protected Vector circlelist = new Vector();
    protected Vector anglelist = new Vector();
    protected Vector constraintlist = new Vector();
    protected Vector distancelist = new Vector();
    protected Vector polygonlist = new Vector();
    protected Vector textlist = new Vector();
    protected Vector tracelist = new Vector();
    protected Vector otherlist = new Vector();

    Vector flashlist = new Vector();


    protected Vector SelectList = new Vector();
    protected Vector CatchList = new Vector();
    protected CPoint CatchPoint = this.CreateATempPoint(0, 0);

    protected int MouseX, MouseY, mouseCatchX, mouseCatchY;
    protected int CatchType = 0;  // 1. middle ,  2. x pt,  3. y pt, 4: x & y.


    protected Param[] parameter = new Param[1024];
    protected double[] pptrans = new double[4];

    protected double[] paraBackup = new double[1024];
    protected TPoly polylist = null;
    protected TPoly pblist = null;

    protected int CurrentAction = 0;

    protected String name = "";
    protected double Width = 0;
    protected double Height = 0;

    protected int GridX = 40;
    protected int GridY = 40;
    protected boolean DRAWGRID = false;
    protected boolean SNAP = false;

    protected Color gridColor = CMisc.getGridColor(); //APPLET ONLY

    protected int paraCounter = 1;
    protected int pnameCounter = 0;
    protected int plineCounter = 1;
    protected int pcircleCounter = 1;

    protected static GeoPoly poly = GeoPoly.getPoly();
    protected static CharSet charset = CharSet.getinstance();

    protected boolean isPointOnObject = false;
    protected boolean isPointOnIntersection = false;
    protected double catchX, catchY;
    protected File file;
    protected boolean mouseInside = false;

    protected int STATUS = 0;

    protected GExpert gxInstance;
    protected Language lan;

    protected boolean footMarkShown = CMisc.isFootMarkShown();
    protected double footMarkLength = CMisc.FOOT_MARK_LENGTH;

    /**
     * The POOL array contains the types of geometric objects and their parameters.
     * Each entry in the array represents a different type of geometric object,
     * with the first element being the type identifier and the subsequent elements
     * representing the number of parameters required for that object.
     */
    final public static int[][] POOL =  //1: pt
            //2. line 3. Circle. 4. LC (line or circle).   
            {
                    {D_LINE, 1, 1},
                    {D_PARELINE, 2, 1},
                    {D_PERPLINE, 2, 1},
                    {PERPWITHFOOT, 1, 2},
                    {D_POLYGON, 1},
                    {D_CIRCLE, 1, 1},
                    {D_3PCIRCLE, 1, 1, 1},
                    {D_MIDPOINT, 1, 1},
                    {D_PFOOT, 1, 1},
                    {D_CIRCLEBYRADIUS, 1, 1, 1},
                    {D_PTDISTANCE, 1, 1, 1, 4},
                    {D_CCLINE, 3, 3},
                    {D_ABLINE, 2, 2},
                    {D_PRATIO, 1, 1, 1},
                    {D_ALINE, 2, 2, 2, 1},
                    {D_BLINE, 1, 1},
                    {D_TCLINE, 3, 1},
                    {MEET, 4, 4},
                    {PERPWITHFOOT, 1, 2},
                    {D_ANGLE, 2, 2},
                    {60, 1, 4},
                    {79, 3, 3},
                    {76, 1, 1, 1, 1},
                    {D_SQUARE, 1, 1},
                    {D_CCLINE, 3, 3},
                    {D_IOSTRI, 1, 1},
                    {MIRROR, 4, 2},
                    {87, 1, 1, 4},
                    {43, 1},
                    {D_PTDISTANCE, 1, 1},
                    {CIRCUMCENTER, 1, 1, 1},
                    {BARYCENTER, 1, 1, 1},
                    {ORTHOCENTER, 1, 1, 1},
                    {INCENTER, 1, 1, 1},
                    {DISTANCE, 1, 1},
                    {65, 1, 1},
                    {72, 1, 1, 1},
                    {73, 1, 1},
                    {74, 1, 1, 1, 1},
                    {75, 1, 1},
                    {82, 1, 1, 1, 1, 1, 1, 1, 1},
                    {81, 2, 1}
            };

    /**
     * Returns the number of parameters for the specified geometric object type.
     * For a polygon (\_D\_POLYGON), returns the current status if less than 10, otherwise 0.
     * For other types, iterates through the POOL array and returns the parameter count.
     * Returns -1 if the type is not found.
     *
     * @param a the geometric object type identifier
     * @return the number of parameters for the object type, or -1 if not found
     */
    final public int getPooln(int a) {
        if (a == D_POLYGON) {
            if (STATUS < 10)
                return STATUS;
            else return 0;
        }

        for (int i = 0; i < POOL.length; i++) {
            if (POOL[i][0] == a)
                return POOL[i].length - 1;
        }

        return -1;
    }

    /**
     * Returns the parameter at a specific index for the given geometric object type.
     * Iterates through the POOL array and returns the value at the specified index.
     * If the index is out of bounds, returns 1.
     *
     * @param a the geometric object type identifier
     * @param index the index of the parameter to retrieve
     * @return the parameter value at the given index, or 1 if not found
     */
    public int getPoolA(int a, int index) {
        for (int i = 0; i < POOL.length; i++) {
            if (POOL[i][0] == a) {
                if (POOL[i].length > index)
                    return POOL[i][index];
            }
        }
        return 1;
    }

    /**
     * Sets the language for the drawing environment.
     *
     * @param lan the Language instance to be set
     */
    public void setLanguage(Language lan) {
        this.lan = lan;
    }

    /**
     * Handles a button down event at the specified coordinates.
     * The implementation depends on the current action.
     *
     * @param x the x-coordinate of the button down event
     * @param y the y-coordinate of the button down event
     */
    public void DWButtonDown(double x, double y) {
        switch (CurrentAction) {
        }
    }

    /**
     * Sets the state indicating whether the mouse is inside the drawing area.
     *
     * @param t true if the mouse is inside, false otherwise
     */
    public void setMouseInside(boolean t) {
        mouseInside = t;
    }

    /**
     * Creates a temporary point with the specified coordinates.
     *
     * @param x the x-coordinate of the temporary point
     * @param y the y-coordinate of the temporary point
     * @return a new temporary CPoint instance
     */
    final public CPoint CreateATempPoint(double x, double y) {
        Param p1 = new Param(-1, x);
        Param p2 = new Param(-1, y);
        return new CPoint(CPoint.TEMP_POINT, p1, p2);
    }

    /**
     * Finds and returns an existing line that connects the two specified points.
     * Returns null if no such line exists or if either point is null.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the CLine connecting the two points, or null if not found
     */
    public CLine fd_line(CPoint p1, CPoint p2) {
        if (p1 == null || p2 == null) {
            return null;
        }
        for (int i = 0; i < linelist.size(); i++) {
            CLine ln = (CLine) linelist.get(i);
            if (ln.points.contains(p1) && ln.points.contains(p2)) {
                return ln;
            }
        }
        return null;
    }

    /**
     * Sets the antialiasing rendering hint on the provided Graphics2D object based on the application setting.
     *
     * @param g2 the Graphics2D object on which to set the anti-aliasing
     */
    final public void setAntiAlias(Graphics2D g2) {
        if (CMisc.AntiAlias) {
            RenderingHints qualityHints = new RenderingHints(RenderingHints.
                    KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHints(qualityHints);
        } else {
            RenderingHints qualityHints = new RenderingHints(RenderingHints.
                    KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHints(qualityHints);

        }
    }

    /**
     * Draws the perpendicular footmarks for constraints.
     * Iterates through the constraint list and draws the foot for each applicable constraint.
     * Supports constraint types such as PERPENDICULAR, PFOOT, RIGHT\_ANGLED\_TRIANGLE,
     * and RIGHT\_ANGLE\_TRAPEZOID.
     *
     * @param g2 the Graphics2D object used for drawing
     * @param vlist a Vector used for additional drawing information
     * @param type the mode type (0 for drawing and 1 for PostScript)
     */
    final public void drawPerpFoot(Graphics2D g2, Vector vlist, int type) { // 0: draw ,1: ps
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            double x, y;
            int n = cs.GetConstraintType();
            switch (n) {

                case Constraint.PERPENDICULAR: {
                    if (cs.getelement(0) instanceof CPoint)
                        continue;

                    CPoint p1, p2;
                    CLine line1 = (CLine) cs.getelement(0);
                    CLine line2 = (CLine) cs.getelement(1);
                    if (!line1.isdraw() || !line2.isdraw())
                        continue;
                    CPoint pt = null;
                    if ((pt = CLine.commonPoint(line1, line2)) == null) {
                        double lc[] = CLine.Intersect(line1, line2);
                        if (lc == null) {
                            continue;
                        }
                        x = lc[0];
                        y = lc[1];
                        if (!line1.inside(x, y) || !(line2.inside(x, y))) {
                            continue;
                        }
                        p1 = line1.getMaxXPoint();
                        p2 = line2.getMaxXPoint();
                    } else {
                        x = pt.getx();
                        y = pt.gety();
                        p1 = line1.getAPointBut(pt);
                        p2 = line2.getAPointBut(pt);
                    }
                    drawTTFoot(type, vlist, g2, x, y, null, p1, p2);
                }
                break;
                case Constraint.PFOOT: {
                    CPoint PC = null;
                    CPoint p1, p2;

                    PC = (CPoint) cs.getelement(0);
                    x = PC.getx();
                    y = PC.gety();
                    p1 = (CPoint) cs.getelement(1);
                    CPoint tp1 = (CPoint) cs.getelement(2);
                    CPoint tp2 = (CPoint) cs.getelement(3);
                    if (tp1.getx() > tp2.getx()) {
                        p2 = tp1;
                    } else {
                        p2 = tp2;
                    }
                    if (!this.find_tmark(PC, tp1, PC, tp2))
                        drawTTFoot(type, vlist, g2, x, y, PC, p1, p2);
                }
                break;
                case Constraint.SQUARE:
                case Constraint.RECTANGLE: {
                    CPoint p1 = (CPoint) cs.getelement(0);
                    CPoint p2 = (CPoint) cs.getelement(1);
                    CPoint p3 = (CPoint) cs.getelement(2);
                    CPoint p4 = (CPoint) cs.getelement(3);

                }
                break;
                case Constraint.RIGHT_ANGLED_TRIANGLE: {
                    CPoint p1 = (CPoint) cs.getelement(0);
                    CPoint p2 = (CPoint) cs.getelement(1);
                    CPoint p3 = (CPoint) cs.getelement(2);
                    drawTTFoot(type, vlist, g2, p1.getx(), p1.gety(), p1, p2, p3);

                }
                break;
                case Constraint.RIGHT_ANGLE_TRAPEZOID: {
                    CPoint p1 = (CPoint) cs.getelement(0);
                    CPoint p2 = (CPoint) cs.getelement(1);
                    CPoint p3 = (CPoint) cs.getelement(2);
                    CPoint p4 = (CPoint) cs.getelement(3);
                    drawTTFoot(type, vlist, g2, p1.getx(), p1.gety(), p1, p2, p4);
                    drawTTFoot(type, vlist, g2, p4.getx(), p4.gety(), p4, p1, p3);
                }
                break;
                default:
                    break;
            }
        }

    }

    /**
     * Removes the last n elements from the provided Vector.
     *
     * @param v the Vector from which elements will be removed
     * @param n the number of elements to remove from the end
     */
    final public void removeFromeListLastNElements(Vector v, int n) {
        if (v.size() < n) return;
        while (n-- > 0)
            v.remove(v.size() - 1);
    }

    /**
     * Returns the number of points in the drawing.
     *
     * @return the size of the point list
     */
    final public int getPointSize() {
        return pointlist.size();
    }

    /**
     * Returns a copy of the point list.
     *
     * @return a new Vector containing all points.
     */
    final public Vector getPointList() {
        Vector v = new Vector();
        v.addAll(pointlist);
        return v;
    }

    /**
     * Draws all objects in the given list using the provided Graphics2D context.
     *
     * @param list the Vector containing drawable objects.
     * @param g2 the Graphics2D object used for drawing.
     */
    final public void drawList(Vector list, Graphics2D g2) {
        if (list == null || list.size() == 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            CClass cc = (CClass) list.get(i);
            cc.draw(g2);
        }
    }

    /**
     * Draws the name and coordinate location of a point.
     *
     * @param p the point whose location is displayed.
     * @param g2 the Graphics2D context used for drawing.
     */
    final public void drawPointNameLocation(CPoint p, Graphics2D g2) {
        g2.drawString("(x: " + ((int) p.getx()) + ", y: " +
                (int) p.gety() + ")",
                (int) p.getx() + 23, (int) p.gety() - 5); // FIXME: 23 and 5 seem hardcoded
    }

    /**
     * Sets the current drawing environment parameters such as color and stroke.
     *
     * @param g2 the Graphics2D object where the environment settings are applied.
     */
    final public void setCurrentDrawEnvironment(Graphics2D g2) {
        g2.setColor(DrawData.getCurrentColor());
        g2.setStroke(CMisc.NormalLineStroke);
    }

    /**
     * Draws the grid on the drawing area if grid drawing or snapping is enabled.
     *
     * @param g2 the Graphics2D context used for drawing the grid.
     */
    final public void drawGrid(Graphics2D g2) {
        if (!this.DRAWGRID && !SNAP) {
            return;
        }
        if (CMisc.isApplication())
            g2.setColor(CMisc.getGridColor());
        else
            g2.setColor(gridColor); //APPLET ONLY.
        //g2.setColor(CMisc.getGridColor());
        int nx = (int) this.Width / this.GridX;
        int ny = (int) this.Height / this.GridY;

        int st = 0;

        int x, y;
        for (int i = 0; i <= nx; i++) {
            x = st + i * GridX;
            g2.drawLine(x, 0, x, (int) Height);
        }
        for (int i = 0; i <= ny; i++) {
            y = st + i * GridY;
            g2.drawLine(0, y, (int) Width, y);
        }

    }

    /**
     * Draws a tip triangle marker based on the provided points.
     *
     * @param p1 the first point defining the triangle.
     * @param p2 the second point defining the triangle.
     * @param p the reference point for triangle alignment.
     * @param g2 the Graphics2D context used for drawing.
     */
    final public void drawTipTirangle(CPoint p1, CPoint p2, CPoint p,
                                      Graphics2D g2) {

        double x0 = p1.getx();
        double y0 = p1.gety();

        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double cy = ry / rr;
        double cx = rx / rr;
        double r;
        boolean isleft = false;

        if (Math.abs(rx) < CMisc.ZERO) {
            if (ry * (p1.getx() - p.getx()) > 0) {
                isleft = false;
            } else {
                isleft = true;
            }
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) /
                    Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0);
        }

        double x1 = (x0 + p2.getx()) / 2;
        double y1 = (y0 + p2.gety()) / 2;
        double x2, y2;
        if (isleft) {
            x2 = (x1 + r * cy);
            y2 = (y1 - r * cx);
        } else {
            x2 = x1 - r * cy;
            y2 = y1 + r * cx;
        }
        CatchPoint.setXY(x2, y2);
        drawCatchRect(g2);

        g2.setColor(Color.red);
        g2.drawLine((int) (x0), (int) (y0), (int) (x2), (int) (y2));
        g2.drawLine((int) p2.getx(), (int) p2.gety(), (int) (x2), (int) (y2));
        g2.drawLine((int) (p1.getx()), (int) (p1.gety()), (int) (p2.getx()), (int) (p2.gety()));
        g2.setStroke(CMisc.DashedStroke);

        if (Math.abs(cy) < CMisc.ZERO) {
            g2.drawLine((int) x1, 0, (int) x1, (int) this.Height);
        } else {
            double k = -cx / cy;
            g2.drawLine((int) (0), (int) (y1 - x1 * k), (int) (this.Width),
                    (int) (y1 + (this.Width - x1) * k));
        }
        catchX = x2;
        catchY = y2;
    }

    /**
     * Draws a cross centered at the given coordinates with the specified half-width.
     *
     * @param x the x-coordinate of the center.
     * @param y the y-coordinate of the center.
     * @param w the half-width of the cross.
     * @param g2 the Graphics2D context used for drawing.
     */
    final public void drawCross(int x, int y, int w, Graphics2D g2) {
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(x - w, y - w, x + w, y + w);
        g2.drawLine(x + w, y - w, x - w, y + w);
    }

    /**
     * Draws a catch rectangle around the catch point if certain conditions are met.
     *
     * @param g2 the Graphics2D context used for drawing.
     */
    public void drawCatchRect(Graphics2D g2) {
        if (!isPointOnObject || !mouseInside) return;
        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1.0f));
        if (!isPointOnIntersection) {
            drawRect(x - 5, y - 5, x + 5, y + 5, g2);
            if (CatchType == 1)
                g2.drawString(GExpert.getLanguage("Middle Point"), x + 10, y);
        } else {
            drawCatchInterCross(g2);
        }
    }

    /**
     * Draws a cross marker to indicate an intersection catch point.
     *
     * @param g2 the Graphics2D context used for drawing.
     */
    public void drawCatchInterCross(Graphics2D g2) {
        if (!isPointOnIntersection) return;
        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();
        g2.setColor(Color.red);
        this.drawCross(x, y, 5, g2);
        g2.setFont(CMisc.font);
        g2.drawString(GExpert.getLanguage("Intersection"), x + 10, y);
    }

    /**
     * Draws a tip rectangle around the specified coordinates.
     *
     * @param x the x-coordinate for the tip rectangle.
     * @param y the y-coordinate for the tip rectangle.
     * @param g2 the Graphics2D context used for drawing.
     */
    public void drawTipRect(int x, int y, Graphics2D g2) {
        g2.setColor(Color.red);
        this.drawRect(x - 5, y - 5, x + 5, y + 5, g2);
    }

    /**
     * Draws either a point or a cross based on the object's state.
     *
     * @param g2 the Graphics2D context used for drawing.
     */
    public void drawPointOrCross(Graphics2D g2) {
        if (this.isPointOnObject) {
            if (!isPointOnIntersection)
                this.drawCross((int) CatchPoint.getx(), (int) CatchPoint.gety(), 5, g2);
            else
                drawCatchInterCross(g2);
        } else {
            drawpoint(CatchPoint, g2);
        }
    }

    /**
     * Draws the name of the caught object if exactly one object is caught.
     *
     * @param g2 the Graphics2D context used for drawing.
     */
    public void drawCatchObjName(Graphics2D g2) {
        if (CatchList.size() != 1)
            return;
        if (!CMisc.DRAW_CATCH_OBJECT_NAME)
            return;
        CClass c = (CClass) CatchList.get(0);
        g2.setColor(Color.red);
        g2.setFont(CMisc.font);
        String s = c.getname();
        if (s != null)
            g2.drawString(s, MouseX + 16, MouseY + 20);
    }

    /**
     * Draws a tip square marker using the provided points.
     *
     * @param p1 the first point defining the square.
     * @param p2 the second point defining the square.
     * @param p the reference point used for adjusting the square.
     * @param g2 the Graphics2D context used for drawing.
     */
    final public void drawTipSquare(CPoint p1, CPoint p2, CPoint p,
                                    Graphics2D g2) {
        double x0 = p1.getx();
        double y0 = p1.gety();

        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double cy = ry / rr;
        double cx = rx / rr;
        double r;
        boolean isleft = false;

        if (Math.abs(rx) < CMisc.ZERO) {
            if (ry * (p1.getx() - p.getx()) > 0) {
                isleft = false;
            } else {
                isleft = true;
            }
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) /
                    Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0); //((ry * dx / rx - dy > 0 && ry / rx > 0) || (ry * dx / rx - dy < 0 && ry / rx < 0));
        }

        int n = (int) (r / rr) + 1;
        if (Math.abs(n * rr - r) < 2 * CMisc.PIXEPS) {
            r = rr * n;
        }

        g2.setColor(Color.red);
        g2.drawLine((int) x0, (int) y0, (int) p2.getx(), (int) p2.gety());
        if (isleft) {
            for (int i = 1; i <= n; i++) {
                g2.drawLine((int) x0, (int) y0, (int) (x0 + i * ry),
                        (int) (y0 - i * rx));
                g2.drawLine((int) (x0 + i * ry), (int) (y0 - i * rx),
                        (int) (x0 + i * ry + rx), (int) (y0 - i * rx + ry));
                g2.drawLine((int) (x0 + rx), (int) (y0 + ry),
                        (int) (x0 + i * ry + rx), (int) (y0 - i * rx + ry));
            }
            g2.drawLine((int) (p1.getx() + r * cy), (int) (p1.gety() - r * cx),
                    (int) (p2.getx() + r * cy), (int) (p2.gety() - r * cx));
        } else {
            for (int i = 1; i <= n; i++) {
                g2.drawLine((int) x0, (int) y0, (int) (x0 - i * ry),
                        (int) (y0 + i * rx));
                g2.drawLine((int) (x0 + rx), (int) (y0 + ry),
                        (int) (x0 + rx - i * ry), (int) (y0 + ry + i * rx));
                g2.drawLine((int) (x0 + rx - i * ry), (int) (y0 + ry + i * rx),
                        (int) (x0 - i * ry), (int) (y0 + i * rx));
            }
            g2.drawLine((int) (p1.getx() - r * cy), (int) (p1.gety() + r * cx),
                    (int) (p2.getx() - r * cy), (int) (p2.gety() + r * cx));
        }
    }

    /**
     * Draws two footmarks for a constraint between two lines.
     *
     * @param type the drawing mode (0 for direct drawing, non-zero for vector accumulation)
     * @param vlist the vector list to add drawing points if not drawing directly
     * @param g2 the Graphics2D context to draw on
     * @param x the starting x coordinate for the footmark
     * @param y the starting y coordinate for the footmark
     * @param pc the common point for both lines (may be null)
     * @param p1 the first point defining the first line
     * @param p2 the second point defining the second line
     */
    public void drawTTFoot(int type, Vector vlist, Graphics2D g2, double x, double y, CPoint pc, CPoint p1, CPoint p2) {
        if (p1 == null || p2 == null) return;

        if (CMisc.isApplication() && !CMisc.isFootMarkShown()) return;

        if (!isLineDrawn(pc, p1) || !isLineDrawn(pc, p2))
            return;
        if (this.findCTMark(pc, p1, pc, p2) != null)
            return;

        double step = footMarkLength;  //APPLET ONLY.
        if (CMisc.isApplication())
            step = CMisc.FOOT_MARK_LENGTH;

        double dx = p1.getx() - x;
        double dy = p1.gety() - y;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) return;
        dx = (dx / len) * step;
        dy = (dy / len) * step;

        double dx1, dy1;
        dx1 = p2.getx() - x;
        dy1 = p2.gety() - y;
        len = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        if (len == 0.0) return;
        dx1 = (dx1 / len) * step;
        dy1 = (dy1 / len) * step;

        double fx = x;
        double fy = y;
        double ex = fx + dx1 + dx;
        double ey = fy + dy1 + dy;
        if (type == 0) {
            g2.setColor(Color.red);
            g2.setStroke(CMisc.NormalLineStroke);
            g2.drawLine((int) (fx + dx), (int) (fy + dy), (int) (ex), (int) (ey));
            g2.drawLine((int) (fx + dx1), (int) (fy + dy1), (int) (ex), (int) (ey));
        } else {
            Point m1 = new Point((int) (fx + dx), (int) (fy + dy));
            Point m2 = new Point((int) (ex), (int) (ey));
            Point m3 = new Point((int) (fx + dx1), (int) (fy + dy1));
            Point m4 = new Point((int) (ex), (int) (ey));
            vlist.add(m1);
            vlist.add(m2);
            vlist.add(m3);
            vlist.add(m4);
        }
    }

    /**
     * Draws the catch indicator based on the current catch list and catch point.
     * If no catch objects exist, draws smart horizontal/vertical catch lines.
     * If one object exists, draws it with its predefined style.
     * If multiple objects exist, displays a prompt for selection.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void drawCatch(Graphics2D g2) {
        int size = CatchList.size();

        int x = (int) CatchPoint.getx();
        int y = (int) CatchPoint.gety();

        CClass cc = null;
        if (size == 0) {
            if (CMisc.SMART_HV_LINE_CATCH) {
                if (CatchType == 2 || CatchType == 4) {
                    CPoint pt = this.getCatchHVPoint(2);
                    if (pt != null) {
                        g2.setColor(Color.red);
                        g2.setStroke(CMisc.DashedStroke);
                        g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) pt.getx(), (int) y);
                    }
                }
                if (CatchType == 3 || CatchType == 4) {
                    CPoint pt = this.getCatchHVPoint(3);
                    if (pt != null) {
                        g2.setColor(Color.red);
                        g2.setStroke(CMisc.DashedStroke);
                        g2.drawLine((int) pt.getx(), (int) pt.gety(), x, (int) pt.gety());
                    }
                }
            }
        } else if (size == 1) {
            cc = (CClass) CatchList.get(0);
            cc.setDraw(g2);
            if (cc.m_type == CClass.POLYGON) {
                Color c = cc.getColor();
                g2.setColor(new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()));
            }
        } else {
            if (!isPointOnIntersection) {
                g2.setFont(CMisc.font);
                g2.setColor(Color.red);
                g2.drawString("(" + size + ") " + GExpert.getLanguage("Which?"), x + 10, y + 25);
            }
        }

    }

    /**
     * Checks if a line connecting the two given points is drawn.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return true if the line exists and is drawn; false otherwise
     */
    public boolean isLineDrawn(CPoint p1, CPoint p2) {
        CLine ln = this.fd_line(p1, p2);
        return ln != null && ln.isdraw();
    }

    /**
     * Draws the selection highlight for a list of geometric objects.
     * Iterates over the list and draws each object with selection indication.
     *
     * @param list the list of objects to be highlighted
     * @param g2 the Graphics2D context to use for drawing
     */
    public void drawSelect(Vector list, Graphics2D g2) {
        for (int i = 0; i < list.size(); i++) {
            CClass cc = (CClass) list.get(i);
            if (cc != null)
                cc.draw(g2, true);
        }
    }

    /**
     * Draws a rectangle defined by two opposite corner coordinates.
     * Four lines are drawn between the specified corners.
     *
     * @param x the x coordinate of the first corner
     * @param y the y coordinate of the first corner
     * @param x1 the x coordinate of the opposite corner
     * @param y1 the y coordinate of the opposite corner
     * @param g2 the Graphics2D context for drawing
     */
    public void drawRect(int x, int y, int x1, int y1, Graphics2D g2) {
        g2.drawLine(x, y, x1, y);
        g2.drawLine(x, y, x, y1);
        g2.drawLine(x, y1, x1, y1);
        g2.drawLine(x1, y, x1, y1);
    }

    /**
     * Draws a circle defined by two points.
     * The first point represents the center and the distance to the
     * second point determines the radius.
     *
     * @param x1 the x coordinate of the center
     * @param y1 the y coordinate of the center
     * @param x2 the x coordinate of a point on the circle
     * @param y2 the y coordinate of a point on the circle
     * @param g2 the Graphics2D context for drawing
     */
    public void drawcircle2p(double x1, double y1, double x2, double y2,
                             Graphics2D g2) {
        int r = (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        g2.drawOval((int) (x1 - r), (int) (y1 - r), 2 * r, 2 * r);
    }

    /**
     * Draws the specified point using its own drawing method.
     *
     * @param p the point to be drawn
     * @param g2 the Graphics2D context for drawing
     */
    public void drawpoint(CPoint p, Graphics2D g2) {
        p.draw(g2);
    }

    /**
     * Adds the specified line to the list of lines if it is not already present.
     *
     * @param ln the line to be added
     */
    public void addLine(CLine ln) {
        if (!linelist.contains(ln)) {
            linelist.add(ln);
        }
    }

    /**
     * Adds the specified circle to the list of circles if it is not already present.
     *
     * @param c the circle to add
     */
    public void addCircle(Circle c) {
        if (!circlelist.contains(c)) {
            circlelist.add(c);
        }
    }

    /**
     * Searches for a point with the given name in the point list.
     *
     * @param name the name of the point to search for
     * @return the point with the specified name, or null if not found
     */
    public CPoint findPoint(String name) {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (p.getname().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Finds a circle defined by a center point and a point on its circumference.
     *
     * Searches the circle list for a circle where the first point is the center and
     * the second point lies on the circle.
     *
     * @param p1 the potential center point of the circle
     * @param p2 the potential point on the circumference
     * @return the matching circle if found; otherwise, null
     */
    public Circle fd_circle(CPoint p1, CPoint p2) {
        if (p1 == null || p2 == null) {
            return null;
        }

        for (int i = 0; i < circlelist.size(); i++) {
            Circle cc = (Circle) circlelist.get(i);
            if (cc.points.contains(p2) && cc.o == p1) {
                return cc;
            }
        }
        return null;

    }

    /**
     * Returns the count of Cedmark objects present in otherlist.
     *
     * @return the number of Cedmark marks in otherlist.
     */
    int getEMarkNum() {
        int k = 0;
        for (int i = 0; i < otherlist.size(); i++) {
            if (otherlist.get(i) instanceof Cedmark) {
                k++;
            }
        }
        return k;
    }

    /**
     * Calculates and returns the bounding rectangle that encompasses all points, circles, and text elements.
     *
     * @return the bounding Rectangle of the drawing.
     */
    public Rectangle getBounds() {

        Rectangle rc = new Rectangle(0, 0, 0, 0);
        Vector v = pointlist;
        double x, y, x1, y1;
        x = y = Integer.MIN_VALUE;
        x1 = y1 = Integer.MAX_VALUE;

        if (v.size() != 0) {
            for (int i = 0; i < v.size(); i++) {
                CPoint p = (CPoint) v.get(i);
                double x0 = p.getx();
                double y0 = p.gety();
                if (x1 > x0) {
                    x1 = x0;
                }
                if (x < x0) {
                    x = x0;
                }
                if (y1 > y0) {
                    y1 = y0;
                }
                if (y < y0) {
                    y = y0;
                }
            }
            for (int i = 0; i < circlelist.size(); i++) {
                Circle c = (Circle) circlelist.get(i);
                double r = c.getRadius();

                if (x1 > c.o.getx() - r) {
                    x1 = c.o.getx() - r;
                }
                if (y1 > c.o.gety() - r) {
                    y1 = c.o.gety() - r;
                }
                if (x < c.o.getx() + r) {
                    x = c.o.getx() + r;
                }
                if (y < c.o.gety() + r) {
                    y = c.o.gety() + r;
                }
            }
        }

        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            Dimension dm = t.getTextDimension();
            int w = (int) dm.getWidth();
            int h = (int) dm.getHeight();
            int xt = t.getSX();
            int yt = t.getSY();
            if (x < xt + w) {
                x = xt + w;
            }
            if (y < yt + h) {
                y = yt + h;
            }
            if (x1 > xt) {
                x1 = xt;
            }
            if (y1 > yt) {
                y1 = yt;
            }
        }
        if (x1 < 0)
            x1 = 0;
        if (y1 < 0)
            y1 = 0;
        if (x > Width)
            x = Width;
        if (y > Height)
            y = Height;
        rc.setBounds((int) x1, (int) y1, (int) (x - x1), (int) (y - y1));
        return rc;
    }

    /**
     * Checks if three points are collinear.
     *
     * @param p1 the first point.
     * @param p2 the second point.
     * @param p3 the third point.
     * @return true if the points are collinear, false otherwise.
     */
    static boolean check_Collinear(CPoint p1, CPoint p2, CPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return isZero((p2.getx() - p1.getx()) * (p3.gety() - p2.gety()) -
                (p2.gety() - p1.gety()) * (p3.getx() - p2.getx()));
    }

    /**
     * Checks if the three points defined by their coordinates are collinear.
     *
     * @param x1 the x-coordinate of the first point.
     * @param y1 the y-coordinate of the first point.
     * @param x2 the x-coordinate of the second point.
     * @param y2 the y-coordinate of the second point.
     * @param x3 the x-coordinate of the third point.
     * @param y3 the y-coordinate of the third point.
     * @return true if the points are collinear, false otherwise.
     */
    static boolean check_Collinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        return
                isZero((x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2));

    }

    /**
     * Computes the signed area determined by three points.
     *
     * @param x1 the x-coordinate of the first point.
     * @param y1 the y-coordinate of the first point.
     * @param x2 the x-coordinate of the second point.
     * @param y2 the y-coordinate of the second point.
     * @param x3 the x-coordinate of the third point.
     * @param y3 the y-coordinate of the third point.
     * @return the signed area value.
     */
    public static double signArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2);
    }

    /**
     * Determines if a point lies between two other collinear points.
     *
     * @param p1 the first endpoint.
     * @param p2 the second endpoint.
     * @param p3 the point to check.
     * @return true if p3 lies between p1 and p2, false otherwise.
     */
    static boolean check_between(CPoint p1, CPoint p2, CPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        if (!check_Collinear(p1, p2, p3))
            return false;

        return (p1.gety() - p2.gety()) * (p1.gety() - p3.gety()) < 0
                || (p1.getx() - p2.getx()) * (p1.getx() - p3.getx()) < 0;


    }

    /**
     * Checks if two lines are parallel.
     *
     * @param ln1 the first line.
     * @param ln2 the second line.
     * @return true if the lines are parallel, false otherwise.
     */
    static boolean check_para(CLine ln1, CLine ln2) {
        double k1 = ln1.getK();
        double k2 = ln2.getK();
        return isZero(k1 - k2);
    }

    /**
     * Checks if two pairs of points define parallel lines.
     *
     * @param p1 the first point of the first line.
     * @param p2 the second point of the first line.
     * @param p3 the first point of the second line.
     * @param p4 the second point of the second line.
     * @return true if the defined lines are parallel, false otherwise.
     */
    static boolean check_para(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero((p2.getx() - p1.getx()) * (p4.gety() - p3.gety()) -
                (p2.gety() - p1.gety()) * (p4.getx() - p3.getx()));
    }

    /**
     * Checks if two lines, defined by two pairs of points, are perpendicular.
     *
     * @param p1 the first point of the first line.
     * @param p2 the second point of the first line.
     * @param p3 the first point of the second line.
     * @param p4 the second point of the second line.
     * @return true if the lines are perpendicular, false otherwise.
     */
    static boolean check_perp(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(Math.abs((p2.getx() - p1.getx()) * (p4.getx() - p3.getx()) +
                (p2.gety() - p1.gety()) * (p4.gety() - p3.gety())));
    }

    /**
     * Checks if a point is the midpoint of two other points.
     *
     * @param p the point to check.
     * @param p1 the first endpoint.
     * @param p2 the second endpoint.
     * @return true if p is the midpoint of p1 and p2, false otherwise.
     */
    static boolean check_mid(CPoint p, CPoint p1, CPoint p2) {
        if (p == null || p1 == null || p2 == null) {
            return false;
        }
        return check_Collinear(p1, p2, p) && check_eqdistance(p, p1, p, p2);
    }

    /**
     * Determines if four points are concyclic, i.e., lie on the same circle.
     *
     * @param p1 the first point.
     * @param p2 the second point.
     * @param p3 the third point.
     * @param p4 the fourth point.
     * @return true if the four points are concyclic, false otherwise.
     */
    static boolean check_cyclic(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double k1 = (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
        double k2 = (p4.gety() - p3.gety()) / (p4.getx() - p3.getx());
        k1 = -1 / k1;
        k2 = -1 / k2;
        double x1 = (p1.getx() + p2.getx()) / 2;
        double y1 = (p1.gety() + p2.gety()) / 2;
        double x2 = (p3.getx() + p4.getx()) / 2;
        double y2 = (p3.gety() + p4.gety()) / 2;
        double x = (y2 - y1 + k1 * x1 - k2 * x2) / (k1 - k2);
        double y = y1 + k1 * (x - x1);

        double t1 = Math.pow(p1.getx() - x, 2) + Math.pow(p1.gety() - y, 2);
        double t2 = Math.pow(p2.getx() - x, 2) + Math.pow(p2.gety() - y, 2);
        double t3 = Math.pow(p3.getx() - x, 2) + Math.pow(p3.gety() - y, 2);
        double t4 = Math.pow(p4.getx() - x, 2) + Math.pow(p4.gety() - y, 2);
        return isZero(t1 - t2) && isZero(t2 - t3) && isZero(t3 - t4);
    }

    /**
     * Checks if the distances between the first pair of points and the second pair of points are equal.
     *
     * @param p1 the first point of the first pair.
     * @param p2 the second point of the first pair.
     * @param p3 the first point of the second pair.
     * @param p4 the second point of the second pair.
     * @return true if the distances are equal, false otherwise.
     */
    static boolean check_eqdistance(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) - sdistance(p3, p4));
    }

    /**
     * Checks if the distance between two points defined by coordinates is equal to the distance between another two points.
     *
     * @param x1 the x-coordinate of the first point.
     * @param y1 the y-coordinate of the first point.
     * @param x2 the x-coordinate of the second point.
     * @param y2 the y-coordinate of the second point.
     * @param x3 the x-coordinate of the third point.
     * @param y3 the y-coordinate of the third point.
     * @param x4 the x-coordinate of the fourth point.
     * @param y4 the y-coordinate of the fourth point.
     * @return true if the computed distances are equal, false otherwise.
     */
    static boolean check_eqdistance(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return isZero(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) - Math.pow(x3 - x4, 2) - Math.pow(y3 - y4, 2));

    }

    /**
     * Checks if the distance between two pairs of points, scaled by given factors, are equal.
     *
     * @param p1 the first point of the first pair.
     * @param p2 the second point of the first pair.
     * @param p3 the first point of the second pair.
     * @param p4 the second point of the second pair.
     * @param t1 the scaling factor for the first pair.
     * @param t2 the scaling factor for the second pair.
     * @return true if the scaled distances are equal, false otherwise.
     */
    static boolean check_eqdistance(CPoint p1, CPoint p2, CPoint p3, CPoint p4, int t1, int t2) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) * t2 - sdistance(p3, p4) * t1);
    }

    /**
     * Checks if the angles formed by three points in two sets are equal.
     *
     * @param p1 the first point of the first angle.
     * @param p2 the vertex of the first angle.
     * @param p3 the third point of the first angle.
     * @param p4 the first point of the second angle.
     * @param p5 the vertex of the second angle.
     * @param p6 the third point of the second angle.
     * @return true if the angles are equal, false otherwise.
     */
    static boolean check_eqangle(CPoint p1, CPoint p2, CPoint p3, CPoint p4, CPoint p5, CPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double t1 = CAngle.getAngleValue(p1, p2, p3);
        double t2 = CAngle.getAngleValue(p4, p5, p6);
        return isZero(t1 - t2);
    }

    /**
     * Checks if the angles defined by four points in two sets are equal.
     *
     * @param p1 the first point of the first angle.
     * @param p2 the second point of the first angle.
     * @param p3 the third point of the first angle.
     * @param p4 the fourth point used with the first angle calculation.
     * @param p5 the first point of the second angle.
     * @param p6 the second point of the second angle.
     * @param p7 the third point of the second angle.
     * @param p8 the fourth point used with the second angle calculation.
     * @return true if the angles are equal, false otherwise.
     */
    static boolean check_eqangle(CPoint p1, CPoint p2, CPoint p3, CPoint p4, CPoint p5, CPoint p6, CPoint p7, CPoint p8) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null || p6 == null || p7 == null || p8 == null) {
            return false;
        }
        double t1 = CAngle.getAngleValue(p1, p2, p3, p4);
        double t2 = CAngle.getAngleValue(p5, p6, p7, p8);
        return isZero(t1 - t2);
    }

    /**
     * Checks if points p3 and p4 lie on the same side of the line defined by p1 and p2.
     *
     * @param p1 first point defining the line
     * @param p2 second point defining the line
     * @param p3 first point to test
     * @param p4 second point to test
     * @return true if p3 and p4 are on the same side of the line; false otherwise
     */
    public static boolean check_same_side(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p4 == null || p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return collv(p1, p2, p3) * collv(p1, p2, p4) > 0;
    }

    /**
     * Computes the cross product of vectors AB and AC.
     *
     * @param A the starting point
     * @param B the end point of the first vector
     * @param C the end point of the second vector
     * @return the cross product value
     */
    public static double collv(CPoint A, CPoint B, CPoint C) {
        double d1 = (B.getx() - A.getx()) * (C.gety() - A.gety()) -
                (B.gety() - A.gety()) * (C.getx() - A.getx());
        return d1;
    }

    /**
     * Determines if point p lies inside the triangle defined by p1, p2, and p3.
     *
     * @param p the point to test
     * @param p1 first vertex of the triangle
     * @param p2 second vertex of the triangle
     * @param p3 third vertex of the triangle
     * @return true if p is inside the triangle; false otherwise
     */
    public static boolean check_triangle_inside(CPoint p, CPoint p1, CPoint p2, CPoint p3) {
        if (p == null || p1 == null || p2 == null || p3 == null) {
            return false;
        }

        double d1 = collv(p, p1, p2);
        double d2 = collv(p, p2, p3);
        double d3 = collv(p, p3, p1);

        return d1 * d2 > 0 && d2 * d3 > 0 && d1 * d3 > 0;

    }

    /**
     * Compares two angles defined by sets of three points.
     *
     * @param p1 first angle's first point
     * @param p2 vertex of the first angle
     * @param p3 first angle's third point
     * @param p4 second angle's first point
     * @param p5 vertex of the second angle
     * @param p6 second angle's third point
     * @return true if the absolute value of the first angle is greater than that of the second; false otherwise
     */
    static boolean check_angle_less(CPoint p1, CPoint p2, CPoint p3, CPoint p4, CPoint p5, CPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        double t1 = CAngle.getAngleValue(p1, p2, p3);
        double t2 = CAngle.getAngleValue(p4, p5, p6);
        return Math.abs(t1) > Math.abs(t2);
    }

    /**
     * Compares the distances between two pairs of points.
     *
     * @param p1 first point of the first pair
     * @param p2 second point of the first pair
     * @param p3 first point of the second pair
     * @param p4 second point of the second pair
     * @return true if the distance between p1 and p2 is less than the distance between p3 and p4; false otherwise
     */
    static boolean check_distance_less(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return false;
        }
        return (sdistance(p1, p2) < sdistance(p3, p4));
    }

    /**
     * Checks if point p1 is equidistant from points p2 and p3.
     *
     * @param p1 the point to test
     * @param p2 first end of the segment
     * @param p3 second end of the segment
     * @return true if p1 is equidistant to p2 and p3; false otherwise
     */
    static boolean check_bisect(CPoint p1, CPoint p2, CPoint p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }
        return isZero(sdistance(p1, p2) - sdistance(p1, p3));
    }

    /**
     * Determines if two triangles are similar by comparing the ratios of their corresponding sides.
     *
     * @param p1 first vertex of the first triangle
     * @param p2 second vertex of the first triangle
     * @param p3 third vertex of the first triangle
     * @param p4 first vertex of the second triangle
     * @param p5 second vertex of the second triangle
     * @param p6 third vertex of the second triangle
     * @return true if the triangles are similar; false otherwise
     */
    static boolean check_simtri(CPoint p1, CPoint p2, CPoint p3, CPoint p4,
                                CPoint p5, CPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null ||
                p6 == null) {
            return false;
        }
        double r1 = sdistance(p1, p2);
        double r2 = sdistance(p1, p3);
        double r3 = sdistance(p2, p3);
        double r4 = sdistance(p4, p5);
        double r5 = sdistance(p4, p6);
        double r6 = sdistance(p5, p6);
        double t1 = r1 / r4;
        double t2 = r2 / r5;
        double t3 = r3 / r6;
        return isZero(t1 - t2) && isZero(t1 - t3) && isZero(t2 - t3);
    }

    /**
     * Determines if two triangles are congruent by comparing the lengths of their corresponding sides.
     *
     * @param p1 first vertex of the first triangle
     * @param p2 second vertex of the first triangle
     * @param p3 third vertex of the first triangle
     * @param p4 first vertex of the second triangle
     * @param p5 second vertex of the second triangle
     * @param p6 third vertex of the second triangle
     * @return true if the triangles are congruent; false otherwise
     */
    static boolean check_congtri(CPoint p1, CPoint p2, CPoint p3, CPoint p4,
                                 CPoint p5, CPoint p6) {
        if (p1 == null || p2 == null || p3 == null || p4 == null || p5 == null ||
                p6 == null) {
            return false;
        }
        double r1 = sdistance(p1, p2);
        double r2 = sdistance(p1, p3);
        double r3 = sdistance(p2, p3);
        double r4 = sdistance(p4, p5);
        double r5 = sdistance(p4, p6);
        double r6 = sdistance(p5, p6);

        return isZero(r1 - r4) && isZero(r2 - r5) && isZero(r3 - r6);
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance between p1 and p2
     */
    static double sdistance(CPoint p1, CPoint p2) {
        return Math.sqrt(Math.pow(p1.getx() - p2.getx(), 2) + Math.pow(p1.gety() - p2.gety(), 2));
    }

    /**
     * Computes an adjusted midpoint on the line defined by (x1, y1) and (x2, y2) based on the direction from the line's midpoint to (x, y).
     *
     * @param x1 the x-coordinate of the first point on the line
     * @param y1 the y-coordinate of the first point on the line
     * @param x2 the x-coordinate of the second point on the line
     * @param y2 the y-coordinate of the second point on the line
     * @param x the x-coordinate of the external reference point
     * @param y the y-coordinate of the external reference point
     * @return a two-element array containing the computed x and y coordinates
     */
    double[] get_pt_dmcr(double x1, double y1, double x2, double y2, double x, double y) {

        double dis = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double xx = (x1 + x2) / 2;
        double yy = (y1 + y2) / 2;
        double rs = Math.sqrt((Math.pow(xx - x, 2) + Math.pow(yy - y, 2)));
        double dx = (x - xx) / rs;
        double dy = (y - yy) / rs;
        double xr = xx + dx * dis / 2;
        double yr = yy + dy * dis / 2;
        double[] r = new double[2];

        if (near(xr, yr, x1, y1) || near(xr, yr, x2, y2)
                || DrawBase.check_Collinear(x1, y1, x2, y2, xr, yr)) {
        } else if (Math.abs(xr - x1) < CMisc.PIXEPS) {
            xr = x1;
            yr = y2;
        } else if (Math.abs(xr - x2) < CMisc.PIXEPS) {
            xr = x2;
            yr = y1;
        } else if (Math.abs(yr - y1) < CMisc.PIXEPS) {
            yr = y1;
            xr = x2;
        } else if (Math.abs(yr - y2) < CMisc.PIXEPS) {
            yr = y2;
            xr = x1;
        }
        r[0] = xr;
        r[1] = yr;
        return r;
    }

    /**
     * Computes the intersection points between a line and a circle.
     *
     * @param ln the line
     * @param cr the circle
     * @return an array containing intersection coordinates; an empty array if there is no intersection
     */
    double[] intersect_lc(CLine ln, Circle cr) {
        double r2 = cr.getRadius();
        r2 *= r2;
        double k = ln.getK();
        CPoint p = ln.getfirstPoint();
        if (p == null) return null;
        CPoint o = cr.o;
        double x2 = p.getx();
        double y2 = p.gety();
        double x3 = o.getx();
        double y3 = o.gety();


        if (Math.abs(k) < CMisc.MAX_SLOPE) {
            double t = y2 - y3 - k * x2;
            double a = k * k + 1;
            double b = 2 * k * t - 2 * x3;
            double c = t * t + x3 * x3 - r2;
            double d = b * b - 4 * a * c;
            if (d < 0) return new double[0];
            d = Math.sqrt(d);
            double t1 = (-b + d) / (2 * a);
            double t2 = (-b - d) / (2 * a);
            double m1 = (t1 - x2) * k + y2;
            double m2 = (t2 - x2) * k + y2;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;

        } else {
            double t1 = x2;
            double dl = r2 - (t1 - x3) * (t1 - x3);
            if (dl < 0) return null;
            double d = Math.sqrt(dl);
            double m1 = y3 + d;
            double t2 = t1;
            double m2 = y3 - d;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;
        }
    }

    /**
     * Computes the intersection point of two lines.
     *
     * @param ln1 the first line
     * @param ln2 the second line
     * @return a two-element array with the x and y coordinates of the intersection, or null if undefined
     */
    public double[] intersect_ll(CLine ln1, CLine ln2) {
        CPoint p1 = ln1.getfirstPoint();
        double k1 = ln1.getK();
        CPoint p2 = ln2.getfirstPoint();
        double k2 = ln2.getK();
        if (p1 == null || p2 == null) return null;
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x, y;
        if (Math.abs(k1) > CMisc.MAX_SLOPE) {
            x = x1;
            y = y2 + k2 * (x - x2);
        } else if (Math.abs(k2) > CMisc.MAX_SLOPE) {
            x = x2;
            y = y1 + k1 * (x - x1);
        } else {
            x = (y2 - y1 + k1 * x1 - k2 * x2) / (k1 - k2);
            y = y1 + k1 * (x - x1);
        }
        double[] r = new double[2];
        r[0] = x;
        r[1] = y;
        return r;
    }

    /**
     * Computes the intersection points of two circles.
     *
     * @param c1 the first circle
     * @param c2 the second circle
     * @return an array containing the intersection coordinates; null if there is no intersection
     */
    public double[] intersect_cc(Circle c1, Circle c2) {
        double r1 = c1.getRadius();
        CPoint o1 = c1.o;
        double r2 = c2.getRadius();
        CPoint o2 = c2.o;

        double x1 = o1.getx();
        double y1 = o1.gety();
        double x2 = o2.getx() - x1;
        double y2 = o2.gety() - y1;
        double a = 2 * x2;
        double b = 2 * y2;
        double c = -x2 * x2 - y2 * y2 - r1 * r1 + r2 * r2;
        double ma = a * a + b * b;
        double d = 4 * a * a * (r1 * r1 * (ma) - c * c);
        if (d < 0) return null;
        d = Math.sqrt(d);
        if (a != 0) {
            double yt1 = (-2 * b * c + d) / (2 * (ma));
            double yt2 = (-2 * b * c - d) / (2 * (ma));
            double xt1 = -(b * yt1 + c) / a;
            double xt2 = -(b * yt2 + c) / a;
            double[] r = new double[4];
            r[0] = xt1 + x1;
            r[1] = yt1 + y1;
            r[2] = xt2 + x1;
            r[3] = yt2 + y1;
            return r;
        } else {
            double yt1, yt2;
            yt1 = yt2 = -c / b;
            double xt1 = Math.sqrt(r1 * r1 - yt1 * yt1);
            double xt2 = -xt1;
            double[] r = new double[4];
            r[0] = xt1 + x1;
            r[1] = yt1 + y1;
            r[2] = xt2 + x1;
            r[3] = yt2 + y1;
            return r;

        }

    }

    /**
     * Checks if two circles intersect based on their radii and center distance.
     *
     * @param c1 the first circle
     * @param c2 the second circle
     * @return true if the circles intersect within the defined tolerance
     */
    protected boolean check_cc_inter(Circle c1, Circle c2) {
        double r1 = c1.getRadius();
        double r2 = c2.getRadius();
        double r = Math.sqrt(Math.pow(c1.getCenterOX() - c2.getCenterOX(), 2) +
                Math.pow(c1.getCenterOY() - c2.getCenterOY(), 2));
        double rx = r - r1 - r2;
        double rx1 = r1 - r - r2;
        double rx2 = r2 - r - r1;

        return rx < 0.1 && rx1 < 0.1 && rx2 < 0.1;

    }

    /**
     * Checks if a line and a circle intersect.
     *
     * @param ln the line
     * @param c2 the circle
     * @return true if the line intersects the circle
     */
    protected boolean check_lc_inter(CLine ln, Circle c2) {
        double r1 = ln.distance(c2.getCenterOX(), c2.getCenterOY());
        double r2 = c2.getRadius();
        return (r2 - r1) > 0;
    }

    /**
     * Determines if a given value is effectively zero within a tolerance.
     *
     * @param r the value to check
     * @return true if the value is considered zero
     */
    protected static boolean isZero(double r) {
        return Math.abs(r) < CMisc.ZERO;
    }

    /**
     * Checks whether two coordinates are nearly equal based on a tolerance.
     *
     * @param x the first x-coordinate
     * @param y the first y-coordinate
     * @param x1 the second x-coordinate
     * @param y1 the second y-coordinate
     * @return true if the points are near each other
     */
    protected static boolean near(double x, double y, double x1, double y1) {
        return Math.abs(Math.pow(x - x1, 2) + Math.pow(y - y1, 2)) < CMisc.PIXEPS * CMisc.PIXEPS;
    }

    /**
     * Retrieves an array of points extracted from the given construct.
     *
     * @param c the construct containing point identifiers
     * @return an array of points, or null if a point cannot be found
     */
    protected CPoint[] getPoints(Cons c) {

        CPoint[] pp = new CPoint[8];
        int i = 0;
        while (true) {
            Object p1 = c.getPTN(i);
            if (p1 == null)
                break;

            pp[i] = findPoint(p1.toString());
            if (pp[i] == null) {
                JOptionPane.showMessageDialog(null, "Can not find point " + p1 + "\nPlease construct the diagram", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            i++;
        }
        return pp;
    }

    /**
     * Builds a TMono object that represents a geometric relation based on the construct.
     *
     * @param c the construct defining the relation
     * @return the constructed TMono object, or null if it cannot be built
     */
    protected TMono getTMono(Cons c) {
        if (c == null) return null;

        CPoint[] pp = getPoints(c);
        if (pp == null) return null;
        TMono m = null;

        switch (c.type) {
            case Gib.CO_COLL:
                m = poly.collinear(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex);
                break;
            case Gib.CO_PARA:
                m = poly.parallel(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case Gib.CO_PERP:
                m = poly.perpendicular(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case Gib.CO_MIDP:
                m = poly.midpoint(pp[1].x1.xindex, pp[0].x1.xindex, pp[2].x1.xindex);
                break;
            case Gib.CO_CYCLIC:
                m = poly.cyclic(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case Gib.CO_CONG:
                m = poly.eqdistance(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex);
                break;
            case Gib.CO_ACONG: {
                if (pp[6] != null && pp[7] != null)
                    m = poly.eqangle(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex, pp[3].x1.xindex, pp[3].y1.xindex,
                            pp[4].x1.xindex, pp[4].y1.xindex, pp[5].x1.xindex, pp[5].y1.xindex, pp[6].x1.xindex, pp[6].y1.xindex, pp[7].x1.xindex, pp[7].y1.xindex);
                else
                    m = poly.eqangle(pp[0].x1.xindex, pp[0].y1.xindex, pp[1].x1.xindex, pp[1].y1.xindex, pp[2].x1.xindex, pp[2].y1.xindex,
                            pp[3].x1.xindex, pp[3].y1.xindex, pp[4].x1.xindex, pp[4].y1.xindex, pp[5].x1.xindex, pp[5].y1.xindex);
            }
            break;
            case Gib.CO_PBISECT:
                break;
            case Gib.CO_STRI:
                break;
            case Gib.CO_CTRI:
                break;
        }

        if (m == null) return m;
        TMono m1 = m;
        while (m1.coef != null)
            m1 = m1.coef;
        if (m1.value() < 0)
            m = poly.cp_times(-1, m);
        return m;
    }

    /**
     * Determines whether two points are identical by comparing their Wu representations.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return true if both points are considered identical
     */
    public boolean decide_wu_identical(CPoint p1, CPoint p2) {

        TMono m1 = poly.ppdd(p1.x1.xindex, p2.x1.xindex); //poly.pdif(poly.pcopy(p1.x1.m), poly.pcopy(p2.x1.m));
        TMono m2 = poly.ppdd(p1.y1.xindex, p2.y1.xindex); //poly.pdif(poly.pcopy(p1.y1.m), poly.pcopy(p2.y1.m));
        return div_set(m1) && div_set(m2);

    }

    /**
     * Checks if the given TMono reduces to zero relative to the polynomial set.
     *
     * @param m1 the TMono expression to check
     * @return true if the expression is effectively zero within the polynomial context
     */
    public boolean div_set(TMono m1) {
        if (m1 == null)
            return true;

        TPoly p1 = polylist;

        if (poly.pzerop(m1))
            return true;
        while (p1 != null) {
            TMono t = p1.poly;
            if (t.x == m1.x)
                break;
            p1 = p1.next;
        }

        while (true) {
            TMono m = p1.poly;
            TMono md = poly.pcopy(m);
            m1 = poly.prem(m1, md);
            if (poly.pzerop(m1))
                return true;
            TPoly p2 = polylist;
            if (p1 == p2)
                break;

            while (p2 != null) {
                if (p2.next == p1)
                    break;
                p2 = p2.next;
            }
            p1 = p2;
        }

        CMisc.print("======================");
        poly.printpoly(m1);
        this.printPoly(polylist);
        return false;
    }

    /**
     * Prints the polynomial represented by the TPoly chain.
     *
     * @param p the TPoly instance containing the polynomial
     */
    public void printPoly(TPoly p) {
        while (p != null) {
            poly.printpoly(p.getPoly());
            p = p.getNext();
        }
    }

    /**
     * Searches for a CTMark based on two pairs of points.
     *
     * @param p1 the first point of the first pair
     * @param p2 the second point of the first pair
     * @param p3 the first point of the second pair
     * @param p4 the second point of the second pair
     * @return the CTMark if found, otherwise null
     */
    public CTMark findCTMark(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        for (int i = 0; i < otherlist.size(); i++) {
            CClass c = (CClass) otherlist.get(i);
            if (c.get_type() == CClass.TMARK) {
                CTMark m = (CTMark) c;
                if (m.ln1.containPTs(p1, p2) && m.ln2.containPTs(p3, p4))
                    return m;
                if (m.ln2.containPTs(p1, p2) && m.ln1.containPTs(p3, p4))
                    return m;

            }
        }
        return null;
    }

    /**
     * Determines if a tmark exists that contains the specified four points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return true if a matching tmark is found
     */
    public boolean find_tmark(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            if (f instanceof JTLineFlash) {
                JTLineFlash t = (JTLineFlash) f;
                if (t.containPt(p1) && t.containPt(p2) && t.containPt(p3) && t.containPt(p4))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is at least one freezed point in the diagram.
     *
     * @return true if any point is freezed, otherwise false
     */
    public boolean containFreezedPoint() {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (p.isFreezed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Unfreezes all points in the diagram.
     */
    public void unfreezeAllPoints() {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (p.isFreezed()) {
                p.setFreezed(false);
            }
        }
    }

    /**
     * Determines if the diagram is in a frozen state.
     *
     * @return true if any point is freezed, indicating the diagram is frozen
     */
    public boolean isFrozen() {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (p.isFreezed()) {
                gxInstance.setTextLabel2(GExpert.getLanguage("The diagram is frozen, use right click menu to unfreeze!"));
                return true;
            }
        }
        return false;
    }

    /**
     * Zooms out the diagram from a specified center by adjusting the points.
     *
     * @param x the x-coordinate of the zoom center
     * @param y the y-coordinate of the zoom center
     * @param zz the zoom factor denominator
     */
    public void zoom_out(double x, double y, int zz) {

        if (isFrozen())
            return;

        double r = CMisc.ZOOM_RATIO;
        r = 1 + (r - 1) / zz;

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.setXY(p.getx() * 1.0 / r + (1.0 - 1.0 / r) * x, p.gety() * 1.0 / r + (1.0 - 1.0 / r) * y);
        }
    }

    /**
     * Zooms in the diagram from a specified center by adjusting the points.
     *
     * @param x the x-coordinate of the zoom center
     * @param y the y-coordinate of the zoom center
     * @param zz the zoom factor denominator
     */
    public void zoom_in(double x, double y, int zz) {
        if (isFrozen())
            return;

        double r = CMisc.ZOOM_RATIO;
        r = 1 + (r - 1) / zz;
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.setXY(p.getx() * r + (1.0 - r) * x, p.gety() * r + (1.0 - r) * y);
        }
    }

    /**
     * Adjusts the catch point type by examining proximity to other points.
     */
    public void hvCatchPoint() {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            if (DrawBase.sdistance(CatchPoint, pt) > CMisc.PIXEPS_PT) {
                if (Math.abs(pt.getx() - CatchPoint.getx()) < CMisc.PIXEPS_PT / 2) {
                    if (CatchType == 3) {
                        CatchType = 4;
                        return;
                    } else CatchType = 2;
                } else if (Math.abs(pt.gety() - CatchPoint.gety()) < CMisc.PIXEPS_PT / 2) {
                    if (CatchType == 2) {
                        CatchType = 4;
                        return;
                    } else
                        CatchType = 3;
                    return;
                }
            }
        }
    }

    /**
     * Retrieves a horizontal or vertical catch point based on the catch type.
     *
     * @param CatchType the catch type indicator (2 for vertical, 3 for horizontal)
     * @return the catch point if found, otherwise null
     */
    public CPoint getCatchHVPoint(int CatchType) {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            if (DrawBase.sdistance(CatchPoint, pt) > CMisc.PIXEPS_PT) {
                if (CatchType == 2 && Math.abs(pt.getx() - CatchPoint.getx()) < CMisc.PIXEPS_PT / 2) {
                    return pt;
                } else if (CatchType == 3 && Math.abs(pt.gety() - CatchPoint.gety()) < CMisc.PIXEPS_PT / 2) {
                    return pt;
                }
            }
        }
        return null;
    }

    /**
     * Adjusts the provided point to align with a nearby point based on the catch type.
     *
     * @param pv the point to be adjusted
     */
    public void setCatchHVPoint(CPoint pv) {
        if (CatchType != 2 && CatchType != 3 && CatchType != 4)
            return;

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint pt = (CPoint) pointlist.get(i);
            if (DrawBase.sdistance(pv, pt) > CMisc.PIXEPS_PT) {
                if ((CatchType == 2 || CatchType == 4) && Math.abs(pt.getx() - pv.getx()) < CMisc.PIXEPS_PT / 2) {
                    pv.setXY(pt.getx(), pv.gety());
                    return;
                }
                if ((CatchType == 3 || CatchType == 4) && Math.abs(pt.gety() - pv.gety()) < CMisc.PIXEPS_PT / 2) {
                    pv.setXY(pv.getx(), pt.gety());
                    return;
                }
            }
        }
    }
}
