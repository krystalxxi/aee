// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.core.Worker;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.core.Job;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: WorkerBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public class WorkerBuilder
{
    public static Job createJob(final Map<String, String> conf) throws Exception {
        if (conf == null) {
            return null;
        }
        try {
            final String workerClass = conf.get("job");
            final Job w = (Job)ObjectBuilder.build((Class)Job.class, workerClass, MapTools.getSub((Map)conf, "job"));
            return w;
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
            throw e;
        }
    }
    
    public static Worker createWorker(final Map<String, String> conf, final String name) throws Exception {
        if (conf == null) {
            return null;
        }
        try {
            final String workerClass = conf.get("worker");
            final Worker w = (Worker)ObjectBuilder.build((Class)Worker.class, workerClass, MapTools.getSub((Map)conf, "worker"));
            if (w instanceof SelfConstructedWorker) {
                return ((SelfConstructedWorker)w).createWorker(name);
            }
            w.setName(name);
            return w;
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
            throw e;
        }
    }
    
    public static Worker createWorker(final String wn) throws Exception {
        final Map<String, String> ms = AEEWorkConfig.getInstance().getWorkConfig(wn);
        return createWorker(ms, wn);
    }
}
