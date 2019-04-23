// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import com.ailk.aee.core.Worker;
import com.ailk.aee.platform.service.AbstractPlatformService;

public class WorkService extends AbstractPlatformService
{
    private Worker w;
    
    public WorkService(final Worker w) {
        this.w = null;
        this.w = w;
    }
    
    @Override
    public String getServiceDescription() {
        return "Worker \u04bb\ufffd\ufffd\ufffd\ufffd\ufffd\u053c\ufffd\ufffd";
    }
    
    @Override
    public String getServiceName() {
        return "WORKER";
    }
}
