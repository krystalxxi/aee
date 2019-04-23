// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.session;

import java.util.Date;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.JobSession;

@CVSID("$Id: SimpleJobSession.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SimpleJobSession extends JobSession
{
    private Object o;
    
    public SimpleJobSession() {
        this.o = null;
    }
    
    @Override
    public Object getPackagedObject() {
        return this.o;
    }
    
    @Override
    public void packageObject(final Object o) {
        this.o = o;
    }
    
    @Override
    public String toString() {
        return "[SimpleJobSession] id=[" + this.getSessionId() + "],createTime=[" + new Date(this.getCreateTime()) + "],object =[" + this.o + "]";
    }
}
