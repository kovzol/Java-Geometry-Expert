package wprover;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;

/**
 * A class that represents a flashing effect for graphical objects.
 */
public class JObjectFlash extends JFlash implements ActionListener {
    private static int TIMERS = 130;
    private static int MAXFLASHTIMES = 12;
    private Vector vlist;
    private int count = 0;

    /**
     * Constructs a new JObjectFlash with the specified JPanel.
     *
     * @param p the JPanel to associate with this JObjectFlash
     */
    public JObjectFlash(JPanel p) {
        super(p);
        panel = p;
        vlist = new Vector();
        timer = new Timer(TIME_INTERVAL, this);
        vType = true;
    }

    /**
     * Draws the flashing objects on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        return true;
    }

    /**
     * Handles action events for the timer.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (count % 2 == 0)
            setListInFlashing(vlist, false);
        else
            setListInFlashing(vlist, true);
        if (panel != null)
            panel.repaint();
        count++;
        if (count == MAXFLASHTIMES)
            this.stop();
    }

    /**
     * Sets the flashing mode for all objects in the list.
     *
     * @param v       the list of objects
     * @param inflash true to enable flashing mode, false to disable
     */
    private void setListInFlashing(Vector v, boolean inflash) {
        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            cc.setInFlashing(inflash);
        }
    }

    /**
     * Stops the flashing effect for all objects in the list.
     *
     * @param v the list of objects
     */
    private void stopListFlash(Vector v) {
        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            cc.stopFlash();
        }
    }

    /**
     * Sets the panel and list of objects to be flashed.
     *
     * @param p    the JPanel to associate with this JObjectFlash
     * @param list the list of objects to be flashed
     */
    public void setAt(JPanel p, Vector list) {
        panel = p;
        stopListFlash(vlist);
        vlist.clear();
        for (int i = 0; i < list.size(); i++) {
            CClass c = (CClass) list.get(i);
            if (c != null && c.visible())
                vlist.add(c);
        }
    }

    /**
     * Adds an object to the list of objects to be flashed.
     *
     * @param obj the object to add
     */
    public void addFlashObject(CClass obj) {
        if (obj != null && !vlist.contains(obj) && obj.visible())
            vlist.add(obj);
    }

    /**
     * Starts the flashing effect.
     */
    public void start() {
        if (vlist.size() == 0) {
            stop();
            return;
        }

        count = 0;
        setListInFlashing(vlist, true);
        super.start();
    }

    /**
     * Stops the flashing effect.
     */
    public void stop() {
        stopListFlash(vlist);
        super.stop();
    }
}