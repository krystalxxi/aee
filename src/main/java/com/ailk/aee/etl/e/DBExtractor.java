// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.sql.ResultSetMetaData;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.sql.SQLException;
import com.ailk.aee.common.util.ExceptionUtils;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import com.ailk.aee.common.sp.IServiceProvider;
import java.sql.ResultSet;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DBExtractor.java 11484 2013-07-11 07:11:23Z xiezl $")
public class DBExtractor extends AbstractExtractor
{
    private Logger log;
    private String database;
    private String selectSQL;
    private String preprocessSQL;
    private String errorSQL;
    private String finishSQL;
    private int fetchSize;
    private Connection conn;
    private PreparedByNameStatement select_stmt;
    private ResultSet rs;
    private IServiceProvider isp;
    private ConcurrentHashMap<Integer, String> index_name;
    private ConcurrentHashMap<Integer, Integer> index_type;
    private int column_count;
    private SimpleDateFormat sdf;
    
    public DBExtractor() {
        this.log = Logger.getLogger((Class)DBExtractor.class);
        this.database = "";
        this.selectSQL = "";
        this.preprocessSQL = "";
        this.errorSQL = "";
        this.finishSQL = "";
        this.fetchSize = 5000;
        this.conn = null;
        this.select_stmt = null;
        this.rs = null;
        this.isp = null;
        this.index_name = new ConcurrentHashMap<Integer, String>();
        this.index_type = new ConcurrentHashMap<Integer, Integer>();
        this.column_count = 0;
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
        if (this.rs != null) {
            try {
                this.rs.close();
                this.rs = null;
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
        if (this.select_stmt != null) {
            try {
                this.select_stmt.close();
                this.select_stmt = null;
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
        if (this.conn != null) {
            try {
                this.conn.close();
                this.conn = null;
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.database == null || this.database.equals("")) {
            throw new Exception("database \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.selectSQL == null || this.selectSQL.equals("")) {
            throw new Exception("selectSQL \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        this.conn = (Connection)this.isp.getService(this.database);
        if (this.conn == null) {
            throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u0221" + this.database + "\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
        }
        (this.select_stmt = new PreparedByNameStatement(this.conn, this.selectSQL)).setFetchSize(this.fetchSize);
        this.rs = this.select_stmt.executeQuery();
        final ResultSetMetaData rsmd = this.rs.getMetaData();
        String name = null;
        this.column_count = rsmd.getColumnCount();
        for (int i = 1; i <= this.column_count; ++i) {
            name = rsmd.getColumnName(i).toUpperCase();
            this.index_name.put(i, name);
            this.index_type.put(i, rsmd.getColumnType(i));
        }
    }
    
    @Override
    public void preprocess(final IBusinessObject o) {
        super.preprocess(o);
        this.executeSQL(o, this.preprocessSQL);
    }
    
    @Override
    public void error(final IBusinessObject o) {
        super.error(o);
        this.executeSQL(o, this.errorSQL);
    }
    
    @Override
    public void finish(final IBusinessObject o) {
        super.finish(o);
        this.executeSQL(o, this.finishSQL);
    }
    
    public boolean executeSQL(final IBusinessObject o, final String sql) {
        boolean flag = false;
        if (o instanceof MapRecord && sql != null && sql.trim().length() > 0) {
            final Map<String, String> m = (MapRecord)o;
            Connection conn = null;
            PreparedByNameStatement temp_stmt = null;
            try {
                conn = (Connection)this.isp.getService(this.database);
                temp_stmt = new PreparedByNameStatement(conn, sql);
                temp_stmt.clearParameters();
                temp_stmt.setValueByMap((Map)m);
                temp_stmt.execute();
                conn.commit();
                flag = true;
            }
            catch (Exception e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack(e));
                flag = false;
                if (temp_stmt != null) {
                    try {
                        temp_stmt.close();
                    }
                    catch (SQLException e2) {
                        this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e2));
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (SQLException e2) {
                        this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e2));
                    }
                }
            }
            finally {
                if (temp_stmt != null) {
                    try {
                        temp_stmt.close();
                    }
                    catch (SQLException e3) {
                        this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e3));
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (SQLException e3) {
                        this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e3));
                    }
                }
            }
        }
        return flag;
    }
    
    @Override
    public boolean hasNextObject() {
        try {
            return this.rs != null && this.rs.next();
        }
        catch (SQLException e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            return false;
        }
    }
    
    @Override
    public IBusinessObject nextObject() {
        if (this.rs == null) {
            return null;
        }
        final MapRecord mr = new MapRecord();
        try {
            for (int i = 1; i <= this.column_count; ++i) {
                final int type = this.index_type.get(i);
                String value = null;
                if (type == 2004) {
                    value = this.rs.getString(i);
                }
                else if (type == 91) {
                    final java.sql.Date date = this.rs.getDate(i);
                    if (date != null) {
                        value = this.sdf.format(date);
                    }
                }
                else if (type == 93) {
                    final Timestamp timestamp = this.rs.getTimestamp(i);
                    if (timestamp != null) {
                        value = this.sdf.format(new java.sql.Date(timestamp.getTime()));
                    }
                }
                else {
                    value = this.rs.getString(i);
                }
                if (value == null) {
                    value = "";
                }
                mr.put(this.index_name.get(i), value);
            }
        }
        catch (SQLException e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
        }
        return mr;
    }
}
