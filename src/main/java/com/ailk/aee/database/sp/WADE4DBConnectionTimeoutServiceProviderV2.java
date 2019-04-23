// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.database.sp;

import java.sql.SQLException;
import javax.naming.NamingException;
import com.ailk.database.dbconn.DBConnection;
import java.util.concurrent.Executors;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import com.ailk.aee.platform.AEEPlatform;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.FutureTask;
import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import com.ailk.aee.common.sp.AbstractServiceProvider;

public class WADE4DBConnectionTimeoutServiceProviderV2 extends AbstractServiceProvider
{
    private static Executor executor;
    
    public Object getService(final String dn) {
        final FutureTask<Connection> task = new FutureTask<Connection>(new Callable<Connection>() {
            @Override
            public Connection call() throws Exception {
                return new DBConnectionFetcher(dn).fetchDBConnection();
            }
        });
        WADE4DBConnectionTimeoutServiceProviderV2.executor.execute(task);
        try {
            final Connection conn = task.get(1L, TimeUnit.MINUTES);
            return conn;
        }
        catch (InterruptedException e) {
            AEEPlatform.getInstance().getLogger().info((Object)("getService InterruptedException :" + e.getMessage()));
            task.cancel(true);
        }
        catch (ExecutionException e2) {
            AEEPlatform.getInstance().getLogger().info((Object)("getService ExecutionException " + e2.getMessage()));
            task.cancel(true);
        }
        catch (TimeoutException e3) {
            AEEPlatform.getInstance().getLogger().info((Object)("getService TimeoutException " + e3.getMessage()));
            return null;
        }
        return null;
    }
    
    static {
        ConnectionManagerFactory.getConnectionManager();
        WADE4DBConnectionTimeoutServiceProviderV2.executor = Executors.newSingleThreadExecutor();
    }
    
    class DBConnectionFetcher
    {
        private String dn;
        
        public DBConnectionFetcher(final String s) {
            this.dn = "";
            this.dn = s;
        }
        
        public Connection fetchDBConnection() {
            try {
                AEEPlatform.getInstance().getLogger().info((Object)(" provider fetchDBConnection " + this.dn));
                final DBConnection c = new DBConnection(this.dn, true, false);
                return c.getConnection();
            }

            catch (SQLException e2) {
                AEEPlatform.getInstance().getLogger().info((Object)("provider SQLException : " + e2.getMessage()));
                return null;
            }
        }
    }
}
