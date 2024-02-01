package wprover;

import gprover.Gib;
import gprover.Rules;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: 2007-5-9
 * Time: 15:28:50
 * To change this template use File | Settings | File Templates.
 */
public class RuleList {
    //  final private static boolean SAVEAGAIN = true;


    private RuleList() {

    }




    final public static Vector GDDLIST = new Vector();
    final public static Vector FULLLIST = new Vector();

    final public static Vector getAllGDDRules() {
        Vector v = new Vector();
        v.addAll(GDDLIST);
        return v;
    }

    final public static Vector getAllFullRules() {
        Vector v = new Vector();
        v.addAll(FULLLIST);
        return v;
    }

    final public static GRule getGrule(int n) {
        n--;
        if (n < 0 || n > GDDLIST.size())
            return null;
        return (GRule) GDDLIST.get(n);
    }

    final public static GRule getFrule(int n) {
        n--;
        if (n < 0 || n > FULLLIST.size())
            return null;
        return (GRule) FULLLIST.get(n);
    }



    final private static void loadRules(String[] src, Vector vs, int type) {


        String s, s1, s2;
        s = s1 = s2 = null;

        int i = 0;
        int len = src.length;

        String t = src[i]; //reader.readLine().trim();

        int id = 1;

        while (t != null) {
            t = t.trim();

            if (t.length() != 0) {
                if (s != null && t.startsWith("*")) {
                    GRule r = new GRule(id++, s, s1, s2, type);
                    vs.add(r);
                    s = t;
                    s1 = s2 = null;
                } else {
                    if (s == null)
                        s = t;
                    else if (s1 == null)
                        s1 = t;
                    else s2 = t;
                }

            }
            if (i >= len - 1)
                break;

            t = src[++i];
        }
    }

    final public static void loadRules() {
     loadRules(Rules.GDD_English, GDDLIST, 0);
     loadRules(Rules.FULL_English, FULLLIST, 1);
    }

    final public static boolean getValue(int n) {
        return Gib.RValue[n - 1];
    }

    final public static void setValue(int n, boolean v) {
        Gib.RValue[n - 1] = v;
    }

}
