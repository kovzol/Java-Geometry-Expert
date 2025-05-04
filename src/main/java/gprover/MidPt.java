package gprover;

/**
 * Represents a midpoint in a geometric construction.
 * This class extends CClass and includes properties for lemma, conditions, and coordinates.
 */
public class MidPt extends CClass {
    // int type; // Commented out type field

    /** The lemma associated with the midpoint. */
    int lemma;

    /** The condition associated with the midpoint. */
    Cond co;

    /** The coordinates of the midpoint. */
    public int m, a, b;

    /** Reference to the next MidPt object. */
    MidPt nx;

    /**
     * Constructs a MidPt object with default values.
     */
    public MidPt() {
        type = lemma = m = a = b = 0;
        co = null;
        nx = null;
    }
}
