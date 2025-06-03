package wprover;

import UI.EntityButtonUI;
import UI.GBevelBorder;
import UI.GifEncoder;
import gprover.Gib;
import gprover.GTerm;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import pdf.PDFJob;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.cli.*;

/**
 * GExpert is the main class for the GEXPERT application.
 * It initializes the application, sets up the GUI, and handles user interactions.
 * It also manages the language settings and file operations.
 */
public class GExpert extends JFrame implements ActionListener, KeyListener, DropTargetListener, WindowListener {    // APPLET ONLY.

    private JLabel label;
    private JLabel label2;
    private JPanel tipanel;
    private Vector vpoolist = new Vector();
    private JToggleButton show_button;


    private static Language language;

    private Group group = new Group();
    private Group menugroup = new Group();

    private JToggleButton buttonMove, buttonSelect;

    public DPanel d;
    public DrawTextProcess dp;
    public CProperty cp;
    public ListTree lp;


    private AnimatePanel aframe;
    private FloatableToolBar afpane;

    private DialogProperty propt;
    private SelectDialog sdialog;
    private UndoEditDialog udialog;
    private CDialogProve pdialog;
    private ConcDialog cdialog;
    private RuleDialog rdialog;
    private NumCheckDialog ndialog;
    private AboutDialog adialog;


    public JScrollPane scroll;
    private CProveBarPanel provePanelbar;
    private CStyleDialog styleDialog;
    private JPopExView rview;
    JToggleButton anButton;
    private MProveInputPanel inputm;

    private PanelProve pprove;
    private JPanel ppanel;
    private JSplitPane contentPane;
    private JFileChooser filechooser;

    private JToggleButton BK1, BK2, BK3, BK4;
    private Vector iconPool = new Vector();
    public String _command;
    public static String lan = null;

    public static I18n i18n;

    public static gprover.Cons conclusion = null; // Temporary fix for storing conclusion with == in GGB import

    /**
     * Constructs a new GExpert instance and initializes the application if it is running as an application.
     */
    public GExpert() {
        super();  //GAPPLET.
        if (CMisc.isApplication())
            init();
    }

    /**
     * Initializes the GExpert application, setting up the GUI components, loading preferences, language settings, and rules.
     * It also initializes various attributes and sets up the main content pane.
     */
    public void init() {
        this.setIconImage(GExpert.createImageIcon("images/gexicon.gif").getImage());    //GAPPLET
        // setLocal();
        // showWelcome();
        CMisc.initFont();

        loadPreference(); //swapped loadPreference() and loadRules() because loadPreference needs to happen first.
        loadLanguage(); // this was after loadrules and loadpreference. real order: loadRules, LoadPreference, loadLanguage
        loadRules();

        initAttribute();
        setLookAndFeel();
        initKeyMap();

        d = new DPanel(this);
        dp = d.dp;
        dp.setCurrentInstance(this);
        dp.setLanguage(language);
        createTipLabel();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        updateTitle();
        scroll = new JScrollPane(d, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.setAutoscrolls(true);
        pprove = new PanelProve(this, d, dp, true, -1);
        inputm = pprove.getmInputPanel();
        ppanel = new JPanel();
        ppanel.setLayout(new BoxLayout(ppanel, BoxLayout.Y_AXIS));
        JToolBar ptoolbar = pprove.createToolBar();
        ppanel.add(ptoolbar);
        ppanel.add(pprove);
        panel.add(scroll);
        ppanel.setBorder(null);
        contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ppanel, panel);
        contentPane.setContinuousLayout(true);
        addSplitListener();

        provePanelbar = new CProveBarPanel(this);
        styleDialog = new CStyleDialog(this, d);
        addMenueToolBar();
        loadCursor();
        new DropTarget(this, this);
        addWindowListener(this);

        this.getContentPane().add(contentPane, BorderLayout.CENTER);
    }

    /**
     * Loads the rules for the GExpert application.
     */
    public void loadRules() {
        RuleList.loadRules();
        Gib.initRules();
    }

    /**
     * Returns the main content pane of the application.
     *
     * @return the main content pane
     */
    public JComponent getContent() {
        return contentPane;
    }

    /**
     * Adds a component listener to the drawing panel to handle resizing and visibility changes.
     */
    public void addSplitListener() {
        d.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                if (provePanelbar != null && provePanelbar.isVisible())
                    provePanelbar.movetoDxy(0, 0);
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    /**
     * Loads the user preferences from the configuration file.
     */
    public void loadPreference() {
        String u = getUserHome();
        try {
            InputStreamReader read = new InputStreamReader(new FileInputStream(u + "/jgex.cfg"), "UTF-8");
            BufferedReader reader = new BufferedReader(read);
            CMisc.LoadProperty(reader);
        } catch (IOException ee) {
            // CMisc.print(ee.getMessage());
        }
    }

    /**
     * Loads the language settings for the application and sets up internationalization.
     */
    public void loadLanguage() {
        language = new Language();
        String user_directory = getUserDir();
        Language.setLanguage(language);
        System.out.println("Language loaded: " + CMisc.lan);
        lan = CMisc.lan; //setting lan to current language so RuleList can read it.

        // Set gettext based internationalization:
        Locale loc = Locale.getDefault();

        if (GExpert.lan.equals("Chinese"))
            loc = Locale.SIMPLIFIED_CHINESE;
        if (GExpert.lan.equals("French"))
            loc = Locale.FRENCH;
        if (GExpert.lan.equals("German"))
            loc = Locale.GERMAN;
        if (GExpert.lan.equals("Hebrew"))
            loc = new Locale("he", "");
        if (GExpert.lan.equals("Hungarian"))
            loc = new Locale("hu", "");
        if (GExpert.lan.equals("Italian"))
            loc = Locale.ITALIAN;
        if (GExpert.lan.equals("Persian"))
            loc = new Locale("fa", "");
        if (GExpert.lan.equals("Polish"))
            loc = new Locale("pl", "");
        if (GExpert.lan.equals("Portuguese"))
            loc = new Locale("pt", "");
        if (GExpert.lan.equals("Serbian"))
            loc = new Locale("rs", "");

        i18n = I18nFactory.getI18n(GExpert.class,
                loc, org.xnap.commons.i18n.I18nFactory.FALLBACK);
        JOptionPane.setDefaultLocale(loc); // this is not required if the next lines are present
        // Some languages may be not supported in the current JDK/JRE, so we use this workaround:
        UIManager.put("OptionPane.yesButtonText", getLanguage("Yes"));
        UIManager.put("OptionPane.noButtonText", getLanguage("No"));
        UIManager.put("OptionPane.cancelButtonText", getLanguage("Cancel"));

        UIManager.put("FileChooser.openDialogTitleText", getLanguage("Open"));
        UIManager.put("FileChooser.lookInLabelText", getLanguage("Look in:"));
        UIManager.put("FileChooser.fileNameLabelText", getLanguage("File name:"));
        UIManager.put("FileChooser.filesOfTypeLabelText", getLanguage("Files of type:"));
        UIManager.put("FileChooser.openButtonText", getLanguage("Open"));
        UIManager.put("FileChooser.cancelButtonText", getLanguage("Cancel"));
        UIManager.put("FileChooser.acceptAllFileFilterText", getLanguage("All Files"));

        UIManager.put("FileChooser.saveDialogTitleText", getLanguage("Save as"));
        UIManager.put("FileChooser.saveInLabelText", getLanguage("Save in:"));
        UIManager.put("FileChooser.saveButtonText", getLanguage("Save"));

        UIManager.put("FileChooser.openButtonToolTipText", getLanguage("Open selected file"));
        UIManager.put("FileChooser.cancelButtonToolTipText", getLanguage("Abort file chooser dialog"));
        UIManager.put("FileChooser.upFolderToolTipText", getLanguage("Up One Level"));
        UIManager.put("FileChooser.homeFolderToolTipText", getLanguage("Home"));
        UIManager.put("FileChooser.newFolderToolTipText", getLanguage("Create New Folder"));
        UIManager.put("FileChooser.listViewButtonToolTipText", getLanguage("List"));
        UIManager.put("FileChooser.newFolderButtonText", getLanguage("Create New Folder"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", getLanguage("Details"));
        UIManager.put("FileChooser.fileSizeHeaderText", getLanguage("Size"));
        UIManager.put("FileChooser.fileDateHeaderText", getLanguage("Date Modified"));

        // There are still some keys that require translation. TODO.
        // This piece of code may help finding the missing keys.
        /*
        UIDefaults defaults = UIManager.getDefaults();
        java.util.Enumeration<Object> keysEnumeration = defaults.keys();
        java.util.ArrayList<Object> keysList = java.util.Collections.list(keysEnumeration);
        for (Object key : keysList) {
            if (key.toString().contains("FileChooser"))
                System.out.println(key);
            }
         */
    }


    /**
     * Initializes the attributes of the application, setting the font if the language is not English.
     */
    public void initAttribute() {
        if (language != null && !language.isEnglish()) {
            Font f = language.getFont();
            if (f != null) {
                setUIFont(new FontUIResource(f));
                CMisc.setFont(f.getName());
            }
        }
    }

    /**
     * Returns the default font of the application.
     *
     * @return the default Font object, or null if the language is not set
     */
    public Font getDefaultFont() {
        if (language == null)
            return null;
        return language.getFont();
    }

    /**
     * Returns the Frame object of the application.
     *
     * @return the Frame object, or null if no Frame is found
     */
    public Frame getFrame() {
        Container c = this;
        while (c != null) {
            if (c instanceof Frame)
                return (Frame) c;
            c = c.getParent();
        }
        return (Frame) null;
    }

    /**
     * Loads the cursor for the application.
     */
    public void loadCursor() {
    }

    /**
     * Checks if the panel is visible.
     *
     * @return true if the panel is visible, false otherwise
     */
    public boolean isPPanelVisible() {
        return ppanel.isVisible();
    }

    /**
     * Shows or hides the panel.
     *
     * @param t if true, hides the panel; if false, shows the panel
     */
    public void showppanel(boolean t) {
        show_button.setSelected(t);
        ppanel.setVisible(!t);
        contentPane.resetToPreferredSizes();
    }

    /**
     * Shows the rule panel at the specified location.
     *
     * @param s the rule to be shown
     * @param x the x-coordinate of the location
     * @param y the y-coordinate of the location
     */
    public void showRulePanel(String s, int x, int y) {
        if (rview == null) {
            rview = new JPopExView(this);
        }
        if (rview.loadRule(s)) {
            rview.setLocationRelativeTo(d);
            rview.setLocation(x, y);
            rview.setVisible(true);
        }
    }

    /**
     * Returns the PanelProve object.
     *
     * @return the PanelProve object
     */
    public PanelProve getpprove() {
        return pprove;
    }

    /**
     * Checks if the animate frame is present.
     *
     * @return true if the animate frame is present, false otherwise
     */
    public boolean hasAFrame() {
        return aframe != null;
    }

    /**
     * Returns the AnimatePanel object, initializing it if necessary.
     *
     * @return the AnimatePanel object
     */
    public AnimatePanel getAnimateDialog() {
        if (aframe == null) {
            aframe = new AnimatePanel(this, d, dp);
        }
        return aframe;
    }

    /**
     * Shows the animate pane.
     */
    public void showAnimatePane() {
        if (aframe == null)
            this.getAnimateDialog();

        Rectangle rc = scroll.getVisibleRect();
        if (afpane == null)
            afpane = new FloatableToolBar();
        aframe.setEnableAll();
        afpane.add(aframe);

        Dimension dm = afpane.getPreferredSize();
        int w = (int) dm.getWidth();
        int h = (int) dm.getHeight();

        afpane.show(d, (int) rc.getWidth() - w, (int) rc.getHeight() - h);
        aframe.repaint();
    }

    /**
     * Returns the RuleDialog object for the specified rule, initializing it if necessary.
     *
     * @param n the rule number
     * @return the RuleDialog object
     */
    public RuleDialog getRuleDialog(int n) {
        if (rdialog == null) {
            rdialog = new RuleDialog(this);
            int w = rdialog.getWidth();
            int x = getX() - w;
            if (x < 0) x = 0;
            rdialog.setLocation(x, getY());
        }
        rdialog.setSelected(n);
        rdialog.setVisible(true);
        return rdialog;
    }


    /**
     * Automatically sets the panel type for the given class.
     *
     * @param c the class for which the panel type is to be set
     */
    public void viewElementsAuto(CClass c) {
        if (c == null)
            return;
        if (propt == null || cp == null)
            return;
        cp.SetPanelType(c);
    }

    /**
     * Returns the dialog property, initializing it if necessary.
     *
     * @return the dialog property
     */
    public DialogProperty getDialogProperty() {
        if (propt == null) {
            cp = new CProperty(d, language);
            propt = new DialogProperty(this, cp);
            propt.getContentPane().add(cp);
            propt.setVisible(false);
            propt.setTitle(getLanguage("Properties"));
            centerDialog(propt);
        }
        return propt;
    }

    /**
     * Centers the given dialog relative to the main frame.
     *
     * @param dlg the dialog to be centered
     */
    public void centerDialog(JDialog dlg) {
        dlg.setLocation(this.getX() + this.getWidth() / 2 - dlg.getWidth() / 2,
                this.getY() + this.getHeight() / 2 -
                        dlg.getHeight() / 2);
    }

    /**
     * Returns the select dialog, initializing it if necessary.
     *
     * @return the select dialog
     */
    public SelectDialog getSelectDialog() {
        if (sdialog == null) {
            sdialog = new SelectDialog(this, new Vector());
        }
        return sdialog;
    }

    /**
     * Checks if the conclusion dialog is visible.
     *
     * @return true if the conclusion dialog is visible, false otherwise
     */
    public boolean isconcVisible() {
        if (cdialog != null && cdialog.isVisible()) {
            return true;
        }
        return false;
    }

    /**
     * Returns the conclusion dialog, initializing it if necessary.
     *
     * @return the conclusion dialog
     */
    public ConcDialog getConcDialog() {
        if (cdialog == null) {
            cdialog = new ConcDialog(this, "");
            centerDialog(cdialog);
        }
        return cdialog;
    }

    /**
     * Shows the number check dialog if there are points to check.
     */
    public void showNumDialog() {
        if (ndialog != null && ndialog.isVisible())
            return;
        if (dp.getPointSize() == 0)
            return;
        ndialog = new NumCheckDialog(this);
        this.centerDialog(ndialog);
        ndialog.setVisible(true);
    }

    /**
     * Selects a point in the number check dialog if it is visible.
     *
     * @param p the point to be selected
     */
    public void selectAPoint(CPoint p) {
        if (ndialog != null && ndialog.isVisible())
            ndialog.addSelectPoint(p);
    }

    /**
     * Returns the undo edit dialog, initializing it if necessary.
     *
     * @return the undo edit dialog
     */
    public UndoEditDialog getUndoEditDialog() {
        if (udialog == null) {
            udialog = new UndoEditDialog(this);
            this.lp = udialog.getTreePanel();
        }
        return udialog;
    }

    /**
     * Checks if the prove dialog is visible.
     *
     * @return true if the prove dialog is visible, false otherwise
     */
    public boolean isDialogProveVisible() {
        return pdialog != null && pdialog.isVisible();
    }

    /**
     * Returns the prove dialog, initializing it if necessary.
     *
     * @return the prove dialog
     */
    public CDialogProve getDialogProve() {
        if (pdialog == null) {
            pdialog = new CDialogProve(this);
        }
        return pdialog;
    }

    /**
     * Returns a file chooser, initializing it if necessary.
     *
     * @param importGgb if true, sets the file filter to GeoGebra files; otherwise, sets it to GEX files
     * @return the file chooser
     */
    public JFileChooser getFileChooser(boolean importGgb) {
        if (filechooser == null) {
            filechooser = new JFileChooser();
            String dr = getUserDir();
            filechooser.setCurrentDirectory(new File(dr));
        }
        if (importGgb) {
            filechooser.setFileFilter(new JFileFilter("ggb"));
        } else {
            filechooser.setFileFilter(new JFileFilter("gex"));
        }
        filechooser.setSelectedFile(null);
        filechooser.setSelectedFiles(null);
        return filechooser;
    }

    /**
     * Returns the user directory.
     *
     * @return the user directory
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    /**
     * Returns the user home directory.
     *
     * @return the user home directory
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * Returns the file separator.
     *
     * @return the file separator
     */
    public static String getFileSeparator() {
        return File.separator;
    }

    /**
     * Checks if the manual input bar is present.
     *
     * @return true if the manual input bar is present, false otherwise
     */
    public boolean hasMannualInputBar() {
        return inputm != null;
    }

    /**
     * Returns the prove status.
     *
     * @return the prove status
     */
    public int getProveStatus() {
        return pprove.getSelectedIndex();
    }

    /**
     * Returns the style dialog.
     *
     * @return the style dialog
     */
    public CStyleDialog getStyleDialog() {
        return styleDialog;
    }

    /**
     * Returns the manual input toolbar.
     *
     * @return the manual input toolbar
     */
    public MProveInputPanel getMannalInputToolBar() {
        return inputm;
    }

    /**
     * Adds a button to the draw group.
     *
     * @param b the button to be added
     */
    public void addButtonToDrawGroup(JToggleButton b) {
        group.add(b);
    }

    /**
     * Switches the visibility of the prove bar.
     *
     * @param r if false, hides the prove bar; if true, shows the prove bar
     */
    public void switchProveBarVisibility(boolean r) {
        if (r == false) {
            if (provePanelbar == null)
                return;
            showProveBar(false);
        } else
            showProveBar(true);
    }

    /**
     * Shows or hides the prove bar.
     *
     * @param show if true, shows the prove bar; if false, hides the prove bar
     */
    public void showProveBar(boolean show) {
        if (provePanelbar == null) {
            provePanelbar = new CProveBarPanel(this);
        }
        if (show) {
            Dimension dm = provePanelbar.getPreferredSize();
            int w = (int) dm.getWidth();
            int h = (int) dm.getHeight();
            Rectangle rc = scroll.getVisibleRect();
            provePanelbar.show(d, 0, (int) rc.getHeight() - h);
            provePanelbar.repaint();

            provePanelbar.setValue(-1);
        } else {
            provePanelbar.setVisible(false);
        }
    }

    /**
     * Shows the style dialog.
     */
    public void showStyleDialog() {
        if (styleDialog == null) {
            styleDialog = new CStyleDialog(this, d);
        }
        if (true) {
            Dimension dm = styleDialog.getPreferredSize();
            int w = (int) dm.getWidth();
            int h = (int) dm.getHeight();
            Rectangle rc = scroll.getVisibleRect();
            styleDialog.show(d, 0, 0);
            styleDialog.repaint();
        } else {
            styleDialog.setVisible(false);
        }
    }

    /**
     * Creates and initializes the tip label.
     */
    public void createTipLabel() {

        label = new JLabel() {
            public Dimension getPreferredSize() {
                Dimension dm = new Dimension(210, 20);
                return dm;
            }

            public Dimension getMaximumSize() {
                Dimension dm = new Dimension(500, 20);
                return dm;
            }
        };

        label2 = new JLabel("") {
            public Dimension getMaximumSize() {
                Dimension dm = new Dimension(Integer.MAX_VALUE, 20);
                return dm;
            }
        };
        Font f = CMisc.button_label_font;
        label2.setFont(f);
        label.setFont(f);

        GBevelBorder border = new GBevelBorder(GBevelBorder.RAISED, 1);

        label.setBorder(border);
        label2.setBorder(border);

        JPanel panel = new JPanel();
        panel.setBorder(null);
        panel.setBackground(CMisc.frameColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        tipanel = panel;

        show_button = new TStateButton(GExpert.createImageIcon("images/ticon/show.gif"),
                GExpert.createImageIcon("images/ticon/hide.gif"));

        show_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JToggleButton b = (JToggleButton) e.getSource();
                showppanel(b.isSelected());
            }
        });

        EntityButtonUI ui = new EntityButtonUI();
        show_button.setUI(ui);
        panel.add(show_button);
        panel.add(label);
        panel.add(label2);
        panel.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
        getContentPane().add("South", panel);

    }

    /**
     * Sets the location of the frame.
     *
     * @param x the x-coordinate of the frame
     * @param y the y-coordinate of the frame
     */
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
    }

    /**
     * Adds all example files from the examples directory to the specified menu.
     *
     * @param menu the menu to which the example files will be added
     */
    void addAllExamples(JMenu menu) {
        addDirectory(menu, "docs/examples");
    }

    /**
     * Populate a JMenu by scanning a folder on the classpath.
     * @param menu        the menu to fill
     * @param resourceDir the resource path (e.g. "docs/examples")
     */
    void addDirectory(JMenu menu, String resourceDir) {
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
                                // first collect all entries to process
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
                                            filesToProcess.add(relativePath);
                                        } else {
                                            // subdirectory
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

                                // process all files in this directory
                                for (String fileName : filesToProcess) {
                                    handleEntry(menu, resourceDir, fileName, false);
                                }
                            }
                        } else {
                            // fallback to JarInputStream if jar-file protocol not available
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
                    // log the exception but continue processing
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
            // store the resource path for later loading
            item.setName(base + "/" + name);
            item.setToolTipText(name);
            item.setActionCommand("example");
            addMenu(menu, item);
        }
    }

    /**
     * Adds the contents of the specified directory to the specified menu.
     *
     * @param f the directory whose contents will be added
     * @param menu the menu to which the contents will be added
     * @param path the path of the directory
     */
    void addDirectory(File f, JMenu menu, String path) {

        String sp = GExpert.getFileSeparator();

        if (f.isDirectory()) {
            File contents[] = f.listFiles();
            Arrays.sort(contents, new Comparator<File>() {
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
            int n = contents.length - 1;
            for (int i = n; i >= 0; i--) {
                if (contents[i].isDirectory()) {
                    continue;
                }
                String s = contents[i].getName();
                String t = s;

                if (s.endsWith(".gex")) {
                    int size = s.length();
                    t = s.substring(0, size - 4);
                }
                JMenuItem mt = new JMenuItem(t);
                mt.setToolTipText(s);
                mt.setName(path);
                mt.setActionCommand("example");
                mt.addActionListener(this);
                addMenu(menu, mt);
            }

            for (int i = 0; i < contents.length; i++) {
                String s = contents[i].getName();
                String t = s;
                if (contents[i].isDirectory()) {
                    JMenu m = new JMenu(s);
                    this.addDirectory(contents[i], m, path + sp + s);
                    menu.add(m);
                }
            }

        }

    }

    /**
     * Adds a menu item to the specified menu in alphabetical order.
     *
     * @param menu the menu to which the item will be added
     * @param item the menu item to be added
     */
    void addMenu(JMenu menu, JMenuItem item) {
        String name = item.getText();
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem m = (JMenuItem) menu.getItem(i);
            if (m == null) {
                continue;
            }
            if (name.compareTo(m.getText()) < 0) {
                menu.add(item, i);
                return;
            }
        }
        menu.add(item);
    }

    /**
     * Initializes and adds the menu and tool bar(s) to the application frame.
     * <p>
     * This method creates and configures the top toolbar, the right-side toolbar,
     * and the menu bar with all necessary menus and menu items for file operations,
     * examples, construction, constraints, actions, and help.
     * Toolbar buttons and menu items are set up with icons and action listeners.
     * </p>
     */
    public void addMenueToolBar() {
        JToolBar toolBar = new JToolBar("Toolbar");
        toolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        toolBar.setBackground(CMisc.frameColor);
        toolBar.setOpaque(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        addButtons(toolBar);

        toolBar.setFloatable(false);
        JToolBar toolBarRight = new JToolBar("Toolbar", JToolBar.VERTICAL);
        toolBarRight.setBorder(new GBevelBorder(GBevelBorder.RAISED, 1));//(EtchedBorder.LOWERED));
        toolBarRight.setBackground(CMisc.frameColor);
        addRightButtons(toolBarRight);
        toolBarRight.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu;

        menu = new JMenu(getLanguage("File"));
        menuBar.add(menu);

        JMenuItem item = addAMenu(menu, "New", null, 'N', this);
        addImageToItem(item, "new");
        menu.addSeparator();

        item = addAMenu(menu, "Open", null, 'O', this);
        addImageToItem(item, "open");
        item = addAMenu(menu, "Import", null, this);
        menu.addSeparator();

        item = addAMenu(menu, "Save", null, 'S', this);
        addImageToItem(item, "save");
        JMenuItem item1 = addAMenu(menu, "Save as...", "null", this);
        KeyStroke ctrlP = KeyStroke.getKeyStroke('S', InputEvent.ALT_MASK);
        item1.setAccelerator(ctrlP);
        addImageToItem(item1);
//        addImageToItem(item1, "saveas");
        item = addAMenu(menu, "Save as Text", null, 'T', this);
        addImageToItem(item);

        item = addAMenu(menu, "Save as PS", null, this);
        addImageToItem(item);
        item = addAMenu(menu, "Save as PDF", null, this);
        addImageToItem(item);

        item = addAMenu(menu, "Save as Image", null, this);
        addImageToItem(item, "image");
        item = addAMenu(menu, "Save as Animated Image", null, this);
        addImageToItem(item);
        item = addAMenu(menu, "Save Proof as Animated Image", null, this);
        addImageToItem(item);
        item = addAMenu(menu, "Save GDD Proof as GraphViz File", null, this);
        addImageToItem(item);

        menu.addSeparator();
        item = addAMenu(menu, "Print", "Print the client area", 'P', this);
        addImageToItem(item, "print");
        menu.addSeparator();
        item = addAMenu(menu, "Exit", "Exit", 'X', this);
        addImageToItem(item);


        menu = new JMenu(getLanguage("Examples"));
        menuBar.add(menu);

        addAllExamples(menu);

        menu = new JMenu(getLanguage("Construct"));
        menuBar.add(menu);
        addRadioButtonMenuItem(menu, "Intersection by compass and circle/line", "You need 3 points and a circle/ segment/ line. Select the distance AB, click into the center C and and then select the intersecting point D with a circle/ segment/ line. AB = CD.", this);
        addRadioButtonMenuItem(menu, "Radical of Two Circles", "Click two circles to construct their radical axis", this);
        menu.addSeparator();

        addRadioButtonMenuItem(menu, "Oriented Segment", "Click two points A, B, then a point C to get AB //= CD", this);
        JRadioButtonMenuItem it = addRadioButtonMenuItem(menu, "Oriented T Segment", "Click two points A, B, then point C to get CD equal and perpendicular to +- AB", this, "o_t_segment");

        JMenu s1 = new JMenu(getLanguage("Oriented Segment * Ratio"));

        addRadioButtonMenuItem(s1, "1 : 2", null, this, "Oriented Segment");
        addRadioButtonMenuItem(s1, "2 : 1", null, this, "Oriented Segment");
        addRadioButtonMenuItem(s1, "Other...", "Input your own ratio", this, "Oriented Segment");
        menu.add(s1);

        JMenu s2 = new JMenu(getLanguage("Oriented T Segment * Ratio"));
        addRadioButtonMenuItem(s2, "1 : 2", null, this, "o_t_segment");
        addRadioButtonMenuItem(s2, "2 : 1", null, this, "o_t_segment");
        addRadioButtonMenuItem(s2, "Other...", "Input your own ratio", this, "o_t_segment");
        menu.add(s2);

        menu.addSeparator();
        JMenu sub = new JMenu(getLanguage("Proportional Segment"));
//        addRadioButtonMenuItem(sub, "1 : -1", "Click two points to get a point with ratio 1:1", this, "propline");
        addRadioButtonMenuItem(sub, "1 : 1", getTranslationViaGettext("Click two points to get a point with ratio {0}", "1:1"), this, "propline");
        addRadioButtonMenuItem(sub, "1 : 2", getTranslationViaGettext("Click two points to get a point with ratio {0}", "1:2"), this, "propline");
        addRadioButtonMenuItem(sub, "1 : 3", getTranslationViaGettext("Click two points to get a point with ratio {0}", "1:3"), this, "propline");
        addRadioButtonMenuItem(sub, "1 : 4", getTranslationViaGettext("Click two points to get a point with ratio {0}", "1:4"), this, "propline");
        addRadioButtonMenuItem(sub, "1 : 5", getTranslationViaGettext("Click two points to get a point with ratio {0}", "1:5"), this, "propline");
        // addRadioButtonMenuItem(sub, "1 : 2", "Click two points to get a point with ratio 1:2", this, "propline");
        addRadioButtonMenuItem(sub, "Other...", "Input your own ratio", this, "propline");
        menu.add(sub);
        menu.addSeparator();
        sub = new JMenu(getLanguage("Point"));
        addRadioButtonMenuItem(sub, "Point", "Add a single point", this);
        addRadioButtonMenuItem(sub, "Midpoint", "Click two points to get their midpoint", this);
        sub.addSeparator();
        addRadioButtonMenuItem(sub, "Circumcenter", "Construct the circumcenter by selecting three points", this);
        addRadioButtonMenuItem(sub, "Centroid", "Construct the centroid by clicking three points", this);
        addRadioButtonMenuItem(sub, "Orthocenter", "Construct the orthocenter by clicking three points", this);
        addRadioButtonMenuItem(sub, "Incenter", "Construct the incenter by clicking three points", this);
        sub.addSeparator();
        addRadioButtonMenuItem(sub, "Foot", "Click a point then drag to a line to construct the foot", this);
        menu.add(sub);

        sub = new JMenu(getLanguage("Line"));
        addRadioButtonMenuItem(sub, "Line", "Draw a line by connecting two points", this);
        addRadioButtonMenuItem(sub, "Parallel", "Parallel Line", "Draw a line which is parallel to another line", this);
        addRadioButtonMenuItem(sub, "Perpendicular", "Perpendicular Line", "Draw a line which is perpendicular to another line", this);
        addRadioButtonMenuItem(sub, "Angle Bisector", "Draw an angle bisector", this);
        addRadioButtonMenuItem(sub, "Aline", "Draw Aline", this);
        addRadioButtonMenuItem(sub, "Bline", "Perp-Bisect Line", "Draw a line which is perp-bisect to another line", this);
        addRadioButtonMenuItem(sub, "TCline", "Tangent Line", "Draw line which is tangent to a circle", this);
        menu.add(sub);

        sub = new JMenu(getLanguage("Circle"));
        addRadioButtonMenuItem(sub, "Circle", "Draw a circle by a center point and a point on circle", this);
        addRadioButtonMenuItem(sub, "Circle by Three Points", "Circle by Three Points", "Draw a circle by three points", this);
        addRadioButtonMenuItem(sub, "Compass", "Circle by Radius", "Draw a circle with center and radius", this);
        menu.add(sub);

        sub = new JMenu(getLanguage("Action"));
        addRadioButtonMenuItem(sub, "Intersect", "Intersect to decide a point", this);
        addRadioButtonMenuItem(sub, "Mirror", "Mirror an element with respect to a line or a point", this);
        menu.add(sub);
        sub = new JMenu(getLanguage("Polygon"));
        addRadioButtonMenuItem(sub, "Triangle", "Draw a triangle", this, "triangle");
        addRadioButtonMenuItem(sub, "Isosceles Triangle", "Draw an isosceles triangle", this, "isosceles triangle");
        addRadioButtonMenuItem(sub, "Equilateral Triangle", "Draw an equilateral triangle", this, "equilateral triangle");
        addRadioButtonMenuItem(sub, "Right-angled Triangle", "Draw a right-angled triangle", this, "Tri_perp");
        addRadioButtonMenuItem(sub, "Isosceles Right-angled Triangle", "Draw an isosceles right-angled triangle", this, "Tri_sq_iso");
        addRadioButtonMenuItem(sub, "Quadrangle", "Draw a quadrangle", this, "quadrangle");
        addRadioButtonMenuItem(sub, "Parallelogram", "Draw a parallelogram", this, "parallelogram");
        addRadioButtonMenuItem(sub, "Trapezoid", "Draw a trapezoid", this, "trapezoid");
        addRadioButtonMenuItem(sub, "Right-angled Trapezoid", "Draw a right angle trapezoid", this, "ra_trapezoid");
        addRadioButtonMenuItem(sub, "Rectangle", "Draw a rectangle", this, "rectangle");
        addRadioButtonMenuItem(sub, "Square", "Draw a square", this, "square");
        addRadioButtonMenuItem(sub, "Pentagon", "Draw a pentagon", this, "pentagon");
        addRadioButtonMenuItem(sub, "Polygon", "Draw a polygon", this, "polygon");
        menu.add(sub);
        sub = new JMenu(getLanguage("Special Angles"));
        String[] angles = {"15", "30", "45", "60", "75", "90", "115", "120"};
        for (String angle : angles) {
            addRadioButtonMenuItem(sub, angle, getTranslationViaGettext("Draw an angle of {0} degrees", angle), this, "sangle");
        }

        // addRadioButtonMenuItem(sub, "30", "Draw an angle of 30 degree", this, "sangle");
        addRadioButtonMenuItem(sub, "Other...", "Draw an angle of other degrees", this, "sangle");
        menu.add(sub);
//        menu.addSeparator();


        menu = new JMenu(getLanguage("Constraint"));
        addRadioButtonMenuItem(menu, "Eqangle", "Set two angles equal", this);
//        addRadioButtonMenuItem(menu, "Nteqangle", "Draw line with two angles equal", this);
        addRadioButtonMenuItem(menu, "Eqangle3p", "Set the sum of three angles equal to one", this);
        addRadioButtonMenuItem(menu, "Angle Specification", "Set specific angles of system", this);
        menu.addSeparator();

        addRadioButtonMenuItem(menu, "Equal Ratio", "Select four segments to set their equal ratio", this);

        addRadioButtonMenuItem(menu, "Equal Distance", "Set two segments to be equal", this, "Equal Distance");

        JMenu sub2 = new JMenu(getLanguage(getLanguage("Ratio Distance")));

        // addRadioButtonMenuItem(sub2, "1 : 1", "Set two segments to have ratio: 1 : 1", this, "ra_side");
        addRadioButtonMenuItem(sub2, "1 : 1", GExpert.getTranslationViaGettext("Set two segments to have ratio: {0}", "1 : 1"), this, "ra_side");
        addRadioButtonMenuItem(sub2, "1 : 2", GExpert.getTranslationViaGettext("Set two segments to have ratio: {0}", "1 : 2"), this, "ra_side");
        addRadioButtonMenuItem(sub2, "1 : 3", GExpert.getTranslationViaGettext("Set two segments to have ratio: {0}", "1 : 3"), this, "ra_side");
        addRadioButtonMenuItem(sub2, "Other...", "Set two segments to have specified ratio", this, "ra_side");
        menu.add(sub2);
        addRadioButtonMenuItem(menu, "CCtangent", "Set two circles to be tangent", this);
        menuBar.add(menu);
        menu = new JMenu(getLanguage("Action"));
        menuBar.add(menu);
        addRadioButtonMenuItem(menu, "Trace", "Select a point to trace its locus (in combination with move or animation)", this);
        addRadioButtonMenuItem(menu, "Locus", "The locus of a point", this);
        addRadioButtonMenuItem(menu, "Animation", "Click a point then an object to animate", this);
        menu.addSeparator();

        addRadioButtonMenuItem(menu, "Fill Polygon", "select a closed segment path to fill the polygon", this);
        addRadioButtonMenuItem(menu, "Measure Distance", "Select two angle to set equal", this);
        addRadioButtonMenuItem(menu, "Arrow", "Select two points to construct an arrow", this);


        JMenu sub1 = new JMenu(getLanguage("Equal Mark"));
        addRadioButtonMenuItem(sub1, "1", "Mark for equal with one line", this, "eqmark");
        addRadioButtonMenuItem(sub1, "2", "Mark for equal with two lines", this, "eqmark");
        addRadioButtonMenuItem(sub1, "3", "Mark for equal with three lines", this, "eqmark");
        addRadioButtonMenuItem(sub1, "4", "Mark for equal with four lines", this, "eqmark");
        menu.add(sub1);
        addRadioButtonMenuItem(menu, "Right-angle Mark", "Draw a right angle mark", this, "RAMark");
        addRadioButtonMenuItem(menu, "Calculation", "Calculation", this, "Calculation");
        menuBar.add(menu);
        menu.addSeparator();
        addRadioButtonMenuItem(menu, "Hide Object", "Hide objects", this);
        addRadioButtonMenuItem(menu, "Show Object", "Show objects that is hiden", this);
        menu.addSeparator();
        addRadioButtonMenuItem(menu, "Transform", "Transform polygon", this);
        addRadioButtonMenuItem(menu, "Equivalence", "Equivalence transform polygon", this);
        addRadioButtonMenuItem(menu, "Free Transform", "Transform polygon freely", this);


        menu = pprove.getProveMenu();
//        item = addAMenu(menu, "All Solutions", "All Solutions", this);
//        addImageToItem(item);
        menuBar.add(menu);

        menu = new JMenu(getLanguage("Lemmas"));
        menuBar.add(menu);
        addAMenu(menu, "Rules for Full Angle", "Rules for Full Angle", this);
        addAMenu(menu, "Rules for GDD", "Rules for GDD", this);


        menu = new JMenu(getLanguage("Options"));
        menuBar.add(menu);
        item = addAMenu(menu, "Preferences", "Set the default property", this);
        addImageToItem(item, "preference");

        item = addAMenu(menu, "Construct History", "Edit construct history", this);
        addImageToItem(item);
        item = addAMenu(menu, "Show Step Bar", "Show Step Bar for prove", this);
        addImageToItem(item, "step");
        item = addAMenu(menu, "Style Dialog", "Show Draw Style Dialog", this);
        addImageToItem(item);

        menu = new JMenu(getLanguage("Help"));
        item = addAMenu(menu, "Help", "Help", KeyEvent.VK_F1, this);
        item.setAccelerator(KeyStroke.getKeyStroke("F1"));

        // this.addImageToItem(item, "help");
        // item = addAMenu(menu, "Online Help", "Online Help", this);
        // addImageToItem(item);

        item = addAMenu(menu, "Help on Mode", "Help on Mode", this);
        addImageToItem(item);

        item = addAMenu(menu, "JGEX Homepage", "JGEX Homepage", this);
        addImageToItem(item);
        item = addAMenu(menu, "Contact Us", "Contact Us", this);
        addImageToItem(item);
        menu.addSeparator();
        item = addAMenu(menu, "Check for Update", "Check for Update", this);
        addImageToItem(item);
        item = addAMenu(menu, "About JGEX", "About Java Geometry Expert", this);
        addImageToItem(item, "infor");

        menuBar.add(menu);
        toolBarRight.add(Box.createVerticalBox());

        getContentPane().add(toolBar, BorderLayout.PAGE_START);
        getContentPane().add(toolBarRight, BorderLayout.EAST);
    }

    /**
     * Creates a JRadioButtonMenuItem with the specified name, tooltip, action listener, and command.
     *
     * @param bar the menu to add the item to
     * @param name the display name and action command of the menu item
     * @param tooltip the tooltip text to display
     * @param listener the action listener to register on the menu item
     * @param command the specific action command to set
     * @return the created JRadioButtonMenuItem
     */
    public JRadioButtonMenuItem addRadioButtonMenuItem(JMenu bar, String name,
                                                       String tooltip, ActionListener listener, String command) {
        JRadioButtonMenuItem item = addRadioButtonMenuItem(bar, name, tooltip, listener);
        item.setActionCommand(command);
        return item;
    }

    /**
     * Creates a JRadioButtonMenuItem with the specified name, custom text, tooltip, and action listener.
     * The menu item's text is set to its localized value.
     *
     * @param bar the menu to add the item to
     * @param name the internal name of the menu item used as its action command
     * @param text the text to display on the menu item (to be localized)
     * @param tooltip the tooltip text to display
     * @param listener the action listener to register on the menu item
     * @return the created JRadioButtonMenuItem with localized text
     */
    public JRadioButtonMenuItem addRadioButtonMenuItem(JMenu bar, String name, String text,
                                                       String tooltip, ActionListener listener) {
        JRadioButtonMenuItem item = addRadioButtonMenuItem(bar, name, tooltip, listener);
        item.setText(getLanguage(text));
        return item;
    }

    /**
     * Creates a JRadioButtonMenuItem with the specified name, tooltip, and action listener.
     * The menu item's text and action command are set based on the provided name.
     * If the tooltip is null, a localized language tip may be used instead.
     *
     * @param bar the menu to add the item to
     * @param name the name used as the action command and display text of the menu item
     * @param tooltip the tooltip text to display (or a localized tip if null)
     * @param listener the action listener to register on the menu item
     * @return the created JRadioButtonMenuItem
     */
    public JRadioButtonMenuItem addRadioButtonMenuItem(JMenu bar, String name,
                                                       String tooltip, ActionListener listener) {
        JRadioButtonMenuItem miten;
        miten = new JRadioButtonMenuItem(name);

        miten.setActionCommand(name);
        miten.setText(getLanguage(name));
        String s1 = getLanguageTip(name);

        if (tooltip != null)
            miten.setToolTipText(tooltip);
        else if (s1 != null && s1.length() > 0)
            miten.setToolTipText(s1);

        miten.addActionListener(listener);
        miten.setActionCommand(name);
        bar.add(miten);
        menugroup.add(miten);
        return miten;
    }

    /**
     * Adds a menu item to the specified menu bar with the given name, tooltip, mnemonic, and action listener.
     *
     * @param bar the menu bar to which the menu item will be added
     * @param name the name of the menu item
     * @param tooltip the tooltip text for the menu item
     * @param ne the mnemonic for the menu item
     * @param listener the action listener for the menu item
     * @return the created JMenuItem
     */
    public JMenuItem addAMenu(JMenu bar, String name, String tooltip, int ne, ActionListener listener) {
        JMenuItem item = addAMenu(bar, name, tooltip, listener);
        item.setMnemonic(ne);
        KeyStroke ctrlP = KeyStroke.getKeyStroke(ne, InputEvent.CTRL_MASK);
        item.setAccelerator(ctrlP);
        return item;
    }

    /**
     * Adds a blank image icon to the specified menu item.
     *
     * @param item the menu item to which the image icon will be added
     */
    public void addImageToItem(JMenuItem item) {
        ImageIcon m = GExpert.createImageIcon("images/small/" + "blank.gif");
        item.setIcon(m);
    }

    /**
     * Adds a blank image icon to the specified menu.
     *
     * @param item the menu to which the image icon will be added
     */
    public void addImageToItem(JMenu item) {
        ImageIcon m = GExpert.createImageIcon("images/small/" + "blank.gif");
        item.setIcon(m);
    }

    /**
     * Adds an image icon to the specified menu item with the given name.
     *
     * @param item the menu item to which the image icon will be added
     * @param name the name of the image icon
     */
    public void addImageToItem(JMenuItem item, String name) {
        ImageIcon m = GExpert.createImageIcon("images/small/" + name + ".gif");
        if (m == null)
            m = GExpert.createImageIcon("images/small/" + "blank.gif");
        item.setIcon(m);
    }

    /**
     * Adds a menu item to the specified menu bar with the given name, tooltip, and action listener.
     *
     * @param bar the menu bar to which the menu item will be added
     * @param name the name of the menu item
     * @param tooltip the tooltip text for the menu item
     * @param listener the action listener for the menu item
     * @return the created JMenuItem
     */
    public JMenuItem addAMenu(JMenu bar, String name, String tooltip, ActionListener listener) {
        JMenuItem miten;
        miten = new JMenuItem(name);
        if (tooltip != null) {
            miten.setToolTipText(this.getLanguage(tooltip));
        }
        miten.setActionCommand(name);
        miten.setText(this.getLanguage(name));
        miten.addActionListener(listener);
        bar.add(miten);
        return miten;
    }

    /**
     * Returns the current language settings.
     *
     * @return the current Language object
     */
    public Language getLan() {
        return language;
    }

    /**
     * Returns the translated string for the given key.
     *
     * @param s1 the key to be translated
     * @return the translated string
     */
    public static String getLanguage(String s1) {
        s1 = getTranslationViaGettext(s1);
        if (s1 != null) {
            return s1;
        }

        String s2 = language.getString(s1);
        if (s2 == null || s2.length() == 0)
            return s1;
        return s2;
    }

    /**
     * Returns the tooltip text for the given key.
     *
     * @param s1 the key to be translated
     * @return the translated tooltip text
     */
    public static String getLanguageTip(String s1) {
        s1 = getTranslationViaGettext(s1);
        if (s1 != null) {
            return s1;
        }

        System.err.println("Missing translation for " + s1);

        if (language == null)
            return s1;

        String s2 = language.getString1(s1);
        return s2;
    }

    /**
     * Returns the translated string for the given key using gettext.
     *
     * @param s the key to be translated
     * @return the translated string
     */
    public static String getTranslationViaGettext(String s) {
        return getTranslationViaGettext(s, (String) null);
    }

    /**
     * Returns the translated string for the given key using gettext with parameters.
     *
     * @param s the key to be translated
     * @param p the parameters for the translation
     * @return the translated string
     */
    public static String getTranslationViaGettext(String s, String... p) {
        String gettextTranslation = null;
        try {
            if (p == null) {
                gettextTranslation = i18n.tr(s);
            } else {
                if (p.length == 1) {
                    if (p[0] != null)
                        gettextTranslation = i18n.tr(s, p[0]);
                    else
                        gettextTranslation = i18n.tr(s);
                }
                if (p.length == 2)
                    gettextTranslation = i18n.tr(s, p[0], p[1]);
                if (p.length == 3)
                    gettextTranslation = i18n.tr(s, p[0], p[1], p[2]);
            }
            if (gettextTranslation != null && !gettextTranslation.equals(""))
                return gettextTranslation;
        } catch (Exception ex) {
            System.err.println("Caught exception " + ex);
        }
        System.err.println("Missing translation: " + s + ", " + p);
        return "";
    }

    /**
     * Sets the action to "Move" and selects the move button.
     */
    public void setActionMove() {
        sendAction("Move", buttonMove);
        buttonMove.setSelected(true);
    }

    /**
     * Sets the action to "Select" and selects the select button.
     */
    public void setActionSelect() {
        sendAction("Select", buttonSelect);
        buttonSelect.setSelected(true);
    }

    /**
     * Handles action events by sending the action command and source to the sendAction method.
     *
     * @param e the action event
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Object src = e.getSource();
        sendAction(command, src);
    }

    /**
     * Processes an action command based on the provided command string and source.
     *
     * <p>This method interprets the action command triggered by the user and executes
     * the corresponding operation such as saving files, opening dialogs, performing undo/redo actions,
     * updating the UI state, and other application-specific functions.
     *
     * @param command the action command to be processed
     * @param src the source object that triggered the command (e.g., JMenuItem, JToggleButton, or File)
     */
    synchronized public void sendAction(String command, Object src) {


        String tip = null;
        String ps = null;
        String pname = null;
        JToggleButton button = null;
        JMenuItem item = null;
        boolean select = true;

        if (src instanceof JMenuItem) {
            item = (JMenuItem) src;
            ps = item.getText();
            tip = item.getToolTipText();
            pname = item.getName();
            select = item.isSelected();

        } else if (src instanceof JToggleButton) {
            button = (JToggleButton) src;
            ps = button.getText();
            tip = button.getToolTipText();
            select = button.isSelected();
        }

        d.setCursor(Cursor.getDefaultCursor());

        if (command.equals("example")) {
            this.openResourceFile(pname);
        } else if (command.equals("Save as PS")) {
            if (!need_save())
                return;

            DialogPsProperty dlg = new DialogPsProperty(this);
            this.centerDialog(dlg);
            dlg.setVisible(true);
            int r = dlg.getSavePsType();
            boolean ptf = dlg.getPointfilled();
//            boolean pts = dlg.getisProveTextSaved();

            if (r == 0 || r == 1 || r == 2) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith("ps");
                    }

                    public String getDescription() {
                        return "PostScript (*.ps)";
                    }
                });
                String dr = getUserDir();
                chooser.setCurrentDirectory(new File(dr));

                int result = chooser.showSaveDialog(this);
                if (result == JFileChooser.CANCEL_OPTION) {
                    return;
                }
                try {
                    File file = chooser.getSelectedFile();
                    String path = file.getPath();
                    if (!path.endsWith(".ps")) {
                        path += ".ps";
                    }
                    if (file.exists() && get_User_Overwrite_Option(file.getName())) {
                        return;
                    }
                    dp.write_ps(path, r, ptf, true);
                } catch (Exception ee) {
                    CMisc.print(ee.toString() + "\n" + ee.getStackTrace());
                }
            }

        } else if (command.equalsIgnoreCase("Save as PDF")) {
            this.saveAsPDF();
        } else if (command.equals("Save as Image")) {
            this.saveAsImage();
        } else if (command.equals("Save as Animated Image")) {
            this.saveAsGIF();
        } else if (command.equalsIgnoreCase("Save Proof as Animated Image")) {
            this.saveProofAsGIF();
        } else if (command.equalsIgnoreCase("Prove")) {
            if (((String) src).equalsIgnoreCase("gdd")) {
                pprove.proveGdd(); // TODO: Add more provers
                // Workaround: certain imported GGB conclusions may need
                // a re-computation. FIXME
                if (GExpert.conclusion != null)
                    pprove.proveGdd();
                GExpert.performCommandLineRequests(this, true);

            } else {
                pprove.prove();
            }
        } else if (command.equalsIgnoreCase("Wait")) {
            Integer secs = (Integer) src;
            try {
                wait(secs * 1000);
            } catch (Exception e) {
                // Dummy placeholder
            }

        } else if (command.equals("Save") || command.equals("Save as...")) {
            if (command.equals("Save")) {
                if (src instanceof File) {
                    dp.setFile((File) src);
                }
                this.saveAFile(false);
            } else this.saveAFile(true);

        } else if (command.equalsIgnoreCase("Save GDD Proof as GraphViz File")) {
            this.saveGDDProofAsGraphViz(src);
        } else if (command.equals("Save as Text")) {
            if (!need_save())
                return;

            GTerm gt = pprove.getConstructionTerm();
            if (gt != null) {
                JFileChooser filechooser1 = new JFileChooser();
                String dr = getUserDir();
                filechooser1.setCurrentDirectory(new File(dr));

                int result = filechooser1.showDialog(this, getLanguage("Save"));
                if (result == JFileChooser.APPROVE_OPTION) {
                    File f = filechooser1.getSelectedFile();
                    FileOutputStream fp;
                    try {
                        if (f.exists()) {
                            f.delete();
                            fp = new FileOutputStream(f, true);
                            fp.write("\n\n".getBytes());
                        } else {
                            f.createNewFile();
                            fp = new FileOutputStream(f, false);
                        }
                        if (fp == null) {
                            return;
                        }
                        gt.writeAterm(fp);
                        dp.writePointPosition(fp);
                        fp.close();
                    } catch (IOException ee) {
                        JOptionPane.showMessageDialog(this, ee.getMessage(),
                                "Save Error", JOptionPane.ERROR_MESSAGE);
                    }

                }

            }
        } else if (command.equals("Open")) {

            if (src instanceof File) {
                openAFile((File) src);
            } else {

                JFileChooser chooser = getFileChooser(false);
                int result = chooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        openAFile(file);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
            // Handle import of ggb file
        } else if (command.equals("Import")) {
            if (src instanceof File) {
                openGGBFile((File) src);
            } else {
                JFileChooser chooser = getFileChooser(true);
                int result = chooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        openGGBFile(file);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        } else if (command.equals("Exit")) {
            if (saveBeforeExit())
                System.exit(0);
        } else if (command.equals("New")) {
            this.Clear();
        } else if (command.equals("Print")) {
            dp.PrintContent();
        } else if (command.equals("undo")) {
            dp.Undo_step();
            if (lp != null) {
                lp.reload();
            }
            setBKState();
            d.repaint();
        } else if (command.equals("redo")) {
            dp.redo_step();
            if (lp != null) {
                lp.reload();
            }
            setBKState();
            d.repaint();
        } else if (command.equals("Online Help")) {
            openURL(("https://github.com/kovzol/Java-Geometry-Expert")); // FIXME, use help/index.html
        } else if (command.equals("JGEX Homepage")) {
            openURL(("https://github.com/kovzol/Java-Geometry-Expert"));
        } else if (command.equals("Contact Us")) {
            openURL(("mailto:jgex@googlegroups.com"));
        } else if (command.equals("ff")) {
            dp.redo();
            setBKState();
            d.repaint();
        } else if (command.equalsIgnoreCase("Check for Update")) {
            openURL("https://github.com/kovzol/Java-Geometry-Expert/releases");
        } else if (command.equals("fr")) {
            dp.Undo();
            setBKState();
            d.repaint();
        } else if (command.equals("autoanimate")) {
            boolean b = dp.autoAnimate();

        } else if (command.equals("autoshowstep")) {
            dp.autoShowstep();
        } else if (command.equals("Preferences")) {
            JDialog dlg = new MiscDialog(this);
            this.centerDialog(dlg);
            dlg.setVisible(true);
        } else if (command.equals("Show Step Bar")) {
            this.showProveBar(true);
        } else if (command.equals("Style Dialog")) {
            this.showStyleDialog();
        } else if (command.equals("About JGEX")) {
            if (adialog == null)
                adialog = new AboutDialog(this);
            adialog.initLocation();
            adialog.setVisible(true);
        } else if (command.equals("Help")) {
            String dr = getUserDir();
            String sp = getFileSeparator();
            openURL("file:///" + dr + sp + "help" + sp + "index.html");
        } else if (command.equals("grid")) {
            dp.SetGrid(select);
            repaint();
        } else if (command.equals("snap")) {
            dp.SetSnap(!dp.isSnap());
            d.repaint();
        } else if (command.equals("view")) {
            this.dp.SetCurrentAction(DrawProcess.VIEWELEMENT);
        } else if (command.equals("lessgrid")) {
            dp.setMeshStep(true);
            button.setSelected(false);
            repaint();
        } else if (command.equals("moregrid")) {
            dp.setMeshStep(false);
            button.setSelected(false);
            repaint();
        } else if (command.equals("Construct History")) {
            this.getUndoEditDialog().showDialog();
        } else if (command.equals("Help on Mode")) {
            String path = HelpMode.getHelpMode(_command);
            if (path != null) {
                String dr = getUserDir();
                String sp = getFileSeparator();
                openURL("file:///" + dr + sp + "help" + sp + path);
            }
        } else {
            _command = command;
            String sx1 = GExpert.getLanguage(command);
            String sx2 = GExpert.getLanguageTip(command);
            if (tip != null)
                sx2 = tip;

            setActionTip(sx1, sx2);
            if (button != null) {
                JRadioButtonMenuItem t = (JRadioButtonMenuItem) menugroup.getButton(command);
                if (t != null)
                    t.setSelected(true);
                else {
                    ButtonModel m = menugroup.getSelection();
                    if (m != null) {
                        m.setGroup(null);
                        m.setSelected(false);
                        m.setGroup(menugroup);
                    }
                }
            } else if (item != null) {
                JToggleButton b = (JToggleButton) group.getButton(command);
                if (b != null)
                    b.setSelected(true);
                else {
                    ButtonModel b1 = group.getSelection();
                    if (b1 != null) {
                        b1.setGroup(null);
                        b1.setSelected(false);
                        b1.setGroup(group);
                    }
                }
            }


            if (command.equalsIgnoreCase("select")) {
                dp.SetCurrentAction(DrawProcess.SELECT);
            } else if (command.equalsIgnoreCase("point")) {
                dp.SetCurrentAction(DrawProcess.D_POINT);
            } else if (command.equalsIgnoreCase("line")) {
                dp.SetCurrentAction(DrawProcess.D_LINE);
            } else if (command.equalsIgnoreCase("circle")) {
                dp.SetCurrentAction(DrawProcess.D_CIRCLE);

            } else if (command.equalsIgnoreCase("oriented segment")) {
                dp.SetCurrentAction(DrawProcess.D_PRATIO);
                String s = ((JMenuItem) src).getText();
                int n1 = 1;
                int n2 = 1;
                // ps = language.getEnglish(ps);
                if (ps.equalsIgnoreCase(getLanguage("Other..."))) {
                    RatioSelectDialog dlg = new RatioSelectDialog(this);
                    dlg.setVisible(true);
                    dp.setParameter(dlg.getValue1(), dlg.getValue2());
                } else {
                    String s1 = ((JMenuItem) src).getText();
                    int[] t = this.parse2Int(s1);
                    dp.setParameter(t[0], t[1]);
                }
            } else if (command.equalsIgnoreCase("compass")) {
                dp.SetCurrentAction(DrawProcess.D_CIRCLEBYRADIUS);
            } else if (command.equalsIgnoreCase("parallel")) {
                dp.SetCurrentAction(DrawProcess.D_PARELINE);
            } else if (command.equalsIgnoreCase("perpendicular")) {
                dp.SetCurrentAction(DrawProcess.D_PERPLINE);
            } else if (command.equalsIgnoreCase("aline")) {
                dp.SetCurrentAction(DrawProcess.D_ALINE);
            } else if (command.equalsIgnoreCase("Angle Bisector")) {
                dp.SetCurrentAction(DrawProcess.D_ABLINE);
            } else if (command.equalsIgnoreCase("bline")) {
                dp.SetCurrentAction(DrawProcess.D_BLINE);
            } else if (command.equalsIgnoreCase("tcline")) {
                dp.SetCurrentAction(DrawProcess.D_TCLINE); //cctangent
            }
//            else if (command.equalsIgnoreCase("cctangent")) {
//                dp.SetCurrentAction(drawProcess.CCTANGENT);
//            }
            else if (command.equalsIgnoreCase("intersect")) {
                dp.SetCurrentAction(DrawProcess.MEET);
            } else if (command.equalsIgnoreCase("middle")) {
                dp.SetCurrentAction(DrawProcess.D_MIDPOINT);
            } else if (command.equalsIgnoreCase("Circle by Three Points")) {
                dp.SetCurrentAction(DrawProcess.D_3PCIRCLE);
            } else if (command.equalsIgnoreCase("translate")) {
                this.setDrawCursor(Cursor.HAND_CURSOR);
                dp.SetCurrentAction(DrawProcess.TRANSLATE);
            } else if (command.equalsIgnoreCase("foot")) {
                dp.SetCurrentAction(DrawProcess.PERPWITHFOOT);
            } else if (command.equalsIgnoreCase("angle")) {
                dp.SetCurrentAction(DrawProcess.D_ANGLE);
            } else if (command.equalsIgnoreCase("zoom-in")) {
                //setDrawCursor("ZOOM_IN");
                dp.SetCurrentAction(DrawProcess.ZOOM_IN);
            } else if (command.equalsIgnoreCase("zoom-out")) {
                //setDrawCursor("ZOOM_OUT");
                dp.SetCurrentAction(DrawProcess.ZOOM_OUT);
            } else if (command.equalsIgnoreCase("animation")) {
                dp.SetCurrentAction(DrawProcess.ANIMATION);
            } else if (command.equalsIgnoreCase("eqangle")) {
                dp.SetCurrentAction(DrawProcess.SETEQANGLE);
            } else if (command.equalsIgnoreCase("nteqangle")) {
                dp.SetCurrentAction(DrawProcess.NTANGLE);
            } else if (command.equalsIgnoreCase("eqangle3p")) {
                dp.SetCurrentAction(DrawProcess.SETEQANGLE3P);
            } else if (command.equalsIgnoreCase("cctangent")) {
                dp.SetCurrentAction(DrawProcess.SETCCTANGENT);
            } else if (command.equalsIgnoreCase("angle specification")) {
                dp.defineSpecificAngle();
            } else if (command.equalsIgnoreCase("ra_side")) {
                dp.SetCurrentAction(DrawProcess.SETEQSIDE);
                dp.setcurrentStatus(0);
                // ps = language.getEnglish(ps);
                if (ps.equalsIgnoreCase(getLanguage("Other..."))) {
                    RatioSelectDialog dlg = new RatioSelectDialog(this);
                    dlg.setVisible(true);
                    dp.setParameter(dlg.getValue1(), dlg.getValue2());
                } else {
                    String s1 = ((JMenuItem) src).getText();
                    int[] t = this.parse2Int(s1);
                    dp.setParameter(t[0], t[1]);
                }

//                int status = Integer.parseInt(ps);
//                dp.setcurrentStatus(status);
            } else if (command.equalsIgnoreCase("equal distance")) {
                dp.SetCurrentAction(DrawProcess.SETEQSIDE);
                dp.setcurrentStatus(1);
                dp.setParameter(1, 1);
            } else if (command.equalsIgnoreCase("fillpolygon")) {
                dp.SetCurrentAction(DrawProcess.DEFINEPOLY);
            } else if (command.equalsIgnoreCase("polygon")) {
                dp.SetCurrentAction(DrawProcess.D_POLYGON);
            } else if (command.equalsIgnoreCase("square")) {
                dp.SetCurrentAction(DrawProcess.D_SQUARE);
            } else if (command.equalsIgnoreCase("radical of two circles")) {
                dp.SetCurrentAction(DrawProcess.D_CCLINE);
            } else if (command.equalsIgnoreCase("isosceles triangle")) {
                dp.SetCurrentAction(DrawProcess.D_IOSTRI);
            } else if (command.equalsIgnoreCase("fill polygon")) {
                dp.SetCurrentAction(DrawProcess.DEFINEPOLY);
            } else if (command.equalsIgnoreCase("text")) {
                dp.SetCurrentAction(DrawProcess.D_TEXT);
            } else if (command.equalsIgnoreCase("mirror")) {
                dp.SetCurrentAction(DrawProcess.MIRROR);
            } else if (command.equalsIgnoreCase("circle by diameter")) {
                dp.SetCurrentAction(DrawProcess.D_PFOOT);
            } else if (command.equalsIgnoreCase("Trace")) {
                dp.SetCurrentAction(DrawProcess.SETTRACK);
            } else if (command.equalsIgnoreCase("Locus")) {
                dp.SetCurrentAction(DrawProcess.LOCUS);
            } else if (command.equalsIgnoreCase("Intersection by compass and circle/line")) {
                dp.SetCurrentAction(DrawProcess.D_PTDISTANCE);
            } else if (command.equalsIgnoreCase("propline")) {
                String s = ((JMenuItem) src).getText();
                // ps = language.getEnglish(ps);
                if (ps.equalsIgnoreCase(getLanguage("Other..."))) {
                    dp.SetCurrentAction(DrawProcess.LRATIO);
                    RatioSelectDialog dlg = new RatioSelectDialog(this);
                    dlg.setVisible(true);
                    dp.setParameter(dlg.getValue1(), dlg.getValue2());
                    this.setTipText(dlg.getValue1() + ":" + dlg.getValue2());
                } else {
                    dp.SetCurrentAction(DrawProcess.LRATIO);
                    int[] t = this.parse2Int(s);
                    dp.setParameter(t[0], t[1]);
                    this.setTipText(s);
                }
            } else if (command.equalsIgnoreCase("midpoint")) {
                dp.SetCurrentAction(DrawProcess.D_MIDPOINT);
            } else if (command.equalsIgnoreCase("circumcenter")) {
                dp.SetCurrentAction(DrawProcess.CIRCUMCENTER);
            } else if (command.equalsIgnoreCase("centroid")) {
                dp.SetCurrentAction(DrawProcess.BARYCENTER);
            } else if (command.equalsIgnoreCase("orthocenter")) {
                dp.SetCurrentAction(DrawProcess.ORTHOCENTER);
            } else if (command.equalsIgnoreCase("incenter")) {
                dp.SetCurrentAction(DrawProcess.INCENTER);
            } else if (command.equalsIgnoreCase("move")) {
                dp.SetCurrentAction(DrawProcess.MOVE);
            } else if (command.equalsIgnoreCase("o_t_segment")) {
                dp.SetCurrentAction(DrawProcess.D_TRATIO);
                String s = ((JMenuItem) src).getText();
                // ps = language.getEnglish(ps);
                if (ps.equalsIgnoreCase(getLanguage("Other..."))) {
                    RatioSelectDialog dlg = new RatioSelectDialog(this);
                    dlg.setVisible(true);
                    dp.setParameter(dlg.getValue1(), dlg.getValue2());
                } else {
                    String s1 = ((JMenuItem) src).getText();
                    int[] t = this.parse2Int(s1);
                    dp.setParameter(t[0], t[1]);
                }
            } else if (command.equalsIgnoreCase("measure distance")) {
                dp.SetCurrentAction(DrawProcess.DISTANCE);
            } else if (command.equalsIgnoreCase("Arrow")) {
                dp.SetCurrentAction(DrawProcess.ARROW);
            } else if (command.equalsIgnoreCase("horizonal")) {
                dp.SetCurrentAction(DrawProcess.H_LINE);
            } else if (command.equalsIgnoreCase("vertical")) {
                dp.SetCurrentAction(DrawProcess.V_LINE);
            } else if (command.equalsIgnoreCase("eqmark")) {
                dp.SetCurrentAction(DrawProcess.EQMARK);
                int status = Integer.parseInt(ps);
                dp.setcurrentStatus(status);
            } else if (command.equalsIgnoreCase("triangle")) {
                dp.setcurrentStatus(3);
                dp.SetCurrentAction(DrawProcess.D_POLYGON);
                dp.setcurrentStatus(3);
            } else if (command.equalsIgnoreCase("equilateral triangle")) {
                dp.SetCurrentAction(DrawProcess.DRAWTRIALL);
            } else if (command.equalsIgnoreCase("Tri_perp")) {
                dp.SetCurrentAction(DrawProcess.D_PFOOT);

            } else if (command.equalsIgnoreCase("Tri_sq_iso")) {
                dp.SetCurrentAction(DrawProcess.DRAWTRISQISO);
            } else if (command.equalsIgnoreCase("quadrangle")) {
                dp.setcurrentStatus(4);
                dp.SetCurrentAction(DrawProcess.D_POLYGON);
                dp.setcurrentStatus(4);
            } else if (command.equalsIgnoreCase("parallelogram")) {
                dp.SetCurrentAction(DrawProcess.PARALLELOGRAM);
            } else if (command.equalsIgnoreCase("ra_trapezoid")) {
                dp.SetCurrentAction(DrawProcess.RA_TRAPEZOID);
            } else if (command.equalsIgnoreCase("trapezoid")) {
                dp.SetCurrentAction(DrawProcess.TRAPEZOID);
            } else if (command.equalsIgnoreCase("rectangle")) {
                dp.SetCurrentAction(DrawProcess.RECTANGLE);
            } else if (command.equalsIgnoreCase("pentagon")) {
                dp.setcurrentStatus(5);
                dp.SetCurrentAction(DrawProcess.D_POLYGON);
                dp.setcurrentStatus(5);
            } else if (command.equalsIgnoreCase("polygon")) {
                dp.SetCurrentAction(DrawProcess.D_POLYGON);
                dp.setcurrentStatus(9999);
            } else if (command.equalsIgnoreCase("hide object")) {
                dp.SetCurrentAction(DrawProcess.HIDEOBJECT);
            } else if (command.equalsIgnoreCase("show object")) {
                dp.SetCurrentAction(DrawProcess.SHOWOBJECT);
            } else if (command.equalsIgnoreCase("Rules for Full Angle")) {
                getRuleDialog(1).setVisible(true);
            } else if (command.equalsIgnoreCase("Rules for GDD")) {
                getRuleDialog(0).setVisible(true);
            } else if (command.equalsIgnoreCase("sangle")) {
                dp.SetCurrentAction(DrawProcess.SANGLE);
                try {
                    int n = 0;
                    ps = language.getEnglish(ps);
                    // Here we need to check the translated string:
                    if (ps.equalsIgnoreCase(getLanguage("Other..."))) {
                        String s = JOptionPane.showInputDialog(this, this.getLanguage("Please input the value of the angle"));
                        if (s == null)
                            s = "0";
                        n = Integer.parseInt(s);
                    } else
                        n = Integer.parseInt(ps);
                    dp.setcurrentStatus(n);
                } catch (NumberFormatException ee) {
                    JOptionPane.showMessageDialog(this, ee.getMessage(), "Information", JOptionPane.WARNING_MESSAGE);
                }
            } else if (command.equalsIgnoreCase("equal ratio")) {
                dp.SetCurrentAction(DrawProcess.RATIO);
            } else if (command.equalsIgnoreCase("RAMark"))
                dp.SetCurrentAction(DrawProcess.RAMARK);
            else if (command.equalsIgnoreCase("Transform"))
                dp.SetCurrentAction(DrawProcess.TRANSFORM);
            else if (command.equalsIgnoreCase("Equivalence"))
                dp.SetCurrentAction(DrawProcess.EQUIVALENCE);
            else if (command.equalsIgnoreCase("Free Transform"))
                dp.SetCurrentAction(DrawProcess.FREE_TRANSFORM);
            else if (command.equalsIgnoreCase("Calculation")) {
                TextValueEditor dlg = new TextValueEditor(this);
                this.centerDialog(dlg);
                dlg.setVisible(true);
            }

        }
    }

    /**
     * Saves the GDD proof as a GraphViz file.
     *
     * @param src the source object, which can be a File or another object
     */
    private void saveGDDProofAsGraphViz(Object src) {
        File ff;
        if (src instanceof File) {
            ff = (File) src;
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new JFileFilter("gv"));

            String dr1 = getUserDir();
            chooser.setCurrentDirectory(new File(dr1));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            String dr = getUserDir();
            chooser.setCurrentDirectory(new File(dr));

            ff = chooser.getSelectedFile();
        }
        String p = ff.getPath();
        if (!p.endsWith("gv") && !p.endsWith("GV")) {
            p = p + ".gv";
            ff = new File(p);
        }
        try {
            DataOutputStream out = dp.openOutputFile(ff.getPath());
            Path path = Paths.get(ff.getPath());
            String program = PanelProve.graphvizProgram;
            Files.write(path, java.util.List.of(program.split("\n")), StandardCharsets.UTF_8);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    /**
     * Opens a GeoGebra file (.ggb) and loads its content.
     *
     * @param file the GeoGebra file to be opened
     * @return true if the file was opened successfully, false otherwise
     */
    private boolean openGGBFile(File file) {
        String path = file.getPath();
        File f = new File(path);
        if (!f.exists() && !path.endsWith(".ggb"))
            path += ".ggb";
        f = new File(path);
        try {
            if (f.exists()) {
                boolean r = true;
                if (2 == this.Clear()) // cancel option.
                    return false;
                if (f.getName().endsWith("ggb")) {
                    dp.clearAll();
                    dp.setFile(f);
                    DataInputStream in = dp.openInputFile(f.getPath());
                    //r = dp.LoadGGB(in,f.getPath());
                    r = dp.LoadGGB2(in, f.getPath());
                    // pprove.LoadProve(in); // TODO:
                    in.close();
                    // dp.stopUndoFlash(); // TODO:
                    dp.reCalculate();
                } else {
                    System.out.println("GeoGebra file must end with .ggb");
                    return false;
                }
                dp.setName(file.getName());
                CMisc.version_load_now = 0;
                CMisc.onFileSavedOrLoaded();
                updateTitle();
                return r;
            } else return false;
        } catch (IOException ee) {
            StackTraceElement[] tt = ee.getStackTrace();

            String s = ee.toString();
            for (int i = 0; i < tt.length; i++) {
                if (tt[i] != null)
                    s += tt[i].toString() + "\n";
            }
            System.out.println(s);
        }
        return false;
    }

    /**
     * Parses a string in the format "x:y" into an array of two integers.
     *
     * @param s the string to be parsed
     * @return an array of two integers parsed from the string
     */
    int[] parse2Int(String s) {
        String[] sl = s.split(":");
        int[] t = new int[2];
        try {
            t[0] = Integer.parseInt(sl[0].trim());
            t[1] = Integer.parseInt(sl[1].trim());
        } catch (NumberFormatException ee) {
            t[0] = 1;
            t[1] = 1;
        }
        return t;
    }

    /**
     * Sets the enabled state of the undo and redo buttons based on the sizes of the undo and redo lists.
     */
    public void setBKState() {
        int n1 = dp.getUndolistSize();
        int n2 = dp.getRedolistSize();
        if (n1 == 0 && n2 == 0) {
            BK2.setEnabled(true);
            BK4.setEnabled(true);
            BK1.setEnabled(true);
            BK3.setEnabled(true);
        } else {
            BK2.setEnabled(n1 != 0);
            BK4.setEnabled(n1 != 0);
            BK1.setEnabled(n2 != 0);
            BK3.setEnabled(n2 != 0);
        }
    }

    /**
     * Prompts the user to save any unsaved changes before exiting the application.
     *
     * @return true if the user chose to save or discard changes, false if the user canceled the operation
     */
    public boolean saveBeforeExit() {
        if (dp.need_save() && CMisc.needSave()) {
            int n = JOptionPane.showConfirmDialog(this, getLanguage("The diagram has been changed, do you want to save it?"),
                    getLanguage("Save"), JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.OK_OPTION) {
                boolean r = saveAFile(false);
                return r;
            } else if (n == JOptionPane.NO_OPTION)
                return true;
            else
                return false;
        }
        return true;
    }

    /**
     * Checks if there are any unsaved changes.
     *
     * @return true if there are unsaved changes, false otherwise
     */
    public boolean need_save() {
        if (!dp.need_save()) {
            this.setTipText(getLanguage("Nothing to be saved."));
            return false;
        }
        return true;
    }

    /**
     * Saves the current file. If the file does not exist or the user chooses "Save as...",
     * prompts the user to select a file location.
     *
     * @param n if true, prompts the user to select a file location
     * @return true if the file was saved successfully, false otherwise
     */
    public boolean saveAFile(boolean n) {
        File file = dp.getFile();
        int result = 0;

        if (need_save()) {
            if (file == null || n) { // command.equals("Save as...")
                JFileChooser chooser = this.getFileChooser(false);

                try {
                    if (file != null && file.exists())
                        chooser.setSelectedFile(file);
                    result = chooser.showSaveDialog(this);
                } catch (Exception ee) {
                    filechooser = null;
                    chooser = this.getFileChooser(false);
                    result = chooser.showSaveDialog(this);
                }

                if (result == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                } else
                    file = null;
            }
            if (file != null)
                try {
                    String path = file.getPath();
                    if (!path.endsWith(".gex")) {
                        path += ".gex";
                    }
                    File f = new File(path);
                    if (f.exists() && get_User_Overwrite_Option(file.getName())) {
                        return false;
                    }
                    saveAFile(path);
                    updateTitle();
                    CMisc.onFileSavedOrLoaded();
                    return true;

                } catch (Exception ee) {
                    ee.printStackTrace();
                    CMisc.print(ee.getMessage() + "\n" + ee.getStackTrace());
                }
        }
        return false;
    }

    /**
     * Clears the current diagram and resets the application state.
     * Prompts the user to save any unsaved changes before clearing.
     *
     * @return 0 if the diagram was cleared successfully, 2 if the user canceled the operation
     */
    public int Clear() {
        int n = 0;
        if (CMisc.isApplication() && !dp.isitSaved()) {
            n = JOptionPane.showConfirmDialog(this, getLanguage("The diagram has been changed, do you want to save it?"),
                    getLanguage("Save"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (n == JOptionPane.YES_OPTION) {
                if (!saveAFile(false))
                    return 2;
            } else if (n == JOptionPane.NO_OPTION) {
                // if (!saveAFile(true))
                // return 2;
            } else {
                return 2;
            }
        }
        this.resetAllButtonStatus();
        CCoBox.resetAll();
        this.setActionMove();
        dp.clearAll();
        d.clearAll();
        if (pprove != null) {
            pprove.finishedDrawing();
        }
        updateTitle();
        scroll.revalidate();
        d.repaint();
        provePanelbar.setVisible(false);
        closeAllDialogs();

        return 0;
    }

    /**
     * Closes all open dialogs in the application.
     */
    public void closeAllDialogs() {
        if (propt != null)
            propt.setVisible(false);
        if (sdialog != null)
            sdialog.setVisible(false);
        if (udialog != null)
            udialog.setVisible(false);
        if (pdialog != null)
            pdialog.setVisible(false);
        if (cdialog != null)
            cdialog.setVisible(false);
        if (rdialog != null)
            rdialog.setVisible(false);
        if (ndialog != null)
            ndialog.setVisible(false);
        if (adialog != null)
            adialog.setVisible(false);
        // removeAllDependentDialogs();
    }

    /**
     * Sets the cursor to the specified predefined cursor type.
     *
     * @param t the predefined cursor type
     */
    public void setDrawCursor(int t) {
        d.setCursor(Cursor.getPredefinedCursor(t));
    }

    /**
     * Reloads the list of points if the list panel and undo dialog are visible.
     */
    public void reloadLP() {
        if (lp != null && udialog != null && udialog.isVisible())
            lp.reload();
    }

    /**
     * Prompts the user to confirm overwriting an existing file.
     *
     * @param name the name of the file to be overwritten
     * @return true if the user chose to overwrite the file, false otherwise
     */
    public boolean get_User_Overwrite_Option(String name) {
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this,
                getTranslationViaGettext("{0} already exists, do you want to overwrite it?", name),
                getLanguage("File exists"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            return true;
        }
        return false;
    }

    /**
     * Saves the project to a file at the specified path.
     *
     * @param path the file path to save the project
     * @throws IOException if an I/O error occurs during saving
     */
    public void saveAFile(String path) throws IOException {
        DataOutputStream out = dp.openOutputFile(path);
        dp.Save(out);
        pprove.SaveProve(out);
        out.close();
    }

    /**
     * Saves the proof as a GIF image.
     * Opens dialogs to select the region and file destination, then encodes the proof into a GIF.
     */
    public void saveProofAsGIF() {
        if (provePanelbar == null)
            return;
        if (!need_save())
            return;

        RectangleSelectionDialog r1 = new RectangleSelectionDialog(this);
        this.centerDialog(r1);
        r1.setVisible(true);
        if (!r1.getResult())
            return;
        Rectangle rc = r1.getRectangle();


        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new JFileFilter("GIF"));

        String dr1 = getUserDir();
        chooser.setCurrentDirectory(new File(dr1));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        String dr = getUserDir();
        chooser.setCurrentDirectory(new File(dr));

        File ff = chooser.getSelectedFile();
        String p = ff.getPath();
        if (!p.endsWith("gif") && !p.endsWith("GIF")) {
            p = p + ".gif";
            ff = new File(p);
        }
        try {
            DataOutputStream out = dp.openOutputFile(ff.getPath());
            GifEncoder e = new GifEncoder();
            e.setQuality(20);
            e.start(out);
            e.setRepeat(0);
            e.setDelay(200);   // 1 frame per sec

            ImageTimer t = new ImageTimer(this);
            t.setEncorder(e);
            t.setRectangle(rc);

            t.setProveBar(provePanelbar);
            t.setDelay(200);
            t.setVisible(true);
            e.finish();
            out.close();

        } catch (Exception ee) {
            ee.printStackTrace();
        }


    }

    /**
     * Saves the current animation as a GIF.
     * Displays GIF options, captures the animation frames, and writes them to the selected file.
     */
    public void saveAsGIF() {

        if (!need_save())
            return;
        AnimateC am = dp.getAnimateC();
        if (am == null) {
            JOptionPane.showMessageDialog(this, getLanguage("No animation has been defined.") + "\n"
                    + getLanguage("Please use the menu \"Action -> Animation\" to define an animation first."), "GIF", JOptionPane.WARNING_MESSAGE);
            return;
        }
        am = new AnimateC(am);


        GIFOptionDialog dlg = new GIFOptionDialog(this, getLanguage("GIF Option"));
        this.centerDialog(dlg);
        dlg.setDefaultValue(20);
        dlg.setVisible(true);
        if (!dlg.getReturnResult())
            return;
        int q = dlg.getQuality();

        Rectangle rect = null;
        RectChooser rchoose = new RectChooser(this);
        if (rchoose.getReturnResult()) {
            rect = rchoose.getSelectedRectangle();
        } else
            return;

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new JFileFilter("GIF"));
        String dr = getUserDir();
        chooser.setCurrentDirectory(new File(dr));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }


        File ff = chooser.getSelectedFile();
        String p = ff.getPath();
        if (!p.endsWith("gif") && !p.endsWith("GIF")) {
            p = p + ".gif";
            ff = new File(p);
        }


        am.reClaclulate();
        int n = am.getRounds();
        if (n == 0) return;

        int v = 1000 / am.getInitValue();

        GIFProcessDialog dlg1 = new GIFProcessDialog(this.getFrame());
        this.centerDialog(dlg1);
        dlg1.setTotal(n);

        int k = 0;

        try {
            DataOutputStream out = dp.openOutputFile(ff.getPath());
            GifEncoder e = new GifEncoder();
            e.setQuality(q);
            e.start(out);
            e.setRepeat(0);
            e.setDelay(v);   // 1 frame per sec
            dlg1.en = e;
            dlg1.dp = dp;
            dlg1.rect = rect;
            dlg1.am = am;
            dlg1.gxInstance = this;
            dlg1.out = out;
            dlg1.setVisible(true);
            dlg1.setRun();

//            while (n >= 0) {
//                am.onTimer();
//                if (!dp.reCalculate()) {
//                    am.resetXY();
//                }
//                e.addFrame(this.getBufferedImage(rect));
//                n--;
//            }
//            e.finish();
//            out.close();

        } catch (IOException ee) {
            System.out.println(ee.getMessage());
        }


    }

    /**
     * Saves the current view as an image file.
     * Opens a file chooser to select the output format and destination, then writes the captured image.
     */
    public void saveAsImage() {

        if (!need_save())
            return;

        Rectangle rect = null;
        RectChooser rchoose = new RectChooser(this);
        if (rchoose.getReturnResult()) {
            rect = rchoose.getSelectedRectangle();
        } else
            return;

        JFileChooser chooser = new JFileChooser();
        String[] s = ImageIO.getWriterFormatNames();
        String[] s1 = new String[s.length + 1];
        for (int i = 0; i < s.length; i++)
            s1[i] = s[i];
        s1[s.length] = "gif";
        s = s1;

        if (s.length > 0) {
            FileFilter t = chooser.getFileFilter();
            chooser.removeChoosableFileFilter(t);

            JFileFilter selected = null;
            for (int i = 0; i < s.length; i++) {
                JFileFilter f = new JFileFilter(s[i]);
                chooser.addChoosableFileFilter(f);

                if (s[i].equalsIgnoreCase("JPG"))
                    selected = f;
                if (selected == null && s[i].equalsIgnoreCase("JPEG"))
                    selected = f;
            }
            chooser.setFileFilter(selected);
        }
        String dr = getUserDir();
        chooser.setCurrentDirectory(new File(dr));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File ff = chooser.getSelectedFile();
        FileFilter f = chooser.getFileFilter();
        String endfix = f.getDescription();
        if (endfix == null)
            return;

        String p = ff.getPath();
        if (!p.endsWith(endfix)) {
            p = p + "." + endfix;
            ff = new File(p);
        }

        if (endfix.equals("gif")) {
            try {
                DataOutputStream out = dp.openOutputFile(ff.getPath());
                GifEncoder e = new GifEncoder();
                e.setQuality(1);
                e.start(out);
                e.setRepeat(0);
                e.setDelay(0);
                e.addFrame(this.getBufferedImage(rect));
                e.finish();
                out.close();
            } catch (IOException ee) {
                if (CMisc.isDebug())
                    ee.printStackTrace();
                else JOptionPane.showMessageDialog(this, ee.getMessage(), "Information", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            BufferedImage image = getBufferedImage(rect);
            Iterator iter = ImageIO.getImageWritersByFormatName(endfix);
            ImageWriter writer = (ImageWriter) iter.next();
            try {
                ImageOutputStream imageOut = ImageIO.createImageOutputStream(ff);
                writer.setOutput(imageOut);

                writer.write(new IIOImage(image, null, null));
                IIOImage iioImage = new IIOImage(image, null, null);
                if (writer.canInsertImage(0))
                    writer.writeInsert(0, iioImage, null);
                imageOut.close();
            } catch (IOException exception) {
                if (CMisc.isDebug())
                    exception.printStackTrace();
                else
                    JOptionPane.showMessageDialog(this, exception.getMessage(), "Information", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /**
     * Returns a buffered image of the drawing area bounded by the specified rectangle.
     *
     * @param rc the rectangle defining the area to capture
     * @return the buffered image of the drawing area
     */
    public BufferedImage getBufferedImage(Rectangle rc) {
        BufferedImage image = new BufferedImage((int) rc.getWidth(), (int) rc.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(-rc.getX(), -rc.getY());
        d.paintComponent(g2);
        return image;
    }

    /**
     * Returns a buffered image of the content pane bounded by the specified rectangle.
     *
     * @param rc the rectangle defining the area to capture
     * @return the buffered image of the content pane
     */
    public BufferedImage getBufferedImage2(Rectangle rc) {
        BufferedImage image = new BufferedImage((int) rc.getWidth(), (int) rc.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(-rc.getX(), -rc.getY());
        contentPane.paint(g2);
        return image;
    }

    /**
     * Opens and loads a project file.
     * Adjusts the file extension if necessary, then loads the project data accordingly.
     *
     * @param file the file to open
     * @return true if the file was successfully loaded, false otherwise
     */
    public boolean openAFile(File file) {
        String path = file.getPath();
        File f = new File(path);
        if (!f.exists() && !path.endsWith(".gex"))
            path += ".gex";
        f = new File(path);


        try {
            if (f.exists()) {
                boolean r = true;
                if (2 == this.Clear()) // cancel option.
                    return false;

                if (f.getName().endsWith("gex")) {
                    dp.clearAll();
                    dp.setFile(f);
                    DataInputStream in = dp.openInputFile(f.getPath());
                    r = dp.Load(in);

                    if (CMisc.version_load_now < 0.035) {
                        this.showppanel(true);
                    } else if (CMisc.version_load_now == 0.035) {
                        MNode n = new MNode();
                        n.Load(in, dp);
                        pprove.loadMTree(n);
                        this.showppanel(false);
                    } else if (CMisc.version_load_now >= 0.036) {
                        pprove.LoadProve(in);
                    }
                    in.close();
                    dp.stopUndoFlash();
                    dp.reCalculate();
                } else {
                    r = pprove.load(f);
                    if (r)
                        showppanel(false);
                    else return r;
                }
                dp.setName(file.getName());
                CMisc.version_load_now = 0;
                CMisc.onFileSavedOrLoaded();
                updateTitle();
                return r;
            } else return false;
        } catch (IOException ee) {
            StackTraceElement[] tt = ee.getStackTrace();

            String s = ee.toString();
            for (int i = 0; i < tt.length; i++) {
                if (tt[i] != null)
                    s += tt[i].toString() + "\n";
            }
            System.out.println(s);
//            CMisc.print(ee.toString() + "\n" + ee.getStackTrace());
//            JDialog dlg = new JDialog(this.getFrame());
//            JTextPane t= new JTextPane();
//            t.setText(ee.getStackTrace().toString());
//            ee.printStackTrace();
        }
        return false;
    }

    /**
     * Opens and loads a project file from a resource path.
     * 
     * @param resourcePath the path of the resource to open
     * @return true if the file was successfully loaded, false otherwise
     */
    public boolean openResourceFile(String resourcePath) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream is = cl.getResourceAsStream(resourcePath);
            if (is == null) return false;

            boolean r = true;
            if (2 == this.Clear()) // cancel option.
                return false;

            dp.clearAll();
            String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            dp.setName(fileName);

            DataInputStream in = new DataInputStream(is);
            r = dp.Load(in);

            if (CMisc.version_load_now < 0.035) {
                this.showppanel(true);
            } else if (CMisc.version_load_now == 0.035) {
                MNode n = new MNode();
                n.Load(in, dp);
                pprove.loadMTree(n);
                this.showppanel(false);
            } else if (CMisc.version_load_now >= 0.036) {
                pprove.LoadProve(in);
            }
            in.close();
            dp.stopUndoFlash();
            dp.reCalculate();

            CMisc.version_load_now = 0;
            CMisc.onFileSavedOrLoaded();
            updateTitle();
            return r;
        } catch (IOException ee) {
            StackTraceElement[] tt = ee.getStackTrace();

            String s = ee.toString();
            for (int i = 0; i < tt.length; i++) {
                if (tt[i] != null)
                    s += tt[i].toString() + "\n";
            }
            System.out.println(s);
        }
        return false;
    }

    /**
     * Adds right-side buttons to the provided tool bar.
     *
     * @param toolBar the tool bar to which the right-side buttons are added
     */
    protected void addRightButtons(JToolBar toolBar) {
        JToggleButton button = null;

        button = makeAButton("construct_history", "Construct History",
                "construct history", "construct history", true);
        toolBar.add(button);

        button = makeAButton("translate", "translate", "translate view", "Translate");
        toolBar.add(button);
        group.add(button);
        button = makeAButton("zoom-in", "zoom-in", "zoom in view", "Zoom-in");
        toolBar.add(button);
        group.add(button);
        button = makeAButton("zoom-out", "zoom-out", "zoom out view", "Zoom-out");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("snap", "snap", "snap to grid", "snap");
        toolBar.add(button);
        button = makeAButton("grid", "grid", "draw the rectangle grid", "grid");
        toolBar.add(button);
        button = makeAButton("lessGrid", "lessgrid", "make the grid less dense", "lessGrid");
        toolBar.add(button);
        button = makeAButton("moreGrid", "moregrid", "make the grid more dense", "moreGrid");
        toolBar.add(button);

        BK1 = button = makeAButton("redo", "redo", "redo a step", "redo", true);
        toolBar.add(button);
//       // KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK);
        //  button.setAccelerator(ctrlP);

        BK2 = button = makeAButton("undo", "undo", "undo a step", "undo", true);
        toolBar.add(button);
        BK3 = button = makeAButton("ff", "ff", "forward to end", "ff", true);
        toolBar.add(button);
        BK4 = button = makeAButton("fr", "fr", "back to start", "fr", true);
        toolBar.add(button);

        button = makeAButton("autoshowstep", "autoshowstep", "auto show draw step by step", "play");
        toolBar.add(button);

        anButton = button =
                makeAButtonWith2ICon("animate_start", "animate_stop", "autoanimate", "start to animate", "play");
        // anButton.setToolTipText(this.getLanguageTip("start animation"));
        toolBar.add(button);
        button.setEnabled(false);
    }

    /**
     * Adds primary action buttons to the provided tool bar.
     *
     * @param toolBar the tool bar to which the primary action buttons are added
     */
    protected void addButtons(JToolBar toolBar) {
        JToggleButton button = null;
        //ButtonGroup group = new ButtonGroup();

        button = makeAButton("new", "New", "Create a new view", "new", true);
        toolBar.add(button);
        //group.add(button);
        toolBar.add(Box.createHorizontalStrut(1));

        button = makeAButton("open", "Open", "Open a file", "open", true);
        //group.add(button);
        toolBar.add(button);

        button = makeAButton("save", "Save", "Save to a file", "save", true);
        //group.add(button);
        toolBar.add(button);

        button = makeAButton("select", "Select", "Select mode", "Select");
        toolBar.add(button);
        buttonSelect = button;
        group.add(button);

        button = makeAButton("drag", "Move", "Move", "move");
        toolBar.add(button);
        group.add(button);
        buttonMove = button;

        button = makeAButton("point", "Point", "Add a single point", "point");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("line", "Line", "Select two points to construct a line", "line");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("parallel", "Parallel", "Draw a line which is parallel to another line", "parallel");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("perp", "Perpendicular", "Draw a line which is perpendicular to another line", "perpendicular");
        toolBar.add(button);
        group.add(button);
//        button = makeAButton("abline", "Abline", "Select two lines to construct the bisector of an angle", "abline");
//        toolBar.add(button);
//        group.add(button);

        button = makeAButton("foot", "Foot",
                "Select a point and a line to construct the foot", "foot");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("circle", "Circle", "Click a point then drag to construct a circle", "circle");
        // button.setToolTipText("Click a point then drag to construct a circle");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("circle3p", "Circle by Three Points",
                "Select three points to construct the circle passing through them", "circle3p");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("circler", "Compass", "Construct a circle by clicking two points as radius and another point as center", "compass");
        // button.setToolTipText("Construct a circle by clicking two points as radius and another point as center");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("fillpolygon", "Fill Polygon", "Define a polygon", "polygon");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("angle", "Angle", "Select two lines to define their full-angle with a label", "angle");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("text", "Text", "Add text", "text");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("intersect", "Intersect", "Take the intersection of two objects (circle or line)", "intersect");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("mirror", "Mirror", "Mirror a object by clicking and then click a reflection axis or point", "mirror");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("iso", "Isosceles Triangle", "Select two points or drag a segment to construct an isosceles triangle", "isosceles triangle");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("midpoint", "Midpoint", "Click two points to get their midpoint", "midpoint");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("square", "Square", "Select two points or drag a segment to construct a square", "square");
        toolBar.add(button);
        group.add(button);

        button = makeAButton("triangle", "Triangle", null, "triangle");
        toolBar.add(button);
        group.add(button);


        button = makeAButton("polygon", "Polygon", null, "polygon");
        toolBar.add(button);
        button.removeActionListener(this);
        group.add(button);


        JToggleButton b1 = null;
        {
            String imgLocation = "images/dselect.gif";
            URL imageURL = GExpert.class.getResource(imgLocation);
            Icon co = null;
            if (imageURL != null) {
                co = (new ImageIcon(imageURL, ""));
            }
            b1 = new JToggleButton(co) {

                public Dimension getPreferredSize() {
                    return new Dimension(10, 28);
                }
            };
            b1.setUI(new EntityButtonUI(1));
            toolBar.add(b1);
        }


        JPopButtonsPanel p = new JPopButtonsPanel(button, b1);
        button = makeAButton("triangle", "Triangle", null, "triangle");
        p.add(button);
        group.add(button);

        button = makeAButton("triangle_iso", "Isosceles Triangle", null, "isosceles triangle");
        p.add(button);
        group.add(button);

        button = makeAButton("triangle_all", "Equilateral Triangle", null, "equilateral triangle");
        p.add(button);
        group.add(button);

        button = makeAButton("triangle_perp", "Tri_perp", null, "triangle");
        p.add(button);
        group.add(button);

        button = makeAButton("quadrangle", "Quadrangle", null, "quadrangle");
        p.add(button);
        group.add(button);
        button = makeAButton("parallelogram", "Parallelogram", null, "pentagon");
        p.add(button);
        group.add(button);
        button = makeAButton("trapezoid", "Trapezoid", null, "trapezoid");
        p.add(button);
        group.add(button);
        button = makeAButton("ra_trapezoid", "RA_trapezoid", null, "right angle trapezoid");
        p.add(button);
        group.add(button);
        button = makeAButton("rectangle", "Rectangle", null, "rectangle");
        p.add(button);
        group.add(button);
        button = makeAButton("quadrangle_square", "Square", null, "square");
        p.add(button);
        group.add(button);

        button = makeAButton("pentagon", "Pentagon", null, "pentagon");
        p.add(button);
        group.add(button);
        button = makeAButton("polygon", "Polygon", null, "polygon");
        p.add(button);
        group.add(button);
        p.setSelectedButton(button);
    }


    private ActionListener listener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            JToggleButton button = (JToggleButton) e.getSource();
            button.getModel().setSelected(false);
        }
    };

    /**
     * Creates a toggle button with the specified image, action command, tooltip text, alternate text, and an optional action listener.
     *
     * @param imageName the name of the image file (without extension) to be used as the button icon
     * @param actionCommand the action command to be set for the button
     * @param toolTipText the tooltip text to be displayed when the mouse hovers over the button
     * @param altText the alternate text to be used if the image cannot be found
     * @param t a boolean indicating whether to add an action listener to the button
     * @return the created JToggleButton
     */
    protected JToggleButton makeAButton(String imageName,
                                        String actionCommand,
                                        String toolTipText,
                                        String altText, boolean t) {
        JToggleButton button = makeAButton(imageName, actionCommand,
                toolTipText, altText);
        if (t) {
            button.addActionListener(listener);
        }
        return button;
    }

    /**
     * Creates a toggle button with the specified image, action command, tooltip text, and alternate text.
     *
     * @param imageName the name of the image file (without extension) to be used as the button icon
     * @param actionCommand the action command to be set for the button
     * @param toolTipText the tooltip text to be displayed when the mouse hovers over the button
     * @param altText the alternate text to be used if the image cannot be found
     * @return the created JToggleButton
     */
    protected JToggleButton makeAButton(String imageName,
                                        String actionCommand,
                                        String toolTipText,
                                        String altText) {
        String imgLocation = "images/" + imageName + ".gif";
        URL imageURL = GExpert.class.getResource(imgLocation);
        Icon co = null;
        if (imageURL != null) {
            co = (new ImageIcon(imageURL, altText));
        }

        JToggleButton button = new ActionButton(co);
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);

        String s2 = null;
        if (toolTipText != null) {
            s2 = getLanguageTip(toolTipText);
        }
        if (s2 != null && s2.length() != 0)
            button.setToolTipText(s2);
        else {
            if (altText != null) {
                String s3 = getLanguage(altText);
                if (s3 != null && s3.length() != 0) {
                    button.setToolTipText(s3);
                } else {
                    String s1 = getLanguage(actionCommand);
                    if (toolTipText == null && s1 != null && s1.length() != 0)
                        button.setToolTipText(s1);
                }
            }
        }
        button.addActionListener(this);

        button.setText(null);
//        button.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
        return button;
    }


    /**
     * Creates a toggle button with two icons, one for the default state and one for the selected state.
     *
     * @param imageName the name of the image file (without extension) to be used as the button icon in the default state
     * @param imageNameSelected the name of the image file (without extension) to be used as the button icon in the selected state
     * @param actionCommand the action command to be set for the button
     * @param toolTipText the tooltip text to be displayed when the mouse hovers over the button
     * @param altText the alternate text to be used if the image cannot be found
     * @return the created DActionButton with two status icons
     */
    protected JToggleButton makeAButtonWith2ICon(String imageName,
                                                 String imageNameSelected,
                                                 String actionCommand,
                                                 String toolTipText,
                                                 String altText) {

        String imgLocation = "images/" + imageName + ".gif";
        URL imageURL = GExpert.class.getResource(imgLocation);

        Icon icon1, icon2;
        icon1 = icon2 = null;

        if (imageURL != null) {
            icon1 = (new ImageIcon(imageURL, altText));
        }

        imgLocation = "images/" + imageNameSelected + ".gif";
        imageURL = GExpert.class.getResource(imgLocation);

        if (imageURL != null) {
            icon2 = (new ImageIcon(imageURL, altText));
        }

        DActionButton button = new DActionButton(icon1);
        button.set2StatusIcons(icon1, icon2);
        button.setActionCommand(actionCommand);
        button.setToolTipText(getLanguage(toolTipText));
        button.addActionListener(this);
        return button;
    }


    /**
     * This method is used to reset all button status.
     * It is used to reset all button status when the user drags a file on the window.
     */
    public void resetAllButtonStatus() {

        if (lp != null) {
            lp.clearAllTrees();
        }

        if (afpane != null)
            afpane.setVisible(false);

        if (aframe != null) {
            aframe.stopA();
        }
        if (anButton != null) {
            anButton.setEnabled(false);
        }
        restorScroll();
        pprove.clearAll();
        d.setCursor(Cursor.getDefaultCursor());
        BK1.setEnabled(true);
        BK2.setEnabled(true);
        BK3.setEnabled(true);
        BK4.setEnabled(true);
    }

    /**
     * This method is used to update the title of the window.
     * It is used to update the title of the window when the user drags a file on the window.
     */
    public void updateTitle() { // APPLET ONLY.
        if (!CMisc.isApplication())
            return;

        String s = dp.getName();
        JFrame frame = (JFrame) (Object) this;

        String v = Version.getProject();
        String d = Version.getData();

        v = this.getLanguage(v);

        if (s != null && s.length() != 0)
            frame.setTitle(s + "  -  " + v);
        else
            frame.setTitle(v);

    }

    /**
     * This method is used to restore the scroll.
     * It is used to restore the scroll when the user drags a file on the window.
     */
    public void restorScroll() {
        Rectangle rc = new Rectangle(0, 0, 0, 0);
        scroll.scrollRectToVisible(rc);
        d.setPreferredSize(new Dimension(100, 100));
        d.revalidate();
    }

    /**
     * This method is used to set the text of the label.
     * It is used to set the text of the label when the user drags a file on the window.
     */
    public void setActionTip(String name, String tip) {
        if (pprove.isProverRunning())
            return;

        label.setText(" " + name);
        if (tip != null)
            label2.setText(" " + tip);
        else
            label2.setText("");
    }

    private Timer timer;
    private int n = 4;
    private static Color fcolor = new Color(128, 0, 0);

    /**
     * This method is used to set the text of the label2.
     * It is used to set the text of the label2 when the user drags a file on the window.
     */
    public void setTextLabel2(String s, int n) {
        setTextLabel2(s);
        this.n = n;
    }

    /**
     * This method is used to set the text of the label2.
     * It is used to set the text of the label2 when the user drags a file on the window.
     */
    public void setLabelText2(String s) {
        label2.setText(" " + s);
    }

    /**
     * This method is used to stop the timer.
     * It is used to stop the timer when the user drags a file on the window.
     */
    public void stopTimer() {
        if (timer != null)
            timer.stop();
        n = 0;
        label2.setForeground(fcolor);
    }

    /**
     * This method is used to set the text of the label2.
     * It is used to set the text of the label2 when the user drags a file on the window.
     */
    public void setTextLabel2(String s) {
        label2.setText(" " + s);
        if (timer == null && s != null && s.length() != 0) {
            timer = new Timer(200, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (n % 2 == 0) {
                        label2.setForeground(fcolor);
                    } else {
                        label2.setForeground(Color.lightGray);
                    }
                    if (n == 0) {
                        timer.stop();
                        timer = null;
                        label2.setForeground(Color.black);
                    }
                    n--;

                }
            });
            n = 8;
            timer.start();
        }

    }

    /**
     * This method is used to set the text of the tip.
     * It is used to set the text of the tip when the user drags a file on the window.
     */
    public void setTipText(String text) {
        this.setTextLabel2(text);
    }


    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * This method is called when the user drops a file on the window.
     * It accepts the drop and adds the file to the text area.
     */
    public void drop(DropTargetDropEvent dtde) {
        try {
            // Ok, get the dropped object and try to figure out what it is
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                // Check for file lists specifically
                if (flavors[i].isFlavorJavaFileListType()) {
                    // Great!  Accept copy drops...
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    // And add the list of file names to our text area
                    java.util.List list = (java.util.List) tr.getTransferData(flavors[i]);

                    if (list.size() == 0)
                        continue;
                    String path = list.get(0).toString();
                    // If we made it this far, everything worked.
                    dtde.dropComplete(true);

                    // Open the target file.
                    File file = new File(path);
                    if (file.isDirectory())
                        continue;
                    this.openAFile(file);

                    //Open the first file for JGEX and return.
                    return;
                }
                // Ok, is it another Java object? Currently not implemented for this. 
                else if (flavors[i].isFlavorSerializedObjectType()) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Object o = tr.getTransferData(flavors[i]);
                    dtde.dropComplete(true);
                    return;
                }
                // How about an input stream?  Currently not implemented for this. 
                else if (flavors[i].isRepresentationClassInputStream()) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    dtde.dropComplete(true);
                    return;
                }
            }
            // Hmm, the user must not have dropped a file list
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }

    public void windowOpened(WindowEvent e) {
    }

    /**
     * This method is called when the user closes the window.
     * It asks the user if he wants to save the file before quitting.
     */
    public void windowClosing(WindowEvent e) {
        if (saveBeforeExit())
            System.exit(0);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {

    }


    /**
     * This class is used to create a popup menu with buttons.
     * It is used to create the popup menu for the buttons in the toolbar.
     */
    class JPopButtonsPanel extends JPopupMenu implements ActionListener, MouseListener, ItemListener, PopupMenuListener {
        JToggleButton button, b2, bselect;
        Vector vlist = new Vector();
        boolean entered = false;

        public JPopButtonsPanel(JToggleButton button, JToggleButton b2) {
            this.setLayout(new GridLayout(4, 3, 2, 2));
            this.button = button;
            this.b2 = b2;


            button.addActionListener(this);
            b2.addActionListener(this);
            button.addItemListener(JPopButtonsPanel.this);
            button.addMouseListener(JPopButtonsPanel.this);
            b2.addMouseListener(JPopButtonsPanel.this);
            this.addMouseListener(JPopButtonsPanel.this);
            this.addPopupMenuListener(this);
        }

        public void setSelectedButton(JToggleButton b) {
            bselect = b;
        }

        public void itemStateChanged(ItemEvent e) {
            int n = e.getStateChange();
            if (n == ItemEvent.SELECTED) {
                b2.setSelected(true);
            } else {
                b2.setSelected(false);
                b2.getModel().setRollover(false);
                button.getModel().setRollover(false);
            }
        }

        public void add(JToggleButton b) {
            b.addActionListener(this);
            vlist.add(b);
            super.add(b);
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            b2.getModel().setRollover(true);
            b2.getModel().setSelected(true);
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
            b2.getModel().setRollover(button.getModel().isRollover());
            b2.getModel().setSelected(button.getModel().isSelected());
        }


        public void actionPerformed(ActionEvent e) {
            JToggleButton bt = (JToggleButton) e.getSource();

            Object o = e.getSource();
            if (o == b2) {
                for (int i = 0; i < vlist.size(); i++) {
                    JToggleButton b = (JToggleButton) vlist.get(i);
                    if (b != bselect)
                        b.getModel().setRollover(false);
                }
                this.show(button, 0, button.getHeight());
                b2.setSelected(true);
            } else if (o == button) {
                button.setSelected(true);
                if (bselect != null)
                    GExpert.this.sendAction(bselect.getActionCommand(), bselect);
            } else {
                JToggleButton b = (JToggleButton) e.getSource();
                this.setVisible(false);
                button.setIcon(b.getIcon());
                button.setSelected(true);
                bselect = b;
            }
            bt.repaint();
        }

        public void mouseClicked(MouseEvent e) {
        }


        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            Object o = e.getSource();
            if (o == b2 || o == button) {
                b2.getModel().setRollover(true);
                button.getModel().setRollover(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Object o = e.getSource();
            if (o == b2 || o == button) {
                if (!b2.isSelected() && !button.isSelected()) {
                    b2.getModel().setRollover(false);
                    button.getModel().setRollover(false);
                }
            }
        }
    }

    /**
     * Create an ImageIcon from the path.
     * @param path the path to the image
     * @return the ImageIcon
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GExpert.class.getResource(path);
        if (imgURL == null) {
            return null;
        }
        return new ImageIcon(imgURL);
    }

    /**
     * Get the resource URL from the path.
     * @param path the path to the resource
     * @return the URL of the resource
     */
    public static URL getResourceURL(String path) {
        return GExpert.class.getResource(path);
    }

    /**
     * The application window is created and it remains active until
     * the user quits the program (via an infinite loop).
     */
    private static void createAndShowGUI() {

        Locale.setDefault(Locale.ENGLISH);

        GExpert exp = new GExpert();
        JFrame frame = (JFrame) (Object) exp;
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(1200, 900);

        frame.setLocation((int) (screenSize.getWidth() - 1200) / 2,
                (int) (screenSize.getHeight() - 900) / 2); //center
        frame.setVisible(true);

        // In case there were command line requests, let us do them:
        performCommandLineRequests(exp, true);
        // After this point we have no control on any actions automatically,
        // each action will be done by the user via the sendAction() mechanism
        // or via the GUI.
    }

    /**
     * Perform command line requests. In the array commandlineCommand there are the
     * commands requested by the user and in the array commandLineSrc the parameters for them.
     * Some commands (now it is the "Prove" command) may be done asynchronous,
     * so we cannot wait for their finish her and cannot continue with the remaining requests. Instead,
     * we will continue performing the requests later, when the asynchronous command finishes.
     *
     * @param exp          a GExpert instance
     * @param breakOnProve if the "Prove" command should be assumed as an asynchronous call
     */
    public static void performCommandLineRequests(GExpert exp, boolean breakOnProve) {
        int commandLineRequests = commandlineCommand.size();
        while (commandLineRequestsPerformed < commandLineRequests) {
            commandLineRequestsPerformed++;
            exp.sendAction(commandlineCommand.get(commandLineRequestsPerformed - 1),
                    commandlineSrc.get(commandLineRequestsPerformed - 1));
            if (breakOnProve && commandlineCommand.get(commandLineRequestsPerformed - 1).equals("Prove")) {
                return; // Continued later in GProver...
            }
        }
    }

    public static void setLookAndFeel() {

        try {
            String f = CMisc.lookAndFeel;
            if (f != null && f.length() != 0 && !f.equals("Default")) {
                UIManager.LookAndFeelInfo[] ff = UIManager.getInstalledLookAndFeels();
                for (int i = 0; i < ff.length; i++) {
                    if (ff[i].getName().equals(f)) {
                        UIManager.setLookAndFeel(ff[i].getClassName());
                        break;
                    }
                }
            }
        } catch (Exception evt) {
        }

    }

    public static ArrayList<String> commandlineCommand = new ArrayList<>();
    public static ArrayList<Object> commandlineSrc = new ArrayList<>();
    public static int commandLineRequestsPerformed = 0;

    private static void processCommandLineOptions(String[] args) {
        Options options = new Options();

        Option helpOption = new Option("h", "help", false, "show help, then exit");
        helpOption.setRequired(false);
        options.addOption(helpOption);

        Option proveOption = new Option("p", "prove", true, "prove default statement via a prover <arg> (gdd)");
        proveOption.setRequired(false);
        options.addOption(proveOption);

        Option outputOption = new Option("o", "output", true, "save GraphViz proof to file <arg>");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        Option saveOption = new Option("s", "save", true, "save to file <arg>");
        saveOption.setRequired(false);
        options.addOption(saveOption);

        Option waitOption = new Option("w", "wait", true, "wait <arg> seconds");
        waitOption.setRequired(false);
        options.addOption(waitOption);

        Option exitOption = new Option("x", "exit", false, "exit immediately");
        exitOption.setRequired(false);
        options.addOption(exitOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                formatter.printHelp("jgex [options] [input file]", options);
                System.out.println("Example: jgex -p gdd -o test.gv -x full_path_to_input.gex");
                System.out.println("Paths are relative to " + Path.of("").toAbsolutePath() + ".");
                System.out.println("Order of the given parameters is important.");
                System.exit(0);
            }
            if (cmd.getArgs().length == 0) {
                return;
            }
            if (cmd.getArgs().length > 1) {
                System.err.println("Only the first argument " + cmd.getArgList().get(0)
                        + " will be used, the rest ignored.");
            }

            // Process first argument as a file:
            String filename = cmd.getArgList().get(0);
            if (filename.endsWith(".gex")) {
                commandlineCommand.add("Open");
            } else {
                commandlineCommand.add("Import");
            }
            commandlineSrc.add(new File(filename));

            if (cmd.hasOption("p")) {
                commandlineCommand.add("Prove");
                commandlineSrc.add(cmd.getOptionValue("p"));
            }
            if (cmd.hasOption("s")) {
                commandlineCommand.add("Save");
                commandlineSrc.add(new File(cmd.getOptionValue("s")));
            }
            if (cmd.hasOption("o")) {
                commandlineCommand.add("Save GDD Proof as GraphViz File");
                commandlineSrc.add(new File(cmd.getOptionValue("o")));
            }
            if (cmd.hasOption("w")) {
                commandlineCommand.add("Wait");
                commandlineSrc.add(Integer.valueOf(cmd.getOptionValue("w")));
            }
            if (cmd.hasOption("x")) {
                commandlineCommand.add("Exit");
                commandlineSrc.add("");
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("jgex", options);
            System.exit(1);
        }
    }

    // TODO START CONVERTING HERE, this is the main method
    /**
     * Main entry point of the application.
     * Processes command line options and initializes the GUI.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Java " + Version.getNameAndVersion());
        processCommandLineOptions(args);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Checks if the application is running in a JAR file (including CheerpJ web environment).
     *
     * @return true if running from a JAR file, false otherwise.
     */
    public static boolean isRunningFromJar() {
        URL resource = GExpert.class.getResource("/wprover/GExpert.class");
        return resource != null && resource.toString().startsWith("jar:");
    }

    /**
     * Checks if the application is running in a CheerpJ web environment.
     * 
     * @return true if likely running in CheerpJ, false otherwise.
     */
    public static boolean isRunningInCheerpJ() {
        // check if running in a browser environment (CheerpJ)
        try {
            // cheerpJ sets this property
            return System.getProperty("java.vm.name", "").contains("CheerpJ") || 
                   // alternative detection method
                   (isRunningFromJar() && System.getProperty("browser", "false").equals("true"));
        } catch (Exception e) {
            // if we can't determine, assume not in CheerpJ
            return false;
        }
    }

    /**
     * Opens the specified URL in the system's default web browser or handles it appropriately
     * for the current environment (desktop or web/CheerpJ).
     *
     * @param url the URL to open.
     */
    public static void openURL(String url) {
        // check if we're running in CheerpJ/web environment
        if (isRunningInCheerpJ()) {
            try {
                // for file:/// URLs in CheerpJ, we need to handle them differently
                if (url.startsWith("file:///")) {
                    // convert file:/// URL to a relative path for resource loading
                    String relativePath = url.substring(url.indexOf("/help/"));

                    // in CheerpJ, we can use JavaScript to open the URL in a new tab/window
                    // this requires the resources to be available at the relative path from the web root
                    String jsCode = "window.open('" + relativePath + "', '_blank');";

                    // execute JavaScript via CheerpJ's JavaScript bridge
                    Class<?> jsClass = Class.forName("com.leaningtech.client.Global");
                    Method evalMethod = jsClass.getMethod("eval", String.class);
                    evalMethod.invoke(null, jsCode);
                    return;
                }

                // for regular URLs (http, https), use JavaScript to open them
                String jsCode = "window.open('" + url + "', '_blank');";
                Class<?> jsClass = Class.forName("com.leaningtech.client.Global");
                Method evalMethod = jsClass.getMethod("eval", String.class);
                evalMethod.invoke(null, jsCode);
            } catch (Exception e) {
                // fallback to showing a message with the URL if JavaScript bridge fails
                JOptionPane.showMessageDialog(null, 
                    GExpert.getTranslationViaGettext("Please open this URL in your browser: {0}", url));
            }
        } else {
            // original desktop behavior
            String osName = System.getProperty("os.name");
            try {
                if (osName.startsWith("Mac OS")) {
                    Class fileMgr = Class.forName("com.apple.eio.FileManager");
                    Method openURL = fileMgr.getDeclaredMethod("openURL",
                            new Class[]{String.class});
                    openURL.invoke(null, new Object[]{url});
                } else if (osName.startsWith("Windows")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else { //assume Unix or Linux
                    String[] browsers = {
                            "firefox", "opera", "konqueror", "epiphany",
                            "mozilla", "netscape"};
                    String browser = null;
                    for (int count = 0; count < browsers.length && browser == null;
                         count++) {
                        if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                            browser = browsers[count];
                        }
                    }
                    if (browser == null) {
                        throw new Exception("Could not find web browser");
                    } else {
                        Runtime.getRuntime().exec(new String[]{browser, url});
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, GExpert.getTranslationViaGettext("Can not open link {0}", url) + "\n" +
                        e.getMessage());
            }
        }
    }

    /**
     * Saves the current view as a PDF file.
     * Prompts the user to choose a file and generates the PDF output.
     */
    public void saveAsPDF() {
        if (!need_save())
            return;

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;

                String s = f.getName();
                if (s.endsWith("pdf") || s.endsWith("PDF"))
                    return true;
                return false;
            }

            public String getDescription() {
                return "Adobe PDF File (*.pdf)";
            }
        });
        String dr = getUserDir();
        chooser.setCurrentDirectory(new File(dr));
        int n = chooser.showOpenDialog(this);
        if (n != JFileChooser.OPEN_DIALOG)
            return;

        try {
            File file = chooser.getSelectedFile();
            String path = file.getPath();
            if (path.endsWith("PDF") || path.endsWith("pdf")) {
            } else {
                file = new File(path + ".pdf");
            }
            if (file.exists()) {
                int n2 = JOptionPane.showConfirmDialog(this,
                        getTranslationViaGettext("{0} already exists, do you want to overwrite it?", file.getName()),
                        "File Exists", JOptionPane.YES_NO_CANCEL_OPTION);
                if (n2 != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);


            Graphics pdfGraphics = null;
            PDFJob job = new PDFJob(fileOutputStream);
            pdfGraphics = job.getGraphics();
            d.paintAll(pdfGraphics);
            pdfGraphics.dispose();
            job.end();
            fileOutputStream.close();
        } catch (IOException ee) {
            JOptionPane.showMessageDialog(this, ee.getMessage());
        }

    }

    /**
     * Handles key typed events.
     *
     * @param e the key event.
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Handles key pressed events.
     *
     * @param e the key event.
     */
    public void keyPressed(KeyEvent e) {
        int k = 0;
    }

    /**
     * Handles key released events.
     *
     * @param e the key event.
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * This class is used to group buttons in a button group.
     * It allows for easy retrieval of buttons by their action command.
     */
    class Group extends ButtonGroup {
        public Group() {
            super();
        }

        public AbstractButton getButton(String s) {
            if (s == null || s.length() == 0)
                return null;

            int n = buttons.size();
            for (int i = 0; i < buttons.size(); i++) {
                AbstractButton b = (AbstractButton) buttons.get(i);
                String s1 = b.getActionCommand();
                if (s.equals(s1))
                    return b;
            }
            return null;
        }
    }

    /**
     * Sets the default UI font for all components.
     *
     * @param f the FontUIResource to be used.
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {

        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    /**
     * Starts or initializes the component.
     */
    public void start() {
    }

    /**
     * Stops or cleans up the component.
     */
    public void stop() {
    }

    /**
     * Updates the action pool using the currently selected list of objects.
     *
     * @param n the pool identifier to update.
     */
    public void updateActionPool(int n) {
        Vector v = dp.getSelectList();
        int nx = vpoolist.size();

        if (v.size() != 0) {
            for (int i = 0; i < v.size() && i < nx; i++) {
                OPoolabel lb = (OPoolabel) vpoolist.get(i);
                lb.setObject((CClass) v.get(i));
            }
        } else setActionPool(n);

    }

    /**
     * Configures the action pool based on the specified pool type.
     *
     * @param a the pool type identifier.
     */
    public void setActionPool(int a) {
        int n = dp.getPooln(a);
        int sz = vpoolist.size();
        if (n <= 0) {
            for (int i = 0; i < sz; i++) {
                JLabel label = (JLabel) vpoolist.get(i);
                label.setVisible(false);
            }
        }

        for (int i = 0; i < n; i++) {
            if (i < sz) {
                OPoolabel lb = (OPoolabel) vpoolist.get(i);
                lb.setType(dp.getPoolA(a, i + 1));
                lb.setVisible(true);
            } else {
                OPoolabel lb = new OPoolabel();
                vpoolist.add(lb);
                tipanel.add(lb);
                lb.setType(dp.getPoolA(a, i + 1));
            }
        }

        if (n > 0 && n < sz) {
            for (int i = n; i < sz; i++) {
                OPoolabel lb = (OPoolabel) vpoolist.get(i);
                lb.setVisible(false);
            }
        }
    }

    /**
     * This class represents a label in the action pool.
     * It displays the type of object and allows for mouse interaction.
     */
    class OPoolabel extends JLabel implements MouseListener {
        private int otype = -1;
        private Object obj;
        private Color bc = this.getForeground();


        public OPoolabel() {
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            Font f = this.getFont();
            f = new Font(f.getName(), Font.BOLD, f.getSize());
            this.setFont(f);
        }

        public void setType(int t) {
            otype = t;

            if (t == 1) {
                this.setText("P ");
                this.setToolTipText("Point");
            } else if (t == 2) {
                this.setText("L ");
                this.setToolTipText("Line");
            } else if (t == 3) {
                this.setText("C ");
                this.setToolTipText("Circle");
            } else if (t == 4) {
                this.setText("LC");
                this.setToolTipText("Line or Circle");
            } else {
                this.setText("?");
                this.setToolTipText("Anything");
            }
            this.setForeground(bc);
        }

        public void setObject(CClass cc) {
            if (cc == null /*|| !(cc instanceof CPoint)*/) {
                return;
            }
            String na = cc.getname();
            if (na == null)
                na = " ";
            if (na.length() == 1)
                na += " ";
            setText(na);
            obj = cc;
            this.setToolTipText(cc.TypeString());
            this.setForeground(new Color(0, 128, 192));
        }

        public void clear() {
            this.setText("");
            this.setToolTipText("");
            obj = null;
        }

        public void mouseClicked(MouseEvent e) {
            if (obj != null) {
                GExpert.this.dp.setObjectListForFlash((CClass) obj);
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
     * Cancels the current action triggered by key events.
     */
    public void onKeyCancel() {
        dp.cancelCurrentAction();
        //this.setActionMove();
//        closeAllDialogs();
    }

    /**
     * Initializes the key mapping by registering a KeyEventPostProcessor.
     */
    public void initKeyMap() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(new KeyProcessor());
    }

    public static long HotKeyTimer = 0;

    /**
     * This class processes key events and performs actions based on the key pressed.
     */
    class KeyProcessor implements KeyEventPostProcessor {
        public boolean postProcessKeyEvent(KeyEvent event) {
            int key = event.getKeyCode();
            long t = System.currentTimeMillis() - HotKeyTimer;
            HotKeyTimer = System.currentTimeMillis();
            if (t < 100)   // Capture in case it run to fast
                return true;

            switch (key) {
                case KeyEvent.VK_ESCAPE:
                    onKeyCancel();
                    break;
                case KeyEvent.VK_S:
                    if (event.isShiftDown())
                        dp.stateChange();
                    break;
                case KeyEvent.VK_Z:
                    if (event.isShiftDown() && event.isControlDown())
                        dp.redo_step();
                    else if (event.isControlDown())
                        dp.Undo_step();
                    d.repaint();
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_EQUALS:
                    if (event.isControlDown()) {
                        dp.zoom_in(d.getWidth() / 2, d.getHeight() / 2, 1);
                        d.repaint();
                    }
                    break;
                case KeyEvent.VK_MINUS:
                    if (event.isControlDown()) {
                        dp.zoom_out(d.getWidth() / 2, d.getHeight() / 2, 1);
                        d.repaint();
                    }
                    break;
                case KeyEvent.VK_G:
                    if (event.isControlDown()) {
                        dp.DRAWGRID = !dp.DRAWGRID;
                        d.repaint();
                    }
                    break;
                case KeyEvent.VK_D:  // For ndgs.
                    if (event.isAltDown()) {
                        dp.printNDGS();
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
    }


}

/// /////////////////////////////////////////////
/// /// End of GExpert.java.
/// ////////////////////////////////////////////

/**
 * This class is used to create a button with two icons, one for the selected state and one for the unselected state.
 * It is used in the GExpert application to create buttons with different icons depending on their state.
 */
class DActionButton extends ActionButton {
    private Icon ico1, ico2;

    public DActionButton(Icon co) {
        super(co);
    }

    /**
     * Sets the icons for the two status states of the button.
     *
     * @param ico1 the icon for the unselected state
     * @param ico2 the icon for the selected state
     */
    public void set2StatusIcons(Icon ico1, Icon ico2) {
        this.ico1 = ico1;
        this.ico2 = ico2;
    }

    /**
     * Sets the selected state of the button and updates the icon accordingly.
     *
     * @param b true if the button is selected, false otherwise
     */
    public void setSelected(boolean b) {
        super.setSelected(b);
        if (b) {
            this.setIcon(ico2);
        } else {
            this.setIcon(ico1);
        }
    }

    /**
     * Enables or disables the button and sets the icon to the unselected state.
     *
     * @param b true to enable the button, false to disable it
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        this.setIcon(ico1);
        this.setSelected(false);
    }
}

/**
 * This class is used to create a button with a custom UI. It is used in the GExpert application to create buttons
 * with a specific look and feel.
 */
class ActionButton extends JToggleButton {

    private static EntityButtonUI ui = new EntityButtonUI();
    Dimension dm;

    public ActionButton(Icon co) {
        super(co);
        setRolloverEnabled(true);
        this.setOpaque(false);
        this.setUI(ui);
        dm = new Dimension(32, 28);
    }

    public Dimension getPreferredSize() {
        return dm;
    }

    public Dimension getMaximumSize() {
        return dm;
    }
}

/**
 * This class is used to create a button with two states (selected and unselected) and two icons. It is used in the
 * GExpert application to create buttons with different icons depending on their state.
 */
class TStateButton extends JToggleButton {
    ImageIcon icon1, icon2;

    public TStateButton(ImageIcon m1, ImageIcon m2) {
        super(m1, false);

        icon1 = m1;
        icon2 = m2;
    }

    public void setSelected(boolean b) {
        if (b) {
            this.setIcon(icon2);
        } else {
            this.setIcon(icon1);
        }

        super.setSelected(b);
    }
}
