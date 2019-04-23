// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: EnvVarFileConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class EnvVarFileConfigurationFactory extends FileConfigurationFactory
{
    private String envConf;
    private String conf;
    
    public EnvVarFileConfigurationFactory(final String conf, final String parserType) {
        super("", parserType);
        this.envConf = "";
        this.conf = "";
        this.conf = conf;
        if (System.getProperty(conf) != null) {
            this.envConf = System.getProperty(conf);
        }
        else if (System.getenv(conf) != null) {
            this.envConf = System.getenv(conf);
        }
        if (!this.envConf.equals("")) {
            this.setFileName(this.envConf);
        }
        this.setParseType(parserType);
    }
    
    @Override
    public String getFactoryName() {
        if (this.getLocation().equals("")) {
            return "Configuration of Env Var File " + this.conf + " buf File not Exist,ingnore this Configuration";
        }
        return "Configuration of Env Var File " + this.conf + " Real File in :" + this.getLocation() + super.getFactoryName();
    }
}
