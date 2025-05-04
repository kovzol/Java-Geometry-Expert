package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * JSegmentMovingFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a moving flash effect on a segment defined by four points.
 */
public class JSegmentMovingFlash extends JFlash implements ActionListener {

    private CPoint p1, p2, p3, p4;
    private int c1, c2;
    private int nd, n, nstep;
    private double da = 0;
    private double xc, yc, dxc, dyc;


    /**
     * Constructs a new JSegmentMovingFlash with the specified parameters.
     *
     * @param p  the JPanel to associate with this JSegmentMovingFlash
     * @param p1 the first CPoint of the segment
     * @param p2 the second CPoint of the segment
     * @param p3 the third CPoint of the segment
     * @param p4 the fourth CPoint of the segment
     * @param c1 the first color index for the flashing effect
     * @param c2 the second color index for the flashing effect
     */
    public JSegmentMovingFlash(JPanel p, CPoint p1, CPoint p2, CPoint p3, CPoint p4, int c1, int c2) {
        super(p);

        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.c1 = c1;
        this.c2 = c2;
        init();
        nd = -3;
        nstep = -3;
        timer = new Timer(TIME_INTERVAL, this);
        super.vType = true;
    }

    /**
     * Initializes the parameters for the moving flash effect.
     */
    private void init() {
        double r = CMisc.getMoveStep();
        double x1 = (p1.getx() + p2.getx()) / 2;
        double x2 = (p3.getx() + p4.getx()) / 2;
        double y1 = (p1.gety() + p2.gety()) / 2;
        double y2 = (p3.gety() + p4.gety()) / 2;

        int n1 = (int) (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) / r);
        int m1 = n1;

        boolean rt = false;
        if (p1 == p3 || p2 == p3) {
            xc = p3.getx();
            yc = p3.gety();
            dxc = dyc = 0;
            rt = true;
            if (p2 == p3) {
                CPoint pt = p2;
                p2 = p1;
                p1 = pt;
            }
        } else if (p1 == p4 || p2 == p4) {
            xc = p4.getx();
            yc = p4.gety();
            dxc = dyc = 0;
            rt = true;
            if (p1 == p4) {
                CPoint pt = p2;
                p2 = p1;
                p1 = pt;
            }
        } else {
            xc = x1;
            yc = y1;
            dxc = (x2 - x1);
            dyc = (y2 - y1);
        }

        n = m1;
        double xt2 = p2.getx() + p3.getx() - p1.getx();
        double yt2 = p2.gety() + p3.gety() - p1.gety();
        double r2 = Math.PI + CAngle.get3pAngle(xt2, yt2, p3.getx(), p3.gety(), p4.getx(), p4.gety());
        if (r2 >= Math.PI)
            r2 = r2 - 2 * Math.PI;
        if (r2 <= -Math.PI)
            r2 = r2 + 2 * Math.PI;

        if (p1 == p3 && p2 == p4 || p1 == p4 && p2 == p3)
            r2 = 0;
        else if (Math.abs(r2) < CMisc.ZERO && rt)
            r2 = Math.PI;

        int m2 = (int) (r2 / ASTEP);
        n = Math.max(m1, m2);
        if (n < MIN_STEP)
            n = MIN_STEP;
        da = r2 / n;
    }

    /**
     * Draws the moving flash effect on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        double r = (double) (n - nd) / n;
        Color c = getRatioColor(c1, c2, r);
        g2.setStroke(JFlash.BStroke2);
        g2.setColor(c);

        if (nd >= 0 && nd != n) {
            double dxt = (dxc) * nd / n;
            double dyt = (dyc) * nd / n;
            double tx1 = p1.getx() - xc;
            double ty1 = p1.gety() - yc;
            double tx2 = p2.getx() - xc;
            double ty2 = p2.gety() - yc;

            double a = nd * da;
            double sin = Math.sin(a);
            double cos = Math.cos(a);
            double x1 = tx1 * cos - ty1 * sin + xc + dxt;
            double y1 = tx1 * sin + ty1 * cos + yc + dyt;

            double x2 = tx2 * cos - ty2 * sin + xc + dxt;
            double y2 = tx2 * sin + ty2 * cos + yc + dyt;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        } else if (nd < 0) {
            g2.drawLine((int) p1.getx(), (int) p1.gety(), (int) p2.getx(), (int) p2.gety());
        } else if (nd == n && nstep <= 0) {
            g2.drawLine((int) p3.getx(), (int) p3.gety(), (int) p4.getx(), (int) p4.gety());
        }
        return true;
    }

    /**
     * Handles the action event for the timer, updating the animation state and repainting the panel.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        nd++;
        if (nd >= n) {
            nd = n;
            if (nstep == 0)
                stop();
            else
                nstep++;
        }
        panel.repaint();
    }


}
