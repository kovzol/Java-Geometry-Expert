package wprover;

import java.util.Objects;

/**
 * Represents a circle in GeoGebra with a name.
 */
public class GgbCircle {
    private String name;

    /**
     * Constructs a new GgbCircle with the specified name.
     *
     * @param name the name of the circle
     */
    public GgbCircle(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the circle.
     *
     * @return the name of the circle
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GgbCircle ggbCircle = (GgbCircle) o;
        return Objects.equals(name, ggbCircle.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
