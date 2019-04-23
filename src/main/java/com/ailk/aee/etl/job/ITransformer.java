// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id")
public interface ITransformer
{
    ETLJob getJob();
    
    void setJob(final ETLJob p0);
    
    void onJobEnd() throws Exception;
    
    void onJobStart() throws Exception;
    
    IBusinessObject trans(final IBusinessObject p0);
}
