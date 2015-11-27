package org.mycat.web.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mycat.web.util.ZookeeperCuratorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishServiceStartupListener implements ServletContextListener {
    private final Logger log = LoggerFactory.getLogger(PublishServiceStartupListener.class);
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
       System.out.println("PublishServiceStartupListener.contextDestroyed()");
    }
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
    	
    	System.out
				.println("PublishServiceStartupListener.contextInitialized()");
    	
    	ZookeeperCuratorHandler.getInstance().connect("127.0.0.1", "org/mycat");
       
    }

}
