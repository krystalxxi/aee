// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: CmdConfig.java 60270 2013-11-03 14:48:37Z tangxy $")
public class CmdConfig
{
    private String prefix;
    public static final int NO_PROMPT2 = 0;
    public static final int PROMPT2_BY_ENDCHAR = 1;
    public static final int PROMPT2_BY_ESCAPECHAR = 2;
    private String copyRight;
    private boolean echo;
    private char endChar;
    private char escapeChar;
    private boolean ignoreCase;
    private String prompt;
    private String prompt2;
    private int prompt2Type;
    private int screenSize;
    private boolean timeStat;
    private String title;
    private boolean verbose;
    private String version;
    
    public CmdConfig() {
        this.prefix = "";
        this.copyRight = "";
        this.echo = true;
        this.endChar = ';';
        this.escapeChar = '\\';
        this.ignoreCase = true;
        this.prompt = "Orz: ";
        this.prompt2 = "  >  ";
        this.prompt2Type = 0;
        this.screenSize = 80;
        this.timeStat = true;
        this.title = "";
        this.verbose = false;
        this.version = "";
    }
    
    public String doSet(final String n, final String v) {
        final Class<?> clazz = CmdConfig.class;
        final Method[] arr$;
        final Method[] ms = arr$ = clazz.getMethods();
        for (final Method m : arr$) {
            final String mname = m.getName();
            if (mname.equalsIgnoreCase("set" + n.trim())) {
                final String type = m.getParameterTypes()[0].getName();
                try {
                    if (type.equals("java.lang.String")) {
                        m.invoke(this, v);
                    }
                    else if (type.equals("int")) {
                        m.invoke(this, Integer.parseInt(v));
                    }
                    else if (type.equals("boolean")) {
                        if (v.equalsIgnoreCase("Y") || v.equalsIgnoreCase("YES") || v.equalsIgnoreCase("true") || v.equalsIgnoreCase("t") || v.equalsIgnoreCase("ok") || v.equalsIgnoreCase("on")) {
                            m.invoke(this, true);
                        }
                        else {
                            if (!v.equalsIgnoreCase("N") && !v.equalsIgnoreCase("NO") && !v.equalsIgnoreCase("false") && !v.equalsIgnoreCase("f") && !v.equalsIgnoreCase("off")) {
                                return "nothing changed!";
                            }
                            m.invoke(this, false);
                        }
                    }
                }
                catch (IllegalArgumentException e) {}
                catch (IllegalAccessException e2) {}
                catch (InvocationTargetException ex) {}
                return m.getName().substring(3) + " changed to " + v;
            }
        }
        return "nothing changed!";
    }
    
    public void fromProperties(final Properties prop) {
        for (final Map.Entry<Object, Object> s : prop.entrySet()) {
            this.doSet(s.getKey().toString(), s.getValue().toString());
        }
    }
    
    public void fromProperties2(final Properties prop) {
        if (prop.containsKey("title")) {
            this.setTitle(prop.getProperty("title"));
        }
        if (prop.containsKey("version")) {
            this.setVersion(prop.getProperty("version"));
        }
        if (prop.containsKey("copright")) {
            this.setCopyRight(prop.getProperty("copright"));
        }
        else {
            this.setCopyRight("@2011 Asiainfo-Linkage");
        }
        if (prop.containsKey("prompt2type")) {
            this.setPrompt2Type(Integer.parseInt(prop.getProperty("prompt2type")));
        }
        if (prop.containsKey("prompt")) {
            this.setPrompt(prop.getProperty("prompt"));
        }
        if (prop.containsKey("prompt2")) {
            this.setPrompt2(prop.getProperty("prompt2"));
        }
        if (prop.containsKey("endchar")) {
            this.setEndChar(prop.getProperty("endchar").charAt(0));
        }
        if (prop.containsKey("escapechar")) {
            this.setEscapeChar(prop.getProperty("escapechar").charAt(0));
        }
        if (prop.containsKey("verbose")) {
            this.setVerbose(this.getBoolValue(prop.getProperty("verbose")));
        }
        if (prop.containsKey("ignoreCase")) {
            this.setIgnoreCase(this.getBoolValue(prop.getProperty("ignoreCase")));
        }
        if (prop.containsKey("timeStat")) {
            this.setTimeStat(this.getBoolValue(prop.getProperty("timeStat")));
        }
        if (prop.containsKey("screenSize")) {
            this.setScreenSize(Integer.parseInt(prop.getProperty("screenSize")));
        }
    }
    
    private boolean getBoolValue(final String s) {
        return s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("T") || s.equals("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("on") || ((s.equalsIgnoreCase("N") || s.equalsIgnoreCase("F") || s.equals("false") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("off")) && false);
    }
    
    public String getCopyRight() {
        return this.copyRight;
    }
    
    public char getEndChar() {
        return this.endChar;
    }
    
    public char getEscapeChar() {
        return this.escapeChar;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getPrompt() {
        return this.prompt;
    }
    
    public String getPrompt2() {
        return this.prompt2;
    }
    
    public int getPrompt2Type() {
        return this.prompt2Type;
    }
    
    public int getScreenSize() {
        return this.screenSize;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean isEcho() {
        return this.echo;
    }
    
    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }
    
    public boolean isTimeStat() {
        return this.timeStat;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public void setCopyRight(final String copyRight) {
        this.copyRight = copyRight;
    }
    
    public void setEcho(final boolean echo) {
        this.echo = echo;
    }
    
    public void setEndChar(final char endChar) {
        this.endChar = endChar;
    }
    
    public void setEscapeChar(final char escapeChar) {
        this.escapeChar = escapeChar;
    }
    
    public void setIgnoreCase(final boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    public void setPrefix(final String str) {
        this.prefix = str;
    }
    
    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }
    
    public void setPrompt2(final String prompt2) {
        this.prompt2 = prompt2;
    }
    
    public void setPrompt2Type(final int prompt2Type) {
        this.prompt2Type = prompt2Type;
    }
    
    public void setScreenSize(final int screenSize) {
        this.screenSize = screenSize;
    }
    
    public void setTimeStat(final boolean timeStat) {
        this.timeStat = timeStat;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
}
