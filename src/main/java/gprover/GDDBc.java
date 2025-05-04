/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-16
 * Time: 10:30:31
 * To change this template use File | Settings | File Templates.
 */
package gprover;

import wprover.GExpert;

import java.util.Vector;

/**
 * GDDBc class handles the geometric proof process and predicate management.
 */
public class GDDBc extends GDDAux {

    private static Rule test_r = new Rule(0);

    AuxPt axptc = null;

    /**
     * Constructs a new GDDBc instance initializing proof status, depth, and check value.
     */
    public GDDBc() {
        P_STATUS = 0;
        depth = 0;
        ck_value = true;
    }

    /**
     * Executes the fixed-point proof process, handling auxiliary points and node list generation.
     */
    void prove_fix() {

        axptc = null;
        pts_pno = pts_no;

        fixpoint();

        if (conc.pred != 0 && conc.pred != CO_NANG && !conc_xtrue()) {
            add_aux();
        }

        if (conc.pred == CO_NANG) {
            add_nodes(conc.p);
            if (all_ns.nx != null) {
                parse_llist();
            }
        } else {
            Vector v = new Vector();
            v.addAll(vauxpts);
            int n = v.size();
            if (n > 0) {
                debug_print("Total auxiliary Points: " + v.size());
                time_start();

                for (int i = 0; i < v.size(); i++) {
                    AuxPt ax = (AuxPt) v.get(i);
                    debug_print(ax.toString());
                    GTerm t = gt;
                    init_dbase();
                    setExample(t);
                    sbase();

                    int na = ax.getPtsNo();
                    for (int j = 0; j < na; j++) {
                        ProPoint pt = ax.getPtsbyNo(j);
                        pts_no++;
                        allpts[pts_no] = pt;
                        pt.ps[0] = pts_no;
                        add_auxpt(pt);
                    }
                    fixpoint();
                    if (conc_xtrue()) {
                        for (int j = 0; j < na; j++) {
                            ProPoint pt = ax.getPtsbyNo(j);
                            pt.set_name(fd_aux_name());
                            auxpt_string(pt);
                        }
                        axptc = ax;
                        break;
                    }
                    if (time_over())
                        break;
                }
            }
        }
    }

    /**
     * Retrieves the lemma value from the condition.
     *
     * @param x the condition from which to obtain the lemma
     * @return the lemma value associated with the condition
     */
    int PLM(Cond x) {
        return (x.u.get_lemma());
    }

    /**
     * Retrieves the condition predicate from the underlying data structure.
     *
     * @param x the condition used to extract the predicate
     * @return the corresponding condition predicate
     */
    Cond PCO(Cond x) {
        return (x.u.get_co());
    }

    /**
     * Creates a copy of the given predicate condition and updates its identifier.
     *
     * @param co the original condition to copy
     * @return a new condition copied from the provided one with updated identifiers
     */
    Cond cp_pred(Cond co) {
        Cond c = new_pr(co.pred);
        co.no = c.no = ++gno;

        c.rule = co.rule;
        c.u.cpv(co.u);
        for (int i = 0; i < Cond.MAX_GEO; i++)
            c.p[i] = co.p[i];
        return (c);
    }

    /**
     * Searches for an existing predicate condition equivalent to the given condition.
     *
     * @param co the condition to search for
     * @return the matching condition if found; otherwise, null
     */
    Cond fd_pred(Cond co) {
        Cond pr = all_nd.nx;
        for (; pr != null; pr = pr.nx) {
            if (compare_pred(co, pr)) return (pr);
        }
        return (null);
    }

    /**
     * Searches for an equivalent predicate condition based on its comparison value.
     *
     * @param co the condition to search against
     * @return the found condition if present; otherwise, null
     */
    Cond fd_prep(Cond co) {
        Cond pr = all_nd.nx;
        for (; pr != null; pr = pr.nx) {
            if (co.u.get_co() == pr.u.get_co()) return (pr);
        }
        return (null);
    }

    /**
     * Creates and returns a predicate condition using the provided type and point parameters.
     *
     * @param m the predicate type identifier
     * @param n the predicate condition identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param p3 the third point parameter
     * @param p4 the fourth point parameter
     * @param p5 the fifth point parameter
     * @param p6 the sixth point parameter
     * @param p7 the seventh point parameter
     * @param p8 the eighth point parameter
     * @return the constructed predicate condition or null if invalid
     */
    Cond add_pred(int m, int n, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
        tm_pr1.u.setnull();
        tm_pr1.pred = n;
        tm_pr1.no = 0;
        tm_pr1.u.ln = null;
        tm_pr1.p[0] = p1;
        tm_pr1.p[1] = p2;
        tm_pr1.p[2] = p3;
        tm_pr1.p[3] = p4;
        tm_pr1.p[4] = p5;
        tm_pr1.p[5] = p6;
        tm_pr1.p[6] = p7;
        tm_pr1.p[7] = p8;
        if (n == CO_COLL) {
            int[] p = tm_pr1.p;
            int nx = 1;
            for (int i = 1; i < p.length; i++) {
                int k = p[i];
                p[i] = 0;

                int j = 0;
                for (; j < nx; j++)
                    if (k == p[j])
                        break;

                if (j == nx)
                    p[nx++] = k;
            }
            if (p[2] == 0) return null;
        }
        if (n == CO_PARA) {
            if (p1 == p3 && p2 == p4 || p1 == p4 && p2 == p3) return null;
        }


        Cond pr3 = fd_pred(tm_pr1);
        if (pr3 != null) {
        } else {
            do_pred(tm_pr1);
            if (PCO((tm_pr1)) == null) {
                gprint(Cm.s2070);
                pr3 = new Cond(tm_pr1);
            } else {
                cp_pred(tm_pr1);
                pr3 = last_nd;
            }
        }
        return pr3;

    }

    /**
     * Determines the intersection point between two lines.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return the intersection point if it exists; otherwise, 0
     */
    int finter_ll(LLine l1, LLine l2) {
        char i, j;

        if (l1 == null || l2 == null || l1 == l2) return (0);
        for (i = 0; i <= l1.no; i++)
            for (j = 0; j <= l2.no; j++) {
                if (l1.pt[i] == l2.pt[j]) return (l1.pt[i]);
            }
        return (0);
    }

    /**
     * Determines the intersection point between two lines, excluding a specified point.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @param p1 the point to exclude from consideration
     * @return the intersection point if valid and not equal to p1; otherwise, 0
     */
    int finter_ll1(LLine l1, LLine l2, int p1) {
        char i, j;
        if (l1 == l2) return (0);
        for (i = 0; i <= l1.no; i++)
            for (j = 0; j <= l2.no; j++) {
                if (l1.pt[i] == l2.pt[j] && !meq_pt(l1.pt[i], p1)) return (l1.pt[i]);
            }
        return (0);
    }

    /**
     * Processes and displays the forward proof for the current condition.
     */
    void show_fproof() {
        if (conc_xtrue()) {
            last_nd = all_nd;
            cp_pred(conc);
            if (check_pred(last_nd)) { //"(The fact is trivially true)\r\n";
                return;
            }
            do_pred(last_nd);
            forw_pred(last_nd);
        }
    }

    /**
     * Processes forward predicates for the specified condition.
     *
     * Traverses the linked list of conditions and applies appropriate predicate rules.
     *
     * @param co the starting condition to process
     */
    void forw_pred(Cond co) {
        Cond pr1, pr2, pr3;
        show_dtype = 0;
        all_nd.nx = co;
        last_nd = co;
        co.no = 1;
        co.nx = null;
        gno = 1;

        for (Cond pr = all_nd.nx; pr != null; pr = pr.nx) {

            if (!isPFull() && !check_tValid(pr)) {
                last_nd = all_nd;
                all_nd.nx = null;
                return;
            }

            if (pr.u.isnull()) {  //The fact is trivially true
                if (show_detail && pr.pred == Gib.CO_ACONG) {
                    forw_eqangle(pr);
                }
                continue;
            } else if ((pr1 = PCO(pr)) == null) { //hyp
                pr.getRuleFromeFacts();
                continue;
            } else if (pr1.p[1] != 0) {

                for (; pr1 != null; pr1 = pr1.nx) {
                    if (pr1.pred == 0) {
                        continue;
                    }
                    if ((pr3 = fd_pred(pr1)) != null) {
                        pr.addcond(pr3);
                    } else {
                        do_pred(pr1);
//                        if (pr_coll) {
//                            if (!add_pr_coll(pr, pr1))
//                                continue;
//                        }
                        if (pr1.u.isnull()) { //obvious
                            pr.addcond(pr1);
                        } else if (PCO(pr1) == null) { //hyp
                            pr.addcond(pr1);
                        } else {
                            cp_pred(pr1);
                            pr1.no = last_nd.no;
                            pr.addcond(pr1);
                        }
                    }
                }
                pr.getRuleFromeFacts();
            } else {
                pr2 = pr1.nx;  /*pr0=last_nd; */
                switch (pr.pred) {
                    case CO_COLL:
                        add_pred_coll(pr, pr1, pr2);
                        break;
                    case CO_PARA:// && (pr1.pred == CO_COLL || pr1.pred == CO_PARA)) {
                        add_pred_para(pr, pr1, pr2);
                        break;
                    case CO_ACONG:
                        add_pred_as(pr, pr1, pr2);
                        break;
                    case CO_TANG:
                        add_pred_at(pr, pr1, pr2);
                        break;
                    case CO_PERP:
                        add_pred_perp(pr, pr1, pr2);
                        break;
                    case CO_ATNG:
                        add_pred_atn(pr, pr1, pr2);
                        break;
                    case CO_CYCLIC:
                        add_pred_cr(pr, pr1, pr2);
                        break;
                    default: {
                        for (; pr1 != null; pr1 = pr1.nx) {
                            pr3 = fd_prep(pr1);
                            if (pr3 != null) {
                                pr.addcond(pr3);
                            } else if ((pr3 = PCO(pr1)) == null) { //"(hyp)";
                                pr.addcond(pr1);
                            } else {
                                cp_pred(pr1);
                                pr.addcond(last_nd);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a predicate for a collision based on the given conditions.
     *
     * Determines the appropriate collision predicate by checking common line intersections
     * and adds a corresponding parallel predicate condition.
     *
     * @param pr  the main condition to add to
     * @param pr1 the first sub-condition containing line information
     * @param pr2 the second sub-condition containing line information
     */
    public void add_pred_coll(Cond pr, Cond pr1, Cond pr2) {
        int lemma = PLM(pr);
        switch (lemma) {
            case 1: {
                LLine l1 = pr1.u.ln;
                LLine l2 = pr2.u.ln;
                Cond c = null;
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        for (int k = 0; k < 3; k++) {
                            if (i != j && j != k && i != k) {
                                if (on_ln(pr.p[i], pr.p[j], l1) && on_ln(pr.p[k], l2))
                                    c = add_pred(0, CO_PARA, pr.p[i], pr.p[j], pr.p[k], get_lpt1(l2, pr.p[k]), 0, 0, 0, 0);
                                else if (on_ln(pr.p[i], pr.p[j], l2) && on_ln(pr.p[k], l1))
                                    c = add_pred(0, CO_PARA, pr.p[i], pr.p[j], pr.p[k], get_lpt1(l1, pr.p[k]), 0, 0, 0, 0);
                                if (c != null) {
                                    pr.addcond(Gib.R_P_COLL, c);
                                    return;
                                }
                            }
                        }
            }
            break;
        }
    }

    /**
     * Adds a predicate for parallelism based on the provided sub-conditions.
     *
     * Chooses the appropriate rule based on a lemma value and constructs the relevant predicate
     * conditions using line or angle data.
     *
     * @param pr  the main condition to add to
     * @param pr1 the first sub-condition providing primary predicate data
     * @param pr2 the second sub-condition providing supplementary predicate data
     */
    public void add_pred_para(Cond pr, Cond pr1, Cond pr2) {
        LLine ln;
        PLine pn1;
        int i1, k1, k2;
        k1 = k2 = 0;
        if (pr1 == null) return;
        int lemma = PLM(pr);
        switch (lemma) {
            case 188: {
                PLine pn = pr1.u.pn;
                LLine l1, l2;
                l1 = l2 = null;

                for (int i = 0; i <= pn.no; i++) {
                    if (xcoll_ln(fd_ln(pr.p[0], pr.p[1]), (pn.ln[i])))
                        l1 = pn.ln[i];
                    else if (xcoll_ln(fd_ln(pr.p[2], pr.p[3]), pn.ln[i]))
                        l2 = pn.ln[i];
                }
                Cond c = add_pred(0, CO_PARA, l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1], 0, 0, 0, 0);
                pr.addcond(c);
            }
            break;
            case 165: {
                Angles as = pr1.u.as;
                LLine l1, l2, l3, l4;
                if (on_ln(pr.p[0], pr.p[1], as.l3) && on_ln(pr.p[2], pr.p[3], as.l4)) {
                    l1 = as.l1;
                    l2 = as.l2;
                    l3 = as.l3;
                    l4 = as.l4;
                } else {
                    l1 = as.l2;
                    l2 = as.l1;
                    l3 = as.l4;
                    l4 = as.l3;
                }
                Cond c1 = add_as_pred_12(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l3, l4, l1, l2);
                Cond c2 = add_pred_pntn(0, CO_PARA, pr.p[0], pr.p[1], l3, pr.p[2], pr.p[3], l4);
                pr.addcond(R_AG_PP12, c1, c2);
            }
            break;
            case 166: {
                Angles as = pr1.u.as;
                LLine l1, l2, l3, l4;
                if (on_ln(pr.p[0], pr.p[1], as.l2) && on_ln(pr.p[2], pr.p[3], as.l4)) {
                    l1 = as.l1;
                    l2 = as.l2;
                    l3 = as.l3;
                    l4 = as.l4;
                } else {
                    l1 = as.l3;
                    l2 = as.l4;
                    l3 = as.l1;
                    l4 = as.l2;
                }
                Cond c1 = add_as_pred_13(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l2, l1, l4, l3);
                Cond c2 = add_pred_pntn(0, CO_PARA, pr.p[0], pr.p[1], l1, pr.p[2], pr.p[3], l3);
                pr.addcond(R_AG_PP13, c1, c2);
            }
            break;
            case R_P_COLL: {
                ln = pr1.u.ln;
                pn1 = pr2.u.pn;
                for (i1 = 0; i1 <= pn1.no; i1++) {
                    if ((k1 = finter_ll(ln, pn1.ln[i1])) != 0 && (k2 = finter_ll1(ln, pn1.ln[i1], k1)) != 0)
                        break;
                }
                if (i1 > pn1.no)
                    gexit(103);

                if (on_ln(pr.p[2], ln) && on_ln(pr.p[3], ln)) {
                    i1 = pr.p[2];
                    pr.p[2] = pr.p[0];
                    pr.p[0] = i1;
                    i1 = pr.p[3];
                    pr.p[3] = pr.p[1];
                    pr.p[1] = i1;
                }
                Cond co1 = add_pred(0, CO_COLL, pr.p[0], pr.p[1], k1, k2, 0, 0, 0, 0);
                Cond co2 = add_pred(1, CO_PARA, pr.p[2], pr.p[3], k1, k2, 0, 0, 0, 0);
                pr.addcond(co1, co2);
            }
            break;
            default: {
                if (pr1.pred == CO_COLL) {
                    ln = pr1.u.ln;
                    pn1 = pr2.u.pn;
                    if (ln == null || pn1 == null) return;
                    for (i1 = 0; i1 <= pn1.no; i1++) {
                        if ((k1 = finter_ll(ln, pn1.ln[i1])) != 0 && (k2 = finter_ll1(ln, pn1.ln[i1], k1)) != 0)
                            break;
                    }
                    if (i1 > pn1.no)
                        gexit(103);

                    if (on_ln(pr.p[2], ln) && on_ln(pr.p[3], ln)) {
                        i1 = pr.p[2];
                        pr.p[2] = pr.p[0];
                        pr.p[0] = i1;
                        i1 = pr.p[3];
                        pr.p[3] = pr.p[1];
                        pr.p[1] = i1;
                    }
                    Cond co1 = add_pred(0, CO_COLL, pr.p[0], pr.p[1], k1, k2, 0, 0, 0, 0);
                    Cond co2 = add_pred(1, CO_PARA, pr.p[2], pr.p[3], k1, k2, 0, 0, 0, 0);
                    pr.addcond(co1, co2);

                } else {
                    pn1 = pr1.u.pn;
                    for (i1 = 0; i1 <= pn1.no; i1++) {
                        if (on_pn(pn1.ln[i1], pr2.u.pn)) break;
                    }
                    if (i1 > pn1.no) {
                        gexit(104);
                    }
                    ln = pn1.ln[i1];
                    Cond co1 = add_pred(0, CO_PARA, pr.p[0], pr.p[1], ln.pt[0], ln.pt[1], 0, 0, 0, 0);
                    Cond co2 = add_pred(1, CO_PARA, pr.p[2], pr.p[3], ln.pt[0], ln.pt[1], 0, 0, 0, 0);
                    pr.addcond(co1, co2);
                }
                break;
            }
        }
    }

    /**
     * Adds a predicate for perpendicularity based on the given conditions.
     *
     * Selects the appropriate perpendicularity rule using a lemma value and established geometric
     * relationships, then adds the resulting predicate condition.
     *
     * @param pr  the main condition in which the predicate is to be added
     * @param pr1 the first sub-condition providing geometric entity information
     * @param pr2 the second sub-condition providing geometric entity information
     */
    public void add_pred_perp(Cond pr, Cond pr1, Cond pr2) {
        int lm = PLM(pr);

        switch (lm) {
            case 401: {
                TLine tn = pr1.u.tn;
                LLine ln1 = tn.l1;
                LLine ln2 = tn.l2;
                Cond c = add_pred(0, CO_PERP, ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1], 0, 0, 0, 0);
                pr.addcond(c);
            }
            break;
            case 142: {
                Angles as1 = pr1.u.as;
                Angles as2 = pr2.u.as;
                Cond c = new Cond();
                c.pred = CO_ACONG;
                c.p[0] = pr.p[0];
                c.p[1] = pr.p[1];
                c.p[2] = pr.p[2];
                c.p[3] = pr.p[3];
                c.p[4] = pr.p[2];
                c.p[5] = pr.p[3];
                c.p[6] = pr.p[0];
                c.p[7] = pr.p[1];

                add_as82_t(c, as1, as2);
                pr.add_allco(c.vlist);
            }
            break;
            case 145: {
                Angles as = pr1.u.as;
                LLine l1, l2, l3, l4;
                if (on_ln(pr.p[0], pr.p[1], as.l3) && on_ln(pr.p[2], pr.p[3], as.l4)) {
                    l1 = as.l1;
                    l2 = as.l2;
                    l3 = as.l3;
                    l4 = as.l4;
                } else {
                    l1 = as.l2;
                    l2 = as.l1;
                    l3 = as.l4;
                    l4 = as.l3;
                }
                Cond c1 = add_as_pred_12(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l3, l4, l1, l2);
                Cond c2 = add_pred(0, CO_PERP, c1.p[4], c1.p[5], c1.p[6], c1.p[7], 0, 0, 0, 0);
                pr.addcond(R_AG_TT12, c1, c2);
            }
            break;
            case 146: {
                Angles as = pr1.u.as;
                LLine l1, l2, l3, l4;
                if (on_ln(pr.p[0], pr.p[1], as.l2) && on_ln(pr.p[2], pr.p[3], as.l4)) {
                    l1 = as.l1;
                    l2 = as.l2;
                    l3 = as.l3;
                    l4 = as.l4;
                } else {
                    l1 = as.l3;
                    l2 = as.l4;
                    l3 = as.l1;
                    l4 = as.l2;
                }
//                cond cx = add_as_pred_12(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l3, l4, l1, l2);

                Cond c1 = add_as_pred_13(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l2, l1, l4, l3);
                Cond c2 = add_pred_tn13(0, CO_PERP, pr.p[0], pr.p[1], l1, pr.p[2], pr.p[3], l3);

                //        cx.addcond(0, c1);
                pr.addcond(0, c1, c2);
//                pr.addcond(R_AG_TT13, c1, c2);
            }
            break;
            case 402: {
                LLine ln = pr1.u.ln;
                TLine tn = pr2.u.tn;
                Cond c = add_pred(0, CO_PERP, tn.l1.pt[0], tn.l1.pt[1], tn.l2.pt[0], tn.l2.pt[1], 0, 0, 0, 0);
                pr.addcond(c);
            }
            break;
        }
    }

    /**
     * Adds a predicate for cyclic configurations based on the given conditions.
     *
     * Extracts circle information from the sub-conditions and creates cyclic predicate conditions.
     *
     * @param pr  the main condition to add to
     * @param pr1 the first sub-condition containing circle data
     * @param pr2 the second sub-condition containing circle data
     */
    public void add_pred_cr(Cond pr, Cond pr1, Cond pr2) {
        int lm = PLM(pr);
        switch (lm) {
            case 301: {
                ACir c1 = pr1.u.cr;
                ACir c2 = pr2.u.cr;
                Cond co1 = add_pred_cyclic1(pr, c1);
                Cond co2 = add_pred_cyclic1(pr, c2);
                pr.addcond(co1, co2);
            }
            break;
        }

    }

    /**
     * Constructs a cyclic predicate condition from the given circle.
     *
     * Calculates a set of points on the circle and creates a cyclic predicate condition.
     *
     * @param pr  the original condition used to reference predicate parameters
     * @param c1  the circle from which cyclic properties are derived
     * @return    a new predicate condition representing the cyclic property
     */
    public Cond add_pred_cyclic1(Cond pr, ACir c1) {
        int[] p = new int[4];
        for (int i = 0; i < 4; i++)
            p[i] = 0;
        int k = -1;

        for (int i = 1; i < 4; i++) {
            int t = pr.p[i];
            if (on_cir(t, c1))
                p[++k] = t;
        }
        if (k < 3) {
            for (int i = 0; i <= c1.no; i++) {
                int j;
                for (j = 0; j <= k; j++)
                    if (p[j] == c1.pt[i])
                        break;
                if (j > k)
                    p[++k] = c1.pt[i];
                if (k == 3) break;
            }
        }
        //if (c1.o != 0)
        //  p[3] = 0;
        return add_pred(0, CO_CYCLIC, c1.o, p[0], p[1], p[2], p[3], 0, 0, 0);
    }

    /**
     * Adds a predicate based on points and two lines.
     *
     * Determines appropriate points on the provided lines and constructs a predicate condition with
     * the supplied parameters.
     *
     * @param m  the mode or additional modifier for the predicate
     * @param n  the predicate type identifier
     * @param p1 the first point reference
     * @param p2 the second point reference
     * @param l1 the first line for the predicate
     * @param p3 the third point reference
     * @param p4 the fourth point reference
     * @param l2 the second line for the predicate
     * @return   a new predicate condition based on the given point and line data
     */
    public Cond add_pred_pntn(int m, int n, int p1, int p2, LLine l1, int p3, int p4, LLine l2) {
        int m1, m2, m3, m4;

        if (on_ln(p1, l1))
            m1 = p1;
        else if (on_ln(p2, l1))
            m1 = p2;
        else
            m1 = l1.pt[0];
        m2 = get_lpt1(l1, m1);

        if (on_ln(p3, l2))
            m3 = p3;
        else if (on_ln(p4, l2))
            m3 = p4;
        else
            m3 = l2.pt[0];
        m4 = get_lpt1(l2, m3);

        return add_pred(m, n, m1, m2, m3, m4, 0, 0, 0, 0);
    }

    /**
     * Adds a predicate based on tangent line intersections.
     *
     * Determines intersection points or valid points from the two lines and constructs a tangent rule
     * predicate with the specified parameters.
     *
     * @param m  the mode or modifier for the predicate
     * @param n  the predicate type
     * @param p1 the first point candidate for line l1
     * @param p2 the second point candidate for line l1
     * @param l1 the first line used to determine the tangent condition
     * @param p3 the first point candidate for line l2
     * @param p4 the second point candidate for line l2
     * @param l2 the second line used to determine the tangent condition
     * @return   a new predicate condition representing the tangent rule
     */
    public Cond add_pred_tn13(int m, int n, int p1, int p2, LLine l1, int p3, int p4, LLine l2) {
        int m1, m2, m3, m4;
        int o = inter_lls(l1, l2);
        if (o != 0)
            m1 = o;
        else if (on_ln(p1, l1))
            m1 = p1;
        else if (on_ln(p2, l1))
            m1 = p2;
        else
            m1 = l1.pt[0];
        m2 = get_lpt1(l1, m1);

        if (o != 0)
            m3 = o;
        else if (on_ln(p3, l2))
            m3 = p3;
        else if (on_ln(p4, l2))
            m3 = p4;
        else
            m3 = l2.pt[0];
        m4 = get_lpt1(l2, m3);
        return add_pred(m, n, m1, m2, m3, m4, 0, 0, 0, 0);
    }

    /**
     * Adds a predicate for angle tangency based on the provided conditions.
     *
     * Uses geometric relationships from the sub-conditions to construct an angle tangency predicate.
     *
     * @param pr  the main condition to which the predicate will be added
     * @param pr1 the first sub-condition containing angle information
     * @param pr2 the second sub-condition that may provide additional angle data
     */
    public void add_pred_atn(Cond pr, Cond pr1, Cond pr2) {
        int lm = PLM(pr);

        switch (lm) {
            case 188: {
                AngTn t = pr1.u.atn;
                int t1 = t.t1;
                int t2 = t.t2;
                Cond c = add_pred(0, CO_ATNG, get_lpt1(t.ln1, t1), t1, t1, get_lpt1(t.ln2, t1),
                        get_lpt1(t.ln3, t2), t2, t2, get_lpt1(t.ln4, t2));
                pr.addcond(c);
            }
            break;
            case 133: {
                Angles as = pr1.u.as;
                AngTn atn = pr2.u.atn;
                LLine l1 = as.l1;
                LLine l2 = as.l2;
                LLine l3 = as.l3;
                LLine l4 = as.l4;
                LLine s1 = atn.ln1;
                LLine s2 = atn.ln2;
                LLine s3 = atn.ln3;
                LLine s4 = atn.ln4;
                if (add_pred_atn_atnas(pr, l1, l2, l3, l4, s1, s2, s3, s4))
                    // A + B = 90 , B = C --> A + C = 90.
                    break;
            }
            break;

            default:
                break;
        }
    }

    /**
     * Adds predicates for angle tangency transformations based on paired line configurations.
     *
     * Compares the relationships between two sets of lines and constructs paired predicates to
     * represent angle tangent conditions.
     *
     * @param pr the main condition to add the predicates to
     * @param l1 the first line of the first pair
     * @param l2 the second line of the first pair
     * @param l3 the first line of the second pair
     * @param l4 the second line of the second pair
     * @param s1 the first line of the complementary pair
     * @param s2 the second line of the complementary pair
     * @param s3 the third line used for additional tangent validation
     * @param s4 the fourth line used for additional tangent validation
     * @return   true if the appropriate tangent predicates were added; false otherwise
     */
    public boolean add_pred_atn_atnas(Cond pr, LLine l1, LLine l2, LLine l3, LLine l4,
                                      LLine s1, LLine s2, LLine s3, LLine s4) {
        if (on_ln4(pr.p, l1, l2, s1, s2)) {
            Cond c1 = add_as_pred1(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l3, l4);
            Cond c2 = add_as_pred1(0, CO_ATNG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], s3, s4);
            pr.addcond(c1, c2);
            return true;
        } else if (on_ln4(pr.p, l2, l1, s2, s1)) {
            Cond c1 = add_as_pred1(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l4, l3);
            Cond c2 = add_as_pred1(0, CO_ATNG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], s4, s3);
            pr.addcond(c1, c2);
            return true;
        } else if (on_ln4(pr.p, s1, s2, l1, l2)) {
            Cond c1 = add_as_pred1(0, CO_ACONG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], l3, l4);
            Cond c2 = add_as_pred1(0, CO_ATNG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], s3, s4);
            pr.addcond(c1, c2);
            return true;
        } else if (on_ln4(pr.p, s2, s1, l2, l1)) {
            Cond c1 = add_as_pred1(0, CO_ACONG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], l4, l3);
            Cond c2 = add_as_pred1(0, CO_ATNG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], s4, s3);
            pr.addcond(c1, c2);
            return true;
        }
        return false;
    }

    /**
     * Adds an angle tangency predicate based on provided angle transformations.
     *
     * Analyzes the relationships between two angle-based conditions and constructs corresponding
     * tangent predicates.
     *
     * @param pr  the main condition to modify
     * @param pr1 the first sub-condition containing angle transformation data
     * @param pr2 the second sub-condition containing angle transformation data
     */
    public void add_pred_at(Cond pr, Cond pr1, Cond pr2) {
        int lm = PLM(pr);

        switch (lm) {
//            case R_AS_AT: {
//                anglet at1 = pr2.u.at;
//                angles as = pr1.u.as;
//                int p1 = get_lpt1(at1.l1, at1.p);
//                int p2 = get_lpt1(at1.l2, at1.p);
//
//                int v = getAtv(p1, at1.p, p2, at1.v);
//
//                l_line l1, l2, l3, l4;
//                l1 = at1.l1;
//                l2 = at1.l2;
//                l3 = as.l3;
//                l4 = as.l4;
//
//                cond co1, co2;
//                if (on_ln(pr.p[0], pr.p[1], l3) && on_ln(pr.p[1], pr.p[2], l4)) {
//                    co1 = add_pred(0, CO_TANG, p1, at1.p, p2, v, 0, 0, 0, 0);
//                    co2 = add_pred(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[1], pr.p[2], p1, at1.p, at1.p, p2);
//                } else {
//                    co1 = add_pred(0, CO_TANG, p2, at1.p, p1, -v, 0, 0, 0, 0);
//                    co2 = add_pred(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[1], pr.p[2], p2, at1.p, at1.p, p1);
//                }
//                pr.addcond(R_AS_AT, co1, co2);
//            }
//            break;
            case 104: {
                AngleT at1 = pr2.u.at;
                AngleT at2 = pr1.u.at;
                LLine l1 = at1.l1;
                LLine l2 = at2.l1;
                LLine l3 = at2.l2;
                int v1 = at1.v;
                int v2 = at2.v;
                int p1 = at1.p;
                int p2 = at2.p;
                int p3 = inter_ll(l1, l3);

                if (p1 != p2) {
                    v1 = getAtv(p3, p1, p2, v1);
                    v2 = getAtv(p1, p2, p3, v2);
                    Cond c1 = add_pred(0, CO_TANG, p3, p1, p2, v1, 0, 0, 0, 0);
                    Cond c2 = add_pred(0, CO_TANG, p1, p2, p3, v2, 0, 0, 0, 0);
                    pr.addcond(0, c1, c2);
                } else {
                    int k = get_lpt1(l2, p1);

                    if (on_ln(pr.p[0], pr.p[1], l1) && on_ln(pr.p[1], pr.p[2], l3)) {
                        int t1 = getAtv(pr.p[0], pr.p[1], k, v1);
                        int t2 = getAtv(k, pr.p[1], pr.p[2], v2);
                        if (t1 + t2 == pr.p[3])
                            lm = 0;
                        else
                            lm = R_TRI_ALL_AG_180;
                        Cond c1 = add_pred(0, CO_TANG, pr.p[0], pr.p[1], k, t1, 0, 0, 0, 0);
                        Cond c2 = add_pred(0, CO_TANG, k, pr.p[1], pr.p[2], t2, 0, 0, 0, 0);
                        pr.addcond(lm, c1, c2);
                    } else {
                        int t1 = getAtv(pr.p[0], pr.p[1], k, -v2);
                        int t2 = getAtv(k, pr.p[1], pr.p[2], -v1);
                        if (t1 + t2 == pr.p[3])
                            lm = 0;
                        else
                            lm = R_TRI_ALL_AG_180;
                        Cond c1 = add_pred(0, CO_TANG, pr.p[0], pr.p[1], k, t1, 0, 0, 0, 0);
                        Cond c2 = add_pred(0, CO_TANG, k, pr.p[1], pr.p[2], t2, 0, 0, 0, 0);
                        pr.addcond(lm, c1, c2);
                    }
                }
            }
            break;
            case 134: {
                Cm.print("134");
            }
            break;
            case 188: {
                AngleT at1 = pr1.u.at;
                int p1 = get_lpt1(at1.l1, at1.p);
                int p2 = get_lpt1(at1.l2, at1.p);
                int v = getAtv(p1, at1.p, p2, at1.v);
                Cond co1 = add_pred(0, CO_TANG, p1, at1.p, p2, v, 0, 0, 0, 0);
                pr.addcond(co1);
            }
            break;
            default: {
                if (pr2 == null) {
                    AngleT at1 = pr1.u.at;
                    int p1 = get_lpt1(at1.l1, at1.p);
                    int p2 = get_lpt1(at1.l2, at1.p);
                    int v = getAtv(p1, at1.p, p2, at1.v);
                    if (pr1.pred == CO_TANG) {
                        Cond co1 = add_pred(0, CO_TANG, p1, at1.p, p2, v, 0, 0, 0, 0);
                        pr.addcond(co1);
                    } else if (pr1.pred == CO_ACONG) {
                        Angles as = pr1.u.as;
                        Cond co1 = add_pred(0, CO_ACONG, as.l1.pt[0], as.l1.pt[1], as.l2.pt[0], as.l2.pt[1], as.l3.pt[0], as.l3.pt[1], as.l4.pt[0], as.l4.pt[1]);
                        pr.addcond(co1);
                    }
                }
                break;
            }

        }
    }

    /**
     * Processes the provided conditions and angle structures to add angle predicates.
     *
     * @param pr the main condition to add predicates to
     * @param pr1 the first condition containing angle information
     * @param pr2 the second condition containing angle information
     */
    public void add_pred_as(Cond pr, Cond pr1, Cond pr2) {
        Angles as1, as2;
        as1 = as2 = null;
        if (pr1 != null)
            as1 = pr1.u.as;
        if (pr2 != null)
            as2 = pr2.u.as;


        LLine l1 = null;
        LLine l2 = null;
        LLine l3 = null;
        LLine l4 = null;
        int lemma = PLM(pr);

        switch (lemma) {
            case 188: {
                Cond co1 = add_as_pred(0, CO_ACONG, as1.l1, as1.l2, as1.l3, as1.l4);
                pr.addcond(co1);
            }
            break;
            case 181: {
                LLine[] lns = null;
                lns = geti81(pr.p[0], pr.p[1], pr.p[2], pr.p[3], as1);
                if (lns != null) {
                    l1 = lns[0];
                    l2 = lns[1];
                }
                if (l1 == null || l2 == null) {
                    lns = geti81(pr.p[0], pr.p[1], pr.p[2], pr.p[3], as2);
                    if (lns != null) {
                        l1 = lns[0];
                        l2 = lns[1];
                    }
                    lns = geti81(pr.p[4], pr.p[5], pr.p[6], pr.p[7], as1);
                    if (lns != null) {
                        l3 = lns[0];
                        l4 = lns[1];
                    }
                } else {
                    lns = geti81(pr.p[4], pr.p[5], pr.p[6], pr.p[7], as2);
                    if (lns != null) {
                        l3 = lns[0];
                        l4 = lns[1];
                    }
                }
                if (l1 == null || l2 == null || l3 == null || l4 == null) {
                    Cond co1 = add_pred(1, CO_ACONG, pr.p[0], pr.p[1], pr.p[4], pr.p[5], pr.p[2], pr.p[3], pr.p[6], pr.p[7]);
                    pr.addcond(co1);
                } else {
                    Cond co1 = add_as_pred1(0, CO_ACONG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], l1, l2);
                    Cond co2 = add_as_pred1(0, CO_ACONG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], l3, l4);
                    pr.addcond(co1, co2);
                }
            }
            break;
            case 131: {
                AngleT at1 = pr1.u.at;
                AngleT at2 = pr2.u.at;
                int t1 = get_at2_v(pr.p[0], pr.p[1], pr.p[2], pr.p[3], at1, at2);
                int t2 = get_at2_v(pr.p[4], pr.p[5], pr.p[6], pr.p[7], at1, at2);
                Cond co1 = add_pred_4p_tang(1, CO_TANG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], t1);
                Cond co2 = add_pred_4p_tang(1, CO_TANG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], t2);
                pr.addcond(co1, co2);
            }
            break;
            case 132: {
                AngTn a1 = pr1.u.atn;
                AngTn atn = pr2.u.atn;
                LLine[] ls1 = get4lntn(pr.p[0], pr.p[1], pr.p[2], pr.p[3], a1.ln1, a1.ln2, a1.ln3, a1.ln4);
                if (ls1 == null)
                    ls1 = get4lntn(pr.p[0], pr.p[1], pr.p[2], pr.p[3], atn.ln1, atn.ln2, atn.ln3, atn.ln4);

                LLine[] ls2 = get4lntn(pr.p[4], pr.p[5], pr.p[6], pr.p[7], a1.ln1, a1.ln2, a1.ln3, a1.ln4);
                if (ls2 == null)
                    ls2 = get4lntn(pr.p[4], pr.p[5], pr.p[6], pr.p[7], atn.ln1, atn.ln2, atn.ln3, atn.ln4);


                if (ls1 == null || ls2 == null) {
                    Cond co1 = add_pred(1, CO_ATNG, pr.p[0], pr.p[1], pr.p[4], pr.p[5], pr.p[2], pr.p[3], pr.p[6], pr.p[7]);
                    pr.addcond(co1);
                } else {
                    Cond co1 = add_as_atn(0, CO_ATNG, pr.p[0], pr.p[1], pr.p[2], pr.p[3], ls1[0], ls1[1]);
                    Cond co2 = add_as_atn(0, CO_ATNG, pr.p[4], pr.p[5], pr.p[6], pr.p[7], ls2[0], ls2[1]);
                    pr.addcond(co1, co2);
                }

            }
            break;
            case 182:
            case 183: {
                if (as1 != null && as2 != null)
                    add_as82_t(pr, as1, as2);
            }
            break;
            default: {
                if (as1 != null && as2 != null) {
                    if (isPFull())
                        add_as82(pr, as1, as2);
                } else {
                    LLine ls = pr1.u.ln;
                    Angles as = pr2.u.as;
                    Angles s1 = pr.u.as;
                    l1 = as.l1;
                    l2 = as.l2;
                    l3 = as.l3;
                    l4 = as.l4;
                    LLine ln = new LLine();
                    ln.no = -1;

                    for (int i = 0; i < 4; i++) {
                        if (on_ln(pr.p[2 * i], pr.p[2 * i + 1], ls)) {
                            this.add_pt2l(pr.p[2 * i], ln);
                            this.add_pt2l(pr.p[2 * i + 1], ln);
                        }
                    }
                    for (int i = 0; i <= ls.no && ln.no < 2; i++)
                        add_pt2l(ls.pt[i], ln);
                    Cond co1 = add_pred(0, CO_COLL, ln.pt[0], ln.pt[1], ln.pt[2], 0, 0, 0, 0, 0);
                    Cond co2 = add_as_pred(1, CO_ACONG, as.l1, as.l2, as.l3, as.l4);
                    pr.addcond(co1, co2);
                }
            }
            break;
        }
    }

    /**
     * Creates angle predicates by comparing two angle structures and adds them to the condition.
     *
     * @param pr the target condition to add predicates to
     * @param as1 the first angle structure
     * @param as2 the second angle structure
     */
    public void add_as82_t(Cond pr, Angles as1, Angles as2) {
        LLine l1, l2, l3, l4, l5, l6, l7, l8;
        l1 = as1.l1;
        l2 = as1.l2;
        l3 = as1.l3;
        l4 = as1.l4;
        l5 = as2.l1;
        l6 = as2.l2;
        l7 = as2.l3;
        l8 = as2.l4;

        boolean b = add_as82t1(pr, l1, l2, l3, l4, l5, l6, l7, l8);
        if (!b)
            b = add_as82t1(pr, l1, l2, l3, l4, l7, l8, l5, l6);
        if (!b)
            b = add_as82t1(pr, l3, l4, l1, l2, l5, l6, l7, l8);
        if (!b)
            b = add_as82t1(pr, l3, l4, l1, l2, l7, l8, l5, l6);

        //
        if (!b)
            b = add_as82t1(pr, l1, l2, l3, l4, l6, l5, l8, l7);
        if (!b)
            b = add_as82t1(pr, l1, l2, l3, l4, l8, l7, l6, l5);
        if (!b)
            b = add_as82t1(pr, l3, l4, l1, l2, l6, l5, l8, l7);
        if (!b)
            b = add_as82t1(pr, l3, l4, l1, l2, l8, l7, l6, l5);

        if (!b) {
            Cond c1 = add_as_pred(0, CO_ACONG, as1.l1, as1.l2, as1.l3, as1.l4);
            Cond c2 = add_as_pred(0, CO_ACONG, as2.l1, as2.l2, as2.l3, as2.l4);
            pr.addcond(c1, c2);
        }
        return;
    }

    /**
     * Attempts to add angle congruence predicates using candidate lines from two angle structures.
     *
     * @param pr the condition containing predicate parameters
     * @param l1 the first line from the first structure
     * @param l2 the second line from the first structure
     * @param l3 the first line from the second structure
     * @param l4 the second line from the second structure
     * @param l5 a candidate line from the first structure of the second angle structure
     * @param l6 a candidate line from the first structure of the second angle structure
     * @param l7 a candidate line from the second structure of the second angle structure
     * @param l8 a candidate line from the second structure of the second angle structure
     * @return true if predicates were successfully added; false otherwise
     */
    public boolean add_as82t1(Cond pr, LLine l1, LLine l2, LLine l3, LLine l4,
                              LLine l5, LLine l6, LLine l7, LLine l8) {

        int[] p = pr.p;
        LLine[] lns = null;
        if (l2 == l5 && l4 == l7 && (lns = get_cond_lns(pr.p, l1, l2, l6, l3, l4, l8)) != null) {
            l2 = lns[0];
            l4 = lns[1];
            Cond c1 = add_as_pred1(0, CO_ACONG, p[0], p[1], l2, p[4], p[5], l4);
            Cond c2 = add_as_pred1(0, CO_ACONG, p[2], p[3], l2, p[6], p[7], l4);
            pr.addcond(c1, c2);
        } else if (l2 == l5 && l3 == l8 && (lns = get_cond_lns(pr.p, l1, l2, l6, l7, l3, l4)) != null) {
            l2 = lns[0];
            l3 = lns[1];
            Cond c1 = add_as_pred1(0, CO_ACONG, p[0], p[1], l2, l3, p[6], p[7]);
            Cond c2 = add_as_pred1(0, CO_ACONG, l2, p[2], p[3], p[4], p[5], l3);
            pr.addcond(c1, c2);
        } else if (l1 == l6 && l4 == l7 && (lns = get_cond_lns(pr.p, l5, l1, l2, l3, l4, l8)) != null) {
            l1 = lns[0];
            l4 = lns[1];
            Cond c1 = add_as_pred1(0, CO_ACONG, l1, p[2], p[3], p[4], p[5], l4);
            Cond c2 = add_as_pred1(0, CO_ACONG, p[0], p[1], l1, l4, p[6], p[7]);
            pr.addcond(c1, c2);
        } else if (l1 == l6 && l3 == l8 && (lns = get_cond_lns(pr.p, l5, l1, l2, l4, l3, l7)) != null) {
            l1 = lns[0];
            l3 = lns[1];
            Cond c1 = add_as_pred1(0, CO_ACONG, p[2], p[3], l1, p[6], p[7], l3);
            Cond c2 = add_as_pred1(0, CO_ACONG, p[0], p[1], l1, p[4], p[5], l3);
            pr.addcond(c1, c2);
        } else
            return false;
        return true;
    }

    /**
     * Retrieves candidate line pairs for predicates based on provided parameters.
     *
     * @param p the integer array containing predicate parameters
     * @param l1 the primary line candidate from the first structure
     * @param s1 the secondary candidate line corresponding to l1
     * @param l2 the primary line candidate from the second structure
     * @param l3 the secondary line candidate from the third structure
     * @param s2 the candidate line corresponding to l3
     * @param l4 a fallback line candidate
     * @return an array of two candidate lines if matching lines are found; null otherwise
     */
    LLine[] get_cond_lns(int[] p, LLine l1, LLine s1, LLine l2, LLine l3, LLine s2, LLine l4) {
        LLine[] lns = get_cond_ln(p, l1, s1, l2, l3, s2, l4);
        if (lns != null) return lns;
        lns = get_cond_ln(p, l2, s1, l1, l4, s2, l3);
        if (lns != null) return lns;
        lns = get_cond_ln(p, l3, s2, l4, l1, s1, l2);
        if (lns != null) return lns;
        lns = get_cond_ln(p, l4, s2, l3, l2, s1, l1);
        if (lns != null) return lns;
        return null;
    }

    /**
     * Retrieves candidate lines if all predicate parameters lie on the specified lines.
     *
     * @param p the integer array of predicate parameters
     * @param l1 the first line to check
     * @param s1 the candidate line corresponding to l1
     * @param l2 the second line to check
     * @param l3 the third line to check
     * @param s2 the candidate line corresponding to l3
     * @param l4 the fourth line to check
     * @return an array containing two candidate lines if conditions match; null otherwise
     */
    public LLine[] get_cond_ln(int[] p, LLine l1, LLine s1, LLine l2, LLine l3, LLine s2, LLine l4) {
        if (!on_ln4(p, l1, l2, l3, l4)) return null;
        LLine[] ns = new LLine[2];
        ns[0] = s1;
        ns[1] = s2;
        return ns;
    }

    /**
     * Checks whether each pair of predicate parameters corresponds to a point on the given lines.
     *
     * @param p the integer array of predicate parameters
     * @param l1 the first line for verification
     * @param l2 the second line for verification
     * @param l3 the third line for verification
     * @param l4 the fourth line for verification
     * @return true if each predicate parameter pair lies on the corresponding line; false otherwise
     */
    public boolean on_ln4(int[] p, LLine l1, LLine l2, LLine l3, LLine l4) {
        return on_ln(p[0], p[1], l1) && on_ln(p[2], p[3], l2) && on_ln(p[4], p[5], l3) && on_ln(p[6], p[7], l4);
    }

    /**
     * Processes angle structures to determine appropriate lines and add angle congruence predicates.
     *
     * @param pr the condition object holding predicate parameters
     * @param as1 the first angle structure
     * @param as2 the second angle structure
     */
    public void add_as82(Cond pr, Angles as1, Angles as2) {
        LLine l1, l2, l3, l4;
        l1 = l2 = l3 = l4 = null;

        LLine[] lns = null;
        lns = geti82(pr.p[0], pr.p[1], pr.p[6], pr.p[7], as1);
        if (lns != null) {
            l1 = lns[0];
            l2 = lns[1];
        }
        lns = geti82(pr.p[4], pr.p[5], pr.p[2], pr.p[3], as2);
        if (lns != null) {
            l3 = lns[0];
            l4 = lns[1];
        }
        if (l1 == null || l2 == null || l3 == null || l4 == null) {
            lns = geti82(pr.p[0], pr.p[1], pr.p[6], pr.p[7], as2);
            if (lns != null) {
                l1 = lns[0];
                l2 = lns[1];
            }
            geti82(pr.p[4], pr.p[5], pr.p[2], pr.p[3], as1);
            if (lns != null) {
                l3 = lns[0];
                l4 = lns[1];
            }
        }
        Cond co1 = add_as_pred2(0, CO_ACONG, pr.p[0], pr.p[1], l1, l2, pr.p[6], pr.p[7]);
        Cond co2 = add_as_pred2(1, CO_ACONG, pr.p[4], pr.p[5], l3, l4, pr.p[2], pr.p[3]);
        pr.addcond(co1, co2);
    }

    /**
     * Creates a tangent predicate using four point parameters and an additional value.
     *
     * @param m a mode or flag for predicate creation
     * @param n a predicate type identifier
     * @param p1 the first point of the first pair
     * @param p2 the second point of the first pair
     * @param p3 the first point of the second pair
     * @param p4 the second point of the second pair
     * @param p5 an additional parameter influencing the tangent value
     * @return a newly created condition representing a tangent predicate
     */
    public Cond add_pred_4p_tang(int m, int n, int p1, int p2, int p3, int p4, int p5) {
        int t1, t2, t3;
        t1 = t2 = t3 = 0;
        if (p1 == p3) {
            t1 = p2;
            t2 = p1;
            t3 = p4;
        } else if (p1 == p4) {
            t1 = p2;
            t2 = p1;
            t3 = p3;
        } else if (p2 == p3) {
            t1 = p1;
            t2 = p2;
            t3 = p4;
        } else if (p2 == p4) {
            t1 = p1;
            t2 = p2;
            t3 = p3;
        }
        return add_pred(m, n, t1, t2, t3, p5, 0, 0, 0, 0);
    }

    /**
     * Determines the angle transformation value based on the provided points and angle transformation objects.
     *
     * @param p1 the first point for comparison
     * @param p2 the second point for comparison
     * @param p3 the third point for comparison
     * @param p4 the fourth point for comparison
     * @param at1 the first angle transformation object
     * @param at2 the second angle transformation object
     * @return the computed angle value
     */
    public int get_at2_v(int p1, int p2, int p3, int p4, AngleT at1, AngleT at2) {
        LLine l1 = at1.l1;
        LLine l2 = at1.l2;
        LLine l3 = at2.l1;
        LLine l4 = at2.l2;
        if (on_ln(p1, p2, l1) && on_ln(p3, p4, l2))
            return at1.v;
        if (on_ln(p1, p2, l2) && on_ln(p3, p4, l1))
            return -at1.v;
        if (on_ln(p1, p2, l3) && on_ln(p3, p4, l4))
            return at2.v;
        if (on_ln(p1, p2, l4) && on_ln(p3, p4, l3))
            return -at2.v;
        return 0;
    }

    /**
     * Determines a pair of lines based on predicate parameters and candidate line options.
     *
     * @param a the first integer parameter for the predicate
     * @param b the second integer parameter for the predicate
     * @param c the third integer parameter for the predicate
     * @param d the fourth integer parameter for the predicate
     * @param ln1 the first candidate line
     * @param ln2 the second candidate line
     * @param ln3 the third candidate line
     * @param ln4 the fourth candidate line
     * @return an array containing two lines if a valid pair is found; null otherwise
     */
    LLine[] get4lntn(int a, int b, int c, int d, LLine ln1, LLine ln2, LLine ln3, LLine ln4) {
        LLine[] ls = new LLine[2];
        if (on_ln(a, b, ln1) && on_ln(c, d, ln2)) {
            ls[0] = ln3;
            ls[1] = ln4;
        } else if (on_ln(a, b, ln2) && on_ln(c, d, ln1)) {
            ls[0] = ln4;
            ls[1] = ln3;
        } else if (on_ln(a, b, ln3) && on_ln(c, d, ln4)) {
            ls[0] = ln1;
            ls[1] = ln2;
        } else if (on_ln(a, b, ln4) && on_ln(c, d, ln3)) {
            ls[0] = ln2;
            ls[1] = ln1;
        } else
            return null;
        return ls;
    }

    /**
     * Forwards equal angle predicate if the condition is of type CO_ACONG.
     *
     * @param pr the condition containing angle parameters
     */
    void forw_eqangle(Cond pr) {
        if (pr.pred != Gib.CO_ACONG) return;
        LLine ln1 = fd_ln(pr.p[0], pr.p[1]);
        LLine ln2 = fd_ln(pr.p[2], pr.p[3]);
        LLine ln3 = fd_ln(pr.p[4], pr.p[5]);
        LLine ln4 = fd_ln(pr.p[6], pr.p[7]);
        if (ln1 == null || ln2 == null || ln3 == null || ln4 == null) return;
        if (ln1 == ln3 && ln2 == ln4) {
            Cond co1 = add_pred(0, CO_COLL, pr.p[0], pr.p[1], pr.p[4], pr.p[5], 0, 0, 0, 0);
            Cond co2 = add_pred(0, CO_COLL, pr.p[2], pr.p[3], pr.p[6], pr.p[7], 0, 0, 0, 0);
            pr.addcond(co1, co2);
        }
    }

    /**
     * Iterates through all conditions and displays each predicate, including any sub-conditions.
     *
     * @return true after processing all predicates
     */
    boolean show_allpred() {
        Cond co = all_nd.nx;
        Cond pr1;
        while (co != null) {
            show_pred(co);
            Vector v = co.vlist;
            if (v != null)
                for (int i = 0; i < v.size(); i++) {
                    pr1 = (Cond) v.get(i);
                    show_pred(pr1);
                }
            co = co.nx;
        }
        return true;
    }

    /**
     * Displays the human-readable form of a given predicate condition.
     *
     * @param co the condition to display
     * @return true after the display has been generated
     */
    boolean show_pred(Cond co) {
        switch (co.pred) {
            case 0:
                break;
            case CO_COLL: {
                if (co.p[0] != 0) {

                    int k = 0;
                    for (int i = 0; i <= 5; i++) if (co.p[i] != 0) k = i;

                    String st = "";
                    for (int i = 0; i <= k; i++) {
                        if (co.p[i] != 0) {
                            st += (ANAME(co.p[i]));
                            if (i != k) st += (",");
                        }
                    }
                    co.sd = GExpert.getTranslationViaGettext("{0} are collinear", st);
                    // co.sd = st + (" " + Cm.s2760);
                } else {
                    this.setPrintToString();
                    this.show_ln(co.u.ln, true);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_PARA: {
                if (co.p[0] != 0) {

                    String str = ANAME(co.p[0]) + ANAME(co.p[1]) + Cm.PARALLEL_SIGN +
                            ANAME(co.p[2]) + ANAME(co.p[3]);
                    gprint(str);
                    co.sd = str;
                } else {
                    this.setPrintToString();
                    this.show_pn(co.u.pn);
                    co.sd = sout.toString();
                }
            }
            break;

            case CO_PERP: {
                if (co.p[0] != 0) {
                    String str = ANAME(co.p[0]) + ANAME(co.p[1]) + " " + Cm.PERPENDICULAR_SIGN + " "
                            + ANAME(co.p[2]) + ANAME(co.p[3]);
                    co.sd = str;
                } else {
                    this.setPrintToString();
                    this.show_tn(co.u.tn);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_RATIO: {
                int t1 = Sqrt(co.p[4]);
                int t2 = Sqrt(co.p[5]);
                String s = "";
                if (t1 > 0 && t2 > 0)
                    s = t1 + "/" + t2;
                else {
                    if (t1 > 0)
                        s += t1;
                    else
                        s += "sqrt(" + co.p[4] + ")";
                    s += "/";
                    if (t2 > 0)
                        s += t2;
                    else
                        s += "sqrt(" + co.p[5] + ")";
                }

                String str = ANAME(co.p[0]) + ANAME(co.p[1]) + "/" +
                        ANAME(co.p[2]) + ANAME(co.p[3]) + " = " + s;
                gprint(str);
                co.sd = str;
            }
            break;
            case CO_CONG: /* cong */ {
                if (co.p[0] != 0) {
                    if (co.p[4] == co.p[5]) {
                        String str = ANAME(co.p[0]) + ANAME(co.p[1]) + " = " + ANAME(co.p[2]) + ANAME(co.p[3]);
                        gprint(str);
                        co.sd = str;
                    } else {
                        int t1 = Sqrt(co.p[4]);
                        int t2 = Sqrt(co.p[5]);
                        String s = "";
                        if (t1 > 0 && t2 > 0)
                            s = t1 + "/" + t2;
                        else {
                            if (t1 > 0)
                                s += t1;
                            else
                                s += "sqrt(" + co.p[4] + ")";
                            s += "/";
                            if (t2 > 0)
                                s += t2;
                            else
                                s += "sqrt(" + co.p[5] + ")";
                        }

                        String str = ANAME(co.p[0]) + ANAME(co.p[1]) + "/" +
                                ANAME(co.p[2]) + ANAME(co.p[3]) + " = " + s;
                        gprint(str);
                        co.sd = str;
                    }
                } else {
                    this.setPrintToString();
                    this.show_cg(co.u.cg);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_ACONG: {
                if (co.p[0] != 0) {
                    if (isPFull() || check_eqangle_t(co.p[0], co.p[1], co.p[2], co.p[3],
                            co.p[4], co.p[5], co.p[6], co.p[7])) {

                        String str = get_fang_str(co.p[0], co.p[1], co.p[2], co.p[3]);
                        str += (" = ");
                        str += get_fang_str(co.p[4], co.p[5], co.p[6], co.p[7]);
                        co.sd = str;
                    } else {
                        String str = get_fang_str(co.p[0], co.p[1], co.p[2], co.p[3]);
                        str += (" + ");
                        str += get_fang_str(co.p[4], co.p[5], co.p[6], co.p[7]);
                        str += " = 180";
                        co.sd = str;
                    }

                } else {
                    this.setPrintToString();
                    this.show_as(co.u.as);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_TANG:
                if (co.p[0] != 0) {
                    String str = get_fang_str(co.p[0], co.p[1], co.p[1], co.p[2]);
                    int v = getAtv(co.p[0], co.p[1], co.p[2], co.p[3]);
                    if (v == 0)
                        v = co.p[3] * 10;

                    if (v % A_TIME == 0)
                        co.sd = str + " = " + v / A_TIME;
                    else
                        co.sd = str + " = " + ((float) v) / A_TIME;
                } else {
                    this.setPrintToString();
                    this.show_at(co.u.at);
                    co.sd = sout.toString();
                }
                break;
            case CO_ATNG:
                if (co.p[0] != 0) {
                    String str = get_fang_str(co.p[0], co.p[1], co.p[2], co.p[3]);
                    String str1 = get_fang_str(co.p[4], co.p[5], co.p[6], co.p[7]);
                    co.sd = str + " + " + str1 + " = 90";
                } else {
                    this.setPrintToString();
                    this.show_atn(co.u.atn);
                    co.sd = sout.toString();
                }
                break;
            case CO_MIDP: {
                if (co.p[0] != 0) {
                    // String str = Cm.s2729 + "(" + ANAME(co.p[0]) + "," + ANAME(co.p[1]) + ANAME(co.p[2]) + ")";
                    String str = GExpert.getTranslationViaGettext("{0} is the midpoint of {1}",
                            ANAME(co.p[0]), ANAME(co.p[1]) + ANAME(co.p[2]));
                    co.sd = str;
                } else {
                    this.setPrintToString();
                    this.show_md(co.u.md);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_PROD: /*eq_produc, eq_ratio*/

            {
                if (co.p[0] != 0) {
                    String st = ANAME(co.p[0]) + ANAME(co.p[1]) + "·" +
                            ANAME(co.p[6]) + ANAME(co.p[7]) + " = " +
                            ANAME(co.p[2]) + ANAME(co.p[3]) + "·" +
                            ANAME(co.p[4]) + ANAME(co.p[5]);
                    co.sd = st;
                } else {
                    this.setPrintToString();
                    show_ra(co.u.ra);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_CYCLIC: /*circle */
                if (co.p[0] != 0 && co.p[3] == 0) {
                    String st = "";
                    st = ANAME(co.p[0]) + ANAME(co.p[1]) + " = " +
                            ANAME(co.p[0]) + ANAME(co.p[2]);
                    co.sd = st;
                } else if (co.p[1] != 0) {
                    String st = "";
                    if (co.p[0] != 0) {
                        st += ("(" + ANAME(co.p[0]) + ")");
                    }
                    st += show_pts(co, 1);
                    st = GExpert.getTranslationViaGettext("{0} are concyclic", st);
                    co.sd = st;

                } else {

                    this.setPrintToString();
                    this.show_cr(co.u.cr);
                    co.sd = sout.toString();
                }
                break;
            case CO_STRI: {
                if (co.p[0] != 0) {
                    // String st = Cm.PC_TRI + " " + ANAME(co.p[0]) + ANAME(co.p[1]) + ANAME(co.p[2]) + Cm.SIMILAR_SIGN +//" ~ " +
                    //         Cm.PC_TRI + " " + ANAME(co.p[3]) + ANAME(co.p[4]) + ANAME(co.p[5]);
                    String st = GExpert.getTranslationViaGettext("△{0}", ANAME(co.p[0]) + ANAME(co.p[1]) + ANAME(co.p[2]))
                            + "~"
                            + GExpert.getTranslationViaGettext("△{0}", ANAME(co.p[3]) + ANAME(co.p[4]) + ANAME(co.p[5]));

                    co.sd = st;
                } else {
                    this.setPrintToString();
                    this.show_ct(co.u.st);
                    co.sd = sout.toString();
                }
            }
            break;
            case CO_CTRI: {
                if (co.p[0] != 0) {
                    String st = GExpert.getTranslationViaGettext("△{0}", ANAME(co.p[0]) + ANAME(co.p[1]) + ANAME(co.p[2]))
                            + "≅" +
                            GExpert.getTranslationViaGettext("△{0}", ANAME(co.p[3]) + ANAME(co.p[4]) + ANAME(co.p[5]));
                    co.sd = st;
                } else {
                    this.setPrintToString();
                    this.show_ct(co.u.st);
                    co.sd = sout.toString();
                }
            }
        }
        return true;
    }

    /**
     * Compares two predicate conditions to check for equivalence.
     *
     * @param co the first condition to compare
     * @param pr the second condition to compare
     * @return true if both conditions are considered equivalent, false otherwise
     */
    boolean compare_pred(Cond co, Cond pr)   // do_pred(x,x,3)   ----------> check two predicate is the same.
    {
        if (co.pred != pr.pred) return (false);
        boolean ex = true;
        for (int i = 0; i <= 7; i++)
            if (co.p[i] != pr.p[i]) {
                ex = false;
                break;
            }
        if (ex)
            return (true);

        switch (co.pred) {
            case 0:
                break;
            case CO_COLL:
                break;
            case CO_PARA: /* para */
                if (((co.p[0] == pr.p[0] && co.p[1] == pr.p[1]) || (co.p[0] == pr.p[1] && co.p[1] == pr.p[0])) &&
                        ((co.p[2] == pr.p[2] && co.p[3] == pr.p[3]) || (co.p[2] == pr.p[3] && co.p[3] == pr.p[2])))
                    ex = true;
                else if (((co.p[0] == pr.p[2] && co.p[1] == pr.p[3]) || (co.p[0] == pr.p[3] && co.p[1] == pr.p[2])) &&
                        ((co.p[2] == pr.p[0] && co.p[3] == pr.p[1]) || (co.p[2] == pr.p[1] && co.p[3] == pr.p[0])))
                    ex = true;
                break;
            case CO_PERP:
                if (((co.p[0] == pr.p[0] && co.p[1] == pr.p[1]) || (co.p[0] == pr.p[1] && co.p[1] == pr.p[0])) &&
                        ((co.p[2] == pr.p[2] && co.p[3] == pr.p[3]) || (co.p[2] == pr.p[3] && co.p[3] == pr.p[2])))
                    ex = true;
                else if (((co.p[0] == pr.p[2] && co.p[1] == pr.p[3]) || (co.p[0] == pr.p[3] && co.p[1] == pr.p[2])) &&
                        ((co.p[2] == pr.p[0] && co.p[3] == pr.p[1]) || (co.p[2] == pr.p[1] && co.p[3] == pr.p[0])))
                    ex = true;
                break;
            case CO_CONG: /* cong */
                if (co.p[0] == pr.p[1] && co.p[1] == pr.p[0] && co.p[2] == pr.p[3] && co.p[3] == pr.p[2])
                    ex = true;
                else if (co.p[0] == pr.p[2] && co.p[1] == pr.p[3] && co.p[2] == pr.p[0] && co.p[3] == pr.p[1])
                    ex = true;
                else if (co.p[0] == pr.p[3] && co.p[1] == pr.p[2] && co.p[2] == pr.p[1] && co.p[3] == pr.p[0])
                    ex = true;
                break;
            case CO_ACONG:
                break;
            case CO_MIDP:
                if (co.p[0] == pr.p[0] && co.p[1] == pr.p[2] && co.p[2] == pr.p[1])
                    ex = true;
                break;
            case CO_PROD:
                break;
            case CO_CYCLIC:
                if (co.p[0] == pr.p[0] && eq_chs(co.p, 7, pr.p, 7))
                    ex = true;
                break;
            case CO_STRI:
            case CO_CTRI:
                break;
            case CO_ORTH:
            case CO_INCENT:
                break;
        }
        return ex;
    }

    /**
     * Checks whether a given predicate is obviously true based on its parameters.
     *
     * @param co the condition to check
     * @return true if the condition meets an obvious criteria, false otherwise
     */
    boolean check_pred(Cond co) // if it obviousely.
    {
        boolean va = false;
        switch (co.pred) {
            case 0:
                break;
            case CO_COLL:
                if (co.p[0] != 0 && (co.p[0] == co.p[1] || co.p[0] == co.p[2] || co.p[1] == co.p[2]))
                    va = true;
                break;
            case CO_PARA: /* para */
                if (co.p[0] != 0 && (co.p[0] == co.p[1] || co.p[2] == co.p[3]))
                    va = true;
                break;
            case CO_PERP: /* perp */
                if (co.p[0] != 0 &&
                        (co.p[0] == co.p[1] || co.p[2] == co.p[3]))
                    va = true;
                break;
            case CO_CONG: /* cong */
                if (co.p[0] != 0 && (co.p[0] == co.p[1] && co.p[2] == co.p[3]))
                    va = true;
                break;

            case CO_ACONG:  /* eqangle */
                break;
            case CO_MIDP: /* midpoint */
                break;
            case CO_PROD: /*eq_produc, eq_ratio*/
                break;
            case CO_CYCLIC: /*circle */
                break;
            default:
                break;
        }
        return va;

    }

    /**
     * Processes the predicate condition by executing the corresponding operation based on its type.
     *
     * @param co the condition to process
     */
    final void do_pred(Cond co) {

        switch (co.pred) {
            case 0:
                break;
            case CO_COLL:
                if (co.p[0] != 0)
                    co.u.ln = fo_ln(co.p, 7);
                break;
            case CO_PARA:
                if (co.p[0] != 0)
                    co.u.pn = fo_pn1(co.p[0], co.p[1], co.p[2], co.p[3]);
                break;
            case CO_PERP:
                if (co.p[0] != 0)
                    co.u.tn = fo_tn1(co.p[0], co.p[1], co.p[2], co.p[3]);
                break;
            case CO_CONG:
                if (co.p[0] != 0) {
                    co.u.cg = fo_cg(co.p[0], co.p[1], co.p[2], co.p[3]);
                    if (co.u.cg == null)
                        co.u.cg = fo_cg1(co.p[0], co.p[1], co.p[2], co.p[3]);
                }
                break;
            case CO_ACONG:
                if (co.p[0] != 0)
                    co.u.as = fo_as1(co.p[0], co.p[1], co.p[2], co.p[3], co.p[4], co.p[5], co.p[6], co.p[7]);
                break;
            case CO_TANG:
                if (co.p[0] != 0)
                    co.u.at = fo_at(co.p[0], co.p[1], co.p[2]);
                break;
            case CO_ATNG:
                if (co.p[0] != 0)
                    co.u.atn = fo_atn(co.p[0], co.p[1], co.p[2], co.p[3], co.p[4], co.p[5], co.p[6], co.p[7]);
                break;
            case CO_MIDP:
                if (co.p[0] != 0)
                    co.u.md = fo_md(co.p[0], co.p[1], co.p[2]);
                break;
            case CO_PROD:
                if (co.p[0] != 0)
                    co.u.ra = fo_ra(co.p[0], co.p[1], co.p[2], co.p[3], co.p[4], co.p[5], co.p[6], co.p[7]);
                break;
            case CO_CYCLIC:
                if (co.p[1] != 0)
                    co.u.cr = fo_cr(co.p[0], co.p[1], co.p[2], co.p[3], co.p[4]);
                break;
            case CO_STRI:
            case CO_CTRI:
                if (co.p[0] != 0)
                    co.u.st = fo_st((co.pred == CO_CTRI ? 0 : 1), 1, co.p[0], co.p[1], co.p[2], co.p[3], co.p[4], co.p[5]);
                break;
            case CO_ORTH:
            case CO_INCENT:
                break;
            case CO_RATIO: {
                co.u.cg = fo_cg1(co.p[0], co.p[1], co.p[2], co.p[3]);
                break;
            }
            default:
                gprint("co-pred " + co.pred + "error");
                break;
        }
    }

    /**
     * Generates a string representation of point names from a condition's parameters.
     *
     * @param co the condition containing the point parameters
     * @param n the starting index of the points in the array
     * @return the generated string of point names
     */
    String show_pts(Cond co, int n) {
        int i, k;
        i = k = 0;
        for (i = n; i <= 5; i++) if (co.p[i] != 0) k = i;

        String s = "";
        for (i = n; i <= k; i++) {
            if (co.p[i] != 0) {
                s += (ANAME(co.p[i]));
                if (i != k) s += (",");
            }
        }
        return s;
    }

    /**
     * Compares two arrays of characteristic values to determine if they have the same elements.
     *
     * @param ch1 the first array of characteristics
     * @param n1 the number of valid entries in the first array
     * @param ch2 the second array of characteristics
     * @param n2 the number of valid entries in the second array
     * @return true if both arrays are equal (ignoring order), false otherwise
     */
    boolean eq_chs(int[] ch1, int n1, int[] ch2, int n2)   // Check !!
    {
        int id1 = 1;

        for (int i = 1; i <= n1; i++) {
            int j = 0;
            for (j = 0; j <= (n2 - 1); j++)
                if (ch1[id1] == ch2[j + 1])
                    break;
            if (j > (n2 - 1))
                return (false);
            id1++;
        }
        id1 = 1;
        for (int i = 1; i <= n2; i++) {
            int j = 0;
            for (j = 0; j <= (n1 - 1); j++)
                if (ch2[id1] == ch1[j + 1])
                    break;
            if (j > (n1 - 1))
                return (false);
            id1++;
        }
        return (true);
    }

    /**
     * Retrieves two lines based on the given endpoints from an Angles object's line set.
     *
     * @param a first point parameter associated with the first line
     * @param b second point parameter associated with the first line
     * @param c first point parameter associated with the second line
     * @param d second point parameter associated with the second line
     * @param as the Angles object containing four lines (l1, l2, l3, l4)
     * @return an array of two LLine objects if a valid pair is found; otherwise, null
     */
    LLine[] geti81(int a, int b, int c, int d, Angles as) {
        LLine l1, l2, l3, l4;
        l1 = as.l1;
        l2 = as.l2;
        l3 = as.l3;
        l4 = as.l4;
        return get4ln(l1, l2, l3, l4, a, b, c, d);
    }

    /**
     * Determines and returns two complementary LLine objects from the provided four lines
     * based on matching endpoint conditions.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @param l4 the fourth line
     * @param a first endpoint parameter for matching
     * @param b second endpoint parameter for matching
     * @param c third endpoint parameter for matching
     * @param d fourth endpoint parameter for matching
     * @return an array of two LLine objects satisfying the conditions; returns null if none found
     */
    LLine[] get4ln(LLine l1, LLine l2, LLine l3, LLine l4, int a, int b, int c, int d) {
        LLine[] ln = new LLine[2];

        if (l1.type == 0)
            l1 = fd_ln(l1.pt[0], l1.pt[1]);
        if (l2.type == 0)
            l2 = fd_ln(l2.pt[0], l2.pt[1]);
        if (l3.type == 0)
            l3 = fd_ln(l3.pt[0], l3.pt[1]);
        if (l4.type == 0)
            l4 = fd_ln(l4.pt[0], l4.pt[1]);

        if (on_ln(a, l1) && on_ln(b, l1) && on_ln(c, l2) && on_ln(d, l2)) {
            ln[0] = l3;
            ln[1] = l4;
        } else if (on_ln(a, l2) && on_ln(b, l2) && on_ln(c, l1) && on_ln(d, l1)) {
            ln[0] = l4;
            ln[1] = l3;
        } else if (on_ln(a, l1) && on_ln(b, l1) && on_ln(c, l3) && on_ln(d, l3)) {
            ln[0] = l2;
            ln[1] = l4;
        } else if (on_ln(a, l3) && on_ln(b, l3) && on_ln(c, l1) && on_ln(d, l1)) {
            ln[0] = l4;
            ln[1] = l2;
        } else if (on_ln(a, l3) && on_ln(b, l3) && on_ln(c, l4) && on_ln(d, l4)) {
            ln[0] = l1;
            ln[1] = l2;
        } else if (on_ln(a, l4) && on_ln(b, l4) && on_ln(c, l3) && on_ln(d, l3)) {
            ln[0] = l2;
            ln[1] = l1;
        } else if (on_ln(a, l2) && on_ln(b, l2) && on_ln(c, l4) && on_ln(d, l4)) {
            ln[0] = l1;
            ln[1] = l3;
        } else if (on_ln(a, l4) && on_ln(b, l4) && on_ln(c, l2) && on_ln(d, l2)) {
            ln[0] = l3;
            ln[1] = l1;
        } else
            return null;
        return ln;

    }

    /**
     * Retrieves a pair of LLine objects from an Angles object based on endpoint criteria.
     *
     * @param a first endpoint parameter
     * @param b second endpoint parameter
     * @param c third endpoint parameter
     * @param d fourth endpoint parameter
     * @param as the Angles object with four lines (l1, l2, l3, l4)
     * @return an array of two LLine objects if matching conditions are met; otherwise, null
     */
    LLine[] geti82(int a, int b, int c, int d, Angles as) {
        LLine l1, l2, l3, l4;
        l1 = as.l1;
        l2 = as.l2;
        l3 = as.l3;
        l4 = as.l4;
        if (l1.type == 0)
            l1 = fd_ln(l1.pt[0], l1.pt[1]);
        if (l2.type == 0)
            l2 = fd_ln(l2.pt[0], l2.pt[1]);
        if (l3.type == 0)
            l3 = fd_ln(l3.pt[0], l3.pt[1]);
        if (l4.type == 0)
            l4 = fd_ln(l4.pt[0], l4.pt[1]);

        LLine[] ln = new LLine[2];
        if (on_ln(a, l1) && on_ln(b, l1) && on_ln(c, l4) && on_ln(d, l4)) {
            ln[0] = l2;
            ln[1] = l3;
        } else if (on_ln(a, l4) && on_ln(b, l4) && on_ln(c, l1) && on_ln(d, l1)) {
            ln[0] = l3;
            ln[1] = l2;
        } else if (on_ln(a, l2) && on_ln(b, l2) && on_ln(c, l3) && on_ln(d, l3)) {
            ln[0] = l1;
            ln[1] = l4;
        } else if (on_ln(a, l3) && on_ln(b, l3) && on_ln(c, l2) && on_ln(d, l2)) {
            ln[0] = l1;
            ln[1] = l4;
        } else
            return null;
        return ln;
    }

    /**
     * Determines whether the current conclusion is valid.
     *
     * @return true if the conclusion satisfies the required geometric conditions; false otherwise
     */
    boolean conc_xtrue() {
        return (docc());
    }

    /**
     * Evaluates the current conclusion by testing various geometric predicates.
     *
     * @return true if the condition passes the geometric tests; false otherwise
     */
    boolean docc() {
        boolean j = false;
        switch (conc.pred) {
            case CO_COLL:  /* collinear */
                j = xcoll(conc.p[0], conc.p[1], conc.p[2]);
                break;
            case CO_PARA:  /* parallel */
                j = xpara(conc.p[0], conc.p[1], conc.p[2], conc.p[3]);
                break;
            case CO_PERP: /* perpendicular */
                j = xperp(conc.p[0], conc.p[1], conc.p[2], conc.p[3]);
                break;
            case CO_CONG: /*congruent */
                if (conc.p[4] == conc.p[5])
                    j = xcong(conc.p[0], conc.p[1], conc.p[2], conc.p[3]);
                else {
                    j = xcong1(conc.p[0], conc.p[1], conc.p[2], conc.p[3], conc.p[4], conc.p[5]);
                }
                break;
            case CO_ACONG: /* angle congruent */
                j = xacong(conc.p[0], conc.p[1], conc.p[2], conc.p[3], conc.p[4], conc.p[5], conc.p[6], conc.p[7]);
                break;
            case CO_TANG:
                j = this.xatcong(conc.p[0], conc.p[1], conc.p[2], conc.p[3]);
                break;
            case CO_MIDP: /* midpoint */
                j = xmid(conc.p[0], conc.p[1], conc.p[2]);
                break;
            case CO_PROD: /* eq-product */
                j = xeq_ratio(conc.p[0], conc.p[1], conc.p[2], conc.p[3],
                        conc.p[4], conc.p[5], conc.p[6], conc.p[7]);
                break;
            case CO_CYCLIC:  /*cocircle */
                j = xcir4(0, conc.p[1], conc.p[2], conc.p[3], conc.p[4]);
                break;
            case CO_STRI:  // sim_triangle
                j = xsim_tri(conc.p[0], conc.p[1], conc.p[2],
                        conc.p[3], conc.p[4], conc.p[5]);
                break;
            case CO_CTRI:  //con_trinagle
                j = xcon_tri(conc.p[0], conc.p[1], conc.p[2],
                        conc.p[3], conc.p[4], conc.p[5]);
                break;
            case CO_PBISECT: {
                gprint("add here!");
            }
            break;
            case CO_RATIO: {
                j = xcong1(conc.p[0], conc.p[1], conc.p[2], conc.p[3], conc.p[4], conc.p[5]);
            }
            break;
            case CO_ORTH: {

                j = xperp(conc.p[0], conc.p[1], conc.p[2], conc.p[3])
                        && xperp(conc.p[0], conc.p[2], conc.p[1], conc.p[3]);

                break;
            }
            case CO_INCENT: {
                break;
            }


            default:
                gprint("not supported conclusion!");

        }
        return (j);
    }

    /**
     * Displays the composite condition by printing concatenated condition information.
     *
     * @param co the condition object to be displayed
     */
    void show_cos(Cond co) {
        if (co != null) {
            gprint(Cm.s2727);
            show_co(co);
            co = co.nx;
            if (co == null) {
            } else if (co.nx == null) {
                gprint(Cm.s2728);
                show_co(co);
            } else {
                while (co != null) {
                    gprint(", ");
                    show_co(co);
                    co = co.nx;
                }
            }
            gprint(".");
        }
    }

    /**
     * Generates textual representations for all geometric entities in the database.
     * This includes midpoints, lines, points, circles, angles, and other conditions.
     */
    public void gen_dbase_text() {
        this.setPrintToString();

        MidPt md = all_md.nx;
        while (md != null) {
            show_md(md);
            md.text = this.getPrintedString();
            md = md.nx;
        }
        LLine ln = all_ln.nx;
        while (ln != null) {
            show_ln(ln, true);
            ln.text = this.getPrintedString();
            ln = ln.nx;
        }
        PLine pn = all_pn.nx;
        while (pn != null) {
            show_pn(pn);
            pn.text = this.getPrintedString();
            pn = pn.nx;
        }
        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type != 0) {
                show_tn(tn);
                tn.text = this.getPrintedString();
            }
            tn = tn.nx;
        }
        ACir cr = all_cir.nx;
        while (cr != null) {
            show_cr(cr);
            cr.text = this.getPrintedString();
            cr = cr.nx;
        }
        Angles as = all_as.nx;
        while (as != null) {
            show_as(as);
            as.text = this.getPrintedString();
            as = as.nx;
        }
        AngSt ast = all_ast.nx;
        while (ast != null) {
            show_ast(ast);
            ast.text = this.getPrintedString();
            ast = ast.nx;
        }
        AngleT at = all_at.nx;
        while (at != null) {
            show_at(at);
            at.text = this.getPrintedString();
            at = at.nx;
        }
        AngTn atn = all_atn.nx;
        while (atn != null) {
            if (atn.type != 0) {
                show_atn(atn);
                atn.text = this.getPrintedString();
            }
            atn = atn.nx;
        }

        CSegs cgs = all_cgs.nx;
        while (cgs != null) {
            if (cgs.type != 0) {
                show_cseg(cgs);
                cgs.text = this.getPrintedString();
            }
            cgs = cgs.nx;
        }

        CongSeg cg = all_rg.nx;
        while (cg != null) {
            show_cg(cg);
            cg.text = this.getPrintedString();
            cg = cg.nx;
        }
        RatioSeg ra = all_ra.nx;
        while (ra != null) {
            show_ra(ra);
            ra.text = this.getPrintedString();
            ra = ra.nx;
        }
        STris sts = all_sts.nx;
        while (sts != null) {
            show_sts(sts);
            sts.text = this.getPrintedString();
            sts = sts.nx;
        }
        sts = all_cts.nx;
        while (sts != null) {
            show_sts(sts);
            sts.text = this.getPrintedString();
            sts = sts.nx;
        }
    }

    /**
     * Displays the provided condition using the default display format.
     *
     * @param co the condition object to display
     */
    void show_co(Cond co) {
        show_dtype = 0;
        show_pred(co);
    }

    /**
     * Displays an angle statement from an angle statement object.
     *
     * @param ast the angle statement object to be displayed
     */
    public void show_ast(AngSt ast) {
        gprint(ast.no + ".");
        if (ast.no < 10) gprint("  ");
        for (int j = 0; j < ast.no; j++) {
            show_agll(ast.ln1[j], ast.ln2[j]);
            if (j != ast.no - 1)
                gprint(" = ");
        }
        ast.sd = this.getPrintedString();
    }

    /**
     * Displays the angle represented by an AngleT object, including its line representation and computed value.
     *
     * @param at the AngleT object containing angle information
     */
    void show_at(AngleT at) {
        if (at == null) return;
        show_agll(at.l1, at.l2);
        int p = at.p;
        int p1 = get_lpt1(at.l1, p);
        int p2 = get_lpt1(at.l2, p);
        int v = getAtv(p1, p, p2, at.v);
        if (v % A_TIME == 0)
            gprint(" = " + v / A_TIME);
        else
            gprint(" = " + ((float) v) / A_TIME);
    }

    /**
     * Inserts a CClass object into a sorted Vector based on its identifier.
     *
     * @param obj the CClass object to be inserted
     * @param v the Vector collection maintaining sorted CClass objects
     */
    public void insertVector(CClass obj, Vector v) {
        if(obj == null)
            return;
        
        for(int i=0; i < v.size(); i ++)
        {
            CClass c = (CClass)v.get(i);
            if(c.id > obj.id)
            {
                v.add(i,obj);
                return;
            }
        }
        v.add(obj);
    }

    /**
     * Returns a Vector containing all MidPt objects with a non-zero type.
     *
     * @return a Vector of valid MidPt objects
     */
    public Vector getAll_md() {
        Vector v = new Vector();
        MidPt md = all_md.nx;
        while (md != null) {
            if (md.type != 0)
                insertVector(md, v);
            md = md.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all LLine objects with a non-zero type and at least 2 points.
     *
     * @return a Vector of valid LLine objects
     */
    public Vector getAll_ln() {
        Vector v = new Vector();
        LLine ln = all_ln.nx;
        while (ln != null) {
            if (ln.type != 0 && ln.no >= 2)
                insertVector(ln, v);
            ln = ln.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all PLine objects with a non-zero type.
     *
     * @return a Vector of valid PLine objects
     */
    public Vector getAll_pn() {
        Vector v = new Vector();
        PLine pn = all_pn.nx;
        while (pn != null) {
            if (pn.type != 0)
                insertVector(pn, v);
            pn = pn.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all TLine objects with a non-zero type.
     *
     * @return a Vector of valid TLine objects
     */
    public Vector getAll_tn() {
        Vector v = new Vector();
        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type != 0)
                insertVector(tn, v);
            tn = tn.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all ACir objects with a non-zero type and at least 2 points.
     *
     * @return a Vector of valid ACir objects
     */
    public Vector getAll_cir() {
        Vector v = new Vector();
        ACir cr = all_cir.nx;
        while (cr != null) {
            if (cr.type != 0 && cr.no >= 2)
                insertVector(cr, v);
            cr = cr.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all AngSt objects with a non-zero type.
     *
     * @return a Vector of valid AngSt objects
     */
    public Vector getAll_as() {
        Vector v = new Vector();
        AngSt ast = all_ast.nx;
        while (ast != null) {
            if (ast.type != 0)
                insertVector(ast, v);
            ast = ast.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all AngleT objects with a non-zero type.
     *
     * @return a Vector of valid AngleT objects
     */
    public Vector getAll_at() {
        Vector v = new Vector();
        AngleT at = all_at.nx;
        while (at != null) {
            if (at.type != 0)
                insertVector(at, v);
            at = at.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all AngTn objects with a non-zero type.
     *
     * @return a Vector of valid AngTn objects
     */
    public Vector getAll_atn() {
        Vector v = new Vector();
        AngTn at = all_atn.nx;
        while (at != null) {
            if (at.type != 0)
                insertVector(at, v);
            at = at.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all CSegs objects with a non-zero type.
     *
     * @return a Vector of valid CSegs objects
     */
    public Vector getAll_cg() {
        Vector v = new Vector();
        CSegs cg = all_cgs.nx;
        while (cg != null) {
            if (cg.type != 0)
                insertVector(cg, v);
            cg = cg.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all CongSeg objects with a non-zero type.
     *
     * @return a Vector of valid CongSeg objects
     */
    public Vector getAll_rg() {
        Vector v = new Vector();
        CongSeg cg = all_rg.nx;
        while (cg != null) {
            if (cg.type != 0)
                insertVector(cg, v);
            cg = cg.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all RatioSeg objects with a non-zero type.
     *
     * @return a Vector of valid RatioSeg objects
     */
    public Vector getAll_ra() {
        Vector v = new Vector();
        RatioSeg ra = all_ra.nx;
        while (ra != null) {
            if (ra.type != 0)
                insertVector(ra, v);
            ra = ra.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all STris objects with a non-zero type.
     *
     * @return a Vector of valid STris objects
     */
    public Vector getAll_sts() {
        Vector v = new Vector();
        STris sts = all_sts.nx;
        while (sts != null) {
            if (sts.type != 0)
                insertVector(sts, v);
            sts = sts.nx;
        }
        return v;
    }

    /**
     * Returns a Vector containing all STris objects (alternate set) with a non-zero type.
     *
     * @return a Vector of valid alternate STris objects
     */
    public Vector getAll_cts() {
        Vector v = new Vector();
        STris sts = all_cts.nx;
        while (sts != null) {
            if (sts.type != 0)
                insertVector(sts, v);
            sts = sts.nx;
        }
        return v;
    }

    /**
     * Searches for a fact based on the specified type and string parameters.
     *
     * @param t the fact type selector
     * @param s1 the first string parameter
     * @param s2 the second string parameter
     * @param s3 the third string parameter
     * @return a Vector containing the matching fact objects
     */
    public Vector search_a_fact(int t, String s1, String s2, String s3) {
        int t1 = this.fd_pt(s1);
        int t2 = this.fd_pt(s2);
        int t3 = this.fd_pt(s3);
        Vector v = new Vector();
        Object o = null;
        switch (t) {
            case 0:
                o = fd_lnno3(t1, t2);
                fo_cg2(t1, t2, v);
                break;
            case 1:
                o = fo_md(t1, t2, t3);
                break;
            case 2:
                fd_cr_p3(t1, t2, t3);
                break;
            case 3:
                fd_pnl(fd_ln(t1, t2));
                break;
            case 4:
                fd_tn(fd_ln(t1, t2));
                break;
            case 5: {
                AngSt st = fd_ast(fd_ln(t1, t2), fd_ln(t2, t3));
                if (st != null)
                    v.add(st);

                AngleT at = fd_at(t1, t2, t3);
                if (at != null)
                    v.add(at);
                fo_atn2(t1, t2, t3, v);
            }
            break;
            case 6:
                fo_tri2(t1, t2, t3, v);
                break;
        }
        if (o != null)
            v.add(o);
        return v;
    }

    /**
     * Retrieves a line if it has at least two points.
     *
     * @param a the first point parameter
     * @param b the second point parameter
     * @return the matching line if it exists and meets the criteria; otherwise, null
     */
    private LLine fd_lnno3(int a, int b) {
        LLine ln = fd_ln(a, b);
        if (ln != null && ln.no >= 2)
            return ln;
        return null;
    }

    /**
     * Searches for angle transformation nodes and adds them to the provided vector.
     *
     * @param a the first point parameter
     * @param b the second point parameter
     * @param c the third point parameter
     * @param v the vector to collect matching angle transformation nodes
     */
    private void fo_atn2(int a, int b, int c, Vector v) {
        AngTn atn = all_atn.nx;

        while (atn != null) {
            if (atn.type != 0) {
                if (on_ln(a, b, atn.ln1) && on_ln(b, c, atn.ln2) || on_ln(a, b, atn.ln2) && on_ln(b, c, atn.ln1))
                    v.add(atn);
                else if (on_ln(a, b, atn.ln3) && on_ln(b, c, atn.ln4) || on_ln(a, b, atn.ln4) && on_ln(b, c, atn.ln3))
                    v.add(atn);
            }
            atn = atn.nx;
        }
    }

    /**
     * Searches for a triangle matching the given parameters and adds it to the vector.
     *
     * @param a the first point parameter
     * @param b the second point parameter
     * @param c the third point parameter
     * @param v the vector to collect the matching triangle nodes
     * @return always returns null
     */
    STris fo_tri2(int a, int b, int c, Vector v) {
        STris st = all_sts.nx;
        while (st != null) {
            if (on_sts1(a, b, c, st) >= 0) {
                v.add(st);
                break;
            }
            st = st.nx;
        }
        st = all_cts.nx;
        while (st != null) {
            if (on_sts1(a, b, c, st) >= 0) {
                v.add(st);
                break;
            }
            st = st.nx;
        }
        return null;
    }

    /**
     * Searches for and adds congruence or circular segments matching the given points.
     *
     * @param a the first point parameter
     * @param b the second point parameter
     * @param v the vector to collect matching segment objects
     * @return always returns null
     */
    CClass fo_cg2(int a, int b, Vector v) {
        CSegs cgs = all_cgs.nx;

        while (cgs != null) {
            if (on_cgs(a, b, cgs) && !v.contains(cgs))
                v.add(cgs);
            cgs = cgs.nx;
        }

        CongSeg cg = all_rg.nx;
        while (cg != null) {
            if (cg.p1 == a && cg.p2 == b || cg.p2 == a && cg.p1 == b || cg.p3 == a && cg.p4 == b || cg.p3 == b && cg.p4 == a) {
                if (!v.contains(cg))
                    v.add(cg);
            }
            cg = cg.nx;
        }
        return null;
    }

    /**
     * Creates a predicate for angle similarity based on four provided lines.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @param l4 the fourth line
     * @return the constructed predicate object
     */
    Cond add_as_pred(int m, int n, LLine l1, LLine l2, LLine l3, LLine l4) {
        int p1, p2, p3, p4, p5, p6, p7, p8;
        int a = inter_ll(l1, l2);
        int b = inter_ll(l3, l4);

        if (a != 0) {
            p2 = a;
            p3 = a;
            p1 = get_lpt1(l1, a);
            p4 = get_lpt1(l2, a);
        } else {
            p1 = l1.pt[0];
            p2 = l1.pt[1];
            p3 = l2.pt[0];
            p4 = l2.pt[1];
        }
        if (b != 0) {
            p6 = b;
            p7 = b;
            p5 = get_lpt1(l3, b);
            p8 = get_lpt1(l4, b);

        } else {
            p5 = l3.pt[0];
            p6 = l3.pt[1];
            p7 = l4.pt[0];
            p8 = l4.pt[1];
        }
        return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * Creates a predicate from point parameters and two lines.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param p3 the third point parameter
     * @param p4 the fourth point parameter
     * @param l3 the third line
     * @param l4 the fourth line
     * @return the constructed predicate object
     */
    Cond add_as_pred1(int m, int n, int p1, int p2, int p3, int p4, LLine l3, LLine l4) {
        int p5, p6, p7, p8;
        int b = inter_ll(l3, l4);

        if (b != 0) {
            p6 = b;
            p7 = b;
            p5 = get_lpt1(l3, b);
            p8 = get_lpt1(l4, b);

            if (!check_eqangle_t(p1, p2, p3, p4, p5, p6, p7, p8)) {
                int t = get_anti_pt(l4, p7, p8);
                if (t != 0)
                    p8 = t;
                else {
                    t = get_anti_pt(l3, p6, p5);
                    if (t != 0)
                        p5 = t;
                }
            }

        } else {
            p5 = l3.pt[0];
            p6 = l3.pt[1];
            p7 = l4.pt[0];
            p8 = l4.pt[1];
        }
        return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * Creates a predicate using a line to derive additional point parameters.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param l1 the line used to obtain additional point information
     * @param p5 the fifth point parameter
     * @param p6 the sixth point parameter
     * @param l2 the second line used to obtain additional point information
     * @return the constructed predicate object
     */
    Cond add_as_pred1(int m, int n, int p1, int p2, LLine l1, int p5, int p6, LLine l2) {
        int p3, p4, p7, p8;
        if (on_ln(p1, l1)) {
            p3 = p1;
            p4 = get_lpt1(l1, p1);
        } else if (on_ln(p2, l1)) {
            p3 = p2;
            p4 = get_lpt1(l1, p2);
        } else {
            p3 = l1.pt[0];
            p4 = l1.pt[1];
        }

        if (on_ln(p5, l2)) {
            p7 = p5;
            p8 = get_lpt1(l2, p5);
        } else if (on_ln(p6, l2)) {
            p7 = p6;
            p8 = get_lpt1(l2, p6);
        } else {
            p7 = l2.pt[0];
            p8 = l2.pt[1];
        }
        if (!check_eqangle_t(p1, p2, p3, p4, p5, p6, p7, p8)) {
            int t = get_anti_pt(l2, p7, p8);
            if (t != 0)
                p8 = t;
            else {
                t = get_anti_pt(l1, p3, p4);
                if (t != 0)
                    p4 = t;
            }
        }

        return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * Creates a predicate from two pairs of points on two lines by selecting the most likely configuration.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param m1 the first candidate point on the first line
     * @param m2 the second candidate point on the first line
     * @param m3 the first candidate point on the second line
     * @param m4 the second candidate point on the second line
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @param l4 the fourth line
     * @return the constructed predicate object
     */
    Cond add_as_pred_12(int m, int n, int m1, int m2, int m3, int m4, LLine l1, LLine l2, LLine l3, LLine l4) {
// m1,m2 on l1,  m3,m4 on l2;  find the most likely predicate
        int p1, p2, p3, p4, p5, p6, p7, p8;
        p2 = p3 = inter_lls(l1, l2);
        if (p2 != 0) {
            if (m1 == p2)
                p1 = m2;
            else if (m2 == p2)
                p1 = m1;
            else
                p1 = m1 < m2 ? m1 : m2;

            if (m3 == p2)
                p4 = m4;
            else if (m4 == p2)
                p4 = m3;
            else
                p4 = m3 < m4 ? m3 : m4;

            p6 = p7 = inter_lls(l3, l4);
            if (p6 != 0) {
                p5 = get_lpt1(l3, p6);
                p8 = get_lpt1(l4, p6);

                if (!check_eqangle_t(p1, p2, p3, p4, p5, p6, p7, p8)) {
                    int t = get_anti_pt(l4, p7, p8);
                    if (t != 0)
                        p8 = t;
                    else {
                        t = get_anti_pt(l3, p6, p5);
                        if (t != 0)
                            p5 = t;
                    }
                }

                return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
            }
        }
        return add_pred(m, n, l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1], l3.pt[0], l3.pt[1], l4.pt[0], l4.pt[1]);
    }

    /**
     * Creates a predicate from two pairs of points on two lines with a different configuration.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param m1 the first candidate point on the first line
     * @param m2 the second candidate point on the first line
     * @param m3 the first candidate point on the third line
     * @param m4 the second candidate point on the third line
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @param l4 the fourth line
     * @return the constructed predicate object
     */
    Cond add_as_pred_13(int m, int n, int m1, int m2, int m3, int m4, LLine l1, LLine l2, LLine l3, LLine l4) {
// m1,m2 on l1,  m3,m4 on l3;  find the most likely predicate
        int p1, p2, p3, p4, p5, p6, p7, p8;
        p2 = p3 = inter_lls(l1, l2);
        if (p2 != 0)

        {
            if (m1 == p2)
                p1 = m2;
            else if (m2 == p2)
                p1 = m1;
            else
                p1 = m1 < m2 ? m1 : m2;

            p4 = get_lpt1(l2, p2);

            p6 = p7 = inter_lls(l3, l4);
            if (p6 != 0) {
                if (m3 == p6)
                    p5 = m4;
                else if (m4 == p6)
                    p5 = m3;
                else
                    p5 = get_lpt1(l3, p6);

                p8 = get_lpt1(l4, p6);

                if (!check_eqangle_t(p1, p2, p3, p4, p5, p6, p7, p8)) {
                    int t = get_anti_pt(l4, p7, p8);
                    if (t != 0)
                        p8 = t;
                    else {
                        t = get_anti_pt(l2, p3, p4);
                        if (t != 0)
                            p4 = t;
                    }
                }

                return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);

            }
        }
        return add_pred(m, n, l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1], l3.pt[0], l3.pt[1], l4.pt[0], l4.pt[1]);
    }

    /**
     * Creates a predicate from a line and additional point parameters.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param l1 the line providing initial point information
     * @param p1 the first point parameter on the line
     * @param p2 the second point parameter on the line
     * @param p5 the fifth point parameter
     * @param p6 the sixth point parameter
     * @param l2 the second line used for deriving further point information
     * @return the constructed predicate object
     */
    Cond add_as_pred1(int m, int n, LLine l1, int p1, int p2, int p5, int p6, LLine l2) {
        int p3, p4, p7, p8;
        if (on_ln(p1, l1)) {
            p3 = p1;
            p4 = get_lpt1(l1, p1);
        } else if (on_ln(p2, l1)) {
            p3 = p2;
            p4 = get_lpt1(l1, p2);
        } else {
            p3 = l1.pt[0];
            p4 = l1.pt[1];
        }

        if (on_ln(p5, l2)) {
            p7 = p5;
            p8 = get_lpt1(l2, p5);
        } else if (on_ln(p6, l2)) {
            p7 = p6;
            p8 = get_lpt1(l2, p6);
        } else {
            p7 = l2.pt[0];
            p8 = l2.pt[1];
        }
        if (!check_eqangle_t(p3, p4, p1, p2, p5, p6, p7, p8)) {
            int t = get_anti_pt(l2, p7, p8);
            if (t != 0)
                p8 = t;
            else {
                t = get_anti_pt(l1, p3, p4);
                if (t != 0)
                    p4 = t;
            }
        }

        return add_pred(m, n, p3, p4, p1, p2, p5, p6, p7, p8);
    }

    /**
     * Creates a predicate from two lines and additional point parameters.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param l1 the first line
     * @param l2 the second line
     * @param p5 the fifth point parameter
     * @param p6 the sixth point parameter
     * @return the constructed predicate object
     */
    Cond add_as_pred1(int m, int n, int p1, int p2, LLine l1, LLine l2, int p5, int p6) {
        int p3, p4, p7, p8;
        if (on_ln(p1, l1)) {
            p3 = p1;
            p4 = get_lpt1(l1, p1);
        } else if (on_ln(p2, l1)) {
            p3 = p2;
            p4 = get_lpt1(l1, p2);
        } else {
            p3 = l1.pt[0];
            p4 = l1.pt[1];
        }

        if (on_ln(p5, l2)) {
            p7 = p5;
            p8 = get_lpt1(l2, p5);
        } else if (on_ln(p6, l2)) {
            p7 = p6;
            p8 = get_lpt1(l2, p6);
        } else {
            p7 = l2.pt[0];
            p8 = l2.pt[1];
        }
        if (!check_eqangle_t(p1, p2, p3, p4, p7, p8, p5, p6)) {
            int t = get_anti_pt(l2, p7, p8);
            if (t != 0)
                p8 = t;
            else {
                t = get_anti_pt(l1, p3, p4);
                if (t != 0)
                    p4 = t;
            }
        }
        return add_pred(m, n, p1, p2, p3, p4, p7, p8, p5, p6);
    }

    /**
     * Creates an angle transformation predicate based on given point parameters and two lines.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param p3 the third point parameter
     * @param p4 the fourth point parameter
     * @param l3 the third line
     * @param l4 the fourth line
     * @return the constructed predicate object
     */
    Cond add_as_atn(int m, int n, int p1, int p2, int p3, int p4, LLine l3, LLine l4) {
        int p5, p6, p7, p8;
        int b = inter_ll(l3, l4);
        p5 = p6 = p7 = p8 = 0;

        if (b != 0) {
            p6 = b;
            p7 = b;
            p5 = get_lpt1(l3, b);
            p8 = get_lpt1(l4, b);
            if (!check_angle_ls_90(p5, b, p8)) {
                int t = get_anti_pt(l3, b, p5);
                if (t != 0)
                    p5 = t;
                else {
                    t = get_anti_pt(l4, b, p8);
                    if (t != 0)
                        p8 = t;
                }
            }
        } else {
            p5 = l3.pt[0];
            p6 = l3.pt[1];
            p7 = l4.pt[0];
            p8 = l4.pt[1];
        }
        return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * Creates a predicate using point parameters and two lines for congruence segments.
     *
     * @param m the predicate type
     * @param n the predicate identifier
     * @param p1 the first point parameter
     * @param p2 the second point parameter
     * @param l2 the first line
     * @param l3 the second line
     * @param p7 the seventh point parameter
     * @param p8 the eighth point parameter
     * @return the constructed predicate object
     */
    Cond add_as_pred2(int m, int n, int p1, int p2, LLine l2, LLine l3, int p7, int p8) {
        int p3, p4, p5, p6;
        if (on_ln(p1, l2)) {
            p3 = p1;
            if (p3 != l2.pt[0])
                p4 = l2.pt[0];
            else
                p4 = l2.pt[1];
        } else if (on_ln(p2, l2)) {
            p3 = p2;
            if (p3 != l2.pt[0])
                p4 = l2.pt[0];
            else
                p4 = l2.pt[1];
        } else {
            p3 = l2.pt[0];
            p4 = l2.pt[1];
        }

        if (on_ln(p7, l3)) {
            p5 = p7;
            if (p5 != l3.pt[0])
                p6 = l3.pt[0];
            else
                p6 = l3.pt[1];
        } else if (on_ln(p8, l3)) {
            p5 = p8;
            if (p5 != l3.pt[0])
                p6 = l3.pt[0];
            else
                p6 = l3.pt[1];
        } else {
            p5 = l2.pt[0];
            p6 = l2.pt[1];
        }
        return add_pred(m, n, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    /**
     * Checks if the given condition is valid. Validity depends on whether the predicate is CO_ACONG
     * and specific point values satisfy the anti-angle criteria.
     *
     * @param c the condition to be validated
     * @return true if the condition is valid; false otherwise
     */
    public boolean check_tValid(Cond c) {
        if (isPFull()) return true;
        if (c.pred != CO_ACONG) return true;
        if (c.p[0] == 0) return true;
        if ((c.p[0] == c.p[2] || c.p[0] == c.p[3] || c.p[1] == c.p[2] || c.p[1] == c.p[3]) &&
                (c.p[4] == c.p[6] || c.p[4] == c.p[7] || c.p[5] == c.p[6] || c.p[5] == c.p[7]))
            return true;
        return true;
    }

    /**
     * Parses the global node list, processes sublists, and displays the list content using backup and preview mechanisms.
     */
    public void parse_llist() {
        LList ns = all_ns.nx;
        if (ns == null) return;
        search_ns(ns);

        Vector v = getPVector();
        last_ns = ns;
        ns.nx = null;
        show_llists(v);

        Vector vl = new Vector();

        while (v.size() != 0) {

            for (int i = 0; i < v.size(); i++) {
                LList ls = (LList) v.get(i);
                Mnde m = ls.mf[0];
                if (m == null) {
                } else if (m.tr != null) {
                    search_ag_split(ls);
                } else if (m.t == 90) {
                    search_t_list(ls);
                } else if (m.t == 180) {
                    search_p_list(ls);
                }
            }
            LList ls = ns.nx;
            v.clear();
            while (ls != null) {
                if (ls.nd <= 1)
                    vl.add(ls);
                else
                    v.add(ls);
                ls = ls.nx;
            }
            if (vl.size() != 0)
                break;

            last_ns = ns;
            ns.nx = null;
        }
    }

    /**
     * Retrieves the next proof head node list from the global node list.
     *
     * @param ls the current node list
     * @return the next node list for proof head, or null if no further node is available
     */
    LList get_next_ls_prove_head(LList ls) {
        LList ns = all_ns.nx;
        if (ns == null) return null;
        ns = ns.nx;

        LList rs = null;
        if (ls == null)
            rs = ns;
        else {
            while (ns != ls)
                ns = ns.nx;
            rs = ns;
        }
        if (rs == null) return null;
        parse_bk_list(rs);
        show_lprv(rs);
        return rs;
    }

    /**
     * Displays the list preview by printing the node list and all its associated rules.
     *
     * @param ls the node list to preview
     */
    void show_lprv(LList ls) {

        setPrintToString();
        while (ls != null) {
            show_llist(ls);
            ls.text = getPrintedString();
            for (int i = 0; i < ls.rl.length; i++) {
                Rule r = ls.rl[i];
                if (r == null) break;
                show_rule(r);
                r.text = getPrintedString();
            }
            ls = ls.fr;
        }
    }

    /**
     * Searches for angle splitting opportunities within the given node list and adds corresponding split rules.
     *
     * @param ls the node list to search for angle splits
     */
    public void search_ag_split(LList ls) {
        if (ls.nd == 1) return;

        Mnde m = ls.mf[0];
        AngTr t = m.tr;
        if (t == null) return;

        for (int j = 0; j < ls.nd; j++) {
            Mnde m1 = ls.md[j];
            AngTr t2 = m1.tr;
            if (t == null || t2 == null) {
            } else if (t.v == t2.v) {
                if (t.l1 == t2.l1) {
                    Rule r = add_rule_spag(t.v, t.l1, t2.l2, t.l2);
                    list_sub(ls, r);
                }
                if (t.l2 == t2.l2) {
                    Rule r = add_rule_spag(t.v, t.l1, t2.l1, t.l2);
                    list_sub(ls, r);
                }
            } else {
                if (t.l1 == t2.l1) {
                    Rule r = add_rule_exag(t.v, t.l1, t2.l2, t.l2);
                    list_sub(ls, r);
                }
                if (t.l2 == t2.l2) {
                    Rule r = add_rule_exag(t.v, t.l1, t2.l1, t.l2);
                    list_sub(ls, r);
                }
            }
        }
    }

    /**
     * Searches the TLine list for applicable transformations and adds corresponding rules to the given node list.
     *
     * @param ls the node list to process for transformation rules
     */
    public void search_t_list(LList ls) {

        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type != 0) {
                int t = inter_lls(tn.l1, tn.l2);
                if (t != 0) {
                    search_list_tn(ls, tn.l1, tn.l2, t);
                    search_list_tn(ls, tn.l2, tn.l1, t);
                }
            }
            tn = tn.nx;
        }
    }

    /**
     * Searches the node list for predicate rules based on angle properties and anti-point values, adding matching rules.
     *
     * @param ls the node list to search for predicate rules
     */
    public void search_p_list(LList ls) {
        for (int i = 0; i < ls.nd; i++) {
            Mnde m = ls.md[i];
            AngTr t = m.tr;
            int p1 = get_lpt1(t.l1, t.v);
            int p2 = get_lpt1(t.l2, t.v);
            if (p1 != 0 && get_anti_pt(t.l1, t.v, p1) != 0 || p2 != 0 && get_anti_pt(t.l2, t.v, p2) != 0) {
                Rule r = add_rule_p_ag(t);
                list_sub(ls, r);
            }
        }
    }

    /**
     * Searches the node list for transformation matches based on the given lines and intersection value,
     * and adds corresponding tag rules.
     *
     * @param ls the node list to search
     * @param l1 the first line for the search criteria
     * @param l2 the second line for the search criteria
     * @param v the intersection value used for matching
     */
    public void search_list_tn(LList ls, LLine l1, LLine l2, int v) {
        if (v != 0) {
            for (int i = 0; i < ls.nd; i++) {
                AngTr t = ls.md[i].tr;
                if (t.v == v) {
                    if (t.l1 == l1) {
//                        rule r = add_rule_spag(v, l1, t.l2, l2);
//                        list_sub(ls, r);
                        Rule r = add_rule_tag(l1, l2);
                        list_sub(ls, r);

                    } else if (t.l2 == l2) {
//                        rule r = add_rule_spag(v, l1, t.l1, l2);
//                        list_sub(ls, r);
                        Rule r = add_rule_tag(l2, l1);
                        list_sub(ls, r);
                    }
                }
            }
        }
    }

    /**
     * Creates and returns a tag rule for an angle based on the intersection of two lines.
     *
     * @param l1 the first line for the rule
     * @param l2 the second line for the rule
     * @return a tag rule with angle relations configured
     */
    public Rule add_rule_tag(LLine l1, LLine l2) {
        Rule r = new Rule(Rule.T_ANGLE);
        Mnde m = new Mnde();
        m.tr = add_tr(inter_lls(l1, l2), l1, l2);
        r.mr1[0] = m;

        Mnde m2 = new Mnde();
        m2.tr = null;
        m2.t = 90;
        r.mr = m2;
        r.no = 1;
        return r;
    }

    /**
     * Creates and returns a split-angle rule that partitions an angle into sub-angles using the given lines.
     *
     * @param v the angle value used for splitting
     * @param l1 the first line for the transformation
     * @param l2 the second line forming the angle vertex
     * @param l3 the third line completing the new angle configuration
     * @return a split-angle rule containing the constructed transformations
     */
    public Rule add_rule_spag(int v, LLine l1, LLine l2, LLine l3) {
        Rule r = new Rule(Rule.SPLIT_ANGLE);
        Mnde m = new Mnde();
        m.tr = add_tr(v, l1, l2);
        r.mr1[0] = m;

        Mnde m1 = new Mnde();
        m1.tr = add_tr(v, l2, l3);
        r.mr1[1] = m1;

        Mnde m2 = new Mnde();
        m2.tr = add_tr(v, l1, l3);
        r.mr = m2;

        r.no = 2;
        return r;
    }

    /**
     * Creates a P_ANGLE rule using the provided angle transformation.
     *
     * @param t the angle transformation containing the angle value and associated lines
     * @return a new Rule object of type P_ANGLE
     */
    public Rule add_rule_p_ag(AngTr t) {

        int v = t.v;
        LLine l1 = t.l1;
        LLine l2 = t.l2;

        Rule r = new Rule(Rule.P_ANGLE);
        Mnde m = new Mnde();
        m.tr = add_tr(v, l1, l2);
        m.tr.t1 = t.t1;
        m.tr.t2 = t.t2;

        r.mr1[0] = m;

        Mnde m1 = new Mnde();
        m1.tr = add_tr(v, l2, l1);

        r.mr1[1] = m1;

        Mnde m2 = new Mnde();
        m2.tr = null;
        m2.t = 180;
        r.mr = m2;
        r.no = 2;

        return r;

    }
    /**
     * Creates an EX_ANGLE rule based on three lines forming an angle.
     *
     * @param v the angle value
     * @param l1 the first line
     * @param l2 the second line
     * @param l3 the third line
     * @return a new Rule object of type EX_ANGLE, or null if a valid intersection is not found
     */
    public Rule add_rule_exag(int v, LLine l1, LLine l2, LLine l3) {
        Rule r = new Rule(Rule.EX_ANGLE);
        Mnde m = new Mnde();
        int v1 = inter_lls(l1, l2);
        if (v1 == 0) return null;

        m.tr = add_tr(v1, l1, l2);
        r.mr1[0] = m;

        int v2 = inter_lls(l2, l3);
        if (v2 == 0) return null;
        Mnde m1 = new Mnde();
        m1.tr = add_tr(v2, l2, l3);
        r.mr1[1] = m1;


        Mnde m2 = new Mnde();
        m2.tr = add_tr(v, l1, l3);
        r.mr = m2;
        r.no = 2;
        return r;
    }

    /**
     * Subtracts matching nodes from the provided node list using the specified rule.
     *
     * @param ls the original node list
     * @param r the rule to apply for subtracting matching nodes
     */
    public void list_sub(LList ls, Rule r) {
        if (r == null) return;

        LList ls1 = cp_nodes(ls);
        test_r.cp_rule(r);

        for (int i = 0; i < ls1.nd; i++) {
            Mnde m = ls1.md[i];
            for (int j = 0; j < test_r.no; j++) {
                Mnde m1 = r.mr1[j];

                if (m == null || m1 == null) {
                    int k = 0;
                } else if (m.tr == null && m1.tr == null) {
                    int t = m.t - m1.t;
                    if (t == 0)
                        ls1.md[i] = null;
                    else
                        ls1.md[i].t = t;
                    test_r.mr1[j] = null;
                } else if (m.tr != null && m1.tr != null && m.tr.l1 == m1.tr.l1 && m.tr.l2 == m1.tr.l2) {
                    ls1.md[i] = null;
                    test_r.mr1[j] = null;
                }
            }
        }

        int k = 0;
        for (int i = 0; i < test_r.no; i++) {
            if (test_r.mr1[i] != null) {
                ls1.mf[k++] = test_r.mr1[i];
            }
        }
        ls1.nf = k;

        int id = 0;
        for (int i = 0; i < ls1.nd; i++) {
            if (ls1.md[i] != null)
                ls1.md[id++] = ls1.md[i];
        }

        ls1.nd = id;
        ls1.fr = ls;
        ls1.add_rule(r);
        add_nodes(ls1);

    }

    /**
     * Retrieves a vector of node lists sorted by their point counts.
     *
     * @return a Vector containing sorted node lists
     */
    public Vector getPVector() {
        Vector v = new Vector();
        LList ns = all_ns.nx;
        while (ns != null) {
            int n = ns.get_npt();
            int i = 0;
            for (; i < v.size(); i++) {
                LList ls = (LList) v.get(i);
                if (ls.npt < n)
                    break;
            }
            v.add(i, ns);
            ns = ns.nx;
        }
        return v;
    }

    /**
     * Displays a single node list in a formatted manner.
     *
     * @param ls the node list to be displayed
     */
    public void show_llist(LList ls) {
        for (int i = 0; i < ls.nd; i++) {
            Mnde m = ls.md[i];
            if (i != 0)
                gprint("+");
            show_mnde(m);
        }
        if (ls.nd == 0)
            gprint("0");

        gprint(" = ");

        for (int i = 0; i < ls.nf; i++) {
            Mnde m = ls.mf[i];
            if (i != 0)
                gprint("+");
            show_mnde(m);
        }
        if (ls.nf == 0)
            gprint("0");

//        gprint("\t\t\tbecause  ");
//        show_rule(ls.rl[0]);
//        gprint("\n");

    }

    /**
     * Displays all node lists contained in the provided vector.
     *
     * @param v a vector containing node lists to be displayed
     */
    public void show_llists(Vector v) {

        for (int i = 0; i < v.size(); i++) {
            LList ls = (LList) v.get(i);
            show_llist(ls);
        }
    }

    /**
     * Displays the Mnde object either by showing its transformation or its angle value.
     *
     * @param m the Mnde object to be displayed
     */
    public void show_mnde(Mnde m) {

        if (m.tr == null)
            gprint(" " + m.t);
        else
            show_tr(m.tr);
    }

    /**
     * Parses a backup list of node lists, updating rules and transformations as needed.
     *
     * @param ls the starting node list for the backup parsing process
     */
    void parse_bk_list(LList ls) {
        Vector v = new Vector();
        while (ls != null) {
            v.add(0, ls);
            ls = ls.fr;
        }


        LList ls1 = null;
        for (int i = 0; i < v.size(); i++) {
            ls = (LList) v.get(i);

            Rule r = ls.rl[0];

            if (r != null) {
                switch (r.type) {
                    case Rule.SPLIT_ANGLE:
                        break;
                    case Rule.EX_ANGLE: {
                        AngTr t = r.mr.tr;
                        AngTr t1 = r.mr1[0].tr;
                        AngTr t2 = r.mr1[1].tr;
                        AngTr t3 = find_tr_in_ls(t1, ls1);
                        AngTr t4 = find_tr_in_ls(t2, ls1);
                        if (t3 != null) {
                            t1.t1 = t3.t1;
                            t1.t2 = t3.t2;
                        } else {
                            t1.t1 = t.v;
                            t1.t2 = t2.v;
                        }
                        if (t4 != null) {
                            t2.t1 = t4.t1;
                            t2.t2 = t4.t2;
                        } else {
                            t2.t1 = t1.v;
                            t2.t2 = t.v;
                        }
                        AngTr tf = ls1.mf[0].tr;
                        t.t1 = tf.t1;
                        t.t2 = tf.t2;
                    }
                    break;
                    case Rule.P_ANGLE: {
                        AngTr t1 = r.mr1[0].tr;
                        AngTr t2 = r.mr1[1].tr;
                        AngTr t3 = find_tr_in_ls(t1, ls1);
                        if (t3 != null) {
                            t1.t1 = t3.t1;
                            t1.t2 = t3.t2;
                        }
                        t2.t1 = t1.t2;
                        t2.t2 = get_anti_pt(t2.l2, t2.v, t1.t1);
                    }
                    break;
                    case Rule.T_ANGLE: {

                    }
                    break;
                }
            }

            ls1 = ls;
        }
    }

    /**
     * Searches the node list for an angle transformation matching the specified transformation.
     *
     * @param t the target angle transformation to find
     * @param ls the node list to search for a matching transformation
     * @return the matching AngTr object if found; otherwise, null
     */
    public AngTr find_tr_in_ls(AngTr t, LList ls) {
        for (int i = 0; i < ls.nd; i++) {
            AngTr t1 = ls.md[i].tr;
            if (t1.l1 == t.l1 && t1.l2 == t.l2) return t1;
        }
        return null;
    }

    /**
     * Displays the provided rule in a formatted manner.
     *
     * @param r the rule to be displayed
     */
    void show_rule(Rule r) {
        if (r == null) return;

        if (r.type == Rule.SPLIT_ANGLE) {
            AngTr t1 = r.mr1[0].tr;
            AngTr t2 = r.mr1[1].tr;

            show_tr(t1);
            gprint("+");
            show_tr(t2);
            gprint("=");
            show_tr(r.mr.tr);

        } else if (r.type == Rule.EX_ANGLE) {
            AngTr t1 = r.mr1[0].tr;
            AngTr t2 = r.mr1[1].tr;

            show_tr(t1);
            gprint("+");
            show_tr(t2);
            gprint("=");
            show_tr(r.mr.tr);
        } else if (r.type == Rule.P_ANGLE) {
            AngTr t1 = r.mr1[0].tr;
            AngTr t2 = r.mr1[1].tr;

            show_tr(t1);
            gprint("+");
            show_tr(t2);
            gprint("=");
            show_mnde(r.mr);
        } else if (r.type == Rule.T_ANGLE) {
            show_mnde(r.mr1[0]);
            gprint("=");
            show_mnde(r.mr);
        } else if (r.type == Rule.EQ_ANGLE) {
            show_mnde(r.mr1[0]);
            gprint("=");
            show_mnde(r.mr);
        }
    }


}
