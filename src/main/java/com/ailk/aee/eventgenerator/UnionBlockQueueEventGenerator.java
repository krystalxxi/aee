// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.Iterator;
import com.ailk.aee.log.LogUtils;
import java.util.HashMap;
import java.util.Map;

public class UnionBlockQueueEventGenerator extends BlockLoaderEventGenerator
{
    private Map<String, BlockLoaderEventGenerator> gens;
    private Map<String, Integer> weights;
    private int defaultWeight;
    
    public UnionBlockQueueEventGenerator() {
        this.gens = new HashMap<String, BlockLoaderEventGenerator>();
        this.weights = new HashMap<String, Integer>();
        this.defaultWeight = 5;
    }
    
    private void log(final String s) {
        LogUtils.logPlatform("EVENT_GENERATOR " + this.getName(), s);
    }
    
    @Override
    public void stop() {
        for (final BlockLoaderEventGenerator b : this.gens.values()) {
            this.log("stop the generator reading operation of " + b.getName());
            b.stop();
        }
        this.log("fetch all data to main queue");
        int i = 0;
        for (final Map.Entry<String, BlockLoaderEventGenerator> e : this.gens.entrySet()) {
            final BlockingQueue<Object> q = e.getValue().getQueue();
            for (Object o = q.poll(); o != null; o = q.poll()) {
                this.getQueue().offer(o);
                ++i;
            }
        }
        this.log("fetch " + i + " record into main queue,waiting for sleep time");
        super.stop();
    }
    
    @Override
    public void start() {
        this.setBlockable(true);
        for (final BlockLoaderEventGenerator b : this.gens.values()) {
            b.start();
        }
        super.start();
    }
    
    @Override
    public Object[] load() {
        final ArrayList<Object> al = new ArrayList<Object>();
        for (final Map.Entry<String, BlockLoaderEventGenerator> e : this.gens.entrySet()) {
            Integer iv = this.weights.get(e.getKey());
            if (iv == null) {
                iv = this.defaultWeight;
            }
            final BlockingQueue<Object> q = e.getValue().getQueue();
            for (int i = 0; i < iv; ++i) {
                final Object o = q.poll();
                if (o != null) {
                    al.add(o);
                }
            }
        }
        return al.toArray();
    }
    
    @Override
    public void onLoad(final Object o) {
    }
    
    public void addBlockLoaderEventGenerator(final String name, final BlockLoaderEventGenerator b, final int weight) {
        synchronized (this) {
            this.weights.put(name, new Integer(weight));
            this.gens.put(name, b);
        }
    }
    
    public void addBlockLoaderEventGenerator(final String name, final BlockLoaderEventGenerator bt) {
        int weight = 0;
        int i = 0;
        for (final Integer b : this.weights.values()) {
            ++i;
            weight += b;
        }
        if (i == 0) {
            weight = 5;
        }
        else {
            weight /= i;
        }
        if (weight < 1) {
            weight = 1;
        }
        this.addBlockLoaderEventGenerator(name, bt, weight);
    }
    
    public Map<String, String> listQueueSize() {
        final Map<String, String> ms = new HashMap<String, String>();
        ms.put("GATEWAY_QUEUE", Integer.toString(this.queue.size()));
        for (final Map.Entry<String, BlockLoaderEventGenerator> e : this.gens.entrySet()) {
            ms.put(e.getKey(), Integer.toString(e.getValue().getQueue().size()));
        }
        return ms;
    }
    
    public Map<String, BlockLoaderEventGenerator> getAllSubGenerator() {
        return this.gens;
    }
}
