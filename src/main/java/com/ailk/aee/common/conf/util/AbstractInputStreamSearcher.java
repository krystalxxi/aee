// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AbstractInputStreamSearcher.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class AbstractInputStreamSearcher
{
    private String loc;
    
    public AbstractInputStreamSearcher() {
        this.loc = "";
    }
    
    public String getLocation() {
        return this.loc;
    }
    
    public abstract InputStream search(final String p0);
    
    public void setLocation(final String s) {
        this.loc = s;
    }
}
