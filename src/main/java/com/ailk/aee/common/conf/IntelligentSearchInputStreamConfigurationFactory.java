// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.io.InputStream;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IntelligentSearchInputStreamConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public abstract class IntelligentSearchInputStreamConfigurationFactory extends InputStreamConfigurationFactory
{
    @Override
    public String getFactoryName() {
        return super.getFactoryName();
    }
    
    @Override
    public void initConfMap() {
        final InputStream is = this.search();
        if (is != null) {
            this.conf.putAll(this.parseInputStream(is, this.parseType));
        }
    }
    
    public abstract InputStream search();
}
