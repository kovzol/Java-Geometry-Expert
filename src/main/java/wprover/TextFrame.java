package wprover;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Vector;
import java.io.IOException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;


/**
 * TextFrame.java
 * This class represents a dialog for editing text properties in a drawing application.
 * It allows users to set font, size, style, and color for the text.
 * The class also provides functionality for cut, copy, paste, delete, and select all operations.
 */
public class TextFrame extends JBaseDialog implements ItemListener,
        ActionListener, FocusListener, MouseListener {
    JEditorPane textpane = null;
    CText text = null;

    JComboBox fonts, sizes, styles, color;
    JButton bok, bcancel;
    int index = 0;
    String fontchoice = "fontchoice";
    int stChoice = 0;
    String siChoice = "10";
    Vector fontfamily;
    //    Font defaultFont = new Font("Dialog", Font.PLAIN, 16);
    GExpert gxInstance;

    int x, y;


    /**
     * Initializes the TextFrame with font, size, style, and color options.
     * Sets up the layout and components for the text editing toolbar.
     */
    public void init() {

        Font tf = new Font("Dialog", Font.PLAIN, 12);

        getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel() {
            public Dimension getPreferredSize() {
                Dimension dm = super.getPreferredSize();
                dm.setSize(dm.getWidth(), 22);
                return dm;
            }
        };
        topPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String envfonts[] = gEnv.getAvailableFontFamilyNames();

        Font cfont = new Font("Dialog", Font.PLAIN, 16);

        fontfamily = new Vector();
        for (int i = 1; i < envfonts.length; i++)
            fontfamily.addElement(envfonts[i]);
        fonts = new JComboBox(fontfamily);
        fonts.setFont(tf);

        fonts.setMaximumRowCount(9);
        fonts.addItemListener(this);
        fontchoice = envfonts[0];
        topPanel.add(fonts);
        topPanel.add(Box.createHorizontalStrut(5));


        int[] fz = CMisc.getFontSizePool();
        int n = fz.length;
        Object[] o = new Object[n];
        for (int i = 0; i < n; i++)
            o[i] = fz[i];

        sizes = new JComboBox(o);

        sizes.setFont(tf);
        sizes.setMaximumRowCount(20);
        sizes.addItemListener(this);
        topPanel.add(sizes);
        topPanel.add(Box.createHorizontalStrut(5));


        styles = new JComboBox(new Object[]{
                "PLAIN",
                "BOLD",
                "ITALIC"});
        styles.setMaximumRowCount(9);
        styles.setFont(tf);
        styles.addItemListener(this);
        sizes.setMaximumRowCount(9);
        topPanel.add(styles);
        topPanel.add(Box.createHorizontalStrut(5));


        color = CCoBox.CreateAInstance();
        color.setPreferredSize(new Dimension(100, 20));
        color.setFont(tf);
        topPanel.add(color);
        topPanel.add(Box.createHorizontalStrut(5));

        bok = new JButton(GExpert.getLanguage("OK"));
        bok.addActionListener(this);
        topPanel.add(bok);
        bcancel = new JButton(GExpert.getLanguage("Cancel"));
        bcancel.addActionListener(this);
        topPanel.add(bcancel);
        bok.setText(GExpert.getLanguage("OK"));
        bcancel.setText(GExpert.getLanguage("Cancel"));

        fonts.addActionListener(this);
        sizes.addActionListener(this);
        styles.addActionListener(this);
        color.addActionListener(this);

        color.setSelectedIndex(16);

        JToolBar tb = new JToolBar("style");
        tb.add(topPanel);
        tb.setFloatable(false);

        textpane.addMouseListener(this);
        this.setCurrentFont(cfont);
        getContentPane().add(BorderLayout.SOUTH, tb);
    }

    /**
     * Sets the current font for the text pane and updates the font, size, and style selections.
     *
     * @param f the Font to set as the current font
     */
    public void setCurrentFont(Font f) {
        if (f == null) return;
        int size = f.getSize();
        int type = f.getStyle();
        String fname = f.getName();

        for (int i = 0; i < fontfamily.size(); i++) {
            String s = (String) fontfamily.get(i);
            if (s.compareTo(fname) == 0) {
                fonts.setSelectedIndex(i);
                break;
            }
        }
        styles.setSelectedIndex(type);

        for (int i = 0; i < sizes.getItemCount(); i++) {
            int s = ((Integer) sizes.getItemAt(i)).intValue();
            if (s == size) {
                sizes.setSelectedIndex(i);
                break;
            }

        }

    }

    /**
     * Constructs a new TextFrame with the specified GExpert instance and coordinates.
     *
     * @param gx the GExpert instance to associate with this frame
     * @param xt the x-coordinate for the frame
     * @param yt the y-coordinate for the frame
     */
    public TextFrame(GExpert gx, double xt, double yt) {
        super(gx.getFrame());
        this.gxInstance = gx;
        this.x = (int) xt;
        this.y = (int) yt;

        if (CMisc.isApplication())
            this.setAlwaysOnTop(true);
        textpane = new JEditorPane();
        this.init();
        this.getContentPane().add(new JScrollPane(textpane));
        text = null;
        //       setCurrentFont(gxInstance.getDefaultFont());
        this.setSize(550, 200);

    }

    /**
     * Sets the text and font for the text pane based on the provided CText object.
     *
     * @param tx the CText object containing the text and font to set
     */
    void setText(CText tx) {
        if (tx != null) {
            text = tx;
            textpane.setText(tx.getText());
            textpane.setFont(tx.getFont());
            Font f = tx.getFont();
            color.setSelectedIndex(tx.m_color);
            setCurrentFont(f);
            this.setTitle(GExpert.getLanguage("Text"));
        } else {
            this.setTitle(GExpert.getLanguage("Text"));
            text = null;
            textpane.setText("");
        }
    }

    /**
     * Returns the preferred size of the TextFrame.
     *
     * @return the preferred Dimension of the TextFrame
     */
    public Dimension getPreferredSize() {
        Dimension dm = super.getPreferredSize();
        dm.setSize(dm.getWidth(), 200);
        return dm;
    }

    /**
     * Handles item state change events for the combo boxes.
     *
     * @param e the ItemEvent triggered by selecting an item in a combo box
     */
    public void itemStateChanged(ItemEvent e) {
    }

    /**
     * Handles action events for the OK and Cancel buttons, and updates the text pane font and color.
     *
     * @param e the ActionEvent triggered by clicking a button
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bok) {
            String str = textpane.getText();

            CText ct = text;
            if (ct == null) {
                if (str == null || str.length() == 0) {
                    this.setVisible(false);
                    return;
                }
                ct = new CText(x, y, str);
            } else
                ct.setText(str);

            String s = (String) fonts.getSelectedItem();
            int size = ((Integer) sizes.getSelectedItem()).intValue();
            int type = styles.getSelectedIndex();
            Font f = new Font(s, type, size);
            ct.setFont(f);
            int id = color.getSelectedIndex();
            ct.setColor(id);
            if (ct != text)
                gxInstance.dp.addText(ct);
            this.setVisible(false);
            gxInstance.d.repaint();
        } else if (e.getSource() == bcancel) {
            this.setVisible(false);
        } else {
            String s = (String) fonts.getSelectedItem();
            int size = ((Integer) sizes.getSelectedItem()).intValue();
            int type = styles.getSelectedIndex();
            Font f = new Font(s, type, size);
            textpane.setFont(f);
            int id = color.getSelectedIndex();
            textpane.setForeground(DrawData.getColor(id));
            if (text != null)
                text.m_color = (id);
        }

    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    }

    /**
     * Handles the mouse clicked event.
     *
     * @param e the MouseEvent triggered by clicking the mouse
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            new TextPopupMenu(textpane.getSelectionStart()
                    != textpane.getSelectionEnd()).show(textpane, e.getX(), e.getY());
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

    /**
     * TextPopupMenu is a class that extends JPopupMenu and implements ActionListener and ClipboardOwner.
     * It provides a context menu for text editing operations such as Cut, Copy, Paste, Delete, and Select All.
     */
    class TextPopupMenu extends JPopupMenu implements ActionListener, ClipboardOwner {
        /**
         * Constructs a new TextPopupMenu with the specified selection state.
         *
         * @param selected whether the menu items should be enabled based on selection
         */
        public TextPopupMenu(boolean selected) {
            this.addMenuItem("Cut", selected);
            this.addMenuItem("Copy", selected);
            this.addMenuItem("Paste", true);
            this.addSeparator();
            this.addMenuItem("Delete", selected);
            this.addMenuItem("Select All", true);
        }

        /**
         * Adds a menu item to the popup menu.
         *
         * @param s        the name of the menu item
         * @param selected whether the menu item should be enabled
         */
        public void addMenuItem(String s, boolean selected) {
            JMenuItem item = new JMenuItem(s);
            item.addActionListener(TextPopupMenu.this);
            item.setEnabled(selected);
            this.add(item);
        }

        /**
         * Handles the loss of ownership of the clipboard.
         *
         * @param aClipboard the clipboard that this owner has lost ownership of
         * @param aContents  the contents which this owner had placed on the clipboard
         */
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
            // do nothing
        }

        /**
         * Handles action events for the popup menu items.
         *
         * @param e the ActionEvent triggered by selecting a menu item
         */
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if (s.equalsIgnoreCase("Cut")) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(textpane.getSelectedText());
                clipboard.setContents(stringSelection, TextPopupMenu.this);
                int st = textpane.getSelectionStart();
                int ed = textpane.getSelectionEnd();
                String text = textpane.getText();
                textpane.setText(text.substring(0, st) + text.substring(ed, text.length()));

            } else if (s.equalsIgnoreCase("Copy")) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringSelection = new StringSelection(textpane.getSelectedText());
                clipboard.setContents(stringSelection, this);
            } else if (s.equalsIgnoreCase("Paste")) {
                int st = textpane.getSelectionStart();
                int ed = textpane.getSelectionEnd();
                String text = textpane.getText();
                textpane.setText(text.substring(0, st) + getClipboardContents() + text.substring(ed, text.length()));
            } else if (s.equalsIgnoreCase("Delete")) {
                int st = textpane.getSelectionStart();
                int ed = textpane.getSelectionEnd();
                String text = textpane.getText();
                textpane.setText(text.substring(0, st) + text.substring(ed, text.length()));
            } else if (s.equalsIgnoreCase("Select All")) {
                textpane.setSelectionStart(0);
                textpane.setSelectionEnd(textpane.getText().length());
            }
        }

        /**
         * Retrieves the contents of the system clipboard as a string.
         *
         * @return the clipboard contents as a string
         */
        public String getClipboardContents() {
            String result = "";
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(TextPopupMenu.this);
            boolean hasTransferableText =
                    (contents != null) &&
                            contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                try {
                    result = (String) contents.getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException ex) {
                    // highly unlikely since we are using a standard DataFlavor
                    System.out.println(ex);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
            }
            return result;
        }
    }
}
