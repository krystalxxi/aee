// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import org.mvel2.MacroProcessor;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.compiler.ExecutableStatement;
import com.ailk.aee.common.util.StringUtils;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import java.util.HashMap;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.ParserContext;
import org.mvel2.Macro;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: MVELMiscCache.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MVELMiscCache
{
    private Map<String, Macro> vars;
    private ParserContext pctx;
    private VariableResolverFactory functionFactory;
    
    public MVELMiscCache() {
        this.vars = new HashMap<String, Macro>();
        this.pctx = new ParserContext();
        this.functionFactory = (VariableResolverFactory)new CachingMapVariableResolverFactory((Map)new HashMap());
    }
    
    public void addByType(final String miscType, final String miscName, final String miscScript) {
        if (miscType.equalsIgnoreCase("MACRO") || miscType.equals("M")) {
            this.addMacro(miscName, miscScript);
        }
        else if (miscType.equalsIgnoreCase("FUNCTION") || miscType.equals("F")) {
            this.addFunction(miscScript);
        }
        else if (miscType.equalsIgnoreCase("IMPORT") || miscType.equals("I")) {
            this.addImport(miscName, miscScript);
        }
        else if (miscType.equalsIgnoreCase("PACKAGE") || miscType.equals("P")) {
            this.addPackageImport(miscScript);
        }
    }
    
    public void addFunction(final String funcDef) {
        final ExpressionCompiler compiler = new ExpressionCompiler(funcDef);
        final CompiledExpression es = compiler.compile();
        MVEL.executeExpression((Object)es, (Object)this.pctx, this.functionFactory);
    }
    
    public void addImport(final String c, final Class clazz) {
        this.pctx.addImport(c, clazz);
    }
    
    public void addImport(final String c, final String clazz) {
        try {
            final Class mc = Class.forName(clazz);
            this.addImport(c, mc);
        }
        catch (ClassNotFoundException ex) {}
    }
    
    public void addMacro(final String name, final Macro m) {
        this.vars.put(name, m);
    }
    
    public void addMacro(final String macro, final String s) {
        this.vars.put(macro, (Macro)new Macro() {
            public String doMacro() {
                return s;
            }
        });
    }
    
    public void addPackageImport(final String imports) {
        final String[] importss = StringUtils.split(imports, ";");
        if (importss != null) {
            for (final String si : importss) {
                String sii = si.trim();
                sii = StringUtils.replace(sii, "\r", "");
                sii = StringUtils.replace(sii, "\n", "");
                if (sii.startsWith("import ")) {
                    sii = StringUtils.replaceOnce(sii, "import ", "");
                }
                sii = si.trim();
                this.pctx.addPackageImport(sii);
            }
        }
    }
    
    public void clearFunction() {
        this.functionFactory = (VariableResolverFactory)new CachingMapVariableResolverFactory((Map)new HashMap());
    }
    
    public void clearMacro() {
        this.vars.clear();
    }
    
    public void clearParserContext() {
        this.pctx = new ParserContext();
    }
    
    public ExecutableStatement compile(final String script2) {
        final String script3 = this.processMacro(script2);
        final ExecutableStatement es = (ExecutableStatement)MVEL.compileExpression(script3, this.pctx);
        return es;
    }
    
    public CompiledTemplate compileTemplate(final String template2) {
        final String template3 = this.processMacro(template2);
        final CompiledTemplate ct = TemplateCompiler.compileTemplate(template3, this.pctx);
        return ct;
    }
    
    public Object compileTemplateWhenNecessary(final String script) {
        if (script.startsWith("@comment{NO_CACHE}")) {
            return script;
        }
        return this.compileTemplate(script);
    }
    
    public Object compileWhenNecessary(final String script) {
        if (script.startsWith("/*NO_CACHE*/")) {
            return script;
        }
        return this.compile(script);
    }
    
    public Map<String, Macro> getAllMacros() {
        return this.vars;
    }
    
    public VariableResolverFactory getFunctionFactory() {
        return this.functionFactory;
    }
    
    public ParserContext getParserContext() {
        return this.pctx;
    }
    
    public String processMacro(final String s) {
        if (this.vars.size() == 0) {
            return s;
        }
        final MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros((Map)this.vars);
        final String v = macroProcessor.parse(s);
        return v;
    }
}
