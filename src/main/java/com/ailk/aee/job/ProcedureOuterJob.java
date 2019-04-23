// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import com.ailk.aee.core.IJobSession;

public class ProcedureOuterJob extends ProcedureJob
{
    @Override
    public void dealException(final IJobSession ctx, final Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}
