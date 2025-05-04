package wprover;

import gprover.Cond;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.*;
import java.util.Vector;

/**
 * CProveText is a class that represents a proof text in a graphical user interface.
 * It contains methods for drawing the text, handling mouse events, and saving/loading data.
 */
public class CProveText {
    final private static int HSpace = 4;
    private static int D_ROW = 0;
    private static Color cchid = Color.blue;
    private static Image arrow;
    private int m_row = -1;
    private boolean isMouseOnArrow = false;
    private double ax, ay;

    private static JPanel d;

    private String head;
    private String msg;
    private String rule;
    private String rpath;

    private Font font;
    private Color chead = Color.blue;
    private Color cmsg = Color.black;
    private double x, y;

    private double w, h;
    private double height;
    private double width;
    private double whead;

    private boolean visible = true;
    private boolean isexpand = false;


    private CProveField cpfield;

    private UndoStruct m_undo;
    private Cond m_co = null;

    private String bidx = "";

    /**
     * Sets the expanded state of the proof text.
     *
     * @param exp true to expand, false to collapse
     */
    public void setExpanded(boolean exp) {
        isexpand = exp;
    }

    /**
     * Checks if the proof text is expanded.
     *
     * @return true if expanded, false otherwise
     */
    public boolean isExpanded() {
        return isexpand;
    }

    /**
     * Retrieves the condition associated with this proof text.
     *
     * @return the condition
     */
    public Cond getcond() {
        return m_co;
    }

    /**
     * Default constructor for the CProveText class.
     */
    public CProveText() {

    }

    /**
     * Resets the row counter to zero.
     */
    public static void resetRow() {
        D_ROW = 0;
    }

    /**
     * Retrieves the current row counter value.
     *
     * @return the current row counter value
     */
    public static int getRow() {
        return D_ROW;
    }

    /**
     * Constructor for the CProveText class with specified header and message.
     *
     * @param s1 the header text
     * @param s2 the message text
     */
    public CProveText(String s1, String s2) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = s1;
        msg = s2;
    }

    /**
     * Constructor for the CProveText class with an undo structure and header.
     *
     * @param un the undo structure
     * @param s the header text
     */
    public CProveText(UndoStruct un, String s) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = s;
        msg = un.msg;
        if (un.m_type == UndoStruct.T_COMBINED_NODE || (un.m_type == UndoStruct.T_PROVE_NODE) && un.childundolist.size() > 0) {
            cpfield = new CProveField(un.childundolist);
            cmsg = cchid;
        }
        m_undo = un;
    }

    /**
     * Constructor for the CProveText class with a specified message.
     *
     * @param s the message text
     */
    public CProveText(String s) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = "";
        msg = s;
    }

    /**
     * Constructor for the CProveText class with a condition and header.
     *
     * @param co the condition
     * @param s the header text
     */
    public CProveText(Cond co, String s) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = s;
        msg = co.getText();
    }

    /**
     * Constructor for the CProveText class with an undo structure.
     *
     * @param un the undo structure
     */
    public CProveText(UndoStruct un) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = "";
        msg = un.msg;
        if (un.m_type == UndoStruct.T_COMBINED_NODE || (un.m_type == UndoStruct.T_PROVE_NODE) && un.childundolist.size() > 0) {
            cpfield = new CProveField(un.childundolist);
            cmsg = cchid;
        }
        m_undo = un;
    }

    /**
     * Constructor for the CProveText class with a vector, condition, index, and a boolean flag.
     *
     * @param vl the vector
     * @param co the condition
     * @param index the index
     * @param gc the boolean flag
     */
    public CProveText(Vector vl, Cond co, int index, boolean gc) {
        m_co = co;
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        if (index >= 0)
            head = (index + 1) + "";
        else
            head = "";

        if (index >= 0) {
            if (index < 10)
                head += "  ";
            else
                head += " ";
        }
        int n = co.getNo();
        Cond c = co.getPCO();
        boolean cons = true;
        Vector vv = new Vector();
        while (c != null) {
            if (c.getNo() != 0) {
                cons = false;
                break;
            }
            vv.add(c);
            c = c.nx;
        }
        if (co.getPCO() == null) {
            msg = co.getText();
        } else if (cons) {
            msg = co.getText();
            cmsg = cchid;
            cpfield = new CProveField(vv, false);
        } else if (n > 0) {
            msg = "Hence " + co.getText();
            cmsg = cchid;

            Cond tc = co.getPCO();
            String dix = "  by ";
            int nco = 0;
            while (tc != null) {
                int j = 0;
                if (tc.getNo() != 0)
                    for (j = 0; j < vl.size(); j++) {
                        Cond c1 = (Cond) vl.get(j);
                        if (tc.getNo() == c1.getNo())
                            break;
                    }
                else {
                    int k = vl.indexOf(co);
                    for (j = k; j >= 0; j--)
                        if (vl.get(j) == tc)
                            break;
                }
                dix += (j + 1);
                nco++;
                tc = tc.nx;
                if (tc != null)
                    dix += ",";

            }
            if (nco > 1) {
                bidx = "   " + dix;
                msg += bidx;
            }
        } else {
            msg = co.getText();
        }
    }

    /**
     * Constructor for the CProveText class with an undo structure and index.
     *
     * @param un the undo structure
     * @param index the index
     */
    public CProveText(UndoStruct un, int index) {
        rule = "";
        rpath = "";
        font = new Font("Dialog", Font.PLAIN, 14);
        head = (index + 1) + ":  ";
        msg = un.msg;
        if (un.m_type == UndoStruct.T_COMBINED_NODE || (un.m_type == UndoStruct.T_PROVE_NODE) && un.childundolist.size() > 0) {
            cpfield = new CProveField(un.childundolist);
            cmsg = cchid;
        }
        m_undo = un;
    }

    /**
     * Sets the font size for the proof text.
     *
     * @param size the font size to set
     */
    public void setFontSize(int size) {
        font = new Font(font.getName(), font.getStyle(), size);
        if (cpfield != null)
            cpfield.setFontSize(size);
    }

    /**
     * Sets the index for the proof text.
     *
     * @param index the index to set
     */
    public void setIndex(int index) {
        head = (index + 1) + ":  ";
        if (cpfield != null)
            cpfield.reGenerateIndex();
    }

    /**
     * Sets the visibility of the proof text.
     *
     * @param v true to make visible, false to hide
     */
    public void setVisible(boolean v) {
        visible = v;
    }

    /**
     * Checks if the proof text is visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean getVisible() {
        return visible;
    }


    /**
     * Retrieves the undo structure associated with this proof text.
     *
     * @return the undo structure
     */
    public UndoStruct getUndoStruct() {
        return m_undo;
    }

    /**
     * Retrieves the rectangle representing the bounds of this proof text.
     *
     * @return the rectangle representing the bounds
     */
    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, (int) w, (int) height);
    }

    /**
     * Retrieves the color of the caption.
     *
     * @return the caption color
     */
    public Color getCaptainColor() {
        return chead;
    }

    /**
     * Sets the rule associated with this proof text.
     *
     * @param r the rule to set
     */
    public void setRule(String r) {
        rule = r;
    }

    /**
     * Retrieves the rule associated with this proof text.
     *
     * @return the rule
     */
    public String getRule() {
        return rule;
    }

    /**
     * Sets the path to the rule file.
     *
     * @param path the path to set
     */
    public void setRulePath(String path) {
        rpath = path;
    }

    /**
     * Retrieves the path to the rule file.
     *
     * @return the rule path
     */
    public String getRulePath() {
        return rpath;
    }

    /**
     * Sets the color of the caption.
     *
     * @param c the color to set
     */
    public void setCaptainColor(Color c) {
        chead = c;
    }

    /**
     * Retrieves the color of the message.
     *
     * @return the message color
     */
    public Color getMessageColor() {
        return cmsg;
    }

    /**
     * Sets the color of the message.
     *
     * @param c the color to set
     */
    public void setMessageColor(Color c) {
        cmsg = c;
    }

    /**
     * Retrieves the font used for the proof text.
     *
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font used for the proof text.
     *
     * @param f the font to set
     */
    public void setFont(Font f) {
        font = f;
    }

    /**
     * Retrieves the header text.
     *
     * @return the header text
     */
    public String getHead() {
        return head;
    }

    /**
     * Sets the header text.
     *
     * @param s the header text to set
     */
    public void setHead(String s) {
        head = s;
    }

    /**
     * Retrieves the message text.
     *
     * @return the message text
     */
    public String getMessage() {
        return msg;
    }

    /**
     * Sets the message text.
     *
     * @param s the message text to set
     */
    public void setMessage(String s) {
        msg = s + "  " + this.bidx;
    }

    /**
     * Retrieves the list of objects associated with the undo structure.
     *
     * @return the list of objects
     */
    public Vector getObjectList() {
        if (m_undo == null) return new Vector();
        return m_undo.objectlist;
    }

    /**
     * Sets the list of objects associated with the undo structure.
     *
     * @param v the list of objects to set
     */
    public void setObjectList(Vector v) {
        if (m_undo != null) {
            m_undo.objectlist.clear();
            m_undo.addObjectRelatedList(v);
        }
    }

    /**
     * Sets the width of the proof text.
     *
     * @param ww the width to set
     */
    public void setWidth(double ww) {
        width = ww;
    }

    /**
     * Retrieves the width of the proof text.
     *
     * @return the width
     */
    public double getWidth() {
        return w;
    }

    /**
     * Sets the x and y coordinates of the proof text.
     *
     * @param x the x-coordinate to set
     * @param y the y-coordinate to set
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the type string of the proof text.
     *
     * @return the type string
     */
    public String TypeString() {
        return "proof text";
    }

    /**
     * Retrieves the description of the proof text.
     *
     * @return the description
     */
    public String getDescription() {
        return this.TypeString();
    }

    /**
     * Selects the child proof text at the given coordinates.
     *
     * @param x1 the x-coordinate to check
     * @param y1 the y-coordinate to check
     * @param onselect a boolean indicating whether to select the child
     * @return the selected child proof text, or null if not found
     */
    public CProveText selectChild(double x1, double y1, boolean onselect) {
        if (cpfield != null)
            return cpfield.select(x1, y1, onselect);
        return null;
    }

    /**
     * Clears the selection of the proof text.
     */
    public void clearSelection() {
        if (cpfield != null)
            cpfield.clearSelection();
    }

    /**
     * Expands or collapses the proof text.
     */
    public void expand() {
        if (this.isexpand)
            this.setExpanded(false);
        else
            this.setExpanded(true);
    }

    /**
     * Redoes the invisible head of the proof text in the draw process.
     *
     * @param dp the draw process
     * @return the proof text with the redone invisible head
     */
    public CProveText redo_invisible_head(DrawProcess dp) {
        if (cpfield == null) return this;
        if (!this.isexpand) return this;

        CProveText ct = cpfield.redo_invisible_head(dp);
        if (ct == null)
            return this;
        else
            return ct;
    }

    /**
     * Retrieves the list of flash objects associated with the proof text.
     *
     * @param v the list to populate with flash objects
     * @param dp the draw process
     */
    public void getFlashObjectList(Vector v, DrawProcess dp) {
        if (m_undo.m_type != UndoStruct.T_PROVE_NODE) {
            v.addAll(m_undo.getAllObjects(dp));
            return;
        }

        if (this.isexpand) {
            v.addAll(m_undo.objectlist);
        } else
            v.addAll(m_undo.getAllObjects(dp));
    }

    /**
     * Finds the proof text associated with the given undo structure.
     *
     * @param un the undo structure to find the proof text for
     * @return the proof text associated with the undo structure, or null if not found
     */
    public CProveText findPText(UndoStruct un) {
        if (un == null)
            return null;
        if (un == m_undo)
            return this;
        if (cpfield == null)
            return null;

        return cpfield.findPText(un);
    }

    /**
     * Finds the next proof step in the draw process.
     *
     * @param dp the draw process
     * @param cpt the current proof text
     * @param find a boolean indicating whether the proof step has been found
     * @return the next proof step, or null if not found
     */
    public CProveText next_prove_step(DrawProcess dp, CProveText cpt, CBoolean find) {
        if (find.getValue() == false) {
            if (cpt == this) {
                find.setValue(true);
                if (this.visible) {
                    if (cpfield != null && this.isexpand)
                        return cpfield.next_prove_step(dp, cpt, find);

                    return null;
                } else
                    return null;
            } else {
                if (cpfield != null) return cpfield.next_prove_step(dp, cpt, find);
                return null;
            }
        } else {
            if (this.visible) {
                if (!this.isexpand || m_undo.m_type == UndoStruct.T_UNDO_NODE) {
                    // dp.redo_step(m_undo);
                }
                return this;
            } else {
                // dp.redo_step(m_undo);
                return null;
            }
        }
    }

    /**
     * Checks if the given coordinates are within the bounds of the proof text.
     *
     * @param x1 the x-coordinate to check
     * @param y1 the y-coordinate to check
     * @return true if the coordinates are within the bounds, false otherwise
     */
    public boolean select(double x1, double y1) {
        double dx = x1 - x;
        double dy = y1 - y;

        if (dx > 0 && dx < width && dy > 0 && dy < height)
            return true;
        else
            return false;
    }

    /**
     * Gets the location for the pop-up menu.
     *
     * @return the location for the pop-up menu
     */
    public Point getPopExLocation() {
        return new Point((int) (ax + 16), (int) (ay + 16));
    }

    /**
     * Handles mouse movement events.
     *
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return the proof text if the mouse is on the arrow, or null otherwise
     */
    public CProveText mouseMove(double x, double y) {
        if (!visible) return null;

        double dx = x - ax;
        double dy = y - ay;
        this.isMouseOnArrow = dx >= 0 && dx <= 16 && dy >= 0 && dy <= 16;
        if (isMouseOnArrow)
            return this;
        if (cpfield != null)
            return cpfield.mouseMove(x, y);
        else
            return null;
    }

    /**
     * Selects all proof texts at the given coordinates.
     *
     * @param x1 the x-coordinate to check
     * @param y1 the y-coordinate to check
     * @return the selected proof text, or null if not found
     */
    public CProveText selectAll(double x1, double y1) {
        if (this.select(x1, y1)) return this;

        if (this.isExpanded())
            return this.selectChild(x1, y1, true);

        return null;
    }

    /**
     * Moves the proof text by the given offsets.
     *
     * @param dx the x-offset to move by
     * @param dy the y-offset to move by
     */
    public void move(double dx, double dy) {
        x = x + (int) dx;
        y = y + (int) dy;
    }

    /**
     * Sets the current position of the proof text.
     *
     * @param p the point to set the position to
     */
    public void setCurrentPosition(Point p) {
        x = p.x;
        y = p.y;
    }

    /**
     * Gets the next position for the proof text.
     *
     * @param p the point to set the next position to
     */
    public void getNextPosition(Point p) {
        p.setLocation((int) x, (int) (y + height));
    }

    /**
     * Runs the proof text to the beginning of the draw process.
     *
     * @param dp the draw process
     * @return true if successful, false otherwise
     */
    public boolean run_to_begin(DrawProcess dp) {
        if (m_undo == null) return false;
        if (cpfield != null)
            cpfield.run_to_begin(dp);
        else if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    /**
     * Undoes the default action for the proof text.
     *
     * @param dp the draw process
     * @return true if successful, false otherwise
     */
    public boolean undo_default(DrawProcess dp) {
        if (m_undo == null) return false;
        if (cpfield != null)
            cpfield.undo_default(dp);
        if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    /**
     * Undoes the proof text to the head of the draw process.
     *
     * @param dp the draw process
     * @return true if successful, false otherwise
     */
    public boolean undo_to_head(DrawProcess dp) {
        if (m_undo == null) return false;
        if (cpfield != null)
            cpfield.undo_to_head(dp);
        if (m_undo.m_type == UndoStruct.T_UNDO_NODE || m_undo.m_type == UndoStruct.T_COMBINED_NODE)
            dp.undo_step(m_undo);
        return true;
    }

    /**
     * Draws the proof text with the given selection state.
     *
     * @param g2 the graphics context
     * @param selected true if the proof text is selected, false otherwise
     */
    public void draw(Graphics2D g2, boolean selected) {
        if (selected == false)
            this.draw(g2);
        else {
            Rectangle rc = new Rectangle((int) (x - 2), (int) (y + 2), (int) width + 4, (int) height + 2);
            g2.setStroke(new BasicStroke(0.5f));
            g2.setColor(new Color(204, 255, 204));
            g2.fill(rc);
            g2.setColor(Color.black);
            g2.draw(rc);
        }
    }

    /**
     * Draws the child proof text at the given point.
     *
     * @param g2 the graphics context
     * @param p the point to draw the child proof text at
     */
    public void drawChild(Graphics2D g2, Point p) {
        if (cpfield != null) {
            cpfield.draw(g2, p);
        }
    }

    /**
     * Finds the proof text with the given row index.
     *
     * @param i the row index to find the proof text for
     * @return the proof text with the given row index, or null if not found
     */
    public CProveText fd_text(int i) {
        if (i == this.m_row)
            return this;
        if (cpfield != null)
            return cpfield.fd_text(i);
        else return null;
    }

    /**
     * Sets the step row to the default value.
     */
    public void setStepRowDefault() {
        this.m_row = -1;
        if (cpfield != null)
            cpfield.setStepRowDefault();
    }

    /**
     * Draws the proof text.
     *
     * @param g2 the graphics context
     */
    public void draw(Graphics2D g2) {
        if (head == null) return;
        m_row = D_ROW++;

        g2.setFont(font);

        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics(head, frc);
        h = lm.getHeight();
        w = 0;
        Rectangle2D r1 = font.getStringBounds(head, frc);
        g2.setColor(chead);

        g2.drawString(head, (float) x, (float) (y + h));
        double tw = r1.getWidth();

        height = h;
        whead = w = tw;
        if (msg == null || msg.length() == 0) return;

        g2.setColor(cmsg);
        String[] sl = msg.split("\n");
        double start = x + tw + HSpace;
        for (int i = 0; i < sl.length; i++) {
            Rectangle2D r2 = font.getStringBounds(sl[i], frc);
            if (r2.getWidth() > w)
                w = r2.getWidth();
            g2.drawString(sl[i], (float) (start), (float) (y + (i + 1) * h));
        }
        height = h * sl.length;
        w = w + tw;
        ax = x + w + 10;
        ay = y + (h - 16);

        if (rule.length() > 0)
            if (isMouseOnArrow) {
                g2.setColor(Color.black);
                g2.drawRect((int) ax, (int) ay, 16, 16);
                g2.drawImage(arrow, (int) (ax), (int) (ay), Color.pink, d);
            } else
                g2.drawImage(arrow, (int) ax, (int) ay, d);
    }

    /**
     * Saves the text representation of the proof to the specified data output stream.
     *
     * @param out the data output stream to write to
     * @param space the number of spaces to indent the text
     * @throws IOException if an I/O error occurs
     */
    public void saveText(DataOutputStream out, int space) throws IOException {
        if (m_undo.m_type == UndoStruct.T_TO_PROVE_NODE || m_undo.m_type == UndoStruct.T_PROVE_NODE) {
            if (msg != null && msg.length() != 0) {
                String tab = "";
                for (int i = 0; i < space; i++)
                    tab += " ";
                tab += head;
                String str = tab + msg + "\n";
                byte[] bt = str.getBytes();
                out.write(bt, 0, bt.length);
            }
            if (cpfield != null)
                cpfield.saveText(out, space + 5);
        }
    }

    /**
     * Saves the proof text as a PostScript file.
     *
     * @param fp the file output stream to write to
     * @param stype the style type (0 for color, 1 for gray, 2 for black &amp; white)
     * @param ntype the number type (0 for default, 1 for 20 added, 2 for 25 added)
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype, int ntype) throws IOException {
        if (visible == false) return;
        if (head == null) return;

        String sf = "/Times-Roman findfont " + font.getSize() + " fzoff add scalefont setfont\n";
        fp.write(sf.getBytes());

        if (head.length() != 0) {
            this.SavePsColor(chead, fp, stype);
            String sh = " " + x + " " + (-y) + " yoff add moveto (" + head + ") show\n";
            fp.write(sh.getBytes());
        }

        this.SavePsColor(cmsg, fp, stype);
        String[] sm = msg.split("\n");
        int sx = (int) (x + whead);
        String s1 = null;

        if (ntype == 1)
            s1 = " " + sx + " 20 add " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";
        else if (ntype == 2)
            s1 = " " + sx + " 25 add " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";
        else
            s1 = " " + sx + " " + (-(int) y) + " yoff add moveto (" + sm[0] + ") show\n";

        fp.write(s1.getBytes());
        for (int i = 1; i < sm.length; i++) {
            String sp = (int) (x + whead) + " " + (-(int) (y + h * i)) + " yoff add moveto (" + sm[i] + ") show\n";
            fp.write("   /yoff  yoff ystep add def\n".getBytes());
            fp.write(sp.getBytes());
        }
        fp.write("   /yoff  yoff ystep add def\n".getBytes());
        if (cpfield != null && this.isexpand)
            cpfield.SavePS(fp, stype);
    }

    /**
     * Sets the PostScript color based on the given color and style type.
     *
     * @param c the color to set
     * @param fp the file output stream to write to
     * @param stype the style type (0 for color, 1 for gray, 2 for black &amp; white)
     * @throws IOException if an I/O error occurs
     */
    public void SavePsColor(Color c, FileOutputStream fp, int stype) throws IOException {
        if (stype == 0) {  // color
            double r = ((double) (100 * c.getRed() / 255)) / 100;
            double g = ((double) (100 * c.getGreen() / 255)) / 100;
            double b = ((double) (100 * c.getBlue() / 255)) / 100;
            String s = r + " " + r + " " + r;
            s += " setrgbcolor ";
            fp.write(s.getBytes());
        } else if (stype == 1) {  // gray
            String s = "";
            double gray = (int) ((0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 2.55) / 100.0;
            s += " " + gray + " " + gray + " " + gray + " setrgbcolor ";
            fp.write(s.getBytes());
        } else if (stype == 2) {  // black & white
            String s = "0.0 0.0 0.0 setrgbcolor ";
            fp.write(s.getBytes());
        }
    }

    /**
     * Writes a string to the specified data output stream.
     *
     * @param out the data output stream to write to
     * @param s the string to write
     * @throws IOException if an I/O error occurs
     */
    public void WriteString(DataOutputStream out, String s) throws IOException {
        out.writeInt(s.length());
        out.writeChars(s);
    }

    /**
     * Writes a font to the specified data output stream.
     *
     * @param out the data output stream to write to
     * @param f the font to write
     * @throws IOException if an I/O error occurs
     */
    public void WriteFont(DataOutputStream out, Font f) throws IOException {
        String s = f.getName();
        WriteString(out, s);
        out.writeInt(f.getStyle());
        out.writeInt(f.getSize());
    }

    /**
     * Reads a string from the specified data input stream.
     *
     * @param in the data input stream to read from
     * @return the string read from the input stream
     * @throws IOException if an I/O error occurs
     */
    public String ReadString(DataInputStream in) throws IOException {
        int size = in.readInt();
        if (size == 0) return new String("");
        String s = new String();
        for (int i = 0; i < size; i++)
            s += in.readChar();
        return s;
    }

    /**
     * Reads a font from the specified data input stream.
     *
     * @param in the data input stream to read from
     * @return the font read from the input stream
     * @throws IOException if an I/O error occurs
     */
    public Font ReadFont(DataInputStream in) throws IOException {
        String name = ReadString(in);
        int stye = in.readInt();
        int size = in.readInt();

        return new Font(name, stye, size);
    }

    /**
     * Saves the proof text data to the specified data output stream.
     *
     * @param out the data output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        this.WriteString(out, head);
        this.WriteString(out, msg);
        this.WriteString(out, rule);
        this.WriteString(out, rpath);

        this.WriteFont(out, font);

        out.writeInt(chead.getRGB());
        out.writeInt(cmsg.getRGB());

        out.writeDouble(x);
        out.writeDouble(y);

        out.writeBoolean(visible);
        out.writeBoolean(isexpand);

        if (cpfield != null) {
            out.writeBoolean(true);
            cpfield.Save(out);
        } else
            out.writeBoolean(false);

        if (m_undo == null)
            out.writeBoolean(false);
        else {
            out.writeBoolean(true);
            out.writeInt(m_undo.m_id);
        }
    }

    /**
     * Loads the proof text data from the specified data input stream.
     *
     * @param in the data input stream to read from
     * @param dp the draw process
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        head = this.ReadString(in);
        msg = this.ReadString(in);
        if (CMisc.version_load_now >= 0.033)
            rule = this.ReadString(in);
        else
            rule = "";
        if (CMisc.version_load_now >= 0.034)
            rpath = this.ReadString(in);
        else {
            if (rule.length() > 0) {
                String sp = File.separator;
                rpath = "rules" + sp + rule + ".gex";
            } else
                rpath = "";
        }
        font = this.ReadFont(in);
        int c = in.readInt();
        chead = new Color(c);
        c = in.readInt();
        cmsg = new Color(c);

        x = in.readDouble();
        y = in.readDouble();

        visible = in.readBoolean();
        isexpand = in.readBoolean();

        boolean cp = in.readBoolean();
        if (cp) {
            cpfield = new CProveField();
            cpfield.Load(in, dp);
        }

        boolean isu = in.readBoolean();
        if (isu) {
            int id = in.readInt();
            m_undo = dp.getUndoById(id);
        } else
            m_undo = null;
    }
}
