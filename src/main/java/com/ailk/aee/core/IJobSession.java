// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IJobSession.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IJobSession
{
    long getCreateTime();
    
    IJob getJob();
    
    Object getPackagedObject();
    
    long getSessionId();
    
    Worker getWorker();
    
    void initializeSession();
    
    void packageObject(final Object p0);
    
    void setJob(final IJob p0);
    
    void setWorker(final Worker p0);
}
