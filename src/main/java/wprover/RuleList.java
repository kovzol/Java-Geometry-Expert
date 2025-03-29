package wprover;

import gprover.Gib;
import gprover.Rules;

import java.util.Vector;

/**
 * RuleList is a class that manages a list of rules for GDD and FULL types.
 * It provides methods to load rules, retrieve rules by index, and manage rule values.
 */
public class RuleList {

    private RuleList() {

    }

    final public static Vector GDDLIST = new Vector();
    final public static Vector FULLLIST = new Vector();

    /**
     * Retrieves all GDD rules.
     *
     * @return a Vector containing all GDD rules
     */
    public static Vector getAllGDDRules() {
        Vector v = new Vector();
        v.addAll(GDDLIST);
        return v;
    }

    /**
     * Retrieves all FULL rules.
     *
     * @return a Vector containing all FULL rules
     */
    public static Vector getAllFullRules() {
        Vector v = new Vector();
        v.addAll(FULLLIST);
        return v;
    }

    /**
     * Retrieves a GDD rule by its index.
     *
     * @param n the index of the GDD rule to retrieve
     * @return the GDD rule at the specified index, or null if the index is out of bounds
     */
    public static GRule getGrule(int n) {
        n--;
        if (n < 0 || n > GDDLIST.size())
            return null;
        return (GRule) GDDLIST.get(n);
    }

    /**
     * Retrieves a FULL rule by its index.
     *
     * @param n the index of the FULL rule to retrieve
     * @return the FULL rule at the specified index, or null if the index is out of bounds
     */
    public static GRule getFrule(int n) {
        n--;
        if (n < 0 || n > FULLLIST.size())
            return null;
        return (GRule) FULLLIST.get(n);
    }

    /**
     * Loads rules from the specified source array into the given vector.
     *
     * @param src  the source array containing the rules
     * @param vs   the vector to load the rules into
     * @param type the type of rules to load
     */
    private static void loadRules(String[] src, Vector vs, int type) {
        String s, s1, s2;
        s = s1 = s2 = null;

        int i = 0;
        int len = src.length;

        String t = src[i];

        int id = 1;

        while (t != null) {
            t = t.trim();

            if (t.length() != 0) {
                if (s != null && t.startsWith("*")) {
                    GRule r = new GRule(id++, s, s1, s2, type);
                    vs.add(r);
                    s = t;
                    s1 = s2 = null;
                } else {
                    if (s == null)
                        s = t;
                    else if (s1 == null)
                        s1 = t;
                    else s2 = t;
                }
            }
            if (i >= len - 1)
                break;

            t = src[++i];
        }
    }

    /**
     * Loads all GDD and FULL rules.
     */
    public static void loadRules() {
        loadRules(Rules.GDD_English, GDDLIST, 0);
        loadRules(Rules.FULL_English, FULLLIST, 1);
    }

    /**
     * Retrieves the value of a rule by its index.
     *
     * @param n the index of the rule
     * @return the value of the rule at the specified index
     */
    public static boolean getValue(int n) {
        return Gib.RValue[n - 1];
    }

    /**
     * Sets the value of a rule by its index.
     *
     * @param n the index of the rule
     * @param v the value to set for the rule
     */
    public static void setValue(int n, boolean v) {
        Gib.RValue[n - 1] = v;
    }
}
