package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating parallel lines.
 * This tool allows users to create a line parallel to an existing line through a specified point.
 */
public class ParallelTool extends AbstractTool {
    private Line selectedLine = null;
    private boolean lineSelected = false;
    private Line previewLine = null;

    /**
     * Constructor for ParallelTool.
     * @param core The GExpertCore instance
     */
    public ParallelTool(GExpertCore core) {
        super(core, "Parallel", "Create a line parallel to another line");
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
                    ui.updateStatus("Line selected. Click to set a point for the parallel line.");
                }
            } else {
                // Not a line, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a line first.");
                }
            }
        } else {
            // Second click - create parallel line through point

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
                    ui.updateStatus("Using existing point " + throughPoint.getName() + " for parallel line.");
                }
            } else {
                // Create a new point at the clicked location
                throughPoint = createPoint(x, y);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + throughPoint.getName() + " for parallel line.");
                }
            }

            // Calculate the parallel line
            createParallelLine(selectedLine, throughPoint);

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
            // Update preview of parallel line
            updatePreview(x, y);

            // Show tooltip
            if (ui != null) {
                ui.showTooltip("Click to set point for parallel line");
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
     * Updates the preview of the parallel line.
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
            // Calculate the parallel line through (x, y)
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

            // Create preview line with same direction but through the point (x, y)
            // Extend the line in both directions
            double extendFactor = 1000; // Extend the line by this factor
            double previewX1 = x - dx * extendFactor;
            double previewY1 = y - dy * extendFactor;
            double previewX2 = x + dx * extendFactor;
            double previewY2 = y + dy * extendFactor;

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
     * Creates a parallel line through the specified point.
     * @param originalLine The original line
     * @param throughPoint The point through which the parallel line passes
     */
    private void createParallelLine(Line originalLine, Point throughPoint) {
        // Calculate the parallel line
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

        // Create parallel line with same direction but through the point (px, py)
        // Extend the line in both directions
        double extendFactor = 1000; // Extend the line by this factor
        double parallelX1 = px - dx * extendFactor;
        double parallelY1 = py - dy * extendFactor;
        double parallelX2 = px + dx * extendFactor;
        double parallelY2 = py + dy * extendFactor;

        // Remove preview line if it exists
        if (previewLine != null) {
            core.removeGeometryObject(previewLine);
            previewLine = null;
        }

        // Create the final parallel line
        Line parallelLine = new Line(parallelX1, parallelY1, parallelX2, parallelY2);
        parallelLine.setColor("#FF0000"); // Red for lines
        parallelLine.setLineWidth(2.0); // Default line width
        core.addGeometryObject(parallelLine);

        // Select the new line
        core.selectObject(parallelLine);

        // Update status
        if (ui != null) {
            ui.updateStatus("Parallel line created through point " + throughPoint.getName());
        }
    }
}
