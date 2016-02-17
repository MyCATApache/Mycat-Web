package jrds.factories.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.*;

public class CompiledXPath {
	static final private Logger logger = LogManager.getLogger(CompiledXPath.class);

	private static final XPath xpather = XPathFactory.newInstance().newXPath();
	private static final Map<String, XPathExpression> xpc = new HashMap<String, XPathExpression>();

	public static XPathExpression get(String xpath)	{
		XPathExpression e =  xpc.get(xpath);
		if(e == null) {
			logger.debug("Uncompiled xpath: " + xpath);
			try {
				e = xpather.compile(xpath);
				xpc.put(xpath, e);
			} catch (XPathExpressionException e1) {
				logger.error("invalid xpath:" + xpath);
				throw new RuntimeException("Invalid xpath " + xpath, e1);
			}
		}
		return e;
	}

}
