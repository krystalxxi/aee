// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.core.EventGenerator;
import com.ailk.aee.core.IEventGenerator2;
import com.ailk.aee.core.IEventGenerator;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EventDrivenWorker.java 60270 2013-11-03 14:48:37Z tangxy $")
public class EventDrivenWorker extends DaemonWorker
{
    protected IEventGenerator eventGenerator;
    
    public EventDrivenWorker() {
        this.eventGenerator = null;
    }
    
    public IEventGenerator getEventGenerator() {
        return this.eventGenerator;
    }
    
    public void onFinishBatch() {
    }
    
    public void rollbackData(final Object ooo) throws Exception {
        ((IEventGenerator2)this.eventGenerator).rollback(new Object[] { ooo });
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        if (this.eventGenerator instanceof EventGenerator) {
            ((EventGenerator)this.eventGenerator).start();
        }
    }
    
    @Override
    public void runJob(final Object ooo) throws Exception {
        if (this.eventGenerator == null) {
            this.dealUnhandleException(new Exception("Event Generator Is Null."));
            this.setState(EventDrivenWorker.STATE_STOP);
        }
        this.init();
        while (true) {
            while (this.getState() != EventDrivenWorker.STATE_STOP) {
                if (this.getState() != EventDrivenWorker.STATE_PAUSE) {
                    final Object[] os = this.eventGenerator.generate();
                    if (os == null || os.length == 0) {
                        this.onFinishBatch();
                        this.sleep();
                    }
                    else {
                        for (final Object o : os) {
                            try {
                                this.runCycledJob(o);
                            }
                            catch (Exception e) {
                                this.dealUnhandleException(e);
                            }
                        }
                    }
                }
                if (this.getState() == EventDrivenWorker.STATE_STOP) {
                    this.fina();
                    return;
                }
            }
            if (this.eventGenerator instanceof IEventGenerator2) {
                this.rollbackData(ooo);
            }
            continue;
        }
    }
    
    public void setEventGenerator(final IEventGenerator eventGenerator) {
        this.eventGenerator = eventGenerator;
    }
    
    @Override
    public void tick() {
        this.onFinishBatch();
    }
}
