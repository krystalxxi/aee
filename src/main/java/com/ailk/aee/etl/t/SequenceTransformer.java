// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.t;

import com.ailk.aee.etl.job.IBusinessObject;
import java.util.Iterator;
import java.util.TreeMap;
import com.ailk.aee.etl.job.ITransformer;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SequenceTransformer.java 11039 2013-06-13 01:44:38Z xiezl $")
public class SequenceTransformer extends AbstractTransformer
{
    private Map<String, ITransformer> transformers;
    
    public SequenceTransformer() {
        this.transformers = new TreeMap<String, ITransformer>();
    }
    
    public Map<String, ITransformer> getTransformers() {
        return this.transformers;
    }
    
    @Override
    public void onJobEnd() throws Exception {
        for (final ITransformer f : this.transformers.values()) {
            f.onJobEnd();
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        for (final ITransformer f : this.transformers.values()) {
            f.onJobStart();
        }
    }
    
    public void setTransformers(final Map<String, ITransformer> transformers) {
        this.transformers = transformers;
    }
    
    @Override
    public IBusinessObject trans(final IBusinessObject o2) {
        for (final ITransformer f : this.transformers.values()) {
            f.trans(o2);
        }
        return o2;
    }
}
