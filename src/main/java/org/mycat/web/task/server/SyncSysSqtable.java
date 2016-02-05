package org.mycat.web.task.server;

import java.util.Date; 
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hx.rainbow.common.core.SpringApplicationContext; 
import org.hx.rainbow.common.util.DateUtil;
import org.mycat.web.service.ShowService;
import org.mycat.web.task.common.ITask;
import org.mycat.web.util.DataSourceUtils;

/*
 * 异步持久化mycat中数据
 */

public class SyncSysSqtable implements ITask {
	
	private static final String NAMESPACE = "SYSSQLTABLE";

	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";
	
	@Override
	public void excute(String dbName, Date nowDate) {  
//		if (!DataSourceUtils.getInstance().isMycatManger(dbName)){
//			return ;
//		}
		ShowService showService = (ShowService)SpringApplicationContext.getBean("showService"); 
		List<Map<String,Object>> list = showService.getDao().query(dbName, SYSPARAM_NAMESPACE, "sqlsumtable");  
		Pattern pattern = Pattern.compile(",\\s+$");
		for(Map<String,Object> entry : list){
			for(String key : entry.keySet()){
				Object val = entry.get(key);
				Class<?> valClass = val.getClass();
				if(valClass == java.lang.String.class){
					String strVal = (String)val;
					Matcher matcher = pattern.matcher(strVal);
					if(matcher.find()){ 
						entry.put(key, matcher.replaceFirst(""));
					}
				}
			} 
			entry.put("LAST_TM", DateUtil.toDateTimeString(new Date((long) entry.get("LAST_TIME")))); 
			entry.put("PERCENT_R", entry.get("R%"));
			entry.remove("R%");
			entry.put("DB_NAME", DataSourceUtils.getInstance().getDbName(dbName));
			//Map<String,Object> entity = showService.getDao().get(NAMESPACE, "query",entry);
			//if(entity == null){ 
				showService.getDao().insert(NAMESPACE, "insert", entry); 
			//}else{
		//		showService.getDao().insert(NAMESPACE, "update", entry); 
		//	}
		} 
 
		list.clear(); 
	}

}
