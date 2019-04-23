// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.ms;

import org.apache.mina.core.future.ConnectFuture;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.core.session.IdleStatus;
import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.service.AbstractPlatformService;
import java.util.concurrent.TimeUnit;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.Map;
import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import com.ailk.aee.platform.main.util.MinaUtil;
import com.ailk.aee.common.util.PIDUtils;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.platform.main.util.Message;
import com.ailk.aee.log.LogUtils;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.platform.main.util.EnvCheckUtil;
import com.ailk.aee.common.util.Options;
import java.util.concurrent.CountDownLatch;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoConnector;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.apache.mina.core.service.IoHandler;
import com.ailk.aee.platform.service.CycledPlatformService;

@CVSID("$Id: AEESlave.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEESlave extends CycledPlatformService implements IoHandler
{
    protected IoConnector connector;
    private int heartbeatTime;
    private String host;
    private int port;
    private IoSession session;
    private String nodeId;
    private String workName;
    private String saultValue;
    private CountDownLatch cdl;
    private int hbcnt;
    
    public AEESlave() {
        this.connector = null;
        this.heartbeatTime = 5;
        this.host = "";
        this.port = 0;
        this.session = null;
        this.cdl = new CountDownLatch(1);
        this.hbcnt = 0;
    }
    
    public static void main(final String[] args) {
        try {
            final AEESlave s = new AEESlave();
            s.runClient(args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Mission Complete!");
    }
    
    public boolean check(final String[] args) {
        try {
            final Options opts = new Options();
            opts.addOption('n', "node", 1, 0, "\u05b8\ufffd\ufffd\ufffd\u06b5\ufffdID\ufffd\ufffd\ufffd\ufffd\ufffd\u00fb\ufffd\ufffd\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\u043b\ufffd\ufffd\ufffdAEE_NODE_ID\ufffd\ufffd\ufffd\ufffd", false, "");
            opts.addOption('w', "work", 0, 0, "\u05b8\ufffd\ufffd\ufffd\ufffdwork\ufffd\ufffd\u01a3\ufffd\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd", false, "");
            opts.addOption('s', "sault", 0, 0, "\u05b8\ufffd\ufffd\ufffd\ufffdsault\u05b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd", false, "");
            final boolean isArgOk = opts.parser(args);
            if (!isArgOk) {
                final Exception e = opts.getFirstException();
                System.out.println(e.getMessage());
                System.out.println(opts.dumpUsage());
                return false;
            }
            this.nodeId = opts.getOptionValue("node");
            this.workName = opts.getOptionValue("work");
            this.saultValue = opts.getOptionValue("sault");
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            EnvCheckUtil.checkAEEHome();
            EnvCheckUtil.checkAEECfg();
            EnvCheckUtil.checkNodeId(this.nodeId);
            EnvCheckUtil.checkWorkName(this.nodeId = Configuration.getValue("AEE_NODE_ID"), this.workName);
            EnvCheckUtil.perpare(this.workName);
            this.checkMasterConfig();
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
        return true;
    }
    
    private void checkMasterConfig() throws Exception {
        final String node = AEEWorkConfig.getInstance().getSingleConfig("AEE.nodes." + this.nodeId + ".location");
        this.port = Integer.parseInt(StringUtils.substringAfter(node, ":"));
        this.host = StringUtils.substringBefore(node, ":");
    }
    
    @Override
    public int cycleTime() {
        return this.heartbeatTime;
    }
    
    public void exceptionCaught(final IoSession arg0, final Throwable e) throws Exception {
        LogUtils.logPlatform("SLAVE", "Communication Session Error " + e.getMessage() + ",Quit!");
        this.quit();
    }
    
    public String getServiceDescription() {
        return "SLAVE MANAGER SERVICE";
    }
    
    public String getServiceName() {
        return "SLAVE";
    }
    
    public void heartbeat() {
        ++this.hbcnt;
        final Message m = new Message();
        m.setMsgType("REQ");
        m.setServiceName("MASTER");
        m.setMethodName("HB");
        m.put("AEE_NODE_ID", AEEPlatform.getInstance().getNodeId());
        m.put("AEE_WORK_NAME", AEEPlatform.getInstance().getWorkName());
        m.put("AEE_WORK_STATUS", "running");
        m.put("AEE_SAULT_ID", this.saultValue);
        m.put("AEE_PROCESS_ID", Integer.toString(PIDUtils.getPid()));
        MinaUtil.sendMessage(this.session, m);
        if (this.hbcnt % 10 == 0) {
            LogUtils.logPlatformDebug("SLAVE", "Slave " + this.nodeId + "." + this.workName + " still alive");
        }
    }
    
    public void innerStop() {
        super.stop();
        AEEPlatform.getInstance().stop();
        if (this.connector != null) {
            this.connector.dispose();
        }
    }
    
    public void messageReceived(final IoSession session, final Object message) throws Exception {
        String msg = "";
        if (message instanceof String) {
            msg = (String)message + ";";
        }
        else {
            final StringBuffer sb = new StringBuffer();
            final IoBuffer in = (IoBuffer)message;
            while (in.hasRemaining()) {
                sb.append(in.getString(Charset.defaultCharset().newDecoder()));
            }
            msg = sb.toString();
        }
        Message m = null;
        try {
            m = Message.parseMessage(msg);
        }
        catch (Exception e) {
            if (!msg.startsWith("ANS")) {
                MinaUtil.sendMessage(session, Message.errorAnswerMessage("parser Message ERror" + e.getMessage()));
            }
            return;
        }
        if (m.getMsgType().equals("ANS")) {
            this.processAnswerMessage(session, m);
        }
        else {
            this.processRequestMessae(session, m);
        }
    }
    
    public void messageSent(final IoSession arg0, final Object arg1) throws Exception {
    }
    
    @Override
    public void onCycleTime() {
        this.heartbeat();
    }
    
    private void processAnswerMessage(final IoSession session, final Message m) {
        if (m.getMethodName().equals("HB") && m.getServiceName().equals("MASTER")) {
            final String sault = m.get((Object)"AEE_SAULT_ID");
            if (!sault.equals(this.saultValue)) {
                LogUtils.logPlatform("SLAVE", "new sault=" + sault + ",is not equal self's sault=" + this.saultValue + ",quit!");
                AEEPlatform.getInstance().stop();
            }
        }
        if (m.getMethodName().equals("REG") && m.getServiceName().equals("MASTER")) {
            final String errorCode = m.get((Object)"AEE_RESULT_CODE");
            if (errorCode.equals("0")) {
                final String pid = m.get((Object)"AEE_PROCESS_ID");
                final String thisPid = Integer.toString(PIDUtils.getPid());
                if (pid.equals(thisPid)) {
                    LogUtils.logPlatform("SLAVE", "reg success\ufffd\ufffdcurrent pid=" + pid);
                    this.cdl.countDown();
                }
                final String sault2 = m.get((Object)"AEE_SAULT_ID");
                this.saultValue = sault2;
            }
            else {
                LogUtils.logPlatform("SLAVE", "reg error," + m.get((Object)"AEE_RESULT_INFO") + ",Quit!");
                this.quit();
                this.cdl.countDown();
            }
        }
    }
    
    private void processRequestMessae(final IoSession session, final Message m) {
        Message mRet = Message.dumpAnswerEmptyMessage(m);
        final Map<String, String> args = m.getArgument();
        try {
            final Map<String, String> ms = AEEPlatform.getInstance().callService(m.getServiceName(), m.getMethodName(), args);
            mRet.putAll(ms);
        }
        catch (Exception e) {
            e.printStackTrace();
            mRet = Message.errorAnswerMessage(e.getMessage() + ",with argument=" + m.getArgument().toString());
        }
        MinaUtil.sendMessage(session, mRet);
    }
    
    @PlatformServiceMethod
    public void shutdown() {
        this.quit();
    }
    
    @PlatformServiceMethod
    public void quit() {
        if (this.cdl.getCount() > 0L) {
            this.cdl.countDown();
        }
        LogUtils.logPlatform("SLAVE", "get message ask for Quit,Quit!");
        this.innerStop();
    }
    
    public void reg() {
        final Message m = new Message();
        m.setMsgType("REQ");
        m.setServiceName("MASTER");
        m.setMethodName("REG");
        m.put("AEE_NODE_ID", AEEPlatform.getInstance().getNodeId());
        m.put("AEE_WORK_NAME", AEEPlatform.getInstance().getWorkName());
        m.put("AEE_SAULT_ID", this.saultValue);
        m.put("AEE_PROCESS_ID", Integer.toString(PIDUtils.getPid()));
        MinaUtil.sendMessage(this.session, m);
    }
    
    public void runClient(final String[] args) throws AEERuntimeException {
        final boolean b = this.check(args);
        if (b) {
            AEEPlatform.getInstance().setNodeId(this.nodeId);
            AEEPlatform.getInstance().setWorkName(this.workName);
            try {
                this.start();
                int i = 0;
                while (true) {
                    this.cdl.await(5L, TimeUnit.SECONDS);
                    ++i;
                    if (this.cdl.getCount() <= 0L) {
                        break;
                    }
                    LogUtils.logPlatform("BOOT", "reg timeout the " + i + " times");
                    if (i < 5) {
                        continue;
                    }
                    LogUtils.logPlatform("BOOT", "reg timeout over " + i + "times,Quit");
                    this.quit();
                }
                AEEPlatform.getInstance().installService(this);
            }
            catch (Exception e) {
                AEEExceptionProcessor.process(e);
                return;
            }
            final String deployMode = Configuration.getValue("AEE_DEPLOY_MODE", "MS");
            if (deployMode.equalsIgnoreCase("MS")) {
                AEEPlatform.getInstance().setPlatformMode(AEEPlatform.CLIENT_PLATFORM_MODE_SLAVE);
            }
            else {
                AEEPlatform.getInstance().setPlatformMode(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_SLAVE);
            }
            AEEPlatform.getInstance().start();
        }
    }
    
    public void sessionClosed(final IoSession arg0) throws Exception {
    }
    
    public void sessionCreated(final IoSession arg0) throws Exception {
    }
    
    public void sessionIdle(final IoSession arg0, final IdleStatus arg1) throws Exception {
    }
    
    public void sessionOpened(final IoSession arg0) throws Exception {
    }
    
    public void start() throws Exception {
        if (this.isRunning()) {
            return;
        }
        this.connector = (IoConnector)new NioSocketConnector();
        this.connector.getSessionConfig().setMinReadBufferSize(65536);
        MinaUtil.buildFilter(this.connector.getFilterChain());
        this.connector.setHandler((IoHandler)this);
        final int timeout = Configuration.getIntValue("AEE_CONNECT_TIMEOUT", 5);
        this.connector.setConnectTimeoutMillis((long)(timeout * 1000));
        final ConnectFuture future = this.connector.connect((SocketAddress)new InetSocketAddress(this.host, this.port));
        future.awaitUninterruptibly();
        if (future.isConnected()) {
            this.session = future.getSession();
            this.reg();
            super.start();
            return;
        }
        LogUtils.logPlatform("BOOT", "Can't not connect to " + this.host + ":" + this.port);
        this.quit();
        throw new Exception("Can't not connect to " + this.host + ":" + this.port);
    }
    
    public void stop() {
        super.stop();
        this.connector.dispose();
    }
}
