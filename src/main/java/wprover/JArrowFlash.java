package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * JArrowFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a flashing arrow effect on a JPanel.
 */
public class JArrowFlash extends JFlash implements ActionListener {

    private CPoint p1, p2;
    private int type = 0; // PI/6 and PI*5/6.
    private static BasicStroke stroke = new BasicStroke(2.0f);

    /**
     * Constructs a new JArrowFlash with the specified JPanel, points, and type.
     *
     * @param panel the JPanel to associate with this JArrowFlash
     * @param p1    the starting point of the arrow
     * @param p2    the ending point of the arrow
     * @param type  the type of the arrow (0 for PI/6, 1 for PI*5/6)
     */
    public JArrowFlash(JPanel panel, CPoint p1, CPoint p2, int type) {
        super(panel);
        this.p1 = p1;
        this.p2 = p2;
        timer = new Timer(TIME_INTERVAL, this);
        this.type = type;
    }

    /**
     * Draws the flashing arrow on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        double alpha = Math.PI / 6;
        if (type == 1)
            alpha *= 5;

        double x1 = p1.getx();
        double y1 = p1.gety();
        double x2 = p2.getx();
        double y2 = p2.gety();
        double x = x2 - x1;
        double y = y2 - y1;
        if (x == 0 && y == 0) return false;

        double dis = Math.sqrt(x * x + y * y);
        double dx = x / dis;
        double dy = y / dis;

        double sin = Math.sin(alpha);
        double cos = Math.cos(alpha);
        double ddx = dx * 26;
        double ddy = dy * 26;
        double px1 = x1 + ddx * cos - ddy * sin;
        double py1 = y1 + ddx * sin + ddy * cos;
        double px2 = x1 + ddx * cos + ddy * sin;
        double py2 = y1 - ddx * sin + ddy * cos;

        ddx = -ddx;
        ddy = -ddy;
        double qx1 = x2 + ddx * cos - ddy * sin;
        double qy1 = y2 + ddx * sin + ddy * cos;
        double qx2 = x2 + ddx * cos + ddy * sin;
        double qy2 = y2 - ddx * sin + ddy * cos;

        g2.setStroke(stroke);
        g2.drawLine((int) x1, (int) y1, (int) px1, (int) py1);
        g2.drawLine((int) x1, (int) y1, (int) px2, (int) py2);
        g2.drawLine((int) x2, (int) y2, (int) qx1, (int) qy1);
        g2.drawLine((int) x2, (int) y2, (int) qx2, (int) qy2);
        g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        return true;
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
