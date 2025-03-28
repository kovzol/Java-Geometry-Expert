package gprover;

/**
 * The Elim class provides methods for trimming geometric terms under various constraints.
 * It extends the Gr class.
 */
public class Elim extends Gr
{
      /**
       * Trims a ratio geometric term based on four point parameters.
       *
       * @param p1 the first point parameter
       * @param p2 the second point parameter
       * @param p3 the third point parameter
       * @param p4 the fourth point parameter
       * @return the trimmed ratio term as an XTerm
       */
      XTerm trim_r(int p1, int p2, int p3, int p4)
      {
            int p;
            char sn = 1;
            /*  if (print_geo) printf("trim_r %d %d %d %d\r\n",p1,p2,p3,p4); */
            if (p1 == p2) return (pzero());
            if (p3 == p4) gerror("rtrim: denominator of ratio is zero.~%");
            if ((p1 == p3) && (p2 == p4)) return (get_n(1L));
            if ((p2 == p3) && (p1 == p4)) return (get_n(-1L));
            if (p1 < p2)
            {
                  sn *= (char) -1;
                  p = p1;
                  p1 = p2;
                  p2 = p;
            }
            if (p3 < p4)
            {
                  sn *= (char) -1;
                  p = p3;
                  p3 = p4;
                  p4 = p;
            }
            if (sn == 1)
                  return (get_m(mk_var(1, p1, p2, p3, p4)));
            else
                  return (neg_poly(get_m(mk_var(1, p1, p2, p3, p4))));
      }

      /**
       * Trims an angle geometric term based on four point parameters.
       *
       * @param p1 the first point parameter
       * @param p2 the second point parameter
       * @param p3 the third point parameter
       * @param p4 the fourth point parameter
       * @return the trimmed angle term as an XTerm
       */
      XTerm trim_a(int p1, int p2, int p3, int p4)
      {
            int p;
            char sn = 1;
            if (xpara(p1, p3, p2, p4)) return (pzero());
            if (xcoll(p1, p2, p3))
            {
                  p2 = p1;
            } else if (xcoll(p4, p2, p3))
            {
                  p3 = p4;
            } else if (xcoll(p4, p1, p3))
            {
                  p4 = p3;
            } else if (xcoll(p4, p2, p1))
            {
                  p1 = p4;
            }
            if (p1 < p3)
            {
                  sn *= (char) -1;
                  p = p1;
                  p1 = p3;
                  p3 = p;
            }
            if (p2 < p4)
            {
                  sn *= (char) -1;
                  p = p2;
                  p2 = p4;
                  p4 = p;
            }
            if (p1 < p2)
            {
                  sn *= (char) -1;
                  p = p1;
                  p1 = p2;
                  p2 = p;
                  p = p3;
                  p3 = p4;
                  p4 = p;
            } else if ((p1 == p2) && (p3 < p4))
            {
                  sn *= (char) -1;
                  p = p3;
                  p3 = p4;
                  p4 = p;
            }

            if (p1 == p2)
            {
                  p2 = p3;
                  p3 = p4;
            } else if (p2 == p3)
            {
                  p3 = p4;
            }

            if (sn == 1)
                  return (get_m(mk_var(2, p1, p2, p3, p4)));
            else
                  return (neg_poly(get_m(mk_var(2, p1, p2, p3, p4))));
      }

      /**
       * Trims a geometric term based on perpendicularity constraints.
       *
       * @param p1 the first point parameter
       * @param p2 the second point parameter
       * @param p3 the third point parameter
       * @param p4 the fourth point parameter
       * @return the trimmed geometric term as an XTerm
       */
      XTerm trim_g(int p1, int p2, int p3, int p4)
      {
            int p;
            char sn = 1;
            /*  if (print_geo)  printf("\r\ntrim_g %d %d %d %d\r\n",p1,p2,p3,p4); */
            if (xperp(p1, p3, p2, p4)) return (get_n(0L));
            if (p1 < p3)
            {
                  sn *= (char) -1;
                  p = p1;
                  p1 = p3;
                  p3 = p;
            }
            if (p2 < p4)
            {
                  sn *= (char) -1;
                  p = p2;
                  p2 = p4;
                  p4 = p;
            }
            if (p1 < p2)
            {
                  p = p1;
                  p1 = p2;
                  p2 = p;
                  p = p3;
                  p3 = p4;
                  p4 = p;
            } else if ((p1 == p2) && (p3 < p4))
            {
                  p = p3;
                  p3 = p4;
                  p4 = p;
            }

            if (p1 == p2)
            {
                  sn *= (char) -1;
                  p1 = p3;
                  p3 = p2;
            } else if (p3 == p4)
            {
                  sn *= (char) -1;
                  p4 = p2;
                  p2 = p3;
            }
            //The largest index is either p1 or p2
            if (sn == 1)
                  return (get_m(mk_var(3, p1, p2, p3, p4)));
            else
                  return (neg_poly(get_m(mk_var(3, p1, p2, p3, p4))));
      }

      /**
       * Trims a geometric term based on four point parameters, typically representing line intersections.
       *
       * @param p1 the first point parameter
       * @param p2 the second point parameter
       * @param p3 the third point parameter
       * @param p4 the fourth point parameter
       * @return the trimmed term as an XTerm
       */
      XTerm trim_f(int p1, int p2, int p3, int p4)
      {
            int p;
            char sn = 1;
            if (xcoll4(p1, p2, p3, p4)) return (pzero());
            if (p1 < p2)
            {
                  p = p1;
                  p1 = p2;
                  p2 = p;
            }
            if (p3 < p4)
            {
                  p = p3;
                  p3 = p4;
                  p4 = p;
            }
            if (p1 < p3)
            {
                  sn *= (char) -1;
                  p = p1;
                  p1 = p3;
                  p3 = p;
                  p = p2;
                  p2 = p4;
                  p4 = p;
            } else if ((p1 == p3) && (p2 < p4))
            {
                  sn *= (char) -1;
                  p = p2;
                  p2 = p4;
                  p4 = p;
            }
            if (sn == 1)
                  return (get_m(mk_var(10, p1, p2, p3, p4)));
            else
                  return (neg_poly(get_m(mk_var(10, p1, p2, p3, p4))));
      }

      /**
       * Trims a geometric term derived from two lines.
       *
       * @param l1 the first line
       * @param l2 the second line
       * @return the trimmed term as an XTerm
       */
      XTerm trim_fl(LLine l1, LLine l2)
      {
            if (l1 == l2) return (pzero());
            return (trim_f(l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1]));
      }

      /**
       * Trims a geometric term representing a vector.
       *
       * @param p1 the first point parameter
       * @param p2 the second point parameter
       * @return the trimmed vector term as an XTerm
       */
      XTerm trim_vec(int p1, int p2)
      {
            if (p2 == 0)
            {
                  return (get_m(mk_var(4, p1, 0, 0, 0)));
            }
            if (p1 == p2) return (pzero());
            if (p1 < p2) return (neg_poly(get_m(mk_var(4, p2, p1, 0, 0))));
            return (get_m(mk_var(4, p1, p2, 0, 0)));
      }
}
