// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import com.ailk.aee.common.util.StringLiker;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.ftp.FtpUtil;
import com.ailk.aee.core.IJobSession;
import java.io.File;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: FTPGetJob.java 10855 2013-05-24 09:57:46Z xiezl $")
public class FTPGetJob extends Job
{
    protected String filterName;
    private String user;
    private String password;
    private String server;
    private int port;
    private String remotePath;
    private String remotePathHis;
    private String localPath;
    private String localPathTemp;
    private int mode;
    private boolean isInit;
    File localPathFile;
    File localPathTempFile;
    
    public FTPGetJob() {
        this.filterName = "";
        this.user = "";
        this.password = "";
        this.server = "";
        this.port = 21;
        this.remotePath = "";
        this.remotePathHis = "";
        this.localPath = "";
        this.localPathTemp = "";
        this.mode = 0;
        this.isInit = false;
        this.localPathFile = null;
        this.localPathTempFile = null;
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        if (!this.isInit) {
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
            this.localPathFile = new File(this.localPath);
            if (!this.localPathFile.exists() || !this.localPathFile.isDirectory()) {
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
                    Label_0417: {
                        if (this.filterName != null) {
                            if (this.filterName.length() != 0) {
                                final String[] filters = StringUtils.split(this.filterName, ",");
                                boolean isLike = false;
                                for (final String f : filters) {
                                    if (StringLiker.PathLiker.isLike(downFileName, f)) {
                                        isLike = true;
                                    }
                                }
                                if (!isLike) {
                                    break Label_0417;
                                }
                            }
                        }
                        u.download(downFileName, downFileName, this.mode);
                        if (this.remotePathHis != null && this.remotePathHis.trim().length() > 0) {
                            u.moveFileToRemoteHisDir(downFileName);
                        }
                        else {
                            u.delete(downFileName);
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
    
    public String getLocalPathTemp() {
        return this.localPathTemp;
    }
    
    public void setLocalPathTemp(final String localPathTemp) {
        this.localPathTemp = localPathTemp;
    }
    
    public static void main(final String[] args) {
        final String downFileName = "aaa";
        final String filterName = "#";
        System.out.println(downFileName.indexOf(filterName) >= 0);
    }
    
    public String getFilterName() {
        return this.filterName;
    }
    
    public void setFilterName(final String filterName) {
        this.filterName = filterName;
    }
}
