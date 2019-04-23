// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: HttpInputStreamConfigurationFactory.java 60270 2013-11-03 14:48:37Z tangxy $")
public class HttpInputStreamConfigurationFactory extends InputStreamConfigurationFactory
{
    private String url;
    private String param;
    private static int BUFFER_SIZE;
    
    public HttpInputStreamConfigurationFactory(final String url, final String param, final String parseType) {
        this.url = null;
        this.param = null;
        this.url = url;
        this.param = param;
        this.setParseType(parseType);
        this.httpCall(url, param);
    }
    
    public InputStream byteTOInputStream(final byte[] in) throws IOException {
        final ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }
    
    public String getParam() {
        return this.param;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void httpCall(final String url, final String param) {
        URLConnection connection = null;
        HttpURLConnection httpconn = null;
        OutputStreamWriter reqOut = null;
        try {
            final URL reqUrl = new URL(url);
            connection = reqUrl.openConnection();
            connection.setDoInput(true);
            if (param != null && param.length() > 0) {
                connection.setDoOutput(true);
                reqOut = new OutputStreamWriter(connection.getOutputStream());
                reqOut.write(param);
                reqOut.flush();
            }
            else {
                connection.setDoOutput(false);
            }
            httpconn = (HttpURLConnection)connection;
            final int HttpResult = httpconn.getResponseCode();
            if (HttpResult != 200) {
                return;
            }
            final byte[] bytes = this.InputStreamTOByte(connection.getInputStream());
            if (bytes != null && bytes.length > 0) {
                this.setInputStream(this.byteTOInputStream(bytes));
            }
        }
        catch (Exception ex) {}
        finally {
            try {
                if (reqOut != null) {
                    reqOut.close();
                }
                if (httpconn != null) {
                    httpconn.disconnect();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    public byte[] InputStreamTOByte(final InputStream in) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[HttpInputStreamConfigurationFactory.BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, HttpInputStreamConfigurationFactory.BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }
        data = null;
        return outStream.toByteArray();
    }
    
    public void setParam(final String param) {
        this.param = param;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    static {
        HttpInputStreamConfigurationFactory.BUFFER_SIZE = 4096;
    }
}
