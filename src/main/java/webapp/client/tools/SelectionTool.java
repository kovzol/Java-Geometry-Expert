package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.GeometryObject;

/**
 * Tool for selecting and manipulating geometry objects.
 */
public class SelectionTool extends AbstractTool {
    private boolean dragging = false;
    private double lastX, lastY;
    private GeometryObject selectedObject = null;
    private double startX, startY; // Starting position for drag operation
    private double totalDx = 0, totalDy = 0; // Total movement during drag

    /**
     * Constructor for SelectionTool.
     * @param core The GExpertCore instance
     */
    public SelectionTool(GExpertCore core) {
        super(core, "Select", "Select and manipulate objects", "images/select.gif");
        setCursorType(0); // Default cursor
    }

    @Override
    public void activate() {
        super.activate();
        dragging = false;
        selectedObject = core.getSelectedObject();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        dragging = false;
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Find object at click position
        GeometryObject obj = core.findObjectAt(x, y);

        // Select the object
        core.selectObject(obj);
        selectedObject = obj;

        // Store position for dragging
        lastX = x;
        lastY = y;
        startX = x;
        startY = y;
        totalDx = 0;
        totalDy = 0;
        dragging = (selectedObject != null);

        // Update status
        if (ui != null) {
            if (selectedObject != null) {
                String objType = selectedObject.getClass().getSimpleName();
                ui.updateStatus("Selected " + objType + 
                    (selectedObject.getName() != null ? " '" + selectedObject.getName() + "'" : ""));
            } else {
                ui.updateStatus("No object selected");
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        if (dragging && selectedObject != null) {
            // Calculate movement for this step
            double dx = x - lastX;
            double dy = y - lastY;

            // Update total movement
            totalDx += dx;
            totalDy += dy;

            // Move the selected object (temporary move during dragging)
            core.moveSelectedObject(dx, dy, true);

            // Update last position
            lastX = x;
            lastY = y;
        } else {
            // Highlight object under cursor
            GeometryObject obj = core.findObjectAt(x, y);
            core.highlightObject(obj);

            // Update cursor based on whether there's an object under it
            if (ui != null) {
                if (obj != null) {
                    ui.setDrawingCursor(2); // Hand cursor

                    // Show tooltip with object info
                    String objType = obj.getClass().getSimpleName();
                    ui.showTooltip(objType + 
                        (obj.getName() != null ? " '" + obj.getName() + "'" : ""));
                } else {
                    ui.setDrawingCursor(0); // Default cursor
                    ui.hideTooltip();
                }
            }
        }
    }

    @Override
    public void onMouseUp(double x, double y) {
        if (dragging && selectedObject != null) {
            // Reset the object to its original position
            selectedObject.move(-totalDx, -totalDy);

            // Now make a single final move with the total movement
            // This ensures only one copy of the object is saved
            core.moveSelectedObject(totalDx, totalDy, false);

            // Log the final movement for debugging
            if (ui != null) {
                ui.updateStatus("Moved " + selectedObject.getClass().getSimpleName() + 
                    (selectedObject.getName() != null ? " '" + selectedObject.getName() + "'" : "") +
                    " by (" + Math.round(totalDx) + ", " + Math.round(totalDy) + ")");
            }
        }
        dragging = false;
    }

    @Override
    public void onMouseExit(double x, double y) {
        if (dragging && selectedObject != null) {
            // If we were dragging and exit the canvas, finalize the movement
            // Reset the object to its original position
            selectedObject.move(-totalDx, -totalDy);

            // Now make a single final move with the total movement
            core.moveSelectedObject(totalDx, totalDy, false);
        }

        dragging = false;

        // Clear highlight
        core.highlightObject(null);

        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }
}
