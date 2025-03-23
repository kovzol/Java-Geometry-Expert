package maths;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides methods to calculate square roots and cube roots with arbitrary precision.
 */
public class BigSquareRoot {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TWO = new BigDecimal("2");
    public static final int DEFAULT_MAX_ITERATIONS = 50;
    public static final int DEFAULT_SCALE = 10;

    private static int maxIterations = DEFAULT_MAX_ITERATIONS;

    /**
     * Gets an initial approximation for the square root computation.
     *
     * @param n the BigDecimal for which to compute the initial approximation
     * @return the initial guess as a BigDecimal
     */
    private static BigDecimal getInitialApproximation(BigDecimal n) {
        BigInteger integerPart = n.toBigInteger();
        int length = integerPart.toString().length();
        if ((length % 2) == 0) {
            length--;
        }
        length /= 2;
        BigDecimal guess = ONE.movePointRight(length);
        return guess;
    }

    /**
     * Calculates the square root of a BigInteger.
     *
     * @param n the BigInteger for which to calculate the square root
     * @return the square root as a BigDecimal
     * @throws IllegalArgumentException if n is non-positive
     */
    public static BigDecimal get(BigInteger n) {
        return get(new BigDecimal(n));
    }

    /**
     * Calculates the square root of a BigDecimal.
     *
     * @param n the BigDecimal for which to calculate the square root
     * @return the square root as a BigDecimal
     * @throws IllegalArgumentException if n is non-positive
     */
    private static BigDecimal get(BigDecimal n) {
        if (n.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal initialGuess = getInitialApproximation(n);
        BigDecimal lastGuess = ZERO;
        BigDecimal guess = new BigDecimal(initialGuess.toString());

        int iterations = 0;
        int scale = DEFAULT_SCALE;
        int length = n.toString().length();
        if (length > 20)
            scale = (length / 2);

        boolean more = true;
        BigDecimal error;
        while (more) {
            lastGuess = guess;
            guess = n.divide(guess, scale, BigDecimal.ROUND_HALF_UP);
            guess = guess.add(lastGuess);
            guess = guess.divide(TWO, scale, BigDecimal.ROUND_HALF_UP);
            error = n.subtract(guess.multiply(guess));
            if (++iterations >= maxIterations) {
                more = false;
            } else if (lastGuess.equals(guess)) {
                more = error.abs().compareTo(ONE) >= 0;
            }
        }
        return guess;
    }

    /**
     * Calculates the square root of a BigInteger.
     *
     * @param b the BigInteger for which to calculate the square root
     * @return the square root as a BigDecimal
     */
    public static BigDecimal sqrt(BigInteger b) {
        return get(b);
    }

    /**
     * Calculates the integer square root of a BigInteger if it is a perfect square.
     *
     * @param b the BigInteger for which to calculate the integer square root
     * @return the integer square root if perfect; null otherwise
     */
    public static BigInteger sqrtI(BigInteger b) {
        BigDecimal b1 = sqrt(b);
        BigInteger b2 = b1.toBigInteger();
        if (b2.multiply(b2).subtract(b).compareTo(BigInteger.ZERO) == 0)
            return b2;
        return null;
    }
}