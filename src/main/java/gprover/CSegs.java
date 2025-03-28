package gprover;

        /**
         * The CSegs class represents a collection of geometric segments in a proof.
         * It includes properties for the number of segments, arrays of points defining the segments,
         * and a reference to the next segment in the list.
         */
        public class CSegs extends CClass {
            /** The number of segments. */
            public int no;

            /** The array of starting points of the segments. */
            public int[] p1;

            /** The array of ending points of the segments. */
            public int[] p2;

            /** The next segment in the list. */
            CSegs nx;

            /**
             * Constructs a CSegs object with default values.
             */
            public CSegs() {
                type = no = 0;
                p1 = new int[MAX_GEO * 2];
                p2 = new int[MAX_GEO * 2];
                nx = null;
            }
        }
