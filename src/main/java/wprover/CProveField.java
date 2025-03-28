package wprover;

import gprover.Cond;

import java.util.Vector;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileOutputStream;

/**
 * CProveField is a class that represents a field for proving mathematical theorems.
 * It handles the display and manipulation of proof steps and conditions.
 */
public class CProveField {
    private boolean HEAD = false;
    CProveText cpname;
    Vector clist;
    Vector vlist;

    CProveText pselect;
    CProveText pundo = null;
    CProveText pex = null;

    Point pt;
    int rstep = -1;
    int rmid = 0;

    /**
     * Default constructor for `CProveField`.
     * Initializes the point and vectors for proof steps and conditions.
     */
    public CProveField() {
        pt = new Point(20, 20);
        clist = new Vector();
        vlist = new Vector();
    }

    /**
     * Sets the x and y coordinates of the point.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setXY(int x, int y) {
        pt.setLocation(x, y);
    }

    /**
     * Drags the selected proof text by the specified delta values.
     *
     * @param dx the change in x-coordinate
     * @param dy the change in y-coordinate
     */
    public void drag(double dx, double dy) {
        if (pselect == null) return;
        pt.setLocation((int) (pt.getX() + dx), (int) (pt.getY() + dy));
    }

    /**
     * Regenerates the index for the proof steps.
     * Sets the visibility and index of each proof step based on the `HEAD` flag.
     */
    public void reGenerateIndex() {

        if (HEAD) {
            this.pselect = null;
            if (vlist.size() == 0) return;

            CProveText cp = (CProveText) vlist.get(0);
            cp.setVisible(true);
            int index = 0;

            for (int i = 1; i < vlist.size(); i++) {
                CProveText cp1 = (CProveText) vlist.get(i);
                if (cp1.getVisible())
                    cp1.setIndex(index++);
            }
        } else {
            int index = 0;
            if (vlist.size() == 0) return;

            for (int i = 0; i < vlist.size(); i++) {
                CProveText cp1 = (CProveText) vlist.get(i);
                if (cp1.getVisible())
                    cp1.setIndex(index++);
            }
        }

    }

    /**
     * Constructor for `CProveField` with a vector of conditions and a head flag.
     * Initializes the point, vectors, and proof text based on the head flag.
     *
     * @param v the vector of conditions
     * @param head the head flag indicating the type of proof field
     */
    public CProveField(Vector v, boolean head) {
        pt = new Point(20, 20);
        clist = new Vector();
        vlist = new Vector();
        HEAD = head;
        if (head) {
            cpname = new CProveText("", "theorem");
            cpname.setFont(new Font("Dialog", Font.PLAIN, 18));
            cpname.setMessageColor(Color.black);
            pselect = null;
        }

        CProveText ct = null;
        int size = v.size();
        if (size == 0) return;
        if (head) {
            ct = new CProveText((Cond) v.get(size - 1), GExpert.getLanguage("To Prove:") + " ");
            vlist.add(ct);
            for (int i = 0; i < size; i++) {
                Cond co = (Cond) v.get(i);
                ct = new CProveText(v, co, i, false);
                vlist.add(ct);
            }
        } else {
            for (int i = 0; i < size; i++) {
                Cond co = (Cond) v.get(i);
                ct = new CProveText(v, co, -1, false);
                vlist.add(ct);
            }
            if (size == 1)
                ct.setMessage("Since " + ct.getMessage());
        }


        this.expandAll();

    }

    /**
     * Constructor for `CProveField` with a vector of undo structures.
     * Initializes the point and vectors for proof steps and conditions.
     *
     * @param ulist the vector of undo structures
     */
    public CProveField(Vector ulist) {
        pt = new Point(20, 20);

        clist = new Vector();
        vlist = new Vector();
        for (int i = 0; i < ulist.size(); i++) {
            UndoStruct u = (UndoStruct) ulist.get(i);
            CProveText ct = new CProveText(u, i);
            vlist.add(ct);
        }
    }

    /**
     * Expands all proof steps and conditions.
     * Sets the visibility of each proof text to expanded.
     */
    public void expandAll() {

        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);
            cp.expand();
        }
        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            cp.expand();
        }


    }

    /**
     * Draws the proof steps on the specified `Graphics2D` context.
     *
     * @param g2 the `Graphics2D` context to draw on
     */
    public void draw(Graphics2D g2) {
        Point p = new Point((int) pt.getX(), (int) pt.getY());
        draw(g2, p);
    }

    /**
     * Sets the font size for all proof texts.
     *
     * @param size the font size to set
     */
    public void setFontSize(int size) {
        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);
            cp.setFontSize(size);
        }
        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            cp.setFontSize(size);
        }
        this.pselect = null;
    }

    /**
     * Gets the currently selected proof text.
     *
     * @return the selected `CProveText` object
     */
    public CProveText getSelect() {
        return pselect;
    }

    /**
     * Undoes the proof steps to the head.
     *
     * @param dp the `DrawProcess` context
     * @return true if the undo operation was successful
     */
    public boolean undo_to_head(DrawProcess dp) {
        if (HEAD) {
            if (vlist.size() == 0) return false;

            pselect = (CProveText) clist.get(0);
            pundo = pselect;

            int index = vlist.size() - 1;
            if (index < 0) return false;

            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_to_head(dp);
            }
            index = clist.size() - 1;
            if (index < 0) return false;
            for (int i = index; i >= 1; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_to_head(dp);
            }

            dp.setUndoStructForDisPlay(pundo.getUndoStruct(), true);

        } else {
            int index = vlist.size() - 1;
            if (index < 0) return false;

            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_to_head(dp);
            }

            index = clist.size() - 1;
            if (index < 0) return false;
            for (int i = index; i >= 1; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_to_head(dp);
            }
        }
        return true;
    }

    /**
     * Runs the proof steps to the beginning.
     *
     * @param dp the `DrawProcess` context
     * @return true if the operation was successful
     */
    public boolean run_to_begin(DrawProcess dp) {
        if (HEAD) {
            // if (vlist.size() == 0) return false;

            pselect = (CProveText) clist.get(0);
            pundo = pselect;

            int index = vlist.size() - 1;
            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.run_to_begin(dp);
            }

            index = clist.size() - 1;
            if (index < 0) return false;
            for (int i = index; i >= 1; i--) {
                CProveText cpt = (CProveText) clist.get(i);
                cpt.run_to_begin(dp);
            }
            dp.setUndoStructForDisPlay(pselect.getUndoStruct(), true);

        } else {
            int index = vlist.size() - 1;

            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.run_to_begin(dp);
            }
            index = clist.size() - 1;

            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) clist.get(i);
                cpt.run_to_begin(dp);
            }
        }
        return true;
    }

    /**
     * Undoes the default action for the proof steps.
     *
     * @param dp the `DrawProcess` context
     * @return true if the undo operation was successful
     */
    public boolean undo_default(DrawProcess dp) {
        if (HEAD) {
            if (vlist.size() == 0) return false;

            pselect = (CProveText) vlist.get(0);
            pundo = pselect;
            int index = vlist.size() - 1;
            if (index < 0) return false;
            for (int i = index; i >= 1; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_default(dp);
            }
            CProveText cpt = (CProveText) vlist.get(0);

            dp.setUndoStructForDisPlay(cpt.getUndoStruct(), true);

        } else {
            int index = vlist.size() - 1;
            if (index < 0) return false;

            for (int i = index; i >= 0; i--) {
                CProveText cpt = (CProveText) vlist.get(i);
                cpt.undo_default(dp);
            }
        }
        return true;
    }

    /**
     * Runs the proof to the end.
     *
     * @param dp the `DrawProcess` context
     * @return true if the operation was successful
     */
    public boolean run_to_end(DrawProcess dp) {
        while (true) {
            if (!this.next_prove_step(dp)) {
                return true;
            }
        }
    }

    /**
     * Redoes the invisible head step for the proof.
     *
     * @param dp the `DrawProcess` context
     * @return the `CProveText` object that was redone, or null if none was found
     */
    public CProveText redo_invisible_head(DrawProcess dp) {
        if (vlist.size() == 0) return null;
        CProveText ct = (CProveText) vlist.get(0);
        if (ct.getVisible() == false) {
            dp.redo_step(ct.getUndoStruct());
            return ct;
        } else
            return null;
    }

    /**
     * Advances to the next proof step.
     *
     * @param dp the `DrawProcess` context
     * @return true if the operation was successful
     */
    public boolean next(DrawProcess dp) {
        CProveText ct = fd_text(++this.rstep);
        if (ct != null) {
            this.pselect = ct;
            ((DrawTextProcess) dp).addaux(ct);
        } else {
            dp.resetAux();
            this.pselect = null;
            rstep = rmid;
        }

        return true;
    }

    /**
     * Finds the `CProveText` object at the specified index.
     *
     * @param index the index to search for
     * @return the `CProveText` object at the specified index, or null if none was found
     */
    public CProveText fd_text(int index) {
        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);
            CProveText ct = cp.fd_text(index);
            if (ct != null) return ct;
        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            CProveText ct = cp.fd_text(index);
            if (ct != null) return ct;
        }
        return null;
    }

    /**
     * Sets the default step row for the proof steps.
     */
    public void setStepRowDefault() {
        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);
            cp.setStepRowDefault();
        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            cp.setStepRowDefault();
        }
    }

    /**
     * Advances to the next proof step.
     *
     * @param dp the `DrawProcess` context
     * @return true if the operation was successful
     */
    public boolean next_prove_step(DrawProcess dp) {
        if (HEAD) {
            CBoolean find = new CBoolean(false);
            Vector vl = new Vector();

            CProveText ct = next_prove_step(dp, pundo, find);
            if (ct != null) {
                pselect = ct;
                ct.getFlashObjectList(vl, dp);
                pundo = ct.redo_invisible_head(dp);
                if (pselect != pundo)
                    vl.addAll(pundo.getUndoStruct().getAllObjects(dp));
                dp.setObjectListForFlash(vl);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the selected undo step for the proof.
     *
     * @param u the `UndoStruct` object to set
     * @param dp the `DrawProcess` context
     */
    public void setSelectedUndo(UndoStruct u, DrawProcess dp) {
        CProveText ct = pselect = findPText(u);
        Vector vl = new Vector();
        if (ct != null) {

            ct.getFlashObjectList(vl, dp);
            pundo = ct.redo_invisible_head(dp);
            if (pselect != pundo)
                vl.addAll(pundo.getUndoStruct().getAllObjects(dp));
            dp.setObjectListForFlash(vl);
        }
    }

    /**
     * Finds the `CProveText` object for the specified undo structure.
     *
     * @param un the `UndoStruct` object to search for
     * @return the `CProveText` object for the specified undo structure, or null if none was found
     */
    public CProveText findPText(UndoStruct un) {
        if (un == null)
            return null;


        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);
            {
                CProveText k = cp.findPText(un);
                if (k != null)
                    return k;
            }

        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            {
                CProveText k = cp.findPText(un);
                if (k != null)
                    return k;
            }

        }
        return null;
    }

    /**
     * Advances to the next proof step.
     *
     * @param dp the `DrawProcess` context
     * @param cpt the current `CProveText` object
     * @param find a boolean indicating whether the step was found
     * @return the next `CProveText` object, or null if none was found
     */
    public CProveText next_prove_step(DrawProcess dp, CProveText cpt, CBoolean find) {

        for (int i = 0; i < clist.size(); i++) {
            CProveText cp = (CProveText) clist.get(i);

            CProveText t = cp.next_prove_step(dp, cpt, find);
            if (t != null) return t;
        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);

            CProveText t = cp.next_prove_step(dp, cpt, find);
            if (t != null) return t;
        }
        return null;
    }

    /**
     * Draws the proof steps.
     *
     * @param g2 the `Graphics2D` context to draw on
     * @param p the current position to draw at
     */
    public void draw(Graphics2D g2, Point p) {
        int dx = (int) p.getX();
        int dy = (int) p.getY();

        double wd = 0;

        if (HEAD && pselect != null) {
            pselect.draw(g2, true);
        }


        if (HEAD) {
            CProveText.resetRow();
            setStepRowDefault();

            this.drawAStep(cpname, p, g2);
            {
                double tw = cpname.getWidth();
                if (tw > wd)
                    wd = tw;
            }

            p.setLocation(p.getX(), p.getY() + 5);
            //p.setY(p.getY() + 5);

            for (int i = 0; i < clist.size(); i++) {
                CProveText cp = (CProveText) clist.get(i);
                this.drawAStep(cp, p, g2);
                double tw = cp.getWidth();
                if (tw > wd)
                    wd = tw;
            }

            rmid = CProveText.getRow();
            if (rstep < 0) rstep = rmid;

            p.setLocation(dx, p.getY());
            for (int i = 0; i < vlist.size(); i++) {
                CProveText cp = (CProveText) vlist.get(i);
                this.drawAStep(cp, p, g2);
                double tw = cp.getWidth();
                if (tw > wd)
                    wd = tw;
                if (i == 0)
                    p.setLocation(p.getX(), p.getY() + 8);
            }
        }
        // p.setY(p.getY() + 10);

        else {
            for (int i = 0; i < clist.size(); i++) {
                CProveText cp = (CProveText) clist.get(i);
                this.drawAStep(cp, p, g2);
                double tw = cp.getWidth();
                if (tw > wd)
                    wd = tw;
            }

            for (int i = 0; i < vlist.size(); i++) {
                CProveText cp = (CProveText) vlist.get(i);
                this.drawAStep(cp, p, g2);
                double tw = cp.getWidth();
                if (tw > wd)
                    wd = tw;
            }
        }

        wd += 5;
        if (HEAD) {
            cpname.setWidth(wd);
            for (int i = 0; i < clist.size(); i++) {
                CProveText cp = (CProveText) clist.get(i);
                cp.setWidth(wd);
            }
        }
        for (int i = 0; i < vlist.size(); i++) {
            CProveText cp = (CProveText) vlist.get(i);
            cp.setWidth(wd);
        }
    }

    /**
     * Moves the proof steps by the specified x and y coordinates.
     *
     * @param x the x-coordinate to move by
     * @param y the y-coordinate to move by
     */
    public void move(double x, double y) {
        pt.setLocation(pt.getX() + (int) x, pt.getY() + (int) y);
    }

    /**
     * Handles mouse movement events and returns the `CProveText` object at the specified coordinates.
     *
     * @param x the x-coordinate of the mouse
     * @param y the y-coordinate of the mouse
     * @return the `CProveText` object at the specified coordinates, or null if none is found
     */
    public CProveText mouseMove(double x, double y) {
        CProveText fd = null;
        for (int i = 0; i < clist.size(); i++) {
            CProveText ct = (CProveText) clist.get(i);
            CProveText cpt = ct.mouseMove(x, y);
            if (cpt != null)
                fd = cpt;
        }
        for (int i = 0; i < vlist.size(); i++) {
            CProveText ct = (CProveText) vlist.get(i);
            CProveText cpt = ct.mouseMove(x, y);
            if (cpt != null)
                fd = cpt;
        }

        pex = fd;
        return fd;
    }

    /**
     * Selects the `CProveText` object at the specified coordinates.
     *
     * @param x the x-coordinate of the selection
     * @param y the y-coordinate of the selection
     * @param on_select a boolean indicating whether the selection is active
     * @return the selected `CProveText` object, or null if none is found
     */
    public CProveText select(double x, double y, boolean on_select) {
        CProveText sel = null;

        if (HEAD) {
            if (cpname.select(x, y))
                sel = cpname;
        }

        CProveText ts;
        for (int i = 0; i < clist.size(); i++) {
            CProveText ct = (CProveText) clist.get(i);
            if ((ts = ct.selectAll(x, y)) != null)
                sel = ts;


        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText ct = (CProveText) vlist.get(i);
            if ((ts = ct.selectAll(x, y)) != null)
                sel = ts;

        }

        if (HEAD)
            pselect = sel;
        return sel;
    }

    /**
     * Clears the selection of all `CProveText` objects.
     */
    public void clearSelection() {
        for (int i = 0; i < clist.size(); i++) {
            CProveText ct = (CProveText) clist.get(i);
            ct.clearSelection();
        }

        for (int i = 0; i < vlist.size(); i++) {
            CProveText ct = (CProveText) vlist.get(i);
            ct.clearSelection();
        }

    }

    /**
     * Draws a single step of the proof for the specified `CProveText` object.
     *
     * @param cp the `CProveText` object to draw
     * @param p the current position to draw at
     * @param g2 the `Graphics2D` context to draw on
     */
    public void drawAStep(CProveText cp, Point p, Graphics2D g2) {

        if (!cp.getVisible()) return;

        cp.setCurrentPosition(p);
        if (cp == pselect)
            cp.draw(g2, true);

        cp.draw(g2);
        cp.getNextPosition(p);

        if (cp.isExpanded()) {
            int x = (int) p.getX();
            p.setLocation(x + 45, p.getY());
            cp.drawChild(g2, p);
            p.setLocation(x, p.getY());
        }
    }

    /**
     * Saves the proof text as a PostScript file.
     *
     * @param fp the `FileOutputStream` to write to
     * @param stype the type of save operation
     * @throws IOException if an I/O error occurs
     */
    public void SavePS(FileOutputStream fp, int stype) throws IOException {
        if (HEAD) {
            fp.write("%draw proof text\n".getBytes());
            fp.write("-60 -100 translate\n/ystep -8 def   /yoff 0 def  /fzoff 10 def\n ".getBytes());
            cpname.SavePS(fp, stype, 0);
            for (int i = 0; i < clist.size(); i++) {
                CProveText ct = (CProveText) clist.get(i);
                if (i == 0)
                    ct.SavePS(fp, stype, 1);
                else
                    ct.SavePS(fp, stype, 0);
            }

            for (int i = 0; i < vlist.size(); i++) {
                CProveText ct = (CProveText) vlist.get(i);
                if (i == 0)
                    ct.SavePS(fp, stype, 2);
                else
                    ct.SavePS(fp, stype, 0);
            }
        } else {
            for (int i = 0; i < clist.size(); i++) {
                CProveText ct = (CProveText) clist.get(i);
                ct.SavePS(fp, stype, 0);
            }

            for (int i = 0; i < vlist.size(); i++) {
                CProveText ct = (CProveText) vlist.get(i);
                ct.SavePS(fp, stype, 0);
            }
        }


    }

    /**
     * Saves the proof text to a `DataOutputStream`.
     *
     * @param out the `DataOutputStream` to write to
     * @param space the amount of space to use for formatting
     * @return true if the save operation was successful
     * @throws IOException if an I/O error occurs
     */
    public boolean saveText(DataOutputStream out, int space) throws IOException {
        if (HEAD) {
            for (int i = 0; i < vlist.size(); i++) {
                CProveText ct = (CProveText) vlist.get(i);
                ct.saveText(out, space);
            }
            out.close();
        } else {
            for (int i = 0; i < vlist.size(); i++) {
                CProveText ct = (CProveText) vlist.get(i);
                ct.saveText(out, space);
            }
        }
        return true;
    }

    /**
     * Saves the state of the `CProveField` to a `DataOutputStream`.
     *
     * @param out the `DataOutputStream` to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {

        out.writeBoolean(HEAD);
        if (HEAD) cpname.Save(out);

        int size = clist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CProveText ct = (CProveText) clist.get(i);
            ct.Save(out);
        }

        size = vlist.size();
        out.writeInt(size);
        for (int i = 0; i < size; i++) {
            CProveText ct = (CProveText) vlist.get(i);
            ct.Save(out);
        }

        out.writeInt((int) pt.getX());
        out.writeInt((int) pt.getY());

    }

    /**
     * Loads the state of the `CProveField` from a `DataInputStream`.
     *
     * @param in the `DataInputStream` to read from
     * @param dp the `DrawProcess` context
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {

        HEAD = in.readBoolean();
        if (HEAD) {
            cpname = new CProveText();
            cpname.Load(in, dp);
        }

        int size = in.readInt();
        clist = new Vector();
        for (int i = 0; i < size; i++) {
            CProveText ct = new CProveText();
            ct.Load(in, dp);
            clist.add(ct);
        }

        size = in.readInt();
        vlist = new Vector();

        for (int i = 0; i < size; i++) {
            CProveText ct = new CProveText();
            ct.Load(in, dp);
            vlist.add(ct);
        }

        int px = in.readInt();
        int py = in.readInt();
        pt = new Point(px, py);
    }
}
