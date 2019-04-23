package com.ailk.aee.app.mwci;


import com.ailk.aee.AEEExceptionProcessor;
import com.ailk.aee.common.stringobject.ObjectBuildException;
import com.ailk.aee.common.stringobject.ObjectBuilder;
import com.ailk.aee.common.stringobject.StringMapConverter;
import com.ailk.aee.core.IJobSession;
import com.ailk.aee.core.Job;

import java.util.HashMap;
import java.util.Map;

public class MWCJob extends Job {
    public MWCJob() {
    }

    public void execute(IJobSession ctx) throws Exception {
        Map<String, String> m = (Map)ctx.getPackagedObject();
        if(m == null) {
            throw new Exception("null object was given");
        } else {
            String workId = (String)m.get("MWC_WORK_ID");
            if(workId == null) {
                throw new Exception("an field called MWC_WORK_ID must in g_select_sql");
            } else {
                AbstractServiceCaller svc = this.getServiceCallerByWorkId(workId);
                if(svc == null) {
                    throw new Exception("can't not or build error when create service caller by WorkId" + workId);
                } else {
                    try {
                        svc.callService(this.getServiceURIByWorkId(workId), m);
                    } catch (Exception var6) {
                        this.processCallException(var6);
                    }
                }
            }
        }
    }

    private String getServiceURIByWorkId(String workId) {
        return MWCConf.getInstance().getServiceURIByWorkId(workId);
    }

    private void processCallException(Exception e) {
        e.printStackTrace();
        AEEExceptionProcessor.process(e);
    }

    private AbstractServiceCaller getServiceCallerByWorkId(String workId) {
        String s = MWCConf.getInstance().getServiceCallerByWorkId(workId);
        String p = MWCConf.getInstance().getServiceParamByWorkId(workId);
        if(s != null) {
            Map<String, String> ms = new HashMap();
            if(p != null && p.length() > 0) {
                ms = (new StringMapConverter()).wrapFromString(p);
            }

            try {
                AbstractServiceCaller as = (AbstractServiceCaller) ObjectBuilder.build(AbstractServiceCaller.class, s, (Map)ms);
                as.setAllParam((Map)ms);
                return as;
            } catch (ObjectBuildException var6) {
                var6.printStackTrace();
                AEEExceptionProcessor.process(var6);
                return null;
            }
        } else {
            return null;
        }
    }
}
