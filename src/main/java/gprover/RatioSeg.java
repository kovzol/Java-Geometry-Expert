package gprover;

/**
 * Represents a ratio segment in a geometric construction.
 */
public class RatioSeg extends CClass {
    //   int type;
    int lemma;
    Cond co;
    public int[] r;
    RatioSeg nx;

    /**
     * The Prover class provides static methods for performing geometric proofs and computations.
     * It manages the state of geometric terms and interacts with the geometric database.
     *
     * <p>This class is not instantiable.</p>
     */
    public RatioSeg() {
        type = lemma = 0;
        co = null;
        r = new int[MAX_GEO];
        nx = null;
    }
}
