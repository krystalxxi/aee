// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.strategy;

import java.sql.ResultSet;
import com.ailk.aee.common.sp.IServiceProvider;
import com.ailk.aee.common.sql.PreparedByNameStatement;
import java.sql.Connection;
import com.ailk.aee.common.sp.ServiceProviderManager;
import com.ailk.aee.core.Worker;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: DBConfigedCounterStrategy.java 60270 2013-11-03 14:48:37Z tangxy $")
public class DBConfigedCounterStrategy implements ICounterStrategy
{
    private String sql;
    private String database;
    private float v;
    private boolean isCalced;
    
    public DBConfigedCounterStrategy() {
        this.sql = "";
        this.database = "";
        this.v = 0.0f;
        this.isCalced = false;
    }
    
    @Override
    public float calc(final Worker jw) {
        if (!this.isCalced) {
            this.isCalced = true;
            try {
                final IServiceProvider isp = ServiceProviderManager.getInstance().getServiceProvider("DataBaseConnection");
                final Connection conn = (Connection)isp.getService(this.database);
                final PreparedByNameStatement stmt = new PreparedByNameStatement(conn, this.sql);
                try {
                    final ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        final float vx = rs.getFloat("CNT");
                        return this.v = vx;
                    }
                }
                catch (Exception e) {
                    stmt.close();
                    e.printStackTrace();
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return this.v;
    }
}
