// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee;

import com.ailk.aee.common.conf.FileConfigurationFactory;
import java.io.File;
import com.ailk.aee.common.conf.Configuration;
import java.util.Iterator;
import java.util.Collection;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEConf.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEConf
{
    public static boolean isInit;
    
    public static synchronized void init() {
        if (!AEEConf.isInit) {
            initself();
            AEEConf.isInit = true;
        }
    }
    
    public static void initClass(final Collection<String> collection) {
        for (final String s : collection) {
            try {
                Class.forName(s.trim());
            }
            catch (ClassNotFoundException e) {
                AEEExceptionProcessor.process(e);
            }
        }
    }
    
    public static void initself() {
        String confFile = Configuration.getValue("AEE_CONF_FILE");
        if (confFile == null || confFile.length() <= 0) {
            String ae = System.getProperty("AEE_HOME");
            if (ae == null) {
                ae = System.getenv("AEE_HOME");
            }
            if (ae != null) {
                final File fn = new File(ae + File.separator + "etc" + File.separator + "aee.cfg");
                if (fn.exists()) {
                    confFile = fn.getAbsolutePath();
                }
            }
            if (confFile == null || confFile.length() == 0) {
                confFile = "aee.cfg";
            }
        }
        final FileConfigurationFactory fc = new FileConfigurationFactory(confFile, "com.ailk.common.conf.parsetype.XML");
        fc.regist();
        if (fc.getLocation() == null || fc.getLocation().trim().equals("")) {
            System.out.println("Can't not find " + confFile + ",may get noncorrect configuration");
        }
//        AEELogger.configureLogger();
        if (Configuration.getBooleanValue("AEE.confs.dumpConfiguration")) {
            System.out.println(Configuration.dump());
        }
    }
    
    static {
        AEEConf.isInit = false;
    }
}
