// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.session;

import com.ailk.aee.core.IJob;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.Job;
import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.ISessionBuilder;

@CVSID("$Id: SimpleJobSessionBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SimpleJobSessionBuilder implements ISessionBuilder
{
    @Override
    public IJobSession createSession(final Worker w, final Job j, final Object o) {
        final SimpleJobSession sess = new SimpleJobSession();
        sess.setWorker(w);
        sess.setJob(j);
        sess.initializeSession();
        sess.packageObject(o);
        return sess;
    }
}
