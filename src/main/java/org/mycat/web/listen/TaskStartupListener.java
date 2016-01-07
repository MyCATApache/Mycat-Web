package org.mycat.web.listen;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mycat.web.service.ZookeeperService;
import org.mycat.web.task.common.TaskManger;
import org.mycat.web.task.server.SyncSysparamProcessor;
import org.mycat.web.util.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册zookeeper中所有mycat数据源
 *
 */

public class TaskStartupListener implements ServletContextListener{ 
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskStartupListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {  
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) { 
		List<Map<String,Object>> mycatList = ZookeeperService.getInstance().getMycat("mycatName",null);
		for(Map<String,Object> mycat : mycatList){
			String mycatName = (String)mycat.get("mycatName"); 
			try {
				DataSourceUtils.getInstance().register(mycatName); 
				LOGGER.info("数据源["+mycatName+"]");
			} catch (Exception e) { 
				LOGGER.error(e.toString());
			}
		}
		TaskManger.getInstance().addTask(new SyncSysparamProcessor(), 60 * 1000);
	}

}
