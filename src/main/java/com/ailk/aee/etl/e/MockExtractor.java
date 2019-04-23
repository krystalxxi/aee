// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MockExtractor.java 11039 2013-06-13 01:44:38Z xiezl $")
public class MockExtractor extends AbstractExtractor
{
    private int testcount;
    private Map<String, String> testmap;
    
    public MockExtractor() {
        this.testcount = 10;
        this.testmap = new HashMap<String, String>();
    }
    
    @Override
    public boolean hasNextObject() {
        return this.testcount > 0;
    }
    
    @Override
    public IBusinessObject nextObject() {
        this.testmap.put("THIS_COUNT", Integer.toString(this.testcount));
        --this.testcount;
        final MapRecord mr = new MapRecord();
        mr.putAll(this.testmap);
        System.out.println("InitObject" + mr.toString());
        return mr;
    }
    
    @Override
    public String toString() {
        return this.testmap.toString();
    }
}
