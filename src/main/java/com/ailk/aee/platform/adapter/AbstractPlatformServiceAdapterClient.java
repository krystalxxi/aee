// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.adapter;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AbstractPlatformServiceAdapterClient.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AbstractPlatformServiceAdapterClient implements IPlatformServiceAdapterClient
{
    @Override
    public Map<String, String> call(final String service, final String method, final Map<String, String> args) {
        return null;
    }
    
    @Override
    public void connect() {
    }
    
    @Override
    public void disconnect() {
    }
}
