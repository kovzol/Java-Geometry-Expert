package gprover;

/**
 * Represents a node in a geometric construction tree.
 */
public class NdgCs {
    /** The parent node. */
    NdgCs parent = null;

    /** The geometric condition associated with this node. */
    CNdg nd = null;

    /** The type of the node (0 for normal, 1 for exists). */
    int ntype = 0;

    /** Indicates if this node is a leaf. */
    boolean leaf = false;

    /** Indicates if this node is valid. */
    boolean valid = true;

    /** The index of the last constraint added. */
    int no = -1;

    /** The child nodes of this node. */
    NdgCs[] child = new NdgCs[3];

    /** The constraints associated with this node. */
    Cons[] allcns = new Cons[100];

    /**
     * Adds a constraint to this node.
     *
     * @param c the constraint to add
     */
    public void add(Cons c) {
        if (c != null) {
            allcns[++no] = c;
        }
    }

    /**
     * Gets the maximum constraint index.
     *
     * @return the maximum constraint index
     */
    public int getMaxCnsInt() {
        int n = 0;
        for (int i = 0; i <= no; i++) {
            if (allcns[i] == null)
                continue;
            int k = allcns[i].getLastPt();
            if (k > n)
                n = k;
        }
        return n;
    }

    /**
     * Adds a constraint at a specific index.
     *
     * @param i the index
     * @param c the constraint to add
     */
    public void add(int i, Cons c) {
        if (c != null) {
            allcns[i] = c;
        }
    }

    /**
     * Gets the number of non-null constraints.
     *
     * @return the number of non-null constraints
     */
    public int getNotNullNum() {
        int k = 0;
        for (int i = 0; i <= no; i++) {
            if (allcns[i] != null)
                k++;
        }
        return k;
    }

    /**
     * Adds a child node to this node.
     *
     * @param c the child node to add
     */
    public void addChild(NdgCs c) {
        for (int i = 0; i < child.length; i++) {
            if (child[i] == null) {
                child[i] = c;
                break;
            }
        }
    }

    /**
     * Replaces a point in all constraints.
     *
     * @param m the point to replace
     * @param n the new point
     */
    public void replace(int m, int n) {
        for (int i = 0; i <= no; i++) {
            Cons c1 = allcns[i];
            if (c1 != null)
                c1.replace(m, n);
        }
    }

    /**
     * Compares two constraints.
     *
     * @param c1 the first constraint
     * @param c2 the second constraint
     * @return a negative integer, zero, or a positive integer as the first constraint is less than, equal to, or greater than the second
     */
    public static int compare(Cons c1, Cons c2) {
        int n1 = c1.getLastPt();
        int n2 = c2.getLastPt();
        if (n1 == n2) {
            if (c1.type > c2.type)
                return 1;
            if (c1.type < c2.type)
                return -1;
            return compare1(c1, c2, n1);
        }
        if (n1 > n2)
            return 1;
        return 0;
    }

    /**
     * Helper method to compare two constraints with equal types.
     *
     * @param c1 the first constraint
     * @param c2 the second constraint
     * @param n the point index to compare
     * @return a negative integer, zero, or a positive integer as the first constraint is less than, equal to, or greater than the second
     */
    private static int compare1(Cons c1, Cons c2, int n) {
        while (n > 0) {
            int n1 = c1.getLessPt(n);
            int n2 = c2.getLessPt(n);
            if (n1 == n2) {
                n = n1;
            } else if (n1 > n2)
                return 1;
            else return -1;
        }
        return 0;
    }

    /**
     * Reduces the constraints by replacing points.
     */
    public void reduce() {
        if (nd == null)
            return;
        int a = nd.p[0];
        int b = nd.p[1];
        if (nd.type == Gib.NDG_NEQ || nd.type == Gib.NDG_NON_ISOTROPIC) {
            for (int i = 0; i <= no; i++) {
                Cons c = allcns[i];
                if (c == null)
                    continue;
                c.replace(b, a);   // replace b with a.
                c.reorder();
            }
        }
    }

    /**
     * Constructs an empty NdgCs object.
     */
    public NdgCs() {
    }

    /**
     * Constructs a NdgCs object by copying another NdgCs object.
     *
     * @param c the NdgCs object to copy
     */
    public NdgCs(NdgCs c) {
        parent = c.parent;
        nd = c.nd;
        no = c.no;
        for (int i = 0; i <= no; i++) {
            if (c.allcns[i] != null)
                allcns[i] = new Cons(c.allcns[i]);
        }
    }

    /**
     * Gets the index of the last non-null child.
     *
     * @return the index of the last non-null child
     */
    public int getCSindex() {
        int a = -1;
        for (int d = 0; d < child.length; d++) {
            if (child[d] != null)
                a = d;
        }
        return a;
    }
}