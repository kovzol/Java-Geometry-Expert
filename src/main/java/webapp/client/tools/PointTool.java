package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;

/**
 * Tool for creating points.
 */
public class PointTool extends AbstractTool {
    /**
     * Constructor for PointTool.
     * @param core The GExpertCore instance
     */
    public PointTool(GExpertCore core) {
        super(core, "Point", "Create a point");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Check if there's already a point at this location
        GeometryObject existingObject = core.findObjectAt(x, y);
        if (existingObject instanceof Point) {
            // Select the existing point
            core.selectObject(existingObject);

            // Update status
            if (ui != null) {
                ui.updateStatus("Selected existing point " + existingObject.getName() + " at (" + 
                    Math.round(x) + ", " + Math.round(y) + ")");
            }
            return;
        }

        // Create a new point at the clicked location
        Point point = createPoint(x, y);

        // Select the new point
        core.selectObject(point);

        // Update status
        if (ui != null) {
            ui.updateStatus("Point " + point.getName() + " created at (" + Math.round(x) + ", " + Math.round(y) + ")");
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Show preview or tooltip
        if (ui != null) {
            ui.showTooltip("Click to create a point at (" + Math.round(x) + ", " + Math.round(y) + ")");
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }
}
