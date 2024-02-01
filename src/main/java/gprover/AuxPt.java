package gprover;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Oct 4, 2006
 * Time: 7:13:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuxPt {
    String name;
    int type;
    Vector vptlist = new Vector();
    String str;

    public AuxPt(int t) {
        type = t;
    }

    public String getConstructedPoint() {
        return vptlist.get(0).toString();
    }

    public int getAux() {
        return type;
    }

    public void addAPt(ProPoint pt) {
        for (int i = 0; i < vptlist.size(); i++)
            if (pt == vptlist.get(i))
                return;
        vptlist.add(pt);
    }

    public int getPtsNo() {
        return vptlist.size();
    }

    public ProPoint getPtsbyNo(int n) {
        return (ProPoint) vptlist.get(n);
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < vptlist.size(); i++) {
            ProPoint pt = (ProPoint) vptlist.get(i);
            s += pt.getText();
        }
        return "(A" + type + " ): " + s;
    }
}
