package wprover;

import java.io.*;
import java.awt.*;
import java.util.Locale;

public class Language {

    private static int MAX_LEN = 500;

    private static Language laninstance = null;

    private String stype;
    private Font font;
    private lnode[] ndlist = new lnode[MAX_LEN];

    public static void setLanguage(Language lan) {
        laninstance = lan;
    }

    public boolean isEnglish() {
        return "English".equalsIgnoreCase(stype);
    }

    public Font getFont() {
        return font;
    }

    public String getName() {
        return stype;
    }

    class lnode {
        public int index;
        String en;
        String tx;
        String tip;

        public lnode(int n, String e, String t1, String t2) {
            index = n;
            en = e;
            tx = t1;
            tip = t2;
        }

        public lnode(String s) {
            index = -1;
            tip = s;
        }

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

        public void writeen(OutputStreamWriter out) throws IOException {
            out.write(index + " \t#\t");
            out.write(en);
            out.write("\n");
        }

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


    public static String getLs(String s) {
        if (laninstance == null)
            return s;
        return laninstance.getString(s);
    }

    public static String getLs1(String s) {
        if (laninstance == null)
            return s;
        return laninstance.getString1(s);
    }


    public OutputStreamWriter outputBlank(File f) throws IOException {
        String path = f.getPath();
        String name = f.getName();

        String n = path + "1";
        File f1 = new File(n);
        f1.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f1), "UNICODE");//UNICODE,UTF-16
        return writer;
    }

    public void writeFile(File f) {
        try {
            OutputStreamWriter writer = this.outputBlank(f);
            writer.write("Font: " + font.getFontName() + " # " + font.getStyle() + " # " + font.getSize());
            for (int i = 0; i < ndlist.length; i++) {
                if (ndlist[i] == null)
                    break;
                lnode ln = ndlist[i];
                ln.write(writer);

            }
            writer.close();


        } catch (IOException ee) {
        }


    }


    public void writeOut(OutputStreamWriter writer, String[] ss) throws IOException {
        if (ss.length == 0)
            writer.write("\n");
        else {
            for (int i = 0; i < ss.length; i++) {
                writer.write(ss[i].trim());
                if (i != ss.length - 1)
                    writer.write(" #\t");
            }
            writer.write("\n");
        }
    }

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
