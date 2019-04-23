// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConverterCollections.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConverterCollections
{
    public static IStringObjectConverter intergerConverter;
    public static IStringObjectConverter charConverter;
    public static IStringObjectConverter shortConverter;
    public static IStringObjectConverter doubleConverter;
    public static IStringObjectConverter longConverter;
    public static IStringObjectConverter stringConverter;
    public static IStringObjectConverter booleanConverter;
    public static IStringObjectConverter byteConverter;
    public static IStringObjectConverter floatConverter;
    public static IStringObjectConverter dateConverter;
    public static IStringObjectConverter dateArrayConverter;
    public static IStringObjectConverter stringArrayConverter;
    public static IStringObjectConverter IntArrayConverter;
    public static IStringObjectConverter ShortArrayConverter;
    public static IStringObjectConverter CharArrayConverter;
    public static IStringObjectConverter LongArrayConverter;
    public static IStringObjectConverter ByteArrayConverter;
    public static IStringObjectConverter FloatArrayConverter;
    public static IStringObjectConverter DoubleArrayConverter;
    public static IStringObjectConverter BooleanArrayConverter;
    public static IStringObjectConverter intArrayConverter;
    public static IStringObjectConverter shortArrayConverter;
    public static IStringObjectConverter charArrayConverter;
    public static IStringObjectConverter longArrayConverter;
    public static IStringObjectConverter byteArrayConverter;
    public static IStringObjectConverter floatArrayConverter;
    public static IStringObjectConverter doubleArrayConverter;
    public static IStringObjectConverter booleanArrayConverter;
    
    static {
        ConverterCollections.intergerConverter = new IStringObjectConverter() {
            private int NULL = 0;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final int i = Integer.decode(s);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final int i = Integer.decode(s);
                    return i;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.charConverter = new IStringObjectConverter() {
            private char NULL = '\0';
            
            @Override
            public boolean canWrapFromString(final String s) {
                return s != null && !s.equals("");
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                return s.toCharArray()[0];
            }
        };
        ConverterCollections.shortConverter = new IStringObjectConverter() {
            private short NULL = 0;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final short i = Short.valueOf(s);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final short i = Short.valueOf(s);
                    return i;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.doubleConverter = new IStringObjectConverter() {
            private double NULL = 0.0;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final double i = Double.valueOf(s);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final double i = Double.valueOf(s);
                    return i;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.longConverter = new IStringObjectConverter() {
            private long NULL = 0L;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final long i = Long.decode(s);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final long i = Long.decode(s);
                    return i;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.stringConverter = new IStringObjectConverter() {
            @Override
            public boolean canWrapFromString(final String s) {
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                return s;
            }
        };
        ConverterCollections.booleanConverter = new IStringObjectConverter() {
            @Override
            public boolean canWrapFromString(final String s) {
                return s == null || s.length() == 0 || (s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("T") || s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("FALSE") || s.equalsIgnoreCase("F") || s.equalsIgnoreCase("ON") || s.equalsIgnoreCase("OFF") || s.equalsIgnoreCase("NO") || s.equalsIgnoreCase("N")) || StringUtils.isNumeric(s);
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null) {
                    return false;
                }
                if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("N") || s.equalsIgnoreCase("off") || "0".equals(s) || "".equals(s)) {
                    return false;
                }
                if (s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("T") || s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("on")) {
                    return true;
                }
                return false;
            }
        };
        ConverterCollections.byteConverter = new IStringObjectConverter() {
            private byte NULL = 0;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final byte b = Byte.decode(s);
                }
                catch (NumberFormatException e) {
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final byte b = Byte.decode(s);
                    return b;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.floatConverter = new IStringObjectConverter() {
            private float NULL = 0.0f;
            
            @Override
            public boolean canWrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return false;
                }
                try {
                    final float f = Float.valueOf(s);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s == null || s.equals("")) {
                    return this.NULL;
                }
                try {
                    final float f = Float.valueOf(s);
                    return f;
                }
                catch (NumberFormatException e) {
                    return this.NULL;
                }
            }
        };
        ConverterCollections.dateConverter = new IStringObjectConverter() {
            @Override
            public boolean canWrapFromString(final String s) {
                if (s.length() == 10 || s.length() == 19) {
                    if (s.length() == 10) {
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            final Date d = sdf.parse(s);
                            return sdf.format(d).equals(s);
                        }
                        catch (ParseException e) {
                            return false;
                        }
                    }
                    if (s.length() == 19) {
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            final Date d = sdf.parse(s);
                            return sdf.format(d).equals(s);
                        }
                        catch (ParseException e) {
                            return false;
                        }
                    }
                }
                return ConverterCollections.longConverter.canWrapFromString(s);
            }
            
            @Override
            public Object wrapFromString(final String s) {
                if (s.length() == 10 || s.length() == 19) {
                    if (s.length() == 10) {
                        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            final Date d = sdf.parse(s);
                            if (sdf.format(d).equals(s)) {
                                return d;
                            }
                            return d;
                        }
                        catch (ParseException e) {
                            return null;
                        }
                    }
                    if (s.length() != 19) {
                        return null;
                    }
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        final Date d = sdf.parse(s);
                        if (sdf.format(d).equals(s)) {
                            return d;
                        }
                        return d;
                    }
                    catch (ParseException e) {
                        return null;
                    }
                }
                if (ConverterCollections.longConverter.canWrapFromString(s)) {
                    return new Date((long)ConverterCollections.longConverter.wrapFromString(s));
                }
                return null;
            }
        };
        ConverterCollections.dateArrayConverter = new ArrayTypeConverter<Object>(Date.class);
        ConverterCollections.stringArrayConverter = new StringArrayConverter();
        ConverterCollections.IntArrayConverter = new ArrayTypeConverter<Object>(Integer.class);
        ConverterCollections.ShortArrayConverter = new ArrayTypeConverter<Object>(Short.class);
        ConverterCollections.CharArrayConverter = new ArrayTypeConverter<Object>(Character.class);
        ConverterCollections.LongArrayConverter = new ArrayTypeConverter<Object>(Long.class);
        ConverterCollections.ByteArrayConverter = new ArrayTypeConverter<Object>(Byte.class);
        ConverterCollections.FloatArrayConverter = new ArrayTypeConverter<Object>(Float.class);
        ConverterCollections.DoubleArrayConverter = new ArrayTypeConverter<Object>(Double.class);
        ConverterCollections.BooleanArrayConverter = new ArrayTypeConverter<Object>(Boolean.class);
        ConverterCollections.intArrayConverter = new ArrayTypeConverter<Object>(Integer.TYPE);
        ConverterCollections.shortArrayConverter = new ArrayTypeConverter<Object>(Short.TYPE);
        ConverterCollections.charArrayConverter = new ArrayTypeConverter<Object>(Character.TYPE);
        ConverterCollections.longArrayConverter = new ArrayTypeConverter<Object>(Long.TYPE);
        ConverterCollections.byteArrayConverter = new ArrayTypeConverter<Object>(Byte.TYPE);
        ConverterCollections.floatArrayConverter = new ArrayTypeConverter<Object>(Float.TYPE);
        ConverterCollections.doubleArrayConverter = new ArrayTypeConverter<Object>(Double.TYPE);
        ConverterCollections.booleanArrayConverter = new ArrayTypeConverter<Object>(Boolean.TYPE);
    }
}
