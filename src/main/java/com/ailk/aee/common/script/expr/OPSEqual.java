// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.util.DateUtils;
import com.ailk.aee.common.stringobject.ConverterCollections;
import java.util.Date;
import com.ailk.aee.common.util.NumberUtils;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSEqual.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSEqual extends AbstractOPS
{
    private static boolean canCast(final Class from, final Class to) {
        return from.equals(to) || from.isAssignableFrom(to) || to.isAssignableFrom(from);
    }
    
    @Override
    public boolean calc(final Object left, final Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (canCast(left.getClass(), right.getClass()) || canCast(right.getClass(), left.getClass())) {
            return left.equals(right);
        }
        if (left instanceof Number || right instanceof Number) {
            if (left instanceof Number) {
                return right instanceof String && StringUtils.isNumeric((CharSequence)right) && ((Number)left).equals(NumberUtils.createNumber((String)right));
            }
            return left instanceof String && StringUtils.isNumeric((CharSequence)left) && ((Number)right).equals(NumberUtils.createNumber((String)left));
        }
        else if (left instanceof Date || right instanceof Date) {
            if (right instanceof Date) {
                final boolean v = ConverterCollections.dateConverter.canWrapFromString((String)left);
                if (v) {
                    final Date v2 = (Date)ConverterCollections.dateConverter.wrapFromString((String)left);
                    return DateUtils.truncatedEquals(v2, (Date)right, 13);
                }
                return false;
            }
            else {
                final boolean v = ConverterCollections.dateConverter.canWrapFromString((String)right);
                if (v) {
                    final Date v2 = (Date)ConverterCollections.dateConverter.wrapFromString((String)right);
                    return DateUtils.truncatedEquals(v2, (Date)left, 13);
                }
                return false;
            }
        }
        else {
            if (!(left instanceof Boolean) && !(right instanceof Boolean)) {
                return false;
            }
            if (left instanceof String) {
                final boolean v = ConverterCollections.booleanConverter.canWrapFromString((String)left);
                if (v) {
                    final boolean v3 = (boolean)ConverterCollections.booleanConverter.wrapFromString((String)left);
                    return ((Boolean)right).equals(v3);
                }
                return false;
            }
            else {
                final boolean v = ConverterCollections.booleanConverter.canWrapFromString((String)right);
                if (v) {
                    final boolean v3 = (boolean)ConverterCollections.booleanConverter.wrapFromString((String)right);
                    return ((Boolean)left).equals(v3);
                }
                return false;
            }
        }
    }
}
