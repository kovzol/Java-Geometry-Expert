package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Angle;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;

/**
 * Tool for creating angles.
 */
public class AngleTool extends AbstractTool {
    private Point centerPoint = null;
    private Point startPoint = null;
    private Point endPoint = null;
    private int state = 0; // 0: no points selected, 1: center selected, 2: start selected
    private Angle previewAngle = null;

    /**
     * Constructor for AngleTool.
     * @param core The GExpertCore instance
     */
    public AngleTool(GExpertCore core) {
        super(core, "Angle", "Create an angle");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        resetState();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        resetState();
    }

    /**
     * Resets the state of the tool.
     */
    private void resetState() {
        state = 0;
        centerPoint = null;
        startPoint = null;
        endPoint = null;

        // Remove preview angle if it exists
        if (previewAngle != null) {
            core.removeGeometryObject(previewAngle);
            previewAngle = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (state == 0) {
            // First click - select or create center point
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point as center point
                centerPoint = (Point) existingObject;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + centerPoint.getName() + 
                        " as center. Click to set start point.");
                }
            } else {
                // Create a new point at the clicked location
                centerPoint = createPoint(x, y);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + centerPoint.getName() + 
                        " as center. Click to set start point.");
                }
            }

            state = 1;
        } else if (state == 1) {
            // Second click - select or create start point
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point as start point
                startPoint = (Point) existingObject;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + startPoint.getName() + 
                        " as start point. Click to set end point.");
                }
            } else {
                // Create a new point at the clicked location
                startPoint = createPoint(x, y);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + startPoint.getName() + 
                        " as start point. Click to set end point.");
                }
            }

            state = 2;
        } else if (state == 2) {
            // Third click - select or create end point and create angle
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point as end point
                endPoint = (Point) existingObject;
            } else {
                // Create a new point at the clicked location
                endPoint = createPoint(x, y);
            }

            // Create the angle
            createAngle();

            // Reset for next angle
            resetState();
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (state == 1) {
            // Center point selected, show tooltip for start point
            if (ui != null) {
                ui.showTooltip("Click to set start point");
            }
        } else if (state == 2) {
            // Center and start points selected, update preview angle
            core.removeGeometryObject(previewAngle);//FIXME to show previewline


            // Calculate angles
            double startAngle = Math.atan2(startPoint.getY() - centerPoint.getY(), 
                                          startPoint.getX() - centerPoint.getX());
            double endAngle = Math.atan2(y - centerPoint.getY(), 
                                        x - centerPoint.getX());
            double arcAngle = endAngle - startAngle;

            // Normalize arcAngle to be between -π and π
            while (arcAngle > Math.PI) arcAngle -= 2 * Math.PI;
            while (arcAngle < -Math.PI) arcAngle += 2 * Math.PI;

            // Calculate radius based on distance to start point
            double dx = startPoint.getX() - centerPoint.getX();
            double dy = startPoint.getY() - centerPoint.getY();
            double radius = Math.sqrt(dx * dx + dy * dy);

            // Create a new preview angle
            previewAngle = new Angle(centerPoint.getX(), centerPoint.getY(), startAngle, arcAngle, radius);
            previewAngle.setColor("#AAAAAA"); // Gray for preview
            previewAngle.setLineWidth(1.0); // Thinner line for preview
            previewAngle.setLineStyle(1); // Dashed line for preview
            core.addGeometryObject(previewAngle);

            // Update status
            if (ui != null) {
                ui.updateStatus("Click to set end point (current angle: " + 
                    Math.round(Math.toDegrees(arcAngle)) + "°)");
                ui.refreshDisplay();
            }
        } else {
            // No points selected, show tooltip for center point
            if (ui != null) {
                ui.showTooltip("Click to set center point");
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Remove preview angle if it exists
        if (previewAngle != null) {
            core.removeGeometryObject(previewAngle);
            previewAngle = null;

            // Refresh display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }

        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }


    /**
     * Creates the final angle based on the selected points.
     */
    private void createAngle() {
        // Calculate angles
        double startAngle = Math.atan2(startPoint.getY() - centerPoint.getY(), 
                                      startPoint.getX() - centerPoint.getX());
        double endAngle = Math.atan2(endPoint.getY() - centerPoint.getY(), 
                                    endPoint.getX() - centerPoint.getX());
        double arcAngle = endAngle - startAngle;

        // Normalize arcAngle to be between -π and π
        while (arcAngle > Math.PI) arcAngle -= 2 * Math.PI;
        while (arcAngle < -Math.PI) arcAngle += 2 * Math.PI;

        // Calculate radius based on distance to start point
        double dx = startPoint.getX() - centerPoint.getX();
        double dy = startPoint.getY() - centerPoint.getY();
        double radius = Math.sqrt(dx * dx + dy * dy);

        // Create the angle
        Angle angle = new Angle(centerPoint.getX(), centerPoint.getY(), startAngle, arcAngle, radius);
        angle.setColor("#FF0000"); // Red for angles
        angle.setLineWidth(2.0); // Default line width
        angle.setLineStyle(0); // Solid line

        // Set angle name (e.g., "∠ABC")
        angle.setName("∠" + startPoint.getName() + centerPoint.getName() + endPoint.getName());

        // Add the angle to the core
        core.addGeometryObject(angle);

        // Select the new angle
        core.selectObject(angle);

        // Update status
        if (ui != null) {
            ui.updateStatus("Angle created: " + angle.getName() + " = " + 
                Math.round(Math.toDegrees(arcAngle)) + "°");
        }
    }
}
