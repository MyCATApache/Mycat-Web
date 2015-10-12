package jrds.factories.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class JrdsElement extends AbstractJrdsNode<Element> implements Element {

    private NamedNodeMap attrs = null;
    private JrdsElement nextOfTag = null;
    private Map<String, JrdsElement> firstChildbyTag = new HashMap<String, JrdsElement>();

    public JrdsElement(Element n) {
        super(n);
    }

    public JrdsElement addElement(String tag, String... attrs) {
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

    public JrdsElement addTextNode(String value) {
        Text textnode = getOwnerDocument().createTextNode(value);
        appendChild(textnode);
        return this;
    }

    public Map<String, String> attrMap() {
        if(! checkAttributes())
            return Collections.emptyMap();
        Map<String, String> retValues = new HashMap<String, String>(attrs.getLength());
        for(int i = 0; i < attrs.getLength(); i++) {
            Node attrNode = attrs.item(i);
            retValues.put(attrNode.getNodeName(), attrNode.getNodeValue());
        }
        return retValues;
    }

    private boolean checkAttributes() {
        if (getParent().getNodeType() != Node.ELEMENT_NODE)
            return false;
        if( ! getParent().hasAttributes())
            return false;
        if(attrs == null)
            attrs = getParent().getAttributes();
        return true;
    }

    /**
     * @param name
     * @return
     * @see org.w3c.dom.Element#getAttribute(java.lang.String)
     */
    public String getAttribute(String name) {
        String attribute = getParent().getAttribute(name);
        if("".equals(attribute))
            return null;
        return attribute;
    }

    /**
     * @param namespaceURI
     * @param localName
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#getAttributeNS(java.lang.String, java.lang.String)
     */
    public String getAttributeNS(String namespaceURI, String localName)
            throws DOMException {
        return getParent().getAttributeNS(namespaceURI, localName);
    }

    /**
     * @param name
     * @return
     * @see org.w3c.dom.Element#getAttributeNode(java.lang.String)
     */
    public Attr getAttributeNode(String name) {
        return getParent().getAttributeNode(name);
    }

    /**
     * @param namespaceURI
     * @param localName
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#getAttributeNodeNS(java.lang.String, java.lang.String)
     */
    public Attr getAttributeNodeNS(String namespaceURI, String localName)
            throws DOMException {
        return getParent().getAttributeNodeNS(namespaceURI, localName);
    }

    /**
     * @param name
     * @return
     * @see org.w3c.dom.Element#getElementsByTagName(java.lang.String)
     */
    public NodeList getElementsByTagName(String name) {
        return getParent().getElementsByTagName(name);
    }

    public JrdsElement getElementbyName(String name) {
        NodeList childs = getParent().getChildNodes();
        if(name == null || "".equals(name.trim()))
            return null;
        if(firstChildbyTag.containsKey(name))
            return firstChildbyTag.get(name);
        for(int i=0; i < childs.getLength(); i++) {
            Node n = childs.item(i);
            if(n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String nodeName = n.getNodeName();
            if( ! firstChildbyTag.containsKey(nodeName))
                firstChildbyTag.put(nodeName, (JrdsElement) AbstractJrdsNode.build(n));
            if(n.getNodeName().equals(name)) {
                return new JrdsElement((Element) n);
            }
        }
        return null;
    }

    public Iterable<JrdsElement> getChildElementsByName(final String name) {
        final Iterator<JrdsElement> iter =  new Iterator<JrdsElement>() {
            JrdsElement curs = getElementbyName(name);
            public boolean hasNext() {
                if(curs == null)
                    return false;
                Node nextnode = curs.getNextSibling();
                while(nextnode != null) {
                    if(nextnode.getNodeType() == Node.ELEMENT_NODE) {
                        if(nextnode.getNodeName().equals(name)) {
                            curs.nextOfTag = AbstractJrdsNode.build(nextnode);
                            break;
                        }
                    }
                    nextnode = nextnode.getNextSibling();
                }
                return true;
            }
            public JrdsElement next() {
                JrdsElement step = curs;
                curs = curs.nextOfTag;
                return step;
            }
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove in a JrdsNode");
            }
        };
        return new Iterable<JrdsElement>() {
            public Iterator<JrdsElement> iterator() {
                return iter;
            }
        };

    }

    public List<JrdsElement> getChildElements() {
        List<JrdsElement> elems = new ArrayList<JrdsElement>();
        for(AbstractJrdsNode<?> n: getChildNodesIterator()) {
            if(n.getNodeType() == Node.ELEMENT_NODE)
                elems.add(new JrdsElement((Element) n));
        }
        return elems;
    }

    /**
     * @param namespaceURI
     * @param localName
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#getElementsByTagNameNS(java.lang.String, java.lang.String)
     */
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
            throws DOMException {
        return getParent().getElementsByTagNameNS(namespaceURI, localName);
    }


    /**
     * @return
     * @see org.w3c.dom.Element#getSchemaTypeInfo()
     */
    public TypeInfo getSchemaTypeInfo() {
        return getParent().getSchemaTypeInfo();
    }

    /**
     * @return
     * @see org.w3c.dom.Element#getTagName()
     */
    public String getTagName() {
        return getParent().getTagName();
    }

    /**
     * @param name
     * @return
     * @see org.w3c.dom.Element#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String name) {
        return getParent().hasAttribute(name);
    }

    /**
     * @param namespaceURI
     * @param localName
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#hasAttributeNS(java.lang.String, java.lang.String)
     */
    public boolean hasAttributeNS(String namespaceURI, String localName)
            throws DOMException {
        return getParent().hasAttributeNS(namespaceURI, localName);
    }

    /**
     * @param name
     * @throws DOMException
     * @see org.w3c.dom.Element#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name) throws DOMException {
        getParent().removeAttribute(name);
    }

    /**
     * @param namespaceURI
     * @param localName
     * @throws DOMException
     * @see org.w3c.dom.Element#removeAttributeNS(java.lang.String, java.lang.String)
     */
    public void removeAttributeNS(String namespaceURI, String localName)
            throws DOMException {
        getParent().removeAttributeNS(namespaceURI, localName);
    }

    /**
     * @param oldAttr
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr)
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        return getParent().removeAttributeNode(oldAttr);
    }

    /**
     * @param name
     * @param value
     * @throws DOMException
     * @see org.w3c.dom.Element#setAttribute(java.lang.String, java.lang.String)
     */
    public void setAttribute(String name, String value) throws DOMException {
        getParent().setAttribute(name, value);
    }

    /**
     * @param namespaceURI
     * @param qualifiedName
     * @param value
     * @throws DOMException
     * @see org.w3c.dom.Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setAttributeNS(String namespaceURI, String qualifiedName,
            String value) throws DOMException {
        getParent().setAttributeNS(namespaceURI, qualifiedName, value);
    }

    /**
     * @param newAttr
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr)
     */
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return getParent().setAttributeNode(newAttr);
    }

    /**
     * @param newAttr
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr)
     */
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        return getParent().setAttributeNodeNS(newAttr);
    }

    /**
     * @param name
     * @param isId
     * @throws DOMException
     * @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean)
     */
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        getParent().setIdAttribute(name, isId);
    }

    /**
     * @param namespaceURI
     * @param localName
     * @param isId
     * @throws DOMException
     * @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean)
     */
    public void setIdAttributeNS(String namespaceURI, String localName,
            boolean isId) throws DOMException {
        getParent().setIdAttributeNS(namespaceURI, localName, isId);
    }

    /**
     * @param idAttr
     * @param isId
     * @throws DOMException
     * @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean)
     */
    public void setIdAttributeNode(Attr idAttr, boolean isId)
            throws DOMException {
        getParent().setIdAttributeNode(idAttr, isId);
    }

}
