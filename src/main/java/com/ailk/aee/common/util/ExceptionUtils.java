// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ExceptionUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ExceptionUtils
{
    public static String getExceptionStack(final Exception e) {
        final StringBuffer sb = new StringBuffer();
        sb.append(e.getClass().getCanonicalName() + " ");
        sb.append(e.getMessage());
        sb.append("\n");
        final StackTraceElement[] arr$;
        final StackTraceElement[] es = arr$ = e.getStackTrace();
        for (final StackTraceElement em : arr$) {
            sb.append(em.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public void unhandle(final Exception e) {
        e.printStackTrace();
    }
}
