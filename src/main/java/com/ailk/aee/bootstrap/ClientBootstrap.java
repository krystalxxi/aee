// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import com.ailk.aee.net.packet.PacketHeader;
import org.apache.mina.core.future.ConnectFuture;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoConnector;
import org.apache.log4j.Logger;

public class ClientBootstrap extends Bootstrap
{
    private Logger log;
    protected IoConnector connector;
    protected String hostname;
    protected int port;
    protected IoSession session;
    
    public ClientBootstrap() {
        this.log = Logger.getLogger((Class)ClientBootstrap.class);
        this.connector = null;
        this.hostname = "";
        this.port = 0;
        this.session = null;
    }
    
    public void connect() {
        this.connector = (IoConnector)new NioSocketConnector();
        this.connector.getSessionConfig().setMinReadBufferSize(1024);
        this.connector.getSessionConfig().setBothIdleTime(60);
        this.addCodecFilter(this.connector.getFilterChain());
        this.addLogFilter(this.connector.getFilterChain());
        this.connector.setHandler((IoHandler)this);
        this.connector.setConnectTimeoutMillis(30000L);
        final ConnectFuture future = this.connector.connect((SocketAddress)new InetSocketAddress(this.hostname, this.port));
        future.awaitUninterruptibly();
        if (future.isConnected()) {
            this.session = future.getSession();
        }
        else {
            this.log.error((Object)("Can't not connect to " + this.hostname + ":" + this.port));
            if (this.connector != null) {
                this.connector.dispose();
            }
        }
    }
    
    public void sendMessage(final PacketHeader packet) {
        synchronized (this.session) {
            this.session.write((Object)packet);
        }
    }
    
    @Override
    public void customize() {
        this.connect();
    }
}
