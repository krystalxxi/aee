// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.database.sp;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.aee.common.sp.AbstractServiceProvider;

public class WADE4DBConnectionServiceProvider extends AbstractServiceProvider
{
    public Object getService(final String dn) {
        try {
            final Connection conn = ConnectionManagerFactory.getConnectionManager().getConnection(dn);
            if (conn != null) {
                conn.setAutoCommit(false);
            }
            return conn;
        }
//        catch (NamingException e) {
//            e.printStackTrace();
//            return null;
//        }
        catch (SQLException e2) {
            e2.printStackTrace();
            return null;
        }
//        catch (ClassNotFoundException e3) {
//            e3.printStackTrace();
//            return null;
//        }
    }
}
