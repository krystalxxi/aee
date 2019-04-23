// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

public interface AuthPacket extends PacketHeader
{
    String getUser();
    
    String getPassword();
    
    long getSeed();
}
