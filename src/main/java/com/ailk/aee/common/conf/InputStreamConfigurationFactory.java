// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import com.ailk.aee.common.conf.util.XMLInputStreamParser;
import com.ailk.aee.common.conf.util.PropInputStreamParser;
import com.ailk.aee.common.conf.util.IniInputStreamParser;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: InputStreamConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class InputStreamConfigurationFactory extends ReloadMapConfigurationFactory
{
    private InputStream is;
    protected String parseType;
    public static final String INPUTSTREAM_PARSETYPE_XML = "com.ailk.common.conf.parsetype.XML";
    public static final String INPUTSTREAM_PARSETYPE_INI = "com.ailk.common.conf.parsetype.ini";
    public static final String INPUTSTREAM_PARSETYPE_PROP = "com.ailk.common.conf.parsetype.prop";
    
    public InputStreamConfigurationFactory() {
        this.is = null;
        this.parseType = "";
    }
    
    @Override
    public String getFactoryName() {
        return super.getFactoryName();
    }
    
    public String getParseType() {
        return this.parseType;
    }
    
    @Override
    public void initConfMap() {
        if (this.is != null) {
            this.conf.putAll(this.parseInputStream(this.is, this.parseType));
        }
    }
    
    public Map<String, String> parseInputStream(final InputStream is, final String parseType) {
        final Map<String, String> ret = new HashMap<String, String>();
        this.setParseType(parseType);
        try {
            if (this.parseType.equals("com.ailk.common.conf.parsetype.ini")) {
                ret.putAll(new IniInputStreamParser().parser(is));
            }
            else if (this.parseType.equals("com.ailk.common.conf.parsetype.prop")) {
                ret.putAll(new PropInputStreamParser().parser(is));
            }
            else if (this.parseType.equals("com.ailk.common.conf.parsetype.XML")) {
                try {
                    ret.putAll(new XMLInputStreamParser().parser(is));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return ret;
    }
    
    public void setInputStream(final InputStream is) {
        this.is = is;
    }
    
    public void setParseType(final String parseType) {
        this.parseType = parseType;
    }
}
