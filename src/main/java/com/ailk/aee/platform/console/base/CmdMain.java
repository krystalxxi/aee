// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: CmdMain.java 60270 2013-11-03 14:48:37Z tangxy $")
public class CmdMain
{
    public static void main(final String[] args) {
        final CmdMain cm = new CmdMain();
        cm.start(args);
    }
    
    public void start(final String[] args) {
        if (args.length >= 1) {
            final String module = args[0];
            final String[] newargs = new String[args.length - 1];
            for (int i = 1; i < args.length; ++i) {
                newargs[i - 1] = args[i];
            }
            try {
                final Class<?> c = Class.forName(module);
                final Object o = c.newInstance();
                if (IConsole.class.isAssignableFrom(o.getClass())) {
                    final CmdEnv env = new CmdEnv();
                    env.start((IConsole)o, newargs);
                    return;
                }
                System.out.println("---------------------------2");
            }
            catch (Exception e) {
                System.out.println("---------3");
            }
        }
        else {
            System.out.println("Usage: java -cp $CLASS_PATH " + this.getClass().getPackage().getName() + "." + this.getClass().getName() + "  module [args...]");
        }
    }
}
