// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.log;

import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.AEEConf;
import com.ailk.aee.common.util.PIDUtils;
import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import com.ailk.aee.platform.AEEPlatform;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: LogUtils.java 60272 2013-11-03 15:29:24Z tangxy $")
public class LogUtils
{
    private static boolean isDebug;
    private static int pid;
    private static Logger bootLogger;
    private static Logger errorLogger;
    private static Logger socketLogger;
    private static Logger stdOutLogger;
    private static Logger stdErrLogger;
    
    public static void logBoot(final String logid, final String s) {
        LogUtils.bootLogger.info((Object)(logid + "-->" + s));
    }
    
    public static void logDebug(final String s) {
        System.out.println(s);
    }
    
    public static void logErr(final String s) {
        writelog(LogUtils.stdErrLogger, false, s);
    }
    
    public static void logError(final String s) {
        writelog(LogUtils.errorLogger, false, s);
        writelog(AEEPlatform.getInstance().getLogger(), false, s);
    }
    
    public static void logMessage(final String s) {
        writelog(AEEPlatform.getInstance().getLogger(), false, s);
    }
    
    public static void logOut(final String s) {
        writelog(LogUtils.stdOutLogger, false, s);
    }
    
    public static void logPlatform(final String cat, final String s) {
        writelog(AEEPlatform.getInstance().getLogger(), false, "[" + cat + "]" + s);
    }
    
    public static void logPlatformDebug(final String cat, final String s) {
        writelog(AEEPlatform.getInstance().getLogger(), true, "[" + cat + "]" + s);
    }
    
    public static void logSocketComm(final String s) {
        writelog(LogUtils.socketLogger, true, s);
    }
    
    public static void logWork(final String s) {
        writelog(AEEPlatform.getInstance().getLogger(), true, s);
    }
    
    private static void writelog(final Logger l, final boolean debuginfo, final String s) {
        boolean v = debuginfo;
        if (debuginfo) {
            v = !LogUtils.isDebug;
        }
        if (v) {
            l.debug((Object)("[" + LogUtils.pid + "][" + AEEPlatform.getInstance().getResourceId() + "]-->" + s));
        }
        else {
            l.info((Object)("[" + LogUtils.pid + "][" + AEEPlatform.getInstance().getResourceId() + "]-->" + s));
        }
    }
    
    public static void debugBootMsg(final String msg) {
        if (LogUtils.bootLogger.isEnabledFor((Priority)Level.DEBUG)) {
            outMsg(LogUtils.bootLogger, Level.DEBUG, msg);
        }
    }
    
    public static void infoBootMsg(final String msg) {
        if (LogUtils.bootLogger.isEnabledFor((Priority)Level.INFO)) {
            outMsg(LogUtils.bootLogger, Level.INFO, msg);
        }
    }
    
    public static void warnBootMsg(final String msg) {
        if (LogUtils.bootLogger.isEnabledFor((Priority)Level.WARN)) {
            outMsg(LogUtils.bootLogger, Level.WARN, msg);
        }
    }
    
    public static void errorBootMsg(final String msg) {
        if (LogUtils.bootLogger.isEnabledFor((Priority)Level.ERROR)) {
            outMsg(LogUtils.bootLogger, Level.ERROR, msg);
        }
    }
    
    public static void debugCommMsg(final String msg) {
        if (LogUtils.socketLogger.isEnabledFor((Priority)Level.DEBUG)) {
            outMsg(LogUtils.socketLogger, Level.DEBUG, msg);
        }
    }
    
    public static void infoCommMsg(final String msg) {
        if (LogUtils.socketLogger.isEnabledFor((Priority)Level.INFO)) {
            outMsg(LogUtils.socketLogger, Level.INFO, msg);
        }
    }
    
    public static void errorCommMsg(final String msg) {
        if (LogUtils.socketLogger.isEnabledFor((Priority)Level.ERROR)) {
            outMsg(LogUtils.socketLogger, Level.ERROR, msg);
        }
    }
    
    public static void warnCommMsg(final String msg) {
        if (LogUtils.socketLogger.isEnabledFor((Priority)Level.WARN)) {
            outMsg(LogUtils.socketLogger, Level.WARN, msg);
        }
    }
    
    public static void debugWorkMsg(final Object o) {
        if (AEEPlatform.getInstance().getLogger().isEnabledFor((Priority)Level.DEBUG)) {
            outMsg(AEEPlatform.getInstance().getLogger(), Level.DEBUG, (o == null) ? "Null" : o.toString());
        }
    }
    
    public static void infoWorkMsg(final Object o) {
        if (AEEPlatform.getInstance().getLogger().isEnabledFor((Priority)Level.INFO)) {
            outMsg(AEEPlatform.getInstance().getLogger(), Level.INFO, (o == null) ? "Null" : o.toString());
        }
    }
    
    public static void warnWorkMsg(final Object o) {
        if (AEEPlatform.getInstance().getLogger().isEnabledFor((Priority)Level.WARN)) {
            outMsg(AEEPlatform.getInstance().getLogger(), Level.WARN, (o == null) ? "Null" : o.toString());
        }
    }
    
    public static void errorWorkMsg(final Object o) {
        if (AEEPlatform.getInstance().getLogger().isEnabledFor((Priority)Level.ERROR)) {
            outMsg(AEEPlatform.getInstance().getLogger(), Level.ERROR, (o == null) ? "Null" : o.toString());
        }
    }
    
    public static void debugPlatformMsg(final String cat, final String msg) {
        final String s = "[" + cat + "]" + msg;
        debugWorkMsg(s);
    }
    
    public static void infoPlatformMsg(final String cat, final String msg) {
        final String s = "[" + cat + "]" + msg;
        infoWorkMsg(s);
    }
    
    public static void warnPlatformMsg(final String cat, final String msg) {
        final String s = "[" + cat + "]" + msg;
        warnWorkMsg(s);
    }
    
    public static void errorPlatformMsg(final String cat, final String msg) {
        final String s = "[" + cat + "]" + msg;
        errorWorkMsg(s);
    }
    
    public static void errorUnhandleMsg(final String msg) {
        if (LogUtils.errorLogger.isEnabledFor((Priority)Level.ERROR)) {
            outMsg(LogUtils.errorLogger, Level.ERROR, msg);
        }
    }
    
    public static void outMsg(final Logger l, final Level level, final String s) {
        final String msg = "[" + LogUtils.pid + "][" + AEEPlatform.getInstance().getResourceId() + "]-->" + s;
        if (40000 == level.toInt()) {
            l.error((Object)msg);
        }
        else if (20000 == level.toInt()) {
            l.info((Object)msg);
        }
        else if (10000 == level.toInt()) {
            l.debug((Object)msg);
        }
        else if (30000 == level.toInt()) {
            l.warn((Object)msg);
        }
    }
    
    static {
        LogUtils.isDebug = true;
        LogUtils.pid = PIDUtils.getPid();
        LogUtils.bootLogger = Logger.getLogger("AEE.logger.boot");
        LogUtils.errorLogger = Logger.getLogger("AEE.logger.unhandle");
        LogUtils.socketLogger = Logger.getLogger("AEE.logger.commuinication");
        LogUtils.stdOutLogger = Logger.getLogger("AEE.logger.stdout");
        LogUtils.stdErrLogger = Logger.getLogger("AEE.logger.stderr");
        AEEConf.init();
        LogUtils.isDebug = Configuration.getBooleanValue("AEE_DEBUG_MODE", false);
    }
}
