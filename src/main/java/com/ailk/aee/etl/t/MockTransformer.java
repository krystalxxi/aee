// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.t;

import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MockTransformer.java 11039 2013-06-13 01:44:38Z xiezl $")
public class MockTransformer extends AbstractTransformer
{
    private static Logger log;
    
    @Override
    public void onJobEnd() {
        MockTransformer.log.debug((Object)("Transformer" + this.toString() + " OnJobEnd"));
    }
    
    @Override
    public void onJobStart() {
        MockTransformer.log.debug((Object)("Transformer" + this.toString() + " OnJobStart"));
    }
    
    @Override
    public IBusinessObject trans(final IBusinessObject o) {
        final MapRecord mr = (MapRecord)o;
        mr.put(this.toString(), "HHHHH");
        MockTransformer.log.debug((Object)("Transformer" + this.toString() + " Trans:\n" + o.toString()));
        return o;
    }
    
    static {
        MockTransformer.log = Logger.getLogger((Class)MockTransformer.class);
    }
}
