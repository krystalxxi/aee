// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.plugin;

import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.IJob;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.IWorkerPlugin;

@CVSID("$Id: LoggerWorkerPlugin.java 60270 2013-11-03 14:48:37Z tangxy $")
public class LoggerWorkerPlugin implements IWorkerPlugin
{
    protected final Logger logger;
    
    public LoggerWorkerPlugin() {
        this.logger = Logger.getLogger((Class)LoggerWorkerPlugin.class);
    }
    
    @Override
    public void after(final IJob.JOB_STEP s, final IJobSession ctx) throws Exception {
        this.logger.info((Object)("AFTER  EVENT:" + s + ",Session=" + ctx));
    }
    
    @Override
    public void before(final IJob.JOB_STEP s, final IJobSession ctx) throws Exception {
        this.logger.info((Object)("BEFORE EVENT:" + s + ",Session=" + ctx));
    }
    
    @Override
    public void onException(final IJobSession ctx, final Exception e) {
        this.logger.info((Object)("Exception: ,Exception=" + ctx));
    }
}
