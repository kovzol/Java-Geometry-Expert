package core;

import com.google.gwt.editor.client.Editor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class GExpertCoreTest {

    private GExpertCore expertCore;

    @BeforeEach
    public void setUp() {
        // Create instance and initialize the core
        expertCore = new GExpertCore();
        expertCore.init();
    }

    @Editor.Ignore
    @Test
    public void initShouldInitializeCore() {
        // This test verifies that the core is initialized properly
        // The init method is already called in setUp()
        assertNotNull(expertCore.getLocale(), "Locale should not be null after initialization");
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
    public void setLocaleShouldChangeCurrentLocale() {
        // Test that setting a locale changes the current locale
        Locale testLocale = Locale.FRENCH;
        expertCore.setLocale(testLocale);
        assertEquals(testLocale, expertCore.getLocale(), 
            "Current locale should match the set locale");
    }

    @Test
    public void getUserDirShouldReturnNonEmptyString() {
        // Test that getUserDir returns a non-empty string
        String userDir = expertCore.getUserDir();
        assertNotNull(userDir, "User directory should not be null");
        assertFalse(userDir.isEmpty(), "User directory should not be empty");
    }
}