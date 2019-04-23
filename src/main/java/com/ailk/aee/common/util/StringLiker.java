// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.util;

import java.util.regex.Pattern;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: StringLiker.java 60270 2013-11-03 14:48:37Z tangxy $")
public class StringLiker
{
    private String c_match_all;
    private String c_match_one;
    public static StringLiker PathLiker;
    public static StringLiker DBLiker;
    static char[] STRAPI_UPPER_TO_LOWER;
    static char[] STRAPI_LOWER_TO_UPPER;
    static int iflag;
    
    public static void main(final String[] args) {
        boolean v1 = false;
        boolean v2 = false;
        v1 = StringLiker.PathLiker.isLike("AEE_HOME", "AEE*E");
        v2 = StringLiker.DBLiker.isLike("AEE_HOME", "AEE*");
        System.out.println(Pattern.matches("^AEE_HO.?\\?E$", "AEE_HOM?E"));
        System.out.println(v1 || v2);
    }
    
    public StringLiker(final String allChar, final String singleChar) {
        this.c_match_all = "*";
        this.c_match_one = "?";
        this.c_match_all = allChar;
        this.c_match_one = singleChar;
    }
    
    private boolean isJavaPatternMetaChar(final char ch) {
        final char[] metachar = { '$', '^', '[', ']', '(', ')', '{', '|', '*', '+', '?', '.', '\\' };
        for (int j = 0; j < metachar.length; ++j) {
            if (ch == metachar[j]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLike(final String src, String pattern) {
        pattern = this.toJavaPattern(pattern);
        return Pattern.matches(pattern, src);
    }
    
    private String toJavaPattern(final String pattern) {
        String result = "^";
        for (int i = 0; i < pattern.length(); ++i) {
            final char ch = pattern.charAt(i);
            if (ch == this.c_match_all.charAt(0)) {
                result += ".*";
            }
            else if (ch == this.c_match_one.charAt(0)) {
                result += ".?";
            }
            else if (this.isJavaPatternMetaChar(ch)) {
                result = result + "\\" + ch;
            }
            else {
                result += ch;
            }
        }
        result += "$";
        return result;
    }
    
    static {
        StringLiker.PathLiker = new StringLiker("*", "?");
        StringLiker.DBLiker = new StringLiker("%", "_");
        StringLiker.STRAPI_UPPER_TO_LOWER = new char[256];
        StringLiker.STRAPI_LOWER_TO_UPPER = new char[256];
        StringLiker.iflag = 0;
    }
}
