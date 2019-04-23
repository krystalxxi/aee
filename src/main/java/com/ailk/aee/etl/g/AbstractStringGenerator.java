// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.g;

import com.ailk.aee.etl.job.ETLJob;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.IStringGenerator;

@CVSID("$Id: AbstractStringGenerator.java 11039 2013-06-13 01:44:38Z xiezl $")
public class AbstractStringGenerator implements IStringGenerator
{
    @Override
    public String genString(final ETLJob job) {
        return "";
    }
}
