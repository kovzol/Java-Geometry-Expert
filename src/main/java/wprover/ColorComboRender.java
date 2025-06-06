package wprover;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2005-3-2
 * Time: 14:01:51
 * To change this template use File | Settings | File Templates.
 */
class ColorComboRender extends JPanel
        implements ListCellRenderer, TableCellRenderer {
    int type;  //0 color, 1,line width,2 line type
    int width;
    int height;


    int index = 0;
    boolean select = false;

    /**
     * Constructs a ColorComboRender with specified type and dimensions.
     *
     * @param type the type of the renderer (0 for color, 1 for line width, 2 for line type)
     * @param w    the width of the renderer
     * @param h    the height of the renderer
     */
    public ColorComboRender(int type, int w, int h) {
        setOpaque(true);
        this.type = type;

        width = w;
        height = h;
    }

    /**
     * Returns the component used for drawing the cell in the list.
     *
     * @param list the JList we're painting
     * @param value the value returned by list.getModel().getElementAt(index)
     * @param index the cell's index
     * @param isSelected true if the specified cell was selected
     * @param cellHasFocus true if the specified cell has the focus
     * @return the component that the renderer uses to draw the value
     */
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        //this.type = 0;

        int selectedIndex;
        if (value != null)
            selectedIndex = ((Integer) value).intValue();
        else
            selectedIndex = 0;

        if (isSelected)
            select = true;
        else
            select = false;
        this.index = selectedIndex;
        return this;
    }
    /**
     * Paints the component.
     *
     * @param g the Graphics object to protect
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (select)
            g2.setBackground(Color.lightGray);
        else
            g2.setBackground(Color.white);
        g2.clearRect(0, 0, width, height);
        width = this.getWidth();

        int w = (height) / 4;
        int gap = (height) / 4;
        if (type == 0) {
            if (index < DrawData.getColorCounter() && index >= 0) {
                g2.setColor(DrawData.getColor(index));
                g2.fillRect(gap, gap, width - gap * 4, gap + w);
                g2.setColor(Color.black);
                g2.drawRect(gap, gap, width - gap * 4, gap + w);
            } else if (index == DrawData.getColorCounter()) {
                int w2 = height / 2 - gap;
                gap *= 2;
                g2.setColor(Color.PINK);
                g2.fillRect(gap, w2, gap, gap);
                g2.setColor(Color.green);
                g2.fillRect(gap * 2, w2, gap, gap);
                g2.setColor(Color.orange);
                g2.fillRect(gap * 3, w2, gap, gap);
                g2.setColor(Color.magenta);
                g2.fillRect(gap * 4, w2, gap, gap);
                g2.setColor(Color.BLACK);
                g2.drawRect(gap - 1, w2, gap * 4, gap);
                g2.drawString("Other ...", gap * 5 + 10, height * 3 / 4);
            }
        } else if (type == 1) {
            float ww = (float) DrawData.getWidth(index);
            g2.setStroke(new BasicStroke(ww));
            g2.setColor(Color.black);
            g2.drawLine(5, height / 2, (width - 40), height / 2);
            g2.drawString("" + ww, width - 35, height / 2 + 4);

        } else if (type == 2) {
            float ds = 0;
            if (index <= 0) {
                g2.setStroke(new BasicStroke(1.5f));
            } else {
                ds = (float) DrawData.getDash(index);
                float dash[] = {ds};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
            }

            g2.setColor(Color.black);
            g2.drawLine(5, height / 2, (width - 40), height / 2);
            if (index <= 0)
                g2.drawString("solid", width - 35, height / 2 + 4);
            else
                g2.drawString("" + ds, width - 35, height / 2 + 4);
        }

        if (select) {
            g2.setStroke(new BasicStroke(1.0f));
            g2.setColor(new Color(128, 128, 255));
            g2.drawRect(1, 1, width - 2, height - 2);
        }

    }

    /**
     * Returns the component used for drawing the cell in the table.
     *
     * @param table the JTable we're painting
     * @param value the value returned by table.getValueAt(row, column)
     * @param isSelected true if the specified cell was selected
     * @param hasFocus true if the specified cell has the focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the component that the renderer uses to draw the value
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        select = isSelected;
        return this;
    }
}