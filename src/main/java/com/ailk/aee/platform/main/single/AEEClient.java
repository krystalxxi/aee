// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.single;

import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.platform.main.util.EnvCheckUtil;
import com.ailk.aee.common.util.Options;
import java.util.Map;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEClient.java 65098 2013-11-11 13:52:23Z huwl $")
public class AEEClient
{
    private String nodeId;
    private String workName;
    
    public static void main(final String[] args2) {
        try {
            System.setProperty("AEE_HOME", "F:\\codes\\j2ee_comm\\aee\\aee1.1");
            final Map<String, String> ms = AEEWorkConfig.getInstance().getAllConfig();
            System.out.println(MapTools.mapToString((Map)ms));
            final String[] args3 = { "-w" };
            final AEEClient aeec = new AEEClient();
            aeec.runClient(args3);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean check(final String[] args) {
        try {
            final Options opts = new Options();
            opts.addOption('n', "node", 1, 0, "\u05b8\ufffd\ufffd\ufffd\u06b5\ufffdID\ufffd\ufffd\ufffd\ufffd\ufffd\u00fb\ufffd\ufffd\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\u043b\ufffd\ufffd\ufffdAEE_NODE_ID\ufffds\ufffd", false, "");
            opts.addOption('w', "work", 0, 0, "\u05b8\ufffd\ufffd\ufffd\ufffdwork\ufffd\ufffd\u01a3\ufffd\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd", false, "");
            final boolean isArgOk = opts.parser(args);
            if (!isArgOk) {
                final Exception e = opts.getFirstException();
                System.out.println(e.getMessage());
                System.out.println(opts.dumpUsage());
                return false;
            }
            this.nodeId = opts.getOptionValue("node");
            this.workName = opts.getOptionValue("work");
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            EnvCheckUtil.checkAEEHome();
            EnvCheckUtil.checkAEECfg();
            EnvCheckUtil.checkNodeId(this.nodeId);
            EnvCheckUtil.checkWorkName(this.nodeId = Configuration.getValue("AEE_NODE_ID"), this.workName);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void runClient(final String[] args) throws AEERuntimeException {
        final boolean b = this.check(args);
        if (b) {
            AEEPlatform.getInstance().setPlatformMode(AEEPlatform.CLIENT_PLATFORM_MODE_SINGLE);
            AEEPlatform.getInstance().setNodeId(this.nodeId);
            AEEPlatform.getInstance().setWorkName(this.workName);
            AEEPlatform.getInstance().start();
        }
    }
}
