// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.dis;

import java.io.IOException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.apache.zookeeper.server.DatadirCleanupManager;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEZooKeeperBridge.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEZooKeeperBridge
{
    public static void runZookKeeperServer(final long serverId) throws IOException, QuorumPeerConfig.ConfigException {
        final AEEQuorumPeerConfig config = new AEEQuorumPeerConfig(serverId);
        config.parseProperties(AEEDisConfig.getAEEDisConfig());
        final DatadirCleanupManager purgeMgr = new DatadirCleanupManager(config.getDataDir(), config.getDataLogDir(), config.getSnapRetainCount(), config.getPurgeInterval());
        purgeMgr.start();
        final QuorumPeerMain qpm = new QuorumPeerMain();
        qpm.runFromConfig((QuorumPeerConfig)config);
    }
}
