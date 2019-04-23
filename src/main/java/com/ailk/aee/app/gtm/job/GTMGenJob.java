// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import java.util.Map;

public class GTMGenJob extends GTMGenInstJob
{
    public void initializeJob(final Map<String, String> m) {
        this.selectSQL = "SELECT * FROM td_gtm_trigger t WHERE last_update_state='1' AND t.trigger_id =:TRIGGER_ID";
        this.updateExceptionStateSQL = "UPDATE td_gtm_trigger t SET last_update_state='3',last_update_info=:RESULT_INFO WHERE t.trigger_id =:TRIGGER_ID";
        this.updateLoadStateSQL = "UPDATE td_gtm_trigger t SET last_update_state='2',last_update_info='' WHERE t.trigger_id =:TRIGGER_ID";
        this.updateFinishStateSQL = "UPDATE td_gtm_trigger SET last_update_state='0',last_update_info=:RESULT_INFO ,last_update_time = to_date(:LAST_UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') WHERE trigger_id = :TRIGGER_ID";
    }
}
