package wprover;

import gprover.*;

import javax.swing.*;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;

import maths.TMono;
import maths.TPoly;

/**
 * PanelWu is a class that extends PanelAlgebraic and implements Runnable and
 * MouseListener interfaces. It provides functionality for proving theorems
 * using Wu's method and displaying the results in a text pane.
 */
public class PanelWu extends PanelAlgebraic implements Runnable, MouseListener {
    private TMono mremainder;


    /**
     * Constructs a new PanelWu with the specified DrawProcess and WuTextPane.
     *
     * @param dp    the DrawProcess instance to associate with this panel
     * @param tpane the WuTextPane instance to associate with this panel
     */
    public PanelWu(DrawProcess dp, WuTextPane tpane) {
        super(dp, tpane);
        tpane.addMouseListener(this);
    }

    /**
     * Sets the language for the panel.
     *
     * @param lan the Language instance to set
     */
    public void setLanguage(Language lan) {
        this.lan = lan;
    }

    /**
     * Stops the running process if it is currently running.
     */
    public void stopRunning() {
        if (!running)
            return;
    }

    /**
     * Initiates the proving process with the given GTerm.
     *
     * @param tm the GTerm instance representing the term to prove
     */
    public void prove(GTerm tm) {
        if (running)
            return;

        tpane.setText("");
        gt = tm;
        main = new Thread(this, "wuProver");
        running = true;
        main.start();
    }

    /**
     * Adds a division step to the text pane.
     *
     * @param index the index of the division step
     * @param m1    the TMono instance representing the polynomial
     * @param x     the variable index
     * @param t     the time taken for the division step
     */
    protected void addDiv(int index, TMono m1, int x, long t) {
        index++;
        String s = "R_" + (index - 1) + " = prem(R_" + index + ", h_" + (index - 1) + ") =  ["
                + poly.printHead(m1) + ", " + poly.plength(m1) + "]";
        addString(s);
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
        addString("R_" + index + " = [" + poly.printHead(m1) + ", " + poly.plength(m1) + "]");
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
            index--;
            addDiv(index, m1, m.x, t1 - time);
            time = t1;
            if (poly.pzerop(m1)) {
                addString(GExpert.getLanguage("Remainder") + " = R_" + (index) + " = 0");
                return 0;
            }
            if (!running)
                return 1;
        }
        String s = poly.printMaxstrPoly(m1);
        if (m1 != null)
            s += " != 0";

        addString(GExpert.getLanguage("Remainder") + " = " + s);
        mremainder = m1;

        return 1;
    }

    /**
     * Retrieves the TMono representation of the specified construction.
     *
     * @param c the construction
     * @return the TMono representation of the construction
     */
    public TMono getTMono(Cons c) {
        TMono m = dp.getTMono(c);

        return m;
    }

    /**
     * Runs the main process for computing the Groebner basis.
     */
    public void run() {
        if (gt == null) {
            running = false;
            return;
        }

        String sc = gt.getConcText();
        Cons cc = gt.getConclusion();
        TMono mc = getTMono(cc);
        if (mc == null) {
            running = false;
            return;
        }

        addAlgebraicForm();
        addString2(GExpert.getLanguage("The equational hypotheses:"));

        TPoly pp = null;
        Vector vc = dp.getAllConstraint();
        int n = 1;
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
                        addString("  " + poly.printSPoly(m));
                        pp = poly.ppush(m, pp);
                    }
                    p1 = p1.next;
                }
            }
        }

        addString2(GExpert.getLanguage("The Triangulized Hypotheses:"));
        TPoly p1 = dp.getPolyList();
        int i = 0;
        while (p1 != null) {
            addString("h" + i++ + ": " + poly.printSPoly(p1.poly));
            p1 = p1.next;
        }

        addString2(GExpert.getLanguage("The conclusion:"));
        addString1(sc + "\n");
        addString(poly.printSPoly(mc));

        addString2(GExpert.getLanguage("Successive Pseudo Remainder of the conclusion wrt Triangulized Hypotheses:"));
        int r = 0;

        try {
            if (mc != null) {
                r = div(mc, dp.getPolyList());
            }
        } catch (final java.lang.OutOfMemoryError e) {
            running = false;
            JOptionPane.showMessageDialog(PanelWu.this, GExpert.getLanguage("System Run Out Of Memory") + "\n" +
                    GExpert.getLanguage("The Theorem Is Not Proved!"), GExpert.getLanguage("System Run Out of Memory"), JOptionPane.WARNING_MESSAGE);
            addString("\n" + GExpert.getLanguage("System Run Out of Memory"));
            addString("icon3", "icon3");
            addString1(GExpert.getLanguage("The conclusion is not proved"));
            return;
        }
        scrollToEnd();

        if (r == 0) {
            addString("icon1", "icon1");
            addString1(GExpert.getLanguage("The conclusion is true"));
        } else if (r == 1) {
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
        if (e.getClickCount() > 1) {
            if (mremainder != null) {
                String s = poly.getExpandedPrint(mremainder);
                JDialog dlg = new JDialog((Frame) null, "Remainder");
                dlg.setSize(300, 200);
                JTextPane p = new JTextPane();
                p.setText(s);
                dlg.getContentPane().add(new JScrollPane(p));
                dlg.setLocation(200, 200);
                dlg.setVisible(true);
            }
        }
    }


    public void mousePressed(MouseEvent e) {
    }


    public void mouseReleased(MouseEvent e) {
    }


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }
}

