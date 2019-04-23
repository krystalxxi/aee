// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import java.util.Iterator;
import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.util.StringLiker;
import com.ailk.aee.common.ftp.FtpUtil;
import java.io.File;
import java.util.Map;
import com.ailk.aee.common.sql.ResultSetTool;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.core.IJobSession;
import java.util.List;
import com.ailk.aee.core.Job;

public class FTPGetJobV3 extends Job
{
    private String ftpCode;
    private String ftpDB;
    private String ftpSQL;
    private List<String> filterList;
    private String dataFileSuffix;
    private boolean check;
    private String checkFileSuffix;
    private String sumFileSuffix;
    private String user;
    private String password;
    private String server;
    private int port;
    private String remotePath;
    private String remotePathHis;
    private String localPath;
    private int mode;
    private boolean isInit;
    
    public FTPGetJobV3() {
        this.ftpCode = "";
        this.ftpDB = "";
        this.ftpSQL = "select * from aee_ftp where state = '0' and ftp_code = ? ";
        this.filterList = null;
        this.dataFileSuffix = "";
        this.check = false;
        this.checkFileSuffix = "check";
        this.sumFileSuffix = "";
        this.user = "";
        this.password = "";
        this.server = "";
        this.port = 21;
        this.remotePath = "";
        this.remotePathHis = "";
        this.localPath = "";
        this.mode = 0;
        this.isInit = false;
    }
    
    @Override
    public void execute(final IJobSession jobsession) throws Exception {
        if (!this.isInit) {
            if (this.ftpDB == null || this.ftpDB.trim().length() == 0) {
                this.ftpDB = Configuration.getValue("AEE.ftp.database");
            }
            if (this.ftpCode != null && this.ftpCode.trim().length() > 0 && this.ftpDB != null && this.ftpDB.trim().length() > 0) {
                final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
                Connection conn = null;
                try {
                    conn = (Connection)isp.getService(this.ftpDB);
                    if (conn == null) {
                        throw new Exception("\ufffd\ufffd\ufffd" + this.ftpDB + "\ufffd\u07b7\ufffd\ufffd\ufffd\u0221\ufffd\ufffd\u077f\ufffd\ufffd\ufffd\ufffd\ufffd.");
                    }
                    PreparedByNameStatement stmt = new PreparedByNameStatement(conn, this.ftpSQL);
                    stmt.setString(1, this.ftpCode);
                    ResultSet rs = stmt.executeQuery();
                    final List<Map<String, String>> list = (List<Map<String, String>>)ResultSetTool.rs2ListMap(rs);
                    rs.close();
                    rs = null;
                    stmt.close();
                    stmt = null;
                    conn.close();
                    conn = null;
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
                        this.setRemotePath(temp.get("REMOTE_PATH"));
                        this.setRemotePathHis(temp.get("REMOTE_PATH_HIS"));
                        final String mode = temp.get("TRANS_MODE");
                        if (mode != null) {
                            this.setMode(Integer.parseInt(mode));
                        }
                    }
                }
                finally {
                    if (conn != null) {
                        conn.close();
                        conn = null;
                    }
                }
            }
            if (this.server == null || this.server.trim().length() == 0) {
                throw new Exception("server\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.user == null || this.user.trim().length() == 0) {
                throw new Exception("user\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.password == null || this.password.trim().length() == 0) {
                throw new Exception("password\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.remotePath == null || this.remotePath.trim().length() == 0) {
                throw new Exception("remotePath\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.localPath == null || this.localPath.trim().length() == 0) {
                throw new Exception("localPath\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            final File localPathFile = new File(this.localPath);
            if (!localPathFile.exists() || !localPathFile.isDirectory()) {
                throw new Exception("localPath\ufffd\ufffd\ufffd\u00f2\ufffd\ufffd\ufffd\u0237");
            }
            this.isInit = true;
        }
        FtpUtil u = null;
        try {
            u = new FtpUtil(this.localPath, (String)null, this.remotePath, this.remotePathHis, this.user, this.password, this.server, this.port);
            final String[] fileNames = u.list();
            if (fileNames != null) {
                for (final String downFileName : fileNames) {
                    Label_1126: {
                        if (this.dataFileSuffix == null || this.dataFileSuffix.length() <= 0 || downFileName.endsWith(this.dataFileSuffix)) {
                            String sumFileName = null;
                            String checkFileName = null;
                            if (this.check) {
                                if (downFileName.endsWith(this.checkFileSuffix)) {
                                    break Label_1126;
                                }
                                if (downFileName.endsWith(this.sumFileSuffix)) {
                                    break Label_1126;
                                }
                                int index = downFileName.lastIndexOf(".");
                                if (index <= 0) {
                                    index = downFileName.length();
                                }
                                checkFileName = downFileName.substring(0, index) + "." + this.checkFileSuffix;
                                if (!this.isExistFile(checkFileName, fileNames)) {
                                    break Label_1126;
                                }
                                if (this.sumFileSuffix != null && this.sumFileSuffix.length() > 0) {
                                    sumFileName = downFileName.substring(0, index) + "." + this.sumFileSuffix;
                                }
                            }
                            if (this.filterList != null && this.filterList.size() > 0) {
                                boolean isMatch = false;
                                for (final String filter : this.filterList) {
                                    if (StringLiker.PathLiker.isLike(downFileName, filter)) {
                                        isMatch = true;
                                        break;
                                    }
                                }
                                if (!isMatch) {
                                    break Label_1126;
                                }
                            }
                            u.download(downFileName, downFileName, this.mode);
                            if (this.check) {
                                u.download(checkFileName, checkFileName, this.mode);
                                if (sumFileName != null && this.isExistFile(sumFileName, fileNames)) {
                                    u.download(sumFileName, sumFileName, this.mode);
                                }
                            }
                            if (this.remotePathHis != null && this.remotePathHis.trim().length() > 0) {
                                u.moveFileToRemoteHisDir(downFileName);
                                if (this.check) {
                                    u.moveFileToRemoteHisDir(checkFileName);
                                    if (sumFileName != null && this.isExistFile(sumFileName, fileNames)) {
                                        u.moveFileToRemoteHisDir(sumFileName);
                                    }
                                }
                            }
                            else {
                                u.delete(downFileName);
                                if (this.check) {
                                    u.delete(checkFileName);
                                    if (sumFileName != null && this.isExistFile(sumFileName, fileNames)) {
                                        u.delete(sumFileName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        finally {
            if (u != null) {
                u.close();
            }
        }
    }
    
    public boolean isExistFile(final String checkfile, final String[] files) {
        for (final String temp : files) {
            if (temp.equals(checkfile)) {
                return true;
            }
        }
        return false;
    }
    
    public String getFtpCode() {
        return this.ftpCode;
    }
    
    public void setFtpCode(final String ftpCode) {
        this.ftpCode = ftpCode;
    }
    
    public String getDatabase() {
        return this.ftpDB;
    }
    
    public void setDatabase(final String database) {
        this.ftpDB = database;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getServer() {
        return this.server;
    }
    
    public void setServer(final String server) {
        this.server = server;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String getRemotePath() {
        return this.remotePath;
    }
    
    public void setRemotePath(final String remotePath) {
        this.remotePath = remotePath;
    }
    
    public String getRemotePathHis() {
        return this.remotePathHis;
    }
    
    public void setRemotePathHis(final String remotePathHis) {
        this.remotePathHis = remotePathHis;
    }
    
    public String getLocalPath() {
        return this.localPath;
    }
    
    public void setLocalPath(final String localPath) {
        this.localPath = localPath;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
}
