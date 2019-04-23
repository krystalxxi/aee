// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sql;

import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.common.conf.Configuration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.sql.CallableStatement;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: PrepareByNameProcedure.java 64483 2013-11-10 12:02:57Z huwl $")
public class PrepareByNameProcedure
{
    private CallableStatement prepstmt;
    private String orisql;
    private String bindsql;
    private Hashtable<String, ParamInfo> params;
    private Hashtable<Integer, Object> values;
    private ArrayList<String> outs;
    
    public PrepareByNameProcedure(final Connection conn, final String sql) throws SQLException {
        this.prepstmt = null;
        this.params = new Hashtable<String, ParamInfo>();
        this.values = new Hashtable<Integer, Object>();
        this.outs = new ArrayList<String>();
        this.parseParams(this.orisql = sql);
        this.prepstmt = conn.prepareCall("{ call " + this.bindsql + "}");
    }
    
    private void addParamInfo(final int cnt, final String fieldname) {
        if (this.params.containsKey(fieldname)) {
            final ParamInfo p = this.params.get(fieldname);
            final int psize = p.bindIndex.length;
            final int[] iv = new int[psize + 1];
            for (int i = 0; i < psize; ++i) {
                iv[i] = p.bindIndex[i];
            }
            iv[psize] = cnt;
            p.bindIndex = iv;
        }
        else {
            final ParamInfo p = new ParamInfo();
            p.paramName = fieldname;
            (p.bindIndex = new int[1])[0] = cnt;
            this.params.put(p.paramName, p);
        }
    }
    
    public void autoMap(final Map<String, String> in) throws SQLException, NoThisVariableException {
        final String[] ss = this.getStringParams();
        if (ss != null && ss.length > 0) {
            for (final String s : ss) {
                if (in.containsKey(s)) {
                    this.setString(s, in.get(s));
                }
                else {
                    final int[] idx = this.getIndexByFieldName(s);
                    if (idx != null) {
                        for (final int i : idx) {
                            this.prepstmt.registerOutParameter(i, 12);
                        }
                    }
                    this.outs.add(s);
                }
            }
        }
    }
    
    public void bindValue(final int i, final Object s) {
        this.values.put(new Integer(i), s);
    }
    
    public void close() {
        if (this.prepstmt != null) {
            try {
                this.prepstmt.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    public Map<String, String> execute() throws SQLException {
        final Map<String, String> res = new HashMap<String, String>();
        this.prepstmt.execute();
        if (this.outs.size() > 0) {
            for (final String s : this.outs) {
                final int[] idx = this.getIndexByFieldName(s);
                if (idx != null) {
                    for (final int i : idx) {
                        final String v = this.prepstmt.getString(i);
                        res.put(s, v);
                    }
                }
            }
        }
        return res;
    }
    
    private int[] getIndexByFieldName(final String name) {
        if (this.params.containsKey(name)) {
            return this.params.get(name).bindIndex;
        }
        return null;
    }
    
    public String getSql() {
        return this.orisql;
    }
    
    public String[] getStringParams() {
        return this.params.keySet().toArray(new String[0]);
    }
    
    private void parseParams(final String s) {
        boolean inQuote = false;
        boolean inVar = false;
        final int slength = s.length();
        int vstart = 0;
        int vend = 0;
        int varcnt = 0;
        final StringBuffer sqlbuffer = new StringBuffer();
        for (int i = 0; i < slength + 1; ++i) {
            char c;
            if (i < slength) {
                c = s.charAt(i);
            }
            else {
                c = ' ';
            }
            if (c == '\'') {
                inQuote = !inQuote;
                sqlbuffer.append(c);
            }
            else if (!inQuote && c == ':') {
                inVar = true;
                vstart = i + 1;
            }
            else if (!inQuote && inVar && (c == ')' || c == '(' || c == ' ' || c == ',' || i == slength || Character.isWhitespace(c) || c == '=')) {
                vend = i;
                ++varcnt;
                this.addParamInfo(varcnt, s.substring(vstart, vend));
                sqlbuffer.append(" ? ");
                sqlbuffer.append(c);
                inVar = false;
            }
            else if (!inVar) {
                sqlbuffer.append(c);
            }
        }
        this.bindsql = sqlbuffer.toString();
    }
    
    public void setString(final String fieldName, final String value) throws NoThisVariableException, SQLException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.prepstmt.setString(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    public void setValueByMap(final Map<String, String> in, final Map<String, String> out) throws SQLException, NoThisVariableException {
        final String[] ss = this.getStringParams();
        this.outs.clear();
        if (ss != null && ss.length > 0) {
            for (final String s : ss) {
                if (in.containsKey(s)) {
                    this.setString(s, in.get(s));
                }
                if (out.containsKey(s)) {
                    final int[] idx = this.getIndexByFieldName(s);
                    if (idx != null) {
                        for (final int i : idx) {
                            this.prepstmt.registerOutParameter(i, 12);
                        }
                    }
                    this.outs.add(s);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append(" With{\n       PROCEDURE    : [");
        sb.append(this.orisql);
        sb.append("]\n");
        sb.append("ChangedSQL    : [ { call ");
        sb.append(this.bindsql);
        sb.append("}");
        sb.append("]\n");
        if (this.params.size() > 0) {
            sb.append("Params        :\n");
            for (final ParamInfo p : this.params.values()) {
                sb.append("        ");
                sb.append(p.paramName);
                if (p.bindIndex != null) {
                    sb.append(":");
                    sb.append("-->{");
                    for (final int i : p.bindIndex) {
                        sb.append("index=");
                        sb.append(i);
                        if (this.values.containsKey(new Integer(i))) {
                            sb.append(",");
                            final String s = this.values.get(new Integer(i)).toString();
                            sb.append("value=[");
                            sb.append(s);
                            sb.append("]");
                        }
                        sb.append("  ");
                    }
                    sb.append("}");
                }
                sb.append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static void main(final String[] ars) {
        try {
            Configuration.getInstance().setLocalConfiguration("serviceprovider.DataBaseConnection.provider", "com.ailk.aee.database.sp.DBConnectionNonPoolServiceProvider");
            Configuration.getInstance().setLocalConfiguration("serviceprovider.DataBaseConnection.argument", "{configfile=G:\\business\\4_SRC_USED\\AEE1.1\\etc\\db.cfg}");
            final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
            final Connection conn = (Connection)isp.getService("ADB3");
            final String sql = "p_test(:V1,:V2,:V3)";
            final PrepareByNameProcedure p = new PrepareByNameProcedure(conn, sql);
            final Map<String, String> ms = new HashMap<String, String>();
            ms.put("V1", "hello world");
            p.autoMap(ms);
            final Map<String, String> resout = p.execute();
            System.out.println(resout);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    class ParamInfo
    {
        String paramName;
        int[] bindIndex;
    }
}
