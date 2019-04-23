// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.util.Map;
import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IInputStreamParser.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IInputStreamParser
{
    Map<String, String> parser(final InputStream p0) throws Exception;
}
