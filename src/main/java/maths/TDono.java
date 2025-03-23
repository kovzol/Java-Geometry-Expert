package maths;

/**
 * Represents a Dono object which contains three polynomials.
 *
 * <p>This class is used to store and manipulate polynomials in the context of
 * polynomial operations. Each Dono object consists of three polynomials: p1, p2, and c.</p>
 */
public class TDono {
    public TMono p1;
    public TMono p2;
    public TMono c;

    /**
     * Constructs a new TDono object with the specified polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @param c the third polynomial
     */
    public TDono(TMono p1, TMono p2, TMono c) {
        this.p1 = p1;
        this.p2 = p2;
        this.c = c;
    }
}
