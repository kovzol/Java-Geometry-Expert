/**
 * The CongSeg class represents a congruent segment in a geometric proof.
 * It includes properties for lemma, condition, points, types, and the next segment.
 */
package gprover;

public class CongSeg extends CClass {

    /** The lemma associated with the congruent segment. */
    int lemma;

    /** The condition associated with the congruent segment. */
    Cond co;

    /** The first point of the segment. */
    public int p1;

    /** The second point of the segment. */
    public int p2;

    /** The third point of the segment. */
    public int p3;

    /** The fourth point of the segment. */
    public int p4;

    /** The first type of the segment. */
    public int t1;

    /** The second type of the segment. */
    public int t2;

    /** The next congruent segment in the list. */
    CongSeg nx;

    /**
     * Constructs a CongSeg object with default values.
     */
    public CongSeg() {
        type = lemma = 0;
        t1 = t2 = 1;
        co = null;
        nx = null;
    }
}
