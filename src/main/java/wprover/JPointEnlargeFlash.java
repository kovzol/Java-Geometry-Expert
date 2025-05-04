package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * JPointEnlargeFlash is a class that extends JFlash and implements ActionListener.
 * It is used to create a flashing effect on a point in a JPanel.
 */
public class JPointEnlargeFlash extends JFlash implements ActionListener {

    private CPoint pt;
    private static final int LENG = 8;
    private static final Color color = Color.white;
    private int nnn = 0;
//    private int mradius = 0;


    /**
     * Constructs a new JPointEnlargeFlash with the specified JPanel and CPoint.
     *
     * @param p  the JPanel to associate with this JPointEnlargeFlash
     * @param pt the CPoint to apply the flashing effect on
     */
    public JPointEnlargeFlash(JPanel p, CPoint pt) {
        super(p);
        this.pt = pt;
        timer = new Timer(TIME_INTERVAL, this);
        nnn = pt.m_radius;
    }

    /**
     * Draws the flashing effect on the specified Graphics2D context.
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
        n--;
        if (n <= 0) stop();
        if (n % 2 != 0) {
            pt.m_radius = (LENG);
        } else {
            pt.m_radius = (nnn);
        }

        panel.repaint();
    }

    /**
     * Stops the flashing effect and resets the point's radius.
     */
    public void stop() {
        pt.m_radius = (nnn);
        super.stop();
    }

}
