// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.util.StringTokenizer;
import java.sql.Connection;
import com.ailk.aee.common.util.ExceptionUtils;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.core.Job;

public abstract class TableDataJob extends Job
{
    protected String database;
    protected String selectSQL;
    protected String updateLoadStateSQL;
    protected String updateFinishStateSQL;
    protected String updateExceptionStateSQL;
    protected String insertBakSQL;
    protected String deleteAfterBakSQL;
    protected IServiceProvider isp;
    protected boolean isInit;
    
    public TableDataJob() {
        this.database = "";
        this.selectSQL = "";
        this.updateLoadStateSQL = "";
        this.updateFinishStateSQL = "";
        this.updateExceptionStateSQL = "";
        this.insertBakSQL = "";
        this.deleteAfterBakSQL = "";
        this.isp = null;
        this.isInit = false;
    }
    
    protected void initCheck() throws Exception {
        if (this.database.equals("")) {
            throw new Exception("database\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.updateFinishStateSQL.equals("")) {
            throw new Exception("updateFinishStateSQL\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.updateExceptionStateSQL.equals("")) {
            throw new Exception("updateExceptionStateSQL\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        this.isInit = true;
    }
    
    public abstract Map<String, String> doByData(final IJobSession p0, final Map<String, String> p1) throws Exception;
    
    @Override
    public void execute(final IJobSession session) throws Exception {
        if (!this.isInit) {
            this.initCheck();
        }
        final Map<String, String> m = (Map<String, String>)session.getPackagedObject();
        this.load(session, m);
        boolean isOK = true;
        Exception lastException = null;
        final Map<String, String> resultInfo = new HashMap<String, String>();
        resultInfo.put("RESULT_CODE", "0");
        resultInfo.put("RESULT_INFO", "OK.");
        try {
            resultInfo.putAll(this.doByData(session, m));
            if (!"0".equals(resultInfo.get("RESULT_CODE"))) {
                isOK = false;
            }
        }
        catch (Exception e) {
            isOK = false;
            lastException = e;
            resultInfo.put("RESULT_CODE", "-1");
            resultInfo.put("RESULT_INFO", ExceptionUtils.getExceptionStack(e));
        }
        finally {
            this.finish(session, isOK, m, resultInfo);
            if (lastException != null) {
                throw lastException;
            }
        }
    }
    
    protected void doDatabaseUpdate(final IJobSession sess, final String sql, final Map<String, String> arg) throws Exception {
        if (sql == null || sql.trim().length() == 0) {
            return;
        }
        Connection conn = (Connection)this.isp.getService(this.database);
        PreparedByNameStatement stmt = null;
        try {
            if (conn == null) {
                return;
            }
            final StringTokenizer st = new StringTokenizer(sql, ";", false);
            while (st.hasMoreElements()) {
                final String tempSql = st.nextToken();
                if (tempSql != null && tempSql.trim().length() > 0) {
                    stmt = new PreparedByNameStatement(conn, tempSql);
                    stmt.setValueByMap((Map)arg);
                    stmt.execute();
                    stmt.close();
                    stmt = null;
                }
            }
            conn.commit();
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            stmt = null;
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
    
    private void load(final IJobSession session, final Map<String, String> m) throws Exception {
        this.doDatabaseUpdate(session, this.updateLoadStateSQL, m);
    }
    
    protected String getDateString(final Date d) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }
    
    private void finish(final IJobSession session, final boolean isSuccess, final Map<String, String> m, final Map<String, String> resultInfo) throws Exception {
        final Map<String, String> ma = new HashMap<String, String>();
        ma.putAll(m);
        ma.putAll(resultInfo);
        ma.put("OPERATION_DATE", this.getDateString(new Date()));
        if (!isSuccess) {
            this.doDatabaseUpdate(session, this.updateExceptionStateSQL, ma);
        }
        else {
            this.doDatabaseUpdate(session, this.updateFinishStateSQL, ma);
        }
        if (this.insertBakSQL != null && this.insertBakSQL.length() > 0) {
            this.doDatabaseUpdate(session, this.insertBakSQL, ma);
        }
        if (this.deleteAfterBakSQL != null && this.deleteAfterBakSQL.length() > 0) {
            this.doDatabaseUpdate(session, this.deleteAfterBakSQL, ma);
        }
    }
}
