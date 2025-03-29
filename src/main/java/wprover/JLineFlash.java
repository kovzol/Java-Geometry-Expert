package wprover;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * A class that represents a flashing line effect.
 */
public class JLineFlash extends JFlash implements ActionListener {
    /**
     * A list of lines to be flashed.
     */
    private Vector vlist = new Vector();

    /**
     * A flag indicating whether to alternate the flashing effect.
     */
    private boolean alter = false;

    /**
     * An index used for alternating the flashing effect.
     */
    private int an = 0;

    /**
     * Constructs a new JLineFlash with the specified JPanel.
     *
     * @param p the JPanel to associate with this JLineFlash
     */
    public JLineFlash(JPanel p) {
        super(p);
        timer = new Timer(TIME_INTERVAL, this);
    }

    /**
     * Adds a new line to the list of lines to be flashed.
     *
     * @return the index of the newly added line
     */
    public int addALine() {
        JLine ln = new JLine();
        vlist.add(ln);
        return vlist.size() - 1;
    }

    /**
     * Sets a line to be drawn infinitely.
     *
     * @param n the index of the line to set as infinite
     */
    public void setInfinitLine(int n) {
        JLine ln = (JLine) vlist.get(n);
        ln.setDrawInfinite(true);
    }

    /**
     * Adds a point to a line at the specified index.
     *
     * @param index the index of the line to add the point to
     * @param p the point to add
     */
    public void addAPoint(int index, CPoint p) {
        JLine ln = (JLine) vlist.get(index);

        if (p != null && ln != null) {
            ln.addAPoint(p);
        }
    }

    /**
     * Sets the alternate flashing mode.
     *
     * @param a true to enable alternate flashing mode, false otherwise
     */
    public void setAlternate(boolean a) {
        if (a) {
            alter = true;
            an = 0;
        }
    }

    /**
     * Draws the flashing lines on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        int cindex = DrawData.RED;

        if (alter == false && n % 2 == 0) {
            g2.setColor(color);
            g2.setStroke(BStroke2);
            for (int i = 0; i < vlist.size(); i++) {
                JLine ln = (JLine) vlist.get(i);
                ln.drawLine(g2);
            }

            for (int i = 0; i < vlist.size(); i++) {
                JLine ln = (JLine) vlist.get(i);
                ln.fillPt(g2);
                g2.setColor(color);
                ln.drawPt(g2);
            }
        } else if (alter) {
            g2.setColor(color);
            g2.setStroke(BStroke2);
            if (an < vlist.size()) {
                g2.setColor(DrawData.getColor(cindex++));
                JLine ln = (JLine) vlist.get(an);
                ln.drawLine(g2);
            }
        }
        return true;
    }

    /**
     * Handles action events for the timer.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        n--;

        if (alter == false) {
            if (n <= 0) {
                super.stop();
            } else {
                for (int i = 0; i < vlist.size(); i++) {
                    JLine ln = (JLine) vlist.get(i);
                    if (n % 2 == 0) {
                        ln.setInFlashMode(true);
                    } else {
                        ln.setInFlashMode(false);
                    }
                }
            }
        } else {
            if (n <= 0) {
                an++;
                n = 8;
            }
            if (an >= vlist.size()) {
                timer.stop();
            } else {
                JLine ln = (JLine) vlist.get(an);
                if (n % 2 == 0) {
                    ln.setInFlashMode(true);
                } else {
                    ln.setInFlashMode(false);
                }
            }
        }

        panel.repaint();
    }

    /**
     * Stops the flashing effect.
     */
    public void stop() {
        super.stop();
        for (int i = 0; i < vlist.size(); i++) {
            JLine ln = (JLine) vlist.get(i);
            ln.setInFlashMode(false);
        }
    }
}
