// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.util.ExceptionUtils;
import java.util.Map;
import com.ailk.aee.common.conf.util.XMLInputStreamParser;
import java.util.HashMap;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;

public class DBConfigurationFactory extends StaticMapConfigurationFactory
{
    private String connName;
    private String nodeId;
    private String workerName;
    
    public DBConfigurationFactory() {
        this.connName = "cen1";
        this.nodeId = "";
        this.workerName = "";
    }
    
    public String getConnName() {
        return this.connName;
    }
    
    public void setConnName(final String connName) {
        this.connName = connName;
    }
    
    public String getNodeId() {
        return this.nodeId;
    }
    
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }
    
    public String getWorkerName() {
        return this.workerName;
    }
    
    public void setWorkerName(final String workerName) {
        this.workerName = workerName;
    }
    
    @Override
    public String getFactoryName() {
        final String s = "DBConfigurationFactory of " + this.connName + "";
        return s;
    }
    
    @Override
    public void initConfMap() {
        final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        final String propSQL = "select prop_name,prop_value from td_s_aee_properties where lower(use_tag) = 'u'";
        final String workSQL = "select node_id,worker_name,worker_value,template_name from td_s_aee_worker where lower(use_tag) = 'u' and node_id = ? and worker_name = ?";
        final String templateSQL = "select template_name,template_value from td_s_aee_template where template_name =?";
        Connection conn = null;
        try {
            conn = (Connection)isp.getService(this.connName);
            if (conn == null) {
                throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u0221" + this.connName + "\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            PreparedByNameStatement stmt = new PreparedByNameStatement(conn, propSQL);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.conf.put(rs.getString(1), rs.getString(2));
            }
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
            stmt = new PreparedByNameStatement(conn, workSQL);
            stmt.setString(1, this.nodeId);
            stmt.setString(2, this.workerName);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new Exception("\ufffd\u07b7\ufffd\ufffd\u04b5\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            final String workerValue = rs.getString("WORKER_VALUE");
            final String templateName = rs.getString("TEMPLATE_NAME");
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
            Map<String, String> target = new HashMap<String, String>();
            if (templateName != null) {
                stmt = new PreparedByNameStatement(conn, templateSQL);
                stmt.setString(1, templateName);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    target = XMLInputStreamParser.parseString(rs.getString("TEMPLATE_VALUE"));
                    target = MapTools.addPrefix(target, this.workerName);
                }
                rs.close();
                rs = null;
                stmt.close();
                stmt = null;
            }
            final Map<String, String> ms = XMLInputStreamParser.parseString(workerValue);
            target.putAll(ms);
            this.conf.putAll(MapTools.addPrefix(target, "AEE.works." + this.workerName));
        }
        catch (Exception e) {
            System.out.println(ExceptionUtils.getExceptionStack(e));
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final Map<String, String> ms = XMLInputStreamParser.parseString("<worker value=\"com.ailk.aee.worker.EventDrivenWorker\"><sleepStrategy value=\"com.ailk.aee.strategy.StaticWorkCounterStrategy\"><v>5</v></sleepStrategy><eventGenerator value=\"com.ailk.aee.app.gtm.generator.GTMEventGenerator\"><database>cen1</database></eventGenerator></worker>");
        System.out.println(MapTools.mapToString(ms));
    }
}
