package wprover;


public class Version {

    private static String sversion = "0.83";
    private static String data = "2024-03-21";
    private static String project = "Geometry Expert";
    private static float version = Float.parseFloat(sversion);


    public static final float getVersionf() {
        return version;
    }

    public static final String getVersion1() {
        return sversion;
    }

    public static final String getNameAndVersion() {
        return project + " " + sversion;
    }

    public static final String getVersion() {
        return " " + project + " ";
    }

    public static final String getProject() {
        return project;
    }

    public static final String getData() {
        return data;
    }
}
