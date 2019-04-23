// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.strategy;

import java.util.Iterator;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.util.DateFormatUtils;
import java.util.Date;
import com.ailk.aee.common.stringobject.StringListConverter;
import com.ailk.aee.core.Worker;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DateTimePatternCounterStrategy.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DateTimePatternCounterStrategy implements ICounterStrategy
{
    private String confStr;
    private float v;
    private boolean isCalced;
    private List<String> ls;
    
    public DateTimePatternCounterStrategy() {
        this.confStr = "";
        this.v = 0.0f;
        this.isCalced = false;
        this.ls = null;
    }
    
    @Override
    public float calc(final Worker jw) {
        if (!this.isCalced) {
            this.isCalced = true;
            if (this.confStr != null && !this.confStr.equals("")) {
                final StringListConverter slc = new StringListConverter();
                if (slc.canWrapFromString(this.confStr)) {
                    this.ls = (List<String>)slc.wrapFromString(this.confStr);
                }
            }
        }
        if (this.ls != null && this.ls.size() > 0) {
            final Date d = new Date();
            final String s4 = DateFormatUtils.format(d, "HHmm");
            final String s5 = DateFormatUtils.format(d, "ddHHmm");
            final long l4 = Long.parseLong(s4);
            final long l5 = Long.parseLong(s5);
            for (final String s6 : this.ls) {
                final String vCounter = StringUtils.substringAfter(s6, ":");
                final String vstart = StringUtils.substringBefore(s6, "-");
                final String vend = StringUtils.substringBetween(s6, "-", ":");
                final float iCounter = Float.parseFloat(vCounter);
                final long lstart = Long.parseLong(vstart);
                final long lend = Long.parseLong(vend);
                if (vstart.length() == 4) {
                    if (lstart < l4 && lend >= l4) {
                        return iCounter;
                    }
                    continue;
                }
                else {
                    if (vstart.length() == 6 && lstart < l5 && lend >= l5) {
                        return iCounter;
                    }
                    continue;
                }
            }
        }
        return this.v;
    }
}
