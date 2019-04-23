// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MVELException.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MVELException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public MVELException(final Exception e) {
        super(e);
    }
}
