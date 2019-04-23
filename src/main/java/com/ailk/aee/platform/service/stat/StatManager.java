// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StatManager.java 65098 2013-11-11 13:52:23Z huwl $")
public class StatManager
{
    private static StatManager instance;
    private ConcurrentHashMap<String, ValueStater> vss;
    
    public static Counter getCounter(final String name) {
        return getInstance().getOrAddCounter(name);
    }
    
    public static StatManager getInstance() {
        return StatManager.instance;
    }
    
    public static Timer getTimer(final String name) {
        return getInstance().getOrAddTimer(name);
    }
    
    public String[] getAllKeys() {
        return this.vss.keySet().toArray(new String[0]);
    }
    
    private StatManager() {
        this.vss = new ConcurrentHashMap<String, ValueStater>();
        this.addStatReporter(new Fetcher("vmfreemem") {
            @Override
            public long getValue() {
                return Runtime.getRuntime().freeMemory();
            }
        });
    }
    
    public void addStatReporter(final IValueProvider ivp) {
        if (!this.vss.containsKey(ivp.getName())) {
            final ValueStater vs = new ValueStater(ivp);
            this.vss.put(ivp.getName(), vs);
        }
    }
    
    public ValueStater getValueStater(final String name) {
        return this.vss.get(name);
    }
    
    public IValueProvider getValueProvider(final String name) {
        if (this.vss.containsKey(name)) {
            final ValueStater vs = this.vss.get(name);
            return vs.getValueProvider();
        }
        return null;
    }
    
    public Counter getOrAddCounter(final String name) {
        synchronized (this) {
            ValueStater vs = null;
            if (!this.vss.containsKey(name)) {
                final Counter c = new Counter(name);
                this.addStatReporter(c);
                return c;
            }
            vs = this.vss.get(name);
            final IValueProvider ivp = vs.getValueProvider();
            if (ivp instanceof Counter) {
                return (Counter)ivp;
            }
        }
        return new NULLCounter(name);
    }
    
    public Timer getOrAddTimer(final String name) {
        synchronized (this) {
            ValueStater vs = null;
            if (!this.vss.containsKey(name)) {
                final Timer c = new Timer(name);
                this.addStatReporter(c);
                return c;
            }
            vs = this.vss.get(name);
            final IValueProvider ivp = vs.getValueProvider();
            if (ivp instanceof Timer) {
                return (Timer)ivp;
            }
        }
        return new NULLTimer(name);
    }
    
    public void saveToRRD() {
    }
    
    public void tick() {
        for (final ValueStater vs : this.vss.values()) {
            vs.tick();
        }
    }
    
    static {
        StatManager.instance = new StatManager();
    }
}
