package gprover;

/**
 * Constructs an STris object with default values.
 */
public class TLine extends CClass {
    int lemma;
    Cond co;
    public LLine l1, l2;
    TLine nx;

    /**
     * Constructs a TLine object with specified line segments.
     *
     * @param l1 the first line segment
     * @param l2 the second line segment
     */
    public TLine(LLine l1, LLine l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    /**
     * Constructs a TLine object with default values.
     */
    public TLine() {
        type = lemma = 0;
        co = null;
        l1 = l2 = null;
        nx = null;
    }
}
