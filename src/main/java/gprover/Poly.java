package gprover;


/**
 * The Poly class extends MathBase and provides implementations
 * for various polynomial operations such as addition, subtraction,
 * multiplication, division, and remainder computation.
 * It also supports simplification and printing functionalities for polynomials.
 */
public class Poly extends MathBase {
    /* initials */
    public static int PRO_VEC = 330;
    public static int PRO_FULL = 329;
    public static int PRO_GB = 328;
    public static int PRO_WU = 327;
    public static int PRO_AREA = 325;
    public static int PRO_GDD = 326;
    public static int PRO_MTIME = 324;
    public static int PRO_COLL = 339;

    boolean print_geo = false;
    int pro_type = 0;
    Var all_var, last_var;

/**
     * Initializes the polynomial by setting up the initial variable.
     */
    void init_poly() {
        all_var = new Var();
        last_var = all_var;
        all_var.nx = null;
    }

    /**
     * Constructs a Poly object and initializes it.
     */
    public Poly() {
        init_poly();
    }

    /**
     * Creates a new XTerm object.
     *
     * @return a new XTerm object
     */
    XTerm get_x() {
        return (new XTerm());
    }

    /**
     * Creates a new DTerm object.
     *
     * @return a new DTerm object
     */
    DTerm get_d() {
        return (new DTerm());
    }

    /**
     * Creates a new XTerm object representing a number.
     *
     * @param n the number to represent
     * @return a new XTerm object representing the number
     */
    XTerm get_n(long n) {
        XTerm p1;
        p1 = get_x();
        p1.var = null;
        p1.c = (n);
        return (p1);
    }

    /**
     * Creates a new XTerm object representing a number.
     *
     * @param n the number to represent
     * @return a new XTerm object representing the number
     */
    XTerm get_num(long n) {
        XTerm p1;
        p1 = get_x();
        p1.var = null;
        p1.c = n;
        return (p1);
    }

    /**
     * Creates a new XTerm object with a variable and a DTerm.
     *
     * @param v the variable
     * @param dp1 the DTerm
     * @return a new XTerm object
     */
    XTerm get_xt(Var v, DTerm dp1) {
        XTerm xp1;
        xp1 = get_x();
        xp1.var = v;
        xp1.ps = dp1;
        return (xp1);
    }

    /**
     * Creates a new XTerm object representing a monomial.
     *
     * @param vn the variable
     * @return a new XTerm object representing the monomial
     */
    XTerm get_m(Var vn) {
        DTerm dp1;
        XTerm xp1;
        if (vn == null)
            return (pzero());
        else {
            dp1 = get_dt(1, get_n(1L), null);
            xp1 = get_xt(vn, dp1);
            return (xp1);
        }
    }

    /**
     * Creates a new XTerm object representing a variable raised to a power.
     *
     * @param v the variable
     * @param d the degree
     * @param p the polynomial term
     * @return a new XTerm object
     */
    XTerm get_v(Var v, int d, XTerm p) {
        DTerm dp1;
        XTerm xp1;
        if (v == null)
            return (pzero());
        else if (d == 0)
            return (p);
        else {
            dp1 = get_dt(d, p, null);
            xp1 = get_xt(v, dp1);
            return (xp1);
        }
    }

    /**
     * Creates a new DTerm object.
     *
     * @param d the degree
     * @param xp1 the XTerm
     * @param dp1 the next DTerm
     * @return a new DTerm object
     */
    DTerm get_dt(int d, XTerm xp1, DTerm dp1) {
        DTerm d1;
        d1 = get_d();
        d1.deg = d;
        d1.p = xp1;
        d1.nx = dp1;
        return (d1);
    }

    /**
     * Placeholder method for handling DTerm objects.
     *
     * @param dpt the DTerm object
     */
    void put_d(DTerm dpt) {
    }

    /**
     * Placeholder method for handling XTerm objects.
     *
     * @param xpt the XTerm object
     */
    void put_x(XTerm xpt) {
    }

    /**
     * Handles the given XTerm object and its associated DTerm objects.
     *
     * @param p1 the XTerm object
     */
    void put_p(XTerm p1) {
        DTerm dp1, dp2;
        if (p1 != null) {
            if (p1.var == null)
                put_x(p1);
            else {
                dp1 = p1.ps;
                put_x(p1);
                while (dp1 != null) {
                    put_p(dp1.p);
                    dp2 = dp1;
                    dp1 = dp1.nx;
                    put_d(dp2);
                }
            }
        }
    }

    /**
     * Handles the given DTerm objects.
     *
     * @param dp0 the head of the DTerm list
     */
    void put_ps(DTerm dp0) {
        DTerm dp1;
        while (dp0 != null) {
            put_p(dp0.p);
            dp1 = dp0.nx;
            put_d(dp0);
            dp0 = dp1;
        }
    }

    /**
     * Checks if two numbers are equal.
     *
     * @param c1 the first number
     * @param c2 the second number
     * @return true if the numbers are equal, false otherwise
     */
    boolean num_eq(long c1, long c2) {
        return c1 == c2;
    }

    /**
     * Checks if two polynomial terms are equal.
     *
     * @param p1 the first polynomial term
     * @param p2 the second polynomial term
     * @return true if the polynomial terms are equal, false otherwise
     */
    boolean eq_poly(XTerm p1, XTerm p2) {
        if ((p1.var == null) && (p2.var == null))
            return (num_eq(p1.c, p2.c));
        else if (p1.var != p2.var)
            return (false);
        else
            return (eq_pols(p1.ps, p2.ps));
    }

    /**
     * Checks if two lists of polynomial terms are equal.
     *
     * @param dp1 the first list of polynomial terms
     * @param dp2 the second list of polynomial terms
     * @return true if the lists of polynomial terms are equal, false otherwise
     */
    boolean eq_pols(DTerm dp1, DTerm dp2) {
        while ((dp1 != null) && (dp2 != null)) {
            if ((dp1.deg != dp2.deg) ||
                    !(eq_poly(dp1.p, dp2.p)))
                return (false);
            dp1 = dp1.nx;
            dp2 = dp2.nx;
        }
        if (dp1 == null && dp2 == null) return (true);
        return (dp1 == dp2);
    }

    /**
     * Creates a copy of the given polynomial term.
     *
     * @param p1 the polynomial term to copy
     * @return a copy of the polynomial term
     */
    XTerm cp_poly(XTerm p1) {
        if (p1.var == null)
            return (get_num(p1.c));
        else
            return (get_xt(p1.var, cp_pols(p1.ps)));
    }

    /**
     * Creates a copy of the given list of polynomial terms.
     *
     * @param dp1 the list of polynomial terms to copy
     * @return a copy of the list of polynomial terms
     */
    DTerm cp_pols(DTerm dp1) {
        DTerm dt2, dp2;
        dt2 = new DTerm();
        dt2.nx = null;
        dp2 = dt2;
        while (dp1 != null) {
            dp2.nx = get_dt(dp1.deg, cp_poly(dp1.p), null);
            dp2 = dp2.nx;
            dp1 = dp1.nx;
        }
        return (dt2.nx);
    }

    /**
     * Creates a new XTerm object representing zero.
     *
     * @return a new XTerm object representing zero
     */
    XTerm pzero() {
        return (get_n(0L));
    }

    /**
     * Gets the leading degree of the polynomial term.
     *
     * @param p the polynomial term
     * @return the leading degree of the polynomial term
     */
    int ldeg(XTerm p) {
        DTerm dp1;
        if (p.var == null) return (0);
        dp1 = p.ps;
        return (dp1.deg);
    }

    /**
     * Gets the degree of the polynomial term with respect to a variable.
     *
     * @param p the polynomial term
     * @param v the variable
     * @return the degree of the polynomial term with respect to the variable
     */
    int pdeg(XTerm p, Var v) {
        int tem, md;
        DTerm dp1;
        if (p.var == null) return (0);
        if (p.var == v) return (ldeg(p));
        if (vless(p.var, v)) return (0);
        dp1 = p.ps;
        md = 0;
        while (dp1 != null) {
            tem = pdeg(dp1.p, v);
            if (tem > md) md = tem;
            dp1 = dp1.nx;
        }
        return (md);
    }

    /**
     * Gets the minimum degree of the polynomial term with respect to a variable.
     *
     * @param p the polynomial term
     * @param v the variable
     * @return the minimum degree of the polynomial term with respect to the variable
     */
    int mdeg(XTerm p, Var v) {
        int tem, md;
        DTerm ps1;
        if (p.var == null) return (0);
        if (p.var == v) {
            ps1 = p.ps;
            while (ps1.nx != null) ps1 = ps1.nx;
            return (ps1.deg);
        }
        if (vless(p.var, v)) return (0);
        ps1 = p.ps;
        md = 10000;
        while (ps1 != null) {
            tem = mdeg(ps1.p, v);
            if (tem < md) md = tem;
            ps1 = ps1.nx;
        }
        return (md);
    }

    /**
     * Gets the leading coefficient of the polynomial term.
     *
     * @param p the polynomial term
     * @return the leading coefficient of the polynomial term
     */
    long lcc(XTerm p) {
        DTerm dp1;
        while (p.var != null) {
            dp1 = p.ps;
            p = dp1.p;
        }
        return (p.c);
    }

    /**
     * Gets the first coefficient of the polynomial term.
     *
     * @param p the polynomial term
     * @return the first coefficient of the polynomial term
     */
    long fcc(XTerm p) {
        return (lcc(p));
    }

    static long lcc_p;

    /**
     * Computes the content (greatest common divisor) of the polynomial coefficients.
     *
     * @param p the polynomial term
     * @return the content of the polynomial as an integer
     */
    int lcontent(XTerm p) {
        lcc_p = mk_num(0L);
        lcont1(p);
        if (num_negp(lcc(p))) lcc_p = num_neg(lcc_p);
        return (int) (lcc_p);
    }

    /**
     * Recursively computes and updates the content based on the polynomial's coefficients.
     *
     * @param p the polynomial term
     */
    void lcont1(XTerm p) {
        DTerm ps1;
        if (p.var == null) {
            lcc_p = lgcd(lcc_p, p.c);
            if ((lcc_p) != 0) return;
        } else {
            for (ps1 = p.ps; ps1 != null; ps1 = ps1.nx) {
                lcont1(ps1.p);
                if (num_unit(lcc_p)) return;
            }
        }
    }

    /**
     * Retrieves the initial term of the polynomial.
     *
     * @param p the polynomial term
     * @return the initial term of the polynomial or null if not applicable
     */
    XTerm init(XTerm p) {
        DTerm dp1;
        if (p.var == null) return (null);
        dp1 = p.ps;
        return (dp1.p);
    }

    /**
     * Retrieves a copy of the initial term of the polynomial.
     *
     * @param p the polynomial term
     * @return a copy of the first term of the polynomial or null if not applicable
     */
    XTerm cp_init(XTerm p) {
        DTerm dp1;
        if (p.var == null) return (null);
        dp1 = p.ps;
        return (cp_poly(dp1.p));
    }

    /**
     * Initializes the polynomial with respect to the specified variable.
     *
     * @param p the polynomial term
     * @param v the variable used for initialization
     * @return the initialized polynomial term for the given variable
     */
    XTerm init_v(XTerm p, Var v) {
        return (pinit(p, v, pdeg(p, v)));
    }

    /**
     * Adjusts the polynomial by removing lower degree parts relative to the specified variable.
     *
     * @param p the polynomial term
     * @param v the variable for initialization
     * @param d the degree threshold
     * @return the modified polynomial after initialization
     */
    XTerm pinit(XTerm p, Var v, int d) {
        DTerm dt, dp0, dp1, dp2;
        if (p.var == null) {
            put_x(p);
            return (pzero());
        }
        if (vless(p.var, v)) {
            put_p(p);
            return (pzero());
        }
        if (p.var == v && (ldeg(p) < d)) {
            put_p(p);
            return (pzero());
        }
        if (p.var == v) {
            dp1 = p.ps;
            do {
                dp1.deg -= d;
                dp2 = dp1;
                dp1 = dp1.nx;
            } while ((dp1 != null) && (dp1.deg >= d));
            if (dp1 != null) put_ps(dp1);
            dp2.nx = null;
            return (psimp(p, p.ps));
        }
        dp1 = p.ps;

        dt = new DTerm();
        dp0 = dt;
        while (dp1 != null) {
            dp1.p = pinit(dp1.p, v, d);
            if (pzerop(dp1.p)) {
                dp2 = dp1;
                dp1 = dp1.nx;
                put_x(dp2.p);
                put_d(dp2);
            } else {
                dp0.nx = dp1;
                dp0 = dp1;
                dp1 = dp1.nx;
            }
        }
        dp0.nx = null;
        return (psimp(p, dt.nx));
    }

    /**
     * Computes the remainder of the polynomial by removing its first term.
     *
     * @param p the polynomial term
     * @return the polynomial remainder as an XTerm
     */
    XTerm rem(XTerm p)
//xterm p;
    {
        DTerm dp1, dp2;
        if (p.var == null) {
            put_p(p);
            return (get_n(0L));
        }
        dp1 = p.ps;
        dp2 = dp1.nx;
        put_p(dp1.p);
        put_d(dp1);
        return (psimp(p, dp2));
    }

    /**
     * Calculates the number of immediate terms in the polynomial.
     *
     * @param p the polynomial term
     * @return the count of terms linked directly to the polynomial
     */
    int tlength(XTerm p) {
        DTerm dp1;
        int count = 0;
        if (p.var == null)
            return (0);
        else {
            dp1 = p.ps;
            while (dp1 != null) {
                count++;
                dp1 = dp1.nx;
            }
        }
        return (count);
    }

    /**
     * Computes the total number of subterms within the polynomial.
     *
     * @param p the polynomial term
     * @return the total length of the polynomial, including nested subterms
     */
    int plength(XTerm p) {
        DTerm dp1;
        int count = 0;

        if (pzerop(p))
            return (0);
        else if (p.var == null)
            return (1);
        else {
            dp1 = p.ps;
            while (dp1 != null) {
                count += plength(dp1.p);
                dp1 = dp1.nx;
            }
        }
        return (count);
    }

    /**
     * Computes the length of the DTerm linked list representing differential terms.
     *
     * @param dp1 the head of the DTerm list
     * @return the total number of DTerm nodes
     */
    int dlength(DTerm dp1) {
        int count = 0;
        while (dp1 != null) {
            count += plength(dp1.p);
            dp1 = dp1.nx;
        }
        return (count);
    }

    /**
     * Compares two polynomial terms for ordering.
     *
     * @param p1 the first polynomial term
     * @param p2 the second polynomial term
     * @return true if the first polynomial is considered less than the second; false otherwise
     */
    boolean pls(XTerm p1, XTerm p2) {
        int k1, k2;
        if ((p1.var == null) && (p2.var == null))
            return (false);
        else if (p1.var == null)
            return (true);
        else if (p2.var == null)
            return (false);
        else if (p1.var == p2.var) {
            if (ldeg(p1) < ldeg(p2))
                return (true);
            else if (ldeg(p1) > ldeg(p2))
                return (false);
            else {
                k1 = plength(p1);
                k2 = plength(p2);
                if (k1 <= 2 || k2 <= 2) return (k1 < k2);
                return (pls(init(p1), init(p2)));
            }
        } else if (vless(p1.var, p2.var))
            return (true);
        else
            return (false);
    }

    /**
     * Simplifies the polynomial by updating its terms with the provided DTerm list.
     *
     * @param p1 the polynomial term to simplify
     * @param dp0 the DTerm list used for simplification
     * @return the simplified polynomial term
     */
    XTerm psimp(XTerm p1, DTerm dp0) {
        XTerm p;
        if (dp0 == null) {
            p1.var = null;
            p1.c = mk_num(0L);
            return (p1);
        }
        if (dp0.deg == 0) {
            put_x(p1);
            p = dp0.p;
            put_d(dp0);
            return (p);
        }
        p1.ps = dp0;
        return (p1);
    }

/* Polynomial Plus: destructive */

    /**
     * Adds two polynomial terms.
     *
     * @param p1 the first polynomial term
     * @param p2 the second polynomial term
     * @return the sum of p1 and p2 as a new polynomial term
     */
    XTerm pplus(XTerm p1, XTerm p2) {
        DTerm dp1;
        XTerm xp1;
        // determined here if time maxmum exceeds.

        if (p1.var == null) {
            if (num_zop(p1.c)) {
                put_x(p1);
                return (p2);
            }
            if (p2.var == null) {
                p1.c = num_p(p1.c, p2.c);
                put_x(p2);
                return (p1);
            }
            return (pcplus(p1, p2));
        }
        if (p2.var == null) {
            if (num_zop(p2.c)) {
                put_x(p2);
                return (p1);
            }
            return (pcplus(p2, p1));
        }
        if (p1.var == p2.var) {
            dp1 = p2.ps;
            put_x(p2);
            xp1 = psimp(p1, pplus1(p1.ps, dp1));
            return (xp1);
        }
        if (vless(p1.var, p2.var)) return (pcplus(p1, p2));
        return (pcplus(p2, p1));
    }

    /**
     * Merges two lists of polynomial terms represented as DTerm linked lists.
     *
     * @param dp1 the first DTerm list
     * @param dp2 the second DTerm list
     * @return the merged DTerm list representing the summed polynomial parts
     */
    DTerm pplus1(DTerm dp1, DTerm dp2) {
        DTerm firstd, dp0, dp3;

        firstd = new DTerm();
        dp0 = firstd;
        dp0.nx = dp1;
        while (dp1 != null && dp2 != null) {
            if (dp1.deg == dp2.deg) {
                dp1.p = pplus(dp1.p, dp2.p);
                if (pzerop(dp1.p)) {
                    dp3 = dp1;
                    dp1 = dp1.nx;
                    put_x(dp3.p);
                    put_d(dp3);
                    dp0.nx = dp1;
                    dp3 = dp2;
                    dp2 = dp2.nx;
                    put_d(dp3);
                } else {
                    dp0 = dp1;
                    dp1 = dp1.nx;
                    dp3 = dp2;
                    dp2 = dp2.nx;
                    put_d(dp3);
                }
            } else if (dp1.deg > dp2.deg) {
                dp0 = dp1;
                dp1 = dp1.nx;
            } else {
                dp0.nx = dp2;
                dp0 = dp2;
                dp2 = dp2.nx;
                dp0.nx = dp1;
            }
        }
        if (dp2 != null) dp0.nx = dp2;
        return (firstd.nx);
    }

    /**
     * Adds a constant polynomial term to a polynomial represented by a DTerm chain.
     *
     * @param c the constant polynomial term to add
     * @param p2 the polynomial term (as an XTerm) to which to add the constant
     * @return the resulting polynomial term after addition
     */
    XTerm pcplus(XTerm c, XTerm p2) {
        DTerm dp1, dp2;
        dp2 = new DTerm();

        dp1 = p2.ps;
        while (dp1.nx != null) {
            dp2 = dp1;
            dp1 = dp1.nx;
        }
        if (dp1.deg == 0) {
            dp1.p = pplus(c, dp1.p);
            if (pzerop(dp1.p)) {
                dp2.nx = null;
                put_x(dp1.p);
                put_d(dp1);
            }
        } else
            dp1.nx = get_dt(0, c, null);
        return (p2);
    }

/* Polynomial difference: destructive */

    /**
     * Subtracts the second polynomial term from the first polynomial term destructively.
     *
     * @param p1 the polynomial term from which to subtract
     * @param p2 the polynomial term to subtract
     * @return the resulting polynomial term after subtraction
     */
    XTerm pminus(XTerm p1, XTerm p2) {
        XTerm q1;
        q1 = neg_poly(p2);
        return (pplus(p1, q1));
    }

    /**
     * Negates a polynomial term destructively.
     *
     * @param p the polynomial term to negate
     * @return the negated polynomial term
     */
    XTerm neg_poly(XTerm p) {
        DTerm dp1;
        if (p.var == null) {
            if (num_zop(p.c)) return (p);
            p.c = num_neg(p.c);
            return (p);
        } else {
            for (dp1 = p.ps; dp1 != null; dp1 = dp1.nx) {
                dp1.p = neg_poly(dp1.p);
            }
        }
        return (p);
    }

/* polynomial times: destructive */

    /**
     * Multiplies two polynomial terms destructively.
     *
     * @param p1 the first polynomial term
     * @param p2 the second polynomial term
     * @return the product polynomial term
     */
    XTerm ptimes(XTerm p1, XTerm p2) {
        DTerm dp1;
        if (p1.var == null) {
            if (num_zop(p1.c)) {
                put_p(p2);
                return (p1);
            }
            if (p2.var == null) {
                p1.c = num_t(p1.c, p2.c);
                put_x(p2);
                return (p1);
            }
            return (pctimes(p1, p2));
        }
        if (p2.var == null) {
            if (num_zop(p2.c)) {
                put_p(p1);
                return (p2);
            }
            return (pctimes(p2, p1));
        }
        if (p1.var == p2.var) {
            dp1 = p2.ps;
            put_x(p2);
            return (psimp(p1, ptimes1(p1.ps, dp1)));
        }
        if (vless(p1.var, p2.var)) return (pctimes(p1, p2));
        return (pctimes(p2, p1));
    }

    /**
     * Multiplies two DTerm polynomial parts and accumulates the result.
     *
     * @param dp1 the first DTerm list
     * @param dp2 the second DTerm list
     * @return the resulting DTerm list from the multiplication
     */
    DTerm ptimes1(DTerm dp1, DTerm dp2)
//dterm dp1,dp2;
    {
        DTerm dp0, dp3, dp4;
        XTerm pp1;

        if (dlength(dp1) > dlength(dp2)) {
            dp3 = dp1;
            dp1 = dp2;
            dp2 = dp3;
        }
        dp0 = null;

        while (dp1 != null) {
            if (dp1.nx == null) dp3 = dp2;
            else dp3 = cp_pols(dp2);
            dp4 = dp3;
            while (dp4 != null) {
                if (dp4.nx == null) pp1 = dp1.p;
                else pp1 = cp_poly(dp1.p);
                dp4.p = ptimes(pp1, dp4.p);
                dp4.deg += dp1.deg;
                dp4 = dp4.nx;
            }
            dp0 = pplus1(dp0, dp3);
            dp3 = dp1;
            dp1 = dp1.nx;
            put_d(dp3);
        }
        return (dp0);
    }

    /**
     * Multiplies a constant polynomial term with a polynomial represented as an XTerm.
     *
     * @param c the constant polynomial term
     * @param p the polynomial term to multiply
     * @return the resulting polynomial term after multiplication
     */
    XTerm pctimes(XTerm c, XTerm p)
//xterm c,p;
    {
        DTerm dp1;
        dp1 = p.ps;
        while (dp1.nx != null) {
            dp1.p = ptimes(dp1.p, cp_poly(c));
            dp1 = dp1.nx;
        }
        dp1.p = ptimes(dp1.p, c);
        return (p);
    }

    /**
     * Raises a polynomial term to the specified integer power.
     *
     * @param p the polynomial term
     * @param n the exponent
     * @return the polynomial term raised to the power n
     */
    XTerm ppower(XTerm p, int n) {
        XTerm pp1 = get_n(1L);
        if (n <= 0) {
            put_p(p);
            return (pp1);
        }
        while (n > 1) {
            if (n % 2 == 0) {
                n /= 2;
                p = ptimes(p, cp_poly(p));
            } else {
                n -= 1;
                pp1 = ptimes(pp1, cp_poly(p));
            }
        }
        return (ptimes(pp1, p));
    }

    /**
     * Performs polynomial division when the divisor is a unit.
     *
     * @param p1 the dividend polynomial term
     * @param p2 the divisor polynomial term
     * @return the resulting polynomial term representing the remainder after division
     */
    XTerm ppdiv(XTerm p1, XTerm p2) {
        XTerm p3;
        if (unitp(p2)) return (p1);
        p3 = pdiv(cp_poly(p1), p2);
        while (p3 != null) {
            put_p(p1);
            p1 = cp_poly(p3);
            p3 = pdiv(p3, p2);
        }
        return (p1);
    }

/* polynomial division: destructive for p1 */

    /**
     * Divides the first polynomial term by the second polynomial term destructively.
     *
     * @param p1 the dividend polynomial term
     * @param p2 the divisor polynomial term
     * @return the resulting polynomial term after division, or null if division is not possible
     */
    XTerm pdiv(XTerm p1, XTerm p2)
    {
        DTerm dp1;

        if (p2.var == null) {
            if (num_zop(p2.c)) {
                put_p(p1);
                return (null);
            }
            if (p1.var == null) {
                long c = num_mod(p1.c, p2.c);
                if (num_zop(c)) {
                    p1.c = num_d(p1.c, p2.c);
                    return (p1);
                }
                put_x(p1);
                return (null);
            } else
                return (pcdiv(p1, p2));
        }
        if (p1.var == null) {
            if (num_zop(p1.c))
                return (p1);
            else {
                put_x(p1);
                return (null);
            }
        }

        if ((p1.var == p2.var) && (ldeg(p1) >= ldeg(p2))) {
            dp1 = pdiv1(p1.ps, p2.ps);
            if (dp1 == null) {
                put_x(p1);
                return (null);
            } else
                return (psimp(p1, dp1));
        }
        if (vless(p2.var, p1.var)) return (pcdiv(p1, p2));
        put_p(p1);
        return (null);
    }

    /**
     * Divides a polynomial term by a constant polynomial term.
     *
     * @param p the polynomial term to be divided
     * @param c the constant polynomial term divisor
     * @return the resulting polynomial term after division, or null if division is not possible
     */
    XTerm pcdiv(XTerm p, XTerm c)
    {
        DTerm dp1;
        for (dp1 = p.ps; dp1 != null; dp1 = dp1.nx) {
            dp1.p = pdiv(dp1.p, c);
            if (dp1.p == null) {
                put_p(p);
                return (null);
            }
        }
        return (p);
    }

    /**
     * Divides the polynomial represented by dp1 by dp2.
     * Returns the quotient as a new DTerm or null if the division fails.
     *
     * @param dp1 the dividend polynomial as a DTerm structure
     * @param dp2 the divisor polynomial as a DTerm structure
     * @return the quotient polynomial as a DTerm, or null if division is not exact
     */
    DTerm pdiv1(DTerm dp1, DTerm dp2) {
        DTerm fird, qterm, dp0, dp3, dp4;
        XTerm lcf, qp;
        int deg1, deg2;

        fird = new DTerm();
        fird.nx = null;
        dp0 = fird;
        lcf = dp2.p;
        deg2 = dp2.deg;
        while (dp1 != null) {
            deg1 = dp1.deg;
            if (deg1 < deg2) {
                put_ps(fird.nx);
                put_ps(dp1);
                return (null);
            }
            qp = pdiv(dp1.p, lcf);
            if (qp == null) {
                put_ps(fird.nx);
                put_ps(dp1.nx);
                put_d(dp1);
                return (null);
            }
            dp0.nx = get_dt(deg1 - deg2, qp, null);
            dp0 = dp0.nx;

            qterm = new DTerm();
            dp4 = qterm;
            qterm.nx = null;
            dp3 = dp2.nx;
            if (dp3 != null) {
                qp = neg_poly(cp_poly(qp));
                while (dp3.nx != null) {
                    dp4.nx = get_dt(dp3.deg + deg1 - deg2,
                            ptimes(cp_poly(qp), cp_poly(dp3.p)),
                            null);
                    dp4 = dp4.nx;
                    dp3 = dp3.nx;
                }
                dp4.nx = get_dt(dp3.deg + deg1 - deg2,
                        ptimes(qp, cp_poly(dp3.p)),
                        null);
                dp3 = dp1.nx;
                put_d(dp1);
                dp1 = pplus1(dp3, qterm.nx);
            } else {
                dp3 = dp1.nx;
                put_d(dp1);
                dp1 = dp3;
            }
        }
        return (fird.nx);
    }

    char init_deg;

    /**
     * Computes the polynomial remainder (prem) of p1 with respect to p2.
     *
     * @param p1 the dividend polynomial as an XTerm structure
     * @param p2 the divisor polynomial as an XTerm structure
     * @return the remainder polynomial as an XTerm, or a zero polynomial if p2 is null
     */
    XTerm prem(XTerm p1, XTerm p2) {
        init_deg = 0;
        if (p2.var == null) {
            put_p(p1);
            return (get_n(0L));
        }
        if (p1.var == null) return (p1);
        if (vless(p1.var, p2.var)) return (p1);
        if (p1.var == p2.var) return (prem_ev(p1, p2));
        return (prem_var(p1, p2, p2.var));
    }

    /**
     * Computes the polynomial remainder when both p1 and p2 share the same variable.
     * Uses an evaluation-based approach.
     *
     * @param p1 the dividend polynomial as an XTerm structure
     * @param p2 the divisor polynomial as an XTerm structure with the same variable as p1
     * @return the remainder polynomial as an XTerm, or null if the division is not exact
     */
    XTerm prem_ev(XTerm p1, XTerm p2) {
        DTerm dt1, dp1, dp2, dp3, dp4;
        XTerm ip1, ip2, pp1, pp2;
        int deg1, deg2;

        if (p2.var == null) {
            put_p(p1);
            return (get_n(0L));
        }
        if (p1.var == null) return (p1);
        if (vless(p1.var, p2.var)) return (p1);
        if (p1.var == p2.var) {
            dp1 = p1.ps;
            dp2 = p2.ps;
            deg2 = dp2.deg;
            ip2 = dp2.p;

            while ((p1.var == p2.var) && (dp1.deg >= deg2)) {
                deg1 = dp1.deg;
                ip1 = neg_poly(dp1.p);

                pp2 = pdiv(cp_poly(ip1), ip2);
                /*{ printf("prem_env\n");pprint(p1);pprint(p2); pprint(pp2); }*/
                if (pp2 != null) {
                    put_p(ip1);

                    dp4 = dt1 = new DTerm();

                    dt1.nx = null;
                    dp3 = dp2.nx;
                    while (dp3 != null) {
                        if (dp3.nx == null) pp1 = pp2;
                        else pp1 = cp_poly(pp2);
                        dp4.nx = get_dt(dp3.deg + deg1 - deg2,
                                ptimes(pp1, cp_poly(dp3.p)),
                                null);
                        dp4 = dp4.nx;
                        dp3 = dp3.nx;
                    }
                    dp3 = dp1.nx;
                    put_d(dp1);
                    dp1 = pplus1(dp3, dt1.nx);
                    p1 = psimp(p1, dp1);
                } else {
                    init_deg += 1;
                    dp4 = dt1 = new DTerm();
                    dt1.nx = null;
                    dp3 = dp2.nx;
                    while (dp3 != null) {
                        if (dp3.nx == null) pp1 = ip1;
                        else pp1 = cp_poly(ip1);
                        dp4.nx = get_dt(dp3.deg + deg1 - deg2,
                                ptimes(pp1, cp_poly(dp3.p)),
                                null);
                        dp4 = dp4.nx;
                        dp3 = dp3.nx;
                    }
                    dp3 = dp1.nx;
                    while (dp3 != null) {
                        dp3.p = ptimes(dp3.p, cp_poly(ip2));
                        dp3 = dp3.nx;
                    }
                    dp3 = dp1.nx;
                    put_d(dp1);
                    dp1 = pplus1(dp3, dt1.nx);
                    p1 = psimp(p1, dp1);
                }
            }
            return (p1);
        }
        Cm.print("prem_ev: not exact\n");
        return (null);
    }

    /**
     * Computes the polynomial remainder for p1 with respect to p2 when they have different variable orderings.
     *
     * @param p1 the dividend polynomial as an XTerm structure
     * @param p2 the divisor polynomial as an XTerm structure
     * @param v the variable used for division operations
     * @return the remainder polynomial as an XTerm, or a zero polynomial if division is complete
     */
    XTerm prem_var(XTerm p1, XTerm p2, Var v) {
        XTerm ip0, ip1, ip2, u1, u2, v2;
        int deg1, deg2;
        init_deg = 0;
        if (p2.var == null) {
            put_p(p1);
            return (get_n(0L));
        }
        if (p1.var == null) return (p1);

        deg2 = pdeg(p2, v);
        if (deg2 <= 0) {
            Cm.print("prem_var error!!");
            return null; //
        }
        ;

        ip2 = pinit(cp_poly(p2), v, deg2);
        u2 = ptimes(get_v(v, deg2, get_n(-1L)), cp_poly(ip2));
        u2 = neg_poly(pplus(cp_poly(p2), u2));

        deg1 = pdeg(p1, v);
        while (deg1 >= deg2) {
            ip1 = pinit(cp_poly(p1), v, deg1);

            u1 = ptimes(get_v(v, deg1, get_n(-1L)), cp_poly(ip1));
            u1 = pplus(p1, u1);
            v2 = get_v(v, deg1 - deg2, get_n(1L));
            ip0 = pdiv(cp_poly(ip1), ip2);
            if (ip0 == null) {
                v2 = ptimes(v2, ip1);
                p1 = pplus(ptimes(cp_poly(ip2), u1), ptimes(cp_poly(u2), v2));
                init_deg += 1;
            } else {
                put_p(ip1);
                v2 = ptimes(v2, ip0);
                p1 = pplus(u1, ptimes(cp_poly(u2), v2));
            }
            deg1 = pdeg(p1, v);
        }
/*   printf("\nprem_var out:");pprint(p1); */
        return (p1);
    }

    /**
     * Prints the polynomial in a compact form.
     *
     * @param p1 the polynomial to be printed as an XTerm structure
     */
    final void xprint(XTerm p1) {
        print_p(p1, (char) 0);
    }

    /**
     * Prints the polynomial followed by a newline.
     *
     * @param p1 the polynomial to be printed as an XTerm structure
     */
    void pprint(XTerm p1) {
        print_p(p1, (char) 0);
        gprint("\r\n");
    }

    static int char_no;

    /**
     * Prints the polynomial with a specified marker for formatting.
     *
     * @param p1 the polynomial to be printed as an XTerm structure
     * @param mk the marker character to use during printing
     */
    void print_p(XTerm p1, char mk)
    {
        char_no = 0;
        print_p1(p1, mk);
    }

    /**
     * Helper method that prints the polynomial in detailed format.
     *
     * @param p1 the polynomial to be printed as an XTerm structure
     * @param mk the marker character used for formatting
     */
    void print_p1(XTerm p1, char mk) {
        DTerm dp1;
        XTerm xp1, xp2;

        if (p1 == null)
            gprint("null");
        else if (p1.var == null) {
            num_show(p1.c);
        } else {
            dp1 = p1.ps;
            xp1 = dp1.p;
            if (numberp(xp1)) {
                if (nunitp(xp1)) {
                    gprint("-");
                    char_no++;
                } else if (!unitp(xp1)) {
                    num_show(xp1.c);
                    char_no += num_digs(xp1.c);
                }
            } else if (tlength(xp1) == 1)
                print_p1(xp1, mk);
            else {
                gprint("(");
                print_p1(xp1, mk);
                gprint(")");
                char_no += 2;
            }

            if (dp1.deg > 1) {
                print_var(p1.var, mk);
                gprint("^{" + dp1.deg + "}");
                char_no += 6;
            } else {
                print_var(p1.var, mk);
                char_no += 2;
            }

            for (dp1 = dp1.nx; dp1 != null; dp1 = dp1.nx) {
                if (char_no >= 60) {
                    gprint("\r\n  ");     //add here. new line..
                    char_no = 0;
                }
                xp1 = dp1.p;
                if (numberp(xp1)) {
                    if (unitp(xp1)) {
                        if (dp1.deg == 0) gprint("+1");
                        else gprint("+");
                        char_no++;
                    } else if (nunitp(xp1)) {
                        if (dp1.deg == 0) gprint("-1");
                        else gprint("-");
                        char_no++;
                    } else if (num_posp(xp1.c)) {
                        gprint("+");
                        num_show(xp1.c);
                        char_no += num_digs(xp1.c);
                    } else {
                        num_show(xp1.c);
                        char_no += num_digs(xp1.c);
                    }
                    char_no++;
                } else if (dp1.deg != 0) {
                    if (plength(xp1) == 1) {
                        if (num_negp(lcc(xp1)))
                            print_p1(xp1, mk);
                        else {
                            gprint("+");
                            print_p1(xp1, mk);
                            char_no++;
                        }
                    } else if (tlength(xp1) == 1) {
                        gprint("+");
                        print_p1(xp1, mk);
                    } else {
                        gprint("+(");
                        print_p1(xp1, mk);
                        gprint(")");
                        char_no += 3;
                    }
                } else {
                    xp2 = init(xp1);
                    if (numberp(xp2)) {
                        if (num_negp(xp2.c))
                            print_p1(dp1.p, mk);
                        else {
                            gprint("+");
                            print_p1(dp1.p, mk);
                            char_no++;
                        }
                    } else if (plength(xp2) == 1) {
                        if (num_negp(lcc(xp2)))
                            print_p1(dp1.p, mk);
                        else {
                            gprint("+");
                            print_p1(dp1.p, mk);
                            char_no++;
                        }
                    } else {
                        gprint("+");
                        print_p1(xp1, mk);
                        char_no++;
                    }
                }
                if (dp1.deg > 1) {
                    print_var(p1.var, mk);
                    gprint("^{" + dp1.deg + "}");
                    char_no += 6;
                } else if (dp1.deg == 1) {
                    print_var(p1.var, mk);
                    char_no += 2;
                }
            }
        }
    }


    /**
     * Prints the index information of the given polynomial.
     *
     * @param p the polynomial as an XTerm structure whose index is to be printed
     */
    void print_ind(XTerm p) {
        Var v;
        if (p == null)
            gprint("()");
        else if (pzerop(p)) {
            gprint(Cm.s1993);
            gprint(": (0,0,0) ");
        } else if (p.var == null) {
            gprint(Cm.s1993);
            gprint(": (0,0,1) ");
        } else {
            v = p.var;
            if (v.nm == 0 || v.nm == 99) {
                gprint(Cm.s1993 + ": (" + v.p + "," + ldeg(p) + "," + plength(p));
            } else {
                gprint(Cm.s1993);
                gprint(": (");
                print_var(v, 0);
                gprint(", " + ldeg(p) + "," + plength(p));
            }
        }
    }


    /**
     * Adds three polynomial terms.
     *
     * @param x the first polynomial term
     * @param y the second polynomial term
     * @param z the third polynomial term
     * @return the sum of the three polynomial terms
     */
    XTerm pplus3(XTerm x, XTerm y, XTerm z) {
        return pplus(x, pplus(y, z));
    }

    /**
     * Adds four polynomial terms.
     *
     * @param x the first polynomial term
     * @param y the second polynomial term
     * @param z the third polynomial term
     * @param w the fourth polynomial term
     * @return the sum of the four polynomial terms
     */
    XTerm pplus4(XTerm x, XTerm y, XTerm z, XTerm w) {
        return pplus(pplus(x, y), pplus(z, w));
    }

    static Var svar = new Var();

    /**
     * Compares two Var objects for equality based on their type and contents.
     *
     * @param v1 the first Var object
     * @param v2 the second Var object
     * @return true if the two variables are considered equal, false otherwise
     */
    boolean eq_var(Var v1, Var v2) {
        int i;
        if (v1.nm == 0 || v1.nm == 99) {
            if (!(v2.nm == 0 || v2.nm == 99)) return false;
            return (strcmp(v1.p, v2.p) == 0);
        }
        if (v1.nm == v2.nm) {
            for (i = 0; i < 4; i++) if (v1.pt[i] != v2.pt[i]) return (false);
            return (true);
        } else
            return (false);
    }

    /**
     * Creates a copy of the given Var object.
     *
     * @param v the Var object to copy
     * @return a new Var object that is a copy of v
     */
    Var cp_var(Var v) {
        Var v1;
        int i;
        v1 = new Var();
        v1.nm = v.nm;
        if (v1.nm == 0 || v1.nm == 99)
            for (i = 0; i < 9; i++) v1.p[i] = v.p[i];
        else
            for (i = 0; i < 4; i++) v1.pt[i] = v.pt[i];
        v1.nx = v.nx;
        return (v1);
    }

    /**
     * Retrieves an existing Var object equal to the given one or adds a new one.
     *
     * @param v the Var object to add or look up
     * @return an existing Var equal to v or a new copy if not found
     */
    Var ad_var(Var v) {
        Var v1;
        char i;
        v1 = all_var.nx;
        while ((v1 != null) && !(eq_var(v, v1))) {
            v1 = v1.nx;
        }
        if (v1 == null) {
            v1 = cp_var(v);
            v1.nx = null;
            last_var.nx = v1;
            last_var = v1;
            // v1.setString(this.get_fang_str(v1.p[0],v1.p[1],v1.p[2],v1.p[3]));
        }
        return (v1);
    }

    /**
     * Constructs and adds a new Var object using the given parameters.
     *
     * @param nm the variable identifier
     * @param p1 the first parameter
     * @param p2 the second parameter
     * @param p3 the third parameter
     * @param p4 the fourth parameter
     * @return the resulting Var object
     */
    Var mk_var(int nm, int p1, int p2, int p3, int p4) {
        svar.nm = nm;
        svar.pt[0] = p1;
        svar.pt[1] = p2;
        svar.pt[2] = p3;
        svar.pt[3] = p4;
        return (ad_var(svar));
    }

    /**
     * Creates a new "w" Var object by initializing its parameters to zero
     * and setting its identifier based on the provided number.
     *
     * @param nm the input number determining the variable type
     * @return the newly created Var object
     */
    Var mk_wvar(int nm) {
        for (int i = 0; i < 4; i++) svar.pt[i] = 0;
        if (nm > 0)
            svar.nm = 100;
        else {
            svar.nm = -100;
            nm = -nm;
        }
        svar.pt[0] = nm;
        return (ad_var(svar));
    }

    /**
     * Compares two integer arrays element-wise from index 0 to n.
     *
     * @param a1 the first integer array
     * @param a2 the second integer array
     * @param n the last index (inclusive) to compare
     * @return 1 if the first differing element in a1 is less than in a2, otherwise 0
     */
    int ials(int a1[], int a2[], int n) {
        char i;
        for (i = 0; i <= n; ++i) {
            if (a1[i] < a2[i])
                return (1);
            else if (a1[i] > a2[i]) return (0);
        }
        return (0);
    }

    /**
     * Determines the maximum point value from a Var object's point array
     * based on the variable type.
     *
     * @param v the Var object to evaluate
     * @return the maximum value among the relevant point entries
     */
    int lpt(Var v) {
        int k = v.pt[0];
        if (v.nm == 1) {
            if (v.pt[2] > v.pt[0]) k = v.pt[2];
        } else if (v.nm == 3) {
            if (v.pt[1] > v.pt[0]) k = v.pt[1];
        }
        return (k);
    }

    /**
     * Compares two Var objects for ordering based on type and point values.
     *
     * @param v1 the first Var object
     * @param v2 the second Var object
     * @return true if v1 is considered less than v2, false otherwise
     */
    boolean vless(Var v1, Var v2) {
        int m, l1, l2;

        if ((v1.nm == 0) && (v2.nm == 0)) {
            return (strcmp(v1.p, v2.p) < 0);
        } else if (v1.nm == 0)
            m = 1;
        else if (v2.nm == 0)
            m = 0;

        else if ((v1.nm == -100) && (v2.nm == -100)) {
            return (v1.pt[0] < v2.pt[0]);
        } else if (v1.nm == -100)
            m = 1;
        else if (v2.nm == -100)
            m = 0;

        else if ((v1.nm == 99) && (v2.nm == 99)) {
            return (strcmp(v1.p, v2.p) < 0);
        } else if (v1.nm == 99)
            m = 1;
        else if (v2.nm == 99)
            m = 0;

        else if ((v1.nm == 100) && (v2.nm == 100)) {
            return (v1.pt[0] < v2.pt[0]);
        } else if (v1.nm == 100)
            m = 1;
        else if (v2.nm == 100)
            m = 0;

        else if (v1.nm == 1 && v2.nm == 1) {
            Var vl_k1, vl_k2;
            vl_k1 = new Var();
            vl_k2 = new Var();

            if (v1.pt[0] > v1.pt[2]) {
                vl_k1.pt[0] = v1.pt[0];
                vl_k1.pt[1] = v1.pt[1];
                vl_k1.pt[2] = v1.pt[2];
                vl_k1.pt[3] = v1.pt[3];
            } else {
                vl_k1.pt[0] = v1.pt[2];
                vl_k1.pt[1] = v1.pt[3];
                vl_k1.pt[2] = v1.pt[0];
                vl_k1.pt[3] = v1.pt[1];
            }
            if (v2.pt[0] > v2.pt[2]) {
                vl_k2.pt[0] = v2.pt[0];
                vl_k2.pt[1] = v2.pt[1];
                vl_k2.pt[2] = v2.pt[2];
                vl_k2.pt[3] = v2.pt[3];
            } else {
                vl_k2.pt[0] = v2.pt[2];
                vl_k2.pt[1] = v2.pt[3];
                vl_k2.pt[2] = v2.pt[0];
                vl_k2.pt[3] = v2.pt[1];
            }
            m = ials(vl_k1.pt, vl_k2.pt, 3);
        } else if (v1.nm == v2.nm) {
            l1 = lpt(v1);
            l2 = lpt(v2);
            if (l1 < l2)
                m = 1;
            else if (l1 > l2)
                m = 0;
            else
                m = ials(v1.pt, v2.pt, 3);
        } else if ((v1.nm < 0) && (v2.nm > 0))
            m = 1;
        else if ((v1.nm > 0) && (v2.nm < 0))
            m = 0;
        else {
            l1 = lpt(v1);
            l2 = lpt(v2);
            if (l1 < l2)
                m = 1;
            else if (l1 > l2)
                m = 0;
            else if (v1.nm < v2.nm)
                m = 1;
            else
                m = 0;
        }
        /*  printf("\nvless = (%d)",m);print_var(v1);print_var(v2); */
        return (m != 0);
    }

    /**
     * Extracts a linked list of unique variables present in the given polynomial term.
     *
     * @param p the polynomial term represented as an XTerm
     * @return the head of a linked list of distinct Var objects from p
     */
    XTerm vars_in_p(XTerm p) {
        DTerm ps1;
        XTerm p0, p1, p2;
        if (p == null) return (null);
        if (p.var == null) return (null);

        p0 = get_x();
        p0.var = p.var;
        p0.p = null;
        ps1 = p.ps;
        do {
            p2 = vars_in_p(ps1.p);
            while (p2 != null) {
                p1 = p0;
                while (p1.var != p2.var && p1.p != null) {
                    p1 = p1.p;
                }
                if (p1.var != p2.var) {
                    p1.p = p2;
                    p2 = p2.p;
                    p1 = p1.p;
                    p1.p = null;
                } else {
                    p1 = p2;
                    p2 = p2.p;
                    put_x(p1);
                }
            }
            ps1 = ps1.nx;
        } while (ps1 != null);

        if (print_geo) {
            gprint("\nvars2 of p ");
            pprint(p);
            p1 = p0;
            while (p1 != null) {
                print_var(p1.var, 0);
                p1 = p1.p;
            }
            gprint("end\n");
        }
        return (p0);
    }

    /**
     * Prints the variable using a specific format determined by its type and mode.
     *
     * @param v the Var object to print
     * @param mk the mode flag determining the format
     */
    void print_var(Var v, int mk) {
        switch (v.nm) {
            case 0:
            case 99:
                gprint(new String(v.p));
                break;
            case-1:
            case 1:
                if (mk != 0) {
                    gprint("\\oo{");
                    print_pt(v.pt[1]);
                    print_pt(v.pt[0]);
                    gprint("}{");
                    print_pt(v.pt[3]);
                    print_pt(v.pt[2]);
                    gprint("}");
                } else {
                    print_pt(v.pt[1]);
                    print_pt(v.pt[0]);
                    gprint("/");
                    print_pt(v.pt[3]);
                    print_pt(v.pt[2]);
                }
                break;
            case-2:
            case 2:
                if (pro_type == PRO_VEC) {
                    if (mk != 0) gprint("\\vp{");
                    else gprint("[");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[2]);
                    if (mk != 0) gprint("}{");
                    else gprint(",");
                    print_pt(v.pt[1]);
                    print_pt(v.pt[3]);
                    if (mk != 0) gprint("}");
                    else gprint("]");
                } else if (mk != 0) {
                    gprint("\\t{");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[1]);
                    print_pt(v.pt[2]);
                    if (v.pt[2] != v.pt[3]) print_pt(v.pt[3]);
                    gprint("}");
                } else {
                    gprint("S(");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[1]);
                    print_pt(v.pt[2]);
                    if (v.pt[2] != v.pt[3]) print_pt(v.pt[3]);
                    gprint(")");
                }
                break;
            case-3:
            case 3:
                if (pro_type == PRO_VEC) {
                    if (mk != 0) gprint("\\ip{");
                    else gprint("[");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[2]);
                    if (mk != 0) gprint("}{");
                    else gprint(",");
                    print_pt(v.pt[1]);
                    print_pt(v.pt[3]);
                    if (mk != 0) gprint("}");
                    else gprint("]");
                } else if (mk != 0) {
                    gprint("\\g{");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[1]);
                    if (v.pt[1] != v.pt[2]) print_pt(v.pt[2]);
                    print_pt(v.pt[3]);
                    gprint("}");
                } else {
                    gprint("P(");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[1]);
                    if (v.pt[1] != v.pt[2]) print_pt(v.pt[2]);
                    print_pt(v.pt[3]);
                    gprint(")");
                }
                break;
            case 4:
                if (mk != 0) gprint("\\vec{");
                else gprint("V(");
                if (v.pt[1] == 0)
                    print_pt(v.pt[0]);
                else {
                    print_pt(v.pt[1]);
                    print_pt(v.pt[0]);
                }
                if (mk != 0) gprint("}");
                else gprint(")");
                break;

            case 5:
                if (mk != 0) gprint("(\\o{");
                else gprint("[");
                print_pt(v.pt[0]);
                print_pt(v.pt[1]);
                if (mk != 0) gprint("})");
                else gprint("]");
                break;
            case 6:
                if (mk != 0) gprint("\\");
                gprint("sin(");
                print_pt(v.pt[0]);
                print_pt(v.pt[1]);
                gprint(")");
                break;
            case 7:
                if (mk != 0) gprint("\\");
                gprint("cos(");
                print_pt(v.pt[0]);
                print_pt(v.pt[1]);
                gprint(")");
                break;
            case 16:
                if (mk != 0) gprint("\\");
                gprint("sin(");
                print_pt(v.pt[0]);
                gprint(")");
                break;
            case 17:
                if (mk != 0) gprint("\\");
                gprint("cos(");
                print_pt(v.pt[0]);
                gprint(")");
                break;
            case 10:
                if (mk != 0) {
                    gprint("\\fa{");
                    print_pt(v.pt[0]);
                    print_pt(v.pt[1]);
                    gprint("}{");
                    print_pt(v.pt[2]);
                    print_pt(v.pt[3]);
                    gprint("}");
                } else
                    print_fang(v.pt[0], v.pt[1], v.pt[2], v.pt[3]);
                break;
            case 100:
                gprint("x" + v.pt[0]);
                break;
            case-100:
                gprint("u" + v.pt[0]);
                break;
            case 101:
                gprint("x_{" + v.pt[0] + "}");
                break;
            default:
                gprint("%sx");
                break;
        }
    }

    /**
     * Prints a point identifier using the established naming conventions.
     *
     * @param p the point identifier
     */
    void print_pt(int p) {
        //sprintf(txt,"%s",pt_name(p));
        gprint(pt_name(p));
    }
}
