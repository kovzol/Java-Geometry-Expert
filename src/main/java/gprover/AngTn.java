package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 16, 2006
 * Time: 2:21:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class AngTn extends CClass {
    int lemma;
    public LLine ln1, ln2, ln3, ln4;
    public int t1, t2;
    Cond co;
    AngTn nx;

    public AngTn(LLine l1, LLine l2, LLine l3, LLine l4) {
        this();
        ln1 = l1;
        ln2 = l2;
        ln3 = l3;
        ln4 = l4;
    }

    public AngTn() {
        ln1 = ln2 = ln3 = ln4 = null;
        co = null;
        nx = null;
        t1 = t2 = lemma = 0;

    }
}
