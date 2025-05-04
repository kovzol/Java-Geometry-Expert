package wprover;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * Represents a Cedmark object, which is a type of equality mark in a geometric drawing.
 * It extends the CClass class and provides methods for drawing and saving the equality mark.
 */
public class Cedmark extends CClass {
    private static int DEFAULT_LEN = 8;
    private static int DEFAULT_GAP = 6;

    CPoint p1, p2;
    private int length = DEFAULT_LEN;
    private int dnum = 2;

    /**
     * Default constructor for Cedmark.
     */
    public Cedmark() {
        super(CClass.EQMARK);
        m_dash = 0;
    }

    /**
     * Gets the length of the equality mark.
     *
     * @return the length of the equality mark
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the length of the equality mark.
     *
     * @param n the length to set
     */
    public void setLength(int n) {
        length = n;
    }

    /**
     * Gets the number of equality marks.
     *
     * @return the number of equality marks
     */
    public int getNum() {
        return dnum;
    }

    /**
     * Sets the number of equality marks.
     *
     * @param n the number to set
     */
    public void setNum(int n) {
        dnum = n;
    }

    /**
     * Constructs a Cedmark object with the specified points.
     *
     * @param pp1 the first point
     * @param pp2 the second point
     */
    public Cedmark(CPoint pp1, CPoint pp2) {
        super(CClass.EQMARK);
        m_color = 3;
        m_dash = 0;
        p1 = pp1;
        p2 = pp2;
    }

    /**
     * Constructs a Cedmark object with the specified points and number of marks.
     *
     * @param pp1 the first point
     * @param pp2 the second point
     * @param d the number of marks
     */
    public Cedmark(CPoint pp1, CPoint pp2, int d) {
        super(CClass.EQMARK);
        m_color = 3;
        m_dash = 0;
        p1 = pp1;
        p2 = pp2;
        dnum = d;
    }

    /**
     * Sets the number of equality marks.
     *
     * @param d the number of marks to set
     */
    public void setdnum(int d) {
        dnum = d;
    }

    /**
     * Gets the type string of the equality mark.
     *
     * @return the type string of the equality mark
     */
    public String TypeString() {
        String st = Language.getLs("Equal Mark");
        return st;
    }

    /**
     * Gets the description of the equality mark.
     *
     * @return the description of the equality mark
     */
    public String getDescription() {
        String st = Language.getLs("Equal Mark");
        return st + " " + p1.m_name + " " + p2.m_name;
    }

    /**
     * Draws the equality mark using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
        if (!isdraw()) return;
        draw(g2, false);
    }

    /**
     * Draws a line for the equality mark.
     *
     * @param x the x-coordinate of the start point
     * @param y the y-coordinate of the start point
     * @param dx the x-direction
     * @param dy the y-direction
     * @param g2 the Graphics2D object
     */
    public void drawALine(double x, double y, double dx, double dy, Graphics2D g2) {
        double xx1 = x - dy * length;
        double yy1 = y + dx * length;

        double xx2 = x + dy * length;
        double yy2 = y - dx * length;
        g2.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2);
    }

    /**
     * Draws the equality mark with the option to highlight if selected.
     *
     * @param g2 the Graphics2D object
     * @param selected true if the equality mark is selected, false otherwise
     */
    void draw(Graphics2D g2, boolean selected) {
        if (!isdraw()) return;

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double dy = y2 - y1;
        double dx = x2 - x1;

        if (dy * dy + dx * dx < DEFAULT_LEN * DEFAULT_LEN)
            return;

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        if (selected) {
            g2.setStroke(CMisc.SelectObjectStroke);
            g2.setColor(CMisc.SelectObjectColor);
        } else
            this.setDraw(g2);

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

        if (!this.isdraw()) {
            g2.setColor(Color.white);
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    /**
     * Selects the equality mark if the given coordinates are within its range.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the equality mark is selected, false otherwise
     */
    boolean select(double x, double y) {
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();

        double xx = (x1 + x2) / 2;
        double yy = (y1 + y2) / 2;

        x = x - xx;
        y = y - yy;

        double fx = (double) dnum * length / 2;
        double fy = length / 2;

        double dx = x1 - x2;
        double dy = y1 - y2;

        double len = Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        double a = (x * dx + y * dy) / (dx * dx + dy * dy);
        if (Math.abs(a) > fx + CMisc.PIXEPS)
            return false;
        double b = (y * dx - x * dy) / (dx * dx + dy * dy);
        if (Math.abs(b) > fy + CMisc.PIXEPS)
            return false;
        return true;
    }

    /**
     * Saves the equality mark line to a PostScript file.
     *
     * @param x the x-coordinate of the start point
     * @param y the y-coordinate of the start point
     * @param dx the x-direction
     * @param dy the y-direction
     * @param fp the file output stream
     * @throws IOException if an I/O error occurs
     */
    public void savePsLine(double x, double y, double dx, double dy, FileOutputStream fp) throws IOException {
        int xx1 = (int) (x - dy * length);
        int yy1 = (int) (y + dx * length);

        int xx2 = (int) (x + dy * length);
        int yy2 = (int) (y - dx * length);

        String s = "";
        s += xx1 + " " + (-yy1) + " moveto " + xx2 + " " + (-yy2) + " lineto ";
        fp.write(s.getBytes());
    }

    /**
     * Saves the equality mark to a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the show type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (!isdraw()) return;
        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double dy = y2 - y1;
        double dx = x2 - x1;

        if (dy * dy + dx * dx < DEFAULT_LEN * DEFAULT_LEN)
            return;

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

                this.savePsLine(xx, yy, dx, dy, fp);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                dgap += DEFAULT_GAP;
            }
        } else {
            int dgap = DEFAULT_GAP;
            this.savePsLine(x, y, dx, dy, fp);
            for (int i = 0; i < dnum / 2; i++) {
                double xx = x + dgap * dx;
                double yy = y + dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                xx = x - dgap * dx;
                yy = y - dgap * dy;
                this.savePsLine(xx, yy, dx, dy, fp);
                dgap += DEFAULT_GAP;
            }
        }
        this.saveSuper(fp);
    }

    /**
     * Saves the equality mark data to an output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(p1.m_id);
        out.writeInt(p2.m_id);
        out.writeInt(length);
        out.writeInt(dnum);
    }

    /**
     * Loads the equality mark data from an input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        int id = in.readInt();
        p1 = dp.getPointById(id);
        id = in.readInt();
        p2 = dp.getPointById(id);
        length = in.readInt();
        dnum = in.readInt();
    }
}
