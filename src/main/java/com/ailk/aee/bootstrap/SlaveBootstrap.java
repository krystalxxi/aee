// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.platform.service.AbstractPlatformService;
import com.ailk.aee.net.packet.HBPacket;
import org.apache.mina.core.session.IdleStatus;
import com.ailk.aee.net.packet.IamPacket;
import com.ailk.aee.net.packet.PacketHeader;
import com.ailk.aee.net.packet.util.PacketsTool;
import org.apache.mina.core.session.IoSession;

public class SlaveBootstrap extends ClientBootstrap
{
    private String node;
    private String worker;
    
    @Override
    public void customize() {
        super.customize();
        this.runWorker();
    }
    
    public void runWorker() {
    }
    
    @Override
    public void sessionOpened(final IoSession arg0) throws Exception {
        final IamPacket packet = PacketsTool.getIamPacketInst();
        packet.setFrom(this.worker);
        packet.setTo("MASTER");
        packet.setIam(this.worker);
        this.sendMessage(packet);
    }
    
    @Override
    public void sessionIdle(final IoSession arg0, final IdleStatus arg1) throws Exception {
        final HBPacket packet = PacketsTool.getHBPacketInst();
        packet.setFrom(this.worker);
        packet.setTo("MASTER");
    }
    
    @Override
    public void installServices() {
        super.installServices();
        this.installService(this);
    }
    
    @PlatformServiceMethod
    public void shutdown() {
        this.stop();
    }
    
    @Override
    public String getServiceDescription() {
        return "SLAVE MANAGER SERVICE";
    }
    
    @Override
    public String getServiceName() {
        return "SLAVE";
    }
}
