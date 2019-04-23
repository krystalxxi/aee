// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StringObjectUtil.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StringObjectUtil
{
    HashMap<Class<?>, IStringObjectConverter> cache;
    private static StringObjectUtil utils;
    
    public static boolean canWrap2Class(final String s, final Class<?> clazz) {
        if (clazz.isEnum()) {
            final EnumConverter ec = new EnumConverter(clazz);
            return ec.canWrapFromString(s);
        }
        final IStringObjectConverter converter = StringObjectUtil.utils.getConverter(clazz);
        return converter != null && converter.canWrapFromString(s);
    }
    
    public static <T> T wrapClass(final String s, final Class<T> clazz) {
        if (clazz.isEnum()) {
            final EnumConverter ec = new EnumConverter(clazz);
            return (T)ec.wrapFromString(s);
        }
        final IStringObjectConverter converter = StringObjectUtil.utils.getConverter(clazz);
        if (converter != null) {
            return (T)converter.wrapFromString(s);
        }
        return null;
    }
    
    public StringObjectUtil() {
        (this.cache = new HashMap<Class<?>, IStringObjectConverter>()).put(Character.TYPE, ConverterCollections.charConverter);
        this.cache.put(Character.class, ConverterCollections.charArrayConverter);
        this.cache.put(Byte.TYPE, ConverterCollections.byteConverter);
        this.cache.put(Byte.class, ConverterCollections.byteConverter);
        this.cache.put(Integer.TYPE, ConverterCollections.intergerConverter);
        this.cache.put(Integer.class, ConverterCollections.intergerConverter);
        this.cache.put(Short.TYPE, ConverterCollections.shortConverter);
        this.cache.put(Short.class, ConverterCollections.shortConverter);
        this.cache.put(Long.TYPE, ConverterCollections.longConverter);
        this.cache.put(Long.class, ConverterCollections.longConverter);
        this.cache.put(Float.TYPE, ConverterCollections.floatConverter);
        this.cache.put(Float.class, ConverterCollections.floatConverter);
        this.cache.put(Double.TYPE, ConverterCollections.doubleConverter);
        this.cache.put(Double.class, ConverterCollections.doubleConverter);
        this.cache.put(Boolean.class, ConverterCollections.booleanConverter);
        this.cache.put(Boolean.TYPE, ConverterCollections.booleanConverter);
        this.cache.put(String.class, ConverterCollections.stringConverter);
        this.cache.put(String[].class, ConverterCollections.stringArrayConverter);
        this.cache.put(char[].class, ConverterCollections.charArrayConverter);
        this.cache.put(Character[].class, ConverterCollections.CharArrayConverter);
        this.cache.put(byte[].class, ConverterCollections.byteArrayConverter);
        this.cache.put(Byte[].class, ConverterCollections.ByteArrayConverter);
        this.cache.put(int[].class, ConverterCollections.intArrayConverter);
        this.cache.put(Integer[].class, ConverterCollections.IntArrayConverter);
        this.cache.put(short[].class, ConverterCollections.shortArrayConverter);
        this.cache.put(Short[].class, ConverterCollections.ShortArrayConverter);
        this.cache.put(long[].class, ConverterCollections.longArrayConverter);
        this.cache.put(Long[].class, ConverterCollections.LongArrayConverter);
        this.cache.put(float[].class, ConverterCollections.floatArrayConverter);
        this.cache.put(Float[].class, ConverterCollections.FloatArrayConverter);
        this.cache.put(double[].class, ConverterCollections.doubleArrayConverter);
        this.cache.put(Double[].class, ConverterCollections.DoubleArrayConverter);
        this.cache.put(boolean[].class, ConverterCollections.booleanArrayConverter);
        this.cache.put(Boolean[].class, ConverterCollections.BooleanArrayConverter);
        this.cache.put(List.class, new StringListConverter());
        this.cache.put(Map.class, new StringMapConverter());
    }
    
    public IStringObjectConverter getConverter(final Class<?> clazz) {
        return this.cache.get(clazz);
    }
    
    static {
        StringObjectUtil.utils = new StringObjectUtil();
    }
}
