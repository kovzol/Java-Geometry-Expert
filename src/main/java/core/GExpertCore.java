package core;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import core.ui.GExpertUI;

public class GExpertCore {
    private String currentFileName;
    private Locale currentLocale;
    private boolean modified = false;
    private GExpertUI ui;

    // Geometry state
    private List<GeometryObject> geometryObjects = new ArrayList<>();

    public GExpertCore() {
        this.currentLocale = Locale.getDefault();
    }

    public void setUI(GExpertUI ui) {
        this.ui = ui;
    }

    public GExpertUI getUI() {
        return ui;
    }

    // Initialization
    public void init() {
        loadPreference();
        loadLanguage();
        loadRules();
        System.out.println("GExpertCore initialized.");
    }

    // Abstract class for geometry objects
    public static abstract class GeometryObject {
        protected double x, y;
        protected String name;
        protected boolean visible = true;

        public GeometryObject(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public abstract void draw(DrawingContext context);
    }

    // Point geometry object
    public static class Point extends GeometryObject {
        public Point(double x, double y) {
            super(x, y);
        }

        @Override
        public void draw(DrawingContext context) {
            context.drawPoint(x, y);
        }
    }

    // Line geometry object
    public static class Line extends GeometryObject {
        private double x2, y2;

        public Line(double x1, double y1, double x2, double y2) {
            super(x1, y1);
            this.x2 = x2;
            this.y2 = y2;
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        @Override
        public void draw(DrawingContext context) {
            context.drawLine(x, y, x2, y2);
        }
    }

    // Circle geometry object
    public static class Circle extends GeometryObject {
        private double radius;

        public Circle(double x, double y, double radius) {
            super(x, y);
            this.radius = radius;
        }

        public double getRadius() {
            return radius;
        }

        @Override
        public void draw(DrawingContext context) {
            context.drawCircle(x, y, radius);
        }
    }

    // Drawing context interface
    public interface DrawingContext {
        void drawPoint(double x, double y);
        void drawLine(double x1, double y1, double x2, double y2);
        void drawCircle(double x, double y, double radius);
    }

    // Geometry operations
    public void addGeometryObject(GeometryObject object) {
        geometryObjects.add(object);
        setModified(true);
        if (ui != null) {
            ui.updateStatus("Added " + object.getClass().getSimpleName());
        }
    }

    public void removeGeometryObject(GeometryObject object) {
        geometryObjects.remove(object);
        setModified(true);
        if (ui != null) {
            ui.updateStatus("Removed " + object.getClass().getSimpleName());
        }
    }

    public List<GeometryObject> getGeometryObjects() {
        return geometryObjects;
    }

    public void drawAll(DrawingContext context) {
        for (GeometryObject obj : geometryObjects) {
            if (obj.isVisible()) {
                obj.draw(context);
            }
        }
    }

    // State management
    public void setModified(boolean modified) {
        this.modified = modified;
        updateTitle();
    }

    public boolean isModified() {
        return modified;
    }

    // Added placeholder for gettext style translation
    public String getTranslationViaGettext(String s, String... p) {
        if (p != null && p.length > 0) {
            // Simple concatenation for placeholder
            return s + " " + String.join(" ", p);
        }
        return getTranslation(s);
    }

    // Added to mimic opening a file as in GExpert
    public void openAFile(String file) {
        loadFile(file);
    }

    // Added to mimic saving file as in GExpert
    public void saveAFile(String file) throws Exception {
        saveFile(file);
    }

    // Updates the title based on the current file name and modified state
    public void updateTitle() {
        String title = "Java Geometry Expert";
        if (currentFileName != null) {
            title += " - " + currentFileName;
            if (modified) {
                title += " *";
            }
        }

        if (ui != null) {
            ui.updateTitle(title);
        } else {
            System.out.println("Title: " + title);
        }
    }

    // Clears the current project
    public void clearProject() {
        clear();
    }

    // File operations interface for platform-specific implementations
    public interface FileOperations {
        String loadFile(String filePath);
        boolean saveFile(String filePath, String content);
        boolean fileExists(String filePath);
        String[] listFiles(String directory);
    }

    private FileOperations fileOperations;

    public void setFileOperations(FileOperations fileOperations) {
        this.fileOperations = fileOperations;
    }

    // Serializes the current geometry state to a string
    public String serializeState() {
        StringBuilder sb = new StringBuilder();
        sb.append("JGEX_FILE_FORMAT_1.0\n");

        // Serialize geometry objects
        for (GeometryObject obj : geometryObjects) {
            if (obj instanceof Point) {
                Point p = (Point) obj;
                sb.append("POINT,").append(p.getX()).append(",").append(p.getY());
                if (p.getName() != null) {
                    sb.append(",").append(p.getName());
                }
                sb.append("\n");
            } else if (obj instanceof Line) {
                Line l = (Line) obj;
                sb.append("LINE,").append(l.getX()).append(",").append(l.getY())
                  .append(",").append(l.getX2()).append(",").append(l.getY2());
                if (l.getName() != null) {
                    sb.append(",").append(l.getName());
                }
                sb.append("\n");
            } else if (obj instanceof Circle) {
                Circle c = (Circle) obj;
                sb.append("CIRCLE,").append(c.getX()).append(",").append(c.getY())
                  .append(",").append(c.getRadius());
                if (c.getName() != null) {
                    sb.append(",").append(c.getName());
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Deserializes a string into geometry state
    public void deserializeState(String content) {
        if (content == null || content.isEmpty()) {
            return;
        }

        // Clear existing objects
        geometryObjects.clear();

        // GWT-compatible string parsing
        int lineStart = 0;
        int lineEnd = content.indexOf('\n', lineStart);
        boolean isFirstLine = true;

        while (lineEnd >= 0 || lineStart < content.length()) {
            String line;
            if (lineEnd >= 0) {
                line = content.substring(lineStart, lineEnd).trim();
                lineStart = lineEnd + 1;
                lineEnd = content.indexOf('\n', lineStart);
            } else {
                line = content.substring(lineStart).trim();
                lineStart = content.length();
            }

            if (line.isEmpty()) continue;

            if (isFirstLine) {
                isFirstLine = false;
                // Check if it's a header line (starts with JGEX_FILE_FORMAT)
                if (line.length() >= 16 && line.substring(0, 16).equals("JGEX_FILE_FORMAT")) {
                    continue;  // Skip header
                }
            }

            // Parse the line (comma-separated values)
            ArrayList<String> parts = new ArrayList<String>();
            int partStart = 0;
            int partEnd = line.indexOf(',', partStart);

            while (partEnd >= 0 || partStart < line.length()) {
                if (partEnd >= 0) {
                    parts.add(line.substring(partStart, partEnd));
                    partStart = partEnd + 1;
                    partEnd = line.indexOf(',', partStart);
                } else {
                    parts.add(line.substring(partStart));
                    partStart = line.length();
                }
            }

            if (parts.isEmpty()) continue;

            String type = parts.get(0);
            if ("POINT".equals(type) && parts.size() >= 3) {
                try {
                    double x = Double.parseDouble(parts.get(1));
                    double y = Double.parseDouble(parts.get(2));
                    Point p = new Point(x, y);
                    if (parts.size() > 3) {
                        p.setName(parts.get(3));
                    }
                    geometryObjects.add(p);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing point: " + line);
                }
            } else if ("LINE".equals(type) && parts.size() >= 5) {
                try {
                    double x1 = Double.parseDouble(parts.get(1));
                    double y1 = Double.parseDouble(parts.get(2));
                    double x2 = Double.parseDouble(parts.get(3));
                    double y2 = Double.parseDouble(parts.get(4));
                    Line l = new Line(x1, y1, x2, y2);
                    if (parts.size() > 5) {
                        l.setName(parts.get(5));
                    }
                    geometryObjects.add(l);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                }
            } else if ("CIRCLE".equals(type) && parts.size() >= 4) {
                try {
                    double x = Double.parseDouble(parts.get(1));
                    double y = Double.parseDouble(parts.get(2));
                    double radius = Double.parseDouble(parts.get(3));
                    Circle c = new Circle(x, y, radius);
                    if (parts.size() > 4) {
                        c.setName(parts.get(4));
                    }
                    geometryObjects.add(c);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing circle: " + line);
                }
            }
        }

        setModified(false);
        if (ui != null) {
            ui.updateStatus("Loaded " + geometryObjects.size() + " objects");
        }
    }

    // Loads a file using the platform-specific file operations
    public void loadFile(String file) {
        if (fileOperations != null) {
            String content = fileOperations.loadFile(file);
            if (content != null) {
                deserializeState(content);
                this.currentFileName = file;
                setModified(false);
                if (ui != null) {
                    ui.updateStatus("Loaded file: " + file);
                }
            } else {
                if (ui != null) {
                    ui.showMessage("Failed to load file: " + file);
                }
                System.err.println("Failed to load file: " + file);
            }
        } else {
            // Fallback for when file operations are not available
            this.currentFileName = file;
            if (ui != null) {
                ui.updateStatus("Loaded file: " + file + " (simulated)");
            }
            System.out.println("Loaded file: " + file + " (simulated)");
        }
    }

    // Saves a file using the platform-specific file operations
    public void saveFile(String file) {
        if (fileOperations != null) {
            String content = serializeState();
            boolean success = fileOperations.saveFile(file, content);
            if (success) {
                this.currentFileName = file;
                setModified(false);
                if (ui != null) {
                    ui.updateStatus("Saved file: " + file);
                }
            } else {
                if (ui != null) {
                    ui.showMessage("Failed to save file: " + file);
                }
                System.err.println("Failed to save file: " + file);
            }
        } else {
            // Fallback for when file operations are not available
            this.currentFileName = file;
            setModified(false);
            if (ui != null) {
                ui.updateStatus("Saved file: " + file + " (simulated)");
            }
            System.out.println("Saved file: " + file + " (simulated)");
        }
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        System.out.println("Locale set to: " + locale.toString());
    }

    public Locale getLocale() {
        return currentLocale;
    }

    // Returns the user working directory
    public String getUserDir() {
        return "/";  // GWT-compatible placeholder
    }

    // Returns the user home directory
    public String getUserHome() {
        return "/";  // GWT-compatible placeholder
    }

    // Returns the file separator
    public String getFileSeparator() {
        return "/";  // GWT-compatible placeholder
    }

    // Placeholder for loading rules (from GExpert.loadRules)
    public void loadRules() {
        // TODO: Implement rule loading logic
        System.out.println("Rules loaded.");
    }

    // Placeholder for loading preferences (from GExpert.loadPreference)
    public void loadPreference() {
        // TODO: Implement preference loading logic; in desktop version, this may read a config file
        System.out.println("Preferences loaded.");
    }

    // Placeholder for loading language settings (from GExpert.loadLanguage)
    public void loadLanguage() {
        // TODO: Integrate with a translation mechanism if needed
        System.out.println("Language loaded: " + currentLocale.toString());
    }

    // Clears the current project state
    public void clear() {
        currentFileName = null;
        geometryObjects.clear();
        setModified(false);
        if (ui != null) {
            ui.updateStatus("Project cleared");
        } else {
            System.out.println("Cleared current state.");
        }
    }

    // Dummy translation method using the core locale
    public String getTranslation(String key) {
        // In a full implementation, this would lookup a resource bundle
        return key;
    }
}
