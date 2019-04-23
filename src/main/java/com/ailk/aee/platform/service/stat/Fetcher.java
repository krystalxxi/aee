// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Fetcher.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class Fetcher implements IValueProvider
{
    protected String name;
    protected int index;
    
    public Fetcher(final String name) {
        this.name = name;
        this.index = 0;
    }
    
    public Fetcher(final String name, final int index) {
        this.name = name;
        this.index = index;
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public abstract long getValue();
    
    @Override
    public long getValueAndReset() {
        return this.getValue();
    }
}
