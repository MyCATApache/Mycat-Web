package jrds.starter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author bacchell
 * A provider is used for XML to solve multi thread problems
 * 
 * As each one is parsed by one and only one thread, it we used one provider by host,
 * we can simply solved the concurency problem and reuse factory and parser without too many risks
 * 
 */
public class XmlProvider extends Starter {
    private ThreadLocal<DocumentBuilder> localDocumentBuilder = new ThreadLocal<DocumentBuilder>(){
        @Override
        protected DocumentBuilder initialValue() {
            DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            instance.setIgnoringComments(true);
            instance.setValidating(false);
            try {
                return instance.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                log(Level.FATAL, e, "No Document builder available");
                return null;
            }
        }
    };
    private ThreadLocal<XPath> localXpath = new ThreadLocal<XPath>() {
        @Override
        protected XPath initialValue() {
            return XPathFactory.newInstance().newXPath();
        }        
    };

    public long findUptimeByDate(Document d, String startTimePath, String currentTimePath, DateFormat pattern) {
        XPath  xpather = localXpath.get();
        try {
            Node startTimeNode = (Node) xpather.evaluate(startTimePath, d, XPathConstants.NODE);
            String startTimeString = startTimeNode.getTextContent();
            Date startTime = pattern.parse(startTimeString);
            Node currentTimeNode = (Node) xpather.evaluate(currentTimePath, d, XPathConstants.NODE);
            String currentTimeString = currentTimeNode.getTextContent();
            Date currentTime = pattern.parse(currentTimeString);
            return (currentTime.getTime() - startTime.getTime()) / 1000;
        } catch (XPathExpressionException e) {
            log(Level.ERROR, e, "Time not found");
        } catch (ParseException e) {
            log(Level.ERROR, e, "Date not parsed with pattern " + ((SimpleDateFormat) pattern).toPattern());
        }
        return 0;
    }

    public long findUptime(Document d, String upTimePath) {
        long uptime = 0;
        if(upTimePath == null) {
            log(Level.ERROR, "No xpath for the uptime for " + this);
            return 0;
        }
        try {
            XPath  xpather = localXpath.get();
            Node upTimeNode = (Node) xpather.evaluate(upTimePath, d, XPathConstants.NODE);
            if(upTimeNode != null) {
                log(Level.TRACE, "Will parse uptime: %s", upTimeNode.getTextContent());
                String dateString = upTimeNode.getTextContent();
                uptime = jrds.Util.parseStringNumber(dateString, 0L);
            }
            log(Level.ALL, "uptime is %d", uptime);
        } catch (XPathExpressionException e) {
            log(Level.ERROR, e, "Uptime not found");
        }
        return uptime;
    }

    public void fileFromXpaths(Document d, Set<String> xpaths, Map<String, Number> oldMap) {
        XPath xpather = localXpath.get();
        for(String xpath: xpaths) {
            try {
                log(Level.TRACE, "Will search the xpath \"%s\"", xpath);
                if(xpath == null || "".equals(xpath))
                    continue;
                Node n = (Node)xpather.evaluate(xpath, d, XPathConstants.NODE);
                double value = 0;
                if(n != null) {
                    log(Level.TRACE, "%s", n);
                    value = jrds.Util.parseStringNumber(n.getTextContent(), Double.NaN).doubleValue();
                    oldMap.put(xpath, Double.valueOf(value));
                }
            } catch (XPathExpressionException e) {
                log(Level.ERROR, "Invalid XPATH : " + xpath + " for " + this);
            } catch (NumberFormatException e) {
                log(Level.WARN, e, "value read from %s  not parsable", xpath);
            }
        }
        log(Level.TRACE, "Values found: %s", oldMap);
        return;
    }

    public Document getDocument(InputSource stream) {
        DocumentBuilder dbuilder = localDocumentBuilder.get();
        Document d = null;
        log(Level.TRACE, "%s %s %s started %s@%s", stream, dbuilder, isStarted(), getClass().getName(), Integer.toHexString(hashCode()));
        try {
            try {
                dbuilder.reset();
            } catch (UnsupportedOperationException e) {
            }
            d = dbuilder.parse(stream);
            log(Level.TRACE, "just parsed a %s", d.getDocumentElement().getTagName());
        } catch (SAXException e) {
            log(Level.ERROR, e, "Invalid XML");
        } catch (IOException e) {
            log(Level.ERROR, e, "IO Exception getting values");
        }
        return d;
    }

    public Document getDocument(InputStream stream) {
        return getDocument(new InputSource(stream));
    }

    public Document getDocument(Reader stream) {
        return getDocument(new InputSource(stream));
    }

    /**
     * Used to get an empty document
     * @return an empty document
     */
    public Document getDocument() {
        DocumentBuilder dbuilder = localDocumentBuilder.get();
        try {
            dbuilder.reset();
        } catch (UnsupportedOperationException e) {
        }
        return dbuilder.newDocument();
    }

    public NodeList getNodeList(Document d, String xpath) throws XPathExpressionException {
        return  (NodeList) localXpath.get().evaluate(xpath, d, XPathConstants.NODESET);
    }

    public Node getNode(Document d, String xpath) throws XPathExpressionException {
        return  (Node) localXpath.get().evaluate(xpath, d, XPathConstants.NODE);
    }

}
