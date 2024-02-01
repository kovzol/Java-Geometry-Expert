/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-14
 * Time: 21:35:25
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class SimTri extends CClass {
    int lemma;
    Cond co;
    int st;

    public int dr;
    public int[] p1;
    public int[] p2;

    SimTri nx;

    public SimTri() {
        type = lemma = 0;
        co = null;
        dr = 0;
        p1 = new int[3];
        p2 = new int[3];
        nx = null;
    }
}
