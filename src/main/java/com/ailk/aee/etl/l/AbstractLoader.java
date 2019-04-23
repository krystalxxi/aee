// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import java.util.Iterator;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.TreeMap;
import com.ailk.aee.etl.job.ETLJob;
import com.ailk.aee.etl.job.ITransformer;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.ILoader;

@CVSID("$Id: AbstractLoader.java 11039 2013-06-13 01:44:38Z xiezl $")
public abstract class AbstractLoader implements ILoader
{
    protected Map<String, ITransformer> transformers;
    private ETLJob job;
    
    public AbstractLoader() {
        this.transformers = new TreeMap<String, ITransformer>();
    }
    
    @Override
    public ETLJob getJob() {
        return this.job;
    }
    
    @Override
    public boolean load(final IBusinessObject o2) {
        IBusinessObject o3 = o2;
        if (this.transformers.size() > 0) {
            for (final ITransformer t : this.transformers.values()) {
                o3 = t.trans(o3);
            }
        }
        return this.loadObject(o3);
    }
    
    public abstract boolean loadObject(final IBusinessObject p0);
    
    @Override
    public void onJobEnd() throws Exception {
        for (final ITransformer t : this.transformers.values()) {
            t.onJobEnd();
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        for (final ITransformer t : this.transformers.values()) {
            t.onJobStart();
        }
    }
    
    @Override
    public void setJob(final ETLJob j) {
        this.job = j;
    }
}
