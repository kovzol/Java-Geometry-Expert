package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that enforces two lines to be parallel.
 */
public class ParallelConstraint implements Constraint {
    private Line line1;
    private Line line2;
    
    /**
     * Creates a new parallel constraint between two lines.
     * 
     * @param line1 The first line
     * @param line2 The second line
     */
    public ParallelConstraint(Line line1, Line line2) {
        this.line1 = line1;
        this.line2 = line2;
    }
    
    @Override
    public String getType() {
        return "PARALLEL";
    }
    
    @Override
    public List<GeometryObject> getObjects() {
        List<GeometryObject> objects = new ArrayList<>();
        objects.add(line1);
        objects.add(line2);
        return objects;
    }
    
    @Override
    public boolean validate() {
        // Calculate direction vectors
        double dx1 = line1.getX2() - line1.getX();
        double dy1 = line1.getY2() - line1.getY();
        double dx2 = line2.getX2() - line2.getX();
        double dy2 = line2.getY2() - line2.getY();
        
        // Calculate cross product to check if lines are parallel
        // If the cross product is close to zero, the lines are parallel
        double crossProduct = dx1 * dy2 - dy1 * dx2;
        
        // Allow for small floating-point errors
        return Math.abs(crossProduct) < 1e-10;
    }
    
    @Override
    public void enforce() {
        if (validate()) {
            // Already parallel, nothing to do
            return;
        }
        
        // Make the second line parallel to the first line
        // We'll keep the start point of the second line fixed and adjust its end point
        
        // Calculate direction vector of the first line
        double dx1 = line1.getX2() - line1.getX();
        double dy1 = line1.getY2() - line1.getY();
        
        // Normalize the direction vector
        double length1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        dx1 /= length1;
        dy1 /= length1;
        
        // Calculate the length of the second line
        double dx2 = line2.getX2() - line2.getX();
        double dy2 = line2.getY2() - line2.getY();
        double length2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        
        // Calculate the new end point for the second line
        double newX2 = line2.getX() + dx1 * length2;
        double newY2 = line2.getY() + dy1 * length2;
        
        // Update the second line
        line2.setEndPoint(newX2, newY2);
    }
    
    @Override
    public String toString() {
        return "Parallel constraint between " + line1.getName() + " and " + line2.getName();
    }
}