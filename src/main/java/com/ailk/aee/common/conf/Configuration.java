// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import com.ailk.aee.common.util.StringUtils;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import com.ailk.aee.common.util.StringLiker;
import java.util.Iterator;
import com.ailk.aee.common.util.text.StrLookup;
import com.ailk.aee.common.util.text.StrSubstitutor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Configuration.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Configuration
{
    private static Configuration inst;
    private ConfigurationFactory cf;
    private ConfigurationFactory staticcf;
    private ConcurrentHashMap<String, String> allData;
    private Map<String, String> localConf;
    
    public static String dump() {
        return getInstance().getDumpInfo();
    }
    
    public static String dump(final String[] args) {
        return getInstance().getDumpInfo(args);
    }
    
    public static boolean getBooleanValue(final String name) {
        return getBooleanValue(name, false);
    }
    
    public static boolean getBooleanValue(final String name, final boolean defv) {
        final String s = getValue(name, defv ? "Y" : "N");
        return s.equalsIgnoreCase("YES") || s.equals("Y") || s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("T") || s.equalsIgnoreCase("OK");
    }
    
    public static Map<String, String> getConf(final String prefix) {
        return getConf(prefix, false);
    }
    
    public static Map<String, String> getConf(final String prefix, final boolean saveprefix) {
        return getInstance().getconf(prefix, saveprefix);
    }
    
    public static Configuration getInstance() {
        Configuration.inst.checkUpdate();
        return Configuration.inst;
    }
    
    public static int getIntValue(final String name) {
        return getIntValue(name, 0);
    }
    
    public static int getIntValue(final String name, final int defv) {
        final String s = getValue(name);
        if (s == null) {
            return defv;
        }
        try {
            final int i = Integer.parseInt(s);
            return i;
        }
        catch (Exception e) {
            return defv;
        }
    }
    
    public static long getLongValue(final String name) {
        return getLongValue(name, 0L);
    }
    
    public static long getLongValue(final String name, final long defv) {
        final String s = getValue(name);
        if (s == null) {
            return defv;
        }
        try {
            final long i = Long.parseLong(s);
            return i;
        }
        catch (Exception e) {
            return defv;
        }
    }
    
    public static String getValue(final String name) {
        return getValue(name, null);
    }
    
    public static String getValue(final String name, final String defv) {
        return getInstance().getvalue(name, defv);
    }
    
    private Configuration() {
        this.cf = null;
        this.staticcf = null;
        this.allData = new ConcurrentHashMap<String, String>();
        this.localConf = new HashMap<String, String>();
    }
    
    public synchronized void addConfigurationFactory(final ConfigurationFactory c) {
        if (this.cf == null) {
            this.cf = c;
            if (this.staticcf != null) {
                this.staticcf.getNextFactory().getNextFactory().setNextFactory(this.cf);
                this.buildData();
            }
            return;
        }
        ConfigurationFactory cft;
        ConfigurationFactory cftnext;
        for (cft = this.cf, cftnext = null, cftnext = this.cf.getNextFactory(); cftnext != null; cftnext = cft.getNextFactory()) {
            cft = cftnext;
        }
        cft.setNextFactory(c);
        this.buildData();
    }
    
    public String applyVariable(final String s) {
        final StrSubstitutor ss = new StrSubstitutor();
        ss.setVariableResolver(new StrLookup<String>() {
            @Override
            public String lookup(final String key) {
                if (Configuration.getInstance().getAllData().containsKey(key)) {
                    return Configuration.getInstance().getAllData().get(key);
                }
                Configuration.getInstance().setLocalConfiguration(key, "");
                return this.lookup(key);
            }
        });
        final String rv = ss.replace(s);
        return rv;
    }
    
    public void build() {
        if (this.staticcf != null) {
            return;
        }
        synchronized (this) {
            if (this.staticcf != null) {
                return;
            }
            final MapConfigurationFactory local = new MapConfigurationFactory();
            local.setConf(this.localConf);
            final EnvConfigurationFactory env = new EnvConfigurationFactory();
            final SystemPropConfigurationFactory sp = new SystemPropConfigurationFactory();
            env.setNextFactory(this.cf);
            sp.setNextFactory(env);
            local.setNextFactory(sp);
            this.staticcf = local;
            this.buildData();
        }
    }
    
    public void buildData() {
        final ConcurrentHashMap<String, String> tempAllData = new ConcurrentHashMap<String, String>();
        ConfigurationFactory tempcf = this.staticcf;
        this.allData.clear();
        while (tempcf != null) {
            final Map<String, String> tempMap = tempcf.getConf("");
            for (final Map.Entry<String, String> entry : tempMap.entrySet()) {
                if (tempAllData.containsKey(entry.getKey())) {
                    continue;
                }
                tempAllData.put(entry.getKey(), entry.getValue());
            }
            tempcf = tempcf.getNextFactory();
        }
        this.allData.putAll(tempAllData);
    }
    
    public void checkUpdate() {
        for (ConfigurationFactory cff = this.cf; cff != null; cff = cff.getNextFactory()) {
            cff.checkReload();
        }
    }
    
    public synchronized void clearConfigurationFactory() {
        if (this.cf != null) {
            this.cf = null;
            this.buildData();
        }
    }
    
    public Map<String, String> getAllData() {
        return this.allData;
    }
    
    public Map<String, String> getconf(final String prefix, final boolean saveprefix) {
        this.build();
        final Map<String, String> allRes = MapTools.getSub(this.allData, prefix, saveprefix);
        for (final String key : allRes.keySet()) {
            String value = allRes.get(key);
            if (value.indexOf("${") >= 0) {
                if (saveprefix) {
                    value = getValue(key);
                }
                else {
                    value = getValue(prefix + "." + key);
                }
                allRes.put(key, value);
            }
        }
        return allRes;
    }
    
    public String getDumpInfo() {
        return this.getDumpInfo(null);
    }
    
    public String getDumpInfo(final String[] args) {
        this.build();
        final StringLiker pl = StringLiker.PathLiker;
        final StringLiker dl = StringLiker.DBLiker;
        final StringBuffer sb = new StringBuffer();
        for (ConfigurationFactory tempcf = this.staticcf; tempcf != null; tempcf = tempcf.getNextFactory()) {
            sb.append("#dumpinfo of ConfigurationFactory: " + tempcf.getFactoryName() + ":\n");
            sb.append(tempcf.dump(args));
            sb.append("#=======================================================================\n");
        }
        sb.append("\n\n\n\n\n\n#dumpinfo of allData ----------------------------------------------------------------------------\n");
        int keylength = 0;
        int valuelength = 0;
        final Map<String, String> temp = new HashMap<String, String>();
        if (args != null) {
            for (final Map.Entry<String, String> e : this.allData.entrySet()) {
                for (final String s : args) {
                    if (pl.isLike(e.getKey(), s) || dl.isLike(e.getKey(), s)) {
                        temp.put(e.getKey(), getValue(e.getKey(), ""));
                    }
                }
            }
        }
        else {
            temp.putAll(this.allData);
        }
        final ArrayList<String> al = new ArrayList<String>();
        for (final Map.Entry<String, String> e2 : temp.entrySet()) {
            if (e2.getKey().length() > keylength) {
                keylength = e2.getKey().length();
            }
            if (e2.getValue().length() > valuelength) {
                valuelength = e2.getValue().length();
            }
            al.add(e2.getKey());
        }
        Collections.sort(al);
        for (final String s2 : al) {
            final String c = temp.get(s2);
            sb.append(StringUtils.rightPad(s2, keylength, " ") + " = " + c);
            sb.append("\n");
        }
        sb.append("#END OF dumpinfo of allData ----------------------------------------------------------------------------\n\n\n\n\n\n\n");
        return sb.toString();
    }
    
    public String getLocalConfiguration(final String key) {
        return this.localConf.get(key);
    }
    
    public String getvalue(final String name, final String defv) {
        this.build();
        final String value = this.allData.get(name);
        if (value == null) {
            return defv;
        }
        if (StringUtils.indexOf(value, "${") >= 0) {
            return this.applyVariable(value);
        }
        return value;
    }
    
    public void setLocalConfiguration(final String key, final String value) {
        this.localConf.put(key, value);
        this.allData.put(key, value);
    }
    
    static {
        Configuration.inst = new Configuration();
    }
}
