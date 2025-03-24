package gprover;

/**
 * The AngSt class represents a geometric configuration of angles.
 * It extends the CClass and includes properties for lines, dependencies,
 * and other attributes related to angles.
 */
public class AngSt extends CClass {
    /** The number of angles. */
    public int no;

    /** The first set of lines that define the angles. */
    public LLine[] ln1;

    /** The second set of lines that define the angles. */
    public LLine[] ln2;

    /** The dependencies associated with the angles. */
    long[] dep;

    /** A string representation of the angles. */
    public String sd;

    /** The next AngSt object in a linked list structure. */
    public AngSt nx;

    /**
     * Constructs an AngSt object with default values.
     */
    public AngSt() {
        type = 1;
        no = 0;
        ln1 = new LLine[1000];
        ln2 = new LLine[1000];
        dep = new long[1000];
    }

    /**
     * Constructs an AngSt object with the specified number of lines.
     *
     * @param n the number of lines
     */
    public AngSt(int n) {
        no = 0;
        ln1 = new LLine[n];
        ln2 = new LLine[n];
        dep = new long[n];
    }

    /**
     * Checks if the specified lines are contained in the angles.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return true if the lines are contained in the angles, false otherwise
     */
    public boolean contain(LLine l1, LLine l2) {
        for (int i = 0; i < no; i++) {
            if (ln1[i] == l1 && ln2[i] == l2 || ln1[i] == l2 && ln2[i] == l1)
                return true;
        }
        return false;
    }

    /**
     * Gets the direction of the specified lines.
     *
     * @param l1 the first line
     * @param l2 the second line
     * @return 1 if the lines are in the same direction, -1 if they are in opposite directions, 0 otherwise
     */
    public int get_dr(LLine l1, LLine l2) {
        for (int i = 0; i < no; i++) {
            if (ln1[i] == l1 && ln2[i] == l2)
                return 1;
            if (ln1[i] == l2 && ln2[i] == l1)
                return -1;
        }
        return 0;
    }

    /**
     * Adds an angle to the angles.
     *
     * @param as the Angles object to add
     * @return true if the angle was added, false otherwise
     */
    public boolean addAngle(Angles as) {
        boolean r1, r2;
        LLine l1 = as.l1;
        LLine l2 = as.l2;
        LLine l3 = as.l3;
        LLine l4 = as.l4;

        r1 = r2 = false;
        for (int i = 0; i < no; i++) {
            if (ln1[i] == l1 && ln2[i] == l2)
                r1 = true;
            else if (ln1[i] == l2 && ln2[i] == l1) {
                r1 = true;
                LLine t = l3;
                l3 = l4;
                l4 = t;
            }

            if (ln1[i] == l3 && ln2[i] == l4)
                r2 = true;
            else if (ln1[i] == l4 && ln2[i] == l3) {
                LLine t = l1;
                l1 = l2;
                l2 = t;
                r2 = true;
            }
            if (r1 && r2) break;
        }
        if (r1 && r2) return true;
        if (r1) {
            ln1[no] = l3;
            ln2[no] = l4;
            dep[no] = as.dep;
            no++;

        } else if (r2) {
            ln1[no] = l1;
            ln2[no] = l2;
            dep[no] = as.dep;
            no++;
        } else if (no == 0) {
            ln1[no] = l1;
            ln2[no] = l2;
            dep[no] = as.dep;
            no++;
            ln1[no] = l3;
            ln2[no] = l4;
            dep[no] = as.dep;
            no++;

        } else
            return false;
        return true;
    }

    /**
     * Returns a string representation of the angles.
     *
     * @return a string representation of the angles
     */
    @Override
    public String toString() {
        return sd;
    }
}