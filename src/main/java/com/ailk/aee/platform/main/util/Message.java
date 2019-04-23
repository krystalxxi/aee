// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.main.util;

import com.ailk.aee.common.conf.util.XMLInputStreamParser;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.ailk.aee.common.stringobject.StringMapConverter;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.util.Map;

@CVSID("$Id: Message.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Message implements Map<String, String>
{
    public static final String MSG_TYPE_REQ = "REQ";
    public static final String MSG_TYPE_ANS = "ANS";
    public static String DEFAULT_FIELD;
    private String msgId;
    private String msgType;
    private String serviceName;
    private String methodName;
    private Map<String, String> argument;
    
    public Message() {
        this.msgId = "";
        this.msgType = "";
        this.serviceName = "";
        this.methodName = "";
        this.argument = new HashMap<String, String>();
    }
    
    public static Message dumpAnswerEmptyMessage(final Message m) {
        final Message m2 = new Message();
        m2.setMsgId(m.getMsgId());
        m2.setMsgType("ANS");
        m2.setServiceName(m.getServiceName());
        m2.setMethodName(m.getMethodName());
        return m2;
    }
    
    public static Message errorAnswerMessage(final String orimsg) {
        final Message m = new Message();
        m.setMsgType("ANS");
        m.setServiceName("ERROR");
        m.setMethodName("ERROR");
        m.put("AEE_RESULT_CODE", "-1");
        m.put("AEE_RESULT_INFO", orimsg);
        return m;
    }
    
    public static Message parseMessage(final String sw) throws Exception {
        boolean isNoArg = false;
        if (sw == null || sw.length() == 0) {
            throw new Exception("command not end with [;]");
        }
        final Message m = new Message();
        final String si = sw.trim();
        if (!si.endsWith(";")) {
            throw new Exception("command not end with [;]");
        }
        String s = si;
        if (si.startsWith("[")) {
            m.setMsgId(StringUtils.substringBetween(si, "[", "]"));
            s = StringUtils.substringAfter(si, "]");
        }
        String cmd = StringUtils.substringBefore(s, ":");
        if (cmd.endsWith(";")) {
            isNoArg = true;
            cmd = StringUtils.substringBeforeLast(cmd, ";");
        }
        final String[] cmds = StringUtils.split(cmd);
        if (cmds.length != 3) {
            throw new Exception("must give type,service name and method name");
        }
        m.setMsgType(cmds[0].toUpperCase());
        m.setServiceName(cmds[1]);
        m.setMethodName(cmds[2]);
        if (!isNoArg) {
            String args = StringUtils.substringAfter(s, ":");
            if (args != null && args.length() > 0) {
                args = StringUtils.substringBeforeLast(args, ";").trim();
                if (args.startsWith("{")) {
                    final String targs = StringUtils.substringBeforeLast(args, "}") + "}";
                    final StringMapConverter mc = new StringMapConverter();
                    if (mc.canWrapFromString(targs)) {
                        final Map<String, String> ms = (Map<String, String>)mc.wrapFromString(targs);
                        m.putAll(ms);
                    }
                    else {
                        m.put("AEE_ORIGIN_MSG", args);
                    }
                }
                else {
                    m.put("AEE_ORIGIN_MSG", args);
                }
            }
        }
        return m;
    }
    
    @Override
    public void clear() {
        this.argument.clear();
    }
    
    @Override
    public boolean containsKey(final Object arg0) {
        return this.argument.containsKey(arg0);
    }
    
    @Override
    public boolean containsValue(final Object arg0) {
        return this.argument.containsValue(arg0);
    }
    
    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.argument.entrySet();
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return this.argument.equals(arg0);
    }
    
    @Override
    public String get(final Object arg0) {
        return this.argument.get(arg0);
    }
    
    public Map<String, String> getArgument() {
        return this.argument;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public String getMsgId() {
        return this.msgId;
    }
    
    public String getMsgType() {
        return this.msgType;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    @Override
    public int hashCode() {
        return this.argument.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.argument.isEmpty();
    }
    
    @Override
    public Set<String> keySet() {
        return this.argument.keySet();
    }
    
    @Override
    public String put(final String arg0, final String arg1) {
        return this.argument.put(arg0, arg1);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends String> arg0) {
        this.argument.putAll(arg0);
    }
    
    @Override
    public String remove(final Object arg0) {
        return this.argument.remove(arg0);
    }
    
    public void setArgument(final Map<String, String> argument) {
        this.argument = argument;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    public void setMsgId(final String msgId) {
        this.msgId = msgId;
    }
    
    public void setMsgType(final String msgType) {
        this.msgType = msgType;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    @Override
    public int size() {
        return this.argument.size();
    }
    
    public String toMessageString() {
        final StringBuffer sb = new StringBuffer();
        if (!this.getMsgId().equals("")) {
            sb.append("[").append(this.getMsgId()).append("]");
        }
        sb.append((this.getMsgType() + " " + this.getServiceName() + " " + this.getMethodName() + " ").trim());
        if (this.argument.size() > 0) {
            sb.append(":");
        }
        if (this.argument.size() > 0) {
            final Iterator<Entry<String, String>> iter = this.argument.entrySet().iterator();
            sb.append("{");
            while (iter.hasNext()) {
                final Entry<String, String> e = iter.next();
                sb.append(e.getKey()).append("='").append(e.getValue()).append("'");
                if (iter.hasNext()) {
                    sb.append(",");
                }
            }
            sb.append("}");
        }
        sb.append(";");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Message:").append("\n");
        sb.append("\t").append("MESSAGE ID  =[").append(this.getMsgId()).append("]\n");
        sb.append("\t").append("MESSAGE TYPE=[").append(this.getMsgType()).append("]\n");
        sb.append("\t").append("SERVICE NAME=[").append(this.getServiceName()).append("]\n");
        sb.append("\t").append("METHO NAME  =[").append(this.getMethodName()).append("]\n");
        if (this.argument.size() > 0) {
            for (final Entry<String, String> e : this.argument.entrySet()) {
                sb.append("\t").append("  ARGUMENT-->[").append(e.getKey()).append("]=[").append(e.getValue()).append("]\n");
            }
        }
        return sb.toString();
    }
    
    @Override
    public Collection<String> values() {
        return this.argument.values();
    }
    
    public static void main(final String[] args) {
        try {
            String s = "REQ TRANS AEE_ALL:{AEE_TO_WORKNAME='test1',AEE_TO_COMMAND='REQ T list:{AEE_THREAD_NAME=main};'};";
            final Message m = parseMessage(s);
            System.out.println(m);
            s = "<sleepStrategy value=\"com.ailk.aee.strategy.StaticWorkCounterStrategy\">\t\t\t\t\t<v>1</v>\t\t\t\t</sleepStrategy>";
            final Map<String, String> ms = (Map<String, String>)XMLInputStreamParser.parseString(s);
            System.out.println(ms);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        Message.DEFAULT_FIELD = "AEE_METHOD_RESPONSE";
    }
}
