// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.job;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IExtractor.java 11039 2013-06-13 01:44:38Z xiezl $")
public interface IExtractor
{
    ETLJob getJob();
    
    void setJob(final ETLJob p0);
    
    boolean hasNext();
    
    IBusinessObject next();
    
    void preprocess(final IBusinessObject p0);
    
    void error(final IBusinessObject p0);
    
    void finish(final IBusinessObject p0);
    
    void onJobEnd() throws Exception;
    
    void onJobStart() throws Exception;
}
