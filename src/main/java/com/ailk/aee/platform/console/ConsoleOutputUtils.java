// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.platform.console;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collection;
import com.ailk.aee.common.conf.MapTools;
import java.util.HashSet;
import java.util.Iterator;
import com.ailk.aee.common.util.StringUtils;
import java.util.Date;
import com.ailk.aee.common.util.DateFormatUtils;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import com.ailk.aee.common.util.ExceptionUtils;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: ConsoleOutputUtils.java 60270 2013-11-03 14:48:37Z tangxy $")
public class ConsoleOutputUtils
{
    public static int weight;
    public static int firstRow;
    public static int leadingspace;
    public static boolean isLineRow;
    private char[][] datas;
    private int currentline;
    
    public ConsoleOutputUtils() {
        this.datas = new char[3000][ConsoleOutputUtils.weight + 50];
        this.currentline = 0;
    }
    
    public static String asList(final Map<String, String> map) {
        final ConsoleOutputUtils cp = new ConsoleOutputUtils();
        return cp.toListString(map);
    }
    
    public static String asTable(final Map<String, String> map) {
        try {
            final ConsoleOutputUtils cp = new ConsoleOutputUtils();
            return cp.toTableString(map);
        }
        catch (Exception e) {
            return ExceptionUtils.getExceptionStack(e);
        }
    }
    
    public static void asListPrint(final Map<String, String> map) {
        try {
            final ConsoleOutputUtils cp = new ConsoleOutputUtils();
            System.out.println(cp.toListString(map));
        }
        catch (Exception e) {
            System.out.println(ExceptionUtils.getExceptionStack(e));
        }
    }
    
    public static void asTablePrint(final Map<String, String> map) {
        try {
            final ConsoleOutputUtils cp = new ConsoleOutputUtils();
            System.out.println(cp.toTableString(map));
        }
        catch (Exception e) {
            System.out.println(ExceptionUtils.getExceptionStack(e));
        }
    }
    
    public static String asTableUtf8(final Map<String, String> map) {
        try {
            final ConsoleOutputUtils cp = new ConsoleOutputUtils();
            final String s = cp.toTableString(map);
            final String sv = getUTF8StringFromGBKString(s);
            return sv;
        }
        catch (Exception e) {
            return ExceptionUtils.getExceptionStack(e);
        }
    }
    
    public static int getFirstRow() {
        return ConsoleOutputUtils.firstRow;
    }
    
    public static int getLeadingspace() {
        return ConsoleOutputUtils.leadingspace;
    }
    
    public static byte[] getUTF8BytesFromGBKString(final String gbkStr) {
        final int n = gbkStr.length();
        final byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; ++i) {
            final int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte)m;
            }
            else {
                utfBytes[k++] = (byte)(0xE0 | m >> 12);
                utfBytes[k++] = (byte)(0x80 | (m >> 6 & 0x3F));
                utfBytes[k++] = (byte)(0x80 | (m & 0x3F));
            }
        }
        if (k < utfBytes.length) {
            final byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }
    
    public static String getUTF8StringFromGBKString(final String gbkStr) {
        try {
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }
    
    public static int getWeight() {
        return ConsoleOutputUtils.weight;
    }
    
    public static boolean isLineRow() {
        return ConsoleOutputUtils.isLineRow;
    }
    
    public static void main(final String[] args) {
        final Map<String, String> ret = new HashMap<String, String>();
        final String[] ss = { "Work1", "work2", "work3", "work4" };
        ret.put("hello ", "workd");
        for (final String s : ss) {
            final String sdate = DateFormatUtils.SIMPLE_DATETIME_FORMAT.format(new Date());
            ret.put(s + ".PID", "8923");
            ret.put(s + ".SAULT", "123213123");
            ret.put(s + ".STARTTIME", sdate);
            ret.put(s + ".STATUS", "running");
        }
        ret.clear();
        ret.put("workm.INFO", "AEE_RESULT_INFO]=[\ufffd\u07b7\ufffd\ufffd\ufffd\ufffd\u04fc\ufffd\ufffd\ufffd\u02ff\ufffd@127.0.0.1:9527]");
        final String s2 = asTable(ret);
        System.out.println(s2);
    }
    
    public static void setDefault() {
        ConsoleOutputUtils.weight = 120;
        ConsoleOutputUtils.firstRow = 20;
        ConsoleOutputUtils.leadingspace = 0;
        ConsoleOutputUtils.isLineRow = false;
    }
    
    public static void setFirstRow(final int firstRow) {
        ConsoleOutputUtils.firstRow = firstRow;
    }
    
    public static void setLeadingspace(final int leadingspace) {
        ConsoleOutputUtils.leadingspace = leadingspace;
    }
    
    public static void setLineRow(final boolean isLineRow) {
        ConsoleOutputUtils.isLineRow = isLineRow;
    }
    
    public static void setWeight(final int weight) {
        ConsoleOutputUtils.weight = weight;
    }
    
    private void calc(final Map<String, String> map, final String[] cols, final String[] rows, final int[] colstart, final int[] collength, final String[] coltitle) {
        if (cols.length == 1) {
            collength[colstart[0] = 0] = ConsoleOutputUtils.firstRow;
            colstart[1] = ConsoleOutputUtils.firstRow;
            collength[1] = ConsoleOutputUtils.weight - ConsoleOutputUtils.firstRow;
            return;
        }
        collength[colstart[0] = 0] = ConsoleOutputUtils.firstRow;
        final int[] tmpcolmaxlen = new int[colstart.length];
        for (int i = 1; i < tmpcolmaxlen.length; ++i) {
            tmpcolmaxlen[i] = this.getMaxLength(map, rows, cols[i - 1]);
        }
        int total = ConsoleOutputUtils.firstRow;
        for (int j = 0; j < tmpcolmaxlen.length; ++j) {
            total += tmpcolmaxlen[j];
        }
        if (total < ConsoleOutputUtils.weight) {
            for (int j = 1; j < tmpcolmaxlen.length; ++j) {
                colstart[j] = colstart[j - 1] + collength[j - 1];
                collength[j] = tmpcolmaxlen[j];
            }
        }
        else {
            final int avg = (ConsoleOutputUtils.weight - 25) / (tmpcolmaxlen.length - 1);
            int shengyu = 0;
            for (int k = 1; k < tmpcolmaxlen.length; ++k) {
                colstart[k] = colstart[k - 1] + collength[k - 1];
                if (tmpcolmaxlen[k] < avg) {
                    collength[k] = tmpcolmaxlen[k];
                    shengyu = avg - collength[k];
                }
                else {
                    collength[k] = avg + shengyu;
                    shengyu = 0;
                }
            }
        }
    }
    
    private void drawAllSingle(final Map<String, String> all) {
        final Map<String, String> map = new HashMap<String, String>();
        map.putAll(all);
        final Map<String, String> titles = new HashMap<String, String>();
        int l = 1;
        int maxkeylen = 0;
        for (final Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey().indexOf(".") >= 0) {
                continue;
            }
            titles.put(e.getKey(), e.getValue());
            if (maxkeylen <= e.getKey().length()) {
                maxkeylen = e.getKey().length();
            }
            all.remove(e.getKey());
        }
        this.writeLine(0);
        if (titles.size() > 0) {
            for (final Map.Entry<String, String> e : titles.entrySet()) {
                l += this.drawText(" " + StringUtils.rightPad((String)e.getKey(), maxkeylen) + "=" + e.getValue(), l, 0, ConsoleOutputUtils.weight, maxkeylen + 1);
            }
            this.writeLine(this.currentline = l);
        }
    }
    
    private void drawLineText(final String s, final int startrow, final int startcol) {
        for (int i = 0; i < s.length(); ++i) {
            this.datas[startrow][startcol + i] = s.charAt(i);
        }
    }
    
    private void drawRow(final String[] values, final int[] colstart, final int[] collength) {
        final int height_start = this.currentline + 1;
        int maxHeight = 1;
        int tempHeight = 0;
        int totalv = 0;
        for (int i = 0; i < values.length; ++i) {
            final String v = values[i];
            if (i == values.length - 1) {
                tempHeight = this.drawText(" " + v, height_start, colstart[i], ConsoleOutputUtils.weight - totalv, 0);
            }
            else {
                totalv += collength[i];
                tempHeight = this.drawText(" " + v, height_start, colstart[i], collength[i] + 1, 0);
            }
            if (maxHeight < tempHeight) {
                maxHeight = tempHeight;
            }
        }
        for (int i = 0; i < maxHeight; ++i) {
            for (int j = 0; j < colstart.length; ++j) {
                this.datas[height_start + i][colstart[j]] = '|';
            }
            this.datas[height_start + i][ConsoleOutputUtils.weight - 1] = '|';
        }
        if (ConsoleOutputUtils.isLineRow) {
            this.writeLine(height_start + maxHeight);
            this.currentline = height_start + maxHeight;
        }
        else {
            this.currentline = height_start + maxHeight - 1;
        }
    }
    
    private void drawSingleRow(final String w, final String v) {
        final int height_start = this.currentline + 1;
        int tempHeight = 0;
        final int tempHeight2 = this.drawText(" " + w, height_start, 0, ConsoleOutputUtils.firstRow + 1, 0);
        final int tempHeight3 = this.drawText(" " + v, height_start, ConsoleOutputUtils.firstRow, ConsoleOutputUtils.weight - ConsoleOutputUtils.firstRow, 0);
        if (tempHeight3 > tempHeight2) {
            tempHeight = tempHeight3;
        }
        else {
            tempHeight = tempHeight2;
        }
        if (ConsoleOutputUtils.isLineRow) {
            this.writeLine(height_start + tempHeight);
            this.currentline = height_start + tempHeight;
        }
        else {
            this.currentline = height_start + tempHeight - 1;
        }
        for (int i = 0; i < tempHeight; ++i) {
            this.datas[height_start + i][0] = '|';
        }
    }
    
    private int drawText(final String s, final int startrow, final int startcol, final int length, final int wraplength) {
        final int reallength = length - 2;
        final int strLength = this.getRealBLength(s);
        if (strLength < reallength) {
            this.datas[startrow][startcol] = '|';
            this.datas[startrow][startcol + length - 1] = '|';
            this.drawLineText(s, startrow, startcol + 1);
            return 1;
        }
        String[] cols;
        int i;
        String sv;
        for (cols = this.splitString(s, reallength, wraplength), i = 0, i = 0; i < cols.length; ++i) {
            sv = cols[i];
            this.datas[startrow + i][startcol] = '|';
            this.drawLineText(sv, startrow + i, startcol + 1);
            if (i == cols.length - 1) {
                this.datas[startrow + i][startcol + length - 1] = '|';
            }
            else {
                this.datas[startrow + i][startcol + sv.length() + 1] = '|';
            }
        }
        return i;
    }
    
    private int drawText2(final String s, final int startrow, final int startcol, final int length, int wraplength) {
        final int reallength = length - 2;
        if (wraplength >= reallength) {
            wraplength = 0;
        }
        if (this.getRealBLength(s) < reallength) {
            this.datas[startrow][startcol] = '|';
            this.datas[startrow][startcol + length - 1] = '|';
            this.drawLineText(s, startrow, startcol + 1);
            return 1;
        }
        final int realcol = (this.getRealBLength(s) - reallength) / (reallength - wraplength) + 2;
        for (int i = 0; i < realcol; ++i) {
            String s2 = "";
            if (i == 0) {
                s2 = this.getRealBString(s, 0, reallength);
            }
            else {
                s2 = this.getRealBString(s, reallength + (i - 1) * (reallength - wraplength), reallength + i * (reallength - wraplength));
            }
            this.datas[startrow + i][startcol] = '|';
            this.datas[startrow + i][startcol + length - 1] = '|';
            if (i == 0) {
                this.drawLineText(s2, startrow + i, startcol + 1);
            }
            else {
                this.drawLineText(s2, startrow + i, startcol + 1 + wraplength);
            }
        }
        return realcol;
    }
    
    private String[] getCols(final Map<String, String> map, final String[] rows) {
        final HashSet<String> hs = new HashSet<String>();
        for (final String s : rows) {
            final Map<String, String> msub = (Map<String, String>)MapTools.getSub((Map)map, s);
            if (msub.size() != 1) {
                hs.addAll(msub.keySet());
            }
        }
        if (hs.size() == 0) {
            hs.add("INFO VALUE");
        }
        boolean hasCode = false;
        if (hs.contains("AEE_RESULT_CODE")) {
            hs.remove("AEE_RESULT_CODE");
            hasCode = true;
        }
        boolean hasInfo = false;
        if (hs.contains("AEE_RESULT_INFO")) {
            hs.remove("AEE_RESULT_INFO");
            hasInfo = true;
        }
        final LinkedList<String> l = new LinkedList<String>();
        l.addAll(hs);
        if (hasCode) {
            l.addFirst("AEE_RESULT_CODE");
        }
        if (hasInfo) {
            l.addLast("AEE_RESULT_INFO");
        }
        return l.toArray(new String[0]);
    }
    
    private int getMaxLength(final Map<String, String> map, final String[] rows, final String k) {
        int maxlen = 0;
        int len = 0;
        for (final String s : rows) {
            final Map<String, String> msub = (Map<String, String>)MapTools.getSub((Map)map, s);
            if (msub.size() != 1) {
                final String v = msub.get(k);
                if (v == null) {
                    len = 0;
                }
                else {
                    len = v.length();
                }
                if (maxlen < len) {
                    maxlen = len;
                }
            }
        }
        return maxlen + 4;
    }
    
    private int getRealBLength(final String s) {
        int r = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c < '\u0100' && c > '\0') {
                ++r;
            }
            else {
                r += 2;
            }
        }
        return r;
    }
    
    private String getRealBString(final String s, final int start, final int length2) {
        int r = 0;
        int l = 0;
        final int length3 = length2 - 1;
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c < '\u0100' && c > '\0') {
                ++r;
            }
            else {
                r += 2;
            }
            if (r >= start) {
                sb.append(c);
                if (c < '\u0100' && c > '\0') {
                    ++l;
                }
                else {
                    l += 2;
                }
                if (l > length3) {
                    return sb.toString();
                }
            }
        }
        return sb.toString();
    }
    
    private String[] getRows(final Map<String, String> map) {
        final String[] xx = MapTools.getSubKeys((Map)map);
        final List<String> ls = new ArrayList<String>();
        for (final String s : xx) {
            ls.add(s);
        }
        Collections.sort(ls);
        return ls.toArray(new String[0]);
    }
    
    public void init() {
        for (int i = 0; i < 3000; ++i) {
            for (int j = 0; j < ConsoleOutputUtils.weight; ++j) {
                this.datas[i][j] = ' ';
            }
        }
    }
    
    private String newLine() {
        return "\n";
    }
    
    private String[] splitString(final String s, final int length, final int wraplength) {
        final List<String> ls = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int thisline = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            boolean ism = false;
            boolean isNextM = false;
            ism = (c >= '\u00ff' || c <= '\0');
            if (i != s.length() - 1) {
                final char c2 = s.charAt(i + 1);
                isNextM = (c2 >= '\u00ff' || c <= '\0');
            }
            sb.append(c);
            if (!ism) {
                ++thisline;
            }
            else {
                thisline += 2;
            }
            if (thisline + 1 == length) {
                if (isNextM) {
                    sb.append(" ");
                }
                else if (i + 1 < s.length()) {
                    sb.append(s.charAt(i + 1));
                    ++i;
                }
                ++thisline;
            }
            if (thisline == length) {
                ls.add(sb.toString());
                sb = new StringBuffer();
                thisline = 0;
            }
        }
        ls.add(sb.toString());
        sb = new StringBuffer();
        return ls.toArray(new String[0]);
    }
    
    public String toListString(final Map<String, String> map) {
        this.init();
        final Map<String, String> all = new HashMap<String, String>();
        all.putAll(map);
        this.writeLine(0);
        final Map<String, String> titles = new HashMap<String, String>();
        int l = 1;
        int maxkeylen = 0;
        for (final Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey().indexOf(".") >= 0) {
                continue;
            }
            titles.put(e.getKey(), e.getValue());
            all.remove(e.getKey());
            if (maxkeylen > e.getKey().length()) {
                continue;
            }
            maxkeylen = e.getKey().length();
        }
        for (final Map.Entry<String, String> e : titles.entrySet()) {
            l += this.drawText(" " + StringUtils.rightPad((String)e.getKey(), maxkeylen) + "=" + e.getValue(), l, 0, ConsoleOutputUtils.weight, maxkeylen + 1);
        }
        this.writeLine(this.currentline = l);
        final String[] rows = MapTools.getSubKeys((Map)all);
        if (rows == null || rows.length == 0) {
            this.drawText("empty", this.currentline + 1, 0, ConsoleOutputUtils.weight, 0);
            this.writeLine(this.currentline + 2);
        }
        else {
            int l1_h = this.currentline + 1;
            int l2_h = this.currentline + 1;
            final int oldStart = this.currentline + 1;
            for (int i = 0; i < rows.length; ++i) {
                l1_h += this.drawText(" " + rows[i], l1_h, 0, ConsoleOutputUtils.firstRow + 1, 0);
                final Map<String, String> subs = (Map<String, String>)MapTools.getSub((Map)all, rows[i], false);
                maxkeylen = 0;
                int subrows = 0;
                for (final Map.Entry<String, String> e2 : subs.entrySet()) {
                    if (maxkeylen <= e2.getKey().length()) {
                        maxkeylen = e2.getKey().length();
                    }
                    ++subrows;
                }
                final List<String> l2 = new ArrayList<String>();
                l2.addAll(subs.keySet());
                Collections.sort(l2);
                for (final String key : l2) {
                    String v = subs.get(key);
                    if (v == null) {
                        v = "";
                    }
                    final String ssub = " " + StringUtils.rightPad(key, maxkeylen) + "=" + v;
                    l2_h += this.drawText(ssub, l2_h, ConsoleOutputUtils.firstRow, ConsoleOutputUtils.weight - ConsoleOutputUtils.firstRow, ConsoleOutputUtils.firstRow + maxkeylen);
                }
                if (l1_h < l2_h) {
                    l1_h = l2_h;
                }
                for (int m = oldStart; m < l1_h; ++m) {
                    this.datas[m][0] = '|';
                    this.datas[m][ConsoleOutputUtils.firstRow] = '|';
                    this.datas[m][ConsoleOutputUtils.weight - 1] = '|';
                }
                this.writeLine(l1_h);
                ++l1_h;
                ++l2_h;
            }
            this.currentline = l1_h + 1;
        }
        return this.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= this.currentline; ++i) {
            for (int m = 0; m < ConsoleOutputUtils.leadingspace; ++m) {
                sb.append(" ");
            }
            for (int j = 0; j < ConsoleOutputUtils.weight; ++j) {
                sb.append(this.datas[i][j]);
            }
            sb.append(this.newLine());
        }
        return sb.toString();
    }
    
    public String toTableString(final Map<String, String> map) {
        this.init();
        final Map<String, String> all = new HashMap<String, String>();
        all.putAll(map);
        this.drawAllSingle(all);
        if (all.size() == 0) {
            return this.toString();
        }
        final String[] rows = this.getRows(all);
        final String[] cols = this.getCols(all, rows);
        final int[] colstart = new int[cols.length + 1];
        final int[] collength = new int[cols.length + 1];
        final String[] coltitle = new String[cols.length + 1];
        this.calc(map, cols, rows, colstart, collength, coltitle);
        final String[] ms = new String[cols.length + 1];
        for (int i = 0; i < cols.length; ++i) {
            ms[i + 1] = cols[i];
        }
        ms[0] = "";
        this.drawRow(ms, colstart, collength);
        this.writeLine(this.currentline + 1);
        for (final String rid : rows) {
            final Map<String, String> msub = (Map<String, String>)MapTools.getSub((Map)map, rid);
            if (msub.size() == 1) {
                this.drawSingleRow(rid, msub.values().iterator().next());
            }
            else {
                for (int j = 0; j < cols.length; ++j) {
                    String v = map.get(rid + "." + cols[j]);
                    if (v == null) {
                        v = "";
                    }
                    ms[j + 1] = v;
                }
                ms[0] = rid;
                this.drawRow(ms, colstart, collength);
            }
        }
        if (!ConsoleOutputUtils.isLineRow) {
            this.writeLine(this.currentline + 1);
        }
        return this.toString();
    }
    
    public String toTableString2(final Map<String, String> map) {
        this.init();
        final Map<String, String> all = new HashMap<String, String>();
        all.putAll(map);
        this.writeLine(0);
        final Map<String, String> titles = new HashMap<String, String>();
        int l = 1;
        int maxkeylen = 0;
        for (final Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey().indexOf(".") >= 0) {
                continue;
            }
            titles.put(e.getKey(), e.getValue());
            all.remove(e.getKey());
            if (maxkeylen > e.getKey().length()) {
                continue;
            }
            maxkeylen = e.getKey().length();
        }
        for (final Map.Entry<String, String> e : titles.entrySet()) {
            l += this.drawText(" " + StringUtils.rightPad((String)e.getKey(), maxkeylen) + "=" + e.getValue(), l, 0, ConsoleOutputUtils.weight, maxkeylen + 1);
        }
        this.writeLine(this.currentline = l);
        final String[] rows = MapTools.getSubKeys((Map)all);
        int colslength = 0;
        final HashSet<String> hs = new HashSet<String>();
        for (int i = 0; i < rows.length; ++i) {
            final Map<String, String> ms = (Map<String, String>)MapTools.getSub((Map)all, rows[i]);
            hs.addAll(ms.keySet());
        }
        colslength = hs.size();
        final String[] cols = hs.toArray(new String[0]);
        final int[] colpos = new int[colslength];
        final int[] colposmax = new int[colslength];
        for (int j = 0; j < cols.length; ++j) {
            colposmax[j] = (colpos[j] = 0);
        }
        for (int j = 0; j < cols.length; ++j) {
            for (final String r : rows) {
                final String s = all.get(r + "." + cols[j]);
                if (s != null && colposmax[j] < s.length() + 2) {
                    colposmax[j] = s.length() + 2;
                }
            }
        }
        int total = colpos.length + 2 + ConsoleOutputUtils.firstRow + 1;
        for (int k = 0; k < cols.length; ++k) {
            total += colposmax[k];
        }
        if (total < ConsoleOutputUtils.weight) {
            for (int k = 0; k < cols.length; ++k) {
                colpos[k] = colposmax[k] + (ConsoleOutputUtils.weight - total) / cols.length;
            }
        }
        else {
            final int avg = (ConsoleOutputUtils.weight - (colpos.length + 2 + ConsoleOutputUtils.firstRow + 1)) / cols.length;
            for (int m = 0; m < cols.length; ++m) {
                if (colposmax[m] < avg) {
                    colpos[m] = colposmax[m];
                }
                else {
                    colpos[m] = avg;
                }
            }
        }
        if (rows == null || rows.length == 0) {
            this.drawText("empty", this.currentline + 1, 0, ConsoleOutputUtils.weight, 0);
            this.writeLine(this.currentline + 2);
        }
        else {
            final int[] lh = new int[cols.length + 1];
            for (int m2 = 0; m2 < lh.length; ++m2) {
                lh[m2] = this.currentline + 1;
            }
            final int oldStart = this.currentline + 1;
            int si = ConsoleOutputUtils.firstRow;
            this.datas[oldStart][0] = '|';
            for (int j2 = 0; j2 < cols.length; ++j2) {
                if (j2 == cols.length - 1) {
                    final int[] array = lh;
                    final int n = j2 + 1;
                    array[n] += this.drawText(" " + cols[j2], lh[j2 + 1], si, ConsoleOutputUtils.weight - si, 0);
                }
                else {
                    final int[] array2 = lh;
                    final int n2 = j2 + 1;
                    array2[n2] += this.drawText(" " + cols[j2], lh[j2 + 1], si, colpos[j2] + 1, 0);
                }
                si += colpos[j2];
            }
            int p = 0;
            for (int m3 = 0; m3 < lh.length; ++m3) {
                if (p < lh[m3]) {
                    p = lh[m3];
                }
            }
            ++p;
            for (int m3 = 0; m3 < lh.length; ++m3) {
                lh[m3] = p;
            }
            this.writeLine(lh[0] - 1);
            for (int i2 = 0; i2 < rows.length; ++i2) {
                final int[] array3 = lh;
                final int n3 = 0;
                array3[n3] += this.drawText(" " + rows[i2], lh[0], 0, ConsoleOutputUtils.firstRow + 1, 0);
                si = ConsoleOutputUtils.firstRow;
                for (int j3 = 0; j3 < cols.length; ++j3) {
                    String v = all.get(rows[i2] + "." + cols[j3]);
                    if (v == null) {
                        v = "";
                    }
                    if (j3 == cols.length - 1) {
                        final int[] array4 = lh;
                        final int n4 = j3 + 1;
                        array4[n4] += this.drawText(" " + v, lh[j3 + 1], si, ConsoleOutputUtils.weight - si, 0);
                    }
                    else {
                        final int[] array5 = lh;
                        final int n5 = j3 + 1;
                        array5[n5] += this.drawText(" " + v, lh[j3 + 1], si, colpos[j3] + 1, 0);
                    }
                    si += colpos[j3];
                }
                p = 0;
                for (int m4 = 0; m4 < lh.length; ++m4) {
                    if (p < lh[m4]) {
                        p = lh[m4];
                    }
                }
                ++p;
                for (int m4 = 0; m4 < lh.length; ++m4) {
                    lh[m4] = p;
                }
                this.writeLine(lh[0] - 1);
            }
            this.currentline = lh[0];
        }
        return this.toString();
    }
    
    private void writeLine(final int i) {
        for (int j = 0; j < ConsoleOutputUtils.weight; ++j) {
            this.datas[i][j] = '-';
        }
        if (this.currentline <= i) {
            this.currentline = i;
        }
    }
    
    static {
        ConsoleOutputUtils.weight = 120;
        ConsoleOutputUtils.firstRow = 20;
        ConsoleOutputUtils.leadingspace = 0;
        ConsoleOutputUtils.isLineRow = false;
    }
}
