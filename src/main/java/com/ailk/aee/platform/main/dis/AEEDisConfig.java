// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.dis;

import java.util.Map;
import java.io.File;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.AEEConf;
import java.util.Properties;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEDisConfig.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEDisConfig
{
    public static Properties getAEEDisConfig() {
        AEEConf.init();
        final Properties prop = new Properties();
        final Map<String, String> maps = AEEWorkConfig.getInstance().getConfig("AEE.cluster.keeper");
        if (!maps.containsKey("tickTime")) {
            maps.put("tickTime", "2000");
        }
        if (!maps.containsKey("initLimit")) {
            maps.put("initLimit", "10");
        }
        if (!maps.containsKey("syncLimit")) {
            maps.put("syncLimit", "5");
        }
        if (!maps.containsKey("clientPort")) {
            maps.put("clientPort", "2182");
        }
        if (!maps.containsKey("dataDir")) {
            final String s = Configuration.getValue("AEE_HOME") + File.separator + "etc" + File.separator + "zkdata";
            final File f = new File(s);
            if (!f.exists()) {
                f.mkdirs();
            }
            maps.put("dataDir", s);
        }
        if (!maps.containsKey("server.1")) {
            throw new RuntimeException("at last one server is need");
        }
        prop.putAll(maps);
        return prop;
    }
}
