// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.util.Iterator;
import java.util.Set;
import java.sql.SQLException;
import com.ailk.aee.common.util.ExceptionUtils;
import java.util.Map;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import com.ailk.aee.common.sp.IServiceProvider;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: XSLLoader.java 11039 2013-06-13 01:44:38Z xiezl $")
public class XSLLoader extends AbstractLoader
{
    private Logger log;
    private String database;
    IServiceProvider isp;
    
    public XSLLoader() {
        this.log = Logger.getLogger((Class)this.getClass());
        this.database = "";
        this.isp = null;
    }
    
    @Override
    public boolean loadObject(final IBusinessObject o) {
        boolean flag = false;
        if (o instanceof MapRecord) {
            final Map<String, String> m = (MapRecord)o;
            final String tableName = m.get("TABLE_NAME__");
            m.remove("TABLE_NAME__");
            final StringBuilder insertSQL = new StringBuilder("INSERT INTO ");
            insertSQL.append(tableName + " (");
            final Set<String> key = m.keySet();
            final StringBuilder columnsSB = new StringBuilder();
            final StringBuilder valuesSB = new StringBuilder();
            for (final String s : key) {
                columnsSB.append(s + ",");
                valuesSB.append("'" + m.get(s) + "',");
            }
            columnsSB.delete(columnsSB.length() - 1, columnsSB.length());
            valuesSB.delete(valuesSB.length() - 1, valuesSB.length());
            insertSQL.append((CharSequence)columnsSB);
            insertSQL.append(") ");
            insertSQL.append("VALUES(");
            insertSQL.append((CharSequence)valuesSB);
            insertSQL.append(" ) ");
            Connection conn = null;
            PreparedByNameStatement psstmt = null;
            try {
                conn = (Connection)this.isp.getService(this.database);
                psstmt = new PreparedByNameStatement(conn, insertSQL.toString());
                psstmt.clearParameters();
                psstmt.setValueByMap((Map)m);
                psstmt.execute();
                conn.commit();
                flag = true;
            }
            catch (Exception e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack(e));
                flag = false;
                if (psstmt != null) {
                    try {
                        psstmt.close();
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
                if (psstmt != null) {
                    try {
                        psstmt.close();
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
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.database == null || this.database.equals("")) {
            throw new Exception("database \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
    }
}
