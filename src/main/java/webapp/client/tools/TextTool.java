package webapp.client.tools;

import webapp.client.GExpertCore;
import webapp.client.GExpertCore.Text;
import webapp.client.GExpertCore.GeometryObject;
import core.ui.GExpertUI;

/**
 * Tool for creating text annotations.
 * This tool allows users to add text labels to the drawing.
 */
public class TextTool extends AbstractTool {
    private double clickX, clickY;
    private boolean locationSelected = false;

    /**
     * Constructor for TextTool.
     * @param core The GExpertCore instance
     */
    public TextTool(GExpertCore core) {
        super(core, "Text", "Add text annotations");
        setCursorType(1); // Crosshair cursor
    }

    @Override
    public void activate() {
        super.activate();
        locationSelected = false;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        locationSelected = false;
    }

    @Override
    public void onMouseDown(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Store the click location
        clickX = x;
        clickY = y;
        locationSelected = true;

        // Show input dialog to get text content
        if (ui != null) {
            ui.showInputDialog(
                "Add Text", 
                "Enter text content:", 
                "", 
                new GExpertUI.InputDialogCallback() {
                    @Override
                    public void onInputSubmitted(String value) {
                        if (value != null && !value.trim().isEmpty()) {
                            createText(clickX, clickY, value);
                        }
                        locationSelected = false;
                    }

                    @Override
                    public void onCancel() {
                        locationSelected = false;
                    }
                }
            );
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        // Snap to grid if enabled
        x = snapToGrid(x);
        y = snapToGrid(y);

        // Show tooltip
        if (ui != null && !locationSelected) {
            ui.showTooltip("Click to add text at (" + Math.round(x) + ", " + Math.round(y) + ")");
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
     * Creates a text annotation at the specified location.
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param content The text content
     */
    private void createText(double x, double y, String content) {
        // Create the text object
        Text text = new Text(x, y, content);
        text.setColor("#FF0000"); // Red for text
        text.setFontSize(12.0); // Default font size

        // Add the text to the core
        core.addGeometryObject(text);

        // Select the text
        core.selectObject(text);

        // Update status
        if (ui != null) {
            ui.updateStatus("Text added: \"" + content + "\"");
        }
    }
}