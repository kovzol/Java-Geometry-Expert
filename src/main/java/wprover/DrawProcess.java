package wprover;

import gprover.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.print.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import maths.TPoly;
import maths.TMono;
import maths.Param;
import maths.PolyBasic;
import maths.CharSet;
import org.w3c.dom.*;

/**
 * DrawProcess is a class that handles the drawing and processing of geometric objects.
 * It extends the DrawBase class and implements the Printable and ActionListener interfaces.
 */
public class DrawProcess extends DrawBase implements Printable, ActionListener {


    final public static int SELECT = 0;
    final public static int MOVE = 34;
    final public static int VIEWELEMENT = 35;

    final public static int TRANSLATE = 40;
    final public static int ZOOM_IN = 41;
    final public static int ZOOM_OUT = 42;
    final public static int SETTRACK = 43;
    final public static int ANIMATION = 60;
    final public static int DEFINEPOLY = 61;
    final public static int MULSELECTSOLUTION = 62;
    final public static int MOVENAME = 63;
    final public static int AUTOSHOWSTEP = 64;
    final public static int EQMARK = 65;
    final public static int PROVE = 66;
    final public static int TRIANGLE = 67;
    final public static int HIDEOBJECT = 68;

    final public static int DRAWTRIALL = 69; //
    final public static int DRAWTRISQISO = 71;

    final public static int PARALLELOGRAM = 72;
    final public static int RECTANGLE = 73;

    final public static int TRAPEZOID = 74;
    final public static int RA_TRAPEZOID = 75;
    final public static int SETEQSIDE = 76;
    final public static int SHOWOBJECT = 77;
    final public static int SETEQANGLE3P = 78;
    final public static int SETCCTANGENT = 79;
    final public static int NTANGLE = 80;
    final public static int SANGLE = 81;
    final public static int RATIO = 82;
    final public static int RAMARK = 83;
    final public static int TRANSFORM = 84;
    final public static int EQUIVALENCE = 85;
    final public static int FREE_TRANSFORM = 86;
    final public static int LOCUS = 87;

    final public static int ARROW = 88;

    final public static int CONSTRUCT_FROM_TEXT = 100;


    Vector undolist = new Vector();
    Vector redolist = new Vector();
    UndoStruct currentUndo = new UndoStruct(1);

    //    CPoint trackPoint = null;
    protected CPoint CTrackPt = null;

    AnimateC animate = null;
    CProveField cpfield = null;
    int pfn = 0; // max 4

    protected JPanel panel;
    private CPoint pSolution = null;
    private Vector solutionlist = new Vector();
    private CPoint FirstPnt = null;
    private CPoint SecondPnt = null;
    private CPoint ThirdPnt = null;


    private int proportion = 0;
    private UndoStruct undo = null;
    private Timer timer = null;
    private int timer_type; // 1: autoundoredo , 2: prove;

    private boolean IsButtonDown = false;
    private boolean isRecal = true;
    private int v1, v2;
    private double vx1, vy1, vangle = 0;
    private double vtrx, vtry = 0;

    private int PreviousAction;
    private Vector updaterListeners = new Vector();
    private boolean needSave = false;
    private int save_id = CMisc.id_count;
    private int CAL_MODE = 0; // 0: MOVEMODE. 1. CAL

    protected GTerm gt;
    protected int nd = 1;

    protected UndoStruct U_Obj = null;
    protected boolean status = true;

    /**
     * Toggles the status state.
     */
    public void stateChange() {
        status = !status;
    }

    /**
     * Sets the calculation mode to 1.
     */
    public void setCalMode1() {
        CAL_MODE = 1;
    }

    /**
     * Sets the calculation mode to 0.
     */
    public void setCalMode0() {
        CAL_MODE = 0;
    }

    /**
     * Retrieves the GTerm object.
     *
     * @return the GTerm object
     */
    public GTerm gterm() {
        if (gt == null)
            gt = gxInstance.getpprove().getConstructionTerm();
        return gt;
    }

    /**
     * Clears the construction and resets the nd value.
     */
    public void clearConstruction() {
        gt = null;
        nd = 1;
    }

    /**
     * Resets the current undo structure's ID to the current ID count.
     */
    public void resetUndo() {
        this.currentUndo.id = CMisc.id_count;
    }

    /**
     * Retrieves the name of the current object.
     *
     * @return the name of the current object
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the recalculation flag.
     *
     * @param r the recalculation flag to set
     */
    public void setRecal(boolean r) {
        isRecal = r;
    }

    /**
     * Sets the name of the current object.
     *
     * @param s the name to set
     */
    public void setName(String s) {
        this.name = s;
    }

    /**
     * Stops tracking the current point.
     */
    public void stopTrack() {
        CTrackPt = null;
    }

    /**
     * Starts tracking a given point.
     *
     * @param pt the point to start tracking
     */
    public void startTrackPt(CPoint pt) {
        CTrackPt = pt;

        boolean r = false;
        for (int i = 0; i < tracelist.size(); i++) {
            CTrace tr = (CTrace) tracelist.get(i);
            if (tr.isTracePt(CTrackPt)) {
                r = true;
                break;
            }
        }
        if (!r) {
            CTrace t = new CTrace(CTrackPt);
            this.addObjectToList(t, tracelist);
            this.UndoAdded(t.toString());
        }
    }


    /**
     * Retrieves a parameter by its index.
     *
     * @param index the index of the parameter to retrieve
     * @return the parameter with the specified index, or null if not found
     */
    public Param getParameterByindex(int index) {
        for (int i = 0; i < paraCounter - 1; i++) {
            if (parameter[i].xindex == index) {
                return parameter[i];
            }
        }
        return null;
    }

    /**
     * Retrieves the last constructed point.
     *
     * @return the last constructed point, or null if no points exist
     */
    public CPoint getLastConstructedPoint() {
        if (pointlist.size() <= 0)
            return null;
        return (CPoint) pointlist.get(pointlist.size() - 1);
    }

    /**
     * Retrieves a point by its ID.
     *
     * @param id the ID of the point to retrieve
     * @return the point with the specified ID, or null if not found
     */
    public CPoint getPointById(int id) {
        return (CPoint) this.getObjectInListById(id, pointlist);
    }

    /**
     * Retrieves all constraints.
     *
     * @return a vector containing all constraints
     */
    public Vector getAllConstraint() {
        return constraintlist;
    }

    /**
     * Retrieves a constraint by its ID.
     *
     * @param id the ID of the constraint to retrieve
     * @return the constraint with the specified ID, or null if not found
     */
    public Constraint getConstraintByid(int id) {
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.id == id) {
                return cs;
            }
        }
        return null;
    }

    /**
     * Retrieves a line by its ID.
     *
     * @param id the ID of the line to retrieve
     * @return the line with the specified ID, or null if not found
     */
    public CLine getLineByid(int id) {
        return (CLine) this.getObjectInListById(id, linelist);
    }

    /**
     * Retrieves a circle by its ID.
     *
     * @param id the ID of the circle to retrieve
     * @return the circle with the specified ID, or null if not found
     */
    public Circle getCircleByid(int id) {
        return (Circle) this.getObjectInListById(id, circlelist);
    }

    /**
     * Retrieves a trace by its ID.
     *
     * @param id the ID of the trace to retrieve
     * @return the trace with the specified ID, or null if not found
     */
    public CTrace getTraceById(int id) {
        return (CTrace) this.getObjectInListById(id, tracelist);
    }

    /**
     * Retrieves an angle by its ID.
     *
     * @param id the ID of the angle to retrieve
     * @return the angle with the specified ID, or null if not found
     */
    public CAngle getAngleByid(int id) {
        return (CAngle) this.getObjectInListById(id, anglelist);
    }

    /**
     * Retrieves all solid objects.
     *
     * @return a vector containing all solid objects
     */
    public Vector getAllSolidObj() {
        Vector v = new Vector();
        int n = CMisc.id_count + 1;
        for (int i = 1; i <= n; i++) {
            Object o = getOjbectById(i);
            if (o instanceof CText) {
                CText tt = (CText) o;
                if (tt.getType() != CText.NORMAL_TEXT)
                    continue;
            }
            if (o != null)
                v.add(o);
        }
        return v;
    }

    /**
     * Retrieves an object by its ID.
     *
     * @param id the ID of the object to retrieve
     * @return the object with the specified ID, or null if not found
     */
    public CClass getOjbectById(int id) {
        CClass cc = this.getObjectInListById(id, pointlist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, linelist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, circlelist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, anglelist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, distancelist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, polygonlist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, textlist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, tracelist);
        if (cc != null) {
            return cc;
        }
        cc = this.getObjectInListById(id, otherlist);
        return cc;
    }

    /**
     * Retrieves an object from a list by its ID.
     *
     * @param id the ID of the object to retrieve
     * @param v  the list to search
     * @return the object with the specified ID, or null if not found
     */
    public CClass getObjectInListById(int id, Vector v) {
        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            if (cc.m_id == id) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Retrieves an UndoStruct object by its ID.
     *
     * @param id the ID of the UndoStruct to retrieve
     * @return the UndoStruct object with the specified ID, or null if not found
     */
    public UndoStruct getUndoById(int id) {
        for (int i = 0; i < undolist.size(); i++) {
            UndoStruct cc = (UndoStruct) undolist.get(i);
            UndoStruct c1 = cc.getUndoStructByid(id);

            if (c1 != null) {
                return c1;
            }
        }
        CMisc.print("Can not find " + id + " in undo list");
        return null;
    }

    /**
     * Adds a DiagramUpdater listener to the list of updater listeners.
     *
     * @param d the DiagramUpdater listener to add
     */
    public void addDiagramUpdaterListener(DiagramUpdater d) {
        if (!updaterListeners.contains(d))
            updaterListeners.add(d);
    }

    /**
     * Removes a DiagramUpdater listener from the list of updater listeners.
     *
     * @param d the DiagramUpdater listener to remove
     */
    public void RemoveDiagramUpdaterListener(DiagramUpdater d) {
        updaterListeners.remove(d);
    }

    /**
     * Clears all geometric objects and resets the drawing state.
     */
    public void clearAll() {
        CurrentAction = SELECT;
        SelectList.clear();
        CatchList.clear();
        pointlist.clear();
        linelist.clear();
        circlelist.clear();
        clearAllConstraint();
        textlist.clear();
        distancelist.clear();
        tracelist.clear();
        polygonlist.clear();
        otherlist.clear();

        paraCounter = 1;
        FirstPnt = SecondPnt = null;
        IsButtonDown = false;
        polylist = null;
        pblist = null;
        anglelist.clear();
        pnameCounter = 0;
        plineCounter = 1;
        pcircleCounter = 1;
        STATUS = 0;
        pSolution = null;
        solutionlist.clear();

        undolist.clear();
        redolist.clear();
        CMisc.id_count = 1;

        this.currentUndo = new UndoStruct(this.paraCounter);
        CCoBox.resetAll();
        DrawData.setDefaultStatus();
        undo = null;
        animate = null;
        cpfield = null;

        this.clearFlash();
        if (gxInstance != null && gxInstance.hasAFrame()) {
            AnimatePanel ac = gxInstance.getAnimateDialog();
            if (ac != null)
                ac.stopA();
        }
        clearConstruction();
        for (int i = 0; i < parameter.length; i++) {
            parameter[i] = null;
        }
        for (int i = 0; i < paraBackup.length; i++) {
            paraBackup[i] = 0.0;
        }
        CTrackPt = null;

        file = null;
        vx1 = vy1 = 0.0;
        vtrx = vtry = 0;
        vangle = 0.0;
        CMisc.Reset();
        needSave = false;
        save_id = CMisc.id_count;
        poly.clearZeroN();
        name = "";
        CAL_MODE = 0;
        status = true;
    }

    /**
     * Sets the saved tag to indicate that the current state is saved.
     */
    public void setSavedTag() {
        needSave = false;
        save_id = CMisc.id_count;
    }

    /**
     * Checks if the current state is saved.
     *
     * @return true if the current state is saved, false otherwise
     */
    public boolean isitSaved() {
        return needSave || save_id >= CMisc.id_count;
    }

    /**
     * Retrieves the AnimateC object.
     *
     * @return the AnimateC object
     */
    public AnimateC getAnimateC() {
        return animate;
    }

    /**
     * Retrieves the current file.
     *
     * @return the current file
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the current file.
     *
     * @param f the file to set
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     * Retrieves the list of selected objects.
     *
     * @return the list of selected objects
     */
    public Vector getSelectList() {
        return SelectList;
    }

    /**
     * Sets the snap mode.
     *
     * @param snap true to enable snap mode, false to disable
     */
    public void SetSnap(boolean snap) {
        this.SNAP = snap;
    }

    /**
     * Retrieves the current status.
     *
     * @return the current status
     */
    public int getStatus() {
        return STATUS;
    }

    /**
     * Sets the current status.
     *
     * @param t the status to set
     */
    public void setStatus(int t) {
        STATUS = t;
    }

    /**
     * Checks if snap mode is enabled.
     *
     * @return true if snap mode is enabled, false otherwise
     */
    public boolean isSnap() {
        return this.SNAP;
    }

    /**
     * Sets the grid mode.
     *
     * @param grid true to enable grid mode, false to disable
     */
    public void SetGrid(boolean grid) {
        this.DRAWGRID = grid;
    }

    /**
     * Checks if grid mode is enabled.
     *
     * @return true if grid mode is enabled, false otherwise
     */
    public boolean isDrawGrid() {
        return this.DRAWGRID;
    }

    /**
     * Adjusts the mesh step size.
     *
     * @param add true to increase the mesh step size, false to decrease
     */
    public void setMeshStep(boolean add) {
        if (add) {
            this.GridX += 10;
            this.GridY += 10;
        } else {
            if (this.GridX < 20) {
                return;
            }
            this.GridX -= 10;
            this.GridY -= 10;
        }
    }

    /**
     * Retrieves the polynomial list.
     *
     * @return the polynomial list
     */
    public TPoly getPolyList() {
        return polylist;
    }

    /**
     * Retrieves the PB list.
     *
     * @return the PB list
     */
    public TPoly getPBList() {
        return pblist;
    }

    /**
     * Returns a vector containing copies of the TMono objects from the pblist.
     *
     * @return a vector of TMono objects.
     */
    public Vector getPBMono() {
        TPoly poly = pblist;
        GeoPoly basic = GeoPoly.getPoly(); //.getInstance();
        Vector vx = new Vector();

        TMono m1, m2;
        m1 = m2 = null;

        if (poly == null)
            return vx;

        Vector v = new Vector();
        while (poly != null) {
            m1 = poly.getPoly();
            if (m1 != null)
                v.add(0, basic.p_copy(m1));
            poly = poly.next;
        }

        return v;
    }

    // Get the nondegenerate conditions from the polynomials.
    // This is the simplest nondegenerate conditions.

    /**
     * Prints the nondegenerate conditions derived from the TPoly list.
     * Simplifies each TMono condition, prints each condition and the final combined condition.
     */
    public void printNDGS() {
        GeoPoly basic = GeoPoly.getPoly();
        CharSet set = CharSet.getinstance();
        basic.setRMCOEF(false);
        try {
            Vector v = getNDGS();
            Vector v1 = new Vector();
            for (int i = 0; i < v.size(); i++) {
                TMono m = (TMono) v.get(i);
                m = basic.simplify(m, parameter);
                if (m != null)
                    v1.add(m);
            }

            int n = v1.size();
            if (n == 0) {
                basic.setRMCOEF(true);
                return;
            }

            System.out.println("The polynomial of nondegenerate conditions:");
            for (int i = 0; i < n; i++) {
                TMono m = (TMono) v1.get(i);
                System.out.println("d" + i + " := " + basic.getExpandedPrint(m) + ";");
            }
            //System.out.println("The final condition after reduce is: ");
            System.out.print("\nND := ");
            for (int i = 0; i < n; i++) {
                if (i != 0)
                    System.out.print("*");
                System.out.print("d" + i);
            }
            System.out.println(";\nND := factor(ND);\n");
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        basic.setRMCOEF(true);


    }

    /**
     * Computes and returns the nondegenerate conditions for the current TPoly.
     * Extracts TMono elements from the pblist, pairs them based on degree,
     * computes and reduces the differences, and collects the resulting conditions.
     *
     * @return a vector of computed nondegenerate conditions.
     */
    public Vector getNDGS() {
        TPoly poly = pblist;
        GeoPoly basic = GeoPoly.getPoly(); //.getInstance();
        Vector vx = new Vector();

        TMono m1, m2;
        m1 = m2 = null;

        if (poly == null)
            return vx;
        int nn = poly.getPoly().x;

        Vector v = new Vector();
        while (poly != null) {
            m1 = poly.getPoly();
            if (m1 != null)
                v.add(0, m1);
            poly = poly.next;
        }

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

            if (m1 != null && m2 != null) {
                TMono t = basic.ll_delta(2 * n, m1, m2);
                t = reduce(t);
                if (basic.plength(t) == 1 && t.x == 0 && t.val.intValue() != 0) {
                } else
                    basic.ppush(t, vx);
            }
            if (m1 != null)
                v.remove(m1);
            if (m2 != null)
                v.remove(m2);
        }
        return vx;
    }

    /**
     * Recalculates the diagram by transforming all points and updating parameters.
     * Restores previous values if the recalculation fails, triggers diagram updates, and recalculates traces and texts.
     *
     * @return true if recalculation is successful, false otherwise.
     */
    public boolean reCalculate() {

        boolean success = true;
        if (paraCounter <= 2) return true;

        double x1, y1, sin, cos;
        x1 = y1 = 0;
        sin = 0;
        cos = 1.0;

        if (CMisc.POINT_TRANS) {
            int n = pointlist.size();
            if (n >= 1) {
                CPoint p1 = (CPoint) pointlist.get(0);
                x1 = p1.x1.value;
                y1 = p1.y1.value;
                for (int i = 0; i < pointlist.size(); i++) {
                    CPoint p = (CPoint) pointlist.get(i);
                    p.x1.value = p.x1.value - x1;
                    p.y1.value = p.y1.value - y1;
                }
            }
            if (n >= 2) {
                CPoint p2 = (CPoint) pointlist.get(1);
                double t1 = p2.getx();
                double t2 = p2.gety();
                double r = Math.sqrt(t1 * t1 + t2 * t2);
                if (r == 0.0) {
                    sin = 0.0;
                    cos = 1.0;
                } else {
                    sin = t1 / r;
                    cos = t2 / r;
                    for (int i = 1; i < pointlist.size(); i++) {
                        CPoint p = (CPoint) pointlist.get(i);
                        t1 = p.getx();
                        t2 = p.gety();
                        p.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
                    }
                }
            }
        }
//        for (int i = 0; i <= this.paraCounter; i++) {
//            if (parameter[i] != null) {
//                System.out.println("x" + parameter[i].xindex + " = " + parameter[i].value);
//            }
//        }
        calv_parameter();

        for (int dx = 0; dx < pointlist.size(); dx++) {
            CPoint p = (CPoint) pointlist.get(dx);
            if (!(success = calculate_a_point(p, true)))
                break;
        }

//        backup_parameter(success);
        {
            if (success == false) {
                for (int i = 0; i < paraCounter; i++) {
                    if (parameter[i] != null)
                        parameter[i].value = paraBackup[i];
                }
                x1 = pptrans[0];
                y1 = pptrans[1];
                sin = pptrans[2];
                cos = pptrans[3];
            } else {
                for (int i = 0; i < paraCounter; i++) {
                    if (parameter[i] != null)
                        paraBackup[i] = parameter[i].value;
                }
                pptrans[0] = x1;
                pptrans[1] = y1;
                pptrans[2] = sin;
                pptrans[3] = cos;
            }
        }

        translate_back(x1, y1, sin, cos);

        for (int i = 0; i < updaterListeners.size(); i++) {
            DiagramUpdater d = (DiagramUpdater) updaterListeners.get(i);
            d.UpdateDiagram();
        }
        calculate_trace();
        recal_allFlash();
        calculate_text();

        return success;
    }

    /**
     * Calculates the text values for all CText objects in the text list.
     * If the text type is VALUE_TEXT, it calculates the value and updates the text value.
     */
    public void calculate_text() {
        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            if (t.getType() == CText.VALUE_TEXT) {
                double r = calculate(t.tvalue);
                t.tvalue.dvalue = roundn(r, t.m_dash);
                if (t.father != null) {
                    CClass c = t.father;
                    if (c instanceof CPolygon) {
                        CPolygon p1 = (CPolygon) c;
                        r = p1.getArea();
                    }
                }
                t.tvalue.dvalue = roundn(r, t.m_dash);
            }
        }
    }

    /**
     * Translates all points back to their original positions after transformation.
     *
     * @param x1  the x-coordinate translation
     * @param y1  the y-coordinate translation
     * @param sin the sine of the rotation angle
     * @param cos the cosine of the rotation angle
     */
    public void translate_back(double x1, double y1, double sin, double cos) {
        if (CMisc.POINT_TRANS) {
            double t1, t2;
            sin = -sin;
            for (int i = 1; i < pointlist.size(); i++) {
                CPoint p = (CPoint) pointlist.get(i);
                t1 = p.getx();
                t2 = p.gety();
                p.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
            }

            for (int i = 0; i < pointlist.size(); i++) {
                CPoint p = (CPoint) pointlist.get(i);
                p.x1.value += x1;
                p.y1.value += y1;
            }
        }
    }

    /**
     * Recalculates all flash animations in the flash list.
     */
    public void recal_allFlash() {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            f.recalculate();
        }
    }

    /**
     * Calculates the trace points for all CTrace objects in the trace list.
     */
    public void calculate_trace() {
        int nt = tracelist.size();
        if (nt == 0)
            return;

        CAL_MODE = 1;

        for (int i = 0; i < nt; i++) {
            CTrace t = (CTrace) tracelist.get(i);
            CPoint p = t.getPoint();
            CPoint po = t.getonPoint();
            if (p == null || po == null)
                continue;

            CClass c = t.getOnObject();
            int n = t.getPointSize();
            double xs = po.getx();
            double ys = po.gety();

            if (c instanceof CLine) {
                CLine ln = (CLine) c;
                CPoint[] lpt = ln.getMaxMinPoint(false);

                double x0 = lpt[0].getx();
                double y0 = lpt[0].gety();
                double x, y, dx, dy;
                x = x0;
                y = y0;
                dx = (lpt[1].getx() - lpt[0].getx()) / n;
                dy = (lpt[1].gety() - lpt[0].gety()) / n;

                for (int j = 0; j < n; j++) {
                    double xt = x + dx * j;
                    double yt = y + dy * j;
                    po.setXY(xt, yt);
                    calculate_allpt(false);
                    t.addTracePoint(j, p.getx(), p.gety());
                }
            } else if (c instanceof Circle) {
                Circle cr = (Circle) c;
                double r = cr.getRadius();
                double a = Math.PI * 2 / n;
                double ox = cr.o.getx();
                double oy = cr.o.gety();
                for (int j = 0; j < n; j++) {
                    double sinx = Math.sin(a * j);
                    double cosx = Math.cos(a * j);
                    double xt = r * cosx + ox;
                    double yt = r * sinx + oy;
                    po.setXY(xt, yt);
                    calculate_allpt(false);
                    t.addTracePoint(j, p.getx(), p.gety());
                }
                t.softEdge();
            }
            po.setXY(xs, ys);
        }
        calculate_allpt(true);

        CAL_MODE = 0;
    }

    /**
     * Retrieves the current parameter values.
     *
     * @return an array of parameter values
     */
    public double[] getParameter() {
        double[] r = new double[parameter.length];
        for (int i = 0; i < paraCounter; i++) {
            if (parameter[i] != null)
                r[i] = parameter[i].value;
        }
        return r;
    }

    /**
     * Sets the parameter values.
     *
     * @param r an array of parameter values to set
     */
    public void setParameter(double[] r) {
        for (int i = 0; i < paraCounter; i++) {
            if (parameter[i] != null)
                parameter[i].value = r[i];
        }
    }

    /**
     * Backs up or restores the parameter values.
     *
     * @param rr an array to store or restore parameter values
     * @param b  a boolean indicating whether to back up (true) or restore (false) the values
     */
    public void BackupParameter(double[] rr, boolean b) {
        if (b)
            for (int i = 0; i < paraCounter; i++) {
                if (parameter[i] != null)
                    rr[i] = parameter[i].value;
            }
        else
            for (int i = 0; i < paraCounter; i++) {
                if (parameter[i] != null)
                    parameter[i].value = rr[i];
            }
    }

    /**
     * Sets the parameter values and translates points back if necessary.
     *
     * @param dd an array of parameter values to set
     */
    public void setParameterValue(double[] dd) {
        for (int i = 0; i < dd.length; i++) {
            if (parameter[i] != null)
                parameter[i].value = dd[i];
        }

        if (CMisc.POINT_TRANS) {
            double x1 = pptrans[0];
            double y1 = pptrans[1];
            double sin = pptrans[2];
            double cos = pptrans[3];
            this.translate_back(x1, y1, sin, cos);
        }
    }

    /**
     * Calculates all results from the polygons.
     *
     * @return a Vector containing result arrays for each parameter configuration.
     */
    public Vector calculate_allResults() {     // calculate all results from the polygons.
        double x1, y1, sin, cos;
        x1 = y1 = 0;
        sin = 0;
        cos = 1.0;

        if (CMisc.POINT_TRANS) {
            int n = pointlist.size();
            if (n >= 1) {
                CPoint p1 = (CPoint) pointlist.get(0);
                x1 = p1.x1.value;
                y1 = p1.y1.value;
                for (int i = 0; i < pointlist.size(); i++) {
                    CPoint p = (CPoint) pointlist.get(i);
                    p.x1.value = p.x1.value - x1;
                    p.y1.value = p.y1.value - y1;
                }
            }
            if (n >= 2) {
                CPoint p2 = (CPoint) pointlist.get(1);
                double t1 = p2.getx();
                double t2 = p2.gety();
                double r = Math.sqrt(t1 * t1 + t2 * t2);
                sin = t1 / r;
                cos = t2 / r;
                for (int i = 1; i < pointlist.size(); i++) {
                    CPoint p = (CPoint) pointlist.get(i);
                    t1 = p.getx();
                    t2 = p.gety();
                    p.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
                }
            }
        }

        for (int i = 0; i < paraCounter; i++) {
            if (parameter[i] != null)
                paraBackup[i] = parameter[i].value;
        }


        Vector vlist = new Vector();
        int n = paraCounter;
        double[] rr = new double[n];
        vlist.add(rr);


        for (int t = 0; t < paraCounter; t++) {
            Param pm = parameter[t];
            if (pm == null)
                continue;
            TMono m1 = pm.m;


            for (int k = 0; k < vlist.size(); k++) {
                double[] rt = (double[]) vlist.get(k);
                if (m1 == null) {
                    rt[t] = parameter[t].value;
                    continue;
                }
                for (int m = 0; m < t; m++)
                    parameter[m].value = rt[m];
                double[] result = result = calcu_m1(m1);
                if (result == null || result.length == 0) {
                    rt[t] = parameter[t].value;
                } else if (result.length == 1) {
                    rt[t] = result[0];
                } else {
                    rt[t] = result[0];
                    for (int i = 1; i < result.length; i++) {
                        double[] r2 = new double[n];
                        for (int c = 0; c < t; c++) {
                            r2[c] = rt[c];
                        }
                        r2[t] = result[i];
                        vlist.add(k, r2);
                        k++;
                    }
                }
            }
        }


        for (int i = 0; i < vlist.size(); i++) {  // remove the common point.
            double[] kk = (double[]) vlist.get(i);
            for (int j = 0; j < n; j++) {
                if (parameter[j] != null)
                    parameter[j].value = kk[j];
            }

            boolean bk = false;
            for (int m = 0; m < pointlist.size(); m++) {
                CPoint p1 = (CPoint) pointlist.get(m);
                for (int n1 = m + 1; n1 < pointlist.size(); n1++) {
                    CPoint p2 = (CPoint) pointlist.get(n1);
                    if (Math.abs(p1.x1.value - p2.x1.value) < CMisc.ZERO
                            && Math.abs(p1.y1.value - p2.y1.value) < CMisc.ZERO) {
                        bk = true;
                        break;
                    }
                }
                if (bk)
                    break;
            }
            if (bk) {
                vlist.remove(i);
                i--;
            }
        }

        for (int i = 0; i < paraCounter; i++) {     // restor the previous data.
            if (parameter[i] != null)
                parameter[i].value = paraBackup[i];
        }
        translate_back(x1, y1, sin, cos);
        return vlist;
    }

    /**
     * Calculates all points based on current parameter values.
     *
     * @param d a flag indicating whether to perform dynamic recalculations
     * @return true if all points are calculated successfully; false otherwise.
     */
    public boolean calculate_allpt(boolean d) {
        double x1, y1, sin, cos;
        x1 = y1 = 0;
        sin = 0;
        cos = 1.0;

        if (CMisc.POINT_TRANS) {
            int n = pointlist.size();
            if (n >= 1) {
                CPoint p1 = (CPoint) pointlist.get(0);
                x1 = p1.x1.value;
                y1 = p1.y1.value;
                for (int i = 0; i < pointlist.size(); i++) {
                    CPoint p = (CPoint) pointlist.get(i);
                    p.x1.value = p.x1.value - x1;
                    p.y1.value = p.y1.value - y1;
                }
            }
            if (n >= 2) {
                CPoint p2 = (CPoint) pointlist.get(1);
                double t1 = p2.getx();
                double t2 = p2.gety();
                double r = Math.sqrt(t1 * t1 + t2 * t2);
                sin = t1 / r;
                cos = t2 / r;
                for (int i = 1; i < pointlist.size(); i++) {
                    CPoint p = (CPoint) pointlist.get(i);
                    t1 = p.getx();
                    t2 = p.gety();
                    p.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
                }
            }
        }
        boolean s = true;
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (!(s = calculate_a_point(p, d)))
                break;
        }

        this.translate_back(x1, y1, sin, cos);
        return s;
    }

    /**
     * Opens the dialog for selecting the leading variable.
     */
    public void popLeadingVariableDialog() {
        LeadVariableDialog dlg = new LeadVariableDialog(gxInstance);
        dlg.loadVariable(this.getPointList(), false);
        dlg.setVisible(true);
    }

    /**
     * Calculates and updates parameter values based on specific angle constraints.
     */
    public void calv_parameter() {
        int n = 0;
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGEL) {
                n++;
            }
        }
        for (int i = 0; i < n; i++) {
            TMono m = polylist.getPoly();
            int x = poly.lv(m);
            double[] r = poly.calculv(m, parameter);
            if (r != null) {
                for (int j = 0; j < r.length; j++) {
                    if (r[j] > 0) {
                        parameter[x - 1].value = r[j];
                        continue;
                    }
                }
            }
        }


    }

    /**
     * Finds and returns the circle on which the specified point lies.
     *
     * @param pt the point to check
     * @return the Circle if the point is on a circle; null otherwise.
     */
    public Circle fd_pt_on_which_circle(CPoint pt) {
        for (int i = 0; i < circlelist.size(); i++) {
            Circle cr = (Circle) circlelist.get(i);
            if (cr.getSidePoint() != null) {
                CPoint o = cr.o;
                CPoint p1 = cr.getSidePoint();
                if (p1.x1.xindex < pt.x1.xindex && o.x1.xindex < pt.x1.xindex)
                    return cr;
            }
        }

        return null;
    }

    /**
     * Finds and returns the line on which the specified point lies.
     *
     * @param pt the point to check
     * @return the CLine if the point is on a line; null otherwise.
     */
    public CLine fd_pt_on_which_line(CPoint pt) {
        for (int i = 0; i < linelist.size(); i++) {
            CLine ln = (CLine) linelist.get(i);
            if (ln.containPT(pt) && ln.getPtsSize() >= 2) {
                CPoint p1 = ln.getfirstPoint();
                CPoint p2 = ln.getSecondPoint(p1);
                if (p1.x1.xindex < pt.x1.xindex && p2.x1.xindex < pt.x1.xindex)
                    return ln;
            }
        }

        return null;
    }

    /**
     * Calculates the corresponding point on a circle.
     *
     * @param pt the point used for the calculation
     * @return an array containing x and y coordinates of the calculated point, or null if not applicable.
     */
    public double[] calculate_ocir(CPoint pt) {
        if (this.CurrentAction == MOVE && this.SelectList.contains(pt) || CAL_MODE == 1)
            return null;

        Circle cr = this.fd_pt_on_which_circle(pt);
        if (cr != null) {
            CPoint p1 = cr.o;
            CPoint p2 = cr.getSidePoint();
            double xt = paraBackup[pt.x1.xindex - 1];
            double yt = paraBackup[pt.y1.xindex - 1];
            double x1 = paraBackup[p1.x1.xindex - 1];
            double y1 = paraBackup[p1.y1.xindex - 1];
            double x2 = paraBackup[p2.x1.xindex - 1];
            double y2 = paraBackup[p2.y1.xindex - 1];

            if (check_eqdistance(x1, y1, xt, yt, x1, y1, x2, y2)) {
                double rr = CAngle.get3pAngle(x2, y2, x1, y1, xt, yt);
                rr -= Math.PI;

//                System.out.println(" " + rr);
                double cos = Math.cos(rr);
                double sin = Math.sin(rr);
                double dx = p2.getx() - p1.getx();
                double dy = p2.gety() - p1.gety();

                double[] r = new double[2];
                r[0] = p1.getx() + dx * cos - dy * sin;
                r[1] = p1.gety() + dx * sin + dy * cos;
                return r;
            }
        }
        return null;
    }

    /**
     * Calculates the intersection point on a line based on the given point.
     *
     * @param pt the point used for the calculation
     * @return an array containing x and y coordinates of the calculated intersection point, or null if not applicable.
     */
    public double[] calculate_oline(CPoint pt) {
        if (this.CurrentAction == MOVE && this.SelectList.contains(pt) || CAL_MODE == 1)
            return null;

        CLine ln = this.fd_pt_on_which_line(pt);
        if (ln != null) {
            CPoint p1 = ln.getfirstPoint();
            CPoint p2 = ln.getSecondPoint(p1);
            double xt = paraBackup[pt.x1.xindex - 1];
            double yt = paraBackup[pt.y1.xindex - 1];
            double x1 = paraBackup[p1.x1.xindex - 1];
            double y1 = paraBackup[p1.y1.xindex - 1];
            double x2 = paraBackup[p2.x1.xindex - 1];
            double y2 = paraBackup[p2.y1.xindex - 1];

            if (check_Collinear(xt, yt, x1, y1, x2, y2)) {
                double d1 = xt - x1;
                double d2 = x2 - xt;
                if (isZero(d1) || isZero(d2) || isZero(d1 + d2)) {
                    d1 = yt - y1;
                    d2 = y2 - yt;
                }

                double d = d1 + d2;
                double x = (p1.getx() * d2 + p2.getx() * d1) / d;
                double y = (p1.gety() * d2 + p2.gety() * d1) / d;
                double[] r = new double[2];
                r[0] = x;
                r[1] = y;
                return r;
            }
        }
        return null;
    }

    /**
     * Calculates and adjusts the given point based on a line-circle or circle-circle constraint.
     *
     * @param cp the point to adjust
     * @param r  an array containing candidate coordinates
     * @return true if the point is set successfully according to constraints; false otherwise.
     */
    public boolean calculate_lccc(CPoint cp, double[] r) {
        Param pm1 = cp.x1;
        Param pm2 = cp.y1;

        TMono m1 = pm1.m;
        TMono m2 = pm2.m;
        if (!(m1 != null && m2 != null && m1.deg == 2 && m2.deg == 1)) {
            return false;
        }

        int type = 0;
        Constraint cs = null;

        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint c = (Constraint) constraintlist.get(i);
            type = c.GetConstraintType();

            if (c.getelement(0) == cp && (type == Constraint.INTER_LC
                    || type == Constraint.INTER_CC)) {

                cs = c;
                break;
            }
        }
        if (cs == null)
            return false;
        if (this.CurrentAction == MOVE && SelectList.contains(cp)) {
            cs.proportion = 1;
            return false;
        }

        if (type == Constraint.INTER_LC) {
            CLine ln = (CLine) cs.getelement(1);
            Circle cr = (Circle) cs.getelement(2);
            CPoint p1 = ln.getfirstPoint();
            CPoint p2 = ln.getSecondPoint(p1);
            if (p1 == null || p2 == null || p2 == cp)
                return false;
            CPoint o = cr.o;
            double xt = paraBackup[cp.x1.xindex - 1];
            double yt = paraBackup[cp.y1.xindex - 1];
            double x1 = paraBackup[p1.x1.xindex - 1];
            double y1 = paraBackup[p1.y1.xindex - 1];
            double x2 = paraBackup[p2.x1.xindex - 1];
            double y2 = paraBackup[p2.y1.xindex - 1];
            double xo = paraBackup[o.x1.xindex - 1];
            double yo = paraBackup[o.y1.xindex - 1];

            double k = (y2 - y1) / (x2 - x1);
            double k1 = -(x2 - x1) / (y2 - y1);

            double mx = (yo - y1 + k * x1 - k1 * xo) / (k - k1);
            double my = y1 + k * (mx - x1);

            double area = signArea(xt, yt, mx, my, xo, yo);
            double area1 = signArea(r[0], r[1], mx, my, o.getx(), o.gety());
            double area2 = signArea(r[2], r[3], mx, my, o.getx(), o.gety());

            int n = cs.proportion;
            if (n == 1) {
                if (isZero(area))
                    return false;
                if (area > 0)
                    cs.proportion = 2;
                else if (area < 0)
                    cs.proportion = 3;


            }
            if (cs.proportion == 2) {
                if (area1 > 0) {
                    cp.setXY(r[0], r[1]);
                    return true;
                } else if (area2 > 0) {
                    cp.setXY(r[2], r[3]);
                    return true;
                }
            } else if (cs.proportion == 3) {
                if (area1 < 0) {
                    cp.setXY(r[0], r[1]);
                    return true;
                } else if (area2 < 0) {
                    cp.setXY(r[2], r[3]);
                    return true;
                }
            }


        } else if (type == Constraint.INTER_CC) {
            Circle cr1 = (Circle) cs.getelement(1);
            Circle cr2 = (Circle) cs.getelement(2);
            CPoint o1 = cr1.o;
            CPoint o2 = cr2.o;

            double xt = paraBackup[cp.x1.xindex - 1];
            double yt = paraBackup[cp.y1.xindex - 1];
            double x1 = paraBackup[o1.x1.xindex - 1];
            double y1 = paraBackup[o1.y1.xindex - 1];
            double x2 = paraBackup[o2.x1.xindex - 1];
            double y2 = paraBackup[o2.y1.xindex - 1];

            int n = cs.proportion;
            double area = signArea(xt, yt, x1, y1, x2, y2);
            double area1 = signArea(r[0], r[1], o1.getx(), o1.gety(), o2.getx(), o2.gety());
            double area2 = signArea(r[2], r[3], o1.getx(), o1.gety(), o2.getx(), o2.gety());

            if (n == 1) {
                if (area > 0)
                    cs.proportion = 2;
                else if (area < 0)
                    cs.proportion = 3;
                else return false;
            }
            //          System.out.println(" " + cs.proportion);

            if (cs.proportion == 2) {
                if (area1 > 0) {
                    cp.setXY(r[0], r[1]);
                    return true;
                } else if (area2 > 0) {
                    cp.setXY(r[2], r[3]);
                    return true;
                }
            } else if (cs.proportion == 3) {
                if (area1 < 0) {
                    cp.setXY(r[0], r[1]);
                    return true;
                } else if (area2 < 0) {
                    cp.setXY(r[2], r[3]);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates the coordinates for a given point using its defining parameters and constraints.
     *
     * @param p the point to calculate
     * @param d a flag that, if true, forces dynamic recalculation using constraint equations
     * @return true if the point is calculated successfully; false otherwise.
     */
    public boolean calculate_a_point(CPoint p, boolean d) {
        if (p == null || p.isAFreePoint())
            return true;
        CPoint cp = p;
        Param pm1 = cp.x1;
        Param pm2 = cp.y1;

        TMono m1 = pm1.m;
        TMono m2 = pm2.m;

        if (m1 != null && m2 == null && poly.deg(m1) == 1 || m1 == null && m2 != null && poly.deg(m2) == 1) {
            double[] r = calculate_oline(cp);
            if (r != null) {
                cp.x1.value = r[0];
                cp.y1.value = r[1];
                return true;
            }
        }
        if (m1 == null && m2 != null && poly.deg(m2) == 2) {
            double[] r = calculate_ocir(cp);
            if (r != null) {
                cp.x1.value = r[0];
                cp.y1.value = r[1];
                return true;
            }
        }


        if (m1 == null && m2 != null && d) {
            double[] r = null;
            int v = poly.deg(m2);
            if (v == 1) {
                r = poly.calculate_online(m2, parameter, cp.x1.xindex, cp.y1.xindex);
            } else if (v == 2)
                r = poly.calculate_oncr(m2, parameter, cp.x1.xindex, cp.y1.xindex);
            if (r != null) {
                cp.x1.value = r[0];
                cp.y1.value = r[1];
                return true;
            }
        }


        int va;
        int vb;
        double[] result = null;
        boolean success = true;
        if (m1 != null) {
            while (true) {
                result = calcu_m1(m1);
                va = poly.deg(m1);
                if (result == null || result.length != 0)
                    break;
                if (m1.next == null)
                    break;
                m1 = m1.next;
            }
        } else {
            va = 1;
            result = new double[1];
            result[0] = cp.x1.value;
        }

        if (m2 == null) {
            vb = 1;
        } else {
            vb = poly.deg(m2);
        }


        if (result == null) {
            success = false;
            return success;
        } else if (result.length == 1 && vb == 1) {
            double oldx = cp.x1.value;
            double oldy = cp.y1.value;

            cp.x1.value = result[0];

            if (m2 != null) {
                double[] result2 = calcu_m1(m2);
                if (result2.length == 0) {
                    result2 = calform(poly.lv(m2), parameter);
                }
                if (result2 == null) {
                    success = false;
                    return success;
                } else if (result2.length == 1) {
                    cp.y1.value = result2[0];
                } else {
                    double nx = oldy;
                    double ds = Double.MAX_VALUE;
                    for (int i = 0; i < result2.length; i++) {
                        if (p.check_xy_valid(result[0], result2[i])) {
                            double dlen = Math.pow(oldy - result2[i], 2);
                            if (dlen < ds) {
                                ds = dlen;
                                nx = result2[i];
                            }
                        }
                    }
                    cp.y1.value = nx;
                }

            }
        } else { //if (result.length > 1)
            int index = 0;
            double oldx = cp.x1.value;
            double oldy = cp.y1.value;

            double[] r = new double[va * vb * 2];
            double[] result2 = null;
            double ox, oy;
            boolean boy = false;
            ox = oy = 0.0;

            for (int i = 0; i < result.length; i++) {
                if (m2 != null) {
                    cp.x1.value = result[i];
                    ox = result[i];
                    result2 = this.calcu_m1(m2);
                    if (result2 == null || result2.length == 0) {
                        result2 = this.calform(p.y1.xindex, parameter);
                    }
                    if (result2 != null && result2.length >= 1) {
                        for (int k = 0; k < result2.length; k++) {
                            cp.y1.value = result2[k];

                            if (!boy) {
                                oy = result2[k];
                                boy = true;
                            }
                            if (isPointAlreadyExists(cp) == null) {
                                if (2 * index < r.length) {
                                    r[2 * index] = result[i];
                                    r[2 * index + 1] = result2[k];
                                    index++;
                                }
                            }
                        }
                    }

                } else {
                    r[2 * index] = result[i];
                    r[2 * index + 1] = cp.y1.value;
                    index++;
                }
            }

            if (index == 0) {
                if (boy) {
                    r[0] = ox;
                    r[1] = oy;
                } else
                    return false;
            }
            if (index == 1) {
                cp.x1.value = r[0];
                cp.y1.value = r[1];
            } else {
                if (index == 2 && this.calculate_lccc(cp, r)) {
                } else {
                    int t = -1;
                    double dis = Double.POSITIVE_INFINITY;

                    for (int i = 0; i < index; i++) {
                        if (p.check_xy_valid(r[2 * i], r[2 * i + 1])) {
                            double ts = Math.pow(oldx - r[2 * i], 2) + Math.pow(oldy - r[2 * i + 1], 2);
                            if (ts < dis) {
                                dis = ts;
                                t = i;
                            }
                        }
                    }
                    if (t >= 0) {
                        cp.x1.value = r[2 * t];
                        cp.y1.value = r[2 * t + 1];
                    }

                }
            }
        }
        return success;
    }

    /**
     * Calculates the values of a polynomial.
     * <p>
     * This method calculates the values of a given polynomial using the provided parameters.
     * If the result is null, it attempts to find the polynomial in the polynomial list and calculate its values.
     *
     * @param m the polynomial to calculate
     * @return an array of calculated values, or null if the calculation fails
     */
    public double[] calcu_m1(TMono m) {
        double[] result = poly.calculv(m, parameter);

        if (result != null && result.length == 0) {
            TMono mx = m.next;
            if (mx != null) {
                if (poly.deg(mx) != 0)
                    result = poly.calculv(mx, parameter);
            }
        }

        int lva = poly.lv(m);
        if (result == null) {
            if (lva < 1)
                return null;

            TPoly plist = pblist;
            TMono m1 = null;
            TMono m2 = null;
            int d = poly.deg(m, lva);

            while (plist != null) {
                if (poly.lv(plist.getPoly()) == lva) {
                    if (m1 == null) {
                        m1 = plist.getPoly();
                    } else {
                        m2 = plist.getPoly();
                    }
                }
                plist = plist.getNext();
            }

            if (m1 == null && m2 == null) {
                return null;
            }
            if (m1 != null && m2 != null) {
                result = poly.calculv2poly(m1, m2, parameter);
            } else if (d == 1) {
                m = m1;
                if (m1 == null)
                    m = m2;

                double[] r = poly.calculv_2v(m, parameter);
                if (r != null && r.length != 0) {
                    parameter[lva - 2].value = r[0];
                }
                return null;
            }
        }
        return result;
    }

    /**
     * Backs up the current parameter values.
     * <p>
     * This method saves the current values of the parameters into the backup array.
     */
    public void pushbackup() {
        for (int i = 0; i < paraCounter; i++) {
            if (parameter[i] != null) {
                paraBackup[i] = parameter[i].value;
            }
        }
    }

    /**
     * Calculates the values of two polynomials.
     * <p>
     * This method calculates the values of two polynomials with the same leading variable.
     *
     * @param lv the leading variable
     * @param p  the array of parameters
     * @return an array of calculated values, or null if the calculation fails
     */
    public double[] calform(int lv, Param p[]) {
        TPoly plist = pblist;
        TMono m1, m2;
        m1 = m2 = null;
        int n = 0;

        while (plist != null) {
            if (poly.lv(plist.getPoly()) == lv) {
                if (m1 == null) {
                    m1 = plist.getPoly();
                } else {
                    m2 = plist.getPoly();
                }
                n++;
            }
            plist = plist.getNext();
        }
        if (m1 == null || m2 == null) {
            return null;
        }

        double[] result;
        result = poly.calculv2poly(m1, m2, p);
        return result;
    }

    /**
     * Adds polynomials to the list and optimizes them.
     * <p>
     * This method adds polynomials to the list, optimizes them, and recalculates the values if necessary.
     *
     * @param calcu a boolean indicating whether to recalculate the values
     */
    public void charsetAndAddPoly(boolean calcu) {
        TPoly plist = Constraint.getPolyListAndSetNull();
        TPoly plist2 = plist;

        if (plist2 == null)
            return;

        while (plist2 != null) {
            pblist = poly.ppush(poly.pcopy(plist2.getPoly()), pblist);
            plist2 = plist2.getNext();
        }
        plist2 = plist;

        if (polylist != null) {
            TPoly tp = plist2;
            while (tp != null) {
                TPoly t = tp;
                tp = tp.getNext();
                t.setNext(null);
                int lva = poly.lv(t.getPoly());
                TPoly pl = polylist;

                if (poly.lv(pl.getPoly()) > lva) {
                    t.setNext(polylist);
                    polylist = t;
                } else {
                    while (pl.getNext() != null) {
                        if (poly.lv(pl.getNext().getPoly()) > lva) {
                            t.setNext(pl.getNext());
                            pl.setNext(t);
                            break;
                        }
                        pl = pl.getNext();
                    }
                    if (pl.getNext() == null) {
                        pl.setNext(t);
                    }
                }
            }
        } else {
            polylist = plist2;
        }

        try {
            polylist = charset.charset(polylist);
        } catch (OutOfMemoryError ee) {
            JOptionPane.showMessageDialog(gxInstance, ee.getMessage(), ee.toString(), JOptionPane.ERROR_MESSAGE);
        }

        optmizePolynomial();
        SetVarable();

        if (!calcu) {
            pushbackup();
            this.reCalculate();
        }
    }

    /**
     * Optimizes the polynomial list.
     * <p>
     * This method optimizes the polynomial list by adding zero constraints for certain points.
     */
    public void optmizePolynomial() {
        if (!CMisc.POINT_TRANS)
            return;
        if (pointlist.size() < 2) return;
        CPoint p1 = (CPoint) pointlist.get(0);
        CPoint p2 = (CPoint) pointlist.get(1);
        int zeron[] = poly.getZeron();
        addZeron(p1.x1.xindex, zeron);
        addZeron(p1.y1.xindex, zeron);
        addZeron(p2.x1.xindex, zeron);

        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            int t = cs.GetConstraintType();
            if (t == Constraint.PONLINE || t == Constraint.INTER_LC) {
                CPoint t1 = (CPoint) cs.getelement(0);
                CLine l1 = (CLine) cs.getelement(1);
                if (l1.containPTs(p1, p2))
                    addZeron(t1.x1.xindex, zeron);
            } else if (t == Constraint.INTER_LL) {
                CPoint t1 = (CPoint) cs.getelement(0);
                CLine l1 = (CLine) cs.getelement(1);
                CLine l2 = (CLine) cs.getelement(2);
                if (l1.containPTs(p1, p2) || l2.containPTs(p1, p2))
                    addZeron(t1.x1.xindex, zeron);
            }
        }
        TPoly tp = polylist;
        while (tp != null) {
            TMono m = tp.getPoly();
            if (m != null && poly.plength(m) == 1)
                addZeron(m.x, zeron);
            tp = tp.getNext();
        }
    }

    /**
     * Adds a zero constraint for a given variable.
     * <p>
     * This method adds a zero constraint for a given variable to the zero constraints array.
     *
     * @param x     the variable index
     * @param zeron the array of zero constraints
     */
    public void addZeron(int x, int[] zeron) {
        for (int i = 0; true; i++) {
            if (zeron[i] == x)
                break;
            if (zeron[i] == 0) {
                zeron[i] = x;
                break;
            }
        }
    }

    /**
     * Selects multiple solutions for a given point.
     * <p>
     * This method selects multiple solutions for a given point by calculating the possible values
     * and adding them to the solution list.
     *
     * @param p the point for which to select multiple solutions
     * @return true if the selection is successful, false otherwise
     */
    public boolean mulSolutionSelect(CPoint p) {
        pSolution = p;
        TMono m1 = p.x1.m;
        TMono m2 = p.y1.m;

        if (m1 == null || m2 == null)
            return true;
        if (m1.deg == 1 && m2.deg == 1)
            return true;

        double x1, y1, sin, cos;
        x1 = y1 = 0;
        sin = 0;
        cos = 1.0;

        if (CMisc.POINT_TRANS) {
            int n = pointlist.size();
            if (n >= 1) {
                CPoint p1 = (CPoint) pointlist.get(0);
                x1 = p1.x1.value;
                y1 = p1.y1.value;
                for (int i = 0; i < pointlist.size(); i++) {
                    CPoint px = (CPoint) pointlist.get(i);
                    px.x1.value = px.x1.value - x1;
                    px.y1.value = px.y1.value - y1;
                }
            }
            if (n >= 2) {
                CPoint p2 = (CPoint) pointlist.get(1);
                double t1 = p2.getx();
                double t2 = p2.gety();
                double r = Math.sqrt(t1 * t1 + t2 * t2);
                sin = t1 / r;
                cos = t2 / r;
                for (int i = 1; i < pointlist.size(); i++) {
                    CPoint px = (CPoint) pointlist.get(i);
                    t1 = px.getx();
                    t2 = px.gety();
                    px.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
                }
            }
        }

        int lva = poly.lv(m1);
        double[] result = poly.calculv(m1, parameter);

        if (result == null) {
            result = this.calform(lva, parameter);
        }
        if (result == null)
            return false;

        lva = poly.lv(m2);
        for (int i = 0; i < result.length; i++) {
            parameter[p.x1.xindex - 1].value = result[i];
            double[] r = poly.calculv(m2, parameter);
            if (r == null)
                r = this.calform(lva, parameter);

            for (int j = 0; j < r.length; j++) {
                CPoint pt = this.CreateATempPoint(result[i], r[j]);
                solutionlist.add(pt);
            }
        }

        if (CMisc.POINT_TRANS) {
            double t1, t2;
            sin = -sin;
            for (int i = 1; i < pointlist.size(); i++) {
                CPoint px = (CPoint) pointlist.get(i);
                t1 = px.getx();
                t2 = px.gety();
                px.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
            }

            for (int i = 0; i < pointlist.size(); i++) {
                CPoint px = (CPoint) pointlist.get(i);
                px.x1.value += x1;
                px.y1.value += y1;
            }
            for (int i = 0; i < solutionlist.size(); i++) {
                CPoint px = (CPoint) solutionlist.get(i);
                t1 = px.getx();
                t2 = px.gety();
                px.setXY(t1 * cos - t2 * sin, t1 * sin + t2 * cos);
            }

            for (int i = 0; i < solutionlist.size(); i++) {
                CPoint px = (CPoint) solutionlist.get(i);
                px.x1.value += x1;
                px.y1.value += y1;
            }
        }

        if (solutionlist.size() == 1) {
            solutionlist.clear();
            return true;
        }
        PreviousAction = CurrentAction;
        SetCurrentAction(MULSELECTSOLUTION);
        return true;
    }

    /**
     * Erases a decided point from the polynomial list.
     * <p>
     * This method removes a point from the polynomial list and adjusts the list accordingly.
     * It also updates the parameter counter.
     *
     * @param p the point to be erased
     */
    public void ErasedADecidedPoint(CPoint p) { //there are some problems in this function.
        int x1 = p.x1.xindex;
        int y1 = p.y1.xindex;

        if (!p.x1.Solved || !p.y1.Solved) {
            return;
        }
        TPoly plist = polylist;
        TPoly pleft = null;

        TMono m1, m2;
        if (poly.lv(plist.getPoly()) < x1) {
            while (plist.getNext() != null) {
                if (poly.lv(plist.getNext().getPoly()) == x1) {
                    break;
                }
                plist = plist.getNext();
            }
            pleft = plist.getNext();
            m1 = pleft.getPoly();
            pleft = pleft.getNext();
            m2 = pleft.getPoly();

            pleft = pleft.getNext();
            plist.setNext(pleft);

        } else {
            m1 = plist.getPoly();
            plist = plist.getNext();
            m2 = plist.getPoly();
            polylist = plist.getNext();
            pleft = polylist;
        }

        plist = pleft;
        while (plist != null) {
            TMono m = poly.prem(plist.getPoly(), poly.pcopy(m2));
            m = poly.prem(m, poly.pcopy(m1));
            poly.printpoly(m);
            plist.setPoly(m);
            plist = plist.getNext();
        }
        paraCounter -= 2;
        return;
    }

    /**
     * Sets the dimensions of the drawing area.
     *
     * @param x the width of the drawing area
     * @param y the height of the drawing area
     */
    public void SetDimension(double x, double y) {
        this.Width = x;
        this.Height = y;
    }

    /**
     * Retrieves the current action type.
     *
     * @return the current action type
     */
    public int GetCurrentAction() {
        return this.CurrentAction;
    }

    /**
     * Sets the parameters for the drawing process.
     *
     * @param v1 the first parameter
     * @param v2 the second parameter
     */
    public void setParameter(int v1, int v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * Sets the current action type and updates the UI accordingly.
     *
     * @param type the action type to set
     */
    public void SetCurrentAction(int type) {
        if (type != MOVE && CurrentAction == CONSTRUCT_FROM_TEXT) {
            this.clearFlash();
        }

        if (gxInstance != null)
            gxInstance.setActionPool(type);

        this.CurrentAction = type;
        SelectList.clear();

        if (type == SETTRACK) {
            CTrackPt = null;
        }
        FirstPnt = SecondPnt = null;
        STATUS = 0;
        CatchList.clear();
        vx1 = vy1 = vangle = 0;
        vtrx = vtry = 0;
        if (panel != null)
            panel.repaint();
        else if (gxInstance != null)
            panel.repaint();
        if (gxInstance != null) {
            CStyleDialog dlg = gxInstance.getStyleDialog();
            if (dlg != null && dlg.isVisible())
                dlg.setAction(this.getActionType(type));
        }
    }

    /**
     * Finds an edmark object between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the found edmark object, or null if not found
     */
    public Cedmark fd_edmark(CPoint p1, CPoint p2) {
        for (int i = 0; i < otherlist.size(); i++) {
            Object obj = otherlist.get(i);
            if (obj instanceof Cedmark) {
                Cedmark ln = (Cedmark) obj;
                if ((ln.p1 == p1 && ln.p2 == p2) || (ln.p2 == p1 && ln.p1 == p2)) {
                    return ln;
                }
            }
        }
        return null;
    }

    /**
     * Sets the current status.
     *
     * @param status the status to set
     */
    public void setcurrentStatus(int status) {
        STATUS = status;
    }

    /**
     * Proceeds to the next step in the proof process.
     *
     * @return true if the next step is successfully executed, false otherwise
     */
    public boolean nextProveStep() {
        if (cpfield != null) {
            clearSelection();
            UndoStruct u = this.redo_step();

            if (u == null)
                this.Undo();
            else
                cpfield.setSelectedUndo(u, this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Starts the proof play with a specified timer interval.
     *
     * @param num the timer interval in milliseconds
     */
    public void provePlay(int num) {
        if (timer_type == 0) {
            timer = new Timer(num, this);
            timer.start();
            timer_type = 2;
        } else if (timer_type == 2) {
            timer.stop();
            timer_type = 0;
            this.redo();
        }
    }

    /**
     * Stops the proof play.
     */
    public void proveStop() {
        if (timer_type != 2) {
            return;
        }
        timer.stop();
        timer_type = 0;
        cpfield.run_to_end(this);
    }

    /**
     * Runs the proof process to a specific step.
     *
     * @param u  the current undo structure
     * @param u1 the target undo structure
     * @return true if the process is successfully executed, false otherwise
     */
    public boolean run_to_prove(UndoStruct u, UndoStruct u1) {
        this.doFlash();

        if (u1 == null && U_Obj == null)
            return false;

        if (u1 != null) {
            runto();
        } else {
            runto1(U_Obj);
            this.repaint();
            return true;
        }
        runto1(u1);

        this.repaint();
        return true;
    }

    /**
     * Runs the proof process to the current undo structure.
     */
    public void runto() {
        UndoStruct u = U_Obj;
        if (u == null) return;

        UndoStruct ux;
        if (this.already_redo(u)) return;

        while (true) {
            ux = redo_step(false);
            this.doFlash();
            if (ux == null || ux == u)
                break;
        }
        U_Obj = null;
    }

    /**
     * Runs the proof process to a specific undo structure.
     *
     * @param u the target undo structure
     */
    public void runto1(UndoStruct u) {
        if (u == null) return;
        UndoStruct ux;
        if (this.already_redo(u))
            return;

        while (true) {
            ux = redo_step(false);
            if (ux == null || ux == u) {
                U_Obj = null;
                return;
            } else if (!all_flash_finished()) {
                U_Obj = u;
                return;
            }
        }
    }

    /**
     * Checks if all flash animations are finished.
     *
     * @return true if all flash animations are finished, false otherwise
     */
    public boolean all_flash_finished() {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            if (!f.isfinished())
                return false;
        }
        return true;
    }

    /**
     * Checks if the construction proof field exists.
     *
     * @return true if the construction proof field exists, false otherwise
     */
    public boolean checkCPfieldExists() {
        return cpfield != null;
    }

    /**
     * Runs the proof process to the end.
     */
    public void prove_run_to_end() {
        if (cpfield != null) {
            cpfield.run_to_end(this);
        } else {
            if (gxInstance != null && gxInstance.getpprove() != null)
                gxInstance.getpprove().m_runtoend();
        }
    }

    /**
     * Retrieves the snap coordinates based on the grid settings.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return an array containing the snapped x and y coordinates
     */
    public double[] getSnap(double x, double y) {
        double[] r = new double[2];
        if (!this.SNAP) {
            r[0] = x;
            r[1] = y;
            return r;
        }
        int nx = (int) (0.5 + x / this.GridX);
        int ny = (int) (0.5 + y / this.GridY);
        r[0] = nx * GridX;
        r[1] = ny * GridY;
        return r;
    }

    /**
     * Handles the mouse wheel event for zooming in and out.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @param n  the number of notches the mouse wheel was rotated
     * @param rt the rotation direction (positive for zoom in, negative for zoom out)
     */
    public void DWMouseWheel(double x, double y, int n, int rt) {
        switch (this.CurrentAction) {
            case MOVE:
            case ZOOM_IN:
            case ZOOM_OUT:
                int k = Math.abs(n);
                for (int i = 0; i < k; i++) {
                    if (rt > 0)
                        zoom_in(x, y, 3);
                    else zoom_out(x, y, 3);
                }
                if (k > 0)
                    this.reCalculate();
                break;
        }
    }

    /**
     * Handles the double-click event on the drawing window.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void DWMouseDbClick(double x, double y) {
        CatchPoint.setXY(x, y);

        switch (this.CurrentAction) {
            case MOVE: {
                if (!viewElementFromXY(x, y)) {
                }
            }
        }
    }

    /**
     * Defines a specific angle constraint.
     */
    public void defineSpecificAngle() {
        if (paraCounter != 1) {
            Vector v = this.getSpecificAngleList();
            if (v.size() == 0) {
                JOptionPane.showMessageDialog(gxInstance,
                        gxInstance.getLanguage("Angle Specification must be done before drawing anything"),
                        gxInstance.getLanguage("Warning"),
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            SpecificAngleDialog dlg = new SpecificAngleDialog(null, 1, v);
            dlg.setVisible(true);
            return;
        }
        SpecificAngleDialog dlg = new SpecificAngleDialog(gxInstance, 0, null);
        dlg.setVisible(true);
        if (!dlg.isOkPressed()) {
            return;
        }
        Vector v = dlg.getSpecificAngle();

        for (int i = 0; i < v.size(); i++) {
            Integer in = (Integer) v.get(i);
            int value = in.intValue();
            Param p1 = parameter[paraCounter - 1] = new Param(paraCounter, 0);
            p1.type = value;
            paraCounter++;
            Constraint cs = new Constraint(Constraint.SPECIFIC_ANGEL, p1, value);
            this.addConstraintToList(cs);
            this.charsetAndAddPoly(false);
        }
        if (paraCounter % 2 != 0) {
            parameter[paraCounter - 1] = new Param(0, 0);
            paraCounter += 1;
            parameter[paraCounter - 1] = new Param(0, 0);
            paraCounter += 1;
        } else {
            parameter[paraCounter - 1] = new Param(0, 0);
            paraCounter += 1;
        }
    }

    /**
     * Retrieves the parameter associated with a specific angle constraint.
     *
     * @param ang the specific angle value
     * @return the parameter associated with the specific angle constraint, or null if not found
     */
    public Param getParaForSpecificAngle(int ang) {
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGEL) {
                if (cs.proportion == ang) {
                    Param pm = (Param) cs.getelement(0);
                    return pm;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a list of specific angle constraints.
     *
     * @return a vector containing the specific angle constraints
     */
    public Vector getSpecificAngleList() {
        Vector v = new Vector();

        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.GetConstraintType() == Constraint.SPECIFIC_ANGEL) {
                v.add(cs.proportion);
            }
        }
        return v;
    }

    /**
     * Views an element from the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if an element is viewed, false otherwise
     */
    public boolean viewElementFromXY(double x, double y) {
        Vector v = new Vector();
        this.SelectAllFromXY(v, x, y, 0);

        CClass c = null;
        if (v.size() == 0) {
            return false;
        }

        if (v.size() > 1) {
            c = (CClass) popSelect(v, (int) x, (int) y);
        } else {
            c = (CClass) v.get(0);
        }
        if (c == null) {
            return false;
        }
        v.clear();
        v.add(c);
        this.setObjectListForFlash(v);
        this.onDBClick(c);
        return true;
    }

    /**
     * Displays a selection dialog if multiple objects are selected.
     *
     * @param v the vector of selected objects
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected object, or null if no object is selected
     */
    public Object popSelect(Vector v, int x, int y) {
        if (v.size() == 1) {
            this.viewElement((CClass) v.get(0));
        }
        if (v.size() > 1) {
            SelectDialog sd = gxInstance.getSelectDialog();

            JPanel d = panel;
            Point p = d.getLocationOnScreen();
            sd.addItem(v);
            sd.setLocation((int) (p.getX() + x), (int) (p.getY() + y));
            sd.setVisible(true);
            Object obj = sd.getSelected();
            gxInstance.setFocusable(true);
            return obj;
        }
        return null;
    }

    /**
     * Handles the right mouse button down event.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void DWMouseRightDown(double x, double y) {
        if (CurrentAction != DEFINEPOLY && CurrentAction != TRANSFORM && CurrentAction != FREE_TRANSFORM) {
            CatchPoint.setXY(x, y);
            clearSelection();
            STATUS = 0;
            this.RightMenuPopup(x, y);
        }
    }

    /**
     * Handles the right mouse button click event.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void DWMouseRightClick(double x, double y) {
        CatchPoint.setXY(x, y);
        switch (CurrentAction) {
            case DEFINEPOLY: {
                if (SelectList.size() == 1 && STATUS != 0) {
                    CPolygon cp = (CPolygon) SelectList.get(0);
                    if (cp.pointlist.size() >= 3) {
                        cp.addAPoint((CPoint) cp.pointlist.get(0));
                        STATUS = 0;
                        addPolygonToList(cp);
                        this.UndoAdded(cp.getDescription());
                        clearSelection();
                    }
                    panel.repaint();
                } else {
                    CatchPoint.setXY(x, y);
                    clearSelection();
                    STATUS = 0;
                    this.RightMenuPopup(x, y);
                }
            }
            break;
            case TRANSFORM: {
                if (STATUS != 0)
                    new RightTransformPopupMenu(this).show(panel, (int) x, (int) y);
                else
                    RightMenuPopup(x, y);
            }
            break;
            case FREE_TRANSFORM: {
                if (SelectList.size() == 1) {
                    JPopupMenu m = new JPopupMenu();
                    ActionListener ls = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand().equals("OK"))
                                DrawProcess.this.add_free_transform();
                            else {
                                CPolygon poly = (CPolygon) SelectList.get(0);
                                STATUS = 0;
                                clearSelection();
                                poly.setDraggedPointsNull();
                            }
                        }
                    };
                    JMenuItem item1 = new JMenuItem(GExpert.getLanguage("OK"));
                    item1.addActionListener(ls);
                    JMenuItem item2 = new JMenuItem(GExpert.getLanguage("Cancel"));
                    item2.addActionListener(ls);
                    m.add(item1);
                    m.add(item2);
                    m.show(panel, (int) x, (int) y);
                }
            }
            break;
        }
    }


    /**
     * Displays a right-click context menu at the specified coordinates.
     *
     * @param x the x-coordinate where the menu should be displayed
     * @param y the y-coordinate where the menu should be displayed
     */
    public void RightMenuPopup(double x, double y) {
        if (gxInstance == null) return;

        Vector v = new Vector();
        SelectAllFromXY(v, x, y, 0);
        CClass c = null;
        JPanel d = panel;

        int len = v.size();

        if (len != 0) {

            if (len > 1) {
                SelectDialog dlg = new SelectDialog(gxInstance, v);
                dlg.addItem(v);
                Point p = d.getLocationOnScreen();
                dlg.setLocation((int) (p.getX() + x), (int) (p.getY() + y));
                dlg.setVisible(true);
                c = (CClass) dlg.getSelected();
            } else
                c = (CClass) v.get(0);

            setObjectListForFlash(c);
        }
        new RightClickPopMenu(c, gxInstance).show(panel, (int) x, (int) y);

    }

    /**
     * Selects objects from one list and adds them to another based on coordinates.
     *
     * @param v1 the list to which selected objects are added
     * @param v2 the list from which objects are selected
     * @param x  the x-coordinate for selection
     * @param y  the y-coordinate for selection
     */
    public void SelectFromAList(Vector v1, Vector v2, double x, double y) {
        for (int i = 0; i < v2.size(); i++) {
            CClass cc = (CClass) v2.get(i);
            if (cc.select(x, y)) {
                v1.add(cc);
            }
        }
    }

    /**
     * Selects all objects from various lists based on coordinates and type.
     *
     * @param v    the list to which selected objects are added
     * @param x    the x-coordinate for selection
     * @param y    the y-coordinate for selection
     * @param type the type of objects to select (0: point preferential, 1: geometry object only, 2: all, etc.)
     */
    public void SelectAllFromXY(Vector v, double x, double y, int type) {
        // 2: all; 1: geometry object only 0: point preferential
        //3: only point, 4:only line, 5: only circle
        //6: only angle 7: only distance  8:only polygon, 9, only text,10 only trace.

        if (type == 0) {
            this.SelectFromAList(v, pointlist, x, y);
            if (v.size() != 0) {
                return;
            }
            this.SelectNameText(v, x, y);
            if (v.size() != 0) {
                return;
            }
            this.SelectFromAList(v, linelist, x, y);
            this.SelectFromAList(v, circlelist, x, y);
            this.SelectFromAList(v, anglelist, x, y);
            this.SelectFromAList(v, distancelist, x, y);
            this.SelectFromAList(v, textlist, x, y);
            this.SelectFromAList(v, tracelist, x, y);
            this.SelectFromAList(v, otherlist, x, y);
            if (v.size() == 0) {
                this.SelectFromAList(v, polygonlist, x, y);
            }
        } else if (type == 1) {
            this.SelectFromAList(v, pointlist, x, y);
            if (v.size() != 0)
                return;
            this.SelectFromAList(v, linelist, x, y);
            this.SelectFromAList(v, circlelist, x, y);
        } else if (type == 2) {
            this.SelectFromAList(v, pointlist, x, y);
            this.SelectFromAList(v, linelist, x, y);
            this.SelectFromAList(v, circlelist, x, y);
            this.SelectFromAList(v, anglelist, x, y);
            this.SelectFromAList(v, distancelist, x, y);
            this.SelectFromAList(v, textlist, x, y);
            this.SelectFromAList(v, tracelist, x, y);
            this.SelectFromAList(v, otherlist, x, y);
            if (v.size() == 0) {
                this.SelectFromAList(v, polygonlist, x, y);
            }
        } else if (type == 3) {
            this.SelectFromAList(v, pointlist, x, y);
        } else if (type == 4) {
            this.SelectFromAList(v, linelist, x, y);
        } else if (type == 5) {
            this.SelectFromAList(v, circlelist, x, y);
        } else if (type == 6) {
            this.SelectFromAList(v, anglelist, x, y);
        } else if (type == 7) {
            this.SelectFromAList(v, distancelist, x, y);
        } else if (type == 8) {
            this.SelectFromAList(v, polygonlist, x, y);
        } else if (type == 9) {
            this.SelectFromAList(v, textlist, x, y);
        } else if (type == 10) {
            this.SelectFromAList(v, tracelist, x, y);
        } else if (type == 11) { // prove only
            this.SelectFromAList(v, otherlist, x, y);
        }

    }

    /**
     * Selects text objects based on coordinates.
     *
     * @param v the list to which selected text objects are added
     * @param x the x-coordinate for selection
     * @param y the y-coordinate for selection
     */
    public void SelectNameText(Vector v, double x, double y) {
        for (int i = 0; i < textlist.size(); i++) {
            CText text = (CText) textlist.get(i);

            if (text.getType() == CText.NAME_TEXT && text.select(x, y)) {
                v.add(text);
            }
            if (text.getType() == CText.CNAME_TEXT && text.select(x, y)) {
                v.add(text);
            }

        }
    }

    /**
     * Selects a single object from various lists based on coordinates and type.
     *
     * @param x    the x-coordinate for selection
     * @param y    the y-coordinate for selection
     * @param type the type of objects to select (0: point preferential, 1: geometry object only, 2: all, etc.)
     * @return the selected object, or null if no object is selected
     */
    public CClass SelectOneFromXY(double x, double y, int type) {
        Vector v = new Vector();
        this.SelectAllFromXY(v, x, y, type);
        if (v.size() == 0) {
            return null;
        }
        if (v.size() == 1) {
            return (CClass) v.get(0);
        }
        return (CClass) popSelect(v, (int) x, (int) y);

    }

    /**
     * Adjusts the coordinates of the second point to align with the first point if they are close enough.
     *
     * @param p1 the first point
     * @param p2 the second point
     */
    public void getSmartPV(CPoint p1, CPoint p2) {
        int x, y;
        x = y = 0;
        if (p1 == null || p2 == null) {
            return;
        }

        int x1 = (int) p1.getx();
        int y1 = (int) p1.gety();
        int x2 = (int) p2.getx();
        int y2 = (int) p2.gety();

        if (Math.abs(x2 - x1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            x = x1;

            p2.setXY(x1, y2);
        } else if (Math.abs(y2 - y1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            y = y1;

            p2.setXY(x2, y1);
        }
    }

    /**
     * Clears the selection list.
     */
    public void clearSelection() {
        SelectList.clear();
        if (gxInstance != null)
            gxInstance.updateActionPool(this.CurrentAction);
    }

    /**
     * Adds an object to the selection list.
     *
     * @param c the object to add
     */
    public void addToSelectList(Object c) {
        if (c != null) {
            SelectList.add(c);
            if (gxInstance != null)
                gxInstance.updateActionPool(this.CurrentAction);
        }

    }

    /**
     * Selects objects based on coordinates and adds them to the catch list.
     *
     * @param x the x-coordinate for selection
     * @param y the y-coordinate for selection
     * @return the list of selected objects
     */
    public Vector OnCatch(double x, double y) {
        CatchList.clear();
        SelectAllFromXY(CatchList, x, y, 0);
        return CatchList;
    }

    /**
     * Checks if a point can be animated along a line.
     *
     * @param p  the point to check
     * @param ln the line to check
     * @return true if the point can be animated along the line, false otherwise
     */
    public boolean check_animation(CPoint p, CLine ln) {
        if (p == null || ln == null)
            return false;
        if (p.isAFixedPoint())
            return false;

        if (p.isAFreePoint() && ln.containPT(p))
            return false;
        return true;
    }

    /**
     * Handles selection logic based on the provided x and y coordinates.
     * Determines whether to select a smart point, display the rule panel, or update the catch list.
     *
     * @param x the x-coordinate for selection
     * @param y the y-coordinate for selection
     */
    private void handleSelectCase(double x, double y) {
        CPoint t = SelectAPoint(x, y);
        boolean r = false;

        if (gxInstance.isDialogProveVisible()) {
            clearSelection();
            if (t != null)
                addToSelectList(t);
            r = true;
            gxInstance.getDialogProve().setSelect(SelectList);
        }

        if (t == null) {
            if (cpfield != null) {
                CProveText ct1 = cpfield.mouseMove(x, y);
                if (ct1 == null) {
                    r = true;
                    CProveText ct = cpfield.select(x, y, false);
                    if (ct != null) {
                        UndoStruct un = ct.getUndoStruct();
                        if (un != null) {
                            this.setObjectListForFlash(un.getAllObjects(this));
                        }
                    }
                } else {
                    Point pt = ct1.getPopExLocation();
                    gxInstance.showRulePanel("R1", (int) pt.getX(), (int) pt.getY());
                }
            }
        } else {
            if (gxInstance.hasMannualInputBar()) {
                PanelProve pp = gxInstance.getpprove();
                r = pp.selectAPoint(t);
                if (r)
                    this.setObjectListForFlash(t);
            }
            gxInstance.selectAPoint(t);
        }

        if (r == false) {
            CatchList.clear();
            this.SelectAllFromXY(CatchList, x, y, 0);
            if (CatchList.size() == 0)
                this.clearSelection();
            else {
                this.addToSelectList(CatchList.get(0));
            }
        } else {
            this.clearSelection();
        }
        vx1 = x;
        vy1 = y;
    }

    /**
     * Handles the move case by selecting objects from the given coordinates.
     * Clears the selection and updates the view of elements based on user interaction.
     *
     * @param x the x-coordinate for moving
     * @param y the y-coordinate for moving
     */
    private void handleMoveCase(double x, double y) {
        FirstPnt = this.CreateATempPoint(x, y);
        Vector v = new Vector();

        this.SelectAllFromXY(v, x, y, 0);
        if (v.size() == 0) {
            clearSelection();
            if (cpfield != null) {
                CProveText ct1 = cpfield.mouseMove(x, y);
                if (ct1 == null) {
                    CProveText ct = cpfield.select(x, y, false);
                    if (ct != null) {
                        UndoStruct un = ct.getUndoStruct();
                        if (un != null) {
                            this.setObjectListForFlash(un.getAllObjects(this));
                        }

                    }
                } else {
                    Point pt = ct1.getPopExLocation();
                    gxInstance.showRulePanel(ct1.getRulePath(),
                            (int) pt.getX(), (int) pt.getY());
                }
            }
        } else if (v.size() == 1) {
            clearSelection();
            SelectList.addAll(v);
            CClass cc = (CClass) v.get(0);
            v.clear();
            if (cc instanceof CPoint) {
                if (gxInstance != null) {
                    if (gxInstance.isconcVisible()) {
                        gxInstance.getConcDialog().selectAPoint((CPoint) cc);
                    }
                    if (gxInstance.hasMannualInputBar()) {
                        gxInstance.getMannalInputToolBar().selectAPoint((CPoint) cc);
                    }
                }
            }
        } else {
            clearSelection();
            addToSelectList(v.get(0));
        }

        if (SelectList.size() == 1) {
            if (gxInstance != null) {
                gxInstance.viewElementsAuto((CClass) SelectList.get(0));
            }
        }
    }

    /**
     * Handles the point definition case.
     * Clears the current selection, retrieves a smart point from the given coordinates, and adds it to the selection.
     *
     * @param x the x-coordinate for defining a point
     * @param y the y-coordinate for defining a point
     * @param p an initial point which may be replaced by a smart-detected point
     */
    private void handleDpointCase(double x, double y, CPoint p) {
        clearSelection();
        p = this.SmartgetApointFromXY(x, y);
        if (p != null) {
            addToSelectList(p);
            this.UndoAdded(p.TypeString());
        }
    }

    /**
     * Handles the triangle creation process by selecting three distinct points.
     * Manages state transitions for triangle construction and adds corresponding lines and constraints.
     *
     * @param x the x-coordinate used in triangle construction
     * @param y the y-coordinate used in triangle construction
     */
    private void handleTriangleCase(double x, double y) {
        if (STATUS == 0) {
            CPoint pp = (CPoint) this.CatchList(pointlist, x, y);
            if (pp == null) {
                pp = SmartgetApointFromXY(x, y);
            }

            this.addToSelectList(pp);
            FirstPnt = pp;
            STATUS = 1;

        } else if (STATUS == 1) {
            CPoint pp = (CPoint) this.CatchList(pointlist, x, y);
            if (pp == null) {
                pp = SmartgetApointFromXY(x, y);
            }

            if (!SelectList.contains(pp)) {
                addToSelectList(pp);
                SecondPnt = pp;
                STATUS = 2;
            }

        } else {
            CPoint pp = (CPoint) this.CatchList(pointlist, x, y);
            if (pp == null) {
                pp = SmartgetApointFromXY(x, y);
            }

            if (!SelectList.contains(pp)) {
                addToSelectList(pp);
            } else {
                return;
            }

            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = (CPoint) SelectList.get(2);
            CLine line1 = new CLine(p1, p2);
            CLine line2 = new CLine(p1, p3);
            CLine line3 = new CLine(p2, p3);
            this.addPointToList(p1);
            this.addPointToList(p2);
            this.addPointToList(p3);
            this.addLineToList(line1);
            this.addLineToList(line2);
            this.addLineToList(line3);
            Constraint cs = new Constraint(Constraint.TRIANGLE, p1, p2, p3);
            this.addConstraintToList(cs);
            this.UndoAdded("Triangle " + p1.m_name + p2.m_name + p3.m_name);
            FirstPnt = SmartgetApointFromXY(x, y);
            SecondPnt = this.CreateATempPoint(x, y);
            clearSelection();
            STATUS = 0;
        }
    }

    /**
     * Handles the vertical line case.
     * Sets the first point and creates a temporary second point for further vertical line processing.
     *
     * @param x the x-coordinate for vertical line creation
     * @param y the y-coordinate for vertical line creation
     */
    private void handleVLineCase(double x, double y) {
        if (STATUS == 0) {
            FirstPnt = SmartgetApointFromXY(x, y);
            SecondPnt = this.CreateATempPoint(x, y);
            STATUS = 1;
        }
    }

    /**
     * Handles the definition of a line by selecting two points.
     * In the first stage, selects the starting point; in the second stage, completes the line, applies smart adjustments,
     * adds the line to the list, and creates the associated constraint.
     *
     * @param x the x-coordinate for line definition
     * @param y the y-coordinate for line definition
     */
    private void handleDLineCase(double x, double y) {
        if (STATUS == 0) {
            if ((FirstPnt = SmartgetApointFromXY(x, y)) != null) {
                STATUS = 1;
                addPointToList(FirstPnt);
                this.addToSelectList(FirstPnt);
            }
        } else if (STATUS == 1) {
            CPoint tp = FirstPnt;
            if (this.isPointOnObject) {
                x = mouseCatchX;
                y = mouseCatchY;
            }
            CPoint pp = SmartgetApointFromXY(x, y);
//                    pp.setXY(x, y);
            getSmartPV(FirstPnt, pp);

            if (tp != pp && tp != null && pp != null) {
                setSmartPVLine(tp, pp);
                addPointToList(pp);
                CLine ln = new CLine(pp, tp, CLine.LLine);
                this.addLineToList(ln);
                Constraint cs = new Constraint(Constraint.LINE, tp, pp);
                addConstraintToList(cs);
                this.reCalculate();
                this.UndoAdded(ln.getDescription());
            }
            clearSelection();
            STATUS = 0;
            FirstPnt = null;
        }
    }

    /**
     * Handles the polygon definition process by selecting multiple points.
     * Adds points until the first point is re-selected, then constructs the polygon, creates connecting lines,
     * and adds polygon constraints.
     *
     * @param x the x-coordinate for polygon definition
     * @param y the y-coordinate for polygon definition
     */
    private void handleDPolygonCase(double x, double y) {
        CPoint pt = SmartgetApointFromXY(x, y);
        setSmartPVLine(FirstPnt, pt);
        boolean finish = false;

        if (SelectList.isEmpty()) {
            this.addPointToList(pt);
            addToSelectList(pt);
            FirstPnt = pt;
            SecondPnt = this.CreateATempPoint(x, y);
        } else if (pt == SelectList.get(0)) {
            finish = true;
        } else if (SelectList.contains(pt)) {
            return;
        } else {
            this.addPointToList(pt);
            addToSelectList(pt);
            if (SelectList.size() == STATUS) {
                finish = true;
            }
            FirstPnt = pt;
        }
        if (finish) {
            if (SelectList.size() <= 1) {
                clearSelection();
                return;
            }
            CPoint t1 = (CPoint) SelectList.get(0);
            CPoint tp = t1;
            for (int i = 1; i < SelectList.size(); i++) {
                CPoint tt = (CPoint) SelectList.get(i);
                if (this.fd_line(tt, tp) == null) {
                    CLine ln = new CLine(tt, tp, CLine.LLine);
                    this.addLineToList(ln);
                }
                tp = tt;
            }
            if (this.fd_line(t1, tp) == null) {
                CLine ln = new CLine(t1, tp);
                this.addLineToList(ln);
            }

            StringBuilder s = new StringBuilder();
            int size = SelectList.size();
            for (Object o : SelectList) {
                CClass cc = (CClass) o;
                s.append(cc.m_name);

            }
            if (size == 3) {
                Constraint cs = new Constraint(Constraint.TRIANGLE, SelectList);
                this.addConstraintToList(cs);

                this.UndoAdded("triangle  " + s);
            } else if (size == 4) {
                Constraint cs = new Constraint(Constraint.QUADRANGLE, SelectList);
                this.addConstraintToList(cs);
                this.UndoAdded("quadrangle  " + s);
            } else if (size == 5) {
                Constraint cs = new Constraint(Constraint.PENTAGON, SelectList);
                this.addConstraintToList(cs);
                this.UndoAdded(GExpert.getTranslationViaGettext("Pentagon {0}", s.toString()));
            } else {
                Constraint cs = new Constraint(Constraint.POLYGON, SelectList);
                this.addConstraintToList(cs);
                this.UndoAdded(GExpert.getTranslationViaGettext("Polygon {0}", s.toString()));
            }
            clearSelection();
        }
    }

    /**
     * Handles the parallel line definition case.
     * Selects an existing line and a point to create a new line that is parallel to the selected one,
     * then adds the parallel constraint.
     *
     * @param x the x-coordinate for parallel line definition
     * @param y the y-coordinate for parallel line definition
     */
    private void handleDPareLineCase(double x, double y) {
        if (STATUS == 0) {
            clearSelection();
            CLine line = this.SmartPLine(CatchPoint);

            if (line == null) {
                return;
            }
            addToSelectList(line);
            STATUS = 1;
        } else if (STATUS == 1) {
            if (SelectList.isEmpty()) {
                return;
            }
            CPoint pt = this.SmartgetApointFromXY(x, y);
            CLine line = (CLine) SelectList.get(0);

            CLine line1 = new CLine(pt, CLine.PLine);
            Constraint cs = new Constraint(Constraint.PARALLEL, line1, line);
            this.addConstraintToList(cs);
            line1.addconstraint(cs);
            clearSelection();
            this.addLineToList(line1);
            // UndoStruct u = this.UndoAdded(line1.TypeString() + " parallel " +
            //         line.getDiscription() + " passing " +
            //         pt.getname());
            UndoStruct u = this.UndoAdded(line1.TypeString() + " " + GExpert.getTranslationViaGettext("parallel to {0} passing {1}",
                    line.getDiscription(), pt.getname()));
            u.addObject(line1);
            u.addObject(line);
            u.addObject(pt);
            clearSelection();
            STATUS = 0;

        }

    }

    /**
     * Handles the perpendicular line definition case.
     * Selects an existing line and a point to create a new line perpendicular to the selected one,
     * then adds the perpendicular constraint.
     *
     * @param x the x-coordinate for perpendicular line definition
     * @param y the y-coordinate for perpendicular line definition
     */
    private void handleDPerpLineCase(double x, double y) {
        if (STATUS == 0) {
            clearSelection();
            CLine line = this.SmartPLine(CatchPoint);
            if (line == null)
                return;

            addToSelectList(line);
            STATUS = 1;
        } else if (STATUS == 1) {
            if (SelectList.size() == 0) {
                return;
            }
            CLine line = (CLine) SelectList.get(0);
            CPoint pt = this.SmartgetApointFromXY(x, y);

            CLine line1 = new CLine(pt, CLine.TLine);
            Constraint c = new Constraint(Constraint.PERPENDICULAR, line1, line);
            this.addConstraintToList(c);
            line1.addconstraint(c);
            addLineToList(line1);
            addCTMark(line, line1);
            UndoStruct u = this.UndoAdded(line1.TypeString() + " perp " +
                    // line.getDiscription() + " passing " +
                    // pt.getname());
                    line.getDescription() + " " +
                    GExpert.getTranslationViaGettext("passing {0}", pt.getname()));
            u.addObject(line1);
            u.addObject(line);
            u.addObject(pt);
            STATUS = 0;
            clearSelection();
        }
    }

    /**
     * Handles the A-line construction case based on existing selected lines.
     * If fewer than three lines are selected, accumulates the selection; otherwise, creates a new A-line constrained
     * by the intersection properties of the selected lines.
     *
     * @param x the x-coordinate used for A-line construction
     * @param y the y-coordinate used for A-line construction
     */
    private void handleDAlineCase(double x, double y) {
        int n = SelectList.size();
        if (n < 3) {
            CLine line = this.SmartPLine(CatchPoint);
            if (line == null) {
                return;
            }
            if (n == 1) {
                CLine ln1 = (CLine) SelectList.get(0);
                if (CLine.commonPoint(ln1, line) == null) {
                    JOptionPane.showMessageDialog(gxInstance, GExpert.getLanguage("The selected two lines don't have intersected point"),
                            GExpert.getLanguage("Warning"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            addToSelectList(line);
        } else {
            CLine ln1 = (CLine) SelectList.get(0);
            CLine ln2 = (CLine) SelectList.get(1);
            CLine ln3 = (CLine) SelectList.get(2);
            CPoint tt = null;
            if (this.SmartPLine(CatchPoint) == ln3 || ((tt = this.SmartPoint(CatchPoint)) != null && ln3.containPT(tt))) {
                CPoint p1 = this.SmartgetApointFromXY(x, y);
                CLine ln = new CLine(CLine.ALine);
                ln.addApoint(p1);
                Constraint cs = new Constraint(Constraint.ALINE, ln1, ln2, ln3, ln);
                cs.setPolyGenerate(false);

                ln.addconstraint(cs);
                this.addLineToList(ln);
                this.addConstraintToList(cs);
                clearSelection();
                this.UndoAdded("ALine " + ln.getname());
            }
        }
    }

    /**
     * Handles the DAB line case.
     *
     * <p>
     * Depending on the current selection status, it selects points or lines and creates a line representing the angle bisector.
     * In one case, it creates a new auxiliary point and line constraint; in the other, it directly creates the bisector.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDABlineCase(double x, double y, CPoint p) {
        int n = SelectList.size();
        if (STATUS == 0) {
            p = this.SelectAPoint(x, y);
            if (p != null) {
                addToSelectList(p);
                STATUS = 1;
            } else {
                CLine ln = SelectALine(x, y);
                if (ln != null) {
                    addToSelectList(ln);
                    CatchPoint.setXY(x, y);
                    ln.pointonline(CatchPoint);
                    catchX = CatchPoint.getx();
                    catchY = CatchPoint.gety();
                }
                STATUS = 2;
            }
        } else if (STATUS == 5) {
            CLine ln = (CLine) SelectList.get(0);
        } else {

            if (n < 3 && STATUS == 1) {
                addSelectPoint(x, y);
            } else if (n < 2 && STATUS == 2) {
                CLine ln = SelectALine(x, y);
                if (ln != null) {
                    if (SelectList.isEmpty())
                        return;
                    CLine ln0 = (CLine) SelectList.get(0);
                    if (CLine.commonPoint(ln0, ln) != null)
                        addToSelectList(ln);
                    else
                        JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The selected two lines don't have intersected point")
                                , gxInstance.getLanguage("No intersected point"), JOptionPane.WARNING_MESSAGE);
                }
            }
            n = SelectList.size();
            {
                CPoint p1, p2, p3;
                boolean dd = true;
                if (STATUS == 1 && n == 3) {
                    p1 = (CPoint) SelectList.get(0);
                    p2 = (CPoint) SelectList.get(1);
                    p3 = (CPoint) SelectList.get(2);
                } else if (STATUS == 2 && n == 2) {
                    CLine ln1 = (CLine) SelectList.get(0);
                    CLine ln2 = (CLine) SelectList.get(1);
                    p2 = CLine.commonPoint(ln1, ln2);
                    p1 = ln1.get_Lptv(p2, catchX, catchY);
                    p3 = ln2.get_Lptv(p2, x, y);
                    dd = false;
                } else
                    return;

                if (p3 != null && p3 != p1 && p3 != p2) {
                    CLine ln = new CLine(CLine.ABLine);
                    ln.addApoint(p2);
                    if (dd) {
                        CPoint pt = this.CreateANewPoint(0, 0);
                        ln.addApoint(pt);
                        CLine ln1 = this.addALine(CLine.LLine, p1, p3);
                        Constraint cs = new Constraint(Constraint.ANGLE_BISECTOR, p1, p2, p3, ln);
                        Constraint cs1 = new Constraint(Constraint.PONLINE, pt, ln1);
                        ln.addconstraint(cs);
                        this.addPointToList(pt);
                        this.addLineToList(ln);
                        this.addConstraintToList(cs);
                        this.charsetAndAddPoly(false);
                        clearSelection();
                        STATUS = 0;
                        this.UndoAdded(ln.getSimpleName() + " is the bisector of angle " + p1 + p2 + p3, true, ln.getPtsSize() > 1);
                    } else {
                        Constraint cs = new Constraint(Constraint.ANGLE_BISECTOR, p1, p2, p3, ln);
                        ln.addconstraint(cs);
                        this.addLineToList(ln);
                        this.addConstraintToList(cs);
                        clearSelection();
                        STATUS = 0;
                        this.UndoAdded("Angle Bisector " + ln.getname(), true, ln.getPtsSize() > 1);
                    }
                }
            }
        }
    }

    /**
     * Handles the DP foot case.
     *
     * <p>
     * Processes the creation of a perpendicular foot. Initially selects a point, then computes the foot point relative
     * to an existing selected point, adds necessary constraints, and may create corresponding lines.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDPfootCase(double x, double y, CPoint p) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                this.setSmartPVLine(pa, pt);
                if (fd_line(pa, pt) == null) {
                    CLine ln = new CLine(pa, pt);
                    this.addLineToList(ln);
                }
            }
            if (!SelectList.contains(pt)) {
                addToSelectList(pt);
            }
            if (SelectList.size() == 2) {
                STATUS = 2;
            }
        } else if (STATUS == 2) {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);

            double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(), p2.gety(), x, y);
            double xr = r[0];
            double yr = r[1];
            p = this.SmartgetApointFromXY(xr, yr);
            if (p == p1 || p == p2) {
                return;
            }
            CLine ln1, ln2;
            ln1 = ln2 = null;
            if ((ln1 = fd_line(p, p1)) == null) {
                ln1 = new CLine(p1, p, CLine.LLine);
                this.addLineToList(ln1);
            }
            if ((ln2 = fd_line(p, p2)) == null) {
                ln2 = new CLine(p2, p, CLine.LLine);
                this.addLineToList(ln2);
            }
            Constraint cs = new Constraint(Constraint.RIGHT_ANGLED_TRIANGLE, p, p1, p2);
            this.addConstraintToList(cs);
            this.charsetAndAddPoly(false);
            if (!this.isLineExists(p1, p2)) {
                CLine lp = new CLine(p1, p2, CLine.LLine);
                this.addLineToList(lp);
            }
            clearSelection();
            STATUS = 0;
            addCTMark(ln1, ln2);
            //this.otherlist.add(m);
            // FIXME: use better keys
            this.UndoAdded(GExpert.getLanguage("Right") + " triangle " + p1.getname() + p2.getname() + p.getname());
        }
    }

    /**
     * Handles the perpendicular with foot case.
     *
     * <p>
     * Selects a point and then computes the perpendicular foot of a point on a line,
     * creating a new point and linking it with a constraint.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    private void handlePerpWithFoot(double x, double y) {
        if (STATUS == 0) {
            FirstPnt = SmartgetApointFromXY(x, y);
            STATUS = 1;
        } else if (STATUS == 1) {
            CPoint pt = this.SmartPoint(CatchPoint);
            if (pt == FirstPnt) {
                return;
            }
            CLine line = this.SmartPLine(CatchPoint);
            if (line == null) {
                return;
            }
            CPoint pp = this.CreateANewPoint(0, 0);
            this.add_PFOOT(line, FirstPnt, pp);
            STATUS = 0;
        }
    }

    /**
     * Handles the DCircle case.
     *
     * <p>
     * In the first step, selects a point to be used as the circle's center. On the subsequent call,
     * selects a second point that defines the radius, creates the circle, and sets the corresponding constraint.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDCircleCase(double x, double y, CPoint p) {
        if (STATUS == 0) {
            p = this.SmartgetApointFromXY(x, y);
            if (p != null) {
                FirstPnt = p;
                addToSelectList(p);
                addPointToList(p);
                STATUS = 1;
            }
        } else if (STATUS == 1) {
            p = SmartgetApointFromXY(x, y);
            if (p == FirstPnt)
                return;

            Circle c = new Circle(FirstPnt, p);
            addCircleToList(c);
            Constraint cs = new Constraint(Constraint.CIRCLE, FirstPnt, p);
            this.addConstraintToList(cs);
            this.charsetAndAddPoly(false);
            this.UndoAdded(c.getDescription());
            STATUS = 0;
            clearSelection();
        }
    }

    /**
     * Handles the DCircle By Radius case.
     *
     * <p>
     * Waits until two points are selected and then uses a third point's coordinate (via the x and y parameters)
     * in conjunction with the selected points to create a circle defined by a radius constraint.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDCircleByRadiusCase(double x, double y, CPoint p) {
        if (SelectList.size() < 2) {
            p = (CPoint) this.CatchList(pointlist, x, y);
            if (p != null) {
                this.addObjectToList(p, SelectList);
            }
        } else {
            p = this.SmartgetApointFromXY(x, y);
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);

            Circle cr = new Circle(Circle.RCircle, p);
            Constraint cs = new Constraint(Constraint.RCIRCLE, p1, p2, cr);
            cr.addConstraint(cs);
            this.addConstraintToList(cs);
            this.addCircleToList(cr);

            STATUS = 0;
            clearSelection();
            FirstPnt = SecondPnt = null;
            this.UndoAdded(cr.getDescription());
        }
    }

    /**
     * Handles the DPRatio case.
     *
     * <p>
     * When two points are already selected, creates a new point based on the ratio parameters and applies a ratio constraint
     * between the selected points, optionally adding an auxiliary line if needed.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDPRatioCase(double x,double y,CPoint p){
        if (SelectList.size() < 2) {
            p = this.SelectAPoint(x, y);
            if (p != null) {
                addObjectToList(p, SelectList);
            } else
                clearSelection();
        } else {
            CPoint px = this.SmartgetApointFromXY(x, y);
            if (px != null) {
                CPoint p1 = (CPoint) SelectList.get(0);
                CPoint p2 = (CPoint) SelectList.get(1);

                p = this.CreateANewPoint(x, y);
                Constraint cs = new Constraint(Constraint.PRATIO, p, px, p1, p2, v1, v2);
                CPoint pu = this.addADecidedPointWithUnite(p);
                if (pu == null) {
                    this.addConstraintToList(cs);
                    this.addPointToList(p);
                    CLine ln = fd_line(p1, p2);
                    if (status && (ln == null || !ln.containPT(px))) {
                        CLine ln1 = new CLine(px, p, CLine.LLine);
                        this.addLineToList(ln1);
                    } else {
                        Constraint cs1 = new Constraint(Constraint.PONLINE);
                        cs1.setPolyGenerate(false);
                        cs1.addElement(p);
                        cs1.addElement(ln);
                        this.addConstraintToList(cs1);
                        if (status && ln != null)
                            ln.addApoint(p);
                    }
                    this.UndoAdded(cs.getMessage());
                } else {
                    p = pu;
                }
                clearSelection();
            } else
                clearSelection();
        }
    }

    /**
     * Handles the DTRatio case.
     *
     * <p>
     * Applies a triangle ratio constraint between two selected points. A new point is created,
     * and additional lines or constraints are generated to enforce the specified ratio.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDTRatioCase(double x, double y, CPoint p) {
        if (SelectList.size() < 2) {
            p = this.SelectAPoint(x, y);
            if (p != null) {
                this.addObjectToList(p, SelectList);
            }
        } else {
            p = this.SmartgetApointFromXY(x, y);
            if (SelectList.size() != 2) return;

            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint px = p;
            p = this.CreateANewPoint(x, y);
            double dx = p2.getx() - p1.getx();
            double dy = p2.gety() - p1.gety();

            Constraint cs = new Constraint(Constraint.TRATIO, p, px, p1, p2, v1, v2);
            CPoint pu = this.addADecidedPointWithUnite(p);
            if (pu == null) {
                addConstraintToList(cs);
                addPointToList(p);
                CLine ln = fd_line(p, px);
                if (status && ln == null) {
                    CLine ln1 = new CLine(px, p, CLine.LLine);
                    this.addLineToList(ln1);
                } else {
                    Constraint cs1 = new Constraint(Constraint.PONLINE);
                    cs1.setPolyGenerate(false);
                    cs1.addElement(p);
                    cs1.addElement(ln);
                    this.addConstraintToList(cs1);
                    if (status && ln != null)
                        ln.addApoint(p);
                }
            } else {
                p = pu;
            }
            clearSelection();
            STATUS = 0;
            this.UndoAdded(cs.getMessage());
        }
    }

    /**
     * Handles the DPTDistance case.
     *
     * <p>
     * When three points are selected, this method enforces an equal distance relationship.
     * Depending on whether a line or circle is selected next, it sets up the appropriate constraint and creates a new point.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleDPTDistanceCase(double x, double y, CPoint p) {
        if (SelectList.size() < 3) {
            CPoint pt = this.CreateATempPoint(x, y);
            p = this.SmartPoint(pt);
//                    String s = null;
            if (p != null) {
                addToSelectList(p);
//                        s = (p.m_name + "  selected");
                this.setObjectListForFlash(p);
            }
//                    switch (SelectList.size()) {
//                        case 0:
//                            gxInstance.setTipText(s + ',' + " Please Select a Point");
//                            break;
//                        case 1:
//                            gxInstance.setTipText("first point  " + s + ',' +
//                                    "  please select the second point");
//                            break;
//                        case 2:
//                            gxInstance.setTipText("second point  " + s + ',' +
//                                    "  please select the third point");
//                            break;
//                        case 3:
//                            gxInstance.setTipText("third point  " + s + ',' +
//                                    "  select a line or a circle");
//                    }
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = (CPoint) SelectList.get(2);
            Circle c = null;
            CLine ln = SelectALine(x, y);
            if (ln != null) {
                double r = ln.distance(p3.getx(), p3.gety());
                double r1 = sdistance(p1, p2);
                if (r < r1) {
                    CPoint pt = this.CreateANewPoint(x, y);
                    this.AddPointToLine(pt, ln, false);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, p1, p2, p3, pt);
                    this.charsetAndAddPoly(true);
                    this.addConstraintToList(cs);
                    this.addPointToList(pt);
                    this.UndoAdded(GExpert.getTranslationViaGettext(
                            "Take a point {0} on line {1} such that {2}", pt.m_name,
                            ln.getSimpleName(), p1.m_name + p2.m_name + " = " +
                                    p3.m_name + pt.m_name));

                } else
                    JOptionPane.showMessageDialog(gxInstance, "Can not add a point", "No Solution", JOptionPane.ERROR_MESSAGE);

            } else if ((c = this.SelectACircle(x, y)) != null) {
                CPoint po = c.o;
                double d = sdistance(po, p3);
                double r = c.getRadius();
                double s = sdistance(p1, p2);
                double d1 = d + r;
                double d2 = Math.abs(d - r);
                if (s > d1 || s < d2) {
                    JOptionPane.showMessageDialog(gxInstance, "Can not add a point", "No Solution", JOptionPane.ERROR_MESSAGE);
                } else {
                    CPoint pt = this.CreateANewPoint(0, 0);
                    Constraint cs = new Constraint(Constraint.EQDISTANCE, p1, p2, p3, pt);
                    Constraint cs1 = new Constraint(Constraint.PONCIRCLE, pt, c);
                    this.charsetAndAddPoly(true);
                    if (this.mulSolutionSelect(pt)) {
                        this.addConstraintToList(cs);
                        this.addConstraintToList(cs1);
                        this.addPointToList(pt);
                        c.addPoint(pt);
                                /*
                                this.UndoAdded("Take a point "
                                        + pt.m_name + "on " + c.getDescription() +
                                        " st " + p1.m_name + p2.m_name + " = " +
                                        p3.m_name + pt.m_name);
                                 */
                        this.UndoAdded(GExpert.getTranslationViaGettext(
                                "Take a point {0} on circle {1} such that {2}",
                                pt.m_name, c.getname(), p1.m_name + p2.m_name + " = " +
                                        p3.m_name + pt.m_name));
                    } else {
                        this.ErasedADecidedPoint(pt);
                        gxInstance.setTipText("Failed: can not find a point(P) on Circle " +
                                " that satisfy |" + p1.m_name + p2.m_name +
                                "| = |" + p3.m_name + "P|");
                    }
                }

            }
            clearSelection();
        }
    }

    /**
     * Handles the LRatio case.
     *
     * <p>
     * Establishes a line ratio constraint. It creates a new point such that the ratio between the distances from the selected point
     * is maintained according to provided values.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleLRatioCase(double x, double y, CPoint p) {
        CPoint pt = this.CreateATempPoint(x, y);
        p = this.SmartPoint(pt);
        if (p == null) {
            return;
        }
        if (SelectList.isEmpty()) {
            this.addObjectToList(p, SelectList);
        } else {
            if (p == SelectList.get(0)) {
                return;
            }
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint pp = this.CreateANewPoint(x, y);
            Integer t1 = v1;
            Integer t2 = v2;
            Constraint cs = new Constraint(Constraint.LRATIO, pp, p1, p, t1, t2);
            CPoint pu = this.addADecidedPointWithUnite(pp);
            if (pu == null) {
                this.addConstraintToList(cs);
                this.addPointToList(pp);
            } else {
                pp = pu;
                clearSelection();
                this.resetUndo();
                return;
            }

            CLine ln = null;
            for (Object o : linelist) {
                CLine t = (CLine) o;
                if (t.sameLine(p1, p)) {
                    ln = t;
                    break;
                }
            }
            if (ln != null) {
                ln.addApoint(pp);
            }
            this.charsetAndAddPoly(false);
            clearSelection();
            this.UndoAdded(pp.TypeString() + ":  " + p1.m_name +
                    pp.m_name + " / " + pp.m_name + p.m_name + " = " + t1 + "/" +
                    t2);
        }
    }

    /**
     * Handles the meet case.
     *
     * <p>
     * Selects geometric objects (lines or circles) and computes their intersection point.
     * When two objects are selected, it finds and processes their meeting point.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    private void handleMeetCase(double x, double y) {
        CClass cc = this.SelectALine(x, y);
        if (cc == null)
            cc = this.SelectACircle(x, y);

        if (cc == null) {
            clearSelection();
            return;
        }
        addObjectToList(cc, SelectList);

        if (SelectList.size() == 1) {
            return;
        } else if (SelectList.size() == 2) {
            Object obj1 = SelectList.get(0);
            Object obj2 = SelectList.get(1);
            meetTwoObject(obj1, obj2, false, x, y);
            clearSelection();
        }
    }

    /**
     * Handles the mirror case.
     *
     * <p>
     * Creates mirrored objects based on the selected geometric entities. Depending on the types of the objects (points, lines, or circles),
     * it generates mirror images and sets up the corresponding constraints.
     * </p>
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @param p a context-dependent point parameter
     */
    private void handleMirrorCase(double x, double y, CPoint p) {
        CatchPoint.setXY(x, y);
        CLine ln = null;
        p = this.SmartPoint(CatchPoint);
        if (p == null) {
            ln = this.SmartPLine(CatchPoint);
            if (ln != null) {
                this.addObjectToList(ln, SelectList);
            } else {
                Circle c = this.SmartPCircle(CatchPoint);
                if (c != null) {
                    this.addObjectToList(c, SelectList);
                }
            }
        } else {
            this.addObjectToList(p, SelectList);
        }

        if (SelectList.size() == 2) {
            Object obj1, obj2;
            obj1 = SelectList.get(0);
            obj2 = SelectList.get(1);
            if (obj1 instanceof CPoint && obj2 instanceof CPoint) {
                CPoint p1 = (CPoint) obj1;
                CPoint p2 = (CPoint) obj2;
                CPoint pp = this.CreateANewPoint(0, 0);
                Constraint cs = new Constraint(Constraint.PSYM, pp, p1, p2);
                CPoint pu = this.addADecidedPointWithUnite(pp);
                if (pu == null) {
                    this.addPointToList(pp);
                    this.addConstraintToList(cs);
                    // this.UndoAdded(pp.TypeString() + " is reflection of " +
                    //        p1.TypeString() + " wrt " +
                    //        p2.TypeString());
                    this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", pp.TypeString(),
                            p1.TypeString(), p2.TypeString()));

                } else {
                    pp = pu;
                }

            } else if (obj1 instanceof CPoint && obj2 instanceof CLine) {
                CPoint p1 = (CPoint) obj1;
                CLine line = (CLine) obj2;

                CPoint pp = this.CreateANewPoint(0, 0);
                Constraint cs = new Constraint(Constraint.MIRROR, pp, p1, line);
                CPoint pu = this.addADecidedPointWithUnite(pp);
                if (pu == null) {
                    this.addPointToList(pp);
                    this.addConstraintToList(cs);
                    // this.UndoAdded(pp.TypeString() + " is reflection of " +
                    //        p1.TypeString() + " wrt " +
                    //        line.getDiscription());
                    this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", pp.TypeString(),
                            p1.TypeString(), line.getDiscription()));

                } else {
                    pp = pu;
                }

            } else if (obj1 instanceof CLine && obj2 instanceof CPoint) {
                CLine line = (CLine) obj1;
                CPoint p1 = (CPoint) obj2;

                int exist_point_number = 0;
                Vector<CPoint> vp = new Vector<>();

                for (int i = 0; i < line.points.size(); i++) {
                    CPoint pu = null;
                    CPoint pp = null;
                    Constraint cs = null;

                    CPoint pt = (CPoint) line.points.get(i);
                    if (pt == p1) {
                        pu = pt;
                    } else {
                        pp = this.CreateANewPoint(0, 0);
                        cs = new Constraint(Constraint.PSYM, pp, pt, p1);
                        pu = this.addADecidedPointWithUnite(pp);
                    }
                    if (pu == null) {
                        this.addPointToList(pp);
                        this.addConstraintToList(cs);
                    } else {
                        pp = pu;
                        exist_point_number++;
                    }
                    vp.add(pp);
                }

                if (exist_point_number < line.points.size()) {
                    if (line.points.contains(p1)) {
                        for (CPoint cPoint : vp) {
                            CPoint tt = (CPoint) cPoint;
                            line.addApoint(tt);
                        }
                        this.UndoAdded("reflection");

                    } else {
                        CLine line2 = new CLine(line.type);
                        line2.m_color = line.m_color;
                        line2.m_dash = line.m_dash;
                        line2.m_width = line.m_width;

                        for (CPoint cPoint : vp) {
                            line2.addApoint((CPoint) cPoint);
                        }
                        Constraint cs = new Constraint(Constraint.LINE, vp);
                        this.addConstraintToList(cs);
                        this.addLineToList(line2);
                        // this.UndoAdded(line2.TypeString() +
                        //        " is reflection of " +
                        //        line.getDiscription() + " wrt " +
                        //        p1.TypeString());
                        this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", line2.TypeString(),
                                line.getDiscription(), p1.TypeString()));


                    }

                } else {
                    boolean exists = false;
                    for (Object o : linelist) {
                        CLine ll = (CLine) o;
                        if (ll.points.containsAll(vp)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        CLine line2 = new CLine(line.type);
                        for (CPoint cPoint : vp) {
                            line2.addApoint(cPoint);
                        }
                        line2.m_color = ln.m_color;
                        line2.m_dash = ln.m_dash;
                        line2.m_width = ln.m_width;
                        Constraint cs = new Constraint(Constraint.LINE, vp);
                        this.addConstraintToList(cs);
                        this.addLineToList(line2);
                        this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", line2.getDiscription(),
                                line.getDescription(), p1.TypeString()));


                    } else
                        this.UndoAdded("reflection");
                }
            } else if (obj1 instanceof CLine && obj2 instanceof CLine) {
                CLine line = (CLine) obj1;
                CLine line2 = (CLine) obj2;
                CPoint cp = CLine.commonPoint(line, line2);

                CLine line3 = new CLine(line.type);
                line3.m_color = line.m_color;
                line3.m_dash = line.m_dash;
                line3.m_width = line.m_width;

                int exist_point_number = 0;
                for (int i = 0; i < line.points.size(); i++) {
                    CPoint pt = (CPoint) line.points.get(i);

                    CPoint pp;
                    if (pt == cp) {
                        pp = cp;
                        exist_point_number++;
                    } else {
                        pp = this.CreateANewPoint(0, 0);
                        Constraint cs = new Constraint(Constraint.MIRROR, pp, pt, line2);
                        CPoint pu = this.addADecidedPointWithUnite(pp);
                        if (pu == null) {
                            this.addPointToList(pp);
                            this.addConstraintToList(cs);
                        } else {
                            pp = pu;
                            exist_point_number++;
                        }

                    }
                    line3.addApoint(pp);
                }
                Constraint cs = new Constraint(Constraint.LINE, line3.points);
                addConstraintToList(cs);

                if (exist_point_number < line.points.size()) {
                    this.addLineToList(line3);

                    // this.UndoAdded(line3.getDiscription() +
                    //        " is reflection of " +
                    //        line.getDiscription() + " wrt " +
                    //        line2.getDiscription());
                    this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", line3.getDiscription(),
                            line.getDiscription(), line2.getDiscription()));


                } else {
                    boolean exists = false;
                    for (Object o : linelist) {
                        CLine ll = (CLine) o;
                        if (ll.sameLine(line3)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        this.addLineToList(line3);
                        // this.UndoAdded(line3.getDiscription() +
                        //        " is reflection of " +
                        //        line.getDiscription() + " wrt " +
                        //        line2.getDiscription());
                        this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", line3.getDiscription(),
                                line.getDiscription(), line2.getDiscription()));

                    }
                }

            } else if (obj1 instanceof Circle && obj2 instanceof CPoint) {
                int exist_point_number = 0;

                Circle c1 = (Circle) obj1;
                CPoint p1 = (CPoint) obj2;
                CPoint pp = this.CreateANewPoint(0, 0);
                Constraint cs = new Constraint(Constraint.PSYM, pp, c1.o, p1);
                CPoint pu = this.addADecidedPointWithUnite(pp);
                if (pu == null) {
                    this.addPointToList(pp);
                    this.addConstraintToList(cs);
                } else {
                    exist_point_number++;
                    pp = pu;
                }

                Circle c = null;
                for (int i = 0; i < c1.points.size(); i++) {
                    CPoint pt = (CPoint) c1.points.get(i);
                    p = this.CreateANewPoint(0, 0);
                    cs = new Constraint(Constraint.PSYM, p, pt, p1);
                    CPoint pu1 = this.addADecidedPointWithUnite(p);
                    if (pu1 == null) {
                        this.addPointToList(p);
                        this.addConstraintToList(cs);
                    } else {
                        p = pu1;
                        exist_point_number++;
                    }

                    if (i == 0) {
                        c = new Circle(pp, p);
                        c.m_color = c1.m_color;
                        c.m_dash = c1.m_dash;
                        c.m_width = c1.m_width;
                    } else {
                        c.addPoint(p);
                    }
                }
                cs = new Constraint(Constraint.CIRCLE, c.o);
                cs.addElement(c.points);
                cs.PolyGenerate();

                addConstraintToList(cs);

                if (exist_point_number < c1.points.size() + 1) {
                    this.addCircleToList(c);
                    // this.UndoAdded(c.getDescription() +
                    //         " is reflection of " + c1.getDescription() +
                    //         " wrt " + p1.TypeString());
                    this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", c.getDescription(),
                            c1.getDescription(), p1.TypeString()));

                }

            } else if (obj1 instanceof Circle && obj2 instanceof CLine) {
                int exist_point_number = 0;

                Circle c1 = (Circle) obj1;
                CLine line = (CLine) obj2;
                CPoint pp = this.CreateANewPoint(0, 0);
                Constraint cs = new Constraint(Constraint.MIRROR, pp, c1.o,
                        line);
                CPoint pu1 = this.addADecidedPointWithUnite(pp);
                if (pu1 == null) {
                    this.addPointToList(pp);
                    this.addConstraintToList(cs);
                } else {
                    pp = pu1;
                    exist_point_number++;
                }

                Circle c = null;
                for (int i = 0; i < c1.points.size(); i++) {
                    CPoint pt = (CPoint) c1.points.get(i);
                    p = this.CreateANewPoint(0, 0);
                    cs = new Constraint(Constraint.MIRROR, p, pt, line);
                    CPoint pu2 = this.addADecidedPointWithUnite(p);
                    if (pu2 == null) {
                        this.addPointToList(p);
                        this.addConstraintToList(cs);
                    } else {
                        p = pu1;
                        exist_point_number++;
                    }
                    if (i == 0) {
                        c = new Circle(pp, p);
                        c.m_color = c1.m_color;
                        c.m_dash = c1.m_dash;
                        c.m_width = c1.m_width;
                    } else {
                        c.addPoint(p);
                    }

                }
                assert c != null;
                cs = new Constraint(Constraint.CIRCLE, c.o);
                cs.addElement(c.points);
                cs.PolyGenerate();
                addConstraintToList(cs);
                if (exist_point_number < c1.points.size() + 1) {
                    this.addCircleToList(c);
                    // this.UndoAdded(c.getDescription() +
                    //         " is reflection of " + c1.getDescription() +
                    //         " wrt " + line.getDescription());
                    this.UndoAdded(GExpert.getTranslationViaGettext("{0} is the reflection of {1} wrt {2}", c.getDescription(),
                            c1.getDescription(), line.getDescription()));

                }
            } else {
                CMisc.print("can not mirror by a circle");
            }
            clearSelection();
        }
    }

    /**
     * Handles the midpoint creation case.
     * Selects a point by its coordinates to compute the midpoint between the selected point and a previously stored point.
     *
     * @param x the x-coordinate of the selected point.
     * @param y the y-coordinate of the selected point.
     */
    private void handleDMidpointCase(double x, double y) {

        CPoint tp = this.SelectAPoint(x, y);
        if (tp != null) {
            if (SelectList.size() == 1 && tp != SelectList.get(0)) {
                CPoint tp1 = (CPoint) SelectList.get(0);

                CPoint po = this.CreateANewPoint(0, 0);
                Constraint cs = new Constraint(Constraint.MIDPOINT, po, tp, tp1);
                CPoint pu = this.addADecidedPointWithUnite(po);
                if (pu == null) {
                    this.addConstraintToList(cs);
                    this.addPointToList(po);
                    CLine ln = fd_line(tp, tp1);
                    if (ln != null) {
                        ln.addApoint(po);
                        Constraint cs2 = new Constraint(Constraint.PONLINE, po, ln, false);
                        this.addConstraintToList(cs2);

                    }
                    // this.UndoAdded(po.getname() + ": the midpoint YYY of " + tp1.m_name + tp.m_name);
                    this.UndoAdded(po.getname() + ": " + GExpert.getTranslationViaGettext("midpoint of {0}", tp1.m_name + tp.m_name));

                } else {
                    po = pu;
                }
                clearSelection();
            } else {
                this.addObjectToList(tp, SelectList);
            }
        }
    }

    /**
     * Handles the 3-point circle creation case.
     * Collects three points from user selections and creates a circle passing through them.
     *
     * @param x the x-coordinate for point selection.
     * @param y the y-coordinate for point selection.
     * @param p a temporary point used during the circle creation process.
     */
    private void handleD3PCircleCase(double x, double y, CPoint p) {
        if (STATUS == 0) { // first click
            clearSelection();
            p = SmartgetApointFromXY(x, y);
            this.addObjectToList(p, SelectList);
            STATUS = 1;

        } else if (STATUS == 1) {
            p = SmartgetApointFromXY(x, y);
            this.addObjectToList(p, SelectList);
            if (SelectList.size() == 2) {
                STATUS = 2;
            }

        } else { //third click
            CPoint p1, p2, p3;
            p1 = (CPoint) SelectList.get(0);
            p2 = (CPoint) SelectList.get(1);

            p3 = this.SelectAPoint(x, y);
            if (p3 != null) {
                if (DrawBase.check_Collinear(p1, p2, p3))
                    return;
            }

            if (p3 == null)
                p3 = SmartgetApointFromXY(x, y);
            if (p3 == null) {
                return;
            }


            if (SelectList.contains(p3)) {
                return;
            }

            p = this.CreateANewPoint(0, 0);
            Constraint cs = new Constraint(Constraint.CIRCLE3P, p, p1, p2, p3);
            CPoint pu = this.addADecidedPointWithUnite(p);
            if (pu == null) {
                Circle c = new Circle(p, p1, p2, p3);
                p.m_name = this.get_cir_center_name();
                this.addPointToList(p);
                this.addConstraintToList(cs);
                addCircleToList(c);
                this.UndoAdded(c.getDescription());

            } else {
                p = pu;
                if (!this.isCircleExists(p1, p2, p3)) {
                    Circle c = new Circle(p, p1, p2, p3);
                    this.addCircleToList(c);
                    this.UndoAdded(c.getDescription());
                }
            }

            clearSelection();
            STATUS = 0;
        }
    }

    /**
     * Handles the translate action case.
     * Captures the starting point for a translation action based on the provided coordinates.
     *
     * @param x the x-coordinate of the translation start point.
     * @param y the y-coordinate of the translation start point.
     */
    private void handleTranslateCase(double x, double y) {
        FirstPnt = this.CreateATempPoint(x, y);
    }

    /**
     * Handles the zoom in action.
     * Zooms in at the specified coordinates and recalculates the drawing.
     *
     * @param x the x-coordinate for zooming in.
     * @param y the y-coordinate for zooming in.
     */
    private void handleZoomInCase(double x, double y) {
        zoom_in(x, y, 1);
        reCalculate();
    }

    /**
     * Handles the zoom out action.
     * Zooms out from the specified coordinates and recalculates the drawing.
     *
     * @param x the x-coordinate for zooming out.
     * @param y the y-coordinate for zooming out.
     */
    private void handleZoomOutCase(double x, double y) {
        zoom_out(x, y, 1);
        reCalculate();
    }

    /**
     * Handles the animation case.
     * Depending on the current selection and point detection, triggers an animation on a point, line, circle, or trace.
     *
     * @param x the x-coordinate used for triggering animation.
     * @param y the y-coordinate used for triggering animation.
     * @param p a temporary point used during the animation process.
     */
    private void handleAnimationCase(double x, double y, CPoint p) {
        CatchPoint.setXY(x, y);
        p = this.SmartPoint(CatchPoint);

        if (SelectList.isEmpty()) {
            if (p != null) {
                addToSelectList(p);
            }
            return;
        }
        if (p != null)
            return;

        p = (CPoint) SelectList.get(0);
        CLine line = SmartPLine(CatchPoint);
        if (line != null && !check_animation(p, line))
            return;

        AnimatePanel af = gxInstance.getAnimateDialog();
        if (line != null) {
            clearSelection();
            animate = new AnimateC(p, line, this.Width, this.Height);
            af.setAttribute(animate);
            gxInstance.showAnimatePane();
            this.SetCurrentAction(MOVE);
        } else {
            Circle c = this.SmartPCircle(CatchPoint);
            if (c != null) {
                clearSelection();
                animate = new AnimateC(p, c, this.Width, this.Height);
                af.setAttribute(animate);
                gxInstance.showAnimatePane();
                this.SetCurrentAction(MOVE);
            } else {
                CTrace ct = (CTrace) this.SelectFromAList(tracelist, x, y);
                if (ct != null) {
                    clearSelection();
                    animate = new AnimateC(p, ct, this.Width, this.Height);
                    af.setAttribute(animate);
                    gxInstance.showAnimatePane();
                    this.SetCurrentAction(MOVE);
                }
            }
        }

    }

    /**
     * Handles the direct angle creation case.
     * Based on the selection status, sets up an angle using a temporary point and/or a previously selected line.
     *
     * @param x the x-coordinate for angle creation.
     * @param y the y-coordinate for angle creation.
     */
    private void handleDAngleCase(double x, double y) {

        if (STATUS == 0 && SelectList.isEmpty()) {
            FirstPnt = this.CreateATempPoint(x, y);

            CLine line = SmartPLine(FirstPnt);
            if (line != null) {
                addToSelectList(line);
            }
        } else if (STATUS == 0 && SelectList.size() == 1) {
            SecondPnt = this.CreateATempPoint(x, y);
            CLine line = SmartPLine(SecondPnt);
            if (line != null) {
                CLine l2 = (CLine) SelectList.get(0);
                if (line == l2) {
                    return;
                }

                CAngle ag = new CAngle(l2, line, FirstPnt, SecondPnt);
                addAngleToList(ag);
                ag.move(x, y);
                clearSelection();
                addToSelectList(ag);
                STATUS = 1;
                this.UndoAdded(ag.getDescription(), false, false);
            }
        } else if (STATUS == 1) {
            STATUS = 0;
            clearSelection();
        }
    }

    /**
     * Handles the equal side setting case.
     * Adds a constraint to set equal distances between points or sides based on the selection.
     *
     * @param x the x-coordinate used for setting equal sides.
     * @param y the y-coordinate used for setting equal sides.
     * @param p a temporary point involved in the constraint process.
     */
    private void handleSetEqSideCase(double x, double y, CPoint p) {
        CPoint pt = (CPoint) this.CatchList(pointlist, x, y);
        if (pt == null) {
            clearSelection();
            return;
        }
        if (SelectList.size() == 3) {
            CPoint pt1 = (CPoint) SelectList.get(0);
            CPoint pt2 = (CPoint) SelectList.get(1);
            CPoint pt3 = (CPoint) SelectList.get(2);
            if (STATUS == 1) {
                Constraint cs = new Constraint(Constraint.EQDISTANCE, pt1, pt2, pt3, pt);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
                clearSelection();
                this.UndoAdded(pt1.m_name + pt2.m_name + " = " + pt3.m_name +
                        pt.m_name);
            } else {
                Constraint cs = new Constraint(Constraint.NRATIO, pt1, pt2, pt3, pt, v1, v2);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
                clearSelection();
                this.UndoAdded(pt1.m_name + pt2.m_name + " = " + STATUS +
                        " " + pt3.m_name + pt.m_name);

            }
        } else {
            addToSelectList(pt);
        }
    }

    /**
     * Handles the equal angle setting case.
     * Compares a selected angle with another angle determined from the coordinates and sets them equal if possible.
     *
     * @param x the x-coordinate used for setting equal angle.
     * @param y the y-coordinate used for setting equal angle.
     * @param p a temporary point or indicator used during the process.
     */
    private void handleSetEqAngleCase(double x, double y, CPoint p) {
        if (SelectList.isEmpty()) {
            CAngle ag = CatchAngle(x, y);
            if (ag != null) {
                addToSelectList(ag);
            }
        } else if (SelectList.size() == 1) {
            CAngle ag = CatchAngle(x, y);
            CAngle ag1 = (CAngle) SelectList.get(0);

            if (ag == ag1) {
                clearSelection();
                return;
            }

            if (ag != null) {
                CPoint pd = CAngle.canEqual(ag, ag1);
                if (pd == null) {
                    CMisc.print("the angle is decided,can not be set equal");
                    clearSelection();
                } else {
                    clearSelection();
                    Constraint cs = new Constraint(Constraint.EQANGLE, ag1, ag);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(false);
//                            this.mulSolutionSelect(pd);
//                            this.reCalculate();
                    this.UndoAdded(ag.getDescription() + " = " +
                            ag1.getDescription());
                }
            }
        }

    }

    /**
     * Handles the equal angle constraint for three points.
     * After selecting two angles, prompts for a specific angle parameter and applies a constraint to equalize them.
     *
     * @param x the x-coordinate for angle selection.
     * @param y the y-coordinate for angle selection.
     * @param p a temporary point used during the constraint process.
     */
    private void handleSetEqAngle3PCase(double x, double y, CPoint p) {
        CAngle ag = (CAngle) this.SelectFromAList(anglelist, x, y);
        if (ag == null) {
            return;
        }
        if (SelectList.size() == 2) {
            CAngle ag1 = (CAngle) SelectList.get(0);
            CAngle ag2 = (CAngle) SelectList.get(1);

            Vector alist = this.getSpecificAngleList();
            SpecificAngleDialog dlg = new SpecificAngleDialog(gxInstance, 2, alist);
            dlg.setLocation(400, 400);
            dlg.setTitle("Please select an specific angle");
            dlg.setVisible(true);

            Vector v = dlg.getSpecificAngle();
            if (v.size() == 1) {
                Integer in = (Integer) v.get(0);
                int va = in.intValue();
                Param pm = this.getParaForSpecificAngle(va);

                Constraint cs = new Constraint(Constraint.EQANGLE3P, ag1, ag2, ag, pm, va);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
                clearSelection();
                this.UndoAdded(ag1.getDescription() + " + " +
                        ag2.getDescription() + " + " +
                        ag.getDescription() + " = " +
                        ag.getDescription());
            } else {
                clearSelection();
            }
        } else {
            addToSelectList(ag);
        }
    }

    /**
     * Handles the circle-to-circle tangent constraint case.
     * When one circle is already selected, selects another circle to set a tangent constraint between them.
     *
     * @param x the x-coordinate used for tangent detection.
     * @param y the y-coordinate used for tangent detection.
     * @param p a temporary point used during the process.
     */
    private void handleSetCCTangentCase(double x, double y, CPoint p) {
        Circle c = (Circle) this.SelectFromAList(circlelist, x, y);
        if (c == null) {
            return;
        }
        if (SelectList.size() == 1) {
            Circle c0 = (Circle) SelectList.get(0);
            Constraint cs = new Constraint(Constraint.CCTANGENT, c0, c);
            this.charsetAndAddPoly(false);
            this.addConstraintToList(cs);
            this.UndoAdded(c0.getDescription() + " tangent to " +
                    c.getDescription());
        } else {
            addToSelectList(c);
        }
    }

    /**
     * Handles the square construction case.
     * Determines two points for the construction of a square and invokes square creation logic.
     *
     * @param x the x-coordinate used for square construction.
     * @param y the y-coordinate used for square construction.
     */
    private void handleDSquareCase(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartPoint(CatchPoint);
            if (pt == null) {
                if (SelectList.isEmpty()) {
                    CLine line = this.SmartPLine(CatchPoint);
                    if (line != null) {
                        addToSelectList(line);
                        STATUS = 1;
                        return;
                    }
                }
                pt = this.SmartgetApointFromXY(x, y);
            }

            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                this.setSmartPVLine(pa, pt);
            }
            if (!SelectList.contains(pt)) {
                addToSelectList(pt);
            }
            if (SelectList.size() == 2) {
                STATUS = 2;
            }
        } else if (STATUS == 2) {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            addsquare(p1, p2, CatchPoint);
            clearSelection();
            STATUS = 0;

        }
    }

    /**
     * Handles the circle-circle line (radical axis) construction case.
     * Selects two circles based on the provided coordinates and creates a line representing their radical axis.
     *
     * @param x the x-coordinate used for circle selection.
     * @param y the y-coordinate used for circle selection.
     */
    private void handleDCCLineCase(double x, double y) {
        if (SelectList.isEmpty()) {
            Circle c = this.SmartPCircle(CatchPoint);
            if (c != null) {
                this.addObjectToList(c, SelectList);
            }
        } else if (SelectList.size() == 1) {
            Circle c = this.SmartPCircle(CatchPoint);
            if (c != null) {
                Circle c0 = (Circle) SelectList.get(0);
                if (c0.o == c.o) {
                    clearSelection();
                    return;
                }

                CLine line = new CLine(CLine.CCLine);
                this.addLineToList(line);

                Constraint cs = new Constraint(Constraint.CCLine, line, c0,
                        c);
                this.addConstraintToList(cs);
                line.addconstraint(cs);
                clearSelection();
                this.UndoAdded(line.TypeString() + ": " + GExpert.getTranslationViaGettext(
                        "Radical of {0} and {1}", c0.getDescription(), c.getDescription()));
            }
        }

    }

    /**
     * Handles drawing of an isosceles triangle based on the current selection.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleDIOSTriCase(double x, double y) {
        if (SelectList.size() < 2) {
            CPoint pt = SmartgetApointFromXY(x, y);
            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                setSmartPVLine(pa, pt);
            }
            if (SelectList.isEmpty()) {
                addToSelectList(pt);
            } else if (pt == SelectList.get(0)) {
                clearSelection();
            } else {
                addToSelectList(pt);
            }
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            addisoAngle(p1, p2, CatchPoint, 0);
            clearSelection();
            STATUS = 0;
        }
    }

    /**
     * Handles drawing of an equilateral triangle in three user interaction steps.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleDrawTriAllCase(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            addToSelectList(pt);
            STATUS = 1;
        } else if (STATUS == 1) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (pt == SelectList.get(0)) {
                STATUS = 0;
                clearSelection();
                return;
            }
            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                this.setSmartPVLine(pa, pt);
            }
            CPoint p1 = (CPoint) SelectList.get(0);
            addToSelectList(pt);
            if (fd_line(p1, pt) == null) {
                CLine line = new CLine(p1, pt, CLine.LLine);
                this.addLineToList(line);
            }
            STATUS = 2;
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint pt = CreateANewPoint(x, y);

            Constraint cs = new Constraint(Constraint.PETRIANGLE, pt, p1, p2);
            CPoint pu = this.addADecidedPointWithUnite(pt);
            if (pu == null) {
                addConstraintToList(cs);
                addPointToList(pt);
            } else {
                pt = pu;
            }
            addALine(CLine.LLine, pt, p1);
            addALine(CLine.LLine, pt, p2);
            clearSelection();
            STATUS = 0;
            // FIXME: use a translation key for triangle and substitute the image later for all translations
            UndoAdded(GExpert.getLanguage("Equilateral") + " triangle " + pt.m_name + p1.m_name + p2.m_name);
        }
    }

    /**
     * Handles drawing of a right-angled trapezoid by capturing two points and a new transient point.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleRATrapezoidCase(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);

            if (!SelectList.isEmpty()) {
                CPoint pa = (CPoint) SelectList.get(SelectList.size() - 1);
                this.setSmartPVLine(pa, pt);
            }

            if (!SelectList.contains(pt)) {
                addToSelectList(pt);
            }
            if (SelectList.size() == 2) {
                STATUS = 1;
            }
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = this.SmartgetApointFromXY(x, y);
            CPoint p4 = this.CreateANewPoint(x, y);
            Constraint cs = new Constraint(Constraint.RIGHT_ANGLE_TRAPEZOID, p1, p2, p3, p4);
            CPoint pu = this.addADecidedPointWithUnite(p4);
            if (pu == null) {
                this.addALine(CLine.LLine, p1, p2);
                this.addALine(CLine.LLine, p2, p3);
                this.addALine(CLine.LLine, p3, p4);
                this.addALine(CLine.LLine, p1, p4);
                this.addPointToList(p4);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
            } else
                p4 = pu;
            // FIXME: use better keys
            this.UndoAdded(GExpert.getLanguage("Right") + " trapezoid " + p1.m_name + p2.m_name + p3.m_name + p4.m_name);
            STATUS = 0;
            clearSelection();
        }
    }

    /**
     * Handles drawing of a trapezoid by computing a fourth point via coordinate calculations.
     *
     * @param x the x-coordinate from the current catch point
     * @param y the y-coordinate computed based on the selected points
     */
    private void handleTrapezoide(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (!SelectList.isEmpty()) {
                CPoint pa = (CPoint) SelectList.get(SelectList.size() - 1);
                this.setSmartPVLine(pa, pt);
            }
            if (!SelectList.contains(pt)) {
                addToSelectList(pt);
            } else {
                return;
            }
            if (SelectList.size() == 3) {
                STATUS = 1;
            }
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = (CPoint) SelectList.get(2);
            x = CatchPoint.getx();
            y = (p1.gety() - p2.gety()) * (x - p3.getx()) /
                    (p1.getx() - p3.getx()) + p3.gety();
            // y = (p1.gety() - p2.gety()) * (x - p3.getx()) / (p1.getx() - p2.getx()) + p3.gety();
            CPoint p4 = this.SmartgetApointFromXY(x, y);
            Constraint cs1 = new Constraint(Constraint.TRAPEZOID, p1, p2, p3, p4);
            CPoint pu = this.addADecidedPointWithUnite(p4);
            p4.setXY(x, y);
            if (pu == null) {
                this.addALine(CLine.LLine, p1, p2);
                this.addALine(CLine.LLine, p2, p3);
                this.addALine(CLine.LLine, p3, p4);
                this.addALine(CLine.LLine, p1, p4);
                this.addPointToList(p4);
                this.addConstraintToList(cs1);
                this.charsetAndAddPoly(false);
            } else
                p4 = pu;
            this.reCalculate();
            this.UndoAdded("trapezoid " + p1.m_name + p2.m_name + p3.m_name + p4.m_name);
            STATUS = 0;
            clearSelection();

        }
    }

    /**
     * Handles drawing of a parallelogram by collecting points and creating the missing vertex.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleParallelogram(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            addToSelectList(pt);
            STATUS = 1;
        } else if (STATUS == 1) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (pt == SelectList.get(0)) {
                STATUS = 0;
                clearSelection();
                return;
            }
            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                this.setSmartPVLine(pa, pt);
            }
            addToSelectList(pt);
            STATUS = 2;
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = this.SmartgetApointFromXY(x, y);
            CPoint p4 = this.CreateANewPoint(x, y);
            Constraint cs = new Constraint(Constraint.PARALLELOGRAM, p1, p2, p3, p4);
            CPoint pu = this.addADecidedPointWithUnite(p4);
            if (pu == null) {
                this.addPointToList(p4);
                addALine(CLine.LLine, p1, p2);
                addALine(CLine.LLine, p1, p4);
                addALine(CLine.LLine, p2, p3);
                addALine(CLine.LLine, p3, p4);
                this.addConstraintToList(cs);
                this.charsetAndAddPoly(false);
            } else
                p4 = pu;
            this.UndoAdded("parallelogram " + p1.m_name + p2.m_name + p3.m_name + p4.m_name);
            STATUS = 0;
            clearSelection();
        }

    }

    /**
     * Handles drawing of a rectangle by using two selected points to compute the remaining vertices.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleRectangle(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);

            addToSelectList(pt);
            STATUS = 1;
        } else if (STATUS == 1) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (pt == SelectList.get(0)) {
                STATUS = 0;
                clearSelection();
                return;
            }
            if (SelectList.size() == 1) {
                CPoint pa = (CPoint) SelectList.get(0);
                this.setSmartPVLine(pa, pt);
            }
            addToSelectList(pt);
            STATUS = 2;
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);

            double x1 = p1.getx();
            double y1 = p1.gety();
            double x2 = p2.getx();
            double y2 = p2.gety();

            double xc = CatchPoint.getx();
            double yc = CatchPoint.gety();

            double dlx = x2 - x1;
            double dly = y2 - y1;
            double dl = dlx * dlx + dly * dly;

            double xx = ((y2 - yc) * dlx * dly + dly * dly * xc +
                    dlx * dlx * x2) / dl;
            double yy = ((x2 - xc) * dlx * dly + dlx * dlx * yc +
                    dly * dly * y2) / dl;

            CPoint p3 = this.SmartgetApointFromXY(xx, yy);
            double xt = x + p1.getx() - p2.getx();
            double yt = y + p1.gety() - p2.gety();
            CPoint p4 = this.CreateANewPoint(xt, yt);
            Constraint cs1 = new Constraint(Constraint.RECTANGLE, p1, p2, p3, p4);
            CPoint pu = this.addADecidedPointWithUnite(p4);
            if (pu == null) {
                this.addPointToList(p4);
                CLine tl1 = addALine(CLine.LLine, p1, p2);
                CLine tl2 = addALine(CLine.LLine, p1, p4);
                addALine(CLine.LLine, p2, p3);
                addALine(CLine.LLine, p3, p4);
                addCTMark(tl1, tl2);
                this.addConstraintToList(cs1);
                this.charsetAndAddPoly(false);
            } else
                p4 = pu;
            this.UndoAdded("rectangle " + p1.m_name + p2.m_name + p3.m_name + p4.m_name);
            STATUS = 0;
            clearSelection();

        }
    }

    /**
     * Handles drawing of an isosceles right triangle by capturing base points and computing the apex.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleDrawTriSqIsoCase(double x, double y) {
        if (STATUS == 0) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            addToSelectList(pt);
            STATUS = 1;
        } else if (STATUS == 1) {
            CPoint pt = this.SmartgetApointFromXY(x, y);
            if (pt == SelectList.get(0)) {
                STATUS = 0;
                clearSelection();
                return;
            }
            CPoint p1 = (CPoint) SelectList.get(0);
            addALine(CLine.LLine, p1, pt);
            addToSelectList(pt);
            STATUS = 2;
        } else {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint pt = this.CreateANewPoint(x, y);
            CLine ln1 = new CLine(pt, p1, CLine.LLine);
            CLine ln2 = new CLine(pt, p2, CLine.LLine);
            Constraint cs = new Constraint(Constraint.PERPBISECT, pt, p1, p2);
            Constraint cs1 = new Constraint(Constraint.PERPENDICULAR, ln1, ln2);
            CPoint pu = this.addADecidedPointWithUnite(pt);
            if (pu == null) {
                this.addPointToList(pt);
                this.charsetAndAddPoly(false);
                this.addConstraintToList(cs1);
                this.addConstraintToList(cs);
                this.addLineToList(ln1);
                this.addLineToList(ln2);
            }

            clearSelection();
            STATUS = 0;
            this.UndoAdded("isoceles-right triangle " + pt.m_name + p1.m_name + p2.m_name);
        }
    }

    /**
     * Handles definition of a polygon by either selecting a circle or accumulating points from user input.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     * @param p a reference point used in the polygon definition process
     */
    private void handleDefinePolyCase(double x, double y, CPoint p) {
        if (this.SelectAPoint(x, y) == null && SelectList.isEmpty()) {
            Circle c = SelectACircle(x, y);
            if (c != null) {
                for (Object o : polygonlist) {
                    CPolygon px = (CPolygon) o;
                    if (px.isEqual(c)) break;
                }
                if (this.fd_polygon(c) == null) {
                    CPolygon px = new CPolygon(c);
                    this.addPolygonToList(px);
                    clearSelection();
                    this.UndoAdded(px.getDescription());
                }
            }
        } else {
            FirstPnt = this.CreateATempPoint(x, y);
            p = this.SmartPoint(FirstPnt);
            if (p != null) {
                if (STATUS == 0) {
                    CPolygon cp = new CPolygon();
                    cp.addAPoint(p);
                    addToSelectList(cp);
                    STATUS = 1;
                } else {
                    CPolygon cp = (CPolygon) SelectList.get(0);
                    if (cp.addAPoint(p)) {
                        STATUS = 0;
                        addPolygonToList(cp);
                        clearSelection();
                        this.UndoAdded(cp.getDescription());
                    }
                }
            }
        }
    }

    /**
     * Handles text editing by showing a dialog for a selected text object.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleDTextCase(double x, double y) {
        CText tc = (CText) this.SelectFromAList(textlist, x, y);
        dialog_addText(tc, (int) x, (int) y);
    }

    /**
     * Handles multiple solution selection by checking proximity of solution points to the user input.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     * @param p a reference point used for comparison
     */
    private void handleMulSelectSolutionCase(double x, double y, CPoint p) {
        for (int i = 0; i < solutionlist.size(); i++) {
            p = (CPoint) solutionlist.get(i);
            if (Math.pow(p.getx() - x, 2) + Math.pow(p.gety() - y, 2) < 18 * 18) {
                pSolution.setXY(p.getx(), p.gety());
                solutionlist.clear();
                pSolution = null;
                SetCurrentAction(PreviousAction);
            }
        }
    }

    /**
     * Handles setting a track point and adds a trace if the point is not already traced.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleSetTrackCase(double x, double y) {
        CTrackPt = this.SelectAPoint(x, y);
        boolean r = false;

        for (Object o : tracelist) {
            CTrace tr = (CTrace) o;
            if (tr.isTracePt(CTrackPt)) {
                r = true;
                break;

            }
        }
        if (!r) {
            CTrace t = new CTrace(CTrackPt);
            this.addObjectToList(t, tracelist);
            this.UndoAdded(t.toString());
            if (gxInstance != null)
                gxInstance.setActionMove();
        }
    }

    /**
     * Handles locus creation by selecting one or two points and then a line or circle to define the locus.
     *
     * @param x the x-coordinate of the user input
     * @param y the y-coordinate of the user input
     */
    private void handleLocusCase(double x, double y) {
        int n = SelectList.size();
        if (n <= 1) {
            CPoint pt = this.SelectAPoint(x, y);
            if (pt != null) {
                if (n == 0 && !pt.isAFixedPoint()) {
                    JOptionPane.showMessageDialog(gxInstance, GExpert.getLanguage("The point should be a fix point."),
                            GExpert.getLanguage("Warning"),
                            JOptionPane.WARNING_MESSAGE);
                } else
                    this.addObjectToList(pt, SelectList);
                int k = SelectList.size();
                if (k == 1)
                    gxInstance.setTipText(GExpert.getLanguage("Please select the second point."));
                else if (k == 2)
                    gxInstance.setTipText(GExpert.getLanguage("Please select a line or a circle."));
            }
        } else {
            CPoint pt = (CPoint) SelectList.get(0);
            CPoint pt1 = (CPoint) SelectList.get(1);
            CLine ln = this.SelectALine(x, y);

            if (ln != null) {
                CTrace t = new CTrace(pt, pt1, ln);
                this.addObjectToList(t, tracelist);
                this.UndoAdded(t.toString());
            } else {
                Circle c = this.SelectACircle(x, y);
                if (c != null) {
                    CTrace t = new CTrace(pt, pt1, c);
                    this.addObjectToList(t, tracelist);
                    this.UndoAdded(t.toString());
                }
            }
            clearSelection();
            this.reCalculate();
        }
    }

    /**
     * Handles the computation and addition of a circumcenter, barycenter, orthocenter,
     * or incenter based on the current action. It creates a new point and adds the corresponding
     * constraint if three points have been selected.
     *
     * @param x the x-coordinate of the temporary point
     * @param y the y-coordinate of the temporary point
     * @param p a CPoint object used in the calculation (may be updated)
     */
    private void handleCircumCenter(double x, double y, CPoint p) {
        CPoint pt = this.CreateATempPoint(x, y);
        CPoint tp = this.SmartPoint(pt);
        if (tp != null) {
            this.addObjectToList(tp, SelectList);
        }
        if (SelectList.size() == 3) {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            CPoint p3 = (CPoint) SelectList.get(2);
            CPoint pp = this.CreateANewPoint(x, y);
            Constraint cs = null;
            String s = null;
            if (CurrentAction == BARYCENTER) {
                cs = new Constraint(Constraint.BARYCENTER, pp, p1, p2, p3);
                s = "barycenter";
            } else if (CurrentAction == CIRCUMCENTER) {
                cs = new Constraint(Constraint.CIRCUMCENTER, pp, p1, p2, p3);
                s = "circumcenter";
            } else if (CurrentAction == ORTHOCENTER) {
                cs = new Constraint(Constraint.ORTHOCENTER, pp, p1, p2, p3);
                s = "orthocenter";
            } else if (CurrentAction == INCENTER) {
                cs = new Constraint(Constraint.INCENTER, pp, p1, p2, p3);
                s = "incenter";
                pp.addcstoPoint(cs);
            } else {
                return;
            }

            CPoint pu = this.addADecidedPointWithUnite(pp);
            if (pu == null) {
                this.addPointToList(pp);
                this.addConstraintToList(cs);
                // Here we construct the translation key from s:
                this.UndoAdded(pp.TypeString() + ": " + GExpert.getTranslationViaGettext(
                        s + " of {0}", p1.m_name + p2.m_name + p3.m_name));

            } else {
                p = pu;
            }
            clearSelection();
        }
    }

    /**
     * Handles the NT Angle case.
     * <p>
     * If STATUS is 0, selects a line from the linelist and flashes the step.
     * If STATUS is 1, creates a new NT Angle line using a selected point and adds its constraints.
     * </p>
     *
     * @param x the x-coordinate of the event
     * @param y the y-coordinate of the event
     */
    private void handleNTangleCase(double x, double y) {
        if (STATUS == 0) {
            CLine ln = (CLine) this.SelectFromAList(linelist, x, y);
            if (ln != null) {
                addToSelectList(ln);
            }
            if (SelectList.size() == 3) {
                STATUS = 1;
                Vector v = new Vector();
                v.add(ln);
                this.flashStep(v);
            }
        } else if (STATUS == 1) {
            CPoint pt = this.SelectAPoint(x, y);
            if (pt != null) {
                CLine ln = new CLine(CLine.NTALine);
                ln.addApoint(pt);
                addToSelectList(ln);
                Constraint cs = new Constraint(Constraint.NTANGLE,
                        SelectList);
                clearSelection();
                Constraint cs1 = new Constraint(Constraint.PONLINE, pt, ln, false);
                ln.addconstraint(cs);
                this.addLineToList(ln);
                this.addConstraintToList(cs1);
                this.addConstraintToList(cs);
                this.UndoAdded("eqanle added");
            }
        }

    }

    /**
     * Handles viewing an element based on the given coordinates.
     *
     * @param x the x-coordinate of the view event
     * @param y the y-coordinate of the view event
     */
    private void handleViewElementCase(double x, double y) {
        viewElementFromXY(x, y);
    }

    /**
     * Handles the arrow creation case.
     * <p>
     * If no object is currently selected, adds a point to the selection.
     * Otherwise, creates an arrow between the first selected point and the newly selected point.
     * </p>
     *
     * @param x the x-coordinate of the event
     * @param y the y-coordinate of the event
     */
    private void handleArrowCase(double x, double y) {
        CPoint pt = (CPoint) this.SmartgetApointFromXY(x, y);
        if (pt == null) {
            return;
        }
        if (SelectList.isEmpty()) {
            this.addObjectToList(pt, SelectList);
        } else {
            CPoint tp = (CPoint) SelectList.get(0);
            if (tp == pt) {
                return;
            }
            CArrow ar = new CArrow(pt, tp);
            otherlist.add(ar);
            clearSelection();
            this.UndoAdded("Arrow " + ar.getDescription());
        }
    }

    /**
     * Handles the distance measurement case.
     * <p>
     * Selects a point from the pointlist; if one is already selected, measures the distance between the two points.
     * </p>
     *
     * @param x the x-coordinate for distance measurement
     * @param y the y-coordinate for distance measurement
     */
    private void handleDistanceCase(double x, double y){

        CPoint pt = (CPoint) this.SelectFromAList(pointlist, x, y);
        if (pt == null) {
            return;
        }
        if (SelectList.size() == 0) {
            if (pt != null) {
                this.addObjectToList(pt, SelectList);
            }
        } else {
            CPoint tp = (CPoint) SelectList.get(0);
            if (tp == pt) {
                return;
            }
            CDistance dis = new CDistance(pt, tp);
            distancelist.add(dis);
            clearSelection();
            this.UndoAdded("measure " + dis.getDescription());
        }

    }

    /**
     * Handles the equality mark case.
     * <p>
     * Selects a point; if another point is already selected, creates an equality mark between them.
     * </p>
     *
     * @param x the x-coordinate of the event
     * @param y the y-coordinate of the event
     */
    private void handleEqMark(double x, double y) {
        CPoint pt = (CPoint) this.SelectFromAList(pointlist, x, y);
        if (pt == null) {
            return;
        }
        if (SelectList.size() == 0) {
            if (pt != null) {
                this.addObjectToList(pt, SelectList);
            }
        } else {
            CPoint tp = (CPoint) SelectList.get(0);
            if (tp == pt)
                return;

            Cedmark ce = new Cedmark(pt, tp, STATUS);
            otherlist.add(ce);
            clearSelection();
            this.UndoAdded("mark of " + pt.m_name + tp.m_name);
        }
    }

    /**
     * Handles the right angle mark case.
     * <p>
     * Selects a line; if another line is already selected, adds a right angle mark between them.
     * </p>
     *
     * @param x the x-coordinate of the event
     * @param y the y-coordinate of the event
     */
    private void handleRaMark(double x, double y) {
        CLine ln = (CLine) this.SelectFromAList(linelist, x, y);
        if (ln == null)
            return;
        if (SelectList.size() == 0)
            this.addObjectToList(ln, SelectList);
        else {
            CLine ln1 = (CLine) SelectList.get(0);
            if (ln == ln1)
                return;
            addCTMark(ln, ln1);
            clearSelection();
            this.UndoAdded("Right Angle Mark of " + ln.getDescription() + " and " + ln1.getDescription());
        }
    }

    /**
     * Handles hiding an object.
     * <p>
     * Selects an object at the given coordinates, adds an invisible constraint, and sets it as not visible.
     * </p>
     *
     * @param x the x-coordinate of the hide event
     * @param y the y-coordinate of the hide event
     */
    private void handleHideObject(double x, double y) {
        CClass cc = this.SelectOneFromXY(x, y, 0);
        if (cc != null) {
            Constraint cs = new Constraint(Constraint.INVISIBLE, cc);
            this.addConstraintToList(cs);
            cc.setVisible(false);
            UndoStruct un = this.UndoAdded("Hide " + cc.getDescription());
            if (un != null) {
                un.addRelatedObject(cc);
            }
        }
    }

    /**
     * Handles showing an object.
     * <p>
     * Iterates through constraints to find an invisible object, restores its visibility, and flashes it.
     * </p>
     *
     * @param x the x-coordinate of the show event
     * @param y the y-coordinate of the show event
     */
    private void handleShowObject(double x, double y) {
        CClass cc = null;
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.GetConstraintType() != Constraint.INVISIBLE) {
                continue;
            }
            CClass c1 = (CClass) cs.getelement(0);
            if (c1.visible == false) {
                c1.setVisible(true);
                if (c1.select(x, y)) {
                    cc = c1;
                    Constraint cs1 = new Constraint(Constraint.VISIBLE, cc);
                    this.addConstraintToList(cs1);
                    UndoStruct un = this.UndoAdded("Show " +
                            cc.getDescription());
                    Vector v = new Vector();
                    v.add(cc);
                    this.setObjectListForFlash(v);
                    break;
                } else {
                    c1.setVisible(false);
                }
            }
        }

    }

    /**
     * Handles the SAngle action using the coordinate (x, y) and a point.
     * <p>
     * If no element is selected, attempts to select a line from the catch point.
     * If one element is selected, selects a point if it lies on the selected line.
     * With two elements selected, calculates two slope candidates and chooses one based on proximity,
     * then adds a new angle constraint and performs related updates.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     * @param p the point parameter used in processing
     */
    private void handleSAngle(double x, double y, CPoint p) {
        int n = SelectList.size();
        if (n == 0) {
            CLine line = this.SmartPLine(CatchPoint);
            if (line != null && line.points.size() >= 2) {
                addToSelectList(line);
            }
        } else if (n == 1) {
            p = SelectAPoint(x, y);
            CLine ln1 = (CLine) SelectList.get(0);
            if (p != null && ln1.pointOnLine(p))
                addToSelectList(p);

        } else if (n == 2) {
            CLine ln1 = (CLine) SelectList.get(0);
            p = (CPoint) SelectList.get(1);


            double k = ln1.getK();
            double k1 = Constraint.get_sp_ag_value(STATUS);
            double kx1 = (k + k1) / (1 - k * k1);
            double kx2 = (k - k1) / (1 + k * k1);

            double r1 = CLine.distanceToPoint(p.getx(), p.gety(), kx1, x, y);
            double r2 = CLine.distanceToPoint(p.getx(), p.gety(), kx2, x, y);


            Integer I = null;
            int id = 0;

            if (r1 <= r2) {
                I = -STATUS;
                id = add_sp_angle_value(-STATUS);
            } else {
                I = STATUS;
                id = add_sp_angle_value(STATUS);
            }
            CLine ln = new CLine(CLine.SALine);
            ln.addApoint(p);
            Constraint cs = new Constraint(Constraint.SANGLE, ln1, ln, I);
            cs.proportion = id;

            ln.addconstraint(cs);
            addConstraintToList(cs);
            this.addLineToList(ln);
            this.UndoAdded(ln.getDescription());
            clearSelection();
        }
    }

    /**
     * Handles the double boundary line action using the coordinate (x, y).
     * <p>
     * Adds a point based on the mouse event and, if two distinct points are collected,
     * creates a boundary line between them along with its associated constraint.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     */
    private void handleDBLine(double x, double y){
        addSelectPoint(x, y);
        if (SelectList.size() == 2) {
            CPoint p1 = (CPoint) SelectList.get(0);
            CPoint p2 = (CPoint) SelectList.get(1);
            if (p1 != p2) {
                CLine ln = new CLine(CLine.BLine);
                Constraint cs = new Constraint(Constraint.BLINE, ln, p1, p2);
                ln.addconstraint(cs);
                this.addLineToList(ln);
                this.addConstraintToList(cs);
                clearSelection();
                this.UndoAdded("BLine " + ln.getDescription());
            }
        }
    }

    /**
     * Handles drawing a tangent line to a circle using the coordinate (x, y).
     * <p>
     * If no object is selected, identifies a circle based on the catch point.
     * Otherwise, if the coordinates lie on the selected circle,
     * calculates a point on the circle and creates a tangent line with an associated constraint.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     */
    private void handleDTCLine(double x, double y) {
        CatchPoint.setXY(x, y);

        if (SelectList.size() == 0) {
            Circle c = SmartPCircle(CatchPoint);
            if (c != null)
                addToSelectList(c);
        } else {
            Circle c = (Circle) SelectList.get(0);
            if (c.on_circle(x, y)) {
                CPoint p1 = SmartgetApointFromXY(x, y);
                CLine ln = new CLine(p1, CLine.TCLine);
                Constraint cs = new Constraint(Constraint.TCLINE, c, ln, p1);
                this.addConstraintToList(cs);
                ln.addconstraint(cs);
                this.addLineToList(ln);
            }
        }
    }

    /**
     * Handles the creation of a common tangent line between two circles using the coordinate (x, y).
     * <p>
     * If one circle is already selected and the new circle is distinct and non-overlapping,
     * creates new points and adds a tangent constraint between the circles.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     */
    private void handleCCTANGent(double x, double y) {
        Circle c = this.SelectACircle(x, y);
        if (c != null) {
            int n = SelectList.size();
            if (n == 1) {
                Circle c1 = (Circle) SelectList.get(0);
                if (c != c1 && c.o != c1.o) {
                    CPoint p1 = this.CreateANewPoint(0, 0);
                    CPoint p2 = this.CreateANewPoint(x, y);
                    c1.addPoint(p1);
                    c.addPoint(p2);
                    Constraint cs = new Constraint(Constraint.CCTANGENT_LINE, p1, p2, c1, c);
                    this.addPointToList(p1);
                    this.addPointToList(p2);
                    this.addConstraintToList(cs);
                    this.charsetAndAddPoly(false);
                    this.UndoAdded("TANGENT LINE");
                }
            } else
                this.addObjectToList(c, SelectList);
        }
    }

    /**
     * Handles the ratio constraint action using the coordinate (x, y) and a point.
     * <p>
     * Selects a point based on the provided coordinates and avoids duplicate successive selections.
     * When eight points are collected, creates a ratio constraint and updates relevant lists.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     * @param p the point parameter used in processing
     */
    private void handleRatio(double x, double y, CPoint p) {
        int n = SelectList.size();
        p = SelectAPoint(x, y);
        if (p != null) {
            if (n % 2 != 0 && p == SelectList.get(n - 1))
                return;
            addToSelectList(p);
            setObjectListForFlash(p);
        }
        if (SelectList.size() == 8) {
            Constraint cs = new Constraint(Constraint.RATIO, SelectList);
            this.addConstraintToList(cs);
            this.charsetAndAddPoly(false);
            this.UndoAdded("RATIO");
            clearSelection();
        }
    }

    /**
     * Handles the equivalence (area-preserving transformation) action using the coordinate (x, y) and a point.
     * <p>
     * Depending on the current status, selects a polygon and then a point or line segment.
     * Creates a new polygon with an appropriate equivalence constraint to record the transformation.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     * @param p the point parameter used in processing
     */
    private void handleEquivalence(double x, double y, CPoint p) {
        if (STATUS == 0) {
            CPolygon g = (CPolygon) this.SelectFromAList(polygonlist, x, y);
            if (g != null) {
                addObjectToList(g, SelectList);
                STATUS = 1;
            }
        } else if (STATUS == 1) {
            CPoint pt = this.SelectAPoint(x, y);
            if (pt != null) {
                addToSelectList(pt);
                STATUS = 2;
            } else {
                CPolygon g = (CPolygon) SelectList.get(0);
                int n = g.getPtn();

                for (int i = 0; i < n - 1; i++) {
                    CPoint p1 = g.getPoint(i);
                    CPoint p2 = g.getPoint(i + 1);
                    if (CLine.mouse_on_line(x, y, p1.getx(), p1.gety(), p2.getx(), p2.gety())) {
                        addToSelectList(p1);
                        addToSelectList(p2);
                        vx1 = x;
                        vy1 = y;
                        STATUS = 3;
                        break;
                    }
                }
            }
            if (STATUS == 1) {
                STATUS = 0;
                clearSelection();
            }

        } else if (STATUS == 2) {
            CPolygon g = (CPolygon) SelectList.get(0);
            CPoint p1 = (CPoint) SelectList.get(1);
            CPoint t1 = g.getPreviousePoint(p1);
            CPoint t2 = g.getNextPoint(p1);
            double[] r = getPTInterSection(x, y, p1.getx(), p1.gety()
                    , t1.getx(), t1.gety(), t2.getx(), t2.gety());
            CPoint pt = this.SelectAPoint(r[0], r[1]);

            if (pt != null && pt != t1) {
                CPolygon poly = new CPolygon();
                poly.copy(g);
                int t = g.getPtn();

                for (int i = 0; i < t; i++) {
                    CPoint m = g.getPoint(i);
                    if (m == p1)
                        m = pt;
                    poly.addAPoint(m);
                }
                if (this.findPolygon(poly.pointlist) != g) {
                    g.setVisible(false);
                    Constraint cs = new Constraint(Constraint.EQUIVALENCE1, g, poly);
                    this.addConstraintToList(cs);
                    this.addObjectToList(poly, polygonlist);
                    this.UndoAdded("Area-Preserving");//+ g.getDescription() + " transformed to " + poly.getDescription());
                }
            }
            STATUS = 0;
            clearSelection();
            g.setDraggedPoints(null, null, 0, 0);

        } else if (STATUS == 3) {
            CPolygon g = (CPolygon) SelectList.get(0);
            CPoint t1 = (CPoint) SelectList.get(1);
            CPoint t2 = (CPoint) SelectList.get(2);
            double dx = x - vx1;
            double dy = y - vy1;

            CPoint pt1 = this.SelectAPoint(t1.getx() + dx, t1.gety() + dy);
            CPoint pt2 = this.SelectAPoint(t2.getx() + dx, t2.gety() + dy);
            if (pt1 != null && pt2 != null && (pt1 != t1 || pt2 != t2)) {
                CPolygon poly = new CPolygon();
                poly.copy(g);
                int t = g.getPtn();

                for (int i = 0; i < t; i++) {
                    CPoint m = g.getPoint(i);
                    if (m == t1)
                        m = pt1;
                    else if (m == t2)
                        m = pt2;

                    poly.addAPoint(m);
                }
                if (this.findPolygon(poly.pointlist) != g) {
                    g.setVisible(false);
                    Constraint cs = new Constraint(Constraint.EQUIVALENCE2, g, poly);
                    this.addConstraintToList(cs);
                    this.addObjectToList(poly, polygonlist);
                    this.UndoAdded("Area-Preserving");//g.getDescription() + " transformed to " + poly.getDescription());
                }
            }

            STATUS = 0;
            clearSelection();
            g.setDraggedPoints(null, null, 0, 0);
        }
    }

    /**
     * Handles the free transformation of a polygon using the coordinate (x, y).
     * <p>
     * If no polygon is selected, attempts to select one based on the coordinates.
     * Otherwise, adds the selected point to the transformation sequence of the polygon,
     * and once all required points are gathered, applies and finalizes the free transform.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     */
    private void handleFreeTransform(double x, double y) {
        if (STATUS == 0) {
            CPolygon g = (CPolygon) this.SelectAPolygon(x, y);//SelectFromAList(polygonlist, x, y);
            if (g != null) {
                this.addObjectToList(g, SelectList);
                STATUS = 1;
            }
        } else {
            CPoint pt = this.SelectAPoint(x, y);
            CPolygon poly = (CPolygon) SelectList.get(0);

            if (pt == null) {
                STATUS = 0;
                clearSelection();
                poly.setDraggedPointsNull();
            } else {
                if (SelectList.size() == 1) {
                    Vector v = poly.getDraggedPoints();
                    boolean already = false;
                    for (int i = 0; i < v.size() / 2; i++) {
                        if (v.get(i * 2) == pt) {
                            already = true;
                            break;
                        }
                    }
                    if (!already)
                        addToSelectList(pt);
                } else {
                    CPoint t1 = (CPoint) SelectList.get(1);
                    poly.addDraggedPoints(t1, pt);
                    SelectList.remove(t1);
                    if (poly.allDragged()) {
                        add_free_transform();
                    }
                }
            }

        }

    }

    /**
     * Handles the transformation (rotation and/or translation) of a polygon using the coordinate (x, y).
     * <p>
     * On initial selection, stores the starting point and enables transformation mode.
     * In subsequent steps, captures additional points, computes new positions for the polygon,
     * applies the transformation, and creates a constraint to record the change.
     *
     * @param x the x-coordinate of the mouse event
     * @param y the y-coordinate of the mouse event
     */
    private void handleTransform(double x, double y) {
        if (STATUS == 0) {
            CPolygon g = (CPolygon) this.SelectAPolygon(x, y); // SelectFromAList(polygonlist, x, y);
            if (g != null) {
                this.addObjectToList(g, SelectList);
                catchX = x;
                catchY = y;
                STATUS = 1;
                FirstPnt = SecondPnt = ThirdPnt = null;
            }
        } else if (STATUS == 1 || STATUS == 2) {
            if (STATUS == 2 && (FirstPnt == null || ThirdPnt == null)) {
                CPoint pt = this.SelectAPoint(x - vx1, y - vy1);
                if (pt != null) {
                    x = pt.getx() + vx1;
                    y = pt.gety() + vy1;
                }
                if (FirstPnt == null) {
                    FirstPnt = this.CreateATempPoint(x, y);
                    SecondPnt = pt;
                    catchX = x - vx1;
                    catchY = y - vy1;
                } else {
                    ThirdPnt = this.CreateATempPoint(x - vx1, y - vy1);
                }
            } else {
                CPolygon poly = (CPolygon) SelectList.get(0);
                clearSelection();
                STATUS = 0;

                int n = poly.getPtn();
                double cx = catchX + vx1;
                double cy = catchY + vy1;
                double sin = Math.sin(vangle);
                double cos = Math.cos(vangle);

                if (Math.abs(vangle) < CMisc.ZERO) {
                    PolygonTransPointsCreated(poly);
                }

                for (int i = 0; i < n; i++) {
                    CPoint t = poly.getPoint(i);
                    double tx = (t.getx() + vx1);
                    double ty = (t.gety() + vy1);

                    tx -= cx;
                    ty -= cy;
                    double mx = (tx) * cos - (ty) * sin;
                    double my = (tx) * sin + (ty) * cos;
                    tx = mx + cx;
                    ty = my + cy;
                    CPoint t1 = this.SelectAPoint(tx, ty);
                    if (t1 == null) {
                        clearSelection();
                        break;
                    }
                    addToSelectList(t1);
                }
                if (!SelectList.isEmpty()) {
                    CPolygon poly1 = new CPolygon();
                    poly1.setPoints(SelectList);
                    if (this.findPolygon(SelectList) != poly) {
                        Constraint cs = new Constraint(Constraint.TRANSFORM, poly, poly1, SecondPnt);

                        int r = -1;

                        if (CMisc.TransComfirmed) {
                            String s1 = poly.getDescription() + " is transformed to " + poly1.getDescription();
                            String s2 = "Do you want to keep the original polygon visible?";
                            TransformConfirmDialog dlg = new TransformConfirmDialog(gxInstance.getFrame(), s1, s2);
                            gxInstance.centerDialog(dlg);
                            dlg.setVisible(true);
                            r = dlg.getResult();
                        } else
                            r = 1;

                        if (r == 0) {//JOptionPane.YES_OPTION) {
                            cs.proportion = 0;
                        } else if (r == 1) {
                            poly.setVisible(false);
                            cs.proportion = 1;
                        }
                        if (r != 2) {
                            this.addObjectToList(poly1, polygonlist);
                            poly1.copy(poly);
                            this.addConstraintToList(cs);
//                                    String s = "Isometry Transforming";
//                                    if (Math.abs(vangle) < CMisc.ZERO)
//                                        s = "Transforming";
//                                    else if (SecondPnt != null)
//                                        s = "Rotating";

                            String s = poly.getDescription() + " = " + poly1.getDescription();
                            this.UndoAdded(s);//);
                        }
                    }

                }
                STATUS = 0;
                clearSelection();
                vtrx = vtry = vx1 = vy1 = vangle = 0.0;
                FirstPnt = SecondPnt = ThirdPnt = null;
            }
        }
    }

    /**
     * Handles the "mouse down" (button press) event within the drawing canvas.
     *
     * <p>This method interprets user input based on the current action mode
     * (such as SELECT, MOVE, D_POINT, etc.) and performs corresponding
     * geometry-related operations. These may include selecting or modifying
     * points, creating lines, circles, constraints, or initiating transformations.</p>
     *
     * <p>Behavior depends on {@code CurrentAction}, which is evaluated in a large
     * switch-case structure that includes many interactive drawing modes.</p>
     *
     * @param x The x-coordinate of the mouse click, in screen or canvas coordinates
     * @param y The y-coordinate of the mouse click, in screen or canvas coordinates
     * @see #CurrentAction
     * @see #SelectList
     * @see #CatchPoint
     * @see #STATUS
     */
    public void DWButtonDown(double x, double y) {
        CPoint p = null;
        CatchList.clear();
        IsButtonDown = true;
        if (SNAP && CurrentAction != SELECT) {
            double[] r = getSnap(x, y);
            x = r[0];
            y = r[1];
        }
        CatchPoint.setXY(x, y);

        switch (this.CurrentAction) {
            case SELECT:
                handleSelectCase(x, y);
                break;
            case MOVE:
                handleMoveCase(x, y);
                break;
            case D_POINT:
                handleDpointCase(x, y, p);
                break;
            case TRIANGLE:
                handleTriangleCase(x, y);
                break;
            case V_LINE:
                handleVLineCase(x, y);
                break;
            case D_LINE:
                handleDLineCase(x, y);
                break;
            case D_POLYGON:
                handleDPolygonCase(x,y);
                break;
            case D_PARELINE:
                handleDPareLineCase(x,y);
                break;
            case D_PERPLINE:
                handleDPerpLineCase(x,y);
                break;
            case D_ALINE:
                handleDAlineCase(x,y);
                break;
            case D_ABLINE:
                handleDABlineCase(x,y,p);
                break;
            case D_PFOOT:
                handleDPfootCase(x,y,p);
                break;
            case PERPWITHFOOT:
                handlePerpWithFoot(x,y);
                break;
            case D_CIRCLE:
                handleDCircleCase(x,y,p);
                break;
            case D_CIRCLEBYRADIUS:
                handleDCircleByRadiusCase(x,y,p);
                break;
            case D_PRATIO:
                handleDPRatioCase(x,y,p);
                break;
            case D_TRATIO:
                handleDTRatioCase(x,y,p);
                break;
            case D_PTDISTANCE:
                handleDPTDistanceCase(x,y,p);
                break;
            case LRATIO:
                handleLRatioCase(x,y,p);
                break;
            case MEET:
                handleMeetCase(x,y);
                break;
            case MIRROR:
                handleMirrorCase(x,y,p);
                break;
            case D_MIDPOINT:
                handleDMidpointCase(x,y);
                break;
            case D_3PCIRCLE:
                handleD3PCircleCase(x,y,p);
                break;
            case TRANSLATE:
                handleTranslateCase(x,y);
                break;
            case ZOOM_IN:
                handleZoomInCase(x,y);
                break;
            case ZOOM_OUT:
                handleZoomOutCase(x,y);
                break;
            case ANIMATION:
                handleAnimationCase(x,y,p);
                break;
            case D_ANGLE:
                handleDAngleCase(x,y);
                break;
            case SETEQSIDE:
                handleSetEqSideCase(x,y,p);
                break;
            case SETEQANGLE:
                handleSetEqAngleCase(x,y,p);
                break;
            case SETEQANGLE3P:
                handleSetEqAngle3PCase(x,y,p);
                break;
            case SETCCTANGENT:
                handleSetCCTangentCase(x,y,p);
                break;
            case D_SQUARE:
                handleDSquareCase(x,y);
                break;
            case D_CCLINE:
                handleDCCLineCase(x,y);
                break;
            case D_IOSTRI:
                handleDIOSTriCase(x,y);
                break;
            case DRAWTRIALL:
                handleDrawTriAllCase(x,y);
                break;
            case RA_TRAPEZOID:
                handleRATrapezoidCase(x,y);
                break;
            case TRAPEZOID:
                handleTrapezoide(x,y);
                break;
            case PARALLELOGRAM:
                handleParallelogram(x,y);
                break;
            case RECTANGLE:
                handleRectangle(x,y);
                break;
            case DRAWTRISQISO:
                handleDrawTriSqIsoCase(x,y);
                break;
            case DEFINEPOLY:
                handleDefinePolyCase(x,y,p);
                break;
            case D_TEXT:
                handleDTextCase(x,y);
                break;
            case MULSELECTSOLUTION:
                handleMulSelectSolutionCase(x,y,p);
                break;
            case SETTRACK:
                handleSetTrackCase(x,y);
                break;
            case LOCUS:
                handleLocusCase(x,y);
                break;
            case CIRCUMCENTER:
                handleCircumCenter(x,y,p);
                break;
            case NTANGLE:
                handleNTangleCase(x,y);
                break;
            case VIEWELEMENT:
                handleViewElementCase(x,y);
                break;
            case ARROW:
                handleArrowCase(x,y);
                break;
            case DISTANCE:
                handleDistanceCase(x,y);
                break;
            case EQMARK:
                handleEqMark(x,y);
                break;
            case RAMARK:
                handleRaMark(x,y);
                break;
            case HIDEOBJECT:
                handleHideObject(x,y);
                break;
            case SHOWOBJECT:
                handleShowObject(x,y);
                break;
            case SANGLE:
                handleSAngle(x,y,p);
                break;
            case D_BLINE:
                handleDBLine(x,y);
                break;
            case D_TCLINE:
                handleDTCLine(x,y);
                break;
            case CCTANGENT:
                handleCCTANGent(x,y);
                break;
            case RATIO:
                handleRatio(x,y,p);
                break;
            case EQUIVALENCE:
                handleEquivalence(x,y,p);
                break;
            case FREE_TRANSFORM:
                handleFreeTransform(x,y);
                break;
            case TRANSFORM:
                handleTransform(x,y);
                break;
            default:
                break;
        }
    }

    /**
     * Adds a free transform to the selected polygon.
     * <p>
     * This method creates a transformed copy of the selected polygon, adds a constraint
     * for the transformation, and updates the polygon list. It also clears the selection
     * and resets the status.
     */
    public void add_free_transform() {
        CPolygon p = (CPolygon) SelectList.get(0);
        Vector v = p.getTransformedPoints();
        CPolygon p1 = new CPolygon();
        p1.copy(p);
        p1.setPoints(v);
        Constraint cs = new Constraint(Constraint.TRANSFORM1, p, p1, null);
        p.setVisible(false);
        this.addConstraintToList(cs);
        this.addObjectToList(p1, polygonlist);
        clearSelection();
        STATUS = 0;
        p.setDraggedPointsNull();
        this.UndoAdded(p.getDescription() + " transformed to " + p1.getDescription());
    }

    /**
     * Finds the intersection point of two geometric objects.
     *
     * @param obj1 the first geometric object (CLine or Circle)
     * @param obj2 the second geometric object (CLine or Circle)
     * @param d    a boolean flag indicating some condition (not specified)
     * @param x    the x-coordinate for the intersection calculation
     * @param y    the y-coordinate for the intersection calculation
     * @return the intersection point, or null if no intersection is found
     */
    public CPoint meetTwoObject(Object obj1, Object obj2, boolean d, double x, double y) {
        if (obj1 instanceof CLine && obj2 instanceof CLine) {
            return MeetDefineAPoint((CLine) obj1, (CLine) obj2);
        } else if (obj1 instanceof Circle && obj2 instanceof Circle) {
            return MeetCCToDefineAPoint((Circle) obj1, (Circle) obj2, d, x, y);
        } else {
            if (obj1 instanceof CLine && obj2 instanceof Circle) {
                return MeetLCToDefineAPoint((CLine) obj1, (Circle) obj2, d, x, y);
            } else if (obj1 instanceof Circle && obj2 instanceof CLine) {
                return MeetLCToDefineAPoint((CLine) obj2, (Circle) obj1, d, x, y);
            }
        }
        return null;
    }

    /**
     * Adds a point to the selection list based on the given coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void addSelectPoint(double x, double y) {
        CPoint p = this.SelectAPoint(x, y);
        if (p != null && !SelectList.contains(p)) {
            addToSelectList(p);
        }
    }

    /**
     * Adds a line between two points to the line list.
     *
     * @param t  the type of the line
     * @param p1 the first point
     * @param p2 the second point
     * @return the added line, or the existing line if it already exists
     */
    public CLine addALine(int t, CPoint p1, CPoint p2) {
        CLine ln1 = this.fd_line(p1, p2);
        if (ln1 != null) {
            return ln1;
        }
        CLine ln = new CLine(p1, p2, t);
        this.addLineToList(ln);
        return ln;
    }

    /**
     * Retrieves the construction steps from the drawing.
     *
     * @return a vector containing the construction steps
     */
    public Vector getConstructionFromDraw() {
        Vector alist = new Vector();
        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            if (cs.csd != null)
                alist.add(cs.csd);
            if (cs.csd1 != null)
                alist.add(cs.csd1);
        }
        Cons st = new Cons(Gib.C_POINT, pointlist.size());
        for (int i = 0; i < pointlist.size(); i++)
            st.add_pt(pointlist.get(i));
        alist.add(0, st);
        return alist;
    }

    /**
     * Finds a polygon in the polygon list that matches the given vector of points.
     *
     * @param v the vector of points
     * @return the matching polygon, or null if no match is found
     */
    public CPolygon findPolygon(Vector v) {
        for (int i = 0; i < polygonlist.size(); i++) {
            CPolygon p = (CPolygon) polygonlist.get(i);
            if (p.check_eq(v))
                return p;
        }
        return null;
    }

    /**
     * Finds a polygon in the polygon list that matches the given vector of points,
     * considering rotational and directional equivalence.
     *
     * @param v the vector of points
     * @return the matching polygon, or null if no match is found
     */
    public CPolygon findPolygon1(Vector v) {
        for (int i = 0; i < polygonlist.size(); i++) {
            CPolygon p = (CPolygon) polygonlist.get(i);
            if (p.check_rdeq(v))
                return p;
        }
        return null;
    }

    /**
     * Checks if auto animation is possible.
     *
     * @return true if auto animation is possible, false otherwise
     */
    public boolean canAutoAnimate() {
        if (animate != null) {
            return true;
        }
        return false;
    }

    /**
     * Toggles auto animation on or off.
     *
     * @return true if auto animation is started, false if it is stopped
     */
    public boolean autoAnimate() {
        if (canAutoAnimate()) {
            AnimatePanel af = gxInstance.getAnimateDialog();

            if (af.isRunning()) {
                af.stopA();
                gxInstance.anButton.setSelected(false);
                return false;
            } else {
                af.setAttribute(this.animate);
                af.startA();
                gxInstance.anButton.setSelected(true);
                return true;
            }
        } else {
            gxInstance.anButton.setEnabled(false);
        }
        return false;
    }

    /**
     * Automatically shows the next step in the construction.
     */
    public void autoShowstep() {
        this.autoUndoRedo();
    }

    /**
     * Toggles automatic undo and redo actions.
     */
    public void autoUndoRedo() {
        if (timer_type == 1) {
            timer.stop();
            this.redo();
            timer_type = 0;

        } else if (timer_type == 0) {
            if (this.undolist.size() == 0 && this.redolist.size() == 0) {
                return;
            }

            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(700, this);
            timer.setInitialDelay(700 * 2);
            timer.start();
            timer_type = 1;
        }
    }


    /**
     * Handles action events triggered by the timer or other sources.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj == timer) {
            if (timer_type == 1) {
                if (this.redolist.size() == 0) {
                    if (timer.getDelay() == 1400) {
                        timer.setDelay(1200);
                        return;
                    }
                    timer.setDelay(1200);
                    this.Undo();
                    this.setUndoStructForDisPlay(null, false);
                } else {
                    if (isFlashFinished()) {
                        UndoStruct undo = (UndoStruct) redolist.get(redolist.size() - 1);
                        this.redo_step();
                        this.setUndoStructForDisPlay(undo, false);
                    }
                }
            } else if (timer_type == 2) {
                if (cpfield == null) {
                    return;
                }
                if (!this.nextProveStep()) {
                    this.proveStop();
                }
            } else if (timer_type == 3) {
            }
        }
        panel.repaint();
    }

    /**
     * Updates the delay for all flash objects in the flash list.
     */
    public void updateFlashDelay() {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            f.updateTimer();
        }
    }

    /**
     * Sets the delay for the timer.
     *
     * @param delay the delay in milliseconds
     */
    public void setTimerDelay(int delay) {
        if (timer == null) {
            return;
        }
        timer.setDelay(delay);
    }

    /**
     * Displays the properties of the specified object in the dialog.
     *
     * @param obj the object to view
     */
    public void viewElement(CClass obj) {
        if (obj == null) {
            return;
        }
        CClass cc = (CClass) obj;

        if (gxInstance != null) {
            gxInstance.getDialogProperty().setVisible(true);
            gxInstance.cp.SetPanelType(cc);
        }
    }

    /**
     * Starts the animation.
     */
    public void animationStart() {
        animate.startAnimate();
    }

    /**
     * Stops the animation and recalculates the drawing.
     */
    public void animationStop() {
        if (animate != null) {
            animate.stopAnimate();
        }
        this.reCalculate();
    }

    /**
     * Updates the animation on each timer tick and recalculates the drawing.
     */
    public void animationOntime() {
        animate.onTimer();
        this.reCalculate();
    }

    /**
     * Selects an object from the list based on the given coordinates.
     *
     * @param v the list of objects
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected object, or null if no object is selected
     */
    public CClass CatchList(Vector v, double x, double y) {
        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            if (cc.select(x, y)) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Selects an angle from the list based on the given coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected angle, or null if no angle is selected
     */
    public CAngle CatchAngle(double x, double y) {
        for (int i = 0; i < anglelist.size(); i++) {
            CAngle ag = (CAngle) anglelist.get(i);
            if (ag.select(x, y)) {
                return ag;
            }
        }
        return null;
    }

    /**
     * Displays a dialog to add or edit text at the specified coordinates.
     *
     * @param tc the text object
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     */
    public void dialog_addText(CText tc, int x, int y) {
        TextFrame tf = new TextFrame(gxInstance, x, y);
        tf.setText(tc);
        gxInstance.centerDialog(tf);
        tf.setVisible(true);
    }

    /**
     * Adds a point at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the added point
     */
    public CPoint SmartgetApointFromXY(double x, double y) {
        CPoint pt = SmartAddPoint(x, y);
        return pt;
    }

    /**
     * Adds a point with the specified name at the given coordinates.
     *
     * @param x    the x-coordinate
     * @param y    the y-coordinate
     * @param name the name of the point
     * @return the added point
     */
    public CPoint SmartgetApointFromXY(double x, double y, String name) {
        CPoint pt = SmartAddPoint(x, y, name);
        return pt;
    }

    /**
     * Adds a point to the point list and assigns a name if not already set.
     *
     * @param p the point to add
     */
    public void addPointToList(CPoint p) {
        if (p == null)
            return;

        if (pointlist.contains(p)) {
            return;
        }

        while (true && !p.hasNameSet()) {
            String s = getPointNameByCount(this.pnameCounter);
            if (null == this.findPoint(s)) {
                p.m_name = s;
                pnameCounter++;
                break;
            }
            pnameCounter++;
        }
        p.setColorDefault();
        pointlist.add(p);
        textlist.add(p.getPText());
        if (pointlist.size() == 2)
            optmizePolynomial();
        this.reCalculate();
    }

    /**
     * Generates a point name based on the given count.
     *
     * @param n the count used to generate the point name
     * @return the generated point name
     */
    public String getPointNameByCount(int n) {
        int in = (n) / 26;
        int number = n - in * 26;
        String s = "";
        if (in == 0) {
            char[] c = new char[1];
            c[0] = (char) (number + 'A');
            s = new String(c);
        } else {
            char[] c = new char[2];
            c[0] = (char) (number + 'A');
            c[1] = (char) ('0' + in);
            s = new String(c);
        }
        return s;
    }

    /**
     * Adds an angle to the angle list if it is not already present.
     *
     * @param ag the angle to add
     */
    public void addAngleToList(CAngle ag) {
        if (anglelist.contains(ag)) {
            return;
        }
        anglelist.add(ag);
        textlist.add(ag.getText());
    }

    /**
     * Adds a line to the line list if it is not already present.
     *
     * @param line the line to add
     */
    public void addLineToList(CLine line) {
        if (linelist.contains(line)) {
            return;
        }
        String str = new String("l");
        str += this.plineCounter;
        line.m_name = str;
        this.plineCounter++;
        linelist.add(line);
    }

    /**
     * Adds a polygon to the polygon list if it is not already present.
     *
     * @param p the polygon to add
     */
    public void addPolygonToList(CPolygon p) {
        if (p == null || polygonlist.contains(p))
            return;
        String s = "poly";
        int i = 0;

        while (true) {
            String s1 = s + i;
            boolean fd = false;
            for (int j = 0; j < polygonlist.size(); j++) {
                CPolygon p1 = (CPolygon) polygonlist.get(j);
                if (s1.equalsIgnoreCase(p1.m_name)) {
                    fd = true;
                    break;
                }
            }
            if (!fd) {
                s = s1;
                break;
            }
            i++;
        }

        p.m_name = s;
        this.polygonlist.add(p);
    }

    /**
     * Draws a line between two points and adds it to the line list if it does not already exist.
     *
     * @param p1 the first point
     * @param p2 the second point
     */
    public void drawLineAndAdd(CPoint p1, CPoint p2) {
        if (p1 == null || p2 == null || p1 == p2) return;

        if (fd_line(p1, p2) == null) {
            CLine ln = new CLine(p1, p2);
            this.addLineToList(ln);
        }
    }

    /**
     * Adds a circle to the circle list if it is not already present.
     *
     * @param c the circle to add
     */
    public void addCircleToList(Circle c) {
        if (circlelist.contains(c)) {
            return;
        }
        String str = new String("c");
        str += this.pcircleCounter;
        pcircleCounter++;
        c.m_name = str;
        circlelist.add(c);
    }

    /**
     * Adds an object to the specified list if it is not already present.
     *
     * @param obj  the object to add
     * @param list the list to which the object is added
     * @return true if the object was added, false otherwise
     */
    public boolean addObjectToList(Object obj, Vector list) {
        if (obj == null) {
            return false;
        }

        if (list.contains(obj))
            return false;
        if (list == SelectList) {
            this.addToSelectList(obj);
        } else
            list.add(obj);
        return true;
    }

    /**
     * Adds a constraint to the constraint list if it is not already present.
     *
     * @param cs the constraint to add
     */
    public void addConstraintToList(Constraint cs) {
        if (cs != null && !constraintlist.contains(cs)) {
            constraintlist.add(cs);
        }
    }

    /**
     * Removes a constraint from the constraint list.
     *
     * @param cs the constraint to remove
     */
    public void removeConstraintFromList(Constraint cs) {
        constraintlist.remove(cs);
    }

    /**
     * Clears all constraints from the constraint list.
     */
    public void clearAllConstraint() {
        constraintlist.clear();
    }

    /**
     * Checks if a line exists between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return true if the line exists, false otherwise
     */
    private boolean isLineExists(CPoint p1, CPoint p2) {
        for (int i = 0; i < linelist.size(); i++) {
            CLine ln = (CLine) linelist.get(i);
            if (ln.points.contains(p1) && ln.points.contains(p2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a circle exists that passes through three points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @return true if the circle exists, false otherwise
     */
    private boolean isCircleExists(CPoint p1, CPoint p2, CPoint p3) {
        for (int i = 0; i < circlelist.size(); i++) {
            Circle ln = (Circle) circlelist.get(i);
            if (ln.points.contains(p1) && ln.points.contains(p2) &&
                    ln.points.contains(p3)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Selects and adds objects (points, lines, circles, and texts) that lie within the rectangular region
     * defined by the two diagonal points (x1, y1) and (x2, y2).
     *
     * @param x1 the x-coordinate of the first corner of the rectangle
     * @param y1 the y-coordinate of the first corner of the rectangle
     * @param x2 the x-coordinate of the opposite corner of the rectangle
     * @param y2 the y-coordinate of the opposite corner of the rectangle
     */
    public void SelectByRect(double x1, double y1, double x2, double y2) {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            double x = p.getx();
            double y = p.gety();
            if ((x - x1) * (x - x2) < 0 && (y - y1) * (y - y2) < 0) {
                addToSelectList(p);
            }
        }
        for (int i = 0; i < linelist.size(); i++) {
            CLine ln = (CLine) linelist.get(i);
            if (SelectList.containsAll(ln.points))
                addToSelectList(ln);
        }
        for (int i = 0; i < circlelist.size(); i++) {
            Circle ln = (Circle) circlelist.get(i);
            if (SelectList.contains(ln.o) && SelectList.containsAll(ln.points))
                this.addToSelectList(ln);
        }

        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            if (t.getType() != CText.NAME_TEXT && t.inRect(x1, y1, x2, y2))
                this.addToSelectList(t);
        }

    }

    /**
     * Handles the mouse button release (up) event by applying snap adjustments and executing actions
     * based on the current drawing mode (e.g., line creation, point selection, or transformation).
     *
     * @param x the x-coordinate where the mouse button was released
     * @param y the y-coordinate where the mouse button was released
     */
    public void DWButtonUp(double x, double y) {
        this.IsButtonDown = false;

        if (SNAP && CurrentAction != SELECT) {
            double[] r = getSnap(x, y);
            x = r[0];
            y = r[1];
        }
        CatchPoint.setXY(x, y);

        switch (this.CurrentAction) {
            case SELECT: {
                vx1 = x;
                vy1 = y;
            }
            break;
            case MOVE:
                break;
            case D_POINT:
                clearSelection();
                break;
            case H_LINE: {

                CPoint p = null;
                if (STATUS == 2) {
                    STATUS = 0;
                    break;
                }
                CPoint pt = this.CreateATempPoint(x, y);
                p = this.SmartPoint(pt);
                if (p == FirstPnt) {
                    STATUS = 0;
                    break;
                }
                if (p == null) {
                    p = SmartgetApointFromXY(x, y);
                }
                Constraint cs = new Constraint(Constraint.HORIZONAL, FirstPnt, p);
                this.charsetAndAddPoly(false);
                this.addPointToList(p);
                this.addConstraintToList(cs);
                CLine ln = new CLine(FirstPnt, p);
                this.addLineToList(ln);
                this.UndoAdded(ln.getDescription() + " is a horizonal line");
                FirstPnt = null;
                STATUS = 0;
                break;
            }
            case V_LINE: {
                CPoint p = null;
                if (STATUS == 2) {
                    STATUS = 0;
                    break;
                }
                CPoint pt = this.CreateATempPoint(x, y);
                p = this.SmartPoint(pt);
                if (p == FirstPnt) {
                    STATUS = 0;
                    break;
                }
                if (p == null) {
                    p = SmartgetApointFromXY(x, y);
                }
                Constraint cs = new Constraint(Constraint.VERTICAL, FirstPnt, p);
                this.charsetAndAddPoly(false);
                this.addPointToList(p);
                this.addConstraintToList(cs);
                CLine ln = new CLine(FirstPnt, p);
                this.addLineToList(ln);
                this.UndoAdded(ln.getDescription() + " is a vertical line");
                FirstPnt = null;
                STATUS = 0;
                break;
            }
            case D_LINE:
                break;
            case D_PARELINE:
                break;
            case D_PERPLINE:
                break;
            case PERPWITHFOOT: {

                if (STATUS == 1) {
                    CPoint p1 = FirstPnt;
                    CPoint pt = this.SmartPoint(CatchPoint);
                    if (pt == p1) {
                        break;
                    }

                    CLine line = this.SmartPLine(CatchPoint);
                    if (line == null) {
                        break;
                    }
                    CPoint p = this.CreateANewPoint(0, 0);
                    add_PFOOT(line, p1, p);
                    FirstPnt = null;
                    STATUS = 0;
                }
            }
            break;
            case D_PFOOT:
                break;
            case D_CIRCLE:
                break;
            case D_CIRCLEBYRADIUS:
                break;
            case D_PRATIO: {
            }
            break;
            case D_TRATIO: {

            }
            break;

            case D_MIDPOINT:
                break;
            case D_3PCIRCLE:
                break;
            case TRANSLATE:
                break;
            case D_SQUARE: {
                if (STATUS == 1) {
                    CLine line = (CLine) SelectList.get(0);
                    CPoint[] pl = line.getTowSideOfLine();
                    if (pl == null) {
                        break;
                    }
                    addsquare(pl[0], pl[1], CatchPoint);
                    clearSelection();
                    STATUS = 0;
                }
            }
            break;
            case D_IOSTRI:
                break;
        }
        IsButtonDown = false;
    }

    /**
     * Adjusts the position of a free-moving point along its connecting line segments to ensure proper alignment.
     * The method checks nearby points on the connected line and snaps the point to the corresponding x or y value
     * if within a defined pixel threshold.
     */
    public void smartPVDragLine() {
        if (SelectList.size() != 1)
            return;
        CClass c = (CClass) SelectList.get(0);
        if (c.get_type() != CClass.POINT)
            return;
        CPoint pt = (CPoint) c;
        if (pt.isAFixedPoint())
            return;

        for (int i = 0; i < linelist.size(); i++) {
            CLine ln = (CLine) linelist.get(i);
            if (!ln.containPT(pt))
                continue;
            CPoint pt2 = ln.getSecondPoint(pt);
            if (pt2 != null && pt2.isAFreePoint()) {
                double r1 = Math.abs(pt.getx() - pt2.getx());
                double r2 = Math.abs(pt.gety() - pt2.gety());

                if (pt.isAFreePoint()) {
                    if (r1 < CMisc.PIXEPS && r2 < CMisc.PIXEPS) {
                        break;
                    } else if (r1 < CMisc.PIXEPS) {
                        pt.setXY(pt2.getx(), pt.gety());
                        break;
                    } else if (r2 < CMisc.PIXEPS) {
                        pt.setXY(pt.getx(), pt2.gety());
                        break;
                    }
                } else {
//                    if (r1 < CMisc.PIXEPS && r2 < CMisc.PIXEPS) {
//                        break;
//                    } else if (r1 < CMisc.PIXEPS) {
//                        pt.setXY(pt2.getx(), pt.gety());
//                        break;
//                    }
                }
            }

        }

    }

    /**
     * Processes mouse drag events and updates the position of objects accordingly based on the current drawing action.
     * Applies snapping if enabled, and updates positions of points and transformations for the active drawing mode.
     *
     * @param x the current x-coordinate during the drag
     * @param y the current y-coordinate during the drag
     */
    public void DWMouseDrag(double x, double y) {

        if (SNAP && CurrentAction != SELECT) {
            double[] r = getSnap(x, y);
            x = r[0];
            y = r[1];
        }
        this.isPointOnObject = false;
        CatchPoint.setXY(x, y);

        switch (this.CurrentAction) {
            case SELECT: {
                if (Math.abs(vx1 - x) > 15 || Math.abs(vy1 - y) > 15) {
                    this.clearSelection();
                    SelectByRect(vx1, vy1, x, y);
                }
            }
            break;
            case MOVE: {
                if (FirstPnt == null) {
                    break;
                }

                ObjectLocationChanged(SelectList, FirstPnt, x, y);
                FirstPnt.setXY(x, y);
                smartPVDragLine();
                if (this.isRecal) {
                    this.reCalculate();
                }
            }
            break;

            case D_POINT: {
                if (this.IsButtonDown && SelectList.size() != 0) {
                    CPoint p = (CPoint) SelectList.get(0);
                    p.setXY(x, y);
                    this.reCalculate();
                }
            }
            break;
            case H_LINE:
                SecondPnt.setXY(x, FirstPnt.gety());
                break;
            case V_LINE:
                SecondPnt.setXY(FirstPnt.getx(), y);
                break;
            case D_LINE:
                if (FirstPnt != null) {
                    isPointOnObject = Smart(CatchPoint, x, y);
                    if (!isPointOnObject) {
//                        isSmartPoint = SmartLineType(FirstPnt.getx(), FirstPnt.gety(), CatchPoint);
                    } else {
//                        isSmartPoint = 0;
                    }
                }

                break;
            case D_PARELINE:
            case D_PERPLINE:
            case D_ALINE:
            case D_CIRCLE:
            case PERPWITHFOOT:
                this.isPointOnObject = Smart(CatchPoint, x, y);
                break;
            case TRANSLATE: {
                double dx = x - FirstPnt.getx();
                double dy = y - FirstPnt.gety();
                FirstPnt.setXY(x, y);
                translate(dx, dy);
            }
            break;
            case D_CIRCLEBYRADIUS: {
                if (STATUS == 1) {
                    SecondPnt.setXY(x, y);
                }
            }
            break;
            case D_PRATIO:
            case D_TRATIO:
            case D_3PCIRCLE:
            case D_SQUARE:
            case D_IOSTRI:
                break;

        }
    }

    /**
     * Adds a perpendicular foot from a point to a line.
     *
     * @param line the line to which the perpendicular foot is added
     * @param p1   the point from which the perpendicular foot is drawn
     * @param p    the perpendicular foot point
     */
    public void add_PFOOT(CLine line, CPoint p1, CPoint p) {
        CLine line1 = new CLine(p1, p);
        CPoint[] pl = line.getTowSideOfLine();
        Constraint cs = null;
        if (pl != null) {
            cs = new Constraint(Constraint.PFOOT, p, p1, pl[0], pl[1]);
            CPoint pu = this.addADecidedPointWithUnite(p);
            if (pu == null) {
                this.addPointToList(p);
                addLineToList(line1);
                this.addConstraintToList(cs);
                this.AddPointToLineX(p, line);
                addCTMark(line, line1);
                line1.addconstraint(cs);
                this.UndoAdded(line1.getSimpleName() + " perp " +
                        line.getSimpleName() +
                        " " + GExpert.getTranslationViaGettext("with foot {0}", p.m_name));

            } else {
                p = pu;
            }
        } else {
            AddPointToLine(p, line, false);
            cs = new Constraint(Constraint.PERPENDICULAR, line, line1);
            CPoint pu = this.addADecidedPointWithUnite(p);
            if (pu == null) {
                this.addPointToList(p);
                addLineToList(line1);
                addCTMark(line, line1);
                this.AddPointToLineX(p, line);
                this.addConstraintToList(cs);
                line1.addconstraint(cs);
                this.UndoAdded(line1.getSimpleName() + " perp " +
                        line.getSimpleName() + " with footy " + p.m_name);
            }
        }
    }

    /**
     * Translates all points and text objects by the given delta values.
     *
     * @param dx the delta x value
     * @param dy the delta y value
     */
    private void translate(double dx, double dy) {
        if (isFrozen())
            return;

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.setXY(p.getx() + dx, p.gety() + dy);
        }
        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            t.move(dx, dy);
        }
        this.reCalculate();
    }

    /**
     * Updates the location of objects in the list based on the new coordinates.
     *
     * @param list the list of objects to update
     * @param old  the old point
     * @param x    the new x-coordinate
     * @param y    the new y-coordinate
     */
    private void ObjectLocationChanged(Vector list, CPoint old, double x, double y) {
        double x0 = FirstPnt.getx();
        double y0 = FirstPnt.gety();
        double dx = x - x0;
        double dy = y - y0;
        int n = list.size();
        if (n == 0)
            return;

        if (cpfield != null && list.size() == 0) {
            cpfield.drag(dx, dy);
        }

        if (n == 1) {
            CClass c = (CClass) list.get(0);
            int t = c.get_type();
            switch (t) {
                case CClass.POINT:
                    CPoint p = (CPoint) c;
                    if (!p.isFreezed())
                        p.setXY(x, y);
                    return;
                case CClass.LINE:
                    CLine ln = (CLine) c;
                    if (ln.isTwoEndFreePoints()) {
                        objectsListMoved(ln.points, dx, dy);
                    }
                    return;
                case CClass.CIRCLE:
                    circleLocationChanged((Circle) c, dx, dy);
                    return;
                case CClass.ANGLE:
                    CAngle ag = (CAngle) c;
                    ag.move(old.getx(), old.gety());
                    return;
                case CClass.TEXT:
                    CText ct = (CText) c;
                    ct.drag(x0, y0, dx, dy);
                    return;
                case CClass.TMARK:
                    CTMark m = (CTMark) c;
                    m.move(x0, y0);
                    return;
                case CClass.DISTANCE:
                    CDistance dis = (CDistance) c;
                    dis.drag(x, y);
                    return;
                case CClass.POLYGON: {
                    CPolygon cp = (CPolygon) c;
                    if (cp.ftype == 1) {
                        Circle cx = fd_circleOR((CPoint) cp.getElement(0), (CPoint) cp.getElement(1), (CPoint) cp.getElement(2));
                        circleLocationChanged(cx, dx, dy);
                    } else {
                        CPoint p1 = (CPoint) cp.getElement(0);
                        double xx = p1.getx();
                        double yy = p1.gety();
                        if (cp.isAllPointsFree()) {
                            objectsListMoved(cp.pointlist, dx, dy);
                            p1.setXY(xx + dx, yy + dy);
                        }
                    }
                }
            }
            return;
        }

        if (isFrozen())
            return;

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.setXY(p.getx() + dx, p.gety() + dy);
        }
    }

    /**
     * Updates the location of a circle and its points based on the given delta values.
     *
     * @param c  the circle to update
     * @param dx the delta x value
     * @param dy the delta y value
     */
    private void circleLocationChanged(Circle c, double dx, double dy) {
        Circle c1 = (Circle) c;
        CPoint p1 = c1.o;
        if (!p1.isFreezed())
            p1.setXY(p1.getx() + dx, p1.gety() + dy);
        objectsListMoved(c1.points, dx, dy);
        return;
    }

    /**
     * Moves all objects in the list by the given delta values.
     *
     * @param list the list of objects to move
     * @param dx   the delta x value
     * @param dy   the delta y value
     */
    public void objectsListMoved(Vector list, double dx, double dy) {
        for (int i = 0; i < list.size(); i++) {
            CClass c = (CClass) list.get(i);
            int t = c.get_type();
            switch (t) {
                case CClass.POINT:
                    CPoint p = (CPoint) c;
                    if (!p.isFreezed())
                        p.setXY(p.getx() + dx, p.gety() + dy);
                    break;
            }
        }
    }

    /**
     * This method is used to move the catch point to the nearest point on the
     * object.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void DWMouseMove(double x, double y) {
        if (SNAP && CurrentAction != SELECT) {
            double[] r = getSnap(x, y);
            x = r[0];
            y = r[1];
        }

        MouseX = (int) x;
        MouseY = (int) y;

        CatchPoint.setXY(x, y);
        isPointOnObject = false;
        isPointOnIntersection = false;

        switch (this.CurrentAction) {
            case SELECT:

            case MOVE:
                if (cpfield != null) {
                    cpfield.mouseMove(x, y);
                }
                break;
            case HIDEOBJECT: {
                int n1 = CatchList.size();
                OnCatch(x, y);
                if (CatchList.size() != 0 || n1 != 0) {
                    if (panel != null)
                        panel.repaint();
                }
            }
            break;

            case D_LINE:
                if (STATUS == 1) {
                    SmartmoveCatch(x, y);
                    if (!isPointOnObject) {
                    } else {
                    }
                } else if (STATUS == 0)
                    SmartmoveCatch(x, y);
                break;

            case D_POLYGON:
                if (SecondPnt != null && !Smart(SecondPnt, x, y)) {
                    SmartmoveCatch(x, y);
                } else
                    SmartmoveCatch(x, y);
                if (SecondPnt != null)
                    SecondPnt.setXY(x, y);
                break;
            case D_POINT:
            case TRIANGLE:
            case D_PARELINE:
            case D_PERPLINE:
            case D_CIRCLE:

            case D_ALINE:
            case D_MIDPOINT:
            case D_3PCIRCLE:
            case D_PRATIO:
            case D_TRATIO:
            case D_SQUARE:
            case RECTANGLE:
            case DRAWTRIALL:
            case TRAPEZOID:
            case RA_TRAPEZOID:
            case PARALLELOGRAM:
            case DRAWTRISQISO:
            case SANGLE:
                SmartmoveCatch(x, y);
                break;
            case PERPWITHFOOT:
                if (STATUS == 0)
                    SmartmoveCatch(x, y);
                else
                    this.SmartmoveCatchLine(x, y);
                break;
            case D_IOSTRI:
                if (SelectList.size() == 2)
                    moveCatch(catchX, catchY);
                else
                    SmartmoveCatch(x, y);
                break;
            case D_PFOOT: {
                if (SelectList.size() == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);

                    double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(), p2.gety(), x, y);
                    x = r[0];
                    y = r[1];
                }
                SmartmoveCatch(x, y);
            }
            break;
            case D_PTDISTANCE:
                if (SelectList.size() < 3)
                    this.SmartmoveCatchPt(x, y);
                else
                    this.SmartmoveCatch(x, y, 6);
                break;

            case D_ANGLE:
                if (STATUS == 1) {
                    CAngle ag = (CAngle) SelectList.get(0);
                    ag.move(x, y);
                }
                break;
            case DEFINEPOLY: {
                if (STATUS == 1)
                    FirstPnt.setXY(x, y);
                SmartmoveCatchPt(x, y);
            }
            case INCENTER:
            case BARYCENTER:
            case ORTHOCENTER:
            case CIRCUMCENTER:
                SmartmoveCatchPt(x, y);
                break;
            case D_TEXT: {
                CatchPoint.setXY(x, y);
                CClass cc = this.SelectFromAList(textlist, x, y);
                CatchList.clear();
                if (cc != null) {
                    CatchList.add(cc);
                }
            }
            break;
            case D_CCLINE: {
                CatchPoint.setXY(x, y);
                CatchList.clear();
                this.SelectFromAList(CatchList, circlelist, x, y);
            }
            break;
            case EQUIVALENCE: {
                if (STATUS == 2) {
                    CPolygon p = (CPolygon) SelectList.get(0);
                    CPoint p1 = (CPoint) SelectList.get(1);
                    CPoint t1 = p.getPreviousePoint(p1);
                    CPoint t2 = p.getNextPoint(p1);
                    double[] r = getPTInterSection(x, y, p1.getx(), p1.gety()
                            , t1.getx(), t1.gety(), t2.getx(), t2.gety());
                    p.setDraggedPoints(p1, null, r[0] - p1.getx(), r[1] - p1.gety());

                } else if (STATUS == 3) {
                    CPolygon p = (CPolygon) SelectList.get(0);
                    CPoint p1 = (CPoint) SelectList.get(1);
                    CPoint p2 = (CPoint) SelectList.get(2);
                    p.setDraggedPoints(p1, p2, x - vx1, y - vy1);
                }
            }
            break;
            case FREE_TRANSFORM: {
                if (STATUS == 1) {
                    if (SelectList.size() == 2) {
                        CPolygon poly = (CPolygon) SelectList.get(0);
                        CPoint pt = (CPoint) SelectList.get(1);
                        poly.setDraggedPoints(pt, null, CatchPoint.getx() - pt.getx(), CatchPoint.gety() - pt.gety());
                    }
                }
            }
            break;
            case TRANSFORM: {
                if (STATUS == 1) {
                    x = x - vtrx;
                    y = y - vtry;
                    vx1 = x - catchX;
                    vy1 = y - catchY;
                } else if (STATUS == 2) {
                    if (FirstPnt != null && ThirdPnt != null) {
                        vangle = Math.PI + CAngle.get3pAngle(ThirdPnt.getx(), ThirdPnt.gety(),
                                FirstPnt.getx() - vx1, FirstPnt.gety() - vy1, x - vx1, y - vy1);

                    }
                }

            }
            break;
        }

        if (this.CurrentAction != MOVE && this.CurrentAction != SELECT)
            panel.repaint();
    }


    /**
     * Moves the catch point to the specified coordinates if a point is found at those coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void SmartmoveCatchPt(double x, double y) {
        CPoint pt = this.SelectAPoint(x, y);
        CatchList.clear();
        if (pt != null) {
            isPointOnObject = true;
            CatchList.add(pt);
            CatchPoint.setXY(pt.getx(), pt.gety());
        }
    }

    /**
     * Moves the catch point to the specified coordinates if a line is found at those coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void SmartmoveCatchLine(double x, double y) {
        CLine pt = this.SelectALine(x, y);
        CatchList.clear();
        if (pt != null) {
            isPointOnObject = true;
            CatchList.add(pt);
            pt.pointonline(CatchPoint);
        }
    }

    /**
     * Moves the catch point to the specified coordinates, considering all types of objects.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void SmartmoveCatch(double x, double y) {
        SmartmoveCatch(x, y, 0);
    }

    /**
     * Moves the catch point to the specified coordinates, considering specific types of objects.
     *
     * @param x    the x-coordinate
     * @param y    the y-coordinate
     * @param type the type of objects to consider (0: All, 1: Point Only, 2: Line Only, 3: Circle Only, 4: Point and Line, 5: Point and Circle, 6: Line and Circle)
     */
    public void SmartmoveCatch(double x, double y, int type) {
        CatchList.clear();
        CatchType = 0;
        SelectAllFromXY(CatchList, x, y, 1);
        int n = CatchList.size();
        if (n > 0) {
            isPointOnObject = true;
            if (n == 1) {
                CClass c = (CClass) CatchList.get(0);
                if (c instanceof CLine) {
                    CLine ln = (CLine) c;
                    if (type == 0 || type == 2 || type == 4 || type == 6) {
                        ln.pointonline(CatchPoint);
                        if (ln.pointonMiddle(CatchPoint)) {
                            CatchType = 1;
                        }
                    }
                } else if (c instanceof Circle) {
                    Circle cr = (Circle) c;
                    if (type == 0 || type == 3 || type == 5 || type == 6)
                        cr.pointStickToCircle(CatchPoint);
                } else if (c instanceof CPoint) {
                    CPoint p = (CPoint) c;
                    if (type == 0 || type == 1 || type == 4 || type == 5)
                        CatchPoint.setXY(p.getx(), p.gety());
                }
            } else {
                if (type == 0 || type == 1 || type == 4 || type == 5)
                    get_Catch_Intersection(x, y);
            }
        } else {
            if (type == 0 || type == 4 || type == 5) {
                hvCatchPoint();
            }
        }
        mouseCatchX = (int) CatchPoint.getx();
        mouseCatchY = (int) CatchPoint.gety();
    }

    /**
     * Moves the catch point to the specified coordinates, considering points, lines, and circles.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void moveCatch(double x, double y) {
        int n = CatchList.size();
        CatchList.clear();
        Object obj = null;
        obj = SelectAPoint(x, y);
        if (obj == null)
            obj = this.SelectALine(x, y);
        if (obj == null)
            obj = this.SelectACircle(x, y);
        if (obj != null) {
            CatchList.add(obj);
            isPointOnObject = true;
        }
        if (n != 0)
            panel.repaint();
    }

    /**
     * Finds the intersection point of two objects (lines or circles) in the catch list.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void get_Catch_Intersection(double x, double y) {
        int k = 0;
        CLine ln = null;
        Circle c = null;
        Object o1, o2;
        o1 = o2 = null;
        for (int i = 0; i < CatchList.size(); i++) {
            Object o = CatchList.get(i);
            if (!(o instanceof CPoint)) {
                if (o1 == null)
                    o1 = o;
                else if (o2 == null)
                    o2 = o;
                k++;
            }
        }
        if (k >= 2) {
            double[] r = null;
            if (o1 instanceof CLine && o2 instanceof CLine) {
                r = intersect_ll((CLine) o1, (CLine) o2);
            } else if (o1 instanceof Circle && o2 instanceof Circle) {
                r = intersect_cc((Circle) o1, (Circle) o2);
            } else {
                if (o1 instanceof CLine && o2 instanceof Circle) {
                    r = intersect_lc((CLine) o1, (Circle) o2);
                } else if (o1 instanceof Circle && o2 instanceof CLine) {
                    r = intersect_lc((CLine) o2, (Circle) o1);
                }
            }
            if (r != null && r.length > 0) {
                int d = -1;
                double len = Double.MAX_VALUE;
                int l = r.length;
                int j = 0;
                for (j = 0; j < l / 2; j++) {
                    double s = Math.pow(r[j * 2] - x, 2) + Math.pow(r[j * 2 + 1] - y, 2);
                    if (s < len) {
                        d = j;
                        len = s;
                    }
                }
                if (d >= 0) {
                    x = r[d * 2];
                    y = r[d * 2 + 1];
                    if (SelectAPoint(x, y) == null) {
                        CatchPoint.setXY(x, y);
                        isPointOnIntersection = true;
                    }
                }
            } else
                isPointOnIntersection = true;
        }
    }

    /**
     * Sets the given point to the specified coordinates and checks if it is on any geometric object.
     *
     * @param p the point to set
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the point is on an object, false otherwise
     */
    public boolean Smart(CPoint p, double x, double y) {
        p.setXY(x, y);
        CPoint pt = SmartPoint(p);
        if (pt != null) {
            return true;
        }
        CLine line = SmartPLine(p);
        if (line != null) {
            return true;
        }
        Circle c = SmartPCircle(p);
        if (c != null) {
            return true;
        }
        return false;
    }

    /**
     * Adds a new point to the drawing at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the created point, or an existing point if one is found at the coordinates
     */
    public CPoint SmartAddPoint(double x, double y) {
        CPoint pt = SelectAPoint(x, y);
        if (pt != null)
            return pt;

        Vector v = new Vector();
        SelectFromAList(v, linelist, x, y);
        SelectFromAList(v, circlelist, x, y);
        if (v.size() >= 2) {
            return meetTwoObject(v.get(0), v.get(1), true, x, y);
        }

        CPoint p = this.CreateANewPoint(x, y);
        this.addPointToList(p);
        p.hasSetColor = false;
        int n = v.size();
        if (n == 0) {
            setCatchHVPoint(p);
        } else if (n == 1) {
            Object obj = v.get(0);
            if (obj instanceof CLine) {
                CLine ln = (CLine) obj;
                ln.pointOnLine(p);
                ln.pointonMiddle(p);
                AddPointToLine(p, (CLine) obj, false);
            } else if (obj instanceof Circle)
                AddPointToCircle(p, (Circle) obj, false);
            charsetAndAddPoly(false);
        }
        return p;
    }

    /**
     * Adds a new point to the drawing at the specified coordinates with a given name.
     *
     * @param x    the x-coordinate
     * @param y    the y-coordinate
     * @param name the name of the new point
     * @return the created point, or an existing point if one is found at the coordinates
     */
    public CPoint SmartAddPoint(double x, double y, String name) {
        CPoint pt = SelectAPoint(x, y);
        if (pt != null)
            return pt;

        Vector v = new Vector();
        SelectFromAList(v, linelist, x, y);
        SelectFromAList(v, circlelist, x, y);
        if (v.size() >= 2) {
            return meetTwoObject(v.get(0), v.get(1), true, x, y);
        }

        CPoint p = this.CreateANewPoint(x, y, name);
        this.addPointToList(p);
        p.hasSetColor = false;
        int n = v.size();
        if (n == 0) {
            setCatchHVPoint(p);
        } else if (n == 1) {
            Object obj = v.get(0);
            if (obj instanceof CLine) {
                CLine ln = (CLine) obj;
                ln.pointOnLine(p);
                ln.pointonMiddle(p);
                AddPointToLine(p, (CLine) obj, false);
            } else if (obj instanceof Circle)
                AddPointToCircle(p, (Circle) obj, false);
            charsetAndAddPoly(false);
        }
        return p;
    }

    /**
     * Selects a line from the drawing that is near the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected line, or null if no line is found
     */
    public CLine SelectALine(double x, double y) {
        return SmartPointOnLine(x, y);
    }

    /**
     * Selects a circle from the drawing that is near the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected circle, or null if no circle is found
     */
    public Circle SelectACircle(double x, double y) {
        for (int i = 0; i < circlelist.size(); i++) {
            Circle c = (Circle) circlelist.get(i);
            if (c.nearcircle(x, y, CMisc.PIXEPS)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Selects a point from the drawing that is near the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the selected point, or null if no point is found
     */
    public CPoint SelectAPoint(double x, double y) {
        CPoint pt = null;
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            if (p.select(x, y)) {
                pt = p;
                break;
            }
        }
        return pt;
    }

    /**
     * Starts and stops the flashing process for flash items in the flash list.
     */
    public void doFlash() {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash ff = (JFlash) flashlist.get(i);
            if (!ff.isfinished()) {
                ff.start();
                ff.stop();
            }
            if (ff.getvisibleType()) {
                flashlist.remove(ff);
                i--;
            }
        }
    }

    /**
     * Clears all flash items from the flash list.
     */
    public void clearFlash() {
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash ff = (JFlash) flashlist.get(i);
            ff.stop();
        }
        flashlist.clear();
    }

    /**
     * Adds a flash item to the flash list and starts it.
     *
     * @param f the flash item to add
     */
    public void addFlash(JFlash f) {
        if (f == null)
            return;

        clearFlash();
        flashlist.add(f);
        f.start();
    }

    /**
     * Finds the common point between two lines or a line and a point.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return the common point if found, otherwise null
     */
    public CPoint getCommonPoint(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        CLine ln1 = this.fd_line(p1, p2);
        CLine ln2 = this.fd_line(p3, p4);
        if (ln1 == null) {
            ln1 = ln2;
            ln2 = null;
            CPoint p = p1;
            p1 = p3;
            p3 = p;
            p = p2;
            p2 = p4;
            p4 = p;
        }

        if (ln1 != null) {
            Vector v = ln1.points;
            if (ln2 == null) {
                if (v.contains(p3)) {
                    return p3;
                }
                if (v.contains(p4)) {
                    return p4;
                }
            } else {
                Vector v2 = ln2.points;
                for (int i = 0; i < v2.size(); i++) {
                    if (v.contains(v2.get(i))) {
                        return (CPoint) v2.get(i);
                    }
                }
            }
        } else {
            if (p1 == p3 || p1 == p4) {
                return p1;
            }
            if (p2 == p3 || p2 == p4) {
                return p2;
            }
        }
        return null;
    }

    /**
     * Adds two JCgFlash items and a JFlash item to the flash list.
     *
     * @param f1 the first JCgFlash item
     * @param f2 the second JCgFlash item
     * @param f  the JFlash item
     */
    public void addCgFlash(JCgFlash f1, JCgFlash f2, JFlash f) {
        int size = flashlist.size();
        int n = 0;
        for (int j = 0; j < size; j++) {
            JFlash fx = (JFlash) flashlist.get(j);
            if (fx instanceof JCgFlash)
                n++;
        }

        int i = 1;
        for (; true; i++) {
            int j = 0;
            for (j = 0; j < size; j++) {
                JFlash fx = (JFlash) flashlist.get(j);
                if (fx instanceof JCgFlash) {
                    JCgFlash fx1 = (JCgFlash) fx;
                    if (i == fx1.getDNum()) {
                        break;
                    }
                }
            }
            if (j == size)
                break;
        }

        if (n == 0) {
            f1.setDNum(2);
            f2.setDNum(2);
        } else {
            f1.setDNum(i);
            f2.setDNum(i);
        }
        addFlashx(f1);
        addFlashx(f);
        addFlashx(f2);
    }

    /**
     * Starts the flashing process for flash items in the flash list.
     */
    public void startFlash() {
        for (int j = 0; j < flashlist.size(); j++) {
            JFlash fx = (JFlash) flashlist.get(j);
            if (fx.isrRunning())
                return;
            if (!fx.isfinished() && !fx.isrRunning()) {
                fx.start();
                return;
            }
        }
    }

    /**
     * Adds the specified flash object before any existing JRedoStepFlash in the flash list.
     *
     * @param f the flash object to add
     */
    public void addFlash2(JFlash f) {
        for (int i = 0; i < flashlist.size(); i++) {
            if (flashlist.get(i) instanceof JRedoStepFlash) {
                flashlist.add(i, f);
                return;
            }
        }
        addFlash1(f);
    }

    /**
     * Adds the specified flash object and starts it immediately if it is the only flash item.
     *
     * @param f the flash object to add
     */
    public void addFlash1(JFlash f) {

        addFlashx(f);
        if (flashlist.size() == 1)
            f.start();
    }

    /**
     * Adds the specified flash object to the flash list if not already present.
     * For JAngleFlash instances, adjusts the flash radius based on the number of similar flash objects.
     *
     * @param f the flash object to add
     */
    public void addFlashx(JFlash f) {
        if (f == null)
            return;

        if (f instanceof JAngleFlash) {
            JAngleFlash tf = (JAngleFlash) f;
            int num = 0;

            CPoint pt = getCommonPoint(tf.p1, tf.p2, tf.p3, tf.p4);
            if (pt != null) {
                for (int i = 0; i < flashlist.size(); i++) {
                    Object obj = flashlist.get(i);
                    if (obj instanceof JAngleFlash) {
                        JAngleFlash ff = (JAngleFlash) obj;
                        if (pt == getCommonPoint(ff.p1, ff.p2, ff.p3, ff.p4)) {
                            num++;
                        }
                    }
                }
            }
            int d = 0;
            d = num * 10;
            tf.setRadius(tf.getRadius() + d);
        }
        if (!flashlist.contains(f))
            flashlist.add(f);
    }

    /**
     * Checks whether the flash process is finished.
     *
     * @return {@code true} if there are no flash items or the sole flash item has finished; {@code false} otherwise
     */
    public boolean isFlashFinished() {
        int n = flashlist.size();
        if (n == 0) return true;
        if (n > 1) return false;
        JFlash f = (JFlash) flashlist.get(0);
        return (f.isfinished());

    }

    /**
     * Draws all flash items to the provided Graphics2D object, starts flash processes if necessary,
     * and triggers a proof run when all flashes are finished.
     *
     * @param g2 the graphics context used for drawing
     */
    public void drawFlash(Graphics2D g2) {
        if (flashlist.size() == 0)
            return;

        boolean r = false;
        for (int i = 0; i < flashlist.size(); i++) {
            JFlash f = (JFlash) flashlist.get(i);
            if (f == null) {
                flashlist.remove(f);
            }
            if (r == false && !f.isrRunning() && !f.isfinished()) {
                f.start();
                f.draw(g2);
                r = true;
            } else if (f.isrRunning()) {
                f.draw(g2);
                r = true;
            }
            if (f.isfinished()) {
                if (f.getvisibleType()) {
                    flashlist.remove(i);
                    i--;
                } else
                    f.draw(g2);

            }
        }
        if (all_flash_finished()) {
            this.run_to_prove(null, null);// (UndoStruct) U_Obj);
        }
    }

    /**
     * Paints the current drawing scene including grid, undo objects, various shape lists, flashes,
     * points, texts, and catch objects. Also draws additional components such as the track point.
     *
     * @param g the graphics context used for painting
     */
    public void paintPoint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        drawGrid(g2);
        this.setAntiAlias(g2);
        if (undo != null) {
            undo.draw(g2);
        }

        drawList(polygonlist, g2);
        drawSelect(SelectList, g2);
        drawList(anglelist, g2);
        drawPerpFoot(g2, null, 0);
        drawList(tracelist, g2);
        drawList(distancelist, g2);
//        drawList(anglelist, g2);

        drawList(circlelist, g2);
        drawList(linelist, g2);
        drawList(otherlist, g2);

        drawFlash(g2);


        drawList(pointlist, g2);
        drawList(textlist, g2);

        drawCurrentAct(g2);
        drawCatch(g2);

        if (cpfield != null) {
            cpfield.draw(g2);
        }
        drawTrackpt(g2);
    }

    /**
     * Draws the tracking point on the given Graphics2D context and adds it to the corresponding trace list.
     *
     * @param g2 the graphics context used for drawing
     */
    public void drawTrackpt(Graphics2D g2) {
        if (CTrackPt == null)
            return;
        CTrackPt.draw_ct(g2);

        for (int i = 0; i < tracelist.size(); i++) {
            CTrace tr = (CTrace) tracelist.get(i);
            if (tr.isTracePt(CTrackPt)) {
                tr.addTracePoint((int) CTrackPt.getx(), (int) CTrackPt.gety());
                return;
            }
        }
    }

    /**
     * Returns the trace object that contains the current tracking point.
     *
     * @param pt the point used to identify the corresponding trace
     * @return the matching CTrace if found; otherwise, {@code null}
     */
    public CTrace getTraceByPt(CPoint pt) {
        for (int i = 0; i < tracelist.size(); i++) {
            CTrace tr = (CTrace) tracelist.get(i);
            if (tr.isTracePt(CTrackPt))
                return tr;
        }
        return null;
    }

    /**
     * Adjusts the position of point p2 relative to point p1 to enforce a smart horizontal or vertical alignment.
     *
     * @param p1 the reference point
     * @param p2 the point to be adjusted
     */
    public void setSmartPVLine(CPoint p1, CPoint p2) {
        if (p1 == null || p2 == null) {
            return;
        }

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();

        if (Math.abs(x2 - x1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            p2.setXY(x1, y2);
        } else if (Math.abs(y2 - y1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            p2.setXY(x2, y1);
        }

    }

    /**
     * Draws a smart PV (parallel or vertical/horizontal) line between p1 and p2.
     * If the line is nearly horizontal or vertical and sufficiently long, the line is extended accordingly.
     *
     * @param p1 the starting point of the line
     * @param p2 the target point for alignment and extension
     * @param g2 the graphics context used for drawing
     */
    public void drawSmartPVLine(CPoint p1, CPoint p2, Graphics2D g2) {
        int x, y;
        x = y = 0;
        if (p1 == null || p2 == null) {
            return;
        }

        g2.setColor(Color.red);

        int x1 = (int) p1.getx();
        int y1 = (int) p1.gety();
        int x2 = (int) p2.getx();
        int y2 = (int) p2.gety();

        if (Math.abs(x2 - x1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            x = x1;
            if (y2 > y1) {
                y = (int) this.Height;
            } else {
                y = 0;
            }
            p2.setXY(x1, y2);
        } else if (Math.abs(y2 - y1) < CMisc.PIXEPS &&
                Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) >
                        4 * CMisc.PIXEPS * CMisc.PIXEPS) {
            y = y1;
            if (x2 > x1) {
                x = (int) this.Width;
            } else {
                x = 0;
            }
            p2.setXY(x2, y1);
        } else {
            g2.drawLine(x1, y1, x2, y2);
            return;
        }

        float dash[] = {2.0f};
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
        g2.drawLine((int) p1.getx(), (int) p1.gety(), x, y);
    }

    /**
     * Draws the current action environment including auxiliary lines, selection outlines,
     * and other drawing components based on the current action state.
     *
     * @param g2 the graphics context used for drawing
     */
    public void drawCurrentAct(Graphics2D g2) {

//        if (trackPoint != null) {
//        }

        if (SHOWOBJECT == this.CurrentAction) {
            for (int i = 0; i < constraintlist.size(); i++) {
                Constraint cs = (Constraint) constraintlist.get(i);
                if (cs.GetConstraintType() != Constraint.INVISIBLE) {
                    continue;
                }
                CClass c1 = (CClass) cs.getelement(0);
                if (c1.visible == false) {
                    c1.setVisible(true);
                    c1.draw(g2, true);
                    c1.draw(g2);
                    c1.setVisible(false);
                }
            }
        }
        setCurrentDrawEnvironment(g2);

        switch (this.CurrentAction) {
            case SELECT: {
                if (this.IsButtonDown) {
                    g2.setColor(Color.black);
                    g2.setStroke(CMisc.DashedStroke);
                    this.drawRect((int) vx1, (int) vy1, (int) CatchPoint.getx(), (int) CatchPoint.gety(), g2);
                }
            }
            break;
            case D_POINT: {
                drawCatchRect(g2);
                if (IsButtonDown)
                    for (int i = 0; i < SelectList.size(); i++) {
                        CPoint p = (CPoint) SelectList.get(i);
                        drawPointNameLocation(p, g2);
                    }
            }
            break;
            case H_LINE:
            case V_LINE:
                if (STATUS == 1) {
                    SecondPnt.draw(g2);
                    this.drawSmartPVLine(FirstPnt, SecondPnt, g2);
                } else
                    drawCatchRect(g2);
                break;
            case D_LINE: {
                this.drawSmartPVLine(FirstPnt, CatchPoint, g2);
                drawCatchRect(g2);
            }

            case D_TEXT: {
                if (gxInstance != null) {
//                      if(gxInstance.)
                }
            }
            break;
            case D_PARELINE:
                if (SelectList.size() == 0) {
                    drawCatchRect(g2);
                    break;
                } else {
                    CLine line = (CLine) SelectList.get(0);
                    CLine.drawPParaLine(line, CatchPoint, g2);
                    drawPointOrCross(g2);
                }
                break;
            case D_PERPLINE:
                if (SelectList.size() == 0) {
                    drawCatchRect(g2);
                    break;
                } else {
                    CLine line = (CLine) SelectList.get(0);
                    CLine.drawTPerpLine(line, CatchPoint, g2);
                    drawPointOrCross(g2);
                }
                break;
            case D_ALINE: {
                int n = SelectList.size();
                if (n == 3) {
                    this.drawPointOrCross(g2);
                }
                if (!IsButtonDown) {
                    n = SelectList.size();
                    if (n == 3) {
                        CLine ln1 = (CLine) SelectList.get(0);
                        CLine ln2 = (CLine) SelectList.get(1);
                        CLine ln3 = (CLine) SelectList.get(2);
                        double k = CLine.getALineK(ln1, ln2, ln3);
                        this.drawAuxLine((int) CatchPoint.getx(), (int) CatchPoint.gety(), k, g2);
                        this.drawPointOrCross(g2);
                    }
                }
            }
            break;
            case PERPWITHFOOT: {
                if (STATUS == 1) {
                    if (FirstPnt == null)
                        break;
                    g2.drawLine((int) FirstPnt.getx(), (int) FirstPnt.gety(), (int) CatchPoint.getx(), (int) CatchPoint.gety());
                    drawPointOrCross(g2);
                    if (CatchList.size() > 0) {
                        CLine ln = (CLine) CatchList.get(0);
                        double k0 = ln.getK();
                        CPoint pt = ln.getfirstPoint();
                        if (ln != null) {
                            double x, y;
                            double x0 = pt.getx();
                            double y0 = pt.gety();
                            double x1 = FirstPnt.getx();
                            double y1 = FirstPnt.gety();

                            if (Math.abs(k0) > CMisc.MAX_SLOPE) {
                                x = x0;
                                y = y1;
                            } else {
                                x = (k0 * (y1 - y0) + k0 * k0 * x0 + x1) / (1 + k0 * k0);
                                y = y0 + k0 * (x - x0);
                            }
                            g2.setColor(Color.red);
                            g2.setStroke(CMisc.DashedStroke);
                            g2.drawLine((int) x1, (int) y1, (int) x, (int) y);

                            if (ln.getExtent() != CLine.ET_ENDLESS) {
                                CPoint[] spt = ln.getTowSideOfLine();
                                if (spt != null && spt.length == 2) {
                                    double r1 = Math.pow(spt[0].getx() - x, 2) + Math.pow(spt[0].gety() - y, 2);
                                    double r2 = Math.pow(spt[1].getx() - x, 2) + Math.pow(spt[1].gety() - y, 2);
                                    double r = Math.pow(spt[1].getx() - spt[0].getx(), 2) + Math.pow(spt[1].gety() - spt[0].gety(), 2);
                                    if (r1 < r && r2 < r) {
                                    } else if (r1 > r2) {
                                        g2.drawLine((int) spt[1].getx(), (int) spt[1].gety(), (int) x, (int) y);
                                    } else {
                                        g2.drawLine((int) spt[0].getx(), (int) spt[0].gety(), (int) x, (int) y);
                                    }

                                }
                            }
                            g2.setStroke(CMisc.NormalLineStroke);
                            this.drawCross((int) x, (int) y, 5, g2);
                        }
                    }
                } else
                    drawCatchRect(g2);
            }
            break;
            case D_PTDISTANCE: {
                int n = SelectList.size();

                if (n >= 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    CPoint p3 = null;
                    if (n == 2) {
//                        if (CatchList.size() == 1) {
//                            CClass c = (CClass) CatchList.get(0);
//                            if (c instanceof CPoint)
//                                p3 = (CPoint) c;
//                        }
                    } else if (n == 3)
                        p3 = (CPoint) SelectList.get(2);
                    if (p3 != null) {
                        double radius = sdistance(p1, p2);
                        int x = (int) p3.getx();
                        int y = (int) p3.gety();
                        g2.setStroke(CMisc.DashedStroke);
                        g2.setColor(Color.red);
                        g2.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
                    }
                }
                this.drawCatchRect(g2);
            }
            break;
            case D_CIRCLE: {
                if (STATUS == 1) {
                    drawcircle2p(FirstPnt.getx(), FirstPnt.gety(), CatchPoint.getx(), CatchPoint.gety(), g2);
                    drawPointOrCross(g2);
                } else
                    this.drawCatchRect(g2);
                break;
            }
            case D_SQUARE: {
                if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    this.drawTipSquare(p1, p2, CatchPoint, g2);
                } else if (STATUS == 0) {
                    if (SelectList.size() > 0) {
                        CPoint p1 = (CPoint) SelectList.get(0);
                        this.drawSmartPVLine(p1, CatchPoint, g2);
                    }
                } else if (STATUS == 1) {
                    CLine line = (CLine) SelectList.get(0);
                    CPoint[] pl = line.getTowSideOfLine();
                    if (pl == null) {
                        break;
                    }
                    drawTipSquare(pl[0], pl[1], CatchPoint, g2);
                }
            }
            break;

            case D_PRATIO: {
                if (SelectList.size() == 2) {
                    drawCatchRect(g2);
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double dx = p2.getx() - p1.getx();
                    double dy = p2.gety() - p1.gety();
                    double ratio = 0;
                    ratio = v1 * 1.00 / v2;
                    dx = dx * ratio;
                    dy = dy * ratio;
                    double x = CatchPoint.getx();
                    double y = CatchPoint.gety();
                    g2.setColor(Color.red);
                    g2.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
                    this.drawCross((int) (x + dx), (int) (y + dy), 3, g2);
                }
            }
            break;
            case D_TRATIO: {
                if (SelectList.size() != 2) {
                    break;
                }
                CPoint p1 = (CPoint) SelectList.get(0);
                CPoint p2 = (CPoint) SelectList.get(1);

                double dx = p2.getx() - p1.getx();
                double dy = p2.gety() - p1.gety();

                double ratio = 0;
                ratio = v1 * 1.0 / v2;
                double x1 = CatchPoint.getx() + dy * ratio;
                double y1 = CatchPoint.gety() - dx * ratio;
//                double x2 = CatchPoint.getx() + dy * ratio;
//                double y2 = CatchPoint.gety() - dx * ratio;

//                double xx = SecondPnt.getx();
//                double yy = SecondPnt.gety();
//                double r1 = Math.pow(xx - x1, 2) + Math.pow(yy - y1, 2);
//                double r2 = Math.pow(xx - x2, 2) + Math.pow(yy - y2, 2);
                g2.setColor(Color.red);
//                if (r1 < r2) {
                g2.drawLine((int) x1, (int) y1, (int) CatchPoint.getx(), (int) CatchPoint.gety());
                this.drawCross((int) (x1), (int) (y1), 3, g2);

//                } else {
//                    g2.drawLine((int) x2, (int) y2, (int) FirstPnt.getx(), (int) FirstPnt.gety());
//                }
            }
            break;
/////////////////////////////////////////////////down;

            case DEFINEPOLY: {
                if (STATUS == 1 && SelectList.size() >= 1) {
                    CPolygon cp = (CPolygon) SelectList.get(0);
                    cp.draw(g2, FirstPnt);
                }
                this.drawCatchRect(g2);
            }
            break;
            case D_PFOOT: {
                int n = SelectList.size();
                if (n == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double x = CatchPoint.getx();
                    double y = CatchPoint.gety();

                    double[] r = get_pt_dmcr(p1.getx(), p1.gety(), p2.getx(), p2.gety(), x, y);
                    double xr = r[0];
                    double yr = r[1];
                    double xx = (p1.getx() + p2.getx()) / 2;
                    double yy = (p1.gety() + p2.gety()) / 2;
                    double dis = sdistance(p1, p2);

                    CatchPoint.setXY(xr, yr);
                    drawCatchRect(g2);
                    g2.setColor(Color.red);
                    g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
                    g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) xr, (int) yr);
                    g2.drawLine((int) xr, (int) yr, (int) p2.getx(), (int) p2.gety());
                    g2.setStroke(CMisc.DashedStroke);
                    g2.drawOval((int) (xx - dis / 2), (int) (yy - dis / 2), (int) dis, (int) dis);
                } else {
                    if (n == 1) {
                        CPoint pt = (CPoint) SelectList.get(0);
                        this.drawSmartPVLine(pt, CatchPoint, g2);
                    }
                    this.drawCatchInterCross(g2);

                }
            }
            break;

            case MULSELECTSOLUTION: {
                for (int i = 0; i < solutionlist.size(); i++) {
                    CPoint p = (CPoint) solutionlist.get(i);
                    g2.setColor(Color.red);
                    g2.drawOval((int) p.getx() - 18, (int) p.gety() - 18, 36, 36);
                    p.draw(g2);
                }
            }
            break;
            case D_POLYGON: {
                if (SelectList.size() >= 1) {
                    drawSmartPVLine(FirstPnt, SecondPnt, g2);
                    if (SelectList.size() == STATUS - 1) {
                        CPoint t1 = (CPoint) (SelectList.get(0));
                        g2.drawLine((int) t1.getx(), (int) t1.gety(), (int) SecondPnt.getx(), (int) SecondPnt.gety());
                    }
                    if (SelectList.size() >= 2) {
                        CPoint t1 = (CPoint) SelectList.get(0);

                        drawTipRect((int) t1.getx(), (int) t1.gety(), g2);

                        for (int i = 1; i < SelectList.size(); i++) {
                            CPoint tp = (CPoint) SelectList.get(i);
                            g2.drawLine((int) t1.getx(), (int) t1.gety(), (int) tp.getx(), (int) tp.gety());
                            t1 = tp;
                        }
                    }
//                    drawPointOrCross(g2);
                } //else
//                    drawCatchInterCross(g2);
                this.drawCatchRect(g2);
            }
            break;
            case DRAWTRIALL: {
                if (STATUS == 1) {
                    CPoint pt = (CPoint) SelectList.get(0);
                    drawSmartPVLine(pt, CatchPoint, g2);
                } else if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double x1 = p1.getx();
                    double y1 = p1.gety();
                    double x2 = p2.getx();
                    double y2 = p2.gety();
                    double xt = (x1 + x2) / 2;
                    double yt = (y1 + y2) / 2;
                    double dx = xt - x1;
                    double dy = yt - y1;

                    double xf = xt - Math.sqrt(3) * dy;
                    double yf = yt + Math.sqrt(3) * dx;

                    double xs = xt + Math.sqrt(3) * dy;
                    double ys = yt - Math.sqrt(3) * dx;

                    double xc = CatchPoint.getx();
                    double yc = CatchPoint.gety();
                    g2.setColor(Color.red);

                    if ((xc - xf) * (xc - xf) + (yc - yf) * (yc - yf) <
                            (xc - xs) * (xc - xs) + (yc - ys) * (yc - ys)) {
                        g2.drawLine((int) x1, (int) y1, (int) xf, (int) yf);
                        g2.drawLine((int) x2, (int) y2, (int) xf, (int) yf);
                    } else {
                        g2.drawLine((int) x1, (int) y1, (int) xs, (int) ys);
                        g2.drawLine((int) x2, (int) y2, (int) xs, (int) ys);
                    }

                }
            }
            break;
            case RA_TRAPEZOID: {
                if (STATUS == 0 && SelectList.size() == 1) {
                    CPoint pt = (CPoint) SelectList.get(0);
                    this.drawSmartPVLine(pt, CatchPoint, g2);
                    drawPointOrCross(g2);
                } else if (STATUS == 1) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double xt = CatchPoint.getx();
                    double yt = CatchPoint.gety();
                    double x1 = p1.getx();
                    double y1 = p1.gety();
                    double x2 = p2.getx();
                    double y2 = p2.gety();
                    double x, y;
                    if (Math.abs(x2 - x1) < CMisc.ZERO) {
                        x = x1;
                        y = yt;
                    } else {
                        double k = (y2 - y1) / (x2 - x1);
                        x = (k * k * xt + x1 + k * y1 - k * yt) / (k * k + 1);
                        y = (k * k * y1 + yt - k * xt + k * x1) / (k * k + 1);
                    }

                    drawPointOrCross(g2);
                    g2.setColor(Color.red);
                    g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    g2.drawLine((int) x1, (int) y1, (int) x, (int) y);
                    g2.drawLine((int) xt, (int) yt, (int) x, (int) y);
                    g2.drawLine((int) xt, (int) yt, (int) x2, (int) y2);
                } else
                    this.drawCatchRect(g2);
            }
            break;
            case TRAPEZOID: {
                if (STATUS == 0) {
                    if (SelectList.size() == 1) {
                        CPoint pt = (CPoint) SelectList.get(0);
                        drawSmartPVLine(pt, CatchPoint, g2);
                        this.drawPointOrCross(g2);
                    } else if (SelectList.size() == 2) {
                        CPoint pt = (CPoint) SelectList.get(0);
                        CPoint pt1 = (CPoint) SelectList.get(1);
                        g2.setColor(Color.red);
                        g2.drawLine((int) pt.getx(), (int) pt.gety(),
                                (int) pt1.getx(), (int) pt1.gety());
                        this.drawSmartPVLine(pt1, CatchPoint, g2);
                        this.drawPointOrCross(g2);
                    } else
                        this.drawCatchRect(g2);
                } else { //1
                    CPoint pt = (CPoint) SelectList.get(0);
                    CPoint pt1 = (CPoint) SelectList.get(1);
                    CPoint pt2 = (CPoint) SelectList.get(2);
                    double x = CatchPoint.getx();
                    double y = (pt.gety() - pt1.gety()) * (x - pt2.getx()) /
                            (pt.getx() - pt1.getx()) + pt2.gety();

                    g2.setColor(Color.red);
                    g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) pt1.getx(), (int) pt1.gety());
                    g2.drawLine((int) pt2.getx(), (int) pt2.gety(), (int) pt1.getx(), (int) pt1.gety());
                    g2.drawLine((int) pt.getx(), (int) pt.gety(), (int) x, (int) y);
                    g2.drawLine((int) pt2.getx(), (int) pt2.gety(), (int) x, (int) y);
                }
            }
            break;


            case PARALLELOGRAM: {
                if (STATUS == 1) {
                    CPoint pt = (CPoint) SelectList.get(0);
                    drawSmartPVLine(pt, CatchPoint, g2);
                } else if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    drawSmartPVLine(p2, CatchPoint, g2);
                    double xt = p1.getx() + CatchPoint.getx() - p2.getx();
                    double yt = p1.gety() + CatchPoint.gety() - p2.gety();
                    g2.drawLine((int) xt, (int) yt, (int) p1.getx(), (int) p1.gety());
                    g2.drawLine((int) xt, (int) yt, (int) CatchPoint.getx(), (int) CatchPoint.gety());
                    g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
                    drawPointOrCross(g2);
                }
            }
            break;
            case RECTANGLE: {
                if (STATUS == 1) {
                    CPoint pt = (CPoint) SelectList.get(0);
                    drawSmartPVLine(pt, CatchPoint, g2);
                } else if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double x1 = p1.getx();
                    double y1 = p1.gety();
                    double x2 = p2.getx();
                    double y2 = p2.gety();

                    double xc = CatchPoint.getx();
                    double yc = CatchPoint.gety();

                    double dlx = x2 - x1;
                    double dly = y2 - y1;
                    double dl = dlx * dlx + dly * dly;

                    double x = ((y2 - yc) * dlx * dly + dly * dly * xc +
                            dlx * dlx * x2) / dl;
                    double y = ((x2 - xc) * dlx * dly + dlx * dlx * yc +
                            dly * dly * y2) / dl;

                    g2.setColor(Color.red);
                    double xt = x + p1.getx() - p2.getx();
                    double yt = y + p1.gety() - p2.gety();

                    g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(),
                            (int) p2.gety());
                    g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) xt,
                            (int) yt);
                    g2.drawLine((int) p2.getx(), (int) p2.gety(), (int) x, (int) y);
                    g2.drawLine((int) xt, (int) yt, (int) x, (int) y);

                }
            }
            break;
            case DRAWTRISQISO: {
                if (STATUS == 1) {
                    CPoint pt = (CPoint) SelectList.get(0);
                    drawSmartPVLine(pt, CatchPoint, g2);
                } else if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    double x1 = p1.getx();
                    double y1 = p1.gety();
                    double x2 = p2.getx();
                    double y2 = p2.gety();
                    double xt = (x1 + x2) / 2;
                    double yt = (y1 + y2) / 2;
                    double dx = xt - x1;
                    double dy = yt - y1;

                    double xf = xt - dy;
                    double yf = yt + dx;

                    double xs = xt + dy;
                    double ys = yt - dx;

                    double xc = CatchPoint.getx();
                    double yc = CatchPoint.gety();
                    g2.setColor(Color.red);

                    if ((xc - xf) * (xc - xf) + (yc - yf) * (yc - yf) <
                            (xc - xs) * (xc - xs) + (yc - ys) * (yc - ys)) {
                        g2.drawLine((int) x1, (int) y1, (int) xf, (int) yf);
                        g2.drawLine((int) x2, (int) y2, (int) xf, (int) yf);
                    } else {
                        g2.drawLine((int) x1, (int) y1, (int) xs, (int) ys);
                        g2.drawLine((int) x2, (int) y2, (int) xs, (int) ys);
                    }
                }
            }
            break;
            case D_IOSTRI: {
                if (SelectList.size() == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);
                    drawTipTirangle(p1, p2, CatchPoint, g2);
                } else {
                    if (SelectList.size() == 1) {
                        CPoint p = (CPoint) SelectList.get(0);
                        drawSmartPVLine(p, CatchPoint, g2);
                    }
                    drawCatchRect(g2);
                }
            }
            break;
            case TRIANGLE: {
                drawPointOrCross(g2);
                if (STATUS == 1 && SelectList.size() == 1) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CatchPoint.draw(g2);
                    g2.setColor(Color.red);
                    g2.drawLine((int) CatchPoint.getx(), (int) CatchPoint.gety(),
                            (int) p1.getx(), (int) p1.gety());
                } else if (STATUS == 2) {
                    if (SelectList.size() == 2) {
                        CPoint p1 = (CPoint) SelectList.get(0);
                        CPoint p2 = (CPoint) SelectList.get(1);

                        CatchPoint.draw(g2);
                        g2.setColor(Color.red);
                        g2.drawLine((int) CatchPoint.getx(), (int) CatchPoint.gety(),
                                (int) p1.getx(), (int) p1.gety());
                        g2.drawLine((int) CatchPoint.getx(), (int) CatchPoint.gety(),
                                (int) p2.getx(), (int) p2.gety());
                        g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(),
                                (int) p2.gety());
                    }

                }
            }
            break;
            case D_3PCIRCLE: {

                if (STATUS == 2) {
                    CPoint p1 = (CPoint) SelectList.get(0);
                    CPoint p2 = (CPoint) SelectList.get(1);

                    double x_1 = p1.getx();
                    double x_2 = p1.gety();
                    double x_3 = p2.getx();
                    double x_4 = p2.gety();
                    double x_5 = CatchPoint.getx();
                    double x_6 = CatchPoint.gety();

                    double m = (2 * (x_3 - x_1) * x_6 + (-2 * x_4 + 2 * x_2) * x_5 +
                            2 * x_1 * x_4 - 2 * x_2 * x_3);

                    double x = (x_4 - x_2) * x_6 * x_6
                            +
                            (-1 * x_4 * x_4 - x_3 * x_3 + x_2 * x_2 + x_1 * x_1) *
                                    x_6
                            + (x_4 - x_2) * x_5 * x_5 + x_2 * x_4 * x_4
                            + (-1 * x_2 * x_2 - x_1 * x_1) * x_4 +
                            x_2 * x_3 * x_3;

                    x = (-1) * x / m;

                    double y = (-1) * ((2 * x_5 - 2 * x_1) * x
                            - x_6 * x_6 - x_5 * x_5 + x_2 * x_2 +
                            x_1 * x_1) / ((2 * x_6 - 2 * x_2));

                    double radius = Math.sqrt(Math.pow(x - x_1, 2) + Math.pow(y - x_2, 2));

                    g2.setStroke(CMisc.DashedStroke);
                    g2.setColor(Color.red);
                    if (Math.abs(x) < CMisc.MAX_DRAW_LEN && Math.abs(y) < CMisc.MAX_DRAW_LEN &&
                            radius < CMisc.MAX_DRAW_LEN) {
                        g2.drawOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
                    }
                    this.drawPointOrCross(g2);
                } else
                    drawCatchRect(g2);


            }
            break;
            case SANGLE: {
                if (SelectList.size() == 2) {
                    CLine ln = (CLine) SelectList.get(0);
                    CPoint p = (CPoint) SelectList.get(1);
                    double k = ln.getK();
                    double k1 = Constraint.get_sp_ag_value(STATUS);
                    double kx1 = (k + k1) / (1 - k * k1);
                    double kx2 = (k - k1) / (1 + k * k1);
                    if (ln.isVertical()) {
                        kx1 = -1 / k1;
                        kx2 = 1 / k1;
                    }

                    double x = CatchPoint.getx();
                    double y = CatchPoint.gety();

                    double r1 = CLine.distanceToPoint(p.getx(), p.gety(), kx1, x, y);
                    double r2 = CLine.distanceToPoint(p.getx(), p.gety(), kx2, x, y);
                    g2.setColor(Color.red);

                    if (r1 <= r2)
                        CLine.drawXLine(p.getx(), p.gety(), kx1, g2);
                    else
                        CLine.drawXLine(p.getx(), p.gety(), kx2, g2);
                }
            }
            break;
            case ZOOM_OUT:
            case ZOOM_IN: {
                if (mouseInside) {
                    g2.setStroke(CMisc.DashedStroke);
                    g2.setColor(Color.red);
                    int x = (int) CatchPoint.getx();
                    int y = (int) CatchPoint.gety();
                    g2.drawLine(x, 0, x, (int) Height);
                    g2.drawLine(0, y, (int) Width, y);
                }
            }
            break;
            case EQUIVALENCE: {
                if (STATUS == 2) {
                    CPolygon p = (CPolygon) SelectList.get(0);
                    CPoint p1 = (CPoint) SelectList.get(1);
                    CPoint t1 = p.getPreviousePoint(p1);
                    CPoint t2 = p.getNextPoint(p1);
                    if (p1 != null && t1 != null && t2 != null) {
                        this.drawAuxLine((int) p1.getx(), (int) p1.gety(), (t2.gety() - t1.gety()) / (t2.getx() - t1.getx()), g2);
                        double[] r = getPTInterSection(CatchPoint.getx(), CatchPoint.gety(), p1.getx(), p1.gety()
                                , t1.getx(), t1.gety(), t2.getx(), t2.gety());
                        this.drawCross((int) r[0], (int) r[1], 2, g2);
                    }
                } else if (STATUS == 3) {

                }
            }
            break;
            case TRANSFORM: {
                if (STATUS == 1 || STATUS == 2 || STATUS == 3) {
                    CPolygon p = (CPolygon) SelectList.get(0);
                    double x1 = catchX + vx1;
                    double y1 = catchY + vy1;
                    p.draw(g2, false, false, vx1, vy1, true, x1, y1, vangle);
                    if (STATUS == 2 && FirstPnt != null) {
                        g2.setColor(Color.red);
                        g2.drawLine((int) CatchPoint.getx(), (int) CatchPoint.gety(), (int) x1, (int) y1);
                        drawCross((int) FirstPnt.getx(), (int) FirstPnt.gety(), 2, g2);
                        if (ThirdPnt != null)
                            drawCross((int) (ThirdPnt.getx() + vx1), (int) (ThirdPnt.gety() + vy1), 2, g2);
                    }
                }
            }
            break;
            case MOVE: {

            }
            break;

            default:
                if (this.isPointOnObject) {
                    drawCatchRect(g2);
                }
                break;
        }
        drawCatchObjName(g2);
    }

    /**
     * Sets the first point for transformation and updates the translation offsets.
     *
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setFirstPnt(double x, double y) {
        if (FirstPnt != null) {
            vtrx = x - FirstPnt.getx();
            vtry = y - FirstPnt.gety();
        }
    }

    /**
     * Sets the transformation status and updates related points and selections.
     *
     * <p>When <code>t</code> is 0, the selection is cleared.
     * When <code>t</code> is 2, the first point's coordinates are updated based on current catch values
     * and a repaint is requested.</p>
     *
     * @param t the new transformation status
     */
    public void setTransformStatus(int t) {

        if (t == 0) {
            clearSelection();
            FirstPnt = SecondPnt = ThirdPnt = null;
        } else if (t == 1) {

        } else if (t == 2) {
            if (FirstPnt != null) {
                CPoint t1 = FirstPnt;
                t1.setXY(catchX + vx1, catchY + vy1);
                repaint();
            }
        }

        STATUS = t;
    }

    /**
     * Requests a repaint of the panel.
     */
    public void repaint() {
        panel.repaint();
    }

    /**
     * Calculates the intersection point of a perpendicular line from a point to a line segment.
     *
     * @param x  the x coordinate of the point
     * @param y  the y coordinate of the point
     * @param xa the x coordinate of the first endpoint of the line segment
     * @param ya the y coordinate of the first endpoint of the line segment
     * @param x1 the x coordinate of the second endpoint of the line segment
     * @param y1 the y coordinate of the second endpoint of the line segment
     * @param x2 the x coordinate of the third endpoint of the line segment
     * @param y2 the y coordinate of the third endpoint of the line segment
     * @return an array containing the x and y coordinates of the intersection point
     */
    double[] getPTInterSection(double x, double y, double xa, double ya, double x1, double y1, double x2, double y2) {
        double k = (y2 - y1) / (x2 - x1);
        double xt = (y - ya + k * xa + x / k) / (k + 1 / k);
        double yt = (x - xa + ya / k + k * y) / (k + 1 / k);
        if (Math.abs(1 / k) > CMisc.MAX_SLOPE) {
            xt = x;
            yt = ya;
        }
        double[] n = new double[2];
        n[0] = xt;
        n[1] = yt;
        return n;

    }

    /**
     * Adds a flash polygon effect between two polygons.
     *
     * @param p1 the first polygon
     * @param p2 the second polygon
     * @param t  the flash type identifier
     * @param ct a flag for custom behavior
     * @param xc the x coordinate for the flash center
     * @param yc the y coordinate for the flash center
     */
    public void addFlashPolygon(CPolygon p1, CPolygon p2, int t, boolean ct, double xc, double yc) {
        int n = p1.getPtn();
        if (n != p2.getPtn()) return;
        JPolygonFlash f = new JPolygonFlash(panel, p1, p2, ct, xc, yc, p1.getColorIndex(), p2.getColorIndex(), t);
        this.addFlash2(f);
    }

    /**
     * Draws an auxiliary line with a dashed stroke based on a given slope.
     *
     * @param x  the x coordinate of the starting point
     * @param y  the y coordinate of the starting point
     * @param k  the slope of the line
     * @param g2 the Graphics2D context used for drawing
     */
    public void drawAuxLine(int x, int y, double k, Graphics2D g2) {
        g2.setColor(Color.red);
        g2.setStroke(CMisc.DashedStroke);
        double max = CMisc.MAX_DRAW_LEN;
        if (Math.abs(k) > CMisc.MAX_K) {
            g2.drawLine(x, 0, x, (int) max);
        } else {
            if (k < 1 && k > -1) {
                g2.drawLine(0, (int) (y - k * x), (int) max, (int) (y + k * (max - x)));
            } else {
                g2.drawLine((int) (x - y / k), 0, (int) (x + (max - y) / k), (int) max);
            }
        }
    }

    /**
     * Adds an isosceles angle constraint by adjusting the third point.
     *
     * @param p1   the first point defining the base of the triangle
     * @param p2   the second point defining the base of the triangle
     * @param p    the point to be adjusted to form an isosceles triangle
     * @param type the angle type indicator (0 for standard isosceles)
     * @return true if the constraint is successfully added, false otherwise
     */
    public boolean addisoAngle(CPoint p1, CPoint p2, CPoint p, int type) {


        double x0 = p1.getx();
        double y0 = p1.gety();
        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double cy = ry / rr;
        double cx = rx / rr;
        double r;
        boolean isleft = false;

        if (Math.abs(rx) < CMisc.ZERO) {
            if (ry * (p1.getx() - p.getx()) > 0) {
                isleft = false;
            } else {
                isleft = true;
            }
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) /
                    Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0);
        }

        double x1 = (x0 + p2.getx()) / 2;
        double y1 = (y0 + p2.gety()) / 2;

        double x2, y2;
        if (isleft) {
            x2 = (x1 + r * cy);
            y2 = (y1 - r * cx);
        } else {
            x2 = x1 - r * cy;
            y2 = y1 + r * cx;
        }

        CLine ln = fd_line(p1, p2);
        p.setXY(x2, y2);
        if (ln != null && ln.nearline(x2, y2)) return false;

        CPoint pt = SmartgetApointFromXY(x2, y2);
        CLine line = null;

        if (type == 0) { // iso
            Constraint cs = new Constraint(Constraint.ISO_TRIANGLE, pt, p1, p2);
            this.addConstraintToList(cs);
            line = new CLine(pt, p1, CLine.LLine);
            this.addLineToList(line);
            line = new CLine(pt, p2, CLine.LLine);
            this.addLineToList(line);
            if (!isLineExists(p1, p2)) {
                CLine lp = new CLine(p1, p2, CLine.LLine);
                this.addLineToList(lp);
            }
            this.charsetAndAddPoly(false);
            this.UndoAdded(GExpert.getLanguage("Isosceles") + " triangle " + p1.m_name + p2.m_name + pt.m_name);
        }

        return true;
    }

    /**
     * Constructs a square by using two initial points and adjusting a third point.
     *
     * @param p1 the first point on the square
     * @param p2 the second point on the square
     * @param p  a point used to determine the orientation and size of the square
     * @return true if the square is successfully constructed, false otherwise
     */
    public boolean addsquare(CPoint p1, CPoint p2, CPoint p) {
        CPoint t1, t2;
        t1 = p1;
        t2 = p2;

        double x0 = p1.getx();
        double y0 = p1.gety();

        double rx = p2.getx() - x0;
        double ry = p2.gety() - y0;
        double dx = p.getx() - x0;
        double dy = p.gety() - y0;
        double rr = Math.sqrt(rx * rx + ry * ry);
        double r;
        boolean isleft = false;
        if (Math.abs(rx) < CMisc.ZERO) {
            if (ry * (p1.getx() - p.getx()) > 0) {
                isleft = false;
            } else {
                isleft = true;
            }
            r = Math.abs(p1.getx() - p.getx());
        } else {
            double k = ry / rx;
            r = Math.abs((p.gety() - k * p.getx() + k * p1.getx() - p1.gety())) /
                    Math.sqrt(1 + k * k);
            isleft = (rx * dy - ry * dx < 0); //((ry * dx / rx - dy > 0 && ry / rx > 0) || (ry * dx / rx - dy < 0 && ry / rx < 0));
        }

        int n = (int) (0.5 + r / rr);
        if (Math.abs(n * rr - r) > 2 * CMisc.PIXEPS) {
            return false;
        }
        if (n == 0) {
            return false;
        }

        if (!this.isLineExists(p1, p2)) {
            CLine lp = new CLine(p1, p2, CLine.LLine);
            this.addLineToList(lp);
        }

        CPoint pa1, pa2;
        pa1 = pa2 = null;

//        CLine line1 = new CLine(p1, CLine.LLine);
//        CLine line2 = new CLine(p2, CLine.LLine);

        Constraint cs1, cs2;
        for (int i = 0; i < n; i++) {
            pa1 = this.CreateANewPoint(0, 0);
            pa2 = this.CreateANewPoint(0, 0);
            CPoint tp1, tp2;
            if (isleft) {
                cs2 = new Constraint(Constraint.NSQUARE, pa2, p2, p1);
                tp2 = this.addADecidedPointWithUnite(pa2);
                if (tp2 == null) {
                    this.addConstraintToList(cs2);
                    this.addPointToList(pa2);
                } else {
                    pa2 = tp2;
                }

                cs1 = new Constraint(Constraint.PSQUARE, pa1, p1, p2);
                tp1 = this.addADecidedPointWithUnite(pa1);
                if (tp1 == null) {
                    this.addConstraintToList(cs1);
                    this.addPointToList(pa1);
                } else {
                    pa1 = tp1;
                }

                addCTMark(p1, pa1, p2, pa1);
                Constraint cs = new Constraint(Constraint.SQUARE, p1, p2, pa2, pa1);
                this.addConstraintToList(cs);

            } else {
                cs2 = new Constraint(Constraint.PSQUARE, pa2, p2, p1);
                tp2 = this.addADecidedPointWithUnite(pa2);
                if (tp2 == null) {
                    this.addConstraintToList(cs2);
                    this.addPointToList(pa2);
                } else {
                    pa2 = tp2;
                }
                cs1 = new Constraint(Constraint.NSQUARE, pa1, p1, p2);
                tp1 = this.addADecidedPointWithUnite(pa1);
                if (tp1 == null) {
                    this.addConstraintToList(cs1);
                    this.addPointToList(pa1);
                } else {
                    pa1 = tp1;
                }
                addCTMark(p1, pa1, p2, pa1);
                Constraint cs = new Constraint(Constraint.SQUARE, p1, p2, pa2, pa1);
                this.addConstraintToList(cs);
            }

//            AddPointToLineX(pa1,line1);
//            AddPointToLineX(pa2,line2);
            add_line(p1, pa1);
            add_line(p2, pa2);
            add_line(pa1, pa2);
            this.addCTMark(this.fd_line(p1, p2), this.fd_line(p1, pa1));
            this.UndoAdded("SQUARE " + p1 + p2 + pa2 + pa1);
            p1 = pa1;
            p2 = pa2;
        }
//        this.addLineToList(line1);
//        this.addLineToList(line2);
        if (pa1 != null && pa2 != null && fd_line(pa1, pa2) == null) {
            CLine line = new CLine(pa1, pa2, CLine.LLine);
            this.addLineToList(line);
        }
        this.UndoAdded("square " + t1.m_name + t2.m_name + pa2.m_name + pa1.m_name);

        return true;
    }

    /**
     * Adds a line defined by two points to the drawing.
     * If the line already exists, the points are added to it.
     *
     * @param p1 the first endpoint of the line
     * @param p2 the second endpoint of the line
     */
    public void add_line(CPoint p1, CPoint p2) {
        CLine ln = null;
        if ((ln = this.fd_line(p1, p2)) != null) {

            this.AddPointToLine(p1, ln);
            this.AddPointToLine(p2, ln);
            return;
        }
        ln = new CLine(p1, p2);
        this.addLineToList(ln);
    }

    /**
     * Finds a point in the drawing that matches the given point's coordinates.
     *
     * @param p the point to match
     * @return the matching point if found, otherwise null
     */
    public CPoint SmartPoint(CPoint p) {
        CPoint pt = SelectAPoint((int) p.getx(), (int) p.gety());
        if (pt != null) {
            p.setXY(pt.getx(), pt.gety());
            return pt;
        }
        return null;
    }

    /**
     * Finds a line in the drawing that contains the given point.
     *
     * @param p the point to match
     * @return the matching line if found, otherwise null
     */
    public CLine SmartPLine(CPoint p) {
        CLine line = SmartPointOnLine(p.getx(), p.gety());
        if (line != null) {
            line.pointonline(p);
            return line;
        }
        return null;
    }

    /**
     * Finds a circle in the drawing that contains the given point.
     *
     * @param p the point to match
     * @return the matching circle if found, otherwise null
     */
    public Circle SmartPCircle(CPoint p) {
        for (int i = 0; i < circlelist.size(); i++) {
            Circle c = (Circle) circlelist.get(i);
            if (c.visible() && c.nearcircle(p.getx(), p.gety(), CMisc.PIXEPS)) {
                c.SmartPonc(p);
                return c;
            }
        }
        return null;
    }

    /**
     * Finds a line in the drawing that is near the given coordinates.
     *
     * @param x the x-coordinate to match
     * @param y the y-coordinate to match
     * @return the matching line if found, otherwise null
     */
    public CLine SmartPointOnLine(double x, double y) {
        double dis = Double.MAX_VALUE;
        CLine ln = null;
        for (int i = 0; i < linelist.size(); i++) {
            CLine line = (CLine) linelist.get(i);
            double d;
            if (line.visible() && line.inside(x, y, CMisc.PIXEPS)) {
                d = CLine.distanceToPoint(line, x, y);
                if (d < dis) {
                    dis = d;
                    ln = line;
                }
            }
        }
        if (dis < CMisc.PIXEPS) {
            return ln;
        } else {
            return null;
        }
    }

    /**
     * Creates a new point with the given coordinates.
     *
     * @param x the x-coordinate of the new point
     * @param y the y-coordinate of the new point
     * @return the created point, or null if the parameter limit is exceeded
     */
    public CPoint CreateANewPoint(double x, double y) {
        if (paraCounter > 1023) {
            CMisc.print("point overflow.");
            return null;
        }

        Param p1 = parameter[paraCounter - 1] = new Param(paraCounter++, x);
        Param p2 = parameter[paraCounter - 1] = new Param(paraCounter++, y);
        CPoint p = new CPoint(p1, p2);
        // this.setTextPositionAutomatically(p.ptext);
        return p;
    }

    /**
     * Creates a new point with the given coordinates and name.
     *
     * @param x    the x-coordinate of the new point
     * @param y    the y-coordinate of the new point
     * @param name the name of the new point
     * @return the created point, or null if the parameter limit is exceeded
     */
    public CPoint CreateANewPoint(double x, double y, String name) {
        if (paraCounter > 1023) {
            CMisc.print("point overflow.");
            return null;
        }

        Param p1 = parameter[paraCounter - 1] = new Param(paraCounter++, x);
        Param p2 = parameter[paraCounter - 1] = new Param(paraCounter++, y);
        CPoint p = new CPoint(name, p1, p2);
        // this.setTextPositionAutomatically(p.ptext);
        return p;
    }

    /**
     * Finds or creates a point at the intersection of two lines.
     *
     * @param line1 the first line
     * @param line2 the second line
     * @return the intersection point, or null if the lines are parallel or already intersect
     */
    public CPoint MeetDefineAPoint(CLine line1, CLine line2) {
        return MeetDefineAPoint(line1, line2, true);
    }

    /**
     * Finds or creates a point at the intersection of two lines, optionally ensuring uniqueness.
     *
     * @param line1  the first line
     * @param line2  the second line
     * @param unique whether to ensure the intersection point is unique
     * @return the intersection point, or null if the lines are parallel or already intersect
     */
    public CPoint MeetDefineAPoint(CLine line1, CLine line2, boolean unique) {
        if (CLine.commonPoint(line1, line2) != null)
            return null;
        if (check_para(line1, line2)) {
            JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The two lines you selected are parallel" +
                    ", don't have any intersection!"), gxInstance.getLanguage("No intersection"), JOptionPane.ERROR_MESSAGE);
            return null;
        }

        int size1 = line1.points.size();
        int size2 = line2.points.size();

        if (size1 <= 1 || size2 <= 1) {
            CPoint p = this.CreateANewPoint(0, 0);

            AddPointToLine(p, line1, false);
            AddPointToLine(p, line2, false);

            charsetAndAddPoly(false);

            CPoint tp = this.addADecidedPointWithUnite(p);
            if (tp != null && unique) {
                line2.points.remove(p);
                line1.points.remove(p);
                line2.addApoint(tp);
                line1.addApoint(tp);
                p = tp;
            } else {
                addPointToList(p);
            }
            this.reCalculate();
            this.UndoAdded(p.m_name + ": intersection of " +
                    line1.getDiscription() + " and " +
                    line2.getDiscription());
            return p;
        } else if (size1 > 1 && size2 > 1) {
            CPoint p = this.CreateANewPoint(0, 0);
            Constraint cs1 = new Constraint(Constraint.INTER_LL, p, line1, line2);
            CPoint tp = this.addADecidedPointWithUnite(p);
            if (tp != null && unique) {
                line2.addApoint(tp);
                line1.addApoint(tp);
                p = tp;
            } else {
                addPointToList(p);
                addConstraintToList(cs1);
                line1.addApoint(p);
                line2.addApoint(p);
            }
            this.UndoAdded(p.m_name + ": intersection of " +
                    line1.getDiscription() + " and " +
                    line2.getDiscription());
            return p;
        }
        return null;
    }

    /**
     * Finds or creates a point at the intersection of a line and a circle.
     *
     * @param line the line
     * @param c    the circle
     * @param m    whether to move the point to the given coordinates
     * @param x    the x-coordinate to move the point to
     * @param y    the y-coordinate to move the point to
     * @return the intersection point, or null if the line and circle do not intersect
     */
    public CPoint MeetLCToDefineAPoint(CLine line, Circle c, boolean m, double x, double y) {
        CPoint p = null;
        CPoint p1 = null;

        if (!check_lc_inter(line, c)) {
            JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The line and the circle you selected don't have any intersection"),
                    gxInstance.getLanguage("No intersection"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        for (int i = 0; i < line.points.size(); i++) {
            Object obj = line.points.get(i);
            if (c.p_on_circle((CPoint) obj)) {
                if (p == null) {
                    p = (CPoint) obj;
                } else {
                    p1 = (CPoint) obj;
                }
            }
        }
        if (p1 != null && p != null) {
            JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The two objects you selected already have two points as their intersections"),
                    gxInstance.getLanguage("intersection already defined"), JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (line.getPtsSize() < 2 || c.points.size() == 0) {
            CPoint pt = this.CreateANewPoint(0, 0);
            Constraint cs1 = new Constraint(Constraint.PONCIRCLE, pt, c);
            this.AddPointToLine(pt, line, false);
            if (m)
                pt.setXY(x, y);
            this.charsetAndAddPoly(true);
            if (m || mulSolutionSelect(pt)) {
                this.addConstraintToList(cs1);
                c.addPoint(pt);
                this.addPointToList(pt);
                this.UndoAdded(pt.m_name + ": intersection of " +
                        line.getDiscription() + " and " +
                        c.getDescription());
            } else {
                this.ErasedADecidedPoint(pt);
                line.points.remove(pt);
                gxInstance.setTipText("Line " + line.m_name + "  and Circle " +
                        c.m_name + "  can not intersect");
            }
            return pt;
        }

        CPoint pout = this.CreateANewPoint(0, 0);
        this.addPointToList(pout);
        Constraint css = new Constraint(Constraint.INTER_LC, pout, line, c);
        this.charsetAndAddPoly(false);
        if (m)
            pout.setXY(x, y);

        line.addApoint(pout);
        c.addPoint(pout);
        this.addPointToList(pout);
        this.addConstraintToList(css);

        this.UndoAdded(pout.m_name + ": intersection of " + line.getDiscription() +
                " and " + c.getDescription());
        this.reCalculate();
        return pout;
    }

    /**
     * Finds or creates a point at the intersection of two circles.
     *
     * @param c1 the first circle
     * @param c2 the second circle
     * @param m  whether to move the point to the given coordinates
     * @param x  the x-coordinate to move the point to
     * @param y  the y-coordinate to move the point to
     * @return the intersection point, or null if the circles do not intersect
     */
    public CPoint MeetCCToDefineAPoint(Circle c1, Circle c2, boolean m, double x, double y) {
        CPoint p = null;
        CPoint p1 = null;

        if (!check_cc_inter(c1, c2)) {
            JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The circles you selected don't have any intersection"),
                    gxInstance.getLanguage("No intersection"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        for (int i = 0; i < c1.points.size(); i++) {
            Object obj = c1.points.get(i);
            if (c2.p_on_circle((CPoint) obj)) {
                if (p == null) {
                    p = (CPoint) obj;
                } else {
                    p1 = (CPoint) obj;
                }
            }
        }
        if (p1 != null && p != null) {
            JOptionPane.showMessageDialog(gxInstance, gxInstance.getLanguage("The two circles you selected already have two points as their intersections"),
                    gxInstance.getLanguage("intersection already defined"), JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (p == null) {
            CPoint pt = this.CreateANewPoint(0, 0);
            Constraint cs = new Constraint(Constraint.INTER_CC, pt, c1, c2);
            if (m)
                pt.setXY(x, y);
            this.charsetAndAddPoly(true);
            if (m || mulSolutionSelect(pt)) {
                this.addConstraintToList(cs);
                c1.addPoint(pt);
                c2.addPoint(pt);
                this.addPointToList(pt);
                this.UndoAdded(pt.m_name + ": intersection of " +
                        c1.getDescription() + " and " +
                        c2.getDescription());
            } else {
                this.ErasedADecidedPoint(pt);
                gxInstance.setTipText("Circle " + c1.m_name + "  and Circle " +
                        c2.m_name + "  can not intersect");
            }
            return pt;
        }
        CPoint pt = this.CreateANewPoint(0, 0);
        Constraint cs = new Constraint(Constraint.INTER_CC, pt, c1, c2);

        CPoint pu = this.addADecidedPointWithUnite(pt);
        if (pu == null) {
            this.addConstraintToList(cs);
            this.addPointToList(pt);
            c1.addPoint(pt);
            c2.addPoint(pt);
            this.charsetAndAddPoly(false);
            this.reCalculate();
            this.UndoAdded(pt.m_name + ": intersection of " + c1.getDescription() +
                    " and " + c2.getDescription());
        } else {
            resetUndo();
        }
        return pt;
    }

    /**
     * Adds a point to a line if it is not already on the line.
     *
     * @param p  the point to add
     * @param ln the line to add the point to
     */
    public void AddPointToLineX(CPoint p, CLine ln) {
        if (ln.containPT(p))
            return;
        ln.addApoint(p);
        Constraint cs = new Constraint(Constraint.PONLINE, p, ln, false);
        p.addcstoPoint(cs);
        this.addConstraintToList(cs);
    }

    /**
     * Adds a point to a circle and optionally adds an undo action.
     *
     * @param p  the point to add
     * @param c  the circle to add the point to
     * @param un whether to add an undo action
     */
    public void AddPointToCircle(CPoint p, Circle c, boolean un) {
        c.addPoint(p);
        Constraint cs = new Constraint(Constraint.PONCIRCLE, p, c);
        this.addConstraintToList(cs);
        p.addcstoPoint(cs);
        if (un) this.UndoAdded(p.TypeString() + " on " + c.getDescription());
    }

    /**
     * Adds a point to a line and adds an undo action.
     *
     * @param p    the point to add
     * @param line the line to add the point to
     */
    public void AddPointToLine(CPoint p, CLine line) {
        AddPointToLine(p, line, true);
    }

    /**
     * Adds a point to a line and optionally adds an undo action.
     *
     * @param p    the point to add
     * @param line the line to add the point to
     * @param un   whether to add an undo action
     */
    public void AddPointToLine(CPoint p, CLine line, boolean un) {
        if (line.containPT(p)) return;

        line.addApoint(p);
        if (line.points.size() > 2 || line.type == CLine.CCLine) {
            Constraint cs = new Constraint(Constraint.PONLINE, p, line);
            this.addConstraintToList(cs);
            line.addconstraint(cs);
            p.addcstoPoint(cs);
            if (un) {
                this.UndoAdded(p.getDescription());
            }
        } else {
            switch (line.type) {
                case CLine.PLine: {
                    Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs);

                    Constraint cs1 = line.getconsByType(Constraint.PARALLEL);
                    if (cs1 == null)
                        break;
                    cs1.PolyGenerate();
                    p.addcstoPoint(cs);
                    if (un)
                        this.UndoAdded(p.getDescription());
                }
                break;
                case CLine.TLine: {
                    Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs);

                    Constraint cs1 = line.getconsByType(Constraint.PERPENDICULAR);
                    if (cs1 == null)
                        break;
                    cs1.PolyGenerate();
                    p.addcstoPoint(cs);
                    if (un)
                        this.UndoAdded(p.getDescription());
                }
                break;
                case CLine.BLine: {
                    Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs);

                    Constraint cs1 = line.getconsByType(Constraint.BLINE);
                    if (cs1 == null) break;
                    cs1.PolyGenerate();
                    if (un) {
                        this.UndoAdded(p.getDescription());
                    }
                }
                break;
                case CLine.ALine: {
                    Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs);

                    Constraint cs1 = line.getconsByType(Constraint.ALINE);
                    if (cs1 == null)
                        break;
                    cs1.setPolyGenerate(true);
                    cs1.PolyGenerate();
                    if (un)
                        this.UndoAdded(p.getDescription());
                    break;
                }
                case CLine.NTALine: {
                    Constraint cs1 = line.getconsByType(Constraint.NTANGLE);
                    if (cs1 == null)
                        break;
                    Vector v = cs1.getAllElements();
                    v.add(p);
                    Constraint cs = new Constraint(Constraint.NTANGLE, v);
                    cs.PolyGenerate();
                    this.addConstraintToList(cs);
                    if (un) {
                        this.UndoAdded(p.getDescription());
                    }
                    break;
                }
                case CLine.SALine: {
                    Constraint cs = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs);

                    Constraint cs1 = line.getconsByType(Constraint.SANGLE);
                    if (cs1 == null)
                        break;
                    line.addApoint(p);
                    cs1.PolyGenerate();
                    this.addConstraintToList(cs1);
                    if (un)
                        this.UndoAdded(p.getDescription());
                }
                break;
                case CLine.ABLine: {
                    Constraint cs1 = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs1);

                    Constraint cs = line.getcons(0);
                    if (cs == null)
                        break;
                    line.addApoint(p);
                    cs.PolyGenerate();
                    p.addcstoPoint(cs);
                    this.addConstraintToList(cs);
                    if (un)
                        this.UndoAdded(p.getDescription());
                }
                break;
                case CLine.TCLine: {
                    Constraint cs1 = new Constraint(Constraint.PONLINE, p, line, false);
                    this.addConstraintToList(cs1);

                    Constraint cs = line.getcons(0);
                    if (cs == null)
                        break;
                    line.addApoint(p);
                    cs.PolyGenerate();
                    p.addcstoPoint(cs);
                    this.addConstraintToList(cs);
                    if (un)
                        this.UndoAdded(p.getDescription());
                }
                break;

                default:
                    break;
            }
        }
    }

    /**
     * Adds a decided point to the list and checks for common points.
     *
     * @param p the point to add
     * @return the common point if found, otherwise null
     */
    public CPoint addADecidedPointWithUnite(CPoint p) {
        boolean r = pointlist.add(p);
        this.charsetAndAddPoly(false);
        if (r)
            pointlist.remove(pointlist.size() - 1);

        CPoint tp = this.CheckCommonPoint(p);

        if (tp == null)
            return null;
        else this.SetVarable();
        eraseAPoly(p.x1.m);
        eraseAPoly(p.y1.m);
        CMisc.showMessage("Point " + tp.m_name + " already exists");
        return tp;
    }

    /**
     * Erases a polynomial from the list.
     *
     * @param m the polynomial to erase
     */
    public void eraseAPoly(TMono m) {
        TPoly t1 = null;
        TPoly t = polylist;
        while (t != null) {
            if (t.poly == m) {
                if (t1 == null)
                    polylist = polylist.next;
                else
                    t1.next = t.next;
            }
            t1 = t;
            t = t.getNext();
        }
    }

    /**
     * Checks for a common point in the list.
     *
     * @param p the point to check
     * @return the common point if found, otherwise null
     */
    public CPoint CheckCommonPoint(CPoint p) {
        TPoly plist = polylist;
        while (plist != null) {
            TMono mm = plist.getPoly();
            int v = poly.lv(mm);
            if (p.x1.xindex == v) {
                p.x1.m = mm;
            } else if (p.y1.xindex == v) {
                p.y1.m = mm;
            }
            plist = plist.getNext();
        }

        if (p.x1.m == null || p.y1.m == null) {
            return null;
        }

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint t = (CPoint) pointlist.get(i);
            if (t == p) {
                continue;
            }
            if (p.isSame_Location(t.getx(), t.gety()) && this.decide_wu_identical(p, t)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Checks if a point already exists in the list.
     *
     * @param p the point to check
     * @return the existing point if found, otherwise null
     */
    public CPoint isPointAlreadyExists(CPoint p) {
        for (int i = 0; i < pointlist.size(); i++) {
            CPoint t = (CPoint) pointlist.get(i);
            if (t == p) {
                continue;
            }
            if (p.isSame_Location(t.getx(), t.gety())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Sets variables for the points in the list.
     */
    public void SetVarable() {
        TPoly plist = polylist;
        while (plist != null) {
            int v = poly.lv(plist.getPoly());
            for (int i = 1; i < this.paraCounter; i++) {
                if ((parameter[i] != null && parameter[i].xindex == v)) {
                    parameter[i].Solved = true;
                    parameter[i].m = plist.getPoly();
                    break;
                }
            }
            plist = plist.getNext();
        }

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.setColorDefault();
        }
    }

    /**
     * Selects a polygon that contains the specified point.
     *
     * @param x the x coordinate of the selection point.
     * @param y the y coordinate of the selection point.
     * @return the selected polygon, or null if no polygon is selected.
     */
    public CPolygon SelectAPolygon(double x, double y) {
        Vector v = new Vector();
        SelectFromAList(v, polygonlist, x, y);
        if (v.size() > 1) {
            return (CPolygon) this.popSelect(v, (int) x, (int) y);
        } else if (v.size() == 1)
            return (CPolygon) v.get(0);
        else return null;

    }

    /**
     * Selects the first object from the given list that is hit by the specified coordinates.
     *
     * @param list the list of objects to search.
     * @param x    the x coordinate of the selection point.
     * @param y    the y coordinate of the selection point.
     * @return the selected object, or null if none are hit.
     */
    public CClass SelectFromAList(Vector list, double x, double y) {
        if (list == null) {
            return null;
        }

        for (int i = 0; i < list.size(); i++) {
            CClass cc = (CClass) list.get(i);
            if (cc.select(x, y)) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Regenerates all polynomial representations, clears current constraints,
     * and optimizes polynomials based on the existing constraints.
     */
    public void re_generate_all_poly() {
        polylist = pblist = null;
        Vector v = new Vector();
        v.addAll(constraintlist);
        constraintlist.clear();
        GeoPoly.clearZeroN();
        this.optmizePolynomial();
        for (int i = 0; i < v.size(); i++) {
            Constraint cs = (Constraint) v.get(i);
            if (cs.is_poly_genereate) {
                cs.clear_all_cons();
                cs.PolyGenerate();
                charsetAndAddPoly(true);
            }
            this.addConstraintToList(cs);
        }
    }

    /**
     * Opens an output file at the specified path.
     *
     * @param path the file path where the output file is to be created.
     * @return a DataOutputStream for writing to the file.
     * @throws IOException if an error occurs while opening or creating the file.
     */
    public DataOutputStream openOutputFile(String path) throws IOException {
        FileOutputStream fp;
        File f = new File(path);

        if (f.exists()) {
            f.delete();
            fp = new FileOutputStream(f, true);

        } else {
            f.createNewFile();
            fp = new FileOutputStream(f, false);
        }
        if (fp == null) {
            return null;
        }
        DataOutputStream out = new DataOutputStream(fp);
        return out;
    }

    /**
     * Saves the current state to the provided DataOutputStream.
     *
     * @param out the output stream to which the state is written.
     * @return true if the state is saved successfully.
     * @throws IOException if an I/O error occurs during saving.
     */
    boolean Save(DataOutputStream out) throws IOException {
        if (cpfield != null) {
            cpfield.run_to_end(this);
        }

        String title = "GE";
        out.write(title.getBytes(), 0, title.length());
        out.writeDouble(CMisc.version);

        Save_global(out);

        out.writeInt(CMisc.id_count);
        out.writeInt(GridX);
        out.writeInt(GridY);
        out.writeBoolean(DRAWGRID);
        out.writeBoolean(SNAP);

        out.writeInt(this.CurrentAction);

        out.writeInt(pnameCounter);
        out.writeInt(plineCounter);
        out.writeInt(pcircleCounter);

        out.writeInt(this.paraCounter);

        for (int i = 0; i < this.paraCounter - 1; i++) {
            parameter[i].Save(out);
        }

        /*
        // Set paraBackup to current paramter values when saving the file
        for (int i = 0; i < paraCounter; i++) {
            if (parameter[i] != null)
                paraBackup[i] = parameter[i].value;
        }
        */


        for (int i = 0; i < this.paraCounter - 1; i++) {
            out.writeDouble(paraBackup[i]);
        }

        int size;

        size = constraintlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            out.writeInt(cs.id);
        }

        size = pointlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.Save(out);
        }

        size = linelist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CLine ln = (CLine) linelist.get(i);
            ln.Save(out);
        }
        size = circlelist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            Circle c = (Circle) circlelist.get(i);
            c.Save(out);
        }

        size = anglelist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CAngle ag = (CAngle) anglelist.get(i);
            ag.Save(out);
        }

        size = distancelist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CDistance dis = (CDistance) distancelist.get(i);
            dis.Save(out);
        }
        size = polygonlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CPolygon poly = (CPolygon) polygonlist.get(i);
            poly.Save(out);
        }
        size = textlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CText tx = (CText) textlist.get(i);
            tx.Save(out);
        }

        size = tracelist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CTrace tr = (CTrace) tracelist.get(i);
            tr.Save(out);
        }

        size = otherlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CClass tr = (CClass) otherlist.get(i);
            out.writeInt(tr.m_type);
            tr.Save(out);
        }

        size = constraintlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            cs.Save(out);
        }

        size = undolist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            UndoStruct undo = (UndoStruct) undolist.get(i);
            undo.Save(out);
        }

        currentUndo.Save(out);
//        if (trackPoint != null) {
//            out.writeInt(trackPoint.m_id);
//        } else {
        out.writeInt(-1);
//        }

        if (this.animate != null && gxInstance.getAnimateDialog().isVisible()) {
            out.writeBoolean(true);
            this.animate.Save(out);
        } else {
            out.writeBoolean(false);
        }

        if (cpfield == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            cpfield.Save(out);
        }
        setSavedTag();
        return true;
    }

    /**
     * Opens an input file from the specified path.
     *
     * @param path the path of the file to be opened.
     * @return a DataInputStream for reading from the file, or null if the file does not exist.
     * @throws IOException if an error occurs while opening the file.
     */
    DataInputStream openInputFile(String path) throws IOException {
        File f = new File(path);
        FileInputStream fp;
        if (f.exists()) {
            fp = new FileInputStream(f);
        } else {
            return null;
        }
        if (f == null) {
            return null;
        }
        file = f;
        DataInputStream in = new DataInputStream(fp);
        return in;
    }

    /**
     * Loads the current state from a file specified by its name.
     *
     * @param name the name of the file to load.
     * @return true if the file is loaded successfully; false otherwise.
     * @throws IOException if an error occurs during file loading.
     */
    boolean Load(String name) throws IOException {
        File f = new File(name);
        return Load(f);
    }

    /**
     * Loads the current state from the given file.
     *
     * @param f the file to load.
     * @return true if the state is loaded successfully; false otherwise.
     * @throws IOException if an error occurs during file loading.
     */
    boolean Load(File f) throws IOException {
        FileInputStream fp;
        if (f.exists()) {
            fp = new FileInputStream(f);
        } else {
            return false;
        }

        if (f == null) {
            return false;
        }

        DataInputStream in = new DataInputStream(fp);
        boolean n = Load(in);
        in.close();
        return n;
    }

    /**
     * Loads the current state from the provided DataInputStream.
     *
     * @param in the DataInputStream from which the state is read.
     * @return true if the state is loaded successfully; false otherwise.
     * @throws IOException if an error occurs during the reading process.
     */
    boolean Load(DataInputStream in) throws IOException {
        byte[] tl = new byte[2];
        in.read(tl, 0, tl.length);
        String title = new String(tl);
        if (title.compareTo("GE") != 0) {
            return false;
        }

        double version = in.readDouble();
        CMisc.version_load_now = version;
        if (version < 0.006) {
            CMisc.eprint(panel, "Error version" + version);
            return false;
        }
        Load_global(in);

        int idcount = CMisc.id_count = in.readInt();
        poly.clearZeroN();


        GridX = in.readInt();
        GridY = in.readInt();
        DRAWGRID = in.readBoolean();
        SNAP = in.readBoolean();
        CurrentAction = in.readInt();
        pnameCounter = in.readInt();
        plineCounter = in.readInt();
        pcircleCounter = in.readInt();
        paraCounter = in.readInt();

        for (int i = 0; i < this.paraCounter - 1; i++) {
            Param pm = new Param();
            pm.Load(in);
            this.parameter[i] = pm;
        }

        for (int i = 0; i < this.paraCounter - 1; i++) {
            paraBackup[i] = in.readDouble();
        }

        int size;

        if (CMisc.version_load_now < 0.01) {
            size = in.readInt();
            int trackCounter = size;
            if (CMisc.version_load_now >= 0.008) {
                for (int i = 0; i < 2 * trackCounter; i++) {
                    in.readInt();
                }
            } else {
                for (int i = 0; i < trackCounter; i++) {
                    in.readInt();
                }
            }
        } else if (CMisc.version_load_now < 0.012) {
            size = in.readInt();
            for (int i = 0; i < size; i++) {
                CTrace ct = new CTrace(null);
                ct.Load(in, this);
                tracelist.add(ct);
            }
        }

        size = in.readInt();
        for (int i = 0; i < size; i++) {
            int d = in.readInt();
            addConstraintToList(new Constraint(d));
        }

        size = in.readInt();
        for (int i = 0; i < size; i++) {
            CPoint p = new CPoint();
            p.Load(in, this);
            pointlist.add(p);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            CLine ln = new CLine(0);
            ln.Load(in, this);
            linelist.add(ln);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            Circle c = new Circle();
            c.Load(in, this);
            circlelist.add(c);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            CAngle ag = new CAngle();
            ag.Load(in, this);
            anglelist.add(ag);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            CDistance dis = new CDistance();
            dis.Load(in, this);
            distancelist.add(dis);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {

            CPolygon poly = new CPolygon();
            poly.Load(in, this);
            addPolygonToList(poly);
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            CText ct = new CText();
            ct.Load(in, this);
            textlist.add(ct);
        }

        if (CMisc.version_load_now >= 0.012) {
            size = in.readInt();
            for (int i = 0; i < size; i++) {
                CTrace ct = new CTrace(null);
                ct.Load(in, this);
                tracelist.add(ct);
            }
        }

        if (CMisc.version_load_now >= 0.017) {
            size = in.readInt();
            if (CMisc.version_load_now <= 0.040)
                for (int i = 0; i < size; i++) {
                    Cedmark ce = new Cedmark();
                    ce.Load(in, this);
                    otherlist.add(ce);
                }
            else {
                for (int i = 0; i < size; i++) {
                    int t = in.readInt();
                    switch (t) {

                        case CClass.TMARK: {
                            CTMark mt = new CTMark();
                            mt.Load(in, this);
                            otherlist.add(mt);
                        }
                        break;
                        case CClass.ARROW: {
                            CArrow ar = new CArrow(null, null);
                            ar.Load(in, this);
                            otherlist.add(ar);
                            break;
                        }
                        case CClass.EQMARK:
                        case 0: {
                            Cedmark ce = new Cedmark();
                            ce.Load(in, this);
                            otherlist.add(ce);
                        }
                        break;
                        default:
                            CMisc.eprint(panel, "Mark unidentified!");
                            break;
                    }
                }
            }

        }


        this.optmizePolynomial();

        size = in.readInt();
        for (int i = 0; i < size; i++) {
            Constraint cs = (Constraint) constraintlist.get(i);
            cs.Load(in, this);
            if (cs.is_poly_genereate) {
                cs.PolyGenerate();
                this.charsetAndAddPoly(true);
            }

        }

//        for (int i = 0; i < size; i++) {
//            constraint cs = (constraint) constraintlist.get(i);
//
//        }

        size = in.readInt();
        for (int i = 0; i < size; i++) {
            UndoStruct ud = new UndoStruct(0);
            ud.Load(in, this);
            undolist.add(ud);
            if (ud.m_type == UndoStruct.T_TO_PROVE_NODE) {
                DrawData.setProveStatus();
            }
        }

        currentUndo = new UndoStruct(0);
        currentUndo.Load(in, this);

        if (version >= 0.006) {
            int ti = in.readInt();
        }

        if (CMisc.version_load_now >= 0.009) { //version 0.009 special for web saver.
            boolean isrun = in.readBoolean();
            if (isrun) {
                this.animate = new AnimateC();
                this.animate.Load(in, this);
                if (gxInstance != null) {
                    {
                        gxInstance.anButton.setEnabled(true);
                        gxInstance.getAnimateDialog().setAttribute(animate);
                        gxInstance.showAnimatePane();
                    }
                }
            } else {
                if (gxInstance != null) {
                    {
                        gxInstance.anButton.setEnabled(false);
                    }
                }
            }
        }

        if (CMisc.version_load_now >= 0.017) {
            boolean havep = in.readBoolean();
            if (havep) {
                cpfield = new CProveField();
                cpfield.Load(in, this);
//                if (gxInstance != null) {
//                    gxInstance.showProveBar(true);
//                }
            }
        }
        CMisc.id_count = idcount;
        CurrentAction = MOVE;
        currentUndo.id = idcount;

        setSavedTag();
        return true;

    }

    /**
     * Saves the global state to the specified output stream.
     *
     * @param out the output stream to save the global state to
     * @throws IOException if an I/O error occurs
     */
    public void Save_global(DataOutputStream out) throws IOException {
        DrawData.Save(out);
        int index = UndoStruct.INDEX;
        out.writeInt(index);
        CMisc.Save(out);
    }

    /**
     * Loads the global state from the specified input stream.
     *
     * @param in the input stream to load the global state from
     * @throws IOException if an I/O error occurs
     */
    public void Load_global(DataInputStream in) throws IOException {
        if (CMisc.version_load_now < 0.010) {
            int size = in.readInt();
            if (size > 100000) {
                return;
            }
            byte[] s = new byte[size];
            in.read(s, 0, size);
        }
        DrawData.Load(in, this);
        if (CMisc.version_load_now >= 0.030) {
            UndoStruct.INDEX = in.readInt();
        }

        if (CMisc.version_load_now >= 0.040)
            CMisc.Load(in);

        footMarkShown = CMisc.isFootMarkShown(); // APPLET ONLY.
        footMarkLength = CMisc.FOOT_MARK_LENGTH;
    }

    /**
     * Writes the drawing to a PostScript file.
     *
     * @param name  the name of the PostScript file
     * @param stype the style type (0: color, 1: gray, 2: black and white)
     * @param ptf   whether to include points in the file
     * @param pts   whether to include the cpfield in the file
     * @return true if the file was written successfully, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean write_ps(String name, int stype, boolean ptf, boolean pts) throws IOException {
        FileOutputStream fp;
        File f = new File(name);
        if (f.exists()) {
            f.delete();
            fp = new FileOutputStream(f, true);
        } else {
            f.createNewFile();
            fp = new FileOutputStream(f, false);
        }
        if (fp == null) {
            return false;
        }

        Calendar c = Calendar.getInstance();
        String stime = "%Create Time: " + c.getTime().toString() + "\n";
        String sversion = "%Created By: " + Version.getNameAndVersion() + "\n";

        String s = "%!PS-Adobe-2.0\n" + stime + sversion + "\n" +
                "%%BoundingBox: 0 500 400 650\n" +
                "0.7 setlinewidth\n" +
                "gsave\n20 700 translate\n.5 .5 scale\n" +
                "/dash {[4 6] 0 setdash stroke [] 0 setdash} def\n" +
                "/cir {0 360 arc} def\n" +
                "/cirfill {0 360 arc 1.0 1.0 1.0 setrgbcolor [] 0 setdash} def\n" +
                "/arcfill{arc 1.0 1.0 1.0 setrgbcolor [] 0 setdash} def\n" +
                "/rm {moveto 4 4 rmoveto} def\n" +
                "/circle {0 360 arc} def\n" +
                "/black {0.0 0.0 0.0 setrgbcolor} def\n" +
                "/mf {/Times-Roman findfont 15.71 scalefont setfont 0.0 0.0 0.0 setrgbcolor} def\n" +
                "/nf {/Times-Roman findfont 11.00 scalefont setfont 0.0 0.0 0.0 setrgbcolor} def\n\n";

        fp.write(s.getBytes());
        this.SaveDrawAttr(fp, stype);

        fp.write("%define points\n".getBytes());

        for (int i = 0; i < pointlist.size(); i++) {
            CPoint p = (CPoint) pointlist.get(i);
            p.SavePS_Define_Point(fp);
        }

        this.write_list_ps(fp, polygonlist, "%-----draw polygons\n", stype);
        this.write_list_ps(fp, anglelist, "%-----draw angles\n", stype);
        this.write_list_ps(fp, distancelist, "%-----draw measures\n", stype);
        this.write_list_ps(fp, otherlist, "%-----draw marks and other\n", stype);
        this.write_list_ps(fp, tracelist, "%-----draw trace list\n", stype);

        write_perp_foot(fp, stype);
        this.write_list_ps(fp, linelist, "%-----draw lines\n", stype);
        this.write_list_ps(fp, circlelist, "%-----draw circles\n", stype);
        if (stype == 0 && ptf) {
            for (int i = 0; i < pointlist.size(); i++) {
                CPoint pt = (CPoint) pointlist.get(i);
                pt.SavePsOringinal(fp);
            }
        } else {
            this.write_list_ps(fp, pointlist, "%-----draw points\n", stype);
        }

        this.write_list_ps(fp, textlist, "%-----draw texts\n", stype);

        if (cpfield != null && pts) {
            cpfield.SavePS(fp, stype);
        }
        s = "grestore\nshowpage\n";
        fp.write(s.getBytes());
        fp.close();
        return true;
    }

    /**
     * Writes the perpendicular foot to the PostScript file.
     *
     * @param fp    the file output stream
     * @param stype the style type
     * @throws IOException if an I/O error occurs
     */
    void write_perp_foot(FileOutputStream fp, int stype) throws IOException {
        Vector vlist = new Vector();
        this.drawPerpFoot(null, vlist, 1);
        if (vlist.size() == 0) {
            return;
        }

        fp.write("%----draw foot\n".getBytes());
        String s = "[] 0 setdash ";
        if (stype == 0) {
            s += "0.5 setlinewidth 1.0 0.0 0.0 setrgbcolor \n";
        } else {
            s += "0.5 setlinewidth 0.0 0.0 0.0 setrgbcolor \n";
        }
        fp.write(s.getBytes());
        for (int i = 0; i < vlist.size() / 2; i++) {
            Point p1 = (Point) vlist.get(2 * i);
            Point p2 = (Point) vlist.get(2 * i + 1);
            String st = p1.getX() + " " + (-p1.getY()) + " moveto " + p2.getX() +
                    " " + (-p2.getY()) + " lineto ";
            if (i % 2 == 0) {
                st += "\n";
            }
            fp.write(st.getBytes());
        }
        fp.write("stroke \n".getBytes());
    }

    /**
     * Writes a list of objects to the PostScript file.
     *
     * @param fp          the file output stream
     * @param vlist       the list of objects to write
     * @param discription the description of the list
     * @param stype       the style type
     * @throws IOException if an I/O error occurs
     */
    void write_list_ps(FileOutputStream fp, Vector vlist, String discription, int stype) throws IOException {
        if (vlist.size() != 0) {
            fp.write((discription).getBytes());
        }
        for (int i = 0; i < vlist.size(); i++) {
            CClass p = (CClass) vlist.get(i);
            p.SavePS(fp, stype);
        }
    }

    /**
     * Saves the drawing attributes to the PostScript file.
     *
     * @param fp    the file output stream
     * @param stype the style type
     * @throws IOException if an I/O error occurs
     */
    private void SaveDrawAttr(FileOutputStream fp, int stype) throws IOException {
        Vector vc = new Vector();
        Vector vd = new Vector();
        Vector vw = new Vector();

        getUDAFromList(vc, vd, vw, pointlist);
        getUDAFromList(vc, vd, vw, linelist);
        getUDAFromList(vc, vd, vw, circlelist);
        getUDAFromList(vc, vd, vw, anglelist);
        getUDAFromList(vc, vd, vw, distancelist);
        getUDAFromList(vc, vd, vw, polygonlist);
        getUDAFromList(vc, vd, vw, textlist);
        getUDAFromList(vc, vd, vw, tracelist);
        getUDAFromList(vc, vd, vw, otherlist);
        for (int i = 0; i < anglelist.size(); i++) {
            CAngle ag = (CAngle) anglelist.get(i);
            if (ag.getAngleType() == 3)
                addAttrToList(ag.getValue1(), vc);
        }
        DrawData.SavePS(vc, vd, vw, fp, stype);
    }

    /**
     * Gets the unique drawing attributes from the list and adds them to the specified vectors.
     *
     * @param vc    the vector for colors
     * @param vd    the vector for dash patterns
     * @param vw    the vector for widths
     * @param vlist the list of objects to process
     */
    private void getUDAFromList(Vector vc, Vector vd, Vector vw, Vector vlist) {
        for (int i = 0; i < vlist.size(); i++) {
            CClass cc = (CClass) vlist.get(i);
            addAttrToList(cc.m_color, vc);
            addAttrToList(cc.m_dash, vd);
            addAttrToList(cc.m_width, vw);
        }
    }

    /**
     * Adds an attribute to the specified vector if it is not already present.
     *
     * @param atrr the attribute to add
     * @param v    the vector to add the attribute to
     */
    private void addAttrToList(int atrr, Vector v) {
        int i = 0;

        for (; i < v.size(); i++) {
            Integer In = (Integer) v.get(i);
            if (In.intValue() == atrr) {
                return;
            } else if (In.intValue() > atrr) {
                v.add(i, atrr);
                return;
            }
        }
        v.add(i, atrr);
    }

    /**
     * Prints the drawing on the specified page.
     *
     * @param graphics   the graphics context
     * @param pageFormat the page format
     * @param pageIndex  the index of the page to print
     * @return PAGE_EXISTS if the page is rendered successfully, NO_SUCH_PAGE otherwise
     * @throws PrinterException if a printer error occurs
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) graphics;

        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        double w = pageFormat.getWidth() / Width;
        double h = pageFormat.getHeight() / Height;

        if (w > h) {
            g2.scale(h, h);
        } else {
            g2.scale(w, w);
        }

        this.paintPoint(graphics);
        return 0;
    }

    /**
     * Prints the content of the current drawing.
     * Sets up the printer job and page format, and initiates the print dialog.
     * If the user confirms the print dialog, it attempts to print the content.
     */
    public void PrintContent() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat landscape = job.defaultPage();
        landscape.setOrientation(PageFormat.LANDSCAPE);

        PageFormat pf = new PageFormat();
        Paper paper = new Paper();
        double margin = 36; // half inch
        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2,
                paper.getHeight() - margin * 2);

        pf.setOrientation(PageFormat.LANDSCAPE);
        pf.setPaper(paper);
        job.setPrintable(this, pf);

        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception exc) {
                CMisc.print("Print Error. ------ " + exc.toString());
            }
        }
    }

    /**
     * Adds a text object to the drawing.
     * If the text object is not null and successfully added to the text list,
     * it adds an undo action for the addition.
     *
     * @param tx the text object to add
     */
    public void addText(CText tx) {
        if (tx != null) {
            if (this.addObjectToList(tx, textlist)) {
                this.UndoAdded(tx.TypeString());
            }
        }
    }

    /**
     * Adds an undo structure to the undo list.
     *
     * @param un the undo structure to add
     */
    public void addNodeToUndoList(UndoStruct un) {
        this.undolist.add(un);
    }

    /**
     * Adds an undo action with a specified tip message.
     *
     * @param tip the tip message for the undo action
     * @return the added undo structure
     */
    public UndoStruct UndoAdded(Object tip) {
        return UndoAdded(tip, true);
    }

    /**
     * Adds an undo action with a specified tip message and a flag to generate proof.
     *
     * @param tip the tip message for the undo action
     * @param gr  whether to generate proof
     * @return the added undo structure
     */
    public UndoStruct UndoAdded(Object tip, boolean gr) {
        return UndoAdded(tip, gr, true);
    }

    /**
     * Adds an undo action with a specified tip message, a flag to generate proof,
     * and a flag to update the manual input bar.
     *
     * @param tip the tip message for the undo action
     * @param gr  whether to generate proof
     * @param m   whether to update the manual input bar
     * @return the added undo structure
     */
    public UndoStruct UndoAdded(Object tip, boolean gr, boolean m) {
        if (CMisc.id_count == this.currentUndo.id) {
            return null;
        }

        String message = tip.toString();
        if (message.length() != 0) {
            char c = message.charAt(0);
            if (c >= 'a' && c <= 'z') {
                c += 'A' - 'a';
                message = c + message.substring(1);
            }
        } // upper case first char of the message.

        this.redolist.clear();
        UndoStruct Undo = this.currentUndo;
        Undo.action = this.CurrentAction;
        if (message != null && message.length() >= 1) {
            char c = message.charAt(0);
            if (c >= 'a' && c <= 'z') {
                c = (char) (c + 'A' - 'a');
                message = c + message.substring(1);
            }
            Undo.msg = message;
        }
        Undo.id_b = CMisc.id_count;
        Undo.paraCounter_b = this.paraCounter;
        Undo.pnameCounter_b = this.pnameCounter;
        Undo.plineCounter_b = this.plineCounter;
        Undo.pcircleCounter_b = this.pcircleCounter;

        this.addNodeToUndoList(Undo);
        this.currentUndo = new UndoStruct(this.paraCounter);
        currentUndo.pnameCounter = this.pnameCounter;
        currentUndo.plineCounter = this.plineCounter;
        currentUndo.pcircleCounter = this.pcircleCounter;

        if (gxInstance != null) {
            if (m && gxInstance.hasMannualInputBar() && gxInstance.getProveStatus() == 4) {
                MProveInputPanel input = gxInstance.getMannalInputToolBar();
                if (input != null)
                    input.addUndo(Undo, message);
            }
            if (gr)
                gxInstance.getpprove().generate();
            gxInstance.reloadLP();
            gxInstance.setBKState();
        }

        return Undo;
    }

    /**
     * Adds a default undo action with a generic message.
     */
    public void UndoAdded() {
        UndoAdded("Not yet added ");
    }


    /**
     * Performs a pure undo operation.
     * Calls `doFlash` and repeatedly calls `Undo_stepPure` until it returns false.
     * Regenerates all polygons and calls `doFlash` again.
     */
    public void UndoPure() {
        this.doFlash();
        while (true) {
            if (false == Undo_stepPure()) {
                this.re_generate_all_poly();
                this.doFlash();
                return;
            }
        }
    }

    /**
     * Performs an undo operation.
     * Repeatedly calls `Undo_step` until it returns false.
     */
    public void Undo() {
        while (true) {
            if (!Undo_step()) {
                return;
            }
        }
    }

    /**
     * Performs a redo operation.
     * Repeatedly calls `redo_step` until it returns null.
     */
    public void redo() {
        while (true) {
            if (null == redo_step()) {
                return;
            }
        }
    }

    /**
     * Performs a single undo step.
     *
     * @param Undo the undo structure
     * @return true if the undo step was successful, false otherwise
     */
    public boolean undo_step(UndoStruct Undo) {
        return undo_step(Undo, true);
    }

    /**
     * Performs a single undo step with an option to regenerate and recalculate.
     *
     * @param Undo the undo structure
     * @param rg   whether to regenerate and recalculate
     * @return true if the undo step was successful, false otherwise
     */
    public boolean undo_step(UndoStruct Undo, boolean rg) {
        if (Undo.m_type == UndoStruct.T_COMBINED_NODE || Undo.m_type == UndoStruct.T_PROVE_NODE) {
            for (int i = 0; i < Undo.childundolist.size(); i++) {
                UndoStruct u = (UndoStruct) Undo.childundolist.get(i);
                this.undo_step(u);
            }
            return true;
        }

        int pc = Undo.id;
        int pcb = Undo.id_b;
        int para = Undo.paraCounter;
        int parab = Undo.paraCounter_b;
        Undo.clear();

        moveUndoObjectFromList(Undo.pointlist, pointlist, pc, pcb);
        moveUndoObjectFromList(Undo.linelist, linelist, pc, pcb);
        moveUndoObjectFromList(Undo.circlelist, circlelist, pc, pcb);
        moveUndoObjectFromList(Undo.anglelist, anglelist, pc, pcb);
        moveUndoObjectFromList(Undo.distancelist, distancelist, pc, pcb);
        moveUndoObjectFromList(Undo.polygonlist, polygonlist, pc, pcb);
        moveUndoObjectFromList(Undo.textlist, textlist, pc, pcb);
        moveUndoObjectFromList(Undo.tracklist, tracelist, pc, pcb);
        moveUndoObjectFromList(Undo.otherlist, otherlist, pc, pcb);

        for (int i = 0; i < constraintlist.size(); i++) {
            Constraint cs = (Constraint) constraintlist.get(i);

            if (cs.id >= pc && cs.id < pcb) {
                Undo.constraintlist.add(cs);
                removeConstraintFromList(cs);
                i--;
                int type = cs.GetConstraintType();
                switch (type) {
                    case Constraint.PONLINE: {
                        CPoint p = (CPoint) cs.getelement(0);
                        CLine ln = (CLine) cs.getelement(1);
                        ln.points.remove(p);
                    }
                    break;
                    case Constraint.PONCIRCLE: {
                        CPoint p = (CPoint) cs.getelement(0);
                        Circle c = (Circle) cs.getelement(1);
                        c.points.remove(p);
                    }
                    break;
                    case Constraint.VISIBLE: {
                        CClass cc = (CClass) cs.getelement(0);
                        cc.setVisible(false);
                    }
                    break;
                    case Constraint.INVISIBLE: {
                        CClass cc = (CClass) cs.getelement(0);
                        cc.setVisible(true);
                    }
                    break;
                    case Constraint.INTER_LL: {
                        CPoint p = (CPoint) cs.getelement(0);
                        CLine ln1 = (CLine) cs.getelement(1);
                        CLine ln2 = (CLine) cs.getelement(2);
                        ln1.points.remove(p);
                        ln2.points.remove(p);
                    }
                    break;
                    case Constraint.INTER_LC: {
                        CPoint p = (CPoint) cs.getelement(0);
                        CLine ln1 = (CLine) cs.getelement(1);
                        Circle c2 = (Circle) cs.getelement(2);
                        ln1.points.remove(p);
                        c2.points.remove(p);
                    }
                    break;
                    case Constraint.INTER_CC: {
                        CPoint p = (CPoint) cs.getelement(0);
                        Circle c1 = (Circle) cs.getelement(1);
                        Circle c2 = (Circle) cs.getelement(2);
                        c1.points.remove(p);
                        c2.points.remove(p);
                    }
                    break;
                    case Constraint.EQUIVALENCE1:
                    case Constraint.EQUIVALENCE2:
                    case Constraint.TRANSFORM:
                    case Constraint.TRANSFORM1: {
                        CPolygon p1 = (CPolygon) cs.getelement(0);
                        CPolygon p2 = (CPolygon) cs.getelement(1);
                        p1.setVisible(true);
                    }
                    break;
                }
            }
        }
        if (rg) {
            re_generate_all_poly();
            this.reCalculate();
        }
        this.currentUndo.id = Undo.id;
        this.currentUndo.msg = Undo.msg;
        this.currentUndo.paraCounter = Undo.paraCounter;
        this.currentUndo.pnameCounter = Undo.pnameCounter;
        this.currentUndo.plineCounter = Undo.plineCounter;
        this.currentUndo.pcircleCounter = Undo.pcircleCounter;

        CMisc.id_count = Undo.id;
        this.paraCounter = Undo.paraCounter;
        this.pnameCounter = Undo.pnameCounter;
        this.plineCounter = Undo.plineCounter;
        this.pcircleCounter = Undo.pcircleCounter;
        return true;
    }

    /**
     * Checks if the redo list is empty.
     *
     * @return true if the redo list is empty, false otherwise
     */
    public boolean isRedoAtEnd() {
        return redolist.size() == 0;
    }

    /**
     * Performs a pure undo step.
     * Clears the selection and catch list, and moves the undo structure from the undo list to the redo list.
     *
     * @return true if the undo step was successful, false otherwise
     */
    public boolean Undo_stepPure() {
        this.undo = null;
        if (CMisc.id_count != this.currentUndo.id) {
            UndoAdded();
        }
        int size = this.undolist.size();
        if (size == 0) {
            return false;
        }
        clearSelection();
        CatchList.clear();
        UndoStruct Undo = (UndoStruct) this.undolist.get(size - 1);
        this.undolist.remove(Undo);
        this.undo_step(Undo, false);
        this.redolist.add(Undo);
        return true;
    }

    /**
     * Performs an undo step.
     * Clears the selection and catch list, and moves the undo structure from the undo list to the redo list.
     *
     * @return true if the undo step was successful, false otherwise
     */
    public boolean Undo_step() {
        this.cancelCurrentAction();
        this.undo = null;
        if (CMisc.id_count != this.currentUndo.id) {
            UndoAdded();
        }
        int size = this.undolist.size();
        if (size == 0) {
            return false;
        }
        clearSelection();
        CatchList.clear();
        UndoStruct Undo = (UndoStruct) this.undolist.get(size - 1);
        this.undolist.remove(Undo);
        this.undo_step(Undo);
        this.redolist.add(Undo);
        this.clearFlash();

        if (gxInstance != null && gxInstance.hasMannualInputBar() && gxInstance.getProveStatus() == 4) {
            MProveInputPanel input = gxInstance.getMannalInputToolBar();
            if (input != null)
                input.undoStep(Undo);
        }

        if (gxInstance != null)
            gxInstance.getpprove().generate();
        return true;
    }

    /**
     * Gets the size of the undo list.
     *
     * @return the size of the undo list
     */
    public int getUndolistSize() {
        return undolist.size();
    }

    /**
     * Gets the size of the redo list.
     *
     * @return the size of the redo list
     */
    public int getRedolistSize() {
        return redolist.size();
    }

    /**
     * Moves undo objects from one list to another based on their IDs.
     *
     * @param v1  the destination list
     * @param v2  the source list
     * @param pc1 the starting ID
     * @param pc2 the ending ID
     */
    public void moveUndoObjectFromList(Vector v1, Vector v2, int pc1, int pc2) {
        for (int i = 0; i < v2.size(); i++) {
            CClass cc = (CClass) v2.get(i);
            if (cc.m_id >= pc1 && cc.m_id < pc2) {
                v1.add(cc);
                v2.remove(i);
                i--;
            }
        }
    }

    /**
     * Selects undo objects from one list to another based on their IDs.
     *
     * @param v1  the destination list
     * @param v2  the source list
     * @param pc1 the starting ID
     * @param pc2 the ending ID
     */
    public void selectUndoObjectFromList(Vector v1, Vector v2, int pc1, int pc2) {
        for (int i = 0; i < v2.size(); i++) {
            CClass cc = (CClass) v2.get(i);
            if (cc.m_id >= pc1 && cc.m_id < pc2) {
                v1.add(cc);
            }
        }
    }

    /**
     * Sets the undo structure for display.
     * If the provided undo structure is not null and flashing is enabled,
     * the method retrieves all associated flash objects and initiates a flash display.
     *
     * @param u                the undo structure to display
     * @param compulsory_flash if true, forces the flash display regardless of the internal flag
     */
    public void setUndoStructForDisPlay(UndoStruct u, boolean compulsory_flash) {
        if (u == null)
            return;

        undo = u;
        if ((u != null && u.flash) || compulsory_flash) {
            Vector v = u.getAllObjects(this);
            JObjectFlash f = new JObjectFlash(panel);
            f.setAt(panel, v);
            this.addFlash(f);
        }
    }

    /**
     * Searches through the undo list for an undo structure whose id range contains the given id,
     * then sets it for display with compulsory flashing.
     *
     * @param id the identifier used to locate the corresponding undo structure
     */
    public void flash_node_by_id(int id) {
        for (int i = 0; i < undolist.size(); i++) {
            UndoStruct u = (UndoStruct) undolist.get(i);
            if (id >= u.id && id < u.id_b) {
                setUndoStructForDisPlay(u, true);
                break;
            }
        }
    }

    /**
     * Creates and returns a flash object for the given graphical component.
     *
     * @param cc the graphical object to be flashed
     * @return the flash object containing the given component
     */
    public JObjectFlash getObjectFlash(CClass cc) {
        JObjectFlash f = new JObjectFlash(panel);
        f.addFlashObject(cc);
        return f;
    }

    /**
     * Creates a flash effect for a single graphical object by wrapping it in a vector
     * and invoking the flash display mechanism.
     *
     * @param cc the graphical object to set for flash display
     */
    public void setObjectListForFlash(CClass cc) {
        Vector v = new Vector();
        v.add(cc);
        setObjectListForFlash(v);
    }

    /**
     * Sets a list of objects for flash display on the specified panel.
     *
     * @param list the list of objects to be flashed
     * @param p    the panel on which the flash effect should be displayed
     */
    public void setObjectListForFlash(Vector list, JPanel p) {

        JObjectFlash f = new JObjectFlash(panel);
        f.setAt(p, list);
        this.addFlash(f);
    }

    /**
     * Sets a list of objects for flash display using the default panel.
     *
     * @param list the list of objects to be flashed
     */
    public void setObjectListForFlash(Vector list) {
        setObjectListForFlash(list, panel);
    }

    /**
     * Extracts all flashable objects from each undo structure in the provided list
     * and sets them for flash display.
     *
     * @param list the list of undo structures whose associated objects will be flashed
     */
    public void setUndoListForFlash(Vector list) {
        Vector v = new Vector();
        for (int i = 0; i < list.size(); i++) {
            UndoStruct u = (UndoStruct) list.get(i);
            v.addAll(u.getAllObjects(this));
        }
        setObjectListForFlash(v);
    }

    /**
     * Extracts all flashable objects from each undo structure in the provided list
     * and sets them for flash display using an alternative flash mechanism.
     *
     * @param list the list of undo structures whose associated objects will be flashed
     */
    public void setUndoListForFlash1(Vector list) {
        Vector v = new Vector();
        for (int i = 0; i < list.size(); i++) {
            UndoStruct u = (UndoStruct) list.get(i);
            v.addAll(u.getAllObjects(this));
        }
        JObjectFlash f = new JObjectFlash(panel);
        f.setAt(panel, v);
        this.addFlash1(f);
    }

    /**
     * Stops any active undo flash display by clearing the flash queue.
     */
    public void stopUndoFlash() {
        this.clearFlash();
    }

    /**
     * Performs a redo step for the provided undo structure.
     * If the undo structure represents a combined or prove node,
     * it recursively redoes each sub-node.
     * Otherwise, it restores various elements and recalculates the state.
     *
     * @param Undo the undo structure to be redone
     * @return true if the redo step is successful
     */
    public boolean redo_step(UndoStruct Undo) {

        if (Undo.m_type == UndoStruct.T_COMBINED_NODE ||
                Undo.m_type == UndoStruct.T_PROVE_NODE) {
            for (int i = 0; i < Undo.childundolist.size(); i++) {
                UndoStruct u = (UndoStruct) Undo.childundolist.get(i);
                this.redo_step(u);
            }
            return true;
        }

        for (int i = 0; i < Undo.pointlist.size(); i++) {
            Object o = Undo.pointlist.get(i);
            this.pointlist.add(o);
        }

        for (int i = 0; i < Undo.linelist.size(); i++) {
            CLine ln = (CLine) Undo.linelist.get(i);
            this.linelist.add(ln);
        }
        for (int i = 0; i < Undo.circlelist.size(); i++) {
            Circle c = (Circle) Undo.circlelist.get(i);
            this.circlelist.add(c);
        }
        for (int i = 0; i < Undo.anglelist.size(); i++) {
            CAngle ca = (CAngle) Undo.anglelist.get(i);
            this.anglelist.add(ca);
        }
        for (int i = 0; i < Undo.constraintlist.size(); i++) {
            Constraint cs = (Constraint) Undo.constraintlist.get(i);
            addConstraintToList(cs);

            int type = cs.GetConstraintType();
            switch (type) {
                case Constraint.PONLINE: {
                    CPoint p = (CPoint) cs.getelement(0);
                    CLine ln = (CLine) cs.getelement(1);
                    ln.addApoint(p);
                }
                break;
                case Constraint.PONCIRCLE: {
                    CPoint p = (CPoint) cs.getelement(0);
                    Circle c = (Circle) cs.getelement(1);
                    c.addPoint(p);
                }
                break;
                case Constraint.VISIBLE: {
                    CClass cc = (CClass) cs.getelement(0);
                    cc.setVisible(true);
                }
                break;
                case Constraint.INVISIBLE: {
                    CClass cc = (CClass) cs.getelement(0);
                    cc.setVisible(false);
                }
                break;
                case Constraint.INTER_LL: {
                    CPoint p = (CPoint) cs.getelement(0);
                    CLine ln1 = (CLine) cs.getelement(1);
                    CLine ln2 = (CLine) cs.getelement(2);
                    ln1.addApoint(p);
                    ln2.addApoint(p);
                }
                break;
                case Constraint.INTER_LC: {
                    CPoint p = (CPoint) cs.getelement(0);
                    CLine ln1 = (CLine) cs.getelement(1);
                    Circle c2 = (Circle) cs.getelement(2);
                    ln1.addApoint(p);
                    c2.addPoint(p);
                }
                break;
                case Constraint.INTER_CC: {
                    CPoint p = (CPoint) cs.getelement(0);
                    Circle c1 = (Circle) cs.getelement(1);
                    Circle c2 = (Circle) cs.getelement(2);
                    c1.addPoint(p);
                    c2.addPoint(p);
                }
                break;
                case Constraint.TRANSFORM: {
                    CPolygon p1 = (CPolygon) cs.getelement(0);
                    CPolygon p2 = (CPolygon) cs.getelement(1);
                    CPoint p = (CPoint) cs.getelement(2);
                    if (p == null)
                        addFlashPolygon(p1, p2, 0, false, 0, 0);
                    else
                        addFlashPolygon(p1, p2, 0, true, p.getx(), p.gety());

                    if (cs.proportion == 1)
                        p1.setVisible(false);
                }
                break;
                case Constraint.TRANSFORM1: {
                    CPolygon p1 = (CPolygon) cs.getelement(0);
                    CPolygon p2 = (CPolygon) cs.getelement(1);
                    addFlashPolygon(p1, p2, 1, false, 0, 0);
                    p1.setVisible(false);

                }
                break;
                case Constraint.EQUIVALENCE1:
                case Constraint.EQUIVALENCE2: {
                    CPolygon p1 = (CPolygon) cs.getelement(0);
                    CPolygon p2 = (CPolygon) cs.getelement(1);
                    CPoint p = (CPoint) cs.getelement(2);
                    if (p == null)
                        addFlashPolygon(p1, p2, 1, false, 0, 0);
                    else
                        addFlashPolygon(p1, p2, 1, true, p.getx(), p.gety());
                    p1.setVisible(false);

                }
                break;
                case Constraint.TRATIO:
                case Constraint.PRATIO: {
//                        CPoint p1 = (CPoint) cs.getelement(0);
//                        CPoint p2 = (CPoint) cs.getelement(1);
//                        CPoint p3 = (CPoint) cs.getelement(2);
//                        CPoint p4 = (CPoint) cs.getelement(3);
//                        JSegmentMoveingFlash f = new JSegmentMoveingFlash(panel, p1, p2, p4, p3, 0, 0);
//                        this.addFlash1(f);
                }
                break;
            }
        }
        for (int i = 0; i < Undo.distancelist.size(); i++) {
            CClass cd = (CClass) Undo.distancelist.get(i);
            this.distancelist.add(cd);

        }
        for (int i = 0; i < Undo.polygonlist.size(); i++) {
            CPolygon cp = (CPolygon) Undo.polygonlist.get(i);
            this.addPolygonToList(cp);
        }
        for (int i = 0; i < Undo.textlist.size(); i++) {
            CText ct = (CText) Undo.textlist.get(i);
            this.textlist.add(ct);
        }
        for (int i = 0; i < Undo.otherlist.size(); i++) {
            CClass ct = (CClass) Undo.otherlist.get(i);
            this.otherlist.add(ct);
        }
        this.re_generate_all_poly();
        this.reCalculate();

        this.currentUndo.id = Undo.id_b;
        this.currentUndo.paraCounter = Undo.paraCounter_b;
        this.currentUndo.pnameCounter = Undo.pnameCounter_b;
        this.currentUndo.plineCounter = Undo.plineCounter_b;
        this.currentUndo.pcircleCounter = Undo.pcircleCounter_b;

        CMisc.id_count = Undo.id_b;
        this.paraCounter = Undo.paraCounter_b;
        this.pnameCounter = Undo.pnameCounter_b;
        this.plineCounter = Undo.plineCounter_b;
        this.pcircleCounter = Undo.pcircleCounter_b;
        return true;
    }

    /**
     * Determines whether a given undo structure is not present in the redo list.
     *
     * @param u the undo structure to check
     * @return true if the structure is not already in the redo list, false otherwise
     */
    public boolean already_redo(UndoStruct u) {
        return !redolist.contains(u);
    }

    /**
     * Performs a redo step with the option to clear the flash display.
     * It retrieves the most recent redo step, updates the internal lists,
     * and returns the corresponding undo structure.
     *
     * @param cf if true, clears any active flash display prior to redoing the step
     * @return the undo structure that was redone, or null if no redo step is available
     */
    public UndoStruct redo_step(boolean cf) {
        if (redolist.size() == 0)
            return null;

        if (cf)
            clearFlash();
        clearSelection();
        CatchList.clear();

        UndoStruct Undo = (UndoStruct) redolist.get(redolist.size() - 1);
        this.redolist.remove(Undo);
        this.redo_step(Undo);
        this.undolist.add(Undo);
        if (gxInstance != null)
            gxInstance.getpprove().generate();
        return Undo;
    }

    /**
     * Performs a redo step with flash clearing enabled by default.
     *
     * @return the undo structure that was redone, or null if no redo step is available
     */
    public UndoStruct redo_step() {
        return redo_step(true);
    }


    /**
     * Finds a point by its index.
     *
     * @param index the index of the point
     * @return the point if found, null otherwise
     */
    public CPoint fd_point(int index) {
        if (index <= 0) {
            return null;
        }
        ProPoint p = gterm().getProPoint(index);
        if (p != null) {
            String s = p.name;
            for (int i = 0; i < pointlist.size(); i++) {
                CPoint t = (CPoint) pointlist.get(i);
                if (t.equals(s))
                    return t;
            }
        }
        if (index >= 1 && index <= pointlist.size())
            return (CPoint) pointlist.get(index - 1);

        return null;
    }

    /**
     * Finds an angle defined by four points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return the angle if found, null otherwise
     */
    public CAngle fd_angle_4p(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        for (int i = 0; i < anglelist.size(); i++) {
            CAngle ag = (CAngle) anglelist.get(i);
            if (ag.isSame(p1, p2, p3, p4)) {
                return ag;
            }
        }
        return null;
    }

    /**
     * Finds an angle defined by four points (alternative method).
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return the angle if found, null otherwise
     */
    public CAngle fd_angle_m(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        for (int i = 0; i < anglelist.size(); i++) {
            CAngle ag = (CAngle) anglelist.get(i);
            if (ag.isSame(p1, p2, p3, p4)) {
                return ag;
            }
        }
        return null;
    }

    /**
     * Finds an angle that matches the given angle.
     *
     * @param ag the angle to match
     * @return the matching angle if found, null otherwise
     */
    public CAngle fd_angle(CAngle ag) {
        for (int i = 0; i < anglelist.size(); i++) {
            CAngle g = (CAngle) anglelist.get(i);
            if (g.sameAngle(ag)) {
                return g;
            }
        }
        return null;
    }

    /**
     * Adds a circle defined by three points.
     *
     * @param o the first point
     * @param a the second point
     * @param b the third point
     * @return the added circle
     */
    public Circle add_rcircle(int o, int a, int b) {
        if (o == 0 || a == 0 || b == 0) {
            return null;
        }
        if (fd_rcircle(o, a, b) != null)
            return fd_rcircle(o, a, b);

        int op = a;
        if (o == a)
            op = b;

        return this.addCr(o, op);
    }

    /**
     * Adds a circle defined by two points.
     *
     * @param o the first point
     * @param a the second point
     * @return the added circle
     */
    public Circle addCr(int o, int a) {
        CPoint p1 = this.fd_point(o);
        Circle c = null;

        if ((c = this.fd_circle(o, a)) != null) {
            return c;
        }
        c = new Circle(p1, fd_point(a));
        this.addCircleToList(c);
        return c;
    }

    /**
     * Finds a circle defined by three points.
     *
     * @param o the first point
     * @param a the second point
     * @param b the third point
     * @return the circle if found, null otherwise
     */
    public Circle fd_rcircle(int o, int a, int b) {
        if (o == 0 || a == 0 || b == 0) {
            return null;
        }
        CPoint p1 = this.fd_point(o);
        CPoint p2 = this.fd_point(a);
        CPoint p3 = this.fd_point(b);
        if (p1 == null || p2 == null || p3 == null) {
            return null;
        }
        CPoint op = p2;
        if (o == a)
            op = p3;

        for (int i = 0; i < circlelist.size(); i++) {
            Circle cc = (Circle) circlelist.get(i);
            if (cc.points.contains(op) && cc.o == p1) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Finds a circle defined by two points.
     *
     * @param o the first point
     * @param a the second point
     * @return the circle if found, null otherwise
     */
    public Circle fd_circle(int o, int a) {
        if (o == 0 || a == 0) {
            return null;
        }
        CPoint p1 = this.fd_point(o);
        CPoint p2 = this.fd_point(a);
        if (p1 == null || p2 == null) {
            return null;
        }

        for (int i = 0; i < circlelist.size(); i++) {
            Circle cc = (Circle) circlelist.get(i);
            if (cc.points.contains(p2) && cc.o == p1) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Finds a circle defined by an origin and two radius points.
     *
     * @param o  the origin point
     * @param p1 the first radius point
     * @param p2 the second radius point
     * @return the circle if found, null otherwise
     */
    public Circle fd_circleOR(CPoint o, CPoint p1, CPoint p2) {
        for (int i = 0; i < circlelist.size(); i++) {
            Circle cc = (Circle) circlelist.get(i);
            if (cc.o != o)
                continue;
            CPoint[] pp = cc.getRadiusPoint();
            if (pp[0] == p1 && pp[1] == p2) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Finds a circle defined by three points.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @return the circle if found, null otherwise
     */
    public Circle fd_circle(int a, int b, int c) {
        CPoint p1 = this.fd_point(a);
        CPoint p2 = this.fd_point(b);
        CPoint p3 = this.fd_point(c);
        for (int i = 0; i < circlelist.size(); i++) {
            Circle cc = (Circle) circlelist.get(i);
            if (cc.points.contains(p1) && cc.points.contains(p2) &&
                    cc.points.contains(p3)) {
                return cc;
            }
        }
        return null;
    }

    /**
     * Adds a mark between two points.
     *
     * @param a the first point
     * @param b the second point
     * @return the added mark
     */
    public Cedmark addedMark(int a, int b) {
        CPoint p1 = this.fd_point(a);
        CPoint p2 = this.fd_point(b);
        if (p1 != null && p2 != null && this.fd_edmark(p1, p2) == null) {
            Cedmark ed = new Cedmark(p1, p2);
            otherlist.add(ed);
            return ed;
        }
        return null;
    }

    /**
     * Adds a mark between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the added mark
     */
    public Cedmark addedMark(CPoint p1, CPoint p2) {
        if (p1 != null && p2 != null && this.fd_edmark(p1, p2) == null) {
            Cedmark ed = new Cedmark(p1, p2);
            otherlist.add(ed);
            return ed;
        }
        return null;
    }

    int aux_angle = 0;
    int aux_polygon = 0;
    int aux_mark = 0;

    /**
     * Resets auxiliary counters and performs an undo step.
     */
    public void resetAux() {
        this.UndoAdded();
        this.Undo_step();
        aux_angle = 0;
        aux_polygon = 0;
        aux_mark = 0;
    }

    /**
     * Initiates a flash using the provided vector of objects.
     *
     * @param v the vector containing objects to flash
     */
    public void flashStep(Vector v) {
        this.setUndoListForFlash(v);
    }

    /**
     * Computes and returns a simple name for an angle based on the current angle count.
     *
     * @return a string representing the angle name
     */
    public String getAngleSimpleName() {
        int n = anglelist.size() + 1;
        String sn;
        char[] ch = new char[1];
        ch[0] = (char) ('A' + n - 10);
        if (n >= 10) {
            sn = new String(ch);
        } else {
            sn = n + "";
        }
        return sn;
    }

    /**
     * Sets the current drawing panel.
     *
     * @param panel the JPanel to set as the current drawing panel
     */
    public void setCurrentDrawPanel(JPanel panel) {
        this.panel = panel;
    }

    /**
     * Sets the current geometry expert instance and updates the drawing panel accordingly.
     *
     * @param gx the GExpert instance to set as the current instance
     */
    public void setCurrentInstance(GExpert gx) {
        gxInstance = gx;
        this.panel = gx.d;
    }

    /**
     * Creates and returns a new parameter, updating the parameter counter.
     *
     * @return a new instance of Param
     */
    public Param getANewParam() {
        int n = paraCounter;
        Param p1 = parameter[paraCounter - 1] = new Param(paraCounter++, 0);
        return p1;
    }

    /**
     * Adds a special angle value.
     *
     * @param v the input value to compute the special angle value
     * @return the x-index of the parameter corresponding to the added angle
     */
    public int add_sp_angle_value(int v) {
        int n = paraCounter;
        Param p1 = parameter[paraCounter - 1] = new Param(paraCounter++, 0);
        p1.value = Constraint.get_sp_ag_value(v);
        p1.setParameterStatic();
        return p1.xindex;
    }

    /**
     * Retrieves a unique name for a circle center.
     * <p>
     * This method generates a name starting with "O", and appends a number
     * if necessary to ensure uniqueness among the existing points.
     *
     * @return the generated circle center name
     */
    String get_cir_center_name() {
        int k = 0;
        while (true) {
            String s = "O";
            if (k != 0)
                s += k;
            boolean e = false;
            for (int i = 0; i < pointlist.size(); i++) {
                CPoint pt = (CPoint) pointlist.get(i);
                String st = pt.getname();
                if (st != null && st.equalsIgnoreCase(s)) {
                    e = true;
                    break;
                }
            }
            if (e == false) {
                break;
            }
            k++;
        }
        if (k == 0)
            return "O";
        else return "O" + k;
    }

    /**
     * Checks if the current state needs to be saved.
     * <p>
     * The state is considered modified if there are points, text objects,
     * other elements, or more than one parameter.
     *
     * @return true if there are unsaved changes, false otherwise
     */
    public boolean need_save() {
        return pointlist.size() > 0
                || textlist.size() > 0
                || otherlist.size() > 0
                || paraCounter > 1;
    }

    /**
     * Finds a polygon associated with the given circle.
     * <p>
     * The method searches through the polygon list for a polygon of a specific type
     * that matches the circle's properties.
     *
     * @param c the circle to search for the corresponding polygon
     * @return the found polygon or null if none match
     */
    public CPolygon fd_polygon(Circle c) {
        for (int i = 0; i < polygonlist.size(); i++) {
            CPolygon cp = (CPolygon) polygonlist.get(i);
            if (cp.ftype != 1)
                continue;

            CClass x = cp.getElement(0);
            if (x != c.o)
                continue;

            CPoint[] pp = c.getRadiusPoint();
            if (pp[0] == cp.getElement(1) && pp[1] == cp.getElement(2))
                return cp;
        }
        return null;
    }

    /**
     * Reduces a given TMono object using the current parameters.
     * <p>
     * This method creates a copy of the input TMono and applies reduction
     * based on the available parameters.
     *
     * @param m the TMono object to reduce
     * @return the reduced TMono object
     */
    public TMono reduce(TMono m) {
        PolyBasic basic = GeoPoly.getInstance();
        return basic.reduce(basic.p_copy(m), parameter);
    }

    /**
     * Handles double-click events on a CClass object.
     * <p>
     * Depending on the type of the object, this method either opens an editor
     * or performs a view action.
     *
     * @param c the CClass object that was double-clicked
     */
    public void onDBClick(CClass c) {
        if (c == null)
            return;
        int t = c.get_type();
        switch (t) {
            case CClass.TEXT:
                CText tx = (CText) c;
                if (tx.getType() == CText.VALUE_TEXT) {
                    TextValueEditor dlg = new TextValueEditor(gxInstance);
                    gxInstance.centerDialog(dlg);
                    dlg.setText(tx);
                    dlg.setVisible(true);
                    break;

                } else {
                    dialog_addText(tx, tx.getX(), tx.getY());
                    break;
                }

            default:
                this.viewElement(c);
        }

    }

    /**
     * Rounds a double value to a specified number of decimal places.
     *
     * @param r the value to round
     * @param n the number of decimal places
     * @return the rounded value
     */
    public double roundn(double r, int n) {
        if (n <= 0)
            return r;

        double d = r;
        double k = 1.0;
        while (n-- > 0) {
            k *= 10;
        }

        return (int) (r * k) / k;
    }

    /**
     * Calculates the numerical value of a given CTextValue expression.
     * <p>
     * This method evaluates the expression represented by the CTextValue object
     * using basic arithmetic operations and mathematical functions.
     *
     * @param ct the CTextValue object representing the expression
     * @return the calculated numerical value
     */
    public double calculate(CTextValue ct) {
        if (ct == null) return 0.0;

        switch (ct.TYPE) {
            case CTextValue.PLUS:
                return calculate(ct.left) + calculate(ct.right);
            case CTextValue.MINUS:
                return calculate(ct.left) - calculate(ct.right);
            case CTextValue.MUL:
                return calculate(ct.left) * calculate(ct.right);
            case CTextValue.DIV:
                return calculate(ct.left) / calculate(ct.right);
            case CTextValue.SQRT:
                return Math.sqrt(calculate(ct.left));
            case CTextValue.SIN:
                return Math.sin(calculate(ct.left));
            case CTextValue.COS:
                return Math.cos(calculate(ct.left));
            case CTextValue.EXP:
                return Math.pow(calculate(ct.left), calculate(ct.right));
            case CTextValue.NODE:
                return parameter[ct.index - 1].value;
            case CTextValue.VALUE:
                return ct.dvalue;
            case CTextValue.PI:
                return Math.PI;
            case CTextValue.E:
                return Math.E;
            case CTextValue.FUNC:
                return CTextValue.cal_func(ct.value, calculate(ct.left));
            case CTextValue.PARAM: {
                CTextValue t = fd_para(ct.sname);
                if (t == null)
                    return 0.0;
                else return t.dvalue;
            }

        }
        return 0.0;

    }

    /**
     * Retrieves a CTextValue parameter by its name.
     * <p>
     * Searches the text list for a text element with a value type that matches the given name.
     *
     * @param s the name of the parameter to find
     * @return the corresponding CTextValue if found; otherwise, null
     */
    CTextValue fd_para(String s) {
        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            if (t.getType() == CText.VALUE_TEXT && t.getname().equalsIgnoreCase(s))
                return t.tvalue;
        }
        return null;
    }

    /**
     * Adds a calculation for the X-coordinate of a point.
     * <p>
     * This method creates a text representation of the X-coordinate
     * calculation for the provided point.
     *
     * @param p the point for which the X-coordinate calculation is added
     */
    final public void addCalculationPX(CPoint p) {
        if (p == null)
            return;
        CText tx = new CText(5, 2, "x" + p.x1.xindex);
        tx.setTextType(CText.VALUE_TEXT);
        tx.m_name = p.m_name + ".x";
        tx.m_width = 2;
        tx.m_dash = 3;

        if (tx.tvalue != null)
            tx.tvalue.calculate(this);
        getTextLocation(tx);
        addText(tx);

    }

    /**
     * Adds a calculation for the Y-coordinate of a point.
     * <p>
     * This method creates a text representation of the Y-coordinate
     * calculation for the provided point.
     *
     * @param p the point for which the Y-coordinate calculation is added
     */
    final public void addCalculationPY(CPoint p) {
        if (p == null)
            return;
        CText tx = new CText(5, 2, "x" + p.y1.xindex);
        tx.setTextType(CText.VALUE_TEXT);
        tx.m_name = p.m_name + ".x";
        tx.m_width = 2;
        tx.m_dash = 3;

        if (tx.tvalue != null)
            tx.tvalue.calculate(this);
        getTextLocation(tx);
        addText(tx);
    }

    /**
     * Adds a polygon calculation displaying its area.
     * <p>
     * Constructs a text object representing the area calculation for the provided polygon.
     *
     * @param poly the polygon for which the area calculation is added
     */
    final public void addCalculationPolygon(CPolygon poly) {
        if (poly == null)
            return;
        String area = GExpert.getLanguage("Area") + " ";
        int n = poly.getPtn();
        for (int i = 0; i < n - 1; i++)
            area += poly.getElement(i);

        CText tx = new CText(5, 2, "");
        tx.setTextType(CText.VALUE_TEXT);
        tx.m_name = area;
        tx.m_width = 1;
        tx.m_dash = 3;
        tx.father = poly;

        if (tx.tvalue != null)
            tx.tvalue.calculate(this);
        getTextLocation(tx);
        addText(tx);
    }

    /**
     * Adds a calculation for the slope of a line.
     * <p>
     * This method creates a text representation of the slope calculation
     * for the given line based on its two supporting points.
     *
     * @param ln the line for which the slope calculation is added
     */
    final public void addLineSlope(CLine ln) {
        if (ln == null)
            return;
        CPoint[] pp = ln.getTowSideOfLine();
        if (pp == null)
            return;
        if (pp.length != 2)
            return;
        String s = "(x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex + ") / (x" +
                pp[0].y1.xindex + " - x" + pp[1].y1.xindex + ")";
        CText tx = new CText(5, 2, s);
        tx.setTextType(CText.VALUE_TEXT);
        tx.m_name = "slope_" + ln.getname();
        tx.m_width = 1;
        tx.m_dash = 3;
        tx.father = ln;

        if (tx.tvalue != null)
            tx.tvalue.calculate(this);
        getTextLocation(tx);
        addText(tx);
    }

    /**
     * Adds a calculation for a circle based on the specified type.
     * <p>
     * Depending on the type parameter, the method adds a text representation for
     * the area, girth, or radius calculation of the circle.
     *
     * @param c the circle for which the calculation is added
     * @param t the type of calculation (0 for area, 1 for girth, other for radius)
     */
    final public void addCalculationCircle(Circle c, int t) {
        if (c == null)
            return;
        CPoint[] pp = c.getRadiusPoint();
        if (pp == null || pp.length == 0)
            return;
        String s, sname;
        if (t == 0) {
            s = CTextValue.SPI + " * " +
                    "((x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex + ")^2 + (x" +
                    pp[0].y1.xindex + " - x" + pp[1].y1.xindex + ")^2)";
            sname = GExpert.getLanguage("Area") + " " + c.m_name;
        } else if (t == 1) {
            s = "2 * " + CTextValue.SPI + " * sqrt((x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex + ")^2 + (x" +
                    pp[0].y1.xindex + " - x" + pp[1].y1.xindex + ")^2)";
            sname = GExpert.getLanguage("Girth") + " " + c.m_name;
        } else {
            s = "sqrt((x" + pp[0].x1.xindex + " - x" + pp[1].x1.xindex + ")^2 + (x" +
                    pp[0].y1.xindex + " - x" + pp[1].y1.xindex + ")^2)";
            sname = GExpert.getLanguage("Radius") + " " + c.m_name;
        }

        CText tx = new CText(5, 2, s);
        tx.setTextType(CText.VALUE_TEXT);
        tx.m_name = sname;
        tx.m_width = 1;
        tx.m_dash = 3;
        tx.father = c;

        if (tx.tvalue != null)
            tx.tvalue.calculate(this);
        getTextLocation(tx);
        addText(tx);
    }

    /**
     * Determines the location for a text object.
     * Calculates an appropriate position for the text object based on existing text objects.
     *
     * @param t1 the text object for which the location is determined
     */
    public void getTextLocation(CText t1) {
        int n = 0;
        int n1 = 5;
        for (int i = 0; i < textlist.size(); i++) {
            CText t = (CText) textlist.get(i);
            if (t.getType() == 3) {
                n = (int) (t.getY() + t.height + 3);
                n1 = t.getX();
            }
        }
        t1.setXY(n1, n);
    }

    /**
     * Cancels the current action and resets relevant states.
     * This method clears selections, resets action-related variables,
     * repaints the panel, and performs undo operations if necessary.
     */
    public void cancelCurrentAction() {

        int type = CurrentAction;
        if (type != MOVE && CurrentAction == CONSTRUCT_FROM_TEXT) {
            this.clearFlash();
        }

        this.CurrentAction = type;
        clearSelection();


        if (type == SETTRACK)
            CTrackPt = null;

        FirstPnt = SecondPnt = null;
        if (type != D_POLYGON)
            STATUS = 0;
        CatchList.clear();
        vtrx = vtry = vx1 = vy1 = vangle = 0;
        if (panel != null && gxInstance != null)
            panel.repaint();


        if (null != this.UndoAdded("", false, false))
            this.Undo_stepPure();
    }

    /**
     * Returns the action type based on the given action code.
     *
     * @param ac the action code
     * @return an integer representing the action type, or -1 if not recognized
     */
    public int getActionType(int ac) //-1. ByPass Action;     0. defalut;
    //  1. Draw Action + point; 2: draw action line + circle
    // 3: fill action 4: angle 5: move/select/intersect   
    {
        switch (ac) {
            case D_POINT:
                return 1;
            case D_LINE:
            case D_PARELINE:
            case D_PERPLINE:
            case PERPWITHFOOT:
            case D_POLYGON:
            case D_CIRCLE:
            case D_3PCIRCLE:
                return 2;
            case D_MIDPOINT:
                return 1;
            case D_PSQUARE:
                return 2;
            case D_TEXT:
                return 0;
            case D_PFOOT:
                return 2;
            case D_CIRCLEBYRADIUS:
            case D_PTDISTANCE:
            case D_CCLINE:
            case D_SQUARE:
                return 1;
            case LRATIO:
            case D_PRATIO:
                return 2;
            case CIRCUMCENTER:
            case BARYCENTER:
            case ORTHOCENTER:
            case INCENTER:
                return 1;
            case D_TRATIO:
                return 2;
            case D_ANGLE:
                return 4;
            case SETEQANGLE:
            case MEET:
                return 5;
            case D_IOSTRI:
                return 2;
            case MIRROR:
                return 5;
            case DISTANCE:
                return 0;
            case H_LINE:
            case V_LINE:
            case D_ALINE:
            case D_ABLINE:
            case D_BLINE:
            case D_CIR_BY_DIM:
            case D_TCLINE:
                return 2;
            case CCTANGENT:
            case SELECT:
            case MOVE:
            case VIEWELEMENT:
            case TRANSLATE:
            case ZOOM_IN:
            case ZOOM_OUT:
            case SETTRACK:
            case ANIMATION:
                return 5;
            case DEFINEPOLY:
                return 3;
            case MULSELECTSOLUTION:
            case MOVENAME:
            case AUTOSHOWSTEP:
                return 5;
            case EQMARK:
            case PROVE:
                return 3;
            case TRIANGLE:
                return 2;
            case HIDEOBJECT:
                return 5;
            case DRAWTRIALL:
            case DRAWTRISQISO:
            case PARALLELOGRAM:
            case RECTANGLE:
            case TRAPEZOID:
            case RA_TRAPEZOID:
                return 2;
            case SETEQSIDE:
            case SHOWOBJECT:
            case SETEQANGLE3P:
            case SETCCTANGENT:
            case NTANGLE:
                return 5;
            case SANGLE:
            case RATIO:
                return 5;
            case RAMARK:
                return 2;
            case TRANSFORM:
            case EQUIVALENCE:
            case FREE_TRANSFORM:
                return 5;
            case LOCUS:
                return 2;
            case ARROW:
                return 2;
            case CONSTRUCT_FROM_TEXT:
                return 5;
        }
        return -1;
    }

    /**
     * Adds a perpendicularity mark between two given lines if they are perpendicular.
     *
     * @param ln1 the first line
     * @param ln2 the second line
     */
    public void addCTMark(CLine ln1, CLine ln2) {
        if (ln1 == null || ln2 == null)
            return;
        if (!CLine.isPerp(ln1, ln2)) {
            JOptionPane.showMessageDialog(gxInstance, "The selected two line is not perpendicular");
            return;
        }

        CTMark m = new CTMark(ln1, ln2);
        this.otherlist.add(m);
    }

    /**
     * Adds a perpendicularity mark by deriving lines from two pairs of points.
     *
     * @param p1 the first point of the first line
     * @param p2 the second point of the first line
     * @param p3 the first point of the second line
     * @param p4 the second point of the second line
     */
    public void addCTMark(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        addCTMark(fd_line(p1, p2), fd_line(p3, p4));
    }

    /**
     * Translates the polygon's points and creates corresponding oriented segments.
     *
     * @param poly the polygon whose points are to be transformed
     */
    public void PolygonTransPointsCreated(CPolygon poly) {
        CPoint pt0, pt1;
        pt0 = pt1 = null;

        int n = poly.getPtn();
        double cx = catchX + vx1;
        double cy = catchY + vy1;
        double sin = Math.sin(vangle);
        double cos = Math.cos(vangle);


        for (int i = 0; i < n; i++) {
            CPoint t = poly.getPoint(i);
            double tx = (t.getx() + vx1);
            double ty = (t.gety() + vy1);

            tx -= cx;
            ty -= cy;
            double mx = (tx) * cos - (ty) * sin;
            double my = (tx) * sin + (ty) * cos;
            tx = mx + cx;
            ty = my + cy;

            CPoint t1 = this.SelectAPoint(tx, ty);
            if (t1 != null && t1 != t) {
                pt0 = t;
                pt1 = t1;
            }
        }
        if (pt0 == null || pt1 == null)
            return;

        for (int i = 0; i < n; i++) {
            CPoint t = poly.getPoint(i);
            double tx = (t.getx() + vx1);
            double ty = (t.gety() + vy1);

            tx -= cx;
            ty -= cy;
            double mx = (tx) * cos - (ty) * sin;
            double my = (tx) * sin + (ty) * cos;
            tx = mx + cx;
            ty = my + cy;

            addOrientedSegment(pt0, pt1, t, tx, ty);
            //CPoint t1 = this.SmartgetApointFromXY(tx, ty);
        }
    }

    /**
     * Creates a new oriented segment for a given point and its transformed coordinates.
     *
     * @param p1 the first reference point
     * @param p2 the second reference point
     * @param px the original point for segment generation
     * @param x  the transformed x-coordinate
     * @param y  the transformed y-coordinate
     */
    public void addOrientedSegment(CPoint p1, CPoint p2, CPoint px, double x, double y) {
        CPoint p = this.CreateANewPoint(x, y);

        Constraint cs = new Constraint(Constraint.PRATIO, p, px, p1, p2, 1, 1);
        CPoint pu = this.addADecidedPointWithUnite(p);
        if (pu == null) {
            this.addConstraintToList(cs);
            this.addPointToList(p);
        }
    }


    /**
     * Loads a GeoGebra file and processes its construction steps.
     * Disclaimer: New version that iterates over the ggb construction element
     *
     * @param in   the data input stream of the file
     * @param path the file path
     * @return true if loaded successfully, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean LoadGGB2(DataInputStream in, String path) throws IOException {
        //BUG: Axis Ratio needs to be 1:1
        // Reset everything in the current construction
        clearAll();
        double version = 0.053; // Current gex file version for JGEX 0.8 is 0.053
        CMisc.version_load_now = version;

        // Need to make type ArrayList because JGEX uses java.awt.List as well
        ArrayList<CPoint> points = new ArrayList<>();
        ArrayList<CLine> lines = new ArrayList<>();
        ArrayList<Circle> circles = new ArrayList<>();
        ArrayList<CAngle> angles = new ArrayList<>();
        HashMap<String, Cons> exprs = new HashMap<>();

        // Save names of all the points
        ArrayList<GgbPoint> pointsGgb = new ArrayList<>();
        // Save names of all the segments
        ArrayList<GgbSegment> segmentsGgb = new ArrayList<>();
        // Save names of all the lines
        ArrayList<GgbLine> linesGgb = new ArrayList<>();
        ArrayList<GgbCircle> circlesGgb = new ArrayList<>();

        ZipFile ggbFile = new ZipFile(path);

        Enumeration<? extends ZipEntry> entries = ggbFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String filename = entry.getName();
            if (filename.equals("geogebra.xml")) {
                InputStream stream = ggbFile.getInputStream(entry);
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    //an instance of builder to parse the specified xml file
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(stream);
                    doc.getDocumentElement().normalize();

                    // System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

                    NodeList nodeSize = doc.getElementsByTagName("size");
                    if (nodeSize.getLength() > 1) {
                        System.out.println("More than one size element in ggb file! Use only graphics view 1.");
                    }
                    NamedNodeMap sizeGGB = nodeSize.item(0).getAttributes();
                    int widthGGB = Integer.parseInt(sizeGGB.getNamedItem("width").getTextContent());
                    int heightGGB = Integer.parseInt(sizeGGB.getNamedItem("height").getTextContent());

                    NodeList nodeScales = doc.getElementsByTagName("coordSystem");
                    if (nodeScales.getLength() > 1) {
                        System.out.println("More than one coord system in ggb file! Use only graphics views 1.");
                    }
                    NamedNodeMap coordsGGB = nodeScales.item(0).getAttributes();
                    double xScaleGGB = Double.parseDouble(coordsGGB.getNamedItem("scale").getTextContent());
                    double yScaleGGB = Double.parseDouble(coordsGGB.getNamedItem("yscale").getTextContent());
                    double xZeroGGB = Double.parseDouble(coordsGGB.getNamedItem("xZero").getTextContent());
                    double yZeroGGB = Double.parseDouble(coordsGGB.getNamedItem("yZero").getTextContent());


                    SetDimension(widthGGB, heightGGB);
                    double widthFraction = (double) Width / widthGGB; // Factor to convert from relative ggb coordinates to JGEX pixels
                    double heightFraction = (double) Height / heightGGB; // Factor to convert from relative ggb coordinates to JGEX pixels
                    double fraction = Math.min(widthFraction, heightFraction);

                    NodeList construction = doc.getElementsByTagName("construction");
                    int len1 = construction.getLength();
                    for (int i = 0; i < len1; i++) { // Should only iterate one time. Only one construction
                        Node constructionNode = construction.item(i);
                        if (constructionNode.getNodeType() == Node.ELEMENT_NODE) {
                            NodeList steps = constructionNode.getChildNodes();
                            int len2 = steps.getLength();
                            for (int j = 0; j < len2; j++) { // Iterate over all steps in the construction
                                Node stepNode = steps.item(j);
                                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element step = (Element) stepNode;
                                    switch (step.getTagName()) {
                                        case "element": // We are mostly interested in points
                                            // Construct a new point
                                            if (step.getAttribute("type").equals("point")) {
                                                Element point = step;
                                                // The name of the point is the same as in GeoGebra
                                                String label = point.getAttribute("label");
                                                GgbPoint gp = new GgbPoint(label);
                                                // Do not add points that already are in the construction
                                                if (pointsGgb.contains(gp)) {
                                                    break;
                                                }

                                                NamedNodeMap coords = point.getElementsByTagName("coords").item(0).getAttributes();
                                                double xGGB = Double.parseDouble(coords.getNamedItem("x").getTextContent());
                                                double yGGB = Double.parseDouble(coords.getNamedItem("y").getTextContent());
                                                double xJGEX = widthFraction * (xZeroGGB + xScaleGGB * xGGB);
                                                double yJGEX = heightFraction * (yZeroGGB - yScaleGGB * yGGB);

                                                CPoint p = this.SmartgetApointFromXY(xJGEX, yJGEX, label); // Automatically handles points on lines and circles
                                                points.add(p);
                                                if (p != null) {
                                                    addToSelectList(p);
                                                    this.UndoAdded(p.TypeString());
                                                }
                                                pointsGgb.add(gp);
                                            }
                                            break;
                                        case "command":
                                            // Polygon
                                            if (step.getAttribute("name").equals("Polygon")) {
                                                NamedNodeMap output = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap input = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = output.getNamedItem("a0").getTextContent();
                                                boolean processed = false;
                                                int n;
                                                for (n = 0; !processed; n++) {
                                                    Node node = input.getNamedItem("a" + n);
                                                    if (node == null) {
                                                        processed = true;
                                                    }
                                                }
                                                n--;
                                                CPolygon polygon = new CPolygon();
                                                // n contains the number of vertices of the polygon
                                                for (int vertex = 0; vertex < n; vertex++) {
                                                    String nameP1 = input.getNamedItem("a" + vertex).getTextContent();
                                                    String nameP2 = input.getNamedItem("a" + (vertex + 1) % n).getTextContent();
                                                    String nameSegment = output.getNamedItem("a" + (vertex + 1)).getTextContent();

                                                    // Below is the same code as for single segment creation. TODO: Unify.
                                                    segmentsGgb.add(new GgbSegment(nameSegment, nameP1, nameP2));

                                                    CPoint[] pts = new CPoint[2];
                                                    pts[0] = getCPoint(points, nameP1);
                                                    pts[1] = getCPoint(points, nameP2);
                                                    polygon.addAPoint(pts[0]);
                                                    // Make a line for every segment
                                                    CPoint tp = pts[0];
                                                    CPoint pp = pts[1];
                                                    lines.add(new CLine(nameSegment, tp, pp));

                                                    getSmartPV(pts[0], pts[1]);

                                                    if (tp != pp && tp != null && pp != null) {
                                                        setSmartPVLine(tp, pp);
                                                        addPointToList(pp);
                                                        CLine ln = new CLine(pp, tp, CLine.LLine);
                                                        this.addLineToList(ln);
                                                        Constraint cs = new Constraint(Constraint.LINE, tp, pp);
                                                        addConstraintToList(cs);
                                                        this.reCalculate();
                                                        this.UndoAdded(ln.getDescription());
                                                    }
                                                }
                                                this.UndoAdded(polygon.getDescription());
                                                // This does not work:
                                                // Graphics g = gxInstance.getGraphics();
                                                // this.drawGrid((Graphics2D) g);
                                                // polygon.draw((Graphics2D) g);

                                            }
                                            // Midpoint
                                            else if (step.getAttribute("name").equals("Midpoint")) {
                                                NamedNodeMap output = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap input = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = output.getNamedItem("a0").getTextContent();
                                                String nameP1 = "";
                                                String nameP2 = "";
                                                // Midpoint of two points
                                                if (input.getLength() == 2) {
                                                    nameP1 = input.getNamedItem("a0").getTextContent();
                                                    nameP2 = input.getNamedItem("a1").getTextContent();
                                                } else if (input.getLength() == 1) {
                                                    // Midpoint of a segment
                                                    String nameSegment = input.getNamedItem("a0").getTextContent();
                                                    GgbSegment segment = getGgbSegment(segmentsGgb, nameSegment);
                                                    nameP1 = segment.getNameP1();
                                                    nameP2 = segment.getNameP2();
                                                }

                                                CPoint[] pts = new CPoint[2];
                                                pts[0] = getCPoint(points, nameP1);
                                                pts[1] = getCPoint(points, nameP2);
                                                CPoint po = this.CreateANewPoint(0, 0, name);
                                                Constraint cs = new Constraint(Constraint.MIDPOINT, po, pts[0], pts[1]);
                                                CPoint pu = this.addADecidedPointWithUnite(po);
                                                if (pu == null) {
                                                    this.addConstraintToList(cs);
                                                    this.addPointToList(po);
                                                    CLine ln = fd_line(pts[0], pts[1]);
                                                    if (ln != null) {
                                                        ln.addApoint(po);
                                                        Constraint cs2 = new Constraint(Constraint.PONLINE, po, ln, false); // is this required?
                                                        this.addConstraintToList(cs2);
                                                    }
                                                    this.UndoAdded(po.getname() + ": the midpoint of " + pts[0].m_name + pts[1].m_name);
                                                } else {
                                                    po = pu;
                                                }
                                                points.add(po);
                                                pointsGgb.add(new GgbPoint(name));
                                                // Handle segment command. Segment between two points
                                            } else if (step.getAttribute("name").equals("Segment")) {
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                if (inputName.getLength() == 2) { // Segment between two points
                                                    String nameSegment = outputName.getNamedItem("a0").getTextContent();
                                                    String nameP1 = inputName.getNamedItem("a0").getTextContent();
                                                    String nameP2 = inputName.getNamedItem("a1").getTextContent();
                                                    segmentsGgb.add(new GgbSegment(nameSegment, nameP1, nameP2));

                                                    CPoint[] pts = new CPoint[2];
                                                    // Make a line for every segment
                                                    pts[0] = getCPoint(points, nameP1);
                                                    pts[1] = getCPoint(points, nameP2);
                                                    CPoint tp = pts[0];
                                                    CPoint pp = pts[1];
                                                    lines.add(new CLine(nameSegment, tp, pp));

                                                    getSmartPV(pts[0], pts[1]);

                                                    if (tp != pp && tp != null && pp != null) {
                                                        setSmartPVLine(tp, pp);
                                                        addPointToList(pp);
                                                        CLine ln = new CLine(pp, tp, CLine.LLine);
                                                        this.addLineToList(ln);
                                                        Constraint cs = new Constraint(Constraint.LINE, tp, pp);
                                                        addConstraintToList(cs);
                                                        this.reCalculate();
                                                        this.UndoAdded(ln.getDescription());
                                                    }
                                                }
                                            } else if (step.getAttribute("name").equals("Line") ||
                                                    step.getAttribute("name").equals("Ray")) { // Handle line-type commands
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                if (inputName.getLength() == 2) {
                                                    String nameLine = outputName.getNamedItem("a0").getTextContent();
                                                    String nameP1 = inputName.getNamedItem("a0").getTextContent();
                                                    String nameP2 = inputName.getNamedItem("a1").getTextContent();
                                                    CPoint[] pts = new CPoint[2];
                                                    // Make a line for every segment
                                                    pts[0] = getCPoint(points, nameP1);
                                                    pts[1] = getCPoint(points, nameP2);

                                                    if (pts[1] != null) { //  // Line between two points Line(Point,Point)
                                                        linesGgb.add(new GgbLine(nameLine, nameP1, nameP2));
                                                        CPoint tp = pts[0];
                                                        CPoint pp = pts[1];
                                                        lines.add(new CLine(nameLine, tp, pp));

                                                        getSmartPV(pts[0], pts[1]);

                                                        if (tp != pp && tp != null && pp != null) {
                                                            setSmartPVLine(tp, pp);
                                                            addPointToList(pp);
                                                            CLine ln = new CLine(pp, tp, CLine.LLine);
                                                            ln.ext_type = 2; // this is a line, not a segment (0)
                                                            this.addLineToList(ln);
                                                            Constraint cs = new Constraint(Constraint.LINE, tp, pp);
                                                            addConstraintToList(cs);
                                                            this.reCalculate();
                                                            this.UndoAdded(ln.getDescription());
                                                        }
                                                    } else { // Handle parallel lines: Line(Point, Parallel Line)
                                                        CLine origLine = getCLine(points, lines, nameP2);
                                                        CLine linePar = new CLine(pts[0], CLine.PLine);
                                                        linePar.ext_type = 2; // line, not a segment (0)
                                                        lines.add(linePar);

                                                        Constraint c = new Constraint(Constraint.PARALLEL, linePar, origLine);
                                                        this.addConstraintToList(c);
                                                        linePar.addconstraint(c);
                                                        origLine.addconstraint(c);
                                                        addLineToList(linePar);
                                                        UndoStruct u = this.UndoAdded(linePar.TypeString() + " parallel " +
                                                                origLine.getDiscription() + " passing " +
                                                                pts[0].getname());
                                                        u.addObject(linePar);
                                                        u.addObject(origLine);
                                                        u.addObject(pts[0]);
                                                        linePar.m_name = nameLine;
                                                    }
                                                }
                                            } else if (step.getAttribute("name").equals("OrthogonalLine")) { // Handle PerpendicularLine (OrthogonalLine) command
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                if (inputName.getLength() == 2) {
                                                    String nameLinePerp = outputName.getNamedItem("a0").getTextContent();
                                                    String nameP = inputName.getNamedItem("a0").getTextContent();
                                                    String nameLine = inputName.getNamedItem("a1").getTextContent();
                                                    linesGgb.add(new GgbLine(nameLinePerp));
                                                    CPoint footPoint = getCPoint(points, nameP);
                                                    CLine origLine = getCLine(points, lines, nameLine);
                                                    CLine linePerp = new CLine(footPoint, CLine.TLine);
                                                    linePerp.ext_type = 2; // line, not a segment (0)
                                                    lines.add(linePerp);

                                                    Constraint c = new Constraint(Constraint.PERPENDICULAR, linePerp, origLine);
                                                    this.addConstraintToList(c);
                                                    linePerp.addconstraint(c);
                                                    origLine.addconstraint(c);
                                                    addLineToList(linePerp);
                                                    addCTMark(origLine, linePerp);
                                                    // this.otherlist.add(m);
                                                    UndoStruct u = this.UndoAdded(linePerp.TypeString() + " perp " +
                                                            origLine.getDiscription() + " passing " +
                                                            footPoint.getname());
                                                    u.addObject(linePerp);
                                                    u.addObject(origLine);
                                                    u.addObject(footPoint);
                                                    linePerp.m_name = nameLinePerp;
                                                }
                                            } else if (step.getAttribute("name").equals("LineBisector")) {
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String nameLineBisector = outputName.getNamedItem("a0").getTextContent();
                                                CPoint p1 = null;
                                                CPoint p2 = null;
                                                CLine origLine = null;
                                                if (inputName.getLength() == 1) {
                                                    String nameLine = inputName.getNamedItem("a0").getTextContent();
                                                    origLine = getCLine(points, lines, nameLine);
                                                    p1 = origLine.getPoint(0);
                                                    p2 = origLine.getPoint(1);
                                                } else if (inputName.getLength() == 2) {
                                                    String namePoint1 = inputName.getNamedItem("a0").getTextContent();
                                                    String namePoint2 = inputName.getNamedItem("a1").getTextContent();
                                                    p1 = getCPoint(points, namePoint1);
                                                    p2 = getCPoint(points, namePoint2);
                                                }
                                                linesGgb.add(new GgbLine(nameLineBisector));
                                                CLine lineBisector = new CLine(CLine.BLine);
                                                lineBisector.ext_type = 2; // line, not a segment (0)

                                                Constraint c = new Constraint(Constraint.BLINE, lineBisector, p1, p2);
                                                lineBisector.addconstraint(c);
                                                this.addLineToList(lineBisector);
                                                this.addConstraintToList(c);
                                                lines.add(lineBisector);
                                                UndoStruct u = this.UndoAdded("Bline " + lineBisector.getDescription());
                                                u.addObject(lineBisector);
                                                lineBisector.m_name = nameLineBisector;

                                                // String mpname = namePoint1 + namePoint2 + "midpoint";
                                                String mpname = "P" + nameLineBisector;
                                                CPoint po = this.CreateANewPoint(0, 0, mpname);
                                                Constraint cs = new Constraint(Constraint.MIDPOINT, po, p1, p2);
                                                // This is a helper point, so we don't have to create a new point
                                                // if there is already existing something which seems the same.
                                                CPoint pu = this.addADecidedPointWithUnite(po);
                                                if (pu == null) {
                                                    this.addConstraintToList(cs);
                                                    this.addPointToList(po);
                                                    this.UndoAdded(po.getname() + ": the midpoint of " + p1.m_name + p2.m_name);
                                                    points.add(po);
                                                } else {
                                                    po = pu;
                                                }

                                                // String rpname = namePoint1 + namePoint2 + "rotated";
                                                String rpname = "Q" + nameLineBisector;
                                                double xd = po.getx() - p1.getx();
                                                double yd = po.gety() - p1.gety();
                                                double xe = po.getx() + yd;
                                                double ye = po.gety() - xd;

                                                CPoint pr = this.CreateANewPoint(xe, ye, rpname);

                                                lineBisector.addApoint(po);
                                                lineBisector.addApoint(pr);

                                                Constraint cr = new Constraint(Constraint.PERPENDICULAR, p1, p2, po, pr);
                                                // This is a helper point, so we don't have to create a new point
                                                // if there is already existing something which seems the same.
                                                CPoint pq = this.addADecidedPointWithUnite(pr);
                                                if (pq == null) {
                                                    this.addConstraintToList(cr);
                                                    this.addPointToList(pr);
                                                    this.UndoAdded(pr.getname() + ": a second point of " + name);
                                                    points.add(pr);
                                                } else {
                                                    pr = pq;
                                                }

                                                if (inputName.getLength() == 1) {
                                                    addCTMark(origLine, lineBisector);
                                                }

                                            } else if (step.getAttribute("name").equals("AngularBisector")) {
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String nameLineABisector = outputName.getNamedItem("a0").getTextContent();
                                                CPoint p1 = null;
                                                CPoint p2 = null;
                                                CPoint p3 = null;
                                                if (inputName.getLength() == 3) {
                                                    String namePoint1 = inputName.getNamedItem("a0").getTextContent();
                                                    String namePoint2 = inputName.getNamedItem("a1").getTextContent();
                                                    String namePoint3 = inputName.getNamedItem("a2").getTextContent();
                                                    p1 = getCPoint(points, namePoint1);
                                                    p2 = getCPoint(points, namePoint2);
                                                    p3 = getCPoint(points, namePoint3);

                                                    linesGgb.add(new GgbLine(nameLineABisector));

                                                    String ptname = "P" + nameLineABisector;
                                                    CPoint pt = this.CreateANewPoint(0, 0, ptname);

                                                    Constraint cs1 = new Constraint(Constraint.COLLINEAR, pt, p1, p3);
                                                    this.addPointToList(pt);
                                                    this.UndoAdded(pt.getDescription());
                                                    pt.m_name = ptname;
                                                    this.addConstraintToList(cs1);

                                                    points.add(pt);
                                                    CLine lineABisector = new CLine(p2, pt);
                                                    Constraint cs = new Constraint(Constraint.ANGLE_BISECTOR, p1, p2, p3, lineABisector);
                                                    lineABisector.ext_type = 2; // line, not a segment (0)

                                                    lineABisector.addconstraint(cs);
                                                    this.addLineToList(lineABisector);
                                                    this.addConstraintToList(cs);

                                                    lines.add(lineABisector);
                                                    UndoStruct u = this.UndoAdded("ABline " + lineABisector.getDescription());
                                                    u.addObject(lineABisector);
                                                    lineABisector.m_name = nameLineABisector;
                                                }

                                            } else if (step.getAttribute("name").equals("Intersect")) { // Handle intersect command
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                // Intersect two objects
                                                if (inputName.getLength() == 2) { // Currently only intersection between two lines or two circles if there is only one point given is implemented.
                                                    // TODO: Handle intersection in general between circles and circle and line too.
                                                    String name = outputName.getNamedItem("a0").getTextContent();
                                                    String nameO1 = inputName.getNamedItem("a0").getTextContent();
                                                    String nameO2 = inputName.getNamedItem("a1").getTextContent();
                                                    CLine line1 = getCLine(points, lines, nameO1);
                                                    CLine line2 = getCLine(points, lines, nameO2);
                                                    if (line1 != null && line2 != null) {
                                                        CPoint intersectionPoint = MeetDefineAPoint(line1, line2, false);
                                                        points.add(intersectionPoint);
                                                        pointsGgb.add(new GgbPoint(name));
                                                        this.addPointToList(intersectionPoint);
                                                        this.UndoAdded(intersectionPoint.getDescription());
                                                        intersectionPoint.m_name = name;
                                                        points.add(intersectionPoint);
                                                        pointsGgb.add(new GgbPoint(name));
                                                        this.addPointToList(intersectionPoint);
                                                        this.UndoAdded(intersectionPoint.getDescription());
                                                        intersectionPoint.m_name = name;
                                                    } else { // One of the inputs should be a circle.
                                                        Circle circle1 = getCircle(circles, nameO1);
                                                        Circle circle2 = getCircle(circles, nameO2);
                                                        // FIXME: The x=0, y=0 workarounds below can be problematic if
                                                        // there are more intersection points.
                                                        if (circle1 != null && circle2 != null) {
                                                            CPoint intersectionPoint = MeetCCToDefineAPoint(circle1, circle2, false,
                                                                    0, 0);
                                                            intersectionPoint.m_name = name;
                                                            points.add(intersectionPoint);
                                                            pointsGgb.add(new GgbPoint(name));
                                                            this.addPointToList(intersectionPoint);
                                                            this.UndoAdded(intersectionPoint.getDescription());
                                                        } else if (circle1 != null && line2 != null) {
                                                            CPoint intersectionPoint = MeetLCToDefineAPoint(line2, circle1, false,
                                                                    0, 0);
                                                            intersectionPoint.m_name = name;
                                                            points.add(intersectionPoint);
                                                            pointsGgb.add(new GgbPoint(name));
                                                            this.addPointToList(intersectionPoint);
                                                            this.UndoAdded(intersectionPoint.getDescription());
                                                        } else if (line1 != null && circle2 != null) {
                                                            CPoint intersectionPoint = MeetLCToDefineAPoint(line1, circle2, false,
                                                                    0, 0);
                                                            intersectionPoint.m_name = name;
                                                            points.add(intersectionPoint);
                                                            pointsGgb.add(new GgbPoint(name));
                                                            this.addPointToList(intersectionPoint);
                                                            this.UndoAdded(intersectionPoint.getDescription());
                                                        }
                                                    }
                                                }
                                            } else if (step.getAttribute("name").equals("Circle")) { // Handle Circle command Circle(Point,Point)
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();

                                                if (inputName.getLength() == 2) { // circle with center and radius
                                                    String name = outputName.getNamedItem("a0").getTextContent();
                                                    String nameCenterPoint = inputName.getNamedItem("a0").getTextContent();
                                                    String namePointOnCircle = inputName.getNamedItem("a1").getTextContent();

                                                    CPoint p1 = getCPoint(points, nameCenterPoint);
                                                    CPoint p2 = getCPoint(points, namePointOnCircle);

                                                    Circle c = new Circle(p1, p2);
                                                    // c.m_name = name;
                                                    circles.add(c);
                                                    circlesGgb.add(new GgbCircle(name));
                                                    Constraint cs = new Constraint(Constraint.CIRCLE, p1, p2);
                                                    this.addConstraintToList(cs);
                                                    this.charsetAndAddPoly(false);
                                                    this.UndoAdded(c.getDescription());
                                                    addCircleToList(c);
                                                    c.m_name = name;
                                                }

                                                if (inputName.getLength() == 3) { // circumcircle of a triangle
                                                    String name = outputName.getNamedItem("a0").getTextContent();
                                                    String namePoint1 = inputName.getNamedItem("a0").getTextContent();
                                                    String namePoint2 = inputName.getNamedItem("a1").getTextContent();
                                                    String namePoint3 = inputName.getNamedItem("a2").getTextContent();

                                                    CPoint p1 = getCPoint(points, namePoint1);
                                                    CPoint p2 = getCPoint(points, namePoint2);
                                                    CPoint p3 = getCPoint(points, namePoint3);

                                                    // Center (creating it auxiliarily).
                                                    String poname = "O_" + name; // FIXME: check if this name already exists
                                                    CPoint po = this.CreateANewPoint(0, 0, poname);
                                                    Constraint cs = new Constraint(Constraint.CIRCUMCENTER, po, p1, p2, p3);
                                                    this.addConstraintToList(cs);
                                                    this.addPointToList(po);
                                                    points.add(po);
                                                    pointsGgb.add(new GgbPoint(poname));

                                                    Circle c = new Circle(po, p1, p2, p3);
                                                    circles.add(c);
                                                    circlesGgb.add(new GgbCircle(name));
                                                    addCircleToList(c);
                                                    c.m_name = name;
                                                    this.charsetAndAddPoly(false);
                                                    this.UndoAdded(c.getDescription());
                                                }

                                            } else if (step.getAttribute("name").equals("Prove")) {
                                                handleGGBProve(step, points, segmentsGgb, lines, circles, exprs);
                                            } else if (step.getAttribute("name").equals("Point")) {
                                                System.out.println("Command 'Point' should be handled automatically");
                                            } else if (step.getAttribute("name").equals("AreParallel")) {
                                                int condtype = CST.getClu_D("Parallel");
                                                Cons c = new Cons(condtype);
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = outputName.getNamedItem("a0").getTextContent();
                                                String nameLine1 = inputName.getNamedItem("a0").getTextContent();
                                                String nameLine2 = inputName.getNamedItem("a1").getTextContent();
                                                setConclusionParameters2Lines(points, lines, c, nameLine1, nameLine2);
                                                c.set_conc(true);
                                                exprs.put(name, c);
                                            } else if (step.getAttribute("name").equals("ArePerpendicular")) {
                                                int condtype = CST.getClu_D("Perpendicular");
                                                Cons c = new Cons(condtype);
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = outputName.getNamedItem("a0").getTextContent();
                                                String nameLine1 = inputName.getNamedItem("a0").getTextContent();
                                                String nameLine2 = inputName.getNamedItem("a1").getTextContent();
                                                setConclusionParameters2Lines(points, lines, c, nameLine1, nameLine2);
                                                c.set_conc(true);
                                                exprs.put(name, c);
                                            } else if (step.getAttribute("name").equals("AreCollinear")) {
                                                int condtype = CST.getClu_D("Collinear");
                                                Cons c = new Cons(condtype);
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = outputName.getNamedItem("a0").getTextContent();
                                                String namePoint1 = inputName.getNamedItem("a0").getTextContent();
                                                String namePoint2 = inputName.getNamedItem("a1").getTextContent();
                                                String namePoint3 = inputName.getNamedItem("a2").getTextContent();
                                                setConclusionParameters3Points(points, c, namePoint1, namePoint2, namePoint3);
                                                c.set_conc(true);
                                                exprs.put(name, c);
                                            } else if (step.getAttribute("name").equals("AreConcyclic")) {
                                                int condtype = CST.getClu_D("Cyclic");
                                                Cons c = new Cons(condtype);
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = outputName.getNamedItem("a0").getTextContent();
                                                String namePoint1 = inputName.getNamedItem("a0").getTextContent();
                                                String namePoint2 = inputName.getNamedItem("a1").getTextContent();
                                                String namePoint3 = inputName.getNamedItem("a2").getTextContent();
                                                String namePoint4 = inputName.getNamedItem("a3").getTextContent();
                                                setConclusionParameters4Points(points, c, namePoint1, namePoint2, namePoint3, namePoint4);
                                                c.set_conc(true);
                                                exprs.put(name, c);
                                            } else if (step.getAttribute("name").equals("AreCongruent")) {
                                                int condtype = CST.getClu_D("Equal Distance");
                                                Cons c = new Cons(condtype);
                                                NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
                                                NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
                                                String name = outputName.getNamedItem("a0").getTextContent();
                                                String nameLine1 = inputName.getNamedItem("a0").getTextContent();
                                                String nameLine2 = inputName.getNamedItem("a1").getTextContent();
                                                setConclusionParameters2Segments(points, segmentsGgb, c, nameLine1, nameLine2);
                                                c.set_conc(true);
                                                GExpert.conclusion = c; // working around that some data may be missing here
                                                exprs.put(name, c);
                                            } else {
                                                System.out.println("Unsupported command: " + step.getAttribute("name"));
                                            }
                                            break;
                                        case "expression":
                                            String label = step.getAttribute("label");
                                            String expr = step.getAttribute("exp");
                                            // getConclusion() covers a bigger number of cases,
                                            // but it should be safe to use it in general:
                                            Cons c = getConclusion(expr, points, segmentsGgb, lines, circles);
                                            exprs.put(label, c);
                                            break;
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ggbFile.close();
        CurrentAction = MOVE;

        setSavedTag();
        return true;
    }

    /**
     * Get conclusion (and eventually set it globally if a workaround is required).
     *
     * @param parameter input string, e.g. AreCollinear(A, B, C)
     * @param points
     * @param lines
     * @param circles
     * @return the conclusion in JGEX format
     */
    Cons getConclusion(String parameter, ArrayList<CPoint> points,
                       ArrayList<GgbSegment> segmentsGgb,
                       ArrayList<CLine> lines, ArrayList<Circle> circles) {
        Cons c = null;
        // input (String): "AreCollinear[F, G, H]"
        // output (Cons): SHOW: COLLINEAR F G H
        if (parameter.startsWith("AreCollinear")) {
            int condtype = CST.getClu_D("Collinear");
            c = new Cons(condtype);
            String[] parameterList = getParameterList(parameter);
            setConclusionParameters3Points(points, c, parameterList[0], parameterList[1],
                    parameterList[2]);
            c.set_conc(true);
        } else if (parameter.startsWith("AreConcyclic")) {
            int condtype = CST.getClu_D("Cyclic");
            c = new Cons(condtype);
            String[] parameterList = getParameterList(parameter);
            setConclusionParameters4Points(points, c, parameterList[0], parameterList[1],
                    parameterList[2], parameterList[3]);
            c.set_conc(true);
        } else if (parameter.startsWith("AreParallel")) {
            int condtype = CST.getClu_D("Parallel");
            c = new Cons(condtype);
            String[] parameterList = getParameterList(parameter);
            setConclusionParameters2Lines(points, lines, c, parameterList[0], parameterList[1]);
            c.set_conc(true);
        } else if (parameter.startsWith("ArePerpendicular")) {
            int condtype = CST.getClu_D("Perpendicular");
            c = new Cons(condtype);
            String[] parameterList = getParameterList(parameter);
            setConclusionParameters2Lines(points, lines, c, parameterList[0], parameterList[1]);
            c.set_conc(true);
        } else if (parameter.startsWith("AreCongruent")) {
            int condtype = CST.getClu_D("Equal Distance");
            c = new Cons(condtype);
            String[] parameterList = getParameterList(parameter);
            setConclusionParameters2Lines(points, lines, c, parameterList[0], parameterList[1]);
            c.set_conc(true);
            GExpert.conclusion = c; // working around that some data may be missing here:
        } else if (parameter.startsWith("AreEqual")) {
            String[] parameterList = getParameterList(parameter);
            int condtype = -1; // dummy init
            // P ≟ Q
            CPoint p1 = getCPoint(points, parameterList[0]);
            CPoint p2 = getCPoint(points, parameterList[1]);
            if (p1 != null && p2 != null) {
                // P = Q is equivalent to PQ = QQ
                condtype = CST.getClu_D("Equal Distance");
                c = new Cons(condtype);
                c.add_pt(p1, 0);
                c.add_pt(p2, 1);
                c.add_pt(p2, 2);
                c.add_pt(p2, 3);
                c.set_conc(true);
                GExpert.conclusion = c; // working around that some data may be missing here:
            }
        } else if (parameter.contains("∥")) {
            int condtype = CST.getClu_D("Parallel");
            String parameter1 = parameter.substring(0, parameter.indexOf("∥")).trim();
            String parameter2 = parameter.substring(parameter.indexOf("∥") + 1).trim();
            c = new Cons(condtype);
            setConclusionParameters2Lines(points, lines, c, parameter1, parameter2);
            c.set_conc(true);
        } else if (parameter.contains("≟")) {
            int condtype = -1; // dummy init
            String parameter1 = parameter.substring(0, parameter.indexOf("≟")).trim();
            String parameter2 = parameter.substring(parameter.indexOf("≟") + 1).trim();
            CPoint p1 = null;
            CPoint p2 = null;
            CPoint p3 = null;
            CPoint p4 = null;
            GgbSegment s1 = getGgbSegment(segmentsGgb, parameter1);
            GgbSegment s2 = getGgbSegment(segmentsGgb, parameter2);
            if (s1 != null && s2 != null) { // compare two segments
                p1 = getCPoint(points, s1.getNameP1());
                p2 = getCPoint(points, s1.getNameP2());
                p3 = getCPoint(points, s2.getNameP1());
                p4 = getCPoint(points, s2.getNameP2());
                condtype = CST.getClu_D("Equal Distance");
            } else {
                // P ≟ Q
                p1 = getCPoint(points, parameter1);
                p2 = getCPoint(points, parameter2);
                if (p1 != null && p2 != null) {
                    p3 = p2;
                    p4 = p2;
                    // P = Q is equivalent to PQ = QQ
                    condtype = CST.getClu_D("Equal Distance");
                }
            }
            if (condtype != -1) {
                c = new Cons(condtype);
                c.add_pt(p1, 0);
                c.add_pt(p2, 1);
                c.add_pt(p3, 2);
                c.add_pt(p4, 3);
                c.set_conc(true);
                GExpert.conclusion = c; // working around that some data may be missing here:
                // We add the fully working conclusion later, when the proof is initiated.
            } else {
                // To implement:
                // Segment[D, F] / Segment[F, A] ≟ 1 / 2

                if (s1 == null && s2 != null) { // s1 = (2 * f) or (1.5 * f), s2 = a
                    Pattern p = Pattern.compile("\\((.*) \\* (.*)\\)");
                    Matcher m = p.matcher(parameter1);
                    if (m.find()) {
                        String segment = m.group(2);
                        int[] fraction = new int[2];
                        try {
                            double multiplier = Double.parseDouble(m.group(1));
                            fraction = GetFraction(multiplier);
                        } catch (Exception e) {
                            p = Pattern.compile("\\((.*) \\/ (.*) \\* .*\\)"); // (3 / 2 * f)
                            m = p.matcher(parameter1);
                            if (m.find()) {
                                fraction[0] = Integer.parseInt(m.group(1));
                                fraction[1] = Integer.parseInt(m.group(2));
                            }
                        }
                        condtype = CST.getClu_D("Ratio");
                        c = new Cons(condtype);
                        s1 = getGgbSegment(segmentsGgb, segment);
                        p1 = getCPoint(points, s1.getNameP1());
                        p2 = getCPoint(points, s1.getNameP2());
                        p3 = getCPoint(points, s2.getNameP1());
                        p4 = getCPoint(points, s2.getNameP2());
                        c.add_pt(p1, 0);
                        c.add_pt(p2, 1);
                        c.add_pt(p3, 2);
                        c.add_pt(p4, 3);
                        c.add_pt(fraction[1], 4);
                        c.add_pt(fraction[0], 5);
                        c.set_conc(true);
                        GExpert.conclusion = c;
                    }
                } else {
                    if (s1 == null && s2 == null) { // k / l ≟ m / n (that is, k * n == m * l)
                        Pattern p = Pattern.compile("(.*) \\/ (.*)");
                        Matcher m = p.matcher(parameter1);
                        if (m.find()) {
                            String segment1 = m.group(1);
                            String segment2 = m.group(2);
                            condtype = CST.getClu_D("Equal Product");
                            c = new Cons(condtype);
                            s1 = getGgbSegment(segmentsGgb, segment1);
                            s2 = getGgbSegment(segmentsGgb, segment2);
                            p1 = getCPoint(points, s1.getNameP1());
                            p2 = getCPoint(points, s1.getNameP2());
                            p3 = getCPoint(points, s2.getNameP1());
                            p4 = getCPoint(points, s2.getNameP2());
                            m = p.matcher(parameter2);
                            if (m.find()) {
                                String segment3 = m.group(1);
                                String segment4 = m.group(2);
                                GgbSegment s3 = getGgbSegment(segmentsGgb, segment3);
                                GgbSegment s4 = getGgbSegment(segmentsGgb, segment4);
                                CPoint p5 = getCPoint(points, s3.getNameP1());
                                CPoint p6 = getCPoint(points, s3.getNameP2());
                                CPoint p7 = getCPoint(points, s4.getNameP1());
                                CPoint p8 = getCPoint(points, s4.getNameP2());
                                c.add_pt(p1, 0);
                                c.add_pt(p2, 1);
                                c.add_pt(p7, 2);
                                c.add_pt(p8, 3);
                                c.add_pt(p5, 4);
                                c.add_pt(p6, 5);
                                c.add_pt(p3, 6);
                                c.add_pt(p4, 7);
                                c.set_conc(true);
                                GExpert.conclusion = c;
                            }
                        } else { // (h * i) ≟ (j * k)
                            p = Pattern.compile("\\((.*) \\* (.*)\\)");
                            m = p.matcher(parameter1);
                            if (m.find()) {
                                String segment1 = m.group(1);
                                String segment2 = m.group(2);
                                condtype = CST.getClu_D("Equal Product");
                                c = new Cons(condtype);
                                s1 = getGgbSegment(segmentsGgb, segment1);
                                s2 = getGgbSegment(segmentsGgb, segment2);
                                p1 = getCPoint(points, s1.getNameP1());
                                p2 = getCPoint(points, s1.getNameP2());
                                p3 = getCPoint(points, s2.getNameP1());
                                p4 = getCPoint(points, s2.getNameP2());
                                m = p.matcher(parameter2);
                                if (m.find()) {
                                    String segment3 = m.group(1);
                                    String segment4 = m.group(2);
                                    GgbSegment s3 = getGgbSegment(segmentsGgb, segment3);
                                    GgbSegment s4 = getGgbSegment(segmentsGgb, segment4);
                                    CPoint p5 = getCPoint(points, s3.getNameP1());
                                    CPoint p6 = getCPoint(points, s3.getNameP2());
                                    CPoint p7 = getCPoint(points, s4.getNameP1());
                                    CPoint p8 = getCPoint(points, s4.getNameP2());
                                    c.add_pt(p1, 0);
                                    c.add_pt(p2, 1);
                                    c.add_pt(p3, 2);
                                    c.add_pt(p4, 3);
                                    c.add_pt(p5, 4);
                                    c.add_pt(p6, 5);
                                    c.add_pt(p7, 6);
                                    c.add_pt(p8, 7);
                                    c.set_conc(true);
                                    GExpert.conclusion = c;
                                }
                            }
                        }

                    } else {
                        System.err.println("Unidentified objects in " + parameter);
                    }
                }
            }
        } else if (parameter.contains("∈")) {
            int condtype = -1; // dummy init
            String parameterPoint = parameter.substring(0, parameter.indexOf("∈")).trim();
            String parameterRest = parameter.substring(parameter.indexOf("∈") + 1).trim();
            CPoint p2 = null;
            CPoint p3 = null;
            CPoint p4 = null;
            CPoint p1 = getCPoint(points, parameterPoint);
            for (CLine l : lines) {
                if (l.getname().equals(parameterRest)) {
                    p2 = l.getfirstPoint();
                    p3 = l.getSecondPoint(p2);
                    condtype = CST.getClu_D("Collinear");
                }
            }
            for (Circle ci : circles) {
                if (ci.getname().equals(parameterRest)) {
                    if (ci.points.size() < 3) {
                        p2 = ci.o;
                        p3 = ci.getP(0);
                        p4 = ci.o;
                        condtype = CST.getClu_D("Equal distance");
                    } else { // we assume that there are at least 3 points
                        p2 = ci.getP(0);
                        p3 = ci.getP(1);
                        p4 = ci.getP(2);
                        condtype = CST.getClu_D("Cyclic");
                    }
                }
            }
            if (condtype != -1) {
                c = new Cons(condtype);
                c.add_pt(p1, 0);
                c.add_pt(p2, 1);
                c.add_pt(p3, 2);
                c.add_pt(p4, 3);
                c.set_conc(true);
            } else {
                System.err.println("Unidentified object: " + parameterRest);
            }
        } else if (parameter.contains("⊥")) {
            String parameter1 = parameter.substring(0, parameter.indexOf("⊥")).trim();
            String parameter2 = parameter.substring(parameter.indexOf("⊥") + 1).trim();
            int condtype = CST.getClu_D("Perpendicular");
            c = new Cons(condtype);
            setConclusionParameters2Lines(points, lines, c, parameter1, parameter2);
            c.set_conc(true);
        } else {
            // To implement:
            // AreConcurrent[d, e, f]
            // h
            System.out.println("Unimplemented: " + parameter);
        }
        return c;
    }

    /**
     * Handles the GGB prove process by setting the conclusion based on the input element and existing expressions.
     *
     * @param step        the XML element representing the prove step
     * @param points      the list of points
     * @param segmentsGgb the list of GGB segments
     * @param lines       the list of lines
     * @param circles     the list of circles
     * @param exprs       the map of constraint expressions
     */
    void handleGGBProve(Element step, ArrayList<CPoint> points,
                        ArrayList<GgbSegment> segmentsGgb,
                        ArrayList<CLine> lines, ArrayList<Circle> circles, HashMap<String, Cons> exprs) {
        GExpert.conclusion = null; // reinitalize
        NamedNodeMap outputName = step.getElementsByTagName("output").item(0).getAttributes();
        NamedNodeMap inputName = step.getElementsByTagName("input").item(0).getAttributes();
        if (inputName.getLength() == 1) {
            Cons c = null;
            String parameter = inputName.getNamedItem("a0").getTextContent();
            if (exprs.containsKey(parameter)) {
                c = exprs.get(parameter);
            } else {
                c = getConclusion(parameter, points, segmentsGgb, lines, circles);
            }
            gxInstance.getpprove().set_conclusion(c, true);
        }
    }

    /**
     * Extracts the parameter list from a string representation.
     *
     * @param parameter the string containing parameter list enclosed in brackets
     * @return an array of trimmed parameter strings
     */
    String[] getParameterList(String parameter) {
        // We assume that the list begins and ends with "[" and "]".
        String parameterItems = parameter.substring(parameter.indexOf("["));
        // Remove "]":
        parameterItems = parameterItems.substring(1, parameterItems.length() - 1);
        String[] parameterList = parameterItems.split(",");
        int parameterLength = parameterList.length;
        for (int k = 0; k < parameterLength; k++) {
            parameterList[k] = parameterList[k].trim();
        }
        return parameterList;
    }

    /**
     * Retrieves a point from the list by its name.
     *
     * @param points        the list of points
     * @param parameterItem the name of the point to retrieve
     * @return the corresponding CPoint, or null if not found
     */
    CPoint getCPoint(ArrayList<CPoint> points, String parameterItem) {
        for (CPoint p : points) {
            if (p.getname().equals(parameterItem)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Retrieves a GGB segment from the list by its name.
     *
     * @param segmentsGgb   the list of GGB segments
     * @param parameterItem the name of the segment to retrieve
     * @return the corresponding GgbSegment, or null if not found
     */
    GgbSegment getGgbSegment(ArrayList<GgbSegment> segmentsGgb, String parameterItem) {
        for (GgbSegment segment : segmentsGgb) { // Find segment
            if (segment.getName().equals(parameterItem)) {
                return segment;
            }
        }
        return null;
    }

    /**
     * Detects a segment based on the provided parameter string.
     * Supports both the "Distance[A, B]" format and concatenated point names.
     *
     * @param points        the list of points
     * @param parameterItem the string representing the segment
     * @return an array containing the two CPoint objects that form the segment, or null if not found
     */
    CPoint[] detectSegment(ArrayList<CPoint> points, String parameterItem) {
        // Format Distance[A, B]
        if (parameterItem.startsWith("Distance")) {
            String[] parameterItems = getParameterList(parameterItem);
            if (parameterItems.length == 2) {
                CPoint p1 = getCPoint(points, parameterItems[0]);
                CPoint p2 = getCPoint(points, parameterItems[1]);
                CPoint p[] = {p1, p2};
                return p;
            }
            return null; // invalid syntax
        }
        // Format AB
        for (CPoint p1 : points) {
            for (CPoint p2 : points) {
                if ((p1.getname() + p2.getname()).equals(parameterItem)) {
                    CPoint p[] = {p1, p2};
                    return p;
                }
            }
        }
        return null; // not found
    }

    /**
     * Retrieves a line from the list by its name.
     * Supports both a direct name lookup and a lookup based on concatenated point names.
     *
     * @param points        the list of points
     * @param lines         the list of lines
     * @param parameterItem the name or concatenated point names representing the line
     * @return the corresponding CLine, or null if not identified
     */
    CLine getCLine(ArrayList<CPoint> points, ArrayList<CLine> lines, String parameterItem) {
        // Format l
        for (CLine l : lines) {
            if (l.getname().equals(parameterItem)) {
                return l;
            }
        }
        // Format AB
        for (CPoint p1 : points) {
            for (CPoint p2 : points) {
                if ((p1.getname() + p2.getname()).equals(parameterItem)) {
                    for (CLine l : lines) {
                        if (l.getfirstPoint() == p1 && l.getSecondPoint(p1) == p2 ||
                                l.getfirstPoint() == p2 && l.getSecondPoint(p2) == p1)
                            return l;
                    }
                }
            }
        }
        return null; // unidentified
    }

    /**
     * Retrieves a circle from the list by its name.
     *
     * @param circles       the list of circles
     * @param parameterItem the name of the circle to retrieve
     * @return the corresponding Circle, or null if not found
     */
    Circle getCircle(ArrayList<Circle> circles, String parameterItem) {
        for (Circle c : circles) {
            if (c.getname().equals(parameterItem)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Sets the conclusion parameters for a relation between two lines.
     *
     * @param points    the list of points
     * @param lines     the list of lines
     * @param c         the constraint to which the points will be added
     * @param nameLine1 the name of the first line
     * @param nameLine2 the name of the second line
     */
    void setConclusionParameters2Lines(ArrayList<CPoint> points, ArrayList<CLine> lines, Cons c, String nameLine1,
                                       String nameLine2) {
        CLine l1 = getCLine(points, lines, nameLine1);
        CLine l2 = getCLine(points, lines, nameLine2);
        CPoint p1;
        CPoint p2;
        CPoint p3;
        CPoint p4;

        p1 = l1.getfirstPoint();
        p2 = l1.getSecondPoint(p1);
        p3 = l2.getfirstPoint();
        p4 = l2.getSecondPoint(p3);

        c.add_pt(p1, 0);
        c.add_pt(p2, 1);
        c.add_pt(p3, 2);
        c.add_pt(p4, 3);
    }

    /**
     * Sets the conclusion parameters for a relation between two segments.
     *
     * @param points      the list of points
     * @param ggbSegments the list of GGB segments
     * @param c           the constraint to which the points will be added
     * @param nameLine1   the name of the first segment (or its points)
     * @param nameLine2   the name of the second segment (or its points)
     */
    void setConclusionParameters2Segments(ArrayList<CPoint> points, ArrayList<GgbSegment> ggbSegments, Cons c, String nameLine1,
                                          String nameLine2) {
        CPoint p1;
        CPoint p2;
        CPoint p3;
        CPoint p4;

        GgbSegment s1 = getGgbSegment(ggbSegments, nameLine1);
        if (s1 != null) {
            p1 = getCPoint(points, s1.getNameP1());
            p2 = getCPoint(points, s1.getNameP2());
        } else {
            CPoint[] p = detectSegment(points, nameLine1);
            p1 = p[0];
            p2 = p[1];
        }

        GgbSegment s2 = getGgbSegment(ggbSegments, nameLine2);
        if (s2 != null) {
            p3 = getCPoint(points, s2.getNameP1());
            p4 = getCPoint(points, s2.getNameP2());
        } else {
            CPoint[] p = detectSegment(points, nameLine1);
            p3 = p[0];
            p4 = p[1];
        }

        c.add_pt(p1, 0);
        c.add_pt(p2, 1);
        c.add_pt(p3, 2);
        c.add_pt(p4, 3);
    }

    /**
     * Sets the conclusion parameters for a relation defined by three points.
     *
     * @param points     the list of points
     * @param c          the constraint to which the points will be added
     * @param namePoint1 the name of the first point
     * @param namePoint2 the name of the second point
     * @param namePoint3 the name of the third point
     */
    void setConclusionParameters3Points(ArrayList<CPoint> points, Cons c, String namePoint1,
                                        String namePoint2, String namePoint3) {

        CPoint p1 = getCPoint(points, namePoint1);
        CPoint p2 = getCPoint(points, namePoint2);
        CPoint p3 = getCPoint(points, namePoint3);
        c.add_pt(p1, 0);
        c.add_pt(p2, 1);
        c.add_pt(p3, 2);
    }

    /**
     * Sets the conclusion parameters for a relation defined by four points.
     *
     * @param points     the list of points
     * @param c          the constraint to which the points will be added
     * @param namePoint1 the name of the first point
     * @param namePoint2 the name of the second point
     * @param namePoint3 the name of the third point
     * @param namePoint4 the name of the fourth point
     */
    void setConclusionParameters4Points(ArrayList<CPoint> points, Cons c, String namePoint1,
                                        String namePoint2, String namePoint3, String namePoint4) {

        CPoint p1 = getCPoint(points, namePoint1);
        CPoint p2 = getCPoint(points, namePoint2);
        CPoint p3 = getCPoint(points, namePoint3);
        CPoint p4 = getCPoint(points, namePoint4);
        c.add_pt(p1, 0);
        c.add_pt(p2, 1);
        c.add_pt(p3, 2);
        c.add_pt(p4, 3);
    }

    // taken from https://stackoverflow.com/a/1657688/1044586
    public static int[] GetFraction(double input) {
        int p0 = 1;
        int q0 = 0;
        int p1 = (int) Math.floor(input);
        int q1 = 1;
        int p2;
        int q2;

        double r = input - p1;
        double next_cf;
        while (true) {
            r = 1.0 / r;
            next_cf = Math.floor(r);
            p2 = (int) (next_cf * p1 + p0);
            q2 = (int) (next_cf * q1 + q0);

            // Limit the numerator and denominator to be 256 or less
            if (p2 > 256 || q2 > 256)
                break;

            // remember the last two fractions
            p0 = p1;
            p1 = p2;
            q0 = q1;
            q1 = q2;

            r -= next_cf;
        }

        input = (double) p1 / q1;
        // hard upper and lower bounds for ratio
        if (input > 256.0) {
            p1 = 256;
            q1 = 1;
        } else if (input < 1.0 / 256.0) {
            p1 = 1;
            q1 = 256;
        }
        return new int[]{p1, q1};
    }
}
