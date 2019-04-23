// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IServiceListener.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IServiceListener
{
    void onServiceRegister(final String p0);
    
    void onServiceStartup(final String p0);
    
    void onServiceStop(final String p0);
    
    void onServiceUnRegister(final String p0);
}
