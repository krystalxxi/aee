// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import com.ailk.aee.net.packet.IamPacket;
import com.ailk.aee.net.packet.PacketHeader;
import com.ailk.aee.net.packet.util.PacketsTool;
import org.apache.mina.core.session.IoSession;

public class ConsoleBootstrap extends ClientBootstrap
{
    private String who;
    
    public ConsoleBootstrap() {
        this.who = "client-" + String.valueOf(System.currentTimeMillis());
    }
    
    @Override
    public void sessionOpened(final IoSession arg0) throws Exception {
        final IamPacket packet = PacketsTool.getIamPacketInst();
        packet.setFrom(this.who);
        packet.setTo("MASTER");
        packet.setIam(this.who);
        this.sendMessage(packet);
    }
}
