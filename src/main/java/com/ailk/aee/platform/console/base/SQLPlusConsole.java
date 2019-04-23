// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console.base;

import java.sql.ResultSet;
import java.sql.Statement;
import com.ailk.aee.common.util.StringUtils;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SQLPlusConsole.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SQLPlusConsole implements IConsole
{
    public void commitTrans(final CmdEnv env, final String[] args) {
        final Object o = env.getObject("Connection");
        if (o != null) {
            final Connection conn = (Connection)o;
            try {
                conn.commit();
            }
            catch (SQLException e) {
                env.doException(e);
            }
        }
        else {
            env.outline("not login on!");
        }
    }
    
    @Override
    public List<Command> getCommand() {
        final ArrayList<Command> al = new ArrayList<Command>();
        al.add(new Command("select", "", "select data", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.selectData(env, env.packageArgs(args, 0));
            }
        }));
        al.add(new Command("update", "", "update data", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.updateData(env, env.packageArgs(args, 0));
            }
        }));
        al.add(new Command("delete", "", "delete data", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.updateData(env, env.packageArgs(args, 0));
            }
        }));
        al.add(new Command("insert", "", "insert data", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.updateData(env, env.packageArgs(args, 0));
            }
        }));
        al.add(new Command("commit", "", "commit transaction", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.commitTrans(env, args);
            }
        }));
        al.add(new Command("rollback", "", "commit transaction", null, new ICommandAdapter() {
            @Override
            public void doCommand(final CmdEnv env, final String[] args) {
                SQLPlusConsole.this.rollbackTrans(env, args);
            }
        }));
        return al;
    }
    
    @Override
    public CmdConfig getConfig() {
        final CmdConfig cc = new CmdConfig();
        cc.setTitle("java sqlplus programe");
        cc.setVersion("1.0.0.0");
        cc.setCopyRight("@2011 Asiainfo-Linkage");
        cc.setPrompt2Type(1);
        cc.setEndChar(';');
        cc.setPrompt("SQL> ");
        cc.setPrompt2("  > ");
        return cc;
    }
    
    @Override
    public boolean isNeedLogin() {
        return true;
    }
    
    @Override
    public void login(final CmdEnv env) {
        final String username = env.readLine("UserName: ");
        final String password = env.readPassword("Password: ");
        final String addr = env.readLine("url: ");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (ClassNotFoundException e) {
            env.doException(e);
        }
        try {
            final Connection con = DriverManager.getConnection(addr, username, password);
            env.addObject("Connection", con);
            env.outline("Login Success!");
            con.setAutoCommit(false);
        }
        catch (SQLException e2) {
            env.doException(e2);
        }
    }
    
    @Override
    public void onQuit(final CmdEnv env) {
        final Object o = env.getObject("Connection");
        if (o != null) {
            final Connection conn = (Connection)o;
            try {
                conn.rollback();
                conn.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    @Override
    public void onStart(final CmdEnv env, final String[] args) {
    }
    
    public void rollbackTrans(final CmdEnv env, final String[] args) {
        final Object o = env.getObject("Connection");
        if (o != null) {
            final Connection conn = (Connection)o;
            try {
                conn.rollback();
            }
            catch (SQLException e) {
                env.doException(e);
            }
        }
    }
    
    public void selectData(final CmdEnv env, final String sql) {
        final Object o = env.getObject("Connection");
        if (o != null) {
            final Connection conn = (Connection)o;
            try {
                final Statement stmt = conn.createStatement();
                final ResultSet rs = stmt.executeQuery(sql);
                try {
                    final int colCount = rs.getMetaData().getColumnCount();
                    for (int i = 0; i < colCount; ++i) {
                        final String title = rs.getMetaData().getColumnLabel(i + 1);
                        final int size2 = rs.getMetaData().getColumnDisplaySize(i + 1);
                        final int size3 = (size2 > title.length()) ? size2 : title.length();
                        env.out(StringUtils.rightPad(title, size3 + 3, " "));
                    }
                    env.outline("");
                    for (int i = 0; i < colCount; ++i) {
                        final String title = rs.getMetaData().getColumnLabel(i + 1);
                        final int size2 = rs.getMetaData().getColumnDisplaySize(i + 1);
                        final int size3 = (size2 > title.length()) ? size2 : title.length();
                        env.out(StringUtils.rightPad("-", size3, "-"));
                        env.out("   ");
                    }
                    env.outline("");
                    while (rs.next()) {
                        for (int i = 0; i < colCount; ++i) {
                            final String value = rs.getString(i + 1);
                            final String title2 = rs.getMetaData().getColumnLabel(i + 1);
                            final int size4 = rs.getMetaData().getColumnDisplaySize(i + 1);
                            final int size5 = (size4 > title2.length()) ? size4 : title2.length();
                            env.out(StringUtils.rightPad(value, size5, " "));
                            env.out("   ");
                        }
                        env.outline("");
                    }
                }
                catch (SQLException e) {
                    env.doException(e);
                }
            }
            catch (SQLException e2) {
                env.doException(e2);
            }
        }
        else {
            env.outline("not login on!");
        }
    }
    
    public void updateData(final CmdEnv env, final String sql) {
        final Object o = env.getObject("Connection");
        if (o != null) {
            final Connection conn = (Connection)o;
            try {
                final Statement stmt = conn.createStatement();
                final int ret = stmt.executeUpdate(sql);
                env.outline(Integer.toString(ret) + " rows update!");
            }
            catch (SQLException e) {
                env.doException(e);
            }
        }
        else {
            env.outline("not login on!");
        }
    }
}
