package wprover;

import java.util.Vector;

/**
 * The HelpMode class provides methods to manage and retrieve help items in GeoGebra.
 */
public class HelpMode {

    /**
     * A vector to store help items.
     */
    public static Vector items = new Vector();

    /**
     * Retrieves the help mode filename for the given command.
     *
     * @param comd the command to look up
     * @return the filename associated with the command, or null if not found
     */
    public static String getHelpMode(String comd) {
        buildItems();
        for (int i = 0; i < items.size(); i++) {
            HelpItemAtom it = (HelpItemAtom) items.get(i);
            if (it.comd.equalsIgnoreCase(comd))
                return it.filename;
        }
        return null;
    }

    /**
     * Builds the list of help items if it is not already built.
     */
    public static void buildItems() {
        if (items.size() != 0)
            return;

        addItem("New", "basic_operation.html");
        addItem("Open", "basic_operation.html");
        addItem("Save", "basic_operation.html");
        addItem("Save as Text", "basic_operation.html");
        addItem("Save as PS", "basic_operation.html");
        addItem("Save as PDF", "basic_operation.html");
        addItem("Save as Image", "basic_operation.html");
        addItem("Save as Animated Image", "basic_operation.html");
        addItem("Save Proof as Animated Image", "basic_operation.html");
        addItem("Print", "basic_operation.html");
        addItem("Exit", "basic_operation.html");
        addItem("Point", "point.html");
        addItem("Midpoint", "midpoint.html");

        addItem("Line", "line.html");
        addItem("Parallel", "parallel.html");
        addItem("Perpendicular", "perpendicular.html");

        addItem("Intersection by compass and circle/line", "point_by_point_and_segement.html");
        addItem("Radical of Two Circles", "radical_of_two_circles.html");
        addItem("Oriented Segment", "oriented_segment.html");
        addItem("Oriented T Segment", "oriented_segment.html");
        addItem("Oriented Segment * Ratio", "oriented_segment.html");
        addItem("Proportional Segment", "proportion.html");
        addItem("Circumcenter", "center.html");
        addItem("Centroid", "center.html");
        addItem("Orthocenter", "center.html");
        addItem("Incenter", "center.html");

        addItem("Foot", "foot.html");
        addItem("Angle Bisector", "abline.html");

        addItem("Aline", "aline.html");
        addItem("Bline", "bline.html");
        addItem("TCline", "tcline.html");

        addItem("Circle", "circle.html");
        addItem("Circle by Three Points", "circle_by_three_points.html");
        addItem("Compass", "circle_by_radius.html");

        addItem("Intersect", "intersect.html");
        addItem("Mirror", "mirror.html");

        addItem("Triangle", "polygon.html");
        addItem("Isosceles Triangle", "polygon.html");
        addItem("Equilateral Triangle", "polygon.html");
        addItem("Right-angled Triangle", "polygon.html");
        addItem("Isosceles Right-angled Triangle", "polygon.html");
        addItem("Quadrangle", "polygon.html");
        addItem("Parallelogram", "polygon.html");
        addItem("Trapezoid", "polygon.html");
        addItem("Right-angled Trapezoid", "polygon.html");
        addItem("Rectangle", "polygon.html");
        addItem("Square", "polygon.html");
        addItem("Pentagon", "polygon.html");
        addItem("Polygon", "polygon.html");
        addItem("sangle", "special_angle.html");

        addItem("Eqangle", "constraint.html");
        addItem("Eqangle3p", "constraint.html");
        addItem("Angle Specification", "constraint.html");
        addItem("Equal Ratio", "constraint.html");
        addItem("Equal Distance", "constraint.html");
        addItem("Ratio Distance", "constraint.html");
        addItem("CCtangent", "constraint.html");

        addItem("Trace", "trace.html");
        addItem("Locus", "locus.html");
        addItem("Animation", "animation.html");

        addItem("Fill Polygon", "fillpolygon.html");
        addItem("Measure Distance", "measure_distance.html");
        addItem("Arrow", "arrow.html");

        addItem("Equal Mark", "mark.html");
        addItem("Right-angle Mark", "mark.html");
        addItem("Calculation", "calculation.html");

        addItem("Hide Object", "show_hide.html");
        addItem("Show Object", "show_hide.html");
        addItem("Transform", "transform.html");
        addItem("Equivalence", "Equivalance.html");
        addItem("Free Transform", "free_transform.html");

        addItem("Rules for Full Angle", "lemmas.html#full");
        addItem("Rules for GDD", "lemmas.html#gdd");

        addItem("Preferences", "preference.html");
        addItem("Construct History", "history.html");
        addItem("Show Step Bar", "index.html");
        addItem("Style Dialog", "index.html");

        addItem("Help", "index.html");
        addItem("Online Help", "index.html");
        addItem("Help on Mode", "index.html");
        addItem("JGEX Homepage", "index.html");
        addItem("Contact Us", "index.html");
        addItem("Check for Update", "index.html");
        addItem("About JGEX", "index.html");
    }

    /**
     * Adds a help item to the list.
     *
     * @param comd the command associated with the help item
     * @param name the filename of the help item
     */
    public static void addItem(String comd, String name) {
        HelpItemAtom it = new HelpItemAtom(comd, name);
        items.add(it);
    }
}

/**
 * Represents a help item with a command and a filename.
 */
class HelpItemAtom {
    String comd;
    String filename;

    /**
     * Constructs a new HelpItemAtom with the specified command and filename.
     *
     * @param s1 the command associated with the help item
     * @param s2 the filename of the help item
     */
    public HelpItemAtom(String s1, String s2) {
        comd = s1;
        filename = s2;
    }
}