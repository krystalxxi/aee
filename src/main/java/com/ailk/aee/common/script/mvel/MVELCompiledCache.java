// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.mvel;

import java.util.Collection;
import java.util.Set;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.compiler.ExecutableStatement;
import com.ailk.aee.common.util.ConcurrentLRUHashMap;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.util.Map;

@CVSID("$Id: MVELCompiledCache.java 60270 2013-11-03 14:48:37Z tangxy $")
public class MVELCompiledCache<T> implements Map<String, T>
{
    private ConcurrentLRUHashMap<String, T> caches;
    private static MVELCompiledCache<ExecutableStatement> statementCache;
    private static MVELCompiledCache<CompiledTemplate> templateCache;
    
    public static MVELCompiledCache<ExecutableStatement> buildStatementCache() {
        return new MVELCompiledCache<ExecutableStatement>();
    }
    
    public static MVELCompiledCache<ExecutableStatement> buildStatementCache(final int size) {
        return new MVELCompiledCache<ExecutableStatement>(size);
    }
    
    public static MVELCompiledCache<CompiledTemplate> buildTemplateCache() {
        return new MVELCompiledCache<CompiledTemplate>();
    }
    
    public static MVELCompiledCache<CompiledTemplate> buildTemplateCache(final int size) {
        return new MVELCompiledCache<CompiledTemplate>(size);
    }
    
    public static MVELCompiledCache<CompiledTemplate> getTemplateCache() {
        return MVELCompiledCache.templateCache;
    }
    
    public static MVELCompiledCache<ExecutableStatement> getStatementCache() {
        return MVELCompiledCache.statementCache;
    }
    
    public MVELCompiledCache() {
        this.caches = new ConcurrentLRUHashMap<String, T>(512);
    }
    
    public MVELCompiledCache(final int t) {
        this.caches = new ConcurrentLRUHashMap<String, T>(512);
        this.caches = new ConcurrentLRUHashMap<String, T>(t);
    }
    
    @Override
    public void clear() {
        this.caches.clear();
    }
    
    public Object clone() {
        return null;
    }
    
    @Override
    public boolean containsKey(final Object arg0) {
        return this.caches.containsKey(arg0);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.caches.containsValue(value);
    }
    
    @Override
    public Set<Entry<String, T>> entrySet() {
        return this.caches.entrySet();
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return this.caches.equals(arg0);
    }
    
    @Override
    public T get(final Object key) {
        return this.caches.get(key);
    }
    
    @Override
    public int hashCode() {
        return this.caches.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.caches.isEmpty();
    }
    
    @Override
    public Set<String> keySet() {
        return this.caches.keySet();
    }
    
    @Override
    public T put(final String arg0, final T arg1) {
        return this.caches.put(arg0, arg1);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends T> arg0) {
        this.caches.putAll(arg0);
    }
    
    @Override
    public T remove(final Object arg0) {
        return this.caches.remove(arg0);
    }
    
    @Override
    public int size() {
        return this.caches.size();
    }
    
    @Override
    public String toString() {
        return this.caches.toString();
    }
    
    @Override
    public Collection<T> values() {
        return this.caches.values();
    }
    
    static {
        MVELCompiledCache.statementCache = new MVELCompiledCache<ExecutableStatement>();
        MVELCompiledCache.templateCache = new MVELCompiledCache<CompiledTemplate>();
    }
}
