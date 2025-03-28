package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * ColorButtonPanel is a JPanel that displays a color button. When the button is clicked,
 * it opens a color menu to allow the user to select a color.
 */
public class ColorButtonPanel extends JPanel implements MouseListener {

        private ColorMenu cm = new ColorMenu("color");

    /**
     * Constructs a ColorButtonPanel with specified dimensions.
     *
     * @param x the width of the panel
     * @param y the height of the panel
     */
        public ColorButtonPanel(int x, int y)
        {
            this.setBorder(BorderFactory.createLineBorder(Color.black, 1));

            Dimension dm = new Dimension(x, y);

            this.setPreferredSize(dm);
            this.setMinimumSize(dm);
            this.setMaximumSize(dm);
            this.addMouseListener(this);
        }

    /**
     * Returns the ColorMenu associated with this panel.
     *
     * @return the ColorMenu
     */
        public ColorMenu getColorMenu()
        {
            return cm;
        }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e the event to be processed
     */
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();
            cm.colorPane = null;
            cm.setColor(this.getBackground());
            cm.show(this, x, y);
        }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
        public void mousePressed(MouseEvent e)
        {

        }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
        public void mouseReleased(MouseEvent e)
        {

        }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
        public void mouseEntered(MouseEvent e)
        {

        }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
        public void mouseExited(MouseEvent e)
        {

        }

    /**
     * Sets the background color of the panel to the selected color from the ColorMenu.
     *
     * @return the new color if the color pane is not null, otherwise null
     */
        public Color setNewColor()
        {
            if (cm.colorPane != null)
            {
                this.setBackground(cm.getColor());
                return cm.getColor();
            }
            return null;
        }
}
