package wprover;

import java.io.*;
import java.awt.*;
import java.util.Locale;

/**
 * Language class for managing language-specific strings and font settings.
 * This class provides methods to retrieve localized strings, write language
 * files, and manage font settings.
 */
public class Language {

    private static int MAX_LEN = 500;

    private static Language laninstance = null;

    private String stype;
    private Font font;
    private lnode[] ndlist = new lnode[MAX_LEN];

    /**
     * Sets the current language instance.
     *
     * @param lan the Language instance to set
     */
    public static void setLanguage(Language lan) {
        laninstance = lan;
    }

    /**
     * Checks if the current language is English.
     *
     * @return true if the current language is English, false otherwise
     */
    public boolean isEnglish() {
        return "English".equalsIgnoreCase(stype);
    }

    /**
     * Gets the font associated with the current language.
     *
     * @return the Font associated with the current language
     */
    public Font getFont() {
        return font;
    }

    /**
     * Gets the name of the current language.
     *
     * @return the name of the current language
     */
    public String getName() {
        return stype;
    }

    /**
     * Represents a language node containing index, English text, translated text, and tooltip.
     */
    class lnode {
        public int index;
        String en;
        String tx;
        String tip;

        /**
         * Writes the language node to the specified OutputStreamWriter.
         *
         * @param out the OutputStreamWriter to write to
         * @throws IOException if an I/O error occurs
         */
        public void write(OutputStreamWriter out) throws IOException {
            if (index >= 0) {
                String s = index + " \t#\t" + en + " \t#\t" + tx;
                out.write(s);
                if (tip != null) {
                    out.write(" #\t" + tip);
                }
            } else
                out.write(tip + "\n");
            out.write("\n");
        }

        /**
         * Combines the current language node with another language node.
         *
         * @param ln the language node to combine with
         */
        public void combine(lnode ln) {
            if (index < 0)
                return;

            if (ln == null) {
                tx = tip = null;
                return;
            }

            en = ln.en;
            tx = ln.tx;
            tip = ln.tip;
        }
    }

    /**
     * Gets the translated string for the specified English string.
     *
     * @param s the English string to translate
     * @return the translated string, or the original string if not found
     */
    public String getString(String s) {
        for (int i = 0; i < MAX_LEN; i++) {
            lnode ln = ndlist[i];
            if (ln == null)
                break;
            if (ln.en.equalsIgnoreCase(s))
                return ln.tx;
        }
        return s;
    }

    /**
     * Gets the tooltip for the specified English string.
     *
     * @param s the English string to get the tooltip for
     * @return the tooltip, or an empty string if not found
     */
    public String getString1(String s) {
        for (int i = 0; i < MAX_LEN; i++) {
            lnode ln = ndlist[i];
            if (ln == null)
                break;
            if (ln.en.equalsIgnoreCase(s))
                return ln.tip;
        }
        return "";
    }

    /**
     * Gets the English string for the specified translated string.
     *
     * @param s the translated string to get the English string for
     * @return the English string, or the original string if not found
     */
    public String getEnglish(String s) {
        if (stype == null)
            return GExpert.getLanguage(s);
        if (stype.equalsIgnoreCase("English"))
            return s;
        for (int i = 0; i < MAX_LEN; i++) {
            lnode ln = ndlist[i];
            if (ln == null)
                break;

            if (ln.tx.equalsIgnoreCase(s))
                return ln.en;
        }
        return s;
    }

    /**
     * Gets the localized string for the specified string.
     *
     * @param s the string to localize
     * @return the localized string, or the original string if not found
     */
    public static String getLs(String s) {
        if (laninstance == null)
            return s;
        return laninstance.getString(s);
    }

    /**
     * Gets the language node with the specified index.
     *
     * @param n the index of the language node to get
     * @return the language node with the specified index, or null if not found
     */
    public lnode getlnode(int n) {
        for (int i = 0; i < ndlist.length; i++) {
            if (ndlist[i] == null)
                break;
            lnode ln = ndlist[i];
            if (ln.index == n)
                return ln;
        }
        return null;
    }

    /**
     * Combines the current language with another language.
     *
     * @param lan the Language instance to combine with
     */
    public void combine(Language lan) {
        for (int i = 0; i < ndlist.length; i++) {
            if (ndlist[i] == null)
                break;
            lnode ln = ndlist[i];
            lnode ln1 = lan.getlnode(ln.index);
            ln.combine(ln1);
        }
    }
}
