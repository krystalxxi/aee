// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.mock;

import com.ailk.aee.core.IJobSession;
import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: DaemonMockJob.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DaemonMockJob extends Job
{
    private AtomicLong l;
    
    public DaemonMockJob() {
        this.l = new AtomicLong(0L);
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        final long x = this.l.incrementAndGet();
        System.out.println("-->[" + Thread.currentThread().getName() + "]-->" + x + "-" + ctx.getPackagedObject());
    }
}
