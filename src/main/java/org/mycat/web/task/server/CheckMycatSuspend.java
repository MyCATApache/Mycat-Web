package org.mycat.web.task.server;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowProperties;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.task.common.ITask;
import org.springframework.stereotype.Service;




/**
 * 检查Mycat是否假死
 * 
 * 方法： 通过执行select命令
 * 
 * 
 * 
 * @author whyuan
 *
 */
@Service
public class CheckMycatSuspend extends BaseService implements ITask {
	private String dbName;
	
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
		this.dbName = dbName;
		//TODO: 这里需要修改
		super.getDao().query(dbName, NAMESPACE, STATEMENT);
	}

	public String getDbName(){
		return this.dbName;
	}
	
}
