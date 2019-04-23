// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.sql;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: SQLFormatter.java 60270 2013-11-03 14:48:37Z tangxy $")
public class SQLFormatter
{
    private static final String WHITESPACE = " \n\r\f\t";
    private static final Set<String> BEGIN_CLAUSES;
    private static final Set<String> END_CLAUSES;
    private static final Set<String> LOGICAL;
    private static final Set<String> QUANTIFIERS;
    private static final Set<String> DML;
    private static final Set<String> MISC;
    private static final Set<String> FUNCTION;
    static final String indentString = "      ";
    static final String initial = "\n    ";
    
    public static void main(final String[] args) {
        final SQLFormatter f = new SQLFormatter();
        String s = "select * from dual where (\ufffd\ufffd\ufffd\ufffd < to_date('2012-03-21','yyyy-MM-dd') AND \u05f4\u032c IN ( '\ufffd\ufffd\ufffd\ufffd','\u0363\ufffd\ufffd' ) AND \ufffd\ufffd\ufffd IN ( '\ufffd\ufffd\ufffd1','\ufffd\ufffd\ufffd3' )) OR (\ufffd\ufffd\ufffd\ufffd\u02b1\ufffd\ufffd >to_date('2012-03-22 11:28:00','yyyy-MM-dd hh24:mi:ss') AND \ufffd\ufffd\ufffd IN ( '\ufffd\ufffd\ufffd2' ))";
        s = "update td_s_assignrule a SET a.depart_frame=(SELECT b.depart_frame ||(SELECT SUBSTR(depart_frame,INSTR(depart_frame,:VDEPART_ID,1,1)) FROM td_s_assignrule c WHERE c.depart_frame LIKE (SELECT rsrv_str4 FROM td_s_assignrule WHERE depart_id=:VDEPART_ID AND eparchy_code=:VEPARCHY_CODE AND res_type_code=:VRES_TYPE_CODE)||'%' AND c.depart_id=a.depart_id AND c.eparchy_code=:VEPARCHY_CODE AND c.res_type_code=:VRES_TYPE_CODE) FROM td_s_assignrule b WHERE b.depart_id=:VDEPART_CODE AND b.eparchy_code=:VEPARCHY_CODE AND b.res_type_code=:VRES_TYPE_CODE) WHERE a.depart_frame LIKE (SELECT rsrv_str4 FROM td_s_assignrule WHERE depart_id=:VDEPART_ID AND eparchy_code=:VEPARCHY_CODE AND res_type_code=:VRES_TYPE_CODE)||'%' AND a.eparchy_code=:VEPARCHY_CODE AND a.res_type_code=:VRES_TYPE_CODE";
        System.out.println(f.format(s));
    }
    
    public String format(final String source) {
        return new FormatProcess(source).perform();
    }
    
    static {
        BEGIN_CLAUSES = new HashSet<String>();
        END_CLAUSES = new HashSet<String>();
        LOGICAL = new HashSet<String>();
        QUANTIFIERS = new HashSet<String>();
        DML = new HashSet<String>();
        MISC = new HashSet<String>();
        FUNCTION = new HashSet<String>();
        SQLFormatter.BEGIN_CLAUSES.add("left");
        SQLFormatter.BEGIN_CLAUSES.add("right");
        SQLFormatter.BEGIN_CLAUSES.add("inner");
        SQLFormatter.BEGIN_CLAUSES.add("outer");
        SQLFormatter.BEGIN_CLAUSES.add("group");
        SQLFormatter.BEGIN_CLAUSES.add("order");
        SQLFormatter.END_CLAUSES.add("where");
        SQLFormatter.END_CLAUSES.add("set");
        SQLFormatter.END_CLAUSES.add("having");
        SQLFormatter.END_CLAUSES.add("join");
        SQLFormatter.END_CLAUSES.add("from");
        SQLFormatter.END_CLAUSES.add("by");
        SQLFormatter.END_CLAUSES.add("join");
        SQLFormatter.END_CLAUSES.add("into");
        SQLFormatter.END_CLAUSES.add("union");
        SQLFormatter.LOGICAL.add("and");
        SQLFormatter.LOGICAL.add("or");
        SQLFormatter.LOGICAL.add("when");
        SQLFormatter.LOGICAL.add("else");
        SQLFormatter.LOGICAL.add("end");
        SQLFormatter.LOGICAL.add("not");
        SQLFormatter.QUANTIFIERS.add("in");
        SQLFormatter.QUANTIFIERS.add("all");
        SQLFormatter.QUANTIFIERS.add("exists");
        SQLFormatter.QUANTIFIERS.add("some");
        SQLFormatter.QUANTIFIERS.add("any");
        SQLFormatter.QUANTIFIERS.add("like");
        SQLFormatter.DML.add("insert");
        SQLFormatter.DML.add("update");
        SQLFormatter.DML.add("delete");
        SQLFormatter.MISC.add("select");
        SQLFormatter.MISC.add("on");
        SQLFormatter.FUNCTION.add("abs");
        SQLFormatter.FUNCTION.add("acos");
        SQLFormatter.FUNCTION.add("add_months");
        SQLFormatter.FUNCTION.add("ascii");
        SQLFormatter.FUNCTION.add("asin");
        SQLFormatter.FUNCTION.add("atan");
        SQLFormatter.FUNCTION.add("atan2");
        SQLFormatter.FUNCTION.add("avg");
        SQLFormatter.FUNCTION.add("bfilename");
        SQLFormatter.FUNCTION.add("ceil");
        SQLFormatter.FUNCTION.add("chartorowid");
        SQLFormatter.FUNCTION.add("chr");
        SQLFormatter.FUNCTION.add("concat");
        SQLFormatter.FUNCTION.add("convert");
        SQLFormatter.FUNCTION.add("cos");
        SQLFormatter.FUNCTION.add("cosh");
        SQLFormatter.FUNCTION.add("count");
        SQLFormatter.FUNCTION.add("deref");
        SQLFormatter.FUNCTION.add("dump");
        SQLFormatter.FUNCTION.add("empty_blob");
        SQLFormatter.FUNCTION.add("empty_clob");
        SQLFormatter.FUNCTION.add("exp");
        SQLFormatter.FUNCTION.add("floor");
        SQLFormatter.FUNCTION.add("greatest");
        SQLFormatter.FUNCTION.add("grouping");
        SQLFormatter.FUNCTION.add("hextoraw");
        SQLFormatter.FUNCTION.add("initcap");
        SQLFormatter.FUNCTION.add("instr");
        SQLFormatter.FUNCTION.add("instrb");
        SQLFormatter.FUNCTION.add("lpad");
        SQLFormatter.FUNCTION.add("ltrim");
        SQLFormatter.FUNCTION.add("last_day");
        SQLFormatter.FUNCTION.add("least");
        SQLFormatter.FUNCTION.add("length");
        SQLFormatter.FUNCTION.add("lengthb");
        SQLFormatter.FUNCTION.add("ln");
        SQLFormatter.FUNCTION.add("log");
        SQLFormatter.FUNCTION.add("lower");
        SQLFormatter.FUNCTION.add("make_ref");
        SQLFormatter.FUNCTION.add("max");
        SQLFormatter.FUNCTION.add("min");
        SQLFormatter.FUNCTION.add("mod");
        SQLFormatter.FUNCTION.add("months_between");
        SQLFormatter.FUNCTION.add("nlssort");
        SQLFormatter.FUNCTION.add("nls_charset_decl_len");
        SQLFormatter.FUNCTION.add("nls_charset_id");
        SQLFormatter.FUNCTION.add("nls_charset_name");
        SQLFormatter.FUNCTION.add("nls_initcap");
        SQLFormatter.FUNCTION.add("nls_lower");
        SQLFormatter.FUNCTION.add("nls_upper");
        SQLFormatter.FUNCTION.add("new_time");
        SQLFormatter.FUNCTION.add("next_day");
        SQLFormatter.FUNCTION.add("nvl");
        SQLFormatter.FUNCTION.add("power");
        SQLFormatter.FUNCTION.add("rpad");
        SQLFormatter.FUNCTION.add("rtrim");
        SQLFormatter.FUNCTION.add("rawtohex");
        SQLFormatter.FUNCTION.add("ref");
        SQLFormatter.FUNCTION.add("reftohex");
        SQLFormatter.FUNCTION.add("replace");
        SQLFormatter.FUNCTION.add("round");
        SQLFormatter.FUNCTION.add("rowidtochar");
        SQLFormatter.FUNCTION.add("sys_context");
        SQLFormatter.FUNCTION.add("sys_guid");
        SQLFormatter.FUNCTION.add("sign");
        SQLFormatter.FUNCTION.add("sin");
        SQLFormatter.FUNCTION.add("sinh");
        SQLFormatter.FUNCTION.add("soundex");
        SQLFormatter.FUNCTION.add("sqrt");
        SQLFormatter.FUNCTION.add("stddev");
        SQLFormatter.FUNCTION.add("substr");
        SQLFormatter.FUNCTION.add("substrb");
        SQLFormatter.FUNCTION.add("sum");
        SQLFormatter.FUNCTION.add("sysdate");
        SQLFormatter.FUNCTION.add("tan");
        SQLFormatter.FUNCTION.add("tanh");
        SQLFormatter.FUNCTION.add("to_lob");
        SQLFormatter.FUNCTION.add("to_char");
        SQLFormatter.FUNCTION.add("to_date");
        SQLFormatter.FUNCTION.add("to_multi_byte");
        SQLFormatter.FUNCTION.add("to_number");
        SQLFormatter.FUNCTION.add("to_single_byte");
        SQLFormatter.FUNCTION.add("translate");
        SQLFormatter.FUNCTION.add("trim");
        SQLFormatter.FUNCTION.add("trunc");
        SQLFormatter.FUNCTION.add("uid");
        SQLFormatter.FUNCTION.add("upper");
        SQLFormatter.FUNCTION.add("user");
        SQLFormatter.FUNCTION.add("userenv");
        SQLFormatter.FUNCTION.add("vsize");
        SQLFormatter.FUNCTION.add("value");
        SQLFormatter.FUNCTION.add("variance");
    }
    
    private static class FormatProcess
    {
        boolean beginLine;
        boolean afterBeginBeforeEnd;
        boolean afterByOrSetOrFromOrSelect;
        boolean afterValues;
        boolean afterOn;
        boolean afterBetween;
        boolean afterInsert;
        int keywordCaseChange;
        int inFunction;
        int parensSinceSelect;
        private LinkedList<Integer> parenCounts;
        private LinkedList<Boolean> afterByOrFromOrSelects;
        int indent;
        StringBuilder result;
        StringTokenizer tokens;
        String lastToken;
        String token;
        String lcToken;
        private boolean is_huwl_function;
        
        private static boolean isFunctionName(final String tok) {
            final char begin = tok.charAt(0);
            final boolean isIdentifier = Character.isJavaIdentifierStart(begin) || '\"' == begin;
            return isIdentifier && !SQLFormatter.LOGICAL.contains(tok) && !SQLFormatter.END_CLAUSES.contains(tok) && !SQLFormatter.QUANTIFIERS.contains(tok) && !SQLFormatter.DML.contains(tok) && !SQLFormatter.MISC.contains(tok) && !SQLFormatter.FUNCTION.contains(tok);
        }
        
        private static boolean isWhitespace(final String token) {
            return " \n\r\f\t".indexOf(token) >= 0;
        }
        
        public FormatProcess(final String sql) {
            this.beginLine = true;
            this.afterBeginBeforeEnd = false;
            this.afterByOrSetOrFromOrSelect = false;
            this.afterValues = false;
            this.afterOn = false;
            this.afterBetween = false;
            this.afterInsert = false;
            this.keywordCaseChange = 1;
            this.inFunction = 0;
            this.parensSinceSelect = 0;
            this.parenCounts = new LinkedList<Integer>();
            this.afterByOrFromOrSelects = new LinkedList<Boolean>();
            this.indent = 1;
            this.result = new StringBuilder();
            this.is_huwl_function = false;
            this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
        }
        
        private void beginNewClause() {
            if (!this.afterBeginBeforeEnd) {
                if (this.afterOn) {
                    --this.indent;
                    this.afterOn = false;
                }
                --this.indent;
                this.newline();
            }
            this.out();
            this.beginLine = false;
            this.afterBeginBeforeEnd = true;
        }
        
        private String capitalize(final String tok) {
            return tok.substring(0, 1).toUpperCase() + tok.toLowerCase().substring(1);
        }
        
        private void closeParen() {
            --this.parensSinceSelect;
            if (this.parensSinceSelect < 0) {
                --this.indent;
                this.parensSinceSelect = this.parenCounts.removeLast();
                this.afterByOrSetOrFromOrSelect = this.afterByOrFromOrSelects.removeLast();
            }
            if (this.inFunction > 0) {
                --this.inFunction;
                this.out();
            }
            else {
                if (!this.afterByOrSetOrFromOrSelect) {
                    --this.indent;
                    this.newline();
                }
                this.out();
            }
            this.beginLine = false;
            this.is_huwl_function = false;
        }
        
        private void commaAfterByOrFromOrSelect() {
            this.out();
            if (!this.is_huwl_function) {
                this.newline();
            }
        }
        
        private void commaAfterOn() {
            this.out();
            --this.indent;
            this.newline();
            this.afterOn = false;
            this.afterByOrSetOrFromOrSelect = true;
        }
        
        private void endNewClause() {
            if (!this.afterBeginBeforeEnd) {
                --this.indent;
                if (this.afterOn) {
                    --this.indent;
                    this.afterOn = false;
                }
                this.newline();
            }
            this.out();
            if (!"union".equals(this.lcToken)) {
                ++this.indent;
            }
            this.newline();
            this.afterBeginBeforeEnd = false;
            this.afterByOrSetOrFromOrSelect = ("by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken));
        }
        
        private boolean isKeyWord(final String toka) {
            final String tok = toka.toLowerCase();
            return SQLFormatter.LOGICAL.contains(tok) || SQLFormatter.END_CLAUSES.contains(tok) || SQLFormatter.QUANTIFIERS.contains(tok) || SQLFormatter.DML.contains(tok) || SQLFormatter.MISC.contains(tok) || SQLFormatter.FUNCTION.contains(tok) || SQLFormatter.BEGIN_CLAUSES.contains(tok);
        }
        
        private void logical() {
            if ("end".equals(this.lcToken)) {
                --this.indent;
            }
            if ("      ".length() > 4) {
                --this.indent;
                this.newline();
                String s2 = "";
                if (this.token.length() == 2) {
                    s2 = "  ";
                }
                else if (this.token.length() == 3) {
                    s2 = " ";
                }
                this.out("      ".substring(0, "      ".length() - 5) + s2);
                this.out(this.token);
                ++this.indent;
            }
            else {
                this.newline();
                this.out();
            }
            this.beginLine = false;
        }
        
        private void misc() {
            this.out();
            if ("between".equals(this.lcToken)) {
                this.afterBetween = true;
            }
            if (this.afterInsert) {
                this.newline();
                this.afterInsert = false;
            }
            else {
                this.beginLine = false;
                if ("case".equals(this.lcToken)) {
                    ++this.indent;
                }
            }
        }
        
        private void newline() {
            this.result.append("\n");
            for (int i = 0; i < this.indent; ++i) {
                this.result.append("      ");
            }
            this.beginLine = true;
        }
        
        private void on() {
            ++this.indent;
            this.afterOn = true;
            this.newline();
            this.out();
            this.beginLine = false;
        }
        
        private void openParen() {
            if (isFunctionName(this.lastToken) || this.inFunction > 0) {
                ++this.inFunction;
            }
            this.beginLine = false;
            if (this.inFunction > 0) {
                this.out();
            }
            else {
                this.out();
                if (!this.afterByOrSetOrFromOrSelect) {
                    ++this.indent;
                    this.newline();
                    this.beginLine = true;
                }
            }
            ++this.parensSinceSelect;
            if (SQLFormatter.FUNCTION.contains(this.lastToken)) {
                this.is_huwl_function = true;
            }
        }
        
        private void out() {
            this.out(this.token);
        }
        
        private void out(final String tokenstring) {
            if (this.keywordCaseChange != 0) {
                if (this.isKeyWord(tokenstring)) {
                    if (this.keywordCaseChange == 1) {
                        this.result.append(tokenstring.toUpperCase());
                    }
                    else if (this.keywordCaseChange == 2) {
                        this.result.append(tokenstring.toLowerCase());
                    }
                    else if (this.keywordCaseChange == 3) {
                        this.result.append(this.capitalize(tokenstring));
                    }
                    else {
                        this.result.append(tokenstring);
                    }
                }
                else {
                    this.result.append(tokenstring);
                }
            }
            else {
                this.result.append(tokenstring);
            }
        }
        
        public String perform() {
            this.result.append("\n    ");
            while (this.tokens.hasMoreTokens()) {
                this.token = this.tokens.nextToken();
                this.lcToken = this.token.toLowerCase();
                if ("'".equals(this.token)) {
                    String t;
                    do {
                        t = this.tokens.nextToken();
                        this.token += t;
                    } while (!"'".equals(t) && this.tokens.hasMoreTokens());
                }
                else if ("\"".equals(this.token)) {
                    String t;
                    do {
                        t = this.tokens.nextToken();
                        this.token += t;
                    } while (!"\"".equals(t));
                }
                if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token)) {
                    this.commaAfterByOrFromOrSelect();
                }
                else if (this.afterOn && ",".equals(this.token)) {
                    this.commaAfterOn();
                }
                else if ("(".equals(this.token)) {
                    this.openParen();
                }
                else if (")".equals(this.token)) {
                    this.closeParen();
                }
                else if (SQLFormatter.BEGIN_CLAUSES.contains(this.lcToken)) {
                    this.beginNewClause();
                }
                else if (SQLFormatter.END_CLAUSES.contains(this.lcToken)) {
                    this.endNewClause();
                }
                else if ("select".equals(this.lcToken)) {
                    this.select();
                }
                else if (SQLFormatter.DML.contains(this.lcToken)) {
                    this.updateOrInsertOrDelete();
                }
                else if ("values".equals(this.lcToken)) {
                    this.values();
                }
                else if ("on".equals(this.lcToken)) {
                    this.on();
                }
                else if (this.afterBetween && this.lcToken.equals("and")) {
                    this.misc();
                    this.afterBetween = false;
                }
                else if (SQLFormatter.LOGICAL.contains(this.lcToken)) {
                    this.logical();
                }
                else if (isWhitespace(this.token)) {
                    this.white();
                }
                else {
                    this.misc();
                }
                if (!isWhitespace(this.token)) {
                    this.lastToken = this.lcToken;
                }
            }
            return this.result.toString();
        }
        
        private void select() {
            this.out();
            ++this.indent;
            this.newline();
            this.parenCounts.addLast(this.parensSinceSelect);
            this.afterByOrFromOrSelects.addLast(this.afterByOrSetOrFromOrSelect);
            this.parensSinceSelect = 0;
            this.afterByOrSetOrFromOrSelect = true;
        }
        
        private void updateOrInsertOrDelete() {
            this.out();
            ++this.indent;
            this.beginLine = false;
            if ("update".equals(this.lcToken)) {
                this.newline();
            }
            if ("insert".equals(this.lcToken)) {
                this.afterInsert = true;
            }
        }
        
        private void values() {
            --this.indent;
            this.newline();
            this.out();
            ++this.indent;
            this.newline();
            this.afterValues = true;
        }
        
        private void white() {
            if (!this.beginLine) {
                this.result.append(" ");
            }
        }
    }
}
