// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.ailk.aee.common.util.text.StrSubstitutor;
import com.ailk.aee.common.util.text.StrLookup;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import java.io.File;
import com.ailk.aee.common.conf.Configuration;

public class AEELogger
{
    public static void configureDebugLogger(final String workName) {
        final String aeeBase = Configuration.getValue("AEE_HOME");
        final Properties p = getConfiguration();
        final String vp = aeeBase + File.separator + "log" + File.separator + workName + File.separator + "aee_" + workName + ".log";
        p.put("log4j.appender.AEELOG.File", vp);
        p.put("log4j.category.AEE.logger.works." + workName, "DEBUG,AEELOG");
        PropertyConfigurator.configure(p);
    }
    
    public static void configureLogger() {
        final Properties source = getConfiguration();
        if (source == null) {
            return;
        }
        final Set<Map.Entry<Object, Object>> set = source.entrySet();
        final Properties target = new Properties();
        final StrSubstitutor ss = new StrSubstitutor((StrLookup)new StrLookup<String>() {
            public String lookup(final String key) {
                return Configuration.getValue(key);
            }
        });
        for (final Map.Entry<Object, Object> entry : set) {
            target.put(entry.getKey(), ss.replace(entry.getValue()));
        }
        PropertyConfigurator.configure(target);
    }
    
    public static Properties getConfiguration() {
        String v = Configuration.getValue("LOG4J_CONF_FILE");
        Properties b = null;
        if (v != null) {
            b = getLogProperties(v);
        }
        if (b != null && b.size() > 0) {
            return b;
        }
        v = "log4j.properties";
        b = getLogProperties(v);
        if (b != null && b.size() > 0) {
            return b;
        }
        v = "logger.properties";
        b = getLogProperties(v);
        if (b != null && b.size() > 0) {
            return b;
        }
        return null;
    }
    
    private static Properties getLogProperties(final String v) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aconst_null    
        //     1: astore_1        /* istream */
        //     2: new             Ljava/io/File;
        //     5: dup            
        //     6: aload_0         /* v */
        //     7: invokespecial   java/io/File.<init>:(Ljava/lang/String;)V
        //    10: astore_2        /* f */
        //    11: aload_2         /* f */
        //    12: invokevirtual   java/io/File.exists:()Z
        //    15: ifeq            55
        //    18: new             Ljava/util/Properties;
        //    21: dup            
        //    22: invokespecial   java/util/Properties.<init>:()V
        //    25: astore_3        /* props */
        //    26: aload_3         /* props */
        //    27: new             Ljava/io/FileInputStream;
        //    30: dup            
        //    31: aload_2         /* f */
        //    32: invokespecial   java/io/FileInputStream.<init>:(Ljava/io/File;)V
        //    35: invokevirtual   java/util/Properties.load:(Ljava/io/InputStream;)V
        //    38: goto            53
        //    41: astore          e
        //    43: goto            53
        //    46: astore          e
        //    48: aload           e
        //    50: invokevirtual   java/io/IOException.printStackTrace:()V
        //    53: aload_3         /* props */
        //    54: areturn        
        //    55: new             Ljava/util/Properties;
        //    58: dup            
        //    59: invokespecial   java/util/Properties.<init>:()V
        //    62: astore_3        /* props */
        //    63: ldc_w           Lcom/ailk/aee/AEELogger;.class
        //    66: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //    69: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //    72: aload_0         /* v */
        //    73: invokevirtual   java/lang/ClassLoader.getResourceAsStream:(Ljava/lang/String;)Ljava/io/InputStream;
        //    76: astore_1        /* istream */
        //    77: aload_3         /* props */
        //    78: aload_1         /* istream */
        //    79: invokevirtual   java/util/Properties.load:(Ljava/io/InputStream;)V
        //    82: aload_3         /* props */
        //    83: astore          4
        //    85: aload_1         /* istream */
        //    86: ifnull          93
        //    89: aload_1         /* istream */
        //    90: invokevirtual   java/io/InputStream.close:()V
        //    93: goto            98
        //    96: astore          e
        //    98: aload           4
        //   100: areturn        
        //   101: astore_3        /* props */
        //   102: aload_1         /* istream */
        //   103: ifnull          110
        //   106: aload_1         /* istream */
        //   107: invokevirtual   java/io/InputStream.close:()V
        //   110: goto            135
        //   113: astore_3        /* e */
        //   114: goto            135
        //   117: astore          6
        //   119: aload_1         /* istream */
        //   120: ifnull          127
        //   123: aload_1         /* istream */
        //   124: invokevirtual   java/io/InputStream.close:()V
        //   127: goto            132
        //   130: astore          e
        //   132: aload           6
        //   134: athrow         
        //   135: new             Lcom/ailk/aee/common/conf/FileConfigurationFactory;
        //   138: dup            
        //   139: aload_0         /* v */
        //   140: ldc             "com.ailk.common.conf.parsetype.prop"
        //   142: invokespecial   com/ailk/aee/common/conf/FileConfigurationFactory.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   145: astore_3        /* fcf */
        //   146: aload_3         /* fcf */
        //   147: invokevirtual   com/ailk/aee/common/conf/FileConfigurationFactory.search:()Ljava/io/InputStream;
        //   150: astore          is
        //   152: aload           is
        //   154: ifnull          181
        //   157: new             Ljava/util/Properties;
        //   160: dup            
        //   161: invokespecial   java/util/Properties.<init>:()V
        //   164: astore          props
        //   166: aload           props
        //   168: aload           is
        //   170: invokevirtual   java/util/Properties.load:(Ljava/io/InputStream;)V
        //   173: goto            178
        //   176: astore          e
        //   178: aload           props
        //   180: areturn        
        //   181: aconst_null    
        //   182: areturn        
        //    LocalVariableTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ---------------------------------------------------
        //  43     0       4     e        Ljava/io/FileNotFoundException;
        //  48     5       4     e        Ljava/io/IOException;
        //  26     29      3     props    Ljava/util/Properties;
        //  98     0       5     e        Ljava/io/IOException;
        //  63     38      3     props    Ljava/util/Properties;
        //  102    0       3     ex       Ljava/lang/Exception;
        //  114    0       3     e        Ljava/io/IOException;
        //  132    0       7     e        Ljava/io/IOException;
        //  178    0       6     e        Ljava/io/IOException;
        //  166    15      5     props    Ljava/util/Properties;
        //  0      183     0     v        Ljava/lang/String;
        //  2      181     1     istream  Ljava/io/InputStream;
        //  11     172     2     f        Ljava/io/File;
        //  146    37      3     fcf      Lcom/ailk/aee/common/conf/FileConfigurationFactory;
        //  152    31      4     is       Ljava/io/InputStream;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                           
        //  -----  -----  -----  -----  -------------------------------
        //  26     38     41     46     Ljava/io/FileNotFoundException;
        //  26     38     46     53     Ljava/io/IOException;
        //  85     93     96     98     Ljava/io/IOException;
        //  55     85     101    183    Ljava/lang/Exception;
        //  102    110    113    117    Ljava/io/IOException;
        //  55     85     117    135    Any
        //  101    102    117    135    Any
        //  119    127    130    132    Ljava/io/IOException;
        //  117    119    117    135    Any
        //  166    173    176    178    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index: 92, Size: 92
        //     at java.util.ArrayList.rangeCheck(ArrayList.java:635)
        //     at java.util.ArrayList.get(ArrayList.java:411)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3303)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3551)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:113)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
}
