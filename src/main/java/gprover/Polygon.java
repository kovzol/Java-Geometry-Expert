package gprover;

/**
 * Represents a polygon in a geometric construction.
 */
public class Polygon extends CClass {
    int lemma;
    int qtype;
    Cond co;
    int p[] = new int[10];
    int o;

    Polygon nx;

    /**
     * Constructs a Polygon object with default values.
     */
    public Polygon() {
        lemma = o = 0;
        co = null;
    }

    /**
     * Constructs a Polygon object with a specified type.
     *
     * @param t the type of the polygon
     */
    public Polygon(int t) {
        qtype = t;
        lemma = o = 0;
        co = null;
    }
}
