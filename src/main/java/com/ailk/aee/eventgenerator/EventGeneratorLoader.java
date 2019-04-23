// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import com.ailk.aee.core.IEventGenerator;

public class EventGeneratorLoader implements IEventLoader
{
    private IEventGenerator generator;
    
    public EventGeneratorLoader() {
        this.generator = null;
    }
    
    @Override
    public Object[] load() {
        if (this.generator != null) {
            return this.generator.generate();
        }
        return null;
    }
    
    @Override
    public void onPutIntoQueue(final Object o) {
    }
}
