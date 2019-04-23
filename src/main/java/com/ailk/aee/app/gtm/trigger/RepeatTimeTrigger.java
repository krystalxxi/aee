// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.trigger;

import java.text.SimpleDateFormat;
import java.util.List;
import com.ailk.aee.common.stringobject.ConverterCollections;
import com.ailk.aee.common.stringobject.StringListConverter;
import com.ailk.aee.common.util.DateUtils;
import java.util.ArrayList;
import java.util.Date;

public class RepeatTimeTrigger extends TimeTrigger
{
    private long seconds;
    private String arguments;
    private String baseFireTime;
    private Date lastFireTime;
    
    public RepeatTimeTrigger() {
        this.lastFireTime = null;
    }
    
    public long getNearestLongIncreaseBy(final long startValue, final long comparedValue, final long intervalValue, final boolean isMax) {
        if (startValue > comparedValue) {
            return startValue;
        }
        long v = 0L;
        if (isMax) {
            v = (comparedValue - startValue + intervalValue - 1L) / intervalValue;
        }
        else {
            v = (comparedValue - startValue) / intervalValue;
        }
        return startValue + intervalValue * v;
    }
    
    @Override
    public Date[] getFireTimeBetween(final Date bstartDate, final Date bendDate, final int countlimits) {
        long ed = bendDate.getTime();
        long sd = bstartDate.getTime();
        if (sd > ed) {
            final long v = ed;
            ed = sd;
            sd = v;
        }
        if (this.lastFireTime == null) {
            this.lastFireTime = new Date();
        }
        final long fd = this.lastFireTime.getTime();
        final ArrayList<Date> d = new ArrayList<Date>();
        long a1 = this.getNearestLongIncreaseBy(fd, sd, this.seconds, true);
        if (a1 > ed) {
            return null;
        }
        int i = 0;
        d.add(new Date(a1));
        while (true) {
            ++i;
            a1 += this.seconds;
            if (a1 >= ed || (i >= countlimits && countlimits >= 0)) {
                break;
            }
            d.add(new Date(a1));
        }
        return d.toArray(new Date[0]);
    }
    
    public String getUsage() {
        return "";
    }
    
    private void buildTrigger() throws Exception {
        this.seconds = DateUtils.getDateDuration(this.arguments);
    }
    
    @Override
    public void setArgument(final String s) throws Exception {
        final List<String> ls = (List<String>)new StringListConverter().wrapFromString(s);
        if (ls.size() != 2) {
            throw new Exception(this.getUsage());
        }
        this.arguments = ls.get(0);
        this.baseFireTime = ls.get(1);
        final Date d = (Date)ConverterCollections.dateConverter.wrapFromString(this.baseFireTime);
        this.setLastFireTime(d);
        this.buildTrigger();
    }
    
    public void setLastFireTime(final Date d) {
        this.lastFireTime = d;
    }
    
    public static void main(final String[] s) {
        try {
            final RepeatTimeTrigger t = new RepeatTimeTrigger();
            t.setArgument("2h");
            final Date sd = (Date)ConverterCollections.dateConverter.wrapFromString("2001-01-03 12:01:14");
            final Date ed = (Date)ConverterCollections.dateConverter.wrapFromString("2001-01-03 18:01:14");
            final Date fd = (Date)ConverterCollections.dateConverter.wrapFromString("2001-01-01 12:01:15");
            t.setLastFireTime(fd);
            final Date[] ds = t.getFireTimeBetween(sd, ed, 20);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (ds == null) {
                System.out.println("not any");
            }
            else {
                for (final Date d : ds) {
                    System.out.println(sdf.format(d));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
