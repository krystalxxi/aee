// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.trigger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CronExprTrigger extends TimeTrigger
{
    private CronExpression expr;
    
    public CronExprTrigger() {
        this.expr = null;
    }
    
    @Override
    public Date[] getFireTimeBetween(final Date startDate, final Date endDate, final int countlimits) {
        if (this.expr == null) {
            return null;
        }
        long ed = endDate.getTime();
        long sd = startDate.getTime();
        if (sd > ed) {
            final long v = ed;
            ed = sd;
            sd = v;
        }
        final ArrayList<Date> dl = new ArrayList<Date>();
        Date d = this.expr.getNextValidTimeAfter(new Date(sd));
        int i = 0;
        if (d.getTime() <= ed) {
            dl.add(d);
            while (i < countlimits || countlimits < 0) {
                ++i;
                d = this.expr.getNextValidTimeAfter(d);
                if (d.getTime() > ed) {
                    break;
                }
                dl.add(d);
            }
            return dl.toArray(new Date[0]);
        }
        return null;
    }
    
    @Override
    public void setArgument(final String s) throws Exception {
        this.expr = new CronExpression(s);
    }
    
    public static void main(final String[] args) {
        final CronExprTrigger c = new CronExprTrigger();
        try {
            c.setArgument("0 0 0/12 * * ? *");
            for (final Date temp : c.getFireTimeBetween(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2013-03-08 14:59:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-05-08 15:00:00"), -1)) {
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(temp));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
