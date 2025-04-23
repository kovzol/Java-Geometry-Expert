package webapp.client;

import java.util.ArrayList;
import java.util.List;
import webapp.client.constraints.ConstraintManager;

/**
 * GWT-compatible version of the GExpertCore class.
 * This is a simplified version that doesn't use Java I/O operations
 * or other features that are not supported in GWT.
 */
public class GExpertCore {
    private String currentFileName;
    private boolean modified = false;
    private List<GeometryObject> geometryObjects = new ArrayList<>();

    // Selection state
    private GeometryObject selectedObject = null;
    private GeometryObject highlightedObject = null;
    private double selectionTolerance = 5.0; // Tolerance for selection in pixels

    // UI reference
    private core.ui.GExpertUI ui;

    // Constraint manager
    private ConstraintManager constraintManager;

    public GExpertCore() {
        constraintManager = new ConstraintManager(this);
    }

    /**
     * Sets the UI implementation.
     * @param ui The UI implementation
     */
    public void setUI(core.ui.GExpertUI ui) {
        this.ui = ui;
    }

    /**
     * Gets the UI implementation.
     * @return The UI implementation
     */
    public core.ui.GExpertUI getUI() {
        return ui;
    }

    /**
     * Interface for geometric constraints.
     * Constraints enforce relationships between geometry objects.
     */
    public interface Constraint {
        /**
         * Gets the type of constraint.
         * @return The constraint type
         */
        String getType();

        /**
         * Gets the objects involved in this constraint.
         * @return List of geometry objects
         */
        List<GeometryObject> getObjects();

        /**
         * Validates if the constraint is currently satisfied.
         * @return True if the constraint is satisfied, false otherwise
         */
        boolean validate();

        /**
         * Enforces the constraint by adjusting the objects.
         */
        void enforce();
    }

    // Initialization
    public void init() {
        loadPreference();
        loadLanguage();
        loadRules();
        System.out.println("GExpertCore initialized.");
    }

    /**
     * Adds a constraint to the system.
     * 
     * @param constraint The constraint to add
     */
    public void addConstraint(Constraint constraint) {
        constraintManager.addConstraint(constraint);
        setModified(true);
    }

    /**
     * Removes a constraint from the system.
     * 
     * @param constraint The constraint to remove
     */
    public void removeConstraint(Constraint constraint) {
        constraintManager.removeConstraint(constraint);
        setModified(true);
    }

    /**
     * Gets all constraints in the system.
     * 
     * @return A list of all constraints
     */
    public List<Constraint> getConstraints() {
        return constraintManager.getConstraints();
    }

    /**
     * Gets all constraints for a specific object.
     * 
     * @param obj The object
     * @return A list of constraints for the object
     */
    public List<Constraint> getConstraintsForObject(GeometryObject obj) {
        return constraintManager.getConstraintsForObject(obj);
    }

    /**
     * Enforces all constraints in the system.
     */
    public void enforceAllConstraints() {
        constraintManager.enforceAllConstraints();
    }

    /**
     * Enforces constraints for a specific object.
     * 
     * @param obj The object
     */
    public void enforceConstraintsForObject(GeometryObject obj) {
        constraintManager.enforceConstraintsForObject(obj);
    }

    /**
     * Clears all constraints in the system.
     */
    public void clearAllConstraints() {
        constraintManager.clearAllConstraints();
        setModified(true);
    }

    // Abstract class for geometry objects
    public static abstract class GeometryObject {
        protected double x, y;
        protected String name;
        protected boolean visible = true;

        // Style properties
        protected String color = "#000000";
        protected double lineWidth = 2.0; // Increased line width for better visibility
        protected String fillColor = null; // null means no fill
        protected int lineStyle = 0; // 0 = solid, 1 = dashed, 2 = dotted

        // Selection and highlighting state
        protected boolean selected = false;
        protected boolean highlighted = false;

        // Constraint references
        protected List<Constraint> constraints = new ArrayList<>();

        // Unique identifier
        protected String id;
        private static int nextId = 1;

        public GeometryObject(double x, double y) {
            this.x = x;
            this.y = y;
            this.id = generateId();
        }

        private String generateId() {
            return getClass().getSimpleName() + "_" + nextId++;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public String getId() {
            return id;
        }

        // Style methods
        public void setColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public void setLineWidth(double width) {
            this.lineWidth = width;
        }

        public double getLineWidth() {
            return lineWidth;
        }

        public void setFillColor(String fillColor) {
            this.fillColor = fillColor;
        }

        public String getFillColor() {
            return fillColor;
        }

        public void setLineStyle(int style) {
            this.lineStyle = style;
        }

        public int getLineStyle() {
            return lineStyle;
        }

        // Selection and highlighting methods
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setHighlighted(boolean highlighted) {
            this.highlighted = highlighted;
        }

        public boolean isHighlighted() {
            return highlighted;
        }

        // Constraint methods
        public void addConstraint(Constraint constraint) {
            constraints.add(constraint);
        }

        public void removeConstraint(Constraint constraint) {
            constraints.remove(constraint);
        }

        public List<Constraint> getConstraints() {
            return constraints;
        }

        // Object manipulation methods
        public void move(double dx, double dy) {
            x += dx;
            y += dy;
        }

        // Hit testing
        public abstract boolean containsPoint(double px, double py, double tolerance);

        // Drawing
        public abstract void draw(DrawingContext context);

        // Apply drawing style to context
        protected void applyStyle(DrawingContext context) {
            // Set color based on selection/highlight state
            if (selected) {
                context.setDrawingColor("#0000FF"); // Blue for selected
                context.setLineWidth((float)(lineWidth + 1.0));
            } else if (highlighted) {
                context.setDrawingColor("#FF0000"); // Red for highlighted
                context.setLineWidth((float)lineWidth);
            } else {
                context.setDrawingColor(color);
                context.setLineWidth((float)lineWidth);
            }

            // Set line style
            // Note: This would need to be implemented in the DrawingContext interface
            // For now, we'll just use solid lines
        }
    }

    // Point geometry object
    public static class Point extends GeometryObject {
        public Point(double x, double y) {
            super(x, y);
            // setColor("#FF0000"); // Red for points
            // setName("A"); // FIXME should work through pointTool
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);
            context.drawPoint(x, y);
            if (name != null && !name.isEmpty()) {
                // Adjust the offset as needed.
                int offsetX = 5;
                int offsetY = -5;

                // If context supports drawing text, use it.
                if (context instanceof GwtGExpertUI.DrawingCanvas) {
                    ((GwtGExpertUI.DrawingCanvas) context).drawText(name, x + offsetX, y + offsetY);
                } else {
                    // Call the drawText method directly
                    context.drawText(name, x + offsetX, y + offsetY);
                }
            }

        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Calculate distance from point to this point
            double dx = px - x;
            double dy = py - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Check if distance is within tolerance
            return distance <= tolerance;
        }
    }

    // Line geometry object
    public static class Line extends GeometryObject {
        private double x2, y2;

        public Line(double x1, double y1, double x2, double y2) {
            super(x1, y1);
            this.x2 = x2;
            this.y2 = y2;
            setColor("#FF0000"); // Red for lines
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }

        public void setY2(double y2) {
            this.y2 = y2;
        }

        public void setEndPoint(double x2, double y2) {
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);
            context.drawLine(x, y, x2, y2);
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Calculate distance from point to line segment
            double dx = x2 - x;
            double dy = y2 - y;
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length == 0) {
                // Point-to-point distance if segment has zero length
                double dist = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y));
                return dist <= tolerance;
            }

            // Calculate projection of point onto line
            double t = ((px - x) * dx + (py - y) * dy) / (length * length);

            if (t < 0) {
                // Point is beyond the start point
                double dist = Math.sqrt((px - x) * (px - x) + (py - y) * (py - y));
                return dist <= tolerance;
            } else if (t > 1) {
                // Point is beyond the end point
                double dist = Math.sqrt((px - x2) * (px - x2) + (py - y2) * (py - y2));
                return dist <= tolerance;
            } else {
                // Point projects onto the segment
                double projX = x + t * dx;
                double projY = y + t * dy;
                double dist = Math.sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY));
                return dist <= tolerance;
            }
        }

        @Override
        public void move(double dx, double dy) {
            super.move(dx, dy);
            x2 += dx;
            y2 += dy;
        }
    }

    // Circle geometry object
    public static class Circle extends GeometryObject {
        private double radius;

        public Circle(double x, double y, double radius) {
            super(x, y);
            this.radius = radius;
            setColor("#FF0000"); // Red for circles
        }

        public double getRadius() {
            return radius;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);
            context.drawCircle(x, y, radius);
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Calculate distance from point to center
            double dx = px - x;
            double dy = py - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Check if distance is within tolerance of the circle's circumference
            return Math.abs(distance - radius) <= tolerance;
        }
    }

    // Segment geometry object (finite line)
    public static class Segment extends Line {
        public Segment(double x1, double y1, double x2, double y2) {
            super(x1, y1, x2, y2);
        }

        // Segment uses the same drawing and hit testing as Line
    }

    // Ray geometry object (semi-infinite line)
    public static class Ray extends GeometryObject {
        private double dx, dy; // Direction vector

        public Ray(double x, double y, double dirX, double dirY) {
            super(x, y);
            // Normalize direction vector
            double length = Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                this.dx = dirX / length;
                this.dy = dirY / length;
            } else {
                this.dx = 1.0; // Default to horizontal ray
                this.dy = 0.0;
            }
        }

        public double getDirX() {
            return dx;
        }

        public double getDirY() {
            return dy;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);

            // Calculate end point far enough to appear infinite
            double length = 1000.0; // Large enough for most displays
            double x2 = x + dx * length;
            double y2 = y + dy * length;

            context.drawLine(x, y, x2, y2);
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Vector from ray origin to point
            double vx = px - x;
            double vy = py - y;

            // Project this vector onto the ray direction
            double projection = vx * dx + vy * dy;

            // Point must be in front of ray (projection > 0)
            if (projection < 0) {
                return false;
            }

            // Calculate distance from point to ray
            double distX = vx - projection * dx;
            double distY = vy - projection * dy;
            double distance = Math.sqrt(distX * distX + distY * distY);

            return distance <= tolerance;
        }

        @Override
        public void move(double dx, double dy) {
            super.move(dx, dy);
            // Direction doesn't change when moving
        }
    }

    // Polygon geometry object
    public static class Polygon extends GeometryObject {
        private double[] xPoints;
        private double[] yPoints;
        private int nPoints;

        public Polygon(double[] xPoints, double[] yPoints, int nPoints) {
            super(xPoints[0], yPoints[0]); // Use first point as reference
            // Create new arrays and copy values (GWT-compatible)
            this.xPoints = new double[xPoints.length];
            this.yPoints = new double[yPoints.length];
            for (int i = 0; i < nPoints; i++) {
                this.xPoints[i] = xPoints[i];
                this.yPoints[i] = yPoints[i];
            }
            this.nPoints = nPoints;
        }

        public double[] getXPoints() {
            // Create a new array and copy values (GWT-compatible)
            double[] copy = new double[xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                copy[i] = xPoints[i];
            }
            return copy;
        }

        public double[] getYPoints() {
            // Create a new array and copy values (GWT-compatible)
            double[] copy = new double[yPoints.length];
            for (int i = 0; i < yPoints.length; i++) {
                copy[i] = yPoints[i];
            }
            return copy;
        }

        public int getPointCount() {
            return nPoints;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);

            if (context instanceof GwtGExpertUI.DrawingCanvas) {
                GwtGExpertUI.DrawingCanvas canvas = (GwtGExpertUI.DrawingCanvas) context;
                boolean filled = fillColor != null;
                canvas.drawPolygon(xPoints, yPoints, nPoints, filled);
            } else {
                // Fallback to drawing lines
                for (int i = 0; i < nPoints; i++) {
                    int next = (i + 1) % nPoints;
                    context.drawLine(xPoints[i], yPoints[i], xPoints[next], yPoints[next]);
                }
            }
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // First check if point is close to any edge
            for (int i = 0; i < nPoints; i++) {
                int next = (i + 1) % nPoints;

                // Create a temporary line segment for this edge
                Line edge = new Line(xPoints[i], yPoints[i], xPoints[next], yPoints[next]);
                if (edge.containsPoint(px, py, tolerance)) {
                    return true;
                }
            }

            // If not on edge, check if point is inside polygon using ray casting algorithm
            boolean inside = false;
            for (int i = 0, j = nPoints - 1; i < nPoints; j = i++) {
                if (((yPoints[i] > py) != (yPoints[j] > py)) &&
                    (px < (xPoints[j] - xPoints[i]) * (py - yPoints[i]) / (yPoints[j] - yPoints[i]) + xPoints[i])) {
                    inside = !inside;
                }
            }

            return inside;
        }

        @Override
        public void move(double dx, double dy) {
            super.move(dx, dy);

            // Move all points
            for (int i = 0; i < nPoints; i++) {
                xPoints[i] += dx;
                yPoints[i] += dy;
            }
        }
    }

    // Angle geometry object
    public static class Angle extends GeometryObject {
        private double startAngle; // In radians
        private double arcAngle;   // In radians
        private double radius;

        public Angle(double x, double y, double startAngle, double arcAngle, double radius) {
            super(x, y);
            this.startAngle = startAngle;
            this.arcAngle = arcAngle;
            this.radius = radius;
        }

        public double getStartAngle() {
            return startAngle;
        }

        public double getArcAngle() {
            return arcAngle;
        }

        public double getRadius() {
            return radius;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);

            if (context instanceof GwtGExpertUI.DrawingCanvas) {
                GwtGExpertUI.DrawingCanvas canvas = (GwtGExpertUI.DrawingCanvas) context;
                boolean filled = fillColor != null;
                canvas.drawArc(x, y, radius, startAngle, arcAngle, filled);

                // Draw angle label (if name is set)
                if (name != null && !name.isEmpty()) {
                    // Position the label at the middle of the arc
                    double labelAngle = startAngle + arcAngle / 2;
                    double labelX = x + Math.cos(labelAngle) * (radius * 0.7);
                    double labelY = y + Math.sin(labelAngle) * (radius * 0.7);
                    canvas.drawText(name, labelX, labelY);
                }
            } else {
                // Fallback for simple drawing contexts
                // Draw an approximation of the arc using line segments
                int segments = 16;
                double angleStep = arcAngle / segments;

                for (int i = 0; i < segments; i++) {
                    double angle1 = startAngle + i * angleStep;
                    double angle2 = startAngle + (i + 1) * angleStep;

                    double x1 = x + Math.cos(angle1) * radius;
                    double y1 = y + Math.sin(angle1) * radius;
                    double x2 = x + Math.cos(angle2) * radius;
                    double y2 = y + Math.sin(angle2) * radius;

                    context.drawLine(x1, y1, x2, y2);
                }
            }
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Vector from center to point
            double dx = px - x;
            double dy = py - y;

            // Distance from center
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Check if distance is within tolerance of the arc radius
            if (Math.abs(distance - radius) > tolerance) {
                return false;
            }

            // Calculate angle of the point
            double angle = Math.atan2(dy, dx);
            if (angle < 0) {
                angle += 2 * Math.PI; // Convert to [0, 2π]
            }

            // Normalize startAngle to [0, 2π]
            double start = startAngle;
            while (start < 0) start += 2 * Math.PI;
            while (start >= 2 * Math.PI) start -= 2 * Math.PI;

            // Calculate end angle
            double end = start + arcAngle;

            // Check if angle is within the arc
            if (arcAngle >= 0) {
                // For positive arc angles
                return (angle >= start && angle <= end) || 
                       (end > 2 * Math.PI && angle <= end - 2 * Math.PI);
            } else {
                // For negative arc angles
                return (angle <= start && angle >= end) || 
                       (end < 0 && angle >= end + 2 * Math.PI);
            }
        }
    }

    // Text label geometry object
    public static class Text extends GeometryObject {
        private String text;
        private double fontSize;
        private boolean bold;
        private boolean italic;

        public Text(double x, double y, String text) {
            super(x, y);
            this.text = text;
            this.fontSize = 12.0;
            this.bold = false;
            this.italic = false;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public double getFontSize() {
            return fontSize;
        }

        public void setFontSize(double fontSize) {
            this.fontSize = fontSize;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isItalic() {
            return italic;
        }

        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        @Override
        public void draw(DrawingContext context) {
            applyStyle(context);

            if (context instanceof GwtGExpertUI.DrawingCanvas) {
                GwtGExpertUI.DrawingCanvas canvas = (GwtGExpertUI.DrawingCanvas) context;
                canvas.setFont("Arial", (int)fontSize, bold, italic);
                canvas.drawText(text, x, y);
            }
            // No fallback for simple drawing contexts as they don't support text
        }

        @Override
        public boolean containsPoint(double px, double py, double tolerance) {
            // Simple rectangular hit testing based on approximate text dimensions
            // This is a rough approximation as actual text dimensions depend on the font
            double textWidth = text.length() * fontSize * 0.6; // Approximate width
            double textHeight = fontSize;

            // Check if point is within the text rectangle
            return px >= x - tolerance && px <= x + textWidth + tolerance &&
                   py >= y - textHeight - tolerance && py <= y + tolerance;
        }
    }

    // Drawing context interface
    public interface DrawingContext {
        void drawPoint(double x, double y);
        void drawLine(double x1, double y1, double x2, double y2);
        void drawCircle(double x, double y, double radius);
        void setDrawingColor(String colorHex);
        void setLineWidth(float width);

        void drawText(String text, double x, double y);
    }

    // Geometry operations
    public void addGeometryObject(GeometryObject object) {
        geometryObjects.add(object);
        setModified(true);
        System.out.println("Added " + object.getClass().getSimpleName());
    }

    public void removeGeometryObject(GeometryObject object) {
        boolean removed = geometryObjects.remove(object);
        if (!removed) {
            System.out.println("Warning: Failed to remove preview object from core: " + object);
        } else {
            System.out.println("Removed preview object: " + object);
        }
        // Update selection if the removed object was selected
        if (object == selectedObject) {
            selectObject(null);
        }

        // Update highlight if the removed object was highlighted
        if (object == highlightedObject) {
            highlightObject(null);
        }

        setModified(true);
        System.out.println("Removed " + object.getClass().getSimpleName());

        // Always refresh the display after removing an object
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    public List<GeometryObject> getGeometryObjects() {
        return geometryObjects;
    }

    public void drawAll(DrawingContext context) {
        for (GeometryObject obj : geometryObjects) {
            if (obj.isVisible()) {
                obj.draw(context);
            }
        }
    }

    /**
     * Finds a geometry object at the specified point.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The geometry object at the point, or null if none found
     */
    public GeometryObject findObjectAt(double x, double y) {
        // Search in reverse order (top to bottom)
        for (int i = geometryObjects.size() - 1; i >= 0; i--) {
            GeometryObject obj = geometryObjects.get(i);
            if (obj.isVisible() && obj.containsPoint(x, y, selectionTolerance)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Selects a geometry object.
     * @param object The object to select, or null to deselect
     */
    public void selectObject(GeometryObject object) {
        // Deselect current selection
        if (selectedObject != null) {
            selectedObject.setSelected(false);

            // Update UI if available
            if (ui != null && selectedObject.getId() != null) {
                ui.selectObject(null);
            }
        }

        // Select new object
        selectedObject = object;
        if (selectedObject != null) {
            selectedObject.setSelected(true);

            // Update UI if available
            if (ui != null && selectedObject.getId() != null) {
                ui.selectObject(selectedObject.getId());
            }
        }

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Gets the currently selected object.
     * @return The selected object, or null if none selected
     */
    public GeometryObject getSelectedObject() {
        return selectedObject;
    }

    /**
     * Highlights a geometry object.
     * @param object The object to highlight, or null to remove highlight
     */
    public void highlightObject(GeometryObject object) {
        // Remove current highlight
        if (highlightedObject != null) {
            highlightedObject.setHighlighted(false);

            // Update UI if available
            if (ui != null && highlightedObject.getId() != null) {
                ui.highlightObject(null);
            }
        }

        // Set new highlight
        highlightedObject = object;
        if (highlightedObject != null) {
            highlightedObject.setHighlighted(true);

            // Update UI if available
            if (ui != null && highlightedObject.getId() != null) {
                ui.highlightObject(highlightedObject.getId());
            }
        }

        // Refresh display
        if (ui != null) {
            ui.refreshDisplay();
        }
    }

    /**
     * Gets the currently highlighted object.
     * @return The highlighted object, or null if none highlighted
     */
    public GeometryObject getHighlightedObject() {
        return highlightedObject;
    }

    /**
     * Moves the selected object by the specified amount.
     * @param dx The x-distance to move
     * @param dy The y-distance to move
     */
    public void moveSelectedObject(double dx, double dy) {
        moveSelectedObject(dx, dy, false);
    }

    /**
     * Moves the selected object by the specified amount.
     * @param dx The x-distance to move
     * @param dy The y-distance to move
     * @param isDragging Whether this is a temporary move during dragging
     */
    public void moveSelectedObject(double dx, double dy, boolean isDragging) {
        if (selectedObject != null) {
            selectedObject.move(dx, dy);

            // Enforce constraints if any
            enforceConstraints();

            // Only mark as modified if this is a final move (not during dragging)
            if (!isDragging) {
                setModified(true);
            }

            // Refresh display
            if (ui != null) {
                ui.refreshDisplay();
            }
        }
    }

    /**
     * Enforces all constraints in the system.
     * This method delegates to the constraint manager.
     */
    private void enforceConstraints() {
        constraintManager.enforceAllConstraints();
    }

    /**
     * Sets the selection tolerance.
     * @param tolerance The selection tolerance in pixels
     */
    public void setSelectionTolerance(double tolerance) {
        this.selectionTolerance = tolerance;
    }

    /**
     * Gets the selection tolerance.
     * @return The selection tolerance in pixels
     */
    public double getSelectionTolerance() {
        return selectionTolerance;
    }

    // File operations (simplified for GWT)
    public void loadFile(String file) {
        this.currentFileName = file;
        setModified(false);
        System.out.println("Loaded file: " + file + " (simulated)");
    }

    public void saveFile(String file) {
        this.currentFileName = file;
        setModified(false);
        System.out.println("Saved file: " + file + " (simulated)");
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    // State management
    public void setModified(boolean modified) {
        this.modified = modified;
        updateTitle();
    }

    public boolean isModified() {
        return modified;
    }

    // Updates the title based on the current file name and modified state
    public void updateTitle() {
        String title = "Java Geometry Expert";
        if (currentFileName != null) {
            title += " - " + currentFileName;
            if (modified) {
                title += " *";
            }
        }
        System.out.println("Title: " + title);
    }

    // Serializes the current geometry state to a string
    public String serializeState() {
        StringBuilder sb = new StringBuilder();
        sb.append("JGEX_FILE_FORMAT_1.0\n");

        // Serialize geometry objects
        for (GeometryObject obj : geometryObjects) {
            if (obj instanceof Point) {
                Point p = (Point) obj;
                sb.append("POINT,").append(p.getX()).append(",").append(p.getY());
                if (p.getName() != null) {
                    sb.append(",").append(p.getName());
                }
                sb.append("\n");
            } else if (obj instanceof Line) {
                Line l = (Line) obj;
                sb.append("LINE,").append(l.getX()).append(",").append(l.getY())
                  .append(",").append(l.getX2()).append(",").append(l.getY2());
                if (l.getName() != null) {
                    sb.append(",").append(l.getName());
                }
                sb.append("\n");
            } else if (obj instanceof Circle) {
                Circle c = (Circle) obj;
                sb.append("CIRCLE,").append(c.getX()).append(",").append(c.getY())
                  .append(",").append(c.getRadius());
                if (c.getName() != null) {
                    sb.append(",").append(c.getName());
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Deserializes a string into geometry state
    public void deserializeState(String content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        // Clear existing objects
        geometryObjects.clear();

        // GWT-compatible string splitting
        List<String> lines = new ArrayList<>();
        int start = 0;
        int end = content.indexOf('\n');
        while (end >= 0) {
            lines.add(content.substring(start, end));
            start = end + 1;
            end = content.indexOf('\n', start);
        }
        if (start < content.length()) {
            lines.add(content.substring(start));
        }

        if (lines.size() > 0) {
            String firstLine = lines.get(0);
            // GWT-compatible startsWith check
            if (firstLine.length() >= 16 && firstLine.substring(0, 16).equals("JGEX_FILE_FORMAT")) {
                // Skip the header line
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isEmpty()) continue;

                    // GWT-compatible string splitting
                    List<String> parts = new ArrayList<>();
                    int partStart = 0;
                    int partEnd = line.indexOf(',');
                    while (partEnd >= 0) {
                        parts.add(line.substring(partStart, partEnd));
                        partStart = partEnd + 1;
                        partEnd = line.indexOf(',', partStart);
                    }
                    if (partStart < line.length()) {
                        parts.add(line.substring(partStart));
                    }

                    if (parts.isEmpty()) continue;

                    String type = parts.get(0);
                    if ("POINT".equals(type) && parts.size() >= 3) {
                        try {
                            double x = Double.parseDouble(parts.get(1));
                            double y = Double.parseDouble(parts.get(2));
                            Point p = new Point(x, y);
                            if (parts.size() > 3) {
                                p.setName(parts.get(3));
                            }
                            geometryObjects.add(p);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing point: " + line);
                        }
                    } else if ("LINE".equals(type) && parts.size() >= 5) {
                        try {
                            double x1 = Double.parseDouble(parts.get(1));
                            double y1 = Double.parseDouble(parts.get(2));
                            double x2 = Double.parseDouble(parts.get(3));
                            double y2 = Double.parseDouble(parts.get(4));
                            Line l = new Line(x1, y1, x2, y2);
                            if (parts.size() > 5) {
                                l.setName(parts.get(5));
                            }
                            geometryObjects.add(l);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing line: " + line);
                        }
                    } else if ("CIRCLE".equals(type) && parts.size() >= 4) {
                        try {
                            double x = Double.parseDouble(parts.get(1));
                            double y = Double.parseDouble(parts.get(2));
                            double radius = Double.parseDouble(parts.get(3));
                            Circle c = new Circle(x, y, radius);
                            if (parts.size() > 4) {
                                c.setName(parts.get(4));
                            }
                            geometryObjects.add(c);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing circle: " + line);
                        }
                    }
                }
            }
        }

        setModified(false);
        System.out.println("Loaded " + geometryObjects.size() + " objects");
    }

    // Clears the current project state
    public void clear() {
        currentFileName = null;
        geometryObjects.clear();
        setModified(false);
        System.out.println("Cleared current state.");
    }

    // Placeholder methods for initialization
    public void loadPreference() {
        System.out.println("Preferences loaded.");
    }

    public void loadLanguage() {
        System.out.println("Language loaded.");
    }

    public void loadRules() {
        System.out.println("Rules loaded.");
    }
}
