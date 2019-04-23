// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.l;

import java.util.HashMap;
import java.util.regex.Pattern;
import com.ailk.aee.common.util.StringUtils;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Map;
import java.io.BufferedWriter;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: FileWriterUtil.java 11053 2013-06-17 01:22:42Z xiezl $")
public class FileWriterUtil
{
    private BufferedWriter fileWriter;
    private String separator;
    private static final Map<String, String> FILE_SEPARATOR_MAP;
    private static boolean AVOID_SCIENCE_COUNT_TO_EXCEL;
    
    public FileWriterUtil(final OutputStream outputStream) {
        this.separator = ",";
        this.fileWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
    }
    
    public FileWriterUtil(final OutputStream outputStream, final String fileType) {
        this(outputStream);
        this.setSeparatorByFileType(fileType);
    }
    
    public void setSeparator(final String s) {
        this.separator = s;
    }
    
    public void writeString(final String strValue) throws Exception {
        this.fileWriter.write(this.checkString(strValue));
    }
    
    public void writeStringAndMoveToNextCell(final String strValue) throws Exception {
        this.writeString(strValue);
        this.fileWriter.write(this.separator);
    }
    
    public void writeStringAndNewLine(final String strValue) throws Exception {
        this.writeString(strValue);
        this.fileWriter.newLine();
    }
    
    public void writeStringArray(final String[] strValues) throws Exception {
        if (strValues == null || strValues.length == 0) {
            return;
        }
        for (int i = 0; i < strValues.length; ++i) {
            strValues[i] = this.checkString(strValues[i]);
        }
        final String completContent = StringUtils.join((Object[])strValues, this.separator);
        this.fileWriter.write(new StringBuilder().append(completContent).toString());
    }
    
    public void newLine() throws Exception {
        this.fileWriter.newLine();
    }
    
    public void save() throws Exception {
        this.fileWriter.flush();
    }
    
    public void close() throws Exception {
        this.fileWriter.close();
    }
    
    public void setSeparatorByFileType(final String fileType) {
        this.separator = FileWriterUtil.FILE_SEPARATOR_MAP.get(fileType);
    }
    
    private String checkString(String strValue) {
        if (StringUtils.isNotBlank((CharSequence)strValue)) {
            if (strValue.indexOf(10) >= 0 || (StringUtils.isNotBlank((CharSequence)this.separator) && strValue.indexOf(this.separator) >= 0)) {
                strValue = "\"" + strValue + "\"";
            }
            if (FileWriterUtil.AVOID_SCIENCE_COUNT_TO_EXCEL && strValue.length() > 6 && this.isNumeric(strValue)) {
                strValue = "\"" + strValue + "\"\t";
            }
        }
        else {
            strValue = "";
        }
        return strValue;
    }
    
    private boolean isNumeric(final String str) {
        final Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    
    static {
        FILE_SEPARATOR_MAP = new HashMap<String, String>();
        FileWriterUtil.AVOID_SCIENCE_COUNT_TO_EXCEL = false;
        FileWriterUtil.FILE_SEPARATOR_MAP.put("xls", "\t");
        FileWriterUtil.FILE_SEPARATOR_MAP.put("csv", ",");
        FileWriterUtil.FILE_SEPARATOR_MAP.put("txt", "\t\t");
    }
}
