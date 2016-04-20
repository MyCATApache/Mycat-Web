package org.mycat.web.task.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.hx.rainbow.common.core.SpringApplicationContext;
import org.mycat.web.task.common.ITask;
import org.mycat.web.util.DataSourceUtils.MycatPortType;
import org.mycat.web.util.MailUtil;

/*
 * 异步持久化mycat中数据
 */

public class CheckServerDown implements ITask {
	
	private static final int timeout = 10;  //10S
	
	@Override
	public void excute(String dbName, Date nowDate) {  
		DataSource dbSource = (DataSource)SpringApplicationContext.getBean(dbName + MycatPortType.MYCAT_MANGER + "dataSource");
		Connection conn = null;
		try {
				conn = dbSource.getConnection();
				if(!conn.isValid(timeout)){
					MailUtil.send("Mycat死机", dbName+"死机");
				}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		 
	}

}
