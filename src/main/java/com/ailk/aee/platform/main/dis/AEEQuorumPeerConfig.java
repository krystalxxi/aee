// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.dis;

import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import org.slf4j.MDC;
import org.slf4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

@CVSID("$Id: AEEQuorumPeerConfig.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEQuorumPeerConfig extends QuorumPeerConfig
{
    private static final Logger LOG;
    private final int MIN_SNAP_RETAIN_COUNT = 3;
    
    public AEEQuorumPeerConfig(final long serverId) {
        this.serverId = serverId;
        MDC.put("myid", Long.toString(this.serverId));
    }
    
    public String getDataDir() {
        return super.getDataDir() + File.separator + Long.toString(this.serverId);
    }
    
    public void parseProperties(final Properties zkProp) throws IOException, QuorumPeerConfig.ConfigException {
        int clientPort = 0;
        String clientPortAddress = null;
        for (final Map.Entry<Object, Object> entry : zkProp.entrySet()) {
            final String key = entry.getKey().toString().trim();
            final String value = entry.getValue().toString().trim();
            if (key.equals("dataDir")) {
                this.dataDir = value;
            }
            else if (key.equals("dataLogDir")) {
                this.dataLogDir = value;
            }
            else if (key.equals("clientPort")) {
                clientPort = Integer.parseInt(value);
            }
            else if (key.equals("clientPortAddress")) {
                clientPortAddress = value.trim();
            }
            else if (key.equals("tickTime")) {
                this.tickTime = Integer.parseInt(value);
            }
            else if (key.equals("maxClientCnxns")) {
                this.maxClientCnxns = Integer.parseInt(value);
            }
            else if (key.equals("minSessionTimeout")) {
                this.minSessionTimeout = Integer.parseInt(value);
            }
            else if (key.equals("maxSessionTimeout")) {
                this.maxSessionTimeout = Integer.parseInt(value);
            }
            else if (key.equals("initLimit")) {
                this.initLimit = Integer.parseInt(value);
            }
            else if (key.equals("syncLimit")) {
                this.syncLimit = Integer.parseInt(value);
            }
            else if (key.equals("electionAlg")) {
                this.electionAlg = Integer.parseInt(value);
            }
            else if (key.equals("peerType")) {
                if (value.toLowerCase().equals("observer")) {
                    this.peerType = QuorumPeer.LearnerType.OBSERVER;
                }
                else {
                    if (!value.toLowerCase().equals("participant")) {
                        throw new QuorumPeerConfig.ConfigException("Unrecognised peertype: " + value);
                    }
                    this.peerType = QuorumPeer.LearnerType.PARTICIPANT;
                }
            }
            else if (key.equals("autopurge.snapRetainCount")) {
                this.snapRetainCount = Integer.parseInt(value);
            }
            else if (key.equals("autopurge.purgeInterval")) {
                this.purgeInterval = Integer.parseInt(value);
            }
            else if (key.startsWith("server.")) {
                final int dot = key.indexOf(46);
                final long sid = Long.parseLong(key.substring(dot + 1));
                final String[] parts = value.split(":");
                if (parts.length != 2 && parts.length != 3 && parts.length != 4) {
                    AEEQuorumPeerConfig.LOG.error(value + " does not have the form host:port or host:port:port " + " or host:port:port:type");
                }
                final InetSocketAddress addr = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
                if (parts.length == 2) {
                    this.servers.put(sid, new QuorumPeer.QuorumServer(sid, addr));
                }
                else if (parts.length == 3) {
                    final InetSocketAddress electionAddr = new InetSocketAddress(parts[0], Integer.parseInt(parts[2]));
                    this.servers.put(sid, new QuorumPeer.QuorumServer(sid, addr, electionAddr));
                }
                else {
                    if (parts.length != 4) {
                        continue;
                    }
                    final InetSocketAddress electionAddr = new InetSocketAddress(parts[0], Integer.parseInt(parts[2]));
                    QuorumPeer.LearnerType type = QuorumPeer.LearnerType.PARTICIPANT;
                    if (parts[3].toLowerCase().equals("observer")) {
                        type = QuorumPeer.LearnerType.OBSERVER;
                        this.observers.put(sid, new QuorumPeer.QuorumServer(sid, addr, electionAddr, type));
                    }
                    else {
                        if (!parts[3].toLowerCase().equals("participant")) {
                            throw new QuorumPeerConfig.ConfigException("Unrecognised peertype: " + value);
                        }
                        type = QuorumPeer.LearnerType.PARTICIPANT;
                        this.servers.put(sid, new QuorumPeer.QuorumServer(sid, addr, electionAddr, type));
                    }
                }
            }
            else if (key.startsWith("group")) {
                final int dot = key.indexOf(46);
                final long gid = Long.parseLong(key.substring(dot + 1));
                ++this.numGroups;
                final String[] arr$;
                final String[] parts = arr$ = value.split(":");
                for (final String s : arr$) {
                    final long sid2 = Long.parseLong(s);
                    if (this.serverGroup.containsKey(sid2)) {
                        throw new QuorumPeerConfig.ConfigException("Server " + sid2 + "is in multiple groups");
                    }
                    this.serverGroup.put(sid2, gid);
                }
            }
            else if (key.startsWith("weight")) {
                final int dot = key.indexOf(46);
                final long sid = Long.parseLong(key.substring(dot + 1));
                this.serverWeight.put(sid, Long.parseLong(value));
            }
            else {
                System.setProperty("zookeeper." + key, value);
            }
        }
        if (this.snapRetainCount < 3) {
            AEEQuorumPeerConfig.LOG.warn("Invalid autopurge.snapRetainCount: " + this.snapRetainCount + ". Defaulting to " + 3);
            this.snapRetainCount = 3;
        }
        if (this.dataDir == null) {
            throw new IllegalArgumentException("dataDir is not set");
        }
        if (this.dataLogDir == null) {
            this.dataLogDir = this.dataDir;
        }
        else if (!new File(this.dataLogDir).isDirectory()) {
            throw new IllegalArgumentException("dataLogDir " + this.dataLogDir + " is missing.");
        }
        if (clientPort == 0) {
            throw new IllegalArgumentException("clientPort is not set");
        }
        if (clientPortAddress != null) {
            this.clientPortAddress = new InetSocketAddress(InetAddress.getByName(clientPortAddress), clientPort);
        }
        else {
            this.clientPortAddress = new InetSocketAddress(clientPort);
        }
        if (this.tickTime == 0) {
            throw new IllegalArgumentException("tickTime is not set");
        }
        if (this.minSessionTimeout > this.maxSessionTimeout) {
            throw new IllegalArgumentException("minSessionTimeout must not be larger than maxSessionTimeout");
        }
        if (this.servers.size() != 0) {
            if (this.servers.size() == 1) {
                if (this.observers.size() > 0) {
                    throw new IllegalArgumentException("Observers w/o quorum is an invalid configuration");
                }
                AEEQuorumPeerConfig.LOG.error("Invalid configuration, only one server specified (ignoring)");
                this.servers.clear();
            }
            else if (this.servers.size() > 1) {
                if (this.servers.size() == 2) {
                    AEEQuorumPeerConfig.LOG.warn("No server failure will be tolerated. You need at least 3 servers.");
                }
                else if (this.servers.size() % 2 == 0) {
                    AEEQuorumPeerConfig.LOG.warn("Non-optimial configuration, consider an odd number of servers.");
                }
                if (this.initLimit == 0) {
                    throw new IllegalArgumentException("initLimit is not set");
                }
                if (this.syncLimit == 0) {
                    throw new IllegalArgumentException("syncLimit is not set");
                }
                if (this.electionAlg != 0) {
                    for (final QuorumPeer.QuorumServer s2 : this.servers.values()) {
                        if (s2.electionAddr == null) {
                            throw new IllegalArgumentException("Missing election port for server: " + s2.id);
                        }
                    }
                }
                if (this.serverGroup.size() > 0) {
                    if (this.servers.size() != this.serverGroup.size()) {
                        throw new QuorumPeerConfig.ConfigException("Every server must be in exactly one group");
                    }
                    for (final QuorumPeer.QuorumServer s2 : this.servers.values()) {
                        if (!this.serverWeight.containsKey(s2.id)) {
                            this.serverWeight.put(s2.id, 1L);
                        }
                    }
                    this.quorumVerifier = (QuorumVerifier)new QuorumHierarchical(this.numGroups, this.serverWeight, this.serverGroup);
                }
                else {
                    AEEQuorumPeerConfig.LOG.info("Defaulting to majority quorums");
                    this.quorumVerifier = (QuorumVerifier)new QuorumMaj(this.servers.size());
                }
                this.servers.putAll(this.observers);
                final QuorumPeer.LearnerType roleByServersList = this.observers.containsKey(this.serverId) ? QuorumPeer.LearnerType.OBSERVER : QuorumPeer.LearnerType.PARTICIPANT;
                if (roleByServersList != this.peerType) {
                    AEEQuorumPeerConfig.LOG.warn("Peer type from servers list (" + roleByServersList + ") doesn't match peerType (" + this.peerType + "). Defaulting to servers list.");
                    this.peerType = roleByServersList;
                }
            }
            return;
        }
        if (this.observers.size() > 0) {
            throw new IllegalArgumentException("Observers w/o participants is an invalid configuration");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)QuorumPeerConfig.class);
    }
}
