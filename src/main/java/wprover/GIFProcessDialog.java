package wprover;

import UI.GifEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A dialog for processing GIF creation in GeoGebra.
 */
public class GIFProcessDialog extends JBaseDialog implements ActionListener {

    JProgressBar progress;
    int total;
    JLabel label;
    public DataOutputStream out;
    public GExpert gxInstance;
    public AnimateC am;
    public DrawTextProcess dp;
    public Rectangle rect;
    public GifEncoder en;

    /**
     * Constructs a new GIFProcessDialog with the specified frame.
     *
     * @param f the frame to associate with this dialog
     */
    public GIFProcessDialog(Frame f) {
        super(f, false);
        this.setTitle(GExpert.getLanguage("Building GIF File"));
        progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progress.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        progress.setBorderPainted(true);
        progress.setStringPainted(true);
        label = new JLabel(GExpert.getLanguage("0 frame(s) added"));
        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout());
        pp.add(Box.createHorizontalStrut(150));
        pp.add(label);

        JPanel panel = new JPanel();
        panel.add(Box.createVerticalStrut(10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(progress);
        panel.add(pp);
        panel.add(Box.createVerticalStrut(10));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.add(Box.createHorizontalGlue());
        JButton b1 = new JButton("Cancel");
        panel1.add(b1);
        b1.addActionListener(this);
        panel.add(panel1);
        panel.add(Box.createVerticalStrut(10));
        panel.setBorder((BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        this.getContentPane().add(panel);
        this.pack();
    }

    boolean finished = false;

    /**
     * Starts the GIF creation process in a new thread.
     */
    public void setRun() {
        Saver sv = new Saver();
        Thread t = new Thread(sv, "Progress");
        finished = false;
        t.start();
    }

    /**
     * A runnable class for saving the GIF frames.
     */
    class Saver implements Runnable {

        public void run() {
            double[] r = dp.getParameter();

            am.minwd = rect.getX() + 5;
            am.minht = rect.getY() + 5;
            am.width = rect.getX() + rect.getWidth() - 5;
            am.height = rect.getY() + rect.getHeight() - 5;

            am.reClaclulate();
            total = am.getRounds();

            GIFProcessDialog.this.setVisible(true);
            dp.setCalMode1();
            try {
                int n = total;
                while (n >= 0) {
                    am.onTimer();
                    if (!dp.reCalculate()) {
                        am.resetXY();
                    }
                    GIFProcessDialog.this.setValue(total - n + 1);
                    en.addFrame(gxInstance.getBufferedImage(rect));
                    n--;
                    if (finished)
                        break;
                }
                en.finish();
                out.close();

            } catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
            GIFProcessDialog.this.setVisible(false);
            dp.setCalMode0();
            dp.setParameter(r);
        }
    }

    /**
     * Sets the total number of frames to be processed.
     *
     * @param n the total number of frames
     */
    public void setTotal(int n) {
        this.total = n;
    }

    /**
     * Updates the progress bar and label with the current frame count.
     *
     * @param n the current frame count
     */
    public void setValue(int n) {
        progress.setValue(n * 100 / total);
        label.setText(n + " frame(s) added");
    }

    /**
     * Handles action events for the Cancel button.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        finished = true;
        this.setVisible(false);
    }
}
