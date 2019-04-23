// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id")
public interface ILoader
{
    ETLJob getJob();
    
    void setJob(final ETLJob p0);
    
    boolean load(final IBusinessObject p0);
    
    void onJobStart() throws Exception;
    
    void onJobEnd() throws Exception;
}
