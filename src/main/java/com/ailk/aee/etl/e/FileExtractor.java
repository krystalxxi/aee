// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import com.ailk.aee.etl.o.MapRecord;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.etl.o.ListRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.io.File;
import java.io.BufferedReader;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileExtractor.java 11528 2013-07-12 10:56:43Z xiezl $")
public class FileExtractor extends AbstractExtractor
{
    private Logger log;
    private String fieldSep;
    private String ingorePrefix;
    private boolean isHasHead;
    private boolean isHasTail;
    private String extractorFileName;
    private String logPath;
    private BufferedReader reader;
    private String thisLine;
    private String nextLine;
    private final int F_TAIL = 3;
    private final int F_ERROR = 4;
    private final int F_OK = 5;
    private boolean isTailFlag;
    private boolean isHasNextObject;
    private String logFileName;
    private File logFile;
    
    public FileExtractor() {
        this.log = Logger.getLogger((Class)FileExtractor.class);
        this.fieldSep = ",";
        this.ingorePrefix = "";
        this.isHasHead = false;
        this.isHasTail = false;
        this.extractorFileName = "";
        this.logPath = null;
        this.reader = null;
        this.thisLine = "";
        this.nextLine = "";
        this.isTailFlag = false;
        this.isHasNextObject = true;
        this.logFileName = null;
        this.logFile = null;
    }
    
    @Override
    public boolean hasNextObject() {
        return this.reader != null && this.isHasNextObject;
    }
    
    @Override
    public IBusinessObject nextObject() {
        final ListRecord lr = new ListRecord();
        final String[] arr$;
        final String[] ss = arr$ = StringUtils.splitPreserveAllTokens(this.thisLine, this.fieldSep);
        for (final String s : arr$) {
            lr.add(s);
        }
        this.readLine();
        return lr;
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
        this.logFileName = null;
        this.logFile = null;
        if (this.reader != null) {
            try {
                this.reader.close();
            }
            catch (IOException e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack((Exception)e));
            }
            this.reader = null;
        }
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.reader == null) {
            String fileName = "";
            if (this.extractorFileName != null && this.extractorFileName.trim().length() > 0) {
                fileName = this.extractorFileName.trim();
            }
            if (fileName != null && fileName.startsWith("$")) {
                final int index = fileName.indexOf("@");
                if (index > 0) {
                    String param = null;
                    final String clazzname = fileName.substring(1, index);
                    String methodname = fileName.substring(index + 1);
                    final int index2 = methodname.indexOf("(");
                    final int index3 = methodname.indexOf(")");
                    if (index2 <= 0 || index3 <= index2) {
                        throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\u02bd\ufffd\ufffd\ufffd\ufffd\u0237.");
                    }
                    param = methodname.substring(index2 + 1, index3);
                    methodname = methodname.substring(0, index2);
                    final Class<?> c = Class.forName(clazzname);
                    final Object obj = c.newInstance();
                    final Method m = c.getMethod(methodname, String.class);
                    final Object ret = m.invoke(obj, param);
                    if (ret == null) {
                        throw new Exception("invoke " + fileName + " can not return value.");
                    }
                    fileName = ret.toString();
                }
            }
            final File f = new File(fileName);
            if (!f.exists() || !f.isFile() || !f.canRead()) {
                throw new Exception("file not exist or is not file:" + fileName);
            }
            this.reader = new BufferedReader(new FileReader(f));
            this.nextLine = this.reader.readLine();
            int res = this.readLine();
            if (this.isHasHead) {
                res = this.readLine();
            }
            if (res == 4 || (res == 3 && this.isHasTail)) {
                this.isHasNextObject = false;
            }
            else {
                this.isHasNextObject = true;
            }
            this.logFileName = StringUtils.substringBeforeLast(f.getName(), ".") + ".log";
        }
    }
    
    @Override
    public void finish(final IBusinessObject o) {
        super.finish(o);
    }
    
    @Override
    public void error(final IBusinessObject o) {
        super.error(o);
        if (this.logPath == null || this.logPath.trim().length() == 0) {
            return;
        }
        synchronized (this.logFileName) {
            BufferedWriter bw = null;
            try {
                if (this.logFile == null || !this.logFile.exists()) {
                    final File temp = new File(this.logPath.trim());
                    if (!temp.exists() || !temp.isDirectory()) {
                        this.log.error((Object)(this.logPath + " is not correct dir."));
                        return;
                    }
                    (this.logFile = new File(temp, this.logFileName)).createNewFile();
                }
                final FileOutputStream fos = new FileOutputStream(this.logFile, true);
                bw = new BufferedWriter(new OutputStreamWriter(fos));
                if (o instanceof MapRecord) {
                    final MapRecord mr = (MapRecord)o;
                    final StringBuilder sb = new StringBuilder();
                    for (final Map.Entry<String, String> entry : mr.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append(this.fieldSep);
                    }
                    sb.delete(sb.length() - this.fieldSep.length(), sb.length());
                    bw.write(sb.toString());
                    sb.delete(0, sb.length());
                    bw.newLine();
                    bw.flush();
                }
                bw.close();
                bw = null;
            }
            catch (Exception e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack(e));
                try {
                    if (bw != null) {
                        bw.close();
                        bw = null;
                    }
                }
                catch (Exception e1) {
                    this.log.error((Object)ExceptionUtils.getExceptionStack(e1));
                }
            }
            finally {
                try {
                    if (bw != null) {
                        bw.close();
                        bw = null;
                    }
                }
                catch (Exception e2) {
                    this.log.error((Object)ExceptionUtils.getExceptionStack(e2));
                }
            }
        }
    }
    
    public int readLine() {
        try {
            if (this.isTailFlag) {
                this.isHasNextObject = false;
                return 5;
            }
            this.thisLine = this.nextLine;
            this.nextLine = this.reader.readLine();
            if (this.nextLine == null) {
                if (this.isHasTail) {
                    this.isHasNextObject = false;
                }
                this.isTailFlag = true;
                return 3;
            }
            if (this.ingorePrefix != null && !this.ingorePrefix.equals("") && this.thisLine.startsWith(this.ingorePrefix)) {
                return this.readLine();
            }
            if (this.thisLine.trim().equals("")) {
                return this.readLine();
            }
        }
        catch (Exception e) {
            try {
                return 4;
            }
            finally {
                if (this.reader != null) {
                    try {
                        this.reader.close();
                    }
                    catch (IOException e2) {
                        this.log.error((Object)ExceptionUtils.getExceptionStack(e));
                    }
                }
                this.reader = null;
            }
        }
        return 5;
    }
}
