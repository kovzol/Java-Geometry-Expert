package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that enforces a point to lie on a line.
 */
public class PointOnLineConstraint implements Constraint {
    private Point point;
    private Line line;
    
    /**
     * Creates a new constraint that enforces a point to lie on a line.
     * 
     * @param point The point that should lie on the line
     * @param line The line that the point should lie on
     */
    public PointOnLineConstraint(Point point, Line line) {
        this.point = point;
        this.line = line;
    }
    
    @Override
    public String getType() {
        return "POINT_ON_LINE";
    }
    
    @Override
    public List<GeometryObject> getObjects() {
        List<GeometryObject> objects = new ArrayList<>();
        objects.add(point);
        objects.add(line);
        return objects;
    }
    
    @Override
    public boolean validate() {
        // Calculate the distance from the point to the line
        double distance = distanceFromPointToLine(
            point.getX(), point.getY(),
            line.getX(), line.getY(),
            line.getX2(), line.getY2()
        );
        
        // Allow for small floating-point errors
        return distance < 1e-10;
    }
    
    @Override
    public void enforce() {
        if (validate()) {
            // Already on the line, nothing to do
            return;
        }
        
        // Project the point onto the line
        double[] projection = projectPointOntoLine(
            point.getX(), point.getY(),
            line.getX(), line.getY(),
            line.getX2(), line.getY2()
        );
        
        // Move the point to the projection
        point.move(projection[0] - point.getX(), projection[1] - point.getY());
    }
    
    /**
     * Calculates the distance from a point to a line.
     * 
     * @param px The x-coordinate of the point
     * @param py The y-coordinate of the point
     * @param x1 The x-coordinate of the first point on the line
     * @param y1 The y-coordinate of the first point on the line
     * @param x2 The x-coordinate of the second point on the line
     * @param y2 The y-coordinate of the second point on the line
     * @return The distance from the point to the line
     */
    private double distanceFromPointToLine(double px, double py, double x1, double y1, double x2, double y2) {
        // Calculate the length of the line
        double lineLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        
        // If the line has zero length, return the distance to the point
        if (lineLength < 1e-10) {
            return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        }
        
        // Calculate the distance using the formula: d = |cross product| / |line length|
        double crossProduct = Math.abs((x2 - x1) * (y1 - py) - (x1 - px) * (y2 - y1));
        return crossProduct / lineLength;
    }
    
    /**
     * Projects a point onto a line.
     * 
     * @param px The x-coordinate of the point
     * @param py The y-coordinate of the point
     * @param x1 The x-coordinate of the first point on the line
     * @param y1 The y-coordinate of the first point on the line
     * @param x2 The x-coordinate of the second point on the line
     * @param y2 The y-coordinate of the second point on the line
     * @return An array containing the x and y coordinates of the projection
     */
    private double[] projectPointOntoLine(double px, double py, double x1, double y1, double x2, double y2) {
        // Calculate direction vector of the line
        double dx = x2 - x1;
        double dy = y2 - y1;
        
        // Calculate the squared length of the line
        double lengthSquared = dx * dx + dy * dy;
        
        // If the line has zero length, return the first point
        if (lengthSquared < 1e-10) {
            return new double[] { x1, y1 };
        }
        
        // Calculate the projection parameter
        double t = ((px - x1) * dx + (py - y1) * dy) / lengthSquared;
        
        // Calculate the projection coordinates
        double projX = x1 + t * dx;
        double projY = y1 + t * dy;
        
        return new double[] { projX, projY };
    }
    
    @Override
    public String toString() {
        return "Point " + point.getName() + " on line " + line.getName();
    }
}