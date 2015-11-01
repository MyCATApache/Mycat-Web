package org.mycat.web.task.server;

import java.util.Date;

import org.mycat.web.task.common.ITask;

public class SyncMasterslave implements ITask {

	@Override
	public void excute(String dbName, Date nowDate) {
		System.out.println("====SyncMasterslave");
	}

}
