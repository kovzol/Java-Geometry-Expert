package wprover;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2009-12-13
 * Time: 13:43:52
 * To change this template use File | Settings | File Templates.
 */
public class CArrow extends CClass {
    public static int ANGLE = 30;
    public static int LENGTH = 12;

    CPoint st, ed;
    int angle = ANGLE;
    int length = LENGTH;

    /**
     * Constructs a CArrow object.
     *
     * @param p1 the starting point of the arrow
     * @param p2 the ending point of the arrow
     */
    public CArrow(CPoint p1, CPoint p2) {
        super(CClass.ARROW);
        st = p1;
        ed = p2;
    }

    /**
     * Gets the type string of the arrow.
     *
     * @return the type string
     */
    public String TypeString() {
        return "Arrow" + m_id;
    }

    /**
     * Gets the description of the arrow.
     *
     * @return the description string
     */
    public String getDescription() {
        return "Arrow(" + st + ed + ")";
    }

    /**
     * Draws the arrow using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
        draw(g2, false);
    }

    /**
     * Draws the arrow with the option to highlight if selected.
     *
     * @param g2 the Graphics2D object
     * @param selected true if the arrow is selected, false otherwise
     */
    void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        double x1, y1, x2, y2;
        x1 = st.getx();
        y1 = st.gety();
        x2 = ed.getx();
        y2 = ed.gety();

        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;
        double xx1 = x1;
        double yy1 = y1;
        double xx2 = x2;
        double yy2 = y2;
        double sin = Math.sin(angle * Math.PI / 180);
        double cos = Math.cos(angle * Math.PI / 180);
        double ddx = dx * length;
        double ddy = dy * length;
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


        if (!selected) {
            //g2.setStroke(new BasicStroke(1));
            this.setDraw(g2);
            //g2.setColor(Color.cyan);
        } else {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.pink);
        }
        //   g2.drawLine((int) x1, (int) y1, (int) (xx1 + 8 * dy), (int) (yy1 - 8 * dx));
        //   g2.drawLine((int) x2, (int) y2, (int) (xx2 + 8 * dy), (int) (yy2 - 8 * dx));
        g2.drawLine((int) xx1, (int) yy1, (int) px1, (int) py1);
        g2.drawLine((int) xx1, (int) yy1, (int) px2, (int) py2);
        //      g2.drawLine((int) xx2, (int) yy2, (int) qx1, (int) qy1);
        //      g2.drawLine((int) xx2, (int) yy2, (int) qx2, (int) qy2);

        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);
        g2.setColor(Color.black);
    }

    /**
     * Selects the arrow if the given coordinates are within the arrow's range.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the arrow is selected, false otherwise
     */
    boolean select(double x, double y) {
        double x1, y1, x2, y2;
        x1 = st.getx();
        y1 = st.gety();

        x2 = ed.getx();
        y2 = ed.gety();

        boolean inside = false;

        if (Math.abs(x1 - x2) < CMisc.PIXEPS) {
            inside = ((y - y1) * (y - y2) <= 0);
        } else
            inside = ((x - x1) * (x - x2) <= 0);
        if (!inside)
            return false;
        double d = distance(x, y);
        if (d < CMisc.PIXEPS)
            return true;
        return false;
    }

    /**
     * Gets the slope of the arrow.
     *
     * @return the slope of the arrow
     */
    public double getK() {
        CPoint p1 = st;
        CPoint p2 = ed;
        return (p2.gety() - p1.gety()) / (p2.getx() - p1.getx());
    }

    /**
     * Calculates the distance from the given coordinates to the arrow.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the distance to the arrow
     */
    public double distance(double x, double y) {
        double k = -getK();
        CPoint pt = st;

        if (Math.abs(k) > CMisc.ZERO && Math.abs(1 / k) < CMisc.ZERO) {
            return Math.abs(x - pt.getx());
        }
        double len = Math.abs(y + k * x - pt.gety() - k * pt.getx()) / Math.sqrt(1 + k * k);
        return len;
    }

    /**
     * Saves the arrow to a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the show type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {

    }

    /**
     * Saves the arrow data to an output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(st.m_id);
        out.writeInt(ed.m_id);
        out.writeInt(angle);
        out.writeInt(length);

    }

    /**
     * Loads the arrow data from an input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        st = dp.getPointById(in.readInt());
        ed = dp.getPointById(in.readInt());
        angle = in.readInt();
        length = in.readInt();
    }

}
