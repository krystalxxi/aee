// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import java.util.Set;
import org.mvel2.ast.Function;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.Macro;
import com.ailk.aee.common.util.StringUtils;
import java.util.Iterator;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.ParserContext;
import com.ailk.aee.common.util.ExceptionUtils;
import org.mvel2.MVEL;
import org.mvel2.integration.PropertyHandlerFactory;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.GlobalListenerFactory;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.Listener;
import org.mvel2.debug.Frame;
import org.mvel2.debug.Debugger;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.DebugTools;
import org.mvel2.compiler.ExpressionCompiler;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MVELDebugExecutor.java 60272 2013-11-03 15:29:24Z tangxy $")
public class MVELDebugExecutor
{
    Logger logger;
    
    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();
            final int init = 0;
            final MVELMiscCache x = new MVELMiscCache();
            if (init == 0) {
                x.addMacro("BRAND_CODE2", "{uca.getProductId()}");
                x.addMacro("V", "new Date().toString");
                x.addMacro("OUT", "System.out.println");
            }
            final Map<String, String> mconf = new HashMap<String, String>();
            mconf.put("NULL_SAFE", "true");
            final MVELExecutor e = new MVELExecutor(mconf);
            e.setMiscCache(x);
            x.getParserContext().addPackageImport("java.util");
            x.getParserContext().addImport("T", (Class)Date.class);
            final String funcdef = "def FUNC(x,y) {return 'AAA' +  x + y;}";
            x.addFunction(funcdef);
            final Map<String, String> map = new HashMap<String, String>();
            map.put("H", "HUWL");
            e.prepare(map);
            final String line1 = "System.out.println( \"a1\" );\n a=1+2;";
            final String line2 = "System.out.println(FUNC(a,2));\n\n";
            final String line3 = "System.out.println('aaa[' + H2 + ']'+ V() ); H;";
            final String expr = line1 + line2 + line3;
            final MVELDebugExecutor debug = new MVELDebugExecutor();
            debug.debugExecute(e, expr);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    public MVELDebugExecutor() {
        this.logger = Logger.getLogger((Class)MVELDebugExecutor.class);
    }
    
    public Object debugExecute(final MVELExecutor tor, final String expr) {
        return this.debugExecute(tor, expr, null);
    }
    
    public Object debugExecute(final MVELExecutor tor, final String expr, final Map<?, ?> tempvar) {
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append("\n").append("TITLE:            MVEL EXECUTOR  DETAIL").append("\n");
            this.writeEnv(tor, sb, tempvar);
            sb.append("SOURCE:==START==[\n");
            sb.append(this.genLineNumberedString(expr));
            sb.append("==END==]\n");
            final String exprMacro = tor.getMiscCache().processMacro(expr);
            sb.append("SOURCE WITH MACRO REPLACED:==START==[\n");
            sb.append(this.genLineNumberedString(exprMacro));
            sb.append("==END==]\n");
            final ExpressionCompiler compiler = new ExpressionCompiler(exprMacro);
            final ParserContext context = tor.getMiscCache().getParserContext().createSubcontext();
            context.setDebugSymbols(true);
            context.setSourceFile("STRINGSOURCE");
            final CompiledExpression compiled = compiler.compile(context);
            final String exprDecompiled = DebugTools.decompile(compiled);
            sb.append("DECOMPILED SOURCE WITH MACRO REPLACED SOURCE:[\n");
            sb.append(exprDecompiled);
            sb.append("]\n");
            for (int i = 1; i < context.getLineCount() + 1; ++i) {
                MVELRuntime.registerBreakpoint(context.getSourceFile(), i);
            }
            sb.append("DECOMPILED SOURCE WITH MACRO REPLACED SOURCE:[\n");
            final Map<String, Class> minputs = (Map<String, Class>)context.getInputs();
            if (minputs != null) {
                for (final Map.Entry<String, Class> e : minputs.entrySet()) {
                    sb.append("   INPUT:      name=[" + e.getKey() + "],type=[" + e.getValue().getCanonicalName() + "]\n");
                }
            }
            final Map<String, Class> mvars = (Map<String, Class>)context.getVariables();
            if (mvars != null) {
                for (final Map.Entry<String, Class> e2 : mvars.entrySet()) {
                    sb.append("VARIABLE:      name=[" + e2.getKey() + "],type=[" + e2.getValue().getCanonicalName() + "]\n");
                }
            }
            final Map mfunc = context.getFunctions();
            if (mfunc != null) {
                for (final Map.Entry e3 : mvars.entrySet()) {
                    sb.append("FUNCTION:      name=[" + e3.getKey() + "],value=[" + e3.getValue() + "]\n");
                }
            }
            final Map<String, Object> mimps = (Map<String, Object>)context.getImports();
            if (mimps != null) {
                for (final Map.Entry e4 : mvars.entrySet()) {
                    sb.append(" IMPORTS:      name=[" + e4.getKey() + "],value=[" + e4.getValue() + "]\n");
                }
            }
            sb.append("==END==]\n");
            final Debugger testDebugger = (Debugger)new Debugger() {
                public int onBreak(final Frame frame) {
                    sb.append("[RUN][BREAKPOINT]Source:" + frame.getSourceName() + "; line:" + frame.getLineNumber() + "]\n");
                    return 0;
                }
            };
            final Listener lget = (Listener)new Listener() {
                public void onEvent(final Object context, final String contextName, final VariableResolverFactory variableFactory, final Object value) {
                    sb.append("[RUN][EVENT  GET]OnEvent()-->[" + context + "],[" + contextName + "],[" + variableFactory + "],[" + value + "]\n");
                }
            };
            final Listener lset = (Listener)new Listener() {
                public void onEvent(final Object context, final String contextName, final VariableResolverFactory variableFactory, final Object value) {
                    sb.append("[RUN][EVENT SET]OnEvent()-->[" + context + "],[" + contextName + "],[" + variableFactory + "],[" + value + "]\n");
                }
            };
            GlobalListenerFactory.registerGetListener(lget);
            GlobalListenerFactory.registerSetListener(lset);
            PropertyHandlerFactory.registerPropertyHandler((Class)Map.class, (PropertyHandler)new PropertyHandler() {
                public Object getProperty(final String name, final Object contextObj, final VariableResolverFactory variableFactory) {
                    sb.append("[RUN][GET       ]getProperty()-->[" + name + "],[" + contextObj + "],[" + variableFactory + "]\n");
                    return "gotcalled";
                }
                
                public Object setProperty(final String name, final Object contextObj, final VariableResolverFactory variableFactory, final Object value) {
                    sb.append("[RUN][SET       ]setProperty()-->[" + name + "],[" + contextObj + "],[" + variableFactory + "],[" + value + "]\n");
                    return null;
                }
            });
            DEBUGVariableResolverFactory df = null;
            if (tempvar == null) {
                df = new DEBUGVariableResolverFactory(sb, tor.getVariableResolverFactory());
            }
            else {
                final VariableResolverFactory tempFactory = (VariableResolverFactory)new ReadOnlyCachingMapVariableResolverFactory(tempvar);
                tempFactory.setNextFactory(tor.getVariableResolverFactory());
                df = new DEBUGVariableResolverFactory(sb, tempFactory);
            }
            MVELRuntime.setThreadDebugger(testDebugger);
            final Object o = MVEL.executeDebugger(compiled, (Object)tor.getMiscCache().getParserContext(), (VariableResolverFactory)df);
            return o;
        }
        catch (Exception e5) {
            e5.printStackTrace();
            sb.append("[RUN][EXCEPTION ]" + ExceptionUtils.getExceptionStack(e5));
            return e5;
        }
        finally {
            this.logger.debug((Object)sb.toString());
            MVELRuntime.clearAllBreakpoints();
            MVELRuntime.resetDebugger();
            GlobalListenerFactory.disposeAll();
            PropertyHandler m = null;
            PropertyHandler v = null;
            if (PropertyHandlerFactory.hasNullMethodHandler()) {
                m = PropertyHandlerFactory.getNullMethodHandler();
            }
            if (PropertyHandlerFactory.hasNullPropertyHandler()) {
                v = PropertyHandlerFactory.getNullPropertyHandler();
            }
            PropertyHandlerFactory.disposeAll();
            if (m != null) {
                PropertyHandlerFactory.setNullMethodHandler(m);
            }
            if (v != null) {
                PropertyHandlerFactory.setNullPropertyHandler(v);
            }
        }
    }
    
    private String genLineNumberedString(final String es) {
        final String[] ss = StringUtils.splitByWholeSeparatorPreserveAllTokens(es, "\n");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ss.length; ++i) {
            sb.append(StringUtils.leftPad(Integer.toString(i + 1), 5, " ")).append(":  ").append(ss[i]).append("\n");
        }
        return sb.toString();
    }
    
    public Logger getLogger() {
        return this.logger;
    }
    
    public void writeEnv(final MVELExecutor tor, final StringBuilder sb, final Map<?, ?> tempvar) {
        sb.append("\nENV ==START==[\n");
        final ParserContext ctx = tor.getMiscCache().getParserContext();
        final Set<String> pkgimports = (Set<String>)ctx.getParserConfiguration().getPackageImports();
        if (pkgimports != null) {
            for (final String s : pkgimports) {
                sb.append("\tPACKAGE IMPORT: [" + s + "]\n");
            }
        }
        final Map<String, Object> imports = (Map<String, Object>)ctx.getImports();
        if (imports != null) {
            for (final Map.Entry<String, Object> e : imports.entrySet()) {
                sb.append("\t        IMPORT: [" + e.getKey() + "]=[" + e.getValue().toString() + "]\n");
            }
        }
        final Map<String, Macro> macros = tor.getMiscCache().getAllMacros();
        if (macros != null) {
            for (final Map.Entry<String, Macro> e2 : macros.entrySet()) {
                sb.append("\t         MACRO: [" + e2.getKey() + "]=[" + e2.getValue().doMacro() + "]\n");
            }
        }
        final VariableResolverFactory vf = tor.getMiscCache().getFunctionFactory();
        final BaseVariableResolverFactory vrf = (BaseVariableResolverFactory)vf;
        final Map<String, VariableResolver> mfuncs = (Map<String, VariableResolver>)vrf.getVariableResolvers();
        if (mfuncs != null) {
            for (final Map.Entry<String, VariableResolver> e3 : mfuncs.entrySet()) {
                final Function func = (Function)e3.getValue().getValue();
                String funcs = e3.getKey() + "(";
                if (func.hasParameters()) {
                    final String[] ss = func.getParameters();
                    for (int i = 0; i < ss.length; ++i) {
                        funcs += ss[i];
                        if (i != ss.length - 1) {
                            funcs += ",";
                        }
                    }
                }
                funcs += ")";
                sb.append("\t      FUNCTION: [" + funcs + "]\n");
            }
        }
        VariableResolverFactory factory = tor.getVariableResolverFactory();
        for (Map<VariableResolverFactory, Boolean> loopDetector = new HashMap<VariableResolverFactory, Boolean>(); factory != null && !loopDetector.containsKey(factory); factory = factory.getNextFactory()) {
            if (factory == vf) {
                sb.append("\t       FACTORY: [" + factory.toString() + "]\n");
                sb.append("\t             With Function Definition Above \n");
            }
            else {
                sb.append("\t       FACTORY: [" + factory.toString() + "]\n");
                sb.append("\t             With Variables[\n");
                final Set<String> allvars = (Set<String>)factory.getKnownVariables();
                for (final String svars : allvars) {
                    sb.append("\t             [" + svars + "]\n");
                }
                sb.append("\t                           ]\n");
            }
            loopDetector.put(factory, new Boolean(true));
        }
        if (tempvar != null) {
            sb.append("\t       FACTORY: [Temporary Variable Factory]\n");
            sb.append("\t                 With Temporary Variable: " + tempvar.keySet().toString() + "\n");
        }
    }
    
    static class DEBUGVariableResolverFactory implements VariableResolverFactory
    {
        private static final long serialVersionUID = 1L;
        private StringBuilder sb;
        private VariableResolverFactory trueFactory;
        
        public DEBUGVariableResolverFactory(final StringBuilder sbv, final VariableResolverFactory vtruefactory) {
            this.sb = null;
            this.trueFactory = null;
            this.sb = sbv;
            this.trueFactory = vtruefactory;
        }
        
        public VariableResolver createIndexedVariable(final int arg0, final String arg1, final Object arg2) {
            this.sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "]\n");
            return this.trueFactory.createIndexedVariable(arg0, arg1, arg2);
        }
        
        public VariableResolver createIndexedVariable(final int arg0, final String arg1, final Object arg2, final Class<?> arg3) {
            this.sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object,Class<?>)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "],[" + arg3 + "]\n");
            return this.trueFactory.createIndexedVariable(arg0, arg1, arg2);
        }
        
        public VariableResolver createVariable(final String arg0, final Object arg1) {
            this.sb.append("[RUN][FACTORY   ][createVariable(int,Object)]-->[" + arg0 + "],[" + arg1 + "]\n");
            return this.trueFactory.createVariable(arg0, arg1);
        }
        
        public VariableResolver createVariable(final String arg0, final Object arg1, final Class<?> arg2) {
            this.sb.append("[RUN][FACTORY   ][createIndexedVariable(int,String,Object,Class<?>)]-->[" + arg0 + "],[" + arg1 + "],[" + arg2.toString() + "]\n");
            return this.trueFactory.createVariable(arg0, arg1, (Class)arg2);
        }
        
        public VariableResolver getIndexedVariableResolver(final int arg0) {
            final VariableResolver vr = this.trueFactory.getIndexedVariableResolver(arg0);
            if (vr == null) {
                this.sb.append("[RUN][FACTORY   ][getIndexedVariableResolver(int)]-->[" + arg0 + "] return =[null]\n");
            }
            else {
                this.sb.append("[RUN][FACTORY   ][getIndexedVariableResolver(int)]-->[" + arg0 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;
        }
        
        public Set<String> getKnownVariables() {
            this.sb.append("[RUN][FACTORY   ][getIndexedVariableResolver()]-->[@see Env VariableSet]\n");
            return (Set<String>)this.trueFactory.getKnownVariables();
        }
        
        public VariableResolverFactory getNextFactory() {
            this.sb.append("[RUN][FACTORY   ][getNextFactory()]-->[]\n");
            return this.trueFactory.getNextFactory();
        }
        
        public VariableResolver getVariableResolver(final String arg0) {
            final VariableResolver vr = this.trueFactory.getVariableResolver(arg0);
            if (vr == null) {
                this.sb.append("[RUN][FACTORY   ][getVariableResolver(String)]-->[" + arg0 + "] return =[null]\n");
            }
            else {
                this.sb.append("[RUN][FACTORY   ][getVariableResolver(String)]-->[" + arg0 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;
        }
        
        public boolean isIndexedFactory() {
            this.sb.append("[RUN][FACTORY   ][isIndexedFactory()]-->[]\n");
            return this.trueFactory.isIndexedFactory();
        }
        
        public boolean isResolveable(final String arg0) {
            final boolean v = this.trueFactory.isResolveable(arg0);
            this.sb.append("[RUN][FACTORY   ][isResolveable(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }
        
        public boolean isTarget(final String arg0) {
            final boolean v = this.trueFactory.isTarget(arg0);
            this.sb.append("[RUN][FACTORY   ][isTarget(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }
        
        public VariableResolver setIndexedVariableResolver(final int arg0, final VariableResolver arg1) {
            final VariableResolver vr = this.trueFactory.setIndexedVariableResolver(arg0, arg1);
            if (vr == null) {
                this.sb.append("[RUN][FACTORY   ][setIndexedVariableResolver(int,VariableResolver)]-->[" + arg0 + "],[" + arg1 + "] return =[null]\n");
            }
            else {
                this.sb.append("[RUN][FACTORY   ][setIndexedVariableResolver(int,VariableResolver)]-->[" + arg0 + "],[" + arg1 + "] return =[" + vr.getName() + "],[" + vr.getClass() + "],[" + vr.getValue() + "]\n");
            }
            return vr;
        }
        
        public VariableResolverFactory setNextFactory(final VariableResolverFactory arg0) {
            this.sb.append("[RUN][FACTORY   ][setNextFactory(VariableResolverFactory)]-->[" + arg0 + "]\n");
            return this.trueFactory.setNextFactory(arg0);
        }
        
        public void setTiltFlag(final boolean arg0) {
            this.sb.append("[RUN][FACTORY   ][setTiltFlag(boolean)]-->[" + arg0 + "]\n");
            this.trueFactory.setTiltFlag(arg0);
        }
        
        public boolean tiltFlag() {
            final boolean v = this.trueFactory.tiltFlag();
            this.sb.append("[RUN][FACTORY   ][tiltFlag()]-->[ reutrn=[" + v + "]\n");
            return v;
        }
        
        public int variableIndexOf(final String arg0) {
            final int v = this.trueFactory.variableIndexOf(arg0);
            this.sb.append("[RUN][FACTORY   ][variableIndexOf(String)]-->[" + arg0 + "] reutrn=[" + v + "]\n");
            return v;
        }
    }
}
