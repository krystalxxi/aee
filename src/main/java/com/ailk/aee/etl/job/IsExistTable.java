// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.core.Job;

public class IsExistTable extends Job
{
    private String tableUser;
    private String tableName;
    private String database;
    protected IServiceProvider isp;
    
    public IsExistTable() {
        this.tableUser = "";
        this.tableName = "";
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
        Connection conn = null;
        PreparedByNameStatement stmt = null;
        final StringBuilder sb = new StringBuilder();
        try {
            conn = (Connection)this.isp.getService(this.database);
            if (conn == null) {
                throw new Exception(this.database + " \ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            sb.append("select * From ");
            if (this.tableUser != null && this.tableUser.trim().length() > 0) {
                sb.append("all_all_tables").append(" where table_name = '").append(this.tableName.trim().toUpperCase()).append("'").append(" and owner = '").append(this.tableUser.trim().toUpperCase()).append("'");
            }
            else {
                sb.append("user_all_tables").append(" where table_name = '").append(this.tableName.trim().toUpperCase()).append("'");
            }
            stmt = new PreparedByNameStatement(conn, sb.toString());
            final ResultSet rs = stmt.executeQuery();
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
            sb.delete(0, sb.length());
        }
    }
    
    public String getTableUser() {
        return this.tableUser;
    }
    
    public void setTableUser(final String tableUser) {
        this.tableUser = tableUser;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getDatabase() {
        return this.database;
    }
    
    public void setDatabase(final String database) {
        this.database = database;
    }
}
