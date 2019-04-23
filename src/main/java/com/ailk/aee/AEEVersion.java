// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee;

import java.util.HashMap;
import com.ailk.aee.common.util.StringUtils;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEVersion.java 60270 2013-11-03 14:48:37Z tangxy $")
public class AEEVersion
{
    public static final int VERSION = 1;
    public static final int MAJOR = 0;
    public static final int MINOR = 0;
    public static final String RELEASE = "$Revision$";
    public static final String PRODUCT = "AEE";
    private static Map<String, Boolean> featureMap;
    
    public static String getVersion() {
        final StringBuffer sb = new StringBuffer();
        sb.append("AEE Version:1.0.0 Release:" + StringUtils.substringBetween("$Revision$", "$Revision:", "$").trim()).append("\n");
        return sb.toString();
    }
    
    public static boolean isFeatureSupport(final String s) {
        final Boolean v = AEEVersion.featureMap.get(s);
        return v != null && v;
    }
    
    public static void main(final String[] args) {
        try {
            System.out.println(getVersion());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void registerFeature(final String s, final boolean v) {
        AEEVersion.featureMap.put(s, new Boolean(v));
    }
    
    @Override
    public String toString() {
        return getVersion();
    }
    
    static {
        AEEVersion.featureMap = new HashMap<String, Boolean>();
    }
}
