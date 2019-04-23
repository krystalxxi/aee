// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EnvConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class EnvConfigurationFactory extends StaticMapConfigurationFactory
{
    public EnvConfigurationFactory() {
        this.init();
    }
    
    @Override
    public String getFactoryName() {
        return "EnvConfiguration From System.getenv()";
    }
    
    @Override
    public void initConfMap() {
        this.conf.putAll(System.getenv());
    }
}
