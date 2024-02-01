package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 17, 2006
 * Time: 11:06:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class AngTr extends CClass {
    public int v, t1, t2;
    public LLine l1;
    public LLine l2;
    Cond co;
    AngTr nx;

    public AngTr() {
        l1 = l2 = null;
        co = null;
        nx = null;
        v = 0;
    }


    public int get_lpt1() {
        if (t1 != 0) return t1;
        return LLine.get_lpt1(l1, v);
    }

    public int get_lpt2() {
        if (t2 != 0) return t2;
        return LLine.get_lpt1(l2, v);
    }
}
