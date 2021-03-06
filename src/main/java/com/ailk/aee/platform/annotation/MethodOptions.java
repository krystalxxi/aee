// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.annotation;

import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CVSID("$Id: MethodOptions.java 60270 2013-11-03 14:48:37Z tangxy $")
public @interface MethodOptions {
    MethodOption[] options();
    
    boolean strickCheck() default false;
}
