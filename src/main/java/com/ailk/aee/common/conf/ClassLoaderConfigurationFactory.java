// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ClassLoaderConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ClassLoaderConfigurationFactory extends IntelligentSearchInputStreamConfigurationFactory
{
    private String fileName;
    
    public ClassLoaderConfigurationFactory(final String fileName, final String parseType) {
        this.fileName = null;
        this.fileName = fileName;
        this.setParseType(parseType);
    }
    
    @Override
    public String getFactoryName() {
        if (this.fileName == null) {
            return "ClassLoaderConfiguration From File:" + this.fileName + " but not found,Configuration ignore";
        }
        return "ClassLoaderConfiguration From File:" + this.fileName;
    }
    
    @Override
    public InputStream search() {
        return ClassLoaderConfigurationFactory.class.getClassLoader().getResourceAsStream(this.fileName);
    }
}
