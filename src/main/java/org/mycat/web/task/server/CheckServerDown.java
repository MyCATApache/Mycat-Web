package org.mycat.web.task.server;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.hx.rainbow.common.core.SpringApplicationContext; 
import org.hx.rainbow.common.util.DateUtil;
import org.mycat.web.service.ShowService;
import org.mycat.web.task.common.ITask;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.MailUtil;
import org.mycat.web.util.DataSourceUtils.MycatPortType;

/*
 * 异步持久化mycat中数据
 */

public class CheckServerDown implements ITask {
	
	private static final int timeout = 10;  //10S
	
	@Override
	public void excute(String dbName, Date nowDate) {  
		try {
			if (!DataSourceUtils.getInstance().register(dbName, MycatPortType.MYCAT_SERVER)){
				MailUtil.send("Mycat死机", dbName+"死机");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
