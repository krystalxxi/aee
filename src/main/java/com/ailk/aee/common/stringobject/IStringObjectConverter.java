// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IStringObjectConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IStringObjectConverter
{
    boolean canWrapFromString(final String p0);
    
    Object wrapFromString(final String p0);
}
