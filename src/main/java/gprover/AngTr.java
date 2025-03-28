package gprover;

 /**
  * The AngTr class represents a geometric configuration of two lines.
  * It extends the CClass and includes properties for values, lines,
  * and other attributes related to angles.
  */
 public class AngTr extends CClass {
     /** An integer value associated with the angle. */
     public int v;

     /** The first integer attribute related to the angle. */
     public int t1;

     /** The second integer attribute related to the angle. */
     public int t2;

     /** The first line that defines the angle. */
     public LLine l1;

     /** The second line that defines the angle. */
     public LLine l2;

     /** The condition associated with the angle. */
     Cond co;

     /** The next AngTr object in a linked list structure. */
     AngTr nx;

     /**
      * Constructs an AngTr object with default values.
      */
     public AngTr() {
         l1 = l2 = null;
         co = null;
         nx = null;
         v = 0;
     }

     /**
      * Gets the first point of the first line that is not equal to the value t1.
      *
      * @return the first point of the first line that is not equal to the value t1
      */
     public int get_lpt1() {
         if (t1 != 0) return t1;
         return LLine.get_lpt1(l1, v);
     }

     /**
      * Gets the first point of the second line that is not equal to the value t2.
      *
      * @return the first point of the second line that is not equal to the value t2
      */
     public int get_lpt2() {
         if (t2 != 0) return t2;
         return LLine.get_lpt1(l2, v);
     }
 }