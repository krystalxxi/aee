// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

public class MyFTPEntryParser extends ConfigurableFTPFileEntryParserImpl
{
    private Class clazz;
    
    public MyFTPEntryParser() {
        this("");
    }
    
    public MyFTPEntryParser(final String regex) {
        super("");
        this.clazz = MyFTPEntryParser.class;
    }
    
    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(this.clazz.getPackage().getName() + this.clazz.getSimpleName(), "", "", "", "", "");
    }
    
    public FTPFile parseFTPEntry(final String entry) {
        final FTPFile file = new FTPFile();
        file.setRawListing(entry);
        final String[] temp = entry.split("\\s+");
        final String fileType = temp[0].substring(0, 1);
        if ("d".equals(fileType)) {
            file.setType(1);
        }
        else {
            file.setType(0);
        }
        file.setName(temp[temp.length - 1]);
        return file;
    }
    
    public static void main(final String[] args) {
        new MyFTPEntryParser().parseFTPEntry("-rwxrwxrwx    1 billing  ngboss           15  1\ufffd\ufffd13 10\u02b145 CFSM_tt3");
    }
}
