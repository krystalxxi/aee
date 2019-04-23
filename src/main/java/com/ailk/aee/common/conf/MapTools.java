// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.TreeMap;
import java.util.Iterator;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MapTools.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MapTools
{
    public static boolean containsKey(final Map<String, String> m, final String keyname) {
        return m.containsKey(keyname);
    }
    
    public static Map<String, String> getSub(final Map<String, String> m, final String prefix) {
        return getSub(m, prefix, false);
    }
    
    public static Map<String, String> getSub(final Map<String, String> m, final String prefix, final boolean saveprefix) {
        final HashMap<String, String> n = new HashMap<String, String>();
        String key = prefix;
        if (!prefix.endsWith(".")) {
            key = prefix + ".";
        }
        for (final String s : m.keySet()) {
            if (s.startsWith(key)) {
                if (saveprefix) {
                    n.put(s, m.get(s));
                }
                else {
                    n.put(StringUtils.substringAfter(s, key), m.get(s));
                }
            }
        }
        return n;
    }
    
    public static Map<String, String> addPrefix(final Map<String, String> m, final String prefix) {
        final Map<String, String> m2 = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : m.entrySet()) {
            m2.put(prefix + "." + entry.getKey(), entry.getValue());
        }
        return m2;
    }
    
    public static String[] getSubKeys(final Map<String, String> m) {
        final TreeMap<String, String> tm = new TreeMap<String, String>();
        for (final String s : m.keySet()) {
            String sk = "";
            if (s.indexOf(".") > 0) {
                sk = StringUtils.substringBefore(s, ".");
            }
            else {
                sk = s;
            }
            tm.put(sk, "");
        }
        return tm.keySet().toArray(new String[0]);
    }
    
    public static String mapToString(final Map<String, String> m2) {
        final TreeMap<String, String> i = new TreeMap<String, String>();
        i.putAll(m2);
        int maxLength = 0;
        for (final String k : i.keySet()) {
            if (k.length() > maxLength) {
                maxLength = k.length();
            }
        }
        final StringBuffer sb = new StringBuffer();
        for (final Map.Entry<String, String> e : i.entrySet()) {
            sb.append(StringUtils.rightPad(e.getKey(), maxLength));
            sb.append(" : ");
            sb.append(e.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static String safeToString(final Map<String, String> m) {
        if (m == null || m.size() == 0) {
            return "{}";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("{");
        final Iterator<Map.Entry<String, String>> iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, String> e = iter.next();
            final String k = e.getKey();
            String v = e.getValue();
            if (StringUtils.indexOfAny(k, '\'') >= 0) {
                sb.append("\"").append(k).append("\"").append("=");
            }
            else {
                sb.append("'").append(k).append("'").append("=");
            }
            if (v == null) {
                v = "";
            }
            if (StringUtils.indexOfAny(v, '\'') >= 0) {
                sb.append("\"").append(v).append("\"");
            }
            else {
                sb.append("'").append(v).append("'");
            }
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
