// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: NULLTimer.java 60270 2013-11-03 14:48:37Z tangxy $")
public class NULLTimer extends Timer
{
    public NULLTimer(final String name) {
        super(name);
    }
    
    @Override
    public void update() {
        super.update();
    }
    
    @Override
    public void update(final long l) {
        super.update(l);
    }
}
