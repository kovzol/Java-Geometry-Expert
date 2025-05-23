package maths;

import java.math.BigInteger;

/**
 * Represents a monomial in a polynomial.
 */
public class TMono {
    public int x = 0;
    public int deg = 0;
    public BigInteger val = null;
    public TMono coef = null;
    public TMono next = null;

    /**
     * Default constructor.
     */
    public TMono() {
    }

    /**
     * Returns the value of the monomial.
     *
     * @return the value of the monomial as a long
     */
    public long value() {
        if (val != null)
            return val.longValue();
        else {
            if (CharSet.debug())
                System.out.println("val == null");
            return 0L;
        }
    }

    /**
     * Constructs a monomial with the specified variable, value, and degree.
     *
     * @param x the variable
     * @param val the value
     * @param deg the degree
     */
    public TMono(int x, BigInteger val, int deg) {
        this.x = x;
        this.deg = deg;
        if (x == 0 || deg == 0) {
            this.x = 0;
            this.deg = 0;
            this.val = val;
        } else {
            this.coef = new TMono(0, val, 0);
        }

        if (val == null) {
            int k = 0;
        }
    }

    /**
     * Constructs a monomial with the specified variable, value, and degree.
     *
     * @param x the variable
     * @param val the value
     * @param deg the degree
     */
    public TMono(int x, int val, int deg) {
        this.x = x;
        this.deg = deg;
        if (x == 0 || deg == 0) {
            this.x = 0;
            this.deg = 0;
            this.val = BigInteger.valueOf(val);
        } else {
            this.coef = new TMono(0, val, 0);
        }
    }

    /**
     * Constructs a monomial with the specified variable, coefficient, and degree.
     *
     * @param x the variable
     * @param coef the coefficient
     * @param deg the degree
     */
    public TMono(int x, TMono coef, int deg) {
        this.x = x;
        this.deg = deg;
        this.coef = coef;
    }
}

