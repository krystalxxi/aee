// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.ArrayList;
import java.util.List;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StringListConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StringListConverter implements IStringObjectConverter
{
    private final String startBrackets = "[";
    private final String endBrackets = "]";
    private final String split = ",";
    
    private static void log(final String msg) {
    }
    
    public static void main(final String[] strs) {
        final StringListConverter obj = new StringListConverter();
        final String str = "[\",\\\",,,\"]";
        System.out.println(obj.canWrapFromString(str));
        System.out.println(obj.wrapFromString(str));
    }
    
    private boolean can(final String str) {
        log(str);
        final StringBuffer key = new StringBuffer("");
        String tail = null;
        for (int i = 0, len = str.length(); i < len; ++i) {
            char c = str.charAt(i);
            if (key.length() > 0) {
                if (tail == null) {
                    if (",".equals(String.valueOf(c))) {
                        log(key.toString().trim());
                        tail = null;
                        key.delete(0, key.length());
                    }
                    else {
                        key.append(c);
                    }
                }
                else if ("\\".equals(String.valueOf(c))) {
                    if (i < len - 1) {
                        ++i;
                        c = str.charAt(i);
                        if (tail.equals(String.valueOf(c))) {
                            key.append(c);
                        }
                        else {
                            key.append("\\");
                            key.append(c);
                        }
                    }
                }
                else if (!tail.equals(String.valueOf(c))) {
                    key.append(c);
                }
                else {
                    log(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
            }
            else if (" ".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if (",".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("\n".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("\t".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("'".equals(String.valueOf(c))) {
                if (tail == null) {
                    tail = "'";
                }
                else if (tail.equals(String.valueOf(c))) {
                    log(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
                else {
                    key.append(c);
                }
            }
            else if ("\"".equals(String.valueOf(c))) {
                if (tail == null) {
                    tail = "\"";
                }
                else if (tail.equals(String.valueOf(c))) {
                    log(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
                else {
                    key.append(c);
                }
            }
            else {
                key.append(c);
            }
        }
        if (key.length() > 0 && tail == null) {
            log(key.toString().trim());
            tail = null;
            key.delete(0, key.length());
        }
        return key.length() <= 0;
    }
    
    @Override
    public boolean canWrapFromString(final String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        String targetStr = str.trim();
        if (!targetStr.startsWith("[") || !targetStr.endsWith("]")) {
            return false;
        }
        targetStr = targetStr.substring(1, targetStr.length() - 1);
        return this.can(targetStr);
    }
    
    private List<String> getValue(final String str) {
        log(str);
        final ArrayList<String> list = new ArrayList<String>();
        final StringBuffer key = new StringBuffer("");
        String tail = null;
        for (int i = 0, len = str.length(); i < len; ++i) {
            char c = str.charAt(i);
            if (key.length() > 0) {
                if (tail == null) {
                    if (",".equals(String.valueOf(c))) {
                        log(key.toString().trim());
                        list.add(key.toString().trim());
                        tail = null;
                        key.delete(0, key.length());
                    }
                    else {
                        key.append(c);
                    }
                }
                else if ("\\".equals(String.valueOf(c))) {
                    if (i < len - 1) {
                        ++i;
                        c = str.charAt(i);
                        if (tail.equals(String.valueOf(c))) {
                            key.append(c);
                        }
                        else {
                            key.append("\\");
                            key.append(c);
                        }
                    }
                }
                else if (!tail.equals(String.valueOf(c))) {
                    key.append(c);
                }
                else {
                    log(key.toString());
                    list.add(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
            }
            else if (" ".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if (",".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("\n".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("\t".equals(String.valueOf(c))) {
                if (tail != null) {
                    key.append(c);
                }
            }
            else if ("'".equals(String.valueOf(c))) {
                if (tail == null) {
                    tail = "'";
                }
                else if (tail.equals(String.valueOf(c))) {
                    log(key.toString());
                    list.add(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
                else {
                    key.append(c);
                }
            }
            else if ("\"".equals(String.valueOf(c))) {
                if (tail == null) {
                    tail = "\"";
                }
                else if (tail.equals(String.valueOf(c))) {
                    log(key.toString());
                    list.add(key.toString());
                    tail = null;
                    key.delete(0, key.length());
                }
                else {
                    key.append(c);
                }
            }
            else {
                key.append(c);
            }
        }
        if (key.length() > 0 && tail == null) {
            log(key.toString().trim());
            list.add(key.toString().trim());
            tail = null;
            key.delete(0, key.length());
        }
        return list;
    }
    
    @Override
    public List<String> wrapFromString(final String str) {
        String targetStr = str.trim();
        targetStr = targetStr.substring(1, targetStr.length() - 1);
        return this.getValue(targetStr);
    }
}
