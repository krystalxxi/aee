// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sql;

import java.util.Iterator;
import java.util.Map;
import java.net.URL;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.sql.NClob;
import java.sql.Timestamp;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.StringReader;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLWarning;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import java.sql.PreparedStatement;

@CVSID("$Id: PreparedByNameStatement.java 60270 2013-11-03 14:48:37Z tangxy $")
public class PreparedByNameStatement implements PreparedStatement
{
    private PreparedStatement prepstmt;
    private String orisql;
    private String bindsql;
    private Logger errorLog;
    private Hashtable<String, ParamInfo> params;
    private Hashtable<Integer, Object> values;
    
    public PreparedByNameStatement(final Connection conn, final String sql) throws SQLException {
        this.prepstmt = null;
        this.errorLog = Logger.getLogger(PreparedByNameStatement.class + ".ERROR");
        this.params = new Hashtable<String, ParamInfo>();
        this.values = new Hashtable<Integer, Object>();
        this.parseParams(this.orisql = sql);
        this.prepstmt = conn.prepareStatement(this.bindsql);
    }
    
    @Override
    public void addBatch() throws SQLException {
        this.prepstmt.addBatch();
    }
    
    @Override
    public void addBatch(final String arg0) throws SQLException {
        this.prepstmt.addBatch(arg0);
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
    
    private void bindValue(final int i, final Object s) {
        this.values.put(new Integer(i), s);
    }
    
    @Override
    public void cancel() throws SQLException {
        this.prepstmt.cancel();
    }
    
    @Override
    public void clearBatch() throws SQLException {
        this.prepstmt.clearBatch();
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.values.clear();
        this.prepstmt.clearParameters();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.prepstmt.clearWarnings();
    }
    
    @Override
    public void close() throws SQLException {
        this.prepstmt.close();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof PreparedByNameStatement && this.getSql().equals(((PreparedByNameStatement)obj).getSql());
    }
    
    @Override
    public boolean execute() throws SQLException {
        return this.prepstmt.execute();
    }
    
    @Override
    public boolean execute(final String arg0) throws SQLException {
        return this.prepstmt.execute(arg0);
    }
    
    @Override
    public boolean execute(final String arg0, final int arg1) throws SQLException {
        return this.prepstmt.execute(arg0, arg1);
    }
    
    @Override
    public boolean execute(final String arg0, final int[] arg1) throws SQLException {
        return this.prepstmt.execute(arg0, arg1);
    }
    
    @Override
    public boolean execute(final String arg0, final String[] arg1) throws SQLException {
        return this.prepstmt.execute(arg0, arg1);
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        return this.prepstmt.executeBatch();
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            final ResultSet rs = this.prepstmt.executeQuery();
            return rs;
        }
        catch (SQLException e) {
            this.errorLog.warn((Object)e);
            this.errorLog.warn((Object)this.toString());
            throw e;
        }
    }
    
    @Override
    public ResultSet executeQuery(final String arg0) throws SQLException {
        return this.prepstmt.executeQuery(arg0);
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        return this.prepstmt.executeUpdate();
    }
    
    @Override
    public int executeUpdate(final String arg0) throws SQLException {
        return this.prepstmt.executeUpdate(arg0);
    }
    
    @Override
    public int executeUpdate(final String arg0, final int arg1) throws SQLException {
        return this.prepstmt.executeUpdate(arg0, arg1);
    }
    
    @Override
    public int executeUpdate(final String arg0, final int[] arg1) throws SQLException {
        return this.prepstmt.executeUpdate(arg0, arg1);
    }
    
    @Override
    public int executeUpdate(final String arg0, final String[] arg1) throws SQLException {
        return this.prepstmt.executeUpdate(arg0, arg1);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.prepstmt.getConnection();
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this.prepstmt.getFetchDirection();
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this.prepstmt.getFetchSize();
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.prepstmt.getGeneratedKeys();
    }
    
    private int[] getIndexByFieldName(final String name) {
        if (this.params.containsKey(name)) {
            return this.params.get(name).bindIndex;
        }
        return null;
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.prepstmt.getMaxFieldSize();
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return this.prepstmt.getMaxRows();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.prepstmt.getMetaData();
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return this.prepstmt.getMoreResults();
    }
    
    @Override
    public boolean getMoreResults(final int arg0) throws SQLException {
        return this.prepstmt.getMoreResults();
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.prepstmt.getParameterMetaData();
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return this.prepstmt.getQueryTimeout();
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.prepstmt.getResultSet();
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.prepstmt.getResultSetConcurrency();
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.prepstmt.getResultSetHoldability();
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return this.prepstmt.getResultSetType();
    }
    
    public String getSql() {
        return this.orisql;
    }
    
    public String[] getStringParams() {
        return this.params.keySet().toArray(new String[0]);
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return this.prepstmt.getUpdateCount();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.prepstmt.getWarnings();
    }
    
    @Override
    public int hashCode() {
        return this.getSql().hashCode();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.prepstmt.isClosed();
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return this.prepstmt.isPoolable();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        return this.prepstmt.isWrapperFor(arg0);
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
        String sql = sqlbuffer.toString();
        if (sql.indexOf("@rowno ?") != -1){
            sql = sql.replaceAll("@rowno [?] ","@rowno:");
        }
        this.bindsql = sql;
    }
    
    @Override
    public void setArray(final int arg0, final Array arg1) throws SQLException {
        this.prepstmt.setArray(arg0, arg1);
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1) throws SQLException {
        this.prepstmt.setAsciiStream(arg0, arg1);
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        this.prepstmt.setAsciiStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        this.prepstmt.setAsciiStream(arg0, arg1, arg2);
    }
    
    public void setAsciiStream(final String fieldName, final InputStream value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setAsciiStream(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    public void setAsciiStream(final String fieldName, final String value2) throws SQLException, NoThisVariableException {
        String value3 = value2;
        if (value3 == null) {
            value3 = "";
        }
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setAsciiStream(i, new ByteArrayInputStream(value3.getBytes()), value3.length());
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setBigDecimal(final int arg0, final BigDecimal arg1) throws SQLException {
        this.prepstmt.setBigDecimal(arg0, arg1);
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1) throws SQLException {
        this.prepstmt.setBinaryStream(arg0, arg1);
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        this.prepstmt.setBinaryStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        this.prepstmt.setBinaryStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setBlob(final int arg0, final Blob arg1) throws SQLException {
        this.prepstmt.setBlob(arg0, arg1);
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1) throws SQLException {
        this.prepstmt.setBlob(arg0, arg1);
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        this.prepstmt.setBlob(arg0, arg1, arg2);
    }
    
    @Override
    public void setBoolean(final int arg0, final boolean arg1) throws SQLException {
        this.prepstmt.setBoolean(arg0, arg1);
    }
    
    @Override
    public void setByte(final int arg0, final byte arg1) throws SQLException {
        this.prepstmt.setByte(arg0, arg1);
    }
    
    @Override
    public void setBytes(final int arg0, final byte[] arg1) throws SQLException {
        this.prepstmt.setBytes(arg0, arg1);
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        this.prepstmt.setCharacterStream(arg0, arg1);
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final int arg2) throws SQLException {
        this.prepstmt.setCharacterStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        this.prepstmt.setCharacterStream(arg0, arg1, arg2);
    }
    
    public void setCharacterStream(final String fieldName, final Reader value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setCharacterStream(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    public void setCharacterStream(final String fieldName, final String value2) throws SQLException, NoThisVariableException {
        String value3 = value2;
        if (value3 == null) {
            value3 = "";
        }
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                if (value3.length() < 4000) {
                    this.setCharacterStream(i, new StringReader(value3));
                }
                else {
                    this.setCharacterStream(i, new StringReader(value3.substring(0, 3999)));
                }
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setClob(final int arg0, final Clob arg1) throws SQLException {
        this.prepstmt.setClob(arg0, arg1);
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1) throws SQLException {
        this.prepstmt.setClob(arg0, arg1);
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        this.prepstmt.setClob(arg0, arg1, arg2);
    }
    
    @Override
    public void setCursorName(final String arg0) throws SQLException {
        this.prepstmt.setCursorName(arg0);
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setDate(arg0, arg1);
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1, final Calendar arg2) throws SQLException {
        this.prepstmt.setDate(arg0, arg1, arg2);
    }
    
    public void setDate(final String fieldName, final Date value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setDate(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    public void setDate(final String fieldName, final java.util.Date value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                final Timestamp sqlDate = new Timestamp(value.getTime());
                this.setTimestamp(i, sqlDate);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setDouble(final int arg0, final double arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setDouble(arg0, arg1);
    }
    
    public void setDouble(final String fieldName, final double value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setDouble(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setEscapeProcessing(final boolean arg0) throws SQLException {
        this.prepstmt.setEscapeProcessing(arg0);
    }
    
    @Override
    public void setFetchDirection(final int arg0) throws SQLException {
        this.prepstmt.setFetchDirection(arg0);
    }
    
    @Override
    public void setFetchSize(final int arg0) throws SQLException {
        this.prepstmt.setFetchSize(arg0);
    }
    
    @Override
    public void setFloat(final int arg0, final float arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setFloat(arg0, arg1);
    }
    
    public void setFloat(final String fieldName, final float value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setFloat(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setInt(final int arg0, final int arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setInt(arg0, arg1);
    }
    
    public void setInt(final String fieldName, final int value) throws NoThisVariableException, SQLException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setInt(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setLong(final int arg0, final long arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setLong(arg0, arg1);
    }
    
    public void setLong(final String fieldName, final long value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                this.setFloat(i, value);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Override
    public void setMaxFieldSize(final int arg0) throws SQLException {
        this.prepstmt.setMaxFieldSize(arg0);
    }
    
    @Override
    public void setMaxRows(final int arg0) throws SQLException {
        this.prepstmt.setMaxRows(arg0);
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        this.prepstmt.setNCharacterStream(arg0, arg1);
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        this.prepstmt.setNCharacterStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setNClob(final int arg0, final NClob arg1) throws SQLException {
        this.prepstmt.setNClob(arg0, arg1);
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1) throws SQLException {
        this.prepstmt.setNClob(arg0, arg1);
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        this.prepstmt.setNClob(arg0, arg1, arg2);
    }
    
    @Override
    public void setNString(final int arg0, final String arg1) throws SQLException {
        this.prepstmt.setNString(arg0, arg1);
    }
    
    @Override
    public void setNull(final int arg0, final int arg1) throws SQLException {
        this.prepstmt.setNull(arg0, arg1);
    }
    
    @Override
    public void setNull(final int arg0, final int arg1, final String arg2) throws SQLException {
        this.prepstmt.setNull(arg0, arg1, arg2);
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1) throws SQLException {
        this.prepstmt.setObject(arg0, arg1);
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2) throws SQLException {
        this.prepstmt.setObject(arg0, arg1, arg2);
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2, final int arg3) throws SQLException {
        this.prepstmt.setObject(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void setPoolable(final boolean arg0) throws SQLException {
        this.prepstmt.setPoolable(arg0);
    }
    
    @Override
    public void setQueryTimeout(final int arg0) throws SQLException {
        this.prepstmt.setQueryTimeout(arg0);
    }
    
    @Override
    public void setRef(final int arg0, final Ref arg1) throws SQLException {
        this.prepstmt.setRef(arg0, arg1);
    }
    
    @Override
    public void setRowId(final int arg0, final RowId arg1) throws SQLException {
        this.prepstmt.setRowId(arg0, arg1);
    }
    
    @Override
    public void setShort(final int arg0, final short arg1) throws SQLException {
        this.prepstmt.setShort(arg0, arg1);
    }
    
    @Override
    public void setSQLXML(final int arg0, final SQLXML arg1) throws SQLException {
        this.prepstmt.setSQLXML(arg0, arg1);
    }
    
    @Override
    public void setString(final int arg0, final String arg1) throws SQLException {
        this.bindValue(arg0, arg1);
        this.prepstmt.setString(arg0, arg1);
    }
    
    public void setString(final String fieldName, final String value2) throws NoThisVariableException, SQLException {
        String value3 = value2;
        if (value3 == null) {
            value3 = "";
        }
        if (value3.length() >= 2000 && value3.length() < 4000) {
            this.setCharacterStream(fieldName, value3);
        }
        else {
            final int[] idx = this.getIndexByFieldName(fieldName);
            if (idx == null) {
                throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
            }
            for (final int i : idx) {
                this.setString(i, value3);
            }
        }
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1) throws SQLException {
        this.prepstmt.setTime(arg0, arg1);
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1, final Calendar arg2) throws SQLException {
        this.prepstmt.setTime(arg0, arg1, arg2);
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1) throws SQLException {
        this.prepstmt.setTimestamp(arg0, arg1);
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1, final Calendar arg2) throws SQLException {
        this.prepstmt.setTimestamp(arg0, arg1, arg2);
    }
    
    public void setTimestamp(final String fieldName, final java.util.Date value) throws SQLException, NoThisVariableException {
        final int[] idx = this.getIndexByFieldName(fieldName);
        if (idx != null) {
            for (final int i : idx) {
                final Timestamp sqlDate = new Timestamp(value.getTime());
                this.setTimestamp(i, sqlDate);
            }
            return;
        }
        throw new NoThisVariableException(this, fieldName + " is not need bind in sql");
    }
    
    @Deprecated
    @Override
    public void setUnicodeStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        this.prepstmt.setUnicodeStream(arg0, arg1, arg2);
    }
    
    @Override
    public void setURL(final int arg0, final URL arg1) throws SQLException {
        this.prepstmt.setURL(arg0, arg1);
    }

    @Override
    public void closeOnCompletion() throws SQLException{

    }
    @Override
    public boolean isCloseOnCompletion() throws SQLException{

        return true;
    }

    public void setValueByMap(final Map<String, String> m) throws SQLException, NoThisVariableException {
        final String[] ss = this.getStringParams();
        if (ss != null && ss.length > 0) {
            for (final String s : ss) {
                if (m.containsKey(s)) {
                    String v = m.get(s);
                    if (v == null) {
                        v = "";
                    }
                    this.setString(s, v);
                }
                else {
                    this.setString(s, "");
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append(" With{\n       SQL    : [");
        sb.append(this.orisql);
        sb.append("]\n");
        sb.append("ChangedSQL    : [");
        sb.append(this.bindsql);
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
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        return this.prepstmt.unwrap(arg0);
    }
    
    class ParamInfo
    {
        String paramName;
        int[] bindIndex;
    }
}
