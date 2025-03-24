package gprover;

/**
 * Represents a point used in geometric constructions.
 * <p>
 * This class stores geometric properties including coordinates,
 * auxiliary data, and arrays that represent point states.
 * It provides methods for setting point attributes and retrieving
 * coordinate information.
 * </p>
 */
public class ProPoint {
    final public static int MAX_GEO = 30;

    public int type, aux, type1;
    public String name = "";
    public int[] ps = new int[12];
    public int[] ps1 = new int[12];
    double x, y, x1, y1;
    Cond co = null;
    String text;


    public ProPoint() {
        type = type1 = 0;
        for (int i = 0; i < 8; i++)
            ps[i] = ps1[i] = 0;
    }

    public ProPoint(int t) {
        this();
        type = t;
    }

    void setPS(int value, int index) {
        if (type1 == 0) {
            if (ps.length <= index) {
                // TODO. Handle this.
                System.err.println("Index out of bounds: " + this.toString());
                return;
            }
            ps[index] = value;
        }
        else {
            if (ps1.length <= index) {
                // TODO. Handle this.
                System.err.println("Index out of bounds: " + this.toString());
                return;
            }
            ps1[index] = value;
        }
    }

    void setType(int t) {
        if ((type == 0 || type == Gib.C_POINT) && t != 0)
            type = t;
        else
            type1 = t;
    }

    public ProPoint(int t, String s) {
        type = t;
        name = s;
    }

    public ProPoint(int Type, String ch, int p1, int p2, int p3, int p4,
                    int p5, int p6, int p7, int p8) {
        type = Type;
        name = (ch);
        ps[0] = p1;
        ps[1] = p2;
        ps[2] = p3;
        ps[3] = p4;
        ps[4] = p5;
        ps[5] = p6;
        ps[6] = p7;
        ps[7] = p8;
    }


    public void set_name(String s) {
        name = s;
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setXY1(int x1, int y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public double getdx() {
        return x;
    }

    public double getdy() {
        return y;
    }

    public int getX1() {
        return (int) x1;

    }

    public int getY1() {
        return (int) y1;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getText() {
        return text;
    }
}

