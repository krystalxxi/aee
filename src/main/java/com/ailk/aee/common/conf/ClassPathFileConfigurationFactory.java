// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import com.ailk.aee.common.util.ClassPathUtils;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.ailk.aee.common.conf.util.XMLInputStreamParser;
import com.ailk.aee.common.util.JarFile;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ClassPathFileConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ClassPathFileConfigurationFactory extends MapConfigurationFactory
{
    private String filePathName;
    private boolean searchAll;
    
    public ClassPathFileConfigurationFactory(final String filePathName) {
        this.filePathName = "";
        this.searchAll = true;
        this.filePathName = filePathName;
    }
    
    public ClassPathFileConfigurationFactory(final String filePathName, final boolean isSearchAll) {
        this.filePathName = "";
        this.searchAll = true;
        this.searchAll = isSearchAll;
    }
    
    private Map<String, String> addFileConf(final String uri) throws Exception {
        Map<String, String> ms = new HashMap<String, String>();
        if (uri.startsWith("jar:file")) {
            ms = XMLInputStreamParser.parseFile(JarFile.getJarFileInputStream(uri));
        }
        else {
            try {
                ms = XMLInputStreamParser.parseFile(new FileInputStream(new File(uri)));
            }
            catch (FileNotFoundException ex) {}
        }
        return ms;
    }
    
    @Override
    public String getFactoryName() {
        String s = "ClassPathConfigurationFactory of " + this.filePathName + ",found@";
        final String[] allPath = ClassPathUtils.getFileListFromClassPath(this.filePathName);
        if (allPath != null && allPath.length > 0) {
            if (!this.searchAll) {
                s = s + "[" + allPath[0] + "]";
            }
            else {
                for (int i = allPath.length - 1; i >= 0; --i) {
                    s = s + "[" + allPath[i] + "] ";
                }
            }
        }
        else {
            s += "[nothing]";
        }
        return s;
    }
    
    @Override
    public void init() {
        try {
            super.init();
            final String[] allPath = ClassPathUtils.getFileListFromClassPath(this.filePathName);
            if (allPath != null && allPath.length > 0) {
                if (!this.searchAll) {
                    this.conf.putAll(this.addFileConf(allPath[0]));
                    return;
                }
                final Map<String, String> mall = new HashMap<String, String>();
                for (int i = allPath.length - 1; i >= 0; --i) {
                    mall.putAll(this.addFileConf(allPath[i]));
                }
                if (this.conf == null) {
                    this.conf = new HashMap<String, String>();
                }
                this.conf.putAll(mall);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
