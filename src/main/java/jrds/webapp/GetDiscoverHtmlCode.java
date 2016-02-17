package jrds.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.*;

import jrds.Util;
import jrds.factories.xml.JrdsDocument;

public class GetDiscoverHtmlCode extends JrdsServlet {

    static final private Logger logger = LogManager.getLogger(GetDiscoverHtmlCode.class);

    private static final String CONTENT_TYPE = "application/xml";
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        DocumentBuilder dbuilder;
        try {
            dbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            JrdsDocument hostDom = new JrdsDocument(dbuilder.newDocument());
            hostDom.doRootElement("div");
            for(DiscoverAgent da: getHostsList().getDiscoverAgent()) {
                logger.debug(jrds.Util.delayedFormatString("Adding discover agent %s", da));
                da.doHtmlDiscoverFields(hostDom);
            }
            resp.setContentType(CONTENT_TYPE);

            Map<String, String> prop = new HashMap<String, String>(1);
            prop.put(OutputKeys.OMIT_XML_DECLARATION, "no");
            prop.put(OutputKeys.INDENT, "yes");
            prop.put("{http://xml.apache.org/xslt}indent-amount", "4");
            Util.serialize(hostDom, resp.getOutputStream(), null, prop);
        } catch (ParserConfigurationException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Parser configuration error");
        } catch (TransformerException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transformer exception error");

        }    
    }

}
