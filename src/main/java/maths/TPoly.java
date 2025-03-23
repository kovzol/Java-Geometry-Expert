package maths;

/**
 * Represents a polynomial linked list.
 *
 * <p>This class is used to store and manipulate polynomials in a linked list structure.
 * Each node in the list contains a polynomial (TMono) and a reference to the next node.</p>
 */
public class TPoly {
    public TMono poly;
    public TPoly next;

    /**
     * Default constructor.
     */
    public TPoly() {
    }

    /**
     * Returns the next node in the list.
     *
     * @return the next node
     */
    public TPoly getNext() {
        return next;
    }

    /**
     * Returns the polynomial of the current node.
     *
     * @return the polynomial
     */
    public TMono getPoly() {
        return poly;
    }

    /**
     * Sets the next node in the list.
     *
     * @param next the next node
     */
    public void setNext(TPoly next) {
        this.next = next;
    }

    /**
     * Sets the polynomial of the current node.
     *
     * @param poly the polynomial
     */
    public void setPoly(TMono poly) {
        this.poly = poly;
    }

    /**
     * Returns the length of the polynomial linked list.
     *
     * @return the length of the list
     */
    public int length() {
        TPoly tp = this;
        int i = 0;

        while (tp != null) {
            tp = tp.next;
            i++;
        }
        return i;
    }
}
