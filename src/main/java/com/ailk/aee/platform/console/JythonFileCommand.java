// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console;

import java.util.Hashtable;
import java.io.InputStream;
import com.ailk.aee.common.util.ExceptionUtils;
import java.io.FileInputStream;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.python.core.PyList;
import java.util.Map;
import org.python.core.PySystemState;
import java.util.Properties;
import com.ailk.aee.platform.console.base.CmdEnv;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.ailk.aee.common.util.StringUtils;
import java.io.File;
import com.ailk.aee.platform.console.base.Command;
import com.ailk.aee.common.annotation.cvsid.CVSID;
import com.ailk.aee.platform.console.base.ICommandAdapter;

@CVSID("$Id: JythonFileCommand.java 60270 2013-11-03 14:48:37Z tangxy $")
public class JythonFileCommand implements ICommandAdapter
{
    private String file;
    
    public static Command getCommand(final String file) {
        final File f = new File(file);
        if (f.exists()) {
            final String cmdName = StringUtils.substringBefore(f.getName(), ".py");
            String desc = "";
            String ddesc = "";
            try {
                final BufferedReader r = new BufferedReader(new FileReader(f));
                for (String s = r.readLine(); s != null; s = r.readLine()) {
                    if (s.startsWith("#@desc=")) {
                        desc = StringUtils.substringAfter(s, "#@desc=");
                    }
                    if (s.startsWith("#@ddesc=")) {
                        ddesc = ddesc + StringUtils.substringAfter(s, "#@ddesc=") + "\n";
                    }
                }
                r.close();
            }
            catch (FileNotFoundException e) {}
            catch (IOException ex) {}
            ddesc = ddesc + "using [" + f.getAbsolutePath() + "] script file to execute";
            return new Command(cmdName, "", desc, ddesc, new JythonFileCommand(file));
        }
        return null;
    }
    
    public JythonFileCommand(final String file) {
        this.file = "";
        this.file = file;
    }
    
    @Override
    public void doCommand(final CmdEnv env, final String[] args) {
        InputStream is = null;
        try {
            final Properties pro = new Properties();
            pro.putAll(PySystemState.getBaseProperties());
            pro.put("python.console.encoding", "utf-8");
            PySystemState.initialize(pro, (Properties)null, (String[])null);
            final PyList pl = new PyList();
            if (args != null && args.length > 0) {
                for (final String s : args) {
                    pl.add((Object)s);
                }
            }
            final PythonInterpreter pin = new PythonInterpreter();
            pin.set("aee_argv", (PyObject)pl);
            is = new FileInputStream(this.file);
            pin.execfile(is);
            pin.cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
            env.outline("execute error,e=" + ExceptionUtils.getExceptionStack(e));
        }
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
