// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.database.sp;

import com.ailk.aee.common.conf.MapTools;
import java.io.InputStream;
import java.io.FileInputStream;
import com.ailk.aee.common.conf.FileConfigurationFactory;
import java.io.File;
import com.ailk.aee.common.util.StringUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.common.sp.AbstractServiceProvider;

@CVSID("$Id: DBConnectionNonPoolServiceProvider.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DBConnectionNonPoolServiceProvider extends AbstractServiceProvider
{
    private Map<String, Map<String, String>> confs;
    
    public DBConnectionNonPoolServiceProvider() {
        this.confs = new HashMap<String, Map<String, String>>();
    }
    
    public Object getService(final String dn) {
        if (this.confs.containsKey(dn)) {
            try {
                final Map<String, String> v = this.confs.get(dn);
                Class.forName(v.get("driverClassName"));
                final Connection conn = DriverManager.getConnection(v.get("url"), v.get("username"), v.get("password"));
                return conn;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public int getDefaultMapValue(final String name, final Map<String, String> v, final int defv) {
        if (!v.containsKey(name)) {
            return defv;
        }
        final String s = v.get(name);
        if (StringUtils.isNumeric((CharSequence)s)) {
            return Integer.parseInt(s);
        }
        return defv;
    }
    
    public void build(final Map<String, String> arg) throws Exception {
        final String configFile = arg.get("configfile");
        if (configFile == null) {
            throw new Exception("\u00fb\ufffd\ufffd\u05b8\ufffd\ufffdconfigfile");
        }
        final File f = new File(configFile);
        if (!f.exists()) {
            throw new Exception("\ufffd\u013c\ufffd" + configFile + "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
        }
        final FileConfigurationFactory fcf = new FileConfigurationFactory();
        final Map<String, String> mx = (Map<String, String>)fcf.parseInputStream((InputStream)new FileInputStream(f), "com.ailk.common.conf.parsetype.XML");
        final Map<String, String> m = (Map<String, String>)MapTools.getSub((Map)mx, "dbconfig");
        final String[] arr$;
        final String[] dbs = arr$ = MapTools.getSubKeys((Map)m);
        for (final String s : arr$) {
            final Map<String, String> ms = (Map<String, String>)MapTools.getSub((Map)m, s);
            if (ms.containsKey("alias") && ms.get("alias").trim().length() > 0) {
                final String alias = ms.get("alias");
                final String[] arr$2;
                final String[] aliass = arr$2 = StringUtils.split(alias, ",");
                for (final String s2 : arr$2) {
                    this.confs.put(s2.trim(), ms);
                }
            }
            this.confs.put(s, ms);
        }
    }
}
