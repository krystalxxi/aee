// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.script.expr;

import java.util.HashMap;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: OPSBuilder.java 60270 2013-11-03 14:48:37Z tangxy $")
public class OPSBuilder
{
    private static Map<String, OPS> allOPS;
    
    public static synchronized void addCalcuator(final String op, final OPS o) {
        OPSBuilder.allOPS.put(op, o);
    }
    
    public static OPS build(final String operator) {
        return OPSBuilder.allOPS.get(operator.toUpperCase());
    }
    
    public static boolean calc(final Object o, final String equal, final Object v) {
        final OPS ops = build(equal);
        return ops != null && ops.calc(o, v);
    }
    
    public static void main(final String[] args) {
        final String operator = "between";
        final OPS ops = build(operator);
        boolean v = false;
        v = ops.calc(9, new Integer[] { null, new Integer(12) });
        System.out.println(v);
    }
    
    static {
        (OPSBuilder.allOPS = new HashMap<String, OPS>()).put("=", OPS.EQUAL);
        OPSBuilder.allOPS.put("==", OPS.EQUAL);
        OPSBuilder.allOPS.put("EQ", OPS.EQUAL);
        OPSBuilder.allOPS.put("EQUAL", OPS.EQUAL);
        OPSBuilder.allOPS.put(">", OPS.GT);
        OPSBuilder.allOPS.put("GT", OPS.GT);
        OPSBuilder.allOPS.put("GREATERTHAN", OPS.GT);
        OPSBuilder.allOPS.put("GREATER_THAN", OPS.GT);
        OPSBuilder.allOPS.put("GREATER THAN", OPS.GT);
        OPSBuilder.allOPS.put(">=", OPS.GE);
        OPSBuilder.allOPS.put("GE", OPS.GE);
        OPSBuilder.allOPS.put("EQUALORGREATERTHAN", OPS.GE);
        OPSBuilder.allOPS.put("GREATERTHANOREQUAL", OPS.GE);
        OPSBuilder.allOPS.put("EQUAL_OR_GREATER_THAN", OPS.GE);
        OPSBuilder.allOPS.put("GREATER_THAN_OR_EQUAL", OPS.GE);
        OPSBuilder.allOPS.put("EQUAL OR GREATER THAN", OPS.GE);
        OPSBuilder.allOPS.put("GREATER THAN OR EQUAL", OPS.GE);
        OPSBuilder.allOPS.put("<", OPS.LT);
        OPSBuilder.allOPS.put("LT", OPS.LT);
        OPSBuilder.allOPS.put("LESSTHAN", OPS.LT);
        OPSBuilder.allOPS.put("LESS_THAN", OPS.LT);
        OPSBuilder.allOPS.put("LESS THAN", OPS.LT);
        OPSBuilder.allOPS.put("<=", OPS.LE);
        OPSBuilder.allOPS.put("LE", OPS.LE);
        OPSBuilder.allOPS.put("EQUALORLESSTHAN", OPS.LE);
        OPSBuilder.allOPS.put("LESSTHANOREQUAL", OPS.LE);
        OPSBuilder.allOPS.put("EQUAL_OR_LESS_THAN", OPS.LE);
        OPSBuilder.allOPS.put("LESS_THAN_OR_EQUAL", OPS.LE);
        OPSBuilder.allOPS.put("EQUAL OR LESS THAN", OPS.LE);
        OPSBuilder.allOPS.put("LESS THAN OR EQUAL", OPS.LE);
        OPSBuilder.allOPS.put("!=", OPS.NOT_EQUAL);
        OPSBuilder.allOPS.put("<>", OPS.NOT_EQUAL);
        OPSBuilder.allOPS.put("NE", OPS.NOT_EQUAL);
        OPSBuilder.allOPS.put("NOT_EQUAL", OPS.NOT_EQUAL);
        OPSBuilder.allOPS.put("NOT EQUAL", OPS.NOT_EQUAL);
        OPSBuilder.allOPS.put("IN", OPS.IN);
        OPSBuilder.allOPS.put("@=", OPS.IN);
        OPSBuilder.allOPS.put("NOT_IN", OPS.NOT_IN);
        OPSBuilder.allOPS.put("NOT IN", OPS.NOT_IN);
        OPSBuilder.allOPS.put("!@=", OPS.NOT_IN);
        OPSBuilder.allOPS.put("LIKE", OPS.LIKE);
        OPSBuilder.allOPS.put("RELIKE", OPS.RE_LIKE);
        OPSBuilder.allOPS.put("RE_LIKE", OPS.RE_LIKE);
        OPSBuilder.allOPS.put("REGULAR_LIKE", OPS.RE_LIKE);
        OPSBuilder.allOPS.put("REGULAR LIKE", OPS.RE_LIKE);
        OPSBuilder.allOPS.put("~=", OPS.RE_LIKE);
        OPSBuilder.allOPS.put("BETWEEN", OPS.BETWEEN);
    }
}
