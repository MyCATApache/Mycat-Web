package jrds.webapp.rpc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import jrds.Configuration;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

public class JrdsRequestProcessorFactoryFactory extends RequestProcessorFactoryFactory.RequestSpecificProcessorFactoryFactory {

	/**
	 * This interface is used for handler class that need access to the global configuration objects
	 * of JRDS : jrds.HostsList and jrds.PropertiesManager
	 * 
	 * @author bacchell
	 *
	 */
	public interface InitializableRequestProcessor {
	}

	private final ServletConfig config;

	public JrdsRequestProcessorFactoryFactory(ServletConfig config) {
		super();
		this.config = config;
	}

	protected Configuration getConfig() {
		ServletContext ctxt = config.getServletContext();
		return (Configuration) ctxt.getAttribute(Configuration.class.getName());
	}

}
