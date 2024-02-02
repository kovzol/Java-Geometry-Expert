package gprover;

class UStruct {
    MidPt md;
    LLine ln;
    PLine pn;
    TLine tn;
    ACir cr;
    Angles as;
    AngleT at;
    AngTn atn;
    SimTri st;
    CongSeg cg;
    RatioSeg ra;
    Polygon pg;
    LList ns;

    public boolean equal(UStruct u1) {
        return
                md == u1.md
                && ln == u1.ln
                && pn == u1.pn
                && tn == u1.tn
                && cr == u1.cr
                && as == u1.as
                && st == u1.st
                && cg == u1.cg
                && ra == u1.ra
                && at == u1.at
                && pg == u1.pg
                && atn == u1.atn
                && ns == u1.ns;
    }

    public boolean isnull() {
        return
                md == null
                && ln == null
                && pn == null
                && tn == null
                && cr == null
                && as == null
                && st == null
                && cg == null
                && ra == null
                && at == null
                && pg == null
                && atn == null
                && ns == null;
    }

    public void setnull() {
        md = null;
        ln = null;
        pn = null;
        tn = null;
        cr = null;
        as = null;
        st = null;
        cg = null;
        ra = null;
        at = null;
        pg = null;
        atn = null;
        ns = null;
    }

    public void cpv(UStruct us) {
        md = us.md;
        pn = us.pn;
        ln = us.ln;
        tn = us.tn;
        cr = us.cr;
        as = us.as;
        st = us.st;
        cg = us.cg;
        ra = us.ra;
        at = us.at;
        pg = us.pg;
        atn = us.atn;
        ns = us.ns;
    }

    public UStruct() {
        md = null;
        ln = null;
        pn = null;
        tn = null;
        cr = null;
        as = null;
        st = null;
        cg = null;
        ra = null;
        at = null;
        pg = null;
        atn = null;
        ns = null;
    }

    public int get_type() {
        if (md != null) return md.type;
        if (ln != null) return ln.type;
        if (pn != null) return pn.type;
        if (tn != null) return tn.type;
        if (cr != null) return cr.type;
        if (as != null) return as.type;
        if (st != null) return st.type;
        if (cg != null) return cg.type;
        if (ra != null) return ra.type;
        if (at != null) return at.type;
        if (pg != null) return pg.type;
        if (atn != null) return atn.type;
        if (ns != null) return ns.type;
        return -1;
    }

    public int get_lemma() {
        if (md != null) return md.lemma;
        if (ln != null) return ln.lemma;
        if (pn != null) return pn.lemma;
        if (tn != null) return tn.lemma;
        if (cr != null) return cr.lemma;
        if (as != null) return as.lemma;
        if (st != null) return st.lemma;
        if (cg != null) return cg.lemma;
        if (ra != null) return ra.lemma;
        if (at != null) return at.lemma;
        if (pg != null) return pg.lemma;
        if (atn != null) return atn.lemma;
        return -1;
    }

    public CClass get_attr() {
        if (md != null) return md;
        if (ln != null) return ln;
        if (pn != null) return pn;
        if (tn != null) return tn;
        if (cr != null) return cr;
        if (as != null) return as;
        if (st != null) return st;
        if (cg != null) return cg;
        if (ra != null) return ra;
        if (at != null) return at;
        if (pg != null) return pg;
        if (atn != null) return atn;

        return null;
    }

    Cond get_co() {
        if (md != null) return md.co;
        if (ln != null) return ln.co;
        if (pn != null) return pn.co;
        if (tn != null) return tn.co;
        if (cr != null) return cr.co;
        if (as != null) return as.co;
        if (st != null) return st.co;
        if (cg != null) return cg.co;
        if (ra != null) return ra.co;
        if (at != null) return at.co;
        if (pg != null) return pg.co;
        if (atn != null) return atn.co;

        return null;
    }

    public int get_no() {
        //if (d != null) return d.no;
        //if (md != null) return md.no;
        if (ln != null) return ln.no;
        if (pn != null) return pn.no;
        //if (tn != null) return tn.no;
        if (cr != null) return cr.no;
        //if (as != null) return as.no;
//        if (st != null) return st.no;
//        if (cg != null) return cg.no;
        //if (ra != null) return ra.no;
        return -1;
    }


    public int set_type(int t) {
        if (md != null)
            md.type = t;
        else if (ln != null)
            ln.type = t;
        else if (pn != null)
            pn.type = t;
        else if (tn != null)
            tn.type = t;
        else if (cr != null)
            cr.type = t;
        else if (as != null)
            as.type = t;
        else if (st != null)
            st.type = t;
        else if (cg != null)
            cg.type = t;
        else if (ra != null)
            ra.type = t;
        else if (at != null)
            at.type = t;
        else if (pg != null)
            pg.type = t;
        else if (atn != null)
            atn.type = t;
        return 0;
    }


}
