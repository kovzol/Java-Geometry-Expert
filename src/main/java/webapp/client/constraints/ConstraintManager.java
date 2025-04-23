package webapp.client.constraints;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for handling constraints.
 * This class provides methods for adding, removing, and enforcing constraints.
 */
public class ConstraintManager {
    private List<Constraint> constraints = new ArrayList<>();
    private Map<GeometryObject, List<Constraint>> objectConstraints = new HashMap<>();
    private GExpertCore core;
    
    /**
     * Creates a new constraint manager.
     * 
     * @param core The GExpertCore instance
     */
    public ConstraintManager(GExpertCore core) {
        this.core = core;
    }
    
    /**
     * Adds a constraint to the manager.
     * 
     * @param constraint The constraint to add
     */
    public void addConstraint(Constraint constraint) {
        // Add the constraint to the list
        constraints.add(constraint);
        
        // Add the constraint to each object's constraint list
        for (GeometryObject obj : constraint.getObjects()) {
            List<Constraint> objConstraints = objectConstraints.computeIfAbsent(obj, k -> new ArrayList<>());
            objConstraints.add(constraint);
            obj.addConstraint(constraint);
        }
        
        // Enforce the constraint
        constraint.enforce();
        
        // Refresh the display
        if (core.getUI() != null) {
            core.getUI().refreshDisplay();
        }
    }
    
    /**
     * Removes a constraint from the manager.
     * 
     * @param constraint The constraint to remove
     */
    public void removeConstraint(Constraint constraint) {
        // Remove the constraint from the list
        constraints.remove(constraint);
        
        // Remove the constraint from each object's constraint list
        for (GeometryObject obj : constraint.getObjects()) {
            List<Constraint> objConstraints = objectConstraints.get(obj);
            if (objConstraints != null) {
                objConstraints.remove(constraint);
                obj.removeConstraint(constraint);
            }
        }
        
        // Refresh the display
        if (core.getUI() != null) {
            core.getUI().refreshDisplay();
        }
    }
    
    /**
     * Gets all constraints.
     * 
     * @return A list of all constraints
     */
    public List<Constraint> getConstraints() {
        return new ArrayList<>(constraints);
    }
    
    /**
     * Gets all constraints for a specific object.
     * 
     * @param obj The object
     * @return A list of constraints for the object
     */
    public List<Constraint> getConstraintsForObject(GeometryObject obj) {
        List<Constraint> objConstraints = objectConstraints.get(obj);
        return objConstraints != null ? new ArrayList<>(objConstraints) : new ArrayList<>();
    }
    
    /**
     * Enforces all constraints.
     * This method iterates through all constraints and enforces them.
     * It uses a simple algorithm that enforces each constraint once,
     * which may not be sufficient for complex constraint systems.
     * A more sophisticated algorithm would be needed for such cases.
     */
    public void enforceAllConstraints() {
        // Enforce each constraint once
        for (Constraint constraint : constraints) {
            constraint.enforce();
        }
        
        // Refresh the display
        if (core.getUI() != null) {
            core.getUI().refreshDisplay();
        }
    }
    
    /**
     * Enforces constraints for a specific object.
     * This method enforces all constraints that involve the specified object.
     * 
     * @param obj The object
     */
    public void enforceConstraintsForObject(GeometryObject obj) {
        List<Constraint> objConstraints = objectConstraints.get(obj);
        if (objConstraints != null) {
            for (Constraint constraint : objConstraints) {
                constraint.enforce();
            }
        }
        
        // Refresh the display
        if (core.getUI() != null) {
            core.getUI().refreshDisplay();
        }
    }
    
    /**
     * Clears all constraints.
     */
    public void clearAllConstraints() {
        // Clear all constraints
        constraints.clear();
        objectConstraints.clear();
        
        // Clear constraints from all objects
        for (GeometryObject obj : core.getGeometryObjects()) {
            for (Constraint constraint : obj.getConstraints()) {
                obj.removeConstraint(constraint);
            }
        }
        
        // Refresh the display
        if (core.getUI() != null) {
            core.getUI().refreshDisplay();
        }
    }
}