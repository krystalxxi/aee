// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.annotation.cvsid;

import java.net.URLStreamHandlerFactory;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.List;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.io.File;

@CVSID("$Id: CVSTool.java 60270 2013-11-03 14:48:37Z tangxy $")
public class CVSTool
{
    public static CVSInfo getClassCVSID(final Class<?> clazz) throws Exception {
        final CVSID idInfo = clazz.getAnnotation(CVSID.class);
        if (idInfo != null) {
            String value = idInfo.value();
            if (value.length() > 4 && value.substring(0, 4).toUpperCase().startsWith("$ID:") && value.endsWith("$")) {
                value = value.substring(4, value.length() - 1).trim();
                final StringBuilder sb = new StringBuilder("");
                int index = value.indexOf(",");
                if (index > 0) {
                    sb.append(clazz.getCanonicalName()).append(value.substring(index));
                }
                else {
                    index = value.indexOf(" ");
                    sb.append(clazz.getCanonicalName()).append(" ").append(value.substring(index));
                }
                return CVSInfo.parse(sb.toString());
            }
        }
        return null;
    }
    
    public static CVSInfo getClassCVSID(final ClassLoader cl, final String classname) throws Exception {
        final Class<?> clazz = Class.forName(classname, true, cl);
        return getClassCVSID(clazz);
    }
    
    public static CVSInfo[] getClassCVSID(final File jarFile) throws Exception {
        if (!jarFile.isFile() || !jarFile.getAbsolutePath().endsWith(".jar")) {
            return null;
        }
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entrys = jar.entries();
        final ArrayList<CVSInfo> list = new ArrayList<CVSInfo>();
        while (entrys.hasMoreElements()) {
            final JarEntry entry = entrys.nextElement();
            final String path = entry.getName();
            if (path.endsWith(".class")) {
                final int index = path.indexOf(".");
                final String classname = path.substring(0, index).replace('/', '.');
                final CVSInfo[] infos = getClassCVSID(classname);
                if (infos == null || infos.length <= 0) {
                    continue;
                }
                list.add(infos[0]);
            }
        }
        entrys = null;
        jar.close();
        jar = null;
        return list.toArray(new CVSInfo[0]);
    }
    
    public static CVSInfo[] getClassCVSID(final File jarFile, final URL[] urls) throws Exception {
        final ClassLoader parent = CVSTool.class.getClassLoader();
        final ExtURLClassLoader extLoader = new ExtURLClassLoader(parent);
        for (final URL u : urls) {
            extLoader.addURL(u);
        }
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entrys = jar.entries();
        final ArrayList<CVSInfo> list = new ArrayList<CVSInfo>();
        while (entrys.hasMoreElements()) {
            final JarEntry entry = entrys.nextElement();
            final String path = entry.getName();
            if (path.endsWith(".class")) {
                final int index = path.indexOf(".");
                final String classname = path.substring(0, index).replace('/', '.');
                final CVSInfo info = getClassCVSID(extLoader, classname);
                if (info == null) {
                    continue;
                }
                list.add(info);
            }
        }
        entrys = null;
        jar.close();
        jar = null;
        return list.toArray(new CVSInfo[0]);
    }
    
    public static CVSInfo[] getClassCVSID(final String classname) throws Exception {
        final ArrayList<CVSInfo> list = new ArrayList<CVSInfo>();
        final File dir = new File(classname);
        if (dir.exists() && dir.isDirectory()) {
            final ClassLoader parent = CVSTool.class.getClassLoader();
            final ExtURLClassLoader extLoader = new ExtURLClassLoader(parent);
            extLoader.addURL(dir.toURI().toURL());
            final List<String> filelist = new ArrayList<String>();
            loadFileInfo(dir, filelist);
            final String[] arr$;
            final String[] classArray = arr$ = filelist.toArray(new String[0]);
            for (String temp : arr$) {
                if (temp.endsWith(".class")) {
                    temp = temp.substring(dir.getAbsolutePath().length() + 1);
                    temp = temp.substring(0, temp.length() - 6).replace(File.separator, ".");
                    final CVSInfo info = getClassCVSID(extLoader, temp);
                    if (info != null) {
                        list.add(info);
                    }
                }
            }
        }
        else {
            final Class<?> clazz = Class.forName(classname);
            final CVSInfo info2 = getClassCVSID(clazz);
            if (info2 != null) {
                list.add(getClassCVSID(clazz));
            }
        }
        return list.toArray(new CVSInfo[0]);
    }
    
    public static CVSInfo[] getClassCVSID(final String classname, final URL[] urls) throws Exception {
        final ClassLoader parent = CVSTool.class.getClassLoader();
        final ExtURLClassLoader extLoader = new ExtURLClassLoader(parent);
        for (final URL u : urls) {
            extLoader.addURL(u);
        }
        final ArrayList<CVSInfo> list = new ArrayList<CVSInfo>();
        final File dir = new File(classname);
        if (dir.exists() && dir.isDirectory()) {
            extLoader.addURL(dir.toURI().toURL());
            final List<String> filelist = new ArrayList<String>();
            loadFileInfo(dir, filelist);
            final String[] arr$;
            final String[] classArray = arr$ = filelist.toArray(new String[0]);
            for (String temp : arr$) {
                if (temp.endsWith(".class")) {
                    temp = temp.substring(dir.getAbsolutePath().length() + 1);
                    temp = temp.substring(0, temp.length() - 6).replace(File.separator, ".");
                    final CVSInfo info = getClassCVSID(extLoader, temp);
                    if (info != null) {
                        list.add(info);
                    }
                }
            }
        }
        else {
            final CVSInfo info2 = getClassCVSID(extLoader, classname);
            if (info2 != null) {
                list.add(info2);
            }
        }
        return list.toArray(new CVSInfo[0]);
    }
    
    public static List<Object> getTokens(final String sInput, final String sDelimiter, final boolean bReturnTokens) {
        final List<Object> list = new ArrayList<Object>();
        if (sInput != null) {
            final StringTokenizer st = new StringTokenizer(sInput, sDelimiter, bReturnTokens);
            while (st.hasMoreTokens()) {
                list.add(st.nextToken().trim());
            }
        }
        return list;
    }
    
    public static byte[] inputStream2byteArray(final InputStream is) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int i;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            baos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] bytes = baos.toByteArray();
        return bytes;
    }
    
    private static void loadFileInfo(final File file, final List<String> filelist) {
        if (file.isFile()) {
            filelist.add(file.getAbsolutePath());
            return;
        }
        final File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            loadFileInfo(files[i], filelist);
        }
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1) {
                CVSInfo info = getClassCVSID(CVSTool.class);
                if (info == null) {
                    System.out.println("Not CVSId Info on " + CVSTool.class.getCanonicalName());
                }
                else {
                    System.out.println(info.toString());
                }
                info = getClassCVSID(CVSID.class);
                if (info == null) {
                    System.out.println("Not CVSId Info on " + CVSID.class.getCanonicalName());
                }
                else {
                    System.out.println(info.toString());
                }
                info = getClassCVSID(CVSInfo.class);
                if (info == null) {
                    System.out.println("Not CVSId Info on " + CVSInfo.class.getCanonicalName());
                }
                else {
                    System.out.println(info.toString());
                }
                printUsage();
                return;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("h") || args[0].equals("?")) {
                    printUsage();
                }
                else {
                    final CVSInfo[] infos = getClassCVSID(args[0]);
                    if (infos == null || infos.length == 0) {
                        System.out.println(args[0] + " no cvsinfo.");
                        return;
                    }
                    printCVSInfo(infos);
                }
            }
            else if (args.length == 2 || args.length == 4) {
                if (!args[0].trim().equals("-jar") && !args[0].trim().equals("-updatejar")) {
                    System.out.println("parameter input error.");
                    printUsage();
                    return;
                }
                final File jarFile = new File(args[1]);
                if (!args[1].endsWith(".jar") || !jarFile.isFile()) {
                    System.out.println("please input correct jarFileName.");
                    return;
                }
                CVSInfo[] infos2 = null;
                if (args.length >= 4 && args[2].equals("-urls")) {
                    final List<Object> list = getTokens(args[3], ";", false);
                    final List<URL> ulist = new ArrayList<URL>();
                    for (final Object obj : list) {
                        final File temp = new File(obj.toString());
                        if (!temp.exists()) {
                            System.out.println("Not Exists Classpath:'" + obj.toString() + "'!");
                        }
                        else {
                            ulist.add(temp.toURI().toURL());
                        }
                    }
                    infos2 = getClassCVSID(jarFile, ulist.toArray(new URL[0]));
                }
                else {
                    infos2 = getClassCVSID(jarFile);
                }
                if (args[0].trim().equals("-jar")) {
                    printCVSInfo(infos2);
                }
                else {
                    printVersionInfo(jarFile, infos2);
                }
            }
            else if (args.length == 3 && !args[0].trim().equals("-jar") && args[1].trim().equals("-urls")) {
                final List<Object> list2 = getTokens(args[2], ";", false);
                final List<URL> ulist2 = new ArrayList<URL>();
                for (final Object obj2 : list2) {
                    final File temp2 = new File(obj2.toString());
                    if (!temp2.exists()) {
                        System.out.println("Not Exists Classpath:'" + obj2.toString() + "'!");
                    }
                    else {
                        ulist2.add(temp2.toURI().toURL());
                    }
                }
                final CVSInfo[] infos3 = getClassCVSID(args[0], ulist2.toArray(new URL[0]));
                if (infos3 == null) {
                    System.out.println(args[0] + " no cvsinfo.");
                    return;
                }
                printCVSInfo(infos3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void printCVSInfo(final CVSInfo[] ins) {
        if (ins == null) {
            return;
        }
        for (final CVSInfo info : ins) {
            System.out.println(info.toString());
        }
    }
    
    public static void printUsage() {
        System.out.println("Usage\ufffd\ufffd\n\tjava " + CVSTool.class.getCanonicalName() + " [classname | -jar jarfilename | -jar jarfilename -urls path1;path2 | classname -urls path1;path2 | dir -urls path1;path2]");
    }
    
    public static void printVersionInfo(final File file, final CVSInfo[] infos) {
        final StringBuilder sb = new StringBuilder("");
        if (infos != null) {
            for (final CVSInfo info : infos) {
                sb.append(info.getFileName()).append("=").append(info.toString()).append("\n");
            }
        }
        if (file.getAbsolutePath().endsWith(".jar")) {
            write2JarFile(file, sb.toString());
        }
    }
    
    public static void write2JarFile(final File original, final String versionInfo) {
        final String originalPath = original.getAbsolutePath();
        final String tempPath = originalPath.substring(0, originalPath.length() - 4) + "_temp.jar";
        System.out.println(tempPath);
        JarFile originalJar = null;
        try {
            originalJar = new JarFile(originalPath);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        final List<JarEntry> lists = new LinkedList<JarEntry>();
        final Enumeration<JarEntry> entrys = originalJar.entries();
        while (entrys.hasMoreElements()) {
            final JarEntry jarEntry = entrys.nextElement();
            lists.add(jarEntry);
        }
        final File handled = new File(tempPath);
        JarOutputStream jos = null;
        try {
            final FileOutputStream fos = new FileOutputStream(handled);
            jos = new JarOutputStream(fos);
            boolean isExistVersionInfo = false;
            for (final JarEntry je : lists) {
                if (je.getName().equals("version.txt")) {
                    isExistVersionInfo = true;
                }
                final JarEntry newEntry = new JarEntry(je.getName());
                jos.putNextEntry(newEntry);
                final InputStream is = originalJar.getInputStream(je);
                final byte[] bytes = inputStream2byteArray(is);
                is.close();
                jos.write(bytes);
            }
            if (!isExistVersionInfo) {
                jos.putNextEntry(new JarEntry("version.txt"));
                jos.write(versionInfo.toString().getBytes());
            }
            jos.close();
            fos.close();
            lists.clear();
            originalJar.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    static class ExtURLClassLoader extends URLClassLoader
    {
        public ExtURLClassLoader() {
            super(new URL[0]);
        }
        
        public ExtURLClassLoader(final ClassLoader parent) {
            super(new URL[0], parent);
        }
        
        public ExtURLClassLoader(final URL[] urls) {
            super(urls);
        }
        
        public ExtURLClassLoader(final URL[] urls, final ClassLoader parent) {
            super(urls, parent);
        }
        
        public ExtURLClassLoader(final URL[] urls, final ClassLoader parent, final URLStreamHandlerFactory factory) {
            super(urls, parent, factory);
        }
        
        public void addURL(final URL url) {
            super.addURL(url);
        }
    }
}
