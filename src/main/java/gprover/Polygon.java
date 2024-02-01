package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 6, 2006
 * Time: 10:22:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Polygon extends CClass {
    int lemma;
    int qtype;
    Cond co;
    int p[] = new int[10];
    int o;

    Polygon nx;

    public Polygon() {
        lemma = o = 0;
        co = null;
    }
    public Polygon(int t) {
        qtype = t;
        lemma = o = 0;
        co = null;
    }
}
