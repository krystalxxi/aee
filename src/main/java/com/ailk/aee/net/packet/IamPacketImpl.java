// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import com.ailk.aee.common.util.PIDUtils;

public class IamPacketImpl extends AbstractPacket implements IamPacket
{
    private static final long serialVersionUID = 1L;
    private String who;
    
    public IamPacketImpl() {
        this.who = "";
    }
    
    @Override
    public String iAm() {
        return this.who;
    }
    
    @Override
    public void setIam(final String who) {
        if (who == null || who.length() == 0) {
            throw new IllegalArgumentException("who can not null.");
        }
        this.who = who;
    }
    
    @Override
    public PacketHeader.packetType getType() {
        return PacketHeader.packetType.WHO;
    }
    
    @Override
    public String getPID() {
        return String.valueOf(PIDUtils.getPid());
    }
}
