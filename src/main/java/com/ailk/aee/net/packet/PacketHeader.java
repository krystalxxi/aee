// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import java.io.Serializable;

public interface PacketHeader extends Serializable
{
    packetType getType();
    
    String getFrom();
    
    void setFrom(final String p0);
    
    String getTo();
    
    void setTo(final String p0);
    
    public enum packetType
    {
        REQUEST, 
        RESPONSE, 
        HB, 
        AUTH, 
        WHO;
    }
}
