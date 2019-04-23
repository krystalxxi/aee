// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Timer.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Timer extends Fetcher implements IStatReporter
{
    private AtomicLong lv;
    private AtomicLong lc;
    private AtomicLong cnt;
    
    public Timer(final String name) {
        super(name, 0);
        this.lv = new AtomicLong(0L);
        this.lc = new AtomicLong(0L);
        this.cnt = new AtomicLong(0L);
    }
    
    public long getAllCallTime() {
        return this.cnt.get();
    }
    
    public long getCallTime() {
        return this.lc.get();
    }
    
    @Override
    public long getValue() {
        if (this.lc.get() == 0L) {
            return 0L;
        }
        return this.lv.get() / this.lc.get();
    }
    
    @Override
    public long getValueAndReset() {
        final long v = this.getValue();
        this.lv.set(0L);
        this.lc.set(0L);
        return v;
    }
    
    public Context time() {
        return new Context(this);
    }
    
    @Override
    public void update() {
        this.update(0L);
    }
    
    @Override
    public void update(final long l) {
        this.lv.addAndGet(l);
        this.lc.incrementAndGet();
        this.cnt.incrementAndGet();
    }
    
    public static class Context
    {
        private final Timer timer;
        private final long startTime;
        
        private Context(final Timer timer) {
            this.timer = timer;
            this.startTime = System.currentTimeMillis();
        }
        
        public void close() {
            this.stop();
        }
        
        public long stop() {
            final long elapsed = System.currentTimeMillis() - this.startTime;
            this.timer.update(elapsed);
            return elapsed;
        }
    }
}
