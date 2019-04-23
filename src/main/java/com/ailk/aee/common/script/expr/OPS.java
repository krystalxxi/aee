// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPS.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface OPS
{
    public static final OPS EQUAL = new OPSEqual();
    public static final OPS NOT_EQUAL = new OPSNotEqual();
    public static final OPS LT = new OPSLessThan();
    public static final OPS LE = new OPSEqualOrLessThan();
    public static final OPS GT = new OPSGreaterThan();
    public static final OPS GE = new OPSEqualOrGreaterThan();
    public static final OPS IN = new OPSIn();
    public static final OPS NOT_IN = new OPSNotIn();
    public static final OPS LIKE = new OPSLike();
    public static final OPS RE_LIKE = new OPSRegularLike();
    public static final OPS BETWEEN = new OPSBetween();
    
    boolean calc(final Object p0, final Object p1);
}
