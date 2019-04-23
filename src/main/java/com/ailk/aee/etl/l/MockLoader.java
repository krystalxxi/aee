// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import com.ailk.aee.etl.job.IBusinessObject;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MockLoader.java 11039 2013-06-13 01:44:38Z xiezl $")
public class MockLoader extends AbstractLoader
{
    @Override
    public boolean loadObject(final IBusinessObject o) {
        System.out.println("Loader :\n " + o);
        return true;
    }
}
