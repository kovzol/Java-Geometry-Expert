package wprover;

/**
 * ListTree.java
 * This class represents a tabbed pane containing two tabs: "Construct History" and "Objects".
 * It displays a list of undo structures and objects, allowing the user to select and view their properties.
 */

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ListTree extends JTabbedPane
        implements ActionListener, MouseListener, ListSelectionListener {

    public GExpert gxInstance;
    public Vector undolist;
    private JList list, listx;
    private DefaultListModel model, modelx;
    private CProperty prop;


    /**
     * Constructs a new ListTree with the specified GExpert instance.
     *
     * @param gx the GExpert instance to associate with this ListTree
     */
    public ListTree(GExpert gx) {
        super(JTabbedPane.BOTTOM);

        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
        gxInstance = gx;
        undolist = new Vector();
        model = new DefaultListModel();
        list = new JList(model);
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane1.add(new JScrollPane(list));

        ListCellRenderer rener = new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                DefaultListCellRenderer d = (DefaultListCellRenderer)
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                d.setText((1 + index) + ". \t" + value.toString());
                return d;
            }
        };
        list.setCellRenderer(rener);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        this.addTab(GExpert.getLanguage("Construct History"), pane1);

        modelx = new DefaultListModel();
        listx = new JList(modelx) {
            public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                double w = dm.getWidth();
                if (w < 100)
                    w = 100;
                dm.setSize(w, dm.getHeight());
                return dm;
            }
        };
        listx.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        ListCellRenderer rener1 = new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                DefaultListCellRenderer d = (DefaultListCellRenderer)
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                CClass c = (CClass) value;

                d.setText(c.getDescription());
                return d;
            }
        };

        prop = new CProperty(gx.d, gx.getLan());
        prop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JSplitPane pane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pane2.setLeftComponent(new JScrollPane(listx));
        listx.setCellRenderer(rener1);
        pane2.setRightComponent(prop);
        listx.addListSelectionListener(this);
        this.addTab(GExpert.getLanguage("Objects"), pane2);
    }

    /**
     * Handles action events for the ListTree.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Handles value change events for the list selections.
     *
     * @param e the list selection event
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list)
            gxInstance.dp.setUndoStructForDisPlay((UndoStruct) (list.getSelectedValue()), true);
        else {
            CClass c = (CClass) listx.getSelectedValue();
            if (c != null) {
                prop.SetPanelType(c);
                gxInstance.dp.setObjectListForFlash(c);
            }
        }
    }

    /**
     * Handles mouse click events for the ListTree.
     *
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 3) {
        } else {
        }
    }

    /**
     * Handles mouse press events for the ListTree.
     *
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Handles mouse release events for the ListTree.
     *
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Handles mouse enter events for the ListTree.
     *
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles mouse exit events for the ListTree.
     *
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Clears all trees in the ListTree.
     */
    public void clearAllTrees() {
    }

    /**
     * Reloads the ListTree with the current undo list and solid objects.
     */
    public void reload() {
        undolist.clear();
        model.removeAllElements();
        modelx.removeAllElements();

        DrawProcess dp = gxInstance.dp;

        Vector v = dp.undolist;
        undolist.addAll(v);
        for (int i = 0; i < undolist.size(); i++)
            model.addElement(undolist.get(i));

        Vector vx = dp.getAllSolidObj();

        for (int i = 0; i < vx.size(); i++) {
            Object o = vx.get(i);
            if (o != null)
                modelx.addElement(o);
        }
    }

}