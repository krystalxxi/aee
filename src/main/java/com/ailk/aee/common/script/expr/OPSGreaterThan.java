// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.util.DateUtils;
import java.util.Date;
import com.ailk.aee.common.util.NumberUtils;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSGreaterThan.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSGreaterThan extends AbstractOPS
{
    @Override
    public boolean calc(final Object left, final Object right) {
        if (left == null || right == null) {
            return false;
        }
        if (left instanceof Number && right instanceof String && StringUtils.isNumeric((CharSequence)right)) {
            final Number v = NumberUtils.createNumber((String)right);
            return this.calc(left, v);
        }
        if (right instanceof Number && left instanceof String && StringUtils.isNumeric((CharSequence)left)) {
            final Number v = NumberUtils.createNumber((String)left);
            return this.calc(right, v);
        }
        if (left instanceof Date && right instanceof Date) {
            return DateUtils.truncatedCompareTo((Date)left, (Date)right, 13) > 0;
        }
        if (left instanceof Comparable && right instanceof Comparable) {
            final int v2 = ((Comparable)left).compareTo(right);
            if (v2 > 0) {
                return true;
            }
        }
        return false;
    }
}
