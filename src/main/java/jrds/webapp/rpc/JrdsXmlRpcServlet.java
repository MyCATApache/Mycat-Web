package jrds.webapp.rpc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.Configuration;
import jrds.HostsList;
import jrds.PropertiesManager;
import jrds.webapp.ParamsBean;

import org.apache.logging.log4j.*;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.XmlRpcErrorLogger;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

public class JrdsXmlRpcServlet extends XmlRpcServlet {
	static final private Logger logger = LogManager.getLogger(JrdsXmlRpcServlet.class);
	Class<?>[] handlersClass = new Class<?>[] {ConfigurationInformations.class};

	private RequestProcessorFactoryFactory factoryfactory;

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		factoryfactory = new JrdsRequestProcessorFactoryFactory(config);
		super.init(config);
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest req,
			HttpServletResponse res) throws IOException, ServletException {
		HostsList hl = getHostsList();

		if(getPropertiesManager().security) {
			ParamsBean p = new ParamsBean();
			p.readAuthorization(req, hl);
			boolean allowed = getPropertiesManager().adminACL.check(p);
			if(! allowed) {
				res.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}

		super.doPost(req, res);
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#getRequestProcessorFactoryFactory()
	 */
	@Override
	public RequestProcessorFactoryFactory getRequestProcessorFactoryFactory() {
		return factoryfactory;
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#log(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(String pMessage, Throwable pThrowable) {
		logger.error(pMessage, pThrowable);
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#log(java.lang.String)
	 */
	@Override
	public void log(String pMessage) {
		logger.error(pMessage);
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#newXmlRpcHandlerMapping()
	 */
	@Override
	protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
	throws XmlRpcException {
		final PropertyHandlerMapping map = new PropertyHandlerMapping();
		final Map<String, String> handlerRole = new HashMap<String, String>();
		map.setRequestProcessorFactoryFactory(factoryfactory);

		map.load(getPropertiesManager().extensionClassLoader, Collections.emptyMap());

		for(Class<?> c: handlersClass) {
			try {

				String handlerName = (String) c.getField("REMOTENAME").get(null);
				map.addHandler(handlerName, c);
				logger.trace("Annotation:" + Arrays.asList(c.getAnnotations()));
				logger.trace("remote name: " + handlerName);
				
				for(Method m: c.getMethods()) {
					logger.trace("method:" + m);
					logger.trace("Annotation:" + Arrays.asList(m.getAnnotations()));
					if(m.isAnnotationPresent(Role.class)) {
						Role r = m.getAnnotation(Role.class);
						handlerRole.put(handlerName + "." + m.getName(), r.value().toString());
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.println(e);
			} catch (SecurityException e) {
				System.out.println(e);
			} catch (IllegalAccessException e) {
				System.out.println(e);
			} catch (NoSuchFieldException e) {
				System.out.println(e);
			}
		}
		
		logger.trace(handlerRole);

		AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
			new AbstractReflectiveHandlerMapping.AuthenticationHandler(){
			public boolean isAuthorized(XmlRpcRequest pRequest){
				String role = handlerRole.get(pRequest.getMethodName());
				logger.trace("isAuthorized");
				logger.trace(pRequest.getMethodName());
				logger.trace(pRequest.getConfig());
				logger.trace(role);

				XmlRpcHttpRequestConfig config =
					(XmlRpcHttpRequestConfig) pRequest.getConfig();
				logger.trace(config.getBasicUserName());
				logger.trace(config.getBasicPassword());
				
				return true;
			}
		};

		map.setAuthenticationHandler(handler);

		return map;
	}

	/* (non-Javadoc)
	 * @see org.apache.xmlrpc.webserver.XmlRpcServlet#newXmlRpcServer(javax.servlet.ServletConfig)
	 */
	@Override
	protected XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig)
	throws XmlRpcException {
		XmlRpcServletServer server = new XmlRpcServletServer() {
			@Override
			protected XmlRpcHttpRequestConfigImpl getConfig(
					HttpServletRequest pRequest) {

				XmlRpcHttpRequestConfigImpl requestconfig = super.getConfig(pRequest);
				if(getPropertiesManager().security) {
					String user = pRequest.getRemoteUser();
					requestconfig.setBasicUserName(user + "ototo");

					HostsList hl = getHostsList();
					ParamsBean p = new ParamsBean();
					p.readAuthorization(pRequest, hl);
					StringBuilder roleStr = new  StringBuilder();
					for(String role: p.getRoles()) {
						roleStr.append(role +",");
					}
					requestconfig.setBasicPassword(roleStr.toString());
				}
				return requestconfig;
			}

		};
		server.setErrorLogger(new XmlRpcErrorLogger() {
			@Override
			public void log(String pMessage, Throwable pThrowable) {
				logger.error(pMessage, pThrowable);
			}
			@Override
			public void log(String pMessage) {
				logger.error(pMessage);
			}
		});
		return server;
	}

	private HostsList getHostsList() {
		return Configuration.get().getHostsList();
	}

	private PropertiesManager getPropertiesManager() {
		return Configuration.get().getPropertiesManager();
	}

}
