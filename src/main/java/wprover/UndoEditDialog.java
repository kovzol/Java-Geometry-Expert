package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

/**
 * UndoEditDialog.java
 * This class represents a dialog for displaying the undo edit history in a tree structure.
 * It extends JBaseDialog and implements WindowListener to handle window events.
 */
public class UndoEditDialog extends JBaseDialog implements WindowListener {
    private ListTree treepanel;

    /**
     * Constructs a new UndoEditDialog with the specified GExpert owner.
     * Sets the title, initializes the tree panel, sets the content pane,
     * sets the size, centers the dialog, and adds a window listener.
     *
     * @param owner the GExpert instance that owns this dialog
     */
    public UndoEditDialog(GExpert owner) {
        super(owner.getFrame());
        this.setTitle(owner.getLanguage("Construct History"));
        treepanel = new ListTree(owner);
        this.setContentPane(treepanel);
        this.setSize(new Dimension(430, 600));
        owner.centerDialog(this);
        this.addWindowListener(this);
    }


    ListTree getTreePanel() {
        return treepanel;
    }

    /**
     * Displays the dialog and reloads the tree panel.
     */
    public void showDialog() {
        this.setVisible(true);
        treepanel.reload();
    }

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {

    }

    public void windowClosed(WindowEvent e) {
    }


    public void windowIconified(WindowEvent e) {

    }


    public void windowDeiconified(WindowEvent e) {

    }


    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}

