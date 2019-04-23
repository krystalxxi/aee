// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm;

import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.worker.OnceWorker;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.stringobject.StringMapConverter;
import com.ailk.aee.core.Job;
import java.sql.ResultSet;
import java.util.Map;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.AEELogger;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.AEEConf;
import com.ailk.aee.common.util.PIDUtils;
import org.apache.log4j.Logger;

public class GTMRemoteProxy
{
    private String workClass;
    private String workArgu;
    private Logger log;
    
    public GTMRemoteProxy() {
        this.workClass = "";
        this.workArgu = "{}";
        this.log = null;
        this.log = Logger.getLogger("GTMRemoteProxy.main");
    }
    
    public static void main(final String[] args) {
        System.out.println(">>>>MYPID=" + PIDUtils.getPid() + "=DIPYM<<<<");
        if (args.length != 2) {
            System.err.println("must with one argument of work_inst_id and database");
            System.exit(-1);
        }
        System.out.println("instId:" + args[0] + ",database:" + args[1]);
        AEEConf.init();
        AEEPlatform.getInstance().setNodeId(Configuration.getValue("AEE_NODE_ID"));
        String workName = Configuration.getValue("AEE_WORK_NAME");
        if (null == workName || "".equals(workName)) {
            workName = "GTM";
        }
        AEEPlatform.getInstance().setWorkName(workName);
//        AEELogger.configureLogger();
        final GTMRemoteProxy proxy = new GTMRemoteProxy();
        try {
            proxy.runWorkInst(args[0], args[1]);
            try {
                proxy.updateWorkInstSuccess(args[0], args[1]);
            }
            catch (Exception s) {
                proxy.outError(ExceptionUtils.getExceptionStack(s));
            }
            System.exit(0);
        }
        catch (Exception e) {
            final String info = ExceptionUtils.getExceptionStack(e);
            proxy.outError(info);
            try {
                proxy.updateWorkInstFailed(args[0], args[1], info);
            }
            catch (Exception s2) {
                proxy.outError(ExceptionUtils.getExceptionStack(s2));
            }
            System.exit(-1);
        }
    }
    
    private void updateWorkRunInfo(final String instId, final String database) throws Exception {
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        String info = "";
        if (null == isp) {
            info = "can not get isp";
            throw new Exception(info);
        }
        if (null == database) {
            info = "can not get database : " + database;
            throw new Exception(info);
        }
        Connection conn = (Connection)isp.getService(database);
        PreparedByNameStatement stmt = null;
        try {
            if (conn == null) {
                info = "can not get database : " + database;
                throw new Exception(info);
            }
            final String sql = "UPDATE TL_GTM_WORKINST SET RESULT_CODE=:PID,RESULT_INFO =:RINFO WHERE WORK_INST_ID = :WORK_INST_ID";
            stmt = new PreparedByNameStatement(conn, sql);
            stmt.setString("WORK_INST_ID", instId);
            stmt.setString("PID", Integer.toString(PIDUtils.getPid()));
            stmt.setString("RINFO", "process id is : " + PIDUtils.getPid() + " is running");
            stmt.execute();
            conn.commit();
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            stmt = null;
            conn = null;
        }
    }
    
    public void updateWorkInstFailed(final String instId, final String database, final String lastErrorString) throws Exception {
        final Map<String, String> arg = new HashMap<String, String>();
        arg.put("WORK_INST_ID", instId);
        arg.put("RESULT_CODE", "-1");
        if (lastErrorString != null) {
            final int length = lastErrorString.length();
            if (length > 3200) {
                arg.put("RESULT_INFO", StringUtils.substring(lastErrorString, length - 3200, length));
            }
            else {
                arg.put("RESULT_INFO", lastErrorString);
            }
        }
        else {
            arg.put("RESULT_INFO", "unknown exception.");
        }
        final String sql = "update TL_GTM_WORKINST t set work_status = '3',end_date=now(),result_code = :RESULT_CODE,result_info =:RESULT_INFO where t.work_inst_id =:WORK_INST_ID";
        this.doDatabaseUpdate(database, sql, arg);
    }
    
    public void updateWorkInstSuccess(final String instId, final String database) throws Exception {
        final Map<String, String> arg = new HashMap<String, String>();
        arg.put("WORK_INST_ID", instId);
        arg.put("RESULT_CODE", "0");
        arg.put("RESULT_INFO", "success");
        final String sql = "update TL_GTM_WORKINST t set work_status = '4',end_date=now(),result_code = :RESULT_CODE,result_info =:RESULT_INFO where t.work_inst_id =:WORK_INST_ID";
        this.doDatabaseUpdate(database, sql, arg);
    }
    
    public void doDatabaseUpdate(final String database, final String sql, final Map<String, String> arg) throws Exception {
        if (sql == null || sql.trim().length() == 0) {
            return;
        }
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        Connection conn = (Connection)isp.getService(database);
        try {
            if (conn == null) {
                return;
            }
            PreparedByNameStatement stmt = new PreparedByNameStatement(conn, sql);
            stmt.setValueByMap((Map)arg);
            stmt.execute();
            stmt.close();
            stmt = null;
            conn.commit();
        }
        finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }
    
    private void fetchWorkInfo(final String instId, final String database) throws Exception {
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        String info = "";
        if (null == isp) {
            info = "can not get isp";
            throw new Exception(info);
        }
        if (null == database) {
            info = "can not get database : " + database;
            throw new Exception(info);
        }
        Connection conn = (Connection)isp.getService(database);
        PreparedByNameStatement stmt = null;
        try {
            if (conn == null) {
                info = "can not get database : " + database;
                throw new Exception(info);
            }
            final String tempSQL = "SELECT IFNULL(WORK_CLASS,'') WORK_CLASS,IFNULL(WORK_ARGU,'{}') WORK_ARGU FROM TL_GTM_WORKINST WHERE WORK_INST_ID = :WORK_INST_ID";
            stmt = new PreparedByNameStatement(conn, tempSQL);
            stmt.setString("WORK_INST_ID", instId);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.workClass = rs.getString("WORK_CLASS");
                this.workArgu = rs.getString("WORK_ARGU");
                this.outInfo("instId : " + instId + ", workClass : " + this.workClass + ", workArgu : " + this.workArgu);
            }
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            stmt = null;
            conn = null;
        }
    }
    
    private Job createJob(final String instId, final String database) throws Exception {
        if (this.workClass == null || this.workClass.length() == 0) {
            throw new Exception("workClass is null value");
        }
        Map<String, String> arg = new HashMap<String, String>();
        if (this.workArgu != null && this.workArgu.length() > 0) {
            arg = (Map<String, String>)new StringMapConverter().wrapFromString(this.workArgu);
        }
        final Job j = (Job)ObjectBuilder.build((Class)Job.class, this.workClass, (Map)arg);
        return j;
    }
    
    public void outError(final String s) {
        this.log.error((Object)s);
    }
    
    public void outInfo(final String s) {
        this.log.info((Object)s);
    }
    
    public void runWorkInst(final String instId, final String database) throws Exception {
        final OnceWorker w = new OnceWorker();
        this.updateWorkRunInfo(instId, database);
        this.outInfo("workInst for " + instId + " get!");
        this.fetchWorkInfo(instId, database);
        this.outInfo("workInst for " + instId + " create!");
        final Job j = this.createJob(instId, database);
        if (j == null) {
            throw new Exception("workInst for " + instId + " create job error!");
        }
        w.setConfig(MapTools.addPrefix(new StringMapConverter().wrapFromString(this.workArgu), "job"));
        w.setJob(j);
        this.outInfo("workInst for " + instId + " start!");
        w.runJob((Object)null);
    }
}
