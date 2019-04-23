// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.eventgenerator;

import java.util.List;
import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import java.sql.SQLException;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.platform.AEEPlatform;
import org.apache.log4j.Logger;
import java.util.Map;

public class TableDataEventLoader implements IEventLoader
{
    private String selectSQL;
    private String updateSQL;
    private String database;
    private String prefixSQL;
    private Map<String, String> mapSqlParam;
    private int maxRowNum;
    private Logger LOG;
    
    public TableDataEventLoader() {
        this.selectSQL = "";
        this.updateSQL = "";
        this.database = "";
        this.prefixSQL = "";
        this.mapSqlParam = null;
        this.maxRowNum = 1000;
        this.LOG = AEEPlatform.getInstance().getLogger();
    }
    
    public String getSelectSQL() {
        return this.selectSQL;
    }
    
    public void setSelectSQL(final String selectSQL) {
        this.selectSQL = selectSQL;
    }
    
    public String getUpdateSQL() {
        return this.updateSQL;
    }
    
    public void setUpdateSQL(final String updateSQL) {
        this.updateSQL = updateSQL;
    }
    
    public String getDatabase() {
        return this.database;
    }
    
    public void setDatabase(final String database) {
        this.database = database;
    }
    
    public String getPrefixSQL() {
        return this.prefixSQL;
    }
    
    public void setPrefixSQL(final String prefixSQL) {
        this.prefixSQL = prefixSQL;
    }
    
    public int getMaxRowNum() {
        return this.maxRowNum;
    }
    
    public void setMaxRowNum(final int maxRowNum) {
        this.maxRowNum = maxRowNum;
    }
    
    private void log(final String sss) {
        this.LOG.debug((Object)sss);
    }
    
    private void debug(final Object sss) {
        if (this.LOG.isDebugEnabled() && sss != null) {
            this.LOG.debug((Object)sss.toString());
        }
    }
    
    @Override
    public Object[] load() {
        final StringBuilder querySql = new StringBuilder("");
        if (this.prefixSQL == null || this.prefixSQL.trim().length() == 0) {
            querySql.append("select t.*,@rowno:=@rowno+1 AS rownum from");
        }
        else {
            querySql.append(this.prefixSQL);
        }
        querySql.append("(");
        querySql.append(this.selectSQL);
        querySql.append(") t, (SELECT @rowno:=0) s ");
        querySql.append(" where @rowno <= " + this.maxRowNum);
        final IServiceProvider sp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        if (sp == null) {
            AEEExceptionProcessor.process(new Exception("can not get ServiceProvider."));
            return new Object[0];
        }
        Connection conn = null;
        PreparedByNameStatement stmt = null;
        try {
            this.log("start fetch database connection of[" + this.database + "]");
            conn = (Connection)sp.getService(this.database);
            stmt = new PreparedByNameStatement(conn, querySql.toString());
            if (this.mapSqlParam != null && this.mapSqlParam.size() > 0) {
                stmt.setValueByMap((Map)this.mapSqlParam);
            }
            final ResultSet rs = stmt.executeQuery();
            final List<Map<String, String>> list = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
            this.log("fetch data [" + list.size() + "]");
            return list.toArray();
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(e);
            return new Object[0];
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException ex) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex2) {}
                conn = null;
            }
        }
    }
    
    @Override
    public void onPutIntoQueue(final Object o) {
        if (this.updateSQL == null || this.updateSQL.length() <= 0) {
            return;
        }
        final IServiceProvider sp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        if (sp == null) {
            AEEExceptionProcessor.process(new Exception("can not get ServiceProvider."));
            return;
        }
        Connection conn = null;
        PreparedByNameStatement stmt = null;
        try {
            conn = (Connection)sp.getService(this.database);
            stmt = new PreparedByNameStatement(conn, this.updateSQL);
            stmt.setValueByMap((Map)o);
            stmt.execute();
            conn.commit();
        }
        catch (Exception e) {
            AEEExceptionProcessor.process(new Exception("can not get ServiceProvider."));
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException ex) {}
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex2) {}
                conn = null;
            }
        }
    }
}
