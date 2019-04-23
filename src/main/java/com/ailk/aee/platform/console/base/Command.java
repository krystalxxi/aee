// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import com.ailk.aee.common.util.StringUtils;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Command.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Command
{
    private ICommandAdapter adapter;
    protected String detailHelp;
    protected String help;
    protected String name;
    private String shortName;
    
    public Command(final String name, final String shortName, final String help, final String detailHelp, final ICommandAdapter ada) {
        this.detailHelp = "";
        this.help = "";
        this.name = "";
        this.setName(name);
        if (shortName != null) {
            this.setShortName(shortName);
        }
        else {
            this.setShortName("");
        }
        if (help != null) {
            this.setHelp(help);
        }
        if (detailHelp != null) {
            this.setDetailHelp(detailHelp);
        }
        else {
            this.setDetailHelp(this.getHelp());
        }
        this.adapter = ada;
    }
    
    public void doCommand(final CmdEnv env, final String[] args) {
        if (this.adapter != null) {
            this.adapter.doCommand(env, args);
        }
    }
    
    public String getCommandShowName() {
        if (this.shortName.equals("")) {
            return this.getName();
        }
        return this.getName() + "(" + this.getShortName() + ")";
    }
    
    public int getCommandShowNameSize() {
        return this.getCommandShowName().length();
    }
    
    public String getDetailHelp() {
        return this.detailHelp;
    }
    
    public String getHelp() {
        return this.help;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    public boolean isLikeMe(final String cmd, final boolean ingoreCase) {
        if (!ingoreCase) {
            return StringUtils.contains((CharSequence)this.name, (CharSequence)cmd) || StringUtils.contains((CharSequence)this.name, (CharSequence)this.shortName);
        }
        return StringUtils.containsIgnoreCase((CharSequence)this.name, (CharSequence)cmd) || StringUtils.containsIgnoreCase((CharSequence)this.shortName, (CharSequence)cmd);
    }
    
    public boolean isMe(final String cmd, final boolean ingoreCase) {
        if (!ingoreCase) {
            return cmd.equals(this.name) || cmd.equals(this.shortName);
        }
        return cmd.equalsIgnoreCase(this.name) || cmd.equalsIgnoreCase(this.shortName);
    }
    
    public void setDetailHelp(final String detailHelp) {
        this.detailHelp = detailHelp;
    }
    
    public void setHelp(final String help) {
        this.help = help;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }
}
