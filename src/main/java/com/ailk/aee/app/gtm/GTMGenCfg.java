// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm;

import java.io.File;
import com.ailk.aee.common.util.ClassPathUtils;

public class GTMGenCfg
{
    public static String AEE_HOME;
    
    public static void main(final String[] args) {
        final GTMGenCfg c = new GTMGenCfg();
        c.genCfg();
        System.out.println(System.getProperties().toString());
    }
    
    public void genCfg() {
        this.genJava();
        this.genClassPath();
        this.genJavaProp();
        this.genClass();
    }
    
    private void genClass() {
        this.out("com.ailk.aee.app.gtm.GTMRemoteProxy");
    }
    
    private void genClassPath() {
        this.out("-classpath");
        final String[] ss = ClassPathUtils.getCurrentClassPathList();
        final StringBuffer sb = new StringBuffer();
        for (final String s : ss) {
            sb.append(s);
            sb.append(System.getProperty("path.separator"));
        }
        this.out(sb.toString());
    }
    
    private void genJava() {
        this.out(System.getProperty("sun.boot.library.path") + File.separator + "java");
    }
    
    private void genJavaProp() {
        this.out("-Dwade.server.name=aee_node01_svr01_gtm");
    }
    
    public void out(final String s) {
        System.out.println(s);
    }
    
    static {
        GTMGenCfg.AEE_HOME = "F:\\codes\\hnan\\AILK\\AEE1.1\\";
    }
}
