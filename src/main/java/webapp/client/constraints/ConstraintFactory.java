package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.Circle;

/**
 * Factory class for creating constraints.
 */
public class ConstraintFactory {
    
    /**
     * Creates a constraint that enforces two lines to be parallel.
     * 
     * @param line1 The first line
     * @param line2 The second line
     * @return A parallel constraint
     */
    public static Constraint createParallelConstraint(Line line1, Line line2) {
        return new ParallelConstraint(line1, line2);
    }
    
    /**
     * Creates a constraint that enforces two lines to be perpendicular.
     * 
     * @param line1 The first line
     * @param line2 The second line
     * @return A perpendicular constraint
     */
    public static Constraint createPerpendicularConstraint(Line line1, Line line2) {
        return new PerpendicularConstraint(line1, line2);
    }
    
    /**
     * Creates a constraint that enforces a point to lie on a line.
     * 
     * @param point The point
     * @param line The line
     * @return A point-on-line constraint
     */
    public static Constraint createPointOnLineConstraint(Point point, Line line) {
        return new PointOnLineConstraint(point, line);
    }
    
    /**
     * Creates a constraint that enforces a point to lie on a circle.
     * 
     * @param point The point
     * @param circle The circle
     * @return A point-on-circle constraint
     */
    public static Constraint createPointOnCircleConstraint(Point point, Circle circle) {
        return new PointOnCircleConstraint(point, circle);
    }
    
    /**
     * Creates a constraint that enforces equal distances between two pairs of points.
     * 
     * @param point1 The first point of the first pair
     * @param point2 The second point of the first pair
     * @param point3 The first point of the second pair
     * @param point4 The second point of the second pair
     * @return An equal-distance constraint
     */
    public static Constraint createEqualDistanceConstraint(Point point1, Point point2, Point point3, Point point4) {
        return new EqualDistanceConstraint(point1, point2, point3, point4);
    }
    
    /**
     * Creates a constraint based on the constraint type and the objects involved.
     * 
     * @param type The type of constraint
     * @param objects The objects involved in the constraint
     * @return A constraint of the specified type, or null if the type is not supported
     */
    public static Constraint createConstraint(String type, GeometryObject... objects) {
        switch (type) {
            case "PARALLEL":
                if (objects.length == 2 && objects[0] instanceof Line && objects[1] instanceof Line) {
                    return createParallelConstraint((Line) objects[0], (Line) objects[1]);
                }
                break;
                
            case "PERPENDICULAR":
                if (objects.length == 2 && objects[0] instanceof Line && objects[1] instanceof Line) {
                    return createPerpendicularConstraint((Line) objects[0], (Line) objects[1]);
                }
                break;
                
            case "POINT_ON_LINE":
                if (objects.length == 2 && objects[0] instanceof Point && objects[1] instanceof Line) {
                    return createPointOnLineConstraint((Point) objects[0], (Line) objects[1]);
                }
                break;
                
            case "POINT_ON_CIRCLE":
                if (objects.length == 2 && objects[0] instanceof Point && objects[1] instanceof Circle) {
                    return createPointOnCircleConstraint((Point) objects[0], (Circle) objects[1]);
                }
                break;
                
            case "EQUAL_DISTANCE":
                if (objects.length == 4 && objects[0] instanceof Point && objects[1] instanceof Point &&
                    objects[2] instanceof Point && objects[3] instanceof Point) {
                    return createEqualDistanceConstraint((Point) objects[0], (Point) objects[1], 
                                                        (Point) objects[2], (Point) objects[3]);
                }
                break;
        }
        
        return null;
    }
}