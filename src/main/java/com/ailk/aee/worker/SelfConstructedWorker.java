// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.core.Worker;

public class SelfConstructedWorker extends Worker
{
    @Override
    public void runJob(final Object o) throws Exception {
        throw new RuntimeException("please use SelfConfiguredWorker carefule\ufffd\ufffdforget the WorkerBuilder! stupid!");
    }
    
    public Worker createWorker(final String name) {
        return null;
    }
}
