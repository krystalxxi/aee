// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet.util;

import com.ailk.aee.net.packet.IamPacketImpl;
import com.ailk.aee.net.packet.IamPacket;
import com.ailk.aee.net.packet.HBPacketImpl;
import com.ailk.aee.net.packet.HBPacket;
import com.ailk.aee.net.packet.ResponsePacketImpl;
import com.ailk.aee.net.packet.ResponsePacket;
import com.ailk.aee.net.packet.RequestPacketImpl;
import com.ailk.aee.net.packet.RequestPacket;

public class PacketsTool
{
    public static RequestPacket getRequestPacketInst() {
        return new RequestPacketImpl();
    }
    
    public static ResponsePacket getResponsePacketInst() {
        return new ResponsePacketImpl();
    }
    
    public static HBPacket getHBPacketInst() {
        return new HBPacketImpl();
    }
    
    public static IamPacket getIamPacketInst() {
        return new IamPacketImpl();
    }
}
