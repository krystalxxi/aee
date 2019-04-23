// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.util.Properties;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.Enumeration;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.HashSet;
import java.io.UnsupportedEncodingException;
import java.util.jar.JarFile;
import java.net.URLDecoder;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import com.ailk.aee.common.util.StringUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Hashtable;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ScriptConsole.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class ScriptConsole implements IConsole
{
    protected Hashtable<String, ScriptCommand> allCmds;
    protected CmdConfig config;
    protected boolean needLogin;
    protected String onLoginString;
    protected String onQuitString;
    protected String onStartString;
    protected String evaluatorString;
    
    public ScriptConsole() {
        this.allCmds = new Hashtable<String, ScriptCommand>();
        this.config = new CmdConfig();
        this.needLogin = false;
        this.onLoginString = "";
        this.onQuitString = "";
        this.onStartString = "";
        this.evaluatorString = "";
    }
    
    public void addScriptCommand(final String name, final InputStream is) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        final ScriptCommand sc = new ScriptCommand();
        sc.name = name;
        sc.detailHelp = "";
        try {
            final StringBuffer sb = new StringBuffer();
            for (String s = br.readLine(); s != null; s = br.readLine()) {
                s.trim();
                if (s.startsWith("//*//")) {
                    sc.help = StringUtils.substringAfter(s, "//*//");
                }
                else if (s.startsWith("//***")) {
                    sc.detailHelp = sc.detailHelp + StringUtils.substringAfter(s, "//***") + "\r\n";
                }
                else {
                    sb.append(s);
                    sb.append("\r\n");
                }
            }
            sc.script = sb.toString();
        }
        catch (IOException ex) {}
        this.allCmds.put(name, sc);
    }
    
    public void doScript(final String s, final CmdEnv env) {
        final Object gs = this.getEvaluator();
        this.setVariable(gs, "env", env);
        this.evaluate(gs, s);
    }
    
    public void doScript(final String s, final CmdEnv env, final String[] args) {
        final Object gs = this.getEvaluator();
        this.setVariable(gs, "env", env);
        this.setVariable(gs, "args", args);
        this.evaluate(gs, s);
    }
    
    public void doScriptCommand(final CmdEnv env, final String[] args) {
        final ScriptCommand sc = this.allCmds.get(args[0]);
        if (sc != null) {
            this.doScript(sc.script, env, args);
        }
    }
    
    private void evaluate(final Object o, final String s) {
        try {
            final Class<?> c = Class.forName("groovy.lang.GroovyShell");
            final Method m = c.getMethod("evaluate", String.class);
            m.invoke(o, s);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SecurityException e2) {
            e2.printStackTrace();
        }
        catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
        catch (IllegalArgumentException e4) {
            e4.printStackTrace();
        }
        catch (IllegalAccessException e5) {
            e5.printStackTrace();
        }
        catch (InvocationTargetException e6) {
            e6.printStackTrace();
        }
    }
    
    @Override
    public List<Command> getCommand() {
        final ArrayList<Command> al = new ArrayList<Command>();
        for (final ScriptCommand sc : this.allCmds.values()) {
            al.add(new Command(sc.name, "", sc.help, sc.detailHelp, new ICommandAdapter() {
                @Override
                public void doCommand(final CmdEnv env, final String[] args) {
                    ScriptConsole.this.doScriptCommand(env, args);
                }
            }));
        }
        return al;
    }
    
    @Override
    public CmdConfig getConfig() {
        return this.config;
    }
    
    public Object getEvaluator() {
        try {
            final Class<?> c = Class.forName("groovy.lang.GroovyShell");
            return c.newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        catch (ClassNotFoundException e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    String[] getResourceListing(final Class<?> clazz, final String path) throws URISyntaxException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            return new File(dirURL.toURI()).list();
        }
        if (dirURL == null) {
            final String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }
        if (dirURL.getProtocol().equals("jar")) {
            final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {}
            catch (IOException ex) {}
            final Enumeration<JarEntry> entries = jar.entries();
            final Set<String> result = new HashSet<String>();
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path)) {
                    String entry = name.substring(path.length());
                    final int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }
        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }
    
    @Override
    public boolean isNeedLogin() {
        return this.needLogin;
    }
    
    public void loadConfig(final InputStream is) {
        final Properties prop = new Properties();
        try {
            prop.load(is);
            this.config.fromProperties(prop);
        }
        catch (IOException ex) {}
    }
    
    @Override
    public void login(final CmdEnv env) {
        if (!this.onLoginString.equals("")) {
            this.doScript(this.onLoginString, env);
        }
    }
    
    @Override
    public void onQuit(final CmdEnv env) {
        if (!this.onQuitString.equals("")) {
            this.doScript(this.onQuitString, env);
        }
    }
    
    @Override
    public void onStart(final CmdEnv env, final String[] args) {
        if (!this.onStartString.equals("")) {
            this.doScript(this.onStartString, env, args);
        }
    }
    
    public String readScriptInInputStream(final InputStream is) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            final StringBuffer sb = new StringBuffer();
            for (String s = br.readLine(); s != null; s = br.readLine()) {
                s.trim();
                if (!s.startsWith("//*//")) {
                    if (!s.startsWith("//***")) {
                        sb.append(s);
                        sb.append("\r\n");
                    }
                }
            }
            return sb.toString();
        }
        catch (IOException ioe) {
            return "";
        }
    }
    
    public void setNeedLogin(final boolean b) {
        this.needLogin = b;
    }
    
    private void setVariable(final Object o, final String n, final Object v) {
        try {
            final Class<?> c = Class.forName("groovy.lang.GroovyShell");
            final Method m = c.getMethod("setVariable", String.class, Object.class);
            m.invoke(o, n, v);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SecurityException e2) {
            e2.printStackTrace();
        }
        catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
        catch (IllegalArgumentException e4) {
            e4.printStackTrace();
        }
        catch (IllegalAccessException e5) {
            e5.printStackTrace();
        }
        catch (InvocationTargetException e6) {
            e6.printStackTrace();
        }
    }
    
    class ScriptCommand
    {
        String detailHelp;
        String help;
        String name;
        String script;
    }
}
