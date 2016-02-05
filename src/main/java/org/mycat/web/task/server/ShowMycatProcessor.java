package org.mycat.web.task.server;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowProperties;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.task.common.ITask;
import org.springframework.stereotype.Service;

@Service
public class ShowMycatProcessor extends BaseService implements ITask {
	private static final String NAMESPACE = "MYCATSHOWPROCESSOR";
	
//	public ShowMycatProcessor(){
//		Long period = Long.parseLong((String)RainbowProperties.getProperties("show.period"));
//		if(period == null || period < 30000){
//			period = UPDATE_PERIOD;
//		}
//		TaskManger.getInstance().addTask(this, period);
//	}
	
	@Override
	public void excute(String dbName, Date nowDate) {
		List<Map<String, Object>> datas = super.getDao().query(dbName, NAMESPACE, STATEMENT);
		for(Map<String, Object> data : datas){
			data.put("createTime", nowDate);
			data.put("mycatName", dbName);
			super.getDao().insert(NAMESPACE, "insert", data);
		}
	}

	public static String ShowMycatSqlonlineServer(){
	return (String)RainbowProperties.getProperties("sqlonline.server");

	}
	
	public static String ShowMycatSqlonlineUser(){
		return (String)RainbowProperties.getProperties("sqlonline.user");

		}
	
	public static String ShowMycatSqlonlinePasswd(){
		return (String)RainbowProperties.getProperties("sqlonline.passwd");

		}
	
}
