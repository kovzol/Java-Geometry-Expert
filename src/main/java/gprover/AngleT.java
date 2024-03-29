package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 4, 2006
 * Time: 1:57:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AngleT extends CClass {   // angle with intersection;
    public int lemma;
    public int p;
    public LLine l1;
    public LLine l2;
    public int v;
    Cond co;
    AngleT nx;

    public AngleT() {
        p = 0;
        l1 = l2 = null;
        v = 0;
        nx = null;
    }

    public AngleT(int p, LLine l1, LLine l2, int v) {
        this();
        this.p = p;
        this.l1 = l1;
        this.l2 = l2;
        this.v = v;
    }


    public int get_pt1() {
        if (l1.pt[0] == p)
            return l1.pt[1];
        else
            return l1.pt[0];
    }

    public int get_pt2() {
        if (l2.pt[0] == p)
            return l2.pt[1];
        else
            return l2.pt[0];
    }

    public int get_val(int p1, int p2) {
        if (l1.on_ln(p1) && l2.on_ln(p2)) return v;
        if (l1.on_ln(p2) && l2.on_ln(p1)) return -v;
        return 9999;                // shall never happen.
    }
}
