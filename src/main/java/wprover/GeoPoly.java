package wprover;

import maths.PolyBasic;
import maths.TMono;
import maths.TPoly;
import gprover.Gib;
import gprover.CNdg;

import java.util.Vector;

/**
 * GeoPoly is a singleton class that provides methods for constructing polynomial expressions
 * related to geometric properties and relationships.
 * It extends the PolyBasic class and provides various methods for creating polynomial terms,
 * performing arithmetic operations on them, and checking conditions related to geometry.
 */
public class GeoPoly extends PolyBasic {
    static GeoPoly poly = new GeoPoly();
    static int[] zeron = new int[100];


    /**
     * Private constructor to prevent instantiation.
     */
    private GeoPoly() {
    }

    /**
     * Adds a non-zero integer to the zeron array if it is not already present.
     *
     * @param n the integer to add
     * @return true if the integer was added, false if it was already present or zero
     */
    public static boolean addZeroN(int n) {
        if (n == 0) return false;

        for (int i = 0; true; i++) {
            if (zeron[i] == n)
                return false;
            if (zeron[i] == 0) {
                zeron[i] = n;
                return true;
            }
        }
    }

    /**
     * Clears the zeron array by setting all elements to zero.
     */
    public static void clearZeroN() {
        int i = 0;
        while (i < zeron.length)
            zeron[i++] = 0;
    }

    /**
     * Returns the zeron array.
     *
     * @return the zeron array
     */
    public static int[] getZeron() {
        return zeron;
    }

    /**
     * Returns the singleton instance of GeoPoly.
     *
     * @return the singleton instance of GeoPoly
     */
    public static GeoPoly getPoly() {
        return poly;
    }

    /**
     * Checks if a given integer is present in the zeron array.
     *
     * @param n the integer to check
     * @return true if the integer is present, false otherwise
     */
    public static boolean vzero(int n) {
        for (int i = 0; i < zeron.length && zeron[i] != 0; i++) {
            if (zeron[i] == n)
                return true;
        }
        return false;
    }

    /**
     * Constructs a TMono representing a polynomial term.
     *
     * @param x the variable index
     * @param c the coefficient
     * @param d the degree
     * @return the constructed TMono, or null if the variable index is zero
     */
    TMono ppth(int x, int c, int d) {
        if (vzero(x))
            return null;
        else return pth(x, 1, 1);
    }

    /**
     * Constructs a TMono representing the difference between two polynomial terms.
     *
     * @param x the variable index of the first term
     * @param y the variable index of the second term
     * @return the constructed TMono
     */
    TMono ppdd(int x, int y) {
        if (CMisc.POINT_TRANS) {
            if (vzero(x)) {
                if (vzero(y))
                    return (pzero());
                else
                    return (pth(y, -1, 1));
            } else {
                if (vzero(y))
                    return (pth(x, 1, 1));
                else
                    return (padd(pth(x, 1, 1), pth(y, -1, 1)));
            }
        } else {
            if (x == 0) {
                if (y == 0)
                    return (pzero());
                else
                    return (pth(y, -1, 1));
            } else {
                if (y == 0)
                    return (pth(x, 1, 1));
                else
                    return (padd(pth(x, 1, 1), pth(y, -1, 1)));
            }
        }
    }

    /**
     * Constructs a TMono representing the squared distance between two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @return the constructed TMono
     */
    TMono sqdistance(int x1, int y1, int x2, int y2) {
        return (padd(pRtimes(ppdd(x1, x2), ppdd(x1, x2)),
                pRtimes(ppdd(y1, y2), ppdd(y1, y2))));
    }

    /**
     * Constructs a TMono representing the equality of distances between two pairs of points.
     *
     * @param x1 the x-coordinate of the first point of the first pair
     * @param y1 the y-coordinate of the first point of the first pair
     * @param x2 the x-coordinate of the second point of the first pair
     * @param y2 the y-coordinate of the second point of the first pair
     * @param x3 the x-coordinate of the first point of the second pair
     * @param y3 the y-coordinate of the first point of the second pair
     * @param x4 the x-coordinate of the second point of the second pair
     * @param y4 the y-coordinate of the second point of the second pair
     * @return the constructed TMono
     */
    TMono eqdistance(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return (pdif(sqdistance(x1, y1, x2, y2), sqdistance(x3, y3, x4, y4)));
    }

    /**
     * Constructs a TMono representing the perpendicularity condition between two lines.
     *
     * @param x1 the x-coordinate of the first point of the first line
     * @param y1 the y-coordinate of the first point of the first line
     * @param x2 the x-coordinate of the second point of the first line
     * @param y2 the y-coordinate of the second point of the first line
     * @param x3 the x-coordinate of the first point of the second line
     * @param y3 the y-coordinate of the first point of the second line
     * @param x4 the x-coordinate of the second point of the second line
     * @param y4 the y-coordinate of the second point of the second line
     * @return the constructed TMono
     */
    TMono perpendicular(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return (padd(pRtimes(ppdd(x1, x2), ppdd(x3, x4)),
                pRtimes(ppdd(y1, y2), ppdd(y3, y4))));
    }

    /**
     * Constructs a TMono representing the parallelism condition between two lines.
     *
     * @param x1 the x-coordinate of the first point of the first line
     * @param y1 the y-coordinate of the first point of the first line
     * @param x2 the x-coordinate of the second point of the first line
     * @param y2 the y-coordinate of the second point of the first line
     * @param x3 the x-coordinate of the first point of the second line
     * @param y3 the y-coordinate of the first point of the second line
     * @param x4 the x-coordinate of the second point of the second line
     * @param y4 the y-coordinate of the second point of the second line
     * @return the constructed TMono
     */
    TMono parallel(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return (pdif(pRtimes(ppdd(x3, x4), ppdd(y1, y2)),
                pRtimes(ppdd(x1, x2), ppdd(y3, y4))));
    }

    /**
     * Constructs a TMono representing the collinearity condition of three points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @return the constructed TMono
     */
    TMono collinear(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (pdif(pRtimes(ppdd(x2, x3), ppdd(y1, y2)),
                pRtimes(ppdd(x1, x2), ppdd(y2, y3))));
    }

    /**
     * Constructs a TMono representing the ratio condition between two sets of points.
     *
     * @param x1 the x-coordinate of the first point of the first set
     * @param y1 the y-coordinate of the first point of the first set
     * @param x2 the x-coordinate of the second point of the first set
     * @param y2 the y-coordinate of the second point of the first set
     * @param x3 the x-coordinate of the third point of the first set
     * @param y3 the y-coordinate of the third point of the first set
     * @param x4 the x-coordinate of the first point of the second set
     * @param y4 the y-coordinate of the first point of the second set
     * @param x5 the x-coordinate of the second point of the second set
     * @param y5 the y-coordinate of the second point of the second set
     * @param x6 the x-coordinate of the third point of the second set
     * @param y6 the y-coordinate of the third point of the second set
     * @param x7 the x-coordinate of the fourth point of the second set
     * @param y7 the y-coordinate of the fourth point of the second set
     * @param x8 the x-coordinate of the fifth point of the second set
     * @param y8 the y-coordinate of the fifth point of the second set
     * @return the constructed TMono
     */
    TMono ratio(int x1, int y1, int x2, int y2, int x3, int y3,
                int x4, int y4, int x5, int y5, int x6, int y6,
                int x7, int y7, int x8, int y8) {
        return pdif(ptimes(sqdistance(x1, y1, x2, y2), sqdistance(x7, y7, x8, y8)),
                ptimes(sqdistance(x3, y3, x4, y4), sqdistance(x5, y5, x6, y6)));
    }

    /**
     * Constructs a TMono representing the equality of three angles formed by nine points.
     *
     * @param x1 the x-coordinate of the first point of the first angle
     * @param y1 the y-coordinate of the first point of the first angle
     * @param x2 the x-coordinate of the second point of the first angle
     * @param y2 the y-coordinate of the second point of the first angle
     * @param x3 the x-coordinate of the third point of the first angle
     * @param y3 the y-coordinate of the third point of the first angle
     * @param x4 the x-coordinate of the first point of the second angle
     * @param y4 the y-coordinate of the first point of the second angle
     * @param x5 the x-coordinate of the second point of the second angle
     * @param y5 the y-coordinate of the second point of the second angle
     * @param x6 the x-coordinate of the third point of the second angle
     * @param y6 the y-coordinate of the third point of the second angle
     * @param x7 the x-coordinate of the first point of the third angle
     * @param y7 the y-coordinate of the first point of the third angle
     * @param x8 the x-coordinate of the second point of the third angle
     * @param y8 the y-coordinate of the second point of the third angle
     * @param x9 the x-coordinate of the third point of the third angle
     * @param y9 the y-coordinate of the third point of the third angle
     * @param xm the x-coordinate of the point used for the final calculation
     * @return the TMono representing the equality of the three angles
     */
    TMono eqangle3p(int x1, int y1, int x2, int y2, int x3, int y3,
                    int x4, int y4, int x5, int y5, int x6, int y6,
                    int x7, int y7, int x8, int y8, int x9, int y9, int xm) {
        TMono sx1 = pdif(pRtimes(ppdd(x3, x2), ppdd(y1, y2)), pRtimes(ppdd(x1, x2), ppdd(y3, y2)));
        TMono sy1 = padd(pRtimes(ppdd(x1, x2), ppdd(x3, x2)), pRtimes(ppdd(y1, y2), ppdd(y3, y2)));

        TMono sx2 = pdif(pRtimes(ppdd(x6, x5), ppdd(y4, y5)), pRtimes(ppdd(x4, x5), ppdd(y6, y5)));
        TMono sy2 = padd(pRtimes(ppdd(x4, x5), ppdd(x6, x5)), pRtimes(ppdd(y4, y5), ppdd(y6, y5)));

        TMono sx3 = pdif(pRtimes(ppdd(x9, x8), ppdd(y7, y8)), pRtimes(ppdd(x7, x8), ppdd(y9, y8)));
        TMono sy3 = padd(pRtimes(ppdd(x7, x8), ppdd(x9, x8)), pRtimes(ppdd(y7, y8), ppdd(y9, y8)));

        TMono mx1 = pRtimes(pcopy(sx1), pRtimes(pcopy(sy2), pcopy(sy3)));
        TMono mx2 = pRtimes(pcopy(sx2), pRtimes(pcopy(sy1), pcopy(sy3)));
        TMono mx3 = pRtimes(pcopy(sx3), pRtimes(pcopy(sy1), pcopy(sy2)));

        TMono t1 = padd(mx1, padd(mx2, mx3));
        TMono t2 = pRtimes(pcopy(sx1), pRtimes(pcopy(sx2), pcopy(sx3)));

        TMono t3 = pRtimes(pcopy(sx1), pRtimes(pcopy(sx2), pcopy(sy3)));
        TMono t4 = pRtimes(pcopy(sx1), pRtimes(pcopy(sx3), pcopy(sy2)));
        TMono t5 = pRtimes(pcopy(sx2), pRtimes(pcopy(sx3), pcopy(sy1)));

        TMono t = pRtimes(pcopy(sy1), pRtimes(pcopy(sy2), pcopy(sy3)));
        TMono mm1 = pdif(t1, t2);
        TMono mm2 = pdif(t, padd(t3, padd(t4, t5)));
        return pdif(mm1, pRtimes(mm2, ppth(xm, 1, 1)));
    }

    /**
     * Constructs a TMono representing a specific angle.
     *
     * @param x1     the x-coordinate of the point
     * @param degree the degree of the angle
     * @return the TMono representing the specific angle
     */
    TMono specificangle(int x1, int degree) {
        if (degree == 0) {
            TMono m1 = new TMono(x1, 1, 1);
            return m1;
        } else if (degree == 15) {
            TMono m1 = new TMono(x1, 1, 2);
            TMono m2 = new TMono(x1, -4, 1);
            TMono m3 = new TMono(0, 1, 0);
            return padd(pdif(m1, m2), m3);
        } else if (degree == 30) {
            TMono m1 = new TMono(x1, 3, 2);
            TMono m2 = new TMono(0, 1, 0);
            return pdif(m1, m2);
        } else if (degree == 45) {
            TMono m1 = new TMono(x1, 1, 1);
            TMono m2 = new TMono(0, 1, 0);
            return pdif(m1, m2);
        } else if (degree == 60) {
            TMono m1 = new TMono(x1, 1, 2);
            TMono m2 = new TMono(0, 3, 0);
            return pdif(m1, m2);
        } else if (degree == 120) {
            TMono m1 = new TMono(x1, 1, 2);
            TMono m2 = new TMono(0, 3, 0);
            return pdif(m1, m2);
        }
        return null;
    }

    /**
     * Constructs a TMono representing the equality of two angles formed by six points.
     *
     * @param x1 the x-coordinate of the first point of the first angle
     * @param y1 the y-coordinate of the first point of the first angle
     * @param x2 the x-coordinate of the second point of the first angle
     * @param y2 the y-coordinate of the second point of the first angle
     * @param x3 the x-coordinate of the third point of the first angle
     * @param y3 the y-coordinate of the third point of the first angle
     * @param x4 the x-coordinate of the first point of the second angle
     * @param y4 the y-coordinate of the first point of the second angle
     * @param x5 the x-coordinate of the second point of the second angle
     * @param y5 the y-coordinate of the second point of the second angle
     * @param x6 the x-coordinate of the third point of the second angle
     * @param y6 the y-coordinate of the third point of the second angle
     * @return the TMono representing the equality of the two angles
     */
    TMono eqangle(int x1, int y1, int x2, int y2, int x3, int y3,
                  int x4, int y4, int x5, int y5, int x6, int y6) {
        TMono sx1 = ppdd(x1, x2);
        TMono sx2 = ppdd(x3, x2);
        TMono sx3 = ppdd(x4, x5);
        TMono sx4 = ppdd(x6, x5);

        TMono sy1 = ppdd(y1, y2);
        TMono sy2 = ppdd(y3, y2);
        TMono sy3 = ppdd(y4, y5);
        TMono sy4 = ppdd(y6, y5);

        TMono s1 = this.pRtimes(poly.pcopy(sy2), poly.pcopy(sx1));
        TMono s2 = this.pRtimes(poly.pcopy(sy1), poly.pcopy(sx2));
        TMono t1 = pdif(s1, s2);

        TMono s3 = this.pRtimes(poly.pcopy(sx3), poly.pcopy(sx4));
        TMono s4 = this.pRtimes(poly.pcopy(sy3), poly.pcopy(sy4));
        TMono t2 = padd(s3, s4);

        s1 = this.pRtimes((sy4), (sx3));
        s2 = this.pRtimes((sx4), (sy3));
        TMono t3 = pdif(s1, s2);
        s3 = this.pRtimes((sx1), (sx2));
        s4 = this.pRtimes((sy1), (sy2));
        TMono t4 = padd(s3, s4);

        TMono r1 = pRtimes(t1, t2);
        TMono r2 = pRtimes(t3, t4);
        return this.pdif(r1, r2);
    }

    /**
     * Constructs a TMono representing the equality of two angles formed by six points.
     *
     * @param x1  the x-coordinate of the first point of the first angle
     * @param y1  the y-coordinate of the first point of the first angle
     * @param x2  the x-coordinate of the second point of the first angle
     * @param y2  the y-coordinate of the second point of the first angle
     * @param x21 the x-coordinate of the third point of the first angle
     * @param y21 the y-coordinate of the third point of the first angle
     * @param x3  the x-coordinate of the first point of the second angle
     * @param y3  the y-coordinate of the first point of the second angle
     * @param x4  the x-coordinate of the second point of the second angle
     * @param y4  the y-coordinate of the second point of the second angle
     * @param x5  the x-coordinate of the third point of the second angle
     * @param y5  the y-coordinate of the third point of the second angle
     * @param x51 the x-coordinate of the fourth point of the second angle
     * @param y51 the y-coordinate of the fourth point of the second angle
     * @param x6  the x-coordinate of the fifth point of the second angle
     * @param y6  the y-coordinate of the fifth point of the second angle
     * @return the TMono representing the equality of the two angles
     */
    TMono eqangle(int x1, int y1, int x2, int y2, int x21, int y21, int x3, int y3,
                  int x4, int y4, int x5, int y5, int x51, int y51, int x6, int y6) {
        TMono sx1 = ppdd(x1, x2);
        TMono sx2 = ppdd(x3, x21);
        TMono sx3 = ppdd(x4, x5);
        TMono sx4 = ppdd(x6, x51);

        TMono sy1 = ppdd(y1, y2);
        TMono sy2 = ppdd(y3, y21);
        TMono sy3 = ppdd(y4, y5);
        TMono sy4 = ppdd(y6, y51);

        TMono s1 = this.pRtimes(poly.pcopy(sy2), poly.pcopy(sx1));
        TMono s2 = this.pRtimes(poly.pcopy(sy1), poly.pcopy(sx2));
        TMono t1 = pdif(s1, s2);

        TMono s3 = this.pRtimes(poly.pcopy(sx3), poly.pcopy(sx4));
        TMono s4 = this.pRtimes(poly.pcopy(sy3), poly.pcopy(sy4));
        TMono t2 = padd(s3, s4);

        s1 = this.pRtimes((sy4), (sx3));
        s2 = this.pRtimes((sx4), (sy3));
        TMono t3 = pdif(s1, s2);
        s3 = this.pRtimes((sx1), (sx2));
        s4 = this.pRtimes((sy1), (sy2));
        TMono t4 = padd(s3, s4);

        TMono r1 = pRtimes(t1, t2);
        TMono r2 = pRtimes(t3, t4);
        return this.pdif(r1, r2);
    }

    /**
     * Constructs a TMono representing the cyclic condition of four points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param x4 the x-coordinate of the fourth point
     * @param y4 the y-coordinate of the fourth point
     * @return the TMono representing the cyclic condition
     */
    TMono cyclic(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return eqangle(x1, y1, x3, y3, x2, y2, x1, y1, x4, y4, x2, y2);
    }

    /**
     * Constructs a TMono representing the angle between three points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param d  the degree of the angle
     * @return the TMono representing the angle
     */
    TMono sangle(int x1, int y1, int x2, int y2, int x3, int y3, int d) {
        TMono m = new TMono(d, -1, 1);
        TMono m1 = pdif(ptimes(ppdd(y3, y2), ppdd(x1, x2)), ptimes(ppdd(y1, y2), ppdd(x3, x2)));
        TMono m2 = padd(ptimes(ppdd(y3, y2), ppdd(y1, y2)), ptimes(ppdd(x1, x2), ppdd(x3, x2)));
        return pdif(m1, ptimes(m2, m));
    }

    /**
     * Constructs a TMono representing the midpoint of three points.
     *
     * @param x1 the x-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @return the TMono representing the midpoint
     */
    TMono midpoint(int x1, int x2, int x3) {
        return padd(ppdd(x2, x1), ppdd(x2, x3));
    }


    /**
     * Constructs a TMono representing the bisector of an angle.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @return the TMono representing the bisector
     */
    TMono bisect(int x1, int y1, int x2, int y2, int x3, int y3) {
        TMono m1 = padd(ppdd(y3, y2), ppdd(y3, y1));
        TMono m2 = padd(ppdd(x3, x1), ppdd(x3, x2));

        return padd(pRtimes(m1, ppdd(y2, y1)), pRtimes(m2, ppdd(x2, x1)));
    }

    /**
     * Constructs a TMono representing the bisector of an angle (alternative method).
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @return the TMono representing the bisector
     */
    TMono bisect1(int x1, int y1, int x2, int y2, int x3, int y3) {
        TMono m1 = padd(ppdd(y1, y2), ppdd(y1, y3));
        TMono m2 = padd(ppdd(x1, x2), ppdd(x1, x3));

        return padd(pRtimes(m1, ppdd(y3, y2)), pRtimes(m2, ppdd(x3, x2)));
    }

    /**
     * Constructs a TMono representing the condition that a point lies on a conic section.
     *
     * @param x  the x-coordinate of the point
     * @param y  the y-coordinate of the point
     * @param x1 the x-coordinate of the first point on the conic
     * @param y1 the y-coordinate of the first point on the conic
     * @param x2 the x-coordinate of the second point on the conic
     * @param y2 the y-coordinate of the second point on the conic
     * @param x3 the x-coordinate of the third point on the conic
     * @param y3 the y-coordinate of the third point on the conic
     * @param x4 the x-coordinate of the fourth point on the conic
     * @param y4 the y-coordinate of the fourth point on the conic
     * @return the TMono representing the condition
     */
    TMono ccline(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        TMono s1 = ppdd(x, x1);
        TMono s2 = ppdd(y, y1);
        TMono s3 = ppdd(x1, x2);
        TMono s4 = ppdd(y1, y2);
        TMono sa = padd(pRtimes(s1, pcopy(s1)), pRtimes(s2, pcopy(s2)));
        TMono sb = padd(pRtimes(s3, pcopy(s3)), pRtimes(s4, pcopy(s4)));
        TMono s = pdif(sa, sb);

        s1 = ppdd(x, x3);
        s2 = ppdd(y, y3);
        s3 = ppdd(x3, x4);
        s4 = ppdd(y3, y4);
        TMono sc = padd(pRtimes(s1, pcopy(s1)), pRtimes(s2, pcopy(s2)));
        TMono sd = padd(pRtimes(s3, pcopy(s3)), pRtimes(s4, pcopy(s4)));
        TMono t = pdif(sc, sd);
        return pdif(s, t);
    }

    /**
     * Constructs a TPoly representing a square point with a given ratio.
     *
     * @param x     the x-coordinate of the point
     * @param y     the y-coordinate of the point
     * @param x0    the x-coordinate of the first reference point
     * @param y0    the y-coordinate of the first reference point
     * @param x1    the x-coordinate of the second reference point
     * @param y1    the y-coordinate of the second reference point
     * @param x2    the x-coordinate of the third reference point
     * @param y2    the y-coordinate of the third reference point
     * @param ratio the ratio to be used
     * @return the TPoly representing the square point
     */
    TPoly squarept1(int x, int y, int x0, int y0, int x1, int y1, int x2, int y2, int ratio) {
        TMono m1, m2;
        m1 = m2 = null;
        if (ratio > 0) {
            m1 = padd(pctimes(ppdd(x, x0), ratio), ppdd(y1, y2));
            m2 = padd(pctimes(ppdd(y, y0), ratio), ppdd(x2, x1));
        } else if (ratio < 0) {
            m1 = padd(ppdd(x, x0), pctimes(ppdd(y1, y2), -ratio));
            m2 = padd(ppdd(y, y0), pctimes(ppdd(x2, x1), -ratio));
        }
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing a square point with a given ratio (alternative method).
     *
     * @param x     the x-coordinate of the point
     * @param y     the y-coordinate of the point
     * @param x0    the x-coordinate of the first reference point
     * @param y0    the y-coordinate of the first reference point
     * @param x1    the x-coordinate of the second reference point
     * @param y1    the y-coordinate of the second reference point
     * @param x2    the x-coordinate of the third reference point
     * @param y2    the y-coordinate of the third reference point
     * @param ratio the ratio to be used
     * @return the TPoly representing the square point
     */
    TPoly squarept2(int x, int y, int x0, int y0, int x1, int y1, int x2, int y2, int ratio) {
        TMono m1, m2;
        m1 = m2 = null;

        if (ratio > 0) {
            m1 = padd(pctimes(ppdd(x, x0), ratio), ppdd(y2, y1));
            m2 = padd(pctimes(ppdd(y, y0), ratio), ppdd(x1, x2));
        } else if (ratio < 0) {
            m1 = padd(ppdd(x, x0), pctimes(ppdd(y2, y1), -ratio));
            m2 = padd(ppdd(y, y0), pctimes(ppdd(x1, x2), -ratio));
        }
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing the mirror image of a point with respect to a line.
     *
     * @param x1 the x-coordinate of the first point on the line
     * @param y1 the y-coordinate of the first point on the line
     * @param x2 the x-coordinate of the second point on the line
     * @param y2 the y-coordinate of the second point on the line
     * @param x3 the x-coordinate of the point to be mirrored
     * @param y3 the y-coordinate of the point to be mirrored
     * @param x4 the x-coordinate of the mirrored point
     * @param y4 the y-coordinate of the mirrored point
     * @return the TPoly representing the mirror image
     */
    TPoly mirrorPL(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        TMono m1 = this.perpendicular(x1, y1, x2, y2, x3, y3, x4, y4);
        TMono s1 = pRtimes(padd(ppdd(y2, y3), ppdd(y1, y3)), ppdd(x4, x3));
        TMono s2 = pRtimes(padd(ppdd(x2, x3), ppdd(x1, x3)), ppdd(y4, y3));
        TMono m2 = pdif(s1, s2);
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing a point proportional to two other points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param v1 the first proportional value
     * @param v2 the second proportional value
     * @return the TPoly representing the proportional point
     */
    TPoly prop_point(int x1, int y1, int x2, int y2, int x3, int y3, int v1, int v2) {
        TMono s1 = pdif(pctimes(ppdd(x2, x1), v2), pctimes(ppdd(x1, x3), v1));
        TMono s2 = pdif(pctimes(ppdd(y2, y1), v2), pctimes(ppdd(y1, y3), v1));
        return newTPoly(s1, s2);
    }

    /**
     * Constructs a TPoly representing the ratio of two points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param x4 the x-coordinate of the fourth point
     * @param y4 the y-coordinate of the fourth point
     * @param r1 the first ratio value
     * @param r2 the second ratio value
     * @return the TPoly representing the ratio
     */
    TPoly Pratio(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int r1, int r2) {
        TMono s1 = pdif(pctimes(ppdd(x1, x2), r2), pctimes(ppdd(x4, x3), r1));
        TMono s2 = pdif(pctimes(ppdd(y1, y2), r2), pctimes(ppdd(y4, y3), r1));
        return newTPoly(s1, s2);
    }

    /**
     * Constructs a TPoly representing the ratio of two points (alternative method).
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param x4 the x-coordinate of the fourth point
     * @param y4 the y-coordinate of the fourth point
     * @param r1 the first ratio value
     * @param r2 the second ratio value
     * @return the TPoly representing the ratio
     */
    TPoly Tratio(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int r1, int r2) {
        TMono s1 = pdif(pctimes(ppdd(x1, x2), r2), pctimes(ppdd(y4, y3), r1));
        TMono s2 = padd(pctimes(ppdd(y1, y2), r2), pctimes(ppdd(x4, x3), r1));
        return newTPoly(s1, s2);
    }

    /**
     * Constructs a TPoly representing the barycenter of four points.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param x4 the x-coordinate of the fourth point
     * @param y4 the y-coordinate of the fourth point
     * @return the TPoly representing the barycenter
     */
    TPoly barycenter(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        TMono m1 = padd(padd(ppdd(x1, x2), ppdd(x1, x3)), ppdd(x1, x4));
        TMono m2 = padd(padd(ppdd(y1, y2), ppdd(y1, y3)), ppdd(y1, y4));

        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing the circumcenter of a triangle.
     *
     * @param x1 the x-coordinate of the first point
     * @param y1 the y-coordinate of the first point
     * @param x2 the x-coordinate of the second point
     * @param y2 the y-coordinate of the second point
     * @param x3 the x-coordinate of the third point
     * @param y3 the y-coordinate of the third point
     * @param x4 the x-coordinate of the fourth point
     * @param y4 the y-coordinate of the fourth point
     * @return the TPoly representing the circumcenter
     */
    TPoly circumcenter(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        TMono m1 = padd(pRtimes(padd(ppdd(x1, x2), ppdd(x1, x3)), ppdd(x2, x3)),
                pRtimes(padd(ppdd(y1, y2), ppdd(y1, y3)), ppdd(y2, y3)));
        TMono m2 = padd(pRtimes(padd(ppdd(x1, x3), ppdd(x1, x4)), ppdd(x3, x4)),
                pRtimes(padd(ppdd(y1, y3), ppdd(y1, y4)), ppdd(y3, y4)));
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing the intersection of a line and a circle.
     *
     * @param xo the x-coordinate of the circle's center
     * @param yo the y-coordinate of the circle's center
     * @param xc the x-coordinate of the circle's circumference
     * @param yc the y-coordinate of the circle's circumference
     * @param xl the x-coordinate of the line's first point
     * @param yl the y-coordinate of the line's first point
     * @param x  the x-coordinate of the intersection point
     * @param y  the y-coordinate of the intersection point
     * @return the TPoly representing the intersection
     */
    TPoly LCMeet(int xo, int yo, int xc, int yc, int xl, int yl, int x, int y) {
        TMono m1 = padd(ptimes(padd(ppdd(y, yo), ppdd(yc, yo)), ppdd(yl, yc)),
                ptimes(padd(ppdd(x, xo), ppdd(xc, xo)), ppdd(xl, xc)));
        TMono m2 = pdif(ptimes(ppdd(y, yc), ppdd(xl, xc)),
                ptimes(ppdd(x, xc), ppdd(yl, yc)));
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TPoly representing an equilateral triangle.
     *
     * @param x  the x-coordinate of the first point
     * @param y  the y-coordinate of the first point
     * @param x1 the x-coordinate of the second point
     * @param y1 the y-coordinate of the second point
     * @param x2 the x-coordinate of the third point
     * @param y2 the y-coordinate of the third point
     * @param p  a boolean flag
     * @return the TPoly representing the equilateral triangle
     */
    TPoly pn_eq_triangle(int x, int y, int x1, int y1, int x2, int y2, boolean p) {
        TMono m1 = eqdistance(x, y, x1, y1, x1, y1, x2, y2);
        TMono m2 = eqdistance(x, y, x2, y2, x1, y1, x2, y2);
        return newTPoly(m1, m2);
    }

    /**
     * Constructs a TMono representing the sum of three squared distances.
     *
     * @param a the first point
     * @param b the second point
     * @param c the third point
     * @param d the fourth point
     * @param e the fifth point
     * @param f the sixth point
     * @return the TMono representing the sum of three squared distances
     */
    TMono sum3(CPoint a, CPoint b, CPoint c, CPoint d, CPoint e, CPoint f) {
        TMono x, y, z, x1, y1, z1, s, r;

        /* (x-y)^2 + z(z - 2(x + y)) */

        x = sqdistance(a.x1.xindex, a.y1.xindex, b.x1.xindex, b.y1.xindex);
        y = sqdistance(c.x1.xindex, c.y1.xindex, d.x1.xindex, d.y1.xindex);
        z = sqdistance(e.x1.xindex, e.y1.xindex, f.x1.xindex, f.y1.xindex);
        x1 = pcopy(x);
        y1 = pcopy(y);
        z1 = pcopy(z);
        s = pdif(x, y);
        s = pRtimes(s, pcopy(s));
        /* s = (x-y)^2 */

        x1 = pctimes(x1, -2);
        y1 = pctimes(y1, -2);
        r = padd(padd(x1, y1), z1);
        /* r = z - 2(x + y) */
        r = pRtimes(z, r);
        return padd(s, r);
    }

    /**
     * Constructs a TMono representing the product of two squared distances.
     *
     * @param a  the first point
     * @param b  the second point
     * @param c  the third point
     * @param d  the fourth point
     * @param t1 the first multiplier
     * @param t2 the second multiplier
     * @return the TMono representing the product of two squared distances
     */
    TMono p_p_mulside(CPoint a, CPoint b, CPoint c, CPoint d, int t1, int t2) {
        TMono m1 = sqdistance(a.x1.xindex, a.y1.xindex, b.x1.xindex, b.y1.xindex);
        TMono m2 = sqdistance(c.x1.xindex, c.y1.xindex, d.x1.xindex, d.y1.xindex);
        return pdif(pctimes(m1, t2 * t2), pctimes(m2, t1 * t1));
    }

    /**
     * Constructs a TMono representing the horizontal distance between two points.
     *
     * @param a the first point
     * @param b the second point
     * @return the TMono representing the horizontal distance
     */
    TMono p_p_horizonal(CPoint a, CPoint b) {
        return ppdd(a.y1.xindex, b.y1.xindex);
    }

    /**
     * Constructs a TMono representing the vertical distance between two points.
     *
     * @param a the first point
     * @param b the second point
     * @return the TMono representing the vertical distance
     */
    TMono p_p_vertical(CPoint a, CPoint b) {
        return ppdd(a.x1.xindex, b.x1.xindex);
    }

    /**
     * Constructs a TMono representing the tangent of a line and a circle.
     *
     * @param a the first point on the line
     * @param b the second point on the line
     * @param c the first point on the circle
     * @param d the second point on the circle
     * @param o the center of the circle
     * @return the TMono representing the tangent
     */
    TMono l_c_tangent(CPoint a, CPoint b, CPoint c, CPoint d, CPoint o) {
        TMono p1, p2, p3;
        p1 = this.collinear(a.x1.xindex, a.y1.xindex, b.x1.xindex, b.y1.xindex, o.x1.xindex, o.y1.xindex);
        p2 = this.sqdistance(c.x1.xindex, c.y1.xindex, d.x1.xindex, d.y1.xindex);
        p3 = this.sqdistance(a.x1.xindex, a.y1.xindex, b.x1.xindex, b.y1.xindex);
        return pdif(pRtimes(p1, pcopy(p1)), pRtimes(p2, p3));
    }

    /**
     * Constructs a TMono representing the tangent of two circles.
     *
     * @param a  the first point on the first circle
     * @param b  the second point on the first circle
     * @param o  the center of the first circle
     * @param c  the first point on the second circle
     * @param d  the second point on the second circle
     * @param o1 the center of the second circle
     * @return the TMono representing the tangent
     */
    TMono c_c_tangent(CPoint a, CPoint b, CPoint o, CPoint c, CPoint d, CPoint o1) {
        return sum3(a, b, c, d, o, o1);
    }

    /**
     * Constructs a new TPoly from two TMonos.
     *
     * @param m1 the first TMono
     * @param m2 the second TMono
     * @return the new TPoly
     */
    TPoly newTPoly(TMono m1, TMono m2) {
        TPoly poly = new TPoly();
        poly.setPoly(m1);
        TPoly poly1 = new TPoly();
        poly1.setPoly(m2);
        poly1.setNext(poly);
        poly.setNext(null);
        return poly1;
    }

    /**
     * Constructs a TMono representing a non-degenerate geometric condition.
     *
     * @param m the TMono to be checked
     * @param z the index of the condition
     * @return the TMono representing the non-degenerate condition
     */
    TMono n_ndg(TMono m, int z) {
        if (m == null) return null;
        return pdif(ptimes(pth(z, 1, 1), m), pth(0, 1, 0));
    }

    /**
     * Constructs a TMono representing the parallel condition of two lines.
     *
     * @param p1 the first point on the first line
     * @param p2 the second point on the first line
     * @param p3 the first point on the second line
     * @param p4 the second point on the second line
     * @return the TMono representing the parallel condition
     */
    TMono parallel(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        return parallel(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex,
                p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    /**
     * Constructs a TMono representing the perpendicular condition of two lines.
     *
     * @param p1 the first point on the first line
     * @param p2 the second point on the first line
     * @param p3 the first point on the second line
     * @param p4 the second point on the second line
     * @return the TMono representing the perpendicular condition
     */
    TMono perpendicular(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        return perpendicular(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex,
                p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    /**
     * Constructs a TMono representing the equal distance condition between two pairs of points.
     *
     * @param p1 the first point of the first pair
     * @param p2 the second point of the first pair
     * @param p3 the first point of the second pair
     * @param p4 the second point of the second pair
     * @return the TMono representing the equal distance condition
     */
    TMono eqdistance(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        return eqdistance(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex,
                p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    /**
     * Constructs a TMono representing the collinear condition of three points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @return the TMono representing the collinear condition
     */
    TMono collinear(CPoint p1, CPoint p2, CPoint p3) {
        return collinear(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex, p2.y1.xindex, p3.x1.xindex, p3.y1.xindex);
    }

    /**
     * Constructs a TMono representing the cyclic condition of four points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return the TMono representing the cyclic condition
     */
    TMono cyclic(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        return cyclic(p1.x1.xindex, p1.y1.xindex, p2.x1.xindex,
                p2.y1.xindex, p3.x1.xindex, p3.y1.xindex, p4.x1.xindex, p4.y1.xindex);
    }

    /**
     * Constructs a TMono representing the isotropic condition of two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the TMono representing the isotropic condition
     */
    TMono isotropic(CPoint p1, CPoint p2) {
        TMono m1 = ptimes(ppdd(p1.x1.xindex, p2.x1.xindex), ppdd(p1.x1.xindex, p2.x1.xindex));
        TMono m2 = ptimes(ppdd(p1.y1.xindex, p2.y1.xindex), ppdd(p1.y1.xindex, p2.y1.xindex));
        return this.padd(m1, m2);
    }

    /**
     * Constructs a TMono representing the triple product invariant condition of four points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @param p3 the third point
     * @param p4 the fourth point
     * @return the TMono representing the triple product invariant condition
     */
    TMono triplePI(CPoint p1, CPoint p2, CPoint p3, CPoint p4) {
        TMono m1 = ptimes(ppdd(p1.y1.xindex, p2.y1.xindex), ppdd(p3.y1.xindex, p4.y1.xindex));
        TMono m2 = ptimes(ppdd(p1.x1.xindex, p2.x1.xindex), ppdd(p3.x1.xindex, p4.x1.xindex));
        TMono m = ptimes(m1, m2);

        m1 = ptimes(ppdd(p1.y1.xindex, p2.y1.xindex), ppdd(p1.y1.xindex, p2.y1.xindex));
        m2 = ptimes(ppdd(p3.x1.xindex, p4.x1.xindex), ppdd(p3.x1.xindex, p4.x1.xindex));
        TMono n = pctimes(ptimes(m1, m2), 3);

        m1 = ptimes(ppdd(p1.y1.xindex, p2.y1.xindex), ppdd(p1.y1.xindex, p2.y1.xindex));
        m2 = ptimes(ppdd(p1.y1.xindex, p2.y1.xindex), ppdd(p1.y1.xindex, p2.y1.xindex));
        TMono x = ptimes(m1, m2);

        return ptimes(m, this.padd(n, x));
    }

    /**
     * Constructs a TMono representing a non-degenerate geometric condition.
     *
     * @param c  the CNdg object representing the condition
     * @param dp the DrawProcess object
     * @return the TMono representing the non-degenerate condition
     */
    public TMono mm_poly(CNdg c, DrawProcess dp) {
        if (c == null) return null;
        switch (c.type) {
            case Gib.NDG_NEQ:
                return isotropic(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]));
            case Gib.NDG_COLL:
                return this.collinear(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]));
            case Gib.NDG_CONG:
                return this.eqdistance(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]), dp.fd_point(c.p[3]));
            case Gib.NDG_CYCLIC:
                return this.cyclic(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]), dp.fd_point(c.p[3]));
            case Gib.NDG_NON_ISOTROPIC:
                return this.isotropic(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]));
            case Gib.NDG_PARA:
                return this.parallel(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]), dp.fd_point(c.p[3]));
            case Gib.NDG_PERP:
                return this.perpendicular(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]), dp.fd_point(c.p[3]));
            case Gib.NDG_TRIPLEPI:
                return this.triplePI(dp.fd_point(c.p[0]), dp.fd_point(c.p[1]), dp.fd_point(c.p[2]), dp.fd_point(c.p[3]));
            default:
                return null;
        }
    }
}
