/**
         * The Cm class contains various constants and utility methods used in geometric proofs.
         * It includes string constants for geometric symbols, predicates, and messages,
         * as well as a debug flag and a print method for debugging purposes.
         */
        package gprover;

        final public class Cm {

            /** Hypothesis string constant. */
            final public static String s2070 = "(hyp)";

            /** The machine proof string constant. */
            final public static String s2072 = "The Machine Proof";

            /** Perpendicular sign constant. */
            final public static String PERPENDICULAR_SIGN = " ⊥ ";
            /** Parallel sign constant. */
            final public static String PARALLEL_SIGN = " ∥ ";
            /** Triangle sign constant. */
            final public static String TRIANGLE_SIGN = "∆";

            /** Angle sign constant. */
            final public static String ANGLE_SIGN = "∠";
            /** Equal sign constant. */
            final public static String EQUAL_SIGN = " = ";
            /** Similar sign constant. */
            final public static String SIMILAR_SIGN = " ~ ";
            /** Intersect sign constant. */
            final public static String INTERSECT_SIGN = " ∩ ";

            /** Lines string constant. */
            final public static String s2707 = "lines";

            /** Circles in the database string constant. */
            final public static String s2713 = "circles in the database.";

            /** Similar triangles string constant. */
            final public static String s2720 = "similar triangles";

            /** Congruent triangles string constant. */
            final public static String s2722 = "congruent triangles";

            /** Because string constant. */
            final public static String s2727 = "       because ";
            /** And string constant. */
            final public static String s2728 = " and ";

            /** Collinear predicate constant. */
            final public static String PC_COLL = "COLLINEAR";
            /** Parallel predicate constant. */
            final public static String PC_PARA = "PARALLEL";
            /** Perpendicular predicate constant. */
            final public static String PC_PERP = "PERPENDICULAR";
            /** Equal distance predicate constant. */
            final public static String PC_CONG = "EQDISTANCE";
            /** Equal angle predicate constant. */
            final public static String PC_ACONG = "EQANGLE";
            /** Cocircle predicate constant. */
            final public static String PC_CYCLIC = "COCIRCLE";

            /** Similar triangle predicate constant. */
            final public static String PC_STRI = "SIM_TRIANGLE";
            /** Congruent triangle predicate constant. */
            final public static String PC_CTRI = "CON_TRIANGLE";
            /** Midpoint predicate constant. */
            final public static String PC_MIDP = "MIDPOINT";

            /** Point predicate constant. */
            final public static String P_POINT = "POINT";

            /** WPT string constant. */
            final public static String DR_WPT = "WPT";

            /** Only full-angles allowed message constant. */
            final public static String s2810 = "\r\n\r\nOnly full-angles are allowd in this case";
            /** Conclusion cannot be represented with full-angles message constant. */
            final public static String s2811 = "\r\nConclusion cannot be represented with full-angles.\r\n";
            /** Cannot solve problem with full-angles message constant. */
            final public static String s2812 = "\r\nCannot solve this problem with full-angles.\r\n";

            /** Index string constant. */
            final public static String s1993 = "Index";

            /** The statement is true message constant. */
            final public static String s2300 = "\r\n\r\nThe statement is true.\r\n\r\n";

            /** No proof exists message constant. */
            final public static String s2220 = "\r\nThere exists no proof.\r\n";
            /** Conclusion message constant. */
            final public static String s2221 = "The conclusion is: ";
            /** Equivalent message constant. */
            final public static String s2222 = "This is equivalent to: ";
            /** Eliminating common factors message constant. */
            final public static String s2223 = "Eliminating the common factors: ";

            /** Geometric quantities used in proof message constant. */
            final public static String s2225 = "\r\nThe geometric quantities used in the proof.\r\n";
            /** Eliminate variables message constant. */
            final public static String s2226 = "Eliminate variables";

            /** Debug flag. */
            final public static boolean DEBUG = false;

            /**
             * Prints the specified string if the debug flag is set.
             *
             * @param s the string to print
             */
            public static void print(String s) {
                if (DEBUG)
                    System.out.println(s);
            }
        }