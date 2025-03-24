package gprover;

/**
 * The DTerm class represents a term in a polynomial with a specific degree.
 * It includes properties for the degree, the term itself, the next term in the list,
 * and a string representation of the term.
 */
public class DTerm {
    /** The degree of the term. */
    public int deg;

    /** The term itself. */
    public XTerm p;

    /** The next term in the list. */
    public DTerm nx;

    /** The string representation of the term. */
    public String text;

    /**
     * Constructs a DTerm object with default values.
     */
    public DTerm() {
        deg = 0;
        p = null;
        nx = null;
        text = null;
    }

    /**
     * Returns the string representation of the term.
     *
     * @return the string representation of the term
     */
    public String toString() {
        return text;
    }
}
