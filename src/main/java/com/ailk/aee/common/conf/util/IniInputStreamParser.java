// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IniInputStreamParser.java 60270 2013-11-03 14:48:37Z tangxy $")
public class IniInputStreamParser implements IInputStreamParser
{
    private boolean needTrimKey;
    private boolean needTrimValue;
    private boolean needTrimFragment;
    private boolean needBoundSymbol;
    private String boundSymbol;
    private String commentSymbol;
    private Map<String, String> data;
    private static String NULLLINE_EXP;
    private static String SECTION_EXP;
    private static String KEYVALUE_EXP;
    
    public IniInputStreamParser() {
        this.needTrimKey = true;
        this.needTrimValue = true;
        this.needTrimFragment = false;
        this.needBoundSymbol = false;
        this.boundSymbol = "/";
        this.commentSymbol = "#";
        this.data = new HashMap<String, String>();
    }
    
    private static void debug(final String message) {
    }
    
    public Map<String, String> getData() {
        return this.data;
    }
    
    public Properties getProperties(final String section) {
        final Properties p = new Properties();
        for (String key : this.data.keySet()) {
            if (!key.startsWith(section + ".")) {
                continue;
            }
            final String value = this.data.get(key);
            key = key.substring(section.length() + 1);
            p.put(key, value);
        }
        return p;
    }
    
    public String getProperty(final String key) {
        return this.data.get(key);
    }
    
    public String getProperty(final String section, final String key) {
        return this.data.get(section + "." + key);
    }
    
    public void parse(final InputStream iniFile) {
        this.data.clear();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(iniFile));
            String l = null;
            String section = "";
            String key = null;
            while ((l = in.readLine()) != null) {
                final Line line = new Line(l);
                if (Type.SECTION.equals(line.type)) {
                    section = line.value + ".";
                }
                else {
                    if (!Type.DATA.equals(line.type)) {
                        continue;
                    }
                    key = section + line.key;
                    String value = line.value;
                    if (line.hasBoundSymbol) {
                        while ((l = in.readLine()) != null) {
                            String v = l;
                            final int idx = v.lastIndexOf(this.boundSymbol);
                            if (idx == -1) {
                                value += (this.needTrimFragment ? v.trim() : v);
                                debug("Bound : [" + v + "]");
                                break;
                            }
                            v = v.substring(0, idx);
                            value += (this.needTrimFragment ? v.trim() : v);
                            debug("Bound : [" + v + "]");
                        }
                    }
                    debug("--> {" + key + "} : {" + value + "}");
                    this.data.put(key, value);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public void parser() {
    }
    
    @Override
    public Map<String, String> parser(final InputStream is) {
        if (is != null) {
            this.setNeedBoundSymbol(true);
            this.setNeedTrimFragment(true);
            this.parse(is);
            return this.getData();
        }
        return new HashMap<String, String>();
    }
    
    public void setBoundSymbol(final String boundSymbol) {
        this.boundSymbol = boundSymbol;
    }
    
    public void setCommentSymbol(final String commentSymbol) {
        this.commentSymbol = commentSymbol;
    }
    
    public void setNeedBoundSymbol(final boolean needBoundSymbol) {
        this.needBoundSymbol = needBoundSymbol;
    }
    
    public void setNeedTrimFragment(final boolean needTrimFragment) {
        this.needTrimFragment = needTrimFragment;
    }
    
    public void setNeedTrimKey(final boolean needTrimKey) {
        this.needTrimKey = needTrimKey;
    }
    
    public void setNeedTrimValue(final boolean needTrimValue) {
        this.needTrimValue = needTrimValue;
    }
    
    static {
        IniInputStreamParser.NULLLINE_EXP = "^\\s*$";
        IniInputStreamParser.SECTION_EXP = "^\\s*\\[([\\w\\s_]+)\\]\\s*$";
        IniInputStreamParser.KEYVALUE_EXP = "^([\\w\\s_]+)=([\\w\\s_]+)(.*)$";
    }
    
    private class Line
    {
        Type type;
        String key;
        String value;
        boolean hasBoundSymbol;
        
        public Line(final String data) {
            this.type = Type.ERROR;
            if (data == null || data.matches(IniInputStreamParser.NULLLINE_EXP)) {
                this.type = Type.NULL;
                debug("Null Line");
                return;
            }
            if (data.trim().startsWith(IniInputStreamParser.this.commentSymbol)) {
                this.type = Type.COMMENT;
                debug("Comment: " + data.trim().substring(1));
                return;
            }
            Pattern p = Pattern.compile(IniInputStreamParser.SECTION_EXP);
            Matcher m = p.matcher(data);
            if (m.matches()) {
                this.type = Type.SECTION;
                this.value = (IniInputStreamParser.this.needTrimKey ? m.group(1).trim() : m.group(1));
                debug("Section: " + this.value);
                return;
            }
            p = Pattern.compile(IniInputStreamParser.KEYVALUE_EXP);
            m = p.matcher(data);
            if (m.matches()) {
                this.type = Type.DATA;
                this.key = (IniInputStreamParser.this.needTrimKey ? m.group(1).trim() : m.group(1));
                this.value = (IniInputStreamParser.this.needTrimValue ? m.group(2).trim() : m.group(2));
                if (IniInputStreamParser.this.needBoundSymbol) {
                    final String suffix = m.group(3);
                    if (suffix != null && suffix.trim().length() > 0 && suffix.trim().equals(IniInputStreamParser.this.boundSymbol)) {
                        this.hasBoundSymbol = true;
                        this.value = (IniInputStreamParser.this.needTrimFragment ? m.group(2).trim() : m.group(2));
                    }
                }
                debug("DATA: {" + this.key + ":" + this.value + "} , bound next?" + this.hasBoundSymbol);
            }
        }
    }
    
    enum Type
    {
        ERROR, 
        NULL, 
        COMMENT, 
        SECTION, 
        DATA, 
        VALUE;
    }
}
