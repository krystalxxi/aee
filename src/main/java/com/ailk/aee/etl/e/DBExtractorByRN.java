// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import java.sql.ResultSetMetaData;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.etl.o.MapRecord;
import java.util.Map;
import com.ailk.aee.common.conf.MapTools;
import java.util.HashMap;
import com.ailk.aee.etl.job.IBusinessObject;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.sql.SQLException;
import com.ailk.aee.common.util.ExceptionUtils;
import java.sql.ResultSet;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DBExtractorByRN.java 11039 2013-06-13 01:44:38Z xiezl $")
public class DBExtractorByRN extends AbstractExtractor
{
    private Logger log;
    private String database;
    private String selectSQL;
    private int fetchSize;
    private int index;
    private Connection conn;
    private PreparedByNameStatement select_stmt;
    private ResultSet rs;
    private long beginTime;
    
    public DBExtractorByRN() {
        this.log = Logger.getLogger((Class)this.getClass());
        this.database = "";
        this.selectSQL = "";
        this.fetchSize = 1000;
        this.index = 0;
        this.conn = null;
        this.select_stmt = null;
        this.rs = null;
        this.beginTime = 0L;
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
        if (this.rs != null) {
            try {
                this.rs.close();
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
        if (this.select_stmt != null) {
            try {
                this.select_stmt.close();
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
        if (this.conn != null) {
            try {
                this.conn.close();
            }
            catch (SQLException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("cost time:" + (System.currentTimeMillis() - this.beginTime) / 1000.0 + "s"));
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        this.beginTime = System.currentTimeMillis();
        if (this.database == null || this.database.equals("")) {
            throw new Exception("database \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.selectSQL == null || this.selectSQL.equals("")) {
            throw new Exception("selectSQL \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        this.conn = (Connection)isp.getService(this.database);
        final StringBuilder sb = new StringBuilder("");
        sb.append("select t.*,@rowno:=@rowno+1 AS rownum from (select t.* from (");
        sb.append(this.selectSQL);
        sb.append(") t) t,(SELECT @rowno:=0) s where @rowno >= :STARTNUM and @rowno <= :ENDNUM");
        this.select_stmt = new PreparedByNameStatement(this.conn, sb.toString());
    }
    
    @Override
    public void preprocess(final IBusinessObject o) {
    }
    
    @Override
    public void error(final IBusinessObject o) {
    }
    
    @Override
    public void finish(final IBusinessObject o) {
    }
    
    @Override
    public boolean hasNextObject() {
        try {
            if (this.rs != null && this.rs.next()) {
                return true;
            }
            final Map<String, String> queryParam = new HashMap<String, String>();
            queryParam.put("STARTNUM", String.valueOf(this.index * this.fetchSize + 1));
            queryParam.put("ENDNUM", String.valueOf((this.index + 1) * this.fetchSize));
            ++this.index;
            this.select_stmt.clearBatch();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("selectSQL:" + this.select_stmt.getSql()));
                this.log.debug((Object)MapTools.mapToString((Map)queryParam));
            }
            this.select_stmt.setValueByMap((Map)queryParam);
            this.rs = this.select_stmt.executeQuery();
            return this.rs != null && this.rs.next();
        }
        catch (Exception e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack(e));
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
            final ResultSetMetaData rsmd = this.rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
                final String name = rsmd.getColumnName(i).toUpperCase();
                mr.put(name, ResultSetTool.getValueByResultSet(this.rs, rsmd.getColumnType(i), name));
            }
        }
        catch (SQLException e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
        }
        return mr;
    }
}
