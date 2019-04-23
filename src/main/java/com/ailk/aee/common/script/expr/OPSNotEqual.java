// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSNotEqual.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSNotEqual extends AbstractOPS
{
    @Override
    public boolean calc(final Object left, final Object right) {
        return !OPS.EQUAL.calc(left, right);
    }
}
