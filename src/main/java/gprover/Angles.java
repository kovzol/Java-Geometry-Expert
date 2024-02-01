
/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:34:29
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class Angles extends CClass
{
   // int type;
    int lemma;
    Cond co;
    int sa;
    public LLine l1,l2, l3, l4;
    Angles nx;
    int atp = 0;

    public Angles(LLine l1, LLine l2, LLine l3, LLine l4)
    {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
    }
    public Angles()
    {
        type = lemma = sa = 0;
        co = null;
        nx = null;
        l1 = l2 = l3 = l4 = null;
    }

}
