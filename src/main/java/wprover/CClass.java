package wprover;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * CClass is an abstract class representing a geometric object with various properties and methods.
 * It serves as a base class for different types of geometric objects.
 */

abstract public class CClass {

    final public static int POINT = 1;
    final public static int LINE = 2;
    final public static int CIRCLE = 3;
    final public static int TRACE = 4;
    final public static int DISTANCE = 5;
    final public static int ANGLE = 6;
    final public static int POLYGON = 7;
    final public static int TEXT = 8;
    final public static int TVALUE = 9;
    final public static int PTEXT = 10;
    final public static int EQMARK = 11;
    final public static int TMARK = 12;
    final public static int ARROW = 13;

    final public static int TEMP_POINT = 99;

    int m_id;
    int m_type;
    String m_name;
    int m_color;
    int m_dash;    // if any
    int m_width;   // if any
    boolean visible = true;
    int mode = 0; // 0. normal  1. in flash.

    /**
     * Gets the type of the geometry item.
     *
     * @return the type of the geometry item
     */
    public int get_type() {
        return m_type;
    }

    /**
     * Sets the flashing mode for the geometry item.
     *
     * @param flash true to enable flashing, false to disable
     */
    public void setInFlashing(boolean flash) {
        if (flash)
            mode = 1;
        else
            mode = 0;
    }

    /**
     * Stops the flashing mode for the geometry item.
     */
    public void stopFlash() {
        mode = 0;
    }

    /**
     * Determines if the geometry item should be drawn.
     *
     * @return true if the item should be drawn, false otherwise
     */
    public boolean isdraw() {
        return (visible == true && mode == 0) || (visible == false && mode == 1);
    }

    /**
     * Determines if the geometry item is visible.
     *
     * @return true if the item is visible, false otherwise
     */
    public boolean visible() {
        return visible;
    }

    /**
     * Constructs a CClass object by copying another CClass object.
     *
     * @param c the CClass object to copy
     */
    public CClass(CClass c) {
        m_type = c.m_type;
        m_id = CMisc.getObjectId();
        m_dash = c.m_dash;
        m_width = c.m_width;
        m_color = c.m_color;
    }

    /**
     * Constructs a CClass object with the specified type.
     *
     * @param type the type of the geometry item
     */
    public CClass(int type) {
        m_type = type;

        if (type != TEMP_POINT)
            m_id = CMisc.getObjectId();
        else m_id = -1;

        m_dash = DrawData.dindex;
        m_width = DrawData.windex;

        if (type == TEMP_POINT) {
            m_color = DrawData.pointcolor;
            return;
        }
        if (type == POINT) {
            m_color = DrawData.pointcolor;
            m_width = 2;
        } else if (type == ANGLE) {
            m_color = DrawData.anglecolor;
            m_dash = DrawData.angledash;
            m_width = DrawData.anglewidth;
        } else if (type == POLYGON)
            m_color = DrawData.polygoncolor;
        else if (type == TRACE)
            m_color = DrawData.tractcolor;
        else if (type == ARROW)
            m_color = 16;
        else
            m_color = DrawData.cindex;

        if (type == EQMARK) {
            m_width = 3;
            m_color = 3;
        }
    }

    /**
     * Sets auxiliary attributes for the geometry item.
     */
    public void setAttrAux() {
        m_color = DrawData.RED;
        m_dash = DrawData.DASH8;
        m_width = DrawData.WIDTH2;
    }

    /**
     * Sets attributes for the geometry item by copying another CClass object.
     *
     * @param c the CClass object to copy attributes from
     */
    public void setAttr(CClass c) {
        if (c == null) return;
        this.m_color = c.m_color;
        this.m_dash = c.m_dash;
        this.m_width = c.m_width;
    }

    /**
     * Copies attributes from another CClass object.
     *
     * @param c the CClass object to copy attributes from
     */
    public void copy(CClass c) {
        if (c == null) return;
        this.m_color = c.m_color;
        this.m_dash = c.m_dash;
        this.m_width = c.m_width;
    }

    /**
     * Sets the dash style for the geometry item.
     *
     * @param d the dash style index
     */
    public void setDash(int d) {
        m_dash = d;
    }

    /**
     * Sets the width for the geometry item.
     *
     * @param index the width index
     */
    public void setWidth(int index) {
        m_width = index;
    }

    /**
     * Gets the name of the geometry item.
     *
     * @return the name of the geometry item
     */
    public String getname() {
        return m_name;
    }

    /**
     * Checks if the geometry item has a name set.
     *
     * @return true if the item has a name set, false otherwise
     */
    public boolean hasNameSet() {
        return m_name != null && m_name.length() != 0;
    }

    /**
     * Gets the color of the geometry item.
     *
     * @return the color of the geometry item
     */
    public Color getColor() {
        return DrawData.getColor(m_color);
    }

    /**
     * Gets the color index of the geometry item.
     *
     * @return the color index of the geometry item
     */
    public int getColorIndex() {
        return m_color;
    }

    /**
     * Draws the geometry item using the given Graphics2D object.
     *
     * @param g2 the Graphics2D object
     */
    public void draw(Graphics2D g2) {
    }

    /**
     * Sets the color for the geometry item.
     *
     * @param c the color index
     */
    public void setColor(int c) {
        m_color = c;
    }

    /**
     * Gets the type string of the geometry item.
     *
     * @return the type string of the geometry item
     */
    abstract public String TypeString();

    /**
     * Gets the description of the geometry item.
     *
     * @return the description of the geometry item
     */
    abstract public String getDescription();

    /**
     * Draws the geometry item with the option to highlight if selected.
     *
     * @param g2 the Graphics2D object
     * @param selected true if the item is selected, false otherwise
     */
    abstract void draw(Graphics2D g2, boolean selected);

    /**
     * Selects the geometry item if the given coordinates are within its range.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the item is selected, false otherwise
     */
    abstract boolean select(double x, double y);

    /**
     * Moves the geometry item by the given distances.
     *
     * @param dx the distance to move in the x direction
     * @param dy the distance to move in the y direction
     */
    void move(double dx, double dy) {
    }

    /**
     * Sets the visibility of the geometry item.
     *
     * @param v true to make the item visible, false to hide it
     */
    void setVisible(boolean v) {
        this.visible = v;
    }

    /**
     * Returns the string representation of the geometry item.
     *
     * @return the string representation of the geometry item
     */
    public String toString() {
        return m_name;
    }

    /**
     * Sets the drawing style for a selected geometry item.
     *
     * @param g2 the Graphics2D object
     */
    void setDrawSelect(Graphics2D g2) {
        float w = (float) DrawData.getWidth(m_width);
        g2.setStroke(new BasicStroke(w + 5));
        Color c = CMisc.SelectObjectColor;
        g2.setColor(c);
    }

    /**
     * Sets the drawing style for the geometry item.
     *
     * @param g2 the Graphics2D object
     */
    void setDraw(Graphics2D g2) {
        float w = (float) DrawData.getWidth(m_width);
        if (m_dash > 0) {
            float d = (float) DrawData.getDash(m_dash);
            float dash[] = {d};
            g2.setStroke(new BasicStroke(w, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
        } else
            g2.setStroke(new BasicStroke(w));

        Color c = DrawData.getColor(m_color);
        if (CMisc.ColorMode == 1) {
            float gray = (float) (0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 255;
            c = new Color(gray, gray, gray);
        }

        double r = CMisc.getAlpha();
        if (r != 1.0) {
            Color cc = new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, (float) r);
            g2.setPaint(cc);
        } else
            g2.setPaint(c);
    }

    /**
     * Saves the geometry item to a PostScript file.
     *
     * @param fp the file output stream
     * @param stype the show type
     * @throws IOException if an I/O error occurs
     */
    public abstract void SavePS(FileOutputStream fp, int stype) throws IOException;

    /**
     * Saves the color of the geometry item to a PostScript file.
     *
     * @param fp the file output stream
     * @throws IOException if an I/O error occurs
     */
    public void saveSuperColor(FileOutputStream fp) throws IOException {
        String s = " Color" + m_color + " ";
        fp.write(s.getBytes());
    }

    /**
     * Saves the geometry item to a PostScript file.
     *
     * @param fp the file output stream
     * @throws IOException if an I/O error occurs
     */
    public void saveSuper(FileOutputStream fp) throws IOException {
        String s = " Color" + m_color + " ";
        s += "Dash" + m_dash + " ";
        s += "Width" + m_width + " ";
        s += "stroke \n";
        fp.write(s.getBytes());
    }

    /**
     * Gets the PostScript line string representation of the geometry item.
     *
     * @param x1 the x-coordinate of the starting point
     * @param y1 the y-coordinate of the starting point
     * @param x2 the x-coordinate of the ending point
     * @param y2 the y-coordinate of the ending point
     * @return the PostScript line string representation
     */
    public String getPSLineString(int x1, int y1, int x2, int y2) {
        String s = x1 + " " + y1 + " moveto " + x2 + " " + y2 + " lineto ";
        return s;
    }

    /**
     * Saves the geometry item to a data output stream.
     *
     * @param out the data output stream
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        out.writeInt(m_id);

        if (m_name == null)
            out.writeInt(0);
        else {
            int size = m_name.length();
            out.writeInt(size);

            char[] nn = new char[size];
            m_name.getChars(0, size, nn, 0);
            for (int i = 0; i < size; i++)
                out.writeChar(nn[i]);
//            out.writeChars(m_name);
        }

        out.writeInt(m_color);
        out.writeInt(m_dash);
        out.writeInt(m_width);
        out.writeBoolean(visible);
    }

    /**
     * Loads the geometry item from a data input stream.
     *
     * @param in the data input stream
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        m_id = in.readInt();

        int size = in.readInt();
        if (size != 0) {
            m_name = new String();
            for (int i = 0; i < size; i++)
                m_name += in.readChar();
        }
        if (m_name == null || m_name.length() == 0)
            m_name = " ";

        m_color = in.readInt();
        m_dash = in.readInt();
        m_width = in.readInt();
        if (CMisc.version_load_now >= 0.017)
            visible = in.readBoolean();
    }

}


