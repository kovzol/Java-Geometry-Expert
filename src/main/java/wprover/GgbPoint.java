package wprover;

import java.util.Objects;

/**
 * Represents a point in GeoGebra with a name.
 */
public class GgbPoint {
    private String name;

    /**
     * Constructs a new GgbPoint with the specified name.
     *
     * @param name the name of the point
     */
    public GgbPoint(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the point.
     *
     * @return the name of the point
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GgbPoint ggbPoint = (GgbPoint) o;
        return Objects.equals(name, ggbPoint.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
