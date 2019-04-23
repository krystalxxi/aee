// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.InputStream;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.util.Map;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.log.LogUtils;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.ailk.aee.common.conf.Configuration;
import java.util.ArrayList;
import java.util.List;

public class GTMWorkInstProcessThread extends Thread
{
    private Process process;
    private String workInstId;
    private String database;
    private String workId;
    private long startTime;
    private String remotePid;
    private String lastErrorString;
    
    public String getWorkInstId() {
        return this.workInstId;
    }
    
    public void setWorkInstId(final String workInstId) {
        this.workInstId = workInstId;
    }
    
    public String getDatabase() {
        return this.database;
    }
    
    public void setDatabase(final String database) {
        this.database = database;
    }
    
    public GTMWorkInstProcessThread(final String name, final String database, final String workId) {
        this.process = null;
        this.workInstId = "";
        this.database = "";
        this.workId = "";
        this.startTime = System.currentTimeMillis();
        this.remotePid = "";
        this.lastErrorString = "";
        this.workInstId = name;
        this.database = database;
        this.workId = workId;
        this.setName("MONITOR_FOR_" + name);
    }
    
    public String getLastErrorString() {
        return this.lastErrorString;
    }
    
    public String getRemotePid() {
        return this.remotePid;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    @Override
    public void interrupt() {
        super.interrupt();
    }
    
    public List<String> readGTMRemoteProxyConfig() {
        final List<String> list = new ArrayList<String>();
        final String cfgPath = Configuration.getValue("AEE_HOME") + File.separator + "etc" + File.separator + "gtm.cfg";
        final File f = new File(cfgPath);
        if (!f.exists()) {
            this.lastErrorString = "read gtm.cfg error: gtm.cfg is not exist";
            return null;
        }
        BufferedReader buffreader = null;
        try {
            buffreader = new BufferedReader(new FileReader(new File(cfgPath)));
            for (String s = buffreader.readLine(); s != null; s = buffreader.readLine()) {
                if (!s.trim().startsWith("#")) {
                    list.add(s);
                }
            }
        }
        catch (IOException e) {
            this.lastErrorString = "read gtm.cfg error:" + e.getMessage();
            return null;
        }
        finally {
            if (buffreader != null) {
                try {
                    buffreader.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return list;
    }
    
    @Override
    public void run() {
        InputStreamReader reader = null;
        this.updateWorkInstStart();
        this.process = this.startGTMRemoteProxy();
        if (this.process == null) {
            if (this.lastErrorString == null || this.lastErrorString.length() == 0) {
                this.lastErrorString = "can't create process";
            }
            GTMPool.getInstance().sendFinishNotice(this.workInstId);
            return;
        }
        final StringBuilder resultbuffer = new StringBuilder("");
        final char[] cbuf = new char[8192];
        try {
            reader = new InputStreamReader(this.process.getInputStream());
            int length = -1;
            while ((length = reader.read(cbuf)) != -1) {
                resultbuffer.append(cbuf, 0, length);
                LogUtils.logPlatform("from InstId:" + this.workInstId, new String(cbuf, 0, length));
                if (resultbuffer.length() > 3072) {
                    resultbuffer.delete(0, 1024);
                }
            }
            final int ret = this.process.waitFor();
            if (ret != 0) {
                resultbuffer.append("exitValue:").append(ret).append("\n");
                this.lastErrorString = resultbuffer.toString();
            }
        }
        catch (Exception e) {
            this.lastErrorString = ExceptionUtils.getExceptionStack(e);
        }
        finally {
            if (this.process != null) {
                this.process.destroy();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            GTMPool.getInstance().sendFinishNotice(this.workInstId);
        }
    }
    
    public void setLastErrorString(final String lastErrorString) {
        this.lastErrorString = lastErrorString;
    }
    
    public void setRemotePid(final String remotePid) {
        this.remotePid = remotePid;
    }
    
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    public Process startGTMRemoteProxy() {
        final ProcessBuilder pb = new ProcessBuilder(new String[0]);
        final List<String> list = this.readGTMRemoteProxyConfig();
        if (list == null || list.size() == 0) {
            return null;
        }
        list.add(this.workInstId);
        list.add(this.database);
        pb.command(list);
        pb.redirectErrorStream(true);
        final Map<String, String> env = pb.environment();
        env.put("AEE_NODE_ID", AEEPlatform.getInstance().getNodeId());
        env.put("AEE_WORK_NAME", this.workInstId);
        env.put("WORK_INST_ID", this.workInstId);
        env.put("WORK_ID", this.workId);
        env.put("LOG4J_CONF_FILE", "gtm_logger.properties");
        env.put("AEE_HOME", Configuration.getValue("AEE_HOME"));
        try {
            return pb.start();
        }
        catch (IOException e) {
            this.lastErrorString = "create process error:" + e.getMessage();
            return null;
        }
    }
    
    public void updateWorkInstStart() {
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
    
    public void updateWorkInstFailed() {
        final Map<String, String> arg = new HashMap<String, String>();
        arg.put("WORK_INST_ID", this.workInstId);
        arg.put("RESULT_CODE", "-1");
        if (this.lastErrorString != null) {
            final int length = this.lastErrorString.length();
            if (length > 3200) {
                arg.put("RESULT_INFO", StringUtils.substring(this.lastErrorString, length - 3200, length));
            }
            else {
                arg.put("RESULT_INFO", this.lastErrorString);
            }
        }
        else {
            arg.put("RESULT_INFO", "unknown exception.");
        }
        final String sql = "update TL_GTM_WORKINST t set work_status = '3',end_date=sysdate,result_code = :RESULT_CODE,result_info =:RESULT_INFO where t.work_inst_id =:WORK_INST_ID";
        try {
            this.doDatabaseUpdate(this.database, sql, arg);
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
        }
    }
    
    public void updateWorkInstSuccess() {
        final Map<String, String> arg = new HashMap<String, String>();
        arg.put("WORK_INST_ID", this.workInstId);
        arg.put("RESULT_CODE", "0");
        arg.put("RESULT_INFO", "success");
        final String sql = "update TL_GTM_WORKINST t set work_status = '4',end_date=sysdate,result_code = :RESULT_CODE,result_info =:RESULT_INFO where t.work_inst_id =:WORK_INST_ID";
        try {
            this.doDatabaseUpdate(this.database, sql, arg);
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
        }
    }
    
    public static class InputReader extends Thread
    {
        GTMWorkInstProcessThread t;
        InputStream is;
        String type;
        
        InputReader(final GTMWorkInstProcessThread t, final InputStream in) {
            this(in, "OUTPUT", t);
        }
        
        InputReader(final InputStream is, final String type, final GTMWorkInstProcessThread t) {
            this.is = is;
            this.type = type;
            this.t = t;
        }
        
        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(this.is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    LogUtils.logError(line);
                    System.out.println(this.type + "-->" + line);
                    this.t.setLastErrorString(line);
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}
