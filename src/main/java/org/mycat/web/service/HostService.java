package org.mycat.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.mycat.web.model.Hosts;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

@Service("hostService") 
public class HostService {
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	public RainbowContext queryAll(RainbowContext context) throws Exception {
		String parentNodePath = Constant.MYCAT_HOST_KEY;
		Map<String, Object> data = zkHander.getChildNodeData(parentNodePath,Hosts.class);
		if (data != null && data.size() > 0) {
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) throws Exception {
		String parentNodePath = Constant.MYCAT_HOST_KEY;
		List<String> childrenName = zkHander.getChildrenName(parentNodePath);
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("Path", parentNodePath);
		attr.put("Data", childrenName);
		context.addRow(attr);
		return context;
	}

}
