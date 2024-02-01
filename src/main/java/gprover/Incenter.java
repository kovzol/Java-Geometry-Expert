
/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:32:05
 * To change this template use File | Settings | File Templates.
 */
package gprover;
public class Incenter extends CClass
{
   // int type;
    int lemma;
    int co;
    int i, a, b, c;
    Incenter nx;

    public Incenter()
    {
        type = lemma = 0;
        i = a = b = c = 0;
        co = 0;
        nx = null;
    }
}
