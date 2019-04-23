// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sql.PrepareByNameProcedure;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import java.util.Map;
import com.ailk.aee.common.stringobject.StringMapConverter;
import java.util.HashMap;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.Job;

public class ProcedureJob extends Job
{
    protected String database;
    protected String proc;
    protected String properties;
    
    public ProcedureJob() {
        this.database = "";
        this.proc = "";
        this.properties = "";
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        if (this.database == null || this.database.length() == 0) {
            throw new Exception("database\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        if (this.proc == null || this.proc.length() == 0) {
            throw new Exception("proc\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        final Map<String, String> hn = new HashMap<String, String>();
        if (this.properties != null && this.properties.length() > 0) {
            final Map<String, String> htemp = (Map<String, String>)new StringMapConverter().wrapFromString(this.properties);
            hn.putAll(htemp);
        }
        Connection conn = null;
        PrepareByNameProcedure pbp = null;
        try {
            final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
            conn = (Connection)isp.getService(this.database);
            if (conn == null) {
                throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            pbp = new PrepareByNameProcedure(conn, this.proc);
            pbp.autoMap((Map)hn);
            System.out.println(pbp);
            final Map<String, String> res = (Map<String, String>)pbp.execute();
            if (res.containsKey("X_RESULTCODE") && !"0".equals(res.get("X_RESULTCODE").toString())) {
                throw new Exception("the resultinfo is:" + res.get("X_RESULTINFO"));
            }
        }
        finally {
            if (pbp != null) {
                pbp.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
