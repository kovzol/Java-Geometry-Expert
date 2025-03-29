package wprover;

import UI.DropShadowBorder;
import UI.EntityButtonUI;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * RunningDialog is a class that extends JPopupMenu and implements ActionListener.
 * It provides a dialog to show the progress of a running process in a graphical user interface.
 */
public class RunningDialog extends JPopupMenu implements ActionListener {
    public static Timer timer = null;
    private int counter = 0;
    private JLabel label;
    private JLabel labelt;
    private String str;
    private long start_time;
    private PanelGB panegb;

    private static Color color = new Color(206, 223, 242);


    /**
     * Constructs a new RunningDialog with the specified GExpert instance and message string.
     *
     * @param gx the GExpert instance to associate with this dialog
     * @param s  the message string to display in the dialog
     */
    public RunningDialog(GExpert gx, String s) {
        this.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(), BorderFactory.createLineBorder(color, 10)));

        counter = 0;
        str = s;
        label = new JLabel(s);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        labelt = new JLabel() {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                if (d.getWidth() < 20)
                    d.setSize(20, d.getHeight());
                return d;
            }
        };

        JButton b = new JButton(GExpert.createImageIcon("images/other/stop1.gif"));
        b.setUI(new EntityButtonUI());
        panel.add(b);
        b.setToolTipText("Stop the running.");
        b.addActionListener(this);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        panel.add(labelt);
        this.add(panel);
    }

    /**
     * Called when the menu selection changes.
     *
     * @param isIncluded whether the menu is included in the selection
     */
    public void menuSelectionChanged(boolean isIncluded) {
    }

    /**
     * Sets the PanelGB instance associated with this dialog.
     *
     * @param gb the PanelGB instance to set
     */
    public void setPanelGB(PanelGB gb) {
        panegb = gb;
    }

    /**
     * Returns the preferred size of this component.
     *
     * @return the preferred size of this component
     */
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.setSize(300, d.getHeight());
        return d;
    }

    /**
     * Sets the string to be shown in the dialog.
     *
     * @param s the string to set
     */
    public void setShownString(String s) {
        str = s;
    }

    /**
     * Starts the timer count by recording the current time.
     */
    public void startCount() {
        start_time = System.currentTimeMillis();
    }

    /**
     * Starts the timer and displays the RunningDialog.
     *
     * @param gx the GExpert instance to associate with this dialog
     * @param s  the message string to display in the dialog
     * @return the RunningDialog instance if the timer is not already running, null otherwise
     */
    public static RunningDialog startTimer(GExpert gx, String s) {
        if (timer != null && timer.isRunning())
            return null;

        RunningDialog r = new RunningDialog(gx, s);
        r.setShownString(s);

        Dimension dm = r.getPreferredSize();
        Frame f = gx.getFrame();

        int x = (int) (f.getWidth() - dm.getWidth()) / 2;
        int y = (int) (f.getHeight() - dm.getHeight()) / 2;
        r.setLocation(x, y);

        if (timer == null) {
            timer = new Timer(300, r);
            timer.start();
        }

        r.show(gx, x, y);
        r.startCount();

        return r;
    }

    /**
     * Stops the timer and hides the RunningDialog.
     */
    public void stopTimer() {
        if (timer != null)
            timer.stop();
        label.setText("");
        this.setVisible(false);
    }

    /**
     * Handles action events for the timer and stop button.
     *
     * @param e the ActionEvent triggered by the timer or stop button
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            counter++;
            String s = str;

            if (counter > 1) {
                int n = (counter - 1) % 8;
                for (int i = 0; i < n; i++)
                    s += ".";
                label.setText(s);
            }
            long t = System.currentTimeMillis();
            long m = (t - start_time) / 1000;
            labelt.setText(Long.toString(m) + " " + GExpert.getLanguage("seconds"));
        } else {
            timer.stop();
            this.setVisible(false);
            if (panegb != null)
                panegb.stopRunning();
        }
    }
}
