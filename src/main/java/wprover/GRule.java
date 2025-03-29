package wprover;


/**
 * Represents a rule in GeoGebra with a type, name, head, description, and example string.
 */
public class GRule {
    public int type;
    public int rx;  // 0: GDD, 1: FULL
    public String name;
    public String head;
    public String description;
    public String exstring;

    /**
     * Constructs a new GRule with the specified type, head, description, example string, and rule type.
     *
     * @param t  the type of the rule
     * @param t1 the head of the rule
     * @param t2 the description of the rule
     * @param t3 the example string of the rule
     * @param tx the rule type (0 for GDD, 1 for FULL)
     */
    public GRule(int t, String t1, String t2, String t3, int tx) {
        type = t;
        head = t1;
        description = t2;
        exstring = t3;
        if (t1.contains("#")) {
            String[] s = t1.split("#");
            name = s[1];
        }
        rx = tx;
    }

    /**
     * Checks if the rule is a GDD rule.
     *
     * @return true if the rule is a GDD rule, false otherwise
     */
    public boolean isGDDRule() {
        return rx == 0;
    }

    /**
     * Checks if the rule is a FULL rule.
     *
     * @return true if the rule is a FULL rule, false otherwise
     */
    public boolean isFullRule() {
        return rx == 1;
    }
}
