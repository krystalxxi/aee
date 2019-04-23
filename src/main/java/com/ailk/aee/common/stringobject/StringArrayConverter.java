// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.ArrayList;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StringArrayConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StringArrayConverter implements IStringObjectConverter
{
    @Override
    public boolean canWrapFromString(final String s) {
        return new ArrayTypeConverter(String.class).canWrapFromString(s);
    }
    
    private String[] getItems(final String s) {
        return StringUtils.split(StringUtils.substringBetween(s, "{", "}"), ", ");
    }
    
    @Override
    public Object wrapFromString(final String s) {
        if (!s.startsWith("{") || !s.endsWith("}")) {
            return null;
        }
        final String[] ss = this.getItems(s);
        if (ss == null) {
            return new Object[0];
        }
        final ArrayList<String> os = new ArrayList<String>();
        for (int i = 0; i < ss.length; ++i) {
            os.add(StringObjectUtil.wrapClass(ss[i], String.class));
        }
        return os.toArray(new String[0]);
    }
}
