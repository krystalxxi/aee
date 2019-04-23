// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

public class HBPacketImpl extends AbstractPacket implements HBPacket
{
    private static final long serialVersionUID = 1L;
    private long time;
    
    public HBPacketImpl() {
        this.time = System.currentTimeMillis();
    }
    
    @Override
    public long getHBTime() {
        return this.time;
    }
    
    @Override
    public PacketHeader.packetType getType() {
        return PacketHeader.packetType.HB;
    }
}
