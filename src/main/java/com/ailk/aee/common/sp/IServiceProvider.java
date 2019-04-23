// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sp;

import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IServiceProvider.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IServiceProvider
{
    public static final String DB_CONNECTION_SERVICEPROVIDER = "DataBaseConnection";
    public static final String JMS_CONNECTION_SERVICEPROVIDER = "JMSConnection";
    public static final String MEMCACHE_CONNECTION_SERVICEPROVIDER = "MemCacheConnection";
    public static final String ZOOKEEPER_CONNECTION_SERVICEPROVIDER = "ZooKeeperConnection";
    
    void build(final Map<String, String> p0) throws Exception;
    
    Object getService(final String p0);
}
