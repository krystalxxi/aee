// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: Options.java 60270 2013-11-03 14:48:37Z tangxy $")
public class Options
{
    public static final int MUST_WITH_ARG = 0;
    public static final int OPT_WITH_ARG = 1;
    public static final int MUST_NOTWITH_ARG = 2;
    public static final char NO_SHORT_NAME = '\0';
    public static final int NEED_USE = 0;
    public static final int OPT_USE = 1;
    public static final int HIDE_USE = 2;
    private ArrayList<String> nonameArgs;
    private Map<String, Option> namedArgs;
    private boolean isSupportNoNameArgs;
    private boolean isParsered;
    private Exception firstException;
    
    public static void main(final String[] args2) {
        final Options opts = new Options();
        opts.addOption('n', "node", 1, 0, "\u05b8\ufffd\ufffd\ufffd\u06b5\ufffdID\ufffd\ufffd\ufffd\ufffd\u05b8\ufffd\ufffd\u0221\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdAEE_NODE_ID", false, "");
        opts.addOption('a', "app", 0, 0, "\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u01a3\ufffd\ufffd\ufffd\u04e6\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffdapps\ufffd\ufffd\ufffd\ufffd\u013d\u06b5\ufffd", false, "");
        opts.addOption('c', "channel", 1, 1, "\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\u0368\ufffd\ufffd\ufffd\u0163\ufffd\u012c\ufffd\ufffd\u03aamain (\ufffd\ufffd\ufffd\ufffd\u0368\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd)", false, "");
        opts.addOption('s', "sault", 0, 0, "\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ub8ec\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0339\ufffd\ufffd\ude3a\ufffd\ufffd\ufffd\ufffd", false, "");
        opts.addOption('d', "debugmode", 2, 2, "\u05b8\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ub8ec\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0339\ufffd\ufffd\ude3a\ufffd\ufffd\ufffd\ufffd", false, "");
        final String[] args3 = { "--node==n1", "--debugmode", "-c100", "-saaa", "--app=hello", "m2" };
        opts.parser(args3);
    }
    
    public Options() {
        this.nonameArgs = new ArrayList<String>();
        this.namedArgs = new HashMap<String, Option>();
        this.isSupportNoNameArgs = true;
        this.isParsered = false;
        this.firstException = null;
    }
    
    public void addOption(final char shortName, final String longName, final int isNeedUse, final int argType, final String desc, final boolean isSupportMutil, final String defValue) {
        final Option o = new Option(shortName, longName, isNeedUse, argType, desc, isSupportMutil, defValue);
        this.namedArgs.put(longName, o);
    }
    
    public String dumpResult() {
        if (this.isParsered) {
            final StringBuffer sb = new StringBuffer();
            int v0 = 0;
            int v2 = 0;
            for (final Option o : this.namedArgs.values()) {
                if (o.longName.length() > v0) {
                    v0 = o.longName.length();
                }
                if (o.value.length() > v2) {
                    v2 = o.value.length();
                }
            }
            if (v2 > 40) {
                v2 = 40;
            }
            for (final Option o : this.namedArgs.values()) {
                sb.append("    --" + StringUtils.rightPad(o.longName, v0, ' '));
                if (o.shortName != '\0') {
                    sb.append("[" + o.shortName + "] ");
                }
                else {
                    sb.append("    ");
                }
                sb.append("=" + StringUtils.rightPad(o.value, v2, ' '));
                sb.append("    ");
                sb.append(o.desc);
                sb.append("\n");
            }
            return sb.toString();
        }
        return this.dumpUsage();
    }
    
    public String dumpUsage() {
        final StringBuffer sb = new StringBuffer();
        if (this.isSupportNoNameArgs) {
            sb.append("usage: app [args] ...\n");
        }
        else {
            sb.append("usage: app [args] \n");
        }
        int v0 = 0;
        for (final Option o : this.namedArgs.values()) {
            if (o.longName.length() > v0) {
                v0 = o.longName.length();
            }
        }
        for (final Option o : this.namedArgs.values()) {
            if (o.isNeedUse != 2) {
                sb.append("    --" + StringUtils.rightPad(o.longName, v0, ' '));
                if (o.shortName != '\0') {
                    sb.append("[" + o.shortName + "] ");
                }
                else {
                    sb.append("    ");
                }
                if (o.argType == 0) {
                    sb.append("= arg ");
                }
                else if (o.argType == 1) {
                    sb.append("=[arg]");
                }
                else {
                    sb.append("     ");
                }
                if (o.isNeedUse == 0) {
                    sb.append("      ");
                }
                else {
                    sb.append(" <opt>");
                }
                sb.append(o.desc);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    private Option findByLongName(final String longName) {
        for (final Option o : this.namedArgs.values()) {
            if (o.longName.equals(longName)) {
                return o;
            }
        }
        return null;
    }
    
    private Option findByShortName(final char c) {
        for (final Option o : this.namedArgs.values()) {
            if (o.shortName == c) {
                return o;
            }
        }
        return null;
    }
    
    public Exception getFirstException() {
        return this.firstException;
    }
    
    public String[] getNoNameArgs() {
        if (this.isParsered) {
            return this.nonameArgs.toArray(new String[0]);
        }
        return new String[0];
    }
    
    public String getOptionValue(final char shortName) {
        return this.getOptionValue(this.shortNameToLongName(shortName));
    }
    
    public String getOptionValue(final String longName) {
        if (this.namedArgs.containsKey(longName)) {
            return this.namedArgs.get(longName).value;
        }
        return "";
    }
    
    private void innerParser(final String[] as2) throws OptionParserException {
        final String[] as3 = new String[as2.length + 1];
        for (int i = 0; i < as2.length; ++i) {
            as3[i] = as2[i];
        }
        as3[as2.length] = "";
        final ArrayList<String> al = new ArrayList<String>();
        for (int j = 0; j < as2.length; ++j) {
            String s = as3[j].trim();
            boolean cycleCheck = true;
            while (cycleCheck) {
                cycleCheck = false;
                if (s.startsWith("--")) {
                    if (s.indexOf("=") > 0) {
                        al.add(s);
                    }
                    else {
                        al.add(s + "=");
                    }
                }
                else if (s.startsWith("-")) {
                    if (s.length() == 2) {
                        final char c = s.charAt(1);
                        final Option o = this.findByShortName(c);
                        if (o == null) {
                            throw new OptionParserException("Option [" + c + "] is not defined ");
                        }
                        final String ns = as3[j + 1];
                        if (o.argType == 0) {
                            if (ns.startsWith("-")) {
                                throw new OptionParserException("Option " + o.longName + "[" + o.shortName + "] is defined must with argument,but not found");
                            }
                            al.add("--" + o.longName + "=" + ns);
                            ++j;
                        }
                        else if (o.argType == 1) {
                            if (ns.startsWith("-")) {
                                continue;
                            }
                            al.add("--" + o.longName + "=" + ns);
                            ++j;
                        }
                        else {
                            if (o.argType != 2) {
                                continue;
                            }
                            al.add("--" + o.longName + "=");
                        }
                    }
                    else {
                        final char c = s.charAt(1);
                        final Option o = this.findByShortName(c);
                        if (o == null) {
                            throw new OptionParserException("Option [" + c + "] is not defined ");
                        }
                        if (o.argType == 0 || o.argType == 1) {
                            al.add("--" + o.longName + "=" + s.substring(2));
                        }
                        else {
                            if (o.argType != 2) {
                                continue;
                            }
                            al.add("--" + o.longName + "=");
                            s = "-" + s.substring(2);
                            cycleCheck = true;
                        }
                    }
                }
                else {
                    al.add(s);
                }
            }
        }
        for (final String arg : al) {
            if (arg.startsWith("--")) {
                final String k = StringUtils.substringBetween(arg, "--", "=").trim();
                String v = StringUtils.substringAfter(arg, "=").trim();
                if (v == null) {
                    v = "";
                }
                if (k == null) {
                    System.out.println(arg);
                }
                final Option o = this.findByLongName(k);
                if (o.argType == 0 && v.length() == 0) {
                    throw new OptionParserException("Option " + o.longName + "[" + o.shortName + "] is defined must with argument,but not found");
                }
                o.isDefined = true;
                if (o.argType == 1 && v.length() == 0 && o.defValue.length() > 0) {
                    o.value = o.defValue;
                }
                if (o.isSupportMutil) {
                    if (o.value.length() > 0) {
                        o.value = o.value + ";" + v;
                    }
                    else {
                        o.value = v;
                    }
                }
                else {
                    o.value = v;
                }
            }
            else {
                if (!this.isSupportNoNameArgs) {
                    throw new OptionParserException("not support no name options");
                }
                this.nonameArgs.add(arg);
            }
        }
        for (final Option o2 : this.namedArgs.values()) {
            if (o2.isNeedUse == 0 && !o2.isDefined) {
                throw new OptionParserException("Option " + o2.longName + "[" + o2.shortName + "] must use,but not found");
            }
        }
    }
    
    public boolean isDefined(final char shortName) {
        return this.isDefined(this.shortNameToLongName(shortName));
    }
    
    public boolean isDefined(final String longName) {
        return this.namedArgs.containsKey(longName) && this.namedArgs.get(longName).isDefined;
    }
    
    private char longNameToShortName(final String longName) {
        for (final Option o : this.namedArgs.values()) {
            if (o.longName.equals(longName)) {
                return o.shortName;
            }
        }
        return ' ';
    }
    
    public boolean parser(final String[] as) {
        try {
            this.innerParser(as);
            return this.isParsered = true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            this.firstException = e;
            return false;
        }
    }
    
    private String shortNameToLongName(final char c) {
        for (final Option o : this.namedArgs.values()) {
            if (o.shortName == c) {
                return o.longName;
            }
        }
        return "";
    }
    
    class Option
    {
        public char shortName;
        public String longName;
        public int isNeedUse;
        public int argType;
        public String desc;
        public boolean isDefined;
        public String value;
        public String defValue;
        public boolean isSupportMutil;
        
        public Option(final char shortName, final String longName, final int isNeedUse, final int argType, final String desc) {
            this.isDefined = false;
            this.value = "";
            this.defValue = "";
            this.isSupportMutil = false;
            this.shortName = shortName;
            this.longName = longName;
            this.isNeedUse = isNeedUse;
            this.argType = argType;
            this.desc = desc;
            this.isSupportMutil = true;
        }
        
        public Option(final char shortName, final String longName, final int isNeedUse, final int argType, final String desc, final boolean isSupportMutil) {
            this.isDefined = false;
            this.value = "";
            this.defValue = "";
            this.isSupportMutil = false;
            this.shortName = shortName;
            this.longName = longName;
            this.isNeedUse = isNeedUse;
            this.argType = argType;
            this.desc = desc;
            this.isSupportMutil = isSupportMutil;
        }
        
        public Option(final char shortName, final String longName, final int isNeedUse, final int argType, final String desc, final boolean isSupportMutil, final String defValue) {
            this.isDefined = false;
            this.value = "";
            this.defValue = "";
            this.isSupportMutil = false;
            this.shortName = shortName;
            this.longName = longName;
            this.isNeedUse = isNeedUse;
            this.argType = argType;
            this.desc = desc;
            this.isSupportMutil = isSupportMutil;
            this.defValue = defValue;
        }
    }
    
    class OptionParserException extends Exception
    {
        private static final long serialVersionUID = 1L;
        
        public OptionParserException(final String s) {
            super(s);
        }
    }
}
