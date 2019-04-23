// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.text.Format;

@CVSID("$Id: FormatCache.java 60270 2013-11-03 14:48:37Z tangxy $")
abstract class FormatCache<F extends Format>
{
    static final int NONE = -1;
    private final ConcurrentMap<MultipartKey, F> cInstanceCache;
    private final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache;
    
    FormatCache() {
        this.cInstanceCache = new ConcurrentHashMap<MultipartKey, F>(7);
        this.cDateTimeInstanceCache = new ConcurrentHashMap<MultipartKey, String>(7);
    }
    
    protected abstract F createInstance(final String p0, final TimeZone p1, final Locale p2);
    
    public F getDateTimeInstance(final Integer dateStyle, final Integer timeStyle, final TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final MultipartKey key = new MultipartKey(new Object[] { dateStyle, timeStyle, locale });
        String pattern = this.cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            try {
                DateFormat formatter;
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle, locale);
                }
                else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle, locale);
                }
                else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
                }
                pattern = ((SimpleDateFormat)formatter).toPattern();
                final String previous = this.cDateTimeInstanceCache.putIfAbsent(key, pattern);
                if (previous != null) {
                    pattern = previous;
                }
            }
            catch (ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return this.getInstance(pattern, timeZone, locale);
    }
    
    public F getInstance() {
        return this.getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
    }
    
    public F getInstance(final String pattern, TimeZone timeZone, Locale locale) {
        if (pattern == null) {
            throw new NullPointerException("pattern must not be null");
        }
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final MultipartKey key = new MultipartKey(new Object[] { pattern, timeZone, locale });
        F format = this.cInstanceCache.get(key);
        if (format == null) {
            format = this.createInstance(pattern, timeZone, locale);
            final F previousValue = this.cInstanceCache.putIfAbsent(key, format);
            if (previousValue != null) {
                format = previousValue;
            }
        }
        return format;
    }
    
    private static class MultipartKey
    {
        private final Object[] keys;
        private int hashCode;
        
        public MultipartKey(final Object... keys) {
            this.keys = keys;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof MultipartKey && Arrays.equals(this.keys, ((MultipartKey)obj).keys));
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int rc = 0;
                for (final Object key : this.keys) {
                    if (key != null) {
                        rc = rc * 7 + key.hashCode();
                    }
                }
                this.hashCode = rc;
            }
            return this.hashCode;
        }
    }
}
