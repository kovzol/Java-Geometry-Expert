package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * JBaseDialog is a custom dialog class that extends JDialog and implements
 * ContainerListener and KeyListener interfaces. It provides functionality to
 * handle key events and manage child components within the dialog.
 */
public class JBaseDialog extends JDialog implements ContainerListener, KeyListener {

    /**
     * Constructs a new JBaseDialog with the specified frame, title, and modality.
     *
     * @param frame the parent frame
     * @param title the title of the dialog
     * @param modal true if the dialog should be modal, false otherwise
     */
    public JBaseDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        addKeyAndContainerListenerRecursively(this);
    }

    /**
     * Constructs a new JBaseDialog with the specified frame and modality.
     *
     * @param frame the parent frame
     * @param modal true if the dialog should be modal, false otherwise
     */
    public JBaseDialog(Frame frame, boolean modal) {
        this(frame, "", modal);
    }

    /**
     * Constructs a new JBaseDialog with no specified frame, title, or modality.
     */
    public JBaseDialog() {
        super();
        addKeyAndContainerListenerRecursively(this);
    }

    /**
     * Constructs a new JBaseDialog with the specified frame and title.
     *
     * @param f     the parent frame
     * @param title the title of the dialog
     */
    public JBaseDialog(Frame f, String title) {
        super(f, title);
        addKeyAndContainerListenerRecursively(this);
    }

    /**
     * Constructs a new JBaseDialog with the specified frame.
     *
     * @param f the parent frame
     */
    public JBaseDialog(Frame f) {
        super(f);
        addKeyAndContainerListenerRecursively(this);
    }

    /**
     * Adds this dialog as a KeyListener and ContainerListener to the specified component
     * and its children recursively.
     *
     * @param c the component to add listeners to
     */
    private void addKeyAndContainerListenerRecursively(Component c) {
        c.removeKeyListener(this);
        c.addKeyListener(this);

        if (c instanceof Container) {
            Container cont = (Container) c;
            cont.removeContainerListener(this);
            cont.addContainerListener(this);

            Component[] children = cont.getComponents();
            for (int i = 0; i < children.length; i++) {
                addKeyAndContainerListenerRecursively(children[i]);
            }
        }
    }

    /**
     * Removes this dialog as a KeyListener and ContainerListener from the specified component
     * and its children recursively.
     *
     * @param c the component to remove listeners from
     */
    private void removeKeyAndContainerListenerRecursively(Component c) {
        c.removeKeyListener(this);

        if (c instanceof Container) {
            Container cont = (Container) c;
            cont.removeContainerListener(this);

            Component[] children = cont.getComponents();
            for (int i = 0; i < children.length; i++) {
                removeKeyAndContainerListenerRecursively(children[i]);
            }
        }
    }

    /**
     * Called when a component is added to a container.
     *
     * @param e the container event
     */
    public void componentAdded(ContainerEvent e) {
        addKeyAndContainerListenerRecursively(e.getChild());
    }

    /**
     * Called when a component is removed from a container.
     *
     * @param e the container event
     */
    public void componentRemoved(ContainerEvent e) {
        removeKeyAndContainerListenerRecursively(e.getChild());
    }

    /**
     * Called when a key is pressed in a component.
     *
     * @param e the key event
     */
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            setVisible(false);
        } else if (code == KeyEvent.VK_ENTER) {
            performEnterAction(e);
        }
    }

    /**
     * Called when a key is released in a component.
     *
     * @param e the key event
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Called when a key is typed in a component.
     *
     * @param e the key event
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Performs the default action when the Enter key is pressed.
     * Subclasses can override this method to provide custom behavior.
     *
     * @param e the key event
     */
    void performEnterAction(KeyEvent e) {
    }

}



