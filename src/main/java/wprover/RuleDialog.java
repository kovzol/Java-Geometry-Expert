package wprover;


import gprover.Gib;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import java.util.Vector;

/**
 * RuleDialog is a class that extends JBaseDialog and implements ChangeListener,
 * ActionListener, and MouseListener interfaces. It provides a dialog for displaying
 * and managing rules related to the GDD method and the Full Angle method.
 */
public class RuleDialog extends JBaseDialog implements ChangeListener, ActionListener, MouseListener {

    private GExpert gxInstance;
    private JTree tree, treef;
    private JTabbedPane pane;

    /**
     * Constructs a new RuleDialog with the specified GExpert instance.
     *
     * @param owner the GExpert instance to associate with this dialog
     */
    public RuleDialog(GExpert owner) {
        super(owner.getFrame());

        gxInstance = owner;

        this.setTitle(GExpert.getLanguage("Rules for the GDD Method"));

        Object rootNodes[] = new Object[6];
        int i = 0;
        Vector vrule = RuleList.getAllGDDRules();

        rootNodes[0] = createNameVector(GExpert.getLanguage("Rules related to parallel lines"), vrule, i, i += 3);
        rootNodes[1] = createNameVector(GExpert.getLanguage("Rules related to perpendicular lines"), vrule, ++i, i += 3);
        rootNodes[2] = createNameVector(GExpert.getLanguage("Rules related to circles"), vrule, ++i, i += 6);
        rootNodes[3] = createNameVector(GExpert.getLanguage("Rules related to angles"), vrule, ++i, i += 6);
        rootNodes[4] = createNameVector(GExpert.getLanguage("Rules related to triangles"), vrule, ++i, i += 14);
        rootNodes[5] = createNameVector(GExpert.getLanguage("Other rules"), vrule, ++i, i += 5);

        Vector rootVector = new NamedVector("Root", rootNodes);
        tree = new JTree(rootVector);

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);

        // tree.setCellEditor(new CheckBoxNodeEditor(tree));
        tree.setEditable(false);
        tree.addMouseListener(this);

        JScrollPane scrollPane = new JScrollPane(tree);
        pane = new JTabbedPane(JTabbedPane.BOTTOM);
        pane.addTab(GExpert.getLanguage("Rules for the GDD Method"), scrollPane);
        pane.addChangeListener(this);

        Vector vfull = RuleList.getAllFullRules();
        Object rNodes[] = new Object[1];
        rNodes[0] = createNameVector(GExpert.getLanguage("Full Rules"), vfull, 0, 28);
        treef = new JTree(new NamedVector("Root", rNodes));
        treef.setCellRenderer(renderer);
        treef.addMouseListener(this);

        JScrollPane scrollPane1 = new JScrollPane(treef);
        pane.addTab(GExpert.getLanguage("Rules for the Full Angle Method"), scrollPane1);

        this.getContentPane().add(pane, BorderLayout.CENTER);
        expandAll();
        this.setSize(600, owner.getHeight());
    }

    /**
     * Sets the selected tab in the JTabbedPane.
     *
     * @param n the index of the tab to select
     */
    public void setSelected(int n) {
        pane.setSelectedIndex(n);
    }

    /**
     * Called when the state of the JTabbedPane changes.
     *
     * @param e the ChangeEvent that triggered this method
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == pane) {
            this.setTitle(pane.getTitleAt(pane.getSelectedIndex()));
        }
    }

    /**
     * Creates a NamedVector with the specified name and a subset of the given vector.
     *
     * @param n     the name of the NamedVector
     * @param vlist the vector to create the subset from
     * @param t1    the starting index of the subset
     * @param t2    the ending index of the subset
     * @return the created NamedVector
     */
    private Vector createNameVector(String n, Vector vlist, int t1, int t2) {
        CheckBoxNode[] list1 = new CheckBoxNode[t2 - t1 + 1];
        createCheckBox(list1, vlist, t1, t2);
        Vector v1 = new NamedVector(n, list1);
        return v1;
    }

    /**
     * Creates an array of CheckBoxNode objects from a subset of the given vector.
     *
     * @param list  the array to store the CheckBoxNode objects
     * @param vlist the vector to create the subset from
     * @param t1    the starting index of the subset
     * @param t2    the ending index of the subset
     */
    private void createCheckBox(CheckBoxNode[] list, Vector vlist, int t1, int t2) {
        int index = 0;
        for (int i = t1; i < vlist.size() && i <= t2; i++) {
            GRule r = (GRule) vlist.get(i);
            int t = r.type;
            list[index++] = new CheckBoxNode(t, t + ".  " +
                    GExpert.getLanguage(r.description), true, r);
        }
    }

    /**
     * Expands all rows in the JTree components.
     */
    private void expandAll() {
        int n = tree.getRowCount();
        for (int i = n - 1; i >= 0; i--)
            tree.expandRow(i);
        n = treef.getRowCount();
        for (int i = n - 1; i >= 0; i--)
            treef.expandRow(i);
    }

    /**
     * Returns the selected GRule from the currently selected tab.
     *
     * @return the selected GRule, or null if no rule is selected
     */
    public GRule getSelectedRule() {
        DefaultMutableTreeNode nd = null;
        JTree tt = null;

        if (pane.getSelectedIndex() == 0) {
            nd = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            tt = tree;
        } else {
            nd = (DefaultMutableTreeNode) treef.getLastSelectedPathComponent();
            tt = treef;
        }

        if (nd != null) {
            Object obj = nd.getUserObject();
            if (obj instanceof CheckBoxNode) {
                CheckBoxNode ch = (CheckBoxNode) obj;
                GRule r = ch.getRule();
                return r;
            }
        }
        return null;
    }

    /**
     * Handles mouse click events on the JTree components.
     *
     * @param e the MouseEvent that triggered this method
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            DefaultMutableTreeNode nd = null;
            JTree tt = null;

            if (pane.getSelectedIndex() == 0) {
                tt = tree;
            } else {
                tt = treef;
            }

            GRule r = this.getSelectedRule();
            if (r != null) {
                ppMenu m = new ppMenu(r);
                m.show(tt, e.getX(), e.getY());
            }
        } else {
            if (e.getClickCount() > 1) {
                GRule r = this.getSelectedRule();
                this.showRuleDialog(r);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * CheckBoxNodeRenderer is a class that implements TreeCellRenderer to render
     * checkboxes in a JTree.
     */
    class CheckBoxNodeRenderer implements TreeCellRenderer {
        private JCheckBox leafRenderer = new JCheckBox();
        private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();
        Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
        CheckBoxNode leafNode = null;

        protected JCheckBox getLeafRenderer() {
            return leafRenderer;
        }

        protected CheckBoxNode getLeafNode() {
            return leafNode;
        }

        public CheckBoxNodeRenderer() {
            Font fontValue;
            fontValue = UIManager.getFont("Tree.font");
            if (fontValue != null) {
                leafRenderer.setFont(fontValue);
            }
            Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
            leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));


            selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
            selectionForeground = UIManager.getColor("Tree.selectionForeground");
            selectionBackground = UIManager.getColor("Tree.selectionBackground");
            textForeground = UIManager.getColor("Tree.textForeground");
            textBackground = UIManager.getColor("Tree.textBackground");
            leafRenderer.setBorder(null);
            leafRenderer.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                }
            });
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {

            Component returnValue;
            if (leaf) {
                String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
                leafRenderer.setText(stringValue);
                leafRenderer.setSelected(false);
                leafRenderer.setEnabled(tree.isEnabled());

                if (selected) {
                    leafRenderer.setForeground(selectionForeground);
                    leafRenderer.setBackground(selectionBackground);
                } else {
                    leafRenderer.setForeground(textForeground);
                    leafRenderer.setBackground(textBackground);
                }

                if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof CheckBoxNode) {
                        CheckBoxNode node = (CheckBoxNode) userObject;
                        leafRenderer.setText(node.getText());
                        leafRenderer.setSelected(node.isSelected());
                        leafNode = node;
                    }
                }
                returnValue = leafRenderer;
            } else {
                returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
                        value, selected, expanded, leaf, row, hasFocus);
            }
            return returnValue;
        }
    }

    /**
     * CheckBoxNode is a class that represents a node in the JTree with a checkbox.
     */
    class CheckBoxNode {

        private String text;
        private boolean selected;
        private int v;
        private GRule rule;

        public CheckBoxNode(int n, String text, boolean selected, GRule rl) {
            this.text = text;
            this.selected = selected;
            this.v = n;
            rule = rl;
        }

        public GRule getRule() {
            return rule;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean newValue) {
            selected = newValue;
        }

        public String getText() {
            return text;
        }

        public void setText(String newValue) {
            text = newValue;
        }

        public void updateValue(boolean r) {
            selected = r;
            Gib.setValue(v, r);

        }

        public String toString() {
            return getClass().getName() + "[" + text + "/" + selected + "]";
        }
    }

    /**
     * NamedVector is a class that extends Vector and adds a name attribute.
     */
    class NamedVector extends Vector {
        String name;

        public NamedVector(String name) {
            this.name = name;
        }

        public NamedVector(String name, Object elements[]) {
            this.name = name;
            for (int i = 0, n = elements.length; i < n; i++) {
                add(elements[i]);
            }
        }

        public String toString() {
            return name;
        }
    }


    public void showRuleDialog(GRule r) {
        if (r != null) {
            if (r.isGDDRule()) {
                RuleListDialog dlg = new RuleListDialog(gxInstance);
                if (dlg.loadRule(0, r.type))
                    dlg.setVisible(true);
            } else if (r.isFullRule()) {
                RuleListDialog dlg = new RuleListDialog(gxInstance);
                if (dlg.loadRule(1, r.type))
                    dlg.setVisible(true);
            }
        }

    }

    public void actionPerformed(ActionEvent e) {
        String cm = e.getActionCommand();
        if (cm.equals("Show Detail")) {
            JMenuItem m1 = (JMenuItem) e.getSource();
            ppMenu m = (ppMenu) m1.getParent();
            GRule r = m.getRule();
            showRuleDialog(r);
        }

    }

    /**
     * Popup menu for displaying rule options.
     */
    class ppMenu extends JPopupMenu {

        private GRule rule;

        public GRule getRule() {
            return rule;
        }

        public ppMenu(GRule r) {
            super();
            rule = r;

            JMenuItem it = new JMenuItem("Show Detail");
            it.addActionListener(RuleDialog.this);
            add(it);
            addSeparator();
            it = new JMenuItem("Enable");
            it.addActionListener(RuleDialog.this);
            it.setEnabled(false);
            add(it);
            it = new JMenuItem("Disable");
            it.addActionListener(RuleDialog.this);
            it.setEnabled(false);
            add(it);
            addSeparator();
            it = new JMenuItem("Help..");
            it.addActionListener(RuleDialog.this);
            it.setEnabled(false);
            add(it);
        }
    }
}
