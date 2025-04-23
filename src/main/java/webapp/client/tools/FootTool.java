package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating the foot of a perpendicular.
 * This tool allows users to select a point and a line, and then creates the foot of the perpendicular
 * from the point to the line.
 */
public class FootTool extends AbstractTool {
    private Point selectedPoint = null;
    private boolean pointSelected = false;
    private Line previewLine = null;
    private Point previewFoot = null;

    /**
     * Constructor for FootTool.
     * @param core The GExpertCore instance
     */
    public FootTool(GExpertCore core) {
        super(core, "Foot", "Create the foot of a perpendicular from a point to a line");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        pointSelected = false;
        selectedPoint = null;
        previewLine = null;
        previewFoot = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        pointSelected = false;
        selectedPoint = null;

        // Remove preview objects if they exist
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewFoot != null) {
            core.removeGeometryObject(previewFoot);
            previewFoot = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!pointSelected) {
            // First click - select a point
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point
                selectedPoint = (Point) existingObject;
                pointSelected = true;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Point " + selectedPoint.getName() + " selected. Click on a line to create the foot of the perpendicular.");
                }
            } else {
                // Not a point, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a point first.");
                }
            }
        } else {
            // Second click - select a line and create the foot
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Line) {
                // Use existing line
                Line selectedLine = (Line) existingObject;

                // Create the foot of the perpendicular
                createFoot(selectedPoint, selectedLine);

                // Reset for next operation
                pointSelected = false;
                selectedPoint = null;
            } else {
                // Not a line, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a line to create the foot of the perpendicular.");
                }
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (pointSelected) {
            // Update preview of foot and perpendicular line
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj instanceof Line) {
                updatePreview(selectedPoint, (Line) obj);

                // Show tooltip
                if (ui != null) {
                    ui.showTooltip("Click to create the foot of the perpendicular");
                }
            } else {
                // Remove preview if not over a line
                if (previewLine != null) {
                    core.removeGeometryObject(previewLine);
                    previewLine = null;
                }
                if (previewFoot != null) {
                    core.removeGeometryObject(previewFoot);
                    previewFoot = null;
                }

                // Refresh display
                if (ui != null) {
                    ui.refreshDisplay();
                    ui.showTooltip("Select a line to create the foot of the perpendicular");
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
        if (previewFoot != null) {
            core.removeGeometryObject(previewFoot);
            previewFoot = null;
        }

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
            ui.hideTooltip();
        }
    }

    /**
     * Updates the preview of the foot and perpendicular line.
     * @param point The selected point
     * @param line The line under the mouse
     */
    private void updatePreview(Point point, Line line) {
        // Remove old preview objects
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewFoot != null) {
            core.removeGeometryObject(previewFoot);
            previewFoot = null;
        }

        // Calculate the foot of the perpendicular
        double px = point.getX();
        double py = point.getY();
        double x1 = line.getX();
        double y1 = line.getY();
        double x2 = line.getX2();
        double y2 = line.getY2();

        // Calculate direction vector of the line
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calculate length of the line for scaling
        double length = Math.sqrt(dx * dx + dy * dy);

        // Normalize direction vector
        dx /= length;
        dy /= length;

        // Calculate the projection of the point onto the line
        double dotProduct = (px - x1) * dx + (py - y1) * dy;
        double footX = x1 + dotProduct * dx;
        double footY = y1 + dotProduct * dy;

        // Create preview foot point
        previewFoot = new Point(footX, footY);
        previewFoot.setColor("#AAAAAA"); // Gray for preview
        core.addGeometryObject(previewFoot);

        // Create preview perpendicular line
        previewLine = new Line(px, py, footX, footY);
        previewLine.setColor("#AAAAAA"); // Gray for preview
        previewLine.setLineWidth(1.0); // Thinner line for preview
        previewLine.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine);

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Creates the foot of the perpendicular from a point to a line.
     * @param point The point
     * @param line The line
     */
    private void createFoot(Point point, Line line) {
        // Calculate the foot of the perpendicular
        double px = point.getX();
        double py = point.getY();
        double x1 = line.getX();
        double y1 = line.getY();
        double x2 = line.getX2();
        double y2 = line.getY2();

        // Calculate direction vector of the line
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calculate length of the line for scaling
        double length = Math.sqrt(dx * dx + dy * dy);

        // Normalize direction vector
        dx /= length;
        dy /= length;

        // Calculate the projection of the point onto the line
        double dotProduct = (px - x1) * dx + (py - y1) * dy;
        double footX = x1 + dotProduct * dx;
        double footY = y1 + dotProduct * dy;

        // Remove preview objects if they exist
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
        if (previewFoot != null) {
            core.removeGeometryObject(previewFoot);
            previewFoot = null;
        }

        // Create the foot point
        Point footPoint = createPoint(footX, footY);

        // Create the perpendicular line
        Line perpLine = new Line(px, py, footX, footY);
        perpLine.setColor("#FF0000"); // Red for lines
        perpLine.setLineWidth(2.0); // Default line width
        core.addGeometryObject(perpLine);

        // Select the foot point
        core.selectObject(footPoint);

        // Update status
        if (ui != null) {
            ui.updateStatus("Created foot point " + footPoint.getName() + " and perpendicular line from point " + point.getName());
        }
    }
}
