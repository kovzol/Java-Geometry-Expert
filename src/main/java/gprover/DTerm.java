package gprover;

/**
 * Created by IntelliJ IDEA.
 * User: yezheng
 * Date: 2006-5-4
 * Time: 11:32:44
 * To change this template use File | Settings | File Templates.
 */

public class DTerm
{
      public int deg;          //degree
      public XTerm p;         //A term
      public DTerm nx;       // All next terms.

      public String text;

      public DTerm()
      {
            deg = 0;
            p = null;
            nx = null;
            text = null;
      }

      public String toString()
      {
            return text;
      }
}
