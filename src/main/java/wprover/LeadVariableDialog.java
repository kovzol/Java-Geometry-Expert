package wprover;

import maths.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * LeadVariableDialog is a class that extends JBaseDialog and implements MouseListener and ActionListener.
 * It is used to display and manipulate lead variables in a graphical user interface.
 */
public class LeadVariableDialog extends JBaseDialog implements MouseListener, ActionListener {

    private JTabbedPane tpane;
    private JTable table;
    private LVTableModel model;
    private InspectPanel ipane = null;
    private Vector vdata = new Vector();
    private GExpert gxInstance;
    private JButton bdtail;
    protected static GeoPoly poly = GeoPoly.getPoly();
    protected static CharSet charset = CharSet.getinstance();

    /**
     * Constructs a new LeadVariableDialog with the specified GExpert instance.
     *
     * @param f the GExpert instance to associate with this LeadVariableDialog
     */
    public LeadVariableDialog(GExpert f) {

        super(f.getFrame());
        gxInstance = f;
        this.setTitle(getLanguage("Algebraic Translation"));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        table = new JTable((model = new LVTableModel()));
        table.addMouseListener(this);
        table.setDragEnabled(true);
        TableColumn c1 = table.getColumnModel().getColumn(0);
        c1.setMaxWidth(60);
        TableColumn c2 = table.getColumnModel().getColumn(1);
        c2.setMaxWidth(60);
        TableColumn c3 = table.getColumnModel().getColumn(2);
        c3.setMaxWidth(60);
        JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRow() != -1)
                    bdtail.setEnabled(true);
                else bdtail.setEnabled(false);
            }
        });
        tpane = new JTabbedPane(JTabbedPane.BOTTOM);
        tpane.addTab(getLanguage("Variables"), pane);
        panel.add(tpane);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT)) {
            public Dimension getMaximumSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(Integer.MAX_VALUE, dm.getHeight());
                return dm;
            }
        };
        JButton bb = new JButton(getLanguage("Reduce"));
        JButton b = new JButton(getLanguage("Details"));
        JButton b1 = new JButton(getLanguage("Reload"));
        JButton b2 = new JButton(getLanguage("Close"));
        bb.setActionCommand("Reduce");
        b.setActionCommand("Details");
        b1.setActionCommand("Reload");
        b2.setActionCommand("Close");

        bb.addActionListener(this);
        b.addActionListener(this);
        b1.addActionListener(this);
        b2.addActionListener(this);
        p2.add(Box.createHorizontalGlue());
        p2.add(bb);
        p2.add(b);
        p2.add(b1);
        p2.add(b2);
        bdtail = b;
        b.setEnabled(false);
        panel.add(p2);

        setSize(600, 420);
        this.setLocation(f.getX(), f.getY());
        this.getContentPane().add(panel);
    }

    /**
     * Gets the language string for the specified key.
     *
     * @param s the key for the language string
     * @return the language string associated with the key
     */
    String getLanguage(String s) {
        return GExpert.getLanguage(s);
    }

    /**
     * Loads variables from the specified vector and updates the table.
     *
     * @param s the vector containing the variables to load
     * @param r a boolean indicating whether to reduce the variables
     */
    public void loadVariable(Vector s, boolean r) {
        try {
            loadAllPoints(s, r);
        } catch (OutOfMemoryError ee) {
            JOptionPane.showMessageDialog(gxInstance, ee.getMessage() + "\n" + "Out of Memory");
            return;
        }
    }

    /**
     * Loads all points from the specified vector and updates the table.
     *
     * @param v the vector containing the points to load
     * @param r a boolean indicating whether to reduce the points
     */
    public void loadAllPoints(Vector v, boolean r) {
        Vector vdata = new Vector();

        for (int i = 0; i < v.size(); i++) {
            CPoint p = (CPoint) v.get(i);
            Param p1 = p.x1;
            Param p2 = p.y1;
            if (p1 != null || p2 != null) {
                Vector o1 = new Vector();
                Vector o2 = new Vector();
                o1.add(p);
                o2.add("");

                o1.add(p1);
                o2.add(p2);
                o1.add(poly.plength(p1.m));
                o2.add(poly.plength(p2.m));

                if (!r) {
                    if (p1.m != null)
                        o1.add(poly.printSPoly(p1.m));
                    else o1.add("");
                    if (p2.m != null)
                        o2.add(poly.printSPoly(p2.m));
                    else o2.add("");
                } else {
                    Param[] pm = gxInstance.dp.parameter;
                    if (p1.m != null)
                        o1.add(poly.printSPoly(poly.reduce(poly.pcopy(p1.m), pm)));
                    else o1.add("");
                    if (p2.m != null)
                        o2.add(poly.printSPoly(poly.reduce(poly.pcopy(p2.m), pm)));
                    else o2.add("");
                }
                vdata.add(o1);
                vdata.add(o2);
            }
        }
        this.vdata.clear();
        this.vdata.addAll(vdata);
        model.setDataList(vdata);
    }

    /**
     * Handles action events for the dialog.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Reload")) {
            this.loadVariable(gxInstance.dp.getPointList(), false);
        } else if (s.equals("Close")) {
            this.setVisible(false);
        } else if (s.equals("Details")) {
            inspectTerm();
        } else if (s.equals("Reduce")) {
            if (tpane.getSelectedIndex() == 0)
                this.loadVariable(gxInstance.dp.getPointList(), true);
            else
                ipane.reduce();
        }
    }

    /**
     * Handles mouse click events for the table.
     *
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == table) {
            if (e.getClickCount() >= 2)
                inspectTerm();
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
     * Inspects the selected term in the table and displays its details.
     */
    public void inspectTerm() {

        if (tpane.getTabCount() > 1) {
            tpane.removeTabAt(1);
            ipane = null;
        }

        int n = table.getSelectedRow();
        if (n < 0 || n >= model.getRowCount()) return;
        Vector v = (Vector) vdata.get(n);

        Object p = getPoints(n);

        if (ipane == null) {
            ipane = new InspectPanel();
            tpane.addTab("" + p, new JScrollPane(ipane,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        } else
            tpane.setTitleAt(1, "" + p);
        ipane.loadValue(n, p, v);
        tpane.setSelectedIndex(1);
    }

    /**
     * Gets the points associated with the specified row index.
     *
     * @param n the row index
     * @return the points associated with the row index
     */
    public Object getPoints(int n) {
        if (n % 2 != 0)
            n -= 1;
        Vector v1 = (Vector) vdata.get(n);
        return v1.get(0);
    }

    /**
     * LVTableModel is a custom table model for displaying lead variables in a JTable.
     * It extends DefaultTableModel and provides methods to manage the data displayed in the table.
     */
    class LVTableModel extends DefaultTableModel {
        Vector vlist = new Vector();
        Vector vdlist = new Vector();

        public LVTableModel() {
            vlist.add(getLanguage("Name"));
            vlist.add(getLanguage("Variable"));
            vlist.add(getLanguage("Length"));
            vlist.add(getLanguage("Polynomial"));
        }

        public void setDataList(Vector v) {
            vdlist.clear();
            vdlist.addAll(v);
            this.fireTableDataChanged();
        }

        public int getColumnCount() {
            if (vlist == null)
                return 0;
            return vlist.size();
        }

        public int getRowCount() {
            if (vdlist == null)
                return 0;
            return vdlist.size();
        }

        public String getColumnName(int col) {
            return vlist.get(col).toString();
        }

        public Object getValueAt(int row, int col) {
            Vector v = (Vector) vdlist.get(row);
            return v.get(col);
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
     * InspectPanel is a custom JPanel that displays the details of a selected lead variable.
     * It contains a JTable and a JEditorPane for displaying the variable's properties and polynomial.
     */
    class InspectPanel extends JPanel {
        private JTable table1;
        private JEditorPane pane;
        private inspectTableModel model1;
        private TextPopupMenu pop;
        private TMono mx = null;

        public InspectPanel() {
            super();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            table1 = new JTable((model1 = new inspectTableModel()));
            TableColumn b = table1.getColumnModel().getColumn(0);
            b.setPreferredWidth(100);
            b.setWidth(100);
            b.setMaxWidth(100);
            this.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
            this.add(table1);

            pane = new JEditorPane();
            pop = new TextPopupMenu();

            pane.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        String select = pane.getSelectedText();
                        if (select != null && select.length() > 0)
                            pop.setStatus(true);
                        else pop.setStatus(false);
                        pop.show(pane, e.getX(), e.getY());
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

            pane.setAutoscrolls(false);
            pane.setBorder(BorderFactory.createTitledBorder(getLanguage("Polynomial")));
            this.add(pane);
        }

        public void reduce() {
            if (mx != null) {
                PolyBasic basic = GeoPoly.getInstance();
                TMono m = basic.reduce(basic.p_copy(mx), gxInstance.dp.parameter);
                String s1 = basic.getExpandedPrint(m);
                pane.setText(s1);
            }
        }

        public void loadValue(int n, Object o, Vector v) {
            Object o1 = o;
            Object o2 = v.get(1);
            Object o3 = v.get(2);
            Object o4 = v.get(3);
            Object[] ls = new Object[3];
            ls[0] = o1;
            ls[1] = o2;
            ls[2] = o3;
            model1.setValueAt(o1, 0, 1);
            model1.setValueAt(o2, 1, 1);
            model1.setValueAt(o3, 2, 1);

            CPoint p = (CPoint) o;
            TMono m = null;
            if (n % 2 == 0)
                m = p.x1.m;
            else m = p.y1.m;
            mx = m;

            PolyBasic basic = GeoPoly.getInstance();
//            m = basic.reduce(m, gxInstance.dp.parameter);
            String s1 = basic.getAllPrinted(m);//.getExpandedPrint(m);
            pane.setText(s1);
        }

        class TextPopupMenu extends JPopupMenu {
            JMenuItem m1, m2, m3, m4;

            public TextPopupMenu() {
                m1 = new JMenuItem(getLanguage("Cut"));
                m2 = new JMenuItem(getLanguage("Copy"));
                m3 = new JMenuItem(getLanguage("Paste"));
                m4 = new JMenuItem(getLanguage("Delete"));
                ActionListener ls = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String s = e.getActionCommand();
                        if (s.equals("Cut"))
                            pane.cut();
                        else if (s.equals("Copy"))
                            pane.copy();
                        else if (s.equals("Paste"))
                            pane.paste();
                        else if (s.equals("Delete"))
                            pane.cut();
                    }
                };
                m1.setActionCommand("Cut");
                m2.setActionCommand("Copy");
                m3.setActionCommand("Paste");
                m4.setActionCommand("Delete");
                m1.addActionListener(ls);
                m2.addActionListener(ls);
                m3.addActionListener(ls);
                m4.addActionListener(ls);
                this.add(m1);
                this.add(m2);
                this.add(m3);
                this.add(m4);
            }


            public void setStatus(boolean r) {
                if (r) {
                    m1.setEnabled(true);
                    m2.setEnabled(true);
                } else {
                    m1.setEnabled(false);
                    m2.setEnabled(false);
                }
            }

        }
    }

    /**
     * inspectTableModel is a custom table model for displaying the details of a lead variable.
     * It extends AbstractTableModel and provides methods to manage the data displayed in the table.
     */
    class inspectTableModel extends AbstractTableModel {
        private String[] names = {"", ""};
        private Object[][] data = {
                {getLanguage("Point"), new String()},
                {getLanguage("Variable"), new String()},
                {getLanguage("Length"), new String()},
        };

        public int getColumnCount() {
            return names.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return names[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    /**
     * ndgModel is a custom table model for displaying NDG (Non-Deterministic Graph) data.
     * It extends DefaultTableModel and provides methods to manage the data displayed in the table.
     */
    class ndgModel extends DefaultTableModel {
        private Vector vlist = new Vector();

        public ndgModel() {
        }

        public void setDatalist(Vector v) {
            vlist.addAll(v);
        }

        public void addData(Object o) {
            if (o != null)
                vlist.add(o);
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            if (vlist == null)
                return 0;
            return vlist.size();
        }

        public String getColumnName(int col) {
            return "";
        }

        public Object getValueAt(int row, int col) {
            if (col == 0)
                return row + 1;
            else
                return vlist.get(row);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0)
                return false;
            else return true;
        }

        public void setValueAt(Object value, int row, int col) {
        }
    }
}

