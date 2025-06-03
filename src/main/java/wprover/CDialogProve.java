package wprover;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * This class represents a dialog for proving geometric terms.
 */
public class CDialogProve extends JBaseDialog {
    ProvePane m_cp;

    /**
     * Constructs a CDialogProve object.
     *
     * @param owner the owner of this dialog
     */
    public CDialogProve(GExpert owner) {
        super((JFrame) null);
        m_cp = new ProvePane(owner, this);
        this.getContentPane().add(m_cp);
        this.setSize(650, 230);
    }

    /**
     * Sets the selected items in the dialog.
     *
     * @param v the vector of selected items
     */
    public void setSelect(Vector v) {
        m_cp.setSelect(v);
    }
}

/**
 * This class represents the panel used in CDialogProve.
 */
class ProvePane extends JPanel
        implements ActionListener, ListSelectionListener, ItemListener, PopupMenuListener {
    private CProveField Cpv = null;
    private JDialog dialog;

    private JTextField captainField;
    private JTextArea proveField;
    private JList selectField;
    private DefaultListModel listModel;
    private Vector vlist;
    private GExpert gxInstance;

    private CProveText cptext;
    private JButton b_select, b_ok, b_cancel;

    private ColorButtonPanel color_captain, color_text;
    private Vector fontfamily;
    private JComboBox bfonts, bsize;
    private JCheckBox cbox;

    private JTextField lrule;
    private JMenu mrule;
    private String srule;

    final static int GAP = 10;

    /**
     * Constructs a ProvePane object.
     *
     * @param gx the GExpert instance
     * @param dlg the JDialog instance
     */
    public ProvePane(GExpert gx, JDialog dlg) {
        gxInstance = gx;
        dialog = dlg;
        vlist = new Vector();
        setRuleList();

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        JPanel leftHalf = new JPanel() {
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE,
                        pref.height);
            }
        };
        leftHalf.setLayout(new BoxLayout(leftHalf, BoxLayout.PAGE_AXIS));
        leftHalf.add(createEntryFields());

        add(leftHalf);
        add(createAddressDisplay());
    }

    /**
     * Sets the rule list for this panel.
     */
    public void setRuleList() {
        String user_directory = GExpert.getUserDir() + "/wprover/rules";
        mrule = new JMenu("-->");
        addDirectory(mrule, user_directory);
    }


    /**
     * Populate a JMenu by scanning a folder on the classpath.
     * @param menu        the menu to fill
     * @param resourceDir the resource path (e.g. "docs/examples")
     */
    public void addDirectory(JMenu menu, String resourceDir) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL dirUrl = cl.getResource(resourceDir + "/");
            if (dirUrl == null) return;

            if (dirUrl.getProtocol().equals("file")) {
                // running in IDE/Gradle on disk
                File folder = new File(dirUrl.toURI());
                File[] files = folder.listFiles();
                // Sort files using custom comparator
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        // If both are directories or both are files, use custom ordering
                        if (f1.isDirectory() == f2.isDirectory()) {
                            String name1 = f1.getName();
                            String name2 = f2.getName();

                            // Check if both names start with numbers
                            if (name1.matches("^\\d+.*") && name2.matches("^\\d+.*")) {
                                // Extract the numeric prefix
                                String num1 = name1.replaceAll("^(\\d+).*", "$1");
                                String num2 = name2.replaceAll("^(\\d+).*", "$1");

                                // If numeric parts are different, compare them numerically
                                if (!num1.equals(num2)) {
                                    return Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
                                }
                            }
                            // If one name starts with a number and the other doesn't, prioritize the one with number
                            else if (name1.matches("^\\d+.*")) {
                                return -1;
                            }
                            else if (name2.matches("^\\d+.*")) {
                                return 1;
                            }

                            // Otherwise, use alphabetical order
                            return name1.compareTo(name2);
                        }

                        // Directories come before files
                        return f1.isDirectory() ? -1 : 1;
                    }
                });
                for (File f : files) {
                    handleEntry(menu, resourceDir, f.getName(), f.isDirectory());
                }
            } else {
                // running from JAR(specifically for CheerpJ)
                String path = resourceDir + "/";
                try {
                    // Use JarFile approach to get all entries
                    URL jarUrl = cl.getResource(path);
                    if (jarUrl != null) {
                        String jarPath = jarUrl.toString();
                        if (jarPath.startsWith("jar:file:")) {
                            jarPath = jarPath.substring(9, jarPath.indexOf("!"));
                            try (JarFile jar = new JarFile(jarPath)) {
                                Enumeration<JarEntry> entries = jar.entries();
                                // First collect all entries to process
                                java.util.Map<String, Boolean> processedDirs = new java.util.HashMap<>();
                                java.util.List<String> filesToProcess = new java.util.ArrayList<String>();

                                while (entries.hasMoreElements()) {
                                    JarEntry entry = entries.nextElement();
                                    String entryName = entry.getName();

                                    if (entryName.startsWith(path)) {
                                        String relativePath = entryName.substring(path.length());
                                        if (relativePath.isEmpty()) continue;

                                        int slashIndex = relativePath.indexOf('/');
                                        if (slashIndex == -1) {
                                            // Direct file in this directory
                                            filesToProcess.add(relativePath);
                                        } else {
                                            // Subdirectory
                                            String dirName = relativePath.substring(0, slashIndex);
                                            if (!processedDirs.containsKey(dirName)) {
                                                processedDirs.put(dirName, true);
                                            }
                                        }
                                    }
                                }

                                // Sort directories using custom comparator
                                java.util.List<String> dirNames = new java.util.ArrayList<>(processedDirs.keySet());
                                Collections.sort(dirNames, new Comparator<String>() {
                                    @Override
                                    public int compare(String name1, String name2) {
                                        // Check if both names start with numbers
                                        if (name1.matches("^\\d+.*") && name2.matches("^\\d+.*")) {
                                            // Extract the numeric prefix
                                            String num1 = name1.replaceAll("^(\\d+).*", "$1");
                                            String num2 = name2.replaceAll("^(\\d+).*", "$1");

                                            // If numeric parts are different, compare them numerically
                                            if (!num1.equals(num2)) {
                                                return Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
                                            }
                                        }
                                        // If one name starts with a number and the other doesn't, prioritize the one with number
                                        else if (name1.matches("^\\d+.*")) {
                                            return -1;
                                        }
                                        else if (name2.matches("^\\d+.*")) {
                                            return 1;
                                        }

                                        // Otherwise, use alphabetical order
                                        return name1.compareTo(name2);
                                    }
                                });

                                // Process directories in sorted order
                                for (String dirName : dirNames) {
                                    handleEntry(menu, resourceDir, dirName, true);
                                }

                                // Sort files using custom comparator
                                Collections.sort(filesToProcess, new Comparator<String>() {
                                    @Override
                                    public int compare(String name1, String name2) {
                                        // Check if both names start with numbers
                                        if (name1.matches("^\\d+.*") && name2.matches("^\\d+.*")) {
                                            // Extract the numeric prefix
                                            String num1 = name1.replaceAll("^(\\d+).*", "$1");
                                            String num2 = name2.replaceAll("^(\\d+).*", "$1");

                                            // If numeric parts are different, compare them numerically
                                            if (!num1.equals(num2)) {
                                                return Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
                                            }
                                        }
                                        // If one name starts with a number and the other doesn't, prioritize the one with number
                                        else if (name1.matches("^\\d+.*")) {
                                            return -1;
                                        }
                                        else if (name2.matches("^\\d+.*")) {
                                            return 1;
                                        }

                                        // Otherwise, use alphabetical order
                                        return name1.compareTo(name2);
                                    }
                                });

                                // Process all files in this directory
                                for (String fileName : filesToProcess) {
                                    handleEntry(menu, resourceDir, fileName, false);
                                }
                            }
                        } else {
                            // Fallback to JarInputStream if jar:file: protocol not available
                            try (InputStream is = cl.getResourceAsStream(path);
                                 JarInputStream jin = new JarInputStream(is)) {
                                JarEntry e;
                                while ((e = jin.getNextJarEntry()) != null) {
                                    String name = e.getName();
                                    if (name.startsWith(path)) {
                                        String entry = name.substring(path.length());
                                        if (!entry.isEmpty() && !entry.contains("/")) {
                                            handleEntry(menu, resourceDir, entry, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Log the exception but continue processing
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleEntry(JMenu menu, String base, String name, boolean isDir) {
        if (isDir) {
            JMenu sub = new JMenu(name);
            menu.add(sub);
            addDirectory(sub, base + "/" + name);
        } else if (name.endsWith(".gex")) {
            String label = name.substring(0, name.length() - 4);
            JMenuItem item = new JMenuItem(label);
            item.addActionListener(this);
            // store the _resource_ path for later loading
            item.setName(base + "/" + name);
            menu.add(item);
        }
    }


    /**
     * Adds directories and files to the given menu.
     *
     * @param menu the menu to add items to
     * @param f the directory file
     * @param apath the path of the directory
     */
    /*
    public void addDirectory(JMenu menu, File f, String apath) {
        if (!f.exists()) return;
        File[] flist = f.listFiles();
        String sp = GExpert.getFileSeparator();

        for (int i = 0; i < flist.length; i++) {
            String sn = flist[i].getName();

            if (flist[i].isFile()) {
                if (sn.endsWith(".gex")) {
                    String sf = sn.substring(0, sn.length() - 4);
                    JMenuItem item = new JMenuItem(sf);
                    item.addActionListener(this);
                    item.setName(apath + sp + sn);
                    menu.add(item);
                }
            } else if (flist[i].isDirectory()) {
                JMenu m = new JMenu(sn);
                menu.add(m);
                addDirectory(m, flist[i], apath + sp + sn);
            }
        }
    }*/

    /**
     * Sets the value for the panel.
     *
     * @param cp the CProveText to set
     */
    public void setValue(CProveText cp) {
        cptext = cp;
        captainField.setForeground(cp.getCaptainColor());

        captainField.setText(cp.getHead());
        proveField.setText(cp.getMessage());
        proveField.setForeground(cp.getMessageColor());
        Vector v = cp.getObjectList();
        vlist.clear();

        for (int i = 0; i < v.size(); i++) {
            CClass cc = (CClass) v.get(i);
            if (cc.m_type != CClass.TEXT)
                vlist.add(cc);
        }
        resetModel();
        Font f = cptext.getFont();

        bfonts.setSelectedItem(f.getName());
        bsize.setSelectedItem(f.getSize());

        Color c1 = cptext.getCaptainColor();
        color_captain.setForeground(c1);
        color_captain.setBackground(c1);

        Color c2 = cptext.getMessageColor();
        color_text.setForeground(c2);
        color_text.setBackground(c2);
        cbox.setSelected(cp.getVisible());
        srule = cp.getRule();
        lrule.setText(cp.getRule());
    }

    /**
     * Resets the model for the list.
     */
    public void resetModel() {
        listModel.clear();
        for (int i = 0; i < vlist.size(); i++) {
            CClass cc = (CClass) vlist.get(i);
            listModel.addElement(cc.TypeString());
        }
    }

    /**
     * Creates the buttons for the panel.
     *
     * @return the created button component
     */
    protected JComponent createButtons() {
        JPanel top = new JPanel();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        JPanel prule = new JPanel();
        prule.setLayout(new BoxLayout(prule, BoxLayout.Y_AXIS));
        lrule = new JTextField(20);
        prule.add(lrule);
        JMenuBar jbar = new JMenuBar();
        jbar.add(mrule);
        prule.add(jbar);

        b_select = new JButton(GExpert.getLanguage("Clear"));
        b_select.addActionListener(this);

        panel.add(b_select);
        b_ok = new JButton(GExpert.getLanguage("OK"));
        b_ok.addActionListener(this);
        panel.add(b_ok);

        b_cancel = new JButton(GExpert.getLanguage("Clear"));
        b_cancel.addActionListener(this);
        panel.add(b_cancel);

        Font f = new Font("Dialog", Font.PLAIN, 14);
        b_select.setFont(f);
        b_ok.setFont(f);
        b_cancel.setFont(f);
        b_select.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        b_ok.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        b_cancel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        top.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP - 5, GAP - 5));
        top.add(prule);
        top.add(panel);

        return top;
    }

    /**
     * Handles the value change event for the list selection.
     *
     * @param e the list selection event
     */
    public void valueChanged(ListSelectionEvent e) {
        int index = selectField.getSelectedIndex();
        if (index >= 0 && index < vlist.size()) {
            Vector vc = new Vector();
            vc.add(vlist.get(index));
            gxInstance.dp.setObjectListForFlash(vc);
        }
    }

    /**
     * Handles the action event for the buttons and menu items.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        String sname = e.getActionCommand();

        if (obj == this.b_select) {
            listModel.clear();
            vlist.clear();
            gxInstance.dp.SetCurrentAction(DrawProcess.SELECT);
        } else if (obj == this.b_ok) {
            cptext.setHead(captainField.getText());
            cptext.setMessage(proveField.getText());
            cptext.setObjectList(vlist);
            String s = (String) bfonts.getSelectedItem();
            int size = ((Integer) bsize.getSelectedItem()).intValue();
            if (size < 10) return;
            Font ff = cptext.getFont();
            cptext.setFont(new Font(s, ff.getStyle(), size));
            cptext.setCaptainColor(captainField.getForeground());
            cptext.setMessageColor(proveField.getForeground());
            cptext.setVisible(cbox.isSelected());

            String rule = lrule.getText();
            cptext.setRule(rule);
            if (rule.length() > 0)
                cptext.setRulePath(srule);
            else cptext.setRulePath("");
            gxInstance.dp.cpfield.reGenerateIndex();
            dialog.setVisible(false);
            gxInstance.d.repaint();

        } else if (obj == this.b_cancel)
            dialog.setVisible(false);
        else if (obj instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();
            String path = item.getName();
            lrule.setText(sname);
            srule = path;
        }
    }

    /**
     * Sets the selected items for the panel.
     *
     * @param v the vector of selected items
     */
    public void setSelect(Vector v) {
        vlist.clear();
        vlist.addAll(v);
        for (int i = 0; i < v.size(); i++) {
            Object obj = v.get(i);
            if (!vlist.contains(obj))
                vlist.add(obj);
        }
        this.resetModel();
    }

    /**
     * Creates the address display component.
     *
     * @return the created address display component
     */
    protected JComponent createAddressDisplay() {
        JPanel panel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel();
        selectField = new JList(listModel);
        selectField.addListSelectionListener(this);
        selectField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectField.setBackground(new Color(220, 220, 220));
        panel.add(new JScrollPane(selectField), BorderLayout.CENTER);
        panel.add(createButtons(), BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(GAP / 2, GAP / 2, GAP / 2, GAP / 2));
        panel.setPreferredSize(new Dimension(250, 150));
        JPanel all = new JPanel(new BorderLayout());
        all.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.LINE_START);
        all.add(panel, BorderLayout.CENTER);
        return all;
    }

    /**
     * Creates the entry fields component.
     *
     * @return the created entry fields component
     */
    protected JComponent createEntryFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JLabel label1 = new JLabel("Captain", JLabel.TRAILING);
        panel.add(label1);

        captainField = new JTextField();
        captainField.setColumns(20);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(captainField);
        color_captain = new ColorButtonPanel(20, 20);
        color_captain.getColorMenu().addPopupMenuListener(this);
        p.add(color_captain);
        panel.add(p);

        label1 = new JLabel("Prove", JLabel.TRAILING);
        panel.add(label1);
        proveField = new JTextArea(5, 20);
        panel.add(new JScrollPane(proveField));

        JPanel lbpanel = new JPanel();
        lbpanel.setLayout(new BoxLayout(lbpanel, BoxLayout.X_AXIS));
        lbpanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        color_text = new ColorButtonPanel(30, 25);
        color_text.getColorMenu().addPopupMenuListener(this);
        lbpanel.add(color_text);
        lbpanel.add(Box.createHorizontalStrut(5));

        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String envfonts[] = gEnv.getAvailableFontFamilyNames();
        fontfamily = new Vector();
        for (int i = 1; i < envfonts.length; i++)
            fontfamily.addElement(envfonts[i]);
        bfonts = new JComboBox(fontfamily);
        bfonts.setMaximumRowCount(9);
        bfonts.addItemListener(this);
        lbpanel.add(bfonts);
        lbpanel.add(Box.createHorizontalStrut(5));

        bsize = new JComboBox(new Object[]{10, 11, 12, 13, 14, 15, 16, 18, 20, 22, 24, 26, 27, 28, 29, 30, 36, 72});
        bsize.setMaximumRowCount(9);
        bsize.addItemListener(this);
        lbpanel.add(bsize);
        lbpanel.add(Box.createHorizontalStrut(5));
        cbox = new JCheckBox("visible");
        cbox.setSelected(false);
        lbpanel.add(cbox);

        lbpanel.setMaximumSize(lbpanel.getPreferredSize());
        panel.add(lbpanel);
        return panel;
    }

    /**
     * Handles the item state changed event for the combo boxes.
     *
     * @param e the item event
     */
    public void itemStateChanged(ItemEvent e) {
        Object obj = e.getSource();

        if (obj == this.bfonts) {
            String s = (String) bfonts.getSelectedItem();
            if (s.length() == 0) return;
            Font f = cptext.getFont();
            Font tf = new Font(s, f.getStyle(), f.getSize());
            proveField.setFont(tf);
        } else if (obj == this.bsize) {
            int size = ((Integer) bsize.getSelectedItem()).intValue();
            if (size < 10) return;

            Font f = cptext.getFont();
            Font tf = new Font(f.getPSName(), f.getStyle(), size);
            proveField.setFont(tf);
        }
    }

    /**
     * Handles the popup menu will become visible event.
     *
     * @param e the popup menu event
     */
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    }

    /**
     * Handles the popup menu will become invisible event.
     *
     * @param e the popup menu event
     */
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        Object obj = e.getSource();

        if (obj == color_captain.getColorMenu()) {
            Color color = color_captain.setNewColor();
            if (color != null) {
                captainField.setForeground(color);
            }
        } else if (obj == color_text.getColorMenu()) {
            Color c = color_text.setNewColor();
            proveField.setForeground(c);
        }
    }

    /**
     * Handles the popup menu canceled event.
     *
     * @param e the popup menu event
     */
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
}
