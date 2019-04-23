// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.tools;

import java.util.Map;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.AEEConf;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConfigurationUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConfigurationUtils
{
    public static void main(final String[] args) {
        AEEConf.init();
        if (args.length == 2) {
            final String workName = args[0].trim();
            final String specName = args[1].trim();
            final Map<String, String> cfg = AEEWorkConfig.getInstance().getWorkConfig(workName);
            if (cfg == null || cfg.size() == 0 || !cfg.containsKey(specName)) {
                return;
            }
            System.out.print(cfg.get(specName).trim());
        }
        else if (args.length == 1) {
            final String workName = args[0].trim();
            final String cfg2 = AEEWorkConfig.getInstance().getSingleConfig(workName);
            System.out.println(cfg2);
        }
        else {
            final Map<String, String> ms = AEEWorkConfig.getInstance().getAllConfig();
            System.out.println(MapTools.mapToString((Map)ms));
        }
    }
}
