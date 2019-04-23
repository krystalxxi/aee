// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.app.gtm.job;

import com.ailk.aee.app.gtm.GTMPool;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.core.Job;
import com.ailk.aee.common.stringobject.StringMapConverter;
import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.core.IJobSession;
import java.util.HashSet;
import com.ailk.aee.job.TableDataJob;

public class GTMRunInstJob extends TableDataJob
{
    private static HashSet<String> set;
    
    public Map<String, String> doByData(final IJobSession ctx, final Map<String, String> rec) throws Exception {
        final Map<String, String> returnInfo = new HashMap<String, String>();
        returnInfo.put("RESULT_CODE", "0");
        returnInfo.put("RESULT_INFO", "OK.");
        if (rec != null) {
            final String workInstId = rec.get("WORK_INST_ID");
            final String workArgument = rec.get("WORK_ARGU");
            final String workClass = rec.get("WORK_CLASS");
            final String workId = rec.get("WORK_ID");
            final String workName = rec.get("WORK_NAME");
            final Map<String, String> argu = new HashMap<String, String>();
            argu.put("workID", workId);
            argu.put("workName", workName);
            if (workArgument != null && workArgument.length() > 0) {
                final StringMapConverter smc = new StringMapConverter();
                if (!smc.canWrapFromString(workArgument)) {
                    throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\u02bd\ufffd\ufffd\ufffd\ufffd\u0237.");
                }
                final Map<String, String> t = (Map<String, String>)smc.wrapFromString(workArgument);
                if (t != null && t.size() > 0) {
                    argu.putAll(t);
                }
            }
            boolean forked = false;
            final String isForked = argu.get("FORKED");
            if (isForked == null) {
                forked = true;
            }
            else if (isForked.equalsIgnoreCase("YES") || isForked.equalsIgnoreCase("TRUE") || isForked.equalsIgnoreCase("Y") || isForked.equalsIgnoreCase("T")) {
                forked = true;
            }
            if (!forked) {
                try {
                    if (workId != null && workId.length() > 0) {
                        synchronized (GTMRunInstJob.set) {
                            if (GTMRunInstJob.set.contains(workId)) {
                                returnInfo.put("RESULT_INFO", "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u05b4\ufffd\u0435\ufffdWORK_ID.");
                                return returnInfo;
                            }
                            GTMRunInstJob.set.add(workId);
                        }
                    }
                    final Job job = (Job)ObjectBuilder.build((Class)Job.class, workClass, (Map)argu);
                    job.initializeJob((Map)argu);
                    final IJobSession session = job.newSession((Object)null);
                    job.prepare(session);
                    job.execute(session);
                    job.finish(session);
                    job.finalizeJob();
                }
                finally {
                    if (workId != null && workId.length() > 0) {
                        synchronized (GTMRunInstJob.set) {
                            GTMRunInstJob.set.remove(workId);
                        }
                    }
                }
            }
            else {
                GTMPool.getInstance().startWork(workInstId, workId, null);
            }
        }
        return returnInfo;
    }
    
    static {
        GTMRunInstJob.set = new HashSet<String>();
    }
}
