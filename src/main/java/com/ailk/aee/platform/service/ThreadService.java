// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class ThreadService extends AbstractPlatformService
{
    @PlatformServiceMethod
    public Map<String, String> list() {
        final Map<String, String> ms = new HashMap<String, String>();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "bengbeng ok!");
        ThreadGroup top;
        for (top = Thread.currentThread().getThreadGroup(); top.getParent() != null; top = top.getParent()) {}
        final int estimatedSize = top.activeCount() * 2;
        final Thread[] slackList = new Thread[estimatedSize];
        final int actualSize = top.enumerate(slackList);
        final Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.rightPad("id", 14));
        sb.append(StringUtils.rightPad("priority", 10));
        sb.append(StringUtils.rightPad("State", 12));
        sb.append(StringUtils.rightPad("isAlive", 10));
        sb.append(StringUtils.rightPad("isDaemon", 10));
        sb.append(StringUtils.rightPad("isInterrupted", 15));
        sb.append(StringUtils.rightPad("group", 24));
        sb.append(StringUtils.rightPad("name", 64));
        ms.put("THREAD_INFO_TITLE", sb.toString());
        for (final Thread t : list) {
            sb = new StringBuffer();
            sb.append(StringUtils.rightPad(Long.toString(t.getId()), 16));
            sb.append(StringUtils.rightPad(Integer.toString(t.getPriority()), 8));
            sb.append(StringUtils.rightPad(t.getState().toString(), 12));
            sb.append(StringUtils.rightPad(t.isAlive() ? "true" : "false", 10));
            sb.append(StringUtils.rightPad(t.isDaemon() ? "true" : "false", 10));
            sb.append(StringUtils.rightPad(t.isInterrupted() ? "true" : "false", 15));
            sb.append(StringUtils.rightPad(t.getThreadGroup().getName(), 24));
            sb.append(StringUtils.rightPad(t.getName(), 64));
            ms.put("THREAD_INFO." + t.getName(), sb.toString());
        }
        return ms;
    }
    
    @PlatformServiceMethod
    public Map<String, String> stack(final Map<String, String> arg) {
        final Map<String, String> ms = new HashMap<String, String>();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "bengbeng ok!");
        String s = arg.get("AEE_THREAD_NAME");
        if (s == null) {
            s = "main";
        }
        ThreadGroup top;
        for (top = Thread.currentThread().getThreadGroup(); top.getParent() != null; top = top.getParent()) {}
        final int estimatedSize = top.activeCount() * 2;
        final Thread[] slackList = new Thread[estimatedSize];
        final int actualSize = top.enumerate(slackList);
        final Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        final StringBuffer sb = new StringBuffer();
        for (final Thread t : list) {
            if (t.getName().equalsIgnoreCase(s) || Long.toString(t.getId()).equals(s)) {
                int i = 0;
                final StackTraceElement[] arr$2;
                final StackTraceElement[] ste = arr$2 = t.getStackTrace();
                for (final StackTraceElement ele : arr$2) {
                    ++i;
                    ms.put("INFO." + t.getName() + "." + i, ele.toString());
                }
            }
        }
        return ms;
    }
    
    @PlatformServiceMethod
    public Map<String, String> interrupt(final Map<String, String> arg) {
        final Map<String, String> ms = new HashMap<String, String>();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "bengbeng ok!");
        String s = arg.get("AEE_THREAD_NAME");
        if (s == null) {
            s = "main";
        }
        ThreadGroup top;
        for (top = Thread.currentThread().getThreadGroup(); top.getParent() != null; top = top.getParent()) {}
        final int estimatedSize = top.activeCount() * 2;
        final Thread[] slackList = new Thread[estimatedSize];
        final int actualSize = top.enumerate(slackList);
        final Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        final StringBuffer sb = new StringBuffer();
        for (final Thread t : list) {
            if (t.getName().equalsIgnoreCase(s) || Long.toString(t.getId()).equals(s)) {
                final int i = 0;
                t.interrupt();
                ms.put("INFO." + t.getName(), "interrupt");
            }
        }
        return ms;
    }
    
    @Override
    public String getServiceDescription() {
        return "Thread Helper Service";
    }
    
    @Override
    public String getServiceName() {
        return "T";
    }
}
