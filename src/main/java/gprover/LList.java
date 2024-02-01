package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 19, 2006
 * Time: 9:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class LList extends CClass {
    final public static int VALUE = 0;
    final public static int LINE = 1;
    final public static int ANGLE = 2;

    final public static int MAX_MDE = 10;

    boolean solved = false;

    int type = -1;
    public Mnde[] md;
    public Mnde[] mf;
    public int nd, nf;
    int npt, pt;

    public Rule[] rl;
    public LList fr;
    LList nx;


    public LList() {
        md = new Mnde[MAX_MDE];
        mf = new Mnde[MAX_MDE];
        rl = new Rule[MAX_MDE];
        nd = nf = 0;
    }

    public int get_npt() {
        int num = 0;
        int t = 0;

        for (int i = 0; i < nd; i++) {
            int k = 1;
            int n = md[i].tr.v;
            for (int j = i + 1; j < nd; j++) {
                if (n == md[j].tr.v)
                    k++;
            }
            if (k > num) {
                num = k;
                t = n;
            }
        }
        npt = num;
        pt = t;
        return num;
    }

    public void cp(LList ls) {
        type = ls.type;
        nd = ls.nd;
        nf = ls.nf;
        for (int i = 0; i < nd; i++) {
            md[i] = new Mnde();
            md[i].cp(ls.md[i]);
        }
        for (int i = 0; i < nf; i++) {
            mf[i] = new Mnde();
            mf[i].cp(ls.mf[i]);
        }

    }

    public void sub1(AngTr t1, AngTr t2) {
        for (int i = 0; i < nd; i++) {
            if (md[i].tr == t1) {
                md[i].tr = t2;
                return;
            }
        }
    }

    public void add_md(Mnde m) {
        if (m == null) return;

        int i;
        for (i = 0; i < MAX_MDE && md[i] != null; i++) ;
        md[i] = m;
        nd = i + 1;
    }

    public void add_mf(Mnde m) {
        if (m == null) return;
        int i;
        for (i = 0; i < MAX_MDE && mf[i] != null; i++) ;
        mf[i] = m;
        nf = i + 1;

    }

    public void add_rule(Rule r) {
        int i;
        for (i = 0; i < MAX_MDE && rl[i] != null; i++) ;
        rl[i] = r;
    }

    public String toString() {
        return text;
    }

}
