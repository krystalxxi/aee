// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.generator;

import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import java.sql.SQLException;
import java.util.Map;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.platform.AEEPlatform;
import java.sql.Connection;
import com.ailk.aee.core.EventGenerator;

public class GTMModEventGenerator extends EventGenerator
{
    private String database;
    private String modvalue;
    private String modmax;
    private String selectSQL;
    private String updateSQL;
    private Connection conn;
    
    public GTMModEventGenerator() {
        this.database = null;
        this.modvalue = null;
        this.modmax = null;
        this.selectSQL = "SELECT T.* FROM TL_GTM_WORKINST T WHERE T.WORK_STATUS = '0' AND T.FIRE_EXEC_TIME < NOW() ";
        this.updateSQL = "UPDATE TL_GTM_WORKINST T SET WORK_STATUS = '1' WHERE T.WORK_INST_ID = :WORK_INST_ID";
        this.conn = null;
    }
    
    public Object[] generate() {
        Object[] returnval = null;
        String sSQL = null;
        if (this.modvalue != null && this.modvalue.trim().length() > 0 && this.modmax != null && this.modmax.trim().length() > 0) {
            final int imodmax = Integer.parseInt(this.modmax);
            final int imodval = Integer.parseInt(this.modvalue);
            if (imodmax > 0 && imodval > 0 && imodval < imodmax && imodval > -1) {
                sSQL = "SELECT s1.*,@rowno:=@rowno+1 AS ROWNUM FROM (" + this.selectSQL + " AND UPPER(T.WORK_ARGU) NOT LIKE '{SUBSYS=%' AND MOD(T.WORK_INST_ID," + this.modmax + ") =" + this.modvalue + " ORDER BY T.FIRE_EXEC_TIME) s1,(SELECT @rowno:=0) s2  WHERE @rowno < 30";
            }
            else if (imodval == -1) {
                sSQL = "SELECT s1.*,@rowno:=@rowno+1 AS ROWNUM FROM (" + this.selectSQL + " AND UPPER(T.WORK_ARGU) NOT LIKE '{SUBSYS=%' ORDER BY T.FIRE_EXEC_TIME) s1,(SELECT @rowno:=0) s2 WHERE @rowno < 30";
            }
        }
        else {
            sSQL = this.selectSQL;
        }
        AEEPlatform.getInstance().getLogger().debug((Object)sSQL);
        final IServiceProvider sp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        this.conn = (Connection)sp.getService(this.database);
        if (null == this.conn) {
            this.error("database : " + this.database + " can not get connection");
            return null;
        }
        PreparedByNameStatement stmt = null;
        try {
            stmt = new PreparedByNameStatement(this.conn, sSQL);
            final ResultSet rs = stmt.executeQuery();
            final List<Map<String, String>> list = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
            if (null != list && list.size() > 0) {
                for (final Map<String, String> m : list) {
                    try {
                        this.updateStatus(m);
                    }
                    catch (Exception e) {
                        this.error("---- updateStatus error: " + e.getMessage());
                    }
                }
                this.conn.commit();
                returnval = list.toArray(new Map[0]);
            }
        }
        catch (Exception e2) {
            this.error("---- generator error: " + e2.getMessage());
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (this.conn != null) {
                    this.conn.close();
                    this.conn = null;
                }
            }
            catch (SQLException e3) {
                this.error("----- release conn : " + e3.getMessage());
            }
        }
        finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (this.conn != null) {
                    this.conn.close();
                    this.conn = null;
                }
            }
            catch (SQLException e4) {
                this.error("----- release conn : " + e4.getMessage());
            }
        }
        return returnval;
    }
    
    public void updateStatus(final Map<String, String> m) throws Exception {
        if (this.updateSQL != null && this.updateSQL.length() > 0) {
            final PreparedByNameStatement stmt = new PreparedByNameStatement(this.conn, this.updateSQL);
            try {
                stmt.setValueByMap((Map)m);
                stmt.execute();
            }
            catch (Exception e) {
                this.error("----- updateStatus : " + e.getMessage());
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }
    
    public String getDatabase() {
        return this.database;
    }
    
    public void setDatabase(final String database) {
        this.database = database;
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
    
    public void log(final String message) {
        try {
            AEEPlatform.getInstance().getLogger().info((Object)message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void error(final String message) {
        try {
            AEEPlatform.getInstance().getLogger().error((Object)message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getModvalue() {
        return this.modvalue;
    }
    
    public void setModvalue(final String modvalue) {
        this.modvalue = modvalue;
    }
    
    public String getModmax() {
        return this.modmax;
    }
    
    public void setModmax(final String modmax) {
        this.modmax = modmax;
    }
}
