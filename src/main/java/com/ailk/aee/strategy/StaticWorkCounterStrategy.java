// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.strategy;

import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StaticWorkCounterStrategy.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StaticWorkCounterStrategy implements ICounterStrategy
{
    private float v;
    
    public StaticWorkCounterStrategy() {
        this.v = 0.0f;
    }
    
    public StaticWorkCounterStrategy(final float v) {
        this.v = 0.0f;
        this.v = v;
    }
    
    public StaticWorkCounterStrategy(final int v) {
        this.v = 0.0f;
        this.v = v;
    }
    
    @Override
    public float calc(final Worker jw) {
        return this.v;
    }
}
