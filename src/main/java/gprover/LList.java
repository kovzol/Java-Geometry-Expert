package gprover;

/**
 * Represents a list of geometric elements.
 * This class extends the CClass and includes properties for different types of elements, rules, and methods to manipulate them.
 */
public class LList extends CClass {
    /** Constant value representing a value type. */
    final public static int VALUE = 0;

    /** Constant value representing a line type. */
    final public static int LINE = 1;

    /** Constant value representing an angle type. */
    final public static int ANGLE = 2;

    /** Maximum number of elements. */
    final public static int MAX_MDE = 10;

    /** Indicates if the list is solved. */
    boolean solved = false;

    /** The type of the list. */
    int type = -1;

    /** Array of Mnde objects representing the elements. */
    public Mnde[] md;

    /** Array of Mnde objects representing the elements. */
    public Mnde[] mf;

    /** Number of elements in md. */
    public int nd;

    /** Number of elements in mf. */
    public int nf;

    /** Number of points. */
    int npt;

    /** Point value. */
    int pt;

    /** Array of Rule objects representing the rules. */
    public Rule[] rl;

    /** Reference to the first LList object. */
    public LList fr;

    /** Reference to the next LList object. */
    LList nx;

    /**
     * Constructs an LList object with default values.
     */
    public LList() {
        md = new Mnde[MAX_MDE];
        mf = new Mnde[MAX_MDE];
        rl = new Rule[MAX_MDE];
        nd = nf = 0;
    }

    /**
     * Gets the number of points and updates the npt and pt fields.
     *
     * @return the number of points
     */
    public int get_npt() {
        int num = 0;
        int t = 0;

        for (int i = 0; i < nd; i++) {
            int k = 1;
            int n = md[i].tr.v;
            for (int j = i + 1; j < nd; j++) {
                if (n == md[j].tr.v)
                    k++;
            }
            if (k > num) {
                num = k;
                t = n;
            }
        }
        npt = num;
        pt = t;
        return num;
    }

    /**
     * Copies the properties of another LList object to this one.
     *
     * @param ls the LList object to copy from
     */
    public void cp(LList ls) {
        type = ls.type;
        nd = ls.nd;
        nf = ls.nf;
        for (int i = 0; i < nd; i++) {
            md[i] = new Mnde();
            md[i].cp(ls.md[i]);
        }
        for (int i = 0; i < nf; i++) {
            mf[i] = new Mnde();
            mf[i].cp(ls.mf[i]);
        }
    }

    /**
     * Adds an Mnde object to the md array.
     *
     * @param m the Mnde object to add
     */
    public void add_md(Mnde m) {
        if (m == null) return;

        int i;
        for (i = 0; i < MAX_MDE && md[i] != null; i++) ;
        md[i] = m;
        nd = i + 1;
    }

    /**
     * Adds an Mnde object to the mf array.
     *
     * @param m the Mnde object to add
     */
    public void add_mf(Mnde m) {
        if (m == null) return;
        int i;
        for (i = 0; i < MAX_MDE && mf[i] != null; i++) ;
        mf[i] = m;
        nf = i + 1;
    }

    /**
     * Adds a Rule object to the rl array.
     *
     * @param r the Rule object to add
     */
    public void add_rule(Rule r) {
        int i;
        for (i = 0; i < MAX_MDE && rl[i] != null; i++) ;
        rl[i] = r;
    }

    /**
     * Returns a string representation of the LList object.
     *
     * @return a string representation of the LList object
     */
    public String toString() {
        return text;
    }
}