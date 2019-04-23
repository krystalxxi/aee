// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sp;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AbstractServiceProvider.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class AbstractServiceProvider implements IServiceProvider
{
    @Override
    public void build(final Map<String, String> arg) throws Exception {
    }
    
    @Override
    public abstract Object getService(final String p0);
}
