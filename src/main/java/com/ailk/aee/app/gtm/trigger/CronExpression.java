// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.trigger;

import java.util.HashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.util.Locale;
import java.util.TreeSet;
import java.util.TimeZone;
import java.util.Map;
import java.io.Serializable;

public class CronExpression implements Serializable, Cloneable
{
    private static final long serialVersionUID = 12423409423L;
    protected static final int SECOND = 0;
    protected static final int MINUTE = 1;
    protected static final int HOUR = 2;
    protected static final int DAY_OF_MONTH = 3;
    protected static final int MONTH = 4;
    protected static final int DAY_OF_WEEK = 5;
    protected static final int YEAR = 6;
    protected static final int ALL_SPEC_INT = 99;
    protected static final int NO_SPEC_INT = 98;
    protected static final Integer ALL_SPEC;
    protected static final Integer NO_SPEC;
    protected static Map monthMap;
    protected static Map dayMap;
    private String cronExpression;
    private TimeZone timeZone;
    protected transient TreeSet seconds;
    protected transient TreeSet minutes;
    protected transient TreeSet hours;
    protected transient TreeSet daysOfMonth;
    protected transient TreeSet months;
    protected transient TreeSet daysOfWeek;
    protected transient TreeSet years;
    protected transient boolean lastdayOfWeek;
    protected transient int nthdayOfWeek;
    protected transient boolean lastdayOfMonth;
    protected transient boolean nearestWeekday;
    protected transient boolean calendardayOfWeek;
    protected transient boolean calendardayOfMonth;
    protected transient boolean expressionParsed;
    
    public CronExpression(final String cronExpression) throws ParseException {
        this.cronExpression = null;
        this.timeZone = null;
        this.lastdayOfWeek = false;
        this.nthdayOfWeek = 0;
        this.lastdayOfMonth = false;
        this.nearestWeekday = false;
        this.calendardayOfWeek = false;
        this.calendardayOfMonth = false;
        this.expressionParsed = false;
        if (cronExpression == null) {
            throw new IllegalArgumentException("cronExpression cannot be null");
        }
        this.cronExpression = cronExpression;
        this.buildExpression(cronExpression.toUpperCase(Locale.US));
    }
    
    public boolean isSatisfiedBy(final Date date) {
        final Calendar testDateCal = Calendar.getInstance();
        testDateCal.setTime(date);
        testDateCal.set(14, 0);
        final Date originalDate = testDateCal.getTime();
        testDateCal.add(13, -1);
        return this.getTimeAfter(testDateCal.getTime()).equals(originalDate);
    }
    
    public Date getNextValidTimeAfter(final Date date) {
        return this.getTimeAfter(date);
    }
    
    public TimeZone getTimeZone() {
        if (this.timeZone == null) {
            this.timeZone = TimeZone.getDefault();
        }
        return this.timeZone;
    }
    
    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }
    
    @Override
    public String toString() {
        return this.cronExpression;
    }
    
    public static boolean isValidExpression(final String cronExpression) {
        try {
            new CronExpression(cronExpression);
        }
        catch (ParseException pe) {
            return false;
        }
        return true;
    }
    
    protected void buildExpression(final String expression) throws ParseException {
        this.expressionParsed = true;
        try {
            if (this.seconds == null) {
                this.seconds = new TreeSet();
            }
            if (this.minutes == null) {
                this.minutes = new TreeSet();
            }
            if (this.hours == null) {
                this.hours = new TreeSet();
            }
            if (this.daysOfMonth == null) {
                this.daysOfMonth = new TreeSet();
            }
            if (this.months == null) {
                this.months = new TreeSet();
            }
            if (this.daysOfWeek == null) {
                this.daysOfWeek = new TreeSet();
            }
            if (this.years == null) {
                this.years = new TreeSet();
            }
            int exprOn = 0;
            for (StringTokenizer exprsTok = new StringTokenizer(expression, " \t", false); exprsTok.hasMoreTokens() && exprOn <= 6; ++exprOn) {
                final String expr = exprsTok.nextToken().trim();
                final StringTokenizer vTok = new StringTokenizer(expr, ",");
                while (vTok.hasMoreTokens()) {
                    final String v = vTok.nextToken();
                    this.storeExpressionVals(0, v, exprOn);
                }
            }
            if (exprOn <= 5) {
                throw new ParseException("Unexpected end of expression.", expression.length());
            }
            if (exprOn <= 6) {
                this.storeExpressionVals(0, "*", 6);
            }
        }
        catch (ParseException pe) {
            throw pe;
        }
        catch (Exception e) {
            throw new ParseException("Illegal cron expression format (" + e.toString() + ")", 0);
        }
    }
    
    protected int storeExpressionVals(final int pos, final String s, final int type) throws ParseException {
        int incr = 0;
        int i = this.skipWhiteSpace(pos, s);
        if (i >= s.length()) {
            return i;
        }
        char c = s.charAt(i);
        if (c >= 'A' && c <= 'Z' && !s.equals("L") && !s.equals("LW")) {
            String sub = s.substring(i, i + 3);
            int sval = -1;
            int eval = -1;
            Label_0537: {
                if (type == 4) {
                    sval = this.getMonthNumber(sub) + 1;
                    if (sval < 0) {
                        throw new ParseException("Invalid Month value: '" + sub + "'", i);
                    }
                    if (s.length() > i + 3) {
                        c = s.charAt(i + 3);
                        if (c == '-') {
                            i += 4;
                            sub = s.substring(i, i + 3);
                            eval = this.getMonthNumber(sub) + 1;
                            if (eval < 0) {
                                throw new ParseException("Invalid Month value: '" + sub + "'", i);
                            }
                        }
                    }
                }
                else {
                    if (type != 5) {
                        throw new ParseException("Illegal characters for this position: '" + sub + "'", i);
                    }
                    sval = this.getDayOfWeekNumber(sub);
                    if (sval < 0) {
                        throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
                    }
                    if (s.length() > i + 3) {
                        c = s.charAt(i + 3);
                        if (c == '-') {
                            i += 4;
                            sub = s.substring(i, i + 3);
                            eval = this.getDayOfWeekNumber(sub);
                            if (eval < 0) {
                                throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
                            }
                            if (sval > eval) {
                                throw new ParseException("Invalid Day-of-Week sequence: " + sval + " > " + eval, i);
                            }
                        }
                        else {
                            if (c == '#') {
                                try {
                                    i += 4;
                                    this.nthdayOfWeek = Integer.parseInt(s.substring(i));
                                    if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
                                        throw new Exception();
                                    }
                                    break Label_0537;
                                }
                                catch (Exception e) {
                                    throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
                                }
                            }
                            if (c == 'L') {
                                this.lastdayOfWeek = true;
                                ++i;
                            }
                        }
                    }
                }
            }
            if (eval != -1) {
                incr = 1;
            }
            this.addToSet(sval, eval, incr, type);
            return i + 3;
        }
        if (c == '?') {
            if (++i + 1 < s.length() && s.charAt(i) != ' ' && s.charAt(i + 1) != '\t') {
                throw new ParseException("Illegal character after '?': " + s.charAt(i), i);
            }
            if (type != 5 && type != 3) {
                throw new ParseException("'?' can only be specfied for Day-of-Month or Day-of-Week.", i);
            }
            if (type == 5 && !this.lastdayOfMonth) {
                final int val = (int)this.daysOfMonth.last();
                if (val == 98) {
                    throw new ParseException("'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", i);
                }
            }
            this.addToSet(98, -1, 0, type);
            return i;
        }
        else if (c == '*' || c == '/') {
            if (c == '*' && i + 1 >= s.length()) {
                this.addToSet(99, -1, incr, type);
                return i + 1;
            }
            if (c == '/' && (i + 1 >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t')) {
                throw new ParseException("'/' must be followed by an integer.", i);
            }
            if (c == '*') {
                ++i;
            }
            c = s.charAt(i);
            if (c == '/') {
                if (++i >= s.length()) {
                    throw new ParseException("Unexpected end of string.", i);
                }
                incr = this.getNumericValue(s, i);
                ++i;
                if (incr > 10) {
                    ++i;
                }
                if (incr > 59 && (type == 0 || type == 1)) {
                    throw new ParseException("Increment > 60 : " + incr, i);
                }
                if (incr > 23 && type == 2) {
                    throw new ParseException("Increment > 24 : " + incr, i);
                }
                if (incr > 31 && type == 3) {
                    throw new ParseException("Increment > 31 : " + incr, i);
                }
                if (incr > 7 && type == 5) {
                    throw new ParseException("Increment > 7 : " + incr, i);
                }
                if (incr > 12 && type == 4) {
                    throw new ParseException("Increment > 12 : " + incr, i);
                }
            }
            else {
                incr = 1;
            }
            this.addToSet(99, -1, incr, type);
            return i;
        }
        else {
            if (c == 'L') {
                ++i;
                if (type == 3) {
                    this.lastdayOfMonth = true;
                }
                if (type == 5) {
                    this.addToSet(7, 7, 0, type);
                }
                if (type == 3 && s.length() > i) {
                    c = s.charAt(i);
                    if (c == 'W') {
                        this.nearestWeekday = true;
                        ++i;
                    }
                }
                return i;
            }
            if (c < '0' || c > '9') {
                throw new ParseException("Unexpected character: " + c, i);
            }
            int val = Integer.parseInt(String.valueOf(c));
            if (++i >= s.length()) {
                this.addToSet(val, -1, -1, type);
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                final ValueSet vs = this.getValue(val, s, i);
                val = vs.value;
                i = vs.pos;
            }
            i = this.checkNext(i, s, val, type);
            return i;
        }
    }
    
    protected int checkNext(final int pos, final String s, final int val, final int type) throws ParseException {
        int end = -1;
        int i = pos;
        if (i >= s.length()) {
            this.addToSet(val, end, -1, type);
            return i;
        }
        char c = s.charAt(pos);
        if (c == 'L') {
            if (type == 5) {
                this.lastdayOfWeek = true;
                final TreeSet set = this.getSet(type);
                set.add(new Integer(val));
                return ++i;
            }
            throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
        }
        else if (c == 'W') {
            if (type == 3) {
                this.nearestWeekday = true;
                final TreeSet set = this.getSet(type);
                set.add(new Integer(val));
                return ++i;
            }
            throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
        }
        else if (c == '#') {
            if (type != 5) {
                throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
            }
            ++i;
            try {
                this.nthdayOfWeek = Integer.parseInt(s.substring(i));
                if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
                    throw new Exception();
                }
            }
            catch (Exception e) {
                throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
            }
            final TreeSet set = this.getSet(type);
            set.add(new Integer(val));
            return ++i;
        }
        else {
            if (c == 'C') {
                if (type == 5) {
                    this.calendardayOfWeek = true;
                }
                else {
                    if (type != 3) {
                        throw new ParseException("'C' option is not valid here. (pos=" + i + ")", i);
                    }
                    this.calendardayOfMonth = true;
                }
                final TreeSet set = this.getSet(type);
                set.add(new Integer(val));
                return ++i;
            }
            if (c == '-') {
                ++i;
                c = s.charAt(i);
                final int v = end = Integer.parseInt(String.valueOf(c));
                if (++i >= s.length()) {
                    this.addToSet(val, end, 1, type);
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    final ValueSet vs = this.getValue(v, s, i);
                    final int v2 = end = vs.value;
                    i = vs.pos;
                }
                if (i >= s.length() || (c = s.charAt(i)) != '/') {
                    this.addToSet(val, end, 1, type);
                    return i;
                }
                ++i;
                c = s.charAt(i);
                final int v3 = Integer.parseInt(String.valueOf(c));
                if (++i >= s.length()) {
                    this.addToSet(val, end, v3, type);
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    final ValueSet vs2 = this.getValue(v3, s, i);
                    final int v4 = vs2.value;
                    this.addToSet(val, end, v4, type);
                    i = vs2.pos;
                    return i;
                }
                this.addToSet(val, end, v3, type);
                return i;
            }
            else {
                if (c != '/') {
                    this.addToSet(val, end, 0, type);
                    return ++i;
                }
                ++i;
                c = s.charAt(i);
                final int v5 = Integer.parseInt(String.valueOf(c));
                if (++i >= s.length()) {
                    this.addToSet(val, end, v5, type);
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    final ValueSet vs = this.getValue(v5, s, i);
                    final int v6 = vs.value;
                    this.addToSet(val, end, v6, type);
                    i = vs.pos;
                    return i;
                }
                throw new ParseException("Unexpected character '" + c + "' after '/'", i);
            }
        }
    }
    
    public String getCronExpression() {
        return this.cronExpression;
    }
    
    public String getExpressionSummary() {
        final StringBuffer buf = new StringBuffer();
        buf.append("seconds: ");
        buf.append(this.getExpressionSetSummary(this.seconds));
        buf.append("\n");
        buf.append("minutes: ");
        buf.append(this.getExpressionSetSummary(this.minutes));
        buf.append("\n");
        buf.append("hours: ");
        buf.append(this.getExpressionSetSummary(this.hours));
        buf.append("\n");
        buf.append("daysOfMonth: ");
        buf.append(this.getExpressionSetSummary(this.daysOfMonth));
        buf.append("\n");
        buf.append("months: ");
        buf.append(this.getExpressionSetSummary(this.months));
        buf.append("\n");
        buf.append("daysOfWeek: ");
        buf.append(this.getExpressionSetSummary(this.daysOfWeek));
        buf.append("\n");
        buf.append("lastdayOfWeek: ");
        buf.append(this.lastdayOfWeek);
        buf.append("\n");
        buf.append("nearestWeekday: ");
        buf.append(this.nearestWeekday);
        buf.append("\n");
        buf.append("NthDayOfWeek: ");
        buf.append(this.nthdayOfWeek);
        buf.append("\n");
        buf.append("lastdayOfMonth: ");
        buf.append(this.lastdayOfMonth);
        buf.append("\n");
        buf.append("calendardayOfWeek: ");
        buf.append(this.calendardayOfWeek);
        buf.append("\n");
        buf.append("calendardayOfMonth: ");
        buf.append(this.calendardayOfMonth);
        buf.append("\n");
        buf.append("years: ");
        buf.append(this.getExpressionSetSummary(this.years));
        buf.append("\n");
        return buf.toString();
    }
    
    protected String getExpressionSetSummary(final Set set) {
        if (set.contains(CronExpression.NO_SPEC)) {
            return "?";
        }
        if (set.contains(CronExpression.ALL_SPEC)) {
            return "*";
        }
        final StringBuffer buf = new StringBuffer();
        final Iterator itr = set.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            final Integer iVal = (Integer) itr.next();
            final String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }
        return buf.toString();
    }
    
    protected String getExpressionSetSummary(final ArrayList list) {
        if (list.contains(CronExpression.NO_SPEC)) {
            return "?";
        }
        if (list.contains(CronExpression.ALL_SPEC)) {
            return "*";
        }
        final StringBuffer buf = new StringBuffer();
        final Iterator itr = list.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            final Integer iVal = (Integer)itr.next();
            final String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }
        return buf.toString();
    }
    
    protected int skipWhiteSpace(int i, final String s) {
        while (i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t')) {
            ++i;
        }
        return i;
    }
    
    protected int findNextWhiteSpace(int i, final String s) {
        while (i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t')) {
            ++i;
        }
        return i;
    }
    
    protected void addToSet(final int val, final int end, int incr, final int type) throws ParseException {
        final TreeSet set = this.getSet(type);
        if (type == 0 || type == 1) {
            if ((val < 0 || val > 59 || end > 59) && val != 99) {
                throw new ParseException("Minute and Second values must be between 0 and 59", -1);
            }
        }
        else if (type == 2) {
            if ((val < 0 || val > 23 || end > 23) && val != 99) {
                throw new ParseException("Hour values must be between 0 and 23", -1);
            }
        }
        else if (type == 3) {
            if ((val < 1 || val > 31 || end > 31) && val != 99 && val != 98) {
                throw new ParseException("Day of month values must be between 1 and 31", -1);
            }
        }
        else if (type == 4) {
            if ((val < 1 || val > 12 || end > 12) && val != 99) {
                throw new ParseException("Month values must be between 1 and 12", -1);
            }
        }
        else if (type == 5 && (val == 0 || val > 7 || end > 7) && val != 99 && val != 98) {
            throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
        }
        if ((incr == 0 || incr == -1) && val != 99) {
            if (val != -1) {
                set.add(new Integer(val));
            }
            else {
                set.add(CronExpression.NO_SPEC);
            }
            return;
        }
        int startAt = val;
        int stopAt = end;
        if (val == 99 && incr <= 0) {
            incr = 1;
            set.add(CronExpression.ALL_SPEC);
        }
        if (type == 0 || type == 1) {
            if (stopAt == -1) {
                stopAt = 59;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 0;
            }
        }
        else if (type == 2) {
            if (stopAt == -1) {
                stopAt = 23;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 0;
            }
        }
        else if (type == 3) {
            if (stopAt == -1) {
                stopAt = 31;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 1;
            }
        }
        else if (type == 4) {
            if (stopAt == -1) {
                stopAt = 12;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 1;
            }
        }
        else if (type == 5) {
            if (stopAt == -1) {
                stopAt = 7;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 1;
            }
        }
        else if (type == 6) {
            if (stopAt == -1) {
                stopAt = 2099;
            }
            if (startAt == -1 || startAt == 99) {
                startAt = 1970;
            }
        }
        for (int i = startAt; i <= stopAt; i += incr) {
            set.add(new Integer(i));
        }
    }
    
    protected TreeSet getSet(final int type) {
        switch (type) {
            case 0: {
                return this.seconds;
            }
            case 1: {
                return this.minutes;
            }
            case 2: {
                return this.hours;
            }
            case 3: {
                return this.daysOfMonth;
            }
            case 4: {
                return this.months;
            }
            case 5: {
                return this.daysOfWeek;
            }
            case 6: {
                return this.years;
            }
            default: {
                return null;
            }
        }
    }
    
    protected ValueSet getValue(final int v, final String s, int i) {
        char c = s.charAt(i);
        String s2 = String.valueOf(v);
        while (c >= '0' && c <= '9') {
            s2 += c;
            if (++i >= s.length()) {
                break;
            }
            c = s.charAt(i);
        }
        final ValueSet val = new ValueSet();
        if (i < s.length()) {
            val.pos = i;
        }
        else {
            val.pos = i + 1;
        }
        val.value = Integer.parseInt(s2);
        return val;
    }
    
    protected int getNumericValue(final String s, final int i) {
        final int endOfVal = this.findNextWhiteSpace(i, s);
        final String val = s.substring(i, endOfVal);
        return Integer.parseInt(val);
    }
    
    protected int getMonthNumber(final String s) {
        final Integer integer = (Integer)CronExpression.monthMap.get(s);
        if (integer == null) {
            return -1;
        }
        return integer;
    }
    
    protected int getDayOfWeekNumber(final String s) {
        final Integer integer = (Integer)CronExpression.dayMap.get(s);
        if (integer == null) {
            return -1;
        }
        return integer;
    }
    
    protected Date getTime(final int sc, final int mn, final int hr, final int dayofmn, final int mon) {
        try {
            final Calendar cl = Calendar.getInstance(this.getTimeZone());
            if (hr >= 0 && hr <= 12) {
                cl.set(9, 0);
            }
            if (hr >= 13 && hr <= 23) {
                cl.set(9, 1);
            }
            cl.setLenient(false);
            if (sc != -1) {
                cl.set(13, sc);
            }
            if (mn != -1) {
                cl.set(12, mn);
            }
            if (hr != -1) {
                cl.set(11, hr);
            }
            if (dayofmn != -1) {
                cl.set(5, dayofmn);
            }
            if (mon != -1) {
                cl.set(2, mon);
            }
            return cl.getTime();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    protected Date getTimeAfter(Date afterTime) {
        final Calendar cl = Calendar.getInstance(this.getTimeZone());
        afterTime = new Date(afterTime.getTime() + 1000L);
        cl.setTime(afterTime);
        cl.set(14, 0);
        boolean gotOne = false;
        while (!gotOne) {
            SortedSet st = null;
            int t = 0;
            int sec = cl.get(13);
            int min = cl.get(12);
            st = this.seconds.tailSet(new Integer(sec));
            if (st != null && st.size() != 0) {
                sec = (int)st.first();
            }
            else {
                sec = (int)this.seconds.first();
                ++min;
                cl.set(12, min);
            }
            cl.set(13, sec);
            min = cl.get(12);
            int hr = cl.get(11);
            t = -1;
            st = this.minutes.tailSet(new Integer(min));
            if (st != null && st.size() != 0) {
                t = min;
                min = (int)st.first();
            }
            else {
                min = (int)this.minutes.first();
                ++hr;
            }
            if (min != t) {
                cl.set(13, 0);
                cl.set(12, min);
                this.setCalendarHour(cl, hr);
            }
            else {
                cl.set(12, min);
                hr = cl.get(11);
                int day = cl.get(5);
                t = -1;
                st = this.hours.tailSet(new Integer(hr));
                if (st != null && st.size() != 0) {
                    t = hr;
                    hr = (int)st.first();
                }
                else {
                    hr = (int)this.hours.first();
                    ++day;
                }
                if (hr != t) {
                    cl.set(13, 0);
                    cl.set(12, 0);
                    cl.set(5, day);
                    this.setCalendarHour(cl, hr);
                }
                else {
                    cl.set(11, hr);
                    day = cl.get(5);
                    int mon = cl.get(2) + 1;
                    t = -1;
                    final int tmon = mon;
                    final boolean dayOfMSpec = !this.daysOfMonth.contains(CronExpression.NO_SPEC);
                    final boolean dayOfWSpec = !this.daysOfWeek.contains(CronExpression.NO_SPEC);
                    if (dayOfMSpec && !dayOfWSpec) {
                        st = this.daysOfMonth.tailSet(new Integer(day));
                        if (this.lastdayOfMonth) {
                            if (!this.nearestWeekday) {
                                t = day;
                                day = this.getLastDayOfMonth(mon, cl.get(1));
                            }
                            else {
                                t = day;
                                day = this.getLastDayOfMonth(mon, cl.get(1));
                                final Calendar tcal = Calendar.getInstance();
                                tcal.set(13, 0);
                                tcal.set(12, 0);
                                tcal.set(11, 0);
                                tcal.set(5, day);
                                tcal.set(2, mon - 1);
                                tcal.set(1, cl.get(1));
                                final int ldom = this.getLastDayOfMonth(mon, cl.get(1));
                                final int dow = tcal.get(7);
                                if (dow == 7 && day == 1) {
                                    day += 2;
                                }
                                else if (dow == 7) {
                                    --day;
                                }
                                else if (dow == 1 && day == ldom) {
                                    day -= 2;
                                }
                                else if (dow == 1) {
                                    ++day;
                                }
                                tcal.set(13, sec);
                                tcal.set(12, min);
                                tcal.set(11, hr);
                                tcal.set(5, day);
                                tcal.set(2, mon - 1);
                                final Date nTime = tcal.getTime();
                                if (nTime.before(afterTime)) {
                                    day = 1;
                                    ++mon;
                                }
                            }
                        }
                        else if (this.nearestWeekday) {
                            t = day;
                            day = (int)this.daysOfMonth.first();
                            final Calendar tcal = Calendar.getInstance();
                            tcal.set(13, 0);
                            tcal.set(12, 0);
                            tcal.set(11, 0);
                            tcal.set(5, day);
                            tcal.set(2, mon - 1);
                            tcal.set(1, cl.get(1));
                            final int ldom = this.getLastDayOfMonth(mon, cl.get(1));
                            final int dow = tcal.get(7);
                            if (dow == 7 && day == 1) {
                                day += 2;
                            }
                            else if (dow == 7) {
                                --day;
                            }
                            else if (dow == 1 && day == ldom) {
                                day -= 2;
                            }
                            else if (dow == 1) {
                                ++day;
                            }
                            tcal.set(13, sec);
                            tcal.set(12, min);
                            tcal.set(11, hr);
                            tcal.set(5, day);
                            tcal.set(2, mon - 1);
                            final Date nTime = tcal.getTime();
                            if (nTime.before(afterTime)) {
                                day = (int)this.daysOfMonth.first();
                                ++mon;
                            }
                        }
                        else if (st != null && st.size() != 0) {
                            t = day;
                            day = (int)st.first();
                        }
                        else {
                            day = (int)this.daysOfMonth.first();
                            ++mon;
                        }
                        if (day != t || mon != tmon) {
                            cl.set(13, 0);
                            cl.set(12, 0);
                            cl.set(11, 0);
                            cl.set(5, day);
                            cl.set(2, mon - 1);
                            continue;
                        }
                    }
                    else {
                        if (!dayOfWSpec || dayOfMSpec) {
                            throw new UnsupportedOperationException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
                        }
                        if (this.lastdayOfWeek) {
                            final int dow2 = (int)this.daysOfWeek.first();
                            final int cDow = cl.get(7);
                            int daysToAdd = 0;
                            if (cDow < dow2) {
                                daysToAdd = dow2 - cDow;
                            }
                            if (cDow > dow2) {
                                daysToAdd = dow2 + (7 - cDow);
                            }
                            final int lDay = this.getLastDayOfMonth(mon, cl.get(1));
                            if (day + daysToAdd > lDay) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, 1);
                                cl.set(2, mon);
                                continue;
                            }
                            while (day + daysToAdd + 7 <= lDay) {
                                daysToAdd += 7;
                            }
                            day += daysToAdd;
                            if (daysToAdd > 0) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, day);
                                cl.set(2, mon - 1);
                                continue;
                            }
                        }
                        else if (this.nthdayOfWeek != 0) {
                            final int dow2 = (int)this.daysOfWeek.first();
                            final int cDow = cl.get(7);
                            int daysToAdd = 0;
                            if (cDow < dow2) {
                                daysToAdd = dow2 - cDow;
                            }
                            else if (cDow > dow2) {
                                daysToAdd = dow2 + (7 - cDow);
                            }
                            boolean dayShifted = false;
                            if (daysToAdd > 0) {
                                dayShifted = true;
                            }
                            day += daysToAdd;
                            int weekOfMonth = day / 7;
                            if (day % 7 > 0) {
                                ++weekOfMonth;
                            }
                            daysToAdd = (this.nthdayOfWeek - weekOfMonth) * 7;
                            day += daysToAdd;
                            if (daysToAdd < 0 || day > this.getLastDayOfMonth(mon, cl.get(1))) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, 1);
                                cl.set(2, mon);
                                continue;
                            }
                            if (daysToAdd > 0 || dayShifted) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, day);
                                cl.set(2, mon - 1);
                                continue;
                            }
                        }
                        else {
                            final int cDow2 = cl.get(7);
                            int dow3 = (int)this.daysOfWeek.first();
                            st = this.daysOfWeek.tailSet(new Integer(cDow2));
                            if (st != null && st.size() > 0) {
                                dow3 = (int)st.first();
                            }
                            int daysToAdd = 0;
                            if (cDow2 < dow3) {
                                daysToAdd = dow3 - cDow2;
                            }
                            if (cDow2 > dow3) {
                                daysToAdd = dow3 + (7 - cDow2);
                            }
                            final int lDay = this.getLastDayOfMonth(mon, cl.get(1));
                            if (day + daysToAdd > lDay) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, 1);
                                cl.set(2, mon);
                                continue;
                            }
                            if (daysToAdd > 0) {
                                cl.set(13, 0);
                                cl.set(12, 0);
                                cl.set(11, 0);
                                cl.set(5, day + daysToAdd);
                                cl.set(2, mon - 1);
                                continue;
                            }
                        }
                    }
                    cl.set(5, day);
                    mon = cl.get(2) + 1;
                    int year = cl.get(1);
                    t = -1;
                    if (year > 2099) {
                        return null;
                    }
                    st = this.months.tailSet(new Integer(mon));
                    if (st != null && st.size() != 0) {
                        t = mon;
                        mon = (int)st.first();
                    }
                    else {
                        mon = (int)this.months.first();
                        ++year;
                    }
                    if (mon != t) {
                        cl.set(13, 0);
                        cl.set(12, 0);
                        cl.set(11, 0);
                        cl.set(5, 1);
                        cl.set(2, mon - 1);
                        cl.set(1, year);
                    }
                    else {
                        cl.set(2, mon - 1);
                        year = cl.get(1);
                        t = -1;
                        st = this.years.tailSet(new Integer(year));
                        if (st == null || st.size() == 0) {
                            return null;
                        }
                        t = year;
                        year = (int)st.first();
                        if (year != t) {
                            cl.set(13, 0);
                            cl.set(12, 0);
                            cl.set(11, 0);
                            cl.set(5, 1);
                            cl.set(2, 0);
                            cl.set(1, year);
                        }
                        else {
                            cl.set(1, year);
                            gotOne = true;
                        }
                    }
                }
            }
        }
        return cl.getTime();
    }
    
    protected void setCalendarHour(final Calendar cal, final int hour) {
        cal.set(11, hour);
        if (cal.get(11) != hour && hour != 24) {
            cal.set(11, hour + 1);
        }
    }
    
    protected Date getTimeBefore(final Date endTime) {
        return null;
    }
    
    protected boolean isLeapYear(final int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }
    
    protected int getLastDayOfMonth(final int monthNum, final int year) {
        switch (monthNum) {
            case 1: {
                return 31;
            }
            case 2: {
                return this.isLeapYear(year) ? 29 : 28;
            }
            case 3: {
                return 31;
            }
            case 4: {
                return 30;
            }
            case 5: {
                return 31;
            }
            case 6: {
                return 30;
            }
            case 7: {
                return 31;
            }
            case 8: {
                return 31;
            }
            case 9: {
                return 30;
            }
            case 10: {
                return 31;
            }
            case 11: {
                return 30;
            }
            case 12: {
                return 31;
            }
            default: {
                throw new IllegalArgumentException("Illegal month number: " + monthNum);
            }
        }
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        try {
            this.buildExpression(this.cronExpression);
        }
        catch (Exception ex) {}
    }
    
    public Object clone() {
        CronExpression copy = null;
        try {
            copy = new CronExpression(this.getCronExpression());
            copy.setTimeZone(this.getTimeZone());
        }
        catch (ParseException ex) {
            throw new IncompatibleClassChangeError("Not Cloneable.");
        }
        return copy;
    }
    
    static {
        ALL_SPEC = new Integer(99);
        NO_SPEC = new Integer(98);
        CronExpression.monthMap = new HashMap(20);
        CronExpression.dayMap = new HashMap(60);
        CronExpression.monthMap.put("JAN", new Integer(0));
        CronExpression.monthMap.put("FEB", new Integer(1));
        CronExpression.monthMap.put("MAR", new Integer(2));
        CronExpression.monthMap.put("APR", new Integer(3));
        CronExpression.monthMap.put("MAY", new Integer(4));
        CronExpression.monthMap.put("JUN", new Integer(5));
        CronExpression.monthMap.put("JUL", new Integer(6));
        CronExpression.monthMap.put("AUG", new Integer(7));
        CronExpression.monthMap.put("SEP", new Integer(8));
        CronExpression.monthMap.put("OCT", new Integer(9));
        CronExpression.monthMap.put("NOV", new Integer(10));
        CronExpression.monthMap.put("DEC", new Integer(11));
        CronExpression.dayMap.put("SUN", new Integer(1));
        CronExpression.dayMap.put("MON", new Integer(2));
        CronExpression.dayMap.put("TUE", new Integer(3));
        CronExpression.dayMap.put("WED", new Integer(4));
        CronExpression.dayMap.put("THU", new Integer(5));
        CronExpression.dayMap.put("FRI", new Integer(6));
        CronExpression.dayMap.put("SAT", new Integer(7));
    }
}
