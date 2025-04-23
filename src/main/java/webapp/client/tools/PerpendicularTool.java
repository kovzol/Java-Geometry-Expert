package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating perpendicular lines.
 * This tool allows users to create a line perpendicular to an existing line through a specified point.
 */
public class PerpendicularTool extends AbstractTool {
    private Line selectedLine = null;
    private boolean lineSelected = false;
    private Line previewLine = null;

    /**
     * Constructor for PerpendicularTool.
     * @param core The GExpertCore instance
     */
    public PerpendicularTool(GExpertCore core) {
        super(core, "Perpendicular", "Create a line perpendicular to another line");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        lineSelected = false;
        selectedLine = null;
        previewLine = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        lineSelected = false;
        selectedLine = null;

        // Remove preview line if it exists
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!lineSelected) {
            // First click - select a line
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Line) {
                // Use existing line
                selectedLine = (Line) existingObject;
                lineSelected = true;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Line selected. Click to set a point for the perpendicular line.");
                }
            } else {
                // Not a line, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a line first.");
                }
            }
        } else {
            // Second click - create perpendicular line through point

            // Check if there's already a point at this location
            GeometryObject existingObject = core.findObjectAt(x, y);
            Point throughPoint = null;

            if (existingObject instanceof Point) {
                // Use existing point
                throughPoint = (Point) existingObject;
                x = throughPoint.getX();
                y = throughPoint.getY();

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + throughPoint.getName() + " for perpendicular line.");
                }
            } else {
                // Create a new point at the clicked location
                throughPoint = createPoint(x, y);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + throughPoint.getName() + " for perpendicular line.");
                }
            }

            // Calculate the perpendicular line
            createPerpendicularLine(selectedLine, throughPoint);

            // Reset for next operation
            lineSelected = false;
            selectedLine = null;
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (lineSelected) {
            // Update preview of perpendicular line
            updatePreview(x, y);

            // Show tooltip
            if (ui != null) {
                ui.showTooltip("Click to set point for perpendicular line");
            }
        } else {
            // Show tooltip for first click
            if (ui != null) {
                GeometryObject obj = core.findObjectAt(x, y);
                if (obj instanceof Line) {
                    ui.showTooltip("Click to select this line");
                } else {
                    ui.showTooltip("Select a line first");
                }
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Remove preview line if it exists
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;

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
     * Updates the preview of the perpendicular line.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview line
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }

        if (selectedLine != null) {
            // Calculate the perpendicular line through (x, y)
            double x1 = selectedLine.getX();
            double y1 = selectedLine.getY();
            double x2 = selectedLine.getX2();
            double y2 = selectedLine.getY2();

            // Calculate direction vector of the original line
            double dx = x2 - x1;
            double dy = y2 - y1;

            // Calculate length of the line for scaling
            double length = Math.sqrt(dx * dx + dy * dy);

            // Normalize direction vector
            dx /= length;
            dy /= length;

            // Calculate perpendicular direction vector (rotate 90 degrees)
            double perpDx = -dy;
            double perpDy = dx;

            // Create preview line with perpendicular direction through the point (x, y)
            // Extend the line in both directions
            double extendFactor = 1000; // Extend the line by this factor
            double previewX1 = x - perpDx * extendFactor;
            double previewY1 = y - perpDy * extendFactor;
            double previewX2 = x + perpDx * extendFactor;
            double previewY2 = y + perpDy * extendFactor;

            // Create the preview line
            previewLine = new Line(previewX1, previewY1, previewX2, previewY2);
            previewLine.setColor("#AAAAAA"); // Gray for preview
            previewLine.setLineWidth(1.0); // Thinner line for preview
            previewLine.setLineStyle(1); // Dashed line for preview
            core.addGeometryObject(previewLine);

            // Refresh display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }
    }

    /**
     * Creates a perpendicular line through the specified point.
     * @param originalLine The original line
     * @param throughPoint The point through which the perpendicular line passes
     */
    private void createPerpendicularLine(Line originalLine, Point throughPoint) {
        // Calculate the perpendicular line
        double x1 = originalLine.getX();
        double y1 = originalLine.getY();
        double x2 = originalLine.getX2();
        double y2 = originalLine.getY2();
        double px = throughPoint.getX();
        double py = throughPoint.getY();

        // Calculate direction vector of the original line
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calculate length of the line for scaling
        double length = Math.sqrt(dx * dx + dy * dy);

        // Normalize direction vector
        dx /= length;
        dy /= length;

        // Calculate perpendicular direction vector (rotate 90 degrees)
        double perpDx = -dy;
        double perpDy = dx;

        // Create perpendicular line with perpendicular direction through the point (px, py)
        // Extend the line in both directions
        double extendFactor = 1000; // Extend the line by this factor
        double perpX1 = px - perpDx * extendFactor;
        double perpY1 = py - perpDy * extendFactor;
        double perpX2 = px + perpDx * extendFactor;
        double perpY2 = py + perpDy * extendFactor;

        // Remove preview line if it exists
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }

        // Create the final perpendicular line
        Line perpLine = new Line(perpX1, perpY1, perpX2, perpY2);
        perpLine.setColor("#FF0000"); // Red for lines
        perpLine.setLineWidth(2.0); // Default line width
        core.addGeometryObject(perpLine);

        // Select the new line
        core.selectObject(perpLine);

        // Update status
        if (ui != null) {
            ui.updateStatus("Perpendicular line created through point " + throughPoint.getName());
        }
    }
}
