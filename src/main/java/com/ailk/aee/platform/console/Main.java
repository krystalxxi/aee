// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console;

import com.ailk.aee.platform.console.base.CmdConfig;
import java.io.FilenameFilter;
import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.config.AEEWorkConfig;
import java.util.Map;
import java.util.HashMap;
import com.ailk.aee.platform.console.base.ICommandAdapter;
import java.io.File;
import com.ailk.aee.platform.console.base.Command;
import com.ailk.aee.common.conf.Configuration;
import com.ailk.aee.platform.console.base.CmdMain;
import com.ailk.aee.common.util.Options;
import com.ailk.aee.AEEConf;
import java.util.ArrayList;
import java.util.List;
import com.ailk.aee.platform.console.base.CmdEnv;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.platform.console.base.IConsole;

@CVSID("$Id: Main.java 62832 2013-11-07 16:56:14Z huwl $")
public class Main implements IConsole
{
    private CmdEnv env;
    private String nodeId;
    private List<String> allWorks;
    
    public Main() {
        this.allWorks = new ArrayList<String>();
    }
    
    public static void main(final String[] args) {
        try {
            AEEConf.init();
            final Options opts = new Options();
            opts.addOption('n', "node", 1, 0, "\u05b8\ufffd\ufffd\ufffd\u06b5\ufffdID\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd\u0221\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdAEE_NODE_ID", false, "");
            final boolean isArgOk = opts.parser(args);
            String[] appArgs = null;
            if (isArgOk) {
                appArgs = opts.getNoNameArgs();
            }
            else {
                final Exception e = opts.getFirstException();
                System.out.println(e.getMessage());
                System.out.println(opts.dumpUsage());
                System.exit(-1);
            }
            final CmdMain cm = new CmdMain();
            String[] args2 = null;
            if (appArgs != null && appArgs.length > 0) {
                args2 = new String[appArgs.length + 2];
                for (int i = 0; i < appArgs.length; ++i) {
                    args2[i + 2] = appArgs[i];
                }
            }
            else {
                args2 = new String[2];
            }
            args2[0] = "com.ailk.aee.platform.console.Main";
            String nodeId = opts.getOptionValue("node");
            if (nodeId != null && nodeId.length() != 0) {
                System.out.println("\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffdnode\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdAEE_NODE_ID\ufffd\ufffd\ufffd\ufffd");
                return;
            }
            nodeId = Configuration.getValue("AEE_NODE_ID");
            if (nodeId == null || nodeId.length() == 0) {
                System.out.println("\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffdnode\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdAEE_NODE_ID\ufffd\ufffd\ufffd\ufffd");
                return;
            }
            args2[1] = nodeId;
            cm.start(args2);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public void cmd_ps(final CmdEnv env, final String[] args) {
        if (args == null || args.length == 0) {
            env.out("hello world");
        }
        env.out("hello world");
    }
    
    @Override
    public List<Command> getCommand() {
        final ArrayList<Command> al = new ArrayList<Command>();
        final String basepath = Configuration.getValue("AEE_HOME") + File.separator + "bin" + File.separator + "script";
        final File fsd = new File(basepath);
        al.add(new Command("confview", "", "\ufffd\ufffd\u04e1\ufffd\ufffd\u01f0\ufffd\ufffdConf(AEEConsole)", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                final Map<String, String> ms = new HashMap<String, String>();
                if (args.length == 1) {
                    ms.putAll(Configuration.getConf(""));
                }
                else {
                    for (int i = 1; i < args.length; ++i) {
                        final String s = args[i];
                        ms.put(s, Configuration.getValue(s));
                    }
                }
                env.out(ConsoleOutputUtils.asList(ms));
            }
        }));
        al.add(new Command("node", "", "change\u05b8\ufffd\ufffd\ufffdnode", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                if (args.length == 1) {
                    env.outline("\ufffd\ufffd\u01f0\u05b8\ufffd\ufffd\ufffdNode=[" + ConsoleStatic.getInstance().getToNodeId() + ",@" + ConsoleStatic.getInstance().getToNodeHost() + ":" + ConsoleStatic.getInstance().getToNodePort());
                    return;
                }
                if (args.length != 2) {
                    env.outline("\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd\u04bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
                    return;
                }
                final String toNode = args[1];
                final String location = AEEWorkConfig.getInstance().getSingleConfig("AEE.nodes." + toNode + ".location");
                if (location == null) {
                    env.outline("\ufffd\u04b2\ufffd\ufffd\ufffdNode[" + toNode + "]\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd");
                }
                ConsoleStatic.getInstance().setToNodeId(toNode);
                ConsoleStatic.getInstance().setToNodeHost(StringUtils.substringBefore(location, ":"));
                ConsoleStatic.getInstance().setToNodePort(StringUtils.substringAfter(location, ":"));
                env.outline("\ufffd\u0131\ufffd\u05b8\ufffd\ufffdNode\u03aa[" + ConsoleStatic.getInstance().getToNodeId() + ",@" + ConsoleStatic.getInstance().getToNodeHost() + ":" + ConsoleStatic.getInstance().getToNodePort());
            }
        }));
        if (fsd.exists() && fsd.isDirectory() && fsd.canRead()) {
            final File[] arr$;
            final File[] sfs = arr$ = fsd.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(final File filefolder, final String fileName) {
                    return fileName.endsWith(".py");
                }
            });
            for (final File f : arr$) {
                al.add(JythonFileCommand.getCommand(f.getAbsolutePath()));
            }
        }
        return al;
    }
    
    @Override
    public CmdConfig getConfig() {
        final CmdConfig cc = new CmdConfig();
        cc.setTitle("AEE Console");
        cc.setVersion("1.0.0.0");
        cc.setCopyRight("@2012 Asiainfo-Linkage");
        cc.setPrompt2Type(0);
        return cc;
    }
    
    @Override
    public boolean isNeedLogin() {
        return false;
    }
    
    @Override
    public void login(final CmdEnv env) {
    }
    
    @Override
    public void onQuit(final CmdEnv env) {
    }
    
    @Override
    public void onStart(final CmdEnv env, final String[] args) {
        this.env = env;
        this.nodeId = args[0];
        ConsoleStatic.getInstance().setToNodeId(this.nodeId);
        final String location = AEEWorkConfig.getInstance().getSingleConfig("AEE.nodes." + this.nodeId + ".location");
        ConsoleStatic.getInstance().setToNodeHost(StringUtils.substringBefore(location, ":"));
        ConsoleStatic.getInstance().setToNodePort(StringUtils.substringAfter(location, ":"));
        this.readAllConfig();
    }
    
    public void readAllConfig() {
        final String[] arr$;
        final String[] works = arr$ = AEEWorkConfig.getInstance().getAllWork();
        for (final String s : arr$) {
            this.allWorks.add(s);
        }
    }
}
