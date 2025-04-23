package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating triangles.
 * This tool allows users to select three points and creates a triangle.
 */
public class TriangleTool extends AbstractTool {
    private Point firstPoint = null;
    private Point secondPoint = null;
    private int state = 0; // 0: no points selected, 1: first point selected, 2: second point selected
    private Line previewLine1 = null;
    private Line previewLine2 = null;
    private Line previewLine3 = null;

    /**
     * Constructor for TriangleTool.
     * @param core The GExpertCore instance
     */
    public TriangleTool(GExpertCore core) {
        super(core, "Triangle", "Create a triangle");
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
        firstPoint = null;
        secondPoint = null;
        removePreviewObjects();
    }

    /**
     * Removes any preview objects.
     */
    private void removePreviewObjects() {
        if (previewLine1 != null) {
            core.removeGeometryObject(previewLine1);
            previewLine1 = null;
        }
        if (previewLine2 != null) {
            core.removeGeometryObject(previewLine2);
            previewLine2 = null;
        }
        if (previewLine3 != null) {
            core.removeGeometryObject(previewLine3);
            previewLine3 = null;
        }
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (state == 0) {
            // First click - select or create first point
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj instanceof Point) {
                // Use existing point
                firstPoint = (Point) obj;
            } else {
                // Create a new point
                firstPoint = createPoint(x, y);
            }
            state = 1;
            
            // Update status
            if (ui != null) {
                ui.updateStatus("First point selected. Click to select second point of the triangle.");
            }
        } else if (state == 1) {
            // Second click - select or create second point
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj instanceof Point) {
                // Use existing point
                secondPoint = (Point) obj;
            } else {
                // Create a new point
                secondPoint = createPoint(x, y);
            }
            
            // Check if the points are different
            if (secondPoint == firstPoint) {
                if (ui != null) {
                    ui.updateStatus("Please select a different point for the second point.");
                }
                return;
            }
            
            state = 2;
            
            // Update status
            if (ui != null) {
                ui.updateStatus("Second point selected. Click to select third point and create triangle.");
            }
        } else if (state == 2) {
            // Third click - select or create third point and create triangle
            GeometryObject obj = core.findObjectAt(x, y);
            Point thirdPoint;
            if (obj instanceof Point) {
                // Use existing point
                thirdPoint = (Point) obj;
            } else {
                // Create a new point
                thirdPoint = createPoint(x, y);
            }
            
            // Check if the point is different from the first two points
            if (thirdPoint == firstPoint || thirdPoint == secondPoint) {
                if (ui != null) {
                    ui.updateStatus("Please select a different point for the third point.");
                }
                return;
            }
            
            // Create the triangle
            createTriangle(thirdPoint);
            
            // Reset for next triangle
            resetState();
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (state == 1) {
            // First point selected, show preview line
            removePreviewObjects();
            
            // Create preview line from first point to mouse position
            previewLine1 = new Line(firstPoint.getX(), firstPoint.getY(), x, y);
            previewLine1.setColor("#AAAAAA"); // Gray for preview
            previewLine1.setLineWidth(1.0); // Thinner line for preview
            previewLine1.setLineStyle(1); // Dashed line for preview
            core.addGeometryObject(previewLine1);
            
            // Update UI
            if (ui != null) {
                ui.refreshDisplay();
                ui.showTooltip("Click to set second point");
            }
        } else if (state == 2) {
            // Second point selected, show preview triangle
            updatePreview(x, y);
            
            // Update UI
            if (ui != null) {
                ui.refreshDisplay();
                ui.showTooltip("Click to set third point and create triangle");
            }
        } else {
            // No points selected yet
            if (ui != null) {
                ui.showTooltip("Click to set first point");
            }
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Remove preview objects
        removePreviewObjects();
        
        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }

    /**
     * Updates the preview of the triangle.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview objects
        removePreviewObjects();
        
        // Create preview lines for the triangle
        previewLine1 = new Line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
        previewLine1.setColor("#AAAAAA"); // Gray for preview
        previewLine1.setLineWidth(1.0); // Thinner line for preview
        previewLine1.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine1);
        
        previewLine2 = new Line(secondPoint.getX(), secondPoint.getY(), x, y);
        previewLine2.setColor("#AAAAAA"); // Gray for preview
        previewLine2.setLineWidth(1.0); // Thinner line for preview
        previewLine2.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine2);
        
        previewLine3 = new Line(x, y, firstPoint.getX(), firstPoint.getY());
        previewLine3.setColor("#AAAAAA"); // Gray for preview
        previewLine3.setLineWidth(1.0); // Thinner line for preview
        previewLine3.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine3);
    }

    /**
     * Creates a triangle with the given third point.
     * @param thirdPoint The third point of the triangle
     */
    private void createTriangle(Point thirdPoint) {
        // Remove preview objects
        removePreviewObjects();
        
        // Create the triangle sides
        Line side1 = new Line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
        Line side2 = new Line(secondPoint.getX(), secondPoint.getY(), thirdPoint.getX(), thirdPoint.getY());
        Line side3 = new Line(thirdPoint.getX(), thirdPoint.getY(), firstPoint.getX(), firstPoint.getY());
        
        // Set line properties
        side1.setColor("#FF0000"); // Red for lines
        side2.setColor("#FF0000");
        side3.setColor("#FF0000");
        
        // Add lines to the core
        core.addGeometryObject(side1);
        core.addGeometryObject(side2);
        core.addGeometryObject(side3);
        
        // Select the third point
        core.selectObject(thirdPoint);
        
        // Update status
        if (ui != null) {
            ui.updateStatus("Created triangle with vertices " + 
                firstPoint.getName() + ", " + secondPoint.getName() + ", and " + thirdPoint.getName());
        }
    }
}