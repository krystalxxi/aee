// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.core.Worker;
import com.ailk.aee.strategy.ICounterStrategy;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DaemonWorker.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DaemonWorker extends OnceWorker
{
    protected ICounterStrategy sleepStrategy;
    public static int STATE_RUN;
    public static int STATE_PAUSE;
    public static int STATE_STOP;
    protected int state;
    
    public DaemonWorker() {
        this.sleepStrategy = null;
        this.state = DaemonWorker.STATE_RUN;
    }
    
    public ICounterStrategy getSleepStrategy() {
        return this.sleepStrategy;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void runCycledJob(final Object o) throws Exception {
        this.runSingleJob(o);
    }
    
    @Override
    public void runJob(final Object o) throws Exception {
        if (this.state == DaemonWorker.STATE_STOP) {
            return;
        }
        this.init();
        while (true) {
            if (this.state != DaemonWorker.STATE_PAUSE) {
                try {
                    this.runCycledJob(o);
                }
                catch (Exception e) {
                    this.dealUnhandleException(e);
                }
            }
            if (this.state == DaemonWorker.STATE_STOP) {
                break;
            }
            this.sleep();
        }
        this.fina();
    }
    
    public void setSleepStrategy(final ICounterStrategy sleepStrategy) {
        this.sleepStrategy = sleepStrategy;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public void sleep() {
        if (this.sleepStrategy != null) {
            final float v = this.sleepStrategy.calc(this);
            try {
                if (v >= 0.0f) {
                    AEEPlatform.getInstance().getLogger().debug((Object)("Worker Sleep " + v));
                    Thread.sleep((int)(v * 1000.0f));
                }
            }
            catch (InterruptedException ex) {}
        }
    }
    
    @Override
    public void stop() {
        this.state = DaemonWorker.STATE_STOP;
    }
    
    static {
        DaemonWorker.STATE_RUN = 0;
        DaemonWorker.STATE_PAUSE = 1;
        DaemonWorker.STATE_STOP = 2;
    }
}
