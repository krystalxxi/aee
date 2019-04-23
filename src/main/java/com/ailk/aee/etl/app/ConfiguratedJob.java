// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.app;

import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.core.IJobSession;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.stringobject.StringListConverter;
import com.ailk.aee.common.conf.MapTools;
import com.ailk.aee.common.conf.FileConfigurationFactory;
import java.io.FileInputStream;
import java.io.File;
import com.ailk.aee.common.util.JarFile;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: ConfiguratedJob.java 11504 2013-07-11 10:00:20Z xiezl $")
public class ConfiguratedJob extends Job
{
    private Logger log;
    private Job innerJob;
    private Map<String, String> config;
    private String myConfigPath;
    private Class<?> loadedClass;
    
    public ConfiguratedJob(final Class<? extends Job> j) {
        this.log = Logger.getLogger((Class)ConfiguratedJob.class);
        this.config = new HashMap<String, String>();
        this.myConfigPath = "";
        this.loadedClass = null;
        this.myConfigPath = "resource:" + StringUtils.replace(j.getCanonicalName(), ".", "/") + ".jobwrapper.xml";
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("myConfigPath(Class<? extends Job>):" + this.myConfigPath));
        }
        this.loadedClass = j;
    }
    
    public ConfiguratedJob(final String path) {
        this.log = Logger.getLogger((Class)ConfiguratedJob.class);
        this.config = new HashMap<String, String>();
        this.myConfigPath = "";
        this.loadedClass = null;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("myConfigPath:" + path));
        }
        this.myConfigPath = path;
    }
    
    public ConfiguratedJob(final Class<?> c, final String path) {
        this.log = Logger.getLogger((Class)ConfiguratedJob.class);
        this.config = new HashMap<String, String>();
        this.myConfigPath = "";
        this.loadedClass = null;
        this.loadedClass = c;
        this.myConfigPath = path;
    }
    
    private void wrap() throws Exception {
        InputStream is = null;
        if (this.myConfigPath.startsWith("resource:")) {
            if (this.loadedClass == null) {
                is = this.getClass().getClassLoader().getResourceAsStream(this.myConfigPath.substring("resource:".length()));
            }
            else {
                final String s = this.myConfigPath.substring("resource:".length());
                is = this.loadedClass.getClassLoader().getResourceAsStream(s);
            }
        }
        else if (this.myConfigPath.startsWith("jar:file")) {
            is = JarFile.getJarFileInputStream(this.myConfigPath);
        }
        else {
            is = new FileInputStream(new File(this.myConfigPath));
        }
        if (is != null) {
            final Map<String, String> mx = (Map<String, String>)FileConfigurationFactory.parseFile(is, "com.ailk.common.conf.parsetype.XML");
            final Map<String, String> jobmap = (Map<String, String>)MapTools.getSub((Map)mx, "jobwrapper.job");
            final Map<String, String> conmap = (Map<String, String>)MapTools.getSub((Map)mx, "jobwrapper.converter");
            final Map<String, String> config2 = new HashMap<String, String>();
            for (final Map.Entry<String, String> p : this.config.entrySet()) {
                config2.put(p.getKey(), p.getValue());
            }
            final StringListConverter slc = new StringListConverter();
            for (final Map.Entry<String, String> p2 : conmap.entrySet()) {
                if (config2.containsKey(p2.getKey())) {
                    final String value = p2.getValue();
                    if (slc.canWrapFromString(value)) {
                        final List<String> temp_list = (List<String>)slc.wrapFromString(value);
                        for (final String temp : temp_list) {
                            jobmap.put(temp, config2.get(p2.getKey()));
                        }
                    }
                    else {
                        jobmap.put(value, config2.get(p2.getKey()));
                    }
                }
            }
            final String jobclass = mx.get("jobwrapper.job");
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("--------------------job:" + jobclass));
                this.log.debug((Object)("\n" + MapTools.mapToString((Map)jobmap)));
            }
            this.innerJob = (Job)ObjectBuilder.build((Class)Job.class, jobclass, (Map)jobmap);
            return;
        }
        throw new Exception("\ufffd\u07b7\ufffd\ufffd\u04b5\ufffdConfiguredJob\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffd@" + this.myConfigPath);
    }
    
    public void dealException(final IJobSession ctx, final Exception e) {
        this.innerJob.dealException(ctx, e);
    }
    
    public void finalizeJob() {
        this.innerJob.finalizeJob();
    }
    
    public void finish(final IJobSession ctx) throws Exception {
        this.innerJob.finish(ctx);
    }
    
    public void initializeJob(final Map<String, String> m) {
        super.initializeJob((Map)m);
        this.config.putAll(m);
        try {
            this.wrap();
        }
        catch (Exception e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack(e));
            return;
        }
        this.innerJob.initializeJob((Map)m);
    }
    
    public boolean prepare(final IJobSession ctx) throws Exception {
        return this.innerJob.prepare(ctx);
    }
    
    public void execute(final IJobSession ctx) throws Exception {
        this.innerJob.execute(ctx);
    }
}
