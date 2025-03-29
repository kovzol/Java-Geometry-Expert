package wprover;

import javax.swing.*;
import java.awt.*;

/**
 * JRedoStepFlash is a class that extends JFlash and is used to create a flashing
 * effect on a JPanel when the redo step is performed in the drawing process.
 */
public class JRedoStepFlash extends JFlash {


    DrawProcess dp;

    /**
     * Constructs a new JRedoStepFlash with the specified JPanel and DrawProcess.
     *
     * @param p  the JPanel to associate with this JRedoStepFlash
     * @param dp the DrawProcess to be used for the redo step
     */
    public JRedoStepFlash(JPanel p, DrawProcess dp) {
        super(p);
        this.dp = dp;
        vType = true;
    }

    /**
     * Draws the flashing effect on the specified Graphics2D context.
     *
     * @param g2 the Graphics2D context to draw on
     * @return true if the drawing was successful, false otherwise
     */
    public boolean draw(Graphics2D g2) {
        return true;
    }

    /**
     * Stops the flashing effect and performs the redo step in the drawing process.
     */
    public void stop() {
        if (finished)
            return;

        dp.redo_step(false);
        finished = true;
    }

    /**
     * Starts the flashing effect by stopping it first.
     */
    public void start() {
        stop();
    }

    /**
     * Checks if the flashing effect is currently running.
     *
     * @return false as the flashing effect is not running
     */
    public boolean isrRunning() {
        return false;
    }

    /**
     * Checks if the flashing effect has finished.
     *
     * @return true if the flashing effect has finished, false otherwise
     */
    public boolean isfinished() {
        return finished;
    }
}
