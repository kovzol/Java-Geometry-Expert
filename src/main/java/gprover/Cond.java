/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:29:38
 * To change this template use File | Settings | File Templates.
 */
package gprover;

import wprover.GExpert;

import java.util.Vector;

/**
 * Represents a condition in the proof structure.
 * It contains details like rule, predicate, step number, description,
 * proof structure and related attributes.
 */
public class Cond {
    /** Maximum number of geometric elements. */
    final public static int MAX_GEO = 16;
    /** The rule being used in this step (theorems or lemmas). */
    protected int rule = 0;
    /** Predicate identifier. */
    public int pred;
    /** The number of the current step. */
    public int no;
    /** Array of geometric elements. */
    public int[] p;
    /** Structure holding different geometric entities. */
    public UStruct u;
    /** Next condition. */
    public Cond nx;
    /** Additional condition. */
    public Cond cd;
    /** Step description. */
    public String sd = null;
    /** List of the direct steps in this node. */
    public Vector vlist = null;
    /** Depth of the condition, initialized from Gib.depth. */
    public long dep = Gib.depth;

    /**
     * Retrieves the step description.
     *
     * @return the step description text
     */
    public String getText() {
        return sd;
    }

    /**
     * Sets the step description.
     *
     * @param s the new description text
     */
    public void setText(String s) {
        sd = s;
    }

    /**
     * Retrieves the current step number.
     *
     * @return the step number
     */
    public int getNo() {
        return no;
    }

    /**
     * Retrieves the current rule.
     *
     * @return the rule applied
     */
    public int getRule() {
        return rule;
    }

    /**
     * Sets the rule from the related facts in the internal structure.
     */
    public void getRuleFromeFacts() {
        rule = u.get_lemma();
    }

    /**
     * Sets the current rule.
     *
     * @param r the rule to be set
     */
    public void setRule(int r) {
        rule = r;
    }

    /**
     * Retrieves the condition from the internal structure.
     *
     * @return the condition pointer (PCO)
     */
    public Cond getPCO() {
        return u.get_co();
    }

    /**
     * Returns the step description as the string representation.
     *
     * @return the step description
     */
    public String toString() {
        return sd;
    }

    /**
     * Prepends the step description with a language specific "To Prove:" message.
     */
    public void setCondToBeProveHead() {
        sd = GExpert.getLanguage("To Prove:") + " " + sd;
    }

    /**
     * Constructs a condition with a given predicate.
     *
     * @param t the predicate
     */
    public Cond(int t) {
        this();
        pred = t;
    }

    /**
     * Default condition constructor that initializes required fields.
     */
    public Cond() {
        pred = no = 0;
        p = new int[MAX_GEO];
        u = new UStruct();
        nx = null;
    }

    /**
     * Constructs a condition optionally initializing the geometric array.
     *
     * @param r if true, initializes the geometric array; otherwise leaves it null
     */
    public Cond(boolean r) {
        pred = no = 0;
        if (r)
            p = new int[MAX_GEO];
        else
            p = null;
        u = new UStruct();
        nx = null;
    }

    /**
     * Copy constructor for creating a duplicate of a given condition.
     *
     * @param co the condition to copy
     */
    public Cond(Cond co) {
        pred = co.pred;
        no = co.no;
        p = new int[MAX_GEO];
        for (int i = 0; i < MAX_GEO; i++)
            p[i] = co.p[i];
        u = new UStruct();
        u.cpv(co.u);
        nx = null;
        sd = null;
    }

    /**
     * Adds a condition as a direct child.
     *
     * @param co the condition to add
     */
    public void addcond(Cond co) {
        rule = 0;
        if (co == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.add(co);
    }

    /**
     * Adds a condition with a specified rule.
     *
     * @param r  the rule value to set
     * @param co the condition to add
     */
    public void addcond(int r, Cond co) {
        rule = r;
        if (co == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.add(co);
    }

    /**
     * Adds all conditions from a given vector.
     *
     * @param v the vector containing conditions to add
     */
    public void add_allco(Vector v) {
        if (v == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.addAll(v);
    }

    /**
     * Adds two conditions as direct children with a specified rule.
     *
     * @param lm  the rule value to set
     * @param co1 the first condition to add
     * @param co2 the second condition to add
     */
    public void addcond(int lm, Cond co1, Cond co2) {
        rule = lm;
        addcond(co1, co2);
    }

    /**
     * Adds two conditions as direct children.
     *
     * @param co1 the first condition to add
     * @param co2 the second condition to add
     */
    public void addcond(Cond co1, Cond co2) {
        rule = 0;
        if (vlist == null)
            vlist = new Vector();
        if (co1 != null) vlist.add(co1);
        if (co2 != null) vlist.add(co2);
    }

    /**
     * Retrieves the attributes associated with the condition.
     *
     * @return a CClass representing the condition's attributes
     */
    public CClass get_attr() {
        return u.get_attr();
    }

    /**
     * Determines the type of the condition's conclusion.
     *
     * @return 1 if the internal structure is null; 2 if the condition obtained from the structure is null; otherwise 0
     */
    public int get_conc_type() {
        if (u.isnull())   // Hype
            return 1;
        else if (u.get_co() == null) // Obviously
            return 2;
        else
            return 0;
    }

    /**
     * Dummy method for rule processing. Implementation pending.
     */
    public void gRule() {
    }
}

/**
 * Represents a structure holding various geometric elements.
 * This class contains fields for different types of geometric data and provides
 * methods for comparing, copying, and retrieving attributes from the stored elements.
 */
class UStruct {
    MidPt md;
    LLine ln;
    PLine pn;
    TLine tn;
    ACir cr;
    Angles as;
    AngleT at;
    AngTn atn;
    SimTri st;
    CongSeg cg;
    RatioSeg ra;
    Polygon pg;
    LList ns;

    /**
     * Compares this UStruct to another UStruct for equality.
     * Equality is defined as all corresponding fields referring to the same object.
     *
     * @param u1 the UStruct to compare with
     * @return true if all fields are equal; false otherwise
     */
    public boolean equal(UStruct u1) {
        return
                md == u1.md
                        && ln == u1.ln
                        && pn == u1.pn
                        && tn == u1.tn
                        && cr == u1.cr
                        && as == u1.as
                        && st == u1.st
                        && cg == u1.cg
                        && ra == u1.ra
                        && at == u1.at
                        && pg == u1.pg
                        && atn == u1.atn
                        && ns == u1.ns;
    }

    /**
     * Checks if all the fields in this UStruct are null.
     *
     * @return true if every field is null; false otherwise
     */
    public boolean isnull() {
        return
                md == null
                        && ln == null
                        && pn == null
                        && tn == null
                        && cr == null
                        && as == null
                        && st == null
                        && cg == null
                        && ra == null
                        && at == null
                        && pg == null
                        && atn == null
                        && ns == null;
    }

    /**
     * Sets all fields in this UStruct to null.
     */
    public void setnull() {
        md = null;
        ln = null;
        pn = null;
        tn = null;
        cr = null;
        as = null;
        st = null;
        cg = null;
        ra = null;
        at = null;
        pg = null;
        atn = null;
        ns = null;
    }

    /**
     * Copies all field values from the specified UStruct into this UStruct.
     *
     * @param us the UStruct to copy from
     */
    public void cpv(UStruct us) {
        md = us.md;
        pn = us.pn;
        ln = us.ln;
        tn = us.tn;
        cr = us.cr;
        as = us.as;
        st = us.st;
        cg = us.cg;
        ra = us.ra;
        at = us.at;
        pg = us.pg;
        atn = us.atn;
        ns = us.ns;
    }

    /**
     * Constructs an empty UStruct with all fields initialized to null.
     */
    public UStruct() {
        md = null;
        ln = null;
        pn = null;
        tn = null;
        cr = null;
        as = null;
        st = null;
        cg = null;
        ra = null;
        at = null;
        pg = null;
        atn = null;
        ns = null;
    }

    /**
     * Retrieves the type of the first non-null geometric element.
     *
     * @return the type if found; -1 if no element is present
     */
    public int get_type() {
        if (md != null) return md.type;
        if (ln != null) return ln.type;
        if (pn != null) return pn.type;
        if (tn != null) return tn.type;
        if (cr != null) return cr.type;
        if (as != null) return as.type;
        if (st != null) return st.type;
        if (cg != null) return cg.type;
        if (ra != null) return ra.type;
        if (at != null) return at.type;
        if (pg != null) return pg.type;
        if (atn != null) return atn.type;
        if (ns != null) return ns.type;
        return -1;
    }

    /**
     * Retrieves the lemma value from the first non-null geometric element.
     *
     * @return the lemma if found; -1 if no element is present
     */
    public int get_lemma() {
        if (md != null) return md.lemma;
        if (ln != null) return ln.lemma;
        if (pn != null) return pn.lemma;
        if (tn != null) return tn.lemma;
        if (cr != null) return cr.lemma;
        if (as != null) return as.lemma;
        if (st != null) return st.lemma;
        if (cg != null) return cg.lemma;
        if (ra != null) return ra.lemma;
        if (at != null) return at.lemma;
        if (pg != null) return pg.lemma;
        if (atn != null) return atn.lemma;
        return -1;
    }

    /**
     * Retrieves the attributes from the first non-null geometric element.
     *
     * @return the attributes as a CClass; null if no element is found
     */
    public CClass get_attr() {
        if (md != null) return md;
        if (ln != null) return ln;
        if (pn != null) return pn;
        if (tn != null) return tn;
        if (cr != null) return cr;
        if (as != null) return as;
        if (st != null) return st;
        if (cg != null) return cg;
        if (ra != null) return ra;
        if (at != null) return at;
        if (pg != null) return pg;
        if (atn != null) return atn;
        return null;
    }

    /**
     * Retrieves the condition (Cond) associated with the first non-null geometric element.
     *
     * @return the associated Cond; null if no element is found
     */
    Cond get_co() {
        if (md != null) return md.co;
        if (ln != null) return ln.co;
        if (pn != null) return pn.co;
        if (tn != null) return tn.co;
        if (cr != null) return cr.co;
        if (as != null) return as.co;
        if (st != null) return st.co;
        if (cg != null) return cg.co;
        if (ra != null) return ra.co;
        if (at != null) return at.co;
        if (pg != null) return pg.co;
        if (atn != null) return atn.co;
        return null;
    }

    /**
     * Retrieves the step number from the first non-null geometric element that contains it.
     *
     * @return the step number if found; -1 otherwise
     */
    public int get_no() {
        //if (d != null) return d.no;
        //if (md != null) return md.no;
        if (ln != null) return ln.no;
        if (pn != null) return pn.no;
        //if (tn != null) return tn.no;
        if (cr != null) return cr.no;
        //if (as != null) return as.no;
        //if (st != null) return st.no;
        //if (cg != null) return cg.no;
        //if (ra != null) return ra.no;
        return -1;
    }

    /**
     * Sets the type of the first non-null geometric element to the specified value.
     *
     * @param t the new type value
     * @return 0 after setting the type
     */
    public int set_type(int t) {
        if (md != null)
            md.type = t;
        else if (ln != null)
            ln.type = t;
        else if (pn != null)
            pn.type = t;
        else if (tn != null)
            tn.type = t;
        else if (cr != null)
            cr.type = t;
        else if (as != null)
            as.type = t;
        else if (st != null)
            st.type = t;
        else if (cg != null)
            cg.type = t;
        else if (ra != null)
            ra.type = t;
        else if (at != null)
            at.type = t;
        else if (pg != null)
            pg.type = t;
        else if (atn != null)
            atn.type = t;
        return 0;
    }
}

