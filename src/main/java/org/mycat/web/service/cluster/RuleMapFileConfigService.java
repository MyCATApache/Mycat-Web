package org.mycat.web.service.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("ruleMapFileConfigService")
public class RuleMapFileConfigService {
	
    public String  MapFilePath = "config";
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	public RainbowContext query(RainbowContext context) throws Exception{
		String clusterPath = (String)context.getAttr("zkId");
		String guid = (String)context.getAttr("guid");
		String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath ,Constant.CLUSTER_RULE , guid ,MapFilePath);
		if(zkHander.existsNode(path)){
			String data = zkHander.getNodeData(path);
			if(data !=null ){
				Map<String,Object> attr = new HashMap<String, Object>();
				List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
				attr.put("configJson", data);
				rows.add(attr);
				context.setRows(rows);
			}
		}
	    return context;
	}

}
