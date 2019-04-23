// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ICommandAdapter.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface ICommandAdapter
{
    void doCommand(final CmdEnv p0, final String[] p1);
}
