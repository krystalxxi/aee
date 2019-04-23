// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.AEEExceptionProcessor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadFactory;
import com.ailk.aee.core.Job;
import com.ailk.aee.job.NullJob;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import com.ailk.aee.core.Worker;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FixThreadWorkerWorker.java 69461 2013-11-21 06:58:37Z xiangyc $")
public class FixThreadWorkerWorker extends DaemonWorker
{
    private Map<String, Worker> workers;
    private ExecutorService es;
    
    @Override
    public void stop() {
        super.stop();
        for (final Worker w : this.workers.values()) {
            w.stop();
        }
        if (this.es != null) {
            this.es.shutdown();
        }
    }
    
    public FixThreadWorkerWorker() {
        this.workers = new HashMap<String, Worker>();
        this.es = null;
        this.setJob(new NullJob());
    }
    
    @Override
    public void runJob(final Object o) throws Exception {
        if (this.state == FixThreadWorkerWorker.STATE_STOP) {
            return;
        }
        while (true) {
            if (this.state != FixThreadWorkerWorker.STATE_PAUSE) {
                this.runCycledJob(o);
            }
            if (this.state == FixThreadWorkerWorker.STATE_STOP) {
                break;
            }
            this.sleep();
        }
        this.fina();
    }
    
    @Override
    public void initializeWorker() {
        if (this.workers.size() != 0) {
            this.es = Executors.newFixedThreadPool(this.workers.size(), new ThreadFactory() {
                private ThreadGroup tg = new ThreadGroup(FixThreadWorkerWorker.this.name);
                private AtomicLong starts = new AtomicLong(0L);
                
                @Override
                public Thread newThread(final Runnable r) {
                    final Thread t = new Thread(this.tg, r);
                    t.setName(FixThreadWorkerWorker.this.name + "_T" + Long.toString(this.starts.incrementAndGet()));
                    return t;
                }
            });
            for (final Map.Entry<String, Worker> e : this.workers.entrySet()) {
                this.es.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().setName(e.getKey());
                            e.getValue().setName(e.getKey());
                            e.getValue().start();
                        }
                        catch (Exception e) {
                            AEEExceptionProcessor.process(e);
                        }
                    }
                });
            }
        }
    }
}
