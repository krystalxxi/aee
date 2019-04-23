// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.strategy;

import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConfiguratedWorkCounterStrategy.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConfiguratedWorkCounterStrategy implements ICounterStrategy
{
    private String confName;
    private float v;
    
    public ConfiguratedWorkCounterStrategy() {
        this.confName = "";
        this.v = 0.0f;
    }
    
    public ConfiguratedWorkCounterStrategy(final String confName) {
        this.confName = "";
        this.v = 0.0f;
        this.confName = confName;
    }
    
    @Override
    public float calc(final Worker jw) {
        try {
            final String sv = Configuration.getValue(this.confName);
            if (sv != null && StringUtils.isNumeric((CharSequence)sv)) {
                this.v = Float.parseFloat(sv);
            }
        }
        catch (Exception e) {
            this.v = 0.0f;
        }
        return this.v;
    }
}
