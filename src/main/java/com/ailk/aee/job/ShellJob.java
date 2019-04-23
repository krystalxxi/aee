// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.job;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import com.ailk.aee.core.IJobSession;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.core.Job;

@CVSID("$Id: ShellJob.java 11354 2013-07-05 06:58:15Z xiezl $")
public class ShellJob extends Job
{
    private List<String> shellCommands;
    private String shellDir;
    private Logger log;
    
    public ShellJob() {
        this.shellCommands = new ArrayList<String>();
        this.shellDir = "";
        this.log = Logger.getLogger((Class)ShellJob.class);
    }
    
    @Override
    public void execute(final IJobSession ctx) throws Exception {
        if (this.shellCommands.size() == 0) {
            throw new Exception("\u00fb\ufffd\ufffd\u05b8\ufffd\ufffdshell\ufffd\ufffd\ufffd");
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.shellCommands);
            this.log.debug((Object)this.shellDir);
        }
        File dir = null;
        if (this.shellDir != null && this.shellDir.length() > 0) {
            dir = new File(this.shellDir);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception(this.shellDir + " is not a correct directory.");
            }
        }
        final ProcessBuilder pb = new ProcessBuilder(this.shellCommands);
        if (dir != null) {
            pb.directory(dir);
        }
        pb.redirectErrorStream(true);
        final Process p = pb.start();
        final InputStream is = p.getInputStream();
        final BufferedReader br = new BufferedReader(new InputStreamReader(is), 1024);
        String str = null;
        final StringBuilder stringbuffer = new StringBuilder("");
        while ((str = br.readLine()) != null) {
            if (str.length() > 0) {
                stringbuffer.append(str).append("\n");
                if (stringbuffer.length() <= 3072) {
                    continue;
                }
                stringbuffer.delete(0, 1024);
            }
        }
        final int i = p.waitFor();
        if (i != 0) {
            this.log.error((Object)stringbuffer.toString());
            throw new Exception(stringbuffer.toString());
        }
        this.log.debug((Object)stringbuffer.toString());
    }
}
