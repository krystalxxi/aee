// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.mock;

import com.ailk.aee.eventgenerator.UnionBlockQueueEventGenerator;
import com.ailk.aee.strategy.ICounterStrategy;
import com.ailk.aee.strategy.StaticWorkCounterStrategy;
import com.ailk.aee.eventgenerator.BlockLoaderEventGenerator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.eventgenerator.IEventLoader;

public class MockEventLoader implements IEventLoader
{
    private AtomicLong l;
    private String name;
    
    public MockEventLoader(final String n) {
        this.l = new AtomicLong();
        this.name = "";
        this.name = n;
    }
    
    @Override
    public Object[] load() {
        int x = new Random().nextInt(10);
        if (x % 5 == 0) {
            System.out.println("i return 0");
            return null;
        }
        if (x <= 2) {
            x = 2;
        }
        final Object[] os = new Object[x];
        for (int i = 0; i < x; ++i) {
            os[i] = this.name + this.l.incrementAndGet();
        }
        System.out.println("i return " + os.length);
        return os;
    }
    
    @Override
    public void onPutIntoQueue(final Object o) {
        System.out.println(Thread.currentThread().getName() + "  " + "---->put QUEUE[" + o + "]");
    }
    
    public static void main(final String[] args) {
        try {
            final BlockLoaderEventGenerator b1 = new BlockLoaderEventGenerator();
            b1.setLoader(new MockEventLoader("a"));
            b1.setSleepStrategy(new StaticWorkCounterStrategy(5));
            b1.setName("HELO");
            final BlockLoaderEventGenerator b2 = new BlockLoaderEventGenerator();
            b2.setLoader(new MockEventLoader("b"));
            b2.setSleepStrategy(new StaticWorkCounterStrategy(5));
            final UnionBlockQueueEventGenerator uc = new UnionBlockQueueEventGenerator();
            uc.addBlockLoaderEventGenerator("A1", b1, 5);
            uc.addBlockLoaderEventGenerator("B1", b2, 8);
            uc.setSleepStrategy(new StaticWorkCounterStrategy(5));
            final Thread t = new Thread() {
                @Override
                public void run() {
                    int i = 0;
                    while (true) {
                        final Object[] os = uc.generate();
                        if (++i == 10) {
                            break;
                        }
                        for (final Object o : os) {
                            System.out.println(Thread.currentThread().getName() + "  " + "i am execute XXX + " + o);
                            System.out.println(uc.listQueueSize());
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException ex) {}
                        }
                    }
                    uc.stop();
                }
            };
            t.start();
            uc.start();
        }
        catch (Exception ex) {}
    }
}
