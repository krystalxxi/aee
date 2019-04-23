// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.Map;

public class MasterBootstrap extends ServerBootstrap
{
    private String node;
    
    public MasterBootstrap() {
        this.node = "";
    }
    
    @Override
    public void customize() {
        super.customize();
    }
    
    @Override
    public String getServiceDescription() {
        return "MASTER MANAGER SERVICE";
    }
    
    @Override
    public String getServiceName() {
        return "MASTER";
    }
    
    @Override
    public int cycleTime() {
        return 60;
    }
    
    @Override
    public void onCycleTime() {
    }
    
    @PlatformServiceMethod
    public Map<String, String> bootByName(final Map<String, String> args) {
        return null;
    }
}
