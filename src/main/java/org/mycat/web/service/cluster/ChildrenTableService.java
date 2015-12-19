package org.mycat.web.service.cluster;

import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.mycat.web.model.cluster.ChildrenTable;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

@Service("childrenTableService")
public class ChildrenTableService {
private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkId");
		String guid=(String)context.getAttr("guid");
		String parentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_SCHEMA,guid);
		int pageNo = context.getPage() == 0 ?1:context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(parentPath, ChildrenTable.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
}
