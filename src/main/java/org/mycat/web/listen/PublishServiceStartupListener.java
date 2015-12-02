package org.mycat.web.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hx.rainbow.common.context.RainbowProperties;
import org.hx.rainbow.common.exception.SysException;
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
    	
    	String connStr = (String)RainbowProperties.getProperties("zookeeper");
    	if(connStr == null || connStr.isEmpty()){
    		throw new SysException("zookeeper is null, please check mycat.properties!");
    	}
    	System.out.println(connStr);
    	ZookeeperCuratorHandler.getInstance().connect(connStr, "mycat-zone/mysql-group1");
       
    }

}
