package wprover;

import gprover.Prover;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static wprover.GExpert.commandlineCommand;

/**
 * The GProver class implements the Runnable interface and is responsible for
 * managing the proving process in GeoGebra.
 */
public class GProver implements Runnable {

    GExpert gxInstance;
    PanelProve pprove;
    Thread main;
    private int Status = 0;
    private boolean isRunning = false;

    Timer timer = null;
    int number = 0;
    long ftime = 0;

    /**
     * Constructs a new GProver with the specified PanelProve and GExpert instances.
     *
     * @param p  the PanelProve instance
     * @param fr the GExpert instance
     */
    public GProver(PanelProve p, GExpert fr) {
        pprove = p;
        gxInstance = fr;
    }

    /**
     * Sets the status to fix mode.
     */
    public void setFix() {
        Status = 0;
    }

    /**
     * Sets the status to prove mode.
     */
    public void setProve() {
        Status = 1;
    }

    /**
     * Starts the timer for the proving process.
     */
    public void startTimer() {
        number = 0;
        ftime = System.currentTimeMillis();

        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isRunning)
                    timer.stop();
                else {
                    double t = System.currentTimeMillis() - ftime;
                    int ft = (int) (t / 1000);
                    gxInstance.setLabelText2("Building fixpoint (" + ft + " seconds"
                            + ";  " + Prover.getNumberofProperties() + " facts)");
                }
            }
        });
        timer.start();
    }

    /**
     * Runs the proving process in a separate thread.
     */
    public void run() {
        isRunning = true;
        try {
            if (Status == 0) {
                long n1 = System.currentTimeMillis();
                Prover.run();
                n1 = System.currentTimeMillis() - n1;
                pprove.displayDatabase(n1);
            } else {
                boolean t = Prover.prove();
                pprove.displayGDDProve(t);
            }
        } catch (OutOfMemoryError ee) {
            if (CMisc.DEBUG)
                ee.printStackTrace();
            CMisc.print(ee.getMessage());
            if (gxInstance != null)
                gxInstance.setTextLabel2("System run out of memory", -1);
            Prover.reset();
            isRunning = false;
            JOptionPane.showMessageDialog(gxInstance,
                    "System run out of memory!\nThe theorem is not proved.",
                    "Not Proved", JOptionPane.WARNING_MESSAGE);

        } catch (Error ee) {
            JOptionPane.showMessageDialog(gxInstance,
                    "The theorem is not proved.\n" + ee.getMessage(),
                    "Not Proved", JOptionPane.WARNING_MESSAGE);
            Prover.reset();

        } catch (Exception ee) {
            JOptionPane.showMessageDialog(gxInstance,
                    "The theorem is not proved.\n" + ee.getMessage(),
                    "Not Proved", JOptionPane.WARNING_MESSAGE);
            Prover.reset();
        }
        if (gxInstance != null)
            gxInstance.stopTimer();

        isRunning = false;
        GExpert.performCommandLineRequests(gxInstance, false);
    }

    /**
     * Starts the proving process in a new thread.
     */
    public void start() {
        if (isRunning) return;
        main = new Thread(this, "Prover");
        main.start();
        startTimer();
    }

    /**
     * Checks if the proving process is currently running.
     *
     * @return true if the proving process is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }
}

