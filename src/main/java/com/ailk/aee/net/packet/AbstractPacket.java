// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.net.packet;

public abstract class AbstractPacket implements PacketHeader
{
    private static final long serialVersionUID = 1L;
    private String to;
    private String from;
    
    public AbstractPacket() {
        this.to = "";
        this.from = "";
    }
    
    @Override
    public String getFrom() {
        return this.from;
    }
    
    @Override
    public String getTo() {
        return this.to;
    }
    
    @Override
    public void setFrom(final String from) {
        this.from = from;
    }
    
    @Override
    public void setTo(final String to) {
        this.to = to;
    }
}
