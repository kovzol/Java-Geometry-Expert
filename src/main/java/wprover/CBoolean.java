package wprover;

/**
 * Represents a boolean value that can be modified.
 * This class is used to encapsulate a boolean value and provide methods to get and set its value.
 */
public class CBoolean
{
    public boolean bl = false;

    /**
     * Constructs a CBoolean object with the given boolean value.
     *
     * @param b the boolean value
     */
    public CBoolean(boolean b)
    {
        bl = b;
    }

    /**
     * Gets the boolean value.
     *
     * @return the boolean value
     */
    public boolean getValue()
    {
        return bl;
    }

    /**
     * Sets the boolean value.
     *
     * @param v the boolean value to set
     */
    public void setValue(boolean v)
    {
        bl = v;
    }
}


