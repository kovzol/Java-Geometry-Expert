package gprover;


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

    void init_poly() {
        all_var = new Var();
        last_var = all_var;
        all_var.nx = null;
    }

    public Poly() {
        init_poly();
    }

    XTerm get_x() {
        return (new XTerm());
    }

    DTerm get_d() {
        return (new DTerm());
    }

    XTerm get_n(long n) {
        XTerm p1;
        p1 = get_x();
        p1.var = null;
        p1.c = (n);
        return (p1);
    }

    XTerm get_num(long n) {
        XTerm p1;
        p1 = get_x();
        p1.var = null;
        p1.c = n;
        return (p1);
    }

    XTerm get_xt(Var v, DTerm dp1) {
        XTerm xp1;
        xp1 = get_x();
        xp1.var = v;
        xp1.ps = dp1;
        return (xp1);
    }

    XTerm get_s(char[] ch) {
        return (get_m(mk_svar(ch)));
    }

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


    DTerm get_dt(int d, XTerm xp1, DTerm dp1) {
        DTerm d1;
        d1 = get_d();
        d1.deg = d;
        d1.p = xp1;
        d1.nx = dp1;
        return (d1);
    }

    void put_d(DTerm dpt) {
    }

    void put_x(XTerm xpt) {
    }

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

    void put_ps(DTerm dp0) {
        DTerm dp1;
        while (dp0 != null) {
            put_p(dp0.p);
            dp1 = dp0.nx;
            put_d(dp0);
            dp0 = dp1;
        }
    }

/* xtermnomials */

    boolean num_eq(long c1, long c2) {
        return c1 == c2;
    }

    boolean eq_poly(XTerm p1, XTerm p2) {
        if ((p1.var == null) && (p2.var == null))
            return (num_eq(p1.c, p2.c));
        else if (p1.var != p2.var)
            return (false);
        else
            return (eq_pols(p1.ps, p2.ps));
    }

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

    XTerm cp_poly(XTerm p1) {
        if (p1.var == null)
            return (get_num(p1.c));
        else
            return (get_xt(p1.var, cp_pols(p1.ps)));
    }

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

    XTerm pzero() {
        return (get_n(0L));
    }

/*
int pclass(p)
xterm *p;
{ if (numberp(p)) return(0);
 else		  return(p.var);
} */

    int ldeg(XTerm p)
//xterm p;
    {
        DTerm dp1;
        if (p.var == null) return (0);
        dp1 = p.ps;
        return (dp1.deg);
    }

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


    long lcc(XTerm p) {
        DTerm dp1;
        while (p.var != null) {
            dp1 = p.ps;
            p = dp1.p;
        }
        return (p.c);
    }


    long fcc(XTerm p) {
        return (lcc(p));
    }

    static long lcc_p;

    int lcontent(XTerm p) {
        lcc_p = mk_num(0L);
        lcont1(p);
        if (num_negp(lcc(p))) lcc_p = num_neg(lcc_p);
        return (int) (lcc_p);
    }

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

    XTerm init(XTerm p) {
        DTerm dp1;
        if (p.var == null) return (null);
        dp1 = p.ps;
        return (dp1.p);
    }

    XTerm cp_init(XTerm p) {
        DTerm dp1;
        if (p.var == null) return (null);
        dp1 = p.ps;
        return (cp_poly(dp1.p));
    }


    XTerm init_v(XTerm p, Var v) {
        return (pinit(p, v, pdeg(p, v)));
    }

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

    XTerm cp_rem(XTerm p)
//xterm p;
    {
        return (rem(cp_poly(p)));
    }

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

    int mono_pol(XTerm p) {
        DTerm ps;
        while (p.var != null) {
            ps = p.ps;
            if (ps.nx != null) return (0);
            p = ps.p;
        }
        return (1);
    }

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

    int mlength(XTerm p) {
        DTerm dp1;
        int count = 0;
        if (p.var != null) {
            count++;
            dp1 = p.ps;
            p = dp1.p;
        }
        return (count);
    }

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

    int dlength(DTerm dp1) {
        int count = 0;
        while (dp1 != null) {
            count += plength(dp1.p);
            dp1 = dp1.nx;
        }
        return (count);
    }

    boolean pless(XTerm p1, XTerm p2) {
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
            else
                return (plength(p1) < plength(p2));
        } else if (vless(p1.var, p2.var))
            return (true);
        else
            return (false);
    }

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


    int bad_poly(XTerm p) {
        DTerm ps;
        if (p.var == null) return (bad_num(p.c));
        for (ps = p.ps; ps != null; ps = ps.nx) {
            if (bad_poly(ps.p) != 0) return (1);
        }
        return (0);
    }

    int bad_num(long n) {
        return 0;
    }

/* Polynomial Plus: destructive */

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

    // c < p2
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

    XTerm pminus(XTerm p1, XTerm p2) {
        XTerm q1;
        q1 = neg_poly(p2);
        return (pplus(p1, q1));
    }

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

    XTerm pdiv(XTerm p1, XTerm p2)
//xterm p1, p2;
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


    XTerm pcdiv(XTerm p, XTerm c)
//xterm p,c;
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

    void xerror(char ch)
//char *ch;
    {
        Cm.print("\n\n Syntax error:\n\n");
        //      Cm.print(ch);
        Cm.print("\n\n Please check your input.\n");
    }

/* print polynomials  */

//      public void gprint(String s)
//      {
//            System.out.print(s);
//      }

    void dprint(DTerm dp1) {
        if (dp1 == null)
            gprint("null\r\n");
        else {
            while (dp1 != null) {
                gprint("\r\n");
                print_ind(dp1.p);
                gprint("\r\n");
                if (plength(dp1.p) < 20) pprint(dp1.p);
                dp1 = dp1.nx;
            }
        }
    }

    final void xprint(XTerm p1) {
        print_p(p1, (char) 0);
    }

    void pprint(XTerm p1) {
        print_p(p1, (char) 0);
        gprint("\r\n");
    }

    void eprint(XTerm p1) {
        print_p(p1, (char) 0);
        gprint("=0\r\n");
    }

    static int char_no;

    void print_p(XTerm p1, char mk)            // print_pp1
    {
        char_no = 0;
        print_p1(p1, mk);
    }

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


    XTerm c_pplus(XTerm x, XTerm y) {
        return pplus(cp_poly(x), cp_poly(y));
    }

    XTerm c_pminus(XTerm x, XTerm y) {
        return pminus(cp_poly(x), cp_poly(y));
    }

    XTerm c_ptimes(XTerm x, XTerm y) {
        return ptimes(cp_poly(x), cp_poly(y));
    }

    XTerm c_pdiv(XTerm x, XTerm y) {
        return pdiv(cp_poly(x), y);
    }

    XTerm pplus3(XTerm x, XTerm y, XTerm z) {
        return pplus(x, pplus(y, z));
    }

    XTerm ptimes3(XTerm x, XTerm y, XTerm z) {
        return ptimes(x, ptimes(y, z));
    }

    XTerm pplus4(XTerm x, XTerm y, XTerm z, XTerm w) {
        return pplus(pplus(x, y), pplus(z, w));
    }

    XTerm ptimes4(XTerm x, XTerm y, XTerm z, XTerm w) {
        return ptimes(ptimes(x, y), ptimes(z, w));
    }

//-----------------------------------------
//-----------------------------------------var.cpp


    static Var svar = new Var();

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

    Var mk_var(int nm, int p1, int p2, int p3, int p4) {
        svar.nm = nm;
        svar.pt[0] = p1;
        svar.pt[1] = p2;
        svar.pt[2] = p3;
        svar.pt[3] = p4;
        return (ad_var(svar));
    }

    Var mk_svar(char[] nm) {
        char i;
        for (i = 0; i < 9; i++) svar.p[i] = 0;
        svar.nm = 0;
        strcpy(svar.p, nm);
        return (ad_var(svar));
    }

    Var mk_cvar(char[] nm) {
        char i;
        for (i = 0; i < 9; i++) svar.p[i] = 0;
        strcpy(svar.p, nm);
        svar.nm = 99;
        return (ad_var(svar));
    }

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


    int ials(int a1[], int a2[], int n) {
        char i;
        for (i = 0; i <= n; ++i) {
            if (a1[i] < a2[i])
                return (1);
            else if (a1[i] > a2[i]) return (0);
        }
        return (0);
    }

    int lpt(Var v) {
        int k = v.pt[0];
        if (v.nm == 1) {
            if (v.pt[2] > v.pt[0]) k = v.pt[2];
        } else if (v.nm == 3) {
            if (v.pt[1] > v.pt[0]) k = v.pt[1];
        }
        return (k);
    }

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


    void print_vars(Var v, char mk) {
        while (v != null) {
            print_var(v, mk);
            v = v.nx;
        }
    }

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

//      void print_fang(int p1, int p2, int p3, int p4)
//      {
//            int p0;
//            if (p1 == p4)
//            {
//                  p0 = p4;
//                  p4 = p3;
//                  p3 = p0;
//            } else if (p2 == p3)
//            {
//                  p0 = p1;
//                  p1 = p2;
//                  p2 = p0;
//            } else if (p2 == p4)
//            {
//                  p0 = p1;
//                  p1 = p2;
//                  p2 = p0;
//                  p0 = p4;
//                  p4 = p3;
//                  p3 = p0;
//            }
////        sprintf(txt, "%s[%s%s,%s%s]", Cm.s2078),
////                pt_name(p1), pt_name(p2), pt_name(p3), pt_name(p4));
//            gprint(Cm.s2078 + "[");
//      }

    void print_pt(int p) {
        //sprintf(txt,"%s",pt_name(p));
        gprint(pt_name(p));
    }

/////--------------------------------------------------getp.cpp

    static DTerm p_stk = new DTerm();
    public static int gno;

    public XTerm rd_pol(String sterm[]) {

        gno = 0;
        int nterm = sterm.length;
        DTerm ps1;

        p_stk.nx = null;
        if (strcmp(sterm[gno], "-") == 0 || strcmp(sterm[gno], "+") == 0) {
            p_stk.nx = get_dt(1, get_n(0L), p_stk.nx);
        }


        ps1 = p_stk.nx;
        while (gno < nterm && strcmp(sterm[gno], ";") != 0 &&
                strcmp(sterm[gno], "=") != 0 && strcmp(sterm[gno], ".") != 0)// && (in_hyp == 0 || (ps1 != null && !(ps1.nx == null && ps1.deg == 1))))
        {

            if (strcmp(sterm[gno], "(") == 0) {
                if (ps1 != null && ps1.deg == 1) {
                    do_it(6);
                    gno--;
                }
                p_stk.nx = get_dt(2, null, p_stk.nx);
                gno++;
                if (strcmp(sterm[gno], "-") == 0 || strcmp(sterm[gno], "+") == 0) {
                    p_stk.nx = get_dt(1, get_n(0L), p_stk.nx);
                }
            } else if (strcmp(sterm[gno], ")") == 0) {
                do_it(3);
            } else if (strcmp(sterm[gno], "+") == 0) {
                do_it(4);
            } else if (strcmp(sterm[gno], "-") == 0) {
                do_it(5);
            } else if (strcmp(sterm[gno], "*") == 0) {
                do_it(6);
            } else if (strcmp(sterm[gno], "/") == 0) {
                do_it(7);
            } else if (strcmp(sterm[gno], "^") == 0) {
                do_it(8);
            } else if (num_ch(sterm[gno])) {
                if (ps1 != null && ps1.deg == 1) {
                    do_it(6);
                }
                p_stk.nx = get_dt(1, get_n(ch2num(sterm[gno])), p_stk.nx);
                gno++;
            } else {
                if (ps1 != null && ps1.deg == 1)
                    do_it(6);
                else
                    gno++;
                p_stk.nx = get_dt(1, get_m(mk_svar(sterm[gno - 1].toCharArray())), p_stk.nx);
            }
            ps1 = p_stk.nx;
        }

        if (gno > nterm) {
            gerror(" expression error\n");
            return (null);
        }
        do_it(0);
        return (ps1.p);
    }


    public void doother(String[] sterm) {
        //       if (strcmp(sterm[gno], Cm.s3000) == 0) {
        //cannot dela with (RATIO B F F C)<-
//                p_stk.nx = get_dt(1, trim_r(fptno(gno + 1), fptno(gno + 2), fptno(gno + 3), fptno(gno + 4)), p_stk.nx);
//                gno += 5;
//            } else if (strcmp(sterm[gno], Cm.s3001) == 0) {
//                //gao need
//                p_stk.nx = get_dt(1, trim_f(fptno(gno + 1), fptno(gno + 2), fptno(gno + 3), fptno(gno + 4)), p_stk.nx);
//                gno += 5;
//            } else if (strcmp(sterm[gno], Cm.s3002) == 0) {
//                if (strcmp(sterm[gno + 4], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_a(fptno(gno + 1), fptno(gno + 2), fptno(gno + 3), fptno(gno + 3)), p_stk.nx);
//                    gno += 4;
//                } else if (strcmp(sterm[gno + 5], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_a(fptno(gno + 1), fptno(gno + 2), fptno(gno + 3), fptno(gno + 4)), p_stk.nx);
//                    gno += 5;
//                } else
//                    gerror("area is not proper");
//            } else if (strcmp(sterm[gno], Cm.s3008) == 0) {
//                if (strcmp(sterm[gno + 4], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_g(fptno(gno + 1), fptno(gno + 2), fptno(gno + 2), fptno(gno + 3)), p_stk.nx);
//                    gno += 4;
//                } else if (strcmp(sterm[gno + 5], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_g(fptno(gno + 1), fptno(gno + 2), fptno(gno + 3), fptno(gno + 4)), p_stk.nx);
//                    gno += 5;
//                } else
//                    gerror("py is not proper");
//            } else if (strcmp(sterm[gno], Cm.s3003) == 0) {
//                p_stk.nx = get_dt(1, trim_g(fptno(gno + 1), fptno(gno + 2), fptno(gno + 2), fptno(gno + 1)), p_stk.nx);
//                gno += 3;
//            } else if (strcmp(sterm[gno], Cm.s3004) == 0) {
//                if (strcmp(sterm[gno + 2], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_vec(fptno(gno + 1), 0), p_stk.nx);
//                    gno += 2;
//                } else if (strcmp(sterm[gno + 3], ")") == 0) {
//                    p_stk.nx = get_dt(1, trim_vec(fptno(gno + 1), fptno(gno + 2)), p_stk.nx);
//                    gno += 3;
//                } else
//                    gerror("vector is not proper");
//            } else if (strcmp(sterm[gno], Cm.s3009) == 0) {
//                p_stk.nx = get_dt(1, trim_l(fptno(gno + 1), fptno(gno + 2)), p_stk.nx);
//                gno += 3;
//            } else if (strcmp(sterm[gno], Cm.s3005) == 0) {
//                p_stk.nx = get_dt(1,
//                        ptimes(trim_r(fptno(gno + 3), fptno(gno + 1), fptno(gno + 3), fptno(gno + 2)),
//                                trim_r(fptno(gno + 4), fptno(gno + 2), fptno(gno + 4), fptno(gno + 1))),
//                        p_stk.nx);
//                gno += 5;
//            } else if (num_ch(sterm[gno])) {
//                if (ps1 != null && ps1.deg == 1) {
//                    do_it(6);
//                }
//                p_stk.nx = get_dt(1, get_n(ch2num(sterm[gno])), p_stk.nx);
//                gno++;
//            } else {
//                if (ps1 != null && ps1.deg == 1)
//                    do_it(6);
//                else
//                    gno++;
//                p_stk.nx = get_dt(1, get_m(mk_svar(sterm[gno - 1])), p_stk.nx);
//            }
    }

    void do_it(int op) {
        DTerm ps1, ps2, ps3;
        XTerm p1;
        int deg;


        ps1 = p_stk.nx;
        if (ps1 == null) {
            p_stk.nx = get_dt(op, null, p_stk.nx);
            return;
        }

        if (ps1.deg == 2) {
            if (op == 3) {
                p_stk.nx = ps1.nx;
                put_d(ps1);
            } else
                p_stk.nx = get_dt(op, null, p_stk.nx);
            gno++;
            return;
        }

        if (ps1.deg != 1) gerror("expresion error 13");
        ps2 = ps1.nx;
        while (ps2 != null && ps2.deg >= op) {
            ps3 = ps2.nx;
            if (ps3 == null) {
                if (ps2.deg != 2) gerror("expression error 14");
                ps1.nx = ps2.nx;
                put_d(ps2);
            } else if (ps3.deg == 2) {
                if (ps2.deg == 4)
                    ps1.p = neg_poly(ps1.p);
                else if (ps2.deg != 3) gerror("expression error15");
                ps1.nx = ps3.nx;
                put_d(ps3);
                put_d(ps2);
            } else if (ps2.deg == 4) {
                ps1.p = pplus(ps1.p, ps3.p);
                ps1.nx = ps3.nx;
                put_d(ps2);
                put_d(ps3);
            } else if (ps2.deg == 5) {
                ps1.p = pminus(ps3.p, ps1.p);
                ps1.nx = ps3.nx;
                put_d(ps2);
                put_d(ps3);
            } else if (ps2.deg == 6) {
                ps1.p = ptimes(ps1.p, ps3.p);
                ps1.nx = ps3.nx;
                put_d(ps2);
                put_d(ps3);
            } else if (ps2.deg == 8) {
                p1 = ps1.p;
                if (p1.var != null) gerror("expression error");
                deg = num_int(p1.c);
                ps1.p = ppower(ps3.p, deg);
                ps1.nx = ps3.nx;
                put_d(ps2);
                put_d(ps3);
            } else {
                gerror("expression error 99");
                return;
            }
            ps2 = ps1.nx;
        }
        if (op == 3) {
            if (ps2 == null || ps2.deg != 2) gerror("expression error 17");
            ps1.nx = ps2.nx;
            put_d(ps2);
            gno++;
        } else if (op > 3) {
            p_stk.nx = get_dt(op, null, p_stk.nx);
            gno++;
        }

    }


    boolean num_ch(String sn) {
        byte[] ch = sn.getBytes();
        int i = 0;
        if (ch[i] == '-')
            i++;
        else if (ch[i] == '+') i++;
        while (i < ch.length && ch[i] != '\0') {
            if (ch[i] < '0' || ch[i] > '9') return (false);
            i++;
        }
        return (true);
    }

    long ch2num(String sn) {
        return Integer.parseInt(sn);
    }
}
