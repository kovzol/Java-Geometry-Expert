package wprover;

import gprover.*;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.Vector;

import static wprover.GExpert.getLanguage;

/**
 * VcellRender is a custom JPanel that serves as a base class for rendering
 * tree cells in a JTree. It provides a custom painting mechanism and handles
 * selection and background colors.
 */
class VcellRender extends JPanel {
    protected static Border eborder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    protected static Border gborder = BorderFactory.createLineBorder(Color.GRAY, 1);

    protected static DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    boolean selected;
    Color backgroundSelectionColor;
    Color backgroundNonSelectionColor;

    public VcellRender() {
        this.setBorder(eborder);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        selected = false;
        defaultRenderer.setFont(ItemLabel.font1);
        backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        backgroundNonSelectionColor = defaultRenderer.
                getBackgroundNonSelectionColor();
    }


    public void paintComponent(Graphics g) {
        if (selected) {
            g.setColor(backgroundSelectionColor.darker());
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        }
        super.paintComponent(g);
    }
}

/**
 * BookCellRenderer is a custom cell renderer for a tree structure, allowing
 * for the rendering of tree nodes with specific content and behavior.
 */
class BookCellRenderer extends VcellRender implements TreeCellRenderer {
    Vector renderlist = new Vector();
    Vector renderlist1 = new Vector();

    /**
     * Constructs a BookCellRenderer with the specified number of labels.
     * Initializes the renderer and adds labels to the render lists.
     *
     * @param n the number of labels to initialize
     */
    public BookCellRenderer(int n) {
        super();
        for (int i = 1; i <= n; i++) {
            ItemLabel lb = new ItemLabel(true, false);
            renderlist.add(lb);
        }
        for (int i = 1; i <= n; i++) {
            ItemLabel lb = new ItemLabel(true, true);
            renderlist1.add(lb);
        }
    }

    /**
     * Sets the font for all labels in the renderer.
     *
     * @param f the font to set for the labels
     */
    public void setCellFont(Font f) {
        for (int i = 0; i < renderlist.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist.get(i);
            lb.setFont(f);
        }
        Font f1 = new Font(f.getFamily(), f.getStyle(), f.getSize() - 1);
        for (int i = 0; i < renderlist1.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist1.get(i);
            lb.setFont(f1);
        }
    }

    /**
     * Sets the user object and type for a label at the specified index in the primary list.
     *
     * @param index the index of the label to set
     * @param t     the type of the label
     * @param obj   the user object to associate with the label
     */
    public void setLabelObject(int index, int t, Object obj) {
        if (obj == null) return;

        ItemLabel label = (ItemLabel) renderlist.get(index);
        label.setUserObject(t, obj);
        this.add(label);
    }

    /**
     * Sets the user object and type for a label at the specified index in the secondary list.
     *
     * @param index the index of the label to set
     * @param t     the type of the label
     * @param obj   the user object to associate with the label
     */
    public void setLabelObject1(int index, int t, Object obj) {
        ItemLabel label = (ItemLabel) renderlist1.get(index);
        label.setUserObject(t, obj);
        this.add(label);
    }

    /**
     * Returns the tree cell renderer component for the specified tree node.
     *
     * @param tree     the JTree that is asking the renderer to render
     * @param value    the value of the cell to be rendered
     * @param selected whether the cell is selected
     * @param expanded whether the node is expanded
     * @param leaf     whether the node is a leaf node
     * @param row      the row index of the node
     * @param hasFocus whether the node has focus
     * @return the component for rendering the tree cell
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected,
                                                  boolean expanded,
                                                  boolean leaf, int row,
                                                  boolean hasFocus) {
        Component returnValue = null;

        boolean crsp = false;

        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            this.removeAll();
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            VcellRender cell = this;

            if (userObject instanceof GrTerm) {
                GrTerm gr = (GrTerm) userObject;
                Vector v = gr.getAllxterm();
                int index = v.size();
                int k = 0;
                if (index >= 0) {
                    if (row != 1) {
                        setLabelObject(0, 0, " = ");
                    } else {
                        setLabelObject(0, 0, "    ");
                    }
                    k++;
                    for (int i = 0; i < v.size(); i++) {
                        XTerm x = (XTerm) v.get(i);
                        setLabelObject(k++, 1, x);
                    }
                }
                if (gr.getPTN() == 0) {
                    setLabelObject(k++, 2, gr.el);
                }
                if (v.size() == 1) {
                    XTerm x = (XTerm) v.get(0);
                    if (x.getPV() == 0)
                        setLabelObject(k++, 0, "   " + getLanguage("Q.E.D."));
                }
            } else if (userObject instanceof ElTerm) {
                ElTerm el = (ElTerm) userObject;
                Vector v = el.getAllxterm();
                int k = 0;
                if (node.getParent() != null) {
                    setLabelObject1(k++, 0, getLanguage("because"));
                }
                setLabelObject1(k++, 1, v.get(0));
                setLabelObject1(k++, 0, " = ");
                for (int i = 1; i < v.size(); i++) {
                    setLabelObject1(k++, 1, v.get(i));
                }
                if (el.getEType() > 0)
                    setLabelObject1(k++, 2, el);
                if (selected) {
                    setLabelObject1(k++, 5, el);
                }
            } else if (userObject instanceof DTerm) {
            } else if (userObject instanceof Cond) {
                Cond c = (Cond) userObject;
                int k = 0;
                int nt = c.getNo();
                if (nt != 0 && nt == BookCellEditor.cond_no)
                    crsp = true;

                if (c.getNo() != 0) {
                    setLabelObject(k++, 0, c.getNo() + ". ");
                }
                setLabelObject1(k++, 4, userObject);
                if (c.getNo() != 0 && node.getChildCount() != 0 && c.getRule() > 0) {
                    setLabelObject1(k++, 2, c);
                } else if (node.getChildCount() == 0) {
                    int n = c.get_conc_type();
                    if (n != 0) {
                        setLabelObject1(k++, 0, "   (" + getLanguage("by HYP") + ")");
                    } else if (c.getNo() == 0) {
                        setLabelObject1(k++, 0, "   (" + getLanguage("in GIB") + ")");
                    }

                }
            } else if (userObject instanceof LList) {
                LList ls = (LList) userObject;
                setLabelObject(0, 7, ls);
            } else if (userObject instanceof Rule) {
                Rule ls = (Rule) userObject;
                setLabelObject(0, 7, ls);
            } else {
                setLabelObject(0, 9999, userObject);
            }

            if (cell != null) {
                if (selected || crsp) {
                    cell.setBackground(cell.backgroundSelectionColor);
                } else {
                    cell.setBackground(cell.backgroundNonSelectionColor);
                }
                cell.setEnabled(tree.isEnabled());
                returnValue = cell;
            }
        }
        if (returnValue == null) {
            returnValue = VcellRender.defaultRenderer.
                    getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }


}

/**
 * BookCellEditor is a custom cell editor for a tree structure, allowing
 * for the editing of tree nodes with specific rendering and behavior.
 */
class BookCellEditor extends BasicCellEditor implements MouseListener {

    /**
     * Constructs a BookCellEditor with the specified number of labels.
     * Initializes the editor and adds mouse listeners to all labels.
     *
     * @param n the number of labels to initialize
     */
    public BookCellEditor(int n) {
        init(n);

        for (int i = 0; i < renderlist.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist.get(i);
            lb.addMouseListener(this);
        }
        for (int i = 0; i < renderlist1.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist1.get(i);
            lb.addMouseListener(this);
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Handles the mouse pressed event. Deselects the previously selected label,
     * selects the new label, and repaints the cell.
     *
     * @param e the MouseEvent that triggered this method
     */
    public void mousePressed(MouseEvent e) {
        if (selectLabel != null) {
            selectLabel.setSelected(false);
        }
        selectLabel = (ItemLabel) e.getSource();
        selectLabel.setSelected(true);
        cell.repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}

/**
 * BasicCellEditor is a custom cell editor for a tree structure, allowing
 * for the editing of tree nodes with specific rendering and behavior.
 */
class BasicCellEditor extends AbstractCellEditor implements TreeCellEditor {
    public static int cond_no = 0;

    Vector renderlist = new Vector();
    Vector renderlist1 = new Vector();
    VcellRender cell = new VcellRender();
    ItemLabel selectLabel = null;


    /**
     * Initializes the editor with the specified number of labels.
     *
     * @param n the number of labels to initialize
     */
    public void init(int n) {
        for (int i = 1; i <= n; i++) {
            ItemLabel lb = new ItemLabel(false, false);
            lb.setRenderT(false);
            renderlist.add(lb);
        }
        for (int i = 1; i <= n; i++) {
            ItemLabel lb = new ItemLabel(false, true);
            lb.setRenderT(false);
            lb.setFont(ItemLabel.font1);
            renderlist1.add(lb);
        }
        cell.setBorder(new LineBorder(cell.backgroundSelectionColor.darker(), 1));
    }

    /**
     * Sets the font for the editor labels.
     *
     * @param f the font to set for the labels
     */
    public void setEditorFont(Font f) {
        for (int i = 0; i < renderlist.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist.get(i);
            lb.setFont(f);
        }
        Font f1 = new Font(f.getFamily(), f.getStyle(), f.getSize() - 1);
        for (int i = 0; i < renderlist1.size(); i++) {
            ItemLabel lb = (ItemLabel) renderlist1.get(i);
            lb.setFont(f1);
        }
    }

    /**
     * Sets the user object and type for a label at the specified index.
     *
     * @param index the index of the label to set
     * @param t     the type of the label
     * @param obj   the user object to associate with the label
     */
    public void setLabelObject(int index, int t, Object obj) {
        ItemLabel label = (ItemLabel) renderlist.get(index);
        label.setUserObject(t, obj);
        cell.add(label);
    }

    /**
     * Sets the user object and type for a label at the specified index in the secondary list.
     *
     * @param index the index of the label to set
     * @param t     the type of the label
     * @param obj   the user object to associate with the label
     */
    public void setLabelObject1(int index, int t, Object obj) {
        ItemLabel label = (ItemLabel) renderlist1.get(index);
        label.setUserObject(t, obj);
        cell.add(label);
    }

    /**
     * Adds a mouse listener to all labels.
     *
     * @param listener the mouse listener to add
     */
    public void addListenerToAllLabel(MouseListener listener) {
        for (int i = 0; i < renderlist.size(); i++) {
            ItemLabel label = (ItemLabel) renderlist.get(i);
            label.addMouseListener(listener);
        }
        for (int i = 0; i < renderlist1.size(); i++) {
            ItemLabel label = (ItemLabel) renderlist1.get(i);
            label.addMouseListener(listener);
        }
    }

    /**
     * Returns the tree cell editor component for the specified tree node.
     *
     * @param tree       the JTree that is asking the editor to edit
     * @param value      the value of the cell to be edited
     * @param isSelected whether the cell is selected
     * @param expanded   whether the node is expanded
     * @param leaf       whether the node is a leaf node
     * @param row        the row index of the node
     * @return the component for editing the tree cell
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                boolean isSelected,
                                                boolean expanded,
                                                boolean leaf, int row) {
        Component returnValue = null;
        cond_no = 0;
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            cell.removeAll();
            cell.revalidate();
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            VcellRender cell = this.cell;

            if (userObject instanceof GrTerm) {
                GrTerm gr = (GrTerm) userObject;
                Vector v = gr.getAllxterm();
                int index = v.size();
                int k = 0;
                if (index >= 0) {
                    if (row != 1) {
                        setLabelObject(0, 0, " = ");
                    } else {
                        setLabelObject(0, 0, "    ");
                    }
                    k++;
                    for (int i = 0; i < v.size(); i++) {
                        XTerm x = (XTerm) v.get(i);
                        setLabelObject(k++, 1, x);
                    }
                }
                if (gr.getPTN() == 0) {
                    ElTerm e = gr.el;
                    if (e.getEType() > 0) {
                        setLabelObject(k++, 2, gr.el);
                        setLabelObject1(k++, 5, gr.el);
                    }
                }
                if (v.size() == 1) {
                    XTerm x = (XTerm) v.get(0);
                    if (x.getPV() == 0)
                        setLabelObject(k++, 0, "   " + getLanguage("Q.E.D."));
                }
            } else if (userObject instanceof ElTerm) {
                ElTerm el = (ElTerm) userObject;
                Vector v = el.getAllxterm();
                int k = 0;
                if (node.getParent() != null) {
                    setLabelObject1(k++, 0, getLanguage("because"));
                }
                setLabelObject1(k++, 1, v.get(0));
                setLabelObject1(k++, 0, " = ");
                for (int i = 1; i < v.size(); i++) {
                    setLabelObject1(k++, 1, v.get(i));
                }
                if (el.getEType() > 0)
                    setLabelObject1(k++, 2, el);
                setLabelObject1(k++, 5, el);
            } else if (userObject instanceof DTerm) {
            } else if (userObject instanceof Cond) {
                Cond c = (Cond) userObject;
                int k = 0;
                cond_no = c.getNo();
                if (c.getNo() != 0) {
                    setLabelObject(k++, 0, c.getNo() + ". ");
                }
                setLabelObject1(k++, 4, userObject);
                if (c.getNo() != 0 && node.getChildCount() != 0 &&
                        c.getRule() >= 0) {
                    if (c.getRule() > 0) {
                        setLabelObject1(k++, 2, c);
                    }
                    setLabelObject1(k++, 5, c);
                } else if (node.getChildCount() == 0) {
                    int n = c.get_conc_type();
                    if (n != 0) {
                        setLabelObject1(k++, 0, "   (" + getLanguage("by HYP") + ")");
                    } else if (c.getNo() == 0) {
                        setLabelObject1(k++, 0, "   (in GIB)");
                    }
                }
            } else {
                setLabelObject(0, 9999, userObject);
            }

            if (cell != null) {
                cell.setBackground(cell.backgroundSelectionColor);
                cell.setEnabled(tree.isEnabled());
                returnValue = cell;
            }
        }
        if (returnValue == null) {
            returnValue = VcellRender.defaultRenderer.
                    getTreeCellRendererComponent(tree,
                            value, isSelected, expanded, leaf, row, true);
        }
        return returnValue;
    }

    /**
     * Cancels the cell editing process.
     */
    public void cancelCellEditing() {
        super.cancelCellEditing();
        cond_no = 0;
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editor
     */
    public Object getCellEditorValue() {
        return null;
    }
}

/**
 * ItemLabel is a custom JLabel that can display different types of content
 * and handle mouse events.
 */
class ItemLabel extends JLabel {
    public static ImageIcon icon = GExpert.createImageIcon("images/dtree/detail.gif");
    private static ImageIcon icon_bc = GExpert.createImageIcon("images/dtree/because.gif");


    public static Font font = CMisc.fullFont;
    public static Font font1 = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
    public GExpert gxInstance;


    boolean isrender = true;
    boolean mouse_inside = false;
    protected boolean iselm = false;
    int type; // 0.Normal text 1. xterm 2.rule.3.el_term,4:cond  , 5: icon.
    private Object userValue;

    boolean selected = false;

    /**
     * Returns the type of the item label.
     *
     * @return the type of the item label
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the user object associated with the item label.
     *
     * @return the user object associated with the item label
     */
    public Object getUserObject() {
        return userValue;
    }

    /**
     * Constructs an ItemLabel with the specified render and element flags.
     *
     * @param r whether the label should be rendered
     * @param e whether the label is an element
     */
    public ItemLabel(boolean r, boolean e) {
        super();
        isrender = r;
        iselm = e;
        if (e) {
            this.setFont(font1);
        } else {
            this.setFont(font);
            this.setForeground(Color.black);
        }

        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                selected = true;
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                mouse_inside = true;
                if (type == 5) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                ItemLabel.this.repaint();
            }

            public void mouseExited(MouseEvent e) {
                mouse_inside = false;
                if (type == 5) {
                    setCursor(Cursor.getDefaultCursor());
                }
                ItemLabel.this.repaint();
            }
        });
    }

    /**
     * Sets whether the label should be rendered.
     *
     * @param r true if the label should be rendered, false otherwise
     */
    public void setRenderT(boolean r) {
        this.isrender = r;
    }

    /**
     * Sets whether the label is selected.
     *
     * @param s true if the label is selected, false otherwise
     */
    public void setSelected(boolean s) {
        selected = s;
        if (iselm) {
            {
                if (s) {
                    this.setForeground(Color.black);
                } else {
                    this.setForeground(Color.black);
                }
            }
        }
        this.repaint();
    }

    /**
     * Returns whether the label is selected.
     *
     * @return true if the label is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the user object and type for the label.
     *
     * @param type the type of the label
     * @param obj  the user object to associate with the label
     */
    public void setUserObject(int type, Object obj) {
        this.type = type;
        userValue = obj;
        if (type == 2) {
            if (obj instanceof ElTerm) {
                ElTerm e = (ElTerm) obj;
                if (!iselm) {
                    this.setForeground(Color.black);
                }
                if (e.etype != 5) {
                    this.setText(" (" + GExpert.getTranslationViaGettext("rule {0}", e.etype + "") + ")");
                } else {
                    this.setText(" (" + GExpert.getLanguage("addition") + ")");
                }
            } else if (obj instanceof Cond) {
                Cond c = (Cond) obj;
                if (!iselm) {
                    this.setForeground(Color.black);
                }
                int r = c.getRule();
                setText(" (" + GExpert.getTranslationViaGettext("r{0}", r + "") + ")");
            }
            this.setIcon(null);
        } else if (type == 5) {
            this.setToolTipText(GExpert.getLanguage("Click to see the detail"));
            this.setIcon(icon);
            this.setText(null);
        } else if (type == 6) {
            Cond c = (Cond) obj;
            this.setHorizontalTextPosition(JLabel.LEFT);
            this.setIcon(icon);
            this.setText("   DR" + c.getRule());
        } else {
            String s = obj.toString();
            if (type == 0 && s.trim().equals("because")) {
                this.setIcon(icon_bc);
                this.setText(null);
            } else {
                this.setText(s);
                this.setIcon(null);
            }
        }
    }

    /**
     * Paints the component.
     *
     * @param g the Graphics object to protect
     */
    public void paint(Graphics g) {
        if (isrender) {
        } else {

            if (type == 5) {
                if (mouse_inside || selected) {
                    g.setColor(Color.pink.brighter());
                    g.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
                    g.setColor(Color.pink);
                    g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
                }
            } else if (type != 0) {
                {
                    if (selected) {
                        g.setColor(Color.pink);
                        g.fillRect(0, 0, this.getWidth() - 1,
                                this.getHeight() - 1);
                    }
                    if (mouse_inside || selected) {
                        g.setColor(Color.pink.darker());
                        g.drawRect(0, 0, this.getWidth() - 1,
                                this.getHeight() - 1);
                    }
                }
            }
        }
        super.paint(g);
    }
}
