// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.platform.service.stat.Counter;
import com.ailk.aee.platform.service.stat.IValueProvider;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.platform.service.stat.ValueStater;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.platform.service.stat.StatManager;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StatManageService.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StatManageService extends CycledPlatformService
{
    @Override
    public int cycleTime() {
        return 5;
    }
    
    @Override
    public String getServiceDescription() {
        return "process Stat Infomation";
    }
    
    @Override
    public String getServiceName() {
        return "STAT";
    }
    
    @Override
    public void onCycleTime() {
        this.tickall();
    }
    
    private void tickall() {
        StatManager.getInstance().tick();
        StatManager.getInstance().saveToRRD();
    }
    
    @PlatformServiceMethod
    public Map<String, String> detailInfo(final Map<String, String> args2) {
        final Map<String, String> m = new HashMap<String, String>();
        final Map<String, String> args3 = new HashMap<String, String>();
        final StatManager sm = StatManager.getInstance();
        if (args3 == null || args2.size() == 0) {
            for (final String sv : sm.getAllKeys()) {
                args3.put(sv, "0");
            }
        }
        else {
            args3.putAll(args2);
        }
        int sec = 60;
        if (args3.containsKey("interval")) {
            final String v = args3.get("interval");
            args3.remove("interval");
            sec = Integer.parseInt(v);
        }
        for (final String s : args3.keySet()) {
            final ValueStater vp = sm.getValueStater(s);
            if (vp == null) {
                m.put("s", "not exist such StatReporter");
            }
            else {
                m.put(s, vp.getTimeWindowInfo(sec));
            }
        }
        m.put("AEE_RESULT_CODE", "0");
        m.put("AEE_RESULT_INFO", "STAT.detailInfo OK");
        return m;
    }
    
    @PlatformServiceMethod
    public Map<String, String> mWorkerInfo() {
        final Map<String, String> m = new HashMap<String, String>();
        final StatManager sm = StatManager.getInstance();
        IValueProvider vp = sm.getValueProvider("edtpw_queuesize");
        if (vp != null) {
            m.put("edtpw_queuesize", Long.toString(vp.getValue()));
        }
        else {
            m.put("edtpw_queuesize", "not supp");
        }
        vp = sm.getValueProvider("edtpw_threadcoresize");
        if (vp != null) {
            m.put("edtpw_threadcoresize", Long.toString(vp.getValue()));
        }
        else {
            m.put("edtpw_threadcoresize", "not supp");
        }
        vp = sm.getValueProvider("edtpw_threadmaxsize");
        if (vp != null) {
            m.put("edtpw_threadmaxsize", Long.toString(vp.getValue()));
        }
        else {
            m.put("edtpw_threadmaxsize", "not supp");
        }
        m.put("AEE_RESULT_CODE", "0");
        m.put("AEE_RESULT_INFO", "STAT.workerinfo OK");
        return m;
    }
    
    @PlatformServiceMethod
    public Map<String, String> workerInfo() {
        final Map<String, String> m = new HashMap<String, String>();
        final StatManager sm = StatManager.getInstance();
        IValueProvider vp = sm.getValueProvider("jobcallavgtime");
        if (vp != null) {
            m.put("jobcallavgtime", Long.toString(vp.getValue()));
        }
        else {
            m.put("jobcallavgtime", "not supp");
        }
        vp = sm.getValueProvider("jobcallcnt");
        if (vp != null) {
            final Counter c = (Counter)vp;
            m.put("jobcallcnt", Long.toString(vp.getValue()));
            m.put("jobcallcnt_total", Long.toString(c.getTotalCount()));
        }
        else {
            m.put("jobcallcnt", "not supp");
            m.put("jobcallcnt_total", "not supp");
        }
        vp = sm.getValueProvider("jobcallexpcnt");
        if (vp != null) {
            final Counter c = (Counter)vp;
            m.put("jobcallexpcnt", Long.toString(vp.getValue()));
            m.put("jobcallexpcnt_total", Long.toString(c.getTotalCount()));
        }
        else {
            m.put("jobcallexpcnt", "not supp");
            m.put("jobcallexpcnt_total", "not supp");
        }
        m.put("AEE_RESULT_CODE", "0");
        m.put("AEE_RESULT_INFO", "STAT.workerinfo OK");
        return m;
    }
}
