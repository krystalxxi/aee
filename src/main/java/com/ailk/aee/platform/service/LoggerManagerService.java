// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service;

import org.apache.log4j.LogManager;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.platform.annotation.PlatformServiceMethod;
import com.ailk.aee.AEELogger;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: LoggerManagerService.java 60795 2013-11-05 03:36:27Z tangxy $")
public class LoggerManagerService extends AbstractPlatformService
{
    @Override
    public String getServiceDescription() {
        return "Logger Manager";
    }
    
    @Override
    public String getServiceName() {
        return "LM";
    }
    
    @PlatformServiceMethod
    public Map<String, String> reset() {
        final Map<String, String> ms = new HashMap<String, String>();
        AEELogger.configureLogger();
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "LM.reset ok!");
        return ms;
    }
    
    @PlatformServiceMethod
    public Map<String, String> openDebug() {
        final Map<String, String> ms = new HashMap<String, String>();
        AEELogger.configureDebugLogger(AEEPlatform.getInstance().getWorkName());
        ms.put("AEE_RESULT_CODE", "0");
        ms.put("AEE_RESULT_INFO", "LM.openDebug ok!");
        return ms;
    }
    
    @PlatformServiceMethod
    public Map<String, String> closeDebug() {
        LogManager.resetConfiguration();
        return this.reset();
    }
}
