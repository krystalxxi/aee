// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.session;

import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.core.IJob;
import java.util.Map;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.Job;
import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.ISessionBuilder;

@CVSID("$Id: DynamicJobSessionBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DynamicJobSessionBuilder implements ISessionBuilder
{
    private String sessionClass;
    
    public DynamicJobSessionBuilder() {
        this.sessionClass = "com.ailk.aee.job.impl.SimpleJobSession";
    }
    
    @Override
    public IJobSession createSession(final Worker w, final Job j, final Object o) {
        try {
            final IJobSession js = (IJobSession)ObjectBuilder.build((Class)IJobSession.class, this.sessionClass, (Map)null);
            if (js != null) {
                js.setWorker(w);
                js.setJob(j);
                js.initializeSession();
                js.packageObject(o);
            }
            return js;
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
            return null;
        }
    }
}
