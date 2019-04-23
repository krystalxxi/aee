// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import java.util.Map;

public interface RequestPacket extends PacketHeader
{
    String getServiceName();
    
    String getMethodName();
    
    Map<String, String> getParams();
}
