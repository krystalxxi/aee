// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.trigger;

import java.util.Date;

public abstract class TimeTrigger
{
    private static Date DOOMSDAY;
    
    public abstract void setArgument(final String p0) throws Exception;
    
    public abstract Date[] getFireTimeBetween(final Date p0, final Date p1, final int p2);
    
    public Date getFirstFireTimeAfter(final Date d) {
        final Date[] ds = this.getFireTimeBetween(d, TimeTrigger.DOOMSDAY, 1);
        if (ds != null) {
            return ds[0];
        }
        return TimeTrigger.DOOMSDAY;
    }
    
    public Date getFirstFireTime() {
        return this.getFirstFireTimeAfter(new Date());
    }
    
    public static void main(final String[] args) {
        System.out.println(TimeTrigger.DOOMSDAY);
    }
    
    static {
        TimeTrigger.DOOMSDAY = new Date(Long.MAX_VALUE);
    }
}
