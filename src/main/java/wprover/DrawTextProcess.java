package wprover;

import gprover.*;
import maths.Param;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * DrawTextProcess is a class that handles the construction and animation of geometric diagrams
 * based on textual input. It extends the DrawProcess class and provides methods for managing
 * construction steps, point attributes, and geometric constraints.
 */
public class DrawTextProcess extends DrawProcess {

    private Timer cons_timer;
    double PX, PY;

    /**
     * Checks if the construction process is currently active.
     *
     * @return true if the construction process is active, false otherwise
     */
    public boolean inConstruction() {
        if (gt == null)
            return false;

        int n = gt.getCons_no();
        return nd > 1 && nd < n && n > 0;
    }

    /**
     * Checks if the construction process is finished.
     *
     * @return true if the construction process is finished, false otherwise
     */
    public boolean isConsFinished() {
        if (gt == null)
            return true;

        int n = gt.getCons_no();
        return nd > n;
    }

    /**
     * Sets the construction lines for the given geometric term.
     *
     * @param g the geometric term
     */
    public void setConstructLines(GTerm g) {
        cleardText();
        this.clearAll();
        this.gt = g;
        nd = 1;
    }

    /**
     * Clears the construction text.
     */
    public void cleardText() {
        gt = null;
        nd = 1;
    }

    /**
     * Animates a diagram from the given string.
     *
     * @param s the string representing the animation
     * @return true if the animation was successfully loaded, false otherwise
     */
    public boolean animateDiagramFromString(String s) {
        AnimateC an = new AnimateC();
        if (false == an.loadAnimationString(s, this)) {
            gxInstance.anButton.setEnabled(false);
            return false;
        } else {
            if (gxInstance != null) {
                gxInstance.anButton.setEnabled(true);
            }
        }

        animate = an;
        this.autoAnimate();
        return true;
    }

    /**
     * Automatically constructs the diagram from the given geometric term.
     *
     * @param g the geometric term
     */
    public void autoConstruct(GTerm g) {

        cleardText();
        this.clearAll();
        this.gt = g;

        this.SetCurrentAction(CONSTRUCT_FROM_TEXT);

        if (cons_timer != null)
            cons_timer.stop();
        int n = 0;
        if (gt.isPositionSet())
            n = 0;
        else {
            Object[] options = {"Text with a diagram",
                    "Auto Construction Step By Step",
                    "Text only"};
            n = JOptionPane.showOptionDialog(gxInstance,
                    "Please choose construct type you want\n",
                    "Constrct type",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
        }

        if (n == 0) {
            while (true) {
                this.mouseDown(0, 0, true);
                this.reCalculate();
                if (isConsFinished())
                    break;
            }
        } else if (n == 1) {

            cons_timer = new Timer(2000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!DrawTextProcess.this.isConsFinished()) {
                        int index = nd;
                        ProPoint pt = gterm().getProPoint(index);
                        if (pt != null) {
                            DrawTextProcess.this.mouseDown(pt.getX(), pt.getY());
                        }
                    } else {
                        ((Timer) e.getSource()).stop();
                        DrawTextProcess.this.mouseDown(0, 0); // for conclusion.
                    }
                    panel.repaint();
                }
            });
            cons_timer.start();
        }
    }

    /**
     * Writes the positions of points to the given output stream.
     *
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void writePointPosition(FileOutputStream out) throws IOException {
        int n = pointlist.size();
        for (int i = 0; i < n; i++) {
            CPoint p = (CPoint) pointlist.get(i);
            out.write(p.toString().getBytes());
            int x = (int) p.getx();
            int y = (int) p.gety();
            CText ct = p.getPText();
            Point p1 = ct.getLocation();
            if (p1 == null) {
                out.write(("(" + x + "," + y + ")  ").getBytes());
            } else {
                out.write(("(" + x).getBytes());
                int tx = p1.x;
                if (tx >= 0) {
                    out.write("+".getBytes());
                }
                out.write((tx + "," +
                        y).getBytes());
                int ty = p1.y;
                if (ty >= 0) {
                    out.write("+".getBytes());
                }
                out.write((ty + ")  ").getBytes());
            }
            if (i != n - 1 && i > 0 && i % 4 == 0) {
                out.write("\n".getBytes());
            }
        }
        if (animate != null) {
            String s = animate.getAnimationString();
            if (s != null) {
                out.write(s.getBytes());
            }
        }
    }

    /**
     * Adds an auxiliary point to the construction.
     *
     * @param ax the auxiliary point to add
     */
    public void addAuxPoint(AuxPt ax) {
        if (this.isConsFinished()) {
            int act = gxInstance.dp.CurrentAction;

            DrawData.setAuxStatus();
            gxInstance.dp.SetCurrentAction(DrawProcess.CONSTRUCT_FROM_TEXT);

            nd = gterm().getCons_no() + 1;

            int no = ax.getPtsNo();
            for (int i = 0; i < no; i++) {
                ProPoint pt = ax.getPtsbyNo(i);
                GTerm gt = gterm();
                gt.addauxedPoint(pt);
                Cons cs = new Cons(pt.type);
                for (int k = 0; k < pt.ps.length && pt.ps[k] != 0; k++)
                    cs.add_pt(pt.ps[k]);
                gt.addauxedCons(cs);

                int n = pointlist.size();
                int nn = nd;
                while (nn == nd)
                    this.pointAdded(cs, cs.type, cs.ps, true, nd, pt.getX(), pt.getY(), null, cs.pss);
                if (pointlist.size() > n) {
                    CPoint c = this.getLastConstructedPoint();
                    setPointAttrAsConstructed(c);
                }
            }
            panel.repaint();
            gxInstance.dp.SetCurrentAction(act);
        }

    }

    /**
     * Sets the attributes of a point as constructed.
     *
     * @param pt the point to set attributes for
     */
    public void setPointAttrAsConstructed(CPoint pt) {
        pt.setRadius(6);
        pt.setColor(8);
    }

    /**
     * Adds a free point to the construction.
     *
     * @param c the construction object
     * @return true if a free point was added, false otherwise
     */
    public boolean addFreePt(Cons c) {
        int[] pp = c.ps;
        for (int i = 0; i < pp.length && pp[i] != 0; i++) {
            if (isFreePoint(pp[i])) {
                if (null != addPt(pp[i])) return true;
            }
        }
        return false;
    }

    /**
     * Adds a geometric term point to the construction.
     *
     * @param c the construction object
     * @return true if a geometric term point was added, false otherwise
     */
    public boolean addGTPt(Cons c) {
        int[] pp = c.ps;
        int i = 0;

        for (; i < pp.length && pp[i] != 0; i++) {
            if (null != addPt2(pp[i]))
                break;
        }
        i = 0;
        for (; i < pp.length && pp[i] != 0; i++) {
            if (findPoint(gt.getPtName(pp[i])) == null)
                return true;
        }
        return false;
    }

    /**
     * Adds a point to the construction by its index.
     *
     * @param n the index of the point
     * @return the added point
     */
    public CPoint addPt2(int n) {
        int x = gt.getPointsNum();
        for (int i = 1; i <= x && i < n; i++) {
            ProPoint pt = gt.getProPoint(i);
            if (findPoint(pt.getName()) == null)
                return addPt(i);
        }
        return addPt(n);
    }

    /**
     * Adds a point to a line in the construction.
     *
     * @param p1 the index of the point to add
     * @param p2 the index of the first point of the line
     * @param p3 the index of the second point of the line
     */
    public void addPt2Line(int p1, int p2, int p3) {
        CLine ln = fd_line(p2, p3);
        if (ln == null)
            ln = addLn(p2, p3);
        if (ln == null)
            return;
        CPoint pt = getPt(p1);
        ln.addApoint(pt);
        Constraint cs = new Constraint(Constraint.PONLINE, pt, ln, false);
        this.addConstraintToList(cs);
    }

    /**
     * Handles mouse down events at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void mouseDown(double x, double y) {
        mouseDown(x, y, false);
    }

    /**
     * Handles mouse down events at the specified coordinates with an option to clear constraints.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @param cc a flag indicating whether to clear constraints
     */
    public void mouseDown(double x, double y, boolean cc) {

        if (CurrentAction == CONSTRUCT_FROM_TEXT) {
            PX = x;
            PY = y;
            CPoint cp = null;
            if (nd <= gt.getCons_no()) {
                int index = nd;
                Cons pt = gterm().getPcons(index);
                if (pt != null) {
                    int[] pp = pt.ps;
                    int type = pt.type;
                    if (!pt.is_conc()) {
                        pointAdded(pt, type, pp, cc, index, x, y, cp, pt.pss);

                        if (index != nd)
                            this.UndoAdded(pt.toDString(), false);
                    } else {
                        Vector v = addCondAux(gterm().getConclusion(), false);
                        Cond cc1 = gterm().getConc();
                        addConcLineOrCircle(cc1);
                        if (gterm().isTermAnimated()) {
                            String s = gterm().getAnimateString();
                            animateDiagramFromString(s);
                        }
                        this.UndoAdded(pt.toString(), false);
                        finishConstruction();
                        nd++;
                    }
                    gxInstance.getpprove().setListSelection(pt);
                } else finishConstruction();
            } else if (addPt2(gt.getPointsNum()) == null)
                finishConstruction();
        } else {
            super.DWButtonDown(x, y);
        }
    }

    /**
     * Processes the provided construction point and applies the appropriate geometric constraints
     * and operations based on its type.
     *
     * <p>This method examines the type of the provided construction object and executes the corresponding
     * logic to update the constraint and geometric element lists. Depending on the provided parameters,
     * it adds points, lines, circles, angles, or other entities and registers constraints related to them.</p>
     *
     * @param pt    the construction object encapsulating geometric data and type information
     * @param type  the specific type of construction operation to execute
     * @param pp    an array of integers representing indices for points and other parameters
     * @param cc    a boolean flag indicating whether to perform specific additional operations
     * @param index an identifier used for certain point creation methods
     * @param x     the x-coordinate value used for point creation if applicable
     * @param y     the y-coordinate value used for point creation if applicable
     * @param cp    a CPoint object that will be updated or used during the construction
     * @param pss   an array of additional parameters used for constructing constants or constraints
     */
    public void pointAdded(Cons pt, int type, int[] pp, boolean cc, int index, double x, double y, CPoint cp, Object[] pss) {
        switch (pt.type) {
            case GDDBase.C_POINT: {

                if (!addFreePt(pt)) {
                    nd++;
                    optmizePolynomial();
                }
            }
            break;
            case GDDBase.C_FOOT: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PFOOT, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                    this.addConstraintToList(cs);
                    addLn(pp[0], pp[1]);
                    addLn(pp[2], pp[3]);
                    addPt2Line(pp[0], pp[2], pp[3]);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.CO_MIDP:
            case GDDBase.C_MIDPOINT: {
                if (!addGTPt(pt)) {
                    this.addLn(pp[1], pp[2]);
                    Constraint cs = new Constraint(Constraint.MIDPOINT, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                    this.addConstraintToList(cs);
                    addPt2Line(pp[0], pp[1], pp[2]);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;

            case GDDBase.C_O_C: {
                if (!addGTPt(pt)) {
                    Circle c = null;
                    if ((c = fd_circle(pp[1], pp[2])) == null) { // add circle
                        this.addCr(pp[1], pp[2]);
                    } else {
                        cp = getPt(pp[0]);
                        Constraint cs = new Constraint(Constraint.PONCIRCLE, cp, c);
                        c.addPoint(cp);
                        c.pointStickToCircle(cp);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;
            case GDDBase.C_O_P: {
                if (!addGTPt(pt)) {
                    CLine lp = null;
                    if ((lp = this.fd_p_line(pp[1], pp[2], pp[3])) == null) {
                        lp = this.addPLn(pp[1], pp[2], pp[3]);
                    }
                    {
                        this.AddPointToLine(getPt(pp[0]), lp, false);
                        this.charsetAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case Gib.C_O_B: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case GDDBase.C_O_L: {
                if (!addGTPt(pt)) {
                    CLine ln = this.addLn(pp[1], pp[2]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                    ln.pointonline(cp);
                    this.charsetAndAddPoly(cc);
                    ln.pointonline(cp);
                    ln.addApoint(cp);
                    this.addConstraintToList(cs);
                    nd++;
                }
            }
            break;
            case Gib.C_O_D: {
                if (!addGTPt(pt)) {
                    CLine ln = this.addLn(pp[1], pp[2]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.ONDCIRCLE, cp, getPt(pp[1]), getPt(pp[2]));
                    addLn(pp[0], pp[1]);
                    addLn(pp[0], pp[2]);
                    this.charsetAndAddPoly(cc);
                    this.addConstraintToList(cs);
                    nd++;
                }
            }
            break;
            case Gib.C_I_BR: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, getPt(pp[1]), cp, getPt(pp[2]));
                    Constraint cs1 = new Constraint(Constraint.EQDISTANCE, cp, getPt(pp[3]), getPt(pp[4]), getPt(pp[5]));
                    this.charsetAndAddPoly(cc);
                    this.addConstraintToList(cs);
                    this.addConstraintToList(cs1);
                    nd++;
                }
            }
            break;

            case GDDBase.C_I_LL: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    CLine ln = this.addLn(pp[1], pp[2]);
                    CLine ln1 = this.addLn(pp[3], pp[4]);
                    Constraint cs = new Constraint(Constraint.PONLINE, cp, ln, false);
                    ln.addApoint(cp);
                    Constraint cs1 = new Constraint(Constraint.PONLINE, cp, ln1, false);
                    ln1.addApoint(cp);
                    Constraint csx = new Constraint(Constraint.INTER_LL, cp, ln, ln1);
                    this.charsetAndAddPoly(cc);
                    this.addConstraintToList(csx);
                    this.addConstraintToList(cs);
                    this.addConstraintToList(cs1);
                    nd++;
                }
            }
            break;
            case GDDBase.C_I_LP: {
                CLine ln = this.addLn(pp[1], pp[2]);
                CLine lp;
                if ((lp = this.fd_p_line(pp[3], pp[4], pp[5])) == null) {
                    this.addPLn(pp[3], pp[4], pp[5]);
                } else {
                    if (!addGTPt(pt)) {
                        cp = getPt(pp[0]);
                        Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                        lp.addApoint(cp);
                        this.AddPointToLine(cp, lp, false);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;

            case GDDBase.C_I_PP: {
                CLine lp1, lp2;
                if ((lp1 = this.fd_p_line(pp[1], pp[2], pp[3])) == null) {
                    this.addPLn(pp[1], pp[2], pp[3]);
                } else if ((lp2 = this.fd_p_line(pp[4], pp[5], pp[6])) == null) {
                    this.addPLn(pp[4], pp[5], pp[6]);
                } else {
                    if (!addGTPt(pt)) {
                        cp = getPt(pp[0]);
                        this.AddPointToLine(cp, lp1, false);
                        this.AddPointToLine(cp, lp2, false);
                        charsetAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case GDDBase.C_CIRCUM: {
                if (!addGTPt(pt)) {
                    CPoint p1 = getPt(pp[1]);
                    CPoint p2 = getPt(pp[2]);
                    CPoint p3 = getPt(pp[3]);
                    cp = getPt(pp[0]);
                    Constraint cs = new Constraint(Constraint.CIRCUMCENTER, cp, p1, p2, p3);
                    this.addConstraintToList(cs);
                    Circle c = this.addCr(pp[0], pp[1]);
                    c.addPoint(p2);
                    c.addPoint(p3);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case GDDBase.C_I_CC: {
                if (!addGTPt(pt)) {
                    cp = getPt(pp[0]);
                    Circle c1 = this.ad_circle(pp[1], pp[2]);
                    Circle c2 = this.ad_circle(pp[3], pp[4]);
                    Vector vlist = Circle.CommonPoints(c1, c2);
                    if (vlist.size() == 1) {
                        CPoint t = (CPoint) vlist.get(0);
                        Constraint cs = new Constraint(Constraint.INTER_CC1, cp, t, c1, c2);
                        c1.addPoint(cp);
                        c2.addPoint(cp);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(cs);
                    } else if (vlist.size() == 0) {
                        Constraint cs1 = new Constraint(Constraint.PONCIRCLE, cp, c1, false);
                        Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2, false);
                        Constraint cs = new Constraint(Constraint.INTER_CC, cp, c1, c2);
                        this.addConstraintToList(cs1);
                        this.addConstraintToList(cs2);
                        this.addConstraintToList(cs);
                        c1.addPoint(cp);
                        c2.addPoint(cp);
                        this.charsetAndAddPoly(true);
                    }
                    nd++;
                }
            }
            break;
            case GDDBase.C_I_LC: {
                if (!addGTPt(pt)) {
                    CLine ln = this.addLn(pp[1], pp[2]);
                    Circle c = this.ad_circle(pp[3], pp[4]);
                    CPoint p, p1;
                    p = p1 = null;
                    cp = getPt(pp[0]);
                    for (int i = 0; i < ln.points.size(); i++) {
                        Object obj = ln.points.get(i);
                        if (c.points.contains(obj)) {
                            if (p == null) {
                                p = (CPoint) obj;
                            } else {
                                p1 = (CPoint) obj;
                            }
                        }
                    }
                    if (p1 != null && p != null) {
                        return;
                    } else if (p != null && p1 == null) {
                        Constraint css = new Constraint(Constraint.LC_MEET, cp, p, ln, c);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(css);
                        ln.addApoint(cp);
                        c.addPoint(cp);
                    } else {
                        Constraint cs1 = new Constraint(Constraint.PONCIRCLE, cp, c, false);
                        Constraint cs2 = new Constraint(Constraint.PONLINE, cp, ln, false);
                        Constraint cs = new Constraint(Constraint.INTER_LC, cp, ln, c);
                        this.addConstraintToList(cs1);
                        this.addConstraintToList(cs2);
                        this.addConstraintToList(cs);
                        ln.addApoint(cp);
                        c.addPoint(cp);
                        this.charsetAndAddPoly(true);
                    }
                    nd++;
                }
            }
            break;
            case GDDBase.C_I_LT: {
                if (!addGTPt(pt)) {
                    CLine ln = this.addLn(pp[1], pp[2]);
                    CLine ln1 = this.addLn(pp[4], pp[5]);
                    CLine lnt = this.addLn(pp[0], pp[3]);
                    Constraint cs = new Constraint(Constraint.PERPENDICULAR, fd_point(pp[0]), fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                    CPoint p = fd_point(pp[0]);
                    this.AddPointToLine(p, ln, false);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;

            case Gib.C_O_T: {
                if (!addGTPt(pt)) {
                    CLine ln = this.addLn(pp[0], pp[1]);
                    CLine ln1 = this.addLn(pp[2], pp[3]);
                    Constraint cs = new Constraint(Constraint.PERPENDICULAR, ln, ln1);
                    this.addConstraintToList(cs);
                    this.addLineToList(ln);
                    this.addLineToList(ln1);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_O_A: {
                if (!addGTPt(pt)) {
                    if ((fd_line(pp[1], pp[2])) == null)
                        addLn(pp[1], pp[2]);
                    if ((fd_line(pp[3], pp[4])) == null)
                        addLn(pp[3], pp[4]);
                    if ((fd_line(pp[4], pp[5])) == null)
                        addLn(pp[4], pp[5]);
                    {
                        cp = getPt(pp[0]);
                        this.addLn(pp[1], pp[0]);
                        Constraint cs = new Constraint(Constraint.ONALINE, fd_point(pp[0]), fd_point(pp[1]),
                                fd_point(pp[2]), fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                        this.addConstraintToList(cs);
                        this.charsetAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case Gib.C_O_R: {
                if (!addGTPt(pt)) {
                    {
                        Circle c = null;
                        while (addGTPt(pt)) ;

                        cp = getPt(pp[0]);
                        c = addCr(pp[1], pp[0]);
                        Constraint cs = new Constraint(Constraint.ONRCIRCLE, cp, getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                        c.addPoint(cp);
                        c.pointStickToCircle(cp);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(cs);
                        nd++;
                    }
                }
            }
            break;
            case Gib.C_O_S: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ONSCIRCLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                nd++;
                break;
            }
            case Gib.C_O_AB: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ONSCIRCLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.addLn(pp[1], pp[2]);
                this.addLn(pp[0], pp[2]);
                this.addLn(pp[3], pp[2]);
                this.charsetAndAddPoly(cc);
                nd++;
                break;
            }
            case Gib.C_SQUARE: {
                while (addGTPt(pt)) ;
                Constraint cs1 = new Constraint(Constraint.PSQUARE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                Constraint cs2 = new Constraint(Constraint.NSQUARE, getPt(pp[1]), getPt(pp[0]), getPt(pp[3]));
                Constraint cs = new Constraint(Constraint.SQUARE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.addConstraintToList(cs1);
                this.addConstraintToList(cs2);
                this.addLn(pp[0], pp[1]);
                this.addLn(pp[1], pp[2]);
                this.addLn(pp[2], pp[3]);
                this.addLn(pp[3], pp[0]);
                this.charsetAndAddPoly(cc);
                nd++;
                break;
            }
            case Gib.C_ISO_TRI: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.ISO_TRIANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                nd++;
                break;
            }
            case Gib.C_EQ_TRI: {
                while (addGTPt(pt)) ;
                CPoint p0 = getPt(pp[0]);
                CPoint p1 = getPt(pp[1]);
                CPoint p2 = getPt(pp[2]);
                Constraint cs = new Constraint(Constraint.PETRIANGLE, p0, p1, p2);
//                            constraint cs1 = new constraint(constraint.EQDISTANCE, p0, p1, p0, p2, false);
//                            constraint cs2 = new constraint(constraint.EQDISTANCE, p1, p0, p1, p2, false);
                this.addConstraintToList(cs);
//                            this.addConstraintToList(cs1);
//                            this.addConstraintToList(cs2);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                this.charsetAndAddPoly(cc);
                nd++;
                break;
            }
            case Gib.C_R_TRI: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RIGHT_ANGLED_TRIANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[0]);
                nd++;
                break;
            }
            case Gib.C_R_TRAPEZOID: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RIGHT_ANGLE_TRAPEZOID, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case Gib.C_TRAPEZOID: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.TRAPEZOID, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case Gib.C_PARALLELOGRAM: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.PARALLELOGRAM, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case Gib.C_RECTANGLE: {
                while (addGTPt(pt)) ;
                Constraint cs = new Constraint(Constraint.RECTANGLE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                addLn(pp[0], pp[1]);
                addLn(pp[1], pp[2]);
                addLn(pp[2], pp[3]);
                addLn(pp[3], pp[0]);
                nd++;
                break;
            }
            case Gib.C_TRIANGLE: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.TRIANGLE, SelectList);
                    this.addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case Gib.C_QUADRANGLE: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.QUADRANGLE, SelectList);
                    this.addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case Gib.C_PENTAGON: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.PENTAGON, SelectList);
                    this.addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case Gib.C_POLYGON: {
                if (!addGTPt(pt)) {
                    addAllLn(pp);
                    Constraint cs = new Constraint(Constraint.POLYGON, SelectList);
                    this.addConstraintToList(cs);
                    nd++;
                    SelectList.clear();
                }
                break;
            }
            case Gib.C_INVERSION:
                break;
            case Gib.C_REF: {
                cp = addPt(index, x, y);
                CLine ln = this.fd_line(getPt(pp[3]), getPt(pp[4]));
                Constraint cs = new Constraint(Constraint.MIRROR, cp, this.getPt(pp[1]), ln);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                nd++;
            }
            break;
            case Gib.C_SYM: {
                cp = addPt(index);
                Constraint cs = new Constraint(Constraint.SYMPOINT, cp,
                        getPt(pp[0]), getPt(pp[1]));
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                nd++;

            }
            break;
            case Gib.C_I_RR: {
                while (addGTPt(pt)) ;
                cp = fd_point(pp[0]);
                CPoint o = this.fd_point(pp[1]);
                CPoint a = this.fd_point(pp[2]);
                CPoint b = this.fd_point(pp[3]);
                Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, o, a, b);
                CPoint o1 = this.fd_point(pp[4]);
                CPoint a1 = this.fd_point(pp[5]);
                CPoint b1 = this.fd_point(pp[6]);
                Constraint cs1 = new Constraint(Constraint.EQDISTANCE, cp, o1, a1, b1);
                this.addConstraintToList(cs);
                this.addConstraintToList(cs1);
                this.charsetAndAddPoly(cc);
                nd++;
            }
            break;
            case Gib.C_I_CR:
                break;
            case Gib.C_I_LR: {
                while (addGTPt(pt)) ;
                {
                    cp = this.fd_point(pp[0]);
                    CPoint o = this.fd_point(pp[3]);
                    CPoint a = this.fd_point(pp[4]);
                    CPoint b = this.fd_point(pp[5]);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, cp, o, a, b);
                    this.addConstraintToList(cs);
                    this.AddPointToLine(cp, addLn(pp[1], (pp[2])), false);
                    charsetAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case Gib.C_I_TC: {
                while (addGTPt(pt)) ;
                {
                    CLine lp1 = this.addTLn(pp[1], pp[2], pp[3]);
                    Circle c1 = this.ad_circle(pp[4], pp[5]);
                    cp = fd_point(pp[0]);
//                                this.MeetLCToDefineAPoint(lp1,c1,false,0,0);
                    this.AddPointToLine(cp, lp1, false);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_TR: {
                {
                    while (addGTPt(pt)) ;
                    addLn(pp[0], pp[1]);
                    addLn(pp[2], pp[3]);
                    Circle c = this.add_rcircle(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c);
                    Constraint cs1 = new Constraint(Constraint.PERPENDICULAR, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_PC: {

                {
                    while (addGTPt(pt)) ;
                    CLine lp1 = null;
                    Circle c1 = null;
                    lp1 = this.addPLn(pp[1], pp[2], pp[3]);
                    c1 = this.addCr(pp[4], pp[5]);
                    cp = fd_point(pp[0]);
                    this.AddPointToLine(cp, lp1, false);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_TT: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                {
                    while (addGTPt(pt)) ;
                    lp1 = this.addTLn(pp[1], pp[2], pp[3]);
                    lp2 = this.addTLn(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    this.AddPointToLine(cp, lp1, false);
                    this.AddPointToLine(cp, lp2, false);
                    charsetAndAddPoly(cc);
                    nd++;
                }

            }
            break;
            case Gib.C_I_PT: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                {
                    lp1 = this.addPLn(pp[1], pp[2], pp[3]);
                    lp2 = this.addTLn(pp[4], pp[5], pp[6]);
                    cp = fd_point(pp[0]);
                    this.AddPointToLine(cp, lp1, false);
                    this.AddPointToLine(cp, lp2, false);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_LS: {
                CLine ln = null;
                {
                    ln = addLn(pp[1], pp[2]);
                    cp = fd_point(pp[0]);
                    Circle c2 = this.fd_circle(pp[3], pp[4], pp[5]);
                    CPoint pi = this.lcmeet(c2, ln);
                    if (pi == null) {
                        Constraint cs = new Constraint(Constraint.PONLINE, cp, ln);
                        Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2);
                        this.addConstraintToList(cs);
                        this.addConstraintToList(cs2);
                        this.charsetAndAddPoly(cc);
                    } else {
                        if (this.lcmeet(c2, ln, pi) != null) {
                            return;
                        }
                        Constraint cs = new Constraint(Constraint.LC_MEET, cp, pi, ln, c2);
                        this.charsetAndAddPoly(cc);
                        this.addConstraintToList(cs);
                    }
                    nd++;
                }

            }
            break;
            case Gib.C_I_SS: {
                Circle c1 = this.fd_circle(pp[1], pp[2], pp[3]);
                Circle c2 = this.fd_circle(pp[4], pp[5], pp[6]);
                Vector v = Circle.CommonPoints(c1, c2);
                if (v.size() == 0) {
                    cp = addPt(index, x, y);
                    Constraint cs = new Constraint(Constraint.PONCIRCLE, cp, c1);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c2);
                    this.charsetAndAddPoly(cc);
                    this.addConstraintToList(cs);
                    this.addConstraintToList(cs2);
                    nd++;
                } else if (v.size() == 1) {
                    cp = addPt(index);
                    CPoint pi = (CPoint) v.get(0);
                    Constraint cs = new Constraint(Constraint.INTER_CC1, cp, pi, c1, c2);
                    this.charsetAndAddPoly(cc);
                    this.addConstraintToList(cs);
                    nd++;
                } else {
                    cp = addPt(index);
                    nd++;
                }
                c1.addPoint(cp);
                c2.addPoint(cp);
            }
            break;
            case Gib.C_I_LB: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;

                if ((lp1 = this.fd_line(pp[1], pp[2])) == null) {
                    this.addLn(pp[1], pp[2]);
                } else if ((lp2 = this.fd_line(pp[4], pp[5])) == null) {
                    this.addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    this.AddPointToLine(cp, lp1, false);
                    Constraint cs = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]), fd_point(pp[5]));
                    this.addConstraintToList(cs);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_TB: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                if ((lp1 = this.fd_t_line(pp[1], pp[2], pp[3])) == null) {
                    this.addLn(pp[1], pp[2]);
                } else if ((lp2 = this.fd_line(pp[4], pp[5])) == null) {
                    this.addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PERPBISECT, lp1, lp2);
                    Constraint cs2 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]));
                    this.addConstraintToList(cs1);
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_PB: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                while (addGTPt(pt)) ;
                if ((lp1 = this.fd_p_line(pp[1], pp[2], pp[3])) == null) {
                    this.addPLn(pp[1], pp[2], pp[3]);
                } else if ((lp2 = this.fd_line(pp[4], pp[5])) == null) {
                    this.addLn(pp[4], pp[5]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PARALLEL, lp1, lp2);
                    Constraint cs2 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[3]), fd_point(pp[4]));
                    this.addConstraintToList(cs1);
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_I_BB: {
                CLine lp1, lp2;
                lp1 = lp2 = null;
                {
                    this.addLn(pp[1], pp[2]);
                    this.addLn(pp[3], pp[4]);
                    if (!addGTPt(pt)) {
                        Constraint cs1 = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                        Constraint cs2 = new Constraint(Constraint.PERPBISECT, fd_point(pp[0]), fd_point(pp[3]), fd_point(pp[4]));
                        this.addConstraintToList(cs1);
                        this.addConstraintToList(cs2);
                        charsetAndAddPoly(cc);
                        nd++;
                    }
                }
            }
            break;
            case Gib.C_I_BC: {
                CLine lp1 = null;
                Circle c1 = null;
                while (addGTPt(pt)) ;
                if ((lp1 = this.fd_line(pp[1], pp[2])) == null) {
                    this.addLn(pp[1], pp[2]);
                } else if ((c1 = this.fd_circle(pp[3], pp[4])) == null) {
                    this.addCr(pp[3], pp[4]);
                } else {
                    cp = fd_point(pp[0]);
                    Constraint cs1 = new Constraint(Constraint.PERPBISECT, cp, fd_point(pp[1]), fd_point(pp[2]));
                    Circle c = this.fd_circle(pp[1], pp[2]);
                    Constraint cs2 = new Constraint(Constraint.PONCIRCLE, cp, c);
                    this.addConstraintToList(cs1);
                    this.addConstraintToList(cs2);
                    charsetAndAddPoly(cc);
                    nd++;
                }
            }
            case Gib.C_NETRIANGLE: {

            }
            break;
            case Gib.C_PETRIANGLE: {
                paraCounter++;
                cp = addPt(index);
                Constraint cs = new Constraint(Constraint.PETRIANGLE, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]));
                this.addLn(index, pp[1]);
                this.addLn(index, pp[2]);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(cc);
                nd++;
            }
            break;
            case Gib.C_ICENT1:
                break;
            case Gib.C_ICENT: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.INCENTER,
                            fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_ORTH: {
                if (!addGTPt(pt)) {
                    Constraint cs1 = new Constraint(Constraint.ORTHOCENTER,
                            fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]));
                    this.addConstraintToList(cs1);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
            }
            break;
            case Gib.C_CENT:
                break;
            case Gib.C_TRATIO: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.TRATIO, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[2]), fd_point(pp[3]),
                            pp[4], pp[5]);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    addLn(pp[0], pp[1]);
                    nd++;
                }
                break;
            }
            case Gib.C_PRATIO: {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.PRATIO, fd_point(pp[0]), fd_point(pp[1]),
                            fd_point(pp[2]), fd_point(pp[3]), pp[4], pp[5]);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    addLn(pp[0], pp[1]);
                    nd++;
                }
                break;
            }
            case Gib.C_LRATIO: {
                if (!addGTPt(pt)) {
                    // constraint cs = new constraint(constraint.LRATIO, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[3]),
                    //         new Integer(pp[4]), new Integer(pp[5]));
                    Constraint cs = new Constraint(Constraint.LRATIO, fd_point(pp[0]), fd_point(pp[1]), fd_point(pp[3]),
                            (Object) (pp[4]), pp[5]);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    addLn(pp[1], pp[3]);
                    nd++;
                }
            }
            break;
            case Gib.C_CONSTANT: {
                Param p = this.getANewParam();
                Constraint cs = new Constraint(Constraint.CONSTANT, pss[0], pss[1], p);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
                nd++;
            }
            break;
            case Gib.C_LINE:
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.LINE, getPt(pp[0]), getPt(pp[1]));
                    this.addLn(pp[0], pp[1]);
                    this.addConstraintToList(cs);
                    nd++;
                }
                break;
            case Gib.C_CIRCLE:
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.CIRCLE, getPt(pp[0]), getPt(pp[1]));
                    this.addCr(pp[0], pp[1]);
                    this.addConstraintToList(cs);
                    nd++;
                    break;
                }
            case Gib.C_EQDISTANCE: // not constructable.
            {
                if (!addGTPt(pt)) {
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, getPt(pp[0]), getPt(pp[1]), getPt(pp[2]), getPt(pp[3]));
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case Gib.C_EQANGLE: {
                if (!addGTPt(pt)) {
                    CPoint p1 = getPt(pp[0]);
                    CPoint p2 = getPt(pp[1]);
                    CPoint p3 = getPt(pp[2]);
                    CPoint p4 = getPt(pp[3]);
                    CPoint p5 = getPt(pp[4]);
                    CPoint p6 = getPt(pp[5]);
                    CLine ln1 = addLn(pp[0], pp[1]);
                    CLine ln2 = addLn(pp[1], pp[2]);
                    CLine ln3 = addLn(pp[3], pp[4]);
                    CLine ln4 = addLn(pp[4], pp[5]);
                    CAngle ag1 = new CAngle(ln1, ln2, p1, p3);
                    CAngle ag2 = new CAngle(ln3, ln4, p4, p6);
                    this.addAngleToList(ag1);
                    this.addAngleToList(ag2);
                    Constraint cs = new Constraint(Constraint.EQANGLE, ag1, ag2);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case Gib.C_CCTANGENT: {
                if (!addGTPt(pt)) {
                    Circle c1 = this.addCr(pp[1], pp[2]);
                    Circle c2 = this.addCr(pp[4], pp[5]);
                    Constraint cs = new Constraint(Constraint.CCTANGENT, c1, c2);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            case Gib.C_EQANGLE3P: {
                if (!addGTPt(pt)) {
                    CLine l1 = this.addLn(pp[0], pp[1]);
                    CLine l2 = this.addLn(pp[1], pp[2]);
                    CLine l3 = this.addLn(pp[3], pp[4]);
                    CLine l4 = this.addLn(pp[4], pp[5]);
                    CLine l5 = this.addLn(pp[6], pp[7]);
                    CLine l6 = this.addLn(pp[7], pp[8]);
                    CAngle ag1 = new CAngle(l1, l2, this.fd_point(pp[0]), fd_point(pp[2]));
                    CAngle ag2 = new CAngle(l3, l4, this.fd_point(pp[3]), fd_point(pp[5]));
                    CAngle ag3 = new CAngle(l5, l6, this.fd_point(pp[6]), fd_point(pp[8]));
                    Param pm = findConstantParam(pss[9].toString());
                    Constraint cs = new Constraint(Constraint.EQANGLE3P, ag1, ag2, ag3, pm);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(cc);
                    nd++;
                }
                break;
            }
            default: {
                CMisc.print("draw not yet supported : ");
                nd++;
            }
        }
    }

    /**
     * Finds a constant parameter by its name.
     *
     * @param name the name of the constant parameter
     * @return the constant parameter, or null if not found
     */
    public Param findConstantParam(String name) {
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.GetConstraintType() == Constraint.CONSTANT) {
                if (name.equals(cs.getelement(0).toString())) {
                    Param p = (Param) cs.getelement(2);
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Adds visual flashing effects for up to three objects.
     *
     * @param c1 the first object to flash
     * @param c2 the second object to flash
     * @param c3 the third object to flash
     */
    public void addObjectFlash(CClass c1, CClass c2, CClass c3) {
        clearFlash();
        int n = CMisc.getFlashInterval();

        if (c1 != null) {
            JFlash f = this.getObjectFlash(c1);
            f.setDealy(n / 2);
            addFlash1(f);
        }

        if (c2 != null) {
            JFlash f = this.getObjectFlash(c2);
            f.setDealy(n / 2);
            addFlash1(f);
        }

        if (c3 != null) {
            JFlash f = this.getObjectFlash(c3);
            f.setDealy(n / 2);
            addFlash1(f);
        }
        panel.repaint();
    }

    /**
     * Renders visual flashing effects for a given construction.
     *
     * @param c the construction
     */
    public void flashcons(Cons c) {
        if (c == null)
            return;
        switch (c.type) {
            case Gib.C_POINT:
                break;
            case Gib.C_O_L:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[1], c.ps[2]), null);
                break;
            case Gib.C_O_P:
                addObjectFlash(fd_point(c.ps[0]), fd_point(c.ps[1]), fd_line(c.ps[2], c.ps[3]));
                break;
            case Gib.C_O_T:
                addObjectFlash(fd_point(c.ps[0]), fd_point(c.ps[1]), fd_line(c.ps[2], c.ps[3]));
                break;
            case Gib.C_O_A:
                break;
            case Gib.C_O_C:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2]), null);
                break;
            case Gib.C_O_R:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2], c.ps[3]), null);
                break;
            case Gib.C_I_LL:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[1], c.ps[2]), fd_line(c.ps[3], c.ps[4]));
                break;
            case Gib.C_I_LP:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[1], c.ps[2]), fd_line(c.ps[4], c.ps[5]));
                break;
            case Gib.C_I_LT:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[1], c.ps[2]), fd_line(c.ps[4], c.ps[5]));
                break;
            case Gib.C_I_LC:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[1], c.ps[2]), fd_circle(c.ps[3], c.ps[4]));
                break;
            case Gib.C_I_PP:
            case Gib.C_I_PT:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[0], c.ps[1]), fd_line(c.ps[0], c.ps[4]));
                break;
            case Gib.C_I_LR:
                break;
            case Gib.C_I_CC:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2]), fd_circle(c.ps[3], c.ps[4]));
                break;
            case Gib.C_FOOT:
                addObjectFlash(fd_point(c.ps[0]), fd_line(c.ps[0], c.ps[1]), fd_line(c.ps[2], c.ps[3]));
                break;
            case Gib.C_CIRCUM:
            case Gib.C_CENT:
            case Gib.C_ORTH:
                addObjectFlash(fd_point(c.ps[0]), fd_circle(c.ps[1], c.ps[2], c.ps[3]), null);
                break;
            default:
                addObjectFlash(fd_point(c.ps[0]), null, null);
                break;
        }
    }

    /**
     * Finishes the construction process.
     */
    public void finishConstruction() {
        gxInstance.setActionMove();
        this.SetCurrentAction(MOVE);
        gxInstance.getpprove().setListSelectionLast();
        gxInstance.getpprove().finishedDrawing();
        this.flashCond(gterm().getConc(), true);
    }

    /**
     * Adds lines or circles based on the given condition.
     *
     * @param cc the condition
     */
    public void addConcLineOrCircle(Cond cc) {
        if (cc == null) return;

        switch (cc.pred) {
            case Gib.CO_ACONG:
                this.drawLineAndAdd(fd_point(cc.p[0]), fd_point(cc.p[1]));
                this.drawLineAndAdd(fd_point(cc.p[2]), fd_point(cc.p[3]));
                this.drawLineAndAdd(fd_point(cc.p[4]), fd_point(cc.p[5]));
                this.drawLineAndAdd(fd_point(cc.p[6]), fd_point(cc.p[7]));
                break;
            case Gib.CO_CONG:
                this.drawLineAndAdd(fd_point(cc.p[0]), fd_point(cc.p[1]));
                this.drawLineAndAdd(fd_point(cc.p[2]), fd_point(cc.p[3]));
        }
    }

    /**
     * Adds all lines based on the given points.
     *
     * @param pp the points
     */
    public void addAllLn(int[] pp) {
        SelectList.clear();
        int p1, p2;
        p1 = p2 = 0;
        for (int i = 0; i < pp.length && pp[i] != 0; i++) {
            int p = pp[i];
            if (p1 == 0)
                p1 = p;
            else if (p2 == 0)
                p2 = p;
            else {
                p1 = p2;
                p2 = p;
            }
            if (p1 != 0 && p2 != 0)
                this.addToSelectList(addLn(p1, p2));
        }
        this.addToSelectList(addLn(p2, pp[0]));
    }

    /**
     * Retrieves a point by its index.
     *
     * @param i the index of the point
     * @return the point, or null if not found
     */
    CPoint getPt(int i) {
        String s = gterm().getPtName(i);

        if (s == null || s.length() == 0) return null;
        for (int j = 0; j < pointlist.size(); j++) {
            CPoint p = (CPoint) pointlist.get(j);
            if (s.equals(p.getname())) return p;
        }
        return null;
    }

    /**
     * Checks if a point is free.
     *
     * @param n the index of the point
     * @return true if the point is free, false otherwise
     */
    public boolean isFreePoint(int n) {
        GTerm gt = gterm();
        return gt.isFreePoint(n);
    }

    /**
     * Adds a point by its index.
     *
     * @param index the index of the point
     * @return the added point
     */
    CPoint addPt(int index) {
        ProPoint pt = gterm().getProPoint(index);
        CPoint cp = null;
        if (findPoint(pt.getName()) == null) {
            cp = CreateANewPoint(pt.getX(), pt.getY());
            CText t = cp.ptext;
            t.setText(pt.name);
            t.setXY(pt.getX1(), pt.getY1());
            pointlist.add(cp);
            textlist.add(t);
            if (PX != 0 && PY != 0 && cp.getx() == 0 && cp.gety() == 0)
                cp.setXY(PX, PY);
        }
        return cp;
    }

    /**
     * Adds a point by its index and sets its coordinates.
     *
     * @param index the index of the point
     * @param x     the x-coordinate of the point
     * @param y     the y-coordinate of the point
     * @return the added point
     */
    CPoint addPt(int index, double x, double y) {
        CPoint p = this.addPt(index);
        p.setXY(x, y);
        return p;
    }

    /**
     * Finds a line between two points.
     *
     * @param a the index of the first point
     * @param b the index of the second point
     * @return the line, or null if not found
     */
    CLine fd_line(int a, int b) {
        return fd_line(getPt(a), getPt(b));
    }

    /**
     * Finds a parallel line through a point.
     *
     * @param a the index of the point
     * @param b the index of the first point of the line
     * @param c the index of the second point of the line
     * @return the parallel line, or null if not found
     */
    CLine fd_p_line(int a, int b, int c) {
        CPoint A = getPt(a);
        CLine ln = fd_line(b, c);
        if (ln != null) {
            for (int i = 0; i < constraintlist.size(); i++) {
                Constraint cs = (Constraint) constraintlist.get(i);
                if (cs.GetConstraintType() == Constraint.PARALLEL) {
                    CLine ln1 = (CLine) cs.getelement(0);
                    CLine ln2 = (CLine) cs.getelement(1);
                    if (ln1.points.contains(A) && ln2 == ln) {
                        return ln1;
                    } else if (ln2.points.contains(A) && ln1 == ln) {
                        return ln2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a perpendicular line through a point.
     *
     * @param a the index of the point
     * @param b the index of the first point of the line
     * @param c the index of the second point of the line
     * @return the perpendicular line, or null if not found
     */
    CLine fd_t_line(int a, int b, int c) {
        CPoint A = getPt(a);
        CLine ln = fd_line(b, c);
        if (ln != null) {
            for (int i = 0; i < constraintlist.size(); i++) {
                Constraint cs = (Constraint) constraintlist.get(i);
                if (cs.GetConstraintType() == Constraint.PERPENDICULAR) {
                    CLine ln1 = (CLine) cs.getelement(0);
                    CLine ln2 = (CLine) cs.getelement(1);
                    if (ln1.points.contains(A) && ln2 == ln) {
                        return ln1;
                    } else if (ln2.points.contains(A) && ln1 == ln) {
                        return ln2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds a circle through two points.
     *
     * @param o the index of the center point
     * @param a the index of the point on the circle
     * @return the added circle
     */
    Circle ad_circle(int o, int a) {
        Circle c = fd_circle(o, a);
        if (c != null) {
            return c;
        }
        c = new Circle(getPt(o), getPt(a));
        this.addCircleToList(c);
        return c;
    }

    /**
     * Adds a perpendicular line through a point.
     *
     * @param a the index of the point
     * @param b the index of the first point of the line
     * @param c the index of the second point of the line
     * @return the added perpendicular line
     */
    CLine addTLn(int a, int b, int c) {
        CPoint p1 = getPt(a);
        CLine lp = this.fd_line(b, c);
        if (lp == null) {
            lp = this.addLn(b, c);
        }
        CLine ln = new CLine(p1, CLine.TLine);
        Constraint cs = new Constraint(Constraint.PERPENDICULAR, ln, lp);
        ln.addconstraint(cs);
        this.addLineToList(ln);
        this.addConstraintToList(cs);
        return ln;
    }

    /**
     * Adds a parallel line through a point.
     *
     * @param a the index of the point
     * @param b the index of the first point of the line
     * @param c the index of the second point of the line
     * @return the added parallel line
     */
    CLine addPLn(int a, int b, int c) {
        CPoint p1 = getPt(a);
        CLine lp = this.fd_line(b, c);
        if (lp == null) {
            lp = this.addLn(b, c);
        }
        CLine ln = new CLine(p1, CLine.PLine);
        Constraint cs = new Constraint(Constraint.PARALLEL, ln, lp);
        ln.addconstraint(cs);
        this.addLineToList(ln);
        this.addConstraintToList(cs);
        return ln;
    }

    /**
     * Adds a line between two points.
     *
     * @param a the index of the first point
     * @param b the index of the second point
     * @return the added line, or null if the points are invalid
     */
    CLine addLn(int a, int b) {
        if (a == 0 || b == 0 || a == b)
            return null;

        CPoint p1 = getPt(a);
        CPoint p2 = getPt(b);
        CLine ln = this.fd_line(p1, p2);
        if (p1 != null && p2 != null && ln == null) {
            CLine line = new CLine(p1, p2, CLine.LLine);
            this.addLineToList(line);
            return line;
        } else {
            return ln;
        }
    }


    /**
     * Finds the intersection point between a circle and a line.
     *
     * @param c  the circle
     * @param ln the line
     * @return the intersection point, or null if no intersection
     */
    public CPoint lcmeet(Circle c, CLine ln) {
        for (int i = 0; i < c.points.size(); i++) {
            CPoint t = (CPoint) c.points.get(i);

            if (ln.points.contains(t)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Finds the intersection point between a circle and a line, excluding a specific point.
     *
     * @param c  the circle
     * @param ln the line
     * @param p1 the point to exclude from the search
     * @return the intersection point, or null if no intersection
     */
    public CPoint lcmeet(Circle c, CLine ln, CPoint p1) {
        for (int i = 0; i < c.points.size(); i++) {
            CPoint t = (CPoint) c.points.get(i);
            if (t != p1 && ln.points.contains(t)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Renders visual flashing effects for a given condition.
     *
     * @param co the condition
     * @param fb a flag indicating whether to clear existing flash effects
     */
    public void flashCond(Cond co, boolean fb) {
        if (this.pointlist.size() == 0) {
            return;
        }

        if (co.p[0] == 0 && co.p[1] == 0) {
            this.flashattr(co.get_attr(), panel);
        } else {
            if (co.pred == Gib.CO_ACONG || co.pred == Gib.CO_ATNG) {
                int[] vp = co.p;
                if (vp[0] != 0) {
                    JFlash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
                    addFlash(f);
                    f = getAngleFlash(panel, vp[4], vp[5], vp[6], vp[7]);
                    addFlash1(f);
                }
            } else {
                this.addFlash(this.getCond(co, fb));
            }
        }
    }

    /**
     * Retrieves a flash effect for a given condition.
     *
     * @param co the condition
     * @param fb a flag indicating whether to clear existing flash effects
     * @return the flash effect
     */
    public JFlash getCond(Cond co, boolean fb) {
        if (this.pointlist.size() == 0) {
            return null;
        }
        JPanel panel = this.panel;
        return getFlashCond(panel, co, fb);
    }

    /**
     * Adds a congruence flash effect for a given condition.
     *
     * @param co the condition
     * @param cl a flag indicating whether to clear existing flash effects
     */
    public void addCongFlash(Cond co, boolean cl) {
        if (co == null) {
            return;
        }
        if (co.pred == Gib.CO_ACONG) {
            addAcongFlash(co, cl);
        } else {
            this.addFlash1(this.getFlashCond(panel, co, cl));
        }
    }

    /**
     * Adds an angle congruence flash effect for a given condition.
     *
     * @param co the condition
     * @param cl a flag indicating whether to clear existing flash effects
     */
    public void addAcongFlash(Cond co, boolean cl) {
        if (co.pred == Gib.CO_ACONG) {
            if (cl) {
                this.clearFlash();
            }
            int[] vp = co.p;
            if (vp[0] == 0) {
                return;
            }
            JFlash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
            this.addFlash1(f);
            f = getAngleFlash(panel, vp[4], vp[5], vp[6], vp[7]);
            this.addFlash1(f);
        }
    }

    /**
     * Adds a line between two points.
     *
     * @param a the first point
     * @param b the second point
     * @return the line, or null if the points are invalid
     */
    public CLine add_Line(CPoint a, CPoint b) {
        if (a == null || b == null) return null;
        CLine ln = fd_line(a, b);
        if (ln != null) return ln;
        ln = new CLine(a, b);
        addLineToList(ln);
        return ln;
    }

    /**
     * Adds a line with specific color and dash style between two points.
     *
     * @param a     the first point
     * @param b     the second point
     * @param color the color of the line
     * @param d     the dash style of the line
     * @return the line
     */
    public CLine addLnWC(CPoint a, CPoint b, int color, int d) {
        CLine ln = null;
        if ((ln = this.fd_line(a, b)) == null) {
            ln = add_Line(a, b);
            if (ln != null) {
                ln.setColor(DrawData.RED);
                ln.setDash(d);
            }
        }
        return ln;
    }

    /**
     * Retrieves a point from a condition by its index.
     *
     * @param c the condition
     * @param n the index of the point
     * @return the point, or null if not found
     */
    public CPoint getPtN(Cons c, int n) {
        if (c == null) return null;
        Object o = c.getPTN(n);
        if (o == null) return null;
        String s = o.toString();
        return this.findPoint(s);
    }

    /**
     * Adds auxiliary conditions for a given condition.
     *
     * @param co  the condition
     * @param aux a flag indicating whether to add auxiliary conditions
     * @return a vector of added lines
     */
    public Vector addCondAux(Cons co, boolean aux) {
        Vector vl = new Vector();
        if (co == null) {
            return vl;
        }
        int d = 0;
        if (aux) {
            d = DrawData.DASH8;
        }

        switch (co.type) {
            case Gib.CO_COLL: {
                CLine ln = this.addLnWC(getPtN(co, 0), getPtN(co, 1), DrawData.RED, d);
                ln.addApoint(getPtN(co, 2));
                vl.add(ln);
            }
            break;
            case Gib.CO_PARA:
            case Gib.CO_PERP:
            case Gib.CO_CONG: {
                CLine ln1 = this.addLnWC(getPtN(co, 0), getPtN(co, 1), DrawData.RED, d);
                CLine ln2 = this.addLnWC(getPtN(co, 2), getPtN(co, 3), DrawData.RED, d);
                vl.add(ln1);
                vl.add(ln2);
            }
            break;
            case Gib.CO_ACONG:
                break;
            case Gib.CO_MIDP: {
                this.add_Line(getPtN(co, 0), getPtN(co, 1));
                this.add_Line(getPtN(co, 2), getPtN(co, 3));
                int n = getEMarkNum() / 2 + 1;
                Cedmark m1 = this.addedMark(getPtN(co, 0), getPtN(co, 1));
                Cedmark m2 = this.addedMark(getPtN(co, 2), getPtN(co, 3));
                if (m1 != null) {
                    m1.setdnum(n);
                }
                if (m2 != null) {
                    m2.setdnum(n);
                }
            }
            break;
            case Gib.CO_CTRI:
                break;
            case Gib.CO_CYCLIC:
                break;
        }
        return vl;
    }

    /**
     * Retrieves a flash effect for a given condition.
     *
     * @param panel the panel to display the flash effect
     * @param co    the condition
     * @param fb    a flag indicating whether to clear existing flash effects
     * @return the flash effect
     */
    public JFlash getFlashCond(JPanel panel, Cond co, boolean fb) {
        JFlash f = this.getFlashCond(panel, co);
        return f;
    }

    /**
     * Retrieves a flash effect for a given condition.
     *
     * @param panel the panel to display the flash effect
     * @param co    the condition
     * @return the flash effect
     */
    public JFlash getFlashCond(JPanel panel, Cond co) {

        if (this.pointlist.size() == 0) {
            return null;
        }

        switch (co.pred) {
            case Gib.CO_COLL: {
                JLineFlash f = new JLineFlash(panel);
                int d = f.addALine();
                for (int i = 0; i < 3; i++) {
                    f.addAPoint(d, fd_point(co.p[i]));
                }
                return (f);
            }
            case Gib.CO_PARA: {
                int[] p = co.p;
                JLineFlash f = new JLineFlash(panel);
                int id = f.addALine();
                f.addAPoint(id, fd_point(p[0]));
                f.addAPoint(id, fd_point(p[1]));
                f.setInfinitLine(id);
                id = f.addALine();
                f.addAPoint(id, fd_point(p[2]));
                f.addAPoint(id, fd_point(p[3]));
                f.setInfinitLine(id);
                return (f);
            }
            case Gib.CO_PERP: {
                int[] p = co.p;
                JTLineFlash f = new JTLineFlash(panel, fd_point(p[0]), fd_point(p[1]),
                        fd_point(p[2]), fd_point(p[3]));
                this.addFlash1(f);
                return (f);
            }
            case Gib.CO_CONG: {
                int[] p = co.p;
                if ((p[0] == p[2] && p[1] == p[3]) || (p[0] == p[3] && p[1] == p[2])) {
                    JLineFlash f = new JLineFlash(panel);
                    int id = f.addALine();
                    f.addAPoint(id, fd_point(p[0]));
                    f.addAPoint(id, fd_point(p[1]));
                    return (f);
                } else {
                    JCgFlash f = new JCgFlash(panel);
                    f.addACg(fd_point(p[0]), fd_point(p[1]));
                    f.addACg(fd_point(p[2]), fd_point(p[3]));
                    return (f);
                }
            }
            case Gib.CO_MIDP: {
                int[] p = co.p;
                JCgFlash f = new JCgFlash(panel);
                f.addACg(fd_point(p[0]), fd_point(p[1]));
                f.addACg(fd_point(p[0]), fd_point(p[2]));
                return (f);
            }
            case Gib.CO_ACONG: {
                int[] vp = co.p;
                if (vp[0] == 0) {
                    break;
                }
                JFlash f = getAngleFlash(panel, vp[0], vp[1], vp[2], vp[3]);
                return (f);
            }
            case Gib.CO_TANG: {
                JFlash f = getAngleFlash(panel, co.p[0], co.p[1], co.p[1], co.p[2]);
                return (f);
            }
            case Gib.CO_STRI:
            case Gib.CO_CTRI: {
                int[] p = co.p;
                JTriFlash f = new JTriFlash(panel, fd_point(p[0]), fd_point(p[1]), fd_point(p[2]), fd_point(p[3]), fd_point(p[4]),
                        fd_point(p[5]), true, DrawData.LIGHTCOLOR);
                return (f);
            }
            case Gib.CO_CYCLIC: {
                JCirFlash f = new JCirFlash(panel);
                int[] p = co.p;
                if (co.p[0] != 0) {
                    f.setCenter(fd_point(p[0]));
                }
                for (int i = 1; i < p.length; i++) {
                    if (p[i] != 0) {
                        f.addAPoint(fd_point(p[i]));
                    }
                }
                return (f);
            }

        }
        return null;

    }

    /**
     * Retrieves an angle flash effect for two lines intersecting at a point.
     *
     * @param panel the panel to display the flash effect
     * @param p1    the first point of the first line
     * @param p2    the second point of the first line
     * @param p3    the first point of the second line
     * @param p4    the second point of the second line
     * @return the angle flash effect
     */
    public JFlash getAngleFlash(JPanel panel, int p1, int p2, int p3, int p4) {
        return getMAngleFlash(panel, fd_point(p1), fd_point(p2), fd_point(p3), fd_point(p4), 1); // full angle.
    }

    /**
     * Retrieves a modified angle flash effect for two lines intersecting at a point.
     *
     * @param panel the panel to display the flash effect
     * @param p1    the first point of the first line
     * @param p2    the second point of the first line
     * @param p3    the first point of the second line
     * @param p4    the second point of the second line
     * @param t     the type of angle flash effect
     * @return the modified angle flash effect
     */
    public JFlash getMAngleFlash(JPanel panel, CPoint p1, CPoint p2, CPoint p3, CPoint p4, int t) {
        CAngle ag = fd_angle_m(p1, p2, p3, p4);
        if (ag != null) {
            JFlash f = this.getObjectFlash(ag);
            return f;
        }
        JAngleFlash f = new JAngleFlash(panel, p1, p2, p3, p4);
        f.setFtype(t);

        CLine ln1, ln2;
        if ((ln1 = this.fd_line(p1, p2)) != null) {
            f.setDrawLine1(false);
        }
        if ((ln2 = this.fd_line(p3, p4)) != null) {
            f.setDrawLine2(false);
        }
        if (ln1 != null && ln2 != null && CLine.commonPoint(ln1, ln2) != null) {
            f.setAngleTwoLineIntersected(true);
        }

        return f;
    }

    /**
     * Retrieves the number of area flash effects.
     *
     * @return the number of area flash effects
     */
    public int getAreaFlashNumber() {
        int n = 0;
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            if (f instanceof JAreaFlash) {
                n++;
            }
        }
        return n;
    }

    /**
     * Retrieves an area flash effect for a given drawing object.
     *
     * @param d the drawing object
     * @return the area flash effect
     */
    public JFlash getAreaFlash(MDrObj d) {
        int n = this.getAreaFlashNumber();
        Vector v = new Vector();
        for (int i = 0; i < d.getObjectNum(); i++)
            v.add((CPoint) d.getObject(i));

        CPolygon p = findPolygon1(v);
        if (p == null) {
            JAreaFlash f = new JAreaFlash(panel, n);
            for (int i = 0; i < v.size(); i++)
                f.addAPoint((CPoint) v.get(i));
            return f;
        }
        JObjectFlash f = new JObjectFlash(panel);
        f.addFlashObject(p);
        return f;
    }

    /**
     * Retrieves a flash effect for a given drawing object.
     *
     * @param d the drawing object
     * @return the flash effect
     */
    public JFlash getDrobjFlash(MDrObj d) {
        int t1 = d.getType1();
        if (t1 == MDrObj.LINE) {
            CPoint p1 = d.getObject(0);
            CPoint p2 = d.getObject(1);
            if (p1 != null && p2 != null) {
                JLineFlash f = new JLineFlash(panel);
                int id = f.addALine();
                f.addAPoint(id, p1);
                f.addAPoint(id, p2);
                return f;
            }
        } else if (t1 == MDrObj.CIRCLE) {
        } else if (t1 == MDrObj.AREA || t1 == MDrObj.TRIANGLE ||
                t1 == MDrObj.SQUARE || t1 == MDrObj.PARALLELOGRAM || t1 == MDrObj.RECTANGLE
                || t1 == MDrObj.QUADRANGLE || t1 == MDrObj.TRAPEZOID) {
            return (getAreaFlash(d));
        } else if (t1 == MDrObj.ANGLE) {
            if (d.getObjectNum() == 3) {
                JFlash f = this.getMAngleFlash(panel, (CPoint) d.getObject(0), (CPoint) d.getObject(1),
                        (CPoint) d.getObject(1), (CPoint) d.getObject(2), 3);
                return f;
            }
        }
        return null;
    }

    /**
     * Renders visual flashing effects for a given node.
     *
     * @param n the node
     */
    public void flashmnode(MNode n) {

        for (int i = 0; i < n.objSize(); i++) {
            MObject obj = n.getObject(i);
            flashmobj(obj, false);
        }
    }

    /**
     * Renders visual flashing effects for a given object.
     *
     * @param obj the object
     */
    public void flashmobj(MObject obj) {
        flashmobj(obj, true);
    }

    /**
     * Renders visual flashing effects for a given object.
     *
     * @param obj   the object
     * @param clear whether to clear existing flash effects
     */
    public void flashmobj(MObject obj, boolean clear) {
        if (obj == null) {
            return;
        }
        if (clear) {
            this.clearFlash();
        }
        int t = obj.getType();
        switch (t) {
            case MObject.DOBJECT: {
                MDrObj d = (MDrObj) obj;
                JFlash f = getDrobjFlash(d);
                if (f != null) {
                    this.addFlash1(f);
                }
            }
            break;
            case MObject.DRAW: {
                MDraw d = (MDraw) obj;
                Vector v = d.getAllUndoStruct();
                this.setUndoListForFlash1(v);
            }
            break;
            case MObject.ASSERT:
                this.flashassert((MAssertion) obj);
                break;
            case MObject.EQUATION: {
                MEquation eq = (MEquation) obj;
                int cd = 0;
                for (int i = 0; i < eq.getTermCount(); i++) {
                    MEqTerm tm = (MEqTerm) eq.getTerm(i);
                    MDrObj dj = tm.getObject();
                    if (dj != null) {
                        JFlash fs = this.getDrobjFlash(dj);
                        if (tm.isEqFamily() && !dj.isPolygon())
                            cd++;

                        if (fs != null) {
                            fs.setColor(DrawData.getColorSinceRed(cd));
                            this.addFlash1(fs);
                        }
                    }
                }
            }
            break;
        }
    }

    /**
     * Adds a flash effect for a line.
     *
     * @param p1 the first point of the line
     * @param p2 the second point of the line
     */
    public void addlineFlash(CPoint p1, CPoint p2) {
        JLineFlash f = new JLineFlash(panel);
        int id = f.addALine();
        f.addAPoint(id, p1);
        f.addAPoint(id, p2);
        addFlash1(f);
    }

    /**
     * Adds a flash effect for an infinite line.
     *
     * @param p1 the first point of the line
     * @param p2 the second point of the line
     */
    public void addInfinitelineFlash(CPoint p1, CPoint p2) {
        JLineFlash f = new JLineFlash(panel);
        int id = f.addALine();
        f.addAPoint(id, p1);
        f.addAPoint(id, p2);
        f.setInfinitLine(id);
        addFlash1(f);
    }

    /**
     * Renders visual flashing effects for a given assertion.
     *
     * <p>
     * This method examines the type of the provided <code>MAssertion</code> and
     * displays the corresponding flash pattern based on the assertion type.
     * It supports various assertion types such as collinearity, parallelism,
     * equality of distances, perpendicularity, cyclic configurations, angle equality,
     * midpoint conditions, congruency, triangle properties, and several others.
     * </p>
     *
     * @param ass the geometric assertion to be processed and flashed; may be null
     */
    public void flashassert(MAssertion ass) {
        if (ass == null)
            return;

        JPanel panel = this.panel;
        switch (ass.getAssertionType()) {
            case MAssertion.COLL: {
                JLineFlash f = new JLineFlash(panel);
                int id = f.addALine();
                for (int i = 0; i < ass.getobjNum(); i++) {
                    f.addAPoint(id, (CPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case MAssertion.PARA: {
                JLineFlash f = new JLineFlash(panel);
                int id = 0;
                for (int i = 0; i < ass.getobjNum(); i++) {
                    if (i % 2 == 0) {
                        id = f.addALine();
                        f.setInfinitLine(id);
                    }
                    f.addAPoint(id, (CPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case MAssertion.EQDIS: {
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                CPoint p4 = (CPoint) ass.getObject(3);
                if (p1 != null && p2 != null && p3 != null && p4 != null) {
                    addCGFlash(p1, p2, p3, p4);
                }
            }
            break;
            case MAssertion.PERP: {
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                CPoint p4 = (CPoint) ass.getObject(3);
                JTLineFlash f = new JTLineFlash(panel, p1, p2, p3, p4);
                this.addFlash1(f);
            }
            break;

            case MAssertion.DISLESS:
            case MAssertion.PERPBISECT:
            case MAssertion.CONCURRENT: {
                JLineFlash f = new JLineFlash(panel);
                int id = 0;
                for (int i = 0; i < ass.getobjNum(); i++) {
                    if (i % 2 == 0) {
                        id = f.addALine();
                    }
                    f.addAPoint(id, (CPoint) ass.getObject(i));
                }
                addFlash1(f);
            }

            break;
            case MAssertion.CYCLIC: {
                JCirFlash f = new JCirFlash(panel);
                for (int i = 0; i < ass.getobjNum(); i++) {
                    f.addAPoint((CPoint) ass.getObject(i));
                }
                addFlash1(f);
            }
            break;
            case MAssertion.EQANGLE:
            case MAssertion.ANGLESS: {
                if (ass.getobjNum() == 6) {
                    JFlash f = this.getMAngleFlash(panel, (CPoint) ass.getObject(0), (CPoint) ass.getObject(1),
                            (CPoint) ass.getObject(1), (CPoint) ass.getObject(2), 3);
                    this.addFlash1(f);
                    f = this.getMAngleFlash(panel, (CPoint) ass.getObject(3), (CPoint) ass.getObject(4),
                            (CPoint) ass.getObject(4), (CPoint) ass.getObject(5), 3);
                    this.addFlash1(f);
                }
            }
            break;
            case MAssertion.MID:
                if (ass.getobjNum() == 3) {
                    JCgFlash f = new JCgFlash(panel);
                    CPoint p1 = (CPoint) ass.getObject(0);
                    CPoint p2 = (CPoint) ass.getObject(1);
                    CPoint p3 = (CPoint) ass.getObject(2);
                    f.addACg(p1, p2);
                    f.addACg(p1, p3);
                    if (this.fd_edmark(p1, p2) != null)
                        f.setDrawdTT(false);

                    this.addFlash1(f);
                }
                break;
            case MAssertion.CONG:
            case MAssertion.SIM: {
                if (6 == ass.getobjNum()) {
                    JTriFlash f = new JTriFlash(panel,
                            (CPoint) ass.getObject(0),
                            (CPoint) ass.getObject(1),
                            (CPoint) ass.getObject(2),
                            (CPoint) ass.getObject(3),
                            (CPoint) ass.getObject(4),
                            (CPoint) ass.getObject(5), false,
                            DrawData.LIGHTCOLOR);
                    this.addFlash1(f);
                }
            }
            break;

            case MAssertion.R_TRIANGLE: {
                this.addAreaFlash(ass);
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
//                CPoint p4 = (CPoint) ass.getObject(3);
                JTLineFlash f = new JTLineFlash(panel, p1, p2, p1, p3);
                this.addFlash1(f);
                break;
            }
            case MAssertion.R_ISO_TRIANGLE: {
                this.addAreaFlash(ass);

                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                addCGFlash(p1, p2, p1, p3);

                JTLineFlash f = new JTLineFlash(panel, p1, p2, p1, p3);
                this.addFlash1(f);
                break;
            }

            case MAssertion.CONVEX:
            case MAssertion.TRAPEZOID:
            case MAssertion.SQUARE:
            case MAssertion.RECTANGLE: {
                this.addAreaFlash(ass);
                break;
            }
            case MAssertion.EQ_TRIANGLE: {
                this.addAreaFlash(ass);
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);

                JCgFlash f1 = new JCgFlash(panel);
                f1.addACg(p1, p2);
                if (null != fd_edmark(p1, p2))
                    f1.setDrawdTT(false);
                JCgFlash f2 = new JCgFlash(panel);
                f2.addACg(p1, p3);
                if (null != fd_edmark(p1, p3))
                    f2.setDrawdTT(false);

                JCgFlash f3 = new JCgFlash(panel);
                f3.addACg(p2, p3);
                if (null != fd_edmark(p1, p3))
                    f3.setDrawdTT(false);
                this.addFlash1(f1);
                this.addFlash1(f2);
                this.addFlash1(f3);
                break;
            }
            case MAssertion.ISO_TRIANGLE: {
                this.addAreaFlash(ass);
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                addCGFlash(p1, p2, p1, p3);
                break;

            }
            case MAssertion.PARALLELOGRAM: {
                this.addAreaFlash(ass);
                break;
            }

            case MAssertion.BETWEEN: {
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                this.addPtEnlargeFlash(p1);
                addlineFlash(p2, p3);
                break;
            }

            case MAssertion.ANGLE_INSIDE:
            case MAssertion.ANGLE_OUTSIDE: {
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                CPoint p4 = (CPoint) ass.getObject(3);
                addPtEnlargeFlash(p1);
                JFlash f = this.getMAngleFlash(panel, p2, p3, p3, p4, 3);
                this.addFlash1(f);
                break;
            }
            case MAssertion.TRIANGLE_INSIDE: {
                this.addAreaFlash1(ass);
                CPoint p1 = (CPoint) ass.getObject(0);
                this.addPtEnlargeFlash(p1);
                break;
            }
            case MAssertion.OPPOSITE_SIDE: {

                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                CPoint p4 = (CPoint) ass.getObject(3);
                this.addInfinitelineFlash(p3, p4);
                JArrowFlash f = new JArrowFlash(panel, p1, p2, 0);
                this.addFlash1(f);
                break;
            }
            case MAssertion.SAME_SIDE: {
                CPoint p1 = (CPoint) ass.getObject(0);
                CPoint p2 = (CPoint) ass.getObject(1);
                CPoint p3 = (CPoint) ass.getObject(2);
                CPoint p4 = (CPoint) ass.getObject(3);
                this.addInfinitelineFlash(p3, p4);
                JArrowFlash f = new JArrowFlash(panel, p1, p2, 1);
                this.addFlash1(f);
                break;
            }
            case MAssertion.PARA_INSIDE: {
                this.addAreaFlash1(ass);
                this.addPtEnlargeFlash((CPoint) ass.getObject(0));
                break;
            }
        }

    }

    /**
     * Adds a flash effect to enlarge a point.
     *
     * @param pt the point to enlarge
     */
    public void addPtEnlargeFlash(CPoint pt) {
        JPointEnlargeFlash f = new JPointEnlargeFlash(panel, pt);
        this.addFlash1(f);
    }

    /**
     * Adds a flash effect for congruent segments.
     *
     * @param p1 the first point of the first segment
     * @param p2 the second point of the first segment
     * @param p3 the first point of the second segment
     * @param p4 the second point of the second segment
     */
    public void addCGFlash(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        JCgFlash f1 = new JCgFlash(panel);
        f1.addACg(p1, p2);
        if (null != fd_edmark(p1, p2))
            f1.setDrawdTT(false);
        JCgFlash f2 = new JCgFlash(panel);
        f2.addACg(p3, p4);
        if (null != fd_edmark(p3, p4))
            f2.setDrawdTT(false);
        JSegmentMovingFlash fn = new JSegmentMovingFlash(panel, p1, p2, p3, p4, 3, 3);
        this.addCgFlash(f1, f2, fn);
        this.startFlash();
    }

    /**
     * Adds a flash effect for an area based on an assertion.
     *
     * @param ass the assertion containing the area information
     */
    public void addAreaFlash1(MAssertion ass) {
        int n = this.getAreaFlashNumber();
        JAreaFlash f = new JAreaFlash(panel, n);
        for (int i = 1; i < ass.getobjNum(); i++) {
            f.addAPoint((CPoint) ass.getObject(i));
        }
        this.addFlash1(f);
    }

    /**
     * Adds a flash effect for an area based on an assertion.
     *
     * @param ass the assertion containing the area information
     */
    public void addAreaFlash(MAssertion ass) {
        int n = this.getAreaFlashNumber();
        JAreaFlash f = new JAreaFlash(panel, n);
        for (int i = 0; i < ass.getobjNum(); i++) {
            f.addAPoint((CPoint) ass.getObject(i));
        }
        this.addFlash1(f);
    }

    /**
     * Retrieves an angle flash effect for two lines intersecting at a point.
     *
     * @param panel the panel to display the flash effect
     * @param p     the intersection point
     * @param l1    the first line
     * @param l2    the second line
     * @return the angle flash effect
     */
    public JFlash getAngleFlashLL(JPanel panel, int p, LLine l1, LLine l2) {
        int a, b;

        if (p == l1.pt[0])
            a = l1.pt[1];
        else
            a = l1.pt[0];
        if (p == l2.pt[0])
            b = l2.pt[1];
        else
            b = l2.pt[0];
        JFlash f = getAngleFlash(panel, a, p, b, p);
        return f;
    }

    /**
     * Adds a flash effect for a geometric class.
     *
     * @param cc    the geometric class
     * @param panel the panel to display the flash effect
     */
    public void flashattr(gprover.CClass cc, JPanel panel) {
        if (cc == null)
            return;

        if (this.pointlist.size() == 0)
            return;

        if (cc instanceof Angles) {
            Angles ag = (Angles) cc;
            LLine ln1 = ag.l1;
            LLine ln2 = ag.l2;
            LLine ln3 = ag.l3;
            LLine ln4 = ag.l4;
            JFlash f = getAngleFlash(panel, ln1.pt[0], ln1.pt[1], ln2.pt[0], ln2.pt[1]);
            JFlash f1 = getAngleFlash(panel, ln3.pt[0], ln3.pt[1], ln4.pt[0], ln4.pt[1]);
            addFlash1(f);
            addFlash1(f1);
        } else if (cc instanceof LLine) {
            LLine ln = (LLine) cc;
            JLineFlash f = new JLineFlash(panel);
            int id = f.addALine();
            for (int i = 0; i <= ln.no; i++) {
                f.addAPoint(id, fd_point(ln.pt[i]));
            }
            addFlash(f);
        } else if (cc instanceof PLine) {
            PLine pn = (PLine) cc;
            JLineFlash f = new JLineFlash(panel);
            for (int i = 0; i <= pn.no; i++) {
                LLine ln = pn.ln[i];
                int nd = f.addALine();
                f.setInfinitLine(nd);
                for (int j = 0; j <= ln.no; j++) {
                    f.addAPoint(nd, fd_point(ln.pt[j]));
                }
            }
            addFlash(f);

        } else if (cc instanceof TLine) {
            TLine tn = (TLine) cc;
            LLine ln = tn.l1;
            LLine ln1 = tn.l2;
            JTLineFlash f = new JTLineFlash(panel);
            for (int i = 0; i <= ln.no; i++)
                f.ln1.addAPoint(fd_point(ln.pt[i]));
            for (int i = 0; i <= ln1.no; i++)
                f.ln2.addAPoint(fd_point(ln1.pt[i]));
            addFlash(f);
        } else if (cc instanceof ACir) {
            ACir ac = (ACir) cc;
            JCirFlash f = new JCirFlash(panel);
            f.setCenter(fd_point(ac.o));
            for (int i = 0; i <= ac.no; i++) {
                f.addAPoint(fd_point(ac.pt[i]));
            }
            addFlash(f);

        } else if (cc instanceof SimTri) {
            SimTri sm = (SimTri) cc;
            this.clearFlash();
            int cn = DrawData.LIGHTCOLOR;
            JTriFlash f = new JTriFlash(panel, fd_point(sm.p1[0]), fd_point(sm.p1[1]), fd_point(sm.p1[2]),
                    fd_point(sm.p2[0]), fd_point(sm.p2[1]), fd_point(sm.p2[2]),
                    sm.dr == 1, cn);
            addFlash1(f);

        } else if (cc instanceof CongSeg) {
            CongSeg cg = (CongSeg) cc;
            JCgFlash f = new JCgFlash(panel);
            f.addACg(fd_point(cg.p1), fd_point(cg.p2));
            f.addACg(fd_point(cg.p3), fd_point(cg.p4));
            addFlash(f);
        } else if (cc instanceof MidPt) {
            MidPt md = (MidPt) cc;
            JCgFlash f = new JCgFlash(panel);
            f.addACg(fd_point(md.a), fd_point(md.m));
            f.addACg(fd_point(md.b), fd_point(md.m));
            addFlash(f);
        } else if (cc instanceof RatioSeg) {
            RatioSeg ra = (RatioSeg) cc;
            JLineFlash f = new JLineFlash(panel);
            for (int i = 0; i < 4; i++) {
                int id = f.addALine();
                f.addAPoint(id, fd_point(ra.r[i * 2 + 1]));
                f.addAPoint(id, fd_point(ra.r[i * 2 + 2]));
            }
            f.setAlternate(true);
            addFlash(f);
        } else if (cc instanceof AngSt) {
            AngSt ag = (AngSt) cc;
            for (int i = 0; i < ag.no; i++) {
                JFlash f = getAngleFlash(panel, ag.ln1[i].pt[0], ag.ln1[i].pt[1],
                        ag.ln2[i].pt[0], ag.ln2[i].pt[1]);
                this.addFlash1(f);
            }
        } else if (cc instanceof AngleT) {
            AngleT at = (AngleT) cc;
            int a, b, p;
            p = at.p;
            if (p == at.l1.pt[0])
                a = at.l1.pt[1];
            else
                a = at.l1.pt[0];
            if (p == at.l2.pt[0])
                b = at.l2.pt[1];
            else
                b = at.l2.pt[0];
            JFlash f = getAngleFlash(panel, a, p, b, p);
            this.addFlash1(f);
        } else if (cc instanceof AngTn) {
            AngTn atn = (AngTn) cc;
            this.clearFlash();
            JFlash f1 = getAngleFlashLL(panel, atn.t1, atn.ln1, atn.ln2);
            JFlash f2 = getAngleFlashLL(panel, atn.t2, atn.ln3, atn.ln4);
            addFlash1(f1);
            addFlash1(f2);

        } else if (cc instanceof STris) {
            STris sm = (STris) cc;
            int n = sm.no;
            this.clearFlash();

            for (int i = 0; i < n; i++) {
                int cn = DrawData.LIGHTCOLOR;
                JTriFlash f = new JTriFlash(panel, fd_point(sm.p1[i]), fd_point(sm.p2[i]), fd_point(sm.p3[i]),
                        fd_point(sm.p1[i + 1]), fd_point(sm.p2[i + 1]), fd_point(sm.p3[i + 1]),
                        sm.dr[i] == sm.dr[i + 1], cn + i);
                addFlash1(f);
            }
        } else if (cc instanceof CSegs) {
            CSegs cg = (CSegs) cc;
            JCgFlash f = new JCgFlash(panel);
            for (int i = 0; i <= cg.no; i++) {
                f.addACg(fd_point(cg.p1[i]), fd_point(cg.p2[i]));
            }
            addFlash(f);
        } else if (cc instanceof AngTr) {

        } else if (cc instanceof LList) {
            clearFlash();
            LList ls = (LList) cc;
            for (int i = 0; i < ls.nd; i++) {
                AngTr t = ls.md[i].tr;
                if (t != null) {
                    JFlash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    addFlash1(f);
                }
            }
            Mnde m = ls.mf[0];
            if (m != null) {
                AngTr t = m.tr;
                if (t != null) {
                    JFlash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    f.setColor(Color.pink);
                    addFlash1(f);
                }
            }
        } else if (cc instanceof Rule) {
            clearFlash();
            Rule r = (Rule) cc;
            for (int i = 0; i < r.mr1.length; i++) {
                Mnde m = r.mr1[i];
                if (m == null)
                    continue;

                AngTr t = r.mr1[i].tr;
                if (t != null) {
                    JFlash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    addFlash1(f);
                }
            }
            Mnde m = r.mr;
            if (m != null) {
                AngTr t = m.tr;
                if (t != null) {
                    JFlash f = getAngleFlash(panel, t.get_lpt1(), t.v, t.v, t.get_lpt2());
                    f.setColor(Color.pink);
                    addFlash1(f);
                }
            }
        }

    }

    /**
     * Finds and returns a JAngleFlash from the flash list that matches the given four points.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @param d the fourth point
     * @return the matching JAngleFlash if found; otherwise, null
     */
    public JAngleFlash find_angFlash(CPoint a, CPoint b, CPoint c, CPoint d) {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash flash = (JFlash) flashlist.get(i);
            if (flash instanceof JAngleFlash) {
                JAngleFlash f = (JAngleFlash) flash;
                if (f.p1 == a && f.p2 == b && f.p3 == c && f.p4 == d) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Creates or retrieves a flash angle based on an XTerm object.
     * The flash is added to the flash list and its color is adjusted based on the PV value.
     *
     * @param x the XTerm object containing variable and point indices information
     * @return the created or restarted flash; null if the XTerm, its variable, or any required point is not found
     */
    public JFlash addFlashXtermAngle(XTerm x) {
        if (x == null) {
            return null;
        }
        Var v = x.var;
        if (v == null) {
            return null;
        }
        int a, b, c, d;
        if (x.getPV() > 0) {
            a = v.pt[0];
            b = v.pt[1];
            c = v.pt[2];
            d = v.pt[3];
        } else {
            a = v.pt[2];
            b = v.pt[3];
            c = v.pt[0];
            d = v.pt[1];
        }
        CPoint p1 = fd_point(a);
        CPoint p2 = fd_point(b);
        CPoint p3 = fd_point(c);
        CPoint p4 = fd_point(d);
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return null;
        }

        JFlash f = null;
        if ((f = find_angFlash(p1, p2, p3, p4)) != null) {
            f.start();
        } else {
            f = getAngleFlash(panel, a, b, c, d);
            this.addFlash1(f);
            if (x.getPV() < 0) {
                f.setColor(Color.magenta);
            }
            panel.repaint();
        }
        return f;
    }

    /**
     * Creates or retrieves a flash angle based on the given point indices.
     * The flash is added to the flash list and the panel is repainted.
     *
     * @param a the index of the first point
     * @param b the index of the second point
     * @param c the index of the third point
     * @param d the index of the fourth point
     * @return the created or restarted flash; null if any of the points is not found
     */
    public JFlash addFlashAngle(int a, int b, int c, int d) {
        CPoint p1 = fd_point(a);
        CPoint p2 = fd_point(b);
        CPoint p3 = fd_point(c);
        CPoint p4 = fd_point(d);
        if (p1 == null || p2 == null || p3 == null || p4 == null) {
            return null;
        }

        JFlash f = null;
        if ((f = find_angFlash(p1, p2, p3, p4)) != null) {
            f.start();
        } else {
            f = getAngleFlash(panel, a, b, c, d);
            this.addFlash1(f);
            panel.repaint();
        }
        return f;
    }

    /**
     * Processes auxiliary proof text to add the corresponding flashes, marks, or polygons.
     *
     * @param cpt the CProveText object containing condition and proof information
     */
    public void addaux(CProveText cpt) {
        Cond co = cpt.getcond();
        if (co == null) {
            return;
        }
        switch (co.pred) {
            case GDDBase.CO_COLL: {
                int[] p = new int[3];
                for (int i = 0; i < 3; i++) {
                    p[i] = co.p[i];
                }
                //  proveFlash(1, p, null);
                break;
            }
            case GDDBase.CO_PARA:
            case GDDBase.CO_PERP:
            case GDDBase.CO_CONG: {
                int[] p1 = new int[2];
                for (int i = 0; i < 2; i++) {
                    p1[i] = co.p[i];
                }
                int[] p2 = new int[2];
                for (int i = 2; i < 4; i++) {
                    p2[i - 2] = co.p[i];
                }
                if (co.pred == GDDBase.CO_PARA) {
                } else if (co.pred == GDDBase.CO_PERP) { // proveFlash(2, p1, p2);
                } else if (co.pred == GDDBase.CO_CONG) { //proveFlash(3, p1, p2);
                    if (p1[0] == p2[0] && p1[1] == p2[1] ||
                            p1[0] == p2[1] && p1[1] == p2[0]) {
                    } else {
                        Cedmark ce1 = this.addedMark(p1[0], p1[1]);
                        Cedmark ce2 = this.addedMark(p2[0], p2[1]);
                        aux_mark++;
                        ce1.setdnum(aux_mark);
                        ce2.setdnum(aux_mark);
                        ce1.setColor(aux_mark + 2);
                        ce2.setColor(aux_mark + 2);
                        Vector v = new Vector();
                        v.add(ce1);
                        v.add(ce2);
                        this.flashStep(v);
                    }
                }
                this.addLn(p1[0], p1[1]);
                this.addLn(p2[0], p2[1]);
            }
            break;
            case GDDBase.CO_ACONG: {
                int[] vp = co.p;
                if (vp[0] != 0 && false) {
                    CLine ln1 = this.addLn(co.p[0], co.p[1]);
                    CLine ln2 = this.addLn(co.p[2], co.p[3]);
                    CLine ln3 = this.addLn(co.p[4], co.p[5]);
                    CLine ln4 = this.addLn(co.p[6], co.p[7]);
                    int[] p = this.get4PtsForAngle(co.p);

                    CPoint p1 = fd_point(p[0]);
                    CPoint p2 = fd_point(p[1]);
                    CAngle ang = new CAngle(ln1, ln2, p1, p2);
                    p1 = fd_point(p[2]);
                    p2 = fd_point(p[3]);
                    CAngle ang1 = new CAngle(ln3, ln4, p1, p2);

                    CAngle ta = this.fd_angle(ang);
                    CAngle ta1 = this.fd_angle(ang1);

                    if (ta == null || ta1 == null) {
                        aux_angle++;
                    }
                    Vector v = new Vector();

                    String ss1, ss2;
                    ss1 = ss2 = null;
                    if (ta == null) {
                        ss1 = this.getAngleSimpleName();
                        ang.setColor(aux_angle);
                        ang.radius = ang.radius + 5 * aux_angle;
                        ang.ptext.setText(ss1);
                        ang.ptext.setColor(ang.getColorIndex());
                        this.addAngleToList(ang);
                        v.add(ang);
                    } else {
                        v.add(ta);
                        ss1 = ta.ptext.getText();
                    }
                    if (ta1 == null) {
                        ss2 = this.getAngleSimpleName();
                        ang1.setColor(aux_angle);
                        ang1.radius = ang1.radius + 5 * aux_angle;
                        ang1.ptext.setText(ss2);
                        ang1.ptext.setColor(ang1.getColorIndex());
                        this.addAngleToList(ang1);
                        v.add(ang1);
                    } else {
                        v.add(ta1);
                        ss2 = ta1.ptext.getText();
                    }
                    cpt.setMessage(ss1 + " = " + ss2);
                    this.flashStep(v);
                } else {
                    Cond c = co.getPCO();
                }

            }
            break;
            case GDDBase.CO_CTRI: {
                CPolygon poly1 = new CPolygon();
                aux_polygon++;
                poly1.setColor(aux_polygon + 2);
                for (int i = 0; i < 3; i++) {
                    poly1.addAPoint(fd_point(co.p[i]));
                }
                poly1.addAPoint(fd_point(co.p[0]));

                CPolygon poly2 = new CPolygon();
                aux_polygon++;
                poly2.setColor(aux_polygon + 2);
                for (int i = 3; i < 6; i++) {
                    poly2.addAPoint(fd_point(co.p[i]));
                }
                poly2.addAPoint(fd_point(co.p[3]));
                this.polygonlist.add(poly1);
                this.polygonlist.add(poly2);
                Vector v = new Vector();
                v.add(poly1);
                v.add(poly2);
                this.flashStep(v);
            }
            break;
            case GDDBase.CO_CYCLIC: {
                int[] p = new int[4];
                int k = 0;
                for (int i = 0; i < 10; i++) {
                    if (co.p[i] != 0) {
                        p[k++] = co.p[i];
                    }
                }
                CPoint p1 = this.fd_point(p[0]);
                CPoint p2 = this.fd_point(p[1]);
                CPoint p3 = this.fd_point(p[2]);
                CPoint p4 = this.fd_point(p[3]);

                Circle c = this.fd_circle(p[0], p[1], p[2]);
                if (c == null) {
                    CPoint pt = this.CreateANewPoint(0, 0);
                    Constraint cs = new Constraint(Constraint.CIRCUMCENTER, pt, p1,
                            p2, p3);
                    this.charsetAndAddPoly(false);
                    this.addPointToList(pt);
                    Circle cc = new Circle(pt, p1, p2, p3);
                    cc.addPoint(p4);
                    this.addCircleToList(cc);
                }
                //proveFlash(5, p, null);
                break;
            }

        }
        this.UndoAdded("step");
    }

    /**
     * Determines and returns an array of four point indices for defining an angle from the given array.
     * Adjustments are made using line intersections if the direct configuration is ambiguous.
     *
     * @param pt an array of point indices relevant to the angle
     * @return an array of four point indices that define the angle
     */
    int[] get4PtsForAngle(int[] pt) {
        int[] p = new int[4];
        boolean rt1, rt2;
        rt1 = rt2 = true;
        int c1, c2;
        c1 = c2 = 0;

        if (pt[0] == pt[2]) {
            p[0] = pt[1];
            p[1] = pt[3];
            c1 = pt[0];
        } else if (pt[0] == pt[3]) {
            p[0] = pt[1];
            p[1] = pt[2];
            c1 = pt[0];
        } else if (pt[1] == pt[2]) {
            p[0] = pt[0];
            p[1] = pt[3];
            c1 = pt[1];

        } else if (pt[1] == pt[3]) {
            p[0] = pt[0];
            p[1] = pt[3];
            c1 = pt[1];

        } else {
            p[0] = pt[0];
            p[1] = pt[3];
            rt1 = false;
        }

        if (pt[4] == pt[6]) {
            p[2] = pt[5];
            p[3] = pt[7];
            c1 = pt[4];
        } else if (pt[4] == pt[7]) {
            p[2] = pt[5];
            p[3] = pt[6];
            c1 = pt[4];
        } else if (pt[5] == pt[6]) {
            p[2] = pt[4];
            p[3] = pt[7];
            c1 = pt[5];
        } else if (pt[5] == pt[7]) {
            p[2] = pt[4];
            p[3] = pt[5];

        } else {
            p[2] = pt[4];
            p[3] = pt[7];
            rt2 = false;
        }

        if (rt1 && rt2) {
            return p;
        }

        CLine ln1 = this.fd_line(fd_point(pt[0]), fd_point(pt[1]));
        CLine ln2 = this.fd_line(fd_point(pt[2]), fd_point(pt[3]));
        CPoint pcm = CLine.commonPoint(ln1, ln2);
        if (pcm == null) {
            return p;
        }
        int nc1 = pointlist.indexOf(pcm) + 1;

        ln1 = this.fd_line(fd_point(pt[4]), fd_point(pt[5]));
        ln2 = this.fd_line(fd_point(pt[6]), fd_point(pt[7]));
        pcm = CLine.commonPoint(ln1, ln2);
        if (pcm == null) {
            return p;
        }
        int nc2 = pointlist.indexOf(pcm) + 1;

        for (int k = 0; k <= 1; k++) {
            for (int m = 2; m <= 3; m++) {
                double ct1 = this.cos3Pt(pt[k], pt[m], nc1);
                for (int i = 4; i <= 5; i++) {
                    for (int j = 6; j <= 7; j++) {
                        double ct2 = this.cos3Pt(pt[i], pt[j], nc2);
                        if (Math.abs(ct2 - ct1) < CMisc.ZERO) {
                            p[0] = pt[k];
                            p[1] = pt[m];
                            p[2] = pt[i];
                            p[3] = pt[j];
                            return p;
                        }
                    }
                }
            }
        }

        return p;
    }

    /**
     * Calculates the cosine of the angle formed by three points identified by their indices.
     *
     * @param a the index of the first point
     * @param b the index of the second point (vertex of the angle)
     * @param c the index of the third point
     * @return the cosine of the angle formed at point b
     */
    double cos3Pt(int a, int b, int c) {
        CPoint p1 = this.fd_point(a);
        CPoint p2 = this.fd_point(b);
        CPoint cp = this.fd_point(c);
        double dc = Math.sqrt(Math.pow(p1.getx() - p2.getx(), 2) +
                Math.pow(p1.gety() - p2.gety(), 2));
        double da = Math.sqrt(Math.pow(p1.getx() - cp.getx(), 2) +
                Math.pow(p1.gety() - cp.gety(), 2));
        double db = Math.sqrt(Math.pow(p2.getx() - cp.getx(), 2) +
                Math.pow(p2.gety() - cp.gety(), 2));
        double cs = (da * da + db * db - dc * dc) / (2 * da * db);
        return cs;
    }

    /**
     * Adds a CAngle to the angle list.
     * If an angle with similar intersection is already present, the radius is adjusted before adding.
     *
     * @param ag the CAngle object to be added
     */
    public void addAngleToList2(CAngle ag) {
        int num = 0;
        double[] p = CLine.Intersect(ag.lstart, ag.lend);
        if (p != null) {
            for (int k = 0; k < anglelist.size(); k++) {
                CAngle ag1 = (CAngle) anglelist.get(k);
                double[] pp = CLine.Intersect(ag1.lstart, ag1.lend);
                if (pp == null) {
                    continue;
                }
                if (Math.abs(p[0] - pp[0]) < CMisc.ZERO &&
                        Math.abs(p[1] - pp[1]) < CMisc.ZERO) {
                    num++;
                }
            }
            if (num != 0) {
                ag.setRadius(15 * num + ag.getRadius());
            }
        }
        this.addAngleToList(ag);
    }
}
