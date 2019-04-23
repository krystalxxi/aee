// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.service.stat;

import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: IStatReporter.java 60270 2013-11-03 14:48:37Z tangxy $")
public interface IStatReporter
{
    public static final IStatReporter NULLCounter = new IStatReporter() {
        @Override
        public void update() {
        }
        
        @Override
        public void update(final long l) {
        }
    };
    public static final IStatReporter NULLTimer = new IStatReporter() {
        @Override
        public void update() {
        }
        
        @Override
        public void update(final long l) {
        }
    };
    
    void update();
    
    void update(final long p0);
}
