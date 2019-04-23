// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: PropInputStreamParser.java 60270 2013-11-03 14:48:37Z tangxy $")
public class PropInputStreamParser implements IInputStreamParser
{
    @Override
    public Map<String, String> parser(final InputStream is) {
        if (is != null) {
            final Properties prop = new Properties();
            try {
                prop.load(is);
                final Map<String, String> sm = new HashMap<String, String>();
                for (final Object o : (prop).keySet()) {
                    sm.put(o.toString(), prop.get(o).toString());
                }
                return sm;
            }
            catch (IOException ex) {}
        }
        return new HashMap<String, String>();
    }
}
