// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.app.indb;

import java.util.List;
import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.util.HashMap;
import com.ailk.aee.common.conf.Configuration;
import java.util.Map;
import org.apache.log4j.Logger;

public class InDBV2 extends InDB
{
    private String database;
    private String indbCode;
    private Logger log;
    private String SQL;
    
    public InDBV2() {
        this.database = null;
        this.indbCode = null;
        this.log = Logger.getLogger((Class)InDBV2.class);
        this.SQL = "select * from aee_indb where state = '0' and indb_code = ? ";
    }
    
    @Override
    public void initializeJob(final Map<String, String> m) {
        if (this.database == null || this.database.trim().length() == 0) {
            this.database = Configuration.getValue("AEE.indb.database");
        }
        final Map<String, String> config = new HashMap<String, String>();
        if (this.database != null && this.database.trim().length() > 0 && this.indbCode != null && this.indbCode.trim().length() > 0) {
            final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
            try {
                final Connection conn = (Connection)isp.getService(this.database);
                if (conn == null) {
                    throw new Exception("\ufffd\ufffd\ufffd" + this.database + "\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
                }
                final PreparedByNameStatement stmt = new PreparedByNameStatement(conn, this.SQL);
                stmt.setString(1, this.indbCode.trim());
                final ResultSet rs = stmt.executeQuery();
                final List<Map<String, String>> list = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
                if (list != null && list.size() > 0) {
                    final Map<String, String> temp = list.get(0);
                    if (temp.get("FIELD_SEPARATOR") != null) {
                        config.put("fieldSep", temp.get("FIELD_SEPARATOR"));
                    }
                    if (temp.get("HAS_HEAD") != null) {
                        final String hasHead = temp.get("HAS_HEAD");
                        if ("true".equalsIgnoreCase(hasHead) || "t".equalsIgnoreCase(hasHead) || "y".equalsIgnoreCase(hasHead) || "yes".equalsIgnoreCase(hasHead)) {
                            config.put("isHasHead", "true");
                        }
                    }
                    if (temp.get("HAS_TAIL") != null) {
                        final String hasTail = temp.get("HAS_TAIL");
                        if ("true".equalsIgnoreCase(hasTail) || "t".equalsIgnoreCase(hasTail) || "y".equalsIgnoreCase(hasTail) || "yes".equalsIgnoreCase(hasTail)) {
                            config.put("isHasTail", "true");
                        }
                    }
                    if (temp.get("INGORE_PREFIX") != null) {
                        config.put("ingorePrefix", temp.get("INGORE_PREFIX"));
                    }
                    if (temp.get("IMPORT_FILE") != null) {
                        config.put("extractorFileName", temp.get("IMPORT_FILE"));
                    }
                    if (temp.get("FIELD_NAMES") != null) {
                        config.put("fieldMap", temp.get("FIELD_NAMES"));
                    }
                    if (temp.get("TRANSFORM_VALUES") != null) {
                        config.put("fieldMap2", temp.get("TRANSFORM_VALUES"));
                    }
                    if (temp.get("INSERT_SQL") != null) {
                        config.put("insertSQL", temp.get("INSERT_SQL"));
                    }
                    if (temp.get("INSERT_DB") != null) {
                        config.put("toDatabase", temp.get("INSERT_DB"));
                    }
                    if (temp.get("TRANSFORMS_STR") != null) {
                        config.put("transformsString", temp.get("TRANSFORMS_STR"));
                    }
                }
            }
            catch (Exception e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack(e));
            }
        }
        super.initializeJob(config);
    }
}
