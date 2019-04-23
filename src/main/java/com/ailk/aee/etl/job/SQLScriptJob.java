// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.util.StringTokenizer;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.core.Job;

public class SQLScriptJob extends Job
{
    private String scriptSQL;
    private String database;
    protected IServiceProvider isp;
    
    public SQLScriptJob() {
        this.scriptSQL = "";
        this.database = "";
        this.isp = null;
    }
    
    public void execute(final IJobSession ctx) throws Exception {
        if (this.database == null || this.database.trim().length() == 0) {
            return;
        }
        if (this.scriptSQL == null || this.scriptSQL.trim().length() == 0) {
            return;
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        Connection conn = null;
        PreparedByNameStatement stmt = null;
        try {
            conn = (Connection)this.isp.getService(this.database);
            if (conn == null) {
                throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            final StringTokenizer st = new StringTokenizer(this.scriptSQL, ";", false);
            while (st.hasMoreTokens()) {
                stmt = new PreparedByNameStatement(conn, st.nextToken());
                stmt.execute();
                stmt.close();
            }
            conn.commit();
        }
        finally {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }
}
