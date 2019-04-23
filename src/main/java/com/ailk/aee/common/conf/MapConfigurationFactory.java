// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Iterator;
import com.ailk.aee.common.util.StringUtils;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import com.ailk.aee.common.util.StringLiker;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MapConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MapConfigurationFactory extends ConfigurationFactory
{
    protected Map<String, String> conf;
    
    public MapConfigurationFactory() {
        this.conf = null;
        this.setSupportDump(true);
    }
    
    @Override
    public String dump(final String[] args) {
        if (this.conf == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        int kl = 0;
        int vl = 0;
        boolean isShow = false;
        final StringLiker pl = StringLiker.PathLiker;
        final StringLiker dl = StringLiker.DBLiker;
        final ArrayList<String> al = new ArrayList<String>();
        for (final Map.Entry<String, String> e : this.conf.entrySet()) {
            if (args != null && args.length > 0) {
                isShow = false;
                for (final String sp : args) {
                    if (pl.isLike(e.getKey(), sp) || dl.isLike(e.getKey(), sp)) {
                        isShow = true;
                    }
                }
            }
            else {
                isShow = true;
            }
            if (isShow) {
                if (e.getKey().length() > kl) {
                    kl = e.getKey().length();
                }
                if (e.getValue().length() > vl) {
                    vl = e.getValue().length();
                }
                al.add(e.getKey());
            }
        }
        Collections.sort(al);
        for (final String s : al) {
            final String c = this.conf.get(s);
            sb.append(StringUtils.rightPad(s, kl, " ") + " = " + c);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public Map<String, String> getConf(final String prefix, final boolean saveprefix) {
        if (prefix == null || prefix.trim().equals("")) {
            return this.conf;
        }
        if (this.conf == null) {
            return null;
        }
        return MapTools.getSub(this.conf, prefix, saveprefix);
    }
    
    @Override
    public String getFactoryName() {
        return "local Map Configuration Factory";
    }
    
    @Override
    public void init() {
    }
    
    public void setConf(final Map<String, String> conf) {
        this.conf = conf;
    }
}
