package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating squares.
 * This tool allows users to select two points as one side of the square,
 * and then creates a square based on those points.
 */
public class SquareTool extends AbstractTool {
    private Point firstPoint = null;
    private Point secondPoint = null;
    private int state = 0; // 0: no points selected, 1: first point selected, 2: second point selected
    private Line previewLine1 = null;
    private Line previewLine2 = null;
    private Line previewLine3 = null;
    private Line previewLine4 = null;
    private Point previewPoint1 = null;
    private Point previewPoint2 = null;

    /**
     * Constructor for SquareTool.
     * @param core The GExpertCore instance
     */
    public SquareTool(GExpertCore core) {
        super(core, "Square", "Create a square");
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
        if (previewLine4 != null) {
            core.removeGeometryObject(previewLine4);
            previewLine4 = null;
        }
        if (previewPoint1 != null) {
            core.removeGeometryObject(previewPoint1);
            previewPoint1 = null;
        }
        if (previewPoint2 != null) {
            core.removeGeometryObject(previewPoint2);
            previewPoint2 = null;
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
                ui.updateStatus("First point selected. Click to select second point for the side of the square.");
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
            
            // Create the square
            createSquare();
            
            // Reset for next square
            resetState();
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        if (state == 1) {
            // First point selected, show preview line and square
            updatePreview(x, y);
            
            // Update UI
            if (ui != null) {
                ui.refreshDisplay();
                ui.showTooltip("Click to set second point and create square");
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
     * Updates the preview of the square.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview objects
        removePreviewObjects();
        
        // Create preview line from first point to mouse position
        previewLine1 = new Line(firstPoint.getX(), firstPoint.getY(), x, y);
        previewLine1.setColor("#AAAAAA"); // Gray for preview
        previewLine1.setLineWidth(1.0); // Thinner line for preview
        previewLine1.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine1);
        
        // Calculate the vector from first point to mouse position
        double dx = x - firstPoint.getX();
        double dy = y - firstPoint.getY();
        
        // Calculate the perpendicular vector (rotate 90 degrees counterclockwise)
        double perpX = -dy;
        double perpY = dx;
        
        // Calculate the other two corners of the square
        double corner3X = x + perpX;
        double corner3Y = y + perpY;
        double corner4X = firstPoint.getX() + perpX;
        double corner4Y = firstPoint.getY() + perpY;
        
        // Create preview points for the other corners
        previewPoint1 = new Point(corner3X, corner3Y);
        previewPoint1.setColor("#AAAAAA"); // Gray for preview
        core.addGeometryObject(previewPoint1);
        
        previewPoint2 = new Point(corner4X, corner4Y);
        previewPoint2.setColor("#AAAAAA"); // Gray for preview
        core.addGeometryObject(previewPoint2);
        
        // Create preview lines for the other sides of the square
        previewLine2 = new Line(x, y, corner3X, corner3Y);
        previewLine2.setColor("#AAAAAA"); // Gray for preview
        previewLine2.setLineWidth(1.0); // Thinner line for preview
        previewLine2.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine2);
        
        previewLine3 = new Line(corner3X, corner3Y, corner4X, corner4Y);
        previewLine3.setColor("#AAAAAA"); // Gray for preview
        previewLine3.setLineWidth(1.0); // Thinner line for preview
        previewLine3.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine3);
        
        previewLine4 = new Line(corner4X, corner4Y, firstPoint.getX(), firstPoint.getY());
        previewLine4.setColor("#AAAAAA"); // Gray for preview
        previewLine4.setLineWidth(1.0); // Thinner line for preview
        previewLine4.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine4);
    }

    /**
     * Creates a square based on the selected points.
     */
    private void createSquare() {
        // Remove preview objects
        removePreviewObjects();
        
        // Calculate the vector from first point to second point
        double dx = secondPoint.getX() - firstPoint.getX();
        double dy = secondPoint.getY() - firstPoint.getY();
        
        // Calculate the perpendicular vector (rotate 90 degrees counterclockwise)
        double perpX = -dy;
        double perpY = dx;
        
        // Calculate the other two corners of the square
        double corner3X = secondPoint.getX() + perpX;
        double corner3Y = secondPoint.getY() + perpY;
        double corner4X = firstPoint.getX() + perpX;
        double corner4Y = firstPoint.getY() + perpY;
        
        // Create or find the third corner point
        Point thirdPoint;
        GeometryObject obj3 = core.findObjectAt(corner3X, corner3Y);
        if (obj3 instanceof Point) {
            // Use existing point
            thirdPoint = (Point) obj3;
        } else {
            // Create a new point
            thirdPoint = createPoint(corner3X, corner3Y);
        }
        
        // Create or find the fourth corner point
        Point fourthPoint;
        GeometryObject obj4 = core.findObjectAt(corner4X, corner4Y);
        if (obj4 instanceof Point) {
            // Use existing point
            fourthPoint = (Point) obj4;
        } else {
            // Create a new point
            fourthPoint = createPoint(corner4X, corner4Y);
        }
        
        // Create the square sides
        Line side1 = new Line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
        Line side2 = new Line(secondPoint.getX(), secondPoint.getY(), thirdPoint.getX(), thirdPoint.getY());
        Line side3 = new Line(thirdPoint.getX(), thirdPoint.getY(), fourthPoint.getX(), fourthPoint.getY());
        Line side4 = new Line(fourthPoint.getX(), fourthPoint.getY(), firstPoint.getX(), firstPoint.getY());
        
        // Set line properties
        side1.setColor("#FF0000"); // Red for lines
        side2.setColor("#FF0000");
        side3.setColor("#FF0000");
        side4.setColor("#FF0000");
        
        // Add lines to the core
        core.addGeometryObject(side1);
        core.addGeometryObject(side2);
        core.addGeometryObject(side3);
        core.addGeometryObject(side4);
        
        // Update status
        if (ui != null) {
            ui.updateStatus("Created square with vertices " + 
                firstPoint.getName() + ", " + secondPoint.getName() + ", " +
                thirdPoint.getName() + ", and " + fourthPoint.getName());
        }
    }
}