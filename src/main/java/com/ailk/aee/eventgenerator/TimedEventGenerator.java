// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.EventGenerator;

@CVSID("$Id: TimedEventGenerator.java 60270 2013-11-03 14:48:37Z tangxy $")
public class TimedEventGenerator extends EventGenerator
{
    private LinkedBlockingQueue<Date> data;
    private int seconds;
    private ScheduledExecutorService executor;
    
    public TimedEventGenerator() {
        this(0);
    }
    
    public TimedEventGenerator(final int s) {
        this.data = new LinkedBlockingQueue<Date>();
        this.seconds = 1;
        this.executor = null;
        this.seconds = s;
    }
    
    @Override
    public Object[] generate() {
        this.init();
        try {
            final Date d = this.data.take();
            return new Object[] { d };
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return new Object[0];
        }
    }
    
    public int getSecond() {
        return this.seconds;
    }
    
    public void init() {
        if (this.executor == null) {
            (this.executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(""))).scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    TimedEventGenerator.this.data.offer(new Date());
                }
            }, this.seconds, this.seconds, TimeUnit.SECONDS);
        }
    }
    
    public void setSecond(final int second) {
        this.seconds = second;
    }
    
    private static class NamedThreadFactory implements ThreadFactory
    {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber;
        private final String namePrefix;
        
        private NamedThreadFactory(final String name) {
            this.threadNumber = new AtomicInteger(1);
            final SecurityManager s = System.getSecurityManager();
            this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
            this.namePrefix = "aee-timeevent-" + name + "-thread-";
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            t.setDaemon(true);
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}
