// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.t;

import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.o.ListRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.ArrayList;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ListToMapTransformer.java 11039 2013-06-13 01:44:38Z xiezl $")
public class ListToMapTransformer extends ValueChangeTransformer
{
    private List<String> names;
    private String defaultName;
    
    public ListToMapTransformer() {
        this.names = new ArrayList<String>();
        this.defaultName = "FIELD";
    }
    
    public String getDefaultName() {
        return this.defaultName;
    }
    
    public List<String> getNames() {
        return this.names;
    }
    
    public void setDefaultName(final String defaultName) {
        this.defaultName = defaultName;
    }
    
    public void setNames(final List<String> names) {
        this.names = names;
    }
    
    @Override
    public IBusinessObject trans(final IBusinessObject o) {
        if (o instanceof ListRecord) {
            final List<String> l = (ListRecord)o;
            final MapRecord mr = new MapRecord();
            for (int i = 0; i < l.size(); ++i) {
                if (this.names.size() > i) {
                    mr.put(this.names.get(i), l.get(i));
                }
                else {
                    mr.put(this.defaultName + "i", l.get(i));
                }
            }
            return super.trans(mr);
        }
        if (o instanceof MapRecord) {
            return super.trans(o);
        }
        return o;
    }
}
