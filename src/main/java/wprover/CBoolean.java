package wprover;

/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2005-9-20
 * Time: 11:16:25
 * To change this template use File | Settings | File Templates.
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
