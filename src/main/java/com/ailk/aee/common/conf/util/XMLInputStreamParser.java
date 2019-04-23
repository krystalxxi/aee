// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.common.conf.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.FileInputStream;
import java.io.File;
import com.ailk.aee.common.util.SystemUtils;
import com.ailk.aee.common.util.text.StrSubstitutor;
import com.ailk.aee.common.conf.MapTools;
import java.util.Iterator;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import com.ailk.aee.common.stringobject.ConverterCollections;
import com.ailk.aee.common.conf.Configuration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.ailk.aee.common.util.StringUtils;
import java.util.HashMap;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: XMLInputStreamParser.java 65097 2013-11-11 13:52:02Z huwl $")
public class XMLInputStreamParser implements IInputStreamParser
{
    private Map<String, ArrayList<String>> inheritInfo;
    
    public static Map<String, String> parseFile(final InputStream is) throws Exception {
        final XMLInputStreamParser loader = new XMLInputStreamParser();
        return loader.innerparseFile(is);
    }
    
    public static Map<String, String> parseString(final String s) throws Exception {
        final XMLInputStreamParser loader = new XMLInputStreamParser();
        return loader.innerparseFile(new SIS(s));
    }
    
    public XMLInputStreamParser() {
        this.inheritInfo = new HashMap<String, ArrayList<String>>();
    }
    
    public void addInheritInfo(final String s, final String v) {
        final String[] arr$;
        final String[] res = arr$ = StringUtils.split(v, ",");
        for (final String ss : arr$) {
            this.addInheritInfoSingle(s, ss);
        }
    }
    
    private void addInheritInfoSingle(final String s, final String v) {
        if (this.inheritInfo.containsKey(s)) {
            final ArrayList<String> al = this.inheritInfo.get(s);
            if (!al.contains(v)) {
                al.add(v);
            }
        }
        else {
            final ArrayList<String> al = new ArrayList<String>();
            al.add(v);
            this.inheritInfo.put(s, al);
        }
    }
    
    private String getAttributeValue(final Node n, final String attrName) {
        if (n == null) {
            return null;
        }
        final NamedNodeMap al = n.getAttributes();
        if (al == null) {
            return null;
        }
        if (al.getNamedItem(attrName) != null) {
            return al.getNamedItem(attrName).getTextContent();
        }
        return null;
    }
    
    private String getConfigurationAndTemp(final String name, final Map<String, String> m) {
        final String v = Configuration.getValue(name);
        if (v != null) {
            return v;
        }
        if (m != null && m.containsKey(name)) {
            return m.get(name);
        }
        return null;
    }
    
    private boolean getConfigurationAndTempBoolean(final String name, final Map<String, String> m) {
        final String v = this.getConfigurationAndTemp(name, m);
        if (v == null) {
            return false;
        }
        final boolean vb = (boolean)ConverterCollections.booleanConverter.wrapFromString(v);
        return vb;
    }
    
    public String getNamedAttribute(final Node n, final String attrName) {
        if (n == null) {
            return null;
        }
        final NamedNodeMap al = n.getAttributes();
        if (al.getNamedItem(attrName) != null) {
            return al.getNamedItem(attrName).getTextContent();
        }
        return null;
    }
    
    public String getNamedChildNode(final Node n, final String name) {
        String v = null;
        final NodeList nl = n.getChildNodes();
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); ++i) {
                if (nl.item(i).getNodeName().equals(name)) {
                    v = nl.item(i).getTextContent();
                }
            }
        }
        return v;
    }
    
    public Map<String, String> innerparseFile(final InputStream is) throws Exception {
        final Map<String, String> m = new HashMap<String, String>();
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(is);
            String rootName = "";
            try {
                rootName = doc.getChildNodes().item(0).getNodeName();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (rootName.equals("properties") || rootName.equals("props")) {
                final Node root = doc.getChildNodes().item(0);
                this.readProperties(m, root);
            }
            else {
                this.wrapNode(m, "", doc);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            throw e2;
        }
        if (this.inheritInfo.size() > 0) {
            this.processInheritInfo(m);
        }
        return m;
    }
    
    @Override
    public Map<String, String> parser(final InputStream is) throws Exception {
        return parseFile(is);
    }
    
    private void processInheritInfo(final Map<String, String> m) {
        for (final Map.Entry<String, ArrayList<String>> p : this.inheritInfo.entrySet()) {
            this.processInheritInfoSub(m, p.getKey(), 0);
        }
    }
    
    private void processInheritInfoSub(final Map<String, String> m, final String key, final int iloops) {
        final ArrayList<String> froms = this.inheritInfo.get(key);
        final Map<String, String> mapNewTo = new HashMap<String, String>();
        for (final String from : froms) {
            if (this.inheritInfo.containsKey(from)) {
                if (iloops < 64) {
                    this.processInheritInfoSub(m, from, iloops + 1);
                }
                else {
                    System.out.println("inherit level over 64,may cycle inherit !,ignore!");
                }
            }
            final Map<String, String> mapfrom = MapTools.getSub(m, from);
            for (final Map.Entry<String, String> fromv : mapfrom.entrySet()) {
                if (!m.containsKey(key + "." + fromv.getKey())) {
                    mapNewTo.put(key + "." + fromv.getKey(), fromv.getValue());
                }
            }
        }
        m.putAll(mapNewTo);
    }
    
    private void readProperties(final Map<String, String> m, final Node doc) {
        this.readProperties(m, doc, "");
    }
    
    private void readProperties(final Map<String, String> m, final Node doc, final String prefix) {
        final NodeList nl = doc.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            if (nl.item(i).getNodeType() == 1) {
                this.readProperty(m, nl.item(i), prefix);
            }
        }
    }
    
    private void readProperty(final Map<String, String> m, final Node n, final String prefix) {
        if (n.getNodeName().equals("prop") || n.getNodeName().equals("property")) {
            String name = "";
            String value = "";
            final String vset = this.getNamedAttribute(n, "usedWhenSet");
            if (vset != null) {
                final String vtemp = this.getConfigurationAndTemp(vset.trim(), m);
                if (vtemp == null) {
                    return;
                }
            }
            final String vnotset = this.getNamedAttribute(n, "usedWhenNotSet");
            if (vnotset != null) {
                final String vtemp2 = this.getConfigurationAndTemp(vnotset.trim(), m);
                if (vtemp2 != null) {
                    return;
                }
            }
            final String vsettrue = this.getNamedAttribute(n, "usedWhenSetTrue");
            if (vsettrue != null) {
                final String vtemp3 = this.getConfigurationAndTemp(vsettrue.trim(), m);
                if (vtemp3 == null) {
                    return;
                }
                final boolean b = this.getConfigurationAndTempBoolean(vsettrue.trim(), m);
                if (!b) {
                    return;
                }
            }
            final String vsetfalse = this.getNamedAttribute(n, "usedWhenSetFalse");
            if (vsetfalse != null) {
                final String vtemp4 = this.getConfigurationAndTemp(vsetfalse.trim(), m);
                if (vtemp4 == null) {
                    return;
                }
                final boolean b2 = this.getConfigurationAndTempBoolean(vsetfalse.trim(), m);
                if (b2) {
                    return;
                }
            }
            String vn = this.getNamedAttribute(n, "usedWhenOsNameLike");
            if (vn != null) {
                if (!System.getProperty("os.name").toUpperCase().startsWith(vn.trim().toUpperCase())) {
                    return;
                }
            }
            vn = this.getNamedAttribute(n, "usedWhenOsNameNotLike");
            if (vn != null) {
                if (System.getProperty("os.name").toUpperCase().startsWith(vn.trim().toUpperCase())) {
                    return;
                }
            }
            String v = this.getNamedAttribute(n, "name");
            if (v == null) {
                return;
            }
            name = v;
            v = this.getNamedAttribute(n, "value");
            if (v == null) {
                v = n.getTextContent().trim();
            }
            if (v == null || v.length() == 0) {
                v = this.getNamedChildNode(n, "value");
            }
            if (v == null) {
                v = "";
            }
            value = v;
            if (prefix != null && prefix.length() > 0) {
                if (prefix.endsWith(".")) {
                    name = prefix + name;
                }
                else {
                    name = prefix + "." + name;
                }
            }
            m.put(name, value);
        }
    }
    
    private void readPropertyFile(final Map<String, String> m, final Node n) {
        final String nname = n.getNodeName();
        if (nname.equalsIgnoreCase("propFile")) {
            String filePath = n.getTextContent();
            if (filePath.indexOf("${") >= 0) {
                filePath = StrSubstitutor.replace(filePath, m);
            }
            if (filePath.indexOf("${") >= 0) {
                filePath = StrSubstitutor.replace(filePath, System.getProperties());
            }
            if (filePath.indexOf("${") >= 0) {
                filePath = StrSubstitutor.replace(filePath, System.getenv());
            }
            if (filePath.indexOf("${") >= 0) {
                filePath = Configuration.getInstance().applyVariable(filePath);
            }
            boolean isAbsPath = false;
            if (filePath.startsWith("/")) {
                isAbsPath = true;
            }
            else if (SystemUtils.isWindows() && filePath.charAt(1) == ':') {
                isAbsPath = true;
            }
            if (!isAbsPath) {
                final String b = SystemUtils.getCurrentPath();
                if (!b.equals("")) {
                    filePath = b + File.separator + filePath;
                }
            }
            try {
                Map<String, String> m2 = null;
                try {
                    m2 = parseFile(new FileInputStream(new File(filePath)));
                }
                catch (Exception e) {
                    throw new Exception("\ufffd\u06b6\ufffd\u0221" + filePath + "\ufffd\ufffd\ufffd\ufffdXML\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u0634\ufffd\ufffd\ufffd" + e.getMessage());
                }
                if (m2 != null) {
                    m.putAll(m2);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private void readPropertyFiles(final Map<String, String> m, final Node n) {
        final NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            if (nl.item(i).getNodeType() == 1) {
                this.readPropertyFile(m, nl.item(i));
            }
        }
    }
    
    private void wrapNode(final Map<String, String> m, final String parentN, final Node n) {
        if (n.getNodeName().equalsIgnoreCase("properties") || n.getNodeName().equalsIgnoreCase("props")) {
            this.readProperties(m, n, "");
            return;
        }
        if (n.getNodeName().equalsIgnoreCase("prop") || n.getNodeName().equalsIgnoreCase("property")) {
            this.readProperty(m, n, parentN);
            return;
        }
        if (n.getNodeName().equalsIgnoreCase("propertiesFiles") || n.getNodeName().equalsIgnoreCase("propFiles") || n.getNodeName().equalsIgnoreCase("propertyFiles") || n.getNodeName().equalsIgnoreCase("includes")) {
            this.readPropertyFiles(m, n);
            return;
        }
        if (n.getNodeName().equals("#cdata-section")) {
            final String s = n.getTextContent().trim();
            if (s != null && s.length() > 0) {
                m.put(parentN, s);
            }
        }
        if (n.getNodeType() == 3) {
            final String s = n.getTextContent().trim();
            if (s != null && s.length() > 0) {
                m.put(parentN, s);
            }
        }
        else {
            final String nv = this.getAttributeValue(n, "value");
            final String nn = this.getAttributeValue(n, "name");
            String nodeNameSpec = n.getNodeName();
            if (nn != null) {
                nodeNameSpec = nn;
            }
            if (nv != null) {
                if (parentN.equals("")) {
                    m.put(nodeNameSpec, nv);
                }
                else {
                    m.put(parentN + "." + nodeNameSpec, nv);
                }
            }
            final NodeList nl = n.getChildNodes();
            final String inherit = this.getAttributeValue(n, "inherit");
            if (inherit != null) {
                if (parentN.equals("")) {
                    this.addInheritInfo(nodeNameSpec, inherit);
                }
                else {
                    this.addInheritInfo(parentN + "." + nodeNameSpec, inherit);
                }
            }
            boolean isNeedChild = true;
            final String vsetTrue = this.getAttributeValue(n, "usedWhenSetTrue");
            if (vsetTrue != null) {
                final boolean v = this.getConfigurationAndTempBoolean(vsetTrue.trim(), m);
                if (!v) {
                    isNeedChild = false;
                }
            }
            final String vsetFalse = this.getAttributeValue(n, "usedWhenSetFalse");
            if (vsetFalse != null) {
                final boolean v2 = this.getConfigurationAndTempBoolean(vsetFalse.trim(), m);
                if (v2) {
                    isNeedChild = false;
                }
            }
            final String vset = this.getAttributeValue(n, "usedWhenSet");
            if (vset != null) {
                final String v3 = this.getConfigurationAndTemp(vset.trim(), m);
                if (v3 == null) {
                    isNeedChild = false;
                }
            }
            final String vnotset = this.getAttributeValue(n, "usedWhenNotSet");
            if (vnotset != null) {
                final String v4 = this.getConfigurationAndTemp(vnotset.trim(), m);
                if (v4 != null) {
                    isNeedChild = false;
                }
            }
            final String vsetvalue = this.getAttributeValue(n, "usedWhenSetValue");
            if (vsetvalue != null) {
                final String[] vs = StringUtils.split(vsetvalue, ":");
                if (vs != null && vs.length == 2) {
                    final String v5 = this.getConfigurationAndTemp(vs[0].trim(), m);
                    if (v5 == null || !v5.equalsIgnoreCase(vs[1])) {
                        isNeedChild = false;
                    }
                }
            }
            if (isNeedChild) {
                for (int i = 0; i < nl.getLength(); ++i) {
                    if (n.getParentNode() == null) {
                        this.wrapNode(m, "", nl.item(i));
                    }
                    else if (parentN.equals("")) {
                        this.wrapNode(m, nodeNameSpec, nl.item(i));
                    }
                    else {
                        this.wrapNode(m, parentN + "." + nodeNameSpec, nl.item(i));
                    }
                }
            }
        }
    }
    
    static final class SIS extends InputStream
    {
        private StringReader sr;
        
        public SIS(final String s) {
            this.sr = null;
            this.sr = new StringReader(s);
        }
        
        @Override
        public int read() throws IOException {
            return this.sr.read();
        }
    }
}
