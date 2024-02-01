/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:35:50
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class RatioSeg extends CClass {
    //   int type;
    int lemma;
    Cond co;
    public int[] r;
    RatioSeg nx;

    public RatioSeg() {
        type = lemma = 0;
        co = null;
        r = new int[MAX_GEO];
        nx = null;
    }

    public void cp_ratio(RatioSeg ra) {
        lemma = ra.lemma;
        for (int i = 0; i <= 8; i++)
            r[i] = ra.r[i];
    }


}
