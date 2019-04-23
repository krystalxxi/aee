package com.ailk.aee.app.mwci;


import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.sql.PreparedByNameStatement;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MWCConf {
    private Map<String, MWCConfItem> allConfs = new HashMap();
    private static MWCConf instance = new MWCConf();

    private MWCConf() {
    }

    public String getServiceURIByWorkId(String workId) {
        MWCConfItem item = (MWCConfItem)this.allConfs.get(workId);
        return item == null?"":item.getServiceURI();
    }

    public String getServiceCallerByWorkId(String workId) {
        MWCConfItem item = (MWCConfItem)this.allConfs.get(workId);
        return item == null?null:item.getServiceCallClass();
    }

    public String getServiceParamByWorkId(String workId) {
        MWCConfItem item = (MWCConfItem)this.allConfs.get(workId);
        return item == null?"":item.getParam();
    }

    public Map<String, MWCConfItem> getConf() {
        return this.allConfs;
    }

    public void loadConf(String database, String groupId) {
        System.out.println("---------------------------- groupid:" + groupId);
        Connection conn = null;
        PreparedByNameStatement stmt = null;

        try {
            IServiceProvider sp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
            if(sp != null) {
                conn = (Connection)sp.getService(database);
                String sql = "select mwc_group_id,  mwc_work_id,   IFNULL(g_select_sql, '') g_select_sql,\t   IFNULL(g_update_sql, '') g_update_sql,\t   IFNULL(g_database, ''), g_database,\t   IFNULL(g_sleep_strategy, '1.0') g_sleep_strategy,\t   IFNULL(g_queue_length, '') g_queue_length,\t   IFNULL(g_weight, '') g_weight,\t   IFNULL(s_service_uri, '') s_service_uri,\t   IFNULL(s_service_call_class, '') s_service_call_class,\t   IFNULL(s_param, '') s_param  from td_aee_mwc_conf where mwc_group_id=:GROUP_ID and state='0' and NOW() between eff_date and exp_date";
                stmt = new PreparedByNameStatement(conn, sql);
                stmt.setString("GROUP_ID", groupId);
                ResultSet rs = stmt.executeQuery();
                int i = 0;

                while(rs.next()) {
                    PrintStream var10000 = System.out;
                    StringBuilder var10001 = (new StringBuilder()).append("---------------------------- ++i:");
                    ++i;
                    var10000.println(var10001.append(i).toString());
                    MWCConfItem n = new MWCConfItem();
                    n.setGroupId(rs.getString("MWC_GROUP_ID"));
                    n.setWorkId(rs.getString("MWC_WORK_ID"));
                    n.setSelectSQL(rs.getString("G_SELECT_SQL"));
                    n.setUpdateSQL(rs.getString("G_UPDATE_SQL"));
                    n.setDatabase(rs.getString("G_DATABASE"));
                    n.setSleepStrategy(rs.getString("G_SLEEP_STRATEGY"));
                    n.setQueueLength(rs.getInt("G_QUEUE_LENGTH"));
                    n.setQueueWeight(rs.getInt("G_WEIGHT"));
                    n.setServiceURI(rs.getString("S_SERVICE_URI"));
                    n.setServiceCallClass(rs.getString("S_SERVICE_CALL_CLASS"));
                    n.setParam(rs.getString("S_PARAM"));
                    this.allConfs.put(n.getWorkId(), n);
                }

                return;
            }
        } catch (Exception var24) {
            var24.printStackTrace();
            return;
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException var23) {
                    var23.printStackTrace();
                }
            }

            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException var22) {
                    var22.printStackTrace();
                }
            }

        }

    }

    public static MWCConf getInstance() {
        return instance;
    }
}
