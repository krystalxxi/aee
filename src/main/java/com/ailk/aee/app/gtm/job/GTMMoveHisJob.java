// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.core.Job;

public class GTMMoveHisJob extends Job
{
    protected String insertSQL;
    protected String deleteSQL;
    protected String database;
    protected IServiceProvider isp;
    protected boolean isInit;
    
    public GTMMoveHisJob() {
        this.insertSQL = "";
        this.deleteSQL = "";
        this.database = "";
        this.isp = null;
        this.isInit = false;
    }
    
    protected void initCheck() throws Exception {
        if (this.isInit) {
            return;
        }
        if (this.database.equals("")) {
            throw new Exception("database call not be null");
        }
        if (this.insertSQL.equals("")) {
            throw new Exception("insertSQL call not be null");
        }
        if (this.deleteSQL.equals("")) {
            throw new Exception("deleteSQL call not be null");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        this.isInit = true;
    }
    
    public void finish(final IJobSession sess, final Map<String, String> m) throws Exception {
        final Map<String, String> ma = new HashMap<String, String>();
        ma.putAll(m);
        if (this.insertSQL != null && this.insertSQL.length() > 0) {
            this.doDatabaseUpdate(sess, this.insertSQL, ma);
        }
        if (this.deleteSQL != null && this.deleteSQL.length() > 0) {
            this.doDatabaseUpdate(sess, this.deleteSQL, ma);
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
    
    public void execute(final IJobSession ctx) throws Exception {
        this.initCheck();
        final Map<String, String> m = (Map<String, String>)ctx.getPackagedObject();
        this.finish(ctx, m);
    }
}
