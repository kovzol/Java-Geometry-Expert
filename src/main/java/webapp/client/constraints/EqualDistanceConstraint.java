package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that enforces equal distances between two pairs of points.
 */
public class EqualDistanceConstraint implements Constraint {
    private Point point1;
    private Point point2;
    private Point point3;
    private Point point4;
    
    /**
     * Creates a new constraint that enforces the distance between point1 and point2
     * to be equal to the distance between point3 and point4.
     * 
     * @param point1 The first point of the first pair
     * @param point2 The second point of the first pair
     * @param point3 The first point of the second pair
     * @param point4 The second point of the second pair
     */
    public EqualDistanceConstraint(Point point1, Point point2, Point point3, Point point4) {
        this.point1 = point1;
        this.point2 = point2;
        this.point3 = point3;
        this.point4 = point4;
    }
    
    @Override
    public String getType() {
        return "EQUAL_DISTANCE";
    }
    
    @Override
    public List<GeometryObject> getObjects() {
        List<GeometryObject> objects = new ArrayList<>();
        objects.add(point1);
        objects.add(point2);
        objects.add(point3);
        objects.add(point4);
        return objects;
    }
    
    @Override
    public boolean validate() {
        // Calculate the distances
        double distance1 = distanceBetweenPoints(point1, point2);
        double distance2 = distanceBetweenPoints(point3, point4);
        
        // Check if the distances are equal
        double difference = Math.abs(distance1 - distance2);
        
        // Allow for small floating-point errors
        return difference < 1e-10;
    }
    
    @Override
    public void enforce() {
        if (validate()) {
            // Already equal, nothing to do
            return;
        }
        
        // Calculate the distances
        double distance1 = distanceBetweenPoints(point1, point2);
        double distance2 = distanceBetweenPoints(point3, point4);
        
        // Decide which distance to use as the target
        // For simplicity, we'll adjust the second pair to match the first pair
        double targetDistance = distance1;
        
        // Calculate the vector from point3 to point4
        double dx = point4.getX() - point3.getX();
        double dy = point4.getY() - point3.getY();
        
        // Calculate the current distance
        double currentDistance = Math.sqrt(dx * dx + dy * dy);
        
        // If the current distance is very small, we can't normalize the vector
        if (currentDistance < 1e-10) {
            // Move point4 in an arbitrary direction
            point4.move(targetDistance, 0);
            return;
        }
        
        // Normalize the vector
        double nx = dx / currentDistance;
        double ny = dy / currentDistance;
        
        // Calculate the new position of point4
        double newX = point3.getX() + nx * targetDistance;
        double newY = point3.getY() + ny * targetDistance;
        
        // Move point4 to the new position
        point4.move(newX - point4.getX(), newY - point4.getY());
    }
    
    /**
     * Calculates the distance between two points.
     * 
     * @param p1 The first point
     * @param p2 The second point
     * @return The distance between the two points
     */
    private double distanceBetweenPoints(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return "Equal distance constraint: |" + point1.getName() + point2.getName() + "| = |" + 
               point3.getName() + point4.getName() + "|";
    }
}