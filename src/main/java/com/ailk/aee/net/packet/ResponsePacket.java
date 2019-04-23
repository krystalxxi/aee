// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import java.util.Map;

public interface ResponsePacket extends PacketHeader
{
    int getResultCode();
    
    Map<String, String> getResultInfo();
}
