package gprover;

/**
 * Represents a geometric circle with various properties and methods.
 */
public class ACir extends CClass
{
    int lemma;
    Cond co;
    public int no;
    public int o;
    public int []pt;
    public int []d;
    public ACir nx;

    /**
     * Default constructor for ACir.
     * Initializes the properties of the circle.
     */
    public ACir()
    {
        type = lemma =0;
        co = null;
        no = o = 0;
        pt = new int[MAX_GEO];
        d = new int[MAX_GEO];
        nx = null;

    }
}
