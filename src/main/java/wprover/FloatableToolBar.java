package wprover;

import UI.EntityButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FloatableToolBar is a custom popup menu that can be dragged around the screen.
 * It contains a close button and allows the user to move it by dragging.
 * It is used in the GExpert application to provide a floating toolbar for various tools.
 * The toolbar can be shown at the bottom of the screen and can be closed by clicking the close button.
 */
public class FloatableToolBar extends JPopupMenu {
    int x, y;
    JPanel mpanel;
    JToggleButton bquit;

    /**
     * Returns the maximum size of the toolbar.
     *
     * @return the maximum size of the toolbar
     */
    public Dimension getMaximumSize() {
        Dimension dm = super.getMaximumSize();
        dm.setSize(Integer.MAX_VALUE, super.getPreferredSize().getHeight());
        return dm;
    }

    /**
     * Shows the toolbar at the bottom of the specified component.
     *
     * @param invoker the component to show the toolbar on
     */
    public void show(Component invoker) {
        int h = invoker.getHeight();
        int w = invoker.getWidth();
        super.show(invoker, 0, h - (int) this.getPreferredSize().getHeight() - 2);
    }

    /**
     * Handles changes in menu selection.
     *
     * @param isIncluded whether the menu is included in the selection
     */
    public void menuSelectionChanged(boolean isIncluded) {
    }

    /**
     * Constructs a FloatableToolBar.
     */
    public FloatableToolBar() {
        this.setBorder(null);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JToggleButton button = new JToggleButton(GExpert.createImageIcon("images/quit.gif")) {
            /**
             * Returns the preferred size of the button.
             *
             * @return the preferred size of the button
             */
            public Dimension getPreferredSize() {
                return super.getMinimumSize();
            }

            /**
             * Returns the maximum size of the button.
             *
             * @return the maximum size of the button
             */
            public Dimension getMaximumSize() {
                return super.getMinimumSize();
            }
        };

        button.setBorder(null);
        button.setActionCommand("Close");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(GExpert.getLanguage("Close"));
        button.addActionListener(new ActionListener() {
            /**
             * Hides the toolbar when the button is clicked.
             *
             * @param e the action event
             */
            public void actionPerformed(ActionEvent e) {
                FloatableToolBar.this.setVisible(false);
                JToggleButton b = (JToggleButton) e.getSource();
                b.setSelected(false);
            }
        });

        bquit = button;
        button.setUI(new EntityButtonUI());

        mpanel = new JPanel();
        mpanel.setBorder(null);
        mpanel.add(button);
        mpanel.setBackground(new Color(200, 200, 235));
        this.add(mpanel);

        mpanel.addMouseMotionListener(new MouseMotionListener() {
            /**
             * Moves the toolbar when the mouse is dragged.
             *
             * @param e the mouse event
             */
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - x;
                int dy = e.getY() - y;
                movetoDxy(dx, dy);
            }

            public void mouseMoved(MouseEvent e) {
            }
        });

        mpanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            /**
             * Records the initial mouse position when the mouse is pressed.
             *
             * @param e the mouse event
             */
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Moves the toolbar by the specified delta values.
     *
     * @param xx the delta x value
     * @param yy the delta y value
     */
    public void movetoDxy(int xx, int yy) {
        int dx = xx;
        int dy = yy;
        Point pt = FloatableToolBar.this.getLocationOnScreen();
        Component comp = FloatableToolBar.this.getInvoker();
        int tx = (int) pt.getX() + dx;
        int ty = (int) pt.getY() + dy;
        if (comp == null) return;
        Point pt1 = comp.getLocationOnScreen();
        int w = comp.getWidth();
        int h = comp.getHeight();
        Dimension dm = FloatableToolBar.this.getSize();

        int wt = (int) dm.getWidth();
        int ht = (int) dm.getHeight();
        int x, y;
        x = y = 0;
        if (tx < pt1.getX())
            x = (int) pt1.getX();
        else if (tx + wt > pt1.getX() + w)
            x = (int) pt1.getX() + w - wt;
        else
            x = tx;
        if (ty < pt1.getY())
            y = (int) pt1.getY();
        else if (ty + ht > pt1.getY() + h)
            y = (int) pt1.getY() + h - ht;
        else
            y = ty;
        FloatableToolBar.this.setLocation(x, y);
    }
}
