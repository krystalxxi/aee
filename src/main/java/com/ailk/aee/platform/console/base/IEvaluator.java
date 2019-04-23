// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IEvaluator.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IEvaluator
{
    void evaluate(final String p0);
    
    void setVariable(final String p0, final Object p1);
}
