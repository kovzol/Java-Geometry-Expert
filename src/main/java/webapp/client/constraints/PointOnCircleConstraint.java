package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Circle;
import webapp.client.GExpertCore.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that enforces a point to lie on a circle.
 */
public class PointOnCircleConstraint implements Constraint {
    private Point point;
    private Circle circle;
    
    /**
     * Creates a new constraint that enforces a point to lie on a circle.
     * 
     * @param point The point that should lie on the circle
     * @param circle The circle that the point should lie on
     */
    public PointOnCircleConstraint(Point point, Circle circle) {
        this.point = point;
        this.circle = circle;
    }
    
    @Override
    public String getType() {
        return "POINT_ON_CIRCLE";
    }
    
    @Override
    public List<GeometryObject> getObjects() {
        List<GeometryObject> objects = new ArrayList<>();
        objects.add(point);
        objects.add(circle);
        return objects;
    }
    
    @Override
    public boolean validate() {
        // Calculate the distance from the point to the center of the circle
        double distance = distanceFromPointToPoint(
            point.getX(), point.getY(),
            circle.getX(), circle.getY()
        );
        
        // Check if the distance is equal to the radius
        double difference = Math.abs(distance - circle.getRadius());
        
        // Allow for small floating-point errors
        return difference < 1e-10;
    }
    
    @Override
    public void enforce() {
        if (validate()) {
            // Already on the circle, nothing to do
            return;
        }
        
        // Calculate the vector from the center of the circle to the point
        double dx = point.getX() - circle.getX();
        double dy = point.getY() - circle.getY();
        
        // Calculate the current distance from the point to the center
        double currentDistance = Math.sqrt(dx * dx + dy * dy);
        
        // If the current distance is very small, move the point in a random direction
        if (currentDistance < 1e-10) {
            // Move the point to a position on the circle (arbitrarily choose angle 0)
            point.move(circle.getX() + circle.getRadius() - point.getX(), 
                       circle.getY() - point.getY());
            return;
        }
        
        // Normalize the vector
        double nx = dx / currentDistance;
        double ny = dy / currentDistance;
        
        // Calculate the new position of the point on the circle
        double newX = circle.getX() + nx * circle.getRadius();
        double newY = circle.getY() + ny * circle.getRadius();
        
        // Move the point to the new position
        point.move(newX - point.getX(), newY - point.getY());
    }
    
    /**
     * Calculates the distance between two points.
     * 
     * @param x1 The x-coordinate of the first point
     * @param y1 The y-coordinate of the first point
     * @param x2 The x-coordinate of the second point
     * @param y2 The y-coordinate of the second point
     * @return The distance between the two points
     */
    private double distanceFromPointToPoint(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return "Point " + point.getName() + " on circle " + circle.getName();
    }
}