// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import org.mvel2.integration.VariableResolver;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;

@CVSID("$Id: ReadOnlyCachingMapVariableResolverFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ReadOnlyCachingMapVariableResolverFactory extends CachingMapVariableResolverFactory
{
    private static final long serialVersionUID = 1L;
    
    public ReadOnlyCachingMapVariableResolverFactory(final Map variables) {
        super(variables);
    }
    
    public VariableResolver createVariable(final String arg0, final Object arg1) {
        if (this.getNextFactory() == null) {
            System.out.println("NO Create Variable @ a ReadOnlyCachingMapVariableResolverFactory");
            return null;
        }
        return this.nextFactory.createVariable(arg0, arg1);
    }
    
    public VariableResolver createVariable(final String arg0, final Object arg1, final Class<?> arg2) {
        if (this.getNextFactory() == null) {
            System.out.println("NO Create Variable @ a ReadOnlyCachingMapVariableResolverFactory");
            return null;
        }
        return this.nextFactory.createVariable(arg0, arg1, (Class)arg2);
    }
}
