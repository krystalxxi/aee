// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import java.util.Arrays;
import com.ailk.aee.common.util.DateFormatUtils;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentSkipListMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ValueStater.java 60795 2013-11-05 03:36:27Z tangxy $")
public class ValueStater
{
    private IValueProvider ivp;
    private long lastValue;
    private long lastTickTime;
    private long firstTime;
    private ConcurrentSkipListMap<Long, Long> tmp;
    private AtomicLong count;
    private int cycleTime;
    
    public ValueStater(final IValueProvider ivp) {
        this.ivp = null;
        this.firstTime = System.currentTimeMillis();
        this.tmp = new ConcurrentSkipListMap<Long, Long>();
        this.count = new AtomicLong(0L);
        this.cycleTime = 5;
        this.ivp = ivp;
    }
    
    private long getAvg(final int sec) {
        final long now = System.nanoTime();
        final Collection<Long> v = this.tmp.tailMap(Long.valueOf(now - TimeUnit.SECONDS.toNanos(sec))).values();
        final Object[] copy = v.toArray();
        if (copy == null || copy.length == 0) {
            return 0L;
        }
        final long[] values = new long[copy.length];
        long total = 0L;
        for (int i = 0; i < copy.length; ++i) {
            total += (long)copy[i];
        }
        return total / copy.length;
    }
    
    public long getAvg15Minute() {
        return this.getAvg(900);
    }
    
    public long getAvg1Minute() {
        return this.getAvg(60);
    }
    
    public long getAvg30Minute() {
        return this.getAvg(1800);
    }
    
    public long getAvg5Minute() {
        return this.getAvg(300);
    }
    
    public long getAvg60Minute() {
        return this.getAvg(3600);
    }
    
    public long getCurrentValue() {
        return this.getValue();
    }
    
    public long getLastValue() {
        return this.lastValue;
    }
    
    public long getMax(final long[] values) {
        if (values.length == 0) {
            return 0L;
        }
        return values[values.length - 1];
    }
    
    public double getMean(final long[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (final long value : values) {
            sum += value;
        }
        return Math.floor(sum / values.length * 100.0) / 100.0;
    }
    
    public long getMin(final long[] values) {
        if (values.length == 0) {
            return 0L;
        }
        return values[0];
    }
    
    private double getRateValue(final long[] values, final double quantile) {
        if (quantile < 0.0 || quantile > 1.0) {
            throw new IllegalArgumentException(quantile + " is not in [0..1]");
        }
        if (values.length == 0) {
            return 0.0;
        }
        final double pos = quantile * (values.length + 1);
        if (pos < 1.0) {
            return values[0];
        }
        if (pos >= values.length) {
            return values[values.length - 1];
        }
        final double lower = values[(int)pos - 1];
        final double upper = values[(int)pos];
        return Math.floor((lower + (pos - Math.floor(pos)) * (upper - lower)) * 100.0) / 100.0;
    }
    
    public double getStdDev(final long[] values) {
        if (values.length <= 1) {
            return 0.0;
        }
        final double mean = this.getMean(values);
        double sum = 0.0;
        for (final long value : values) {
            final double diff = value - mean;
            sum += diff * diff;
        }
        final double variance = sum / (values.length - 1);
        return Math.floor(Math.sqrt(variance) * 100.0) / 100.0;
    }
    
    public String getTimeWindowInfo(final int sec) {
        final long now = System.nanoTime();
        final String snow = DateFormatUtils.SIMPLE_DATETIME_FORMAT_3.format(System.currentTimeMillis());
        final Collection<Long> v = this.tmp.tailMap(Long.valueOf(now - TimeUnit.SECONDS.toNanos(sec))).values();
        final Object[] copy = v.toArray();
        final long[] values = new long[copy.length];
        for (int i = 0; i < copy.length; ++i) {
            values[i] = (long)copy[i];
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("@Last[").append(sec).append("]seconds");
        sb.append(" to [").append(snow).append("]");
        sb.append(" ").append(this.ivp.getName()).append("[").append(this.ivp.getIndex()).append("]:");
        sb.append("current=").append(this.ivp.getValue()).append(",");
        Arrays.sort(values);
        sb.append("max=").append(this.getMax(values)).append(",");
        sb.append("min=").append(this.getMin(values)).append(",");
        sb.append("mean=").append(this.getMean(values)).append(",");
        sb.append("stddev=").append(this.getStdDev(values)).append(",");
        sb.append("75%=").append(this.getRateValue(values, 0.75)).append(",");
        sb.append("95%=").append(this.getRateValue(values, 0.95)).append(",");
        sb.append("99%=").append(this.getRateValue(values, 0.99)).append(",");
        sb.append("99.9%=").append(this.getRateValue(values, 0.999)).append(",");
        return sb.toString();
    }
    
    public long getValue() {
        return this.ivp.getValue();
    }
    
    public IValueProvider getValueProvider() {
        return this.ivp;
    }
    
    public long tick() {
        synchronized (this) {
            final long v = this.ivp.getValueAndReset();
            this.lastValue = v;
            final long now = System.nanoTime();
            this.tmp.put(now, new Long(v));
            this.lastTickTime = now;
            this.trim();
            return v;
        }
    }
    
    private void trim() {
        final long now = System.nanoTime();
        this.tmp.headMap(Long.valueOf(now - TimeUnit.SECONDS.toNanos(3600L))).clear();
    }
}
