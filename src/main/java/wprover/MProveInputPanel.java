package wprover;

import UI.EntityButtonUI;
import UI.BLeveledButtonUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

/**
 * MProveInputPanel is a class that represents a panel for manual input in a
 * graphical user interface. It provides various components for user interaction
 * and input handling.
 */
public class MProveInputPanel extends JToolBar implements ActionListener {
    public static ImageIcon icon_Right = GExpert.createImageIcon("images/dtree/right.gif");
    public static ImageIcon icon_Wrong = GExpert.createImageIcon("images/dtree/wrong.gif");
    public static ImageIcon icon_Question = GExpert.createImageIcon("images/dtree/question.gif");

    JToggleButton bok, badd, bappend, bchild, bcancel;

    private JPanel contentPane = new JPanel();
    private JPanel mdPanel = new JPanel();
    private BLeveledButtonUI ui = new BLeveledButtonUI();

    popSelectMenu1 popSelect;
    private int type = 0;
    private JLabel slabel;

    private textPanel textPane;
    private prefixPanel prefixPane;
    private drawPanel drawPane;
    private dobjPanel objectPane;
    private ConcPanel assertPane;
    private symbolPanel symbolPane;

    private CommonEquationPanel epane;
    private rulePanel rulepane;

    private MObject obj;
    private MNode node;
    private DefaultMutableTreeNode tnode;
    private MProveTree tree;


    private GExpert gxInstance;
    private DPanel dpane;
    private DrawTextProcess dp;

    private JPanel topPanel;


    /**
     * Sets the node value and updates the panel based on the provided MObject.
     *
     * @param node the DefaultMutableTreeNode to set
     * @param obj  the MObject to associate with the node
     */
    public void setNodeValue(DefaultMutableTreeNode node, MObject obj) {
        tnode = node;
        this.node = (MNode) node.getUserObject();
        this.obj = obj;

        if (obj != null && obj.getType() != 0) {
            int d = obj.getType() - 1;
            int d1 = -1;
            String s = MObject.pStrings[d];

            s = getLanguage(s);
            if (obj instanceof MPrefix)
                d1 = ((MPrefix) obj).getPrefixType();
            else if (obj instanceof MSymbol) {
                d1 = ((MSymbol) obj).getSymbolType();
            } else if (obj instanceof MAssertion) {
                d1 = ((MAssertion) obj).getAssertionType();
            } else if (obj instanceof MDrObj) {
                d1 = ((MDrObj) obj).getType1();
            } else
                d1 = -1;
            mselected(s, d, d1);
        } else {
            mdPanel.removeAll();
            mdPanel.revalidate();
            mselected("TYPE", -1, -1);
        }
    }

    /**
     * Gets the language string for the specified key.
     *
     * @param s the key for the language string
     * @return the language string associated with the key
     */
    String getLanguage(String s) {
        if (gxInstance != null)
            return GExpert.getLanguage(s);
        return s;
    }

    /**
     * Gets the maximum size of the component.
     *
     * @return the maximum size of the component
     */
    public Dimension getMaximumSize() {
        Dimension dm = super.getPreferredSize();
        dm.setSize(Integer.MAX_VALUE, dm.getHeight());
        return dm;
    }

    /**
     * Changes the state of the buttons based on the provided boolean value.
     *
     * @param r a boolean indicating whether to enable or disable the buttons
     */
    private void stateButtonChange(boolean r) {
        bok.setEnabled(r);
        badd.setEnabled(r);
        bappend.setEnabled(r);
        bchild.setEnabled(r);
        bcancel.setEnabled(r);
    }

    /**
     * Gets the preferred size of the component.
     *
     * @return the preferred size of the component
     */
    public Dimension getPreferredSize() {
        Dimension dm = super.getPreferredSize();
        return dm;
    }

    /**
     * Handles menu selection and updates the panel based on the selected item.
     *
     * @param id  the ID of the selected item
     * @param id2 the secondary ID of the selected item
     */
    private void onMenuSelected(int id, int id2) {
        int t = id;
        if (t == -1) return;
        type = t + 1;

        setBState(true);

        if (type == MObject.TEXT) {
            mdPanel.removeAll();
            mdPanel.add(textPane);
            if (obj instanceof MText)
                textPane.setUserObject((MText) obj);
            else
                textPane.setUserObject(null);
        } else if (type == MObject.PREFIX) {
            mdPanel.removeAll();
            mdPanel.add(prefixPane);
            if (obj instanceof MPrefix) {
                prefixPane.setUserObject((MPrefix) obj);
            } else
                prefixPane.setUserObject(null);
            prefixPane.setTypeSelection(id2);
        } else if (type == MObject.DRAW) {
            mdPanel.removeAll();
            if (obj instanceof MDraw)
                drawPane.setUserObject((MDraw) obj);
            else
                drawPane.setUserObject(null);
            mdPanel.add(drawPane);
        } else if (type == MObject.ASSERT) {
            mdPanel.removeAll();
            assertPane.update();
            if (obj != null && obj instanceof MAssertion) {
                assertPane.setUserObject((MAssertion) obj);
            } else {
                assertPane.setUserObject(null);
            }
            assertPane.setTypeSelection(id2);
            mdPanel.add(assertPane);
            assertPane.updateBState();
            gxInstance.setActionSelect();
        } else if (type == MObject.SYMBOL) {
            mdPanel.removeAll();
            if (obj instanceof MSymbol)
                symbolPane.setUserObject((MSymbol) obj);
            else
                symbolPane.setUserObject(null);
            symbolPane.setTypeSelection(id2);
            mdPanel.add(symbolPane);

        } else if (type == MObject.DOBJECT) {
            mdPanel.removeAll();
            if (obj instanceof MDrObj) {
                objectPane.setUserObject((MDrObj) obj);
            } else
                objectPane.setUserObject(null);
            objectPane.setTypeSelection(id2);
            mdPanel.add(objectPane);
            objectPane.updateState();
            gxInstance.setActionSelect();

        } else if (type == MObject.EQUATION) {
            mdPanel.removeAll();
            mdPanel.add(epane);
            if (obj instanceof MEquation)
                epane.setUserObject((MEquation) obj);
            else
                epane.setUserObject(null);
            gxInstance.setActionSelect();

        } else if (type == MObject.RULE) {
            mdPanel.removeAll();
            mdPanel.add(rulepane);
            if (obj instanceof MRule)
                rulepane.setUserObject((MRule) obj);
            else
                rulepane.setUserObject(null);
        }
        mdPanel.revalidate();
        mdPanel.repaint();
    }

    /**
     * Constructs a new MProveInputPanel with the specified GExpert instance, DPanel, DrawTextProcess, and JTree.
     *
     * @param gx   the GExpert instance to associate with this MProveInputPanel
     * @param pp   the DPanel instance to associate with this MProveInputPanel
     * @param dp   the DrawTextProcess instance to associate with this MProveInputPanel
     * @param tree the JTree instance to associate with this MProveInputPanel
     */
    public MProveInputPanel(GExpert gx, DPanel pp, DrawTextProcess dp, JTree tree) {
        super("Manual Input Toolbar");
        super.setVisible(true);
        super.setFloatable(true);

        gxInstance = gx;
        this.dpane = pp;
        this.dp = dp;

        this.tree = (MProveTree) tree;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel pane1 = new JPanel();
        topPanel = pane1;
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.X_AXIS));

        slabel = new selectLabel();
        pane1.add(slabel);
        mdPanel = new JPanel();
        mdPanel.setBorder(new LineBorder(Color.gray, 1));
        pane1.add(mdPanel);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        JPanel ocPanel = new JPanel();
        ocPanel.setLayout(new BoxLayout(ocPanel, BoxLayout.X_AXIS));
        JToggleButton button = new JToggleButton(getLanguage("Edit"));
        button.setActionCommand("Edit");
        button.setSelected(true);
        button.addActionListener(this);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setUI(new EntityButtonUI());
        ocPanel.add(button);
        button = new JToggleButton(GExpert.createImageIcon("images/ptree/full_step.gif"));
        button.setActionCommand("Step");
        button.addActionListener(this);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setUI(ui);
        ocPanel.add(button);
        ocPanel.add(Box.createHorizontalStrut(2));
        ocPanel.add(Box.createHorizontalGlue());
        bok = button = new JToggleButton(getLanguage("OK"));
        button.setActionCommand("OK");
        button.addActionListener(this);
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        button.setUI(ui);
        ImageIcon upicon = GExpert.createImageIcon("images/dtree/up1.gif");
        ocPanel.add(Box.createHorizontalStrut(2));
        ocPanel.add(button);
        badd = button = new JToggleButton(getLanguage("Add"), upicon);
        button.setActionCommand("Add");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        button.setHorizontalTextPosition(AbstractButton.LEFT);
        button.addActionListener(this);
        button.setUI(ui);
        ocPanel.add(Box.createHorizontalStrut(2));
        ocPanel.add(button);
        bappend = button = new JToggleButton(getLanguage("Append"), upicon);
        button.setActionCommand("Append");
        button.setHorizontalTextPosition(AbstractButton.LEFT);
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        button.addActionListener(this);
        button.setUI(ui);
        ocPanel.add(Box.createHorizontalStrut(2));
        ocPanel.add(button);
        bchild = button = new JToggleButton(getLanguage("Child"), upicon);
        button.setActionCommand("Child");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        button.setHorizontalTextPosition(AbstractButton.LEFT);
        button.addActionListener(this);
        button.setUI(ui);
        ocPanel.add(Box.createHorizontalStrut(2));
        ocPanel.add(button);
        bcancel = button = new JToggleButton(getLanguage("Cancel"));
        button.setActionCommand("Cancel");
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
        button.addActionListener(this);
        button.setUI(ui);
        ocPanel.add(button);
        ocPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        contentPane.add(ocPanel);

        this.add(pane1);
        this.add(contentPane);

        textPane = new textPanel();
        prefixPane = new prefixPanel();
        drawPane = new drawPanel();
        symbolPane = new symbolPanel();
        objectPane = new dobjPanel();
        assertPane = new ConcPanel(gxInstance, this);
        epane = new CommonEquationPanel();
        rulepane = new rulePanel();
        createPopMain();
    }

    /**
     * Sets the state of the buttons in the panel.
     *
     * @param f a boolean indicating whether to enable or disable the buttons
     */
    public void setBState(boolean f) {
        if (f) {
            bok.setEnabled(true);
            badd.setEnabled(true);
            bappend.setEnabled(true);
            bchild.setEnabled(true);

        } else {
            bok.setEnabled(false);
            badd.setEnabled(false);
            bappend.setEnabled(false);
            bchild.setEnabled(false);
        }
    }

    /**
     * Creates the pop-up menu for selection.
     */
    private void createPopMain() {
        popSelect = new popSelectMenu1();
    }

    /**
     * Handles the OK button action.
     */
    public void buttonOK() {
        if (type == MObject.TEXT) {
            if (obj instanceof MText)
                ((MText) obj).setString(textPane.getText());
            else {
                MText tx = new MText(textPane.getText());
                node.replace(obj, tx);
            }
        } else if (type == MObject.PREFIX) {
            Object obj1 = prefixPane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        } else if (type == MObject.SYMBOL) {
            Object obj1 = symbolPane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        } else if (type == MObject.ASSERT) {
            Object obj1 = assertPane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        } else if (type == MObject.DRAW) {
            Object obj1 = drawPane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
            tree.expandSelectedNode();
        } else if (type == MObject.DOBJECT) {
            Object obj1 = objectPane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        } else if (type == MObject.EQUATION) {
            Object obj1 = epane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        } else if (type == MObject.RULE) {
            Object obj1 = rulepane.getUserObject();
            if (obj1 != obj)
                node.replace(obj, obj1);
        }

        ((TreeCellOPaqueEditor) tree.getCellEditor()).reset();
        tree.cancelEditing();
        tree.reload();
    }

    /**
     * Handles action events for the panel.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        JToggleButton b = (JToggleButton) e.getSource();

        if (s.equals("OK")) {
            b.setSelected(false);
            buttonOK();
        } else if (s.equals("Cancel")) {
            b.setSelected(false);
            tree.cancelEditing();
            if (type == MObject.TEXT) {
                textPane.cancel();
            } else if (type == MObject.PREFIX) {
                prefixPane.cancel();
            } else if (type == MObject.DRAW) {
                drawPane.cancel();
            } else if (type == MObject.ASSERT) {
                assertPane.cancel();
            } else if (type == MObject.SYMBOL) {
                symbolPane.cancel();
            } else if (type == MObject.DOBJECT) {
                objectPane.cancel();
            } else if (type == MObject.RULE) {
                rulepane.cancel();
            }
            mdPanel.repaint();
        } else if (s.equals("Append")) {
            popSelect.show(b, 0, b.getHeight(), 1);
        } else if (s.equals("Add")) {
            popSelect.show(b, 0, b.getHeight(), 0);
        } else if (s.equals("Child")) {
            popSelect.show(b, 0, -popSelect.getHeight(), 2);
        } else if (s.equals("Edit")) {
            boolean r = b.isSelected();
            tree.setEditable(r);
            topPanel.setVisible(r);
            this.stateButtonChange(r);
        } else if (s.equals("Step")) {
            b.setSelected(false);
            if (gxInstance != null)
                gxInstance.showProveBar(true);
        }

    }

    /**
     * Selects a point in the panel.
     *
     * @param p the point to select
     * @return true if the point was selected, false otherwise
     */
    public boolean selectAPoint(CPoint p) {
        if (type == MObject.DOBJECT) {
            objectPane.selectAPoint(p);
        } else if (type == MObject.ASSERT) {
            assertPane.selectAPoint(p);
        } else if (type == MObject.EQUATION) {
            epane.selectAPoint(p);
        } else
            return false;
        return true;
    }

    /**
     * Adds an undo step to the panel.
     *
     * @param un  the UndoStruct to add
     * @param tip the tip associated with the undo step
     */
    public void addUndo(UndoStruct un, Object tip) {

        if (type == MObject.DRAW) {
            if (drawPane.isNNode()) {
                drawPane.addUndoObject(un);
                return;
            }
        }
        tree.addUndoObject(un);
    }

    /**
     * Undoes the specified step in the panel.
     *
     * @param u the UndoStruct representing the step to undo
     */
    public void undoStep(UndoStruct u) {
        tree.undoStep(u);
    }

    /**
     * A class representing a text panel for user input.
     */
    class textPanel extends JPanel {

        JTextField tpane;
        MText tx;

        public textPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            tpane = new JTextField() {
                public Dimension getPreferredSize() {
                    Dimension dm = super.getPreferredSize();
                    int w = textPanel.this.getParent().getWidth();
                    dm.setSize(w - 2, dm.getHeight());
                    return dm;
                }
            };
            this.add(tpane);
            this.add(Box.createHorizontalGlue());
        }

        public String getText() {
            return tpane.getText();
        }

        public void setUserObject(MText t) {
            if (t != null)
                tpane.setText(t.toString());
            else
                tpane.setText(null);
            tx = t;

        }

        public void cancel() {
            tpane.setText("");
            tx = null;
        }
    }

    /**
     * A class representing a prefix panel for user input.
     */
    class prefixPanel extends JPanel {
        JComboBox box;
        MPrefix pfix = null;

        public prefixPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            String[] cSprefixT = new String[MPrefix.cSprefix.length];
            // Create the translations first:
            for (int i = 0; i < MPrefix.cSprefix.length; i++) {
                cSprefixT[i] = getLanguage(MPrefix.cSprefix[i]);
            }
            // Use the translations:
            box = new JComboBox(cSprefixT);
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(box);
            this.add(Box.createHorizontalStrut(15));
            this.add(new JLabel(getLanguage("Please select")));

        }

        public void setTypeSelection(int k) {
            box.setSelectedIndex(k);
        }

        public void setUserObject(MPrefix p) {
            pfix = p;
            if (p != null)
                box.setSelectedIndex(p.getPrefixType());
            else
                box.setSelectedIndex(-1);
            if (box.getSelectedIndex() != -1)
                setBState(true);
            else setBState(false);
        }

        public Object getUserObject() {
            if (pfix != null) {
                pfix.setPrefixType(box.getSelectedIndex());
                return pfix;
            } else
                return new MPrefix(box.getSelectedIndex());
        }

        public void cancel() {
            pfix = null;
            box.setSelectedIndex(-1);
        }
    }

    /**
     * A class representing a symbol panel for user input.
     */
    class symbolPanel extends JPanel implements ItemListener {
        JComboBox box;
        MSymbol sym = null;

        public symbolPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            MSymbol.createAllIcons();
            box = new JComboBox(MSymbol.vlist);
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(box);
            box.addItemListener(this);
            this.add(Box.createHorizontalStrut(10));
            this.add(new JLabel("(please select)"));
        }

        public void setUserObject(MSymbol p) {
            sym = p;
            if (p != null)
                box.setSelectedIndex(p.getSymbolType());
            else
                box.setSelectedIndex(0);
        }

        public void itemStateChanged(ItemEvent e) {
            if (box.getSelectedIndex() != -1)
                setBState(true);
            else setBState(false);
        }

        public void setTypeSelection(int k) {
            box.setSelectedIndex(k);
        }

        public Object getUserObject() {
            if (sym != null) {
                sym.setSymbolType(box.getSelectedIndex());
                return sym;
            } else
                return new MSymbol(box.getSelectedIndex());
        }

        public void cancel() {
            box.setSelectedIndex(0);
            sym = null;
        }
    }

    /**
     * A class representing a drawing panel for user input.
     */
    class drawPanel extends JPanel {
        private MDraw draw;
        private JTextPane field;
        private JLabel label;
        private boolean nnode = false;

        public drawPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            field = new JTextPane() {
                public Dimension getPreferredSize() {
                    Dimension dm = super.getPreferredSize();
                    if (dm.getWidth() < 200)
                        dm.setSize(200, dm.getHeight());
                    return dm;
                }

                public Dimension getMaximumSize() {
                    Dimension dm = super.getMaximumSize();
                    dm.setSize(Integer.MAX_VALUE, dm.getHeight());
                    return dm;
                }
            };
            field.setBorder(new LineBorder(Color.gray, 1));
            this.add(field);
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(Box.createHorizontalStrut(3));
            this.add((label = new JLabel(getLanguage("Please draw..."))));
        }

        public void setUserObject(Object obj) {
            draw = (MDraw) obj;
            if (draw.getdrawCount() == 0)
                nnode = true;
            else nnode = false;

            if (obj != null) {
                field.setText(obj.toString());
                label.setText("(" + draw.getdrawCount() + ") " + getLanguage("Please draw..."));
            } else {
                field.setText("");
                label.setText(getLanguage("Please draw..."));
            }
        }

        public boolean isNNode() {
            return nnode;
        }

        public void addUndoObject(UndoStruct un) {
            if (draw == null) {
                draw = new MDraw();
            }
            draw.adddrawStruct(un);
            field.setText(un.toString());
            label.setText("(" + draw.getdrawCount() + ") " + getLanguage("Please draw..."));
        }

        public Object getUserObject() {
            if (draw == null) {
                draw = new MDraw();
                draw.setText(field.getText());
            } else
                draw.setText(field.getText());
            return draw;
        }

        public void cancel() {
            nnode = false;
            draw = null;
            field.setText("");
            label.setText(getLanguage("Please draw..."));
        }

    }

    /**
     * A class representing a panel for user input of objects.
     */
    class dobjPanel extends JPanel implements ActionListener, ItemListener {

        private JComboBox box;
        int pnum = 0;
        private Vector vlist = new Vector();
        private MDrObj dobj;
        private JLabel labelx;

        public dobjPanel() {
            this.setBorder(null);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel pane = new JPanel();
            pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));

            box = new JComboBox(MDrObj.vlist);
            pane.add(box);
            box.addActionListener(this);
            Vector v = dp.getPointList();
            pnum = v.size();
            this.add(Box.createHorizontalStrut(15));
            for (int i = 0; i < 5; i++) {
                JComboBox b = new JComboBox();
                vlist.add(b);
                b.addItemListener(this);
                pane.add(b);
                pane.add(Box.createHorizontalStrut(5));
            }
//            this.add(pane);
//            pane = new JPanel();
//            pane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//            pane.add(Box.createHorizontalGlue());
            labelx = new JLabel();
            pane.add(labelx);
            pane.add(Box.createHorizontalStrut(15));
            this.add(pane);
            //      box.setSelectedIndex(-1);

            int t = MDrObj.getPtAcount(0);
            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = ((JComboBox) vlist.get(i));
                if (i >= t) {
                    b.setEnabled(false);
                    b.setVisible(false);
                } else {
                    b.setEnabled(true);
                    b.setVisible(true);
                }
            }
        }

        public void selectAPoint(CPoint p) {
            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                if (b.getSelectedIndex() < 0) {
                    b.setSelectedItem(p);
                    return;
                }
            }
        }

        public boolean inputFinished() {
            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                if (b.isEnabled() && b.getSelectedIndex() < 0)
                    return false;
            }
            return true;
        }


        public Object getUserObject() {
            if (dobj == null)
                dobj = new MDrObj(box.getSelectedIndex());
            else
                dobj.setType1(box.getSelectedIndex());
            dobj.clear();
            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                if (b.isEnabled()) {
                    dobj.add(b.getSelectedItem());
                } else
                    break;
            }
            return dobj;
        }

        public MDrObj getObject() {
            MDrObj d = new MDrObj(box.getSelectedIndex());

            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                if (b.isEnabled()) {
                    d.add(b.getSelectedItem());
                } else
                    break;
            }
            return d;
        }


        public void setTypeSelection(int k) {
            box.setSelectedIndex(k);
        }

        public void setUserObject(MDrObj d) {
            dobj = null;
            pointupdate();

            dobj = d;
            reset();
            dobj = d;
            if (d != null) {
                int n = dobj.getObjectNum();
                for (int i = 0; i < vlist.size(); i++) {
                    JComboBox bx = ((JComboBox) vlist.get(i));
                    if (i < n)
                        bx.setSelectedItem(dobj.getObject(i));
                    else {
                        bx.setSelectedIndex(-1);
                    }
                }
                box.setSelectedIndex(dobj.getType1());
//                labelx.setText(d.getTip());
            }

            if (inputFinished())
                setBState(true);
            else setBState(false);

        }

        public void reset() {

            pnum = 0;
            for (int i = 0; i < vlist.size(); i++) {
                ((JComboBox) vlist.get(i)).setSelectedIndex(-1);
            }
            dobj = null;
            box.setSelectedIndex(0);
            labelx.setText("");
            labelx.setIcon(null);
        }

        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == box) {
                int t = MDrObj.getPtAcount(box.getSelectedIndex());
                for (int i = 0; i < vlist.size(); i++) {
                    JComboBox b = ((JComboBox) vlist.get(i));
                    if (i >= t) {
                        b.setEnabled(false);
                        b.setVisible(false);
                    } else {
                        b.setEnabled(true);
                        b.setVisible(true);
                    }
                }
            }
            updateState();

        }

        public void itemStateChanged(ItemEvent e) {
            updateState();
        }


        private void updateState() {
            if (inputFinished()) {
                bok.setEnabled(true);
                MDrObj o = getObject();
                labelx.setVisible(true);
                if (o.check_valid()) {
                    labelx.setIcon(icon_Right);
                } else {
                    labelx.setIcon(icon_Wrong);
                }
                setBState(true);
            } else {
                labelx.setVisible(false);
                setBState(false);
                bok.setEnabled(false);
            }
        }

        private void pointupdate() {
            Vector v = dp.getPointList();
            for (int i = 0; i < vlist.size(); i++) {
                JComboBox b = (JComboBox) vlist.get(i);
                b.removeAllItems();
                for (int j = 0; j < v.size(); j++)
                    b.addItem(v.get(j));
                b.setSelectedIndex(-1);
            }
        }

        public void cancel() {
            reset();
        }
    }

    /**
     * A class representing a panel for user input of equations.
     */
    class rulePanel extends JPanel {
        private JComboBox box;
        private MRule rule;

        public rulePanel() {
            String[] cStringsT = new String[MRule.cStrings.length];
            for (int i = 0; i < MRule.cStrings.length; i++)
                cStringsT[i] = GExpert.getLanguage(MRule.cStrings[i]);
            box = new JComboBox(cStringsT);
            this.add(box);
        }

        public void setUserObject(MRule rule) {
            this.rule = rule;
            if (rule != null)
                box.setSelectedIndex(rule.getRuleIndex());
            else
                box.setSelectedIndex(-1);
        }

        public MRule getUserObject() {
            if (rule == null)
                rule = new MRule(box.getSelectedIndex());
            else
                rule.setRuleIndex(box.getSelectedIndex());
            return rule;
        }

        public void cancel() {
            box.setSelectedIndex(-1);
        }

    }

    /**
     * A class representing a panel for user input of common equations.
     */
    class CommonEquationPanel extends JPanel implements ActionListener {

        private Vector vlist = new Vector();
        private JPanel topPane;
        private JPanel pbottom;
        private MEquation eq;

        public CommonEquationPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            topPane = new JPanel();
            topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));
            addNewTerm();
            this.add(topPane);
            pbottom = new JPanel();
            pbottom.setLayout(new BoxLayout(pbottom, BoxLayout.X_AXIS));
            pbottom.add(Box.createHorizontalGlue());
            JButton b = new JButton(GExpert.getLanguage("OK"));
            b.addActionListener(this);
            pbottom.add(b);
            b = new JButton(GExpert.getLanguage("Add"));
            b.addActionListener(this);
            pbottom.add(b);
            b = new JButton(GExpert.getLanguage("Cancel"));
            b.addActionListener(this);
            pbottom.add(b);
            this.add(pbottom);
        }

        private void addNewTerm() {
            boolean b = true;
            if (vlist.size() == 0)
                b = false;
            termPane opane = new termPane(b);
            topPane.add(opane);
            vlist.add(opane);
        }

        public JComboBox getSymbolBox() {
            JComboBox B = new JComboBox(MEqTerm.cStrings);
            B.setFont(new Font("Dialog", Font.BOLD, 16));
            return B;
        }

        public termPane getATermPane(int k) {
            if (vlist.size() <= k)
                addNewTerm();
            return (termPane) vlist.get(k);
        }

        public void setUserObject(MEquation eq) {
            this.eq = eq;
            topPane.removeAll();
            vlist.clear();

            if (eq != null)
                for (int i = 0; i < eq.getTermCount(); i++) {
                    termPane tm = (termPane) this.getATermPane(i);
                    tm.setUserObject((MEqTerm) eq.getTerm(i));
                }
            else
                this.getATermPane(0);
        }

        public MEquation getUserObject() {
            if (eq == null)
                eq = new MEquation();
            else
                eq.clearAll();

            for (int i = 0; i < vlist.size(); i++) {
                termPane t = (termPane) vlist.get(i);
                eq.addTerm(t.getUserObject());
            }
            return eq;
        }

        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equals("OK")) {
                buttonOK();
            } else if (s.equals("Cancel")) {
                int n = topPane.getComponentCount();
                if (n != 0) {
                    JComponent comp = (JComponent) topPane.getComponent(n - 1);
                    topPane.remove(comp);
                    vlist.remove(comp);
                    this.revalidate();
                }

            } else if (s.equals("Add")) {
                addNewTerm();
                this.revalidate();
            }
        }

        public void selectAPoint(CPoint p) {
            int n = vlist.size();
            if (n == 0) return;

            termPane t = (termPane) vlist.get(n - 1);
            t.selectAPoint(p);
        }

        class termPane extends JPanel {
            private dobjPanel opane;
            private JComboBox box;
            private MEqTerm eqt;

            public termPane(boolean f) {
                this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                if (f) {
                    JPanel p = new JPanel();
                    p.add((box = CommonEquationPanel.this.getSymbolBox()));
                    this.add(p);
                }
                this.add((opane = new dobjPanel()));
                opane.pointupdate();
            }

            public void setUserObject(MEqTerm eq) {
                eqt = eq;

                if (eq != null) {
                    if (box != null) box.setSelectedIndex(eq.getEType());
                    opane.setUserObject(eq.getObject());
                } else {
                    box.setSelectedIndex(0);
                    opane.setUserObject(null);
                }
            }

            public void selectAPoint(CPoint p) {
                opane.selectAPoint(p);
            }

            public MEqTerm getUserObject() {
                if (eqt == null) {
                    int b = -1;
                    if (box != null)
                        b = box.getSelectedIndex();
                    eqt = new MEqTerm(b, (MDrObj) opane.getUserObject());
                } else {

                    opane.getUserObject();
                    if (box != null)
                        eqt.setEType(box.getSelectedIndex());
                }
                return eqt;
            }
        }
    }

    private static Border border1 = BorderFactory.createRaisedBevelBorder();
    private static Border border2 = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    /**
     * This class is used to show the select menu
     */
    class selectLabel extends JLabel implements MouseListener, MouseMotionListener {

        public selectLabel() {
            //  this.setIcon(GExpert.createImageIcon("images/dtree/up.gif"));
            this.setHorizontalTextPosition(JLabel.LEFT);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.setBorder(border2);
            this.setText("TYPE");
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            // popMain.show(selectLabel.this, 0, -popMain.getHeight());
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            this.setBorder(border1);
        }

        public void mouseExited(MouseEvent e) {
            this.setBorder(border2);
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }
    }

    /**
     * This class is used to show the select menu
     */
    class popSelectMenu1 extends JPopupMenu implements ActionListener {
        private int type;

        public popSelectMenu1() {
            {
                JMenuItem item = new popSelectMenuItem(getLanguage(MObject.pStrings[0]), 0, -1);
                this.add(item);
                item.addActionListener(this);
            }

            JMenu menu = new JMenu(getLanguage(MObject.pStrings[1]));
            for (int i = 0; i < MPrefix.cSprefix.length; i++) {
                JMenuItem item = new popSelectMenuItem(getLanguage(MPrefix.cSprefix[i]), 1, i);
                menu.add(item);
                item.addActionListener(this);
            }

//            this.addSeparator();
            menu.addSeparator();
            for (int i = 0; i < MSymbol.cSprefix.length; i++) {
                JMenuItem item = new popSelectMenuItem("(" + getLanguage(MSymbol.cSprefix[i]) + ")", MSymbol.getSymbolIcon(i), 2, i);
                menu.add(item);
                item.addActionListener(this);
            }
            this.add(menu);

            menu = new JMenu(getLanguage(MObject.pStrings[3])); // assertion
            addAssertion(menu);
            this.add(menu);

            menu = new JMenu(getLanguage(MObject.pStrings[4])); // object
            for (int i = 0; i < MDrObj.pStrings.length; i++) {
                JMenuItem item = new popSelectMenuItem("(" + getLanguage(MDrObj.pStrings[i]) + ")", MDrObj.getImageIcon(i), 4, i);
                menu.add(item);
                item.addActionListener(this);
            }
            this.add(menu);
            JMenuItem item = new popSelectMenuItem(getLanguage(MObject.pStrings[5]), 5, -1); // draw
            this.add(item);
            item.addActionListener(this);

            item = new popSelectMenuItem(getLanguage(MObject.pStrings[6]), 6, -1); // object
            this.add(item);
            item.addActionListener(this);
            item.setEnabled(false);

            item = new popSelectMenuItem(getLanguage(MObject.pStrings[7]), 7, -1); // object
            this.add(item);
            item.addActionListener(this);
            item = new popSelectMenuItem(getLanguage(MObject.pStrings[8]), 8, -1); // object
            this.add(item);
            item.addActionListener(this);
        }

        public void addAssertion(JMenu menu) {

            int n1 = 8;

//            JPanel panel1 = new JPanel(new GridLayout(10, 3));

            JMenu menu1 = new JMenu(getLanguage("Basic Assertions"));

            for (int i = 0; i < 12; i++) {
                if (i == 9 || i == 10)
                    continue;

                String s = MAssertion.cStrings[i];
                JMenuItem item = null;

                if (s.equalsIgnoreCase("Eqangle")) {
                    item = new popSelectMenuItem(getLanguage(s), GExpert.createImageIcon("images/symbol/sym_eqangle.gif"), 3, i);
                } else if (s.equalsIgnoreCase("Congruent")) {
                    item = new popSelectMenuItem(getLanguage(s), GExpert.createImageIcon("images/symbol/sym_congruent.gif"), 3, i);
                } else
                    item = new popSelectMenuItem(getLanguage(s), 3, i);
                menu1.add(item);
                item.addActionListener(this);
            }

            int n2 = 18;


            JMenu menu2 = new JMenu(getLanguage("Polygon Related"));

            for (int i = 13; i < 20; i++) {
                String s = MAssertion.cStrings[i];
                JMenuItem item = new popSelectMenuItem(getLanguage(s), 3, i);
                menu2.add(item);
                item.addActionListener(this);
            }

            JMenu menu3 = new JMenu(getLanguage("Inequality"));

            for (int i = 9; i <= 10; i++) {
                String s = MAssertion.cStrings[i];
                JMenuItem item = new popSelectMenuItem(getLanguage(s), 3, i);
                menu3.add(item);
                item.addActionListener(this);
            }


            for (int i = 21; i < MAssertion.cStrings.length; i++) {
                String s = MAssertion.cStrings[i];
                JMenuItem item = new popSelectMenuItem(getLanguage(s), 3, i);
                menu3.add(item);
                item.addActionListener(this);
            }

            menu.add(menu1);
            menu.add(menu2);
            menu.add(menu3);
        }


        public void actionPerformed(ActionEvent e) {
            popSelectMenuItem item = (popSelectMenuItem) e.getSource();
            int id1 = item.getType1();
            int id2 = item.getType2();
            if (type == 0)  // add
            {
                MNode node = new MNode();
                MObject obj = MObject.createObject(id1, id2);
                node.add(obj);
                DefaultMutableTreeNode d1 = tree.addNewNode(node);
                MProveInputPanel.this.setNodeValue(d1, obj);
            } else if (type == 1) {
                MObject obj = MObject.createObject(id1, id2);
                DefaultMutableTreeNode d1 = tree.append(obj);
                MProveInputPanel.this.setNodeValue(d1, obj);
            } else if (type == 2) {
                MObject obj = MObject.createObject(id1, id2);
                DefaultMutableTreeNode d1 = tree.addChild(obj);
                MProveInputPanel.this.setNodeValue(d1, obj);

            }

        }

        public void show(Component invoker, int x, int y, int type) {
            this.type = type;
            super.show(invoker, x, y);
        }

    }

    /**
     * This class is used to show the select menu
     */
    class popSelectMenuItem extends JMenuItem {
        int m_id1, m_id2;

        public popSelectMenuItem(String text, int id1, int id2) {
            super(text, null);
            m_id1 = id1;
            m_id2 = id2;
        }

        public popSelectMenuItem(String text, Icon icon, int id1, int id2) {
            super(text, icon);
            m_id1 = id1;
            m_id2 = id2;
        }

        public int getType1() {
            return m_id1;
        }

        public int getType2() {
            return m_id2;
        }

    }


    /**
     * This method is used to set the node value
     */
    private void mselected(String s, int t1, int t2) {
        onMenuSelected(t1, t2);
        slabel.setText(s);
    }
}
