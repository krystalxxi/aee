// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.t;

import com.ailk.aee.etl.job.IBusinessObject;
import com.ailk.aee.etl.job.ETLJob;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.ITransformer;

@CVSID("$Id: AbstractTransformer.java 11039 2013-06-13 01:44:38Z xiezl $")
public class AbstractTransformer implements ITransformer
{
    private ETLJob job;
    
    @Override
    public ETLJob getJob() {
        return this.job;
    }
    
    @Override
    public void onJobEnd() throws Exception {
    }
    
    @Override
    public void onJobStart() throws Exception {
    }
    
    @Override
    public void setJob(final ETLJob j) {
        this.job = j;
    }
    
    @Override
    public IBusinessObject trans(final IBusinessObject o) {
        return o;
    }
}
