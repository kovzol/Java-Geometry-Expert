package gprover;
/**
 * Constructs a SimTri object with default values.
 */
public class STris extends CClass {
    static int MAX_TRI = 300;
    public int st = -1;
    public int no;
    public int[] dr;
    public int[] p1;
    public int[] p2;
    public int[] p3;

    STris nx;

    /**
     * Constructs an STris object with default values.
     */
    public STris() {
        type = 0;
        dr = new int[300];
        p1 = new int[300];
        p2 = new int[300];
        p3 = new int[300];
        nx = null;
    }
}
