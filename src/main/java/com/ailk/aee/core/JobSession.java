// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: JobSession.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class JobSession implements IJobSession
{
    private static AtomicLong idGenerator;
    private final long createTime;
    private final long sessionId;
    private IJob job;
    private Worker worker;
    
    public JobSession() {
        this.job = null;
        this.worker = null;
        this.sessionId = JobSession.idGenerator.incrementAndGet();
        this.createTime = System.currentTimeMillis();
    }
    
    @Override
    public long getCreateTime() {
        return this.createTime;
    }
    
    public Date getCreateTimeAsDate() {
        return new Date(this.createTime);
    }
    
    @Override
    public IJob getJob() {
        return this.job;
    }
    
    @Override
    public abstract Object getPackagedObject();
    
    public Object getReqData() {
        return this.getPackagedObject();
    }
    
    @Override
    public long getSessionId() {
        return this.sessionId;
    }
    
    @Override
    public Worker getWorker() {
        return this.worker;
    }
    
    @Override
    public void initializeSession() {
    }
    
    @Override
    public abstract void packageObject(final Object p0);
    
    @Override
    public void setJob(final IJob job) {
        this.job = job;
    }
    
    @Override
    public void setWorker(final Worker worker) {
        this.worker = worker;
    }
    
    static {
        JobSession.idGenerator = new AtomicLong(0L);
    }
}
