package wprover;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * Represents a distance measurement between two points in a geometric context.
 * This class extends CClass and provides methods for drawing, saving, and loading distance measurements.
 */
public class CDistance extends CClass {

    CPoint pstart;
    CPoint pend;
    double len = 18;

    /**
     * Default constructor for CDistance.
     */
    public CDistance() {
        super(CClass.DISTANCE);
    }

    /**
     * Constructs a CDistance object with the specified start and end points.
     *
     * @param p1 the start point
     * @param p2 the end point
     */
    public CDistance(CPoint p1, CPoint p2) {
        super(CClass.DISTANCE);
        m_color = 6;
        pstart = p1;
        pend = p2;
        m_name = "|" + p1.getname() + p2.getname() + "|";
        if (pstart == null || pend == null)
            CMisc.print("Two ends of the Distance cannot be null");
    }

    /**
     * Gets the type string of the distance.
     *
     * @return the type string of the distance
     */
    public String TypeString() {
        String st = Language.getLs("Measure Distance");
        if (st == null)
            st = "distance";

        if (m_name == null) return st;
        return st + " " + m_name;
    }

    /**
     * Gets the description of the distance.
     *
     * @return the description of the distance
     */
    public String getDescription() {
        String st = Language.getLs("Measure Distance");
        if (st == null)
            st = "distance";

        return st + " " + pstart.m_name + " " + pend.m_name;
    }

    /**
     * Drags the distance to a new position.
     *
     * @param xx the x-coordinate of the new position
     * @param yy the y-coordinate of the new position
     */
    public void drag(double xx, double yy) {
        double x1, y1, x2, y2, x3, y3;
        x1 = pstart.getx();
        y1 = pstart.gety();
        x2 = pend.getx();
        y2 = pend.gety();
        x3 = xx;
        y3 = yy;

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double m = x3 - x1;
        double n = y3 - y1;
        len = m * dy - n * dx;
    }

    /**
     * Selects the distance if the given coordinates are within its range.
     *
     * @param xx the x-coordinate
     * @param yy the y-coordinate
     * @return true if the distance is selected, false otherwise
     */
    public boolean select(double xx, double yy) {
        double x1, y1, x2, y2, x3, y3;
        x1 = pstart.getx();
        y1 = pstart.gety();
        x2 = pend.getx();
        y2 = pend.gety();
        x3 = xx;
        y3 = yy;

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return false;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double m = x3 - x1;
        double n = y3 - y1;
        double length = m * dy - n * dx;
        if (Math.abs(length - len) < CMisc.PIXEPS)
            return true;
        return false;
    }

    /**
     * Draws the distance using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
        draw(g2, false);
    }

    /**
     * Draws the distance with the option to highlight if selected.
     *
     * @param g2 the Graphics2D object
     * @param isSelected true if the distance is selected, false otherwise
     */
    public void draw(Graphics2D g2, boolean isSelected) {
        if (!isdraw()) return;

        double x1, y1, x2, y2;
        x1 = pstart.getx();
        y1 = pstart.gety();
        x2 = pend.getx();
        y2 = pend.gety();

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double xx1 = x1 + dy * len;
        double yy1 = y1 - dx * len;
        double xx2 = x2 + dy * len;
        double yy2 = y2 - dx * len;
        double sin = Math.sin(Math.PI / 6);
        double cos = Math.cos(Math.PI / 6);
        double ddx = dx * 12;
        double ddy = dy * 12;
        double px1 = xx1 + ddx * cos - ddy * sin;
        double py1 = yy1 + ddx * sin + ddy * cos;
        double px2 = xx1 + ddx * cos + ddy * sin;
        double py2 = yy1 - ddx * sin + ddy * cos;

        ddx = -ddx;
        ddy = -ddy;
        double qx1 = xx2 + ddx * cos - ddy * sin;
        double qy1 = yy2 + ddx * sin + ddy * cos;
        double qx2 = xx2 + ddx * cos + ddy * sin;
        double qy2 = yy2 - ddx * sin + ddy * cos;

        if (!isSelected) {
            //g2.setStroke(new BasicStroke(1));
            this.setDraw(g2);
            //g2.setColor(Color.cyan);
        } else {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.pink);
        }
        g2.drawLine((int) x1, (int) y1, (int) (xx1 + 8 * dy), (int) (yy1 - 8 * dx));
        g2.drawLine((int) x2, (int) y2, (int) (xx2 + 8 * dy), (int) (yy2 - 8 * dx));
        g2.drawLine((int) xx1, (int) yy1, (int) px1, (int) py1);
        g2.drawLine((int) xx1, (int) yy1, (int) px2, (int) py2);
        g2.drawLine((int) xx2, (int) yy2, (int) qx1, (int) qy1);
        g2.drawLine((int) xx2, (int) yy2, (int) qx2, (int) qy2);

        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);
        g2.setColor(Color.black);
        g2.drawString("" + pstart.m_name + pend.m_name + " = " + ((float) ((int) (dis * 100))) / 100, (int) ((xx1 + xx2) / 2), (int) ((yy1 + yy2) / 2));
    }

    /**
     * Saves the distance to a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the show type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!isdraw()) return;
        double x1, y1, x2, y2;
        x1 = pstart.getx();
        y1 = pstart.gety();
        x2 = pend.getx();
        y2 = pend.gety();

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double xx1 = x1 + dy * len;
        double yy1 = y1 - dx * len;
        double xx2 = x2 + dy * len;
        double yy2 = y2 - dx * len;
        double sin = Math.sin(Math.PI / 6);
        double cos = Math.cos(Math.PI / 6);
        double ddx = dx * 12;
        double ddy = dy * 12;
        double px1 = xx1 + ddx * cos - ddy * sin;
        double py1 = yy1 + ddx * sin + ddy * cos;
        double px2 = xx1 + ddx * cos + ddy * sin;
        double py2 = yy1 - ddx * sin + ddy * cos;

        ddx = -ddx;
        ddy = -ddy;
        double qx1 = xx2 + ddx * cos - ddy * sin;
        double qy1 = yy2 + ddx * sin + ddy * cos;
        double qx2 = xx2 + ddx * cos + ddy * sin;
        double qy2 = yy2 - ddx * sin + ddy * cos;

        String s = "";
        s += this.getPSLineString((int) x1, -(int) y1, (int) (xx1 + 8 * dy), -(int) (yy1 - 8 * dx));
        s += this.getPSLineString((int) x2, -(int) y2, (int) (xx2 + 8 * dy), -(int) (yy2 - 8 * dx));
        s += this.getPSLineString((int) xx1, -(int) yy1, (int) px1, -(int) py1);
        s += "\n";

        s += this.getPSLineString((int) xx1, -(int) yy1, (int) px2, -(int) py2);
        s += this.getPSLineString((int) xx2, -(int) yy2, (int) qx1, -(int) qy1);
        s += this.getPSLineString((int) xx2, -(int) yy2, (int) qx2, -(int) qy2);
        s += "\n";
        s += this.getPSLineString((int) xx1, -(int) yy1, (int) xx2, -(int) yy2);
        fp.write(s.getBytes());
        super.saveSuper(fp);

        String sv = pstart.m_name + pend.m_name + " = " + ((float) ((int) (dis * 100))) / 100;
        s = "black";
        s += " mf " + (int) ((xx1 + xx2) / 2) + " " + (-(int) ((yy1 + yy2) / 2) - 15) + " moveto (" + sv + ") " + "show\n";
        fp.write(s.getBytes());
    }

    /**
     * Saves the distance data to an output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(pstart.m_id);
        out.writeInt(pend.m_id);
        out.writeDouble(len);
    }

    /**
     * Loads the distance data from an input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        if (CMisc.version_load_now < 0.01) {
            m_id = in.readInt();
            pstart = dp.getPointById(in.readInt());
            pend = dp.getPointById(in.readInt());
        } else {
            pstart = dp.getPointById(in.readInt());
            pend = dp.getPointById(in.readInt());
            if (CMisc.version_load_now >= 0.020)
                len = in.readDouble();
        }
    }
}
