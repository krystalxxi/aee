// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

import java.util.HashMap;
import java.util.Map;

public class RequestPacketImpl extends AbstractPacket implements RequestPacket
{
    private static final long serialVersionUID = 1L;
    private String methodName;
    private String serviceName;
    private Map<String, String> param;
    
    public RequestPacketImpl() {
        this.methodName = "";
        this.serviceName = "";
        this.param = new HashMap<String, String>();
    }
    
    @Override
    public String getMethodName() {
        return this.methodName;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    @Override
    public Map<String, String> getParams() {
        return this.param;
    }
    
    public void setParams(final Map<String, String> params) {
        this.param.clear();
        this.param.putAll(params);
    }
    
    @Override
    public String getServiceName() {
        return this.serviceName;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    @Override
    public PacketHeader.packetType getType() {
        return PacketHeader.packetType.REQUEST;
    }
}
