// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.session.SimpleJobSessionBuilder;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Job.java 62239 2013-11-07 06:59:14Z huwl $")
public abstract class Job implements IJob
{
    protected ISessionBuilder isb;
    private JOB_STEP currentState;
    
    public Job() {
        this.isb = null;
        this.currentState = JOB_STEP.INITIALIZE;
    }
    
    @Override
    public void dealException(final IJobSession ctx, final Exception e) {
    }
    
    @Override
    public abstract void execute(final IJobSession p0) throws Exception;
    
    @Override
    public void finalizeJob() {
    }
    
    @Override
    public void finish(final IJobSession ctx) throws Exception {
    }
    
    @Override
    public JOB_STEP getCurrentState() {
        return this.currentState;
    }
    
    @Override
    public void initializeJob(final Map<String, String> m) {
    }
    
    @Override
    public IJobSession newSession(final Object o) {
        if (this.isb == null) {
            synchronized (this) {
                if (this.isb == null) {
                    this.isb = new SimpleJobSessionBuilder();
                }
            }
        }
        final IJobSession js = this.isb.createSession(null, this, o);
        return js;
    }
    
    @Override
    public boolean prepare(final IJobSession ctx) throws Exception {
        return true;
    }
    
    @Override
    public void setCurrentState(final JOB_STEP currentState) {
        this.currentState = currentState;
    }
}
