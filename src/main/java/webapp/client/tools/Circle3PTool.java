package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Circle;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for creating a circle through three points.
 * This tool allows users to select three points and creates a circle that passes through all three points.
 */
public class Circle3PTool extends AbstractTool {
    private List<Point> selectedPoints = new ArrayList<>();
    private Circle previewCircle = null;

    /**
     * Constructor for Circle3PTool.
     * @param core The GExpertCore instance
     */
    public Circle3PTool(GExpertCore core) {
        super(core, "Circle by Three Points", "Create a circle passing through three points");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        selectedPoints.clear();
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        selectedPoints.clear();
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

        // Check if we already have three points
        if (selectedPoints.size() >= 3) {
            // Reset and start over
            selectedPoints.clear();
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

            // Check if this point is already selected
            if (selectedPoints.contains(point)) {
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

        // Update status
        if (ui != null) {
            if (selectedPoints.size() == 1) {
                ui.updateStatus("First point selected. Select second point.");
            } else if (selectedPoints.size() == 2) {
                ui.updateStatus("Second point selected. Select third point.");
            } else if (selectedPoints.size() == 3) {
                ui.updateStatus("Third point selected. Creating circle...");
                createCircle();
            }
        }

        // Update preview if we have at least two points
        if (selectedPoints.size() >= 2) {
            updatePreview(x, y);
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Show tooltip based on current state
        if (ui != null) {
            if (selectedPoints.size() == 0) {
                ui.showTooltip("Click to set first point");
            } else if (selectedPoints.size() == 1) {
                ui.showTooltip("Click to set second point");
            } else if (selectedPoints.size() == 2) {
                ui.showTooltip("Click to set third point");
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
     * Updates the preview circle based on the current mouse position.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview circle
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }

        // We need at least two points to show a preview
        if (selectedPoints.size() < 2) {
            return;
        }

        // If we have exactly two points, use the current mouse position as the third point
        Point p1 = selectedPoints.get(0);
        Point p2 = selectedPoints.get(1);
        Point p3 = selectedPoints.size() == 3 ? selectedPoints.get(2) : new Point(x, y);

        // Calculate the center and radius of the circle
        double[] center = calculateCircleCenter(p1, p2, p3);

        // If the points are collinear, we can't create a circle
        if (center == null) {
            return;
        }

        double centerX = center[0];
        double centerY = center[1];
        double radius = calculateDistance(centerX, centerY, p1.getX(), p1.getY());

        // Create preview circle
        previewCircle = new Circle(centerX, centerY, radius);
        previewCircle.setColor("#AAAAAA"); // Gray for preview
        previewCircle.setLineWidth(1.0); // Thinner line for preview
        previewCircle.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewCircle);

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Creates the final circle through the three selected points.
     */
    private void createCircle() {
        // We need exactly three points to create a circle
        if (selectedPoints.size() != 3) {
            return;
        }

        Point p1 = selectedPoints.get(0);
        Point p2 = selectedPoints.get(1);
        Point p3 = selectedPoints.get(2);

        // Calculate the center and radius of the circle
        double[] center = calculateCircleCenter(p1, p2, p3);

        // If the points are collinear, we can't create a circle
        if (center == null) {
            if (ui != null) {
                ui.updateStatus("Cannot create circle: the three points are collinear.");
            }
            return;
        }

        double centerX = center[0];
        double centerY = center[1];
        double radius = calculateDistance(centerX, centerY, p1.getX(), p1.getY());

        // Remove preview circle if it exists
        if (previewCircle != null) {
            core.removeGeometryObject(previewCircle);
            previewCircle = null;
        }

        // Create the final circle
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setColor("#FF0000"); // Red for final circle
        circle.setLineWidth(2.0); // Default line width
        core.addGeometryObject(circle);

        // Select the circle
        core.selectObject(circle);

        // Update status
        if (ui != null) {
            ui.updateStatus("Circle created through points " + 
                p1.getName() + ", " + p2.getName() + ", and " + p3.getName());
        }

        // Reset for next circle
        selectedPoints.clear();
    }

    /**
     * Calculates the center of a circle passing through three points.
     * @param p1 The first point
     * @param p2 The second point
     * @param p3 The third point
     * @return An array containing the x and y coordinates of the center, or null if the points are collinear
     */
    private double[] calculateCircleCenter(Point p1, Point p2, Point p3) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();

        // Check if the points are collinear
        double area = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        if (Math.abs(area) < 1e-10) {
            // Points are collinear, can't create a circle
            return null;
        }

        // Calculate the center using the perpendicular bisector method
        double d1 = x1 * x1 + y1 * y1;
        double d2 = x2 * x2 + y2 * y2;
        double d3 = x3 * x3 + y3 * y3;

        double a = x1 * (y2 - y3) - y1 * (x2 - x3) + x2 * y3 - x3 * y2;
        double b = d1 * (y3 - y2) + d2 * (y1 - y3) + d3 * (y2 - y1);
        double c = d1 * (x2 - x3) + d2 * (x3 - x1) + d3 * (x1 - x2);

        double centerX = b / (2 * a);
        double centerY = c / (2 * a);

        return new double[] { centerX, centerY };
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
