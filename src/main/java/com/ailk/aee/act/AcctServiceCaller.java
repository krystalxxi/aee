package com.ailk.aee.act;

import com.ailk.aee.app.mwci.AbstractServiceCaller;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import com.ailk.aee.platform.AEEPlatform;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.Utility;
import com.ailk.service.client.ServiceFactory;
import com.wade.trace.TraceContext;

import java.sql.Connection;
import java.util.Map;
import java.util.StringTokenizer;


public class AcctServiceCaller extends AbstractServiceCaller {
    public AcctServiceCaller() {
    }

    public void log(String message) {
        try {
            AEEPlatform.getInstance().getLogger().info(message);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private IDataInput packageInput(Object o) {
        IData head = new DataMap();
        IData data = new DataMap();
        Map mObj = (Map)o;
        data.putAll(mObj);
        head.put("STAFF_EPARCHY_CODE", mObj.get("TRADE_EPARCHY_CODE"));
        head.put("LOGIN_EPARCHY_CODE", mObj.get("TRADE_EPARCHY_CODE"));
        head.put("STAFF_ID", mObj.get("TRADE_STAFF_ID"));
        head.put("CITY_CODE", mObj.get("TRADE_CITY_CODE"));
        head.put("DEPART_ID", mObj.get("TRADE_DEPART_ID"));
        head.put("SUBSYS_CODE", "AEE_ACCT");
        if(data.containsKey("X_TRACE_ID") && data.containsKey("X_PTRACE_ID")) {
            TraceContext.startAppProbe(data.getString("X_TRACE_ID"), data.getString("X_PTRACE_ID"), (String)null, (String)null, (String)null, (IData)null);
        }

        if(data.containsKey("ACCT_ID") && "0".equals(data.getString("ACCT_ID"))) {
            data.remove("ACCT_ID");
        }

        if(data.containsKey("BATCH_ID") && "0".equals(data.getString("BATCH_ID"))) {
            data.remove("BATCH_ID");
        }

        if(data.containsKey("CHARGE_ID") && "0".equals(data.getString("CHARGE_ID"))) {
            data.remove("CHARGE_ID");
        }

        DataInput input = new DataInput(head, data);
        input.setPagination((Pagination)null);
        return input;
    }

    public void callService(String svcUri, Object o) {
        IDataInput datainput = null;
        Object dataoutput = null;

        try {
            datainput = this.packageInput(o);
            this.log("serviceName:" + svcUri);
            this.log("datainput:" + datainput.toString());
            String addr = GlobalCfg.getProperty("service.router.addr");
            ServiceFactory.call(addr, svcUri, datainput, (Pagination)null, false, true);
        } catch (Exception var6) {
            this.onError(o, datainput, (IDataOutput)dataoutput, var6);
        }

    }

    public void onError(Object o, IDataInput datainput, IDataOutput dataoutput, Exception e) {
        Map<String, String> datamap = (Map)o;
        datamap.put("X_RESULTINFO", this.limitString(Utility.getBottomException(e).getMessage(), 4000));
        IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
        String database = (String)this.param.get("database");
        this.log("database==============" + database);
        Connection conn = (Connection)isp.getService(database);
        PreparedByNameStatement stmt = null;

        try {
            try {
                if(conn == null) {
                    throw new Exception("there is no database named " + database);
                }

                this.log("exception_sql===" + (String)this.param.get("exception_sql"));
                e.printStackTrace();
                StringTokenizer st = new StringTokenizer((String)this.param.get("exception_sql"), ";", false);

                while(st.hasMoreElements()) {
                    String sql = st.nextToken();
                    if(sql != null && sql.trim().length() > 0) {
                        stmt = new PreparedByNameStatement(conn, sql);
                        stmt.setValueByMap(datamap);
                        stmt.execute();
                        stmt.close();
                    }
                }

                conn.commit();
            } finally {
                if(stmt != null) {
                    stmt.close();
                }

                stmt = null;
                if(conn != null) {
                    conn.close();
                }

                conn = null;
            }
        } catch (Exception var16) {
            this.log("---ServiceFactory.call(" + database + ")--- dealException PreparedByNameStatement:" + var16.getMessage());
        }

    }

    public String limitString(String msg, int iBLimit) {
        String tempErrorInfo = msg;
        if(msg == null || msg.trim().length() == 0) {
            tempErrorInfo = "unknow error";
        }

        String finalErrorInfo = "";
        byte[] bytes = tempErrorInfo.getBytes();
        if(bytes.length <= iBLimit) {
            finalErrorInfo = tempErrorInfo;
        } else {
            byte[] newbytes = new byte[iBLimit];

            for(int i = 0; i < iBLimit; ++i) {
                newbytes[i] = bytes[i];
            }

            finalErrorInfo = new String(newbytes);
        }

        return finalErrorInfo;
    }
}
