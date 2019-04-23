// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.dis;

import com.ailk.aee.common.util.Options;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEDisServer.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEDisServer
{
    private long myid;
    
    public AEEDisServer() {
        this.myid = 0L;
    }
    
    public static void main(final String[] args) {
        final Options opts = new Options();
        opts.addOption('i', "myid", 0, 0, "\u05b8\ufffd\ufffdkeeper\ufffd\u06b5\ufffdID", false, "");
        final boolean isArgOk = opts.parser(args);
        if (isArgOk) {
            try {
                final String myids = opts.getOptionValue("myid");
                long myid = 0L;
                try {
                    myid = Long.parseLong(myids);
                }
                catch (NumberFormatException e2) {
                    throw new IllegalArgumentException("serverid " + myids + " is not a number");
                }
                AEEZooKeeperBridge.runZookKeeperServer(myid);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        final Exception e = opts.getFirstException();
        System.out.println(e.getMessage());
        System.out.println(opts.dumpUsage());
    }
}
