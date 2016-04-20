package org.mycat.web.task.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.service.ShowService;
import org.mycat.web.task.common.ITask;
import org.mycat.web.util.DataSourceUtils.MycatPortType;
import org.mycat.web.util.MailUtil;
import org.springframework.stereotype.Service;




/**
 * 检查Mycat是否宕机，已宕机，直接报警返回；没有宕机检查是否假死，如假死报警结束
 * 
 * 检查宕机方法： 判断Connection是否valid（设置timeout）同步
 * 
 * 检查假死方法：  
 * 通过执行select user()命令
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
	
	private static final int timeout = 10;  //宕机timeout：10S
	
	@Override
	public void excute(String dbName, Date nowDate) {
		
		DataSource dbSource = (DataSource)SpringApplicationContext.getBean(dbName + MycatPortType.MYCAT_MANGER + "dataSource");
		Connection conn = null;
		try {
			conn = dbSource.getConnection();
			if(!conn.isValid(timeout)){
				MailUtil.send("Mycat死机", dbName+"死机");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				MailUtil.send("Mycat死机", dbName+"死机");
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			return;			
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		 
		this.dbName = dbName;

		ShowService showService = (ShowService)SpringApplicationContext.getBean("showService"); 
		showService.getDao().query(dbName + MycatPortType.MYCAT_MANGER, NAMESPACE, "testMycat");
	}

	public String getDbName(){
		return this.dbName;
	}
	
}
