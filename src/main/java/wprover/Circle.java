package wprover;

import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.awt.*;

/**
 * Represents a geometric circle with various properties and methods.
 */
public class Circle extends CClass {
    public static int PCircle = 0;
    public static int RCircle = 1;
    public static int SCircle = 2;

    int type = PCircle;

    CPoint o = new CPoint();
    Vector points = new Vector();
    Vector cons = new Vector();

    /**
     * Gets the number of points on the circle.
     *
     * @return the number of points on the circle
     */
    public int psize() {
        return points.size();
    }

    /**
     * Gets the point at the specified index.
     *
     * @param i the index of the point
     * @return the point at the specified index
     */
    public CPoint getP(int i) {
        return (CPoint) points.get(i);
    }

    /**
     * Checks if the given point is on the circle.
     *
     * @param p the point to check
     * @return true if the point is on the circle, false otherwise
     */
    public boolean p_on_circle(CPoint p) {
        for (int i = 0; i < points.size(); i++)
            if (p == points.get(i))
                return true;
        return false;
    }

    /**
     * Draws the circle using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     * @param selected true if the circle is selected, false otherwise
     */
    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;
        if (selected) {
            g2.setColor(CMisc.SelectObjectColor);
            g2.setStroke(CMisc.SelectObjectStroke);
        } else
            super.setDraw(g2);

        double x1, y1, r;
        x1 = o.x1.value;
        y1 = o.y1.value;
        r = getRadius();
        if (r < CMisc.MAX_DRAW_LEN)
            g2.drawOval((int) (x1 - r), (int) (y1 - r), 2 * (int) r, 2 * (int) r);
        else {
            if (points.size() < 2) return;
            CPoint p1, p2;
            p1 = p2 = null;
            double len = 0.00;
            for (int i = 0; i < points.size(); i++) {
                CPoint tp1 = (CPoint) points.get(i);
                for (int j = 1; j < points.size(); j++) {

                    CPoint tp2 = (CPoint) points.get(j);
                    if (tp1 == tp2) continue;
                    double tlen = Math.pow(tp1.getx() - tp2.getx(), 2) + Math.pow(tp1.gety() - tp2.gety(), 2);
                    if (tlen > len) {
                        len = tlen;
                        p1 = tp1;
                        p2 = tp2;
                    }
                }
            }
            if (p1 == null || p2 == null) return;
            double dx = p2.getx() - p1.getx();
            double dy = p2.gety() - p1.gety();
            double sl = Math.sqrt(dx * dx + dy * dy);
            x1 = p1.getx() - dx * 2000 / sl;
            y1 = p1.gety() - dy * 2000 / sl;
            double x2 = p1.getx() + dx * 2000 / sl;
            double y2 = p1.gety() + dy * 2000 / sl;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    /**
     * Draws the circle using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
        this.draw(g2, false);
    }

    /**
     * Gets the type string of the circle.
     *
     * @return the type string of the circle
     */
    public String TypeString() {
        if (m_name == null) return GExpert.getLanguage("Circle");
        return GExpert.getTranslationViaGettext("Circle {0}" , m_name);
    }

    /**
     * Gets the description of the circle.
     *
     * @return the description of the circle
     */
    public String getDescription() {
        if (type == PCircle)
            return GExpert.getTranslationViaGettext("Circle {0}", "(" + o.m_name + "," + o.m_name + this.getSidePoint().getname() + ")");
        else if (type == SCircle) {
            CPoint p1, p2, p3;
            p1 = p2 = p3 = null;
            if (points.size() < 3) return GExpert.getLanguage("Circle");

            p1 = (CPoint) points.get(0);
            p2 = (CPoint) points.get(1);
            p3 = (CPoint) points.get(2);
            return GExpert.getTranslationViaGettext("Circle {0}", "(" + o.getname() + "," + p1.getname() + p2.getname() + p3.getname() + ")");
        } else if (type == RCircle) {
            Constraint cs = (Constraint) cons.get(0);
            if (cs.GetConstraintType() == Constraint.RCIRCLE) {
                CClass p1 = (CClass) cs.getelement(0);
                CClass p2 = (CClass) cs.getelement(1);
                return GExpert.getTranslationViaGettext("Circle {0}","(" + o.m_name + "," + p1.getname() + p2.getname() + ")");
            }
        }
        return this.TypeString();
    }

    /**
     * Selects the circle if the given coordinates are within its range.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the circle is selected, false otherwise
     */
    public boolean select(double x, double y) {
        if (!visible) return false;
        double ox, oy;
        ox = o.getx();
        oy = o.gety();

        double r = this.getRadius();
        double len = Math.sqrt(Math.pow(x - ox, 2) + Math.pow(y - oy, 2));
        if (Math.abs(r - len) < CMisc.PIXEPS)
            return true;
        return false;
    }

    /**
     * Sets the type of the circle.
     *
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the names of all points on the circle.
     *
     * @return a string containing the names of all points on the circle
     */
    public String getAllPointName() {
        String s = new String();
        for (int i = 0; i < points.size(); i++) {
            CPoint p = (CPoint) points.get(i);
            if (i == 0)
                s += p.m_name;
            else
                s += ", " + p.m_name;
        }
        return s;
    }

    /**
     * Gets the point on the circle with the least index.
     *
     * @return the point on the circle with the least index
     */
    // point that with least number
    public CPoint getSidePoint() {
        CPoint pt = null;

        for (int i = 0; i < points.size(); i++) {
            CPoint p = (CPoint) points.get(i);
            if (p == null)
                continue;
            if (pt == null)
                pt = p;
            else if (pt.x1.xindex > p.x1.xindex)
                pt = p;
        }
        return pt;
    }

    /**
     * Gets the x-coordinate of the center of the circle.
     *
     * @return the x-coordinate of the center
     */
    public double getCenterOX() {
        return o.getx();
    }

    /**
     * Gets the y-coordinate of the center of the circle.
     *
     * @return the y-coordinate of the center
     */
    public double getCenterOY() {
        return o.gety();
    }

    /**
     * Gets the radius of the circle.
     *
     * @return the radius of the circle
     */
    public double getRadius() {
        if (type == RCircle) {
            //constraint cs = null;
            for (int i = 0; i < cons.size(); i++) {
                Constraint c = (Constraint) cons.get(i);
                if (c.GetConstraintType() == Constraint.RCIRCLE) {
                    CPoint p1 = (CPoint) c.getelement(0);
                    CPoint p2 = (CPoint) c.getelement(1);
                    return Math.sqrt(Math.pow(p1.getx() - p2.getx(), 2) + Math.pow(p1.gety() - p2.gety(), 2));
                }
            }
            return -1;
        } else {
            CPoint p = getSidePoint();
            return Math.sqrt(Math.pow(p.getx() - o.getx(), 2) + Math.pow(p.gety() - o.gety(), 2));
        }
    }

    /**
     * Gets the points that define the radius of the circle.
     *
     * @return an array containing the points that define the radius
     */
    public CPoint[] getRadiusPoint() {
        CPoint[] pl = new CPoint[2];
        if (type == Circle.RCircle) {
            for (int i = 0; i < cons.size(); i++) {
                Constraint cs = (Constraint) cons.get(i);
                if (cs.GetConstraintType() == Constraint.RCIRCLE) {
                    pl[0] = (CPoint) cs.getelement(0);
                    pl[1] = (CPoint) cs.getelement(1);
                    return pl;
                }
            }



        } else if (type == Circle.PCircle | type == Circle.SCircle) {
            pl[0] = o;
            pl[1] = this.getSidePoint();
        }
        return pl;

    }


    /**
     * This class represents a circle in geometric constructions.
     */
    public Circle() {
        super(CClass.CIRCLE);
    }

    /** Create a circle using the four points O, A, B, and C.
     * @param O Center of the circle
     * @param A Point on the circle line
     * @param B Point on the circle line
     * @param C Point on the circle line
     */
    public Circle(CPoint O, CPoint A, CPoint B, CPoint C) {
        super(CClass.CIRCLE);
        type = SCircle;
        this.o = O;
        points.add(A);
        points.add(B);
        points.add(C);
    }

/**
     * Constructs a Circle object with the specified center and two points on the circle.
     *
     * @param O the center of the circle
     * @param A the first point on the circle
     * @param B the second point on the circle
     */
    public Circle(CPoint O, CPoint A, CPoint B) {
        super(CClass.CIRCLE);
        this.o = O;
        points.add(A);
        points.add(B);
    }

    /**
     * Constructs a Circle object with the specified center and one point on the circle.
     *
     * @param O the center of the circle
     * @param A the point on the circle
     */
    public Circle(CPoint O, CPoint A) {
        super(CClass.CIRCLE);
        this.o = O;
        points.add(A);
    }

    /**
     * Constructs a Circle object with the specified type and center.
     *
     * @param type the type of the circle
     * @param O the center of the circle
     */
    public Circle(int type, CPoint O) {
        super(CClass.CIRCLE);
        this.o = O;
        this.type = type;
    }

    /**
     * Adds a constraint to the circle.
     *
     * @param cs the constraint to add
     */
    public void addConstraint(Constraint cs) {
        cons.add(cs);
    }

    /**
     * Adds a point to the circle.
     *
     * @param p the point to add
     */
    public void addPoint(CPoint p) {
        if (!points.contains(p))
            points.add(p);
    }

    /**
     * Adjusts the coordinates of the given point to lie on the circle.
     *
     * @param p the point to adjust
     */
    public void pointStickToCircle(CPoint p) {
        double x = p.getx();
        double y = p.gety();

        double xo = o.x1.value;
        double yo = o.y1.value;
        double R = this.getRadius();
        double R1 = Math.sqrt((xo - x) * (xo - x) + (yo - y) * (yo - y));

        double y1 = yo + (y - yo) * R / R1;
        double x1 = xo + (x - xo) * R / R1;
        p.setXY(x1, y1);
    }

    /**
     * Checks if the given coordinates are on the circle.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the coordinates are on the circle, false otherwise
     */
    public boolean on_circle(double x, double y) {
        return this.select(x, y);
    }

    /**
     * Checks if the given coordinates are near the circle within a specified tolerance.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param eps the tolerance
     * @return true if the coordinates are near the circle, false otherwise
     */
    public boolean nearcircle(double x, double y, double eps) {
        double ox, oy;
        ox = o.getx();
        oy = o.gety();

        double r = getRadius();
        double len = Math.sqrt(Math.pow(x - ox, 2) + Math.pow(y - oy, 2));
        if (Math.abs(r - len) < eps)
            return true;
        return false;
    }

    /**
     * Adjusts the coordinates of the given point to lie on the circle using a smart algorithm.
     *
     * @param p the point to adjust
     */
    public void SmartPonc(CPoint p) {
        double ox, oy, x, y;
        ox = o.getx();
        oy = o.gety();
        x = p.getx();
        y = p.gety();

        double r = this.getRadius();
        double len = Math.sqrt(Math.pow(p.getx() - ox, 2) + Math.pow(p.gety() - oy, 2));

        if (Math.abs(x - o.getx()) < 0.001) {
            if (y > oy)
                p.setXY(ox, oy + r);
            else
                p.setXY(ox, oy - r);
        } else {
            double k = r / len;
            p.setXY(ox + k * (x - ox), oy + k * (y - oy));
        }
    }

    /**
     * Finds the common points between two circles.
     *
     * @param c1 the first circle
     * @param c2 the second circle
     * @return a vector of common points
     */
    public static Vector CommonPoints(Circle c1, Circle c2) {
        Vector vlist = new Vector();
        for (int i = 0; i < c1.points.size(); i++) {
            Object obj = c1.points.get(i);
            if (c2.points.contains(obj)) {
                if (!vlist.contains(obj))
                    vlist.add(obj);
            }
        }
        return vlist;
    }

    /**
     * Checks if the given object is tangent to the circle.
     *
     * @param obj the object to check
     * @return true if the object is tangent to the circle, false otherwise
     */
    public boolean Tangent(Object obj) {
        if (obj instanceof CLine) {
            return true;
        } else if (obj instanceof Circle) {
            Circle c2 = (Circle) obj;
            CPoint p1 = this.getSidePoint();
            CPoint p2 = c2.getSidePoint();
            double r1 = Math.sqrt(Math.pow(this.o.getx() - p1.getx(), 2) + Math.pow(this.o.gety() - p1.gety(), 2));
            double r2 = Math.sqrt(Math.pow(c2.o.getx() - p2.getx(), 2) + Math.pow(c2.o.gety() - p2.gety(), 2));
            double d = Math.sqrt(Math.pow(this.o.getx() - c2.o.getx(), 2) + Math.pow(this.o.gety() - c2.o.gety(), 2));
            if (Math.abs(r1 + r2 - d) < CMisc.PIXEPS)
                return true;
            else
                return false;
        } else
            return false;
    }

    /**
     * Saves the circle to a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the style type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!visible) return;

        float r = (float) (((int) (getRadius() * 100)) / 100.0);
        String s = "newpath " + o.m_name + " " +
                r + " circle";
        fp.write(s.getBytes());
        this.saveSuper(fp);
    }

    /**
     * Saves the circle to a data output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);

        out.writeInt(type);
        out.writeInt(o.m_id);
        out.writeInt(points.size());
        for (int i = 0; i < points.size(); i++) {
            CPoint p = (CPoint) points.get(i);
            out.writeInt(p.m_id);
        }
        out.writeInt(cons.size());
        for (int i = 0; i < cons.size(); i++) {
            Constraint cs = (Constraint) cons.get(i);
            out.writeInt(cs.id);
        }
    }

    /**
     * Loads the circle from a data input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        if (CMisc.version_load_now < 0.010) {
            m_id = in.readInt();

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

            type = in.readInt();
            int size = in.readInt();
            m_name = new String();
            for (int i = 0; i < size; i++)
                m_name += in.readChar();
            int d = in.readInt();
            o = dp.getPointById(d);

            size = in.readInt();
            for (int i = 0; i < size; i++) {
                int dx = in.readInt();
                points.add(dp.getPointById(dx));
            }
            size = in.readInt();
            for (int i = 0; i < size; i++) {
                int dx = in.readInt();
                cons.add(dp.getConstraintByid(dx));
            }
        } else {
            super.Load(in, dp);

            type = in.readInt();

            int d = in.readInt();
            o = dp.getPointById(d);

            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int dx = in.readInt();
                points.add(dp.getPointById(dx));
            }
            size = in.readInt();
            for (int i = 0; i < size; i++) {
                int dx = in.readInt();
                cons.add(dp.getConstraintByid(dx));
            }
        }
    }


}
