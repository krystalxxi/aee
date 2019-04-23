// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util.watchdog;

import java.util.Date;
import java.io.IOException;
import java.io.File;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileDog.java 60270 2013-11-03 14:48:37Z tangxy $")
public class FileDog implements IWatchDog
{
    private long lastTime;
    private long lastFileTime;
    private File monitorFile;
    private int timeInterval;
    
    public FileDog(final File v, final int timeInterval) {
        this.lastTime = 0L;
        this.lastFileTime = 0L;
        this.timeInterval = 5;
        if (!v.exists()) {
            try {
                v.createNewFile();
            }
            catch (IOException e1) {
                System.out.println("FileDog Error,Create File:" + v.getAbsolutePath() + "error");
            }
        }
        this.monitorFile = v;
        this.timeInterval = timeInterval;
        this.lastFileTime = v.lastModified();
        this.lastTime = new Date().getTime();
        WatchDogMgr.getInstance().addDog(this);
    }
    
    @Override
    public boolean checked() {
        return this.wangwang();
    }
    
    public long getLastFileTime() {
        return this.lastFileTime;
    }
    
    public long getLastTime() {
        return this.lastTime;
    }
    
    public File getMonitorFile() {
        return this.monitorFile;
    }
    
    public int getTimeInterval() {
        return this.timeInterval;
    }
    
    public void setLastFileTime(final long lastFileTime) {
        this.lastFileTime = lastFileTime;
    }
    
    public void setLastTime(final long lastTime) {
        this.lastTime = lastTime;
    }
    
    public void setMonitorFile(final File monitorFile) {
        this.monitorFile = monitorFile;
    }
    
    public void setTimeInterval(final int timeInterval) {
        this.timeInterval = timeInterval;
    }
    
    @Override
    public String toString() {
        return "WatchDog @" + this.monitorFile.getAbsolutePath() + " ,interval =" + this.timeInterval;
    }
    
    public boolean wangwang() {
        if (this.timeInterval <= 0) {
            return false;
        }
        final long l = new Date().getTime();
        if (l - this.lastTime >= this.timeInterval) {
            this.lastTime = l;
            final long lf = this.monitorFile.lastModified();
            if (lf > this.lastFileTime) {
                synchronized (this) {
                    if (lf > this.lastFileTime) {
                        this.lastFileTime = lf;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
