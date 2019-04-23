// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IJob.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IJob
{
    void dealException(final IJobSession p0, final Exception p1);
    
    void execute(final IJobSession p0) throws Exception;
    
    void finalizeJob();
    
    void finish(final IJobSession p0) throws Exception;
    
    JOB_STEP getCurrentState();
    
    void initializeJob(final Map<String, String> p0);
    
    IJobSession newSession(final Object p0);
    
    boolean prepare(final IJobSession p0) throws Exception;
    
    void setCurrentState(final JOB_STEP p0);
    
    public enum JOB_STEP
    {
        INITIALIZE, 
        PREPARE, 
        EXECUTE, 
        FINISH, 
        FINALIZE, 
        EXCEPTIONPROCESS;
    }
}
