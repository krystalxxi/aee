// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.o;

import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.ArrayList;

@CVSID("$Id: ListRecord.java 11039 2013-06-13 01:44:38Z xiezl $")
public class ListRecord extends ArrayList<String> implements IBusinessObject
{
    @Override
    public String getType() {
        return this.getClass().getName();
    }
}
