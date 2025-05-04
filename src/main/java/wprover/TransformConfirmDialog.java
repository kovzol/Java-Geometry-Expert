package wprover;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * TransformConfirmDialog.java
 * This class represents a confirmation dialog for transformations in the GExpert application.
 * It extends JBaseDialog and implements ActionListener to handle button actions.
 */
public class TransformConfirmDialog extends JBaseDialog implements ActionListener {

    int result = -1;

    /**
     * Constructs a new TransformConfirmDialog with the specified frame and messages.
     *
     * @param f  the parent frame for the dialog
     * @param s1 the first message to display in the dialog
     * @param s2 the second message to display in the dialog
     */
    public TransformConfirmDialog(Frame f, String s1, String s2) {
        super(f, "Confirm", true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label1 = new JLabel(s1);
        label1.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 16));
        panel.add(label1);
        JLabel label2 = new JLabel(s2);
        label2.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 16));
        panel.add(label2);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        JButton b1 = new JButton(GExpert.getLanguage("Yes"));
        JButton b2 = new JButton(GExpert.getLanguage("No"));
        JButton b3 = new JButton(GExpert.getLanguage("Cancel"));
        p.add(b1);
        p.add(Box.createHorizontalStrut(5));
        p.add(b2);
        p.add(Box.createHorizontalStrut(5));
        p.add(b3);
        p.add(Box.createHorizontalStrut(5));
        panel.add(p);
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);

        AbstractAction aaa = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if (s.equalsIgnoreCase("Y"))
                    result = 0;
                else if (s.equalsIgnoreCase("N"))
                    result = 1;
                else
                    result = 2;

                TransformConfirmDialog.this.setVisible(false);
            }
        };

        b1.getInputMap().put(KeyStroke.getKeyStroke("Y"), "action");
        b1.getActionMap().put("action", aaa);
        b2.getInputMap().put(KeyStroke.getKeyStroke("N"), "action1");
        b2.getActionMap().put("action1", aaa);
        b3.getInputMap().put(KeyStroke.getKeyStroke("C"), "action2");
        b3.getActionMap().put("action2", aaa);

        this.getContentPane().add(panel);
        pack();
    }

    /**
     * Handles action events for the buttons in the dialog.
     *
     * @param e the ActionEvent triggered by clicking a button
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equalsIgnoreCase("Yes"))
            result = 0;
        else if (s.equalsIgnoreCase("No"))
            result = 1;
        else
            result = 2;
        this.setVisible(false);
    }

    /**
     * Returns the result of the dialog.
     *
     * @return the result of the dialog (0 for Yes, 1 for No, 2 for Cancel)
     */
    public int getResult() {
        return result;
    }

    /**
     * Handles key pressed events for the dialog.
     *
     * @param e the KeyEvent triggered by pressing a key
     */
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_Y) {
            result = 0;
            this.setVisible(false);
        } else if (code == KeyEvent.VK_N) {
            result = 1;
            this.setVisible(false);
        } else if (code == KeyEvent.VK_C) {
            result = 2;
            this.setVisible(false);
        }
        super.keyPressed(e);
    }
}
