// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import java.util.Iterator;
import com.ailk.aee.platform.AEEPlatform;
import java.util.HashMap;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: HelperService.java 60270 2013-11-03 14:48:37Z tangxy $")
public class HelperService extends AbstractPlatformService
{
    @Override
    public String getServiceDescription() {
        return "get help or other description";
    }
    
    @Override
    public String getServiceName() {
        return "HELP";
    }
    
    @PlatformServiceMethod
    public Map<String, String> help(final Map<String, String> args) {
        return null;
    }
    
    @PlatformServiceMethod
    public Map<String, String> service() {
        final Map<String, String> ms = new HashMap<String, String>();
        final Map<String, AbstractPlatformService> m = AEEPlatform.getInstance().getServices();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "HELP.service ok!");
        int i = 0;
        for (final String key : m.keySet()) {
            ms.put("AEE_SVC_HELP_SERVICE." + i, key);
            ++i;
        }
        return ms;
    }
}
