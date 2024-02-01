package gprover;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-4-17
 * Time: 13:16:18
 * To change this template use File | Settings | File Templates.
 */
public class GrTerm {

    private int ptn = -1;

    long c1;
    DTerm ps1;
    long c2;
    DTerm ps2;
    int c; /* construction */

    public ElTerm el; /* eliminations */
    public DTerm ps; /* simplifiers */

    public GrTerm nx;


    public String text = "";

    public String toString() {
        return text;
    }

    public DTerm getps1() {
        return ps1;
    }

    public XTerm getds1() {
        if (ps1 != null)
            return ps1.p;
        else return null;
    }

    public void setPTN(int n) {
        ptn = n;
    }

    public int getPTN() {
        return ptn;
    }

    public boolean isZero() {
        if (ps1 == null) {
            if (c1 == 0)
                return true;
            else return false;
        }
        XTerm p = ps1.p;
        if (p.var == null && p.c == 0) return true;
        return false;
    }

    public Vector getAllxterm() {
        Vector v = new Vector();
        if (ps1 != null && ps1.p != null) {
            XTerm x = ps1.p;
            while (x != null) {
                v.add(x);
                DTerm d = x.ps;
                if (d != null)
                    d = d.nx;
                if (d != null)
                    x = d.p;
                else break;
            }
        }
        if (v.size() > 0) {
            XTerm x = (XTerm) v.get(0);
            x.cutMark();
        }
        return v;
    }

    public Vector getAllvars() {
        Vector v = new Vector();
        getPSVar(v, ps1);
        getPSVar(v, ps2);
        return v;
    }

    void getPSVar(Vector v, DTerm d) {
        while (d != null) {
            getPVar(v, d.p);
            d = d.nx;
        }
    }

    void getPVar(Vector v, XTerm x) {
        if (x == null) return;
        if (x.var != null)
            v.add(x.var);
        getPSVar(v, x.ps);
    }


}
