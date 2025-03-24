package gprover;

/**
 * Constructs a Mnde object with default values.
 */
public class PLine extends CClass {
    int lemma;
    Cond co;
    public int no;
    public LLine[] ln;

    PLine nx;

    /**
     * Constructs a PLine object with two lines.
     *
     * @param l1 the first line
     * @param l2 the second line
     */
    public PLine(LLine l1, LLine l2) {
        this();
        ln[0] = l1;
        ln[1] = l2;
        no = 1;
    }

    /**
     * Constructs a PLine object with default values.
     */
    public PLine() {
        type = lemma = no = 0;
        co = null;
        ln = new LLine[MAX_GEO];
        nx = null;
    }
}
