package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating midpoints.
 * This tool allows users to select two points and creates a midpoint between them.
 */
public class MidpointTool extends AbstractTool {
    private Point firstPoint = null;
    private boolean firstPointSelected = false;
    private Line previewLine = null;
    private Point previewMidpoint = null;

    /**
     * Constructor for MidpointTool.
     * @param core The GExpertCore instance
     */
    public MidpointTool(GExpertCore core) {
        super(core, "Midpoint", "Create a midpoint between two points");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        firstPointSelected = false;
        firstPoint = null;
        previewLine = null;
        previewMidpoint = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        firstPointSelected = false;
        firstPoint = null;

        // Remove preview objects if they exist
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewMidpoint != null) {
            core.removeGeometryObject(previewMidpoint);
            previewMidpoint = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!firstPointSelected) {
            // First click - select first point
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point
                firstPoint = (Point) existingObject;
                firstPointSelected = true;

                // Update status
                if (ui != null) {
                    ui.updateStatus("First point " + firstPoint.getName() + " selected. Click to select second point.");
                }
            } else {
                // Not a point, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a point first.");
                }
            }
        } else {
            // Second click - select second point and create midpoint
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point
                Point secondPoint = (Point) existingObject;

                // Check if it's the same as the first point
                if (secondPoint == firstPoint) {
                    if (ui != null) {
                        ui.updateStatus("Please select a different point.");
                    }
                    return;
                }

                // Create the midpoint
                createMidpoint(firstPoint, secondPoint);

                // Reset for next operation
                firstPointSelected = false;
                firstPoint = null;
            } else {
                // Not a point, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a point for the second point.");
                }
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (firstPointSelected) {
            // Update preview of midpoint and line
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj instanceof Point && obj != firstPoint) {
                updatePreview(firstPoint, (Point) obj);

                // Show tooltip
                if (ui != null) {
                    ui.showTooltip("Click to create midpoint");
                }
            } else {
                // Remove preview if not over a valid second point
                if (previewLine != null) {
                    core.removeGeometryObject(previewLine);
                    previewLine = null;
                }
                if (previewMidpoint != null) {
                    core.removeGeometryObject(previewMidpoint);
                    previewMidpoint = null;
                }

                // Refresh display
                if (ui != null) {
                    ui.refreshDisplay();
                    ui.showTooltip("Select a different point for the second point");
                }
            }
        } else {
            // Show tooltip for first click
            if (ui != null) {
                GeometryObject obj = core.findObjectAt(x, y);
                if (obj instanceof Point) {
                    ui.showTooltip("Click to select this point");
                } else {
                    ui.showTooltip("Select a point first");
                }
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Remove preview objects if they exist
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewMidpoint != null) {
            core.removeGeometryObject(previewMidpoint);
            previewMidpoint = null;
        }

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
            ui.hideTooltip();
        }
    }

    /**
     * Updates the preview of the midpoint and line.
     * @param p1 The first point
     * @param p2 The second point
     */
    private void updatePreview(Point p1, Point p2) {
        // Remove old preview objects
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewMidpoint != null) {
            core.removeGeometryObject(previewMidpoint);
            previewMidpoint = null;
        }

        // Calculate midpoint coordinates
        double midX = (p1.getX() + p2.getX()) / 2;
        double midY = (p1.getY() + p2.getY()) / 2;

        // Create preview line
        previewLine = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        previewLine.setColor("#AAAAAA"); // Gray for preview
        previewLine.setLineWidth(1.0); // Thinner line for preview
        previewLine.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine);

        // Create preview midpoint
        previewMidpoint = new Point(midX, midY);
        previewMidpoint.setColor("#AAAAAA"); // Gray for preview
        core.addGeometryObject(previewMidpoint);

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Creates a midpoint between two points.
     * @param p1 The first point
     * @param p2 The second point
     */
    private void createMidpoint(Point p1, Point p2) {
        // Calculate midpoint coordinates
        double midX = (p1.getX() + p2.getX()) / 2;
        double midY = (p1.getY() + p2.getY()) / 2;

        // Remove preview objects if they exist
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewMidpoint != null) {
            core.removeGeometryObject(previewMidpoint);
            previewMidpoint = null;
        }

        // Create the midpoint
        Point midpoint = createPoint(midX, midY);

        // Select the midpoint
        core.selectObject(midpoint);

        // Update status
        if (ui != null) {
            ui.updateStatus("Created midpoint " + midpoint.getName() + " between points " + p1.getName() + " and " + p2.getName());
        }
    }
}
