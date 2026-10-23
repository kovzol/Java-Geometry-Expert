package wprover;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class BuildInfo {
    private static final Properties PROPS = new Properties();
    private static final String PROJECT_NAME = "Java Geometry Expert";

    static {
        // BuildInfo.class.getResourceAsStream looks in the same package directory (wprover/)
        try (InputStream in = BuildInfo.class.getResourceAsStream("buildinfo.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (Exception ignored) {
        }
    }

    public static String getVersion() {
        return PROPS.getProperty("version", "Unknown");
    }

    public static String getBuildDate() {
        return PROPS.getProperty("buildDate", "Unknown");
    }

    public static String getRepo() {
        return PROPS.getProperty("repo", "Unknown");
    }

    public static String getRepoUrl() {
        String repo = getRepo();
        if (Objects.equals(repo, "Unknown")) {
            return "Unknown";
        }
        return "https://github.com/" + repo;
    }

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public static String getProjectNameAndVersion() {
        return getProjectName() + " " + getVersion();
    }
}