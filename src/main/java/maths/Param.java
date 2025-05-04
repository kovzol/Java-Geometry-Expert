package maths;

    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.io.DataInputStream;

    /**
     * Represents a parameter with a type, index, value, and optional monomial.
     */
    public class Param {
        public static int STATIC = 1;

        public int type = 0;
        public int xindex;
        public double value;

        public boolean Solved = false;
        public TMono m = null;

        /**
         * Default constructor for Param.
         */
        public Param() {
        }

        /**
         * Constructs a Param with the specified index and value.
         *
         * @param index the index of the parameter
         * @param val the value of the parameter
         */
        public Param(int index, double val) {
            xindex = index;
            value = val;
        }

        /**
         * Sets the parameter type to static.
         */
        public void setParameterStatic() {
            type = STATIC;
        }

        /**
         * Returns a string representation of the parameter.
         *
         * @return the string representation of the parameter
         */
        public String toString() {
            String s = "x" + xindex;
            if (m != null && m.deg != 1)
                s += "^" + m.deg;
            return s;
        }

        /**
         * Returns a string representation of the parameter index.
         *
         * @return the string representation of the parameter index
         */
        public String getString() {
            String s = "x" + xindex;
            return s;
        }

        /**
         * Saves the parameter to a DataOutputStream.
         *
         * @param out the DataOutputStream to write to
         * @throws IOException if an I/O error occurs
         */
        public void Save(DataOutputStream out) throws IOException {
            out.writeInt(type);
            out.writeInt(xindex);
            out.writeDouble(value);
            out.writeBoolean(Solved);
        }

        /**
         * Loads the parameter from a DataInputStream.
         *
         * @param in the DataInputStream to read from
         * @throws IOException if an I/O error occurs
         */
        public void Load(DataInputStream in) throws IOException {
            type = in.readInt();
            xindex = in.readInt();
            value = in.readDouble();
            Solved = in.readBoolean();
        }
    }