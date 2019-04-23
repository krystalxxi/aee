// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConfFileInputStreamSearcher.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConfFileInputStreamSearcher extends AbstractInputStreamSearcher
{
    private String basePath;
    
    public ConfFileInputStreamSearcher(final String basePath) {
        this.basePath = "";
        this.basePath = basePath;
    }
    
    @Override
    public InputStream search(final String conf) {
        if (!this.basePath.equals("")) {
            final File f = new File(this.basePath);
            if (f.exists() && f.isDirectory() && f.canRead()) {
                final File fo = new File(f.getAbsolutePath() + File.separator + conf);
                if (fo.exists() && fo.canRead() && fo.isFile()) {
                    try {
                        super.setLocation(fo.getAbsolutePath());
                        return new FileInputStream(fo);
                    }
                    catch (FileNotFoundException e) {
                        return null;
                    }
                }
            }
        }
        else {
            final File fo2 = new File(conf);
            if (fo2.exists() && fo2.canRead()) {
                try {
                    super.setLocation(fo2.getAbsolutePath());
                    return new FileInputStream(fo2);
                }
                catch (FileNotFoundException e2) {
                    return null;
                }
            }
        }
        return null;
    }
}
