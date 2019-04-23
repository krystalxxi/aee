// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.Properties;
import java.util.Locale;
import java.util.Formatter;
import java.net.UnknownHostException;
import java.io.File;
import java.util.Set;
import java.util.Enumeration;
import java.net.SocketException;
import java.net.InetAddress;
import java.util.HashSet;
import java.net.NetworkInterface;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SystemUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SystemUtils
{
    public static String[] getAllIps() {
        try {
            final Enumeration en = NetworkInterface.getNetworkInterfaces();
            final Set<String> ipList = new HashSet<String>();
            while (en.hasMoreElements()) {
                final NetworkInterface intf = (NetworkInterface)en.nextElement();
                final Enumeration enAddr = intf.getInetAddresses();
                while (enAddr.hasMoreElements()) {
                    final InetAddress addr = (InetAddress)enAddr.nextElement();
                    ipList.add(addr.getHostAddress());
                }
            }
            return ipList.toArray(new String[0]);
        }
        catch (SocketException e) {
            return new String[0];
        }
    }
    
    public static String getCurrentPath() {
        final File directory = new File("");
        try {
            return directory.getAbsolutePath();
        }
        catch (Exception e) {
            return "";
        }
    }
    
    public static String getLocalIp() {
        try {
            final InetAddress address = InetAddress.getLocalHost();
            final NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            final String sIP = address.getHostAddress();
            return sIP;
        }
        catch (UnknownHostException e) {
            return "0.0.0.0";
        }
        catch (SocketException e2) {
            return "0.0.0.0";
        }
    }
    
    public static String getLocalMac() {
        try {
            final InetAddress address = InetAddress.getLocalHost();
            final NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            ni.getInetAddresses().nextElement().getAddress();
            final byte[] mac = ni.getHardwareAddress();
            String sMAC = "";
            final Formatter formatter = new Formatter();
            for (int i = 0; i < mac.length; ++i) {
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "").toString();
            }
            return sMAC;
        }
        catch (UnknownHostException e) {
            return "00-00-00-00-00-00";
        }
        catch (SocketException e2) {
            return "00-00-00-00-00-00";
        }
    }
    
    public static boolean isWindows() {
        final Properties prop = System.getProperties();
        final String os = prop.getProperty("os.name");
        return os.startsWith("win") || os.startsWith("Win");
    }
}
