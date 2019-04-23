// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DateFormatUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DateFormatUtils
{
    public static final FastDateFormat SIMPLE_DATETIME_FORMAT;
    public static final FastDateFormat SIMPLE_DATETIME_FORMAT_2;
    public static final FastDateFormat SIMPLE_DATETIME_FORMAT_3;
    private static final TimeZone UTC_TIME_ZONE;
    public static final FastDateFormat ISO_DATETIME_FORMAT;
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT;
    public static final FastDateFormat ISO_DATE_FORMAT;
    public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT;
    public static final FastDateFormat ISO_TIME_FORMAT;
    public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT;
    public static final FastDateFormat ISO_TIME_NO_T_FORMAT;
    public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT;
    public static final FastDateFormat SMTP_DATETIME_FORMAT;
    
    public static String format(final Calendar calendar, final String pattern) {
        return format(calendar, pattern, null, null);
    }
    
    public static String format(final Calendar calendar, final String pattern, final Locale locale) {
        return format(calendar, pattern, null, locale);
    }
    
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone) {
        return format(calendar, pattern, timeZone, null);
    }
    
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(calendar);
    }
    
    public static String format(final Date date, final String pattern) {
        return format(date, pattern, null, null);
    }
    
    public static String format(final Date date, final String pattern, final Locale locale) {
        return format(date, pattern, null, locale);
    }
    
    public static String format(final Date date, final String pattern, final TimeZone timeZone) {
        return format(date, pattern, timeZone, null);
    }
    
    public static String format(final Date date, final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(date);
    }
    
    public static String format(final long millis, final String pattern) {
        return format(new Date(millis), pattern, null, null);
    }
    
    public static String format(final long millis, final String pattern, final Locale locale) {
        return format(new Date(millis), pattern, null, locale);
    }
    
    public static String format(final long millis, final String pattern, final TimeZone timeZone) {
        return format(new Date(millis), pattern, timeZone, null);
    }
    
    public static String format(final long millis, final String pattern, final TimeZone timeZone, final Locale locale) {
        return format(new Date(millis), pattern, timeZone, locale);
    }
    
    public static String formatUTC(final Date date, final String pattern) {
        return format(date, pattern, DateFormatUtils.UTC_TIME_ZONE, null);
    }
    
    public static String formatUTC(final Date date, final String pattern, final Locale locale) {
        return format(date, pattern, DateFormatUtils.UTC_TIME_ZONE, locale);
    }
    
    public static String formatUTC(final long millis, final String pattern) {
        return format(new Date(millis), pattern, DateFormatUtils.UTC_TIME_ZONE, null);
    }
    
    public static String formatUTC(final long millis, final String pattern, final Locale locale) {
        return format(new Date(millis), pattern, DateFormatUtils.UTC_TIME_ZONE, locale);
    }
    
    static {
        SIMPLE_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        SIMPLE_DATETIME_FORMAT_2 = FastDateFormat.getInstance("yyyyMMddHHmmss");
        SIMPLE_DATETIME_FORMAT_3 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.S");
        UTC_TIME_ZONE = TimeZone.getTimeZone("GMT");
        ISO_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
        ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");
        ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
        ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");
        ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");
        ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");
        ISO_TIME_NO_T_FORMAT = FastDateFormat.getInstance("HH:mm:ss");
        ISO_TIME_NO_T_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");
        SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    }
}
