// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SystemFileScriptConsole.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SystemFileScriptConsole extends ScriptConsole
{
    private File parentFolder;
    
    public SystemFileScriptConsole(final File f) {
        this.parentFolder = f;
        this.load();
    }
    
    private InputStream getInputStream(final File f) {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        }
        catch (FileNotFoundException ex) {}
        return is;
    }
    
    protected File getSpecFile(final File u, final String s) {
        final File f = new File(u.getPath() + File.separator + s);
        if (f.exists() && f.canRead()) {
            return f;
        }
        return null;
    }
    
    private void load() {
        File f = this.getSpecFile(this.parentFolder, "__config.ini");
        if (f != null) {
            super.loadConfig(this.getInputStream(f));
        }
        else {
            super.config.setTitle(this.parentFolder.getName());
        }
        f = this.getSpecFile(this.parentFolder, "__onstart.script");
        if (f != null) {
            super.onStartString = this.readScriptInInputStream(this.getInputStream(f));
        }
        f = this.getSpecFile(this.parentFolder, "__onquit.script");
        if (f != null) {
            super.onQuitString = this.readScriptInInputStream(this.getInputStream(f));
        }
        f = this.getSpecFile(this.parentFolder, "__login.script");
        if (f != null) {
            super.onLoginString = this.readScriptInInputStream(this.getInputStream(f));
            super.setNeedLogin(true);
        }
        else {
            super.setNeedLogin(false);
        }
        final File[] arr$;
        final File[] fs = arr$ = this.parentFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File arg0) {
                return (!arg0.getName().startsWith("__") || arg0.getName().endsWith(".script")) && (arg0.isFile() && arg0.canRead());
            }
        });
        for (final File s : arr$) {
            this.addScriptCommand(s.getName(), this.getInputStream(s));
        }
    }
}
