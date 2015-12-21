package org.mycat.web.service.cluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.cluster.Rule;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service("ruleService")
public class RuleService {
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkId");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_RULE);
		int pageNo = context.getPage()==0?1: context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, Rule.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryAll(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkId");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_RULE);
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, Rule.class);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	public RainbowContext insert (RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		String clusterPath = (String)context.getAttr("zkId");
		String name = (String) context.getAttr("name");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_RULE);
		String datahostPath = ZKPaths.makePath(datahostParentPath , name);
		String mycatJson_new = JSON.toJSONString(params);
		Rule rule = JSON.parseObject(mycatJson_new, Rule.class);
		
		List<String> childrenPath =  zkHander.getChildNode(datahostParentPath);
		for (String cpath : childrenPath) {
			if(rule.getName().equals(cpath)){
				context.setSuccess(false);
				context.setMsg("名称已存在");
				return context;
			}
		}
		String data = JSON.toJSONString(rule);
		zkHander.createNode(datahostPath, data);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		String clusterPath = (String)context.getAttr("zkId");
		String guid = (String) context.getAttr("guid");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_RULE);
		String path = ZKPaths.makePath(datahostParentPath, guid);
		if(params.containsKey("name")){
			String json_newStr =  JSON.toJSONString(params);
			Rule _new = JSON.parseObject(json_newStr, Rule.class);
			String  json_oldStr = zkHander.getNodeData(path);
			Rule _old = JSON.parseObject(json_oldStr, Rule.class);
			JavaBeanUtil.copyProperties(_new, _old);
			String data = JSON.toJSONString(_old);
			zkHander.setNodeData(path, data);
		}
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		String clusterPath = (String)context.getAttr("zkId");
		String guid = (String) context.getAttr("guid");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_RULE);
		String path = ZKPaths.makePath(datahostParentPath, guid);
		if (zkHander.existsNode(path)) {
			zkHander.deleteNode(path);
		}else{
			context.setSuccess(false);
			context.setMsg("删除失败！");
		}
		return context;
	}
}
