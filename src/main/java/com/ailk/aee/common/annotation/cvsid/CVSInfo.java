// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.annotation.cvsid;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@CVSID("$Id: CVSInfo.java 60270 2013-11-03 14:48:37Z tangxy $")
public class CVSInfo
{
    private final String fileName;
    private final Date lastModifyDate;
    private final String lastModifyUserName;
    private final String version;
    
    public static CVSInfo parse(String s) {
        boolean isSVN = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (s == null || s.trim().equals("")) {
            return null;
        }
        final int index = s.indexOf(",");
        if (index <= 0) {
            isSVN = true;
        }
        String fileName;
        String version;
        String moddate;
        String user;
        if (isSVN) {
            final List<Object> list = CVSTool.getTokens(s, " ", false);
            fileName = list.get(0).toString();
            version = list.get(1).toString();
            moddate = list.get(2).toString() + " " + list.get(3);
            user = list.get(4).toString();
        }
        else {
            fileName = s.substring(0, index);
            s = s.substring(index);
            final List<Object> list = CVSTool.getTokens(s, " ", false);
            if (list.size() < 5) {
                return null;
            }
            version = list.get(1).toString();
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            moddate = list.get(2).toString() + " " + list.get(3);
            user = list.get(4).toString();
        }
        Date lastModifyDate = new Date();
        try {
            lastModifyDate = sdf.parse(moddate.substring(0, 19));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        final CVSInfo info = new CVSInfo(fileName, lastModifyDate, user, version);
        return info;
    }
    
    public CVSInfo(final String fileName, final Date lastModifyDate, final String lastModifyUserName, final String version) {
        this.fileName = fileName;
        this.lastModifyDate = lastModifyDate;
        this.lastModifyUserName = lastModifyUserName;
        this.version = version;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public Date getLastModifyDate() {
        return this.lastModifyDate;
    }
    
    public String getLastModifyUserName() {
        return this.lastModifyUserName;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        sb.append("{LastChangedBy:\"").append(this.getLastModifyUserName()).append("\",").append(" ");
        sb.append("Revision:\"").append(this.getVersion()).append("\",").append(" ");
        sb.append("LastChangedDate:\"").append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(this.getLastModifyDate())).append("\",").append(" ");
        sb.append("FileName:\"").append(this.getFileName()).append("\"}");
        return sb.toString();
    }
}
