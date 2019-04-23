// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.adapter;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IPlatformServiceAdapterClient.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IPlatformServiceAdapterClient
{
    Map<String, String> call(final String p0, final String p1, final Map<String, String> p2);
    
    void connect();
    
    void disconnect();
}
