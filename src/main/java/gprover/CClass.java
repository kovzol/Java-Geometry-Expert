/**
     * The CClass class represents a base class for geometric objects.
     * It includes properties for unique identification, depth, type, and text description.
     */
    package gprover;

    public class CClass {
        /** The maximum number of geometric objects. */
        final public static int MAX_GEO = 40;

        /** A static counter for generating unique IDs. */
        public static long id_count = 0;

        /** The unique ID of the geometric object. */
        long id = id_count++;

        /** The depth of the geometric object. */
        long dep = Gib.depth;

        /** The type of the geometric object. */
        int type;

        /** The text description of the geometric object. */
        String text;

        /**
         * Returns a string representation of the geometric object.
         *
         * @return the text description of the geometric object
         */
        @Override
        public String toString() {
            return text;
        }
    }
