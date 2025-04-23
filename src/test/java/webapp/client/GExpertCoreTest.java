package webapp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import core.ui.GExpertUI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GWT-compatible GExpertCore class.
 */
public class GExpertCoreTest {

    private GExpertCore expertCore;

    @BeforeEach
    public void setUp() {
        // Create instance and initialize the core
        expertCore = new GExpertCore();
        expertCore.init();
    }

    @Test
    public void initShouldInitializeCore() {
        // This test verifies that the core is initialized properly
        // The init method is already called in setUp()
        // We can't directly test the initialization, but we can verify that subsequent operations work
        assertNotNull(expertCore, "GExpertCore should not be null after initialization");
    }

    @Test
    public void loadFileShouldSetCurrentFileName() {
        // Test that loading a file sets the current file name
        String testFileName = "test.gex";
        expertCore.loadFile(testFileName);
        assertEquals(testFileName, expertCore.getCurrentFileName(), 
            "Current file name should match the loaded file name");
    }

    @Test
    public void clearShouldResetCurrentFileName() {
        // Test that clearing the project resets the current file name
        String testFileName = "test.gex";
        expertCore.loadFile(testFileName);
        expertCore.clear();
        assertNull(expertCore.getCurrentFileName(), 
            "Current file name should be null after clearing");
    }

    @Test
    public void addGeometryObjectShouldIncreaseListSize() {
        // Test that adding a geometry object increases the list size
        int initialSize = expertCore.getGeometryObjects().size();
        GExpertCore.Point point = new GExpertCore.Point(10, 20);
        expertCore.addGeometryObject(point);
        assertEquals(initialSize + 1, expertCore.getGeometryObjects().size(),
            "Geometry objects list size should increase by 1");
    }

    @Test
    public void removeGeometryObjectShouldDecreaseListSize() {
        // Test that removing a geometry object decreases the list size
        GExpertCore.Point point = new GExpertCore.Point(10, 20);
        expertCore.addGeometryObject(point);
        int initialSize = expertCore.getGeometryObjects().size();
        expertCore.removeGeometryObject(point);
        assertEquals(initialSize - 1, expertCore.getGeometryObjects().size(),
            "Geometry objects list size should decrease by 1");
    }

    @Test
    public void serializeStateShouldIncludeAllObjects() {
        // Test that serializing the state includes all geometry objects
        expertCore.clear();
        GExpertCore.Point point = new GExpertCore.Point(10, 20);
        point.setName("P1");
        GExpertCore.Line line = new GExpertCore.Line(10, 20, 30, 40);
        line.setName("L1");
        GExpertCore.Circle circle = new GExpertCore.Circle(50, 60, 70);
        circle.setName("C1");

        expertCore.addGeometryObject(point);
        expertCore.addGeometryObject(line);
        expertCore.addGeometryObject(circle);

        String serialized = expertCore.serializeState();

        assertTrue(serialized.contains("POINT,10.0,20.0,P1"), 
            "Serialized state should include the point");
        assertTrue(serialized.contains("LINE,10.0,20.0,30.0,40.0,L1"), 
            "Serialized state should include the line");
        assertTrue(serialized.contains("CIRCLE,50.0,60.0,70.0,C1"), 
            "Serialized state should include the circle");
    }

    @Test
    public void deserializeStateShouldRestoreObjects() {
        // Test that deserializing a state restores all geometry objects
        String serialized = "JGEX_FILE_FORMAT_1.0\n" +
                           "POINT,10.0,20.0,P1\n" +
                           "LINE,10.0,20.0,30.0,40.0,L1\n" +
                           "CIRCLE,50.0,60.0,70.0,C1\n";

        expertCore.clear();
        expertCore.deserializeState(serialized);

        assertEquals(3, expertCore.getGeometryObjects().size(),
            "Should restore 3 geometry objects");

        // Verify the objects were restored correctly
        boolean foundPoint = false;
        boolean foundLine = false;
        boolean foundCircle = false;

        for (GExpertCore.GeometryObject obj : expertCore.getGeometryObjects()) {
            if (obj instanceof GExpertCore.Point) {
                GExpertCore.Point p = (GExpertCore.Point) obj;
                if (p.getX() == 10.0 && p.getY() == 20.0 && "P1".equals(p.getName())) {
                    foundPoint = true;
                }
            } else if (obj instanceof GExpertCore.Line) {
                GExpertCore.Line l = (GExpertCore.Line) obj;
                if (l.getX() == 10.0 && l.getY() == 20.0 && 
                    l.getX2() == 30.0 && l.getY2() == 40.0 && 
                    "L1".equals(l.getName())) {
                    foundLine = true;
                }
            } else if (obj instanceof GExpertCore.Circle) {
                GExpertCore.Circle c = (GExpertCore.Circle) obj;
                if (c.getX() == 50.0 && c.getY() == 60.0 && 
                    c.getRadius() == 70.0 && "C1".equals(c.getName())) {
                    foundCircle = true;
                }
            }
        }

        assertTrue(foundPoint, "Should restore the point correctly");
        assertTrue(foundLine, "Should restore the line correctly");
        assertTrue(foundCircle, "Should restore the circle correctly");
    }

    @Test
    public void saveFileShouldSetCurrentFileNameAndClearModified() {
        // Test that saving a file sets the current file name and clears the modified flag
        String testFileName = "test.gex";
        expertCore.setModified(true);
        expertCore.saveFile(testFileName);
        assertEquals(testFileName, expertCore.getCurrentFileName(), 
            "Current file name should match the saved file name");
        assertFalse(expertCore.isModified(), 
            "Modified flag should be cleared after saving");
    }

    @Test
    public void setModifiedShouldUpdateModifiedFlag() {
        // Test that setting the modified flag works
        expertCore.setModified(true);
        assertTrue(expertCore.isModified(), 
            "Modified flag should be set to true");

        expertCore.setModified(false);
        assertFalse(expertCore.isModified(), 
            "Modified flag should be set to false");
    }

    @Test
    public void removeGeometryObjectShouldRefreshDisplay() {
        // Create a mock UI to track if refreshDisplay is called
        MockUI mockUI = new MockUI();
        expertCore.setUI(mockUI);

        // Add and then remove a geometry object
        GExpertCore.Point point = new GExpertCore.Point(10, 20);
        expertCore.addGeometryObject(point);

        // Reset the mock UI's state before removing the object
        mockUI.resetRefreshDisplayCalled();

        // Remove the object
        expertCore.removeGeometryObject(point);

        // Verify that refreshDisplay was called
        assertTrue(mockUI.wasRefreshDisplayCalled(), 
            "refreshDisplay should be called after removing a geometry object");
    }

    // Mock UI implementation for testing
    private static class MockUI implements GExpertUI {
        private boolean refreshDisplayCalled = false;

        @Override
        public void refreshDisplay() {
            refreshDisplayCalled = true;
        }

        public boolean wasRefreshDisplayCalled() {
            return refreshDisplayCalled;
        }

        public void resetRefreshDisplayCalled() {
            refreshDisplayCalled = false;
        }

        // Implement other required methods with empty implementations
        @Override
        public void updateStatus(String status) {}

        @Override
        public void updateTitle(String title) {}

        @Override
        public void showMessage(String message) {}

        @Override
        public void selectObject(String id) {}

        @Override
        public void highlightObject(String id) {}

        @Override
        public void showTooltip(String text) {}

        @Override
        public void hideTooltip() {}

        @Override
        public void showDialog(String title, String message, String[] buttons, DialogCallback callback) {}

        @Override
        public void showFileDialog(boolean open, String[] extensions, FileDialogCallback callback) {}

        @Override
        public void showInputDialog(String title, String message, String initialValue, InputDialogCallback callback) {}

        @Override
        public void showPropertyDialog(String title, List<String> properties, List<String> values, PropertyDialogCallback callback) {}

        @Override
        public void showColorDialog(String title, String initialColor, ColorDialogCallback callback) {}

        @Override
        public void setDrawingMode(int mode) {}

        @Override
        public void setDrawingCursor(int cursorType) {}

        @Override
        public void zoomToFit() {}

        @Override
        public void setZoom(double zoomFactor) {}

        @Override
        public double getZoom() { return 1.0; }

        @Override
        public DrawingCanvas getDrawingCanvas() { return null; }

        @Override
        public int getDrawingWidth() { return 0; }

        @Override
        public int getDrawingHeight() { return 0; }

        @Override
        public void enableMenuItem(String menuId, boolean enable) {}

        @Override
        public void checkMenuItem(String menuId, boolean check) {}

        @Override
        public void addMenuItem(String parentMenuId, String menuId, String label, String tooltip, Runnable command) {}

        @Override
        public void removeMenuItem(String menuId) {}

        @Override
        public void addToolbarButton(String toolbarId, String buttonId, String label, String tooltip, String iconPath, Runnable command) {}

        @Override
        public void removeToolbarButton(String buttonId) {}

        @Override
        public void setToolbarButtonSelected(String buttonId, boolean selected) {}

        @Override
        public void deselectAllObjects() {}

        @Override
        public void clearHighlights() {}

        @Override
        public void showProofPanel(boolean visible) {}

        @Override
        public void updateProofSteps(List<String> steps) {}

        @Override
        public void showTheoremResult(String theoremName, boolean isProven, String explanation) {}

        @Override
        public void startAnimation(int animationType, int durationMs) {}

        @Override
        public void stopAnimation() {}

        @Override
        public void setAnimationSpeed(double speedFactor) {}
    }
}
