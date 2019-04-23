// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.ftp;

import com.ailk.aee.common.conf.Configuration;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import com.ailk.aee.common.util.ExceptionUtils;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import java.util.List;
import java.util.ArrayList;
import com.ailk.aee.common.util.StringUtils;
import java.io.File;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FtpUtil.java 10807 2013-05-22 07:57:15Z xiezl $")
public class FtpUtil
{
    private static Logger log;
    public static final int BIN = 0;
    public static final int ASC = 1;
    private FTPClient client;
    private String localPath;
    private String localPathHis;
    private String remotePath;
    private String remotePathHis;
    private static int TIMEOUT_SECONDS;
    private static int CONCURRENT_CAPACITY;
    private static int CONCURRENT_ACQUIRE_TIMEOUT_SECONDS;
    private static ConcurrentCapacity SEM;
    
    public FtpUtil(final String localPath, final String localPathHis, final String remotePath, final String remotePathHis, final String user, final String password, final String server, final int port) throws Exception {
        this.client = null;
        this.localPath = null;
        this.localPathHis = null;
        this.remotePath = null;
        this.remotePathHis = null;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>WQ1");
        if (localPath != null && localPath.length() > 0) {
            this.localPath = (localPath.endsWith(File.separator) ? localPath.substring(0, localPath.length() - 1) : localPath);
        }
        if (localPathHis != null && localPathHis.length() > 0) {
            this.localPathHis = (localPathHis.endsWith(File.separator) ? localPathHis.substring(0, localPathHis.length() - 1) : localPathHis);
        }
        if (remotePath != null && remotePath.length() > 0) {
            this.remotePath = (remotePath.endsWith("/") ? remotePath.substring(0, remotePath.length() - 1) : remotePath);
        }
        if (remotePathHis != null && remotePathHis.length() > 0) {
            this.remotePathHis = (remotePathHis.endsWith("/") ? remotePathHis.substring(0, remotePathHis.length() - 1) : remotePathHis);
        }
        (this.client = new FTPClient()).setDefaultTimeout(FtpUtil.TIMEOUT_SECONDS * 1000);
        boolean remoteVerificationEnabled = true;
        boolean localPasv = false;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>WQ2");
        String ip = server;
        if (ip.startsWith("{PASV}")) {
            remoteVerificationEnabled = false;
            ip = StringUtils.substringAfter(ip, "{PASV}");
        }
        else if (ip.startsWith("{PORT}")) {
            remoteVerificationEnabled = true;
            ip = StringUtils.substringAfter(ip, "{PORT}");
        }
        else if (ip.startsWith("{LOCAL_PASV}")) {
            localPasv = true;
            ip = StringUtils.substringAfter(ip, "{LOCAL_PASV}");
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>WQ5:ip:" + ip + ",port:" + port + ",remoteVerificationEnabled:" + remoteVerificationEnabled + ",user:" + user + ",password:" + password);
        this.client.connect(ip, port);
        this.client.setRemoteVerificationEnabled(remoteVerificationEnabled);
        this.client.login(user, password);
        if (localPasv) {
            this.client.enterLocalPassiveMode();
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>WQ6:login ok");
        this.client.changeWorkingDirectory(this.wrapper(remotePath));
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
    
    public void setEncoding(final String encoding) {
        this.client.setControlEncoding(encoding);
    }
    
    public void bin() throws Exception {
        this.client.setFileType(2);
    }
    
    public void asc() throws Exception {
        this.client.setFileType(0);
    }
    
    public String[] list(final String pathName) throws Exception {
        boolean isAcquire = false;
        final List<String> list = new ArrayList<String>();
        try {
            isAcquire = FtpUtil.SEM.acquire();
            final FTPFile[] ftpFiles = this.client.listFiles(pathName);
            for (int i = 0; i < ftpFiles.length; ++i) {
                if (ftpFiles[i].isFile()) {
                    list.add(ftpFiles[i].getName());
                }
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return list.toArray(new String[0]);
    }
    
    public String[] listNames() throws Exception {
        boolean isAcquire = false;
        String[] fileNames = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            fileNames = this.client.listNames();
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return fileNames;
    }
    
    public String[] listNames(final String pathName) throws Exception {
        boolean isAcquire = false;
        String[] fileNames = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            fileNames = this.client.listNames(pathName);
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return fileNames;
    }
    
    public String[] list() throws Exception {
        boolean isAcquire = false;
        final List<String> list = new ArrayList<String>();
        try {
            isAcquire = FtpUtil.SEM.acquire();
            this.client.configure(new FTPClientConfig("com.ailk.aee.common.ftp.MyFTPEntryParser"));
            final FTPFile[] ftpFiles = this.client.listFiles();
            System.out.println(">>>>>>>>>>>>>>>>>>>>WQ7:ftpFiles.length:" + ftpFiles.length);
            for (int i = 0; i < ftpFiles.length; ++i) {
                if (ftpFiles[i].isFile()) {
                    list.add(ftpFiles[i].getName());
                }
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return list.toArray(new String[0]);
    }
    
    public FTPFile getFtpFile(final String fileName) throws Exception {
        boolean isAcquire = false;
        FTPFile rtn = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            final FTPFile[] ftpFiles = this.client.listFiles(fileName);
            for (int i = 0; i < ftpFiles.length; ++i) {
                if (ftpFiles[i].isFile() && ftpFiles[i].getName().equals(fileName)) {
                    rtn = ftpFiles[i];
                    break;
                }
            }
            if (rtn == null) {
                throw new Exception("\ufffd\u07b7\ufffd\ufffd\ufffd\u013f?:" + this.client.printWorkingDirectory() + "\ufffd\ufffd\ufffd\u04b5\ufffd\ufffd\u013c\ufffd:" + fileName);
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return rtn;
    }
    
    public String[] listDir() throws Exception {
        boolean isAcquire = false;
        final List<String> list = new ArrayList<String>();
        try {
            isAcquire = FtpUtil.SEM.acquire();
            final FTPFile[] ftpFiles = this.client.listFiles();
            for (int i = 0; i < ftpFiles.length; ++i) {
                if (ftpFiles[i].isDirectory()) {
                    list.add(ftpFiles[i].getName());
                }
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return list.toArray(new String[0]);
    }
    
    public String getCurrentWorkingDirectory() throws Exception {
        return this.client.printWorkingDirectory();
    }
    
    public boolean mkdir(final String dir) throws Exception {
        boolean isAcquire = false;
        boolean flag = false;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            flag = this.client.makeDirectory(dir);
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return flag;
    }
    
    public void changeWorkingDirectory(final String dir) throws Exception {
        this.client.changeWorkingDirectory(this.wrapper(dir));
    }
    
    public void upload(final String remoteFileName, final InputStream input, final int mode) throws Exception {
        if (mode == 0) {
            this.client.setFileType(2);
        }
        else {
            if (mode != 1) {
                throw new Exception("\u0123\u02bd\u05b5\ufffd\ufffd\ufffd\ufffd");
            }
            this.client.setFileType(0);
        }
        this.upload(remoteFileName, input);
    }
    
    public void upload(final String remoteFileName, final InputStream input) throws Exception {
        boolean isAcquire = false;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            final boolean isSuccess = this.client.storeFile(this.wrapper(remoteFileName), input);
            if (!isSuccess) {
                String remoteAddress = null;
                int port = 0;
                try {
                    remoteAddress = this.client.getRemoteAddress().getHostAddress();
                    port = this.client.getRemotePort();
                }
                catch (Exception ex) {
                    FtpUtil.log.error((Object)ExceptionUtils.getExceptionStack(ex));
                }
                throw new Exception("\ufffd\u03f4\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd:" + remoteAddress + ":" + port);
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public void upload(final String remoteFileName, final String localFileName) throws Exception {
        boolean isAcquire = false;
        InputStream is = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            is = new BufferedInputStream(new FileInputStream(this.localPath + File.separator + localFileName));
            final boolean isSuccess = this.client.storeFile(this.wrapper(remoteFileName), is);
            if (!isSuccess) {
                String remoteAddress = null;
                int port = 0;
                try {
                    remoteAddress = this.client.getRemoteAddress().getHostAddress();
                    port = this.client.getRemotePort();
                }
                catch (Exception ex) {
                    FtpUtil.log.error((Object)ExceptionUtils.getExceptionStack(ex));
                }
                throw new Exception("\ufffd\u03f4\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffd:" + this.localPath + File.separator + localFileName + " \ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd:" + remoteAddress + ":" + port);
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public void upload(final String remoteFileName, final String localFileName, final int mode) throws Exception {
        if (mode == 0) {
            this.client.setFileType(2);
        }
        else {
            if (mode != 1) {
                throw new Exception("\u0123\u02bd\u05b5\ufffd\ufffd\ufffd\ufffd");
            }
            this.client.setFileType(0);
        }
        this.upload(remoteFileName, localFileName);
    }
    
    public void download(final String remoteFileName, final OutputStream output, final int mode) throws Exception {
        if (mode == 0) {
            this.client.setFileType(2);
        }
        else {
            if (mode != 1) {
                throw new Exception("\u0123\u02bd\u05b5\ufffd\ufffd\ufffd\ufffd");
            }
            this.client.setFileType(0);
        }
        this.download(remoteFileName, output);
    }
    
    public void download(final String remoteFileName, final OutputStream output) throws Exception {
        boolean isAcquire = false;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            final boolean rtn = this.client.retrieveFile(this.wrapper(remoteFileName), output);
            if (!rtn) {
                throw new Exception("\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\u013c\ufffd\u02a7\ufffd\ufffd:" + remoteFileName);
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public void download(final String remoteFileName, final String localFileName, final int mode) throws Exception {
        if (mode == 0) {
            this.client.setFileType(2);
        }
        else {
            if (mode != 1) {
                throw new Exception("\u0123\u02bd\u05b5\ufffd\ufffd\ufffd\ufffd");
            }
            this.client.setFileType(0);
        }
        this.download(remoteFileName, localFileName);
    }
    
    public void download(final String remoteFileName, final String localFileName) throws Exception {
        boolean isAcquire = false;
        OutputStream os = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            os = new BufferedOutputStream(new FileOutputStream(this.localPath + File.separator + localFileName));
            final boolean rtn = this.client.retrieveFile(this.wrapper(remoteFileName), os);
            if (!rtn) {
                throw new Exception("\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\u013c\ufffd\u02a7\ufffd\ufffd:" + remoteFileName);
            }
        }
        finally {
            if (os != null) {
                os.close();
            }
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public InputStream readRemote(final String remoteFileName) throws Exception {
        boolean isAcquire = false;
        InputStream localInputStream = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            localInputStream = this.client.retrieveFileStream(this.wrapper(remoteFileName));
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return localInputStream;
    }
    
    public int getReplay() throws Exception {
        return this.client.getReply();
    }
    
    public InputStream readRemote(final String remoteFileName, final int mode) throws Exception {
        if (mode == 0) {
            this.client.setFileType(2);
        }
        else {
            if (mode != 1) {
                throw new Exception("\u0123\u02bd\u05b5\ufffd\ufffd\ufffd\ufffd");
            }
            this.client.setFileType(0);
        }
        return this.readRemote(remoteFileName);
    }
    
    public void rename(final String oldRemoteFileName, final String newRemoteFileName) throws Exception {
        boolean isAcquire = false;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            this.client.rename(this.wrapper(oldRemoteFileName), this.wrapper(newRemoteFileName));
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public void delete(final String remoteFileName) throws Exception {
        boolean isAcquire = false;
        boolean rtn = false;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            rtn = this.client.deleteFile(this.wrapper(remoteFileName));
            if (!rtn) {
                throw new Exception("\u027e\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\u013c\ufffd\u02a7\ufffd\ufffd:" + remoteFileName);
            }
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
    }
    
    public void completePendingCommand() throws Exception {
        this.client.completePendingCommand();
    }
    
    public void close() throws Exception {
        if (this.client.isConnected()) {
            this.client.disconnect();
        }
    }
    
    public OutputStream getOutputStream(final String fileName) throws Exception {
        boolean isAcquire = false;
        OutputStream localOutputStream = null;
        try {
            isAcquire = FtpUtil.SEM.acquire();
            localOutputStream = this.client.storeFileStream(this.wrapper(fileName));
        }
        finally {
            if (isAcquire) {
                FtpUtil.SEM.release();
            }
        }
        return localOutputStream;
    }
    
    public void moveFileToRemoteHisDir(final String fileName) throws Exception {
        if (this.client.listFiles(this.wrapper(fileName)).length == 0) {
            throw new Exception("\ufffd\u013c\ufffd\ufffd\ufffd\ufffd\ufffd\u03aa\ufffd\ufffd");
        }
        if (StringUtils.isBlank(this.remotePathHis)) {
            throw new Exception("\u0536\ufffd\ufffd\ufffd\ufffd\u02b7\u013f?\ufffd\ufffd\ufffd\ufffd\u03aa\ufffd\ufffd");
        }
        final StringBuffer newFileName = new StringBuffer();
        newFileName.append(this.remotePathHis);
        newFileName.append("/");
        newFileName.append(fileName);
        this.rename(fileName, newFileName.toString());
    }
    
    private String wrapper(final String str) throws Exception {
        return new String(str.getBytes(), "ISO-8859-1");
    }
    
    public static void copyFile(final File sourceFile, final File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            final byte[] b = new byte[5120];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        }
        finally {
            if (inBuff != null) {
                inBuff.close();
            }
            if (outBuff != null) {
                outBuff.close();
            }
        }
    }
    
    public static void copyDirectiory(final String sourceDir, final String targetDir) throws IOException {
        new File(targetDir).mkdirs();
        final File[] file = new File(sourceDir).listFiles();
        for (int i = 0; i < file.length; ++i) {
            if (file[i].isFile()) {
                final File sourceFile = file[i];
                final File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                final String dir1 = sourceDir + File.separator + file[i].getName();
                final String dir2 = targetDir + File.separator + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
    
    static {
        FtpUtil.log = Logger.getLogger((Class)FtpUtil.class);
        FtpUtil.TIMEOUT_SECONDS = 120;
        FtpUtil.CONCURRENT_CAPACITY = 20;
        FtpUtil.CONCURRENT_ACQUIRE_TIMEOUT_SECONDS = 3;
        FtpUtil.SEM = null;
        FtpUtil.TIMEOUT_SECONDS = Configuration.getIntValue("AEE.ftp.timeoutSeconds", 120);
        FtpUtil.CONCURRENT_CAPACITY = Configuration.getIntValue("AEE.ftp.concurrentCapacity", 20);
        FtpUtil.CONCURRENT_ACQUIRE_TIMEOUT_SECONDS = Configuration.getIntValue("AEE.ftp.concurrentCapacityAcquireTimeoutSeconds", 3);
        FtpUtil.SEM = new ConcurrentCapacity(FtpUtil.CONCURRENT_CAPACITY, FtpUtil.CONCURRENT_ACQUIRE_TIMEOUT_SECONDS);
        if (FtpUtil.log.isDebugEnabled()) {
            FtpUtil.log.debug((Object)("ftp\ufffd\ufffd\u02b1" + FtpUtil.TIMEOUT_SECONDS + "\ufffd\ufffd"));
            FtpUtil.log.debug((Object)("ftp\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd" + FtpUtil.CONCURRENT_CAPACITY + "\ufffd\ufffd"));
            FtpUtil.log.debug((Object)("ftp\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdacquire\ufffd\ufffd\u02b1(\ufffd\ufffd)" + FtpUtil.CONCURRENT_ACQUIRE_TIMEOUT_SECONDS));
        }
    }
}
