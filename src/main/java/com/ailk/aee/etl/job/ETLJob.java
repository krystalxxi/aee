// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import java.util.concurrent.TimeUnit;
import com.ailk.aee.core.IJobSession;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: ETLJob.java 11986 2013-07-30 10:08:30Z xiezl $")
public class ETLJob extends Job
{
    protected IExtractor extractor;
    protected List<ILoader> loaders;
    protected boolean isConcurrent;
    private boolean hasNext;
    private int queueSize;
    private int poolSize;
    private BlockingQueue<IBusinessObject> queue;
    
    public ETLJob() {
        this.extractor = null;
        this.loaders = new ArrayList<ILoader>();
        this.isConcurrent = true;
        this.hasNext = true;
        this.queueSize = 20;
        this.poolSize = 8;
        this.queue = null;
    }
    
    public synchronized void buildPool(final String pks) {
        this.queue = new ArrayBlockingQueue<IBusinessObject>(this.queueSize);
        final Random r = new Random();
        for (int i = 0; i < this.poolSize; ++i) {
            final Thread t = new InnerThread(this.queue, pks + "_" + r.nextFloat());
            t.start();
        }
    }
    
    public void addLoader(final ILoader l) {
        this.loaders.add(l);
    }
    
    public void setExtractor(final IExtractor e) {
        this.extractor = e;
    }
    
    public void doSingleBO(final IBusinessObject bo) {
        this.extractor.preprocess(bo);
        boolean isSuccess = true;
        for (final ILoader l : this.loaders) {
            isSuccess = l.load(bo);
            if (!isSuccess) {
                break;
            }
        }
        if (isSuccess) {
            this.extractor.finish(bo);
        }
        else {
            this.extractor.error(bo);
        }
    }
    
    public void initializeJob(final Map<String, String> m) {
        super.initializeJob((Map)m);
        if (this.extractor != null) {
            this.extractor.setJob(this);
        }
        if (this.loaders.size() > 0) {
            for (final ILoader i : this.loaders) {
                i.setJob(this);
            }
        }
    }
    
    public boolean prepare(final IJobSession ctx) throws Exception {
        final boolean b = super.prepare(ctx);
        if (this.extractor == null) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdExtractor.");
        }
        if (this.loaders.size() == 0) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdLoader.");
        }
        this.extractor.onJobStart();
        for (final ILoader l : this.loaders) {
            l.onJobStart();
        }
        if (this.isConcurrent && this.queue == null) {
            synchronized (this) {
                if (this.queue == null) {
                    this.buildPool("etl");
                }
            }
        }
        return b;
    }
    
    public void execute(final IJobSession ctx) throws Exception {
        while (this.extractor.hasNext()) {
            final IBusinessObject bo = this.extractor.next();
            if (this.isConcurrent) {
                this.doInPool(bo);
            }
            else {
                this.doSingleBO(bo);
            }
        }
        this.hasNext = false;
    }
    
    public void doInPool(final IBusinessObject bo) {
        try {
            this.queue.put(bo);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finish(final IJobSession ctx) throws Exception {
        while (this.queue != null && this.queue.size() > 0) {
            Thread.sleep(1000L);
        }
        for (final ILoader l : this.loaders) {
            l.onJobEnd();
        }
        this.extractor.onJobEnd();
    }
    
    public void finalizeJob() {
        super.finalizeJob();
    }
    
    class InnerThread extends Thread
    {
        BlockingQueue<IBusinessObject> queue;
        String name;
        
        InnerThread(final BlockingQueue<IBusinessObject> q, final String n) {
            super(n);
            this.queue = null;
            this.name = null;
            this.queue = q;
            this.name = n;
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    do {
                        final IBusinessObject bo = this.queue.poll(1L, TimeUnit.SECONDS);
                        if (bo != null) {
                            ETLJob.this.doSingleBO(bo);
                        }
                    } while (ETLJob.this.hasNext || this.queue.size() != 0);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
}
