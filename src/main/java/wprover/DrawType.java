package wprover;


import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * The DrawType class represents a drawing type with properties such as color index, dash style, and width.
 * It provides methods to save and load its state, set properties, and retrieve color information.
 */
public class DrawType {
    int color_index;
    int dash;
    int width;


/**
     * Default constructor for the DrawType class.
     */
    public DrawType()
    {
    }

    /**
     * Saves the current state of the DrawType object to a DataOutputStream.
     *
     * @param out the DataOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException
    {
        out.writeInt(color_index);
        out.writeInt(dash);
        out.writeInt(width);
    }

    /**
     * Loads the state of the DrawType object from a DataInputStream.
     *
     * @param in the DataInputStream to read from
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in) throws IOException
    {
        color_index = in.readInt();
        dash = in.readInt();
        width = in.readInt();
    }

    /**
     * Sets the dash style for the DrawType object.
     *
     * @param dash the dash style to set
     */
    public void setDash(int dash)
    {
        this.dash = dash;
    }

    /**
     * Sets the width for the DrawType object.
     *
     * @param width the width to set
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * Retrieves the color index of the DrawType object.
     *
     * @return the color index
     */
    public int getColorIndex()
    {
        return color_index;
    }

    /**
     * Retrieves the color corresponding to the color index of the DrawType object.
     *
     * @return the color
     */
    public Color getColor()
    {
        return DrawData.getColor(color_index);
    }
}
