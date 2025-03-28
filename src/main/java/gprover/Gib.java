package gprover;


import wprover.GExpert;

import java.util.Vector;


/**
 * The Gib class provides functionality for geometric computations,
 * including operations on angles, lines, circles, triangles, and other
 * geometric constructions.
 *
 * <p>This class is a central component within the geometric prover system,
 * facilitating the creation, manipulation, and evaluation of various geometric
 * entities and their relationships.</p>
 */
public class Gib {

    //********************pred types*************************

    final public static int C_POINT = 1;
    final public static int C_LINE = 2;
    final public static int C_O_L = 3;
    final public static int C_O_P = 4;
    final public static int C_O_T = 5;
    final public static int C_O_B = 6;

    final public static int C_O_A = 7;
    final public static int C_FOOT = 8;
    final public static int C_CIRCLE = 9;
    final public static int C_O_C = 10;
    final public static int C_CIRCUM = 11;
    final public static int C_O_R = 12;

    final public static int C_MIDPOINT = 13;


    final public static int C_EQDISTANCE = 14;
    final public static int C_EQANGLE = 15;


    final public static int C_TRATIO = 16;
    final public static int C_PRATIO = 17;
    final public static int C_NRATIO = 18;
    final public static int C_LRATIO = 19;

    final public static int C_INVERSION = 20;
    final public static int C_REF = 21;
    final public static int C_SYM = 22;

    final public static int C_TRIANGLE = 23;
    final public static int C_QUADRANGLE = 24;
    final public static int C_PENTAGON = 25;
    final public static int C_POLYGON = 26;

    final public static int C_ISO_TRI = 27;
    final public static int C_R_TRI = 28;
    final public static int C_EQ_TRI = 29;
    final public static int C_TRAPEZOID = 30;
    final public static int C_R_TRAPEZOID = 31;
    final public static int C_PARALLELOGRAM = 32;
    final public static int C_LOZENGE = 33;
    final public static int C_RECTANGLE = 34;
    final public static int C_SQUARE = 35;


    final public static int C_ICENT = 36;
    final public static int C_ORTH = 37;
    final public static int C_CENT = 38;
    final public static int C_CONSTANT = 39;


    final public static int C_PSQUARE = 40;
    final public static int C_NSQUARE = 41;
    final public static int C_SANGLE = 42;
    final public static int C_ANGLE_BISECTOR = 43;
    final public static int C_LC_TANGENT = 44;
    final public static int C_RATIO = 45;
    final public static int C_CCTANGENT = 46;
    final public static int C_O_S = 47;
    final public static int C_O_AB = 48;
    final public static int C_O_D = 49;
    final public static int C_EQANGLE3P = 50;


    final public static int C_ICENT1 = 199;

    final public static int C_I_LL = 101;
    final public static int C_I_LP = 102;
    final public static int C_I_LC = 103;
    final public static int C_I_LB = 104;
    final public static int C_I_LT = 105;
    final public static int C_I_LR = 106;
    final public static int C_I_LS = 107;
    final public static int C_I_PP = 108;
    final public static int C_I_PC = 109;
    final public static int C_I_PT = 110;
    final public static int C_I_PB = 111;
    final public static int C_I_TC = 112;
    final public static int C_I_TT = 113;
    final public static int C_I_TB = 114;
    final public static int C_I_BB = 115;
    final public static int C_I_BC = 116;
    final public static int C_I_CC = 117;
    final public static int C_I_CR = 118;
    final public static int C_I_RR = 119;
    final public static int C_I_SS = 120;
    final public static int C_I_AA = 121;

    final public static int C_I_LA = 122;
    final public static int C_I_PA = 123;
    final public static int C_I_PR = 124;
    final public static int C_I_TA = 125;
    final public static int C_I_TR = 126;
    final public static int C_I_BA = 127;
    final public static int C_I_BR = 128;
    final public static int C_I_EQ = 129; // B = C.                                 


    final public static int C_NETRIANGLE = 151;
    final public static int C_PETRIANGLE = 150;

    //********************end of pred types*************************

    //********************conclusions******************************
    final public static int CO_COLL = 50 + 20;
    final public static int CO_PARA = 51 + 20;
    final public static int CO_PERP = 52 + 20;
    final public static int CO_MIDP = 53 + 20;
    final public static int CO_CYCLIC = 54 + 20;
    final public static int CO_CONG = 55 + 20;
    final public static int CO_ACONG = 56 + 20;
    final public static int CO_PBISECT = 57 + 20;
    final public static int CO_TANGENT = 58 + 20;
    final public static int CO_HARMONIC = 59 + 20;
    final public static int CO_PETRI = 60 + 20;
    final public static int CO_STRI = 61 + 20;
    final public static int CO_CTRI = 62 + 20;
    final public static int CO_PROD = 63 + 20;
    final public static int CO_ORTH = 64 + 20;
    final public static int CO_INCENT = 65 + 20;
    final public static int CO_RATIO = 66 + 20;
    final public static int CO_TANG = 67 + 20;
    final public static int CO_NANG = 68 + 20;
    final public static int CO_NSEG = 69 + 20;

    final public static int CO_ATNG = 1111;
    final public static int CO_EQ = 1112;
    final public static int CO_12 = 1903;
    final public static int CO_PET = 1904;
    //********************end of conclusions******************************
    //****************************Ordered .

    final public static int NDG_NEQ = 200;
    final public static int NDG_COLL = 201;
    final public static int NDG_PARA = 202;
    final public static int NDG_PERP = 203;
    final public static int NDG_CYCLIC = 204;
    final public static int NDG_CONG = 205;
    final public static int NDG_ACONG = 206;
    final public static int NDG_NON_ISOTROPIC = 207;
    final public static int NDG_TRIPLEPI = 208;


    //*********************Inequality Predicates
    final public static int IN_BETWEEN = 301;
    final public static int IN_AG_INSIDE = 302;
    final public static int IN_AG_OUTSIDE = 303;
    final public static int IN_TRI_INSIDE = 304;
    final public static int IN_PARA_INSIDE = 305;
    final public static int IN_OPP_SIDE = 306;
    final public static int IN_SAME_SIDE = 307;
    final public static int IN_PG_CONVEX = 308;

    //*************************************

    //*******************************************************************

    //************special polygons**************

    final public static int TRIANGLE = 1;
    final public static int QUADRANGLE = 2;
    final public static int PENTAGON = 3;
    final public static int POLYGON = 4;

    final public static int ISO_TRI = 5;
    final public static int R_TRI = 6;
    final public static int EQ_TRI = 7;
    final public static int TRAPEZOID = 8;
    final public static int R_TRAPEZOID = 9;
    final public static int PARALLELOGRAM = 10;
    final public static int LOZENGE = 11;
    final public static int RECTANGLE = 12;
    final public static int SQUARE = 13;

    //******************end of special polgons***************

    //******************angles**************************
    final public static int A_TIME = 12;
    final public static int A_360 = 360 * A_TIME;
    final public static int A_180 = 180 * A_TIME;
    final public static int A_90 = 90 * A_TIME;
    final public static int A_60 = 60 * A_TIME;
    final public static int A_45 = 45 * A_TIME;
    final public static int A_30 = 30 * A_TIME;
    //******************end of angles**************************

    //****************************rules*******************************
    final public static int R_P_COLL = 1;
    final public static int R_PL_AS = 2;
    final public static int R_P_RA = 3;
    final public static int R_PT_T = 4;

    final public static int R_T_AT90 = 5;
    final public static int R_TT_MDCY = 6;
    final public static int R_TT_PP = 7;
    final public static int R_TT_CY = 8;

    final public static int R_CR_DM_MD = 9;
    final public static int R_CR_DM_T = 10;//
    final public static int R_CR_AS2 = 11;
    final public static int R_CR_P_EQARC = 12;
    //cr_iso?
    final public static int R_CR_INSCRIBE_AS = 13;
    final public static int R_CR_TAN_AS = 14;
    final public static int R_CR_OO_B = 15;

    final public static int R_AS_PLUS = 16;
    final public static int R_AG_PP12 = 17;
    final public static int R_AG_PP13 = 18;
    final public static int R_AG_TT12 = 19;
    final public static int R_AG_TT13 = 20;
    final public static int R_AG_ATN = 21;
    final public static int R_AG_SPECIAL = 22;


    final public static int R_ISOCELES = 23;
    final public static int R_ISO_3L = 24;
    final public static int R_CTRI = 25;

    final public static int R_AAS = 26;
    final public static int R_SAS = 27;
    final public static int R_SSS = 28;
    final public static int R_TTCG2_CT = 29;

    final public static int R_STRI = 30;
    final public static int R_AA_STRI = 31;
    final public static int R_ASRA_STRI = 32;
    final public static int R_ST_RAAS = 33;
    final public static int R_RA_ST_CT = 34;

    final public static int R_MID_CONNECTION = 35;
    final public static int R_RTRI_MD_CY = 36;
    final public static int R_TRI_EQ = 37;
    final public static int R_PYTH_THM = 38;
    final public static int R_TRI_ALL_AG_180 = 39;

    final public static int R_PARALLELOGRAM = 40;
    final public static int R_MID_CONNECTION_TRAPZOID = 41;

    final public static int R_RATIO = 42;
    final public static int R_AG_BISECTOR_ATIO = 43;

    protected static boolean R_SEARCH_ALL_LN = true;
    protected static boolean R_AG_ALL = true;
    public static boolean[] RValue = new boolean[100];

    //aux

    //****************************end of rules************************

    //**************Full Angle Rules.


    final public static int RF_GIB = -1;
    final public static int RF_DEFINITION = 1;
    final public static int RF_MINUS = 2;
    final public static int RF_PARA = 3;
    final public static int RF_PERP = 4;
    final public static int RF_ADDITION = 5;
    final public static int RF_PERP_SPLIT = 6;
    final public static int RF_ISO = 7;
    final public static int RF_INSCRIBE = 8;
    final public static int RF_9 = 9;
    final public static int RF_10 = 10;
    final public static int RF_DM_PERP = 11;
    final public static int RF_12 = 12;
    final public static int RF_13 = 13;
    final public static int RF_14 = 14;
    final public static int RF_15 = 15;
    final public static int RF_16 = 16;
    final public static int RF_17 = 17;
    final public static int RF_18 = 18;
    final public static int RF_ORTH = 19;
    final public static int RF_20 = 20;
    final public static int RF_21 = 21;
    final public static int RF_22 = 22;
    final public static int RF_TT = 23;
    final public static int RF_TT2 = 24;
    final public static int RF_PPO = 25;
    final public static int RF_26 = 26;
    final public static int RF_DM2 = 27;
    final public static int RF_CY = 28;
    final public static int RF_CY2 = 29;

    //////////////////////////////////////////////////////////////////

    final public static int FE_TYPE_ERROR = 1;

    //end of Full Angle Rules.

    //****************************facts*******************************
    protected MidPt all_md, last_md;
    protected LLine all_ln, last_ln;
    protected PLine all_pn, last_pn;
    protected TLine all_tn, last_tn;
    protected ACir all_cir, last_cir;
    protected Angles all_as, last_as;
    protected AngSt all_ast, last_ast;
    protected CongSeg all_cg, last_cg;
    protected CongSeg all_rg, last_rg;
    protected AngleT all_at, last_at;
    protected AngTn all_atn, last_atn;
    protected SimTri all_st, last_st;
    protected SimTri all_ct, last_ct;
    protected RatioSeg all_ra, last_ra;
    protected Polygon all_pg, last_pg;

    protected Cond all_nd, last_nd;
    protected Cond co_db, co_xy;
    // for conclusion;
    protected AngTr all_tr, last_tr;
    protected LList all_ns, last_ns;
    // for collection.
    protected STris all_sts, last_sts;
    protected STris all_cts, last_cts;
    protected CSegs all_cgs, last_cgs;
    //****************************end of facts*******************************

    //***************************inputs******************
    protected GTerm gt;
    protected ProPoint[] allpts = new ProPoint[100];
    protected Cons[] allcns = new Cons[100];
    protected int pts_no, pts_pno;
    protected int gno = 0;
    protected int cns_no;

    protected Cond conc = new Cond();
    protected int cons_no = 0;


    //*****************************othres***********************
    final protected static ACir test_c = new ACir();
    final protected static LLine test_ln = new LLine();
    final protected static RatioSeg test_ra1 = new RatioSeg();
    protected RatioSeg test_ra;
    static Cond tm_pr1 = new Cond();
    public static long depth = 0;
    final protected static double ZERO = 0.001;
    protected static boolean show_detail = false;


    protected static boolean ck_value = true;

    protected static int P_STATUS = 0;
    protected int tri_type;
    protected int d_base = 1;
    protected int show_dtype = 0;
    protected int printype = 0; // screen  1: text
    protected StringBuffer sout = null;
    protected boolean DEBUG = true;

    /**
     * Initializes the rule configuration.
     * Sets all rules to true and then disables specific rules.
     */
    public static void initRules() {
        for (int i = 0; i < RValue.length; i++)
            RValue[i] = true;

//        RValue[R_RATIO - 1] = false;
        RValue[R_AG_BISECTOR_ATIO - 1] = false;
        RValue[R_PARALLELOGRAM - 1] = false;
        RValue[R_PYTH_THM-1] = false;
       // RValue[R_RATIO - 1] = false;
        RValue[R_RA_ST_CT - 1] = false;
        //      RValue[R_AA_STRI -1] = false;

    }

    /**
     * Constructs a new Gib instance.
     * Initializes all internal data structures and sets default values.
     */
    public Gib() {

        co_db = new Cond();
        co_xy = new Cond();

        all_md = new MidPt();
        all_ln = new LLine();
        all_pn = new PLine();
        all_tn = new TLine();
        all_cir = new ACir();
        all_as = new Angles();
        all_ast = new AngSt(0);
        all_at = new AngleT();
        all_cg = new CongSeg();
        all_rg = new CongSeg();
        all_st = new SimTri();
        all_ct = new SimTri();
        all_ra = new RatioSeg();
        all_nd = new Cond();

        co_db = new Cond();

        d_base = 0;
        all_sts = new STris();
        all_cts = new STris();
        all_cgs = new CSegs();
        all_pg = new Polygon();
        all_atn = new AngTn();


        all_ns = new LList();
        all_tr = new AngTr();

        gt = null;
    }

    /**
     * Initializes the database.
     * Resets various lists, counters, and clears auxiliary collections.
     */
    public void init_dbase() {

        depth = 0;

        all_md.nx = null;
        all_ln.nx = null;
        all_pn.nx = null;
        all_tn.nx = null;
        all_cir.nx = null;
        all_as.nx = null;
        all_ast.nx = null;
        all_cg.nx = null;
        all_rg.nx = null;
        all_at.nx = null;
        all_atn.nx = null;

        all_st.nx = null;
        all_ct.nx = null;
        all_ra.nx = null;
        all_nd.nx = null;
        all_pg.nx = null;

        co_db.nx = null;
        co_xy.nx = null;
        all_tr.nx = null;
        all_nd.nx = null;
        last_nd = all_nd;

        all_sts.nx = null;
        all_cts.nx = null;
        all_cgs.nx = null;

        last_md = all_md;
        last_ln = all_ln;
        last_pn = all_pn;
        last_tn = all_tn;
        last_cir = all_cir;
        last_as = all_as;
        last_ast = all_ast;
        last_at = all_at;
        last_cg = all_cg;
        last_rg = all_rg;
        last_st = all_st;
        last_ct = all_ct;
        last_ra = all_ra;
        last_nd = all_nd;
        last_pg = all_pg;
        last_atn = all_atn;

        last_sts = all_sts;
        last_cts = all_cts;
        last_cgs = all_cgs;


        last_ns = all_ns;
        last_tr = all_tr;

        co_db.nx = null;

        d_base = 0;
        pts_no = pts_pno = 0;

        for (int i = 0; i < 100; i++) {
            allpts[i] = null;
            allcns[i] = null;
        }
        cns_no = 0;
        conc.pred = 0;
        cons_no = 0;
        vauxpts.clear();
        vauxptf.clear();
        gt = null;
        tm_pr1 = new Cond();
    }

    /**
     * Validates the provided index.
     *
     * @param i the index to check
     * @return true if the index is within valid bounds; false otherwise
     */
    boolean valid(int i) {
        if (i == 0 || i > 100) return true;
        if (i < 0 || i >= RValue.length) return false;
        return RValue[i - 1];
    }

    /**
     * Retrieves the x-coordinate of the point at the specified index.
     *
     * @param p the index of the point in the array
     * @return the x-coordinate of the point
     */
    final double VPTX(int p) {
        return allpts[p].x;
    }

    /**
     * Retrieves the y-coordinate of the point at the specified index.
     *
     * @param p the index of the point in the array
     * @return the y-coordinate of the point
     */
    final double VPTY(int p) {
        return allpts[p].y;
    }

    /**
     * Retrieves the name of the point at the specified index.
     *
     * @param p the index of the point in the array
     * @return the name of the point, or null if the index is negative
     */
    final String ANAME(int p) {
        if (p < 0) return null;

        if (p > 0) {
            ProPoint t = allpts[p];
            if (t == null) {
                int k = 0;
            }
            return t.name;
//            return allpts[p].name;
        } else
            return null;
    }

    /**
     * Searches for a point by its name.
     *
     * @param s the name to search for
     * @return the index of the point if found; otherwise, 0
     */
    final int SPT(String s) {
        if (s == null || s.length() == 0) return 0;

        for (int i = 1; true; i++) {
            if (allpts[i] == null) break;
            if (s.equals(allpts[i].getName())) return i;
        }
        return 0;
    }

    /**
     * Counts the number of defined properties among various entities.
     *
     * @return the total count of properties
     */
    final int getNumberofProperties() {
        int n = 0;
        MidPt md = all_md.nx;
        while (md != null) {
            md = md.nx;
            if(md != null && md.type != 0)
                n++;
        }
        
        LLine ln = all_ln.nx;
        while (ln != null) {
            ln = ln.nx;
            if(ln != null && ln.type != 0 && ln.no > 2)
            n++;
        }
        PLine pn = all_pn;
        while (pn != null) {
            pn = pn.nx;
            if(pn != null && pn.type != 0)
                n++;
        }
        TLine tn = all_tn;
        while (tn != null) {
            tn = tn.nx;
            if(tn != null && tn.type != 0)
                n++;
        }
        ACir cr = all_cir.nx;
        while (cr != null) {
            cr = cr.nx;
            if(cr != null && cr.type != 0)
                n++;
        }
        Angles ag = all_as.nx;
        while (ag != null) {
            ag = ag.nx;
            if(ag != null && ag.type != 0)
                n++;
        }
//        angst ags = all_ast.nx;
//        while (ags != null) {
//            ags = ags.nx;
//            if(ags != null && ags.type != 0)
//                n++;
//        }
        CongSeg cg = all_cg.nx;
        while (cg != null) {
            cg = cg.nx;
            if(cg != null && cg.type != 0)
                n++;
        }
        CongSeg csg = all_rg.nx;
        while (csg != null) {
            csg = csg.nx;
            if(csg != null && csg.type != 0)
                n++;
        }
//        anglet at = all_at.nx;
//        while (at != null) {
//            at = at.nx;
//            if(at != null && at.type != 0)
//                n++;
//        }
//        angtn atn = all_atn;
//        while (atn != null) {
//            atn = atn.nx;
//            if(atn != null && atn.type != 0)
//                n++;
//        }
        SimTri st = all_st.nx;
        while (st != null) {
            st = st.nx;
            if(st != null && st.type != 0)
                n++;
        }
        SimTri ct = all_ct.nx;
        while (ct != null) {
            ct = ct.nx;
            if(ct != null && ct.type != 0)
                n++;
        }
        RatioSeg ra = all_ra;
        while (ra != null) {
            ra = ra.nx;
            if(ra != null && ra.type != 0)
                n++;
        }
        Polygon pg = all_pg;
        while (pg != null) {
            pg = pg.nx;
            if(pg != null && pg.type != 0)
                n++;
        }
        return n;
    }

    /**
     * Retrieves the point at the specified index.
     *
     * @param i the index of the point in the array
     * @return the ProPoint at the specified index
     */
    final ProPoint APT(int i) {
        ProPoint pt = allpts[i];
        if (pt == null) {
            int k = 0;
        }
        return pt;
    }

    /**
     * Returns the type of the point at the given index.
     *
     * @param t the index of the point in the array
     * @return the type of the point
     */
    final int ATYPE(int t) {
        return (allpts)[t].type;
    }

    /**
     * Retrieves an auxiliary point index from the given point.
     *
     * @param p the index of the point in the array
     * @param i the auxiliary index within the point
     * @return the auxiliary point index
     */
    final int APTS(int p, int i) {
        return ((allpts)[p].ps[i]);
    }

    /**
     * Returns the x-coordinate of the point at the given index.
     *
     * @param p the index of the point in the allpts array
     * @return the x-coordinate of the point, or 0.0 if the point is null
     */
    final double aptx(int p) {
        if (allpts[p] != null)
            return allpts[p].x;
        else {
            eprint("allpts[" + p + "] overflow!");
            return 0.0;
        }
    }

    /**
     * Returns the y-coordinate of the point at the given index.
     *
     * @param p the index of the point in the allpts array
     * @return the y-coordinate of the point, or 0.0 if the point is null
     */
    final double apty(int p) {
        if (allpts[p] != null)
            return allpts[p].y;
        else {
            eprint("allpts[" + p + "] overflow!");
            return 0.0;
        }
    }

    /**
     * Checks if the specified index corresponds to a conclusion.
     *
     * @param n the index in the allcns array
     * @return true if the conclusion exists and is valid; false otherwise
     */
    final boolean isConclusion(int n) {
        if (allcns[n] != null)
            return allcns[n].is_conc();
        return false;
    }

    /**
     * Finds the point index by its name.
     *
     * @param s the name of the point
     * @return the index of the point if found; 0 otherwise
     */
    public int fd_pt(String s) {
        return SPT(s);
    }

    /**
     * Finds a point by its coordinates.
     *
     * @param x the x-coordinate to match
     * @param y the y-coordinate to match
     * @return the point if one exists within tolerance; null otherwise
     */
    public ProPoint fd_pt(double x, double y) {
        double m, n;
        for (int i = 1; i <= pts_no; i++) {
            m = APT(i).x;
            n = APT(i).y;
            if ((m - x) * (m - x) + (n - y) * (n - y) < ZERO)
                return APT(i);
        }
        return null;
    }

    /**
     * Sets the print mode to output to a string buffer.
     */
    void setPrintToString() {
        printype = 1;
        sout = new StringBuffer();//"";
    }

    /**
     * Disables printing output.
     */
    public void setNoPrint() {
        printype = -1;
    }

    /**
     * Returns the string buffer containing the file proof.
     *
     * @return the string buffer with the proof content
     */
    StringBuffer getFileProve() {
        return sout;
    }

    /**
     * Retrieves and resets the printed output string.
     *
     * @return the printed output as a string
     */
    String getPrintedString() {
        if (sout == null) return "";
        String s = sout.toString();
        sout = new StringBuffer();
        return s;
    }

    /**
     * Prints an error message if debugging is enabled.
     *
     * @param s the error message to print
     */
    void eprint(String s) {
        if (Cm.DEBUG)
            System.out.println(s);
    }

    /**
     * Prints a general message based on the current print type.
     *
     * @param s the message to print
     */
    void gprint(String s) {
        if (printype == 0) {
            if (Cm.DEBUG)
                System.out.print(s);
        } else if (printype == 1) {
            sout.append(s);
        } else {
        }
    }

    /**
     * Prints a debug message if debugging is enabled.
     *
     * @param s the debug message to print
     */
    void debug_print(String s) {
        if (DEBUG)
            Cm.print(s);
    }

    /**
     * Checks if the proof status is full.
     *
     * @return true if the proof status is full; false otherwise
     */
    public boolean isPFull() {
        return P_STATUS == 0;
    }

    /**
     * Exits the application with the specified error id.
     *
     * @param id the error id for the exit process
     */
    public void gexit(int id) {
        gprint("exit " + id);
        Cm.print("Error, exit " + id);
    }

    /**
     * Displays the parallel line information.
     *
     * @param pn the PLine object representing parallel lines
     */
    void show_pn(PLine pn) {
        gprint(pn_string(pn));
    }

    /**
     * Returns a formatted string representing the parallel lines in a PLine.
     *
     * @param pn the PLine object containing lines to be formatted
     * @return the formatted string of parallel lines
     */
    String pn_string(PLine pn) {
        String s = "";
        for (int i = 0; i <= pn.no; i++) {
            s += ln_string(pn.ln[i], false);
            if (i != pn.no) s += "; ";
        }
        s = GExpert.getTranslationViaGettext("{0} are parallel", s);
        return s;
    }

    /**
     * Displays the string representation of the given line.
     *
     * @param ln the LLine object to display
     * @param nk flag indicating whether to translate as collinear
     */
    void show_ln(LLine ln, boolean nk) {
        gprint(ln_string(ln, nk));
    }

    /**
     * Returns a formatted string representing the line.
     *
     * @param ln the LLine object to convert to string
     * @param nk flag indicating whether to use collinearity translation
     * @return the formatted string of the line
     */
    String ln_string(LLine ln, boolean nk) {
        int i;
        if (ln == null)
            return Cm.s2713;
        else {
            String s = "";
            for (i = 0; i <= ln.no; i++) {
                s += ANAME(ln.pt[i]);
                if (i != ln.no) s += ",";
            }
            if (nk) s = GExpert.getTranslationViaGettext("{0} are collinear", s);
            return s;
        }
    }

    /**
     * Returns a formatted string representing the perpendicular relationship of a TLine.
     *
     * @param tn the TLine object to convert to string
     * @return the formatted string indicating perpendicularity
     */
    String tn_string(TLine tn) {
        LLine l1, l2;

        l1 = tn.l1;
        l2 = tn.l2;

        String s = "";
        s = ln_string(l1, false) + "; " + ln_string(l2, false);
        s = GExpert.getTranslationViaGettext("{0} are perpendicular", s);
        return s;
    }

    /**
     * Displays the formatted string for the given TLine.
     *
     * @param tn the TLine object representing perpendicular lines
     */
    void show_tn(TLine tn) {
        gprint(tn_string(tn));
    }

    /**
     * Displays the angle represented by an AngTn object as a combination of two angle components.
     *
     * @param atn the AngTn object containing two sets of line pairs forming an angle
     */
    void show_atn(AngTn atn) {
        show_agll(atn.ln1, atn.ln2);
        gprint(" + ");
        show_agll(atn.ln3, atn.ln4);
        gprint(" = 90");
    }

    /**
     * Displays the segments contained in a CSegs object.
     *
     * @param cg the CSegs object holding congruent segments
     */
    void show_cseg(CSegs cg) {
        if (cg.no < 0) {
            gprint("NULL");
            return;
        }

        gprint(ANAME(cg.p1[0]) + ANAME(cg.p2[0]));
        for (int i = 1; i <= cg.no; i++) {
            gprint(" = " + ANAME(cg.p1[i]) + ANAME(cg.p2[i]));
        }
    }

    /**
     * Displays the congruent segment information for the given CongSeg object.
     *
     * @param cg the CongSeg object containing congruence data
     */
    void show_cg(CongSeg cg) {
        if (cg == null) {
            Cm.print("cong_seg is null");
            return;
        }

        if (cg.t1 == cg.t2) {
            String str = ANAME(cg.p1) + ANAME(cg.p2) + " ";
            gprint(str);
            gprint("= ");
            String s = ANAME(cg.p3) + ANAME(cg.p4) + " ";
            gprint(s);
        } else {

            int p1, p2, p3, p4;
            int m1, m2;
            p1 = p2 = p3 = p4 = m1 = m2 = 0;
            if (cg.t1 <= cg.t2) {
                p1 = cg.p1;
                p2 = cg.p2;
                p3 = cg.p3;
                p4 = cg.p4;
                m1 = cg.t1;
                m2 = cg.t2;
            } else {
                p1 = cg.p3;
                p2 = cg.p4;
                p3 = cg.p1;
                p4 = cg.p2;
                m1 = cg.t2;
                m2 = cg.t1;
            }
            int t1 = Sqrt(m1);
            int t2 = Sqrt(m2);
            String s = "";
            if (t1 > 0 && t2 > 0)
                s = t1 + " / " + t2;
            else {
                if (t1 > 0)
                    s += t1;
                else
                    s += "s(" + m1 + ")";
                s += " / ";
                if (t2 > 0)
                    s += t2;
                else
                    s += "s(" + m2 + ")";
            }
            String str = ANAME(p1) + ANAME(p2) + "/" + ANAME(p3) + ANAME(p4) + " = " + s;
            gprint(str);
        }
    }

    /**
     * Prints the formatted angle string for the angle defined by four points.
     *
     * @param p1 the first vertex of the angle
     * @param p2 the second vertex of the angle
     * @param p3 the third vertex of the angle
     * @param p4 the fourth vertex of the angle
     */
    void print_fang(int p1, int p2, int p3, int p4) {
        gprint(get_fang_str(p1, p2, p3, p4));
    }

    /**
     * Returns a formatted string representing an angle defined by four points.
     *
     * @param p1 the first vertex of the angle
     * @param p2 the second vertex of the angle
     * @param p3 the third vertex of the angle
     * @param p4 the fourth vertex of the angle
     * @return the formatted angle string
     */
    String get_fang_str(int p1, int p2, int p3, int p4) {
        int p0;

        if (p1 == p4) {
            p0 = p4;
            p4 = p3;
            p3 = p0;
        } else if (p2 == p3) {
            p0 = p1;
            p1 = p2;
            p2 = p0;
        } else if (p2 == p4) {
            p0 = p1;
            p1 = p2;
            p2 = p0;
            p0 = p4;
            p4 = p3;
            p3 = p0;
        }
        if (p1 != p3)
            return Cm.ANGLE_SIGN + "[" + pt_name(p1) + pt_name(p2) + "," + pt_name(p3) + pt_name(p4) + "]";
        else
            return Cm.ANGLE_SIGN + "[" + pt_name(p2) + pt_name(p3) + pt_name(p4) + "]";

    }

    /**
     * Returns the name of the point identified by its index.
     *
     * @param p the index of the point
     * @return the name of the point, or an empty string if the index is zero
     */
    final String pt_name(int p) {
        if (p != 0) return (APT(p).name);
        else return ("");
    }

    /**
     * Displays the angle between two lines by determining their intersection point.
     *
     * @param ln1 the first LLine object
     * @param ln2 the second LLine object
     */
    final public void show_agll(LLine ln1, LLine ln2) {  // overrited.
        int n = inter_lls(ln1, ln2);
        if (n == 0)
            print_fang(ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1]);
        else {
            int p1 = get_lpt1(ln1, n);
            int p2 = get_lpt1(ln2, n);
            print_fang(p1, n, n, p2);
        }
    }

    /**
     * Displays a formatted representation of an angle expressed by an Angles object.
     *
     * @param as the Angles object containing the angle representation
     */
    final void show_as(Angles as) {
        if (as == null) return;
        show_agll(as.l1, as.l2);
        gprint(" = ");
        show_agll(as.l3, as.l4);
    }

    /**
     * Returns a formatted string representing the midpoint relationship.
     *
     * @param md the MidPt object containing midpoint data
     * @return the formatted midpoint string
     */
    final String md_print(MidPt md) {
        String st = GExpert.getTranslationViaGettext("{0} is the midpoint of {1}",
                ANAME(md.m), ANAME(md.a) + ANAME(md.b));
        return st;
    }

    /**
     * Displays a formatted representation of the midpoint.
     *
     * @param md the midpoint to be displayed
     */
    final void show_md(MidPt md) {
        gprint(md_print(md));
    }

    /**
     * Displays a formatted representation of the ratio segment.
     *
     * @param ra the ratio segment to be displayed
     */
    final void show_ra(RatioSeg ra) {
        if (show_dtype != 0) {
            gprint("[" + ra.type + "]:");
        }
        String str =
                ANAME(ra.r[1]) + ANAME(ra.r[2]) + "·" + ANAME(ra.r[7]) + ANAME(ra.r[8]) +
                        " = " + ANAME(ra.r[3]) + ANAME(ra.r[4]) + "·" + ANAME(ra.r[5]) + ANAME(ra.r[6]);
        gprint(str);
    }

    /**
     * Returns a string representation of the circle.
     *
     * @param cr the circle object
     * @return the formatted circle string
     */
    final String cr_string(ACir cr) {
        char i;
        if (cr == null) {
            Cm.print("a_cir == null");
            return "";
        }
        String s = "";
        if (show_dtype != 0) {
            s = "[" + cr.type + "]";
            return s;
        }
        if (cr.o > 0) {
            s += "[" + ANAME(cr.o) + ", ";
        } else {
            s += "[";
        }
        for (i = 0; i <= cr.no; i++)
            s += ANAME(cr.pt[i]);
        s += "]";
        return GExpert.getTranslationViaGettext("Circle {0}", s);
    }

    /**
     * Displays the circle using its string representation.
     *
     * @param cr the circle object to be displayed
     */
    final void show_cr(ACir cr) {
        gprint(cr_string(cr));
    }

    /**
     * Displays a formatted representation of the similar triangle.
     *
     * @param st the similar triangle to be displayed
     */
    final void show_ct(SimTri st) {
        gprint(Cm.s2722);
        if (show_dtype != 0) {
            gprint("[" + st.type + "]");
        }
        String s = "[" + st.dr + "." +
                ANAME(st.p1[0]) + ANAME(st.p1[1]) + ANAME(st.p1[2]) +
                "." + ANAME(st.p2[0]) + ANAME(st.p2[1]) + ANAME(st.p2[2]) + "] ";
        gprint(s);
    }

    /**
     * Displays a formatted representation of a set of similar triangles.
     *
     * @param st the set of similar triangles to be displayed
     */
    final void show_sts(STris st) {
        String s = "";
        int i = 0;
        for (; i <= st.no; i++) {
            s += "[" + st.dr[i] + "," +
                    ANAME(st.p1[i]) + ANAME(st.p2[i]) + ANAME(st.p3[i]) + "] ";
        }
        s = i + ". " + s;
        gprint(s);
    }

    /**
     * Exits the system with the specified exit status.
     *
     * @param v the exit status code
     */
    final public void exit(int v) {
        Cm.print("System exit: " + v);
        System.exit(v);
    }

    /**
     * Prints an error message and terminates the program.
     *
     * @param s the error message to display before exiting
     */
    final public void gerror(String s) {
        Cm.print("Error: " + s);
        System.exit(0);
    }

    /**
     * Returns a common point between two lines, or 0 if none exists.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return the common point of the two lines, or 0 if there is no intersection
     */
    final int inter_lls(LLine l1, LLine l2) {
        int i, j;
        if (l1 == null || l2 == null) return (0);
        if (l1 == l2) return 0;
        for (i = 0; i <= l1.no; i++)
            for (j = 0; j <= l2.no; j++) {
                if (l1.pt[i] == l2.pt[j]) return (l1.pt[i]);
            }
        return (0);
    }

    /**
     * Returns the first point from the line that is not equal to the specified point.
     *
     * @param l1 the line to search
     * @param p1 the point to be excluded
     * @return the first point in the line that differs from p1, or 0 if none found
     */
    final int get_lpt1(LLine l1, int p1) {
        char j;
        for (j = 0; j <= l1.no; j++) {
            if (l1.pt[j] != p1) return (l1.pt[j]);
        }
        return (0);
    }

    /**
     * Returns the first point from the line that is different from both specified points.
     *
     * @param l1 the line to search
     * @param p1 the first point to be excluded
     * @param p2 the second point to be excluded
     * @return the first point in the line that differs from both p1 and p2, or 0 if none found
     */
    final int get_lpt2(LLine l1, int p1, int p2) {
        char j;
        for (j = 0; j <= l1.no; j++) {
            if (l1.pt[j] != p1 && l1.pt[j] != p2) return (l1.pt[j]);
        }
        return (0);
    }

    /**
     * Returns the default condition for the specified geometric class.
     *
     * @param cc the geometric class object
     * @return a default condition associated with the provided class, or null if unsupported
     */
    final public Cond getDefaultCond(CClass cc) {
        Cond co = new Cond();
        co.pred = 0;

        if (cc instanceof LLine) {
            LLine ln = (LLine) cc;
            co.pred = CO_COLL;
            co.nx = null;
            co.p[0] = ln.pt[0];
            co.p[1] = ln.pt[1];
            co.p[2] = ln.pt[2];
        } else if (cc instanceof PLine) {
            PLine ln = (PLine) cc;
            co.pred = CO_PARA;
            co.nx = null;
            co.p[0] = ln.ln[0].pt[0];
            co.p[1] = ln.ln[0].pt[1];
            co.p[2] = ln.ln[1].pt[0];
            co.p[3] = ln.ln[1].pt[1];
        } else if (cc instanceof TLine) {
            TLine ln = (TLine) cc;
            co.pred = CO_PERP;
            co.nx = null;
            co.p[0] = ln.l1.pt[0];
            co.p[1] = ln.l1.pt[1];
            co.p[2] = ln.l2.pt[0];
            co.p[3] = ln.l2.pt[1];
        } else if (cc instanceof ACir) {
            ACir c = (ACir) cc;
            co.pred = CO_CYCLIC;
            co.nx = null;
            for (int i = 0; i < 4; i++)
                co.p[i + 1] = c.pt[i];
        } else if (cc instanceof CSegs) {
            CSegs cg = (CSegs) cc;
            co.pred = CO_CONG;
            co.p[0] = cg.p1[0];
            co.p[1] = cg.p2[0];
            co.p[2] = cg.p1[1];
            co.p[3] = cg.p2[1];
        } else if (cc instanceof AngSt) {
            AngSt ag = (AngSt) cc;
            co.pred = CO_ACONG;
            co.nx = null;

            LLine l1 = ag.ln1[0];
            LLine l2 = ag.ln2[0];

            int t = inter_lls(l1, l2);
            int p1 = get_lpt1(l1, t);
            int p2 = get_lpt1(l2, t);

            l1 = ag.ln1[1];
            l2 = ag.ln2[1];

            int t1 = inter_lls(l1, l2);
            int p3 = get_lpt1(l1, t1);
            int p4 = get_lpt1(l2, t1);

            co.p[0] = p1;
            co.p[1] = t;
            co.p[2] = t;
            co.p[3] = p2;
            co.p[4] = p3;
            co.p[5] = t1;
            co.p[6] = t1;
            co.p[7] = p4;

        } else if (cc instanceof MidPt) {
            MidPt md = (MidPt) cc;
            co.pred = CO_MIDP;
            co.nx = null;
            co.p[0] = md.m;
            co.p[1] = md.a;
            co.p[2] = md.b;
        } else if (cc instanceof STris) {
            STris sm = (STris) cc;
            //if(sm.dr)
            if (sm.st == 1)
                co.pred = CO_CTRI;
            else
                co.pred = CO_STRI;
            co.nx = null;
            co.p[0] = sm.p1[0];
            co.p[1] = sm.p2[0];
            co.p[2] = sm.p3[0];
            co.p[3] = sm.p1[1];
            co.p[4] = sm.p2[1];
            co.p[5] = sm.p3[1];
        } else if (cc instanceof RatioSeg) {
            RatioSeg seg = (RatioSeg) cc;
            co.pred = CO_PROD;
            co.nx = null;
            for (int i = 0; i < 8; i++)
                co.p[i] = seg.r[i + 1];
        } else if (cc instanceof AngleT) {
            AngleT at = (AngleT) cc;
            co.pred = CO_TANG;
            co.nx = null;
            co.p[0] = at.get_pt1();
            co.p[1] = at.p;
            co.p[2] = at.get_pt2();
            co.p[3] = at.v;
        } else if (cc instanceof CongSeg) {
            CongSeg cg = (CongSeg) cc;
            co.pred = CO_CONG;
            co.p[0] = cg.p1;
            co.p[1] = cg.p2;
            co.p[2] = cg.p3;
            co.p[3] = cg.p4;
            co.p[4] = cg.t1;
            co.p[5] = cg.t2;
        } else
            return null;
        return co;
    }

    //show
    ///////////////////////////////////

    /**
     * Computes the orientation of two triangles defined by their vertices.
     *
     * @param p1 the first point of the first triangle
     * @param p2 the second point of the first triangle
     * @param p3 the third point of the first triangle
     * @param p4 the first point of the second triangle
     * @param p5 the second point of the second triangle
     * @param p6 the third point of the second triangle
     * @return 1 if the triangles share the same orientation, -1 otherwise
     */
    int check_tri_dr(int p1, int p2, int p3, int p4, int p5, int p6) {
        double r1 = (aptx(p2) - aptx(p1)) * (apty(p3) - apty(p1)) -
                (aptx(p3) - aptx(p1)) * (apty(p2) - apty(p1));

        double r2 = (aptx(p5) - aptx(p4)) * (apty(p6) - apty(p4)) -
                (aptx(p6) - aptx(p4)) * (apty(p5) - apty(p4));

        if (r1 * r2 >= 0)
            return 1;
        else
            return -1;
    }

    /**
     * Disables further checking and logs a check error.
     */
    final void add_checkError() {
        ck_value = false;
        gprint("On Check Error!");
    }

    /**
     * Determines whether the three given points are collinear.
     *
     * @param p1 index of the first point
     * @param p2 index of the second point
     * @param p3 index of the third point
     * @return true if the points are collinear; false otherwise
     */
    final boolean check_coll(int p1, int p2, int p3) {
        return Math.abs((apty(p2) - apty(p1)) * (aptx(p3) - aptx(p1)) -
                (aptx(p2) - aptx(p1)) * (apty(p3) - apty(p1))) < ZERO;
    }

    /**
     * Determines whether the four given points are collinear by checking if both
     * the third and fourth points lie on the line through the first two points.
     *
     * @param p1 index of the first point
     * @param p2 index of the second point
     * @param p3 index of the third point
     * @param p4 index of the fourth point
     * @return true if all points are collinear; false otherwise
     */
    final boolean check_coll(int p1, int p2, int p3, int p4) {
        return check_coll(p1, p2, p3) && check_coll(p1, p2, p4);
    }

    /**
     * Checks whether two lines are parallel.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return true if the lines are parallel; false otherwise
     */
    final boolean check_para(LLine l1, LLine l2) {
        return check_para(l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1]);
    }

    /**
     * Checks whether two segments defined by their endpoints are parallel.
     *
     * @param p1 index of the first point of the first segment
     * @param p2 index of the second point of the first segment
     * @param p3 index of the first point of the second segment
     * @param p4 index of the second point of the second segment
     * @return true if the segments are parallel; false otherwise
     */
    final boolean check_para(int p1, int p2, int p3, int p4) {
        return Math.abs((apty(p2) - apty(p1)) * (aptx(p4) - aptx(p3)) -
                (aptx(p2) - aptx(p1)) * (apty(p4) - apty(p3))) < ZERO;
    }

    /**
     * Determines whether two segments defined by their endpoints are perpendicular.
     *
     * @param p1 index of the first point of the first segment
     * @param p2 index of the second point of the first segment
     * @param p3 index of the first point of the second segment
     * @param p4 index of the second point of the second segment
     * @return true if the segments are perpendicular; false otherwise
     */
    final boolean check_perp(int p1, int p2, int p3, int p4) {
        return Math.abs((apty(p2) - apty(p1)) * (apty(p4) - apty(p3)) + (aptx(p4) - aptx(p3)) *
                (aptx(p2) - aptx(p1))) < ZERO;
    }

    /**
     * Checks if the angles formed by points (p1, p2, p3) and (p4, p5, p6) are equal.
     *
     * @param p1 index of the vertex for the first angle
     * @param p2 index of the first arm point for the first angle
     * @param p3 index of the second arm point for the first angle
     * @param p4 index of the vertex for the second angle
     * @param p5 index of the first arm point for the second angle
     * @param p6 index of the second arm point for the second angle
     * @return true if the two angles are equal within a tolerance; false otherwise
     */
    protected boolean check_eqangle(int p1, int p2, int p3, int p4, int p5, int p6) {
        if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0 || p5 == 0 || p6 == 0) {
            Cm.print("null point in eqangle");
            return false;
        }
        return Math.abs(getAngleValue(p1, p2, p2, p3) - getAngleValue(p4, p5, p5, p6)) < ZERO;
    }

    /**
     * Checks whether the sum of the two angles defined by the given points equals 90°.
     *
     * @param p1 index of the vertex for the first angle
     * @param p2 index of an arm point for the first angle
     * @param p3 index of the other arm point for the first angle
     * @param p4 index of the vertex for the second angle
     * @param p5 index of an arm point for the second angle
     * @param p6 index of the other arm point for the second angle
     * @return true if the sum of angles is 90° (within tolerance); false otherwise
     */
    protected boolean check_atn(int p1, int p2, int p3, int p4, int p5, int p6) {
        double r = getAngleValue(p1, p2, p3) + getAngleValue(p4, p5, p6);
        return Math.abs(r - Math.PI / 2) < ZERO || Math.abs(r + Math.PI / 2) < ZERO;
    }

    /**
     * Checks if two angles defined by four points are equal or supplementary.
     *
     * @param p1 index of the first point of the first angle
     * @param p2 index of the second point of the first angle
     * @param p3 index of the third point of the first angle
     * @param p4 index of the fourth point of the first angle
     * @param p5 index of the first point of the second angle
     * @param p6 index of the second point of the second angle
     * @param p7 index of the third point of the second angle
     * @param p8 index of the fourth point of the second angle
     * @return true if the angles are equal or supplementary (within tolerance); false otherwise
     */
    protected boolean check_eqangle(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
        if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0 || p5 == 0 || p6 == 0) {
            Cm.print("null point in eqangle");
            return false;
        }
        double r1 = getAngleValue(p1, p2, p3, p4);
        double r2 = getAngleValue(p5, p6, p7, p8);
        double r = Math.abs(r1 - r2);
        return r < ZERO || Math.abs(r - Math.PI) < ZERO;
    }

    /**
     * Checks if two angle values computed via a transformed approach are equal.
     *
     * @param p1 index of the first point of the first angle
     * @param p2 index of the second point of the first angle
     * @param p3 index of the third point of the first angle
     * @param p4 index of the fourth point of the first angle
     * @param p5 index of the first point of the second angle
     * @param p6 index of the second point of the second angle
     * @param p7 index of the third point of the second angle
     * @param p8 index of the fourth point of the second angle
     * @return true if the absolute transformed angle values are equal (within tolerance); false otherwise
     */
    protected boolean check_eqangle_t(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
        if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0 || p5 == 0 || p6 == 0) {
            Cm.print("Check EQANgle ==0");
            return false;
        }

        double r1 = getAngleValue_t(p1, p2, p3, p4);
        double r2 = getAngleValue_t(p5, p6, p7, p8);
        double r = Math.abs(Math.abs(r1) - Math.abs(r2));
        return r < ZERO;
    }

    /**
     * Determines whether the angle formed by three points is a right angle (90°).
     *
     * @param p1 index of the vertex of the angle
     * @param p2 index of the first arm point
     * @param p3 index of the second arm point
     * @return true if the angle is 90° (within tolerance); false otherwise
     */
    public boolean check_angle_ls_90(int p1, int p2, int p3) {

        double r1 = getAngleValue(p1, p2, p3);
        return Math.abs(Math.abs(r1) - Math.PI / 2) < ZERO;
    }

    /**
     * Determines if a point lies strictly between two other points along both axes.
     *
     * @param p1 index of the point to test
     * @param p2 index of the first boundary point
     * @param p3 index of the second boundary point
     * @return true if p1 is inside the interval defined by p2 and p3; false otherwise
     */
    protected boolean x_inside(int p1, int p2, int p3) {
        double r1 = ((aptx(p1) - aptx(p2)) * (aptx(p1) - aptx(p3)));
        double r2 = ((apty(p1) - apty(p2)) * (apty(p1) - apty(p3)));
        if (r1 < 0 && r2 < 0) return true;
        if (Math.abs(r1) < ZERO && r2 < 0) return true;
        if (Math.abs(r2) < ZERO && r1 < 0) return true;
        return false;
    }

    /**
     * Computes an angle value using transformed points.
     *
     * The method considers special ordering of the points to compute the appropriate angle.
     *
     * @param p1 index of the first point
     * @param p2 index of the second point
     * @param p3 index of the third point
     * @param p4 index of the fourth point
     * @return the computed angle value
     */
    protected double getAngleValue_t(int p1, int p2, int p3, int p4) {
        int p0;
        if (p1 == p4) {
            p0 = p4;
            p4 = p3;
            p3 = p0;
        } else if (p2 == p3) {
            p0 = p1;
            p1 = p2;
            p2 = p0;
        } else if (p2 == p4) {
            p0 = p1;
            p1 = p2;
            p2 = p0;
            p0 = p4;
            p4 = p3;
            p3 = p0;
        }
        if (p1 != p3)
            return getAngleValue(p2, p3, p4);
        else
            return getAngleValue(p1, p2, p3, p4);
    }

    /**
     * Checks if the distance between two pairs of points are equal after scaling.
     *
     * @param p1 index of the first point of the first segment
     * @param p2 index of the second point of the first segment
     * @param p3 index of the first point of the second segment
     * @param p4 index of the second point of the second segment
     * @param t1 scaling factor for the first segment
     * @param t2 scaling factor for the second segment
     * @return true if the scaled distances are equal (within tolerance); false otherwise
     */
    protected boolean check_eqdistance(int p1, int p2, int p3, int p4, double t1, double t2) {
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        double x3 = aptx(p3);
        double y3 = apty(p3);
        double x4 = aptx(p4);
        double y4 = apty(p4);
        double r = Math.abs(Math.pow(x2 - x1, 2) * t2 + Math.pow(y2 - y1, 2) * t2
                - Math.pow(x4 - x3, 2) * t1 - Math.pow(y4 - y3, 2) * t1);

        return Math.abs(r) < ZERO;
    }

    /**
     * Checks if the distances between two pairs of points are equal.
     *
     * @param p1 index of the first point of the first segment
     * @param p2 index of the second point of the first segment
     * @param p3 index of the first point of the second segment
     * @param p4 index of the second point of the second segment
     * @return true if the distances are equal (within tolerance); false otherwise
     */
    protected boolean check_eqdistance(int p1, int p2, int p3, int p4) {
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        double x3 = aptx(p3);
        double y3 = apty(p3);
        double x4 = aptx(p4);
        double y4 = apty(p4);
        return Math.abs(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)
                - Math.pow(x4 - x3, 2) - Math.pow(y4 - y3, 2)) < ZERO;
    }

    /**
     * Validates whether the ratio of the squared lengths of two segments remain equal.
     *
     * @param a index of the first point of the first segment
     * @param b index of the second point of the first segment
     * @param c index of the first point of the second segment
     * @param d index of the second point of the second segment
     * @param p index of the first point of the third segment
     * @param q index of the second point of the third segment
     * @param r index of the first point of the fourth segment
     * @param s index of the second point of the fourth segment
     * @return true if the products of squared lengths are equal (within tolerance); false otherwise
     */
    protected boolean check_ratio(int a, int b, int c, int d, int p, int q, int r, int s) {
        return Math.abs(length2(a, b) * length2(r, s) - length2(c, d) * length2(p, q)) < ZERO;
    }

    /**
     * Checks if two pairs of points are equal regardless of order.
     *
     * @param p1 the first point of the first pair
     * @param p2 the second point of the first pair
     * @param p3 the first point of the second pair
     * @param p4 the second point of the second pair
     * @return true if the pairs (p1, p2) and (p3, p4) are equal in any order, false otherwise
     */
    public boolean ck_4peq(int p1, int p2, int p3, int p4) {
        return p1 == p3 && p2 == p4 || p1 == p4 && p2 == p3;
    }

    /**
     * Evaluates directional compatibility between two segments defined by two pairs of points.
     *
     * @param p1 the first point of the first segment
     * @param p2 the second point of the first segment
     * @param p3 the first point of the second segment
     * @param p4 the second point of the second segment
     * @return true if the segments satisfy specific directional conditions, false otherwise
     */
    public boolean ck_dr(int p1, int p2, int p3, int p4) {
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        double x3 = aptx(p3);
        double y3 = apty(p3);
        double x4 = aptx(p4);
        double y4 = apty(p4);
        double r1 = (y2 - y1) * (y4 - y3);
        double r2 = (x2 - x1) * (x4 - x3);
        return (r1 > 0 && r2 > 0) || (r1 == 0 && r2 > 0) || (r1 > 0 && r2 == 0);
    }

    /**
     * Calculates the squared Euclidean distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the squared distance between p1 and p2
     */
    protected double length2(int p1, int p2) {
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
    }

    /**
     * Computes the angle value at the middle point defined by three points.
     *
     * @param p1 the first point
     * @param p2 the vertex point where the angle is measured
     * @param p3 the third point
     * @return the angle value at p2 (in radians)
     */
    protected double getAngleValue(int p1, int p2, int p3) {
        return getAngleValue(p1, p2, p3, p2);
    }

    /**
     * Determines the angle formed at a common point by two lines.
     *
     * @param p the intersection point of the lines
     * @param l1 the first line
     * @param l2 the second line
     * @return the angle between l1 and l2 at point p (in radians)
     */
    protected double getAngleValue(int p, LLine l1, LLine l2) {
        int a, b;
        if (p == l1.pt[0])
            a = l1.pt[1];
        else
            a = l1.pt[0];
        if (p == l2.pt[0])
            b = l2.pt[1];
        else
            b = l2.pt[0];
        return getAngleValue(a, p, b, p);
    }

    /**
     * Checks if the angle formed by three points is approximately equal to a given value.
     *
     * @param a the first point
     * @param b the vertex point where the angle is measured
     * @param c the third point
     * @param v the target angle value in degrees
     * @return true if the measured angle is approximately equal to v, false otherwise
     */
    protected boolean check_ateq(int a, int b, int c, int v) {
        double r = getAngleValue(a, b, c);
        double x = Math.abs(r * A_180 / Math.PI - v);
        boolean d = x < 0.01 || Math.abs(x - A_180) < ZERO;
        if (!d) {
            int k = 0;
        }
        return d;
    }

    /**
     * Computes the angle difference between the lines defined by two point pairs.
     *
     * @param p1 the first point of the first line
     * @param p2 the second point of the first line
     * @param p3 the first point of the second line
     * @param p4 the second point of the second line
     * @return the difference between the angles (in radians) of the two lines
     */
    protected double getAngleValue(int p1, int p2, int p3, int p4) {
        if (p1 == 0 || p2 == 0 || p3 == 0 || p4 == 0) {
            int k = 0;
            return 0;
        }
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        double x3 = aptx(p3);
        double y3 = apty(p3);
        double x4 = aptx(p4);
        double y4 = apty(p4);


        double dx1 = x1 - x2;
        double dy1 = y1 - y2;
        dy1 = (-1) * dy1;

        double k1 = dy1 / dx1;
        double r1 = Math.atan(k1);
        if (dx1 < 0)
            r1 += Math.PI;

        double t = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        dx1 = dx1 / t;
        dy1 = dy1 / t;

        double dx2 = x3 - x4;
        double dy2 = y3 - y4;

        dy2 = (-1) * dy2;
        double k2 = dy2 / dx2;
        double r2 = Math.atan(k2);

        if (dx2 < 0)
            r2 += Math.PI;

        t = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        dx2 = dx2 / t;
        dy2 = dy2 / t;
        double dr = ((r2 - r1));
        if (dr >= Math.PI)
            dr = dr - Math.PI * 2;
        else if (dr < -Math.PI) dr = dr + Math.PI * 2;
        return dr;
    }

    /**
     * Checks whether the triangles defined by the given points are similar.
     *
     * @param p1 first vertex of the first triangle
     * @param p2 second vertex of the first triangle
     * @param p3 third vertex of the first triangle
     * @param p4 first vertex of the second triangle
     * @param p5 second vertex of the second triangle
     * @param p6 third vertex of the second triangle
     * @return true if the triangles are similar based on their side ratios, false otherwise
     */
    boolean check_simtri(int p1, int p2, int p3, int p4, int p5, int p6) {

        double r1 = getRatio(p1, p2, p4, p5);
        double r2 = getRatio(p1, p3, p4, p6);
        double r3 = getRatio(p2, p3, p5, p6);

        return (Math.abs(r1 - r2) < ZERO) && (Math.abs(r1 - r3) < ZERO);
    }

    /**
     * Checks whether two triangles are congruent by comparing side ratios and one corresponding side length.
     *
     * @param p1 first vertex of the first triangle
     * @param p2 second vertex of the first triangle
     * @param p3 third vertex of the first triangle
     * @param p4 first vertex of the second triangle
     * @param p5 second vertex of the second triangle
     * @param p6 third vertex of the second triangle
     * @return true if the triangles are congruent, false otherwise
     */
    boolean check_congtri(int p1, int p2, int p3, int p4, int p5, int p6) {

        double r1 = getRatio(p1, p2, p4, p5);
        double r2 = getRatio(p1, p3, p4, p6);
        double r3 = getRatio(p2, p3, p5, p6);

        return (Math.abs(r1 - r2) < ZERO) && (Math.abs(r1 - r3) < ZERO) &&
                (Math.abs(length2(p1, p2) - length2(p4, p5)) < ZERO);
    }

    /**
     * Determines if the first point is the midpoint of the segment defined by the other two points.
     *
     * @param p1 the point to test as the midpoint
     * @param p2 the first endpoint of the segment
     * @param p3 the second endpoint of the segment
     * @return true if p1 is the midpoint of p2 and p3, false otherwise
     */
    boolean check_mid(int p1, int p2, int p3) {
        double x1 = aptx(p1);
        double y1 = apty(p1);
        double x2 = aptx(p2);
        double y2 = apty(p2);
        double x3 = aptx(p3);
        double y3 = apty(p3);
        return Math.abs(x2 + x3 - 2 * x1) < ZERO && Math.abs(y2 + y3 - 2 * y1) < ZERO;
    }

    /**
     * Calculates the ratio of the squared distances between two pairs of points.
     *
     * @param p1 the first point of the first segment
     * @param p2 the second point of the first segment
     * @param p3 the first point of the second segment
     * @param p4 the second point of the second segment
     * @return the ratio of the squared distance between (p1, p2) and (p3, p4)
     */
    double getRatio(int p1, int p2, int p3, int p4) {
        double r1 = (Math.pow(aptx(p1) - aptx(p2), 2) + Math.pow(apty(p1) - apty(p2), 2)) /
                (Math.pow(aptx(p3) - aptx(p4), 2) + Math.pow(apty(p3) - apty(p4), 2));
        return r1;
    }

    /**
     * Computes the integer square root of a number.
     * Returns the square root if the number is a perfect square; otherwise, returns -1.
     *
     * @param n the number to compute the square root of
     * @return the integer square root if it exists; -1 otherwise
     */
    int Sqrt(int n) {
        int i = 1;
        while (i * i < n)
            i++;
        if (i * i == n) return i;
        return -1;

    }

    /**
     * Checks if two triangles, defined by three vertex points each, are identical.
     * The comparison is independent of the vertex order.
     *
     * @param p1 vertex of the first triangle
     * @param p2 vertex of the first triangle
     * @param p3 vertex of the first triangle
     * @param q1 vertex of the second triangle
     * @param q2 vertex of the second triangle
     * @param q3 vertex of the second triangle
     * @return true if the triangles are the same; false otherwise
     */
    final public boolean same_tri(int p1, int p2, int p3, int q1, int q2, int q3) {
        if (
                p1 == q1 && (p2 == q2 && p3 == q3 || p2 == q3 && p3 == q2) ||
                        p1 == q2 && (p2 == q1 && p3 == q3 || p2 == q3 && p3 == q1) ||
                        p1 == q3 && (p2 == q1 && p3 == q2 || p2 == q2 && p3 == q1))
            return true;
        return false;
    }

    /**
     * Collects angle expressions into a collection.
     * Processes the global list of angle objects and groups unique angle expressions.
     */
    public void collect_angst() {

        Vector v = new Vector();
        Angles as = all_as.nx;
        Vector vt = new Vector();
        while (as != null) {
            if (as.type != 0 && as.l1 != as.l2 && as.l3 != as.l4) {
                vt.add(as);
            }
            as = as.nx;
        }

        while (vt.size() != 0) {
            Angles a1 = (Angles) vt.get(0);
            addAngst(a1, v);
            vt.remove(0);
            int len = 0;
            do {
                len = vt.size();
                for (int i = 0; i < vt.size(); i++) {
                    Angles a = (Angles) vt.get(i);
                    if (insertAngle(a))
                        vt.remove(i);
                }

            } while (len > vt.size());
        }
    }

    /**
     * Adds an angle expression to a new angle structure.
     * Updates the global angle structure list with the provided angle.
     *
     * @param ag the angle expression to add
     * @param v a vector used for collecting angle structures
     */
    public void addAngst(Angles ag, Vector v) {
        AngSt a = new AngSt();
        a.addAngle(ag);
        last_ast.nx = a;
        last_ast = a;
    }

    /**
     * Attempts to insert an angle expression into the existing angle structure list.
     *
     * @param ag the angle expression to insert
     * @return true if the angle was merged with an existing structure; false otherwise
     */
    public boolean insertAngle(Angles ag) {
        if (ag.l1 == ag.l3 && ag.l2 == ag.l4 || ag.l1 == ag.l2 && ag.l3 == ag.l4)
            return true;

        AngSt ast = all_ast.nx;

        while (ast != null) {
            if (ast.addAngle(ag))
                return true;
            ast = ast.nx;
        }
        return false;
    }

    /**
     * Finds and returns the angle structure that contains the specified lines.
     *
     * @param l1 the first line to search within angle structures
     * @param l2 the second line to search within angle structures
     * @return the angle structure containing the lines, or null if not found
     */
    public AngSt fd_ast(LLine l1, LLine l2) {
        AngSt ast = all_ast.nx;
        while (ast != null) {
            if (ast.contain(l1, l2)) return ast;
            ast = ast.nx;
        }
        return null;
    }

    /**
     * Computes the greatest common divisor (GCD) of two long integers using the Euclidean algorithm.
     *
     * @param l1 the first number
     * @param l2 the second number
     * @return the GCD of the two numbers
     */
    long gcd(long l1, long l2) {
        long l;
        if (l1 < 0L) {
            l1 = -l1;
        }
        if (l2 < 0L) {
            l2 = -l2;
        }
        if (l1 > l2) {
            l = l1;
            l1 = l2;
            l2 = l;
        }
        while (l1 != 0L) {
            l = l2 % l1;
            l2 = l1;
            l1 = l;
        }
        if (l2 == 0) {
            int k = 0;
        }
        return (l2);
    }

    protected Vector vauxpts = new Vector();
    protected Vector vauxptf = new Vector();

    /**
     * Sets the boolean flag at a given index in a shared values array.
     *
     * @param n the 1-based index of the value to set
     * @param v the boolean value to set
     */
    public static void setValue(int n, boolean v) {
        RValue[n - 1] = v;
    }
}
