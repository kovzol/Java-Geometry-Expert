package gprover;

/**
 * The Var class represents a variable in a geometric context.
 * It includes properties for the variable's name, points, and a string representation.
 */
public class Var {
    int nm;
    char[] p = new char[9];
    public int[] pt = new int[4];
    Var nx;

    String sd = null;

    /**
     * Constructs a Var object with default values.
     */
    public Var() {    }

    /**
     * Constructs a Var object with specified values.
     *
     * @param n the name of the variable
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     */
    public Var(int n, int p1, int p2, int p3, int p4) {
        nm = n;
        pt[0] = p1;
        pt[1] = p2;
        pt[2] = p3;
        pt[3] = p4;
    }

    /**
     * Returns the string representation of the variable.
     *
     * @return the string representation of the variable
     */
    @Override
    public String toString() {
        return sd;
    }

    /**
     * Constructs a TLine object with default values.
     */
    public void setString(String s) {
        sd = s;
    }
}
