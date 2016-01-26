package org.mycat.web.listen;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
import org.mycat.web.task.common.TaskManger;
import org.mycat.web.task.server.SyncSysSql;
import org.mycat.web.task.server.SyncSysSqlhigh;
import org.mycat.web.task.server.SyncSysSqlslow;
import org.mycat.web.task.server.SyncSysSqlsum;
import org.mycat.web.task.server.SyncSysSqtable;
import org.mycat.web.util.Constant;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.ZookeeperCuratorHandler;
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
		List<Map<String, Object>> mycatList;
		try {
			mycatList = ZookeeperCuratorHandler.getInstance().getChildNodeData(Constant.MYCATS);
			for(Map<String,Object> mycat : mycatList){
				String mycatName = (String)mycat.get("mycatName"); 
				try {
					DataSourceUtils.getInstance().register(mycatName); 
					LOGGER.info("数据源["+mycatName+"]");
				} catch (Exception e) { 
					LOGGER.error(e.toString());
				}
			}
			TaskManger.getInstance().addTask(new SyncSysSql(), 60 * 1000);//1分钟
			TaskManger.getInstance().addTask(new SyncSysSqlhigh(), 60 * 1000*2);//2分钟
			TaskManger.getInstance().addTask(new SyncSysSqlslow(), 60 * 1000*2);//2分钟
			TaskManger.getInstance().addTask(new SyncSysSqtable(), 60 * 1000*3);//3分钟
			TaskManger.getInstance().addTask(new SyncSysSqlsum(), 60 * 1000*3);//3分钟
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
