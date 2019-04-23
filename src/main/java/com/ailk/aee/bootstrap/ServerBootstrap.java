// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import java.io.IOException;
import com.ailk.aee.common.util.ExceptionUtils;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.log4j.Logger;

public class ServerBootstrap extends Bootstrap
{
    private Logger log;
    private int listenport;
    private SocketAcceptor acceptor;
    
    public ServerBootstrap() {
        this.log = Logger.getLogger((Class)MasterBootstrap.class);
        this.listenport = 9527;
        this.acceptor = null;
    }
    
    public void bind() {
        (this.acceptor = (SocketAcceptor)new NioSocketAcceptor()).setReuseAddress(true);
        this.acceptor.getSessionConfig().setMaxReadBufferSize(65536);
        this.acceptor.getSessionConfig().setKeepAlive(true);
        this.addCodecFilter(this.acceptor.getFilterChain());
        this.addLogFilter(this.acceptor.getFilterChain());
        this.acceptor.setHandler((IoHandler)this);
        this.log.info((Object)"Server will start.");
        try {
            this.acceptor.bind((SocketAddress)new InetSocketAddress(this.listenport));
            this.log.info((Object)("@port" + this.listenport + " start listener,success"));
        }
        catch (IOException e1) {
            this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e1));
            if (this.acceptor != null) {
                this.acceptor.dispose();
            }
        }
    }
    
    @Override
    public void customize() {
        this.bind();
    }
}
