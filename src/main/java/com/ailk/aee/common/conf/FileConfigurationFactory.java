// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.util.Iterator;
import com.ailk.aee.common.conf.util.ConfFileInputStreamSearcher;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import com.ailk.aee.common.util.JarFile;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import com.ailk.aee.common.conf.util.AbstractInputStreamSearcher;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class FileConfigurationFactory extends IntelligentSearchInputStreamConfigurationFactory
{
    protected String loc;
    private List<AbstractInputStreamSearcher> searchers;
    private String fileName;
    
    private static String guessParseType(final String uri) {
        if (uri.endsWith(".prop") || uri.endsWith(".properties")) {
            return "com.ailk.common.conf.parsetype.prop";
        }
        if (uri.endsWith(".ini")) {
            return "com.ailk.common.conf.parsetype.ini";
        }
        return "com.ailk.common.conf.parsetype.XML";
    }
    
    public static Map<String, String> parseFile(final InputStream is, final String parseType) {
        if (is == null) {
            return new HashMap<String, String>();
        }
        final FileConfigurationFactory fcf = new FileConfigurationFactory();
        final Map<String, String> mx = fcf.parseInputStream(is, parseType);
        if (mx == null) {
            return new HashMap<String, String>();
        }
        return mx;
    }
    
    public static Map<String, String> parseFile(final String uri) {
        return parseFile(uri, guessParseType(uri));
    }
    
    public static Map<String, String> parseFile(final String uri, final String parseType) {
        InputStream is = null;
        if (uri.startsWith("jar:file")) {
            int pling = -1;
            if (uri.startsWith("jar:file") && (pling = uri.indexOf("!/")) > -1) {
                final String jarName = uri.substring("jar:".length(), pling);
                final String zipFileName = JarFile.fromURIJava13(jarName);
                final File f = new File(zipFileName);
                if (!f.exists() || !f.canRead()) {}
            }
            is = JarFile.getJarFileInputStream(uri);
        }
        else if (uri.startsWith("http://")) {
            try {
                final URL u = new URL(uri);
                is = u.openStream();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        else {
            final File f2 = new File(uri);
            if (f2.exists() && f2.canRead()) {
                try {
                    is = new FileInputStream(f2);
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
        return parseFile(is, parseType);
    }
    
    public FileConfigurationFactory() {
        this.loc = "";
        (this.searchers = new ArrayList<AbstractInputStreamSearcher>()).add(new ConfFileInputStreamSearcher(""));
        this.searchers.add(new ConfFileInputStreamSearcher("." + File.separator + ""));
        this.searchers.add(new ConfFileInputStreamSearcher("." + File.separator + "etc"));
        this.searchers.add(new ConfFileInputStreamSearcher("." + File.separator + "cfg"));
        this.searchers.add(new ConfFileInputStreamSearcher("." + File.separator + "config"));
        this.searchers.add(new ConfFileInputStreamSearcher("." + File.separator + "conf"));
        this.searchers.add(new ConfFileInputStreamSearcher(".." + File.separator + ""));
        this.searchers.add(new ConfFileInputStreamSearcher(".." + File.separator + "etc"));
        this.searchers.add(new ConfFileInputStreamSearcher(".." + File.separator + "cfg"));
        this.searchers.add(new ConfFileInputStreamSearcher(".." + File.separator + "config"));
        this.searchers.add(new ConfFileInputStreamSearcher(".." + File.separator + "conf"));
        final String currusr = System.getProperty("user.dir");
        this.searchers.add(new ConfFileInputStreamSearcher(currusr + File.separator));
        this.searchers.add(new ConfFileInputStreamSearcher(currusr + File.separator + "etc"));
        this.searchers.add(new ConfFileInputStreamSearcher(currusr + File.separator + "cfg"));
        this.searchers.add(new ConfFileInputStreamSearcher(currusr + File.separator + "config"));
        this.searchers.add(new ConfFileInputStreamSearcher(currusr + File.separator + "conf"));
    }
    
    public FileConfigurationFactory(final String fileName, final String parseType) {
        this();
        this.fileName = fileName;
        this.setParseType(parseType);
    }
    
    public void addInputStreamSearch(final AbstractInputStreamSearcher aiss) {
        this.searchers.add(aiss);
    }
    
    @Override
    public String getFactoryName() {
        if (this.getLocation().equals("")) {
            return "FileConfiguration From File:" + this.fileName + " but not found,Configuration ignore";
        }
        return "FileConfiguration From File:" + this.fileName + " real file is :" + this.loc + super.getFactoryName();
    }
    
    public String getLocation() {
        return this.loc;
    }
    
    @Override
    public InputStream search() {
        for (final AbstractInputStreamSearcher iss : this.searchers) {
            final InputStream is = iss.search(this.fileName);
            this.loc = iss.getLocation();
            if (is != null) {
                return is;
            }
        }
        return null;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
}
