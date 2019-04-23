// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: DropTableJob.java 11032 2013-06-09 06:27:31Z xiezl $")
public class DropTableJob extends Job
{
    private String tableName;
    private String tableUser;
    private String database;
    protected IServiceProvider isp;
    
    public DropTableJob() {
        this.tableName = "";
        this.tableUser = "";
        this.database = "";
        this.isp = null;
    }
    
    public void execute(final IJobSession ctx) throws Exception {
        if (this.database == null || this.database.trim().length() == 0) {
            throw new Exception("database\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.tableName == null || this.tableName.trim().length() == 0) {
            throw new Exception("tableName\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        Connection conn = (Connection)this.isp.getService(this.database);
        try {
            PreparedByNameStatement stmt = null;
            if (conn == null) {
                throw new Exception("\ufffd\ufffd\ufffddatabase\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            final StringBuilder sb = new StringBuilder("drop table ");
            if (this.tableUser != null && this.tableUser.trim().length() > 0) {
                sb.append(this.tableUser).append(".");
            }
            sb.append(this.tableName);
            stmt = new PreparedByNameStatement(conn, sb.toString());
            stmt.execute();
            stmt.close();
            stmt = null;
        }
        finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }
}
