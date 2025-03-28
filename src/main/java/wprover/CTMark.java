package wprover;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;

/**
 * CTMark is a class that represents a mark on a drawing, defined by two lines.
 * It provides methods for drawing the mark, moving it, and saving/loading its state.
 */
public class CTMark extends CClass {

    CLine ln1, ln2;
    private double tx, ty;
    private int length = -1;


    // Position;
    int pos1x, pos1y, pos2x, pos2y;
    int pos3x, pos3y, pos4x, pos4y;

    /**
     * Default constructor for CTMark.
     */
    public CTMark() {
        super(TMARK);
        m_color = 3;
        m_dash = 0;
        m_color = DrawData.RED;
    }

    /**
     * Constructs a CTMark with two lines.
     *
     * @param ln1 the first line
     * @param ln2 the second line
     */
    public CTMark(CLine ln1, CLine ln2) {
        super(TMARK);

        m_color = 3;
        m_dash = 0;
        this.ln1 = ln1;
        this.ln2 = ln2;
    }

    /**
     * Returns the type string of the mark.
     *
     * @return the type string
     */
    public String TypeString() {
        return null;
    }

    /**
     * Returns the description of the mark.
     *
     * @return the description
     */
    public String getDescription() {
        return null;
    }

    /**
     * Draws the mark using the specified Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
        draw(g2, false);
    }

    /**
     * Moves the mark by the specified delta values.
     *
     * @param dx the delta x value
     * @param dy the delta y value
     */
    void move(double dx, double dy) {
        double r[] = CLine.Intersect(ln1, ln2);
        if (r == null || r.length == 0)
            return;

        int len = (int) (Math.sqrt(Math.pow(r[0] - dx, 2) + Math.pow(r[1] - dy, 2)));
        int len1 = (int) (Math.sqrt(Math.pow(tx - dx, 2) + Math.pow(ty - dy, 2)));
        double ddx = tx - r[0];
        double ddy = ty - r[1];
        double ddx1 = dx - r[0];
        double ddy1 = dy - r[1];

        if (ddx * ddx1 < 0 && ddy * ddy1 < 0)
            length = CMisc.FOOT_MARK_LENGTH;
        else if (len > 10 && len < 40)
            length = len;
    }

    /**
     * Draws the mark using the specified Graphics2D object, with an option to select it.
     *
     * @param g2 the Graphics2D object
     * @param selected whether the mark is selected
     */
    public void draw(Graphics2D g2, boolean selected) {
        if (!isdraw())
            return;
        if (!selected)
            super.setDraw(g2);
        if (!CLine.isPerp(ln1, ln2))
            return;

        double r[] = CLine.Intersect(ln1, ln2);
        if (r != null && r.length == 2) {
            if (ln1.inside(r[0], r[1]) && ln2.inside(r[0], r[1])) {
                CPoint p = CLine.commonPoint(ln1, ln2);
                if (p == null)
                    drawTTFoot(g2, r[0], r[1], ln1.getfirstPoint(), ln2.getfirstPoint(), selected);
                else
                    drawTTFoot(g2, r[0], r[1], ln1.getSecondPoint(p), ln2.getSecondPoint(p), selected);
            }
        }
    }

    /**
     * Selects the mark based on the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the mark is selected, false otherwise
     */
    boolean select(double x, double y) {
        boolean xr = Math.pow(tx - x, 2) + Math.pow(ty - y, 2) < CMisc.PIXEPS * CMisc.PIXEPS;
        return xr;
    }

    /**
     * Saves the mark to a PostScript file.
     *
     * @param fp the FileOutputStream to write to
     * @param stype the stroke type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!visible) return;

        String st1 = pos1x + " " + -pos1y + " moveto " + pos2x + " " + -pos2y + " lineto \n";
        fp.write(st1.getBytes());
        String st2 = pos3x + " " + -pos3y + " moveto " + pos4x + " " + -pos4y + " lineto \n";
        fp.write(st2.getBytes());
        String st3 = "Color" + m_color + " stroke\n";
        fp.write(st3.getBytes());

    }

    /**
     * Draws the TTFoot mark using the specified Graphics2D object.
     *
     * @param g2 the Graphics2D object
     * @param x the x coordinate
     * @param y the y coordinate
     * @param p1 the first point
     * @param p2 the second point
     * @param select whether the mark is selected
     */
    public void drawTTFoot(Graphics2D g2, double x, double y, CPoint p1, CPoint p2, boolean select) {
        if (p1 == null || p2 == null) return;

        double step = CMisc.FOOT_MARK_LENGTH;
        if (length > 0)
            step = length;

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
        tx = ex;
        ty = ey;

        super.setDraw(g2);
        if (select) {
            super.setDrawSelect(g2);
        }

        g2.drawLine((int) (fx + dx), (int) (fy + dy), (int) (ex), (int) (ey));
        g2.drawLine((int) (fx + dx1), (int) (fy + dy1), (int) (ex), (int) (ey));

        pos1x = (int) (fx + dx);
        pos1y = (int) (fy + dy);
        pos2x = (int) (ex);
        pos2y = (int) (ey);

        pos3x = (int) (fx + dx1);
        pos3y = (int) (fy + dy1);
        pos4x = (int) (ex);
        pos4y = (int) (ey);
    }

    /**
     * Saves the mark to a DataOutputStream.
     *
     * @param out the DataOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(ln1.m_id);
        out.writeInt(ln2.m_id);
        out.writeInt(length);
    }

    /**
     * Loads the mark from a DataInputStream.
     *
     * @param in the DataInputStream to read from
     * @param dp the DrawProcess used for loading
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        int d = in.readInt();
        ln1 = dp.getLineByid(d);
        d = in.readInt();
        ln2 = dp.getLineByid(d);
        if (CMisc.version_load_now >= 0.053)
            length = in.readInt();
        else
            length = -1;
    }
}
