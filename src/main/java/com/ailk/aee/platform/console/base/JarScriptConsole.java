// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.net.URISyntaxException;
import com.ailk.aee.common.util.StringUtils;
import java.io.InputStream;
import java.io.File;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: JarScriptConsole.java 60270 2013-11-03 14:48:37Z tangxy $")
public class JarScriptConsole extends ScriptConsole
{
    String basepath;
    
    public JarScriptConsole(final String basepath) {
        this.basepath = "";
        this.basepath = "etc" + File.separator + basepath;
        this.load();
    }
    
    private InputStream getInputStream(final InputStream f) {
        return f;
    }
    
    protected InputStream getSpecFile(final String u, final String s) {
        final String p = u + File.separator + s;
        return this.getClass().getClassLoader().getResourceAsStream(p);
    }
    
    private void load() {
        InputStream f = this.getSpecFile(this.basepath, "__config.ini");
        if (f != null) {
            super.loadConfig(this.getInputStream(f));
        }
        else {
            super.config.setTitle(StringUtils.substringAfterLast(this.basepath, File.separator));
        }
        f = this.getSpecFile(this.basepath, "__onstart.script");
        if (f != null) {
            super.onStartString = this.readScriptInInputStream(this.getInputStream(f));
        }
        f = this.getSpecFile(this.basepath, "__onquit.script");
        if (f != null) {
            super.onQuitString = this.readScriptInInputStream(this.getInputStream(f));
        }
        f = this.getSpecFile(this.basepath, "__login.script");
        if (f != null) {
            super.onLoginString = this.readScriptInInputStream(this.getInputStream(f));
            super.setNeedLogin(true);
        }
        else {
            super.setNeedLogin(false);
        }
        String[] fs = null;
        try {
            fs = super.getResourceListing(this.getClass(), this.basepath);
        }
        catch (URISyntaxException ex) {}
        for (final String s : fs) {
            if (!s.startsWith("__") && s.endsWith(".script")) {
                this.addScriptCommand(StringUtils.substringBefore(s, ".script"), this.getClass().getClassLoader().getResourceAsStream(this.basepath + File.separator + s));
            }
        }
    }
}
