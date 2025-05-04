package wprover;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.Vector;

/**
 * CStyleDialog is a class that creates a dialog for selecting drawing styles, including color,
 * line width, and line type. It is used in the GExpert application.
 */
public class CStyleDialog extends FloatableToolBar {
    public static int CELLWIDTH = 60;
    int action = -1;

    GExpert gxInstance;
    DPanel d;
    Panel_CS pcs;

    PopComboRender render0, render1, render2;
    DrawStylePanel rColor = null;
    DrawStylePanel rStyle = null;
    DrawStylePanel rWidth = null;

    JPanel pColor, pStyle, pWidth;


    DrawStylePanel pgColor = null;
    PolygonFillPopComboRender pgRender = null;
    JPanel ppgColor = null;


    DrawStylePanel agColor = null;
    PolygonFillPopComboRender agRender = null;
    JPanel pagColor = null;
    JPanel topAgPanel = null;

    /**
     * Constructor for the CStyleDialog class.
     * Initializes the dialog with the given GExpert and DPanel instances.
     *
     * @param gx the GExpert instance
     * @param d the DPanel instance
     */
    public CStyleDialog(GExpert gx, DPanel d) {
        this.gxInstance = gx;
        this.d = d;
        mpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mpanel.add(Box.createHorizontalGlue());
        mpanel.removeAll();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        pcs = new Panel_CS();
        pcs.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
        this.setBorder(BorderFactory.createLineBorder(mpanel.getBackground().darker(), 1));
        this.add(pcs);
    }

    /**
     * Resets the drawing style panels to the current drawing data indices.
     */
    public void reset() {
        rColor.index = DrawData.cindex;
        rStyle.index = DrawData.dindex;
        rWidth.index = DrawData.windex;
        if (pgColor != null)
            pgColor.index = DrawData.polygoncolor;
    }

    /**
     * Sets the action type for the dialog and updates the layout accordingly.
     *
     * @param actionType the action type to set
     *                  -1: ByPass Action
     *                   0: Default
     *                   1: Draw Action + point
     *                   2: Draw Action line + circle
     *                   3: Fill Action
     *                   4: Angle
     *                   5: Move/Select/Intersect
     */
    public void setAction(int actionType) {
        reset();
        if (actionType == action)
            return;

        switch (actionType) {
            case -1:
            case 0:
            case 5:
                this.removeAll();
                this.add(mpanel);
                break;
            case 1:
                this.removeAll();
                this.add(mpanel);
                this.add(pColor);
                break;
            case 2:
                this.removeAll();
                this.add(mpanel);
                this.add(pColor);
                this.add(pStyle);
                this.add(pWidth);
                break;
            case 3:
                this.removeAll();

                if (pgColor == null) {
                    pgColor = new DrawStylePanel(0);
                    pgColor.index = DrawData.polygoncolor;
                    pgRender = new PolygonFillPopComboRender(0);
                    pgColor.selector = pgRender;
                    pgColor.label = new JLabel(GExpert.getLanguage("Color"));
                    ppgColor = new JPanel();
                    ppgColor.add(pgColor.label);
                    ppgColor.add(pgColor);
                }
                this.add(mpanel);
                this.add(ppgColor);
                break;
            case 4:
                this.removeAll();
                this.add(mpanel);

                this.add(pColor);
                this.add(pStyle);
                this.add(pWidth);
                break;
            default:
                break;
        }
        this.pack();
    }

    /**
     * Returns the preferred size of the dialog.
     *
     * @return the preferred size of the dialog
     */
    public Dimension getPreferredSize() {
        Dimension dm = super.getPreferredSize();
        return dm;
    }

    /**
     * This class is used to create a panel for selecting color, line width, and line type.
     * It extends the JPanel class and implements ActionListener to handle action events.
     */
    class Panel_CS extends JPanel implements ActionListener {

        DPanel d;

        public void setColorOnly(boolean r) {
        }

        public Panel_CS() {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            render0 = new GeneralPopComboRender(0);
            render1 = new GeneralPopComboRender(2);
            render2 = new GeneralPopComboRender(1);

            rColor = new DrawStylePanel(0);
            rStyle = new DrawStylePanel(2);
            rWidth = new DrawStylePanel(1);

            pColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lb1 = new JLabel(GExpert.getLanguage("Color"));
            pColor.add(lb1);
            pColor.add(rColor);
            pColor.setBorder(null);
            rColor.label = lb1;
            rColor.selector = render0;

            pStyle = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lb2 = new JLabel(GExpert.getLanguage("Type"));
            pStyle.add(lb2);
            pStyle.add(rStyle);
            pStyle.setBorder(null);
            rStyle.label = lb2;
            rStyle.selector = render1;

            pWidth = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lb3 = new JLabel(GExpert.getLanguage("Width"));
            pWidth.add(lb3);
            pWidth.add(rWidth);
            pWidth.setBorder(null);
            rWidth.label = lb3;
            rWidth.selector = render2;

            this.add(pColor);
            this.add(pStyle);
            this.add(pWidth);
        }

        public void setVariable() {
        }

        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    /**
     * This class is used to handle the rendering of the color, line width, and line type
     * selection. It extends the JPanel class and implements MouseListener to handle mouse events.
     */
    class DrawStylePanel extends JPanel implements MouseListener {
        JLabel label;
        PopComboRender selector;

        int type;  //0 color, 1,line width,2 line type
        int index = 4;
        boolean select = false;

        public DrawStylePanel(int type) {
            setOpaque(true);
            this.setBackground(Color.white);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            this.addMouseListener(this);
            this.type = type;
        }

        public Dimension getPreferredSize() {
            Dimension dm = super.getPreferredSize();
            dm.setSize(CELLWIDTH, label.getHeight());
            return dm;
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int width = this.getWidth();
            int height = this.getHeight();

            g2.setBackground(Color.white);
            g2.clearRect(0, 0, width, height);

            int w = (width) / 6;
            int gap = (height) / 6;
            if (type == 0) {
                if (index < DrawData.getColorCounter() && index >= 0) {
                    g2.setColor(DrawData.getColor(index));
                    g2.fillRect(5, gap + 1, width - 10, height - 2 * gap - 3);
                    g2.setColor(Color.black);
                    g2.drawRect(5, gap + 1, width - 10, height - 2 * gap - 3);
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
                }
            } else if (type == 1) {
                float ww = (float) DrawData.getWidth(index);
                g2.setStroke(new BasicStroke(ww));
                g2.setColor(Color.black);
                g2.drawLine(5, height / 2, (width - 5), height / 2);

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
                g2.drawLine(5, height / 2, (width - 5), height / 2);
            }

            if (select) {
                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(new Color(128, 128, 255));
                g2.drawRect(1, 1, width - 2, height - 2);
            }

        }

        public void mouseClicked(MouseEvent e) {
            JPopupMenu m = new JPopupMenu() {
                public Dimension getPreferredSize() {
                    Dimension dm = super.getPreferredSize();
                    dm.setSize(100, dm.getHeight());
                    return dm;
                }
            };
            m.add(selector);
            m.show(this, 0, this.getHeight());
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            select = true;
            this.repaint();
        }

        public void mouseExited(MouseEvent e) {
            select = false;
            this.repaint();
        }
    }

    /**
     * This class is used to handle the rendering of the color, line width, and line type
     * selection. It extends the JPanel class and implements MouseListener to handle mouse events.
     */
    class PopComboRender extends JPanel implements MouseListener {
        Vector vlist = new Vector();

        public PopComboRender(int type) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            switch (type) {
                case 0:       //Color
                {
                    int n = DrawData.getColorCounter();
                    for (int i = 0; i < n; i++) {
                        PopComboRenderCell r = new PopComboRenderCell(0, 100, 20);
                        r.index = i;
                        r.addMouseListener(this);
                        vlist.add(r);
                        this.add(r);
                    }
                }
                break;
                case 1:       //Width
                {
                    int n = DrawData.getWidthCounter();
                    if (n > 20)
                        n = 20;
                    for (int i = 0; i < n; i++) {
                        PopComboRenderCell r = new PopComboRenderCell(1, 100, 20);
                        r.index = i;
                        r.addMouseListener(this);
                        vlist.add(r);
                        this.add(r);
                    }
                }
                break;
                case 2:       //Style
                {
                    int n = DrawData.getDashCounter();
                    for (int i = 0; i < n; i++) {
                        PopComboRenderCell r = new PopComboRenderCell(2, 100, 20);
                        r.index = i;
                        r.addMouseListener(this);
                        vlist.add(r);
                        this.add(r);
                    }

                }
                break;
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            this.getParent().setVisible(false);
            CStyleDialog.this.repaint();
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * This class is used to handle the rendering of the color, line width, and line type
     * selection. It extends the ColorComboRender class and implements MouseListener to handle
     * mouse events.
     */
    class PopComboRenderCell extends ColorComboRender implements MouseListener {
        public PopComboRenderCell(int type, int w, int h) {
            super(type, w, h);
            this.addMouseListener(this);
        }

        public Dimension getPreferredSize() {
            Dimension dm = super.getPreferredSize();
            dm.setSize(width, height);
            return dm;
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            select = false;
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            select = true;
            this.repaint();
        }

        public void mouseExited(MouseEvent e) {
            select = false;
            this.repaint();
        }
    }

    /**
     * This class is used to handle the rendering of the color, line width, and line type
     * selection. It extends the PopComboRender class and overrides the mousePressed method to
     * set the selected index for the corresponding style.
     */
    class GeneralPopComboRender extends PopComboRender {
        public GeneralPopComboRender(int type) {
            super(type);
        }

        public void mousePressed(MouseEvent e) {
            PopComboRenderCell c = (PopComboRenderCell) e.getSource();
            int t = c.type;
            int idx = c.index;
            switch (t) {
                case 0:
                    DrawData.cindex = idx;
                    rColor.index = idx;
                    break;
                case 2:
                    DrawData.dindex = idx;
                    rStyle.index = idx;
                    break;
                case 1:
                    DrawData.windex = idx;
                    rWidth.index = idx;
                    break;
            }
            super.mousePressed(e);
        }
    }

    /**
     * This class is used to handle the rendering of the polygon fill color selection.
     * It extends the PopComboRender class and overrides the mousePressed method to
     * set the polygon color when a color is selected.
     */
    class PolygonFillPopComboRender extends PopComboRender {
        public PolygonFillPopComboRender(int type) {
            super(type);

        }

        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            PopComboRenderCell c = (PopComboRenderCell) e.getSource();
            int t = c.type;
            int idx = c.index;
            DrawData.polygoncolor = idx;
            if (pgColor != null)
                pgColor.index = idx;
        }
    }
}
