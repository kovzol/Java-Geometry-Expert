package wprover;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
/**
 * The `AnimateC` class represents the animation control for a geometric object in the GExpert application.
 * It provides methods to animate a point along a line, circle, or trace path.
 */
public class AnimateC {

    double bx, by;

    double minwd = 0;
    double minht = 0;
    double width = 0;
    double height = 0;

    CPoint pA = null;
    double x, y;

    Object onObj = null;
    int onType = 0; // 1:line 2:cirlce 3:trace   // 4: prove


    double dx, dy; // for line

    double csa, sia; // for circle

    double gap = CMisc.ANIMATE_GAP;

    int pindex = 0;
    int step_time = 0; // for trace;

    double delta = 0.05;

    /**
     * Copy constructor for `AnimateC`.
     *
     * @param n The `AnimateC` instance to copy.
     */
    public AnimateC(AnimateC n) {
        bx = n.bx;
        by = n.by;
        width = n.width;
        height = n.height;
        pA = n.pA;
        x = n.x;
        y = n.y;
        onObj = n.onObj;
        onType = n.onType;
        dx = n.dx;
        dy = n.dy;
        csa = n.csa;
        sia = n.sia;
        gap = n.gap;
        pindex = n.pindex;
        step_time = n.step_time;
        delta = n.delta;
    }


    /**
     * Generates a string representation of the animation settings.
     *
     * @return A string representation of the animation settings.
     */
    String getAnimationString() {
        if (pA == null) {
            return null;
        }

        String son = "\nANI " + pA.toString();

        if (onType == 1) {
            CLine ln = (CLine) onObj;
            CPoint pl[] = ln.getTowSideOfLine();
            if (pl == null) {
                return null;
            }
            son += " LN(" + pl[0] + "," + pl[1] + ") " + (int) gap;
            return son;
        } else if (onType == 2) {
            Circle c = (Circle) onObj;
            CPoint p = c.getSidePoint();
            if (c.o == null || p == null) {
                return null;
            }
            son += " CR(" + c.o + "," + p + ") " + (int) gap;
            return son;
        }
        return null;
    }

    /**
     * Loads animation settings from a string.
     *
     * @param s The string containing animation settings.
     * @param dp The `DrawTextProcess` instance to use for finding points and objects.
     * @return true if the animation settings were loaded successfully, false otherwise.
     */
    public boolean loadAnimationString(String s, DrawTextProcess dp) {
        if (!s.startsWith("ANI")) {
            return false;
        }
        s = s.substring(4);
        if (s == null) {
            return false;
        }
        s = s.trim();
        int index = 0;
        int len = s.length();

        char c = s.charAt(index);
        String name = "";
        while (c != ' ') {
            name += c;
            index++;
            if (index >= len) {
                return false;
            }
            c = s.charAt(index);
        }
        String s1 = "";
        while (c != '(') {
            s1 += c;
            index++;
            if (index >= len) {
                return false;
            }
            c = s.charAt(index);
        }
        s1 = s1.trim();

        String sx = "";
        index++;
        if (index >= len) {
            return false;
        }
        c = s.charAt(index);
        while (c != ',') {
            sx += c;
            index++;
            if (index >= len) {
                return false;
            }
            c = s.charAt(index);
        }
        String sy = "";
        index++;
        if (index >= len) {
            return false;
        }
        c = s.charAt(index);
        while (c != ')') {
            sy += c;
            index++;
            if (index >= len) {
                return false;
            }
            c = s.charAt(index);
        }
        String loc = "";
        c = s.charAt(++index);
        while (index < len) {
            loc += s.charAt(index++);
        }
        CPoint p1 = dp.findPoint(name);
        CPoint p2 = dp.findPoint(sx.trim());
        CPoint p3 = dp.findPoint(sy.trim());
        int n = Integer.parseInt(loc.trim());
        Object obj = null;
        if (s1.equals("LN")) {
            {
                obj = dp.fd_line(p2, p3);
                if (obj == null) {
                    CLine ln = new CLine(p2, p3);
                    dp.addLineToList(ln);
                    obj = ln;
                }
                onType = 1;
            }
        } else if (s1.equals("CR")) {
            {
                obj = dp.fd_circle(p2, p3);
                if (obj == null) {
                    Circle c1 = new Circle(p2, p3);
                    dp.addCircleToList(c1);
                    obj = c1;
                }
                onType = 2;
            }
        }
        if (p1 == null || p2 == null || p3 == null || obj == null) return false;
        pA = p1;
        onObj = obj;
        gap = n;
        return true;
    }

    /**
     * Sets the animation step value.
     *
     * @param step The step value to set.
     */
    void Setstep(double step) {
        gap = step + delta;

        if (onType == 3) {
            gap = step;
            step_time = (int) (1000 / step);
        }
        this.reClaclulate();
    }

    /**
     * Gets the current animation value.
     *
     * @param f The frame number.
     * @return The current animation value.
     */
    public int getValue(int f) {
        if (onType == 1 || onType == 2) {
            return this.getInitValue();
        }

        CTrace ct = (CTrace) onObj;
        double len = ct.Roud_length();
        int n = ct.getPointSize();
        if (n == 0) {
            return 0;
        }
        int d = (int) ((f * gap * n) / (len));
        return d;
    }

    AnimateC() {
    }

    /**
     * Gets the initial animation value.
     *
     * @return The initial animation value.
     */
    public int getInitValue() {
        return (int) gap;
    }

    AnimateC(CPoint p, Object obj, double width, double height) {
        pA = p;
        onObj = obj;
        this.width = width;
        this.height = height;

        if (obj instanceof CLine) {
            onType = 1;
        } else if (obj instanceof Circle) {
            onType = 2;
        } else if (obj instanceof CTrace) {
            onType = 3;
        } else {
            CMisc.print("Error,undifined on type ");
        }
        reClaclulate();

    }

    /**
     * Recalculates the animation parameters based on the current settings.
     */
    public void reClaclulate() {
        if (onType == 2) {
            Circle c = (Circle) onObj;
            CPoint pt = c.getSidePoint();
            double rx = c.o.getx();
            double ry = c.o.gety();
            double r = Math.sqrt(Math.pow(pt.getx() - rx, 2) +
                    Math.pow(pt.gety() - ry, 2));
            csa = Math.cos(-gap / r);
            sia = Math.sin(-gap / r);
        } else if (onType == 1) {
            CLine line = (CLine) onObj;
            CPoint[] pp = line.getTowSideOfLine();
            if (line.isVertical()) {
                dx = (pp[1].getx() - pp[0].getx());
                dy = (pp[1].gety() - pp[0].gety());
                double r = Math.sqrt(dx * dx + dy * dy);
                dx = dx / r;
                dy = dy / r;
            } else {
                if (pp[0] == pA) {
                    pp[0] = pp[1];
                    pp[1] = pA;
                }

                dx = (pp[1].getx() - pp[0].getx());
                dy = (pp[1].gety() - pp[0].gety());
                double r = Math.sqrt(dx * dx + dy * dy);
                dx = dx / r;
                dy = dy / r;
            }
        } else if (onType == 3) {

        } else {
            CMisc.print("Error,undifined on type ");
        }
    }

    /**
     * Starts the animation by recalculating initial values and setting initial positions.
     */
    public void startAnimate() {
        reClaclulate();

        bx = pA.getx();
        by = pA.gety();

        x = pA.getx();
        y = pA.gety();

    }

    /**
     * Resets the position of the point to its initial coordinates.
     */
    public void resetXY() {
        pA.setXY(x, y);
    }

    /**
     * Returns a string representation of the animation state.
     *
     * @return a string representing the point and the object it is on
     */
    public String toString() {
        if (pA == null || onObj == null)
            return super.toString();
        return pA + " on " + onObj;
    }

    /**
     * Stops the animation by optionally resetting the point's position.
     */
    public void stopAnimate() {
        // pA.setXY(bx, by);
    }

    /**
     * Calculates the number of rounds needed based on the type of object being animated.
     *
     * @return the number of rounds for the animation
     */
    public int getRounds() {
        if (onType == 2) {
            Circle c = (Circle) onObj;
            CPoint pt = c.getSidePoint();
            double rx = c.o.getx();
            double ry = c.o.gety();
            double r = Math.sqrt(Math.pow(pt.getx() - rx, 2) +
                    Math.pow(pt.gety() - ry, 2));
            return (int) Math.abs(Math.PI * r * 2 / gap);
        } else if (onType == 1) {
            int n1 = (int) Math.abs((width - minwd) / (gap * dx));
            int n2 = (int) Math.abs((height - minht) / (gap * dy));
            return Math.min(n1, n2) * 2;
        }
        return 0;
    }

    /**
     * Handles the animation logic based on a timer event.
     *
     * @return true if the animation continues, false otherwise
     */
    public boolean onTimer() {
        // pA.setXY(x, y);
        boolean r = true;

        if (onType == 2) {
            Circle c = (Circle) onObj;
            double x = pA.getx();
            double y = pA.gety();
            double rx = c.o.getx();
            double ry = c.o.gety();

            double x0 = csa * (x - rx) - sia * (y - ry) + rx;
            double y0 = sia * (x - rx) + csa * (y - ry) + ry;

            pA.setXY(x0, y0);

        } else if (onType == 1) {
            double x = pA.getx() + gap * dx;
            double y = pA.gety() + gap * dy;
            if (x < minwd || x > width || y < minht || y > height) {
                dx = -dx;
                dy = -dy;
                r = false;
            }
            pA.setXY(x, y);
        } else if (onType == 3) {
            CTrace ct = (CTrace) onObj;

            int len = ct.getPointSize();
            if (len == 0) {
                return r;
            }
            if (pindex >= len) {
                pindex = 0;
            }
            pA.setXY(ct.getPtxi(pindex), ct.getPtyi(pindex));
            pindex++;
        }

        x = pA.getx();
        y = pA.gety();

        return r;
    }

    /**
     * Saves the current state of the animation to an output stream.
     *
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    public void Save(DataOutputStream out) throws IOException {
        out.writeDouble(bx);
        out.writeDouble(by);
        out.writeDouble(width);
        out.writeDouble(height);

        if (pA == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(pA.m_id);
        }

        out.writeInt(onType);

        CClass c = (CClass) onObj;
        if (c == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(c.m_id);
        }

        out.writeDouble(dx);
        out.writeDouble(dy);
        out.writeDouble(csa);
        out.writeDouble(sia);
        out.writeDouble(gap);
        out.writeInt(pindex);
        out.writeInt(step_time);

    }

    /**
     * Loads the state of the animation from an input stream.
     *
     * @param in the input stream to read from
     * @param dp the draw process to use for retrieving objects
     * @throws IOException if an I/O error occurs
     */
    public void Load(DataInputStream in, DrawProcess dp) throws IOException {
        bx = in.readDouble();
        by = in.readDouble();
        width = in.readDouble();
        height = in.readDouble();
        int d = in.readInt();
        pA = dp.getPointById(d);
        onType = in.readInt();

        d = in.readInt();

        if (onType == 1) {
            onObj = dp.getLineByid(d);
        } else if (onType == 2) {
            onObj = dp.getCircleByid(d);
        } else if (onType == 3) {
            onObj = dp.getTraceById(d);
        }

        dx = in.readDouble();
        dy = in.readDouble();
        csa = in.readDouble();
        sia = in.readDouble();
        gap = in.readDouble();
        pindex = in.readInt();
        step_time = in.readInt();

    }


}



