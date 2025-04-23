package core.ui;

import core.GExpertCore;
import java.util.List;

/**
 * Interface for UI operations that can be implemented differently
 * for desktop (Swing) and web (GWT) versions.
 * 
 * This interface provides a comprehensive abstraction of all UI operations
 * needed by the Java Geometry Expert application, allowing for different
 * implementations for desktop (Swing) and web (GWT) versions.
 */
public interface GExpertUI {
    // ===== Basic UI operations =====

    /**
     * Shows a message to the user.
     * @param message The message to show
     */
    void showMessage(String message);

    /**
     * Updates the application title.
     * @param title The new title
     */
    void updateTitle(String title);

    /**
     * Updates the status bar text.
     * @param status The new status text
     */
    void updateStatus(String status);

    /**
     * Shows a tooltip text.
     * @param text The tooltip text
     */
    void showTooltip(String text);

    /**
     * Hides any visible tooltip.
     */
    void hideTooltip();

    // ===== Dialog operations =====

    /**
     * Shows a dialog with a message and buttons.
     * @param title Dialog title
     * @param message Dialog message
     * @param buttons Array of button labels
     * @param callback Callback for button clicks
     */
    void showDialog(String title, String message, String[] buttons, DialogCallback callback);

    /**
     * Shows a file dialog for opening or saving files.
     * @param open True for open dialog, false for save dialog
     * @param extensions Array of allowed file extensions
     * @param callback Callback for file selection
     */
    void showFileDialog(boolean open, String[] extensions, FileDialogCallback callback);

    /**
     * Shows an input dialog for text entry.
     * @param title Dialog title
     * @param message Dialog message
     * @param initialValue Initial value for the input field
     * @param callback Callback for input submission
     */
    void showInputDialog(String title, String message, String initialValue, InputDialogCallback callback);

    /**
     * Shows a property dialog for editing object properties.
     * @param title Dialog title
     * @param properties List of property names
     * @param values List of property values
     * @param callback Callback for property changes
     */
    void showPropertyDialog(String title, List<String> properties, List<String> values, PropertyDialogCallback callback);

    /**
     * Shows a color picker dialog.
     * @param title Dialog title
     * @param initialColor Initial color (hex format: #RRGGBB)
     * @param callback Callback for color selection
     */
    void showColorDialog(String title, String initialColor, ColorDialogCallback callback);

    // ===== Drawing operations =====

    /**
     * Sets the current drawing mode.
     * @param mode The drawing mode to set
     */
    void setDrawingMode(int mode);

    /**
     * Refreshes the display.
     */
    void refreshDisplay();

    /**
     * Sets the cursor for the drawing area.
     * @param cursorType The type of cursor to set
     */
    void setDrawingCursor(int cursorType);

    /**
     * Zooms the drawing to fit all objects.
     */
    void zoomToFit();

    /**
     * Sets the zoom level.
     * @param zoomFactor The zoom factor (1.0 = 100%)
     */
    void setZoom(double zoomFactor);

    /**
     * Gets the current zoom factor.
     * @return The current zoom factor
     */
    double getZoom();

    // ===== Drawing canvas operations =====

    /**
     * Gets the drawing canvas.
     * @return The drawing canvas
     */
    DrawingCanvas getDrawingCanvas();

    /**
     * Gets the width of the drawing area.
     * @return The width in pixels
     */
    int getDrawingWidth();

    /**
     * Gets the height of the drawing area.
     * @return The height in pixels
     */
    int getDrawingHeight();

    // ===== Menu and toolbar operations =====

    /**
     * Enables or disables a menu item.
     * @param menuId The ID of the menu item
     * @param enable True to enable, false to disable
     */
    void enableMenuItem(String menuId, boolean enable);

    /**
     * Checks or unchecks a menu item.
     * @param menuId The ID of the menu item
     * @param check True to check, false to uncheck
     */
    void checkMenuItem(String menuId, boolean check);

    /**
     * Adds a menu item.
     * @param parentMenuId The ID of the parent menu
     * @param menuId The ID of the new menu item
     * @param label The label for the menu item
     * @param tooltip The tooltip for the menu item
     * @param command The command to execute when clicked
     */
    void addMenuItem(String parentMenuId, String menuId, String label, String tooltip, Runnable command);

    /**
     * Removes a menu item.
     * @param menuId The ID of the menu item to remove
     */
    void removeMenuItem(String menuId);

    /**
     * Adds a toolbar button.
     * @param toolbarId The ID of the toolbar
     * @param buttonId The ID of the new button
     * @param label The label for the button
     * @param tooltip The tooltip for the button
     * @param iconPath The path to the icon image
     * @param command The command to execute when clicked
     */
    void addToolbarButton(String toolbarId, String buttonId, String label, String tooltip, String iconPath, Runnable command);

    /**
     * Removes a toolbar button.
     * @param buttonId The ID of the button to remove
     */
    void removeToolbarButton(String buttonId);

    /**
     * Sets the selected state of a toolbar button.
     * @param buttonId The ID of the button
     * @param selected True to select, false to deselect
     */
    void setToolbarButtonSelected(String buttonId, boolean selected);

    // ===== Geometry object manipulation =====

    /**
     * Selects a geometry object.
     * @param objectId The ID of the object to select
     */
    void selectObject(String objectId);

    /**
     * Deselects all geometry objects.
     */
    void deselectAllObjects();

    /**
     * Highlights a geometry object.
     * @param objectId The ID of the object to highlight
     */
    void highlightObject(String objectId);

    /**
     * Removes highlighting from all objects.
     */
    void clearHighlights();

    // ===== Proof system UI =====

    /**
     * Shows the proof panel.
     * @param visible True to show, false to hide
     */
    void showProofPanel(boolean visible);

    /**
     * Updates the proof steps display.
     * @param steps List of proof step descriptions
     */
    void updateProofSteps(List<String> steps);

    /**
     * Shows a theorem result.
     * @param theoremName The name of the theorem
     * @param isProven True if proven, false otherwise
     * @param explanation The explanation or proof
     */
    void showTheoremResult(String theoremName, boolean isProven, String explanation);

    // ===== Animation and visualization =====

    /**
     * Starts an animation.
     * @param animationType The type of animation
     * @param durationMs The duration in milliseconds
     */
    void startAnimation(int animationType, int durationMs);

    /**
     * Stops the current animation.
     */
    void stopAnimation();

    /**
     * Sets the animation speed.
     * @param speedFactor The speed factor (1.0 = normal speed)
     */
    void setAnimationSpeed(double speedFactor);

    // ===== Interface for callbacks =====

    /**
     * Callback for dialog button clicks.
     */
    interface DialogCallback {
        /**
         * Called when a button is clicked.
         * @param buttonIndex The index of the clicked button
         */
        void onButtonClicked(int buttonIndex);
    }

    /**
     * Callback for file dialog operations.
     */
    interface FileDialogCallback {
        /**
         * Called when a file is selected.
         * @param filePath The path of the selected file
         */
        void onFileSelected(String filePath);

        /**
         * Called when the dialog is canceled.
         */
        void onCancel();
    }

    /**
     * Callback for input dialog operations.
     */
    interface InputDialogCallback {
        /**
         * Called when input is submitted.
         * @param value The input value
         */
        void onInputSubmitted(String value);

        /**
         * Called when the dialog is canceled.
         */
        void onCancel();
    }

    /**
     * Callback for property dialog operations.
     */
    interface PropertyDialogCallback {
        /**
         * Called when properties are changed.
         * @param properties List of property names
         * @param values List of property values
         */
        void onPropertiesChanged(List<String> properties, List<String> values);

        /**
         * Called when the dialog is canceled.
         */
        void onCancel();
    }

    /**
     * Callback for color dialog operations.
     */
    interface ColorDialogCallback {
        /**
         * Called when a color is selected.
         * @param colorHex The selected color in hex format (#RRGGBB)
         */
        void onColorSelected(String colorHex);

        /**
         * Called when the dialog is canceled.
         */
        void onCancel();
    }

    /**
     * Interface for drawing canvas operations.
     * This extends the DrawingContext interface from GExpertCore.
     */
    interface DrawingCanvas extends GExpertCore.DrawingContext {
        // Canvas operations
        /**
         * Clears the canvas.
         */
        void clear();

        /**
         * Refreshes the canvas display.
         */
        void refresh();

        /**
         * Sets the background color of the canvas.
         * @param colorHex The color in hex format (#RRGGBB)
         */
        void setBackgroundColor(String colorHex);

        /**
         * Sets the drawing color.
         * @param colorHex The color in hex format (#RRGGBB)
         */
        void setDrawingColor(String colorHex);

        /**
         * Sets the line width for drawing.
         * @param width The line width in pixels
         */
        void setLineWidth(float width);

        /**
         * Sets the line style for drawing.
         * @param style The line style (0=solid, 1=dashed, 2=dotted, 3=dash-dot)
         */
        void setLineStyle(int style);

        /**
         * Sets the font for text drawing.
         * @param fontName The font name
         * @param fontSize The font size
         * @param bold True for bold, false for normal
         * @param italic True for italic, false for normal
         */
        void setFont(String fontName, int fontSize, boolean bold, boolean italic);

        /**
         * Draws text at the specified position.
         * @param text The text to draw
         * @param x The x-coordinate
         * @param y The y-coordinate
         */
        void drawText(String text, double x, double y);

        // Transformation operations
        /**
         * Sets the transformation matrix.
         * @param scaleX The x-scale factor
         * @param scaleY The y-scale factor
         * @param translateX The x-translation
         * @param translateY The y-translation
         */
        void setTransform(double scaleX, double scaleY, double translateX, double translateY);

        /**
         * Translates the canvas.
         * @param dx The x-translation
         * @param dy The y-translation
         */
        void translate(double dx, double dy);

        /**
         * Scales the canvas.
         * @param sx The x-scale factor
         * @param sy The y-scale factor
         */
        void scale(double sx, double sy);

        /**
         * Rotates the canvas.
         * @param angle The rotation angle in radians
         */
        void rotate(double angle);

        /**
         * Resets the transformation to identity.
         */
        void resetTransform();

        // Advanced drawing operations
        /**
         * Draws a polygon.
         * @param xPoints Array of x-coordinates
         * @param yPoints Array of y-coordinates
         * @param nPoints Number of points
         * @param filled True to fill the polygon, false for outline only
         */
        void drawPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean filled);

        /**
         * Draws an arc.
         * @param x The x-coordinate of the center
         * @param y The y-coordinate of the center
         * @param radius The radius
         * @param startAngle The start angle in radians
         * @param arcAngle The arc angle in radians
         * @param filled True to fill the arc, false for outline only
         */
        void drawArc(double x, double y, double radius, double startAngle, double arcAngle, boolean filled);

        /**
         * Draws a bezier curve.
         * @param x1 The x-coordinate of the start point
         * @param y1 The y-coordinate of the start point
         * @param ctrlX1 The x-coordinate of the first control point
         * @param ctrlY1 The y-coordinate of the first control point
         * @param ctrlX2 The x-coordinate of the second control point
         * @param ctrlY2 The y-coordinate of the second control point
         * @param x2 The x-coordinate of the end point
         * @param y2 The y-coordinate of the end point
         */
        void drawBezier(double x1, double y1, double ctrlX1, double ctrlY1, 
                        double ctrlX2, double ctrlY2, double x2, double y2);

        // Event handling
        /**
         * Adds a mouse listener.
         * @param listener The mouse listener to add
         */
        void addMouseListener(MouseListener listener);

        /**
         * Removes a mouse listener.
         * @param listener The mouse listener to remove
         */
        void removeMouseListener(MouseListener listener);

        /**
         * Adds a keyboard listener.
         * @param listener The keyboard listener to add
         */
        void addKeyboardListener(KeyboardListener listener);

        /**
         * Removes a keyboard listener.
         * @param listener The keyboard listener to remove
         */
        void removeKeyboardListener(KeyboardListener listener);

        // Interface for mouse events
        /**
         * Interface for mouse event listeners.
         */
        interface MouseListener {
            /**
             * Called when a mouse button is pressed.
             * @param x The x-coordinate
             * @param y The y-coordinate
             */
            void onMouseDown(double x, double y);

            /**
             * Called when the mouse is moved.
             * @param x The x-coordinate
             * @param y The y-coordinate
             */
            void onMouseMove(double x, double y);

            /**
             * Called when a mouse button is released.
             * @param x The x-coordinate
             * @param y The y-coordinate
             */
            void onMouseUp(double x, double y);

            /**
             * Called when the mouse wheel is scrolled.
             * @param x The x-coordinate
             * @param y The y-coordinate
             * @param deltaZ The scroll amount
             */
            default void onMouseWheel(double x, double y, double deltaZ) {}

            /**
             * Called when the mouse enters the canvas.
             * @param x The x-coordinate
             * @param y The y-coordinate
             */
            default void onMouseEnter(double x, double y) {}

            /**
             * Called when the mouse exits the canvas.
             * @param x The x-coordinate
             * @param y The y-coordinate
             */
            default void onMouseExit(double x, double y) {}
        }

        /**
         * Interface for keyboard event listeners.
         */
        interface KeyboardListener {
            /**
             * Called when a key is pressed.
             * @param keyCode The key code
             * @param character The character (if applicable)
             */
            void onKeyDown(int keyCode, char character);

            /**
             * Called when a key is released.
             * @param keyCode The key code
             * @param character The character (if applicable)
             */
            void onKeyUp(int keyCode, char character);

            /**
             * Called when a key is typed (pressed and released).
             * @param character The character
             */
            default void onKeyTyped(char character) {}
        }
    }
}
