package gprover;

import java.io.*;
import java.util.Vector;

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
            String dr = user_directory + sp + "examples";
            File file = new File(dr);

            Vector vm = new Vector();
            readThems(file, vm);
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
        } catch (IOException ee) {
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
    static void readThems(File file, Vector v) throws IOException {
        File[] sf = file.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String nm = pathname.getName();
                return !(nm.contains("."));
            }
        });
        for (int i = 0; i < sf.length; i++) {
            if (sf[i].isDirectory())
                readThems(sf[i], v);
            else {
                BufferedReader in = new BufferedReader(new FileReader(sf[i]));

                while (true) {
                    GTerm tm = new GTerm();
                    if (!tm.readAterm(in)) break;
                    tm.setName(sf[i].getName());
                    v.add(tm);
                }
                in.close();
            }
        }
    }
}