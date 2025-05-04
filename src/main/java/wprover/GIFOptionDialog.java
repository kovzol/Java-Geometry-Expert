package wprover;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
//import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A dialog for setting GIF options in GeoGebra.
 */
public class GIFOptionDialog extends JBaseDialog implements ActionListener, ChangeListener {

    private JSlider slider;
    private JTextField field1, field2;
    private JButton bok, bcancel;
    private boolean result = false;
    private GExpert gxInstance;

    /**
     * Constructs a new GIFOptionDialog with the specified GExpert instance and title.
     *
     * @param fr    the GExpert instance
     * @param title the title of the dialog
     */
    public GIFOptionDialog(GExpert fr, String title) {
        super(fr.getFrame(), title, true);
        gxInstance = fr;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.add(new JLabel(fr.getLanguage("Image Quality")));
        field1 = new JTextField();
        field2 = new JTextField();
        field1.setEditable(false);
        field2.setEditable(false);
        panel1.add(field1);
        panel1.add(field2);
        panel.add(panel1);
        panel.add(Box.createVerticalStrut(5));
        slider = new JSlider(1, 20, 1);
        slider.setValue(2);
        slider.setPaintTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(4);
        slider.setMinimum(1);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        panel.add(slider);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.add(Box.createHorizontalGlue());
        panel2.add(bok = new JButton(fr.getLanguage("OK")));
        panel2.add(bcancel = new JButton(fr.getLanguage("Cancel")));
        bok.addActionListener(this);
        bcancel.addActionListener(this);
        panel.add(panel2);
        updateValue();
        this.getContentPane().add(panel);
        this.setSize(300, 150);
    }

    /**
     * Sets the default value of the slider.
     *
     * @param n the default value
     */
    public void setDefaultValue(int n) {
        slider.setValue(n);
        this.updateValue();
    }

    /**
     * Returns the result of the dialog.
     *
     * @return true if OK was pressed, false otherwise
     */
    public boolean getReturnResult() {
        return result;
    }

    /**
     * Returns the quality value based on the slider position.
     *
     * @return the quality value
     */
    public int getQuality() {
        return 21 - slider.getValue();
    }

    /**
     * Handles action events for the OK and Cancel buttons.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bok)
            result = true;
        else result = false;
        this.setVisible(false);
    }

    /**
     * Handles state change events for the slider.
     *
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        updateValue();
    }

    /**
     * Updates the text fields based on the slider value.
     */
    private void updateValue() {
        int v = slider.getValue();
        field1.setText(Integer.toString(v));
        field2.setText(getRate(v));
    }

    /**
     * Returns the quality rate as a string based on the slider value.
     *
     * @param n the slider value
     * @return the quality rate
     */
    private String getRate(int n) {
        if (n > 18)
            return gxInstance.getLanguage("Best");
        if (n > 15)
            return gxInstance.getLanguage("Good");
        if (n > 10)
            return gxInstance.getLanguage("Medium");
        else if (n > 5)
            return gxInstance.getLanguage("Low");
        else return gxInstance.getLanguage("Very Low");
    }
}