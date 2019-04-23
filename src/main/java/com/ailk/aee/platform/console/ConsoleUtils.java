// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console;

import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.config.AEEWorkConfig;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import com.ailk.aee.common.util.StringUtils;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoConnector;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.ailk.aee.platform.main.util.MinaUtil;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.ailk.aee.platform.main.util.Message;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConsoleUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConsoleUtils
{
    public static Map<String, String> callService(final String workNameFilter, final String serviceName, final String methodName, final String argv) {
        try {
            return callServiceOther(workNameFilter, serviceName, methodName, argv);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Map<String, String> callServiceNodeChange(final String w) {
        final Message in = new Message();
        in.setMsgType("REQ");
        in.setServiceName("MASTER");
        in.setMethodName("nodeAliveNotice");
        in.put("AEE_NODE_IDS", w);
        return callServiceMessage(in);
    }
    
    public static Map<String, String> callServiceBoot(final String w) {
        return callServiceMaster(w, "boot");
    }
    
    public static Map<String, String> callServiceMaster(final String w, final String c) {
        final Message in = new Message();
        in.setMsgType("REQ");
        in.setServiceName("MASTER");
        in.setMethodName(c);
        in.put("AEE_WORK_NAME", w);
        return callServiceMessage(in);
    }
    
    private static Map<String, String> callServiceMessage(final Message in) {
        IoConnector connector = null;
        IoSession session = null;
        final String host = ConsoleStatic.getInstance().getToNodeHost();
        final int port = Integer.parseInt(ConsoleStatic.getInstance().getToNodePort());
        try {
            connector = (IoConnector)new NioSocketConnector();
            final IoSessionConfig cfg = connector.getSessionConfig();
            cfg.setMaxReadBufferSize(65536);
            cfg.setUseReadOperation(true);
            MinaUtil.addLengthFilter(connector.getFilterChain());
            connector.setConnectTimeoutMillis(5000L);
            final ConnectFuture future = connector.connect((SocketAddress)new InetSocketAddress(host, port));
            future.awaitUninterruptibly();
            if (!future.isConnected()) {
                return Message.errorAnswerMessage("\ufffd\u07b7\ufffd\ufffd\ufffd\ufffd\u04fc\ufffd\ufffd\ufffd\u02ff\ufffd@" + host + ":" + port);
            }
            session = future.getSession();
            final String cmds = in.toMessageString();
            final IoBuffer buf = IoBuffer.allocate(cmds.length()).setAutoExpand(true);
            buf.putString((CharSequence)(cmds + "@;"), Charset.defaultCharset().newEncoder());
            buf.flip();
            session.write((Object)buf).await(5000L);
            final ReadFuture readFuture = session.read();
            Object message = null;
            if (readFuture.awaitUninterruptibly(5000L)) {
                message = readFuture.getMessage();
            }
            if (message == null) {
                System.out.println("can not get return Message");
                return Message.errorAnswerMessage("can not get reply ,timeout or no response");
            }
            String msg = "";
            if (message instanceof String) {
                msg = (String)message + ";";
            }
            else {
                final StringBuffer sb = new StringBuffer();
                final IoBuffer in2 = (IoBuffer)message;
                while (in2.hasRemaining()) {
                    sb.append(in2.getString(Charset.defaultCharset().newDecoder()));
                }
                msg = sb.toString();
            }
            return Message.parseMessage(msg);
        }
        catch (CharacterCodingException e) {
            return Message.errorAnswerMessage(e.getMessage());
        }
        catch (InterruptedException e2) {
            return Message.errorAnswerMessage(e2.getMessage());
        }
        catch (Exception e3) {
            return Message.errorAnswerMessage(e3.getMessage());
        }
        finally {
            if (session != null) {
                session.close(true);
                session.getService().dispose();
            }
            if (connector != null) {
                connector.dispose();
            }
        }
    }
    
    private static Map<String, String> callServiceOther(final String workNameFilter, final String serviceName, final String methodName, final String argv) {
        final Message in = new Message();
        in.setMsgType("REQ");
        in.setServiceName("TRANS");
        in.setMethodName("AEE_ALL");
        in.put("AEE_TO_WORKNAME", workNameFilter);
        String toCmd = "REQ " + serviceName + " " + methodName;
        if (argv != null && argv.length() > 0) {
            toCmd = toCmd + ":" + argv;
        }
        toCmd += ";";
        in.put("AEE_TO_COMMAND", toCmd);
        return callServiceMessage(in);
    }
    
    public static Map<String, String> callServicePs(final String w) {
        return callServiceMaster(w, "ps");
    }
    
    public static Map<String, String> callServiceStop(final String w) {
        return callServiceMaster(w, "stop");
    }
    
    public static Map<String, String> union(final Map<String, String> a, final Map<String, String> b) {
        final Map<String, String> s = new HashMap<String, String>();
        s.putAll(a);
        for (final Map.Entry<String, String> e : b.entrySet()) {
            if (a.containsKey(e.getKey())) {
                final String v = a.get(e.getKey());
                s.put(e.getKey(), v + ",[" + e.getValue() + "]");
            }
            else {
                s.put(e.getKey(), e.getValue());
            }
        }
        return s;
    }
    
    public static boolean checkSocketExist(final String loc) {
        final String host = StringUtils.substringBefore(loc, ":");
        final String port = StringUtils.substringAfter(loc, ":");
        final NioSocketConnector connector = new NioSocketConnector();
        connector.getSessionConfig().setMinReadBufferSize(65536);
        MinaUtil.buildFilter(connector.getFilterChain());
        connector.setHandler((IoHandler)new IoHandlerAdapter());
        connector.setConnectTimeoutMillis(500L);
        final ConnectFuture future = connector.connect((SocketAddress)new InetSocketAddress(host, Integer.parseInt(port)));
        future.awaitUninterruptibly();
        boolean retv = false;
        retv = future.isConnected();
        connector.dispose();
        return retv;
    }
    
    public static Map<String, String> listagent() {
        final Map<String, String> s = new HashMap<String, String>();
        final String[] arr$;
        final String[] nodes = arr$ = AEEWorkConfig.getInstance().getAllNodes();
        for (final String n : arr$) {
            final String l = AEEWorkConfig.getInstance().getSingleConfig("AEE.nodes." + n + ".location");
            s.put(n + ".Location", l);
            final boolean b = checkSocketExist(l);
            s.put(n + ".isAlive", Boolean.toString(b));
            if (Configuration.getValue("AEE_NODE_ID").equals(n)) {
                s.put(n + ".is_current_node", "TRUE");
            }
            else {
                s.put(n + ".is_current_node", "*");
            }
        }
        return s;
    }
}
