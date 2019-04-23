// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.plugin;

import java.util.Iterator;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.IJob;
import java.util.LinkedList;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.IWorkerPlugin;

@CVSID("$Id: ChainPlugin.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ChainPlugin implements IWorkerPlugin
{
    protected List<IWorkerPlugin> plugins;
    
    public ChainPlugin() {
        this.plugins = new LinkedList<IWorkerPlugin>();
    }
    
    public void add(final IWorkerPlugin wp) {
        this.plugins.add(wp);
    }
    
    @Override
    public void after(final IJob.JOB_STEP s, final IJobSession ctx) throws Exception {
        for (final IWorkerPlugin wp : this.plugins) {
            wp.after(s, ctx);
        }
    }
    
    @Override
    public void before(final IJob.JOB_STEP s, final IJobSession ctx) throws Exception {
        for (final IWorkerPlugin wp : this.plugins) {
            wp.before(s, ctx);
        }
    }
    
    @Override
    public void onException(final IJobSession ctx, final Exception e) {
        for (final IWorkerPlugin wp : this.plugins) {
            wp.onException(ctx, e);
        }
    }
}
