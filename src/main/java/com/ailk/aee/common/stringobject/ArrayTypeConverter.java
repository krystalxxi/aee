// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.ArrayList;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ArrayTypeConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ArrayTypeConverter<T> implements IStringObjectConverter
{
    private Class<T> clazz;
    
    public ArrayTypeConverter(final Class clazz) {
        this.clazz = (Class<T>)clazz;
    }
    
    @Override
    public boolean canWrapFromString(final String s) {
        if ((!s.startsWith("{") || !s.endsWith("}")) && (!s.startsWith("[") || !s.endsWith("]"))) {
            return false;
        }
        final String[] ss = this.getItems(s);
        if (ss == null) {
            return true;
        }
        for (final String si : ss) {
            if (!StringObjectUtil.canWrap2Class(si, this.clazz)) {
                return false;
            }
        }
        return true;
    }
    
    private String[] getItems(final String s) {
        if (s.startsWith("{")) {
            return StringUtils.split(StringUtils.substringBetween(s, "{", "}"), ", ");
        }
        return StringUtils.split(StringUtils.substringBetween(s, "[", "]"), ", ");
    }
    
    @Override
    public Object wrapFromString(final String s) {
        if ((!s.startsWith("{") || !s.endsWith("}")) && (!s.startsWith("[") || !s.endsWith("]"))) {
            return null;
        }
        final String[] ss = this.getItems(s);
        if (ss == null) {
            return new Object[0];
        }
        final ArrayList<Object> os = new ArrayList<Object>();
        for (int i = 0; i < ss.length; ++i) {
            os.add(StringObjectUtil.wrapClass(ss[i], this.clazz));
        }
        if (this.clazz.equals(Integer.TYPE)) {
            final int[] ret = new int[os.size()];
            for (int j = 0; j < ret.length; ++j) {
                ret[j] = (int)os.get(j);
            }
            return ret;
        }
        if (this.clazz.equals(Byte.TYPE)) {
            final byte[] ret2 = new byte[os.size()];
            for (int j = 0; j < ret2.length; ++j) {
                ret2[j] = (byte)os.get(j);
            }
            return ret2;
        }
        if (this.clazz.equals(Short.TYPE)) {
            final short[] ret3 = new short[os.size()];
            for (int j = 0; j < ret3.length; ++j) {
                ret3[j] = (short)os.get(j);
            }
            return ret3;
        }
        if (this.clazz.equals(Long.TYPE)) {
            final long[] ret4 = new long[os.size()];
            for (int j = 0; j < ret4.length; ++j) {
                ret4[j] = (long)os.get(j);
            }
            return ret4;
        }
        if (this.clazz.equals(Float.TYPE)) {
            final float[] ret5 = new float[os.size()];
            for (int j = 0; j < ret5.length; ++j) {
                ret5[j] = (float)os.get(j);
            }
            return ret5;
        }
        if (this.clazz.equals(Double.TYPE)) {
            final double[] ret6 = new double[os.size()];
            for (int j = 0; j < ret6.length; ++j) {
                ret6[j] = (double)os.get(j);
            }
            return ret6;
        }
        if (this.clazz.equals(Character.TYPE)) {
            final char[] ret7 = new char[os.size()];
            for (int j = 0; j < ret7.length; ++j) {
                ret7[j] = (char)os.get(j);
            }
            return ret7;
        }
        if (this.clazz.equals(Boolean.TYPE)) {
            final boolean[] ret8 = new boolean[os.size()];
            for (int j = 0; j < ret8.length; ++j) {
                ret8[j] = (boolean)os.get(j);
            }
            return ret8;
        }
        return os.toArray();
    }
}
