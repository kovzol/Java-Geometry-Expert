package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Constraint;
import webapp.client.GExpertCore.GeometryObject;
import webapp.client.GExpertCore.Line;
import webapp.client.GExpertCore.Point;
import webapp.client.GExpertCore.Circle;
import webapp.client.constraints.ConstraintFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool for adding constraints between geometric objects.
 */
public class ConstraintTool extends AbstractTool {
    private List<GeometryObject> selectedObjects = new ArrayList<>();
    private String constraintType = null;
    
    /**
     * Constructor for ConstraintTool.
     * @param core The GExpertCore instance
     */
    public ConstraintTool(GExpertCore core) {
        super(core, "Constraint", "Add constraints between objects");
        setCursorType(1); // Crosshair cursor
    }
    
    /**
     * Sets the type of constraint to create.
     * @param type The constraint type
     */
    public void setConstraintType(String type) {
        this.constraintType = type;
        selectedObjects.clear();
        
        // Update status
        if (ui != null) {
            ui.updateStatus("Select objects for " + type + " constraint");
        }
    }
    
    @Override
    public void activate() {
        super.activate();
        selectedObjects.clear();
        
        // Show constraint type selection dialog
        if (ui != null) {
            ui.updateStatus("Select constraint type from menu");
        }
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        selectedObjects.clear();
        constraintType = null;
    }
    
    @Override
    public void onMouseDown(double x, double y) {
        // If no constraint type is selected, show a message
        if (constraintType == null) {
            if (ui != null) {
                ui.updateStatus("Please select a constraint type from the menu first");
            }
            return;
        }
        
        // Find the object at the clicked position
        GeometryObject obj = core.findObjectAt(x, y);
        if (obj == null) {
            if (ui != null) {
                ui.updateStatus("No object found at this position");
            }
            return;
        }
        
        // Check if the object is already selected
        if (selectedObjects.contains(obj)) {
            if (ui != null) {
                ui.updateStatus("Object already selected");
            }
            return;
        }
        
        // Add the object to the selected objects list
        selectedObjects.add(obj);
        
        // Update status
        if (ui != null) {
            ui.updateStatus("Selected " + getObjectDescription(obj) + 
                ". " + (getRequiredObjectCount() - selectedObjects.size()) + 
                " more object(s) needed.");
        }
        
        // Check if we have enough objects to create the constraint
        if (selectedObjects.size() >= getRequiredObjectCount()) {
            createConstraint();
        }
    }
    
    @Override
    public void onMouseMove(double x, double y) {
        // Find the object at the current mouse position
        GeometryObject obj = core.findObjectAt(x, y);
        
        // Show tooltip
        if (ui != null) {
            if (obj != null) {
                ui.showTooltip("Click to select " + getObjectDescription(obj));
            } else {
                ui.showTooltip("Click on an object to select it");
            }
        }
    }
    
    @Override
    public void onMouseExit(double x, double y) {
        // Hide tooltip
        if (ui != null) {
            ui.hideTooltip();
        }
    }
    
    /**
     * Gets the number of objects required for the current constraint type.
     * @return The number of required objects
     */
    private int getRequiredObjectCount() {
        switch (constraintType) {
            case "PARALLEL":
            case "PERPENDICULAR":
                return 2;
            case "POINT_ON_LINE":
            case "POINT_ON_CIRCLE":
                return 2;
            case "EQUAL_DISTANCE":
                return 4;
            default:
                return 0;
        }
    }
    
    /**
     * Gets a description of the object.
     * @param obj The object
     * @return A description of the object
     */
    private String getObjectDescription(GeometryObject obj) {
        String type = "object";
        if (obj instanceof Point) {
            type = "point";
        } else if (obj instanceof Line) {
            type = "line";
        } else if (obj instanceof Circle) {
            type = "circle";
        }
        
        return type + " " + (obj.getName() != null ? obj.getName() : "");
    }
    
    /**
     * Creates a constraint based on the selected objects.
     */
    private void createConstraint() {
        // Create the constraint
        Constraint constraint = null;
        
        switch (constraintType) {
            case "PARALLEL":
                if (selectedObjects.size() >= 2 && 
                    selectedObjects.get(0) instanceof Line && 
                    selectedObjects.get(1) instanceof Line) {
                    constraint = ConstraintFactory.createParallelConstraint(
                        (Line) selectedObjects.get(0), 
                        (Line) selectedObjects.get(1)
                    );
                }
                break;
                
            case "PERPENDICULAR":
                if (selectedObjects.size() >= 2 && 
                    selectedObjects.get(0) instanceof Line && 
                    selectedObjects.get(1) instanceof Line) {
                    constraint = ConstraintFactory.createPerpendicularConstraint(
                        (Line) selectedObjects.get(0), 
                        (Line) selectedObjects.get(1)
                    );
                }
                break;
                
            case "POINT_ON_LINE":
                if (selectedObjects.size() >= 2 && 
                    selectedObjects.get(0) instanceof Point && 
                    selectedObjects.get(1) instanceof Line) {
                    constraint = ConstraintFactory.createPointOnLineConstraint(
                        (Point) selectedObjects.get(0), 
                        (Line) selectedObjects.get(1)
                    );
                }
                break;
                
            case "POINT_ON_CIRCLE":
                if (selectedObjects.size() >= 2 && 
                    selectedObjects.get(0) instanceof Point && 
                    selectedObjects.get(1) instanceof Circle) {
                    constraint = ConstraintFactory.createPointOnCircleConstraint(
                        (Point) selectedObjects.get(0), 
                        (Circle) selectedObjects.get(1)
                    );
                }
                break;
                
            case "EQUAL_DISTANCE":
                if (selectedObjects.size() >= 4 && 
                    selectedObjects.get(0) instanceof Point && 
                    selectedObjects.get(1) instanceof Point && 
                    selectedObjects.get(2) instanceof Point && 
                    selectedObjects.get(3) instanceof Point) {
                    constraint = ConstraintFactory.createEqualDistanceConstraint(
                        (Point) selectedObjects.get(0), 
                        (Point) selectedObjects.get(1), 
                        (Point) selectedObjects.get(2), 
                        (Point) selectedObjects.get(3)
                    );
                }
                break;
        }
        
        // Add the constraint to the core
        if (constraint != null) {
            core.addConstraint(constraint);
            
            // Update status
            if (ui != null) {
                ui.updateStatus("Added " + constraintType + " constraint");
            }
        } else {
            // Update status
            if (ui != null) {
                ui.updateStatus("Failed to create constraint");
            }
        }
        
        // Reset for next constraint
        selectedObjects.clear();
    }
}