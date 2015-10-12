package jrds.factories.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class JrdsDocument extends AbstractJrdsNode<Document> implements Document {

    Document parent;
    
    public JrdsDocument(Document n) {
        super(n);
    }
    
    public JrdsElement doRootElement(String tag, String... attrs) {
        Element newelement = getOwnerDocument().createElement(tag);
        appendChild(newelement);
        for(String attr: attrs) {
            int pos = attr.indexOf('=');
            String key = attr.substring(0, pos);
            String value = attr.substring(pos +  1);
            newelement.setAttribute(key, value);
        }
        return new JrdsElement(newelement);

    }
    
    /**
     * @param arg0
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#adoptNode(org.w3c.dom.Node)
     */
    public Node adoptNode(Node arg0) throws DOMException {
        return getParent().adoptNode(arg0);
    }

    /**
     * @param arg0
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createAttribute(java.lang.String)
     */
    public Attr createAttribute(String arg0) throws DOMException {
        return getParent().createAttribute(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createAttributeNS(java.lang.String, java.lang.String)
     */
    public Attr createAttributeNS(String arg0, String arg1) throws DOMException {
        return getParent().createAttributeNS(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createCDATASection(java.lang.String)
     */
    public CDATASection createCDATASection(String arg0) throws DOMException {
        return getParent().createCDATASection(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see org.w3c.dom.Document#createComment(java.lang.String)
     */
    public Comment createComment(String arg0) {
        return getParent().createComment(arg0);
    }

    /**
     * @return
     * @see org.w3c.dom.Document#createDocumentFragment()
     */
    public DocumentFragment createDocumentFragment() {
        return getParent().createDocumentFragment();
    }

    /**
     * @param arg0
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createElement(java.lang.String)
     */
    public Element createElement(String arg0) throws DOMException {
        return getParent().createElement(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)
     */
    public Element createElementNS(String arg0, String arg1)
            throws DOMException {
        return getParent().createElementNS(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createEntityReference(java.lang.String)
     */
    public EntityReference createEntityReference(String arg0)
            throws DOMException {
        return getParent().createEntityReference(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#createProcessingInstruction(java.lang.String, java.lang.String)
     */
    public ProcessingInstruction createProcessingInstruction(String arg0,
            String arg1) throws DOMException {
        return getParent().createProcessingInstruction(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     * @see org.w3c.dom.Document#createTextNode(java.lang.String)
     */
    public Text createTextNode(String arg0) {
        return getParent().createTextNode(arg0);
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getDoctype()
     */
    public DocumentType getDoctype() {
        return getParent().getDoctype();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getDocumentElement()
     */
    public Element getDocumentElement() {
        return getParent().getDocumentElement();
    }
    
    public JrdsElement getRootElement() {
        return new JrdsElement(getParent().getDocumentElement());

    }

    /**
     * @return
     * @see org.w3c.dom.Document#getDocumentURI()
     */
    public String getDocumentURI() {
        return getParent().getDocumentURI();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getDomConfig()
     */
    public DOMConfiguration getDomConfig() {
        return getParent().getDomConfig();
    }

    /**
     * @param arg0
     * @return
     * @see org.w3c.dom.Document#getElementById(java.lang.String)
     */
    public Element getElementById(String arg0) {
        return getParent().getElementById(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see org.w3c.dom.Document#getElementsByTagName(java.lang.String)
     */
    public NodeList getElementsByTagName(String arg0) {
        return getParent().getElementsByTagName(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @see org.w3c.dom.Document#getElementsByTagNameNS(java.lang.String, java.lang.String)
     */
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return getParent().getElementsByTagNameNS(arg0, arg1);
    }


    /**
     * @return
     * @see org.w3c.dom.Document#getImplementation()
     */
    public DOMImplementation getImplementation() {
        return getParent().getImplementation();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getInputEncoding()
     */
    public String getInputEncoding() {
        return getParent().getInputEncoding();
    }
    /**
     * @return
     * @see org.w3c.dom.Document#getStrictErrorChecking()
     */
    public boolean getStrictErrorChecking() {
        return getParent().getStrictErrorChecking();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getXmlEncoding()
     */
    public String getXmlEncoding() {
        return getParent().getXmlEncoding();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getXmlStandalone()
     */
    public boolean getXmlStandalone() {
        return getParent().getXmlStandalone();
    }

    /**
     * @return
     * @see org.w3c.dom.Document#getXmlVersion()
     */
    public String getXmlVersion() {
        return getParent().getXmlVersion();
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#importNode(org.w3c.dom.Node, boolean)
     */
    public Node importNode(Node arg0, boolean arg1) throws DOMException {
        return getParent().importNode(arg0, arg1);
    }

    /**
     * 
     * @see org.w3c.dom.Document#normalizeDocument()
     */
    public void normalizeDocument() {
        getParent().normalizeDocument();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Document#renameNode(org.w3c.dom.Node, java.lang.String, java.lang.String)
     */
    public Node renameNode(Node arg0, String arg1, String arg2)
            throws DOMException {
        return getParent().renameNode(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @see org.w3c.dom.Document#setDocumentURI(java.lang.String)
     */
    public void setDocumentURI(String arg0) {
        getParent().setDocumentURI(arg0);
    }

    /**
     * @param nodeValue
     * @throws DOMException
     * @see org.w3c.dom.Node#setNodeValue(java.lang.String)
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        getParent().setNodeValue(nodeValue);
    }

    /**
     * @param arg0
     * @see org.w3c.dom.Document#setStrictErrorChecking(boolean)
     */
    public void setStrictErrorChecking(boolean arg0) {
        getParent().setStrictErrorChecking(arg0);
    }

    /**
     * @param arg0
     * @throws DOMException
     * @see org.w3c.dom.Document#setXmlStandalone(boolean)
     */
    public void setXmlStandalone(boolean arg0) throws DOMException {
        getParent().setXmlStandalone(arg0);
    }

    /**
     * @param arg0
     * @throws DOMException
     * @see org.w3c.dom.Document#setXmlVersion(java.lang.String)
     */
    public void setXmlVersion(String arg0) throws DOMException {
        getParent().setXmlVersion(arg0);
    }

}
