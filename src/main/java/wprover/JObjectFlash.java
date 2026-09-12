package wprover;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * A class that represents a flashing effect for graphical objects.
 */
public class JObjectFlash extends JFlash implements ActionListener {
    private static final int MAXFLASHTIMES = 12;
    private final java.util.List<Object> vlist;
    private int count = 0;

    /**
     * Constructs a new JObjectFlash with the specified JPanel.
     *
     * @param p the JPanel to associate with this JObjectFlash
     */
    public JObjectFlash(JPanel p) {
        super(p);
        panel = p;
        vlist = new ArrayList<>();
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
        setListInFlashing(vlist, count % 2 != 0);
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
     * @param inFlash true to enable flashing mode, false to disable
     */
    private void setListInFlashing(java.util.List<Object> v, boolean inFlash) {
        for (Object o : v) {
            CClass cc = (CClass) o;
            cc.setInFlashing(inFlash);
        }
    }

    /**
     * Stops the flashing effect for all objects in the list.
     *
     * @param v the list of objects
     */
    private void stopListFlash(java.util.List<Object> v) {
        for (Object o : v) {
            CClass cc = (CClass) o;
            cc.stopFlash();
        }
    }

    /**
     * Sets the panel and list of objects to be flashed.
     *
     * @param p    the JPanel to associate with this JObjectFlash
     * @param list the list of objects to be flashed
     */
    public void setAt(JPanel p, java.util.List<Object> list) {
        panel = p;
        stopListFlash(vlist);
        vlist.clear();
        for (Object o : list) {
            CClass c = (CClass) o;
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
        if (vlist.isEmpty()) {
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