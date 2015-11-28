package org.mycat.web.service;

import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.MycatCluster;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service("mycatClusterService")
public class MycatClusterService  {
	
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	public RainbowContext queryByPage(RainbowContext context){
		String path = Constant.MYCAT_NODES;
		int pageNo = context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(path, MycatCluster.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	public RainbowContext queryAll(RainbowContext context){
		String path = Constant.MYCAT_NODES;
		Map<String,Object> data = zkHander.getChildNodeData(path, MycatCluster.class);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	public RainbowContext insert (RainbowContext context) throws Exception{
		Map<String, Object> params = context.getAttr();
		String  parentPath = Constant.MYCAT_NODE;
		String path = zkHander.createSeqNode(parentPath, "");
		String mycatJson_new = (String)params.get("value");
		MycatCluster mycatCluster = JSON.parseObject(mycatJson_new, MycatCluster.class);
		mycatCluster.setGuid(path);
		String data = JSON.toJSONString(mycatCluster);
		zkHander.setNodeData(path, data);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		if(params.containsKey("guid")){
			String json_newStr = (String)params.get("value");
			String path =  String.valueOf(params.get("guid"));
			MycatCluster cluster_new = JSON.parseObject(json_newStr, MycatCluster.class);
			String  json_oldStr = zkHander.getNodeData(path);
			MycatCluster cluster_old = JSON.parseObject(json_oldStr, MycatCluster.class);
			JavaBeanUtil.copyProperties(cluster_new, cluster_old);
			String data = JSON.toJSONString(cluster_old);
			zkHander.setNodeData(path, data);
		}
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		Map<String, Object> params = context.getAttr();
		if (params.containsKey("guid")) {
			String path = (String) params.get("guid");
			zkHander.deleteNode(path);
		}
		return context;
	}
}
