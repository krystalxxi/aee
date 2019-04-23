// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util.watchdog;

import java.util.ArrayList;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: WatchDogMgr.java 60270 2013-11-03 14:48:37Z tangxy $")
public class WatchDogMgr
{
    private List<IWatchDog> dogs;
    private static WatchDogMgr dogmgr;
    
    public static WatchDogMgr getInstance() {
        return WatchDogMgr.dogmgr;
    }
    
    private WatchDogMgr() {
        this.dogs = new ArrayList<IWatchDog>();
    }
    
    public void addDog(final IWatchDog d) {
        this.dogs.add(d);
    }
    
    public List<IWatchDog> getDogs() {
        return this.dogs;
    }
    
    static {
        WatchDogMgr.dogmgr = new WatchDogMgr();
    }
}
