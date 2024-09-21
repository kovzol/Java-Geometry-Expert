package wprover;

import gprover.*;
import UI.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Vector;
import java.io.*;

import gprover.CClass;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.swing.JSVGCanvas;
import org.graphper.api.Graphviz;
import org.graphper.api.Graphviz.GraphvizBuilder;
import org.graphper.api.Node;
import org.graphper.api.attributes.NodeShapeEnum;
import org.graphper.api.attributes.Rankdir;
import org.w3c.dom.svg.SVGDocument;

public class PanelProve extends JTabbedPane implements ChangeListener {
//    private Font font_thm = new Font("Dialog", Font.BOLD, 12);

    private GExpert gxInstance;
    private DPanel dpane;
    private DrawTextProcess dp;

    private Conspanel condPane; //  construction;

    private DefaultMutableTreeNode top;
    private JTree tree; // GDD

    private DefaultMutableTreeNode top_db;
    private JTree tree_db; //Data  Base

    private DefaultMutableTreeNode top_full;
    private JTree tree_full; // Full Angle

    private DefaultMutableTreeNode top_mp;
    private MProveTree tree_mp;
    private MProveInputPanel inputm;

    private JPanel mpPanel;
    private JScrollPane gddPanel, areaPanel, dbPanel, fullPanel;
    private PanelWu wuPanel;
    private PanelGB gbPanel;

    private popMenu popcond;
    private ButtonToolBar tbar;
    private int findex = -1;
    private int gindex = 0;

    public GProver gprover;
    private boolean is_database_updated = true;

    private ConcDialog cdialog;
    private FactFinderDialog fdialog;

    private JDialog lstDrawDialog = null;
    private JDialog lstRuleDialog = null;


    public PanelProve(GExpert gx, DPanel dd, DrawTextProcess dp, boolean mbar, int idonly) {
        gxInstance = gx;

        if (gx != null) {
            dpane = gx.d;
            this.dp = gx.dp;
        } else {
            this.dpane = dd;
            this.dp = dp;
        }


        UIManager.put("Tree.line", Color.LIGHT_GRAY);
        this.setTabPlacement(JTabbedPane.BOTTOM);
        condPane = new Conspanel();

        JScrollPane scroll = new JScrollPane(condPane);
        scroll.setBorder(null);

        /*
        if (idonly < 0)
            this.add("Thm", scroll);     //0
        else this.add("Thm", scroll);     //0
         */
        this.addTab("Thm", null, scroll, GExpert.getLanguage("Show the internal description of the construction"));

        tbar = new ButtonToolBar();
        createAllPopupMenu();
        this.addChangeListener(this);
        this.createFullTreePanel();
        this.createGDDTreePanel();                         //2

        // This is not implemented yet. TODO: Implement the area method.
        // To avoid confusion, this tab should be disabled, but currently the manual proof editor assumes its presence
        // (because of using tab enumeration, seemingly, FIXME):
        this.addTab("A", null, (areaPanel = new JScrollPane()), GExpert.getLanguage("Area Method computations"));    //3
        areaPanel.disable();

        createMProvePanel(mbar);
        addTab("M", null, mpPanel, GExpert.getLanguage("Manual proof editor"));//4
        this.createDatabaseTree();        //5
        gprover = new GProver(this, gxInstance);
        this.setBorder(null);
        initFont();
    }

    public void initFont() {
//        if (gxInstance != null) {
//            Font f = gxInstance.getDefaultFont();
//            if (f != null) {
//                tree_db.setFont(f);
//            }
//        }
    }

    public void setSelectedIndex(int index) {
        if (gxInstance != null)
            gxInstance.setTextLabel2("");

        super.setSelectedIndex(index);

    }

    public void setMember(DPanel dd, DrawTextProcess dp) {
        this.dpane = dd;
        this.dp = dp;
    }


    public void stateChanged(ChangeEvent e) {
        Component comp = this.getSelectedComponent();
        if (comp == fullPanel) {
            tbar.setSelectedState(1);
        } else if (comp == gddPanel) {
            tbar.setSelectedState(2);
        } else if (comp == areaPanel) {
            tbar.setSelectedState(3);
        } else if (comp == dbPanel) {
            tbar.setSelectedState(4);
            if (!is_database_updated) {
                this.showDatabase();
                return;
            }
        } else if (comp == mpPanel) {
            tree_mp.setEditorLastRow();

        } else if (this.getSelectedIndex() == 0) {
            tbar.setSelectedState(0);
        }

        if (gxInstance != null) {
            if (comp == mpPanel)
                gxInstance.switchProveBarVisibility(true);
            else
                gxInstance.switchProveBarVisibility(false);
        }
    }


    public boolean SaveProve(DataOutputStream out) throws IOException {
        out.writeBoolean(true);
        GTerm t = condPane.getTerm();
        if (t == null)
            t = new GTerm();
        t.writeAterm2(out);
        MNode n = getmproveNode();
        if (n != null) {
            out.writeBoolean(true);
        } else {
            out.writeBoolean(false);
        }
        n.Save(out);
        int k = this.getSelectedIndex();
        if (gxInstance != null && !gxInstance.isPPanelVisible())
            k = -1;
        out.writeInt(k);
        return true;
    }

    public boolean LoadProve(DataInputStream in) throws IOException {
        in.readBoolean();
        GTerm t = new GTerm();
        if (CMisc.version_load_now < 0.037) {
            t.readAterm(in);
        } else {
            t.readAterm2(in);
        }

        condPane.setConstruction(t);
        if (t.getCons_no() > 0) {
            this.setSelectedIndex(0);
        }

        boolean r = in.readBoolean();
        int k = 0;
        if (r) {
            MNode n = new MNode();
            n.Load(in, /*gxInstance.*/ dp);
            this.loadMTree(n);
            k = in.readInt();
        }

        if (gxInstance != null) {
            if (k < 0)
                gxInstance.showppanel(true);
            else if ((t.getCons_no() > 0 || !tree_mp.isTreeEmpty())) {
                gxInstance.showppanel(false);
            }
        }
        return true;
    }

    public MNode getmproveNode() {

        if (tree_mp == null) {
            return null;
        }
        return (MNode) tree_mp.getRoot().getUserObject();
    }


    public JMenu getProveMenu() {
        return tbar.getProveMenu();
    }

    private void createGDDTreePanel() {

        top = new PTNode("", null);
        tree = new JTree(top);
        if (!drawStructure) {
            tree.putClientProperty("JTree.lineStyle", "Horizontal");
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.
                SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new BookCellRenderer(4));
        tree.setEditable(true);
        BookCellEditor editor = new BookCellEditor(4);

        MouseListener listener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                Object obj = e.getSource();
                if (obj instanceof ItemLabel) {
                    ItemLabel selectLabel = (ItemLabel) obj;
                    int t = selectLabel.getType();
                    if (t == 5) {
                        Object o = selectLabel.getUserObject();
                        if (dp.getPointList().size() != 0) {
                            if (o instanceof Cond) {
                                Cond c = (Cond) o;
                                Object objx = tree.getLastSelectedPathComponent();

                                RuleApplicationDialog dialog = null;

                                if (gxInstance != null)
                                    dialog = new RuleApplicationDialog(gxInstance, dpane, dp);
                                else dialog = new RuleApplicationDialog(dpane, dp);

                                if (lstDrawDialog != null)
                                    lstDrawDialog.setVisible(false);
                                lstDrawDialog = dialog;

                                dialog.LoadRule(c);
                                if (objx != null)
                                    dialog.setTitle(objx.toString());
                                dialog.setVisible(true);
                            }
                        }
                    } else if (t == 2 && e.getClickCount() > 1) {
                        Object o = selectLabel.getUserObject();
                        Cond c = (Cond) o;
//                        RuleListDialog dlg = new RuleListDialog(gxInstance);
                        RuleListDialog dlg = null;

                        if (gxInstance != null)
                            dlg = new RuleListDialog(gxInstance);
                        else dlg = new RuleListDialog();

                        if (lstRuleDialog != null)
                            lstRuleDialog.setVisible(false);
                        lstRuleDialog = dlg;

                        if (dlg.loadRule(0, c.getRule()))
                            dlg.setVisible(true);
                    } else if (t == 1) {
                        Var v = ((XTerm) selectLabel.getUserObject()).var;
                        if (null != v) {
                            {
                                dp.addFlashAngle(v.pt[0], v.pt[1], v.pt[2], v.pt[3]);
                            }
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };

        editor.addListenerToAllLabel(listener);
        tree.setCellEditor(editor);

        tree.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row < 0) {
                    tree.setSelectionRow(-1);
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

        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {

                if (top == tree.getLastSelectedPathComponent()) {
                    return;
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                Object obj = node.getUserObject();
                if (obj instanceof Cond) {
                    Cond co = (Cond) obj;
                    if (co == null) {
                        return;
                    }
                    setHighLightNode((PTNode) node);
                    flash_cond(co);
                } else if (obj instanceof LList) {
                    LList ls = (LList) obj;
                    dp.flashattr(ls, dpane);

                } else if (obj instanceof Rule) {
                    Rule r = (Rule) obj;
                    dp.flashattr(r, dpane);
                }
                tree.repaint();
            }
        });
        gddPanel = new JScrollPane(tree);
        this.addTab("D", null, gddPanel, GExpert.getLanguage("Deduction tree of the GDD method"));
    }

    private void createDatabaseTree() {

        top_db = new DefaultMutableTreeNode(getLanguage("Fixpoint"));
        tree_db = new JTree(top_db);
        ((DefaultTreeCellRenderer) tree_db.getCellRenderer()).setLeafIcon(null);
        tree_db.setFont(CMisc.fixFont);

        tree_db.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                TreePath path = tree_db.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    tree_db.setSelectionPath(path);
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        popcond.showMenu(tree_db.getLastSelectedPathComponent(), tree_db, e.getX(), e.getY());
                    }
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.
                            getLastPathComponent();
                    //drawProcess dp = gxInstance.dp;
                    dp.clearFlash();
                    if (node == null || top_db == node) {
                        return;
                    }
                    if (node.getParent() == top_db) {
                        dpane.repaint();
                        return;
                    }
                    CClass cc = (CClass) node.getUserObject();
                    if (cc == null) {
                        return;
                    }
                    flashattr(cc);
                } else {
                    tree_db.setSelectionRow(-1);
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        popcond.showMenu(null, tree_db, e.getX(), e.getY());
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
        });

        dbPanel = new JScrollPane(tree_db);

        this.addTab("Fix", null, dbPanel, GExpert.getLanguage("Show general properties that seem to be true"));
    }

    private void createFullTreePanel() {
        top_full = new DefaultMutableTreeNode("");

        DefaultTreeModel model = new DefaultTreeModel(top_full);
        tree_full = new JTree(model);
        tree_full.setCellRenderer(new BookCellRenderer(10));
        BookCellEditor editor = new BookCellEditor(10);

        MouseListener listener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                Object obj = e.getSource();
                if (obj instanceof ItemLabel) {
                    ItemLabel selectLabel = (ItemLabel) obj;
                    int t = selectLabel.getType();
                    if (t == 5) {
                        Object o = selectLabel.getUserObject();
                        if (dp.getPointList().size() != 0) {
                            if (o instanceof ElTerm) {
                                ElTerm el = (ElTerm) o;
                                Object objx = tree_full.getLastSelectedPathComponent();
//                                RuleApplicationDialog dialog = new RuleApplicationDialog(gxInstance, dpane, dp);
                                RuleApplicationDialog dialog = null;

                                if (gxInstance != null)
                                    dialog = new RuleApplicationDialog(gxInstance, dpane, dp);
                                else dialog = new RuleApplicationDialog(dpane, dp);

                                dialog.LoadRule(el);
                                if (objx != null)
                                    dialog.setTitle(objx.toString());
                                dialog.setVisible(true);
                            } else if (o instanceof Cond) {
                                Cond c = (Cond) o;
                                Object objx = tree_full.getLastSelectedPathComponent();
//                                RuleApplicationDialog dialog = new RuleApplicationDialog(gxInstance, dpane, dp);
                                RuleApplicationDialog dialog = null;

                                if (gxInstance != null)
                                    dialog = new RuleApplicationDialog(gxInstance, dpane, dp);
                                else dialog = new RuleApplicationDialog(dpane, dp);

                                dialog.LoadRule(c);
                                if (objx != null)
                                    dialog.setTitle(objx.toString());
                                dialog.setVisible(true);
                            }
                        }
                    } else if (t == 2 && e.getClickCount() > 1) {
                        Object o = selectLabel.getUserObject();
                        ElTerm el = (ElTerm) o;
//                        RuleListDialog dlg = new RuleListDialog(gxInstance);
                        RuleListDialog dlg = null;

                        if (gxInstance != null)
                            dlg = new RuleListDialog(gxInstance);
                        else dlg = new RuleListDialog();

                        if (dlg.loadRule(1, el.getEType()))
                            dlg.setVisible(true);
                    } else if (t == 1) {
                        // drawTextProcess dp = ((drawTextProcess) dp);
                        XTerm x = ((XTerm) selectLabel.getUserObject());
                        dp.addFlashXtermAngle(x);
                    }
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        Object o = selectLabel.getUserObject();
                        if (o != null &&
                                (o instanceof CClass || o instanceof Cond)) {
                            popcond.showMenu(selectLabel, selectLabel, e.getX(), e.getY());
                        }

                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };

        editor.addListenerToAllLabel(listener);
        tree_full.setCellEditor(editor);
        tree_full.setEditable(true);
        tree_full.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree_full.getLastSelectedPathComponent();
                //              drawTextProcess dp = gxInstance.dp;

                if (node == null) {
                    dp.clearFlash();
                    return;
                } else if (top_full == node) {
                    Object obj = node.getUserObject();
                    if (obj instanceof Cond) {
                        Cond co = (Cond) obj;
                        dp.flashCond(co, true);
                    }
                    return;
                }

                Object cc = node.getUserObject();
                if (cc == null)
                    return;
                Vector vp = dp.getPointList();
                if (vp.size() == 0)
                    return;

                if (cc instanceof GrTerm) {
                    dp.clearFlash();
                    GrTerm gr = (GrTerm) cc;
                    Vector list = gr.getAllxterm();

                    for (int i = 0; i < list.size(); i++) {
                        XTerm x = (XTerm) list.get(i);
                        dp.addFlashXtermAngle(x);
                    }
                } else if (cc instanceof ElTerm) {
                    dp.clearFlash();
                    ElTerm e1 = (ElTerm) cc;
                    Vector vl = e1.getAllCond();
                    for (int i = 0; i < vl.size(); i++) {
                        Cond c = (Cond) vl.get(i);
                        if (c.pred == Gib.CO_CYCLIC &&
                                dp.fd_circle(c.p[0], c.p[1]) == null &&
                                dp.fd_circle(c.p[1], c.p[2], c.p[3]) == null) {
                            dp.addCongFlash(c, false);
                        }
                    }
                    Vector list = e1.getAllxterm();
                    for (int i = 0; i < list.size(); i++) {
                        XTerm x = (XTerm) list.get(i);
                        dp.addFlashXtermAngle(x);
                    }
                    dpane.repaint();
                } else if (cc instanceof DTerm) {
                    dp.clearFlash();
                } else if (cc instanceof Cond) {
                    (dp).flashCond((Cond) cc, true);
                }

            }

        });

        tree_full.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                tree_full.setSelectionPath(tree_full.getPathForLocation(e.getX(), e.getY()));
            }

            public void mouseMoved(MouseEvent e) {
            }
        });

        tree_full.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    TreePath path = tree_full.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object obj = node.getUserObject();
                        if (obj instanceof Cond) {
                            popcond.show(tree_full, e.getX(), e.getY());
                        }
                    } else {
                        popFull pf = new popFull();
                        pf.show(tree_full, e.getX(), e.getY());
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


        });
        tree_full.addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent event) {
//                PanelProve1.this.setFullLabel1();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
//                PanelProve1.this.setFullLabel1();
            }

        });
        fullPanel = new JScrollPane(tree_full);
        this.addTab("F", null, fullPanel,  GExpert.getLanguage("Derivation for the Full Angle method"));
    }

    private void createMProvePanel(boolean mbar) {
        mpPanel = new JPanel();
        mpPanel.setLayout(new BoxLayout(mpPanel, BoxLayout.Y_AXIS));
        JScrollPane pane = new JScrollPane((tree_mp = new MProveTree(gxInstance, dpane, dp)),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        pane.setBackground(Color.white);
        pane.setForeground(Color.white);

        mpPanel.add(pane);

        MDrObj.createAllIcons();

        if (mbar)
            mpPanel.add((inputm = new MProveInputPanel(gxInstance, dpane, dp, tree_mp)));
//        this.addTab("M", mpPanel);
        mpPanel.setBackground(Color.white);
        mpPanel.setForeground(Color.white);
    }

    public void loadMTree(MNode n) {
        tree_mp.loadmtree(n);
        if (tree_mp != null && !tree_mp.isTreeEmpty() && this.indexOfComponent(mpPanel) != -1)
            this.setSelectedComponent(mpPanel);
    }

    public MProveInputPanel getmInputPanel() {
        return inputm;
    }

    public FactFinderDialog getFactFinderDialog() {
        if (fdialog == null)
            fdialog = new FactFinderDialog(gxInstance, 0, getLanguage("Search facts"));

        gxInstance.centerDialog(fdialog);
        fdialog.setPoints(dp.getPointList());
        return fdialog;
    }

    public void mstop() {
        Component cm = this.getSelectedComponent();
        if (cm == mpPanel) {
            tree_mp.stepEnd();
        } else if (cm == fullPanel) {
            tree_full.setSelectionRow(-1);
            findex = -1;
        } else if (cm == gddPanel) {
            tree.setSelectionRow(-1);
            gindex = -1;
        }

    }

    public boolean isStepAtEnd() {
        Component cm = this.getSelectedComponent();
        if (cm == mpPanel) {
            return tree_mp.isMStepEnd();
        } else if (cm == fullPanel) {
            return findex <= 0;
        } else if (cm == gddPanel) {
            return gindex <= 0;
        }
        return true;
    }

    public boolean isStepAtMid() {
        Component cm = this.getSelectedComponent();
        if (cm == mpPanel) {
            if (!tree_mp.isTreeEmpty())
                return tree_mp.isMStepMid();
        } else if (cm == fullPanel) {
            if (!(top_full != null && top_full.getChildCount() <= 1))
                return findex > 0;
        } else if (cm == gddPanel) {
            if (!(top_db != null && top_db.getChildCount() <= 1))
                return gindex > 0;
        }
        return true;
    }

    public boolean hasProof() {
        Component cm = this.getSelectedComponent();
        if (cm == mpPanel) {
            if (tree_mp.isTreeEmpty())
                return false;
            return true;
        } else if (cm == fullPanel) {
            if (top_full != null && top_full.getChildCount() <= 1)
                return false;
            return true;
        } else if (cm == gddPanel) {
            if (top_db != null && top_db.getChildCount() <= 1)
                return false;
            return true;
        }
        return false;
    }

    public void setProofStatus(int n) {

        if (n <= 0 || n >= this.getTabCount())
            return;


        if (n == 1) { // Full
            this.proveFull();

        } else if (n == 2) { // GDD.
            this.proveGdd();

        }
        this.setSelectedIndex(n);
    }

    public boolean mstep() {
        Component cm = this.getSelectedComponent();
        if (cm == mpPanel) {
            if (tree_mp.isTreeEmpty())
                return false;
            tree_mp.step();
            return true;
        } else if (cm == fullPanel) {
            if (top_full != null && top_full.getChildCount() <= 1)
                return false;
            full_step();
            return true;
        } else if (cm == gddPanel) {
            if (top != null && top.getChildCount() <= 1)
                return false;
            gdd_step();
            return true;
        }
        return false;
    }

    public void m_runtobegin() {
        Component cm = this.getSelectedComponent();

        if (cm == mpPanel) {
            tree_mp.run_to_begin();
        } else if (cm == fullPanel) {
            int n = tree_full.getRowCount() - 1;
            if (n >= 0) {
                tree_full.setSelectionRow(0);
                findex = 0;
            }
        } else if (cm == gddPanel) {
            int n = tree.getRowCount() - 1;
            if (n >= 0) {
                tree.setSelectionRow(0);
                gindex = 0;
            }
        }

    }

    public void m_runtoend() {
        Component cm = this.getSelectedComponent();

        if (cm == mpPanel) {
            tree_mp.run_to_end();
        } else if (cm == fullPanel) {
            int n = tree_full.getRowCount() - 1;
            if (n >= 0) {
                tree_full.setSelectionRow(n);
                findex = n;
            }
        } else if (cm == gddPanel) {
            int n = tree.getRowCount() - 1;
            if (n >= 0) {
                tree.setSelectionRow(n);
                gindex = n;
            }
        }

    }


    public void gdd_step() {
        if (gindex > tree.getRowCount()) {
            tree.setSelectionRow(0);
            gindex = 0;
        } else {
            tree.setSelectionRow(gindex);
            gindex++;
        }
    }

    public void full_step() {
        if (top_full != null) {
            if (findex < 0) {
                tree_full.setSelectionRow(0);
                findex = 0;
            } else if (findex >= top_full.getChildCount()) {
                findex = -1;
                dp.UndoAdded("Aux added");
            } else {
                boolean find = false;
                while (!find) {
                    TreeNode node = top_full.getChildAt(findex);
                    for (int i = 0; i < tree_full.getRowCount(); i++) {
                        Object nd2 = tree_full.getPathForRow(i).
                                getLastPathComponent();
                        if (nd2 == node) {
                            tree_full.setSelectionRow(i);
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        findex++;
                    }
                }
            }
        }

    }

    public boolean selectAPoint(CPoint p) {
        if (p == null) return false;
        if (cdialog != null && cdialog.isVisible()) {
            cdialog.selectAPoint(p);
            return true;
        }
        if (fdialog != null && fdialog.isVisible()) {
            fdialog.selectAPoint(p);
            return true;
        }

        if (inputm != null && this.getSelectedComponent() == mpPanel) {
            return inputm.selectAPoint(p);
        }
        return false;
    }

    private void createAllPopupMenu() {
        popcond = new popMenu();
    }

    public void addConclusion(String s) {
        if (!condPane.checkValid()) {
            return;
        }

        if (gxInstance != null)
            gxInstance.setActionSelect();

        if (cdialog == null) {
            cdialog = new ConcDialog(gxInstance, getLanguage("Add Conclusion"));
            cdialog.setTitle(s);
        }
        cdialog.setPoints(condPane.getAllPts());
        cdialog.showDialog(s);
    }

    public void proveWu() {

        if (wuPanel == null) {
            wuPanel = new PanelWu(dp, new WuTextPane());
            if (gxInstance != null) {
                wuPanel.setLanguage(gxInstance.getLan());
                wuPanel.setXInstance(gxInstance);
            }
            this.addTab("Wu", null, wuPanel, GExpert.getLanguage("Algebraic computations for Wu's method"));
        }
        if (!wuPanel.isRunning()) {
            this.setSelectedComponent(wuPanel);
            wuPanel.prove(condPane.getTerm());
        }
    }

    public void proveGB() {
        if (gbPanel == null) {
            gbPanel = new PanelGB(dp, new WuTextPane());
            gbPanel.setXInstance(gxInstance);
            this.addTab("GB", null, gbPanel, GExpert.getLanguage("Algebraic computations for the Groebner basis method"));
        }
        if (!gbPanel.isRunning()) {
            this.setSelectedComponent(gbPanel);
            gbPanel.prove(condPane.getTerm(), dp);
        }
    }

    public void proveArea() {
    }

    public void showTime(long n) {
        float t = (float) (n / 1000.0);
        if (gxInstance != null)
            gxInstance.setTextLabel2(getLanguage("Time:") + " " + t + " " + getLanguage("second(s)"));
             //       + ";" );//+ getLanguage(10001, "  Facts") + ": " + Prover.getNumberofProperties());
    }

    public void showDatabase() {
        if (!check_construction_finished()) return;

        if (dbPanel == null) {
            createDatabaseTree();
        }
        if (top_db != null) {
            top_db.removeAllChildren();
        }
        ((DefaultTreeModel) tree_db.getModel()).reload();
        GTerm gt = condPane.getTerm();
        if (gt == null) return;
        Prover.set_gterm(gt);
        gprover.setFix();
        gprover.start();

        is_database_updated = false;
        this.setSelectedComponent(dbPanel);
        return;
    }

    public boolean isProverRunning() {
        return gprover != null && gprover.isRunning() ||
                gbPanel != null && gbPanel.isRunning() ||
                wuPanel != null && wuPanel.isRunning();
    }

    public void showFullGIB() {
        if (dbPanel == null) {
            createDatabaseTree();
        }
        if (top_db != null) {
            top_db.removeAllChildren();
        }
        ((DefaultTreeModel) tree_db.getModel()).reload();
        Prover.setGIB();
        displayDatabase(0);
        is_database_updated = true;
        this.setSelectedComponent(dbPanel);
    }

    public void displayDatabase(long t) {

        is_database_updated = true;
        if (top_db != null) {
            top_db.removeAllChildren();
        }

        GDDBc db = Prover.get_gddbase();
        if (db == null)
            return;

        showTime(t);
        db.gen_dbase_text();
        int size;

        Vector v1 = db.getAll_ln();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("lines") + " (" + size + ")", v1);
        }

        v1 = db.getAll_pn();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("parallel lines") + " (" + size + ")", v1);
        }

        v1 = db.getAll_tn();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("perpendicular lines") + " (" + size + ")", v1);
        }

        v1 = db.getAll_md();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("midpoints") + " (" + size + ")", v1);
        }

        v1 = db.getAll_cir();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("circles") + " (" + size + ")", v1);
        }

        v1 = db.getAll_cg();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("congruent segments") + " (" + size + ")", v1);
        }


        v1 = db.getAll_as();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("congruent angles") + " (" + size + ")", v1);
        }

//        v1 = db.getAll_rg();
//        size = v1.size();
//        if (size != 0) {
//            add_predicates("ratio segments (" + size + ")", v1);
//        }

        v1 = db.getAll_sts();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("similiar triangles") + " (" + size + ")", v1);
        }
        v1 = db.getAll_cts();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("congruent triangles") + " (" + size + ")", v1);
        }


        v1 = db.getAll_at();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("special angles") + " (" + size + ")", v1);
        }
        v1 = db.getAll_atn();
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("perp-acong angles") + " (" + size + ")", v1);
        }

        v1 = db.getAll_rg();
        Vector v2 = db.getAll_ra();
        v1.addAll(v2);
        size = v1.size();
        if (size != 0) {
            add_predicates(getLanguage("ratio segments") + " (" + size + ")", v1);
        }
        ((DefaultTreeModel) tree_db.getModel()).reload();

        tree_db.expandRow(0);
    }

    public void show_AllFullAux(boolean ang) {
        //     drawTextProcess dp = gxInstance.dp;
        DrawData.setAuxStatus();
        DefaultMutableTreeNode node = top_full;
        if (node == null) {
            node = top;
            if (node == null)
                return;
        }
        if (top_full.getChildCount() == 0)
            node = top;
        if (node.getChildCount() == 0) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("Can not find any proof!"),
                    getLanguage("Warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vector v = new Vector();
        v.add(node);
        int s1 = dp.linelist.size();
        int s2 = dp.circlelist.size();
        int s3 = dp.anglelist.size();

        while (v.size() != 0) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) v.remove(0);
            for (int j = 0; j < n.getChildCount(); j++) {
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode) n.
                        getChildAt(j);
                v.add(ch);
            }
            Object obj = n.getUserObject();
            if (obj instanceof GrTerm) {
                GrTerm g = (GrTerm) obj;
                Vector v1 = g.getAllvars();
                for (int i = 0; i < v1.size(); i++) {
                    Var vr = (Var) v1.get(i);
                    CLine ln1 = dp.addLn(vr.pt[0], vr.pt[1]);
                    CLine ln2 = dp.addLn(vr.pt[2], vr.pt[3]);
                    CPoint p1 = dp.fd_point(vr.pt[0]);
                    CPoint p2 = dp.fd_point(vr.pt[1]);
                    CPoint p3 = dp.fd_point(vr.pt[2]);
                    CPoint p4 = dp.fd_point(vr.pt[3]);
                    if (ang && dp.fd_angle_4p(p1, p2, p3, p4) == null) {
                        CAngle ag = new CAngle(ln1, ln2, p1, p2, p3, p4);
                        ag.setAngleText("");
                        dp.addAngleToList2(ag);
                    }
                }
            } else if (obj instanceof ElTerm) {
                ElTerm el = (ElTerm) obj;
                Vector v1 = el.getAllxterm();
                for (int i = 0; i < v1.size(); i++) {
                    XTerm x = (XTerm) v1.get(i);
                    Var vr = x.var;
                    if (vr == null) {
                        continue;
                    }

                    CLine ln1 = dp.addLn(vr.pt[0], vr.pt[1]);
                    CLine ln2 = dp.addLn(vr.pt[2], vr.pt[3]);
                    CPoint p1 = dp.fd_point(vr.pt[0]);
                    CPoint p2 = dp.fd_point(vr.pt[1]);
                    CPoint p3 = dp.fd_point(vr.pt[2]);
                    CPoint p4 = dp.fd_point(vr.pt[3]);
                    if (ang && dp.fd_angle_4p(p1, p2, p3, p4) == null) {
                        CAngle ag = new CAngle(ln1, ln2, p1, p2, p3, p4);
                        ag.setAngleText("");
                        dp.addAngleToList2(ag);
                    }
                }

            } else if (obj instanceof Cond) {
                //gxInstance.dp.addCondAux((cond) obj, true);
            }
        }
        int mm1, mm2, mm3;
        mm1 = s1;
        mm2 = s2;
        mm3 = s3;

        s1 = dp.linelist.size() - s1;
        s2 = dp.circlelist.size() - s2;
        s3 = dp.anglelist.size() - s3;

        int n = s1 + s2 + s3;
        int k = n - 6;
        int t = 0;
        if (k >= s3) {
            k = k - s3;
            t = s3;
        } else {
            t = k;
            k = 0;
        }
        dp.removeFromeListLastNElements(dp.anglelist, t);

        if (k >= s1) {
            k = k - s1;
            t = s1;
        } else {
            t = k;
            k = 0;
        }
        dp.removeFromeListLastNElements(dp.linelist, t);
        if (k >= s2) {
            k = k - s2;
            t = s2;
        } else {
            t = k;
            k = 0;
        }
        dp.removeFromeListLastNElements(dp.circlelist, t);

        int a1 = dp.linelist.size() - mm1;
        int a2 = dp.circlelist.size() - mm2;
        int a3 = dp.anglelist.size() - mm3;

        JOptionPane.showMessageDialog(gxInstance,
                "Found:  \n" + "Aux Line(s) :   " + s1 + ",     Aux Circle(s)   " + s2 +
                        ",      Aux Angle(s) " + s3 +
                        "\nConstructed:  \n" + "Aux Line(s) :       " + a1 + ",    Aux Circle(s):    " + a2 +
                        ",      Aux Angle(s) " + a3);

        dp.UndoAdded("Aux");
        dpane.repaint();
    }

    public GTerm getConstructionTerm() {
        GTerm gt = condPane.getConstruction();
        if (gt == null || gt.getCons_no() == 0) {
            this.generate();
        }
        return condPane.getConstruction();
    }

    public void generate() {
        //drawProcess dp = gxInstance.dp;
        Vector v = dp.getConstructionFromDraw();
        int n = dp.getPointSize();
        condPane.setVector(v);
        if (0 == dp.getRedolistSize())
            condPane.addConclusion();

    }

    String getLanguage(String s) {
        return GExpert.getLanguage(s);
        /*
        if (gxInstance != null)
            return gxInstance.getLanguage(s);
        return s;
         */
    }

    public void drawConstruction() {
        if (dp.inConstruction()) {
            dp.SetCurrentAction(DrawProcess.CONSTRUCT_FROM_TEXT);
            return;
        }

        this.setSelectedIndex(0);
        GTerm gt = condPane.getTerm();
        if (gt == null) {
            return;
        }
        dp.setConstructLines(gt);
        dp.SetCurrentAction(DrawProcess.CONSTRUCT_FROM_TEXT);
        dpane.repaint();

    }

    public void proveGdd() {
        if (gddPanel == null) {
            this.createGDDTreePanel();
        }

        // Override conclusion from GGB import:
        if (GExpert.conclusion != null) {
            gxInstance.getpprove().set_conclusion(GExpert.conclusion, true);
        }

        GTerm gt = condPane.getTerm();
        if (gt == null || !gt.hasConclusion()) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("No conclusion has been set!"),
                    getLanguage("No conclusion"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        top.setUserObject("");
        top.removeAllChildren();
        tree.cancelEditing();
        ((DefaultTreeModel) tree.getModel()).reload();
        tree.setVisible(false);

        Prover.set_gterm(gt);

        boolean threaded = false; // temporary change because of command-line
        // Threaded version:
        if (threaded) {
            gprover.setProve();
            gprover.start();
        } else {
            // Non-threaded version:
            boolean t = Prover.prove();
            displayGDDProve(t);
        }
    }

    public void displayGDDProve(boolean t) {


        LList ls = null;

        if (t) {
            addGddProveTree(Prover.getProveHead());
            this.setSelectedComponent(gddPanel);
            AuxPt p = Prover.getConstructedAuxPoint();
            if (p != null) {
                dp.addAuxPoint(p);
                int xt = p.getAux();
                // TODO: Translate these strings as well:
                String s = "An auxiliary point (point " + p.getConstructedPoint() + ") is constructed by A" + xt;
                JOptionPane.showMessageDialog(gxInstance, s + "\n" + p, "Auxiliary Point Constructed", JOptionPane.INFORMATION_MESSAGE);
                gxInstance.setActionTip("Auxiliary Points", p.toString());
            }
        } else if ((ls = Prover.getProveHead_ls(null)) != null) {
            addGddProveTree_ls(ls);
            this.setSelectedComponent(gddPanel);
        } else {
            String s = "Failed to prove this theorem with Deductive Database Method.";
            s = this.getLanguage(s);
            if (gxInstance != null)
                gxInstance.setTextLabel2(s);

            JOptionPane.showMessageDialog(gxInstance, s, getLanguage("Message"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean check_construction_finished() {
        GTerm gt = condPane.getTerm();
        if (gt == null) return false;

        int n1 = condPane.getTerm().getPointsNum();
        int n2 = dp.getPointSize();

        if (n2 < n1 && dp.getPt(n1) == null) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("Please construct the diagram first!"),
                    getLanguage("Diagram not completed"), JOptionPane.WARNING_MESSAGE);
            return false;

        } else return true;
    }

    public void prove() {
        if (!check_construction_finished()) return;

        String s1 = tbar.getProveMethodSelected();
        if (s1.equals("GDD")) {
            proveGdd();
        } else if (s1.equals("Full")) {
            proveFull();
        } else if (s1.equals("Area")) {
            proveArea();
        } else if (s1.equals("Wu"))
            proveWu();
        else if (s1.equals("GB"))
            proveGB();
    }

    public void proveCond(Cond co, boolean n) {

        if (gddPanel == null)
            this.createGDDTreePanel();
        if (co.pred == 0) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("No conclusion has been set!"),
                    getLanguage("No conclusion"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (true/*!pv.isfixed()*/) {
            Prover.fixpoint(condPane.getTerm());
        }
        if (Prover.prove(co)) {
            Cond c = Prover.getProveHead();
            int t = c.get_conc_type();
            if (t != 1 && t != 2) {
                if (n) {
                    addGddProveTree(c);
                } else {
                    TreePath path = tree_full.getSelectionPath();
                    if (path != null) {
                        Object obj = path.getLastPathComponent();
                        if (obj instanceof DefaultMutableTreeNode) {
                            DefaultMutableTreeNode node = (
                                    DefaultMutableTreeNode) obj;
                            node.removeAllChildren();
                            this.createNodes(c, node);
                            return;
                        }
                    }
                }
                this.setSelectedComponent(gddPanel);
            } else {
                if (t == 1) {
                    JOptionPane.showMessageDialog(gxInstance,
                            // c.toString() + getLanguage(1010, "is trivially true"),
                            GExpert.getTranslationViaGettext("{0} is trivially true", c.toString()),
                            getLanguage("Message"),
                            JOptionPane.
                                    INFORMATION_MESSAGE);
                } else if (t == 2) {
                    JOptionPane.showMessageDialog(gxInstance,
                            // c.toString() + getLanguage(1011, " is obviously true  (by hypothesis)"),
                            GExpert.getTranslationViaGettext("{0} is obviously true (by hypothesis)", c.toString()),
                            getLanguage("Message"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(gxInstance,
                    getLanguage("Can not prove this theorem"),
                    getLanguage("Information"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public boolean proveFull() {
        if (fullPanel == null) {
            this.createFullTreePanel();
        }
        GTerm t = condPane.getTerm();
        if (t == null || !t.hasConclusion()) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("No conclusion has been set!"),
                    getLanguage("No conclusion"),
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        GrTerm gt = Prover.proveFull(t);
        int nx = Prover.getPFullResult();

        if (nx == 1) {
            Object[] options = {getLanguage("Show Detail"), getLanguage("OK")};
            int n1 = Prover.getErrorType();
            String s = getLanguage("Can not prove this theorem");
            int n = JOptionPane.showOptionDialog(null, s, getLanguage("Failed"),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            if (n == 1)
                return false;
        } else if (nx == 2) {
            JOptionPane.showMessageDialog(gxInstance, getLanguage("The conclusion can not be translated to " +
                    "full-angle expression."), getLanguage("Not Supported"), JOptionPane.WARNING_MESSAGE);
            return false;
        }

        this.setSelectedComponent(fullPanel);
        Cond c = Prover.getFullconc();
        top_full.removeAllChildren();
        tree_full.cancelEditing();
        ((DefaultTreeModel) (tree_full.getModel())).reload();

        c.setCondToBeProveHead();
        top_full.setUserObject(c);

        while (gt != null) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(gt);
            if (gt.el != null && (gt.getPTN() != 0 || gt.el.etype != Gib.RF_ADDITION)) {
                DefaultMutableTreeNode n1 = new DefaultMutableTreeNode(gt.el);
                Cond co = gt.el.co;
                while (co != null) {
                    Prover.showCondTextF(co);
                    DefaultMutableTreeNode n2 = new DefaultMutableTreeNode(co);
                    n1.add(n2);
                    co = co.nx;
                }
                ElTerm e1 = gt.el.et;
                addElmToNode(n1, e1);
                top_full.add(n1);
            } else if (gt.ps != null) {
                DefaultMutableTreeNode n1 = new DefaultMutableTreeNode(gt.ps);
                node.add(n1);
            }
            top_full.add(node);
            gt = gt.nx;
        }
        tree_full.expandRow(0);


        return true;
    }

    public void addElmToNode(DefaultMutableTreeNode node, ElTerm e1) {
        while (e1 != null) {
            DefaultMutableTreeNode n2 = new DefaultMutableTreeNode(e1);
            node.add(n2);
            Cond co = e1.co;
            while (co != null) {
                DefaultMutableTreeNode n3 = new DefaultMutableTreeNode(co);
                n2.add(n3);
                co = co.nx;
            }
            ElTerm e2 = e1.et;
            addElmToNode(n2, e2);
            e1 = e1.nx;
        }

    }

    /*
    public void saveConstruction() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showDialog(gxInstance, "Save");
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            FileOutputStream fp;

            try {
                if (f.exists()) {
                    f.delete();
                    fp = new FileOutputStream(f, true);
                } else {
                    f.createNewFile();
                    fp = new FileOutputStream(f, false);
                }
                if (fp == null) {
                    return;
                }
                gterm t = condPane.getTerm();
                t.writeAterm(fp);
                fp.close();
            } catch (IOException ee) {
            }

        }

    }
     */ // Seemingly unused. Remove.


    private void expandOrCollapse() {
        Component comp = this.getSelectedComponent();
        if (comp == gddPanel) {
            expandOrCollpaseTree(tree);
        } else if (comp == dbPanel) {
            expandOrCollpaseTree(tree_db);
        } else if (comp == fullPanel) {
            expandOrCollpaseTree(tree_full);
        }
    }

    private void expandOrCollpaseTree(JTree tree) {
        if (tree == null) {
            return;
        }
        boolean exp = false;
        int row = tree.getRowCount();
        for (int i = 1; i < row; i++) {
            if (tree.isExpanded(i)) {
                exp = true;
                break;
            }
        }
        if (exp) {
            for (int i = tree.getRowCount(); i > 0; i--) {
                tree.collapseRow(i);
            }
        } else {
            for (int i = 1; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        }
    }

    // TODO: Add this as an option.
    boolean drawStructure = true; // set this to false to get original behavior
    public static String graphvizProgram = "";
    public static String hypotheses = "";
    GraphvizBuilder gb; // The global Graphviz object that can be appended.
    HashSet<Node> nodes;

    class Pair {
        public Node from;
        public Node to;
        public Pair(Node from, Node to)
        {
            this.from = from;
            this.to = to;
        }
        public boolean equals(Object o) {
            return o.hashCode() == this.hashCode();
        }
        public int hashCode() {
            return (from + "->" + to).hashCode();
        }
    }
    HashSet<Pair> edges;
    JDialog frame;

    private void createNodes(Cond co, DefaultMutableTreeNode to) {

        PTNode node = null;
        node = new PTNode(co.getNo() + ". " + co.getText(), co);
        node.tlevel = true;
        to.add(node);

        createSubNode(node, co, co);
        if (!drawStructure && co.nx != null) {
            createNodes(co.nx, to);
        }

    }

    String COLOR_TRIVIAL = "#FFA0A0", // light red
        COLOR_PARALLEL_LINES = "#C0FFC0", // light green
        COLOR_PERPENDICULAR_LINES = "#C0C0FF", // light blue
        COLOR_CIRCLES = "#FFFFC0", // light yellow
        COLOR_ANGLES = "#AE7439", // light orange
        COLOR_TRIANGLES = "#C0FFFF", // light cyan
        COLOR_OTHER = "#C0C0C0", // grey
        COLOR_HYPOTHESIS = "#F7CAC9"; // pink

    String FORM_TRIVIAL = "box",
            FORM_PARALLEL_LINES = "invtrapezium",
            FORM_PERPENDICULAR_LINES = "rectangle",
            FORM_CIRCLES = "oval",
            FORM_ANGLES = "diamond",
            FORM_TRIANGLES = "invtriangle",
            FORM_OTHER = "hexagon",
            FORM_HYPOTHESIS = "oval";

    String STYLE_TRIVIAL = "rounded, filled",
            STYLE_PARALLEL_LINES = "filled",
            STYLE_PERPENDICULAR_LINES = "filled",
            STYLE_CIRCLES = "filled",
            STYLE_ANGLES = "filled",
            STYLE_TRIANGLES = "filled",
            STYLE_OTHER = "filled",
            STYLE_HYPOTHESIS = "filled";


    int getRule(Cond co) {
        int rule = co.getRule();
        if (rule == 0) {
            co.getRuleFromeFacts(); // maybe it's not filled yet
            rule = co.getRule();
            if (rule > 43) { // this may be some error
                rule = 0;
                co.setRule(rule); // try to fix it
            }
        }
        return rule;
    }

    String setNode(Cond co) {
        int rule = getRule(co);
        // System.out.println("GV: Rule " + rule + " is used for node " + co.getNo());
        String c = getRuleColor(rule);
        String f = getRuleForm(rule);
        String s = getRuleStyle(rule);
        String ret;
        ret = co.getNo() + " [ label = <" + co.getNo() + ") " + co.getText();
        if (rule >= 1 && rule <= 43) {
            ret += "<BR/>\n" + "<FONT POINT-SIZE=\"10\">"
                    + GExpert.getTranslationViaGettext("Rule {0}", rule + "")
                    + "</FONT>";
        }
        ret += ">";
        if (rule >= 1 && rule <= 43) {
            GRule r = (GRule) RuleList.GDDLIST.get(rule);
            String exstring = r.exstring;
            String description = r.description;
            ret += ", tooltip = \""
                    + getLanguage(description);
            if (exstring != null) {
                ret += "\\n" + getLanguage(exstring);
            }
            ret += "\"";
        }
        else {
            ret += ", tooltip = \" \""; // do not show any tooltip
        }
        ret += ", style = \"" + s + "\", shape = " + f + ", fillcolor = \"" + c + "\"];\n";
        return ret;
    }

    /**
     * Create a Graphviz node from a cond.
     * @param co
     * @return
     */
    Node graphvizNode(Cond co) {
        int rule = getRule(co);
        // System.out.println("GRAPHPER: Rule " + rule + " is used for node " + co.getNo());
        org.graphper.api.attributes.Color c;
        org.graphper.api.attributes.NodeShape s = NodeShapeEnum.RECT;
        c = org.graphper.api.attributes.Color.ofRGB(getRuleColor(rule));
        Node n = Node.builder().label(co.getNo() + ") " + co.getText()).fillColor(c).shape(s).build();
        return n;
    }

    String getRuleColor(int rule) {
        if (rule == 0)
            return COLOR_TRIVIAL;
        if (rule >= 1 && rule <= 4) {
            return COLOR_PARALLEL_LINES;
        }
        if (rule >= 5 && rule <= 8) {
            return COLOR_PERPENDICULAR_LINES;
        }
        if (rule >= 9 && rule <= 15) {
            return COLOR_CIRCLES;
        }
        if (rule >= 16 && rule <= 22) {
            return COLOR_ANGLES;
        }
        if (rule >= 23 && rule <= 37) {
            return COLOR_TRIANGLES;
        }
        if (rule >= 38) {
            return COLOR_OTHER;
        }
        return "#ffffff"; // this should not happen
    }

    String getRuleForm(int rule) {
        if (rule == 0)
            return FORM_TRIVIAL;
        if (rule >= 1 && rule <= 4) {
            return FORM_PARALLEL_LINES;
        }
        if (rule >= 5 && rule <= 8) {
            return FORM_PERPENDICULAR_LINES;
        }
        if (rule >= 9 && rule <= 15) {
            return FORM_CIRCLES;
        }
        if (rule >= 16 && rule <= 22) {
            return FORM_ANGLES;
        }
        if (rule >= 23 && rule <= 37) {
            return FORM_TRIANGLES;
        }
        if (rule >= 38) {
            return FORM_OTHER;
        }
        return "box"; // this should not happen
    }

    String getRuleStyle(int rule) {
        if (rule == 0)
            return STYLE_TRIVIAL;
        if (rule >= 1 && rule <= 4) {
            return STYLE_PARALLEL_LINES;
        }
        if (rule >= 5 && rule <= 8) {
            return STYLE_PERPENDICULAR_LINES;
        }
        if (rule >= 9 && rule <= 15) {
            return STYLE_CIRCLES;
        }
        if (rule >= 16 && rule <= 22) {
            return STYLE_ANGLES;
        }
        if (rule >= 23 && rule <= 37) {
            return STYLE_TRIANGLES;
        }
        if (rule >= 38) {
            return STYLE_OTHER;
        }
        return "filled"; // this should not happen
    }

    /**
     * Create a Graphviz node from a string.
     * @param st
     * @return
     */
    Node graphvizNode(String st) {
        org.graphper.api.attributes.Color c = org.graphper.api.attributes.Color.ofRGB(COLOR_HYPOTHESIS);
        org.graphper.api.attributes.NodeShape s = NodeShapeEnum.ELLIPSE;
        Node n = Node.builder().label(st).fillColor(c).shape(s).build();
        return n;
    }

    /* Search for a numbered condition in the main tree. */
    private Cond searchSubCond(Cond co, int no) {
        while (co.nx != null && co.nx.getNo() != no) {
            co = co.nx;
        }
        return co.nx;
    }

    private void addLine(Node from, Node to) {
        Pair p = new Pair(from, to);
        if (!edges.contains(p)) {
            gb.addLine(to, from).rankdir(Rankdir.BT).build();
            edges.add(p);
        }
    }

    /**
     * Create a Graphviz node from cond if it does not exist. If it does, return the node.
     */
    private Node getGraphvizNode(Cond co) {
        for (Node n : nodes) {
            if (n.nodeAttrs().getLabel().equals(co.no + ") " + co.toString())) {
                return n;
            }
        }
        Node n = graphvizNode(co);
        nodes.add(n);
        return n;
    }

    /**
     * Create a Graphviz node from string if it does not exist. If it does, return the node.
     */
    private Node getGraphvizNode(String s) {
        for (Node n : nodes) {
            if (n.nodeAttrs().getLabel().equals(s)) {
                return n;
            }
        }
        Node n = graphvizNode(s);
        nodes.add(n);
        return n;
    }

    private void createSubNode(DefaultMutableTreeNode node, Cond co, Cond root) {

        if (co.vlist == null) {
            return;
        }

        for (int i = 0; i < co.vlist.size(); i++) {
            Cond c = (Cond) co.vlist.get(i);
            int num = c.getNo();
            String st;
            if (num != 0) {
                st = c.getNo() + ". " + c.getText();
                // We put the connection between co and c in the GraphViz output:
                if (drawStructure) {
                    // It is possible to draw something on the arrow... but what? TODO...
                    // To avoid multiple edges, we already set "strict".
                    graphvizProgram += co.getNo() + " -> " + c.getNo() + ";\n";

                    Node from = getGraphvizNode(co);
                    Node to = getGraphvizNode(c);
                    addLine(from, to);
                }
            } else {
                st = c.getText();
                if (drawStructure) {
                    // This is a leaf without numbering, we need a label. Let's use its text:
                    graphvizProgram += co.getNo() + " -> \"" + st + "\";\n";

                    Node from = getGraphvizNode(co);
                    Node to = getGraphvizNode(st);
                    addLine(from, to);

                    // This may duplicate some entries, FIXME:
                    hypotheses += "\"" + st + "\" [ fillcolor = \"" + COLOR_HYPOTHESIS
                            + "\", shape = " + FORM_HYPOTHESIS + ", style = \"" + STYLE_HYPOTHESIS
                            + "\", tooltip = \" \" ];\n";
                }
            }
            Cond leaf = searchSubCond(root, num);
            DefaultMutableTreeNode nd;
            if (!drawStructure || leaf == null) {
                nd = new PTNode(st, c);
            } else {
                nd = new PTNode(st, leaf);
            }

            node.add(nd);
            if (drawStructure && num != 0) {
                if (leaf != null) {
                    createSubNode(nd, leaf, root);
                }
            }
        }
    }

    public void addGddProveTree_ls(LList ls) {
        top.removeAllChildren();
        tree.cancelEditing();
        top.setUserObject(GExpert.getLanguage("To Prove:") + " " + ls);
        ((DefaultTreeModel) (tree.getModel())).reload();

        while (ls != null) {
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(ls);
            top.insert(n, 0);
            Rule r = ls.rl[0];
            if (r != null) {
                DefaultMutableTreeNode n1 = new DefaultMutableTreeNode(r);
                top.insert(n1, 0);
            }
            ls = ls.fr;
        }

        tree.expandRow(0);
        int n = top.getChildCount();
        for (int i = n; i > 0; i--) {
            tree.expandRow(i);
        }
        tree.setVisible(true);
    }

    public void addGddProveTree(Cond co) {
        top.removeAllChildren();
        tree.cancelEditing();

        top.setUserObject(GExpert.getLanguage("To Prove:") + " " + co);

        ((DefaultTreeModel) (tree.getModel())).reload();

        // initialize
        gb = Graphviz.digraph();
        nodes = new HashSet<>();
        edges = new HashSet<>();

        if (drawStructure) {
            // We don't want multiple edges:
            graphvizProgram = "strict digraph \" \" {\n";
            // We create yellow boxes:
            graphvizProgram += "node [shape = box, color = black, style = filled];\n";
            // We set the direction for the arrows reversed:
            graphvizProgram += "edge [dir = back, tooltip = \" \"];\n";
            // Reset hypotheses:
            hypotheses = "";
        }

        createNodes(co, top);

        if (drawStructure) {
            // At the end of the GraphViz file we give a detailed definition for each
            // step of the proof:
            while (co.nx != null) {
                graphvizProgram += setNode(co);
                co = co.nx;
            }
            // For the last node we add the missing information:
            graphvizProgram += setNode(co);
            graphvizProgram += hypotheses;
            graphvizProgram += "}\n";
        }

        tree.expandRow(0);

        int n = top.getChildCount();
        for (int i = n; i > 0; i--) {
            tree.expandRow(i);
        }
        tree.setVisible(true);

        if (drawStructure) {

            // Expand really all nodes:
            for (int i = 1; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }

            int w = 1000, h = 800;
            if (frame == null || !frame.isValid())
            try {
                Graphviz graphviz = gb.build();
                String svgString = graphviz.toSvgStr();
                JSVGCanvas svg = new JSVGCanvas();
                StringReader reader = new StringReader(svgString);
                String uri = "file:make-something-up"; // dummy uri
                String parser = XMLResourceDescriptor.getXMLParserClassName();
                SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
                SVGDocument doc = f.createSVGDocument(uri, reader);
                svg.setSVGDocument(doc);
                svg.setPreferredSize(new Dimension(w, h));
                svg.setEnableZoomInteractor(false); // disallow CTRL+left mouse key: select view (a part of the current view)
                svg.setEnableRotateInteractor(false); // disallow CTRL+right mouse key+move mouse: rotate view

                JPanel panel = new JPanel();
                panel.add(svg);
                String info = "GDD proof visualization";
                // TODO: Put this piece of information to some other place of the program:
                info = "Press SHIFT and drag with right mouseclick to zoom, press SHIFT and drag with left mouseclick to move";
                frame = new JDialog(gxInstance.getFrame(), GExpert.getLanguage(info));
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(panel);
                frame.pack(); // This SVG window hides the main application window, FIXME.
                frame.setSize(w, h);
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error: " + e.toString());
            }
        }

    }

    private void createNodes(Vector vl) {

        for (int i = 0; i < vl.size(); i++) {
            Cond co = (Cond) vl.get(i);
            int n = co.getNo();
            PTNode node;
            Cond c = co.getPCO();

            boolean cons = true;
            while (c != null) {
                if (c.getNo() != 0) {
                    cons = false;
                    break;
                }
                c = c.nx;
            }
            if (co.getPCO() == null) {
                node = new PTNode(i + "    " + co.getText(), co);
            } else if (cons) {
                node = new PTNode(i + "    " + co.getText(), co);
                Cond tc = co.getPCO();
                while (tc != null) {
                    PTNode n1 = new PTNode("    " + tc.getText(), tc);
                    node.add(n1);
                    tc = tc.nx;
                }
            } else if (n > 0) {
                Cond tc = co.getPCO();
                String dix = "  by(";
                int nco = 0;
                while (tc != null) {
                    int j = 0;
                    if (tc.getNo() != 0) {
                        for (j = 0; j < vl.size(); j++) {
                            Cond c1 = (Cond) vl.get(j);
                            if (tc.getNo() == c1.getNo()) {
                                break;
                            }
                        }
                    } else {
                        int k = vl.indexOf(co);
                        for (j = k; j >= 0; j--) {
                            if (vl.get(j) == tc) {
                                break;
                            }
                        }
                    }
                    dix += j;
                    nco++;
                    tc = tc.nx;
                    if (tc != null) {
                        dix += ",";
                    }
                }
                dix += ")";
                if (nco > 1) {
                    node = new PTNode(i + "       Hence, " + co.getText() +
                            "   " + dix, co);
                } else {
                    node = new PTNode(i + "       Hence, " + co.getText() +
                            "   ", co);
                }
                node.tlevel = true;
            } else {
                node = new PTNode(i + "    " + co.getText(), co);
                node.tlevel = false;
            }
            top.add(node);
        }
    }

    public void add_predicates(String sh, Vector v) {
        DefaultMutableTreeNode no = new DefaultMutableTreeNode(sh);
        top_db.add(no);

        for (int i = 0; i < v.size(); i++) {
            CClass c = (CClass) v.get(i);
            DefaultMutableTreeNode d = new DefaultMutableTreeNode(c);
            no.add(d);
        }
    }

    public void addProveTree(Vector vl, Vector vd) {
        clearAll();
        if (vl.size() == 0) {
            return;
        }
        Cond co = (Cond) vl.get(vl.size() - 1);
        top.setUserObject(GExpert.getLanguage("To Prove:") + " " + co.getText());
        createNodes(vl);

        tree.expandPath(new TreePath(top));
        int n = top.getChildCount();
        for (int i = n; i > 0; i--) {
            tree.expandRow(i);
        }
        setListData(vd);

    }

    public void setListData(Vector v) {
        condPane.setConstruction(v);
    }

    public void clearAll() {
        if (top != null) {
            top.setUserObject("");
            top.removeAllChildren();
            tree.cancelEditing();
            ((DefaultTreeModel) tree.getModel()).reload();
        }
        if (top_full != null) {
            top_full.setUserObject("");
            top_full.removeAllChildren();
            tree_full.cancelEditing();
            ((DefaultTreeModel) tree_full.getModel()).reload();
        }

        if (top_db != null) {
            top_db.removeAllChildren();
            tree_db.cancelEditing();
            ((DefaultTreeModel) tree_db.getModel()).reload();
        }
        tree_mp.clearAll();
        condPane.clearAll();
        findex = -1;
        if (wuPanel != null)
            wuPanel.clearAll();
        if (gbPanel != null)
            gbPanel.clearAll();

        tbar.resetAll();
        if (this.getSelectedIndex() != 4)
            this.setSelectedIndex(0);
        this.repaint();
    }


    public void setListSelection(Cons c) {
        condPane.setListSelection(c);
    }

    public void setListSelectionLast() {
        condPane.setLastSelection();
    }

    private void setHighLightNode(PTNode node) {
        Cond co = node.co;
        if (co == null) {
            return;
        }
        int no = co.getNo();
        if (no == 0) {
            return;
        }
        for (int i = 0; i < top.getChildCount(); i++) {
            PTNode nd = (PTNode) top.getChildAt(i);
            if (nd != node && nd.co != null && nd.co.getNo() == no) {
                nd.setHightLight(true);
            } else {
                nd.setHightLight(false);
            }

        }
    }

    class PTNode extends DefaultMutableTreeNode {

        public Cond co = null;
        boolean tlevel = false;
        private boolean highlight = false;

        public PTNode(Object userObject, Cond o) {
            super(userObject);
            co = o;
        }

        public void setHightLight(boolean h) {
            highlight = h;
        }

        public boolean isHightLight() {
            return highlight;
        }

        public String toString() {
            return userObject.toString();
        }

        public Object getUserObject() {
            return co;
        }
    }


    JToolBar createToolBar() {
        return tbar;
    }

    public void finishedDrawing() {
        tbar.finishedDrawing();
        PanelProve.this.setSelectedIndex(0);
    }

    class popFull extends JPopupMenu implements ActionListener {

        public popFull() {
            JMenuItem item = new JMenuItem("Show GIB");
            add(item);
            item.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            PanelProve.this.showFullGIB();
        }
    }

    class popMenu extends JPopupMenu implements ActionListener {
        private Object obj;

        public popMenu() {
            super();
            JMenuItem item = new JMenuItem(GExpert.getLanguage("Prove"));
            // item.setActionCommand("Prove");
            add(item);
            item.addActionListener(this);
            /*
            // TODO: This is not yet implemented, so it is disabled since version 0.81:
            item = new JMenuItem(GExpert.getLanguage(311, "Prove in a new tab"));
            item.setEnabled(false);
            add(item);
             */
            item.addActionListener(this);
            item = new JMenuItem(GExpert.getLanguage("Refresh"));
            // item.setActionCommand("Refresh");
            item.addActionListener(this);
            add(item);
            item = new JMenuItem(GExpert.getLanguage("Search a fact"));
            item.setActionCommand("FACT");
            item.addActionListener(this);
            add(item);

        }

        public void showMenu(Object select, Component invoker, int x, int y) {
            CClass c = null;
            if (select != null && select instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode) select;
                Object obj = nd.getUserObject();
                if (obj != null && obj instanceof CClass)
                    c = (CClass) obj;

            }
            if (select != null && select instanceof ItemLabel) {
                //c = ((itemLabel)select).getUserObject();
            }
            //if (c != null) {
            {
                obj = invoker;
                super.show(invoker, x, y);
            }
        }

        private JMenuItem getItem(int n) {
            return (JMenuItem) popMenu.this.getComponent(n);
        }

        private void setMenuEnableItems(int t) {

            if (t == 0) {

                getItem(2).setEnabled(true);
                getItem(3).setEnabled(true);

            } else {
                getItem(0).setEnabled(true);
                getItem(1).setEnabled(true);
                getItem(2).setEnabled(true);
                getItem(3).setEnabled(true);

            }

        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("FACT")) {
                FactFinderDialog dlg = getFactFinderDialog();
                if (gxInstance != null)
                    gxInstance.setActionSelect();
                dlg.showDialog();
                return;
            } else if (command.equals("Refresh")) {
                PanelProve.this.showDatabase();
            }

            if (obj instanceof ItemLabel) {
                ItemLabel lb = (ItemLabel) obj;
                Object value = lb.getUserObject();
                if (value instanceof Cond) {
                    if (command.equals("Prove")) {
                        PanelProve.this.proveCond((Cond) value, false);
                    } else if (command.equals("Prove in a new tab")) {
                    }
                }
            } else if (obj == tree_db) {
                TreePath path = tree_db.getSelectionPath();
                if (path != null) {
                    Object obj2 = path.getLastPathComponent();
                    if (obj2 instanceof DefaultMutableTreeNode) {
                        Object obj3 = ((DefaultMutableTreeNode) obj2).getUserObject();
                        if (obj3 instanceof CClass) {
                            Cond c = getSelectedCondFromAttr((CClass) obj3);
                            if (c != null) // Prove via right-click, from Fixpoint tab
                                PanelProve.this.proveCond(c, true);
                        }
                    }
                }
            }
        }

    }

    public void setTreeFullFont(Font f) {
        if (f != null) {
            ItemLabel.font = f;
            BookCellRenderer render = (BookCellRenderer)
                    tree_full.getCellRenderer();
            render.setCellFont(f);
            BookCellEditor editor = (BookCellEditor) tree_full.
                    getCellEditor();
            editor.setEditorFont(f);
            ((DefaultTreeModel) tree_full.getModel()).reload();
        }
    }

    public boolean load(File file) {
        GTerm gt = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Vector v = new Vector();
            while (true) {
                gt = new GTerm();
                if (gt.readAterm(reader)) {
                    v.add(gt);
                } else {
                    break;
                }
            }

            reader.close();

            if (v.size() == 0) {
                JOptionPane.showMessageDialog(gxInstance,
                        file.getName() +
                                "\n" + GExpert.getLanguage("File format not supported!"),
                        GExpert.getLanguage("Can not open"),
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (v.size() == 1) {
                gt = (GTerm) v.get(0);
            } else {
                Object rv = JOptionPane.showInputDialog(gxInstance,
                        GExpert.getLanguage("Please select theorem"), GExpert.getLanguage("input"),
                        JOptionPane.PLAIN_MESSAGE, null, v.toArray(), GExpert.getLanguage("Select"));
                if (rv == null)
                    return true;
                gt = (GTerm) rv;
            }
            gt.pc();
            clearAll();
            condPane.setConstruction(gt);
            if (false && gt.isPositionSet()) {
                tbar.startDrawing();
                dp.autoConstruct(gt);
            } else {
                drawConstruction();
                tbar.startDrawing();
            }
            this.setSelectedIndex(0);

        } catch (IOException ee) {
            JOptionPane.showMessageDialog(gxInstance, ee.getMessage(),
                    "Read Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    class ButtonToolBar extends JToolBar implements ActionListener {
        private JToggleButton b1, button1, button2, button3, button4, button5, button6, button7;

        private JLabel label;

        private JPopupMenu popup1;

        private JMenu pmenu;

        private boolean show_ang = true;

        private ButtonGroup group;

        private void startConstcutDiagram() {
            button4.setSelected(true);
            gxInstance.addButtonToDrawGroup(button4);
            drawConstruction();
        }

        public Dimension getMaximumSize() {
            Dimension d = super.getMaximumSize();
            Dimension d1 = this.getPreferredSize();
            d.setSize(d.getWidth(), d1.getHeight());
            return d;
        }

        public void resetAll() {
            button4.setSelected(false);
        }


        public JMenu getProveMenu() {
            return pmenu;
        }

        public void addImageToItem(JMenuItem item) {
            if (gxInstance != null)
                gxInstance.addImageToItem(item);
        }

        public void addImageToItem(JMenuItem item, String name) {
            if (gxInstance != null)
                gxInstance.addImageToItem(item, name);
        }

        public ButtonToolBar() {
            setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

            b1 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/font.gif")) {

                public void actionPerformed(ActionEvent e) {
                    b1.setSelected(false);
                    PanelProve.this.getSelectedIndex();

                    if (gxInstance != null) {
                        MiscDialog dlg = new MiscDialog(gxInstance);
                        gxInstance.centerDialog(dlg);
                        dlg.setSelectedTabbedPane(3);
                        dlg.setVisible(true);

                    }
//                    int t = vFontChooser.showDialog(gxInstance, itemLabel.font, null);
//                    if (t == JOptionPane.OK_OPTION) {
//                        Font f = vFontChooser.getReturnFont();
//                        setTreeFullFont(f);
//                    }
                }
            });
            b1.setToolTipText(GExpert.getLanguage("Default Font"));

            button3 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/expand.gif")) {

                public void actionPerformed(ActionEvent e) {
                    button3.setSelected(false);
                    expandOrCollapse();
                }
            });

            button3.setToolTipText(GExpert.getLanguage("Expand/collapse proof tree"));

            button4 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/draw.gif")) {
                public void actionPerformed(ActionEvent e) {
                    startConstcutDiagram();
                }
            });
            button4.setToolTipText(GExpert.getLanguage("Show the internal description of the construction"));

            button5 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/addconc.gif")) {
                public void actionPerformed(ActionEvent e) {
                    addConclusion(null);
                    button5.setSelected(false);
                }
            });
            button5.setToolTipText(GExpert.getLanguage("Add Conclusion"));

            button6 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/refresh.gif")) {
                public void actionPerformed(ActionEvent e) {
                    showDatabase();
                    button6.setSelected(false);
                }
            });
            button6.setToolTipText(GExpert.getLanguage("Refresh"));

            button7 = new JToggleButton(new AbstractAction("",
                    GExpert.createImageIcon("images/ptree/run.gif")) {
                public void actionPerformed(ActionEvent e) {
                    prove();
                    button7.setSelected(false);
                }
            });
            button7.setToolTipText(GExpert.getLanguage("Compute proof with the selected method"));

            EntityButtonUI ui = new EntityButtonUI();
            b1.setUI(ui);
            button3.setUI(ui);
            button4.setUI(ui);
            button5.setUI(ui);
            button6.setUI(ui);
            button7.setUI(ui);
            this.add(b1);
            this.add(button3);
            this.add(button4);
            this.add(button5);
            this.add(button6);
            this.add(button7);

            this.add(Box.createHorizontalGlue());
            this.setFloatable(false);
            button3.setEnabled(false);
            {
                group = new ButtonGroup();
                popup1 = new JPopupMenu();
                JRadioButtonMenuItem item1 = new JRadioButtonMenuItem(
                        getLanguage("Deductive Database Method"));
                item1.setActionCommand("GDD");
                item1.addActionListener(this);
                item1.setSelected(true);
                popup1.add(item1);
                group.add(item1);
                item1 = new JRadioButtonMenuItem(getLanguage("Full Angle Method"));
                item1.setActionCommand("Full");
                item1.addActionListener(this);
                popup1.add(item1);
                group.add(item1);

                item1 = new JRadioButtonMenuItem(getLanguage("Traditional Method"));
                item1.setActionCommand("TRAD");
                item1.addActionListener(this);
                item1.setEnabled(false);
                popup1.add(item1);
                group.add(item1);

                item1 = new JRadioButtonMenuItem(getLanguage("Area Method"));
                item1.setActionCommand("Area");
                item1.addActionListener(this);
                item1.setEnabled(false);
                popup1.add(item1);
                group.add(item1);

                item1 = new JRadioButtonMenuItem(getLanguage("Groebner Basis Method"));
                item1.setActionCommand("GB");
                item1.addActionListener(this);
                item1.setEnabled(true);
                popup1.add(item1);
                group.add(item1);


                item1 = new JRadioButtonMenuItem(getLanguage("Wu's Method"));
                item1.setActionCommand("Wu");
                item1.addActionListener(this);
                popup1.add(item1);
                group.add(item1);


                pmenu = new JMenu(getLanguage("Prove"));

                JMenuItem item = new JMenuItem(getLanguage("Prove"));
                item.setActionCommand("Prove");
                item.addActionListener(this);
                pmenu.add(item);
                addImageToItem(item, "run");

                JMenu m = new JMenu(getLanguage("To Prove"));
                String[] ts = ConcDialog.ts;
                for (int i = 0; i < ts.length; i++) {
                    JMenuItem it = new JMenuItem(getLanguage(ts[i]));
                    it.setActionCommand("CONC");
                    it.addActionListener(this);
                    m.add(it);
                }
                if (gxInstance != null)
                    gxInstance.addImageToItem(m);
                pmenu.add(m);

                item = new JMenuItem(getLanguage("Numerical Check"));
                item.setActionCommand("Check");
                item.addActionListener(this);
                addImageToItem(item, "check");
                pmenu.add(item);

                pmenu.addSeparator();

                item = new JMenuItem(getLanguage("Polynomials"));
                item.setActionCommand("LV");
                addImageToItem(item, "poly");
                item.addActionListener(this);
                pmenu.add(item);

                item = new JMenuItem(getLanguage("Nondegenerate Conditions"));
                item.setActionCommand("NDG");
                addImageToItem(item);
                item.setSelected(true);
                item.addActionListener(this);
                pmenu.add(item);

                item = new JMenuItem(getLanguage("All solutions"));
                item.setActionCommand("All solutions");
                addImageToItem(item);
                item.setSelected(true);
                item.addActionListener(this);
                pmenu.add(item);

            }
            label = new JLabel("GDD", GExpert.createImageIcon("images/ptree/downsel.gif"), JLabel.HORIZONTAL) {
                public Dimension getPreferredSize() {
                    Dimension dm = super.getPreferredSize();
                    if (dm.getWidth() < 35) {
                        dm.setSize(35, dm.getHeight());
                    }
                    return dm;
                }
            };
            label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            label.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    popup1.show(label, 0, label.getHeight());
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.setBorder(new SolidBorder());
                }

                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                }
            });

            this.add(Box.createHorizontalStrut(15));
            label.setAlignmentX(Component.RIGHT_ALIGNMENT);
            this.add(label);
        }


        private void prove() {
            PanelProve.this.prove();
        }


        public void actionPerformed(ActionEvent e) {
            String s = (e.getActionCommand());
            if (s.equals("GDD") || s.equals("Full") || s.equals("Area") || s.equals("Wu") || s.equals("GB")) {
                label.setText(GExpert.getLanguage(s));
                JMenuItem item = (JMenuItem) e.getSource();
                label.setToolTipText(item.getText());
            } else if (s.equals("CONC")) {
                JMenuItem item = (JMenuItem) e.getSource();
                addConclusion(item.getText());
            } else if (s.equals("Generate")) {
                PanelProve.this.generate();
            } else if (s.equals("Detail")) {
                JMenuItem item = (JMenuItem) e.getSource();
                boolean detail_proof = item.isSelected();
                Full.set_showdetai(detail_proof);
            } else if (s.equals("Aux")) {
                show_AllFullAux(show_ang);
            } else if (s.equals("Check")) {
                if (gxInstance != null) {
                    gxInstance.showNumDialog();
                    gxInstance.setActionSelect();
                }
            } else if (s.equals("AngText")) {
                JMenuItem item = (JMenuItem) e.getSource();
                CMisc.show_angle_text = item.isSelected();
                dpane.repaint();
            } else if (s.equals("SAng")) {
                JMenuItem item = (JMenuItem) e.getSource();
                show_ang = item.isSelected();
            } else if (s.equals("pproperty")) {
                int r = tree_full.getRowCount();
                JOptionPane.showMessageDialog(gxInstance,
                        "There are " + r + "rows and " + (r / 2 - 1) + " steps");
            } else if (s.equals("Prove")) {
                prove();
            } else if (s.equals("Rules")) {
                RuleDialog dlg = new RuleDialog(gxInstance);
                dlg.setVisible(true);
            } else if (s.equals("LV")) {
                dp.popLeadingVariableDialog();
            } else if (s.equals("NDG"))
                PanelProve.this.showNDGs();
            else if (s.equalsIgnoreCase("All solutions")) {
                Vector v = dp.calculate_allResults();
                AllSolutionDialog dlg = new AllSolutionDialog(gxInstance);
                dlg.setVlist(v);
                dlg.autoFiltered();

                gxInstance.centerDialog(dlg);
                dlg.setVisible(true);
            }
        }

        public void proveADirectory(File f, StringBuffer buffer) {
            try {
                if (f.isDirectory()) {
                    File[] list = f.listFiles();
                    for (int i = 0; i < list.length; i++)
                        proveADirectory(list[i], buffer);

                } else {
                    if (gxInstance != null) {
                        gxInstance.Clear();
                        gxInstance.openAFile(f);
                    }

                    GTerm gt = condPane.getTerm();
                    if (gt != null) {
                        buffer.append("\n" + f.getName() + ": ");
                        Prover.set_gterm(gt);
                        if (Prover.prove()) {
                            Cond co = Prover.getProveHead();
                            addGddProveTree(co);
                            int d = 1;
                            while (co != null) {
                                co = co.nx;
                                d++;
                            }
                        } else if (gt.conc.pred != 0)
                            buffer.append("\tfalse ");
                        else
                            buffer.append("\tNo conclusion ");
                    }
                }
            } catch (Exception ee) {
                CMisc.print(ee.getMessage());
            }
        }

        public void setSelectedState(int d) {
            if (d == 0) { // theorem
                button3.setEnabled(false);
            } else if (d == 1) { // full
                button3.setEnabled(true);
            } else if (d == 2) { // gdd
                button3.setEnabled(true);
            } else if (d == 3) { //area
            } else if (d == 4) { // db
                button3.setEnabled(false);
            }
        }

        public void finishedDrawing() {
            button4.setSelected(false);
        }

        public void startDrawing() {
            button4.setSelected(true);
        }

        public String getProveMethodSelected() {
            ButtonModel model = group.getSelection();
            if (model != null) {
                String s = model.getActionCommand();
                return s;
            }
            return "";
        }
    }

    public void flash_cond(Cond co) {
        (dp).flashCond(co, true);

    }

    public void flashattr(CClass cc) {
        (dp).flashattr(cc, dpane);
    }


    public void showNDGs() {

        GTerm g = condPane.getTerm();
        if (g == null)
            return;
        g.pc();

//        Vector v = g.pc();
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        Vector v3 = new Vector();
        Vector v4 = new Vector();

        if (!Prover.getAllNdgs(g, v1, v2, v3, v4))
            return;
        NdgDialog d = new NdgDialog(gxInstance, g, dp);

        d.setValue(v1, v2, v3, v4);
        d.setVisible(true);
    }

    public void setSelectedConstruction(Cons c) {
        condPane.setSelectedCons(c);
    }

    class Conspanel extends JSplitPane implements MouseMotionListener, ListSelectionListener, MouseListener, ActionListener {
        private JList list, listx;
        private GTerm gt;
        private String lstpt;
        private DefaultListModel listModel, listModelx;
        private Cons sconc = null;
        private boolean showD = false;
        private JPopupMenu showMenu;

        private int divSize = 0;
        private boolean divShown = true;
        private JToolBar tipbar;
        private JTextPane tiptext;
        private JToggleButton bclose;

        public Conspanel() {
            super(JSplitPane.VERTICAL_SPLIT);

            listModel = new DefaultListModel();
            listModelx = new DefaultListModel();
            list = new JList();
            listx = new JList();
            list.setFont(CMisc.thmFont);
            listx.setFont(CMisc.thmFont);
            list.setModel(listModel);
            listx.setModel(listModelx);
            ListSelectionModel listSelectionModel = list.getSelectionModel();
            list.addMouseMotionListener(this);
            listSelectionModel.addListSelectionListener(this);
            list.setCellRenderer(new clistRender());
            list.addMouseListener(this);
            listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            listx.setSelectionModel(listSelectionModel);
            this.setTopComponent(new JScrollPane(list));
            tipbar = new JToolBar(JToolBar.HORIZONTAL);
            tipbar.setFloatable(false);
            tiptext = new JTextPane() {
                public Dimension getMaximumSize() {
                    return super.getPreferredSize();
                }
            };
            tiptext.setEditable(false);
            tiptext.setFont(CMisc.thmFont);
            JToggleButton button = new JToggleButton(GExpert.createImageIcon("images/quit.gif"));
            button.setBorder(null);
            button.setActionCommand("Close");
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setToolTipText("Close");
            button.addActionListener(this);
            bclose = button;

            tipbar.add(tiptext);
            tipbar.add(Box.createHorizontalGlue());
            tipbar.add(button);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JScrollPane scroll = new JScrollPane(listx);
            panel.add(scroll);
            panel.add(tipbar);
            this.setBottomComponent(panel);

            list.setBorder(null);
            listx.setBorder(null);
            scroll.setBorder(null);
            panel.setBorder(null);
            this.setBorder(null);

            showDetail(false);
            addMenu();
        }

        public void showDetail(boolean r) {
            if (r) {
                if (divShown)
                    return;
                divShown = true;
                this.setDividerSize(divSize);
                this.getBottomComponent().setVisible(true);
                this.resetToPreferredSizes();
            } else {
                if (!divShown)
                    return;
                divShown = false;
                divSize = this.getDividerSize();
                this.setDividerSize(0);
                this.getBottomComponent().setVisible(false);
                this.resetToPreferredSizes();
            }
        }

        public void addMenu() { // TODO: Check if these strings should be added for internationalization.
            showMenu = new JPopupMenu();
            JRadioButtonMenuItem t2 = new JRadioButtonMenuItem("Detail Construction");
            t2.addActionListener(this);
            showMenu.add(t2);
            showMenu.addSeparator();
            JMenuItem it = new JMenuItem("Prove");
            it.addActionListener(this);
            showMenu.add(it);

            it = new JMenuItem("Construct Diagram");
            it.addActionListener(this);
            showMenu.add(it);

            it = new JMenuItem("Add Conclusion");
            it.addActionListener(this);
            showMenu.add(it);

            it = new JMenuItem("Add Nondegenerate Conditions");
            it.setActionCommand("NDGS");
            it.addActionListener(this);
            showMenu.add(it);

            it = new JMenuItem("Show Database");
            it.addActionListener(this);
            showMenu.add(it);

            showMenu.addSeparator();
            it = new JMenuItem("Nondegenerate Conditions");
            it.setActionCommand("NDG");
            it.addActionListener(this);
            showMenu.add(it);
        }


        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            Object o = e.getSource();
            if (command.equals("Detail Construction")) {
                JRadioButtonMenuItem m = (JRadioButtonMenuItem) o;
                this.showDetail(m.isSelected());
            } else if (command.equals("PC")) {
                PPDialog pp = new PPDialog(gxInstance, condPane.getTerm(), dp);
                pp.setVisible(true);
            } else if (command.equals("NDG")) {
                PanelProve.this.showNDGs();
            } else if (command.equals("Add Conclusion")) {
                PanelProve.this.addConclusion("Add Conclusion");
            } else if (command.equals("NDGS")) {
                PanelProve.this.addConclusion("Add Nondegenerate Conditions");
                cdialog.setType(1);
            } else if (command.equals("Prove")) {
                PanelProve.this.prove();
            } else if (command.equals("Construct Diagram")) {
                tbar.startConstcutDiagram();
            } else if (command.equals("Show Database")) {
                PanelProve.this.showDatabase();
            } else if (command.equals("Close")) {
                bclose.setSelected(false);
                this.showDetail(false);
            }
            this.repaint();
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                this.showMenu.show(this, e.getX(), e.getY());
            } else if (e.getClickCount() > 1) {
                int n = list.getSelectedIndex();
                if (n < 0)
                    showDetail(false);
                else this.showDetail(!divShown);
            } else {
                int n = list.getSelectedIndex();
                if (gt == null)
                    return;
                int n1 = gt.getconsNum();
                if (n == n1 + 1 && gt.hasConclusion()) {
                    Cond c = gt.getConc();
                    if (dp != null)
                        dp.flashCond(c, true);
                } else {
                    Cons c = gt.getCons(n);
                    dp.flashcons(c);
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

        public void valueChanged(ListSelectionEvent e) {

            Object obj = list.getSelectedValue();

            Cons cs = (Cons) obj;
            if (cs != null) {
                tiptext.setText(cs.toDDString());
                dp.flash_node_by_id(cs.getId());
            } else
                tiptext.setText("");

        }

        public void setListSelection(Cons c) {
            list.setSelectedValue(c, true);
            lstpt = null;
            if (!c.is_conc()) {
                CPoint p = dp.getLastConstructedPoint();
                if (p != null)
                    lstpt = p.getname();
            }

        }

        public void setLastSelection() {
            int i = listModel.getSize();
            if (i > 0) {
                list.setSelectedIndex(i - 1);
                lstpt = null;
            }
        }

        public void setSelectedCons(Cons c) {
            if (c == null)
                return;
            for (int i = 0; i < listModel.getSize(); i++) {
                if (listModel.getElementAt(i) == c) {
                    list.setSelectedIndex(i);
                    return;
                }
            }
        }

        public boolean checkValid() {
            return gt != null;
        }

        public Vector getAllPts() {
            return gt.getAllptsText();
        }

        public GTerm getTerm() {
            if (gt == null) return null;

            Vector v = dp.getPointList();
            for (int i = 0; i < v.size(); i++) {
                CPoint p = (CPoint) v.get(i);
                gt.add_pt(p.getname());
                gt.setPtLoc(p.getname(), p.getx(), p.gety(), p.getTx(), p.getTy());
            }
            gt.ge_cpt();
            return gt;
        }

        public void setConclusion(Cons s, boolean r) {

            if (gt.setConclusion(s)) {
                setConstruction(gt);
                if (r)
                    dp.addCondAux(gt.getConclusion(), false);
                dpane.repaint();
            }
        }

        public void add_ndgs(Cons c) {
            if (c == null)
                return;
            switch (c.type) {
                case Gib.CO_COLL: {
                    Cons c1 = new Cons(c);
                    c1.type = Gib.NDG_COLL;
                    gt.addNdg(c1);
                }
                break;
                case Gib.CO_PARA: {
                    Cons c1 = new Cons(c);
                    c1.type = Gib.NDG_PARA;
                    gt.addNdg(c1);
                }
                break;

                case Gib.CO_PERP: {
                    Cons c1 = new Cons(c);
                    c1.type = Gib.NDG_PERP;
                    if (c1.ps[2] == c1.ps[0] && c1.ps[3] == c1.ps[1]) {
                        c1.type = Gib.NDG_NON_ISOTROPIC;
                    }
                    gt.addNdg(c1);
                }
                break;
            }
        }

        public GTerm getConstruction() {
            return gt;
        }

        public void clearAll() {
            listModel.removeAllElements();
            listModelx.removeAllElements();

            gt = null;
            sconc = null;
        }

        public void setConstruction(Vector v) {
        }

        public void setVector(Vector v) {
            if (gt == null)
                gt = new GTerm();

            Cons c = gt.getConclusion();
            if (c != null)
                sconc = c;
            gt.clear();
            gt.addConsV(v);
            setConstruction(gt);
        }

        public void addConclusion() {
            Cons c = sconc;
            if (c != null) {
                gt.setConclusion(c);
                listModel.addElement(c);
                listModelx.addElement(c.toDString());
                gt.ge_cpt();
//                dp.flashCond(gt.getConc(), true);
            }
        }

        public void setConstruction(GTerm gt) {
            if (gt == null) {
                return;
            }
            if (this.gt != null && this.gt.hasConclusion()) {
            } else {
                if (!gt.hasConclusion() && gt.getCons_no() > 0) {
                    gt.setConclusionNo();
                }
            }

            this.gt = gt;
            is_database_updated = false;


            listModel.removeAllElements();
            listModelx.removeAllElements();

            Vector v = gt.getCons();
            for (int i = 0; i < v.size(); i++) {
                Cons c = (Cons) v.get(i);
                listModel.addElement(c);
                listModelx.addElement(c.toDString());
            }
        }

        public void setLayStyle(boolean v) {
            this.revalidate();
        }

        class clistRender extends JPanel implements ListCellRenderer {
            private boolean selected = false;
            private Vector vlist = new Vector();


            public clistRender() {
                this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                for (int i = 0; i < 25; i++) {
                    DefaultListCellRenderer cell = new DefaultListCellRenderer();
                    vlist.add(cell);
                    this.add(cell);
                    this.add(Box.createHorizontalStrut(3));
                }
                this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }

            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                Cons c = (Cons) value;
                String s = null;
                if (!showD)
                    s = c.getPrintText(isSelected);//value.toString();
                else
                    s = c.toDString();

                String[] sl = s.split(" ");
                int n = sl.length;

                for (int i = 0; i < 25; i++) {
                    DefaultListCellRenderer cell = null;
                    cell = (DefaultListCellRenderer) vlist.get(i);

                    if (i < n) {
                        cell.setVisible(true);
                        cell.getListCellRendererComponent(list, sl[i], index, isSelected, false);
                        selected = isSelected;
                        if (isSelected) {
                            if (i != 0 && index >= 0 && lstpt != null) {
                                String s2 = lstpt.trim();
                                if (s2 != null && s2.length() > 0 && sl[i].equals(s2)) {
                                    cell.setBackground(Color.pink);
                                    cell.setBorder(new LineBorder(Color.pink.darker(), 1));
                                }
                            }
                        }
                    } else {
                        cell.setVisible(false);
                    }
                    if (i != 0)
                        cell.setForeground(Color.blue);
                }
                return this;
            }

            public boolean isOpaque() {
                return false;
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (selected) {
                    Color b = ((DefaultListCellRenderer) vlist.get(0)).
                            getBackground();
                    g.setColor(b);
                    int w = this.getWidth();
                    int h = this.getHeight();
                    g.fillRect(0, 0, w, h);
                    g.setColor(b.darker());
                    g.drawRect(0, 0, w - 1, h - 1);
                }

            }
        }
    }


    public void set_conclusion(Cons s, boolean r) {
        condPane.setConclusion(s, r);
    }

    public void add_ndgs(Cons s) {
        condPane.add_ndgs(s);
    }

    public Cond getSelectedCondFromAttr(CClass c) {

        GDDBc db = Prover.get_gddbase();
        Cond co = null;
        AttrToCondDialog dlg = null;


        if (c instanceof LLine) {
            LLine ln = (LLine) c;
            if (ln.no >= 3) {
                dlg = new AttrToCondDialog(gxInstance, ln);
                dlg.setVisible(true);
            }
        } else if (c instanceof PLine) {
            PLine pn = (PLine) c;
            if (pn.no >= 2) {
                dlg = new AttrToCondDialog(gxInstance, pn);
                dlg.setVisible(true);
            }
        } else if (c instanceof ACir) {
            ACir cr = (ACir) c;
            if (cr.no >= 4) {
                dlg = new AttrToCondDialog(gxInstance, cr);
                dlg.setVisible(true);
            }
        } else if (c instanceof AngSt) {
            AngSt st = (AngSt) c;
            if (st.no > 2) {
                dlg = new AttrToCondDialog(gxInstance, st);
                dlg.setVisible(true);
            }
        } else if (c instanceof STris) {
            STris st = (STris) c;
            if (st.no >= 2) {
                dlg = new AttrToCondDialog(gxInstance, (STris) c);
                dlg.setVisible(true);
            }
        } else if (c instanceof CSegs) {
            CSegs cg = (CSegs) c;
            if (cg.no >= 2) {
                dlg = new AttrToCondDialog(gxInstance, (CSegs) c);
                dlg.setVisible(true);
            }
        }
        if (dlg != null) {
            co = dlg.getReturnedCond();
            if (co == null) return null;
        }

        if (co == null)
            co = db.getDefaultCond(c);
        return co;
    }

    public void high_light_a_fact(CClass c) {
        if (c == null) return;
        int n = top_db.getChildCount();
        for (int i = 1; i < n; i++)
            tree_db.collapseRow(i);

        for (int i = 0; i < n; i++) {
            DefaultMutableTreeNode d = (DefaultMutableTreeNode) top_db.getChildAt(i);
            int m = d.getChildCount();
            for (int j = 0; j < m; j++) {
                DefaultMutableTreeNode d1 = (DefaultMutableTreeNode) d.getChildAt(j);
                if (d1.getUserObject() == c) {
                    TreePath path = new TreePath(d1.getPath());
                    tree_db.expandPath(path);
                    tree_db.setSelectionPath(path);
                    dbPanel.scrollRectToVisible(tree_db.getPathBounds(path));
                    dp.clearFlash();
                    flashattr(c);
                    dpane.repaint();
                    return;
                }
            }

        }
    }


}
