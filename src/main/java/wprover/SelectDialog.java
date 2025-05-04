package wprover;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * SelectDialog is a class that extends JBaseDialog and implements ActionListener,
 * ListSelectionListener, MouseListener, MouseMotionListener, and KeyListener interfaces.
 * It provides a dialog for selecting items from a list.
 */
public class SelectDialog extends JBaseDialog implements
        ActionListener, ListSelectionListener, MouseListener, MouseMotionListener, KeyListener {

    private int oldx;
    private int oldy;


    private JList list;
    private DefaultListModel listModel;
    private static final String str = "Cancel";
    private JButton cancle_button;
    private Vector selectedlist;
    Object selected = null;
    GExpert gxInstance;

    private static Font listFont = new Font("Arial", Font.PLAIN, 12);

    /**
     * Constructs a new SelectDialog with the specified GExpert instance and list of items.
     *
     * @param owner the GExpert instance to associate with this dialog
     * @param vlist the list of items to display in the dialog
     */
    public SelectDialog(GExpert owner, Vector vlist) {
        super(owner.getFrame(), "Select");
        gxInstance = owner;

        this.setModal(true);
        listModel = new DefaultListModel();
        list = new JList(listModel);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.addMouseListener(this);
        list.addMouseMotionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);
        list.setFont(listFont);
        //        list.addKeyListener(this);
        //        this.addKeyListener(this);

        cancle_button = new JButton(str);
        cancle_button.addActionListener(this);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(listScrollPane, BorderLayout.CENTER);
        panel.add(cancle_button, BorderLayout.PAGE_END);
        getContentPane().add(panel);

        this.setSize(new Dimension(130, 150));
        selectedlist = new Vector();
        addItem(vlist);
    }

    /**
     * Handles the mouse dragged event.
     *
     * @param e the MouseEvent triggered by dragging the mouse
     */
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Handles the mouse moved event.
     *
     * @param e the MouseEvent triggered by moving the mouse
     */
    public void mouseMoved(MouseEvent e) {
        Rectangle rc = list.getCellBounds(0, 0);
        if (rc == null)
            return;

        double r = rc.getHeight();
        double r1 = e.getY();
        int n = (int) (r1 / r);
        if (n < 0)
            n = 0;
        else if (n >= listModel.getSize())
            n = listModel.getSize() - 1;

        list.setSelectedIndex(n);
    }

    /**
     * Adds items to the list model and selected list.
     *
     * @param v the vector of items to add
     */
    public void addItem(Vector v) {
        listModel.clear();
        selectedlist.clear();

        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            listModel.addElement(cc.getDescription());
            selectedlist.add(cc);
        }
    }

    /**
     * Handles the list selection event.
     *
     * @param e the ListSelectionEvent triggered by selecting an item in the list
     */
    public void valueChanged(ListSelectionEvent e) {
        int n = list.getSelectedIndex();
        int len = selectedlist.size();
        if (n >= 0 && n < len)
            gxInstance.dp.setObjectListForFlash((CClass) selectedlist.get(n));
    }

    /**
     * Handles the action event for the cancel button.
     *
     * @param e the ActionEvent triggered by clicking the cancel button
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancle_button) {
            selectedlist.clear();
            listModel.clear();
            this.setVisible(false);
        }
    }

    /**
     * Returns the selected item.
     *
     * @return the selected item
     */
    public Object getSelected() {
        return selected;
    }

    /**
     * Handles the mouse clicked event.
     *
     * @param e the MouseEvent triggered by clicking the mouse
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Displays the dialog at the specified location.
     *
     * @param x the x-coordinate of the location
     * @param y the y-coordinate of the location
     */
    public void popSelect(int x, int y) {
        oldx = x;
        oldy = y;
        this.setLocation(oldx, oldy);
        this.setFocusable(true);
        this.setVisible(true);
    }

    /**
     * Handles the mouse pressed event.
     *
     * @param e the MouseEvent triggered by pressing the mouse
     */
    public void mousePressed(MouseEvent e) {
        int index = list.getSelectedIndex();
        if (index < 0 || index >= selectedlist.size())
            return;
        selected = selectedlist.get(index);
        this.setVisible(false);
    }

    /**
     * Handles the mouse released event.
     *
     * @param e the MouseEvent triggered by releasing the mouse
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Handles the mouse entered event.
     *
     * @param e the MouseEvent triggered by entering the mouse
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Handles the mouse exited event.
     *
     * @param e the MouseEvent triggered by exiting the mouse
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Handles the key typed event.
     *
     * @param e the KeyEvent triggered by typing a key
     */
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            this.setVisible(false);
    }

    /**
     * Handles the key pressed event.
     *
     * @param e the KeyEvent triggered by pressing a key
     */
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
    }
}
