package gprover;

import java.util.Vector;


/**
 * Implements full angle proof processes and geometric elimination.
 */
public class Full extends Elim {

    int max_term;
    GrTerm proof, last_pr;
    boolean qerror;
    int ertype = 0;

    boolean max_termp = false;
    XTerm conc_p1, conc_p2;
    boolean print_conc = false;

    /**
     * Constructs a new Full object.
     */
    public Full() {
        P_STATUS = 0;
    }

    /**
     * Proves the geometric configuration using full elimination logic.
     */
    void prove_full() {
        GrTerm gr1;
        DTerm ps1;
        XTerm p1;
        ElTerm e1;

        ertype = 0;
        pro_type = PRO_FULL;
        max_term = 0;
        last_pr = proof = new GrTerm();
        proof.nx = null;
        d_base = 1;
        qerror = false;
        dbase();
        fconc();
        if (qerror) return;
        boolean first = true;

        do {
            co_db.nx = null;
            ps1 = last_pr.ps1;
            if (ps1 == null) return;

            p1 = ps1.p;

            if (npoly(p1)) {
                print_fend();
                return;
            }
            if (((p1.var).nm) != 10) {
                gprint(Cm.s2810);
                qerror = true;
                return;
            }
            while (true) {
                if ((e1 = elim_q7(p1)) != null) {
                } else if ((e1 = elim_q8(p1)) != null) {
                } else if ((e1 = elim_f(p1.var)) != null) {
                } else if ((fcc(p1) % 2L) == 0 && (e1 = elim_d(p1.var)) != null) {
                } else if ((fcc(p1) % 3L) == 0 && (e1 = elim_t(p1.var)) != null) {
                } else if ((e1 = elim_tri(p1.var)) == null) {
                }
                if (e1 != null) {

                    ElTerm e = null;
                    if (first || show_detail && e1.et != null)
                        e = e1.et;
                    else
                        e = e1;
                    first = false;

                    while (e != null) {
                        int tn = p1.getTermNumber();
                        p1 = eprem(cp_poly(p1), e);
                        p1 = fpoly(p1);
                        if (max_termp) {
                            int tem = plength(p1);
                            if (tem > max_term) max_term = tem;
                        }
                        gr1 = mk_gr(mk_num(1L), get_dt(1, p1, null), mk_num(0L), null, 99, null);
                        gr1.setPTN(tn);
                        gr1.el = e;
                        e = e.nx;
                        gr1.el.nx = null;
                        last_pr.nx = gr1;
                        last_pr = gr1;
                    }
                    break;

                } else if (d_base == 1) {
                    d_base = 2;
                    dbase();
                } else
                    break;
            }
            //  fr = false;
        } while (e1 != null);
        print_fend();
    }

    /**
     * Initializes the geometric database by searching for midpoints, lines, circles, and angles.
     */
    void dbase() {
        MidPt md;
        PLine pn;
        TLine tn;
        ACir cr;
        Angles as;
        for (md = all_md.nx; md != null; md = md.nx) {
            search_md(md);
        }
        for (tn = all_tn.nx; tn != null; tn = tn.nx) {
            search_tn(tn);
        }
        for (cr = all_cir.nx; cr != null; cr = cr.nx) {
            search_cr(cr);
        }
        if (d_base == 2) {
            for (pn = all_pn.nx; pn != null; pn = pn.nx) {
                search_pn(pn);
            }
            for (as = all_as.nx; as != null; as = as.nx) {
                search_as(as);
            }
            for (cr = all_cir.nx; cr != null; cr = cr.nx) {
                search_cr(cr);
            }
        }
    }

    /**
     * Checks if the provided term is non-polynomial.
     *
     * @param p the term to check.
     * @return true if the term has no associated variable; false otherwise.
     */
    boolean npoly(XTerm p) {
        return (p.var == null);
    }

    /**
     * Prints terminal details based on the print_conc flag.
     */
    void print_t() {
        if (print_conc)
            gprint(Cm.s2300);
    }

    /**
     * Prints the final conclusion of the proof.
     */
    void print_fend() {
        DTerm ps1;
        XTerm p1;
        ps1 = last_pr.ps1;
        p1 = ps1.p;
        if (pzerop(p1))
            print_t();
        else if (print_conc) {
            gprint(Cm.s2812);
        }
    }

    /**
     * Constructs and links a new geometric term in the proof chain.
     *
     * @param c1 the first constant.
     * @param p1 the first term.
     * @param c2 the second constant.
     * @param p2 the second term.
     */
    void conc_gr(long c1, XTerm p1, long c2, XTerm p2) {
        if (p1 != null && p1.getPV() < 0)
            p1 = this.neg_poly(p1);

        GrTerm gr = mk_gr1((int) mk_num(c1), p1, (int) mk_num(c2), p2);
        gr.c = 0;
        last_pr.nx = gr;
        last_pr = gr;
    }

    /**
     * Executes the default full angle concatenation process.
     */
    void fconc() {
        fconc(conc);
    }

    /**
     * Executes the full angle concatenation based on the provided condition.
     *
     * @param conc the condition with the predicate and associated parameters.
     */
    void fconc(Cond conc) {
        switch (conc.pred) {
            case CO_COLL:
                /* collinear */
                fconc_coll(conc.p[0], conc.p[1], conc.p[2]);
                break;
            case CO_PARA:
                /* parallel */
                conc_gr(1L, trim_full(conc.p[0], conc.p[1], conc.p[2], conc.p[3]), 0L, null);
                break;
            case CO_PERP:
                /* perpendicular */
                conc_gr(1L, pplus(trim_full(conc.p[0], conc.p[1], conc.p[2], conc.p[3]), get_n(1L)), 0L, null);
                break;
            case CO_ACONG:
                /* eqangle */
                conc_gr(1L, pminus(trim_f(conc.p[0], conc.p[1], conc.p[2], conc.p[3]), trim_f(conc.p[4], conc.p[5], conc.p[6], conc.p[7])),
                        0L, null);
                break;
            case CO_CYCLIC:
                /*cocircle */
                conc_gr(1L, pminus(trim_f(conc.p[3], conc.p[1], conc.p[3], conc.p[2]),
                        trim_f(conc.p[4], conc.p[1], conc.p[4], conc.p[2])),
                        0L, null);
                break;
            case -12:
                /* perp-b */
                conc_gr(1L, pminus(trim_f(conc.p[0], conc.p[1], conc.p[1], conc.p[2]),
                        trim_f(conc.p[1], conc.p[2], conc.p[0], conc.p[2])),
                        0L, null);
                break;
            case 49:
                break;
            case CO_EQ:
                /* constants8 */
                conc_gr(1L, pminus(conc_p1, conc_p2), 0L, null);
                break;
            case CO_PBISECT:
                conc_gr(1L, pminus(trim_f(conc.p[0], conc.p[1], conc.p[1], conc.p[2]),
                                trim_f(conc.p[1], conc.p[2], conc.p[2], conc.p[0])),
                        0L, null);
                break;
            case CO_CONG:
                fconc_cong(conc.p[0], conc.p[1], conc.p[2], conc.p[3]);
                break;
            default: {
                conc_gr(1L, null, 1L, null);
                gprint(Cm.s2811);
                qerror = true;
                ertype = 1; // can not translate to full-angle epression.
            }
        }
    }

    /**
     * Returns the error type encountered during proof processing.
     *
     * @return the error type code.
     */
    public int getErrorType() {
        return ertype;
    }

    /**
     * Processes the collinearity condition for full angle concatenation.
     *
     * @param a first geometric parameter.
     * @param b second geometric parameter.
     * @param c third geometric parameter.
     */
    public void fconc_coll(int a, int b, int c) {
        if (a < b) {
            int k = a;
            a = b;
            b = k;
        }
        if (a < c) {
            int k = a;
            a = c;
            c = k;
        }
        conc_gr(1L, trim_f(a, b, a, c), 0L, null);
    }

    /**
     * Processes the congruence condition for full angle concatenation.
     *
     * @param a first geometric parameter.
     * @param b second geometric parameter.
     * @param c third geometric parameter.
     * @param d fourth geometric parameter.
     * @return true if the congruence condition was successfully processed; false otherwise.
     */
    public boolean fconc_cong(int a, int b, int c, int d) {
        int l, m, n;
        if (a == c) {
            l = a;
            m = b;
            n = d;
        } else if (b == c) {
            l = b;
            m = a;
            n = d;
        } else if (a == d) {
            l = a;
            m = b;
            n = c;
        } else if (b == d) {
            l = b;
            m = a;
            n = c;
        } else {
            conc_gr(1L, null, 1L, null);
            return false;
        }
        conc_gr(1L, pminus(trim_f(l, m, m, n), trim_f(m, n, n, l)), 0L, null);
        return true;
    }

    /**
 * Constructs an elimination term using a variable and two XTerm operands.
 * Reorders the variable if necessary and computes the metric.
 *
 * @param v   the variable
 * @param p1  the first XTerm operand
 * @param p2  the second XTerm operand
 * @return the constructed elimination term
 */
    ElTerm mk_felim(Var v, XTerm p1, XTerm p2) {
        ElTerm e1 = new ElTerm();
        if (this.var_reOrder(v)) {
            p1 = neg_poly(p1);
        }
        v = this.ad_var(v);
        e1.v = v;
        e1.p1 = p1;
        e1.p2 = p2;
        e1.p = get_m(v);
        e1.co = co_db.nx;
        return (e1);
    }

    /**
     * Constructs an elimination term using a variable and two XTerm operands,
     * and scales the resulting term by a factor if necessary.
     *
     * @param v   the variable
     * @param p1  the first XTerm operand
     * @param p2  the second XTerm operand
     * @param n   the scaling factor; if not 1, the term is multiplied by this factor
     * @param t   the elimination type
     * @return the scaled elimination term
     */
    ElTerm mk_felim(Var v, XTerm p1, XTerm p2, int n, int t) {
        ElTerm e = mk_felim(v, p1, p2, t);
        if (n != 1)
            e.p = ptimes(get_n(n), e.p);
        return e;
    }

    /**
     * Constructs an elimination term using a variable and two XTerm operands,
     * and sets its elimination type.
     *
     * @param v   the variable
     * @param p1  the first XTerm operand
     * @param p2  the second XTerm operand
     * @param t   the elimination type to set
     * @return the elimination term with the specified type
     */
    ElTerm mk_felim(Var v, XTerm p1, XTerm p2, int t) {
        ElTerm el = mk_felim(v, p1, p2);
        el.etype = t;
        return el;
    }

    /**
     * Attempts the elimination procedure (query type 7) on the provided XTerm.
     * Iterates through subterms to identify a valid elimination candidate based on
     * geometric relationships.
     *
     * @param p the XTerm to process
     * @return the resulting elimination term if a valid candidate is found; null otherwise
     */
    ElTerm elim_q7(XTerm p) {
        LLine ln1, ln2, ln3, ln4;
        XTerm p1 = p;

        while (true) {
            if (p1 == null || npoly(p1)) return (null);

            DTerm ps1 = p1.ps;
            ps1 = ps1.nx;
            if (ps1 == null) return (null);

            Var v1 = p1.var;
            ln1 = fadd_ln(v1.pt[0], v1.pt[1]);
            ln2 = fadd_ln(v1.pt[2], v1.pt[3]);
            DTerm ps2 = ps1;

            while (ps2 != null) {
                XTerm p2 = ps2.p;
                if (npoly(p2))
                    break;
                Var v2 = p2.var;
                ln3 = fadd_ln(v2.pt[0], v2.pt[1]);
                ln4 = fadd_ln(v2.pt[2], v2.pt[3]);
                if (fcc(p1) == fcc(p2)) {
                    if (ln2 == ln3 && ln_less(ln4, ln2)) {
                        co_db.nx = null;
                        add_codb(CO_COLL, v1.pt[2], v1.pt[3], v2.pt[0], v2.pt[1], 0, 0, 0, 0);
                        XTerm xt = trim_f(v1.pt[0], v1.pt[1], v2.pt[2], v2.pt[3]);
                        int r = RF_ADDITION;
                        if (pzerop(xt))
                            r = 2;
                        return (mk_felim(p1.var, pminus(xt, get_m(p2.var)), get_n(1L), r));
                    }
                    if (ln1 == ln4) {
                        co_db.nx = null;
                        add_codb(CO_COLL, v1.pt[0], v1.pt[1], v2.pt[2], v2.pt[3], 0, 0, 0, 0);
                        XTerm xt = trim_f(v2.pt[0], v2.pt[1], v1.pt[2], v1.pt[3]);
                        int r = RF_ADDITION;
                        if (pzerop(xt))
                            r = 2;
                        return (mk_felim(p1.var, pminus(xt, get_m(p2.var)), get_n(1L), r));
                    }
                } else if (fcc(p1) == (-fcc(p2))) {
                    if (ln1 == ln3 && ln2 == ln4) {
                        co_db.nx = null;
                        add_codb(CO_COLL, v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1], 0, 0, 0, 0);
                        add_codb(CO_COLL, v1.pt[2], v1.pt[3], v2.pt[2], v2.pt[3], 0, 0, 0, 0);
                        return (mk_felim(p1.var, get_m(p2.var), get_n(1L), 1));
                    }
                    if (ln2 == ln4 && ln_less(ln3, ln2)) {
                        co_db.nx = null;
                        add_codb(CO_COLL, v1.pt[2], v1.pt[3], v2.pt[2], v2.pt[3], 0, 0, 0, 0);
                        XTerm xt = trim_f(v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1]);
                        int r = RF_ADDITION;
                        if (pzerop(xt))
                            r = 1;
                        return (mk_felim(p1.var, pplus(get_m(p2.var), xt), get_n(1L), r));
                    }
                    if (ln1 == ln3) {
                        co_db.nx = null;
                        add_codb(CO_COLL, v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1], 0, 0, 0, 0);
                        XTerm xt = trim_f(v2.pt[2], v2.pt[3], v1.pt[2], v1.pt[3]);
                        int r = RF_ADDITION;
                        if (pzerop(xt))
                            r = 1;
                        return (mk_felim(p1.var, pplus(get_m(p2.var), xt), get_n(1L), r));
                    }
                }
                ps2 = p2.ps;
                ps2 = ps2.nx;
            }
            ps1 = p1.ps;
            ps1 = ps1.nx;
            if (ps1 == null)
                return (null);
            p1 = ps1.p;
        }
    }

    /**
     * Attempts the elimination procedure (query type 8) on the provided XTerm.
     * Verifies collinearity of the term's sub-elements before processing.
     *
     * @param p1 the XTerm to process
     * @return the resulting elimination term if successful; null otherwise
     */
    ElTerm elim_q8(XTerm p1) {
        DTerm ps1;
        XTerm p2;
        Var v1, v2;
        LLine ln1, ln2;

        if (p1 == null || npoly(p1))
            return (null);
        v1 = p1.var;
        ln1 = fadd_ln(v1.pt[0], v1.pt[1]);
        ps1 = p1.ps;
        ps1 = ps1.nx;
        if (ps1 == null)
            return (null);
        p2 = ps1.p;
        if (npoly(p2))
            return (null);
        v2 = p2.var;
        ln2 = fadd_ln(v2.pt[0], v2.pt[1]);
        if (ln1 != ln2)
            return (null);
        {
            co_db.nx = null;
            add_codb(CO_COLL, v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1], 0, 0, 0, 0);
            return (mk_felim(p1.var, pplus(get_m(p2.var), trim_f(v2.pt[2], v2.pt[3], v1.pt[2], v1.pt[3])), get_n(1L), RF_ADDITION));
        }
    }

    /**
     * Processes an XTerm by applying modulus to its coefficient.
     * Traverses subterms until a non-polynomial term is encountered and applies the modulus.
     *
     * @param p the XTerm to process
     * @return the processed XTerm with its coefficient modified
     */
    XTerm fpoly(XTerm p) {
        DTerm ps1, ps2;
        XTerm p1, p2;
        if (npoly(p)) {
            p.c = num_modt(p.c);
            return (p);
        }
        p1 = p;

        while (true) {
            ps1 = p1.ps;
            ps2 = ps1.nx;
            if (ps2 == null)
                return (p);
            p2 = ps2.p;
            if (!npoly(p2))
                p1 = p2;
            else
                break;
        }

        p2.c = num_modt(p2.c);
        if (pzerop(p2)) {
            put_p(p2);
            put_d(ps2);
            ps1.nx = null;
        }
        return (p);
    }

    /**
     * Constructs an elimination term for cyclic configurations.
     * Sets up conditions based on collinearity and cyclic properties.
     *
     * @param v   the variable associated with the term
     * @param a   first parameter for collinearity
     * @param b   second parameter for collinearity
     * @param c   third parameter for collinearity
     * @param d   fourth parameter for collinearity
     * @param o   origin or reference parameter
     * @param p1  first point parameter
     * @param p2  second point parameter
     * @param o1  additional reference parameter
     * @return the elimination term constructed for the cyclic case
     */
    ElTerm mk_felim11(Var v, int a, int b, int c, int d, int o, int p1, int p2, int o1) {
        co_db.nx = null;

        add_codb(CO_CYCLIC, 0, o, o1, p1, p2, 0, 0, 0);
        Cond c1 = add_codb(CO_COLL, o, p1, a, b, 0, 0, 0, 0);
        Cond c2 = add_codb(CO_COLL, o, p2, c, d, 0, 0, 0, 0);

        ElTerm el = (mk_felim(v, trim_f(o1, p1, o1, p2), get_n(1L), RF_INSCRIBE));
        co_db.nx = null;

        return el;
    }

    /**
     * Constructs an elimination term for parallel configurations.
     * Registers the parallel condition before creating the elimination term.
     *
     * @param v   the variable associated with the term
     * @param a   first parameter for the parallel condition
     * @param b   second parameter for the parallel condition
     * @param c   third parameter for the parallel condition
     * @param d   fourth parameter for the parallel condition
     * @return the elimination term representing the parallel condition
     */
    ElTerm mk_felim6(Var v, int a, int b, int c, int d) {
        co_db.nx = null;
        add_codb(CO_PARA, a, b, c, d, 0, 0, 0, 0);
        ElTerm e1 = mk_felim(v, get_n(0L), get_n(1L), 3);
        return e1;
    }

    /**
     * Constructs an elimination term for a perpendicular configuration.
     *
     * @param v the variable associated with the term
     * @param a first parameter for the perpendicular configuration
     * @param b second parameter for the perpendicular configuration
     * @param c third parameter for the perpendicular configuration
     * @param d fourth parameter for the perpendicular configuration
     * @return the elimination term constructed for the perpendicular configuration
     */
    ElTerm mk_felim7(Var v, int a, int b, int c, int d) {
        co_db.nx = null;
        add_codb(CO_PERP, a, b, c, d, 0, 0, 0, 0);
        return mk_felim(v, get_n(1L), get_n(1L), 4);
    }

    /**
     * Attempts to eliminate a geometric term using various strategies.
     *
     * @param v the variable associated with the term
     * @return the resulting elimination term if a strategy succeeds; otherwise, null
     */
    ElTerm elim_f(Var v) {
        ElTerm e1 = null;
        int a, b, c, d;

        a = v.pt[0];
        b = v.pt[1];
        c = v.pt[2];
        d = v.pt[3];
        if (xpara(a, b, c, d)) {
            e1 = mk_felim6(v, a, b, c, d);
        } else if (xperp(a, b, c, d)) {
            e1 = mk_felim7(v, a, b, c, d);
        } else if ((e1 = elim_f_pn(v, a, b, c, d)) != null) {
        } else if ((e1 = elim_f_tn(v, a, b, c, d)) != null) {
        } else {
            ElTerm e2 = null;
            e2 = elim_f_cir1(v, a, b, c, d);
            if (e2 != null && e2.etype != 14)
                e1 = e2;
            else {
                e1 = elim_f_cir2(v, a, b, c, d);
            }
            if (e1 == null)
                e1 = elim_f_cir3(v, a, b, c, d);
            if (e1 == null)
                e1 = elim_f_cir4(v, a, b, c, d);
            if (e1 == null)
                e1 = e2;
        }
        if (e1 == null) {
            if ((e1 = elim_f_center(v, a, b, c, d)) != null) {
            } else if ((e1 = elim_f_ans(v, a, b, c, d)) != null) {
            } else if ((e1 = elim_f_ln(v, a, b, c, d)) != null) {
            } else
                e1 = null;
        }

        return (e1);
    }

    /**
     * Processes an elimination based on a line configuration.
     *
     * @param v the variable associated with the term
     * @param a first geometric parameter
     * @param b second geometric parameter
     * @param c third geometric parameter
     * @param d fourth geometric parameter
     * @return the elimination term constructed from the line configuration; null if not applicable
     */
    ElTerm elim_f_ln(Var v, int a, int b, int c, int d) {
        LLine ln1 = fd_ln(a, b);
        if (ln1 != null && a > ln1.pt[1]) {
            co_db.nx = null;
            add_codb(CO_COLL, ln1.pt[0], ln1.pt[1], a, b, 0, 0, 0, 0);
            return (mk_felim(v, trim_f(ln1.pt[0], ln1.pt[1], c, d), get_n(1L), 1));
        }
        return (null);
    }

    /**
     * Performs elimination based on a parallel line configuration.
     *
     * @param v the variable associated with the term
     * @param a first geometric parameter
     * @param b second geometric parameter
     * @param c third geometric parameter
     * @param d fourth geometric parameter
     * @return the elimination term constructed from the parallel line configuration; null if not found
     */
    ElTerm elim_f_pn(Var v, int a, int b, int c, int d) {
        LLine ln1 = fd_ln(a, b);
        PLine pn1 = fd_pn(a, b);
        if (pn1 == null) return (null);
        for (int i = 0; i <= pn1.no; i++) {
            LLine ln2 = pn1.ln[i];
            if (ln_less(ln2, ln1)) {
                co_db.nx = null;
                add_codb(CO_PARA, a, b, ln2.pt[0], ln2.pt[1], 0, 0, 0, 0);
                Var v1 = new Var(10, a, b, ln2.pt[0], ln2.pt[1]);
                ElTerm e1 = this.mk_felim6(v1, a, b, ln2.pt[0], ln2.pt[1]);
                co_db.nx = null;
                ElTerm e = (mk_felim(v, trim_f(ln2.pt[0], ln2.pt[1], c, d), get_n(1L), 1));
                e.et = e1;
                return e;
            }
        }
        return (null);
    }

    /**
     * Performs elimination based on a tn-line configuration.
     *
     * @param v the variable associated with the term
     * @param a first geometric parameter
     * @param b second geometric parameter
     * @param c third geometric parameter
     * @param d fourth geometric parameter
     * @return the elimination term constructed from the tn-line configuration; null if not applicable
     */
    ElTerm elim_f_tn(Var v, int a, int b, int c, int d) {
        LLine ln2;
        LLine ln1 = fd_ln(a, b);
        TLine tn1 = fd_tn(ln1);
        //        if (tn1 == null) return (null);
        if (tn1 != null) {
            if (tn1.l1 == ln1)
                ln2 = tn1.l2;
            else
                ln2 = tn1.l1;
            if (ln_less(ln2, ln1)) {
                co_db.nx = null;
                ElTerm e1 = mk_felim(v, pplus(trim_f(a, b, ln2.pt[0], ln2.pt[1]), trim_f(ln2.pt[0], ln2.pt[1], c, d)), get_n(1L), RF_ADDITION);
                add_codb(CO_PERP, a, b, ln2.pt[0], ln2.pt[1], 0, 0, 0, 0);
                Var v1 = new Var(10, a, b, ln2.pt[0], ln2.pt[1]);
                ElTerm e2 = this.mk_felim7(v1, a, b, ln2.pt[0], ln2.pt[1]);
                e1.nx = e2;
                co_db.nx = null;
                ElTerm e = (mk_felim(v, pplus(trim_f(ln2.pt[0], ln2.pt[1], c, d), get_n(1L)), get_n(1L), RF_PERP_SPLIT));
                e.et = e1;
                return e;
            }
        } else if (a == c) {
            ln1 = fd_ln(c, d);
            tn1 = fd_tn(ln1);
            if (tn1 != null) {
                if (tn1.l1 == ln1)
                    ln2 = tn1.l2;
                else
                    ln2 = tn1.l1;

                if (ln_less(ln2, ln1)) {
                    co_db.nx = null;
                    ElTerm e1 = mk_felim(v, pplus(trim_f(a, b, ln2.pt[0], ln2.pt[1]), trim_f(ln2.pt[0], ln2.pt[1], c, d)), get_n(1L), RF_ADDITION);
                    add_codb(CO_PERP, c, d, ln2.pt[0], ln2.pt[1], 0, 0, 0, 0);
                    Var v1 = new Var(10, c, d, ln2.pt[0], ln2.pt[1]);
                    ElTerm e2 = this.mk_felim7(v1, c, d, ln2.pt[0], ln2.pt[1]);
                    e1.nx = e2;
                    co_db.nx = null;
                    ElTerm e = (mk_felim(v, pplus(trim_f(a, b, ln2.pt[0], ln2.pt[1]), get_n(1L)), get_n(1L), RF_PERP_SPLIT));
                    e.et = e1;
                    return e;
                }
            }
        }
        return (null);
    }

    /**
     * Processes the cyclic configuration for elimination.
     *
     * @param v the variable associated with the term
     * @param a first geometric parameter
     * @param b second geometric parameter
     * @param c third geometric parameter
     * @param d fourth geometric parameter
     * @return the elimination term constructed from the cyclic configuration; null if not applicable
     */
    ElTerm elim_f_cir1(Var v, int a, int b, int c, int d) {
        int o, p1, p2, p3, p4;
        LLine ln3, ln4, ln5, ln6;

        LLine ln1 = fadd_ln(a, b);
        LLine ln2 = fadd_ln(c, d);
        ACir cr = all_cir.nx;
        while (cr != null)           //R11
        {
            if (cr.type == 0) {
                cr = cr.nx;
                continue;
            }
            o = inter_ll(ln1, ln2);
            p1 = inter_lc1(ln1, cr, o);
            p2 = inter_lc1(ln2, cr, o);
            if (o != 0 && p1 != 0 && p2 != 0 && on_cir(o, cr)) {
                for (int i = 0; i <= cr.no; i++) {
                    p3 = cr.pt[i];
                    if (p3 != o && p3 != p1 && p3 != p2 &&
                            ln_less((ln3 = fadd_ln(p3, p1)), ln1) &&
                            ln_less((ln4 = fadd_ln(p3, p2)), ln1)) {
                        return mk_felim11(v, a, b, c, d, o, p1, p2, p3);
                    }
                }
            }

            p1 = inter_lc(ln1, cr);
            p2 = inter_lc1(ln1, cr, p1);
            if (p1 == 0 || p2 == 0) {
                cr = cr.nx;
                continue;
            }

            ////////////////////////////////////////////////////////////////////////////////////
            ElTerm rel = null;

            for (int i = 0; i < cr.no; i++)
                for (int j = 0; j <= cr.no; j++) {
                    if (i == j) continue;
                    p3 = cr.pt[i];
                    p4 = cr.pt[j];

                    if (p3 != p1 && p3 != p2 && p4 != p1 && p4 != p2) {
                        ln3 = fadd_ln(p3, p1);
                        ln4 = fadd_ln(p3, p4);
                        ln5 = fadd_ln(p4, p2);
                        int tp1 = inter_ll(ln1, ln5);
                        int tp2 = inter_ll(ln2, ln5);

                        if (ln_less(ln3, ln1) && ln_less(ln4, ln1) && ln_less(ln5, ln1))// R12.
                        {
                            ElTerm e1 = mk_felim(v, pplus(trim_f(a, b, p2, p4), trim_f(p2, p4, c, d)), get_n(1L), RF_ADDITION);
                            Var v1 = new Var(10, a, b, p2, p4);
                            ElTerm e2 = this.mk_felim11(v1, a, b, p2, p4, p2, p1, p4, p3);
                            e1.nx = e2;
                            co_db.nx = null;
                            ElTerm el = (mk_felim(v, pplus(trim_f(p3, p1, p3, p4), trim_f(p4, p2, c, d)), get_n(1L), RF_9));
                            el.et = e1;
                            if (tp1 != 0 && tp2 != 0)
                                return el;
                            else if (rel == null)
                                rel = el;

                        } else if (ln_less(ln3, ln1) && ln_less(ln4, ln1)) {
                            ln6 = all_ln.nx;
                            while (ln6 != null) {
                                if (ln_para(ln6, ln5) && ln_less(ln6, ln1)) {
                                    ElTerm e1 = mk_felim(v, pplus(trim_f(a, b, p2, p4), trim_f(p2, p4, c, d)), get_n(1L), RF_ADDITION);
                                    Var v2 = new Var(10, a, b, p2, p4);
                                    ElTerm e2 = this.mk_felim11(v2, a, b, p2, p4, p2, p1, p4, p3);
                                    co_db.nx = null;
                                    Var v3 = new Var(10, p4, p2, ln6.pt[0], ln6.pt[1]);
                                    ElTerm e3 = this.mk_felim6(v3, p4, p2, ln6.pt[0], ln6.pt[1]);
                                    e1.nx = e2;
                                    e2.nx = e3;
                                    co_db.nx = null;
                                    ElTerm el = (mk_felim(v, pplus(trim_f(p3, p1, p3, p4), trim_f(ln6.pt[0], ln6.pt[1], c, d)), get_n(1L), RF_10));
                                    el.et = e1;
                                    return el;
                                }
                                ln6 = ln6.nx;
                            }
                            ln6 = all_ln.nx;
                            while (ln6 != null) {
                                if (ln_perp(ln6, ln5) && ln_less(ln6, ln1)) {
                                    ElTerm e1 = mk_felim(v, pplus(trim_f(a, b, p2, p4), trim_f(p2, p4, c, d)), get_n(1L), RF_ADDITION);
                                    Var v2 = new Var(10, a, b, p2, p4);
                                    ElTerm e2 = this.mk_felim11(v2, a, b, p2, p4, p2, p1, p4, p3);
                                    co_db.nx = null;
                                    Var v3 = new Var(10, p4, p2, ln6.pt[0], ln6.pt[1]);
                                    ElTerm e3 = this.mk_felim7(v3, p4, p2, ln6.pt[0], ln6.pt[1]);
                                    e1.nx = e2;
                                    e2.nx = e3;
                                    co_db.nx = null;
                                    ElTerm el = mk_felim(v, pplus3(trim_f(p3, p1, p3, p4), trim_f(ln6.pt[0], ln6.pt[1], c, d), get_n(1L)), get_n(1L), RF_DM_PERP);
                                    el.et = e1;
                                    return el;
                                }
                                ln6 = ln6.nx;
                            }
                        }
                    }
                }
                if (rel != null) return rel;
                l1:
                cr = cr.nx;
            }
            return (null);
        }

    /**
     * Processes the cyclic configuration (version 2) for elimination.
     *
     * @param v the variable associated with the term
     * @param a first geometric parameter
     * @param b second geometric parameter
     * @param c third geometric parameter
     * @param d fourth geometric parameter
     * @return the elimination term constructed from the second cyclic configuration; null if not applicable
     */
    ElTerm elim_f_cir2(Var v, int a, int b, int c, int d) {
        ACir cr1, cr2;
        int p1, p2, p3, p4;
        LLine ln3, ln4, ln5, ln6;

        LLine ln1 = fadd_ln(a, b);
        LLine ln2 = fadd_ln(c, d);
        int o = inter_ll(ln1, ln2);
        if (o == 0) return (null);
        cr1 = all_cir.nx;
        while (cr1 != null) {
            if (cr1.type == 0) {
                cr1 = cr1.nx;
                continue;
            }

            if (!on_cir(o, cr1)) {
                cr1 = cr1.nx;
                continue;
            }
            p1 = inter_lc1(ln1, cr1, o);
            if (p1 == 0) {
                cr1 = cr1.nx;
                continue;
            }
            cr2 = all_cir.nx;
            while (cr2 != null) {
                if (cr2.type == 0) {
                    cr2 = cr2.nx;
                    continue;
                }
                if (!on_cir(o, cr2)) {
                    cr2 = cr2.nx;
                    continue;
                }
                p2 = inter_lc1(ln2, cr2, o);
                if (p2 == 0) {
                    cr2 = cr2.nx;
                    continue;
                }

                for (int i = 0; i <= cr1.no; i++)
                    for (int j = 0; j <= cr2.no; j++) {
                        if (cr1.pt[i] != o && cr1.pt[i] != p1 && cr2.pt[j] != o && cr2.pt[j] != p2 && xcoll(o, cr1.pt[i], cr2.pt[j]) &&
                                (p3 = get_cpt3(cr1, o, p1, cr1.pt[i])) != 0 && (p4 = get_cpt3(cr2, o, p2, cr2.pt[j])) != 0) {
                            ln3 = fadd_ln(p3, p1);
                            ln4 = fadd_ln(p3, cr1.pt[i]);
                            ln5 = fadd_ln(p4, p2);
                            ln6 = fadd_ln(p4, cr2.pt[j]);
                            if (l2_less(ln3, ln4, ln1, ln2) && l2_less(ln5, ln6, ln1, ln2)) {
                                co_db.nx = null;
                                add_codb(CO_CYCLIC, 0, o, p2, cr2.pt[j], p4, 0, 0, 0);
                                add_codb(CO_CYCLIC, 0, o, p1, cr1.pt[i], p3, 0, 0, 0);
                                add_codb(CO_COLL, o, cr1.pt[i], cr2.pt[j], 0, 0, 0, 0, 0);
                                return (mk_felim(v, pminus(trim_f(p3, p1, p3, cr1.pt[i]), trim_f(p4, p2, p4, cr2.pt[j])), get_n(1L), RF_12));
                            }
                        }
                    }
                cr2 = cr2.nx;
            }
            cr1 = cr1.nx;
        }
        return (null);
    }

    /**
     * Performs elimination based on a cyclic circle configuration (variant 3).
     *
     * This method searches through cyclic configurations in the circle list and
     * attempts to form an elimination term based on inter-line relationships.
     *
     * @param v the variable associated with the elimination term
     * @param a the first geometric parameter
     * @param b the second geometric parameter
     * @param c the third geometric parameter
     * @param d the fourth geometric parameter
     * @return the elimination term constructed from the cyclic configuration or null if not applicable
     */
    ElTerm elim_f_cir3(Var v, int a, int b, int c, int d) {
        ACir cr1;
        int o, p1, p2, p3, p4;
        LLine ln1, ln2, ln3, ln4;
        int i, j;

        ln1 = fadd_ln(a, b);
        ln2 = fadd_ln(c, d);
        for (cr1 = all_cir.nx; cr1 != null; cr1 = cr1.nx) {
            if (cr1.type == 0) continue;
            o = cr1.o;
            if (o == 0) continue;
            if (!on_ln(o, ln1)) continue;
            p1 = inter_lc(ln1, cr1);
            if (p1 == 0) continue;

            if (o == inter_ll(ln1, ln2)) {
                p2 = inter_lc(ln2, cr1);
                if (p2 != 0) {
                    for (i = 0; i <= cr1.no; i++) {
                        p3 = cr1.pt[i];
                        if (p3 != p1 && p3 != p2 &&
                                ln_less((ln3 = fadd_ln(p3, p1)), ln1) &&
                                ln_less((ln4 = fadd_ln(p3, p2)), ln1)) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);
                            return (mk_felim(v, ptimes(get_n(2L), trim_f(p3, p1, p3, p2)), get_n(1L), RF_13));      //R23
                        }
                    }
                    ln3 = fadd_ln(p1, p2);
                    if (ln_less(ln3, ln1)) {
                        co_db.nx = null;
                        add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                        return (mk_felim(v, ptimes(get_n(2L), trim_f(p1, p2, c, d)), get_n(1L), RF_14));       //r24
                    }
                }
            } else if (on_ln(p1, ln2)) {                                                                          //r25
                p2 = inter_lc1(ln2, cr1, p1);
                if (p2 != 0 && ln_less(fadd_ln(o, p2), ln1)) {
                    co_db.nx = null;
                    //add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    add_codb(CO_CONG, o, p1, o, p2, 0, 0, 0, 0);
                    return (mk_felim(v, trim_f(p1, p2, o, p2), get_n(1L), RF_ISO));
                }
            }
            for (i = 0; i < cr1.no; i++)
                for (j = i + 1; j <= cr1.no; j++) {
                    p2 = cr1.pt[i];
                    p3 = cr1.pt[j];
                    if (p2 != p1 && p3 != p1) {
                        if (xcoll(p1, c, d) && xcoll(p3, c, d)) {
                            p4 = p2;
                            p2 = p3;
                            p3 = p4;
                        }
                        ln2 = fadd_ln(p1, p2);
                        ln3 = fadd_ln(p3, p1);
                        ln4 = fadd_ln(p3, p2);
                        if (ln_less(ln2, ln1) && ln_less(ln3, ln1) && ln_less(ln4, ln1)) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);
                            return (mk_felim(v, pplus3(trim_f(p3, p1, p3, p2), trim_f(ln2.pt[0], ln2.pt[1], c, d), get_n(1L)), get_n(1L), RF_16));
                        }
                    }
                }
        }
        return (null);
    }

    /**
     * Performs elimination based on a cyclic circle configuration (variant 4).
     *
     * This method searches through cyclic configurations in the circle list and
     * attempts to form an elimination term using midpoint and line intersection strategies.
     *
     * @param v the variable associated with the elimination term
     * @param a the first geometric parameter
     * @param b the second geometric parameter
     * @param c the third geometric parameter
     * @param d the fourth geometric parameter
     * @return the elimination term constructed from the cyclic configuration or null if not applicable
     */
    ElTerm elim_f_cir4(Var v, int a, int b, int c, int d) {
        ACir cr1;
        int o, p1, p2, p3, p4;
        LLine ln1;
        ln1 = fadd_ln(a, b);
        cr1 = all_cir.nx;
        while (cr1 != null) {
            if (cr1.type == 0) {
                cr1 = cr1.nx;
                continue;
            }

            o = cr1.o;
            if (o == 0 || o > a) {
                cr1 = cr1.nx;
                continue;
            }
            p1 = inter_lc(ln1, cr1);
            p2 = inter_lc1(ln1, cr1, p1);
            if (p1 == 0 || p2 == 0) {
                cr1 = cr1.nx;
                continue;
            }

            for (int i = 0; i <= cr1.no; i++)
                if (cr1.pt[i] != p1 && cr1.pt[i] != p2) {
                    p3 = cr1.pt[i];
                    p4 = fd_pt_md(p1, p3);
                    if (p4 != 0 && o != p4 &&
                            ln_less((fadd_ln(p1, o)), ln1) &&
                            ln_less((fadd_ln(o, p4)), ln1) &&
                            ln_less((fadd_ln(p2, p3)), ln1)) {
                        co_db.nx = null;
                        add_codb(CO_MIDP, p4, p1, p3, 0, 0, 0, 0, 0);
                        add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);
                        return (mk_felim(v, pplus(trim_f(o, p1, o, p4), trim_f(p2, p3, c, d)), get_n(1L), RF_17));       //r27
                    }
                    if (p4 != 0 && o != p4 &&
                            ln_less((fadd_ln(p3, o)), ln1) &&
                            ln_less((fadd_ln(o, p4)), ln1) &&
                            ln_less((fadd_ln(p2, p3)), ln1)) {
                        co_db.nx = null;
                        add_codb(CO_MIDP, p4, p1, p3, 0, 0, 0, 0, 0);
                        add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);
                        return (mk_felim(v, pplus(trim_f(o, p4, o, p3), trim_f(p2, p3, c, d)), get_n(1L), RF_17));
                    }
                    if (ln_less((fadd_ln(p1, o)), ln1) &&
                            ln_less((fadd_ln(p1, p3)), ln1) &&
                            ln_less((fadd_ln(p2, p3)), ln1)) {
                        co_db.nx = null;
                        add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);      //r33
                        return (mk_felim(v, pplus3(trim_f(p1, o, p1, p3), trim_f(p2, p3, c, d), get_n(1L)),
                                get_n(1L), RF_18));
                    }
                    if (ln_less((fadd_ln(p3, o)), ln1) &&
                            ln_less((fadd_ln(p1, p3)), ln1) &&
                            ln_less((fadd_ln(p2, p3)), ln1)) {
                        co_db.nx = null;
                        add_codb(CO_CYCLIC, o, p1, p2, p3, 0, 0, 0, 0);
                        return (mk_felim(v, pplus3(trim_f(p3, p1, p3, o), trim_f(p2, p3, c, d), get_n(1L)),
                                get_n(1L), RF_18));
                    }
                }
            m1:
            cr1 = cr1.nx;
        }
        return (null);
    }

    /**
     * Performs elimination based on center configurations.
     *
     * This method iterates through line points and configuration constraints to
     * determine centers (such as orthocenters or incenters) for constructing an elimination term.
     *
     * @param v the variable associated with the elimination term
     * @param a the first geometric parameter
     * @param b the second geometric parameter
     * @param c the third geometric parameter
     * @param d the fourth geometric parameter
     * @return the elimination term constructed from center-based elimination or null if not applicable
     */
    ElTerm elim_f_center(Var v, int a, int b, int c, int d) {
        LLine ln1, ln2;
        int p1, p2;
        char i, j, k, l;

        ln1 = fd_ln(a, b);
        for (i = 0; i <= ln1.no; i++)
            for (j = 0; j <= ln1.no; j++) {
                p1 = ln1.pt[i];
                p2 = ln1.pt[j];
                if (p1 == p2) {
                    k = 1;
                    continue;
                }

                for (k = 1; k <= cons_no; k++)
                    for (l = 1; l <= cons_no; l++) {
                        // orthocenter k p1 p2 l
                        if (k < l && p1 < p2 && k != p1 && k != p2 && l != p1 && l != p2 &&
                                xperp(p1, k, p2, l) && xperp(p2, k, p1, l) &&
                                ln_less((fadd_ln(k, l)), ln1)) {
                            co_db.nx = null;
                            add_codb(CO_ORTH, k, p1, p2, l, 0, 0, 0, 0);
                            return (mk_felim(v, pplus(trim_f(k, l, c, d), get_n(1L)), get_n(1L), RF_ORTH));
                        }

                        /* incenter (p1) p2 k l */
                        /* gprint("cen1: %s %s %s %s\r\n",ANAME(p1),ANAME(p2),ANAME(k),ANAME(l)); */
                        if (k < l && k != p1 && k != p2 && l != p1 && l != p2 &&
                                xacong(k, l, p1, p1, l, p2) && xacong(l, k, p1, p1, k, p2) &&
                                ln_less((fadd_ln(k, p2)), ln1) &&
                                ln_less((fadd_ln(k, l)), ln1) &&
                                ln_less((fadd_ln(p1, k)), ln1) &&
                                ln_less((fadd_ln(p1, l)), ln1)) {
                            co_db.nx = null;
                            add_codb(CO_INCENT, p1, p2, k, l, 0, 0, 0, 0);
                            return (mk_felim(v, pplus4(trim_f(k, l, k, p1),
                                    trim_f(l, p1, l, k),
                                    trim_f(p2, k, c, d),
                                    get_n(1L)),
                                    get_n(1L), RF_20));
                        }
                    }
                m1:
                k = 1;
            }
        return (null);
    }

    /**
     * Performs elimination based on angle configurations.
     *
     * This method processes the angle and congruence relationships to form an elimination term.
     *
     * @param v the variable associated with the elimination term
     * @param a the first geometric parameter
     * @param b the second geometric parameter
     * @param c the third geometric parameter
     * @param d the fourth geometric parameter
     * @return the elimination term constructed from angle-based configurations or null if not applicable
     */
    ElTerm elim_f_ans(Var v, int a, int b, int c, int d) {
        LLine l1, l2, ln0, ln1, ln2;
        Angles as;

        l1 = fadd_ln(a, b);
        l2 = fadd_ln(c, d);

        for (as = all_as.nx; as != null; as = as.nx) {
            if (as.type == 0) continue;
            if ((l1 == as.l1) && (l2 == as.l2)) {
                ln1 = as.l3;
                ln2 = as.l4;
            } else if ((l1 == as.l2) && (l2 == as.l1)) {
                ln1 = as.l4;
                ln2 = as.l3;
            } else if ((l1 == as.l1) && (l2 == as.l3)) {
                ln1 = as.l2;
                ln2 = as.l4;
            } else if ((l1 == as.l3) && (l2 == as.l1)) {
                ln1 = as.l4;
                ln2 = as.l2;
            } else if ((l1 == as.l2) && (l2 == as.l4)) {
                ln1 = as.l1;
                ln2 = as.l3;
            } else if ((l1 == as.l4) && (l2 == as.l2)) {
                ln1 = as.l3;
                ln2 = as.l1;
            } else if ((l1 == as.l3) && (l2 == as.l4)) {
                ln1 = as.l1;
                ln2 = as.l2;
            } else if ((l1 == as.l4) && (l2 == as.l3)) {
                ln1 = as.l2;
                ln2 = as.l1;
            } else
                continue;

            if (ln_less(ln1, l1) && ln_less(ln2, l1)) {
                co_db.nx = null;
                add_codb(CO_ACONG, a, b, c, d, ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1]);
                return (mk_felim(v, trim_fl(ln1, ln2), get_n(1L), RF_21));
            }
        }
        for (as = all_as.nx; as != null; as = as.nx) {
            if (l1 == as.l1 && ln_less(as.l2, l1) && ln_less(as.l3, l1) && ln_less(as.l4, l1)) {
                ln0 = as.l2;
                ln1 = as.l3;
                ln2 = as.l4;
            } else if (l1 == as.l2 && ln_less(as.l1, l1) && ln_less(as.l3, l1) && ln_less(as.l4, l1)) {
                ln0 = as.l1;
                ln1 = as.l4;
                ln2 = as.l3;
            } else if (l1 == as.l3 && ln_less(as.l1, l1) && ln_less(as.l2, l1) && ln_less(as.l4, l1)) {
                ln0 = as.l4;
                ln1 = as.l1;
                ln2 = as.l2;
            } else if (l1 == as.l4 && ln_less(as.l1, l1) && ln_less(as.l2, l1) && ln_less(as.l3, l1)) {
                ln0 = as.l3;
                ln1 = as.l2;
                ln2 = as.l1;
            } else
                continue;

            co_db.nx = null;
            add_codb(CO_ACONG, a, b, ln0.pt[0], ln0.pt[1],
                    ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1]);
            return (mk_felim(v, pplus(trim_fl(ln0, l2), trim_fl(ln1, ln2)), get_n(1L), 0));   //?????
        }
        return (null);
    }

    /**
     * Performs elimination based on combined geometric configurations involving circles and lines.
     *
     * <p>
     * This method takes a variable containing four geometric points and attempts to construct an elimination
     * term by evaluating a series of geometric constraints. It uses helper methods such as
     * <code>inter_lc</code>, <code>ln_less</code>, <code>on_ln</code>, <code>on_cir</code>, <code>xperp</code>,
     * and <code>xpara</code> to verify perpendicular, parallel, and cyclic conditions among lines and circles.
     * The method iterates through the circle lists and applies different elimination strategies.
     * When a valid geometric configuration is detected, it creates and returns the corresponding elimination term.
     * Otherwise, it returns <code>null</code>.
     * </p>
     *
     * @param v the variable containing four geometric points used to derive lines and circles
     * @return the constructed elimination term if a valid configuration is identified; <code>null</code> otherwise
     */
    ElTerm elim_d(Var v) {
        LLine ln1, ln2;
        ACir cr, cr1;
        int o, p1, p2, p3, p4, p5, a, b, c, d;

        a = v.pt[0];
        b = v.pt[1];
        c = v.pt[2];
        d = v.pt[3];
        ln1 = fadd_ln(v.pt[0], v.pt[1]);
        ln2 = fadd_ln(v.pt[2], v.pt[3]);
        cr = all_cir.nx;
        while (cr != null) {
            if (cr.type == 0) {
                cr = cr.nx;
                continue;
            }

            o = cr.o;
            if (o == 0) {
                cr = cr.nx;
                continue;
            }
            /* gprint("45 du\r\n"); */
            p1 = inter_lc(ln1, cr);
            if (p1 != 0 && on_ln(o, ln1)) {
                if (on_ln(p1, ln2) && (p2 = inter_lc1(ln2, cr, p1)) != 0 && xperp(o, p1, o, p2)) {
                    co_db.nx = null;
                    add_codb(CO_PERP, o, p1, o, p2, 0, 0, 0, 0);
                    add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    return (mk_felim(v, get_n(1L), get_n(2L), RF_22));
                }
                for (int i = 0; i <= cr.no; i++) {
                    p2 = cr.pt[i];
                    if (xperp(o, p1, o, p2) && ln_less(fadd_ln(p1, p2), ln1)) {
                        co_db.nx = null;
                        add_codb(CO_CYCLIC, o, p1, o, p2, 0, 0, 0, 0);
                        add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                        return (mk_felim(v, pplus(ptimes(get_n(2L), trim_f(p1, p2, c, d)),
                                get_n(1L)),
                                get_n(2L), RF_TT));
                    }
                }
            }

            /* gprint("isoceles \r\n"); */
            p1 = inter_lc(ln1, cr);
            p2 = inter_lc1(ln1, cr, p1);
            if (p1 != 0 && p2 != 0 &&
                    ln_less(fadd_ln(o, p1), ln1) && ln_less(fadd_ln(o, p2), ln1))
                if (xpara(o, p1, c, d)) {
                    co_db.nx = null;
                    add_codb(CO_PARA, o, p1, c, d, 0, 0, 0, 0);
                    add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    return (mk_felim(v, trim_f(o, p2, o, p1), get_n(2L), RF_TT2));
                } else {
                    co_db.nx = null;
                    add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    return (mk_felim(v, pplus(trim_f(o, p1, o, p2),
                            ptimes(get_n(2L), trim_f(o, p2, c, d))),
                            get_n(2L), RF_PPO));
                }

            /* gprint("iso + cir \r\n"); */
            if (!on_ln(o, ln1)) {
                cr = cr.nx;
                continue;
            }

            cr1 = all_cir.nx;
            while (cr1 != null) {
                if (cr.type == 0) {
                    cr = cr.nx;
                    continue;
                }
                p1 = inter_cc(cr, cr1);
                p2 = inter_cc1(cr, cr1, p1);
                if (p1 == 0 || p2 == 0) {
                    cr1 = cr1.nx;
                    continue;
                }
                if (!on_cir(o, cr1)) {
                    cr1 = cr1.nx;
                    continue;
                }
                p3 = inter_lc1(ln1, cr1, o);
                if (p3 == 0) {
                    cr1 = cr1.nx;
                    continue;
                }
                p4 = 0;
                if (on_ln(p3, ln2)) p4 = inter_lc1(ln2, cr1, p3);

                if (p4 == p1 && xperp(p3, p1, p3, p2)) {
                    co_db.nx = null;
                    add_codb(CO_PERP, p3, p1, p3, p2, 0, 0, 0, 0);
                    add_codb(CO_CYCLIC, 0, o, p1, p2, p3, 0, 0, 0);
                    add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                    add_codb(CO_COLL, c, d, p1, 0, 0, 0, 0, 0);
                    return (mk_felim(v, get_n(1L), get_n(2L), RF_26));
                } else if (p4 == p2 && xperp(p3, p1, p3, p2)) {
                    co_db.nx = null;
                    add_codb(CO_PERP, p3, p1, p3, p2, 0, 0, 0, 0);
                    add_codb(CO_CYCLIC, 0, o, p1, p2, p3, 0, 0, 0);
                    add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                    add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                    add_codb(CO_COLL, c, d, p2, 0, 0, 0, 0, 0);
                    return (mk_felim(v, get_n(1L), get_n(2L), RF_26));
                }

                /* gprint("(iso + cir) double \r\n"); */
                for (int i = 0; i <= cr1.no; i++) {
                    p5 = cr1.pt[i];
                    if (p5 != p1 && p5 != p2 &&
                            ln_less(fadd_ln(p5, p1), ln1) &&
                            ln_less(fadd_ln(p5, p2), ln1)) {
                        if (p4 == p1) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, 0, o, p1, p2, p3, p5, 0, 0);
                            add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                            add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                            add_codb(CO_COLL, c, d, p1, 0, 0, 0, 0, 0);
                            return (mk_felim(v, trim_f(p5, p2, p5, p1), get_n(2L), RF_DM2));
                        } else if (p4 == p2) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, 0, o, p1, p2, p3, p5, 0, 0);
                            add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                            add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                            add_codb(CO_COLL, c, d, p2, 0, 0, 0, 0, 0);
                            return (mk_felim(v, trim_f(p5, p1, p5, p2), get_n(2L), RF_DM2));
                        }
                        /* check here */
                        else if (ln_less(fadd_ln(p3, p1), ln1)) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, 0, o, p1, p2, p3, p5, 0, 0);
                            add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                            add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                            return (mk_felim(v, pplus(trim_f(p5, p2, p5, p1),
                                    ptimes(get_n(2L), trim_f(p3, p1, c, d))),
                                    get_n(2L), RF_CY));
                        }
                        /* check here */
                        else if (ln_less(fadd_ln(p3, p2), ln1)) {
                            co_db.nx = null;
                            add_codb(CO_CYCLIC, 0, o, p1, p2, p3, p5, 0, 0);
                            add_codb(CO_CYCLIC, o, p1, p2, 0, 0, 0, 0, 0);
                            add_codb(CO_COLL, o, a, b, p3, 0, 0, 0, 0);
                            return (mk_felim(v, pplus(trim_f(p5, p1, p5, p2),
                                    ptimes(get_n(2L), trim_f(p3, p2, c, d))), get_n(2L), RF_CY));
                        }
                    }
                }
                cr1 = cr1.nx;
            }
            cr = cr.nx;
        }

        Angles as = all_as.nx;
        ln2 = fd_ln(c, d);

        LLine l1, l2, l3, l4;


        while (as != null) {
            if (as.type != 0) {
                l1 = as.l1;
                l2 = as.l2;
                l3 = as.l3;
                l4 = as.l4;

                if (ln1 == l2 && ln2 == l1 || ln1 == l4 && ln2 == l3) {
                    LLine lx = l1;
                    l1 = l2;
                    l2 = lx;
                    lx = l3;
                    l3 = l4;
                    l4 = lx;
                }

                if (ln1 == l1 && ln2 == l2 || ln1 == l3 && ln2 == l4) {
                    if (l2 == l3 && ln_less(l1, l2) && ln_less(l4, l3)) {
                        add_codb(CO_ACONG, l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1], l3.pt[0], l3.pt[1], l4.pt[0], l4.pt[1]);
                        return mk_felim(v, trim_f(l1.pt[0], l1.pt[1], l4.pt[0], l4.pt[1]), get_n(2L), 2, 0);
                    } else if (l1 == l4 && ln_less(l2, l1) && ln_less(l3, l4)) {
                        add_codb(CO_ACONG, l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1], l3.pt[0], l3.pt[1], l4.pt[0], l4.pt[1]);
                        return mk_felim(v, trim_f(l3.pt[0], l3.pt[1], l2.pt[0], l2.pt[1]), get_n(2L), 2, 0);
                    }

                }
            }
            as = as.nx;
        }
        return (null);
    }

/**
 * Performs elimination based on a t-based strategy.
 *
 * <p>This method handles elimination cases that require t-based processing.
 * It applies specific geometric transformations and validations to compute
 * the corresponding elimination term.</p>
 *
 * @param v the variable containing geometric term data
 * @return the computed elimination term using the t-based strategy, or
 *         <code>null</code> if no appropriate elimination can be performed
 */
ElTerm elim_t(Var v) {
        LLine ln1, ln2;
        ACir cr;
        int p1, p2, p3;
        Angles as;

        ln1 = fadd_ln(v.pt[0], v.pt[1]);
        ln2 = fadd_ln(v.pt[2], v.pt[3]);
        p1 = inter_ll(ln1, ln2);
        if (p1 == 0) return (null);

        for (cr = all_cir.nx; cr != null; cr = cr.nx) {
            if (cr.type == 0) {
                continue;
            }
            if (cr.o != p1) continue;
            p2 = inter_lc1(ln1, cr, p1);
            p3 = inter_lc1(ln2, cr, p1);
            if (p2 == 0 || p3 == 0) continue;
            if (xcir2(p2, p1, p3) || xcir2(p3, p1, p2)) {
                co_db.nx = null;
                add_codb(CO_PET, p1, p2, p3, 0, 0, 0, 0, 0);
                return (mk_felim(v, get_n(0L), get_n(3L), RF_CY2));
            }
        }
        for (as = all_as.nx; as != null; as = as.nx) {
            if (as.sa == 60 && (onl_as(ln1, ln2, as) || onl_as(ln2, ln1, as))) {
                co_db.nx = null;
                add_codb(-(CO_ACONG), 60, v.pt[0], v.pt[1], v.pt[2], v.pt[3], 0, 0, 0);
                return (mk_felim(v, get_n(0L), get_n(3L), 0));
            }
        }
        return (null);
    }

/**
 * Performs triangle elimination.
 *
 * <p>
 * This method processes elimination based on triangle configurations.
 * It evaluates the geometric relationships between triangle vertices to
 * compute the corresponding elimination term.
 * </p>
 *
 * @param v the variable containing triangle points data
 * @return the elimination term constructed from the triangle configuration,
 *         or null if no appropriate elimination can be performed
 */
ElTerm elim_tri(Var v) {
        int a = v.pt[0];
        int b = v.pt[1];
        int c = v.pt[2];
        int d = v.pt[3];
        LLine ln1 = fadd_ln(v.pt[0], v.pt[1]);
        LLine ln2 = fadd_ln(v.pt[2], v.pt[3]);

        int o = inter_lls(ln1, ln2);
        if (o != 0) {
            for (int i = 0; i <= ln1.no; i++)
                for (int j = 0; j <= ln2.no; j++) {
                    if (ln1.pt[i] != o && ln2.pt[j] != o) {
                        if (xcong(o, ln1.pt[i], ln1.pt[i], ln2.pt[j]) && xperp(o, ln1.pt[i], ln1.pt[i], ln2.pt[j])) {
                            add_codb(CO_PERP, o, ln1.pt[i], ln1.pt[i], ln2.pt[j], 0, 0, 0, 0);
                            add_codb(CO_CONG, o, ln1.pt[i], ln1.pt[i], ln2.pt[j], 0, 0, 0, 0);
                            return mk_felim(v, get_n(1), get_n(2), 2, 0);
                        } else
                        if (xcong(o, ln2.pt[j], ln2.pt[j], ln1.pt[i]) && xperp(o, ln2.pt[j], ln2.pt[j], ln1.pt[i])) {
                            add_codb(CO_PERP, o, ln2.pt[j], ln2.pt[j], ln1.pt[i], 0, 0, 0, 0);
                            add_codb(CO_CONG, o, ln2.pt[j], ln2.pt[j], ln1.pt[i], 0, 0, 0, 0);
                            return mk_felim(v, get_n(1), get_n(2), 2, 0);
                        }
                    }
                }
        }
        return null;
    }

    /////froem area
/**
 * Performs pre-elimination computations on an XTerm using the specified elimination term.
 *
 * <p>This method applies an elimination strategy by processing the given XTerm
 * in conjunction with an elimination term. The transformation rules and constraints
 * applied within the method lead to a modified XTerm that encapsulates specific geometric
 * relationships or configurations. The exact processing is defined by the elimination
 * scheme used in the overall geometric computation.</p>
 *
 * @param p the original XTerm input for pre-elimination processing
 * @param e the elimination term that guides the computation
 * @return the resulting XTerm after processing or null if the computation is not applicable
 */
XTerm eprem(XTerm p, ElTerm e) {
        XTerm p1, p2, p3;
        if (e == null) return p;
        p2 = get_n(1L);
        if (e.p1 == null) {
            p1 = prem_var(p, e.p2, e.v);
            p3 = init_v(cp_poly(e.p2), e.v);
            if (eq_poly(p3, p2)) {
                init_deg = 0;
            }
            put_p(p3);
        } else {
            p1 = get_m(e.v);
            p1 = ptimes(p1, cp_poly(e.p2));
            p1 = pminus(p1, cp_poly(e.p1));
            p1 = prem_var(p, p1, e.v);
            if (eq_poly(e.p2, p2)) {
                init_deg = 0;
            }
        }
        put_x(p2);
        return (p1);
    }

/**
 * Returns true if a full angle proof is expressed.
 *
 * @return true if the full angle proof head exists, false otherwise.
 */
public boolean canExpressedAsFullAngle() {
    return proof.nx != null;
}

/**
 * Determines if the proof has been established as true.
 *
 * @return true if last_pr is non-null and equals zero, false otherwise.
 */
public boolean isProvedTrue() {
    if (last_pr != null && last_pr.isZero()) return true;
    return false;
}

/**
 * Retrieves the head of the full angle proof.
 *
 * This method iterates through the proof elements while printing intermediate
 * proof components. It processes both primary and linked elimination terms.
 *
 * @return the first element in the full angle proof chain, or null if none exists.
 */
public GrTerm getFullAngleProofHead() {
    GrTerm gt = proof.nx;
    if (gt == null) return null;
    while (gt != null) {
        if (gt.ps1 != null)
            myprint_p1(gt.ps1.p, true);
        ElTerm el = gt.el;
        if (el != null) {
            myprint_p1(el.p1, true);
            myprint_p1(el.p2, true);
            myprint_p1(el.p, true);
            Cond co = el.co;
            while (co != null) {
                this.show_pred(co);
                do_pred(co);
                //forw_pred(co);
                co = co.nx;
            }
        }
        if (el != null) {
            el = el.et;
            while (el != null) {
                myprint_p1(el.p1, true);
                myprint_p1(el.p2, true);
                myprint_p1(el.p, true);
                Cond co = el.co;
                while (co != null) {
                    this.show_pred(co);
                    do_pred(co);
                    // forw_pred(co);
                    co = co.nx;
                }
                el = el.nx;
            }
        }
        gt = gt.nx;
    }
    return proof.nx;
}

/**
 * Prints the proof text.
 *
 * This method iterates through all proof terms and prints their associated
 * elimination information. It assembles the printed proof text by processing
 * both display and elimination terms.
 *
 * @return true if proof text printing succeeds, false otherwise.
 */
public boolean print_prooftext()  // added   MAY 4th 2006
{
    char mk = 0;
    GrTerm gr1 = proof.nx;
    if (gr1 == null) return false;
    while (gr1 != null) {
        if (gr1.c == -1) {
            this.setPrintToString();
            DTerm dt = gr1.ps;
            print_ps(dt, mk);
            dt.text = this.getPrintedString();
        } else if (gr1.c == -2) {
        } else if (gr1.c == 0) {
        } else {
            ElTerm el = gr1.el;
            print_elims(el, mk);
        }
        if (gr1.c == 0) {
        } else {
        }
        this.setPrintToString();
        print_gr(gr1, mk);
        gr1.text = this.getPrintedString();
        gr1 = gr1.nx;
    }
    return true;
}

/**
 * Recursively prints elimination terms.
 *
 * This method prints the elimination term provided and then recursively processes
 * any linked elimination terms. It sets the printed text for each elimination element.
 *
 * @param el the elimination term to print
 * @param mk a marker character used during the printing process
 */
void print_elims(ElTerm el, char mk) {
//        gprint(Cm.s2224);
    if (el == null)
        return;
    this.setPrintToString();
    print_elim(el, mk);
    el.setText(this.getPrintedString());
    print_elims(el.et, mk);
//        gprint("\r\n");
    for (el = el.nx; el != null; el = el.nx) {
        this.setPrintToString();
        print_elim(el, mk);
        el.setText(this.getPrintedString());
        print_elims(el.et, mk);
    }
}


    static GrTerm el_gr = new GrTerm();
    static DTerm el_d1 = new DTerm();
    static DTerm el_d2 = new DTerm();

    /**
     * Prints the elimination term information.
     * <p>
     * If the elimination term is null or its associated variable has a negative index,
     * the method returns without printing. Depending on whether the elimination term has
     * a first polynomial (p1) or not, the method prints the appropriate representation.
     * </p>
     *
     * @param e  the elimination term to be printed
     * @param mk the marker character used during printing
     */
    void print_elim(ElTerm e, char mk) {
        XTerm p1, p2;
        Var v;
        if (e == null) {
            return;
        }
        v = e.v;
        if (v.nm < 0) return;

        if (e.p1 == null) {
            p2 = e.p2;
            if (pdeg(p2, e.v) == 1) {
                p1 = pminus(get_m(e.v), cp_poly(p2));
                print_var(e.v, mk);
                gprint(" = ");
                print_p(p1, mk);
                put_p(p1);
            } else {
                print_p(p2, mk);
                gprint(" = 0 ( ");
                gprint(Cm.s2226);
                print_var(e.v, mk);
                gprint(")");
            }
        } else if (pro_type == 0) {
            if (!unitp(e.p2)) print_p(e.p2, mk);
            print_var(e.v, mk);
            gprint(" = ");
            print_p(e.p1, mk);
        } else {
            print_var(e.v, mk);
            gprint(" = ");
            p1 = e.p1;
            p2 = e.p2;
            //  pprint(p1);
            //  pprint(p2);
            if (p1.var == null) {
                el_gr.c1 = p1.c;
                el_gr.ps1 = null;
            } else {
                el_gr.c1 = mk_num(1L);
                el_d1.deg = 1;
                el_d1.p = e.p1;
                el_d1.nx = null;
                el_gr.ps1 = el_d1;
            }
            if (p2.var == null) {
                el_gr.c2 = p2.c;
                el_gr.ps2 = null;
            } else {
                el_gr.c2 = mk_num(1L);
                el_d2.deg = 1;
                el_d2.p = e.p2;
                el_d2.nx = null;
                el_gr.ps2 = el_d2;
            }
            print_gr(el_gr, mk);
        }
        if (pro_type == 0 && e.co != null) {
            gprint(" (");
            show_cos(e.co);
            gprint(")");
        }
    }

    /**
     * Recursively checks whether a geometric term's polynomial parts are regular.
     *
     * @param gr the geometric term to check
     * @return true if both polynomial parts (ps1 and ps2) are regular, false otherwise
     */
    boolean rgr(GrTerm gr) {
        return (rps(gr.ps1) && rps(gr.ps2));
    }

    /**
     * Recursively checks whether the given polynomial term is regular.
     * <p>
     * A regular term requires that each associated variable has an index of 1, that
     * its subsequent term (ps) contains only one element, and that this recursively
     * holds for all subsequent terms.
     * </p>
     *
     * @param ps1 the polynomial term to check
     * @return true if the term is regular, false otherwise
     */
    boolean rps(DTerm ps1) {
        DTerm ps2;
        XTerm p1;
        Var v1;
        while (ps1 != null) {
            p1 = ps1.p;
            v1 = p1.var;
            if (v1 != null) {
                if (v1.nm != 1) return (false);
                ps2 = p1.ps;
                if (ps2.nx != null) return (false);
                if (!(rps(ps2))) return (false);
            }
            ps1 = ps1.nx;
        }
        return (true);
    }

    /**
     * Prints a geometric term.
     * <p>
     * The method prints the term based on its coefficients and polynomial parts.
     * Depending on whether the term represents a fraction, an integer, or a combination,
     * it formats the output accordingly.
     * </p>
     *
     * @param gr the geometric term to print
     * @param mk a marker character used during printing
     */
    void print_gr(GrTerm gr, char mk) {
        boolean rg;
        long n;

        if (num_zop(gr.c2)) {
            if (num_zop(gr.c1)) {
                gprint("0");
            } else if (gr.ps1 == null) {
                gprint("" + gr.c1);
            } else {
                if (num_unit(gr.c1)) {
                } else if (num_unit(gr.c1)) {
                    gprint("-");       ///////////////////////////////////????????????????????????
                } else {
                    num_show(gr.c1);
                }
                print_ps(gr.ps1, mk);
            }
        } else if (gr.ps1 == null && gr.ps2 == null) {
            if (num_unit(gr.c2)) {
                num_show(gr.c1);
            } else if (num_nunit(gr.c2)) {
                n = num_neg(gr.c1);
                num_show(n);
            } else
                show_num2(gr.c1, gr.c2, mk);
        } else if (gr.ps2 == null) {
            if (num_unit(gr.c1) && num_unit(gr.c2)) {
            } else if (num_unit(gr.c1) && num_nunit(gr.c2)) {
                gprint("-");
            } else if (num_unit(gr.c2)) {
                num_show(gr.c1);
            } else if (num_nunit(gr.c2)) {
                n = num_neg(gr.c1);
                num_show(n);
            } else {
                show_num2(gr.c1, gr.c2, mk);
            }
            print_ps(gr.ps1, mk);
        } else {
            rg = rgr(gr);
            if (gr.ps1 == null) {
                num_show(gr.c1);
            } else {
                if (num_unit(gr.c1)) { /* c_mark = 0; */
                } else if (num_nunit(gr.c1)) {
                    gprint("-");
                } else {
                    num_show(gr.c1);
                }
                print_ps(gr.ps1, mk);
            }
            gprint(" / ");
            if (gr.ps2 == null) {
                num_show(gr.c2);
            } else {
                if (num_unit(gr.c2)) { /* c_mark = 0; */
                } else if (num_nunit(gr.c2)) {
                    gprint("-");
                } else {
                    num_show(gr.c2);
                }
                print_ps(gr.ps2, mk);
            }
        }
    }

    /**
     * Displays a numeric fraction.
     * <p>
     * The numerator and denominator are shown separated by a forward slash.
     * </p>
     *
     * @param c1 the numerator value
     * @param c2 the denominator value
     * @param mk a marker character used during printing
     */
    void show_num2(long c1, long c2, char mk) {
        num_show(c1);
        gprint("/");
        num_show(c2);
    }

    /**
     * Prints a polynomial expression represented by a DTerm.
     * <p>
     * If the term has a degree of 1, it is printed normally or with parentheses if the
     * expression requires grouping. For higher degree terms, the term is printed with
     * an exponent notation.
     * </p>
     *
     * @param dp1 the polynomial term to print
     * @param mk  a marker character used during printing
     */
    void print_ps(DTerm dp1, char mk) {
        if (dp1 == null)
            gprint("");
        else {
            int k = 0;
            DTerm ps1 = dp1;
            while (ps1 != null) {
                k++;
                ps1 = ps1.nx;
            }
            while (dp1 != null) {
                if (dp1.deg == 1) {
                    if (k >= 0 && pro_type != PRO_FULL && ((plength(dp1.p) > 1) || (lcc(dp1.p) < 0))) {
                        gprint("(");
                        print_p(dp1.p, mk);
                        gprint(")");
                    } else
                        print_p(dp1.p, mk);
                } else {
                    gprint("(");
                    print_p(dp1.p, mk);
                    gprint(")^{" + dp1.deg + "}");
                }
                dp1 = dp1.nx;
            }
        }
    }

    /**
     * Prints an XTerm (mathematical expression) with its variable and coefficient.
     * <p>
     * Constant terms without an associated variable are printed in an angle-signed format.
     * For non-constant terms, the method processes the polynomial parts recursively.
     * </p>
     *
     * @param p1    the XTerm to be printed
     * @param first true if this is the first term (affects sign formatting), false otherwise
     */
    public void myprint_p1(XTerm p1, boolean first) {
        this.setPrintToString();
        DTerm dp1, dp2;
        XTerm xp1;

        if (p1 == null)
            return;
        if (p1.var == null) {
            {
                if (!first && p1.c > 0.)
                    gprint(" + ");
                if (p1.c < 0)
                    gprint(" - ");
                gprint(Cm.ANGLE_SIGN + "[");
                num_show(Math.abs(p1.c));
                gprint("]");
                dp2 = null;
            }
        } else {
            dp1 = p1.ps;
            xp1 = dp1.p;
            if (numberp(xp1)) {
                if (nunitp(xp1))
                    gprint(" - ");
                else {
                    if (!unitp(xp1)) {
                        if (xp1.c > 0)
                            gprint(" + ");
                        num_show(xp1.c);
                    } else if (!first) gprint(" + ");
                }
            }
            print_var(p1.var, (char) 0);
            dp2 = dp1.nx;
        }

        String s = this.getPrintedString();
        p1.sd = s;

        if (dp2 != null)
            myprint_p1(dp2.p, false);
    }

    /**
     * Counts the number of variable instances.
     *
     * @return the total number of variables in the linked list starting at all_var.nx
     */
    int getvarNum() {
        Var v = all_var.nx;
        int t = 0;
        while (v != null) {
            v = v.nx;
            t++;
        }
        return t;
    }

    /**
     * Counts the number of line instances.
     *
     * @return the total number of lines in the linked list starting at all_ln.nx
     */
    int getlnNum() {
        LLine ln = all_ln.nx;
        int t = 0;
        while (ln != null) {
            t++;
            ln = ln.nx;
        }
        return t;
    }

    /**
     * Reorders the points in the given variable.
     *
     * <p>If the first point is less than the second, they are swapped.
     * Similarly, if the third point is less than the fourth, they are swapped.
     * Additional reordering is performed if necessary, and the method returns a flag indicating if any reordering took place.</p>
     *
     * @param v the variable whose points are to be reordered
     * @return true if reordering occurred; false otherwise
     */
    boolean var_reOrder(Var v) {
        int p1, p2, p3, p4, p;
        boolean sr = false;
        p1 = v.pt[0];
        p2 = v.pt[1];
        p3 = v.pt[2];
        p4 = v.pt[3];

        if (p1 < p2) {
            p = p1;
            p1 = p2;
            p2 = p;
        }
        if (p3 < p4) {
            p = p3;
            p3 = p4;
            p4 = p;
        }
        if (p1 < p3) {
            sr = true;
            p = p1;
            p1 = p3;
            p3 = p;
            p = p2;
            p2 = p4;
            p4 = p;
        } else if ((p1 == p3) && (p2 < p4)) {
            sr = true;
            p = p2;
            p2 = p4;
            p4 = p;
        }
        {
            v.pt[0] = p1;
            v.pt[1] = p2;
            v.pt[2] = p3;
            v.pt[3] = p4;
        }
        return sr;
    }

    /**
     * Trims the line segments based on the intersection of two lines.
     *
     * <p>This method retrieves two lines from two pairs of points and checks whether they intersect.
     * If an intersection is found, the endpoints are adjusted accordingly.
     * Finally, an XTerm is generated using the trimmed endpoints.</p>
     *
     * @param p1 the first coordinate of the first point
     * @param p2 the second coordinate of the first point
     * @param p3 the first coordinate of the second point
     * @param p4 the second coordinate of the second point
     * @return the XTerm representing the trimmed line segments
     */
    XTerm trim_full(int p1, int p2, int p3, int p4) {
        int t = 0;

        LLine ln1 = fd_ln(p1, p2);
        LLine ln2 = fd_ln(p3, p4);
        if (ln1 != null && ln2 != null && (t = inter_ll(ln1, ln2)) != 0) {
            if (p1 != t && p2 != t)
                p2 = t;
            if (p3 != t && p4 != t)
                p4 = t;
        } else if (ln1 == null && ln2 != null) {
            // When ln1 is absent and ln2 is present, no adjustment is performed.
        }
        return trim_f(p1, p2, p3, p4);
    }

    /**
     * Sets the flag to show details.
     *
     * <p>This static method controls whether detailed information should be displayed.</p>
     *
     * @param d the boolean value to set for showing details
     */
    public static void set_showdetai(boolean d) {
        show_detail = d;
    }

    /**
     * Determines if a given type represents a valid construction.
     *
     * <p>The type is considered valid if it falls within specific ranges and does not require freeCS.</p>
     *
     * @param type the construction type to check
     * @return true if the type is a construction type; false otherwise
     */
    public boolean isConstructionType(int type) {
        return (type > 0 && type < 50 || type > 100 && type < 150)
                && !freeCS(type);
    }

    /**
     * Populates the provided vectors with non-degenerate geometry constraints (NDGs) from constructions.
     *
     * <p>This method iterates through constructions, adds valid constructions to the first vector,
     * initializes and filters NDGs, deduces additional constraints, and finally updates the provided vectors.</p>
     *
     * @param v1 a vector for constructions used as NDG constraints
     * @param v2 a vector for initial NDGs
     * @param v3 a vector for filtered NDGs
     * @param v4 a vector for deduced NDGs
     */
    public void get_ndgs(Vector v1, Vector v2, Vector v3, Vector v4) { // cndg

        int n = cns_no;
        for (int i = 1; i <= n; i++) {
            Cons c = allcns[i];
            if (c != null && isConstructionType(c.type))
                v1.add(c);
        }

        vndgs.clear();
        init_ndgs(v2);          // Init NDGs.
        filter_ndg(v2, v3);     // Remove redundant NDGs.
        ndg_deduction(v3, v4);
        filter_ndg(v4);
        filter_ndg(vndgs);

        parse_neq(v4);
        v4.clear();
        v4.addAll(vndgs);
    }

    /**
     * Creates and adds a non-isotropic NDG constraint based on two points.
     *
     * <p>This method checks that the points are different and valid.
     * It creates a new CNdg for non-isotropic constraints and adds it to the provided vector.</p>
     *
     * @param a the first point
     * @param b the second point
     * @param v1 the vector to which the NDG is added
     * @return the created CNdg object, or null if the points are identical or invalid
     */
    protected CNdg add_n_isotropic(int a, int b, Vector v1) {
        if (a == b)
            return null;

        if (a > b) {
            int c = a;
            a = b;
            b = c;
        }

        if (APT(a) == null && APT(b) == null) return null;

        CNdg n = new CNdg();
        n.type = NDG_NON_ISOTROPIC;
        n.p[0] = a;
        n.p[1] = b;
        n.no = 1;
        add_ndgs(n, v1);
        return n;
    }

    /**
     * Creates and adds an NDG constraint representing parallelism or perpendicularity.
     *
     * <p>This method reorders four points if needed and creates a corresponding NDG
     * depending on the specified type. The resulting constraint is then added to the vector.</p>
     *
     * @param type the type of NDG (e.g. NDG_PARA for parallel or NDG_PERP for perpendicular)
     * @param a the first point of the first line
     * @param b the second point of the first line
     * @param c the first point of the second line
     * @param d the second point of the second line
     * @param v1 the vector to which the NDG is added
     * @return the created CNdg object
     */
    protected CNdg add_n_pt(int type, int a, int b, int c, int d, Vector v1) {
        if (a > b) {
            int t = a;
            a = b;
            b = t;
        }
        if (c > d) {
            int t = c;
            c = d;
            d = t;
        }

        if (a > c) {
            int t = a;
            a = c;
            c = t;
            t = b;
            b = d;
            d = t;
        }

        CNdg n = null;

        if (type == NDG_PARA) {
            n = add_ndg_para(a, b, c, d);
        } else if (type == NDG_PERP) {
            n = add_ndg_perp(a, b, c, d);
        } else {
            n = new CNdg();
            n.type = type;
            n.p[0] = a;
            n.p[1] = b;
            n.p[2] = c;
            n.p[3] = d;
            n.no = 3;
        }
        add_ndgs(n, v1);

        return n;
    }

    /**
     * Creates and adds a collinearity NDG constraint based on three points.
     *
     * <p>This method reorders the three points to ensure a proper order for establishing collinearity,
     * then creates a new CNdg representing the collinearity constraint and adds it to the given vector.</p>
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @param v1 the vector to which the NDG is added
     * @return the created CNdg object
     */
    protected CNdg add_n_coll(int a, int b, int c, Vector v1) {
        if (a > b) {
            int t = a;
            a = b;
            b = t;
        }

        if (a > c) {
            int t = a;
            a = c;
            c = t;
        }

        if (b > c) {
            int t = b;
            b = c;
            c = t;
        }

        CNdg n = new CNdg();
        n.type = NDG_COLL;
        n.p[0] = a;
        n.p[1] = b;
        n.p[2] = c;
        n.no = 2;
        add_ndgs(n, v1);
        return n;
    }

    /**
     * Initializes non-degenerate geometry constraints (NDGs) from existing constructions.
     *
     * <p>This method iterates through all constructions, creates appropriate NDG constraints based on
     * the type of each construction, and associates dependencies with the NDGs when modifications occur.</p>
     *
     * @param v1 the vector to be populated with NDG constraints
     */
    public void init_ndgs(Vector v1) {
        CNdg nd;

        int sz = v1.size();

        int n = cns_no;
        for (int i = 1; i <= n; i++) {
            Cons c = allcns[i];
            switch (c.type) {
                case C_FOOT:
                    add_n_isotropic(c.ps[2], c.ps[3], v1);
                    break;
                case C_I_LL:
                    add_n_pt(NDG_PARA, c.ps[1], c.ps[2], c.ps[3], c.ps[4], v1);
                    break;
                case C_I_LB:
                    add_n_pt(NDG_PERP, c.ps[1], c.ps[2], c.ps[3], c.ps[4], v1);
                    break;
                case C_I_LP:
                    add_n_pt(NDG_PARA, c.ps[1], c.ps[2], c.ps[4], c.ps[5], v1);
                    break;
                case C_I_LT:
                    add_n_pt(NDG_PERP, c.ps[1], c.ps[2], c.ps[3], c.ps[4], v1);
                    break;
                case C_I_PP:
                    add_n_pt(NDG_PARA, c.ps[2], c.ps[3], c.ps[5], c.ps[6], v1);
                    break;
                case C_I_TT:
                    add_n_pt(NDG_PARA, c.ps[2], c.ps[3], c.ps[5], c.ps[6], v1);
                    break;
                case C_CIRCUM:
                case C_CENT:
                case C_ORTH:
                    add_n_coll(c.ps[1], c.ps[2], c.ps[3], v1);
                    break;
                case C_O_C:
                    break;
                case C_CIRCLE:
                    add_n_coll(c.ps[1], c.ps[2], c.ps[3], v1);
                    break;
                case C_PARALLELOGRAM:
                    add_coll_para(c, v1);
                    break;
                case C_I_CC:
                    nd = add_ndg_neq(c.ps[1], c.ps[3]);
                    this.add_ndgs(nd, v1);
                    break;
                case C_O_L:
                case C_I_LC:
                    nd = add_ndg_neq(c.ps[1], c.ps[2]);
                    this.add_ndgs(nd, v1);
                    break;
                case C_I_PA: {
                    CNdg dx = add_n_pt(NDG_PARA, c.ps[2], c.ps[3], c.ps[4], c.ps[5], v1);
                    if (dx != null)
                        dx.exists = true;

                    add_n_neq(c.ps[4], c.ps[5], vndgs);
                    add_n_neq(c.ps[6], c.ps[7], vndgs);
                    add_n_neq(c.ps[8], c.ps[9], vndgs);
                    XTerm xt = pplus(trim_f(c.ps[2], c.ps[3], c.ps[4], c.ps[5]),
                                      trim_f(c.ps[6], c.ps[7], c.ps[7], c.ps[8]));
                    xt = add_deduction(xt);
                    addxtermndg(xt, vndgs);
                }
                break;
                case C_I_LA: {
                    CNdg dx = add_n_coll(c.ps[0], c.ps[1], c.ps[3], v1);
                    if (dx != null)
                        dx.exists = true;

                    add_n_neq(c.ps[3], c.ps[4], vndgs);
                    add_n_neq(c.ps[5], c.ps[6], vndgs);
                    add_n_neq(c.ps[6], c.ps[7], vndgs);
                    XTerm xt = pplus(trim_f(c.ps[1], c.ps[2], c.ps[3], c.ps[4]),
                                      trim_f(c.ps[7], c.ps[6], c.ps[6], c.ps[5]));
                    xt = add_deduction(xt);
                    addxtermndg(xt, vndgs);
                }
                break;
                case C_I_AA: {
                    CNdg dx = add_n_pt(NDG_PARA, c.ps[0], c.ps[1], c.ps[0], c.ps[6], v1);
                    if (dx != null)
                        dx.exists = true;

                    add_n_neq(c.ps[1], c.ps[2], vndgs);
                    add_n_neq(c.ps[3], c.ps[4], vndgs);
                    add_n_neq(c.ps[4], c.ps[5], vndgs);
                    add_n_neq(c.ps[6], c.ps[7], vndgs);
                    add_n_neq(c.ps[8], c.ps[9], vndgs);
                    add_n_neq(c.ps[9], c.ps[10], vndgs);
                    XTerm xt = pplus(trim_f(c.ps[3], c.ps[4], c.ps[4], c.ps[5]),
                                      trim_f(c.ps[1], c.ps[2], c.ps[6], c.ps[7]));
                    xt = pplus(xt, trim_f(c.ps[10], c.ps[9], c.ps[9], c.ps[8]));
                    xt = add_deduction(xt);
                    addxtermndg(xt, vndgs);
                }
                break;
            }

            if (sz < v1.size()) {
                sz = v1.size();
                CNdg dd = (CNdg) v1.get(v1.size() - 1);
                dd.dep = c;
            }
        }
    }


    /**
     * Adds a non-equality NDG constraint for the given points.
     *
     * <p>This method creates an NDG constraint of type NDG_NEQ (or equivalent) for points a and b,
     * then adds it to the provided vector.</p>
     *
     * @param a the first point index
     * @param b the second point index
     * @param v1 the vector to add the NDG constraint
     */
    public void add_n_neq(int a, int b, Vector v1) {
        CNdg nd = add_ndg_neq(a, b);
        this.add_ndgs(nd, v1);
    }

    /**
     * Creates and adds a collinearity constraint in parallel form based on a construction.
     *
     * <p>This method determines the highest point value from the construction's point array, then
     * selects the three remaining points to build a collinearity NDG constraint and adds it to vector v1.</p>
     *
     * @param cs the construction containing the points
     * @param v1 the vector to add the NDG constraint
     */
    public void add_coll_para(Cons cs, Vector v1) {
        int a, b;

        a = cs.ps[0];
        b = 0;
        for (int i = 0; i < 4; i++) {
            if (a < cs.ps[i]) {
                a = cs.ps[i];
                b = i;
            }
        }

        int c1, c2, c3;
        c1 = c2 = c3 = -1;

        for (int i = 0; i < 4; i++) {
            if (c1 < 0 && i != b)
                c1 = cs.ps[i];
            else if (c2 < 0 && i != b)
                c2 = cs.ps[i];
            else if (c3 < 0 && i != b)
                c3 = cs.ps[i];
        }

        add_n_coll(c1, c2, c3, v1);
    }

    /**
     * Deduces angle-based NDG constraints and processes them.
     *
     * <p>This method handles a given CNdg constraint by checking if all associated points are free.
     * If so, it directly duplicates the constraint into vector v4; otherwise, it computes an XTerm deduction
     * based on the NDG type and adds related non-equality constraints.</p>
     *
     * @param c  the CNdg constraint to analyze
     * @param v4 the vector in which the deduced NDG constraints are stored
     */
    public void angle_deduction(CNdg c, Vector v4) {
        if (c == null)
            return;

        if (ck_allFree(c)) {  // all free Points.
            CNdg d = new CNdg(c);
            v4.add(d);
            return;
        }

        XTerm xt = null;

        switch (c.type) {
            case NDG_COLL:
                xt = add_deduction(trim_f(c.p[0], c.p[1], c.p[1], c.p[2]));
                this.addxtermndg(xt, v4);
                xt = add_deduction(trim_f(c.p[0], c.p[2], c.p[2], c.p[1]));
                this.addxtermndg(xt, v4);
                xt = add_deduction(trim_f(c.p[1], c.p[0], c.p[0], c.p[2]));
                this.addxtermndg(xt, v4);
                add_neqTo(c.p[0], c.p[1], v4);
                add_neqTo(c.p[0], c.p[2], v4);
                add_neqTo(c.p[1], c.p[2], v4);
                break;
            case NDG_PARA:
                xt = add_deduction(trim_f(c.p[0], c.p[1], c.p[2], c.p[3]));
                this.addxtermndg(xt, v4);
                add_neqTo(c.p[0], c.p[1], v4);
                add_neqTo(c.p[2], c.p[3], v4);
                break;
            case NDG_PERP:
                xt = add_deduction(trim_f(c.p[0], c.p[1], c.p[2], c.p[3]));
                this.addxtermndg(xt, v4);
                add_neqTo(c.p[0], c.p[1], v4);
                add_neqTo(c.p[2], c.p[3], v4);
                break;
            case NDG_NEQ:
            case NDG_NON_ISOTROPIC:
                CNdg d1 = new CNdg(c);
                this.add_ndgs(d1, v4);
                break;
        }
    }

    /**
     * Checks and adds a non-equality constraint between two points if not already present.
     *
     * <p>This method iterates over the vector v4 to determine if an equivalent NDG constraint exists.
     * If none is found, a new NDG constraint (NDG_NEQ) for the pair of points is created and added.</p>
     *
     * @param a  the first point index
     * @param b  the second point index
     * @param v4 the vector containing NDG constraints
     */
    protected void add_neqTo(int a, int b, Vector v4) {
        for (int i = 0; i < v4.size(); i++) {
            CNdg d = (CNdg) v4.get(i);
            if (d.type == NDG_NEQ || d.type == NDG_NON_ISOTROPIC) {
                if (d.contain(a) && d.contain(b))
                    return;
            } else if (d.type == NDG_COLL) {
                if (d.contain(a) && d.contain(b))
                    return;
            } else if (d.type == NDG_PARA || d.type == NDG_PERP) {
                if (d.p[0] == a && d.p[1] == b || d.p[0] == b && d.p[1] == a
                        || d.p[2] == a && d.p[3] == b || d.p[2] == b && d.p[3] == a)
                    return;
            }
        }
        CNdg d = new CNdg();
        d.type = NDG_NEQ;
        d.no = 1;
        d.p[0] = a;
        d.p[1] = b;
        add_ndgs(d, v4);
    }

    /**
     * Checks whether all points in the given CNdg constraint are free.
     *
     * <p>This method iterates over the points associated with the constraint and calls freeCSP
     * for each one. It returns false as soon as any point is not free.</p>
     *
     * @param d the CNdg constraint to check
     * @return true if all points are free; false otherwise
     */
    protected boolean ck_allFree(CNdg d) {
        if (d == null)
            return true;
        for (int i = 0; i <= d.no; i++) {
            if (!freeCSP(d.p[i]))
                return false;
        }
        return true;
    }

    /**
     * Performs angle deduction on the given XTerm.
     *
     * <p>If the XTerm has an associated variable, angle-based deduction is applied followed by
     * a final deduction adjustment.</p>
     *
     * @param x the XTerm to deduct
     * @return the resulting XTerm after deduction
     */
    protected XTerm add_deduction(XTerm x) {
        if (x.var == null)
            return x;

        XTerm xt = angle_deduction(x);
        xt = final_deduction(xt);
        return xt;
    }

    /**
     * Applies a final deduction adjustment to an XTerm.
     *
     * <p>If the factor computed by fcc is negative, the XTerm is multiplied by -1.</p>
     *
     * @param p1 the XTerm to adjust
     * @return the adjusted XTerm
     */
    protected XTerm final_deduction(XTerm p1) {
        if (p1 == null)
            return p1;

        if (fcc(p1) < 0)
            p1 = ptimes(get_n(-1), p1);

        return p1;
    }

    /**
     * Filters out redundant NDG constraints in the given vector.
     *
     * <p>This method compares each NDG constraint in the vector with the others and removes any
     * that are equal or less significant than another constraint.</p>
     *
     * @param v4 the vector containing NDG constraints to filter
     */
    protected void filter_ndg(Vector v4) {
        for (int i = 0; i < v4.size(); i++) {
            CNdg d = (CNdg) v4.get(i);
            for (int j = i + 1; j < v4.size(); j++) {
                CNdg nx = (CNdg) v4.get(j);
                if (ndg_eq(d, nx) || ndg_less(nx, d)) {
                    v4.remove(j);
                    i = -1;
                    break;
                } else if (ndg_less(d, nx)) {
                    v4.remove(j);
                    v4.remove(i);
                    v4.add(i, nx);
                    i = -1;
                    break;
                }
            }
        }
    }

    /**
     * Filters and merges NDG constraints from vector v2 into vector v3.
     *
     * <p>This method iterates over the constraints in v2 and checks against those in v3.
     * If an equivalent or less significant constraint exists, it merges or sets an equivalence link.</p>
     *
     * @param v2 the source vector of NDG constraints
     * @param v3 the target vector for filtered NDG constraints
     */
    protected void filter_ndg(Vector v2, Vector v3) {
        for (int i = 0; i < v2.size(); i++) {
            CNdg d = (CNdg) v2.get(i);
            boolean added = false;
            for (int j = 0; j < v3.size(); j++) {
                CNdg nx = (CNdg) v3.get(j);

                if (ndg_eq(d, nx) || ndg_less(d, nx)) {
                    d.equ = nx;
                    added = true;
                    break;
                } else if (ndg_less(nx, d)) {
                    CNdg d1 = new CNdg(d);
                    d.equ = d1;
                    v3.remove(j);
                    v3.add(j, d1);
                    added = true;
                    break;
                }
            }
            if (!added) {
                CNdg d1 = new CNdg(d);
                d.equ = d1;
                v3.add(d1);
            }
        }
    }

    /**
     * Adds an NDG constraint to the specified vector.
     *
     * <p>This helper method ensures the NDG constraint is valid (non-null), initializes its string
     * representation by calling get_ndgstr, and then adds it to the vector.</p>
     *
     * @param d     the NDG constraint to add
     * @param vlist the vector where the NDG constraint is stored
     */
    protected void add_ndgs(CNdg d, Vector vlist) {
        if (d == null)
            return;

        get_ndgstr(d);
        vlist.add(d);
    }

    /**
     * Compares two NDG constraints to determine if the first is less significant than the second.
     *
     * <p>For non-equality NDGs, if the second constraint is of type NDG_COLL and contains both points
     * of the first constraint, the first is considered less significant.</p>
     *
     * @param n1 the first NDG constraint
     * @param n2 the second NDG constraint
     * @return true if n1 is less significant than n2; false otherwise
     */
    protected boolean ndg_less(CNdg n1, CNdg n2) {
        if (n1.type == NDG_NEQ || n1.type == NDG_NON_ISOTROPIC) {
            if (n2.type == NDG_COLL) {
                if (n2.contain(n1.p[0]) && n2.contain(n1.p[1]))
                    return true;
            }
        }
        return false;
    }

    /**
     * Checks whether two NDG constraints are equal.
     *
     * <p>This method first compares their types (allowing for NDG_NEQ and NDG_NON_ISOTROPIC to be equivalent),
     * then verifies that the number of points and the point values are identical.</p>
     *
     * @param n1 the first NDG constraint
     * @param n2 the second NDG constraint
     * @return true if the two constraints are equal; false otherwise
     */
    protected boolean ndg_eq(CNdg n1, CNdg n2) {
        if (n1.type != n2.type) {
            if ((n1.type == NDG_NEQ || n1.type == NDG_NON_ISOTROPIC)
                    && (n2.type == NDG_NEQ || n2.type == NDG_NON_ISOTROPIC)) {
            } else
                return false;
        }

        if (n1.no != n2.no)
            return false;

        for (int i = 0; i <= n1.no; i++)
            if (n1.p[i] != n2.p[i])
                return false;

        return true;
    }

    /**
     * Converts an XTerm into an NDG constraint and adds its printed representation.
     *
     * <p>This method first converts the XTerm into a corresponding NDG constraint via xterm2ndg,
     * then updates its string representation using the printing methods.</p>
     *
     * @param x  the XTerm to convert and add
     * @param v4 the vector where the NDG information is stored
     */
    protected void addxtermndg(XTerm x, Vector v4) {
        if (x == null)
            return;
        xterm2ndg(x, v4);

        this.setPrintToString();
        xprint(x);
        x.sd = this.getPrintedString();
    }


    /**
     * Converts an XTerm into NDG constraints and adds them to the provided list.
     * <p>
     * Depending on the term number of the XTerm, the method delegates to a helper method
     * to generate the appropriate NDG constraints.
     * </p>
     *
     * @param x the XTerm to convert
     * @param vlist the vector where the generated NDG constraints will be added
     */
    protected void xterm2ndg(XTerm x, Vector vlist) {
        if (x == null || x.var == null)
            return;

        int n = x.getTermNumber();

        if (n == 0)
            xterm_1term(x, vlist);
        else if (n == 1)
            xterm_2term(x, vlist);
    }

    /**
     * Processes an XTerm with one term to generate NDG constraints.
     * <p>
     * The method examines the factor computed by {@code fcc(x)} and based on its value,
     * creates and adds NDG constraints using parallel, perpendicular, or triple PI rules.
     * </p>
     *
     * @param x the XTerm to process
     * @param vlist the vector where the generated NDG constraints will be added
     */
    protected void xterm_1term(XTerm x, Vector vlist) {
        long n = fcc(x);

        if (x.var == null) {
            ///////xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        } else if (n == 1) {
            CNdg d = add_ndg_para(x.var);
            if (d != null)
                d.dep = x;
            add_ndgs(d, vlist);
        } else if (n == 2) {
            CNdg d = add_ndg_para(x.var);
            if (d != null)
                d.dep = x;
            add_ndgs(d, vlist);
            d = add_ndg_perp(x.var);
            if (d != null)
                d.dep = x;
            add_ndgs(d, vlist);
        } else if (n == 3) {
            CNdg d = add_ndg_triplePI(x.var);
            if (d != null)
                d.dep = x;
            add_ndgs(d, vlist);
        }
    }

    /**
     * Creates a Triple PI NDG constraint from the specified variable.
     * <p>
     * It initializes the NDG using the points from the variable and reorders them,
     * setting the number of points to 3.
     * </p>
     *
     * @param v the variable containing the points for the NDG constraint
     * @return the constructed Triple PI NDG constraint
     */
    protected CNdg add_ndg_triplePI(Var v) {
        CNdg n = new CNdg();
        n.type = NDG_TRIPLEPI;

        n.p[0] = v.pt[0];
        n.p[1] = v.pt[1];
        n.p[2] = v.pt[2];
        n.p[3] = v.pt[3];
        n.no = 3;
        reorder22(n);

        return n;
    }

    /**
     * Processes an XTerm with two terms to generate NDG constraints.
     * <p>
     * The method examines the second term of the XTerm and creates congruency or collinearity NDGs
     * based on the sign of the factor computed by {@code fcc} for the second term.
     * </p>
     *
     * @param x the XTerm to process
     * @param vlist the vector where the generated NDG constraints will be added
     */
    protected void xterm_2term(XTerm x, Vector vlist) {
        long n = fcc(x);
        if (x.ps != null) {
            DTerm dx1 = x.ps.nx;
            if (dx1 != null) {
                XTerm x1 = dx1.p;
                long n1 = fcc(x1);

                Var v1 = x.var;
                Var v2 = x1.var;

                if (v2 != null) {  // v2 is available
                    if (n1 < 0) {
                        if (v1.pt[2] == v2.pt[0] && v1.pt[3] == v2.pt[1]) {
                            CNdg d = add_ndg_cong(v1.pt[0], v1.pt[1], v2.pt[2], v2.pt[3]);
                            if (d != null) {
                                d.dep = x;
                                add_ndgs(d, vlist);
                            }
                            d = this.add_ndg_coll(v1.pt[0], v1.pt[1], v1.pt[3]);
                            if (d != null) {
                                d.dep = x;
                                add_ndgs(d, vlist);
                            }
                        }
                    } else if (n1 > 0) {
                        if (v1.pt[2] == v2.pt[2] && v1.pt[3] == v2.pt[3]) {
                            CNdg d = add_ndg_cong(v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1]);
                            if (d != null) {
                                d.dep = x;
                                add_ndgs(d, vlist);
                            }
                            d = this.add_ndg_coll(v1.pt[0], v1.pt[1], v1.pt[3]);
                            if (d != null) {
                                d.dep = x;
                                add_ndgs(d, vlist);
                            }
                        }
                    }
                } else {  // v2 is not available
                    if (x1.var == null & x1.c == 1) {
                        CNdg d = add_ndg_perp(v1);
                        if (d != null)
                            d.dep = x;
                        add_ndgs(d, vlist);
                    }
                }
            }
        }
    }

    /**
     * Reorders the points in an NDG constraint (with three points) into ascending order.
     *
     * @param n the NDG constraint whose points are to be reordered
     */
    protected void reorder3(CNdg n) {
        for (int i = 0; i < 2; i++) {
            int d = n.p[i];
            if (d > n.p[i + 1]) {
                n.p[i] = n.p[i + 1];
                n.p[i + 1] = d;
                i = -1;
            }
        }
    }

    /**
     * Reorders two pairs of points in an NDG constraint so that each pair is in ascending order.
     * <p>
     * In addition, it ensures that the first pair is sorted relative to the second pair.
     * </p>
     *
     * @param n the NDG constraint whose points are to be reordered
     */
    protected void reorder22(CNdg n) {
        if (n.p[0] > n.p[1]) {
            int d = n.p[0];
            n.p[0] = n.p[1];
            n.p[1] = d;
        }

        if (n.p[2] > n.p[3]) {
            int d = n.p[2];
            n.p[2] = n.p[3];
            n.p[3] = d;
        }

        if (n.p[0] > n.p[2]) {
            int d = n.p[0];
            n.p[0] = n.p[2];
            n.p[2] = d;
            d = n.p[1];
            n.p[1] = n.p[3];
            n.p[3] = d;
        }
    }

    /**
     * Creates a non-equality NDG constraint for two points.
     * <p>
     * Returns null if both points are identical.
     * </p>
     *
     * @param a the first point index
     * @param b the second point index
     * @return the NDG non-equality constraint, or null if the points are identical
     */
    protected CNdg add_ndg_neq(int a, int b) {
        if (a == b)
            return null;
        CNdg n = new CNdg();
        n.type = NDG_NEQ;
        if (a > b) {
            int c = a;
            a = b;
            b = c;
        }

        n.p[0] = a;
        n.p[1] = b;
        n.no = 1;

        return n;
    }

    /**
     * Checks whether the point at the given index is free from constraints.
     * <p>
     * It retrieves the associated point object and checks its construction type.
     * </p>
     *
     * @param index the index of the point to check
     * @return true if the point is free; false otherwise
     */
    public boolean freeCSP(int index) {
        ProPoint pt = APT(index);
        if (pt == null)
            return false;

        int type = 0;
        for (int i = 1; i <= cns_no; i++) {
            Cons c = allcns[i];
            if (c.type != C_POINT && c.ps[0] == index) {
                type = c.type;
                break;
            }
        }

        return freeCS(type);
    }

    /**
     * Determines if the given construction type represents a free state.
     * <p>
     * A type is considered free if it matches one of the basic construction types.
     * </p>
     *
     * @param t the construction type to check
     * @return true if the type is free; false otherwise
     */
    public boolean freeCS(int t) {
        return t == 0 || t == C_POINT || t == C_LINE || t == C_TRIANGLE ||
                t == C_QUADRANGLE || t == C_PENTAGON || t == C_POLYGON || t == C_CIRCLE;
    }

    /**
     * Creates a collinearity NDG constraint from three points.
     * <p>
     * The points are reordered to ensure proper ordering. If redundant, then null is returned.
     * </p>
     *
     * @param a the first point index
     * @param b the second point index
     * @param c the third point index
     * @return the NDG collinearity constraint, or null if the points are redundant
     */
    protected CNdg add_ndg_coll(int a, int b, int c) {
        CNdg n = new CNdg();
        n.type = NDG_COLL;

        n.p[0] = a;
        n.p[1] = b;
        n.p[2] = c;
        n.no = 2;
        reorder3(n);
        if (n.redundentPt())
            return null;
        else return n;
    }

    /**
     * Creates a congruency NDG constraint based on four points.
     * <p>
     * The points are reordered; if the set of points is redundant, null is returned.
     * </p>
     *
     * @param a the first point index of the first segment
     * @param b the second point index of the first segment
     * @param c the first point index of the second segment
     * @param d the second point index of the second segment
     * @return the NDG congruency constraint, or null if the points are redundant
     */
    protected CNdg add_ndg_cong(int a, int b, int c, int d) {
        CNdg n = new CNdg();
        n.type = NDG_CONG;

        n.p[0] = a;
        n.p[1] = b;
        n.p[2] = c;
        n.p[3] = d;
        n.no = 3;
        reorder22(n);
        if (n.redundentPt())
            return n;
        else return null;
    }

    /**
     * Creates a parallel NDG constraint using the points of a given variable.
     *
     * @param v the variable containing the points for the constraint
     * @return the NDG parallel constraint
     */
    protected CNdg add_ndg_para(Var v) {
        return add_ndg_para(v.pt[0], v.pt[1], v.pt[2], v.pt[3]);
    }

    /**
     * Creates a parallel NDG constraint based on four point indices.
     * <p>
     * The points are reordered to ascending order; if the set is redundant,
     * an alternative collinearity constraint is constructed instead.
     * </p>
     *
     * @param a the first point index of the first segment
     * @param b the second point index of the first segment
     * @param c the first point index of the second segment
     * @param d the second point index of the second segment
     * @return the NDG parallel constraint, or an alternative NDG constraint if redundant
     */
    protected CNdg add_ndg_para(int a, int b, int c, int d) {
        CNdg n = new CNdg();
        n.type = NDG_PARA;

        n.p[0] = a;
        n.p[1] = b;
        n.p[2] = c;
        n.p[3] = d;
        n.no = 3;

        reorder22(n);
        if (n.redundentPt()) {

            CNdg n1 = new CNdg();
            n1.type = NDG_COLL;
            for (int i = 0; i < 4; i++)
                if (!n1.contain(n.p[i]))
                    n1.addAPt(n.p[i]);
            if (n1.no == 1)
                n1.type = NDG_NEQ;

            reorder3(n1);
            return n1;
        }

        return n;
    }

    /**
     * Creates a perpendicular NDG constraint using the points of a given variable.
     *
     * @param v the variable containing the points for the constraint
     * @return the NDG perpendicular constraint
     */
    protected CNdg add_ndg_perp(Var v) {
        return add_ndg_perp(v.pt[0], v.pt[1], v.pt[2], v.pt[3]);
    }

    /**
     * Creates a perpendicular NDG constraint based on four point indices.
     * <p>
     * If the two segments are identical, a non-isotropic NDG constraint is created instead.
     * </p>
     *
     * @param a the first point index of the first segment
     * @param b the second point index of the first segment
     * @param c the first point index of the second segment
     * @param d the second point index of the second segment
     * @return the NDG perpendicular constraint, or a non-isotropic constraint when applicable
     */
    protected CNdg add_ndg_perp(int a, int b, int c, int d) {
        CNdg n = new CNdg();
        n.type = NDG_PERP;

        n.p[0] = a;
        n.p[1] = b;
        n.p[2] = c;
        n.p[3] = d;
        n.no = 3;
        reorder22(n);
        if (n.p[0] == n.p[2] && n.p[1] == n.p[3]) {
            n.type = NDG_NON_ISOTROPIC;
            n.no = 1;
            n.p[2] = n.p[3] = 0;
        }
        return n;
    }


    /**
     * Sets the string representation (sd) of the given NDG constraint (d) based on its type
     * and associated points. Depending on the type, constructs a descriptive message.
     *
     * @param d the NDG constraint for which the string representation is set
     */
    protected void get_ndgstr(CNdg d) {
        String sd = "";
        switch (d.type) {
            case NDG_COLL:
                sd = ANAME(d.p[0]) + ", " + ANAME(d.p[1]) + ", " + ANAME(d.p[2]) + " are not collinear";
                break;
            case NDG_NEQ:
                sd = ANAME(d.p[0]) + " != " + ANAME(d.p[1]);
                break;
            case NDG_NON_ISOTROPIC:
                sd = ANAME(d.p[0]) + ANAME(d.p[1]) + " is non-isotropic";
                break;
            case NDG_PARA:
                sd = ANAME(d.p[0]) + ANAME(d.p[1]) + " is not parallel to " + ANAME(d.p[2]) + ANAME(d.p[3]);
                break;
            case NDG_PERP:
                sd = ANAME(d.p[0]) + ANAME(d.p[1]) + " is not perpendicular to " + ANAME(d.p[2]) + ANAME(d.p[3]);
                break;
            case NDG_CYCLIC:
                sd = ANAME(d.p[0]) + ", " + ANAME(d.p[1]) + ", " + ANAME(d.p[2]) + ", " + ANAME(d.p[3]) + " are not cyclic";
                break;
            case NDG_CONG:
                sd = "|" + ANAME(d.p[0]) + ANAME(d.p[1]) + "| != |" + ANAME(d.p[2]) + ANAME(d.p[3]) + "|";
                break;
            case NDG_TRIPLEPI: {
                int n = d.getRedundentPt();
                if (n == 0) {
                    sd = Cm.ANGLE_SIGN + "[" + ANAME(d.p[0]) + ANAME(d.p[1]) + ", " + ANAME(d.p[2]) + ANAME(d.p[3])
                            + "] != (n*PI) / 3 (n = 0, 1, 2, 3 ..)";
                } else {
                    int a, b;
                    if (d.p[0] == n)
                        a = d.p[1];
                    else
                        a = d.p[0];
                    if (d.p[2] == n)
                        b = d.p[3];
                    else
                        b = d.p[2];

                    sd = Cm.ANGLE_SIGN + "[" + ANAME(a) + ANAME(n) + ANAME(b)
                            + "] != (n*PI) / 3 (n = 0, 1, 2, 3 ..)";
                }
            }
            break;
        }
        d.sd = sd;
    }

    /**
     * Performs angle deduction on the given XTerm. This method initializes necessary properties
     * and iteratively attempts to eliminate terms using various elimination methods. At the end,
     * it optimizes the term based on triangle configurations.
     *
     * @param p1 the XTerm on which angle deduction is performed
     * @return the resulting XTerm after angle deduction
     */
    protected XTerm angle_deduction(XTerm p1) {
        ertype = 0;
        pro_type = PRO_FULL;
        max_term = 0;
        d_base = 1;
        qerror = false;
        last_pr = proof = new GrTerm();
        dbase();
        if (qerror)
            return null;
        ElTerm e1 = null;

        do {
            if (npoly(p1)) {
                return p1;
            }

            while (true) {
                if (p1.var == null) {
                    e1 = null;
                } else if ((e1 = elim_q7(p1)) != null) {
                } else if ((e1 = elim_q8(p1)) != null) {
                } else if ((e1 = elim_f(p1.var)) != null) {
                } else if ((fcc(p1) % 2L) == 0 && (e1 = elim_d(p1.var)) != null) {
                } else if ((fcc(p1) % 3L) == 0 && (e1 = elim_t(p1.var)) != null) {
                } else if ((e1 = elim_tri(p1.var)) == null) {
                }
                if (e1 != null) {
                    p1 = eprem(cp_poly(p1), e1);
                    p1 = fpoly(p1);
                } else if (d_base == 1) {
                    d_base = 2;
                    dbase();
                } else
                    break;
            }
        } while (e1 != null);

        p1 = opt_tri(p1);
        return p1;
    }

    /**
     * Optimizes the given XTerm by detecting and processing triangle configurations.
     * It compares factors from the current term and its subterm to determine if any adjustment
     * such as scaling or subtraction should be applied.
     *
     * @param x the XTerm to be optimized
     * @return the optimized XTerm
     */
    public XTerm opt_tri(XTerm x) {
        long n = fcc(x);
        if (x.ps != null) {
            DTerm dx1 = x.ps.nx;
            if (dx1 != null) {
                XTerm x1 = dx1.p;
                long n1 = fcc(x1);
                Var v1 = x.var;
                Var v2 = x1.var;
                if (n == n1) {
                    if (v1.pt[2] == v2.pt[0] && v1.pt[3] == v2.pt[1]) {
                        XTerm p1 = pplus(get_m(v1), get_m(v2));
                        p1 = pminus(p1, trim_f(v1.pt[0], v1.pt[1], v2.pt[2], v2.pt[3]));
                        if (n != 1) {
                            p1 = ptimes(this.get_n(n), p1);
                            return pminus(x, p1);
                        }
                    } else if (v1.pt[0] == v2.pt[2] && v1.pt[1] == v2.pt[3]) {
                        XTerm p1 = pplus(get_m(v1), get_m(v2));
                        p1 = pminus(p1, trim_f(v2.pt[0], v2.pt[1], v1.pt[2], v1.pt[3]));
                        if (n != 1) {
                            p1 = ptimes(this.get_n(n), p1);
                            return pminus(x, p1);
                        }
                    }
                } else if (n + n1 == 0) {
                    if (v1.pt[0] == v2.pt[0] && v1.pt[1] == v2.pt[1]) {
                        XTerm p1 = pminus(get_m(v1), get_m(v2));
                        p1 = pminus(p1, trim_f(v2.pt[2], v2.pt[3], v1.pt[2], v1.pt[3]));
                        if (n != 1) {
                            p1 = ptimes(this.get_n(n), p1);
                            return pminus(x, p1);
                        }
                    } else if (v1.pt[2] == v2.pt[2] && v1.pt[3] == v2.pt[3]) {
                        XTerm p1 = pminus(get_m(v1), get_m(v2));
                        p1 = pminus(p1, trim_f(v1.pt[0], v1.pt[1], v2.pt[0], v2.pt[1]));
                        if (n != 1) {
                            p1 = ptimes(this.get_n(n), p1);
                            return pminus(x, p1);
                        }
                    }
                }
            }
        }
        return x;
    }

    /**
     * Performs NDG (non-degeneracy) deduction on a set of NDG constraints.
     * For each constraint in the source vector v3, if the constraint is marked as existing
     * or if all its points are free, it is directly added or duplicated into the target vector v4.
     * Otherwise, angle deduction is applied.
     *
     * @param v3 the source vector of NDG constraints
     * @param v4 the target vector to store the deduced NDG constraints
     */
    public void ndg_deduction(Vector v3, Vector v4) {
        for (int i = 0; i < v3.size(); i++) {
            CNdg d = (CNdg) v3.get(i);
            if (d.exists) {
                // Existing NDG constraints are skipped.
            } else if (ck_allFree(d)) {
                CNdg d1 = new CNdg(d);
                v4.add(d1);
            } else {
                angle_deduction(d, v4);
            }
        }
    }

    /**
     * Compares two NDG constraints based on their maximum integer values.
     *
     * @param d1 the first NDG constraint
     * @param d2 the second NDG constraint
     * @return a positive value if d1 &gt; d2, a negative value if d1 &lt; d2, or zero if they are equal
     */
    public int compare(CNdg d1, CNdg d2) {
        if (d1 == d2)
            return 0;
        int n1 = d1.getMaxInt();
        int n2 = d2.getMaxInt();
        if (n1 > n2)
            return 1;
        if (n1 < n2)
            return -1;
        return 0;
    }

    /**
     * Sorts a vector of NDG constraints in ascending order based on their significance.
     * It uses the compare method to insert each constraint into its correct position in the vector.
     *
     * @param v4 the vector containing NDG constraints to be sorted
     */
    public void sortVector(Vector v4) {
        for (int i = 1; i < v4.size(); i++) {
            CNdg d = (CNdg) v4.get(i);
            for (int j = 0; j < i; j++) {
                CNdg d1 = (CNdg) v4.get(j);
                if (compare(d, d1) < 0) {
                    v4.remove(i);
                    v4.add(j, d);
                    i--;
                    break;
                }
            }
        }
    }

    /**
     * Parses non-equality and non-isotropic NDG constraints from the given vector.
     * <p>
     * This method sorts the input vector of NDG constraints, filters out those
     * constraints that are non-equality (NDG_NEQ) or non-isotropic (NDG_NON_ISOTROPIC)
     * and have both points free, and then processes the remaining constraints.
     * The remaining constraints are added to a global NDG list after being updated.
     * </p>
     *
     * @param v4 the vector containing NDG constraints to be parsed
     */
    public void parse_neq(Vector v4) {

        sortVector(v4);
        Vector v5 = new Vector();

        for (int i = 0; i < v4.size(); i++) {
            CNdg d1 = (CNdg) v4.get(i);
            if (d1.type != NDG_NEQ && d1.type != NDG_NON_ISOTROPIC)
                continue;
            if (this.freeCSP(d1.p[0]) && this.freeCSP(d1.p[1]))
                continue;

            v5.add(d1);
            v4.remove(i);
            i--;
        }

        // vndgs.clear();
        vndgs.addAll(v4);

        for (int i = 0; i < v5.size(); i++) {
            CNdg d = (CNdg) v5.get(i);
            NdgCs c = parse_neq(d);
            updateSD(c);

            if (c.ntype == 0 && !addNdg(c, c.no))
                add_ndgs(d, vndgs);
        }

        this.filter_ndg(vndgs);
        return;
    }

    /**
     * Recursively updates the string documentation (SD) for the provided NDG constraint structure.
     * <p>
     * For each constraint in the structure, the method updates its point string array and resets its text.
     * The method also recurses into any child constraint sets.
     * </p>
     *
     * @param dd the NDG constraint structure to update
     */
    private void updateSD(NdgCs dd) {
        if (dd == null)
            return;

        for (int i = 0; i <= dd.no; i++) {
            Cons c1 = dd.allcns[i];
            if (c1 != null) {
                updatePSS(c1);
                c1.setText(null);
            }
        }
        for (int i = 0; i < dd.child.length; i++)
            if (dd.child[i] != null)
                updateSD(dd.child[i]);
    }

    /**
     * Constructs and returns an NDG constraint set (NdgCs) associated with the given inequality constraint.
     * <p>
     * The method iterates through existing constraints and gathers those related to
     * the points of the provided constraint. The collected constraints are then added
     * to a new NdgCs structure and updated.
     * </p>
     *
     * @param d the NDG inequality constraint used as a basis for gathering related constraints
     * @return the constructed NDG constraint set (NdgCs)
     */
    public NdgCs getCS(CNdg d) {
        int n = d.getMaxInt();

        NdgCs c = new NdgCs();
        for (int i = 1; i <= cns_no; i++) {
            Cons c1 = allcns[i];
            if (c1 == null)
                continue;

            if (construct_related(c1.type)) {
                if (c1.getLastPt() > n) {
                    break;
                }
                if (c1 != null) {
                    int x = c1.getLastPt();
                    if (x == d.p[0] || x == d.p[1])
                        add_RelatedCnsToDg(i, c);
                }
            }
        }

        NdgCs dd = new NdgCs();
        for (int i = 0; i <= cns_no; i++) {
            Cons c2 = c.allcns[i];
            if (c2 != null)
                addConsToNdgcs(c2, dd);
        }

        for (int i = 0; i <= dd.no; i++) {
            Cons c1 = dd.allcns[i];
            updatePSS(c1);
        }
        return dd;
    }

    /**
     * Updates the point string array (pss) of the specified constraint using the global points array.
     *
     * @param c the constraint whose point string array is to be updated
     */
    private void updatePSS(Cons c) {
        if (c == null)
            return;

        for (int i = 0; i <= c.no; i++) {
            c.pss[i] = this.allpts[c.ps[i]];
        }
    }

    /**
     * Recursively adds all constraints related to the constraint at index nx into the provided NDG constraint set.
     * <p>
     * For each point within the constraint, the method checks previous constraints for related ones and adds them.
     * </p>
     *
     * @param nx the index of the current constraint in the global constraints array
     * @param d the NDG constraint set (NdgCs) where related constraints are added
     */
    private void add_RelatedCnsToDg(int nx, NdgCs d) {

        Cons c = allcns[nx];
        if (c == null)
            return;

        for (int i = 0; i <= c.no; i++) {
            for (int j = 0; j < nx; j++) {
                Cons c1 = allcns[j];
                if (c1 == null)
                    continue;
                if (!construct_related(c1.type))
                    continue;

                if (c1.getLastPt() == c.ps[i]) {
                    d.add(j, c1);
                    add_RelatedCnsToDg(j, d);
                }
            }
        }
        d.add(nx, c);
    }

    /**
     * Adds the provided constraint to the given NDG constraint set.
     * <p>
     * Based on the type of the constraint, it may clone the constraint or generate additional constraints.
     * </p>
     *
     * @param c the constraint to be added
     * @param d the NDG constraint set (NdgCs) that will include the constraint
     */
    public void addConsToNdgcs(Cons c, NdgCs d) {
        if (c == null)
            return;

        switch (c.type) {
            case C_O_L:
            case C_O_P:
            case C_O_T:
            case C_O_C:
                c = new Cons(c);
                d.add(c);
                break;
            case C_FOOT:
                add_cons(C_O_L, c.ps[0], c.ps[2], c.ps[3], 0, d);
                add_cons(C_O_T, c.ps[0], c.ps[1], c.ps[2], c.ps[3], d);
                break;
            case C_I_PP:
                add_cons(C_O_P, c.ps[0], c.ps[1], c.ps[2], c.ps[3], d);
                add_cons(C_O_P, c.ps[0], c.ps[4], c.ps[5], c.ps[6], d);
                break;
            case C_I_LL:
                add_cons(C_O_L, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_L, c.ps[0], c.ps[3], c.ps[4], 0, d);
                break;
            case C_I_CC:
                add_cons(C_O_C, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_C, c.ps[0], c.ps[3], c.ps[4], 0, d);
                break;
            case C_CIRCUM:
                add_cons(C_CIRCUM, c.ps[0], c.ps[1], c.ps[2], c.ps[3], d);
                break;
            case C_I_LT:
                add_cons(C_O_L, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_T, c.ps[0], c.ps[3], c.ps[4], c.ps[5], d);
                break;
            case C_I_LP:
                add_cons(C_O_L, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_P, c.ps[0], c.ps[3], c.ps[4], c.ps[5], d);
                break;
            case C_I_LC:
                add_cons(C_O_L, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_C, c.ps[0], c.ps[3], c.ps[4], 0, d);
                break;
            case C_I_LA:
                add_cons(C_O_L, c.ps[0], c.ps[1], c.ps[2], 0, d);
                add_cons(C_O_A, c.ps[0], c.ps[3], c.ps[4], c.ps[5], c.ps[6], c.ps[7], c.ps[8], d);
                break;
            case C_I_PT:
                add_cons(C_O_P, c.ps[0], c.ps[1], c.ps[2], c.ps[3], d);
                add_cons(C_O_T, c.ps[0], c.ps[4], c.ps[5], c.ps[6], d);
                break;
        }
    }

    /**
     * Creates a new constraint with 7 points and adds it to the specified NDG constraint set.
     *
     * @param type the type of the new constraint
     * @param a the first point index
     * @param b the second point index
     * @param c the third point index
     * @param d the fourth point index
     * @param e the fifth point index
     * @param f the sixth point index
     * @param g the seventh point index
     * @param d1 the NDG constraint set (NdgCs) where the new constraint will be added
     */
    public void add_cons(int type, int a, int b, int c, int d, int e, int f, int g, NdgCs d1) {
        Cons c1 = new Cons(type);
        c1.add_pt(a);
        c1.add_pt(b);
        c1.add_pt(c);
        c1.add_pt(d);
        c1.add_pt(e);
        c1.add_pt(f);
        c1.add_pt(g);
        c1.reorder();
        d1.add(c1);
    }

    /**
     * Creates a new constraint with 4 points and adds it to the specified NDG constraint set.
     *
     * @param type the type of the new constraint
     * @param a the first point index
     * @param b the second point index
     * @param c the third point index
     * @param d the fourth point index
     * @param d1 the NDG constraint set (NdgCs) where the new constraint will be added
     */
    public void add_cons(int type, int a, int b, int c, int d, NdgCs d1) {
        Cons c1 = new Cons(type);
        c1.add_pt(a);
        c1.add_pt(b);
        c1.add_pt(c);
        c1.add_pt(d);
        c1.reorder();
        d1.add(c1);
    }

    /**
     * Checks whether the specified constraint is recursively valid with respect to the given NDG type.
     * <p>
     * Depending on the type of the constraint, it recursively checks if related NDG constraints already exist.
     * </p>
     *
     * @param c the constraint to check
     * @param cs the NDG constraint set (NdgCs) in which the check is performed
     * @param type the NDG type against which the constraint is validated
     * @return true if the constraint is valid; false otherwise
     */
    public boolean ck_right(Cons c, NdgCs cs, int type) {
        if (c == null || cs == null)
            return true;

        switch (c.type) {
            case C_I_EQ:
                return !ck_recursive_ndg(type, cs, NDG_NEQ, c.ps[0], c.ps[1], 0, 0);
            case C_O_L:
                return !ck_recursive_ndg(type, cs, NDG_COLL, c.ps[0], c.ps[1], c.ps[2], 0);
            case C_O_T:
                return !ck_recursive_ndg(type, cs, NDG_PARA, c.ps[0], c.ps[1], c.ps[2], c.ps[3]);
            case C_O_P:
                return !ck_recursive_ndg(type, cs, NDG_PERP, c.ps[0], c.ps[1], c.ps[2], c.ps[3]);
        }
        return true;
    }

    /**
     * Removes NDG constraint set nodes that have no constraints from the provided NDG structure.
     * <p>
     * This method recursively traverses the child NDG constraint sets and nullifies any reference that
     * does not contain constraints.
     * </p>
     *
     * @param c the NDG constraint set (NdgCs) from which null nodes will be removed
     */
    private void rm_null_ndgcs(NdgCs c) {
        if (c == null)
            return;

        int n = c.getCSindex();
        for (int i = 0; i <= n; i++) {
            NdgCs cc = c.child[i];
            if (cc == null)
                continue;
            rm_null_ndgcs(cc);
        }
        if (c.getNotNullNum() != 0)
            return;

        n = c.getCSindex();
        if (n < 0) {
            NdgCs cs = c.parent;
            if (cs != null)
                for (int i = 0; i <= cs.getCSindex(); i++) {
                    if (cs.child[i] == c)
                        cs.child[i] = null;
                }
        }
    }

    /**
     * Removes extraneous constraints from the given NDG constraint set.
     * <p>
     * At a leaf node, this method nullifies any constraints that are marked as extraneous.
     * For non-leaf nodes, the method recurses through each child NDG constraint set.
     * </p>
     *
     * @param c the NDG constraint set (NdgCs) from which extraneous constraints will be removed
     */
    private void rm_excons(NdgCs c) {
        if (c == null)
            return;

        NdgCs[] css = c.child;
        int a = c.getCSindex();
        if (a < 0) {            // leaf node.
            for (int i = 0; i <= c.no; i++) {
                if (c.allcns[i] == null)
                    continue;
                if (cons_ex(c, i)) {
                    c.allcns[i] = null;
                }
            }
        } else {
            for (int i = 0; i <= a; i++) {
                rm_excons(css[i]);
            }
        }
    }

    /**
     * Checks if the constraint at the given index in the NDG constraint set is extraneous
     * by comparing it with the corresponding constraint at the root of the constraint set.
     *
     * @param cs the NDG constraint set
     * @param index the index of the constraint to check
     * @return true if the constraint is considered extraneous; false otherwise
     */
    private boolean cons_ex(NdgCs cs, int index) {
        Cons c = cs.allcns[index];

        if (c == null)
            return true;

        while (cs.parent != null)
            cs = cs.parent;
        Cons c1 = cs.allcns[index];
        if (c1 == null)
            return false;
        return c1.isEqual(c);
    }

    /**
     * Recursively checks constraints in an NDG constraint set for consistency.
     * For leaf nodes, it verifies each constraint against its expected conditions;
     * for non-leaf nodes, it cleans up inconsistent child sets.
     *
     * @param c the NDG constraint set to check
     * @param type the type identifier for the check; usage depends on the context
     * @return true if the constraint set is consistent; false otherwise
     */
    public boolean ck_right(NdgCs c, int type) {
        if (c == null)
            return true;

        NdgCs[] css = c.child;
        int a = c.getCSindex();
        if (a < 0) {            // leaf node.
            for (int i = 0; i <= c.no; i++) {
                if (c.allcns[i] == null)
                    continue;
                if (!ck_right(c.allcns[i], c, type)) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i <= a; i++) {
                if (css[i] == null)
                    continue;
                if (!ck_right(css[i], type))
                    css[i] = null;
            }
            for (int i = 0; i <= a; i++)
                if (css[i] != null)
                    return true;
                else return false;
        }
        return true;
    }

    /**
     * Parses non-equality and non-isotropic NDG constraints by replacing point indices,
     * reordering constraints, and invoking further parsing, cleanup, and optimization steps.
     *
     * @param nd the NDG constraint to be parsed
     * @return the updated NDG constraint set after parsing
     */
    public NdgCs parse_neq(CNdg nd) {
        NdgCs c = getCS(nd);
        NdgCs cx = new NdgCs(c);
        cx.parent = c;
        c.addChild(cx);

        switch (nd.type) {
            case NDG_NEQ:
            case NDG_NON_ISOTROPIC:
                int a = nd.p[0];
                int b = nd.p[1];
                for (int i = 0; i <= c.no; i++) {
                    Cons c1 = cx.allcns[i];
                    if (c1 == null)
                        break;
                    c1.replace(b, a);   // replace b with a.
                    c1.reorder();
                }
                break;
        }
        parseStep(cx);
        ck_leaf(c);
        rm_excons(c);               // remove existing constraints
        rm_null_ndgcs(c);           // remove NDG sets without children
        ck_right(c, 1);             // remove constraints contradicting with parents
        int n1 = c.getCSindex();
        ck_right(c, 0);
        if (n1 >= 0 && c.getCSindex() < 0)
            c.ntype = 1;
        rm_nleaf(c);
        cons_reorder(c);
        return c;
    }

    /**
     * Compares two constraints based on their last point and type.
     *
     * @param c1 the first constraint
     * @param c2 the second constraint
     * @return a positive integer if c1 &gt; c2, a negative integer if c1 &lt; c2, or 0 if they are equal
     */
    private int compare(Cons c1, Cons c2) {
        if (c1 == null) {
            if (c2 == null)
                return 0;
            return -1;
        }
        if (c2 == null)
            return 1;
        int n1 = c1.getLastPt();
        int n2 = c2.getLastPt();

        if (n1 > n2)
            return 1;
        if (n1 < n2)
            return -1;
        if (c1.type < c2.type)
            return -1;
        if (c1.type > c2.type)
            return 1;
        return 0;
    }

    /**
     * Reorders constraints within an NDG constraint set.
     * For leaf nodes, it sorts the constraints in ascending order;
     * for non-leaf nodes, it recursively reorders each child set.
     *
     * @param c the NDG constraint set to reorder
     */
    private void cons_reorder(NdgCs c) {
        if (c == null)
            return;
        if (c.leaf) {
            for (int i = 0; i <= c.no; i++) {
                for (int j = i + 1; j <= c.no; j++) {
                    if (c.allcns[i] == null) {
                        if (c.allcns[j] != null) {
                            c.allcns[i] = c.allcns[j];
                            c.allcns[j] = null;
                        }
                    } else {
                        if (c.allcns[j] != null) {
                            if (compare(c.allcns[i], c.allcns[j]) > 0) {
                                Cons cx = c.allcns[i];
                                c.allcns[i] = c.allcns[j];
                                c.allcns[j] = cx;
                            }
                        }
                    }
                }
            }
        } else {
            int n = c.getCSindex();
            for (int i = 0; i <= n; i++) {
                cons_reorder(c.child[i]);
            }
        }
    }

    /**
     * Checks whether an NDG constraint set is a leaf node.
     * A node is considered a leaf if it has no child NDG constraint sets.
     *
     * @param c the NDG constraint set to check
     */
    private void ck_leaf(NdgCs c) {
        if (c == null)
            return;
        if (c.getCSindex() < 0) {
            c.leaf = true;
            return;
        }
        for (int i = 0; i <= c.getCSindex(); i++) {
            ck_leaf(c.child[i]);
        }
    }

    /**
     * Recursively removes non-leaf NDG constraint set nodes that are not required.
     *
     * @param c the NDG constraint set from which non-leaf nodes will be removed
     */
    private void rm_nleaf(NdgCs c) {
        if (c == null || c.leaf)
            return;

        int n = c.getCSindex();
        if (n < 0) {
            NdgCs cs = c.parent;
            if (cs != null) {
                for (int i = 0; i <= cs.getCSindex(); i++) {
                    if (cs.child[i] == c)
                        cs.child[i] = null;
                }
            }
        } else {
            for (int i = 0; i <= n; i++) {
                rm_nleaf(c.child[i]);
            }
        }
    }

    /**
     * Main process for parsing constraints within the NDG constraint set.
     * This method performs optimization, redundant constraint removal, and processes
     * constraints based on their maximum index.
     *
     * @param c the NDG constraint set to parse
     * @return true if parsing completes successfully; otherwise, false
     */
    private boolean parseStep(NdgCs c) {
        if (c == null)
            return true;

        opt_cons(c); // reorder constraints (P to L)
        rm_redundent(c);

        if (c.getNotNullNum() <= 1)
            return true;

        int max = c.getMaxCnsInt();
        int n = c.no;
        for (int i = 0; i <= n; i++) {
            Cons c1 = c.allcns[i];
            if (c1 == null)
                continue;
            if (c1.getLastPt() != max)
                continue;

            for (int j = i + 1; j <= n; j++) {
                Cons c2 = c.allcns[j];
                if (c2 == null)
                    continue;
                if (c2.getLastPt() != max)
                    continue;
                switch (c1.type) {
                    case C_O_L:
                        switch (c2.type) {
                            case C_O_L:
                                if (parse_ndg_ll(c, c1, c2))
                                    return true;
                                break;
                            case C_O_T:
                                if (parse_ndg_lt(c, c1, c2))
                                    return true;
                                break;
                            default:
                                break;
                        }
                        break;
                    case C_O_T:
                        switch (c2.type) {
                            case C_O_L:
                                if (parse_ndg_lt(c, c2, c1))
                                    return true;
                                break;
                            default:
                                break;
                        }
                        break;
                    case C_CIRCUM:
                        switch (c2.type) {
                            case C_CIRCUM:
                                parse_ndg_circums(c, c1, c2);
                                break;
                        }
                        break;
                }
            }
        }
        return true;
    }

    /**
     * Processes cyclic NDG constraints by identifying collinear points among
     * the constraints and generating additional equality constraints as needed.
     *
     * @param c the NDG constraint set containing the constraints
     * @param c1 the first constraint to compare
     * @param c2 the second constraint to compare
     * @return false after processing is complete
     */
    private boolean parse_ndg_circums(NdgCs c, Cons c1, Cons c2) {
        int o = c1.ps[0];

        int[] pp = new int[6];
        for (int i = 0; i < pp.length; i++)
            pp[i] = 0;

        for (int i = 0; i < 3; i++) {
            addPtNoRedunent(c1.ps[i + 1], pp);
            addPtNoRedunent(c2.ps[i + 1], pp);
        }

        for (int i = 0; i < pp.length; i++) {
            if (pp[i] == 0)
                break;
            for (int j = i + 1; j < pp.length; j++) {
                if (pp[j] == 0)
                    break;
                for (int k = j + 1; k < pp.length; k++) {
                    if (pp[k] == 0)
                        break;
                    {
                        for (int m = 0; m <= c.no; m++) {
                            Cons cx = c.allcns[m];
                            if (cx == null)
                                break;
                            switch (cx.type) {
                                case C_O_L:
                                    if (xcoll(pp[i], pp[j], pp[k])) {
                                        if (!ck_recursive_ndg(1, c, NDG_NEQ, pp[i], pp[j], 0, 0)) {
                                            NdgCs nc1 = new NdgCs(c); // ! Collinear A, B, C
                                            nc1.parent = c;
                                            c.addChild(nc1);
                                            Cons cc = new Cons(C_I_EQ);
                                            cc.add_pt(pp[i]);
                                            cc.add_pt(pp[j]);
                                            cc.reorder();
                                            ndg_pteq_added(cc, nc1);
                                            parseStep(nc1);
                                        }
                                        if (!ck_recursive_ndg(1, c, NDG_NEQ, pp[i], pp[k], 0, 0)) {
                                            NdgCs nc1 = new NdgCs(c); // ! Collinear A, B, C
                                            nc1.parent = c;
                                            c.addChild(nc1);
                                            Cons cc = new Cons(C_I_EQ);
                                            cc.add_pt(pp[i]);
                                            cc.add_pt(pp[k]);
                                            cc.reorder();
                                            ndg_pteq_added(cc, nc1);
                                            parseStep(nc1);
                                        }
                                        if (!ck_recursive_ndg(1, c, NDG_NEQ, pp[i], pp[j], 0, 0)) {
                                            NdgCs nc1 = new NdgCs(c); // ! Collinear A, B, C
                                            nc1.parent = c;
                                            c.addChild(nc1);
                                            Cons cc = new Cons(C_I_EQ);
                                            cc.add_pt(pp[j]);
                                            cc.add_pt(pp[k]);
                                            cc.reorder();
                                            ndg_pteq_added(cc, nc1);
                                            parseStep(nc1);
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Adds point p to the array pp if it is not already present.
     * The method fills the first available zero slot.
     *
     * @param p the point index to add
     * @param pp the array of point indices
     */
    private void addPtNoRedunent(int p, int[] pp) {
        for (int i = 0; i < pp.length; i++) {
            if (pp[i] == p)
                return;
            if (pp[i] == 0) {
                pp[i] = p;
                return;
            }
        }
    }

    /**
     * Parses NDG constraints of type LL by comparing two constraints and creating new NDG constraint sets.
     * It creates new equality or collinearity constraints based on the matched point indices.
     *
     * @param c the NDG constraint set
     * @param c1 the first constraint to compare
     * @param c2 the second constraint to compare
     * @return true if parsing takes place; false otherwise
     */
    private boolean parse_ndg_ll(NdgCs c, Cons c1, Cons c2) {

        int n1 = c1.getLastPt();
        int n2 = c2.getLastPt();

        if (n1 != n2)
            return false;

        int m, a, b;
        if (c1.ps[1] == c2.ps[1]) {
            m = c1.ps[1];
            a = c1.ps[2];
            b = c2.ps[2];
        } else if (c1.ps[1] == c2.ps[2]) {
            m = c1.ps[1];
            a = c1.ps[2];
            b = c2.ps[1];
        } else if (c1.ps[2] == c2.ps[1]) {
            m = c1.ps[1];
            a = c1.ps[1];
            b = c2.ps[2];
        } else if (c1.ps[2] == c2.ps[2]) {
            m = c1.ps[2];
            a = c1.ps[1];
            b = c2.ps[1];
        } else
            return false;

        {
            NdgCs nc1 = new NdgCs(c); // Collinear A, B, C
            nc1.parent = c;
            c.addChild(nc1);
            Cons cc = new Cons(C_I_EQ);
            cc.add_pt(n1);
            cc.add_pt(m);
            cc.reorder();
            ndg_pteq_added(cc, nc1);
            nc1.nd = this.add_ndg_coll(m, a, b);
            parseStep(nc1);
        }

        if (!ck_recursive_ndg(1, c, NDG_COLL, m, a, b, 0)) {
            NdgCs nc2 = new NdgCs(c); // Collinear M A B; A != B
            nc2.parent = c;
            c.addChild(nc2);
            Cons cc = new Cons(C_O_L);
            cc.add_pt(m);
            cc.add_pt(a);
            cc.add_pt(b);
            ndg_coll_added(cc, nc2);
            nc2.nd = this.add_ndg_neq(a, b);
            parseStep(nc2);
        }

        if (!ck_recursive_ndg(1, c, NDG_NEQ, a, b, 0, 0)) {
            NdgCs nc3 = new NdgCs(c); // A = B.
            nc3.parent = c;
            c.addChild(nc3);
            Cons cc = new Cons(C_I_EQ);
            cc.add_pt(a);
            cc.add_pt(b);
            cc.reorder();
            ndg_pteq_added(cc, nc3);
            nc3.nd = null;
            parseStep(nc3);
        }

        return true;
    }

    /**
     * Recursively checks for an NDG constraint that meets the specified parameters.
     * In global mode (type 0) the search is performed in the global NDG list,
     * while in recursive mode (type 1) the search goes through the current constraint set's parent chain.
     *
     * @param type the check mode (0 for global, 1 for recursive search)
     * @param cs the NDG constraint set to check within
     * @param t the NDG type to check (e.g. NDG_COLL, NDG_NEQ)
     * @param a the first point index involved in the constraint
     * @param b the second point index involved in the constraint
     * @param c the third point index, or 0 if not used
     * @param d the fourth point index, or 0 if not used
     * @return true if a matching NDG constraint is found; false otherwise
     */
    private boolean ck_recursive_ndg(int type, NdgCs cs, int t, int a, int b, int c, int d) {

        if (type == 0) {
            for (int i = 0; i < vndgs.size(); i++) {
                CNdg dx = (CNdg) vndgs.get(i);
                if (ck_ndg(t, a, b, c, d, dx))
                    return true;
            }
            return false;

        } else { // type == 1;

            if (cs == null)
                return false;

            CNdg dd = cs.nd;
            if (dd == null)
                return false;

            while (cs != null) {
                if (cs.nd != null && ck_ndg(t, a, b, c, d, cs.nd))
                    return true;
                cs = cs.parent;
            }
        }
        return false;
    }

    /**
     * Checks if the provided NDG constraint (dd) satisfies the condition for the given NDG type.
     * For collinearity (NDG_COLL), it checks if the constraint contains the three specified points.
     * For inequality types (NDG_NEQ or NDG_NON_ISOTROPIC), it checks if the constraint contains the two specified points.
     *
     * @param t the NDG type to evaluate (e.g. NDG_COLL, NDG_NEQ)
     * @param a the first point index
     * @param b the second point index
     * @param c the third point index, or 0 if not applicable
     * @param d the fourth point index, or 0 if not applicable
     * @param dd the NDG constraint to check
     * @return true if the NDG constraint meets the criteria; false otherwise
     */
    private boolean ck_ndg(int t, int a, int b, int c, int d, CNdg dd) {
        boolean r = false;

        switch (t) {
            case NDG_COLL:
                if (dd.type == NDG_COLL) {
                    r = dd.contain(a) && dd.contain(b) && dd.contain(c);
                } else if (dd.type == NDG_NEQ || dd.type == NDG_NON_ISOTROPIC) {
                    // Condition for NDG_NEQ or NDG_NON_ISOTROPIC is omitted.
                }
                break;
            case NDG_NEQ:
            case NDG_NON_ISOTROPIC:
                if (dd.type == NDG_COLL)
                    r = dd.contain(a) && dd.contain(b);
                else if (dd.type == NDG_NEQ || dd.type == NDG_NON_ISOTROPIC)
                    r = dd.contain(a) && dd.contain(b);
                break;
        }
        return r;
    }

    /**
     * Parses NDG constraints of type LT by comparing two constraints and creating new constraint sets.
     * This method adjusts point orders and generates equality constraints when necessary.
     *
     * @param c the NDG constraint set
     * @param c1 the first constraint to compare
     * @param c2 the second constraint to compare
     * @return true if parsing proceeded; false otherwise
     */
    private boolean parse_ndg_lt(NdgCs c, Cons c1, Cons c2) {

        int n1 = c1.getLastPt();
        int n2 = c2.getLastPt();

        if (c1.type == C_O_T && c2.type == C_O_L) {
            Cons x = c1;
            c1 = c2;
            c2 = x;
        }

        if (n1 != n2)
            return false;

        int m, a, b;
        if (c2.ps[0] == c2.ps[2]) {
            m = c2.ps[0];
            a = c2.ps[1];
            b = c2.ps[3];
        } else if (c2.ps[1] == c2.ps[2]) {
            m = c2.ps[1];
            a = c2.ps[0];
            b = c2.ps[3];
        } else if (c2.ps[1] == c2.ps[3]) {
            m = c2.ps[1];
            a = c2.ps[0];
            b = c2.ps[2];
        } else if (c2.ps[0] == c2.ps[3]) {
            m = c2.ps[0];
            a = c2.ps[1];
            b = c2.ps[2];
        } else
            return false;

        if (!(c1.contains(m) && c1.contains(a) && c1.contains(b)))
            return false;

        if (a < b) {
            int x = a;
            a = b;
            b = x;
        }

        {
            NdgCs nc2 = new NdgCs(c); // Otherwise.
            nc2.parent = c;
            c.addChild(nc2);
            Cons cc2 = new Cons(C_I_EQ);
            cc2.add_pt(n1);
            cc2.add_pt(m);
            nc2.add(cc2);
            ndg_pteq_added(cc2, nc2);
            nc2.nd = this.add_ndg_neq(m, b);
            parseStep(nc2);
        }

        if (!ck_recursive_ndg(1, c, NDG_NEQ, m, b, 0, 0)) {
            NdgCs nc2 = new NdgCs(c); // Otherwise.
            nc2.parent = c;
            c.addChild(nc2);
            Cons cc2 = new Cons(C_I_EQ);
            cc2.add_pt(m);
            cc2.add_pt(b);
            nc2.add(cc2);
            ndg_pteq_added(cc2, nc2);
            nc2.nd = null;
            parseStep(nc2);
        }
        return true;
    }

    /**
     * Updates the NDG constraint set by replacing occurrences of one point with another in all constraints.
     * This method is used when a new equality constraint is added.
     *
     * @param c the newly added equality constraint
     * @param d the NDG constraint set to update
     */
    private void ndg_pteq_added(Cons c, NdgCs d) {
        int m = c.ps[0];
        int a = c.ps[1];
        for (int i = 0; i <= d.no; i++) {
            Cons c2 = d.allcns[i];
            if (c2 == null || c2 == c)
                continue;
            c2.replace(m, a);
        }
        d.add(c);
    }

    /**
     * Incorporates a collinearity NDG constraint into the provided NDG constraint set.
     * This method updates related constraints by replacing point indices when necessary.
     *
     * @param c the collinearity constraint to add
     * @param d the NDG constraint set to update
     */
    private void ndg_coll_added(Cons c, NdgCs d) {
        int m = c.ps[0];
        int a = c.ps[1];
        int b = c.ps[2];

        for (int i = 0; i <= d.no; i++) {
            Cons c2 = d.allcns[i];
            if (c2 == null || c2 == c)
                continue;

            switch (c2.type) {
                case C_O_L:
                    if (c2.contains(m)) {
                        if (c2.contains(a)) {
                            c2.replace(m, b);
                        } else if (c.contains(b)) {
                            c2.replace(m, a);
                        }
                    }
                    break;
                case C_O_T:
                case C_O_P:
                    if (c2.ps[0] == m) {
                        if (c2.ps[1] == a)
                            c2.ps[0] = b;
                        else if (c2.ps[1] == b)
                            c2.ps[0] = a;
                    } else if (c2.ps[2] == m) {
                        if (c2.ps[3] == a)
                            c2.ps[2] = b;
                        else if (c2.ps[3] == b)
                            c2.ps[2] = a;
                    } else if (c2.ps[1] == m) {
                        if (c2.ps[0] == a)
                            c2.ps[1] = b;
                        else if (c2.ps[0] == b)
                            c2.ps[1] = a;
                    } else if (c2.ps[3] == m) {
                        if (c2.ps[2] == a)
                            c2.ps[3] = b;
                        else if (c2.ps[2] == b)
                            c2.ps[3] = a;
                    }
                    break;
            }
            c2.reorder();
        }
        d.add(c);
    }

    /**
     * Optimizes constraints within the provided NDG constraint set.
     * <p>
     * This method iterates over each constraint, attempts to optimize it using the
     * overloaded opt_cons method, and applies equality replacements if modifications occur.
     * </p>
     *
     * @param c the NDG constraint set to optimize
     */
    private void opt_cons(NdgCs c) {
        while (true) {
            boolean r = true;

            for (int i = 0; i <= c.no; i++) {
                Cons c1 = c.allcns[i];
                if (c1 != null) {
                    c1.reorder();
                    if (opt_cons(c, c1))
                        r = false;
                }
            }

            if (!r) {
                for (int i = 0; i <= c.no; i++) {
                    Cons c1 = c.allcns[i];
                    if (c1 != null) {
                        switch (c1.type) {
                            case C_I_EQ:
                                for (int j = 0; j <= c.no; j++) {
                                    Cons c2 = c.allcns[j];
                                    if (c2 != null && c2 != c1)
                                        c2.replace(c1.ps[0], c1.ps[1]);
                                }
                                break;
                        }
                    }
                }
            } else
                break;
        }
    }

    /**
     * Attempts to optimize a single constraint within the given NDG constraint set.
     * <p>
     * The method simplifies or converts the constraint based on its type.
     * For instance, certain conditions on C_O_P may result in converting it to C_O_L,
     * while a C_O_T constraint may be simplified to an equality constraint.
     * </p>
     *
     * @param cs the NDG constraint set containing the constraint
     * @param c the constraint to optimize
     * @return true if the constraint was modified; false otherwise
     */
    private boolean opt_cons(NdgCs cs, Cons c) {
        if (c == null)
            return false;

        switch (c.type) {
            case C_O_P:
                if (c.ps[0] == c.ps[2] || c.ps[1] == c.ps[2]) {
                    c.type = C_O_L;
                    c.no = -1;
                    c.add_pt(c.ps[0]);
                    c.add_pt(c.ps[1]);
                    c.add_pt(c.ps[3]);
                    c.reorder();
                    return true;
                } else if (c.ps[1] == c.ps[3]) {
                    c.type = C_O_L;
                    c.no = -1;
                    c.add_pt(c.ps[0]);
                    c.add_pt(c.ps[1]);
                    c.add_pt(c.ps[2]);
                    c.reorder();
                    return true;
                }
                break;
            case C_O_T:
                if (c.ps[0] == c.ps[2] && c.ps[1] == c.ps[3]) {
                    c.type = C_I_EQ;
                    c.no = -1;
                    c.add_pt(c.ps[0]);
                    c.add_pt(c.ps[1]);
                    c.reorder();
                    return true;
                }
                break;
            case C_CIRCUM: {
                int t = 0;
                boolean r = false;
                for (int i = 0; i < 4; i++) {
                    for (int j = i + 1; j < 4; j++)
                        if (c.ps[i] == c.ps[j]) {
                            r = true;
                            t = c.ps[i];
                            break;
                        }
                    if (r)
                        break;
                }

                if (r) {
                    boolean r1 = false;
                    int min = Integer.MAX_VALUE;

                    for (int i = 0; i < 4; i++)
                        if (c.ps[i] != 0 && c.ps[i] < min)
                            min = c.ps[i];

                    for (int i = 0; i < 4; i++) {
                        if (add_eq(min, c.ps[i], cs))
                            r1 = true;
                    }
                    return r1;
                }
            }
            break;
        }
        return false;
    }

    /**
     * Adds an equality constraint between two points into the specified NDG constraint set.
     * <p>
     * If no existing equality constraint is found for the given non-zero points,
     * this method creates a new equality constraint (C_I_EQ) relating the two points.
     * </p>
     *
     * @param a the first point index
     * @param b the second point index
     * @param c the NDG constraint set where the equality constraint will be added
     * @return true if a new equality constraint was added; false otherwise
     */
    private boolean add_eq(int a, int b, NdgCs c) {
        if (a == b)
            return false;
        if (a == 0 || b == 0)
            return false;

        if (a < b) {
            int t = a;
            a = b;
            b = t;
        }

        for (int i = 0; i <= c.no; i++) {
            Cons c1 = c.allcns[i];
            if (c1 == null)
                continue;
            if (c1.type != C_I_EQ)
                continue;
            if (c1.contains(a) && c1.contains(b))
                return false;
        }

        Cons c2 = new Cons(C_I_EQ);
        c2.add_pt(a);
        c2.add_pt(b);
        c.add(c2);
        return true;
    }

    /**
     * Removes redundant constraints from the provided NDG constraint set.
     * <p>
     * This method first removes constraints flagged as redundant based on internal criteria,
     * and then eliminates duplicate constraints by comparing existing constraints.
     * </p>
     *
     * @param c the NDG constraint set from which redundant constraints will be removed
     */
    private void rm_redundent(NdgCs c) {
        for (int i = 0; i <= c.no; i++) {
            Cons c1 = c.allcns[i];
            if (c1 != null && cons_redundent(c1))
                c.allcns[i] = null;
        }

        for (int i = 0; i <= c.no; i++) {
            Cons c1 = c.allcns[i];
            if (c1 == null)
                continue;
            for (int j = 0; j < i; j++) {
                Cons c2 = c.allcns[j];
                if (c2 != null && c2.isEqual(c1)) {
                    c.allcns[i] = null;
                    break;
                }
            }
        }
    }

    /**
     * Checks if a given constraint is redundant.
     * <p>
     * The redundancy is determined based on the type of the constraint and comparisons
     * of its point indices, such as duplicated points in a collinearity or perpendicular constraint.
     * </p>
     *
     * @param c the constraint to evaluate
     * @return true if the constraint is considered redundant; false otherwise
     */
    private boolean cons_redundent(Cons c) {
        switch (c.type) {
            case C_O_L:
                return c.ps[0] == c.ps[1] || c.ps[0] == c.ps[2] || c.ps[1] == c.ps[2];
            case C_O_T:
            case C_O_P:
                return c.ps[0] == c.ps[1] || c.ps[2] == c.ps[3];
            case C_O_C:
                return c.ps[0] == c.ps[2];
            case C_CIRCUM:
                return c.ps[0] == c.ps[1] && c.ps[1] == c.ps[2] && c.ps[2] == c.ps[3];
        }
        return false;
    }

    /**
     * Determines whether a constraint type is considered related to construction operations.
     * <p>
     * The method returns false for fundamental geometric element types (e.g., point, triangle, etc.)
     * and for types within a specific range, indicating that additional construction steps are not required.
     * </p>
     *
     * @param t the constraint type identifier
     * @return true if the constraint type is construction-related; false otherwise
     */
    public boolean construct_related(int t) {
        if (t == C_POINT || t == C_TRIANGLE || t == C_POLYGON || t == C_QUADRANGLE
                || t == C_PENTAGON || t == C_LINE || t == C_CIRCLE)
            return false;
        if (t >= 50 && t <= 100)
            return false;
        return true;
    }

    /**
     * Recursively adds NDG constraints from the provided NDG constraint set.
     * <p>
     * For non-leaf nodes, the method traverses each child NDG set to add constraints.
     * For leaf nodes, it attempts to add an NDG constraint based on the first non-null constraint encountered.
     * </p>
     *
     * @param d the NDG constraint set to process
     * @param no the iteration limit based on the number of constraints in the set
     * @return true if any NDG constraint was successfully added; false otherwise
     */
    private boolean addNdg(NdgCs d, int no) {
        boolean r = false;
        int n = d.getCSindex();

        if (n >= 0) {
            for (int i = 0; i <= n; i++) {
                if (d.child[i] != null) {
                    if (addNdg(d.child[i], no)) ;
                    r = true;
                }
            }
            return r;
        }

        boolean added = false;

        for (int i = 0; i <= d.no; i++) {
            Cons c1 = d.allcns[i];
            if (c1 != null) {
                if (addNdg(c1))
                    added = true;
                break;
            }
        }
        return added;
    }

    /**
     * Adds NDG constraint(s) for the specified geometric constraint.
     *
     * <p>
     * This method inspects the type of the provided constraint and adds the corresponding
     * NDG constraint as follows:
     * </p>
     * <ul>
     *   <li><code>C_O_L</code>: Invokes <code>add_n_coll</code> to add a collinearity NDG constraint.</li>
     *   <li><code>C_O_P</code>: Invokes <code>add_ndg_para</code> to add a parallelism NDG constraint and
     *       then registers it with <code>add_ndgs</code>.</li>
     *   <li><code>C_O_T</code>: Invokes <code>add_ndg_perp</code> to add a perpendicularity NDG constraint and
     *       then registers it with <code>add_ndgs</code>.</li>
     *   <li><code>C_I_EQ</code>: Invokes <code>add_ndg_neq</code> to add an equality NDG constraint and
     *       then registers it with <code>add_ndgs</code>.</li>
     *   <li>For any other constraint type, the method returns <code>false</code> without modification.</li>
     * </ul>
     *
     * @param c the constraint to process for adding the corresponding NDG constraints
     * @return <code>true</code> if the constraint was processed for NDG addition; <code>false</code> otherwise
     */
    private boolean addNdg(Cons c) {
        switch (c.type) {
            case C_O_L:
                this.add_n_coll(c.ps[0], c.ps[1], c.ps[2], vndgs);
                break;
            case C_O_P: {
                CNdg d = add_ndg_para(c.ps[0], c.ps[1], c.ps[2], c.ps[3]);
                add_ndgs(d, vndgs);
            }
            break;
            case C_O_T: {
                CNdg d = add_ndg_perp(c.ps[0], c.ps[1], c.ps[2], c.ps[3]);
                add_ndgs(d, vndgs);
            }
            break;
            case C_I_EQ: {
                CNdg d = add_ndg_neq(c.ps[0], c.ps[1]);
                add_ndgs(d, vndgs);
            }
            break;
            default:
                return false;
        }
        return true;
    }

    private Vector vndgs = new Vector();
}
