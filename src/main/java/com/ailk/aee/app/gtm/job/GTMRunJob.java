// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.app.gtm.GTMPool;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.util.StringTokenizer;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.IJobSession;
import java.util.Map;
import com.ailk.aee.core.Job;

public class GTMRunJob extends Job
{
    private String database;
    private String updateLoadStateSQL;
    private int maxJob;
    
    public GTMRunJob() {
        this.database = "cen1";
        this.updateLoadStateSQL = "";
        this.maxJob = 100;
    }
    
    public void initializeJob(final Map<String, String> m) {
        this.updateLoadStateSQL = "update TL_GTM_WORKINST t set work_status = '2',start_date=sysdate where t.work_inst_id =:WORK_INST_ID";
    }
    
    private void load(final IJobSession session, final Map<String, String> m) throws Exception {
        this.doDatabaseUpdate(session, this.updateLoadStateSQL, m);
    }
    
    protected void doDatabaseUpdate(final IJobSession sess, final String sql, final Map<String, String> arg) throws Exception {
        if (sql == null || sql.trim().length() == 0) {
            return;
        }
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        Connection conn = (Connection)isp.getService(this.database);
        PreparedByNameStatement stmt = null;
        try {
            if (conn == null) {
                return;
            }
            final StringTokenizer st = new StringTokenizer(sql, ";", false);
            while (st.hasMoreElements()) {
                final String tempSql = st.nextToken();
                if (tempSql != null && tempSql.trim().length() > 0) {
                    stmt = new PreparedByNameStatement(conn, tempSql);
                    stmt.setValueByMap((Map)arg);
                    stmt.execute();
                    stmt.close();
                    stmt = null;
                }
            }
            conn.commit();
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            stmt = null;
            if (conn != null) {
                conn.close();
            }
            conn = null;
        }
    }
    
    public void execute(final IJobSession session) throws Exception {
        final Map<String, String> m = (Map<String, String>)session.getPackagedObject();
        if (m != null) {
            final String workInstId = m.get("WORK_INST_ID");
            final String workId = m.get("WORK_ID");
            GTMPool.getInstance().setMaxThreadCount(this.maxJob);
            if (!GTMPool.getInstance().isFull()) {
                this.load(session, m);
                GTMPool.getInstance().startWork(workInstId, workId, this.database);
            }
            else {
                AEEPlatform.getInstance().getLogger().info((Object)("GTMPool is Full,ignore execute " + workInstId));
            }
        }
    }
}
