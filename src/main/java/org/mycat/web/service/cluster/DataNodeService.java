package org.mycat.web.service.cluster;

import java.util.List;
import java.util.Map;

import org.apache.curator.utils.ZKPaths;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.JavaBeanUtil;
import org.mycat.web.model.cluster.DataNode;
import org.mycat.web.util.Constant;
import org.mycat.web.util.ZookeeperCuratorHandler;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service("dataNodeService")
public class DataNodeService {
	private ZookeeperCuratorHandler zkHander=  ZookeeperCuratorHandler.getInstance();
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryByPage(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkId");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_DATANODE);
		int pageNo = context.getPage()==0?1:context.getPage();
	    int pageSize = context.getLimit();
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, DataNode.class, pageNo, pageSize,null);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
			context.setTotal((int) data.get("total"));
		}
	    return context;
	}
	
	@SuppressWarnings("unchecked")
	public RainbowContext queryAll(RainbowContext context){
		String clusterPath=(String)context.getAttr("zkId");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_DATANODE);
		Map<String,Object> data = zkHander.getChildNodeData(datahostParentPath, DataNode.class);
		if(data != null && data.size() > 0){
			context.setRows((List<Map<String, Object>>) data.get("rows"));
		}
	    return context;
	}
	
	
	public RainbowContext insert (RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		String clusterPath = (String)context.getAttr("zkId");
		String name = (String) context.getAttr("name");
		String datahostParentPath = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath, Constant.CLUSTER_DATANODE);
		String datahostPath = ZKPaths.makePath(datahostParentPath , name);
		String mycatJson_new = JSON.toJSONString(params);
		DataNode dataNode = JSON.parseObject(mycatJson_new, DataNode.class);
		List<String> childrenPath =  zkHander.getChildNode(datahostParentPath);
		for (String cpath : childrenPath) {
			if(dataNode.getName().equals(cpath)){
				context.setSuccess(false);
				context.setMsg("名称已存在");
				return context;
			}
		}
		String data = JSON.toJSONString(dataNode);
		zkHander.createNode(datahostPath, data);
		return context;
	}
	
	public RainbowContext update(RainbowContext context) throws Exception{
		Map<String,Object> params = context.getAttr();
		if(params.containsKey("name")){
			String json_newStr = JSON.toJSONString(params);
			String clusterPath =  String.valueOf(params.get("zkId"));
			String guid =  String.valueOf(params.get("guid"));
			String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath,Constant.CLUSTER_DATANODE,guid);
			DataNode _new = JSON.parseObject(json_newStr,DataNode.class);
			String  json_oldStr = zkHander.getNodeData(path);
			DataNode _old = JSON.parseObject(json_oldStr, DataNode.class);
			JavaBeanUtil.copyProperties(_new, _old);
			String data = JSON.toJSONString(_old);
			zkHander.setNodeData(path, data);
		}else{
			context.setMsg("name不能为空");
			context.setSuccess(false);
		}
		return context;
	}

	public RainbowContext delete(RainbowContext context) throws Exception {
		Map<String, Object> params = context.getAttr();
		String clusterPath =  String.valueOf(params.get("zkId"));
		String guid =  String.valueOf(params.get("guid"));
		String path = ZKPaths.makePath(Constant.MYCAT_CLUSTER_KEY, clusterPath,Constant.CLUSTER_DATANODE,guid);
		
		if (zkHander.existsNode(path)) {
			zkHander.deleteNode(path);
		}else{
			context.setMsg("系统异常，请稍后再试");
			context.setSuccess(false);
		}
		return context;
	}
}
