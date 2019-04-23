// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.config;

import java.util.Set;
import com.ailk.aee.common.util.StringLiker;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.common.conf.MapTools;
import java.util.Iterator;
import java.util.HashMap;
import com.ailk.aee.common.conf.FileConfigurationFactory;
import com.ailk.aee.AEEConf;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.common.util.text.StrSubstitutor;
import java.util.ArrayList;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: AEEWorkConfig.java 62289 2013-11-07 08:03:13Z huwl $")
public class AEEWorkConfig
{
    private static AEEWorkConfig instance;
    private Map<String, String> allConfig;
    private Map<String, String> rtNodes;
    private ArrayList<String> aliveNodes;
    
    public static AEEWorkConfig getInstance() {
        return AEEWorkConfig.instance;
    }
    
    private String applyVariable(final String s) {
        final String v = StrSubstitutor.replace((Object)s, (Map)this.allConfig);
        if (v.indexOf("${") >= 0) {
            return Configuration.getInstance().applyVariable(s);
        }
        return v;
    }
    
    public static void main(final String[] args) {
        System.setProperty("AEE_HOME", "D:\\Billing\\Alpha\\yunn\\out\\production\\aee");
        AEEConf.init();
        final String workCfgFile = Configuration.getValue("AEE_WORK_CONFIG");
        final FileConfigurationFactory fc = new FileConfigurationFactory(workCfgFile, "com.ailk.common.conf.parsetype.XML");
        fc.initConfMap();
        System.out.println(fc.dump());
        final String[] arr$;
        final String[] ws = arr$ = getInstance().getMyWorkByFilter("");
        for (final String s : arr$) {
            System.out.println(s);
        }
    }
    
    private AEEWorkConfig() {
        this.allConfig = new HashMap<String, String>();
        this.rtNodes = new HashMap<String, String>();
        this.aliveNodes = new ArrayList<String>();
        this.read();
        this.calcRuntime();
    }
    
    public Map<String, String> getAllConfig() {
        final Map<String, String> ret = new HashMap<String, String>();
        final Map<String, String> ret2 = new HashMap<String, String>();
        ret.putAll(this.allConfig);
        ret.putAll(Configuration.getConf(""));
        for (final Map.Entry<String, String> e : ret.entrySet()) {
            String s = e.getValue();
            if (s.indexOf("${") > -1) {
                s = this.applyVariable(s);
            }
            ret2.put(e.getKey(), s);
        }
        return ret2;
    }
    
    public String[] getAllWork() {
        final Map<String, String> mcfg = (Map<String, String>)MapTools.getSub((Map)this.allConfig, "AEE.works");
        return MapTools.getSubKeys((Map)mcfg);
    }
    
    public String[] getAllNodes() {
        final Map<String, String> ms = (Map<String, String>)MapTools.getSub((Map)this.allConfig, "AEE.nodes");
        return MapTools.getSubKeys((Map)ms);
    }
    
    public Map<String, String> getConfig(final String prefix) {
        final Map<String, String> mcfg = (Map<String, String>)MapTools.getSub((Map)this.allConfig, prefix);
        final Map<String, String> mAEEConf = (Map<String, String>)Configuration.getConf(prefix);
        final Map<String, String> ret = new HashMap<String, String>();
        final Map<String, String> ret2 = new HashMap<String, String>();
        ret.putAll(mcfg);
        ret.putAll(mAEEConf);
        for (final Map.Entry<String, String> e : ret.entrySet()) {
            String s = e.getValue();
            if (s.indexOf("${") >= 0) {
                s = this.applyVariable(s);
            }
            ret2.put(e.getKey(), s);
        }
        return ret2;
    }
    
    public String getSingleConfig(final String it) {
        final String scfg = this.allConfig.get(it);
        final String sAEEconf = Configuration.getValue(it);
        String s = "";
        if (sAEEconf != null) {
            s = sAEEconf;
        }
        else if (scfg != null) {
            s = scfg;
        }
        if (s.indexOf("${") >= 0) {
            s = this.applyVariable(s);
        }
        return s;
    }
    
    private boolean isWorkInThisNode(final String w, final String n) {
        final String s = this.getSingleConfig("AEE.services." + w + ".node");
        if (s == null) {
            return false;
        }
        if (this.rtNodes.containsKey(w)) {
            final String isc = this.rtNodes.get(w);
            return isc.equals(n) || this.aliveNodes.contains(isc);
        }
        return false;
    }
    
    public String[] filterWorkByNode(final String[] ws) {
        if (ws == null || ws.length == 0) {
            return new String[0];
        }
        final String nodeId = AEEPlatform.getInstance().getNodeId();
        final ArrayList<String> as = new ArrayList<String>();
        for (final String w : ws) {
            if (this.isWorkInThisNode(w, nodeId)) {
                as.add(w);
            }
        }
        return as.toArray(new String[0]);
    }
    
    public String[] getWorkByFilter(final String f, final String nodeId) {
        return this.filterWorkByNode(this.getWorkByFilter(f));
    }
    
    public String[] getMyWorkByFilter(final String f) {
        return this.filterWorkByNode(this.getWorkByFilter(f));
    }
    
    public String[] getWorkByFilter(final String f) {
        final List<String> ls = new ArrayList<String>();
        if (f == null || f.length() == 0) {
            return this.getAllWork();
        }
        final String[] arr$;
        final String[] fs = arr$ = StringUtils.split(f, ",");
        for (final String s : arr$) {
            if (s.startsWith("group[")) {
                final String groups = StringUtils.substringBetween(s, "group[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByGroup(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else if (s.startsWith("g[")) {
                final String groups = StringUtils.substringBetween(s, "g[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByGroup(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else if (s.startsWith("l[")) {
                final String groups = StringUtils.substringBetween(s, "l[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByLike(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else if (s.startsWith("like[")) {
                final String groups = StringUtils.substringBetween(s, "like[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByLike(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else if (s.startsWith("node[")) {
                final String groups = StringUtils.substringBetween(s, "node[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByNode(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else if (s.startsWith("n[")) {
                final String groups = StringUtils.substringBetween(s, "n[", "]");
                final String[] groupsa = StringUtils.split(groups, ",");
                final String[] arr$2;
                final String[] sss = arr$2 = this.getWorkByNode(groupsa);
                for (final String sw : arr$2) {
                    ls.add(sw);
                }
            }
            else {
                ls.add(s.trim());
            }
        }
        Collections.sort(ls);
        final HashSet<String> hs = new HashSet<String>();
        hs.addAll(ls);
        return hs.toArray(new String[0]);
    }
    
    public String[] getWorkByGroup(final String[] g) {
        final List<String> ls = new ArrayList<String>();
        final String[] ws = this.getAllWork();
        if (g == null || g.length == 0) {
            return ws;
        }
        for (final String s : ws) {
            final String v = this.getSingleConfig("AEE.services." + s + ".group");
            if (v != null) {
                final String[] arr$2;
                final String[] gps = arr$2 = StringUtils.split(v, ",");
                for (final String gn : arr$2) {
                    for (final String gp : g) {
                        if (gp.equals(gn)) {
                            ls.add(s);
                        }
                    }
                }
            }
        }
        Collections.sort(ls);
        final HashSet<String> hs = new HashSet<String>();
        hs.addAll(ls);
        return hs.toArray(new String[0]);
    }
    
    public String[] getWorkByLike(final String[] g) {
        final List<String> ls = new ArrayList<String>();
        final String[] ws = this.getAllWork();
        if (g == null || g.length == 0) {
            return ws;
        }
        for (final String s : ws) {
            for (final String gp : g) {
                if (StringLiker.PathLiker.isLike(s, gp)) {
                    ls.add(s);
                }
            }
        }
        Collections.sort(ls);
        final HashSet<String> hs = new HashSet<String>();
        hs.addAll(ls);
        return hs.toArray(new String[0]);
    }
    
    public String[] getWorkByNode(final String[] ns) {
        final List<String> ls = new ArrayList<String>();
        final String[] ws = this.getAllWork();
        if (ns == null || ns.length == 0) {
            return ws;
        }
        for (final String s : ws) {
            final String v = this.getSingleConfig("AEE.services." + s + ".node");
            for (final String n : ns) {
                if (v.startsWith(n)) {
                    ls.add(s);
                    break;
                }
            }
        }
        Collections.sort(ls);
        final HashSet<String> hs = new HashSet<String>();
        hs.addAll(ls);
        return hs.toArray(new String[0]);
    }
    
    public Map<String, String> getWorkConfig(final String workName) {
        return this.getConfig("AEE.works." + workName);
    }
    
    public Map<String, String> getWorkConfig(final String workName, final String subitem) {
        return this.getConfig("AEE.works." + workName + "." + subitem);
    }
    
    public boolean isNodeExist(final String nodeId) {
        try {
            final String s = AEEPlatform.getInstance().getPlatformMode();
            return s == null || (!s.equals(AEEPlatform.CLIENT_PLATFORM_MODE_DIS_MASTER) && !s.equals(AEEPlatform.CLIENT_PLATFORM_MODE_MASTER)) || (this.aliveNodes.contains(nodeId) || nodeId.equals(AEEPlatform.getInstance().getNodeId()));
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public synchronized void calcRuntime() {
        final Map<String, String> nrt = new HashMap<String, String>();
        final String[] arr$;
        final String[] ws = arr$ = this.getAllWork();
        for (final String s : arr$) {
            final String v = this.getSingleConfig("AEE.services." + s + ".node");
            if (v != null) {
                final String[] arr$2;
                final String[] ns = arr$2 = StringUtils.split(v, ",");
                for (final String n : arr$2) {
                    if (this.isNodeExist(n)) {
                        nrt.put(s, n);
                        break;
                    }
                }
            }
        }
        this.rtNodes.clear();
        this.rtNodes.putAll(nrt);
    }
    
    public void read() {
        AEEConf.init();
        final String workCfgFile = Configuration.getValue("AEE_WORK_CONFIG");
        final FileConfigurationFactory fc = new FileConfigurationFactory(workCfgFile, "com.ailk.common.conf.parsetype.XML");
        fc.initConfMap();
        this.allConfig.putAll(fc.getConf(""));
    }
    
    public ArrayList<String> setAliveNode(final String nodes) {
        final Set<String> ls = new HashSet<String>();
        final String[] arr$;
        final String[] ss = arr$ = StringUtils.split(nodes, ",");
        for (final String s : arr$) {
            ls.add(s);
        }
        ls.add(AEEPlatform.getInstance().getNodeId());
        this.aliveNodes.clear();
        this.aliveNodes.addAll(ls);
        this.calcRuntime();
        return (ArrayList<String>)this.aliveNodes.clone();
    }
    
    public String getOrginNodeInfo(final String workname) {
        return this.rtNodes.get(workname);
    }
    
    static {
        AEEWorkConfig.instance = new AEEWorkConfig();
    }
}
