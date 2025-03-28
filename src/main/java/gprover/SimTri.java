package gprover;

/**
 * Rules class holds the translated geometric rules and full angle definitions.
 * The arrays defined in this class provide language-specific translations for
 * geometric construction rules and full angle definitions. These translations are
 * integrated with a gettext-based internationalization system.
 */
public class SimTri extends CClass {
    int lemma;
    Cond co;
    int st;

    public int dr;
    public int[] p1;
    public int[] p2;

    SimTri nx;

    /**
     * Constructs a SimTri object with default values.
     */
    public SimTri() {
        type = lemma = 0;
        co = null;
        dr = 0;
        p1 = new int[3];
        p2 = new int[3];
        nx = null;
    }
}
