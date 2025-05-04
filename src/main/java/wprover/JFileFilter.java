package wprover;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * A file filter for use with JFileChooser that filters files based on a specified suffix.
 */
public class JFileFilter extends FileFilter {
    /**
     * The suffix to filter files by.
     */
    String endfix;

    /**
     * An additional integer field, not used in this implementation.
     */
    int dep = 0;

    /**
     * The uppercase version of the suffix to filter files by.
     */
    private String endfix1;

    /**
     * Constructs a new JFileFilter with the specified suffix.
     *
     * @param s the suffix to filter files by
     */
    public JFileFilter(String s) {
        if (s == null || s.length() == 0) return;
        endfix = s;
        endfix1 = endfix.toUpperCase();
    }

    /**
     * Determines whether the specified file should be accepted by this filter.
     *
     * @param f the file to test
     * @return true if the file is a directory or ends with the specified suffix, false otherwise
     */
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        String s = f.getName();
        s = s.toUpperCase();
        return s.endsWith(endfix) || s.endsWith(endfix1);
    }

    /**
     * Returns the description of this filter.
     *
     * @return the suffix used by this filter
     */
    public String getDescription() {
        return endfix;
    }
}