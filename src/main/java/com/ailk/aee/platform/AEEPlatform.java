// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform;

import java.util.concurrent.atomic.AtomicInteger;
import com.ailk.aee.AEEExceptionProcessor;
import java.util.Iterator;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.worker.WorkerBuilder;
import com.ailk.aee.log.LogUtils;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.config.AEEWorkConfig;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import org.apache.log4j.Logger;
import com.ailk.aee.platform.adapter.AbstractPlatformServiceAdapter;
import java.util.List;
import com.ailk.aee.platform.service.AbstractPlatformService;
import java.util.Map;
import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEPlatform.java 65098 2013-11-11 13:52:23Z huwl $")
public class AEEPlatform
{
    private static AEEPlatform instance;
    public static String CLIENT_PLATFORM_MODE_SINGLE;
    public static String CLIENT_PLATFORM_MODE_MASTER;
    public static String CLIENT_PLATFORM_MODE_SLAVE;
    public static String CLIENT_PLATFORM_MODE_DIS_MASTER;
    public static String CLIENT_PLATFORM_MODE_DIS_WATCHER;
    public static String CLIENT_PLATFORM_MODE_DIS_SLAVE;
    private String nodeId;
    private String workName;
    private String platformMode;
    private Worker worker;
    private Map<String, AbstractPlatformService> services;
    private List<AbstractPlatformServiceAdapter> adapters;
    private Logger LOG;
    private final ExecutorService asyncExecutor;
    private final ScheduledExecutorService tickerExecutor;
    
    public static AEEPlatform getInstance() {
        return AEEPlatform.instance;
    }
    
    private AEEPlatform() {
        this.worker = null;
        this.services = new HashMap<String, AbstractPlatformService>();
        this.adapters = new ArrayList<AbstractPlatformServiceAdapter>();
        this.LOG = null;
        this.asyncExecutor = new ThreadPoolExecutor(0, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory("async"));
        this.tickerExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("event"));
    }
    
    public Logger getLogger() {
        if (this.LOG == null) {
            synchronized (this) {
                if (this.LOG == null) {
                    this.LOG = Logger.getLogger("AEE.logger.works." + this.getWorkName());
                }
            }
        }
        return this.LOG;
    }
    
    public Map<String, String> callService(final String service, final String method, final Map<String, String> args) {
        AbstractPlatformService ips = null;
        if (this.services.containsKey(service)) {
            ips = this.services.get(service);
            return ips.onServiceCall(method, args);
        }
        return AbstractPlatformService.packageMap("service [" + service + "] is not support yet");
    }
    
    private String[] getFinalConfiged(final String cat) {
        final List<String> temps = new ArrayList<String>();
        final String sdef = AEEWorkConfig.getInstance().getSingleConfig("AEE.platformModes." + cat + "." + this.platformMode);
        final String[] arr$;
        final String[] sall = arr$ = StringUtils.split(sdef, ",");
        for (final String s : arr$) {
            temps.add(s);
        }
        final String splus = Configuration.getValue("AEE.works." + this.workName + ".platformModes." + cat + ".allow", "");
        if (!splus.equals("")) {
            final String[] arr$2;
            final String[] spluss = arr$2 = StringUtils.split(splus, ",");
            for (final String s2 : arr$2) {
                temps.add(s2);
            }
        }
        final String sdeny = Configuration.getValue("AEE.works." + this.workName + ".platformModes.services.deny", "");
        if (!sdeny.equals("")) {
            final String[] arr$3;
            final String[] sdenys = arr$3 = StringUtils.split(sdeny, ",");
            for (final String s3 : arr$3) {
                if (temps.contains(s3)) {
                    temps.remove(s3);
                }
            }
        }
        Collections.sort(temps);
        final HashSet<String> hs = new HashSet<String>();
        hs.addAll(temps);
        return hs.toArray(new String[0]);
    }
    
    public String getNodeId() {
        return this.nodeId;
    }
    
    public String getPlatformMode() {
        return this.platformMode;
    }
    
    public String getResourceId() {
        return this.nodeId + "." + this.workName;
    }
    
    public Map<String, AbstractPlatformService> getServices() {
        return this.services;
    }
    
    public int getTimeOut() {
        return 5;
    }
    
    public String getWorkName() {
        return this.workName;
    }
    
    public void installAdapter(final AbstractPlatformServiceAdapter a) {
        if (a != null) {
            this.adapters.add(a);
        }
    }
    
    public void installAdapter(final String name) {
        final Map<String, String> args = AEEWorkConfig.getInstance().getConfig("AEE.platforms.adapters." + name);
        final String svccls = AEEWorkConfig.getInstance().getSingleConfig("AEE.platforms.adapters." + name);
        if (svccls != null && svccls.length() > 0) {
            try {
                final AbstractPlatformServiceAdapter aps = (AbstractPlatformServiceAdapter)ObjectBuilder.build((Class)AbstractPlatformServiceAdapter.class, svccls, (Map)args);
                this.installAdapter(aps);
            }
            catch (Exception e) {
                LogUtils.logPlatform("ADAPTER", "\ufffd\u06b0\ufffd\u05f0\ufffd\u04ff\ufffd:" + svccls + "\u02b1\ufffd\ufffd\ufffd\u05b4\ufffd\ufffd\ufffd:" + e.getMessage() + ",\ufffd\ufffd\ufffd\u0534\u02fd\u04ff\ufffd");
            }
        }
        else {
            LogUtils.logPlatform("ADAPTER", "\ufffd\u07b7\ufffd\ufffd\u04b5\ufffd\ufffd\ufffd\u0637\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd:" + name + ",\ufffd\ufffd\ufffd\u0534\u02fd\u04ff\ufffd");
        }
    }
    
    public void installAdapters(final String[] names) {
        for (final String s : names) {
            this.installAdapter(s);
        }
    }
    
    public void installService(final AbstractPlatformService s) throws AEERuntimeException {
        if (s != null) {
            this.services.put(s.getServiceName(), s);
            try {
                s.install();
                LogUtils.logPlatformDebug("SERVICE", "install SERVICE=" + s.getServiceName() + ",class=" + s.getClass().getCanonicalName());
            }
            catch (Exception e) {
                LogUtils.logPlatform("SERVICE", "install SERVICE=" + s.getServiceName() + ",class=" + s.getClass().getCanonicalName() + " error Message e=" + e.getMessage());
                throw new AEERuntimeException(e.getMessage());
            }
        }
    }
    
    public void installService(final String name) throws AEERuntimeException {
        final Map<String, String> args = AEEWorkConfig.getInstance().getConfig("AEE.platforms.services." + name);
        final String svccls = AEEWorkConfig.getInstance().getSingleConfig("AEE.platforms.services." + name);
        if (svccls != null && svccls.length() > 0) {
            try {
                final AbstractPlatformService aps = (AbstractPlatformService)ObjectBuilder.build((Class)AbstractPlatformService.class, svccls, (Map)args);
                this.installService(aps);
                return;
            }
            catch (Exception e) {
                throw new AEERuntimeException("\ufffd\u06b0\ufffd\u05f0\ufffd\ufffd\ufffd\ufffd:" + svccls + "\u02b1\ufffd\ufffd\ufffd\u05b4\ufffd\ufffd\ufffd:" + e.getMessage());
            }
//            throw new AEERuntimeException("\ufffd\u07b7\ufffd\ufffd\u04b5\ufffd\ufffd\ufffd\u0637\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd:" + name);
        }
        throw new AEERuntimeException("\ufffd\u07b7\ufffd\ufffd\u04b5\ufffd\ufffd\ufffd\u0637\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd:" + name);
    }
    
    public void installServices(final String[] names) throws AEERuntimeException {
        for (final String s : names) {
            this.installService(s);
        }
    }
    
    public void prepareWorker() throws Exception {
        if (this.getPlatformMode().equals(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_MASTER) || this.getPlatformMode().equals(AEEPlatform.CLIENT_PLATFORM_MODE_MASTER) || this.getPlatformMode().equals(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_WATCHER)) {
            return;
        }
        try {
            this.worker = WorkerBuilder.createWorker(this.workName);
        }
        catch (Exception e) {
            e.printStackTrace();
            LogUtils.logPlatform("WORKER", ExceptionUtils.getExceptionStack(e));
            throw e;
        }
        LogUtils.logPlatformDebug("WORKER", "Worker " + this.workName + " prepare success");
    }
    
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }
    
    public void setPlatformMode(final String platformMode) {
        this.platformMode = platformMode;
    }
    
    public void setWorkName(final String workName) {
        System.setProperty("AEE_WORK", this.workName = workName);
    }
    
    public void start() throws AEERuntimeException {
        final String[] svcs = this.getFinalConfiged("services");
        LogUtils.logPlatformDebug("SERVICE INSTALL", "service need to install is\ufffd\ufffd" + StringUtils.join((Object[])svcs, ","));
        this.installServices(svcs);
        final String[] adps = this.getFinalConfiged("adapters");
        LogUtils.logPlatformDebug("ADAPTER INSTALL", "adapter need to install is\ufffd\ufffd" + StringUtils.join((Object[])adps, ","));
        this.installAdapters(adps);
        LogUtils.logPlatformDebug("WORK INSTALL", "work need to install is:" + this.workName);
        try {
            this.prepareWorker();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.logPlatformDebug("WORK INSTALL", "start Tick safe thread");
        this.tickerExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (final AbstractPlatformService aps : AEEPlatform.this.services.values()) {
                    aps.onTicker();
                }
                if (AEEPlatform.this.worker != null) {
                    AEEPlatform.this.worker.tick();
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
        for (final AbstractPlatformService s : this.services.values()) {
            try {
                s.start();
            }
            catch (Exception e2) {
                LogUtils.logError(ExceptionUtils.getExceptionStack(e2));
                LogUtils.logPlatform("SERVICE", "while boot Service" + s.getServiceName() + " an error occoured,quit");
            }
        }
        for (final AbstractPlatformServiceAdapter a : this.adapters) {
            try {
                a.start();
            }
            catch (Exception e2) {
                LogUtils.logPlatform("SERVICE", "while boot Adapter[" + a.getClass().getCanonicalName() + "],an error occoured,this adapter will be ignore");
            }
        }
        if (this.getPlatformMode().equals(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_MASTER) || this.getPlatformMode().equals(AEEPlatform.CLIENT_PLATFORM_MODE_MASTER) || this.getPlatformMode().equalsIgnoreCase(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_WATCHER)) {
            return;
        }
        try {
            if (this.worker != null) {
                this.worker.start();
            }
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
        }
    }
    
    public void stop() {
        for (final AbstractPlatformService s : this.services.values()) {
            try {
                LogUtils.logPlatform("SERVICE", "\u0363\u05b9Service" + s.getServiceName());
                s.stop();
            }
            catch (Exception e) {
                LogUtils.logError(ExceptionUtils.getExceptionStack(e));
                LogUtils.logPlatform("SERVICE", "\u0363\u05b9Service" + s.getServiceName() + "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u02f3\ufffd");
            }
        }
        for (final AbstractPlatformServiceAdapter a : this.adapters) {
            LogUtils.logPlatform("ADAPTER", "\u0363\u05b9Adapter" + a.getClass().getCanonicalName());
            a.stop();
        }
        LogUtils.logPlatform("SERVICE", "\u0363\u05b9\ufffd\uccbd\u05b4\ufffd\ufffd\ufffd\u07f3\ufffd");
        this.asyncExecutor.shutdown();
        LogUtils.logPlatform("SERVICE", "\u0363\u05b9Ticket\u05b4\ufffd\ufffd\ufffd\u07f3\ufffd");
        this.tickerExecutor.shutdown();
        if (this.worker != null) {
            this.worker.stop();
        }
        int v = Configuration.getIntValue("AEE_SHUTDOWN_WAIT", 0);
        if (v <= 0) {
            v = 0;
        }
        if (v > 0) {
            try {
                Thread.sleep(v * 1000);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            System.exit(0);
        }
    }
    
    public void submitAsyncTask(final Runnable r) {
        this.asyncExecutor.execute(r);
    }
    
    static {
        AEEPlatform.instance = new AEEPlatform();
        AEEPlatform.CLIENT_PLATFORM_MODE_SINGLE = "SINGLE";
        AEEPlatform.CLIENT_PLATFORM_MODE_MASTER = "MASTER";
        AEEPlatform.CLIENT_PLATFORM_MODE_SLAVE = "SLAVE";
        AEEPlatform.CLIENT_PLATFORM_MODE_DIS_MASTER = "DIS_MASTER";
        AEEPlatform.CLIENT_PLATFORM_MODE_DIS_WATCHER = "DIS_WATCHER";
        AEEPlatform.CLIENT_PLATFORM_MODE_DIS_SLAVE = "DIS_SLAVE";
    }
    
    private static class NamedThreadFactory implements ThreadFactory
    {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber;
        private final String namePrefix;
        
        private NamedThreadFactory(final String name) {
            this.threadNumber = new AtomicInteger(1);
            final SecurityManager s = System.getSecurityManager();
            this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
            this.namePrefix = "aee-platform-" + name + "-thread-";
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            t.setDaemon(true);
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }

    public static void main(String[] args) throws Exception{
        AEEPlatform aeePlatform = new AEEPlatform();
        aeePlatform.setPlatformMode("MWC");
        aeePlatform.workName="pf_actp_newactgtm";

        aeePlatform.start();
    }
}
