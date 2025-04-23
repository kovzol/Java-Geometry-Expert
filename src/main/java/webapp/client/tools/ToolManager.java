package webapp.client.tools;

import java.util.HashMap;
import java.util.Map;

import core.ui.GExpertUI;
import webapp.client.GExpertCore;
import webapp.client.tools.IntersectTool;
import webapp.client.tools.MirrorTool;
import webapp.client.tools.IsoscelesTriangleTool;
import webapp.client.tools.SquareTool;
import webapp.client.tools.TriangleTool;

/**
 * Manager for construction tools.
 * Handles tool registration, selection, and activation.
 */
public class ToolManager {
    private GExpertCore core;
    private GExpertUI ui;
    private Map<String, Tool> tools = new HashMap<>();
    private Tool activeTool = null;
    private String activeToolId = null;

    /**
     * Constructor for ToolManager.
     * @param core The GExpertCore instance
     */
    public ToolManager(GExpertCore core) {
        this.core = core;
        this.ui = core.getUI();

        // Initialize default tools
        initializeDefaultTools();
    }

    /**
     * Initializes the default set of tools.
     */
    private void initializeDefaultTools() {
        // Register basic tools
        registerTool("select", new SelectionTool(core));
        registerTool("point", new PointTool(core));
        registerTool("line", new LineTool(core));
        registerTool("parallel", new ParallelTool(core));
        registerTool("perp", new PerpendicularTool(core));
        registerTool("foot", new FootTool(core));
        registerTool("circle", new CircleTool(core));
        registerTool("circle3p", new Circle3PTool(core));
        registerTool("circler", new CompassTool(core));
        registerTool("angle", new AngleTool(core));
        registerTool("polygon", new PolygonTool(core));
        registerTool("text", new TextTool(core));
        registerTool("midpoint", new MidpointTool(core));

        // Register additional tools
        registerTool("intersect", new IntersectTool(core));
        registerTool("mirror", new MirrorTool(core));
        registerTool("iso", new IsoscelesTriangleTool(core));
        registerTool("square", new SquareTool(core));
        registerTool("triangle", new TriangleTool(core));

        // Set default tool
        activateTool("select");
    }

    /**
     * Registers a tool with the manager.
     * @param id The tool ID
     * @param tool The tool instance
     */
    public void registerTool(String id, Tool tool) {
        tools.put(id, tool);
    }

    /**
     * Gets a tool by ID.
     * @param id The tool ID
     * @return The tool instance, or null if not found
     */
    public Tool getTool(String id) {
        return tools.get(id);
    }

    /**
     * Gets the currently active tool.
     * @return The active tool, or null if none active
     */
    public Tool getActiveTool() {
        return activeTool;
    }

    /**
     * Gets the ID of the currently active tool.
     * @return The active tool ID, or null if none active
     */
    public String getActiveToolId() {
        return activeToolId;
    }

    /**
     * Activates a tool by ID.
     * @param id The tool ID
     * @return True if the tool was activated, false if not found
     */
    public boolean activateTool(String id) {
        Tool tool = tools.get(id);
        if (tool == null) {
            return false;
        }

        // Deactivate current tool
        if (activeTool != null) {
            // Remove mouse listener from canvas
            if (ui != null) {
                ui.getDrawingCanvas().removeMouseListener(activeTool);
            }

            // Deactivate tool
            activeTool.deactivate();
        }

        // Activate new tool
        activeTool = tool;
        activeToolId = id;

        // Add mouse listener to canvas
        if (ui != null) {
            ui.getDrawingCanvas().addMouseListener(activeTool);
        }

        // Activate tool
        activeTool.activate();

        return true;
    }

    /**
     * Updates the UI to reflect the current tool selection.
     * This should be called when the UI is initialized or changed.
     */
    public void updateUI() {
        if (ui != null && activeTool != null) {
            // Set cursor
            ui.setDrawingCursor(activeTool.getCursorType());

            // Update status
            ui.updateStatus(activeTool.getDescription());

            // Update toolbar buttons (if implemented)
            // This would require the UI to have a method for setting toolbar button selection
        }
    }
}
