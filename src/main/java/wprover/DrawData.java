package wprover;

import java.awt.*;
import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * DrawData is a utility class that manages color, dash, and width configurations for drawing operations.
 * It provides methods to add colors, retrieve color and dash values, and save/load configurations.
 */
public class DrawData {
    public static int RED = 3;
    public static int DASH8 = 15;
    public static int WIDTH2 = 2;
    public static int LIGHTCOLOR = 18;

    public static int cindex = 0;
    public static int dindex = 0;
    public static int windex = 2;

    final public static int pointcolor = 3;
    final public static int pointcolor_half_decided = 5;
    final public static int pointcolor_decided = 14;

    public static int polygoncolor = 17;
    public static int anglecolor = 3;
    public static int anglewidth = 2;
    public static int angledash = 0;
    public static int tractcolor = 3;

    private static DrawData dd = new DrawData();
    private static int cnum;

    public Vector colorlist = new Vector();
    public Vector dashlist = new Vector();
    public Vector widthlist = new Vector();

    int default_color_num;

    /**
     * Constructs a new DrawData instance and initializes default color, dash, and width lists.
     */
    private DrawData() {
        Color[] color = {
            Color.blue,
            new Color(0, 255, 255),

            new Color(128, 0, 0),
            Color.red,

            new Color(0, 128, 0),
            Color.green,
            new Color(0, 128, 192),
            new Color(128, 128, 255),
            new Color(255, 0, 255),
            new Color(255, 128, 0),
            new Color(128, 128, 0),
            new Color(255, 255, 0),
            Color.orange,
            Color.white,
            Color.lightGray,
            Color.gray,
            Color.black,
            new Color(204, 255, 204),
            new Color(255, 204, 204),
            new Color(204, 204, 255),
            new Color(204, 255, 255),
            new Color(255, 204, 255),
            new Color(255, 255, 204)
        };

        default_color_num = color.length;
        cnum = this.default_color_num;

        for (int i = 0; i < color.length; i++) {
            colorlist.add(color[i]);
        }


        dashlist.add(0);
        for (int i = 1; i < 10; i++) {
            dashlist.add(i);
            dashlist.add(i + 0.5);
        }

        double[] wid =
                {
                    0.5, 0.8, 1.0, 1.3, 1.5, 1.8
                };
        for (int i = 0; i < wid.length; i++)
            widthlist.add(wid[i]);
        for (int i = 2; i < 20; i++) {
            widthlist.add(i);
            widthlist.add(i + 0.5);
        }
    }

    /**
     * Returns the number of available widths.
     *
     * @return the total width count
     */
    public static int getWidthCounter() {
        return dd.widthlist.size();
    }

    /**
     * Returns the width value at the specified index.
     *
     * @param index the index of the width value to retrieve
     * @return the width value as a double
     */
    public static double getWidth(int index) {
        double d;
        try {
            d = (double) dd.widthlist.get(index);
        } catch (ClassCastException cce) {
            d = (int) dd.widthlist.get(index);
        }
        return d;
    }

    /**
     * Returns the number of available dash configurations.
     *
     * @return the total dash count
     */
    public static int getDashCounter() {
        return dd.dashlist.size();
    }

    /**
     * Returns the dash value at the specified index.
     *
     * @param index the index of the dash value to retrieve
     * @return the dash value as a double
     */
    public static double getDash(int index) {
        double d;
        try {
            d = (double) dd.dashlist.get(index);
        } catch (ClassCastException cce) {
            d = (int) dd.dashlist.get(index);
        }
        return d;
    }

    /**
     * Returns the number of available colors.
     *
     * @return the total color count
     */
    public static int getColorCounter() {
        return dd.colorlist.size();
    }

    /**
     * Returns a color offset from the default angle color.
     *
     * @param n the offset from the angle color index
     * @return the calculated Color object
     */
    public static Color getColorSinceRed(int n) {
        return getColor(anglecolor + n);
    }

    /**
     * Returns the color corresponding to the given index. The index wraps around the color list.
     *
     * @param index the index of the color to retrieve
     * @return the Color object, or null if index is negative
     */
    public static Color getColor(int index) {
        int n = dd.colorlist.size();
        if (index < 0 ) return null;
        return (Color) (dd.colorlist.get(index % n));
    }

    /**
     * Adds a new color to the color list if it is not already present.
     *
     * @param co the Color to add
     * @return the new total count of colors, or the existing index plus one if already present
     */
    public static int addColor(Color co) {
        for (int i = 0; i < dd.colorlist.size(); i++) {
            Color c = (Color) dd.colorlist.get(i);
            if (c.getRGB() == co.getRGB())
                return i + 1;
        }

        dd.colorlist.add(co);
        return dd.colorlist.size();
    }

    /**
     * Resets the color, dash, and width indices to the default status.
     */
    public static void setDefaultStatus() {
        cindex = 0;
        dindex = 0;
        windex = 2;
    }

    /**
     * Sets the color, dash, and width indices to the prove status.
     */
    public static void setProveStatus() {
        cindex = 3;
        dindex = 9;
        windex = 0;

    }

    /**
     * Sets the color, dash, and width indices to the auxiliary status.
     */
    public static void setAuxStatus() {
        cindex = RED;
        dindex = DASH8;
        windex = 2;
    }

    /**
     * Returns the index of the specified color in the color list.
     *
     * @param color the Color to search for
     * @return the index of the color, or -1 if not found
     */
    public static int getColorIndex(Color color) {
        for (int i = 0; i < dd.colorlist.size(); i++) {
            Color c = (Color) dd.colorlist.get(i);
            if (c.getRGB() == color.getRGB())
                return i;
        }
        return -1;
    }

    /**
     * Returns the current color based on the current color index.
     *
     * @return the current Color object
     */
    public static Color getCurrentColor() {
        return getColor(cindex);
    }

    /**
     * Resets the DrawData instance by reinitializing it to default values.
     */
    public static void reset() {
        dd = new DrawData();
    }

    /**
     * Saves the color, dash, and width configuration to the specified PostScript file.
     *
     * @param vc the vector containing color indices
     * @param vd the vector containing dash indices
     * @param vw the vector containing width indices
     * @param fp the file output stream to write to
     * @param stype the style type (0 for color, 1 for gray, 2 for black &amp; white)
     * @throws IOException if an I/O error occurs while writing
     */
    public static void SavePS(Vector vc, Vector vd, Vector vw, FileOutputStream fp, int stype) throws IOException {

        fp.write("%-----define color, dash and width\n".getBytes());

        for (int i = 0; i < vc.size(); i++) {
            Integer In = (Integer) vc.get(i);
            int index = In.intValue();
            Color c = (Color) dd.colorlist.get(index);
            if (stype == 0) {
                String rs = (((1000 * c.getRed()) / 255)) / 1000.0 + "";
                String rg = (((1000 * c.getGreen()) / 255)) / 1000.0 + "";
                String rb = (((1000 * c.getBlue()) / 255)) / 1000.0 + "";
                String s = "/Color" + In.toString() + "{" + rs
                        + " " + rg + " " + rb
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());
            } else if (stype == 1) {
                double gray = (0.11 * c.getRed() + 0.59 * c.getGreen() + 0.3 * c.getBlue()) / 255;
                String s = "/Color" + In.toString() + "{" + gray
                        + " " + gray + " " + gray
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());

            } else if (stype == 2) {
                String s = "/Color" + In.toString() + "{" + 0.0
                        + " " + 0.0 + " " + 0.0
                        + " " + "setrgbcolor" + "} " + " def " + "\n";
                fp.write(s.getBytes());
            }
        }

        for (int i = 0; i < vd.size(); i++) {
            Integer In = (Integer) vd.get(i);
            int index = In.intValue();
            Double db = (Double) dd.dashlist.get(index);
            int v = (int) (db.doubleValue());

            String s;

            s = "/Dash" + In.toString() + " ";
            if (v == 0)
                s += " {[] 0 setdash} def" + "\n";
            else
                s += " {[" + db.toString()
                        + " " + db.toString() + "] 0 " + "setdash" + "} def" + "\n";
            fp.write(s.getBytes());
        }

        for (int i = 0; i < vw.size(); i++) {
            Integer In = (Integer) vw.get(i);
            int index = In.intValue();
            Double db = (Double) dd.widthlist.get(index);
            String s = "/Width" + In.toString() + " {" + db.toString() + " setlinewidth} def " + "\n";
            fp.write(s.getBytes());
        }
    }

    /**
     * Saves additional color configurations to the specified data output stream.
     *
     * @param out the data output stream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    public static void Save(DataOutputStream out) throws IOException {
        int size = dd.colorlist.size();
        out.writeInt(size);
        if (size > cnum) {
            for (int i = cnum; i < size; i++) {
                Color c = (Color) dd.colorlist.get(i);
                int cv = c.getRGB();
                out.writeInt(cv);
            }
        }
    }

    /**
     * Loads color configurations from the specified data input stream.
     *
     * @param in the data input stream to read from
     * @param dp the DrawProcess instance used for object mapping
     * @throws IOException if an I/O error occurs while reading
     */
    public static void Load(DataInputStream in, DrawProcess dp) throws IOException {
        if (CMisc.version_load_now < 0.01) {
            int size = in.readInt();
            dd = new DrawData();

            if (size > 11) {
                for (int i = 11; i < size; i++) {
                    int len = in.readInt();
                    byte[] s = new byte[len];
                    in.read(s, 0, len);
                    String name = new String(s);
                    //dd.namelist.add(name);

                    int cv = in.readInt();
                    Color c = new Color(cv);
                    dd.colorlist.add(c);
                }
                CCoBox.reGenerateAll();
            }
        } else {
            int size = in.readInt();
            dd = new DrawData();

            int colorNumber;
            if (CMisc.version_load_now >= 0.031)
                colorNumber = cnum;
            else
                colorNumber = cnum - 6;

            if (size > colorNumber) {
                for (int i = colorNumber; i < size; i++) {
                    int cv = in.readInt();
                    Color c = new Color(cv);
                    dd.colorlist.add(c);
                }
                CCoBox.reGenerateAll();
            }
        }
    }
}



