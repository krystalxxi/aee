// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AbstractOPS.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class AbstractOPS implements OPS
{
    @Override
    public abstract boolean calc(final Object p0, final Object p1);
}
