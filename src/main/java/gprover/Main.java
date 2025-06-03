package gprover;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The main class for the GProver application.
 * This class contains the main method and handles the reading of geometric terms,
 * processing them, and outputting the results.
 */
@Deprecated
public class Main {

    /**
     * The main method that serves as the entry point of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        main1(args);
    }

    /**
     * The main logic of the application.
     * Reads geometric terms from files, processes them, and outputs the results.
     *
     * @param args the command line arguments
     */
    public static void main1(String[] args) {
        try {
            String user_directory = System.getProperty("user.dir");
            String sp = File.separator;
            // examples are displayed through this method
            String dr = user_directory + sp + "examples";

            Vector vm = new Vector();
            readThems(dr, vm);
            for (int id = 0; id < vm.size(); id++) {
                GTerm gt = (GTerm) vm.get(id);
                System.out.print(id + " : " + gt.getName() + "\t\t");
                if (id % 4 == 0)
                    Cm.print("\n");
            }

            int t = 0;
            int f = 0;
            int n = 0;
            Cm.print("\n\n************************\n");

            Vector tlist = new Vector();
            long t1 = System.currentTimeMillis();

            for (int id = 0; id < vm.size(); id++) {
                GDDBc db = new GDDBc();
                db.init_dbase();

                GTerm gt = (GTerm) vm.get(id);
                db.setExample(gt);

                db.sbase();
                db.fixpoint();

                if (gt.getConc().pred == 0) {
                    n++;
                    System.out.print(id + " : " + gt.getName() + "\t\tNO\n");
                } else if (db.docc()) {
                    t++;
                    System.out.print(id + " : " + gt.getName() + "\t\ttrue\n");
                    tlist.add(gt);

                    FileOutputStream fp;
                    String drec = (dr + sp + "proved");

                    // Create folder if it does not exist:
                    new File(drec).mkdirs();

                    File ff = new File(drec + sp + gt.getName() + ".rtf");

                    if (ff.exists()) {
                        ff.delete();
                        fp = new FileOutputStream(ff, true);
                    } else {
                        ff.createNewFile();
                        fp = new FileOutputStream(ff, false);
                    }
                    if (fp == null) return;
                    DataOutputStream out = new DataOutputStream(fp);
                    gt.Save(out);

                    out.close();
                } else {
                    f++;
                    System.out.print(id + " : " + gt.getName() + "\t\tfalse\n");
                }
            }
            Cm.print("Total = " + vm.size() + ";  t =  " + t + ",  f = " + f + ", n = " + n);
            long t2 = System.currentTimeMillis();
            Cm.print("Time = " + (t2 - t1) / 1000.0);
        } catch (IOException | URISyntaxException ee) {
            System.err.println("IOException: " + ee);
        }
    }

    /**
     * Reads geometric terms from files and adds them to a vector.
     *
     * @param file the directory containing the files
     * @param v the vector to store the geometric terms
     * @throws IOException if an I/O error occurs
     */
    /**
     * Recursively read all “.gex”‐style term files from a resource directory on the classpath.
     */
    static void readThems(String resourceDir, Vector<GTerm> v) throws IOException, URISyntaxException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL dirUrl = cl.getResource(resourceDir + "/");
        if (dirUrl == null) return;

        if (dirUrl.getProtocol().equals("file")) {
            // running in IDE or exploded build
            Path folder = Paths.get(dirUrl.toURI());
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
                for (Path p : ds) {
                    String nm = p.getFileName().toString();
                    if (Files.isDirectory(p)) {
                        readThems(resourceDir + "/" + nm, v);
                    } else {
                        loadTerms(cl.getResourceAsStream(resourceDir + "/" + nm), nm, v);
                    }
                }
            }
        } else if (dirUrl.getProtocol().equals("jar")) {
            // running from JAR
            String path = dirUrl.getPath();
            String jarPath = path.substring(path.indexOf("file:" ) + 5, path.indexOf("!"));
            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                String prefix = resourceDir + "/";
                Enumeration<JarEntry> en = jar.entries();
                while (en.hasMoreElements()) {
                    JarEntry entry = en.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith(prefix) || entry.isDirectory()) continue;
                    String rel = name.substring(prefix.length());
                    if (rel.contains("/")) {
                        String sub = rel.substring(0, rel.indexOf('/'));
                        readThems(resourceDir + "/" + sub, v);
                    } else {
                        loadTerms(cl.getResourceAsStream(name), rel, v);
                    }
                }
            }
        }
    }

    private static void loadTerms(InputStream is, String fileName, Vector<GTerm> v) throws IOException {
        if (is == null) return;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            while (true) {
                GTerm tm = new GTerm();
                if (!tm.readAterm(in)) break;
                tm.setName(fileName);
                v.add(tm);
            }
        }
    }

}