// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.stringobject;

import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StringMapConverter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StringMapConverter implements IStringObjectConverter
{
    private final String startBrackets = "{";
    private final String endBrackets = "}";
    private final String split = ",";
    private final String keyValueSplit = "=";
    
    private static void log(final String msg) {
    }
    
    public static void main(final String[] agrs) {
        final String s1 = "{key1  = 'any char here', key2=\" any char here \"}";
        final String s2 = "{}";
        final String s3 = "{key1 and key1 last=any char here}";
        final String s4 = "{key1  = \"a =b\"}";
        final String s5 = "{key1  = ' a,b'}";
        final String s6 = "{key1  = 'a\"\\'\\'\\'b'}";
        final String s7 = "{key1  = \"a'b\"}";
        final String s8 = "{key1 =  'select *,@rowno:=@rowno+1 AS rownum from dual,(SELECT @rowno:=0) \n where @rowno <1'}";
        final String s9 = "{key1   = 'a\\'b'}";
        final String s10 = "{key1 = a\\\"b\"}";
        final String s11 = "{key1 = a\\b\"}";
        final String s12 = "{key1 = a\\\\b}";
        final String s13 = "{\"abcd\"}";
        final String s14 = "{a=,b=1}";
        test("s1", s1);
        test("s2", s2);
        test("s3", s3);
        test("s4", s4);
        test("s5", s5);
        test("s6", s6);
        test("s7", s7);
        test("s8", s8);
        test("s9", s9);
        test("s10", s10);
        test("s11", s11);
        test("s12", s12);
        test("s13", s13);
        test("s14", s14);
    }
    
    public static void test(final String s1, final String s) {
        System.out.println("==============test  " + s1 + "-================");
        final StringMapConverter obj = new StringMapConverter();
        System.out.println("ori: [" + s + "]");
        System.out.println(obj.canWrapFromString(s));
        System.out.println(obj.wrapFromString(s));
        System.out.println("==============test  " + s1 + " finish================\n\n");
    }
    
    private boolean can(final String str) {
        log(str);
        final StringBuffer key = new StringBuffer("");
        Boolean existKey = false;
        Boolean existValue = false;
        String tail = null;
        final StringBuffer value = new StringBuffer("");
        for (int i = 0, len = str.length(); i < len; ++i) {
            char c = str.charAt(i);
            if (!existKey) {
                if (key.length() > 0) {
                    if ("=".equals(String.valueOf(c))) {
                        existKey = true;
                        log(key.toString().trim());
                    }
                    else {
                        key.append(c);
                    }
                }
                else if (!" ".equals(String.valueOf(c))) {
                    if (!",".equals(String.valueOf(c))) {
                        if (!"\n".equals(String.valueOf(c))) {
                            if (!"\t".equals(String.valueOf(c))) {
                                if ("=".equals(String.valueOf(c))) {
                                    return false;
                                }
                                key.append(c);
                            }
                        }
                    }
                }
            }
            else if (existKey && !existValue) {
                if (value.length() > 0) {
                    if (tail == null) {
                        if (!",".equals(String.valueOf(c))) {
                            value.append(c);
                        }
                        else {
                            existValue = true;
                            log(value.toString().trim());
                            tail = null;
                            existKey = false;
                            existValue = false;
                            key.delete(0, key.length());
                            value.delete(0, value.length());
                        }
                    }
                    else if ("\\".equals(String.valueOf(c))) {
                        if (i >= len - 1) {
                            return false;
                        }
                        ++i;
                        c = str.charAt(i);
                        if (tail.equals(String.valueOf(c))) {
                            value.append(c);
                        }
                        else {
                            value.append("\\");
                            value.append(c);
                        }
                    }
                    else if (!tail.equals(String.valueOf(c))) {
                        value.append(c);
                    }
                    else {
                        existValue = true;
                        log(value.toString());
                        tail = null;
                        existKey = false;
                        existValue = false;
                        key.delete(0, key.length());
                        value.delete(0, value.length());
                    }
                }
                else if (tail != null || !" ".equals(String.valueOf(c))) {
                    if (tail != null || !"\n".equals(String.valueOf(c))) {
                        if (tail != null || !"\t".equals(String.valueOf(c))) {
                            if (tail == null && ",".equals(String.valueOf(c))) {
                                existValue = true;
                                log(value.toString());
                                tail = null;
                                existKey = false;
                                existValue = false;
                                key.delete(0, key.length());
                                value.delete(0, value.length());
                            }
                            else if ("'".equals(String.valueOf(c))) {
                                if (tail == null) {
                                    tail = String.valueOf(c);
                                }
                                else if (tail.equals(String.valueOf(c))) {
                                    existValue = true;
                                    log(value.toString());
                                    tail = null;
                                    existKey = false;
                                    existValue = false;
                                    key.delete(0, key.length());
                                    value.delete(0, value.length());
                                }
                                else {
                                    value.append(c);
                                }
                            }
                            else if ("\"".equals(String.valueOf(c))) {
                                if (tail == null) {
                                    tail = String.valueOf(c);
                                }
                                else if (tail.equals(String.valueOf(c))) {
                                    existValue = true;
                                    log(value.toString());
                                    tail = null;
                                    existKey = false;
                                    existValue = false;
                                    key.delete(0, key.length());
                                    value.delete(0, value.length());
                                }
                                else {
                                    value.append(c);
                                }
                            }
                            else {
                                value.append(c);
                            }
                        }
                    }
                }
            }
            else if (existKey && existValue) {
                if (!" ".equals(String.valueOf(c))) {
                    if (!",".equals(String.valueOf(c))) {
                        return false;
                    }
                    tail = null;
                    existKey = false;
                    existValue = false;
                    key.delete(0, key.length());
                    value.delete(0, value.length());
                }
            }
        }
        if (existKey && !existValue && tail == null) {
            existValue = true;
            log(value.toString().trim());
            tail = null;
            existKey = false;
            existValue = false;
            key.delete(0, key.length());
            value.delete(0, value.length());
        }
        return key.length() <= 0 && value.length() <= 0;
    }
    
    @Override
    public boolean canWrapFromString(final String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        String targetStr = str.trim();
        if (!targetStr.startsWith("{") || !targetStr.endsWith("}")) {
            return false;
        }
        targetStr = targetStr.substring(1, targetStr.length() - 1);
        return this.can(targetStr);
    }
    
    private Map<String, String> getKV(final String str) {
        log(str);
        final HashMap<String, String> map = new HashMap<String, String>();
        final StringBuffer key = new StringBuffer("");
        Boolean existKey = false;
        Boolean existValue = false;
        String tail = null;
        final StringBuffer value = new StringBuffer("");
        for (int i = 0, len = str.length(); i < len; ++i) {
            char c = str.charAt(i);
            if (!existKey) {
                if (key.length() > 0) {
                    if ("=".equals(String.valueOf(c))) {
                        existKey = true;
                        log(key.toString().trim());
                    }
                    else {
                        key.append(c);
                    }
                }
                else if (!" ".equals(String.valueOf(c))) {
                    if (!",".equals(String.valueOf(c))) {
                        if (!"\n".equals(String.valueOf(c))) {
                            if (!"\t".equals(String.valueOf(c))) {
                                key.append(c);
                            }
                        }
                    }
                }
            }
            else if (existKey && !existValue) {
                if (value.length() > 0) {
                    if (tail == null) {
                        if (!",".equals(String.valueOf(c))) {
                            value.append(c);
                        }
                        else {
                            existValue = true;
                            log(value.toString().trim());
                            map.put(key.toString().trim(), value.toString().trim());
                            tail = null;
                            existKey = false;
                            existValue = false;
                            key.delete(0, key.length());
                            value.delete(0, value.length());
                        }
                    }
                    else if ("\\".equals(String.valueOf(c))) {
                        if (i < len - 1) {
                            ++i;
                            c = str.charAt(i);
                            if (tail.equals(String.valueOf(c))) {
                                value.append(c);
                            }
                            else {
                                value.append("\\");
                                value.append(c);
                            }
                        }
                    }
                    else if (!tail.equals(String.valueOf(c))) {
                        value.append(c);
                    }
                    else {
                        existValue = true;
                        log(value.toString());
                        map.put(key.toString().trim(), value.toString());
                        tail = null;
                        existKey = false;
                        existValue = false;
                        key.delete(0, key.length());
                        value.delete(0, value.length());
                    }
                }
                else if (tail != null || !" ".equals(String.valueOf(c))) {
                    if (tail != null || !"\n".equals(String.valueOf(c))) {
                        if (tail != null || !"\t".equals(String.valueOf(c))) {
                            if (tail == null && ",".equals(String.valueOf(c))) {
                                existValue = true;
                                log(value.toString());
                                map.put(key.toString().trim(), value.toString());
                                tail = null;
                                existKey = false;
                                existValue = false;
                                key.delete(0, key.length());
                                value.delete(0, value.length());
                            }
                            else if ("'".equals(String.valueOf(c))) {
                                if (tail == null) {
                                    tail = String.valueOf(c);
                                }
                                else if (tail.equals(String.valueOf(c))) {
                                    existValue = true;
                                    log(value.toString());
                                    map.put(key.toString().trim(), value.toString());
                                    tail = null;
                                    existKey = false;
                                    existValue = false;
                                    key.delete(0, key.length());
                                    value.delete(0, value.length());
                                }
                                else {
                                    value.append(c);
                                }
                            }
                            else if ("\"".equals(String.valueOf(c))) {
                                if (tail == null) {
                                    tail = String.valueOf(c);
                                }
                                else if (tail.equals(String.valueOf(c))) {
                                    existValue = true;
                                    log(value.toString());
                                    map.put(key.toString().trim(), value.toString());
                                    tail = null;
                                    existKey = false;
                                    existValue = false;
                                    key.delete(0, key.length());
                                    value.delete(0, value.length());
                                }
                                else {
                                    value.append(c);
                                }
                            }
                            else {
                                value.append(c);
                            }
                        }
                    }
                }
            }
            else if (existKey && existValue) {
                if (!" ".equals(String.valueOf(c))) {
                    if (",".equals(String.valueOf(c))) {
                        map.put(key.toString().trim(), value.toString().trim());
                        tail = null;
                        existKey = false;
                        existValue = false;
                        key.delete(0, key.length());
                        value.delete(0, value.length());
                    }
                }
            }
        }
        if (existKey && !existValue && tail == null) {
            existValue = true;
            log(value.toString().trim());
            map.put(key.toString().trim(), value.toString().trim());
            tail = null;
            existKey = false;
            existValue = false;
            key.delete(0, key.length());
            value.delete(0, value.length());
        }
        return map;
    }
    
    @Override
    public Map<String, String> wrapFromString(final String str) {
        String targetStr = str.trim();
        targetStr = targetStr.substring(1, targetStr.length() - 1);
        return this.getKV(targetStr);
    }
}
