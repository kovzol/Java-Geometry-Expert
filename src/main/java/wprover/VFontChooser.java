package wprover;

import static java.awt.font.TextAttribute.FAMILY;

import static java.awt.font.TextAttribute.SIZE;

import static java.awt.font.TextAttribute.UNDERLINE;

import static java.awt.font.TextAttribute.UNDERLINE_LOW_ONE_PIXEL;

import static java.awt.font.TextAttribute.STRIKETHROUGH;

import static java.awt.font.TextAttribute.STRIKETHROUGH_ON;

import static java.awt.font.TextAttribute.WEIGHT;

import static java.awt.font.TextAttribute.WEIGHT_BOLD;

import static java.awt.font.TextAttribute.POSTURE;

import static java.awt.font.TextAttribute.POSTURE_OBLIQUE;

import static java.awt.font.TextAttribute.SUPERSCRIPT;

import static java.awt.font.TextAttribute.SUPERSCRIPT_SUB;

import static java.awt.font.TextAttribute.SUPERSCRIPT_SUPER;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * VFontChooser is a class that extends JBaseDialog and provides a font chooser
 * dialog for selecting fonts, sizes, and styles. It includes checkboxes for
 * various font effects and a color picker.
 */
public class VFontChooser extends JBaseDialog {
    protected int Closed_Option = JOptionPane.CLOSED_OPTION;

    protected InputList fontNameInputList = new InputList(fontNames, "Name:");

    protected InputList fontSizeInputList = new InputList(fontSizes, "Size:");

    protected MutableAttributeSet attributes;

    protected JCheckBox boldCheckBox;

    protected JCheckBox italicCheckBox;

    protected JCheckBox underlineCheckBox;

    protected JCheckBox strikethroughCheckBox;

    protected JCheckBox subscriptCheckBox;

    protected JCheckBox superscriptCheckBox;

    protected ColorComboBox colorComboBox;

    protected FontLabel previewLabel;

    public static String[] fontNames;

    public static String[] fontSizes;

    private String PREVIEW_TEXT;
    private static VFontChooser chooser;
    private GExpert gxInstance;


    /**
     * Constructs a VFontChooser dialog with the specified owner.
     * Initializes the dialog components and sets up the layout.
     *
     * @param owner the GExpert instance that owns this dialog
     */
    private VFontChooser(GExpert owner) {
        super(owner.getFrame(), "Font Chooser", true);
        gxInstance = owner;
        this.setTitle(getLanguage("Font Chooser"));

        boldCheckBox = new JCheckBox(getLanguage("Bold"));
        italicCheckBox = new JCheckBox(getLanguage("Italic"));
        underlineCheckBox = new JCheckBox(getLanguage("Underline"));
        strikethroughCheckBox = new JCheckBox(getLanguage("Strikethrough"));
        subscriptCheckBox = new JCheckBox(getLanguage("Subscript"));
        superscriptCheckBox = new JCheckBox(getLanguage("Superscript"));
        PREVIEW_TEXT = getLanguage("Preview Font");


        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage("Font")));
        p.add(fontNameInputList);
        fontNameInputList.setDisplayedMnemonic('n');
        fontNameInputList.setToolTipText(getLanguage("Font name"));

        p.add(fontSizeInputList);
        fontSizeInputList.setDisplayedMnemonic('s');
        fontSizeInputList.setToolTipText(getLanguage("Font size"));
        getContentPane().add(p);

        p = new JPanel(new GridLayout(2, 3, 10, 5));
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage("Effects")));
        boldCheckBox.setMnemonic('b');
        boldCheckBox.setToolTipText("Bold font");
        p.add(boldCheckBox);

        italicCheckBox.setMnemonic('i');
        italicCheckBox.setToolTipText("Italic font");
        p.add(italicCheckBox);

        underlineCheckBox.setMnemonic('u');
        underlineCheckBox.setToolTipText("Underline font");
        p.add(underlineCheckBox);

        strikethroughCheckBox.setMnemonic('r');
        strikethroughCheckBox.setToolTipText("Strikethrough font");
        p.add(strikethroughCheckBox);

        subscriptCheckBox.setMnemonic('t');
        subscriptCheckBox.setToolTipText("Subscript font");
        p.add(subscriptCheckBox);

        superscriptCheckBox.setMnemonic('p');
        superscriptCheckBox.setToolTipText("Superscript font");
        p.add(superscriptCheckBox);
        getContentPane().add(p);

        getContentPane().add(Box.createVerticalStrut(5));
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalStrut(10));
        JLabel lbl = new JLabel(getLanguage("Color"));
        lbl.setDisplayedMnemonic('c');
        p.add(lbl);
        p.add(Box.createHorizontalStrut(20));
        colorComboBox = new ColorComboBox();
        lbl.setLabelFor(colorComboBox);
        colorComboBox.setToolTipText(getLanguage("Font color"));
        ToolTipManager.sharedInstance().registerComponent(colorComboBox);
        p.add(colorComboBox);
        p.add(Box.createHorizontalStrut(10));
        getContentPane().add(p);

        p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(new EtchedBorder(), getLanguage("Preview")));
        previewLabel = new FontLabel(PREVIEW_TEXT);

        p.add(previewLabel, BorderLayout.CENTER);
        getContentPane().add(p);

        p = new JPanel(new FlowLayout());
        JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
        JButton btOK = new JButton(getLanguage("OK"));
        btOK.setToolTipText(getLanguage("Save and exit"));
        getRootPane().setDefaultButton(btOK);
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Closed_Option = JOptionPane.OK_OPTION;
                dispose();
            }
        };
        btOK.addActionListener(actionListener);
        p1.add(btOK);

        JButton btCancel = new JButton(getLanguage("Cancel"));
        btCancel.setToolTipText(getLanguage("Exit without save"));
        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Closed_Option = JOptionPane.CANCEL_OPTION;
                dispose();
            }
        };
        btCancel.addActionListener(actionListener);
        p1.add(btCancel);
        p.add(p1);
        getContentPane().add(p);

//        pack();
        setResizable(false);

        ListSelectionListener listSelectListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updatePreview();
            }
        };
        fontNameInputList.addListSelectionListener(listSelectListener);
        fontSizeInputList.addListSelectionListener(listSelectListener);

        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        };
        boldCheckBox.addActionListener(actionListener);
        italicCheckBox.addActionListener(actionListener);
        colorComboBox.addActionListener(actionListener);
        underlineCheckBox.addActionListener(actionListener);
        strikethroughCheckBox.addActionListener(actionListener);
        subscriptCheckBox.addActionListener(actionListener);
        superscriptCheckBox.addActionListener(actionListener);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNames = ge.getAvailableFontFamilyNames();
        fontSizes = new String[]{"8", "9", "10", "11", "12", "14", "15", "16",
                "17", "18", "19", "20", "21", "22", "24", "26", "28", "36", "48", "72"};

//        SimpleAttributeSet a = new SimpleAttributeSet();
//        StyleConstants.setFontFamily(a, "Monospaced");
//        StyleConstants.setFontSize(a, 12);
//        this.setAttributes(a);
    }


    /**
     * Returns the language-specific string for the given key.
     *
     * @param s the key for the language string
     * @return the language-specific string
     */
    String getLanguage(String s) {
        if (gxInstance != null)
            return gxInstance.getLanguage(s);
        return s;
    }

    /**
     * Sets the attributes for the font chooser based on the given AttributeSet.
     *
     * @param a the AttributeSet containing font attributes
     */
    private void setAttributes(AttributeSet a) {
        attributes = new SimpleAttributeSet(a);
        String name = StyleConstants.getFontFamily(a);
        fontNameInputList.setSelected(name);
        int size = StyleConstants.getFontSize(a);
        fontSizeInputList.setSelectedInt(size);
        boldCheckBox.setSelected(StyleConstants.isBold(a));
        italicCheckBox.setSelected(StyleConstants.isItalic(a));
        underlineCheckBox.setSelected(StyleConstants.isUnderline(a));
        strikethroughCheckBox.setSelected(StyleConstants.isStrikeThrough(a));
        subscriptCheckBox.setSelected(StyleConstants.isSubscript(a));
        superscriptCheckBox.setSelected(StyleConstants.isSuperscript(a));
        colorComboBox.setSelectedItem(StyleConstants.getForeground(a));
        updatePreview();
    }

    /**
     * Retrieves the current font attributes from the font chooser.
     *
     * @return the AttributeSet containing the current font attributes
     */
    private AttributeSet getAttributes() {
        if (attributes == null)
            return null;
        StyleConstants.setFontFamily(attributes, fontNameInputList.getSelected());
        StyleConstants.setFontSize(attributes, fontSizeInputList.getSelectedInt());
        StyleConstants.setBold(attributes, boldCheckBox.isSelected());
        StyleConstants.setItalic(attributes, italicCheckBox.isSelected());
        StyleConstants.setUnderline(attributes, underlineCheckBox.isSelected());
        StyleConstants.setStrikeThrough(attributes, strikethroughCheckBox.isSelected());
        StyleConstants.setSubscript(attributes, subscriptCheckBox.isSelected());
        StyleConstants.setSuperscript(attributes, superscriptCheckBox.isSelected());
        StyleConstants.setForeground(attributes, (Color) colorComboBox.getSelectedItem());
        return attributes;
    }

    /**
     * Returns the option selected by the user in the font chooser dialog.
     *
     * @return the selected option
     */
    private int getOption() {
        return Closed_Option;
    }

    /**
     * Updates the font preview based on the current selections in the font chooser.
     */
    protected void updatePreview() {
        StringBuilder previewText = new StringBuilder(PREVIEW_TEXT);
        String name = fontNameInputList.getSelected();
        int size = fontSizeInputList.getSelectedInt();
        if (size <= 0)
            return;

        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();

        attributes.put(FAMILY, name);
        attributes.put(SIZE, (float) size);

        // Using HTML to force JLabel to manage natively unsupported attributes
        if (underlineCheckBox.isSelected() || strikethroughCheckBox.isSelected()) {
            previewText.insert(0, "<html>");
            previewText.append("</html>");
        }

        if (underlineCheckBox.isSelected()) {
            attributes.put(UNDERLINE, UNDERLINE_LOW_ONE_PIXEL);
            previewText.insert(6, "<u>");
            previewText.insert(previewText.length() - 7, "</u>");
        }
        if (strikethroughCheckBox.isSelected()) {
            attributes.put(STRIKETHROUGH, STRIKETHROUGH_ON);
            previewText.insert(6, "<strike>");
            previewText.insert(previewText.length() - 7, "</strike>");
        }

        if (boldCheckBox.isSelected())
            attributes.put(WEIGHT, WEIGHT_BOLD);
        if (italicCheckBox.isSelected())
            attributes.put(POSTURE, POSTURE_OBLIQUE);

        if (subscriptCheckBox.isSelected()) {
            attributes.put(SUPERSCRIPT, SUPERSCRIPT_SUB);
        }
        if (superscriptCheckBox.isSelected())
            attributes.put(SUPERSCRIPT, SUPERSCRIPT_SUPER);

        superscriptCheckBox.setEnabled(!subscriptCheckBox.isSelected());
        subscriptCheckBox.setEnabled(!superscriptCheckBox.isSelected());

        Font fn = new Font(attributes);

        previewLabel.setText(previewText.toString());
        previewLabel.setFont(fn);

        Color c = (Color) colorComboBox.getSelectedItem();
        previewLabel.setForeground(c);
        previewLabel.repaint();
    }

    /**
     * Displays the font chooser dialog and returns the selected option.
     *
     * @param frame the parent frame
     * @param f     the initial font
     * @param title the title of the dialog
     * @param c     the initial color
     * @return the selected option
     */
    public static int showDialog(GExpert frame, Font f, String title, Color c) {
        if (chooser == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontNames = ge.getAvailableFontFamilyNames();
            fontSizes = new String[]{"8", "9", "10", "11", "12", "13", "14", "15", "16",
                    "18", "20", "22", "24", "26", "28", "36", "48", "72"};
            chooser = new VFontChooser(frame);
        }
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setFontSize(a, f.getSize());
        StyleConstants.setBold(a, f.isBold());
        StyleConstants.setItalic(a, f.isItalic());
        StyleConstants.setFontFamily(a, f.getFamily());
        chooser.setAttributes(a);
        int x = frame.getX() + frame.getWidth() / 2 - 350 / 2;
        int y = frame.getY() + frame.getHeight() / 2 - 450 / 2;
        chooser.setLocation(x, y);
        chooser.setSize(350, 450);
        chooser.setVisible(true);
        return chooser.getOption();
    }

    /**
     * Displays the font chooser dialog and returns the selected option.
     *
     * @param frame the parent frame
     * @param f     the initial font
     * @param c     the initial color
     * @return the selected option
     */
    public static int showDialog(GExpert frame, Font f, Color c) {
        return showDialog(frame, f, "Font Chooser", c);
    }

    /**
     * Returns the selected font from the font chooser dialog.
     *
     * @return the selected font
     */
    public static Font getReturnFont() {
        SimpleAttributeSet a = (SimpleAttributeSet) chooser.getAttributes();

        String s1 = StyleConstants.getFontFamily(a);
        int size = StyleConstants.getFontSize(a);

        boolean b = StyleConstants.isBold(a);
        boolean it = StyleConstants.isItalic(a);
        int style;
        if (b)
            style = Font.BOLD;
        else if (it)
            style = Font.ITALIC;
        else
            style = Font.PLAIN;

        Font f = new Font(s1, style, size);
        return f;
    }

    /**
     * InputList is a custom JPanel that contains a label, a text field, and a list.
     * It allows the user to select an item from the list and displays it in the text field.
     */
    class InputList extends JPanel implements ListSelectionListener, ActionListener {
        protected JLabel label = new JLabel();

        protected JTextField textfield;

        protected JList list;

        protected JScrollPane scroll;

        public InputList(String[] data, String title) {
            setLayout(null);

            add(label);
            textfield = new OpelListText();
            textfield.addActionListener(this);
            label.setLabelFor(textfield);
            add(textfield);
            list = new OpelListList(data);
            list.setVisibleRowCount(4);
            list.addListSelectionListener(this);
            scroll = new JScrollPane(list);
            add(scroll);
        }

        public InputList(String title, int numCols) {
            setLayout(null);
            label = new OpelListLabel(title, JLabel.LEFT);
            add(label);
            textfield = new OpelListText(numCols);
            textfield.addActionListener(this);
            label.setLabelFor(textfield);
            add(textfield);
            list = new OpelListList();
            list.setVisibleRowCount(4);
            list.addListSelectionListener(this);
            scroll = new JScrollPane(list);
            add(scroll);
        }

        public void setToolTipText(String text) {
            super.setToolTipText(text);
            label.setToolTipText(text);
            textfield.setToolTipText(text);
            list.setToolTipText(text);
        }

        public void setDisplayedMnemonic(char ch) {
            label.setDisplayedMnemonic(ch);
        }

        public void setSelected(String sel) {
            list.setSelectedValue(sel, true);
            textfield.setText(sel);
        }

        public String getSelected() {
            return textfield.getText();
        }

        public void setSelectedInt(int value) {
            setSelected(Integer.toString(value));
        }

        public int getSelectedInt() {
            try {
                return Integer.parseInt(getSelected());
            } catch (NumberFormatException ex) {
                return -1;
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            Object obj = list.getSelectedValue();
            if (obj != null)
                textfield.setText(obj.toString());
        }

        public void actionPerformed(ActionEvent e) {
            ListModel model = list.getModel();
            String key = textfield.getText().toLowerCase();
            for (int k = 0; k < model.getSize(); k++) {
                String data = (String) model.getElementAt(k);
                if (data.toLowerCase().startsWith(key)) {
                    list.setSelectedValue(data, true);
                    break;
                }
            }
        }

        public void addListSelectionListener(ListSelectionListener lst) {
            list.addListSelectionListener(lst);
        }

        public Dimension getPreferredSize() {
            Insets ins = getInsets();
            Dimension labelSize = label.getPreferredSize();
            Dimension textfieldSize = textfield.getPreferredSize();
            Dimension scrollPaneSize = scroll.getPreferredSize();
            int w = Math.max(Math.max(labelSize.width, textfieldSize.width),
                    scrollPaneSize.width);
            int h = labelSize.height + textfieldSize.height + scrollPaneSize.height;
            return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
        }

        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void doLayout() {
            Insets ins = getInsets();
            Dimension size = getSize();
            int x = ins.left;
            int y = ins.top;
            int w = size.width - ins.left - ins.right;
            int h = size.height - ins.top - ins.bottom;

            Dimension labelSize = label.getPreferredSize();
            label.setBounds(x, y, w, labelSize.height);
            y += labelSize.height;
            Dimension textfieldSize = textfield.getPreferredSize();
            textfield.setBounds(x, y, w, textfieldSize.height);
            y += textfieldSize.height;
            scroll.setBounds(x, y, w, h - y);
        }

        public void appendResultSet(ResultSet results, int index,
                                    boolean toTitleCase) {
            textfield.setText("");
            DefaultListModel model = new DefaultListModel();
            try {
                while (results.next()) {
                    String str = results.getString(index);
                    if (toTitleCase) {
                        str = Character.toUpperCase(str.charAt(0))
                                + str.substring(1);
                    }

                    model.addElement(str);
                }
            } catch (SQLException ex) {
                System.err.println("appendResultSet: " + ex.toString());
            }
            list.setModel(model);
            if (model.getSize() > 0)
                list.setSelectedIndex(0);
        }

        class OpelListLabel extends JLabel {
            public OpelListLabel(String text, int alignment) {
                super(text, alignment);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        class OpelListText extends JTextField {
            public OpelListText() {
            }

            public OpelListText(int numCols) {
                super(numCols);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        class OpelListList extends JList {
            public OpelListList() {
            }

            public OpelListList(String[] data) {
                super(data);
            }

            public AccessibleContext getAccessibleContext() {
                return InputList.this.getAccessibleContext();
            }
        }

        // Accessibility Support

        public AccessibleContext getAccessibleContext() {
            if (accessibleContext == null)
                accessibleContext = new AccessibleOpenList();
            return accessibleContext;
        }

        protected class AccessibleOpenList extends AccessibleJComponent {

            public String getAccessibleName() {
                CMisc.print("getAccessibleName: " + accessibleName);
                if (accessibleName != null)
                    return accessibleName;
                return label.getText();
            }

            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.LIST;
            }
        }
    }

    /**
     * FontLabel is a custom JLabel that displays a font preview. It sets the
     * background, foreground, and border properties for the label.
     */
    class FontLabel extends JLabel {
        public FontLabel(String text) {
            super(text, JLabel.CENTER);
            setBackground(Color.white);
            setForeground(Color.black);
            setOpaque(true);
            setBorder(new LineBorder(Color.black));
            setPreferredSize(new Dimension(120, 40));
        }
    }

    /**
     * ColorComboBox is a custom JComboBox that displays a list of colors. It uses a
     * custom renderer to display the colors in the combo box.
     */
    class ColorComboBox extends JComboBox {

        public ColorComboBox() {
            int[] values = new int[]{0, 128, 192, 255};
            for (int r = 0; r < values.length; r++)
                for (int g = 0; g < values.length; g++)
                    for (int b = 0; b < values.length; b++) {
                        Color c = new Color(values[r], values[g], values[b]);
                        addItem(c);
                    }
            setRenderer(new ColorComboRenderer1());

        }

        class ColorComboRenderer1 extends JPanel implements ListCellRenderer {
            protected Color m_c = Color.black;

            public ColorComboRenderer1() {
                super();
                setBorder(new CompoundBorder(new MatteBorder(2, 10, 2, 10, Color.white), new LineBorder(Color.black)));
            }

            public Component getListCellRendererComponent(JList list, Object obj, int row, boolean sel, boolean hasFocus) {
                if (obj instanceof Color)
                    m_c = (Color) obj;
                return this;
            }

            public void paint(Graphics g) {
                setBackground(m_c);
                super.paint(g);
            }

        }
    }

}