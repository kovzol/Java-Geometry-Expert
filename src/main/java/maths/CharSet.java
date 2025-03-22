package maths;

/**
 * Represents a character set for polynomial operations.
 */
public class CharSet {
    final private static boolean DEBUG = false;
    private final PolyBasic basic = PolyBasic.getInstance();
    private static final CharSet charset = new CharSet();
    private static int REDUCE_LEN = 2;

    /**
     * Returns the debug status.
     *
     * @return true if debug is enabled; false otherwise
     */
    public static boolean debug() {
        return DEBUG;
    }

    /**
     * Returns the singleton instance of CharSet.
     *
     * @return the singleton instance
     */
    public static CharSet getinstance() {
        return charset;
    }

    /**
     * Processes the given polynomial and returns the resulting polynomial.
     *
     * @param pp the input polynomial
     * @return the resulting polynomial
     */
    public TPoly charset(TPoly pp) {
        TPoly rm, ch, chend, p, output;
        output = null;
        p = pp;
        rm = p;

        if (rm == null) return pp;
        pp = reduce1(pp);

        while (rm != null) {
            TPoly tp;
            ch = chend = tp = rm;

            int vra = basic.lv(tp.getPoly());
            tp = tp.getNext();
            if (tp == null) {
                output = basic.ppush(rm.getPoly(), output);
                break;
            }
            int v = basic.lv(tp.getPoly());

            while (vra == v) {
                chend = tp;
                tp = tp.getNext();
                if (tp == null)
                    break;
                v = basic.lv(tp.getPoly());
            }
            chend.setNext(null);
            rm = tp;

            if (ch == chend) {
                output = basic.ppush(ch.getPoly(), output);
            } else {
                TPoly poly = null;

                while (ch.getNext() != null) {
                    TMono divor = basic.getMinV(vra, ch);
                    do {
                        TMono out;
                        TMono div = ch.getPoly();
                        if (div == divor) continue;
                        out = basic.prem(div, basic.p_copy(divor));

                        int a = basic.lv(out);
                        if (a == 0) {
                            if (DEBUG) {
                                System.out.println("Condition redundancy---------------------:");
                                basic.print(div);
                                basic.print(divor);
                            }
                        } else if (vra > a)
                            rm = basic.ppush(out, rm);
                        else
                            poly = basic.addpoly(out, poly);
                    } while ((ch = ch.getNext()) != null);
                    if (poly == null) {
                        output = basic.ppush(divor, output);
                        break;
                    } else {
                        poly = basic.addpoly(divor, poly);
                        ch = poly;
                        poly = null;
                    }
                }
            }
        }

        reduce(output);
        TPoly tp = reverse(output);
        if (!cfinished(tp))
            tp = charset(tp);

        TPoly p1 = tp;
        while (p1 != null) {
            TMono m = p1.getPoly();
            basic.factor1(m);
            basic.coefgcd(m);
            p1 = p1.getNext();
        }
        return tp;
    }

    /**
     * Reduces the given polynomial.
     *
     * @param poly the input polynomial
     * @return the reduced polynomial
     */
    public TPoly reduce1(TPoly poly) {
        poly = reverse(poly);
        reduce(poly);
        poly = reverse(poly);
        return poly;
    }

    /**
     * Reduces the given polynomial in place.
     *
     * @param poly the input polynomial
     */
    public void reduce(TPoly poly) {
        TPoly p1 = poly;
        while (p1 != null) {
            TMono m = p1.poly;
            if (basic.plength(m) <= REDUCE_LEN) {
                TPoly tx = poly;
                while (tx != null && tx != p1) {
                    TMono m2 = tx.poly;
                    tx.poly = basic.prem(m2, basic.p_copy(m));
                    tx = tx.next;
                }
            }
            p1 = p1.next;
        }
    }

    /**
     * Checks if the given polynomial is finished.
     *
     * @param pp the input polynomial
     * @return true if the polynomial is finished; false otherwise
     */
    public boolean cfinished(TPoly pp) {
        if (pp == null) return true;
        int a = basic.lv(pp.getPoly());
        pp = pp.getNext();
        while (pp != null) {
            int n = basic.lv(pp.getPoly());
            if (a == n)
                return false;
            else {
                a = n;
                pp = pp.getNext();
            }
        }
        return true;
    }

    /**
     * Prints the given polynomial.
     *
     * @param pp the input polynomial
     */
    public void printpoly(TPoly pp) {
        int i = 0;
        while (pp != null) {
            if (pp.getPoly() != null) {
                Integer s = i;
                System.out.print("f" + s.toString() + "= ");
                basic.print(pp.getPoly());
                i++;
            }
            pp = pp.getNext();
        }
    }

    /**
     * Reverses the given polynomial.
     *
     * @param pp the input polynomial
     * @return the reversed polynomial
     */
    public static TPoly reverse(TPoly pp) {
        if (pp == null) return pp;
        TPoly out = null;

        while (pp != null) {
            TPoly p = pp;
            pp = pp.getNext();

            if (out == null) {
                out = p;
                out.setNext(null);
            } else {
                p.setNext(out);
                out = p;
            }
        }
        return out;
    }
}