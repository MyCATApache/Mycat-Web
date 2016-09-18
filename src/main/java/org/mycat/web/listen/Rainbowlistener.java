package org.mycat.web.listen;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Rainbowlistener implements ServletContextListener {
	private final String WEB_APP_ROOT_DEFAULT = "webapp.root";

	public void contextDestroyed(ServletContextEvent sce) {

	}

	public void contextInitialized(ServletContextEvent sce) {
		String prefix = sce.getServletContext().getRealPath("/");
		if (prefix.endsWith(File.separator)) {
			prefix = prefix.substring(0, prefix.length() - 1);
			prefix = prefix.replaceAll("\\\\", "/");
		}
		System.setProperty(WEB_APP_ROOT_DEFAULT, prefix);
	}
}