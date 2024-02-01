package gprover;

import java.util.Vector;


public class Prover {
    private static GDDBc db = null;
    private static Full dbfull = null;
    private static GTerm gt = null;
    private static boolean aux = false;

    private Prover() {
    }

    public static void set_gterm(GTerm g) {
        gt = g;
    }

    public static void reset() {
        db = null;
        dbfull = null;
        gt = null;
        aux = false;
    }

    public static void run() {
        fixpoint(gt);

    }

    public static String get_pt_name(int t) {
        if (db == null)
            return "null";
        return db.ANAME(t);
    }

    public static int get_pt_id(String s) {
        if (db == null)
            return 0;
        return db.SPT(s);
    }

    public static int getNumberofProperties() {
        if (db == null)
            return 0;
        return db.getNumberofProperties();
    }

    public static boolean fixpoint(GTerm gt) {
        try {
            db = new GDDBc();
            db.init_dbase();
            db.setPrintToString();
            db.setExample(gt);
            db.sbase();
            db.fixpoint();
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean prove() {
        try {
            aux = false;
            db = new GDDBc();
            db.init_dbase();

            db.setExample(gt);
            db.sbase();
            db.setNoPrint();

            int n = db.pts_no;
            db.prove_fix();
            int n1 = db.pts_no;
            if (n1 > n)
                aux = true;
            if (db.docc()) {
                db.show_fproof();
                if (db.all_nd.nx != null)
                    return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return false;
        }
    }

    public static AuxPt getConstructedAuxPoint() {
        if (!aux) return null;
        return db.axptc;
    }

    public static boolean prove(Cond co) {
        try {
            if (db == null)
                db = new GDDBc();

            db.setConc(co);
            if (db.docc()) {
                db.setNoPrint();
                db.show_fproof();
                return true;
            } else
                return false;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return false;
        }
    }

    public static boolean isfixed() {
        return db != null;
    }

    public static GDDBc get_gddbase() {
        return db;
    }

    public static void setGIB() {
        db = dbfull;
    }


    public static Cond getProveHead() {
        try {
            if (db != null) {
                db.show_allpred();
                Cond co = db.all_nd.nx;
                return co;
            } else
                return null;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return null;
        }

    }

    public static LList getProveHead_ls(LList ls) {
        return db.get_next_ls_prove_head(ls);
    }

    public static Vector search_a_fact(int t, String s1, String s2, String s3) {
        return db.search_a_fact(t, s1, s2, s3);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Cond getFullconc() {
        Cond co = gt.getConc();
        dbfull.show_pred(co);
        return co;
    }

    public static GrTerm proveFull(GTerm gt) {

        try {
            Prover.gt = gt;
            dbfull = new Full();
            dbfull.init_dbase();
            dbfull.setExample(gt);
            dbfull.sbase();
            dbfull.setNoPrint();

            dbfull.prove_full();
            if (dbfull.print_prooftext()) {
                GrTerm gr = dbfull.getFullAngleProofHead();
                return gr;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return null;
        }
    }

    public static boolean getAllNdgs(GTerm gt, Vector v1, Vector v2, Vector v3, Vector v4) {
        try {
            Prover.gt = gt;
            if (dbfull == null)
                dbfull = new Full();
            dbfull.init_dbase();
            dbfull.setExample(gt);
            dbfull.sbase();
            dbfull.setNoPrint();
            dbfull.get_ndgs(v1, v2, v3, v4);
            return true;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return false;
        }
    }

    public static boolean isFullAngleProvedTrue() {
        return dbfull.isProvedTrue();
    }

    public static int getPFullResult() {
        if (dbfull.isProvedTrue()) {
            return 0; // true
        } else if (dbfull.canExpressedAsFullAngle())
            return 1;  //failed.
        else return 2; // can not be expressed as full angle expression.
    }

    public static int getErrorType() {
        return dbfull.getErrorType();
    }

    public static void showFullPred(Cond co) {
        try {
            Cond c = new Cond(co);
            dbfull.conc = c;
            if (dbfull.docc())
                dbfull.show_fproof();
            dbfull.print_prooftext();
            return;
        } catch (Exception e) {
            System.out.println("Exception on gprover.Prover: \n" + e.getMessage());
            return;
        }
    }

    public static int getAngleNum() {
        if (dbfull == null) return 0;
        return dbfull.getvarNum();
    }

    public static int getLnNum() {
        if (dbfull == null) return 0;
        return dbfull.getlnNum();
    }

    public static void showCondTextF(Cond co) {
        dbfull.show_pred(co);
    }

    public static void showCondText(Cond co) {
        if (db != null)
            db.show_pred(co);
    }

    public static GrTerm proveArea(Vector v) {
        return null;
    }

    /////////////////////////////////////////////////////
    /// angle deduction.

}
