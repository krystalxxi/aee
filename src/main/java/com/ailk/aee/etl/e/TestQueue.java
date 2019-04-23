// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class TestQueue
{
    public static void main(final String[] args) {
        final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
        final Thread t1 = new ThreadPut(queue, "testput1");
        t1.start();
        final Thread t2 = new ThreadGet(queue, "testget2");
        t2.start();
        final Thread t3 = new ThreadGet(queue, "testget3");
        t3.start();
    }
    
    static class ThreadPut extends Thread
    {
        BlockingQueue<String> queue;
        String name;
        
        ThreadPut(final BlockingQueue<String> q, final String n) {
            this.queue = null;
            this.name = null;
            this.queue = q;
            this.name = n;
        }
        
        @Override
        public void run() {
            final Random r = new Random();
        Label_0008_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            final String e = String.valueOf(r.nextFloat());
                            this.queue.put(e);
                            System.out.println(this.name + " put " + e);
                            Thread.sleep(500L);
                        }
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                        continue Label_0008_Outer;
                    }
//                    continue;
                }
            }
        }
    }
    
    static class ThreadGet extends Thread
    {
        BlockingQueue<String> queue;
        String name;
        
        ThreadGet(final BlockingQueue<String> q, final String n) {
            this.queue = null;
            this.name = null;
            this.queue = q;
            this.name = n;
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        final String e = this.queue.poll(1L, TimeUnit.SECONDS);
                        if (e != null) {
                            System.out.println(this.name + " get " + e);
                        }
                        Thread.sleep(2000L);
                    }
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                    continue;
                }
//                break;
            }
        }
    }
}
