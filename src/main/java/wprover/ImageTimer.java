package wprover;

import UI.GifEncoder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;
/**
 * A dialog for saving a proof as a GIF file in GeoGebra.
 */
public class ImageTimer extends JBaseDialog implements ActionListener, ChangeListener {
    private GExpert gxInstance;
    private JLabel label1, label2;
    private Timer timer;
    private Rectangle rc;
    private CProveBarPanel bar;
    private GifEncoder encorder;
    private int delay, delay1, delay2;
    private int nf = 0;
    private JSlider slider, slider1, slider2;
    private JTextField field, field1, field2;
    private Thread sprocess;
    private boolean result = false;
    private boolean interrupted = false;
    private boolean finished = false;


    BufferedImage[] nimage = new BufferedImage[1000];
    int n1 = 0;
    int n2 = 0;

    /**
     * Returns the result of the image timer process.
     *
     * @return true if the process was successful, false otherwise
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Handles state changes for the sliders and updates the delay values.
     *
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        int n = slider.getValue();
        delay = n;
        delay1 = slider1.getValue();
        delay2 = slider2.getValue();
        updateValue();
    }

    /**
     * Constructs a new ImageTimer dialog with the specified GExpert instance.
     *
     * @param f the GExpert instance
     */
    public ImageTimer(GExpert f) {
        super(f.getFrame(), GExpert.getLanguage("Saving Proof as GIF File"), true);
        gxInstance = f;
        nf = 0;
        n1 = 0;
        n2 = -1;

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
        pp.setBorder(BorderFactory.createEmptyBorder(3, 13, 3, 13));

        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(3, 1));
        p2.setBorder(BorderFactory.createEmptyBorder(3, 13, 3, 13));
        p2.add(new JLabel(GExpert.getLanguage("Default Delay")));
        p2.add(new JLabel(GExpert.getLanguage("Delay at Begin")));
        p2.add(new JLabel(GExpert.getLanguage("Delay at End")));
        pp.add(p2);

        p2 = new JPanel();
        p2.setLayout(new GridLayout(3, 1));
        p2.setBorder(BorderFactory.createEmptyBorder(3, 13, 3, 13));

        slider = new JSlider(0, 1500, 200);
        slider1 = new JSlider(0, 15000, 500);
        slider2 = new JSlider(0, 15000, 500);
        slider.setMinorTickSpacing(50);
        slider1.setMinorTickSpacing(200);
        slider2.setMinorTickSpacing(200);

        slider.addChangeListener(this);
        slider1.addChangeListener(this);
        slider2.addChangeListener(this);
        p2.add(slider);
        p2.add(slider1);
        p2.add(slider2);
        pp.add(p2);

        p2 = new JPanel();
        p2.setLayout(new GridLayout(3, 1));
        p2.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        field = new JTextField("1111111");
        field1 = new JTextField("1111111");
        field2 = new JTextField("1111111");
        p2.add(field);
        p2.add(field1);
        p2.add(field2);
        field.setEditable(false);
        field1.setEditable(false);
        field2.setEditable(false);

        pp.add(p2);
        p.add(pp);


        label1 = new JLabel(GExpert.getTranslationViaGettext("{0} frame(s) added", "0"));
        p.add(label1);


        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.add(Box.createHorizontalGlue());
        JButton bok = new JButton(GExpert.getLanguage("Start"));
        bok.setActionCommand("Start");
        JButton bcancel = new JButton(GExpert.getLanguage("Cancel"));
        bok.addActionListener(this);
        bcancel.addActionListener(this);
        p3.add(bok);
        p3.add(bcancel);
        p.add(p3);


        this.getContentPane().add(p);
        pack();
        f.centerDialog(this);

    }

    /**
     * Sets the delay values for the sliders.
     *
     * @param n the delay value
     */
    public void setDelay(int n) {
        delay = n;
        slider.setValue(n);
        slider1.setValue(n * 2);
        slider2.setValue(n * 3);
    }

    /**
     * Updates the text fields with the current slider values.
     */
    public void updateValue() {
        int n = slider.getValue() / 100;
        int n1 = slider1.getValue() / 100;
        int n2 = slider2.getValue() / 100;
        field.setText(Double.toString(n / 10.00));
        field1.setText(Double.toString(n1 / 10.00));
        field2.setText(Double.toString(n2 / 10.00));
    }

    /**
     * Sets the GifEncoder instance for the image timer.
     *
     * @param e the GifEncoder instance
     */
    public void setEncorder(GifEncoder e) {
        this.encorder = e;
    }

    /**
     * Sets the CProveBarPanel instance for the image timer.
     *
     * @param bar the CProveBarPanel instance
     */
    public void setProveBar(CProveBarPanel bar) {
        this.bar = bar;
    }

    /**
     * Sets the rectangle area for capturing images.
     *
     * @param rc the rectangle area
     */
    public void setRectangle(Rectangle rc) {
        this.rc = rc;
    }

    /**
     * Starts the image timer and the save process.
     */
    public void start() {
        if (timer == null)
            timer = new Timer(delay, this);
        timer.start();
        bar.AStep();
        startSave();
    }

    /**
     * Starts the save process in a new thread.
     */
    public void startSave() {
        if (sprocess == null) {
            sprocess = new Thread(new SaveProcess());
            sprocess.start();
        } else if (!sprocess.isAlive()) {
            if (!running) {
                sprocess = new Thread(new SaveProcess());
                sprocess.start();
            }
        }
    }

    /**
     * Adds a new image to the buffer.
     */
    public void imageAdded() {
        BufferedImage i = gxInstance.getBufferedImage2(rc);
        if (i != null) {
            n2++;
            nimage[n2] = i;
            nf++;
            label1.setText(GExpert.getTranslationViaGettext("{0} frame(s) added", nf + ""));
        }
    }

    /**
     * Handles action events for the timer and buttons.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == timer) {
            if (n1 > n2) {
                imageAdded();
                startSave();
            }
            if (gxInstance.dp.all_flash_finished()) {
                if (bar.isStepAtEnd()) {
                    timer.stop();
                    finished = true;
                    result = true;
                } else
                    bar.AStep();
            }
        } else {
            String s = e.getActionCommand();
            if (s.equalsIgnoreCase("Start")) {
                this.start();
                JButton b = (JButton) e.getSource();
                b.setEnabled(false);
                slider.setEnabled(false);
                slider1.setEnabled(false);
                slider2.setEnabled(false);
            } else {
                if (timer != null) {
                    timer.stop();
                    bar.stop();
                    interrupted = true;
                } else {
                    interrupted = true;
                    this.setVisible(false);
                }
            }
        }
    }

    boolean running = false;

    /**
     * A runnable class for saving the images in a separate thread.
     */
    class SaveProcess implements Runnable {

        public SaveProcess() {
            running = false;
        }

        public void run() {
            running = true;
            if (nimage[n1] != null) {
                while (nimage[n1] != null) {
                    if (n1 == 0)
                        encorder.setDelay(delay1);
                    else if (finished && nimage[n1 + 1] == null)
                        encorder.setDelay(delay2);
                    else encorder.setDelay(delay);

                    BufferedImage a = nimage[n1];
                    encorder.addFrame(a);
                    nimage[n1] = null;
                    n1++;
                }
            }

            if (interrupted) {
                result = false;
                ImageTimer.this.setVisible(false);
            }
            if (finished) {
                result = true;
                ImageTimer.this.setVisible(false);
            }
            running = false;
        }
    }
}