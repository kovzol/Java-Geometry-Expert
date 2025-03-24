package gprover;


/**
 * Constructs a Var object with default values.
 */
public class XTerm {
    public Var var;        // variable
    long c;                // value is an integer
    DTerm ps;              // prefix
    XTerm p;
    String sd;

    /**
     * Constructs an XTerm object with default values.
     */
    public XTerm() {
        var = null;
        c = 0;
        ps = null;
        p = null;
    }

    /**
     * Returns the value of the prefix term.
     *
     * @return the value of the prefix term
     */
    public long getPV() {
        if (ps == null || ps.p == null) return 0;
        return ps.p.c;
    }

    /**
     * Returns the string representation of the term.
     *
     * @return the string representation of the term
     */
    @Override
    public String toString() {
        return sd;
    }

    /**
     * Removes the leading '+' character from the string representation of the term.
     */
    public void cutMark() {
        if (sd != null && sd.trim().startsWith("+"))
            sd = sd.trim().substring(1);
    }

    /**
     * Returns the trimmed string representation of the term without the leading '+' character.
     *
     * @return the trimmed string representation of the term
     */
    public String getString() {
        if (sd == null) return null;
        String t = sd.trim();
        if (t.startsWith("+"))
            return t.substring(1).trim();
        return t;
    }

    /**
     * Returns the number of terms in the linked list of terms.
     *
     * @return the number of terms in the linked list of terms
     */
    public int getTermNumber() {
        XTerm t = this;
        int n = 0;
        while (t != null) {
            DTerm d = t.ps;
            if (d == null || d.nx == null) return n;
            t = d.nx.p;
            n++;
        }
        return n;
    }
}
