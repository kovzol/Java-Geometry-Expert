package gprover;

/**
 * Represents a geometric construction node.
 */
public class Mnde {
    int t, type;
    public AngTr tr;
    Mnde nx;

    /**
     * Constructs a Mnde object with default values.
     */
    public Mnde()
    {
        t = 1;
        type = 0;
        tr = null;
        nx = null;

    }

    /**
     * Copies the values from another Mnde object to this object.
     *
     * @param m the Mnde object to copy from
     */
    public void cp(Mnde m)
    {
        t = m.t;
        type = m.type;
        tr = m.tr;
    }
}
