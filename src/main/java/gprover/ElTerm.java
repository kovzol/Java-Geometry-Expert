package gprover;

import java.util.Vector;

/**
 * The ElTerm class represents an element term in the geometric theorem
 * proving framework. It encapsulates the term type, its associated variable,
 * geometric expressions (XTerm instances), conditions, and linked terms.
 */
public class ElTerm {
    public int etype = 0;
    public Var v;
    public XTerm p1, p2, p;
    public int np = 1;
    public Cond co;
    public ElTerm nx;
    public String text = "";

    public ElTerm et;

    /**
             * Constructs an ElTerm object with default values.
             */
            public ElTerm() {
                int k = 0;
            }

            /**
             * Sets the text description of the element term.
             *
             * @param s the text description to set
             */
            public void setText(String s) {
                text = s;
            }

            /**
             * Returns the string representation of the element term.
             *
             * @return the string representation of the element term
             */
            public String toString() {
                return text;
            }

            /**
             * Gets all XTerm objects associated with the element term.
             *
             * @return a vector of all XTerm objects
             */
            public Vector getAllxterm() {
                Vector v = new Vector();
                v.add(p);

                XTerm x = p1;
                while (x != null) {
                    v.add(x);
                    DTerm d = x.ps;
                    if (d != null)
                        d = d.nx;
                    if (d != null)
                        x = d.p;
                    else
                        break;
                }

                if (v.size() > 0) {
                    x = (XTerm) v.get(0);
                    x.cutMark();
                }
                return v;
            }

            /**
             * Gets the type of the element term.
             *
             * @return the type of the element term
             */
            public int getEType() {
                return etype;
            }

            /**
             * Gets all Cond objects associated with the element term.
             *
             * @return a vector of all Cond objects
             */
            public Vector getAllCond() {
                Vector v = new Vector();
                if (co != null) {
                    Cond c = co;
                    while (c != null) {
                        v.add(c);
                        c = c.nx;
                    }
                }
                if (et != null) {
                    ElTerm e = et;
                    while (e != null) {
                        if (e.co != null) {
                            Cond c = e.co;
                            while (c != null) {
                                v.add(c);
                                c = c.nx;
                            }
                        }
                        e = e.nx;
                    }
                }
                return v;
            }
}
