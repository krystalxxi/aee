// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import java.util.List;
import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import java.util.Map;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FTPGetJobV2.java 10855 2013-05-24 09:57:46Z xiezl $")
public class FTPGetJobV2 extends FTPGetJob
{
    private String ftpCode;
    private String database;
    private boolean isInit;
    private String SQL;
    
    public FTPGetJobV2() {
        this.ftpCode = "";
        this.database = "";
        this.isInit = false;
        this.SQL = "select * from td_s_aee_ftp where state = '0' and ftp_code = ? ";
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        if (!this.isInit) {
            if (this.ftpCode == null || this.ftpCode.trim().length() == 0) {
                throw new Exception("ftpCode\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd.");
            }
            if (this.database == null || this.database.trim().length() == 0) {
                this.database = Configuration.getValue("AEE.ftp.database");
            }
            if (this.database == null || this.database.trim().length() == 0) {
                throw new Exception("\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\u04f1\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd.");
            }
            final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
            final Connection conn = (Connection)isp.getService(this.database);
            if (conn == null) {
                throw new Exception("\ufffd\ufffd\ufffd" + this.database + "\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
            }
            final PreparedByNameStatement stmt = new PreparedByNameStatement(conn, this.SQL);
            stmt.setString(1, this.ftpCode);
            final ResultSet rs = stmt.executeQuery();
            final List<Map<String, String>> list = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
            if (list != null && list.size() > 0) {
                final Map<String, String> temp = list.get(0);
                this.setServer(temp.get("SERVER"));
                String port = temp.get("PORT");
                if (port == null) {
                    port = "21";
                }
                this.setPort(Integer.parseInt(port));
                this.setUser(temp.get("USERNAME"));
                this.setPassword(temp.get("PASSWORD"));
                this.setLocalPath(temp.get("LOCAL_PATH"));
                this.setLocalPathTemp(temp.get("LOCAL_PATH_TEMP"));
                this.setRemotePath(temp.get("REMOTE_PATH"));
                this.setRemotePathHis(temp.get("REMOTE_PATH_HIS"));
                this.setFilterName(temp.get("REMARKS"));
                final String mode = temp.get("TRANS_MODE");
                if (mode != null) {
                    this.setMode(Integer.parseInt(mode));
                }
                this.isInit = true;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception ex) {}
        }
        super.execute(ctx);
    }
}
