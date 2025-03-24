package gprover;

/**
 * The CNdg class represents a geometric non-degenerate condition.
 * It includes properties for dependent constructions, equivalent conditions,
 * existence flag, type, number, points, and a string description.
 */
public class CNdg {
    /** The maximum number of geometric objects. */
    final public static int MAX_GEO = 16;

    /** The dependent construction. */
    public Object dep;

    /** The equivalent conditions. */
    public Object equ;

    /** The existence flag. */
    public boolean exists = false;

    /** The type of the geometric condition. */
    public int type;

    /** The number of points. */
    public int no = -1;

    /** The array of points. */
    public int[] p = new int[MAX_GEO];

    /** The string description of the geometric condition. */
    String sd;

    /**
     * Constructs a CNdg object with default values.
     */
    public CNdg() {
        dep = equ = sd = null;
        type = 0;
    }

    /**
     * Gets the maximum integer value from the points array.
     *
     * @return the maximum integer value from the points array
     */
    public int getMaxInt() {
        int n = 0;
        for (int i = 0; i <= no; i++) {
            if (n < p[i])
                n = p[i];
        }
        return n;
    }

    /**
     * Constructs a CNdg object by copying another CNdg object.
     *
     * @param c1 the CNdg object to copy
     */
    public CNdg(CNdg c1) {
        dep = c1.dep;
        equ = c1.equ;
        type = c1.type;
        no = c1.no;
        sd = c1.sd;
        exists = c1.exists;
        for (int i = 0; i <= no; i++)
            p[i] = c1.p[i];
    }

    /**
     * Returns a string representation of the geometric condition.
     *
     * @return the string description of the geometric condition
     */
    public String toString() {
        return sd;
    }

    /**
     * Checks if the condition contains the specified points.
     *
     * @param a the first point
     * @param b the second point
     * @return true if the condition contains the specified points, false otherwise
     */
    public boolean contain2(int a, int b) {
        if (a == 0 && b == 0)
            return true;
        return p[0] == a && p[1] == b || p[0] == b && p[1] == a
                || p[2] == a && p[3] == b || p[2] == b && p[3] == a;
    }

    /**
     * Checks if the condition contains the specified point.
     *
     * @param pt the point to check
     * @return true if the condition contains the specified point, false otherwise
     */
    public boolean contain(int pt) {
        if (pt == 0)
            return true;

        for (int i = 0; i <= no; i++)
            if (pt == p[i])
                return true;

        return false;
    }

    /**
     * Checks if there are redundant points in the condition.
     *
     * @return true if there are redundant points, false otherwise
     */
    public boolean redundentPt() {
        for (int i = 0; i <= no; i++) {
            for (int j = i + 1; j <= no; j++)
                if (p[i] == p[j])
                    return true;
        }
        return false;
    }

    /**
     * Adds a point to the condition.
     *
     * @param pt the point to add
     */
    public void addAPt(int pt) {
        p[++no] = pt;
    }

    /**
     * Adds all points from the specified array to the condition.
     *
     * @param p1 the array of points to add
     */
    public void addAllPt(int[] p1) {
        for (int i = 0; i < p.length; i++)
            p[i] = 0;
        int i = 0;
        for (i = 0; i < p1.length && i < p.length; i++) {
            p[i] = p1[i];
            if (p[i] == 0) {
                i--;
                break;
            }

        }
        no = i;
    }

    /**
     * Gets the redundant point in the condition.
     *
     * @return the redundant point, or 0 if there are no redundant points
     */
    public int getRedundentPt() {
        for (int i = 0; i <= no; i++) {
            for (int j = i + 1; j <= no; j++)
                if (p[i] == p[j])
                    return p[i];
        }
        return 0;
    }
}