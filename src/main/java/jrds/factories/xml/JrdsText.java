package jrds.factories.xml;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class JrdsText extends AbstractJrdsNode<Text> implements Text {
    
    public JrdsText(Text n) {
        super(n);
    }

    /**
     * @param arg0
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#appendData(java.lang.String)
     */
    public void appendData(String arg0) throws DOMException {
        getParent().appendData(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#deleteData(int, int)
     */
    public void deleteData(int arg0, int arg1) throws DOMException {
        getParent().deleteData(arg0, arg1);
    }

     /**
     * @return
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#getData()
     */
    public String getData() throws DOMException {
        return getParent().getData();
    }

    /**
     * @return
     * @see org.w3c.dom.CharacterData#getLength()
     */
    public int getLength() {
        return getParent().getLength();
    }

    /**
     * @return
     * @see org.w3c.dom.Text#getWholeText()
     */
    public String getWholeText() {
        return getParent().getWholeText();
    }

    /**
     * @param arg0
     * @param arg1
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#insertData(int, java.lang.String)
     */
    public void insertData(int arg0, String arg1) throws DOMException {
        getParent().insertData(arg0, arg1);
    }

    /**
     * @return
     * @see org.w3c.dom.Text#isElementContentWhitespace()
     */
    public boolean isElementContentWhitespace() {
        return getParent().isElementContentWhitespace();
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#replaceData(int, int, java.lang.String)
     */
    public void replaceData(int arg0, int arg1, String arg2)
            throws DOMException {
        getParent().replaceData(arg0, arg1, arg2);
    }

    /**
     * @param content
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Text#replaceWholeText(java.lang.String)
     */
    public Text replaceWholeText(String content) throws DOMException {
        return getParent().replaceWholeText(content);
    }

    /**
     * @param arg0
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#setData(java.lang.String)
     */
    public void setData(String arg0) throws DOMException {
        getParent().setData(arg0);
    }

     /**
     * @param offset
     * @return
     * @throws DOMException
     * @see org.w3c.dom.Text#splitText(int)
     */
    public Text splitText(int offset) throws DOMException {
        return getParent().splitText(offset);
    }

    /**
     * @param arg0
     * @param arg1
     * @return
     * @throws DOMException
     * @see org.w3c.dom.CharacterData#substringData(int, int)
     */
    public String substringData(int arg0, int arg1) throws DOMException {
        return getParent().substringData(arg0, arg1);
    }

}
