package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * A custom JPanel that handles various mouse and component events.
 * Implements MouseListener, MouseMotionListener, MouseWheelListener, and ComponentListener.
 */
class DPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    /** Constant for draw mode. */
    final public static int DRAW = 0;
    /** Constant for construction mode. */
    final public static int CONS = 1;    // construction.
    DrawTextProcess dp = new DrawTextProcess();
    /** The input type, either DRAW or CONS. */
    private int Input_type = DRAW; // draw  ,1 : prove
    GExpert gxInstance;

    /**
     * Clears all components from the panel.
     */
    public void clearAll() {
        this.removeAll();
    }

    /**
     * Sets the animation step.
     *
     * @param step the step value to set
     */
    public void setStep(double step) {
        dp.animate.Setstep(step);
    }

    /**
     * Checks if an action needs to proceed based on the prover's state.
     *
     * @return true if the action needs to proceed, false otherwise
     */
    public boolean actionNeedProceed() {
        return gxInstance == null || !gxInstance.getpprove().isProverRunning();
    }

    /**
     * Constructor for DPanel.
     *
     * @param gx the GExpert instance
     */
    public DPanel(GExpert gx) {
        gxInstance = gx;

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
        this.setDoubleBuffered(true);
        this.setLayout(null);
        this.addComponentListener(this);
        this.setBackground(CMisc.getBackGroundColor());
    }

    /**
     * Recalculates and repaints the panel.
     */
    public void repaintAndCalculate() {
        dp.reCalculate();
        this.repaint();
    }

    /**
     * Handles animation based on the type.
     *
     * @param type the type of animation (0: on time, 1: start, 2: stop)
     */
    public void onAnimate(int type) {
        if (type == 1) //start
            dp.animationStart();
        else if (type == 2)
            dp.animationStop();
        else if (type == 0)
            dp.animationOntime();

        repaint();
    }

    /**
     * Returns the preferred size of the panel.
     *
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    /**
     * Handles mouse pressed events.
     *
     * @param e the MouseEvent
     */
    public void mousePressed(MouseEvent e) {
        if (!this.actionNeedProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            dp.DWMouseRightDown(e.getX(), e.getY());
            return;
        }
        if (Input_type == 0) {
            if (dp.GetCurrentAction() == DrawProcess.CONSTRUCT_FROM_TEXT) {
                dp.mouseDown(e.getX(), e.getY());
            } else
                dp.DWButtonDown(e.getX(), e.getY());
            repaint();
        }
    }

    /**
     * Handles mouse dragged events.
     *
     * @param e the MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        if (!this.actionNeedProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            return;
        }
        if (Input_type == 0) {
            dp.DWMouseDrag(e.getX(), e.getY());
            repaint();
        }
    }

    /**
     * Handles mouse released events.
     *
     * @param e the MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
        if (!this.actionNeedProceed())
            return;

        int button = e.getButton();
        if (button == MouseEvent.BUTTON3)
            return;

        if (Input_type == 0) {
            dp.DWButtonUp(e.getX(), e.getY());
            repaint();
        }
    }

    /**
     * Handles mouse moved events.
     *
     * @param e the MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        int button = e.getButton();

        if (button == MouseEvent.BUTTON3)
            return;

        if (Input_type == 0) {
            dp.DWMouseMove(e.getX(), e.getY());
        }
    }

    /**
     * Handles mouse clicked events.
     *
     * @param e the MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
        if (!this.actionNeedProceed())
            return;

        if (e.getButton() == MouseEvent.BUTTON3)
            dp.DWMouseRightClick(e.getX(), e.getY());
        else if (e.getClickCount() > 1)
            dp.DWMouseDbClick(e.getX(), e.getY());
    }

    /**
     * Handles mouse exited events.
     *
     * @param e the MouseEvent
     */
    public void mouseExited(MouseEvent e) {
        dp.setMouseInside(false);
        this.repaint();
    }

    /**
     * Handles mouse entered events.
     *
     * @param e the MouseEvent
     */
    public void mouseEntered(MouseEvent e) {
        dp.setMouseInside(true);
        this.repaint();
    }

    /**
     * Handles mouse wheel moved events.
     *
     * @param e the MouseWheelEvent
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        int n = e.getScrollAmount();
        dp.DWMouseWheel(e.getX(), e.getY(), n, e.getWheelRotation());
        this.repaint();
    }

    /**
     * Handles component resized events.
     *
     * @param e the ComponentEvent
     */
    public void componentResized(ComponentEvent e) {
    }

    /**
     * Handles component moved events.
     *
     * @param e the ComponentEvent
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * Handles component shown events.
     *
     * @param e the ComponentEvent
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Handles component hidden events.
     *
     * @param e the ComponentEvent
     */
    public void componentHidden(ComponentEvent e) {

    }

    /**
     * Paints the component.
     *
     * @param g the Graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Dimension dm = this.getSize();
        dp.SetDimension(dm.getWidth(), dm.getHeight());
        dp.paintPoint(g);
    }
}