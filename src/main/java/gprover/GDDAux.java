package gprover;

/**
 * GDDAux extends GDD to provide auxiliary processing methods for geometric constructions.
 */
public class GDDAux extends GDD {

/* main entry */
    static long time;
    /**
     * Initiates auxiliary processing steps.
     *
     * @return true if the auxiliary processing is successfully initiated.
     */
    boolean add_aux() {
        gno = cons_no;
        ax_backward();
        aux_rules();

        ax_orth();
        ax_md();
        ax_pn();
        ax_tn();
        ax_as();
        ax_cr();
        ax_tn_1();
        ax_cg();
        return true;
    }

    /**
     * Adds an auxiliary point if it does not already exist.
     *
     * @param ax the auxiliary point to add.
     */
    public void add_aux(AuxPt ax) {
        if (aux_exists(ax))
            return;
        vauxpts.add(ax);
    }

    /**
     * Checks if the given auxiliary point already exists in the list.
     *
     * @param ax the auxiliary point to check.
     * @return true if the auxiliary point exists, false otherwise.
     */
    private boolean aux_exists(AuxPt ax) {
        int n = ax.getPtsNo();
        if (n > 1) return false;
        ProPoint pt = ax.getPtsbyNo(0);
        if (fd_pt(pt.getdx(), pt.getdy()) != null) return true;

        for (int i = 0; i < vauxpts.size(); i++) {
            AuxPt ax1 = (AuxPt) vauxpts.get(i);
            if (isaux_contpt(ax1, pt))
                return true;
        }
        return false;
    }

    /**
     * Determines if the auxiliary point contains the specified point.
     *
     * @param ax the auxiliary point.
     * @param pt the point to check.
     * @return true if the point is contained within the auxiliary point, false otherwise.
     */
    private boolean isaux_contpt(AuxPt ax, ProPoint pt) {
        int n = ax.getPtsNo();
        for (int i = 0; i < n; i++) {
            ProPoint p = (ProPoint) ax.getPtsbyNo(i);
            if (isSamePt(pt, p))
                return true;
        }
        return false;
    }

    /**
     * Compares two points to determine if they are the same.
     *
     * @param p1 the first point.
     * @param p2 the second point.
     * @return true if the points are considered the same, false otherwise.
     */
    private boolean isSamePt(ProPoint p1, ProPoint p2) {
        if (p1.type != p2.type) return false;
        return Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2) < ZERO * ZERO;

    }

    /**
     * Checks if the elapsed time since start exceeds the defined limit.
     *
     * @return true if the time is over the limit, false otherwise.
     */
    public boolean time_over() {
        long t = System.currentTimeMillis() - time;
        if (t > 200000) return true;
        return false;
    }

    /**
     * Records the current system time as the start time for timing.
     */
    public void time_start() {
        time = System.currentTimeMillis();
    }

    /**
     * Creates a new auxiliary point of the specified type.
     *
     * @param aux the auxiliary identifier.
     * @param type the type of the point.
     * @return the created auxiliary point.
     */
    ProPoint aux_pt(int aux, int type) {
        ProPoint p = new ProPoint(type);
        p.aux = aux;
        return p;
    }

    /**
     * Adds the given point as an auxiliary point after generating its descriptive string.
     *
     * @param t the auxiliary identifier.
     * @param pt the point to be added.
     */
    private void add_as_aux(int t, ProPoint pt) {
        if (pt == null)
            return;
        AuxPt ax = new AuxPt(t);
        auxpt_string(pt);
        ax.addAPt(pt);

        add_aux(ax);
        return;
    }

    /**
     * Creates an auxiliary point based on a tangent line configuration.
     *
     * @param aux the auxiliary identifier.
     * @param p1 the first defining point.
     * @param p2 the second defining point.
     * @param p3 the third defining point.
     * @return the created auxiliary point for the tangent line.
     */
    ProPoint aux_tline(int aux, int p1, int p2, int p3) {
        ProPoint pt = aux_pt(aux, C_O_T);
        pt.ps[0] = 0;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        pt.ps[3] = p3;
        cal_ax_tn(pt, p1, p2, p3);
        return (pt);
    }

    /**
     * Creates an auxiliary tangent line point and adds it to the auxiliary list.
     *
     * @param aux the auxiliary identifier.
     * @param p1 the first defining point.
     * @param p2 the second defining point.
     * @param p3 the third defining point.
     * @return the created auxiliary tangent line point.
     */
    ProPoint auxpt_tline(int aux, int p1, int p2, int p3) {
        ProPoint pt = aux_tline(aux, p1, p2, p3);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Creates an auxiliary midpoint between two points.
     *
     * @param aux the auxiliary identifier.
     * @param p1 the first point.
     * @param p2 the second point.
     * @return the created auxiliary midpoint.
     */
    ProPoint aux_mid(int aux, int p1, int p2) {
        ProPoint pt = aux_pt(aux, C_MIDPOINT);
        pt.ps[0] = cons_no;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        cal_ax_md(pt, p1, p2);
        return (pt);
    }

    /**
     * Creates an auxiliary midpoint and adds it to the auxiliary list.
     *
     * @param aux the auxiliary identifier.
     * @param p1 the first point.
     * @param p2 the second point.
     * @return the created auxiliary midpoint after adding it.
     */
    ProPoint auxpt_mid(int aux, int p1, int p2) {
        ProPoint pt = aux_mid(aux, p1, p2);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Creates an intersection point for two lines using the provided points.
     * The point is computed and validated by cal_ax_ill.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param p4 fourth point identifier.
     * @return the constructed intersection point if valid; otherwise, null.
     */
    ProPoint aux_ill(int aux, int p1, int p2, int p3, int p4) {
        ProPoint pt = aux_pt(aux, C_I_LL);
        pt.ps[0] = cons_no;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        pt.ps[3] = p3;
        pt.ps[4] = p4;
        if (cal_ax_ill(pt, p1, p2, p3, p4))
            return (pt);
        return null;
    }

    /**
     * Creates and adds an intersection point for two lines.
     * Computes the point using aux_ill and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param p4 fourth point identifier.
     * @return the added intersection point if valid; otherwise, null.
     */
    ProPoint auxpt_ill(int aux, int p1, int p2, int p3, int p4) {
        ProPoint pt = aux_ill(aux, p1, p2, p3, p4);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Creates an intersection point for a line configuration using five points.
     * The point is computed and validated by cal_ax_ilp.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param p4 fourth point identifier.
     * @param p5 fifth point identifier.
     * @return the constructed intersection point if valid; otherwise, null.
     */
    ProPoint aux_ilp(int aux, int p1, int p2, int p3, int p4, int p5) {
        ProPoint pt = aux_pt(aux, C_I_LP);
        pt.ps[0] = cons_no;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        pt.ps[3] = p3;
        pt.ps[4] = p4;
        pt.ps[5] = p5;
        if (cal_ax_ilp(pt, p1, p2, p3, p4, p5))
            return (pt);
        return null;
    }

    /**
     * Creates and adds an intersection point for a line configuration.
     * Computes the point using aux_ilp and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param p4 fourth point identifier.
     * @param p5 fifth point identifier.
     * @return the added intersection point if valid; otherwise, null.
     */
    ProPoint auxpt_ilp(int aux, int p1, int p2, int p3, int p4, int p5) {
        ProPoint pt = aux_ilp(aux, p1, p2, p3, p4, p5);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Calculates the foot of the perpendicular from a point onto a line.
     * The foot is computed using points p1, p2, and p3 via cal_ax_foot.
     *
     * @param aux auxiliary identifier.
     * @param p1 the point from which the perpendicular is drawn.
     * @param p2 the first point defining the line.
     * @param p3 the second point defining the line.
     * @return the point representing the foot of the perpendicular.
     */
    ProPoint aux_foot(int aux, int p1, int p2, int p3) {
        ProPoint pt = aux_pt(aux, C_FOOT);
        pt.ps[0] = cons_no;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        pt.ps[3] = p3;
        cal_ax_foot(pt, p1, p2, p3);
        return (pt);
    }

    /**
     * Creates and adds the foot of the perpendicular from a point onto a line.
     * Computes the foot using aux_foot and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param p1 the point from which the perpendicular is drawn.
     * @param p2 the first point defining the line.
     * @param p3 the second point defining the line.
     * @return the added perpendicular foot point.
     */
    ProPoint auxpt_foot(int aux, int p1, int p2, int p3) {
        ProPoint pt = aux_foot(aux, p1, p2, p3);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Creates an intersection point between a line and a circle.
     * Uses points p1 and p2 along with the circle's center and first point to compute the intersection.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param cr the circle used for the intersection.
     * @return the intersection point if successfully computed; otherwise, null.
     */
    ProPoint aux_ilc(int aux, int p1, int p2, ACir cr) {
        ProPoint p = aux_pt(aux, C_I_LC);
        p.ps[0] = cons_no;
        p.ps[1] = p1;
        p.ps[2] = p2;
        p.ps[3] = cr.o;
        p.ps[4] = cr.pt[0];
        if (cal_ax_ilc(p, p1, p2, cr.o, cr.pt[0]))
            return p;
        else
            return null;
    }

    /**
     * Creates and adds an intersection point between a line and a circle.
     * Computes the point using aux_ilc and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param cr the circle used for the intersection.
     * @return the added intersection point if valid; otherwise, null.
     */
    ProPoint auxpt_ilc(int aux, int p1, int p2, ACir cr) {
        ProPoint pt = aux_ilc(aux, p1, p2, cr);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Calculates the intersection point between a line and a circle using a translation approach.
     * The point is computed using p1, p2, and p3 along with the circle’s properties via cal_ax_ipc.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param cr the circle used for the intersection.
     * @return the calculated intersection point if successful; otherwise, null.
     */
    ProPoint aux_ipc(int aux, int p1, int p2, int p3, ACir cr) {
        ProPoint pt = aux_pt(aux, C_I_PC);
        pt.ps[0] = cons_no;
        pt.ps[1] = p1;
        pt.ps[2] = p2;
        pt.ps[3] = p3;
        pt.ps[4] = cr.o;
        pt.ps[5] = p1;
        if (cal_ax_ipc(pt, p1, p2, p3, cr.o, cr.pt[0]))
            return pt;
        return (null);
    }

    /**
     * Creates and adds an intersection point between a line and a circle.
     * Computes the point using aux_ipc and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param p1 first point identifier.
     * @param p2 second point identifier.
     * @param p3 third point identifier.
     * @param cr the circle used for the intersection.
     * @return the added intersection point if valid; otherwise, null.
     */
    ProPoint auxpt_ipc(int aux, int p1, int p2, int p3, ACir cr) {
        ProPoint pt = aux_ipc(aux, p1, p2, p3, cr);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Computes the circumcenter point of a circle.
     * The circumcenter is calculated from the circle's defining points via cal_ax_co.
     *
     * @param aux auxiliary identifier.
     * @param cr the circle for which the circumcenter is computed.
     * @return the computed circumcenter point.
     */
    ProPoint aux_co(int aux, ACir cr) {
        ProPoint pt = aux_pt(aux, C_CIRCUM); //62
        pt.ps[0] = 0;
        pt.ps[1] = cr.pt[0];
        pt.ps[2] = cr.pt[1];
        pt.ps[3] = cr.pt[2];
        cal_ax_co(pt, cr.pt[0], cr.pt[1], cr.pt[2]);
        return (pt);
    }

    /**
     * Creates and adds the circumcenter point of a circle.
     * Computes the point using aux_co and adds it if valid.
     *
     * @param aux auxiliary identifier.
     * @param cr the circle for which the circumcenter is computed.
     * @return the added circumcenter point.
     */
    ProPoint auxpt_co(int aux, ACir cr) {
        ProPoint pt = aux_co(aux, cr);
        if (pt != null)
            add_as_aux(aux, pt);
        return pt;
    }

    /**
     * Initiates the processing of auxiliary rules.
     * This method serves as a placeholder for additional auxiliary rule implementations.
     */
    void aux_rules() {

    }

    /**
     * Iterates through all midpoint structures and processes them by checking for common line conditions
     * and applying appropriate auxiliary transformations.
     */
    void ax_md() {
        MidPt md = all_md.nx;
        while (md != null) {
            LLine ln = fd_ln(md.a, md.b);
            ax_mdpn(md);
            MidPt md1 = md.nx;
            while (md1 != null) {
                if (md1 != md && on_ln(md1.a, ln) && on_ln(md1.b, ln)) {
                    ax_mml(md, md1, ln);
                } else if (md1 != md) {
                    if (md.m != md1.m && !(md.a == md1.a || md.a == md1.b || md.b == md1.a || md.b == md1.b))
                        ax_mm(md, md1);
                }
                md1 = md1.nx;
            }
            md = md.nx;
        }
    }

    /**
     * Processes point network intersections for the given midpoint.
     * It finds relevant lines forming the point network structure and calls further processing methods.
     *
     * @param md the midpoint structure to process
     */
    void ax_mdpn(MidPt md) {
        PLine pn = all_pn.nx;
        while (pn != null) {
            if (pn.type == 0 || pn.no <= 1) {
                pn = pn.nx;
                continue;
            }
            LLine ln1 = fd_ln_pn1(pn, md.m);
            if (ln1 == null) {
                pn = pn.nx;
                continue;
            }
            LLine ln2 = fd_ln_pn1(pn, md.a);
            if (ln2 == null || ln2 == ln1) {
                pn = pn.nx;
                continue;
            }
            LLine ln3 = fd_ln_pn1(pn, md.b);
            if (ln3 == null || ln3 == ln1 || ln3 == ln2) {
                pn = pn.nx;
                continue;
            }
            ax_p3m(pn, md.m, md.a, md.b, ln1, ln2, ln3);
            pn = pn.nx;
        }
    }

    /**
     * Examines global lines and determines if auxiliary intersections should be created based on the
     * provided network lines.
     *
     * @param pn  the point network segment being examined
     * @param p1  the first reference point
     * @param p2  the second reference point
     * @param p3  the third reference point
     * @param ln1 the first line corresponding to p1
     * @param ln2 the second line corresponding to p2
     * @param ln3 the third line corresponding to p3
     */
    void ax_p3m(PLine pn, int p1, int p2, int p3, LLine ln1, LLine ln2, LLine ln3) {
        int m1, m2, m3;
        LLine ln;
        {
            ln = all_ln.nx;
            while (ln != null) {
                if (ln.type != 0 && ln != ln1 && ln != ln2 && ln != ln3 && (m1 = inter_ll(ln, ln1)) != 0
                        && m1 != p1 && (m2 = inter_ll(ln, ln2)) != 0 && m2 != p2 &&
                        (m3 = inter_ll(ln, ln3)) != 0 && m3 != p3 && !xmid(m1, m2, m3)) {

                    if (inter_ll(ln1, fd_ln(p2, m3)) == 0 && inter_ll(ln1, fd_ln(p3, m2)) == 0) {
                        auxpt_ill(8, p1, m1, p2, m3);
                        auxpt_ill(8, p1, m1, p3, m2);
                    }
                }
                ln = ln.nx;
            }
        }
    }

    /**
     * Processes overlapping midpoint configurations between two midpoint structures.
     * It identifies a common point from the first structure and iterates over global lines to trigger
     * auxiliary midpoint creation when needed.
     *
     * @param md  the first midpoint structure
     * @param md1 the second midpoint structure for comparison
     * @param l1  the line determined by the current configuration
     */
    void ax_mml(MidPt md, MidPt md1, LLine l1) {
        int p1, p2;

        if (md.a == md1.a || md.a == md1.b) {
            p1 = md.a;
        } else if (md.b == md1.a || md.b == md1.b) {
            p1 = md.b;
        } else
            return;
        {
            LLine ln = all_ln.nx;
            while (ln != null) {
                if (ln.type != 0 && ln != l1 && on_ln(p1, ln)) {
                    p2 = get_lpt1(ln, p1);
                    if (fd_pt_md(p1, p2) == 0) {
//                        if (aux_mid(2, p1, p2) != 0) return;
                        auxpt_mid(2, p1, p2);//???????
                    }
                }
                ln = ln.nx;
            }
        }
    }

    /**
     * Handles mismatched midpoint configurations by evaluating parallel conditions and generating
     * auxiliary intersections when specific criteria are met.
     *
     * @param md  the first midpoint structure
     * @param md1 the second midpoint structure
     */
    void ax_mm(MidPt md, MidPt md1) {
        int m1, m2, p1, p2, p3, p4;
        p1 = md.a;
        p2 = md.b;
        m1 = md.m;
        m2 = md1.m;


        if (xpara(md.a, md1.b, md.b, md1.a)) {
            p3 = md1.b;
            p4 = md1.a;
        } else {
            p3 = md1.a;
            p4 = md1.b;
        }

        if (!xpara(p1, p3, m1, m2) && xpara(p1, p3, p2, p4)) {

            LLine l1 = fd_ln(md.a, p1);
            LLine l2 = fd_ln(md.b, p2);

            if (inter_ll(l2, fd_ln(p3, m1)) == 0)
                auxpt_ill(18, p2, p4, p3, m1);
            if (inter_ll(l2, fd_ln(p1, m2)) == 0)
                auxpt_ill(18, p2, p4, p1, m2);
            if (inter_ll(l1, fd_ln(p4, m1)) == 0)
                auxpt_ill(18, p1, p3, p4, m1);
            if (inter_ll(l1, fd_ln(p2, m2)) == 0)
                auxpt_ill(18, p1, p3, p2, m2);
        }else if(md.dep < 2 && md1.dep <2 )
        {
            if(md.a != md1.a && md.a != md1.b && md.b != md1.a && md.b != md1.b)
            {
                this.auxpt_mid(0,md.a,md1.a);
                this.auxpt_mid(0,md.a,md1.b);
                this.auxpt_mid(0,md.b,md1.a);
                this.auxpt_mid(0,md.b,md1.b);
            }
        }
    }

    /**
     * Iterates through all tangent lines and applies orthogonal transformations
     * by processing each non-zero type tangent line.
     */
    void ax_orth() {

        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type == 0) {
                tn = tn.nx;
                continue;
            }
            ax_orth1(tn, tn.l1, tn.l2);
            tn = tn.nx;
        }
    }

    /**
     * Processes orthogonal configurations for a given tangent line and its associated lines.
     * If the lines meet the required intersection conditions, auxiliary intersection points are generated.
     *
     * @param tn1 the primary tangent line to process
     * @param ln1 the first line associated with the tangent
     * @param ln2 the second line associated with the tangent
     */
    void ax_orth1(TLine tn1, LLine ln1, LLine ln2) {
        int a, b, c, h;

        if (inter_ll(ln1, ln2) != 0) return;
        TLine tn = all_tn.nx;

        while (tn != null) {
            if (tn.type == 0 || tn == tn1) {
                tn = tn.nx;
                continue;
            }

            LLine ln3 = tn.l1;
            LLine ln4 = tn.l2;
            if (inter_ll(ln3, ln4) != 0) {
                tn = tn.nx;
                continue;
            }

            if ((h = inter_ll(ln1, ln3)) != 0 &&
                    (b = inter_ll(ln1, ln4)) != 0 && b != h &&
                    (c = inter_ll(ln2, ln3)) != 0 && c != b && c != h &&
                    (a = inter_ll(ln2, ln4)) != 0 && a != b && a != c && a != h) {
                auxpt_ill(10, c, h, a, b);
                auxpt_ill(10, b, h, a, c);
            }
            ln4 = null;
            tn = tn.nx;
        }
    }

    /**
     * Processes all TLine elements in the list and applies geometric constructions
     * including circle intersections, angle adjustments, midpoint validations,
     * and constructing auxiliary points based on line intersections.
     */
    void ax_tn() {
        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type == 0) {
                tn = tn.nx;
                continue;
            }

            LLine ln1 = tn.l1;
            LLine ln2 = tn.l2;
            ax_tn_cr(tn);
            ax_tn_as(tn);
            ax_tn_md(tn);
            ax_tn_21(tn, ln1, ln2);
            ax_tn_21(tn, ln2, ln1);
            tn = tn.nx;
        }
    }

    /**
     * Processes circle intersections for the given TLine.
     * Evaluates intersections between the circles and the two lines of the TLine,
     * and constructs auxiliary points if the intersection conditions are met.
     *
     * @param tn the TLine whose circle intersections are to be processed
     */
    void ax_tn_cr(TLine tn) {
        int p2, p3, m;
        LLine ln;

        LLine ln1 = tn.l1;
        LLine ln2 = tn.l2;

        ACir cr = all_cir.nx;
        m = inter_ll(ln1, ln2);
        while (cr != null) {
            if (cr.type == 0 || cr.o == 0) {
                cr = cr.nx;
                continue;
            }
            if (m != 0) {
                if (!on_cir(m, cr)) {
                    cr = cr.nx;
                    continue;
                }
                p2 = inter_lc1(ln1, cr, m);
                if (p2 == 0) {
                    p2 = inter_lc1(ln2, cr, m);
                    ln = ln1;
                    ln1 = ln2;
                    ln2 = ln;
                }
                if (p2 == 0) {
                    cr = cr.nx;
                    continue;
                }
                p3 = inter_ll(ln2, fd_ln(p2, cr.o));
                if (p3 == 0) {
                    auxpt_ilc(30, m, get_lpt1(ln2, m), cr); //2007.1.10
                    cr = cr.nx;
                    continue;
                }
                if (!xmid(cr.o, p2, p3)) {
                    auxpt_mid(14, p2, m);
                }
            } else {
                if (on_ln(cr.o, ln2)) {
                    ln = ln1;
                    ln1 = ln2;
                    ln2 = ln;
                }
                if (on_ln(cr.o, ln1) && (p2 = inter_lc(ln2, cr)) != 0 &&
                        (p3 = inter_lc1(ln2, cr, p2)) != 0 && p2 != p3 && fd_pt_md(p2, p3) == 0) {
                    auxpt_ill(21, p2, p3, cr.o, get_lpt1(ln1, cr.o));   //y1
                }
            }
            cr = cr.nx;
        }
    }

    /**
     * Processes angle-related transformations for the given TLine.
     * Iterates over angle configuration objects and, based on the positional
     * relationships of the TLine's segments, constructs auxiliary intersection points.
     *
     * @param tn the TLine for which angle transformations are evaluated
     */
    void ax_tn_as(TLine tn) {
        int p1, p2, p3;
        LLine ln, n1, n2, l1, l2, l3, l4;
        ProPoint pt;


        LLine ln1 = tn.l1;
        LLine ln2 = tn.l2;
        int m = inter_ll(ln1, ln2);
        Angles as1 = all_as.nx;
        while (as1 != null) {
            if (as1.type == 0)// goto m1;
            {
                as1 = as1.nx;
                continue;
            }
            l1 = as1.l1;
            l2 = as1.l2;
            l3 = as1.l3;
            l4 = as1.l4;
            if (ln1 == l2 && ln1 == l3) {
                n1 = l1;
                n2 = l4;
            } else if (ln2 == l2 && ln2 == l3) {
                ln = ln1;
                ln1 = ln2;
                ln2 = ln;
                n1 = l1;
                n2 = l4;
            } else {
                as1 = as1.nx;
                continue;
            }
            p1 = inter_ll(n1, n2);
            if (p1 == 0 || p1 == m || !on_ln(p1, ln1)) {
                as1 = as1.nx;
                continue;
            }
            p2 = inter_ll(ln2, n1);
            p3 = inter_ll(ln2, n2);
            if (m != 0 && p2 != 0 && p3 != 0) return;

            if (m == 0 && p2 != 0 && p3 != 0) {
                pt = auxpt_ill(3, p2, p3, p1, get_lpt1(ln1, p1));
            } else if (m != 0 && p2 != 0 && p3 == 0) {
                pt = auxpt_ill(1, m, p2, p1, get_lpt1(n2, p1));
            } else if (m != 0 && p2 == 0 && p3 != 0) {
                pt = auxpt_ill(1, m, p3, p1, get_lpt1(n1, p1));
            } else {
                as1 = as1.nx;
                continue;
            }
            as1 = as1.nx;
        }
    }

    /**
     * Processes midpoint constructions associated with the given TLine.
     * Searches through midpoint objects and, if the conditions are met,
     * constructs an auxiliary foot point.
     *
     * @param tn the TLine used for evaluating midpoint intersections
     */
    void ax_tn_md(TLine tn) {
        int p1, p2;

        LLine ln1 = tn.l1;
        LLine ln2 = tn.l2;

        int m = inter_ll(ln1, ln2);
        if (m == 0) return;
        MidPt md = all_md.nx;

        while (md != null) {
            if (on_ln(md.m, ln1)) {
                LLine ln = ln1;
                ln1 = ln2;
                ln2 = ln;
            }
            if (!on_ln(md.m, ln2)) {
                md = md.nx;
                continue;
            }
            if (on_ln(md.a, ln1)) {
                p1 = md.a;
                p2 = md.b;
            } else if (on_ln(md.b, ln1)) {
                p1 = md.b;
                p2 = md.a;
            } else {
                md = md.nx;
                continue;
            }
            if (m == md.m || m == p1) {
                md = md.nx;
                continue;
            }
            if (inter_ll(ln2, fd_tline(p2, m, md.m)) == 0) {
                auxpt_foot(22, p2, m, md.m);
            }
            md = md.nx;
        }
    }

    /**
     * Processes all TLine elements to construct perpendicular line intersections.
     * Delegates to ax_tn_ln for each TLine's pair of lines.
     */
    void ax_tn_1() {
        TLine tn = all_tn.nx;
        while (tn != null) {
            if (tn.type == 0) {
                tn = tn.nx;
                continue;
            }
            ax_tn_ln(tn.l1, tn.l2);
            tn = tn.nx;
        }
    }

    /**
     * Evaluates intersections and geometric relationships between two lines.
     * Constructs auxiliary foot points if the specified conditions and
     * intersection criteria are satisfied.
     *
     * @param ln1 the first line used in the intersection check
     * @param ln2 the second line used in the intersection check
     */
    void ax_tn_ln(LLine ln1, LLine ln2) {
        int p1, p2, p3;

        int m = inter_ll(ln1, ln2);
        if (m == 0) return;
        LLine ln = all_ln.nx;

        while (ln != null) {
            if (ln.type == 0 || ln == ln1 || ln == ln2) {
                ln = ln.nx;
                continue;
            }
            p1 = inter_ll(ln, ln1);
            if (p1 == 0 || ln_para(ln, ln2)) {
                p1 = inter_ll(ln, ln2);
                LLine l1 = ln1;
                ln1 = ln2;
                ln2 = l1;
            }
            if (p1 == 0 || p1 == m)// goto m1;
            {
                ln = ln.nx;
                continue;
            }
            if (ln_para(ln, ln2))// goto m1;
            {
                ln = ln.nx;
                continue;
            }
            LLine l2;
            for (int i = 0; i <= ln2.no; i++) {
                p2 = ln2.pt[i];
                if (p2 != m && !on_ln(p2, ln) && (l2 = fd_ln(p1, p2)) != null && !ln_perp(ln, l2) &&
                        inter_ll(ln, fd_tline(p2, ln.pt[0], ln.pt[1])) == 0) {
                    p3 = get_lpt1(ln, p1);
                    auxpt_foot(12, p2, p1, p3);
                }
            }
            ln = ln.nx;
        }
    }

    /**
     * Processes candidate intersections between lines within a TLine context.
     * Iterates through candidate line groups to construct auxiliary points based
     * on midpoint validations and line intersections.
     *
     * @param tn the TLine context for the operation
     * @param ln1 the first line segment involved in the intersection check
     * @param ln2 the second line segment involved in the intersection check
     */
    void ax_tn_21(TLine tn, LLine ln1, LLine ln2) {
        int p2, p3, p4, p5;
        int p1 = inter_ll(ln1, ln2);
        if (p1 == 0) return;

        PLine pn1 = fd_pnl(ln1);
        if (pn1 == null) return;

        for (int i = 0; i <= pn1.no; i++) {
            LLine l1 = pn1.ln[i];
            if (l1 == ln1)
                continue;
            p2 = inter_ll(l1, ln2);
            if (p2 == 0)
                continue;

            LLine l2 = all_ln.nx;

            while (l2 != null) {
                if (l2.type != 0 && l2 != ln1 && l2 != l1 && l2 != ln2 &&
                        (p3 = inter_ll(ln1, l2)) != 0 && p3 != p1 &&
                        (p4 = inter_ll(l1, l2)) != 0 && p4 != p2) {
                    if ((p5 = fd_pt_md(p3, p4)) != 0 && fd_pt_md(p1, p2) == 0 &&
                            inter_ll(ln2, fd_tline(p5, p1, p2)) == 0) {

                        auxpt_foot(23, p5, p1, p2);

                    }
                    if (!ln_perp(l2, ln1) && (inter_ll(l1, fd_tline(p3, p3, p1))) == 0 &&
                            (inter_ll(ln1, fd_tline(p4, p4, p2))) == 0 &&
                            (inter_ll(l2, ln2)) == 0 &&
                            (inter_ll(l1, fd_pline(p1, p3, p4))) == 0) {

                        auxpt_ilp(24, p2, p4, p1, p3, p4);

                    }
                }
                l2 = l2.nx;
            }
        }
    }

    /**
     * Processes all angle transformation objects.
     * Iterates through the list of angles and delegates processing
     * to specific configuration methods based on the angle type.
     */
    void ax_as() {
        Angles as1;
        LLine l1, l2, l3, l4;
        as1 = all_as.nx;
        while (as1 != null) {
            if (as1.type == 0) {
                as1 = as1.nx;
                continue;
            }
            l1 = as1.l1;
            l2 = as1.l2;
            l3 = as1.l3;
            l4 = as1.l4;
            if (l2 == l3)
                ax_as_1(as1, l1, l2, l4);
            else
                ax_as_2(as1, l1, l2, l3, l4);

            as1 = as1.nx;
        }
    }

    /**
     * Evaluates the angle transformation for a specific configuration.
     * Constructs auxiliary points by verifying intersection points and circle
     * conditions for the configuration defined by the provided lines.
     *
     * @param as  the angle transformation object to process
     * @param l1  the first line defining the angle
     * @param l2  the second line defining the angle
     * @param l3  the auxiliary line used for comparative intersection analysis
     */
    void ax_as_1(Angles as, LLine l1, LLine l2, LLine l3) {
        int p1, p2, p3, p4;

        int o = inter_ll(l1, l3);
        if (o == 0) return;
        if (!on_ln(o, l2)) return;


        ACir cr = all_cir.nx;
        while (cr != null) {
            if (cr.type == 0) {
                cr = cr.nx;
                continue;
            }
            if (!on_cir(o, cr) || ((p1 = inter_lc1(l1, cr, o)) == 0) || ((p3 = inter_lc1(l3, cr, o)) == 0)) //goto st1;
            {
                cr = cr.nx;
                continue;
            }
            p2 = inter_lc1(l2, cr, o);
            if (p2 == 0 && cr.o != 0) {//&& !ln_perp(l2, fd_ln(p1, p3)) && 0 != 0)  /* d123 */ {??????????
                auxpt_ilc(25, o, get_lpt1(l2, o), cr);
            }
            cr = cr.nx;
        }
        p4 = get_lpt1(l2, o);
        int i = p4;
        for (i = 0; i <= l1.no; i++)
            for (int j = 0; j <= l3.no; j++) {
                p1 = l1.pt[i];
                p3 = l3.pt[j];
                if (p1 != o && p3 != o && inter_ll(l2, fd_ln(p1, p3)) == 0) {
                    ax_at(as, o, p1, p3, l2);
                }
            }

        for (i = 0; i <= l2.no; i++) {
            int p = l2.pt[i];
            if (p != o) {
                LLine n1 = fd_lpp2(p, l1);
                LLine n2 = fd_lpp2(p, l3);
                if (n1 != null && n2 == null) {
                    int t3 = inter_ll(n1, l3);
                    if (t3 != 0)
                        auxpt_ilp(31, o, get_lpt1(l1, o), p, o, t3);
                } else if (n1 == null && n2 != null) {
                    if (n1 != null && n2 == null) {
                        int t4 = inter_ll(n2, l1);
                        if (t4 != 0)
                            auxpt_ilp(31, o, get_lpt1(l3, o), p, o, t4);
                    }
                }
            }
        }

    }

    /**
     * Processes angle transformations by iterating through candidate points on the given line.
     * If the line between p1 and p3 intersects l2, no transformation is applied.
     * Otherwise, for each point p2 on l2 (excluding o and midpoints between p1 and p3),
     * an auxiliary intersection is recorded.
     *
     * @param as the current Angles instance
     * @param o the reference point to exclude from processing
     * @param p1 the first defining point of the line segment
     * @param p3 the second defining point of the line segment
     * @param l2 the line containing candidate points
     */
    void ax_at(Angles as, int o, int p1, int p3, LLine l2) {
        if (inter_ll(l2, fd_ln(p1, p3)) != 0) return;

        for (int k = 0; k <= l2.no; k++) {
            int p2 = l2.pt[k];
            if (p2 != o && !xmid(p2, p1, p3)) {
                auxpt_ill(26, p1, p3, o, p2);
            }
        }
    }

    /**
     * Processes paired angle structures by determining intersections between two pairs of lines.
     * If valid intersection points exist between l1/l2 and l3/l4 respectively, this method
     * initiates further angle processing using ax_as_21.
     *
     * @param as the current Angles instance
     * @param l1 the first line of the first pair
     * @param l2 the second line of the first pair
     * @param l3 the first line of the second pair
     * @param l4 the second line of the second pair
     */
    void ax_as_2(Angles as, LLine l1, LLine l2, LLine l3, LLine l4) {
        int p1, p2;
        if (((p1 = inter_ll(l1, l2)) == 0) || ((p2 = inter_ll(l3, l4)) == 0)) return;

        ax_as_21(as, p1, p2, l1, l2, l3, l4);
        ax_as_21(as, p2, p1, l3, l4, l1, l2);
    }

    /**
     * Processes detailed angle transformations based on intersections and circle conditions.
     * This method evaluates intersections between l1 and l3 (or l2 and l4) along with additional
     * geometric constraints. If the conditions are met and no conflicting line exists, an auxiliary
     * intersection is recorded.
     *
     * @param as the current Angles instance
     * @param p1 the intersection point from the first pair of lines
     * @param p2 the intersection point from the second pair of lines
     * @param l1 the first line of the first pair
     * @param l2 the second line of the first pair
     * @param l3 the first line of the second pair
     * @param l4 the second line of the second pair
     */
    void ax_as_21(Angles as, int p1, int p2, LLine l1, LLine l2, LLine l3, LLine l4) {
        int p3, p4, m;

        if ((p3 = inter_ll(l1, l3)) != 0 && xcir2(p2, p1, p3) &&
                (p4 = inter_lc1(l2, fd_cr_op(p2, p1), p1)) != 0 &&
                p1 != p3 && p1 != p4 && p2 != p3 && p2 != p4 && p3 != p4) {
//            add_codb(CO_ACONG, p3, p1, p1, p4, p3, p2, p2, get_lpt1(l4, p2));
//            add_codb(CO_CYCLIC, p2, p1, p3, p4, 0, 0, 0, 0);
            m = inter_ll(l4, fd_ln(p3, p4));
            if (m == 0) {
                auxpt_ill(20, p3, p4, p2, get_lpt1(l4, p2));
            }
        }
        if ((p4 = inter_ll(l2, l4)) != 0 && xcir2(p2, p1, p4) &&
                (p3 = inter_lc1(l1, fd_cr_op(p2, p1), p1)) != 0 &&
                p1 != p3 && p1 != p4 && p2 != p3 && p2 != p4 && p3 != p4) {
//            add_codb(CO_ACONG, p3, p1, p1, p4, get_lpt1(l3, p2), p2, p2, p4);
//            add_codb(CO_CYCLIC, p2, p1, p3, p4, 0, 0, 0, 0);
            m = inter_ll(l3, fd_ln(p3, p4));
            if (m == 0) {
//m =
                auxpt_ill(21, p3, p4, p2, get_lpt1(l3, p2));
            }
//            if (m != 0) add_mid(0, m, p3, p4);
//            pop_codb();
//            pop_codb();
        }
    }

    /**
     * Iterates over all circles and applies various circle-related transformations.
     * For each circle that is active (non-zero type), this method triggers a sequence of
     * operations including center determination, chord and arc processing, tangent and
     * diameter handling.
     */
    void ax_cr() {
        ACir cr = all_cir.nx;
        while (cr != null) {
            if (cr.type != 0) {
                ax_cr1(cr);
                ax_cr2(cr);
                ax_center(cr);
                ax_cr_cr(cr);
                ax_tan(cr);
                ax_diameter(cr);
            }
            cr = cr.nx;
        }
    }

    /**
     * Processes the diameter of a circle by evaluating candidate lines from the circle's center
     * to its boundary points. If a candidate line exists and does not yield an intersection via
     * circle-line logic, an auxiliary intersection is registered.
     *
     * @param cr the circle to be processed
     */
    void ax_diameter(ACir cr) {
        for (int i = 0; i <= cr.no; i++) {
            LLine ln = fd_ln(cr.o, cr.pt[i]);
            if (ln != null && inter_lc1(ln, cr, cr.pt[i]) == 0) {
                auxpt_ilc(33, cr.o, cr.pt[i], cr);
            }
        }
    }

    /**
     * Processes tangent line generation for a circle.
     * If the circle's center is valid and the circle contains concave points,
     * the method iterates over the circle's points to determine if a tangent line is missing.
     * When a point qualifies, a tangent is created.
     *
     * @param cr1 the circle to be processed for tangency
     */
    void ax_tan(ACir cr1) {
        int i;
        if (cr1.o == 0 || cr1.no <= 1) return;
        if (!in_conc(cr1.o)) return;

        for (i = 0; i <= cr1.no; i++)
            if (in_conc(cr1.pt[i]) && fd_tline(cr1.pt[i], cr1.pt[i], cr1.o) == null) {
                auxpt_tline(11, cr1.pt[i], cr1.pt[i], cr1.o);
            }
    }

    /**
     * Checks if a given point is part of a predefined concave point set.
     *
     * @param p the point to check
     * @return true if the point is in the concave set, false otherwise
     */
    boolean in_conc(int p) {
        for (int i = 0; i <= 7; i++) if (conc.p[i] == p) return (true);
        return (false);
    }


    void ax_cr_cr(ACir cr1) {
        ACir cr2 = all_cir.nx;
        int p1, p2;
        while (cr2 != null) {
            if (cr2.type == 0) //goto m1;
            {
                cr2 = cr2.nx;
                continue;
            }
            p1 = inter_cc(cr1, cr2);
            if (p1 == 0)// goto m1;
            {
                cr2 = cr2.nx;
                continue;
            }
            p2 = inter_cc1(cr1, cr2, p1);
            if (p2 == 0)// goto m1;
            {
                cr2 = cr2.nx;
                continue;
            }
            if (cr1.no >= 3)
                ax_cr_cr1(cr1, cr2, p1, p2);
            if (cr2.no >= 3)
                ax_cr_cr1(cr2, cr1, p2, p1);
            cr2 = cr2.nx;
        }
    }

    /**
     * Processes detailed intersection actions between two circles at specified points.
     *
     * @param cr1 the first circle involved in the intersection
     * @param cr2 the second circle involved in the intersection
     * @param p1 the first intersection point from circle calculations
     * @param p2 the second intersection point from circle calculations
     */
    void ax_cr_cr1(ACir cr1, ACir cr2, int p1, int p2) {
        int p3, p4, p5, p6;
        ProPoint pt1, pt2, pt3, pt4;

        if (cr2.o == 0) return;

        for (int i = 0; i <= cr1.no; i++) {
            p3 = cr1.pt[i];
            if (p3 != p1 && p3 != p2)
                for (int j = 0; j <= cr1.no; j++) {
                    p4 = cr1.pt[j];
                    if (p4 != p1 && p4 != p2 && p4 != p3) {
                        p5 = inter_lc1(fd_ln(p1, p3), cr2, p1);
                        p6 = inter_lc1(fd_ln(p2, p4), cr2, p2);
                        pt1 = pt2 = pt3 = pt4 = null;
                        if (p5 == 0 && p6 != 0 && !mperp(p1, cr2.o, p1, p3)) {
                            pt1 = auxpt_ilc(27, p1, p3, cr2);
                        } else if (p5 != 0 && p6 == 0 && !mperp(p2, cr2.o, p2, p4)) {
                            pt2 = auxpt_ilc(27, p2, p4, cr2);
                        }
                        p5 = inter_lc1(fd_ln(p1, p4), cr2, p1);
                        p6 = inter_lc1(fd_ln(p2, p3), cr2, p2);
                        if (p5 == 0 && p6 != 0 && !mperp(p1, p4, p1, cr2.o)) {
                            pt3 = auxpt_ilc(27, p1, p4, cr2);
                        } else if (p5 != 0 && p6 == 0 && !mperp(p2, p3, p2, cr2.o)) {
                            pt4 = auxpt_ilc(27, p2, p3, cr2);
                        }
                    }
                }
        }
    }

    /**
     * Processes circle intersections by evaluating tangent constructions and auxiliary points.
     *
     * @param cr the circle for which intersections are processed
     */
    void ax_cr1(ACir cr) {
        int p1, p2, p3, p4;
        LLine l1;
        int o = cr.o;

        ProPoint pt = null;

        if (o == 0) return;
        for (p1 = 1; p1 <= cons_no; p1++) {
            if (p1 != o && !on_cir(p1, cr) && (l1 = fd_tline(p1, p1, o)) != null) {
                p3 = get_lpt1(l1, p1);
                for (int i = 0; i <= cr.no; i++) {
                    p2 = cr.pt[i];
                    if (!xcoll(p1, p2, p3) && !xcoll(p1, p2, o) && !xperp(o, p2, o, p1) &&
                            inter_lc1(fd_ln(p1, p2), cr, p2) != 0) {
                        l1 = fd_pline(p2, p1, p3);
                        if (l1 == null) {
                            pt = auxpt_ipc(28, p2, p1, p3, cr);
                        } else if ((p4 = inter_lc1(l1, cr, p2)) == 0) {
                            pt = auxpt_ilc(28, p2, get_lpt1(l1, p2), cr);
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * Verifies and processes tangent intersections for the provided circle.
     *
     * @param cr the circle used for tangent intersection checking
     */
    void ax_cr2(ACir cr) {
        LLine l1;
        int o = cr.o;
        if (o == 0) return;

        for (int i = 0; i <= cr.no; i++) {
            int p1 = cr.pt[i];
            if (inter_lc1(fd_ln(o, p1), cr, p1) == 0) {
                if ((l1 = fd_tline(o, o, p1)) != null && cr.no > 1 && (!(inter_lc(l1, cr) != 0) || cr.no > 2)) {
                    auxpt_ilc(29, p1, o, cr);
                }
            }
        }
    }

    /**
     * Determines and processes the center of a circle based on intersecting lines and points.
     *
     * @param cr the circle for which the center is being determined
     */
    void ax_center(ACir cr) {
        LLine l1, l2;
        PLine pn;
        int k, p1, p2, p3, p4;

        if (cr.o != 0) return;

        for (int i = 0; i <= cr.no; i++) {
            p1 = cr.pt[i];
            for (int j = i + 1; j <= cr.no; j++) {
                p2 = cr.pt[j];
                p3 = fd_pt_md(p1, p2);
                if (p3 == 0)//    goto m1;
                {
                    k = 0;
                    continue;
                }
                l1 = fd_tline(p3, p1, p2);
                if (l1 == null)// goto m1;
                {
                    k = 0;
                    continue;
                }
                pn = fd_pn(p1, p2);
                if (pn == null || pn.no == 0)// goto m1;
                {
                    k = 0;
                    continue;
                }
                k = 0;
                while (k <= pn.no && (l2 = pn.ln[k]) != null &&
                        !(!on_ln(p1, l2) &&
                        (p3 = inter_lc(l2, cr)) != 0 &&
                        (p4 = inter_lc1(l2, cr, p3)) != 0 &&
                        fd_pt_md(p3, p4) == 0))
                    k++;
                if (k > pn.no)// goto m1;
                {
                    k = 0;
                    continue;
                }
                auxpt_co(30, cr);
                k = 0;
            }
        }
    }

    /**
     * Processes point network intersections and applies auxiliary transformations.
     */
    void ax_pn() {
        int p1, p2, p3, p4;

        PLine pn = all_pn.nx;
        while (pn != null) {
            if (pn.type == 0 || pn.no <= 0) //goto m0;
            {
                pn = pn.nx;
                continue;
            }
            for (int i = 0; i <= pn.no; i++) {
                LLine ln1 = pn.ln[i];
                for (int j = i + 1; j <= pn.no; j++) {
                    LLine ln2 = pn.ln[j];
                    if (ln1 == ln2) //goto m1;
                    {
                        p1 = 1;
                        continue;
                    }
                    for (int k1 = 0; k1 <= ln2.no; k1++)
                        for (int k2 = k1 + 1; k2 <= ln2.no; k2++)
                            for (int k = 1; k <= cons_no; k++) {
                                if (k != k1 && k != k2 && k1 != k2 && !on_ln(k, ln1) && !on_ln(k, ln2) &&
                                        (p1 = inter_ll(ln1, fd_ln(k, ln2.pt[k1]))) != 0 && (p2 = inter_ll(ln1, fd_ln(k, ln2.pt[k2]))) != 0) {

                                    LLine ln = all_ln.nx;
                                    while (ln != null) {
                                        if (ln.type == 0 || ln == ln1 || ln == ln2)// goto m2;
                                        {
                                            ln = ln.nx;
                                            continue;
                                        }
                                        if (!on_ln(k, ln)) //goto m2;
                                        {
                                            ln = ln.nx;
                                            continue;
                                        }
                                        p3 = inter_ll(ln1, ln);
                                        p4 = inter_ll(ln2, ln);
                                        if (p3 != 0 && p4 == 0)
                                            auxpt_ill(26, k, p3, ln2.pt[k1], ln2.pt[k2]);

                                        if (p3 == 0 && p4 != 0)
                                            auxpt_ill(27, k, p4, p1, p2);

                                        ln = ln.nx;
                                    }
                                }
                            }
                    p1 = 1;
                }
            }
            pn = pn.nx;
        }
    }

    /**
     * Processes congruence segments within the point network.
     */
    public void ax_cg() {
        CongSeg cg = all_cg.nx;
        while (cg != null) {
            cg = cg.nx;
        }
    }

    /**
     * Initiates the backward process based on current configuration in the point network.
     */
    public void ax_backward() {
        int[] p = conc.p;

        switch (conc.pred) {
            case 0:
                return;
            case CO_CONG:
                {
                    if (xcoll4(p[0], p[1], p[2], p[3])) {
                        //abx_mid()
                    }
                }
                break;
            case CO_MIDP:
                {
                    ax_bk_mid(p[0], p[1], p[2]);
                }
                break;

        }
    }

    /**
     * Processes backward midpoint transformations for the given points.
     *
     * @param m the midpoint involved in the transformation
     * @param p1 the first point component of the midpoint
     * @param p2 the second point component of the midpoint
     */
    public void ax_bk_mid(int m, int p1, int p2) {
        //1: rotat.

        

    }




    /**
     * Calculates the Y coordinate on the tangent line for point p1 using p2 and p3.
     * Sets the point pt with an X coordinate of 0 and the computed Y coordinate.
     *
     * @param pt the point to update with the computed coordinate
     * @param p1 the reference point for the tangent calculation
     * @param p2 the second point used in the calculation
     * @param p3 the third point used in the calculation
     */
    public void cal_ax_tn(ProPoint pt, int p1, int p2, int p3) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);

        double y = y1 + -(x3 - x2) * (0 - x1) / (y3 - y2);
        pt.setXY(0, y);
    }

    /**
     * Calculates the midpoint of points p1 and p2.
     * Updates pt with the computed midpoint coordinates.
     *
     * @param pt the point to update with the midpoint coordinates
     * @param p1 the first point
     * @param p2 the second point
     */
    public void cal_ax_md(ProPoint pt, int p1, int p2) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        pt.setXY((x1 + x2) / 2, (y1 + y2) / 2);
    }

    /**
     * Calculates the intersection point between the lines defined by (p1, p2) and (p3, p4).
     * Updates pt with the computed intersection coordinates if valid.
     *
     * @param pt the point to update with the intersection coordinates
     * @param p1 the first point of the first line
     * @param p2 the second point of the first line
     * @param p3 the first point of the second line
     * @param p4 the second point of the second line
     * @return true if an intersection point is successfully computed, false otherwise
     */
    public boolean cal_ax_ill(ProPoint pt, int p1, int p2, int p3, int p4) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);
        double x4 = VPTX(p4);
        double y4 = VPTY(p4);
        double xt1 = x2 - x1;
        double xt2 = x4 - x3;
        double yt1 = y2 - y1;
        double yt2 = y4 - y3;

        if (Math.abs(yt1 * xt2 - yt2 * xt1) < ZERO)
            return false;

        if (Math.abs(xt1) > ZERO) {
            double x = (xt1 * xt2 * (y3 - y1) + yt1 * x1 * xt2 - yt2 * x3 * xt1) / (yt1 * xt2 - yt2 * xt1);
            double y = y1 + (x - x1) * yt1 / xt1;
            pt.setXY(x, y);
        } else {
            double x = x1;
            double y = y4 + yt2 * (x - x4) / xt2;
            pt.setXY(x, y);
        }
        return true;
    }

    /**
     * Calculates the foot of the perpendicular from point p1 to the line defined by p2 and p3.
     * Updates pt with the computed foot coordinates.
     *
     * @param pt the point to update with the foot coordinates
     * @param p1 the point from which the perpendicular is drawn
     * @param p2 the first point defining the line
     * @param p3 the second point defining the line
     */
    public void cal_ax_foot(ProPoint pt, int p1, int p2, int p3) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);
        double b = y3 - y2;
        double a = x3 - x2;
        double m = a * a + b * b;
        double x = (a * b * y1 + b * b * x3 + a * a * x1 - a * b * y3) / m;
        double y = (b * b * y1 + a * a * y3 + a * b * x1 - a * b * x3) / m;
        pt.setXY(x, y);
    }

    /**
     * Calculates the intersection point for a line configuration using points p1, p2, p3, p4, and p5.
     * Updates pt with the computed intersection coordinates.
     *
     * @param pt the point to update with the intersection coordinates
     * @param p1 the first reference point
     * @param p2 the second reference point
     * @param p3 the third reference point
     * @param p4 the fourth reference point
     * @param p5 the fifth reference point
     * @return true if the intersection is successfully computed, false otherwise
     */
    public boolean cal_ax_ilp(ProPoint pt, int p1, int p2, int p3, int p4, int p5) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);
        double x4 = VPTX(p4);
        double y4 = VPTY(p4);
        double x5 = VPTX(p5);
        double y5 = VPTY(p5);

        double xt1 = x2 - x1;
        double xt2 = x5 - x4;
        double yt1 = y2 - y1;
        double yt2 = y5 - y4;
        if (Math.abs(yt1 * xt2 - yt2 * xt1) < ZERO)
            return false;

        if (Math.abs(xt1) > ZERO) {
            double x = (xt1 * xt2 * (y3 - y1) + yt1 * x1 * xt2 - yt2 * x3 * xt1) / (yt1 * xt2 - yt2 * xt1);
            double y = y1 + (x - x1) * yt1 / xt1;
            pt.setXY(x, y);
        } else {
            double x = x1;
            double y = y3 + yt2 * (x - x3) / xt2;
            pt.setXY(x, y);
        }
        return true;
    }

    /**
     * Calculates the center based on the configuration of points p1, p2, and p3.
     * Updates pt with the computed center coordinates.
     *
     * @param pt the point to update with the center coordinates
     * @param p1 the first reference point
     * @param p2 the second reference point
     * @param p3 the third reference point
     */
    public void cal_ax_co(ProPoint pt, int p1, int p2, int p3) {
        double x_1 = VPTX(p1);
        double x_2 = VPTY(p1);
        double x_3 = VPTX(p2);
        double x_4 = VPTY(p2);
        double x_5 = VPTX(p3);
        double x_6 = VPTY(p3);
        double m = (2 * (x_3 - x_1) * x_6 + (-2 * x_4 + 2 * x_2) * x_5 + 2 * x_1 * x_4 - 2 * x_2 * x_3);

        double x = (x_4 - x_2) * x_6 * x_6 + (-1 * x_4 * x_4 - x_3 * x_3 + x_2 * x_2 + x_1 * x_1) * x_6
                + (x_4 - x_2) * x_5 * x_5 + x_2 * x_4 * x_4 + (-1 * x_2 * x_2 - x_1 * x_1) * x_4 + x_2 * x_3 * x_3;

        x = (-1) * x / m;

        double y = (-1) * ((2 * x_5 - 2 * x_1) * x - x_6 * x_6 - x_5 * x_5 + x_2 * x_2 + x_1 * x_1)
                / ((2 * x_6 - 2 * x_2));

        pt.setXY(x, y);
    }

    /**
     * Calculates the intersection point between a translated line and a circle.
     * The translation is based on the vector from p1 to p2 applied to p3.
     * Updates pt with the computed intersection coordinates.
     *
     * @param pt the point to update with the intersection coordinates
     * @param p1 the first reference point for the translation vector
     * @param p2 the second reference point for the translation vector
     * @param p3 the base point for translation
     * @param p4 the first point defining the circle
     * @param p5 the second point defining the circle
     * @return true if the intersection is successfully computed, false otherwise
     */
    public boolean cal_ax_ipc(ProPoint pt, int p1, int p2, int p3, int p4, int p5) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);
        double x4 = VPTX(p4);
        double y4 = VPTY(p4);
        double x5 = VPTX(p5);
        double y5 = VPTY(p5);
        double dx = x2 - x1;
        double dy = y2 - y1;
        double x0 = x3 + dx;
        double y0 = y3 + dy;
        double[] r = cal_inter_lc(x0, y0, x3, y3, x4, y4, x5, y5);
        if (r.length == 0) return false;
        if (fd_pt(r[0], r[1]) == null)
            pt.setXY(r[0], r[1]);
        else
            pt.setXY(r[2], r[3]);
        return true;
    }

    /**
     * Calculates the intersection point between the line defined by p1 and p2
     * and the circle defined by p3 and p4.
     * Updates pt with the computed intersection coordinates.
     *
     * @param pt the point to update with the intersection coordinates
     * @param p1 the first point defining the line
     * @param p2 the second point defining the line
     * @param p3 the first point defining the circle
     * @param p4 the second point defining the circle
     * @return true if an intersection point is successfully computed, false otherwise
     */
    public boolean cal_ax_ilc(ProPoint pt, int p1, int p2, int p3, int p4) {
        double x1 = VPTX(p1);
        double y1 = VPTY(p1);
        double x2 = VPTX(p2);
        double y2 = VPTY(p2);
        double x3 = VPTX(p3);
        double y3 = VPTY(p3);
        double x4 = VPTX(p4);
        double y4 = VPTY(p4);
        double[] r = cal_inter_lc(x1, y1, x2, y2, x3, y3, x4, y4);
        if (r.length == 0) return false;
        if (fd_pt(r[0], r[1]) == null)
            pt.setXY(r[0], r[1]);
        else
            pt.setXY(r[2], r[3]);
        return true;
    }

    /**
     * Computes the intersection points between the line defined by (x1, y1) and (x2, y2)
     * and the circle defined by center (x3, y3) with a radius derived from (x4, y4).
     * Returns an array containing two intersection points formatted as [t1, m1, t2, m2].
     *
     * @param x1 the x-coordinate of the first point on the line
     * @param y1 the y-coordinate of the first point on the line
     * @param x2 the x-coordinate of the second point on the line
     * @param y2 the y-coordinate of the second point on the line
     * @param x3 the x-coordinate used as a reference for the circle
     * @param y3 the y-coordinate used as a reference for the circle
     * @param x4 the x-coordinate used to derive the circle's radius
     * @param y4 the y-coordinate used to derive the circle's radius
     * @return an array containing the intersection points [t1, m1, t2, m2], or an empty array if there are none
     */
    double[] cal_inter_lc(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        double r2 = (y4 - y3) * (y4 - y3) + (x4 - x3) * (x4 - x3);
        if (Math.abs(x2 - x1) > ZERO) {
            double k = (y2 - y1) / (x2 - x1);
            double t = y2 - y3 - k * x2;
            double a = k * k + 1;
            double b = 2 * k * t - 2 * x3;
            double c = t * t + x3 * x3 - r2;
            double d = b * b - 4 * a * c;
            if (d < 0) return new double[0];
            d = Math.sqrt(d);
            double t1 = (-b + d) / (2 * a);
            double t2 = (-b - d) / (2 * a);
            double m1 = (t1 - x2) * k + y2;
            double m2 = (t2 - x2) * k + y2;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;

        } else {
            double t1 = x1;
            double d = Math.sqrt(r2 - (t1 - x3) * (t1 - x3));
            double m1 = y3 + d;
            double t2 = t1;
            double m2 = y3 - d;
            double[] r = new double[4];
            r[0] = t1;
            r[1] = m1;
            r[2] = t2;
            r[3] = m2;
            return r;

        }
    }

    /**
     * Generates a descriptive string for the given point based on its type.
     * Updates the text attribute of pt with the generated description.
     *
     * @param pt the point for which the description is generated
     */
    void auxpt_string(ProPoint pt) {
        String s = pt.toString();
        if (s == null || s.length() == 0) {
        } else
            s += " is ";

        switch (pt.type) {
            case C_MIDPOINT:
                pt.text = s + "midpoint of " + ANAME(pt.ps[1]) + ANAME(pt.ps[2]);
                break;
            case C_I_LL:
                pt.text = s + "intersection of " + ANAME(pt.ps[1]) + ANAME(pt.ps[2])
                        + " and " + ANAME(pt.ps[3]) + ANAME(pt.ps[4]);
                break;
            case C_FOOT:
                pt.text = s + "the foot of " + ANAME(pt.ps[1]) + "," + ANAME(pt.ps[2]) + "," + ANAME(pt.ps[3]);
                break;
            case C_I_LP:
                pt.text = s + "on line " + ANAME(pt.ps[1]) + ANAME(pt.ps[2]) +
                        " and" + pt + ANAME(pt.ps[3]) + " // " + ANAME(pt.ps[4]) + ANAME(pt.ps[5]);
                break;
            case C_I_LT:
                pt.text = s + "on line " + ANAME(pt.ps[1]) + ANAME(pt.ps[2]) +
                        " and" + pt + ANAME(pt.ps[3]) + " perp " + ANAME(pt.ps[4]) + ANAME(pt.ps[5]);
                break;
            case C_I_LC:
                pt.text = s + "the intersection of  " + ANAME(pt.ps[1]) + ANAME(pt.ps[2]) +
                        " and circle(" + ANAME(pt.ps[3]) + ANAME(pt.ps[4]) + ")";
                break;

        }
    }

    /**
     * Generates and returns an auxiliary name for a new point.
     * The name is determined based on existing constraint names.
     *
     * @return the generated auxiliary name
     */
    protected String fd_aux_name() {
        char c[] = new char[1];//= new char[3];
        int len = 1;
        char n = 1;

        while (true) {
            int j = 1;
            c[0] = 'A';

            while (c[0] <= 'Z') {
                if (len == 1) {
                } else {
                    c[1] = (char) ('0' + n);
                    n++;
                }
                String t = new String(c);
                for (j = 1; j <= cons_no; j++)
                    if (ANAME(j).equals(t))
                        break;
                if (j > cons_no)
                    break;
                c[0] += 1;
            }
            if (j >= cons_no)
                break;
            len++;
            c = new char[len];
            c[0] = 'A';
        }
        return new String(c);
    }

}
