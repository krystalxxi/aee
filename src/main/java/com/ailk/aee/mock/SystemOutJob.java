// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.mock;

import java.util.Map;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: SystemOutJob.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SystemOutJob extends Job
{
    @Override
    public void dealException(final IJobSession ctx, final Exception e) {
        System.out.println("dealException");
        super.dealException(ctx, e);
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        System.out.println("execute + session=" + ctx);
    }
    
    @Override
    public void finalizeJob() {
        System.out.println("finalizeJob");
    }
    
    @Override
    public void finish(final IJobSession ctx) throws Exception {
        System.out.println("finish");
    }
    
    @Override
    public void initializeJob(final Map<String, String> m) {
        System.out.println("initializeJob");
    }
    
    @Override
    public boolean prepare(final IJobSession ctx) throws Exception {
        System.out.println("prepare");
        return true;
    }
}
