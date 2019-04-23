// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.ms;

import java.util.concurrent.TimeUnit;
import com.ailk.aee.common.util.ExceptionUtils;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.core.session.IdleStatus;
import com.ailk.aee.platform.AEERuntimeException;
import com.ailk.aee.platform.service.AbstractPlatformService;
import com.ailk.aee.common.util.PIDUtils;
import java.util.Date;
import com.ailk.aee.common.util.DateFormatUtils;
import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import com.ailk.aee.platform.main.util.MinaUtil;
import org.apache.mina.core.session.IoSession;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.ServerSocket;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.platform.main.util.EnvCheckUtil;
import com.ailk.aee.platform.main.AEEEnvException;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.common.util.Options;
import java.io.File;
import com.ailk.aee.common.util.SystemUtils;
import com.ailk.aee.common.conf.Configuration;
import java.util.Random;
import com.ailk.aee.log.LogUtils;
import com.ailk.aee.config.AEEWorkConfig;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.AEEConf;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import com.ailk.aee.platform.main.util.Message;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.transport.socket.SocketAcceptor;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.apache.mina.core.service.IoHandler;
import com.ailk.aee.platform.service.CycledPlatformService;

@CVSID("$Id: AEEMaster.java 62239 2013-11-07 06:59:14Z huwl $")
public class AEEMaster extends CycledPlatformService implements IoHandler
{
    private Map<String, PINFO> infoMap;
    private int listenport;
    protected SocketAcceptor acceptor;
    private AtomicLong msgIdSeed;
    private Map<Long, String> tabSessionMap;
    private Map<String, Long> tabResourceMap;
    private Map<String, CountDownLatch> waitQueue;
    private Map<String, List<Message>> returnQueue;
    private String nodeId;
    private boolean isSlient;
    private Map<String, Integer> failedMap;
    
    public AEEMaster() {
        this.infoMap = new ConcurrentHashMap<String, PINFO>();
        this.listenport = 9527;
        this.acceptor = null;
        this.msgIdSeed = new AtomicLong(0L);
        this.tabSessionMap = new ConcurrentHashMap<Long, String>();
        this.tabResourceMap = new ConcurrentHashMap<String, Long>();
        this.waitQueue = new ConcurrentHashMap<String, CountDownLatch>();
        this.returnQueue = new ConcurrentHashMap<String, List<Message>>();
        this.nodeId = "";
        this.isSlient = false;
        this.failedMap = new HashMap<String, Integer>();
    }
    
    private static void execCmd(final String cmd) {
        try {
            final Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        }
        catch (IOException e) {}
        catch (InterruptedException ex) {}
    }
    
    public static void main(final String[] args) {
        try {
            AEEConf.init();
            final AEEMaster c = new AEEMaster();
            c.runMaster(args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @PlatformServiceMethod
    public Map<String, String> boot(final Map<String, String> args) {
        final String workConfig = args.get("AEE_WORK_NAME");
        return this.bootone(workConfig);
    }
    
    public Map<String, String> bootone(final String workConfig) {
        final Map<String, String> mret = new HashMap<String, String>();
        if (workConfig == null) {
            mret.put("AEE_RESULT_CODE", "-1");
            mret.put("AEE_RESULT_INFO", "MASTER.boot Error!not any work name match " + workConfig + " need boot");
            return mret;
        }
        final String[] arr$;
        final String[] workConfigWorks = arr$ = AEEWorkConfig.getInstance().getMyWorkByFilter(workConfig);
        for (final String workName : arr$) {
            if (this.infoMap.containsKey(this.nodeId + "." + workName)) {
                final PINFO p = this.infoMap.get(this.nodeId + "." + workName);
                final String sault = p.sault;
                final String spid = Integer.toString(p.pid);
                mret.put(workName + ".BOOT", "ALREADY EXIST,ignore boot,current pid=" + spid + ". SAULT=" + sault);
                LogUtils.logPlatform("MASTER", workName + " exists");
            }
            else {
                final String sault2 = Integer.toString(Math.abs(new Random().nextInt()));
                final PINFO p2 = new PINFO();
                p2.nodeId = this.nodeId;
                p2.workName = workName;
                p2.createCommandTime = System.currentTimeMillis();
                p2.sault = sault2;
                p2.status = "WAIT_BOOT";
                p2.pid = -1;
                this.infoMap.put(p2.nodeId + "." + p2.workName, p2);
                LogUtils.logPlatformDebug("MASTER", "===================== infoMap : " + this.infoMap.toString());
                final String aeeHome = Configuration.getValue("AEE_HOME");
                String ext = ".sh";
                if (SystemUtils.isWindows()) {
                    ext = ".bat";
                }
                else {
                    ext = ".sh";
                }
                final String scmd = aeeHome + File.separator + "bin" + File.separator + "aee" + ext + " client " + p2.workName + " -s" + sault2 + "";
                LogUtils.logPlatformDebug("MASTER", "booting " + workName + " " + scmd);
                execCmd(scmd);
                mret.put(workName + ".BOOT", "SAULT=" + sault2);
            }
        }
        mret.put("AEE_RESULT_CODE", "0");
        mret.put("AEE_RESULT_INFO", "MASTER.boot ok!");
        return mret;
    }
    
    public void check(final String[] args) throws AEEEnvException {
        final Options opts = new Options();
        try {
            opts.addOption('n', "node", 1, 0, "\u05b8\ufffd\ufffd\ufffd\u06b5\ufffdID\ufffd\ufffd\ufffd\ufffd\ufffd\u00fb\ufffd\ufffd\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\u043b\ufffd\ufffd\ufffdAEE_NODE_ID\ufffds\ufffd", false, "");
            final boolean isArgOk = opts.parser(args);
            if (!isArgOk) {
                final Exception e = opts.getFirstException();
                System.out.println(e.getMessage());
                System.out.println(opts.dumpUsage());
            }
            this.isSlient = true;
            this.nodeId = opts.getOptionValue("node");
            AEEPlatform.getInstance().setNodeId(this.nodeId);
            AEEPlatform.getInstance().setWorkName("MASTER");
        }
        catch (Exception e2) {
            System.out.println(opts.dumpUsage());
            throw new AEEEnvException(e2.getMessage());
        }
        try {
            EnvCheckUtil.checkAEEHome();
            EnvCheckUtil.checkAEECfg();
            EnvCheckUtil.checkNodeId(this.nodeId);
            this.nodeId = Configuration.getValue("AEE_NODE_ID");
            EnvCheckUtil.perpare("MASTER");
            this.checkNodeConfig();
        }
        catch (Exception e2) {
            AEEExceptionProcessor.process(e2);
            throw new AEEEnvException(e2.getMessage());
        }
    }
    
    public void checkNodeConfig() throws Exception {
        final String node = AEEWorkConfig.getInstance().getSingleConfig("AEE.nodes." + this.nodeId + ".location");
        if (node == null || node.length() == 0) {
            throw new Exception("\u00fb\ufffd\ufffdNode=" + this.nodeId + "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        final int port = Integer.parseInt(StringUtils.substringAfter(node, ":"));
        this.listenport = port;
        try {
            final ServerSocket server = new ServerSocket(this.listenport);
            server.setReuseAddress(true);
            server.close();
        }
        catch (IOException e) {
            throw new Exception("the port " + this.listenport + " is in use!");
        }
        Configuration.getInstance().setLocalConfiguration("AEE.adapters.SOCKET.listenport", String.valueOf(this.listenport));
    }
    
    private Map<String, String> autobootshutdown() {
        final Map<String, String> ms = new HashMap<String, String>();
        final String[] allWorks = AEEWorkConfig.getInstance().getMyWorkByFilter("");
        final List<String> as = new ArrayList<String>();
        for (final String s : allWorks) {
            as.add(s);
            final String rid = this.nodeId + "." + s;
            if (!this.infoMap.containsKey(rid)) {
                LogUtils.logPlatform("MASTER", "Work=" + s + " not exist ,boot Now");
            }
        }
        for (final Map.Entry<String, PINFO> e : this.infoMap.entrySet()) {
            final String wn = StringUtils.substringAfter((String)e.getKey(), ".");
            if (as.contains(wn)) {
                continue;
            }
            LogUtils.logPlatform("MASTER", "Work=" + wn + "should not exist\ufffd\ufffdstop Now");
        }
        return ms;
    }
    
    private void checkSession() {
        LogUtils.logPlatformDebug("MASTER", "Master Of " + AEEPlatform.getInstance().getNodeId() + " listen @" + this.listenport + " still service");
        if (this.isSlient) {
            LogUtils.logPlatformDebug("MASTER", "the auto process check will not effect");
        }
        else {
            LogUtils.logPlatformDebug("MASTER", "start auto process check");
            this.autobootshutdown();
        }
    }
    
    @Override
    public int cycleTime() {
        return 5;
    }
    
    public void exceptionCaught(final IoSession arg0, final Throwable arg1) throws Exception {
        arg1.printStackTrace();
    }
    
    public IoSession findSessionByRid(final String rid) {
        final Long l = this.tabResourceMap.get(rid);
        if (l == null) {
            return null;
        }
        final Map<Long, IoSession> nx = (Map<Long, IoSession>)this.acceptor.getManagedSessions();
        if (nx.containsKey(l)) {
            return nx.get(l);
        }
        return null;
    }
    
    public String getServiceDescription() {
        return "MASTER MANAGER SERVICE";
    }
    
    public String getServiceName() {
        return "MASTER";
    }
    
    public void hb(final IoSession session, final Message m) {
        final String nodeId = m.get((Object)"AEE_NODE_ID");
        final String workName = m.get((Object)"AEE_WORK_NAME");
        final String rid = nodeId + "." + workName;
        final Map<String, String> ret = new HashMap<String, String>();
        if (this.infoMap.containsKey(rid)) {
            final PINFO p = this.infoMap.get(rid);
            ret.put("AEE_NODE_ID", p.nodeId);
            ret.put("AEE_WORK_NAME", p.workName);
            ret.put("AEE_WORK_STATUS", p.status);
            ret.put("AEE_SAULT_ID", p.sault);
            ret.put("AEE_PROCESS_ID", Integer.toString(p.pid));
            ret.put("AEE_RESULT_CODE", "0");
            ret.put("AEE_RESULT_INFO", "MASTER.HB ok!");
        }
        else {
            ret.put("AEE_ERROR_MSG", "can't not find " + rid + " in local map");
        }
        final Message mret = Message.dumpAnswerEmptyMessage(m);
        mret.putAll(ret);
        MinaUtil.sendMessage(session, mret);
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
                MinaUtil.sendMessage(session, Message.errorAnswerMessage("parse Message Error " + e.getMessage()));
            }
            return;
        }
        if (m.getMsgType().equals("ANS")) {
            this.processAnswerMessage(session, m);
        }
        else if (m.getServiceName().equals("TRANS")) {
            this.processTransferMessage(session, m);
        }
        else {
            this.processRequestMessae(session, m);
        }
    }
    
    public void messageSent(final IoSession arg0, final Object arg1) throws Exception {
    }
    
    @Override
    public void onCycleTime() {
        this.checkSession();
    }
    
    private void processAnswerMessage(final IoSession session, final Message m) {
        final String msgId = m.getMsgId();
        if (msgId == null || msgId.equals("")) {
            return;
        }
        if (this.waitQueue.containsKey(msgId)) {
            final CountDownLatch cdl = this.waitQueue.get(msgId);
            if (cdl != null) {
                final List<Message> al = this.returnQueue.get(msgId);
                al.add(m);
                cdl.countDown();
            }
        }
    }
    
    private void processRequestMessae(final IoSession session, final Message m) {
        if (m.getServiceName().equals("MASTER") && m.getMethodName().equals("REG")) {
            this.reg(session, m);
        }
        else if (m.getServiceName().equals("MASTER") && m.getMethodName().equals("HB")) {
            this.hb(session, m);
        }
        else if (m.getServiceName().equals("MASTER") && m.getMethodName().equals("ps")) {
            this.ps(session, m);
        }
        else {
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
    }
    
    private void processTransferMessage(final IoSession session, final Message m) {
        final String rid = m.get((Object)"AEE_TO_WORKNAME");
        final String cmd = m.get((Object)"AEE_TO_COMMAND");
        final MessageTransferTask mtt = new MessageTransferTask(session, rid, cmd);
        final Thread t = new Thread(mtt);
        t.start();
    }
    
    public void ps(final IoSession session, final Message m) {
        final String workConfig = m.get((Object)"AEE_WORK_NAME");
        final String[] works = AEEWorkConfig.getInstance().getMyWorkByFilter(workConfig);
        final Map<String, String> ret = new HashMap<String, String>();
        ret.put("AEE_RESULT_CODE", "0");
        ret.put("AEE_RESULT_INFO", "MASTER.PS ok!");
        for (final String s : works) {
            final PINFO p = this.infoMap.get(this.nodeId + "." + s);
            String v = "";
            if (p == null) {
                v = "not find any work infomation named " + s + "@local map";
                ret.put(s + ".INFO", v);
            }
            else {
                final String sdate = DateFormatUtils.SIMPLE_DATETIME_FORMAT.format(new Date(p.createCommandTime));
                if (PIDUtils.isPidExist(p.pid)) {
                    ret.put(s + ".PID", Integer.toString(p.pid));
                }
                else {
                    ret.put(s + ".PID", Integer.toString(p.pid) + "[*]");
                }
                ret.put(s + ".NODEINFO", AEEWorkConfig.getInstance().getOrginNodeInfo(s));
                ret.put(s + ".SAULT", p.sault);
                ret.put(s + ".STARTTIME", sdate);
                ret.put(s + ".STATUS", p.status);
            }
        }
        final Message mret = Message.dumpAnswerEmptyMessage(m);
        mret.putAll(ret);
        MinaUtil.sendMessage(session, mret);
    }
    
    private void reg(final IoSession session, final Message m) {
        final String nodeId = m.get((Object)"AEE_NODE_ID");
        final String workName = m.get((Object)"AEE_WORK_NAME");
        final String pid = m.get((Object)"AEE_PROCESS_ID");
        final String custSault = m.get((Object)"AEE_SAULT_ID");
        final String rid = nodeId + "." + workName;
        LogUtils.logDebug(m.toString());
        final Map<String, String> mret = new HashMap<String, String>();
        if (custSault.equals("-1")) {
            final PINFO p = new PINFO();
            p.nodeId = this.nodeId;
            p.workName = workName;
            p.pid = Integer.parseInt(pid);
            p.lastHBTime = System.currentTimeMillis();
            p.status = "running";
            p.sault = Integer.toString(Math.abs(new Random().nextInt()));
            p.createCommandTime = System.currentTimeMillis();
            mret.put("AEE_NODE_ID", p.nodeId);
            mret.put("AEE_WORK_NAME", p.workName);
            mret.put("AEE_WORK_STATUS", "running");
            mret.put("AEE_SAULT_ID", p.sault);
            mret.put("AEE_PROCESS_ID", Integer.toString(p.pid));
            mret.put("AEE_RESULT_CODE", "0");
            mret.put("AEE_RESULT_INFO", "MASTER.REG ok!");
            this.tabResourceMap.put(rid, new Long(session.getId()));
            this.tabSessionMap.put(new Long(session.getId()), rid);
            this.infoMap.put(rid, p);
        }
        else {
            LogUtils.logDebug("----------------------------------- reg infomap : " + this.infoMap.toString());
            if (this.infoMap.containsKey(rid)) {
                final PINFO p = this.infoMap.get(rid);
                p.pid = Integer.parseInt(pid);
                p.lastHBTime = System.currentTimeMillis();
                p.status = "running";
                mret.put("AEE_NODE_ID", p.nodeId);
                mret.put("AEE_WORK_NAME", p.workName);
                mret.put("AEE_WORK_STATUS", "running");
                mret.put("AEE_SAULT_ID", p.sault);
                mret.put("AEE_PROCESS_ID", Integer.toString(p.pid));
                mret.put("AEE_RESULT_CODE", "0");
                mret.put("AEE_RESULT_INFO", "MASTER.REG ok!");
                this.tabResourceMap.put(rid, new Long(session.getId()));
                this.tabSessionMap.put(new Long(session.getId()), rid);
            }
            else {
                mret.put("AEE_RESULT_CODE", "-1");
                mret.put("AEE_RESULT_INFO", "MASTER.REG Error!the process work name is not in the control list,shoule not boot@this node");
            }
        }
        final Message ret = Message.dumpAnswerEmptyMessage(m);
        ret.putAll(mret);
        MinaUtil.sendMessage(session, ret);
    }
    
    public void runMaster(final String[] args) throws Exception {
        try {
            this.check(args);
        }
        catch (AEEEnvException e) {
            final int pid = PIDUtils.getPid();
            final String sargs = StringUtils.join((Object[])args, " ");
            final String slogid = "ON_BOOT MASTER[" + pid + "],args=[" + sargs + "],\ufffd\u02f3\ufffd!";
            LogUtils.logBoot(slogid, e.getMessage());
            return;
        }
        try {
            AEEPlatform.getInstance().setNodeId(this.nodeId);
            AEEPlatform.getInstance().setWorkName("MASTER");
            AEEPlatform.getInstance().setPlatformMode(AEEPlatform.CLIENT_PLATFORM_MODE_MASTER);
            LogUtils.logPlatformDebug("PLATFORM", "current deploy mode:" + AEEPlatform.CLIENT_PLATFORM_MODE_MASTER);
            AEEPlatform.getInstance().installService(this);
            AEEPlatform.getInstance().start();
        }
        catch (AEERuntimeException e2) {
            try {
                LogUtils.logPlatform("PLATFORM", "runtime Exception occured :[" + e2.getMessage() + "] quit now!");
            }
            finally {
                AEEPlatform.getInstance().stop();
            }
        }
    }
    
    public void sessionClosed(final IoSession session) throws Exception {
        final Long l = new Long(session.getId());
        if (this.tabSessionMap.containsKey(l)) {
            final String rid = this.tabSessionMap.get(l);
            final PINFO p = this.infoMap.get(rid);
            if (p == null) {
                LogUtils.logPlatform("SERVICE", "RESOURCE_ID=" + rid + " communication session is being closed\ufffd\ufffdmay process has terminated,the infomation is not store at control list,may get stop command");
            }
            else {
                if (this.failedMap.containsKey(rid)) {
                    final Integer n;
                    Integer i = n = this.failedMap.get(rid);
                    ++i;
                    i = n;
                    this.failedMap.put(rid, i);
                }
                else {
                    this.failedMap.put(rid, new Integer(1));
                }
                final int pid = p.pid;
                if (PIDUtils.isPidExist(pid)) {
                    LogUtils.logPlatform("SERVICE", "RESOURCE_ID=" + rid + "\ufffd\ufffd\u04e6\ufffd\ufffdsession\ufffd\ufffd\ufffd\u0631\u0563\ufffd\ufffd\u043f\ufffd\ufffd\u073d\ufffd\ufffd\ufffd\u047e\ufffd\ufffd\u02f3\ufffd,\ufffd\ufffdPID\ufffd\ufffd\u04e6\ufffd\u013d\ufffd\ufffd" + pid + "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
                }
                else {
                    this.infoMap.remove(rid);
                    if (this.isSlient) {
                        LogUtils.logPlatform("SERVICE", "RESOURCE_ID=" + rid + "\ufffd\ufffd\u04e6\ufffd\ufffdsession\ufffd\ufffd\ufffd\u0631\u0563\ufffd\ufffd\u043f\ufffd\ufffd\u073d\ufffd\ufffd\ufffd\u047e\ufffd\ufffd\u02f3\ufffd,\ufffd\ufffdPID\ufffd\ufffd\u04e6\ufffd\u013d\ufffd\ufffd" + pid + "\u04b2\ufffd\ufffd\ufffd\ufffd\ufffd\u06a3\ufffd\ufffd\ufffd\ufffd\ufffd\u0775\ufffd\u01f0\ufffd\u06b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03aasilent,\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
                    }
                    else {
                        LogUtils.logPlatform("SERVICE", "RESOURCE_ID=" + rid + "\ufffd\ufffd\u04e6\ufffd\ufffdsession\ufffd\ufffd\ufffd\u0631\u0563\ufffd\ufffd\u043f\ufffd\ufffd\u073d\ufffd\ufffd\ufffd\u047e\ufffd\ufffd\u02f3\ufffd,\ufffd\ufffdPID\ufffd\ufffd\u04e6\ufffd\u013d\ufffd\ufffd" + pid + "\u04b2\ufffd\ufffd\ufffd\ufffd\ufffd\u06a3\ufffd\ufffd\ufffd\ufffd\ufffd\u0775\ufffd\u01f0\ufffd\u06b5\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03aanot silent,\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
                        this.bootone(StringUtils.substringAfter(rid, "."));
                    }
                }
            }
        }
    }
    
    public void sessionCreated(final IoSession arg0) throws Exception {
    }
    
    public void sessionIdle(final IoSession arg0, final IdleStatus arg1) throws Exception {
    }
    
    public void sessionOpened(final IoSession arg0) throws Exception {
    }
    
    @PlatformServiceMethod
    public Map<String, String> notsilent() {
        final Map<String, String> mret = new HashMap<String, String>();
        this.isSlient = false;
        mret.put("AEE_RESULT_CODE", "0");
        mret.put("AEE_RESULT_INFO", "MASTER.silent set false");
        return mret;
    }
    
    @PlatformServiceMethod
    public Map<String, String> silent() {
        final Map<String, String> mret = new HashMap<String, String>();
        this.isSlient = true;
        mret.put("AEE_RESULT_CODE", "0");
        mret.put("AEE_RESULT_INFO", "MASTER.silent set true");
        return mret;
    }
    
    @PlatformServiceMethod
    public Map<String, String> shutdown(final Map<String, String> args) {
        final String workConfig = args.get("AEE_WORK_NAME");
        return this.shutdownone(workConfig);
    }
    
    @PlatformServiceMethod
    public Map<String, String> nodeAliveNotice(final Map<String, String> args) {
        final String workConfig = args.get("AEE_NODE_IDS");
        final ArrayList<String> alivenodes = AEEWorkConfig.getInstance().setAliveNode(workConfig);
        final StringBuilder sb = new StringBuilder();
        for (final String alivenode : alivenodes) {
            sb.append(alivenode).append(",");
        }
        final Map<String, String> mret = new HashMap<String, String>();
        mret.put("AEE_RESULT_CODE", "0");
        mret.put("AEE_RESULT_INFO", "MASTER.nodeAliveNotice success");
        mret.put("AEE_ALIVE_NODES", sb.toString());
        return mret;
    }
    
    public Map<String, String> shutdownone(final String workConfig) {
        final Map<String, String> mret = new HashMap<String, String>();
        if (workConfig == null) {
            mret.put("AEE_RESULT_CODE", "-1");
            mret.put("AEE_RESULT_INFO", "MASTER.boot Error! not any work match " + workConfig + " to stop");
            return mret;
        }
        final String[] arr$;
        final String[] workConfigWorks = arr$ = AEEWorkConfig.getInstance().getMyWorkByFilter(workConfig);
        for (final String workName : arr$) {
            if (this.infoMap.containsKey(this.nodeId + "." + workName)) {
                final PINFO p = this.infoMap.get(this.nodeId + "." + workName);
                final String sault = p.sault;
                final String spid = Integer.toString(p.pid);
                final String rid = this.nodeId + "." + workName;
                final IoSession sx = this.findSessionByRid(rid);
                if (sx != null) {
                    final Message m = new Message();
                    m.setMsgType("REQ");
                    m.setServiceName("SLAVE");
                    m.setMethodName("shutdown");
                    MinaUtil.sendMessage(sx, m);
                    mret.put(workName + ".SHUTDOWN", "[ps -elf | grep " + spid + "] Send Stop Message Success,current pid=" + spid + ". SAULT=" + sault + " please check after a while");
                }
                else {
                    mret.put(workName + ".SHUTDOWN", "[ps -elf | grep " + spid + "]Not Find Session,Fail to Stop,current pid=" + spid + ". SAULT=" + sault + " please check after a while");
                }
                this.infoMap.remove(this.nodeId + "." + workName);
            }
            else {
                mret.put(workName + ".SHUTDOWN", "NOT EXIST,ignore SHUTDOWN");
            }
        }
        mret.put("AEE_RESULT_CODE", "0");
        mret.put("AEE_RESULT_INFO", "MASTER.shutdown ok!");
        return mret;
    }
    
    public void start() throws Exception {
        (this.acceptor = (SocketAcceptor)new NioSocketAcceptor()).setReuseAddress(true);
        this.acceptor.getSessionConfig().setMaxReadBufferSize(65536);
        MinaUtil.buildFilter(this.acceptor.getFilterChain());
        this.acceptor.setHandler((IoHandler)this);
        LogUtils.logPlatform("MASTER", "For Node:[" + this.nodeId + "],Agent Will start");
        try {
            this.acceptor.bind((SocketAddress)new InetSocketAddress(this.listenport));
            LogUtils.logPlatform("MASTER", "@port" + this.listenport + " start listener\ufffd\ufffdsuccess");
        }
        catch (IOException e1) {
            LogUtils.logError(ExceptionUtils.getExceptionStack((Exception)e1));
            LogUtils.logPlatform("MASTER", "@port" + this.listenport + "start listener error :" + e1.getMessage());
            throw e1;
        }
        if (this.isSlient) {
            LogUtils.logPlatform("MASTER", "\ufffd\ufffdMaster\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u03aa \ufffd\ufffd\ufffd\ufffd\u0123\u02bd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0536\ufffd\ufffd\ufffd\u0363\ufffd\ufffd\ufffd");
        }
        super.start();
    }
    
    public class MessageTransferTask implements Runnable
    {
        private IoSession session;
        private String workconfig;
        private String cmd;
        
        public MessageTransferTask(final IoSession session, final String rid, final String cmd) {
            this.session = session;
            this.workconfig = rid;
            this.cmd = cmd;
        }
        
        @Override
        public void run() {
            Message m = null;
            try {
                m = Message.parseMessage(this.cmd);
            }
            catch (Exception e) {
                final Message mno = new Message();
                mno.setMsgType("ANS");
                mno.setServiceName("ERROR");
                mno.setMethodName("ERROR");
                mno.put("AEE_RESULT_CODE", "-1");
                mno.put("AEE_RESULT_INFO", "TRNAS error! parse Message error" + e.getMessage() + "\n" + this.cmd);
                MinaUtil.sendMessage(this.session, mno);
                return;
            }
            final String[] workConfigWorks = AEEWorkConfig.getInstance().getMyWorkByFilter(this.workconfig);
            if (workConfigWorks == null || workConfigWorks.length == 0) {
                final Message mno = Message.dumpAnswerEmptyMessage(m);
                mno.put("AEE_RESULT_CODE", "0");
                mno.put("AEE_RESULT_INFO", "NOT ANY WORK @" + this.workconfig);
                MinaUtil.sendMessage(this.session, mno);
                return;
            }
            CountDownLatch cdl = null;
            final String msgid = Long.toString(AEEMaster.this.msgIdSeed.incrementAndGet());
            try {
                cdl = new CountDownLatch(workConfigWorks.length);
                final Map<String, String> ret = new HashMap<String, String>();
                for (final String workName : workConfigWorks) {
                    final String rid = AEEMaster.this.nodeId + "." + workName;
                    final IoSession sx = AEEMaster.this.findSessionByRid(rid);
                    if (sx == null) {
                        ret.put(workName + ".AEE_RESULT_CODE", "-1");
                        ret.put(workName + ".AEE_RESULT_INFO", "the SocketSession of Work is not exist,May Process has been down");
                        cdl.countDown();
                    }
                    else {
                        final Message mToWork = new Message();
                        mToWork.setMsgType("REQ");
                        mToWork.setMsgId(msgid);
                        mToWork.setServiceName(m.getServiceName());
                        mToWork.setMethodName(m.getMethodName());
                        mToWork.putAll(m.getArgument());
                        AEEMaster.this.waitQueue.put(msgid, cdl);
                        final List<Message> al = new ArrayList<Message>();
                        AEEMaster.this.returnQueue.put(msgid, al);
                        MinaUtil.sendMessage(sx, mToWork);
                    }
                }
                try {
                    cdl.await(AEEPlatform.getInstance().getTimeOut(), TimeUnit.SECONDS);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                final List<Message> l = AEEMaster.this.returnQueue.get(msgid);
                if (l != null) {
                    if (l.size() != 0) {
                        for (final Message msg : l) {
                            String wn = msg.get((Object)"AEE_WORK_NAME");
                            if (wn == null) {
                                wn = "unknow (" + msg.get((Object)"AEE_PROCESS_ID") + ")";
                            }
                            for (final Map.Entry<String, String> e3 : msg.entrySet()) {
                                final String key = e3.getKey();
                                if (!key.equals("AEE_WORK_NAME") && !key.equals("AEE_NODE_ID")) {
                                    if (key.equals("AEE_PROCESS_ID")) {
                                        continue;
                                    }
                                    ret.put(wn + "." + e3.getKey(), e3.getValue());
                                }
                            }
                        }
                    }
                }
                ret.put("AEE_NORESPONSE_COUNT", Long.toString(cdl.getCount()));
                final Message mout = Message.dumpAnswerEmptyMessage(m);
                mout.putAll(ret);
                MinaUtil.sendMessage(this.session, mout);
            }
            finally {
                while (cdl.getCount() > 0L) {
                    cdl.countDown();
                }
                if (AEEMaster.this.returnQueue.containsKey(msgid)) {
                    AEEMaster.this.returnQueue.remove(msgid);
                }
                if (AEEMaster.this.waitQueue.containsKey(msgid)) {
                    AEEMaster.this.returnQueue.remove(msgid);
                }
            }
        }
    }
    
    class PINFO
    {
        public String nodeId;
        public String workName;
        public String sault;
        public String status;
        public int pid;
        public long createCommandTime;
        public long lastHBTime;
    }
}
