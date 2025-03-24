package gprover;

import wprover.GExpert;

/**
 * The CST class contains constants and utility methods for managing
 * geometric constructions and their validations.
 *
 * <p>This class provides arrays for construction types, intersection types,
 * and conclusion types, along with methods to retrieve indices, generate
 * descriptive strings for geometric constructions, and perform various
 * validations and conversions.
 *
 * <p>The static method {@link #charCons(int, Cons, Cons, Object[])} constructs a new
 * construction by combining two given constructions, performing necessary
 * validations and point adjustments based on geometric rules.
 */
public class CST {

    // Array of geometric construction types
    final public static String[] cst = {
            "POINT", "LINE", "ON_LINE", "ON_PLINE", "ON_TLINE", "ON_BLINE", "ON_ALINE",
            "FOOT", "CIRCLE", "ON_CIRCLE", "CIRCUMCENTER", "ON_RCIRCLE",
            "MIDPOINT", "EQDISTANCE", "EQANGLE", "TRATIO", "PRATIO", "NRATIO", "LRATIO",
            "INVERSION", "REFLECTION", "SYM", "TRIANGLE", "QUADRANGLE", "PENTAGON",
            "POLYGON", "ISO_TRIANGLE", "R_TRIANGLE", "EQ_TRIANGLE", "TRAPEZOID",
            "R_TRAPEZOID", "PARALLELOGRAM", "LOZENGE", "RECTANGLE", "SQUARE",
            "INCENTER", "ORTHOCENTER", "CENTROID", "CONSTANT", "PSQUARE", "NSQUARE",
            "S_ANGLE", "ANGLE_BISECTOR", "LC_TANGENT", "RATIO", "CCTANGENT",
            "ON_SCIRCLE", "ON_BALINE", "ON_DCIRCLE", "EQANGLE3P"
    };

    // Array of intersection types
    final public static String[] inters = {
            "INTERSECTION_LL", "INTERSECTION_LP", "INTERSECTION_LC", "INTERSECTION_LB",
            "INTERSECTION_LT", "INTERSECTION_LR", "INTERSECTION_LS", "INTERSECTION_PP",
            "INTERSECTION_PC", "INTERSECTION_PT", "INTERSECTION_PB", "INTERSECTION_TC",
            "INTERSECTION_TT", "INTERSECTION_TB", "INTERSECTION_BB", "INTERSECTION_BC",
            "INTERSECTION_CC", "INTERSECTION_CR", "INTERSECTION_RR", "INTERSECTION_SS",
            "INTERSECTION_AA", "INTERSECTION_LA", "INTERSECTION_PA", "INTERSECTION_PR",
            "INTERSECTION_TA", "INTERSECTION_TR", "INTERSECTION_BA", "INTERSECTION_BR",
            "PT_EQUAL"
    };

    // Array of conclusion types
    final public static String[] conclusion = {
            "COLLINEAR", "PARALLEL", "PERPENDICULAR", "MIDPOINT", "CYCLIC",
            "EQDISTANCE", "EQANGLE", "PERP_BISECT", "TANGENT", "HARMONIC_PAIR",
            "EQ_TRIANGLE", "SIM_TRIANGLE", "CON_TRIANGLE", "EQ_PRODUCT", "ORTHOCENTER",
            "INCENTER", "RATIO", "S_ANGLE", "N_ANGLES", "N_SEGMENTS"
    };

    // Array of detailed conclusion descriptions
    public static String[] s_conc_detail = {
            "Collinear", "Parallel", "Perpendicular", "Midpoint", "Cyclic",
            "Equal Distance", "Equal Angle", "Bisect", "Tangent", "Harmonic Pair",
            "Equilateral Triangle", "Similiar Triangle", "Congruent Triangle",
            "Equal product", "Orthocenter", "Incenter", "Ratio", "Special angle",
            "Angles Equation", "Segment Equation"
    };

    // Private constructor to prevent instantiation
    private CST() {}

    // Constants for index ranges
    final private static int CONC_INDEX = 70;
    final private static int INTER_INDEX = 100;

    /**
     * Gets the index of a conclusion type.
     * @param s The conclusion type as a string.
     * @return The index of the conclusion type.
     */
    public static int getClu(String s) {
        s = s.toUpperCase();
        for (int i = 0; i < conclusion.length; i++)
            if (s.equals(conclusion[i]))
                return i + CONC_INDEX;

        if (s.equals("COCIRCLE")) {
            s = "CYCLIC";
            return getClu(s);
        }
        return 0;
    }

    /**
     * Gets the index of a detailed conclusion description.
     * @param s The detailed conclusion description as a string.
     * @return The index of the detailed conclusion description.
     */
    public static int getClu_D(String s) {
        for (int i = 0; i < s_conc_detail.length; i++)
            if (s.equalsIgnoreCase(s_conc_detail[i]))
                return i + CONC_INDEX;
        return 0;
    }

    /**
     * Gets the conclusion or intersection type as a string.
     * @param n The index of the type.
     * @return The type as a string.
     */
    public static String getClus(int n) {
        int i = n - CONC_INDEX;
        if (i >= 0 && i < conclusion.length)
            return conclusion[i];

        if (n >= CONC_INDEX && n < INTER_INDEX)
            return s_conc_detail[n - CONC_INDEX];

        if (n > INTER_INDEX)
            i = n - INTER_INDEX - 1;
        if (i < 0)
            return "";
        return inters[i];
    }

    /**
     * Gets the index of a predicate type.
     * @param s The predicate type as a string.
     * @return The index of the predicate type.
     */
    public static int get_pred(String s) {
        int n = 0;

        if (n == 0)
            for (int i = 0; i < cst.length; i++)
                if (s.equals(cst[i])) {
                    n = i + 1;
                    break;
                }

        if (n == 0)
            for (int i = 0; i < inters.length; i++)
                if (s.equals(inters[i])) {
                    n = i + INTER_INDEX + 1;
                    break;
                }

        if (n == 0)
            n = getClu(s);

        return n;
    }

    /**
     * Gets the predicate type as a string.
     * @param n The index of the type.
     * @return The type as a string.
     */
    public static String get_preds(int n) {
        if (n >= 1 && n <= cst.length)
            return cst[n - 1];
        if (n > INTER_INDEX && n < 200)
            return inters[n - INTER_INDEX - 1];
        if (n >= 200)
            return " ";

        return getClus(n);
    }

    /**
     * Gets a descriptive string for a given type and parameters.
     * @param pss The parameters.
     * @param t The type.
     * @return The descriptive string.
     */
    public static String getDString(Object[] pss, int t) {
        return getDString(pss, t, true);
    }

    /**
     * Gets a descriptive string for a given type and parameters.
     * @param pss The parameters.
     * @param t The type.
     * @param d Whether to include detailed descriptions.
     * @return The descriptive string.
     */
    public static String getDString(Object[] pss, int t, boolean d) {

        switch (t) {
            case Gib.C_POINT: {
                String s = "";
                int i = 0;
                for (i = 0; i < pss.length && pss[i] != null; i++)
                    if (i != 0)
                        s += ", " + pss[i];
                    else
                        s += pss[i];

                // return "Point: " + s;
                return GExpert.getTranslationViaGettext("Point {0}", s);
            }
            case Gib.C_LINE:
                // return "Line " + pss[0] + pss[1];
                return GExpert.getTranslationViaGettext("Line {0}", pss[0] + "" + pss[1]);
            case Gib.C_O_L:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("on line {0}", "" + pss[1] + pss[2]);
                else
                    return GExpert.getTranslationViaGettext("{0} is on line {1}",
                            "" + pss[0], "" + pss[1] + pss[2]);

            case Gib.C_O_P:
                if (d)
                    return pss[0] + "" + pss[1] + Cm.PARALLEL_SIGN + pss[2] + "" + pss[3];
                else
                    return GExpert.getTranslationViaGettext("{0} is parallel to {1}",
                            pss[0] + "" + pss[1], "" + pss[2] + pss[3]);
            case Gib.C_O_T:
                if (d)
                    return pss[0] + "" + pss[1] + Cm.PERPENDICULAR_SIGN + pss[2] + "" + pss[3];
                else
                    return GExpert.getTranslationViaGettext("{0} is perpendicular to {1}",
                            pss[0] + "" + pss[1], "" + pss[2] + pss[3]);
            case Gib.C_O_B:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("on the perpendicular bisector of",
                            "" + pss[1] + pss[2]);
                else
                    return GExpert.getTranslationViaGettext("{0} is on the perpendicular bisector of {1}",
                        pss[0] + "", "" + pss[1] + pss[2]);
            case Gib.C_O_A:
            case Gib.C_EQANGLE: {
                if (pss[6] != null && pss[7] != null)
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + ", " + pss[2] + pss[3] + "] = " + Cm.ANGLE_SIGN + "[" + pss[4] + pss[5] + ", " + pss[6] + pss[7] + "]";
                else
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + pss[2] + "] = " + Cm.ANGLE_SIGN + "[" + pss[3] + pss[4] + pss[5] + "]";
            }
            case Gib.C_FOOT:
                return pss[0] + "" + pss[1] + Cm.PERPENDICULAR_SIGN + pss[2] + "" + pss[3] + " " + GExpert.getTranslationViaGettext("with foot {0}",
                        "" + pss[0]);
            case Gib.C_CIRCLE: {
                String st = "(" + pss[0] + ",";
                for (int i = 1; i < pss.length && pss[i] != null; i++)
                    st += pss[i];
                st += ")";
                return GExpert.getTranslationViaGettext("Circle {0}", st);
            }
            case Gib.C_O_C:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("on circle {0}",
                           "(" + pss[1] + "," + pss[2] + ")");
                else
                    return GExpert.getTranslationViaGettext("{0} on circle {1}",
                        pss[0] + "", "(" + pss[1] + "," + pss[2] + ")");

            case Gib.C_CIRCUM:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("circumcenter of {0}",
                            "" + pss[1] + pss[2] + pss[3]);
                else
                    return GExpert.getTranslationViaGettext("{0} is the circumcenter of {1}",
                            "" + pss[0], "" + pss[1] + pss[2] + pss[3]);

            case Gib.C_O_R:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("on circle {0}",
                            "(" + pss[1] + "," + pss[2] + pss[3] + ")");
                else
                    return GExpert.getTranslationViaGettext("{0} on circle {1}",
                            pss[0] + "", "(" + pss[1] + "," + pss[2] + pss[3] + ")");

            case Gib.C_MIDPOINT:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("midpoint of {0}", "" + pss[1] + pss[2]);
                else
                    return GExpert.getTranslationViaGettext("{0} is the midpoint of {1}",
                            "" + pss[0], "" + pss[1] + pss[2]);
            case Gib.C_EQDISTANCE: {
                String st = "";
                for (int i = 0; i < pss.length / 2; i++) {
                    if (pss[i * 2] == null || pss[i * 2 + 1] == null)
                        break;
                    if (i != 0)
                        st += " = ";
                    st += "|" + pss[i * 2] + pss[i * 2 + 1] + "|";
                }
                return st;
            }
            case 16:
                return GExpert.getTranslationViaGettext("TRatio of {0} and {1}", pss[0] + "" + pss[1],
                        pss[2] + "" + pss[3]);
            case 17:
                return GExpert.getTranslationViaGettext("PRatio of {0} and {1}", pss[0] + "" + pss[1],
                        pss[2] + "" + pss[3]);
            case 18:
                return GExpert.getTranslationViaGettext("NRatio of {0} and {1}", pss[0] + "" + pss[1],
                        pss[2] + "" + pss[3]);
            case 19:
                return GExpert.getTranslationViaGettext("LRatio of {0} and {1}", pss[0] + "" + pss[1],
                        pss[2] + "" + pss[3]);
            case 20:
                return "????"; // FIXME: What's this?
            case Gib.C_TRIANGLE:
                return GExpert.getTranslationViaGettext("Triangle {0}", "" + pss[0] + pss[1] + pss[2]);
            case Gib.C_QUADRANGLE:
                return GExpert.getTranslationViaGettext("Quadrangle {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case Gib.C_PENTAGON:
                return GExpert.getTranslationViaGettext("Pentagon {0}", "" + pss[0] + pss[1] + pss[2] + pss[3] + pss[4]);
            case 27:
                return GExpert.getTranslationViaGettext("Isosceles triangle {0}", "" + pss[0] + pss[1] + pss[2]);
            case 28:
                return GExpert.getTranslationViaGettext("Right triangle {0}", "" + pss[0] + pss[1] + pss[2]);
            case 29:
                return GExpert.getTranslationViaGettext("Equilateral triangle {0}", "" + pss[0] + pss[1] + pss[2]);
            case 30:
                return GExpert.getTranslationViaGettext("Trapezoid {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case 31:
                return GExpert.getTranslationViaGettext("Right trapezoid {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case 32:
                return GExpert.getTranslationViaGettext("Parallelogram {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case 33:
                return GExpert.getTranslationViaGettext("Rhombus {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case 34:
                return GExpert.getTranslationViaGettext("Rectangle {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);
            case 35:
                return GExpert.getTranslationViaGettext("Square {0}", "" + pss[0] + pss[1] + pss[2] + pss[3]);

            case 36:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("incenter of {0}",
                            "" + pss[1] + pss[2] + pss[3]);
                else
                    return GExpert.getTranslationViaGettext("{0} is the incenter of {1}",
                            "" + pss[0], "" + pss[1] + pss[2] + pss[3]);
            case 37:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("orthocenter of {0}",
                            "" + pss[1] + pss[2] + pss[3]);
                else
                    return GExpert.getTranslationViaGettext("{0} is the orthocenter of {1}",
                            "" + pss[0], "" + pss[1] + pss[2] + pss[3]);
            case 38:
                if (d)
                    return pss[0] + ": " + GExpert.getTranslationViaGettext("centroid of {0}",
                            "" + pss[1] + pss[2] + pss[3]);
                else
                    return GExpert.getTranslationViaGettext("{0} is the centroid of {1}",
                            "" + pss[0], "" + pss[1] + pss[2] + pss[3]);
            case 46:
                return "circle(" + pss[0] + "," + pss[1] + ") tangent to circle(" + pss[2] + "," + pss[3] + ")";

            case Gib.C_I_LL:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + pss[3] + pss[4];
                else
                    return pss[0] + " is the intersection of " + pss[1] + pss[2] + " and " + pss[3] + pss[4];
            case Gib.C_I_LP:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "P(" + pss[3] + ", " + pss[4] + pss[5] + ")";
                else
                    return pss[0] + " is on " + pss[1] + pss[2] + " and " + pss[0] + pss[3] + " is parallel to " + pss[4] + pss[5] + ")";
            case Gib.C_I_LC:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "C(" + pss[3] + pss[4] + ")";
                else
                    return pss[0] + " is the intersection of line " + pss[1] + pss[2] + " and circle(" + pss[3] + pss[4] + ")";

            case Gib.C_I_LB:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "B(" + pss[3] + pss[4] + ")";
                else
                    return pss[0] + " is the intersection of " + pss[1] + pss[2] + " and perp-bisector of " + pss[3] + pss[4] + "";

            case Gib.C_I_LT:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "T(" + pss[3] + ", " + pss[4] + pss[5] + ")";
                else
                    return pss[0] + " is on line " + pss[1] + pss[2] + " and " + pss[0] + pss[3] + " is parallel to " + pss[4] + pss[5];
            case Gib.C_I_LR:
                if (d)
                    return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "R(" + pss[3] + ", " + pss[4] + pss[5] + ")";
                else
                    return pss[0] + " is the intersection of " + pss[1] + pss[2] + " and circle(" + pss[3] + "," + pss[4] + pss[5] + ")";
            case Gib.C_I_LS:
                return null;
            case Gib.C_I_LA:
                return pss[0] + " = " + pss[1] + pss[2] + Cm.INTERSECT_SIGN + "A(" + vprint(3, 9, pss) + ")";
            case Gib.C_I_PP:
                if (d)
                    return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "P(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + pss[3] + " and " + pss[0] + pss[4] + " is parallel to " + pss[5] + pss[6];
            case Gib.C_I_PC:
                if (d)
                    return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "C(" + pss[4] + pss[5] + ")";
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + pss[3] + " and " + pss[0] + " is on circle(" + pss[4] + pss[5] + ")";
            case Gib.C_I_PR:
                if (d)
                    return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "R(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + pss[3] + " and " + pss[0] + " is on circle(" + pss[4] + "," + pss[5] + pss[6] + ")";
            case Gib.C_I_PT:
                if (d)
                    return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "T(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + pss[3] + " and " + pss[0] + pss[4] + " is perpendicular to" + pss[5] + pss[6];
            case Gib.C_I_PB:
                if (d)
                    return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "B(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + pss[3] + " and " + pss[0] + " is on the perep-bisector of" + pss[4] + pss[5];
            case Gib.C_I_PA:
                return pss[0] + " = P(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "A(" + vprint(3, 9, pss) + ")";

            case Gib.C_I_TT:
                if (d)
                    return pss[0] + " = T(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "T(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is perpendicular to " + pss[2] + pss[3] + " and " + pss[0] + pss[4] + " is perependicular to " + pss[5] + pss[6];

            case Gib.C_I_TC:
                if (d)
                    return pss[0] + " = T(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "C(" + pss[4] + pss[5] + ")";
                else
                    return pss[0] + "" + pss[1] + " is perpendicular to " + pss[2] + pss[3] + " and " + pss[0] + " is on circle(" + pss[4] + pss[5] + ")";

            case Gib.C_I_TR:
                if (d)
                    return pss[0] + " = T(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "R(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is perpendicular to " + pss[2] + pss[3] + " and " + pss[0] + " is on circle(" + pss[4] + "," + pss[5] + pss[6] + ")";
            case Gib.C_I_TB:
                if (d)
                    return pss[0] + " = T(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "B(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + "" + pss[1] + " is perpendicular to " + pss[2] + pss[3] + " and " + pss[0] + " is on the perp-bisecotr of " + pss[4] + pss[5];
            case Gib.C_I_TA:
                return pss[0] + " = T(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "A(" + vprint(3, 9, pss) + ")";

            case Gib.C_I_BB:
                if (d)
                    return pss[0] + " = B(" + pss[1] + pss[2] + ")" + Cm.INTERSECT_SIGN + "B(" + pss[3] + pss[4] + ")";
                else
                    return pss[0] + " is the intersection of perp-bisector of " + pss[1] + pss[2] + " and " + "perp-bisector of " + pss[3] + pss[4];
            case Gib.C_I_BC:
                if (d)
                    return pss[0] + " = B(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "C(" + pss[4] + pss[5] + ")";
                else
                    return pss[0] + " is the intersection of perp-bisector of " + pss[1] + pss[2] + " and circle(" + pss[3] + pss[4] + ")";
            case Gib.C_I_BR:
                if (d)
                    return pss[0] + " = B(" + pss[1] + "," + pss[2] + pss[3] + ")" + Cm.INTERSECT_SIGN + "R(" + pss[4] + "," + pss[5] + pss[6] + ")";
                else
                    return pss[0] + " is the intersection of perp-bisector of " + pss[1] + pss[2] + " and circle(" + pss[3] + "," + pss[4] + pss[5] + ")";
            case Gib.C_I_CC:
                if (d)
                    return pss[0] + " = C(" + pss[1] + pss[2] + ")" + Cm.INTERSECT_SIGN + "C(" + pss[3] + pss[4] + ")";
                else
                    return pss[0] + " is the intersection of circle(" + pss[1] + pss[2] + ") and circle(" + pss[3] + pss[4] + ")";
            case Gib.C_I_CR:
                if (d)
                    return pss[0] + " = C(" + pss[1] + pss[2] + ")" + Cm.INTERSECT_SIGN + "R(" + pss[3] + ", " + pss[4] + pss[5] + ")";
                else
                    return pss[0] + " is the intersection of circle(" + pss[1] + pss[2] + ") and circle(" + pss[3] + "," + pss[4] + pss[5] + ")";

            case Gib.CO_COLL:
                return GExpert.getTranslationViaGettext("{0} are collinear", pss[0] + ", " + pss[1] + ", " + pss[2]);
            case Gib.CO_PARA:
                if (d)
                    return pss[0] + "" + pss[1] + Cm.PARALLEL_SIGN + pss[2] + "" + pss[3];
                else
                    return pss[0] + "" + pss[1] + " is parallel to " + pss[2] + "" + pss[3];
            case Gib.CO_PERP:
                if (d)
                    return pss[0] + "" + pss[1] + Cm.PERPENDICULAR_SIGN + pss[2] + "" + pss[3];
                else
                    return pss[0] + "" + pss[1] + " is perpendicular to " + pss[2] + "" + pss[3];
            case Gib.CO_MIDP:
                if (d)
                    return pss[0] + " : midpoint(" + pss[1] + pss[2] + ")";
                else
                    return pss[0] + " is the midpoint of " + pss[1] + pss[2];
            case Gib.CO_CYCLIC:
                // if (d)
                    return GExpert.getTranslationViaGettext("{0} are concyclic", pss[0] + "," + pss[1] + "," + pss[2] + "," + pss[3]);
                // else
                //    return pss[0] + ", " + pss[1] + ", " + pss[2] + ", " + pss[3] + " are cyclic";
            case Gib.CO_CONG:
                return "|" + pss[0] + pss[1] + "| = |" + pss[2] + pss[3] + "|";
            case Gib.CO_ACONG: {
                if (pss[6] != null && pss[7] != null)
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + ", " + pss[2] + pss[3] + "] = " + Cm.ANGLE_SIGN + "[" + pss[4] + pss[5] + ", " + pss[6] + pss[7] + "]";
                else
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + pss[2] + "] = " + Cm.ANGLE_SIGN + "[" + pss[3] + pss[4] + pss[5] + "]";
            }
            case Gib.CO_PBISECT:
                return GExpert.getLanguage("To Prove:") + " " + "??"; // TODO: Find some better method to express the content here.
            case Gib.CO_STRI:
                return Cm.TRIANGLE_SIGN + pss[0] + pss[1] + pss[2] + Cm.SIMILAR_SIGN + Cm.TRIANGLE_SIGN + pss[3] + pss[4] + pss[5];
            case Gib.CO_CTRI:
                return Cm.TRIANGLE_SIGN + pss[0] + pss[1] + pss[2] + Cm.EQUAL_SIGN + Cm.TRIANGLE_SIGN + pss[3] + pss[4] + pss[5];


            case Gib.NDG_NEQ:
                return pss[0] + " != " + pss[1];
            case Gib.NDG_COLL:
                return pss[0] + "," + pss[1] + "," + pss[2] + " are not collinear";
            case Gib.NDG_CONG:
                return pss[0] + "" + pss[1] + " != " + pss[2] + pss[3];
            case Gib.NDG_PARA:
                return pss[0] + "" + pss[1] + " is not parallel to " + pss[2] + "" + pss[3];
            case Gib.NDG_PERP:
                return pss[0] + "" + pss[1] + " is not perpendicular to " + pss[2] + "" + pss[3];
            case Gib.NDG_NON_ISOTROPIC:
                return pss[0] + "" + pss[1] + " is non-isotropic";
            case Gib.NDG_ACONG: {
                if (pss[6] != null && pss[7] != null)
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + ", " + pss[2] + pss[3] + "] != " + Cm.ANGLE_SIGN + "[" + pss[4] + pss[5] + ", " + pss[6] + pss[7] + "]";
                else
                    return Cm.ANGLE_SIGN + "[" + pss[0] + pss[1] + pss[2] + "] != " + Cm.ANGLE_SIGN + "[" + pss[3] + pss[4] + pss[5] + "]";
            }
            case Gib.NDG_CYCLIC:
                return pss[0] + "" + pss[1] + "" + pss[2] + "" + pss[3] + " is not cyclic";
            case Gib.IN_AG_INSIDE:
                return pss[0] + " is inside " + Cm.ANGLE_SIGN + pss[1] + pss[2] + pss[3];
            case Gib.IN_AG_OUTSIDE:
                return pss[0] + " is outside " + Cm.ANGLE_SIGN + pss[1] + pss[2] + pss[3];
            case Gib.IN_BETWEEN:
                return pss[0] + " is between " + pss[1] + pss[2];
            case Gib.IN_OPP_SIDE:
                return pss[0] + "" + pss[1] + " are on the opposite side of " + pss[2] + pss[3];
            case Gib.IN_PARA_INSIDE:
                return pss[0] + " is inside the parallelogram " + pss[1] + pss[2] + pss[3] + pss[4];
            case Gib.IN_PG_CONVEX: {
                String str = "polygon ";
                int i = 0;
                while (i < pss.length && pss[i] != null) {
                    str += pss[i];
                    i++;
                }
                return str + " is convex";
            }
            case Gib.IN_SAME_SIDE:
                return pss[0] + " and " + pss[1] + " is on the same side of line " + pss[2] + pss[3];
            case Gib.IN_TRI_INSIDE:
                return pss[0] + " is inside the triangle " + pss[1] + pss[2] + pss[3];
            case Gib.C_O_S:
                return pss[0] + " is on circle(" + pss[1] + pss[2] + pss[3] + ")";
            case Gib.C_O_AB:
                return pss[0] + " is on the bisector of " + Cm.ANGLE_SIGN + "[" + pss[1] + pss[2] + pss[3] + "]";
            case Gib.C_O_D:
                return pss[0] + " is on the circle D(" + pss[1] + pss[2] + ")";

            default: {
                String st = CST.get_preds(t);
                for (int x = 0; x < pss.length && pss[x] != null; x++)
                    st += " " + pss[x];
                return st;
            }


        }
    }
    /**
     * Generates a concatenated String of the non-null elements in the specified subarray.
     *
     * @param m the starting index (inclusive)
     * @param n the ending index (inclusive)
     * @param ps the array of Objects
     * @return a String containing the concatenation of each non-null element's String representation
     */
    public static String vprint(int m, int n, Object[] ps) {
        String s = "";
        for (int i = m; i <= n; i++)
            if (ps[i] != null)
                s += ps;
        return s;
    }

    /**
     * Constructs a new construction by combining two given construction objects.
     *
     * <p>This method performs the necessary validations and adjustments of point indices
     * according to geometric rules. If the primary construction (c1) is null, the method
     * uses the secondary construction (c2) as the basis for constructing the new Cons object.
     *
     * @param pt the reference point index used for adjustments
     * @param c1 the primary Cons object; may be null
     * @param c2 the secondary Cons object to use if c1 is null
     * @param pss the array of point information associated with the construction
     * @return the resulting Cons object after combining and validating the constructions
     */
    public static Cons charCons(int pt, Cons c1, Cons c2, Object[] pss) {
        if (c1 == null) {
            c1 = c2;
            c2 = null;
        } else if (c1 != null && c2 != null && c1.type > c2.type) {
            Cons c = c1;
            c1 = c2;
            c2 = c;
        }
        int[] p = new int[c1.ps.length];
        p[0] = pt;
        int rt = 0;
        int t1, t2;
        int[] p1, p2;
        p1 = c1.ps;
        t1 = c1.type;
        if (c2 != null) {
            p2 = c2.ps;
            t2 = c2.type;
        } else {
            p2 = null;
            t2 = 0;
        }

        p1 = pcopy(p1);
        t1 = validate_all(pt, t1, p1);
        if (p2 != null) {
            p2 = pcopy(p2);
            t2 = validate_all(pt, t2, p2);
        }

        if (pt == p1[0]) {
            int k = 1;
            for (int i = 1; i < p1.length && p1[i] != 0; i++)
                p[k++] = p1[i];
            if (p2 != null) {
                for (int i = 1; i < p2.length && p2[i] != 0; i++)
                    p[k++] = p2[i];
            }
        } else {
            int k = 0;
            for (int i = 0; i < p1.length && p1[i] != 0; i++)
                p[k++] = p1[i];
        }


        if (p2 == null) {
            rt = t1;
        } else {
            switch (t1) {
                case Gib.C_O_L: {
                    switch (t2) {
                        case Gib.C_O_L:
                            rt = Gib.C_I_LL;
                            break;
                        case Gib.C_O_P:
                            rt = Gib.C_I_LP;
                            break;
                        case Gib.C_O_T:
                            rt = Gib.C_I_LT;
                            rt = ge_lt_foot(rt, p);
                            break;
                        case Gib.C_O_B:
                            rt = Gib.C_I_LB;
                            break;
                        case Gib.C_O_A:
                            rt = Gib.C_I_LA;
                            break;
                        case Gib.C_O_C:
                            rt = Gib.C_I_LC;
                            break;
                        case Gib.C_O_R:
                            rt = Gib.C_I_LR;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_P: {
                    switch (t2) {
                        case Gib.C_O_P:
                            rt = Gib.C_I_PP;
                            break;
                        case Gib.C_O_T:
                            rt = Gib.C_I_PT;
                            break;
                        case Gib.C_O_B:
                            rt = Gib.C_I_PB;
                            break;
                        case Gib.C_O_A:
                            rt = Gib.C_I_PA;
                            break;
                        case Gib.C_O_C:
                            rt = Gib.C_I_PC;
                            break;
                        case Gib.C_O_R:
                            rt = Gib.C_I_PR;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_T: {
                    switch (t2) {
                        case Gib.C_O_T:
                            rt = Gib.C_I_TT;
                            break;
                        case Gib.C_O_B:
                            rt = Gib.C_I_TB;
                            break;
                        case Gib.C_O_A:
                            rt = Gib.C_I_TA;
                            break;
                        case Gib.C_O_C:
                            rt = Gib.C_I_TC;
                            break;
                        case Gib.C_O_R:
                            rt = Gib.C_I_TR;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_B: {
                    switch (t2) {
                        case Gib.C_O_B:
                            rt = Gib.C_I_BB;
                            break;
                        case Gib.C_O_A:
                            rt = Gib.C_I_BA;
                            break;
                        case Gib.C_O_C:
                            rt = Gib.C_I_BC;
                            break;
                        case Gib.C_O_R:
                            rt = Gib.C_I_BR;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_A: {
                    switch (t2) {
                        case Gib.C_O_A:
                            rt = Gib.C_I_AA;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_C: {
                    switch (t2) {
                        case Gib.C_O_C:
                            rt = Gib.C_I_CC;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                case Gib.C_O_R: {
                    switch (t2) {
                        case Gib.C_O_R:
                            rt = Gib.C_I_RR;
                            break;
                        default:
                            break;
                    }
                    break;
                }
                default:
                    break;  // failed.
            }
        }

        if (rt == 0)
            return null;

        if (t1 == Gib.C_O_L && t2 == Gib.C_O_A) {

            int i = 0;
            for (; p2[i] != 0; i++) ;

            if (i == 6) {
                if (p2[1] == p2[4] && p2[2] == pt) {
                    int a, b, c;

                    if (p2[2] == p2[3] && p2[0] != p2[5]) {
                        a = p2[0];
                        b = p2[5];
                        c = p2[2];
                    } else if (p2[0] == p2[5] && p2[2] != p2[3]) {
                        a = p2[2];
                        b = p2[3];
                        c = p2[0];
                    } else {
                        a = b = c = 0;
                    }
                    if (a != 0) {
                        rt = Gib.C_FOOT;
                        p[0] = pt;
                        p[1] = c;
                        p[2] = a;
                        p[3] = b;
                    }
                }
            }
        }


        Cons c = new Cons(rt, p.length);
        int i = 0;
        for (i = 0; i < p.length && p[i] != 0; i++)
            c.ps[i] = p[i];
        c.no = i - 1;
        spec(pt, c);
        addPss(c, pss);
        return c;
    }


    /**
     * Adjusts the specification fields of the given construction based on the reference point.
     *
     * <p>This method analyzes and modifies the type and point indices of the provided
     * construction (c) according to geometric rules and validations.
     *
     * @param pt the reference point index used for adjustments
     * @param c the construction object to be adjusted; may be {@code null}
     */
    public static void spec(int pt, Cons c) {
        if (c == null) return;

        switch (c.type) {
            case Gib.C_O_A: {
                int n = c.getPts();
                if (n == 8) {
                    if (c.ps[0] == c.ps[4] && c.ps[3] == c.ps[7] && pt == c.ps[1] && pt == c.ps[2] && c.ps[5] == c.ps[6]) {
                        c.type = Gib.C_O_S;
                        c.ps[0] = pt;
                        c.ps[1] = c.ps[4];
                        c.ps[2] = c.ps[5];
                        c.ps[3] = c.ps[7];
                        c.ps[4] = c.ps[5] = c.ps[6] = c.ps[7] = 0;
                    } else
                    if (pt == c.ps[0] && pt == c.ps[7] && c.ps[1] == c.ps[2] && c.ps[5] == c.ps[6] && c.ps[1] == c.ps[5] && c.ps[3] != 4) {
                        c.type = Gib.C_O_AB;
                        c.ps[0] = pt;
                        c.ps[1] = c.ps[3];
                        c.ps[2] = c.ps[2];
                        c.ps[3] = c.ps[4];
                        c.ps[4] = c.ps[5] = c.ps[6] = c.ps[7] = 0;
                    } else
                    if (pt == c.ps[0] && pt == c.ps[7] && c.ps[1] == c.ps[2] && c.ps[5] == c.ps[6] && c.ps[1] == c.ps[4] && c.ps[3] == c.ps[5]) {
                        c.type = Gib.C_O_B;
                        c.ps[0] = pt;
                        c.ps[2] = c.ps[3];
                        c.ps[4] = c.ps[5] = c.ps[6] = c.ps[7] = 0;
                    }
                } else if (n == 6) {
                    if (pt == c.ps[0] && pt == c.ps[5] && c.ps[1] == c.ps[3] && c.ps[2] == c.ps[4]) {
                        c.type = Gib.C_O_B;
                        c.ps[0] = pt;
                        if (c.ps[1] > c.ps[2]) {
                            int t = c.ps[1];
                            c.ps[1] = c.ps[2];
                            c.ps[2] = t;
                        }
                        c.ps[3] = c.ps[4] = c.ps[5] = c.ps[6] = 0;
                    } else if (c.ps[0] == c.ps[3] && c.ps[2] == c.ps[5] && pt == c.ps[1]) {
                        c.type = Gib.C_O_S;
                        c.ps[0] = pt;
                        c.ps[1] = c.ps[3];
                        c.ps[2] = c.ps[4];
                        c.ps[3] = c.ps[5];
                        c.ps[4] = c.ps[5] = c.ps[6] = 0;
                    } else if (pt == c.ps[0] && pt == c.ps[5] && c.ps[1] == c.ps[4] && c.ps[2] != c.ps[3]) {
                        c.type = Gib.C_O_AB;
                        c.ps[0] = pt;
                        c.ps[4] = c.ps[5] = c.ps[6] = c.ps[7] = 0;
                    }

                }
                break;
            }
            case Gib.C_O_T: {
                if (c.ps[0] == c.ps[2]) {
                    c.type = Gib.C_O_D;
                    c.ps[2] = c.ps[3];
                    c.ps[3] = 0;
                } else if (c.ps[0] == c.ps[3]) {
                    c.type = Gib.C_O_D;
                    c.ps[3] = 0;
                }
            }
            break;

            default:
                return;
        }
    }

    /**
     * Adds additional point specification data to the given construction.
     *
     * <p>This method processes the provided array of point specification data and updates the
     * corresponding fields in the construction object accordingly.</p>
     *
     * @param c   the construction object to update
     * @param pss the array of additional point specification data
     */
    public static void addPss(Cons c, Object[] pss) {
        int[] p = c.ps;
        int i = 0;
        for (i = 0; i < p.length && p[i] != 0; i++) {
            c.ps[i] = p[i];
            int n = p[i] - 1;
            if (n < pss.length) {
                Object s = pss[p[i] - 1];
                c.pss[i] = s;
            }
        }
        c.no = i - 1;
    }

    /**
     * Revalidates the provided array of point indices based on the reference point and count.
     * <p>
     * This method applies revalidation rules to adjust the point indices in the array
     * so that they are consistent with the given reference point.
     * </p>
     *
     * @param pt the reference point used for revalidation
     * @param p  the array of point indices to validate
     * @param n  the number of valid entries in the point indices array
     */
    public static void reval(int pt, int[] p, int n) {
        int n1 = n / 2;
        boolean c = false;

        for (int i = n1; i < n; i++) {
            if (p[i] == pt) {
                c = true;
                break;
            }
        }
        if (c) {
            for (int i = 0; i < n1; i++) {
                int t = p[i];
                p[i] = p[i + n1];
                p[i + n1] = t;
                if (2 * n < p.length) {
                    t = p[i + n];
                    p[i + n] = p[i + n + n1];
                    p[i + n + n1] = t;
                }
            }
        }
        if (n1 == 1)
            return;
        else
            reval(pt, p, n1);
    }

    /**
     * Adjusts the type code for a line-to-foot construction.
     * Applies foot-specific rules and validations on the provided type and point array.
     *
     * @param t the original type code
     * @param p the array of point indices associated with the construction
     * @return the adjusted type code after applying the foot-specific rules
     */
    public static int ge_lt_foot(int t, int[] p) {
        if (p[1] == p[4] && p[2] == p[5] || p[1] == p[5] && p[2] == p[4]) {
            p[4] = p[5] = 0;
            int t1 = p[3];
            p[3] = p[2];
            p[2] = p[1];
            p[1] = t1;
            return Gib.C_FOOT;
        }
        return t;
    }

    /**
     * Validates and adjusts the type code for a construction based on the reference point and its associated point indices.
     *
     * <p>This method checks the provided point index array and the initial type code, applying specific geometric rules
     * to ensure the correctness of the construction. It returns an adjusted type code reflecting any validations applied,
     * or 0 if the validation fails.
     *
     * @param pt the reference point index used during validation
     * @param t1 the initial type code of the construction
     * @param p1 an array of point indices associated with the construction (may be modified during validation)
     * @return the validated (and possibly adjusted) type code, or 0 if validation fails
     */
    public static int validate_all(int pt, int t1, int[] p1) {

        if (t1 == Gib.C_EQDISTANCE || t1 == Gib.CO_CONG) {
            t1 = validate_cg(pt, p1);
        } else if (t1 == Gib.C_EQANGLE || t1 == Gib.CO_ACONG || t1 == Gib.C_O_A) {
            t1 = validate_ea(pt, p1);
        } else if (t1 == Gib.CO_COLL)
            t1 = validate_coll(pt, p1);
        else if (t1 == Gib.CO_PARA || t1 == Gib.C_O_P)
            t1 = validate_p(pt, p1);
        else if (t1 == Gib.CO_PERP || t1 == Gib.C_O_T)
            t1 = validate_t(pt, p1);
        return t1;
    }

    /**
     * Validates and adjusts the construction type for an angle-related scenario.
     *
     * <p>This method checks and modifies the provided point indices array for angle-related constructions,
     * applying specific geometric validations. It returns an adjusted type code if the validation is successful,
     * or 0 if validation fails.</p>
     *
     * @param pt the reference point index used for validation
     * @param ps an array of point indices related to the construction
     * @return the validated (and possibly adjusted) type code, or 0 if validation fails
     */
    public static int validate_ea(int pt, int[] ps) {
        int t1 = Gib.C_O_A;
        int i = 0;
        for (; ps[i] != 0; i++) ;
        if (i == 6) {
            if (pt == ps[3] || pt == ps[4] || pt == ps[5]) {
                int t = ps[0];
                ps[0] = ps[3];
                ps[3] = t;
                t = ps[1];
                ps[1] = ps[4];
                ps[4] = t;
                t = ps[2];
                ps[2] = ps[5];
                ps[5] = t;
            }
            if (pt == ps[2]) {
                int t = ps[0];
                ps[0] = ps[2];
                ps[2] = t;
                t = ps[3];
                ps[3] = ps[5];
                ps[5] = t;
            }
            if (ps[1] == ps[5] && ps[2] == ps[4]) {
                ps[4] = ps[5] = 0;
                t1 = Gib.C_O_P;
            } else if (ps[0] == ps[5] && ps[1] == ps[4] && ps[2] == ps[3]) {
                t1 = Gib.C_O_T;
                ps[3] = ps[1];
                ps[4] = ps[5] = ps[6] = 0;
            } else if (ps[0] == ps[5] && ps[1] == ps[3] && ps[2] == ps[4]) {
                t1 = Gib.C_O_B;
                ps[3] = ps[4] = ps[5] = ps[6] = 0;
            }


        } else if (i == 8) {
            reval(pt, ps, 8);
        }
        return t1;
    }

    /**
     * Validates and adjusts the construction type for a collinearity scenario.
     *
     * <p>This method examines the provided array of point indices and determines if they satisfy
     * the conditions for defining a collinear construction. It returns the validated type code
     * if successful, or 0 if validation fails.</p>
     *
     * @param pt the reference point index used for validation
     * @param ps the array of point indices to validate
     * @return the validated type code for the collinear construction, or 0 if validation fails
     */
    public static int validate_coll(int pt, int[] ps) {
        if (ps[0] < ps[1])
            exchange(0, 1, ps);
        if (ps[0] < ps[2])
            exchange(0, 2, ps);
        if (ps[1] < ps[2])
            exchange(1, 2, ps);
        return Gib.C_O_L;
    }

    /**
     * Validates and adjusts the construction type for a parallel scenario.
     *
     * <p>This method examines the array of point indices and validates them according to
     * the rules for parallel constructions. It returns the validated type code if the
     * validation is successful, or 0 if validation fails.
     *
     * @param pt the reference point index used during validation
     * @param ps the array of point indices associated with the parallel construction
     * @return the validated construction type code for a parallel configuration, or 0 if invalid
     */
    public static int validate_p(int pt, int[] ps) {
        if (ps[0] < ps[1])
            exchange(0, 1, ps);
        if (ps[2] < ps[3])
            exchange(2, 3, ps);
        if (ps[0] < ps[2]) {
            exchange(0, 2, ps);
            exchange(1, 3, ps);
        }
        return Gib.C_O_P;
    }

    /**
     * Validates and adjusts the construction type for a perpendicular construction.
     *
     * <p>This method examines the provided array of point indices and applies
     * perpendicular-specific validation rules. It may reorder or adjust the point indices
     * to ensure consistency with the geometric definition of a perpendicular construction.
     * If the validation is successful, the method returns the adjusted construction type;
     * otherwise, it returns 0.</p>
     *
     * @param pt the reference point index used during validation
     * @param ps the array of point indices associated with the construction
     * @return the validated (and possibly adjusted) construction type code, or 0 if validation fails
     */
    public static int validate_t(int pt, int[] ps) {
        if (ps[0] < ps[1])
            exchange(0, 1, ps);
        if (ps[2] < ps[3])
            exchange(2, 3, ps);
        if (ps[0] < ps[2]) {
            exchange(0, 2, ps);
            exchange(1, 3, ps);
        }
        return Gib.C_O_T;
    }

    /**
     * Validates and adjusts the construction type for a congruence of distances construction.
     *
     * <p>This method examines the provided array of point indices associated
     * with a congruence construction (e.g., verifying segment congruence) and applies
     * specific validations. It returns an adjusted type code reflecting the outcome of the
     * validation, or 0 if validation fails.</p>
     *
     * @param pt the reference point index used for validation
     * @param ps the array of point indices associated with the congruence construction
     * @return the validated construction type code, or 0 if validation fails
     */
    public static int validate_cg(int pt, int[] ps) {
        if (ps[0] < ps[1])
            exchange(0, 1, ps);
        if (ps[2] < ps[3])
            exchange(2, 3, ps);
        if (ps[0] < ps[2]) {
            exchange(0, 2, ps);
            exchange(1, 3, ps);
        }

        if (ps[0] == ps[2] && ps[0] == pt) {
            ps[2] = ps[3];
            ps[3] = 0;
            return Gib.C_O_B;
        }
        return Gib.C_O_R;
    }


    /**
     * Exchanges the elements at indices i and j in the given array.
     *
     * @param i the index of the first element to exchange
     * @param j the index of the second element to exchange
     * @param ps the array in which the elements will be swapped
     */
    public static void exchange(int i, int j, int[] ps) {
        int t = ps[i];
        ps[i] = ps[j];
        ps[j] = t;
    }


    /**
     * Creates a copy of the given integer array.
     * This method returns a new array containing the same elements as the input array.
     *
     * @param p the array to copy
     * @return a new array that is a copy of p
     */
    public static int[] pcopy(int[] p) {
        if (p == null)
            return null;

        int n = p.length;
        int[] p1 = new int[n];
        for (int i = 0; i < n; i++)
            p1[i] = p[i];
        return p1;
    }

}
