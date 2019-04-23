// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IWorkerPlugin.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IWorkerPlugin
{
    void after(final IJob.JOB_STEP p0, final IJobSession p1) throws Exception;
    
    void before(final IJob.JOB_STEP p0, final IJobSession p1) throws Exception;
    
    void onException(final IJobSession p0, final Exception p1);
}
