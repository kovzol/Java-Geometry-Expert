package webapp.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import webapp.client.GExpertCore;
import core.ui.GExpertUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GWT implementation of the GExpertUI interface.
 * This class provides the web UI implementation using GWT components.
 */
public class GwtGExpertUI implements GExpertUI {
    private JGEXWebApp webApp;
    private GExpertCore core; // Reference to the GExpertCore instance
    private Map<String, MenuItem> menuItems = new HashMap<>();
    private Map<String, Button> toolbarButtons = new HashMap<>();
    private Label statusLabel;
    private Label tooltipLabel;
    private GwtDrawingCanvas drawingCanvas;
    private Timer tooltipTimer;

    /**
     * Constructor for GwtGExpertUI.
     * 
     * @param webApp The JGEXWebApp instance this UI is associated with
     */
    public GwtGExpertUI(JGEXWebApp webApp) {
        this.webApp = webApp;
        this.statusLabel = new Label(" ");
        this.tooltipLabel = new Label();
        tooltipLabel.getElement().getStyle().setBackgroundColor("#FFFFF0");
        tooltipLabel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        tooltipLabel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        tooltipLabel.getElement().getStyle().setBorderColor("#000000");
        tooltipLabel.getElement().getStyle().setPadding(3, Style.Unit.PX);
        tooltipLabel.setVisible(false);

        // Initialize drawing canvas
        this.drawingCanvas = new GwtDrawingCanvas();

        // Initialize tooltip timer
        tooltipTimer = new Timer() {
            @Override
            public void run() {
                hideTooltip();
            }
        };
    }

    /**
     * Sets the GExpertCore instance.
     * @param core The GExpertCore instance
     */
    public void setCore(GExpertCore core) {
        this.core = core;
    }

    @Override
    public void showMessage(String message) {
        Window.alert(message);
    }

    @Override
    public void updateTitle(String title) {
        Window.setTitle(title);
    }

    @Override
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void showTooltip(String text) {
        tooltipLabel.setText(text);
        tooltipLabel.setVisible(true);
        tooltipTimer.schedule(3000);
    }

    @Override
    public void hideTooltip() {
        tooltipLabel.setVisible(false);
        tooltipTimer.cancel();
    }

    @Override
    public void showDialog(String title, String message, String[] buttons, DialogCallback callback) {
        // Create a dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText(title);
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);

        // Create content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add message
        HTML messageLabel = new HTML(message.replace("\n", "<br>"));
        dialogContents.add(messageLabel);

        // Add buttons
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setSpacing(10);
        dialogContents.add(buttonPanel);

        for (int i = 0; i < buttons.length; i++) {
            final int buttonIndex = i;
            Button button = new Button(buttons[i]);
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    dialogBox.hide();
                    if (callback != null) {
                        callback.onButtonClicked(buttonIndex);
                    }
                }
            });
            buttonPanel.add(button);
        }

        dialogBox.center();
        dialogBox.show();
    }

    @Override
    public void showFileDialog(boolean open, String[] extensions, FileDialogCallback callback) {
        // GWT doesn't have a built-in file dialog
        // For now, we'll just show a message that this is not supported
        Window.alert("File operations are not fully supported in the web version yet.");
        callback.onCancel();
    }

    @Override
    public void showInputDialog(String title, String message, String initialValue, InputDialogCallback callback) {
        // Create a dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText(title);
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);

        // Create content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add message
        HTML messageLabel = new HTML(message.replace("\n", "<br>"));
        dialogContents.add(messageLabel);

        // Add input field
        final TextBox textBox = new TextBox();
        textBox.setText(initialValue);
        textBox.setWidth("250px");
        dialogContents.add(textBox);

        // Add buttons
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setSpacing(10);
        dialogContents.add(buttonPanel);

        Button okButton = new Button("OK");
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                if (callback != null) {
                    callback.onInputSubmitted(textBox.getText());
                }
            }
        });
        buttonPanel.add(okButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });
        buttonPanel.add(cancelButton);

        dialogBox.center();
        dialogBox.show();
        textBox.setFocus(true);
    }

    @Override
    public void showPropertyDialog(String title, List<String> properties, List<String> values, PropertyDialogCallback callback) {
        // Create a dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText(title);
        dialogBox.setGlassEnabled(true);
        dialogBox.setAnimationEnabled(true);

        // Create content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add property fields
        final List<TextBox> fields = new ArrayList<>();
        Grid grid = new Grid(properties.size(), 2);
        grid.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        grid.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        grid.getElement().getStyle().setBorderColor("#CCCCCC");
        grid.getElement().getStyle().setMargin(5, Style.Unit.PX);

        for (int i = 0; i < properties.size(); i++) {
            grid.setText(i, 0, properties.get(i) + ":");
            TextBox field = new TextBox();
            field.setText(values.get(i));
            field.setWidth("200px");
            grid.setWidget(i, 1, field);
            fields.add(field);
        }

        dialogContents.add(grid);

        // Add buttons
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setSpacing(10);
        dialogContents.add(buttonPanel);

        Button okButton = new Button("OK");
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                if (callback != null) {
                    for (int i = 0; i < properties.size(); i++) {
                        values.set(i, fields.get(i).getText());
                    }
                    callback.onPropertiesChanged(properties, values);
                }
            }
        });
        buttonPanel.add(okButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });
        buttonPanel.add(cancelButton);

        dialogBox.center();
        dialogBox.show();
    }

    @Override
    public void showColorDialog(String title, String initialColor, ColorDialogCallback callback) {
        // GWT doesn't have a built-in color picker
        // For now, we'll just show a simple input dialog for hex color
        showInputDialog(title, "Enter color in hex format (e.g., #FF0000 for red):", initialColor, new InputDialogCallback() {
            @Override
            public void onInputSubmitted(String value) {
                if (callback != null) {
                    callback.onColorSelected(value);
                }
            }

            @Override
            public void onCancel() {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });
    }

    @Override
    public void setDrawingMode(int mode) {
        drawingCanvas.setDrawingMode(mode);
    }

    @Override
    public void refreshDisplay() {
        drawingCanvas.refresh();
    }

    @Override
    public void setDrawingCursor(int cursorType) {
        String cursor;
        switch (cursorType) {
            case 0: // Default
                cursor = "default";
                break;
            case 1: // Crosshair
                cursor = "crosshair";
                break;
            case 2: // Hand
                cursor = "pointer";
                break;
            case 3: // Move
                cursor = "move";
                break;
            default:
                cursor = "default";
        }
        drawingCanvas.getElement().getStyle().setCursor(Style.Cursor.valueOf(cursor.toUpperCase()));
    }

    @Override
    public void zoomToFit() {
        drawingCanvas.resetTransform();
        refreshDisplay();
    }

    @Override
    public void setZoom(double zoomFactor) {
        drawingCanvas.scale(zoomFactor, zoomFactor);
        refreshDisplay();
    }

    @Override
    public double getZoom() {
        // This would return the current zoom factor
        // For now, we'll just return 1.0
        return 1.0;
    }

    @Override
    public DrawingCanvas getDrawingCanvas() {
        return drawingCanvas;
    }

    @Override
    public int getDrawingWidth() {
        return drawingCanvas.getOffsetWidth();
    }

    @Override
    public int getDrawingHeight() {
        return drawingCanvas.getOffsetHeight();
    }

    @Override
    public void enableMenuItem(String menuId, boolean enable) {
        MenuItem item = menuItems.get(menuId);
        if (item != null) {
            item.setEnabled(enable);
        }
    }

    @Override
    public void checkMenuItem(String menuId, boolean check) {
        // GWT MenuItems don't have a checked state
        // This would need to be implemented with custom menu items
    }

    @Override
    public void addMenuItem(String parentMenuId, String menuId, String label, String tooltip, Runnable command) {
        MenuItem item = new MenuItem(label, new com.google.gwt.user.client.Command() {
            @Override
            public void execute() {
                command.run();
            }
        });
        item.setTitle(tooltip);
        menuItems.put(menuId, item);

        // Find parent menu
        MenuBar parentMenu = findMenuBar(parentMenuId);
        if (parentMenu != null) {
            parentMenu.addItem(item);
        }
    }

    private MenuBar findMenuBar(String menuId) {
        // This would need to be implemented to find the menu bar by ID
        // For now, we'll just return null
        return null;
    }

    @Override
    public void removeMenuItem(String menuId) {
        MenuItem item = menuItems.get(menuId);
        if (item != null) {
            // GWT doesn't provide a direct way to remove menu items
            // This would need to be implemented with custom menu handling
            menuItems.remove(menuId);
        }
    }

    @Override
    public void addToolbarButton(String toolbarId, String buttonId, String label, String tooltip, String iconPath, Runnable command) {
        Button button = new Button();

        if (iconPath != null && !iconPath.isEmpty()) {
            // GWT doesn't have a direct way to set button icons
            // This would need to be implemented with custom button styling
            button.setText(label);
        } else {
            button.setText(label);
        }

        button.setTitle(tooltip);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                command.run();
            }
        });

        toolbarButtons.put(buttonId, button);

        // Find toolbar
        // This would need to be implemented to find the toolbar by ID
    }

    @Override
    public void removeToolbarButton(String buttonId) {
        Button button = toolbarButtons.get(buttonId);
        if (button != null) {
            // This would need to be implemented to remove the button from its parent
            toolbarButtons.remove(buttonId);
        }
    }

    @Override
    public void setToolbarButtonSelected(String buttonId, boolean selected) {
        Button button = toolbarButtons.get(buttonId);
        if (button != null) {
            // GWT Buttons don't have a selected state
            // Use inline styles to show selection state
            if (selected) {
                button.getElement().getStyle().setBackgroundColor("#CCCCCC");
                button.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            } else {
                button.getElement().getStyle().setBackgroundColor("");
                button.getElement().getStyle().setFontWeight(Style.FontWeight.NORMAL);
            }
        }
    }

    @Override
    public void selectObject(String objectId) {
        // This would be implemented to select a geometry object
        // For now, we'll just log the action
        System.out.println("Selected object: " + objectId);
    }

    @Override
    public void deselectAllObjects() {
        // This would be implemented to deselect all geometry objects
        // For now, we'll just log the action
        System.out.println("Deselected all objects");
    }

    @Override
    public void highlightObject(String objectId) {
        // This would be implemented to highlight a geometry object
        // For now, we'll just log the action
        System.out.println("Highlighted object: " + objectId);
    }

    @Override
    public void clearHighlights() {
        // This would be implemented to clear all highlights
        // For now, we'll just log the action
        System.out.println("Cleared all highlights");
    }

    @Override
    public void showProofPanel(boolean visible) {
        // This would be implemented to show/hide the proof panel
        // For now, we'll just log the action
        System.out.println("Proof panel visibility: " + visible);
    }

    @Override
    public void updateProofSteps(List<String> steps) {
        // This would be implemented to update the proof steps display
        // For now, we'll just log the action
        System.out.println("Updated proof steps: " + steps.size() + " steps");
    }

    @Override
    public void showTheoremResult(String theoremName, boolean isProven, String explanation) {
        // This would be implemented to show a theorem result
        // For now, we'll just log the action
        System.out.println("Theorem result: " + theoremName + " - " + (isProven ? "Proven" : "Not proven"));
    }

    @Override
    public void startAnimation(int animationType, int durationMs) {
        // This would be implemented to start an animation
        // For now, we'll just log the action
        System.out.println("Started animation type " + animationType + " for " + durationMs + "ms");
    }

    @Override
    public void stopAnimation() {
        // This would be implemented to stop the current animation
        // For now, we'll just log the action
        System.out.println("Stopped animation");
    }

    @Override
    public void setAnimationSpeed(double speedFactor) {
        // This would be implemented to set the animation speed
        // For now, we'll just log the action
        System.out.println("Set animation speed to " + speedFactor);
    }

    /**
     * Gets the status label component.
     * @return The status label
     */
    public Label getStatusLabel() {
        return statusLabel;
    }

    /**
     * Gets the tooltip label component.
     * @return The tooltip label
     */
    public Label getTooltipLabel() {
        return tooltipLabel;
    }

    /**
     * GWT implementation of the DrawingCanvas interface.
     */
    private class GwtDrawingCanvas extends FocusPanel implements DrawingCanvas {
        private Canvas canvas;
        private Canvas bufferCanvas; // For double-buffering
        private String backgroundColor = "#FFFFFF";
        private String drawingColor = "#000000";
        private double lineWidth = 1.0;
        private String fontName = "Arial";
        private int fontSize = 12;
        private boolean fontBold = false;
        private boolean fontItalic = false;
        private int drawingMode = 0;
        private int lineStyle = 0; // 0=solid, 1=dashed, 2=dotted, 3=dash-dot

        // Zoom and pan state
        private double zoomFactor = 1.0;
        private double panX = 0.0;
        private double panY = 0.0;
        private boolean isPanning = false;
        private double lastMouseX = 0.0;
        private double lastMouseY = 0.0;

        // Grid properties
        private boolean showGrid = false; // Hide grid by default
        private double gridSize = 20.0;
        private String gridColor = "#AAAAAA"; // Slightly darker gray for better visibility

        public GwtDrawingCanvas() {
            canvas = Canvas.createIfSupported();
            if (canvas != null) {
                canvas.setWidth("800px");
                canvas.setHeight("600px");
                canvas.setCoordinateSpaceWidth(800);
                canvas.setCoordinateSpaceHeight(600);

                // Add border to canvas
                canvas.getElement().getStyle().setBorderWidth(2, Style.Unit.PX);
                canvas.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
                canvas.getElement().getStyle().setBorderColor("#000000");

                // Initialize buffer canvas for double-buffering
                initBufferCanvas();

                // Set initial background
                setBackgroundColor(backgroundColor);

                // Add to panel
                setWidget(canvas);
            } else {
                // Canvas not supported, show error message
                setWidget(new Label("Canvas not supported in this browser"));
            }

            // Initialize transform
            applyTransformation();

            // Initial refresh to show grid
            refresh();

            // Add mouse handlers
            addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    // Store mouse position for panning
                    lastMouseX = event.getX();
                    lastMouseY = event.getY();

                    // Check if middle button or right button (for panning)
                    if (event.getNativeButton() == NativeEvent.BUTTON_MIDDLE || 
                        (event.getNativeButton() == NativeEvent.BUTTON_RIGHT && !event.isControlKeyDown())) {
                        isPanning = true;
                        event.preventDefault();
                    } else {
                        // Forward to listeners for other operations
                        for (MouseListener listener : mouseListeners) {
                            listener.onMouseDown(event.getX(), event.getY());
                        }
                    }
                }
            });

            addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(MouseMoveEvent event) {
                    if (isPanning) {
                        // Calculate the distance moved
                        double dx = event.getX() - lastMouseX;
                        double dy = event.getY() - lastMouseY;

                        // Pan the canvas
                        pan(dx, dy);

                        // Update last position
                        lastMouseX = event.getX();
                        lastMouseY = event.getY();

                        event.preventDefault();
                    } else {
                        // Forward to listeners for other operations
                        for (MouseListener listener : mouseListeners) {
                            listener.onMouseMove(event.getX(), event.getY());
                        }
                    }
                }
            });

            addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent event) {
                    if (isPanning) {
                        isPanning = false;
                        event.preventDefault();
                    } else {
                        // Forward to listeners for other operations
                        for (MouseListener listener : mouseListeners) {
                            listener.onMouseUp(event.getX(), event.getY());
                        }
                    }
                }
            });

            addMouseWheelHandler(new MouseWheelHandler() {
                @Override
                public void onMouseWheel(MouseWheelEvent event) {
                    // Zoom with mouse wheel
                    double delta = event.getDeltaY();
                    double factor = Math.pow(1.1, -delta / 40); // Adjust sensitivity

                    // Zoom around mouse position
                    zoom(factor, event.getX(), event.getY());

                    // Also forward to listeners
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseWheel(event.getX(), event.getY(), delta);
                    }

                    event.preventDefault();
                }
            });

            addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseEnter(event.getX(), event.getY());
                    }
                }
            });

            addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseExit(event.getX(), event.getY());
                    }
                }
            });

            // Add keyboard handlers
            addKeyDownHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    for (KeyboardListener listener : keyboardListeners) {
                        // Convert int to char for the second parameter
                        listener.onKeyDown(event.getNativeKeyCode(), (char)event.getNativeEvent().getCharCode());
                    }
                }
            });

            addKeyUpHandler(new KeyUpHandler() {
                @Override
                public void onKeyUp(KeyUpEvent event) {
                    for (KeyboardListener listener : keyboardListeners) {
                        // Convert int to char for the second parameter
                        listener.onKeyUp(event.getNativeKeyCode(), (char)event.getNativeEvent().getCharCode());
                    }
                }
            });

            addKeyPressHandler(new KeyPressHandler() {
                @Override
                public void onKeyPress(KeyPressEvent event) {
                    for (KeyboardListener listener : keyboardListeners) {
                        listener.onKeyTyped(event.getCharCode());
                    }
                }
            });
        }

        private List<MouseListener> mouseListeners = new ArrayList<>();
        private List<KeyboardListener> keyboardListeners = new ArrayList<>();

        @Override
        public void addMouseListener(MouseListener listener) {
            mouseListeners.add(listener);
        }

        @Override
        public void removeMouseListener(MouseListener listener) {
            mouseListeners.remove(listener);
        }

        @Override
        public void addKeyboardListener(KeyboardListener listener) {
            keyboardListeners.add(listener);
        }

        @Override
        public void removeKeyboardListener(KeyboardListener listener) {
            keyboardListeners.remove(listener);
        }

        @Override
        public void clear() {
            if (canvas != null) {
                canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
                setBackgroundColor(backgroundColor);
            }
        }

        /**
         * Initializes the buffer canvas for double-buffering.
         */
        private void initBufferCanvas() {
            bufferCanvas = Canvas.createIfSupported();
            if (bufferCanvas != null) {
                bufferCanvas.setCoordinateSpaceWidth(canvas.getCoordinateSpaceWidth());
                bufferCanvas.setCoordinateSpaceHeight(canvas.getCoordinateSpaceHeight());
            }
        }

        /**
         * Applies the current zoom and pan transformation to the canvas.
         */
        private void applyTransformation() {
            if (canvas != null) {
                canvas.getContext2d().setTransform(zoomFactor, 0, 0, zoomFactor, panX, panY);
            }
        }

        @Override
        public void refresh() {
            if (canvas == null || bufferCanvas == null) return;

            // Clear buffer canvas
            bufferCanvas.getContext2d().clearRect(0, 0, bufferCanvas.getCoordinateSpaceWidth(), 
                                               bufferCanvas.getCoordinateSpaceHeight());

            // Apply transformation to buffer
            Context2d bufferCtx = bufferCanvas.getContext2d();
            bufferCtx.setTransform(zoomFactor, 0, 0, zoomFactor, panX, panY);

            // Draw grid if enabled
            if (showGrid) {
                drawGrid(bufferCtx);
            }

            // Draw all geometry objects
            if (core != null) {
                // Create a simple adapter that converts GwtDrawingCanvas to DrawingContext
                GExpertCore.DrawingContext adapter = new GExpertCore.DrawingContext() {
                    @Override
                    public void drawPoint(double x, double y) {
                        GwtDrawingCanvas.this.drawPoint(x, y);
                    }

                    @Override
                    public void drawLine(double x1, double y1, double x2, double y2) {
                        GwtDrawingCanvas.this.drawLine(x1, y1, x2, y2);
                    }

                    @Override
                    public void drawCircle(double x, double y, double radius) {
                        GwtDrawingCanvas.this.drawCircle(x, y, radius);
                    }

                    @Override
                    public void setDrawingColor(String colorHex) {
                        GwtDrawingCanvas.this.setDrawingColor(colorHex);
                    }

                    @Override
                    public void setLineWidth(float width) {
                        GwtDrawingCanvas.this.setLineWidth(width);
                    }

                    @Override
                    public void drawText(String text, double x, double y) {
                        GwtDrawingCanvas.this.drawText(text, x, y);
                    }
                };

                // Call drawAll with the adapter
                core.drawAll(adapter);
            }

            // Copy buffer to visible canvas
            canvas.getContext2d().drawImage(bufferCanvas.getCanvasElement(), 0, 0);
        }

        /**
         * Draws a grid on the canvas.
         * @param ctx The canvas context to draw on
         */
        private void drawGrid(Context2d ctx) {
            if (ctx == null) return;

            double width = canvas.getCoordinateSpaceWidth();
            double height = canvas.getCoordinateSpaceHeight();

            // Save current drawing state
            ctx.save();

            // Set grid style
            ctx.setStrokeStyle(gridColor);
            ctx.setLineWidth(0.8); // Slightly thicker lines for better visibility

            // Calculate grid bounds based on current transform
            double invZoom = 1.0 / zoomFactor;
            double startX = -panX * invZoom;
            double startY = -panY * invZoom;
            double endX = (width - panX) * invZoom;
            double endY = (height - panY) * invZoom;

            // Adjust to grid size
            double gridStartX = Math.floor(startX / gridSize) * gridSize;
            double gridStartY = Math.floor(startY / gridSize) * gridSize;

            // Draw vertical grid lines
            for (double x = gridStartX; x <= endX; x += gridSize) {
                ctx.beginPath();
                ctx.moveTo(x, startY);
                ctx.lineTo(x, endY);
                ctx.stroke();
            }

            // Draw horizontal grid lines
            for (double y = gridStartY; y <= endY; y += gridSize) {
                ctx.beginPath();
                ctx.moveTo(startX, y);
                ctx.lineTo(endX, y);
                ctx.stroke();
            }

            // Restore drawing state
            ctx.restore();
        }

        @Override
        public void setBackgroundColor(String colorHex) {
            this.backgroundColor = colorHex;
            if (canvas != null) {
                canvas.getContext2d().setFillStyle(colorHex);
                canvas.getContext2d().fillRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
            }
        }

        @Override
        public void setDrawingColor(String colorHex) {
            this.drawingColor = colorHex;
            if (canvas != null) {
                canvas.getContext2d().setStrokeStyle(colorHex);
                canvas.getContext2d().setFillStyle(colorHex);
            }
        }

        @Override
        public void setLineWidth(float width) {
            this.lineWidth = width;
            if (canvas != null) {
                canvas.getContext2d().setLineWidth(width);
            }
        }

        @Override
        public void setLineStyle(int style) {
            this.lineStyle = style;
        }

        @Override
        public void setFont(String fontName, int fontSize, boolean bold, boolean italic) {
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontBold = bold;
            this.fontItalic = italic;

            if (canvas != null) {
                String fontStyle = "";
                if (italic) fontStyle += "italic ";
                if (bold) fontStyle += "bold ";
                canvas.getContext2d().setFont(fontStyle + fontSize + "px " + fontName);
            }
        }

        @Override
        public void drawText(String text, double x, double y) {
            if (canvas != null && bufferCanvas != null) {
                // Draw to buffer canvas instead of main canvas
                bufferCanvas.getContext2d().setFillStyle(drawingColor);

                // Apply font settings to buffer canvas
                String fontStyle = "";
                if (fontItalic) fontStyle += "italic ";
                if (fontBold) fontStyle += "bold ";
                bufferCanvas.getContext2d().setFont(fontStyle + fontSize + "px " + fontName);

                // Draw the text
                bufferCanvas.getContext2d().fillText(text, x, y);
            }
        }

        @Override
        public void setTransform(double scaleX, double scaleY, double translateX, double translateY) {
            if (canvas != null) {
                canvas.getContext2d().setTransform(scaleX, 0, 0, scaleY, translateX, translateY);
            }
        }

        @Override
        public void translate(double dx, double dy) {
            if (canvas != null) {
                canvas.getContext2d().translate(dx, dy);
            }
        }

        @Override
        public void scale(double sx, double sy) {
            if (canvas != null) {
                canvas.getContext2d().scale(sx, sy);
            }
        }

        @Override
        public void rotate(double angle) {
            if (canvas != null) {
                canvas.getContext2d().rotate(angle);
            }
        }

        @Override
        public void resetTransform() {
            if (canvas != null) {
                canvas.getContext2d().setTransform(1, 0, 0, 1, 0, 0);
            }
        }

        @Override
        public void drawPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean filled) {
            if (canvas != null && nPoints > 0) {
                canvas.getContext2d().beginPath();
                canvas.getContext2d().moveTo(xPoints[0], yPoints[0]);

                for (int i = 1; i < nPoints; i++) {
                    canvas.getContext2d().lineTo(xPoints[i], yPoints[i]);
                }

                canvas.getContext2d().closePath();

                if (filled) {
                    canvas.getContext2d().fill();
                } else {
                    canvas.getContext2d().stroke();
                }
            }
        }

        @Override
        public void drawArc(double x, double y, double radius, double startAngle, double arcAngle, boolean filled) {
            if (canvas != null) {
                canvas.getContext2d().beginPath();
                canvas.getContext2d().arc(x, y, radius, startAngle, startAngle + arcAngle);

                if (filled) {
                    canvas.getContext2d().fill();
                } else {
                    canvas.getContext2d().stroke();
                }
            }
        }

        @Override
        public void drawBezier(double x1, double y1, double ctrlX1, double ctrlY1, 
                              double ctrlX2, double ctrlY2, double x2, double y2) {
            if (canvas != null) {
                canvas.getContext2d().beginPath();
                canvas.getContext2d().moveTo(x1, y1);
                canvas.getContext2d().bezierCurveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);
                canvas.getContext2d().stroke();
            }
        }

        @Override
        public void drawPoint(double x, double y) {
            if (canvas != null) {
                double size = lineWidth * 2;
                canvas.getContext2d().beginPath();
                canvas.getContext2d().arc(x, y, size / 2, 0, 2 * Math.PI);
                canvas.getContext2d().fill();
            }
        }

        @Override
        public void drawLine(double x1, double y1, double x2, double y2) {
            if (canvas != null) {
                // For solid lines, use the standard drawing method
                if (lineStyle == 0) {
                    canvas.getContext2d().beginPath();
                    canvas.getContext2d().moveTo(x1, y1);
                    canvas.getContext2d().lineTo(x2, y2);
                    canvas.getContext2d().stroke();
                } else {
                    // For dashed, dotted, or dash-dot lines, draw multiple segments
                    double dx = x2 - x1;
                    double dy = y2 - y1;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double nx = dx / distance;  // normalized direction vector
                    double ny = dy / distance;

                    // Define dash patterns based on line style
                    double[] dashLengths;
                    switch (lineStyle) {
                        case 1: // Dashed
                            dashLengths = new double[] {5, 5};
                            break;
                        case 2: // Dotted
                            dashLengths = new double[] {2, 2};
                            break;
                        case 3: // Dash-dot
                            dashLengths = new double[] {5, 2, 2, 2};
                            break;
                        default:
                            dashLengths = new double[] {5, 5}; // Default to dashed
                    }

                    // Draw the dashed/dotted line
                    double dashSum = 0;
                    for (double len : dashLengths) {
                        dashSum += len;
                    }

                    int numDashes = (int) Math.ceil(distance / dashSum);
                    double currentX = x1;
                    double currentY = y1;
                    boolean draw = true;

                    for (int i = 0; i < numDashes; i++) {
                        for (double len : dashLengths) {
                            if (currentX >= x2 && currentY >= y2) break;

                            double endX = currentX + nx * len;
                            double endY = currentY + ny * len;

                            // Ensure we don't go past the end point
                            if ((nx > 0 && endX > x2) || (nx < 0 && endX < x2) ||
                                (ny > 0 && endY > y2) || (ny < 0 && endY < y2)) {
                                endX = x2;
                                endY = y2;
                            }

                            if (draw) {
                                canvas.getContext2d().beginPath();
                                canvas.getContext2d().moveTo(currentX, currentY);
                                canvas.getContext2d().lineTo(endX, endY);
                                canvas.getContext2d().stroke();
                            }

                            currentX = endX;
                            currentY = endY;
                            draw = !draw;
                        }
                    }
                }
            }
        }

        @Override
        public void drawCircle(double x, double y, double radius) {
            if (canvas != null) {
                canvas.getContext2d().beginPath();
                canvas.getContext2d().arc(x, y, radius, 0, 2 * Math.PI);
                canvas.getContext2d().stroke();
            }
        }

        /**
         * Zooms the canvas around a center point.
         * @param factor The zoom factor (> 1 to zoom in, < 1 to zoom out)
         * @param centerX The x-coordinate of the center point
         * @param centerY The y-coordinate of the center point
         */
        public void zoom(double factor, double centerX, double centerY) {
            // Calculate new zoom factor
            double newZoom = zoomFactor * factor;

            // Limit zoom range (e.g., between 0.1 and 10)
            newZoom = Math.max(0.1, Math.min(10.0, newZoom));

            // Calculate new pan values to zoom around the center point
            panX = centerX - (centerX - panX) * (newZoom / zoomFactor);
            panY = centerY - (centerY - panY) * (newZoom / zoomFactor);

            // Update zoom factor
            zoomFactor = newZoom;

            // Apply transformation and refresh
            applyTransformation();
            refresh();
        }

        /**
         * Pans the canvas by the specified amount.
         * @param dx The x-distance to pan
         * @param dy The y-distance to pan
         */
        public void pan(double dx, double dy) {
            panX += dx;
            panY += dy;
            applyTransformation();
            refresh();
        }

        /**
         * Sets whether to show the grid.
         * @param show True to show the grid, false to hide it
         */
        public void setShowGrid(boolean show) {
            this.showGrid = show;
            refresh();
        }

        /**
         * Sets the grid size.
         * @param size The grid size in pixels
         */
        public void setGridSize(double size) {
            this.gridSize = size;
            if (showGrid) {
                refresh();
            }
        }

        /**
         * Sets the grid color.
         * @param color The grid color in hex format (#RRGGBB)
         */
        public void setGridColor(String color) {
            this.gridColor = color;
            if (showGrid) {
                refresh();
            }
        }

        /**
         * Snaps a coordinate value to the nearest grid point.
         * @param value The coordinate value to snap
         * @return The snapped coordinate value
         */
        public double snapToGrid(double value) {
            return Math.round(value / gridSize) * gridSize;
        }

        /**
         * Handles canvas resize.
         */
        public void onResize() {
            // Get the new size from the parent element
            int width = getOffsetWidth();
            int height = getOffsetHeight();

            // Update canvas size
            canvas.setWidth(width + "px");
            canvas.setHeight(height + "px");
            canvas.setCoordinateSpaceWidth(width);
            canvas.setCoordinateSpaceHeight(height);

            // Update buffer canvas size
            if (bufferCanvas != null) {
                bufferCanvas.setCoordinateSpaceWidth(width);
                bufferCanvas.setCoordinateSpaceHeight(height);
            }

            // Reapply transformation and refresh
            applyTransformation();
            refresh();
        }

        public void setDrawingMode(int mode) {
            this.drawingMode = mode;
        }

        public int getDrawingMode() {
            return drawingMode;
        }
    }
}
