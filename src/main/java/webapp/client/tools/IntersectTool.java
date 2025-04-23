package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Circle;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for finding intersections between geometric objects.
 * This tool allows users to select two objects (lines and/or circles) and creates a point at their intersection.
 */
public class IntersectTool extends AbstractTool {
    private GeometryObject firstObject = null;
    private boolean firstObjectSelected = false;

    /**
     * Constructor for IntersectTool.
     * @param core The GExpertCore instance
     */
    public IntersectTool(GExpertCore core) {
        super(core, "Intersect", "Find intersection between two objects");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        firstObjectSelected = false;
        firstObject = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        firstObjectSelected = false;
        firstObject = null;
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!firstObjectSelected) {
            // First click - select first object (line or circle)
            GeometryObject obj = findObjectAt(x, y);
            if (obj != null && (obj instanceof Line || obj instanceof Circle)) {
                firstObject = obj;
                firstObjectSelected = true;

                // Update status
                if (ui != null) {
                    String objType = (obj instanceof Line) ? "line" : "circle";
                    ui.updateStatus("Selected " + objType + ". Click on another object to find intersection.");
                }
            } else {
                // Not a line or circle, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a line or circle first.");
                }
            }
        } else {
            // Second click - select second object and find intersection
            GeometryObject secondObject = findObjectAt(x, y);
            if (secondObject != null && (secondObject instanceof Line || secondObject instanceof Circle)) {
                // Check if it's the same as the first object
                if (secondObject == firstObject) {
                    if (ui != null) {
                        ui.updateStatus("Please select a different object.");
                    }
                    return;
                }

                // Find intersection
                findIntersection(firstObject, secondObject, x, y);

                // Reset for next operation
                firstObjectSelected = false;
                firstObject = null;
            } else {
                // Not a line or circle, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a line or circle for the second object.");
                }
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (firstObjectSelected) {
            // Show tooltip for second object selection
            if (ui != null) {
                GeometryObject obj = findObjectAt(x, y);
                if (obj != null && (obj instanceof Line || obj instanceof Circle) && obj != firstObject) {
                    ui.showTooltip("Click to find intersection with this object");
                } else {
                    ui.showTooltip("Select a line or circle to find intersection");
                }
            }
        } else {
            // Show tooltip for first object selection
            if (ui != null) {
                GeometryObject obj = findObjectAt(x, y);
                if (obj != null && (obj instanceof Line || obj instanceof Circle)) {
                    ui.showTooltip("Click to select this object");
                } else {
                    ui.showTooltip("Select a line or circle first");
                }
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }

    /**
     * Finds an object (line or circle) at the specified coordinates.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The found object, or null if no object is found
     */
    private GeometryObject findObjectAt(double x, double y) {
        GeometryObject obj = core.findObjectAt(x, y);
        if (obj instanceof Line || obj instanceof Circle) {
            return obj;
        }
        return null;
    }

    /**
     * Finds the intersection between two objects and creates a point at the intersection.
     * @param obj1 The first object (line or circle)
     * @param obj2 The second object (line or circle)
     * @param x The x-coordinate of the mouse click (used for selecting between multiple intersections)
     * @param y The y-coordinate of the mouse click (used for selecting between multiple intersections)
     */
    private void findIntersection(GeometryObject obj1, GeometryObject obj2, double x, double y) {
        // Check all possible combinations of object types
        if (obj1 instanceof Line && obj2 instanceof Line) {
            // Line-Line intersection
            Line line1 = (Line) obj1;
            Line line2 = (Line) obj2;

            // Check if lines are parallel
            double dx1 = line1.getX2() - line1.getX();
            double dy1 = line1.getY2() - line1.getY();
            double dx2 = line2.getX2() - line2.getX();
            double dy2 = line2.getY2() - line2.getY();

            // Calculate cross product to check if lines are parallel
            double crossProduct = dx1 * dy2 - dy1 * dx2;

            if (Math.abs(crossProduct) < 1e-10) {
                // Lines are parallel
                if (ui != null) {
                    ui.updateStatus("The selected lines are parallel and don't intersect.");
                }
                return;
            }

            // Calculate intersection point
            double x1 = line1.getX();
            double y1 = line1.getY();
            double x2 = line1.getX2();
            double y2 = line1.getY2();
            double x3 = line2.getX();
            double y3 = line2.getY();
            double x4 = line2.getX2();
            double y4 = line2.getY2();

            double denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
            double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;

            double intersectX = x1 + ua * (x2 - x1);
            double intersectY = y1 + ua * (y2 - y1);

            // Create intersection point
            createIntersectionPoint(intersectX, intersectY, line1, line2);

        } else if (obj1 instanceof Circle && obj2 instanceof Circle) {
            // Circle-Circle intersection
            Circle circle1 = (Circle) obj1;
            Circle circle2 = (Circle) obj2;

            double x1 = circle1.getX();
            double y1 = circle1.getY();
            double r1 = circle1.getRadius();
            double x2 = circle2.getX();
            double y2 = circle2.getY();
            double r2 = circle2.getRadius();

            // Calculate distance between circle centers
            double dx = x2 - x1;
            double dy = y2 - y1;
            double d = Math.sqrt(dx * dx + dy * dy);

            // Check if circles are too far apart or one is inside the other
            if (d > r1 + r2 || d < Math.abs(r1 - r2)) {
                if (ui != null) {
                    ui.updateStatus("The selected circles don't intersect.");
                }
                return;
            }

            // Check if circles are coincident
            if (Math.abs(d) < 1e-10 && Math.abs(r1 - r2) < 1e-10) {
                if (ui != null) {
                    ui.updateStatus("The selected circles are coincident and have infinite intersection points.");
                }
                return;
            }

            // Calculate intersection points
            double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
            double h = Math.sqrt(r1 * r1 - a * a);

            double x3 = x1 + a * dx / d;
            double y3 = y1 + a * dy / d;

            double intersect1X = x3 + h * dy / d;
            double intersect1Y = y3 - h * dx / d;
            double intersect2X = x3 - h * dy / d;
            double intersect2Y = y3 + h * dx / d;

            // Determine which intersection point is closer to the mouse click
            double dist1 = Math.sqrt(Math.pow(intersect1X - x, 2) + Math.pow(intersect1Y - y, 2));
            double dist2 = Math.sqrt(Math.pow(intersect2X - x, 2) + Math.pow(intersect2Y - y, 2));

            if (dist1 <= dist2) {
                createIntersectionPoint(intersect1X, intersect1Y, circle1, circle2);
            } else {
                createIntersectionPoint(intersect2X, intersect2Y, circle1, circle2);
            }

        } else {
            // Line-Circle or Circle-Line intersection
            Line line;
            Circle circle;

            if (obj1 instanceof Line) {
                line = (Line) obj1;
                circle = (Circle) obj2;
            } else {
                line = (Line) obj2;
                circle = (Circle) obj1;
            }

            double cx = circle.getX();
            double cy = circle.getY();
            double r = circle.getRadius();
            double x1 = line.getX();
            double y1 = line.getY();
            double x2 = line.getX2();
            double y2 = line.getY2();

            // Calculate the closest point on the line to the circle center
            double dx = x2 - x1;
            double dy = y2 - y1;
            double t = ((cx - x1) * dx + (cy - y1) * dy) / (dx * dx + dy * dy);

            double closestX = x1 + t * dx;
            double closestY = y1 + t * dy;

            // Calculate distance from closest point to circle center
            double distance = Math.sqrt(Math.pow(closestX - cx, 2) + Math.pow(closestY - cy, 2));

            // Check if line and circle intersect
            if (distance > r) {
                if (ui != null) {
                    ui.updateStatus("The selected line and circle don't intersect.");
                }
                return;
            }

            // Calculate intersection points
            double dt = Math.sqrt(r * r - distance * distance) / Math.sqrt(dx * dx + dy * dy);

            double intersect1X = closestX + dt * dx;
            double intersect1Y = closestY + dt * dy;
            double intersect2X = closestX - dt * dx;
            double intersect2Y = closestY - dt * dy;

            // Determine which intersection point is closer to the mouse click
            double dist1 = Math.sqrt(Math.pow(intersect1X - x, 2) + Math.pow(intersect1Y - y, 2));
            double dist2 = Math.sqrt(Math.pow(intersect2X - x, 2) + Math.pow(intersect2Y - y, 2));

            if (dist1 <= dist2) {
                createIntersectionPoint(intersect1X, intersect1Y, line, circle);
            } else {
                createIntersectionPoint(intersect2X, intersect2Y, line, circle);
            }
        }
    }

    /**
     * Creates a point at the intersection of two objects.
     * @param x The x-coordinate of the intersection
     * @param y The y-coordinate of the intersection
     * @param obj1 The first object
     * @param obj2 The second object
     */
    private void createIntersectionPoint(double x, double y, GeometryObject obj1, GeometryObject obj2) {
        // Create a new point at the intersection
        Point point = createPoint(x, y);

        // Select the new point
        core.selectObject(point);

        // Update status
        if (ui != null) {
            String obj1Type = (obj1 instanceof Line) ? "line" : "circle";
            String obj2Type = (obj2 instanceof Line) ? "line" : "circle";
            ui.updateStatus("Created intersection point " + point.getName() + " between " + obj1Type + " and " + obj2Type);
        }
    }
}
