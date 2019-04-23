// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.o;

import java.util.Map;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.HashMap;

@CVSID("$Id: MapRecord.java 11039 2013-06-13 01:44:38Z xiezl $")
public class MapRecord extends HashMap<String, String> implements IBusinessObject
{
    private static final long serialVersionUID = 7932520548213895465L;
    
    @Override
    public String getType() {
        return this.getClass().getCanonicalName();
    }
    
    @Override
    public String toString() {
        return MapTools.mapToString((Map)this);
    }
}
