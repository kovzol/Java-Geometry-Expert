package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating lines.
 */
public class LineTool extends AbstractTool {
    private double startX, startY;
    private boolean firstPointSelected = false;
    private Point startPoint = null;
    private Line previewLine = null;

    /**
     * Constructor for LineTool.
     * @param core The GExpertCore instance
     */
    public LineTool(GExpertCore core) {
        super(core, "Line", "Create a line");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        firstPointSelected = false;
        startPoint = null;
        previewLine = null;
    }

    @Override
    public void deactivate() {
        super.deactivate(); // This will call removePreviewObject()
        firstPointSelected = false;
        startPoint = null;
        previewLine = null;
    }

    @Override
    protected GeometryObject createPreviewObject(double x, double y) {
        if (firstPointSelected) {
            // Create a new preview line
            Line line = new Line(startX, startY, x, y);
            line.setColor("#AAAAAA"); // Gray for preview
            line.setLineWidth(100.0); // Thinner line for preview
            line.setLineStyle(1); // Dashed line for preview
            return line;
        }
        return null;
    }

    @Override
    protected boolean updatePreviewObject(double x, double y) {
        if (firstPointSelected && previewObject instanceof Line) {
            // Update the existing preview line's endpoint
            Line line = (Line) previewObject;
            line.setColor("#AAAAFF");
            line.setLineWidth(0.5);
            line.setEndPoint(x, y);

            // Update UI
            if (ui != null) {
                ui.updateStatus("Click to set end point");
                ui.showTooltip("Click to set end point");
                ui.refreshDisplay();
            }
            return true;
        }
        return false;
    }

    @Override
    protected GeometryObject finalizePreviewObject(double x, double y) {
        if (firstPointSelected && previewObject instanceof Line) {
            // Update the preview line's appearance to match the final line
            Line line = (Line) previewObject;
            line.setColor("#FF0000"); // Red for lines
            line.setLineWidth(2.0); // Default line width
            line.setLineStyle(0); // Solid line

            // Select the line
            core.selectObject(line);

            // Return the finalized line
            return line;
        }
        return null;
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (!firstPointSelected) {
            // First click - select or create start point

            // Check if there's already a point at this location
            GeometryObject existingObject = core.findObjectAt(x, y);
            if (existingObject instanceof Point) {
                // Use existing point as start point
                startPoint = (Point) existingObject;
                startX = startPoint.getX();
                startY = startPoint.getY();

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + startPoint.getName() + 
                        " as start point. Click to set end point.");
                }
            } else {
                // Create a new point at the clicked location
                startPoint = createPoint(x, y);

                startX = x;
                startY = y;

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + startPoint.getName() + 
                        " as start point. Click to set end point.");
                }
            }

            firstPointSelected = true;

        } else {
            // Second click - select or create end point and create line

            // Check if there's already a point at this location
            GeometryObject existingObject = core.findObjectAt(x, y);
            Point endPoint = null;

            if (existingObject instanceof Point) {
                // Use existing point as end point
                endPoint = (Point) existingObject;
                x = endPoint.getX();
                y = endPoint.getY();

                // Update status
                if (ui != null) {
                    ui.updateStatus("Using existing point " + endPoint.getName() + " as end point.");
                }
            } else {
                // Create a new point at the clicked location
                endPoint = createPoint(x, y);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Created point " + endPoint.getName() + " as end point.");
                }
            }

            // Check if start and end points are different
            if (Math.abs(x - startX) > 1 || Math.abs(y - startY) > 1) {
                // Finalize the preview object if it exists, or create a new line
                if (previewObject != null) {
                    // Finalize the preview object
                    GeometryObject finalObject = finalizePreviewObject(x, y);

                    // Set previewObject to null so it's not removed when the tool is deactivated
                    previewObject = null;
                    previewLine = null;

                    // Always refresh display to ensure the changes are visible
                    if (ui != null) {
                        ui.refreshDisplay();
                    }
                } else {
                    // Create the actual line if no preview exists
                    Line line = new Line(startX, startY, x, y);
                    line.setColor("#FF0000"); // Red for lines
                    core.addGeometryObject(line);

                    // Select the new line
                    core.selectObject(line);

                    // Always refresh display to ensure the new line is visible
                    if (ui != null) {
                        ui.refreshDisplay();
                    }
                }

                // Update status
                if (ui != null) {
                    ui.updateStatus("Line created from " + startPoint.getName() + " to " + endPoint.getName());
                }
            } else {
                // Points are too close, don't create a line
                if (ui != null) {
                    ui.updateStatus("Points are too close. Line not created.");
                }
            }

            // Reset for next line
            firstPointSelected = false;
            startPoint = null;
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (firstPointSelected) {
            // If we don't have a preview object yet, create one
            if (previewObject == null) {
                previewObject = createPreviewObject(x, y);
                if (previewObject != null) {
                    // Store a reference to the preview line for backward compatibility
                    previewLine = (Line) previewObject;
                    // Add the preview object to the core
                    core.addGeometryObject(previewObject);

                    // Initial UI update for new preview object
                    if (ui != null) {
                        ui.updateStatus("Click to set end point");
                        ui.refreshDisplay();
                    }
                }
            } else {
                // Update the existing preview object
                // UI updates are handled in updatePreviewObject
                updatePreviewObject(x, y);
            }
        } else {
            // Show tooltip for first click
            if (ui != null) {
                ui.showTooltip("Click to set start point");
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Cancel operation if first point is selected
        if (firstPointSelected) {
            // Don't reset firstPointSelected here, just remove preview object if it exists
            removePreviewObject();
            previewLine = null;
        }

        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }
}
