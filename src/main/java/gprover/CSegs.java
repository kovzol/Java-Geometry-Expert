package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Oct 23, 2006
 * Time: 5:17:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSegs extends CClass {
    public int no;
    public int[] p1;
    public int[] p2;
    CSegs nx;

    public CSegs() {
        type = no = 0;
        p1 = new int[MAX_GEO * 2];
        p2 = new int[MAX_GEO * 2];
        nx = null;
    }
}
