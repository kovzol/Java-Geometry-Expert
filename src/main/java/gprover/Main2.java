package gprover;

import javax.swing.*;
import java.io.*;

/**
 * Represents a geometric line in the construction.
 */
public class Main2 {
    public static void main(String[] args) {

        String user_directory = System.getProperty("user.dir");
        String sp = File.separator;
        String dr = user_directory + sp + "examples";
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(dr));
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) return;
        GTerm gt = new GTerm();
        try {
            gt.readAterm(new BufferedReader(new FileReader(chooser.getSelectedFile())));
            GDDBc db = new GDDBc();
            db.init_dbase();

            db.setExample(gt);
            db.sbase();

            db.fixpoint();

            db.show_fproof();

        } catch (IOException ee) {
            System.err.println("IOException " + ee.toString());
        }
    }
}
