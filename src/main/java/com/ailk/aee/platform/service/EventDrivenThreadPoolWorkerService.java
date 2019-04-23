// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.worker.EventDrivenThreadPoolWorker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EventDrivenThreadPoolWorkerService.java 60270 2013-11-03 14:48:37Z tangxy $")
public class EventDrivenThreadPoolWorkerService extends AbstractPlatformService
{
    private EventDrivenThreadPoolWorker w;
    
    public EventDrivenThreadPoolWorkerService(final EventDrivenThreadPoolWorker w) {
        this.w = null;
        this.w = w;
    }
    
    @PlatformServiceMethod
    public Map<String, String> pw() {
        final Map<String, String> m = new HashMap<String, String>();
        m.put("queue_size", this.w.getQueueSizeInfo());
        m.put("active_thread_size", this.w.getActiveThreadSizeInfo());
        m.put("core_thread_size", this.w.getCoreThreadSizeInfo());
        m.put("current_object", this.w.getCurrentObjectInfo());
        return m;
    }
    
    @PlatformServiceMethod
    public Map<String, String> currp() {
        final Map<String, String> m = new HashMap<String, String>();
        m.put("current_object", this.w.getCurrentObjectInfo());
        return m;
    }
    
    @PlatformServiceMethod
    public Map<String, String> setCoreSize(final Map<String, String> args) {
        String sNew;
        final String sOld = sNew = this.w.getCoreThreadSizeInfo();
        if (args != null && args.size() > 0) {
            sNew = args.get("core_thread_size");
        }
        if (sNew == null) {
            sNew = sOld;
        }
        this.w.setThreadCount(Integer.parseInt(sNew));
        final Map<String, String> m = new HashMap<String, String>();
        m.put("new_core_thread_size", sNew);
        m.put("old_core_thread_size", this.w.getCoreThreadSizeInfo());
        m.put("current_object", this.w.getCurrentObjectInfo());
        return m;
    }
    
    @Override
    public String getServiceDescription() {
        return "Service For EventDrivenThreadPoolWorker";
    }
    
    @Override
    public String getServiceName() {
        return "EDTPWS";
    }
}
