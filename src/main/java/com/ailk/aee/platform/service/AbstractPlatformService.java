// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.util.PIDUtils;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.platform.annotation.MethodOption;
import java.lang.annotation.Annotation;
import com.ailk.aee.platform.annotation.MethodOptions;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.platform.IAEEPlatformService;

@CVSID("$Id: AbstractPlatformService.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class AbstractPlatformService implements IAEEPlatformService
{
    private Logger log;
    private Map<String, Method> proxyMethods;
    private Map<String, Boolean> proxyMethodNoParam;
    private AtomicBoolean running;
    
    public static Map<String, String> packageMap(final Object v) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("AEE_METHOD_RESPONSE", (v == null) ? "" : v.toString());
        return map;
    }
    
    public AbstractPlatformService() {
        this.log = Logger.getLogger((Class)AbstractPlatformService.class);
        this.proxyMethods = new HashMap<String, Method>();
        this.proxyMethodNoParam = new HashMap<String, Boolean>();
        this.running = new AtomicBoolean(false);
    }
    
    private String checkOption(final Method m, final Object arg) {
        if (m.isAnnotationPresent(MethodOptions.class)) {
            final MethodOptions ops = m.getAnnotation(MethodOptions.class);
            final MethodOption[] opts = ops.options();
            if (opts == null || opts.length == 0) {
                return null;
            }
            for (int i = 0; i < opts.length; ++i) {}
        }
        return null;
    }
    
    public void initMyselfMethod() {
    }
    
    private void initMyselfMethodAutoByAnnotation() {
        final Method[] arr$;
        final Method[] ms = arr$ = this.getClass().getMethods();
        for (final Method m : arr$) {
            Label_0176: {
                if (m.isAnnotationPresent(PlatformServiceMethod.class)) {
                    final Class<?>[] params = m.getParameterTypes();
                    Boolean b = new Boolean(true);
                    if (params.length != 0) {
                        if (params.length != 1 || !params[0].isAssignableFrom(Map.class)) {
                            break Label_0176;
                        }
                        b = new Boolean(false);
                    }
                    this.proxyMethods.put(m.getName(), m);
                    this.proxyMethodNoParam.put(m.getName(), b);
                    this.log.debug((Object)("install SERVICE METHOD:" + this.getServiceName() + "." + m.getName() + " success"));
                }
            }
        }
    }
    
    public void install() throws Exception {
        this.initMyselfMethodAutoByAnnotation();
        this.initMyselfMethod();
    }
    
    public boolean isRunning() {
        return this.running.get();
    }
    
    public Map<String, String> onServiceCall(final String method, final Object arg) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("AEE_PROCESS_ID", Integer.toString(PIDUtils.getPid()));
        final Method m = this.proxyMethods.get(method);
        if (m == null) {
            map.put("AEE_RESULT_INFO", "Method [" + method + "] is not present @service [" + this.getServiceName() + "]");
            map.put("AEE_RESULT_CODE", "-1");
            return map;
        }
        final String s = this.checkOption(m, arg);
        if (s != null) {
            map.put("AEE_RESULT_INFO", s);
            map.put("AEE_RESULT_CODE", "-1");
            return map;
        }
        Object o = null;
        try {
            Boolean noParams = this.proxyMethodNoParam.get(method);
            if (noParams == null) {
                noParams = true;
            }
            if (noParams) {
                o = m.invoke(this, new Object[0]);
            }
            else {
                o = m.invoke(this, arg);
            }
        }
        catch (IllegalArgumentException e2) {
            map.put("AEE_RESULT_INFO", "Method [" + method + "] has IllegalArgumentException  @service [" + this.getServiceName() + "]");
            map.put("AEE_RESULT_CODE", "-1");
        }
        catch (IllegalAccessException e3) {
            map.put("AEE_RESULT_INFO", "Method [" + method + "] has IllegalAccessException  @service [" + this.getServiceName() + "]");
            map.put("AEE_RESULT_CODE", "-1");
        }
        catch (InvocationTargetException e) {
            map.put("AEE_RESULT_INFO", "Method [" + method + "] has InvocationTargetException  @service [" + this.getServiceName() + "]");
            map.put("AEE_RESULT_CODE", "-1");
            this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e.getTargetException()));
        }
        if (o == null) {
            map.put("AEE_RESULT_CODE", "0");
            map.put("AEE_RESULTINFO", this.getServiceName() + "." + method + " OK!");
        }
        else if (o instanceof Map) {
            final Map<?, ?> tm = (Map<?, ?>)o;
            for (final Map.Entry<?, ?> entry : tm.entrySet()) {
                if (entry.getValue() != null) {
                    map.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }
        else {
            map = packageMap(o);
        }
        if (map.containsKey("AEE_RESULT_CODE")) {
            map.put("AEE_RESULT_CODE", "0");
        }
        return map;
    }
    
    public void onTicker() {
    }
    
    public void start() throws Exception {
        this.running.set(true);
    }
    
    public void stop() {
        this.running.set(false);
    }
}
