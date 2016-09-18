package org.mycat.web.task.common;

import java.util.Date;

public interface ITask{
	public static final String STATEMENT = "queryMycat";
	public static final long UPDATE_PERIOD = 5000*60;
	
	public void excute(String dbName, Date nowDate);
}
