// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Counter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Counter extends Fetcher implements IStatReporter
{
    private AtomicLong v;
    private AtomicLong vall;
    
    public Counter(final String name) {
        super(name, 0);
        this.v = new AtomicLong(0L);
        this.vall = new AtomicLong(0L);
    }
    
    public long getTotalCount() {
        return this.vall.get();
    }
    
    @Override
    public long getValue() {
        return this.v.get();
    }
    
    @Override
    public long getValueAndReset() {
        return this.v.getAndSet(0L);
    }
    
    @Override
    public void update() {
        this.update(1L);
    }
    
    @Override
    public void update(final long l) {
        this.v.addAndGet(l);
        this.vall.addAndGet(l);
    }
}
