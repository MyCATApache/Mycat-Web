package jrds.factories.xml;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.logging.log4j.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class AbstractJrdsNode<NodeType extends Node> implements Node {
    static final private Logger logger = LogManager.getLogger(AbstractJrdsNode.class);

    private final NodeType parent;

    /**
     * Wrap a DOM node object with an enhanced Jrds Node object
     * If the null node is given, a null value is returned
     * @param n A node
     * @return a jrds node object
     */
    @SuppressWarnings("unchecked")
    public static <N extends AbstractJrdsNode<?>> N build(Node n) {
        if(n == null)
            return null;
        Class<?> c = n.getClass();
        if(AbstractJrdsNode.class.isAssignableFrom(c))
            return (N) n;

        if(n.getNodeType() == Node.ELEMENT_NODE)
            return (N) new JrdsElement((Element) n);
        if(n.getNodeType() == Node.DOCUMENT_NODE)
            return (N) new JrdsDocument((Document) n);
        if(n.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
            return (N) new JrdsNode(n);
        if(n.getNodeType() == Node.TEXT_NODE)
            return (N) new JrdsNode(n);
        else {
            logger.warn("Anonymous node created: " + n.getNodeType());
            return (N) new JrdsNode(n);
        }
    }

    protected AbstractJrdsNode(NodeType n){
        if(n == null)
            throw new NullPointerException("The parent node is null");
        this.parent = n;
    }

    public final NodeType getParent() {
        return parent;
    }

    public <T extends AbstractJrdsNode<?>> T findByPath(String xpathString) {
        try {
            XPathExpression xpath = CompiledXPath.get(xpathString);
            return AbstractJrdsNode.build((Node) xpath.evaluate(parent, XPathConstants.NODE));
        } catch (XPathExpressionException e) {
            throw new RuntimeException("xpath evaluate failed", e);
        }
    }

    /**
     * @param newChild
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)
     */
    public Node appendChild(Node newChild) throws DOMException {
        return parent.appendChild(newChild);
    }

    /**
     * @param deep
     * @return
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean deep) {
        return parent.cloneNode(deep);
    }

    /**
     * @param other
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
     */
    public short compareDocumentPosition(Node other) throws DOMException {
        return parent.compareDocumentPosition(other);
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getAttributes()
     */
    public NamedNodeMap getAttributes() {
        return parent.getAttributes();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getBaseURI()
     */
    public String getBaseURI() {
        return parent.getBaseURI();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        return parent.getChildNodes();
    }

    public NodeListIterator<AbstractJrdsNode<?>> getChildNodesIterator() {
        return new NodeListIterator<AbstractJrdsNode<?>>(parent.getChildNodes());
    }

    /**
     * @param feature
     * @param version
     * @return
     * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
     */
    public Object getFeature(String feature, String version) {
        return parent.getFeature(feature, version);
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getFirstChild()
     */
    public Node getFirstChild() {
        return parent.getFirstChild();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getLastChild()
     */
    public Node getLastChild() {
        return parent.getLastChild();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getLocalName()
     */
    public String getLocalName() {
        return parent.getLocalName();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        return parent.getNamespaceURI();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getNextSibling()
     */
    public Node getNextSibling() {
        return parent.getNextSibling();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return parent.getNodeName();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return parent.getNodeType();
    }

    /**
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return parent.getNodeValue();
    }

    /**
     * If it's a document, it return itself
     * @return
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    public Document getOwnerDocument() {
        if(getNodeType() == Node.DOCUMENT_NODE)
            return (Document) this;
        return new JrdsDocument(parent.getOwnerDocument());
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getParentNode()
     */
    public Node getParentNode() {
        return parent.getParentNode();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getPrefix()
     */
    public String getPrefix() {
        return parent.getPrefix();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    public Node getPreviousSibling() {
        return parent.getPreviousSibling();
    }

    /**
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#getTextContent()
     */
    public String getTextContent() throws DOMException {
        return parent.getTextContent();
    }

    /**
     * @param key
     * @return
     * @see org.w3c.dom.Node#getUserData(java.lang.String)
     */
    public Object getUserData(String key) {
        return parent.getUserData(key);
    }

    /**
     * @return
     * @see org.w3c.dom.Node#hasAttributes()
     */
    public boolean hasAttributes() {
        return parent.hasAttributes();
    }

    /**
     * @return
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    public boolean hasChildNodes() {
        return parent.hasChildNodes();
    }

    /**
     * @param newChild
     * @param refChild
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return parent.insertBefore(newChild, refChild);
    }

    /**
     * @param namespaceURI
     * @return
     * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
     */
    public boolean isDefaultNamespace(String namespaceURI) {
        return parent.isDefaultNamespace(namespaceURI);
    }

    /**
     * @param arg
     * @return
     * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
     */
    public boolean isEqualNode(Node arg) {
        return parent.isEqualNode(arg);
    }

    /**
     * @param other
     * @return
     * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node)
     */
    public boolean isSameNode(Node other) {
        return parent.isSameNode(other);
    }

    /**
     * @param feature
     * @param version
     * @return
     * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String)
     */
    public boolean isSupported(String feature, String version) {
        return parent.isSupported(feature, version);
    }

    /**
     * @param prefix
     * @return
     * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
     */
    public String lookupNamespaceURI(String prefix) {
        return parent.lookupNamespaceURI(prefix);
    }

    /**
     * @param namespaceURI
     * @return
     * @see org.w3c.dom.Node#lookupPrefix(java.lang.String)
     */
    public String lookupPrefix(String namespaceURI) {
        return parent.lookupPrefix(namespaceURI);
    }

    /**
     * 
     * @see org.w3c.dom.Node#normalize()
     */
    public void normalize() {
        parent.normalize();
    }

    /**
     * @param oldChild
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node)
     */
    public Node removeChild(Node oldChild) throws DOMException {
        return parent.removeChild(oldChild);
    }

    /**
     * @param newChild
     * @param oldChild
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return parent.replaceChild(newChild, oldChild);
    }

    /**
     * @param nodeValue
     * @throws DOMException
     * @see org.w3c.dom.Node#setNodeValue(java.lang.String)
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        parent.setNodeValue(nodeValue);
    }

    /**
     * @param prefix
     * @throws DOMException
     * @see org.w3c.dom.Node#setPrefix(java.lang.String)
     */
    public void setPrefix(String prefix) throws DOMException {
        parent.setPrefix(prefix);
    }

    /**
     * @param textContent
     * @throws DOMException
     * @see org.w3c.dom.Node#setTextContent(java.lang.String)
     */
    public void setTextContent(String textContent) throws DOMException {
        parent.setTextContent(textContent);
    }

    /**
     * @param key
     * @param data
     * @param handler
     * @return
     * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
     */
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return parent.setUserData(key, data, handler);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return parent.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        Class<?> c = arg0.getClass();
        if(AbstractJrdsNode.class.isAssignableFrom(c)) {
            JrdsNode otherNode = (JrdsNode) arg0;
            return parent.equals(otherNode.getParent());
        }
        else
            return parent.equals(arg0);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return parent.hashCode();
    }

}
