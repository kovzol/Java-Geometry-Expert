/**
 * Represents an incenter in a geometric proof.
 * This class extends the CClass and includes properties for lemma, coordinates, and a reference to the next incenter.
 */
package gprover;

public class Incenter extends CClass {
    /** The lemma associated with the incenter. */
    int lemma;

    /** The coordinate of the incenter. */
    int co;

    /** The indices of the points forming the incenter. */
    int i, a, b, c;

    /** The next incenter in the list. */
    Incenter nx;

    /**
     * Constructs an Incenter object with default values.
     */
    public Incenter() {
        type = lemma = 0;
        i = a = b = c = 0;
        co = 0;
        nx = null;
    }
}
