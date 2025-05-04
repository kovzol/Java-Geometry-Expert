package gprover;

import java.util.Vector;

/**
 * The AuxPt class represents an auxiliary point in a geometric construction.
 * It includes methods for managing a list of ProPoint objects and retrieving
 * information about the auxiliary point.
 */
public class AuxPt {
    /** The type of the auxiliary point. */
    int type;

    /** The list of ProPoint objects associated with the auxiliary point. */
    Vector<ProPoint> vptlist = new Vector<>();

    /**
     * Constructs an AuxPt object with the specified type.
     *
     * @param t the type of the auxiliary point
     */
    public AuxPt(int t) {
        type = t;
    }

    /**
     * Gets the constructed point as a string.
     *
     * @return the constructed point as a string
     */
    public String getConstructedPoint() {
        return vptlist.get(0).toString();
    }

    /**
     * Gets the type of the auxiliary point.
     *
     * @return the type of the auxiliary point
     */
    public int getAux() {
        return type;
    }

    /**
     * Adds a ProPoint to the list if it is not already present.
     *
     * @param pt the ProPoint to add
     */
    public void addAPt(ProPoint pt) {
        for (int i = 0; i < vptlist.size(); i++)
            if (pt == vptlist.get(i))
                return;
        vptlist.add(pt);
    }

    /**
     * Gets the number of ProPoints in the list.
     *
     * @return the number of ProPoints in the list
     */
    public int getPtsNo() {
        return vptlist.size();
    }

    /**
     * Gets the ProPoint at the specified index.
     *
     * @param n the index of the ProPoint to retrieve
     * @return the ProPoint at the specified index
     */
    public ProPoint getPtsbyNo(int n) {
        return vptlist.get(n);
    }

    /**
     * Returns a string representation of the auxiliary point and its associated ProPoints.
     *
     * @return a string representation of the auxiliary point and its associated ProPoints
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < vptlist.size(); i++) {
            ProPoint pt = vptlist.get(i);
            s += pt.getText();
        }
        return "(A" + type + " ): " + s;
    }
}
