// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.worker;

import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Worker;

@CVSID("$Id: OnceWorker.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OnceWorker extends Worker
{
    @Override
    public void stop() {
    }
    
    public OnceWorker() {
    }
    
    public OnceWorker(final String name) {
        this.setName(name);
    }
}
