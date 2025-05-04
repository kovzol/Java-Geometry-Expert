package wprover;

import java.util.Objects;
/**
 * Represents a line in GeoGebra with a name and two point names.
 */
public class GgbLine {
    private String name;
    private String nameP1;
    private String nameP2;

    /**
     * Constructs a new GgbLine with the specified name and point names.
     *
     * @param name the name of the line
     * @param nameP1 the name of the first point
     * @param nameP2 the name of the second point
     */
    public GgbLine(String name, String nameP1, String nameP2) {
        this.name = name;
        this.nameP1 = nameP1;
        this.nameP2 = nameP2;
    }

    /**
     * Constructs a new GgbLine with the specified name.
     *
     * @param name the name of the line
     */
    public GgbLine(String name) {
        this.name=name;
    }

    /**
     * Returns the name of the line.
     *
     * @return the name of the line
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the first point.
     *
     * @return the name of the first point
     */
    public String getNameP1() {
        return nameP1;
    }

    /**
     * Returns the name of the second point.
     *
     * @return the name of the second point
     */
    public String getNameP2() {
        return nameP2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GgbLine ggbLine = (GgbLine) o;
        return Objects.equals(name, ggbLine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
