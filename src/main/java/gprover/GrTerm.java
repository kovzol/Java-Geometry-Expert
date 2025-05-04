package gprover;

import java.util.Vector;

/**
 * The GrTerm class represents a geometric term in the theorem proving framework.
 * It includes properties for coefficients, polynomial terms, construction type,
 * elimination terms, simplifiers, and a linked list of geometric terms.
 */
public class GrTerm {

    private int ptn = -1;

    long c1;
    DTerm ps1;
    long c2;
    DTerm ps2;
    int c; /* construction */

    public ElTerm el; /* eliminations */
    public DTerm ps; /* simplifiers */

    public GrTerm nx;


    public String text = "";

    /**
     * Returns the text representation of this geometric term.
     *
     * @return the text representing this geometric term.
     */
    public String toString() {
        return text;
    }

    /**
     * Sets the PTN (property tracking number) for this term.
     *
     * @param n the new PTN value.
     */
    public void setPTN(int n) {
        ptn = n;
    }

    /**
     * Retrieves the PTN (property tracking number) of this term.
     *
     * @return the current PTN value.
     */
    public int getPTN() {
        return ptn;
    }

    /**
     * Checks whether this geometric term is equivalent to zero.
     * Determines zero based on the first polynomial term or its coefficient.
     *
     * @return true if the term is zero; false otherwise.
     */
    public boolean isZero() {
        if (ps1 == null) {
            if (c1 == 0)
                return true;
            else return false;
        }
        XTerm p = ps1.p;
        if (p.var == null && p.c == 0) return true;
        return false;
    }

    /**
     * Retrieves all XTerm objects from the first polynomial term.
     * Cuts the mark from the first XTerm in the resulting list.
     *
     * @return a vector containing all XTerm objects.
     */
    public Vector getAllxterm() {
        Vector v = new Vector();
        if (ps1 != null && ps1.p != null) {
            XTerm x = ps1.p;
            while (x != null) {
                v.add(x);
                DTerm d = x.ps;
                if (d != null)
                    d = d.nx;
                if (d != null)
                    x = d.p;
                else break;
            }
        }
        if (v.size() > 0) {
            XTerm x = (XTerm) v.get(0);
            x.cutMark();
        }
        return v;
    }

    /**
     * Retrieves all variables present in the first and second polynomial terms.
     *
     * @return a vector containing all variables.
     */
    public Vector getAllvars() {
        Vector v = new Vector();
        getPSVar(v, ps1);
        getPSVar(v, ps2);
        return v;
    }

    /**
     * Collects variables recursively from a chain of DTerm objects and adds them to the provided vector.
     *
     * @param v the vector to which variables are added.
     * @param d the DTerm chain to process.
     */
    void getPSVar(Vector v, DTerm d) {
        while (d != null) {
            getPVar(v, d.p);
            d = d.nx;
        }
    }

    /**
     * Retrieves a variable from an XTerm and its associated DTerm chain,
     * adding them to the provided vector.
     *
     * @param v the vector to which the variable is added.
     * @param x the XTerm from which the variable is retrieved.
     */
    void getPVar(Vector v, XTerm x) {
        if (x == null) return;
        if (x.var != null)
            v.add(x.var);
        getPSVar(v, x.ps);
    }
}
