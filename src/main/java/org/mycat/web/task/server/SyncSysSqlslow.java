package org.mycat.web.task.server;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.core.SpringApplicationContext; 
import org.hx.rainbow.common.util.DateUtil;
import org.mycat.web.service.ShowService;
import org.mycat.web.task.common.ITask;
import org.mycat.web.util.DataSourceUtils;

/*
 * 异步持久化mycat中数据
 */

public class SyncSysSqlslow implements ITask {
	
	private static final String NAMESPACE = "SYSSQLSLOW";

	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";
	
	@Override
	public void excute(String dbName, Date nowDate) {  
//		if (!DataSourceUtils.getInstance().isMycatManger(dbName)){
//			return ;
//		}
		ShowService showService = (ShowService)SpringApplicationContext.getBean("showService"); 
		List<Map<String,Object>> list = showService.getDao().query(dbName, SYSPARAM_NAMESPACE, "sqlslow"); 
		for(Map<String,Object> entry : list){
			//entry.put("START_TM", new Date((long) entry.get("START_TIME")));
			entry.put("START_TM", DateUtil.toDateTimeString(new Date((long) entry.get("START_TIME"))));
			entry.put("DB_NAME", DataSourceUtils.getInstance().getDbName(dbName));
			//Map<String,Object> entity = showService.getDao().get(NAMESPACE, "query",entry);
			//if(entity == null){ 
				showService.getDao().insert(NAMESPACE, "insert", entry);
			//}
		} 
		
		 
	}

}
