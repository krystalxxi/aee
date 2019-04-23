// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.Map;
import com.ailk.aee.platform.service.AbstractPlatformService;

public class GTMService extends AbstractPlatformService
{
    private GTMPool pool;
    
    public GTMService(final GTMPool p) {
        this.pool = null;
        this.pool = p;
    }
    
    public String getServiceDescription() {
        return "Group Task Manager";
    }
    
    public String getServiceName() {
        return "GTM";
    }
    
    @PlatformServiceMethod
    public Map<String, String> kill(final Map<String, String> args) {
        return null;
    }
    
    @PlatformServiceMethod
    public Map<String, String> list() {
        return this.pool.listWork();
    }
}
