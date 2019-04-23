// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import com.ailk.aee.app.gtm.ITriggerAcceptor;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.app.gtm.trigger.TimeTrigger;
import com.ailk.aee.app.gtm.trigger.CronExprTrigger;
import com.ailk.aee.common.util.ExceptionUtils;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import java.util.Map;
import com.ailk.aee.common.util.text.StrSubstitutor;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import com.ailk.aee.job.TableDataJob;

public class GTMGenInstJob extends TableDataJob
{
    private long interval;
    protected String desTablePrefix;
    protected String insertSQL;
    
    public GTMGenInstJob() {
        this.interval = 600000L;
        this.desTablePrefix = "TL_GTM";
        this.insertSQL = "insert into " + this.desTablePrefix + "_WORKINST (" + "WORK_INST_ID," + "WORK_ID," + "WORK_NAME," + "FIRE_EXEC_TIME," + "WORK_CLASS," + "WORK_ARGU," + "CREATE_DATE," + "WORK_STATUS) " + "select " + "seq_gtm_workinst.nextval," + ":WORK_ID," + ":WORK_NAME," + "to_date(:FIRE_EXEC_TIME,'yyyy-mm-dd hh24:mi:ss')," + ":WORK_CLASS," + ":WORK_ARGU," + "sysdate," + "'0' " + "from dual";
    }
    
    protected void initCheck() throws Exception {
        super.initCheck();
        if (this.interval <= 0L) {
            this.interval = 15000L;
        }
    }
    
    private void fireWork(final String id, final String name, final Date d, final String argtmp, final String argcls) throws Exception {
        final Map<String, String> m = new HashMap<String, String>();
        m.put("WORK_ID", id);
        m.put("WORK_NAME", name);
        m.put("FIRE_EXEC_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
        m.put("WORK_ARGU_TEMPLATE", argtmp);
        m.put("WORK_CLASS", argcls);
        final String arg = StrSubstitutor.replace((Object)argtmp, (Map)m);
        m.put("WORK_ARGU", arg);
        final Connection conn = (Connection)this.isp.getService(this.database);
        final PreparedByNameStatement stmt = new PreparedByNameStatement(conn, this.insertSQL);
        try {
            stmt.setValueByMap((Map)m);
            stmt.execute();
            conn.commit();
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private Date doFire(final IJobSession ctx, final Map<String, String> m) throws Exception {
        final Map<String, String> queryParam = new HashMap<String, String>();
        queryParam.putAll(m);
        Date currDate = null;
        if (m.get("SYSDATE") != null) {
            try {
                currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(m.get("SYSDATE"));
            }
            catch (Exception e) {
                AEEPlatform.getInstance().getLogger().error((Object)ExceptionUtils.getExceptionStack(e));
                currDate = new Date();
            }
        }
        else {
            currDate = new Date();
        }
        final Date endDate = new Date(currDate.getTime() + this.interval);
        final Map<String, String> workData = queryParam;
        final String workid = workData.get("WORK_ID");
        final String trigId = workData.get("TRIGGER_ID");
        if (null == trigId || 0 == trigId.trim().length()) {
            throw new Exception("trigger_id of record of work_id:" + workid + " is null, please check");
        }
        final String workname = workData.get("WORK_NAME");
        final String fireclass = workData.get("FIRE_CLASS");
        final String fireargu = workData.get("FIRE_ARGU");
        AEEPlatform.getInstance().getLogger().debug((Object)("work,work_id=" + workid));
        Date lastUpdateTime = null;
        final String lastUpdateTimeStr = workData.get("LAST_UPDATE_TIME");
        if (lastUpdateTimeStr == null || lastUpdateTimeStr.trim().length() == 0) {
            lastUpdateTime = currDate;
            AEEPlatform.getInstance().getLogger().debug((Object)"last update time is null, user");
        }
        else {
            lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastUpdateTimeStr);
        }
        String triggerclass = workData.get("TRIGGER_CLASS");
        final String triggerargu = workData.get("TRIGGER_ARGU");
        final String repeattag = workData.get("REPEAT_TAG");
        final String workclass = workData.get("EVT_TARGET_CLASS");
        final String workargu = workData.get("EVT_TARGET_ARGU");
        if (triggerclass == null || triggerclass.trim().length() == 0) {
            triggerclass = CronExprTrigger.class.getCanonicalName();
        }
        final TimeTrigger tt = (TimeTrigger)ObjectBuilder.build((Class)TimeTrigger.class, triggerclass, (Map)null);
        tt.setArgument(triggerargu);
        Date[] needFireDates = null;
        Date startDate = null;
        boolean needAdd = true;
        if (repeattag.equals("1")) {
            needAdd = false;
        }
        if (needAdd) {
            startDate = lastUpdateTime;
        }
        else {
            startDate = currDate;
        }
        if (startDate != currDate) {
            final Date[] temp = tt.getFireTimeBetween(startDate, currDate, -1);
            if (temp != null && temp.length > 0) {
                AEEPlatform.getInstance().getLogger().debug((Object)"\ufffd\ufffd\u013f\u01f0\u05f7\ufffd\ufffd\u04bb\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd.");
                this.fireWork(workid, workname, temp[temp.length - 1], workargu, workclass);
            }
            startDate = currDate;
        }
        needFireDates = tt.getFireTimeBetween(startDate, endDate, -1);
        if (needFireDates != null) {
            AEEPlatform.getInstance().getLogger().debug((Object)("work_id=" + workid + ",work_name=" + workname + " ;\ufffd\ufffd\u04aa\ufffd\ufffd\ufffd\ufffd" + needFireDates.length + "\ufffd\ufffd"));
            for (final Date firedate : needFireDates) {
                if (fireclass == null || fireclass.length() == 0) {
                    this.fireWork(workid, workname, firedate, workargu, workclass);
                }
                else {
                    this.fireWorkCustomer(fireclass, fireargu, workid, workname, firedate, workclass, workargu);
                }
            }
        }
        return endDate;
    }
    
    public void fireWorkCustomer(final String fireClass, final String fireArgu, final String workId, final String workName, final Date fireTime, final String targetClass, final String targetArgu) throws Exception {
        final ITriggerAcceptor tor = (ITriggerAcceptor)ObjectBuilder.build((Class)ITriggerAcceptor.class, fireClass, (Map)null);
        tor.onFire(fireArgu, workId, workName, fireTime, targetClass, targetArgu);
    }
    
    public Map<String, String> doByData(final IJobSession ctx, final Map<String, String> rec) throws Exception {
        final Map<String, String> returnInfo = new HashMap<String, String>();
        final Date d = this.doFire(ctx, rec);
        returnInfo.put("LAST_UPDATE_TIME", this.getDateString(d));
        return returnInfo;
    }
}
