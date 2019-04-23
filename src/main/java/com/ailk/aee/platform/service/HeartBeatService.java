// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: HeartBeatService.java 60270 2013-11-03 14:48:37Z tangxy $")
public class HeartBeatService extends CycledPlatformService
{
    private int hbInterval;
    private AtomicLong hbcnt;
    
    public HeartBeatService() {
        this.hbInterval = 1;
        this.hbcnt = new AtomicLong(0L);
    }
    
    @PlatformServiceMethod
    public Map<String, String> bengbeng() {
        final Map<String, String> ms = new HashMap<String, String>();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "bengbeng ok!");
        ms.put("AEE_SVC_HB_VALUE", Long.toString(this.hbcnt.get()));
        return ms;
    }
    
    @Override
    public boolean cycleActionAsynchronized() {
        return false;
    }
    
    @Override
    public int cycleTime() {
        return this.hbInterval;
    }
    
    @Override
    public String getServiceDescription() {
        return "Heart Beat Service";
    }
    
    @Override
    public String getServiceName() {
        return "HB";
    }
    
    public long insidebengbeng() {
        final long l = this.hbcnt.incrementAndGet();
        if (l >= 1000000000L) {
            this.hbcnt.set(0L);
        }
        return l;
    }
    
    @Override
    public void onCycleTime() {
        this.insidebengbeng();
    }
}
