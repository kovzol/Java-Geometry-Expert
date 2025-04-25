package wprover;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import com.google.gwt.editor.client.Editor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GExpertTest {

    private GExpert expert;

    @BeforeEach
    public void setUp() {
        // Create instance and initialize the application
        expert = new GExpert();
        expert.init();
    }

    @Editor.Ignore
    @Test
    public void contentPanelShouldNotBeNull() {
        JComponent content = expert.getContent();
        assertNotNull(content, "Content panel should not be null");
    }

    @Editor.Ignore
    @Test
    public void shouldReturnNonNullFileChooser() {
        JFileChooser chooser = expert.getFileChooser(false);
        assertNotNull(chooser, "File chooser should not be null");
    }

    @Editor.Ignore
    @Test
    public void clearOperationShouldReturnZero() {
        // Clear returns 0 on success
        int result = expert.Clear();
        assertEquals(0, result, "Clear operation should return 0");
    }

}
