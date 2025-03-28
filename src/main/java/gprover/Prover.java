package gprover;

import java.util.Vector;

/**
 * The Prover class provides static methods for performing geometric proofs and computations.
 * It manages the state of geometric terms and interacts with the geometric database.
 *
 * <p>This class is not instantiable.</p>
 */
public class Prover {
    private static GDDBc db = null;
    private static Full dbfull = null;
    private static GTerm gt = null;
    private static boolean aux = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private Prover() {
    }

    /**
     * Sets the global geometric term.
     *
     * @param g the geometric term to set
     */
    public static void set_gterm(GTerm g) {
        gt = g;
    }

    /**
     * Resets the internal database and state.
     */
    public static void reset() {
        db = null;
        dbfull = null;
        gt = null;
        aux = false;
    }

    /**
     * Executes a fixpoint computation on the current geometric term.
     */
    public static void run() {
        fixpoint(gt);

    }

    /**
     * Retrieves the name of the point corresponding to the specified identifier.
     *
     * @param t the point identifier
     * @return the point name, or "null" if the database is not initialized
     */
    public static String get_pt_name(int t) {
        if (db == null)
            return "null";
        return db.ANAME(t);
    }

    /**
     * Retrieves the identifier of the point corresponding to the specified name.
     *
     * @param s the point name
     * @return the point identifier, or 0 if the database is not initialized
     */
    public static int get_pt_id(String s) {
        if (db == null)
            return 0;
        return db.SPT(s);
    }

    /**
     * Retrieves the total number of properties in the database.
     *
     * @return the number of properties, or 0 if the database is not initialized
     */
    public static int getNumberofProperties() {
        if (db == null)
            return 0;
        return db.getNumberofProperties();
    }

    /**
     * Initializes the database and performs a fixpoint computation using the provided geometric term.
     *
     * @param gt the geometric term used for the computation
     * @return true if the fixpoint computation is successful, false otherwise
     */
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

    /**
     * Attempts to prove the current geometric term.
     *
     * @return true if the proof is successful, false otherwise
     */
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

    /**
     * Retrieves the constructed auxiliary point if one was created during the proof.
     *
     * @return the auxiliary point if available, or null otherwise
     */
    public static AuxPt getConstructedAuxPoint() {
        if (!aux) return null;
        return db.axptc;
    }

    /**
     * Attempts to prove the provided geometric condition.
     *
     * @param co the condition to prove
     * @return true if the condition is successfully proved, false otherwise
     */
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

    /**
     * Retrieves the current geometric database.
     *
     * @return the current GDDBc instance
     */
    public static GDDBc get_gddbase() {
        return db;
    }

    /**
     * Sets the global database to the full geometric base.
     */
    public static void setGIB() {
        db = dbfull;
    }

    /**
     * Returns the head of the proof condition.
     *
     * @return the current proof condition head as a Cond object, or null if the database is not initialized.
     */
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

    /**
     * Retrieves the next proof head in the given list.
     *
     * @param ls the current list of proof heads
     * @return the next LList proof head from the database
     */
    public static LList getProveHead_ls(LList ls) {
        return db.get_next_ls_prove_head(ls);
    }

    /**
     * Searches for a fact matching the provided criteria.
     *
     * @param t the type of fact to search for
     * @param s1 the first search parameter
     * @param s2 the second search parameter
     * @param s3 the third search parameter
     * @return a Vector of matching facts
     */
    public static Vector search_a_fact(int t, String s1, String s2, String s3) {
        return db.search_a_fact(t, s1, s2, s3);
    }

    /**
     * Retrieves and displays the full conclusion from the geometric term.
     *
     * @return the full conclusion as a Cond object
     */
    public static Cond getFullconc() {
        Cond co = gt.getConc();
        dbfull.show_pred(co);
        return co;
    }

    /**
     * Proves the full angle expression for the specified geometric term.
     *
     * @param gt the geometric term to prove
     * @return the GrTerm representing the full angle proof head, or null if proof fails
     */
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

    /**
     * Retrieves all nondiscussed geometric statements and stores them in the provided vectors.
     *
     * @param gt the geometric term to process
     * @param v1 vector to store the first group of statements
     * @param v2 vector to store the second group of statements
     * @param v3 vector to store the third group of statements
     * @param v4 vector to store the fourth group of statements
     * @return true if the nondiscussed statements were successfully retrieved; false otherwise
     */
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

    /**
     * Returns the result of the full angle proof.
     *
     * @return 0 if the proof is successful, 1 if it fails, or 2 if it cannot be expressed as a full angle expression
     */
    public static int getPFullResult() {
        if (dbfull.isProvedTrue()) {
            return 0; // true
        } else if (dbfull.canExpressedAsFullAngle())
            return 1;  //failed.
        else return 2; // can not be expressed as full angle expression.
    }

    /**
     * Retrieves the error type code from the full angle proof.
     *
     * @return an integer representing the error type
     */
    public static int getErrorType() {
        return dbfull.getErrorType();
    }

    /**
     * Displays the condition text for the given proof condition.
     *
     * @param co the condition to be displayed
     */
    public static void showCondTextF(Cond co) {
        dbfull.show_pred(co);
    }
}
