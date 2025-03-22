package maths;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Represents a fraction with arbitrary precision using BigInteger.
 */
public class BigFraction implements Cloneable, Comparable, Serializable {
    protected final BigInteger numerator_;
    protected final BigInteger denominator_;

    final public static BigFraction ZERO = new BigFraction("0/1");
    final public static BigFraction ONE = new BigFraction("1/1");

    /**
     * Returns the numerator as a BigInteger.
     *
     * @return the numerator as a BigInteger
     */
    public BigInteger numerator() {
        return numerator_;
    }

    /**
     * Returns the denominator as a BigInteger.
     *
     * @return the denominator as a BigInteger
     */
    public BigInteger denominator() {
        return denominator_;
    }

    /**
     * Constructs a BigFraction with the specified numerator and denominator.
     *
     * @param num the numerator
     * @param den the denominator
     */
    public BigFraction(BigInteger num, BigInteger den) {

        boolean numNonnegative = gteq(num, BigInteger.ZERO);
        boolean denNonnegative = gteq(den, BigInteger.ZERO);
        BigInteger a = numNonnegative ? num : num.negate();
        BigInteger b = denNonnegative ? den : den.negate();
        BigInteger g = a.gcd(b);
        if (numNonnegative == denNonnegative) {
            numerator_ = a.divide(g);
        } else {
            numerator_ = a.negate().divide(g);
        }
        denominator_ = b.divide(g);
    }

    /**
     * Constructs a BigFraction with the specified BigInteger numerator.
     *
     * @param b the numerator
     */
    public BigFraction(BigInteger b) {
        this(b, BigInteger.valueOf(1));
    }

    /**
     * Copy constructor for BigFraction.
     *
     * @param f the BigFraction to copy
     */
    public BigFraction(BigFraction f) {
        numerator_ = f.numerator();
        denominator_ = f.denominator();
    }

    /**
     * Constructs a BigFraction from a string representation.
     *
     * @param s the string representation of the fraction
     */
    public BigFraction(String s) {
        this(new BigInteger(s.substring(0, s.indexOf('/'))),
                new BigInteger(s.substring(s.indexOf('/') + 1)));
    }

    /**
     * Constructs a BigFraction with the specified long numerator and denominator.
     *
     * @param num the numerator
     * @param den the denominator
     */
    public BigFraction(long num, long den) {
        this(new BigInteger(Long.toString(num)),
                new BigInteger(Long.toString(den)));
    }

    /**
     * Returns a string representation of the fraction.
     *
     * @return the string representation of the fraction
     */
    @Override
    public String toString() {
        BigInteger b2 = denominator();

        if (b2.compareTo(BigInteger.ONE) != 0)
            return numerator().toString() + "/" + denominator().toString();
        else
            return numerator().toString();
    }

    /**
     * Creates and returns a copy of this BigFraction.
     *
     * @return a clone of this BigFraction
     */
    public Object clone() {
        return new BigFraction(this);
    }

    /**
     * Checks if this fraction is greater than or equal to the specified BigInteger.
     *
     * @param x the first BigInteger
     * @param y the second BigInteger
     * @return true if x is greater than or equal to y; false otherwise
     */
    private boolean gteq(BigInteger x, BigInteger y) {
        return x.compareTo(y) >= 0;
    }

    /**
     * Checks if the numerator is greater than or equal to the specified BigInteger.
     *
     * @param y the BigInteger to compare with
     * @return true if the numerator is greater than or equal to y; false otherwise
     */
    private boolean gteq(BigInteger y) {
        return numerator_.compareTo(y) >= 0;
    }

    /**
     * Checks if this fraction is less than the specified BigInteger.
     *
     * @param x the first BigInteger
     * @param y the second BigInteger
     * @return true if x is less than y; false otherwise
     */
    private boolean lt(BigInteger x, BigInteger y) {
        return x.compareTo(y) < 0;
    }


    /**
     * Returns the minimum of this fraction and the specified fraction.
     *
     * @param val the fraction to compare with
     * @return the minimum fraction
     */
    public BigFraction min(BigFraction val) {
        if (compareTo(val) <= 0) {
            return this;
        } else {
            return val;
        }
    }

    /**
     * Returns the maximum of this fraction and the specified fraction.
     *
     * @param val the fraction to compare with
     * @return the maximum fraction
     */
    public BigFraction max(BigFraction val) {
        if (compareTo(val) > 0) {
            return this;
        } else {
            return val;
        }
    }

    /**
     * Raises this fraction to the power of the specified integer.
     *
     * @param d the exponent
     * @return the resulting fraction
     */
    public BigFraction pow(int d) {
        BigInteger an = numerator();
        BigInteger ad = denominator();
        return new BigFraction(an.pow(d), (ad.pow(d)));
    }

    /**
     * Adds the specified fraction to this fraction.
     *
     * @param b the fraction to add
     * @return the resulting fraction
     */
    public BigFraction add(BigFraction b) {
        BigInteger an = numerator();
        BigInteger ad = denominator();
        BigInteger bn = b.numerator();
        BigInteger bd = b.denominator();
        return new BigFraction(an.multiply(bd).add(bn.multiply(ad)), ad.multiply(bd));
    }

    /**
     * Adds the specified BigInteger to this fraction.
     *
     * @param n the BigInteger to add
     * @return the resulting fraction
     */
    public BigFraction add(BigInteger n) {
        return add(new BigFraction(n, BigInteger.ONE));
    }

    /**
     * Adds the specified long value to this fraction.
     *
     * @param n the long value to add
     * @return the resulting fraction
     */
    public BigFraction add(long n) {
        return add(new BigInteger(Long.toString(n)));
    }

    /**
     * Multiplies this fraction by the specified fraction.
     *
     * @param b the fraction to multiply by
     * @return the resulting fraction
     */
    public BigFraction multiply(BigFraction b) {
        BigInteger an = numerator();
        BigInteger ad = denominator();
        BigInteger bn = b.numerator();
        BigInteger bd = b.denominator();
        return new BigFraction(an.multiply(bn), ad.multiply(bd));
    }

    /**
     * Multiplies this fraction by the specified BigInteger.
     *
     * @param n the BigInteger to multiply by
     * @return the resulting fraction
     */
    public BigFraction multiply(BigInteger n) {
        return multiply(new BigFraction(n, BigInteger.ONE));
    }

    /**
     * Divides this fraction by the specified fraction.
     *
     * @param b the fraction to divide by
     * @return the resulting fraction
     */
    public BigFraction divide(BigFraction b) {
        BigInteger an = numerator();
        BigInteger ad = denominator();
        BigInteger bn = b.numerator();
        BigInteger bd = b.denominator();
        return new BigFraction(an.multiply(bd), ad.multiply(bn));
    }

    /**
     * Divides this fraction by the specified BigInteger.
     *
     * @param n the BigInteger to divide by
     * @return the resulting fraction
     */
    public BigFraction divide(BigInteger n) {
        return divide(new BigFraction(n, BigInteger.ONE));
    }

    /**
     * Calculates the square root of this fraction if it is non-negative.
     *
     * @return the square root as a BigFraction, or null if not a perfect square
     */
    public BigFraction sqrt() {
        if (!gteq(BigInteger.ZERO))
            return null;

        BigInteger an = numerator();
        BigInteger ad = denominator();
        an = BigSquareRoot.sqrtI(an);
        ad = BigSquareRoot.sqrtI(ad);
        if (an != null && ad != null)
            return new BigFraction(an, ad);

        return null;
    }

    /**
     * Checks if the numerator is zero.
     *
     * @return true if the numerator is zero; false otherwise
     */
    public boolean isZero() {
        return numerator().compareTo(BigInteger.ZERO) == 0;
    }

    /**
     * Compares this fraction with another object.
     *
     * @param other the object to compare with
     * @return -1, 0, or 1 as this fraction is less than, equal to, or greater than the given object
     */
    public int compareTo(Object other) {
        BigFraction b = (BigFraction) (other);
        BigInteger an = numerator();
        BigInteger ad = denominator();
        BigInteger bn = b.numerator();
        BigInteger bd = b.denominator();
        BigInteger left = an.multiply(bd);
        BigInteger right = bn.multiply(ad);
        if (lt(left, right)) {
            return -1;
        }
        if (left.equals(right)) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Compares this fraction with a BigInteger.
     *
     * @param n the BigInteger to compare with
     * @return -1, 0, or 1 as this fraction is less than, equal to, or greater than the given BigInteger
     */
    public int compareTo(BigInteger n) {
        Object obj = new BigFraction(n, BigInteger.ONE);
        return compareTo(obj);
    }

    /**
     * Checks if this fraction is equal to another object.
     *
     * @param other the object to compare with
     * @return true if the fractions are equal; false otherwise
     */
    public boolean equals(Object other) {
        return compareTo((BigFraction) other) == 0;
    }

    /**
     * Checks if this fraction is equal to a BigInteger.
     *
     * @param n the BigInteger to compare with
     * @return true if equal; false otherwise
     */
    public boolean equals(BigInteger n) {
        return compareTo(n) == 0;
    }

    /**
     * Checks if this fraction is equal to a long value.
     *
     * @param n the long value to compare with
     * @return true if equal; false otherwise
     */
    public boolean equals(long n) {
        return equals(new BigInteger(Long.toString(n)));
    }

    /**
     * Returns a hash code for this fraction.
     *
     * @return the hash code computed from numerator and denominator
     */
    public int hashCode() {
        int num = numerator().intValue();
        int den = denominator().intValue();
        return num ^ den;
    }
}
