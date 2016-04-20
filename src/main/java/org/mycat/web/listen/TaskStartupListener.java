package org.mycat.web.listen;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mycat.web.task.common.TaskManger;
import org.mycat.web.task.server.CheckMycatSuspend;
import org.mycat.web.task.server.CheckServerDown;
import org.mycat.web.task.server.SyncSysSql;
import org.mycat.web.task.server.SyncSysSqlhigh;
import org.mycat.web.task.server.SyncSysSqlslow;
import org.mycat.web.task.server.SyncSysSqlsum;
import org.mycat.web.task.server.SyncSysSqtable;
import org.mycat.web.task.server.SyncClearData;
import org.mycat.web.util.Constant;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.MailConfigUtils;
import org.mycat.web.util.MailUtil;
import org.mycat.web.util.DataSourceUtils.MycatPortType;
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


		MailConfigUtils.getInstance();
		List<Map<String, Object>> mycatList;
		try {
			mycatList = ZookeeperCuratorHandler.getInstance().getChildNodeData(Constant.MYCATS);
			if (mycatList!=null){
			  for(Map<String,Object> mycat : mycatList){
				String mycatName = (String)mycat.get("mycatName"); 
				try {
					DataSourceUtils.getInstance().register(mycatName, MycatPortType.MYCAT_MANGER); 
					LOGGER.info("数据源["+mycatName+"]");
				} catch (Exception e) { 
					LOGGER.error(e.toString());
				}
			  }
			  TaskManger.getInstance().addTask(new SyncSysSql(), 60 * 1000, "SyncSysSql");//1分钟
			  TaskManger.getInstance().addTask(new SyncSysSqlhigh(), 60 * 1000*2, "SyncSysSqlhigh");//2分钟
			  TaskManger.getInstance().addTask(new SyncSysSqlslow(), 60 * 1000*2, "SyncSysSqlslow");//2分钟
			  TaskManger.getInstance().addTask(new SyncSysSqtable(), 60 * 1000*3, "SyncSysSqtable");//3分钟
			  TaskManger.getInstance().addTask(new SyncSysSqlsum(), 60 * 1000*3, "SyncSysSqlsum");//3分钟
			  TaskManger.getInstance().addTask(new SyncClearData(),60 *1000*60*10, "SyncClearData");//10小时
//			  
			  TaskManger.getInstance().addTask(new CheckMycatSuspend(), 5*60 * 1000, "CheckMycatSuspend",20);//5分钟  20秒没响应报警（包括10秒宕机）
		
			  //TaskManger.getInstance().addTask(new CheckServerDown(), 60 * 1000*5, "CheckServerDown");//5分钟检查一次
			  
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//TaskManger.getInstance().addTask(new CheckMycatSuspend(), 60 * 1000, "CheckMycatSuspend",10);//1分钟  10秒没响应报警
			e1.printStackTrace();
		}
	}

}
