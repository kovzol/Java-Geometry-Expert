package wprover;

import maths.Param;

import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.awt.*;

/**
 * CPoint class represents a point in a 2D space with x and y coordinates.
 * It extends the CClass class and implements various methods for drawing,
 * saving, and loading the point's properties.
 */
public class CPoint extends CClass {
    private int type = 0;
    public Param x1, y1;
    private Vector cons = new Vector();
    boolean hasSetColor = false;
    int m_radius = -1; //default.
    private boolean freezed = false;
    CText ptext;


    /**
      * Default constructor for the CPoint class.
      * Initializes the point with default values and sets the text position.
      */
    public CPoint() {
        super(CClass.POINT);
        ptext = new CText(this, 5, -20, CText.NAME_TEXT);
    }

     /**
      * Retrieves the first constraint associated with this point.
      *
      * @return the first Constraint object, or null if no constraints are present
      */
    public Constraint getConstraint() {
        if (cons.size() == 0) return null;
        return (Constraint) cons.get(0);
    }

     /**
      * Constructor for the CPoint class with specified type and coordinates.
      *
      * @param type the type of the point
      * @param X the x-coordinate parameter
      * @param Y the y-coordinate parameter
      */
    public CPoint(int type, Param X, Param Y) {
        super(type);
        x1 = X;
        y1 = Y;
    }

     /**
      * Constructor for the CPoint class with specified name and coordinates.
      *
      * @param Name the name of the point
      * @param X the x-coordinate parameter
      * @param Y the y-coordinate parameter
      */
    public CPoint(String Name, Param X, Param Y) {
        super(CClass.POINT);
        m_name = Name;
        x1 = X;
        y1 = Y;
        ptext = new CText(this, 7, -24, CText.NAME_TEXT);
    }

     /**
      * Constructor for the CPoint class with specified coordinates.
      *
      * @param X the x-coordinate parameter
      * @param Y the y-coordinate parameter
      */
    public CPoint(Param X, Param Y) {
        super(CClass.POINT);
        x1 = X;
        y1 = Y;
        ptext = new CText(this, 7, -24, CText.NAME_TEXT);
    }

     /**
      * Retrieves the text associated with this point.
      *
      * @return the CText object associated with this point
      */
    public CText getPText() {
        if (ptext == null) {
            return new CText(this, 7, -24, CText.NAME_TEXT);
        } else {
            return ptext;
        }
    }

     /**
      * Checks if this point is equal to another object based on the name.
      *
      * @param obj the object to compare with
      * @return true if the names are equal, false otherwise
      */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (m_name == null || m_name.length() == 0) {
            return false;
        }
        return m_name.equals(obj.toString());
    }

     /**
      * Stops the flashing effect for this point and its associated text.
      */
    public void stopFlash() {
        super.stopFlash();
        if (ptext != null)
            ptext.stopFlash();
    }

     /**
      * Sets the flashing state for this point and its associated text.
      *
      * @param flash true to enable flashing, false to disable
      */
    public void setInFlashing(boolean flash) {
        super.setInFlashing(flash);
        if (ptext != null) {
            if (flash) {
                ptext.setInFlashing(true);
            } else {
                ptext.setInFlashing(false);
            }
        }
    }

/**
     * Checks if this point is a fixed point.
     *
     * @return true if both x and y coordinates are solved, false otherwise
     */
    public boolean isAFixedPoint() {
        return x1.Solved && y1.Solved;
    }

    /**
     * Checks if this point is a free point.
     *
     * @return true if both x and y coordinates are not solved, false otherwise
     */
    public boolean isAFreePoint() {
        return !x1.Solved && !y1.Solved;
    }

    /**
     * Sets the color of this point.
     *
     * @param c the color to set
     */
    public void setColor(int c) {
        super.setColor(c);
        this.hasSetColor = true;
    }

    /**
     * Selects this point if the given coordinates are within a certain distance.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the point is selected, false otherwise
     */
    public boolean select(double x, double y) {
        if (visible == false) {
            return false;
        }

        double dis = (Math.pow((getx() - x), 2) + Math.pow((gety() - y), 2));
        if (dis < CMisc.PIXEPS_PT * CMisc.PIXEPS_PT) {
            return true;
        }
        return false;
    }

    /**
     * Draws this point on the given graphics context.
     *
     * @param g2 the graphics context
     * @param selected true if the point is selected, false otherwise
     */
    public void draw(Graphics2D g2, boolean selected) {
        int radius = getRadius();
        setDrawSelect(g2);
        int x = (int) getx();
        int y = (int) gety();
        g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    /**
     * Gets the default radius of the point.
     *
     * @return the default radius of the point
     */
    public int POINT_RADIUS = CMisc.getPointRadius();

    /**
     * Gets the radius of the point. If the radius is not set, it returns the default radius
     * based on whether the application is running as an applet or a standalone application.
     *
     * @return the radius of the point
     */
    public int getRadius() {
        int radius = m_radius;
        if (radius < 0) {
            if (CMisc.isApplication())
                radius = CMisc.getPointRadius();
            else
                radius = POINT_RADIUS; // APPLET ONLY
        }
        return radius;
    }

/**
     * Gets the radius value of the point.
     *
     * @return the radius value of the point
     */
    public int getRadiusValue() {
        return m_radius;
    }

    /**
     * Sets the radius of the point. If the given radius is less than or equal to 0,
     * it sets the radius to the default value.
     *
     * @param r the new radius of the point
     */
    public void setRadius(int r) {
        if (r <= 0)
            m_radius = -1;
        m_radius = r;
    }

    /**
     * Draws the point on the given graphics context.
     *
     * @param g2 the graphics context
     */
    public void draw(Graphics2D g2) {
        if (!isdraw()) {
            return;
        }
        int x = (int) getx();
        int y = (int) gety();
        int radius = getRadius();

        if (radius <= 1) return;

        if (radius < 3) {
            setDraw(g2);
            g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
            return;
        }
        setDraw(g2);
        g2.setColor(new Color(0, 0, 0));
        g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);

        setDraw(g2);
        g2.fillOval(x - radius + 1, y - radius + 1, 2 * radius - 2, 2 * radius - 2);
    }

    /**
     * Draws the point with a specific style on the given graphics context.
     *
     * @param g2 the graphics context
     */
    public void drawA0(Graphics2D g2) {
        if (!isdraw()) {
            return;
        }
        int radius = getRadius();
        int x = (int) getx();
        int y = (int) gety();
        setDraw(g2);
        g2.setColor(Color.black);
        g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        g2.setColor(super.getColor());
        g2.fillOval(x - radius + 1, y - radius + 1, 2 * radius - 2, 2 * radius - 2);
    }

/**
     * Draws the point and its text on the given graphics context.
     *
     * @param g2 the graphics context
     */
    public void draw_wt(Graphics2D g2) {
        this.drawA0(g2);
        if (ptext != null) {
            ptext.draw(g2);
        }
    }

    /**
     * Draws the point with a custom style on the given graphics context.
     *
     * @param g2 the graphics context
     */
    public void draw_ct(Graphics2D g2) {
        int x = (int) getx();
        int y = (int) gety();
        setDraw(g2);
        int radius = CMisc.getPointRadius() + 2;

        g2.setColor(Color.white);
        g2.fillOval(x - radius + 1, y - radius + 1, 2 * radius - 2, 2 * radius - 2);

        g2.setColor(Color.black);
        g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
        radius -= 3;
        g2.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    /**
     * Returns the type of the point as a string.
     *
     * @return the type of the point
     */
    public String TypeString() {
        String s1 = Language.getLs("Point");

        if (m_name == null) {
            return GExpert.getLanguage("Point");
        }

        return GExpert.getTranslationViaGettext("Point {0}", m_name);
    }

    /**
     * Returns a description of the point.
     *
     * @return the description of the point
     */
    public String getDescription() {
        if (this.isAFreePoint()) {
            String s1 = Language.getLs("Free Point");
            return s1 + " " + this.m_name;
        } else {
            String s1 = Language.getLs("Point");
            return s1 + " " + m_name;
        }
    }

    //////////////////////////////////////////////////////

    /**
     * Sets the default color of the point based on its solved state.
     */
    public void setColorDefault() {
        if (this.hasSetColor) {
            return;
        }
        if (!x1.Solved && !y1.Solved) {
            this.m_color = DrawData.pointcolor;
        } else if (x1.Solved && y1.Solved) {
            this.m_color = DrawData.pointcolor_decided;
        } else {
            this.m_color = DrawData.pointcolor_half_decided;
        }
    }

/**
     * Adds a constraint to the point if it is not already present.
     *
     * @param cs the constraint to add
     */
    public void addcstoPoint(Constraint cs) {
        if (cs != null && !cons.contains(cs)) {
            cons.add(cs);
        }
    }

    /**
     * Checks if the given x and y coordinates are valid based on the constraints.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates are valid, false otherwise
     */
    public boolean check_xy_valid(double x, double y) {
        for (int i = 0; i < cons.size(); i++) {
            Constraint cs = (Constraint) cons.get(i);
            if (!cs.check_constraint(x, y))
                return false;
        }
        return true;
    }

    /**
     * Checks if this point is equal to another point based on their coordinates.
     *
     * @param p the point to compare with
     * @return true if the points have the same coordinates, false otherwise
     */
    public boolean isEqual(CPoint p) {
        if ((p.x1 == this.x1) && (p.y1 == this.y1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if this point is equal to given coordinates based on their indices.
     *
     * @param x the x-coordinate index to compare
     * @param y the y-coordinate index to compare
     * @return true if the indices match this point's indices, false otherwise
     */
    public boolean isEqual(int x, int y) {
        if ((x == this.x1.xindex) && (y == this.y1.xindex)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the given location is the same as this point's location.
     *
     * @param x the x-coordinate to compare
     * @param y the y-coordinate to compare
     * @return true if the location matches this point's location, false otherwise
     */
    public boolean isSame_Location(double x, double y) {
        if (Math.abs(x - this.getx()) < CMisc.ZERO &&
                Math.abs(y - this.gety()) < CMisc.ZERO) {
            return true;
        }
        return false;
    }

    /**
     * Gets the x-coordinate of this point.
     *
     * @return the x-coordinate of this point
     */
    public double getx() {
        if (x1 == null) {
            CMisc.print("CPoint error,x1 undefined");
            return -1;
        }
        return x1.value;
    }

    /**
     * Gets the x-coordinate of the point's text.
     *
     * @return the x-coordinate of the point's text
     */
    public int getTx() {
        return ptext.getX();
    }

    /**
     * Gets the y-coordinate of the point's text.
     *
     * @return the y-coordinate of the point's text
     */
    public int getTy() {
        return ptext.getY();
    }

    /**
     * Gets the y-coordinate of this point.
     *
     * @return the y-coordinate of this point
     */
    public double gety() {
        if (y1 == null) {
            CMisc.print("CPoint error,y1 undefined");
            return -1;
        }
        return y1.value;
    }

    /**
     * Checks if this point is frozen.
     *
     * @return true if the point is frozen, false otherwise
     */
    public boolean isFreezed() {
        return freezed;
    }

    /**
     * Sets the frozen state of this point.
     *
     * @param r the new frozen state
     */
    public void setFreezed(boolean r) {
        freezed = r;
    }

    /**
     * Sets the x and y coordinates of this point.
     *
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setXY(double x, double y) {
        if (true) {
            x1.value = x;
            y1.value = y;
        }
    }

    /**
     * Sets the fill color of this point.
     *
     * @param index the color index to set
     */
    public void setFillColor(int index) {
        this.m_color = index;
    }

    /**
     * Returns the string representation of this point.
     *
     * @return the name of this point
     */
    public String toString() {
        return m_name;
    }

    /**
     * Saves the PostScript definition of this point to a file.
     *
     * @param fp the file output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void SavePS_Define_Point(FileOutputStream fp) throws IOException {
        String st = m_name;

        if (st.length() == 0 || st.trim().length() == 0)
            st = "POINT" + m_id;

        String s = '/' + st + " {";

        fp.write(s.getBytes());

        float x1 = (float) (((int) (this.x1.value * 100)) / 100.0);
        float y1 = (float) (((int) (this.y1.value * 100)) / 100.0);

        fp.write(((x1) + " ").getBytes());

        fp.write(((-y1) + "} def \n").getBytes());
    }

    /**
     * Saves the PostScript representation of this point to a file.
     *
     * @param fp the file output stream to write to
     * @param stype the style type
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (visible == false) {
            return;
        }

        String s = null;
        int n = getRadius();
        if (n == 0)
            return;

        String st = m_name;

        if (st.length() == 0 || st.trim().length() == 0)
            st = "POINT" + m_id;

        s = st + " " + n + " cirfill fill " + st + " " + n + " cir black" + " stroke \n";
        fp.write(s.getBytes());
    }

    /**
     * Saves the original PostScript representation of this point to a file.
     *
     * @param fp the file output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void SavePsOringinal(FileOutputStream fp) throws IOException {
        if (visible == false) {
            return;
        }

        String s = null;
        int n = getRadius();

        String st = m_name;

        if (st.length() == 0 || st.trim().length() == 0)
            st = "POINT" + m_id;

        s = st + " " + n + " cirfill ";
        fp.write(s.getBytes());
        this.saveSuperColor(fp);
        s = " fill " + st + " " + n + " cir black" + " stroke \n";
        fp.write(s.getBytes());
    }

    /**
     * Saves the state of this point to a data output stream.
     *
     * @param out the data output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(type);
        out.writeInt(x1.xindex);
        out.writeInt(y1.xindex);
        out.writeInt(/*OnCircleOrOnLine*/0);
        int size = cons.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            Constraint cs = (Constraint) cons.get(i);
            if (cs != null)
                out.writeInt(cs.id);
            else out.writeInt(-1);
        }
        out.writeBoolean(visible);
        out.writeInt(m_radius);
        out.writeBoolean(freezed);
    }

    /**
     * Loads the state of this point from a data input stream.
     *
     * @param in the data input stream to read from
     * @param dp the draw process to use for loading
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        if (CMisc.version_load_now < 0.01) {
            m_id = in.readInt();
            DrawType drawt;
            if (in.readInt() == 0) {
                drawt = null;
            } else {
                drawt = new DrawType();
                drawt.Load(in);
                m_color = drawt.color_index;
                m_dash = drawt.dash;
                m_width = drawt.width;
            }

            int len = in.readInt();
            m_name = new String();
            for (int i = 0; i < len; i++) {
                m_name += in.readChar();
            }
            type = in.readInt();

            int ix = in.readInt();
            x1 = dp.getParameterByindex(ix);
            int iy = in.readInt();
            y1 = dp.getParameterByindex(iy);
            /*OnCircleOrOnLine = */
            in.readInt();
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int id = in.readInt();
                cons.add(dp.getConstraintByid(id));
            }
            visible = in.readBoolean();

            this.ptext = new CText(this, 5, -5, CText.NAME_TEXT);
            dp.addObjectToList(ptext, dp.textlist);

        } else {
            super.Load(in, dp);

            type = in.readInt();
            int ix = in.readInt();
            x1 = dp.getParameterByindex(ix);
            int iy = in.readInt();
            y1 = dp.getParameterByindex(iy);
            /*OnCircleOrOnLine = */
            in.readInt();
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                int id = in.readInt();
                Constraint cs = dp.getConstraintByid(id);
                addcstoPoint(cs);
            }
            visible = in.readBoolean();
            this.hasSetColor = true;
            if (CMisc.version_load_now >= 0.043)
                m_radius = in.readInt();
            else
                m_radius = -1;// default.
            if (CMisc.version_load_now >= 0.050)
                freezed = in.readBoolean();
        }
    }


}

