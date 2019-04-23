// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ClassPathUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ClassPathUtils
{
    public static String getCurrentClassPath() {
        final RuntimeMXBean rbean = ManagementFactory.getRuntimeMXBean();
        final StringBuffer sb = new StringBuffer();
        sb.append(rbean.getClassPath()).append("");
        return sb.toString();
    }
    
    public static String[] getCurrentClassPathList() {
        final String v = getCurrentClassPath();
        return StringUtils.split(v, File.pathSeparatorChar);
    }
    
    public static String[] getFileListFromClassPath(final String fileNamePath) {
        final List<String> al = new ArrayList<String>();
        final String[] arr$;
        final String[] scp = arr$ = getCurrentClassPathList();
        for (final String cp : arr$) {
            final File f = new File(cp);
            if (f.exists()) {
                if (f.isDirectory()) {
                    final File nf = new File(f.getAbsolutePath() + File.separator + fileNamePath);
                    if (nf.exists()) {
                        al.add(nf.getAbsolutePath());
                    }
                }
                else {
                    final String uri = "jar:file" + f.getAbsolutePath() + "!/" + fileNamePath;
                    if (JarFile.isJarFileExists(uri)) {
                        al.add(uri);
                    }
                }
            }
        }
        return al.toArray(new String[0]);
    }
}
