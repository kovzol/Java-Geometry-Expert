package gprover;

/**
 * The Angles class represents a geometric configuration of four lines.
 * It extends the CClass and includes properties for lemma, condition,
 * and other attributes related to angles.
 */
public class Angles extends CClass
{
    /** The lemma associated with the angles. */
    int lemma;

    /** The condition associated with the angles. */
    Cond co;

    /** An integer attribute related to the angles. */
    int sa;

    /** The four lines that define the angles. */
    public LLine l1, l2, l3, l4;

    /** The next Angles object in a linked list structure. */
    Angles nx;

    /** An integer attribute with a default value of 0. */
    int atp = 0;

    /**
     * Constructs an Angles object with the specified lines.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @param l4 the fourth line
     */
    public Angles(LLine l1, LLine l2, LLine l3, LLine l4)
    {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
    }

    /**
     * Constructs an Angles object with default values.
     */
    public Angles()
    {
        type = lemma = sa = 0;
        co = null;
        nx = null;
        l1 = l2 = l3 = l4 = null;
    }
}
