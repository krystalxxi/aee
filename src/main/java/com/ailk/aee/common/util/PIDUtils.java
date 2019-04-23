// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.util.Properties;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: PIDUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class PIDUtils
{
    public static int thisPid;
    
    private static void execCmd(final String cmd) {
        try {
            final Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        }
        catch (IOException e) {}
        catch (InterruptedException ex) {}
    }
    
    private static String execCmdGetOneResult(final String cmd) {
        Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        }
        catch (Exception e) {
            return "ERROR";
        }
        final InputStream istr = process.getInputStream();
        final BufferedReader br = new BufferedReader(new InputStreamReader(istr));
        final StringBuffer sb = new StringBuffer();
        try {
            String str;
            while ((str = br.readLine()) != null) {
                if (str.trim().length() > 0) {
                    sb.append(str);
                }
            }
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    private static int execCmdWithReturn(final String cmd) {
        try {
            final Process process = Runtime.getRuntime().exec(cmd);
            return process.waitFor();
        }
        catch (IOException e) {}
        catch (InterruptedException ex) {}
        return -1;
    }
    
    public static int getPid() {
        if (PIDUtils.thisPid == -2147483647) {
            PIDUtils.thisPid = innerGetPid();
        }
        return PIDUtils.thisPid;
    }
    
    private static int getPidByMbean() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        final String name = runtime.getName();
        try {
            return Integer.parseInt(name.substring(0, name.indexOf(64)));
        }
        catch (Exception e) {
            return -1;
        }
    }
    
    private static int getPidByNetstatLinux() {
        final int port = 50000 + (int)Math.round(Math.random() * 1000.0);
        final String cmd = "netstat -anp";
        final String criteria = "0.0.0.0:" + port;
        new SocketListener(port, 5000).start();
        int pid = -1;
        try {
            final Process process = Runtime.getRuntime().exec("netstat -anp");
            final InputStream istr = process.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(istr));
            String str;
            while ((str = br.readLine()) != null) {
                if (str.indexOf(criteria) > 0) {
                    final String match = str.substring(1 + str.lastIndexOf(" "));
                    pid = Integer.valueOf(StringUtils.substringBefore(match, "/"));
                    return pid;
                }
            }
        }
        catch (Exception e) {
            return -1;
        }
        return pid;
    }
    
    private static int getPidByNetstatWindows() {
        final int port = 50000 + (int)Math.round(Math.random() * 1000.0);
        final String cmd = "netstat -ano";
        final String criteria = "0.0.0.0:" + port;
        new SocketListener(port, 5000).start();
        int pid = -1;
        try {
            final Process process = Runtime.getRuntime().exec("netstat -ano");
            final InputStream istr = process.getInputStream();
            final BufferedReader br = new BufferedReader(new InputStreamReader(istr));
            String str;
            while ((str = br.readLine()) != null) {
                if (str.indexOf(criteria) > 0) {
                    final String match = str.substring(1 + str.lastIndexOf(" "));
                    pid = Integer.valueOf(match);
                    return pid;
                }
            }
        }
        catch (Exception e) {
            return -1;
        }
        return pid;
    }
    
    private static int innerGetPid() {
        int pid = -1;
        final Properties prop = System.getProperties();
        final String s = prop.getProperty("java.vm.vendor");
        if (s.startsWith("Sun") || s.startsWith("sun") || s.startsWith("IBM") || s.startsWith("\"Hewlett-Packard") || s.startsWith("\"HP")) {
            pid = getPidByMbean();
        }
        if (pid != -1) {
            return pid;
        }
        final String os = prop.getProperty("os.name");
        if (os.startsWith("win") || os.startsWith("Win")) {
            pid = getPidByNetstatWindows();
        }
        else if (os.startsWith("linux") || os.startsWith("Lin")) {
            pid = getPidByNetstatLinux();
        }
        if (pid != -1) {
            return pid;
        }
        if (pid == -1) {}
        return pid;
    }
    
    public static boolean isPidExist(final int pid) {
        if (isWindows()) {
            return isPidExistForWindows(pid);
        }
        return isPidExistForUnix(pid);
    }
    
    private static boolean isPidExistForUnix(final int pid) {
        final int i = execCmdWithReturn("ps -p " + pid);
        return i == 0;
    }
    
    private static boolean isPidExistForWindows(final int pid) {
        final String s = "tasklist /NH /FI \"PID eq " + pid + "\" /FO CSV";
        final String ret = execCmdGetOneResult(s);
        return ret != null && ret.indexOf("\"" + pid + "\"") > 0;
    }
    
    private static boolean isWindows() {
        final Properties prop = System.getProperties();
        final String os = prop.getProperty("os.name");
        return os.startsWith("win") || os.startsWith("Win");
    }
    
    public static void killByPid(final int pid) {
        if (isWindows()) {
            killByPidForWindows(pid);
        }
        else {
            killByPidForUnix(pid);
        }
    }
    
    private static void killByPidForUnix(final int pid) {
        final String s = "kill -SIGTERM " + pid;
        execCmd(s);
    }
    
    private static void killByPidForWindows(final int pid) {
        final String s = "taskkill /PID " + pid;
        execCmd(s);
    }
    
    public static void main(final String[] args) {
        System.out.println("the pid is :" + getPid() + " by check enviroment(default)");
        System.out.println("the pid is :" + getPidByMbean() + " by using sun jdk management");
        System.out.println("the pid is :" + getPidByNetstatWindows() + " by using netstat -ano with a randome port server socket");
        System.out.println("the pid is :" + getPidByNetstatLinux() + " by using netstat -anp with a randome port server socket for *nix");
    }
    
    static {
        PIDUtils.thisPid = -2147483647;
    }
    
    static class SocketListener extends Thread
    {
        int port;
        int wait;
        
        public SocketListener(final int port, final int wait) {
            this.port = port;
            this.wait = wait;
        }
        
        @Override
        public void run() {
            try {
                final ServerSocket server = new ServerSocket(this.port);
                server.setSoTimeout(this.wait);
                server.accept();
                server.close();
            }
            catch (SocketTimeoutException e) {}
            catch (IOException ex) {}
        }
    }
}
