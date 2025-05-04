package wprover;

import java.util.Objects;

/**
 * Represents a midpoint in GeoGebra with a label and optionally associated points or a segment.
 */
public class GgbMidpoint {
    private String labelMidpoint = "";
    private String labelP1;
    private String labelP2;
    private String labelSegment;

    /**
     * Constructs a new GgbMidpoint with the specified midpoint label and point labels.
     *
     * @param labelMidpoint the label of the midpoint
     * @param labelP1       the label of the first point
     * @param labelP2       the label of the second point
     */
    public GgbMidpoint(String labelMidpoint, String labelP1, String labelP2) {
        this.labelMidpoint = labelMidpoint;
        this.labelP1 = labelP1;
        this.labelP2 = labelP2;
    }

    /**
     * Constructs a new GgbMidpoint with the specified midpoint label and segment label.
     *
     * @param labelMidpoint the label of the midpoint
     * @param labelSegment  the label of the segment
     */
    public GgbMidpoint(String labelMidpoint, String labelSegment) {
        this.labelMidpoint = labelMidpoint;
        this.labelSegment = labelSegment;
    }

    /**
     * Constructs a new GgbMidpoint with the specified name.
     *
     * @param name the name of the midpoint
     */
    public GgbMidpoint(String name) {
        this.labelMidpoint = name;
    }

    /**
     * Returns the name of the midpoint.
     *
     * @return the name of the midpoint
     */
    public String getName() {
        return labelMidpoint;
    }

    /**
     * Returns the name of the first point.
     *
     * @return the name of the first point
     */
    public String getNameP1() {
        return labelP1;
    }

    /**
     * Returns the name of the second point.
     *
     * @return the name of the second point
     */
    public String getNameP2() {
        return labelP2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GgbMidpoint that = (GgbMidpoint) o;
        return labelMidpoint.equals(that.labelMidpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelMidpoint);
    }
}
