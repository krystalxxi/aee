// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sql;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: NoThisVariableException.java 60270 2013-11-03 14:48:37Z tangxy $")
public class NoThisVariableException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public NoThisVariableException() {
    }
    
    public NoThisVariableException(final PrepareByNameProcedure ps, final String string) {
        super(string + "\n" + "@" + ps.toString());
    }
    
    public NoThisVariableException(final PreparedByNameStatement ps, final String string) {
        super(string + "\n" + "@" + ps.toString());
    }
}
