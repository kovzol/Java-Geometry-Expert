
package gprover;
/**
 * Represents a geometric line in the construction.
 * <p>
 * The class extends CClass and is used to model a line in a geometric context.
 * It includes properties such as a lemma identifier, a condition object,
 * an integer number to uniquely identify the line, an array of point indices,
 * and a reference to another LLine (for linked constructs).
 * </p>
 */
public class LLine extends CClass {

    int lemma;
    public Cond co;
    public int no;
    public int[] pt;
    LLine nx;

    /**
     * Constructs an LLine object with default values.
     */
    public LLine() {
        type = lemma = no = 0;
        co = null;
        pt = new int[MAX_GEO];
        nx = null;
    }

    /**
     * Copies the properties of another LLine object to this one.
     *
     * @param l1 the LLine object to copy from
     */
    public void cp_ln(LLine l1) {
        lemma = l1.lemma;
        co = null;
        no = l1.no;
        for (int i = 0; i <= l1.no; i++)
            pt[i] = l1.pt[i];
        nx = null;
    }

    /**
     * Checks if the line contains a specific point.
     *
     * @param n the point to check
     * @return true if the line contains the point, false otherwise
     */
    public boolean containPt(int n) {
        if (n == 0) return false;
        for (int i = 0; i < MAX_GEO; i++)
            if (pt[i] == n) return true;
        return false;
    }

    /**
     * Finds the intersection point of two lines.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return the intersection point, or 0 if there is no intersection
     */
    public static int inter_lls(LLine l1, LLine l2) {
        if (l1 == null || l2 == null || l1 == l2) return (0);
        if (l1 == l2) return 0;

        for (int i = 0; i <= l1.no; i++)
            for (int j = 0; j <= l2.no; j++) {
                if (l1.pt[i] == l2.pt[j]) return (l1.pt[i]);
            }
        return (0);
    }

    /**
     * Gets a point from the line that is not equal to the specified point.
     *
     * @param l1 the line
     * @param p1 the point to exclude
     * @return a point from the line that is not equal to p1, or 0 if no such point exists
     */
    public static int get_lpt1(LLine l1, int p1) {
        for (int j = 0; j <= l1.no; j++) {
            if (l1.pt[j] != p1) return (l1.pt[j]);
        }
        return (0);
    }

    /**
     * Checks if a point is on the line.
     *
     * @param p the point to check
     * @return true if the point is on the line, false otherwise
     */
    final public boolean on_ln(int p) {
        for (int i = 0; i <= no; i++)
            if (pt[i] == p)
                return true;
        return false;
    }
}
