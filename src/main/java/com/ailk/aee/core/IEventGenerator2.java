// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IEventGenerator2.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IEventGenerator2
{
    void rollback(final Object[] p0);
    
    void start();
    
    void stop();
}
