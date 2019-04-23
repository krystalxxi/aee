// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sql;

import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ResultSetTool.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ResultSetTool
{
    public static String getValueByResultSet(final ResultSet rs, final int type, final String name) throws SQLException {
        if (type == 2004) {
            return rs.getString(name);
        }
        if (type == 91) {
            final java.sql.Date date = rs.getDate(name);
            if (date == null) {
                return null;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        else {
            if (type != 93) {
                return rs.getString(name);
            }
            final Timestamp timestamp = rs.getTimestamp(name);
            if (timestamp == null) {
                return null;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.sql.Date(timestamp.getTime()));
        }
    }
    
    public static List<Map<String, String>> rs2ListMap(final ResultSet rs) throws SQLException {
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (rs.next()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            do {
                HashMap<String, String> data = new HashMap<String, String>();
                for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
                    final String name = rsmd.getColumnName(i).toUpperCase();
                    final String value = getValueByResultSet(rs, rsmd.getColumnType(i), name);
                    data.put(name, value);
                }
                list.add(data);
                data = null;
            } while (rs.next());
            rsmd = null;
        }
        return list;
    }
}
