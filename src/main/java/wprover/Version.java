package wprover;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Version is a class that provides version information for the Geometry Expert project.
 * It includes methods to retrieve the version number, project name, and release date.
 */
public class Version {

    private static final String version;
    private static final String buildDate;
    private static final String project = "Geometry Expert";

    static {
        Properties props = new Properties();
        try (InputStream in = Version.class.getResourceAsStream("/wprover/version.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }
        version = props.getProperty("version", "unknown");
        buildDate = props.getProperty("buildDate", "unknown");
    }

/**
 * Returns the version as a string.
 *
 * @return the version string
 */
public static String getVersion1() {
    return version;
}

/**
 * Returns the project name and version as a single string.
 *
 * @return the project name and version string
 */
public static String getNameAndVersion() {
    return project + " " + version;
}

/**
 * Returns the project name surrounded by spaces.
 *
 * @return the project name with surrounding spaces
 */
public static String getVersion() {
    return " " + project + " ";
}

/**
 * Returns the project name.
 *
 * @return the project name
 */
public static String getProject() {
    return project;
}

/**
 * Returns the build date as a string.
 *
 * @return the date string
 */
public static String getBuildDate() {
    return buildDate;
}
}
