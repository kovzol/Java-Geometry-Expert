/**
 * Created by IntelliJ IDEA.
 * User: Ye
 * Date: 2006-2-15
 * Time: 13:28:06
 * To change this template use File | Settings | File Templates.
 */
package gprover;

public class CClass {
    final public static int MAX_GEO = 40;
    public static long id_count = 0;


    long id = id_count++;
    long dep = Gib.depth;


    int type;
    String text;

    public String toString() {
        return text;
    }
}
