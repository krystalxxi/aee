// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.util;

import com.ailk.aee.AEELogger;
import java.util.Map;
import com.ailk.aee.config.AEEWorkConfig;
import java.io.File;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.AEEConf;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EnvCheckUtil.java 60795 2013-11-05 03:36:27Z tangxy $")
public class EnvCheckUtil
{
    public static void checkAEECfg() throws Exception {
        AEEConf.init();
        final String workConfig = Configuration.getValue("AEE_WORK_CONFIG");
        if (workConfig == null || workConfig.equals("")) {
            throw new Exception("can't not find AEE_WORK_CONFIG configuration");
        }
        final File f = new File(workConfig);
        if (!f.exists()) {
            throw new Exception("AEE_WORK_CONFIG file of " + workConfig + " not exist");
        }
    }
    
    public static void checkAEEHome() throws Exception {
        final String v = Configuration.getValue("AEE_HOME");
        if (v != null) {
            final File f = new File(v);
            if (f.exists() && f.isDirectory()) {
                return;
            }
        }
        throw new Exception("can't not get AEE_HOME configuration\ufffd\ufffdOr AEE_HOME=" + v + " is not a real directory");
    }
    
    public static void checkNodeId(final String nodeId2) throws Exception {
        if (nodeId2 == null || nodeId2.length() == 0) {
            final String nodeId3 = Configuration.getValue("AEE_NODE_ID");
            if (nodeId3 == null || nodeId3.length() == 0) {
                throw new Exception("AEE_NODE_ID is need or use option of -n");
            }
        }
        else {
            Configuration.getInstance().setLocalConfiguration("AEE_NODE_ID", nodeId2);
        }
    }
    
    public static void checkWorkName(final String nodeId, final String workName) throws Exception {
        if (workName == null) {
            throw new Exception("Work is needed");
        }
        final Map<String, String> ms = AEEWorkConfig.getInstance().getWorkConfig(workName);
        if (ms != null && ms.size() > 0) {
            return;
        }
        throw new Exception("the work of " + workName + " has not any configuration");
    }
    
    public static void perpare(final String s) {
        if (Configuration.getBooleanValue("AEE_DEBUG_MODE", false)) {
            AEELogger.configureDebugLogger(s);
        }
    }
}
