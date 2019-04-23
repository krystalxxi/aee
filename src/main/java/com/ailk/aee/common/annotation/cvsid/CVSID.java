// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.annotation.cvsid;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@CVSID("$Id: CVSID.java 60270 2013-11-03 14:48:37Z tangxy $")
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CVSID {
    String value();
}
