package wprover;

import gprover.*;
import maths.TMono;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * NdgDialog is a custom dialog class that extends JBaseDialog and implements
 * ActionListener, MouseMotionListener, MouseListener, ChangeListener,
 * TableModelListener, and ListSelectionListener interfaces. It provides
 * functionality to display and manage non-degenerate conditions in a graphical
 * user interface.
 */
public class NdgDialog extends JBaseDialog implements ActionListener, MouseMotionListener, MouseListener,
        ChangeListener, TableModelListener, ListSelectionListener {
    private DrawProcess dp;
    private static int WD = 700;
    private static int HD = 500;
    private GTerm gt;
    JTable tabel1, tabel2, tabel3;
    ndgTableModel model1;
    ndgTableModel1 model2, model3;
    DefaultListSelectionModel lselect1;

    private JSplitPane spane;
    private JTabbedPane tt;
    JSplitPane sptop, sp1;

    private GExpert gxInstance;

    /**
     * Constructs a new NdgDialog with the specified GExpert, GTerm, and DrawProcess instances.
     *
     * @param gx the GExpert instance to associate with this NdgDialog
     * @param gt the GTerm instance to associate with this NdgDialog
     * @param dp the DrawProcess instance to associate with this NdgDialog
     */
    public NdgDialog(GExpert gx, GTerm gt, DrawProcess dp) {
        super(gx.getFrame(), gx.getLanguage("Nondegenerate Conditions"));
        gxInstance = gx;

        this.gt = gt;
        this.dp = dp;

        tt = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);

        tt.addChangeListener(this);
        this.getConstructions();

        model1 = new ndgTableModel();
        String c1 = gxInstance.getLanguage("The Simplified Nondegenerate Conditions");
        String c2 = gxInstance.getLanguage("The Final Nondegenerate Conditions");
        model2 = new ndgTableModel1(c1);
        model3 = new ndgTableModel1(c2);
        tabel1 = new JTable(model1);
        tabel2 = new JTable(model2);
        tabel3 = new JTable(model3);

        tabel1.setDragEnabled(true);

        tabel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model1.addTableModelListener(this);
        lselect1 = new DefaultListSelectionModel();
        lselect1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabel1.setSelectionModel(lselect1);
        lselect1.addListSelectionListener(this);

        JScrollPane pane = new JScrollPane(tabel1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
            public Dimension getPreferredSize() {
                Dimension dm = tabel1.getPreferredSize();
                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }
        };
        JScrollPane pane2 = new JScrollPane(tabel2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
            public Dimension getPreferredSize() {
                Dimension dm = tabel2.getPreferredSize();
                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }
        };
        JScrollPane pane3 = new JScrollPane(tabel3, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
            public Dimension getPreferredSize() {
                Dimension dm = tabel3.getPreferredSize();
                dm.setSize(dm.getWidth(), dm.getHeight() + 30);
                return dm;
            }
        };

        sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane2, pane3);

        JSplitPane panelx = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, sp1);
        sptop = panelx;

        this.getContentPane().add(new JScrollPane(panelx));
        panelx.resetToPreferredSizes();
        panelx.revalidate();
        spane = panelx;

        this.setSize(WD, HD);
        sp1.setDividerLocation(WD / 2 - 10);
        sp1.resetToPreferredSizes();

        int w = gx.getWidth();
        int h = gx.getHeight();
        int x = gx.getX();
        int y = gx.getY();
        this.setLocation(x + w / 2 - WD / 2, y + h / 2 - HD / 2);
    }

    /**
     * Handles list selection events for the NdgDialog.
     *
     * @param e the list selection event
     */
    public void valueChanged(ListSelectionEvent e) {
        int n = tabel1.getSelectedRow();
        Cons c = (Cons) model1.getValueAt(n, 0);
        if (gxInstance != null)
            gxInstance.getpprove().setSelectedConstruction(c);
        Object o = model1.getValueAt(n, 1);
        if (o != null && o instanceof CNdg) {
            CNdg dd = (CNdg) o;
            for (int i = 0; i < model2.getRowCount(); i++) {
                if (model2.getValueAt(i, 0) == dd.equ) {
                    tabel2.getSelectionModel().setSelectionInterval(i, i);
                    break;
                }
            }
        }
    }


    public void tableChanged(TableModelEvent e) {

    }

    public void stateChanged(ChangeEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
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
     * Handles action events for the NdgDialog.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    /**
     * Retrieves the constructions for the NdgDialog.
     */
    public void getConstructions() {
    }

    /**
     * Sets the values for the NdgDialog models based on the provided vectors.
     *
     * @param v1 the vector containing constructions
     * @param v2 the vector containing non-degenerate conditions
     * @param v3 the vector containing simplified non-degenerate conditions
     * @param v4 the vector containing final non-degenerate conditions
     */
    public void setValue(Vector v1, Vector v2, Vector v3, Vector v4) {
        model1.reset();
        model2.reset();
        model3.reset();

        for (int i = 0; i < v1.size(); i++) {
            Cons c = (Cons) v1.get(i);
            CNdg d = fd_ndg(c, v2);
            model1.addElement(c, d);
        }

        for (int i = 0; i < v3.size(); i++) {
            CNdg c = (CNdg) v3.get(i);
            model2.addElement(c);
        }

        for (int i = 0; i < v4.size(); i++) {
            CNdg c = (CNdg) v4.get(i);
            model3.addElement(c);
        }
        model3.addElement(" ");

        GeoPoly poly = GeoPoly.getPoly();
        for (int i = 0; i < v4.size(); i++) {
            CNdg c = (CNdg) v4.get(i);
            model3.addElement(poly.getAllPrinted(getTMono(c)));
        }

        spane.resetToPreferredSizes();
    }

    /**
     * Retrieves the x-index of the specified point.
     *
     * @param n the index of the point
     * @return the x-index of the point
     */
    public int dxindex(int n) {
        CPoint pt = dp.fd_point(n);
        if (pt == null)
            return 0;
        return pt.x1.xindex;
    }

    /**
     * Retrieves the y-index of the specified point.
     *
     * @param n the index of the point
     * @return the y-index of the point
     */
    public int dyindex(int n) {
        CPoint pt = dp.fd_point(n);
        return pt.y1.xindex;
    }

    /**
     * Retrieves the TMono representation of the specified non-degenerate condition.
     *
     * @param d the non-degenerate condition
     * @return the TMono representation of the condition
     */
    public TMono getTMono(CNdg d) {
        if (d == null)
            return null;
        GeoPoly poly = GeoPoly.getPoly();
        switch (d.type) {
            case Gib.NDG_COLL:
                return poly.collinear(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]), dxindex(d.p[2]), dyindex(d.p[2]));
            case Gib.NDG_NEQ:
            case Gib.NDG_NON_ISOTROPIC:
                return poly.perpendicular(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]));
            case Gib.NDG_PARA:
                return poly.parallel(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            case Gib.NDG_PERP:
                return poly.perpendicular(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            case Gib.NDG_CYCLIC:
                return poly.cyclic(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            case Gib.NDG_CONG:
                return poly.eqdistance(dxindex(d.p[0]), dyindex(d.p[0]), dxindex(d.p[1]), dyindex(d.p[1]),
                        dxindex(d.p[2]), dyindex(d.p[2]), dxindex(d.p[3]), dyindex(d.p[3]));
            default:
                System.out.println("Error NDG type: " + d.type);
        }
        return null;
    }

    /**
     * Finds the non-degenerate condition associated with the specified construction.
     *
     * @param c  the construction
     * @param v2 the vector containing non-degenerate conditions
     * @return the non-degenerate condition associated with the construction, or null if not found
     */
    public CNdg fd_ndg(Cons c, Vector v2) {
        for (int i = 0; i < v2.size(); i++) {
            CNdg d = (CNdg) v2.get(i);
            if (d.dep == c)
                return d;
        }
        return null;
    }

    /**
     * The ndgTableModel class is a custom table model that extends DefaultTableModel.
     * It is used to manage the data displayed in the JTable for non-degenerate conditions.
     */
    class ndgTableModel extends DefaultTableModel {
        String c1 = gxInstance.getLanguage("Construction");
        String c2 = gxInstance.getLanguage("Nondegenerate Condition");

        Object[] cons = new Object[100];
        Object[] ndgs = new Object[100];
        int num = 0;


        public ndgTableModel() {
        }

        public void addElement(Object o1, Object o2) {
            if (o2 == null)
                o2 = "";

            cons[num] = o1;
            ndgs[num] = o2;
            num++;
            this.fireTableDataChanged();

        }

        public void reset() {
            num = 0;
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return num;
        }

        public String getColumnName(int col) {
            if (col == 0)
                return c1;
            else return c2;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0)
                return cons[row];
            else return ndgs[row];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }

    /**
     * The ndgTableModel1 class is a custom table model that extends DefaultTableModel.
     * It is used to manage the data displayed in the JTable for non-degenerate conditions.
     */
    class ndgTableModel1 extends DefaultTableModel {
        String c1 = null;

        Object[] ndgs = new Object[100];
        int num = 0;


        public ndgTableModel1() {
        }

        public ndgTableModel1(String s) {
            c1 = s;
        }

        public void addElement(Object o1) {
            ndgs[num] = o1;
            num++;
        }

        public void reset() {
            num = 0;
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            return 1;
        }

        public int getRowCount() {
            return num;
        }

        public String getColumnName(int col) {
            return c1;
        }

        public Object getValueAt(int row, int col) {
            return ndgs[row];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }

}