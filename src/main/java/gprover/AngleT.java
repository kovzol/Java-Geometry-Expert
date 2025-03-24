package gprover;

    /**
     * The AngleT class represents an angle with an intersection.
     * It extends the CClass and includes properties for lemma, points, lines,
     * and other attributes related to the angle.
     */
    public class AngleT extends CClass {   // angle with intersection;

        /** The lemma associated with the angle. */
        public int lemma;

        /** The point associated with the angle. */
        public int p;

        /** The first line that defines the angle. */
        public LLine l1;

        /** The second line that defines the angle. */
        public LLine l2;

        /** An integer value associated with the angle. */
        public int v;

        /** The condition associated with the angle. */
        Cond co;

        /** The next AngleT object in a linked list structure. */
        AngleT nx;

        /**
         * Constructs an AngleT object with default values.
         */
        public AngleT() {
            p = 0;
            l1 = l2 = null;
            v = 0;
            nx = null;
        }

        /**
         * Constructs an AngleT object with the specified values.
         *
         * @param p the point associated with the angle
         * @param l1 the first line that defines the angle
         * @param l2 the second line that defines the angle
         * @param v an integer value associated with the angle
         */
        public AngleT(int p, LLine l1, LLine l2, int v) {
            this();
            this.p = p;
            this.l1 = l1;
            this.l2 = l2;
            this.v = v;
        }

        /**
         * Gets the first point of the first line that is not equal to the point p.
         *
         * @return the first point of the first line that is not equal to the point p
         */
        public int get_pt1() {
            if (l1.pt[0] == p)
                return l1.pt[1];
            else
                return l1.pt[0];
        }

        /**
         * Gets the first point of the second line that is not equal to the point p.
         *
         * @return the first point of the second line that is not equal to the point p
         */
        public int get_pt2() {
            if (l2.pt[0] == p)
                return l2.pt[1];
            else
                return l2.pt[0];
        }

        /**
         * Gets the value associated with the angle based on the points p1 and p2.
         *
         * @param p1 the first point
         * @param p2 the second point
         * @return the value associated with the angle, or 9999 if the points do not lie on the lines
         */
        public int get_val(int p1, int p2) {
            if (l1.on_ln(p1) && l2.on_ln(p2)) return v;
            if (l1.on_ln(p2) && l2.on_ln(p1)) return -v;
            return 9999;                // shall never happen.
        }
    }