// 
// Decompiled by Procyon v0.5.30
// 

package com.ailk.aee.etl.e;

import com.ailk.aee.etl.o.MapRecord;
import com.ailk.aee.etl.job.IBusinessObject;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.ailk.aee.common.util.ExceptionUtils;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerFactory;
import java.io.File;
import org.w3c.dom.NodeList;
import org.apache.log4j.Logger;
import com.ailk.aee.common.annotation.cvsid.CVSID;

@CVSID("$Id: XSLExtractor.java 11039 2013-06-13 01:44:38Z xiezl $")
public class XSLExtractor extends AbstractExtractor
{
    private Logger log;
    private String xslFileName;
    private String srcFileName;
    private String outFileName;
    private String elementTagName;
    private NodeList tableinsertNodes;
    private int index;
    
    public XSLExtractor() {
        this.log = Logger.getLogger((Class)this.getClass());
        this.xslFileName = null;
        this.srcFileName = null;
        this.outFileName = null;
        this.elementTagName = "tableinsert";
        this.tableinsertNodes = null;
        this.index = 0;
    }
    
    @Override
    public void onJobStart() throws Exception {
        super.onJobStart();
        this.transform();
    }
    
    @Override
    public void onJobEnd() throws Exception {
        super.onJobEnd();
    }
    
    public void transform() throws Exception {
        File temp = null;
        if (this.srcFileName == null || this.srcFileName.trim().length() == 0) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u04aa\ufffd\ufffd\ufffd\ufffd\ufffdxml\ufffd\ufffd\ufffd\u013c\ufffd\ufffd\ufffd.");
        }
        temp = new File(this.srcFileName);
        if (!temp.exists() || !temp.isFile() || !temp.canRead()) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffdxml\ufffd\u013c\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd.");
        }
        if (this.xslFileName == null || this.xslFileName.trim().length() == 0) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffdxslFileName\ufffd\ufffd\ufffd\u013c\ufffd\ufffd\ufffd.");
        }
        temp = new File(this.xslFileName);
        if (!temp.exists() || !temp.isFile() || !temp.canRead()) {
            throw new Exception("xslFileName\ufffd\u013c\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd.");
        }
        if (this.outFileName == null || this.outFileName.trim().length() == 0) {
            throw new Exception("\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd\u013c\ufffd\ufffd\ufffd.");
        }
        temp = new File(this.outFileName);
        temp = temp.getParentFile();
        if (!temp.exists() || !temp.isDirectory()) {
            throw new Exception("\ufffd\ufffd\ufffd\u013f?\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd.");
        }
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer xsl_transformer = tFactory.newTransformer(new StreamSource(this.xslFileName));
        xsl_transformer.transform(new StreamSource(this.srcFileName), new StreamResult(this.outFileName));
        final File outFile = new File(this.outFileName);
        if (outFile.exists() && outFile.isFile()) {
            this.domXmlData2Map(outFile);
            return;
        }
        throw new Exception("create outFile failed.");
    }
    
    public void domXmlData2Map(final File f) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(f);
            final Element element = document.getDocumentElement();
            this.tableinsertNodes = element.getElementsByTagName(this.elementTagName);
        }
        catch (Exception e) {
            this.log.error((Object)ExceptionUtils.getExceptionStack(e));
        }
    }
    
    @Override
    public boolean hasNextObject() {
        return this.tableinsertNodes != null && this.tableinsertNodes.getLength() > this.index;
    }
    
    @Override
    public IBusinessObject nextObject() {
        final MapRecord mr = new MapRecord();
        final Element tableelement = (Element)this.tableinsertNodes.item(this.index);
        final String tableName = tableelement.getAttribute("tableName");
        mr.put("TABLE_NAME__", tableName);
        final NodeList columnNodes = tableelement.getChildNodes();
        for (int j = 0; j < columnNodes.getLength(); ++j) {
            if (columnNodes.item(j).getNodeType() == 1) {
                final Element columnElement = (Element)columnNodes.item(j);
                final String colName = columnElement.getNodeName();
                if (null == columnElement.getFirstChild()) {
                    mr.put(colName, "");
                }
                else {
                    final String firstChildNodeValue = columnElement.getFirstChild().getNodeValue();
                    mr.put(colName, firstChildNodeValue);
                }
            }
        }
        ++this.index;
        return mr;
    }
}
