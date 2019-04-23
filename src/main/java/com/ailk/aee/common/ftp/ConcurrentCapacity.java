// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.ftp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.io.Serializable;

@CVSID("$Id$")
public class ConcurrentCapacity implements Serializable
{
    private Semaphore sem;
    private int capacity;
    private int seconds;
    private static final long serialVersionUID = -5056130359460509593L;
    
    public ConcurrentCapacity(final int capacity, final int seconds) {
        this.sem = null;
        this.capacity = 0;
        this.seconds = 0;
        this.sem = new Semaphore(capacity);
        this.capacity = capacity;
        this.seconds = seconds;
    }
    
    public boolean acquire() throws Exception {
        final boolean rtn = this.sem.tryAcquire(this.seconds, TimeUnit.SECONDS);
        if (!rtn) {
            throw new Exception("semaphore acquire timeout " + this.seconds + " seconds,capacity limit:" + this.capacity);
        }
        return rtn;
    }
    
    public void release() {
        this.sem.release();
    }
}
