// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.adapter;

import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.platform.IServiceListener;

@CVSID("$Id: AbstractPlatformServiceAdapter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AbstractPlatformServiceAdapter implements IServiceListener
{
    @Override
    public void onServiceRegister(final String serviceName) {
    }
    
    @Override
    public void onServiceStartup(final String serviceName) {
    }
    
    @Override
    public void onServiceStop(final String serviceName) {
    }
    
    @Override
    public void onServiceUnRegister(final String serviceName) {
    }
    
    public void start() throws Exception {
    }
    
    public void stop() {
    }
}
