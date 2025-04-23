package wprover;

import core.GExpertCore;
import core.ui.GExpertUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Swing implementation of the GExpertUI interface.
 * This class provides the desktop UI implementation using Swing components.
 */
public class SwingGExpertUI implements GExpertUI {
    private GExpert gexpert;
    private Map<String, JMenuItem> menuItems = new HashMap<>();
    private Map<String, AbstractButton> toolbarButtons = new HashMap<>();
    private JLabel statusLabel;
    private JLabel tooltipLabel;
    private SwingDrawingCanvas drawingCanvas;
    private Timer tooltipTimer;

    /**
     * Constructor for SwingGExpertUI.
     * 
     * @param gexpert The GExpert instance this UI is associated with
     */
    public SwingGExpertUI(GExpert gexpert) {
        this.gexpert = gexpert;
        this.statusLabel = new JLabel(" ");
        this.tooltipLabel = new JLabel();
        tooltipLabel.setOpaque(true);
        tooltipLabel.setBackground(new Color(255, 255, 225));
        tooltipLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tooltipLabel.setVisible(false);

        // Initialize drawing canvas
        this.drawingCanvas = new SwingDrawingCanvas();

        // Initialize tooltip timer
        tooltipTimer = new Timer(3000, e -> hideTooltip());
        tooltipTimer.setRepeats(false);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(gexpert, message);
    }

    @Override
    public void updateTitle(String title) {
        gexpert.setTitle(title);
    }

    @Override
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void showTooltip(String text) {
        tooltipLabel.setText(text);
        tooltipLabel.setVisible(true);
        tooltipTimer.restart();
    }

    @Override
    public void hideTooltip() {
        tooltipLabel.setVisible(false);
        tooltipTimer.stop();
    }

    @Override
    public void showDialog(String title, String message, String[] buttons, DialogCallback callback) {
        int result = JOptionPane.showOptionDialog(
            gexpert,
            message,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            buttons,
            buttons[0]
        );
        if (callback != null) {
            callback.onButtonClicked(result);
        }
    }

    @Override
    public void showFileDialog(boolean open, String[] extensions, FileDialogCallback callback) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        if (extensions != null && extensions.length > 0) {
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    for (String ext : extensions) {
                        if (name.endsWith("." + ext.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    StringBuilder sb = new StringBuilder("Supported files (");
                    for (int i = 0; i < extensions.length; i++) {
                        sb.append("*.").append(extensions[i]);
                        if (i < extensions.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append(")");
                    return sb.toString();
                }
            });
        }

        int result = open ? 
            fileChooser.showOpenDialog(gexpert) : 
            fileChooser.showSaveDialog(gexpert);

        if (result == JFileChooser.APPROVE_OPTION) {
            callback.onFileSelected(fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            callback.onCancel();
        }
    }

    @Override
    public void showInputDialog(String title, String message, String initialValue, InputDialogCallback callback) {
        String result = JOptionPane.showInputDialog(gexpert, message, title, JOptionPane.QUESTION_MESSAGE);
        if (result != null) {
            callback.onInputSubmitted(result);
        } else {
            callback.onCancel();
        }
    }

    @Override
    public void showPropertyDialog(String title, List<String> properties, List<String> values, PropertyDialogCallback callback) {
        // Create a simple property dialog
        JDialog dialog = new JDialog(gexpert, title, true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[properties.size()];

        for (int i = 0; i < properties.size(); i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            panel.add(new JLabel(properties.get(i) + ":"), gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;
            fields[i] = new JTextField(values.get(i), 20);
            panel.add(fields[i], gbc);
        }

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        gbc.gridx = 1;
        gbc.gridy = properties.size();
        gbc.gridwidth = 1;
        panel.add(okButton, gbc);

        gbc.gridx = 2;
        panel.add(cancelButton, gbc);

        okButton.addActionListener(e -> {
            for (int i = 0; i < properties.size(); i++) {
                values.set(i, fields[i].getText());
            }
            dialog.dispose();
            callback.onPropertiesChanged(properties, values);
        });

        cancelButton.addActionListener(e -> {
            dialog.dispose();
            callback.onCancel();
        });

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(gexpert);
        dialog.setVisible(true);
    }

    @Override
    public void showColorDialog(String title, String initialColor, ColorDialogCallback callback) {
        Color initialColorObj = Color.decode(initialColor);
        Color selectedColor = JColorChooser.showDialog(gexpert, title, initialColorObj);

        if (selectedColor != null) {
            String hexColor = String.format("#%02x%02x%02x", 
                selectedColor.getRed(), 
                selectedColor.getGreen(), 
                selectedColor.getBlue());
            callback.onColorSelected(hexColor);
        } else {
            callback.onCancel();
        }
    }

    @Override
    public void setDrawingMode(int mode) {
        // This would be implemented to set the current drawing mode
        // and update UI elements accordingly
        // For now, we'll just update the canvas
        drawingCanvas.setDrawingMode(mode);
    }

    @Override
    public void refreshDisplay() {
        drawingCanvas.refresh();
        gexpert.repaint();
    }

    @Override
    public void setDrawingCursor(int cursorType) {
        Cursor cursor;
        switch (cursorType) {
            case 0: // Default
                cursor = Cursor.getDefaultCursor();
                break;
            case 1: // Crosshair
                cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                break;
            case 2: // Hand
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                break;
            case 3: // Move
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                break;
            default:
                cursor = Cursor.getDefaultCursor();
        }
        drawingCanvas.setCursor(cursor);
    }

    @Override
    public void zoomToFit() {
        // This would calculate the appropriate zoom level to fit all objects
        // For now, we'll just reset the zoom
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
        return drawingCanvas.getWidth();
    }

    @Override
    public int getDrawingHeight() {
        return drawingCanvas.getHeight();
    }

    @Override
    public void enableMenuItem(String menuId, boolean enable) {
        JMenuItem item = menuItems.get(menuId);
        if (item != null) {
            item.setEnabled(enable);
        }
    }

    @Override
    public void checkMenuItem(String menuId, boolean check) {
        JMenuItem item = menuItems.get(menuId);
        if (item != null && item instanceof JCheckBoxMenuItem) {
            ((JCheckBoxMenuItem) item).setSelected(check);
        }
    }

    @Override
    public void addMenuItem(String parentMenuId, String menuId, String label, String tooltip, Runnable command) {
        JMenuItem item = new JMenuItem(label);
        item.setToolTipText(tooltip);
        item.addActionListener(e -> command.run());
        menuItems.put(menuId, item);

        JMenu parentMenu = (JMenu) menuItems.get(parentMenuId);
        if (parentMenu != null) {
            parentMenu.add(item);
        }
    }

    @Override
    public void removeMenuItem(String menuId) {
        JMenuItem item = menuItems.get(menuId);
        if (item != null) {
            Container parent = item.getParent();
            if (parent != null) {
                parent.remove(item);
                parent.revalidate();
                parent.repaint();
            }
            menuItems.remove(menuId);
        }
    }

    @Override
    public void addToolbarButton(String toolbarId, String buttonId, String label, String tooltip, String iconPath, Runnable command) {
        JToolBar toolbar = (JToolBar) gexpert.getContentPane().getComponent(0);
        JButton button = new JButton();

        if (iconPath != null && !iconPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            button.setIcon(icon);
        } else {
            button.setText(label);
        }

        button.setToolTipText(tooltip);
        button.addActionListener(e -> command.run());

        toolbar.add(button);
        toolbar.revalidate();

        toolbarButtons.put(buttonId, button);
    }

    @Override
    public void removeToolbarButton(String buttonId) {
        AbstractButton button = toolbarButtons.get(buttonId);
        if (button != null) {
            Container parent = button.getParent();
            if (parent != null) {
                parent.remove(button);
                parent.revalidate();
                parent.repaint();
            }
            toolbarButtons.remove(buttonId);
        }
    }

    @Override
    public void setToolbarButtonSelected(String buttonId, boolean selected) {
        AbstractButton button = toolbarButtons.get(buttonId);
        if (button != null) {
            button.setSelected(selected);
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
    public JLabel getStatusLabel() {
        return statusLabel;
    }

    /**
     * Gets the tooltip label component.
     * @return The tooltip label
     */
    public JLabel getTooltipLabel() {
        return tooltipLabel;
    }

    /**
     * Swing implementation of the DrawingCanvas interface.
     */
    private class SwingDrawingCanvas extends JPanel implements DrawingCanvas {
        private Color backgroundColor = Color.WHITE;
        private Color drawingColor = Color.BLACK;
        private float lineWidth = 1.0f;
        private Font font = new Font("SansSerif", Font.PLAIN, 12);
        private int drawingMode = 0;
        private int lineStyle = 0; // 0=solid, 1=dashed, 2=dotted, 3=dash-dot

        public SwingDrawingCanvas() {
            setBackground(backgroundColor);
            setPreferredSize(new Dimension(800, 600));

            // Add mouse listeners
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseDown(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseUp(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseMove(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseWheel(e.getX(), e.getY(), e.getPreciseWheelRotation());
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseEnter(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    for (MouseListener listener : mouseListeners) {
                        listener.onMouseExit(e.getX(), e.getY());
                    }
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
            addMouseWheelListener(mouseAdapter);

            // Add keyboard listeners
            KeyAdapter keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    for (KeyboardListener listener : keyboardListeners) {
                        listener.onKeyDown(e.getKeyCode(), e.getKeyChar());
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    for (KeyboardListener listener : keyboardListeners) {
                        listener.onKeyUp(e.getKeyCode(), e.getKeyChar());
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    for (KeyboardListener listener : keyboardListeners) {
                        listener.onKeyTyped(e.getKeyChar());
                    }
                }
            };

            addKeyListener(keyAdapter);
            setFocusable(true);
        }

        private java.util.List<MouseListener> mouseListeners = new java.util.ArrayList<>();
        private java.util.List<KeyboardListener> keyboardListeners = new java.util.ArrayList<>();

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
            Graphics g = getGraphics();
            if (g != null) {
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        }

        @Override
        public void refresh() {
            repaint();
        }

        @Override
        public void setBackgroundColor(String colorHex) {
            backgroundColor = Color.decode(colorHex);
            setBackground(backgroundColor);
            repaint();
        }

        @Override
        public void setDrawingColor(String colorHex) {
            drawingColor = Color.decode(colorHex);
        }

        @Override
        public void setLineWidth(float width) {
            this.lineWidth = width;
        }

        @Override
        public void setFont(String fontName, int fontSize, boolean bold, boolean italic) {
            int style = Font.PLAIN;
            if (bold) style |= Font.BOLD;
            if (italic) style |= Font.ITALIC;
            this.font = new Font(fontName, style, fontSize);
        }

        @Override
        public void drawText(String text, double x, double y) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                g.setFont(font);
                g.drawString(text, (float) x, (float) y);
                g.dispose();
            }
        }

        @Override
        public void setTransform(double scaleX, double scaleY, double translateX, double translateY) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setTransform(new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY));
                g.dispose();
            }
        }

        @Override
        public void translate(double dx, double dy) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                AffineTransform at = g.getTransform();
                at.translate(dx, dy);
                g.setTransform(at);
                g.dispose();
            }
        }

        @Override
        public void scale(double sx, double sy) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                AffineTransform at = g.getTransform();
                at.scale(sx, sy);
                g.setTransform(at);
                g.dispose();
            }
        }

        @Override
        public void rotate(double angle) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                AffineTransform at = g.getTransform();
                at.rotate(angle);
                g.setTransform(at);
                g.dispose();
            }
        }

        @Override
        public void resetTransform() {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setTransform(new AffineTransform());
                g.dispose();
            }
        }

        @Override
        public void drawPolygon(double[] xPoints, double[] yPoints, int nPoints, boolean filled) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                g.setStroke(new BasicStroke(lineWidth));

                int[] xPointsInt = new int[nPoints];
                int[] yPointsInt = new int[nPoints];

                for (int i = 0; i < nPoints; i++) {
                    xPointsInt[i] = (int) xPoints[i];
                    yPointsInt[i] = (int) yPoints[i];
                }

                if (filled) {
                    g.fillPolygon(xPointsInt, yPointsInt, nPoints);
                } else {
                    g.drawPolygon(xPointsInt, yPointsInt, nPoints);
                }

                g.dispose();
            }
        }

        @Override
        public void drawArc(double x, double y, double radius, double startAngle, double arcAngle, boolean filled) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                g.setStroke(new BasicStroke(lineWidth));

                int diameter = (int) (radius * 2);
                int startDegrees = (int) Math.toDegrees(startAngle);
                int arcDegrees = (int) Math.toDegrees(arcAngle);

                if (filled) {
                    g.fillArc((int) (x - radius), (int) (y - radius), diameter, diameter, startDegrees, arcDegrees);
                } else {
                    g.drawArc((int) (x - radius), (int) (y - radius), diameter, diameter, startDegrees, arcDegrees);
                }

                g.dispose();
            }
        }

        @Override
        public void drawBezier(double x1, double y1, double ctrlX1, double ctrlY1, 
                              double ctrlX2, double ctrlY2, double x2, double y2) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                g.setStroke(new BasicStroke(lineWidth));

                java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
                path.moveTo(x1, y1);
                path.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);

                g.draw(path);
                g.dispose();
            }
        }

        @Override
        public void drawPoint(double x, double y) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                int size = (int) (lineWidth * 2);
                g.fillOval((int) (x - size/2), (int) (y - size/2), size, size);
                g.dispose();
            }
        }

        @Override
        public void drawLine(double x1, double y1, double x2, double y2) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);

                // Set stroke based on line style
                switch (lineStyle) {
                    case 1: // Dashed
                        float[] dash = {10.0f, 5.0f};
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                        break;
                    case 2: // Dotted
                        float[] dot = {2.0f, 2.0f};
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dot, 0.0f));
                        break;
                    case 3: // Dash-dot
                        float[] dashDot = {10.0f, 5.0f, 2.0f, 5.0f};
                        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashDot, 0.0f));
                        break;
                    default: // Solid
                        g.setStroke(new BasicStroke(lineWidth));
                        break;
                }

                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                g.dispose();
            }
        }

        @Override
        public void drawCircle(double x, double y, double radius) {
            Graphics2D g = (Graphics2D) getGraphics();
            if (g != null) {
                g.setColor(drawingColor);
                g.setStroke(new BasicStroke(lineWidth));
                int diameter = (int) (radius * 2);
                g.drawOval((int) (x - radius), (int) (y - radius), diameter, diameter);
                g.dispose();
            }
        }

        public void setDrawingMode(int mode) {
            this.drawingMode = mode;
        }

        public int getDrawingMode() {
            return drawingMode;
        }

        @Override
        public void setLineStyle(int style) {
            this.lineStyle = style;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Custom painting would be done here
        }
    }
}
