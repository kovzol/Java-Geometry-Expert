package gprover;

import javax.swing.*;
import java.io.*;


/**
 * Main entry point for the application.
 *
 * <p>
 * This class demonstrates file selection from a specific directory, reading data using a custom term reader,
 * initializing a database, processing the example, computing a fixpoint and displaying a full proof.
 * The file chooser is set to the "examples" directory relative to the user's current working directory.
 * If the file selection is cancelled, the application exits.
 * </p>
 */
@Deprecated
public class Main2 {
    /**
     * Main method to run the application.
     *
     * @param args Command line arguments (not used).
     */
    @Deprecated
    public static void main(String[] args) {

        // Get the user's current directory.
        String user_directory = System.getProperty("user.dir");
        // Get the file separator specific to the operating system.
        String sp = File.separator;
        // Construct the path to the "examples" directory.
        String dr = user_directory + sp + "examples";

        // Initialize a file chooser with the current directory set to the examples folder.
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(dr));
        int result = chooser.showOpenDialog(null);

        // Exit if the file chooser is cancelled.
        if (result == JFileChooser.CANCEL_OPTION) return;

        // Create an instance of GTerm to process the selected file.
        GTerm gt = new GTerm();
        try {
            // Read the term from the selected file.
            gt.readAterm(new BufferedReader(new FileReader(chooser.getSelectedFile())));

            // Initialize database and process the example.
            GDDBc db = new GDDBc();
            db.init_dbase();
            db.setExample(gt);
            db.sbase();
            db.fixpoint();
            db.show_fproof();

        } catch (IOException ee) {
            // Print any IOExceptions encountered during file reading or processing.
            System.err.println("IOException " + ee.toString());
        }
    }
}
