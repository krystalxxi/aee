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
@CVSID("$Id: MethodOption.java 60270 2013-11-03 14:48:37Z tangxy $")
public @interface MethodOption {
    ArguType arguType() default ArguType.OPT_WITH_ARG;
    
    String defValue() default "";
    
    String description() default "";
    
    String name();
    
    OptionUseType optionUseType() default OptionUseType.OPT_USE;
    
    public enum ArguType
    {
        MUST_WITH_ARG, 
        OPT_WITH_ARG, 
        MUST_NOTWITH_ARG;
    }
    
    public enum OptionUseType
    {
        NEED_USE, 
        OPT_USE, 
        HIDE_USE;
    }
}
