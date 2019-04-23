// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Map;
import java.util.HashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StaticMapConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class StaticMapConfigurationFactory extends MapConfigurationFactory
{
    public StaticMapConfigurationFactory() {
        this.setConf(new HashMap<String, String>());
    }
    
    @Override
    public String getFactoryName() {
        return "Static Map Configuration Factory";
    }
    
    @Override
    public void init() {
        this.initConfMap();
    }
    
    public abstract void initConfMap();
}
