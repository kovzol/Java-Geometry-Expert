package wprover;

/**
 * Version is a class that provides version information for the Geometry Expert project.
 * It includes methods to retrieve the version number, project name, and release date.
 */
public class Version {

    private static String sversion = "0.86";
    private static String data = "2024-11-26";
    private static String project = "Geometry Expert";

/**
 * Returns the version as a string.
 *
 * @return the version string
 */
public static final String getVersion1() {
    return sversion;
}

/**
 * Returns the project name and version as a single string.
 *
 * @return the project name and version string
 */
public static final String getNameAndVersion() {
    return project + " " + sversion;
}

/**
 * Returns the project name surrounded by spaces.
 *
 * @return the project name with surrounding spaces
 */
public static final String getVersion() {
    return " " + project + " ";
}

/**
 * Returns the project name.
 *
 * @return the project name
 */
public static final String getProject() {
    return project;
}

/**
 * Returns the date as a string.
 *
 * @return the date string
 */
public static final String getData() {
    return data;
}
}
