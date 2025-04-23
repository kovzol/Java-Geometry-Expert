package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

/**
 * Tool for creating mirror images of objects.
 * This tool allows users to select an object and a mirror line or point, and creates a mirror image of the object.
 */
public class MirrorTool extends AbstractTool {
    private GeometryObject objectToMirror = null;
    private GeometryObject mirrorObject = null;
    private boolean firstObjectSelected = false;
    private Point previewPoint = null;
    private Line previewLine = null;

    /**
     * Constructor for MirrorTool.
     * @param core The GExpertCore instance
     */
    public MirrorTool(GExpertCore core) {
        super(core, "Mirror", "Create mirror images of objects");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        firstObjectSelected = false;
        objectToMirror = null;
        mirrorObject = null;
        removePreviewObjects();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        firstObjectSelected = false;
        objectToMirror = null;
        mirrorObject = null;
        removePreviewObjects();
    }

    /**
     * Removes any preview objects.
     */
    private void removePreviewObjects() {
        if (previewPoint != null) {
            core.removeGeometryObject(previewPoint);
            previewPoint = null;
        }
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

        if (!firstObjectSelected) {
            // First click - select object to mirror
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj != null && (obj instanceof Point || obj instanceof Line)) {
                objectToMirror = obj;
                firstObjectSelected = true;

                // Update status
                if (ui != null) {
                    String objType = (obj instanceof Point) ? "point" : "line";
                    ui.updateStatus("Selected " + objType + " to mirror. Click on a point or line to use as mirror.");
                }
            } else {
                // Not a valid object, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a point or line to mirror.");
                }
            }
        } else {
            // Second click - select mirror object and create mirror image
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj != null && (obj instanceof Point || obj instanceof Line)) {
                mirrorObject = obj;

                // Create mirror image
                createMirrorImage(objectToMirror, mirrorObject);

                // Reset for next operation
                firstObjectSelected = false;
                objectToMirror = null;
                mirrorObject = null;
            } else {
                // Not a valid object, show error message
                if (ui != null) {
                    ui.updateStatus("Please select a point or line to use as mirror.");
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
            // Show preview of mirror image
            GeometryObject obj = core.findObjectAt(x, y);
            if (obj != null && (obj instanceof Point || obj instanceof Line)) {
                updatePreview(objectToMirror, obj);

                // Show tooltip
                if (ui != null) {
                    String objType = (obj instanceof Point) ? "point" : "line";
                    ui.showTooltip("Click to use this " + objType + " as mirror");
                }
            } else {
                // Remove preview if not over a valid mirror object
                removePreviewObjects();

                // Show tooltip
                if (ui != null) {
                    ui.showTooltip("Select a point or line to use as mirror");
                }
            }
        } else {
            // Show tooltip for first object selection
            if (ui != null) {
                GeometryObject obj = core.findObjectAt(x, y);
                if (obj != null && (obj instanceof Point || obj instanceof Line)) {
                    String objType = (obj instanceof Point) ? "point" : "line";
                    ui.showTooltip("Click to select this " + objType + " to mirror");
                } else {
                    ui.showTooltip("Select a point or line to mirror");
                }
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
     * Updates the preview of the mirror image.
     * @param objectToMirror The object to mirror
     * @param mirrorObject The mirror object (point or line)
     */
    private void updatePreview(GeometryObject objectToMirror, GeometryObject mirrorObject) {
        // Remove old preview objects
        removePreviewObjects();

        // Create preview based on object types
        if (objectToMirror instanceof Point) {
            Point p = (Point) objectToMirror;
            
            if (mirrorObject instanceof Point) {
                // Point reflection through a point
                Point mirrorPoint = (Point) mirrorObject;
                double reflectedX = 2 * mirrorPoint.getX() - p.getX();
                double reflectedY = 2 * mirrorPoint.getY() - p.getY();
                
                // Create preview point
                previewPoint = new Point(reflectedX, reflectedY);
                previewPoint.setColor("#AAAAAA"); // Gray for preview
                core.addGeometryObject(previewPoint);
            } else if (mirrorObject instanceof Line) {
                // Point reflection through a line
                Line mirrorLine = (Line) mirrorObject;
                double[] reflectedPoint = reflectPointThroughLine(
                    p.getX(), p.getY(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                
                // Create preview point
                previewPoint = new Point(reflectedPoint[0], reflectedPoint[1]);
                previewPoint.setColor("#AAAAAA"); // Gray for preview
                core.addGeometryObject(previewPoint);
                
                // Create preview line connecting the point and its reflection
                previewLine = new Line(p.getX(), p.getY(), reflectedPoint[0], reflectedPoint[1]);
                previewLine.setColor("#AAAAAA"); // Gray for preview
                previewLine.setLineWidth(1.0); // Thinner line for preview
                previewLine.setLineStyle(1); // Dashed line for preview
                core.addGeometryObject(previewLine);
            }
        } else if (objectToMirror instanceof Line) {
            Line l = (Line) objectToMirror;
            
            if (mirrorObject instanceof Point) {
                // Line reflection through a point
                Point mirrorPoint = (Point) mirrorObject;
                double reflectedX1 = 2 * mirrorPoint.getX() - l.getX();
                double reflectedY1 = 2 * mirrorPoint.getY() - l.getY();
                double reflectedX2 = 2 * mirrorPoint.getX() - l.getX2();
                double reflectedY2 = 2 * mirrorPoint.getY() - l.getY2();
                
                // Create preview line
                previewLine = new Line(reflectedX1, reflectedY1, reflectedX2, reflectedY2);
                previewLine.setColor("#AAAAAA"); // Gray for preview
                previewLine.setLineWidth(1.0); // Thinner line for preview
                previewLine.setLineStyle(1); // Dashed line for preview
                core.addGeometryObject(previewLine);
            } else if (mirrorObject instanceof Line) {
                // Line reflection through a line
                Line mirrorLine = (Line) mirrorObject;
                double[] reflectedPoint1 = reflectPointThroughLine(
                    l.getX(), l.getY(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                double[] reflectedPoint2 = reflectPointThroughLine(
                    l.getX2(), l.getY2(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                
                // Create preview line
                previewLine = new Line(reflectedPoint1[0], reflectedPoint1[1], reflectedPoint2[0], reflectedPoint2[1]);
                previewLine.setColor("#AAAAAA"); // Gray for preview
                previewLine.setLineWidth(1.0); // Thinner line for preview
                previewLine.setLineStyle(1); // Dashed line for preview
                core.addGeometryObject(previewLine);
            }
        }
        
        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Creates the mirror image of an object.
     * @param objectToMirror The object to mirror
     * @param mirrorObject The mirror object (point or line)
     */
    private void createMirrorImage(GeometryObject objectToMirror, GeometryObject mirrorObject) {
        // Remove preview objects
        removePreviewObjects();
        
        // Create mirror image based on object types
        if (objectToMirror instanceof Point) {
            Point p = (Point) objectToMirror;
            
            if (mirrorObject instanceof Point) {
                // Point reflection through a point
                Point mirrorPoint = (Point) mirrorObject;
                double reflectedX = 2 * mirrorPoint.getX() - p.getX();
                double reflectedY = 2 * mirrorPoint.getY() - p.getY();
                
                // Create reflected point
                Point reflectedPoint = createPoint(reflectedX, reflectedY);
                
                // Select the new point
                core.selectObject(reflectedPoint);
                
                // Update status
                if (ui != null) {
                    ui.updateStatus("Created reflection of point " + p.getName() + 
                        " through point " + mirrorPoint.getName());
                }
            } else if (mirrorObject instanceof Line) {
                // Point reflection through a line
                Line mirrorLine = (Line) mirrorObject;
                double[] reflectedPoint = reflectPointThroughLine(
                    p.getX(), p.getY(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                
                // Create reflected point
                Point reflectedP = createPoint(reflectedPoint[0], reflectedPoint[1]);
                
                // Select the new point
                core.selectObject(reflectedP);
                
                // Update status
                if (ui != null) {
                    ui.updateStatus("Created reflection of point " + p.getName() + 
                        " through line");
                }
            }
        } else if (objectToMirror instanceof Line) {
            Line l = (Line) objectToMirror;
            
            if (mirrorObject instanceof Point) {
                // Line reflection through a point
                Point mirrorPoint = (Point) mirrorObject;
                double reflectedX1 = 2 * mirrorPoint.getX() - l.getX();
                double reflectedY1 = 2 * mirrorPoint.getY() - l.getY();
                double reflectedX2 = 2 * mirrorPoint.getX() - l.getX2();
                double reflectedY2 = 2 * mirrorPoint.getY() - l.getY2();
                
                // Create reflected line
                Line reflectedLine = new Line(reflectedX1, reflectedY1, reflectedX2, reflectedY2);
                reflectedLine.setColor(l.getColor());
                reflectedLine.setLineWidth(l.getLineWidth());
                reflectedLine.setLineStyle(l.getLineStyle());
                core.addGeometryObject(reflectedLine);
                
                // Select the new line
                core.selectObject(reflectedLine);
                
                // Update status
                if (ui != null) {
                    ui.updateStatus("Created reflection of line through point " + 
                        mirrorPoint.getName());
                }
            } else if (mirrorObject instanceof Line) {
                // Line reflection through a line
                Line mirrorLine = (Line) mirrorObject;
                double[] reflectedPoint1 = reflectPointThroughLine(
                    l.getX(), l.getY(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                double[] reflectedPoint2 = reflectPointThroughLine(
                    l.getX2(), l.getY2(), 
                    mirrorLine.getX(), mirrorLine.getY(), 
                    mirrorLine.getX2(), mirrorLine.getY2()
                );
                
                // Create reflected line
                Line reflectedLine = new Line(reflectedPoint1[0], reflectedPoint1[1], reflectedPoint2[0], reflectedPoint2[1]);
                reflectedLine.setColor(l.getColor());
                reflectedLine.setLineWidth(l.getLineWidth());
                reflectedLine.setLineStyle(l.getLineStyle());
                core.addGeometryObject(reflectedLine);
                
                // Select the new line
                core.selectObject(reflectedLine);
                
                // Update status
                if (ui != null) {
                    ui.updateStatus("Created reflection of line through line");
                }
            }
        }
        
        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Reflects a point through a line.
     * @param px The x-coordinate of the point to reflect
     * @param py The y-coordinate of the point to reflect
     * @param lx1 The x-coordinate of the first point on the line
     * @param ly1 The y-coordinate of the first point on the line
     * @param lx2 The x-coordinate of the second point on the line
     * @param ly2 The y-coordinate of the second point on the line
     * @return An array containing the x and y coordinates of the reflected point
     */
    private double[] reflectPointThroughLine(double px, double py, double lx1, double ly1, double lx2, double ly2) {
        // Calculate direction vector of the line
        double dx = lx2 - lx1;
        double dy = ly2 - ly1;
        
        // Normalize direction vector
        double length = Math.sqrt(dx * dx + dy * dy);
        dx /= length;
        dy /= length;
        
        // Calculate normal vector (perpendicular to the line)
        double nx = -dy;
        double ny = dx;
        
        // Calculate the projection of the point onto the line
        double dotProduct = (px - lx1) * dx + (py - ly1) * dy;
        double projX = lx1 + dotProduct * dx;
        double projY = ly1 + dotProduct * dy;
        
        // Calculate the reflection
        double reflectedX = 2 * projX - px;
        double reflectedY = 2 * projY - py;
        
        return new double[] { reflectedX, reflectedY };
    }
}