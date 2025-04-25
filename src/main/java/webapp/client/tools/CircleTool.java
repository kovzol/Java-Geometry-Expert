package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Circle;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;

/**
 * Tool for creating circles.
 */
public class CircleTool extends AbstractTool {
    private double centerX, centerY;
    private boolean centerPointSelected = false;
    private Point centerPoint = null;
    private Circle previewCircle = null;

    /**
     * Constructor for CircleTool.
     * @param core The GExpertCore instance
     */
    public CircleTool(GExpertCore core) {
        super(core, "Circle", "Create a circle", "images/circle.gif");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        centerPointSelected = false;
        centerPoint = null;
        previewCircle = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        centerPointSelected = false;
        centerPoint = null;

        // Remove preview circle if it exists
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!centerPointSelected) {
            // First click - select or create center point

            // Check if there's already a point at this location
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point as center point
                centerPoint = (Point) existingObject;
                centerX = centerPoint.getX();
                centerY = centerPoint.getY();

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + centerPoint.getName() + 
                        " as center. Click to set radius.");
                }
            } else {
                // Create a new point at the clicked location
                centerPoint = createPoint(x, y);

                centerX = x;
                centerY = y;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + centerPoint.getName() + 
                        " as center. Click to set radius.");
                }
            }

            centerPointSelected = true;

        } else {
            // Second click - create circle with radius determined by distance

            // Calculate radius
            double dx = x - centerX;
            double dy = y - centerY;
            double radius = Math.sqrt(dx * dx + dy * dy);

            // Check if radius is large enough
            if (radius > 1) {
                // Instead of removing and creating a new circle, update the preview circle
                if (previewCircle != null) {
                    // Update the preview circle's appearance to match the final circle
                    previewCircle.setColor("#FF0000"); // Red for circles
                    previewCircle.setLineWidth(2.0); // Default line width
                    previewCircle.setLineStyle(0); // Solid line

                    // Select the circle
                    core.selectObject(previewCircle);

                    // Keep the reference to the circle but set previewCircle to null
                    // so it's not removed when the tool is deactivated
                    previewCircle = null;
                } else {
                    // Create the actual circle if no preview exists
                    Circle circle = new Circle(centerX, centerY, radius);
                    circle.setColor("#FF0000"); // Red for circles
                    core.addGeometryObject(circle);

                    // Select the new circle
                    core.selectObject(circle);
                }

                // Update status
                if (ui != null) {
                    ui.updateStatus("Circle created with center " + centerPoint.getName() + 
                        " and radius " + Math.round(radius));
                }
            } else {
                // Radius is too small, don't create a circle
                if (ui != null) {
                    ui.updateStatus("Radius is too small. Circle not created.");
                }
            }

            // Reset for next circle
            centerPointSelected = false;
            centerPoint = null;
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (centerPointSelected) {
            // Update the preview circle
            core.removeGeometryObject(previewCircle);//FIXME to show previewline


            // Calculate radius
            double dx = x - centerX;
            double dy = y - centerY;
            double radius = Math.sqrt(dx * dx + dy * dy);

            // Create a new preview circle
            previewCircle = new Circle(centerX, centerY, radius);
            previewCircle.setColor("#AAAAAA"); // Gray for preview
            previewCircle.setLineWidth(1.0); // Thinner line for preview
            previewCircle.setLineStyle(1); // Dashed line for preview
            core.addGeometryObject(previewCircle);

            // Update status
            if (ui != null) {
                ui.updateStatus("Click to set radius (current: " + Math.round(radius) + ")");
                ui.refreshDisplay();
            }
        } else {
            // Show tooltip for first click
            if (ui != null) {
                ui.showTooltip("Click to set center point");
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Cancel operation if center point is selected
        if (centerPointSelected) {
            // Don't reset centerPointSelected here, just remove preview circle if it exists
            if (previewCircle != null) {
                core.removeGeometryObject(previewCircle);
                previewCircle = null;

                // Refresh display
                if (ui != null) {
                    ui.refreshDisplay();
                }
            }
        }

        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }
}
