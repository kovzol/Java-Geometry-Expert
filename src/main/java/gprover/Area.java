package gprover;

      /**
       * The Area class represents a geometric area and extends the Full class.
       * It includes methods for printing results, creating geometric terms,
       * and evaluating geometric expressions.
       */
      public class Area extends Full {

          /**
           * Prints the results if the print_conc flag is set.
           */
          void print_t() {
              if (print_conc) {
                  // gprint("\r\n***************The results******          \r\n");
                  gprint(Cm.s2300);
              }
              // pro_result = 1;
          }

          /**
           * Creates a geometric term with the specified coefficients and terms.
           *
           * @param c1 the first coefficient
           * @param p1 the first term
           * @param c2 the second coefficient
           * @param p2 the second term
           */
          void conc_gr(long c1, XTerm p1, long c2, XTerm p2) {
              GrTerm gr = mk_gr1(mk_num(c1), p1, mk_num(c2), p2);
              gr.c = 0;
              last_pr.nx = gr;
              last_pr = gr;
          }

          /** The head of the elimination term linked list. */
          ElTerm all_elim = new ElTerm();

          /**
           * Creates an elimination term with the specified variable and terms.
           *
           * @param v the variable
           * @param p1 the first term
           * @param p2 the second term
           * @return the created elimination term
           */
          ElTerm mk_elim(Var v, XTerm p1, XTerm p2) {
              ElTerm v1 = new ElTerm(); // (el_term  )calloc(1,sizeof(el_term));
              v1.v = v;
              v1.p1 = p1;
              v1.p2 = p2;
              v1.nx = all_elim.nx;
              all_elim.nx = v1;
              return v1;
          }

          /**
           * Evaluates a geometric expression with the specified variable, integer, and point.
           *
           * @param var the variable
           * @param y the integer
           * @param p the point
           * @return the evaluated geometric term
           */
          XTerm geval(Var var, int y, int p) {
              int[] pt = new int[9];

              for (int i = 0; i < 4; i++)
                  if (var.pt[i] == y)
                      pt[i] = p;
                  else
                      pt[i] = var.pt[i];
              switch (var.nm) {
                  case 1:
                  case -1:
                      return trim_r(pt[0], pt[1], pt[2], pt[3]);
                  case 2:
                  case -2:
                      return trim_a(pt[0], pt[1], pt[2], pt[3]);
                  case 3:
                  case -3:
                      return trim_g(pt[0], pt[1], pt[2], pt[3]);
                  case 4:
                  case -4:
                      return trim_vec(pt[0], pt[1]);
                  default:
                      exit(1);
              }
              return null;
          }

          /**
           * Trims a geometric term with the specified points.
           *
           * @param p1 the first point
           * @param p2 the second point
           * @param p3 the third point
           * @param p4 the fourth point
           * @return the trimmed geometric term
           */
          XTerm trim_a(int p1, int p2, int p3, int p4) {
              int p;
              char sn = 1;
              if (xpara(p1, p3, p2, p4)) return pzero();
              if (xcoll(p1, p2, p3)) {
                  p2 = p1;
              } else if (xcoll(p4, p2, p3)) {
                  p3 = p4;
              } else if (xcoll(p4, p1, p3)) {
                  p4 = p3;
              } else if (xcoll(p4, p2, p1)) {
                  p1 = p4;
              }
              if (p1 < p3) {
                  sn *= -1;
                  p = p1;
                  p1 = p3;
                  p3 = p;
              }
              if (p2 < p4) {
                  sn *= -1;
                  p = p2;
                  p2 = p4;
                  p4 = p;
              }
              if (p1 < p2) {
                  sn *= -1;
                  p = p1;
                  p1 = p2;
                  p2 = p;
                  p = p3;
                  p3 = p4;
                  p4 = p;
              } else if ((p1 == p2) && (p3 < p4)) {
                  sn *= -1;
                  p = p3;
                  p3 = p4;
                  p4 = p;
              }

              if (p1 == p2) {
                  p2 = p3;
                  p3 = p4;
              } else if (p2 == p3) {
                  p3 = p4;
              }

              if (sn == 1)
                  return get_m(mk_var(2, p1, p2, p3, p4));
              else
                  return neg_poly(get_m(mk_var(2, p1, p2, p3, p4)));
          }

          /**
           * Trims a geometric term with the specified points.
           *
           * @param p1 the first point
           * @param p2 the second point
           * @param p3 the third point
           * @param p4 the fourth point
           * @return the trimmed geometric term
           */
          XTerm trim_f(int p1, int p2, int p3, int p4) {
              int p;
              char sn = 1;
              if (print_geo) {
                  gprint("trim_a1 " + p1 + p2 + p3 + p4 + "\r\n");
              }
              if (xcoll4(p1, p2, p3, p4)) return pzero();
              if (p1 < p2) {
                  p = p1;
                  p1 = p2;
                  p2 = p;
              }
              if (p3 < p4) {
                  p = p3;
                  p3 = p4;
                  p4 = p;
              }
              if (p1 < p3) {
                  sn *= -1;
                  p = p1;
                  p1 = p3;
                  p3 = p;
                  p = p2;
                  p2 = p4;
                  p4 = p;
              } else if ((p1 == p3) && (p2 < p4)) {
                  sn *= -1;
                  p = p2;
                  p2 = p4;
                  p4 = p;
              }
              if (sn == 1)
                  return get_m(mk_var(10, p1, p2, p3, p4));
              else
                  return neg_poly(get_m(mk_var(10, p1, p2, p3, p4)));
          }

          /**
           * Trims a geometric term with the specified lines.
           *
           * @param l1 the first line
           * @param l2 the second line
           * @return the trimmed geometric term
           */
          @Override
          XTerm trim_fl(LLine l1, LLine l2) {
              if (l1 == l2) return pzero();
              return trim_f(l1.pt[0], l1.pt[1], l2.pt[0], l2.pt[1]);
          }
      }
