// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.core.Worker;
import com.ailk.aee.core.Job;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.service.AbstractPlatformService;
import com.ailk.aee.platform.service.EventDrivenThreadPoolWorkerService;
import com.ailk.aee.platform.service.stat.IValueProvider;
import com.ailk.aee.platform.service.stat.Fetcher;
import com.ailk.aee.platform.service.stat.StatManager;
import com.ailk.aee.core.EventGenerator;
import com.ailk.aee.platform.AEEPlatform;
import java.util.concurrent.ThreadPoolExecutor;
import com.ailk.aee.strategy.ICounterStrategy;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EventDrivenThreadPoolWorker.java 65098 2013-11-11 13:52:23Z huwl $")
public class EventDrivenThreadPoolWorker extends EventDrivenWorker
{
    protected ICounterStrategy threadCountStrategy;
    protected ThreadPoolExecutor tpe;
    protected int queueSize;
    protected int coreThreadCount;
    protected int maxThreadCount;
    protected boolean blockQueue;
    protected static final ThreadLocal<OnceWorker> workerLocal;
    protected Object arg;
    
    public ICounterStrategy getThreadCountStrategy() {
        return this.threadCountStrategy;
    }
    
    public void setThreadCountStrategy(final ICounterStrategy threadCountStrategy) {
        this.threadCountStrategy = threadCountStrategy;
    }
    
    public int getQueueSize() {
        return this.queueSize;
    }
    
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    
    public int getCoreThreadCount() {
        return this.coreThreadCount;
    }
    
    public void setCoreThreadCount(final int coreThreadCount) {
        this.coreThreadCount = coreThreadCount;
    }
    
    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }
    
    public void setMaxThreadCount(final int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }
    
    private void log(final String s) {
        AEEPlatform.getInstance().getLogger().info((Object)s);
    }
    
    @Override
    public void stop() {
        this.log("stop all read thread");
        if (this.eventGenerator instanceof EventGenerator) {
            ((EventGenerator)this.eventGenerator).stop();
        }
        if (this.tpe != null) {
            this.tpe.shutdown();
        }
        this.log("stop EventDrivenThreadPoolWorker");
        super.stop();
    }
    
    @Override
    public void stopImmediate() {
        super.stopImmediate();
    }
    
    public EventDrivenThreadPoolWorker() {
        this.threadCountStrategy = null;
        this.tpe = null;
        this.queueSize = 200;
        this.coreThreadCount = 20;
        this.maxThreadCount = 100;
        this.blockQueue = false;
        this.arg = null;
        StatManager.getInstance().addStatReporter(new Fetcher("edtpw_queuesize", 0) {
            @Override
            public long getValue() {
                if (EventDrivenThreadPoolWorker.this.tpe == null) {
                    return 0L;
                }
                return EventDrivenThreadPoolWorker.this.tpe.getQueue().size();
            }
        });
        StatManager.getInstance().addStatReporter(new Fetcher("edtpw_threadactivesize", 0) {
            @Override
            public long getValue() {
                if (EventDrivenThreadPoolWorker.this.tpe == null) {
                    return 0L;
                }
                return EventDrivenThreadPoolWorker.this.tpe.getActiveCount();
            }
        });
        StatManager.getInstance().addStatReporter(new Fetcher("edtpw_threadmaxsize", 141) {
            @Override
            public long getValue() {
                if (EventDrivenThreadPoolWorker.this.tpe == null) {
                    return 0L;
                }
                return EventDrivenThreadPoolWorker.this.tpe.getMaximumPoolSize();
            }
        });
        StatManager.getInstance().addStatReporter(new Fetcher("edtpw_threadcoresize", 141) {
            @Override
            public long getValue() {
                if (EventDrivenThreadPoolWorker.this.tpe == null) {
                    return 0L;
                }
                return EventDrivenThreadPoolWorker.this.tpe.getCorePoolSize();
            }
        });
        final EventDrivenThreadPoolWorkerService edtpws = new EventDrivenThreadPoolWorkerService(this);
        try {
            AEEPlatform.getInstance().installService(edtpws);
            edtpws.start();
        }
        catch (AEERuntimeException e) {
            AEEExceptionProcessor.process(e);
        }
        catch (Exception e2) {
            AEEExceptionProcessor.process(e2);
        }
    }
    
    private synchronized void buildpool() {
        BlockingQueue<Runnable> q = null;
        if (this.blockQueue) {
            final SynchronousQueue<Runnable> qsync = (SynchronousQueue<Runnable>)(q = new SynchronousQueue<Runnable>());
        }
        else {
            final ArrayBlockingQueue<Runnable> qarr = (ArrayBlockingQueue<Runnable>)(q = new ArrayBlockingQueue<Runnable>(this.queueSize));
        }
        (this.tpe = new ThreadPoolExecutor(this.coreThreadCount, this.maxThreadCount, 60L, TimeUnit.SECONDS, q, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
                try {
                    executor.getQueue().put(r);
                }
                catch (InterruptedException e) {
                    AEEExceptionProcessor.process(e);
                }
            }
        }) {
            @Override
            protected void afterExecute(final Runnable r, final Throwable t) {
                super.afterExecute(r, t);
            }
            
            @Override
            protected void beforeExecute(final Thread t, final Runnable r) {
                super.beforeExecute(t, r);
            }
        }).setThreadFactory(new ThreadFactory() {
            private ThreadGroup tg = new ThreadGroup(EventDrivenThreadPoolWorker.this.name);
            private AtomicLong starts = new AtomicLong(0L);
            
            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = new Thread(this.tg, r);
                t.setName(EventDrivenThreadPoolWorker.this.name + "_T" + Long.toString(this.starts.incrementAndGet()));
                t.setDaemon(true);
                return t;
            }
        });
        this.tpe.setKeepAliveTime(30L, TimeUnit.SECONDS);
        this.tpe.prestartAllCoreThreads();
    }
    
    public OnceWorker getWorker() {
        OnceWorker w = EventDrivenThreadPoolWorker.workerLocal.get();
        if (w == null) {
            w = new OnceWorker(this.name);
            try {
                final Job j = WorkerBuilder.createJob(this.getWorkerConfig());
                w.setJob(j);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            EventDrivenThreadPoolWorker.workerLocal.set(w);
        }
        return w;
    }
    
    @Override
    public void onFinishBatch() {
        if (this.threadCountStrategy != null) {
            int v = (int)this.threadCountStrategy.calc(this);
            if (v < 0) {
                v = 1;
            }
            if (v != this.coreThreadCount) {
                this.setThreadCount(v);
            }
        }
    }
    
    @Override
    public void runCycledJob(final Object o) throws Exception {
        if (this.tpe == null) {
            synchronized (this) {
                if (this.tpe == null) {
                    this.buildpool();
                }
            }
        }
        this.arg = o;
        this.tpe.execute(new Runnable() {
            @Override
            public void run() {
                final OnceWorker w = EventDrivenThreadPoolWorker.this.getWorker();
                try {
                    w.runJob(o);
                }
                catch (Exception e) {
                    AEEExceptionProcessor.process(e);
                }
            }
        });
    }
    
    public void setThreadCount(int cnt) {
        if (cnt <= 0) {
            cnt = 5;
        }
        if (this.coreThreadCount > (this.maxThreadCount = cnt)) {
            this.coreThreadCount = cnt;
        }
        if (this.tpe != null) {
            final int currcnt = this.tpe.getPoolSize();
            if (currcnt != cnt) {
                if (currcnt > cnt) {
                    this.tpe.setCorePoolSize(cnt);
                    this.tpe.setMaximumPoolSize(cnt);
                }
                else {
                    this.tpe.setCorePoolSize(cnt);
                    this.tpe.setMaximumPoolSize(cnt);
                    this.tpe.prestartAllCoreThreads();
                }
            }
            AEEPlatform.getInstance().getLogger().info((Object)("current Corthread count changed to" + cnt));
        }
    }
    
    public void shutdown() {
        this.tpe.shutdown();
    }
    
    public String getQueueSizeInfo() {
        if (this.tpe != null) {
            return Long.toString(this.tpe.getQueue().size());
        }
        return "NaN";
    }
    
    public String getActiveThreadSizeInfo() {
        if (this.tpe != null) {
            return Long.toString(this.tpe.getActiveCount());
        }
        return "NaN";
    }
    
    public String getCoreThreadSizeInfo() {
        if (this.tpe != null) {
            return Long.toString(this.tpe.getCorePoolSize());
        }
        return "NaN";
    }
    
    public String getCurrentObjectInfo() {
        if (this.tpe != null) {
            String s = this.arg.toString();
            s = StringUtils.replace(s, "\"", "");
            s = StringUtils.replace(s, "'", "");
            s = StringUtils.replace(s, "{", "");
            s = StringUtils.replace(s, "}", "");
            s = StringUtils.replace(s, ",", " ");
            return s;
        }
        return "NULL";
    }
    
    static {
        workerLocal = new ThreadLocal<OnceWorker>();
    }
}
