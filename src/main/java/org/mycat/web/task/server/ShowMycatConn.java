package org.mycat.web.task.server;

import java.util.Date;

import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.task.common.ITask;
import org.springframework.stereotype.Service;

@Service
public class ShowMycatConn extends BaseService implements ITask {


	@Override
	public void excute(String dbName, Date nowDate) {
		System.out.println("====ShowMycatMemory");
	}

}
