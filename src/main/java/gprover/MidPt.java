
/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:31:33
 * To change this template use File | Settings | File Templates.
 */
package gprover;
public class MidPt extends CClass
{
//    int type;
    int lemma;
    Cond co;
    public int m, a, b;
    MidPt nx;
    public MidPt()
    {
        type = lemma = m = a = b = 0;
        co = null;
        nx = null;
    }
}
