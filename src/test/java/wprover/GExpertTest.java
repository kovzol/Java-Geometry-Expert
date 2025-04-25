package wprover;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("disabled because github can't execute this type of tests remotely")
public class GExpertTest {

    private GExpert expert;

    @BeforeEach
    public void setUp() {
        // Create instance and initialize the application
        expert = new GExpert();
        expert.init();
    }


    @Test
    public void contentPanelShouldNotBeNull() {
        JComponent content = expert.getContent();
        assertNotNull(content, "Content panel should not be null");
    }


    @Test
    public void shouldReturnNonNullFileChooser() {
        JFileChooser chooser = expert.getFileChooser(false);
        assertNotNull(chooser, "File chooser should not be null");
    }

    @Test
    public void clearOperationShouldReturnZero() {
        // Clear returns 0 on success
        int result = expert.Clear();
        assertEquals(0, result, "Clear operation should return 0");
    }

}
