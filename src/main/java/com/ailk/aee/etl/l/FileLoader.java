// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileLoader.java 11053 2013-06-17 01:22:42Z xiezl $")
public class FileLoader extends AbstractLoader
{
    private String filePath;
    private String fileName;
    private boolean isRar;
    private String fileType;
    private String fieldSeparator;
    private List<String> titles;
    private List<String> fields;
    private Logger log;
    private FileWriterUtil writer;
    List<String> temp;
    boolean isFirstLine;
    
    public FileLoader() {
        this.filePath = "";
        this.fileName = "";
        this.isRar = true;
        this.fileType = "txt";
        this.fieldSeparator = ",";
        this.titles = null;
        this.fields = null;
        this.log = Logger.getLogger((Class)FileLoader.class);
        this.writer = null;
        this.temp = new ArrayList<String>();
        this.isFirstLine = true;
    }
    
    @Override
    public boolean loadObject(final IBusinessObject o) {
        if (o instanceof MapRecord) {
            final Map<String, String> mo = (MapRecord)o;
            this.temp.clear();
            for (final String key : this.fields) {
                if (key != null) {
                    final String value = mo.get(key);
                    if (value != null) {
                        this.temp.add(value);
                    }
                    else {
                        this.temp.add("");
                    }
                }
                else {
                    this.temp.add("");
                }
            }
            try {
                if (!this.isFirstLine) {
                    this.writer.newLine();
                }
                this.writer.writeStringArray(this.temp.toArray(new String[0]));
                if (this.isFirstLine) {
                    this.isFirstLine = false;
                }
            }
            catch (Exception e) {
                this.log.error((Object)ExceptionUtils.getExceptionStack(e));
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        if (this.fileName != null && this.fileName.startsWith("$")) {
            final int index = this.fileName.indexOf("@");
            if (index <= 0) {
                throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\u02bd\ufffd\ufffd\ufffd\ufffd\u0237.");
            }
            String param = null;
            final String clazzname = this.fileName.substring(1, index);
            String methodname = this.fileName.substring(index + 1);
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
                throw new Exception("invoke " + this.fileName + " can not return value.");
            }
            this.fileName = ret.toString();
        }
        if (this.fields == null || this.fields.size() == 0) {
            throw new Exception("columns is not correct.");
        }
        this.fileType = this.fileType.toLowerCase();
        if (!"txt".equals(this.fileType) && !"csv".equals(this.fileType)) {
            this.fileType = "txt";
        }
        final File createFilePath = new File(this.filePath);
        if (!createFilePath.exists() || !createFilePath.isDirectory()) {
            throw new Exception(this.filePath + " is not correct directory.");
        }
        File createFile = null;
        if (this.isRar) {
            createFile = new File(createFilePath, this.fileName + ".rar");
        }
        else {
            createFile = new File(createFilePath, this.fileName + "." + this.fileType);
        }
        if (createFile.exists() && createFile.isFile()) {
            createFile.delete();
        }
        createFile.createNewFile();
        final FileOutputStream fileOutputStream = new FileOutputStream(createFile);
        OutputStream finalOutputStream = null;
        if (this.isRar) {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            final ZipEntry entry = new ZipEntry("ExportResult." + this.fileType);
            zipOutputStream.putNextEntry(entry);
            finalOutputStream = zipOutputStream;
        }
        else {
            finalOutputStream = fileOutputStream;
        }
        this.writer = new FileWriterUtil(finalOutputStream, this.fileType);
        if ("txt".equals(this.fileType) && this.fieldSeparator != null && this.fieldSeparator.length() > 0) {
            this.writer.setSeparator(this.fieldSeparator);
        }
        if (this.titles != null && this.titles.size() > 0) {
            this.writer.writeStringArray(this.titles.toArray(new String[0]));
            this.isFirstLine = false;
        }
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
        if (this.writer != null) {
            this.writer.save();
            this.writer.close();
        }
    }
}
