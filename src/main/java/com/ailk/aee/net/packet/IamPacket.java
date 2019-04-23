// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

public interface IamPacket extends PacketHeader
{
    String iAm();
    
    void setIam(final String p0);
    
    String getPID();
}
