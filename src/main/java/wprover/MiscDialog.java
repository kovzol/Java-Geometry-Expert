package wprover;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * MiscDialog is a class that extends JBaseDialog and implements FocusListener and ActionListener.
 * It provides a dialog for setting various preferences in the GExpert application.
 */
public class MiscDialog extends JBaseDialog implements FocusListener, ActionListener {
    private GExpert gxInstance;
    private String lan;
    private JTabbedPane tpane;

    private DisplayPanel pane1;
    private modePanel pane2;
    private colorPanel panelc;
    private FontPanel pane3;
    private AnglePanel pane4;
    private boolean onSetting = false;


    /**
     * Constructs a new MiscDialog with the specified GExpert instance.
     *
     * @param gx the GExpert instance to associate with this MiscDialog
     */
    public MiscDialog(GExpert gx) {
        super(gx.getFrame(), ("Preferences"), false);
        gxInstance = gx;
        lan = CMisc.lan;

        String s = GExpert.getLanguage("Preferences");
        this.setTitle(s);

        gxInstance = gx;

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
        pane.addTab(GExpert.getLanguage("Display"), pane1 = createPanelDisply());
        pane.addTab(GExpert.getLanguage("Mode"), pane2 = new modePanel());
        pane.addTab(GExpert.getLanguage("Color"), panelc = new colorPanel());
        pane.addTab(GExpert.getLanguage("Font"), pane3 = new FontPanel());
        pane.addTab(GExpert.getLanguage("Other"), pane4 = new AnglePanel());
        tpane = pane;

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pane);

        JPanel p2 = new JPanel(new FlowLayout());
        JButton b1 = new JButton("Save Preferences");
        b1.setText(GExpert.getLanguage("Save Preferences"));

        JButton b3 = new JButton("Default");
        b3.setText(GExpert.getLanguage("Default"));
        b3.setActionCommand("Default");
        b3.addActionListener(this);

        b1.setActionCommand("Save Preferences");

        JButton b2 = new JButton(GExpert.getLanguage("OK"));
        b2.setText(GExpert.getLanguage("OK"));
        b2.setActionCommand("OK");
        b1.addActionListener(this);
        b2.addActionListener(this);
        p2.add(Box.createHorizontalGlue());
        p2.add(b1);
        p2.add(b3);
        p2.add(Box.createHorizontalGlue());
        p2.add(b2);
        panel.add(p2);

        this.addFocusListener(this);
        this.getContentPane().add(panel);
        this.setSize(550, 600); // Changed the size of the window, so 'Save Preferences' was visible. TODO: Modify the height automatically to the chosen appearance.
    }

    /**
     * Sets the selected tab in the tabbed pane.
     *
     * @param n the index of the tab to select
     */
    public void setSelectedTabbedPane(int n) {
        tpane.setSelectedIndex(n);
    }

    /**
     * Initializes the panels in the dialog.
     */
    public void init() {
        pane1.init();
        pane2.init();
        pane3.init();
        pane4.init();
    }

    /**
     * Handles action events for the dialog.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("OK")) {
            this.setVisible(false);
        } else if (command.equals("Default")) {
            onSetting = true;
            CMisc.Reset();
            init();
            onSetting = false;
        } else if (command.equals("Save Preferences")) {
            String s1 = GExpert.getUserHome();
            String s2 = GExpert.getFileSeparator();

            try {
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(new File(s1 + s2 + "jgex.cfg")), "UTF-8");
                CMisc.SaveProperty(writer);
            } catch (IOException ee) {
                JOptionPane.showMessageDialog(gxInstance, GExpert.getLanguage("Can not save Preferences"),
                        GExpert.getLanguage("Fail"), JOptionPane.WARNING_MESSAGE);
            }
            JOptionPane.showMessageDialog(gxInstance, GExpert.getLanguage("Preferences have been successfully saved.") + "\n" +
                            GExpert.getLanguage("Please restart the program."),
                    GExpert.getLanguage("Saved"), JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Creates and returns a new DisplayPanel.
     *
     * @return a new DisplayPanel
     */
    private DisplayPanel createPanelDisply() {
        return new DisplayPanel();
    }


    /**
     * DisplayPanel is a JPanel that provides various display settings for the GExpert application.
     * It includes sliders, checkboxes, and radio buttons for configuring the display options.
     */
    class DisplayPanel extends JPanel {

        private JLabel text;
        private JRadioButton b1, b2, b3;
        private JSlider slider, slider1;
        private JCheckBox bts, bft;
        private JSpinner spinner;

        public DisplayPanel() {
            this.setLayout(new GridLayout(5, 1));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Filling of Polygons")));
            float f = CMisc.getFillCompositeAlpha();
            int n = (int) (f * 100);

            slider = new JSlider(0, 100);
            slider.setValue(n);
            slider.setPaintTicks(true);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(20);
            slider.setPaintTrack(true);
            slider.setPaintLabels(true);

            p1.add(slider);
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    JSlider slider = (JSlider) e.getSource();
                    int n = slider.getValue();
                    float f = (n) / 100.0f;
                    CMisc.setFillCompositeAlpha(f);
                    text.setText(n + "");
                    gxInstance.d.repaint();
                }
            });

            p1.add((text = new JLabel()));
            //   p1.add(Box.createHorizontalGlue());
            text.setText(n + "");

            p1.add(Box.createHorizontalGlue());
            this.add(p1);

            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            slider1 = new JSlider(0, 20);
            slider1.setValue(CMisc.getPointRadius());
            slider1.setPaintTicks(true);
            slider1.setMinorTickSpacing(1);
            slider1.setMajorTickSpacing(5);
            slider1.setPaintTrack(true);
            slider1.setPaintLabels(true);

            p2.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Point Size")));
            p2.add(slider1);
            slider1.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    JSlider slider = (JSlider) e.getSource();

                    int n = slider.getValue();
                    CMisc.setPointRadius(n);
                    gxInstance.d.repaint();
                }
            });
            p2.add(Box.createHorizontalGlue());
            this.add(p2);

            JPanel p3 = new JPanel();
            p3.setLayout(new FlowLayout(FlowLayout.LEADING));
            p3.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Point's Text")));
            JButton button = new JButton(GExpert.getLanguage("Default Font"));
            button.setText(CMisc.nameFont.getName() + " " + CMisc.nameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.nameFont,
                                    GExpert.getLanguage("Choose default font for point's text"), Color.black)) {
                        CMisc.nameFont = VFontChooser.getReturnFont();
                        JButton b = (JButton) e.getSource();
                        b.setText(CMisc.nameFont.getName() + " " + CMisc.nameFont.getSize());
                    }
                }

            });

            JCheckBox b = bts = new JCheckBox(GExpert.getLanguage("Show Text"));
            b.setSelected(CMisc.nameTextShown);
            b.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    CMisc.nameTextShown = ((JCheckBox) e.getSource()).isSelected();
                    gxInstance.d.repaint();
                }


            });

            p3.add(b);
            p3.add(Box.createHorizontalStrut(10));
            p3.add(button);
            p3.add(Box.createHorizontalStrut(10));
            this.add(p3);


            JPanel p5 = new JPanel();
            p5.setLayout(new FlowLayout(FlowLayout.LEADING));
            p5.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Angle Text")));
            ButtonGroup bg = new ButtonGroup();
            ItemListener listener = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    Object obj = e.getSource();
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (obj == b1) {
                            CMisc.show_angle_type = 0;
                        } else if (obj == b2) {
                            CMisc.show_angle_type = 1;
                        } else if (obj == b3) {
                            CMisc.show_angle_type = 2;
                        }
                    }
                }
            };
            b1 = new JRadioButton(GExpert.getLanguage("None"));
            b1.addItemListener(listener);
            bg.add(b1);
            p5.add(b1);
            p5.add((b2 = new JRadioButton(GExpert.getLanguage("Label"))));
            bg.add(b2);
            b2.addItemListener(listener);
            p5.add((b3 = new JRadioButton(GExpert.getLanguage("Degrees"))));
            bg.add(b3);
            b3.addItemListener(listener);
            switch (CMisc.show_angle_type) {
                case 0:
                    b1.setSelected(true);
                    break;
                case 1:
                    b2.setSelected(true);
                    break;
                case 2:
                    b3.setSelected(true);
                    break;
            }
            button = new JButton(GExpert.getLanguage("Font"));
            button.setText(CMisc.angleNameFont.getName() + " " + CMisc.angleNameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.angleNameFont, Color.black)) {
                        CMisc.angleNameFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.angleNameFont.getName() + " " + CMisc.angleNameFont.getSize());
                    }
                }

            });
            p5.add(Box.createHorizontalStrut(10));
            p5.add(button);
            this.add(p5);

            JPanel p6 = new JPanel();
            p6.setLayout(new FlowLayout(FlowLayout.LEADING));
            p6.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Foot Mark")));
            JCheckBox bx = bft = new JCheckBox(GExpert.getLanguage("Show foot mark"));
            bx.setSelected(CMisc.footMarkShown);

            bx.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    CMisc.footMarkShown = ((JCheckBox) e.getSource()).isSelected();
                    gxInstance.d.repaint();
                }
            });
            p6.add(bx);
            p6.add(Box.createHorizontalStrut(10));
            p6.add(new JLabel(GExpert.getLanguage("Length") + ":  "));
            JSpinner spin = spinner = new JSpinner();
            spin.setValue(CMisc.FOOT_MARK_LENGTH);
            spin.addChangeListener(new ChangeListener() {


                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;
                    Object obj = ((JSpinner) e.getSource()).getValue();
                    int n = Integer.parseInt(obj.toString());
                    CMisc.FOOT_MARK_LENGTH = n;
                    gxInstance.d.repaint();
                }
            });

            p6.add(spin);

            this.add(p6);
        }

        public void init() {
            float f = CMisc.getFillCompositeAlpha();
            int n = 100 - (int) (f * 100);


            bts.setSelected(CMisc.nameTextShown);

            switch (CMisc.show_angle_type) {
                case 0:
                    b1.setSelected(true);
                    break;
                case 1:
                    b2.setSelected(true);
                    break;
                case 2:
                    b3.setSelected(true);
                    break;
            }
            bft.setSelected(CMisc.footMarkShown);
            spinner.setValue(CMisc.FOOT_MARK_LENGTH);


            slider.setValue(n);
            slider1.setValue(CMisc.getPointRadius());
        }

    }

    /**
     * AnglePanel is a JPanel that provides options for configuring angles in the GExpert application.
     * It includes radio buttons for different angle types and a slider for adjusting the polygon moving interval.
     */
    class AnglePanel extends JPanel implements ItemListener {
        JRadioButton ba, bwa, bma, bfill;
        JSlider slider;


        public AnglePanel() {
            this.setLayout(new GridLayout(5, 1));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Angle")));

            ButtonGroup bg = new ButtonGroup();
            ba = new JRadioButton(GExpert.getLanguage("Without Arrow"));
            bg.add(ba);
            bwa = new JRadioButton(GExpert.getLanguage("With Arrow"));
            bg.add(bwa);
            bma = new JRadioButton(GExpert.getLanguage("Multiple Arc"));
            bg.add(bma);
            bfill = new JRadioButton(GExpert.getLanguage("Fill"));


            ba.addItemListener(this);
            bwa.addItemListener(this);
            bma.addItemListener(this);
            bfill.addItemListener(this);
            bg.add(bfill);
            p1.add(ba);
            p1.add(bwa);
            p1.add(bma);
            p1.add(bfill);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Polygon Moving Interval")));
            p1.setLayout(new FlowLayout(FlowLayout.LEFT));

            int d = CMisc.getMoveStep();
            slider = new JSlider(0, 20);
            slider.setValue(d);
            slider.setPaintTicks(true);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(4);
            slider.setMinimum(2);
            slider.setPaintTrack(true);
            slider.setPaintLabels(true);

            p1.add(slider);
            p1.add(Box.createHorizontalGlue());
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (onSetting) return;

                    JSlider slider = (JSlider) e.getSource();
                    int n = slider.getValue();
                    CMisc.setMoveStep(n);
                    gxInstance.dp.recal_allFlash();
                }
            });
            this.add(p1);
            init();
        }

        public void itemStateChanged(ItemEvent e) {
            if (onSetting) return;
            Object obj = e.getSource();
            if (obj == ba || obj == bwa || obj == bma || obj == bfill) {
                if (ba.isSelected())
                    CMisc.ANGLE_TYPE = 0;
                else if (bwa.isSelected())
                    CMisc.ANGLE_TYPE = 1;
                else if (bma.isSelected())
                    CMisc.ANGLE_TYPE = 2;
                else if (bfill.isSelected())
                    CMisc.ANGLE_TYPE = 3;
            }
        }

        public void init() {
            ba.setSelected(CMisc.ANGLE_TYPE == 0);
            bwa.setSelected(CMisc.ANGLE_TYPE == 1);
            bma.setSelected(CMisc.ANGLE_TYPE == 2);
            bfill.setSelected(CMisc.ANGLE_TYPE == 3);
            int d = CMisc.getMoveStep();
            slider.setValue(d);
        }
    }


    /**
     * Invoked when the component gains the keyboard focus.
     *
     * @param e the focus event
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * Invoked when the component loses the keyboard focus.
     *
     * @param e the focus event
     */
    public void focusLost(FocusEvent e) {
        this.setVisible(false);
    }

    /**
     * This class represents a panel for selecting fonts in the GExpert application.
     * It extends JPanel and provides buttons to choose different fonts for various elements.
     */
    class FontPanel extends JPanel {


        public FontPanel() {
            this.setLayout(new GridLayout(3, 2));

            JPanel p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Point's Text")));
            JButton button = new JButton("PTEXT"); // FIXME
            button.setText(CMisc.nameFont.getName() + " " + CMisc.nameFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.nameFont, Color.black)) {
                        CMisc.nameFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.nameFont.getName() + " " + CMisc.nameFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);


            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("THM") + " - " +
                    GExpert.getLanguage("Theorem")));
            button = new JButton(GExpert.getLanguage("THM"));
            button.setText(CMisc.thmFont.getName() + " " + CMisc.thmFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.thmFont, Color.black)) {
                        CMisc.thmFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.thmFont.getName() + " " + CMisc.thmFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("F-D")));// + GExpert.getLanguage(3002, "Full Angle Method")
//                    + "-" + GExpert.getLanguage(3001, "Deductive Datab?ase Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton(GExpert.getLanguage("Full"));
            button.setText(CMisc.fullFont.getName() + " " + CMisc.fullFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.fullFont, Color.black)) {
                        CMisc.fullFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.fullFont.getName() + " " + CMisc.fullFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Area") + " - " +
                    GExpert.getLanguage("Area Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton(GExpert.getLanguage("Area"));
            button.setText(CMisc.areaFont.getName() + " " + CMisc.areaFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.areaFont, Color.black)) {
                        CMisc.areaFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.areaFont.getName() + " " + CMisc.areaFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Manual") + " - " +
                    GExpert.getLanguage("Manual Method")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton(GExpert.getLanguage("Manual"));
            button.setText(CMisc.manualFont.getName() + " " + CMisc.manualFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.manualFont, Color.black)) {
                        CMisc.manualFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.manualFont.getName() + " " + CMisc.manualFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Fix") + " - " +
                    GExpert.getLanguage("Fixpoint")));
            // p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton(GExpert.getLanguage("Fixpoint"));
            button.setText(CMisc.fixFont.getName() + " " + CMisc.fixFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.fixFont, Color.black)) {
                        CMisc.fixFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.fixFont.getName() + " " + CMisc.fixFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);

            p1 = new JPanel();
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Algebra") + " - " +
                    GExpert.getLanguage("Algebra")));
            //
            //  p1.setLayout(new FlowLayout(FlowLayout.LEFT));
            button = new JButton(GExpert.getLanguage("Algebra"));
            button.setText(CMisc.algebraFont.getName() + " " + CMisc.algebraFont.getSize());
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (JOptionPane.OK_OPTION ==
                            VFontChooser.showDialog(gxInstance, CMisc.algebraFont, Color.black)) {
                        CMisc.algebraFont = VFontChooser.getReturnFont();
                        JButton button = (JButton) e.getSource();
                        button.setText(CMisc.algebraFont.getName() + " " + CMisc.algebraFont.getSize());
                    }
                }
            });
            p1.add(button);
            this.add(p1);


        }


        public void init() {

        }

    }

    /**
     * colorPanel is a JPanel that allows the user to select colors for the background and grid.
     * It includes radio buttons for different color modes and color selection panels.
     */
    class colorPanel extends JPanel implements ItemListener, MouseListener {

        private JRadioButton b1, b2, b3;
        private ColorPane pbk, pgrid;

        public colorPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Color Mode")));

            ButtonGroup bg = new ButtonGroup();
            b1 = new JRadioButton(GExpert.getLanguage("Colorful"));
            b2 = new JRadioButton(GExpert.getLanguage("Gray"));
            b3 = new JRadioButton(GExpert.getLanguage("Black and White"));
            {
                int n = CMisc.ColorMode;
                if (n == 0)
                    b1.setSelected(true);
                else if (n == 1)
                    b2.setSelected(true);
                else b3.setSelected(true);
            }


            bg.add(b1);
            bg.add(b2);
            bg.add(b3);
            p1.add(b1);
            p1.add(b2);
            p1.add(b3);
            b1.addItemListener(this);
            b2.addItemListener(this);
            b3.addItemListener(this);
            p1.add(Box.createHorizontalGlue());


            JPanel p3 = new JPanel(new GridLayout(1, 2));
            p3.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Color")));

            pbk = new ColorPane(100, 30);
            pbk.addMouseListener(this);
            JPanel p31 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p31.add(new JLabel(GExpert.getLanguage("Background color")));
            p31.add(pbk);

            pgrid = new ColorPane(100, 30);
            pgrid.addMouseListener(this);
            JPanel p32 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p32.add(new JLabel(GExpert.getLanguage("Grid")));
            p32.add(pgrid);

            p3.add(p31);
            p3.add(p32);

            this.add(p1);
            this.add(p3);
            init();

        }


        public void init() {

            int n = CMisc.ColorMode;
            if (n == 0)
                b1.setSelected(true);
            else if (n == 1)
                b2.setSelected(true);
            else b3.setSelected(true);

            pbk.setBackground(CMisc.getBackGroundColor());
            pgrid.setBackground(CMisc.getGridColor());
//            gxInstance.d.setBackground(CMisc.getBackGroundColor());
            this.repaint();
        }

        public void itemStateChanged(ItemEvent e) {
            Object o = e.getSource();
            if (o == b1 || o == b2 || o == b3) {
                if (b1.isSelected()) {
                    CMisc.ColorMode = 0;
                } else if (b2.isSelected()) {
                    CMisc.ColorMode = 1;
                } else if (b3.isSelected()) {
                    CMisc.ColorMode = 2;
                }
            }
            gxInstance.d.repaint();


        }

        public void mouseClicked(MouseEvent e) {
            Object o = e.getSource();
            if (o == pbk) {
                Color newColor = JColorChooser.showDialog(gxInstance,
                        GExpert.getLanguage("Choose Color"), CMisc.getBackGroundColor());
                if (newColor != null) {
                    Color c = newColor;
                    CMisc.setBackGroundColor(c);
                    gxInstance.d.setBackground(c);
                    pbk.setBackground(c);
                    pbk.repaint();
                }

            } else if (o == pgrid) {
                Color newColor = JColorChooser.showDialog(gxInstance,
                        GExpert.getLanguage("Choose Color"), CMisc.getBackGroundColor());
                if (newColor != null) {
                    Color c = newColor;
                    CMisc.setGridColor(c);
                    gxInstance.d.repaint();
                    pgrid.setBackground(c);
                    pgrid.repaint();
                }
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }


    }

    /**
     * ColorPane is a JPanel that represents a color selection area.
     * It is used in the colorPanel class to display the selected background and grid colors.
     */
    class ColorPane extends JPanel {
        int w, h;

        public ColorPane(int w, int h) {
            this.w = w;
            this.h = h;
            this.setBorder(new LineBorder(Color.lightGray, 1));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public Dimension getPreferredSize() {
            return new Dimension(w, h);
        }
    }

    /**
     * modePanel is a JPanel that allows the user to select various modes and settings.
     * It includes options for anti-aliasing, language selection, and look-and-feel selection.
     */
    class modePanel extends JPanel implements ItemListener {
        private JRadioButton r1, r2;
        private JComboBox blanguage, blook;

        modePanel() {

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p2.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("AntiAlias")));
            ButtonGroup bg1 = new ButtonGroup();
            r1 = new JRadioButton(GExpert.getLanguage("ON"));
            r2 = new JRadioButton(GExpert.getLanguage("OFF"));
            if (CMisc.AntiAlias)
                r1.setSelected(true);
            else r2.setSelected(true);
            r1.addItemListener(this);
            r2.addItemListener(this);
            bg1.add(r1);
            bg1.add(r2);
            p2.add(r1);
            p2.add(r2);


            JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

            p4.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("Language")));
            String[] lan = {
                    "English",
                    "Chinese",
                    "French",
                    "German",
                    "Hebrew",
                    "Hungarian",
                    "Italian",
                    "Persian",
                    "Polish",
                    "Portuguese",
                    "Serbian"
            };
            blanguage = new JComboBox(lan);
            blanguage.setSelectedItem(CMisc.lan);
            p4.add(blanguage);
            blanguage.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (blanguage.getSelectedIndex() != -1)
                        CMisc.lan = blanguage.getSelectedItem().toString();
                }
            });

            JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p5.setBorder(BorderFactory.createTitledBorder(GExpert.getLanguage("LookAndFeel")));
            UIManager.LookAndFeelInfo[] ff = UIManager.getInstalledLookAndFeels();
            String ss[] = new String[ff.length + 1];
            ss[0] = "Default";


            for (int i = 1; i < ff.length + 1; i++)
                ss[i] = ff[i - 1].getName();

            blook = new JComboBox(ss);
            blook.setSelectedItem(CMisc.lookAndFeel);
            p5.add(blook);
            blook.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (onSetting) return;
                    if (blook.getSelectedIndex() != -1)
                        CMisc.lookAndFeel = blook.getSelectedItem().toString();
                }
            });


            this.add(p2);
            this.add(p4);
            this.add(p5);
            this.revalidate();
        }

        public void itemStateChanged(ItemEvent e) {
            Object o = e.getSource();
            if (o == r1 || o == r2) {
                if (r1.isSelected())
                    CMisc.AntiAlias = true;
                else CMisc.AntiAlias = false;
            }
            gxInstance.d.repaint();
        }


        public void init() {


            blanguage.setSelectedItem(CMisc.lan);
            blook.setSelectedItem(CMisc.lookAndFeel);
            gxInstance.d.setBackground(CMisc.getBackGroundColor());
        }
    }
}
