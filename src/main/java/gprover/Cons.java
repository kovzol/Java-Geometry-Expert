package gprover;

import wprover.GExpert;

/**
 * The Cons class represents a geometric construction in a proof.
 * It includes properties for type, points, and descriptions,
 * as well as methods for managing and retrieving information about the construction.
 */
public class Cons {
    /** The maximum length of the points array. */
    final public static int MAXLEN = 16;

    /** The unique identifier of the construction. */
    int id = 0;

    /** The type of the construction. */
    public int type = 0;

    /** The number of points in the construction. */
    int no = 0;

    /** The flag indicating if the construction is a conclusion. */
    boolean conc = false;

    /** The array of point identifiers. */
    public int[] ps;

    /** The array of point objects. */
    public Object[] pss;

    /** The string description of the construction. */
    private String sd = null;

    /**
     * Constructs a Cons object with the specified type.
     *
     * @param t the type of the construction
     */
    public Cons(int t) {
        type = t;
        ps = new int[MAXLEN];
        pss = new Object[MAXLEN];
        no = -1;
        conc = false;
    }

    /**
     * Constructs a Cons object by copying another Cons object.
     *
     * @param c the Cons object to copy
     */
    public Cons(Cons c) {
        this(c.type);

        if (c == null)
            return;
        id = c.id;
        type = c.type;
        no = c.no;
        for (int i = 0; i <= no; i++) {
            ps[i] = c.ps[i];
            pss[i] = c.pss[i];
        }
        sd = c.sd;
    }

    /**
     * Gets the number of points in the construction.
     *
     * @return the number of points in the construction
     */
    int getPts() {
        for (int i = 0; i < ps.length; i++)
            if (ps[i] == 0) return i;
        return ps.length;
    }

    /**
     * Sets the unique identifier of the construction.
     *
     * @param id the unique identifier to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the construction.
     *
     * @return the unique identifier of the construction
     */
    public int getId() {
        return id;
    }

    /**
     * Checks if the construction contains the specified point.
     *
     * @param pt the point to check
     * @return true if the construction contains the specified point, false otherwise
     */
    public boolean contains(int pt) {
        for (int i = 0; i < ps.length; i++)
            if (pt == ps[i])
                return true;
        return false;
    }

    /**
     * Gets the last point in the construction.
     *
     * @return the last point in the construction
     */
    public int getLastPt() {
        int pt = 0;
        for (int i = 0; i < ps.length; i++)
            if (pt < ps[i])
                pt = ps[i];
        return pt;
    }

    /**
     * Constructs a Cons object with the specified type and length.
     *
     * @param t the type of the construction
     * @param len the length of the points array
     */
    public Cons(int t, int len) {
        type = t;
        ps = new int[len + 1];
        pss = new Object[len + 1];
        no = -1;
    }

    /**
     * Adds a point to the construction.
     *
     * @param n the point to add
     */
    public void add_pt(int n) {
        if (n == 0)
            return;

        ps[++no] = n;
    }

    /**
     * Adds a point object to the construction.
     *
     * @param s the point object to add
     */
    public void add_pt(Object s) {
        pss[++no] = s;
    }

    /**
     * Adds a point to the construction at the specified index.
     *
     * @param n the point to add
     * @param id the index to add the point at
     */
    public void add_pt(int n, int id) {
        if (ps.length <= id) {
            // TODO. Handle this.
            System.err.println("Index out of bounds: " + this.toString());
            return;
        }
        ps[no = id] = n;
    }

    /**
     * Adds a point object to the construction at the specified index.
     *
     * @param s the point object to add
     * @param id the index to add the point object at
     */
    public void add_pt(Object s, int id) {
        if (pss.length <= id) {
            // TODO. Handle this.
            System.err.println("Index out of bounds: " + this.toString());
            return;
        }
        pss[no = id] = s;
    }

    /**
     * Returns a string representation of the construction.
     *
     * @return the string description of the construction
     */
    public String toString() {
        if (sd == null) {
            String s = "";
            for (int i = 0; i <= no; i++)
                if (pss[i] != null)
                    s += " " + pss[i];

            if (!is_conc()) {
                sd = CST.get_preds(type) + s;
            } else {
                sd = "SHOW: " + CST.getClus(type) + s;
            }
        } else if (type == Gib.CO_NANG || type == Gib.CO_NSEG) {
            return "SHOW: " + CST.getClus(type) + " " + sd;
        }

        if (type == Gib.C_POINT)
            return trim(sd);

        return sd;
    }

    /**
     * Returns an extended string representation of the construction.
     *
     * @return the extended string description of the construction
     */
    public String toStringEx() {
        if (sd == null) {
            String s = "";
            for (int i = 0; i <= no; i++)
                if (pss[i] != null)
                    s += " " + pss[i];

            if (!is_conc()) {
                sd = CST.get_preds(type) + s;
            } else {
                sd = "SHOW: " + CST.getClus(type) + s;
            }
        } else if (type == Gib.CO_NANG || type == Gib.CO_NSEG) {
            return "SHOW: " + CST.getClus(type) + " " + sd;
        }

        return sd;
    }

    /**
     * Sets the string description of the construction.
     *
     * @param s the string description to set
     */
    public void setText(String s) {
        sd = s;
    }

    /**
     * Gets the print text of the construction.
     *
     * @param isSelected the flag indicating if the construction is selected
     * @return the print text of the construction
     */
    public String getPrintText(boolean isSelected) {
        if (sd == null) {
            String s = "";
            for (int i = 0; i <= no; i++)
                if (pss[i] != null)
                    s += " " + pss[i];
            if (!is_conc()) {
                sd = CST.get_preds(type) + s;
            } else {
                sd = "SHOW: " + CST.getClus(type) + s;
            }
        } else if (type == Gib.CO_NANG || type == Gib.CO_NSEG) {
            return "SHOW: " + sd;
        }

        if (!isSelected && type == Gib.C_POINT)
            return trim(sd);

        return sd;
    }

    /**
     * Trims the string to the specified length.
     *
     * @param st the string to trim
     * @param len the length to trim to
     * @return the trimmed string
     */
    public String trim(String st, int len) {
        if (st.length() > len)
            return st.substring(0, len) + "...";
        return st;
    }

    /**
     * Trims the string to the default length.
     *
     * @param st the string to trim
     * @return the trimmed string
     */
    public String trim(String st) {
        return trim(st, 32);
    }

    /**
     * Revalidates the construction.
     */
    public void revalidate() {
        if (this.type == Gib.CO_NANG || this.type == Gib.CO_NSEG)
            return;
        sd = null;
    }

    /**
     * Sets the conclusion flag of the construction.
     *
     * @param r the conclusion flag to set
     */
    public void set_conc(boolean r) {
        conc = r;
    }

    /**
     * Checks if the construction is a conclusion.
     *
     * @return true if the construction is a conclusion, false otherwise
     */
    public boolean is_conc() {
        return type >= 50 && type < 100 && conc;
    }

    /**
     * Gets the point object at the specified index.
     *
     * @param n the index to get the point object from
     * @return the point object at the specified index
     */
    public Object getPTN(int n) {
        if (n < 0 || n >= pss.length)
            return null;
        return pss[n];
    }

    /**
     * Returns a short string representation of the construction.
     *
     * @return the short string description of the construction
     */
    public String toSString() {
        return CST.getDString(pss, type);
    }

    /**
     * Returns a detailed string representation of the construction.
     *
     * @return the detailed string description of the construction
     */
    public String toDString() {
        String s = CST.getDString(pss, type);
        if (conc)
            return GExpert.getLanguage("To Prove:") + " " + s;
        if (type == Gib.C_POINT)
            return trim(s);
        return s;
    }

    /**
     * Returns a detailed string representation of the construction without trimming.
     *
     * @return the detailed string description of the construction without trimming
     */
    public String toDDString() {
        String s = CST.getDString(pss, type, false);
        if (conc)
            return GExpert.getLanguage("To Prove:") + " " + s;
        return s;
    }

    /**
     * Copies the specified Cons object.
     *
     * @param c the Cons object to copy
     * @return the copied Cons object
     */
    public static Cons copy(Cons c) {
        Cons c1 = new Cons(c.type, c.no);
        for (int i = 0; i < c1.no; i++) {
            c1.ps[i] = c.ps[i];
            c1.pss[i] = c.pss[i];
        }

        c1.id = c.id;
        return c1;
    }

    /**
     * Replaces the specified point with another point in the construction.
     *
     * @param a the point to replace
     * @param b the point to replace with
     */
    public void replace(int a, int b) {
        for (int i = 0; i <= no; i++) {
            if (ps[i] == a)
                ps[i] = b;
        }
    }

    /**
     * Checks if the construction is equal to another construction.
     *
     * @param c the construction to compare
     * @return true if the constructions are equal, false otherwise
     */
    public boolean isEqual(Cons c) {
        if (c.type != type)
            return false;
        if (c.no != no)
            return false;
        for (int i = 0; i <= no; i++) {
            if (c.ps[i] != ps[i])
                return false;
        }
        return true;
    }

    /**
     * Reorders the points in the construction based on the type.
     */
    public void reorder() {
        switch (type) {
            case Gib.C_O_L:
                reorder1(0, 1);
                reorder1(0, 2);
                reorder1(1, 2);
                break;
            case Gib.C_O_P:
            case Gib.C_O_T:
                reorder2();
                break;
            case Gib.C_I_EQ:
                reorder1(0, 1);
                break;
            case Gib.C_CIRCUM:
                reorder1(1, 2);
                reorder1(0, 2);
                reorder1(1, 2);
                break;
        }
    }

    /**
     * Reorders two points in the construction.
     *
     * @param m the first point index
     * @param n the second point index
     */
    public void reorder1(int m, int n) {
        if (m == n)
            return;
        if (ps[m] < ps[n]) {
            int d = ps[m];
            ps[m] = ps[n];
            ps[n] = d;
        }
    }

    /**
     * Reorders the points in the construction for specific types.
     */
    public void reorder2() {
        reorder1(0, 1);
        reorder1(2, 3);
        if (ps[0] < ps[2]) {
            int a = ps[0];
            ps[0] = ps[2];
            ps[2] = a;
            a = ps[1];
            ps[1] = ps[3];
            ps[3] = a;
        }
    }

    /**
     * Gets the point less than the specified point in the construction.
     *
     * @param n the point to compare
     * @return the point less than the specified point
     */
    public int getLessPt(int n) {
        int k = 0;

        for (int i = 0; i <= no; i++) {
            if (ps[i] < n && ps[i] > k)
                k = ps[i];
        }
        return k;
    }
}