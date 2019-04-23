// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import java.util.HashMap;
import java.util.Map;

public class ResponsePacketImpl extends AbstractPacket implements ResponsePacket
{
    private static final long serialVersionUID = 1L;
    private int resultCode;
    private Map<String, String> resultInfo;
    
    public ResponsePacketImpl() {
        this.resultCode = -1;
        this.resultInfo = new HashMap<String, String>();
    }
    
    @Override
    public int getResultCode() {
        return this.resultCode;
    }
    
    public void setResultCode(final int code) {
        this.resultCode = code;
    }
    
    @Override
    public Map<String, String> getResultInfo() {
        return this.resultInfo;
    }
    
    public void setResultInfo(final Map<String, String> result) {
        this.resultInfo.clear();
        this.resultInfo.putAll(result);
    }
    
    @Override
    public PacketHeader.packetType getType() {
        return PacketHeader.packetType.RESPONSE;
    }
}
