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

    public static String getRepoPagesUrl() {
        String repo = getRepo();
        if (Objects.equals(repo, "Unknown")) {
            return "Unknown";
        }

        String[] parts = repo.split("/", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            return "Unknown";
        }

        String owner = parts[0];
        String repoName = parts[1];

        return "https://" + owner + ".github.io/" + repoName + "/";
    }

    public static String getProjectName() {
        return PROJECT_NAME;
    }

    public static String getProjectNameAndVersion() {
        return getProjectName() + " " + getVersion();
    }
}