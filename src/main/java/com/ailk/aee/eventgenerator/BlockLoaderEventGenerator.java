// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.log.LogUtils;
import com.ailk.aee.core.Worker;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import com.ailk.aee.strategy.ICounterStrategy;
import java.util.concurrent.BlockingQueue;

public class BlockLoaderEventGenerator extends LoaderEventGenerator
{
    private int state;
    protected BlockingQueue<Object> queue;
    protected boolean blockable;
    protected int queueSize;
    protected ICounterStrategy sleepStrategy;
    protected LoaderThread loadThread;
    private boolean isInit;
    
    public BlockLoaderEventGenerator() {
        this.state = 0;
        this.queue = null;
        this.blockable = true;
        this.queueSize = 8;
        this.sleepStrategy = null;
        this.loadThread = null;
        this.isInit = false;
    }
    
    @Override
    public void start() {
        this.init();
    }
    
    public BlockingQueue<Object> getQueue() {
        return this.queue;
    }
    
    public void setQueue(final BlockingQueue<Object> queue) {
        this.queue = queue;
    }
    
    @Override
    public void stop() {
        this.state = 1;
    }
    
    public ICounterStrategy getSleepStrategy() {
        return this.sleepStrategy;
    }
    
    public void setSleepStrategy(final ICounterStrategy sleepStrategy) {
        this.sleepStrategy = sleepStrategy;
    }
    
    public int getQueueSize() {
        return this.queueSize;
    }
    
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    
    public void init() {
        if (!this.isInit) {
            synchronized (this) {
                if (!this.isInit) {
                    this.isInit = true;
                    this.queue = new LinkedBlockingQueue<Object>();
                    (this.loadThread = new LoaderThread()).setDaemon(true);
                    this.loadThread.setName("BLEGTOR_" + this.getName());
                    this.loadThread.start();
                }
            }
        }
    }
    
    public Object take() {
        final Object[] os = this.generate();
        if (os == null || os.length == 0) {
            return null;
        }
        return os[0];
    }
    
    @Override
    public Object[] generate() {
        this.init();
        Object t = null;
        if (this.queue == null) {
            return new Object[0];
        }
        Label_0076: {
            if (this.blockable) {
                try {
                    do {
                        t = this.queue.poll(1L, TimeUnit.SECONDS);
                        if (t != null) {
                            break Label_0076;
                        }
                    } while (this.state != 2);
                    return new Object[0];
                }
                catch (InterruptedException e) {}
            }
            else {
                t = this.queue.poll();
            }
        }
        if (t == null) {
            return new Object[0];
        }
        return new Object[] { t };
    }
    
    public boolean isBlockable() {
        return this.blockable;
    }
    
    public void setBlockable(final boolean blockable) {
        this.blockable = blockable;
    }
    
    public Object[] load() {
        if (this.loader != null && this.state != 1) {
            return this.loader.load();
        }
        return new Object[0];
    }
    
    public void onLoad(final Object o) {
        this.loader.onPutIntoQueue(o);
    }
    
    class LoaderThread extends Thread
    {
        private void doSleep() {
            if (BlockLoaderEventGenerator.this.state == 1 && BlockLoaderEventGenerator.this.queue.size() == 0) {
                BlockLoaderEventGenerator.this.state = 2;
            }
            if (BlockLoaderEventGenerator.this.sleepStrategy != null) {
                final float f = BlockLoaderEventGenerator.this.sleepStrategy.calc(null);
                if (f > 0.0f) {
                    try {
                        LogUtils.logPlatform("EVENT_GENERATOR" + this.getName(), "Thread Sleep for " + f + " seconds");
                        Thread.sleep((long)(f * 1000.0f));
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
            if (BlockLoaderEventGenerator.this.state == 1 && BlockLoaderEventGenerator.this.queue.size() == 0) {
                BlockLoaderEventGenerator.this.state = 2;
            }
        }
        
        @Override
        public void run() {
            while (true) {
                if (BlockLoaderEventGenerator.this.state == 1) {
                    this.doSleep();
                }
                else {
                    if (BlockLoaderEventGenerator.this.state == 2) {
                        break;
                    }
                    if (BlockLoaderEventGenerator.this.queue.size() >= BlockLoaderEventGenerator.this.queueSize / 2 + 1) {
                        this.doSleep();
                    }
                    else {
                        final Object[] os = BlockLoaderEventGenerator.this.load();
                        if (os == null || os.length == 0) {
                            this.doSleep();
                        }
                        else {
                            for (final Object o : os) {
                                final boolean b = BlockLoaderEventGenerator.this.queue.offer(o);
                                if (!b) {
                                    break;
                                }
                                BlockLoaderEventGenerator.this.onLoad(o);
                            }
                        }
                    }
                }
            }
            AEEPlatform.getInstance().getLogger().info((Object)"Thread was asked for quit,Clear Data Complete.");
        }
    }
}
