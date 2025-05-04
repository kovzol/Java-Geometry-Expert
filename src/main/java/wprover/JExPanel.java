package wprover;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.URL;

/**
 * JExPanel is a custom JPanel that provides a drawing area with animation capabilities.
 * It allows for mouse interactions and supports animation through a timer.
 */
class JExPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ComponentListener {
    DrawProcess dp;
    static ImageIcon icoa;
    static ImageIcon icos;
    JToggleButton button;
    Timer timer = null;

    /**
     * Paints the component. This method is called whenever the component needs to be repainted.
     *
     * @param g the Graphics context in which to paint
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (dp != null)
            dp.paintPoint(g);
    }

    /**
     * Constructs a new JExPanel. Initializes the panel with a null layout, adds mouse listeners,
     * sets the background color, and initializes the toggle button for animation control.
     */
    public JExPanel() {
        super();
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
        this.setBackground(new Color(228, 236, 252));
        URL imageURL = GExpert.class.getResource("images/animate_start.gif");
        ImageIcon image = new ImageIcon(imageURL);
        URL imageURL1 = GExpert.class.getResource("images/animate_stop.gif");
        ImageIcon image1 = new ImageIcon(imageURL1);

        button = new JToggleButton(image) {
            /**
             * Returns the preferred size of the button.
             *
             * @return the preferred size of the button
             */
            public Dimension getPreferredSize() {
                return new Dimension(30, 28);
            }
        };
        this.add(button);
        button.addActionListener(this);
        Dimension dm = this.getSize();
        button.setBounds(new Rectangle(0, (int) dm.getHeight() - 28, 32, 28));
        button.setSelectedIcon(image1);
    }

    /**
     * Returns the preferred size of the panel.
     *
     * @return the preferred size of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }

    /**
     * Sets the DrawProcess for this panel and starts the animation if available.
     *
     * @param dp the DrawProcess to set
     */
    public void setdrawP(DrawProcess dp) {
        this.dp = dp;
        if (timer != null) timer.stop();
        if (dp.animate != null) {
            AnimateC ant = dp.animate;
            if (timer == null)
                timer = new Timer(ant.getInitValue(), this);
            else
                timer.setDelay(ant.getInitValue());
            timer.start();
            button.setIcon(icos);
            button.setVisible(true);
        } else
            button.setVisible(false);
    }

    /**
     * Handles action events for the button and timer.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            if (!timer.isRunning()) {
                dp.animationStart();
                button.setIcon(icos);
                timer.start();
            } else {
                dp.animationStop();
                button.setIcon(icoa);
                timer.stop();
            }
        } else if (e.getSource() == timer) {
            dp.animationOntime();
            this.repaint();
        }
    }

    /**
     * Handles mouse click events.
     *
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1)
            dp.DWMouseDbClick(e.getX(), e.getY());
        if (e.getButton() == MouseEvent.BUTTON3)
            dp.DWMouseRightClick(e.getX(), e.getY());
    }

    /**
     * Handles mouse press events.
     *
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) {
        dp.DWButtonDown(e.getX(), e.getY());
        repaint();
    }

    /**
     * Handles mouse release events.
     *
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) {
        dp.DWButtonUp(e.getX(), e.getY());
        repaint();
    }

    /**
     * Handles mouse enter events.
     *
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles mouse exit events.
     *
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Handles mouse drag events.
     *
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        dp.DWMouseDrag(e.getX(), e.getY());
        this.repaint();
    }

    /**
     * Handles mouse move events.
     *
     * @param e the mouse event
     */
    public void mouseMoved(MouseEvent e) {
        dp.DWMouseMove(e.getX(), e.getY());
        this.repaint();
    }

    /**
     * Paints the component. This method is called whenever the component needs to be repainted.
     *
     * @param g the Graphics context in which to paint
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dp.paintPoint(g);
    }

    /**
     * Handles component resize events.
     *
     * @param e the component event
     */
    public void componentResized(ComponentEvent e) {
        Dimension dm = this.getSize();
        button.setBounds(new Rectangle(0, (int) dm.getHeight() - 28, 32, 28));
    }

    /**
     * Handles component move events.
     *
     * @param e the component event
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * Handles component show events.
     *
     * @param e the component event
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Handles component hide events.
     *
     * @param e the component event
     */
    public void componentHidden(ComponentEvent e) {
        timer.stop();
    }
}
