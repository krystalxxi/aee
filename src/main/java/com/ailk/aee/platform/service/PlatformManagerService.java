// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: PlatformManagerService.java 60270 2013-11-03 14:48:37Z tangxy $")
public class PlatformManagerService extends AbstractPlatformService
{
    @Override
    public String getServiceDescription() {
        return "Platform Manager";
    }
    
    @Override
    public String getServiceName() {
        return "PM";
    }
}
