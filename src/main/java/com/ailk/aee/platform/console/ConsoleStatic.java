// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console;

import com.ailk.aee.common.util.SystemUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConsoleStatic.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConsoleStatic
{
    private String toNodeId;
    private String toNodeHost;
    private String toNodePort;
    private String localIp;
    private static ConsoleStatic instance;
    
    public ConsoleStatic() {
        this.toNodeId = "";
        this.toNodeHost = "127.0.0.1";
        this.toNodePort = "9527";
        this.localIp = "";
    }
    
    public static void setInstance(final ConsoleStatic instance) {
        ConsoleStatic.instance = instance;
    }
    
    public static ConsoleStatic getInstance() {
        return ConsoleStatic.instance;
    }
    
    public String getLocalIp() {
        return SystemUtils.getLocalIp();
    }
    
    public String getToNodeHost() {
        return this.toNodeHost;
    }
    
    public String getToNodeId() {
        return this.toNodeId;
    }
    
    public String getToNodePort() {
        return this.toNodePort;
    }
    
    public void setToNodeHost(final String toNodeHost) {
        this.toNodeHost = toNodeHost;
    }
    
    public void setToNodeId(final String toNodeId) {
        this.toNodeId = toNodeId;
    }
    
    public void setToNodePort(final String toNodePort) {
        this.toNodePort = toNodePort;
    }
    
    static {
        ConsoleStatic.instance = new ConsoleStatic();
    }
}
