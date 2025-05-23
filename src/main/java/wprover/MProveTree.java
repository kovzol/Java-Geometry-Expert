package wprover;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/**
 * MProveTree is a custom JTree implementation that represents a tree structure
 * for mathematical objects and their associated undo structures.
 */
public class MProveTree extends JTree implements ActionListener {
    private GExpert gxInstance;
    private DPanel dpane;
    private DrawTextProcess dp;

    private DefaultMutableTreeNode top;
    private DefaultTreeModel model;
    private TreeCellOPaqueEditor editor;
    private mpopup popup;
    private MNode topm;
    private int rstep = -1;
    private int statusID = -1;

    private int x1, y1, x2, y2;
    private boolean isButtonDown = false;


    public void loadmtree(MNode n) {
        topm = n;
        top.removeAllChildren();
        loadmnode(top, n);
        this.cancelEditing();
        model.reload();
        rstep = this.getRowCount();

    }

    public boolean isTreeEmpty() {
        if (top == null) return true;
        MNode m = (MNode) top.getUserObject();
        if (m == null || m.size() <= 1) return true;
        return false;
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (isButtonDown) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.gray);
            g2.setStroke(CMisc.DashedStroke);
            g2.drawLine(x1, y1, x1, y2);
            g2.drawLine(x1, y1, x2, y1);
            g2.drawLine(x2, y2, x1, y2);
            g2.drawLine(x2, y2, x2, y1);
        }
    }


    private void loadmnode(DefaultMutableTreeNode nd, MNode n) {
        nd.setUserObject(n);
        for (int i = 0; i < n.size(); i++) {
            MNode n1 = (MNode) n.get(i);
            DefaultMutableTreeNode nd1 = new DefaultMutableTreeNode(n1);
            loadmnode(nd1, n1);
            nd.add(nd1);
            if (nd == top) {
                int index = this.getLastProveNodeIndex();
                if (index > 0) {
                    n1.setIndex(index);
                }
            } else {
                n1.setIndex(i + 1);
            }
        }
    }


    private DefaultMutableTreeNode getSelectedNodeOrLast() {
        TreePath path = this.getSelectionPath();
        DefaultMutableTreeNode node = null;

        if (path == null) {
            node = (DefaultMutableTreeNode) this.getLastNodeOnTop();
        } else
            node = (DefaultMutableTreeNode) path.getLastPathComponent();
        return (DefaultMutableTreeNode) node;
    }

    private TreeNode getLastNodeOnTop() {
        int n = top.getChildCount();
        if (n > 0)
            return top.getChildAt(n - 1);
        return null;
    }

    private int getLastProveNodeIndex() {
        int k = top.getChildCount();
        this.getToProveIndex();
        int s = this.getStatusID();
        if (s >= 0 && k >= s) {
            return k - s - 1;
        }
        return -1;
    }

    public DefaultMutableTreeNode addNewNode(DefaultMutableTreeNode d, MNode n) {
        TreePath path = this.getSelectionPath();
        DefaultMutableTreeNode node = this.getSelectedNodeOrLast();

        if (node == null)
            return d;

        MNode n1 = (MNode) node.getUserObject();
        if (n1 == null) {
            return d;
        }

        if (node.getParent() == top) {
            int index = this.getLastProveNodeIndex();
            if (index >= 0) {
                n.setIndex(index + 1);
            }
            top.add(d);
            ((MNode) top.getUserObject()).add(n);
        } else {
            DefaultMutableTreeNode dn = (DefaultMutableTreeNode) node.getParent();
            if (dn != null) {
                dn.add(d);
                ((MNode) dn.getUserObject()).add(n);
            }
        }
        this.reload();
        this.setEditorLastRow();
        return d;
    }

    public DefaultMutableTreeNode addNewNode(MNode n) {
        DefaultMutableTreeNode d = new DefaultMutableTreeNode(n);
        return addNewNode(d, n);
    }

    public int getToProveIndex() {
        for (int i = 0; i < top.getChildCount(); i++) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) top.getChildAt(i);
            MNode t = (MNode) nd.getUserObject();
            if (t != null && t.objSize() != 0 && t.getObject(0) != null &&
                    t.getObject(0) instanceof MPrefix) {
                MPrefix f = (MPrefix) t.getObject(0);
                if (f.getPrefixType() == 1) {
                    statusID = i;
                    return i;
                }
            }
        }
        statusID = -1;
        return -1;
    }


    public int getStatusID() {
        return statusID;
    }

    public void undoStep(UndoStruct un) {
        MNode n = (MNode) top.getUserObject();
        if (n == null) return;
        int k = n.size();
        if (k > 0) {
            MNode n2 = n.getChild(k - 1);
            if (n2.containsUndo(un)) {
                this.cancelEditing();
                n.remove(k - 1);
                top.remove(k - 1);
                this.reload();
                this.setEditorLastRow();
            }
        }


    }

    public void reload() {
        TreePath path = this.getSelectionPath();
        this.cancelEditing();
        model.reload();
        this.setSelectionPath(path);
        rstep = this.getRowCount();
    }

    public void appendDefault() {
        TreePath path = this.getSelectionPath();
        DefaultMutableTreeNode node = this.getSelectedNodeOrLast();
        if (node == null) {
            return;
        }
        MNode n = (MNode) node.getUserObject();
        if (n == null) {
            return;
        }
        MObject d = null;
        if (n.size() == 1) {
            MObject obj = n.getObject(0);
            if (obj instanceof MPrefix) {
                MPrefix pf = (MPrefix) obj;
                int t = pf.getPrefixType();
                if (t == MPrefix.GIVEN) {
                    d = new MDraw();
                } else if (t == MPrefix.TOPROVE) {
                    d = new MAssertion(0);
                }
            }
        }
        if (d == null) {
            d = new MObject(0);
        }
        append(d);

    }

    public DefaultMutableTreeNode addChild(MObject obj) {
        TreePath path = this.getSelectionPath();
        DefaultMutableTreeNode node = this.getSelectedNodeOrLast();
        if (node == null) {
            return null;
        }
        MNode n = (MNode) node.getUserObject();
        if (n == null) {
            return null;
        }
        MNode nd = new MNode();
        nd.add(obj);
        n.addChild(nd);
        DefaultMutableTreeNode dn = new DefaultMutableTreeNode(nd);
        node.add(dn);
        this.cancelEditing();
        this.reload();
        this.expandPath(path);
        this.setEditorLastRow();
        return dn;
    }

    public DefaultMutableTreeNode append(MObject obj) {
        TreePath path = this.getSelectionPath();
        DefaultMutableTreeNode node = this.getSelectedNodeOrLast();
        if (node == null) {
            return null;
        }
        MNode n = (MNode) node.getUserObject();
        if (n == null) {
            return null;
        }
        n.add(obj);
        this.startEditingAtPath(path);
        this.setEditorLast();
        return node;
    }

    public DefaultMutableTreeNode addNewNode() {
        MNode node = new MNode();
        int k = top.getChildCount();
        int n = this.getToProveIndex();
        if (n < 0) {
//            node.add(new mdraw("draw..."));
        } else {
            node.add(new MObject(0));
            node.setIndex(k - n - 1);
        }
        MNode nt = (MNode) top.getUserObject();
        nt.addChild(node);
        DefaultMutableTreeNode nd = new DefaultMutableTreeNode(node);
        top.add(nd);

        return nd;
    }

    public DefaultMutableTreeNode getRoot() {
        return top;
    }

    public void setEditorLastRow() {
        int n = this.getRowCount();
        if (n == 0) {
            return;
        }
        TreePath path = this.getPathForRow(n - 1);
        this.startEditingAtPath(path);
        setEditorLast();
    }

    public void setEditorLast() {
        editor.setSelectionLast();
    }

    public void setEditorFirst(DefaultMutableTreeNode nd) {
        TreePath path = new TreePath(nd.getPath());
        this.expandPath(path);
        this.startEditingAtPath(path);
        editor.setSelectionFirst();
    }

    public void setEditorLast(DefaultMutableTreeNode nd) {
        TreePath path = new TreePath(nd.getPath());
        this.startEditingAtPath(path);
        editor.setSelectionLast();
    }

    public void init_top() {
        MNode node = new MNode();
        node.add(new MText(getLanguage("Theorem")));
        top.setUserObject(node);
        MNode node1 = new MNode();
        node1.add(new MPrefix(0));
        top.add(new DefaultMutableTreeNode(node1));
        node.add(node1);
        topm = node;
    }


    String getLanguage(String s) {
        if (gxInstance != null)
            return gxInstance.getLanguage(s);
        return s;
    }

    public String getLanguage(int n, String s) {
        String s1 = "";
        if (gxInstance != null)
            s1 = gxInstance.getLanguage(s); // avoid using the number n, instead, use the string
        if (s1 != null && s1.length() > 0)
            return s1;
        return s;
    }

    public void selectByRect(int y1, int y2) {
        if (y1 > y2) {
            int y = y1;
            y1 = y2;
            y2 = y;
        }

        int n = this.getRowCount();
        int r1, r2;
        r1 = r2 = -1;
        for (int i = 0; i < n; i++) {
            Rectangle rc = getRowBounds(i);
            double y = rc.getY();
            double h = rc.getHeight();
            if (y <= y2 && y + h >= y1) {
                if (r1 < 0)
                    r1 = r2 = i;
                else
                    r2 = i;
            }
        }

        n = r2 - r1 + 1;
        if (n >= 0) {
            int[] t = new int[n];
            for (int i = 0; i < n; i++)
                t[i] = r1 + i;
            this.setSelectionRows(t);
        } else
            this.clearSelection();
    }

    public void setEditable(boolean flag) {
        super.setEditable(flag);

    }

    public MProveTree(GExpert gx, DPanel dd, DrawTextProcess dpp) {
        this.gxInstance = gx;
        this.dpane = dd;
        this.dp = dpp;

        top = new DefaultMutableTreeNode();
        init_top();

        model = new DefaultTreeModel(top);
        this.setModel(model);

        getSelectionModel().setSelectionMode
                (TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

        TreeCellOpaqueRender treeRender = new TreeCellOpaqueRender();
        treeRender.setOpaque(false);
        setCellRenderer(treeRender);
        editor = new TreeCellOPaqueEditor(gxInstance);
        this.setCellEditor(editor);
        this.setEditable(true);

        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopupMenu(MProveTree.this, e.getX(), e.getY());
                    return;
                }

                int n = MProveTree.this.getRowCount();
                Rectangle r = MProveTree.this.getRowBounds(n - 1);
                if (e.getY() > r.getY() + r.getHeight())
                    MProveTree.this.setSelectionRow(-1);
                else {
                    TreePath path = MProveTree.this.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode n1 = (DefaultMutableTreeNode) path.getLastPathComponent();
                        MNode n2 = (MNode) n1.getUserObject();
                        MProveTree.this.dp.setUndoStructForDisPlay(n2.getLastUndo(), true);
                        if (e.getClickCount() > 1 && n1 == top) {
                        }
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) {
                    isButtonDown = true;
                    x1 = e.getX();
                    y1 = e.getY();
                    x2 = x1;
                    y2 = y1;
                    selectByRect(y1, y2);
                }
            }

            public void mouseReleased(MouseEvent e) {
                isButtonDown = false;
                x2 = e.getX();
                y2 = e.getY();
                x1 = y1 = x2 = y2 = 0;
                MProveTree.this.repaint();
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3)
                    return;

                if (isButtonDown) {
                    x2 = e.getX();
                    y2 = e.getY();
                    selectByRect(y1, y2);
                    MProveTree.this.repaint();
                }
            }

            public void mouseMoved(MouseEvent e) {
            }
        });

        this.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.
                            getLastPathComponent();
                    if (node != null) {
                        Object obj = node.getUserObject();
                        if (obj != null && obj instanceof MNode &&
                                !MProveTree.this.isEditing()) {
                            MNode n = (MNode) obj;
                            MProveTree.this.dp.flashmnode(n);
                            dpane.repaint();
                        }
                    }
                }
            }
        });
        createpopupMenu();
        this.setForeground(Color.white);
        this.setBackground(Color.white);
    }

    public void createpopupMenu() {
        popup = new mpopup();

    }

    public void showPopupMenu(JComponent comp, int x, int y) {
        int n = this.getSelectionCount();
        popup.setMul(n);
        popup.show(comp, x, y);
    }

    private MNode getSelectedMnode() {
        TreePath path = this.getSelectionPath();
        if (path == null) {
            return null;
        }
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.
                getLastPathComponent();
        if (nd == null) {
            return null;
        }
        return (MNode) nd.getUserObject();
    }

    public void expandSelectedNode() {
        TreePath path = this.getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (nd == null) {
            return;
        }
        MNode n = (MNode) nd.getUserObject();
        if (n == null) {
            return;
        }
        if (n.objSize() != 1) {
            return;
        }

        if (n.size() != 0) {
            nd.removeAllChildren();
            n.removeAllElements();
            model.reload();

        }

        if (n.size() == 0) {
            Vector v = n.getUndoList();
            if (v.size() > 1) {
                for (int i = 0; i < v.size(); i++) {
                    UndoStruct u = (UndoStruct) v.get(i);
                    MDraw d = new MDraw(u.toString());
                    d.adddrawStruct(u);
                    MNode n1 = new MNode();
                    n1.add(d);
                    DefaultMutableTreeNode t = new DefaultMutableTreeNode(n1);
                    nd.add(t);
                    n.addChild(n1);
                }
                model.reload();
                this.expandPath(path);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {


    }

    public void deleteRow() {
        TreePath path = this.getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (nd == null)
            return;

        MNode n = (MNode) nd.getUserObject();
        if (n == null) {
            return;
        }
        DefaultMutableTreeNode p = (DefaultMutableTreeNode) nd.getParent();
        if (p != null) {
            this.cancelEditing();
            MNode n2 = (MNode) p.getUserObject();
            n2.remove(n);
            this.loadmtree(topm);
            this.setEditorLastRow();
        }
    }

    public void combineSelection() {
        TreePath[] paths = this.getSelectionPaths();
        if (paths.length <= 1) return;
        this.cancelEditing();
        DefaultMutableTreeNode parent = null;
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            if (nd == null || nd == top)
                return;
            parent = (DefaultMutableTreeNode) nd.getParent();
            break;
        }
        MNode n = (MNode) parent.getUserObject();
        if (n == null)
            return;
        MNode nx = new MNode();

        int id = -1;
        int dk = 1;
        for (int i = 0; i < paths.length; i++) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            MNode n1 = (MNode) nd.getUserObject();
            nd.removeFromParent();
            n1.setIndex(dk++);
            int k = n.remove(n1);
            if (id == -1)
                id = k;

            nx.add(n1);
        }
        n.add(id, nx);
        loadmtree(topm);
    }

    public void expandSelection() {
        TreePath path = this.getSelectionPath();
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.
                getLastPathComponent();
        if (nd == null) {
            return;
        }
        nd.getUserObject();
    }

    public void addToProve(Vector v) {
    }

    public void stepEnd() {
        while (rstep > 0) {
            step();
        }
        rstep = -1;
    }


    public boolean isMStepEnd() {
        return rstep == this.getRowCount() - 1;

    }

    public boolean isMStepMid() {
        return rstep >= 0 && rstep < this.getRowCount();
    }

    public void step() {
        dp.clearFlash();

        if (rstep < 0) {
            this.run_to_begin();
        } else if (rstep >= this.getRowCount() - 1) {
            rstep = -1;
            this.setSelectionRow(-1);
            dp.runto();
            dp.clearFlash();
        } else {
            DefaultMutableTreeNode d = (DefaultMutableTreeNode) this.getPathForRow(rstep).
                    getLastPathComponent();
            MNode n = (MNode) d.getUserObject();
            DefaultMutableTreeNode d1 = (DefaultMutableTreeNode) this.getPathForRow(rstep + 1).
                    getLastPathComponent();
            MNode n1 = (MNode) d1.getUserObject();
            dp.run_to_prove(n.getFirstUndo(), getLastUndo(d1));
            rstep++;
            this.setSelectionRow(rstep);
        }
        dpane.repaint();
    }

    public void run_to_begin() {
        dp.UndoPure();
        rstep = 0;
    }

    public void run_to_end() {
        dp.redo();
        rstep = this.getRowCount() - 1;
        this.setSelectionRow(rstep);
    }

    public UndoStruct getLastUndo(DefaultMutableTreeNode d) {
        TreePath path = new TreePath(d.getPath());
        if (!d.isLeaf() && isCollapsed(path)) {
            int n = d.getChildCount();
            for (int i = n - 1; i >= 0; i--) {
                DefaultMutableTreeNode d1 = (DefaultMutableTreeNode) d.getChildAt(i);
                UndoStruct u = getLastUndo(d1);
                if (u != null) return u;
            }
        } else {
            MNode n1 = (MNode) d.getUserObject();
            return n1.getLastUndo();
        }
        return null;
    }

    public Dimension getPreferredSize() {
        Dimension dm = super.getPreferredSize();
        return dm;
    }

    public void clearAll() {
        this.cancelEditing();
        top.removeAllChildren();
        init_top();
        model.reload();
        this.setEditorLastRow();
    }

    public void addUndoObject(UndoStruct u) {
        if (u.isNodeValued()) {
            if (top.getChildCount() == 1) {
                DefaultMutableTreeNode nd = (DefaultMutableTreeNode) top.getChildAt(0);
                MNode m = (MNode) nd.getUserObject();
                if (m.objSize() == 1) {
                    MDraw d = new MDraw(u.toString());
                    d.adddrawStruct(u);
                    m.add(d);
                    m.addUndo(u);
                    this.reload();
                    this.setEditorLastRow();
                    return;
                }
            }

            MNode n = new MNode();
            MDraw d = new MDraw(u.toString());
            d.adddrawStruct(u);
            n.add(d);
            n.addUndo(u);
            this.addNewNode(n);

        } else {
            TreePath path = this.getPathForRow(this.getRowCount() - 1);
            if (path == null) {
                return;
            }
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode) path.
                    getLastPathComponent();
            MNode md = (MNode) nd.getUserObject();
            md.addUndo(u);
        }
    }


    class mpopup extends JPopupMenu implements ActionListener {
        private JMenuItem bd, bd1, ba, ba1, bc;

        public mpopup() {
            JMenuItem item = new JMenuItem(getLanguage(3101, "Delete"));

            bd = item;
            add(item);
            item.setActionCommand("Delete");
            item.addActionListener(this);
            bd1 = item = new JMenuItem(getLanguage(3102, "Delete this row"));
            item.addActionListener(this);
            add(item);
            item.setActionCommand("DTR");
            addSeparator();
            ba = item = new JMenuItem(getLanguage(3103, "Add a new row"));
            item.addActionListener(this);
            add(item);
            item.setActionCommand("AANR");
            ba1 = item = new JMenuItem(getLanguage(3104, "Append a term"));
            item.addActionListener(this);
            add(item);
            item.setActionCommand("AAT");
            bc = item = new JMenuItem(getLanguage(3105, "Combine selected rows"));
            item.addActionListener(this);
            add(item);
            item.setActionCommand("CSR");
        }

        public void setMul(int n) {
            if (n == 0) {
                bd.setEnabled(false);
                bd1.setEnabled(false);
                ba.setEnabled(true);
                ba1.setEnabled(true);
                bc.setEnabled(false);
            } else if (n > 1) {
                bd.setEnabled(false);
                bd1.setEnabled(false);
                ba.setEnabled(true);
                ba1.setEnabled(true);
                bc.setEnabled(true);
            } else {
                bd.setEnabled(true);
                bd1.setEnabled(true);
                ba.setEnabled(true);
                ba1.setEnabled(true);
                bc.setEnabled(false);
            }
        }

        public void show(Component invoker, int x, int y) {
            super.show(invoker, x, y);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("CSR")) {
                MProveTree.this.combineSelection();
            } else if (command.equals("Delete")) {
                MNode n = editor.getEditorValue();
                if (n != null) {
                    if (n.objSize() >= 1) {
                        if (n.myremoveLast()) {
                            MProveTree.this.cancelEditing();
                        }
                    } else
                        deleteRow();
                }
            } else if (command.equals("DTR")) {
                deleteRow();
            } else if (command.equals("AAT")) {

            } else if (command.equals("AANR")) {
                MProveTree.this.addNewNode(new MNode());
            }
        }

    }
}

/**
 * MNode is a class that represents a node in a tree structure, specifically for
 * managing mathematical objects and their associated undo structures.
 */
class MNode extends Vector {
    private int index = -1;

    Vector vundolist = new Vector();
    Vector vlist = new Vector();


    public MNode() {
        super(0);
    }

    public boolean containsUndo(UndoStruct u) {
        return vundolist.contains(u);
    }

    public void setIndex(int k) {
        index = k;
    }

    public int remove(MNode m) {
        for (int i = 0; i < size(); i++) {
            if (m == get(i)) {
                remove(i);
                return i;
            }
        }
        return -1;
    }

    public boolean myremoveLast() {
        if (vlist.size() == 0) {
            return false;
        }
        int n = vlist.size();
        vlist.remove(n - 1);
        return true;
    }

    public int getIndex() {
        return index;
    }

    public void addChild(MNode node) {
        super.add(node);
    }

    public void add(MObject node) {
        vlist.add(node);
    }

    public void addUndo(UndoStruct un) {
        vundolist.add(un);
    }

    public void replace(Object a, Object b) {
        if (!vlist.contains(a)) {
            return;
        }
        int size = vlist.size();
        for (int i = 0; i < size; i++) {
            if (vlist.get(i) == a) {
                vlist.remove(i);
                vlist.add(i, b);
                break;
            }
        }
    }

    public int objSize() {
        return vlist.size();
    }

    public Vector getUndoList() {
        Vector v = new Vector();
        v.addAll(vundolist);
        return v;
    }

    public MNode getChild(int id) {
        return (MNode) this.get(id);
    }

    public MObject getObject(int id) {
        return (MObject) vlist.get(id);
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < vlist.size(); i++) {
            s += vlist.get(i);
        }
        return s;
    }

    public Vector getAllUndoStruct() {
        Vector v1 = new Vector();
        v1.addAll(vundolist);
        return v1;
    }

    public UndoStruct getFirstUndo() {
        if (vundolist.size() == 0) {
            return null;
        }
        return (UndoStruct) vundolist.get(0);
    }

    public UndoStruct getLastUndo() {
        if (vundolist.size() == 0) {
            return this.getUndoFromDraw();
        }
        int n = vundolist.size();
        return (UndoStruct) vundolist.get(n - 1);
    }

    public UndoStruct getUndoFromDraw() {
        for (int i = 0; i < vlist.size(); i++) {
            MObject o = (MObject) vlist.get(i);
            if (o instanceof MDraw) {
                MDraw d = (MDraw) o;
                return d.getUndoStruct();
            }
        }
        return null;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        int n1 = in.readInt();
        int n2 = in.readInt();
        int n3 = in.readInt();
        for (int i = 0; i < n1; i++) {
            UndoStruct u = dp.getUndoById(in.readInt());
            if (u != null) {
                vundolist.add(u);
            }
        }
        for (int i = 0; i < n2; i++) {
            Object o = MObject.load(in, dp);
            if (o != null)
                vlist.add(o);
        }
        for (int i = 0; i < n3; i++) {
            MNode nd = new MNode();
            nd.Load(in, dp);
            this.add(nd);
        }
    }

    public void Save(DataOutputStream out) throws IOException {
        out.writeInt(vundolist.size());
        out.writeInt(vlist.size());
        out.writeInt(this.size());
        for (int i = 0; i < vundolist.size(); i++) {
            UndoStruct u = (UndoStruct) vundolist.get(i);
            out.writeInt(u.m_id);
        }
        for (int i = 0; i < vlist.size(); i++) {
            MObject obj = (MObject) vlist.get(i);
            obj.Save(out);
        }
        for (int i = 0; i < size(); i++) {
            MNode nd = (MNode) get(i);
            nd.Save(out);
        }
    }

}

/**
 * MSymbol is a class that represents a mathematical symbol in a tree structure.
 * It extends the MObject class and provides methods to load, save, and manage
 * the symbol's type and associated image icon.
 */
class MSymbol extends MObject {
    final static ImageIcon EQQ = GExpert.createImageIcon("images/symbol/eqq.gif");
    final static ImageIcon EQ = GExpert.createImageIcon("images/symbol/eq.gif");
    final static ImageIcon EXISTS = GExpert.createImageIcon("images/symbol/exist.gif");
    final static ImageIcon FOREVERY = GExpert.createImageIcon("images/symbol/for_every.gif");
    final static ImageIcon INFINITY = GExpert.createImageIcon("images/symbol/infinity.gif");
    final static ImageIcon NEQEVER = GExpert.createImageIcon("images/symbol/neqever.gif");
    final static ImageIcon NOTEQ = GExpert.createImageIcon("images/symbol/noteq.gif");
    final static ImageIcon SIM = GExpert.createImageIcon("images/symbol/sim.gif");
    final static ImageIcon EQSIM = GExpert.createImageIcon("images/symbol/eq_sim.gif");
    final static ImageIcon LESS = GExpert.createImageIcon("images/symbol/less.gif");
    final static ImageIcon TRI = GExpert.createImageIcon("images/symbol/triangle.gif");
    final static ImageIcon ANGLE = GExpert.createImageIcon("images/symbol/angle.gif");
    final static ImageIcon PARA = GExpert.createImageIcon("images/symbol/para.gif");
    final static ImageIcon PERP = GExpert.createImageIcon("images/symbol/perp.gif");

    public static String[] cSprefix = {"because", "hence"};
    static Vector vlist;
    int type1;

    public static void createAllIcons() {
        vlist = new Vector();
        for (int i = 0; i < cSprefix.length; i++) {
            ImageIcon icon = GExpert.createImageIcon("images/dtree/" +
                    cSprefix[i] + ".gif");
            if (icon == null) {
                CMisc.print(GExpert.getTranslationViaGettext("Can not find image: {0}", cSprefix[i]));
            } else {
                vlist.add(icon);
            }
        }
    }

    public static ImageIcon getSymbolIcon(int k) {
        return (ImageIcon) vlist.get(k);
    }

    public MSymbol(int t) {
        super(SYMBOL);
        type1 = t;
    }

    public int getSymbolType() {
        return type1;
    }

    public void setSymbolType(int t) {
        type1 = t;
    }

    public ImageIcon getImage() {
        return (ImageIcon) vlist.get(type1);
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        type1 = in.readInt();
    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        //  out.writeInt(type);
        out.writeInt(type1);
    }
}

/**
 * MPrefix is a class that represents a prefix in a mathematical proof tree.
 * It extends the MObject class and provides methods to manage the prefix type
 * and associated text.
 */
class MPrefix extends MObject {

    public static int GIVEN = 0;
    public static int TOPROVE = 1;

    public static String[] cSprefix = {"Given:", "To Prove:", "In", "and",
            "Similarly,", "Q.E.D."};
    private int type1;

    public MPrefix(int type) {
        super(PREFIX);
        this.type1 = type;
    }

    public void setPrefixType(int t) {
        type1 = t;
    }

    public int getPrefixType() {
        return type1;
    }

    public void setText(String s) {
    }

    public String toString() {
        if (type1 < cSprefix.length)
            return GExpert.getLanguage(cSprefix[type1]);
        else
            return "????"; // FIXME: What is this?
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        type1 = in.readInt();
        if (CMisc.version_load_now < 0.037) {
            if (type1 == 5)
                type1 = 2;
            else if (type1 == 7)
                type1 = 3;
            else if (type1 == 8)
                type1 = 4;
            else if (type1 == 10)
                type1 = 5;
        }
    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(type1);
    }
}

/**
 * MDraw is a class that represents a drawing object in a mathematical proof tree.
 * It extends the MObject class and provides methods to manage the drawing's
 * associated undo structures and text.
 */
class MDraw extends MObject {

    private Vector vunlist = new Vector();
    private String str = "";

    public MDraw() {
        super(DRAW);
    }

    public MDraw(String s) {
        super(DRAW);
        str = s;
    }

    public Vector getAllUndoStruct() {
        Vector v = new Vector();
        v.addAll(vunlist);
        return v;
    }

    public void setText(String s) {
        str = s;
    }

    public String getText() {
        return str;
    }

    public void adddrawStruct(UndoStruct undo) {
        if (!vunlist.contains(undo))
            vunlist.add(undo);
    }

    public UndoStruct getUndoStruct() {
        int n = vunlist.size();
        if (n == 0) return null;
        return (UndoStruct) vunlist.get(n - 1);
    }

    public int getdrawCount() {
        return vunlist.size();
    }

    public String toString() {
        if (str != null) {
            return str;
        } else if (vunlist.size() != 0) {
            String s = "";
            for (int i = 0; i < vunlist.size(); i++) {
                s += vunlist.get(i).toString();
            }
            return s;
        } else {
            return "NULL";
        }
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            int id = in.readInt();
            UndoStruct u = dp.getUndoById(id);
            if (u != null) {
                vunlist.add(u);
            }
        }

        str = ReadString(in);
    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        int n = vunlist.size();
        out.writeInt(n);
        for (int i = 0; i < n; i++) {
            UndoStruct u = (UndoStruct) vunlist.get(i);
            out.writeInt(u.m_id);
        }
        WriteString(out, str);
    }
}

/**
 * MText is a class that represents a text object in a mathematical proof tree.
 * It extends the MObject class and provides methods to manage the text string.
 */
class MText extends MObject {
    private String str = "";

    public MText() {
        super(TEXT);
    }

    public MText(String s) {
        super(TEXT);
        str = s;
    }

    public String toString() {
        return str;
    }

    public void setString(String s) {
        str = s;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        str = ReadString(in);
    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        WriteString(out, str);
    }
}

/**
 * MDrObj is a class that represents a mathematical object in a proof tree.
 * It extends the MObject class and provides methods to manage the object's
 * type, associated points, and validation checks.
 */
class MDrObj extends MObject {
    final public static int LINE = 0;
    final public static int TRIANGLE = 1;
    final public static int CIRCLE = 2;
    final public static int SQUARE = 3;
    final public static int AREA = 4;
    final public static int ANGLE = 5;
    final public static int PARALLELOGRAM = 6;
//    public static int VALUE = 7;

    final public static int RECTANGLE = 7;

    final public static int QUADRANGLE = 8;
    final public static int TRAPEZOID = 9;


    final static String[] pStrings = {"line", "triangle", "circle", "square", "area", "angle", "parallelogram",
            "rectangle", "quadrangle", "trapezoid"};

    final static String[] tipStrings = {"Please select two points", "Please select three non-collinear points",
            "Please select three points", "Please select four points", "Please select N(>=3) points", "Please select three points",
            "Please select four points", "Please select four points", "Please select four points"};

    static Vector vlist = new Vector();
    Vector objlist = new Vector();
    int type1;

    static void createAllIcons() {
        for (int i = 0; i < pStrings.length; i++) {
            ImageIcon icon = GExpert.createImageIcon("images/dtree/" + pStrings[i] + ".gif");
            if (icon != null) {
                vlist.add(icon);
            } else {
                CMisc.print(GExpert.getTranslationViaGettext("Can not find object icon {0}", pStrings[i]));
            }
        }
    }

    public String getTip() {
        if (type1 >= 0 && type1 < tipStrings.length)
            return tipStrings[type1];
        else return "";
    }


    public boolean check_valid() {
        switch (type1) {
            case LINE:
                return objlist.size() == 2 && objlist.get(0) != objlist.get(1);
            case TRIANGLE:
            case ANGLE:
                return objlist.size() == 3 && objlist.get(0) != objlist.get(1) &&
                        objlist.get(0) != objlist.get(2) && objlist.get(1) != objlist.get(2);
            case CIRCLE:
                return objlist.size() == 3 && objlist.get(1) != objlist.get(2);
            case SQUARE:
                return ck_pt4() && ck_square();

            case PARALLELOGRAM:
                return ck_pt4() && ck_parallelogram();
            case RECTANGLE:
                return ck_pt4() && ck_rectangle();
            case TRAPEZOID:
                return ck_pt4() && ck_trapezoid();
            case QUADRANGLE:
                return ck_pt4();
        }
        return true;
    }

    public boolean ck_pt4() {
        if (objlist.size() != 4) return false;

        CPoint p1 = (CPoint) objlist.get(0);
        CPoint p2 = (CPoint) objlist.get(1);
        CPoint p3 = (CPoint) objlist.get(2);
        CPoint p4 = (CPoint) objlist.get(3);
        return p1 != p2 && p1 != p3 && p2 != p3 && p1 != p4 && p2 != p4 && p3 != p4;
    }

    public boolean ck_parallelogram() {
        CPoint p1 = (CPoint) objlist.get(0);
        CPoint p2 = (CPoint) objlist.get(1);
        CPoint p3 = (CPoint) objlist.get(2);
        CPoint p4 = (CPoint) objlist.get(3);
        return DrawBase.check_para(p1, p2, p3, p4) && DrawBase.check_para(p1, p4, p2, p3);
    }


    public boolean ck_square() {
        CPoint p1 = (CPoint) objlist.get(0);
        CPoint p2 = (CPoint) objlist.get(1);
        CPoint p3 = (CPoint) objlist.get(2);
        CPoint p4 = (CPoint) objlist.get(3);

        return DrawBase.check_eqdistance(p1, p2, p2, p3) && ck_rectangle();
    }

    public boolean ck_trapezoid() {
        CPoint p1 = (CPoint) objlist.get(0);
        CPoint p2 = (CPoint) objlist.get(1);
        CPoint p3 = (CPoint) objlist.get(2);
        CPoint p4 = (CPoint) objlist.get(3);
        return DrawBase.check_para(p1, p2, p3, p4) || DrawBase.check_para(p1, p4, p2, p3);
    }

    public boolean ck_rectangle() {
        CPoint p1 = (CPoint) objlist.get(0);
        CPoint p2 = (CPoint) objlist.get(1);
        CPoint p3 = (CPoint) objlist.get(2);
        CPoint p4 = (CPoint) objlist.get(3);

        return DrawBase.check_perp(p1, p2, p2, p3) && ck_parallelogram();
    }


    public static ImageIcon getImageIcon(int k) {
        if (k < 0 || k >= vlist.size())
            return null;

        return (ImageIcon) vlist.get(k);
    }

    public static ImageIcon getImageIconFromName(String s) {
        if (s == null)
            return null;

        if (s.equalsIgnoreCase("line") || s.equalsIgnoreCase("number"))
            return null;

        if (s.equalsIgnoreCase("triangle") || s.equalsIgnoreCase("tri")) {
            return (ImageIcon) vlist.get(1);
        }
        if (s.equalsIgnoreCase("circle") || s.equalsIgnoreCase("cir")) {
            return (ImageIcon) vlist.get(2);
        }
        if (s.equalsIgnoreCase("square")) {
            return (ImageIcon) vlist.get(3);
        }
        if (s.equals("area")) {
            return (ImageIcon) vlist.get(4);
        }
        if (s.equalsIgnoreCase("parallelogram")) {
            return (ImageIcon) vlist.get(6);
        }

        if (s.equalsIgnoreCase("para")) {
            return MSymbol.PARA;
        }
        if (s.equalsIgnoreCase("perp")) {
            return MSymbol.PERP;
        }
        if (s.equalsIgnoreCase("angle")) {
            return MSymbol.ANGLE;
        }

        if (s.equalsIgnoreCase("rectangle")) {
            return (ImageIcon) vlist.get(7);
        }
        if (s.equalsIgnoreCase("quadrangle")) {
            return (ImageIcon) vlist.get(8);
        }

        if (s.equalsIgnoreCase("trapezoid")) {
            return (ImageIcon) vlist.get(9);
        }
        return null;
    }

    public int getObjectNum() {
        return objlist.size();
    }

    public CPoint getObject(int id) {
        if (id < objlist.size()) {
            return (CPoint) objlist.get(id);
        }
        return null;
    }

    public static int getPtAcount(int t) {
        if (t == 0) {
            return 2;
        }
        if (t == 1) {
            return 3;
        }
        if (t == 2) {
            return 3;
        }
        if (t == 3) {
            return 4;
        }
        if (t == 4) {
            return 4;
        }
        if (t == 5) {
            return 3;
        }
        if (t == 6) {
            return 4;
        }

        if (t == 7 || t == 8 || t == 9)
            return 4;
        return 0;
    }


    public boolean isPolygon() {
        return type1 != 0;
    }

    public ImageIcon getImageWithoutLine() {
        if (type1 == 0 || type1 >= vlist.size()) {
            return null;
        }
        return (ImageIcon) vlist.get(type1);
    }

    public ImageIcon getImage() {
        if (type1 >= vlist.size()) {
            return null;
        }
        return (ImageIcon) vlist.get(type1);
    }

    public void setType1(int t) {
        type1 = t;
    }

    public int getType1() {
        return type1;
    }

    public MDrObj(int t) {
        super(DOBJECT);
        type1 = t;
    }

    public void clear() {
        objlist.clear();
    }

    public void add(Object obj) {
        if (obj != null) {
            objlist.add(obj);
        }
    }

    public String toString() {
        String s1 = "";
        for (int i = 0; i < objlist.size(); i++) {
            s1 += objlist.get(i);
        }
        return s1;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        type1 = in.readInt();
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            int id = in.readInt();
            objlist.add(dp.getPointById(id));
        }

    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(type1);
        out.writeInt(objlist.size());
        for (int i = 0; i < objlist.size(); i++) {
            CPoint p = (CPoint) objlist.get(i);
            out.writeInt(p.m_id);
        }
    }
}

/**
 * MObject is a base class for representing various types of mathematical objects.
 * It encapsulates basic properties such as the object type and provides methods for
 * creating, loading, saving, and managing object data. Subclasses should extend this
 * class to implement specialized behavior appropriate to their specific object type.
 */
class MObject {
    static String[] pStrings = {"Text", "Keywords", "Symbol", "Assertion",
            "Object", "Draw", "Construction", "Equation",
            "Rule"};

    final static int NONE = 0;
    final static int TEXT = 1;
    final static int PREFIX = 2;
    final static int SYMBOL = 3;
    final static int ASSERT = 4;
    final static int DOBJECT = 5;
    final static int DRAW = 6;

    final static int EQUATION = 8;
    final static int RULE = 9;

    int type;

    public MObject(int t) {
        type = t;
    }

    public int getType() {
        return type;
    }

    public void setType(int t) {
        type = t;
    }


    public String toString() {
        return "     ";
    }

    public static MObject createObject(int t1, int t2) {
        if (t1 == 0) {
            return new MText("  ");
        }
        if (t1 == 1) {
            return new MPrefix(t2);
        }
        if (t1 == 2) {
            return new MSymbol(t2);
        }
        if (t1 == 3) {
            return new MAssertion(t2);
        }
        if (t1 == 4) {
            return new MDrObj(t2);
        }
        if (t1 == 5) {
            return new MDraw("");
        }
        if (t1 == 6) {
            return null;
        }
        if (t1 == 7) {
            MEquation eq = new MEquation();
            eq.addTerm(new MEqTerm(-1, new MDrObj(0)));
            return eq;
        }
        if (t1 == 8) {
            return new MRule(t2);
        }
        return null;
    }

    protected static void WriteString(DataOutputStream out, String s) throws
            IOException {
        out.writeInt(s.length());
        out.writeChars(s);
    }

    protected static void WriteFont(DataOutputStream out, Font f) throws
            IOException {
        String s = f.getName();
        WriteString(out, s);
        out.writeInt(f.getStyle());
        out.writeInt(f.getSize());
    }

    protected static String ReadString(DataInputStream in) throws IOException {
        int size = in.readInt();
        if (size == 0) {
            return new String("");
        }
        String s = new String();
        for (int i = 0; i < size; i++) {
            s += in.readChar();
        }
        return s;
    }

    public static MObject load(DataInputStream in, DrawProcess dp) throws
            IOException {
        int t = in.readInt();
        switch (t) {
            case MObject.TEXT: {
                MObject m = new MText();
                m.Load(in, dp);
                return m;
            }
            case MObject.PREFIX: {
                MPrefix m = new MPrefix(0);
                m.Load(in, dp);
                return m;
            }
            case MObject.SYMBOL: {
                MSymbol m = new MSymbol(0);
                m.Load(in, dp);
                return m;
            }
            case MObject.ASSERT: {
                MAssertion m = new MAssertion(0);
                m.Load(in, dp);
                return m;
            }
            case MObject.DOBJECT: {
                MDrObj m = new MDrObj(0);
                m.Load(in, dp);
                return m;
            }
            case MObject.DRAW: {
                MDraw m = new MDraw();
                m.Load(in, dp);
                return m;
            }
            case MObject.EQUATION: {
                MEquation eq = new MEquation();
                eq.Load(in, dp);
                return eq;
            }
            case MObject.RULE: {
                MRule r = new MRule(0);
                r.Load(in, dp);
                return r;
            }
        }
        return null;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        type = in.readInt();
    }

    public void Save(DataOutputStream out) throws IOException {
        out.writeInt(type);
        out.writeInt(type);
    }
}

// TODO. This seems to be unfinished.
/**
 * MRule is a class that represents a rule in the GExpert system.
 * It extends the MObject class and provides functionality to load and save
 * rule data, as well as to manage the rule index and name.
 */
class MRule extends MObject {
    int rindex;
    public static String[] cStrings = {"Rule1", "Rule2", "Rule3", "SAS", /* "AAS", */ "SSS", "ASA"}; // FIXME. AAS is missing among the files.

    public MRule(int n) {
        super(RULE);
        rindex = n;
    }

    public String toString() {

        if (rindex < 0 || rindex >= cStrings.length) {
            return GExpert.getLanguage("by Rule?");
        }
        return GExpert.getTranslationViaGettext("by {0}", GExpert.getLanguage(cStrings[rindex]));
    }

    public String getRuleName() {
        int n = rindex;
        return cStrings[n];
        // return "Rule" + n;
    }

    public int getRuleIndex() {
        return rindex;
    }

    public void setRuleIndex(int n) {
        rindex = n;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        rindex = in.readInt();
    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(rindex);
    }

}

/**
 * MEquation is a class that represents an equation in the GExpert system.
 * It extends the MObject class and provides functionality to manage terms
 * in the equation, as well as to load and save the equation data.
 */
class MEquation extends MObject {
    private Vector vlist = new Vector();

    public MEquation() {
        super(EQUATION);
    }

    public void clearAll() {
        vlist.clear();
    }

    public int getTermCount() {
        return vlist.size();
    }

    public void addTerm(MEqTerm t) {
        vlist.add(t);
    }

    public MEqTerm getTerm(int index) {
        return (MEqTerm) vlist.get(index);
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            vlist.add(MEqTerm.Load(in, dp));
        }

    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(vlist.size());
        for (int i = 0; i < vlist.size(); i++) {
            MEqTerm t = (MEqTerm) vlist.get(i);
            t.Save(out);
        }

    }
}

/**
 * MEqTerm is a class that represents a term in an equation in the GExpert system.
 * It provides functionality to manage the type of the term and the associated object.
 */
class MEqTerm {
    public static String[] cStrings = {" + ", " - ", " * ", " / ", " = ", " > ",
            " >= ", " < ", " <= ", " //= "};
    int etype;
    MDrObj obj;

    public MEqTerm() {
        etype = -1;
        obj = null;
    }

    public boolean isEqFamily() {
        return etype >= 4;
    }

    public boolean isPolygon() {
        return obj.isPolygon();
    }

    public MEqTerm(int t, MDrObj o) {
        etype = t;
        obj = o;
    }

    public void setEType(int t) {
        etype = t;
    }

    public void setObject(MDrObj d) {
        obj = d;
    }

    public int getEType() {
        return etype;
    }

    public MDrObj getObject() {
        return obj;
    }

    public static MEqTerm Load(DataInputStream in, DrawProcess dp) throws
            IOException {
        int t = in.readInt();
        MDrObj o = new MDrObj(0);
        in.readInt();
        o.Load(in, dp);
        return new MEqTerm(t, o);

    }

    public void Save(DataOutputStream out) throws IOException {
        out.writeInt(etype);
        obj.Save(out);
    }

}

/**
 * MAssertion is a class that represents an assertion in the GExpert system.
 * It extends the MObject class and provides functionality to manage the
 * assertion type and associated objects.
 */
class MAssertion extends MObject {
    public static String[] cStrings = {"Collinear", "Parallel", "Perpendicular",
            "Midpoint", "Eqdistant", "Cyclic",
            "Eqangle", "Congruent", "Similar", "Distance Less",
            "Angle Less", "Concurrent", "Perp-Bisector",
            "Parallelogram",

            "Right Triangle", "Isosceles Triangle",
            "Iso-Right Triangle", "Equilateral Triangle", "Trapezoid",
            "Rectangle", "Square",
            "Between", "Angle Inside", "Angle Outside", "Triangle Inside",
            "Para Inside", "Opposite Inside", "Same Side", "Convex"};

    final public static int COLL = 0;
    final public static int PARA = 1;
    final public static int PERP = 2;
    final public static int MID = 3;
    final public static int EQDIS = 4;
    final public static int CYCLIC = 5;
    final public static int EQANGLE = 6;
    final public static int CONG = 7;
    final public static int SIM = 8;
    final public static int DISLESS = 9;
    final public static int ANGLESS = 10;
    final public static int CONCURRENT = 11;
    final public static int PERPBISECT = 12;
    final public static int PARALLELOGRAM = 13;

    final public static int R_TRIANGLE = 14;
    final public static int ISO_TRIANGLE = 15;
    final public static int R_ISO_TRIANGLE = 16;
    final public static int EQ_TRIANGLE = 17;
    final public static int TRAPEZOID = 18;
    final public static int RECTANGLE = 19;
    final public static int SQUARE = 20;
    final public static int BETWEEN = 21;
    final public static int ANGLE_INSIDE = 22;
    final public static int ANGLE_OUTSIDE = 23;
    final public static int TRIANGLE_INSIDE = 24;
    final public static int PARA_INSIDE = 25;
    final public static int OPPOSITE_SIDE = 26;
    final public static int CONVEX = 28;
    final public static int SAME_SIDE = 27;


    private Vector objlist = new Vector();
    private int type1;

    public MAssertion(int t) {
        super(ASSERT);
        type1 = t;
    }

    public int getAssertionType() {
        return type1;
    }

    public void setAssertionType(int t) {
        type1 = t;
    }

    public boolean ShowType() {
        if (type1 == 0 || type1 == 3) {
            return true;
        }
        return false;
    }

    public String getShowString1() {
        if (type1 == 0 || type1 == 3) {
            return this.toString();
        }
        switch (type1) {
            case R_TRIANGLE:
                return plmn(0, 3) + " is a " + cStrings[type1].toLowerCase();
            case R_ISO_TRIANGLE:
            case ISO_TRIANGLE:
            case EQ_TRIANGLE:
                return plmn(0, 3) + " is an " + cStrings[type1].toLowerCase();
            case TRAPEZOID:
                return plmn(0, 4) + " is a trapezoid";
            case RECTANGLE:
                return plmn(0, 4) + " is a rectangle";
            case BETWEEN:
                return plmn(0, 1) + " is between " + plmn(1, 3);
            case ANGLE_INSIDE:
                return plmn(0, 1) + " is inside";
            case ANGLE_OUTSIDE:
                return plmn(0, 1) + " is outside";
            case TRIANGLE_INSIDE:
                return plmn(0, 1) + " is inside";
            case PARA_INSIDE:
                return plmn(0, 1) + " is inside";
            case OPPOSITE_SIDE:
                return plmn(0, 2) + " is on the opposite side of " + plmn(2, 4);
            case SAME_SIDE:
                return plmn(0, 2) + " is on the same side of " + plmn(2, 4);
            case CONVEX:
                return plmn() + " is convex";

            default:
                int t = objlist.size();
                int n = t / 2;
                String s = "";
                for (int i = 0; i < n; i++) {
                    s += objlist.get(i);
                }
                return s;
        }
    }

    public String plmn() {
        return plmn(0, objlist.size());
    }

    public String plmn(int n, int m) {
        String s = "";
        if (n < 0 || m > objlist.size())
            return s;

        for (int i = n; i < m; i++)
            s += objlist.get(i);
        return s;
    }

    public String getShowString2() {
        if (type1 == 0 || type1 == 3) {
            return null;
        }
        switch (type1) {
            case ANGLE_INSIDE:
                return plmn(1, 4);
            case ANGLE_OUTSIDE:
                return plmn(1, 4);
            case TRIANGLE_INSIDE:
                return plmn(1, 4);
            case PARA_INSIDE:
                return plmn(1, 5);
            default:
                int t = objlist.size();
                int n = t / 2;
                String s = "";
                for (int i = n; i < t; i++) {
                    s += objlist.get(i);
                }
                return s;
        }
    }

    public ImageIcon getImageIcon() {
        if (type1 == COLL || type1 == MID) {
            return null;
        }
        if (type1 == PARA) {
            return MSymbol.PARA;
        }
        if (type1 == PERP) {
            return MSymbol.PERP;
        }
        if (type1 == EQDIS || type1 == EQANGLE) {
            return MSymbol.EQ;
        }
        if (type1 == SIM) {
            return MSymbol.SIM;
        }
        if (type1 == CONG) {
            return MSymbol.EQSIM;
        }
        if (type1 == DISLESS || type1 == ANGLESS) {
            return MSymbol.LESS;
        }
        return MSymbol.EQQ;
    }

    public int getobjNum() {
        return objlist.size();
    }

    public void clearObjects() {
        objlist.clear();
    }

    public void addAll(MAssertion a) {
        objlist.clear();
        int n = a.getobjNum();
        for (int i = 0; i < n; i++) {
            Object obj = a.getObject(i);
            if (obj != null) {
                objlist.add(obj);
            }
        }
    }

    public void addObject(CPoint p) {
        if (p != null) {
            objlist.add(p);
        }
    }

    public void addObject(Object p) {
        if (p != null) {
            objlist.add(p);
        }

    }

    public Object getObject(int id) {
        return objlist.get(id);
    }

    public String toString() {
        String s1 = cStrings[type1];
        switch (type1) {
            case COLL:
            case MID:
            case CYCLIC:
                return s1 + " " + this.getTStrings();
            case CONCURRENT: {
                int n = objlist.size();
                n = n / 2;
                for (int i = 0; i < n; i++) {
                    if (i == 0) {
                        s1 += " ";
                    } else {
                        s1 += ",";
                    }
                    s1 = s1 + objlist.get(i * 2) + objlist.get(i * 2 + 1);
                }
                return s1;
            }
            case PARALLELOGRAM:
                return this.getSSTrings() + " is a " + s1;
            case PERPBISECT:
                int n = objlist.size();
                if (n < 4) {
                    return s1;
                }
                return objlist.get(0).toString() + objlist.get(1).toString() +
                        " is the " + s1 + " of " +
                        objlist.get(2).toString() + objlist.get(3).toString();
            default:
                CMisc.print(GExpert.getTranslationViaGettext("assertion type: {0} not defined.", s1));
                return s1;
        }

    }

    public Object getLabelObject(int d) {
        switch (type1) {
            case 0:
                if (d == 0) {
                    return getTStrings() + " are collinear";
                } else {
                    return null;
                }
            case 1:
            case 2:
            case 4:
            case 9:
                break;
            case 5:
                break;
            case 6:
        }
        return null;

    }

    public String sr(int i) {
        return objlist.get(i).toString();
    }

    public void addItem(Object p) {
        if (p != null) {
            objlist.add(p);
        }
    }

    public boolean checkValid() {
        if ((type == MID || type == COLL) && objlist.size() == 3) {
            return true;
        }
        if ((type == SIM || type == EQANGLE || type == CONG) &&
                objlist.size() == 6) {
            return true;
        }
        if (type == CONCURRENT) {
            return objlist.size() == 6;
        }

        if (objlist.size() == 4) {
            return true;
        }
        return false;
    }

    public String getSSTrings() {
        String s = "";
        for (int i = 0; i < objlist.size(); i++) {
            s += objlist.get(i);
        }
        return s;

    }

    public String getTStrings() {
        String s = "";
        for (int i = 0; i < objlist.size(); i++) {
            if (i != 0) {
                s += ",";
            }
            s += objlist.get(i);
        }
        return s;
    }

    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        super.Load(in, dp);
        type1 = in.readInt();
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            int id = in.readInt();
            objlist.add(dp.getPointById(id));
        }

    }

    public void Save(DataOutputStream out) throws IOException {
        super.Save(out);
        out.writeInt(type1);
        out.writeInt(objlist.size());
        for (int i = 0; i < objlist.size(); i++) {
            CPoint p = (CPoint) objlist.get(i);
            if (p == null)
                out.writeInt(-1);
            else
                out.writeInt(p.m_id);
        }
    }

}
