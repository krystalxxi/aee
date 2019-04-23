// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import java.util.Iterator;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import com.ailk.aee.etl.job.ITransformer;
import java.util.Map;
import com.ailk.aee.etl.job.ETLJob;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.IExtractor;

@CVSID("$Id: AbstractExtractor.java 11906 2013-07-25 07:57:21Z xiezl $")
public abstract class AbstractExtractor implements IExtractor
{
    private ETLJob job;
    protected Map<String, ITransformer> transformers;
    private AtomicLong failedNum;
    private AtomicLong successNum;
    
    public AbstractExtractor() {
        this.job = null;
        this.transformers = new TreeMap<String, ITransformer>();
        this.failedNum = null;
        this.successNum = null;
    }
    
    @Override
    public ETLJob getJob() {
        return this.job;
    }
    
    @Override
    public boolean hasNext() {
        return this.hasNextObject();
    }
    
    public abstract boolean hasNextObject();
    
    @Override
    public IBusinessObject next() {
        IBusinessObject bo = this.nextObject();
        if (this.transformers.size() > 0) {
            for (final ITransformer t : this.transformers.values()) {
                bo = t.trans(bo);
            }
        }
        return bo;
    }
    
    public abstract IBusinessObject nextObject();
    
    @Override
    public void preprocess(final IBusinessObject o) {
    }
    
    @Override
    public void error(final IBusinessObject o) {
    }
    
    @Override
    public void finish(final IBusinessObject o) {
    }
    
    @Override
    public void onJobEnd() throws Exception {
        for (final ITransformer t : this.transformers.values()) {
            t.onJobEnd();
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        this.failedNum = new AtomicLong(0L);
        this.successNum = new AtomicLong(0L);
        for (final ITransformer t : this.transformers.values()) {
            t.onJobStart();
        }
    }
    
    @Override
    public void setJob(final ETLJob j) {
        this.job = j;
    }
    
    public AtomicLong getFailedNum() {
        return this.failedNum;
    }
    
    public AtomicLong getSuccessNum() {
        return this.successNum;
    }
}
