// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import java.util.HashMap;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.PropertyHandler;
import java.util.Map;
import com.ailk.aee.common.stringobject.ConverterCollections;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MVELExecutor.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MVELExecutor
{
    private VariableResolverFactory thisFactory;
    private MVELMiscCache miscCache;
    private MVELCompiledCache<ExecutableStatement> stmtCache;
    private MVELCompiledCache<CompiledTemplate> tpltCache;
    private boolean isCacheStmt;
    private boolean isCacheTplt;
    
    public static String apply(final String template, final Object... args) throws MVELException {
        try {
            final MVELExecutor exector = new MVELExecutor();
            exector.prepare(args);
            return exector.applyTemplate(template);
        }
        catch (Exception e) {
            throw new MVELException(e);
        }
    }
    
    public static Object execute(final String script, final Object... args) throws MVELException {
        try {
            final MVELExecutor exector = new MVELExecutor();
            exector.prepare(args);
            return exector.execScript(script);
        }
        catch (Exception e) {
            throw new MVELException(e);
        }
    }
    
    public static boolean getBooleanValue(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (boolean)o;
        }
        return o instanceof String && (boolean)ConverterCollections.booleanConverter.wrapFromString(o.toString());
    }
    
    public MVELExecutor() {
        this(null, MVELCompiledCache.getStatementCache(), MVELCompiledCache.getTemplateCache());
    }
    
    public MVELExecutor(final Map<String, String> conf) {
        this(conf, MVELCompiledCache.getStatementCache(), MVELCompiledCache.getTemplateCache());
    }
    
    public MVELExecutor(final Map<String, String> conf, final MVELCompiledCache<ExecutableStatement> stmtCache, final MVELCompiledCache<CompiledTemplate> tpltCache) {
        this.thisFactory = null;
        this.miscCache = new MVELMiscCache();
        this.stmtCache = null;
        this.tpltCache = null;
        this.isCacheStmt = false;
        this.isCacheTplt = false;
        this.stmtCache = stmtCache;
        this.tpltCache = tpltCache;
        if (conf != null) {
            String v = conf.get("NOT_CACHE");
            if (getBooleanValue(v)) {
                this.stmtCache = null;
                this.tpltCache = null;
            }
            boolean vv = false;
            boolean vm = false;
            v = conf.get("NULL_SAFE");
            if (v != null) {
                vv = (vm = getBooleanValue(v));
            }
            v = conf.get("NULL_VARIABLE");
            if (v != null) {
                final boolean vt = vv = getBooleanValue(v);
            }
            v = conf.get("NULL_METHOD");
            if (v != null) {
                final boolean vt = vm = getBooleanValue(v);
            }
            if (vv) {
                PropertyHandlerFactory.setNullPropertyHandler((PropertyHandler)new PropertyHandler() {
                    public Object getProperty(final String arg0, final Object arg1, final VariableResolverFactory arg2) {
                        return "";
                    }
                    
                    public Object setProperty(final String arg0, final Object arg1, final VariableResolverFactory arg2, final Object arg3) {
                        return null;
                    }
                });
            }
            if (vm) {
                PropertyHandlerFactory.setNullMethodHandler((PropertyHandler)new PropertyHandler() {
                    public Object getProperty(final String arg0, final Object arg1, final VariableResolverFactory arg2) {
                        return null;
                    }
                    
                    public Object setProperty(final String arg0, final Object arg1, final VariableResolverFactory arg2, final Object arg3) {
                        return null;
                    }
                });
            }
        }
        if (this.stmtCache != null) {
            this.isCacheStmt = true;
        }
        if (this.tpltCache != null) {
            this.isCacheTplt = true;
        }
    }
    
    public String applyTemplate(final CompiledTemplate ct) throws MVELException {
        return this.applyTemplate(ct, null);
    }
    
    public String applyTemplate(final CompiledTemplate ct, final Map<?, ?> tempvar) throws MVELException {
        String p = "";
        if (tempvar == null) {
            p = (String)TemplateRuntime.execute(ct, (Object)this.miscCache.getParserContext(), this.thisFactory);
        }
        else {
            final VariableResolverFactory tempFactory = (VariableResolverFactory)new ReadOnlyCachingMapVariableResolverFactory(tempvar);
            tempFactory.setNextFactory(this.thisFactory);
            p = (String)TemplateRuntime.execute(ct, (Object)this.miscCache.getParserContext(), tempFactory);
        }
        return p;
    }
    
    public String applyTemplate(final String template) throws MVELException {
        return this.applyTemplate(template, null, this.isCacheTplt);
    }
    
    public String applyTemplate(final String template, final Map<?, ?> tempvar) throws MVELException {
        return this.applyTemplate(template, tempvar, this.isCacheTplt);
    }
    
    public String applyTemplate(final String template, final Map<?, ?> tempvar, final boolean isCache) throws MVELException {
        if (isCache && this.tpltCache != null) {
            final CompiledTemplate es = this.tpltCache.get(template);
            if (es != null) {
                return this.applyTemplate(es, tempvar);
            }
        }
        final Object o = this.miscCache.compileTemplateWhenNecessary(template);
        if (o instanceof String) {
            return (String)TemplateRuntime.eval((String)o, (Object)this.miscCache.getParserContext(), this.thisFactory);
        }
        if (isCache && this.tpltCache != null) {
            this.tpltCache.put(template, (CompiledTemplate)o);
        }
        return this.applyTemplate((CompiledTemplate)o, tempvar);
    }
    
    public Object execScript(final ExecutableStatement sz) {
        return this.execScript(sz, null);
    }
    
    public Object execScript(final ExecutableStatement sz, final Map<?, ?> tempVars) {
        Object o = null;
        if (tempVars == null) {
            o = MVEL.executeExpression((Object)sz, (Object)this.miscCache.getParserContext(), this.thisFactory);
        }
        else {
            final VariableResolverFactory tempFactory = (VariableResolverFactory)new ReadOnlyCachingMapVariableResolverFactory(tempVars);
            tempFactory.setNextFactory(this.thisFactory);
            o = MVEL.executeExpression((Object)sz, (Object)this.miscCache.getParserContext(), tempFactory);
        }
        return o;
    }
    
    public Object execScript(final String script) {
        return this.execScript(script, null, this.isCacheStmt);
    }
    
    public Object execScript(final String script, final Map<?, ?> tempVars) {
        return this.execScript(script, tempVars, this.isCacheStmt);
    }
    
    public Object execScript(final String script, final Map<?, ?> tempVars, final boolean isCache) {
        if (isCache && this.stmtCache != null) {
            final ExecutableStatement es = this.stmtCache.get(script);
            if (es != null) {
                return this.execScript(es, tempVars);
            }
        }
        final Object o = this.miscCache.compileWhenNecessary(script);
        if (o instanceof String) {
            return MVEL.eval((String)o, (Object)this.miscCache.getParserContext(), this.thisFactory);
        }
        if (isCache && this.stmtCache != null) {
            this.stmtCache.put(script, (ExecutableStatement)o);
        }
        return this.execScript((ExecutableStatement)o, tempVars);
    }
    
    public MVELMiscCache getMiscCache() {
        return this.miscCache;
    }
    
    public VariableResolverFactory getVariableResolverFactory() {
        return this.thisFactory;
    }
    
    public void prepare(final Object... args) {
        final Map<String, Object> margs = new HashMap<String, Object>();
        VariableResolverFactory factory;
        final VariableResolverFactory factortyFirst = factory = (VariableResolverFactory)new CachingMapVariableResolverFactory((Map)margs);
        int i = 0;
        for (final Object o : args) {
            margs.put("$" + i, o);
            margs.put("arg" + i, o);
            margs.put("\ufffd\ufffd\ufffd\ufffd" + i, o);
            ++i;
            if (o instanceof VariableResolverFactory) {
                factory.setNextFactory((VariableResolverFactory)o);
                factory = (VariableResolverFactory)o;
            }
            else if (o instanceof IMVELScriptCompatible) {
                final VariableResolverFactory factorynext = (VariableResolverFactory)new MVELVariableResolverFactory((IMVELScriptCompatible)o);
                factory.setNextFactory(factorynext);
                factory = factorynext;
            }
            else if (o instanceof Map) {
                final VariableResolverFactory factorynext = (VariableResolverFactory)new CachingMapVariableResolverFactory((Map)o);
                factory.setNextFactory(factorynext);
                factory = factorynext;
            }
        }
        factory.setNextFactory(this.miscCache.getFunctionFactory());
        this.thisFactory = factortyFirst;
    }
    
    public void setMiscCache(final MVELMiscCache miscCache) {
        this.miscCache = miscCache;
    }
    
    static {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;
        OptimizerFactory.setDefaultOptimizer(OptimizerFactory.SAFE_REFLECTIVE);
    }
}
