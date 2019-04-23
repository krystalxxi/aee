// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.ByteArrayOutputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: JarFile.java 60270 2013-11-03 14:48:37Z tangxy $")
public class JarFile
{
    private static final int WORD = 16;
    public static final String URI_ENCODING = "UTF-8";
    private static final int NIBBLE = 4;
    
    public static String decodeUri(final String uri) throws UnsupportedEncodingException {
        if (uri.indexOf(37) == -1) {
            return uri;
        }
        final ByteArrayOutputStream sb = new ByteArrayOutputStream(uri.length());
        final CharacterIterator iter = new StringCharacterIterator(uri);
        for (char c = iter.first(); c != '\uffff'; c = iter.next()) {
            if (c == '%') {
                final char c2 = iter.next();
                if (c2 != '\uffff') {
                    final int i1 = Character.digit(c2, 16);
                    final char c3 = iter.next();
                    if (c3 != '\uffff') {
                        final int i2 = Character.digit(c3, 16);
                        sb.write((char)((i1 << 4) + i2));
                    }
                }
            }
            else if (c >= '\0' && c < '\u0080') {
                sb.write(c);
            }
            else {
                final byte[] bytes = String.valueOf(c).getBytes("UTF-8");
                sb.write(bytes, 0, bytes.length);
            }
        }
        return sb.toString("UTF-8");
    }
    
    public static String fromURIJava13(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        }
        catch (MalformedURLException ex) {}
        if (url == null || !"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("not valid file uri" + uri);
        }
        final StringBuffer buf = new StringBuffer(url.getHost());
        if (buf.length() > 0) {
            buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }
        final String file = url.getFile();
        final int queryPos = file.indexOf(63);
        buf.append((queryPos < 0) ? file : file.substring(0, queryPos));
        uri = buf.toString().replace('/', File.separatorChar);
        if (File.pathSeparatorChar == ';' && uri.startsWith("\\") && uri.length() > 2 && Character.isLetter(uri.charAt(1)) && uri.lastIndexOf(58) > -1) {
            uri = uri.substring(1);
        }
        String path = null;
        try {
            path = decodeUri(uri);
            final String cwd = System.getProperty("user.dir");
            final int posi = cwd.indexOf(58);
            final boolean pathStartsWithFileSeparator = path.startsWith(File.separator);
            final boolean pathStartsWithUNC = path.startsWith("" + File.separator + File.separator);
            if (posi > 0 && pathStartsWithFileSeparator && !pathStartsWithUNC) {
                path = cwd.substring(0, posi + 1) + path;
            }
        }
        catch (UnsupportedEncodingException exc) {
            throw new IllegalStateException("Could not convert URI " + uri + " to path: " + exc.getMessage());
        }
        return path;
    }
    
    public static InputStream getJarFileInputStream(final String uri) {
        try {
            int pling = -1;
            if (uri.startsWith("jar:file") && (pling = uri.indexOf("!/")) > -1) {
                InputStream is = null;
                final String jarName = uri.substring("jar:".length(), pling);
                final String zipFileName = fromURIJava13(jarName);
                final ZipFile zf = new ZipFile(zipFileName);
                is = zf.getInputStream(zf.getEntry(uri.substring(pling + 2)));
                if (is != null) {
                    return is;
                }
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }
    
    public static boolean isJarFileExists(final String uri) {
        try {
            int pling = -1;
            if (uri.startsWith("jar:file") && (pling = uri.indexOf("!/")) > -1) {
                InputStream is = null;
                try {
                    final String jarName = uri.substring("jar:".length(), pling);
                    final String zipFileName = fromURIJava13(jarName);
                    final ZipFile zf = new ZipFile(zipFileName);
                    final ZipEntry e = zf.getEntry(uri.substring(pling + 2));
                    is = zf.getInputStream(e);
                    if (is != null) {
                        return true;
                    }
                }
                catch (Exception e2) {
                    return false;
                }
                finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        }
        catch (Exception e3) {
            return false;
        }
        return false;
    }
    
    public JarFile(final String uri) {
    }
}
