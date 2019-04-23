// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm;

import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.common.sp.IServiceProvider;
import java.sql.SQLException;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.log.LogUtils;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.util.DateFormatUtils;
import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.service.AbstractPlatformService;
import com.ailk.aee.platform.AEEPlatform;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class GTMPool
{
    private static GTMPool instance;
    private Logger GTMLogger;
    private ConcurrentHashMap<String, GTMWorkInstProcessThread> allThreads;
    private int maxThreadCount;
    private LinkedBlockingQueue<String> queue;
    private ConcurrentHashMap<String, String> workIdMap;
    private String deleteSQL;
    private String backSQL;
    
    public static GTMPool getInstance() {
        return GTMPool.instance;
    }
    
    private GTMPool() {
        this.GTMLogger = Logger.getLogger("AEE.logger.GTM");
        this.allThreads = new ConcurrentHashMap<String, GTMWorkInstProcessThread>();
        this.maxThreadCount = 100;
        this.queue = new LinkedBlockingQueue<String>();
        this.workIdMap = new ConcurrentHashMap<String, String>();
        this.deleteSQL = "delete from tl_gtm_workinst where work_inst_id = ?";
        this.backSQL = "insert into tl_gtm_workinst_h select * from tl_gtm_workinst where work_inst_id = ?";
        final GTMService gs = new GTMService(this);
        final Thread tMonitor = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(1000L);
                            final String workInstId = GTMPool.this.queue.poll();
                            if (workInstId != null) {
                                GTMPool.this.finishWork(workInstId);
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        continue;
                    }
                }
            }
        };
        tMonitor.setDaemon(true);
        tMonitor.start();
        try {
            AEEPlatform.getInstance().installService((AbstractPlatformService)gs);
            gs.start();
        }
        catch (AEERuntimeException e) {
            AEEExceptionProcessor.process((Exception)e);
        }
        catch (Exception e2) {
            AEEExceptionProcessor.process(e2);
        }
    }
    
    public void destroyWork(final String workInstId) {
        final GTMWorkInstProcessThread p = this.allThreads.get(workInstId);
        if (p != null) {
            p.interrupt();
        }
    }
    
    public void finishWork(final String workInstId) {
        if (this.allThreads.containsKey(workInstId)) {
            final GTMWorkInstProcessThread pt = this.allThreads.get(workInstId);
            if (!pt.isAlive()) {
                this.GTMLogger.info((Object)("FINISH-->WORK_INST_ID=" + workInstId + ",START_TIME=" + DateFormatUtils.SIMPLE_DATETIME_FORMAT.format(pt.getStartTime()) + ",END_TIME=" + DateFormatUtils.SIMPLE_DATETIME_FORMAT.format(System.currentTimeMillis()) + ",LAST_ERROR=" + pt.getLastErrorString()));
                this.move2His(workInstId, pt.getDatabase());
                this.allThreads.remove(workInstId);
            }
        }
        if (this.workIdMap.containsKey(workInstId)) {
            synchronized (this.workIdMap) {
                this.workIdMap.remove(workInstId);
            }
        }
    }
    
    public boolean isFull() {
        return this.maxThreadCount >= 0 && this.maxThreadCount <= this.allThreads.size();
    }
    
    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }
    
    public void setMaxThreadCount(final int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }
    
    public Map<String, String> listWork() {
        final Map<String, String> ms = new HashMap<String, String>();
        for (final Map.Entry<String, GTMWorkInstProcessThread> e : this.allThreads.entrySet()) {
            final String workInstId = e.getKey();
            final GTMWorkInstProcessThread p = e.getValue();
            ms.put(workInstId + "." + "WORK_INST_ID", p.getWorkInstId());
            ms.put(workInstId + "." + "REMOTE_PID", p.getRemotePid());
            ms.put(workInstId + "." + "THREAD_STATUS", p.isAlive() ? "ALIVE" : "DEATH");
            ms.put(workInstId + "." + "LAST_ERROR", p.getLastErrorString());
            ms.put(workInstId + "." + "START_TIME", DateFormatUtils.SIMPLE_DATETIME_FORMAT.format(p.getStartTime()));
        }
        return ms;
    }
    
    public void sendFinishNotice(final String workInstId) {
        try {
            this.queue.put(workInstId);
        }
        catch (InterruptedException ex) {}
    }
    
    public void startWork(final String workInstId, final String workId, final String database) {
        LogUtils.logPlatform("GTM", "now prepare to boot work_inst_id=" + workInstId);
        if (this.workIdMap.containsValue(workId)) {
            this.updateWhenExistWorkId(workInstId, database);
            this.move2His(workInstId, database);
            return;
        }
        synchronized (this.workIdMap) {
            if (this.workIdMap.containsValue(workId)) {
                this.updateWhenExistWorkId(workInstId, database);
                this.move2His(workInstId, database);
                return;
            }
            this.workIdMap.put(workInstId, workId);
        }
        final GTMWorkInstProcessThread thread = new GTMWorkInstProcessThread(workInstId, database, workId);
        this.allThreads.put(workInstId, thread);
        thread.start();
    }
    
    public void updateWhenExistWorkId(final String instId, final String database) {
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        if (null == isp) {
            return;
        }
        if (null == database) {
            return;
        }
        Connection conn = (Connection)isp.getService(database);
        PreparedByNameStatement stmt = null;
        try {
            if (conn == null) {
                return;
            }
            final String executeSQL = "UPDATE TL_GTM_WORKINST SET END_DATE=SYSDATE,WORK_STATUS='4',RESULT_CODE='0',RESULT_INFO=:RINFO WHERE work_inst_id=:WORK_INST_ID";
            stmt = new PreparedByNameStatement(conn, executeSQL);
            stmt.setString("WORK_INST_ID", instId);
            stmt.setString("RINFO", "current plan already running ,this plan be passed by system");
            stmt.execute();
            stmt.close();
            stmt = null;
            conn.commit();
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public void move2His(final String instId, final String database) {
        if (Configuration.getBooleanValue("AEE_DEBUG_MODE", false)) {
            return;
        }
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        if (null == isp) {
            return;
        }
        if (null == database) {
            return;
        }
        Connection conn = null;
        PreparedByNameStatement stmt = null;
        try {
            conn = (Connection)isp.getService(database);
            if (conn == null) {
                return;
            }
            stmt = new PreparedByNameStatement(conn, this.backSQL);
            stmt.setString(1, instId);
            stmt.execute();
            stmt.close();
            stmt = null;
            stmt = new PreparedByNameStatement(conn, this.deleteSQL);
            stmt.setString(1, instId);
            stmt.execute();
            stmt.close();
            stmt = null;
            conn.commit();
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                }
                catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    static {
        GTMPool.instance = new GTMPool();
    }
}
