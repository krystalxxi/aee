// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.g;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.io.File;
import com.ailk.aee.etl.job.ETLJob;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileSequenceStringGenerator.java 11039 2013-06-13 01:44:38Z xiezl $")
public class FileSequenceStringGenerator extends AbstractStringGenerator
{
    private String filePath;
    private String pattern;
    private long max;
    private long min;
    private long incr;
    
    public FileSequenceStringGenerator() {
        this.filePath = "";
        this.pattern = "";
        this.max = Long.MAX_VALUE;
        this.min = 0L;
        this.incr = 1L;
    }
    
    @Override
    public String genString(final ETLJob job) {
        try {
            final File f = new File(this.filePath);
            if (!f.exists()) {
                f.createNewFile();
                this.writeFile(f, this.min);
            }
            final long l = this.readFile(f);
            long newl = l + this.incr;
            if (l >= this.max) {
                newl = this.min;
            }
            this.writeFile(f, newl);
            if (this.pattern == null || this.pattern.equals("")) {
                return Long.toString(newl);
            }
            final DecimalFormat df = new DecimalFormat(this.pattern);
            return df.format(newl);
        }
        catch (Exception e) {
            e.printStackTrace();
            final long newl2 = this.min;
            if (this.pattern == null || this.pattern.equals("")) {
                return Long.toString(newl2);
            }
            final DecimalFormat df2 = new DecimalFormat(this.pattern);
            return df2.format(newl2);
        }
    }
    
    private long readFile(final File f) throws Exception {
        final BufferedReader br = new BufferedReader(new FileReader(f));
        String s = br.readLine();
        s = s.trim();
        s = s.replace("\r", "");
        s = s.replace("\n", "");
        br.close();
        return Long.parseLong(s);
    }
    
    private void writeFile(final File f, final long l) throws Exception {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("" + l + "\r\n");
        bw.close();
    }
}
