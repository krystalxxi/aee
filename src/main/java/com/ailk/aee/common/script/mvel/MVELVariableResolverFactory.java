// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import java.util.Map;
import java.util.HashMap;
import org.mvel2.integration.VariableResolver;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import org.mvel2.integration.impl.LocalVariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

@CVSID("$Id: MVELVariableResolverFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MVELVariableResolverFactory extends BaseVariableResolverFactory implements LocalVariableResolverFactory
{
    private static final long serialVersionUID = -8631524542787717467L;
    private IMVELScriptCompatible content;
    
    public MVELVariableResolverFactory(final IMVELScriptCompatible c) {
        this.content = c;
    }
    
    private void addResolver(final String name, final VariableResolver vr) {
        if (this.variableResolvers == null) {
            this.variableResolvers = new HashMap();
        }
        this.variableResolvers.put(name, vr);
    }
    
    public VariableResolver createVariable(final String name, final Object value) {
        VariableResolver vr = null;
        try {
            vr = this.getVariableResolver(name);
        }
        catch (Exception ex) {}
        if (vr == null) {
            this.addResolver(name, vr = (VariableResolver)new MVELScriptCompatibleResolver(this.content, name));
        }
        vr.setValue(value);
        return vr;
    }
    
    public VariableResolver createVariable(final String name, final Object value, final Class<?> arg2) {
        VariableResolver vr = null;
        try {
            vr = this.getVariableResolver(name);
        }
        catch (Exception ex) {}
        if (vr == null) {
            this.addResolver(name, vr = (VariableResolver)new MVELScriptCompatibleResolver(this.content, name, arg2));
        }
        vr.setValue(value);
        return vr;
    }
    
    public Map<String, VariableResolver> getVariableResolvers() {
        return (Map<String, VariableResolver>)this.variableResolvers;
    }
    
    public boolean isResolveable(final String name) {
        boolean b = false;
        if (this.nextFactory != null) {
            b = this.nextFactory.isResolveable(name);
        }
        if (b) {
            return b;
        }
        if (name == null) {
            return false;
        }
        if (this.variableResolvers != null && this.variableResolvers.containsKey(name)) {
            return true;
        }
        if (this.content.containKey(name)) {
            final VariableResolver vr = (VariableResolver)new MVELScriptCompatibleResolver(this.content, name);
            this.addResolver(name, vr);
            return true;
        }
        return false;
    }
    
    public boolean isTarget(final String name) {
        return this.variableResolvers != null && this.variableResolvers.containsKey(name);
    }
    
    class MVELScriptCompatibleResolver implements VariableResolver
    {
        private static final long serialVersionUID = -3924687495424018561L;
        private IMVELScriptCompatible content;
        private final String name;
        private Class knownType;
        
        public MVELScriptCompatibleResolver(final IMVELScriptCompatible i, final String name) {
            this.knownType = null;
            this.content = i;
            this.name = name;
        }
        
        public MVELScriptCompatibleResolver(final IMVELScriptCompatible i, final String name, final Class knowType) {
            this.knownType = null;
            this.content = i;
            this.name = name;
            this.knownType = knowType;
        }
        
        public int getFlags() {
            return 0;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Class getType() {
            return this.knownType;
        }
        
        public Object getValue() {
            return this.content.getData(this.name);
        }
        
        public void setStaticType(final Class knownType) {
            this.knownType = knownType;
        }
        
        public void setValue(final Object value) {
            if (this.knownType == null || value == null || value.getClass() != this.knownType) {}
            this.content.setData(this.name, value);
        }
    }
}
