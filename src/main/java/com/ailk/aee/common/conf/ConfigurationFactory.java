// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class ConfigurationFactory
{
    private ConfigurationFactory nextCf;
    private boolean isSupportDump;
    private boolean isInit;
    
    public ConfigurationFactory() {
        this.nextCf = null;
        this.isSupportDump = false;
        this.isInit = false;
    }
    
    public void checkReload() {
    }
    
    public String dump() {
        return this.dump(null);
    }
    
    public String dump(final String[] args) {
        return "Not support Dump";
    }
    
    public Map<String, String> getConf(final String prefix) {
        return this.getConf(prefix, false);
    }
    
    public abstract Map<String, String> getConf(final String p0, final boolean p1);
    
    public abstract String getFactoryName();
    
    public ConfigurationFactory getNextFactory() {
        return this.nextCf;
    }
    
    public abstract void init();
    
    public boolean isSupportDump() {
        return this.isSupportDump;
    }
    
    public void regist() {
        if (!this.isInit) {
            synchronized (this) {
                if (!this.isInit) {
                    this.init();
                    this.isInit = true;
                }
            }
        }
        Configuration.getInstance().addConfigurationFactory(this);
    }
    
    public void setNextFactory(final ConfigurationFactory cf) {
        this.nextCf = cf;
    }
    
    public void setSupportDump(final boolean isSupportDump) {
        this.isSupportDump = isSupportDump;
    }
}
