package gprover;


/**
 * Represents group related polynomial operations.
 */
public class Gr extends Poly {

    /**
     * Creates a new GrTerm object with the specified parameters.
     *
     * @param c1 the first coefficient
     * @param ps1 the first polynomial term
     * @param c2 the second coefficient
     * @param ps2 the second polynomial term
     * @param c the constant term
     * @param nx the next GrTerm in the list
     * @return the created GrTerm object
     */
    GrTerm mk_gr(long  c1, DTerm ps1, long c2, DTerm ps2, int c, GrTerm nx) {
        GrTerm gr = new GrTerm();
        gr.c1 = c1;
        gr.c2 = c2;
        gr.ps1 = ps1;
        gr.ps2 = ps2;
        gr.c = c;
        gr.nx = nx;
        return (gr);
    }

    /**
     * Creates a new GrTerm object with the specified coefficients and polynomial terms.
     *
     * @param c1 the first coefficient
     * @param p1 the first polynomial term
     * @param c2 the second coefficient
     * @param p2 the second polynomial term
     * @return the created GrTerm object
     */
    GrTerm mk_gr1(long c1, XTerm p1, long c2, XTerm p2) {
        GrTerm gr;
        gr = new GrTerm();
        gr.c1 = c1;
        gr.c2 = c2;
        if (p1 == null)
            gr.ps1 = null;
        else
            gr.ps1 = get_dt(1, p1, null);
        if (p2 == null)
            gr.ps2 = null;
        else
            gr.ps2 = get_dt(1, p2, null);
        gr.c = 0;
        gr.ps = null;
        return (gr);
    }
}
