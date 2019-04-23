// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sp;

import com.ailk.aee.common.stringobject.StringMapConverter;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.common.conf.Configuration;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ServiceProviderManager.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ServiceProviderManager
{
    private static ServiceProviderManager spm;
    private Map<String, IServiceProvider> providers;
    
    public static ServiceProviderManager getInstance() {
        return ServiceProviderManager.spm;
    }
    
    private ServiceProviderManager() {
        this.providers = new HashMap<String, IServiceProvider>();
        Map<String, String> m = Configuration.getConf("serviceprovider");
        if (m == null || m.size() == 0) {
            m = Configuration.getConf("AEE.serviceprovider");
        }
        if (m.size() > 0) {
            final String[] arr$;
            final String[] ss = arr$ = MapTools.getSubKeys(m);
            for (final String s : arr$) {
                final String v = m.get(s + ".provider");
                if (v != null) {
                    try {
                        final IServiceProvider isp = ObjectBuilder.build(IServiceProvider.class, v, null);
                        if (isp != null) {
                            Map<String, String> arg = new HashMap<String, String>();
                            final String arguments = m.get(s + ".argument");
                            if (arguments != null) {
                                if (arguments.length() != 0) {
                                    arg = new StringMapConverter().wrapFromString(arguments);
                                }
                            }
                            isp.build(arg);
                        }
                        this.providers.put(s, isp);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public IServiceProvider getServiceProvider(final String spn) {
        if (this.providers.containsKey(spn)) {
            return this.providers.get(spn);
        }
        return null;
    }
    
    static {
        ServiceProviderManager.spm = new ServiceProviderManager();
    }
}
