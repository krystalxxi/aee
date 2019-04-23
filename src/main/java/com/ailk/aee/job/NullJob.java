// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: NullJob.java 60270 2013-11-03 14:48:37Z tangxy $")
public class NullJob extends Job
{
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        Thread.currentThread().join();
    }
}
