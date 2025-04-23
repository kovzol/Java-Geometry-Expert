package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Polygon;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI.DrawingCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for creating polygons.
 */
public class PolygonTool extends AbstractTool {
    private List<Point> points = new ArrayList<>();
    private List<Double> xPoints = new ArrayList<>();
    private List<Double> yPoints = new ArrayList<>();
    private Polygon previewPolygon = null; // For backward compatibility
    private double currentX, currentY;
    private boolean isCreating = false;

    /**
     * Constructor for PolygonTool.
     * @param core The GExpertCore instance
     */
    public PolygonTool(GExpertCore core) {
        super(core, "Polygon", "Create a polygon");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        resetTool();
    }

    @Override
    public void deactivate() {
        super.deactivate(); // This will call removePreviewObject()
        resetTool();
    }

    /**
     * Resets the tool state.
     */
    private void resetTool() {
        points.clear();
        xPoints.clear();
        yPoints.clear();
        isCreating = false;
        previewPolygon = null;

        // Remove preview object if it exists
        removePreviewObject();
    }

    @Override
    protected GeometryObject createPreviewObject(double x, double y) {
        if (isCreating && points.size() > 0) {
            // Create arrays for preview polygon
            double[] xArray = new double[points.size() + 1];
            double[] yArray = new double[points.size() + 1];

            // Copy existing points
            for (int i = 0; i < points.size(); i++) {
                xArray[i] = points.get(i).getX();
                yArray[i] = points.get(i).getY();
            }

            // Add current mouse position
            xArray[points.size()] = x;
            yArray[points.size()] = y;

            // Create preview polygon
            Polygon polygon = new Polygon(xArray, yArray, points.size() + 1);
            polygon.setColor("#AAAAAA"); // Gray for preview
            polygon.setLineWidth(1.0); // Thinner line for preview
            polygon.setLineStyle(1); // Dashed line for preview

            return polygon;
        }
        return null;
    }

    @Override
    protected boolean updatePreviewObject(double x, double y) {
        if (isCreating && points.size() > 0 && previewObject instanceof Polygon) {
            // Update the preview polygon with the new mouse position
            Polygon polygon = (Polygon) previewObject;

            // Get the current points
            double[] xArray = polygon.getXPoints();
            double[] yArray = polygon.getYPoints();

            // Update the last point (mouse position)
            if (xArray.length > 0) {
                xArray[xArray.length - 1] = x;
                yArray[yArray.length - 1] = y;
            }

            return true;
        }
        return false;
    }

    @Override
    protected GeometryObject finalizePreviewObject(double x, double y) {
        if (isCreating && points.size() >= 3 && previewObject instanceof Polygon) {
            // Create arrays for final polygon
            double[] xArray = new double[points.size()];
            double[] yArray = new double[points.size()];

            // Copy points
            for (int i = 0; i < points.size(); i++) {
                xArray[i] = points.get(i).getX();
                yArray[i] = points.get(i).getY();
            }

            // Create final polygon
            Polygon polygon = new Polygon(xArray, yArray, points.size());
            polygon.setColor("#FF0000"); // Red for final polygon
            polygon.setLineWidth(2.0); // Default line width
            polygon.setLineStyle(0); // Solid line

            return polygon;
        }
        return null;
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Check if we're closing the polygon
        if (isCreating && points.size() >= 3) {
            // Check if click is near the first point
            Point firstPoint = points.get(0);
            double dx = x - firstPoint.getX();
            double dy = y - firstPoint.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= 10) { // 10 pixels tolerance for closing
                // Close the polygon
                createFinalPolygon();
                return;
            }
        }

        // Check if there's already a point at this location
        GeometryObject existingObject = core.findObjectAt(x, y);
        Point point = null;

        if (existingObject instanceof Point) {
            // Use existing point
            point = (Point) existingObject;
            x = point.getX();
            y = point.getY();

            // Update status
            if (ui != null) {
                ui.updateStatus("Using existing point " + point.getName());
            }
        } else {
            // Create a new point at the clicked location
            point = createPoint(x, y);

            // Update status
            if (ui != null) {
                ui.updateStatus("Created point " + point.getName());
            }
        }

        // Add the point to our list
        points.add(point);
        xPoints.add(x);
        yPoints.add(y);

        // Update status
        if (ui != null) {
            if (points.size() == 1) {
                ui.updateStatus("First point added. Click to add more points. Click near the first point to close the polygon.");
            } else {
                ui.updateStatus("Point added. Click to add more points. Click near the first point to close the polygon.");
            }
        }

        isCreating = true;

        // Create initial preview object
        if (previewObject == null) {
            previewObject = createPreviewObject(x, y);
            if (previewObject != null) {
                // Store a reference to the preview polygon for backward compatibility
                previewPolygon = (Polygon) previewObject;
                // Add the preview object to the core
                core.addGeometryObject(previewObject);

                // Refresh display
                if (ui != null) {
                    ui.refreshDisplay();
                }
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        currentX = x;
        currentY = y;

        if (isCreating) {
            // Remove the old preview object before creating a new one
            removePreviewObject();
            previewPolygon = null;

            // Create a new preview object
            previewObject = createPreviewObject(x, y);
            if (previewObject != null) {
                // Store a reference to the preview polygon for backward compatibility
                previewPolygon = (Polygon) previewObject;
                // Add the preview object to the core
                core.addGeometryObject(previewObject);
            }

            // Always refresh the display to ensure the preview object is visible
            if (ui != null) {
                ui.refreshDisplay();
            }

            // Check if we're near the first point (for closing)
            if (points.size() >= 3) {
                Point firstPoint = points.get(0);
                double dx = x - firstPoint.getX();
                double dy = y - firstPoint.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= 10) { // 10 pixels tolerance
                    if (ui != null) {
                        ui.showTooltip("Click to close polygon");
                    }
                } else {
                    if (ui != null) {
                        ui.showTooltip("Click to add point");
                    }
                }
            } else {
                if (ui != null) {
                    ui.showTooltip("Click to add point");
                }
            }
        } else {
            // Show tooltip for first click
            if (ui != null) {
                ui.showTooltip("Click to set first point");
            }
        }
    }

    /**
     * Creates the final polygon.
     */
    private void createFinalPolygon() {
        if (points.size() >= 3) {
            // Finalize the preview object if it exists, or create a new polygon
            if (previewObject != null) {
                // Finalize the preview object
                GeometryObject finalObject = finalizePreviewObject(currentX, currentY);

                if (finalObject != null) {
                    // Add the finalized polygon to the core
                    core.addGeometryObject(finalObject);

                    // Select the polygon
                    core.selectObject(finalObject);

                    // Update status
                    if (ui != null) {
                        ui.updateStatus("Polygon created with " + points.size() + " points");
                    }
                }
            } else {
                // Create a new polygon if no preview exists
                double[] xArray = new double[points.size()];
                double[] yArray = new double[points.size()];

                // Copy points
                for (int i = 0; i < points.size(); i++) {
                    xArray[i] = points.get(i).getX();
                    yArray[i] = points.get(i).getY();
                }

                // Create final polygon
                Polygon polygon = new Polygon(xArray, yArray, points.size());
                polygon.setColor("#FF0000"); // Red for final polygon
                polygon.setLineWidth(2.0); // Default line width
                polygon.setLineStyle(0); // Solid line
                core.addGeometryObject(polygon);

                // Select the polygon
                core.selectObject(polygon);

                // Update status
                if (ui != null) {
                    ui.updateStatus("Polygon created with " + points.size() + " points");
                }
            }

            // Reset for next polygon
            resetTool();
        }
    }

    @Override
    public void onMouseExit(double x, double y) {
        // Remove preview object if it exists
        removePreviewObject();
        previewPolygon = null;

        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }

    public void onKeyDown(int keyCode, char keyChar) {
        // Handle Escape key to cancel polygon creation
        if (keyCode == 27) { // ESC key
            resetTool();

            // Update status
            if (ui != null) {
                ui.updateStatus("Polygon creation cancelled");
            }
        }
        // Handle Enter key to finish polygon
        else if (keyCode == 13 && isCreating && points.size() >= 3) { // Enter key
            createFinalPolygon();
        }
    }
}
