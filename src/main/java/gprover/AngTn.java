package gprover;

    /**
     * The AngTn class represents a geometric configuration of four lines.
     * It extends the CClass and includes properties for lemma, condition,
     * and other attributes related to angles.
     */
    public class AngTn extends CClass {
        /** The lemma associated with the angles. */
        int lemma;

        /** The first line that defines the angles. */
        public LLine ln1;

        /** The second line that defines the angles. */
        public LLine ln2;

        /** The third line that defines the angles. */
        public LLine ln3;

        /** The fourth line that defines the angles. */
        public LLine ln4;

        /** The first integer attribute related to the angles. */
        public int t1;

        /** The second integer attribute related to the angles. */
        public int t2;

        /** The condition associated with the angles. */
        Cond co;

        /** The next AngTn object in a linked list structure. */
        AngTn nx;

        /**
         * Constructs an AngTn object with the specified lines.
         *
         * @param l1 the first line
         * @param l2 the second line
         * @param l3 the third line
         * @param l4 the fourth line
         */
        public AngTn(LLine l1, LLine l2, LLine l3, LLine l4) {
            this();
            ln1 = l1;
            ln2 = l2;
            ln3 = l3;
            ln4 = l4;
        }

        /**
         * Constructs an AngTn object with default values.
         */
        public AngTn() {
            ln1 = ln2 = ln3 = ln4 = null;
            co = null;
            nx = null;
            t1 = t2 = lemma = 0;
        }
    }