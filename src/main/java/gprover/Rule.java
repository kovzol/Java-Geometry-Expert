package gprover;

/**
 * Represents a rule in a geometric construction.
 */
public class Rule extends CClass {

    /** Constant for split angle type. */
    public final static int SPLIT_ANGLE = 0;

    /** Constant for P angle type. */
    public final static int P_ANGLE = 1;

    /** Constant for T angle type. */
    public final static int T_ANGLE = 2;

    /** Constant for external angle type. */
    public final static int EX_ANGLE = 3;

    /** Constant for equal angle type. */
    public final static int EQ_ANGLE = 4;

    /** The type of the rule. */
    int type;

    /** The number associated with the rule. */
    int no;

    /** Array of Mnde objects associated with the rule. */
    public Mnde[] mr1 = new Mnde[5];

    /** The main Mnde object associated with the rule. */
    public Mnde mr;

    /** Reference to the next Rule object. */
    Rule nx;

    /**
     * Constructs a Rule object with a specified type.
     *
     * @param t the type of the rule
     */
    public Rule(int t) {
        type = t;
        nx = null;
        mr = null;
    }

    /**
     * Copies the properties of another Rule object to this Rule object.
     *
     * @param r the Rule object to copy from
     */
    public void cp_rule(Rule r) {
        type = r.type;
        no = r.no;
        for (int i = 0; i < 5; i++)
            mr1[i] = r.mr1[i];
    }

    /**
     * Returns a string representation of the rule.
     *
     * @return a string representation of the rule
     */
    public String toString() {
        return "   because " + text;
    }
}