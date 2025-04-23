package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Circle;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for creating a circle with a specified radius.
 * This tool allows users to select two points to define the radius, and then a third point as the center.
 */
public class CompassTool extends AbstractTool {
    private List<Point> selectedPoints = new ArrayList<>();
    private double radius = 0;
    private boolean radiusSet = false;
    private Circle previewCircle = null;

    /**
     * Constructor for CompassTool.
     * @param core The GExpertCore instance
     */
    public CompassTool(GExpertCore core) {
        super(core, "Compass", "Create a circle with a specified radius");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        selectedPoints.clear();
        radiusSet = false;
        radius = 0;
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        selectedPoints.clear();
        radiusSet = false;
        radius = 0;
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

        // Check if we need to reset
        if (radiusSet && selectedPoints.size() >= 1) {
            // We've already created a circle, reset for a new one
            selectedPoints.clear();
            radiusSet = false;
            radius = 0;
            if (previewCircle != null) {
                core.removeGeometryObject(previewCircle);
                previewCircle = null;
            }
        }

        // Check if there's already a point at this location
        GeometryObject existingObject = core.findObjectAt(x, y);
        Point point = null;

        if (existingObject instanceof Point) {
            // Use existing point
            point = (Point) existingObject;
            x = point.getX();
            y = point.getY();

            // Check if this point is already selected (only matters for radius points)
            if (!radiusSet && selectedPoints.size() == 1 && selectedPoints.contains(point)) {
                // Point already selected, show message
                if (ui != null) {
                    ui.updateStatus("Point " + point.getName() + " is already selected.");
                }
                return;
            }

            // Update status
            if (ui != null) {
                ui.updateStatus("Using existing point " + point.getName());
            }
        } else {
            // Create a new point at the clicked location
            point = createPoint(x, y);

            // Update status
            if (ui != null) {
                ui.updateStatus("Created point " + point.getName());
            }
        }

        // Add the point to our list
        selectedPoints.add(point);

        // Update status and handle the current state
        if (!radiusSet) {
            if (selectedPoints.size() == 1) {
                // First radius point selected
                if (ui != null) {
                    ui.updateStatus("First radius point selected. Select second radius point.");
                }
            } else if (selectedPoints.size() == 2) {
                // Second radius point selected, calculate radius
                Point p1 = selectedPoints.get(0);
                Point p2 = selectedPoints.get(1);
                radius = calculateDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                radiusSet = true;

                // Clear the points list but keep the radius
                selectedPoints.clear();

                if (ui != null) {
                    // Format radius to 2 decimal places without using String.format (not available in GWT)
                    int radiusInt = (int)(radius * 100);
                    double radiusRounded = radiusInt / 100.0;
                    ui.updateStatus("Radius set to " + radiusRounded + ". Select center point.");
                }
            }
        } else {
            // Radius is set, this point is the center
            createCircle(point, radius);
        }

        // Update preview if needed
        updatePreview(x, y);
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Show tooltip based on current state
        if (ui != null) {
            if (!radiusSet) {
                if (selectedPoints.size() == 0) {
                    ui.showTooltip("Click to set first radius point");
                } else if (selectedPoints.size() == 1) {
                    ui.showTooltip("Click to set second radius point");
                    updatePreview(x, y);
                }
            } else {
                ui.showTooltip("Click to set center point");
                updatePreview(x, y);
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }

        // Remove preview circle if it exists
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;

            // Refresh display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }
    }

    /**
     * Updates the preview based on the current mouse position.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview circle
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }

        if (!radiusSet) {
            // If we have one radius point, show a line preview
            if (selectedPoints.size() == 1) {
                Point p1 = selectedPoints.get(0);
                // We could show a line preview here, but for simplicity we'll skip it
            }
        } else {
            // Radius is set, show a circle preview at the current mouse position
            previewCircle = new Circle(x, y, radius);
            previewCircle.setColor("#AAAAAA"); // Gray for preview
            previewCircle.setLineWidth(1.0); // Thinner line for preview
            previewCircle.setLineStyle(1); // Dashed line for preview
            core.addGeometryObject(previewCircle);

            // Refresh display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }
    }

    /**
     * Creates a circle with the specified center and radius.
     * @param center The center point
     * @param radius The radius
     */
    private void createCircle(Point center, double radius) {
        // Remove preview circle if it exists
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }

        // Create the final circle
        Circle circle = new Circle(center.getX(), center.getY(), radius);
        circle.setColor("#FF0000"); // Red for final circle
        circle.setLineWidth(2.0); // Default line width
        core.addGeometryObject(circle);

        // Select the circle
        core.selectObject(circle);

        // Update status
        if (ui != null) {
            // Format radius to 2 decimal places without using String.format (not available in GWT)
            int radiusInt = (int)(radius * 100);
            double radiusRounded = radiusInt / 100.0;
            ui.updateStatus("Circle created with center " + center.getName() + 
                " and radius " + radiusRounded);
        }

        // Reset for next circle but keep the radius
        selectedPoints.clear();
    }

    /**
     * Calculates the distance between two points.
     * @param x1 The x-coordinate of the first point
     * @param y1 The y-coordinate of the first point
     * @param x2 The x-coordinate of the second point
     * @param y2 The y-coordinate of the second point
     * @return The distance between the two points
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
