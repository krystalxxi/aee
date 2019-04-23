// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import com.ailk.aee.core.EventGenerator;

public class LoaderEventGenerator extends EventGenerator
{
    protected IEventLoader loader;
    
    public IEventLoader getLoader() {
        return this.loader;
    }
    
    public void setLoader(final IEventLoader loader) {
        this.loader = loader;
    }
    
    @Override
    public Object[] generate() {
        final Object[] arr$;
        final Object[] os = arr$ = this.loader.load();
        for (final Object o : arr$) {
            this.loader.onPutIntoQueue(o);
        }
        return os;
    }
}
