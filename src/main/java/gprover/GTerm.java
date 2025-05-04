package gprover;

import javax.swing.*;
import java.io.*;
import java.util.Vector;

/**
 * JGEX supports loading and saving scripts in a simple textual format.
 * Among the examples, these files have no extension (see, e.g. 3_JAR/simson).
 * This class can load and save them.
 * * Represents geometric constructions and their associated operations.
 * *
 * * <p>This class encapsulates the management of points, construction steps,
 * * and constraints within a geometric theorem proving framework.
 * * It provides methods for adding, retrieving, and processing geometric data,
 * * including the generation of non-degenerate and prerequisite constraints.
 * * Fundamental operations include loading, saving, and validating the structure
 * * of geometric constructions.</p>
 * *
 * * <p>Utilize this class to perform geometric analyses and manipulations necessary
 * * for automated theorem proving and geometric constraint solving within the
 * * framework.</p>
 */
public class GTerm {

    // position of all points in the current construction, collected in a single String
    private String posString = "";
    private String aniString = "";

    // array of points in the current construction
    private Vector gpoints = new Vector();
    // array of construction steps of the current construction
    private Vector gcons = new Vector();
    public Cond conc = new Cond();

    // name of construction
    private String name = null;
    private boolean generated = false;
    private boolean is_position_set = false;

    private Vector ccons = new Vector();
    private Vector ncons = new Vector();

    /**
     * Constructs a new GTerm instance.
     */
    public GTerm() {
    }

    /**
     * Returns the constraint at the specified index from the constraint vector.
     *
     * @param n the 0-based index of the constraint to retrieve
     * @return the constraint at index n, or null if n is out of range
     */
    public Cons getCons(int n) {
        int n1 = gcons.size();
        if (n < 0 || n >= n1)
            return null;
        return (Cons) gcons.get(n);
    }

    /**
     * Returns a vector containing non degenerate constraints.
     *
     * @return a vector of CNdg objects representing non degenerate constraints
     */
    public Vector getNcons() {
        Vector v = new Vector();

        for (int i = 0; i < ncons.size(); i++) {
            Cons c = (Cons) ncons.get(i);
            int t = 0;
            if (c.type == Gib.CO_COLL)
                t = Gib.NDG_COLL;
            else if (c.type == Gib.CO_PERP)
                t = Gib.NDG_PERP;
            else t = c.type;

            CNdg d = new CNdg();
            d.type = t;
            d.addAllPt(c.ps);
            v.add(d);
            generateSd(d, c.pss);
        }
        return v;
    }

    /**
     * Generates a string description for the specified CNdg object based on the provided parameters.
     *
     * @param dg  the CNdg object to update with its description
     * @param pss the array of parameters used for generating the description
     */
    public void generateSd(CNdg dg, Object[] pss) {
        switch (dg.type) {
            case Gib.NDG_COLL:
                dg.sd = pss[0] + ", " + pss[1] + ", " + pss[2] + " is not collinear";
                break;
            case Gib.NDG_NON_ISOTROPIC:
                dg.sd = pss[0] + "" + pss[1] + " is non-isotropic";
                break;
            case Gib.NDG_PARA:
                dg.sd = pss[0] + "" + pss[1] + " is not parallel to " + pss[2] + "" + pss[3];
                break;
            case Gib.NDG_PERP:
                dg.sd = pss[0] + "" + pss[1] + " is not perp to " + pss[2] + "" + pss[3];
                break;
        }

    }

    /**
     * Returns the vector containing all constraints.
     *
     * @return the vector of constraint objects
     */
    public Vector getCons() {
        return gcons;
    }

    /**
     * Copies all constraints into the given array, starting at index 1.
     *
     * @param cn the array in which to store the constraint objects
     * @return the number of constraints copied
     */
    public int setAllcons(Cons[] cn) {
        for (int i = 0; i < gcons.size(); i++) {
            Cons c = (Cons) gcons.get(i);
            cn[i + 1] = c;
        }
        return gcons.size();
    }

    /**
     * Copies all points into the given array, starting at index 1.
     *
     * @param pp the array in which to store the point objects
     * @return the number of points copied
     */
    public int setAllpts(ProPoint[] pp) {
        for (int i = 0; i < gpoints.size(); i++) {
            ProPoint p = (ProPoint) gpoints.get(i);
            pp[i + 1] = p;
        }
        return gpoints.size();
    }

    /**
     * Returns the number of points in the construction.
     *
     * @return the number of points
     */
    public int getPointsNum() {
        return gpoints.size();
    }

    /**
     * Returns a vector containing the names of all points.
     *
     * @return a vector of point names
     */
    public Vector getAllptsText() {

        Vector v = new Vector();
        for (int i = 0; i < gpoints.size(); i++) {
            ProPoint p = (ProPoint) gpoints.get(i);
            v.add(p.name);
        }
        return v;
    }

    /**
     * Returns the number of constraints (as derived from the points vector).
     *
     * @return the number of constraints
     */
    public int getCons_no() {
        return gpoints.size();
    }

    /**
     * Returns the current conclusion condition.
     *
     * @return the Cond object representing the conclusion
     */
    public Cond getConc() {
        return conc;
    }

    /**
     * Retrieves the conclusion constraint if it exists.
     *
     * @return the conclusion constraint, or null if none exists
     */
    public Cons getConclusion() {
        int s = gcons.size();
        if (s == 0) return null;
        Cons c = (Cons) gcons.get(s - 1);
        if (c.is_conc())
            return c;
        return null;
    }

    /**
     * Sets the conclusion constraint and updates the underlying condition.
     *
     * @param c the conclusion constraint to set
     * @return true if the conclusion was set successfully, false otherwise
     */
    public boolean setConclusion(Cons c) {

        int s = gcons.size();
        if (s == 0) return false;
        Cons c1 = (Cons) gcons.get(s - 1);
        if (c1.is_conc())
            gcons.remove(c1);
        gcons.add(c);


        if (conc == null)
            conc = new Cond();
        conc.pred = c.type;
        int i = 0;
        while (i < c.pss.length) {
            if (c.pss[i] != null) {
                String xs = c.pss[i].toString();
                int xt = this.findPt(xs);
                if (xt == 0 && isStringTypeInt(xs))
                    conc.p[i] = Integer.parseInt(xs);
                else
                    conc.p[i] = xt;
            } else
                break;
            i++;
        }

        if (conc.pred == Gib.CO_CYCLIC) {
            for (i = 0; i < 4; i++)
                conc.p[i + 1] = conc.p[i];
        }

        return true;
    }

    /**
     * Returns the name of the construction.
     *
     * @return the construction name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the construction.
     *
     * @param s the name to set
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * Reads an A-term from the provided BufferedReader.
     *
     * @param in the BufferedReader to read from
     * @return true if the term is successfully read; false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean readAterm(BufferedReader in) throws IOException {
        Vector glines = new Vector();
        int status = 0;

        while (true) {
            String ln = in.readLine();
            if (ln == null) {
                if (status == 1 || status == 2) {
                    break;
                }

                if (glines.size() == 0) {
                    return false;
                } else {
                    break;
                }
            } else if (ln.length() == 0) {
                if (status == 1 || status == 2) {
                    break;
                }
                continue;
            }

            ln = ln.trim();
            if (ln.replaceAll(" ", "").replaceAll("\n", "").length() == 0) {
                if (status == 1 || status == 2) {
                    break;
                }
                continue;
            }
            ln = ln.replaceAll("  ", " ");
            if (ln.startsWith("#")) {
                if (status == 2) {
                    break;
                }
                continue;
            }
            if (status == 0) {
                String s = ln.toUpperCase();
                if (s.startsWith("EXAMPLE")) {
                    String[] list = s.split(" ");
                    if (list.length != 2) {
                        Cm.print("Head Format Error");
                        return false;
                    }
                    this.name = list[1].trim();
                } else if (s.startsWith("HYPOTHESES"))
                    status = 1;
                else
                    return false;
            } else if (status == 1) {
                glines.add(ln);
                if (ln.toUpperCase().startsWith("SHOW")) {
                    status = 2;
                }
            } else if (status == 2) {
                if (ln.startsWith("ANI")) {
                    aniString = ln;
                } else {
                    posString += ln;
                }
            }
        }

        this.generate(glines);
        ge_cpt();
        return true;
    }

    /**
     * Writes the current A-term to the specified FileOutputStream.
     *
     * @param out the FileOutputStream to write to
     * @return true if the A-term is successfully written; false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean writeAterm(FileOutputStream out) throws IOException {
        String sn = name;
        if (sn == null || sn.length() == 0)
            sn = "THM";
        sn = "EXAMPLE " + sn + "\nHYPOTHESES: \n";
        out.write(sn.getBytes());

        for (int i = 0; i < gcons.size(); i++) {
            Cons c = (Cons) gcons.get(i);
            out.write(c.toString().getBytes());
            out.write("\n".getBytes());
        }
        if (this.getConclusion() == null)
            out.write("SHOW: NO\n".getBytes());
        return true;
    }

    /**
     * Reads an A-term from the given DataInputStream.
     *
     * @param in the DataInputStream to read from
     * @return true if the A-term is successfully read; false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean readAterm(DataInputStream in) throws IOException {
        int n = in.readInt();
        Vector glines = new Vector();
        for (int i = 0; i < n; i++) {
            int m = in.readInt();
            byte[] b = new byte[m];
            in.read(b);
            String s = new String(b);
            glines.add(s);
        }
        generate(glines);
        return true;
    }

    /**
     * Writes the current A-term to the provided DataOutputStream using an alternative format.
     *
     * @param out the DataOutputStream to write to
     * @return true if the A-term is successfully written; false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean writeAterm2(DataOutputStream out) throws IOException {
        int n = gcons.size();
        out.writeInt(n + 2);
        String st = "EXAMPLE ";
        if (name == null)
            st += "THM";
        else
            st += name;

        out.writeUTF(st);
        st = "HYPOTHESES:";
        out.writeUTF(st);

        for (int i = 0; i < gcons.size(); i++) {
            Cons at = (Cons) gcons.get(i);
            String s = at.toStringEx();
            if (!s.endsWith(";"))
                s += ";";
            out.writeUTF(s);
        }
        return true;
    }

    /**
     * Adds a new point with the specified name if it does not already exist.
     *
     * @param s the name of the point to add
     * @return false if the point already exists, otherwise false after adding the point
     */
    public boolean add_pt(String s) {
        for (int i = 0; i < gpoints.size(); i++) {
            ProPoint p = (ProPoint) gpoints.get(i);
            if (p.getName().equals(s))
                return false;
        }
        ProPoint p = new ProPoint(0, s);
        gpoints.add(p);
        return false;
    }

    /**
     * Sets the location for the point matching the given name.
     *
     * @param sn the name of the point
     * @param x  the x-coordinate of the point
     * @param y  the y-coordinate of the point
     * @param x1 the first additional coordinate parameter
     * @param y1 the second additional coordinate parameter
     * @return true if the point location is successfully set; false otherwise
     */
    public boolean setPtLoc(String sn, double x, double y, int x1, int y1) {
        for (int i = 0; i < gpoints.size(); i++) {
            ProPoint p = (ProPoint) gpoints.get(i);
            if (p.getName().equals(sn)) {
                p.setXY(x, y);
                p.setXY1(x1, y1);
                return true;
            }
        }
        return false;
    }

    /**
     * Reads an A-term from the provided DataInputStream while ignoring header lines.
     *
     * @param in the DataInputStream to read from
     * @return true if the A-term is successfully read; false otherwise
     * @throws IOException if an I/O error occurs
     */
    public boolean readAterm2(DataInputStream in) throws IOException {
        int n = in.readInt();
        Vector glines = new Vector();

        for (int i = 0; i < n; i++) {
            String s = in.readUTF();
            if (s.startsWith("#") || s.startsWith("EXAMPLE") || s.startsWith("HYPOTHESES"))
                continue;
            glines.add(s);
        }
        generate(glines);
        return true;
    }

    /**
     * Clears all stored points, construction steps, and resets the construction name.
     */
    public void clear() {
        gpoints.clear();
        gcons.clear();
        ncons.clear();
        name = null;
    }

    /**
     * Clears current construction data and sets constraints from the given vector.
     *
     * @param v the vector containing new constraints
     */
    public void addConsV(Vector v) {
        this.clear();
        gcons.addAll(v);
    }

    /**
     * Generates the construction based on the provided vector of input lines.
     *
     * @param glines the vector containing construction lines
     * @return true if the generation is successful; false otherwise
     */
    public boolean generate(Vector glines) {
        if (generated)
            return true;

        gpoints.clear();
        gcons.clear();
        generated = true;
        conc.pred = 0;
        int Index = -1;

        while (true) {
            Index++;
            if (Index >= glines.size()) {
                break;
            }
            String ln = (String) glines.get(Index);

            if (ln == null || ln.startsWith("#"))
                break;

            if (!addCondition(ln))
                return false;
        }
        generate_position();
        return true;
    }

    /**
     * Generates the positions of points using the stored position string.
     * Parses the position string, updates each point's coordinates, and sets the position flag.
     *
     * @return true if the positions were generated successfully or no positions were provided.
     */
    boolean generate_position() {
        String s = posString.trim();
        if (s == null || s.length() == 0) {
            return true;
        }
        int n = s.length();
        int index = 0;
        int num = 0;
        int[] k = new int[4];

        while (index < n) {
            String name = "";
            char c = s.charAt(index);
            while (c != '(') {
                name += c;
                index++;
                if (index >= n) {
                    break;
                }
                c = s.charAt(index);
            }
            name = name.trim();
            int d = this.findPt(name);

            ProPoint pt = null;
            if (d != 0) {
                pt = (ProPoint) gpoints.get(d - 1);
            }

            String s1 = "";
            index++;
            if (index >= n) {
                break;
            }
            c = s.charAt(index);
            int dd = 0;
            do {
                if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
                    index++;
                    if (index >= n) {
                        break;
                    }
                    c = s.charAt(index);
                } else if (s1.length() == 0) {
                    if (c >= '0' && c <= '9' || c == '-') {
                        s1 += c;
                    }
                    index++;
                    if (index >= n) {
                        break;
                    }
                    c = s.charAt(index);

                } else if (c >= '0' && c <= '9') {
                    s1 += c;
                    index++;
                    if (index >= n) {
                        break;
                    }
                    c = s.charAt(index);

                } else {
                    int x = 0;
                    try {
                        x = Integer.parseInt(s1.trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Number format exception: " + s1.trim());
                    }
                    k[dd] = x;
                    dd++;
                    s1 = "";
                }
            } while (dd < 4);

            if (index >= n)
                break;
            c = s.charAt(index);
            if (pt != null) {
                pt.setXY(k[0], k[2]);
                pt.setXY1(k[1], k[3]);
            }
            num++;
            while (index < n && ((c = s.charAt(index)) != ')')) {
                index++;
            }
            if (++index >= n) {
                break;
            }

        }
        if (num > 0) {
            this.is_position_set = true;
        }
        return true;
    }

    /**
     * Adds an auxiliary point to the construction.
     *
     * @param pt the point to be added.
     */
    public void addauxedPoint(ProPoint pt) {
        gpoints.add(pt);
    }

    /**
     * Adds an auxiliary constraint to the construction.
     * Inserts the constraint appropriately depending on whether a conclusion exists.
     *
     * @param c the constraint to be added.
     */
    public void addauxedCons(Cons c) {
        int n = gcons.size();
        if (n == 0)
            gcons.add(c);
        else {
            Cons c1 = (Cons) gcons.get(n - 1);
            if (c1.is_conc())
                gcons.add(n - 1, c);
        }
    }

    /**
     * Returns the number of constructions excluding the final conclusion if present.
     *
     * @return the count of constructions.
     */
    public int getconsNum() {
        int n = gcons.size() - 1;
        if (n <= 0)
            return 0;
        Cons c1 = (Cons) gcons.get(n - 1);
        if (c1.is_conc())
            return n - 2;
        return n - 1;
    }

    /**
     * Determines if the term is animated based on the animation string.
     *
     * @return true if the term is animated, false otherwise.
     */
    public boolean isTermAnimated() {
        return aniString.startsWith("ANI");
    }

    /**
     * Retrieves the animation string for the term.
     *
     * @return the animation string.
     */
    public String getAnimateString() {
        return aniString;
    }

    /**
     * Checks if the positions of points have been set.
     *
     * @return true if positions are set, false otherwise.
     */
    public boolean isPositionSet() {
        return is_position_set;
    }

    /**
     * Add a conclusion to the construction.
     *
     * @param ln A script command in the form "SHOW: COLLINEAR P Q R"
     * @return if the conclusion was added successfully
     */
    boolean addConclusion(String ln) {
        Cons c = new Cons(0);
        String sln = ln.substring(4, ln.length());
        sln = sln.replace(':', ' ');
        sln = sln.replace(";", "");
        sln = sln.replace(".", "");
        sln = sln.trim();
        String[] list = sln.split(" ");
        // Now list contains the conclusion word by word, for example: ["COLLINEAR", "P", "Q", "R"]
        if (list[0].equalsIgnoreCase("NO")) {
            return true;
        }

        int t = CST.getClu(list[0]); // convert the conclusion type to integer, e.g. COLLINEAR to Gib.CO_COLL
        if (t == Gib.CO_NANG || t == Gib.CO_NSEG) {
            String s = "";
            for (int i = 1; i < list.length; i++)
                s += " " + list[i];
            c.setText(s);
        }

        if (t == Gib.CO_CYCLIC) {
            for (int i = 1; i < list.length; i++) {
                conc.p[i] = this.findPt(list[i]);
            }

        } else if (t == Gib.CO_ACONG && list.length == 7) {
            if (list.length < 9) {
                int[] pp = new int[6];
                for (int i = 0; i < 6; i++) {
                    pp[i] = this.findPt(list[i + 1]);
                }
                conc.p[0] = pp[0];
                conc.p[1] = pp[1];
                conc.p[2] = pp[1];
                conc.p[3] = pp[2];
                conc.p[4] = pp[3];
                conc.p[5] = pp[4];
                conc.p[6] = pp[4];
                conc.p[7] = pp[5];
            } else
                for (int i = 1; i < list.length; i++)
                    conc.p[i] = findPt(list[i]);

        } else {
            if (conc.p.length < list.length) {
                // TODO. Handle this.
                System.err.println("Index out of bounds: " + this.toString() + " " + ln);
                return false;
            }
            for (int i = 1; i < list.length; i++) {
                String s = list[i];
                if (isStringTypeInt(s)) {
                    conc.p[i - 1] = Integer.parseInt(s);
                } else {
                    conc.p[i - 1] = this.findPt(s);
                }
            }
        }
        conc.pred = t;
        c.type = t;
        int id = 0;
        for (int i = 1; i < list.length; i++) {
            c.pss[id++] = list[i];
        }
        c.no = id;
        c.conc = true;
        // Now variable c contains the conclusion in the required format and can be added to the construction:
        gcons.add(c);
        return true;
    }

    /**
     * Adds a construction condition parsed from the provided script line.
     * Handles various types of conditions including point definitions and constraints.
     *
     * @param ln the script line representing the condition.
     * @return true if the condition was added successfully, false otherwise.
     */
    boolean addCondition(String ln) {
        String st = ln.trim().substring(0, 4);
        if (st.equalsIgnoreCase("show")) {
            this.addConclusion(ln.trim());
        } else {

            ln = ln.replaceAll(";", "");
            String[] list = ln.split(" ");
            String sh = list[0].toUpperCase();

            if (sh.equals(Cm.P_POINT) || sh.equals(Cm.DR_WPT)) {
                for (int i = 1; i < list.length; i++) {
                    if (!list[i].equals(";")) {
                        ProPoint pt = new ProPoint();
                        pt.name = list[i];
                        pt.type = Gib.C_POINT;
                        addPtToList(pt);
                    }
                }
            } else {
                if (sh.contains("-")) {
                    sh = sh.replaceAll("-", "_");
                }
                if (sh.startsWith("~")) {
                    String sh1 = sh.substring(1);
                    int t = getPred(sh1);
                    addNdg(t, list);
                } else {
                    int t = getPred(sh);
                    if (t == 0)
                        return false;

                    if (t == Gib.C_CONSTANT) {
                        if (list.length < 3) {
                            // TODO. Handle this.
                            System.err.println("Index out of bounds: " + this.toString() + " " + ln);
                            return false;
                        }
                        this.addConstant(list[1], list[2]);
                    } else if (t == Gib.C_I_SS) {
                        addInterSS(list);
                    } else if (t == Gib.C_I_LS) {
                        addInterLS(list);
                    } else {
                        Cons c = addCd(t, list);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Retrieves the predicate type corresponding to the provided string.
     *
     * @param sh the string representing the predicate.
     * @return the integer value corresponding to the predicate type.
     */
    private int getPred(String sh) {
        int t = CST.get_pred(sh);
        if (t == 0)
            Cm.print("Can not find : " + sh);
        return t;
    }


    /**
     * Processes an inter-line constraint.
     * Adds a circumcenter point if required before creating the constraint.
     *
     * @param list the array of parameters representing the constraint.
     */
    public void addInterLS(String[] list) {
        if (list.length != 7) {
            return;
        }

        int c1 = findCenter(list[4], list[5], list[6]);
        if (c1 == 0) {
            String cn = this.get_cir_center_name();
            ProPoint pt = new ProPoint(Gib.C_CIRCUM, cn, findPt(list[4]),
                    findPt(list[5]), findPt(list[6]), 0, 0,
                    0, 0, 0);
            addPtToList(pt);
        }
        this.addCd(Gib.C_I_LS, list);
    }


    /**
     * Processes an inter-segment constraint.
     * Adds circumcenter points if needed before creating the constraint.
     *
     * @param list the array of parameters representing the constraint.
     */
    public void addInterSS(String[] list) {
        if (list.length != 8) {
            return;
        }

        int c1 = findCenter(list[2], list[3], list[4]);
        int c2 = findCenter(list[5], list[6], list[7]);
        if (c1 == 0) {
            String cn = this.get_cir_center_name();
            ProPoint pt = new ProPoint(Gib.C_CIRCUM, cn, findPt(list[2]),
                    findPt(list[3]), findPt(list[4]), 0, 0,
                    0, 0, 0);
            addPtToList(pt);
        }
        if (c2 == 0) {
            String cn = this.get_cir_center_name();
            ProPoint pt = new ProPoint(Gib.C_CIRCUM, cn, findPt(list[5]),
                    findPt(list[6]), findPt(list[7]), 0, 0,
                    0, 0, 0);
            addPtToList(pt);
        }
        this.addCd(Gib.C_I_SS, list);
    }


    /**
     * Adds a constant to the construction.
     * Creates a new constant constraint using the provided identifier and function.
     *
     * @param sf   the identifier for the constant.
     * @param func the function defining the constant.
     */
    public void addConstant(String sf, String func) {

        Cons c = new Cons(Gib.C_CONSTANT);
        c.add_pt(sf);
        c.add_pt(func);
        gcons.add(c);

//        poly po = new poly();
//        Vector v = new Vector();
//        char[] cm = sf.toCharArray();
//        StringBuffer c = new StringBuffer();
//        int tt = 0;
//        int i = 0;
//        while (i < cm.length) {
//            int t = 0;
//            if (cm[i] >= '0' && cm[i] <= '9') {
//                t = 1;
//            } else if (cm[i] >= 'a' && cm[i] <= 'z' ||
//                    cm[i] >= 'A' && cm[i] <= 'Z') {
//                t = 2;
//            } else {
//                t = 3;
//            }
//            if (t == tt || tt == 0) {
//                c.append(cm[i]);
//                i++;
//                tt = t;
//            } else {
//                v.add(c.toString());
//                c.setLength(0);
//                tt = 0;
//            }
//        }
//        if (c.length() != 0) {
//            v.add(c.toString());
//        }
//
//        String[] st = new String[v.size()];
//        v.toArray(st);
//        xterm t = po.rd_pol(st);

//        Pro_point pt = this.addPt(st[0]);
//        pt.type = gib.C_CONSTANT;
        return;

    }

    /**
     * Adds a point to the list and updates the initial point construction if needed.
     *
     * @param pt the point to be added.
     * @return the new total count of points.
     */
    int addPtToList(ProPoint pt) {
        gpoints.add(pt);

        if (gcons.size() == 0)
            gcons.add(new Cons(Gib.C_POINT, 100));
        Cons c = (Cons) gcons.get(0);
        c.add_pt(pt.getName());
        return gpoints.size();
    }

    /**
     * Generates a unique name for a circle center that does not conflict with existing points.
     *
     * @return the generated circle center name.
     */
    String get_cir_center_name() {
        int i = 1;
        while (true) {
            String s = "o" + i;

            int j;
            for (j = 0; j < gpoints.size(); j++) {
                ProPoint p = (ProPoint) gpoints.get(j);
                if (s.equalsIgnoreCase(p.name)) {
                    break;
                }
            }
            if (j >= gpoints.size()) {
                return s;
            }
            i++;
        }
    }

    /**
     * Retrieves a point based on its 1-based index.
     *
     * @param x the 1-based index of the point.
     * @return the corresponding ProPoint, or null if the index is out of bounds.
     */
    public ProPoint getProPoint(int x) {
        if (x <= 0 || x > gpoints.size()) {
            return null;
        }
        return ((ProPoint) gpoints.get(x - 1));
    }

    /**
     * Retrieves a construction constraint based on its 1-based index.
     *
     * @param x the 1-based index of the constraint.
     * @return the corresponding Cons object, or null if the index is out of bounds.
     */
    public Cons getPcons(int x) {
        if (x <= 0 || x > gcons.size()) {
            return null;
        }
        return ((Cons) gcons.get(x - 1));

    }

    /**
     * Retrieves the name of a point based on its 1-based index.
     *
     * @param x the 1-based index of the point.
     * @return the point name, or an empty string if the index is invalid.
     */
    public String getPtName(int x) {
        if (x <= 0 || x > gpoints.size()) {
            return "";
        }
        return ((ProPoint) gpoints.get(x - 1)).name;
    }

    /**
     * Recalculates and updates constraint point indexes and conclusion values.
     */
    public void ge_cpt() {
        for (int i = 0; i < gcons.size(); i++) {
            Cons c = (Cons) gcons.get(i);
            if (c.type == Gib.C_CONSTANT)
                continue;
            c.revalidate();
            for (int j = 0; j < c.pss.length; j++) {
                Object obj = c.pss[j];
                if (obj != null) {
                    if (obj instanceof Integer) {
                        Integer I = (Integer) obj;
                        c.ps[j] = I.intValue();
                    } else {
                        String s = obj.toString();
                        int pt = findPt(s);
                        if (pt != 0)
                            c.ps[j] = pt;
                        else if (this.isStringTypeInt(s)) {
                            c.ps[j] = Integer.parseInt(s);
                        } else {
                            int k = 0;
                        }
                    }
                }
            }
        }

        Cons c = getConclusion();
        if (c != null) {
            conc.pred = c.type;
            if (conc.pred == Gib.CO_ACONG) {
                if (c.pss[7] == null) {
                    conc.p[0] = c.ps[0];
                    conc.p[1] = c.ps[1];
                    conc.p[2] = c.ps[1];
                    conc.p[3] = c.ps[2];
                    conc.p[4] = c.ps[3];
                    conc.p[5] = c.ps[4];
                    conc.p[6] = c.ps[4];
                    conc.p[7] = c.ps[5];
                } else
                    for (int i = 0; i < 8; i++)
                        conc.p[i] = c.ps[i];

            } else if (conc.pred == Gib.CO_CONG || conc.pred == Gib.CO_RATIO) {
                conc.p[4] = c.ps[4] * c.ps[4];
                conc.p[5] = c.ps[5] * c.ps[5];
            } else if (conc.pred == Gib.CO_CYCLIC) {
                for (int i = 0; i < 4; i++)
                    conc.p[i + 1] = c.ps[i];
            } else {
                for (int k = 0; k <= c.no; k++)
                    conc.p[k] = c.ps[k];
            }
        }


    }

    /**
     * Finds the center point index based on three given point names.
     *
     * @param a the first point name
     * @param b the second point name
     * @param c the third point name
     * @return the index of the center point if found; 0 otherwise
     */
    int findCenter(String a, String b, String c) {
        if (a == null || b == null || c == null) {
            return 0;
        }

        int t1 = this.findPt(a);
        int t2 = this.findPt(b);
        int t3 = this.findPt(c);

        for (int j = 0; j < gpoints.size(); j++) {
            ProPoint p = (ProPoint) gpoints.get(j);
            if (p.type == Gib.C_CIRCUM) {
                int num = 0;
                for (int i = 0; i < 3; i++) {
                    if (p.ps[i] == t1 || p.ps[i] == t2 || p.ps[i] == t3) {
                        num++;
                    }
                }
                if (num == 3) {
                    return j + 1;
                }
            }
        }
        return 0;
    }

    /**
     * Finds the index of the point that matches the specified name.
     *
     * @param sn the name of the point to search for
     * @return the index of the point if found; 0 otherwise
     */
    int findPt(String sn) {
        for (int j = 0; j < gpoints.size(); j++) {
            ProPoint p = (ProPoint) gpoints.get(j);
            if (sn.equalsIgnoreCase(p.name)) {
                return j + 1;
            }
        }
        return 0;
    }

    /**
     * Adds a new point with the specified name if it does not exist.
     *
     * @param sn the name of the point to add
     * @return the added or existing ProPoint instance
     */
    ProPoint addPt(String sn) {
        ProPoint pt;
        if ((pt = fd_pt(sn)) == null) {
            pt = new ProPoint();
            pt.name = sn;
            addPtToList(pt);
        }

        return pt;
    }

    /**
     * Retrieves a point with the specified name from the list.
     *
     * @param sn the name of the point to retrieve
     * @return the ProPoint if found; null otherwise
     */
    protected ProPoint fd_pt(String sn) {
        for (int i = 0; i < gpoints.size(); i++) {
            ProPoint pt = (ProPoint) gpoints.get(i);
            String s = pt.getName();
            if (s != null && s.equals(sn))
                return pt;
        }
        return null;
    }

    /**
     * Determines whether the provided string represents a valid integer.
     *
     * @param s the string to check
     * @return true if the string is a valid integer representation; false otherwise
     */
    boolean isStringTypeInt(String s) {
        if (s == null || s.length() == 0) return false;
        int i = 0;
        char c;
        while (i < s.length()) {
            c = s.charAt(i);
            if (c < '0' || c > '9') return false;
            i++;
        }
        return true;
    }

    /**
     * Creates and adds a constant constraint using the specified type and parameters.
     *
     * @param type the constraint type
     * @param list the array of parameters
     * @return the created constant constraint
     */
    Cons addCd(int type, String[] list) {
        Cons c = getCd(type, list);
        if (c != null)
            gcons.add(c);
        return c;
    }

    /**
     * Converts the string parameters of the given constraint to point indexes.
     *
     * @param c the constraint to process
     */
    public void ge_pss2ps(Cons c) {
        int i = 0;
        while (true) {
            if (c.pss[i] != null)
                c.ps[i] = this.findPt(c.pss[i].toString());
            else
                break;
            i++;
        }

    }

    /**
     * Updates and adds a non-degenerate constraint if it is not already present.
     *
     * @param c the constraint to add
     */
    public void addNdg(Cons c) {
        ge_pss2ps(c);
        if (!ncons.contains(c))
            ncons.add(c);
    }

    /**
     * Creates and adds a non-degenerate constraint from the provided type and parameters.
     *
     * @param type the constraint type
     * @param list the array of parameters
     * @return the created non-degenerate constraint
     */
    Cons addNdg(int type, String[] list) {
        Cons c = getCd(type, list);
        if (c != null)
            ncons.add(c);
        return c;
    }

    /**
     * Creates a constraint based on the provided type and parameter list.
     *
     * @param type the constraint type
     * @param list the array of parameters
     * @return the created constraint
     */
    Cons getCd(int type, String[] list) {
        int len = list.length;
        if (len <= 1)
            return null;

        int p[] = new int[len - 1];
        int n = 0;

        int m = this.findPt(list[1]);
        if (m == 0) this.addPt(list[1]);

        for (int i = 1; i < len; i++) {
            String s = list[i];
            int t = findPt(s);
            p[i - 1] = t;
            if (t > n) n = t;
        }

        ProPoint pt = (ProPoint) gpoints.get(n - 1);
        pt.setType(type);

        int index = 0;
        Cons c = new Cons(type);

        for (int i = 1; i < len; i++) {
            String s = list[i];
            if (isStringTypeInt(s)) {
                int v = Integer.parseInt(s);
                pt.setPS(v, index++);
                c.add_pt(v, i - 1);
                c.add_pt(v, i - 1);
            } else {
                pt.setPS(findPt(s), index++);
                c.add_pt(s, i - 1);
                c.add_pt(findPt(s), i - 1);
            }
        }
        return c;
    }

    /**
     * Saves data to the given output stream.
     *
     * @param out the DataOutputStream to write to
     * @return true if the save operation is successful
     * @throws IOException if an I/O error occurs
     */
    boolean Save(DataOutputStream out) throws IOException {
        out.writeChars("\n");
        return true;
    }

    /**
     * Retrieves the text representation of the current conclusion.
     *
     * @return the conclusion text, or "NO" if no conclusion is set
     */
    public String getConcText() {
        Cons c = this.getConclusion();
        if (c == null)
            return "NO";
        return CST.getDString(c.pss, c.type);
    }

    /**
     * Sets the conclusion number.
     */
    public void setConclusionNo() {

    }

    /**
     * Checks if a valid conclusion exists in the construction.
     *
     * @return true if the last condition type is within the valid range (&gt;= 50 and &lt; 100); false otherwise.
     */
    public boolean hasConclusion() {
        int n = gcons.size();
        if (n == 0)
            return false;
        Cons c = (Cons) gcons.get(n - 1);
        return c.type >= 50 && c.type < 100;

    }

    /**
     * Determines whether the point identified by the given index is free.
     *
     * @param n the 1-based index of the point to check.
     * @return true if the point is not used as the last element in any constraint; false otherwise.
     */
    public boolean isFreePoint(int n) {
        for (int i = 0; i < gcons.size(); i++) {
            Cons c = (Cons) gcons.get(i);
            if (c.type == Gib.C_POINT)
                continue;

            int t = c.getLastPt();
            if (t == n) return false;
        }
//        if (gcons.size() <= 1)
        return true;
//        return false;
    }

    /**
     * Returns a string representation of the construction.
     *
     * @return the name of the construction if available; otherwise, the default string representation.
     */
    public String toString() {
        if (name != null) {
            return name;
        } else {
            return super.toString();
        }
    }

    /**
     * Processes and returns a vector of processed constraints.
     *
     * @return a vector containing the processed constraints.
     */
    public Vector pc() {
        Vector vlist = new Vector();
        Vector v = new Vector();
        Cons conc = null;
        ccons.clear();

        for (int i = 0; i < gcons.size(); i++) {
            Cons c = (Cons) gcons.get(i);
            if (!c.is_conc() && c.type != Gib.C_TRIANGLE && c.type != Gib.C_LINE
                    && c.type != Gib.C_QUADRANGLE)
                v.add(c);
            else if (c.is_conc())
                conc = c;
        }

        if (v.size() == 0)
            return vlist;

        Cons c = (Cons) gcons.get(0);
        v.remove(c);

        for (int i = c.no; i >= 0; i--) {
            int n = c.ps[i];
            if (n == 0)
                break;
            Cons c1 = getcons(n, v);
            Cons c2 = getcons(n, v);
            if (c1 != null)
                add_preq(c1.type, c1.ps);
            if (c2 != null)
                add_preq(c2.type, c2.ps);

            Cons cx = getcons(n, v);
            if (cx != null) {
                vlist.clear();
                //               showNCMessage(c1, c2);
                return vlist;
            }
            if (c1 != null) {
                Cons cr = CST.charCons(n, c1, c2, c.pss);
                if (cr == null) {
                    vlist.clear();
                    //                  showNCMessage(c1, c2);
                    return vlist;
                }
                vlist.add(0, cr);
            }
        }

        gcons.clear();
        gcons.addAll(vlist);
        gcons.add(0, c);
        vlist.add(0, c);
        if (conc != null)
            gcons.add(conc);
        for (int i = 0; i < gcons.size(); i++) {
            Cons cs = (Cons) gcons.get(i);
            int n = cs.getLastPt();
            ProPoint pt = this.getProPoint(n);
            if (pt != null)
                pt.type = c.type;
        }


        getAllCircles();
        for (int i = 0; i < ccons.size(); i++) {
            Cons cs = (Cons) ccons.get(i);
            CST.addPss(cs, c.pss);

        }

        return vlist;
    }

    /**
     * Aggregates and computes all circle constraints from the current constraint data.
     */
    public void getAllCircles() {
        Vector v = ccons;

        for (int i = 0; i < v.size(); i++) {
            Cons c1 = (Cons) v.get(i);
            while (true) {
                int n = (c1.no + 1) / 2;
                boolean r = false;

                for (int j = i + 1; j < v.size(); j++) {
                    Cons c2 = (Cons) v.get(j);
                    for (int k = 0; k < n; k++) {
                        if (c1.ps[k * 2] == c2.ps[0] && c1.ps[k * 2 + 1] == c2.ps[1] || c1.ps[k * 2] == c2.ps[1] && c1.ps[k * 2 + 1] == c2.ps[0]) {
                            addccc(c2.ps[2], c2.ps[3], c1);
                            v.remove(j);
                            r = true;
                        } else if (c1.ps[k * 2] == c2.ps[2] && c1.ps[k * 2 + 1] == c2.ps[3] || c1.ps[k * 2] == c2.ps[3] && c1.ps[k * 2 + 1] == c2.ps[2]) {
                            addccc(c2.ps[0], c2.ps[1], c1);
                            v.remove(j);
                            r = true;
                        }
                    }
                }
                if (!r)
                    break;
            }
        }

        int n = v.size();
        for (int i = 0; i < n; i++) {
            Cons c = (Cons) v.get(i);
            for (int j = 0; j <= c.no; j++) {
                boolean r = false;
                for (int k = 0; k < j; k++)
                    if (c.ps[k] == c.ps[j]) {
                        r = true;
                        break;
                    }
                if (r)
                    continue;

                int num = 0;
                for (int k = j + 1; k <= c.no; k++) {
                    if (c.ps[k] == c.ps[j])
                        num++;
                }
                if (num >= 3) {
                    Cons cc = new Cons(Gib.C_CIRCLE, 30);
                    cc.add_pt(c.ps[j]);
                    int m = (c.no + 1) / 2;
                    for (int k = 0; k <= m; k++) {
                        if (c.ps[k * 2] == c.ps[j])
                            cc.add_pt(c.ps[k * 2 + 1]);
                        else if (c.ps[k * 2 + 1] == c.ps[j])
                            cc.add_pt(c.ps[k * 2]);
                    }
                    ccons.add(cc);
                }
            }
        }
    }

    /**
     * Adds a circle constraint between the two specified points to the provided constraint,
     * if the connection does not already exist.
     *
     * @param a  the first point index.
     * @param b  the second point index.
     * @param cs the constraint to which the circle condition is added.
     */
    public void addccc(int a, int b, Cons cs) {
        int n = (cs.no + 1) / 2;
        for (int i = 0; i < n; i++) {
            if (cs.ps[i * 2] == a && cs.ps[i * 2 + 1] == b || cs.ps[i * 2] == b && cs.ps[i * 2 + 1] == a)
                return;
        }
        cs.ps[cs.no + 1] = a;
        cs.ps[cs.no + 2] = b;
        cs.no += 2;
    }

    /**
     * Adds prerequisite equality constraints based on the given type and associated points.
     *
     * @param t the constraint type.
     * @param p an array of point indices representing the constraint.
     */
    public void add_preq(int t, int[] p) {
        switch (t) {
            case Gib.C_O_C:
                add_eqcons(p[0], p[1], p[1], p[2]);
                break;
            case Gib.C_O_R:
                add_eqcons(p[0], p[1], p[2], p[3]);
                break;
            case Gib.C_O_B:
                add_eqcons(p[0], p[1], p[0], p[2]);
                break;
            case Gib.CO_CONG:
            case Gib.C_EQDISTANCE:
                add_eqcons(p[0], p[1], p[2], p[3]);
                break;
            case Gib.C_MIDPOINT:
            case Gib.CO_MIDP:
                add_eqcons(p[0], p[1], p[0], p[2]);
                break;
            case Gib.C_CIRCUM:
                add_eqcons(p[0], p[1], p[0], p[2]);
                add_eqcons(p[0], p[1], p[0], p[3]);
                break;
            case Gib.C_TRATIO:
            case Gib.C_PRATIO:
                if (p[4] == p[5])
                    add_eqcons(p[0], p[1], p[2], p[3]);
            case Gib.C_ISO_TRI:
                add_eqcons(p[0], p[1], p[0], p[2]);
                break;
            case Gib.C_EQ_TRI:
                add_eqcons(p[0], p[1], p[0], p[2]);
                add_eqcons(p[0], p[1], p[0], p[3]);
                break;
            case Gib.C_PARALLELOGRAM:
            case Gib.C_RECTANGLE:
                add_eqcons(p[0], p[1], p[2], p[3]);
                add_eqcons(p[0], p[3], p[1], p[2]);
                break;
            case Gib.C_SQUARE:
                add_eqcons(p[0], p[1], p[1], p[2]);
                add_eqcons(p[0], p[1], p[2], p[3]);
                add_eqcons(p[0], p[1], p[3], p[4]);
                break;
            case Gib.C_I_LC:
                add_eqcons(p[0], p[3], p[3], p[4]);
                break;
            case Gib.C_I_PC:
            case Gib.C_I_TC:
                add_eqcons(p[0], p[4], p[4], p[5]);
                break;
            case Gib.C_I_BC:
                add_eqcons(p[0], p[1], p[0], p[2]);
                add_eqcons(p[0], p[3], p[3], p[4]);
                break;
            case Gib.C_I_LB:
                add_eqcons(p[0], p[3], p[0], p[4]);
                break;
            case Gib.C_I_PB:
            case Gib.C_I_TB:
                add_eqcons(p[0], p[4], p[0], p[5]);
                break;
            case Gib.C_I_CC:
                add_eqcons(p[0], p[1], p[1], p[2]);
                add_eqcons(p[0], p[3], p[3], p[4]);
                break;
        }
    }

    /**
     * Adds an equality constraint equating the distances between the two pairs of points.
     *
     * @param a the first point index of the first pair.
     * @param b the second point index of the first pair.
     * @param c the first point index of the second pair.
     * @param d the second point index of the second pair.
     */
    public void add_eqcons(int a, int b, int c, int d) {
        Cons cs = new Cons(Gib.C_EQDISTANCE, 100);
        cs.add_pt(a);
        cs.add_pt(b);
        cs.add_pt(c);
        cs.add_pt(d);
        ccons.add(cs);
    }

    /**
     * Retrieves and removes the first constraint in the vector that contains the specified point.
     *
     * @param pt the point identifier to search for in the constraints.
     * @param v  the vector of constraints to search through.
     * @return the constraint that contains the point, or null if no such constraint exists.
     */
    public Cons getcons(int pt, Vector v) {
        for (int i = 0; i < v.size(); i++) {
            Cons c = (Cons) v.get(i);
            if (c.contains(pt)) {
                v.remove(c);
                return c;
            }
        }
        return null;
    }

    /**
     * Generates all non-degenerate constraints from the given vector.
     *
     * @param v the vector of constraints from which to generate non-degenerate constraints.
     * @return a vector containing all non-degenerate constraints.
     */
    public Vector getAllNdgs(Vector v) {

        Vector v1 = new Vector();
        for (int i = 0; i < v.size(); i++) {
            Cons c = (Cons) v.get(i);
            generateCons(c, v1);
        }
        return v1;
    }

    /**
     * Generates non-degenerate constraints based on the specified constraint and adds them to the provided vector.
     *
     * @param c the original constraint used as a basis for generating non-degenerate constraints.
     * @param v the vector to which the generated constraints will be added.
     */
    public void generateCons(Cons c, Vector v) {
        switch (c.type) {

            case Gib.C_O_C:
            case Gib.C_O_B:
            case Gib.C_O_L: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_O_R: {
                break;
            }
            case Gib.C_O_P: {
                Cons c1 = this.getNDG_NEQ(c.ps[2], c.ps[3], c.pss[2], c.pss[3]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_O_T: {
                if (c.pss[0] == c.pss[2]) {
                    Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[3], c.pss[1], c.pss[3]);
                    addNDG(c1, v);
                } else {
                    Cons c1 = this.getNDG_NON_ISOTROPIC(c.ps[2], c.ps[3], c.pss[2], c.pss[3]);
                    addNDG(c1, v);
                }
                break;
            }
            case Gib.C_O_AB:
            case Gib.C_O_S: {
                Cons c1 = this.getNDG_COLL(c.ps[1], c.ps[2], c.ps[3], c.pss[1], c.pss[2], c.pss[3]);
                addNDG(c1, v);
            }
            break;

            case Gib.C_O_A: {
                int n = c.getPts();
                if (n == 8) {
                    if (c.ps[1] == c.ps[2] && c.ps[5] == c.ps[6]) {
                        Cons c1 = this.getNDG_COLL(c.ps[4], c.ps[6], c.ps[7], c.pss[4], c.pss[6], c.pss[7]);
                        addNDG(c1, v);
                        c1 = this.getNDG_NEQ(c.ps[1], c.ps[3], c.pss[1], c.pss[3]);
                        addNDG(c1, v);

                    }
                } else if (n == 6) {
                    Cons c1 = this.getNDG_COLL(c.ps[3], c.ps[4], c.ps[5], c.pss[3], c.pss[4], c.pss[5]);
                    addNDG(c1, v);
                    c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                    addNDG(c1, v);
                }
                break;
            }
            case Gib.C_I_LL: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                c1 = this.getNDG_NEQ(c.ps[3], c.ps[4], c.pss[3], c.pss[4]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_LP: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                c1 = this.getNDG_PARA(c.ps[1], c.ps[2], c.ps[4], c.ps[5], c.pss[1], c.pss[2], c.pss[4], c.pss[5]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_LC:
                break;
            case Gib.C_I_LB: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                c1 = this.getNDG_PERP(c.ps[1], c.ps[2], c.ps[3], c.ps[4], c.pss[1], c.pss[2], c.pss[3], c.pss[4]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_LT: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                c1 = this.getNDG_PERP(c.ps[1], c.ps[2], c.ps[4], c.ps[5], c.pss[1], c.pss[2], c.pss[4], c.pss[5]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_TT:
            case Gib.C_I_PP: {
                Cons c1 = this.getNDG_PARA(c.ps[2], c.ps[3], c.ps[5], c.ps[6], c.pss[2], c.pss[3], c.pss[5], c.pss[6]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_PT: {
                Cons c1 = this.getNDG_PERP(c.ps[2], c.ps[3], c.ps[5], c.ps[6], c.pss[2], c.pss[3], c.pss[5], c.pss[6]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_PB: {
                Cons c1 = this.getNDG_PERP(c.ps[2], c.ps[3], c.ps[4], c.ps[5], c.pss[2], c.pss[3], c.pss[4], c.pss[5]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_PC:
                break;
            case Gib.C_I_TB: {
                Cons c1 = this.getNDG_PARA(c.ps[2], c.ps[3], c.ps[4], c.ps[5], c.pss[2], c.pss[3], c.pss[4], c.pss[5]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_FOOT: {
                Cons c1 = getNDG_NON_ISOTROPIC(c.ps[2], c.ps[3], c.pss[2], c.pss[3]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_RR: {
                Cons c1 = getNDG_NON_ISOTROPIC(c.ps[1], c.ps[4], c.pss[1], c.pss[4]);
                addNDG(c1, v);
                c1 = this.getNDG_NEQ(c.ps[2], c.ps[3], c.pss[2], c.pss[3]);
                addNDG(c1, v);
                c1 = this.getNDG_NEQ(c.ps[5], c.ps[6], c.pss[5], c.pss[6]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_BR: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                c1 = this.getNDG_NEQ(c.ps[4], c.ps[5], c.pss[4], c.pss[5]);
                addNDG(c1, v);
                break;
            }
            case Gib.C_I_BB: {
                Cons c1 = this.getNDG_PARA(c.ps[1], c.ps[2], c.ps[3], c.ps[4], c.pss[1], c.pss[2], c.pss[3], c.pss[4]);
                addNDG(c1, v);
                break;
            }


            case Gib.C_TRIANGLE:
            case Gib.C_ISO_TRI:
            case Gib.C_EQ_TRI:
            case Gib.C_PARALLELOGRAM:
            case Gib.C_TRAPEZOID:
            case Gib.C_R_TRAPEZOID:
            case Gib.C_LOZENGE:
            case Gib.C_RECTANGLE: {
                Cons c1 = this.getNDG_COLL(c.ps[0], c.ps[1], c.ps[2], c.pss[0], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                break;
            }

            case Gib.C_R_TRI: {
                Cons c1 = this.getNDG_NEQ(c.ps[1], c.ps[2], c.pss[1], c.pss[2]);
                addNDG(c1, v);
                break;
            }

            case Gib.C_EQANGLE: {
            }
            case Gib.C_SQUARE:
                break;
            case Gib.C_I_AA: {

                break;
            }
        }
    }

    /**
     * Creates a non-degenerate parallel constraint from the provided points and associated objects.
     *
     * @param t1 the first point identifier.
     * @param t2 the second point identifier.
     * @param t3 the third point identifier.
     * @param t4 the fourth point identifier.
     * @param o1 the first associated object.
     * @param o2 the second associated object.
     * @param o3 the third associated object.
     * @param o4 the fourth associated object.
     * @return a Cons object representing the non-degenerate parallel constraint, or null if conditions are not met.
     */
    public Cons getNDG_PARA(int t1, int t2, int t3, int t4, Object o1, Object o2, Object o3, Object o4) {
        if (t1 > t2) {
            int t = t1;
            t1 = t2;
            t2 = t;
            Object o = o1;
            o1 = o2;
            o2 = o;
        }
        if (t3 > t4) {
            int t = t3;
            t3 = t4;
            t4 = t;
            Object o = o3;
            o3 = o4;
            o4 = o;
        }

        if (t1 > t3) {
            int t = t1;
            t1 = t3;
            t3 = t;
            t = t2;
            t2 = t4;
            t4 = t;
            Object o = o1;
            o1 = o3;
            o3 = o;
            o = o2;
            o2 = o4;
            o4 = o;
        }
        if (t1 == t3 && t2 == t4) return null;
        if (t1 == t3 || t2 == t3)
            return getNDG_COLL(t1, t2, t4, o1, o2, o4);
        if (t1 == t4 || t2 == t4)
            return getNDG_COLL(t1, t2, t3, o1, o2, o3);

        Cons c1 = new Cons(Gib.NDG_PARA);
        c1.pss[0] = o1;
        c1.pss[1] = o2;
        c1.pss[2] = o3;
        c1.pss[3] = o4;
        c1.ps[0] = t1;
        c1.ps[1] = t2;
        c1.ps[2] = t3;
        c1.ps[3] = t4;
        return c1;


    }

    /**
     * Creates a non-degenerate perpendicular constraint based on the provided points and associated objects.
     *
     * @param t1 the first point identifier.
     * @param t2 the second point identifier.
     * @param t3 the third point identifier.
     * @param t4 the fourth point identifier.
     * @param o1 the first associated object.
     * @param o2 the second associated object.
     * @param o3 the third associated object.
     * @param o4 the fourth associated object.
     * @return a Cons object representing the non-degenerate perpendicular constraint.
     */
    public Cons getNDG_PERP(int t1, int t2, int t3, int t4, Object o1, Object o2, Object o3, Object o4) {
        if (t1 > t2) {
            int t = t1;
            t1 = t2;
            t2 = t;
            Object o = o1;
            o1 = o2;
            o2 = o;
        }
        if (t3 > t4) {
            int t = t3;
            t3 = t4;
            t4 = t;
            Object o = o3;
            o3 = o4;
            o4 = o;
        }

        if (t1 > t3) {
            int t = t1;
            t1 = t3;
            t3 = t;
            t = t2;
            t2 = t4;
            t4 = t;
            Object o = o1;
            o1 = o3;
            o3 = o;
            o = o2;
            o2 = o4;
            o4 = o;
        }

        if (t1 == t3 && t2 == t4)
            return getNDG_NON_ISOTROPIC(t1, t2, o1, o2);
        Cons c1 = new Cons(Gib.NDG_PERP);
        c1.pss[0] = o1;
        c1.pss[1] = o2;
        c1.pss[2] = o3;
        c1.pss[3] = o4;
        c1.ps[0] = t1;
        c1.ps[1] = t2;
        c1.ps[2] = t3;
        c1.ps[3] = t4;
        return c1;
    }

    /**
     * Creates a non-degenerate collinearity constraint from the given point identifiers and associated objects.
     *
     * @param t1 the first point identifier.
     * @param t2 the second point identifier.
     * @param t3 the third point identifier.
     * @param o1 the first associated object.
     * @param o2 the second associated object.
     * @param o3 the third associated object.
     * @return a Cons object representing the non-degenerate collinearity constraint, or null if any two points are equal.
     */
    public Cons getNDG_COLL(int t1, int t2, int t3, Object o1, Object o2, Object o3) {
        if (t1 == t2 || t1 == t3 || t2 == t3) return null;
        Cons c1 = new Cons(Gib.NDG_COLL);
        if (t1 > t2) {
            int t = t1;
            t1 = t2;
            t2 = t;
            Object o = o1;
            o1 = o2;
            o2 = o;
        }
        if (t1 > t3) {
            int t = t1;
            t1 = t3;
            t3 = t;
            Object o = o1;
            o1 = o3;
            o3 = o;
        }
        if (t2 > t3) {
            int t = t2;
            t2 = t3;
            t3 = t;
            Object o = o2;
            o2 = o3;
            o3 = o;
        }


        c1.pss[0] = o1;
        c1.pss[1] = o2;
        c1.pss[2] = o3;
        c1.ps[0] = t1;
        c1.ps[1] = t2;
        c1.ps[2] = t3;
        return c1;

    }

    /**
     * Creates a non-degenerate non-isotropic constraint from two point identifiers and their associated objects.
     *
     * @param t1 the first point identifier.
     * @param t2 the second point identifier.
     * @param o1 the first associated object.
     * @param o2 the second associated object.
     * @return a Cons object representing the non-degenerate non-isotropic constraint.
     */
    public Cons getNDG_NON_ISOTROPIC(int t1, int t2, Object o1, Object o2) {
        Cons c1 = new Cons(Gib.NDG_NON_ISOTROPIC);
        if (t1 > t2) {
            int t = t1;
            t1 = t2;
            t2 = t;
            Object o = o1;
            o1 = o2;
            o2 = o;
        }

        c1.pss[0] = o1;
        c1.pss[1] = o2;
        c1.ps[0] = t1;
        c1.ps[1] = t2;
        return c1;
    }

    /**
     * Creates a non-degenerate inequality constraint based on two point identifiers and associated objects.
     *
     * @param t1 the first point identifier.
     * @param t2 the second point identifier.
     * @param o1 the first associated object.
     * @param o2 the second associated object.
     * @return a Cons object representing the non-degenerate inequality constraint.
     */
    public Cons getNDG_NEQ(int t1, int t2, Object o1, Object o2) {
        if (t1 > t2) {
            int t = t1;
            t1 = t2;
            t2 = t;
            Object o = o1;
            o1 = o2;
            o2 = o;
        }
        Cons c1 = new Cons(Gib.NDG_NEQ);
        c1.pss[0] = o1;
        c1.pss[1] = o2;
        c1.ps[0] = t1;
        c1.ps[1] = t2;
        return c1;
    }

    /**
     * Adds a non-degenerate constraint to the specified vector.
     * If a similar constraint exists, it avoids duplication or replaces the existing one.
     *
     * @param c the non-degenerate constraint to add.
     * @param v the vector in which the constraint is to be added.
     */
    public void addNDG(Cons c, Vector v) {
        if (c == null) return;

        for (int i = 0; i < v.size(); i++) {
            Cons c1 = (Cons) v.get(i);
            if (NDG_Contains(c, c1))
                return;
            if (NDG_Contains(c1, c)) {
                v.remove(c1);
                i--;
            }
        }
        v.add(c);
    }

    /**
     * Checks if the first non-degenerate constraint is considered contained within the second.
     *
     * @param c  the constraint to check for containment.
     * @param c1 the constraint that may contain the first.
     * @return true if the first constraint is contained within the second, false otherwise.
     */
    public boolean NDG_Contains(Cons c, Cons c1) // c < c1
    {

        if (c.type == c1.type) {
            for (int j = 0; j < c.pss.length; j++) {
                if (c.pss[j] == null && c1.pss[j] == null)
                    break;
                if (c.pss[j] != c1.pss[j])
                    return false;
            }
            return true;
        }

        if (c.type == Gib.NDG_NEQ && c1.type == Gib.NDG_NON_ISOTROPIC ||
                c1.type == Gib.NDG_NEQ && c.type == Gib.NDG_NON_ISOTROPIC)
            return c1.contains(c.ps[0]) && c1.contains(c.ps[1]);

        if (c.type == Gib.NDG_NEQ || c.type == Gib.NDG_NON_ISOTROPIC) {
            if (c1.type == Gib.NDG_COLL && c1.contains(c.ps[0]) && c1.contains(c.ps[1]))
                return true;
            if (c1.type == Gib.NDG_NON_ISOTROPIC && c1.contains(c.ps[0]) && c1.contains(c.ps[1]))
                return true;
        }
        return false;
    }
}

