package wprover;

import maths.TMono;

/**
 * Parser is a class that parses a mathematical expression represented as a string
 * and converts it into a TMono object.
 */
public class Parser {
    private static GeoPoly poly = GeoPoly.getPoly();

    TMono m1;
    String sname;
    String sfunc;
    int id;
    int param;

    /**
     * Constructs a new Parser with the specified name, function, and parameter.
     *
     * @param n the name of the parser
     * @param f the function to parse
     * @param x the parameter for the parser
     */
    public Parser(String n, String f, int x) {
        m1 = null;
        sname = n;
        sfunc = f;
        id = 0;
        param = x;
    }

    /**
     * Parses the function string and converts it into a TMono object.
     *
     * @return the TMono object representing the parsed function
     */
    public TMono parse() {
        byte[] bf = sfunc.getBytes();
        byte[] nm = sname.getBytes();
        id = 0;
        parseterm(true, bf, nm);
        return m1;
    }

    /**
     * Checks if the given byte represents a numeric character.
     *
     * @param b the byte to check
     * @return true if the byte is a numeric character, false otherwise
     */
    public boolean isNum(byte b) {
        return b >= '0' && b <= '9';
    }

    /**
     * Checks if the given byte represents an alphabetic character.
     *
     * @param b the byte to check
     * @return true if the byte is an alphabetic character, false otherwise
     */
    public boolean isAlpha(byte b) {
        return b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z';
    }

    /**
     * Parses a term from the function string and updates the TMono object.
     *
     * @param first indicates if this is the first term being parsed
     * @param bf    the byte array representing the function string
     * @param nm    the byte array representing the name string
     */
    public void parseterm(boolean first, byte[] bf, byte[] nm) {
        while (true) {
            if (id >= bf.length)
                return;
            while (bf[id] == 32) {
                if (id >= bf.length)
                    return;
                id++;
            }

            if (id >= bf.length)
                return;

            if (!first) {
                if (bf[id] == '+') {
                    id++;
                    m1 = poly.padd(m1, getAterm(bf, nm));
                } else if (bf[id] == '-') {
                    id++;
                    m1 = poly.pdif(m1, getAterm(bf, nm));
                }
            } else {
                first = false;
                m1 = poly.padd(m1, getAterm(bf, nm));
            }
        }
    }

    /**
     * Skips blank spaces in the function string.
     *
     * @param bf the byte array representing the function string
     * @param nm the byte array representing the name string
     */
    public void parseBlank(byte[] bf, byte[] nm) {
        while (bf[id] == 32) {
            if (id >= bf.length)
                return;
            id++;
        }
    }

    /**
     * Parses a term from the function string and returns it as a TMono object.
     *
     * @param bf the byte array representing the function string
     * @param nm the byte array representing the name string
     * @return the TMono object representing the parsed term
     */
    public TMono getAterm(byte[] bf, byte[] nm) {
        parseBlank(bf, nm);

        int value = 0;
        int coef = 1;
        while (id < bf.length && isNum(bf[id])) {
            value = value * coef + (bf[id] - '0');
            coef *= 10;
            id++;
        }
        if (value == 0)
            value = 1;

        if (id < bf.length && isAlpha(bf[id])) {
            id += nm.length;
        }
        int n = 0;

        if (id < bf.length) {
            if (bf[id] == '^') {
                id++;
                coef = 1;
                while (id < bf.length && isNum(bf[id])) {
                    n = n * coef + (bf[id] - '0');
                    coef *= 10;
                    id++;
                }
            }
        }
        if (n != 0)
            return poly.pth(param, value, n);
        else return
                poly.pth(0, value, 0);
    }
}
