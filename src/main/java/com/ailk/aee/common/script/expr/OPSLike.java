// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import com.ailk.aee.common.util.StringLiker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSLike.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSLike extends AbstractOPS
{
    @Override
    public boolean calc(final Object left, final Object right) {
        return left != null && right != null && StringLiker.PathLiker.isLike(left.toString(), right.toString());
    }
}
