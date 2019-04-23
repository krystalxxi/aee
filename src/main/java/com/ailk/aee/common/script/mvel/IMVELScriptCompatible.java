// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IMVELScriptCompatible.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IMVELScriptCompatible
{
    boolean containKey(final String p0);
    
    Object getData(final String p0);
    
    void setData(final String p0, final Object p1);
}
