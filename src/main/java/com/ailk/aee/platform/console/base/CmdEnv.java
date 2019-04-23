// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.io.OutputStream;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.List;
import com.ailk.aee.common.util.StringUtils;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.ArrayList;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: CmdEnv.java 60270 2013-11-03 14:48:37Z tangxy $")
public class CmdEnv
{
    ArrayList<Command> allCommand;
    CmdConfig config;
    IConsole console;
    private Hashtable<String, Object> envObjects;
    private PrintStream outputStream;
    
    public CmdEnv() {
        this.allCommand = new ArrayList<Command>();
        this.config = null;
        this.envObjects = new Hashtable<String, Object>();
        this.outputStream = System.out;
    }
    
    public void addCommand(final Command ac) {
        this.allCommand.add(ac);
    }
    
    public void addInnerCommand() {
        this.addCommand(new Command("help", "h", "show command usage", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doHelp(args);
            }
        }));
        this.addCommand(new Command("version", "v", "show version infomation", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doVersion(args);
            }
        }));
        this.addCommand(new Command("set", "s", "set envrionment ", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doSet(args);
            }
        }));
        this.addCommand(new Command("?", "", "show command usage", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doHelp(args);
            }
        }));
        this.addCommand(new Command("list", "l", "show command usage", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doList(args);
            }
        }));
        this.addCommand(new Command("echo", "", "echo string", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                CmdEnv.this.doEcho(args);
            }
        }));
        this.addCommand(new Command("quit", "", "quit this programe", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
            }
        }));
        this.addCommand(new Command("exit", "", "quit this programe", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
            }
        }));
    }
    
    public void addObject(final String s, final Object o) {
        this.envObjects.put(s, o);
    }
    
    public boolean doCommand(final String s) {
        final String[] cmds = StringUtils.split(s, " \t");
        final String cmd = cmds[0];
        long startTime = 0L;
        long endTime = 0L;
        if (this.isQuitCommand(cmd)) {
            return false;
        }
        if (this.config.isTimeStat()) {
            startTime = System.currentTimeMillis();
        }
        final List<Command> al = this.findCommand(cmd);
        if (al.size() == 0) {
            this.doNoThisCommand(cmds);
            return true;
        }
        if (al.size() > 1) {
            this.doMutilCommandFind(cmds);
            return true;
        }
        try {
            al.get(0).doCommand(this, cmds);
        }
        catch (Exception ex) {
            this.doExceptionCommand(cmds, ex);
            return true;
        }
        if (this.config.isTimeStat()) {
            endTime = System.currentTimeMillis();
            this.outline("execute time:" + Float.toString((endTime - startTime) / 1000.0f));
        }
        return true;
    }
    
    void doCommandCycle() {
        String s;
        do {
            s = this.readCommand();
        } while (this.doCommand(s));
    }
    
    public void doEcho(final String s) {
        this.outline(s);
    }
    
    public void doEcho(final String[] cmds) {
        if (cmds.length > 1) {
            for (int i = 1; i < cmds.length; ++i) {
                this.outline(cmds[i]);
            }
        }
        else {
            this.outline("");
        }
    }
    
    public void doException(final Exception e) {
        this.outline("ERROR:[" + e.getMessage() + "] occurred");
    }
    
    private void doExceptionCommand(final String[] cmds, final Exception e) {
        this.outline("ERROR:[" + e.getMessage() + "] occurred,when execute " + this.packageArgs(cmds, 0));
        if (this.config.isVerbose()) {
            e.printStackTrace();
        }
    }
    
    public void doHelp(final String s) {
        this.doHelp(new String[] { "help", s });
    }
    
    public void doHelp(final String[] cmds) {
        if (cmds.length > 1) {
            for (int i = 1; i < cmds.length; ++i) {
                this.innerDoHelp(cmds[i]);
            }
        }
        else {
            this.outline("HELP: use help <command name> for detail help");
        }
    }
    
    public void doList(final String s) {
        this.doList(new String[] { "list", s });
    }
    
    public void doList(final String[] args) {
        if (args.length == 1) {
            this.innerDoList("");
        }
        else {
            for (int i = 1; i < args.length; ++i) {
                this.innerDoList(args[i]);
            }
        }
    }
    
    private void doMutilCommandFind(final String[] cmds) {
        this.outline("ERROR:mutil command find use :" + cmds[0] + " when execute [" + this.packageArgs(cmds, 0) + "],use full name to execute special one");
        this.innerDoList(cmds[0]);
    }
    
    public void doNoThisCommand(final String[] cmds) {
        this.outline("ERROR:no this command:" + cmds[0] + " when execute [" + this.packageArgs(cmds, 0) + "],use list for all command set");
    }
    
    public void doSet(final String v, final String p) {
        this.doSet(new String[] { v, p });
    }
    
    public void doSet(final String[] cmds) {
        if (cmds.length == 1) {
            final Class<?> clazz = CmdConfig.class;
            final Method[] arr$;
            final Method[] ms = arr$ = clazz.getMethods();
            for (final Method m : arr$) {
                final String mname = m.getName();
                if (mname.startsWith("set")) {
                    final String type = m.getParameterTypes()[0].getName();
                    if (type.equals("java.lang.String")) {
                        this.outline("set " + mname.substring(3) + "  <value>");
                    }
                    else if (type.equals("int")) {
                        this.outline("set " + mname.substring(3) + "  <number>");
                    }
                    else if (type.equals("boolean")) {
                        this.outline("set " + mname.substring(3) + "  <on|off>");
                    }
                }
            }
        }
        else if (cmds.length != 3) {
            this.outline("HELP: usage: set <env name> <env value>");
        }
        else {
            this.outline("[SET]: " + this.config.doSet(cmds[1], cmds[2]));
        }
    }
    
    public void doVersion(final String[] args) {
        this.outline(this.config.getVersion());
    }
    
    public void echo(final String s) {
        this.outline(s);
    }
    
    public List<Command> findCommand(final String s) {
        final ArrayList<Command> fl = new ArrayList<Command>();
        for (final Command c : this.allCommand) {
            if (c.isMe(s, this.config.isIgnoreCase())) {
                fl.add(c);
            }
        }
        return fl;
    }
    
    public boolean getBoolValue(final String s) {
        return s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("T") || s.equals("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("on") || ((s.equalsIgnoreCase("N") || s.equalsIgnoreCase("F") || s.equals("false") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("off")) && false);
    }
    
    public CmdConfig getConfig() {
        return this.config;
    }
    
    public int getIntValue(final String s) {
        return Integer.parseInt(s);
    }
    
    public Object getObject(final String s) {
        return this.envObjects.get(s);
    }
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    private void innerDoHelp(final String cmd) {
        final List<Command> al = this.findCommand(cmd);
        if (al.size() == 0) {
            this.outline("ERROR: no command named " + cmd);
            return;
        }
        for (final Command c : al) {
            this.outline("command:   [" + c.getName() + "]");
            if (!c.getShortName().equals("")) {
                this.outline("short:     [" + c.getShortName() + "]");
            }
            this.outline("infomation:[" + c.getHelp() + "]");
            if (!c.getDetailHelp().equals(c.getHelp()) && !c.getDetailHelp().equals("")) {
                this.outline("detail:    [" + c.getDetailHelp() + "]");
            }
            this.outline("");
        }
    }
    
    private void innerDoList(final String cmdHead) {
        int maxSize = 0;
        final ArrayList<String> fl = new ArrayList<String>();
        if (cmdHead.equals("")) {
            for (final Command ac : this.allCommand) {
                if (ac.getCommandShowNameSize() > maxSize) {
                    maxSize = ac.getCommandShowNameSize();
                }
                fl.add(ac.getCommandShowName());
            }
            this.outline("all command:");
        }
        else {
            this.outline("all command like [*" + cmdHead + "*]:");
            for (final Command ac : this.allCommand) {
                if (ac.isLikeMe(cmdHead, this.config.isIgnoreCase())) {
                    if (ac.getCommandShowNameSize() > maxSize) {
                        maxSize = ac.getCommandShowNameSize();
                    }
                    fl.add(ac.getCommandShowName());
                }
            }
        }
        Collections.sort(fl);
        if (maxSize < 10) {
            maxSize = 10;
        }
        int cmdperline = 1;
        if (maxSize < this.config.getScreenSize()) {
            cmdperline = this.config.getScreenSize() / (maxSize + 5);
        }
        int i = 0;
        for (final String s : fl) {
            this.out(StringUtils.rightPad(s, maxSize + 5, " "));
            if (++i == cmdperline) {
                this.outline("");
                i = 0;
            }
        }
        this.outline("");
    }
    
    private boolean isEqual(final String s, final String s1) {
        if (this.config.isIgnoreCase()) {
            return s.equalsIgnoreCase(s1);
        }
        return s.equals(s1);
    }
    
    public boolean isQuitCommand(final String s) {
        return this.isEqual(s, "quit") || this.isEqual(s, "exit");
    }
    
    public void out(final String s) {
        this.outputStream.print(s);
    }
    
    public void outline(final String s) {
        if (s.endsWith("\r") || s.endsWith("\n") || s.endsWith("\r\n")) {
            this.out(s);
        }
        else {
            this.out(s + "\n");
        }
    }
    
    public String packageArgs(final String[] cmds, final int start) {
        final StringBuffer sb = new StringBuffer();
        for (int i = start; i < cmds.length; ++i) {
            sb.append(cmds[i]);
            sb.append(" ");
        }
        return sb.toString();
    }
    
    private String readCommand() {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        final boolean quitFlag = true;
        String realCommand = "";
        boolean prompt2Flag = false;
        while (quitFlag) {
            if (prompt2Flag) {
                this.out(this.config.getPrefix() + this.config.getPrompt2());
            }
            else {
                this.out(this.config.getPrefix() + this.config.getPrompt());
            }
            String s = "";
            try {
                s = br.readLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            s = s.trim();
            if (!s.equals(null) && s.length() != 0 && !s.equals("\r") && !s.equals("\n")) {
                if (s.equals("\r\n")) {
                    continue;
                }
                if (this.config.getPrompt2Type() == 0) {
                    return s;
                }
                if (this.config.getPrompt2Type() == 1) {
                    final char endChar = s.charAt(s.length() - 1);
                    if (endChar == this.config.getEndChar()) {
                        if (realCommand.equals("") && s.trim().equals(";")) {
                            continue;
                        }
                        realCommand = realCommand + s.substring(0, s.length() - 1) + " ";
                        return realCommand;
                    }
                    else {
                        realCommand = realCommand + s + " ";
                        prompt2Flag = true;
                    }
                }
                if (this.config.getPrompt2Type() != 2) {
                    continue;
                }
                final char endChar = s.charAt(s.length() - 1);
                if (endChar != this.config.getEscapeChar()) {
                    realCommand += s;
                    return realCommand;
                }
                realCommand += s.substring(0, s.length() - 1);
                prompt2Flag = true;
            }
        }
    }
    
    public String readLine(final String inputHint) {
        System.out.print(inputHint);
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        }
        catch (IOException e) {
            return "";
        }
    }
    
    public String readPassword(final String inputHint) {
        System.out.print(inputHint);
        return new String(System.console().readPassword());
    }
    
    public void setConfig(final CmdConfig cfg) {
        this.config = cfg;
    }
    
    public void setOutputStream(final OutputStream os) {
        this.outputStream = new PrintStream(os);
    }
    
    public void setOutputStream(final PrintStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void start(final IConsole console, final String[] args) {
        this.console = console;
        this.setConfig(console.getConfig());
        this.addInnerCommand();
        final List<Command> l = console.getCommand();
        for (final Command c : l) {
            this.addCommand(c);
        }
        this.outline(this.getConfig().getTitle() + " Release:" + this.getConfig().getVersion());
        this.outline(this.getConfig().getCopyRight());
        this.outline("");
        console.onStart(this, args);
        if (console.isNeedLogin()) {
            console.login(this);
        }
        this.doCommandCycle();
        console.onQuit(this);
    }
    
    public void verbose(final String s) {
        if (this.config.isVerbose()) {
            this.outline("[VERBOSE]:" + s);
        }
    }
}
