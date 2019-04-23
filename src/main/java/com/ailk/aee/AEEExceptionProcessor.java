// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee;

import com.ailk.aee.log.LogUtils;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEExceptionProcessor.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEExceptionProcessor
{
    public static void process(final Exception t) {
        LogUtils.logError(ExceptionUtils.getExceptionStack(t));
    }
    
    public static void process(final Throwable t) {
        LogUtils.logError(ExceptionUtils.getExceptionStack(new Exception(t)));
    }
}
