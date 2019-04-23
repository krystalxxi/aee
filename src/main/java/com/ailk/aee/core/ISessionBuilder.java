// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.core;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ISessionBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface ISessionBuilder
{
    IJobSession createSession(final Worker p0, final Job p1, final Object p2);
}
