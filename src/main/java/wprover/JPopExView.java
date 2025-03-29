package wprover;

import java.io.IOException;

/**
 * JPopExView is a class that represents a pop-up view for displaying rules in the GExpert application.
 * It extends the JBaseDialog class and provides functionality to load and display rules.
 */
public class JPopExView extends JBaseDialog {
    GExpert gxInstance;
    JExPanel panel;

    /**
     * Constructs a new JPopExView with the specified GExpert instance.
     *
     * @param exp the GExpert instance to associate with this JPopExView
     */
    public JPopExView(GExpert exp) {
        super(exp.getFrame());
        gxInstance = exp;
        this.setSize(600, 400);
        panel = new JExPanel();
        this.add(panel);
    }

    /**
     * Loads a rule from the specified file name and displays it in the panel.
     *
     * @param s the name of the rule file to load
     * @return true if the rule was loaded successfully, false otherwise
     */
    public boolean loadRule(String s) {
        this.setTitle(s);

        String f = GExpert.getUserDir();
        String sp = GExpert.getFileSeparator();

        DrawProcess dp = new DrawProcess();
        dp.clearAll();
        try {
            boolean ss = dp.Load(f + sp + "rules" + sp + s);
            panel.setdrawP(dp);
        } catch (IOException ee) {
            CMisc.eprint(panel, "can not load rule: " + sp);
            return false;
        }
        return true;
    }


}
