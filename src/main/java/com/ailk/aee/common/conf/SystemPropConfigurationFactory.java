// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SystemPropConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SystemPropConfigurationFactory extends StaticMapConfigurationFactory
{
    public SystemPropConfigurationFactory() {
        this.init();
    }
    
    @Override
    public String getFactoryName() {
        return "SystemPropertiesConfiguration From System.getProperties()";
    }
    
    @Override
    public void initConfMap() {
        final Properties prop = System.getProperties();
        for (final Object s : prop.keySet()) {
            this.conf.put((String)s, prop.getProperty((String)s));
        }
    }
}
