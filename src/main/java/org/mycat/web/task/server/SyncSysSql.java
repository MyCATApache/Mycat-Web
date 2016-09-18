package org.mycat.web.task.server;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.core.SpringApplicationContext; 
import org.mycat.web.service.ShowService;
import org.mycat.web.task.common.ITask;
import org.mycat.web.task.common.SqliteStore;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.DataSourceUtils.MycatPortType;
import org.hx.rainbow.common.util.DateUtil;
/*
 * 异步持久化mycat中数据
 */

public class SyncSysSql implements ITask {
	
	private static final String NAMESPACE = "SYSSQL";

	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";
	
	@Override
	public void excute(String dbName, Date nowDate) {  
		//System.out.println("持久化mycat中数据:"+dbName);
//		if (!DataSourceUtils.getInstance().isMycatManger(dbName)){
//			return ;
//		}
		//System.out.println("持久化mycat中数据开始:"+dbName);
		ShowService showService = (ShowService)SpringApplicationContext.getBean("showService"); 
		List<Map<String,Object>> list = showService.getDao().query(dbName + MycatPortType.MYCAT_MANGER, SYSPARAM_NAMESPACE, "sql"); 
		for(Map<String,Object> entry : list){			
			entry.put("START_TM", DateUtil.toDateTimeString(new Date((long) entry.get("START_TIME"))));
			entry.put("DB_NAME", DataSourceUtils.getInstance().getDbName(dbName));
			//Map<String,Object> entity = showService.getDao().get(NAMESPACE, "query",entry);
			//if(entity == null){ 
			SqliteStore.getInstance().insert(showService.getDao(), NAMESPACE, "insert", entry);
			//}
		} 
	}

}
