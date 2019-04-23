// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.util;

import java.nio.charset.CharacterCodingException;
import java.io.IOException;
import java.net.ServerSocket;
import com.ailk.aee.log.LogUtils;
import com.ailk.aee.common.conf.Configuration;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import com.ailk.aee.common.util.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import java.nio.charset.Charset;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MinaUtil.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MinaUtil
{
    public static void addLengthFilter(final DefaultIoFilterChainBuilder iof) {
        final TextLineCodecFactory y = new TextLineCodecFactory(Charset.defaultCharset(), new LineDelimiter("@;"), new LineDelimiter(";@;"));
        y.setDecoderMaxLineLength(65536);
        y.setEncoderMaxLineLength(65536);
        final ProtocolCodecFilter x = new ProtocolCodecFilter((ProtocolCodecFactory)y);
        iof.addLast("codec", (IoFilter)x);
    }
    
    public static void addLengt2Filter(final DefaultIoFilterChainBuilder iof) {
        iof.addFirst("length", (IoFilter)new IoFilterAdapter() {
            public void messageReceived(final IoFilter.NextFilter nextFilter, final IoSession session, final Object message) throws Exception {
                final IoBuffer ib = ((IoBuffer)message).duplicate();
                final String sb = ib.getString(Charset.defaultCharset().newDecoder());
                if (session.containsAttribute((Object)"NEED_REMAIN")) {
                    ((IoBuffer)message).getString(Charset.defaultCharset().newDecoder());
                    final int l = (int)session.getAttribute((Object)"NEED_REMAIN");
                    final String s = (String)session.getAttribute((Object)"BEFORE_DATA", (Object)sb);
                    if (sb.length() >= l) {
                        final IoBuffer iof = IoBuffer.allocate(l + s.length() - 8);
                        iof.putString((CharSequence)(StringUtils.substring(sb, 0, 8) + s), Charset.defaultCharset().newEncoder());
                        session.removeAttribute((Object)"NEED_REMAIN");
                        session.removeAttribute((Object)"BEFORE_DATA");
                        nextFilter.messageReceived(session, (Object)iof);
                    }
                    else {
                        session.setAttribute((Object)"BEFORE_DATA", (Object)(s + sb));
                        session.setAttribute((Object)"NEED_REMAIN", (Object)(l - sb.length()));
                    }
                }
                else if (sb.length() >= 8) {
                    final String i = StringUtils.substring(sb, 0, 8);
                    final int len = Integer.parseInt(i);
                    System.out.println("------------" + sb);
                    if (len == sb.length() - 8) {
                        nextFilter.messageReceived(session, message);
                    }
                    else {
                        session.setAttribute((Object)"BEFORE_DATA", (Object)sb);
                        session.setAttributeIfAbsent((Object)"NEED_REMAIN", (Object)(len - sb.length() - 8));
                        ((IoBuffer)message).getString(Charset.defaultCharset().newDecoder());
                    }
                }
                else {
                    nextFilter.messageReceived(session, message);
                }
            }
            
            public void messageSent(final IoFilter.NextFilter nextFilter, final IoSession session, final WriteRequest writeRequest) throws Exception {
                final IoBuffer ib2 = (IoBuffer)writeRequest.getMessage();
                final String m = ib2.getString(Charset.defaultCharset().newDecoder());
                final String m2 = StringUtils.leftPad(Integer.toString(m.length()), 9);
                final IoBuffer ibc = IoBuffer.allocate((m + m2).length());
                ibc.putString((CharSequence)(m2 + m), Charset.defaultCharset().newEncoder());
                final DefaultWriteRequest dwr = new DefaultWriteRequest((Object)ibc);
                nextFilter.messageSent(session, (WriteRequest)dwr);
            }
        });
    }
    
    public static void addLogFilter(final DefaultIoFilterChainBuilder iofcb) {
        if (Configuration.getBooleanValue("AEE_DEBUG_MODE", false)) {
            iofcb.addFirst("logger", (IoFilter)new IoFilterAdapter() {
                public void messageReceived(final IoFilter.NextFilter nextFilter, final IoSession session, final Object message) throws Exception {
                    final IoBuffer ib = ((IoBuffer)message).duplicate();
                    LogUtils.logSocketComm("RECEIVED: {" + session.getId() + "}-->" + ib.getString(Charset.defaultCharset().newDecoder()));
                    nextFilter.messageReceived(session, message);
                }
                
                public void messageSent(final IoFilter.NextFilter nextFilter, final IoSession session, final WriteRequest writeRequest) throws Exception {
                    final IoBuffer ib = ((IoBuffer)writeRequest.getMessage()).duplicate();
                    LogUtils.logSocketComm("SENT: {" + session.getId() + "}-->" + ib.getString(Charset.defaultCharset().newDecoder()));
                    nextFilter.messageSent(session, writeRequest);
                }
            });
        }
    }
    
    public static void buildFilter(final DefaultIoFilterChainBuilder iofcb) {
        addLogFilter(iofcb);
        addLengthFilter(iofcb);
    }
    
    public static boolean isListenPortExist(final int port) {
        try {
            final ServerSocket server = new ServerSocket(port);
            server.setReuseAddress(true);
            server.close();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public static void sendMessage(final IoSession session, final Message m) {
        final String retMsg = m.toMessageString() + "@;";
        final IoBuffer buf = IoBuffer.allocate(retMsg.length()).setAutoExpand(true);
        try {
            buf.putString((CharSequence)retMsg, Charset.defaultCharset().newEncoder());
        }
        catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        buf.flip();
        session.write((Object)buf);
    }
    
    public static void sendMessage(final IoSession session, final String retMsg) throws Exception {
        final IoBuffer buf = IoBuffer.allocate(retMsg.length()).setAutoExpand(true);
        buf.putString((CharSequence)retMsg, Charset.defaultCharset().newEncoder());
        buf.flip();
        session.write((Object)buf);
    }
}
