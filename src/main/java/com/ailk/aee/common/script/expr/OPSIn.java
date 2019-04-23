// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import java.util.Iterator;
import java.lang.reflect.Array;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSIn.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSIn extends AbstractOPS
{
    @Override
    public boolean calc(final Object left, final Object right) {
        if (right == null || left == null) {
            return false;
        }
        if (right.getClass().isArray()) {
            for (int length = Array.getLength(right), i = 0; i < length; ++i) {
                final Object o = Array.get(right, i);
                if (OPS.EQUAL.calc(left, o)) {
                    return true;
                }
            }
            return false;
        }
        if (!(right instanceof Iterable)) {
            return OPS.EQUAL.calc(left, right);
        }
        final Iterable iter = (Iterable)right;
        final Iterator iters = iter.iterator();
        if (iters == null) {
            return false;
        }
        while (iters.hasNext()) {
            final Object o = iters.next();
            if (OPS.EQUAL.calc(left, o)) {
                return true;
            }
        }
        return false;
    }
}
