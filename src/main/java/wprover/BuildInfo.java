package wprover;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class BuildInfo {
    private static final Properties PROPS = new Properties();
    private static final String projectName = "Java Geometry Expert";

    static {
        try (InputStream in = BuildInfo.class.getClassLoader().getResourceAsStream("build.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (Exception ignored) {
        }
    }

    public static String getBuildTime() {
        return PROPS.getProperty("build.time", "Unknown");
    }

    public static String getVersion() {
        return PROPS.getProperty("build.version", "Unknown");
    }

    public static String getRepo() {
        return PROPS.getProperty("build.repo", "Unknown");
    }

    public static String getRepoUrl() {
        String repo = PROPS.getProperty("build.repo", "Unknown");
        if (Objects.equals(repo, "Unknown")){
            return "Unknown";
        }

        return "https://github.com/"+repo;
    }

    public static String getBuildDate() {
        return PROPS.getProperty("build.date", "Unknown");
    }

    public static String getProjectName(){
        return projectName;
    }
}