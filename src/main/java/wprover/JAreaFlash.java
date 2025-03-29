package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * JAreaFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a flashing area effect on a JPanel.
 */
public class JAreaFlash extends JFlash implements ActionListener {
    private Vector vlist = new Vector();
    private int color = DrawData.LIGHTCOLOR;

    /**
     * Constructs a new JAreaFlash with the specified JPanel and color index.
     *
     * @param p      the JPanel to associate with this JAreaFlash
     * @param cindex the color index to use for the flashing area
     */
    public JAreaFlash(JPanel p, int cindex) {
        super(p);

        color = DrawData.LIGHTCOLOR + cindex-1;
        timer = new Timer(TIME_INTERVAL, this);
    }

    /**
     * Draws the flashing area on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        if (n % 2 != 0) return true;

        int n = vlist.size();
        Composite ac = g2.getComposite();
        g2.setComposite(CMisc.getFillComposite());
        if (n == 0) return true;

        int[] x = new int[n];
        int[] y = new int[n];
        for (int i = 0; i < n; i++) {
            CPoint p1 = (CPoint) vlist.get(i);
            x[i] = (int) p1.getx();
            y[i] = (int) p1.gety();
        }
        g2.setColor(Color.black);
        g2.drawPolygon(x, y, n);
        g2.setColor(DrawData.getColor(color));
        g2.fillPolygon(x, y, n);
        g2.setComposite(ac);
        return true;
    }

    /**
     * Adds a point to the list of points defining the flashing area.
     *
     * @param p the point to add
     */
    public void addAPoint(CPoint p) {
        if (p != null) vlist.add(p);
    }

    /**
     * Handles action events for the timer.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        n--;
        if (n <= 0) super.stop();
        panel.repaint();
    }
}
