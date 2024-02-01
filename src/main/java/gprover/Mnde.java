package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: ye
 * Date: Nov 17, 2006
 * Time: 5:00:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mnde {
    int t, type;
    public AngTr tr;
    Mnde nx;
    public Mnde()
    {
        t = 1;
        type = 0;
        tr = null;
        nx = null;
        
    }
    public void cp(Mnde m)
    {
        t = m.t;
        type = m.type;
        tr = m.tr;
    }
}
