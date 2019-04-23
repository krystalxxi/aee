// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Iterator;
import java.util.ArrayList;
import com.ailk.aee.common.util.watchdog.IWatchDog;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ReloadMapConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class ReloadMapConfigurationFactory extends StaticMapConfigurationFactory
{
    private List<IWatchDog> watchers;
    
    public ReloadMapConfigurationFactory() {
        this.watchers = new ArrayList<IWatchDog>();
    }
    
    public void addWatcher(final IWatchDog w) {
        this.watchers.add(w);
    }
    
    @Override
    public void checkReload() {
        for (final IWatchDog w : this.watchers) {
            if (w.checked()) {
                this.reload();
                Configuration.getInstance().buildData();
            }
        }
    }
    
    @Override
    public String getFactoryName() {
        final StringBuffer sb = new StringBuffer();
        sb.append("@Dog:");
        for (final IWatchDog dog : this.watchers) {
            sb.append(dog.toString() + ",");
        }
        return sb.toString();
    }
    
    @Override
    public void initConfMap() {
    }
    
    public void reload() {
        this.conf.clear();
        this.init();
    }
}
