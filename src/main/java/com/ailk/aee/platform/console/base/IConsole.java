// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IConsole.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IConsole
{
    List<Command> getCommand();
    
    CmdConfig getConfig();
    
    boolean isNeedLogin();
    
    void login(final CmdEnv p0);
    
    void onQuit(final CmdEnv p0);
    
    void onStart(final CmdEnv p0, final String[] p1);
}
