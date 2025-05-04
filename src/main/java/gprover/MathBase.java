package gprover;

/**
 * Provides basic arithmetic, string, and array manipulation methods.
 */
public class MathBase extends GDDBc {

    /**
     * Compares two character arrays lexicographically.
     *
     * @param p1 the first character array
     * @param p2 the second character array
     * @return a negative, zero, or positive integer as p1 is less than, equal to, or greater than p2
     */
    int strcmp(char[] p1, char[] p2) {
        int l1, l2;
        if (p1 == null && p2 == null)
            return 0;
        else if (p1 == null)
            return -1;
        else if (p2 == null) return 1;

        l1 = p1.length;
        l2 = p2.length;
        for (int i = 0; i < p1.length && i < p2.length; i++) {
            if (p1[i] > p2[i])
                return 1;
            else if (p1[i] < p2[i]) return -1;
        }
        if (l1 == l2) return 0;
        if (l1 > l2) return 1;
        return -1;
    }

    /**
     * Compares two strings lexicographically.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @return a negative, zero, or positive integer as s1 is less than, equal to, or greater than s2
     */
    int strcmp(String s1, String s2) {
        if (s1 != null)
            return s1.compareTo(s2);
        else if (s2 == null)
            return 0;
        else
            return -1;
    }

    /**
     * Copies the source character array to the destination array.
     *
     * @param p the destination character array
     * @param s the source character array
     * @return true if the copy was successful
     */
    boolean strcpy(char[] p, char[] s) {
        if (s == null) return true;

        int len = p.length;
        for (int i = 0; i < len; i++)
            p[i] = '\0';
        for (int i = 0; i < len && i < s.length; i++)
            p[i] = s[i];
        return true;
    }

    /**
     * Determines if the given XTerm represents a number.
     *
     * @param p the XTerm to check
     * @return true if p represents a number, false otherwise
     */
    boolean numberp(XTerm p) {
        return (p.var == null);
    }

    /**
     * Determines if the given XTerm represents a polynomial.
     *
     * @param p the XTerm to check
     * @return true if p represents a polynomial, false otherwise
     */
    boolean npoly(XTerm p) {
        return (p.var == null);
    }

    /**
     * Determines if the given XTerm represents zero.
     *
     * @param p the XTerm to check
     * @return true if p represents zero, false otherwise
     */
    boolean pzerop(XTerm p) {
        return ((p.var == null) && (num_zop(p.c)));
    }

    /**
     * Determines if the given XTerm represents the unit value 1.
     *
     * @param p the XTerm to check
     * @return true if p represents 1, false otherwise
     */
    boolean unitp(XTerm p) {
        return ((p.var == null) && (p.c == mk_num(1L)));
    }

    /**
     * Determines if the given XTerm represents the negative unit value -1.
     *
     * @param p the XTerm to check
     * @return true if p represents -1, false otherwise
     */
    boolean nunitp(XTerm p) {
        return ((p.var == null) && (p.c == mk_num(-1L)));
    }

    /**
     * Checks whether the given number is zero.
     *
     * @param x the number to check
     * @return true if x is zero, false otherwise
     */
    boolean num_zop(long x) {
        return (x) == 0L;
    }

    /**
     * Checks whether the given number is positive.
     *
     * @param x the number to check
     * @return true if x is positive, false otherwise
     */
    boolean num_posp(long x) {
        return (x) > 0L;
    }

    /**
     * Checks whether the given number is negative.
     *
     * @param x the number to check
     * @return true if x is negative, false otherwise
     */
    boolean num_negp(long x) {
        return x < 0L;
    }

    /**
     * Creates a numeric representation from a long value.
     *
     * @param x the input value
     * @return the numeric representation of x
     */
    long mk_num(long x) {
        return x;
    }

    /**
     * Computes the sum of two numbers.
     *
     * @param x the first number
     * @param y the second number
     * @return the sum of x and y
     */
    long num_p(long x, long y) {
        return x + y;
    }

    /**
     * Computes the product of two numbers.
     *
     * @param x the first number
     * @param y the second number
     * @return the product of x and y
     */
    long num_t(long x, long y) {
        return x * y;
    }

    /**
     * Computes the integer division of one number by another.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the quotient of x divided by y
     */
    long num_d(long x, long y) {
        return x / y;
    }

    /**
     * Returns the negation of the given number.
     *
     * @param x the number to negate
     * @return the negated value of x
     */
    long num_neg(long x) {
        return -x;
    }

    /**
     * Computes the remainder when one number is divided by another.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the remainder of x divided by y
     */
    long num_mod(long x, long y) {
        return ((x) % (y));
    }

    /**
     * Computes the modulus of a number by 2.
     *
     * @param x the number to evaluate
     * @return the remainder of x divided by 2
     */
    long num_modt(long x) {
        return ((x) % 2L);
    }

    /**
     * Checks if the given number is the unit value 1.
     *
     * @param p the number to check
     * @return true if p is 1, false otherwise
     */
    boolean num_unit(long p) {
        return (p) == 1L;
    }

    /**
     * Checks if the given number is the negative unit value -1.
     *
     * @param p the number to check
     * @return true if p is -1, false otherwise
     */
    boolean num_nunit(long p) {
        return (p) == (-1L);
    }

    /**
     * Determines the number of digits in the given number.
     *
     * @param x the number to evaluate
     * @return the number of digits in x
     */
    long num_digs(long x) {
        return int_digs(x);
    }

    /**
     * Displays the numeric value by converting it to its string representation.
     *
     * @param x the number to display
     */
    void num_show(long x) {
        int_show(x);
    }

    /**
     * Counts the number of digits in a long integer.
     *
     * @param x the number to evaluate
     * @return the count of digits in x
     */
    int int_digs(long x) {
        int i = 0;
        if (x < 0L) x = -x;
        while (x > 0L) {
            i++;
            x = x / 10;
        }
        return (i);
    }

    /**
     * Converts a long number to an integer.
     *
     * @param c the number to convert
     * @return the integer representation of c
     */
    int num_int(long c) {
        return (int) c;
    }

    /**
     * Displays the integer value by printing its string representation.
     *
     * @param x the number to display
     */
    void int_show(long x) {
        gprint(Integer.toString((int) x));
    }

    /**
     * Computes the greatest common divisor of two numbers.
     *
     * @param l1 the first number
     * @param l2 the second number
     * @return the greatest common divisor of l1 and l2
     */
    long lgcd(long l1, long l2) {
        long l;
        if (l1 < 0L) {
            l1 = -l1;
        }
        if (l2 < 0L) {
            l2 = -l2;
        }
        if (l1 > l2) {
            l = l1;
            l1 = l2;
            l2 = l;
        }
        while (l1 != 0L) {
            l = l2 % l1;
            l2 = l1;
            l1 = l;
        }
        return (l2);
    }
}
