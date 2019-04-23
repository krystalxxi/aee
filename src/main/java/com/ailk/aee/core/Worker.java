// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.service.stat.StatManager;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.ailk.aee.platform.service.stat.Counter;
import com.ailk.aee.platform.service.stat.Timer;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.common.stringobject.IConfigSaver;

@CVSID("$Id: Worker.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class Worker implements Runnable, IConfigSaver
{
    protected Job job;
    protected IWorkerPlugin plugin;
    protected Map<String, String> workerConfig;
    public final Timer t;
    public final Counter callCnt;
    public final Counter expCnt;
    protected String name;
    protected Logger LOG;
    
    public Logger getLogger() {
        if (this.LOG == null) {
            if (this.name.endsWith("")) {
                this.LOG = Logger.getLogger((Class)Worker.class);
            }
            else {
                this.LOG = Logger.getLogger("AEE.logger.worker." + this.name);
            }
        }
        return this.LOG;
    }
    
    public Worker() {
        this.job = null;
        this.plugin = null;
        this.workerConfig = new HashMap<String, String>();
        this.t = StatManager.getTimer("jobcallavgtime");
        this.callCnt = StatManager.getCounter("jobcallcnt");
        this.expCnt = StatManager.getCounter("jobcallexpcnt");
        this.name = "";
        this.LOG = null;
    }
    
    private void AFTER(final IJob.JOB_STEP s, final IJobSession ctx) {
        this.makePluginWork(s, ctx, false);
    }
    
    private void BEFORE(final IJob.JOB_STEP s, final IJobSession ctx) {
        this.makePluginWork(s, ctx, true);
    }
    
    public void dealUnhandleException(final Exception e) {
        AEEExceptionProcessor.process(e);
    }
    
    public void doFinalizeJob() throws Exception {
        this.BEFORE(IJob.JOB_STEP.FINALIZE, null);
        if (this.job != null) {
            this.job.finalizeJob();
        }
        this.AFTER(IJob.JOB_STEP.FINALIZE, null);
    }
    
    public void doInitializeJob() throws Exception {
        this.BEFORE(IJob.JOB_STEP.INITIALIZE, null);
        if (this.job != null) {
            this.job.initializeJob(MapTools.getSub((Map)this.workerConfig, "job"));
        }
        this.AFTER(IJob.JOB_STEP.INITIALIZE, null);
    }
    
    private void EXCEPTION(final IJobSession ctx, final Exception e) {
        if (this.plugin != null) {
            this.plugin.onException(ctx, e);
        }
        if (this.expCnt != null) {
            this.expCnt.update();
        }
        AEEExceptionProcessor.process(e);
    }
    
    public Job getJob() {
        return this.job;
    }
    
    public String getName() {
        return this.name;
    }
    
    public IWorkerPlugin getPlugin() {
        return this.plugin;
    }
    
    public Map<String, String> getWorkerConfig() {
        return this.workerConfig;
    }
    
    public void fina() throws Exception {
        try {
            this.doFinalizeJob();
        }
        catch (Exception e) {
            this.dealUnhandleException(e);
        }
    }
    
    public void init() throws Exception {
        this.initializeWorker();
        if (this.job == null) {
            this.dealUnhandleException(new Exception("not any job found,the job is null"));
        }
        try {
            this.doInitializeJob();
        }
        catch (Exception e) {
            this.dealUnhandleException(e);
        }
    }
    
    public void initializeWorker() {
        this.getLogger();
    }
    
    private void makePluginWork(final IJob.JOB_STEP s, final IJobSession ctx, final boolean isBefore) {
        if (isBefore && this.job != null) {
            this.job.setCurrentState(s);
        }
        if (this.plugin != null) {
            try {
                if (isBefore) {
                    this.plugin.before(s, ctx);
                }
                else {
                    this.plugin.after(s, ctx);
                }
            }
            catch (Exception e) {
                this.EXCEPTION(ctx, e);
            }
        }
    }
    
    @Override
    public void run() {
        try {
            this.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void runJob(final Object o) throws Exception {
        this.init();
        this.runSingleJob(o);
        this.fina();
    }
    
    public void runSingleJob(final Object o) throws Exception {
        Timer.Context ctx = null;
        if (this.t != null) {
            ctx = this.t.time();
        }
        final IJobSession session = this.job.newSession(o);
        session.setWorker(this);
        try {
            if (session == null) {
                throw new Exception("can't create session.");
            }
            boolean v = false;
            this.BEFORE(IJob.JOB_STEP.PREPARE, session);
            v = this.job.prepare(session);
            if (!v) {
                throw new Exception("can't prepare.");
            }
            this.AFTER(IJob.JOB_STEP.PREPARE, session);
            this.BEFORE(IJob.JOB_STEP.EXECUTE, session);
            this.job.execute(session);
            this.AFTER(IJob.JOB_STEP.EXECUTE, session);
            this.BEFORE(IJob.JOB_STEP.FINISH, session);
            this.job.finish(session);
            this.AFTER(IJob.JOB_STEP.FINISH, session);
        }
        catch (Exception ex) {
            this.EXCEPTION(session, ex);
            this.job.dealException(session, ex);
            throw ex;
        }
        finally {
            if (this.callCnt != null) {
                this.callCnt.update();
            }
            if (ctx != null) {
                ctx.close();
            }
        }
    }
    
    public void setConfig(final Map<String, String> conf) {
        this.setWorkerConfig(conf);
    }
    
    public void setJob(final Job job) {
        this.job = job;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setPlugin(final IWorkerPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void setWorkerConfig(final Map<String, String> workerConfig) {
        this.workerConfig.putAll(workerConfig);
    }
    
    public void start() throws Exception {
        this.start(null);
    }
    
    public void start(final Object o) throws Exception {
        this.initializeWorker();
        if (this.job == null) {
            this.dealUnhandleException(new Exception("can't find any job,the job is null"));
        }
        try {
            this.doInitializeJob();
            this.runJob(o);
            this.doFinalizeJob();
        }
        catch (Exception e) {
            this.dealUnhandleException(e);
        }
    }
    
    public void stop() {
    }
    
    public void stopImmediate() {
        this.stop();
    }
    
    public void tick() {
    }
}
