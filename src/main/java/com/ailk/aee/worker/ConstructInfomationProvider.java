// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import java.util.HashMap;
import java.util.Map;

public class ConstructInfomationProvider implements IConstructInfomationProvider
{
    private Map<String, String> conf;
    
    public ConstructInfomationProvider() {
        this.conf = new HashMap<String, String>();
    }
    
    @Override
    public Map<String, String> getConstructInfomation() {
        return this.conf;
    }
}
