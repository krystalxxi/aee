// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.handler;

import com.ailk.aee.net.packet.PacketHeader;

public interface MessageHandler
{
    PacketHeader handleMessage(final PacketHeader p0);
}
