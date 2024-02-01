/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:33:19
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class PLine extends CClass {
    int lemma;
    Cond co;
    public int no;
    public LLine[] ln;

    PLine nx;

    public PLine(LLine l1, LLine l2) {
        this();
        ln[0] = l1;
        ln[1] = l2;
        no = 1;
    }

    public PLine() {
        type = lemma = no = 0;
        co = null;
        ln = new LLine[MAX_GEO];
        nx = null;
    }
}
