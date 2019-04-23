// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EventGenerator.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class EventGenerator implements IEventGenerator, IEventGenerator2
{
    protected String name;
    
    public EventGenerator() {
        this.name = this.getClass().getSimpleName();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public abstract Object[] generate();
    
    @Override
    public void rollback(final Object[] obs) {
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public void stop() {
    }
}
