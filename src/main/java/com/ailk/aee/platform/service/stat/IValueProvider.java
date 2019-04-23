// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IValueProvider.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IValueProvider
{
    int getIndex();
    
    String getName();
    
    long getValue();
    
    long getValueAndReset();
}
