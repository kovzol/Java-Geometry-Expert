package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI;

/**
 * Abstract base class for construction tools.
 * Provides common functionality for all tools.
 */
public abstract class AbstractTool implements Tool {
    protected GExpertCore core;
    protected GExpertUI ui;
    protected String name;
    protected String description;
    protected String iconPath;
    protected int cursorType = 1; // Default to crosshair
    protected GeometryObject previewObject = null; // Object being previewed during drawing

    /**
     * Constructor for AbstractTool.
     * @param core The GExpertCore instance
     * @param name The tool name
     * @param description The tool description
     */
    public AbstractTool(GExpertCore core, String name, String description) {
        this.core = core;
        this.ui = core.getUI();
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor for AbstractTool.
     * @param core The GExpertCore instance
     * @param name The tool name
     * @param description The tool description
     * @param iconPath The icon path
     */
    public AbstractTool(GExpertCore core, String name, String description, String iconPath) {
        this(core, name, description);
        this.iconPath = iconPath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void activate() {
        // Set cursor type
        if (ui != null) {
            ui.setDrawingCursor(getCursorType());
            ui.updateStatus(getDescription());
        }
    }

    @Override
    public void deactivate() {
        // Reset cursor
        if (ui != null) {
            ui.setDrawingCursor(0); // Default cursor
            ui.updateStatus("");
        }

        // Remove any preview object
        removePreviewObject();
    }

    @Override
    public int getCursorType() {
        return cursorType;
    }

    /**
     * Sets the cursor type for this tool.
     * @param cursorType The cursor type (0 = default, 1 = crosshair, 2 = hand, 3 = move)
     */
    public void setCursorType(int cursorType) {
        this.cursorType = cursorType;
    }

    // Default empty implementations of MouseListener methods
    @Override
    public void onMouseDown(double x, double y) {
        // Default implementation does nothing
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Default implementation does nothing
    }

    @Override
    public void onMouseUp(double x, double y) {
        // Default implementation does nothing
    }

    @Override
    public void onMouseWheel(double x, double y, double deltaZ) {
        // Default implementation does nothing
    }

    @Override
    public void onMouseEnter(double x, double y) {
        // Default implementation does nothing
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Default implementation does nothing
    }

    /**
     * Utility method to snap a coordinate to the grid if grid snapping is enabled.
     * @param value The coordinate value
     * @return The snapped coordinate value
     */
    protected double snapToGrid(double value) {
        // This would need to be implemented based on the grid settings
        // For now, we'll just return the original value
        return value;
    }

    /**
     * Creates a preview object for drawing operations.
     * This method should be overridden by tools that need to create preview objects.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The created preview object, or null if no preview is needed
     */
    protected GeometryObject createPreviewObject(double x, double y) {
        // Default implementation returns null
        // Subclasses should override this method to create appropriate preview objects
        return null;
    }

    /**
     * Updates the preview object during mouse movement.
     * This method should be overridden by tools that need to update preview objects.
     * @param x The current x-coordinate
     * @param y The current y-coordinate
     * @return true if the preview was updated, false otherwise
     */
    protected boolean updatePreviewObject(double x, double y) {
        // Default implementation returns false
        // Subclasses should override this method to update their preview objects
        return false;
    }

    /**
     * Finalizes the preview object when the drawing is complete.
     * This method should be overridden by tools that need to finalize preview objects.
     * @param x The final x-coordinate
     * @param y The final y-coordinate
     * @return The finalized object, or null if finalization failed
     */
    protected GeometryObject finalizePreviewObject(double x, double y) {
        // Default implementation returns null
        // Subclasses should override this method to finalize their preview objects
        return null;
    }

    /**
     * Removes the preview object if it exists.
     * This is typically called when the drawing operation is canceled.
     */
    protected void removePreviewObject() {
        if (previewObject != null) {
            // Store a reference to the object we're removing for verification
            GeometryObject objectToRemove = previewObject;

            // Remove the object
            core.removeGeometryObject(objectToRemove);

            // Verify that the object was actually removed
            boolean stillExists = false;
            for (GeometryObject obj : core.getGeometryObjects()) {
                if (obj == objectToRemove) {
                    stillExists = true;
                    break;
                }
            }

            if (stillExists) {
                System.out.println("Warning: Preview object was not removed. Trying again...");
                // Try to remove it again
                core.removeGeometryObject(objectToRemove);
            }

            // Set to null regardless of whether it was removed
            previewObject = null;

            // Refresh the display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }
    }

    /**
     * Finds the next available label for a point.
     * @return The next available label as a character
     */
    protected char findNextPointLabel() {
        char nextLabel = 'A';
        for (GeometryObject obj : core.getGeometryObjects()) {
            if (obj instanceof GExpertCore.Point && obj.getName() != null && obj.getName().length() == 1) {
                char label = obj.getName().charAt(0);
                if (label >= 'A' && label <= 'Z' && label >= nextLabel) {
                    nextLabel = (char) (label + 1);
                    if (nextLabel > 'Z') nextLabel = 'A';
                }
            }
        }
        return nextLabel;
    }

    /**
     * Creates a new point at the specified coordinates.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The created point
     */
    protected GExpertCore.Point createPoint(double x, double y) {
        GExpertCore.Point point = new GExpertCore.Point(x, y);
        point.setColor("#FF0000"); // Red for points

        // Set point name
        char nextLabel = findNextPointLabel();
        point.setName(String.valueOf(nextLabel));

        // Add the point to the core
        core.addGeometryObject(point);

        return point;
    }
}
