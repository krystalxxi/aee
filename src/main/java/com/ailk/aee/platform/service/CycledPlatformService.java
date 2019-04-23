// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: CycledPlatformService.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class CycledPlatformService extends AbstractPlatformService
{
    protected int currentTicker;
    
    public CycledPlatformService() {
        this.currentTicker = 0;
    }
    
    public boolean cycleActionAsynchronized() {
        return true;
    }
    
    public abstract int cycleTime();
    
    public abstract void onCycleTime();
    
    @Override
    public void onTicker() {
        if (!this.isRunning()) {
            return;
        }
        if (this.cycleTime() <= 0) {
            return;
        }
        ++this.currentTicker;
        if (this.currentTicker >= this.cycleTime()) {
            this.currentTicker = 0;
            if (this.cycleActionAsynchronized()) {
                AEEPlatform.getInstance().submitAsyncTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CycledPlatformService.this.onCycleTime();
                        }
                        catch (Exception e) {
                            AEEExceptionProcessor.process(e);
                        }
                    }
                });
            }
            else {
                try {
                    this.onCycleTime();
                }
                catch (Exception e) {
                    AEEExceptionProcessor.process(e);
                }
            }
        }
    }
}
