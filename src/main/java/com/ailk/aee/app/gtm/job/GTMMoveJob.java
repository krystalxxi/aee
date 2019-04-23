// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import java.util.Map;

public class GTMMoveJob extends GTMMoveHisJob
{
    public void initializeJob(final Map<String, String> m) {
        this.insertSQL = "insert into TL_GTM_WORKINST_h select * from TL_GTM_WORKINST t where t.work_inst_id =:WORK_INST_ID";
        this.deleteSQL = "delete from TL_GTM_WORKINST t where t.work_inst_id =:WORK_INST_ID";
    }
}
