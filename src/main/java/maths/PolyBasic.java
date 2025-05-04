package maths;


import java.util.Vector;
import java.math.BigInteger;

/**
 * This class provides basic polynomial operations.
 */
public class PolyBasic {
    private static int MAXSTR = 100;
    final private static double ZERO = 10E-6;
    private static PolyBasic basic = new PolyBasic();
    private static boolean BB_STOP = false;
    private static boolean RM_SCOEF = true;

    /**
     * Returns the singleton instance of the PolyBasic class.
     *
     * @return the singleton instance of PolyBasic
     */

    public static PolyBasic getInstance() {
        return basic;
    }

    /**
     * Sets the BB\_STOP flag.
     *
     * @param t the new value for the BB\_STOP flag
     */
    public static void setbbStop(boolean t) {
        BB_STOP = t;
    }

    /**
     * Sets the RM\_SCOEF flag.
     *
     * @param s the new value for the RM\_SCOEF flag
     */
    public static void setRMCOEF(boolean s) {
        RM_SCOEF = s;
    }
/**
     * Adds two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @param es a flag indicating whether to perform exact simplification
     * @return the resulting polynomial after addition
     */
    private TMono pp_plus(TMono p1, TMono p2, boolean es) {

        if (p1 == null) return p2;
        if (p2 == null) return p1;

        TMono poly = null;
        if (p1.x < p2.x || (p1.x == p2.x && p1.deg < p2.deg)) {
            TMono t = p1;
            p1 = p2;
            p2 = t;
        }
        poly = p1;

        if (p1.x > p2.x)//  ||(p1.x == p2.x && p1.deg > p2.deg))     append to the last one.
        {
            while (p1.next != null && p1.deg != 0) p1 = p1.next;

            if (p1.deg != 0) {
                p1.next = new TMono();
                p1.next.x = p1.x;
                p1 = p1.next;
                p1.coef = p2;
                p1.next = null;
            } else {
                p1.coef = pp_plus(p1.coef, p2, true);
                p1.next = null;
            }
            if (p1.coef == null) {
                if (poly == p1)
                    poly = null;
                else {
                    TMono t = poly;
                    while (t != null && t.next != p1)
                        t = t.next;
                    t.next = null;
                }
            }
        } else {
            if (p1.deg > p2.deg) {
                p1.next = pp_plus(p1.next, p2, false);
            } else if ((p1.x == 0)) {
                BigInteger v = p1.val.add(p2.val);
                if (v.compareTo(BigInteger.ZERO) == 0) return null;
                p1.val = v;
            } else {     //p1.deg == p2.deg
                p1.coef = pp_plus(p1.coef, p2.coef, true);
                p1.next = pp_plus(p1.next, p2.next, false);
                if (p1.coef == null) {
                    if (poly == p1)
                        poly = poly.next;
                    else {
                        TMono t = poly;
                        while (t != null && t.next != p1)
                            t = t.next;
                        t.next = p1.next;
                    }
                }

            }
        }


        if (poly != null && poly.coef == null && poly.deg != 0)
            poly = poly.next;

        while (es == true && poly != null && poly.deg == 0 && poly.x != 0)
            poly = poly.coef;

        return (poly);

    }
    /**
     * Subtracts one polynomial from another.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the resulting polynomial after subtraction
     */
    private TMono pp_minus(TMono p1, TMono p2) {
        TMono m = pp_plus(p1, cp_times(-1, p2), true);
        return m;
    }

    /**
         * Multiplies a polynomial by a constant.
         *
         * @param c the constant to multiply by
         * @param p1 the polynomial to be multiplied
         * @return the resulting polynomial after multiplication
         */
    public TMono cp_times(long c, TMono p1) {
        return cp_times(BigInteger.valueOf(c), p1);
    }

    /**
     * Multiplies a polynomial by a constant.
     *
     * @param c the constant to multiply by
     * @param p1 the polynomial to be multiplied
     * @return the resulting polynomial after multiplication
     */
    private TMono cp_times(BigInteger c, TMono p1) {
        if (p1 == null || c.compareTo(BigInteger.ZERO) == 0) return null;
        if (c.compareTo(BigInteger.ONE) == 0) return p1;

        if ((p1.x == 0)) {
            p1.val = p1.val.multiply(c);
            return p1;
        }
        TMono m = p1;
        while (m != null) {
            m.coef = cp_times(c, m.coef);
            m = m.next;
        }
        return p1;
    }

    /**
     * Multiplies two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the resulting polynomial after multiplication
     */
    private TMono pp_times(TMono p1, TMono p2) //(m X n)
    {
        if (p1 == null || p2 == null) return null;
        TMono tp = null;
        if ((p2.x == 0))
            return cp_times(p2.val, p1);

        if ((p1.x == 0))
            return cp_times(p1.val, p2);

        if (p1.x > p2.x) {
            tp = p1;
            p1 = p2;
            p2 = tp;
        }

        TMono poly = null;
        while (p1 != null) {
            tp = p1;
            p1 = p1.next;
            tp.next = null;
            TMono tt;

            if (p1 != null)
                tt = mp_times(tp, p_copy(p2));
            else {
                if (tp.deg == 0 && tp.x != 0)
                    tp = tp.coef;
                tt = mp_times(tp, p2);
            }

            while (tt != null && tt.deg == 0 && tt.x != 0)
                tt = tt.coef;
            while (tt != null && tt.x != 0 && tt.coef == null)
                tt = tt.next;

            poly = pp_plus(poly, tt, true);
        }

        return (poly);
    }

    /**
     * Multiplies two monomials.
     *
     * @param p1 the first monomial
     * @param p2 the second monomial
     * @return the resulting monomial after multiplication
     */
    private TMono mp_times(TMono p1, TMono p2) // p1.x <= p2.x
    {
        TMono poly = p2;

        if (p1 == null || p2 == null) return null;
        if (Int(p1)) return cp_times(p1.val, p2);

        if (p1.x == p2.x) {
            while (p2 != null) {
                p2.deg += p1.deg;
                if (p2.next != null)
                    p2.coef = pp_times(p_copy(p1.coef), p2.coef);
                else
                    p2.coef = pp_times(p1.coef, p2.coef);

                p2 = p2.next;
            }
        } else if (p1.x < p2.x) {
            while (p2 != null) {
                if (p2.next != null)
                    p2.coef = pp_times(p_copy(p1), p2.coef);
                else
                    p2.coef = pp_times(p_copy(p1), p2.coef);
                p2 = p2.next;
            }
        } else {
            System.out.println("Error,must p1.x < p2.x");
        }
        return (poly);
    }

    /**
     * Checks if the given polynomial is zero.
     *
     * @param m the polynomial to check
     * @return true if the polynomial is zero, false otherwise
     */
    public boolean check_zero(TMono m) {
        if (m == null) return false;
        if (m.x == 0 && m.value() == 0) return true;

        while (m != null) {
            if (check_zero(m.coef))
                return true;
            m = m.next;
        }
        return false;
    }

    /**
     * Returns the degree of the given polynomial `p` with respect to the variable `x`.
     *
     * @param p the polynomial to check
     * @param x the variable to check the degree against
     * @return the degree of the polynomial with respect to `x`
     */
    public int deg(TMono p, int x) {
        if (p.x == x) return p.deg;
        if (p.x > x) {
            int d1 = deg(p.coef, x);
            int d2 = deg(p.next, x);
            return Math.max(d1, d2);
        }
        return 0;
    }

    /**
     * Reduces the given polynomial `m` using the provided parameters `p`.
     *
     * @param m the polynomial to be reduced
     * @param p the array of parameters used for reduction
     * @return the reduced polynomial
     */
    public TMono reduce(TMono m, Param[] p) {
        if (m == null)
            return null;

        int x = m.x;
        int d = m.deg;
        m = p_copy(m);

        int n = 0;
        for (; n < p.length; n++) {
            Param pm = p[n];
            if (pm == null)
                break;
            else if (pm.xindex == x) {
                n--;
                break;
            } else if (pm.xindex > x)
                break;
        }

        for (int i = n; i >= 0; i--) {
            Param pm = p[i];
            if (pm != null && pm.m != null)
                m = prem(m, p_copy(pm.m));
        }
        return m;
    }

    /**
     * Simplifies a polynomial to a lower degree without removing coefficients.
     *
     * @param m the polynomial to simplify
     * @param p the array of parameters used for simplification
     * @return the simplified polynomial
     */
    public TMono simplify(TMono m, Param[] p) {
        if (m == null)
            return null;

        int x = m.x;
        int d = m.deg;
        m = p_copy(m);

        int n = 0;
        for (; n < p.length; n++) {
            Param pm = p[n];
            if (pm == null)
                break;
            else if (pm.xindex == x) {
                break;
            } else if (pm.xindex > x)
                break;
        }

        for (int i = n; i >= 0; i--) {
            Param pm = p[i];
            if (pm != null && pm.m != null)
                m = prem(m, p_copy(pm.m));
        }
        return m;
    }

    /**
     * Computes the pseudo-remainder of two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the pseudo-remainder of the two polynomials
     */
    public TMono prem(TMono p1, TMono p2) {
        if (p1 == null)
            return p1;
        if (p2 == null)
            return p1;
        if (p1.x < p2.x)
            return p1;


        TMono result = null;
        if (p1.x == p2.x) {
            TMono m = p_copy(p1.coef);
            TMono n = p_copy(p2.coef);
            result = prem1(p1, p2);
            result = factor_remove(result, m);
            result = factor_remove(result, n);
        } else
            result = prem3(p1, p2);

        coefgcd(result);
        factor1(result);
        return result;
    }

    /**
     * Returns the degree of the polynomial `m` with respect to the variable `x`.
     *
     * @param m the polynomial
     * @param x the variable
     * @return the degree of the polynomial with respect to `x`
     */
    private int degree(TMono m, int x) {
        if (m == null || m.x < x)
            return 0;
        while (m.x > x)
            m = m.coef;
        if (m != null && m.x == x)
            return m.deg;
        return 0;
    }

    /**
     * Checks if the pseudo-remainder of two polynomials can be computed.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return true if the pseudo-remainder can be computed, false otherwise
     */
    private boolean can_prem3(TMono p1, TMono p2) {
        if (p1 == null || p2 == null)
            return false;
        return prem3(p1, p2, false) == null;
    }

    /**
     * Computes the pseudo-remainder of two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the pseudo-remainder of the two polynomials
     */
    private TMono prem3(TMono p1, TMono p2) {
        return prem3(p1, p2, true);
    }

    /**
     * Computes the pseudo-remainder of two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @param r  a flag indicating whether to perform all steps
     * @return the pseudo-remainder of the two polynomials
     */
    private TMono prem3(TMono p1, TMono p2, boolean r) {
        if (p2 == null)
            return p1;
        int x = p2.x;
        if (x == 0)
            return p1;
        TMono[] mm = new TMono[2];
        getm2(p1, p2, mm);
        boolean rx = false;

        while (mm[0] != null && mm[1] != null) {
            if (p1 != null) {
                {
                    TMono t1 = pp_times(p1, mm[0]);
                    TMono t2 = pp_times(p_copy(p2), mm[1]);
                    p1 = pp_minus(t1, t2);
                    coefgcd(p1);
                    if (p1 == null) break;
                }
            }
            if (p1 == null) break;
            mm[0] = mm[1] = null;
            getm2(p1, p2, mm);
            if (!r && mm[1] != null && mm[1].x < p1.x)
                return p1;

        }
        return p1;
    }


    /**
     * Computes the monomials `mm` required for the pseudo-remainder calculation.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @param mm an array to store the resulting monomials
     */
    private void getm2(TMono p1, TMono p2, TMono[] mm) {
        if (p1 == null || p2 == null || p1.x < p2.x || p1.x == p2.x && p1.deg < p2.deg)
            return;

        while (p1 != null) {
            if (p1.x < p2.x || p1.x == p2.x && p1.deg < p2.deg) return;

            if (p1.x == p2.x && p1.deg >= p2.deg) {
                mm[0] = pth(0, 1, 0);
                mm[1] = pth(0, 1, 0);
                get_mm(p1, p2, mm);
                int dd = p1.deg - p2.deg;
                mm[0] = p_copy(p2.coef);
                mm[1] = p_copy(p1.coef);
                if (dd > 0)
                    mm[1] = ptimes(mm[1], pth(p1.x, 1, dd));
                return;
            } else {
                getm2(p1.coef, p2, mm);
                if (mm[0] != null && mm[1] != null) {
                    mm[1] = ptimes(mm[1], pth(p1.x, 1, p1.deg));
                    return;
                }
            }

            p1 = p1.next;
            if (p1 != null && p1.deg == 0)
                p1 = p1.coef;
        }
    }


    /**
     * Computes the pseudo-remainder of two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the pseudo-remainder of the two polynomials
     */
    private TMono prem1(TMono p1, TMono p2) {

        if (p1 == null)
            return null;

        TMono result = null;
        if (p1.x < p2.x)
            result = p1;
        else if (p1.x > p2.x) {
            result = p1;
            while (p1 != null) {
                TMono m1 = prem1(p1.coef, (p2));
                p1.coef = m1;
                p1 = p1.next;
            }
        } else if (p1.x == p2.x) {

            if (p1.deg < p2.deg) {
                TMono t = p1;
                p1 = p_copy(p2);
                p2 = t;
            } else p2 = p_copy(p2);

            if (p1.deg >= p2.deg) {
                int x = p2.x;

                while (p1 != null && p1.x >= x) {
                    int d1 = deg(p1);
                    int d2 = deg(p2);
                    if (d1 < d2) {
                        break;
                    }
                    if (d1 >= d2) {
                        int dd = d1 - d2;
                        TMono tp;
                        if (dd > 0) {
                            tp = pth(x, 1, dd);
                            tp = pp_times(tp, p_copy(p2));
                        } else
                            tp = p_copy(p2);
                        p1 = pp_minus(pp_times(p1.next, tp.coef), pp_times(p1.coef, tp.next));
                        coefgcd(p1);
                        if (p1 == null) break;
                    }
                }
                result = p1;
            }
        }
        while (result != null && result.x != 0 && result.coef == null)
            result = result.next;

        while (result != null && result.deg == 0 && result.x != 0)
            result = result.coef;
        return result;
    }


    /**
     * Divides the degree of the polynomial `m` by `n` for the variable `x`.
     *
     * @param m the polynomial to be modified
     * @param x the variable whose degree is to be divided
     * @param n the divisor for the degree
     */
    public void div_factor1(TMono m, int x, int n) {
        if (x > m.x) return;

        if (x == m.x) {
            while (m != null && m.deg != 0) {
                m.deg -= n;
                if (m.deg < 0) {
                    int k = 0;
                }
                m = m.next;
            }
        } else {
            while (m != null) {
                TMono mx = m.coef;
                if (mx != null && mx.x != 0 && mx.deg != 0) {
                    div_factor1(mx, x, n);
                    if (mx.deg == 0)
                        m.coef = mx.coef;
                }
                m = m.next;
                if (m != null && m.x == 0)
                    m = m.coef;
            }
        }

    }

    /**
     * Factors the polynomial `m1` by dividing it by its common factors.
     *
     * @param m1 the polynomial to be factored
     */
    public void factor1(TMono m1) {
        if (!RM_SCOEF)
            return;

        TMono m = this.get_factor1(m1);
        if (m == null)
            return;
        while (m != null) {
            if (m.x != 0) {
                this.div_factor1(m1, m.x, m.deg);
            }
            m = m.coef;
        }
    }

    /**
     * Gets the common factors of the polynomial `m`.
     *
     * @param m the polynomial to get factors from
     * @return the common factors of the polynomial
     */
    public TMono get_factor1(TMono m) {
        if (m == null)
            return null;

        TMono mx = null;
        TMono m1 = m;
        m = m.coef;
        long n = 0;
        while (m != null) {
            if (m.x != 0)
                n = factor_contain(m.x, m.deg, m1);
            if (n != 0) {
                if (mx == null)
                    mx = new TMono(m.x, 1, (int) n);
                else
                    mx = this.pp_times(mx, new TMono(m.x, 1, (int) n));
            }
            m = m.coef;
        }
        return mx;
    }

    /**
     * Checks if the polynomial `m` contains the factor `(x, d)`.
     *
     * @param x the variable of the factor
     * @param d the degree of the factor
     * @param m the polynomial to check
     * @return true if the polynomial contains the factor, false otherwise
     */
    private boolean m_contain(long x, long d, TMono m) {
        if (m == null || x > m.x || x == m.x && d > m.deg)
            return false;
        if (m.x == x && m.deg <= d)
            return true;

        while (m != null) {
            if (m_contain(x, d, m.coef))
                return true;
            m = m.next;
        }
        return false;
    }

    /**
     * Determines the minimum degree of the factor `(x, n)` contained in the polynomial `m`.
     *
     * @param x the variable of the factor
     * @param n the degree of the factor
     * @param m the polynomial to check
     * @return the minimum degree of the factor contained in the polynomial
     */
    private long factor_contain(long x, long n, TMono m) {
        if (m == null)
            return n;

        if (x > m.x) {
            return 0;
        } else if (x == m.x) {
            while (m.next != null)
                m = m.next;
            if (m.x == 0 && m.coef != null)
                return 0;
            else if (m.deg == 0 && m.coef == null)
                return n;
            else
                return Math.min(m.deg, n);
        } else if (x < m.x) {
            while (m != null) {
                long t = factor_contain(x, n, m.coef);
                if (t == 0)
                    return 0;
                else if (t < n)
                    n = t;
                m = m.next;
            }
        }
        return n;
    }

    /**
     * Removes the common factors of the polynomial `p1` using the polynomial `p2`.
     *
     * @param p1 the polynomial to be factored
     * @param p2 the polynomial used for factoring
     * @return the factored polynomial
     */
    public TMono factor_remove(TMono p1, TMono p2) {  // p1 ,p2 be destryoed.
        if (p1 == null || p2 == null || plength(p1) > 1000)
            return p1;

        if (p1.x == p2.x)
            return p1;

        if (plength(p2) <= 1)
            return p1;

        if (Int(p1) || Int(p2)) return p1;
        coefgcd(p2);
        factor1(p1);
        factor1(p2);
        boolean r = false;
        if (r) {
            print(p1);
            print(p2);
        }

        if (can_prem3(p_copy(p1), p2)) {

            if (CharSet.debug()) {
                System.out.println("p1 can be factored.");
                System.out.print("p1 = ");
                print(p1);
                System.out.print("p2 = ");
                print(p2);
            }
            TMono tp2 = p_copy(p2);
            TMono tp = p_copy(p1);
            TMono m = div((p1), (p2));
            TMono rm = this.pdif(tp, ptimes(tp2, p_copy(m)));
            if (rm != null) {

                if (CharSet.debug()) {
                    System.out.print("***********rm = ");
                    print(rm);
                }
            }
            if (CharSet.debug()) {
                System.out.print("result = ");
                print(m);
            }
            return m;
        }
        return p1;
    }

    /**
     * Divides the polynomial `m` by the polynomial `d`.
     *
     * @param m the dividend polynomial
     * @param d the divisor polynomial
     * @return the quotient polynomial
     */
    TMono div(TMono m, TMono d) {
        if (m == null || d == null)
            return m;
        if (m.x < d.x)
            return null;
        if (m.x == d.x && m.deg < d.deg)
            return null;

        if (m.x == 0 && d.x == 0) {
            BigInteger n = m.val.divide(d.val);
            return pth(0, n, 0);
        }

        TMono result = null;

        if (m.x > d.x) {

            result = null;
            while (m != null) {
                TMono t = div(m.coef, d);
                if (m.deg != 0)
                    t = ptimes(t, pth(m.x, 1, m.deg));
                result = padd(result, t);
                m = m.next;
            }

        } else // m.x == d.x;
        {
            int x = m.x;
            while (m != null) {
                int dd = m.deg - d.deg;
                if (dd < 0)
                    return null;
                TMono m1 = div(m.coef, d.coef);
                if (m1 == null)
                    return null;         //failed.

                m1 = ptimes(m1, pth(x, 1, dd));
                result = padd(result, p_copy(m1));
                TMono mx = d.next;
                if (mx != null && mx.x != 0 && mx.deg == 0)
                    mx = mx.coef;

                m = pdif(m.next, ptimes((m1), p_copy(d.next)));
            }
        }
        while (result != null && result.x != 0 && result.coef == null)
            result = result.next;

        while (result != null && result.deg == 0 && result.x != 0)
            result = result.coef;
        return result;
    }

    /**
     * Creates a copy of the polynomial `p`.
     *
     * @param p the polynomial to copy
     * @return the copied polynomial
     */
    public TMono p_copy(TMono p) {
        if (p == null) return null;

        TMono p1 = new TMono();
        p1.x = p.x;
        p1.deg = p.deg;
        p1.val = p.val;
        p1.coef = p_copy(p.coef);
        p1.next = p_copy(p.next);
        return p1;

    }

    /**
     * Compares two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return -1 if p1 is less than p2, 1 if p1 is greater than p2, 0 if they are equal
     */
    private int pp_compare(TMono p1, TMono p2) {
        if (p1 == null && p2 == null)
            return 0;
        if (p1 == null && p2 != null)
            return -1;
        if (p1 != null && p2 == null)
            return 1;

        if (p1.x < p2.x || (p1.x == p2.x && p1.deg < p2.deg))
            return -1;
        if (p1.x > p2.x || (p1.x == p2.x && p1.deg > p2.deg))
            return 1;
        int c = pp_compare(p1.coef, p2.coef);
        if (c != 0)
            return c;
        return pp_compare(p1.next, p2.next);
    }

    /**
     * Pushes a polynomial into a sorted vector.
     *
     * @param m the polynomial to push
     * @param v the vector to push into
     */
    public void ppush(TMono m, Vector v) {
        if (m == null) return;

        for (int i = 0; i < v.size(); i++) {
            TMono m1 = (TMono) v.get(i);
            int n = (pp_compare2(m, m1));
            if (n > 0) {
                v.add(i, m);
                return;
            }
        }
        v.add(m);
    }

    /**
     * Compares two polynomials for sorting.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return -1 if p1 is less than p2, 1 if p1 is greater than p2, 0 if they are equal
     */
    private int pp_compare2(TMono p1, TMono p2) {
        if (p1 == null && p2 == null)
            return 0;
        if (p1 == null && p2 != null)
            return -1;
        if (p1 != null && p2 == null)
            return 1;
        if (p1.x == 0 && p2.x == 0)
            return 0;

        int x1 = p1.x;
        int x2 = p2.x;

        if (x1 == 0 && x2 != 0)
            return -1;
        if (x1 != 0 && x2 == 0)
            return 1;


        if (p1.x > p2.x || p1.x == p2.x && p1.deg > p2.deg)
            return 1;
        if (p1.x < p2.x || p1.x == p2.x && p1.deg < p2.deg)
            return -1;
        int n = pp_compare2(p1.coef, p2.coef);
        if (n == 0)
            n = pp_compare2(p1.next, p2.next);
        return n;
    }

    /**
     * Checks if the given polynomial is an integer.
     *
     * @param p the polynomial to check
     * @return true if the polynomial is an integer, false otherwise
     */
    private boolean Int(TMono p) {
        if (p == null)
            return false;

        if (p.x == 0)
            return true;
        if (p.deg == 0)
            return Int(p.coef);
        return false;
    }

    /**
     * Prints the given polynomial.
     *
     * @param p the polynomial to print
     */
    public void print(TMono p) {
        if (p == null)
            return;

        int v = this.lv(p);
        int d = this.deg(p);

        System.out.print(String_p_print(p, false, true, true));

        System.out.print("\n");
    }

    /**
     * Prints the polynomial in a simplified format.
     *
     * @param p the polynomial to print
     */
    public void sprint(TMono p) {
        p_print(p, false, true);
    }


    /**
     * Prints the polynomial in a specified format.
     *
     * @param p the polynomial to print
     * @param ce a flag indicating whether to enclose the polynomial in parentheses
     * @param first a flag indicating whether this is the first polynomial in a sequence
     */
    private void p_print(TMono p, boolean ce, boolean first) {
        if (p == null) return;
        if (p.next == null) ce = false;

        if (ce) {
            if (first)
                System.out.print("(");
            else
                System.out.print(" + (");
            m_print(p, true);
            p = p.next;
            while (p != null) {
                m_print(p, false);
                p = p.next;
            }
            System.out.print(")");
        } else if (!first) {
            while (p != null) {
                m_print(p, false);
                p = p.next;
            }
        } else {
            m_print(p, true);
            p = p.next;
            while (p != null) {
                m_print(p, false);
                p = p.next;
            }
        }
    }

    /**
     * Prints a monomial in a polynomial.
     *
     * @param p the monomial to print
     * @param first a flag indicating whether this is the first monomial in the polynomial
     */
    private void m_print(TMono p, boolean first) {
        if (p.x == 0) {
            if (first != true) {
                if (p.value() > 0)
                    System.out.print(" + ");
                else
                    System.out.print(" - ");
                long t = Math.abs(p.value());
                if (t != 1)
                    System.out.print(t);
            } else {
                if (p.value() != 1)
                    System.out.print(p.value());
            }
        } else if (p.deg == 0)
            p_print(p.coef, false, first);
        else {
            p_print(p.coef, true, first);
            if (p.coef == null)
                System.out.print("0");
            if (p.x >= 0) {
                if (p.deg != 1)
                    System.out.print("x" + p.x + "^" + p.deg);
                else
                    System.out.print("x" + p.x);
            } else {
                if (p.deg != 1)
                    System.out.print("u" + (-p.x) + "^" + p.deg);
                else
                    System.out.print("u" + (-p.x));

            }
        }
    }


    /**
     * Creates a new monomial with the specified variable, coefficient, and degree.
     *
     * @param x the variable of the monomial
     * @param c the coefficient of the monomial
     * @param d the degree of the monomial
     * @return the created monomial
     */
    public TMono pth(int x, int c, int d) {
        return new TMono(x, c, d);
    }

    /**
     * Creates a new monomial with the specified variable, BigInteger coefficient, and degree.
     *
     * @param x the variable of the monomial
     * @param c the BigInteger coefficient of the monomial
     * @param d the degree of the monomial
     * @return the created monomial
     */
    public TMono pth(int x, BigInteger c, int d) {
        return new TMono(x, c, d);
    }

    /**
     * Returns the degree of the given polynomial.
     *
     * @param p the polynomial
     * @return the degree of the polynomial
     */
    public int deg(TMono p) {
        if (p == null) {
            int k = 0;
        }
        return p.deg;
    }

    /**
     * Returns the leading variable of the given polynomial.
     *
     * @param p the polynomial
     * @return the leading variable of the polynomial
     */
    public int lv(TMono p) {
        if (p == null) return 0;
        return p.x;
    }

    /**
     * Returns a zero polynomial.
     *
     * @return a zero polynomial
     */
    public TMono pzero() {
        return null;
    }

    /**
     * Returns the length of the given polynomial.
     *
     * @param m the polynomial
     * @return the length of the polynomial
     */
    public int plength(TMono m) {
        if (m == null) return 0;

        if (Int(m))
            return 1;
        else {
            return plength(m.coef) + plength(m.next);
        }
    }

    /**
     * Checks if the given polynomial is zero.
     *
     * @param m the polynomial to check
     * @return true if the polynomial is zero, false otherwise
     */
    public boolean pzerop(TMono m) {
        if (m == null) return true;

        if (Int(m))
            return m.value() == 0;
        return pzerop(m.coef) && pzerop(m.next);
    }

    /**
     * Adds a monomial to a polynomial.
     *
     * @param t the monomial to add
     * @param p the polynomial to add to
     * @return the resulting polynomial after addition
     */
    TPoly addpoly(TMono t, TPoly p) {
        TPoly poly = new TPoly();
        poly.setNext(p);
        poly.setPoly(t);

        return poly;
    }

    /**
     * Pushes a polynomial into a sorted linked list.
     *
     * @param t the polynomial to push
     * @param pp the linked list to push into
     * @return the updated linked list with the polynomial added
     */
    public TPoly ppush(TMono t, TPoly pp) {
        if (t == null)
            return pp;

        int vra = this.lv(t);
        TPoly poly = new TPoly();
        poly.next = null;
        poly.poly = t;

        if (pp == null)
            return poly;

        TPoly former = null;
        TPoly p = pp;

        while (p != null) {
            int lee = this.lv(p.getPoly());
            if (lee > vra) {
                former = p;
                p = p.getNext();
            } else
                break;
        }
        if (p == null || this.lv(p.getPoly()) < vra) {
            poly.setNext(p);
            if (former == null)
                return poly;
            else {
                former.setNext(poly);
                return pp;
            }
        }
        //else ==
        while (p != null) {
            if (pp_compare(p.getPoly(), poly.getPoly()) < 0) {
                if (former == null) {
                    poly.setNext(p);
                    return poly;
                } else {
                    former.setNext(poly);
                    poly.setNext(p);
                    return pp;
                }
            } else {
                former = p;
                p = p.getNext();
            }
        }
        if (former == null) {
            poly.setNext(p);
            return poly;
        }
        former.setNext(poly);
        return pp;
    }

    /**
     * Calculates the value of a polynomial given the polynomial and parameters.
     *
     * @param m the polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @return the calculated value of the polynomial
     */
    double calpoly(TMono m, Param[] p) {
        if (m == null || p == null)
            return 0.0;

        if (Int(m))
            return (double) m.value();
        double r = 0.0;

        while (m != null) {
            double v = calpoly(m.coef, p);
            int id = m.x - 1;
            if (id < 0 || id >= p.length || p[m.x - 1] == null) return 0.0;
            r += Math.pow(p[m.x - 1].value, m.deg) * v;
            m = m.next;
        }
        return r;
    }


    /**
     * Calculates the values of the polynomial `mm` given the parameters `p`.
     *
     * @param mm the polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @return an array of calculated values of the polynomial
     */
    public double[] calculv(TMono mm, Param[] p) {
        int x, d;
        double[] result = null;
        if (mm == null)
            return result;

        x = lv(mm);
        d = deg(mm, x);


        if (d == 1) {
            double val = calpoly(mm.coef, p);
            if (ZERO(val))
                return new double[0];

            result = new double[1];
            double v1 = calpoly(mm.next, p);
            val = (-1) * v1 / val;
            result[0] = val;
            return result;
        } else if (d == 2) {

            TMono a1 = mm.coef;
            TMono b1;
            mm = mm.next;
            if (mm != null && deg(mm) == 1) {
                b1 = mm.coef;
                mm = mm.next;
            } else
                b1 = null;

            TMono b2;
            if (mm != null && deg(mm) == 0)
                b2 = mm.coef;
            else
                b2 = null;

            double aa = calpoly(a1, p);
            double bb1 = calpoly(b1, p);
            double bb2 = calpoly(b2, p);
            if (ZERO(aa))
                return new double[0];

            return poly_solve_quadratic(aa, bb1, bb2);

        } else if (d == 3 || d == 4) {
            TMono a1, b1, c1, d1, e1;
            a1 = b1 = c1 = d1 = e1 = null;
            while (mm != null) {
                switch (deg(mm)) {
                    case 0:
                        d1 = mm.coef;
                        break;
                    case 1:
                        c1 = mm.coef;
                        break;
                    case 2:
                        b1 = mm.coef;
                        break;
                    case 3:
                        a1 = mm.coef;
                        break;
                    case 4:
                        e1 = mm.coef;
                        break;
                    default:
                        return null;
                }
                mm = mm.next;
            }
            double aa = calpoly(a1, p);
            double bb = calpoly(b1, p);
            double cc = calpoly(c1, p);
            double dd = calpoly(d1, p);
            double ee = calpoly(e1, p);
            double[] r = null;

            if (d == 3 && aa != 0.0)
                r = poly_solve_cubic(1, bb / aa, cc / aa, dd / aa);
            else if (d == 4 && ee != 0)
                r = poly_solve_quartic(aa / ee, bb / ee, cc / ee, dd / ee);
            return r;

        }
        return null;
    }

    /**
     * Calculates the values of the polynomial `mm` given the parameters `p` for two variables.
     *
     * @param mm the polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @return an array of calculated values of the polynomial
     */
    public double[] calculv_2v(TMono mm, Param[] p) {
        if (mm.next != null)
            return this.calculv(mm.next.coef, p);
        else
            return null;

    }

    /**
     * Calculates the values of the polynomial `mm` given the parameters `p` for two variables.
     *
     * @param mm the polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @param dx the first variable index
     * @param dy the second variable index
     * @return an array of calculated values of the polynomial
     */
    public double[] calculate_online(TMono mm, Param[] p, int dx, int dy) {
        if (mm.deg != 1 && mm.x != dy) return null;

        double a = calpoly(mm.coef, p);
        double c = 0.0;
        double b = 0.0;

        if (mm.deg != 1 && mm.x != dx) return null;


        if (mm.next != null) {
            TMono m1 = mm.next.coef;
            if (m1.x != dx)
                return null;

            b = calpoly(m1.coef, p);
            if (m1.next != null) {
                m1 = m1.next.coef;
                c = calpoly(m1, p);
            }
        } else
            return null;

        double md = b * b + a * a;
        double x = p[dx - 1].value;
        double y = p[dy - 1].value;

        if (Math.abs(md) < ZERO)
            return null;
        double[] result = new double[2];

        result[0] = (a * a * x - a * b * y - b * c) / md;
        result[1] = (b * b * y - a * b * x - a * c) / md;
        return result;
    }

    /**
     * Calculates the values of the polynomial `mm` given the parameters `p` for two variables.
     *
     * @param mm the polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @param dx the first variable index
     * @param dy the second variable index
     * @return an array of calculated values of the polynomial
     */
    public double[] calculate_oncr(TMono mm, Param[] p, int dx, int dy) {

        if (mm.deg != 2 && mm.x != dy) return null;
        double b2 = calpoly(mm.coef, p);
        mm = mm.next;
        double b1 = 0.0;
        if (mm != null && mm.deg == 1 && mm.x == dy) {
            b1 = calpoly(mm.coef, p);
            mm = mm.next;
        }
        if (mm == null)
            return null;

        mm = mm.coef;
        if (mm.deg != 2 && mm.x != dx) return null;
        double a2 = calpoly(mm.coef, p);
        if (Math.abs(a2) < ZERO || Math.abs(b2) < ZERO || Math.abs(a2 - b2) > ZERO) return null;

        mm = mm.next;
        double a1 = 0.0;
        if (mm != null && mm.deg == 1 && mm.x == dx) {
            a1 = calpoly(mm.coef, p);
            mm = mm.next;
        }
        double c = 0.0;
        if (mm != null) {
            mm = mm.coef;
            c = calpoly(mm, p);
        }

        double a = a1 / a2;
        double b = b1 / a2;
        c = c / a2;
        double x = p[dx - 1].value;
        double y = p[dy - 1].value;

        double yd = y + b / 2;
        double xd = x + a / 2;
        double r = Math.sqrt(a * a / 4 + b * b / 4 - c);
        double ln = Math.sqrt(xd * xd + yd * yd);
        double[] result = new double[2];
        result[0] = -a / 2 + xd * r / ln;
        result[1] = -b / 2 + yd * r / ln;
        return result;
    }

    /**
     * Calculates the values of two polynomials `mm1` and `mm2` given the parameters `p`.
     *
     * @param mm1 the first polynomial to calculate
     * @param mm2 the second polynomial to calculate
     * @param p the array of parameters used in the calculation
     * @return an array of calculated values of the polynomials
     */
    public double[] calculv2poly(TMono mm1, TMono mm2, Param[] p)
    {
        int x, d;
        double[] result;

        TMono a1, b1, c1, m1;

        x = lv(mm1);
        if (deg(mm1, x) < deg(mm2, x)) {
            TMono m = mm1;
            mm1 = mm2;
            mm2 = m;
        }

        m1 = mm1;
        d = deg(m1, x);

        if (d == 2) {
            a1 = m1.coef;
            m1 = m1.next;
            if (deg(m1) == 1) {
                b1 = m1.coef;
                m1 = m1.next;
            } else
                b1 = null;

            if (m1 != null)
                c1 = m1.coef;
            else
                c1 = null;


            double ra1 = calpoly(a1, p);
            double rb1 = calpoly(b1, p);
            double rc1 = calpoly(c1, p);
            double dl = rb1 * rb1 - 4 * ra1 * rc1;


            if (Math.abs(dl) < ZERO) {
                result = new double[1];
                result[0] = ((-1) * rb1) / (2 * ra1);
                return result;
            }
            if (dl < 0) return null;
            dl = Math.sqrt(dl);
            if (Math.abs(ra1) < ZERO) return null;

            result = new double[2];
            result[0] = ((-1) * rb1 + dl) / (2 * ra1);
            result[1] = ((-1) * rb1 - dl) / (2 * ra1);
            return result;
        }


        result = calculv(mm1, p);
        if (result == null || result.length == 0)
            result = calculv(mm2, p);
        if (result == null || result.length == 0) {
            return null;
        }
        return result;


    }

    /**
     * Multiplies two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the product of the two polynomials
     */
    public TMono pRtimes(TMono p1, TMono p2) {
        return pp_times(p1, p2);
    }

    /**
     * Adds two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the sum of the two polynomials
     */
    public TMono padd(TMono p1, TMono p2) { //add
        TMono m = (pp_plus(p1, p2, true));
        return m;
    }

    /**
     * Subtracts the second polynomial from the first polynomial.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the difference of the two polynomials
     */
    public TMono pdif(TMono p1, TMono p2) {//minus
        TMono m = (pp_minus(p1, p2));
        return m;

    }

    /**
     * Creates a copy of the given polynomial.
     *
     * @param p the polynomial to copy
     * @return the copied polynomial
     */
    public TMono pcopy(TMono p) {
        return p_copy(p);
    }

    /**
     * Multiplies two polynomials.
     *
     * @param p1 the first polynomial
     * @param p2 the second polynomial
     * @return the product of the two polynomials
     */
    public TMono ptimes(TMono p1, TMono p2) {
        return pp_times(p1, p2);
    }

    /**
     * Multiplies a polynomial by a constant.
     *
     * @param p the polynomial
     * @param c the constant
     * @return the product of the polynomial and the constant
     */
    public TMono pctimes(TMono p, long c) {
        return cp_times(BigInteger.valueOf(c), p);
    }

    /**
     * Prints the given polynomial.
     *
     * @param m the polynomial to print
     */
    public void printpoly(TMono m) {
        print(m);
    }


    /**
     * Gets the monomial with the minimum degree for the given variable in the polynomial.
     *
     * @param x the variable
     * @param p the polynomial
     * @return the monomial with the minimum degree for the given variable
     */
    TMono getMinV(int x, TPoly p) {
        TMono poly = null;
        int exp = 0;
        while (p != null) {
            TMono m = p.getPoly();
            if (m == null || m.x != x) {
                p = p.getNext();
                continue;
            }

            int e = m.deg;
            if ((e > 0) && ((exp == 0) || (e < exp))) {
                exp = e;
                poly = p.getPoly();
            }
            p = p.getNext();
        }
        return poly;

    }

    /**
     * Returns the head of the polynomial as a string.
     *
     * @param m the polynomial
     * @return the head of the polynomial as a string
     */
    public String printHead(TMono m) {
        if (m == null)
            return "0";
        int v = this.lv(m);
        int d = this.deg(m);
        if (d != 1)
            return ("x" + v + "^" + d);
        else
            return "x" + v;
    }

    /**
     * Returns a simplified string representation of the polynomial.
     *
     * @param m the polynomial
     * @return the simplified string representation of the polynomial
     */
    public String printSPoly(TMono m) {
        return printSPoly(m, MAXSTR);
    }

    /**
     * Returns a string representation of the polynomial with a maximum length.
     *
     * @param m the polynomial
     * @return the string representation of the polynomial with a maximum length
     */
    public String printNPoly(TMono m) {
        if (m == null)
            return "";

        String s = String_p_print(m, false, true, true);
        if (s.length() > MAXSTR)
            return s.substring(0, MAXSTR) + ".... != 0";
        else s += " !=0";
        return s;
    }

    /**
     * Returns a string representation of two polynomials with a maximum length.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the string representation of the two polynomials with a maximum length
     */
    public String printNPoly(TMono m1, TMono m2) {
        int n1 = plength(m1);
        int n2 = plength(m2);
        String s1 = String_p_print(m1, false, true, true);
        String s2 = String_p_print(m2, false, true, true);
        if (n1 > 1)
            s1 = "(" + s1 + ")";
        if (n2 > 1)
            s2 = " (" + s2 + ")";
        return s1 + s2 + " != 0";
    }

    /**
     * Returns a simplified string representation of the polynomial with a specified maximum length.
     *
     * @param m the polynomial
     * @param n the maximum length of the string representation
     * @return the simplified string representation of the polynomial with a specified maximum length
     */
    public String printSPoly(TMono m, int n) {
        if (m == null)
            return "0";

        String s = String_p_print(m, false, true, true);
        if (s.length() > n)
            return s.substring(0, n) + ".... = 0";
        else s += " =0";
        return s;
    }

    /**
     * Returns a string representation of the polynomial with a maximum length.
     *
     * @param m the polynomial
     * @return the string representation of the polynomial with a maximum length
     */
    public String printMaxstrPoly(TMono m) {
        int n = MAXSTR;
        if (m == null)
            return "0";

        String s = String_p_print(m, false, true, true);
        if (s.length() > n)
            return s.substring(0, n) + "....0";
        return s;
    }

    /**
     * Returns a string representation of the polynomial.
     *
     * @param p the polynomial
     * @param ce a flag indicating whether to enclose the polynomial in parentheses
     * @param first a flag indicating whether this is the first polynomial in a sequence
     * @param nn a flag indicating whether to include the leading coefficient
     * @return the string representation of the polynomial
     */
    public String String_p_print(TMono p, boolean ce, boolean first, boolean nn) {
        if (p == null) return "";
        if (p.next == null) ce = false;
        String s = "";

        if (ce) {
            if (first)
                s += ("(");
            else
                s += (" + (");
            s += (String_m_print(p, true, nn));
            p = p.next;
            while (p != null) {
                if (s.length() > MAXSTR)
                    return s;
                s += String_m_print(p, false, true);
                p = p.next;
            }
            s += (")");
        } else if (!first) {
            while (p != null) {
                if (s.length() > MAXSTR)
                    return s;
                s += String_m_print(p, false, nn);
                p = p.next;
            }
        } else {
            s += String_m_print(p, true, nn);
            p = p.next;
            while (p != null) {
                if (s.length() > MAXSTR)
                    return s;
                s += String_m_print(p, false, nn);
                p = p.next;
            }
        }
        return s;
    }

    /**
     * Returns a string representation of the polynomial with expanded format.
     *
     * @param p the polynomial
     * @return the string representation of the polynomial with expanded format
     */
    public String getExpandedPrint(TMono p) {
        String r = ep_print(p, "", true);
        if (r != null && (r.endsWith("-") || r.endsWith("+")))
            return r + "1";
        return r;
    }

    /**
     * Returns a string representation of the polynomial with expanded format.
     *
     * @param p the polynomial
     * @param s the string to append to
     * @param f a flag indicating whether this is the first polynomial in a sequence
     * @return the string representation of the polynomial with expanded format
     */
    private String ep_print(TMono p, String s, boolean f) {
        String st = "";
        while (p != null) {
            if (p.next == null && p.deg == 0 && p.x != 0)
                p = p.coef;
            if (p == null)
                break;

            st += eprint(p, s, f);
            f = false;
            if (p.next == null && p.deg == 0)
                p = p.coef;
            else
                p = p.next;
        }
        return st;
    }

    /**
     * Returns a string representation of the polynomial with expanded format.
     *
     * @param p the polynomial
     * @param s the string to append to
     * @param f a flag indicating whether this is the first polynomial in a sequence
     * @return the string representation of the polynomial with expanded format
     */
    private String eprint(TMono p, String s, boolean f) {
        if (p == null)
            return "";
        if (p.x == 0) // int value;
        {
            if (p.value() == 1) {
                if (f)
                    return s;
                else return "+" + s;
            } else if (p.value() == -1)
                return "-" + s;
            else {
                if (f || p.value() < 0)
                    return p.value() + "*" + s;
                else
                    return "+" + p.value() + "*" + s;
            }

        } else if (p.deg == 0) {
            return ep_print(p.coef, s, f);
        } else {
            String n = "";
            if (p.x > 0) {
                if (p.deg > 1)
                    n = "x" + p.x + "^" + p.deg;
                else
                    n = "x" + p.x;
            } else if (p.x < 0) {
                if (p.deg > 1)
                    n = "u" + (-p.x) + "^" + p.deg;
                else
                    n = "u" + (-p.x);
            }
            if (s.length() == 0)
                s = n;
            else s = n + "*" + s;
            return ep_print(p.coef, s, f);
        }
    }

    /**
     * Returns a string representation of all printed monomials in the polynomial.
     *
     * @param p the polynomial
     * @param b a flag indicating whether to include the leading coefficient
     * @return the string representation of all printed monomials in the polynomial
     */
    public String getAllPrinted(TMono p, boolean b) {
        int n = MAXSTR;
        MAXSTR = 1000000;
        String s = "";
        boolean f = true;
        while (p != null) {
            if (p.deg != 0) {
                if (f)
                    s += String_m_print(p, f, true);
                else {
                    if (b)
                        s += "\n" + String_m_print(p, f, true);
                    else
                        s += String_m_print(p, f, true);
                }
            }
            if (f)
                f = false;

            if (p.next == null && p.deg == 0)
                p = p.coef;
            else
                p = p.next;
        }
        MAXSTR = n;
        if (b)
            return s + "\n = 0";
        else return s;
    }

    /**
     * Returns a string representation of all printed monomials in the polynomial.
     *
     * @param p the polynomial
     * @return the string representation of all printed monomials in the polynomial
     */
    public String getAllPrinted(TMono p) {
        return getAllPrinted(p, true);
    }


    /**
     * Returns a string representation of the monomial.
     *
     * @param p the monomial
     * @param first a flag indicating whether this is the first monomial in the polynomial
     * @param nn a flag indicating whether to include the leading coefficient
     * @return the string representation of the monomial
     */
    private String String_m_print(TMono p, boolean first, boolean nn) {


        String s = new String();

        if (p.x == 0) {
            if (nn) {
                long t = p.value();
                if (t > 0)
                    s += "+" + t;
                else s += t;
            } else if (first != true) {
                if (p.value() > 0)
                    s += (" + ");
                else
                    s += (" - ");
                long t = Math.abs(p.value());
                if (t != 1)
                    s += (t);
            } else {
                long t = p.value();
                if (t == -1)
                    s += "-";
                else if (t != 1)
                    s += (t);
            }
        } else if (p.deg == 0) {
            s += String_p_print(p.coef, false, first, nn);
        } else {
            s += String_p_print(p.coef, true, first, false);
            if (p.x >= 0) {
                if (p.deg != 1)
                    s += ("x" + p.x + "^" + p.deg);
                else
                    s += ("x" + p.x);
            } else {
                if (p.deg != 1)
                    s += ("u" + (-p.x) + "^" + p.deg);
                else
                    s += ("u" + (-p.x));
            }
        }
        return s;
    }

    /**
     * Returns the greatest common divisor of the coefficients of the polynomial.
     *
     * @param p the polynomial
     * @return the greatest common divisor of the coefficients of the polynomial
     */
    public BigInteger coefgcd(TMono p) {
        if (p == null) return BigInteger.ONE;

        BigInteger c = coefgcd(p, BigInteger.ZERO);

        TMono m = p;
        while (m != null && m.x != 0)
            m = m.coef;
        if (m == null)
            return c;

        if (m.val.compareTo(BigInteger.ZERO) < 0)
            c = c.negate();

        if (c.compareTo(BigInteger.ONE) != 0)
            coef_div(p, c);
        return c;
    }

    /**
     * Divides the coefficients of the polynomial by the given constant.
     *
     * @param m the polynomial
     * @param c the constant to divide by
     * @return true if successful, false otherwise
     */
    private boolean coef_div(TMono m, BigInteger c) {
        if (m == null) return true;
        if (m.x == 0) {
            m.val = m.val.divide(c);
            return true;
        } else {
            if (coef_div(m.coef, c))
                return coef_div(m.next, c);
            return false;
        }

    }

    /**
     * Returns the greatest common divisor of two BigInteger values.
     *
     * @param a the first BigInteger
     * @param b the second BigInteger
     * @return the greatest common divisor of the two BigInteger values
     */
    BigInteger gcd(BigInteger a, BigInteger b) {
        return a.gcd(b);
    }

    /**
     * Returns the greatest common divisor of the coefficients of the polynomial.
     *
     * @param p the polynomial
     * @param c the constant to divide by
     * @return the greatest common divisor of the coefficients of the polynomial
     */
    private BigInteger coefgcd(TMono p, BigInteger c) {

        if (p == null) return c;
        if (c.compareTo(BigInteger.ONE) == 0) return c;

        while (p != null) {
            if ((p.x == 0)) {
                if (c.compareTo(BigInteger.ZERO) == 0)
                    c = p.val;
                else c = gcd(c, p.val);
            } else {
                BigInteger cc = coefgcd(p.coef, c);
                c = gcd(c, cc);
            }
            if (c.compareTo(BigInteger.ONE) == 0) return c;

            if (p.x != 0 && p.deg == 0)
                p = p.coef;
            else
                p = p.next;
        }
        return c;
    }

    /**
     * Checks if the given value is close to zero.
     *
     * @param r the value to check
     * @return true if the value is close to zero, false otherwise
     */
    private boolean ZERO(double r) {
        return Math.abs(r) < ZERO;
    }

    /**
     * Solves a quadratic polynomial equation.
     *
     * @param aa the coefficient of x^2
     * @param bb1 the coefficient of x
     * @param bb2 the constant term
     * @return an array of solutions to the equation
     */
    private double[] poly_solve_quadratic(double aa, double bb1, double bb2) {

        double[] result;
        double mo = Math.pow(Math.abs(aa * bb1 * bb2), 1.0 / 3);
        if (ZERO(mo))
            mo = 1.0;
        aa = aa / mo;
        bb1 = bb1 / mo;
        bb2 = bb2 / mo;

        double dl = (bb1 * bb1 - 4 * aa * bb2);


        double tdl = dl;/// (mo * mo);

        if (Math.abs(tdl) < ZERO) {
            result = new double[1];
            result[0] = ((-1) * bb1) / (2 * aa);
            return result;
        }

        if (dl < 0) return null;
        dl = Math.sqrt(dl);
        result = new double[2];

        double x1 = ((-1) * bb1 + dl) / (2 * aa);
        double x2 = ((-1) * bb1 - dl) / (2 * aa);

        result[0] = x1;
        result[1] = x2;
        return result;
    }

    /**
     * Solves a cubic polynomial equation.
     *
     * @param a the coefficient of x^3
     * @param b the coefficient of x^2
     * @param c the coefficient of x
     * @param d the constant term
     * @return an array of solutions to the equation
     */
    double[] poly_solve_cubic(double a, double b, double c, double d) {
        double p = (3 * c / a - (b * b / (a * a))) / 3;
        double q = (2 * Math.pow(b / a, 3) - 9 * b * c / a / a + 27 * d / a) / 27;

        double D = Math.pow(p / 3, 3) + Math.pow(q / 2, 2);

        if (D >= 0) {
            double u = cubic_root(-q / 2 + Math.sqrt(D));
            double v = cubic_root(-q / 2 - Math.sqrt(D));
            double y1 = u + v;
//            double y2 = -(u + v) / 2 + i(u - v) * sqrt(3) / 2
//            double y3 = -(u + v) / 2 - i(u - v) * sqrt(3) / 2
            double[] r = new double[1];
            r[0] = y1 - b / a / 3;
            return r;

        } else if (D < 0) {
            p = Math.abs(p);
            double phi = Math.acos(-q / 2 / Math.sqrt(p * p * p / 27));
            double pi = Math.PI;
            double y1 = 2 * Math.sqrt(p / 3) * Math.cos(phi / 3);
            double y2 = -2 * Math.sqrt(p / 3) * Math.cos((phi + pi) / 3);
            double y3 = -2 * Math.sqrt(p / 3) * Math.cos((phi - pi) / 3);
//            x = y - b / a / 3
            double t = b / a / 3;
            double[] r = new double[3];
            r[0] = y1 - t;
            r[1] = y2 - t;
            r[2] = y3 - t;
            return r;
        }
        return null;
    }

    /**
     * Calculates the cubic root of a given value.
     *
     * @param r the value to calculate the cubic root of
     * @return the cubic root of the value
     */
    private double cubic_root(double r) {
        double r1 = Math.pow(Math.abs(r), 1.0 / 3.0);
        if (r < 0)
            r1 = -r1;
        return r1;
    }

    /**
     * Solves a quartic polynomial equation.
     *
     * @param a the coefficient of x^4
     * @param b the coefficient of x^3
     * @param c the coefficient of x^2
     * @param d the coefficient of x
     * @return an array of solutions to the equation
     */
    double[] poly_solve_quartic(double a, double b, double c, double d) {
        /*
         * This code is based on a simplification of
         * the algorithm from zsolve_quartic.c for real roots
         */
        double aa, pp, qq, rr, rc, sc, tc, mt, x1, x2, x3, x4;
        double w1r, w1i, w2r, w2i, w3r;
        double v1, v2, arg;
        double disc, h;
        int k1, k2;
        double[] u, v, zarr;
        u = new double[3];
        v = new double[3];
        zarr = new double[4];
        /////////////////////////////////////

        ////////////////////////////////

        k1 = k2 = 0;
        aa = a * a;
        pp = b - (3.0 / 8.0) * aa;
        qq = c - (1.0 / 2.0) * a * (b - (1.0 / 4.0) * aa);
        rr = d - (1.0 / 4.0) * (a * c - (1.0 / 4.0) * aa * (b - (3.0 / 16.0) * aa));
        rc = (1.0 / 2.0) * pp;
        sc = (1.0 / 4.0) * ((1.0 / 4.0) * pp * pp - rr);
        tc = -((1.0 / 8.0) * qq * (1.0 / 8.0) * qq);

        /* This code solves the resolvent cubic in a convenient fashion
         * for this implementation of the quartic. If there are three real
         * roots, then they are placed directly into u[].  If two are
         * complex, then the real root is put into u[0] and the real
         * and imaginary part of the complex roots are placed into
         * u[1] and u[2], respectively. Additionally, this
         * calculates the discriminant of the cubic and puts it into the
         * variable disc. */
        {
            double qcub = (rc * rc - 3 * sc);
            double rcub = (2 * rc * rc * rc - 9 * rc * sc + 27 * tc);

            double Q = qcub / 9;
            double R = rcub / 54;

            double Q3 = Q * Q * Q;
            double R2 = R * R;

            double CR2 = 729 * rcub * rcub;
            double CQ3 = 2916 * qcub * qcub * qcub;

            disc = (CR2 - CQ3) / 2125764.0;


            if (0 == R && 0 == Q) {
                u[0] = -rc / 3;
                u[1] = -rc / 3;
                u[2] = -rc / 3;
            } else if (CR2 == CQ3) {
                double sqrtQ = Math.sqrt(Q);
                if (R > 0) {
                    u[0] = -2 * sqrtQ - rc / 3;
                    u[1] = sqrtQ - rc / 3;
                    u[2] = sqrtQ - rc / 3;
                } else {
                    u[0] = -sqrtQ - rc / 3;
                    u[1] = -sqrtQ - rc / 3;
                    u[2] = 2 * sqrtQ - rc / 3;
                }
            } else if (CR2 < CQ3) {
                double sqrtQ = Math.sqrt(Q);
                double sqrtQ3 = sqrtQ * sqrtQ * sqrtQ;
                double theta = Math.acos(R / sqrtQ3);
                if (R / sqrtQ3 >= 1.0) theta = 0.0;
                {
                    double norm = -2 * sqrtQ;

                    u[0] = norm * Math.cos(theta / 3) - rc / 3;
                    u[1] = norm * Math.cos((theta + 2.0 * Math.PI) / 3) - rc / 3;
                    u[2] = norm * Math.cos((theta - 2.0 * Math.PI) / 3) - rc / 3;
                }
            } else {
                double sgnR = (R >= 0 ? 1 : -1);
                double modR = Math.abs(R);
                double x = R2 - Q3;
                if (x <= 0)
                    x = 0;
                double sqrt_disc = Math.sqrt(x);        // modified here. 2007.1.2
                double A = -sgnR * Math.pow(modR + sqrt_disc, 1.0 / 3.0);
                double B = Q / A;
                double mod_diffAB = Math.abs(A - B);

                u[0] = A + B - rc / 3;
                u[1] = -0.5 * (A + B) - rc / 3;
                u[2] = -(Math.sqrt(3.0) / 2.0) * mod_diffAB;
            }
        }

        /* End of solution to resolvent cubic */

        /* Combine the square roots of the roots of the cubic
         * resolvent appropriately. Also, calculate 'mt' which
         * designates the nature of the roots:
         * mt=1 : 4 real roots (disc == 0)
         * mt=2 : 0 real roots (disc < 0)
         * mt=3 : 2 real roots (disc > 0)
         */

        if (0.0 == disc)
            u[2] = u[1];

        if (0 >= disc) {
            mt = 2;

            /* One would think that we could return 0 here and exit,
             * since mt=2. However, this assignment is temporary and
             * changes to mt=1 under certain conditions below.
             */

            v[0] = Math.abs(u[0]);
            v[1] = Math.abs(u[1]);
            v[2] = Math.abs(u[2]);

            v1 = Math.max(Math.max(v[0], v[1]), v[2]);
            /* Work out which two roots have the largest moduli */
            k1 = 0;
            k2 = 0;
            if (v1 == v[0]) {
                k1 = 0;
                v2 = Math.max(v[1], v[2]);
            } else if (v1 == v[1]) {
                k1 = 1;
                v2 = Math.max(v[0], v[2]);
            } else {
                k1 = 2;
                v2 = Math.max(v[0], v[1]);
            }

            if (v2 == v[0]) {
                k2 = 0;
            } else if (v2 == v[1]) {
                k2 = 1;
            } else {
                k2 = 2;
            }

            if (0.0 <= u[k1]) {
                w1r = Math.sqrt(u[k1]);
                w1i = 0.0;
            } else {
                w1r = 0.0;
                w1i = Math.sqrt(-u[k1]);
            }
            if (0.0 <= u[k2]) {
                w2r = Math.sqrt(u[k2]);
                w2i = 0.0;
            } else {
                w2r = 0.0;
                w2i = Math.sqrt(-u[k2]);
            }
        } else {
            mt = 3;

            if (0.0 == u[1] && 0.0 == u[2]) {
                arg = 0.0;
            } else {
                arg = Math.sqrt(Math.sqrt(u[1] * u[1] + u[2] * u[2]));
            }
            double theta = Math.atan2(u[2], u[1]);

            w1r = arg * Math.cos(theta / 2.0);
            w1i = arg * Math.sin(theta / 2.0);
            w2r = w1r;
            w2i = -w1i;
        }

        /* Solve the quadratic to obtain the roots to the quartic */
        w3r = qq / 8.0 * (w1i * w2i - w1r * w2r) /
                (w1i * w1i + w1r * w1r) / (w2i * w2i + w2r * w2r);
        h = a / 4.0;

        zarr[0] = w1r + w2r + w3r - h;
        zarr[1] = -w1r - w2r + w3r - h;
        zarr[2] = -w1r + w2r - w3r - h;
        zarr[3] = w1r - w2r - w3r - h;

        /* Arrange the roots into the variables z0, z1, z2, z3 */
        if (2 == mt) {
            if (u[k1] >= 0 && u[k2] >= 0) {
                mt = 1;
                x1 = zarr[0];
                x2 = zarr[1];
                x3 = zarr[2];
                x4 = zarr[3];
                double[] x = new double[4];
                x[0] = x1;
                x[1] = x2;
                x[2] = x3;
                x[3] = x4;
                return x;
            } else {
                return null;
            }
        } else {
            x1 = zarr[0];
            x2 = zarr[1];
            double[] x = new double[2];
            x[0] = x1;
            x[1] = x2;
            return x;
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Calculates the polynomial value for the given monomial and coefficients.
     *
     * @param m the monomial
     * @param p the coefficients
     * @return the polynomial value
     */
    BigFraction calpoly(TMono m, BigFraction[] p) {
        if (m == null || p == null)
            return BigFraction.ZERO;

        if (Int(m))
            return new BigFraction(m.val);
        BigFraction r = BigFraction.ZERO;

        while (m != null) {
            BigFraction v = calpoly(m.coef, p);
            if (m.deg == 0)
                r = r.add(v);
            else {
                int id = m.x - 1;
                if (id < 0 || id >= p.length || p[m.x - 1] == null)
                    return BigFraction.ZERO;
                r = r.add((p[m.x - 1]).pow(m.deg).multiply(v));
            }
            m = m.next;
        }
        return r;
    }

    /**
     * Reduces the polynomial by dividing it by the leading term of another polynomial.
     *
     * @param vlist the list of polynomials to reduce
     * @param t the time limit for reduction
     * @return the reduced polynomial
     */
    public Vector bb_reduce(Vector vlist, long t) {
        bb_reduce(vlist, t, false);
        return vlist;
    }

    /**
     * Reduces the polynomial by dividing it by the leading term of another polynomial.
     *
     * @param vlist the list of polynomials to reduce
     * @param t the time limit for reduction
     * @param s a flag indicating whether to use a special reduction method
     * @return the reduced polynomial
     */
    public Vector bb_reduce(Vector vlist, long t, boolean s) {


        while (true) {
            boolean r = true;
            int size = vlist.size();

            for (int i = size - 2; i >= 0; i--) {
                boolean modified = false;
                TMono m2 = (TMono) vlist.get(i);
                for (int j = i + 1; j < vlist.size(); j++) {
                    TMono m1 = (TMono) vlist.get(j);
                    if (s && plength(m1.coef) != 1)
                        continue;

                    if (BB_STOP) return vlist;
                    TMono m = bb_divnh(m2, m1);
                    if (m != null) {
                        modified = true;
                        m1 = p_copy(m1);
                        BigInteger b1 = getLN(m1);
                        m2 = pdif(cp_times(b1, m2), pp_times(m, m1));
                        coefgcd(m2);
                        r = false;
                        if (m2 == null) break;
                        if (Int(m2)) return vlist;
                    }
                }


                if (modified) {
                    vlist.remove(i);
                    if (m2 != null) {
                        coefgcd(m2);
                        ppush(m2, vlist);
                    }
                    size = vlist.size();
                }
            }
            if (r) break;
        }
        return vlist;
    }

    /**
     * Reduces the polynomial by dividing it by the leading term of another polynomial.
     *
     * @param m1 the first polynomial
     * @param vlist the list of polynomials to reduce
     * @return the reduced polynomial
     */
    public TMono b_reduce(TMono m1, Vector vlist) {
        if (m1 == null) return null;

        while (true) {
            boolean r = true;
            for (int i = 0; i < vlist.size(); i++) {
                TMono m2 = (TMono) vlist.get(i);
                if (m1 == m2)
                    continue;
                //             if (m1.coef != null && m1.coef.coef == null)
                //                continue;

                //              m1 = this.sp_reduce(m1, m2);

                TMono m = bb_divn(m1, m2);
                while (m != null) {
                    BigInteger b2 = getLN(m2);
                    if (BB_STOP) return null;

                    m1 = pdif(cp_times(b2, m1), pp_times(m, p_copy(m2)));

                    if (m1 == null) return null;
                    r = false;
                    m = bb_divn(m1, m2);
                }
            }
            if (r) break;
        }
        coefgcd(m1);
        return m1;
    }

    /**
     * Divides the leading term of one polynomial by the leading term of another polynomial.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the result of the division
     */
    private TMono bb_divnh(TMono m1, TMono m2) {
        if (m1 == null || m2 == null) return null;

        if (m1.x < m2.x || m1.x == m2.x && m1.deg < m2.deg) return null;

        if (Int(m1) && Int(m2))
            return pth(0, m1.val, 0);

        TMono mx = null;
        if (m1.x == m2.x) {
            if (m1.deg == m2.deg)
                return bb_divn(m1.coef, m2.coef);
            else {
                mx = bb_divn(m1.coef, m2.coef);
                int dd = m1.deg - m2.deg;
                if (dd == 0)
                    return mx;
                else if (dd > 0)
                    return pp_times(pth(m1.x, 1, dd), mx);
            }
        }


        mx = bb_divn(m1.coef, m2);
        if (mx == null) return null;

        return pp_times(pth(m1.x, 1, m1.deg), mx);
    }

    /**
     * Divides the leading term of one polynomial by the leading term of another polynomial.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the result of the division
     */
    private TMono bb_divn(TMono m1, TMono m2) {  //  get a term of m1 which diviid leading variable of m2.
        if (m1 == null || m2 == null) return null;

        if (m1.x < m2.x || m1.x == m2.x && m1.deg < m2.deg) return null;

        if (Int(m1) && Int(m2))
            return pth(0, m1.val, 0);

        TMono mx = null;


        if (m1.x == m2.x) {
            if (m1.deg == m2.deg)
                return bb_divn(m1.coef, m2.coef);
            else {

                while (m1 != null && m1.deg >= m2.deg) {
                    mx = bb_divn(m1.coef, m2.coef);
                    int dd = m1.deg - m2.deg;
                    if (mx != null) {
                        if (dd == 0)
                            return mx;
                        else if (dd > 0)
                            return pp_times(pth(m1.x, 1, dd), mx);
                    }

                    m1 = m1.next;
                }
            }
            return null;
        } else if (m1.x > m2.x) {
            while (m1 != null) { // m1.x > m2.x

                mx = bb_divn(m1.coef, m2);
                if (mx != null)
                    break;
                m1 = m1.next;
            }

            if (m1 == null) return null;
            if (mx == null) return null;
            if (m1.deg == 0) return mx;
            return pp_times(pth(m1.x, 1, m1.deg), mx);
        }

        return null;
    }


    /**
     * Prints the given vector of polynomials.
     *
     * @param v the vector of polynomials to print
     */
    public void printVpoly(Vector v) {
        for (int i = 0; i < v.size(); i++)
            this.print((TMono) v.get(i));
        System.out.println("\n");
    }

    /**
     * Computes the Groebner basis for the given vector of polynomials.
     *
     * @param v the vector of polynomials
     * @return the Groebner basis as a vector of polynomials
     */
    public Vector g_basis(Vector v) {
        while (true) {
            bb_reduce(v, System.currentTimeMillis());

            if (gb_finished(v))
                break;

            //          this.printVpoly(v);
            Vector tp = s_polys(v);

            for (int i = 0; i < tp.size(); i++) {
                ppush((TMono) tp.get(i), v);
                this.printpoly((TMono) tp.get(i));
            }
            if (tp.size() == 0)
                break;
        }
        return v;
    }

    /**
     * Computes the S-polynomials for the given vector of polynomials.
     *
     * @param vlist the vector of polynomials
     * @return the S-polynomials as a vector of polynomials
     */
    public Vector s_polys(Vector vlist) {

        Vector v = new Vector();
        for (int i = 0; i < vlist.size(); i++) {
            TMono m1 = (TMono) vlist.get(i);
            for (int j = i + 1; j < vlist.size(); j++) {
                TMono m2 = (TMono) vlist.get(j);

                TMono mx = s_poly1(m1, m2);

                mx = b_reduce(mx, vlist);
                coefgcd(mx);
                if (mx != null) {
                    ppush(mx, v);
                }
            }
        }
        return v;
    }

    /**
     * Computes the S-polynomial for two given polynomials.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the S-polynomial of the two polynomials
     */
    private TMono s_poly1(TMono m1, TMono m2) {
        return prem4(m1, m2);
    }

    /**
     * Computes the S-polynomial for two given polynomials.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the S-polynomial of the two polynomials
     */
    private TMono prem4(TMono m1, TMono m2) {
        if (m1 == null || m2 == null) return null;
        if (m1.x < m2.x) return m1;

//        if (m1.x > m2.x)
        {
            TMono mm = gcd_h(m1, m2);
            if (mm == null || mm.x == 0) return null;

            if (mm != null && mm.x != 0) {
                TMono t1 = div_gcd(m1, mm);
                TMono t2 = div_gcd(m2, mm);
                TMono m = pdif(ptimes(t2, p_copy(m1)), ptimes(t1, p_copy(m2)));
                return m;
            }
        }
//        else {
//            return prem1(m1, m2);
//        }
        return null;
    }

    /**
     * Computes the greatest common divisor of two polynomials.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the greatest common divisor of the two polynomials
     */
    private TMono gcd_h(TMono m1, TMono m2) {           // gcd of m1, m2.   (HEAD);
        if (m1 == null || m2 == null) return null;
        TMono mx = null;

        while (m1 != null && m2 != null && m1.x != 0 && m2.x != 0) {
            if (m1.x == m2.x) {
                TMono m;
                if (m1.x > 0) {
                    int dd = m1.deg;
                    if (dd > m2.deg)
                        dd = m2.deg;
                    m = pth(m1.x, 1, dd);
                    if (mx == null)
                        mx = m;
                    else
                        mx = ptimes(mx, m);
                }

                m1 = m1.coef;
                m2 = m2.coef;
            } else if (m1.x > m2.x)
                m1 = m1.coef;
            else if (m1.x < m2.x)
                m2 = m2.coef;
        }
        return mx;
    }

    /**
     * Divides the leading term of one polynomial by the leading term of another polynomial.
     *
     * @param m1 the first polynomial
     * @param m the second polynomial
     * @return the result of the division
     */
    private TMono div_gcd(TMono m1, TMono m) {
        TMono mx = pth(0, 1, 0);

        while (m1 != null) {
            if (m1.x > m.x) {
                mx = ptimes(mx, pth(m1.x, 1, m1.deg));
                m1 = m1.coef;
            } else if (m1.x == m.x) {
                if (m1.x == 0) {
                    mx = ptimes(mx, pth(0, m1.val, 0));
                } else {
                    int dd = m1.deg - m.deg;
                    if (dd > 0)
                        mx = ptimes(mx, pth(m1.x, 1, dd));
                }
                m1 = m1.coef;
                m = m.coef;
            }
        }
        return mx;
    }


    /**
     * Computes the greatest common divisor of two polynomials and stores the result in the provided array.
     *
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @param mm the array to store the result
     */
    private void get_mm(TMono m1, TMono m2, TMono[] mm) {
        if (m1 == null || m2 == null) return;


        if (m1.x > m2.x) {
            mm[1] = ptimes(mm[1], pth(m1.x, 1, m1.deg));
            m1 = m1.coef;
        } else if (m1.x < m2.x) {
            mm[0] = ptimes(mm[0], pth(m2.x, 1, m2.deg));
            m2 = m2.coef;
        } else {
            if (m1.x == 0) {
                mm[1] = this.cp_times(m1.val, mm[1]);
                mm[0] = this.cp_times(m2.val, mm[0]);
                return;
            } else {
                if (m1.deg > m2.deg)
                    mm[1] = ptimes(mm[1], pth(m1.x, 1, m1.deg - m2.deg));
                else if (m1.deg < m2.deg)
                    mm[0] = ptimes(mm[0], pth(m1.x, 1, m2.deg - m1.deg));

                m1 = m1.coef;
                m2 = m2.coef;
            }
        }
        get_mm(m1, m2, mm);
    }

    /**
     * Gets the leading coefficient of a polynomial.
     *
     * @param m the polynomial
     * @return the leading coefficient
     */
    private BigInteger getLN(TMono m) {
        if (m == null) return null;
        while (!Int(m))
            m = m.coef;
        return m.val;
    }

    /**
     * Computes the delta of two polynomials.
     *
     * @param x the variable
     * @param m1 the first polynomial
     * @param m2 the second polynomial
     * @return the delta of the two polynomials
     */
    public TMono ll_delta(int x, TMono m1, TMono m2) {
        if (m1 == null) return null;
        if (m1.deg == 1) {
            TMono m11 = getxm1(x, 1, m1);
            TMono m12 = getxm1(x - 1, 1, m1);

            if (m2 == null) {
                return this.pp_minus(pp_times(p_copy(m11), p_copy(m11)), pp_times(p_copy(m12), p_copy(m12)));
            }
            TMono m21 = getxm1(x, 1, m2);
            TMono m22 = getxm1(x - 1, 1, m2);

            TMono mx = pp_minus(pp_times(p_copy(m11), p_copy(m22)), pp_times(p_copy(m12), p_copy(m21)));

            if (mx == null) return mx;
            if (getLN(mx).intValue() < 0)
                mx = cp_times(-1, mx);
            return mx;
        } else if (m1.deg == 2) {
            if (m2 == null) return null;
            if (m2.deg == 1) {

            } else if (m2.deg == 2) {
                TMono m11 = getxm1(x, 1, m1);
                TMono m12 = getxm1(x - 1, 1, m1);

                TMono m21 = getxm1(x, 1, m2);
                TMono m22 = getxm1(x - 1, 1, m2);

                TMono x1 = pp_minus(p_copy(m11), p_copy(m21));
                TMono x2 = pp_minus(p_copy(m12), p_copy(m22));
                TMono mx = padd(pp_times(x1, p_copy(x1)), pp_times(x2, p_copy(x2)));
                return mx;
            }
        }
        return null;
    }

    /**
     * Gets the leading coefficient of a polynomial.
     *
     * @param x the variable
     * @param d the degree
     * @param m the polynomial
     * @return the leading coefficient
     */
    public TMono getxm1(int x, int d, TMono m) {
        if (m == null)
            return null;
        while (m != null) {
            if (m.x < x || m.x == x && m.deg < d)
                return null;

            if (m.x == x && m.deg == d)
                return m.coef;

            if (m.next == null) {
                if (m.deg != 0)
                    return null;
                else m = m.coef;
            } else
                m = m.next;
        }
        return null;
    }

    /**
     * Updates the value of a polynomial by adding a given value to its coefficients.
     *
     * @param v the polynomial
     * @param dx the value to add
     */
    public void upValueTM(Vector v, int dx) {
        if (dx == 0)
            return;

        for (int i = 0; i < v.size(); i++) {
            upValueTM((TMono) v.get(i), dx);
        }
    }

    /**
     * Updates the value of a polynomial by adding a given value to its coefficients.
     *
     * @param v the polynomial
     * @param dx the value to add
     */
    public void upValueDM(Vector v, int dx) {
        for (int i = 0; i < v.size(); i++) {
            TDono d = (TDono) v.get(i);

            upValueTM(d.p1, dx);
            upValueTM(d.p2, dx);
        }
    }

    /**
     * Gets the maximum value of x in a vector of polynomials.
     *
     * @param v the vector of polynomials
     * @return the maximum value of x
     */
    public int getMaxX(Vector v) {
        int x = 0;

        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (x < m.x)
                x = m.x;
        }
        return x;
    }

    /**
     * Updates the value of a polynomial by adding a given value to its coefficients.
     *
     * @param m the polynomial
     * @param dx the value to add
     */
    public void upValueTM(TMono m, int dx) {
        if (dx == 0)
            return;

        if (m == null)
            return;
        if (m.x == 0)
            return;

        while (m != null) {
            if (m.x != 0)
                m.x += dx;
            if (m.x == 0) {
                int n = 0;
            }

            upValueTM(m.coef, dx);
            m = m.next;
        }
    }
    
    /**
     * Checks if the polynomial is finished.
     *
     * @param v the vector of polynomials
     * @return true if the polynomial is finished, false otherwise
     */
    public boolean gb_finished(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (plength(m) == 1 && m.x == 0 && m.value() != 0)
                return true;
        }
        return false;
    }

    /**
     * Reduces the polynomial by removing terms with degree 0.
     *
     * @param v the vector of polynomials
     */
    public void ndg_reduce(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (m.deg == 0) {
                v.remove(m);
                i--;
            }
        }
    }

    /**
     * Gets the conditions for the given vector of polynomials.
     *
     * @param v the vector of polynomials
     * @param dx the value to add
     * @return the conditions as a vector of polynomials
     */
    public Vector getcnds(Vector v, int dx) {
        Vector v1 = new Vector();
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (ctLessdx1(m, dx)) {
                v1.add(p_copy(m));
                v.remove(m);
                i--;
            }
        }
        return v1;
    }


    /**
     * Parses common Dono objects from the given vector of polynomials.
     *
     * @param v the vector of polynomials
     * @param dx the value to add
     * @return a vector of parsed Dono objects
     */
    public Vector parseCommonDono(Vector v, int dx) {
        Vector v1 = new Vector();
        for (int i = 0; i < v.size(); i++) {
            TDono d = (TDono) v.get(i);
            for (int j = i + 1; j < v.size(); j++) {
                TDono d1 = (TDono) v.get(j);
                TMono p1 = d.p2;
                TMono p2 = d1.p2;
                BigInteger b1 = this.getLN(p1);
                BigInteger b2 = this.getLN(p2);

                if (ck_eq(p1, p2)) {
                    TMono m1 = pp_times(p_copy(d.p1), p_copy(d1.c));
                    TMono m2 = pp_times(p_copy(d.c), p_copy(d1.p1));
                    TMono m = pdif(cp_times(b1, m1), cp_times(b2, m2));
                    v1.add(m);
                }
            }
        }
        return v1;
    }

    /**
     * Erases common Dono objects from the given vector of polynomials.
     *
     * @param v the vector of polynomials
     */
    public void eraseCommonDono(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            TDono d = (TDono) v.get(i);
            for (int j = i + 1; j < v.size(); j++) {
                TDono d1 = (TDono) v.get(j);
                TMono p1 = d.p2;
                TMono p2 = d1.p2;
                if (ck_eq(p1, p2)) {
                    v.remove(j);
                    j--;
                }
            }
        }
    }

    /**
     * Checks if two monomials are equal.
     *
     * @param m1 the first monomial
     * @param m2 the second monomial
     * @return true if the monomials are equal, false otherwise
     */
    public boolean ck_eq(TMono m1, TMono m2) {
        while (m1 != null && m2 != null) {
            if (m1.x != m2.x || m1.deg != m2.deg)
                return false;
            if (!ck_eq(m1.coef, m2.coef))
                return false;
            m1 = m1.next;
            m2 = m2.next;
        }

        return m1 == m2;
    }

    /**
     * Checks if the polynomial is less than a given value.
     *
     * This method only traverses the `coef` chain of the polynomial.
     *
     * @param m the polynomial
     * @param dx the value to compare with
     * @return true if the polynomial is less than the given value, false otherwise
     */
    public boolean ctLessdx1(TMono m, int dx) {
        while (m != null) {
            if (m.x > 0 && m.deg > 0 && m.x < dx)
                return true;
            m = m.coef;
        }
        return false;
    }


    /**
     * Checks if the polynomial is less than a given value.
     *
     * This method traverses both the `coef` and `next` chains of the polynomial and uses recursion.
     *
     * @param m the polynomial
     * @param dx the value to compare with
     * @return true if the polynomial is less than the given value, false otherwise
     */
    public boolean ctLessdx(TMono m, int dx) {
        int r = 0;

        while (m != null) {
            if (m.x > 0 && m.deg > 0 && m.x < dx)
                return true;

            if (ctLessdx(m.coef, dx))
                return true;

            m = m.next;
        }

        return false;
    }

    /**
     * Gets the minimum leading degree of a polynomial.
     *
     * @param m the polynomial
     * @param dx the value to compare with
     * @return the minimum leading degree, or -1 if not found
     */
    private int MinLdx(TMono m, int dx) {
        int r = MinLdx(m);
        if (r >= dx)
            return -1;
        return r;
    }

    /**
     * Gets the minimum leading degree of a polynomial.
     *
     * @param m the polynomial
     * @return the minimum leading degree
     */
    private int MinLdx(TMono m) {
        int r = Integer.MAX_VALUE;

        while (m != null) {
            if (m.x == 0)
                return Integer.MAX_VALUE;
            if (r > m.x)
                r = m.x;
            int k = MinLdx(m.coef);
            if (k < r)
                r = k;
            m = m.next;
        }
        return r;

    }


    /**
     * Gets the maximum leading degree of a polynomial.
     *
     * @param m the polynomial
     * @param i the value to compare with
     * @return the maximum leading degree
     */
    private int ctMLdx(TMono m, int i) {  // MAX
        int r = 0;

        while (m != null) {
            if (m.x < i)
                break;

            if (m.x == i) {
                if (r < m.deg)
                    r = m.deg;
                break;
            } else if (m.x > i) {
                int k = ctMLdx(m.coef, i);
                if (r < k)
                    r = k;
            }

            m = m.next;
        }
        return r;
    }


    /**
     * Reduces the polynomial by dividing it by the leading term of another polynomial.
     *
     * @param mm the polynomial to reduce
     * @param v the vector of polynomials
     * @param dx the value to add
     * @return the reduced polynomial
     */
    public TMono reduceMDono(TMono mm, Vector v, int dx) {
        TMono m = mm;

        while (true) {
            int max = MinLdx(m, dx);

            if (max <= 0)
                break;

            TDono d1 = getDo(v, max);
            if (BB_STOP)
                return null;

            if (d1 != null) {


                int rd = ctMLdx(m, max);

                TMono m2 = padd(pp_times(p_copy(d1.p1), p_copy(d1.p2)), p_copy(d1.c));
                for (int k = 0; k < rd; k++) {
                    m = pp_times(m, p_copy(d1.p2));
                }

                TMono dp = basic.p_copy(d1.p1);
                div_factor1(dp, max, 1);
                while (dp != null && dp.x != 0 && dp.deg == 0)
                    dp = dp.coef;

                if (dp != null && dp.x != 0 && dp.deg != 0) {
                    for (int k = 0; k < rd; k++) {
                        m = pp_times(m, p_copy(dp));
                    }
                }


                TMono mx = bb_divn(m, m2);

                while (mx != null && mx.x != 0) {
                    BigInteger b2 = getLN(m2);
                    m = pdif(cp_times(b2, m), pp_times(mx, p_copy(m2)));
                    mx = bb_divn(m, m2);
                    if (BB_STOP)
                        return null;
                }
                coefgcd(m);
            }
        }
        return m;
    }

    /**
     * Gets the Dono object from the given vector of polynomials.
     *
     * @param v the vector of polynomials
     * @param n the value to compare with
     * @return the Dono object, or null if not found
     */
    public TDono getDo(Vector v, int n) {
        TDono xd = null;
        int nn = -1;

        for (int i = 0; i < v.size(); i++) {
            TDono d = (TDono) v.get(i);
            TMono m = d.p1;
            nn = -1;

            while (m != null) {
                if (m != null && m.x != 0) {
                    nn = m.x;
                } else
                    break;
                m = m.coef;
            }

            if (nn == n)
                return d;
        }

        return xd;
    }

    /**
     * Splits the given monomial into a Dono object.
     *
     * @param m the monomial
     * @param dx the value to add
     * @return the Dono object
     */
    public TDono splitDono(TMono m, int dx) {
        TMono m1 = m;
        TMono c = null;

        while (m1 != null) {
            if (m1.x == 0) {
                c = pth(0, m1.val, 0);
                break;
            } else if (m1.deg == 0)
                m1 = m1.coef;
            else m1 = m1.next;
        }

        if (c == null)
            return null;

        TMono mx = this.pp_minus(p_copy(m), p_copy(c));

        TMono mo = pth(0, 1, 0);
        TMono mf = get_factor1(mx);
        while (mf != null && mf.x != 0) {
            if (mf.x < dx) {
                mo = pp_times(mo, pth(mf.x, 1, mf.deg));
                div_factor1(mx, mf.x, mf.deg);
            }
            mf = mf.coef;
        }

        if (mx != null && getLN(mx).intValue() < 0) {
            mx = cp_times(-1, mx);
            c = cp_times(-1, c);
        }

        return new TDono(mo, mx, c);
    }
}

