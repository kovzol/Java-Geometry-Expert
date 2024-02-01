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

public class Cond {
    final public static int MAX_GEO = 16;
    protected int rule = 0; // the rule being used in this step (theorems or lemmas)
    public int pred;
    public int no; // the number of the current step
    public int[] p;
    public UStruct u;
    public Cond nx, cd; // nx: next
    public String sd = null; // step description
    public Vector vlist = null; // the list of the direct steps in this node
    public long dep = Gib.depth;

    public String getText() {
        return sd;
    }

    public void setText(String s) {
        sd = s;
    }

    public int getNo() {
        return no;
    }

    public int getRule() {
        return rule;
    }

    public void getRuleFromeFacts() {
        rule = u.get_lemma();
    }

    public void setRule(int r) {
        rule = r;
    }

    public Cond getPCO() {
        return u.get_co();
    }

    public String toString() {
        return sd;
    }

    public void setCondToBeProveHead() {
        sd = GExpert.getLanguage("To Prove:") + " " + sd;
    }

    public Cond(int t) {
        this();
        pred = t;
    }

    public Cond() {
        pred = no = 0;
        p = new int[MAX_GEO];
        u = new UStruct();
        nx = null;
    }

    public Cond(boolean r) {
        pred = no = 0;
        if (r)
            p = new int[MAX_GEO];
        else
            p = null;

        u = new UStruct();
        nx = null;
    }

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

    public void addcond(Cond co) {
        rule = 0;
        if (co == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.add(co);
    }

    public void addcond(int r, Cond co) {
        rule = r;
        if (co == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.add(co);
    }

    public void add_allco(Vector v) {
        if (v == null) return;
        if (vlist == null)
            vlist = new Vector();
        vlist.addAll(v);

    }

    public void addcond(int lm, Cond co1, Cond co2) {
        rule = lm;
        addcond(co1, co2);
    }

    public void addcond(Cond co1, Cond co2) {
        rule = 0;
        if (vlist == null)
            vlist = new Vector();
        if (co1 != null) vlist.add(co1);
        if (co2 != null) vlist.add(co2);
    }

    public CClass get_attr() {
        return u.get_attr();
    }

    public int get_conc_type() {
        if (u.isnull())   // Hype
            return 1;
        else if (u.get_co() == null) // Obviousely
            return 2;
        else
            return 0;
    }

    public void gRule() {
    }
}


