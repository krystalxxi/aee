// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.sql.ResultSet;
import java.util.Map;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sp.IServiceProvider;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: CreateTableJob.java 12019 2013-08-01 06:32:37Z xiezl $")
public class CreateTableJob extends Job
{
    private Logger log;
    private String templateName;
    private String templateUser;
    private String tableName;
    private String tableUser;
    private String tablespace;
    private String storage;
    private String database;
    private String createSQL;
    private boolean nologging;
    protected IServiceProvider isp;
    
    public CreateTableJob() {
        this.log = Logger.getLogger((Class)CreateTableJob.class);
        this.templateName = "";
        this.templateUser = "";
        this.tableName = "";
        this.tableUser = "";
        this.tablespace = "";
        this.storage = "";
        this.database = "";
        this.createSQL = "";
        this.nologging = true;
        this.isp = null;
    }
    
    public void execute(final IJobSession ctx) throws Exception {
        if (this.database == null || this.database.trim().length() == 0) {
            throw new Exception("database\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.createSQL == null || this.createSQL.trim().length() == 0) {
            if (this.templateName == null || this.templateName.trim().length() == 0) {
                throw new Exception("templateName\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.tableName == null || this.tableName.trim().length() == 0) {
                throw new Exception("tableName\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        Connection conn = (Connection)this.isp.getService(this.database);
        try {
            PreparedByNameStatement stmt = null;
            if (conn == null) {
                throw new Exception("\ufffd\ufffd\ufffddatabase\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            final StringBuilder sb = new StringBuilder();
            if (this.createSQL == null || this.createSQL.trim().length() == 0) {
                if (this.storage == null || this.storage.trim().length() <= 0) {
                    if (this.tablespace == null || this.tablespace.trim().length() == 0) {
                        if (this.tableUser == null || this.tableUser.trim().length() == 0) {
                            this.tableUser = "";
                        }
                        else {
                            this.tableUser = this.tableUser.trim();
                        }
                        if (this.templateUser == null || this.templateUser.trim().length() == 0) {
                            this.templateUser = "";
                        }
                        else {
                            this.templateUser = this.templateUser.trim();
                        }
                        if (this.templateUser.equalsIgnoreCase(this.tableUser)) {
                            sb.append("select TABLESPACE_NAME from ");
                            if (this.templateUser != null && this.templateUser.trim().length() > 0) {
                                sb.append("all_tables where table_name='").append(this.templateName.trim().toUpperCase()).append("'").append(" and owner = '").append(this.templateUser.trim().toUpperCase()).append("'");
                            }
                            else {
                                sb.append("user_tables where table_name='").append(this.templateName.trim().toUpperCase()).append("'");
                            }
                            stmt = new PreparedByNameStatement(conn, sb.toString());
                            final ResultSet rs = stmt.executeQuery();
                            final List<Map<String, String>> a = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
                            if (a != null && a.size() > 0) {
                                this.tablespace = a.get(0).get("TABLESPACE_NAME");
                            }
                            stmt.close();
                            stmt = null;
                            sb.delete(0, sb.length());
                        }
                        if (this.tablespace == null || this.tablespace.length() == 0) {
                            if (this.tableUser != null && this.tableUser.trim().length() > 0) {
                                sb.append("select DEFAULT_TABLESPACE TABLESPACE_NAME from dba_users where username ='").append(this.tableUser.trim().toUpperCase()).append("'");
                            }
                            else {
                                sb.append("select DEFAULT_TABLESPACE TABLESPACE_NAME from user_users ");
                            }
                            stmt = new PreparedByNameStatement(conn, sb.toString());
                            final ResultSet rs = stmt.executeQuery();
                            final List<Map<String, String>> a = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
                            if (a != null && a.size() > 0) {
                                this.tablespace = a.get(0).get("TABLESPACE_NAME");
                            }
                            stmt.close();
                            stmt = null;
                            sb.delete(0, sb.length());
                        }
                    }
                }
                sb.append("create table ");
                if (this.tableUser != null && this.tableUser.trim().length() > 0) {
                    sb.append(this.tableUser.trim()).append(".").append(this.tableName.trim());
                }
                else {
                    sb.append(this.tableName.trim());
                }
                if (this.storage != null && this.storage.trim().length() > 0) {
                    sb.append(" ").append(this.storage);
                }
                else if (this.tablespace != null && this.tablespace.trim().length() > 0) {
                    sb.append(" TABLESPACE ").append(this.tablespace);
                }
                if (this.nologging) {
                    sb.append(" NOLOGGING");
                }
                sb.append(" as select * from ");
                if (this.templateUser != null && this.templateUser.trim().length() > 0) {
                    sb.append(this.templateUser.trim()).append(".").append(this.templateName);
                }
                else {
                    sb.append(this.templateName);
                }
                sb.append(" where 1=2 ");
            }
            else {
                sb.append(this.createSQL);
            }
            this.log.debug((Object)("createTable script:" + sb.toString()));
            stmt = new PreparedByNameStatement(conn, sb.toString());
            stmt.execute();
            stmt.close();
            stmt = null;
            sb.delete(0, sb.length());
        }
        finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }
}
