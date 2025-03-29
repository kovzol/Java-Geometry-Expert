package wprover;

import gprover.CNdg;
import gprover.Cons;
import gprover.GTerm;
import maths.PolyBasic;
import maths.TDono;
import maths.TMono;
import maths.TPoly;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PanelGB extends PanelAlgebraic implements MouseListener {

    private Vector vndgs;
    private boolean prs = false;
    private static long TIME = 1000000;
    private JPopupMenu menu;

    /**
     * Constructs a new PanelGB with the specified DrawProcess and WuTextPane.
     *
     * @param dp    the DrawProcess instance to associate with this panel
     * @param tpane the WuTextPane instance to associate with this panel
     */
    public PanelGB(DrawProcess dp, WuTextPane tpane) {
        super(dp, tpane);
        menu = new JPopupMenu();
        JMenuItem it = new JMenuItem(GExpert.getLanguage("Save as Maple Format"));
        menu.add(it);
        it.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAsMaple();
            }
        });
        tpane.addMouseListener(this);
    }

    /**
     * Stops the running process and updates the status.
     */
    public void stopRunning() {
        running = false;
        PolyBasic.setbbStop(true);
        this.addString("\n");
        this.addString("icon4", "icon4");
        this.addString(GExpert.getLanguage("The Process Is Stopped By The User."));
    }

    /**
     * Initiates the proving process with the given GTerm and DrawProcess.
     *
     * @param tm the GTerm instance representing the term to prove
     * @param dp the DrawProcess instance to use for proving
     */
    public void prove(GTerm tm, DrawProcess dp) {
        if (running)
            return;

        tpane.setText("");
        _mremainder = null;
        gt = tm;
        main = new Thread(this, "GbProver");
        running = true;
        main.start();
        startTimer();
    }

    /**
     * Starts a timer to monitor the running process.
     */
    public void startTimer() {
        if (gxInstance != null) {
            Timer t = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (running) {
                        rund = RunningDialog.startTimer(gxInstance, GExpert.getLanguage("GBasis is Running"));
                        rund.setPanelGB(PanelGB.this);
                    }
                    Timer t = (Timer) e.getSource();
                    t.stop();
                }
            });

            t.start();
        }
    }

    /**
     * Divides the given polynomial by the terms in the specified TPoly.
     *
     * @param m1 the polynomial to divide
     * @param p1 the TPoly containing the terms to divide by
     * @return 0 if the division is successful, 1 if the process is interrupted
     */
    protected int div(TMono m1, TPoly p1) {
        if (poly.pzerop(m1))
            return 0;
        Vector vt = new Vector();

        while (p1 != null) {
            TMono t = p1.poly;
            vt.add(0, t);
            if (t.x == m1.x)
                break;
            p1 = p1.next;
        }

        int index = vt.size();

        long time = System.currentTimeMillis();
        int i = 0;
        while (true) {
            if (i >= vt.size())
                break;

            TMono m = (TMono) vt.get(i++);
            TMono md = poly.pcopy(m);
            m1 = poly.prem(m1, md);
            if (m1 != null && m1.x == 9) {
                int k = 0;
            }
            long t1 = System.currentTimeMillis();
            time = t1;
            if (poly.pzerop(m1))
                return 0;
            if (!running)
                return 1;
        }
        String s = poly.printSPoly(m1);
        addString("Remainder:  " + s);

        return 1;
    }

    /**
     * Retrieves the TMono representation of the specified construction.
     *
     * @param c the construction
     * @return the TMono representation of the construction
     */
    protected TMono getTMono(Cons c) {
        return dp.getTMono(c);
    }

    /**
     * Runs the main process for computing the Groebner basis.
     */
    public void run() {
        if (gt == null) {
            running = false;
            if (rund != null)
                rund.stopTimer();
            return;
        }

        gbasis();

        PolyBasic.setbbStop(false);
        running = false;
        if (rund != null)
            rund.stopTimer();
    }

    /**
     * Prints the terms in the specified vector to the text pane.
     *
     * @param v the vector containing the terms to print
     */
    public void printTP(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (m != null)
                addString(poly.printSPoly(m));
        }
    }

    /**
     * Checks if the Groebner basis computation is finished.
     *
     * @param v the vector containing the terms to check
     * @return true if the computation is finished, false otherwise
     */
    public boolean gb_finished(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            if (poly.plength(m) == 1 && m.x == 0)
                return true;
        }
        return false;
    }

    /**
     * Tests the Groebner basis computation with the specified polynomial vector and index.
     *
     * @param pp the vector containing the polynomial terms
     * @param dx the index value for the computation
     */
    public void test(Vector pp, int dx) {
        int size = pp.size();

        if (size < 2) return;

        int index = size - 3;
        Vector vp = new Vector();
        for (int i = size - 2; i < size; i++)
            vp.add(pp.get(i));

        for (int i = index; i >= 0; i--) {
            addString2(i + "GBASIS");
            printTP(vp);

            vp.add(0, pp.get(i));
            gbasis(vp);
        }

        addString2(-1 + "GBASIS");
        poly.upValueTM(vp, -dx);
        printTP(vp);
    }

    /**
     * Computes the Groebner basis for the specified polynomial vector.
     *
     * @param pp the vector containing the polynomial terms
     */
    public void gbasis(Vector pp) {
        while (true) {
            pp = poly.bb_reduce(pp, 10000);
            if (!isRunning())
                break;

            if (gb_finished(pp))
                break;

            Vector tp = poly.s_polys(pp);

            if (tp.size() != 0) {
                for (int i = 0; i < tp.size(); i++)
                    poly.ppush((TMono) tp.get(i), pp);
            } else {
                break;
            }
        }
    }

    /**
     * Computes a modified Groebner basis (SBasis) for the given polynomial vector and conclusion.
     *
     * <p>This method selects and removes polynomial terms from the vector based on the provided index,
     * computes intermediate deltas and nondegenerate conditions, and applies a series of polynomial
     * updates and reductions. It modifies the input vector with updated terms and returns the reduced
     * conclusion polynomial.</p>
     *
     * @param x  the maximum index value controlling term selection and iterations
     * @param v  the vector of polynomial terms (TMono objects) to be processed and updated
     * @param mc the conclusion polynomial term (TMono) to be reduced
     * @return the reduced conclusion polynomial or {@code null} if the process is interrupted
     */
    public TMono sbasis(int x, Vector v, TMono mc) {

        Vector vg = new Vector();
        if (v.size() == 0) return mc;
        GeoPoly basic = GeoPoly.getPoly();

        int nn = x;
        int dx = nn / 2 + 2;
        TMono m1, m2;
        int param = 0;
        Vector vrs = new Vector();


        for (int n = 1; n < nn / 2 + 1; n++) {
            m1 = m2 = null;

            for (int i = 0; i < v.size(); i++) {
                TMono m = (TMono) v.get(i);
                if (m.x == 2 * n || m.x == 2 * n - 1) {
                    if (m1 == null)
                        m1 = m;
                    else m2 = m;
                }
            }

            if (m1 != null)
                v.remove(m1);
            if (m2 != null)
                v.remove(m2);

            if (m1 != null || m2 != null) {
                TMono t = basic.ll_delta(2 * n, m1, m2);
                if (basic.plength(t) == 1 && t.x == 0 && t.val.intValue() != 0)
                    t = null;
                --param;
                int dd = -param + 3;
                t = basic.n_ndg(t, param);

                vg.clear();

                basic.ppush(m2, vg);
                basic.ppush(m1, vg);
                basic.ppush(t, vg);
//                System.out.println(basic.getAllPrinted(m1));
//                System.out.println(basic.getAllPrinted(m2));

                basic.upValueTM(vg, dd);
//
//                if(false)
//                {
//                    Vector vtp = basic.specialTreatment(m1, m2, param + dd);
//                    if (vtp.size() != 0) {
//                        vg.clear();
//                        for (int i = 0; i < vtp.size(); i++)
//                            basic.ppush((TMono) vtp.get(i), vg);
//                        basic.ppush(t, vg);
//                    }
//                }

                this.gbasis(vg);
                basic.upValueTM(vg, -dd);
                for (int i = 0; i < vg.size(); i++) {
                    TMono tt = (TMono) vg.get(i);
                    basic.ppush(tt, vrs);
                }
            }
        }

        basic.upValueTM(vrs, dx);

        Vector vnds = basic.getcnds(vrs, dx);
        basic.bb_reduce(vrs, 10000, true);
        if (!running)
            return null;

        Vector vnn = new Vector();
        for (int i = 0; i < vnds.size(); i++) {

            TMono m = (TMono) vnds.get(i);
            m = basic.b_reduce(basic.p_copy(m), vrs);
            if (!running)
                return null;
            TDono d = basic.splitDono(m, dx);

            if (d != null)
                vnn.add(d);
        }

        Vector vco = basic.parseCommonDono(vnn, dx);
        for (int i = 0; i < vnds.size(); i++) {
            TMono d = (TMono) vnds.get(i);
            basic.ppush(d, vrs);
        }

        if (vco.size() != 0) {
            for (int i = 0; i < vco.size(); i++) {
                TMono m = (TMono) vco.get(i);
                basic.ppush(m, vrs);
            }

        }

        basic.bb_reduce(vrs, 10000, true);
        mc = basic.b_reduce(mc, vrs);

        while (basic.ctLessdx(mc, dx)) {
            mc = basic.reduceMDono(mc, vnn, dx); // reduced all u parameters.
            mc = basic.b_reduce(mc, vrs);
            if (!running)
                return null;
        }

        TMono mcr = basic.p_copy(mc);
        basic.eraseCommonDono(vnn);


        Vector vnn1 = new Vector();
        for (int i = 0; i < vnn.size(); i++) {
            TDono d = (TDono) vnn.get(i);
            TMono m = basic.p_copy(d.p2);
            m = basic.reduceMDono(m, vnn, dx);
            if (!running)
                return null;
            m = basic.b_reduce(m, vrs);
            if (!running)
                return null;
            vnn1.add(m);
        }

        basic.upValueTM(vrs, -dx);
        basic.upValueDM(vnn, -dx);
        basic.upValueTM(mc, -dx);
        basic.upValueTM(mcr, -dx);
        basic.upValueTM(vnn1, -dx);

        this.printTP(vrs);
//        this.printVectorExpanded(vrs, 0);

        addSVdd(vnn1);
        v.clear();
        v.addAll(vrs);
        return mc;
    }

    /**
     * Adds the non-degenerate conditions to the text pane.
     *
     * @param v the vector containing the non-degenerate conditions
     */
    public void addSVdd(Vector v) {
        GeoPoly basic = GeoPoly.getPoly();

        addString2(GExpert.getLanguage("The Nondegenerate Conditions:"));
        for (int i = 0; i < v.size(); i++) {
            TMono m = (TMono) v.get(i);
            basic.coefgcd(m);
            TMono mf = basic.get_factor1(m);
            if (mf == null) {
                basic.factor1(m);
                String s = basic.printNPoly(m);
                this.addString(s);
            } else {
                TMono ff = mf;
                while (mf != null) {
                    basic.div_factor1(m, mf.x, mf.deg);
                    mf = mf.coef;
                }
                String s = basic.printNPoly(ff, m);
                this.addString(s);
            }
        }
    }

    /**
     * Computes the Groebner basis for the current set of constraints and updates the text pane.
     */
    public void gbasis() {
        GeoPoly basic = GeoPoly.getPoly();
        String sc = gt.getConcText();
        Cons cc = gt.getConclusion();
        TMono mc = getTMono(cc);
        if (mc == null) {
            running = false;
            return;
        }

        addAlgebraicForm();
        addString2(GExpert.getLanguage("The equational hypotheses:"));

        Vector vc = dp.getAllConstraint();
        int n = 1;
        Vector pp = new Vector();

        for (int i = 0; i < vc.size(); i++) {
            Constraint c = (Constraint) vc.get(i);
            if (c.is_poly_genereate) {
                c.PolyGenerate();
                TPoly p1 = Constraint.getPolyListAndSetNull();
                if (p1 != null)
                    addString1(n++ + ": " + c.toString() + "\n");
                while (p1 != null) {
                    TMono m = p1.getPoly();
                    if (m != null) {
                        poly.ppush(m, pp);
                        addString("  " + poly.printSPoly(m));
                    }
                    p1 = p1.next;
                }
            }
        }

        addString2(GExpert.getLanguage("The Initial Polynomial Set:"));
        printTP(pp);

        String s1 = poly.printSPoly(mc);

        addString2(GExpert.getLanguage("The Groebner basis:"));
        Vector v = dp.getPBMono();

        int x = basic.getMaxX(v);
        int dx = x / 2 + 2;
        basic.upValueTM(mc, dx);

        mc = sbasis(x, v, mc);
        if (!running)
            return;
        pp = v;

        String s2 = poly.printSPoly(mc);
        addString2(GExpert.getLanguage("The conclusion:"));
        addString1(sc + "\n");
        addString(s1);
        addString2(GExpert.getLanguage("The conclusion after reduce:"));
        addString(s2);

        if (mc == null) {
            addString("icon1", "icon1");
            addString1(GExpert.getLanguage("The conclusion is true"));
        } else {
            addString("icon2", "icon2");
            addString1(GExpert.getLanguage("The conclusion is false"));
            if (poly.plength(mc) > 2) {
                _mremainder = mc;
                addString("\n");
                addButton();
            }
        }

        running = false;
    }

    /**
     * Handles mouse click events to show the popup menu.
     *
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        menu.show((JComponent) e.getSource(), e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * Saves the current data in Maple format.
     * Opens a file chooser dialog to select the save location.
     * If the file exists, it will be overwritten.
     * If the file does not exist, it will be created.
     */
    public void saveAsMaple() {
        JFileChooser filechooser1 = new JFileChooser();
        String dr = GExpert.getUserDir();
        filechooser1.setCurrentDirectory(new File(dr));
        int result = filechooser1.showDialog(this, GExpert.getLanguage("Save"));
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = filechooser1.getSelectedFile();
            FileOutputStream fp;
            try {
                if (f.exists()) {
                    f.delete();
                    fp = new FileOutputStream(f, true);
                } else {
                    f.createNewFile();
                    fp = new FileOutputStream(f, false);
                }
                if (fp == null) {
                    return;
                }
                writeMaple(fp);
                fp.close();
            } catch (IOException ee) {
                JOptionPane.showMessageDialog(this, ee.getMessage(),
                        "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Writes the current data in Maple format to the specified output stream.
     *
     * @param out the output stream to write the data to
     * @throws IOException if an I/O error occurs
     */
    public void writeMaple(FileOutputStream out) throws IOException {
        GeoPoly basic = GeoPoly.getPoly();
        Cons cc = gt.getConclusion();
        TMono mc = getTMono(cc);

        // Part 1: Order for the variables.
        boolean fr = true;
        out.write("vars := [".getBytes());

        Vector vp = dp.getPointList();
        for (int i = vp.size() - 1; i >= 0; i--) {
            CPoint pt = (CPoint) vp.get(i);
            String s1 = pt.x1.getString();
            String s2 = pt.y1.getString();
            if (GeoPoly.vzero(pt.x1.xindex))
                continue;
            if (GeoPoly.vzero(pt.y1.xindex))
                continue;
            if (fr) {
                fr = false;
            } else out.write(", ".getBytes());
            out.write((s2 + ", " + s1).getBytes());
        }

        Vector v = dp.getPBMono();
        int x = basic.getMaxX(v);

        Vector vg = new Vector();
        int nn = x;
        TMono m1, m2;
        int param = 0;

        for (int n = 1; n < nn / 2 + 1; n++) {
            m1 = m2 = null;

            for (int i = 0; i < v.size(); i++) {
                TMono m = (TMono) v.get(i);
                if (m.x == 2 * n || m.x == 2 * n - 1) {
                    if (m1 == null)
                        m1 = m;
                    else m2 = m;
                }
            }

            if (m1 != null)
                v.remove(m1);
            if (m2 != null)
                v.remove(m2);

            if (m1 != null || m2 != null) {
                TMono t = basic.ll_delta(2 * n, m1, m2);
                if (basic.plength(t) == 1 && t.x == 0 && t.val.intValue() != 0)
                    t = null;
                --param;
                t = basic.n_ndg(t, param);
                if (t != null)
                    out.write((", u" + (-param)).getBytes());

                basic.ppush(m2, vg);
                basic.ppush(m1, vg);
                basic.ppush(t, vg);
            }
        }
        out.write("];".getBytes());

        // Part 2: All polynomials.
        for (int i = 0; i < vg.size(); i++) {
            TMono m = (TMono) vg.get(i);
            out.write("\n".getBytes());
            out.write(("P" + i + " := " + poly.getExpandedPrint(m) + " ;").getBytes());
        }
        String st = poly.getExpandedPrint(mc);
        out.write(("\n C := " + st + " ;").getBytes());
    }
}