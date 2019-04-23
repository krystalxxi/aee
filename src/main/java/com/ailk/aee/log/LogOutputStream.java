// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.log;

import java.util.Arrays;
import java.io.IOException;
import java.io.PrintStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.io.OutputStream;

@CVSID("$Id: LogOutputStream.java 60270 2013-11-03 14:48:37Z tangxy $")
public class LogOutputStream extends OutputStream
{
    public boolean isOut;
    protected byte[] buf;
    protected int count;
    
    public static void redirectStdStream() {
        System.setOut(new PrintStream(new LogOutputStream(true)));
        System.setErr(new PrintStream(new LogOutputStream(false)));
    }
    
    public LogOutputStream(final boolean lcat) {
        this.isOut = true;
        this.buf = new byte[1024];
        this.isOut = lcat;
    }
    
    @Override
    public synchronized void flush() throws IOException {
        final String s = new String(this.buf);
        if (this.isOut) {
            LogUtils.logOut(s);
        }
        else {
            LogUtils.logErr(s);
        }
        super.flush();
    }
    
    @Override
    public synchronized void write(final int b) throws IOException {
        final int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newcount));
        }
        this.buf[this.count] = (byte)b;
        this.count = newcount;
    }
}
