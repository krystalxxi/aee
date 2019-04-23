// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSBetween.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSBetween extends AbstractOPS
{
    @Override
    public boolean calc(final Object left, final Object right) {
        if (right == null || left == null) {
            return false;
        }
        final List<OPSBetweenPair> pps = this.getAllPairs(right);
        if (pps != null) {
            for (final OPSBetweenPair p : pps) {
                if ((p.getLeftArea() == null || OPS.GE.calc(left, p.getLeftArea())) && (p.getRightArea() == null || OPS.LE.calc(left, p.getRightArea()))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<OPSBetweenPair> getAllPairs(final Object right) {
        final List<Object> al = new ArrayList<Object>();
        if (right.getClass().isArray()) {
            for (int length = Array.getLength(right), i = 0; i < length; ++i) {
                final Object o = Array.get(right, i);
                al.add(o);
            }
            return this.getAllPairsFromArray(al.toArray());
        }
        if (!(right instanceof Iterable)) {
            al.add(right);
            return this.getAllPairsFromArray(al.toArray());
        }
        final Iterable iter = (Iterable)right;
        final Iterator iters = iter.iterator();
        if (iters == null) {
            return this.getAllPairsFromArray(al.toArray());
        }
        while (iters.hasNext()) {
            final Object o = iters.next();
            al.add(o);
        }
        return this.getAllPairsFromArray(al.toArray());
    }
    
    private List<OPSBetweenPair> getAllPairsFromArray(final Object[] as) {
        final List<OPSBetweenPair> al = new ArrayList<OPSBetweenPair>();
        for (int i = 0; i < as.length; i += 2) {
            final OPSBetweenPair p = new OPSBetweenPair();
            p.setLeftArea(as[i]);
            if (i + 1 < as.length) {
                p.setRightArea(as[i + 1]);
            }
            else {
                p.setRightArea(null);
            }
            al.add(p);
        }
        return al;
    }
    
    static class OPSBetweenPair
    {
        private Object leftArea;
        private Object rightArea;
        
        public OPSBetweenPair() {
            this.leftArea = null;
            this.rightArea = null;
        }
        
        public OPSBetweenPair(final Object left, final Object right) {
            this.leftArea = null;
            this.rightArea = null;
            this.leftArea = left;
            this.rightArea = right;
        }
        
        public Object getLeftArea() {
            return this.leftArea;
        }
        
        public Object getRightArea() {
            return this.rightArea;
        }
        
        public void setLeftArea(final Object leftArea) {
            this.leftArea = leftArea;
        }
        
        public void setRightArea(final Object rightArea) {
            this.rightArea = rightArea;
        }
    }
}
