/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:33:44
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class TLine extends CClass {
    int lemma;
    Cond co;
    public LLine l1, l2;
    TLine nx;

    public TLine(LLine l1, LLine l2) {
        this.l1 = l1;
        this.l2 = l2;
    }

    public TLine() {
        type = lemma = 0;
        co = null;
        l1 = l2 = null;
        nx = null;
    }
}
