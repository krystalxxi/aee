// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.t;

import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.Iterator;
import com.ailk.aee.common.conf.FileConfigurationFactory;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ValueChangeTransformer.java 11039 2013-06-13 01:44:38Z xiezl $")
public class ValueChangeTransformer extends AbstractTransformer
{
    protected Map<String, String> values;
    protected String valueChangeConfigsFiles;
    
    public ValueChangeTransformer() {
        this.values = new HashMap<String, String>();
        this.valueChangeConfigsFiles = "";
    }
    
    public String getValueChangeConfigsFiles() {
        return this.valueChangeConfigsFiles;
    }
    
    public Map<String, String> getValues() {
        return this.values;
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.valueChangeConfigsFiles == null || this.valueChangeConfigsFiles.equals("")) {
            final Map<String, String> mx = (Map<String, String>)FileConfigurationFactory.parseFile(this.valueChangeConfigsFiles);
            for (final Map.Entry<String, String> p : mx.entrySet()) {
                if (!this.values.containsKey(p.getKey())) {
                    this.values.put(p.getKey(), p.getValue());
                }
            }
        }
    }
    
    public void setValueChangeConfigsFiles(final String valueChangeConfigsFiles) {
        this.valueChangeConfigsFiles = valueChangeConfigsFiles;
    }
    
    public void setValues(final Map<String, String> values) {
        this.values = values;
    }
    
    @Override
    public IBusinessObject trans(final IBusinessObject o) {
        if (o instanceof MapRecord) {
            if (this.values.size() > 0) {
                final Map<String, String> m = (MapRecord)o;
                for (final Map.Entry<String, String> p : this.values.entrySet()) {
                    final String key = p.getKey();
                    String value = p.getValue();
                    if (value.startsWith("@")) {
                        final String vref = value.substring(1);
                        if (m.containsKey(vref)) {
                            value = m.get(vref);
                        }
                        else {
                            value = "";
                        }
                        m.put(key, value);
                    }
                    else {
                        m.put(key, value);
                    }
                }
            }
            return o;
        }
        return o;
    }
}
