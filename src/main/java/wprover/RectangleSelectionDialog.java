package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


/**
 * RectangleSelectionDialog is a class that extends JBaseDialog and implements
 * MouseListener, MouseMotionListener, ComponentListener, ActionListener, and Runnable.
 * It is used to create a dialog for selecting a rectangle in a JPanel.
 */
public class RectangleSelectionDialog extends JBaseDialog implements MouseListener, MouseMotionListener,
        ComponentListener, ActionListener, Runnable {

    private int x2, y2, x1, y1;
    private GExpert gxInstance;
    private JPanel contentPane;
    private JComponent content;
    private double dx, dy;
    private boolean result = false;

    /**
     * Returns the result of the dialog.
     *
     * @return true if the OK button was pressed, false otherwise
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Constructs a new RectangleSelectionDialog with the specified GExpert instance.
     *
     * @param gx the GExpert instance to associate with this dialog
     */
    public RectangleSelectionDialog(GExpert gx) {
        super(gx.getFrame(), GExpert.getLanguage("Choose a rectangle"), true);
        JPanel ppp = new JPanel();
        ppp.setLayout(new BoxLayout(ppp, BoxLayout.Y_AXIS));


        gxInstance = gx;
        content = gx.getContent();
        Point pt = content.getLocation();
        dx = pt.getX();
        dy = pt.getY();

        contentPane = new JPanel() {
            public Dimension getPreferredSize() {
                return content.getSize();
            }


            public void paintComponent(Graphics g) {
                super.paintComponent(g);
//                super.paintAll();
                //this.pai
                Graphics2D g2 = (Graphics2D) g;
//                g2.translate(-dx, -dy);
                g2.drawImage(image, 0, 0, null);

//                gxInstance.getContentPane().paintComponents(g2);


                g2.translate(-dx, -dy);

                g2.setStroke(CMisc.DashedStroke1);

                g2.setColor(Color.red);
                g2.drawLine(x1, y1, x1, y2);
                g2.drawLine(x1, y1, x2, y1);
                g2.drawLine(x1, y2, x2, y2);
                g2.drawLine(x2, y1, x2, y2);


            }
        };
        contentPane.addMouseListener(this);
        contentPane.addMouseMotionListener(this);
        ppp.add(contentPane);


        JPanel bpane = new JPanel();
        bpane.setLayout(new BoxLayout(bpane, BoxLayout.X_AXIS));
        bpane.add(Box.createHorizontalGlue());
        JButton bok = new JButton(GExpert.getLanguage("OK"));
        JButton bcancel = new JButton(GExpert.getLanguage("Cancel"));
        bok.addActionListener(this);
        bcancel.addActionListener(this);
        bpane.add(bok);
        bpane.add(bcancel);
        ppp.add(bpane);

        this.getContentPane().add(ppp);


        this.pack();
        x1 = y1 = x2 = y2 = 0;
    }

    BufferedImage image;

    /**
     * Sets the visibility of the dialog and captures the current content as an image.
     *
     * @param r true to make the dialog visible, false to hide it
     */
    public void setVisible(boolean r) {
        Dimension dm = content.getSize();
        image = gxInstance.getBufferedImage2(new Rectangle(0, 0, (int) dm.getWidth(), (int) dm.getHeight()));
        super.setVisible(r);
    }

    /**
     * Handles mouse clicked events.
     *
     * @param e the MouseEvent triggered by clicking the mouse
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Handles mouse pressed events to start drawing the rectangle.
     *
     * @param e the MouseEvent triggered by pressing the mouse
     */
    public void mousePressed(MouseEvent e) {
        x1 = (int) (e.getX() + dx);
        y1 = (int) (e.getY() + dy);
        x2 = x1;
        y2 = y1;
        this.repaint();
    }

    /**
     * Returns the selected rectangle.
     *
     * @return the selected Rectangle
     */
    public Rectangle getRectangle() {
        return new Rectangle(x1 - (int) dx, y1 - (int) dy, x2 - x1, y2 - y1);
    }

    /**
     * Handles mouse released events.
     *
     * @param e the MouseEvent triggered by releasing the mouse
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Handles mouse entered events.
     *
     * @param e the MouseEvent triggered by entering a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles mouse exited events.
     *
     * @param e the MouseEvent triggered by exiting a component
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Handles mouse dragged events to update the rectangle's size.
     *
     * @param e the MouseEvent triggered by dragging the mouse
     */
    public void mouseDragged(MouseEvent e) {
        x2 = (int) (e.getX() + dx);
        y2 = (int) (e.getY() + dy);
        this.repaint();
    }

    /**
     * Handles mouse moved events.
     *
     * @param e the MouseEvent triggered by moving the mouse
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Handles component resized events to update the content location.
     *
     * @param e the ComponentEvent triggered by resizing the component
     */
    public void componentResized(ComponentEvent e) {
        Point pt = content.getLocation();
        dx = pt.getX();
        dy = pt.getY();
    }

    /**
     * Handles component moved events.
     *
     * @param e the ComponentEvent triggered by moving the component
     */
    public void componentMoved(ComponentEvent e) {
    }

    /**
     * Handles component shown events.
     *
     * @param e the ComponentEvent triggered by showing the component
     */
    public void componentShown(ComponentEvent e) {
    }

    /**
     * Handles component hidden events.
     *
     * @param e the ComponentEvent triggered by hiding the component
     */
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * Translates the rectangle by a specified amount.
     *
     * @param n the amount to translate the rectangle
     */
    public void translate(int n) {
        x1 += n;
        y1 += n;
        x2 += n;
        y2 += n;
    }

    /**
     * Handles key pressed events to move the rectangle or confirm the selection.
     *
     * @param e the KeyEvent triggered by pressing a key
     */
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                x1 -= 1;
                x2 -= 1;
                break;
            case KeyEvent.VK_RIGHT:
                x1 += 1;
                x2 += 1;
                break;
            case KeyEvent.VK_UP:
                y1 -= 1;
                y2 -= 1;
                break;
            case KeyEvent.VK_DOWN:
                y1 += 1;
                y2 += 1;
                break;
            case KeyEvent.VK_ENTER:
                result = true;
                setVisible(false);
                break;
            default:
                return;
        }
        this.repaint();
    }

    /**
     * Handles action events for the OK and Cancel buttons.
     *
     * @param e the ActionEvent triggered by the buttons
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equalsIgnoreCase("OK")) {
            result = true;
        } else {
            result = false;
        }
        this.setVisible(false);
    }

    /**
     * Paints the component.
     *
     * @param g the Graphics object to protect
     */
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * Runs the dialog.
     */
    public void run() {
    }
}
