package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating isosceles triangles.
 * This tool allows users to select two points as the base of the triangle,
 * and then create an isosceles triangle by selecting a third point.
 */
public class IsoscelesTriangleTool extends AbstractTool {
    private Point firstPoint = null;
    private Point secondPoint = null;
    private int state = 0; // 0: no points selected, 1: first point selected, 2: second point selected
    private Line previewLine1 = null;
    private Line previewLine2 = null;
    private Line previewLine3 = null;

    /**
     * Constructor for IsoscelesTriangleTool.
     * @param core The GExpertCore instance
     */
    public IsoscelesTriangleTool(GExpertCore core) {
        super(core, "Isosceles Triangle", "Create an isosceles triangle");
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
                ui.updateStatus("First point selected. Click to select second point for the base of the triangle.");
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
                ui.updateStatus("Second point selected. Click to create the apex of the isosceles triangle.");
            }
        } else if (state == 2) {
            // Third click - create the isosceles triangle
            createIsoscelesTriangle(x, y);
            
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
            // Both base points selected, show preview of isosceles triangle
            updatePreview(x, y);
            
            // Update UI
            if (ui != null) {
                ui.refreshDisplay();
                ui.showTooltip("Click to create isosceles triangle");
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
     * Updates the preview of the isosceles triangle.
     * @param x The x-coordinate of the mouse
     * @param y The y-coordinate of the mouse
     */
    private void updatePreview(double x, double y) {
        // Remove old preview objects
        removePreviewObjects();
        
        // Calculate the base midpoint
        double midX = (firstPoint.getX() + secondPoint.getX()) / 2;
        double midY = (firstPoint.getY() + secondPoint.getY()) / 2;
        
        // Calculate the base vector
        double baseX = secondPoint.getX() - firstPoint.getX();
        double baseY = secondPoint.getY() - firstPoint.getY();
        
        // Calculate the perpendicular vector (rotate 90 degrees)
        double perpX = -baseY;
        double perpY = baseX;
        
        // Normalize the perpendicular vector
        double length = Math.sqrt(perpX * perpX + perpY * perpY);
        if (length > 0) {
            perpX /= length;
            perpY /= length;
        }
        
        // Calculate the vector from midpoint to mouse position
        double mouseVectorX = x - midX;
        double mouseVectorY = y - midY;
        
        // Project this vector onto the perpendicular vector to get the height
        double dotProduct = mouseVectorX * perpX + mouseVectorY * perpY;
        
        // Ensure the apex is on the correct side (the side the mouse is on)
        if (dotProduct < 0) {
            perpX = -perpX;
            perpY = -perpY;
        }
        
        // Calculate the distance from the midpoint to the apex
        double height = Math.abs(dotProduct);
        
        // Calculate the apex position
        double apexX = midX + height * perpX;
        double apexY = midY + height * perpY;
        
        // Create preview lines
        previewLine1 = new Line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
        previewLine1.setColor("#AAAAAA"); // Gray for preview
        previewLine1.setLineWidth(1.0); // Thinner line for preview
        previewLine1.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine1);
        
        previewLine2 = new Line(firstPoint.getX(), firstPoint.getY(), apexX, apexY);
        previewLine2.setColor("#AAAAAA"); // Gray for preview
        previewLine2.setLineWidth(1.0); // Thinner line for preview
        previewLine2.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine2);
        
        previewLine3 = new Line(secondPoint.getX(), secondPoint.getY(), apexX, apexY);
        previewLine3.setColor("#AAAAAA"); // Gray for preview
        previewLine3.setLineWidth(1.0); // Thinner line for preview
        previewLine3.setLineStyle(1); // Dashed line for preview
        core.addGeometryObject(previewLine3);
    }

    /**
     * Creates an isosceles triangle with the given apex coordinates.
     * @param x The x-coordinate of the apex
     * @param y The y-coordinate of the apex
     */
    private void createIsoscelesTriangle(double x, double y) {
        // Remove preview objects
        removePreviewObjects();
        
        // Calculate the base midpoint
        double midX = (firstPoint.getX() + secondPoint.getX()) / 2;
        double midY = (firstPoint.getY() + secondPoint.getY()) / 2;
        
        // Calculate the base vector
        double baseX = secondPoint.getX() - firstPoint.getX();
        double baseY = secondPoint.getY() - firstPoint.getY();
        
        // Calculate the perpendicular vector (rotate 90 degrees)
        double perpX = -baseY;
        double perpY = baseX;
        
        // Normalize the perpendicular vector
        double length = Math.sqrt(perpX * perpX + perpY * perpY);
        if (length > 0) {
            perpX /= length;
            perpY /= length;
        }
        
        // Calculate the vector from midpoint to mouse position
        double mouseVectorX = x - midX;
        double mouseVectorY = y - midY;
        
        // Project this vector onto the perpendicular vector to get the height
        double dotProduct = mouseVectorX * perpX + mouseVectorY * perpY;
        
        // Ensure the apex is on the correct side (the side the mouse is on)
        if (dotProduct < 0) {
            perpX = -perpX;
            perpY = -perpY;
        }
        
        // Calculate the distance from the midpoint to the apex
        double height = Math.abs(dotProduct);
        
        // Calculate the apex position
        double apexX = midX + height * perpX;
        double apexY = midY + height * perpY;
        
        // Create or find the apex point
        Point apexPoint;
        GeometryObject obj = core.findObjectAt(apexX, apexY);
        if (obj instanceof Point) {
            // Use existing point
            apexPoint = (Point) obj;
        } else {
            // Create a new point
            apexPoint = createPoint(apexX, apexY);
        }
        
        // Create the triangle sides
        Line baseLine = new Line(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
        Line side1 = new Line(firstPoint.getX(), firstPoint.getY(), apexPoint.getX(), apexPoint.getY());
        Line side2 = new Line(secondPoint.getX(), secondPoint.getY(), apexPoint.getX(), apexPoint.getY());
        
        // Set line properties
        baseLine.setColor("#FF0000"); // Red for lines
        side1.setColor("#FF0000");
        side2.setColor("#FF0000");
        
        // Add lines to the core
        core.addGeometryObject(baseLine);
        core.addGeometryObject(side1);
        core.addGeometryObject(side2);
        
        // Select the apex point
        core.selectObject(apexPoint);
        
        // Update status
        if (ui != null) {
            ui.updateStatus("Created isosceles triangle with base " + 
                firstPoint.getName() + secondPoint.getName() + 
                " and apex " + apexPoint.getName());
        }
    }
}