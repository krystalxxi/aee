// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import java.sql.SQLException;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.etl.job.ITransformer;
import java.util.Map;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.common.stringobject.StringMapConverter;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.sp.IServiceProvider;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DBLoader.java 11985 2013-07-30 10:06:16Z xiezl $")
public class DBLoader extends AbstractLoader
{
    private Logger log;
    protected String insertSQL;
    protected String database;
    protected String transformsString;
    protected IServiceProvider isp;
    
    public DBLoader() {
        this.log = Logger.getLogger((Class)DBLoader.class);
        this.insertSQL = "";
        this.database = "";
        this.transformsString = null;
        this.isp = null;
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.database == null || this.database.equals("")) {
            throw new Exception("database \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.insertSQL == null || this.insertSQL.equals("")) {
            throw new Exception("insertSQL \ufffd\ufffd\ufffd\ubc7b\ufffd\ufffd\ufffd\ufffd");
        }
        this.isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        final Connection conn = (Connection)this.isp.getService(this.database);
        if (conn == null) {
            throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u0221" + this.database + "\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
        }
        conn.close();
        final StringMapConverter mapConverter = new StringMapConverter();
        if (this.transformsString != null && mapConverter.canWrapFromString(this.transformsString)) {
            final Map<String, String> m = (Map<String, String>)mapConverter.wrapFromString(this.transformsString);
            final String[] arr$;
            final String[] keys = arr$ = MapTools.getSubKeys((Map)m);
            for (final String key : arr$) {
                final Map<String, String> subMap = (Map<String, String>)MapTools.getSub((Map)m, key);
                final ITransformer tt = (ITransformer)ObjectBuilder.build((Class)ITransformer.class, (String)m.get(key), (Map)subMap);
                this.transformers.put(key, tt);
            }
        }
    }
    
    @Override
    public boolean loadObject(final IBusinessObject o) {
        boolean flag = false;
        if (o instanceof MapRecord) {
            final Map<String, String> m = (MapRecord)o;
            Connection conn = null;
            PreparedByNameStatement psstmt = null;
            try {
                conn = (Connection)this.isp.getService(this.database);
                psstmt = new PreparedByNameStatement(conn, this.insertSQL);
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
}
