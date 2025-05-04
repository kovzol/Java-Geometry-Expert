package wprover;

import maths.TMono;

/**
 * CTextValue is a class that represents a mathematical expression in a tree structure.
 * It can parse a string representation of the expression, evaluate it, and perform various
 * mathematical functions.
 */
public class CTextValue {

    final static String SPI = "π";
    final static String SE = "e";
    final public static String[] sfunction = {"sin", "cos", "tan", "arcsin", "arccos", "arctan",
            "abs", "sqrt", "ln", "log", "sgn", "round", "trunc", "area"};

    final static int PLUS = 1;
    final static int MINUS = 2;
    final static int MUL = 3;
    final static int DIV = 4;
    final static int SQRT = 5;
    final static int SQURAR = 6;
    final static int CUBE = 7;
    final static int SIN = 8;
    final static int COS = 9;
    final static int TAN = 10;
    final static int CTAN = 11;

    final static int EXP = 12;
    final static int NODE = 13;
    final static int VALUE = 14;
    final static int FUNC = 15;
    final static int PARAM = 16;
    final static int AREA = 17;


    final static int PI = 20;
    final static int E = 21;


    int TYPE;

    String sname = "";
    int value = 1;
    int index = -1;
    double dvalue;

    CTextValue left;
    CTextValue right;

    /**
     * Default constructor for CTextValue.
     */
    public CTextValue() {
    }

    /**
     * Constructor for CTextValue with a specified type.
     *
     * @param t the type of the CTextValue
     */
    private CTextValue(int t) {
        TYPE = t;
    }

    /**
     * Parses a string representation of a mathematical expression into a CTextValue object.
     *
     * @param str the string representation of the expression
     * @return the parsed CTextValue object
     */
    public static CTextValue parseString(String str) {
        TMono index = new TMono(0, 0, 0);

        return parseEntityA(str.toCharArray(), index);
    }

    /**
     * Parses a byte array representation of a mathematical expression into a CTextValue object.
     *
     * @param src the byte array representation of the expression
     * @param index the index used for parsing
     * @return the parsed CTextValue object
     */
    public static CTextValue parse(byte[] src, TMono index) {
        return null;
    }

    /**
     * Gets the function index for a given function name.
     *
     * @param s the function name
     * @return the index of the function, or -1 if not found
     */
    public static int getFunction(String s) {
        for (int i = 0; i < sfunction.length; i++) {
            if (s.equalsIgnoreCase(sfunction[i]))
                return i;
        }
        return -1;
    }

    /**
     * Parses a function name from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed function name
     */
    public static String parseFunction(char[] src, TMono index) {
        parseSpace(src, index);
        int i = index.x;
        String s = new String();

        if (src[i] == 'x' || src[i] == 'X') return s;

        while ((src[i] >= '0' && src[i] <= '9') || (src[i] >= 'a' && src[i] <= 'z') || (src[i] >= 'A' && src[i] <= 'Z')) {
            char[] bb = new char[1];
            bb[0] = src[i];
            s += new String(bb);
            i++;
            if (i >= src.length) break;
        }
        index.x = i;
        return s;
    }

    /**
     * Parses an entity of type A from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed CTextValue object
     */
    private static CTextValue parseEntityA(char[] src, TMono index) {

        CTextValue ct1 = parseEntityB(src, index);
        parseSpace(src, index);

        int i = index.x;
        if (i >= src.length)
            return ct1;

        char b = src[i];
        while (b == '+' || b == '-') {

            index.x++;

            CTextValue ct2 = parseEntityB(src, index);
            if (ct2 == null)
                break;

            CTextValue ct = new CTextValue();
            if (b == '+')
                ct.TYPE = PLUS;
            else if (b == '-')
                ct.TYPE = MINUS;
            ct.left = ct1;
            ct.right = ct2;
            ct1 = ct;
            parseSpace(src, index);
            i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        return ct1;
    }

    /**
     * Parses an entity of type B from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed CTextValue object
     */
    private static CTextValue parseEntityB(char[] src, TMono index) {

        parseSpace(src, index);
        CTextValue ct1 = parseEntityC(src, index);
        parseSpace(src, index);
        if (index.x >= src.length)
            return ct1;

        char b = src[index.x];
        while (b == '*' || b == '/') {
            index.x++;
            CTextValue ct2 = parseEntityC(src, index);
            if (ct2 == null)
                break;

            CTextValue ct = new CTextValue();
            if (b == '*')
                ct.TYPE = MUL;
            else if (b == '/')
                ct.TYPE = DIV;

            ct.left = ct1;
            ct.right = ct2;
            ct1 = ct;
            parseSpace(src, index);
            int i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        return ct1;
    }

    /**
     * Parses an entity of type C from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed CTextValue object
     */
    private static CTextValue parseEntityC(char[] src, TMono index) { // ^
        parseSpace(src, index);
        CTextValue t1 = parseEntityD(src, index);
        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length)
            return t1;

        CTextValue vl = null;

        char b = src[i];
        while (b == '^') {
            index.x++;
            CTextValue t2 = parseEntityD(src, index);

            if (vl == null) {
                vl = new CTextValue(EXP);
                vl.left = t1;
                vl.right = t2;
            } else {
                CTextValue v = new CTextValue(EXP);
                CTextValue t = vl;
                while (t.right.right != null)
                    t = t.right;
                v.left = t.right;
                v.right = t2;
                t.right = v;
            }

            parseSpace(src, index);
            i = index.x;
            if (i >= src.length)
                break;
            b = src[i];
        }
        if (vl != null)
            return vl;
        else return t1;
    }

    /**
     * Parses an entity of type D from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed CTextValue object
     */
    private static CTextValue parseEntityD(char[] src, TMono index) { // (), x1,value.


        parseSpace(src, index);
        int i = index.x;
        if (i >= src.length)
            return null;

        if (src[i] == '(') {
            index.x = i + 1;
            CTextValue ct = parseEntityA(src, index);
            parseSpace(src, index);
            index.x++;
            return ct;
        } else if (src[i] == 'x' || src[i] == 'X') {
            index.x++;
            CTextValue t1 = new CTextValue(NODE);
            int v = (int) parseInt(src, index);
            t1.TYPE = NODE;
            t1.index = v;
            return t1;
        } else if (src[i] >= '0' && src[i] <= '9') {
            double v = parseInt(src, index);
            CTextValue t1 = new CTextValue(NODE);
            t1.dvalue = v;
            t1.TYPE = VALUE;
            return t1;
        } else if (src[i] == 960){
            CTextValue t1 = new CTextValue(PI);
            index.x++;
            return t1;
        } else if (src[i] == 'e') {
            CTextValue t1 = new CTextValue(E);
            index.x++;
            return t1;
        } else {
            String s = parseFunction(src, index);
            if (s.length() != 0) {
                int fn = getFunction(s);
                parseSpace(src, index);

                int d = index.x;
//                index.x++;
//
//                int d = index.x;
                if (d < src.length && src[d] == '(') { // function.
                    CTextValue t1 = new CTextValue();
                    t1.TYPE = FUNC;
                    t1.value = fn;
                    index.x++;      //(
                    parseSpace(src, index);
                    t1.left = parseEntityA(src, index);
                    parseSpace(src, index);
                    index.x++;      //)
                    return t1;
                } else // parameter.
                {
                    CTextValue t1 = new CTextValue();
                    t1.TYPE = PARAM;
                    t1.sname = s;
                    return t1;
                }
            }
        }

        CMisc.print("Error input polynomial type in CTextValue");
        return null;

    }

    /**
     * Parses an integer value from the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     * @return the parsed integer value
     */
    private static double parseInt(char[] src, TMono index) {

        parseSpace(src, index);

        int i = index.x;
        if (i >= src.length)
            return 0.0;

        char b = src[i];
        double v = 0;

        double d = 0.1;

        int step = 0; // 0. Befor  1.after.
        while (b >= '0' && b <= '9' || b == '.') {
            if (b == '.')
                step = 1;
            else if (step == 0) {
                v *= 10;
                v += (b - '0');
            } else {
                v += (b - '0') * d;
                d *= 0.1;
            }

            i++;
            index.x++;
            if (i >= src.length) break;

            b = src[i];
        }

        parseSpace(src, index);
        return v;
    }

    /**
     * Parses and skips spaces in the source character array.
     *
     * @param src the source character array
     * @param index the index used for parsing
     */
    private static void parseSpace(char[] src, TMono index) {

        int i = index.x;

        if (i >= src.length) return;

        while (i < src.length && src[i] == ' ')
            i++;

        index.x = i;

    }

    /**
     * Rounds a double value to three decimal places.
     *
     * @param r the double value to round
     * @return the rounded double value
     */
    public static double roud3(double r) {
        return Math.round(r * 1000 + 0.1) / 1000.0;
    }

    /**
     * Calculates the value of this CTextValue object using the specified DrawProcess.
     *
     * @param dp the DrawProcess used for calculation
     */
    public void calculate(DrawProcess dp) {
        double r = calvalue(this, dp);
        this.dvalue = roud3(r);
    }

    /**
     * Calculates the value of the specified CTextValue object using the specified DrawProcess.
     *
     * @param ct the CTextValue object to calculate
     * @param dp the DrawProcess used for calculation
     * @return the calculated double value
     */
    public static double calvalue(CTextValue ct, DrawProcess dp) {
        if (ct == null) return 0.0;
        return dp.calculate(ct);
    }

    /**
     * Calculates the result of a mathematical function on a given value.
     *
     * @param n the function index
     * @param v the value to apply the function to
     * @return the result of the function
     */
    public static double cal_func(int n, double v) {
        switch (n) {
            case 0:
                return Math.sin(v);
            case 1:
                return Math.cos(v);
            case 2:
                return Math.tan(v);
            case 3:
                return Math.asin(v);
            case 4:
                return Math.acos(v);
            case 5:
                return Math.atan(v);
            case 6:
                return Math.abs(v);
            case 7:
                return Math.sqrt(v);
            case 8:
                return Math.log10(v);
            case 9:
                return Math.log(v);
            case 10:
                return Math.signum(v);
            case 11:
                return Math.round(v);
            case 12:
                return v;//////???
        }
        return v;
    }
}
