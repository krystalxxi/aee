// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.apache.poi.ss.formula.functions.T;

@CVSID("$Id: EnumConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class EnumConverter implements IStringObjectConverter
{
    private Class clazz;
    
    private EnumConverter() {
    }
    
    public EnumConverter(final Class c) {
        this.clazz = c;
    }
    
    @Override
    public boolean canWrapFromString(final String s) {
        if (this.clazz.isEnum()) {
            try {
                final Object o = Enum.valueOf(this.clazz, s);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public Object wrapFromString(final String s) {
        if (this.clazz.isEnum()) {
            try {
                final Object o = Enum.valueOf(this.clazz, s);
                return o;
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
