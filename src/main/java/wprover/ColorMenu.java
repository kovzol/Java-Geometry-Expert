package wprover;

import javax.swing.*;
import javax.swing.border.*;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

class ColorMenu extends JPopupMenu
{
    protected Border unselectedBorder;

    protected Border selectedBorder;

    protected Border activeBorder;

    protected Hashtable paneTable;

    protected ColorPane colorPane;

    public ColorMenu(String name)
    {
        super(name);
        unselectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                getBackground()), new BevelBorder(BevelBorder.LOWERED,
                Color.white, Color.gray));
        selectedBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                Color.red), new MatteBorder(1, 1, 1, 1, getBackground()));
        activeBorder = new CompoundBorder(new MatteBorder(1, 1, 1, 1,
                Color.blue), new MatteBorder(1, 1, 1, 1, getBackground()));

        JPanel p = new JPanel();
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        p.setLayout(new GridLayout(8, 8));
        paneTable = new Hashtable();

        int[] values = new int[]{0, 128, 192, 255};

        for (int r = 0; r < values.length; r++)
        {
            for (int g = 0; g < values.length; g++)
            {
                for (int b = 0; b < values.length; b++)
                {
                    Color c = new Color(values[r], values[g], values[b]);
                    ColorPane pn = new ColorPane(c);
                    p.add(pn);
                    paneTable.put(c, pn);
                }
            }
        }
        add(p);
    }
    /**
     * Sets the selected color in the ColorMenu.
     *
     * @param c the color to be selected
     */
    public void setColor(Color c)
    {
        Object obj = paneTable.get(c);
        if (obj == null)
            return;
        if (colorPane != null)
            colorPane.setSelected(false);
        colorPane = (ColorPane) obj;
        colorPane.setSelected(true);
    }

/**
 * Gets the currently selected color in the ColorMenu.
 *
 * @return the selected color, or null if no color is selected
 */
    public Color getColor()
    {
        if (colorPane == null)
            return null;
        return colorPane.getColor();
    }

    /**
     * Performs the selection action. This method is intended to be overridden.
     */
    public void doSelection()
    {
    }

    /**
     * Hides the ColorMenu.
     */
    public void HideMenu()
    {
        this.setVisible(false);
    }

    class ColorPane extends JPanel implements MouseListener
    {
        protected Color color;

        protected boolean isSelected;

/**
         * Constructs a ColorPane with the specified color.
         * Sets the background color, border, tooltip text, and registers a MouseListener.
         *
         * @param c the color of the pane
         */
        public ColorPane(Color c)
        {
            color = c;
            setBackground(c);
            setBorder(unselectedBorder);
            String msg = "R " + c.getRed() + ", G " + c.getGreen() + ", B " + c.getBlue();
            setToolTipText(msg);
            addMouseListener(this);
        }

        /**
         * Returns the color of the pane.
         *
         * @return the color of the pane
         */
        public Color getColor()
        {
            return color;
        }

        /**
         * Returns the preferred size of the pane.
         *
         * @return the preferred size of the pane
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(25, 25);
        }

        /**
         * Returns the maximum size of the pane.
         *
         * @return the maximum size of the pane
         */
        public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        /**
         * Returns the minimum size of the pane.
         *
         * @return the minimum size of the pane
         */
        public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        /**
         * Sets the selection state of the pane.
         * Updates the border based on the selection state.
         *
         * @param selected the selection state to set
         */
        public void setSelected(boolean selected)
        {
            isSelected = selected;
            if (isSelected)
                setBorder(selectedBorder);
            else
                setBorder(unselectedBorder);
        }

        /**
         * Returns whether the pane is selected.
         *
         * @return true if the pane is selected, false otherwise
         */
        public boolean isSelected()
        {
            return isSelected;
        }

        /**
         * Invoked when a mouse button has been pressed on the pane.
         *
         * @param e the MouseEvent triggered by the press
         */
        public void mousePressed(MouseEvent e)
        {
        }

        /**
         * Invoked when the mouse has been clicked on the pane.
         *
         * @param e the MouseEvent triggered by the click
         */
        public void mouseClicked(MouseEvent e)
        {
        }

        /**
         * Invoked when a mouse button has been released on the pane.
         * Sets the color, clears the selected path, performs the selection action, and hides the menu.
         *
         * @param e the MouseEvent triggered by the release
         */
        public void mouseReleased(MouseEvent e)
        {
            setColor(color);
            MenuSelectionManager.defaultManager().clearSelectedPath();
            doSelection();
            HideMenu();
        }

        /**
         * Invoked when the mouse enters the pane.
         * Sets the border to the active border.
         *
         * @param e the MouseEvent triggered when entering the pane
         */
        public void mouseEntered(MouseEvent e)
        {
            setBorder(activeBorder);
        }

        /**
         * Invoked when the mouse exits the pane.
         * Sets the border based on the selection state.
         *
         * @param e the MouseEvent triggered when exiting the pane
         */
        public void mouseExited(MouseEvent e)
        {
            setBorder(isSelected ? selectedBorder : unselectedBorder);
        }
    }
}