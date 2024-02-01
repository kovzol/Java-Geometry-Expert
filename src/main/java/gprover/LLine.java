/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:32:37
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class LLine extends CClass {

    int lemma;
    public Cond co;
    public int no;
    public int[] pt;
    LLine nx;

    public LLine() {
        type = lemma = no = 0;
        co = null;
        pt = new int[MAX_GEO];
        nx = null;
    }


    public void cp_ln(LLine l1) {
        lemma = l1.lemma;
        co = null;
        no = l1.no;
        for (int i = 0; i <= l1.no; i++)
            pt[i] = l1.pt[i];
        nx = null;
    }

    public boolean containPt(int n) {
        if (n == 0) return false;
        for (int i = 0; i < MAX_GEO; i++)
            if (pt[i] == n) return true;
        return false;
    }

    public static int inter_lls(LLine l1, LLine l2) {
        if (l1 == null || l2 == null || l1 == l2) return (0);
        if (l1 == l2) return 0;

        for (int i = 0; i <= l1.no; i++)
            for (int j = 0; j <= l2.no; j++) {
                if (l1.pt[i] == l2.pt[j]) return (l1.pt[i]);
            }
        return (0);
    }

    public static int get_lpt1(LLine l1, int p1) {
        for (int j = 0; j <= l1.no; j++) {
            if (l1.pt[j] != p1) return (l1.pt[j]);
        }
        return (0);
    }

    final public boolean on_ln(int p) {
        for (int i = 0; i <= no; i++)
            if (pt[i] == p)
                return true;
        return false;
    }
}
