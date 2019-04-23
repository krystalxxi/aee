// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.bootstrap;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.core.write.WriteRequest;
import com.ailk.aee.net.packet.PacketHeader;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import com.ailk.aee.common.conf.ConfigurationFactory;
import org.apache.log4j.Logger;
import com.ailk.aee.platform.service.AbstractPlatformService;
import java.util.Map;
import org.apache.mina.core.service.IoHandler;
import com.ailk.aee.platform.service.CycledPlatformService;

public abstract class Bootstrap extends CycledPlatformService implements IoHandler
{
    protected Map<String, AbstractPlatformService> services;
    private Logger log;
    private ConfigurationFactory factory;
    
    public Bootstrap() {
        this.services = new ConcurrentHashMap<String, AbstractPlatformService>();
        this.log = Logger.getLogger((Class)Bootstrap.class);
        this.factory = null;
    }
    
    public Bootstrap(final ConfigurationFactory factory) {
        this.services = new ConcurrentHashMap<String, AbstractPlatformService>();
        this.log = Logger.getLogger((Class)Bootstrap.class);
        this.factory = null;
        this.setConfigurationFactory(factory);
    }
    
    public void setConfigurationFactory(final ConfigurationFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (this.factory != null) {
            throw new IllegalStateException("factory can't change once set.");
        }
        this.factory = factory;
    }
    
    public ConfigurationFactory getConfigurationFactory() {
        final ConfigurationFactory factory = this.factory;
        if (factory == null) {
            throw new IllegalStateException("factory is not set yet.");
        }
        return factory;
    }
    
    public void start() {
        this.factory.regist();
        this.installServices();
        this.installAdapters();
        this.customize();
    }
    
    public abstract void customize();
    
    public void installServices() {
    }
    
    public void installAdapters() {
    }
    
    public void stop() {
        for (final AbstractPlatformService s : this.services.values()) {
            this.log.debug((Object)("\u0363\u05b9Service" + s.getServiceName()));
            s.stop();
        }
    }
    
    public Map<String, String> callService(final String service, final String method, final Map<String, String> args) {
        AbstractPlatformService ips = null;
        if (this.services.containsKey(service)) {
            ips = this.services.get(service);
            return ips.onServiceCall(method, args);
        }
        return AbstractPlatformService.packageMap("service [" + service + "] is not support yet");
    }
    
    public void installService(final AbstractPlatformService s) {
        if (s != null) {
            this.services.put(s.getServiceName(), s);
            try {
                s.install();
                this.log.debug((Object)("install SERVICE=" + s.getServiceName() + ",class=" + s.getClass().getCanonicalName()));
            }
            catch (Exception e) {
                this.log.error((Object)("install SERVICE=" + s.getServiceName() + ",class=" + s.getClass().getCanonicalName() + " error Message e=" + e.getMessage()));
            }
        }
    }
    
    public void exceptionCaught(final IoSession arg0, final Throwable arg1) throws Exception {
    }
    
    public void messageReceived(final IoSession arg0, final Object arg1) throws Exception {
    }
    
    public void messageSent(final IoSession arg0, final Object arg1) throws Exception {
    }
    
    public void sessionClosed(final IoSession arg0) throws Exception {
    }
    
    public void sessionCreated(final IoSession arg0) throws Exception {
    }
    
    public void sessionIdle(final IoSession arg0, final IdleStatus arg1) throws Exception {
    }
    
    public void sessionOpened(final IoSession arg0) throws Exception {
    }
    
    public void addLogFilter(final DefaultIoFilterChainBuilder filters) {
        filters.addLast("logger", (IoFilter)new IoFilterAdapter() {
            public void messageReceived(final IoFilter.NextFilter nextFilter, final IoSession session, final Object message) throws Exception {
                if (Bootstrap.this.log.isDebugEnabled() && message != null && message instanceof PacketHeader) {
                    final PacketHeader packet = (PacketHeader)message;
                    Bootstrap.this.log.debug((Object)("Received Message From:[" + packet.getFrom() + "] To:[" + packet.getTo() + "]"));
                }
                nextFilter.messageReceived(session, message);
            }
            
            public void messageSent(final IoFilter.NextFilter nextFilter, final IoSession session, final WriteRequest writeRequest) throws Exception {
                if (Bootstrap.this.log.isDebugEnabled()) {
                    final Object message = writeRequest.getOriginalRequest().getMessage();
                    if (message != null && message instanceof PacketHeader) {
                        final PacketHeader packet = (PacketHeader)message;
                        Bootstrap.this.log.debug((Object)("Sent Message From:[" + packet.getFrom() + "] To:[" + packet.getTo() + "]"));
                    }
                }
                nextFilter.messageSent(session, writeRequest);
            }
        });
    }
    
    public void addCodecFilter(final DefaultIoFilterChainBuilder filters) {
        filters.addLast("codec", (IoFilter)new ProtocolCodecFilter((ProtocolCodecFactory)new ObjectSerializationCodecFactory()));
    }
    
    public String getServiceDescription() {
        return null;
    }
    
    public String getServiceName() {
        return null;
    }
    
    @Override
    public boolean cycleActionAsynchronized() {
        return false;
    }
    
    @Override
    public int cycleTime() {
        return -1;
    }
    
    @Override
    public void onCycleTime() {
    }
}
