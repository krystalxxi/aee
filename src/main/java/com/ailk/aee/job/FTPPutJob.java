// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import com.ailk.aee.common.ftp.FtpUtil;
import java.io.File;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: FTPPutJob.java 10855 2013-05-24 09:57:46Z xiezl $")
public class FTPPutJob extends Job
{
    private String user;
    private String password;
    private String server;
    private int port;
    private String remotePath;
    private String remotePathTemp;
    private String localPath;
    private String localPathHis;
    private int mode;
    private boolean isInit;
    
    public FTPPutJob() {
        this.user = "";
        this.password = "";
        this.server = "";
        this.port = 21;
        this.remotePath = "";
        this.remotePathTemp = "";
        this.localPath = "";
        this.localPathHis = "";
        this.mode = 1;
        this.isInit = false;
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
    
    public String getRemotePathTemp() {
        return this.remotePathTemp;
    }
    
    public void setRemotePathTemp(final String remotePathTemp) {
        this.remotePathTemp = remotePathTemp;
    }
    
    public String getLocalPath() {
        return this.localPath;
    }
    
    public void setLocalPath(final String localPath) {
        this.localPath = localPath;
    }
    
    public String getLocalPathHis() {
        return this.localPathHis;
    }
    
    public void setLocalPathHis(final String localPathHis) {
        this.localPathHis = localPathHis;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setMode(final int model) {
        this.mode = model;
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        File localFile = null;
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
            if (this.remotePathTemp == null || this.remotePathTemp.trim().length() == 0) {
                throw new Exception("remotePathTemp\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            if (this.localPath == null || this.localPath.trim().length() == 0) {
                throw new Exception("localPath\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
            }
            localFile = new File(this.localPath);
            if (!localFile.exists()) {
                throw new Exception("localPath\ufffd\ufffd\ufffd\u00f2\ufffd\ufffd\ufffd\u0237");
            }
            this.isInit = true;
        }
        final File localHisFile = new File(this.localPathHis);
        FtpUtil u = null;
        try {
            u = new FtpUtil(this.localPath, this.localPathHis, this.remotePathTemp, this.remotePath, this.user, this.password, this.server, this.port);
            if (localFile != null && localFile.isFile()) {
                u.upload(localFile.getName(), localFile.getName(), this.mode);
                u.moveFileToRemoteHisDir(localFile.getName());
                if (localHisFile.exists() && localHisFile.isDirectory()) {
                    FtpUtil.copyFile(localFile, new File(localHisFile, localFile.getName()));
                }
                localFile.delete();
            }
            else if (localFile != null && localFile.isDirectory()) {
                final File[] arr$;
                final File[] tempFiles = arr$ = localFile.listFiles();
                for (final File temp : arr$) {
                    if (temp.isFile()) {
                        u.upload(temp.getName(), temp.getName(), this.mode);
                        u.moveFileToRemoteHisDir(temp.getName());
                        if (localHisFile.exists() && localHisFile.isDirectory()) {
                            FtpUtil.copyFile(temp, new File(localHisFile, temp.getName()));
                        }
                        temp.delete();
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
}
