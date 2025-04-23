package webapp.client.tools;

import core.ui.GExpertUI.DrawingCanvas.MouseListener;
import webapp.client.GExpertCore;

/**
 * Interface for construction tools.
 * Tools are used to create and manipulate geometry objects.
 */
public interface Tool extends MouseListener {
    /**
     * Gets the name of the tool.
     * @return The tool name
     */
    String getName();
    
    /**
     * Gets the description of the tool.
     * @return The tool description
     */
    String getDescription();
    
    /**
     * Gets the icon path for the tool.
     * @return The icon path
     */
    String getIconPath();
    
    /**
     * Called when the tool is activated.
     */
    void activate();
    
    /**
     * Called when the tool is deactivated.
     */
    void deactivate();
    
    /**
     * Gets the cursor type for this tool.
     * @return The cursor type (0 = default, 1 = crosshair, 2 = hand, 3 = move)
     */
    int getCursorType();
}