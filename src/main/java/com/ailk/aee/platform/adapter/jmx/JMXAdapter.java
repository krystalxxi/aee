// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.adapter.jmx;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.platform.adapter.AbstractPlatformServiceAdapter;

@CVSID("$Id: JMXAdapter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class JMXAdapter extends AbstractPlatformServiceAdapter
{
    private MBeanServer mbs;
    
    public JMXAdapter() {
        this.mbs = ManagementFactory.getPlatformMBeanServer();
    }
    
    @Override
    public void onServiceRegister(final String serviceName) {
    }
    
    @Override
    public void onServiceUnRegister(final String serviceName) {
    }
}
